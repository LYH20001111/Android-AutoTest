package com.newland.sdk.me.module.emv;

import com.newland.emv.jni.type.EmvConst;
import com.newland.sdk.module.emv.EmvExtParams;
import com.newland.sdk.module.emv.TransactionExtParams;
import com.newland.sdk.mtype.Device;
import com.newland.sdk.mtype.DeviceRTException;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.common.ErrorCode;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.module.cardreader.CardType;
import com.newland.sdk.module.emv.AccountType;
import com.newland.sdk.module.emv.EMVControllerListener;
import com.newland.sdk.module.emv.EMVTransController;
import com.newland.sdk.module.emv.EMVTransInfo;
import com.newland.sdk.module.emv.OnlineTransactionData;
import com.newland.sdk.mtype.util.InnerUtils;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @description: Emv event controller
 * @author: Lindan
 * @create: 2019/8/2
 */
public class EMVLevel2TransferController extends AbstractEMVTransController implements EMVTransController {
    private DeviceLogger logger = DeviceLoggerFactory.getLogger("EMVLevel2TransferController");
    volatile EMVLevel2ContextHelper contextHelper = new EMVLevel2ContextHelper();


    public EMVLevel2TransferController(Device device, EmvExtParams emvExtParams, EMVControllerListener emvControllerListener) {
        super(device, emvExtParams, emvControllerListener);
    }

    public EMVLevel2TransferController(Device device, EmvExtParams emvExtParams, EMVControllerListener emvControllerListener, List<EMVTransStep> expectedSteps) {
        super(device, emvExtParams, emvControllerListener, expectedSteps);
    }


    @Override
    public EMVTransInfo doEmvCoreStep(EMVTransContext context, EMVTransInfo emvTransInfo) {
        return getEmvModule().doEmvStep0(context, emvTransInfo);
    }

    @Override
    protected EMVTransInfo doEmvCoreSecondIssuance(EMVTransContext context, OnlineTransactionData secondIssuanceRequest, EMVTransInfo emvTransInfo) {
        return getEmvModule().doSecondIssurance0(context, secondIssuanceRequest, emvTransInfo);
    }

    @Override
    protected void doEmvCoreFinish(EMVTransContext context, boolean isSuccess) {
        getEmvModule().doEmvFinish0(context, isSuccess);
    }

    @Override
    public void setSelectedApplication(int index) {
        if (this.contextHelper == null || this.contextHelper.aidselectRunnable == null) {
            throw new DeviceRTException(ErrorCode.EMV_TRANSFER_FAILED, "aidselectRunnable should not be null!");
        }
        this.contextHelper.aidselectRunnable.selectAid(index);
    }

    @Override
    public void cancelEMVProcess() {
        logger.debug("[cancelEMVProcess] currentEmvState: " + currentEmvState);
        switch (currentEmvState) {
            case PREPARED:
                break;
            case EC_SWITCH:
                if (this.contextHelper.ecChoiceRunnable != null) {
                    this.contextHelper.ecChoiceRunnable.selectEC(-1);
                }
                break;
            case MESSAGE_CONFIRM:
                confirmMessage(false);
                break;
            case LANGUAGE_SELECT:
                setSelectedLanguage(null);
                break;
            case ACCOUNT_SELECT:
                setSelectedAccountType(null);
                break;
            case PIN_INPUT:
                setPIN(null);
                break;
            case AMOUNT_INPUT:
                if (this.contextHelper.amtEntryRunnable != null) {
                    this.contextHelper.amtEntryRunnable.inputAmtEntry(null);
                }
                break;
            case EP_AMOUNT_INPUT:
                if (getEpRFCardRequestAmtRunnable() != null) {
                    getEpRFCardRequestAmtRunnable().inputAmtResult(null);
                    getEmvTransInfo().setErrorcode(-7);
                }
                break;
            case CERT_CONFIRM:
                confirmID(false);
                break;
            case APPLICATION_SELECT:
                setSelectedApplication(-1);
                break;
            case CARDINFO_CONFIRM:
            case FINAL_APPLICATION_SELECT:
                confirmInformation(false);
                break;
            case ICC_POWER_ON:
                if (getExternalRFCardModule() != null) {
                    getExternalRFCardModule().reset();
                }
                break;
            case ICC_POWER_OFF:
                if (getExternalRFCardModule() != null) {
                    getExternalRFCardModule().reset();
                }
                break;
            case ICC_COMM:
                if (getExternalRFCardModule() != null) {
                    getExternalRFCardModule().reset();
                }
                break;
            case TRADE_ONLINE:
            default:
                if (null != getEmvTransInfo()) {
                    getEmvTransInfo().setErrorcode(0);
                    getEmvTransInfo().setEmvrsltCode(_EMV_RSLT_STEP_FAILED);
                    getEmvTransInfo().setExecuteRslt(_EMV_RSLT_STEP_FAILED);
                }
                doEmvFinish0(false);
                break;
        }

    }

