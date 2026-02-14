package com.newland.nsdk.core.internal.pinentry;

import android.text.TextUtils;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.keymanager.AsymKeyType;
import com.newland.nsdk.core.api.common.keymanager.AsymmetricKey;
import com.newland.nsdk.core.api.common.keymanager.DUKPTDerivateKey;
import com.newland.nsdk.core.api.common.keymanager.DUKPTDerivateUsage;
import com.newland.nsdk.core.api.common.keymanager.Key;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.api.common.keymanager.KeyUsage;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.common.pinentry.PINBlockMode;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.internal.devicemanager.DeviceInfo;
import com.newland.nsdk.core.api.internal.exception.NSDKNDKException;
import com.newland.nsdk.core.api.internal.pinentry.ExtendedPINEntryListener;
import com.newland.nsdk.core.api.internal.pinentry.ExtendedRNIBPINEntryListener;
import com.newland.nsdk.core.api.internal.pinentry.PINConvertMode;
import com.newland.nsdk.core.api.internal.pinentry.PINConvertParameters;
import com.newland.nsdk.core.api.internal.pinentry.PINEntry;
import com.newland.nsdk.core.api.internal.pinentry.PINEntryListener;
import com.newland.nsdk.core.api.internal.pinentry.PINEntryParameters;
import com.newland.nsdk.core.api.internal.pinentry.PINPadButton;
import com.newland.nsdk.core.api.internal.pinentry.RNIBPINEntryListener;
import com.newland.nsdk.core.api.internal.pinentry.RSAKey;
import com.newland.nsdk.core.common.NSDKExecutors;
import com.newland.nsdk.core.common.keymanager.ST_SEC_SYMM_KEYID_INFO;
import com.newland.nsdk.core.internal.crypto.ST_SEC_DUKPT_DERIVATE_DATA;
import com.newland.nsdk.core.internal.jni.NSDKJni;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Author by wuhh, Date on 2020/1/19.
 */
public class PINEntryImpl implements PINEntry {

    private static final String TAG = "PINEntryImpl";
    private static final int SYS_EVENT_NONE = 0;
    private static final int SYS_EVENT_PIN = 0x00000020;
    private static final int SYS_EVENT_ICCARD = 0x00000008;

    private DeviceInfo deviceInfo;
    private Object pinEvent = new Object();
    private Object pinKeyListObj = new Object();
    private LinkedList<Integer> pinKeyList = new LinkedList<Integer>();

    public boolean isSupported;
    private volatile boolean isStartPINEntry = false;

    private volatile static PINEntryImpl instance;

    public static PINEntryImpl getInstance(boolean isSupported, DeviceInfo deviceInfo) {
        if (instance == null) {
            synchronized (PINEntryImpl.class) {
                if (instance == null || instance.isSupported != isSupported || instance.deviceInfo != deviceInfo) {
                    instance = new PINEntryImpl(isSupported, deviceInfo);
                }
            }
        } else {
            if (instance.isSupported != isSupported || instance.deviceInfo != deviceInfo) {
                instance = new PINEntryImpl(isSupported, deviceInfo);
            }
        }
        return instance;
    }

    private PINEntryImpl(){
        this.isSupported = true;
    }

    private PINEntryImpl(boolean isSupported, DeviceInfo deviceInfo){
        this.isSupported = isSupported;
        this.deviceInfo = deviceInfo;
    }

    private void isSupported() throws NSDKException {
        if(!isSupported){
            throw new NSDKException(ErrorCode.UNSUPPORTED, "UnSupported PINEntry Module");
        }
    }

    @Override
    public byte[] initKeyLayout(byte[] numBtn, byte[] funcBtn, boolean isRandomKeyboard) throws NSDKException {
        isSupported();
        isSupportInitVirtualKeyboard();
        if (numBtn == null || funcBtn == null) {
            throw new NSDKIllegalParameterException();
        }

        byte[] keySeq = new byte[] {0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39};
        int ret;
        if (isRandomKeyboard) {
            ret = NSDKJni.getInstance().NAPI_SecVppTpInit(numBtn, funcBtn, keySeq, 1);
        } else {
            ret = NSDKJni.getInstance().NAPI_SecVppTpInit(numBtn, funcBtn, keySeq, 0);
        }
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to init key layout, result code = %d", ret));
        }

