package com.newland.sdk.me.module.emv;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.newland.ISettingsManager;
import android.os.Handler;

import com.newland.emv.jni.type.candidate;
import com.newland.emv.jni.type.emv_oper;
import com.newland.emv.jni.type.ep_opt;
import com.newland.emv.jni.type.publickey;
import com.newland.emv.jni.type.ui_request_data;
import com.newland.ndk.NdkApiManager;
import com.newland.sdk.me.module.emv.AbstractEMVTransController.EMVState;
import com.newland.sdk.module.emv.EMVModule;
import com.newland.sdk.module.emv.EMVUtils;
import com.newland.sdk.module.externaliccard.ExtICCardModule;
import com.newland.sdk.mtype.Device;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.module.emv.AIDEntity;
import com.newland.sdk.module.emv.AccountType;
import com.newland.sdk.module.emv.EMVControllerListener;
import com.newland.sdk.module.emv.EMVInterceptListener;
import com.newland.sdk.module.emv.EMVTransInfo;
import com.newland.sdk.module.emv.IDCardType;
import com.newland.sdk.module.emv.PINEntity;
import com.newland.sdk.module.emv.PinRequiredType;
import com.newland.sdk.module.iccard.ICCardModule;
import com.newland.sdk.module.rfcard.RFCardModule;
import com.newland.sdk.module.rfcard.RFCardType;
import com.newland.sdk.module.rfcard.RFResult;
import com.newland.sdk.mtype.util.Dump;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.utils.TLVPackage;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * emv core operator
 */
public class EMVCoreOperator implements emv_oper {

    /**
     * 账户类型
     * <p>
     * 参考说明：{@link #acctype_sel()}
     *
     * @see #acctype_sel()
     */
    private final String[] ACCT_TYPES = {"Default", "Savings", "Cheque/debit", "Credit"};

    private String[] CERTIFICATE_TYPES = {"身份证", "军官证", "护照", "入境证", "临时身份证", "其他"};

    private  int USER_OPERATOR_WAITING_MILLS = 30000;
    private  int USER_OPERATOR_WAITING_MILLS_PININPUT = 60000;

    private final int ICCARD_COMMUNICATE_TIMEOUT_MILLS = 3000;

    private DeviceLogger deviceLogger = DeviceLoggerFactory.getLogger("EMVCoreOperator");

    private MemoryBrancher brancher = MemoryBrancher.createInstance();

    private FileHandler handler = null;

    protected ICCardModule iccardModule;

    protected RFCardModule rfcardModule;

    private EMVLevel2TransferController emvL2controller;

    private Device device;

    private byte[] languageID;//z h

    public EMVLevel2ContextHelper getContextHelper() {
        if (emvL2controller == null) {
            deviceLogger.debug("[getContextHelper]controller is null!");
            return null;
        }
        return emvL2controller.contextHelper;
    }

    private void runOnUIThread(Runnable run) {
        Handler mainHandler = getContextHelper().getMainHandler();
        if (mainHandler == null)
            deviceLogger.debug("[runOnUIThread]contextHelper should be init first!");

        mainHandler.post(run);
    }

    public EMVCoreOperator(Device device) {
        this.device = device;
        this.iccardModule = (ICCardModule) device.getStandardModule(ModuleType.ICCARDREADER);
        this.rfcardModule = (RFCardModule) device.getStandardModule(ModuleType.RFCARDREADER);
    }

    class MessageConfirmRunnable implements Runnable {
        private Object sync = new Object();

        volatile boolean choice = false;

        private String title;
        private String content;
        private String[] buttonsDescription;
        private boolean needYesNo = true;
        private int waittime;

        public MessageConfirmRunnable(String title, String[] buttonsDescription, String content, boolean needYesNo, int waittime) {
            this.title = title;
            this.content = content;
            this.buttonsDescription = buttonsDescription;
            this.needYesNo = needYesNo;
            this.waittime = waittime;
        }

