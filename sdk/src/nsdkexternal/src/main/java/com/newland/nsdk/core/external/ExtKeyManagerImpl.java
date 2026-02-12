package com.newland.nsdk.core.external;


import com.newland.nsdk.core.api.common.crypto.AlgorithmParameters;
import com.newland.nsdk.core.api.common.crypto.AsymAlgorithmParameters;
import com.newland.nsdk.core.api.common.crypto.KCVMode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.keymanager.AsymAlgInfo;
import com.newland.nsdk.core.api.common.keymanager.AsymKeyType;
import com.newland.nsdk.core.api.common.keymanager.AsymKeyUsage;
import com.newland.nsdk.core.api.common.keymanager.AsymmetricKey;
import com.newland.nsdk.core.api.common.keymanager.DUKPTKey;
import com.newland.nsdk.core.api.common.keymanager.Key;
import com.newland.nsdk.core.api.common.keymanager.KeyGenerateMethod;
import com.newland.nsdk.core.api.common.keymanager.KeyInfoID;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.api.common.keymanager.KeyUsage;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.external.keymanager.ExtKeyManager;
import com.newland.nsdk.core.external.command.ExternalCommandType;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.keymanager.ExtPinKCVType;
import com.newland.nsdk.core.external.command.keymanager.ExternalKeyManagerModule;
import com.newland.nsdk.core.common.keymanager.ST_SEC_ASYM_KEYIN_DATA;
import com.newland.nsdk.core.common.keymanager.ST_SEC_KCV_DATA;

public class ExtKeyManagerImpl implements ExtKeyManager {
    private ExternalKeyManagerModule externalKeyManagerModule;
    private volatile static ExtKeyManagerImpl instance;
    public static ExtKeyManagerImpl getInstance() {
        if (instance == null) {
            synchronized (ExtKeyManagerImpl.class) {
                if (instance == null) {
                    instance = new ExtKeyManagerImpl();
                }
            }
        }
        return instance;
    }
    private ExtKeyManagerImpl() {
        externalKeyManagerModule = new ExternalKeyManagerModule();
    }

    @Override
    public void generateKey(KeyGenerateMethod method, AlgorithmParameters algorithmParameters, SymmetricKey srcKey, Key dstKey, byte[] addtionalData) throws NSDKException {
        if (method == null || dstKey == null) {
            throw new NSDKIllegalParameterException("Key generation method, dst key shall not be null!");
        }

        if (dstKey.getKeyData() == null) {
            throw new NSDKIllegalParameterException("Key data shall not be null.");
        }

        // 适配 NAPI 指令
        if (ExternalCommunicationManager.getInstance().getConfig().getCommandType() == ExternalCommandType.NAPI) {
            externalKeyManagerModule.generateKeyNapi(method, algorithmParameters, srcKey, dstKey, addtionalData);
            return;
        }

        // NDK 明文方式，源密钥 id 填 0
        if (method == KeyGenerateMethod.CLEAR) {
            srcKey = new SymmetricKey();
        }

        if (!(dstKey instanceof SymmetricKey)) {
            throw new NSDKIllegalParameterException("NDK commands only support symmetric target key now.");
        }

        if (method == KeyGenerateMethod.GISKE) {
            if (srcKey == null) {
                loadConvertAtmToGiske((SymmetricKey) dstKey);
            } else {
                if (dstKey instanceof DUKPTKey) {
                    loadGiskeTik(srcKey, (DUKPTKey) dstKey);
                } else {
                    loadGiskeKey(srcKey, dstKey);
                }
            }
        } else {
            if (dstKey instanceof DUKPTKey) {
                loadDukptKey(method, srcKey, (DUKPTKey) dstKey);
            } else {
                SymmetricKey tempKey = (SymmetricKey) dstKey;
                if (tempKey.getKeyType() == KeyType.DES || tempKey.getKeyType() == KeyType.AES) {
                    loadKey(method, srcKey, tempKey);
                } else {
                    throw new NSDKIllegalParameterException("Unsupported key loading.");
                }
            }
        }
    }

