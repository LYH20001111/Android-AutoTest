/*******************************************************************************
 * Copyright (C) 2019 Newland Payment Technology Co., Ltd All Rights Reserved
 ******************************************************************************/
/* Security Module */
#ifndef NAPI_CRYPTO_EXTD_H
#define NAPI_CRYPTO_EXTD_H

#ifdef __cplusplus
extern "C" {
#endif

#include <stdint.h>
#include "napi_crypto.h"

typedef void* NAPI_HANDLE;

/** @addtogroup Security_Extended
* @{
*/

/**************************************** Extended ********************************************/

/**
 *@brief 		generate the nonce use to authenticate KLD
 *@param[in] nLenRandom 	Length of the nonce
 *@param[out] psRandom 		Generated random number
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

extern int (*NAPI_SecKlaGenNonce)(int nLenRandom,  uchar* psRandom);

/**
 *@brief 		Verify the KLD certificates(PKLD_AUTH, PKLD_ENC) and the nonce signed by PKLD_AUTH; Return the session key encrypted by PKLD_ENC.
 *@param[in] nLenAuthData 		Authentication data length
 *@param[in] psAuthData 		Authentication data
 *@param[in] nLenAuthCert 		Certificate length used for authentication
 *@param[in] psAuthCert 		Certificate data used for authentication
 *@param[in] nLenEncCert		Certificate length used for encryption
 *@param[in] psEncCert 			Certificate data used for encryption
 *@param[out] pnLenSsKeyCyTxt   the Length of Session key
 *@param[out] psSsKeyCyTxt 	    Session key
 *@param[out] keyowner			The name of the key table where the session key is stored
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

extern int (*NAPI_SecKlaMKLDAuth)(int nLenAuthData, uchar* psAuthData, int nLenAuthCert, uchar* psAuthCert, int nLenEncCert, uchar* psEncCert, int* pnLenSsKeyCyTxt, uchar* psSsKeyCyTxt, char * keyowner);
extern int (*NAPI_SecKlaMKLDAuthV2)(int nLenAuthData, uchar *psAuthData, int nLenAuthCert, uchar *psAuthCert, int nLenEncCert, uchar *psEncCert, int *pnLenSsKeyCyTxt, uchar *psSsKeyCyTxt, uchar *keyowner, int *ownerlen);
extern int (*NAPI_SecGetDrySR)(int *pnVal);
extern int (*NAPI_SecClear)(void);
extern int (*NAPI_SecSetCfg)(uint32_t cfg);
extern int (*NAPI_SecGetServKeyOwner)(char *pszOwner);

/**
 *@brief	Initialize the touch screen keyboard.
 *@param[in]	num_btn			10 numeric buttons (VPP_buttons).Each button consists of two dots: "top left" and "bottom right".Each dot consists of (uint16_t x, uint16_t y).				
 *@param[in]	func_key		3 function keys (backspace, cancel, enter, VPP_key [3]).Each button is composed of a key value + button (int key, Vpp_button).
 *@param[out]	out_seq			The output of randomly arranged key values ('0'-'9') corresponds to the input parameter num_btn, which can be used for keyboard display.
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

extern int (*NAPI_SecVppTpInit)(uchar *num_btn, uchar *func_key, uchar *out_seq);

/**
 *@brief	Read security configuration
 *@param[out]	cfg		Configuration information
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
extern int (*NAPI_SecGetCfg)(uint32_t *cfg);


/**
 *@brief	Get KeyOwner
 *@param[in]  nLenOfOwnerBuffer			the nLenOfOwnerBuffer is used as the maximum length for safety.
 *@param[out]	pszOwner		          KeyOwner to be set
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
extern int (*NAPI_SecGetKeyOwner)(int nLenOfOwnerBuffer,char *pszOwner);



typedef struct{	      
	EM_SEC_CRYPTO_KEY_TYPE KeytType;    
    EM_SEC_KEY_USAGE KeyUsage;    
    uchar KeyIdx;      
}ST_SEC_ASYM_KEY_INFO;

typedef struct{	      
	uint unBit;    
	uchar ucRsaPubExp[5];
}ST_SEC_ASYM_ALG_INFO;

typedef struct{	      
	EM_SEC_CRYPTO_KEY_TYPE KeytType;    
    EM_SEC_KEY_USAGE KeyUsage;    
    uchar KeyIdx;      
}ST_SEC_SKKEY_INFO;

typedef enum {
    SEC_MD_NONE=0,    /**< None. (not support yet)*/
    SEC_MD_SHA1,      /**< The SHA-1 message digest. */
    SEC_MD_SHA224,    /**< The SHA-224 message digest. */
    SEC_MD_SHA256,    /**< The SHA-256 message digest. */
    SEC_MD_SHA384,    /**< The SHA-384 message digest. */
    SEC_MD_SHA512,    /**< The SHA-512 message digest. */
    SEC_MD_SM3,       /**(not support yet)*/
    EM_SEC_MD_TYPE_MAX = 65536
} EM_SEC_MD_TYPE;

