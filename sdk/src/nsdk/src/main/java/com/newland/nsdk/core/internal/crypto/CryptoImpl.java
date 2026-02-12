package com.newland.nsdk.core.internal.crypto;

import android.text.TextUtils;
import android.util.Log;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.crypto.AsymAlgorithmParameters;
import com.newland.nsdk.core.api.common.crypto.AsymEncodingMode;
import com.newland.nsdk.core.api.common.crypto.CSRFileType;
import com.newland.nsdk.core.api.common.crypto.CSRParameters;
import com.newland.nsdk.core.api.common.crypto.CipherOutput;
import com.newland.nsdk.core.api.common.crypto.CipherParameters;
import com.newland.nsdk.core.api.common.crypto.CipherType;
import com.newland.nsdk.core.api.common.crypto.CryptogramInfo;
import com.newland.nsdk.core.api.common.crypto.GCMCipherOut;
import com.newland.nsdk.core.api.common.crypto.MACOutput;
import com.newland.nsdk.core.api.common.crypto.MACType;
import com.newland.nsdk.core.api.common.crypto.MessageDigestType;
import com.newland.nsdk.core.api.common.crypto.PaddingMode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.keymanager.AsymmetricKey;
import com.newland.nsdk.core.api.common.keymanager.DUKPTDerivateKey;
import com.newland.nsdk.core.api.common.keymanager.Key;
import com.newland.nsdk.core.api.common.keymanager.KeyUsage;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.internal.crypto.Crypto;
import com.newland.nsdk.core.api.internal.exception.NSDKNDKException;
import com.newland.nsdk.core.common.keymanager.ST_SEC_ASYM_KEYIN_DATA;
import com.newland.nsdk.core.internal.jni.NSDKJni;
import com.newland.nsdk.core.internal.keymanager.ST_SEC_KEYIN_DATA;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * The interface CipherImpl spi.
 */
public class CryptoImpl implements Crypto {
    private static final String TAG = "CipherImpl";
    private static final int MODE_ENCRYPT = 1;
    private static final int MODE_DECRYPT = 2;
    private static final int NOT_INITIALED_CSR_HANDLE = -11;

    public boolean isSupported;

    private volatile static CryptoImpl instance;

    public static CryptoImpl getInstance(boolean isSupported) {
        if (instance == null) {
            synchronized (CryptoImpl.class) {
                if (instance == null || instance.isSupported != isSupported) {
                    instance = new CryptoImpl(isSupported);
                }
            }
        } else {
            if (instance.isSupported != isSupported) {
                instance = new CryptoImpl(isSupported);
            }
        }
        return instance;
    }

    private CryptoImpl(){
        this.isSupported = true;
    }

    private CryptoImpl(boolean isSupported){
        this.isSupported = isSupported;
    }

    private void isSupported() throws NSDKException {
        if(!isSupported){
            throw new NSDKException(ErrorCode.UNSUPPORTED, "UnSupported Crypto Module");
        }
    }

    @Override
    public CipherOutput encrypt(SymmetricKey key, CipherType cipherType, PaddingMode paddingMode, byte[] iv, byte[] data) throws NSDKException {
        isSupported();

        return calculate(MODE_ENCRYPT, key, cipherType, paddingMode, iv, data);
    }

    @Override
    public CipherOutput encrypt(SymmetricKey key, CipherParameters cipherParameters, byte[] data) throws NSDKException {
        isSupported();
        if (cipherParameters == null) {
            throw new NSDKIllegalParameterException("Cipher parameters shall not be null.");
        }
        CipherType cipherType = cipherParameters.getCipherType();
        if (cipherType == null) {
            throw new NSDKIllegalParameterException("Cipher type shall not be null.");
        }
        if (cipherType.name().contains("GCM")) {
            return calculateGCM(MODE_ENCRYPT, key, cipherType, cipherParameters.getPaddingMode(), cipherParameters.getIv(), data, cipherParameters.getAuthTag(), cipherParameters.getAuthTagLen(), cipherParameters.getAuthData());
        } else {
            return calculate(MODE_ENCRYPT, key, cipherType, cipherParameters.getPaddingMode(), cipherParameters.getIv(), data);
        }
    }

