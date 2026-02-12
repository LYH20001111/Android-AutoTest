package com.newland.nsdk.core.api.external.keymanager;

import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.crypto.AlgorithmParameters;
import com.newland.nsdk.core.api.common.crypto.AsymAlgorithmParameters;
import com.newland.nsdk.core.api.common.crypto.KCVMode;
import com.newland.nsdk.core.api.common.crypto.PaddingMode;
import com.newland.nsdk.core.api.common.keymanager.AsymAlgInfo;
import com.newland.nsdk.core.api.common.keymanager.AsymKeyType;
import com.newland.nsdk.core.api.common.keymanager.AsymKeyUsage;
import com.newland.nsdk.core.api.common.keymanager.AsymmetricKey;
import com.newland.nsdk.core.api.common.keymanager.CipherMode;
import com.newland.nsdk.core.api.common.keymanager.Key;
import com.newland.nsdk.core.api.common.keymanager.KeyGenerateMethod;
import com.newland.nsdk.core.api.common.keymanager.KeyInfoID;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.api.common.keymanager.KeyUsage;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.common.exception.NSDKException;

/**
 * <b>[External Module]</b> Provides the ability to manage keys in the PIN pad.
 *
 * <p>How to get this module:</p>
 * <pre>
 *     ExtKeyManager extKeyManager = (ExtKeyManager)ExtNSDKModuleManagerImpl.getInstance().getModule(ModuleType.EXT_KEY_MANAGER);
 * </pre>
 */
