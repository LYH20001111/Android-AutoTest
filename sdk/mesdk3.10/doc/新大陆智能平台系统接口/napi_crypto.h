/*******************************************************************************
 * Copyright (C) 2019 Newland Payment Technology Co., Ltd All Rights Reserved
 ******************************************************************************/
/* Security Module */
#ifndef NAPI_CRYPTO_H
#define NAPI_CRYPTO_H

#ifdef __cplusplus
extern "C" {
#endif

#include <stdint.h>
#include "napi.h"
#include <time.h>
//#undef OVERSEA
//#define OVERSEA 

/** @addtogroup Security
* @{
*@attention Safety restrictions
* -# Key misuse: \n
* a) The NAPI_SecEncryption /NAPI_SecDecryption interface only allows the input of KEY_USE_DATA type keys for calculations; \n
* b) NAPI_SecGenerateMAC interface only allows KEY_USE_MAC type keys; \n
* c) NAPI_SecVPPInit interface only allows KEY_USE_PIN type keys; \n
* d) The NAPI_SecGenerateKey interface only allows KEK_USE_KEK* type keys; \n
* e) The DUKPT key is derived according to the interface function and operation mode, and the function of deriving the PIN key for data encryption and decryption operations is no longer supported. \n
* \n
* -# Key strength restriction: \n
* a) 8 byte key length key installation is not allowed; \n
* b) The strength of the master key must be greater than the strength of the destination key; \n
* \n
* -# Key uniqueness restriction: \n
* a) Two keys with the same key value are not allowed to be installed on the device; \n
* \n
* -# Key anti-exhaustive restrictions: \n
* a) Only 120 PIN entries are allowed within an hour; \n
* b) Only 2000 data encryption and decryption are allowed in one hour \n
* \n
* -# DUKPT key derivation restriction: \n
* a) The DUKPT key allows only one PIN encryption operation, and then the KSN needs to be changed and derived again before the next PIN operation can be continued; \n
* \n
* -# Interface function restrictions: \n
* a) The interface no longer supports SEC_DES_KEYLEN_8/SEC_DES_KEYLEN_16/SEC_DES_KEYLEN_24 mode to specify the key operation length, and the operation needs to be strictly in accordance with the installation key length; \n
* b) NDK compatible interface no longer supports TR31 installation mode; \n
* \n
* -# Key KCV length adjustment: \n
* a) The DES algorithm key KCV is adjusted to 3 bytes; \n
* b) AES algorithm key KCV is calculated using cmac algorithm, and the length is adjusted to 5 bytes; \n
* \n
* -# TR31 key installation restrictions: \n
* a) The security level of the KEY_USE_TR31_KEK key is higher than that of the ordinary KEK key; \n
* b) The TR31 key package can only be installed using the KEY_USE_TR31_KEK master key. At the same time, KEY_USE_TR31_KEK can only be used for the decryption installation of the TR31 key package, not for normal decryption installation \n
* c) The original NDK_SecLoadKey interface no longer supports the TR31 key installation method. If you need to use the TR31 key to install, you need to use the NAPI_SecGenerateKey interface instead, and the master key type must be KEY_USE_TR31_KEK. \n
* d) KEY_USE_TR31_KEK can only be imported through three methods: random generation/asymmetric encryption/other TR31_KEK encryption \n
* \n
* -# Adjustable key ID range: \n
* a) The allowable key ID installation range for ordinary applications is 1~250, and the 250~255 key is reserved for the system and only allows installation with root privileges; \n
* b) The TLK type key of the original NDK interface is converted and installed in the location where ID 251 is KEY_USE_TR31_KEK;  \n
*/

/**
 *@brief  Key Type.
*/
typedef enum {
    KEY_TYPE_DES,
    KEY_TYPE_AES,
#ifndef OVERSEA
    KEY_TYPE_SM4,
#endif
	KEY_TYPE_MAX,
    KEY_TYPE_ASYM_RSA = 0x20,
    KEY_TYPE_ASYM_ECC,
    KEY_TYPE_ASYM_SM2,
	KEY_TYPE_ASYM_MAX,
    EM_SEC_CRYPTO_KEY_TYPE_MAX=65536
} EM_SEC_CRYPTO_KEY_TYPE;


/**
  *@brief  block cipher mode.
  *@details All block cipher modes are defined here. But only ECB & CBC modes are supported at present.
*/
typedef enum {
    SEC_CIPHER_MODE_ECB,
    SEC_CIPHER_MODE_CBC,
    SEC_CIPHER_MODE_CFB,
    SEC_CIPHER_MODE_OFB,
    SEC_CIPHER_MODE_CTR,
    SEC_CIPHER_MODE_GCM,
    SEC_CIPHER_MODE_STREAM,
    SEC_CIPHER_MODE_CCM,
    EM_SEC_CIPHER_MODE_MAX = 65536
} EM_SEC_CIPHER_MODE;

/**
  *@brief  Full cipher identifier for data encryption.
  *@details Including the cipher type and also the block cipher operation mode. Only ECB & CBC modes are supported at present.
*/
typedef enum {
    /* TDES Algorithm */
    SEC_CIPHER_DES_ECB,      /**< ECB mode without specifying the key length, firmware will use appropriate algorithm according to the key length */
    SEC_CIPHER_DES_CBC,      /**< CBC mode without specifying the key length, firmware will use appropriate algorithm according to the key length */
    SEC_CIPHER_DES_CFB,
    SEC_CIPHER_DES_OFB,
    SEC_CIPHER_AES_ECB,        /**< ECB mode without specifying the key length, firmware will use appropriate algorithm according to the key length */
    SEC_CIPHER_AES_CBC,        /**< CBC mode without specifying the key length, firmware will use appropriate algorithm according to the key length */
    SEC_CIPHER_AES_CFB,
    SEC_CIPHER_AES_OFB,
    /* DUKPT TDES Algorithm */
    SEC_CIPHER_DUKPT_ECB_RESP,     /**< 3DES encryption in ECB mode, with DUKPT key variant for Data Encryption: response (Input Message) */
    SEC_CIPHER_DUKPT_ECB_BOTH,    /**< 3DES encryption in ECB mode, with DUKPT key variant for Data Encryption: request or both ways (Output Message) */
    SEC_CIPHER_DUKPT_CBC_RESP,     /**< 3DES encryption in CBC mode, with DUKPT key variant for Data Encryption: response (Input Message) */
    SEC_CIPHER_DUKPT_CBC_BOTH,    /**< 3DES encryption in CBC mode, with DUKPT key variant for Data Encryption: request or both ways (Output Message) */
    SEC_CIPHER_DUKPT_CFB_RESP,
    SEC_CIPHER_DUKPT_CFB_BOTH,
    SEC_CIPHER_DUKPT_OFB_RESP,
    SEC_CIPHER_DUKPT_OFB_BOTH,
    /* DUKPT AES Algorithm: */
    SEC_CIPHER_AES_DUKPT_ECB,    /**< AES encryption in ECB mode, with AES DUKPT key variant for Data Encryption */  
    SEC_CIPHER_AES_DUKPT_CBC,     /**< AES  encryption in CBC mode, with AES DUKPT key variant for Data Encryption*/  
#ifndef OVERSEA
    /* SM4 Algorithm*/
	SEC_CIPHER_SM4_ECB = 24,        /**< ECB mode without specifying the key length, firmware will use appropriate algorithm according to the key length */
    SEC_CIPHER_SM4_CBC,        /**< CBC mode without specifying the key length, firmware will use appropriate algorithm according to the key length */
//    SEC_CIPHER_SM4_CFB,
//    SEC_CIPHER_SM4_OFB,
#endif
    SEC_CIPHER_TYPE_MAX,
	SEC_CIPHER_KEYLEN_8= (1<<8),			/**<Encrypted with 8-byte  key*/	
	SEC_CIPHER_KEYLEN_16=(1<<9),		   /**<Encrypted with 16-byte  key*/	
	SEC_CIPHER_KEYLEN_24=(1<<10),			/**<Encrypted with 24-byte	key*/	
	SEC_CIPHER_DERIVE_PIN=(1<<11),           /**< DUKPT key derive a key for PIN*/    
	SEC_CIPHER_DERIVE_MAC= (1<<12),            /**<DUKPT key derive a key for MAC*/    
	SEC_CIPHER_DERIVE_MAC_RESP=(1<<13),           /**<DUKPT key derive a key for MAC(response)*/
    EM_SEC_CIPHER_TYPE_MAX = 65536
} EM_SEC_CIPHER_TYPE;

/**
 *@brief enum2 definition
*/	
typedef enum {
    SEC_PADDING_NONE = 0,      /**< Never pad (full blocks only) */
    SEC_PADDING_PKCS7,         /**< PKCS7 padding, same as PKCS5. Always padded even though the data is multiple of block size. The value of each added byte is the number of bytes that are added, e.g. "DD DD DD DD 04 04 04 04" */
    SEC_PADDING_ONE_AND_ZEROS, /**< ISO/IEC 7816-4 padding. First byte is a mandatory byte valued '80' then rest bytes are set to zero, e.g. "DD DD DD DD 80 00 00 00" */
    SEC_PADDING_ZEROS_AND_LEN, /**< ANSI X.923 padding. Zeros are padded and the last byte defines the padding boundaries or the number of padded bytes, e.g. "DD DD DD DD 00 00 00 04" */
    SEC_PADDING_ZEROS,         /**< zero padding (not reversible!), e.g. "DD DD DD DD 00 00 00 00" */
    EM_SEC_PADDING_MAX = 65536
} EM_SEC_PADDING;

/**
 *@brief  MAC (Message Authentication Code) Algorithms.
*/
typedef enum {
    /* TDES MAC */
    SEC_MAC_TDES_LAST, 	         /**< MAC Digital signature with TDES on last block only. 9606 */
    SEC_MAC_TDES_X99, 	         /**< MAC Digital signature with TDES on all blocks. X99 */
    SEC_MAC_TDES_X919,           /**< MAC Digital signature with sigle DES on each block, but full TDES on last block. */
    SEC_MAC_TDES_UNIONPAY_ECB,   /**< MAC Digital signature with TDES on the first 4 bytes of last block. In accordance to the UnionPay requirement */
    /* DUKPT MAC */
    SEC_MAC_DUKPT_LAST,          /**< MAC using a DUKPT key with TDES on last block only. 9606 */
    SEC_MAC_DUKPT_X99,           /**< MAC using a DUKPT key with TDES on all blocks.  X99 */
    SEC_MAC_DUKPT_X919,          /**< MAC Digital signature with sigle DES on each block, but full TDES on last block. */
    SEC_MAC_DUKPT_UNIONPAY_ECB,  /**< MAC Digital signature with TDES on the first 4 bytes of last block. In accordance to the UnionPay requirement */
    /* DUKPT RESPONSE MAC */
    SEC_MAC_DUKPT_RESP_LAST,     /**< MAC using a DUKPT key (response) with TDES on last block only. 9606 */
    SEC_MAC_DUKPT_RESP_X99,      /**< MAC using a DUKPT key (response) with TDES on all blocks.  X99 */
    SEC_MAC_DUKPT_RESP_X919,     /**< MAC using a DUKPT key (response) with sigle DES on each block, but full TDES on last block. */
    SEC_MAC_DUKPT_RESP_UNIONPAY_ECB, /**< MAC using a DUKPT key (response) with TDES on the first 4 bytes of last block. In accordance to the UnionPay requirement */
    /* AES MAC */
    SEC_MAC_AES_LAST,            /**< MAC Digital signature with AES on last block only. 9606 */
    SEC_MAC_AES_X99,             /**< MAC Digital signature with AES on all blocks. X99 */
    /* AES DUKPT MAC */
    SEC_MAC_AES_DUKPT_LAST,      /**< MAC using a DUKPT key with AES on last block only. 9606 */
    SEC_MAC_AES_DUKPT_X99,       /**< MAC using a DUKPT key with AES on all blocks. X99 */
    SEC_MAC_AES_DUKPT_X919, /**< MAC using a DUKPT key  with sigle DES on each block, but full TDES on last block. */ 
    SEC_MAC_AES_DUKPT_UNIONPAY_ECB,  /**< MAC using a DUKPT key  with TDES on the first 4 bytes of last block. In accordance to the UnionPay requirement */  
#ifndef OVERSEA
	/* SM4 MAC*/
	SEC_MAC_SM4_LAST,            /**< MAC Digital signature with SM4 on last block only. 9606 */
    SEC_MAC_SM4_X99,             /**< MAC Digital signature with SM4 on all blocks. X99 */
#endif
    SEC_MAC_SM4_UNIONPAY_ECB,
	SEC_MAC_MAX,
    EM_SEC_MAC_TYPE_MAX = 65536
} EM_SEC_MAC_TYPE;

/**
    @brief Key Injection Methods
*/
typedef enum {
    SEC_KIM_CLEAR = 0,     /**< Key is created from KeyData wich contains crear key data */
    SEC_KIM_CIPHER,        /**< Key is derived from ciphertext which is encrypted by the specified KEK. */
    SEC_KIM_TR31,          /**< Key is generated under TR-31 rules.  */
    SEC_KIM_RANDOM,        /**< A random key is generated and stored in the specified index. */
    SEC_KIM_RANDOM_OUT,    /**< Session key is generated randomly, then encrypted under the corresponding master key. Q: Output to APP? */
	SEC_KIM_DUKPT_DERIVE,  /**< Derive a new DUKPT key, the KSN will increase after derivation */
    SEC_KIM_DIVERSIFY_X,   /**< Customized key derivation method, e.g. In accordance to the Spanish requirements.? */
    SEC_KIM_GISKE,
    SEC_KIM_RANDOM_OUT_TR31,
    SEC_KIM_CIPHER_VTB,
	SEC_KIM_AES_DUKPT_UPDATE_IK, 
	SEC_KIM_ECDHE_GEN_SK,   
    SEC_KIM_MAX,            /**< Invalid keygen method. */
    EM_SEC_KEYIN_METHOD_MAX = 65536
} EM_SEC_KEYIN_METHOD;

/**
 *@brief Key Usages
*/
typedef enum {
    /* Master key, KEK */
    KEY_USE_KEK,            /**<Master key for all key, same as NDK TMK*/
    KEY_USE_PIN_KEK,        /**<Master key ONLY for PIN key*/
    KEY_USE_MAC_KEK,        /**<Master key ONLY for MAC generation key*/
    KEY_USE_DATA_KEK,       /**<Master key ONLY for data encryption & decryption key*/
    KEY_USE_DATA_ENC_KEK,   /**<Master key ONLY for data encryption key*/
    KEY_USE_TR31_KEK,       /**<Master key ONLY for TR31 key block*/
    /* Session / Working key */
    KEY_USE_PIN,
    KEY_USE_MAC,
    KEY_USE_DATA,
    KEY_USE_DATA_ENC_ONLY,
    /* DUKPT Initial Key */
    KEY_USE_DUKPT = 0x10, /**<DUKPT Initial Key*/
    /* Asym Key*/
    KEY_USE_ASYM_AUTH = 0x20,          /**<Asym key used for sign & verify */
    KEY_USE_ASYM_DATA,                 /**<Asym key used for data encryption & decryption */
    KEY_USE_ASYM_ANY,                  /**<Asym key used for AUTH & DATA, except KEY_DISTRIBUTION*/
    KEY_USE_ASYM_KEY_DISTRIBUTION,     /**<Asym Key ONLY for symmetric key distribution */ 
    EM_SEC_KEY_USAGE_MAX = 65536
} EM_SEC_KEY_USAGE;

/**
    @brief  The Virtual PIN Pad session types supported by security service
    @details This enum is used by the structure SEC_VPP_DATA to indicate the VPP session type.
    For Online VPP sessions the PIN Block is returned encrypted with the specified key. 
    For Offline VPP sessions the status bytes SW1 and SW2 indicate the success or failure of the operation. 
*/
typedef enum {
    SEC_VPP_DUKPT = 1,               /**< Online DUKPT session, does not iterate DUKPT key */
    SEC_VPP_MASTER_SESSION,          /**< Online Master Session */
    SEC_VPP_EMV_OFFLINE_CLEARPIN,    /**< EMV off-line with clear PIN */
    SEC_VPP_EMV_OFFLINE_ENCPIN,      /**< EMV off-line with encrypted password */
    SEC_VPP_EMV_PIN_VERIFY_CLEARPIN, /**< EMV off-line with clear verification using a PIN block. */
    SEC_VPP_EMV_PIN_VERIFY_ENCPIN,   /**< EMV off-line with encrypted password using a PIN block. */
    SEC_VPP_MS_DIVERSIFYKEY,         /**< On-line Master Session, using a diversified key generated in accordance to the Spanish requirements. */
    SEC_VPP_INVALID_SESSION,         /**< For parameter testing purposes */
	SEC_VPP_ICC_PRESENT_CHECK_FLAG = 1<<8,	/**< Detect whether ICC card is removed when this flag is set */
    EM_SEC_VPP_SESSION_TYPE_MAX = 65536
} EM_SEC_VPP_SESSION_TYPE;

/**
 *@brief undefined 3
*/
typedef enum{
	NAPI_SEC_KCV_NONE=0,		/**<No check*/
	NAPI_SEC_KCV_ZERO,          /**<Run DES/TDES encryption algorithm with 0x00 of 8 bytes, and first 4 bytes of ciphertext is obtained, which is KCV*/
	NAPI_SEC_KCV_VAL,		    /**<First, run odd parity with plaintext of key, then run DES/TDES algorithem with "\x12\x34x56\x78\x90\x12\x34\x56", first 4 bytes of ciphertext is obtained, which is KCV*/
	NAPI_SEC_KCV_DATA,	        /**<Send in a string of data KcvData, run specific mode of MAC algorithm to [aucDstKeyValue(ciphertext) + KcvData] with source key pair, MAC of 8 bytes is obtained, which is KCV*/
	NAPI_SEC_KCV_CMAC,          /**<kcv is calclated by MACing an all-zero block using the CMAC algorithm, and first 5 bytes of ciphertext is obtained, which is KCV*/
	NAPI_SEC_KCV_MAX,
    EM_NAPI_SEC_KCV_MAX = 65536
}EM_NAPI_SEC_KCV;

/**
 *@brief undefined 4
 */
typedef enum {
	NAPI_SEC_PIN_ISO9564_0=3,    
	NAPI_SEC_PIN_ISO9564_1=4,  
	NAPI_SEC_PIN_ISO9564_2=5,  
	NAPI_SEC_PIN_ISO9564_3=6,
	NAPI_SEC_PIN_SM4_1,		
	NAPI_SEC_PIN_SM4_2,		
	NAPI_SEC_PIN_SM4_3,		
	NAPI_SEC_PIN_SM4_4,		
	NAPI_SEC_PIN_SM4_5,		   
    NAPI_SEC_PIN_ISO9564_4 = 12,
    NAPI_EM_SEC_PIN_MAX = 65536
}NAPI_EM_SEC_PIN;

/**
 *@brief undefined 5
 */
typedef enum{
    NAPI_SEC_VPP_KEY_PIN,					
    NAPI_SEC_VPP_KEY_BACKSPACE,			
    NAPI_SEC_VPP_KEY_CLEAR,			
    NAPI_SEC_VPP_KEY_ENTER,				
    NAPI_SEC_VPP_KEY_ESC,
    NAPI_SEC_VPP_KEY_ADA_ON,					/** <ADA模式PIN输入开�?*/
    NAPI_SEC_VPP_KEY_ADA_OFF,				/** <ADA模式PIN输入关闭 */    				
    NAPI_SEC_VPP_KEY_NULL,
    NAPI_EM_SEC_VPP_KEY_MAX = 65536			
}NAPI_EM_SEC_VPP_KEY;

#define MAX_RSA_MODULUS_BITS 2048
#define MAX_RSA_MODULUS_LENGTH  ((MAX_RSA_MODULUS_BITS + 7) / 8)
#define MAX_RSA_PRIME_BITS      ((MAX_RSA_MODULUS_BITS + 1) / 2)
#define MAX_RSA_PRIME_LENGTH    ((MAX_RSA_PRIME_BITS + 7) / 8)
#define MAX_RSA_MODULUS_LEN		512	

/**
 *@brief Values for the requested key information
*/
typedef enum {
    SEC_KEY_INFO_KEYLEN,
    SEC_KEY_INFO_KCV,
    SEC_KEY_INFO_KSN,
    SEC_KEY_INFO_CERT,
	SEC_KEY_INFO_PKEY_CERTLEN,
    SEC_KEY_INFO_PKEY_PUBKEY,
	SEC_KEY_INFO_KCV_CMAC,
    SEC_KEY_INFO_RKI_CA_CERT,   
    SEC_KEY_INFO_RKI_CA_PUBKEY, 
    SEC_KEY_INFO_MAX,
    EM_SEC_KEY_INFO_ID_MAX = 65536
} EM_SEC_KEY_INFO_ID;

typedef enum{
    DUKPT_DERIVATE_NONE,        /**NONE */
    DUKPT_DERIVATE_KEK,         /**< key Encryption Key */
    DUKPT_DERIVATE_PIN,         /**< PIN Encryption */
    DUKPT_DERIVATE_MAC_GEN,         /**< Message Authentication,generation */
    DUKPT_DERIVATE_MAC_VERIFY,  /**< Message Authentication,verification */
    DUKPT_DERIVATE_MAC_BOTH,    /**< Message Authentication, both ways(When the key type is DES, it is consistent with DUKPT_DERIVATE_MAC_GEN) */
    DUKPT_DERIVATE_DATA_ENC,    /**< Data Encryption, encrypt */
    DUKPT_DERIVATE_DATA_DEC,    /**< Data Encryption, decrypt */
    DUKPT_DERIVATE_DATA_BOTH,    /**< Data Encryption, both ways (When the key type is DES, it is consistent with DUKPT_DERIVATE_DATA_ENC)*/
    DUKPT_DERIVATE_DERIVATEKEY, /**< Key Derivation */
    DUKPT_DERIVATE_DERIVATEKEY_INITIAL, /**< Initial Key Derivation */
} EM_SEC_DUKPT_DERIVATE_USAGE;


typedef struct {  
    EM_SEC_CRYPTO_KEY_TYPE KeyType; //指定派生密钥的算�? 
    EM_SEC_DUKPT_DERIVATE_USAGE DerivateUsage; //指定派生密钥
    int nKeyLen;//指定派生密钥的长�? 
}ST_SEC_DUKPT_DERIVATE_DATA;


#define KEY_USAGE_DEN 	0X4430	//‘D0�?0x44, 0x30	Data Encryption 
#define KEY_USAGE_IV 	0X4930  //‘I0�?0x49, 0x30	IV 
#define KEY_USAGE_CTL 	0X5430  //‘T0�?0x54, 0x30	‘T�?for conTrol vector
#define KEY_USAGE_KEW 	0X4B30  //‘K0�?0x4B, 0x30	Key Encryption or wrapping
#define KEY_USAGE_GMAC 	0X4730  //‘G0�?0x47, 0x30	MAC Generation
#define KEY_USAGE_VMAC 	0X4D30  //‘M0�?0x4D, 0x30	MAC Verification
//#define KEY_USAGE_PIN 	0X5030  //‘P0�?0x50, 0x30	Pin Encryption
#define KEY_USAGE_KPV 	0X5630  //‘V0�?0x56, 0x30	PIN verification, KPV
#define KEY_USAGE_CVK 	0X4330  //‘C0�?0x43, 0x30	CVK Card Verification Key 
#define KEY_USAGE_KC 	0X6330  //‘c0�?0x63, 0x30	Key component
//#define KEY_USAGE_BDK 	0X4230  //‘B0�?0x42, 0x30	BDK Base Derivation Key
#define KEY_USAGE_MAC1 	0X3030  //�?0�?0x30, 0x30	ISO 9797-1 MAC Algorithm 1 �?56 bits
#define KEY_USAGE_MAC2 	0X3130  //�?0�?0x31, 0x30	ISO 9797-1 MAC Algorithm 1 �?112 bits
#define KEY_USAGE_MAC3 	0X3230  //�?0�?0x32, 0x30	ISO 9797-1 MAC Algorithm 2 �?112 bits
#define KEY_USAGE_MAC4 	0X3330  //�?0�?0x33, 0x30	ISO 9797-1 MAC Algorithm 3 �?112 bits
#define KEY_USAGE_MAC5 	0X3430  //�?0�?0x34, 0x30	ISO 9797-1 MAC Algorithm 4 �?112 bits
#define KEY_USAGE_MAC6 	0X3530  //�?0�?0x35, 0x30	ISO 9797-1 MAC Algorithm 5 �?56 bits
#define KEY_USAGE_MAC7 	0X3630  //�?0�?0x36, 0x30	ISO 9797-1 MAC Algorithm 5 �?112 bits

#define KEY_USAGE(usage)        (((usage)[0] << 8) | (usage)[1])
#define KEY_USAGE_BDK           0x4230          /* B0:KT_BDK BASE Derivation Key */
#define KEY_USAGE_PRI           0x4430          /* D0:KT_PRI  */
#define KEY_USAGE_MST           0x4B30          /* K0:KT_MST */
#define KEY_USAGE_MAC           0x4D30          /* M0:KT_MAC */
#define KEY_USAGE_PIN           0x5030          /* P0:KT_PIN */
#define KEY_USAGE_MAG           0x4330          /* C0:KT_MAG */
#define KEY_USAGE_IDK           0x4231          /* B1:KT_IDK DUKPT INITIAL Key */

/**
 *@brief  Session key structure
 *@details Used by functions that suport an encrypted     session key.
 *@note The IV for CBC mode is all zeros which means no IV.
*/
typedef struct {
    uchar IV[16];
    int KeyLen;            /**< Length of decrypted plaintext session key. Since block size of AES is multiple of 128bits, while cihpertext of AES192 and AES256 is the same 256bits  */
    uchar KeyData[32];   /**< Encrypted session key, multiple of cipher block size. */
    unsigned mode;         /**< 0: CBC, 1: ECB encrypted */
} ST_SEC_SESSION_KEY;

/**
 *@brief Sysmetric key Injection Data
*/
typedef struct {
    uchar ucKEKIdx;    /**<KEK Index, 1~255 */
	EM_SEC_CRYPTO_KEY_TYPE KEKType;   /**<key type of the master key: TDES or AES*/
    EM_SEC_KEY_USAGE KEKUsage;    /**<Specify the KEK Usage*/
	uchar ucKeyIdx;    /**<Index for the injected key, 1~255 */
	EM_SEC_CRYPTO_KEY_TYPE KeyType;   /**<key type of the key to be injected. e.g. TDES or AES*/
    EM_SEC_KEY_USAGE KeyUsage;   /**<Usage of the key to be injected (\ref EM_SEC_KEY_USAGE "EM_SEC_KEY_USAGE"), for SEC_KGM_TR31 method, it is ignored and will be parsed from TR31 header*/
    int CipherMode;   /**<cipher mode for the key injection, only ECB or CBC, ECB mode does not support padding*/
    int PadingMode;   /**<Padding mode for the cipher, e.g. ECB, CBC, PadingMode is used only when key injection methods is "SEC_KIM_CIPHER" */
    int nKeyLen;       /**<Length of the injecting key. Most of the time it is exactly the length of input "pKeyData". But there are some exceptions when using zero paddings of cipher. see comments for "pKeyData"*/
	int nKeyDataLen;   /**<Length of pKeyData*/
	uchar *pKeyData;   /**<Pointer to the key data.
                        When the key injection methods is "SEC_KIM_CIPHER":
                        (a) if PadingMode is "SEC_PADDING_ZEROS", then above "nKeyLen" must be the REAL length of the key. while data in "pKeyData" is multiple of cipher block size and it may be larger then "nKeyLen".
                        E.g. When installing a 24 bytes AES key in ciphertext with zero padding, then "nKeyLen" must be 24, but the firmware will read 32 bytes (multiple of AES block size 16) of data from "pKeyData".
                        (b) if PadingMode is other values than "SEC_PADDING_ZEROS", then "nKeyLen" will be exactly the length of "pKeyData", and the REAL length of the key will be determined by the firmware after removing the pad bytes.
                        When the key injection method is "SEC_KIM_RANDOM_OUT", then "pKeyData" will be used as output pointer for the random generated session key. The output session key is encrypted by the specified KEK */
    uchar *psIV;       /**<Pointer to the Initial Vector*/
    int nKsnLen;       /**<Length of DUKPT KSN*/
    uchar *psKsn;      /**<DUKPT KSN for key type "SEC_KEY_TYPE_DUKPT" */
    int nADSize;       /**<Length of additional data*/
    uchar *pAD;        /**<Pointer to the additional data, When the key injection method is SEC_KIM_RANDOM_OUT, then "pAD" will be used as output pointer for the length of "pKeyData".*/
} ST_SEC_KEYIN_DATA;

/**
 *@brief Key check infromation
*/
typedef struct {
    int nCheckMode;      /**<Check mode (\ref ST_SEC_KCV_INFO "ST_SEC_KCV_INFO")*/
    int nLen;            /**<Data length to be checked*/
    uchar sCheckBuf[8];  /**<KCV value buffer, always 3 bytes, for future extension*/
} ST_SEC_KCV_DATA;



/**
 *@brief RSA Key Struct
*/
typedef struct {
    uint usBits;                    		
    uchar sModulus[MAX_RSA_MODULUS_LEN];  	
    uchar sExponent[MAX_RSA_MODULUS_LEN]; 	
}ST_NAPI_RSA_KEY;

/**
 *@brief Verify Pin Add
*/
 typedef struct {
    EM_SEC_KEY_USAGE KeyUsage;
	uint             unPinBlockLen;
	uchar            psPinBlock[32];
	uint             unTSKLen;
	uchar            psTSK[32];
} ST_SEC_VERIFY_PIN_AD;

/**
 *@brief The structure for necessary inputs of Data encryption
*/
typedef struct {
    uchar ucKeyID;                    /**< DATA encryption Key index, 1~250 */
    EM_SEC_CIPHER_TYPE CipherType;    /**< Full cipher identifier (e.g. SEC_CIPHER_AES_CBC) */
    EM_SEC_KEY_USAGE KeyUsage;        /**< Usage of the encryption key */
    EM_SEC_PADDING PaddingMode;       /**< Padding mode for the cipher (\ref EM_SEC_PADDING "EM_SEC_PADDING") */
    uint unIVSize;                    /**< IV size, 8 bytes for TDES, 16 bytes for AES */
    uchar *psIV;                      /**< Initial Vector */
    uint unDataInLen;                 /**< Input data length */
    uchar *psDataIn;                  /**< Pointer to the input data */
    uint unADSize;                    /**< Size of additional data, could be the size of ST_SEC_SESSION_KEY */
    uchar *pAD;                       /**< Additional data, Pointer to a ST_SEC_SESSION_KEY structure when a session key is used to encrypt data. */
                                      /**< This means that the key indicated by KeyID is a KEK */
}ST_SEC_ENCRYPTION_DATA;

typedef struct {
	EM_SEC_KEYIN_METHOD method;
	int (*genkey_checkparam)(ST_SEC_KEYIN_DATA *pstKGData, int* pIvlen);
} keygen_paramcheck_t;

/**
 *@brief The structure for GISKE key block
*/
typedef struct {
	uint8_t version;        /* Key Block Version Number --- 'A'(0x41) */
	uint8_t length[4];      /* Key Block Length, including Header + DATA + MAC */
	uint8_t usage[2];       /* Key Usage */
	uint8_t algorithm;      /* The approved algorithm to be used by the protected key */
	uint8_t mode;           /* the operation the protected key can perform */
	uint8_t setId[16];      /* Key Set Identifier */
	uint8_t ver_num[2];     /* Key Version Number */
	uint8_t	exportable;		/* Should be 'N'(0x4e) */
	uint8_t	key_length[4];	/* Key Length */
} napi_giske_kbh_t;

/**
 *@brief GISKE Key information
*/
typedef struct {
        uchar 	ucScrKeyType;	       /**<Type of source key that diffused this key (\ref EM_SEC_CRYPTO_KEY_TYPE "EM_SEC_CRYPTO_KEY_TYPE"), it shall not be lower than key level ucDstKeyType is at*/
        uchar 	ucScrKeyIdx;           /**<Index of source key that diffused this key, typically index starts from 1, if this variable equals 0, then this key is written in plaintext*/
        uchar 	ucDstKeyIdx;	       /**<Index of destination key, range [1 - 255], DO NOT USE 0*/
        int 	nDstKeyLen;            /**<Length of destination key:96, 120*/
        char 	sDstKeyValue[120 + 1]; /** GISKE block (ASCII)*/
} ST_NAPI_SEC_GISKE_KEY_INFO;

/**
 *@brief The structure for the number of key contained in each ID
*/
typedef struct {
    uchar ucKeyID;                    /**<Key index, 1~255 */
    int nNum;                         /**<The number of key */
} ST_SEC_KEYNUM_INFO;

/**
 *@brief The structure for symmetry key information contained in each ID
*/
typedef struct {
    uchar ucKeyID;                      /**<Key index, 1~255 */
    EM_SEC_CRYPTO_KEY_TYPE KeyType;     /**<key type of the key. e.g. TDES or AES*/
    EM_SEC_KEY_USAGE KeyUsage;          /**<Usage of the key. (\ref EM_SEC_KEY_USAGE "EM_SEC_KEY_USAGE")*/
    int   	nKcvLen;	                /**<The length of KCV value,according to different key type*/
    uchar 	sKcvBuf[8]; 	            /**<KCV value buffer*/
} ST_SEC_SYMM_KEYID_INFO;


/*
 * Touch screen keypad definition
 */
typedef struct
{
	uint16_t x;    /* the point's X coordinate  */
	uint16_t y;    /* the point's Y coordinate  */
}vpp_point;

typedef struct
{
	vpp_point l_top;       /* left top point */
	vpp_point r_bottom;    /* right bottom point */
}vpp_button;

typedef struct
{
	uint32_t key;          /* Value of key-press, e. g. ,'1', '2', ENTER. */
	vpp_button btn;        /* Button area */
}vpp_key;

#define NUM_KEY_CNT     10        /* 10 numeric keys: '0'-'9' */
#define FUNC_KEY_CNT    3         /* 3 function keys: ESC / BASP / ENTER */
#define TOTAL_KEY_CNT   (NUM_KEY_CNT + FUNC_KEY_CNT)

/** @addtogroup 键盘
* @{
*/
/**
 *@brief 功能键值定�?*/
#define  K_F1       0x01
#define  K_F2       0x02
#define  K_F3       0x03
#define  K_F4       0x04
#define  K_F5       0x05
#define  K_F6       0x06
#define  K_F7       0x07
#define  K_F8       0x08
#define  K_F9       0x09
#define  K_BASP     0x0a    /**<退格键*/
#define  K_ENTER    0x0D    /**<确认键*/
#define  K_ESC      0x1B    /**<取消键*/
#define  K_ZMK      0x1C    /**<字母键*/
#define  K_DOT      0x2E    /**<小数键*/

#define	 K_QUIT		0x9B	/**<退出键*/
#define	 K_CLEAR	0x9C	/**<清空输入键*/

/**
 *@brief 数字键值定�?*/
#define K_ZERO      0x30    /**<数字0键*/
#define K_ONE       0x31    /**<数字1键*/
#define K_TWO       0x32    /**<数字2键*/
#define K_THREE     0x33    /**<数字3键*/
#define K_FOUR      0x34    /**<数字4键*/
#define K_FIVE      0x35    /**<数字5键*/
#define K_SIX       0x36    /**<数字6键*/
#define K_SEVEN     0x37    /**<数字7键*/
#define K_EIGHT     0x38    /**<数字8键*/
#define K_NINE      0x39    /**<数字9键*//



/**
*@brief Key Injection
 *@details Generic key injection for all key types, with different algorithm...
 *@param[in] Method             Key generation method,ref EM_SEC_KEYIN_METHOD "EM_SEC_KEYIN_METHOD"
 *@param[in] pstKGData          All needed data for key injection, ref ST_SEC_KEYIN_DATA "ST_SEC_KEYIN_DATA"
 *@param[in] pstKcvData         Key check value (KCV) for the injected key, Optional
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE". 
*/


NAPI_ERR_CODE NAPI_SecGenerateKey( EM_SEC_KEYIN_METHOD Method, ST_SEC_KEYIN_DATA *pstKGData, ST_SEC_KCV_DATA *pstKcvData);

/**
 @brief Set the Lenth of passward during PIN entry.
 *@param[in] key    the Lenth of passward: 
                    like:0,4,6
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE". 
*/

NAPI_ERR_CODE NAPI_SecVPPSetExpPinLenIn(char *pszExpPinLenIn);

/**
 *@brief        Initialises the Virtual (internal) PIN pad. Start the PIN entry mode.
 *@param[in] SessionType    For SessionType "SEC_VPP_MASTER_SESSION", pAD will be an encrypted session key, see ST_SEC_SESSION_KEY.
 *@param[in] KeyType        Key type
 *@param[in] ucKeyIdx       PIN Key index, 1~250.
 *@param[in] pPAN           Primary Account Number, NULL terminated character string.
 *@param[in] PINBlockFmt    PIN BLOCK per ISO9564, format 0~4.
 *@param[in] unTimeOut      Timeout value (seconds), 5-200.
 *@param[in] pRSAKey        RSA public key for the offline ciphertext PIN encryption.
 *@param[in] pAD            Additional data, for Master Session this is packed encrypted session key, given by the structure ST_SEC_SESSION_KEY.
 *@param[in] unADSize       Size of Additional Data.
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE". 
*/

NAPI_ERR_CODE NAPI_SecVPPInit( EM_SEC_VPP_SESSION_TYPE SessionType,
                EM_SEC_CRYPTO_KEY_TYPE KeyType,
                uchar ucKeyIdx,
                char *pPAN,
                uint PINBlockFmt,
                uint unTimeOut,
                ST_NAPI_RSA_KEY *pRSAKey,
                void *pAD,
                uint unADSize );

/**
 *@brief        Process and get PIN entry event
 *@param[out]   nEvent        PIN entry event, see EM_SEC_VPP_KEY 
 *@param[out]   psPinBlock    Ciphertext pinblock if the user finish PIN entry and press Enter key.
                              During the PIN entry, the first byte of psPinBlock[0] indictaes length of current PIN digits.
 *@param[out]   pnOutPinLen   Pointer to size of output pinblock.
 *@param[out]   psKsn         Pointer to the output KSN for current PIN encryption if the "SessionType" is DUKPT.
 *@param[out]   pnOutKsnLen   Pointer to size of output KSN if the "SessionType" is DUKPT.
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE". 
*/


NAPI_ERR_CODE NAPI_SecVPPGetEvent(int *nEvent, uchar *psPinBlock, int *pnOutPinLen, uchar *psKsn, int *pnOutKsnLen);

/**
 @brief Simulated key code to externally influence PIN entry procedure.
 *@param[in] key    The simulated key may be set externally during PIN entry: 
                    NAPI_SEC_VPP_KEY_ESC - simulates pressing CANCEL key
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE". 
*/

NAPI_ERR_CODE NAPI_SecVPPSetEvent(uint key);

/**
 *@brief         Encrypt Data using the algorithm and Key specified.
 *@param[in] pstDataIn       Pointer to the data for encryption (\ref ST_SEC_ENCRYPTION_DATA "ST_SEC_ENCRYPTION_DATA")
 *@param[out] psDataOut      Pointer to output data
 *@param[out] pnOutLen       Pointer to size of output data
 *@param[out] psKsnOut       Pointer to output KSN if it is a DUKPT encryption
 *@param[out] pnOutKsnLen    Pointer to the size of output KSN if it is a DUKPT encryption.
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE". 
*/


NAPI_ERR_CODE NAPI_SecEncryption(ST_SEC_ENCRYPTION_DATA *pstDataIn, uchar *psDataOut, int *pnOutLen, uchar *psKsnOut, int *pnOutKsnLen);

/**
 *@brief         Decrypt Data using the algorithm and Key specified.
 *@param[in] pstDataIn       Pointer to the data for decryption (\ref ST_SEC_ENCRYPTION_DATA "ST_SEC_ENCRYPTION_DATA")
 *@param[out] psDataOut      Pointer to output data
 *@param[out] pnOutLen       Pointer to size of output data
 *@param[out] psKsnOut       Pointer to output KSN if it is a DUKPT decryption.
 *@param[out] pnOutKsnLen    Pointer to the size of output KSN if it is a DUKPT decryption.
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE". 
*/


NAPI_ERR_CODE NAPI_SecDecryption(ST_SEC_ENCRYPTION_DATA *pstDataIn, uchar *psDataOut, int *pnOutLen, uchar *psKsnOut, int *pnOutKsnLen);

/**
 *@brief        Generate Message Authentication Code for a block of data.
 *@param[in] MacType         Mac type
 *@param[in] ucKeyID         Key index
 *@param[in] psIV            Initial Vector
 *@param[in] unIVSize        IV size, 8 bytes for TDES, 16 bytes for AES
 *@param[in] psDataIn        Input data
 *@param[in] nDataInLen      Input data length
 *@param[in] pAD             Additional data, Pointer to a ST_SEC_SESSION_KEY structure when a session key is used to encrypt data.
                             This means that the key indicated by KeyID is a KEK
 *@param[in] unADSize        Size of additional data, could be the size of ST_SEC_SESSION_KEY
 *@param[out] psMacOut       Pointer to output MAC value
 *@param[out] pnOutLen       Pointer to size of output data
 *@param[out] psKsnOut       Pointer to output KSN if the encryption key is DUKPT key
  *@param[out] nOutKsnLen	 Pointer to size of output KSN
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE". 
*/


NAPI_ERR_CODE NAPI_SecGenerateMAC(EM_SEC_MAC_TYPE MacType, uchar ucKeyID, uchar *psIV, int unIVSize, uchar *psDataIn, int nDataInLen, uchar *pAD, int unADSize, uchar *psMacOut, int *pnOutLen, uchar *psKsnOut, int *nOutKsnLen);

/**
 *@brief		Returns key information such as KCV, length, etc.
 *@param[in] InfoID		 	 Key info index
 *@param[in] ucKeyID		 Key index, 1~250
 *@param[in] KeyType		 Key Type
 *@param[in] KeyUsage		 Key Usage
 *@param[in] pAD			 Additional data for key information.
 *@param[in] unADSize		 Size of Additional data.

 *@param[out] psOutInfo 	  Pointer to the output buffer
 *@param[out] pnOutInfoLen	  Pointer to the output length
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

NAPI_ERR_CODE NAPI_SecGetKeyInfo(EM_SEC_KEY_INFO_ID InfoID, uchar ucKeyID, EM_SEC_CRYPTO_KEY_TYPE KeyType, EM_SEC_KEY_USAGE KeyUsage,
					  uchar *pAD, uint unADSize, uchar *psOutInfo, int *pnOutInfoLen);
/**
 *@brief		Delete key.
 *@param[in] ucKeyID		 Key index, 1~250
 *@param[in] KeyType		 Key Type
 *@param[in] KeyUsage		 Key Usage

 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

NAPI_ERR_CODE NAPI_SecDeleteKey(uchar ucKeyID, EM_SEC_CRYPTO_KEY_TYPE KeyType, EM_SEC_KEY_USAGE KeyUsage);

/**
 *@brief		Set KeyOwner.
 *@param[in] pszOwner		Key Owner,
 							1. If the application does not set the keyOwner, the shared key table "Phoenix_Share_Table" is used by default;
							2. Only the root application can configure the keyowner arbitrarily. 
							3. User permission application allows set the keyowner to as "" (self-key table) or "*" (shared key table);
 
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

NAPI_ERR_CODE NAPI_SecSetKeyOwner(char *pszOwner);

/**
 *@brief		Returns Security Module Version.

 *@param[out] pszVerInfoOut	  Version Info
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

NAPI_ERR_CODE NAPI_SecGetVer(uchar * pszVerInfoOut);

/**
 *@brief		Get Random Number.
 *@param[in] nRandLen		Length of Random Number,

 *@param[out] pvRandom	  Random Number
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

NAPI_ERR_CODE NAPI_SecGetRandom(int nRandLen , void *pvRandom);

/**
 *@brief	Get tamper status
 *@param[out]	pnStatus			Tamper status (\ref EM_SEC_TAMPER_STATUS "EM_SEC_TAMPER_STATUS")
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

NAPI_ERR_CODE NAPI_SecGetTamperStatus(uint32_t *pnStatus);

/**
 *@brief 		Get RTC time(root only)
 *@retval       t 	stTime Time
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

NAPI_ERR_CODE NAPI_SecGetRtcTime(struct tm *t);

/**
 *@brief 		Set RTC time(root only)
 *@param[in]    t 	stTime Time
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

NAPI_ERR_CODE NAPI_SecSetRtcTime(struct tm *t);


/**
 *@brief	Get time left before next reboot required from PCI 4.0
 *@details 	This function gets the time to reset as specified in PCI 4.0 (and above). PCI 4.0 specifies that device must be rebooted every 24 hours maximum. If application does not implements this rebooting sequence, firmware does perform that feature on its own side, this function retrieves how much time until it.\n
It is useful for applications in order to reboot terminal by its own when it is "idle". This is, when no critical action is running: payment transaction, batch process, updating tasks, sending reports to acquire.
 *@param[in] timeToReboot Time left before next reboot
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR "NAPI_ERR".
*/

NAPI_ERR_CODE NAPI_SecGetTimeToReboot( unsigned int *timeToReboot );

/** @} */ // end of Security

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

NAPI_ERR_CODE NAPI_SecKlaGenNonce(int nLenRandom,  uchar* psRandom);

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

NAPI_ERR_CODE NAPI_SecKlaMKLDAuth(int nLenAuthData, uchar* psAuthData, int nLenAuthCert, uchar* psAuthCert, int nLenEncCert, uchar* psEncCert, int* pnLenSsKeyCyTxt, uchar* psSsKeyCyTxt, char * keyowner);
NAPI_ERR_CODE NAPI_SecKlaMKLDAuthV2(int nLenAuthData, uchar *psAuthData, int nLenAuthCert, uchar *psAuthCert, int nLenEncCert, uchar *psEncCert, int *pnLenSsKeyCyTxt, uchar *psSsKeyCyTxt, uchar *keyowner, int *ownerlen);
NAPI_ERR_CODE NAPI_SecGetDrySR(int *pnVal);
NAPI_ERR_CODE NAPI_SecClear(void);
NAPI_ERR_CODE NAPI_SecSetCfg(uint32_t cfg);
NAPI_ERR_CODE NAPI_SecGetServKeyOwner(char *pszOwner);
/**
 *@brief	Initialize the touch screen keyboard.
 *@param[in]	num_btn			10 numeric buttons (VPP_buttons).Each button consists of two dots: "top left" and "bottom right".Each dot consists of (uint16_t x, uint16_t y).				
 *@param[in]	func_key		3 function keys (backspace, cancel, enter, VPP_key [3]).Each button is composed of a key value + button (int key, Vpp_button).
 *@param[out]	out_seq			The output of randomly arranged key values ('0'-'9') corresponds to the input parameter num_btn, which can be used for keyboard display.
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

NAPI_ERR_CODE NAPI_SecVppTpInit(uchar *num_btn, uchar *func_key, uchar *out_seq);

/**
 *@brief	Read security configuration
 *@param[out]	cfg		Configuration information
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
NAPI_ERR_CODE NAPI_SecGetCfg(uint32_t *cfg);


/**
 *@brief	Get KeyOwner
 *@param[in]  nLenOfOwnerBuffer			the nLenOfOwnerBuffer is used as the maximum length for safety.
 *@param[out]	pszOwner		          KeyOwner to be set
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
NAPI_ERR_CODE NAPI_SecGetKeyOwner(int nLenOfOwnerBuffer,char *pszOwner);

/**
 *@brief Asymmetric key information
*/
typedef struct{	      
	EM_SEC_CRYPTO_KEY_TYPE KeyType;    
    EM_SEC_KEY_USAGE KeyUsage;    
    uchar KeyIdx;      
}ST_SEC_ASYM_KEY_INFO;

typedef struct{	      
	uint unBit;    
	uchar ucRsaPubExp[5];
}ST_SEC_ASYM_ALG_INFO;

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
    ASYM_RSA_DEFAULT = 0,     /**Default use public key for encryption & private key for decryption*/
    ASYM_RSA_PUBLIC,          /**Calculate with public key*/       
    ASYM_RSA_PRIVATE,         /**Calculate with private key*/
    EM_SEC_ASYM_CRYPTO_MODE_MAX = 65536
}EM_SEC_ASYM_CRYPTO_MODE;