typedef enum {
    ASYM_RSA_NO_ENCODING = 0, /**(not support yet)*/
    ASYM_RSA_PKCS_V15 = 1, /**< Use PKCS#1 v1.5 encoding. */
    ASYM_RSA_PKCS_V21 = 2, /**< Use PKCS#1 v2.1 encoding. */
    EM_SEC_ASYM_ENCODING_MODE_MAX = 65536
} EM_SEC_ASYM_ENCODING_MODE;

typedef enum {
    ASYM_RSA_DEFAULT = 0,
    ASYM_RSA_PUBLIC,
    ASYM_RSA_PRIVATE,
    EM_SEC_ASYM_CRYPTO_MODE_MAX = 65536
}EM_SEC_ASYM_CRYPTO_MODE;

/**
 *@brief Asymmetric key Injection Data
*/
typedef struct {
    uchar ucKEKIdx;    /**<KEK Index, 1~250 */
	  EM_SEC_CRYPTO_KEY_TYPE KEKType;   /**<key type of the master key: "ASYM"*/
    EM_SEC_KEY_USAGE KEKUsage;    /**<Specify the KEK Usage*/
	  uchar ucKeyIdx;    /**<Index for the injected key, 1~250 */
	  EM_SEC_CRYPTO_KEY_TYPE KeyType;   /**<key type of the key to be injected. e.g. RSA or ECC*/
    EM_SEC_KEY_USAGE KeyUsage;   /**<Usage of the key to be injected (\ref EM_SEC_KEY_USAGE "EM_SEC_KEY_USAGE")*/
    EM_SEC_MD_TYPE MdAlg;   /**<Message digest algorithm for the Asymmetric cipher, MdAlg is used only when EncodingMode is "ASYM_RSA_PKCS_V21"*/
    EM_SEC_ASYM_ENCODING_MODE EncodingMode;   /**<Encoding mode for the Asymmetric cipher */
    int nKeyLen;       /**<Length of the injecting key. Most of the time it is exactly the length of input "pKeyData". But there are some exceptions when using zero paddings of cipher. see comments for "pKeyData"*/
	  uchar *pKeyData;   /**<Pointer to the key data.
                        When the key injection methods is "SEC_KIM_CIPHER":
                        (a) if EncodingMode is "SEC_PADDING_ZEROS", then above "nKeyLen" must be the REAL length of the key. while data in "pKeyData" is multiple of cipher block size and it may be larger then "nKeyLen".
                        E.g. When installing a 24 bytes AES key in ciphertext with zero padding, then "nKeyLen" must be 24, but the firmware will read 32 bytes (multiple of AES block size 16) of data from "pKeyData".
                        (b) if EncodingMode is other values than "SEC_PADDING_ZEROS", then "nKeyLen" will be exactly the length of "pKeyData", and the REAL length of the key will be determined by the firmware after removing the pad bytes.
                        When the key injection method is "SEC_KIM_RANDOM_OUT", then "pKeyData" will be used as output pointer for the random generated session key. The output session key is encrypted by the specified KEK */
    int nKsnLen;       /**<Length of DUKPT KSN*/
    uchar *psKsn;      /**<DUKPT KSN for key type "SEC_KEY_TYPE_DUKPT_IK" */
  	int nADSize;       /**<Length of additional data*/
  	uchar *pAD;        /**<Pointer to the additional data*/
} ST_SEC_ASYM_KEYIN_DATA;
/**
 *@brief	Encrypt data using the specified algorithm and the specified asymmetric key
 *@param[in] pstDataIn 	    Pointer to the key information used for encryption (\ref ST_SEC_ASYM_KEY_INFO "ST_SEC_ASYM_KEY_INFO")
 *@param[in] MdAlg			Hash algorithm mode
 *@param[in] EncodingMode    Data pading mode
 *@param[in] CryptoMode     Data encryption mode
 *@param[in] nDataInLen     Length of data to be encrypted
 *@param[in] psDataIn	    Data to be encrypted
 *@param[out] pnDataOutLen	Data length after encryption
 *@param[out] psDataOut		Point to the data after encryption
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

extern int (*NAPI_SecAsymEncryption)(ST_SEC_ASYM_KEY_INFO *pstKeyinfo, EM_SEC_MD_TYPE MdAlg, EM_SEC_ASYM_ENCODING_MODE EncodingMode, EM_SEC_ASYM_CRYPTO_MODE CryptoMode, int nDataInLen, const unsigned char *psDataIn,  int* pnDataOutLen, unsigned char *psDataOut);

/**
 *@brief	Decrypt data using the specified algorithm and the specified asymmetric key
 *@param[in] pstDataIn 	    Pointer to the key information used for decryption (\ref ST_SEC_ASYM_KEY_INFO "ST_SEC_ASYM_KEY_INFO")
 *@param[in] MdAlg			Hash algorithm mode
 *@param[in] EncodingMode    Data pading mode
 *@param[in] CryptoMode     Data encryption mode
 *@param[in] nDataInLen     Length of data to be decrypted
 *@param[in] psDataIn	    Data to be decrypted
 *@param[out] pnDataOutLen	Data length after decryption
 *@param[out]psDataOut		Point to the data after decryption
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

extern int (*NAPI_SecAsymDecryption)(ST_SEC_ASYM_KEY_INFO *pstKeyinfo, EM_SEC_MD_TYPE MdAlg, EM_SEC_ASYM_ENCODING_MODE EncodingMode, EM_SEC_ASYM_CRYPTO_MODE CryptoMode,int nDataInLen, const unsigned char *psDataIn,  int* pnDataOutLen, unsigned char *psDataOut);

/**
 *@brief	Get data signature
 *@param[in] pstDataIn 	    Pointer to the key information used for signature (\ref ST_SEC_ASYM_KEY_INFO "ST_SEC_ASYM_KEY_INFO")
 *@param[in] MdAlg  		Hash algorithm mode
 *@param[in] EncodingMode    Data pading mode
 *@param[in] nHashLen       The length of the hash data to be signed
 *@param[in] psHash	  		Hash data to be signed
 *@param[out] nSigLen		Signature data length
 *@param[out] psSig			Signature data
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

extern int (*NAPI_SecAsymSign)(ST_SEC_ASYM_KEY_INFO *pstKeyinfo, EM_SEC_MD_TYPE MdAlg, EM_SEC_ASYM_ENCODING_MODE EncodingMode, int nHashLen, const unsigned char *psHash,  int* nSigLen, const unsigned char *psSig);

/**
 *@brief	Verify that the signature is correct
 *@param[in] pstDataIn 	    Pointer to the key information used for signature (\ref ST_SEC_ASYM_KEY_INFO "ST_SEC_ASYM_KEY_INFO")
 *@param[in] MdAlg  		Hash algorithm mode
 *@param[in] EncodingMode    Data pading mode
 *@param[in] nHashLen       The length of the hash data to be signed
 *@param[in] psHash	  		Hash data to be signed
 *@param[in] nSigLen		Signature data length
 *@param[in] psSig			Signature data
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

extern int (*NAPI_SecAsymVerify)(ST_SEC_ASYM_KEY_INFO *pstKeyinfo,  EM_SEC_MD_TYPE MdAlg, EM_SEC_ASYM_ENCODING_MODE EncodingMode, int nHashLen, const unsigned char *psHash,  int nSigLen, const unsigned char *psSig);

/**
*@brief Symmetric Key Injection  with the Asymmetric key
 *@details Inject symmetric key with the asymmetric key for all key types, with different algorithm...
 *@param[in] Method             Key generation method,ref EM_SEC_KEYIN_METHOD "EM_SEC_KEYIN_METHOD"
 *@param[in] pstKGData          All needed data for key injection, ref ST_SEC_ASYM_KEYIN_DATA "ST_SEC_ASYM_KEYIN_DATA"
 *@param[in] pstKcvData         Key check value (KCV) for the injected key, Optional
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref EM_NAPI_ERR "EM_NAPI_ERR". 
*/
extern int (*NAPI_SecAsymGenerateKey)(EM_SEC_KEYIN_METHOD Method, ST_SEC_ASYM_KEYIN_DATA *pstKGData, ST_SEC_KCV_DATA *pstKcvData);

