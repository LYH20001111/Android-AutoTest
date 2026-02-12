package com.newland.nsdk.core.internal.keymanager;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.crypto.AlgorithmParameters;
import com.newland.nsdk.core.api.common.crypto.AsymAlgorithmParameters;
import com.newland.nsdk.core.api.common.keymanager.ExportMode;
import com.newland.nsdk.core.api.common.crypto.KCVMode;
import com.newland.nsdk.core.api.common.crypto.MessageDigestType;
import com.newland.nsdk.core.api.common.crypto.PaddingMode;
import com.newland.nsdk.core.api.common.crypto.TR34EncodingMode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.keymanager.AsymAlgInfo;
import com.newland.nsdk.core.api.common.keymanager.AsymKeyType;
import com.newland.nsdk.core.api.common.keymanager.AsymKeyUsage;
import com.newland.nsdk.core.api.common.keymanager.AsymmetricKey;
import com.newland.nsdk.core.api.common.keymanager.CipherMode;
import com.newland.nsdk.core.api.common.keymanager.DUKPTKey;
import com.newland.nsdk.core.api.common.keymanager.KDFInfo;
import com.newland.nsdk.core.api.common.keymanager.Key;
import com.newland.nsdk.core.api.common.keymanager.KeyGenerateMethod;
import com.newland.nsdk.core.api.common.keymanager.KeyInfoID;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.api.common.keymanager.KeyUsage;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.internal.keymanager.MACVerifyParameters;
import com.newland.nsdk.core.api.internal.keymanager.SignVerifyParameters;
import com.newland.nsdk.core.api.internal.keymanager.VerifyParameters;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.internal.exception.NSDKNDKException;
import com.newland.nsdk.core.api.internal.keymanager.KeyManager;
import com.newland.nsdk.core.common.keymanager.ST_SEC_ASYM_ALG_INFO;
import com.newland.nsdk.core.common.keymanager.ST_SEC_ASYM_KEYIN_DATA;
import com.newland.nsdk.core.common.keymanager.ST_SEC_ASYM_KEY_INFO;
import com.newland.nsdk.core.common.keymanager.ST_SEC_KCV_DATA;
import com.newland.nsdk.core.common.keymanager.ST_SEC_KEYNUM_INFO;
import com.newland.nsdk.core.common.keymanager.ST_SEC_SYMM_KEYID_INFO;
import com.newland.nsdk.core.internal.jni.NSDKJni;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * The interface Key manager spi.
 */
public class KeyManagerImpl implements KeyManager {

    private static final String TAG = "KeyManager";

    public boolean isSupported;

    private volatile static KeyManagerImpl instance;

    private long handle = -1;

    public static KeyManagerImpl getInstance(boolean isSupported) {
        if (instance == null) {
            synchronized (KeyManagerImpl.class) {
                if (instance == null || instance.isSupported != isSupported) {
                    instance = new KeyManagerImpl(isSupported);
                }
            }
        } else {
            if (instance.isSupported != isSupported) {
                instance = new KeyManagerImpl(isSupported);
            }
        }
        return instance;
    }

    private KeyManagerImpl(){
        this.isSupported = true;
    }

    private KeyManagerImpl(boolean isSupported){
        this.isSupported = isSupported;
    }

    private void isSupported() throws NSDKException {
        if(!isSupported){
            throw new NSDKException(ErrorCode.UNSUPPORTED, "UnSupported KeyManager Module");
        }
    }

    /**
     * Generate key int.
     *
     * @param method the method
     * @param srcKey the src key
     * @param dstKey the dst key
     * @return the int
     */
    @Override
    public void generateKey(KeyGenerateMethod method, SymmetricKey srcKey, Key dstKey) throws NSDKException {
        isSupported();

        generateKey(method, null, srcKey, dstKey);
    }

    @Override
    public void generateKey(KeyGenerateMethod method, AlgorithmParameters algorithmParameters, SymmetricKey srcKey, Key dstKey) throws NSDKException {
        isSupported();

        generateKey(method, algorithmParameters, srcKey, dstKey, null);
    }

