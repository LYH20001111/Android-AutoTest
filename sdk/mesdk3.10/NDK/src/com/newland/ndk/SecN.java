package com.newland.ndk;

import com.newland.ndk.h.EM_NDK_SEC_CIPHER_MODE;
import com.newland.ndk.h.EM_NDK_SEC_CIPHER_OPERATION;
import com.newland.ndk.h.EM_NDK_SEC_MAC_TYPE;
import com.newland.ndk.h.ST_AESDUKPT_KEYINFO;
import com.newland.ndk.h.ST_NDK_SEC_DUKPT_DERIVATE_DATA;
import com.newland.ndk.h.ST_SEC_KCV_INFO;
import com.newland.ndk.h.ST_SEC_KEY_INFO;
import com.newland.ndk.h.ST_SEC_RSA_KEY;

public class SecN {
	protected SecN(){
		super();
	}

	/**
	 * Get KCV value of key.
	 * Used to check key for both part of session
	 * @param ucKeyType Key type
	 * @param ucKeyIdx Key index
	 * @param pstKcvInfoOut KCV value
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public int NDK_SecGetKcv(byte ucKeyType, byte ucKeyIdx, ST_SEC_KCV_INFO pstKcvInfoOut){
		return NDK_SecGetKcv_m(ucKeyType,ucKeyIdx,pstKcvInfoOut.nCheckMode,pstKcvInfoOut.nLen,pstKcvInfoOut.sCheckBuf);
	}

	/**
	 *Install key.
     *
	 *Include writing and diffusing TLK, TMK and TWK, also choose to check correctness of key with KCV. Key length must be more than 8 bytes.
	 *PED adopts 3-layer key system, and layers from top down are:
	 *TLK-Terminal Key Loading Key
	 *private key held by acquiring bank or POS operator which is directly written by its holder under secured conditions.
	 *each PED terminal has only one such key, so the index No. of key is from 1 to 1.
     *
	 *TMK-Terminal Master Key=Acquirer Master Key
	 *is master key for terminal or called master key for acquiring bank keys of this type can amount to 100 in number, so the index no. is from 1 to 100.
	 *TMK can be directly written under secured conditions, write TMK directly, and make it consistent with MK/SK key system by diffusing TWK.
	 *TWK-Transaction working key = Transaction Pin Key + Transaction MAC Key + Terminal DES Enc Key + Terminal DES DEC/ENC Key
	 *Terminal working key for PIN ciphertext, MAC and other operations. keys of this type can amount to 100 in number, so the index no. is from 1 to 100.
	 *TPK: is to calculate PIN Block after application inputs PIN.
	 *TAK: is to calculate MAC for message communication in the application.
	 *TEK: is to provide DES/TDES encrypted transmission or storage for sensitive data in the application.
	 *TEK: is to provide DES/TDES encryption or decryption for sensitive data in the application.
	 *TWK can be written under secured conditions, TWK directly written is consistent with Fixed Key system. Each key has its index No., length, usage and tag.
	 *wherein key tag is set via API before key is written, in order to authorize this key for use and ensure it won't be misused.
     *
	 *
	 *DUKPT key mechanism:
	 *DUKPT[Derived Unique Key Per Transaction]key management system is designed for providing one unique key for one transaction, and the working key[PIN,MAC]for each transaction differs.
	 *It has introduced KSN [Key Serial Number] concept, KSN is the essential factor to achieve one transaction one key. Each KSN corresponds to a key, different keys can be generated per usage.
	 *10 groups of such keys are allowed, user has to select index No. of group when writing TIK and select proper group index when DUKPT key is used
	 *
	 * @param pstKeyInfoIn Key info
	 * @param pstKcvInfoIn Key check info
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public int NDK_SecLoadKey(ST_SEC_KEY_INFO pstKeyInfoIn, ST_SEC_KCV_INFO  pstKcvInfoIn){
		return NDK_SecLoadKey_m(pstKeyInfoIn.ucScrKeyType,pstKeyInfoIn.ucDstKeyType,pstKeyInfoIn.ucScrKeyIdx,pstKeyInfoIn.ucDstKeyIdx,pstKeyInfoIn.nDstKeyLen,pstKeyInfoIn.DstKeyValue,pstKcvInfoIn.nCheckMode,pstKcvInfoIn.nLen,pstKcvInfoIn.sCheckBuf);
	};

	/**
	 * Install DUKPT key.
	 * @param ucGroupIdx Key group index
	 * @param ucSrcKeyIdx Old key index (Key index that is used to encrypt initial key value)
	 * @param ucKeyLen Key length
	 * @param psKeyValueIn Initial key value
	 * @param psKsnIn KSN value
	 * @param pstKcvInfoIn KCV info
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public int NDK_SecLoadTIK(byte ucGroupIdx, byte ucSrcKeyIdx, byte ucKeyLen, byte[] psKeyValueIn, byte[] psKsnIn, ST_SEC_KCV_INFO  pstKcvInfoIn){
		return NDK_SecLoadTIK_m(ucGroupIdx, ucSrcKeyIdx, ucKeyLen, psKeyValueIn, psKsnIn,  pstKcvInfoIn.nCheckMode,pstKcvInfoIn.nLen,pstKcvInfoIn.sCheckBuf);
	};

	/**
	 * Install ras key
	 * @param ucRsaKeyIndex keyindex
	 * @param pstRsaKeyIn reskey info
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public int NDK_SecLoadRsaKey(byte ucRsaKeyIndex, ST_SEC_RSA_KEY pstRsaKeyIn){
		return NDK_SecLoadRsaKey_m(ucRsaKeyIndex,pstRsaKeyIn.usBits,pstRsaKeyIn.sModulus,pstRsaKeyIn.sExponent,pstRsaKeyIn.reverse);
	};

	/**
	 *Verify offline ciphertext PIN.
	 *Encrypt plaintext PIN with RsaPinKey per EMV spec and then send ciphertext PIN directly to card.
	 * @param ucIccSlot Smart card slot
	 * @param pszExpPinLenIn Password length, can be separated as 0, 4, 6
	 * @param pstRsaKeyIn RSA key data
	 * @param psIccRespOut Response code from smart card
	 * @param ucMode Calculation mode (Support EMV only)
	 * @param unTimeoutMs Timeout in milliseconds
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public int NDK_SecVerifyCipherPin(byte ucIccSlot, String pszExpPinLenIn, ST_SEC_RSA_KEY pstRsaKeyIn, byte[] psIccRespOut, byte ucMode,int unTimeoutMs){
		return NDK_SecVerifyCipherPin_m(ucIccSlot,pszExpPinLenIn, pstRsaKeyIn.usBits,pstRsaKeyIn.sModulus,pstRsaKeyIn.sExponent,pstRsaKeyIn.reverse,psIccRespOut,ucMode,unTimeoutMs);
	};

	/**
	 * Read security module version.
	 * @param pszVerInfoOut Buffer to save version (Less than 16 bytes)
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SecGetVer(byte[] pszVerInfoOut);

	/**
	 * Get random number.
	 * @param nRandLen Length needed
	 * @param pvRandom Buffer to save random number
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SecGetRandom(int nRandLen , byte[] pvRandom);

	/**
	 * set secure config
	 * @param unCfgInfo
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SecSetCfg(int unCfgInfo);

	/**
	 * get secure config
	 * @param punCfgInfo config info
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SecGetCfg(int[] punCfgInfo);
	private native int NDK_SecGetKcv_m(byte ucKeyType, byte ucKeyIdx,int nCheckMode,int nLen, byte[] sCheckBuf);

	/**
	 * Delate all keys.
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	//public native int NDK_SecKeyErase();

	private native int NDK_SecLoadKey_m(byte ucScrKeyType,byte ucDstKeyType,byte ucScrKeyIdx,byte ucDstKeyIdx,int nDstKeyLen,byte[] DstKeyValue,int nCheckMode,int nLen,byte[] sCheckBuf);

	/**
	 * set calculate pinblock/mac time interval
	 * @param unTPKIntervalTimeMs calculate pinblock computing interval
	 * @param unTAKIntervalTimeMs calculate mac computing interval
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SecSetIntervaltime(int unTPKIntervalTimeMs,int unTAKIntervalTimeMs);

	/**
	 * set Function Key
	 * @param ucType key
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SecSetFunctionKey(byte ucType);

	/**
	 * Calculate MAC.
	 * @param ucKeyIdx Key index
	 * @param psDataIn Input data
	 * @param nDataInLen Input data length
	 * @param psMacOut MAC value (8 bytes)
	 * @param ucMod MAC calculation mode (EM_SEC_MAC)
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SecGetMac(byte ucKeyIdx, byte[] psDataIn, int nDataInLen, byte[] psMacOut, byte ucMod);

	/**
	 * Get PIN Block.
	 * @param ucKeyIdx Key index
	 * @param pszExpPinLenIn Password length (Can be separated as 0, 4, 6)
	 * @param pszDataIn PIN BLOCK per ISO9564
	 * @param psPinBlockOut Outpur PIN Block
	 * @param ucMode Calculation mode (EM_SEC_PIN)
	 * @param nTimeOutMs Timeout in milliseconds
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SecGetPin(byte ucKeyIdx, String pszExpPinLenIn,String pszDataIn, byte[] psPinBlockOut, byte ucMode,int nTimeOutMs);

	/**
	 * Calculate DES.Do DES calculation with specified key index 1-100
	 * @param ucKeyType DES key type
	 * @param ucKeyIdx DES key type
	 * @param psDataIn Input data
	 * @param nDataInLen Input data length
	 * @param psDataOut Output data
	 * @param ucMode Encryption mode (EM_SEC_DES)
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SecCalcDes(byte ucKeyType, byte ucKeyIdx, byte[] psDataIn, int nDataInLen, byte[] psDataOut, byte ucMode);

	/**
	 *Verify offline plaintext PIN.
	 *Send plaintext PIN BlOCK directly to card.
	 * @param ucIccSlot Smart card slot
	 * @param pszExpPinLenIn Password length, can be separated as 0, 4, 6
	 * @param psIccRespOut Response code from smart card
	 * @param ucMode Calculation mode (Support EMV only)
	 * @param unTimeoutMs Timeout in milliseconds
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SecVerifyPlainPin(byte ucIccSlot, String pszExpPinLenIn, byte[] psIccRespOut, byte ucMode,int unTimeoutMs);
	private native int NDK_SecVerifyCipherPin_m(byte ucIccSlot, String pszExpPinLenIn, int usBits,byte[] sModulus,byte[] sExponent,byte[] reverse, byte[] psIccRespOut, byte ucMode,int unTimeoutMs);
	private native int NDK_SecLoadTIK_m(byte ucGroupIdx, byte ucSrcKeyIdx, byte ucKeyLen, byte[] psKeyValueIn, byte[] psKsnIn, int nCheckMode,int nLen,byte[] sCheckBuf);

	/**
	 * Get KSN.
	 * @param ucGroupIdx Key group index
	 * @param psKsnOut Current KSN
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SecGetDukptKsn(byte ucGroupIdx, byte[] psKsnOut);

	/**
	 * Increase KSN.
	 * @param ucGroupIdx Key group index
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SecIncreaseDukptKsn(byte ucGroupIdx);

	/**
	 * Get PIN Block with DUKPT key.
	 * @param ucGroupIdx Key group index
	 * @param pszExpPinLenIn Password length (Can be separated as 0, 4, 6)
	 * @param psDataIn Input PIN block per ISO9564
	 * @param psKsnOut Current KSN
	 * @param psPinBlockOut Output PIN Block
	 * @param ucMode Calculation mode
	 * @param unTimeoutMs Timeout in milliseconds
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SecGetPinDukpt(byte ucGroupIdx, String pszExpPinLenIn, String psDataIn, byte[] psKsnOut, byte[] psPinBlockOut, byte ucMode, int unTimeoutMs);

	/**
	 * Calculate MAC with DUKPT key.
	 * @param ucGroupIdx Key group index
	 * @param psDataIn Input data
	 * @param nDataInLen Input data length
	 * @param psMacOut MAC calculation mode
	 * @param psKsnOut Current KSN
	 * @param ucMode MAC calculation mode
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SecGetMacDukpt(byte ucGroupIdx,byte[] psDataIn, int nDataInLen, byte[] psMacOut, byte[] psKsnOut, byte ucMode);

	/**
	 * Calculate DES with DUKPT key.
	 * @param ucGroupIdx Key group index
	 * @param ucKeyVarType Key type
	 * @param psIV Initial vector
	 * @param usDataInLen Input data length
	 * @param psDataIn Input data
	 * @param psDataOut Output data
	 * @param psKsnOut Current KSN
	 * @param ucMode Encryption mode
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SecCalcDesDukpt(byte ucGroupIdx, byte ucKeyVarType, String psIV, int usDataInLen, byte[] psDataIn,byte[] psDataOut,byte[] psKsnOut ,byte ucMode);
	public native int NDK_SecCalcDesDukpt2(byte ucGroupIdx, byte ucKeyVarType, byte[] psIV, int usDataInLen, byte[] psDataIn,byte[] psDataOut,byte[] psKsnOut ,byte ucMode);
	private native int NDK_SecLoadRsaKey_m(byte ucRsaKeyIndex, int usBits,byte[] sModulus,byte[] sExponent,byte[] reverse);

	/**
	 * encrypt or decrypt of rsa
	 * @param ucRsaKeyIndex key index
	 * @param psDataIn data
	 * @param nDataLen data len
	 * @param psDataOut out data
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SecRecover(byte ucRsaKeyIndex, byte psDataIn, int nDataLen, byte[] psDataOut);

	/**
	 * Get PIN input status.
	 * @param psPinBlock Pinblock data in SEC_VPP_KEY_PIN, SEC_VPP_KEY_BACKSPACE, SEC_VPP_KEY_CLEAR, the length of input PIN is saved in the first byte.
	 * @param nStatus PIN input status
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SecGetPinResult(byte[] psPinBlock, int[] nStatus);

	/**
	 *Set key owner.
	 *Passing NULL or an empty string "" to restore key owner to the application.
	 * @param pszName Key owner name (Less than 256)
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SecSetKeyOwner(String pszName);

	/**
	 * Get tamper status.
	 * @param pnStatus Tamper status (EM_SEC_TAMPER_STATUS)
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SecGetTamperStatus(int[] pnStatus);

	/**
	 * Get PIN input status (For DUKPT)
	 * @param psPinBlock Pinblock data
	 * @param psKsn Current KSN
	 * @param nStatus PIN input status
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SecGetPinResultDukpt(byte[] psPinBlock, byte[] psKsn, int[] nStatus);
	//public native int NDK_GetTamperStatus();

	/**
	 * Delate key.
	 * @param ucKeyIdx Key index
	 * @param ucKeyType Key Type (EM_SEC_KEY_TYPE)
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SecKeyDelete(byte ucKeyIdx,byte ucKeyType);
	//public native int NDK_SysGoSuspend_Extern();
	
	public native int NDK_SecGetDrySR(int[] pnVal);
	public native int NDK_SecClear();
	//public native int NDK_SecUserKeyDelete();
	/**
	 * keyboard init
	 * @param num_btn key number
	 * @param func_key fun number
	 * @param out_seq  out key
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_SecVppTpInit(byte[] num_btn, byte[] func_key, byte[] out_seq);


	/**
	 *
	 * @param ucKeyID
	 * @param ucSKType
	 * @param nTSKLen
	 * @param psTSK
	 * @param psPan
	 * @param PinBlockLen
	 * @param psPinBlockIn
	 * @param pstRsaPinKeyIn
	 * @param psIccRespOut
	 * @param ucMode
	 * @return
	 */
	public native int NDK_SecVerifyPIN(byte ucKeyID,
									   byte ucSKType,
									   int nTSKLen,
									   byte[] psTSK,
									   byte[] psPan,
									   int PinBlockLen,
									   byte[] psPinBlockIn,
									   ST_SEC_RSA_KEY pstRsaPinKeyIn,
									   byte[] psIccRespOut,
									   byte ucMode);
	/**
	 *@brief     Generate Message Authentication Code for a block of data.
	 *@param[in] MacType         Full cipher identifier (e.g. SEC_CIPHER_AES_128_CBC)
	 *@param[in] ucKeyID         Key index
	 *@param[in] psIV            Initial Vector
	 *@param[in] unIVSize        IV size, 8 bytes for TDES, 16 bytes for AES
	 *@param[in] psDataIn        Input data
	 *@param[in] nDataInLen      Input data length
	 *@param[in] pAD             Additional data, Pointer to a ST_SEC_SESSION_KEY structure when a session key is used to encrypt data.
	 *                           This means that the key indicated by KeyID is a KEK
	 *@param[in] unADSize        Size of additional data, could be the size of ST_SEC_SESSION_KEY
	 *@param[out] psMacOut       Pointer to output MAC value
	 *@param[out] pnOutLen       Pointer to size of output data
	 *@param[out] psKsnOut       Pointer to output KSN if the encryption key is DUKPT key
	 *@return On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref EM_NAPI_ERR "EM_NAPI_ERR".
	 */
	private native int NAPI_SecGenerateMAC(int MacType, int ucKeyID, byte[] psIV, int unIVSize,
										  byte[] psDataIn, int nDataInLen, byte[] pAD, int unADSize,
										  byte[] psMacOut, int[] pnOutLen, byte[] psKsnOut, int[] nOutKsnLen);

