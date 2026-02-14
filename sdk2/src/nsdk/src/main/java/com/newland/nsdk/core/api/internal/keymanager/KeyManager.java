package com.newland.nsdk.core.api.internal.keymanager;

import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.crypto.AlgorithmParameters;
import com.newland.nsdk.core.api.common.crypto.AsymAlgorithmParameters;
import com.newland.nsdk.core.api.common.keymanager.ExportMode;
import com.newland.nsdk.core.api.common.crypto.KCVMode;
import com.newland.nsdk.core.api.common.crypto.PaddingMode;
import com.newland.nsdk.core.api.common.crypto.TR34EncodingMode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.keymanager.AsymAlgInfo;
import com.newland.nsdk.core.api.common.keymanager.AsymKeyType;
import com.newland.nsdk.core.api.common.keymanager.AsymKeyUsage;
import com.newland.nsdk.core.api.common.keymanager.AsymmetricKey;
import com.newland.nsdk.core.api.common.keymanager.CipherMode;
import com.newland.nsdk.core.api.common.keymanager.KDFInfo;
import com.newland.nsdk.core.api.common.keymanager.Key;
import com.newland.nsdk.core.api.common.keymanager.KeyGenerateMethod;
import com.newland.nsdk.core.api.common.keymanager.KeyInfoID;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.api.common.keymanager.KeyUsage;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;

import java.util.Map;

/**
 * Provides the ability to manage keys in the PIN pad.
 *
 * <p>How to get this module:</p>
 * <pre>
 *     KeyManager keyManager = (KeyManager)NSDKModuleManagerImpl.getInstance().getModule(ModuleType.KEY_MANAGER);
 * </pre>
 */
public interface KeyManager extends Module {
    /**
     * Generates symmetric/asymmetric key in PIN pad under the protection of specified symmetric key with default algorithm parameters and IV.
     *
     * <ul>
     *     <li>Cipher Mode: ECB</li>
     *     <li>Padding Mode: NONE</li>
     *     <li>IV: null</li>
     * </ul>
     *
     * <p>Supports the following keys:</p>
     * <ul>
     *     <li>DES</li>
     *     <li>AES</li>
     *     <li>DUKPT</li>
     *     <li>RSA</li>
     *     <li>GISKE</li>
     *     <li>TR31</li>
     * </ul>
     *
     * <p><b>Note:</b></p>
     * <ul>
     *     <li>For DEV devices: KeyGenerateMethod.CLEAR can be used to load symmetric and asymmetric keys.</li>
     *     <li>For PRO devices: the plain text key installation method is exclusively applicable to the installation of DATA/AUTH/ANY type asymmetric keys.
     *     KEY_DISTRIBUTION type asymmetric key must undergo verification of their public key certificate's legitimacy through the LoadTrustCert interface before they are permitted to be injected into the device in plain text.</li>
     * </ul>
     *
     * <p>Example:</p>
     * <pre>
     *     SymmetricKey sourceKey = new SymmetricKey();
     *     SymmetricKey dstKey = new SymmetricKey();
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
     *         keyManager.generateKey(KeyGenerateMethod.CIPHER, sourceKey, dstKey);
     *     } catch (NSDKException e) {
     *         // Handle the exception
     *     }
     * </pre>
     *
     * @param method <b>[Required]</b> Key injection method. See {@link KeyGenerateMethod}.
     * @param srcKey <b>[Required]</b> The key to protect the target key. This can be null when the method is {@link KeyGenerateMethod#CLEAR}.
     *               <ul>
     *               <li>Default key type: {@link KeyType#DES}</li>
     *               <li>Default key usage: {@link KeyUsage#KEK}</li>
     *               <li>Default KCV mode: {@link KCVMode#NONE}</li>
     *               </ul>
     * @param dstKey <b>[Required]</b> Target key to be generated in PIN pad.
     *               <ul> For symmetric key:
     *               <li>Default key type: {@link KeyType#DES}</li>
     *               <li>Default key usage: {@link KeyUsage#KEK}</li>
     *               <li>Default KCV mode: {@link KCVMode#NONE}</li>
     *               </ul>
     *               <ul> For asymmetric key(Only support {@link KeyGenerateMethod#CLEAR} and {@link KeyGenerateMethod#TR31} now):
     *               <li>Default key type: {@link AsymKeyType#RSA}</li>
     *               <li>Default key usage: {@link AsymKeyUsage#AUTH_DATA}</li>
     *               </ul>
     * @throws NSDKException
     */
    void generateKey(KeyGenerateMethod method, SymmetricKey srcKey, Key dstKey) throws NSDKException;