/**
 *@brief KDF Information
*/
typedef enum
{
	SEC_ECDHE_HKDF = 0,
	SEC_ECDHE_MAX  = 65536,
} EM_SEC_ECDHE_KDF_TYPE;

/**
 *@brief ECDHE Key Information
*/
typedef struct {
	uchar ucKeyID;                   /**<Key index, 1~255 */
	EM_SEC_CRYPTO_KEY_TYPE KeyType;  /**<key type of the key. e.g. TDES or AES*/
	EM_SEC_KEY_USAGE KeyUsage;        /**<Usage of the key. (\ref EM_SEC_KEY_USAGE "EM_SEC_KEY_USAGE")*/
	int nKeyLen;				     /**<Length of key*/
} ST_SEC_ECDHE_KEY_INFO;

/**
 *@brief ECDHE KDF Information
*/
typedef struct {
    EM_SEC_ECDHE_KDF_TYPE KDFType;    /**<Type of KDF */
    EM_SEC_MD_TYPE MdAlg;  /**<Hash algorithm mode */
    int nSaltLen;      /**<Length of salt*/     
	uchar *pSalt;    /**<An optional salt value (a non-secret random value);  if the salt is not provided, a string of all zeros of md.size length is used as the salt.>*/
    int nInfoLen;     /**<Length of info*/ 
	uchar *psInfo;    /**<An optional context and application specific information string. This can be a zero-length string.>*/
} ST_SEC_ECDHE_KDF_INFO;

