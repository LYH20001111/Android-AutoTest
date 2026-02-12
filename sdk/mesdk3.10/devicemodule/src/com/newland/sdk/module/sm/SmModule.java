package com.newland.sdk.module.sm;

import com.newland.sdk.module.pin.AlgorithmMode;
import com.newland.smmanager.assistant.ST_RSA_PRIVATE_KEY;
import com.newland.smmanager.assistant.ST_RSA_PUBLIC_KEY;

/** 
 * @description: Sm module
 * @author Suyuming
 * @since 2019/7/28
 */
public interface SmModule {
	
	/**
	 * SHA1 algorithm
	 * @param inputData The data to calculate
	 * @return   Success:the result data
	 *         <p>Fail:null
	 * @since 3.10.01
	 */	
	byte[] calcSHA1(byte[] inputData);
	
	/** 
	 * SHA256 algorithm
	 * @param inputData The data to calculate
	 * @return   Success:the result data
	 *           fail:null
	 * @since 3.10.01
	 */	
	byte[] calcSHA256(byte[] inputData);
	
	/** 
	 * SHA512 algorithm
	 * @param inputData The data to calculate
	 * @return  Success:the result data
	 *       <p> Fail:null
	 * @since 3.10.01
	 */	
	byte[] calcSHA512(byte[] inputData);
	
	/** 
	 *  Generate RSA pair
	 * @param keyBit	The digit of key(512、1024 and 2048)
     * @param exponseType  Exponse Type
	 * @return Success: return Rsa Key
	 *         <p>fail:null
	 * @since 3.10.01
	 */	
	RSAKeyPair genRSAKeyPair(int keyBit, int exponseType);
	
	/** 
	 * Use RSA key to encrypt/decry data
	 * @details	If the key is private key,do encry operation; If the key is public key,do decry operation;<p>
	 			The first byte of dataBuffer must be lesser than the first byte of psModule.and data length must be lesser than 2048<p>
	 * @param	psModule		PsModule
	 * @param	moduleLen		The length of module(512/8,1024/8,2048/8)
	 * @param	exponent		RSA exponent
	 * @param	dataBuffer		DataBuffer
	 * @return  Success:the result data
	 *         <p>Fail:null
	 * @since 3.10.01
	 */
	byte[] rsaRecover(String psModule, int moduleLen, byte[] exponent, byte[] dataBuffer);
	
	/** 
	 * Verify the RSA key
	 * @param	publicKey	Public Key
	 * @param	privateKey	Private Key
	 * @return      0:Success
	 *         <p> -1:Fail
	 *         <p> -6:Error param
	 * @since 3.10.01
	 */
	int rsaKeyPairVerify(ST_RSA_PUBLIC_KEY publicKey, ST_RSA_PRIVATE_KEY privateKey);

	/** 
	 * Generate SM2 key
	 * @return Sucess:SM2 key
	 *        <p> Fail:null
	 * @since 3.10.01
	 */
	Sm2Key genSM2KeyPair();

	/** 
	 * Data encrypt use SM2 public key
	 * @param   pubkey     Public key
	 * @param   inputData       Data to be encry
	 * @return  Sucess:encried data
	 *         <p> Fail:null
	 * @since 3.10.01
	 */
	byte[] sm2Encrypt(byte[] pubkey, byte[] inputData);

	/**
	 * Decry used SM2 private key
	 * @param   prikey   Private key
	 * @param   inputData     Encried data
	 * @return  Sucess:plain data
	 *        <p>Fail :null
	 * @since 3.10.01
	 */
	byte[] sm2Decrypt(byte[] prikey, byte[] inputData);

	/**
	 * Use SM2 key sign
	 * @details Digest: (r,s)=sign(digest,key)
	 * @param   prikey  Private key
	 * @param   digest  Digest of the data to be signed
	 * @return  Sucess:64 bytes of signal message
	 *         <p>Fail:null
	 * @since 3.10.01
	 */
	byte[] sm2Sign(byte[] prikey, byte[] digest);

	/**
	 * Verify with SM2 key
	 * @param   publicKey Public key
	 * @param   digest    Digest
	 * @param   signedData The signed data
	 * @return   <p> 0:Sucess;
	 *           <p>-1:Fail
	 *           <p>-6:Error param
	 *           <p>-4:Open file fail
	 * @since 3.10.01
	 */
	int sm2Verify(byte[] publicKey, byte[] digest, byte[] signedData);