    /**
     * Generates the key in PIN pad under the protection of specified symmetric key.
     *
     * <p>Supports the following keys:</p>
     * <ul>
     *     <li>DES</li>
     *     <li>AES</li>
     *     <li>DUKPT</li>
     *     <li>RSA</li>
     *     <li>GISKE</li>
     *     <li>TR31</li>
     * </ul>
     *
     * <p><b>Note:</b></p>
     * <ul>
     *     <li>For DEV devices: KeyGenerateMethod.CLEAR can be used to load symmetric and asymmetric keys.</li>
     *     <li>For Pro devices: the plain text key installation method is exclusively applicable to the installation of DATA/AUTH/ANY type asymmetric keys. KEY_DISTRIBUTION type asymmetric key must undergo verification of their public key certificate's legitimacy through the LoadTrustCert interface before they are permitted to be injected into the device in plain text.</li>
     * </ul>
     *
     * <p>Example:</p>
     * <pre>
     *     SymmetricKey sourceKey = new SymmetricKey();
     *     SymmetricKey dstKey = new SymmetricKey();
     *
     *     byte[] iv = ISOUtils.hex2byte("1122334455667788");
     *
     *     AlgorithmParameters algorithmParameters = new AlgorithmParameters();
     *     algorithmParameters.setPaddingMode(PaddingMode.ZEROS);
     *     algorithmParameters.setCipherMode(CipherMode.CBC);
     *     algorithmParameters.setIV(iv);
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
     *         keyManager.generateKey(KeyGenerateMethod.CIPHER, algorithmParameters, sourceKey, dstKey);
     *     } catch (NSDKException e) {
     *         // Handle the exception
     *     }
     * </pre>
     *
     * @param method              <b>[Required]</b> Key injection method. See {@link KeyGenerateMethod}
     * @param algorithmParameters <b>[Optional]</b> Algorithm parameters, see {@link AlgorithmParameters}
     *                            <ul>
     *                            <li>Default cipher mode: {@link CipherMode#ECB}</li>
     *                            <li>Default padding mode: {@link PaddingMode#NONE}</li>
     *                            <li>IV is required when cipher mode is {@link CipherMode#CBC}</li>
     *                            </ul>
     * @param srcKey              <b>[Required]</b> The key to protect the target key.
     *                            <ul>
     *                            <li>Default key type: {@link KeyType#DES}</li>
     *                            <li>Default key usage: {@link KeyUsage#KEK}</li>
     *                            <li>Default KCV mode: {@link KCVMode#NONE}</li>
     *                            </ul>
     * @param dstKey              <b>[Required]</b> Target key to be generated in PIN pad.
     *                            <ul> For symmetric key:
     *                            <li>Default key type: {@link KeyType#DES}</li>
     *                            <li>Default key usage: {@link KeyUsage#KEK}</li>
     *                            <li>Default KCV mode: {@link KCVMode#NONE}</li>
     *                            </ul>
     *                            <ul> For asymmetric key(Only support {@link KeyGenerateMethod#CLEAR} and {@link KeyGenerateMethod#TR31} now):
     *                            <li>Default key type: {@link AsymKeyType#RSA}</li>
     *                            <li>Default key usage: {@link AsymKeyUsage#AUTH_DATA}</li>
     *                            </ul>
     * @throws NSDKException
     */
    void generateKey(KeyGenerateMethod method, AlgorithmParameters algorithmParameters, SymmetricKey srcKey, Key dstKey) throws NSDKException;

