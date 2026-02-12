package com.newland.nsdk.core.external;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.cardreader.CardReaderListener;
import com.newland.nsdk.core.api.common.cardreader.CardReaderParameters;
import com.newland.nsdk.core.api.common.cardreader.CardType;
import com.newland.nsdk.core.api.common.crypto.CipherType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.keymanager.CipherMode;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.api.external.cardreader.ExtCardReader;
import com.newland.nsdk.core.api.external.cardreader.ExtCardReaderParameters;
import com.newland.nsdk.core.common.NSDKExecutors;
import com.newland.nsdk.core.external.command.cardreader.DetectedCardInfo;
import com.newland.nsdk.core.external.command.cardreader.ExternalCardReaderModule;
import com.newland.nsdk.core.external.command.contactlesscard.ExternalContactlessCardModule;
import com.newland.nsdk.core.external.command.exception.ExternalErrorMessage;
import com.newland.nsdk.core.external.command.smartcard.ExternalSmartCardModule;

/**
 * @author hlh
 * @date 2020/7/14
 */
public class ExtCardReaderImpl implements ExtCardReader {
    private ExternalCardReaderModule externalCardReaderModule;
    private ExternalSmartCardModule externalSmartCardModule;
    protected ExternalContactlessCardModule externalContactlessCardModule;
    private volatile static ExtCardReaderImpl instance;
    public static ExtCardReaderImpl getInstance() {
        if (instance == null) {
            synchronized (ExtCardReaderImpl.class) {
                if (instance == null) {
                    instance = new ExtCardReaderImpl();
                }
            }
        }
        return instance;
    }
    private ExtCardReaderImpl() {
        externalCardReaderModule = new ExternalCardReaderModule();
        externalSmartCardModule = new ExternalSmartCardModule();
        externalContactlessCardModule = new ExternalContactlessCardModule();
    }

    /**
     * Open the card reader by non-blocking way<p>
     *
     * @param parameter
     * @param cardReaderListener Non-blocked listener{@link CardReaderListener}
     */
    @Override
    public void openCardReader(final CardType[] cardTypes, final int timeout, final CardReaderParameters parameter, final CardReaderListener cardReaderListener) throws NSDKException {
        if (cardTypes == null || cardTypes.length == 0) {
            throw new NSDKIllegalParameterException("Please set what cards to search.");
        }

        if (cardReaderListener == null) {
            throw new NSDKIllegalParameterException("Listener should not be null!");
        }

        if (timeout < 0) {
            throw new NSDKIllegalParameterException("Timeout shall not be less than 0.");
        }

        if (parameter == null) {
            throw new NSDKIllegalParameterException("Card reader parameter is null.");
        }

        if (parameter instanceof ExtCardReaderParameters) {
            ExtCardReaderParameters tempParameters = (ExtCardReaderParameters) parameter;

            if (tempParameters.getPANKeyIndex() != 0) {
                CipherType cipherType = tempParameters.getCipherType();

                if (cipherType != null) {
                    KeyType keyType = CipherType.getKeyType(cipherType);
                    if (keyType == null || (keyType != KeyType.AES && keyType != KeyType.DES)) {
                        throw new NSDKIllegalParameterException(String.format("Unsupported PAN key type(%s)", cipherType));
                    }
                    CipherMode cipherMode = CipherType.getCipherMode(cipherType);
                    if (cipherMode == null || (cipherMode != CipherMode.ECB && cipherMode != CipherMode.CBC)) {
                        throw new NSDKIllegalParameterException(String.format("Unsupported key mode(%s)", cipherType));
                    }
                    if (cipherMode == CipherMode.CBC && tempParameters.getIV() == null) {
                        throw new NSDKIllegalParameterException(ExternalErrorMessage.EMPTY_IV);
                    }
                }
            }
        }

        NSDKExecutors.threadStart(new Runnable() {
            @Override
            public void run() {
                try {
                    DetectedCardInfo info = externalCardReaderModule.searchCard(cardTypes, timeout, parameter);
                    if (info.getCardType() == CardType.CONTACT_CARD) {
                        cardReaderListener.onFindContactCard();
                    } else if (info.getCardType() == CardType.CONTACTLESS_CARD) {
                        cardReaderListener.onFindContactlessCard(info.getContactlessCardType(), info.getContactlessCardInfo());
                    } else {
                        cardReaderListener.onFindMagCard(info.getMagCardInfo());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (e instanceof NSDKException) {
                        if (((NSDKException) e).getCode() == ErrorCode.TIMEOUT) {
                            cardReaderListener.onTimeout();
                        } else if (((NSDKException) e).getCode() == ErrorCode.CANCELLED) {
                            cardReaderListener.onCancel();
                        } else {
                            cardReaderListener.onError(((NSDKException) e).getCode(), null);
                        }
                    } else {
                        cardReaderListener.onError(ErrorCode.EXT_ERROR, "Failed to open external card reader.");
                    }
                }
            }
        });
    }

    /**
     * Cancel the current card reading <p>
     */
    @Override
    public void cancelCardReader() throws NSDKException {
        externalCardReaderModule.cancelSearch();
    }

    @Override
    public boolean isCardInserted() throws NSDKException{
        boolean res;
        try {
            externalSmartCardModule.checkCard(0);
            res = true;
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof NSDKException) {
                if (((NSDKException) e).getCode() == ErrorCode.TIMEOUT) {
                    res = false;
                } else {
                    throw e;
                }
            } else {
                throw new NSDKException(ErrorCode.EXT_ERROR, "Unexpected error.", e);
            }
        }

        return res;
    }

    @Override
    public boolean isCardPresent() throws NSDKException{
        return externalContactlessCardModule.checkCard(100);
    }
}