    /**
     * Generates symmetric key in PIN pad under the protection of specified asymmetric key.
     *
     * <p>Example:</p>
     * <pre>
     *     AsymmetricKey sourceKey = new SymmetricKey();
     *     SymmetricKey dstKey = new SymmetricKey();
     *
     *     AsymAlgorithmParameters algorithmParameters = new AsymAlgorithmParameters();
     *     algorithmParameters.setMessageDigestType(MessageDigestType.SHA256);
     *     algorithmParameters.setEncodingMode(AsymEncodingMode.PKCS_V15);
     *
     *     sourceKey.setKeyID(1);
     *     sourceKey.setKeyType(AsymKeyType.RSA);
     *     sourceKey.setKeyUsage(AsymKeyUsage.KEY_DISTRIBUTION);
     *
     *     dstKey.setKeyID(2);
     *     dstKey.setKeyType(KeyType.DES);
     *     dstKey.setKeyUsage(KeyUsage.TR31_KEK);
     *     dstKey.setKeyLen(24);
     *
     *     try {
     *         byte[] keyData = keyManager.generateKey(KeyGenerateMethod.RANDOM_OUT, algorithmParameters, sourceKey, dstKey);
     *     } catch (NSDKException e) {
     *         // Handle the exception
     *     }
     * </pre>
     *
     * @param method              <b>[Required]</b> Key injection method. For now, only {@link KeyGenerateMethod#CIPHER} and {@link KeyGenerateMethod#RANDOM_OUT} are supported.
     * @param algorithmParameters Algorithm parameters, see {@link AlgorithmParameters}
     *                            <ul>
     *                            <li><b>[Required]</b> Message digest type</li>
     *                            <li><b>[Required]</b> Encoding mode</li>
     *                            </ul>
     * @param srcKey              <b>[Required]</b> The key to protect the target key.
     *                            <ul>
     *                            <li>Default key type: {@link AsymKeyType#RSA}</li>
     *                            <li>Default key usage: {@link AsymKeyUsage#KEY_DISTRIBUTION}</li>
     *                            </ul>
     * @param dstKey              <b>[Required]</b> Target key be generated in PIN pad.
     *                            <ul>
     *                            <li>Default key type: {@link KeyType#DES}</li>
     *                            <li>Default key usage: {@link KeyUsage#KEK}</li>
     *                            <li>Default KCV mode: {@link KCVMode#NONE}</li>
     *                            </ul>
     * @return When method is {@link KeyGenerateMethod#RANDOM_OUT}, random key will be generated and returned.
     * @throws NSDKException
     */
    @Override
    public byte[] generateKeyWithAsymKey(KeyGenerateMethod method, AsymAlgorithmParameters algorithmParameters, AsymmetricKey srcKey, SymmetricKey dstKey) throws NSDKException {
        if (method == null || dstKey == null || algorithmParameters == null || srcKey == null) {
            throw new NSDKIllegalParameterException("All the parameters shall not be null!");
        }

        if (method != KeyGenerateMethod.CIPHER && method != KeyGenerateMethod.RANDOM_OUT) {
            throw new NSDKIllegalParameterException("Only support CIPHER and RANDOM_OUT methods.");
        }

        if (algorithmParameters.getEncodingMode() == null || algorithmParameters.getMessageDigestType() == null) {
            throw new NSDKIllegalParameterException("Encoding mode and message digest type shall not be null.");
        }

        if (srcKey.getKeyUsage() != AsymKeyUsage.KEY_DISTRIBUTION) {
            throw new NSDKIllegalParameterException("SrcKey only support KEY_DISTRIBUTION usage.");
        }

        ST_SEC_ASYM_KEYIN_DATA keyData = createAsymKeyInData(algorithmParameters, srcKey, dstKey);
        ST_SEC_KCV_DATA kcvData = createKcvData(dstKey);

        byte[] ret = externalKeyManagerModule.generateKeyWithAsymKey(method.getCode(), keyData, kcvData);
        // 当 method 是 RANDOM_OUT 时，ad 是用来传出生成的随机密钥的长度的。
        if (method == KeyGenerateMethod.RANDOM_OUT) {
            return ret;
        }

        return null;
    }