    /**
     * Generates the key in PIN pad under the protection of specified symmetric key.
     *
     * <p>Supports the following keys:</p>
     * <ul>
     *     <li>DES</li>
     *     <li>AES</li>
     *     <li>DUKPT</li>
     *     <li>RSA</li>
     *     <li>GISKE</li>
     *     <li>TR31</li>
     * </ul>
     *
     * <p><b>Note:</b></p>
     * <ul>
     *     <li>For DEV devices: KeyGenerateMethod.CLEAR can be used to load symmetric and asymmetric keys.</li>
     *     <li>For Pro devices: the plain text key installation method is exclusively applicable to the installation of DATA/AUTH/ANY type asymmetric keys. KEY_DISTRIBUTION type asymmetric key must undergo verification of their public key certificate's legitimacy through the LoadTrustCert interface before they are permitted to be injected into the device in plain text.</li>
     * </ul>
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
     *         keyManager.generateKey(KeyGenerateMethod.CIPHER, algorithmParameters, sourceKey, dstKey);
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
     *         keyManager.generateKey(KeyGenerateMethod.CLEAR, null, null, distributionKey, certEncBuf);
     *     } catch (NSDKException e) {
     *         // Handle the error
     *     }
     * </pre>
     *
     * @param method              <b>[Required]</b> Key injection method. See {@link KeyGenerateMethod}
     * @param algorithmParameters <b>[Optional]</b> Algorithm parameters, see {@link AlgorithmParameters}
     *                            <ul>
     *                            <li>Default cipher mode: {@link CipherMode#ECB}</li>
     *                            <li>Default padding mode: {@link PaddingMode#NONE}</li>
     *                            <li>IV is required when cipher mode is {@link CipherMode#CBC}</li>
     *                            </ul>
     * @param srcKey              <b>[Required]</b> The key to protect the target key.
     *                            <ul>
     *                            <li>Default key type: {@link KeyType#DES}</li>
     *                            <li>Default key usage: {@link KeyUsage#KEK}</li>
     *                            <li>Default KCV mode: {@link KCVMode#NONE}</li>
     *                            </ul>
     * @param dstKey              <b>[Required]</b> Target key to be generated in PIN pad.
     *                            <ul> For symmetric key:
     *                            <li>Default key type: {@link KeyType#DES}</li>
     *                            <li>Default key usage: {@link KeyUsage#KEK}</li>
     *                            <li>Default KCV mode: {@link KCVMode#NONE}</li>
     *                            </ul>
     *                            <ul> For asymmetric key(Only support {@link KeyGenerateMethod#CLEAR} and {@link KeyGenerateMethod#TR31} now):
     *                            <li>Default key type: {@link AsymKeyType#RSA}</li>
     *                            <li>Default key usage: {@link AsymKeyUsage#AUTH_DATA}</li>
     *                            </ul>
     * @param additionalData      <b>[Optional]</b> Additional data. Usually when target key is asymmetric key, use this parameter to set certificate data whose format shall be .pem.
     * @throws NSDKException
     */
    void generateKey(KeyGenerateMethod method, AlgorithmParameters algorithmParameters, SymmetricKey srcKey, Key dstKey, byte[] additionalData) throws NSDKException;

    /**
     * Generate keys with HKDF
     * <p>Example:</p>
     * <pre>
     *     String sn = "Q28720000040";
     *     String salt = "Q123456";
     *     SymmetricKey srcKey = new SymmetricKey();
     *     srcKey.setKeyID((byte) 252);
     *     srcKey.setKeyType(KeyType.DES);
     *     srcKey.setKeyUsage(KeyUsage.KEK);
     *
     *     SymmetricKey dstKey = new SymmetricKey();
     *     dstKey.setKeyID((byte) 2);
     *     dstKey.setKeyUsage(KeyUsage.KEK);
     *     dstKey.setKeyType(KeyType.DES);
     *     dstKey.setKeyLen(24);
     *
     *     KDFInfo hkdfInfo = new KDFInfo();
     *     hkdfInfo.setKDFType(KDFType.HKDF);
     *     hkdfInfo.setMessageDigestType(MessageDigestType.SHA256);
     *     hkdfInfo.setInfo(sn.getBytes(StandardCharsets.US_ASCII);
     *     hkdfInfo.setSalt(salt.getBytes(StandardCharsets.US_ASCII);
     *     try {
     *             mKeyManager.generateKeyWithHKDF(KeyGenerateMethod.HKDF, null, hkdfInfo, srcKey, dstKey);
     *             showMessage("Generate Key With HKDF success.");
     *         }catch (NSDKException e) {
     *             e.printStackTrace();
     *         }
     *
     * </pre>
     *
     *
     * @param method              <b>[Required]</b> Key inject method.For now, only {@link KeyGenerateMethod#HKDF} is supported.
     * @param algorithmParameters <b>[Optional]</b> Algorithm parameters, see {@link AlgorithmParameters}
     *                            <ul>
     *                            <li>Default cipher mode: {@link CipherMode#ECB}</li>
     *                            <li>Default padding mode: {@link PaddingMode#NONE}</li>
     *                            <li>IV is required when cipher mode is {@link CipherMode#CBC}</li>
     *                            </ul>
     * @param kdfInfo             <b>[Required]</b> KDF info, used as padding data for dstKey, see {@link KDFInfo}
     *                            <ul>
     *                            <li>Default KDF type: {@link com.newland.nsdk.core.api.common.keymanager.KDFType#HKDF}</li>
     *                            <li>Default MessageDigestType: {@link com.newland.nsdk.core.api.common.crypto.MessageDigestType#SHA256}</li>
     *                            </ul>
     * @param srcKey              <b>[Required]</b> BDK Key, the key to protect the target key.
     *                            <ul> BDK Key parameters are restricted.
     *                            <li>Key Type: {@link KeyType#DES}</li>
     *                            <li>Key Usage: {@link KeyUsage#KEK}</li>
     *                            <li>Key ID: BDK ID.</li>
     *                            </ul>
     * @param dstKey              <b>[Required]</b> Target key to be generated with HKDF.
     *                            <ul>Only used for Symmetric keys:
     *                            <li>Key Type: {@link KeyType#DES}</li>
     *                            <li>Default Key Usage: {@link KeyUsage#KEK}</li>
     *                            <li>Key Length: 24</li>
     *                            <li>Key ID: User defined</li>
     *                            </ul>
     * @throws NSDKException
     */