    @Override
    public CipherOutput decrypt(SymmetricKey key, CipherType cipherType, PaddingMode paddingMode, byte[] iv, byte[] data) throws NSDKException {
        isSupported();

        return calculate(MODE_DECRYPT, key, cipherType, paddingMode, iv, data);
    }

    @Override
    public CipherOutput decrypt(SymmetricKey key, CipherParameters cipherParameters, byte[] data) throws NSDKException {
        isSupported();
        if (cipherParameters == null) {
            throw new NSDKIllegalParameterException("Cipher parameters shall not be null.");
        }
        CipherType cipherType = cipherParameters.getCipherType();
        if (cipherType == null) {
            throw new NSDKIllegalParameterException("Cipher type shall not be null.");
        }
        if (cipherType.name().contains("GCM")) {
            return calculateGCM(MODE_DECRYPT, key, cipherType, cipherParameters.getPaddingMode(), cipherParameters.getIv(), data, cipherParameters.getAuthTag(), cipherParameters.getAuthTagLen(), cipherParameters.getAuthData());
        } else {
            return calculate(MODE_DECRYPT, key, cipherType, cipherParameters.getPaddingMode(), cipherParameters.getIv(), data);
        }
    }

    @Override
    public byte[] getRandom(int len) throws NSDKException {
        isSupported();

        if (len < 0) {
            throw new NSDKIllegalParameterException("Random data length shall not be less than 0");
        }
        int ret;
        byte[] random = new byte[len];

        ret = NSDKJni.getInstance().NAPI_SecGetRandom(random.length, random);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to get random, result code = %d.", ret));
        }