	public class MacOutput {
		private byte[] data;
		private byte[] ksn;
		public byte[] getData() {
			return data;
		}
		public byte[] getKsn() {
			return ksn;
		}
		public MacOutput( byte[] data, byte[] ksn) {
			this.data = data;
			this.ksn = ksn;
		}
	}
	public MacOutput NAPI_SecGenerateMAC(int macType,int keyIndex,byte[] inputData,byte[] iv,byte[] ad){
		byte[] outData = new byte[256];
		int[] outDataLen = new int[1];

		byte[] ksnData = new byte[32];
		int[] ksnDataLen = new int[1];

		int ivLen = (iv==null?0:iv.length);
		int adLen = (ad==null?0:ad.length);
		int ret = this.NAPI_SecGenerateMAC(macType,keyIndex,iv, ivLen,inputData,inputData.length,
				ad,adLen,outData,outDataLen,ksnData,ksnDataLen);
		byte[] data = null; byte[] ksn = null;
		if(ret == 0){
			if(outDataLen[0] > 0){
				data = new byte[outDataLen[0]];
				System.arraycopy(outData,0,data,0,data.length);
			}
			if(ksnDataLen[0] > 0){
				ksn = new byte[ksnDataLen[0]];
				System.arraycopy(ksnData,0,ksn,0,ksn.length);
			}
			return new MacOutput(data,ksn);
		}
		return null;
	}