public interface ExtKeyManager extends Module {
    /**
     * Generates symmetric/asymmetric key in PIN pad under the protection of specified symmetric key.
     *
     * <p>Example:</p>
     * <pre>
     *     // Case 1: Load symmetric key
     *     SymmetricKey sourceKey = new SymmetricKey();
     *     SymmetricKey dstKey = new SymmetricKey();
     *
     *     AlgorithmParameters algorithmParameters = new AlgorithmParameters();
     *     algorithmParameters.setCipherMode(CipherMode.ECB);
     *
     *     sourceKey.setKeyID((byte)1);
     *     sourceKey.setKeyType(KeyType.DES);
     *     sourceKey.setKeyUsage(KeyUsage.KEK);
     *
     *     dstKey.setKeyID((byte)2);
     *     dstKey.setKeyType(KeyType.DES);
     *     dstKey.setKeyUsage(KeyUsage.DATA);
     *     dstKey.setKeyLen(16);
     *     dstKey.setKeyData(ISOUtils.hex2byte("253C9D9D7C2FBBFA253C9D9D7C2FBBFA"));
     *
     *     try {
     *         extKeyManager.generateKey(KeyGenerateMethod.CIPHER, algorithmParameters, sourceKey, dstKey);
     *     } catch (NSDKException e) {
     *         // Handle the exception
     *     }
     *
     *     // Case 2: Load asymmetric key
     *     String certEnc = FileUtils.readFromAssets(context, "kms_enc.pem");
     *     byte[] certEncBuf = certEnc.getBytes();
     *     byte[] keyData;
     *
     *     try {
     *         keyData = keyManager.loadTrustedCert(false, certEncBuf);
     *     } catch (NSDKException e) {
     *         // Handle the error
     *         return;
     *     }
     *
     *     AsymmetricKey distributionKey = new AsymmetricKey();
     *     distributionKey.setKeyType(AsymKeyType.RSA);
     *     distributionKey.setKeyUsage(AsymKeyUsage.KEY_DISTRIBUTION);
     *     distributionKey.setKeyID((byte)3);
     *     distributionKey.setKeyData(keyData);
     *     distributionKey.setKeyLen(keyData.length);
     *
     *     try {
     *         extKeyManager.generateKey(KeyGenerateMethod.CLEAR, null, null, distributionKey, certEncBuf);
     *     } catch (NSDKException e) {
     *         // Handle the error
     *     }
     * </pre>
     *
     * @param method              <b>[Required]</b> Key injection method， see {@link KeyGenerateMethod}.
     *                            <ul>
     *                            <li>For most cases, set this to {@link KeyGenerateMethod#CIPHER}</li>
     *                            <li>For TR31 keys, set this to {@link KeyGenerateMethod#TR31}</li>
     *                            <li>For Giske keys, set this to {@link KeyGenerateMethod#GISKE}</li>
     *                            </ul>
     * @param algorithmParameters <b>[Optional]</b> Algorithm parameters, used when external device based on Forth firmware. See {@link AlgorithmParameters}
     *                            <ul>
     *                            <li>Default cipher mode: {@link CipherMode#ECB}</li>
     *                            <li>Default padding mode: {@link PaddingMode#NONE}</li>
     *                            <li>IV is required when cipher mode is {@link CipherMode#CBC}</li>
     *                            </ul>
     * @param srcKey              <b>[Required]</b> The key to protect the target key.
     *                            <ul>
     *                            <li>For external device based on Forth firmware, the following parameters are required:</li>
     *                                <ul>
     *                                <li>Key index</li>
     *                                <li>Key type</li>
     *                                <li>Key usage</li>
     *                                </ul>
     *                            <li>Otherwise, only key index is required.</li>
     *                            </ul>
     * @param dstKey              <b>[Required]</b> Target key to be generated in PIN pad. The following parameters are required:
     *                            <ul>
     *                            <li>Key index</li>
     *                            <li>Key usage</li>
     *                            <li>Key type</li>
     *                            <li>Key data</li>
     *                            <li>Key length</li>
     *                            <li>KSN(Required if it is a DUKPT key)</li>
     *                            <li>KCV(Optional)</li>
     *                            </ul>
     * @param additionalData      <b>[Optional]</b> Additional data. Usually when target key is asymmetric key, use this parameter to set certificate data.
     * @throws NSDKException
     */
    void generateKey(KeyGenerateMethod method, AlgorithmParameters algorithmParameters, SymmetricKey srcKey, Key dstKey, byte[] additionalData) throws NSDKException;

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
     *         byte[] keyData = extKeyManager.generateKey(KeyGenerateMethod.RANDOM_OUT, algorithmParameters, sourceKey, dstKey);
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
    byte[] generateKeyWithAsymKey(KeyGenerateMethod method, AsymAlgorithmParameters algorithmParameters, AsymmetricKey srcKey, SymmetricKey dstKey) throws NSDKException;

    /**
     * Generates asymmetric key.
     * <p>For example:</p>
     * <pre>
     *     AsymmetricKey asymmetricKey = new AsymmetricKey();
     *     asymmetricKey.setKeyID((byte) 249);
     *     asymmetricKey.setKeyType(AsymKeyType.RSA);
     *     asymmetricKey.setKeyUsage(AsymKeyUsage.KEY_DISTRIBUTION);
     *     AsymAlgInfo algInfo = new AsymAlgInfo();
     *     algInfo.setUnBit(2048);
     *     algInfo.setUcRSAPubExp(new byte[] {0x01, 0x00, 0x01});
     *
     *     try {
     *         extKeyManager.generateAsymKey(asymmetricKey, algInfo);
     *     } catch (NSDKException e) {
     *         //Handle an exception
     *     }
     * </pre>
     * @param dstKey       <b>[Required]</b> Target asymmetric key to be injected in PIN pad, see {@link AsymmetricKey}.
     * @param asymAlgInfo  <b>[Required]</b> The asymmetric algorithm information, see {@link AsymAlgInfo}.
     * @throws NSDKException
     */
    void generateAsymKey(AsymmetricKey dstKey, AsymAlgInfo asymAlgInfo) throws NSDKException;