/**
 *@brief Asym Key Injection Data
*/
typedef struct {
    uchar ucKEKIdx;                         /**<KEK Index, 1~250 */
    EM_SEC_CRYPTO_KEY_TYPE KEKType;         /**<key type of the master key: only KEY_TYPE_ASYM_RSA*/
    EM_SEC_KEY_USAGE KEKUsage;              /**<Specify the KEK Usage, only KEY_USE_ASYM_KEY_DISTRIBUTION*/
    uchar ucKeyIdx;                         /**<Index for the injected key, 1~250 */
    EM_SEC_CRYPTO_KEY_TYPE KeyType;         /**<key type of the key to be injected. KEY_TYPE_DES ~ KEY_TYPE_SM4*/
    EM_SEC_KEY_USAGE KeyUsage;              /**<Usage of the key to be injected , KEY_USE_KEK ~ KEY_USE_DUKPT*/
    EM_SEC_MD_TYPE MdAlg;                   /**<Hash algorithm mode, MdAlg is used only when EncodingMode is "ASYM_RSA_PKCS_V21 */
    EM_SEC_ASYM_ENCODING_MODE EncodingMode; /**< Encoding mode for the data */
    int nKeyLen;                            /**<Length of the injecting key. for SEC_KIM_RANDOM_OUT method, it is the random key length */      
    uchar *pKeyData;                        /**<Pointer to the key data.  When the key injection method is SEC_KIM_RANDOM_OUT, then "pKeyData" will be used as output pointer for the random generated session key. The output session key is encrypted by the specified KEK */
    int nKsnLen;                            /**<Length of DUKPT KSN*/
    uchar *psKsn;                           /**<DUKPT KSN for key type "SEC_KEY_TYPE_DUKPT" */
    int nADSize;                            /**<Length of additional data*/
    uchar *pAD;                             /**<Pointer to the additional data, When the key injection method is SEC_KIM_RANDOM_OUT, then "pAD" will be used as output pointer for the length of "pKeyData".*/
} ST_SEC_ASYM_KEYIN_DATA;
/**
 *@brief	Encrypt data using the specified algorithm and the specified asymmetric key
 *@param[in] pstKeyinfo 	Pointer to the key information used for encryption (\ref ST_SEC_ASYM_KEY_INFO "ST_SEC_ASYM_KEY_INFO")
 *@param[in] MdAlg			Hash algorithm mode (It is invalid when EncodingMode is ASYM_RSA_PKCS_V15)
 *@param[in] EncodingMode   Data pading mode
 *@param[in] CryptoMode     Data encryption mode 
 *@param[in] nDataInLen     Length of data to be encryptedis
 *@param[in] psDataIn	    Data to be encrypted
 *@param[out] pnDataOutLen	Data length after encryption
 *@param[out] psDataOut		Point to the data after encryption
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

NAPI_ERR_CODE NAPI_SecAsymEncryption(ST_SEC_ASYM_KEY_INFO *pstKeyinfo, EM_SEC_MD_TYPE MdAlg, EM_SEC_ASYM_ENCODING_MODE EncodingMode, EM_SEC_ASYM_CRYPTO_MODE CryptoMode, int nDataInLen, const unsigned char *psDataIn,  int* pnDataOutLen, unsigned char *psDataOut);

/**
 *@brief	Decrypt data using the specified algorithm and the specified asymmetric key
 *@param[in] pstKeyinfo 	Pointer to the key information used for decryption (\ref ST_SEC_ASYM_KEY_INFO "ST_SEC_ASYM_KEY_INFO")
 *@param[in] MdAlg			Hash algorithm mode (It is invalid when EncodingMode is ASYM_RSA_PKCS_V15)
 *@param[in] EncodingMode   Data pading mode
 *@param[in] CryptoMode     Data decryption mode 
 *@param[in] nDataInLen     Length of data to be decrypted 
 *@param[in] psDataIn       Data to be decrypted 
 *@param[out] pnDataOutLen	Data length after decryption 
 *@param[out] psDataOut		Point to the data after decryption 
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

NAPI_ERR_CODE NAPI_SecAsymDecryption(ST_SEC_ASYM_KEY_INFO *pstKeyinfo, EM_SEC_MD_TYPE MdAlg, EM_SEC_ASYM_ENCODING_MODE EncodingMode, EM_SEC_ASYM_CRYPTO_MODE CryptoMode,int nDataInLen, const unsigned char *psDataIn,  int* pnDataOutLen, unsigned char *psDataOut);

/**
 *@brief	Get data signature
 *@param[in] pstKeyinfo 	    Pointer to the key information used for signature (\ref ST_SEC_ASYM_KEY_INFO "ST_SEC_ASYM_KEY_INFO")
 *@param[in] MdAlg  		Hash algorithm mode
 *@param[in] EncodingMode    Data pading mode
 *@param[in] nHashLen       The length of the hash data to be signed
 *@param[in] psHash	  		Hash data to be signed
 *@param[out] nSigLen		Signature data length
 *@param[out] psSig			Signature data
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

NAPI_ERR_CODE NAPI_SecAsymSign(ST_SEC_ASYM_KEY_INFO *pstKeyinfo, EM_SEC_MD_TYPE MdAlg, EM_SEC_ASYM_ENCODING_MODE EncodingMode, int nHashLen, const unsigned char *psHash,  int* nSigLen, const unsigned char *psSig);

/**
 *@brief	Verify that the signature is correct
 *@param[in] pstKeyinfo 	    Pointer to the key information used for Verify (\ref ST_SEC_ASYM_KEY_INFO "ST_SEC_ASYM_KEY_INFO")
 *@param[in] MdAlg  		Hash algorithm mode
 *@param[in] EncodingMode    Data pading mode
 *@param[in] nHashLen       The length of the hash data to be signed
 *@param[in] psHash	  		Hash data to be signed
 *@param[in] nSigLen		Signature data length
 *@param[in] psSig			Signature data
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

NAPI_ERR_CODE NAPI_SecAsymVerify(ST_SEC_ASYM_KEY_INFO *pstKeyinfo,  EM_SEC_MD_TYPE MdAlg, EM_SEC_ASYM_ENCODING_MODE EncodingMode, int nHashLen, const unsigned char *psHash,  int nSigLen, const unsigned char *psSig);



//certficate request generation
typedef void* CSR_HANDLE;
typedef void* ASYM_GEN_HANDLE;


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

NAPI_ERR_CODE NAPI_SecCSRInit(CSR_HANDLE* handle);

/**
 *@brief	 set the SubjectName of csr process(Not Supported on Android) 
 *@param[in] handle 	        the handle of csr process
 *@param[in] psSubjectName		Pointer to SubjectName
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

NAPI_ERR_CODE NAPI_SecCSRSetSubjectName(CSR_HANDLE handle,const unsigned char* psSubjectName);

/**
 *@brief	 set the key of csr process(Not Supported on Android) 
 *@param[in] handle 	        the handle of csr process
 *@param[in] pstKeyinfo		    Pointer to keyinfo of the key
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
 
NAPI_ERR_CODE NAPI_SecCSRSetKey(CSR_HANDLE handle, ST_SEC_ASYM_KEY_INFO *pstKeyinfo);

/**
 *@brief	 set the alg of csr process(Not Supported on Android) 
 *@param[in] handle 	        the handle of csr process
 *@param[in] MdAlg		        Alg
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
 
NAPI_ERR_CODE NAPI_SecCSRSetMdAlg(CSR_HANDLE handle, EM_SEC_MD_TYPE MdAlg);

/**
 *@brief	 set the KeyUsag of csr process(Not Supported on Android) 
 *@param[in] handle 	        the handle of csr process
 *@param[in] ucKeyUsage		    KeyUsag
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
 
NAPI_ERR_CODE NAPI_SecCSRSetKeyUsage(CSR_HANDLE handle, unsigned short ucKeyUsage);
 
 /**
 *@brief	 set the CertType of csr process(Not Supported on Android) 
 *@param[in] handle 	        the handle of csr process
 *@param[in] ucCertType		    CertType
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
 
NAPI_ERR_CODE NAPI_SecCSRSetNSCertType(CSR_HANDLE handle, unsigned char ucCertType);

 /**
 *@brief	 set the CA value of csr process(Not Supported on Android) 
 *@param[in] handle 	        the handle of csr process
 *@param[in] ifCA		        the value of CA
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
 
NAPI_ERR_CODE NAPI_SecCSRSetIsCA(CSR_HANDLE handle, int is_ca);

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
 
NAPI_ERR_CODE NAPI_SecCSRSetExtension(CSR_HANDLE handle, const char *oid, int oidLen, unsigned char* val, int valLen);

 /**
 *@brief	 generation the pem of csr(Not Supported on Android) 
 *@param[in] handle 	        the handle of csr process
 *@param[in] pnOlen		        Pointer to len of outbuf
 *@param[in] psOutBuf		    Pointer to the outbuf
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
 
NAPI_ERR_CODE NAPI_SecCSRGenPem(CSR_HANDLE handle, int *pnOlen, unsigned char* psOutBuf);

 /**
 *@brief	 generation the der of csr(Not Supported on Android) 
 *@param[in] handle 	        the handle of csr process
 *@param[in] pnOlen		        Pointer to len of outbuf
 *@param[in] psOutBuf		    Pointer to the outbuf
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
 
NAPI_ERR_CODE NAPI_SecCSRGenDer(CSR_HANDLE handle, int *pnOlen, unsigned char* psOutBuf);

 /**
 *@brief	 release the handle of csr(Not Supported on Android) 
 *@param[in] handle 	        the handle of csr process
 *@param[in] pnOlen		        Pointer to len of outbuf
 *@param[in] psOutBuf		    Pointer to the outbuf
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
 
NAPI_ERR_CODE NAPI_SecCSRRelease(CSR_HANDLE handle);

 /**
  *@brief	  ask for a random generated asymmetric key
  *@param[out] handle	   	the handle of generating process
  *@param[in] pstKeyinfo	pointer to the key information (\ref ST_SEC_ASYM_KEY_INFO "ST_SEC_ASYM_KEY_INFO")
  *@param[in] nADSize		size of pAD buffer
  *@param[in] pAD			pointer to the algorithm information(\ref ST_SEC_ASYM_ALG_INFO "ST_SEC_ASYM_ALG_INFO")
  *@return
   On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
 */