extern int (*NAPI_SecLoadTrustedCert)(char isCA, char * cert, int certlen, char * pubkey, int * pubkeylen);
extern int (*NAPI_SecResetCertStatus)(void);
extern int (*NAPI_SecInitAtomic)(void);
extern int (*NAPI_SecCommitAtomic)(char status);

/**
 *@brief	Encryption certificate of SK1(Not Supported on Android)
 *@param[in] ca_crt				Pointer to the ca_Cert
 *@param[in] nLenServerCert  	The length of the Server Cert 
 *@param[in] psServerCert       Pointer to the Server Cert
 *@param[in] nSKey1Len          The length of the SKey 
 *@param[in] pnLenSsKey1CyTxt	The length of the SKey1 Ciphertext
 *@param[in] psSsKey1CyTxt		the Ciphertext of SKey1	
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
extern int (*NAPI_SecRKIGenSK1)(unsigned char * ca_crt, int nLenServerCert, unsigned char* psServerCert, int nSKey1Len, int* pnLenSsKey1CyTxt, unsigned char* psSsKey1CyTxt);
/**
 *@brief	 Generate sk and store the index corresponding to SKeyIndex(Not Supported on Android)
 *@param[in] nLenSsKey2CyTxt 	The length of the SKey2 Ciphertext
 *@param[in] psSsKey2CyTxt		the Ciphertext of SKey2	
 *@param[in] pstAsymKeyinfo     Pointer to the infomation of AsymKey    
 *@param[in] pstSkKeyinfo       Pointer to the infomation of SkKey      		
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

extern int (*NAPI_SecRKIInjectSK)(int nLenSsKey2CyTxt, unsigned char* psSsKey2CyTxt,ST_SEC_ASYM_KEY_INFO *pstAsymKeyinfo, ST_SEC_SKKEY_INFO *pstSkKeyinfo);


//certficate request generation
typedef void* CSR_HANDLE;

/*
 * X.509 v3 Key Usage Extension flags
 */