    void generateKeyWithHKDF(KeyGenerateMethod method, AlgorithmParameters algorithmParameters, KDFInfo kdfInfo, SymmetricKey srcKey, Key dstKey) throws NSDKException;


    /**
     * Generates symmetric key in PIN pad under the protection of specified asymmetric key.
     *
     * <p><b>Note:</b></p>
     * <ul>
     *     <li>For DEV devices: Only KeyGenerateMethod.CIPHER and KeyGenerateMethod.RANDOM_OUT are allowed.</li>
     *     <li>For PRO devices: Only KeyGenerateMethod.RANDOM_OUT is allowed.</li>
     * </ul>
     *
     * <p>Example:</p>
     * <pre>
     *     AsymmetricKey sourceKey = new AsymmetricKey();
     *     SymmetricKey dstKey = new SymmetricKey();
     *
     *     AsymAlgorithmParameters algorithmParameters = new AsymAlgorithmParameters();
     *     algorithmParameters.setMessageDigestType(MessageDigestType.SHA256);
     *     algorithmParameters.setEncodingMode(AsymEncodingMode.PKCS_V15);
     *
     *     sourceKey.setKeyID((byte)1);
     *     sourceKey.setKeyType(AsymKeyType.RSA);
     *     sourceKey.setKeyUsage(AsymKeyUsage.KEY_DISTRIBUTION);
     *
     *     dstKey.setKeyID((byte)2);
     *     dstKey.setKeyType(KeyType.DES);
     *     dstKey.setKeyUsage(KeyUsage.TR31_KEK);
     *     dstKey.setKeyLen(24);
     *
     *     try {
     *         byte[] keyData = keyManager.generateKeyWithAsymKey(KeyGenerateMethod.RANDOM_OUT, algorithmParameters, sourceKey, dstKey);
     *     } catch (NSDKException e) {
     *         // Handle the exception
     *     }
     * </pre>
     *
     * @param method              <b>[Required]</b> Key injection method. For now, only {@link KeyGenerateMethod#CIPHER} and {@link KeyGenerateMethod#RANDOM_OUT} are supported.
     * @param algorithmParameters Algorithm parameters, see {@link AsymAlgorithmParameters}
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
     * Generates asymmetric keys.
     * <p>For Example:</p>
     * <pre>
     *      try {
     *          //destination key info
     *          AsymmetricKey asymmetricKey = new AsymmetricKey();
     *          asymmetricKey.setKeyID((byte) 15);
     *          asymmetricKey.setKeyType(AsymKeyType.RSA);
     *          asymmetricKey.setKeyUsage(AsymKeyUsage.DATA);
     *          //Algorithm info
     *          AsymAlgInfo asymAlgInfo = new AsymAlgInfo();
     *          asymAlgInfo.setUnBit(2048);
     *          asymAlgInfo.setUcRSAPubExp(new byte[] {0x01, 0x00, 0x00, 0x00, 0x01});
     *
     *          keyManager.generateAsymKey(asymmetricKey, asymAlgInfo);
     *
     *      } catch(NSDKException e) {
     *          //handle exception
     *      }
     *</pre>
     * @param dstKey        <b>[Required]</b> The asymmetric key to be generated,  which keyUsage shall be {@link AsymKeyUsage#AUTH} or {@link AsymKeyUsage#DATA}.
     *                             <ul>
     *                             <li>Supported asymmetric key type:{@link AsymKeyType#RSA}</li>
     *                             <li>Supported asymmetric key usage:{@link AsymKeyUsage#DATA} or {@link AsymKeyUsage#AUTH}</li>
     *                             </ul>
     * @param asymAlgInfo   <b>[Required]</b> The asymmetric algorithm info, see {@link AsymAlgInfo}
     *                             <ul>
     *                      `      <li>unBit: Key size</li>
     *                             <li>ucRSAPubExp: RSA public key index, 5 bytes</li>
     *                             </ul>
     * @throws NSDKException
     */
    void generateAsymKey(AsymmetricKey dstKey, AsymAlgInfo asymAlgInfo) throws NSDKException;