NAPI_ERR_CODE NAPI_SecGenerateAsymKey(ASYM_GEN_HANDLE* handle, ST_SEC_ASYM_KEY_INFO *pstKeyinfo, int nADSize, uchar *pAD);

/**
 *@brief	 ask for a random generated asymmetric key
 *@param[in] handle	  the handle of generating process
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/


NAPI_ERR_CODE NAPI_SecGenerateAsymKeyState(ASYM_GEN_HANDLE handle);
/**
 *@brief Symmetric Key Injection  with the Asymmetric key
 *@details  Added at V1.1.5;\n
 *          Inject symmetric key with the asymmetric key for all key types, with different algorithm...
 *@param[in] Method             Key generation method, ref EM_SEC_KEYIN_METHOD, only SEC_KIM_CIPHER or SEC_KIM_RANDOM_OUT 
 *@param[in] pstKGData          All needed data for key injection, ref ST_SEC_ASYM_KEYIN_DATA "ST_SEC_ASYM_KEYIN_DATA"
 *@param[in] pstKcvData         Key check value (KCV) for the injected key, Optional
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE". 
*/
NAPI_ERR_CODE NAPI_SecAsymGenerateKey( EM_SEC_KEYIN_METHOD Method, ST_SEC_ASYM_KEYIN_DATA *pstKGData, ST_SEC_KCV_DATA *pstKcvData);