#define NAPI_X509_KU_DIGITAL_SIGNATURE            (0x80)  /* bit 0 */
#define NAPI_X509_KU_NON_REPUDIATION              (0x40)  /* bit 1 */
#define NAPI_X509_KU_KEY_ENCIPHERMENT             (0x20)  /* bit 2 */
#define NAPI_X509_KU_DATA_ENCIPHERMENT            (0x10)  /* bit 3 */
#define NAPI_X509_KU_KEY_AGREEMENT                (0x08)  /* bit 4 */
#define NAPI_X509_KU_KEY_CERT_SIGN                (0x04)  /* bit 5 */
#define NAPI_X509_KU_CRL_SIGN                     (0x02)  /* bit 6 */
#define NAPI_X509_KU_ENCIPHER_ONLY                (0x01)  /* bit 7 */
#define NAPI_X509_KU_DECIPHER_ONLY              (0x8000)  /* bit 8 */

/*
 * Netscape certificate types
 */

#define NAPI_X509_NS_CERT_TYPE_SSL_CLIENT         (0x80)  /* bit 0 */
#define NAPI_X509_NS_CERT_TYPE_SSL_SERVER         (0x40)  /* bit 1 */
#define NAPI_X509_NS_CERT_TYPE_EMAIL              (0x20)  /* bit 2 */
#define NAPI_X509_NS_CERT_TYPE_OBJECT_SIGNING     (0x10)  /* bit 3 */
#define NAPI_X509_NS_CERT_TYPE_RESERVED           (0x08)  /* bit 4 */
#define NAPI_X509_NS_CERT_TYPE_SSL_CA             (0x04)  /* bit 5 */
#define NAPI_X509_NS_CERT_TYPE_EMAIL_CA           (0x02)  /* bit 6 */
#define NAPI_X509_NS_CERT_TYPE_OBJECT_SIGNING_CA  (0x01)  /* bit 7 */