    /**
     * Generate key with Symmetric key.
     * @param method                <b>[Required]</b> The key generation method, see {@link KeyGenerateMethod}.
     * @param algorithmParameters   <b>[Optional]</b> Algorithm parameters, see {@link AlgorithmParameters}
     *                              <ul>
     *                              <li>Default cipher mode: {@link CipherMode#ECB}.</li>
     *                              <li>Default padding mode: {@link PaddingMode#NONE}.</li>
     *                              <li>IV is required when cipher mode is {@link CipherMode#CBC}.</li>
     *                              </ul>
     * @param srcKey                <b>[Required]</b> BDK Key, the key to protect the target key.
     *                              <ul>BDK Key parameters are restricted.
     *                              <li>Key Type: {@link KeyType#DES}</li>
     *                              <li>Key Usage: {@link KeyUsage#KEK}</li>
     *                              <li>Key ID: BDK ID.</li>
     *                              </ul>
     * @param dstKey                <b>[Required]</b> Target key to be generated in PIN pad.
     *                              <ul> For symmetric key:
     *                              <li>Default key type: {@link KeyType#DES}</li>
     *                              <li>Default key usage: {@link KeyUsage#KEK}</li>
     *                              <li>Default KCV mode: {@link KCVMode#NONE}</li>
     *                              </ul>
     * @return When method is {@link KeyGenerateMethod#RANDOM_OUT}, random key will be generated and returned.
     * @throws NSDKException
     */
    byte[] generateKeyWithSymmKey(KeyGenerateMethod method, AlgorithmParameters algorithmParameters, SymmetricKey srcKey, Key dstKey) throws NSDKException;




    /**
     * Deletes a specified key.
     *
     * <p>Example:</p>
     * <pre>
     *     SymmetricKey delKey = new SymmetricKey();
     *     delKey.setKeyID((byte)6);
     *     delKey.setKeyType(KeyType.DES);
     *     delKey.setKeyUsage(KeyUsage.DUKPT);
     *     try {
     *         keyManager.deleteKey(delKey);
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
     *         byte[] ksn = keyManager.getKeyInfo(KeyInfoID.KSN, infoKey);
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
     *         byte[] kcv = mKeyManager.getKeyInfo(KeyInfoID.KCV, desKey);
     *     } catch (NSDKException e) {
     *         // Handle the exception
     *     }
     * </pre>
     *
     * @param infoID <b>[Required]</b> What info to get. See {@link KeyInfoID}
     * @param key    <b>[Required]</b> Which key's info to get.
     * @return Key info.
     * @throws NSDKException
     */
    byte[] getKeyInfo(KeyInfoID infoID, Key key) throws NSDKException;

    /**
     * Sets the key owner of the keys that are going to be injected.
     *
     * @param keyOwner <b>[Required]</b> Key owner which length is less than 256. If it is an empty string, it will clear key owner.
     * @throws NSDKException
     */
    void setKeyOwner(String keyOwner) throws NSDKException;

    /**
     * Gets key owner.
     *
     * @return Key owner.
     * @throws NSDKException
     */
    String getKeyOwner() throws NSDKException;

    /**
     * Increases DES DUKPT KSN.
     *
     * @param groupId <b>[Required]</b>DES DUKPT group id. Value range: [1-250].
     * @throws NSDKException
     * @deprecated Replaced by {@link #increaseKSN(SymmetricKey)}.
     */
    void increaseKSN(byte groupId) throws NSDKException;

    /**
     * Generate random data for TR34 key block.
     * @param len The length of Random data.
     * @return Random data for TR34 key block.
     * @throws NSDKException
     */
    byte[] generateTR34Random(int len) throws NSDKException;