/**
 *@brief Certificate verification
 *@details  Added at V1.1.5;\n
 *          Verify whether the certificate is issued by the upper CA
 *@param[in] isCA         Is it a CA certificate
 *@param[in] cert         cert data
 *@param[in] certlen      data length
 *@param[out] pubkey      public key
 *@param[out] pubkeylen   key length
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE". 
*/

NAPI_ERR_CODE NAPI_SecLoadTrustedCert(char isCA, char * cert, int certlen, char * pubkey, int * pubkeylen);

/**
 *@brief reset Certstatus
 *@details  Added at V1.1.5;\n
 *          Reset locally saved certificate status
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE". 
*/

NAPI_ERR_CODE NAPI_SecResetCertStatus(void);

/**
 *@brief  Initializes an atomic section of code. 
 *@details  Added at V1.1.5;\n
 *          Initialize the atomic transaction to back up the current key file
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE". 
*/

NAPI_ERR_CODE NAPI_SecInitAtomic(void);

/**
 *@brief Finishes an atomic section.
 *@details  Added at V1.1.5;\n
 *          End an atomic transaction and process the secret key file. When the status is true, the current secret key file is synchronized, and when the status is false, the key file is returned to the state before the transaction is executed.
 *@param[in] status     section status
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE". 
*/

