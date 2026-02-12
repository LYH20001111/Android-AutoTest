package com.newland.nsdk.core.external;

import android.text.TextUtils;
import android.util.Log;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.keymanager.AsymmetricKey;
import com.newland.nsdk.core.api.common.keymanager.Key;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.external.pinentry.CipherPAN;
import com.newland.nsdk.core.api.external.pinentry.ExtOfflinePINParameters;
import com.newland.nsdk.core.api.external.pinentry.ExtOnlinePINParameters;
import com.newland.nsdk.core.api.external.pinentry.ExtPINEntry;
import com.newland.nsdk.core.api.external.pinentry.ExtPINEntryListener;
import com.newland.nsdk.core.api.external.pinentry.ExtPINEntryParameters;
import com.newland.nsdk.core.api.external.pinentry.ExtendedCipherPAN;
import com.newland.nsdk.core.api.external.pinentry.ExtendedExtPINEntryListener;
import com.newland.nsdk.core.api.external.pinentry.ExtendedExtPINEntryParams;
import com.newland.nsdk.core.api.external.pinentry.PINMessageMode;
import com.newland.nsdk.core.api.external.pinentry.RSAKey;
import com.newland.nsdk.core.common.NSDKExecutors;
import com.newland.nsdk.core.external.command.keyboard.ExternalKeyboardEntryModule;
import com.newland.nsdk.core.external.command.keyboard.KeyboardEntryResult;

public class ExtPINEntryImpl implements ExtPINEntry {
    private ExternalKeyboardEntryModule keyboardEntryModule;
    private volatile static ExtPINEntryImpl instance;
    public static ExtPINEntryImpl getInstance() {
        if (instance == null) {
            synchronized (ExtPINEntryImpl.class) {
                if (instance == null) {
                    instance = new ExtPINEntryImpl();
                }
            }
        }
        return instance;
    }
    private ExtPINEntryImpl(){
        keyboardEntryModule = new ExternalKeyboardEntryModule();
    }

    @Override
    public void startOnlinePINEntry(final Key key, final String plainPan, final int timeout, final ExtPINEntryParameters pinInputParameter, final ExtPINEntryListener extPinpadListener) throws NSDKException {
        if (pinInputParameter == null || extPinpadListener == null) {
            throw new NSDKIllegalParameterException("Parameter or listener is null !");
        }

        if (timeout <= 0 || timeout > 255) {
            throw new NSDKIllegalParameterException("Timeout should be >0 and <=255!");
        }

        if (key == null) {
            throw new NSDKIllegalParameterException("Please set PIN key.");
        }
        if (key instanceof SymmetricKey) {
            if (((SymmetricKey)key).getKeyType() == null) {
                throw new NSDKIllegalParameterException("Please set PIN key type.");
            }
        } else if (key instanceof AsymmetricKey) {
            if (((AsymmetricKey)key).getKeyType() == null) {
                throw new NSDKIllegalParameterException("Please set PIN key type.");
            }
        } else {
            throw new NSDKIllegalParameterException("Key shall be a symmetric or asymmetric key.");
        }

        if (pinInputParameter.getPINBlockMode() == null) {
            throw new NSDKIllegalParameterException("Please set PIN block mode.");
        }

        if (TextUtils.isEmpty(plainPan)) {
            throw new NSDKIllegalParameterException("Please set plain text PAN.");
        }

        if (pinInputParameter instanceof ExtOnlinePINParameters) {
            ExtOnlinePINParameters onlinePINParameters = (ExtOnlinePINParameters)pinInputParameter;
            if (onlinePINParameters.getExtendedPINKeyData() != null && onlinePINParameters.getExtendedPINKeyData().length != 0) {
                if (onlinePINParameters.getExtendedKeyMode() == null) {
                    throw new NSDKIllegalParameterException("Please set extended PIN key mode.");
                }
            }
        }

        NSDKExecutors.threadStart(new Runnable() {
            @Override
            public void run() {
                try {
                    if (pinInputParameter.getPinLengthRange() != null) {
                        keyboardEntryModule.setValidPINLengthRange(pinInputParameter.getPinLengthRange());
                    }
                    if (pinInputParameter.getMaskLine() != null && pinInputParameter.getPinMaskAlignment() == null) {
                        keyboardEntryModule.setPinLine((byte) pinInputParameter.getMaskLine().getCode());
                    } else if (pinInputParameter.getMaskLine() != null && pinInputParameter.getPinMaskAlignment() != null) {
                        keyboardEntryModule.setPinLine((byte) pinInputParameter.getMaskLine().getCode(), pinInputParameter.getPinMaskAlignment().ordinal() + 1);
                    }
                    KeyboardEntryResult result = null;
                    if (pinInputParameter instanceof ExtendedExtPINEntryParams) {
                        result = keyboardEntryModule.newPinEntry(key, plainPan, null, pinInputParameter, timeout);
                    } else {
                        result = keyboardEntryModule.pinEntry(key, plainPan, null, pinInputParameter, timeout);
                    }
                    extPinpadListener.onOnlineSuccess(result.getPinLen(), result.getEncryptedPinBlock(), result.getKsn());
                } catch (Exception e) {
                    e.printStackTrace();
                    if (e instanceof NSDKException) {
                        if (((NSDKException) e).getCode() == ErrorCode.CANCELLED) {
                            extPinpadListener.onCancel();
                        } else if (((NSDKException) e).getCode() == ErrorCode.TIMEOUT) {
                            extPinpadListener.onTimeout();
                        } else {
                            extPinpadListener.onError(((NSDKException) e).getCode(), e.getMessage());
                        }
                    } else {
                        extPinpadListener.onError(ErrorCode.EXT_ERROR, e.getMessage());
                    }
                }
            }
        });
    }