	/**
	 * Generate SM2 digest
	 * @param	pID		 The PID data,null:use PBOC3.0,the default PID="1234567812345678"
	 * @param	message	 Input data
	 * @param	pubKey	 Public key
	 * @return  <p>Sucess:32 bytes of digest
	 *          <p>Fail:null
	 * @since 3.10.01
	 */
	byte[] sm2GenDigest(byte[] pID, byte[] message, byte[] pubKey);


	/**
	 * SM3  operation init
	 * @return <p>0:sucess
	 *         <p>-1:fail
	 *         <p>-4:open file fail
	 * @since 3.10.01
	 */
	int sm3Start();

	/**
	 * Use SM3 to update a group data,the length of data must be 64 euploid<p>
	 * @param inputData  64-byte integer multiple
	 * @return
	 *        <p> 0:sucess
	 *        <p> -1:fail
	 *        <p> -4:open file fail
	 *        <p>  -6:error param
	 * @since 3.10.01
     */
	int sm3Update(byte[] inputData);

	/**
	 * Calculate the final group data and generate the digest
	 * @param inputData    The final group data
	 * @return   Sucess:32 bytes of digest
	 *          <p>Fail:null
	 * @since 3.10.01
	 */
	byte[] sm3calcFinal(byte[] inputData);

	/**
	 * Cal digest by SM3 key
	 * @param inputData Input data
	 * @return   Sucess:32 bytes of digest
	 *         <p>Fail:null
	 * @since 3.10.01
	 */
	byte[] calcSM3(byte[] inputData);

	/**
	 * SM4 Calculations
	 * @param	pKey	 Input key data
	 * @param	pIVector Initialization Vector( it can be null if the Calculations mode is ECB)
	 * @param	inputData 	Input data
	 * @param	mode	<p>The Calculations mode;</p>
	 *                     <p>0x00：SM4 ECB encrypt mode; </p>
	 *                    <p> 0x01:SM4 ECB decry mode</p>
	 *                    <p>0x02：SM4 CBC encrypt mode; </p>
	 * 	 *               <p> 0x03:SM4 CBC decry mode</p>
	 * @return   Sucess :result data
	 *          <p>Fail:null
	 * @since 3.10.01
	 */
	byte[] calcSM4(byte[] pKey, byte[] pIVector, byte[] inputData, byte mode);

	/**
	 * generate public key and private key
	 * @param index key index
	 * @param keySize key length(just for RSA key)
	 * @param keyType key type，0-RSA; 1-SM2
	 * @return
	 */
	boolean generatePubPriKey(int index, int keySize,int keyType);

	/**
	 * load public key and private key
	 * @param index  key index
	 * @param publicKey public key data
	 * @param privateKey private key data
	 * @param keyType key type，0-RSA; 1-SM2
	 * @return
	 */
	boolean loadPubPriKey(int index,byte[] publicKey, byte[] privateKey,int keyType);

	/**
	 * get public key data
	 * @param index key index
	 * @param keyType key type，0-RSA; 1-SM2
	 * @return
	 */
	byte[] getPulicbKey(int index,int keyType);

	/**
	 * encry or decry data by private key.
	 * @param index key index
	 * @param algotithm encrypt or decrypt option，0x00-encrypt；0x01-decrypt
	 * @param data data to operation
	 * @param keyType  key type，0-RSA; 1-SM2（sm2时，只能私钥解密）
	 * @return
	 */
	byte[] calPrivateKey(int index,int algotithm,byte[] data,int keyType);

	/**
	 * @param algorithmMode
	 * @param masterKeyIndex
	 * @param masterKeyData master key data encrypt by publickey
	 * @param checkValue
	 * @param publicKeyIndex
	 * @param publicKeyType public key type，0-RSA; 1-SM2
	 * @return
	 */
	boolean loadMKByPublicKey(AlgorithmMode algorithmMode, int masterKeyIndex, byte[] masterKeyData, byte[] checkValue,int publicKeyIndex,int publicKeyType);

	/**
	 * sign data by private key
	 * @param privateKeyIndex
	 * @param privateKeyType
	 * @param data
	 * @return
	 */
	byte[] signData(int privateKeyIndex,int privateKeyType,byte[] data);
}