NAPI_ERR_CODE NAPI_SecCommitAtomic(char status);

/**
 *@brief Convert ATM to Giske.
 *@details  Added at V1.2.0;
 *@param[in] pstKGData          All needed data for key injection, ref ST_SEC_KEYIN_DATA "ST_SEC_KEYIN_DATA"
 *@param[out] sDstKeyValue 		GISKE block (ASCII)
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE". 
*/
NAPI_ERR_CODE NAPI_SecConvertAtmToGiske(ST_SEC_KEYIN_DATA * pstKGData, char *sDstKeyValue);

/**
*@brief Initialise ECDHE context
 *@param[out] handle 	        the handle of ECDHE process
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE". 
*/
NAPI_ERR_CODE NAPI_SecECDHEInit(NAPI_HANDLE* handle); 

/**
*@brief Generate ECC key pair and return the public key
 *@param[in] handle 	        the handle of ECDHE process
 *@param[in] CurveType             ECC curve type, ref ECC_TYPE
 *@param[out] pnPubKeyLen           The length of the public key
 *@param[out] psPubKey             The public key 
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE". 
*/
NAPI_ERR_CODE NAPI_SecECDHEGenKeyPair(NAPI_HANDLE handle, ECC_TYPE CurveType, int *pnPubKeyLen, uchar *psPubKey);

