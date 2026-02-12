package com.newland.nsdk.core.internal.pinentry;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.keymanager.AsymKeyType;
import com.newland.nsdk.core.api.common.keymanager.AsymmetricKey;
import com.newland.nsdk.core.api.common.keymanager.DUKPTDerivateKey;
import com.newland.nsdk.core.api.common.keymanager.DUKPTDerivateUsage;
import com.newland.nsdk.core.api.common.keymanager.Key;
import com.newland.nsdk.core.api.common.keymanager.KeyUsage;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.internal.devicemanager.DeviceInfo;
import com.newland.nsdk.core.api.internal.exception.NSDKNDKException;
import com.newland.nsdk.core.api.internal.pinentry.ExtendedEventInfo;
import com.newland.nsdk.core.api.internal.pinentry.PINEntry2Listener;
import com.newland.nsdk.core.api.internal.pinentry.ExtendedEvent;
import com.newland.nsdk.core.api.internal.pinentry.KeyboardParameters;
import com.newland.nsdk.core.api.internal.pinentry.PINCustomizedAction;
import com.newland.nsdk.core.api.internal.pinentry.PINEntry2Parameters;
import com.newland.nsdk.core.api.internal.pinentry.PINEntry2;
import com.newland.nsdk.core.api.internal.pinentry.PINPadButton;
import com.newland.nsdk.core.api.internal.pinentry.RSAKey;
import com.newland.nsdk.core.api.internal.pinentry.SetMode;
import com.newland.nsdk.core.api.internal.pinentry.TouchState;
import com.newland.nsdk.core.common.NSDKExecutors;
import com.newland.nsdk.core.internal.crypto.ST_SEC_DUKPT_DERIVATE_DATA;
import com.newland.nsdk.core.internal.jni.NSDKJni;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PINEntry2Impl implements PINEntry2 {

    private static final String TAG = "PINEntry2Impl";
    private static final int SYS_EVENT_NONE = 0;
    private static final int SYS_EVENT_PIN = 0x00000020;
    private static final int SYS_EVENT_ICCARD = 0x00000008;

    private static volatile PINEntry2Impl instance;
    private boolean isSupported;
    private DeviceInfo deviceInfo;
    private volatile boolean isStartPINEntry = false;
    private Object pinEvent = new Object();
    private Object pinKeyListObj = new Object();
    private int pinRangeCheckCode = 0x00;
    private int count = 0;
    private LinkedList<Integer> pinKeyList = new LinkedList<Integer>();

    public static PINEntry2Impl getInstance(boolean isSupported, DeviceInfo deviceInfo) {
        if (instance == null) {
            synchronized (PINEntry2Impl.class) {
                if (instance == null || instance.isSupported != isSupported || instance.deviceInfo != deviceInfo) {
                    instance = new PINEntry2Impl(isSupported, deviceInfo);
                }
            }
        } else {
            if (instance.isSupported != isSupported || instance.deviceInfo != deviceInfo) {
                instance = new PINEntry2Impl(isSupported, deviceInfo);
            }
        }
        return instance;
    }

    private PINEntry2Impl(boolean isSupported, DeviceInfo deviceInfo) {
        this.isSupported = isSupported;
        this.deviceInfo = deviceInfo;
    }

    private void isSupported() throws NSDKException {
        if (!isSupported) {
            throw new NSDKException(ErrorCode.UNSUPPORTED, "UnSupported PINEntry2 Module");
        }
    }

    @Override
    public byte[] initKeyLayout(Map<PINPadButton, int[]> pinPadButtonMap, int[] screenArea, int[] pinpadArea, KeyboardParameters keyboardParameters) throws NSDKException {
        isSupported();

        // 参数校验
        if (pinPadButtonMap == null || screenArea == null || pinpadArea == null || keyboardParameters == null || screenArea.length == 0 || pinpadArea.length == 0) {
            throw new NSDKIllegalParameterException("The pinPadButtonMap, screenArea, pinpadArea or keyboardParameters shall not be null.");
        }

        if (pinPadButtonMap.size() < 10) {
            throw new NSDKIllegalParameterException("PinPadButtonMap must include all number buttons (0-9).");
        }

        if (screenArea.length != 2 && screenArea.length != 4) {
            throw new NSDKIllegalParameterException("Screen Area shall contains left-top and right-bottom which required 4 certain coordination value, or 2 bytes of the right-bottom coordinates.");
        }

        if (pinpadArea.length != 4) {
            throw new NSDKIllegalParameterException("Pinpad Area shall contains left-top and right-bottom which required 4 certain coordination value.");
        }

        if (screenArea.length == 2) {
            int[] rightBottom = new int[2];
            System.arraycopy(screenArea, 0, rightBottom, 0, 2);
            screenArea = new int[4];
            screenArea[0] = 0;
            screenArea[1] = 0;
            screenArea[2] = rightBottom[0];
            screenArea[3] = rightBottom[1];
        }


        int buttonCount = pinPadButtonMap.size();
        int[] keyValues = new int[buttonCount];
        int[] buttonsCoordination = new int[4 * buttonCount];
        int validButtonCount = 0;

        // 填充键值和坐标数据
        // 这里的数字键需要按顺序传入，否则会出现键值对不上的情况。
        // 在该段代码中，PINPadButton.values()已经帮我们按照PINPadButton本身定义的顺序做好了排序
        // 后续如果有新加的键值定义，请加在末尾，否则会影响此处的逻辑。
        for (PINPadButton button : PINPadButton.values()) {
            int[] rect = pinPadButtonMap.get(button);

            if (rect == null || rect.length != 4) {
                LogUtils.w("initKeyLayout", "Button " + button + " not found or has invalid coordinates");
                continue; // 跳过缺失的按钮
            }

            // 验证矩形有效性
            if (rect[0] >= rect[2] || rect[1] >= rect[3]) {
                LogUtils.w("initKeyLayout", "Invalid rectangle for button: " + button);
                continue; // 跳过无效的坐标
            }

            // 映射按键到键值
            keyValues[validButtonCount] = mapButtonToKeyValue(button);

            // 填充坐标数据
            int offset = validButtonCount * 4;
            buttonsCoordination[offset] = rect[0];     // left
            buttonsCoordination[offset + 1] = rect[1]; // top
            buttonsCoordination[offset + 2] = rect[2]; // right
            buttonsCoordination[offset + 3] = rect[3]; // bottom

            validButtonCount++;

        }

        // 如果有效按钮数量不一致，记录警告
        if (validButtonCount != buttonCount) {
            LogUtils.w("initKeyLayout", "Only " + validButtonCount + " valid buttons of " + buttonCount + " were processed");
        }

        // 初始化键盘（使用实际有效的按钮数量）
        int ret = NSDKJni.getInstance().NAPI_SecVPPAAInit(keyValues, buttonsCoordination, validButtonCount,
                screenArea, pinpadArea, keyboardParameters);

        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to init key layout, ret = %d", ret));
        }

        // 重置键盘映射
        ret = NSDKJni.getInstance().NAPI_SecVPPAASetMap(null, null, 0, SetMode.SET_DEFAULT.ordinal());

        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to reset key map, error code: %d", ret));
        }

        // 重置PIN范围检查码
        pinRangeCheckCode = 0x00;

        // 返回乱序后的数组（仅数字键）
        if (Boolean.TRUE.equals(keyboardParameters.isRandomPinpad())) {
            byte[] numberSequence = new byte[10];
            for (int i = 0; i < 10; i++) {
                numberSequence[i] = (byte) keyValues[i];
            }
            return numberSequence;
        } else {
            return null;
        }
    }

    @Override
    public void setCustomizedActions(Map<ExtendedEvent, PINCustomizedAction> eventActionsMap) throws NSDKException {
        isSupported();

        // 1. 参数校验
        if (eventActionsMap == null || eventActionsMap.isEmpty()) {
            throw new NSDKIllegalParameterException("The eventActionsMap shall not be null.");
        }

        // 3. 新增事件-行为映射
        List<ExtendedEvent> events = new ArrayList<>(eventActionsMap.keySet());
        int count = events.size();
        int[] eventValues = new int[count];
        int[] actionValues = new int[count];
        int index = 0;

        for (Map.Entry<ExtendedEvent, PINCustomizedAction> entry : eventActionsMap.entrySet()) {
            eventValues[index] = entry.getKey().ordinal();
            actionValues[index] = entry.getValue().ordinal();
            index++;
        }

        int ret = NSDKJni.getInstance().NAPI_SecVPPAASetMap(eventValues, actionValues, count, SetMode.RESET.ordinal());

        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, "Failed to set event action map");
        }

        PINCustomizedAction tooLongAction = eventActionsMap.get(ExtendedEvent.TOO_LONG);
        PINCustomizedAction tooShortAction = eventActionsMap.get(ExtendedEvent.TOO_SHORT);

        // 检查两个事件是否同时设置
        if (tooLongAction != null && tooShortAction != null) {
            // 检查两个事件的行为是否一致
            if (tooLongAction != tooShortAction) {
                throw new NSDKIllegalParameterException("TOO_LONG and TOO_SHORT events must have the same action");
            }

            // 根据行为更新 PIN 范围检查代码
            switch (tooLongAction) {
                case IGNORE:
                    pinRangeCheckCode = 0x00; // 应用不处理对应事件
                    break;
                case NONE:
                    pinRangeCheckCode = 0x40; // 通过回调通知应用，继续输 PIN
                    break;
                case ESC:
                    pinRangeCheckCode = 0x80; // 通过回调通知应用，并退出流程
                    break;
                default:
                    throw new NSDKIllegalParameterException("Invalid action for TOO_LONG/TOO_SHORT events: " + tooLongAction);
            }
        } else if (tooLongAction != null || tooShortAction != null) {
            // 如果只设置了其中一个事件
            throw new NSDKIllegalParameterException("Both TOO_LONG and TOO_SHORT events must be set together");
        }
    }

    @Override
    public void startOnlinePINEntry(Key key, String pan, int timeout, final PINEntry2Parameters parameters, final PINEntry2Listener listener) throws NSDKException {
        isSupported();

        if (key == null || parameters == null || parameters.getPINBlockMode() == null || listener == null) {
            throw new NSDKIllegalParameterException("Key, PIN block mode, listener shall not be null.");
        }

        if (timeout <= 0) {
            throw new NSDKIllegalParameterException("Timeout shall be >0");
        }

        if (isStartPINEntry) {
            throw new NSDKException(ErrorCode.ERROR, "PIN entry is busy.");
        }

        int pinSessionTypeCode;
        int keyTypeCode;
        ST_SEC_DUKPT_DERIVATE_DATA dukptDerivateData = null;
        int icPresentCode = 0;
        if (parameters.isCheckIcPresent()) {
            icPresentCode = (1 << 8);
        }

        if (key instanceof SymmetricKey) {
            SymmetricKey tempSymmKey = (SymmetricKey) key;
            if (tempSymmKey.getKeyUsage() == KeyUsage.DUKPT) {
                pinSessionTypeCode = PINSessionType.DUKPT.getCode() | icPresentCode;
            } else {
                pinSessionTypeCode = PINSessionType.MASTER_SESSION.getCode() | icPresentCode;
            }
            if (tempSymmKey.getKeyType() == null) {
                throw new NSDKIllegalParameterException("Please set key type of the key.");
            }
            keyTypeCode = tempSymmKey.getKeyType().getCode();
        } else if (key instanceof AsymmetricKey) {
            pinSessionTypeCode = PINSessionType.MASTER_SESSION.getCode();
            AsymKeyType asymKeyType = ((AsymmetricKey) key).getKeyType();
            if (asymKeyType == null) {
                throw new NSDKIllegalParameterException("Please set key type of the key.");
            }
            keyTypeCode = asymKeyType.getCode();
        } else {
            throw new NSDKIllegalParameterException("Key shall be a symmetric or asymmetric key.");
        }

        if (key instanceof DUKPTDerivateKey) {
            DUKPTDerivateKey dukptDerivateKey = (DUKPTDerivateKey) key;
            if (dukptDerivateKey.getDerivateKeyType() == null) {
                throw new NSDKIllegalParameterException("Derivate key type shall not be null.");
            }
            if (dukptDerivateKey.getDerivateUsage() == null) {
                dukptDerivateKey.setDerivateUsage(DUKPTDerivateUsage.PIN);
            }
            dukptDerivateData = new ST_SEC_DUKPT_DERIVATE_DATA();
            dukptDerivateData.setDerivateKeyType(dukptDerivateKey.getDerivateKeyType().getCode());
            dukptDerivateData.setDerivateKeyUsage(dukptDerivateKey.getDerivateUsage().ordinal());
            dukptDerivateData.setDerivateKeyLen(dukptDerivateKey.getDerivateKeyLen());
        }

        int ret = -1;
        pinKeyList.clear();

        if (dukptDerivateData != null) {
            ret = NSDKJni.getInstance().NAPI_SecVPPInit_DerivateKey(pinSessionTypeCode, keyTypeCode, key.getKeyID() & 0xFF, pan,
                    parameters.getPINBlockMode().getCode() | pinRangeCheckCode, timeout, dukptDerivateData);
        } else {
            ret = NSDKJni.getInstance().NAPI_SecVPPInit(pinSessionTypeCode, keyTypeCode, key.getKeyID() & 0xFF, pan,
                    parameters.getPINBlockMode().getCode() | pinRangeCheckCode, timeout, null, null, 0);
        }

        Map<PINPadButton, PINPadButton> customButtons = parameters.getCustomButtons();
        setCustomButtonFunc(customButtons);

        String pwdRange = "";
        if (ret == ErrorCode.OK && parameters != null) {
            pwdRange = getPwdLenRange(parameters);
            if (pwdRange == null || pwdRange.isEmpty()) {
                throw new NSDKIllegalParameterException("Max len shall be greater than min len.");
            }

            ret = NSDKJni.getInstance().NAPI_SecVPPSetExpPinLenIn(pwdRange);
        } else {
            parameters.setMaxPINLen(12);
        }
        LogUtils.d(TAG, "******* pin entry started.");
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to start online PIN input, result code = %d", ret));
        }
        isStartPINEntry = true;
        NSDKExecutors.threadStart(new Runnable() {
            @Override
            public void run() {
                count = 0;
                while (true) {
                    boolean exitGetPin = false;
                    PINOutputEvent pinOutputEvent = new PINOutputEvent();
                    int ret = 0;

                    try {
                        pinOutputEvent = getPINOutputEvent();
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (e instanceof NSDKException) {
                            ret = ((NSDKException) e).getCode();
                        } else {
                            ret = ErrorCode.ERROR;
                        }
                    }

                    LogUtils.d(TAG, "******* getPinEvent : " + pinOutputEvent.getPinKeyEvent());
                    if (ret != ErrorCode.OK) {
                        isStartPINEntry = false;
                        // -1122 is timeout error
                        if (ret == -1122) {
                            listener.onTimeout();
                        } else {
                            listener.onError(ret, "PIN entry failed.");
                        }
                        exitGetPin = true;
                    }

                    if (!exitGetPin) {
                        if (pinOutputEvent.getPinKeyEvent() != null) {
                            switch (pinOutputEvent.getPinKeyEvent()) {
                                case PIN:
                                    listener.onKeyPress();
                                    count++;
                                    LogUtils.d(TAG, "************** max len = " + parameters.getMaxPINLen() + ", count = " + count);
                                    if (count == parameters.getMaxPINLen() && parameters.isAutoComplete()) {
                                        try {
                                            setPinEvent(PINKeyEvent.ENTER);
                                        } catch (NSDKException e) {
                                            isStartPINEntry = false;
                                            e.printStackTrace();
                                            listener.onError(e.getCode(), "Failed to finish PIN input automatically.");
                                            exitGetPin = true;
                                        }
                                    }
                                    break;
                                case BACKSPACE:
                                    listener.onBackspace();
                                    if (count > 0) {
                                        count--;
                                    }
                                    break;
                                case CLEAR:
                                    listener.onClear();
                                    count = 0;
                                    break;
                                case ENTER:
                                    isStartPINEntry = false;
                                    listener.onFinish(pinOutputEvent.getPinLen(), pinOutputEvent.getPinBlock(), pinOutputEvent.getKsn());
                                    exitGetPin = true;
                                    break;
                                case ESC:
                                    isStartPINEntry = false;
                                    listener.onCancel();
                                    exitGetPin = true;
                                    break;
                                case TOO_SHORT:
                                    if (pinRangeCheckCode == 0x80) {
                                        isStartPINEntry = false;
                                        listener.onError(ErrorCode.SECVP_VPP_PIN_TOO_SHORT, "PIN too short.");
                                        exitGetPin = true;
                                    } else if (pinRangeCheckCode == 0x40) {
                                        listener.onExtendedEvent(new ExtendedEventInfo(ExtendedEvent.TOO_SHORT, null));
                                    }
                                    break;
                                case TOO_LONG:
                                    if (pinRangeCheckCode == 0x80) {
                                        isStartPINEntry = false;
                                        listener.onError(ErrorCode.SECVP_VPP_BUFFER_FULL, "PIN too long.");
                                        exitGetPin = true;
                                    } else if (pinRangeCheckCode == 0x40) {
                                        listener.onExtendedEvent(new ExtendedEventInfo(ExtendedEvent.TOO_LONG, null));
                                    }
                                    break;
                                case NULL:
                                    listener.onExtendedEvent(new ExtendedEventInfo(pinOutputEvent.getExtendedEvent(), pinOutputEvent.getTouchState()));
                                    break;
                            }
                        }
                    }
                    if (exitGetPin) {
                        return;
                    } else {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                }
            }
        });
    }

    @Override
    public void startOfflinePINEntry(RSAKey key, int timeout, PINEntry2Parameters parameters, final PINEntry2Listener listener) throws NSDKException {
        isSupported();

        if (listener == null) {
            throw new NSDKIllegalParameterException("PIN input listener is null");
        }

        if (timeout <= 0) {
            throw new NSDKIllegalParameterException("Timeout shall be >0");
        }

        if (isStartPINEntry) {
            throw new NSDKException(ErrorCode.ERROR, "PIN entry is busy.");
        }

        PINSessionType pinSessionType = PINSessionType.EMV_OFFLINE_CLEARPIN;
        ST_NAPI_RSA_KEY rsaKey = null;
        if (key != null) {
            if (isNotEmpty(key.getExponent()) && isNotEmpty(key.getModulus())) {
                pinSessionType = PINSessionType.EMV_OFFLINE_ENCPIN;
                rsaKey = new ST_NAPI_RSA_KEY(key.getModulus().length * 8, key.getModulus(), key.getExponent());
            }
        }

        int ret = -1;

        pinKeyList.clear();

        ret = NSDKJni.getInstance().NAPI_SecVPPInit(pinSessionType.getCode(), -1, -1, null, pinRangeCheckCode, timeout, rsaKey, null, 0);
        if (ret == ErrorCode.PARAM_ERROR) {
            NSDKJni.getInstance().NDK_SYS_UnRegisterEvent(SYS_EVENT_PIN | SYS_EVENT_ICCARD);
            throw new NSDKIllegalParameterException();
        }

        if (ret == ErrorCode.OK && parameters != null) {
            String pwdRange = getPwdLenRange(parameters);
            if (pwdRange == null || pwdRange.length() == 0) {
                NSDKJni.getInstance().NDK_SYS_UnRegisterEvent(SYS_EVENT_PIN | SYS_EVENT_ICCARD);
                throw new NSDKIllegalParameterException("Max len shall be greater than min len.");
            }
            ret = NSDKJni.getInstance().NAPI_SecVPPSetExpPinLenIn(pwdRange);
        } else {
            if (parameters == null) {
                parameters = new PINEntry2Parameters();
                parameters.setMaxPINLen(12);
            }
        }

        Map<PINPadButton, PINPadButton> customButtons = parameters.getCustomButtons();
        setCustomButtonFunc(customButtons);

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to start offline PIN input, result code = %d", ret));
        }
        isStartPINEntry = true;
        final PINEntry2Parameters finalParameters = parameters;
        NSDKExecutors.threadStart(new Runnable() {
            @Override
            public void run() {
                count = 0;
                while (true) {
                    boolean exitGetPin = false;
                    PINOutputEvent pinOutputEvent = new PINOutputEvent();
                    int ret = 0;
                    try {
                        pinOutputEvent = getPINOutputEvent();
                    } catch (Exception e) {
                        if (e instanceof NSDKException) {
                            ret = ((NSDKException) e).getCode();
                        } else {
                            ret = ErrorCode.ERROR;
                        }
                    }
                    if (ret != ErrorCode.OK) {
                        isStartPINEntry = false;
                        // -1122 is timeout error
                        if (ret == -1122) {
                            listener.onTimeout();
                        } else {
                            listener.onError(ret, "PIN input failed.");
                        }
                        exitGetPin = true;
                    }
                    if (!exitGetPin) {
                        if (pinOutputEvent.getPinKeyEvent() != null) {
                            switch (pinOutputEvent.getPinKeyEvent()) {
                                case PIN:
                                    listener.onKeyPress();
                                    count++;
                                    if (count == finalParameters.getMaxPINLen() && finalParameters.isAutoComplete()) {
                                        try {
                                            setPinEvent(PINKeyEvent.ENTER);
                                        } catch (NSDKException e) {
                                            e.printStackTrace();
                                            isStartPINEntry = false;
                                            listener.onError(e.getCode(), "Failed to finish PIN input automatically.");
                                            exitGetPin = true;
                                        }
                                    }
                                    break;
                                case BACKSPACE:
                                    listener.onBackspace();
                                    if (count > 0) {
                                        count--;
                                    }
                                    break;
                                case CLEAR:
                                    listener.onClear();
                                    count = 0;
                                    break;
                                case ENTER:
                                    isStartPINEntry = false;
                                    listener.onFinish(pinOutputEvent.getPinLen(), pinOutputEvent.getPinBlock(), pinOutputEvent.getKsn());
                                    exitGetPin = true;
                                    break;
                                case ESC:
                                    isStartPINEntry = false;
                                    listener.onCancel();
                                    exitGetPin = true;
                                    break;
                                case TOO_SHORT:
                                    if (pinRangeCheckCode == 0x80) {
                                        isStartPINEntry = false;
                                        listener.onError(ErrorCode.SECVP_VPP_PIN_TOO_SHORT, "PIN too short.");
                                        exitGetPin = true;
                                    } else if (pinRangeCheckCode == 0x40) {
                                        listener.onExtendedEvent(new ExtendedEventInfo(ExtendedEvent.TOO_SHORT, null));
                                    }
                                    break;
                                case TOO_LONG:
                                    if (pinRangeCheckCode == 0x80) {
                                        isStartPINEntry = false;
                                        listener.onError(ErrorCode.SECVP_VPP_BUFFER_FULL, "PIN too long.");
                                        exitGetPin = true;
                                    } else if (pinRangeCheckCode == 0x40) {
                                        listener.onExtendedEvent(new ExtendedEventInfo(ExtendedEvent.TOO_LONG, null));
                                    }
                                    break;
                                case NULL:
                                    listener.onExtendedEvent(new ExtendedEventInfo(pinOutputEvent.getExtendedEvent(), pinOutputEvent.getTouchState()));
                                    break;
                            }
                        }
                    }

                    if (exitGetPin) {
                        return;
                    } else {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                }
            }
        });
    }

    @Override
    public void cancelPINEntry() throws NSDKException {
        isSupported();

        setPinEvent(PINKeyEvent.ESC);
    }

    private PINOutputEvent getPINOutputEvent() throws NSDKException {
        int[] nEvent = new int[1];
        int[] extendedEvent = new int[1];
        int[] touchState = new int[1];
        byte[] pinBlock = new byte[32];
        int[] pinLen = new int[1];
        byte[] ksn = new byte[32];
        int[] ksnLen = new int[1];

        int ret = NSDKJni.getInstance().NAPI_SecVPPAAGetPin(
                nEvent,
                extendedEvent,
                touchState,
                pinBlock,
                pinLen,
                ksn,
                ksnLen
        );

        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to get PIN event, result code = %d", ret));
        }

        LogUtils.d(TAG, "NAPI_SecVPPAAGetPin results:");
        LogUtils.d(TAG, "nEvent[0] = " + nEvent[0]);
        LogUtils.d(TAG, "extendedEvent[0] = " + extendedEvent[0]);
        LogUtils.d(TAG, "touchState[0] = " + touchState[0]);
        LogUtils.d(TAG, "pinLen[0] = " + pinLen[0]);
        LogUtils.d(TAG, "ksnLen[0] = " + ksnLen[0]);

        // 创建输出事件对象
        PINOutputEvent outputEvent = new PINOutputEvent();

        // 设置事件类型
        outputEvent.setPinKeyEvent(PINKeyEvent.values()[nEvent[0]]);

        // 设置操作事件类型
        outputEvent.setExtendedEvent(ExtendedEvent.values()[extendedEvent[0]]);

        // 设置事件状态
        outputEvent.setTouchState(TouchState.values()[touchState[0]]);

        // 设置当前输入的PIN长度
        outputEvent.setPinLen(count);
        int len = pinLen[0];

        if (len > 0) {
            outputEvent.setPinBlock(Arrays.copyOf(pinBlock, len));
        }

        // 设置KSN数据
        len = ksnLen[0];
        if (len > 0) {
            byte[] actualKsn = new byte[len];
            System.arraycopy(ksn, 0, actualKsn, 0, ksnLen[0]);
            outputEvent.setKsn(actualKsn);
        }

        return outputEvent;
    }

    private void setPinEvent(PINKeyEvent event) throws NSDKException {
        if (event == null) {
            throw new NSDKIllegalParameterException("Event should not be null!");
        }

        if (event != PINKeyEvent.ENTER && event != PINKeyEvent.ESC
                && event != PINKeyEvent.CLEAR) {
            throw new NSDKIllegalParameterException("Only support the following PIN events: Enter, ESC, Clear.");
        }

        int ret = NSDKJni.getInstance().NAPI_SecVPPSetEvent(event.ordinal());
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to set PIN event, result code = %d", ret));
        } else {
            //应用调用该接口时，systemevent不会将对应设置的event通过回调上送给应用，导致NDK_SYS_UnRegisterEvent不会被调用，后续再调用输pin接口就会操作4007
            // setevent传入的值，可以通过getevent获取，因此在此处添加了
            //addPinKey和notifyPinEvent，
            addPinKey(SYS_EVENT_PIN);
            notifyPinEvent();
        }
    }


    // 映射按钮到键值
    private int mapButtonToKeyValue(PINPadButton button) {
        switch (button) {
            case NUMBER_0:
                return 0x30;
            case NUMBER_1:
                return 0x31;
            case NUMBER_2:
                return 0x32;
            case NUMBER_3:
                return 0x33;
            case NUMBER_4:
                return 0x34;
            case NUMBER_5:
                return 0x35;
            case NUMBER_6:
                return 0x36;
            case NUMBER_7:
                return 0x37;
            case NUMBER_8:
                return 0x38;
            case NUMBER_9:
                return 0x39;
            case ENTER:
                return 0x0D;
            case BACKSPACE:
                return 0x0A;
            case CANCEL:
                return 0x1B;
            case CLEAR:
                return 0x9C;
            case QUIT:
                return 0x9B;
            case BLANK1:
            case BLANK2:
                return 0x00;
            case SWITCH:
                return 0x10;
            default:
                return -1;
        }
    }

    private void addPinKey(int key) {
        synchronized (pinKeyListObj) {
            pinKeyList.add(key);
        }
    }

    private void notifyPinEvent() {
        synchronized (pinEvent) {
            pinEvent.notify();
        }
    }

    private void setCustomButtonFunc(Map<PINPadButton, PINPadButton> customButtons) throws NSDKException {
        if (deviceInfo.isPhysicalKeyboard()) {
            if (customButtons != null && !customButtons.isEmpty()) {
                for (Map.Entry<PINPadButton, PINPadButton> buttonEntry : customButtons.entrySet()) {
                    checkCustomButtonsValid(buttonEntry.getKey(), buttonEntry.getValue());
                }
            }
        }
    }

    private void checkCustomButtonsValid(PINPadButton originButton, PINPadButton customButton) throws NSDKException {
        if (!(originButton == PINPadButton.CANCEL && customButton == PINPadButton.QUIT) && !(originButton == PINPadButton.BACKSPACE && customButton == PINPadButton.CLEAR)) {
            throw new NSDKException(ErrorCode.NOT_SUPPORTED, "Only allows setting QUIT function to CANCEL button and CLEAR function to BACKSPACE button.");
        }
        int ret = 0;
        if (originButton == PINPadButton.CANCEL) {
            ret = NSDKJni.getInstance().NAPI_SecVppSetButtonFunc(0x1B, 0);
        } else if (originButton == PINPadButton.BACKSPACE) {
            ret = NSDKJni.getInstance().NAPI_SecVppSetButtonFunc(0x0A, 1);
        }
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to set custom button function, ret = %d", ret));
        }
    }

    private String getPwdLenRange(PINEntry2Parameters parameters) {
        // 如果没有设置密码长度区间，则使用最小长度和最大长度之间递增的数组
        if (parameters.getPINLengthRange() == null) {
            parameters.setPINLengthRange(new byte[0]);
        }

        boolean isByPass = false;
        // 如果用户设置的最小长度小于 4，则修正为 4
        if (parameters.getMinPINLen() < 4) {
            if (parameters.getMinPINLen() == 0) {
                isByPass = true;
            }
            parameters.setMinPINLen(4);
        }

        // 如果用户设置的最大长度超过 12，则修正为 12
        if (parameters.getMaxPINLen() > 12) {
            parameters.setMaxPINLen(12);
        }

        if (parameters.getMinPINLen() > parameters.getMaxPINLen()) {
            parameters.setMinPINLen(4);
        }
        String pwdLenRangeIn = "";
        if (isByPass) {
            pwdLenRangeIn += String.format(Locale.US, "%d%c", 0, ',');
        }
        byte[] pwdLenRange = new byte[parameters.getPINLengthRange().length];
        int pwdLenRangeCount = 0;
        for (int i = 0; i < parameters.getPINLengthRange().length; i++) {
            boolean isByPassInRange = parameters.getPINLengthRange()[i] == 0;
            boolean isInRange = parameters.getPINLengthRange()[i] <= parameters.getMaxPINLen() && parameters.getPINLengthRange()[i] >= parameters.getMinPINLen();
            if ((isByPassInRange && !isByPass) || isInRange) {
                pwdLenRange[pwdLenRangeCount] = parameters.getPINLengthRange()[i];
                pwdLenRangeCount++;
            }
        }

        if (pwdLenRangeCount == 0) {
            for (int i = parameters.getMinPINLen(); i <= parameters.getMaxPINLen(); i++) {
                pwdLenRangeIn += String.format(Locale.US, "%d%c", i, ',');
            }
            parameters.setMaxPINLen(parameters.getMaxPINLen());
        } else {
            for (int i = 0; i < pwdLenRangeCount; i++) {
                if (pwdLenRange[i] <= parameters.getMaxPINLen()) {
                    pwdLenRangeIn += String.format(Locale.US, "%d%c", pwdLenRange[i], ',');
                }
            }
            parameters.setMaxPINLen(pwdLenRange[pwdLenRangeCount - 1]);
        }
        LogUtils.d(TAG, "************** max len = " + parameters.getMaxPINLen());
        pwdLenRangeIn = pwdLenRangeIn.substring(0, pwdLenRangeIn.length() - 1);
        LogUtils.d(TAG, "pwd range:" + pwdLenRangeIn);
        return pwdLenRangeIn;
    }

    private int getPinKey() {
        synchronized (pinKeyListObj) {
            if (pinKeyList.size() > 0) {
                int key = pinKeyList.get(0);
                pinKeyList.remove(0);
                return key;
            }
            return -1;
        }
    }

    private void waitPinEvent() {
        synchronized (pinEvent) {
            try {
                pinEvent.wait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isEmpty(byte[] l) {
        return l == null || l.length == 0;
    }

    private boolean isNotEmpty(byte[] l) {
        return !isEmpty(l);
    }

}
