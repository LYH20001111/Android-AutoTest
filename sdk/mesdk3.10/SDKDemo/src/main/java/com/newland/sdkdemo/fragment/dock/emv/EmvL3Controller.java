package com.newland.sdkdemo.fragment.dock.emv;

import static com.newland.sdkdemo.AppConfig.isSimpleFlow;

import android.content.Context;
import android.util.Log;

import com.newland.industryic.BytesUtil;
import com.newland.sdk.DeviceController;

import com.newland.sdk.inter.externalpin.AlgorithmMode;
import com.newland.sdk.inter.externalpin.KeyManagement;
import com.newland.sdk.inter.externalpin.PinInputListener;
import com.newland.sdk.module.emvl3.EMVL3Module;
import com.newland.sdk.module.emvl3.TransactionResult;
import com.newland.sdk.module.emvl3.common.EmvL3Const;
import com.newland.sdk.module.emvl3.common.ErrorCode;
import com.newland.sdk.module.emvl3.utils.EMVInnerUtils;
import com.newland.sdk.pinpad.utils.ISOUtils;
import com.newland.sdk.pinpad.utils.LoggerUtil;
import com.newland.sdk.pinpad.utils.tlv.TLVPackage;
import com.newland.sdkdemo.AppConfig;
import com.newland.sdkdemo.MainActivity;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.utils.MessageTag;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Author by wuhh, Date on 2020/3/18.
 */
public class EmvL3Controller {
    private static final String TAG = "EmvL3Controller";
    private Context mContext;
    private EMVL3Module mEmvL3Module;
    private TransParam transParam;
    private TransResult transResult;
    private List<Method> emvStepList = new ArrayList<>();
    private long beginTime;
    private static int nextStep;

    public EmvL3Controller(Context context, EMVL3Module emvL3Module, TransParam transParam) {
        mContext = context;
        this.mEmvL3Module = emvL3Module;
        this.transParam = transParam;
        transResult = new TransResult();
        transResult.setSignature(false);
        transResult.setTransResultCode(EmvL3TransConstant.TRANS_FAIL);
        this.findEmvL3Step();
    }