	public int NDK_SecCalcAesDukpt(byte ucGroupIdx, ST_NDK_SEC_DUKPT_DERIVATE_DATA pstDerivateInfo,
								   byte[] psIV, int usDataInLen, byte[] psDataIn, byte[] psDataOut, byte[] psKsnOut,
								   EM_NDK_SEC_CIPHER_MODE ucCipherMode, EM_NDK_SEC_CIPHER_OPERATION ucCiperOperation){
		return NDK_SecCalcAesDukpt(ucGroupIdx,pstDerivateInfo.KeyAlg.ordinal(),pstDerivateInfo.DerivateUsage.ordinal(),pstDerivateInfo.nKeyLen,
				psIV, usDataInLen,psDataIn,psDataOut,psKsnOut, ucCipherMode.ordinal(),ucCiperOperation.ordinal());
	}
	public native int NDK_SecCalcAesDukpt(byte ucGroupIdx, int KeyAlg, int DerivateUsage, int nKeyLen,
										  byte[] psIV, int usDataInLen, byte[] psDataIn, byte[] psDataOut, byte[] psKsnOut,
										  int ucCipherMode, int ucCiperOperation);

	public int NDK_SecGetPinAesDukpt(byte ucGroupIdx, ST_NDK_SEC_DUKPT_DERIVATE_DATA pstDerivateInfo,
											byte[] pszExpPinLenIn, byte[] psPan, byte[] psKsnOut, byte[] psPinBlockOut, int ucMode, int unTimeoutMs){
		return NDK_SecGetPinAesDukpt(ucGroupIdx,pstDerivateInfo.KeyAlg.ordinal(),pstDerivateInfo.DerivateUsage.ordinal(),pstDerivateInfo.nKeyLen,
				pszExpPinLenIn,psPan,psKsnOut,psPinBlockOut,ucMode,unTimeoutMs);
	}
	public native int NDK_SecGetPinAesDukpt(byte ucGroupIdx,int KeyAlg, int DerivateUsage, int nKeyLen,
											byte[] pszExpPinLenIn, byte[] psPan, byte[] psKsnOut, byte[] psPinBlockOut, int ucMode, int unTimeoutMs);