    @Override
    public void startOnlinePINEntry(final Key key, final CipherPAN cipherPan, final int timeout, final ExtPINEntryParameters extPinpadParameter, final ExtPINEntryListener extPinpadListener) throws NSDKException {
        if (extPinpadParameter == null || extPinpadListener == null) {
            throw new NSDKIllegalParameterException("Parameter or listener is null !");
        }

        if (timeout <= 0 || timeout > 255) {
            throw new NSDKIllegalParameterException("Timeout should be >0 and <=255!");
        }

        if (key == null) {
            throw new NSDKIllegalParameterException("Please set PIN key.");
        }
        if (key instanceof SymmetricKey) {
            if (((SymmetricKey)key).getKeyType() == null) {
                throw new NSDKIllegalParameterException("Please set PIN key type.");
            }
        } else if (key instanceof AsymmetricKey) {
            if (((AsymmetricKey)key).getKeyType() == null) {
                throw new NSDKIllegalParameterException("Please set PIN key type.");
            }
        } else {
            throw new NSDKIllegalParameterException("Key shall be a symmetric or asymmetric key.");
        }

        if (extPinpadParameter.getPINBlockMode() == null) {
            throw new NSDKIllegalParameterException("Please set PIN block mode.");
        }

        if (cipherPan == null) {
            throw new NSDKIllegalParameterException("Please set cipher PAN.");
        }
        if (cipherPan.getPANKey() != null) {
            int panKeyIndex = cipherPan.getPANKey().getKeyID() & 0xFF;
            if (panKeyIndex >= 129 && panKeyIndex <= 255) {
                if (cipherPan.getPANKey() instanceof SymmetricKey) {
                    if (((SymmetricKey)cipherPan.getPANKey()).getKeyType() == null) {
                        throw new NSDKIllegalParameterException("Please set PAN key type.");
                    }
                } else if (cipherPan.getPANKey() instanceof AsymmetricKey) {
                    if (((AsymmetricKey)cipherPan.getPANKey()).getKeyType() == null) {
                        throw new NSDKIllegalParameterException("Please set PAN key type.");
                    }
                } else {
                    throw new NSDKIllegalParameterException("PAN key shall be a symmetric or asymmetric key.");
                }
            }
        }

        if (cipherPan.getCipherPAN() == null) {
            throw new NSDKIllegalParameterException("Please set cipher PAN data.");
        }

        if (extPinpadParameter instanceof ExtOnlinePINParameters) {
            ExtOnlinePINParameters onlinePINParameters = (ExtOnlinePINParameters)extPinpadParameter;
            if (onlinePINParameters.getExtendedPINKeyData() != null && onlinePINParameters.getExtendedPINKeyData().length != 0) {
                if (onlinePINParameters.getExtendedKeyMode() == null) {
                    throw new NSDKIllegalParameterException("Please set extended PIN key mode.");
                }
            }
        }

        NSDKExecutors.threadStart(new Runnable() {
            @Override
            public void run() {
                try {
                    if (extPinpadParameter.getPinLengthRange() != null) {
                        keyboardEntryModule.setValidPINLengthRange(extPinpadParameter.getPinLengthRange());
                    }
                    if (extPinpadParameter.getMaskLine() != null && extPinpadParameter.getPinMaskAlignment() == null) {
                        keyboardEntryModule.setPinLine((byte) extPinpadParameter.getMaskLine().getCode());
                    } else if (extPinpadParameter.getMaskLine() != null && extPinpadParameter.getPinMaskAlignment() != null) {
                        keyboardEntryModule.setPinLine((byte) extPinpadParameter.getMaskLine().getCode(), extPinpadParameter.getPinMaskAlignment().ordinal() + 1);
                    }
                    KeyboardEntryResult result = new KeyboardEntryResult();
                    if (cipherPan instanceof ExtendedCipherPAN && extPinpadParameter instanceof ExtendedExtPINEntryParams) {
                        if (((ExtendedCipherPAN) cipherPan).getCipherType() != null || ((ExtendedExtPINEntryParams)extPinpadParameter).getPinMessageMode() != PINMessageMode.DEFAULT
                        || ((ExtendedExtPINEntryParams)extPinpadParameter).getPinMessageAlignment() != null || ((ExtendedExtPINEntryParams) extPinpadParameter).getAdditionalData() != null) {
                            result = keyboardEntryModule.newPinEntry(key, null, cipherPan, extPinpadParameter, timeout);
                        }
                    } else {
                        result = keyboardEntryModule.pinEntry(key, null, cipherPan, extPinpadParameter, timeout);
                    }
                    if (extPinpadListener instanceof ExtendedExtPINEntryListener) {
                        ((ExtendedExtPINEntryListener)extPinpadListener).onOnlineSuccessExtended(result.getPinLen(), result.getEncryptedPinBlock(), result.getKsn(), result.getTlvData());
                    } else {
                        extPinpadListener.onOnlineSuccess(result.getPinLen(), result.getEncryptedPinBlock(), result.getKsn());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (e instanceof NSDKException) {
                        if (((NSDKException) e).getCode() == ErrorCode.CANCELLED) {
                            extPinpadListener.onCancel();
                        } else if (((NSDKException) e).getCode() == ErrorCode.TIMEOUT) {
                            extPinpadListener.onTimeout();
                        } else {
                            extPinpadListener.onError(((NSDKException) e).getCode(), e.getMessage());
                        }
                    } else {
                        extPinpadListener.onError(ErrorCode.EXT_ERROR, e.getMessage());
                    }
                }
            }
        });
    }

    @Override
    public void startOfflinePINEntry(final Key pinKey, final String pan, final int timeout, final ExtPINEntryParameters offlinePinInputParameter, final ExtPINEntryListener listener) throws NSDKException {
        if (offlinePinInputParameter == null || listener == null) {
            throw new NSDKIllegalParameterException("Parameter or listener is null !");
        }

        if (timeout <= 0 || timeout > 255) {
            throw new NSDKIllegalParameterException("Timeout should be >0 and <=255!");
        }

        NSDKExecutors.threadStart(new Runnable() {
            @Override
            public void run() {
                try {
                    if (offlinePinInputParameter.getPinLengthRange() != null) {
                        keyboardEntryModule.setValidPINLengthRange(offlinePinInputParameter.getPinLengthRange());
                    }
                    if (offlinePinInputParameter.getMaskLine() != null && offlinePinInputParameter.getPinMaskAlignment() == null) {
                        keyboardEntryModule.setPinLine((byte) offlinePinInputParameter.getMaskLine().getCode());
                    } else if (offlinePinInputParameter.getMaskLine() != null && offlinePinInputParameter.getPinMaskAlignment() != null) {
                        keyboardEntryModule.setPinLine((byte) offlinePinInputParameter.getMaskLine().getCode(), offlinePinInputParameter.getPinMaskAlignment().ordinal() + 1);
                    }
                    boolean isRandomKey = false;
                    if (offlinePinInputParameter instanceof ExtOfflinePINParameters) {
                        isRandomKey = ((ExtOfflinePINParameters)offlinePinInputParameter).isRandomProtectMode();
                    }
                    KeyboardEntryResult result = keyboardEntryModule.extendedPinEntry(pinKey, pan, offlinePinInputParameter, timeout, isRandomKey);
                    if (result != null) {
                        listener.onOfflineSuccess(result.getPinLen(), result.getEncryptedPinBlock(), result.getEncryptedRandomPinKey());
                    } else {
                        listener.onError(ErrorCode.EXT_PINPAD_ERROR, "Result is null.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (e instanceof NSDKException) {
                        if (((NSDKException) e).getCode() == ErrorCode.CANCELLED) {
                            listener.onCancel();
                        } else if (((NSDKException) e).getCode() == ErrorCode.TIMEOUT) {
                            listener.onTimeout();
                        } else {
                            listener.onError(((NSDKException) e).getCode(), e.getMessage());
                        }
                    } else {
                        listener.onError(ErrorCode.EXT_ERROR, e.getMessage());
                    }
                }
            }
        });
    }

    @Override
    public void startOfflinePINEntry(final RSAKey rsaKey, final int timeout, final ExtPINEntryParameters pinEntryParameters, final ExtPINEntryListener listener) throws NSDKException {
        if (pinEntryParameters == null) {
            throw new NSDKIllegalParameterException("ExtPINEntryParameters shall not be null");
        }
        if (timeout < 5 || timeout > 200) {
            throw new NSDKIllegalParameterException("Timeout shall range between 5 to 200 seconds.");
        }
        if (listener == null) {
            throw new NSDKIllegalParameterException("ExtPINEntryListener shall not be null.");
        }

        NSDKExecutors.threadStart(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] pinLengthRange = pinEntryParameters.getPinLengthRange();
                    if (pinLengthRange != null && pinLengthRange.length != 0) {
                        keyboardEntryModule.setValidPINLengthRange(pinLengthRange);
                    }
                    byte pinMaxLength = pinEntryParameters.getMaxPINLen();
                    String[] displayMessages = pinEntryParameters.getDisplayMessages();
                    keyboardEntryModule.verifyOfflinePIN(rsaKey, timeout, pinMaxLength, displayMessages, listener);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (e instanceof NSDKException) {
                        if (((NSDKException) e).getCode() == ErrorCode.CANCELLED) {
                            listener.onCancel();
                        } else if (((NSDKException) e).getCode() == ErrorCode.TIMEOUT) {
                            listener.onTimeout();
                        } else {
                            listener.onError(((NSDKException) e).getCode(), e.getMessage());
                        }
                    } else {
                        listener.onError(ErrorCode.EXT_ERROR, e.getMessage());
                    }
                }
            }
        });
    }

    @Override
    public void cancelPINEntry() throws NSDKException {
        keyboardEntryModule.cancelPinEntry();
    }
}
