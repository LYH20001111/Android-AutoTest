package com.newland.nsdk.core.external;

import com.newland.nsdk.core.api.common.crypto.AsymAlgorithmParameters;
import com.newland.nsdk.core.api.common.crypto.CipherOutput;
import com.newland.nsdk.core.api.common.crypto.CipherType;
import com.newland.nsdk.core.api.common.crypto.MACMode;
import com.newland.nsdk.core.api.common.crypto.MACOutput;
import com.newland.nsdk.core.api.common.crypto.MACType;
import com.newland.nsdk.core.api.common.crypto.PaddingMode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.keymanager.AsymmetricKey;
import com.newland.nsdk.core.api.common.keymanager.CipherMode;
import com.newland.nsdk.core.api.common.keymanager.DUKPTKey;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.external.crypto.ExtCrypto;
import com.newland.nsdk.core.external.command.ExternalCommandType;
import com.newland.nsdk.core.external.command.cipher.ExtMacBlockFlag;
import com.newland.nsdk.core.external.command.cipher.ExternalCipherModule;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.exception.ExternalErrorMessage;
import com.newland.nsdk.core.external.keymanager.KeySys;

import java.util.ArrayList;
import java.util.List;

public class ExtCryptoImpl implements ExtCrypto {
    private static final int MODE_ENCRYPT = 1;
    private static final int MODE_DECRYPT = 2;
    private static final int MAX_DATA_LENGTH = 4000;

    private ExternalCipherModule externalCipherModule;
    private volatile static ExtCryptoImpl instance;
    public static ExtCryptoImpl getInstance() {
        if (instance == null) {
            synchronized (ExtCryptoImpl.class) {
                if (instance == null) {
                    instance = new ExtCryptoImpl();
                }
            }
        }
        return instance;
    }
    private ExtCryptoImpl() {
        externalCipherModule = new ExternalCipherModule();
    }
    @Override
    public MACOutput generateMAC(byte keyId, MACType macType, byte[] iv, byte[] data) throws NSDKException {
        if (data == null || data.length == 0) {
            throw new NSDKIllegalParameterException(ExternalErrorMessage.DATA_NULL_OR_EMPTY);
        }

        if (macType == null ) {
            throw new NSDKIllegalParameterException("MAC type shall not be null.");
        }

        List<byte[]> dataList = splitData(data);
        int dataCount = dataList.size();
        MACOutput result = null;

        if (ExternalCommunicationManager.getInstance().getConfig().getCommandType() == ExternalCommandType.NAPI) {
            if (dataCount == 1) {
                return externalCipherModule.generateMacNapi(keyId, macType, iv, data, ExtMacBlockFlag.ONLY);
            }

            for(int i = 0; i < dataCount; i++) {
                if (i == 0) {
                    result = externalCipherModule.generateMacNapi(keyId, macType, iv, dataList.get(i), ExtMacBlockFlag.FIRST);
                } else if (i == dataCount - 1) {
                    result = externalCipherModule.generateMacNapi(keyId, macType, iv, dataList.get(i), ExtMacBlockFlag.LAST);
                } else {
                    result = externalCipherModule.generateMacNapi(keyId, macType, iv, dataList.get(i), ExtMacBlockFlag.NEXT);
                }
            }

            return result;
        }

        byte macMode = getMacMode(macType);
        byte keyType = getKeySystem(macType);
        // Key mode is reserved.
        byte keyMode = 1;
        if (dataCount == 1) {
            return externalCipherModule.generateMacNdk(keyId, keyType, macMode, (byte) ExtMacBlockFlag.ONLY.getCode(), data, keyMode, null);
        }

        for(int i = 0; i < dataCount; i++) {
            if (i == 0) {
                result = externalCipherModule.generateMacNdk(keyId, keyType, macMode, (byte) ExtMacBlockFlag.FIRST.getCode(), dataList.get(i), keyMode, null);
            } else if (i == dataCount - 1) {
                result = externalCipherModule.generateMacNdk(keyId, keyType, macMode, (byte) ExtMacBlockFlag.LAST.getCode(), dataList.get(i), keyMode, null);
            } else {
                result = externalCipherModule.generateMacNdk(keyId, keyType, macMode, (byte) ExtMacBlockFlag.NEXT.getCode(), dataList.get(i), keyMode, null);
            }
        }

        return result;
    }

    private List<byte[]> splitData(byte[] data) {
        List<byte[]> dataList = new ArrayList<>();
        int totalLen = data.length;
        int offset = 0;
        byte[] tempBuf;
        int tempBufLen;
        while (offset < totalLen) {
            if (totalLen - offset >= MAX_DATA_LENGTH) {
                tempBufLen = MAX_DATA_LENGTH;
            } else {
                tempBufLen = totalLen - offset;
            }
            tempBuf = new byte[tempBufLen];
            System.arraycopy(data, offset, tempBuf, 0, tempBufLen);
            dataList.add(tempBuf);
            offset += tempBufLen;
        }
        return dataList;
    }