/**
 *@brief	 csr process init(Not Supported on Android)
 *@param[in] handle 	  the handle of csr process
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

extern int (*NAPI_SecCSRInit)(CSR_HANDLE* handle);

/**
 *@brief	 set the SubjectName of csr process(Not Supported on Android)
 *@param[in] handle 	        the handle of csr process
 *@param[in] psSubjectName		Pointer to SubjectName
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

extern int (*NAPI_SecCSRSetSubjectName)(CSR_HANDLE handle,const unsigned char* psSubjectName);

/**
 *@brief	 set the key of csr process(Not Supported on Android)
 *@param[in] handle 	        the handle of csr process
 *@param[in] pstKeyinfo		    Pointer to keyinfo of the key
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

extern int (*NAPI_SecCSRSetKey)(CSR_HANDLE handle, ST_SEC_ASYM_KEY_INFO *pstKeyinfo);

/**
 *@brief	 set the alg of csr process(Not Supported on Android)
 *@param[in] handle 	        the handle of csr process
 *@param[in] MdAlg		        Alg
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

extern int (*NAPI_SecCSRSetMdAlg)(CSR_HANDLE handle, EM_SEC_MD_TYPE MdAlg);

/**
 *@brief	 set the KeyUsag of csr process(Not Supported on Android)
 *@param[in] handle 	        the handle of csr process
 *@param[in] ucKeyUsage		    KeyUsag
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

extern int (*NAPI_SecCSRSetKeyUsage)(CSR_HANDLE handle, unsigned short ucKeyUsage);

 /**
 *@brief	 set the CertType of csr process(Not Supported on Android)
 *@param[in] handle 	        the handle of csr process
 *@param[in] ucCertType		    CertType
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

extern int (*NAPI_SecCSRSetNSCertType)(CSR_HANDLE handle, unsigned char ucCertType);

 /**
 *@brief	 set the CA value of csr process(Not Supported on Android)
 *@param[in] handle 	        the handle of csr process
 *@param[in] ifCA		        the value of CA
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

extern int (*NAPI_SecCSRSetIsCA)(CSR_HANDLE handle, int is_ca);

 /**
 *@brief	 set the Extension infomation of csr process(Not Supported on Android)
 *@param[in] handle 	        the handle of csr process
 *@param[in] oid		        Pointer to oid
 *@param[in] oidLen		        oidLen
 *@param[in] val		        Pointer to val
 *@param[in] valLen		        valLen
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

extern int (*NAPI_SecCSRSetExtension)(CSR_HANDLE handle, const char *oid, int oidLen, unsigned char* val, int valLen);

 /**
 *@brief	 generation the pem of csr(Not Supported on Android)
 *@param[in] handle 	        the handle of csr process
 *@param[in] pnOlen		        Pointer to len of outbuf
 *@param[in] psOutBuf		    Pointer to the outbuf
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

extern int (*NAPI_SecCSRGenPem)(CSR_HANDLE handle, int *pnOlen, unsigned char* psOutBuf);

 /**
 *@brief	 generation the der of csr(Not Supported on Android)
 *@param[in] handle 	        the handle of csr process
 *@param[in] pnOlen		        Pointer to len of outbuf
 *@param[in] psOutBuf		    Pointer to the outbuf
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

extern int (*NAPI_SecCSRGenDer)(CSR_HANDLE handle, int *pnOlen, unsigned char* psOutBuf);

 /**
 *@brief	 release the handle of csr(Not Supported on Android)
 *@param[in] handle 	        the handle of csr process
 *@param[in] pnOlen		        Pointer to len of outbuf
 *@param[in] psOutBuf		    Pointer to the outbuf
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

extern int (*NAPI_SecCSRRelease)(CSR_HANDLE handle);


 /**
  *@brief	  ask for a random generated asymmetric key
  *@param[out] handle	   	the handle of generating process
  *@param[in] pstKeyinfo	pointer to the key information (\ref ST_SEC_ASYM_KEY_INFO "ST_SEC_ASYM_KEY_INFO")
  *@param[in] nADSize		size of pAD buffer
  *@param[in] pAD			pointer to the algorithm information(\ref ST_SEC_ASYM_ALG_INFO "ST_SEC_ASYM_ALG_INFO")
  *@return
   On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
 */


extern int (*NAPI_SecGenerateAsymKey)(NAPI_HANDLE* handle, ST_SEC_ASYM_KEY_INFO *pstKeyinfo, int nADSize, uchar *pAD);

/**
 *@brief	 ask for a random generated asymmetric key
 *@param[int] handle	  the handle of generating process
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/


extern int (*NAPI_SecGenerateAsymKeyState)(NAPI_HANDLE handle);
extern int (*NAPI_SecCancelGenerateAsymKey)(NAPI_HANDLE handle);

/** @} */ // end of crypto extended

#ifdef __cplusplus
}
#endif

#endif

/* End of this file*/