	public native int NDK_SecGetPinResultAesDukpt(byte[] psPinBlock, byte[] psKsn, int[] nStatus);

	public int NDK_SecGetMacAesDukpt(byte ucGroupIdx, ST_NDK_SEC_DUKPT_DERIVATE_DATA pstDerivateInfo,
									 byte[] psDataIn, int nDataInLen, byte[] psMacOut, byte[] psKsnOut, EM_NDK_SEC_MAC_TYPE ucMacType){
		return NDK_SecGetMacAesDukpt(ucGroupIdx,pstDerivateInfo.KeyAlg.ordinal(),pstDerivateInfo.DerivateUsage.ordinal(),pstDerivateInfo.nKeyLen,
			psDataIn, nDataInLen,psMacOut,psKsnOut, ucMacType.ordinal());
	}

	public native int NDK_SecGetMacAesDukpt(byte ucGroupIdx, int KeyAlg, int DerivateUsage, int nKeyLen,
											byte[] psDataIn, int nDataInLen, byte[] psMacOut, byte[] psKsnOut, int ucMacType);
	public native int NDK_SecGetAesDukptKsn(byte ucGroupIdx,byte[] psKsnOut);
	public native int NDK_SecIncreaseAesDukptKsn(byte ucGroupIdx);

	public  int NDK_SecLoadAesDukptKey(byte ucSrcKeyIdx, byte ucSrcKeyType, ST_AESDUKPT_KEYINFO keyinfo, ST_SEC_KCV_INFO pstKcvInfoIn){
		return NDK_SecLoadAesDukptKey(ucSrcKeyIdx,ucSrcKeyType,
				keyinfo.keyIndex, keyinfo.keyType, keyinfo.ksnlen, keyinfo.ksn, keyinfo.keylen, keyinfo.keydatalen, keyinfo.keyvalue,
				pstKcvInfoIn.nCheckMode,pstKcvInfoIn.nLen,pstKcvInfoIn.sCheckBuf);
	}