    public void startTransaction(final TransResultListener listener) {
        new Thread(() -> {
            try {
                int stepIndex = EmvL3TransConstant.TransStep.INPUT_AMOUNT;
                while (true) {
                    Method method = getEmvStep(stepIndex);
                    if (method == null) {
                        break;
                    }
                    method.setAccessible(true);
                    Object obj = method.invoke(EmvL3Controller.this);
                    stepIndex = Integer.valueOf(obj.toString());
                }
                terminateTransaction();
                if (stepIndex >= 0) {
                    listener.onSuccess();
                } else {
                    listener.onFail("Transaction failure.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void findEmvL3Step() {
        Class<?> clazz = this.getClass();
        Method[] methods = clazz.getDeclaredMethods();

        for (Method method : methods) {
            EmvL3StepAnno step = method.getAnnotation(EmvL3StepAnno.class);
            if (step != null) {
                emvStepList.add(method);
            }
        }
    }

    private Method getEmvStep(int index) {
        for (Method method : emvStepList) {
            EmvL3StepAnno stepAnno = method.getAnnotation(EmvL3StepAnno.class);
            if (stepAnno.index() == index) {
                return method;
            }
        }
        return null;
    }

    @EmvL3StepAnno(index = EmvL3TransConstant.TransStep.INPUT_AMOUNT)
    private int inputAmount() {
        showMessage("TransStep:inputAmount", MessageTag.TIP);

        if (AppConfig.EMV.amt != null) {
            long amount = AppConfig.EMV.amt.setScale(2, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).toBigInteger().longValue();
            transParam.setAmount(amount);
        } else {
            transParam.setAmount(0);
        }
        transParam.setAmountOther(0);
        return EmvL3TransConstant.TransStep.TRANS_PERFORMED;
    }

    @EmvL3StepAnno(index = EmvL3TransConstant.TransStep.TRANS_PERFORMED)
    private int performTransaction() {
        beginTime = System.currentTimeMillis();
        showMessage("TransStep:performTransaction:" + System.currentTimeMillis(), MessageTag.TIP);

        TLVPackage tlvpackage = EMVInnerUtils.newTlvPackage();
        String tmp = String.format("%012d", transParam.getAmount());
        tlvpackage.append(0x9F02, ISOUtils.hex2byte(tmp));

        tmp = String.format("%012d", transParam.getAmountOther());
        tlvpackage.append(0x9F03, ISOUtils.hex2byte(tmp));

        tlvpackage.append(0x9C, transParam.getTransType() + "");

        String date = (new SimpleDateFormat("yyyyMMdd")).format(new Date());
        String time = (new SimpleDateFormat("HHmmss")).format(new Date());
        tlvpackage.append(0x9A, ISOUtils.hex2byte(date));
        tlvpackage.append(0x9F21, ISOUtils.hex2byte(time));

        tlvpackage.append(0x1F8126, 0x01 + "");
//        tlvpackage.append(0x1F8121, new byte[]{0x04});
//        tlvpackage.append(0x1F8164, ISOUtils.hex2byte("C0"));
        // pin回调
//        byte callBackFlag = (byte) (EmvL3Const.CALLBACK_ENABLE_GET_PIN);
//        tlvpackage.append(0x1F8139, new byte[]{callBackFlag, 0x00, 0x00});

        if (isSimpleFlow) {
            tlvpackage.append(0x1F8128, new byte[]{0x01}); // 简易流程
        } else {
            tlvpackage.append(0x1F8128, new byte[]{0x00}); // 默认0x00
        }

        if (transParam != null && transParam.getCurrentCardInterfaces() != null) {
            LoggerUtil.debug(TAG, "[startEMV] transactionExtParams.getCurrentCardInterfaces:" + transParam.getCurrentCardInterfaces());
            //Current card interface,如果先调用上电，再调用startemv，需要设置当前卡类型
            tlvpackage.append(0x1F8121, new byte[]{(byte) (transParam.getCurrentCardInterfaces().intValue())});
        }
        byte[] data = tlvpackage.pack();
        byte[] transData = new byte[5 + data.length];
        int cardInputMode = transParam.getCardInputMode();

        transData[0] = (byte) cardInputMode;
        System.arraycopy(BytesUtil.intToBytes(60, 4), 0, transData, 1, 4);
        System.arraycopy(data, 0, transData, 5, data.length);

        LoggerUtil.debug(TAG, ">>>performTransaction transData:" + ISOUtils.hexString(transData));
        showMessage("PerformTransaction Step transData:" + ISOUtils.hexString(transData), MessageTag.DATA);
        TransactionResult result = mEmvL3Module.performTransaction(transData);
        LoggerUtil.debug(TAG, ">>>performTransaction result:" + result);

        if (!isSimpleFlow) {
            if (result == null) {
                return EmvL3TransConstant.TRANS_FAIL;
            }
        }
        int resultCode = result.getResultCode();
        int errCode = result.getErrorCode();
        int cvmStatus = result.getCvmStatus();
        byte[] tlvData = result.getTlvData();

        if (mEmvL3Module.isSignature()) {
            transResult.setSignature(true);
        }

        transResult.setTransResultCode(resultCode);
        String info = "PerformTransaction ErrCode=" + errCode + " ResultCode=" + resultCode + " CVM=" + cvmStatus;
        String tlv = (tlvData == null ? "null" : ISOUtils.hexString(tlvData));
        LoggerUtil.debug(TAG, info);
        LoggerUtil.debug(TAG, "tlvData:" + tlv);
        showMessage(info, MessageTag.DATA);
        showMessage(tlv, MessageTag.DATA);
        nextStep = EmvL3TransConstant.TransStep.TRANS_RESULT;
        switch (resultCode) {
            case EmvL3Const.TransResult.L3_TXN_DECLINE:
                LoggerUtil.debug(TAG, ">>>DECLINE");
                showMessage("PerformTransaction:OffLine DECLINE", MessageTag.WARN);
                nextStep = EmvL3TransConstant.TransStep.TRANS_RESULT;
                break;
            case EmvL3Const.TransResult.L3_TXN_APPROVED:
                LoggerUtil.debug(TAG, ">>>APPROVED");
                showMessage("PerformTransaction:OffLine APPROVED", MessageTag.WARN);
                nextStep = EmvL3TransConstant.TransStep.TRANS_RESULT;
                break;
            case EmvL3Const.TransResult.L3_TXN_ONLINE:
                showMessage("PerformTransaction:ONLINE", MessageTag.WARN);
                nextStep = EmvL3TransConstant.TransStep.TRANS_ONLINE;
                break;
            default:
                showMessage("PerformTransaction:errorCode=" + errCode, MessageTag.WARN);
                if (errCode == ErrorCode.L3_ERR_SUCC) {
                    getTlv();
                    return nextStep = EmvL3TransConstant.TransStep.TRANS_RESULT;
                } else if (errCode == ErrorCode.L3_ERR_TIMEOUT) {
                    showMessage("PerformTransaction:Timeout");
                    return nextStep = EmvL3TransConstant.TRANS_FAIL;

                } else if (errCode == ErrorCode.L3_ERR_COLLISION) {
                    showMessage("PerformTransaction:Present One Card Only");
                    return nextStep = EmvL3TransConstant.TRANS_FAIL;
                } else if (errCode == ErrorCode.L3_ERR_CANCEL) {
                    showMessage("PerformTransaction:Cancel");
                    return nextStep = EmvL3TransConstant.TRANS_FAIL;
                } else {
                    showMessage("PerformTransaction:default failed.");
                    return nextStep = EmvL3TransConstant.TRANS_FAIL;
                }

        }
        LoggerUtil.info(TAG, "result.getCvmStatus():" + result.getCvmStatus());
//        setTlv();


        byte[] entryMODE = mEmvL3Module.getData(EmvL3Const.L3_DATA.POS_ENTRY_MODE);
        if (entryMODE != null && Arrays.equals(new byte[]{0x05}, entryMODE)) {//插卡

        } else if (entryMODE != null && Arrays.equals(new byte[]{0x07}, entryMODE)) {//非接

        } else if (entryMODE != null && Arrays.equals(new byte[]{0x02}, entryMODE)) {//刷卡
            byte[] track1 = mEmvL3Module.getData(EmvL3Const.L3_DATA.TRACK1);
            byte[] track2 = mEmvL3Module.getData(EmvL3Const.L3_DATA.TRACK2);
            byte[] track3 = mEmvL3Module.getData(EmvL3Const.L3_DATA.TRACK3);
            showMessage("一磁道数据：" + (track1 == null ? null : new String(track1)));
            showMessage("二磁道数据：" + (track2 == null ? null : new String(track2)));
            showMessage("三磁道数据：" + (track3 == null ? null : new String(track3)));

        }
        LoggerUtil.error(TAG, "POS_ENTRY_MODE:" + (entryMODE == null ? null : ISOUtils.hexString(entryMODE)));

        //        TODO 非接根据CVM判断走脱机pin还是联机pin可以等整个emv流程结束后再继续输密码操作
        if (nextStep == EmvL3TransConstant.TransStep.TRANS_ONLINE && result.getCvmStatus() == EmvL3Const.CVMStatus.OP_ONLINE_PIN && (entryMODE != null && Arrays.equals(new byte[]{0x07}, entryMODE))) {
            // 联机pin输入
            nextStep = EmvL3TransConstant.TransStep.TRANS_ONLINE_PIN;
        } else {
            // 其他pin类型操作 如不输密直接联机处理
            nextStep = EmvL3TransConstant.TransStep.TRANS_ONLINE;
        }

        return nextStep;

    }

    //TODO Online Pin
    @EmvL3StepAnno(index = EmvL3TransConstant.TransStep.TRANS_ONLINE_PIN)
    private int onlinePin() {
        return doOnlinePin();
    }

    // TODO no pin
    @EmvL3StepAnno(index = EmvL3TransConstant.TransStep.TRANS_NO_PIN)
    private int noPin() {
        return EmvL3TransConstant.TransStep.TRANS_ONLINE;
    }

    //TODO Online Process
    @EmvL3StepAnno(index = EmvL3TransConstant.TransStep.TRANS_ONLINE)
    private int onlineProcess() {
        long millsecond = System.currentTimeMillis() - beginTime;
        showMessage("TransStep:onlineProcess:" + millsecond + "ms", MessageTag.TIP);
        /**
         *  build 8583 package, send to the backend host for online authorization
         *  The back-end host may respond back with a result or there may be a timeout or a network error.
         *  In any case, we should return EmvL3TransConstant.TransStep.TRANS_COMPLETE;
         */
        LoggerUtil.debug(TAG, ">>>Online Process");

        //Unable go online, for test
        transResult.setOnlineResult(true);//根据实际联机结果设置

        if (transResult.getOnlineResult()) {
            //TODO Get Field 39 and Field 55 from the response data
            transResult.setResponseCode(new byte[]{0x30, 0x30});//39域授权响应码
            transResult.setIsoField55(null);//55域数据
        }
        return EmvL3TransConstant.TransStep.TRANS_COMPLETE;
    }

    @EmvL3StepAnno(index = EmvL3TransConstant.TransStep.TRANS_COMPLETE)
    private int completeTransaction() {
        showMessage("TransStep:completeTransaction", MessageTag.TIP);
        byte[] transData = null;

        boolean onlineResult = transResult.getOnlineResult();
        LoggerUtil.debug(TAG, "onlineResult:" + onlineResult);
        if (onlineResult) {
            TLVPackage tlvpackage = EMVInnerUtils.newTlvPackage();
            tlvpackage.append(0x8A, transResult.getResponseCode());
            //TODO get 91/71/72 from field-55
//			pack.append(0x91, new byte[]{0x00});
//			pack.append(0x71, new byte[]{0x00});
//			pack.append(0x72, new byte[]{0x00});
            byte[] data = tlvpackage.pack();
            transData = new byte[1 + data.length];
            transData[0] = 1;
            System.arraycopy(data, 0, transData, 1, data.length);

        } else { // unable go online
            transData = new byte[1];
            transData[0] = 0;
        }
        LoggerUtil.debug(TAG, "CompleteTransaction transData:" + (null == transData ? null : ISOUtils.hexString(transData)));
        showMessage("CompleteTransaction transData:" + (null == transData ? null : ISOUtils.hexString(transData)), MessageTag.DATA);
        TransactionResult result = mEmvL3Module.completeTransaction(transData);
        if (result == null) {
            showMessage("result==null");
            return EmvL3TransConstant.TRANS_FAIL;
        }
        int resultCode = result.getResultCode();
        int errCode = result.getErrorCode();

        if (mEmvL3Module.isSignature()) {
            transResult.setSignature(true);
        }
        transResult.setTransResultCode(resultCode);
        String codeInfo = "CompleteTranaction ErrCode=" + errCode + " ResultCode=" + resultCode;
        LoggerUtil.debug(TAG, codeInfo);
        showMessage(codeInfo, MessageTag.DATA);
        return EmvL3TransConstant.TransStep.TRANS_RESULT;
    }

    @EmvL3StepAnno(index = EmvL3TransConstant.TransStep.TRANS_RESULT)
    private int resultTransaction() {
        showMessage("TransStep:resultTransaction", MessageTag.TIP);
        int resultCode = transResult.getTransResultCode();
        if (isSimpleFlow) {
            if (resultCode == EmvL3Const.TransResult.L3_TXN_OK) {
                return EmvL3TransConstant.TRANS_SUCC;
            }
        } else {
            if (resultCode == EmvL3Const.TransResult.L3_TXN_APPROVED) {
                return EmvL3TransConstant.TRANS_SUCC;
            } else {
                return EmvL3TransConstant.TRANS_FAIL;
            }
        }
        return EmvL3TransConstant.TRANS_FAIL;
    }

    private boolean terminateTransaction() {
        showMessage("TransStep:terminateTransaction", MessageTag.TIP);
        return mEmvL3Module.terminateTransaction();
    }

    private void showMessage(final String mess, final int messageType) {
        ((MainActivity) mContext).showMessage(mess, messageType);
    }

    private void showMessage(final String mess) {
        ((MainActivity) mContext).showMessage(mess, MessageTag.DATA);
    }

    //for test
    public void getTlv() {
        byte[] tagDF24 = mEmvL3Module.getData(0xDF24);
        showMessage(">>>getData DF24=" + (tagDF24 == null ? "null" : ISOUtils.hexString(tagDF24)));
        Log.d(TAG, ">>>getData DF24=" + (tagDF24 == null ? "null" : ISOUtils.hexString(tagDF24)));

        byte[] tag5A = mEmvL3Module.getData(0x5A);
        showMessage(">>>getData 5A=" + (tagDF24 == null ? "null" : ISOUtils.hexString(tag5A)));

        byte[] tag57 = mEmvL3Module.getData(0x57);
        showMessage(">>>getData 57=" + (tagDF24 == null ? "null" : ISOUtils.hexString(tag57)));
        ArrayList taglist = new ArrayList<>();
        taglist.add(0x9f26);
        taglist.add(0x9F10);
        taglist.add(0x9F27);
        taglist.add(0x9F37);
        taglist.add(0x9F36);
        taglist.add(0x95);
        taglist.add(0x9A);
        taglist.add(0x9C);
        taglist.add(0x9F02);
        taglist.add(0x5F2A);
        taglist.add(0x82);
        taglist.add(0x9F1A);
        taglist.add(0x9F03);
        taglist.add(0x9F33);
        taglist.add(0x9F34);
        taglist.add(0x9F35);
        taglist.add(0x9F1E);
        taglist.add(0x84);
        taglist.add(0x9F09);
        taglist.add(0x9F41);
        taglist.add(0x8a);
        taglist.add(0x9f63);
        taglist.add(0x50);
        taglist.add(0x4f);
        taglist.add(0x9f12);
        taglist.add(0x9B);
        byte[] tlvData = mEmvL3Module.getTlvData(taglist, true);
        Log.d(TAG, "getTlvData tlvData=" + ISOUtils.hexString(tlvData));
    }

    //for test
    public void setTlv() {
        boolean isSucc = mEmvL3Module.setData(0xDF24, ISOUtils.hex2byte("07F4C0F0E8EF0E60"));
        showMessage(">>>setData isSucc=" + isSucc);
        Log.d(TAG, "setData isSucc=" + isSucc);

        isSucc = mEmvL3Module.setTLVData(ISOUtils.hex2byte("DF2407F4C0F0E8EF0E60DF010101DF13050010000000"));
        Log.d(TAG, "setTLVData isSucc=" + isSucc);
        showMessage(">>>setTLVData isSucc=" + isSucc);
    }

    private AppConfig.PinInputResult pinInputResult;
    private byte[] pinBlock;

    public int doOnlinePin() {
        try {
            String content = "Enter online pin:";
            showMessage(content, MessageTag.NORMAL);
            String accNo = "6225760008219599";
            KeyManagement keyManagement = KeyManagement.MKSK;
            AlgorithmMode algorithmMode = AlgorithmMode.DES;
            int pinkeyIndex = AppConfig.Pin.MKSK_SM4_INDEX_WK_PIN;
            if (AppConfig.MKSK_SM4.equals(AppConfig.KEY_SYS_ALG)) {
                algorithmMode = AlgorithmMode.SM4;
                keyManagement = KeyManagement.MKSK;
                pinkeyIndex = AppConfig.Pin.MKSK_SM4_INDEX_WK_PIN;
            } else if (AppConfig.MKSK_DES.equals(AppConfig.KEY_SYS_ALG)) {
                algorithmMode = AlgorithmMode.DES;
                keyManagement = KeyManagement.MKSK;
                pinkeyIndex = AppConfig.Pin.MKSK_DES_INDEX_WK_PIN;
            } else if (AppConfig.DUKPT_DES.equals(AppConfig.KEY_SYS_ALG)) {
                algorithmMode = AlgorithmMode.DES;
                keyManagement = KeyManagement.DUKPT;
                pinkeyIndex = AppConfig.Pin.DUKPT_DES_INDEX;
            }
            DeviceController.getInstance().getExtPinpadModule().startExternalPinInput(keyManagement, algorithmMode, pinkeyIndex, accNo, 60, new PinInputListener() {
                @Override
                public void onKeyPress() {
                    showMessage("Click  key", MessageTag.NORMAL);
                }

                @Override
                public void onBackspace() {
                    showMessage("Click backspace key", MessageTag.NORMAL);

                }

                @Override
                public void onCancel() {
                    showMessage(mContext.getString(R.string.msg_ext_pininput_cancel), MessageTag.NORMAL);
                    pinInputResult = AppConfig.PinInputResult.CANCEL;
                    resumeStep();
                }

                @Override
                public void onFinish(int pinblockLen, byte[] pinblock, byte[] ksn) {
                    showMessage("pinblockLen:" + pinblockLen);
                    if (pinblockLen == 0) {
                        showMessage(mContext.getString(R.string.msg_ext_pininput_confirm), MessageTag.NORMAL);
                        pinInputResult = AppConfig.PinInputResult.BYPASS;
                    } else {
                        pinInputResult = AppConfig.PinInputResult.SUCCESS;
                        showMessage(mContext.getString(R.string.msg_ext_pininput_confirm_result) + (pinblock == null ? "null" : ISOUtils.hexString(pinblock)), MessageTag.NORMAL);
                        showMessage("ksn:" + (ksn == null ? null : ISOUtils.hexString(ksn)));
                    }
                    resumeStep();
                }

                @Override
                public void onTimeout() {
                    showMessage(mContext.getString(R.string.msg_ext_pininput_input_password_exception_code) + "time out", MessageTag.ERROR);
                    pinInputResult = AppConfig.PinInputResult.TIME_OUT;
                    resumeStep();
                }

                @Override
                public void onError(int errorCode, String message) {
                    showMessage(mContext.getString(R.string.msg_ext_pininput_input_password_exception_code) + message, MessageTag.ERROR);
                    pinInputResult = AppConfig.PinInputResult.FAIL;
                    resumeStep();
                }

            }, null);

        } catch (Exception e) {
            e.printStackTrace();
            showMessage(mContext.getString(R.string.msg_ext_pininput_input_password_exception) + e.getMessage(), MessageTag.ERROR);
            return EmvL3TransConstant.TRANS_FAIL;
        }
        pauseStep();
        switch (pinInputResult) {
            case SUCCESS:
                showMessage("PinEntry SUCCESS", MessageTag.DATA);
                return EmvL3TransConstant.TransStep.TRANS_ONLINE;
            case FAIL:
                showMessage("PinEntry FAIL", MessageTag.DATA);
                return EmvL3TransConstant.TRANS_FAIL;
            case CANCEL:
                showMessage("PinEntry CANCEL", MessageTag.DATA);
                return EmvL3TransConstant.TRANS_FAIL;
            case TIME_OUT:
                showMessage("PinEntry TIME_OUT", MessageTag.DATA);
                return EmvL3TransConstant.TRANS_FAIL;
            case BYPASS:
                showMessage("PinEntry BYPASS", MessageTag.DATA);
                return EmvL3TransConstant.TransStep.TRANS_ONLINE;
            default:
                return EmvL3TransConstant.TRANS_FAIL;
        }
    }

    private Object waitStep = new Object();

    private void pauseStep() {
        synchronized (waitStep) {
            try {
                waitStep.wait(60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void resumeStep() {
        synchronized (waitStep) {
            waitStep.notify();
        }
    }
}