        return random;
    }

    @Override
    public void initCSR(CSRParameters parameters) throws NSDKException {
        if (parameters == null) {
            throw new NSDKIllegalParameterException("CSR parameters shall not be null.");
        }
        int ret = NSDKJni.getInstance().NAPI_SecCSRInit();
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to init CSR handle[%d].", ret));
        }

        String userName = parameters.getUserName();
        if (TextUtils.isEmpty(userName)) {
            releaseCSR();
            throw new NSDKIllegalParameterException("User name shall not be null.");
        }
        AsymmetricKey asymmetricKey = parameters.getAsymmetricKey();
        if (asymmetricKey == null) {
            releaseCSR();
            throw new NSDKIllegalParameterException("Asymmetric key shall not be null.");
        }
        if (asymmetricKey.getKeyUsage() == null || asymmetricKey.getKeyType() == null) {
            releaseCSR();
            throw new NSDKIllegalParameterException("Asymmetric key type and usage shall not be null.");
        }
        MessageDigestType messageDigestType = parameters.getMessageDigestType();
        if (messageDigestType == null) {
            releaseCSR();
            throw new NSDKIllegalParameterException("Message digest type shall not be null.");
        }

        ST_SEC_ASYM_KEYIN_DATA stSecAsymKeyinData = new ST_SEC_ASYM_KEYIN_DATA();
        stSecAsymKeyinData.setUcKeyIdx(asymmetricKey.getKeyID());
        stSecAsymKeyinData.setKeyType(asymmetricKey.getKeyType().getCode());
        stSecAsymKeyinData.setKeyUsage(asymmetricKey.getKeyUsage().getCode());
        stSecAsymKeyinData.setMdAlg(messageDigestType.ordinal());
        stSecAsymKeyinData.setKEKUsage(parameters.getKeyUsage());
        ret = NSDKJni.getInstance().NAPI_SecCSRSetParameters(stSecAsymKeyinData, parameters.getCertType(), parameters.isCA(), parameters.getUserName());
        if (ret == NOT_INITIALED_CSR_HANDLE) {
            throw new NSDKException("No initialed CSR handle, please init CSR first.");
        }
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to set CSR parameters[%d]", ret));
        }
        //set extension information
        List<byte[]> oidList = parameters.getOidList();
        List<byte[]> valueList = parameters.getValueList();
        if (oidList != null && valueList != null) {
            if (oidList.size() != valueList.size()) {
                releaseCSR();
                throw new NSDKIllegalParameterException("Oid and Value shall be one-to-one correspondence.");
            }
            for (int i = 0; i < oidList.size(); i++) {
                byte[] oid = oidList.get(i);
                byte[] value = valueList.get(i);
                ret = NSDKJni.getInstance().NAPI_SecCSRSetExtension(oid, oid.length, value, value.length);
                if (ret == ErrorCode.PARAM_ERROR) {
                    throw new NSDKIllegalParameterException();
                }
                if (ret != ErrorCode.OK) {
                    throw new NSDKException(ret, String.format(Locale.US, "Failed to set extension info[%d]", ret));
                }
            }
        }
    }

    @Override
    public byte[] generateCSR(CSRFileType fileType) throws NSDKException {
        if (fileType == null) {
            throw new NSDKIllegalParameterException("File type shall not be null.");
        }

        int type = fileType == CSRFileType.PEM ? 0 : 1;
        byte[] data = new byte[4096];
        int[] dataLen = new int[1];
        int ret = NSDKJni.getInstance().NAPI_SecCSRGen(type, data, dataLen);
        if (ret == NOT_INITIALED_CSR_HANDLE) {
            throw new NSDKException("No initialed CSR handle, please init CSR first.");
        }
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to generate CSR[%d]", ret));
        }
        byte[] result = null;
        int len = dataLen[0];
        if (len > 0) {
            result = new byte[len];
            System.arraycopy(data, 0, result, 0, len);
        }
        return result;
    }

    @Override
    public void releaseCSR() throws NSDKException {
        int ret = NSDKJni.getInstance().NAPI_SecCSRRelease();
        if (ret == NOT_INITIALED_CSR_HANDLE) {
            LogUtils.e(TAG, "No initialed CSR handle");
            return;
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to release CSR[%d]", ret));
        }
    }

    @Override
    public MACOutput generateMAC(byte keyId, MACType macType, byte[] iv, byte[] dataIn) throws NSDKException {
        isSupported();

        int keyIdInt = keyId & 0xFF;
        if (macType == null || dataIn == null) {
            throw new NSDKIllegalParameterException("MAC type and data shall not be null.");
        }

        int ivLen = ((iv == null) ? 0 : iv.length);

        byte[] outData = new byte[256];
        int[] outDataLen = new int[1];

        byte[] ksnData = new byte[32];
        int[] ksnDataLen = new int[1];

        int ret = NSDKJni.getInstance().NAPI_SecGenerateMAC(macType.getCode(), keyIdInt, iv, ivLen, dataIn, dataIn.length, null, 0, outData, outDataLen, ksnData, ksnDataLen);
        byte[] data = null;
        byte[] ksn = null;
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, "Failed to generate mac.");
        }

        if (outDataLen[0] > 0) {
            data = new byte[outDataLen[0]];
            System.arraycopy(outData, 0, data, 0, data.length);
        }
        if (ksnDataLen[0] > 0) {
            ksn = new byte[ksnDataLen[0]];
            System.arraycopy(ksnData, 0, ksn, 0, ksn.length);
        }
        return new MACOutput(data, ksn);
    }

    @Override
    public MACOutput generateMAC(DUKPTDerivateKey key, MACType macType, byte[] iv, byte[] dataIn) throws NSDKException {
        isSupported();

        if (key == null || macType == null || dataIn == null) {
            throw new NSDKIllegalParameterException("Key, MAC type and data shall not be null.");
        }

        if (key.getDerivateKeyType() == null || key.getDerivateUsage() == null) {
            throw new NSDKIllegalParameterException("Derivate key type and usage shall not be null.");
        }

        ST_SEC_DUKPT_DERIVATE_DATA dukptDerivateData = new ST_SEC_DUKPT_DERIVATE_DATA();
        dukptDerivateData.setDerivateKeyType(key.getDerivateKeyType().getCode());
        dukptDerivateData.setDerivateKeyUsage(key.getDerivateUsage().ordinal());
        dukptDerivateData.setDerivateKeyLen(key.getDerivateKeyLen());

        int keyIdInt = key.getKeyID() & 0xFF;
        int ivLen = ((iv == null) ? 0 : iv.length);

        byte[] outData = new byte[256];
        int[] outDataLen = new int[1];

        byte[] ksnData = new byte[32];
        int[] ksnDataLen = new int[1];

        int ret = NSDKJni.getInstance().NAPI_SecGenerateMAC_DerivateKey(macType.getCode(), keyIdInt, iv, ivLen, dataIn, dataIn.length, dukptDerivateData, outData, outDataLen, ksnData, ksnDataLen);
        byte[] data = null;
        byte[] ksn = null;
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, "Failed to generate mac.");
        }

        if (outDataLen[0] > 0) {
            data = new byte[outDataLen[0]];
            System.arraycopy(outData, 0, data, 0, data.length);
        }
        if (ksnDataLen[0] > 0) {
            ksn = new byte[ksnDataLen[0]];
            System.arraycopy(ksnData, 0, ksn, 0, ksn.length);
        }
        return new MACOutput(data, ksn);
    }

    @Override
    public byte[] encryptAsym(AsymmetricKey key, AsymAlgorithmParameters algorithmParameters, byte[] data) throws NSDKException {
        isSupported();

        return calculateAsym(MODE_ENCRYPT, key, algorithmParameters, data);
    }

    @Override
    public byte[] decryptAsym(AsymmetricKey key, AsymAlgorithmParameters algorithmParameters, byte[] data) throws NSDKException {
        isSupported();

        return calculateAsym(MODE_DECRYPT, key, algorithmParameters, data);
    }

    @Override
    public byte[] signAsym(AsymmetricKey key, AsymAlgorithmParameters algorithmParameters, byte[] hash) throws NSDKException {
        isSupported();

        if (key == null || algorithmParameters == null || hash == null) {
            throw new NSDKIllegalParameterException("Key, algorithm parameters and hash shall not be null.");
        }

        if (key.getKeyType() == null || key.getKeyUsage() == null) {
            throw new NSDKIllegalParameterException("Key type and usage are required.");
        }

        if (algorithmParameters.getMessageDigestType() == null || algorithmParameters.getEncodingMode() == null) {
            throw new NSDKIllegalParameterException("Message digest type and encoding mode are required.");
        }

        int[] sigDataLen = new int[1];
        byte[] sigData = new byte[1024];
        int encodingMode = algorithmParameters.getEncodingMode().ordinal();
        if (algorithmParameters.getEncodingMode() == AsymEncodingMode.ECC_ASN1) {
            encodingMode = 16;
        }
        int ret = NSDKJni.getInstance().NAPI_SecAsymSign(key.getKeyID(),
                key.getKeyType().getCode(),
                key.getKeyUsage().getCode(),
                algorithmParameters.getMessageDigestType().ordinal(),
                encodingMode,
                hash.length, hash, sigDataLen, sigData);

        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to sign data, ret = %d.", ret));
        }

        if (sigDataLen[0] > 0) {
            return Arrays.copyOf(sigData, sigDataLen[0]);
        }

        return null;
    }

    @Override
    public void verifyAsym(AsymmetricKey key, AsymAlgorithmParameters algorithmParameters, byte[] hash, byte[] signedData) throws NSDKException {
        isSupported();

        if (key == null || algorithmParameters == null || hash == null || signedData == null) {
            throw new NSDKIllegalParameterException("Key, algorithm parameters, hash and signed data shall not be null.");
        }

        if (key.getKeyType() == null || key.getKeyUsage() == null) {
            throw new NSDKIllegalParameterException("Key type and usage are required.");
        }

        if (algorithmParameters.getMessageDigestType() == null || algorithmParameters.getEncodingMode() == null) {
            throw new NSDKIllegalParameterException("Message digest type and encoding mode are required.");
        }

        int encodingMode = algorithmParameters.getEncodingMode().ordinal();
        if (algorithmParameters.getEncodingMode() == AsymEncodingMode.ECC_ASN1) {
            encodingMode = 16;
        }
        int ret = NSDKJni.getInstance().NAPI_SecAsymVerify(key.getKeyID(),
                key.getKeyType().getCode(),
                key.getKeyUsage().getCode(),
                algorithmParameters.getMessageDigestType().ordinal(),
                encodingMode,
                hash.length, hash, signedData.length, signedData);

        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to sign data, ret = %d.", ret));
        }
    }

    @Override
    public byte[] createCryptogram(AsymmetricKey cryptoKey, Key sessionKey, Key componentSecretKey, CryptogramInfo cryptogramInfo) throws NSDKException {
        if (cryptoKey == null || sessionKey == null || componentSecretKey == null) {
            throw new NSDKIllegalParameterException("Crypto key, session key and component secret key shall not be null.");
        }
        if (cryptogramInfo == null) {
            throw new NSDKIllegalParameterException("Cryptogram information shall not be null.");
        }

        ST_SEC_KEYIN_DATA secKeyinData = new ST_SEC_KEYIN_DATA();
        if (sessionKey instanceof SymmetricKey) {
            SymmetricKey tempKey = (SymmetricKey) sessionKey;
            secKeyinData.setUcKEKIdx(tempKey.getKeyID() & 0xFF);
            if (tempKey.getKeyType() == null) {
                throw new NSDKIllegalParameterException("Session key type shall not be null.");
            }
            secKeyinData.setKEKType(tempKey.getKeyType().getCode());
            if (tempKey.getKeyUsage() == null) {
                throw new NSDKIllegalParameterException("Session key usage shall not be null.");
            }
            secKeyinData.setKEKUsage(tempKey.getKeyUsage().getCode());
        } else if (sessionKey instanceof AsymmetricKey) {
            AsymmetricKey tempKey = (AsymmetricKey) sessionKey;
            secKeyinData.setUcKEKIdx(tempKey.getKeyID() & 0xFF);
            if (tempKey.getKeyType() == null) {
                throw new NSDKIllegalParameterException("Session key type shall not be null.");
            }
            secKeyinData.setKEKType(tempKey.getKeyType().getCode());
            if (tempKey.getKeyUsage() == null) {
                throw new NSDKIllegalParameterException("Session key usage shall not be null.");
            }
            secKeyinData.setKEKUsage(tempKey.getKeyUsage().getCode());
        }

        if (componentSecretKey instanceof SymmetricKey) {
            SymmetricKey tempKey = (SymmetricKey) componentSecretKey;
            secKeyinData.setUcKeyIdx(tempKey.getKeyID() & 0xFF);
            if (tempKey.getKeyType() == null) {
                throw new NSDKIllegalParameterException("Component secret key type shall not be null.");
            }
            secKeyinData.setKeyType(tempKey.getKeyType().getCode());
            if (tempKey.getKeyUsage() == null) {
                throw new NSDKIllegalParameterException("Component secret key usage shall not be null.");
            }
            secKeyinData.setKeyUsage(tempKey.getKeyUsage().getCode());
        } else if (componentSecretKey instanceof AsymmetricKey) {
            AsymmetricKey tempKey = (AsymmetricKey) componentSecretKey;
            secKeyinData.setUcKeyIdx(tempKey.getKeyID() & 0xFF);
            if (tempKey.getKeyType() == null) {
                throw new NSDKIllegalParameterException("Component secret key type shall not be null.");
            }
            secKeyinData.setKeyType(tempKey.getKeyType().getCode());
            if (tempKey.getKeyUsage() == null) {
                throw new NSDKIllegalParameterException("Component secret key usage shall not be null.");
            }
            secKeyinData.setKeyUsage(tempKey.getKeyUsage().getCode());
        }
        byte[] outData = new byte[4096];
        int[] outDataLen = new int[1];
        int ret = NSDKJni.getInstance().NAPI_SecCreateCryptogram(cryptoKey, secKeyinData, cryptogramInfo, outData, outDataLen);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to create cryptogram, ret = %d", ret));
        }
        int outLen = outDataLen[0];
        if (outLen > 0) {
            return Arrays.copyOf(outData, outLen);
        }
        return null;
    }

    private CipherOutput calculate(int mode, SymmetricKey key, CipherType cipherType, PaddingMode paddingMode, byte[] iv, byte[] datain) throws NSDKException {
        if (key == null || cipherType == null || datain == null || datain.length == 0) {
            throw new NSDKIllegalParameterException("Key, cipher type and data shall not be null.");
        }

        ST_SEC_ENCRYPTION_DATA encryptionData = new ST_SEC_ENCRYPTION_DATA();
        ST_SEC_DUKPT_DERIVATE_DATA dukptDerivateData;
        encryptionData.setUcKeyID(key.getKeyID() & 0xFF);

        if (key.getKeyUsage() != null) {
            encryptionData.setKeyUsage(key.getKeyUsage().getCode());
        } else {
            if (key instanceof DUKPTDerivateKey) {
                encryptionData.setKeyUsage(KeyUsage.DUKPT.getCode());
            } else {
                encryptionData.setKeyUsage(KeyUsage.DATA.getCode());
            }
        }
        encryptionData.setCipherType(cipherType.getCode());

        if (paddingMode != null) {
            encryptionData.setPaddingMode(paddingMode.getCode());
        } else {
            encryptionData.setPaddingMode(PaddingMode.NONE.getCode());
        }
        if (iv != null) {
            encryptionData.setUnIVSize(iv.length);
            encryptionData.setPsIV(iv);
        } else {
            encryptionData.setUnIVSize(0);
            encryptionData.setPsIV(null);
        }
        encryptionData.setUnDataInLen(datain.length);
        encryptionData.setPsDataIn(datain);
        encryptionData.setUnADSize(0);
        encryptionData.setpAD(null);

        if (key instanceof DUKPTDerivateKey) {
            DUKPTDerivateKey dukptDerivateKey = (DUKPTDerivateKey) key;
            if (dukptDerivateKey.getDerivateKeyType() == null || dukptDerivateKey.getDerivateUsage() == null) {
                throw new NSDKIllegalParameterException("Derivate key type and usage shall not be null.");
            }
            dukptDerivateData = new ST_SEC_DUKPT_DERIVATE_DATA();
            dukptDerivateData.setDerivateKeyType(dukptDerivateKey.getDerivateKeyType().getCode());
            dukptDerivateData.setDerivateKeyUsage(dukptDerivateKey.getDerivateUsage().ordinal());
            dukptDerivateData.setDerivateKeyLen(dukptDerivateKey.getDerivateKeyLen());

            encryptionData.setDukptDerivateData(dukptDerivateData);
        }

        byte[] outData = new byte[4096];
        int[] outDataLen = new int[1];

        byte[] ksnData = new byte[32];
        int[] ksnDataLen = new int[1];
        int ret = ErrorCode.ERROR;
        if (mode == MODE_ENCRYPT) {
            ret = NSDKJni.getInstance().NAPI_SecEncryption(encryptionData, outData, outDataLen, ksnData, ksnDataLen);
        } else if (mode == MODE_DECRYPT) {
            ret = NSDKJni.getInstance().NAPI_SecDecryption(encryptionData, outData, outDataLen, ksnData, ksnDataLen);
        }
        byte[] data = null;
        byte[] ksn = null;
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret == ErrorCode.OK) {
            if (outDataLen[0] > 0) {
                data = new byte[outDataLen[0]];
                System.arraycopy(outData, 0, data, 0, data.length);
            }
            if (ksnDataLen[0] > 0) {
                ksn = new byte[ksnDataLen[0]];
                System.arraycopy(ksnData, 0, ksn, 0, ksn.length);
            }
        } else {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to %s data, ret = %d.", mode == MODE_ENCRYPT?"encrypt":"decrypt", ret));
        }
        return new CipherOutput(data, ksn);
    }

    private byte[] calculateAsym(int mode, AsymmetricKey key, AsymAlgorithmParameters algorithmParameters, byte[] dataIn) throws NSDKException {
        if (key == null || algorithmParameters == null || dataIn == null || dataIn.length == 0) {
            throw new NSDKIllegalParameterException("Key, algorithm parameters and data shall not be null.");
        }

        if (key.getKeyType() == null || key.getKeyUsage() == null) {
            throw new NSDKIllegalParameterException("Key type and usage are required.");
        }

        if (algorithmParameters.getEncodingMode() == null || algorithmParameters.getCryptoMode() == null) {
            throw new NSDKIllegalParameterException("Message digest type, encoding mode and crypto mode are required.");
        }

        if (algorithmParameters.getMessageDigestType() == null) {
            if (algorithmParameters.getEncodingMode() != AsymEncodingMode.PKCS_V15) {
                throw new NSDKIllegalParameterException("Message digest type is required when encoding mode is PKCS_V15.");
            }
            algorithmParameters.setMessageDigestType(MessageDigestType.SHA256);
        }

        byte[] outData = new byte[4096];
        int[] outDataLen = new int[1];
        int ret;
        if (mode == MODE_ENCRYPT) {
            ret = NSDKJni.getInstance().NAPI_SecAsymEncryption(key.getKeyID(),
                    key.getKeyType().getCode(),
                    key.getKeyUsage().getCode(),
                    algorithmParameters.getMessageDigestType().ordinal(),
                    algorithmParameters.getEncodingMode().ordinal(),
                    algorithmParameters.getCryptoMode().ordinal(),
                    dataIn.length, dataIn, outDataLen, outData);
        } else {
            ret = NSDKJni.getInstance().NAPI_SecAsymDecryption(key.getKeyID(),
                    key.getKeyType().getCode(),
                    key.getKeyUsage().getCode(),
                    algorithmParameters.getMessageDigestType().ordinal(),
                    algorithmParameters.getEncodingMode().ordinal(),
                    algorithmParameters.getCryptoMode().ordinal(),
                    dataIn.length, dataIn, outDataLen, outData);
        }
        
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to %s data, ret = %d.", mode == MODE_ENCRYPT ? "encrypt" : "decrypt", ret));
        } 
        
        if (outDataLen[0] > 0) {
            return Arrays.copyOf(outData, outDataLen[0]);
        }
        
        return null;
    }

    @Override
    public byte[] verifyCert(boolean isCACertPath, String caData, byte[] certData) throws NSDKException {
        if (caData == null || caData.isEmpty()) {
            throw new NSDKIllegalParameterException("CA cert data shall not be null.");
        }
        if (certData == null || certData.length == 0) {
            throw new NSDKIllegalParameterException("Cert data shall not be null.");
        }

        byte[] publicKey = new byte[4096];
        int[] publicKeyLen = new int[1];

        int ret = NSDKJni.getInstance().NAPI_SecVerifyCert(isCACertPath ? 0 : 1, caData, caData.length(), certData, certData.length, publicKey, publicKeyLen);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to verify cert, ret = %d", ret));
        }

        return publicKeyLen[0] > 0 ? Arrays.copyOf(publicKey, publicKeyLen[0]) : null;
    }
    private CipherOutput calculateGCM(int mode, SymmetricKey key, CipherType cipherType, PaddingMode paddingMode, byte[] iv, byte[] datain, byte[] authTag, int authTagLen, byte[] authData) throws NSDKException {
        int authDataLen = (authData == null) ? 0 : authData.length;
        if (key == null || cipherType == null || datain == null || datain.length == 0) {
            throw new NSDKIllegalParameterException("Key, cipher type and data shall not be null.");
        }

        if (mode == MODE_ENCRYPT) {
            if (authTagLen <= 0) {
                throw new NSDKIllegalParameterException("Auth tag length shall not be <= 0 when encrypting data.");
            }
        } else if (mode == MODE_DECRYPT) {
            if (authTag == null || authTag.length == 0) {
                throw new NSDKIllegalParameterException("Auth tag shall not be null when decrypting data.");
            }
        }
        ST_SEC_ENCRYPTION_DATA encryptionData = new ST_SEC_ENCRYPTION_DATA();
        ST_SEC_DUKPT_DERIVATE_DATA dukptDerivateData;
        encryptionData.setUcKeyID(key.getKeyID() & 0xFF);

        if (key.getKeyUsage() != null) {
            encryptionData.setKeyUsage(key.getKeyUsage().getCode());
        } else {
            if (key instanceof DUKPTDerivateKey) {
                encryptionData.setKeyUsage(KeyUsage.DUKPT.getCode());
            } else {
                encryptionData.setKeyUsage(KeyUsage.DATA.getCode());
            }
        }
        encryptionData.setCipherType(cipherType.getCode());

        if (paddingMode != null) {
            encryptionData.setPaddingMode(paddingMode.getCode());
        } else {
            encryptionData.setPaddingMode(PaddingMode.NONE.getCode());
        }
        if (iv != null && iv.length > 0) {
            encryptionData.setUnIVSize(iv.length);
            encryptionData.setPsIV(iv);
        } else {
            encryptionData.setUnIVSize(0);
            encryptionData.setPsIV(null);
        }
        encryptionData.setUnDataInLen(datain.length);
        encryptionData.setPsDataIn(datain);
        encryptionData.setUnADSize(0);
        encryptionData.setpAD(null);

        if (key instanceof DUKPTDerivateKey) {
            DUKPTDerivateKey dukptDerivateKey = (DUKPTDerivateKey) key;
            if (dukptDerivateKey.getDerivateKeyType() == null || dukptDerivateKey.getDerivateUsage() == null) {
                throw new NSDKIllegalParameterException("Derivate key type and usage shall not be null.");
            }
            dukptDerivateData = new ST_SEC_DUKPT_DERIVATE_DATA();
            dukptDerivateData.setDerivateKeyType(dukptDerivateKey.getDerivateKeyType().getCode());
            dukptDerivateData.setDerivateKeyUsage(dukptDerivateKey.getDerivateUsage().ordinal());
            dukptDerivateData.setDerivateKeyLen(dukptDerivateKey.getDerivateKeyLen());

            encryptionData.setDukptDerivateData(dukptDerivateData);
        }

        byte[] outData = new byte[4096];
        int[] outDataLen = new int[1];

        byte[] ksnData = new byte[32];
        int[] ksnDataLen = new int[1];

        byte[] authTagOut = new byte[1024];
        int ret = ErrorCode.ERROR;
        if (mode == MODE_ENCRYPT) {
            ret = NSDKJni.getInstance().NAPI_SecEncryption_GCM(encryptionData, outData, outDataLen, ksnData, ksnDataLen, authTagOut, authTagLen, authData, authDataLen);
        } else if (mode == MODE_DECRYPT) {
            ret = NSDKJni.getInstance().NAPI_SecDecryption_GCM(encryptionData, outData, outDataLen, ksnData, ksnDataLen, authTag, authTag.length, authData, authDataLen);
        }
        byte[] data = null;
        byte[] ksn = null;
//        byte[] authTag = null;
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret == ErrorCode.OK) {
            if (outDataLen[0] > 0) {
                data = new byte[outDataLen[0]];
                System.arraycopy(outData, 0, data, 0, data.length);
            }
            if (ksnDataLen[0] > 0) {
                ksn = new byte[ksnDataLen[0]];
                System.arraycopy(ksnData, 0, ksn, 0, ksn.length);
            }
            if (mode == MODE_ENCRYPT) {
                authTag = new byte[authTagLen];
                System.arraycopy(authTagOut, 0, authTag, 0, authTag.length);
            }
        } else {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to %s data, ret = %d.", mode == MODE_ENCRYPT?"encrypt":"decrypt", ret));
        }

        if (mode == MODE_ENCRYPT) {
            return new GCMCipherOut(data, ksn, authTag);
        } else {
            return new CipherOutput(data, ksn);
        }
    }
}