/**
*@brief Generate session key with KDF 
 *@param[in] pstSessionKeyInfo  THe information of session key, ref ST_SEC_ECDHE_KEY_INFO "ST_SEC_ECDHE_KEY_INFO"
 *@param[in] pstKDFInfo        THe information of KDF parameter, ref ST_SEC_ECDHE_KDF_INFO "ST_SEC_ECDHE_KDF_INFO"
 *@param[in] nServPubKeyLen           The length of the server public key
 *@param[in] pServPubKey             The server public key 
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE". 
*/
NAPI_ERR_CODE NAPI_SecECDHEGenSK(NAPI_HANDLE handle, ST_SEC_ECDHE_KEY_INFO *pstSessionKeyInfo, ST_SEC_ECDHE_KDF_INFO *pstKDFInfo, int nServPubKeyLen, uchar *pServPubKey);

/**
*@brief Release ECDHE context
 *@param[in] handle 	        the handle of ECDHE process
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE". 
*/
NAPI_ERR_CODE NAPI_SecECDHERelease(NAPI_HANDLE handle);
/**
* @brief	Get the total number of  symmetry keys in the database and the number of symmetry keys contained in each ID
* @param	[out]	pnTotalKeyNum	the total number of symmetry keys in the database
* @param	[out]	pstKeyNumInfoArray	  the number of symmetry keys contained in each ID
* @param	[out]	pnArrayCount The arrays number of pstKeyNumInfoArray structure  
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE". 
*/
NAPI_ERR_CODE NAPI_SecGetSymmKeyNum(int *pnTotalKeyNum, ST_SEC_KEYNUM_INFO *pstKeyNumInfoArray, int* pnArrayCount);
/**
* @brief	Get the symmetry key information contained under the ID
* @param	[in]	ucKeyID	 ID of the query
* @param	[out]	pstKeyInfoArray	Key information contained in each ID
* @param	[out]	pnArrayCount The arrays number of pstKeyInfoArray structure
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE". 
*/
NAPI_ERR_CODE NAPI_SecGetSymmKeyInfoByID(uchar ucKeyID, ST_SEC_SYMM_KEYID_INFO *pstKeyInfoArray, int* pnArrayCount);