    /**
     * Processes TR34 key block and generate key with target TR34 encoding mode.
     * @param encodingMode EncodingMode for TR34 key block, see {@link TR34EncodingMode}
     * @param asymmetricKey AsymmetricKey for Checking signature and decrypt.
     * @param symmetricKey Kn key information, TR34 Key Block set by setKeyData().
     * @throws NSDKException
     */
    void processTR34KeyBlock(TR34EncodingMode encodingMode, AsymmetricKey asymmetricKey, SymmetricKey symmetricKey) throws NSDKException;

    /**
     * Processes TR34 key block and generate key with target TR34 encoding mode. If the according mode will output block data, it will be set in the return block.
     * @param encodingMode    <b>[Required]</b> Encoding mode for TR34 key block, see {@link TR34EncodingMode}.
     * @param asymmetricKey          <b>[Required]</b> AsymmetricKey for checking signature and decrypt key data.
     * @param symmetricKey          <b>[Required]</b> The destination key, TR34 encryption key block shall be set by {@link SymmetricKey#setKeyData(byte[])}.
     * @param additionalData  <b>[Optional]</b> If the according encoding mode needs additional data, it will be set in this parameter area.
     * @return The output data according to the encoding mode. It is not a necessary part.
     * @throws NSDKException
     */
    byte[] processTR34KeyBlock(TR34EncodingMode encodingMode, AsymmetricKey asymmetricKey, SymmetricKey symmetricKey, byte[] additionalData) throws NSDKException;

    /**
     * Increases DUKPT KSN.
     *
     * @param key <b>[Required]</b>Key. The following required:
     *            <ul>
     *            <li>Key ID, value range: [1-250].</li>
     *            <li>Key type, see {@link KeyType}</li>
     *            </ul>
     * @throws NSDKException
     */
    void increaseKSN(SymmetricKey key) throws NSDKException;

    /**
     * Verifies Newland certificate and returns its public key.
     *
     * <p>Note: This is usually used to verify Newland certificates of KDH(Key Distribution Host) during RKI(Remote Key Injection) process.</p>
     *
     * @param isCA <b>[Required]</b> Indicates if it is CA certificate.
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
     * Gets the number of keys installed for each id.
     * @return The number of keys installed for each id.
     * @throws NSDKException
     */
    Map<Integer, Integer> getSymmKeyNums() throws NSDKException;

    /**
     * Get detailed Symmetric key info by its id.
     * @param id ID of the Symmetric key to be gained.
     * @return Symmetric keys info.
     * @throws NSDKException
     */
    SymmetricKey[] getSymmKeyInfoByID(byte id) throws NSDKException;

    /**
     * Clear all the symmetric keys whose IDs are range from 1 to 250.
     * @throws NSDKException
     */
    void clearSymmetricKeys() throws NSDKException;

    /**
     * Generates a public cert by the asymmetric key which contains CA certificate info.
     * @param caKey          <b>[Required]</b> The asymmetric key which contains CA certificate info.
     * @param cipherCertKey  <b>[Required]</b> The asymmetric key where the generated cipher certificate info saved.
     * @return Generated public cert by the asymmetric key which contains CA certificate info.
     * @throws NSDKException
     */
    String generatePublicCert(AsymmetricKey caKey, Key cipherCertKey) throws NSDKException;

    /**
     * Exports a key with target source key and returns the exported key value.
     * @param exportMode      <b>[Required]</b> The key exported mode.
     * @param sourceKey       <b>[Required]</b> The source key used to export another key.
     * @param dstKey          <b>[Required]</b> The exported key information about key id, usage and type.
     * @param additionalData  <b>[Optional]</b> The additional data for key exportation.
     * @return The exported key value.
     * @throws NSDKException
     */
    byte[] exportKey(ExportMode exportMode, Key sourceKey, Key dstKey, byte[] additionalData) throws NSDKException;

    /**
     * Inject the public key with the received public key information.
     * @param pubKeyInfoMap     <b>[Required]</b> The map of the public key information to be injected to devices.
     * @param verifyParameters  <b>[Required]</b> The verification parameters, currently supports {@link MACVerifyParameters} and {@link SignVerifyParameters} for the two different verification ways.
     * @param data              <b>[Required]</b> The xml data of the public keys.
     * @param additionalData    <b>[Optional]</b> The extension parameter, which is not useful currently.
     * @return The map of the injection public key ID and its injection result.
     * @throws NSDKException
     */
    Map<Byte, Boolean> injectPubKey(Map<AsymmetricKey, String> pubKeyInfoMap, VerifyParameters verifyParameters, byte[] data, byte[] additionalData) throws NSDKException;
}