    @Override
    public void generateKey(KeyGenerateMethod method, AlgorithmParameters algorithmParameters, SymmetricKey srcKey, Key dstKey, byte[] additionalData) throws NSDKException {
        isSupported();

        if (method == null || dstKey == null || (method != KeyGenerateMethod.CLEAR && method != KeyGenerateMethod.DUKPT_DERIVE && method != KeyGenerateMethod.RANDOM && srcKey == null)) {
            throw new NSDKIllegalParameterException("Parameter method ,srcKey or dstKey is null!");
        }

        ST_SEC_KEYIN_DATA keyData = createKeyInData(algorithmParameters, srcKey, dstKey, additionalData);

        ST_SEC_KCV_DATA kcvData = createKcvData(dstKey);

        int ret = NSDKJni.getInstance().NAPI_SecGenerateKey(method.getCode(), keyData, kcvData);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to generate key, result code = %d", ret));
        }
    }

    @Override
    public void generateKeyWithHKDF(KeyGenerateMethod method, AlgorithmParameters algorithmParameters, KDFInfo kdfInfo, SymmetricKey srcKey, Key dstKey) throws NSDKException {
        isSupported();

        if (method == null || dstKey == null || srcKey == null) {
            throw new NSDKIllegalParameterException("Parameter method ,srcKey or dstKey is null!");
        }

        ST_SEC_KEYIN_DATA keyData = createHKDFKeyInData(algorithmParameters, srcKey, dstKey, kdfInfo);
        ST_SEC_KCV_DATA kcvData = createKcvData(dstKey);

        int ret = NSDKJni.getInstance().NAPI_SecGenerateKey(method.getCode(), keyData, kcvData);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to generate key with HKDF, result code = %d", ret));
        }
    }



    @Override
    public byte[] generateKeyWithAsymKey(KeyGenerateMethod method, AsymAlgorithmParameters algorithmParameters, AsymmetricKey srcKey, SymmetricKey dstKey) throws NSDKException {
        isSupported();

        if (method == null || dstKey == null || algorithmParameters == null || srcKey == null) {
            throw new NSDKIllegalParameterException("All the parameters shall not be null!");
        }

        if (method != KeyGenerateMethod.CIPHER && method != KeyGenerateMethod.RANDOM_OUT) {
            throw new NSDKIllegalParameterException("Only support CIPHER and RANDOM_OUT methods.");
        }

        if (algorithmParameters.getEncodingMode() == null || algorithmParameters.getMessageDigestType() == null) {
            throw new NSDKIllegalParameterException("Encoding mode and message digest type shall not be null.");
        }

        // 当 method 是 RANDOM_OUT 时，ad 是用来传出生成的随机密钥的长度的。
        byte[] randomKey = null;
        int[] randomKeyLen = null;
        if (method == KeyGenerateMethod.RANDOM_OUT) {
            randomKey = new byte[512];
            randomKeyLen = new int[1];
        }

        ST_SEC_ASYM_KEYIN_DATA keyData = createAsymKeyInData(algorithmParameters, srcKey, dstKey);
        ST_SEC_KCV_DATA kcvData = createKcvData(dstKey);

        int ret = NSDKJni.getInstance().NAPI_SecAsymGenerateKey(method.getCode(), keyData, kcvData, randomKeyLen, randomKey);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to generate key, result code = %d", ret));
        }

        if (method == KeyGenerateMethod.RANDOM_OUT) {
            return Arrays.copyOf(randomKey, randomKeyLen[0]);
        }

        return null;
    }

    @Override
    public void generateAsymKey(AsymmetricKey dstKey, AsymAlgInfo asymAlgInfo) throws NSDKException {
        long[] handle = new long[1];
        ST_SEC_ASYM_KEYIN_DATA stSecAsymKeyinData = getStAsymKeyInData(dstKey);
        ST_SEC_ASYM_ALG_INFO stSecAsymAlgInfo = new ST_SEC_ASYM_ALG_INFO();
        if (asymAlgInfo == null) {
            throw new NSDKIllegalParameterException(ErrorCode.PARAM_ERROR, "Asym algorithm info shall not be null.");
        } else {
            stSecAsymAlgInfo.setUnBit(asymAlgInfo.getUnBit());
            stSecAsymAlgInfo.setUcRSAPubExp(asymAlgInfo.getUcRSAPubExp());
        }

        int ret = NSDKJni.getInstance().NAPI_SecGenerateAsymKey(handle, stSecAsymKeyinData, stSecAsymAlgInfo);
        if (ret != 0) {
           throw new NSDKException(ret, String.format(Locale.US, "Failed to generate asymmetric key, ret = %d", ret));
        }
        this.handle = handle[0];
    }

    @Override
    public byte[] generateKeyWithSymmKey(KeyGenerateMethod method, AlgorithmParameters algorithmParameters, SymmetricKey srcKey, Key dstKey) throws NSDKException {
        if (method == null) {
            throw new NSDKIllegalParameterException("Key generation method shall not be null.");
        }
        if (srcKey == null || dstKey == null) {
            throw new NSDKIllegalParameterException("Source key and destination key shall not be null.");
        }
        if (method == KeyGenerateMethod.RANDOM_OUT && dstKey.getKeyLen() == 0) {
            throw new NSDKIllegalParameterException("Destination key length shall not be 0 when method is RANDOM_OUT.");
        }
        byte[] randomKeyDataOut = new byte[1024];
        int[] randomKeyDataOutLen = new int[1];
        ST_SEC_KEYIN_DATA keyinData = createKeyInData(algorithmParameters, srcKey, dstKey, null);
        ST_SEC_KCV_DATA kcvData = createKcvData(dstKey);

        int ret = NSDKJni.getInstance().generateKeyWithSymmKey(method.getCode(), keyinData, kcvData, randomKeyDataOut, randomKeyDataOutLen);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to generate key with symmetric key, ret = %d", ret));
        }
        if (randomKeyDataOutLen[0] > 0) {
            return Arrays.copyOf(randomKeyDataOut, randomKeyDataOutLen[0]);
        }
        return null;
    }


    /**
     * Delete key int.
     *
     * @param key the key
     */
    @Override
    public void deleteKey(Key key) throws NSDKException {
        isSupported();

        if (key == null) {
            throw new NSDKIllegalParameterException();
        }
        int keyType = -1;
        int keyUsage = -1;

        if (key instanceof SymmetricKey) {
            SymmetricKey tempKey = (SymmetricKey)key;
            if (tempKey.getKeyType() != null) {
                keyType = tempKey.getKeyType().getCode();
            }
            if (tempKey.getKeyUsage() != null) {
                keyUsage = tempKey.getKeyUsage().getCode();
            }
        } else if (key instanceof AsymmetricKey) {
            AsymmetricKey tempKey = (AsymmetricKey)key;
            if (tempKey.getKeyType() != null) {
                keyType = tempKey.getKeyType().getCode();
            }
            if (tempKey.getKeyUsage() != null) {
                keyUsage = tempKey.getKeyUsage().getCode();
            }
        }

        int ret = NSDKJni.getInstance().NAPI_SecDeleteKey(key.getKeyID() & 0xFF, keyType, keyUsage);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to delete key, result code = %d", ret));
        }
    }

    /**
     * Get key info int.
     *
     * @param infoID the info id
     * @param key    the key
     * @return the int
     */
    @Override
    public byte[] getKeyInfo(KeyInfoID infoID, Key key) throws NSDKException {
        isSupported();

        if (infoID == null || key == null) {
            throw new NSDKIllegalParameterException();
        }
        int keyType = -1;
        int keyUsage = -1;

        if (key instanceof SymmetricKey) {
            SymmetricKey tempKey = (SymmetricKey)key;
            if (tempKey.getKeyType() != null) {
                keyType = tempKey.getKeyType().getCode();
            }
            if (tempKey.getKeyUsage() != null) {
                keyUsage = tempKey.getKeyUsage().getCode();
            }
        } else if (key instanceof AsymmetricKey) {
            AsymmetricKey tempKey = (AsymmetricKey)key;
            if (tempKey.getKeyType() != null) {
                keyType = tempKey.getKeyType().getCode();
            }
            if (tempKey.getKeyUsage() != null) {
                keyUsage = tempKey.getKeyUsage().getCode();
            }
        }

        byte[] outInfo = new byte[4096];
        int[] outInfoLen = new int[1];
        int ret = NSDKJni.getInstance().NAPI_SecGetKeyInfo(infoID.ordinal(), key.getKeyID() & 0xFF, keyType, keyUsage, null, 0, outInfo, outInfoLen);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to get key info, result code = %d", ret));
        }

        byte[] data = new byte[outInfoLen[0]];
        System.arraycopy(outInfo, 0, data, 0, outInfoLen[0]);
        return data;
    }

    /**
     * Set key owner int.
     *
     * @param keyOwner the key owner
     */
    @Override
    public void setKeyOwner(String keyOwner) throws NSDKException {
        isSupported();

        int ret = NSDKJni.getInstance().NAPI_SecSetKeyOwner(keyOwner);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to set key owner, result code = %d", ret));
        }
    }

    /**
     * Get key owner int.
     *
     * @return the int
     */
    @Override
    public String getKeyOwner() throws NSDKException {
        isSupported();

        byte[] name = new byte[1024];
        int ret = NSDKJni.getInstance().NAPI_SecGetKeyOwner(name.length, name);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to get key owner, result code = %d", ret));
        }
        String keyOwner = new String(name).trim();
        LogUtils.d(TAG, ">>>keyOwner=" + keyOwner);
        return keyOwner;
    }

    @Override
    public void increaseKSN(byte groupId) throws NSDKException {
        isSupported();

        int id = groupId & 0xFF;
        int ret = NSDKJni.getInstance().NAPI_SecIncreaseKsn(id);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to increase KSN, result code = %d", ret));
        }
    }

    @Override
    public void increaseKSN(SymmetricKey key) throws NSDKException {
        isSupported();

        if (key == null) {
            throw new NSDKIllegalParameterException("Key shall not be null.");
        }

        KeyType keyType = key.getKeyType();
        if (keyType == null) {
            throw new NSDKIllegalParameterException("Key type shall not be null.");
        }

        int ret;
        int id = key.getKeyID() & 0xFF;
        if (keyType == KeyType.DES) {
            ret = NSDKJni.getInstance().NAPI_SecIncreaseKsn(id);
        } else if (keyType == KeyType.AES) {
            ret = NSDKJni.getInstance().NAPI_SecIncreaseAESKSN(id);
        } else {
            throw new NSDKIllegalParameterException("Unsupported key type: " + keyType);
        }

        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to increase AES KSN, result code = %d", ret));
        }
    }

    @Override
    public byte[] generateTR34Random(int len) throws NSDKException {
        if (len < 0 || len > 32) {
            throw new NSDKIllegalParameterException("Length of random data to be generated by KRD shall range from 0 to 32.");
        }
        byte[] out = new byte[32];
        int ret = NSDKJni.getInstance().NAPI_SecGenerateTR34Random(len, out);
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format("Failed to generate KRD TR34 random data, result code = %d", ret));
        }
        byte[] result = new byte[len];
        System.arraycopy(out, 0, result, 0, len);
        return result;
    }

    @Override
    public void processTR34KeyBlock(TR34EncodingMode encodingMode, AsymmetricKey asymmetricKey, SymmetricKey symmetricKey) throws NSDKException {
        if (encodingMode == null || asymmetricKey == null || symmetricKey == null) {
            throw new NSDKIllegalParameterException("All the parameters shall not be null.");
        }
        if (symmetricKey.getKeyData() == null || symmetricKey.getKeyData().length == 0) {
            throw new NSDKIllegalParameterException("KeyData of SymmetricKey where TR34 Data put in shall not be null.");
        }
        ST_SEC_ASYM_KEYIN_DATA asymKeyinData = createTR34KeyInData(asymmetricKey, symmetricKey);
        int ret = -1;
        if (encodingMode != TR34EncodingMode.TR34_BLOCK_ENCODING_RAW2 && encodingMode != TR34EncodingMode.TR34_BLOCK_ENCODING_RAW4 && encodingMode != TR34EncodingMode.TR34_BLOCK_ENCODING_C1) {
            String keyData = new String(symmetricKey.getKeyData());
            ret = NSDKJni.getInstance().NAPI_SecTR34ProcessKeyBlock(encodingMode.ordinal(), asymKeyinData, keyData);
        } else {
            ret = NSDKJni.getInstance().NAPI_SecTR34ProcessKeyBlockRevolut(encodingMode.ordinal(), asymKeyinData, symmetricKey.getKeyData());
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format("Failed to process TR34 key block, result code = %d", ret));
        }
    }

    @Override
    public byte[] processTR34KeyBlock(TR34EncodingMode encodingMode, AsymmetricKey asymmetricKey, SymmetricKey symmetricKey, byte[] additionalData) throws NSDKException {
        if (encodingMode == null || asymmetricKey == null || symmetricKey == null) {
            throw new NSDKIllegalParameterException("All the parameters shall not be null.");
        }
        if (symmetricKey.getKeyData() == null || symmetricKey.getKeyData().length == 0) {
            throw new NSDKIllegalParameterException("KeyData of SymmetricKey where TR34 Data put in shall not be null.");
        }
        ST_SEC_ASYM_KEYIN_DATA asymKeyinData = createTR34KeyInData(asymmetricKey, symmetricKey);
        asymKeyinData.setpKeyData(symmetricKey.getKeyData());
        int ret = -1;
        byte[] pad = new byte[1024];
        int[] padLen = new int[1];
        if (encodingMode == TR34EncodingMode.TR34_BLOCK_ENCODING_RAW3 && (additionalData == null || additionalData.length == 0)) {
            ret = NSDKJni.getInstance().NAPI_SecTR34ProcessKeyBlockWithPad(encodingMode.ordinal(), asymKeyinData, pad, padLen);
        } else if (additionalData != null && additionalData.length != 0) {
            padLen[0] = additionalData.length;
            ret = NSDKJni.getInstance().NAPI_SecTR34ProcessKeyBlockWithPad(encodingMode.ordinal(), asymKeyinData, additionalData, padLen);
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format("Failed to process TR34 key block, result code = %d", ret));
        }
        return padLen[0] == 0 ? null : Arrays.copyOf(pad, padLen[0]);
    }

    @Override
    public byte[] loadTrustedCert(boolean isCA, byte[] cert) throws NSDKException {
        isSupported();

        int[] pubKeyLen = new int[1];
        byte[] pubKey = new byte[2048];

        if (cert == null) {
            throw new NSDKIllegalParameterException("Cert shall not be null.");
        }

        int ret = NSDKJni.getInstance().NAPI_SecLoadTrustedCert(isCA, cert.length, cert, pubKeyLen, pubKey);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to load trusted cert, result code = %d", ret));
        }

        return Arrays.copyOf(pubKey, pubKeyLen[0]);
    }

    @Override
    public void resetCertStatus() throws NSDKException {
        isSupported();

        int ret = NSDKJni.getInstance().NAPI_SecResetCertStatus();
        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to reset cert status, result code = %d", ret));
        }
    }

    @Override
    public void initAtomic() throws NSDKException {
        isSupported();

        int ret = NSDKJni.getInstance().NAPI_SecInitAtomic();
        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to init atomic, result code = %d", ret));
        }
    }

    @Override
    public void commitAtomic(boolean isSuccessful) throws NSDKException {
        isSupported();

        int ret = NSDKJni.getInstance().NAPI_SecCommitAtomic(isSuccessful);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to commit atomic, result code = %d", ret));
        }
    }

    @Override
    public Map<Integer, Integer> getSymmKeyNums() throws NSDKException {
        int[] pTotalKeyNum = new int[1];
        ST_SEC_KEYNUM_INFO[] stSecKeynumInfos = new ST_SEC_KEYNUM_INFO[255];
        for (int i = 0; i < stSecKeynumInfos.length; i++) {
            stSecKeynumInfos[i] = new ST_SEC_KEYNUM_INFO();
        }
        int[] pArrayCounts = new int[1];
        int ret = NSDKJni.getInstance().NAPI_SecGetSymmKeyNum(pTotalKeyNum, stSecKeynumInfos, pArrayCounts);
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to get installed symmetric key number of the targeted key id, result code = %d", ret));
        }
        int totalKeyNum = pArrayCounts[0];
        Map<Integer, Integer> symmKeyNums = new HashMap<>();
        for (int i = 0; i < totalKeyNum; i++) {
            symmKeyNums.put(stSecKeynumInfos[i].getKeyId() & 0XFF, stSecKeynumInfos[i].getKeyNum());
        }
        return symmKeyNums;
    }

    @Override
    public SymmetricKey[] getSymmKeyInfoByID(byte id) throws NSDKException {
        int[] arrayCounts = new int[1];
        ST_SEC_SYMM_KEYID_INFO[] stSecSymmKeyidInfos = new ST_SEC_SYMM_KEYID_INFO[255];
        for (int i = 0; i < stSecSymmKeyidInfos.length; i++) {
            stSecSymmKeyidInfos[i] = new ST_SEC_SYMM_KEYID_INFO();
        }
        int ret = NSDKJni.getInstance().NAPI_SecGetSymmKeyInfoById(id, stSecSymmKeyidInfos, arrayCounts);
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to get info of symmetirc keys, result code = %d", ret));
        }
        int nArrayCount = arrayCounts[0];
        SymmetricKey[] symmetricKeys = new SymmetricKey[nArrayCount];
        byte[] kcvData = null;
        int kcvLen = 0;
        for (int i = 0;i < nArrayCount;i++) {
            kcvLen = stSecSymmKeyidInfos[i].getCheckLen();
            kcvData = new byte[kcvLen];
            System.arraycopy(stSecSymmKeyidInfos[i].getsCheckBuf(), 0, kcvData, 0, kcvLen);
            symmetricKeys[i] = new SymmetricKey();
            symmetricKeys[i].setKeyUsage(getKeyUsage(stSecSymmKeyidInfos[i].getKeyUsage()));
            symmetricKeys[i].setKeyType(getKeyType(stSecSymmKeyidInfos[i].getKeyType()));
            symmetricKeys[i].setKCV(kcvData);
        }
        return symmetricKeys;
    }

    @Override
    public void clearSymmetricKeys() throws NSDKException {
        int ret = NSDKJni.getInstance().NAPI_SecSymmKeyErase();
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to clear symmetric keys, ret = %d", ret));
        }
    }

    @Override
    public String generatePublicCert(AsymmetricKey caKey, Key cipherCertKey) throws NSDKException {
        if (caKey == null) {
            throw new NSDKIllegalParameterException("CA key shall not be null");
        }
        if (cipherCertKey == null) {
            throw new NSDKIllegalParameterException("The destination key for cipher cert generation shall not be null.");
        }
        ST_SEC_ASYM_KEY_INFO caKeyInfo = new ST_SEC_ASYM_KEY_INFO();
        ST_SEC_ASYM_KEY_INFO cipherCertInfo = new ST_SEC_ASYM_KEY_INFO();
        if (caKey.getKeyType() == null || caKey.getKeyUsage() == null) {
            throw new NSDKIllegalParameterException("CA key type and usage shall not be null.");
        }
        caKeyInfo.setKeyType(caKey.getKeyType().getCode());
        caKeyInfo.setKeyUsage(caKey.getKeyUsage().getCode());
        caKeyInfo.setKeyIdx(caKey.getKeyID() & 0xFF);

        cipherCertInfo.setKeyIdx(cipherCertKey.getKeyID() & 0xFF);
        if (cipherCertKey instanceof SymmetricKey) {
            SymmetricKey key = (SymmetricKey) cipherCertKey;
            if (key.getKeyType() == null || key.getKeyUsage() == null) {
                throw new NSDKIllegalParameterException("Destination key type and usage shall not be null.");
            }
            cipherCertInfo.setKeyType(key.getKeyType().getCode());
            cipherCertInfo.setKeyUsage(key.getKeyUsage().getCode());
        } else if (cipherCertKey instanceof AsymmetricKey) {
            AsymmetricKey key = (AsymmetricKey) cipherCertKey;
            if (key.getKeyType() == null || key.getKeyUsage() == null) {
                throw new NSDKIllegalParameterException("Destination key type and usage shall not be null.");
            }
            cipherCertInfo.setKeyType(key.getKeyType().getCode());
            cipherCertInfo.setKeyUsage(key.getKeyUsage().getCode());
        }
        byte[] certDate = new byte[4096];
        int[] certDataLen = new int[1];
        int ret = NSDKJni.getInstance().NAPI_SecGeneratePubKeyCert(caKeyInfo, cipherCertInfo, certDate, certDataLen);
        if (ret ==ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to generate public certificate by CA, ret = %d", ret));
        }

        if (certDataLen[0] > 0) {
            return new String(Arrays.copyOf(certDate, certDataLen[0]));
        }

        return null;
    }

    @Override
    public byte[] exportKey(ExportMode exportMode, Key sourceKey, Key dstKey, byte[] additionalData) throws NSDKException {
        if (exportMode == null) {
            throw new NSDKIllegalParameterException("Export key mode shall not be null.");
        }
        if (sourceKey == null || dstKey == null) {
            throw new NSDKIllegalParameterException("Source and destination key shall not be null.");
        }
        ST_SEC_KEYIN_DATA stSecKeyinData = new ST_SEC_KEYIN_DATA();
        stSecKeyinData.setUcKEKIdx(sourceKey.getKeyID() & 0xFF);
        if (sourceKey instanceof SymmetricKey) {
            SymmetricKey tempKey = (SymmetricKey) sourceKey;
            if (tempKey.getKeyUsage() == null) {
                throw new NSDKIllegalParameterException("Key usage of the source key shall not be null");
            }
            stSecKeyinData.setKEKUsage(tempKey.getKeyUsage().getCode());
            if (tempKey.getKeyType() == null) {
                throw new NSDKIllegalParameterException("Key type of the source key shall not be null");
            }
            stSecKeyinData.setKEKType(tempKey.getKeyType().getCode());
        } else if (sourceKey instanceof AsymmetricKey) {
            AsymmetricKey tempKey = (AsymmetricKey) sourceKey;
            if (tempKey.getKeyUsage() == null) {
                throw new NSDKIllegalParameterException("Key usage of the source key shall not be null");
            }
            stSecKeyinData.setKEKUsage(tempKey.getKeyUsage().getCode());
            if (tempKey.getKeyType() == null) {
                throw new NSDKIllegalParameterException("Key type of the source key shall not be null");
            }
            stSecKeyinData.setKEKType(tempKey.getKeyType().getCode());
        }
        stSecKeyinData.setUcKeyIdx(dstKey.getKeyID() & 0xFF);
        if (dstKey instanceof SymmetricKey) {
            SymmetricKey tempKey = (SymmetricKey) dstKey;
            if (tempKey.getKeyUsage() == null) {
                throw new NSDKIllegalParameterException("Key usage of the destination key shall not be null");
            }
            stSecKeyinData.setKeyUsage(tempKey.getKeyUsage().getCode());
            if (tempKey.getKeyType() == null) {
                throw new NSDKIllegalParameterException("Key type of the destination key shall not be null");
            }
            stSecKeyinData.setKeyType(tempKey.getKeyType().getCode());
        } else if (dstKey instanceof AsymmetricKey) {
            AsymmetricKey tempKey = (AsymmetricKey) dstKey;
            if (tempKey.getKeyUsage() == null) {
                throw new NSDKIllegalParameterException("Key usage of the destination key shall not be null");
            }
            stSecKeyinData.setKeyUsage(tempKey.getKeyUsage().getCode());
            if (tempKey.getKeyType() == null) {
                throw new NSDKIllegalParameterException("Key type of the destination key shall not be null");
            }
            stSecKeyinData.setKeyType(tempKey.getKeyType().getCode());
        }
        if (additionalData != null) {
            stSecKeyinData.setpAD(additionalData);
            stSecKeyinData.setnADSize(additionalData.length);
        }
        byte[] outData = new byte[4096];
        int[] outDataLen = new int[1];
        int ret = NSDKJni.getInstance().NAPI_SecKeyExport(exportMode.ordinal(), stSecKeyinData, outData, outDataLen);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to export key, ret = %d", ret));
        }
        int outLen = outDataLen[0];
        if (outLen > 0) {
            return Arrays.copyOf(outData, outLen);
        }
        return null;
    }

    @Override
    public Map<Byte, Boolean> injectPubKey(Map<AsymmetricKey, String> pubKeyInfoMap, VerifyParameters verifyParameters, byte[] data, byte[] additionalData) throws NSDKException {
        if (pubKeyInfoMap.isEmpty()) {
            throw new NSDKIllegalParameterException("Public key info map shall not be null.");
        }
        if (verifyParameters == null) {
            throw new NSDKIllegalParameterException("Verify parameters shall not be null.");
        }
        if (data == null || data.length == 0) {
            throw new NSDKIllegalParameterException("Data shall not be null.");
        }
        int mapSize = pubKeyInfoMap.size();
        ST_SEC_INJECTKEY_INFO[] stSecInjectkeyInfos = new ST_SEC_INJECTKEY_INFO[mapSize];
        int i = 0;
        for (Map.Entry<AsymmetricKey, String> entry : pubKeyInfoMap.entrySet()) {
            stSecInjectkeyInfos[i] = new ST_SEC_INJECTKEY_INFO();
            AsymmetricKey asymmetricKey = entry.getKey();
            stSecInjectkeyInfos[i].setKeyID(asymmetricKey.getKeyID() & 0xFF);
            stSecInjectkeyInfos[i].setKeyType(asymmetricKey.getKeyType().getCode());
            stSecInjectkeyInfos[i].setKeyUsage(asymmetricKey.getKeyUsage().getCode());
            String tag = entry.getValue();
            stSecInjectkeyInfos[i].setTag(tag);
            i++;
        }

        ST_SEC_VERIFY_MAC_INFO stSecVerifyMacInfo = new ST_SEC_VERIFY_MAC_INFO();
        if (verifyParameters instanceof MACVerifyParameters) {
            stSecVerifyMacInfo.setKeyID(((MACVerifyParameters) verifyParameters).getMacKeyInfo().getKeyID() & 0xFF);
            stSecVerifyMacInfo.setKeyType(((MACVerifyParameters) verifyParameters).getMacKeyInfo().getKeyType().getCode() & 0xFF);
            stSecVerifyMacInfo.setKeyUsage(((MACVerifyParameters) verifyParameters).getMacKeyInfo().getKeyUsage().getCode() & 0xFF);
            stSecVerifyMacInfo.setMacMode(((MACVerifyParameters) verifyParameters).getMacType().getCode() & 0xFF);
            byte[] iv = ((MACVerifyParameters) verifyParameters).getIv();
            if (iv != null && iv.length != 0) {
                stSecVerifyMacInfo.setIv(iv);
                stSecVerifyMacInfo.setIvLen(iv.length);
            } else {
                stSecVerifyMacInfo.setIv(null);
                stSecVerifyMacInfo.setIvLen(0);
            }
            byte[] macData = ((MACVerifyParameters) verifyParameters).getMacData();
            if (macData == null) {
                throw new NSDKIllegalParameterException("Mac data shall not be null.");
            }
            stSecVerifyMacInfo.setMacData(macData);
            stSecVerifyMacInfo.setMacDataLen(macData.length);
        }

        int adLen = (additionalData == null || additionalData.length == 0) ? 0 : additionalData.length;
        byte[] ad = null;
        if (adLen > 0) {
            ad = new byte[adLen];
            System.arraycopy(additionalData, 0, ad, 0, adLen);
        }

        int ret = 0;
        if (verifyParameters instanceof SignVerifyParameters) {
            ret = NSDKJni.getInstance().NAPI_SecInjectPubKeys(stSecInjectkeyInfos, stSecInjectkeyInfos.length, null, (SignVerifyParameters) verifyParameters, data, data.length, ad, adLen);
        } else {
            ret = NSDKJni.getInstance().NAPI_SecInjectPubKeys(stSecInjectkeyInfos, stSecInjectkeyInfos.length, stSecVerifyMacInfo, null, data, data.length,  ad, adLen);
        }

        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to inject public key info, ret = %d", ret));
        }

        Map<Byte, Boolean> resultMap = new HashMap<>();
        for (ST_SEC_INJECTKEY_INFO stSecInjectkeyInfo : stSecInjectkeyInfos) {
            resultMap.put((byte) stSecInjectkeyInfo.getKeyID(), stSecInjectkeyInfo.isInjectResult());
        }


        return resultMap;
    }

    private ST_SEC_ASYM_KEYIN_DATA createTR34KeyInData(AsymmetricKey asymmetricKey, SymmetricKey symmetricKey) throws NSDKException{
        ST_SEC_ASYM_KEYIN_DATA stSecAsymKeyinData = new ST_SEC_ASYM_KEYIN_DATA();
        stSecAsymKeyinData.setUcKEKIdx(asymmetricKey.getKeyID() & 0xFF);
        if (asymmetricKey.getKeyType() == null) {
            asymmetricKey.setKeyType(AsymKeyType.RSA);
        } else if (asymmetricKey.getKeyType() != AsymKeyType.RSA) {
            throw new NSDKIllegalParameterException("AsymmetricKey KeyType shall be RSA.");
        }
        if (asymmetricKey.getKeyUsage() == null) {
            asymmetricKey.setKeyUsage(AsymKeyUsage.KEY_DISTRIBUTION);
        } else if (asymmetricKey.getKeyUsage() != AsymKeyUsage.KEY_DISTRIBUTION) {
            throw new NSDKIllegalParameterException("AsymmerticKey KeyUsage shall be KEY_DISTRIBUTION");
        }
        stSecAsymKeyinData.setUcKeyIdx(symmetricKey.getKeyID() & 0xFF);
        stSecAsymKeyinData.setnKeyLen(symmetricKey.getKeyLen());
//        stSecAsymKeyinData.setpKeyData(symmetricKey.getKeyData());
        if (symmetricKey.getKeyType() == null) {
            throw new NSDKIllegalParameterException("Key type of the destination symmetricKey shall not be null.");
        }
        stSecAsymKeyinData.setKeyType(symmetricKey.getKeyType().getCode() & 0xFF);
        if (symmetricKey.getKeyUsage() == null) {
            throw new NSDKIllegalParameterException("Key usage of the destination symmetricKey shall not be null.");
        }
        stSecAsymKeyinData.setKeyUsage(symmetricKey.getKeyUsage().getCode() & 0xFF);
        return stSecAsymKeyinData;
    }

    private ST_SEC_KCV_DATA createKcvData(Key dstKey) throws NSDKIllegalParameterException {
        ST_SEC_KCV_DATA kcvData = new ST_SEC_KCV_DATA();
        if (dstKey instanceof SymmetricKey) {
            // Only symmetric keys have KCV
            if (((SymmetricKey) dstKey).getKCVMode() == null) {
                kcvData.setnCheckMode(KCVMode.NONE.ordinal());
            } else {
                kcvData.setnCheckMode(((SymmetricKey) dstKey).getKCVMode().ordinal());
            }
            if (kcvData.getnCheckMode() != KCVMode.NONE.ordinal()) {
                byte[] kcvValue = ((SymmetricKey) dstKey).getKCV();
                if (kcvValue == null) {
                    throw new NSDKIllegalParameterException("KCV is required when KCV mode is not KcvMode.NONE.");
                }
                kcvData.setnLen(kcvValue.length);
                kcvData.setsCheckBuf(kcvValue);
            }
        }

        return kcvData;
    }

    private ST_SEC_KEYIN_DATA createHKDFKeyInData(AlgorithmParameters algorithmParameters, SymmetricKey srcKey, Key dstKey, KDFInfo kdfInfo) throws NSDKIllegalParameterException {
        ST_SEC_KEYIN_DATA keyData = new ST_SEC_KEYIN_DATA();
        if (srcKey != null) {
            keyData.setUcKEKIdx(srcKey.getKeyID() & 0xFF);
            if (srcKey.getKeyType() != null) {
                keyData.setKEKType(srcKey.getKeyType().getCode());
            }else {
                throw new NSDKIllegalParameterException("BDK KeyType shall not be null!");
            }
            if (srcKey.getKeyUsage() != null && srcKey.getKeyUsage().getCode() == (byte)0) {
                keyData.setKEKUsage(srcKey.getKeyUsage().getCode());
            }else {
                throw new NSDKIllegalParameterException("BDK KeyUsage should be KEK!");
            }
        } else {
            keyData.setUcKEKIdx(0);
            keyData.setKEKType(KeyType.DES.getCode());
            keyData.setKeyUsage(KeyUsage.KEK.getCode());
        }

        keyData.setUcKeyIdx(dstKey.getKeyID() & 0xFF);
        if (dstKey instanceof SymmetricKey) {
            SymmetricKey tempKey = (SymmetricKey)dstKey;
            if (tempKey.getKeyType() != null) {
                keyData.setKeyType(tempKey.getKeyType().getCode());
            }
            if (tempKey.getKeyUsage() != null) {
                keyData.setKeyUsage(tempKey.getKeyUsage().getCode());
            }
        } else if (dstKey instanceof AsymmetricKey) {
            AsymmetricKey tempKey = (AsymmetricKey)dstKey;
            if (tempKey.getKeyType() != null) {
                keyData.setKeyType(tempKey.getKeyType().getCode());
            } else {
                keyData.setKeyType(AsymKeyType.RSA.getCode());
            }
            if (tempKey.getKeyUsage() != null) {
                keyData.setKeyUsage(tempKey.getKeyUsage().getCode());
            } else {
                keyData.setKeyUsage(AsymKeyUsage.AUTH_DATA.getCode());
            }
        } else {
            throw new NSDKIllegalParameterException("Target key shall be a symmetric key or asymmetric key.");
        }

        if (algorithmParameters != null && algorithmParameters.getCipherMode() != null) {
            if (algorithmParameters.getCipherMode() == CipherMode.CBC && algorithmParameters.getIV() == null) {
                throw new NSDKIllegalParameterException("IV is required when cipher mode is CBC.");
            }
            keyData.setCipherMode(algorithmParameters.getCipherMode().ordinal());
        } else {
            keyData.setCipherMode(CipherMode.ECB.ordinal());
        }

        if (algorithmParameters != null && algorithmParameters.getPaddingMode() != null) {
            keyData.setPaddingMode(algorithmParameters.getPaddingMode().getCode());
        } else {
            keyData.setPaddingMode(PaddingMode.NONE.getCode());
        }
        keyData.setnKeyLen(dstKey.getKeyLen());
        byte[] keyDataBuf = dstKey.getKeyData();
        if (keyDataBuf != null) {
            keyData.setnKeyDataLen(keyDataBuf.length);
            keyData.setpKeyData(keyDataBuf);
        }

        if (algorithmParameters != null && algorithmParameters.getIV() != null) {
            keyData.setPsIV(algorithmParameters.getIV());
        }

        if (dstKey instanceof DUKPTKey) {
            DUKPTKey dukptKey = (DUKPTKey) dstKey;
            byte[] ksn = dukptKey.getKSN();
            if (ksn == null) {
                throw new NSDKIllegalParameterException("KSN is null");
            }
            keyData.setnKsnLen(dukptKey.getKSN().length);
            keyData.setPsKsn(dukptKey.getKSN());
        }

        if (kdfInfo == null ) {
            keyData.setnADSize(0);
            keyData.setpAD(null);
        } else {
            byte[] addtional = new byte[3];
            Arrays.fill(addtional, (byte) 0);

            byte[] kdfType;
            byte[] kdfMessageType;
            byte[] Salt;
            byte[] saltInput = new byte[64];
            byte[] KDFInfo;
            byte[] kdfInfoInput = new byte[64];

            if (kdfInfo.getKDFType() != null) {
                kdfType = ISOUtils.hex2byte(String.valueOf(kdfInfo.getKDFType().ordinal()));
            } else {
                throw new NSDKIllegalParameterException("KDFType shall not be null.");
            }

            if (kdfInfo.getMessageDigestType() != null) {
                kdfMessageType = ISOUtils.hex2byte(String.valueOf(kdfInfo.getMessageDigestType().ordinal()));
            } else {
                kdfMessageType = ISOUtils.hex2byte(String.valueOf(MessageDigestType.SHA256.ordinal()));
            }


            Salt = kdfInfo.getSalt();

            if(Salt == null) {
                kdfInfo.setUcSaltLen((byte) 0);
                Arrays.fill(saltInput, (byte) 0);
            }else {
                kdfInfo.setUcSaltLen((byte) Salt.length);
                if(Salt.length >= 0 && Salt.length < 64) {
                    System.arraycopy(Salt, 0, saltInput, 0, Salt.length);
                    Arrays.fill(saltInput, Salt.length, 63, (byte) 0);
                }
            }
            KDFInfo = kdfInfo.getInfo();
            if(KDFInfo == null) {
                throw new NSDKIllegalParameterException("SN should be null!");
            }else {
                if(KDFInfo.length >= 0 && KDFInfo.length < 64) {
                    System.arraycopy(KDFInfo, 0, kdfInfoInput, 0, KDFInfo.length);
                    Arrays.fill(kdfInfoInput, KDFInfo.length, 63, (byte) 0);
                    kdfInfo.setUcInfoLen((byte) KDFInfo.length);
                }else if(KDFInfo.length == 64){
                    System.arraycopy(KDFInfo, 0, kdfInfoInput, 0, 64);
                    kdfInfo.setUcInfoLen((byte) 64);
                }else {
                    throw new NSDKIllegalParameterException("SN Length Illegal!");
                }
            }

            byte[] kdf_info = new byte[kdfType.length + kdfMessageType.length + kdfInfoInput.length + saltInput.length + 14];
            System.arraycopy(kdfType, 0, kdf_info, 0, kdfType.length);
            System.arraycopy(addtional, 0, kdf_info, kdfType.length, 3);
            System.arraycopy(kdfMessageType, 0, kdf_info, kdfType.length + 3, kdfMessageType.length);
            System.arraycopy(addtional, 0, kdf_info, kdfType.length + kdfMessageType.length + 3, 3);
            System.arraycopy(new byte[] {kdfInfo.getUcSaltLen()}, 0, kdf_info, kdfType.length + kdfMessageType.length + 6, 1);
            System.arraycopy(addtional, 0, kdf_info, kdfType.length + kdfMessageType.length + 7, 3);
            System.arraycopy(saltInput, 0, kdf_info, kdfType.length + kdfMessageType.length + 10, saltInput.length);
            System.arraycopy(new byte[] {kdfInfo.getUcInfoLen()}, 0, kdf_info, kdfType.length + kdfMessageType.length + saltInput.length + 10, 1);
            System.arraycopy(addtional, 0, kdf_info,kdfType.length + kdfMessageType.length + saltInput.length + 11, 3);
            System.arraycopy(kdfInfoInput, 0, kdf_info, kdfType.length + kdfMessageType.length + saltInput.length + 14, kdfInfoInput.length);

            keyData.setnADSize(kdf_info.length);
            keyData.setpAD(kdf_info);
        }

        return keyData;
    }

    private ST_SEC_KEYIN_DATA createKeyInData(AlgorithmParameters algorithmParameters, SymmetricKey srcKey, Key dstKey, byte[] additionalData) throws NSDKIllegalParameterException {
        ST_SEC_KEYIN_DATA keyData = new ST_SEC_KEYIN_DATA();
        if (srcKey != null) {
            keyData.setUcKEKIdx(srcKey.getKeyID() & 0xFF);
            if (srcKey.getKeyType() != null) {
                keyData.setKEKType(srcKey.getKeyType().getCode());
            }
            if (srcKey.getKeyUsage() != null) {
                keyData.setKEKUsage(srcKey.getKeyUsage().getCode());
            }
        } else {
            keyData.setUcKEKIdx(0);
            keyData.setKEKType(KeyType.DES.getCode());
            keyData.setKeyUsage(KeyUsage.KEK.getCode());
        }

        keyData.setUcKeyIdx(dstKey.getKeyID() & 0xFF);
        if (dstKey instanceof SymmetricKey) {
            SymmetricKey tempKey = (SymmetricKey)dstKey;
            if (tempKey.getKeyType() != null) {
                keyData.setKeyType(tempKey.getKeyType().getCode());
            }
            if (tempKey.getKeyUsage() != null) {
                keyData.setKeyUsage(tempKey.getKeyUsage().getCode());
            }
        } else if (dstKey instanceof AsymmetricKey) {
            AsymmetricKey tempKey = (AsymmetricKey)dstKey;
            if (tempKey.getKeyType() != null) {
                keyData.setKeyType(tempKey.getKeyType().getCode());
            } else {
                keyData.setKeyType(AsymKeyType.RSA.getCode());
            }
            if (tempKey.getKeyUsage() != null) {
                keyData.setKeyUsage(tempKey.getKeyUsage().getCode());
            } else {
                keyData.setKeyUsage(AsymKeyUsage.AUTH_DATA.getCode());
            }
        } else {
            throw new NSDKIllegalParameterException("Target key shall be a symmetric key or asymmetric key.");
        }

        if (algorithmParameters != null && algorithmParameters.getCipherMode() != null) {
            if (algorithmParameters.getCipherMode() == CipherMode.CBC && algorithmParameters.getIV() == null) {
                throw new NSDKIllegalParameterException("IV is required when cipher mode is CBC.");
            }
            keyData.setCipherMode(algorithmParameters.getCipherMode().ordinal());
        } else {
            keyData.setCipherMode(CipherMode.ECB.ordinal());
        }

        if (algorithmParameters != null && algorithmParameters.getPaddingMode() != null) {
            keyData.setPaddingMode(algorithmParameters.getPaddingMode().getCode());
        } else {
            keyData.setPaddingMode(PaddingMode.NONE.getCode());
        }
        keyData.setnKeyLen(dstKey.getKeyLen());
        byte[] keyDataBuf = dstKey.getKeyData();
        if (keyDataBuf != null) {
            keyData.setnKeyDataLen(keyDataBuf.length);
            keyData.setpKeyData(keyDataBuf);
        }

        if (algorithmParameters != null && algorithmParameters.getIV() != null) {
            keyData.setPsIV(algorithmParameters.getIV());
        }

        if (dstKey instanceof DUKPTKey) {
            DUKPTKey dukptKey = (DUKPTKey) dstKey;
            byte[] ksn = dukptKey.getKSN();
            if (ksn == null) {
                throw new NSDKIllegalParameterException("KSN is null");
            }
            keyData.setnKsnLen(dukptKey.getKSN().length);
            keyData.setPsKsn(dukptKey.getKSN());
        }

        if (additionalData == null || additionalData.length == 0) {
            keyData.setnADSize(0);
            keyData.setpAD(null);
        } else {
            keyData.setnADSize(additionalData.length);
            keyData.setpAD(additionalData);
        }

        return keyData;
    }

    private ST_SEC_ASYM_KEYIN_DATA createAsymKeyInData(AsymAlgorithmParameters algorithmParameters, AsymmetricKey srcKey, SymmetricKey dstKey) throws NSDKIllegalParameterException {
        ST_SEC_ASYM_KEYIN_DATA keyData = new ST_SEC_ASYM_KEYIN_DATA();
        keyData.setUcKEKIdx(srcKey.getKeyID() & 0xFF);
        if (srcKey.getKeyType() != null) {
            keyData.setKEKType(srcKey.getKeyType().getCode());
        } else {
            keyData.setKEKType(AsymKeyType.RSA.getCode());
        }
        if (srcKey.getKeyUsage() != null) {
            keyData.setKEKUsage(srcKey.getKeyUsage().getCode());
        } else {
            keyData.setKEKUsage(AsymKeyUsage.KEY_DISTRIBUTION.getCode());
        }

        keyData.setUcKeyIdx(dstKey.getKeyID() & 0xFF);
        if (dstKey.getKeyType() != null) {
            keyData.setKeyType(dstKey.getKeyType().getCode());
        }
        if (dstKey.getKeyUsage() != null) {
            keyData.setKeyUsage(dstKey.getKeyUsage().getCode());
        }

        keyData.setEncodingMode(algorithmParameters.getEncodingMode().ordinal());
        keyData.setMdAlg(algorithmParameters.getMessageDigestType().ordinal());

        keyData.setnKeyLen(dstKey.getKeyLen());
        byte[] keyDataBuf = dstKey.getKeyData();
        if (keyDataBuf != null) {
            keyData.setpKeyData(keyDataBuf);
        }

        if (dstKey instanceof DUKPTKey) {
            DUKPTKey dukptKey = (DUKPTKey) dstKey;
            byte[] ksn = dukptKey.getKSN();
            if (ksn == null) {
                throw new NSDKIllegalParameterException("KSN is null");
            }
            keyData.setnKsnLen(dukptKey.getKSN().length);
            keyData.setPsKsn(dukptKey.getKSN());
        }

        keyData.setnADSize(0);
        keyData.setpAD(null);

        return keyData;
    }

    private ST_SEC_ASYM_KEYIN_DATA getStAsymKeyInData(AsymmetricKey asymmetricKey) throws NSDKIllegalParameterException{
        ST_SEC_ASYM_KEYIN_DATA stSecAsymKeyinData = new ST_SEC_ASYM_KEYIN_DATA();
        if (asymmetricKey == null) {
            throw new NSDKIllegalParameterException(ErrorCode.PARAM_ERROR, "Asymmetric Key shall not be null");
        } else {
            stSecAsymKeyinData.setKeyType(AsymKeyType.RSA.getCode());
           if (asymmetricKey.getKeyUsage() == null) {
               throw new NSDKIllegalParameterException(ErrorCode.PARAM_ERROR, "Asymmetric key usage shall not be null");
           } else {
               stSecAsymKeyinData.setKeyUsage(asymmetricKey.getKeyUsage().getCode());
           }
           stSecAsymKeyinData.setUcKeyIdx(asymmetricKey.getKeyID());
        }
        return stSecAsymKeyinData;
    }


    private KeyUsage getKeyUsage(int keyusage) {
        KeyUsage keyUsage = null;
        for (KeyUsage ku : KeyUsage.values()) {
            if ((ku.getCode() & 0xFF) == keyusage) {
                keyUsage = ku;
            }
        }
        return keyUsage;
    }

    private KeyType getKeyType(int keytype) {
        KeyType keyType = null;
        for (KeyType kt : KeyType.values()) {
            if ((kt.getCode() & 0xFF) == keytype) {
                keyType = kt;
            }
        }
        return keyType;
    }
}