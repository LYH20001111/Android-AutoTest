package com.newland.sdk.me.module.sm;

import android.content.Context;

import com.newland.ndk.FileN;
import com.newland.ndk.NdkApiManager;
import com.newland.sdk.me.module.pininput.MEPinpad;
import com.newland.sdk.module.pin.AlgorithmMode;
import com.newland.sdk.module.pin.CipherExtParams;
import com.newland.sdk.module.pin.CipherResult;
import com.newland.sdk.module.pin.LoadKeyMode;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.module.sm.RSAKeyPair;
import com.newland.sdk.module.sm.Sm2Key;
import com.newland.sdk.module.sm.SmModule;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.AbstractModule;
import com.newland.sdk.utils.ISOUtils;
import com.newland.smmanager.SmManager;
import com.newland.smmanager.assistant.ST_RSA_PRIVATE_KEY;
import com.newland.smmanager.assistant.ST_RSA_PUBLIC_KEY;

import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;

public class MESmModule extends AbstractModule implements SmModule {
	private DeviceLogger deviceLogger = DeviceLoggerFactory.getLogger("MESmModule");
	private MEPinpad mePinpad;
	SmManager sm = null;

	private static final String NDK_FILE = "/appfs/";
	private static final String PUBKEY = "PUBKEY";
	private static final String PRIKEY = "PRIKEY";
	private final Object mNDKLock = new Object();
	private static final String PUBKEY_SM2 = "PUBKEY_SM2";
	private static final String PRIKEY_SM2 = "PRIKEY_SM2";
	private static final String OLD_PUBLICKEY = "PUBKEY1";
	private static final String OLD_PRIVATEKEY = "PRIKEY1";

	public MESmModule(AbstractDevice owner,Context context) {
		super(owner);
		sm = SmManager.getSmManager();
		mePinpad = new MEPinpad(owner, context);
	}

	@Override
	public boolean isStandardModule() {
		return true;
	}

	@Override
	public ModuleType getStandardModuleType() {
		return ModuleType.SM;
	}

	@Override
	public String getExModuleType() {
		return null;
	}

	@Override
	public byte[] calcSHA1(byte[] psDataIn) {
		deviceLogger.debug("[calcSHA1]");
		if (null == psDataIn)
			return null;
		byte[] out = new byte[20];
		int ret = -1;
		ret = sm.NDK_AlgSHA1(psDataIn, psDataIn.length, out);
		if (ret == 0)
			return out;
		return null;
	}

	@Override
	public byte[] calcSHA256(byte[] psDataIn) {
		deviceLogger.debug("[calcSHA256]");
		if (null == psDataIn)
			return null;
		byte[] out = new byte[32];
		int ret = -1;
		ret = sm.NDK_AlgSHA256(psDataIn, psDataIn.length, out);
		if (ret == 0)
			return out;
		return null;
	}

	@Override
	public byte[] calcSHA512(byte[] psDataIn) {
		deviceLogger.debug("[calcSHA512]");
		if (null == psDataIn)
			return null;
		byte[] out = new byte[64];
		int ret = -1;
		ret = sm.NDK_AlgSHA512(psDataIn, psDataIn.length, out);
		if (ret == 0)
			return out;
		return null;
	}

	@Override
	public RSAKeyPair genRSAKeyPair(int keyBit, int exponseType) {
		deviceLogger.debug("[genRSAKeyPair]");
		int ret = -1;
		RSAKeyPair rsa = new RSAKeyPair();
		ST_RSA_PUBLIC_KEY pub = new ST_RSA_PUBLIC_KEY();
		ST_RSA_PRIVATE_KEY pri = new ST_RSA_PRIVATE_KEY();
		ret = sm.NDK_AlgRSAKeyPairGen(keyBit, exponseType, pub, pri);
		if (ret == 0) {
			rsa.pubkey = pub;
			rsa.prikey = pri;
			return rsa;
		}
		return null;
	}