	public native int NDK_SecLoadAesDukptKey(byte ucSrcKeyIdx, byte ucSrcKeyType,
											 byte keyIndex, byte keyType, int ksnlen, byte[] ksn, int keylen, int keydatalen, byte[] keyvalue,
											 int nCheckMode,int nLen,byte[] sCheckBuf);

	public int NDK_SecLoadKeyRsa(byte ucSrcKeyIdx, byte ucSrcKeyType, byte ucRsaKeyIndex,
										ST_SEC_RSA_KEY pstRsaKeyIn){
		return NDK_SecLoadKeyRsa(ucSrcKeyIdx,ucSrcKeyType,ucRsaKeyIndex,
				pstRsaKeyIn.usBits,pstRsaKeyIn.sModulus,pstRsaKeyIn.sExponent,pstRsaKeyIn.reverse);
	}

	public native int NDK_SecLoadKeyRsa(byte ucSrcKeyIdx, byte ucSrcKeyType, byte ucRsaKeyIndex,
										int usBits,byte[] sModulus,byte[] sExponent,byte[] reverse);
	public native int NDK_SecRsaKeyRandomOut(byte ucSrcKeyIdx, byte ucKeyType, byte ucKeyIndex, int keylen, byte[] keyData, int[] keyoutlen);

}
