package com.newland.smmanager;

import com.newland.smmanager.assistant.ST_RSA_PRIVATE_KEY;
import com.newland.smmanager.assistant.ST_RSA_PUBLIC_KEY;

import android.util.Log;

public class SmManager {
	private static final String tag = "SmManager";
	private static SmManager sm;
	static{
		try {
			Log.d(tag, "load ndkapi in SmManager!!!");
			System.loadLibrary("ndkapi");
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}	
	private SmManager(){
		int ret = NDK_Init();
		Log.d(tag," SmManager NDK_Init ret="+ret);
	}
	public static SmManager getSmManager(){
		if(sm == null)
			sm = new SmManager();
		return sm;
	}
	private native int NDK_Init();
	public native int NDK_SecGetCfg(int[] cfg);
	public native int NDK_SecSetCfg(int cfg);
	public native int NDK_AlgSHA1(byte[] psDataIn, int nInlen, byte[] psDataOut);
	public native int NDK_AlgSHA256(byte[] psDataIn, int nInlen, byte[] psDataOut);
	public native int NDK_AlgSHA512(byte[] psDataIn, int nInlen, byte[] psDataOut);
	public int NDK_AlgRSAKeyPairGen( int nProtoKeyBit, int nPubEType, ST_RSA_PUBLIC_KEY pstPublicKeyOut, ST_RSA_PRIVATE_KEY pstPrivateKeyOut){
		int ret = -1;
		short[] b1 = new short[1];
		short[] b2 = new short[1];
		ret = NDK_AlgRSAKeyPairGen_m(nProtoKeyBit,nPubEType,b1,pstPublicKeyOut.modulus,pstPublicKeyOut.exponent,b2,pstPrivateKeyOut.modulus,pstPrivateKeyOut.publicExponent,pstPrivateKeyOut.exponent,pstPrivateKeyOut.prime[0],pstPrivateKeyOut.prime[1],pstPrivateKeyOut.primeExponent[0],pstPrivateKeyOut.primeExponent[1],pstPrivateKeyOut.coefficient);
		if(ret == 0)
		{
			pstPublicKeyOut.bits = b1[0];
			pstPrivateKeyOut.bits = b2[0];
		}
		return ret;
	};
	private native int NDK_AlgRSAKeyPairGen_m( int nProtoKeyBit, int nPubEType, short[] bits,byte[]  modulus,byte[] exponent, short[] bits1,byte[] modulus1,byte[] publicExponent,byte[] exponent1,byte[] prime0,byte[] prime1,byte[] primeExponent0,byte[] primeExponent1,byte[] coefficient);
	public native int NDK_AlgRSARecover(byte[] psModule, int nModuleLen, byte[] psExp, byte[] psDataIn, byte[] psDataOut);
	public int NDK_AlgRSAKeyPairVerify(ST_RSA_PUBLIC_KEY pstPublicKey, ST_RSA_PRIVATE_KEY pstPrivateKey){
		return NDK_AlgRSAKeyPairVerify_m(pstPublicKey.bits,pstPublicKey.modulus,pstPublicKey.exponent,pstPrivateKey.bits,pstPrivateKey.modulus,pstPrivateKey.publicExponent,pstPrivateKey.exponent,pstPrivateKey.prime[0],pstPrivateKey.prime[1],pstPrivateKey.primeExponent[0],pstPrivateKey.primeExponent[1],pstPrivateKey.coefficient);
	};
	private native int NDK_AlgRSAKeyPairVerify_m(short bits,byte[]  modulus,byte[] exponent, short bits1,byte[] modulus1,byte[] publicExponent,byte[] exponent1,byte[] prime0,byte[] prime1,byte[] primeExponent0,byte[] primeExponent1,byte[] coefficient);
	public native int NDK_AlgSM2KeyPairGen( byte[] eccpubkey, byte[] eccprikey );
	public native int NDK_AlgSM2Encrypt( byte[] eccpubkey, byte[] Message, int MessageLen, byte[] Crypto, int[] CryptoLen );
	public native int NDK_AlgSM2Decrypt(byte[] eccprikey, byte[] Crypto, int CryptoLen, byte[] Message, int[] MessageLen );
	public native int NDK_AlgSM2Sign(byte[] eccprikey, byte[] e,byte[] output );
	public native int NDK_AlgSM2Verify(byte[] pPublicKey, byte[] e, byte[] pSignedData );
	public native int NDK_AlgSM2GenE( int usID, byte[] pID, int usM, byte[] pM,byte[] pubKey, byte[] pHashData);
	public native int NDK_AlgSM3Start();
	public native int NDK_AlgSM3Update( byte[] pDat,  int len );
	public native int NDK_AlgSM3Final( byte[] pDat,int len, byte[] pHashDat );
	public native int NDK_AlgSM3Compute( byte[] pDat, int len, byte[] pHashDat );
	public native int NDK_AlgSM4Compute(byte[] pKey, byte[] pIVector, int len, byte[] pSm4Input, byte[] pSm4Output, byte mode);
	public native int NDK_AlgSM2Sign_YS(int type,byte[] e,byte[] output );
}