    @Override
    public void generateAsymKey(AsymmetricKey dstKey, AsymAlgInfo asymAlgInfo) throws NSDKException {
        if (dstKey == null) {
            throw new NSDKIllegalParameterException("DstKey shall not be null.");
        }
        if (asymAlgInfo == null) {
            throw new NSDKIllegalParameterException("AsymAlgInfo shall not be null.");
        }
        externalKeyManagerModule.generateAsymKey(dstKey, asymAlgInfo);
    }

    @Override
    public byte[] getKeyInfo(KeyInfoID keyInfoID, Key key) throws NSDKException {
        if (keyInfoID == null || key == null) {
            throw new NSDKIllegalParameterException("Key info ID and key shall not be null.");
        }

        if (ExternalCommunicationManager.getInstance().getConfig().getCommandType() == ExternalCommandType.NAPI) {
            return externalKeyManagerModule.getKeyInfoNapi(keyInfoID, key);
        }

        if (keyInfoID == KeyInfoID.KCV) {
            if (!(key instanceof SymmetricKey)) {
                throw new NSDKIllegalParameterException("Only can get KCV of symmetric key.");
            }
            ExtPinKCVType keyType = null;
            SymmetricKey tempKey = (SymmetricKey) key;
            if (tempKey.getKeyType() == KeyType.DES) {
                if (tempKey.getKeyUsage() == KeyUsage.KEK) {
                    keyType = ExtPinKCVType.DES_KEK;
                } else if (tempKey.getKeyUsage() == KeyUsage.PIN) {
                    keyType = ExtPinKCVType.DES_PIN;
                } else if (tempKey.getKeyUsage() == KeyUsage.MAC) {
                    keyType = ExtPinKCVType.DES_MAC;
                } else if (tempKey.getKeyUsage() == KeyUsage.DATA || tempKey.getKeyUsage() == KeyUsage.DATA_ENC_ONLY) {
                    keyType = ExtPinKCVType.DES_DATA;
                }
            } else if (tempKey.getKeyType() == KeyType.AES) {
                if (tempKey.getKeyUsage() == KeyUsage.KEK) {
                    keyType = ExtPinKCVType.AES_KEK;
                } else if (tempKey.getKeyUsage() == KeyUsage.PIN) {
                    keyType = ExtPinKCVType.AES_PIN;
                } else if (tempKey.getKeyUsage() == KeyUsage.MAC) {
                    keyType = ExtPinKCVType.AES_MAC;
                } else if (tempKey.getKeyUsage() == KeyUsage.DATA || tempKey.getKeyUsage() == KeyUsage.DATA_ENC_ONLY) {
                    keyType = ExtPinKCVType.AES_DATA;
                }
            }

            if (keyType == null) {
                throw new NSDKIllegalParameterException(String.format("Unsupported key type(%s) or key usage(%s).", tempKey.getKeyType(), tempKey.getKeyUsage()));
            }

            return externalKeyManagerModule.getKcv(key.getKeyID(), (byte) keyType.getCode());
        }

        if (keyInfoID == KeyInfoID.KSN) {
            return externalKeyManagerModule.getKsn(key.getKeyID());
        }

        return null;
    }