        @Override
        public void run() {
            boolean isLevel2Listener = emvL2controller.getListener() instanceof EMVInterceptListener;
            if (isLevel2Listener && !(((EMVInterceptListener) emvL2controller.getListener()).activateTransactionMessageInterceptor())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContextHelper().getContext());
                builder.setTitle(title).setMessage(content).setPositiveButton(buttonsDescription[0], new OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        try {
                            MessageConfirmRunnable.this.choice = true;

                        } finally {
                            synchronized (sync) {
                                sync.notify();
                            }
                            dialog.dismiss();
                        }
                    }
                });
                if (needYesNo) {
                    builder.setNegativeButton(buttonsDescription[1], new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int arg1) {
                            try {
                                MessageConfirmRunnable.this.choice = false;

                            } finally {
                                synchronized (sync) {
                                    sync.notify();
                                }
                                dialog.dismiss();
                            }
                        }
                    });
                }
                builder.show();
            } else {
                EMVControllerListener listener = emvL2controller.getListener();
                String titleStr = new String(title);
                emvL2controller.setCurrentEmvState(EMVState.MESSAGE_CONFIRM);
                deviceLogger.debug(">>>[onRequestShowMessage]");
                listener.onRequestShowMessage(emvL2controller, titleStr, content, needYesNo, waittime);
            }

        }

        public boolean startwaiting(int waitting) {
            synchronized (sync) {
                try {
                    sync.wait(waitting); // 等待30秒
                } catch (InterruptedException e) {
                }
            }
            return choice;
        }

        /**
         * 输入confirm result
         */
        void confirmMessage(boolean isConfirm) {
            try {
                this.choice = isConfirm;
            } finally {
                notifyWaiting();
            }
        }

        private void notifyWaiting() {
            synchronized (sync) {
                sync.notify();
            }
        }
    }


    class CertIDConfirmRunnable implements Runnable {
        private Object sync = new Object();

        volatile boolean choice = false;

        private String title;
        private String content;
        private String[] buttonsDescription;
        private IDCardType type;
        private String certNo;

        public CertIDConfirmRunnable(String title, String[] buttonsDescription, String content, IDCardType type, String certNo) {
            this.title = title;
            this.content = content;
            this.buttonsDescription = buttonsDescription;
            this.type = type;
            this.certNo = certNo;
        }

        @Override
        public void run() {
            boolean isLevel2Listener = emvL2controller.getListener() instanceof EMVInterceptListener;
            if (isLevel2Listener && !((EMVInterceptListener) emvL2controller.getListener()).activateCertConfirmInterceptor()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContextHelper().getContext());
                builder.setTitle(title).setMessage(content).setPositiveButton(buttonsDescription[0], new OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        try {
                            CertIDConfirmRunnable.this.choice = true;

                        } finally {
                            synchronized (sync) {
                                sync.notify();
                            }
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            } else {
                EMVControllerListener listener = emvL2controller.getListener();
                emvL2controller.setCurrentEmvState(EMVState.CERT_CONFIRM);
                deviceLogger.debug(">>>[onRequestConfirmID]");
                listener.onRequestConfirmID(emvL2controller, type, certNo);
            }

        }

        public boolean startwaiting(int waitting) {
            synchronized (sync) {
                try {
                    sync.wait(waitting); // 等待30秒
                } catch (InterruptedException e) {
                }
            }
            return choice;
        }

        /**
         * 输入confirm result
         */
        void confirmCertID(boolean isConfirm) {
            try {
                this.choice = isConfirm;
            } finally {
                notifyWaiting();
            }
        }

        private void notifyWaiting() {
            synchronized (sync) {
                sync.notify();
            }
        }
    }

    class AccountTypeSelectRunnable implements Runnable {

        private Object sync = new Object();

        volatile int whichButton = -1;

        private String title;
        private String[] items;
        EMVDialogTips emvDialogTips = new EMVDialogTips();

        public AccountTypeSelectRunnable(String title, String[] items) {
            this.title = title;
            this.items = items;
        }

        @Override
        public void run() {
            boolean isLevel2Listener = emvL2controller.getListener() instanceof EMVInterceptListener;
            if (isLevel2Listener && !((EMVInterceptListener) emvL2controller.getListener()).activateAccountTypeSelectInterceptor()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContextHelper().getContext());
                builder.setTitle(title).setSingleChoiceItems(items, 0, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int selected) {
                        AccountTypeSelectRunnable.this.whichButton = whichButton;
                    }
                }).setPositiveButton(emvDialogTips.getDialogMesdkConfirm(), new OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        synchronized (sync) {
                            sync.notify();
                        }
                        dialog.dismiss();
                    }
                }).show();
            } else {
                EMVControllerListener listener = emvL2controller.getListener();
                emvL2controller.setCurrentEmvState(EMVState.ACCOUNT_SELECT);
                deviceLogger.debug(">>>[onRequestSelectAccountType]");
                listener.onRequestSelectAccountType(emvL2controller, new AccountType[]{AccountType.DEFAULT, AccountType.SAVINGS, AccountType.CHEQUE_DEBIT, AccountType.CREDIT});
            }

        }

        public int startwaiting(int waitting) {
            synchronized (sync) {
                try {
                    sync.wait(waitting); // 等待30秒
                } catch (InterruptedException e) {
                }
            }
            return whichButton;
        }

        /**
         * 输入account type
         */
        void selectAccount(int whichButton) {
            try {
                this.whichButton = whichButton;
            } finally {
                notifyWaiting();
            }
        }

        private void notifyWaiting() {
            synchronized (sync) {
                sync.notify();
            }
        }
    }

    /**
     * 账号类型选择
     * <p>
     * 返回int范围
     * <p>
     * <ol>
     * <li>default: 1</li>
     * <li>savings: 2</li>
     * <li>Cheque/debit: 3</li>
     * <li>Credit: 4</li>
     * </ol>
     */
    @Override
    public int acctype_sel() {
        deviceLogger.debug("[acctype_sel]");
        if (!isControllerAlive())
            return -1;
        try {
            if (null == emvL2controller.getListener()) {
                deviceLogger.error("[acctype_sel]:emvL2controller.getListener() return null!");
                return -1;
            }
            if(emvL2controller!=null && emvL2controller.getEMVTimeOut()>0){
                USER_OPERATOR_WAITING_MILLS = emvL2controller.getEMVTimeOut() * 1000;
                USER_OPERATOR_WAITING_MILLS_PININPUT = emvL2controller.getEMVTimeOut() * 1000;
            }
            if (emvL2controller.getListener() instanceof EMVInterceptListener) {
                deviceLogger.debug("[acctype_sel] EMVInterceptListener");
                EMVInterceptListener listener = (EMVInterceptListener) emvL2controller.getListener();
                if (!listener.activateAccountTypeSelectInterceptor()) {
                    deviceLogger.debug("[acctype_sel] activateAccountType");
                    AccountTypeSelectRunnable dialogger = new AccountTypeSelectRunnable("Account Type Selection", ACCT_TYPES);
                    runOnUIThread(dialogger);
                    int selected = dialogger.startwaiting(USER_OPERATOR_WAITING_MILLS);
                    deviceLogger.debug("[acctype_sel] acct type selected:" + selected);
                    if (selected >= 0 && selected < 4) {
                        deviceLogger.debug("[acctype_sel] return "+(selected + 1));
                        return selected + 1;
                    } else {
                        deviceLogger.debug("[acctype_sel] return -1;");
                        return -1;
                    }
                }
            }

            AccountTypeSelectRunnable accountTypeSelectRunnable = new AccountTypeSelectRunnable("Account Type Selection", ACCT_TYPES);
            getContextHelper().accountTypeSelectedRunnable = accountTypeSelectRunnable;
            new Thread(accountTypeSelectRunnable).start();
            int ret = accountTypeSelectRunnable.startwaiting(USER_OPERATOR_WAITING_MILLS);
            deviceLogger.debug("[acctype_sel] return ret="+ret);
            return ret;

        } catch (Exception e) {
            e.printStackTrace();
            deviceLogger.error("[acctype_sel]accttype select failed!", e);
            return -1;
        }
    }

    class AIDSelectRunnable implements Runnable {

        private Object sync = new Object();

        volatile int selectedAid = -1;

        private List<AIDEntity> aidEntities;
        private int times;

        public AIDSelectRunnable(List<AIDEntity> aidEntities, int times) {
            this.aidEntities = aidEntities;
            this.times = times;
        }

        @Override
        public void run() {
            try {
                if (!isControllerAlive())
                    return;
                EMVControllerListener listener = emvL2controller.getListener();
                emvL2controller.setCurrentEmvState(EMVState.APPLICATION_SELECT);
                deviceLogger.debug("[EMVCoreOperator] onRequestSelectApplication emvL2controller.currentStep="+emvL2controller.currentStep+"->"+EMVTransStep.APPLICATION_SELECT);
                emvL2controller.currentStep = EMVTransStep.APPLICATION_SELECT;
                deviceLogger.debug(">>>[onRequestSelectApplication]");
                listener.onRequestSelectApplication(emvL2controller, aidEntities, times);
            } catch (Exception e) {
                onError(e); // 仅通知pin输入过程异常，由上层通知交易处理
            }
        }

        void startWaiting() {
            synchronized (sync) {
                try {
                    if(emvL2controller!=null && emvL2controller.getEMVTimeOut()>0){
                        USER_OPERATOR_WAITING_MILLS = emvL2controller.getEMVTimeOut() * 1000;
                        USER_OPERATOR_WAITING_MILLS_PININPUT = emvL2controller.getEMVTimeOut() * 1000;
                    }
                    sync.wait(USER_OPERATOR_WAITING_MILLS);
                } catch (InterruptedException e) {
                }
            }
        }

        /**
         * 输入pin，若为空或者为
         */
        void selectAid(int selectedAid) {
            try {
                this.selectedAid = selectedAid;
            } finally {
                notifyWaiting();
            }
        }

        void onError(Throwable e) {
            try {
                deviceLogger.error("[AIDSelectRunnable]failed to do aid selected!", e);
            } finally {
                notifyWaiting();
            }
        }

        private void notifyWaiting() {
            synchronized (sync) {
                sync.notify();
            }
        }

    }

    /**
     * 多应用选择
     */
    @Override
    public int candidate_sel(candidate[] aids, int totalCount, int times) {
        deviceLogger.debug("[candidate_sel] totalCount:"+totalCount+"; times:"+times+" aids="+aids);
        if(aids == null){
            return -1;
        }
        int length = (aids.length > totalCount ? totalCount : aids.length);
        for (int i = 0; i < length; i++) {
            deviceLogger.debug("[candidate_sel] _aid="+InnerUtils.hexString(aids[i]._aid)+" _priority="+aids[i]._priority+" _enable="+aids[i]._enable+" aids.length="+aids.length);
        }
        if (!isControllerAlive())
            return -1;

        try {
            List<AIDEntity> aidEntities = getAidItems(aids, totalCount);
            if(hasVCDA(aidEntities)){
                deviceLogger.debug("[candidate_sel]hasVCDA:true");
                for(Iterator<AIDEntity> it = aidEntities.iterator(); it.hasNext();){
                    AIDEntity aidEntity = it.next();
                    byte[] aid = aidEntity.getAid();
                    if(aid!=null && aid.length>=5 && Arrays.equals(new byte[]{aid[0],aid[1],aid[2],aid[3],aid[4]},new byte[]{(byte)0xA0, 0x00, 0x00, 0x00, 0x03})){
                        it.remove();
                    }
                }
            }
            if(hasMaster(aidEntities)){
                deviceLogger.debug("[candidate_sel]hasMaster:true");
                for(Iterator<AIDEntity> it = aidEntities.iterator(); it.hasNext();){
                    AIDEntity aidEntity = it.next();
                    byte[] aid = aidEntity.getAid();
                    if(aid!=null && aid.length>=5 && Arrays.equals(new byte[]{aid[0],aid[1],aid[2],aid[3],aid[4]},new byte[]{(byte)0xA0, 0x00, 0x00, 0x00, 0x04})){
                        it.remove();
                    }
                }
            }
            if(aidEntities!=null && aidEntities.size()==1){
                AIDEntity aidEntity =  aidEntities.get(0);
                int index = aidEntity.getIndex();
                deviceLogger.debug("[candidate_sel] final index="+index);

                return index;

            }
            AIDSelectRunnable aidselectRunnable = new AIDSelectRunnable(aidEntities, times);
            getContextHelper().aidselectRunnable = aidselectRunnable;
            new Thread(aidselectRunnable).start();
            aidselectRunnable.startWaiting();

            //由于在onRequestSelectApplication回调中返回的索引是EMV内部指示的索引,所以直接返回即可;
            int index = aidselectRunnable.selectedAid;
            deviceLogger.debug("[candidate_sel] index="+index);
            if(index < 0){
                return -1;
            }
            return index;
            /*
            deviceLogger.debug("[candidate_sel] selectedAid:"+aidselectRunnable.selectedAid+" aidEntities.size()="+aidEntities.size());
            if (aidselectRunnable.selectedAid < 0 || aidselectRunnable.selectedAid >= aidEntities.size()) {
                deviceLogger.debug("[candidate_sel] return -1");
                return -1;
            }
            AIDEntity aidEntity = aidEntities.get(aidselectRunnable.selectedAid);
            if (null != aidEntity && aidEntity.getIndex() == aidselectRunnable.selectedAid) {
                deviceLogger.debug("[candidate_sel] return getIndex="+aidEntity.getIndex()+" getAid="+InnerUtils.hexString(aidEntity.getAid()));
                return aidEntity.getIndex();
            }
            deviceLogger.debug("[candidate_sel] return -1.");
            return -1;
            */
        } catch (Exception e) {
            e.printStackTrace();
            deviceLogger.error("[candidate_sel]candidate select failed!", e);
            return -1;
        }
    }

    private List<AIDEntity> getAidItems(candidate[] aids, int count) throws Exception {
        List<AIDEntity> container = new LinkedList<AIDEntity>();
        try {
            int length = (aids.length > count ? count : aids.length);
            for (int i = 0; i < length; i++) {
                candidate c = aids[i];
                if (c == null || c._enable == 0){
                    deviceLogger.debug("[getAidItems] i="+i+" c="+(c==null?"null":c._enable));
                    continue;
                }
                if (c._aid_len > 0) {
                    byte[] aid = new byte[c._aid_len];
                    System.arraycopy(c._aid, 0, aid, 0, c._aid_len);
                    String name = null;
                    if (c._preferred_name_len > 0) {// 使用名称描述
                        try {
                            name = new String(c._preferred_name, 0, c._preferred_name_len);
                        } catch (Exception e) {
                            deviceLogger.debug("[getAidItems]get aid name failed:" + Dump.getHexDump(aid), e);
                        }
                    }
                    if (name == null) {
                        name = Dump.getHexDump(aid);
                    }
                    byte terminalPriority = c._terminal_priority;
                    byte[] appLabel = null;
                    if (c._lable_len > 0) {
                        appLabel = new byte[c._lable_len];
                        System.arraycopy(c._lable, 0, appLabel, 0, c._lable_len);
                    }
                    byte enable = c._enable;
                    byte flag = c._limit_flag;
                    byte[] kernelId = c._kernel_id;
                    byte apid = c._priority;
                    deviceLogger.debug("getAidItems i="+i+" aid="+InnerUtils.hexString(aid)+" name="+name+" appLabel="+InnerUtils.hexString(appLabel)+
                            " terminalPriority="+terminalPriority+" enable="+enable+" flag="+flag+" kernelId="+InnerUtils.hexString(kernelId)+" apid="+apid);
                    AIDEntity select = new AIDEntity(i, aid, name, appLabel, terminalPriority, enable, flag, kernelId, apid);
                    container.add(select);
                } else {
                    deviceLogger.debug("[getAidItems]_aid_length <=0 ???");
                }
            }
            return container;
        } catch (Exception e) {
            deviceLogger.debug("[getAidItems]failed to parser aid!", e);
            throw e;
        }
    }

    /**
     * 证书类型选择 Func: 执卡人身份认证。 Para: type 证件类型(身份证,军官证,护照,入境证,临时身份证,其他) Return: 1
     * 执卡人身份确认 0 身份确认失败
     */
    @Override
    public int cert_confirm(byte type, byte[] certNo, int len) {
        deviceLogger.debug("[cert_confirm] type="+type);
        if (!isControllerAlive())
            return -1;
        try {
            if (null == emvL2controller.getListener()) {
                deviceLogger.error("[cert_confirm]:emvL2controller.getListener() return null!");
                return -1;
            }
            if(emvL2controller!=null && emvL2controller.getEMVTimeOut()>0){
                USER_OPERATOR_WAITING_MILLS = emvL2controller.getEMVTimeOut() * 1000;
                USER_OPERATOR_WAITING_MILLS_PININPUT = emvL2controller.getEMVTimeOut() * 1000;
            }
            if (emvL2controller.getListener() instanceof EMVInterceptListener) {
                EMVInterceptListener listener = (EMVInterceptListener) emvL2controller.getListener();
                if (!listener.activateCertConfirmInterceptor()) {
                    EMVDialogTips emvDialogTips = new EMVDialogTips();
                    String title = emvDialogTips.getDialogMesdkCertConfirmTitle();//"请出示证件";
                    CERTIFICATE_TYPES = emvDialogTips.getCertType();
                    String[] buttonsDescription = new String[]{emvDialogTips.getDialogMesdkCertConfirm(), emvDialogTips.getDialogMesdkCertCancel()};
                    String context = emvDialogTips.getDialogMesdkCertType() + CERTIFICATE_TYPES[(type & 0xff)] + "\n " + emvDialogTips.getDialogMesdkCertNumber() + new String(certNo);
                    CertIDConfirmRunnable certIDConfirmRunnable = new CertIDConfirmRunnable(title, buttonsDescription, context, null, null);
                    runOnUIThread(certIDConfirmRunnable);
                    if (certIDConfirmRunnable.startwaiting(USER_OPERATOR_WAITING_MILLS)) {
                        deviceLogger.debug("[cert_confirm] return 1");
                        return 1;
                    }else {
                        deviceLogger.debug("[cert_confirm] return 0");
                        return 0;
                    }
                }
            }
            String certno = new String(certNo, 0, len);
            IDCardType certType = null;
            switch (type & 0xff) {
                /**
                 * private static final String[] CERTIFICATE_TYPES =
                 * {"身份证","军官证","护照","入境证","临时身份证","其他"};
                 */
                case 0:
                    certType = IDCardType.CITIZEN_IDCARD;
                    break;
                case 1:
                    certType = IDCardType.MILITARY_IDCARD;
                    break;
                case 2:
                    certType = IDCardType.PASSPORT;
                    break;
                case 3:
                    certType = IDCardType.ENTRY_PERMIT;
                    break;
                case 4:
                    certType = IDCardType.TEMPORARY_CITIZEN_IDCARD;
                    break;
                default:
                    certType = IDCardType.OTHERS;
                    break;
            }

            CertIDConfirmRunnable certIDConfirmRunnable = new CertIDConfirmRunnable("", null, null, certType, certno);
            getContextHelper().certIDConfirmRunnable = certIDConfirmRunnable;
            new Thread(certIDConfirmRunnable).start();
            boolean confirm = certIDConfirmRunnable.startwaiting(USER_OPERATOR_WAITING_MILLS);
            deviceLogger.debug("[cert_confirm] confirm="+confirm);
            if (confirm) {
                return 1;
            } else
                return 0;

        } catch (Exception e) {
            e.printStackTrace();
            deviceLogger.error("[cert_confirm] failed!", e);
            return -1;
        }
    }

    class ECChoiceRunnable implements Runnable {
        private static final int CHOOSE_EC = 1;
        private static final int NOT_CHOOSE_EC = 0;
        private static final int USER_CANCELED = -1;
        private static final int TIMEOUT = -3;

        private Object sync = new Object();
        volatile int choice = TIMEOUT;
        EMVDialogTips emvdialogTips = new EMVDialogTips();

        @Override
        public void run() {
            boolean isLevel2Listener = emvL2controller.getListener() instanceof EMVInterceptListener;
            if (isLevel2Listener && !((EMVInterceptListener) emvL2controller.getListener()).activateECSwitchInterceptor()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContextHelper().getContext());
                builder.setTitle(emvdialogTips.getDialogMesdkEcChoiceTitle()).setMessage(emvdialogTips.getDialogMesdkEcChoiceContext()).setNegativeButton(emvdialogTips.getDialogMesdkEcChoiceOnline(), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        try {
                            ECChoiceRunnable.this.choice = NOT_CHOOSE_EC;
                        } finally {
                            synchronized (sync) {
                                sync.notify();
                            }
                            dialog.dismiss();
                        }
                    }
                }).setPositiveButton(emvdialogTips.getDialogMesdkEcChiceEc(), new OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        try {
                            ECChoiceRunnable.this.choice = CHOOSE_EC;
                        } finally {
                            synchronized (sync) {
                                sync.notify();
                            }
                            dialog.dismiss();
                        }
                    }
                }).setNeutralButton(emvdialogTips.getDialogMesdkEcChoiceCancel(), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        try {
                            ECChoiceRunnable.this.choice = USER_CANCELED;
                        } finally {
                            synchronized (sync) {
                                sync.notify();
                            }

                        }
                    }
                });
                builder.setCancelable(false);
                builder.show();
            } else {
                EMVControllerListener listener = emvL2controller.getListener();
                emvL2controller.setCurrentEmvState(EMVState.EC_SWITCH);
                deviceLogger.debug(">>>[onRequestConfirmEC]");
                listener.onRequestConfirmEC(emvL2controller);
            }

        }

        public int startwaiting(int waitting) {
            synchronized (sync) {
                try {
                    sync.wait(waitting); // 等待30秒

                } catch (InterruptedException e) {
                }
            }
            return choice;
        }

        /**
         * 输入ec choice
         */
        void selectEC(int choice) {
            try {
                this.choice = choice;
            } finally {
                notifyWaiting();
            }
        }

        private void notifyWaiting() {
            synchronized (sync) {
                sync.notify();
            }
        }
    }

    /**
     * 电子现金/emv选择
     * <p>
     * 交易返回：
     * <p>
     * <ul>
     * <li>1：继续电子现金交易</li>
     * <li>0：不进行电子现金交易</li>
     * <li>－1:用户中止</li>
     * <li>－3:超时</li>
     * </ul>
     */
    @Override
    public int emv_ec_switch() {
        deviceLogger.debug("[emv_ec_switch]");
        if (!isControllerAlive())
            return -1;
        try {
            boolean isForceOnline = emvL2controller.getContext().getForceOnline();
            deviceLogger.debug("[emv_ec_switch] isForceOnline="+isForceOnline);
            if (isForceOnline) {
                return 0;
            }
            if (null == emvL2controller.getListener()) {
                deviceLogger.error("[emv_ec_switch]:emvL2controller.getListener() return null!");
                return -1;
            }
            if(emvL2controller!=null && emvL2controller.getEMVTimeOut()>0){
                USER_OPERATOR_WAITING_MILLS = emvL2controller.getEMVTimeOut() * 1000;
                USER_OPERATOR_WAITING_MILLS_PININPUT = emvL2controller.getEMVTimeOut() * 1000;
            }
            if (emvL2controller.getListener() instanceof EMVInterceptListener) {
                EMVInterceptListener listener = (EMVInterceptListener) emvL2controller.getListener();
                if (!listener.activateECSwitchInterceptor()) {
                    ECChoiceRunnable ecChoiceDialogger = new ECChoiceRunnable();
                    runOnUIThread(ecChoiceDialogger);
                    int ret = ecChoiceDialogger.startwaiting(USER_OPERATOR_WAITING_MILLS);
                    deviceLogger.debug("[emv_ec_switch] ret="+ret);
                    return ret;
                }
            }
            ECChoiceRunnable ecChoiceRunnable = new ECChoiceRunnable();
            getContextHelper().ecChoiceRunnable = ecChoiceRunnable;
            new Thread(ecChoiceRunnable).start();
            int ret = ecChoiceRunnable.startwaiting(USER_OPERATOR_WAITING_MILLS);
            deviceLogger.debug("[emv_ec_switch]2 ret="+ret);
            return ret;
        } catch (Exception e) {
            deviceLogger.error("[emv_ec_switch]ec switch failed!", e);
            return -1;
        }
    }

    /**
     * 获取金额和返现金额
     */
    @Override
    public int emv_get_bcdamt(byte transtype, byte[] cashPayload, byte[] cashbackPayload) {
        deviceLogger.debug("[emv_get_bcdamt] transtype="+transtype+" cashPayload="+InnerUtils.hexString(cashPayload)+ " cashbackPayload="+InnerUtils.hexString(cashbackPayload));
        if (!isControllerAlive())
            return -1;

        if (transtype == EMVLevel2Const.InnerEmvTransType.EMV_TRANS_INQUIRY || transtype == EMVLevel2Const.InnerEmvTransType.EMV_TRANS_ADMIN) {
            deviceLogger.debug("[emv_get_bcdamt] return 0; transtype="+transtype);
            return 0;
        }
        EMVTransInfo emvTransInfo = emvL2controller.getEmvTransInfo();
        String cashStr = emvL2controller.getContext().getAmountAuthorisedNumeric();
        byte[] cashBs = null;
        deviceLogger.debug("[emv_get_bcdamt] cashStr=" + cashStr);
        if (cashStr != null) {
            cashBs = toAmt(cashStr);
        } else {
            if (transtype == EMVLevel2Const.InnerEmvTransType.EMV_TRANS_PREAUTH || transtype == EMVLevel2Const.InnerEmvTransType.EMV_TRANS_GOODS || transtype == EMVLevel2Const.InnerEmvTransType.EMV_TRANS_EC_BINDLOAD || transtype == EMVLevel2Const.InnerEmvTransType.EMV_TRANS_RF_BINDLOAD || transtype == EMVLevel2Const.InnerEmvTransType.EMV_TRANS_EC_NOBINDLOAD || transtype == EMVLevel2Const.InnerEmvTransType.EMV_TRANS_RF_NOBINDLOAD || transtype == EMVLevel2Const.InnerEmvTransType.EMV_TRANS_EC_CASHLOAD || transtype == EMVLevel2Const.InnerEmvTransType.EMV_TRANS_RF_CASHLOAD) {
                try {
                    AmtEntryRunnable runnable = new AmtEntryRunnable(emvTransInfo);
                    getContextHelper().amtEntryRunnable = runnable;
                    new Thread(runnable).start();
                    runnable.startWaiting();
                    if (runnable.amtEntryRslt == 0) {
                        if (runnable.amtEntry == null) {
                            deviceLogger.warn("[emv_get_bcdamt]no input amount,emv finish");
                            return -1;
                        } else {
                            long amtInt = runnable.amtEntry.setScale(2, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).toBigInteger().longValue();
                            if (amtInt > 999999999999L) {
                                throw new IllegalArgumentException("amt out of range:" + amtInt);
                            }
                            cashBs = toAmt(Long.toString(amtInt));
                            emvL2controller.getContext().setAmountAuthorisedNumeric(Long.toString(amtInt));
                        }
                    } else {
                        deviceLogger.warn("[emv_get_bcdamt]input amount timeout,emv finish");
                        return -1;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return -1;
                }
            }
        }
        if (cashBs != null) {
            System.arraycopy(cashBs, 0, cashPayload, 0, cashBs.length);
        }
        String cashbackStr = emvL2controller.getContext().getAmountOtherNumeric();
        if (cashbackStr != null) {
            byte[] cashbackBs = toAmt(cashbackStr);
            if (cashbackBs != null) {
                System.arraycopy(cashbackBs, 0, cashbackPayload, 0, cashbackBs.length);
            }
        }
        deviceLogger.debug("[emv_get_bcdamt] return 0. transtype="+transtype+" cashPayload="+InnerUtils.hexString(cashPayload)+ " cashbackPayload="+InnerUtils.hexString(cashbackPayload));
        return 0;
    }

    class AmtEntryRunnable implements Runnable {

        private Object sync = new Object();

        volatile BigDecimal amtEntry = null;

        private EMVTransInfo emvTransInfo;
        volatile int amtEntryRslt = -1;

        public AmtEntryRunnable(EMVTransInfo emvTransInfo) {
            this.emvTransInfo = emvTransInfo;
        }

        @Override
        public void run() {
            try {
                if (!isControllerAlive())
                    return;

                EMVControllerListener listener = emvL2controller.getListener();
                emvL2controller.setCurrentEmvState(EMVState.AMOUNT_INPUT);
                deviceLogger.debug(">>>[onRequestInputAmount]");
                listener.onRequestInputAmount(emvL2controller);
            } catch (Exception e) {
                onError(e);
            }
        }

        void startWaiting() {
            synchronized (sync) {
                try {
                    if(emvL2controller!=null && emvL2controller.getEMVTimeOut()>0){
                        USER_OPERATOR_WAITING_MILLS = emvL2controller.getEMVTimeOut() * 1000;
                        USER_OPERATOR_WAITING_MILLS_PININPUT = emvL2controller.getEMVTimeOut() * 1000;
                    }
                    sync.wait(USER_OPERATOR_WAITING_MILLS);
                } catch (InterruptedException e) {
                    amtEntryRslt = -1;
                }
            }
        }

        /**
         * 输入pin，若为空或者为
         */
        void inputAmtEntry(BigDecimal amount) {
            try {
                this.amtEntry = amount;
                amtEntryRslt = 0;
            } finally {
                notifyWaiting();
            }
        }

        void onError(Throwable e) {
            try {
                deviceLogger.error("[AmtEntryRunnable]failed to do amtEntry input!", e);
            } finally {
                notifyWaiting();
            }
        }

        private void notifyWaiting() {
            synchronized (sync) {
                sync.notify();
            }
        }

    }

    class PinEntryRunnable implements Runnable {

        private Object sync = new Object();

        volatile byte[] pinEntry = null;

        private PINEntity pinEntity;

        /**
         * -1 输入失败 故障 -2 未输入 BYPASS -3 中止交易和Timeout >0 输入密码长度
         */
        volatile int pinEntryRslt = EMVLevel2Const.PinEntryRslt.INPUT_FAILED;

        public PinEntryRunnable(PINEntity pinEntity) {
            this.pinEntity = pinEntity;
        }

        @Override
        public void run() {
            try {
                if (!isControllerAlive())
                    return;
                EMVControllerListener listener = emvL2controller.getListener();
                boolean requireOnline = true;
                if (pinEntity.getPinRequiredType().equals(PinRequiredType.OFFLINE) || pinEntity.getPinRequiredType().equals(PinRequiredType.LAST_OFFLINE))
                    requireOnline = false;
                emvL2controller.setCurrentEmvState(EMVState.PIN_INPUT);
                deviceLogger.debug(">>>[onRequestInputPIN]");
                listener.onRequestInputPIN(emvL2controller, requireOnline, pinEntity);
            } catch (Exception e) {
                onError(e); // 仅通知pin输入过程异常，由上层通知交易处理
            }
        }

        void startWaiting() {
            deviceLogger.debug("[PinEntryRunnable]emvcore  pin--startWaiting:" + Thread.currentThread().getName());
            synchronized (sync) {
                try {
                    if(emvL2controller!=null && emvL2controller.getEMVTimeOut()>0){
                        USER_OPERATOR_WAITING_MILLS = emvL2controller.getEMVTimeOut() * 1000;
                        USER_OPERATOR_WAITING_MILLS_PININPUT = emvL2controller.getEMVTimeOut() * 1000;
                    }
                    sync.wait(USER_OPERATOR_WAITING_MILLS_PININPUT);
                } catch (InterruptedException e) {
                    pinEntryRslt = EMVLevel2Const.PinEntryRslt.INTERRUPTED_OR_TIMEOUT;
                }
            }
            if (pinEntryRslt == EMVLevel2Const.PinEntryRslt.INPUT_FAILED) {
                pinEntryRslt = EMVLevel2Const.PinEntryRslt.INTERRUPTED_OR_TIMEOUT;
            }
        }

        /**
         * 输入pin，若为空或者为
         */
        void inputPinEntry(int len, byte[] pinEntry) {
            try {
                if (len == 0) {
                    pinEntryRslt = EMVLevel2Const.PinEntryRslt.BYPASS;
                } else if (len == -1) {
                    pinEntryRslt = EMVLevel2Const.PinEntryRslt.INTERRUPTED_OR_TIMEOUT;
                } else {
                    deviceLogger.debug("[inputPinEntry] getEmvPinInputType:" + pinEntity.getPinRequiredType());
                    deviceLogger.debug("[inputPinEntry] pinEntry:" + EMVInnerUtils.hexString(pinEntry));
                    if (pinEntity.getPinRequiredType() == PinRequiredType.OFFLINE || pinEntity.getPinRequiredType() == PinRequiredType.LAST_OFFLINE) {
                        if (EMVInnerUtils.isSDK3()) {
                            if (null != pinEntry && pinEntry.length >= 2) {
                                if (Arrays.equals(pinEntry, new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00})) {
                                    pinEntryRslt = -2;
                                } else {
                                    pinEntryRslt = ((((pinEntry[0] & 0xff) << 8) | (pinEntry[1] & 0xff)) & 0xffff);
                                }
                            }
                            deviceLogger.debug("[inputPinEntry] NS3 OFFLINE pinEntryRslt:" + pinEntryRslt);
                            deviceLogger.debug("[inputPinEntry] NS3 OFFLINE pinEntry:" + EMVInnerUtils.hexString(pinEntry));
                        } else {
                            if (null != pinEntry && pinEntry.length >= 2) {
                                pinEntryRslt = len;
                                this.pinEntry = pinEntry;
                            }
                            deviceLogger.debug("[inputPinEntry]SDK2.0 OFFLINE pinEntryRslt:" + pinEntryRslt);
                            deviceLogger.debug("[inputPinEntry]SDK2.0 OFFLINE pinEntry:" + EMVInnerUtils.hexString(pinEntry));
                        }

                    } else {
                        pinEntryRslt = len;
                    }
                    this.pinEntry = pinEntry;
                    deviceLogger.debug("[inputPinEntry]pinEntryRslt:" + pinEntryRslt);
                    deviceLogger.debug("[inputPinEntry]pinEntry:" + EMVInnerUtils.hexString(pinEntry));
                }
            } finally {
                notifyWaiting();
            }
        }

        void onError(Throwable e) {
            try {
                deviceLogger.error("[inputPinEntry]failed to do pinEntry input!", e);
            } finally {
                notifyWaiting();
            }
        }

        private void notifyWaiting() {
            synchronized (sync) {
                sync.notify();
            }
        }

    }

    @Override
    public int emv_get_pinentry(int type, publickey pinPK, byte[] pinEntry) {
        deviceLogger.debug("[emv_get_pinentry] type="+type);
        if(pinPK != null){
            deviceLogger.debug("[emv_get_pinentry] pk_mod_len="+pinPK.pk_mod_len+" pk_exponent="+InnerUtils.hexString(pinPK.pk_exponent)+" pk_modulus="+InnerUtils.hexString(pinPK.pk_modulus));
        }
        if (!isControllerAlive())
            return -1;

        try {
            emvL2controller.refreshEmvTransInfo();
            PinRequiredType inputType = null;
            int offlinePwdCount =0;
                    switch (type) {
                case EMVLevel2Const.EmvPinInputType.EMV_OFFLINEPIN_INPUT:
                    inputType = PinRequiredType.OFFLINE;
                    offlinePwdCount = emvL2controller.fetchOfflinePwdCount();
                    // 获取脱机剩余密码
                    break;
                case EMVLevel2Const.EmvPinInputType.EMV_OFFLINE_ONLY_INPUT:
                    inputType = PinRequiredType.LAST_OFFLINE;
                    offlinePwdCount = emvL2controller.fetchOfflinePwdCount();
                    break;
                case EMVLevel2Const.EmvPinInputType.EC_ONLINEPIN_INPUT:
                    inputType = PinRequiredType.EC_ONLINE;
                    break;
                default:
                    inputType = PinRequiredType.ONLINE;
            }
            deviceLogger.debug("[emv_get_pinentry] inputType="+inputType+" offlinePwdCount="+offlinePwdCount);
            byte[] modulus = new byte[pinPK.pk_mod_len];
            System.arraycopy(pinPK.pk_modulus, 0, modulus, 0, pinPK.pk_mod_len);
            PINEntity pinEntity = new PINEntity(inputType, offlinePwdCount, modulus, (pinPK.pk_mod_len > 0 ? pinPK.pk_exponent : null));
            deviceLogger.debug("[emv_get_pinentry] emvcore pin:" + Thread.currentThread().getName());
            PinEntryRunnable runnable = new PinEntryRunnable(pinEntity);
            getContextHelper().pinEntryRunnable = runnable;
            new Thread(runnable).start();
            runnable.startWaiting();
            deviceLogger.debug("[emv_get_pinentry] pinEntryRslt="+runnable.pinEntryRslt+" pinEntry.length="+(runnable.pinEntry==null?null:runnable.pinEntry.length));

            if (runnable.pinEntryRslt > 0) {
                if (runnable.pinEntry == null || runnable.pinEntry.length <= 0) {//当成是空输入
                    deviceLogger.warn("[emv_get_pinentry] unexpected entry, why rslt is " + runnable.pinEntryRslt + ", but entry is empty?");
                    deviceLogger.debug("[emv_get_pinentry] PinEntryRslt.BYPASS");
                    return EMVLevel2Const.PinEntryRslt.BYPASS;
                } else {
                    System.arraycopy(runnable.pinEntry, 0, pinEntry, 0, runnable.pinEntry.length);
                    deviceLogger.debug("[emv_get_pinentry] pinEntry="+InnerUtils.hexString(pinEntry));
                }
            }
            deviceLogger.debug("[emv_get_pinentry] pinEntryRslt="+runnable.pinEntryRslt);
            return runnable.pinEntryRslt;
        } catch (Exception e) {
            e.printStackTrace();
            deviceLogger.error("[emv_get_pinentry]pin input failed!");
            return -1;
        }
    }

    @Override
    public int emv_icc_powerup(int[] arg0) {
        deviceLogger.debug("[emv_icc_powerup]");

        if (!isControllerAlive())
            return -1;
        if (null != emvL2controller.getEmvExtParams() && emvL2controller.getEmvExtParams().isExternalReader()) {
            emvL2controller.setCurrentEmvState(EMVState.ICC_POWER_ON);
            if(emvL2controller.getEmvExtParams().getMediaType()==0x00){
                byte[] result = emvL2controller.getExtICCardModule().powerOn();
                if (result != null) {
                    deviceLogger.debug("[emv_icc_powerup] ExtICCardModule().powerOn succ.");
                    return 0;
                } else {
                    deviceLogger.error("[emv_icc_powerup] external ic card poweron failed");
                    return -1;
                }
            }else{
                RFResult rfResult = emvL2controller.getExternalRFCardModule().powerOn(new RFCardType[]{RFCardType.ACARD}, 5);
                if (rfResult != null) {
                    deviceLogger.debug("[emv_icc_powerup] ExternalRFCardModule().powerOn succ.");
                    return 0;
                } else {
                    deviceLogger.error("[emv_icc_powerup] external RF card poweron failed");
                    return -1;
                }
            }


        }
        if (EMVTransContext._EMV_MEDIATYPE_ICCARD == emvL2controller.getContext().getMediaType()) {
            try {
                byte[] atr = iccardModule.powerOn(getContextHelper().getUseICCardSlot(), getContextHelper().getUseICCardType());
                if (deviceLogger.isDebugEnabled() && atr != null) {
                    deviceLogger.debug("[emv_icc_powerup]iccard powerup,atr:" + Dump.getHexDump(atr));
                }
                deviceLogger.debug("[emv_icc_powerup] ICCard.powerOn succ.");
                return 0;
            } catch (Exception e) {// 是否需要支持降级？
                deviceLogger.error("[emv_icc_powerup]power on iccard failed!" + getContextHelper().getUseICCardSlot() + "," + getContextHelper().getUseICCardType(), e);
            }
        } else if (EMVTransContext._EMV_MEDIATYPE_RFCARD == emvL2controller.getContext().getMediaType()) {
            throw new EMVTransferException("[emv_icc_powerup]rfcard not need to powerup first!");
        }
        deviceLogger.debug("[emv_icc_powerup] failed -1.");
        return -1;
    }


    /**
     * ic卡与apdu交互
     */
    @Override
    public int emv_icc_rw(int cardno, byte[] inbuf, int inlen, byte[] outbuf, int olen) {
        deviceLogger.debug("[emv_icc_rw]");

        if (!isControllerAlive())
            return -1;

        if (null != emvL2controller.getEmvExtParams() && emvL2controller.getEmvExtParams().isExternalReader()) {
            try {
                emvL2controller.setCurrentEmvState(EMVState.ICC_COMM);
                byte[] req = new byte[inlen];
                System.arraycopy(inbuf, 0, req, 0, inlen);
                byte[] rsp = null;
                if(emvL2controller.getEmvExtParams().getMediaType()==0x00){
                    rsp = emvL2controller.getExtICCardModule().transmit(req,null);
                }else{
                    rsp = emvL2controller.getExternalRFCardModule().transmit(req);
                }
                if (rsp == null || rsp.length < 1) {
                    deviceLogger.error("[emv_icc_rw]entry is empty");
                    return -1;
                } else {
                    System.arraycopy(rsp, 0, outbuf, 0, rsp.length);
                }
                deviceLogger.debug("[emv_icc_rw]1 sendData="+InnerUtils.hexString(req));
                deviceLogger.debug("[emv_icc_rw]1 receiveData="+InnerUtils.hexString(rsp));
                return rsp.length;
            } catch (Exception ex) {
                ex.printStackTrace();
                return -1;
            }
        }
        if (EMVTransContext._EMV_MEDIATYPE_ICCARD == emvL2controller.getContext().getMediaType()) {
            try {
                byte[] req = new byte[inlen];
                System.arraycopy(inbuf, 0, req, 0, inlen);
                byte[] rslt = iccardModule.transmit(getContextHelper().getUseICCardSlot(), getContextHelper().getUseICCardType(), req, ICCARD_COMMUNICATE_TIMEOUT_MILLS);
                deviceLogger.debug("[emv_icc_rw], send iccmd finished:" + rslt.length);
                System.arraycopy(rslt, 0, outbuf, 0, rslt.length);
                deviceLogger.debug("[emv_icc_rw], return:" + rslt.length);

                deviceLogger.debug("[emv_icc_rw]2 sendData="+InnerUtils.hexString(req));
                deviceLogger.debug("[emv_icc_rw]2 receiveData="+InnerUtils.hexString(rslt));
                return rslt.length;
            } catch (Exception e) {
                e.printStackTrace();
                deviceLogger.error("[emv_icc_rw] by iccard failed!", e);
            }
        } else if (EMVTransContext._EMV_MEDIATYPE_RFCARD == emvL2controller.getContext().getMediaType()) {
            try {
//                if (getContextHelper().getRfrslt() == null) {//opencardReader接口已经上电，没必要再上电
//                    RFResult sRslt = rfcardModule.powerOn(new RFCardType[]{RFCardType.ACARD, RFCardType.BCARD}, 3, null);
//                    getContextHelper().setRfrslt(sRslt);
//                    if (sRslt.getRfcardType() == null) {
//                        deviceLogger.error("[emv_icc_rw]power on rfcard failed!");
//                        return -1;
//                    }
//                    if (deviceLogger.isDebugEnabled() && (sRslt.getATQA() != null))
//                        deviceLogger.debug("iccard powerup,atr:" + Dump.getHexDump(sRslt.getATQA()));
//                }
                byte[] req = new byte[inlen];
                System.arraycopy(inbuf, 0, req, 0, inlen);
                long apduStartTime = System.currentTimeMillis();
                int[] outAPDULen = new int[1];
                byte[] outbufAPDU = new byte[4000];
                int ret = NdkApiManager.getNdkApiManager().getRfCard().NDK_RfidPiccApdu(req.length, req, outAPDULen, outbufAPDU);
                if(ret!=0){
                    deviceLogger.error("[emv_icc_rw]:by rfcard failed!"+ret);
                    return -1;
                }
                byte[] rslt =  new byte[outAPDULen[0]];
                System.arraycopy(outbufAPDU, 0, rslt, 0,outAPDULen[0]);
                deviceLogger.debug("[emv_icc_rw]:req:"+(req==null?null:InnerUtils.hexString(req)));

                if(req!=null && (InnerUtils.hexString(req).startsWith("00A4") || InnerUtils.hexString(req).startsWith("80A8"))){//统计PPSE ,SelectAID，GPO总耗时
                    long time =  System.currentTimeMillis()-apduStartTime;
                    deviceLogger.debug("[emv_icc_rw]:EmvDurationUtil.addEmvTime:"+time);
                    EmvDurationUtil.addEmvTime(time);
                }

                deviceLogger.debug("[emv_icc_rw], send rfcmd finished:" + rslt.length);
                System.arraycopy(rslt, 0, outbuf, 0, rslt.length);
                deviceLogger.debug("[emv_icc_rw], return:" + rslt.length);

                deviceLogger.debug("[emv_icc_rw]3 sendData="+InnerUtils.hexString(req));
                deviceLogger.debug("[emv_icc_rw]3 receiveData="+InnerUtils.hexString(rslt));
                return rslt.length;
            } catch (Exception e) {
                e.printStackTrace();
                deviceLogger.error("[emv_icc_rw] by rfcard failed!", e);
            }
        }
        deviceLogger.debug("[emv_icc_rw] return -1.");
        return -1;
    }

    /**
     * ic卡/rf卡上下电
     */
    @Override
    public int emv_rf_powerdown(int arg0) {
        deviceLogger.debug("[emv_rf_powerdown]" + arg0);
        if (!isControllerAlive())
            return -1;

        if (emvL2controller == null || emvL2controller.getContext() == null) {
            deviceLogger.debug("[emv_rf_powerdown] emvL2controller="+emvL2controller);
            return 0;
        }

        if (null != emvL2controller.getEmvExtParams() && emvL2controller.getEmvExtParams().isExternalReader()) {
            emvL2controller.setCurrentEmvState(EMVState.ICC_POWER_OFF);
            if(emvL2controller.getEmvExtParams().getMediaType()==0x00){
                emvL2controller.getExtICCardModule().powerOff();
                deviceLogger.debug("[emv_rf_powerdown] Ext IC powerOff");
                return 0;
            }else{
                boolean result = emvL2controller.getExternalRFCardModule().powerOff();
                if (result) {
                    deviceLogger.debug("[emv_rf_powerdown] Ext RF powerOff");
                    return 0;
                } else {
                    deviceLogger.debug("[emv_rf_powerdown] Ext RF powerOff failed.");
                    return -1;
                }
            }

        }
        if (EMVTransContext._EMV_MEDIATYPE_ICCARD == emvL2controller.getContext().getMediaType()) {
            try {
                iccardModule.powerOff(getContextHelper().getUseICCardSlot(), getContextHelper().getUseICCardType());
                deviceLogger.debug("[emv_rf_powerdown] IC powerOff");
                return 0;
            } catch (Exception e) {
                e.printStackTrace();
                deviceLogger.error("[emv_rf_powerdown]power off iccard failed!" + getContextHelper().getUseICCardSlot() + "," + getContextHelper().getUseICCardType(), e);
            }
        } else if (EMVTransContext._EMV_MEDIATYPE_RFCARD == emvL2controller.getContext().getMediaType()) {
            try {
                // rfcardModule.powerOff(3);//这个接口比较耗时，可以只做NDK_RfidPiccDeactivate。因为onemvfinish前会做poweroff
                NdkApiManager.getNdkApiManager().getRfCard().NDK_RfidPiccDeactivate(0);
                deviceLogger.debug("[emv_rf_powerdown] RF powerOff");
                return 0;
            } catch (Exception e) {
                e.printStackTrace();
                deviceLogger.error("[emv_rf_powerdown]power off rfcard failed!", e);
            }
        }
        deviceLogger.debug("[emv_rf_powerdown] return -1.");
        return -1;

    }

    /**
     * 交易流水号＋1
     */
    @Override
    public int inc_tsc() {
        deviceLogger.debug("[inc_tsc]");
        if (!isControllerAlive())
            return -1;
        try {
            if (null == emvL2controller.getListener()) {
                deviceLogger.error("[inc_tsc]:emvL2controller.getListener() return null!");
                return -1;
            }
            if (emvL2controller.getListener() instanceof EMVInterceptListener) {
                EMVInterceptListener listener = (EMVInterceptListener) emvL2controller.getListener();
                if (listener.activateTransactionCountInterceptor()) {
                    return listener.increaseTransactionCount();
                }
            }
            if (getContextHelper().getSeqGen() == null) {
                deviceLogger.error("[inc_tsc]:getContextHelper().getSeqGen() return null");
                return -1;
            }
            int seq = getContextHelper().getSeqGen().next();
            deviceLogger.debug("[inc_tsc] seq:"+seq);
            if (seq <= 0){
                seq=1;
                deviceLogger.warn("[inc_tsc]trans seq generate failed:" + seq);
            }
            return seq;
        } catch (Exception e) {
            deviceLogger.error("[inc_tsc] failed!", e);
            return -1;
        }
    }

    /**
     * 根据张捷说法，全部返回拒绝
     */
    @Override
    public int iss_ref(byte[] panbs, int panlen) {
        deviceLogger.debug("[iss_ref]");
        return 1;
        // if(!isControllerAlive())
        // return -1;
        //
        // try{
        // String title = "交易确认";
        // String[] buttonsDescription = new String[]{"脱机批准","脱机拒绝"};
        // String context = "请联系你的银行：\n应用主账号："+new String(panbs,0,panlen);
        // ConfirmDialogger confirmDialogger = new ConfirmDialogger(title,
        // buttonsDescription, context,true);
        // runOnUIThread(confirmDialogger);
        // if(confirmDialogger.startwaiting(USER_OPERATOR_WAITING_MILLS))
        // return 0;
        // else
        // return 1;
        // }catch(Exception e){
        // deviceLogger.error("iss_ref failed!",e);
        // return -1;
        // }
    }

    /*
     * Func: 屏幕显示信息函数 Para: title 标题 msg 显示信息(16进制) len msg长度 yesno
     * 是否需要显示(确认和取消) 1 需要显示 0 不显示 waittime 等待时间 Return: 如果yesno=1, 返回1
     * 表示确认，返回0表示取消 如果yesno=0, 返回等待时输入的键值(返回值无意义)
     */
    @Override
    public int lcd_msg(String title, byte[] msg, int len, int yesno, int waittime) {
        deviceLogger.debug("[lcd_msg] title="+title+" yesno="+yesno+" waittime="+waittime+" len="+len+" msg="+InnerUtils.hexString(msg));
        if (!isControllerAlive())
            return -1;
        if(emvL2controller!=null && emvL2controller.getEMVTimeOut()>0){
            USER_OPERATOR_WAITING_MILLS = emvL2controller.getEMVTimeOut() * 1000;
            USER_OPERATOR_WAITING_MILLS_PININPUT = emvL2controller.getEMVTimeOut() * 1000;
        }
        try {
            if (null == emvL2controller.getListener()) {
                deviceLogger.error("[lcd_msg]:emvL2controller.getListener() return null!");
                return -1;
            }
            if (emvL2controller.getListener() instanceof EMVInterceptListener) {
                EMVInterceptListener listener = (EMVInterceptListener) emvL2controller.getListener();
                if (!listener.activateTransactionMessageInterceptor()) {
                    String titleStr = new String(title);
                    String context = null;
                    EMVDialogTips emvDialogTips = new EMVDialogTips();
                    try {
                        context = new String(msg, 0, len, "GBK");
                    } catch (UnsupportedEncodingException e) {
                        context = "";
                    }
                    if (yesno == 1) {
                        String[] buttonsDescription = new String[]{emvDialogTips.getDialogMesdkConfirm(), emvDialogTips.getDialogMesdkCancel()};
                        MessageConfirmRunnable messageConfirmRunnable = new MessageConfirmRunnable(titleStr, buttonsDescription, context, true, waittime);
                        runOnUIThread(messageConfirmRunnable);
                        if (messageConfirmRunnable.startwaiting(USER_OPERATOR_WAITING_MILLS)) {
                            deviceLogger.debug("[lcd_msg] return 1.");
                            return 1;
                        }else{
                            deviceLogger.debug("[lcd_msg] return 0.");
                            return 0;
                        }
                    } else {
                        String[] buttonsDescription = new String[]{emvDialogTips.getDialogMesdkConfirm()};
                        MessageConfirmRunnable messageConfirmRunnable = new MessageConfirmRunnable(titleStr, buttonsDescription, context, false, waittime);
                        runOnUIThread(messageConfirmRunnable);
                        messageConfirmRunnable.startwaiting(USER_OPERATOR_WAITING_MILLS);
                    }
                    deviceLogger.debug("[lcd_msg] return 1..");
                    return 1;
                }
            }
            String context = "";
            try {
                context = new String(msg, 0, len, "GBK");
            } catch (UnsupportedEncodingException e) {
                context = "";
            }
            MessageConfirmRunnable messageConfirmRunnable = new MessageConfirmRunnable(new String(title), null, context, (yesno == 1 ? true : false), waittime);
            getContextHelper().messageConfirmRunnable = messageConfirmRunnable;
            new Thread(messageConfirmRunnable).start();
            boolean isConfirm = messageConfirmRunnable.startwaiting(USER_OPERATOR_WAITING_MILLS);
            deviceLogger.debug("[lcd_msg] isConfirm="+isConfirm);
            if (isConfirm) {
                return 1;
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            deviceLogger.error("[lcd_msg] failed!", e);
            return -1;
        }
    }

    private byte[] toAmt(String amtStr) {
        amtStr = EMVInnerUtils.padleft(amtStr, 12, '0');// 填充到12个字长
        byte[] amtBs = EMVInnerUtils.str2bcd(amtStr, true);

        deviceLogger.debug("[toAmt]get amt:" + Dump.getHexDump(amtBs));
        return amtBs;
    }

    /**
     * 交易相关的回调，都要进行改项判定
     *
     * @return
     */
    private boolean isControllerAlive() {
        boolean flag = (this.emvL2controller != null);
        if (!flag)
            deviceLogger.debug("[isControllerAlive]emvController should have not been started!");

        return flag;
    }

    void setTransferController(EMVLevel2TransferController controller) {
        this.emvL2controller = controller;
    }

    void clearTransferController() {
        this.emvL2controller = null;
    }

    /***************************** 文件操作接口 *****************************************/
    /*
     * String filename：文件名称（包括路径） int filemode： #define FILE_READ 1 #define
     * FILE_WRITE 2 没有的话自动创建 返回值： 失败小于0，否则成功
     */
    public synchronized int NL_open(String filename, int filemode) {
        handler = brancher.findOrCreateMemoryFile(filename);
        return handler.open(filemode);
    }

    /*** 以下函数，根据刘罡反馈，均不需要实现 ****/
    public int NL_truncate(String filename, int size) {
        throw new UnsupportedOperationException("[NL_truncate]not support this method!");
    }

    public int NL_delete(String filename) {
        throw new UnsupportedOperationException("not support this method!");
    }

    public int NL_rename(String srcname, String dstname) {
        throw new UnsupportedOperationException("not support this method!");
    }

    @Override
    public int outcome_msg(ui_request_data msg) {
        deviceLogger.debug("[outcome_msg]");
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int send_msg(ep_opt epopt) {
        deviceLogger.debug("[send_msg]");
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int dek_det(byte mestype, byte[] outbuf, int[] outbuflen) {
        deviceLogger.debug("[dek_det]");
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int emv_debug(byte[] debuglog, int len) {
        deviceLogger.debug("[emv_debug]");
        if (null == debuglog)
            return 0;
        byte[] emvlog = new byte[(len <= debuglog.length ? len : debuglog.length)];
        System.arraycopy(debuglog, 0, emvlog, 0, emvlog.length);
        deviceLogger.debug("[EMV]:" + new String(emvlog));
        return 0;

    }

    @Override
    public int lcd_msg_new(int msgID, int yesno, int waittime) {
        deviceLogger.debug("[lcd_msg_new],msgID=" + msgID + ";yesno=" + yesno + ";waittime="+waittime);
        if (!isControllerAlive())
            return -1;
        try {
            String msgtitle = null;
            String language = device.getDefaultLocale().getLanguage();
            if (null != languageID)
                language = new String(languageID);
            switch (msgID) {
                case 0x0101: //1 "PIN 输入成功"
                    if (language.equals("zh") || language.equals("ZH")) {

                        msgtitle = new String("PIN 输入成功\r\n");

                    } else if ("fr".equals(language)) {

                        msgtitle = new String("PIN input successful\r\n");

                    } else {

                        msgtitle = new String("PIN input successful\r\n");
                    }
                    break;
                case 0x0102: //2 "PIN 错误"

                    if ("zh".equals(language) || language.equals("ZH")) {

                        msgtitle = new String("PIN 错误\r\n");

                    } else if ("fr".equals(language)) {

                        msgtitle = new String("INCORRECT PIN\r\n");

                    } else {

                        msgtitle = new String("INCORRECT PIN\r\n");
                    }
                    break;
                case 0x0103: //3 "CAPK 校验失败"
                    if ("zh".equals(language) || language.equals("ZH")) {

                        msgtitle = new String("CAPK 校验失败\r\n");

                    } else if ("fr".equals(language)) {

                        msgtitle = new String("CAPK spécial est incorrect\r\n");

                    } else {

                        msgtitle = new String("Special CAPK is incorrect\r\n");
                    }
                    break;
                case 0x0104: //4 "特定CAPK 缺失"
                    if ("zh".equals(language) || language.equals("ZH")) {

                        msgtitle = new String("特定CAPK 缺失\r\n");

                    } else if ("fr".equals(language)) {

                        msgtitle = new String("CAPK spécial non trouvé\r\n");

                    } else {

                        msgtitle = new String("Special CAPK not found\r\n");
                    }
                    break;
                case 0x0105: //5 "是否强迫联机"
                    if ("zh".equals(language) || language.equals("ZH")) {

                        msgtitle = new String("是否强迫联机\r\n");

                    } else if ("fr".equals(language)) {

                        msgtitle = new String("Si forcé à en ligne\r\n");

                    } else {

                        msgtitle = new String("Whether forced to online \r\n");
                    }
                    break;
                case 0x0106: //6 "发卡行认证失败"
                    if ("zh".equals(language) || language.equals("ZH")) {

                        msgtitle = new String("\n\n发卡行认证失败\n\r\n");

                    } else if ("fr".equals(language)) {

                        msgtitle = new String("\n  émetteur\n authentication\n  échoué\r\n");

                    } else {

                        msgtitle = new String("\n  Issuer\n  authentication\n  failed\r\n");
                    }
                    break;
                case 0x0107: //7 "PIN重试次数超限"
                    if ("zh".equals(language) || language.equals("ZH")) {

                        msgtitle = new String("\nPIN重试次数超限\r\n");

                    } else if ("fr".equals(language)) {

                        msgtitle = new String("\n  NIP Limite d'essai\n    dépassé\r\n");

                    } else {

                        msgtitle = new String("\n  PIN Try Limit\n  exceeded\r\n");
                    }
                    break;
                case 0x0108: //8 "服务不允许"
                    if ("zh".equals(language) || language.equals("ZH")) {

                        msgtitle = new String("\n服务不允许\r\n");

                    } else if ("fr".equals(language)) {

                        msgtitle = new String("\n  Un service\n Interdit\r\n");

                    } else {

                        msgtitle = new String("\n  Service\n  Not allowed\r\n");
                    }
                    break;
            }
            String msgContent = "YES(Confirm)    NO(Cancel)";
            if (yesno == 0) {
                msgContent = "YES(Confirm)";
            }
            return lcd_msg(msgtitle, msgContent.getBytes(), msgContent.getBytes().length, yesno, waittime);
        } catch (Exception e) {
            deviceLogger.error("[lcd_msg_new] error:" + e);
        }
        deviceLogger.debug("[lcd_msg_new] return 0;");
        return 0;
    }

    class LanguageChoiceRunnable implements Runnable {
        private Object sync = new Object();
        volatile int choice = -1;
        private byte[] languages;
        private String[] languageToSelect;
        private int len;

        public LanguageChoiceRunnable(byte[] languages, int len) {
            this.languages = languages;
            this.len = len;
        }

        @Override
        public void run() {
            int length = (len <= (languages.length / 2) ? len : (languages.length / 2));
            languageToSelect = new String[length];
            int j = 0;
            for (int i = 0; i < length; i++) {
                languageToSelect[i] = new String(new byte[]{languages[j], languages[j + 1]});
                j = j + 2;
            }
            boolean isLevel2Listener = emvL2controller.getListener() instanceof EMVInterceptListener;
            if (isLevel2Listener && ((EMVInterceptListener) emvL2controller.getListener()).activateLanguageSelectInterceptor()) {
                EMVDialogTips emvDialogTips = new EMVDialogTips();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContextHelper().getContext());
                builder.setTitle(emvDialogTips.getLanguage());
                builder.setSingleChoiceItems(languageToSelect, 0, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        try {
                            deviceLogger.debug("[LanguageChoiceRunnable]languageChoiceDialogger i=" + i);
                            languageID = languageToSelect[i].getBytes();
                            LanguageChoiceRunnable.this.choice = 0;
                        } finally {
                            synchronized (sync) {
                                sync.notify();
                            }
                            dialog.dismiss();
                        }
                    }
                }).show();
            } else {
                EMVControllerListener listener = emvL2controller.getListener();
                emvL2controller.setCurrentEmvState(EMVState.LANGUAGE_SELECT);
                deviceLogger.debug(">>>[onRequestSelectLanguage]");
                listener.onRequestSelectLanguage(emvL2controller, languageToSelect);
            }

        }

        public int startwaiting(int waitting) {
            synchronized (sync) {
                try {
                    sync.wait(waitting); // 等待30秒
                } catch (InterruptedException e) {
                }
            }
            return choice;
        }

        /**
         * 输入language
         */
        void selectLanguage(byte[] languages) {
            try {
                languageID = languages;
                if (languages != null && languages.length == 2) {
                    LanguageChoiceRunnable.this.choice = 0;
                }
            } finally {
                notifyWaiting();
            }
        }

        private void notifyWaiting() {
            synchronized (sync) {
                sync.notify();
            }
        }
    }

    @Override
    public int language_select(byte[] bytes, int len, int i1, int i2) {
        deviceLogger.debug("[language_select],bytes=" + (bytes == null ? null : EMVInnerUtils.hexString(bytes)) + ";len=" + len);
        if (!isControllerAlive())
            return -1;
        if(emvL2controller!=null && emvL2controller.getEMVTimeOut()>0){
            USER_OPERATOR_WAITING_MILLS = emvL2controller.getEMVTimeOut() * 1000;
            USER_OPERATOR_WAITING_MILLS_PININPUT = emvL2controller.getEMVTimeOut() * 1000;
        }
        try {
            if (null == emvL2controller.getListener()) {
                deviceLogger.error("[language_select]:emvL2controller.getListener() return null!");
                return -1;
            }
            if (emvL2controller.getListener() instanceof EMVInterceptListener) {
                EMVInterceptListener listener = (EMVInterceptListener) emvL2controller.getListener();
                if (!listener.activateLanguageSelectInterceptor()) {
                    LanguageChoiceRunnable languageChoiceRunnable = new LanguageChoiceRunnable(bytes, len);
                    runOnUIThread(languageChoiceRunnable);
                    int ret = languageChoiceRunnable.startwaiting(USER_OPERATOR_WAITING_MILLS);
                    deviceLogger.debug("[language_select] ret="+ret);
                    return ret;
                }
            }

            LanguageChoiceRunnable languageChoiceRunnable = new LanguageChoiceRunnable(bytes, len);
            getContextHelper().languageChoiceRunnable = languageChoiceRunnable;
            new Thread(languageChoiceRunnable).start();
            int ret = languageChoiceRunnable.startwaiting(USER_OPERATOR_WAITING_MILLS);
            deviceLogger.debug("[language_select] ret="+ret);
            return ret;
        } catch (Exception e) {
            deviceLogger.error("[language_select] failed! ", e);
            return -1;
        }
    }

    @Override
    public int after_final_select(byte[] aid, int aidLen, byte[] usTlvData, int[] nTlvDataLen) {
        //NDK emv,AfterFinalSelect callback Flag 为1时会回调到
        deviceLogger.debug("[after_final_select],aid=" + (aid == null ? null : EMVInnerUtils.hexString(aid)) + ";aidLen=" + aidLen+";usTlvData:"+(usTlvData==null?null:InnerUtils.hexString(usTlvData)));
        if (!isControllerAlive())
            return -1;
        if(emvL2controller!=null && emvL2controller.getEMVTimeOut()>0){
            USER_OPERATOR_WAITING_MILLS = emvL2controller.getEMVTimeOut() * 1000;
            USER_OPERATOR_WAITING_MILLS_PININPUT = emvL2controller.getEMVTimeOut() * 1000;
        }
        try {
            if (null == emvL2controller.getListener()) {
                deviceLogger.error("[after_final_select]:emvL2controller.getListener() return null!");
                return -1;
            }
            EMVTransInfo emvTransInfo = emvL2controller.getEmvTransInfo();
            emvTransInfo.setAid(aid);
            FinalSelectRunnable runnable = new FinalSelectRunnable(null);
            getContextHelper().finalSelectRunnable = runnable;
            new Thread(runnable).start();
            runnable.startWaiting();
            if (runnable.usTLVData!=null) {
                deviceLogger.debug("[after_final_select] runnable.usTLVData:"+(InnerUtils.hexString(runnable.usTLVData)));
                System.arraycopy(runnable.usTLVData,0,usTlvData,0,runnable.usTLVData.length);
                nTlvDataLen[0] = runnable.usTLVData.length;
            } else {
                deviceLogger.warn("[after_final_select]input final select,usTLVData null");
            }
            deviceLogger.debug("[after_final_select] return 0");
            return 0;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                emvL2controller.setCurrentEmvState(EMVState.FINAL_APPLICATION_SELECT_COMPLETE);
                ((MEEMVLevel2) device.getStandardModule(ModuleType.EMV)).setFinanlSelData(EMVUtils.newTlvPackage());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        deviceLogger.warn("[after_final_select] return -1");
        return -1;
    }

    class FinalSelectRunnable implements Runnable {

        private Object sync = new Object();

        volatile byte[] usTLVData = null;


        /**
         * -1 输入失败 故障
         */
        volatile int finalSelectRslt = EMVLevel2Const.PinEntryRslt.INPUT_FAILED;

        public FinalSelectRunnable(byte[] usTLVData) {
            this.usTLVData = usTLVData;
        }

        @Override
        public void run() {
            try {
                if (!isControllerAlive())
                    return;

                emvL2controller.setCurrentEmvState(EMVState.FINAL_APPLICATION_SELECT);
                EMVControllerListener listener = emvL2controller.getListener();
                deviceLogger.debug(">>>[onRequestConfirmFinalAppSelection]");

                listener.onRequestConfirmFinalAppSelection(emvL2controller);

            } catch (Exception e) {
                onError(e); // 仅通知最终选择输入过程异常，由上层通知交易处理
            }
        }

        void startWaiting() {
            deviceLogger.debug("[FinalSelectRunnable] startWaiting:" + Thread.currentThread().getName());
            synchronized (sync) {
                try {
                    if(emvL2controller!=null && emvL2controller.getEMVTimeOut()>0){
                        USER_OPERATOR_WAITING_MILLS = emvL2controller.getEMVTimeOut() * 1000;
                    }
                    sync.wait(USER_OPERATOR_WAITING_MILLS);
                } catch (InterruptedException e) {
                    finalSelectRslt = EMVLevel2Const.PinEntryRslt.INPUT_FAILED;
                }
            }
            if (finalSelectRslt == EMVLevel2Const.PinEntryRslt.INPUT_FAILED) {
                finalSelectRslt = EMVLevel2Const.PinEntryRslt.INTERRUPTED_OR_TIMEOUT;
            }
        }

        /**
         * 输入最终选择数据
         */
        void inputFinalSelect(int len, byte[] data) {
            try {
                TLVPackage tlvPackage = ((MEEMVLevel2) device.getStandardModule(ModuleType.EMV)).getFinanlSelData();
                byte[] finalData = tlvPackage.pack();
                this.usTLVData = finalData;
                deviceLogger.debug("[inputFinalSelect]usTLVData:"+(usTLVData==null?null:InnerUtils.hexString(usTLVData)));
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                notifyWaiting();
            }
        }

        void onError(Throwable e) {
            try {
                deviceLogger.error("[inputFinalSelect]failed to do inputFinalSelect input!", e);
            } finally {
                notifyWaiting();
            }
        }

        private void notifyWaiting() {
            synchronized (sync) {
                sync.notify();
            }
        }

    }

    public EMVLevel2TransferController getTransferController() {
        return this.emvL2controller;
    }

    /**
     * 判断aid列表是否包含国内发行的VISA卡的aid
     * @param aidEntityList aid列表
     * @return
     */
    private boolean hasVCDA(List<AIDEntity> aidEntityList){
        if(aidEntityList!=null && aidEntityList.size()>0){
            for(AIDEntity aidEntity:aidEntityList){
                byte[] aid = aidEntity.getAid();
                if(aid!=null && aid.length>=5 && Arrays.equals(new byte[]{aid[0],aid[1],aid[2],aid[3],aid[4]},new byte[]{(byte)0xA0, 0x00, 0x00, 0x02, 0x41})){
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * 判断aid列表是否包含国内发行的万事达卡（Mastercard）境内AID（'A0000000108888'）
     * @param aidEntityList aid列表
     * @return
     */
    private boolean hasMaster(List<AIDEntity> aidEntityList){
        if(aidEntityList!=null && aidEntityList.size()>0){
            for(AIDEntity aidEntity:aidEntityList){
                byte[] aid = aidEntity.getAid();
                if(aid!=null && aid.length>=5 && Arrays.equals(new byte[]{aid[0],aid[1],aid[2],aid[3],aid[4]},new byte[]{(byte)0xA0, 0x00, 0x00, 0x00, 0x10})){
                    return true;
                }
            }
        }
        return false;
    }
}