    @Override
    public CipherOutput encrypt(SymmetricKey key, CipherType cipherType, PaddingMode paddingMode, byte[] iv, byte[] data) throws NSDKException {
        return calculateData(MODE_ENCRYPT, key, cipherType, paddingMode, iv, data);
    }

    @Override
    public CipherOutput decrypt(SymmetricKey key, CipherType cipherType, PaddingMode paddingMode, byte[] iv, byte[] data) throws NSDKException {
        return calculateData(MODE_DECRYPT, key, cipherType, paddingMode, iv, data);
    }

    @Override
    public byte[] encryptAsym(AsymmetricKey key, AsymAlgorithmParameters algorithmParameters, byte[] data) throws NSDKException {
        return calculateDataAsym(MODE_ENCRYPT, key, algorithmParameters, data);
    }

    @Override
    public byte[] decryptAsym(AsymmetricKey key, AsymAlgorithmParameters algorithmParameters, byte[] data) throws NSDKException {
        return calculateDataAsym(MODE_DECRYPT, key, algorithmParameters, data);
    }

    @Override
    public byte[] signAsym(AsymmetricKey key, AsymAlgorithmParameters algorithmParameters, byte[] hash) throws NSDKException {
        if (key == null || algorithmParameters == null || hash == null) {
            throw new NSDKIllegalParameterException("Key, hash and algorithm parameters shall not be null.");
        }

        if (key.getKeyType() == null || key.getKeyUsage() == null) {
            throw new NSDKIllegalParameterException("Key type and key usage shall not be null.");
        }

        if (algorithmParameters.getEncodingMode() == null
                || algorithmParameters.getMessageDigestType() == null) {
            throw new NSDKIllegalParameterException("Crypto mode ,encoding mode and message digest shall not be null.");
        }

        return externalCipherModule.signVerifyAsym(true,key,algorithmParameters,hash,null);
    }

    @Override
    public void verifyAsym(AsymmetricKey key, AsymAlgorithmParameters algorithmParameters, byte[] hash, byte[] signedData) throws NSDKException {
        if (key == null || algorithmParameters == null || hash == null || signedData == null) {
            throw new NSDKIllegalParameterException("Key, hash,signed data and algorithm parameters shall not be null.");
        }

        if (key.getKeyType() == null || key.getKeyUsage() == null) {
            throw new NSDKIllegalParameterException("Key type and key usage shall not be null.");
        }

        if ( algorithmParameters.getEncodingMode() == null
                || algorithmParameters.getMessageDigestType() == null) {
            throw new NSDKIllegalParameterException("Crypto mode ,encoding mode and message digest shall not be null.");
        }

        externalCipherModule.signVerifyAsym(false,key,algorithmParameters,hash,signedData);
    }

    @Override
    public byte[] getRandom(int len) throws NSDKException{
        if (len <= 0 || len > 2048) {
            throw new NSDKIllegalParameterException("Parameter should between 1 to 2048");
        }

        return externalCipherModule.getRandeom(len);
    }

    private CipherOutput calculateData(int mode, SymmetricKey key, CipherType cipherType, PaddingMode paddingMode, byte[] iv, byte[] data) throws NSDKException {
        if (key == null || cipherType == null || data == null) {
            throw new NSDKIllegalParameterException("Key, cipher type, data shall not be null!");
        }

        if (ExternalCommunicationManager.getInstance().getConfig().getCommandType() == ExternalCommandType.NAPI) {
            if (mode == MODE_ENCRYPT) {
                return externalCipherModule.encryptOrDecryptNapi((byte) 0, key, cipherType, paddingMode, iv, data);
            }

            return externalCipherModule.encryptOrDecryptNapi((byte) 1, key, cipherType, paddingMode, iv, data);
        }

        CipherMode cipherMode = CipherType.getCipherMode(cipherType);
        KeyType keyType = CipherType.getKeyType(cipherType);
        if (cipherMode == null) {
            throw new NSDKIllegalParameterException(String.format("Unsupported cipher mode, please check cipher type(%s)", cipherType));
        }
        if (cipherMode == CipherMode.CBC && iv == null) {
            throw new NSDKIllegalParameterException("IV shall not be null when cipher mode is CBC!");
        }

        CipherOutput out = null;
        if (keyType == KeyType.DES) {
            byte keyMode;
            if (cipherMode == CipherMode.CBC) {
                if (mode == MODE_ENCRYPT) {
                    keyMode = 1;
                } else {
                    keyMode = 3;
                }
            } else if (cipherMode == CipherMode.ECB) {
                if (mode == MODE_ENCRYPT) {
                    keyMode = 2;
                } else {
                    keyMode = 4;
                }
            } else {
                throw new NSDKIllegalParameterException("Only supports CBC and ECB decryption now.");
            }

            if (key instanceof DUKPTKey) {
                // Only support TDK(4) type
                byte dukptKeyType = 4;
                out = externalCipherModule.dukptEncryptOrDecryptNdk(keyMode, key.getKeyID(), dukptKeyType, iv, data);
            } else {
                // Protect key is not supported yet, set protect key mode to ECB by default
                byte protectKeyMode = 1;
                byte[] dataResult = externalCipherModule.encryptOrDecryptNdk(key.getKeyID(), keyMode, iv, data, protectKeyMode, null);
                out = new CipherOutput(dataResult, null);
            }
        } else if (keyType == KeyType.AES) {
            byte[] dataResult;
            if (mode == MODE_ENCRYPT) {
                dataResult = externalCipherModule.aesEncryptOrDecryptNdk((byte) 1, key.getKeyID(), data);
            } else {
                dataResult = externalCipherModule.aesEncryptOrDecryptNdk((byte) 2, key.getKeyID(), data);
            }
            out = new CipherOutput(dataResult, null);
        } else {
            throw new NSDKIllegalParameterException(String.format("Unsupported key type(%s) for decryption.", key.getKeyType()));
        }

        return out;
    }