/**
 *@brief Get Dukpt key ksn.
 *@details  Added at V1.2.0;
 *@param[in] ucGroupIdx         Group ID
 *@param[out] psKsnOut 			KSN
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE". 
*/
NAPI_ERR_CODE NAPI_SecGetDukptKsn(uchar ucGroupIdx, uchar * psKsnOut);

/**
 *@brief     Verify KLD random data, generate random Signature data and get the certificate.
 *@details   Added at V1.2.3;\n
 *           KLD -- random data --> KLA (use device cert to sign random data and generate signature)\n
 *           KLA --  sig & Cert --> KLD (verify the certificate through the CA chain, decrypt the random data from sig with this certificate, and compare the data with the one sent by KLD)
 *@param[in]  nLenRandom    Random data length
 *@param[out] psRandom      Random data
 *@param[out] pLenAuthData  Signature length
 *@param[out] psAuthData    Signature data
 *@param[out] pLenAuthCert  Device Auth cert length
 *@param[out] psAuthCert    Device Auth cert
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE". 
*/

NAPI_ERR_CODE NAPI_SecKLDAuthKla(int nLenRandom, uchar* psRandom, int* pLenAuthData, uchar* psAuthData, int* pLenAuthCert, uchar* psAuthCert);
/**
 *@brief    The interface will delate all symmetric keys on the device(NOT RECOMMENDED)
 *@details  Added at V1.2.4;
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

NAPI_ERR_CODE NAPI_SecSymmKeyErase(void);

/**
 *@brief     The interface will cancel generate asymmetric keys on the device(NOT RECOMMENDED)
 *@details   Added at V1.2.4;
 *@param[in]  handle     the handle of generating process
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

NAPI_ERR_CODE NAPI_SecCancelGenerateAsymKey(ASYM_GEN_HANDLE handle);

/**
 *@brief	初始化PIN输入的ADA模式以及ADA切换开关的按钮坐标。若执行成功则返回当前的PIN输入键盘模式（ADA键盘或常规键盘），通常紧接在NDK_SecVppTpInit()之后调用。
 *@param	mode			指定PIN输入的密码键盘模式，0---常规键盘模式（点击按键完成输入），1(非0)---ADA键盘输入模式（在固定的ADA键盘布局上，通过“滑行”+“点击”完成按键输入）
 *@param	ord_btn			指定常规键盘上，进行ADA模式切换的开关按钮坐标(vpp_button)，如果传NULL或者全0的坐标，则表示不允许从常规键盘切换到ADA模式的键盘
 *@param	ord_btn			指定在ADA键盘上，进行ADA模式切换的开关按钮坐标(vpp_button)，如果传NULL或者全0的坐标，则表示不允许从ADA键盘切换到常规键盘
 *@return
 *@li		>=0				操作成功，返回当前的键盘键盘模式，0-常规键盘，1-ADA键盘
 *@li		<0				失败，参数错误，例如传入无效的按钮坐标。
*/
NAPI_ERR_CODE NAPI_SecVppADAInit(uchar mode, uchar *old_btn, uchar *ada_btn);

/**
 *@brief	Initialize the ada touch screen keyboard .
 *@param[in]	psTotalADAButton			total ada buttons (VPP_buttons).		
 *@param[in]	nTotalADAButtonLen		    length of ada buttons
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
NAPI_ERR_CODE NAPI_SecVppTpADAInit(EM_SEC_VPP_SCREEN_TYPE ScreenType);

/** @} */ // end of crypto extended

/** @addtogroup Security_Deprecated
* @{
*/
/**************************************** Deprecated ********************************************/

/**
 *@deprecated replaced by \ref NAPI_SecGetKeyInfo "NAPI_SecGetKeyInfo"
 *@brief	Get KCV value of key
 *@details	Used to check key for both part of session
 *@param[in] ucKeyType		Key type
 *@param[in] ucKeyIdx		Key index
 *@param[in] pstKcvInfoOut	KCV encryption mode
 *@param[out]	pstKcvInfoOut	KCV value
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

NAPI_ERR_CODE NAPI_SecGetKcv(uchar ucKeyType, uchar ucKeyIdx, ST_SEC_KCV_INFO *pstKcvInfoOut);

/**
 *@brief	 The interface will delate all keys on the device(NOT RECOMMENDED)
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

NAPI_ERR_CODE NAPI_SecKeyErase(void);


/** @} */ // end of Security_deprecated

#ifdef __cplusplus
}
#endif

#endif

/* End of this file*/