    private void loadKey(KeyGenerateMethod method, SymmetricKey srcKey, SymmetricKey dstKey) throws NSDKException {
        byte format;
        if (method == KeyGenerateMethod.TR31) {
            format = 0;
        } else {
            KeyType keyType = dstKey.getKeyType();
            if (keyType == KeyType.AES) {
                format = 1;
            } else if (keyType == KeyType.DES) {
                format = 2;
            } else {
                throw new NSDKIllegalParameterException(String.format("Unsupported key type(%s) for block format(NDK).", dstKey.getKeyType()));
            }
        }

        byte keyType;
        KeyUsage keyUsage = dstKey.getKeyUsage();
        if (keyUsage == KeyUsage.KEK) {
            keyType = 3;
        } else if (keyUsage == KeyUsage.PIN) {
            keyType = 0;
        } else if (keyUsage == KeyUsage.MAC) {
            keyType = 1;
        } else if (keyUsage == KeyUsage.DATA || keyUsage == KeyUsage.DATA_ENC_ONLY) {
            keyType = 2;
        } else if (keyUsage == KeyUsage.TR31_KEK) {
            keyType = 3;
        } else {
            throw new NSDKIllegalParameterException(String.format("Unsupported key usage(%s) for NDK.", dstKey.getKeyUsage()));
        }

        byte[] kcv = null;
        if (dstKey instanceof SymmetricKey) {
            if (dstKey.getKCVMode() != KCVMode.NONE) {
                kcv = dstKey.getKCV();
            }
        }

        externalKeyManagerModule.loadKeyBlock(srcKey.getKeyID(), format, keyType, dstKey.getKeyID(), dstKey.getKeyData(), kcv, dstKey.getKeyLen());
    }

    private void loadDukptKey(KeyGenerateMethod method, Key srcKey, DUKPTKey dstKey) throws NSDKException {
        byte format;
        if (method == KeyGenerateMethod.TR31) {
            format = 0;
        } else {
            format = 1;
        }
        if (dstKey.getKSN() == null) {
            throw new NSDKIllegalParameterException("KSN shall not be null!");
        }

        externalKeyManagerModule.loadDukptBlock(srcKey.getKeyID(), dstKey.getKeyID(), format, dstKey.getKeyData(), dstKey.getKSN());
    }

    private void loadGiskeKey(Key srcKey, Key dstKey) throws NSDKException {
        // Only support TMK type, fixed to 1
        byte kekType = 1;

        byte[] kcv = null;
        if (dstKey instanceof SymmetricKey) {
            kcv = ((SymmetricKey) dstKey).getKCV();
        }

        externalKeyManagerModule.loadGiskeKey(kekType, srcKey.getKeyID(), dstKey.getKeyID(), dstKey.getKeyData(), kcv);
    }

    private void loadGiskeTik(Key srcKey, DUKPTKey dstKey) throws NSDKException {
        // Only support TMK type, fixed to 1
        byte kekType = 1;

        externalKeyManagerModule.loadGiskeTik(kekType, srcKey.getKeyID(), dstKey.getKeyID(), dstKey.getKeyData(), dstKey.getKSN(), dstKey.getKCV());
    }

    private void loadConvertAtmToGiske(SymmetricKey dstKey) throws NSDKException {
        byte keyType;
        KeyUsage keyUsage = dstKey.getKeyUsage();
        if (keyUsage == KeyUsage.KEK) {
            keyType = 1;
        } else if (keyUsage == KeyUsage.PIN) {
            keyType = 2;
        } else if (keyUsage == KeyUsage.MAC) {
            keyType = 3;
        } else if (keyUsage == KeyUsage.DATA || keyUsage == KeyUsage.DATA_ENC_ONLY) {
            keyType = 4;
        } else {
            throw new NSDKIllegalParameterException(String.format("Unsupported key usage(%s).", dstKey.getKeyUsage()));
        }

        externalKeyManagerModule.convertAtmToGiske(keyType, dstKey.getKeyID(), dstKey.getKeyData());
    }

    @Override
    public void increaseKSN(byte groupId) throws NSDKException {
        externalKeyManagerModule.increaseKsn(groupId);
    }