        return keySeq;
    }

    @Override
    public byte[] initKeyLayout(Map<PINPadButton, int[]> pinPadButtons, boolean isRandomKeyboard) throws NSDKException {
        isSupported();
        isSupportInitVirtualKeyboard();
        if (pinPadButtons == null || pinPadButtons.size() < 13) {
            throw new NSDKIllegalParameterException("At least 13 buttons required for PIN pad.");
        }

        byte[] numBtnCoordinates = getNumberButtonCoordinates(pinPadButtons);
        byte[] funBtnCoordinates = getFunctionButtonCoordinates(pinPadButtons);

        return initKeyLayout(numBtnCoordinates, funBtnCoordinates, isRandomKeyboard);
    }

    @Override
    public void initKeyLayout(Map<PINPadButton, int[]> pinPadButtons, int[] screenArea) throws NSDKException {
        isSupported();
        isSupportInitVirtualKeyboard();
        if (pinPadButtons == null || (pinPadButtons.size() != 12 && pinPadButtons.size() != 13 && pinPadButtons.size() != 15)) {
            throw new NSDKIllegalParameterException("12 or 13 or 15 buttons are required in RNIB key layout for PIN pad.");
        }
        if (screenArea == null || (screenArea.length != 2 && screenArea.length != 4)) {
            throw new NSDKIllegalParameterException("Screen Area shall contains left-top and right-bottom  which required 4 certain coordination value, or 2 bytes of the right-bottom coordinates.");
        }
        int[] buttonsCoordination = null;
        int keyNumber = 13;
        if (pinPadButtons.size() == 13) {
            buttonsCoordination = new int[4 * 13];
            System.arraycopy(pinPadButtons.get(PINPadButton.NUMBER_1), 0, buttonsCoordination, 0, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.NUMBER_2), 0, buttonsCoordination, 4, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.NUMBER_3), 0, buttonsCoordination, 8, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.NUMBER_4), 0, buttonsCoordination, 12, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.NUMBER_5), 0, buttonsCoordination, 16, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.NUMBER_6), 0, buttonsCoordination, 20, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.NUMBER_7), 0, buttonsCoordination, 24, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.NUMBER_8), 0, buttonsCoordination, 28, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.NUMBER_9), 0, buttonsCoordination, 32, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.NUMBER_0), 0, buttonsCoordination, 36, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.CANCEL), 0, buttonsCoordination, 40, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.BACKSPACE), 0, buttonsCoordination, 44, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.ENTER), 0, buttonsCoordination, 48, 4);
        } else if (pinPadButtons.size() == 12){
            keyNumber = 12;
            buttonsCoordination = new int[4 * 12];
            System.arraycopy(pinPadButtons.get(PINPadButton.NUMBER_1), 0, buttonsCoordination, 0, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.NUMBER_2), 0, buttonsCoordination, 4, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.NUMBER_3), 0, buttonsCoordination, 8, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.NUMBER_4), 0, buttonsCoordination, 12, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.NUMBER_5), 0, buttonsCoordination, 16, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.NUMBER_6), 0, buttonsCoordination, 20, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.NUMBER_7), 0, buttonsCoordination, 24, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.NUMBER_8), 0, buttonsCoordination, 28, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.NUMBER_9), 0, buttonsCoordination, 32, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.NUMBER_0), 0, buttonsCoordination, 36, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.CANCEL), 0, buttonsCoordination, 40, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.ENTER), 0, buttonsCoordination, 44, 4);
        } else if (pinPadButtons.size() == 15) {
            keyNumber = 15;
            buttonsCoordination = new int[4 * 15];
            System.arraycopy(pinPadButtons.get(PINPadButton.NUMBER_1), 0, buttonsCoordination, 0, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.NUMBER_2), 0, buttonsCoordination, 4, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.NUMBER_3), 0, buttonsCoordination, 8, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.CANCEL), 0, buttonsCoordination, 12, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.NUMBER_4), 0, buttonsCoordination, 16, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.NUMBER_5), 0, buttonsCoordination, 20, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.NUMBER_6), 0, buttonsCoordination, 24, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.BACKSPACE), 0, buttonsCoordination, 28, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.NUMBER_7), 0, buttonsCoordination, 32, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.NUMBER_8), 0, buttonsCoordination, 36, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.NUMBER_9), 0, buttonsCoordination, 40, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.ENTER), 0, buttonsCoordination, 44, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.BLANK1), 0, buttonsCoordination, 48, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.NUMBER_0), 0, buttonsCoordination, 52, 4);
            System.arraycopy(pinPadButtons.get(PINPadButton.BLANK2), 0, buttonsCoordination, 56, 4);
        }

        int[] keyPadCoordination = new int[4];
        keyPadCoordination[0] = pinPadButtons.get(PINPadButton.NUMBER_1)[0];
        keyPadCoordination[1] = pinPadButtons.get(PINPadButton.NUMBER_1)[1];
        keyPadCoordination[2] = pinPadButtons.get(PINPadButton.ENTER)[2];
        keyPadCoordination[3] = pinPadButtons.get(PINPadButton.ENTER)[3];

        if (screenArea.length == 2) {
            int[] rightBottom = new int[2];
            System.arraycopy(screenArea, 0, rightBottom, 0, 2);
            screenArea = new int[4];
            screenArea[0] = 0;
            screenArea[1] = 0;
            screenArea[2] = rightBottom[0];
            screenArea[3] = rightBottom[1];
        }
        int ret = NSDKJni.getInstance().NAPI_SecVppRNIBTpInit(buttonsCoordination, screenArea, keyPadCoordination, keyNumber);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to init RNIB key layout, ret = %d", ret));
        }
    }

    private byte[] getFunctionButtonCoordinates(Map<PINPadButton, int[]> pinPadButtons) throws NSDKNDKException{
        byte[] funcBtnLayout = new byte[36];
        byte[] funcBtnCode = { 0x00, 0x00, 0x00, 0x00 };
        int offset = 0;
        int funBtnCount = 0;
        List<PINPadButton> buttons = new ArrayList<>();
        for (Map.Entry<PINPadButton, int[]> buttonEntry : pinPadButtons.entrySet()) {
            buttons.add(buttonEntry.getKey());
        }
        for (PINPadButton button : buttons){
            boolean isFunBtn = true;
            switch (button) {
                case BACKSPACE:
                    funcBtnCode[0] = 0x0A;
                    funBtnCount ++;
                    break;
                case CANCEL:
                    funcBtnCode[0] = 0x1B;
                    funBtnCount ++;
                    break;
                case ENTER:
                    funcBtnCode[0] = 0x0D;
                    funBtnCount ++;
                    break;
                case QUIT:
                    funcBtnCode[0] = (byte) 0x9B;
                    funBtnCount ++;
                    break;
                case CLEAR:
                    funcBtnCode[0] = (byte) 0x9C;
                    funBtnCount ++;
                    break;
                default:
                    isFunBtn = false;
                    break;
            }

            if (!isFunBtn) {
                continue;
            }

            if (funBtnCount > 3) {
                throw new NSDKNDKException(ErrorCode.PARAM_ERROR, "Only 3 function keys allowed.");
            }

            LogUtils.d(TAG, "funcBtnCode:" + ISOUtils.hexString(funcBtnCode));

            System.arraycopy(funcBtnCode, 0, funcBtnLayout, offset, 4);
            offset += 4;
            System.arraycopy(getCoordinate(pinPadButtons.get(button)), 0, funcBtnLayout, offset, 8);
            offset += 8;
        }

        return funcBtnLayout;
    }

    private byte[] getNumberButtonCoordinates(Map<PINPadButton, int[]> pinPadButtons) {
        int length = 8;
        byte[] numBtnLayout = new byte[10 * 8];
        int offset = 0;

        //0
        System.arraycopy(getCoordinate(pinPadButtons.get(PINPadButton.NUMBER_0)), 0, numBtnLayout, offset, length);
        offset += length;
        //1
        System.arraycopy(getCoordinate(pinPadButtons.get(PINPadButton.NUMBER_1)), 0, numBtnLayout, offset, length);
        offset += length;
        //2
        System.arraycopy(getCoordinate(pinPadButtons.get(PINPadButton.NUMBER_2)), 0, numBtnLayout, offset, length);
        offset += length;
        //3
        System.arraycopy(getCoordinate(pinPadButtons.get(PINPadButton.NUMBER_3)), 0, numBtnLayout, offset, length);
        offset += length;
        //4
        System.arraycopy(getCoordinate(pinPadButtons.get(PINPadButton.NUMBER_4)), 0, numBtnLayout, offset, length);
        offset += length;
        //5
        System.arraycopy(getCoordinate(pinPadButtons.get(PINPadButton.NUMBER_5)), 0, numBtnLayout, offset, length);
        offset += length;
        //6
        System.arraycopy(getCoordinate(pinPadButtons.get(PINPadButton.NUMBER_6)), 0, numBtnLayout, offset, length);
        offset += length;
        //7
        System.arraycopy(getCoordinate(pinPadButtons.get(PINPadButton.NUMBER_7)), 0, numBtnLayout, offset, length);
        offset += length;
        //8
        System.arraycopy(getCoordinate(pinPadButtons.get(PINPadButton.NUMBER_8)), 0, numBtnLayout, offset, length);
        offset += length;
        //9
        System.arraycopy(getCoordinate(pinPadButtons.get(PINPadButton.NUMBER_9)), 0, numBtnLayout, offset, length);

        return numBtnLayout;
    }

    private byte[] getCoordinate(int[] coordinate) {
        byte[] result = new byte[8];
        int leftTopX = coordinate[0];
        int leftTopY = coordinate[1];
        int rightBottomX = coordinate[2];
        int rightBottomY = coordinate[3];

        result[1] = (byte) ((leftTopX >> 8) & 0xff);
        result[0] = (byte) (leftTopX & 0xff);
        result[3] = (byte) ((leftTopY >> 8) & 0xff);
        result[2] = (byte) (leftTopY & 0xff);
        result[5] = (byte) ((rightBottomX >> 8) & 0xff);
        result[4] = (byte) (rightBottomX & 0xff);
        result[7] = (byte) ((rightBottomY >> 8) & 0xff);
        result[6] = (byte) (rightBottomY & 0xff);
        return result;
    }

    @Override
    public void startOnlinePINEntry(Key key, String pan, int timeout, final PINEntryParameters parameters, final PINEntryListener listener) throws NSDKException {
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

        int  pinSessionTypeCode;
        int keyTypeCode;
        ST_SEC_DUKPT_DERIVATE_DATA dukptDerivateData = null;
        int icPresentCode = 0;
        if (parameters.isCheckIcPresent()) {
            icPresentCode = (1 << 8);
        }
        int pinRangeCheckCode = 0;
        if (parameters.isCheckPINRange()) {
           pinRangeCheckCode = 0x80;
        } else {
            if (listener instanceof ExtendedPINEntryListener) {
                pinRangeCheckCode = 0x40;
            }
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
            AsymKeyType asymKeyType = ((AsymmetricKey)key).getKeyType();
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

        final int event = parameters.isCheckIcPresent() ? SYS_EVENT_PIN | SYS_EVENT_ICCARD : SYS_EVENT_PIN;

        // timeout + 2 in case that NDK event service times out before PIN pad.
        int ret = NSDKJni.getInstance().NDK_SYS_RegisterEvent(event, (timeout + 2) * 1000, new SysEventCallBack() {
            @Override
            public void callback(int event, int msgLen, byte[] msg) {
                LogUtils.e(TAG, "pin callback: event=" + event);
                addPinKey(event);
                notifyPinEvent();
            }
        });

        if (ret == -4007) {
            NSDKJni.getInstance().NDK_SYS_UnRegisterEvent(event);

            ret = NSDKJni.getInstance().NDK_SYS_RegisterEvent(event, (timeout + 2) * 1000, new SysEventCallBack() {
                @Override
                public void callback(int event, int msgLen, byte[] msg) {
                    LogUtils.e(TAG, "pin callback: event=" + event);
                    addPinKey(event);
                    notifyPinEvent();
                }
            });
        }

        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to register event, result code = %d", ret));
        }

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
            if (pwdRange == null || pwdRange.length() == 0) {
                NSDKJni.getInstance().NDK_SYS_UnRegisterEvent(event);
                throw new NSDKIllegalParameterException("Max len shall be greater than min len.");
            }

            ret = NSDKJni.getInstance().NAPI_SecVPPSetExpPinLenIn(pwdRange);
        } else {
            parameters.setMaxPINLen(12);
        }
        LogUtils.d(TAG, "******* pin entry started.");
        if (ret == ErrorCode.PARAM_ERROR) {
            NSDKJni.getInstance().NDK_SYS_UnRegisterEvent(event);
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            NSDKJni.getInstance().NDK_SYS_UnRegisterEvent(event);
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to start online PIN input, result code = %d", ret));
        }
        isStartPINEntry = true;
        final int finalPinRangeCheckCode = pinRangeCheckCode;
        NSDKExecutors.threadStart(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                while (true) {
                    boolean exitGetPin = false;
                    int key = getPinKey();
                    if (key < 0) {
                        waitPinEvent();
                        continue;
                    }
                    if (key == SYS_EVENT_NONE) {
                        LogUtils.d(getClass().getName(), "key  SYS_EVENT_NONE: " + key);
                        releaseEvent(event);
                        listener.onTimeout();
                        exitGetPin = true;
                    } else {
                        LogUtils.d(TAG, "******* key : " + key);
                        PINOutput pinOutput = new PINOutput();
                        int ret = 0;

                        try {
                            pinOutput = getPinEvent();
                        } catch (Exception e) {
                            if (e instanceof NSDKException) {
                                ret = ((NSDKException) e).getCode();
                            } else {
                                ret = ErrorCode.ERROR;
                            }
                        }

                        LogUtils.d(TAG, "******* getPinEvent : " + pinOutput.getEvent());
                        if (ret != ErrorCode.OK) {
                            releaseEvent(event);
                            // -1122 is timeout error
                            if (ret == -1122) {
                                listener.onTimeout();
                            } else {
                                listener.onError(ret, "PIN entry failed.");
                            }
                            exitGetPin = true;
                        }

                        if (!exitGetPin) {
                            switch (pinOutput.getEvent()) {
                                case PIN:
                                    listener.onKeyPress();
                                    count ++;
                                    LogUtils.d(TAG, "************** max len = " + parameters.getMaxPINLen() + ", count = " + count);
                                    if (count == parameters.getMaxPINLen() && parameters.isAutoComplete()) {
                                        try {
                                            setPinEvent(PINKeyEvent.ENTER);
                                        } catch (NSDKException e) {
                                            releaseEvent(event);
                                            e.printStackTrace();
                                            listener.onError(e.getCode(), "Failed to finish PIN input automatically.");
                                            exitGetPin = true;
                                        }
                                    }
                                    break;
                                case BACKSPACE:
                                    listener.onBackspace();
                                    if (count > 0) {
                                        count --;
                                    }
                                    break;
                                case CLEAR:
                                    listener.onClear();
                                    count = 0;
                                    break;
                                case ENTER:
                                    releaseEvent(SYS_EVENT_PIN | SYS_EVENT_ICCARD);
                                    listener.onFinish(pinOutput.getPinlen(), pinOutput.getPinBlock(), pinOutput.getKsn());
                                    exitGetPin = true;
                                    break;
                                case ESC:
                                    releaseEvent(SYS_EVENT_PIN | SYS_EVENT_ICCARD);
                                    listener.onCancel();
                                    exitGetPin = true;
                                    break;
                                case TOO_SHORT:
                                    if (finalPinRangeCheckCode == 0x40) {
                                        ((ExtendedPINEntryListener) listener).onPINLengthInsufficient();
                                    } else if (finalPinRangeCheckCode == 0x80) {
                                        releaseEvent(SYS_EVENT_PIN | SYS_EVENT_ICCARD);
                                        if (listener instanceof ExtendedPINEntryListener) {
                                            ((ExtendedPINEntryListener) listener).onPINLengthInsufficient();
                                        }
                                        listener.onError(ErrorCode.SECVP_VPP_PIN_TOO_SHORT, "PIN too short.");
                                        exitGetPin = true;
                                    }
                                    break;
                                case TOO_LONG:
                                    if (finalPinRangeCheckCode == 0x40) {
                                        ((ExtendedPINEntryListener) listener).onPINLengthExceeded();
                                    } else if (finalPinRangeCheckCode == 0x80) {
                                        releaseEvent(SYS_EVENT_PIN | SYS_EVENT_ICCARD);
                                        if (listener instanceof ExtendedPINEntryListener) {
                                            ((ExtendedPINEntryListener) listener).onPINLengthExceeded();
                                        }
                                        listener.onError(ErrorCode.SECVP_VPP_BUFFER_FULL, "PIN too long.");
                                        exitGetPin = true;
                                    }
                                    break;
                                case NULL:
                                    break;
                            }
                        }
                    }
                    if (exitGetPin) {
                        return;
                    } else {
                        continue;
                    }
                }
            }
        });
    }


    @Override
    public void startOfflinePINEntry(RSAKey key, int timeout, PINEntryParameters parameters, final PINEntryListener listener) throws
            NSDKException {
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

        int pinRangeCheckCode = 0;
        if (parameters != null && parameters.isCheckPINRange()) {
            pinRangeCheckCode = 0x80;
        } else {
            if (listener instanceof ExtendedPINEntryListener) {
                pinRangeCheckCode = 0x40;
            }
        }

        PINSessionType pinSessionType = PINSessionType.EMV_OFFLINE_CLEARPIN;
        ST_NAPI_RSA_KEY rsaKey = null;
        if (key != null) {
            if (isNotEmpty(key.getExponent()) && isNotEmpty(key.getModulus())) {
                pinSessionType = PINSessionType.EMV_OFFLINE_ENCPIN;
                rsaKey = new ST_NAPI_RSA_KEY(key.getModulus().length * 8, key.getModulus(), key.getExponent());
            }
        }
        // timeout + 2 in case that NDK event service times out before PIN pad.
        int ret = NSDKJni.getInstance().NDK_SYS_RegisterEvent(SYS_EVENT_PIN | SYS_EVENT_ICCARD, (timeout + 2) * 1000, new SysEventCallBack() {
            @Override
            public void callback(int event, int msgLen, byte[] msg) {
                LogUtils.e(TAG, "pin callback: event=" + event);
                addPinKey(event);
                notifyPinEvent();
            }
        });

        if (ret == -4007) {
            NSDKJni.getInstance().NDK_SYS_UnRegisterEvent(SYS_EVENT_PIN | SYS_EVENT_ICCARD);

            ret = NSDKJni.getInstance().NDK_SYS_RegisterEvent(SYS_EVENT_PIN | SYS_EVENT_ICCARD, (timeout + 2) * 1000, new SysEventCallBack() {
                @Override
                public void callback(int event, int msgLen, byte[] msg) {
                    addPinKey(event);
                    notifyPinEvent();
                }
            });
        }

        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to register event, result code = %d.", ret));
        }

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
                parameters = new PINEntryParameters();
                parameters.setMaxPINLen(12);
            }
        }

        Map<PINPadButton, PINPadButton> customButtons = parameters.getCustomButtons();
        setCustomButtonFunc(customButtons);

        if (ret != ErrorCode.OK) {
            NSDKJni.getInstance().NDK_SYS_UnRegisterEvent(SYS_EVENT_PIN | SYS_EVENT_ICCARD);
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to start offline PIN input, result code = %d", ret));
        }
        isStartPINEntry = true;
        final PINEntryParameters finalParameters = parameters;
        final int finalPinRangeCheckCode = pinRangeCheckCode;
        NSDKExecutors.threadStart(new Runnable()  {
            @Override
            public void run() {

                while (true) {
                    boolean exitGetPin = false;
                    int key = getPinKey();
                    int count = 0;
                    if (key < 0) {
                        waitPinEvent();
                        continue;
                    }
                    if (key == SYS_EVENT_NONE) {
                        releaseEvent(SYS_EVENT_PIN | SYS_EVENT_ICCARD);
                        listener.onTimeout();
                        exitGetPin = true;
                    } else {
                        PINOutput pinOutput = new PINOutput();
                        int ret = 0;
                        try {
                            pinOutput = getPinEvent();

                        } catch (Exception e) {
                            if (e instanceof NSDKException) {
                                ret = ((NSDKException) e).getCode();
                            } else {
                                ret = ErrorCode.ERROR;
                            }
                        }
                        if (ret != ErrorCode.OK) {
                            releaseEvent(SYS_EVENT_PIN | SYS_EVENT_ICCARD);
                            // -1122 is timeout error
                            if (ret == -1122) {
                                listener.onTimeout();
                            } else {
                                listener.onError(ret, "PIN input failed.");
                            }
                            exitGetPin = true;
                        }
                        if (!exitGetPin) {
                            if (pinOutput.getEvent() != null) {
                                switch (pinOutput.getEvent()) {
                                    case PIN:
                                        listener.onKeyPress();
                                        count++;
                                        if (count == finalParameters.getMaxPINLen() && finalParameters.isAutoComplete()) {
                                            try {
                                                setPinEvent(PINKeyEvent.ENTER);
                                            } catch (NSDKException e) {
                                                e.printStackTrace();
                                                releaseEvent(SYS_EVENT_PIN | SYS_EVENT_ICCARD);
                                                listener.onError(e.getCode(), "Failed to finish PIN input automatically.");
                                                exitGetPin = true;
                                            }
                                        }
                                        break;
                                    case BACKSPACE:
                                        listener.onBackspace();
                                        if (count > 0) {
                                            count --;
                                        }
                                        break;
                                    case CLEAR:
                                        listener.onClear();
                                        count = 0;
                                        break;
                                    case ENTER:
                                        releaseEvent(SYS_EVENT_PIN | SYS_EVENT_ICCARD);
                                        listener.onFinish(pinOutput.getPinlen(), pinOutput.getPinBlock(), pinOutput.getKsn());
                                        exitGetPin = true;
                                        break;
                                    case ESC:
                                        releaseEvent(SYS_EVENT_PIN | SYS_EVENT_ICCARD);
                                        listener.onCancel();
                                        exitGetPin = true;
                                        break;
                                    case TOO_SHORT:
                                        if (finalPinRangeCheckCode == 0x40) {
                                            ((ExtendedPINEntryListener) listener).onPINLengthInsufficient();
                                        } else if (finalPinRangeCheckCode == 0x80) {
                                            releaseEvent(SYS_EVENT_PIN | SYS_EVENT_ICCARD);
                                            if (listener instanceof ExtendedPINEntryListener) {
                                                ((ExtendedPINEntryListener) listener).onPINLengthInsufficient();
                                            }
                                            listener.onError(ErrorCode.SECVP_VPP_PIN_TOO_SHORT, "PIN too short.");
                                            exitGetPin = true;
                                        }

                                        break;
                                    case TOO_LONG:
                                        if (finalPinRangeCheckCode == 0x40) {
                                            ((ExtendedPINEntryListener) listener).onPINLengthExceeded();
                                        } else if (finalPinRangeCheckCode == 0x80) {
                                            releaseEvent(SYS_EVENT_PIN | SYS_EVENT_ICCARD);
                                            if (listener instanceof ExtendedPINEntryListener) {
                                                ((ExtendedPINEntryListener) listener).onPINLengthExceeded();
                                            }
                                            listener.onError(ErrorCode.SECVP_VPP_BUFFER_FULL, "PIN too long.");
                                            exitGetPin = true;
                                        }
                                        break;
                                    case NULL:
                                        break;
                                }
                            }
                        }
                    }

                    if (exitGetPin) {
                        return;
                    } else {
                        continue;
                    }
                }
            }
        });
    }

    @Override
    public void startOnlinePINEntry(Key key, final String pan, int timeout, final PINEntryParameters parameters, final RNIBPINEntryListener listener) throws NSDKException {
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

        int icPresentCode = parameters.isCheckIcPresent() ? (1 << 8) : 0;
        int pinRangeCheckCode = 0;
        if (parameters.isCheckPINRange()) {
            pinRangeCheckCode = 0x80;
        } else {
            if (listener instanceof ExtendedRNIBPINEntryListener) {
                pinRangeCheckCode = 0x40;
            }
        }
        PINSessionType pinSessionType;
        int keyTypeCode;
        ST_SEC_DUKPT_DERIVATE_DATA dukptDerivateData = null;

        if (key instanceof SymmetricKey) {
            SymmetricKey tempSymmKey = (SymmetricKey) key;
            if (tempSymmKey.getKeyUsage() == KeyUsage.DUKPT) {
                pinSessionType = PINSessionType.DUKPT;
            } else {
                pinSessionType = PINSessionType.MASTER_SESSION;
            }
            if (tempSymmKey.getKeyType() == null) {
                throw new NSDKIllegalParameterException("Please set key type of the key.");
            }
            keyTypeCode = tempSymmKey.getKeyType().getCode();
        } else if (key instanceof AsymmetricKey) {
            pinSessionType = PINSessionType.MASTER_SESSION;
            AsymKeyType asymKeyType = ((AsymmetricKey)key).getKeyType();
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


        int ret = 0;

        if (parameters.isCheckIcPresent()) {
            ret = NSDKJni.getInstance().NDK_SYS_RegisterEvent(SYS_EVENT_ICCARD, (timeout + 2) * 1000, new SysEventCallBack() {
                @Override
                public void callback(int event, int msgLen, byte[] msg) {
                    addPinKey(event);
                    notifyPinEvent();
                }
            });
            if (ret == -4007) {
                NSDKJni.getInstance().NDK_SYS_UnRegisterEvent(SYS_EVENT_ICCARD);
                ret = NSDKJni.getInstance().NDK_SYS_RegisterEvent(SYS_EVENT_ICCARD, (timeout + 2) * 1000, new SysEventCallBack() {
                    @Override
                    public void callback(int event, int msgLen, byte[] msg) {
                        addPinKey(event);
                        notifyPinEvent();
                    }
                });
            }
            if (ret == ErrorCode.PARAM_ERROR) {
                throw new NSDKIllegalParameterException();
            }

            if (ret != ErrorCode.OK) {
                throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to register IC event, result code = %d", ret));
            }
        }

        pinKeyList.clear();

        if (dukptDerivateData != null) {
            ret = NSDKJni.getInstance().NAPI_SecVPPInit_DerivateKey(pinSessionType.getCode() | icPresentCode, keyTypeCode, key.getKeyID() & 0xFF, pan,
                    parameters.getPINBlockMode().getCode() | pinRangeCheckCode, timeout, dukptDerivateData);
        } else {
            ret = NSDKJni.getInstance().NAPI_SecVPPInit(pinSessionType.getCode() | icPresentCode, keyTypeCode, key.getKeyID() & 0xFF, pan,
                    parameters.getPINBlockMode().getCode() | pinRangeCheckCode, timeout, null, null, 0);
        }

        String pwdRange = "";
        if (ret == ErrorCode.OK && parameters != null) {
            pwdRange = getPwdLenRange(parameters);
            if (pwdRange == null || pwdRange.length() == 0) {
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
        final int finalPinRangeCheckCode = pinRangeCheckCode;
        NSDKExecutors.threadStart(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                while (true) {
                    boolean exitGetPin = false;
                    PINOutput pinOutput = new PINOutput();
                    int ret = 0;
                    try {
                        pinOutput = getPinEvent();
                    } catch (Exception e) {
                        if (e instanceof NSDKException) {
                            ret = ((NSDKException) e).getCode();
                        } else {
                            ret = ErrorCode.ERROR;
                        }
                    }

                    LogUtils.d(TAG, "******* getPinEvent : " + pinOutput.getEvent());
                    if (ret != ErrorCode.OK) {
                        releaseEvent(SYS_EVENT_ICCARD);
                        // -1122 is timeout error
                        if (ret == -1122) {
                            listener.onTimeout();
                        } else {
                            listener.onError(ret, "PIN entry failed.");
                        }
                        exitGetPin = true;
                    }
                    if (!exitGetPin) {
                        switch (pinOutput.getEvent()) {
                            case PIN:
                                listener.onKeyPress();
                                count ++;
                                LogUtils.d(TAG, "************** max len = " + parameters.getMaxPINLen() + ", count = " + count);
                                if (count == parameters.getMaxPINLen() && parameters.isAutoComplete()) {
                                    try {
                                        setPinEvent(PINKeyEvent.ENTER);
                                    } catch (NSDKException e) {
                                        e.printStackTrace();
                                        releaseEvent(SYS_EVENT_ICCARD);
                                        listener.onError(e.getCode(), "Failed to finish PIN input automatically.");
                                        exitGetPin = true;
                                    }
                                }
                                break;
                            case BACKSPACE:
                                listener.onBackspace();
                                if (count > 0) {
                                    count --;
                                }
                                break;
                            case CLEAR:
                                listener.onClear();
                                count = 0;
                                break;
                            case ENTER:
                                releaseEvent(SYS_EVENT_ICCARD);
                                listener.onFinish(pinOutput.getPinlen(), pinOutput.getPinBlock(), pinOutput.getKsn());
                                exitGetPin = true;
                                break;
                            case ESC:
                                releaseEvent(SYS_EVENT_ICCARD);
                                listener.onCancel();
                                exitGetPin = true;
                                break;
                            case TOO_SHORT:
                                if (finalPinRangeCheckCode == 0x40) {
                                    ((ExtendedRNIBPINEntryListener) listener).onPINLengthInsufficient();
                                } else if (finalPinRangeCheckCode == 0x80){
                                    releaseEvent(SYS_EVENT_ICCARD);
                                    if (listener instanceof ExtendedRNIBPINEntryListener) {
                                        ((ExtendedRNIBPINEntryListener) listener).onPINLengthInsufficient();
                                    }
                                    listener.onError(ErrorCode.SECVP_VPP_PIN_TOO_SHORT, "PIN too short.");
                                    exitGetPin = true;
                                }
                                break;
                            case TOO_LONG:
                                if (finalPinRangeCheckCode == 0x40) {
                                    ((ExtendedRNIBPINEntryListener) listener).onPINLengthExceeded();
                                } else if (finalPinRangeCheckCode == 0x80){
                                    releaseEvent(SYS_EVENT_ICCARD);
                                    if (listener instanceof ExtendedRNIBPINEntryListener) {
                                        ((ExtendedRNIBPINEntryListener) listener).onPINLengthExceeded();
                                    }
                                    listener.onError(ErrorCode.SECVP_VPP_BUFFER_FULL, "PIN too long.");
                                    exitGetPin = true;
                                }
                                break;
                            case NULL:
                                break;
                            case SLID_UP:
                                listener.onSlidUp();
                                break;
                            case SLID_DOWN:
                                listener.onSlidDown();
                                break;
                            case SLID_LEFT:
                                listener.onSlidLeft();
                                break;
                            case SLID_RIGHT:
                                listener.onSlidRight();
                                break;
                            case SLID_BACKSPACE:
                                listener.onSlidBackSpace();
                                break;
                            case SLID_ENTER:
                                listener.onSlidEnter();
                                break;
                            case SLID_CANCEL:
                                listener.onSlidCancel();
                                break;
                            case SLID_NUMKEY:
                                listener.onSlidNumberKey();
                                break;
                            case SLID_NO_DIGIT:
                                listener.onSlidNoDigitKey();
                                break;
                            default:
                                break;
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
    public void startOfflinePINEntry(RSAKey key, int timeout, PINEntryParameters parameters, final RNIBPINEntryListener listener) throws NSDKException {
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

        int icPresentCode = (parameters != null && parameters.isCheckIcPresent()) ? (1 << 8) : 0;
        int pinRangeCheckCode = 0;
        if (parameters != null && parameters.isCheckPINRange()) {
            pinRangeCheckCode = 0x80;
        } else {
            if (listener instanceof ExtendedRNIBPINEntryListener) {
                pinRangeCheckCode = 0x40;
            }
        }


        PINSessionType pinSessionType = PINSessionType.EMV_OFFLINE_CLEARPIN;
        ST_NAPI_RSA_KEY rsaKey = null;
        if (key != null) {
            if (isNotEmpty(key.getExponent()) && isNotEmpty(key.getModulus())) {
                pinSessionType = PINSessionType.EMV_OFFLINE_ENCPIN;
                rsaKey = new ST_NAPI_RSA_KEY(key.getModulus().length * 8, key.getModulus(), key.getExponent());
            }
        }
        int ret = 0;
        if (parameters != null && parameters.isCheckIcPresent()) {
            ret = NSDKJni.getInstance().NDK_SYS_RegisterEvent(SYS_EVENT_ICCARD, (timeout + 2) * 1000, new SysEventCallBack() {
                @Override
                public void callback(int event, int msgLen, byte[] msg) {
                    addPinKey(event);
                    notifyPinEvent();
                }
            });
            if (ret == -4007) {
                NSDKJni.getInstance().NDK_SYS_UnRegisterEvent(SYS_EVENT_ICCARD);
                ret = NSDKJni.getInstance().NDK_SYS_RegisterEvent(SYS_EVENT_ICCARD, (timeout + 2) * 1000, new SysEventCallBack() {
                    @Override
                    public void callback(int event, int msgLen, byte[] msg) {
                        addPinKey(event);
                        notifyPinEvent();
                    }
                });
            }
            if (ret == ErrorCode.PARAM_ERROR) {
                throw new NSDKIllegalParameterException();
            }

            if (ret != ErrorCode.OK) {
                throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to register IC event, result code = %d", ret));
            }
        }

        ret = NSDKJni.getInstance().NAPI_SecVPPInit(pinSessionType.getCode() | icPresentCode, -1, -1, null, pinRangeCheckCode, timeout, rsaKey, null, 0);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret == ErrorCode.OK && parameters != null) {
            String pwdRange = getPwdLenRange(parameters);
            if (pwdRange == null || pwdRange.length() == 0) {
                throw new NSDKIllegalParameterException("Max len shall be greater than min len.");
            }
            ret = NSDKJni.getInstance().NAPI_SecVPPSetExpPinLenIn(pwdRange);
        } else {
            if (parameters == null) {
                parameters = new PINEntryParameters();
                parameters.setMaxPINLen(12);
            }
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to start offline PIN input, result code = %d", ret));
        }
        isStartPINEntry = true;
        final PINEntryParameters finalParameters = parameters;
        final int finalPinRangeCheckCode = pinRangeCheckCode;
        NSDKExecutors.threadStart(new Runnable()  {
            @Override
            public void run() {

                while (true) {
                    boolean exitGetPin = false;
                    int count = 0;
                    PINOutput pinOutput = new PINOutput();
                    int ret = 0;
                    try {
                        pinOutput = getPinEvent();

                    } catch (Exception e) {
                        if (e instanceof NSDKException) {
                            ret = ((NSDKException) e).getCode();
                        } else {
                            ret = ErrorCode.ERROR;
                        }
                    }
                    if (ret != ErrorCode.OK) {
                        releaseEvent(SYS_EVENT_ICCARD);
                        // -1122 is timeout error
                        if (ret == -1122) {
                            listener.onTimeout();
                        } else {
                            listener.onError(ret, "PIN input failed.");
                        }
                        exitGetPin = true;
                    }
                    if (!exitGetPin) {
                        if (pinOutput.getEvent() != null) {
                            switch (pinOutput.getEvent()) {
                                case PIN:
                                    listener.onKeyPress();
                                    count++;
                                    if (count == finalParameters.getMaxPINLen() && finalParameters.isAutoComplete()) {
                                        try {
                                            setPinEvent(PINKeyEvent.ENTER);
                                        } catch (NSDKException e) {
                                            e.printStackTrace();
                                            releaseEvent(SYS_EVENT_ICCARD);
                                            listener.onError(e.getCode(), "Failed to finish PIN input automatically.");
                                            exitGetPin = true;
                                        }
                                    }
                                    break;
                                case BACKSPACE:
                                    listener.onBackspace();
                                    if (count > 0) {
                                        count --;
                                    }
                                    break;
                                case CLEAR:
                                    listener.onClear();
                                    count = 0;
                                    break;
                                case ENTER:
                                    releaseEvent(SYS_EVENT_ICCARD);
                                    listener.onFinish(pinOutput.getPinlen(), pinOutput.getPinBlock(), pinOutput.getKsn());
                                    exitGetPin = true;
                                    break;
                                case ESC:
                                    releaseEvent(SYS_EVENT_ICCARD);
                                    listener.onCancel();
                                    exitGetPin = true;
                                    break;
                                case TOO_SHORT:
                                    if (finalPinRangeCheckCode == 0x40) {
                                        ((ExtendedRNIBPINEntryListener) listener).onPINLengthInsufficient();
                                    } else if (finalPinRangeCheckCode == 0x80){
                                        releaseEvent(SYS_EVENT_ICCARD);
                                        if (listener instanceof ExtendedRNIBPINEntryListener) {
                                            ((ExtendedRNIBPINEntryListener) listener).onPINLengthInsufficient();
                                        }
                                        listener.onError(ErrorCode.SECVP_VPP_PIN_TOO_SHORT, "PIN too short.");
                                        exitGetPin = true;
                                    }
                                    break;
                                case TOO_LONG:
                                    if (finalPinRangeCheckCode == 0x40) {
                                        ((ExtendedRNIBPINEntryListener) listener).onPINLengthExceeded();
                                    } else if (finalPinRangeCheckCode == 0x80){
                                        releaseEvent(SYS_EVENT_ICCARD);
                                        if (listener instanceof ExtendedRNIBPINEntryListener) {
                                            ((ExtendedRNIBPINEntryListener) listener).onPINLengthExceeded();
                                        }
                                        listener.onError(ErrorCode.SECVP_VPP_BUFFER_FULL, "PIN too long");
                                        exitGetPin = true;
                                    }
                                    break;
                                case NULL:
                                    break;
                                case SLID_UP:
                                    listener.onSlidUp();
                                    break;
                                case SLID_DOWN:
                                    listener.onSlidDown();
                                    break;
                                case SLID_LEFT:
                                    listener.onSlidLeft();
                                    break;
                                case SLID_RIGHT:
                                    listener.onSlidRight();
                                    break;
                                case SLID_NUMKEY:
                                    listener.onSlidNumberKey();
                                    break;
                                case SLID_BACKSPACE:
                                    listener.onSlidBackSpace();
                                    break;
                                case SLID_CANCEL:
                                    listener.onSlidCancel();
                                    break;
                                case SLID_ENTER:
                                    listener.onSlidEnter();
                                    break;
                                case SLID_NO_DIGIT:
                                    listener.onSlidNoDigitKey();
                                    break;
                                default:
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

    private String getPwdLenRange(PINEntryParameters parameters) {
        // 如果没有设置密码长度区间，则使用最小长度和最大长度之间递增的数组
        if (parameters.getPINLengthRange() == null) {
            parameters.setPINLengthRange(new byte[0]);
        }

        boolean isByPass = false;
        // 如果用户设置的最小长度小于 4，则修正为 4
        if (parameters.getMinPINLen() < 4) {
            if(parameters.getMinPINLen() == 0) {
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
        if(isByPass) {
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
            parameters.setMaxPINLen(pwdLenRange[pwdLenRangeCount-1]);
        }
        LogUtils.d(TAG, "************** max len = " + parameters.getMaxPINLen());
        pwdLenRangeIn = pwdLenRangeIn.substring(0, pwdLenRangeIn.length() - 1);
        LogUtils.d(TAG, "pwd range:" + pwdLenRangeIn);
        return pwdLenRangeIn;
    }

    public PINOutput getPinEvent() throws NSDKException {
        int[] nEvent = new int[1];
        byte[] psPinBlock = new byte[32];
        int[] pnOutPinLen = new int[1];
        byte[] psKsn = new byte[32];
        int[] pnOutKsnLen = new int[1];
        int ret = NSDKJni.getInstance().NAPI_SecVPPGetEvent(nEvent, psPinBlock, pnOutPinLen, psKsn, pnOutKsnLen);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to get PIN event, result code = %d", ret));
        }
        PINOutput pinOutput = new PINOutput();
        pinOutput.setEvent(PINKeyEvent.values()[nEvent[0]]);
        int len = pnOutPinLen[0];
        if (len > 0) {
            byte[] pinblock = new byte[len];
            System.arraycopy(psPinBlock, 0, pinblock, 0, len);
            pinOutput.setPinBlock(pinblock);
            pinOutput.setPinlen(len);
            if (pinblock.length >= 2) {
                pinOutput.setSW(new byte[]{pinblock[0], pinblock[1]});
            }
        }
        len = pnOutKsnLen[0];
        if (len > 0) {
            byte[] ksn = new byte[len];
            System.arraycopy(psKsn, 0, ksn, 0, len);
            pinOutput.setKsn(ksn);
        }
        return pinOutput;
    }

    @Override
    public void cancelPINEntry() throws NSDKException {
        isSupported();

        setPinEvent(PINKeyEvent.ESC);
    }

    @Override
    public byte[] verifyOfflinePIN(Key key, String pan, byte[] pinBlock, RSAKey rsaKey, byte[] extKey) throws NSDKException {
        isSupported();

        if (key == null || pan == null || pinBlock == null) {
            throw new NSDKIllegalParameterException("Key, PAN and PIN block shall not be null.");
        }

        int keyTypeCode = KeyType.DES.getCode();
        int keyUsageCode = KeyUsage.PIN.getCode();
        int pinBlockFormat = PINBlockMode.ISO9564_0.getCode();
        if (key instanceof SymmetricKey) {
            SymmetricKey tempKey = (SymmetricKey) key;
            if (tempKey.getKeyType() != null) {
                keyTypeCode = tempKey.getKeyType().getCode();
                if (tempKey.getKeyType() == KeyType.AES) {
                    pinBlockFormat = PINBlockMode.ISO9564_4.getCode();
                }
            }

            if (extKey != null && extKey.length >0) {
                keyUsageCode = KeyUsage.KEK.getCode();
            }
        } else {
            throw new NSDKIllegalParameterException("Only support DES and AES key now.");
        }

        PINSessionType pinSessionType = PINSessionType.EMV_PIN_VERIFY_CLEARPIN;
        ST_NAPI_RSA_KEY jniRSAKey = null;
        if (rsaKey != null) {
            if (isNotEmpty(rsaKey.getExponent()) && isNotEmpty(rsaKey.getModulus())) {
                pinSessionType = PINSessionType.EMV_PIN_VERIFY_ENCPIN;
                jniRSAKey = new ST_NAPI_RSA_KEY(rsaKey.getModulus().length * 8, rsaKey.getModulus(), rsaKey.getExponent());
            }
        }

        byte[] outData = new byte[100];
        int[] outDataLen = new int[1];
        int ret = NSDKJni.getInstance().verifyOfflinePIN(pinSessionType.getCode(), keyTypeCode, key.getKeyID() & 0xFF, keyUsageCode, pan, pinBlockFormat, pinBlock, jniRSAKey, extKey, outData, outDataLen);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to verify offline PIN, result code = %d", ret));
        }

        int resultDataLen = outDataLen[0];
        if (resultDataLen <= 0) {
            throw new NSDKNDKException("Failed to verify offline PIN, result data len is 0.");
        }

        return Arrays.copyOf(outData, outDataLen[0]);
    }

    @Override
    public byte[] convertPINBlock(PINConvertParameters pinConvertParameters, SymmetricKey sessionKey, SymmetricKey pinKey, byte[] pinBlock) throws NSDKException {
        if (pinConvertParameters == null) {
            throw new NSDKIllegalParameterException("PIN block convert parameters shall not be null.");
        }
        if (sessionKey == null) {
            throw new NSDKIllegalParameterException("Session key shall not be null.");
        }
        if (pinBlock == null || pinBlock.length == 0) {
            throw new NSDKIllegalParameterException("PIN block data to be converted shall not be null.");
        }

        if (sessionKey.getKeyType() == null || sessionKey.getKeyUsage() == null) {
            throw new NSDKIllegalParameterException("Session key type and usage shall not be null.");
        }
        String PAN = pinConvertParameters.getPAN();
        if (TextUtils.isEmpty(PAN)) {
            throw new NSDKIllegalParameterException("PAN shall not be null.");
        }
        PINConvertMode convertMode = pinConvertParameters.getPinConvertMode();
        if (convertMode == null) {
            throw new NSDKIllegalParameterException("PIN convert mode shall not be null.");
        }
        if (convertMode == PINConvertMode.ONLY_CONVERT && pinKey == null) {
            throw new NSDKIllegalParameterException("Pin key shall not be null when convert mode is ONLY_CONVERT.");
        }


        PINBlockMode sessionPinBlockMode = pinConvertParameters.getSessionPinBlockMode();
        PINBlockMode convertPinBlockMode = pinConvertParameters.getConvertPinBlockMode();
        if (sessionPinBlockMode == null || convertPinBlockMode == null) {
            throw new NSDKIllegalParameterException("Session PIN block mode and Convert PIN Block shall not be null.");
        }

        ST_SEC_SYMM_KEYID_INFO sessionKeyInfo = new ST_SEC_SYMM_KEYID_INFO();
        sessionKeyInfo.setKeyType(sessionKey.getKeyType().getCode());
        sessionKeyInfo.setKeyUsage(sessionKey.getKeyUsage().getCode());
        if (pinKey.getKeyType() == null || pinKey.getKeyUsage() == null) {
            throw new NSDKIllegalParameterException("PIN key type and usage shall not be null.");
        }
        ST_SEC_SYMM_KEYID_INFO pinKeyInfo = new ST_SEC_SYMM_KEYID_INFO();
        pinKeyInfo.setKeyType(pinKey.getKeyType().getCode());
        pinKeyInfo.setKeyUsage(pinKey.getKeyUsage().getCode());

        byte[] outPinBlock = new byte[2048];
        int[] outPinBlockLen = new int[1];
        int ret = NSDKJni.getInstance().NAPI_SecPINBlockConvert(PAN, convertMode.ordinal(), sessionPinBlockMode.getCode(), convertPinBlockMode.getCode(), sessionKey.getKeyID(), sessionKeyInfo, pinKey.getKeyID(), pinKeyInfo, pinConvertParameters.getOfflinePinKey(), pinBlock, pinBlock.length, outPinBlock, outPinBlockLen);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to convert pin block, ret = %d", ret));
        }
        int outLen = outPinBlockLen[0];
        if (outLen > 0) {
            return Arrays.copyOf(outPinBlock, outLen);
        }
        return null;
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


    private boolean isEmpty(byte[] l) {
        return l == null || l.length == 0;
    }

    private boolean isNotEmpty(byte[] l) {
        return !isEmpty(l);
    }

    private void addPinKey(int key) {
        synchronized (pinKeyListObj) {
            pinKeyList.add(key);
        }
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

    private void notifyPinEvent() {
        synchronized (pinEvent) {
            pinEvent.notify();
        }
    }

    private void isSupportInitVirtualKeyboard() throws NSDKException {
        if (deviceInfo.isPhysicalKeyboard()) {
            throw new NSDKException(ErrorCode.NOT_SUPPORTED, "This function can be only used in device without physical keyboard.");
        }
    }

    private void setCustomButtonFunc(Map<PINPadButton, PINPadButton> customButtons) throws NSDKException{
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
        } else if (originButton == PINPadButton.BACKSPACE){
            ret = NSDKJni.getInstance().NAPI_SecVppSetButtonFunc(0x0A, 1);
        }
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to set custom button function, ret = %d", ret));
        }
    }


    private void releaseEvent(int eventType) {
        NSDKJni.getInstance().NDK_SYS_UnRegisterEvent(eventType);
        isStartPINEntry = false;
    }
}