    @Override
    public void setSelectedAccountType(AccountType accountType) {
        if (this.contextHelper == null || this.contextHelper.accountTypeSelectedRunnable == null) {
            throw new DeviceRTException(ErrorCode.EMV_TRANSFER_FAILED, "accountTypeSelectedRunnable should not be null!");
        }
        int account = -1;
        if (accountType != null) {
            switch (accountType) {
                case DEFAULT:
                    account = 1;
                    break;
                case SAVINGS:
                    account = 2;
                    break;
                case CHEQUE_DEBIT:
                    account = 3;
                    break;
                case CREDIT:
                    account = 4;
                    break;
            }
        }

        this.contextHelper.accountTypeSelectedRunnable.selectAccount(account);
    }

    @Override
    public void confirmID(boolean confirm) {
        if (this.contextHelper == null || this.contextHelper.certIDConfirmRunnable == null) {
            throw new DeviceRTException(ErrorCode.EMV_TRANSFER_FAILED, "certIDConfirmRunnable should not be null!");
        }
        this.contextHelper.certIDConfirmRunnable.confirmCertID(confirm);
    }

    @Override
    public void confirmEC(boolean isEC) {
        if (this.contextHelper == null || this.contextHelper.ecChoiceRunnable == null) {
            throw new DeviceRTException(ErrorCode.EMV_TRANSFER_FAILED, "ecChoiceRunnable should not be null!");
        }
        int ecChoice = 1;//use ec
        if (isEC) {
            ecChoice = 1;
        } else {
            ecChoice = 0;//online
        }
        this.contextHelper.ecChoiceRunnable.selectEC(ecChoice);
    }

    @Override
    public void confirmMessage(boolean confirm) {
        logger.debug("[confirmMessage]confirm:" + confirm + "; getMessageConfirmRunnable():" + getMessageConfirmRunnable());
        if (getMessageConfirmRunnable() != null) {
            getMessageConfirmRunnable().confirmMessage(confirm);
            setMessageConfirmRunnable(null);
            return;
        }
        if (this.contextHelper == null || this.contextHelper.messageConfirmRunnable == null) {
            throw new DeviceRTException(ErrorCode.EMV_TRANSFER_FAILED, "messageConfirmRunnable should not be null!");
        }
        this.contextHelper.messageConfirmRunnable.confirmMessage(confirm);
    }