    @Override
    public void deleteKey(Key key) throws NSDKException {
        if (key == null) {
            throw new NSDKIllegalParameterException("The key to delete shall not be null.");
        }

        byte keyType;
        byte keyUsage;

        if (key instanceof SymmetricKey) {
            SymmetricKey symmetricKey = (SymmetricKey) key;
            if (symmetricKey.getKeyUsage() == null) {
                throw new NSDKIllegalParameterException("Please set key usage.");
            }

            if (symmetricKey.getKeyType() == null) {
                throw new NSDKIllegalParameterException("Please set key type.");
            }

            keyType = symmetricKey.getKeyType().getCode();
            switch (symmetricKey.getKeyUsage()) {
                case PIN:
                    keyUsage = (byte) 0;
                    break;
                case MAC:
                    keyUsage = (byte) 1;
                    break;
                case DATA:
                    keyUsage = (byte) 2;
                    break;
                case KEK:
                    keyUsage = (byte) 3;
                    break;
                case DUKPT:
                    keyUsage = (byte) 4;
                    break;
                case TR31_KEK:
                    keyUsage = (byte) 5;
                    break;
                case PIN_KEK:
                    keyUsage = (byte) 6;
                    break;
                case MAC_KEK:
                    keyUsage = (byte) 7;
                    break;
                case DATA_KEK:
                    keyUsage = (byte) 8;
                    break;
                case DATA_ENC_ONLY:
                    keyUsage = (byte) 9;
                    break;
                case DATA_ENC_KEK:
                    keyUsage = 0x10;
                    break;
                default:
                    keyUsage = symmetricKey.getKeyUsage().getCode();
                    break;
            }
        } else if (key instanceof AsymmetricKey) {
            AsymmetricKey asymmetricKey = (AsymmetricKey) key;

            if (asymmetricKey.getKeyUsage() == null) {
                throw new NSDKIllegalParameterException("Please set key usage.");
            }

            if (asymmetricKey.getKeyType() == null) {
                throw new NSDKIllegalParameterException("Please set key type.");
            }

            keyType = asymmetricKey.getKeyType().getCode();
            keyUsage = asymmetricKey.getKeyUsage().getCode();
        } else {
            throw new NSDKIllegalParameterException("Key shall be symmetric or asymmetric key.");
        }

        externalKeyManagerModule.deleteKey(key.getKeyID(), keyUsage, keyType);
    }

    /**
     * Verifies the certificate and returns its public key.
     *
     * <p>Note: This is usually used to verify certificates of KDH(Key Distribution Host) during RKI(Remote Key Injection) process.</p>
     *
     * @param isCA <b>[Required]</b> Indicates if the certificate is CA.
     * @param cert <b>[Required]</b> Certificate data.
     * @return The public key of the certificate.
     * @throws NSDKException
     */
    @Override
    public byte[] loadTrustedCert(boolean isCA, byte[] cert) throws NSDKException {
        if (cert == null) {
            throw new NSDKIllegalParameterException("Cert data shall not be null.");
        }

        return externalKeyManagerModule.loadTrustedCert(isCA, cert);
    }

    /**
     * Resets certificates that loaded before.
     *
     * <p>Note: This is used to clear loaded KDH(Key Distribution Host) certificates before starting RKI(Remote Key Injection) process.</p>
     *
     * @throws NSDKException
     */
    @Override
    public void resetCertStatus() throws NSDKException {
        externalKeyManagerModule.resetCertStatus();
    }

    /**
     * Initializes an atomic key loading process.
     *
     * <p>This is usually called before loading keys and work with {@link #commitAtomic(boolean)} to ensure that either all keys loaded, or no keys loaded.</p>
     *
     * @throws NSDKException
     */
    @Override
    public void initAtomic() throws NSDKException {
        externalKeyManagerModule.initAtomic();
    }

    /**
     * Commits the result of the atomic key loading process.
     *
     * @param isSuccessful <b>[Required]</b> The result of the atomic key loading process.
     *                     <ul>
     *                     <li>true: All the keys are loaded successfully.</li>
     *                     <li>false: There is a failure during the atomic key loading process, then all of the keys that already loaded will be reversed.</li>
     *                     </ul>
     * @throws NSDKException
     */
    @Override
    public void commitAtomic(boolean isSuccessful) throws NSDKException {
        externalKeyManagerModule.commitAtomic(isSuccessful);
    }

    @Override
    public void clearSymmetricKeys() throws NSDKException {
        externalKeyManagerModule.clearSymmetricKeys();
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
}