	@Override
	public byte[] rsaRecover(String psModule, int nModuleLen, byte[] psExp, byte[] psDataIn) {
		deviceLogger.debug("[rsaRecover]");
		int ret = -1;
		byte out[] = new byte[nModuleLen];
		try {
			ret = sm.NDK_AlgRSARecover(psModule.getBytes("GBK"), nModuleLen, psExp, psDataIn, out);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		if (ret == 0)
			return out;
		return null;
	}

	@Override
	public int rsaKeyPairVerify(ST_RSA_PUBLIC_KEY pstPublicKey, ST_RSA_PRIVATE_KEY pstPrivateKey) {
		deviceLogger.debug("[rsaKeyPairVerify]");
		int ret = -1;
		ret = sm.NDK_AlgRSAKeyPairVerify(pstPublicKey, pstPrivateKey);
		return ret;
	}

	@Override
	public Sm2Key genSM2KeyPair() {
		Sm2Key sm2 = new Sm2Key();
		int ret = -1;
		byte pub[] = new byte[64];
		byte pri[] = new byte[32];
		ret = sm.NDK_AlgSM2KeyPairGen(pub, pri);
		if (ret == 0) {
			sm2.eccpubKey = pub;
			sm2.eccprikey = pri;
			return sm2;
		}
		return null;
	}

	@Override
	public byte[] sm2Encrypt(byte[] pubkey, byte[] message) {
		if (null == message) {
			return null;
		}
		int ret = -1;
		int len[] = new int[1];
		len[0] = message.length + 96;
		byte out[] = new byte[len[0]];
		ret = sm.NDK_AlgSM2Encrypt(pubkey, message, message.length, out, len);
		if (ret == 0)
			return out;
		return null;
	}

	@Override
	public byte[] sm2Decrypt(byte[] prikey, byte[] crypto) {
		if (null == crypto) {
			return null;
		}
		int ret = -1;
		int len[] = new int[1];
		len[0] = crypto.length - 96;
		byte out[] = new byte[len[0]];
		ret = sm.NDK_AlgSM2Decrypt(prikey, crypto, crypto.length, out, len);
		if (ret == 0)
			return out;
		return null;
	}

	@Override
	public byte[] sm2Sign(byte[] eccprikey, byte[] e) {
		byte out[] = new byte[64];
		int ret = -1;
		ret = sm.NDK_AlgSM2Sign(eccprikey, e, out);
		if (ret == 0)
			return out;
		return null;
	}

	@Override
	public int sm2Verify(byte[] pPublicKey, byte[] e, byte[] pSignedData) {
		int ret = -1;
		ret = sm.NDK_AlgSM2Verify(pPublicKey, e, pSignedData);
		return ret;
	}

	@Override
	public byte[] sm2GenDigest(byte[] pID, byte[] message, byte[] pubKey) {
		if (null == pID || null == message)
			return null;
		byte out[] = new byte[32];
		int ret = -1;
		ret = sm.NDK_AlgSM2GenE(pID.length, pID, message.length, message, pubKey, out);
		if (ret == 0)
			return out;
		return null;
	}

	@Override
	public int sm3Start() {
		return sm.NDK_AlgSM3Start();
	}

	@Override
	public int sm3Update(byte[] data) {
		if (null == data)
			return -6;
		int ret = -1;
		ret = sm.NDK_AlgSM3Update(data, data.length);
		return ret;
	}

	@Override
	public byte[] sm3calcFinal(byte[] data) {
		if (null == data)
			return null;
		byte out[] = new byte[32];
		int ret = -1;
		ret = sm.NDK_AlgSM3Final(data, data.length, out);
		if (ret == 0)
			return out;
		return null;
	}

	@Override
	public byte[] calcSM3(byte[] data) {
		if (null == data)
			return null;
		byte out[] = new byte[32];
		int ret = -1;
		ret = sm.NDK_AlgSM3Compute(data, data.length, out);
		if (ret == 0)
			return out;
		return null;
	}
	private static final int CALCULATE_MAX_LEN = 4096;


	@Override
	public byte[] calcSM4(byte[] pKey, byte[] pIVector, byte[] data, byte mode) {
		if (null == data)
			return null;
		int ret = -1;

		if (pIVector == null) {
			pIVector = new byte[16];
		}
		if (mode == 0x02 || mode == 0x03) {
			int elementLen = 16, maxLen = CALCULATE_MAX_LEN;

			int destLen = (data.length + elementLen - 1) / elementLen * elementLen;
			int count = destLen / maxLen, remainder = destLen % maxLen;

			if ((count == 1 && remainder != 0) || (count > 1)) {
				byte[] srcData = new byte[destLen];
				Arrays.fill(srcData, (byte) 0x00);
				System.arraycopy(data, 0, srcData, 0, data.length);
				byte[] destData = new byte[destLen];
				byte[] srcItem = new byte[maxLen];

				for (int i = 0; i < count; i++) {
					System.arraycopy(srcData, i * maxLen, srcItem, 0, maxLen);
					byte[] result=new byte[srcItem.length];

					ret = sm.NDK_AlgSM4Compute(pKey, pIVector, result.length, srcItem, result, mode);
					if (ret != 0) {
						return null;
					}
					System.arraycopy(result,0,destData,i*maxLen,maxLen);

					byte[] cbcIv = new byte[elementLen];
					System.arraycopy(result, result.length - elementLen, cbcIv, 0, elementLen);
					pIVector = cbcIv;

				}
				if (remainder != 0) {
					byte[] srcRemData = new byte[remainder];
					byte[] result=new byte[srcItem.length];
					System.arraycopy(srcData, count * maxLen, srcRemData, 0, remainder);
					ret = sm.NDK_AlgSM4Compute(pKey, pIVector,  result.length, srcRemData, result, mode);
					if (ret != 0) {
						return null;
					}
					System.arraycopy(result,0,destData,count*maxLen,remainder);

				}
				return destData;
			}

		} else {
			int elementLen = 16, maxLen = CALCULATE_MAX_LEN;
			int destLen = (data.length + elementLen - 1) / elementLen * elementLen;
			int count = destLen / maxLen, remainder = destLen % maxLen;

			if ((count == 1 && remainder != 0) || (count > 1)) {
				byte[] srcData = new byte[destLen];
				Arrays.fill(srcData, (byte) 0x00);
				System.arraycopy(data, 0, srcData, 0, data.length);
				byte[] destData = new byte[destLen];
				byte[] srcItem = new byte[maxLen];

				for (int i = 0; i < count; i++) {
					System.arraycopy(srcData, i * maxLen, srcItem, 0, maxLen);
					byte[] result=new byte[srcItem.length];

					ret = sm.NDK_AlgSM4Compute(pKey, pIVector,  result.length, srcItem, result, mode);
					if (ret != 0) {
						return null;
					}
					System.arraycopy(result,0,destData,i*maxLen,maxLen);
				}
				if (remainder != 0) {
					byte[] srcRemData = new byte[remainder];
					byte[] result=new byte[srcItem.length];
					System.arraycopy(srcData, count * maxLen, srcRemData, 0, remainder);
					ret = sm.NDK_AlgSM4Compute(pKey, pIVector,  result.length, srcItem, result, mode);//pIVector 不可以是null
					if (ret != 0) {
						return null;
					}
					System.arraycopy(result,0,destData,count*maxLen,remainder);
				}
				return destData;

			}
		}

		int dataLen = data.length;
		byte out[] = new byte[dataLen];
		ret = sm.NDK_AlgSM4Compute(pKey, pIVector, dataLen, data, out, mode);
		if (ret == 0){
			return out;

		}

		return null;
	}

	@Override
	public boolean generatePubPriKey(int index, int keySize, int keyType) {
		try {
			deviceLogger.debug("[generatePubPriKey] index:"+index+"; keySize:"+keySize+"; keyType:"+keyType);
			if(keyType==0){
				deviceLogger.debug("[generatePubPriKey] generate RSA key");
				SecureRandom secureRandom = new SecureRandom();
				KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
				keyPairGenerator.initialize(keySize, secureRandom);
				KeyPair keyPair = keyPairGenerator.generateKeyPair();
				byte[] publicKey = keyPair.getPublic().getEncoded();
				byte[] privateKey = keyPair.getPrivate().getEncoded();
				boolean result = saveKey(PUBKEY, ISOUtils.hexString(publicKey), index);
				if (!result) {
					deviceLogger.error("Save PublicKey failed");
					return false;
				}
				result = saveKey(PRIKEY, ISOUtils.hexString(privateKey), index);
				if (!result) {
					deviceLogger.error("Save PrivateKey failed");
					return false;
				}
				return true;
			}else{
				deviceLogger.debug("[generatePubPriKey] generate SM2 key");
				Sm2Key sm2Key = genSM2KeyPair();
				byte[] publicKey = sm2Key.eccpubKey;
				byte[] privateKey = sm2Key.eccprikey;

				boolean result = saveKey(PUBKEY_SM2, ISOUtils.hexString(publicKey), index);
				if (!result) {
					deviceLogger.error("Save PublicKey failed");
					return false;
				}
				result = saveKey(PRIKEY_SM2, ISOUtils.hexString(privateKey), index);
				if (!result) {
					deviceLogger.error("Save PrivateKey failed");
					return false;
				}
				return true;
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean loadPubPriKey(int index, byte[] publicKey, byte[] privateKey, int keyType) {
		try {
			deviceLogger.debug("[loadPubPriKey] index:"+index+"; publicKey:"+(publicKey==null?null:ISOUtils.hexString(publicKey))+";privateKey:"+(privateKey==null?null:ISOUtils.hexString(privateKey))+";keyType:"+keyType);
			if(keyType==0){
				deviceLogger.debug("[loadPubPriKey] load RSA key");
				if(publicKey != null && publicKey.length > 0 && privateKey != null && privateKey.length > 0){
					boolean loadpubResult = saveKey(PUBKEY, ISOUtils.hexString(publicKey), index);
					boolean loadpriResult = saveKey(PRIKEY, ISOUtils.hexString(privateKey), index);
					deviceLogger.debug("[loadPubPriKey] loadpubResult:"+loadpubResult+";loadpriResult:"+loadpriResult);
					if(loadpubResult && loadpriResult){
						return true;
					}
				}
				if (publicKey != null && publicKey.length > 0) {
					return saveKey(PUBKEY, ISOUtils.hexString(publicKey), index);
				}else{
					deviceLogger.error("[loadPubPriKey] RSA publicKey==null");
				}
				if (privateKey != null && privateKey.length > 0) {
					return saveKey(PRIKEY, ISOUtils.hexString(privateKey), index);
				}else{
					deviceLogger.error("[loadPubPriKey] RSA privateKey==null");
				}
			}else{
				deviceLogger.debug("[loadPubPriKey] load SM2 key");
				if(publicKey != null && publicKey.length > 0 && privateKey != null && privateKey.length > 0){
					boolean loadpubResult = saveKey(PUBKEY_SM2, ISOUtils.hexString(publicKey), index);
					boolean loadpriResult = saveKey(PRIKEY_SM2, ISOUtils.hexString(privateKey), index);
					deviceLogger.debug("[loadPubPriKey]load SM2 key loadpubResult:"+loadpubResult+";loadpriResult:"+loadpriResult);
					if(loadpubResult && loadpriResult){
						return true;
					}
				}
				if (publicKey != null && publicKey.length > 0) {
					return saveKey(PUBKEY_SM2, ISOUtils.hexString(publicKey), index);
				}else{
					deviceLogger.error("[loadPubPriKey] SM2 publicKey==null");
				}
				if (privateKey != null && privateKey.length > 0) {
					return saveKey(PRIKEY_SM2, ISOUtils.hexString(privateKey), index);
				}else{
					deviceLogger.error("[loadPubPriKey] SM2 privateKey==null");
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public byte[] getPulicbKey(int index, int keyType) {
		try {
			deviceLogger.debug("[getPulicbKey] index:"+index+"; keyType:"+keyType);
			if(keyType==0){
				deviceLogger.debug("[getPulicbKey] get RSA public key");
				String key = getKey(PUBKEY, index);
				if (key != null && key.length() > 0) {
					byte[] pubKey = ISOUtils.hex2byte(key);
					deviceLogger.debug("[getPulicbKey] pubKey:"+(pubKey==null?null:ISOUtils.hexString(pubKey)));
					return pubKey;
				}
				key = getKey(OLD_PUBLICKEY, 1);//兼容嘉联早期存的地址
				if (key != null && key.length() > 0) {
					return ISOUtils.hex2byte(key);
				}
			}else{
				deviceLogger.debug("[getPulicbKey] get SM2 public key");
				String key = getKey(PUBKEY_SM2, index);
				if (key != null && key.length() > 0) {
					byte[] pubKey = ISOUtils.hex2byte(key);
					deviceLogger.debug("[getPulicbKey] pubKey:"+(pubKey==null?null:ISOUtils.hexString(pubKey)));
					return pubKey;
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public byte[] calPrivateKey(int index, int algotithm, byte[] data, int keyType) {
		try {
			deviceLogger.debug("[calPrivateKey] index:"+index+"; keyType:"+keyType);
			deviceLogger.debug("[calPrivateKey] algotithm:"+algotithm+"; data:"+(data==null?null:ISOUtils.hexString(data)));
			if(data==null || data.length<=0){
				deviceLogger.error("[calPrivateKey]  parama error, data is null");
				return null;
			}

			if(keyType==0){
				String key = getKey(PRIKEY, index);
				if (key!= null && key.length() > 0) {
					byte[] result = null;
					if (algotithm == 0x00) {
						deviceLogger.debug("[calPrivateKey]  RSA private key encrypt");
						result = encryptByRSAPrivateKey(ISOUtils.hex2byte(key), data);
					} else if (algotithm == 0x01) {
						deviceLogger.debug("[calPrivateKey] RSA private key decrypt");
						result =  decryptByRSAPrivateKey(ISOUtils.hex2byte(key), data);
					}
					deviceLogger.debug("[calPrivateKey] RSA private key decrypt result:"+(result==null?null:ISOUtils.hexString(result)));
					return result;
				}
				key = getKey(OLD_PRIVATEKEY, 1);//兼容嘉联旧地址
				if (key != null && key.length() > 0) {
					if (algotithm == 0x00) {
						return encryptByRSAPrivateKey(ISOUtils.hex2byte(key), data);
					} else if (algotithm == 0x01) {
						return decryptByRSAPrivateKey(ISOUtils.hex2byte(key), data);
					}
				}
				deviceLogger.error("[calPrivateKey]  RSA private key doesn't exist");
				return null;
			}else{
				String key = getKey(PRIKEY_SM2, index);
				if (key!= null && key.length() > 0) {
					byte[] result = sm2Decrypt(ISOUtils.hex2byte(key), data);
					deviceLogger.debug("[calPrivateKey] SM2 private key decrypt result:"+(result==null?null:ISOUtils.hexString(result)));
					return result;
				}else{
					deviceLogger.error("[calPrivateKey]  SM2 private key doesn't exist");
					return null;
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean loadMKByPublicKey(AlgorithmMode algorithmMode, int masterKeyIndex, byte[] masterKeyData, byte[] checkValue, int publicKeyIndex, int publicKeyType) {
		try {
			deviceLogger.debug("[loadMKByPublicKey]masterKeyIndex:"+masterKeyIndex+";masterKeyData:"+(masterKeyData==null?null:ISOUtils.hexString(masterKeyData))+";checkValue:"+(checkValue==null?null:ISOUtils.hexString(checkValue)));
			deviceLogger.debug("[loadMKByPublicKey]publicKeyIndex:"+publicKeyIndex+";publicKeyType:"+publicKeyType);
			if(algorithmMode==null){
				algorithmMode = AlgorithmMode.DES;
			}
			byte[] mkData =calPrivateKey(publicKeyIndex, 1, masterKeyData, publicKeyType);//私钥解密
			deviceLogger.debug("[loadMKByPublicKey]mkData:"+(mkData==null?null:ISOUtils.hexString(mkData)));
			if(mkData!=null && mkData.length==19){
				byte[] keyData = new byte[16];
				System.arraycopy(mkData,0,keyData,0,16);
				byte[] kcv= new byte[3];
				System.arraycopy(mkData,16,kcv,0,3);
				return mePinpad.loadMasterKey(LoadKeyMode.PLAIN,algorithmMode,masterKeyIndex,keyData,kcv,null);
			}else if(mkData!=null && mkData.length==16 && (checkValue!=null && (checkValue.length==3 || checkValue.length==4))){
				return mePinpad.loadMasterKey(LoadKeyMode.PLAIN,algorithmMode,masterKeyIndex,mkData,checkValue,null);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public byte[] signData(int privateKeyIndex, int privateKeyType, byte[] data) {
		try {
			deviceLogger.debug("[signData]privateKeyIndex:"+privateKeyIndex+";privateKeyType:"+privateKeyType+";data:"+(data==null?null:ISOUtils.hexString(data)));
			if(privateKeyType==0){
				String priKey = getKey(PRIKEY, privateKeyIndex);
				PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(ISOUtils.hex2byte(priKey));
				KeyFactory keyFactory = KeyFactory.getInstance("RSA");
				PrivateKey key = keyFactory.generatePrivate(keySpec);
				Signature signature = Signature.getInstance("MD5withRSA");
				signature.initSign(key);

				signature.update(data);
				byte[] signData = signature.sign();
				deviceLogger.debug("RSA signData:"+(signData==null?null:ISOUtils.hexString(signData)));
				return signData;
			}else{
				String priKey = getKey(PRIKEY_SM2, privateKeyIndex);
				byte[] signData = sm2Sign(ISOUtils.hex2byte(priKey),data);
				deviceLogger.debug("SM2 signData:"+(signData==null?null:ISOUtils.hexString(signData)));
				return signData;
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * 保存公私钥
	 * @param key
	 * @param value
	 * @param index
	 * @return
	 */
	private boolean saveKey(String key, String value, int index) {
		synchronized (mNDKLock) {
			int fd = -1;
			FileN file = NdkApiManager.getNdkApiManager().getFileN();
			try {
				String filePath = getFilePath(key, index);
				fd = file.NDK_FsOpen(filePath, "w");
				if (fd < 0) {
					deviceLogger.error("[saveKey] NDK_FsOpen fd = " + fd);
					return false;
				}
				byte[] keyDataByte = ISOUtils.hex2byte(value);

				int writeLen = file.NDK_FsWrite(fd, keyDataByte, keyDataByte.length);
				if (writeLen != keyDataByte.length) {
					deviceLogger.error("[saveKey] NDK_FsWrite writeLen = " + writeLen + ", value length = " + keyDataByte.length);
					return false;
				}
				deviceLogger.debug("[saveKey] saveKey key = " + key + ", filePath = " + filePath + ", value length = " + keyDataByte.length);
				int clrWriteLen = file.NDK_FsTruncate(filePath,  writeLen);//保留实际写的长度，防止原来数据比现在数据长，后续获取的数据，有部分是之前的
				deviceLogger.debug("[saveKey] NDK_FsWrite clrWriteLen:"+clrWriteLen);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				deviceLogger.error("[saveKey] saveKey exception, " + e.getMessage());
				return false;
			} finally {
				int ret = file.NDK_FsClose(fd);
				if (ret != 0) {
					deviceLogger.error("[saveKey] NDK_FsClose ret = " + ret + ", fd = " + fd);
				}
			}
		}
	}

	private String getFilePath(String key, int index) {
		return NDK_FILE + key + index;
	}

	/**
	 * RSA私钥解密
	 * @param privateKey
	 * @param data
	 * @return
	 */
	private byte[] decryptByRSAPrivateKey(byte[] privateKey, byte[] data) {
		try {
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(privateKey);
			KeyFactory keyf = KeyFactory.getInstance("RSA");
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, priKey);
			return cipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
			return new byte[0];
		}
	}

	/**
	 * RSA 私钥加密
	 * @param privateKey 私钥数据
	 * @param data 待加密数据
	 * @return
	 */
	private byte[] encryptByRSAPrivateKey(byte[] privateKey, byte[] data) {
		try {
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(privateKey);
			KeyFactory keyf = KeyFactory.getInstance("RSA");
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, priKey);
			return cipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 从NDK文件里取私钥数据
	 * @param key
	 * @param index 密钥索引
	 * @return
	 */
	private String getKey(String key, int index) {
		synchronized (mNDKLock) {
			FileN file = NdkApiManager.getNdkApiManager().getFileN();
			int fd = -1;
			try {
				String filePath = getFilePath(key, index);
				fd = file.NDK_FsOpen(filePath, "r");
				if (fd < 0) {
					deviceLogger.error("NDK_FsOpen failed fd = " + fd);
					return null;
				}
				int[] size = new int[1];
				int result = file.NDK_FsFileSize(filePath,size);
				deviceLogger.error("[getKey] NDK_FsFileSize: "+result);
				byte[] keyDataByte = new byte[size[0]];
				int readLen = file.NDK_FsRead(fd, keyDataByte, keyDataByte.length);
				deviceLogger.error("[getKey] NDK_FsRead: " + readLen);

				byte[] temp = new byte[readLen];
				System.arraycopy(keyDataByte, 0, temp, 0, readLen);
				deviceLogger.debug("getKey key = " + key + ", filePath = " + filePath + ", data length = " + readLen);
				return ISOUtils.hexString(temp);
			} catch (Exception e) {
				e.printStackTrace();
				deviceLogger.error("getKey exception, " + e.getMessage());
				return "";
			} finally {
				int ret = file.NDK_FsClose(fd);
				if (ret != 0) {
					deviceLogger.error("NDK_FsClose ret = " + ret + ", fd = " + fd);
				}
			}
		}
	}

}
