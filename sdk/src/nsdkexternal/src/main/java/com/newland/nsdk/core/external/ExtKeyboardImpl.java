package com.newland.nsdk.core.external;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.crypto.AlgorithmParameters;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.keymanager.Key;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.external.keyboard.AmountListener;
import com.newland.nsdk.core.api.external.keyboard.AmountParameters;
import com.newland.nsdk.core.api.external.keyboard.AmountType;
import com.newland.nsdk.core.api.external.keyboard.ExtKeyboard;
import com.newland.nsdk.core.api.external.keyboard.InputItem;
import com.newland.nsdk.core.api.external.keyboard.InputListener;
import com.newland.nsdk.core.api.external.keyboard.InputParameters;
import com.newland.nsdk.core.api.external.keyboard.KeyboardListener;
import com.newland.nsdk.core.api.external.keyboard.KeyboardParameters;
import com.newland.nsdk.core.common.NSDKExecutors;
import com.newland.nsdk.core.external.command.keyboard.ExternalKeyboardEntryModule;
import com.newland.nsdk.core.external.command.keyboard.KeyboardEntryResult;

/**
 * @author hlh
 * @date 2020/7/8
 */
public class ExtKeyboardImpl implements ExtKeyboard {
    private ExternalKeyboardEntryModule externalKeyboardEntryModule;
    private volatile static ExtKeyboardImpl instance;
    public static ExtKeyboardImpl getInstance() {
        if (instance == null) {
            synchronized (ExtKeyboardImpl.class) {
                if (instance == null) {
                    instance = new ExtKeyboardImpl();
                }
            }
        }
        return instance;
    }
    private ExtKeyboardImpl() {
        externalKeyboardEntryModule = new ExternalKeyboardEntryModule();
    }

    @Override
    public void startKeyEntry(final Key dataKey, final int timeout, final KeyboardParameters parameter, final KeyboardListener keyboardListener) throws NSDKException {
        if (parameter == null) {
            throw new NSDKIllegalParameterException("Keyboard parameters should not be null!");
        }

        if (keyboardListener == null) {
            throw new NSDKIllegalParameterException("Keyboard listener should not be null!");
        }

        if (parameter.getKeyboardMode() == null || parameter.getPromptID() == null) {
            throw new NSDKIllegalParameterException("Please set keyboard mode and prompt ID.");
        }

        NSDKExecutors.threadStart(new Runnable() {
            @Override
            public void run() {
                try {
                    KeyboardEntryResult res = externalKeyboardEntryModule.sensitiveDataEntry(dataKey,timeout,parameter);
                    keyboardListener.onSuccess(res.getDataLen(), res.getEncryptedData());
                } catch (Exception e) {
                    e.printStackTrace();
                    if (e instanceof NSDKException) {
                        if (((NSDKException) e).getCode() == ErrorCode.TIMEOUT) {
                            keyboardListener.onTimeout();
                        } else if (((NSDKException) e).getCode() == ErrorCode.CANCELLED) {
                            keyboardListener.onCancel();
                        } else {
                            keyboardListener.onError(((NSDKException) e).getCode(), e.getMessage());
                        }
                    } else {
                        keyboardListener.onError(ErrorCode.EXT_ERROR, e.getMessage());
                    }
                }
            }
        });
    }

    @Override
    public void startKeyEntry(final SymmetricKey key, final AlgorithmParameters params, final int timeout, final KeyboardParameters parameter, final KeyboardListener keyboardListener) throws NSDKException {
        if (parameter == null) {
            throw new NSDKIllegalParameterException("Keyboard parameters should not be null!");
        }

        if (keyboardListener == null) {
            throw new NSDKIllegalParameterException("Keyboard listener should not be null!");
        }

        if (parameter.getKeyboardMode() == null || parameter.getPromptID() == null) {
            throw new NSDKIllegalParameterException("Please set keyboard mode and prompt ID.");
        }

        NSDKExecutors.threadStart(new Runnable() {
            @Override
            public void run() {
                try {
                    KeyboardEntryResult res = externalKeyboardEntryModule.sensitiveDataEntry(key, params, timeout, parameter);
                    keyboardListener.onSuccess(res.getDataLen(), res.getEncryptedData());
                } catch (Exception e) {
                    e.printStackTrace();
                    if (e instanceof NSDKException) {
                        if (((NSDKException) e).getCode() == ErrorCode.TIMEOUT) {
                            keyboardListener.onTimeout();
                        } else if (((NSDKException) e).getCode() == ErrorCode.CANCELLED) {
                            keyboardListener.onCancel();
                        } else {
                            keyboardListener.onError(((NSDKException) e).getCode(), e.getMessage());
                        }
                    } else {
                        keyboardListener.onError(ErrorCode.EXT_ERROR, e.getMessage());
                    }
                }
            }
        });
    }

    @Override
    public void cancelKeyEntry() throws NSDKException {
        externalKeyboardEntryModule.cancelPinEntry();
    }

    @Override
    public void inputData(InputItem[] inputItems, InputParameters parameters, InputListener listener) throws NSDKException {
        if (inputItems == null || inputItems.length == 0) {
            throw new NSDKIllegalParameterException("Input items should not be null.");
        }
        if (listener == null) {
            throw new NSDKIllegalParameterException("Input listener shall not be null.");
        }

        externalKeyboardEntryModule.inputData(inputItems, parameters, listener);
    }

    @Override
    public void inputAmount(AmountType amountType, AmountParameters parameters, int timeout, AmountListener amountListener) throws NSDKException {
        if (amountType == null) {
            throw new NSDKIllegalParameterException("AmountType shall not be null.");
        }
        if (parameters == null) {
            throw new NSDKIllegalParameterException("AmountParameters shall not be null.");
        }
        if (timeout <= 0) {
            throw new NSDKIllegalParameterException("Timeout shall be > 0.");
        }
        if (amountListener == null) {
            throw new NSDKIllegalParameterException("Amount listener shall not be null.");
        }

        externalKeyboardEntryModule.inputAmount(amountType, parameters, timeout, amountListener);
    }
}