    @Override
    public void setSelectedLanguage(String language) {
        if (this.contextHelper == null || this.contextHelper.languageChoiceRunnable == null) {
            throw new DeviceRTException(ErrorCode.EMV_TRANSFER_FAILED, "languageChoiceRunnable should not be null!");
        }
        try {
            this.contextHelper.languageChoiceRunnable.selectLanguage(language.getBytes("gbk"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setEMVTimeOut(int timeout) {
        super.setEMVTimeOut(timeout);
    }

    @Override
    public void setPIN(byte[] pinblock) {
        if (lastCardReadContainType(CardType.RFCARD) && (this.contextHelper == null || this.contextHelper.pinEntryRunnable == null)) {
            return;
        }
        if (this.contextHelper == null || this.contextHelper.pinEntryRunnable == null) {
            throw new DeviceRTException(ErrorCode.EMV_TRANSFER_FAILED, "pinEntryRunnable should not be null!");
        }
        int pinlen = -1;
        if (null != pinblock) {
            pinlen = pinblock.length;
        }
        this.contextHelper.pinEntryRunnable.inputPinEntry(pinlen, pinblock);
    }

    @Override
    public void setTransactionAmount(BigDecimal amount) {
        if (getEpRFCardRequestAmtRunnable() != null) {
            try {
                getEpRFCardRequestAmtRunnable().inputAmtResult(amount);
            } catch (Exception e) {
                e.printStackTrace();
            }
            setEpRFCardRequestAmtRunnable(null);
            return;
        }
        if (this.contextHelper == null || this.contextHelper.amtEntryRunnable == null) {
            throw new DeviceRTException(ErrorCode.EMV_TRANSFER_FAILED, "amtEntryRunnable should not be null!");
        }
        this.contextHelper.amtEntryRunnable.inputAmtEntry(amount);
    }

    @Override
    protected int getSpecifyMediaType() {
        ModuleType type = contextHelper.getDefaultModuleType();
        if (type != null) {
            if (type == ModuleType.COMMON_CARDREADER)
                return EMVTransContext._EMV_MEDIATYPE_ICCARD;
            else if (type == ModuleType.RFCARDREADER)
                return EMVTransContext._EMV_MEDIATYPE_RFCARD;
        }
        // 若本地找不到对应的介质，则使用超类方式寻找
        return super.getSpecifyMediaType();
    }

    @Override
    protected MEEMVLevel2 getEmvModule() {
        return (MEEMVLevel2) getOwner().getStandardModule(ModuleType.EMV);
    }

    void refreshEmvTransInfo() {
        getEmvModule().refreshEmvTransferInfo(getEmvTransInfo());
    }

    @Override
    protected void beforeEmvStart(boolean isRfCard, TransactionExtParams extParams) {
        logger.debug("[beforeEmvStart] SUSPEND isRfCard:" + isRfCard);
        if (null != extParams && null != extParams.getEpOpt() && extParams.getEpOpt().emSeqStart == EmvConst.EntryPointSeq.START_FINAL_APP) {
            logger.debug("second tap not SUSPEND");
            return;
        }
        if (isRfCard && !getContext().isNDKEMVProcess()) {
            getEmvModule().doEntrypointSuspend(0);
        } else {
            if(extParams != null && extParams.isEnablePreParam()){
                logger.debug("[beforeEmvStart] EnablePreParam");
            }else {
                logger.debug("[beforeEmvStart] EmvJNIService->jniemvSuspend(0)");
                getEmvModule().emvcore.jniemvSuspend(0);
            }
            if (!getHasSecModule()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
                byte[] time = InnerUtils.hex2byte(sdf.format(new Date()));
                logger.debug("[beforeEmvStart] jniemvWriteNLTagData(0x0014, " + InnerUtils.hexString(time) + ")");
                getEmvModule().emvcore.jniemvWriteNLTagData(0x0014, time, time.length);
            }
        }
    }

    private String getSysProperty(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method method = c.getMethod("get", String.class);
            value = (String) (method.invoke(c, key));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
    private boolean getHasSecModule(){
        boolean hasSecModule = true;
        if(getSysProperty("persist.sys.HasSecModule","yes").equals("no")){
            hasSecModule = false;
        }
        logger.debug(">>>hasSecModule="+hasSecModule);
        return hasSecModule;
    }

    @Override
    protected void innerContextInit(EMVTransContext context) {
    }

    protected int fetchOfflinePwdCount() {
        byte[] pwdCount = new byte[1];
        int[] dataLength = new int[1];
        int result = getEmvModule().emvcore.jniemvICCGetDataByTagName(0x9F17, pwdCount, dataLength);
        logger.debug("[fetchOfflinePwdCount] EmvJNIService->jniemvICCGetDataByTagName 0x9F17 result=" + result + " pwdCount=" + pwdCount[0]);
        if (result == 0) {
            return pwdCount[0];
        }
        return -1;
    }


    @Override
    public byte[] getEmvICCData(int tag) {
        byte[] temp = new byte[15];
        int[] lentemp = new int[1];
        int rs = getEmvModule().emvcore.jniemvICCGetDataByTagName(tag, temp, lentemp);
        logger.debug("[fetchOfflinePwdCount] EmvJNIService->jniemvICCGetDataByTagName tag=" + tag + " ret=" + rs + " len=" + lentemp + " result=" + InnerUtils.hexString(temp));
        if (rs == 0) {
            int length = lentemp[0];
            byte[] sc = new byte[length];
            System.arraycopy(temp, 0, sc, 0, length);
            return sc;
        }
        return null;
    }

    @Override
    public void setInnerEmvData(int tag, byte[] data) {
        logger.debug("[setInnerEmvData] EmvJNIService->jniemvsetdata tag=" + tag + " data=" + InnerUtils.hexString(data));
        getEmvModule().emvcore.jniemvsetdata(tag, data, data.length);
    }

    @Override
    public void writeNLTagData(int tag, byte[] data, int dataLength) {
        logger.debug("[writeNLTagData] EmvJNIService->jniemvWriteNLTagData tag=" + tag + " data=" + InnerUtils.hexString(data) + " dataLength=" + dataLength);
        getEmvModule().emvcore.jniemvWriteNLTagData(tag, data, dataLength);
    }

    @Override
    public void confirmInformation(boolean confirmed) {
        logger.debug("[confirmInformation]isNDKEMVProcess:" + getContext().isNDKEMVProcess() + "; hasDoneNDKEMV:" + hasDoneNDKEMV());
        if (getContext().isNDKEMVProcess() && hasDoneNDKEMV() && this.contextHelper != null && this.contextHelper.finalSelectRunnable != null) {
            this.contextHelper.finalSelectRunnable.inputFinalSelect(0, new byte[]{});
            return;
        }
        super.confirmInformation(confirmed);
    }


}