    /**
     * Gets key info.
     *
     * <p>Example:</p>
     * <pre>
     *     // Get KSN
     *     DUKPTKey infoKey = new DUKPTKey();
     *     infoKey.setKeyID((byte)2);
     *     infoKey.setKeyType(KeyType.DES);
     *     infoKey.setKeyUsage(KeyUsage.DUKPT);
     *     try {
     *         byte[] ksn = extKeyManager.getKeyInfo(KeyInfoID.KSN, infoKey);
     *     } catch (NSDKException e) {
     *         // Handle the exception
     *     }
     *
     *     // Get KCV
     *     SymmetricKey desKey = new SymmetricKey();
     *     desKey.setKeyID((byte)2);
     *     desKey.setKeyType(KeyType.DES);
     *     desKey.setKeyUsage(KeyUsage.DATA);
     *     try {
     *         byte[] kcv = extKeyManager.getKeyInfo(KeyInfoID.KCV, desKey);
     *     } catch (NSDKException e) {
     *         // Handle the exception
     *     }
     * </pre>
     *
     * @param infoID <b>[Required]</b> What info to get. See {@link KeyInfoID}.
     * @param key    <b>[Required]</b> Which key's info to get.
     * @return Key info.
     * @throws NSDKException
     */
    byte[] getKeyInfo(KeyInfoID infoID, Key key) throws NSDKException;

    /**
     * Increases DUKPT KSN.
     *
     * @param groupId <b>[Required]</b> DUKPT group id. Value range: [1-250].
     * @throws NSDKException
     */
    void increaseKSN(byte groupId) throws NSDKException;

    /**
     * Deletes a specified key.
     *
     * <p>Example:</p>
     * <pre>
     *     SymmetricKey delKey = new SymmetricKey();
     *     delKey.setKeyID((byte)2);
     *     delKey.setKeyType(KeyType.DES);
     *     delKey.setKeyUsage(KeyUsage.PIN);
     *     try {
     *         extKeyManager.deleteKey(delKey);
     *     } catch (NSDKException e) {
     *         // Handle the exception
     *     }
     * </pre>
     *
     * @param key <b>[Required]</b> Which key to delete.
     * @throws NSDKException
     */
    void deleteKey(Key key) throws NSDKException;

    /**
     * Verifies Newland certificate and returns its public key.
     *
     * <p>Note: This is usually used to verify Newland certificates of KDH(Key Distribution Host) during RKI(Remote Key Injection) process.</p>
     *
     * @param isCA <b>[Required]</b> Indicates if the certificate is CA.
     * @param cert <b>[Required]</b> Newland certificate data.
     * @return The public key of the certificate.
     * @throws NSDKException
     */
    byte[] loadTrustedCert(boolean isCA, byte[] cert) throws NSDKException;

    /**
     * Resets certificates that loaded before.
     *
     * <ul>Note:
     * <li>This is used to clear loaded KDH(Key Distribution Host) certificates before starting RKI(Remote Key Injection) process.</li>
     * <li>This shall be called before {@link #loadTrustedCert(boolean, byte[])}</li>
     * </ul>
     *
     * @throws NSDKException
     */
    void resetCertStatus() throws NSDKException;

    /**
     * Backs up keys.
     *
     * <p>This is usually called before loading keys and work with {@link #commitAtomic(boolean)} to ensure that either all keys loaded, or no keys loaded.</p>
     *
     * @throws NSDKException
     */
    void initAtomic() throws NSDKException;

    /**
     * Commits the result of remote key loading.
     *
     * @param isSuccessful <b>[Required]</b> The result of remote key loading.
     *                     <ul>
     *                     <li>true: All the keys are loaded successfully.</li>
     *                     <li>false: Error occurred during key loading, all of the keys that already loaded will be reversed.</li>
     *                     </ul>
     * @throws NSDKException
     */
    void commitAtomic(boolean isSuccessful) throws NSDKException;

    /**
     * Clears all symmetric keys in the device.
     *
     * @throws NSDKException
     */
    void clearSymmetricKeys() throws NSDKException;
}
