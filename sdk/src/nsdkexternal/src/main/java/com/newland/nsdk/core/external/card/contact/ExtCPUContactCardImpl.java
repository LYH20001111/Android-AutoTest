package com.newland.nsdk.core.external.card.contact;

import com.newland.nsdk.core.api.common.card.contact.ContactCardSlot;
import com.newland.nsdk.core.api.common.card.contact.ContactCardType;
import com.newland.nsdk.core.api.common.crypto.AlgorithmParameters;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.keymanager.CipherMode;
import com.newland.nsdk.core.api.common.keymanager.Key;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.external.card.ExtAPDUOutput;
import com.newland.nsdk.core.api.external.card.contact.ExtCPUContactCard;
import com.newland.nsdk.core.external.command.smartcard.ExternalSmartCardModule;

public class ExtCPUContactCardImpl implements ExtCPUContactCard {
    private ExtContactCardImpl extContactCard;
    private ExternalSmartCardModule externalContactCardCommand;
    public ExtCPUContactCardImpl(ContactCardSlot slot) {
        extContactCard = new ExtContactCardImpl(slot, ContactCardType.CPU);
        this.externalContactCardCommand = new ExternalSmartCardModule();
    }

    @Override
    public ExtAPDUOutput performAPDU(Key key, AlgorithmParameters algorithmParameters, byte[] command) throws NSDKException {
        if (command == null) {
            throw new NSDKIllegalParameterException("Key and command data shall not be null!");
        }

        if (key == null) {
            key = new SymmetricKey();
            key.setKeyID((byte) 0);
            ((SymmetricKey)key).setKeyType(KeyType.DES);
        }

        byte[] iv = null;
        byte keyType = 0;
        byte keyMode = 0;
        CipherMode cipherMode;
        if (key.getKeyID() != 0) {
            if (algorithmParameters == null) {
                algorithmParameters = new AlgorithmParameters();
            }

            if (algorithmParameters.getCipherMode() == null) {
                algorithmParameters.setCipherMode(CipherMode.ECB);
            }

            cipherMode = algorithmParameters.getCipherMode();
            iv = algorithmParameters.getIV();
            if (key instanceof SymmetricKey) {
                KeyType type = ((SymmetricKey)key).getKeyType();
                if (type != null) {
                    if (type == KeyType.DES) {
                        keyType = 0;
                    } else if (type == KeyType.AES) {
                        keyType = 1;
                    } else {
                        throw new NSDKIllegalParameterException("Only support AES and DES keys to encrypt command data.");
                    }
                }
            } else {
                throw new NSDKIllegalParameterException("Only support symmetric key now.");
            }

            if (cipherMode == CipherMode.ECB) {
                keyMode = 0;
            } else if (cipherMode == CipherMode.CBC) {
                keyMode = 1;
            } else {
                throw new NSDKIllegalParameterException("Only support ECB and DES now.");
            }
        }

        return externalContactCardCommand.exchangeAPDU(key.getKeyID(), keyType, keyMode, iv, command);
    }

    @Override
    public ExtAPDUOutput performAPDU(Key key, AlgorithmParameters algorithmParameters, int actualLen, byte[] command) throws NSDKException {
        if (command == null || command.length == 0) {
            throw new NSDKIllegalParameterException("Key and command data shall not be null!");
        }
        if(actualLen <= 0 || actualLen > command.length) {
            throw new NSDKIllegalParameterException("The actual length error!");
        }
        if (key == null) {
            key = new SymmetricKey();
            key.setKeyID((byte) 0);
            ((SymmetricKey)key).setKeyType(KeyType.DES);
        }

        if(key.getKeyID() == 0) {
            if(actualLen != command.length) {
                throw new NSDKIllegalParameterException("The actual length error!");
            }
        }

        byte[] iv = null;
        byte keyType = 0;
        byte keyMode = 0;
        CipherMode cipherMode;
        if (key.getKeyID() != 0) {
            if (algorithmParameters == null) {
                algorithmParameters = new AlgorithmParameters();
            }

            if (algorithmParameters.getCipherMode() == null) {
                algorithmParameters.setCipherMode(CipherMode.ECB);
            }

            cipherMode = algorithmParameters.getCipherMode();
            iv = algorithmParameters.getIV();

            if (key instanceof SymmetricKey) {
                KeyType type = ((SymmetricKey)key).getKeyType();
                if (type != null) {
                    if (type == KeyType.DES) {
                        keyType = 0;
                    } else if (type == KeyType.AES) {
                        keyType = 1;
                    } else {
                        throw new NSDKIllegalParameterException("Only support AES and DES keys to encrypt command data.");
                    }
                }
            } else {
                throw new NSDKIllegalParameterException("Only support symmetric key now.");
            }

            if (cipherMode == CipherMode.ECB) {
                keyMode = 0;
            } else if (cipherMode == CipherMode.CBC) {
                keyMode = 1;
                if(iv == null || iv.length == 0) {
                    throw new NSDKIllegalParameterException("CBC Mode need IV.");
                }
            } else {
                throw new NSDKIllegalParameterException("Only support ECB and DES now.");
            }
        }

        return externalContactCardCommand.exchangeAPDU(key.getKeyID(), keyType, keyMode, iv, actualLen, command);
    }

    @Override
    public byte[] powerUp() throws NSDKException {
        return this.extContactCard.powerUp();
    }

    @Override
    public void powerDown() throws NSDKException {
        this.extContactCard.powerDown();
    }
}