    private byte[] calculateDataAsym(int mode, AsymmetricKey key, AsymAlgorithmParameters algorithmParameters, byte[] data) throws NSDKException {
        if (key == null || data == null || algorithmParameters == null) {
            throw new NSDKIllegalParameterException("Key, data and algorithm parameters shall not be null.");
        }

        if (key.getKeyType() == null || key.getKeyUsage() == null) {
            throw new NSDKIllegalParameterException("Key type and key usage shall not be null.");
        }

        if (algorithmParameters.getCryptoMode() == null || algorithmParameters.getEncodingMode() == null
                || algorithmParameters.getMessageDigestType() == null) {
            throw new NSDKIllegalParameterException("Crypto mode ,encoding mode and message digest shall not be null.");
        }

        byte calculateMode = (byte) (mode == MODE_ENCRYPT ? 0 : 1);
        //做ndk和napi的适配,ndk暂时不实现
        if (ExternalCommunicationManager.getInstance().getConfig().getCommandType() == ExternalCommandType.NAPI) {
            return externalCipherModule.asymEncryptOrDecryptNapi(calculateMode, key, algorithmParameters.getMessageDigestType(), algorithmParameters.getEncodingMode(), algorithmParameters.getCryptoMode(), data);
        }

        return externalCipherModule.asymEncryptOrDecryptNdk(calculateMode, key.getKeyID(), data);
    }

    private byte getKeySystem(MACType macType) throws NSDKException {
        KeySys keyType = null;

        switch (macType) {
            case TDES_LAST:
            case TDES_X99:
            case TDES_X919:
            case TDES_UNIONPAY_ECB:
                keyType = KeySys.MKSK;
                break;
            case DUKPT_LAST:
            case DUKPT_RESP_LAST:
            case DUKPT_X99:
            case DUKPT_RESP_X99:
            case DUKPT_X919:
            case DUKPT_RESP_X919:
            case DUKPT_UNIONPAY_ECB:
            case DUKPT_RESP_UNIONPAY_ECB:
                keyType = KeySys.DUKPT;
                break;
            case AES_LAST:
            case AES_DUKPT_LAST:
            case AES_DUKPT_X919:
            case AES_X99:
            case AES_DUKPT_X99:
            case AES_DUKPT_UNIONPAY_ECB:
                keyType = KeySys.AES;
                break;
            default:
                break;
        }

        if (keyType == null) {
            throw new NSDKIllegalParameterException(String.format("Unsupported key type, please check MAC type(%s).", macType));
        }

        return (byte) keyType.ordinal();
    }

    private byte getMacMode(MACType macType) throws NSDKException {
        MACMode macMode = null;
        switch (macType) {
            case TDES_X99:
            case DUKPT_X99:
                macMode = MACMode.X99;
                break;
            case TDES_X919:
            case DUKPT_X919:
                macMode = MACMode.X919;
                break;
            case TDES_UNIONPAY_ECB:
            case DUKPT_UNIONPAY_ECB:
                macMode = MACMode.UNIONPAY_ECB;
                break;
            case TDES_LAST:
            case DUKPT_LAST:
                macMode = MACMode.LAST;
                break;
            case AES_X99:
                macMode = MACMode.AES;
                break;
            default:
                break;
        }
        if (macMode == null) {
            throw new NSDKIllegalParameterException(String.format("Unsupported MAC mode, please check MAC type(%s).", macType));
        }
        switch (macMode) {
            case X99:
                return 0;
            case X919:
                return 1;
            case UNIONPAY_ECB:
                return 2;
            case LAST:
                return 3;
            case AES:
                return 5;
            default:
                throw new NSDKIllegalParameterException(String.format("Unsupported MAC mode(%s), please check MAC type(%s).", macMode, macType));
        }
    }
}
