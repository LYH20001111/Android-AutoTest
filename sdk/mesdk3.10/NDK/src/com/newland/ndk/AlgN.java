package com.newland.ndk;

import com.newland.ndk.h.ST_RSA_PRIVATE_KEY;
import com.newland.ndk.h.ST_RSA_PUBLIC_KEY;

public class AlgN {
	
	protected AlgN()
	{
		super();
	}

	/**
	 * Calculate 3DES.
	 * @param psDataIn Buffer for data to encrypt
	 * @param psDataOut Output data
	 * @param psKey Key buffer
	 * @param nKeyLen Key length (8, 16 or 24 only)
	 * @param nMode Encryption mode
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_AlgTDes(byte[] psDataIn, byte[] psDataOut, byte[] psKey, int nKeyLen, int nMode);

	/**
	 * Calculate SHA1.
	 * @param psDataIn Input data
	 * @param nInlen Input data length
	 * @param psDataOut Output data (20 bytes)
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_AlgSHA1(byte[] psDataIn, int nInlen, byte[] psDataOut);

	/**
	 * Calculate SHA256.
	 * @param psDataIn Input number
	 * @param nInlen Input data length
	 * @param psDataOut Output data
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_AlgSHA256(byte[] psDataIn, int nInlen, byte[] psDataOut);
	//public native int NDK_AlgSHA512(byte[] psDataIn, int nInlen, byte[] psDataOut);
	//public native int NDK_AlgRSAKeyPairGen( int nProtoKeyBit, int nPubEType, ST_RSA_PUBLIC_KEY pstPublicKeyOut, ST_RSA_PRIVATE_KEY pstPrivateKeyOut);
	//public native int NDK_AlgRSARecover(byte[] psModule, int nModuleLen, byte psExp, byte psDataIn, byte psDataOut);
	//public native int NDK_AlgRSAKeyPairVerify(ST_RSA_PUBLIC_KEY pstPublicKey, ST_RSA_PRIVATE_KEY pstPrivateKey);

}
