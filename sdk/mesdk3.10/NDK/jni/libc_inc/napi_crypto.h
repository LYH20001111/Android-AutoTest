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
#include <time.h>
//#undef OVERSEA
//#define OVERSEA 

/** @addtogroup Security
* @{
*/

/**
 *@brief Error Code
*/
typedef enum {
	NAPI_OK,
	NAPI_ERR=-1,

	NAPI_ERR_SECP_BASE = (-1000),
	NAPI_ERR_SECP_TIMEOUT = (NAPI_ERR_SECP_BASE - 1),
	NAPI_ERR_SECP_PARAM = (NAPI_ERR_SECP_BASE - 2),
	NAPI_ERR_SECP_DBUS = (NAPI_ERR_SECP_BASE - 3),
	NAPI_ERR_SECP_MALLOC = (NAPI_ERR_SECP_BASE - 4),
	NAPI_ERR_SECP_OPEN_SEC = (NAPI_ERR_SECP_BASE - 5),
	NAPI_ERR_SECP_SEC_DRV = (NAPI_ERR_SECP_BASE - 6),
	NAPI_ERR_SECP_GET_RNG = (NAPI_ERR_SECP_BASE - 7),
	NAPI_ERR_SECP_GET_KEY = (NAPI_ERR_SECP_BASE - 8),
	NAPI_ERR_SECP_KCV_CHK = (NAPI_ERR_SECP_BASE - 9),
	NAPI_ERR_SECP_GET_CALLER = (NAPI_ERR_SECP_BASE - 10),
	NAPI_ERR_SECP_OVERRUN = (NAPI_ERR_SECP_BASE - 11),
	NAPI_ERR_SECP_NO_PERMIT = (NAPI_ERR_SECP_BASE - 12),
	NAPI_ERR_SECP_TAMPER = (NAPI_ERR_SECP_BASE - 13),
	NAPI_ERR_SECP_UNSUPPORT = (NAPI_ERR_SECP_BASE - 14),
	NAPI_ERR_SECVP_BASE = (-1100),
	NAPI_ERR_SECVP_TIMEOUT = (NAPI_ERR_SECVP_BASE - 1),
	NAPI_ERR_SECVP_PARAM = (NAPI_ERR_SECVP_BASE - 2),
	NAPI_ERR_SECVP_DBUS = (NAPI_ERR_SECVP_BASE - 3),
	NAPI_ERR_SECVP_OPEN_EVENT0 =	(NAPI_ERR_SECVP_BASE - 4),
	NAPI_ERR_SECVP_SCAN_VAL = (NAPI_ERR_SECVP_BASE - 5),
	NAPI_ERR_SECVP_OPEN_RNG = (NAPI_ERR_SECVP_BASE - 6),
	NAPI_ERR_SECVP_GET_RNG = (NAPI_ERR_SECVP_BASE - 7),
	NAPI_ERR_SECVP_GET_ESC = (NAPI_ERR_SECVP_BASE - 8),
	NAPI_ERR_SECVP_VPP = (-1120),
	NAPI_ERR_SECVP_INVALID_KEY=(NAPI_ERR_SECVP_VPP),
	NAPI_ERR_SECVP_NOT_ACTIVE=(NAPI_ERR_SECVP_VPP-1),
	NAPI_ERR_SECVP_TIMED_OUT=(NAPI_ERR_SECVP_VPP-2),
	NAPI_ERR_SECVP_ENCRYPT_ERROR=(NAPI_ERR_SECVP_VPP-3),
	NAPI_ERR_SECVP_BUFFER_FULL=(NAPI_ERR_SECVP_VPP-4),
	NAPI_ERR_SECVP_PIN_KEY=(NAPI_ERR_SECVP_VPP-5),
	NAPI_ERR_SECVP_ENTER_KEY=(NAPI_ERR_SECVP_VPP-6),
	NAPI_ERR_SECVP_BACKSPACE_KEY=(NAPI_ERR_SECVP_VPP-7),
	NAPI_ERR_SECVP_CLEAR_KEY=(NAPI_ERR_SECVP_VPP-8),
	NAPI_ERR_SECVP_CANCEL_KEY=(NAPI_ERR_SECVP_VPP-9),
	NAPI_ERR_SECVP_GENERALERROR=(NAPI_ERR_SECVP_VPP-10),
	NAPI_ERR_SECVP_CUSTOMERCARDNOTPRESENT=(NAPI_ERR_SECVP_VPP-11),
	NAPI_ERR_SECVP_HTCCARDERROR=(NAPI_ERR_SECVP_VPP-12),
	NAPI_ERR_SECVP_WRONG_PIN_LAST_TRY=(NAPI_ERR_SECVP_VPP-13),
	NAPI_ERR_SECVP_WRONG_PIN=(NAPI_ERR_SECVP_VPP-14),
	NAPI_ERR_SECVP_ICCERROR=(NAPI_ERR_SECVP_VPP-15),
	NAPI_ERR_SECVP_PIN_BYPASS=(NAPI_ERR_SECVP_VPP-16),
	NAPI_ERR_SECVP_ICCFAILURE=(NAPI_ERR_SECVP_VPP-17),
	NAPI_ERR_SECVP_GETCHALLENGE_BAD=(NAPI_ERR_SECVP_VPP-18),
	NAPI_ERR_SECVP_GETCHALLENGE_NOT8=(NAPI_ERR_SECVP_VPP-19),
	NAPI_ERR_SECVP_PIN_ATTACK_TIMER=(NAPI_ERR_SECVP_VPP-20),

	NAPI_ERR_SECCR_BASE = (-1200),
	NAPI_ERR_SECCR_TIMEOUT = (NAPI_ERR_SECCR_BASE - 1),
	NAPI_ERR_SECCR_PARAM = (NAPI_ERR_SECCR_BASE - 2),
	NAPI_ERR_SECCR_DBUS = (NAPI_ERR_SECCR_BASE - 3),
	NAPI_ERR_SECCR_MALLOC = (NAPI_ERR_SECCR_BASE - 4),
	NAPI_ERR_SECCR_OPEN_RNG = (NAPI_ERR_SECCR_BASE - 5),
	NAPI_ERR_SECCR_DRV = (NAPI_ERR_SECCR_BASE - 6),
	NAPI_ERR_SECCR_KEY_TYPE = (NAPI_ERR_SECCR_BASE - 7),
	NAPI_ERR_SECCR_KEY_LEN = (NAPI_ERR_SECCR_BASE - 8),
	NAPI_ERR_SECCR_GET_KEY = (NAPI_ERR_SECCR_BASE - 9),

	NAPI_ERR_SECKM_BASE = (-1300),
	NAPI_ERR_SECKM_TIMEOUT = (NAPI_ERR_SECKM_BASE - 1),
	NAPI_ERR_SECKM_PARAM = (NAPI_ERR_SECKM_BASE - 2),
	NAPI_ERR_SECKM_DBUS = (NAPI_ERR_SECKM_BASE - 3),
	NAPI_ERR_SECKM_MALLOC = (NAPI_ERR_SECKM_BASE - 4),
	NAPI_ERR_SECKM_OPEN_DB = (NAPI_ERR_SECKM_BASE - 5),
	NAPI_ERR_SECKM_DEL_DB = (NAPI_ERR_SECKM_BASE - 6),
	NAPI_ERR_SECKM_DEL_REC = (NAPI_ERR_SECKM_BASE - 7),
	NAPI_ERR_SECKM_INSTALL_REC = (NAPI_ERR_SECKM_BASE - 8),
	NAPI_ERR_SECKM_READ_REC = (NAPI_ERR_SECKM_BASE - 9),
	NAPI_ERR_SECKM_OPT_NOALLOW = (NAPI_ERR_SECKM_BASE - 10),
	NAPI_ERR_SECKM_KEY_MAC = (NAPI_ERR_SECKM_BASE - 11),
	NAPI_ERR_SECKM_KEY_TYPE = (NAPI_ERR_SECKM_BASE - 12),
	NAPI_ERR_SECKM_KEY_ARCH = (NAPI_ERR_SECKM_BASE - 13),
	NAPI_ERR_SECKM_KEY_LEN  = (NAPI_ERR_SECKM_BASE - 14),
	NAPI_ERR_SECKM_SYS = (NAPI_ERR_SECKM_BASE - 15),
	NAPI_ERR_SECKM_UNSUPPORT = (NAPI_ERR_SECKM_BASE - 16),
	NAPI_ERR_SECKM_KEY_ALREADY_USED = (NAPI_ERR_SECKM_BASE - 17),
	NAPI_ERR_SECKM_CALCKCV = (NAPI_ERR_SECKM_BASE - 18),
	NAPI_ERR_SECKM_DEL_TABLE = (NAPI_ERR_SECKM_BASE - 19),
	NAPI_ERR_SECKM_SIZE_ERROR = (NAPI_ERR_SECKM_BASE - 20),
	NAPI_ERR_SECKM_OPT_ERROR = (NAPI_ERR_SECKM_BASE - 21),
	NAPI_ERR_SECKM_TABLE_ERROR = (NAPI_ERR_SECKM_BASE - 22),
	NAPI_ERR_SECKM_DB_NULL = (NAPI_ERR_SECKM_BASE - 23),
	NAPI_ERR_SECKM_NOT_SUPPORT = (NAPI_ERR_SECKM_BASE - 24),

	//key store
	NAPI_ERR_SECKS_BASE = (-1400),
	NAPI_ERR_SECKS_TIMEOUT = (NAPI_ERR_SECKS_BASE - 1),
	NAPI_ERR_SECKS_PARAM = (NAPI_ERR_SECKS_BASE - 2),
	//kla
	NAPI_ERR_SECKLA_BASE = (-1500),
	NAPI_ERR_SECKLA_ERR_INTERNAL = (NAPI_ERR_SECKLA_BASE -1),				/*Unspecified internal error.*/
	NAPI_ERR_SECKLA_PARAM = (NAPI_ERR_SECKLA_BASE -2),				/*Invalid parameter passed to function.*/
	NAPI_ERR_SECKLA_ERR_INVALID_CRT = (NAPI_ERR_SECKLA_BASE -3),		/*Invalid certification*/
	NAPI_ERR_SECKLA_ERR_INVALID_SIG = (NAPI_ERR_SECKLA_BASE -4),			/*Invalid nonce signature*/
	NAPI_ERR_SECKLA_ERR_KEY_NOT_FOUND = (NAPI_ERR_SECKLA_BASE -5),		/*Key not found*/
	NAPI_ERR_SECKLA_ERR_INVALIDKEY_USAGE = (NAPI_ERR_SECKLA_BASE -6),		/*Invalid use of the key according to the key tag*/
	//NAPI algorithm
	NAPI_ERR_SECALG_BASE = (-1600),
	NAPI_ERR_SECALG_TIMEOUT = (NAPI_ERR_SECALG_BASE - 1),
	NAPI_ERR_SECALG_PARAM = (NAPI_ERR_SECALG_BASE - 2),
	NAPI_ERR_SECALG_UPDATE = (NAPI_ERR_SECALG_BASE - 3),
	NAPI_ERR_SECALG_FINISH = (NAPI_ERR_SECALG_BASE - 4),
	//
	NAPI_ERR_SEC_CFG_BASE = (-1700),
	NAPI_ERR_SEC_CFG_TABLE = (NAPI_ERR_SEC_CFG_BASE - 1),             /* indicate current key table, "" for app itself */
	NAPI_ERR_SEC_CFG_UNIQUE = (NAPI_ERR_SEC_CFG_BASE - 2),                /* check if installing key is unique : 0 - no check, 1 - check */
	NAPI_ERR_SEC_CFG_MISUSE = (NAPI_ERR_SEC_CFG_BASE - 3),                /* check if key is misused according to its type : 0 - no check, 1 - check */
	NAPI_ERR_SEC_CFG_TRIES_LIMIT = (NAPI_ERR_SEC_CFG_BASE - 4),           /* check if current function is overrun: 0 - no check, 1 - check */
	NAPI_ERR_SEC_CFG_STRENGTH = (NAPI_ERR_SEC_CFG_BASE - 5),              /* keys should be protected by the same or higher strength keys: 0 - no check, 1 - check */
	NAPI_ERR_SEC_CFG_KEYLEN_LIMIT = (NAPI_ERR_SEC_CFG_BASE - 6),          /* key length should be stronger than 8 bytes : 0 - no check, 1- check */
	NAPI_ERR_SEC_CFG_DPA_DEFENCE = (NAPI_ERR_SEC_CFG_BASE - 7),          /* DPA defence: 0 - disable, 1- enable */
}EM_NAPI_ERR;


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
    /* DUKPT AES Algorithm: Unrealized */
    SEC_CIPHER_AES_DUKPT_ECB_RESP,     /**< 3DES encryption in ECB mode, with DUKPT key variant for Data Encryption: response (Input Message) */
    SEC_CIPHER_AES_DUKPT_ECB_BOTH,    /**< 3DES encryption in ECB mode, with DUKPT key variant for Data Encryption: request or both ways (Output Message) */
    SEC_CIPHER_AES_DUKPT_CBC_RESP,     /**< 3DES encryption in CBC mode, with DUKPT key variant for Data Encryption: response (Input Message) */
    SEC_CIPHER_AES_DUKPT_CBC_BOTH,    /**< 3DES encryption in CBC mode, with DUKPT key variant for Data Encryption: request or both ways (Output Message) */
    SEC_CIPHER_AES_DUKPT_CFB_RESP,
    SEC_CIPHER_AES_DUKPT_CFB_BOTH,
    SEC_CIPHER_AES_DUKPT_OFB_RESP,
    SEC_CIPHER_AES_DUKPT_OFB_BOTH,
#ifndef OVERSEA
    /* SM4 Algorithm*/
	SEC_CIPHER_SM4_ECB,        /**< ECB mode without specifying the key length, firmware will use appropriate algorithm according to the key length */
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
    SEC_MAC_AES_DUKPT_RESP_LAST, /**< MAC using a DUKPT key (response) with AES on last block only. 9606 */
    SEC_MAC_AES_DUKPT_RESP_X99,  /**< MAC using a DUKPT key (response) with AES on all blocks. X99 */
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
  *@brief Key Injection Methods
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
    SEC_KIM_MAX,           /**< Invalid keygen method. */
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
    /* Asym Auth Key*/
    KEY_USE_ASYM_AUTH = 0x20,
    /* Asym Data Key*/
    KEY_USE_ASYM_DATA,
    /* Asym Key Use for AUTH&ENC */
    KEY_USE_ASYM_ANY,
    /* Asym Key Use for KEY DISTRIBUTION */
    KEY_USE_ASYM_KEY_DISTRIBUTION,   
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
    EM_SEC_VPP_SESSION_TYPE_MAX = 65536
} EM_SEC_VPP_SESSION_TYPE;

/**
 *@brief undefined 3
*/
typedef enum{
	NAPI_SEC_KCV_NONE=0,		
	NAPI_SEC_KCV_ZERO,
	NAPI_SEC_KCV_VAL,		
	NAPI_SEC_KCV_DATA,
	NAPI_SEC_KCV_CMAC,
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

#define KEY_USAGE_DEN 	0X4430	//‘D0’	0x44, 0x30	Data Encryption 
#define KEY_USAGE_IV 	0X4930  //‘I0’	0x49, 0x30	IV 
#define KEY_USAGE_CTL 	0X5430  //‘T0’	0x54, 0x30	‘T’ for conTrol vector
#define KEY_USAGE_KEW 	0X4B30  //‘K0’	0x4B, 0x30	Key Encryption or wrapping
#define KEY_USAGE_GMAC 	0X4730  //‘G0’	0x47, 0x30	MAC Generation
#define KEY_USAGE_VMAC 	0X4D30  //‘M0’	0x4D, 0x30	MAC Verification
//#define KEY_USAGE_PIN 	0X5030  //‘P0’	0x50, 0x30	Pin Encryption
#define KEY_USAGE_KPV 	0X5630  //‘V0’	0x56, 0x30	PIN verification, KPV
#define KEY_USAGE_CVK 	0X4330  //‘C0’	0x43, 0x30	CVK Card Verification Key 
#define KEY_USAGE_KC 	0X6330  //‘c0’	0x63, 0x30	Key component
//#define KEY_USAGE_BDK 	0X4230  //‘B0’	0x42, 0x30	BDK Base Derivation Key
#define KEY_USAGE_MAC1 	0X3030  //‘00’	0x30, 0x30	ISO 9797-1 MAC Algorithm 1 – 56 bits
#define KEY_USAGE_MAC2 	0X3130  //‘10’	0x31, 0x30	ISO 9797-1 MAC Algorithm 1 – 112 bits
#define KEY_USAGE_MAC3 	0X3230  //‘20’	0x32, 0x30	ISO 9797-1 MAC Algorithm 2 – 112 bits
#define KEY_USAGE_MAC4 	0X3330  //‘30’	0x33, 0x30	ISO 9797-1 MAC Algorithm 3 – 112 bits
#define KEY_USAGE_MAC5 	0X3430  //‘40’	0x34, 0x30	ISO 9797-1 MAC Algorithm 4 – 112 bits
#define KEY_USAGE_MAC6 	0X3530  //‘50’	0x35, 0x30	ISO 9797-1 MAC Algorithm 5 – 56 bits
#define KEY_USAGE_MAC7 	0X3630  //‘60’	0x36, 0x30	ISO 9797-1 MAC Algorithm 5 – 112 bits

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
    int CipherMode;   /**<cipher mode for the key injection, e.g. ECB, CBC*/
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
	uchar *pAD;        /**<Pointer to the additional data*/
}ST_SEC_KEYIN_DATA;

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
        uchar 	ucScrKeyType;	       /**<Type of source key that diffused this key (\ref EM_SEC_KEY_TYPE "EM_SEC_KEY_TYPE"), it shall not be lower than key level ucDstKeyType is at*/
        uchar 	ucScrKeyIdx;           /**<Index of source key that diffused this key, typically index starts from 1, if this variable equals 0, then this key is written in plaintext*/
        uchar 	ucDstKeyIdx;	       /**<Index of destination key, range [1 - 255], DO NOT USE 0*/
        int 	nDstKeyLen;            /**<Length of destination key:96, 120*/
        char 	sDstKeyValue[120 + 1]; /** GISKE block (ASCII)*/
} ST_NAPI_SEC_GISKE_KEY_INFO;



/**
 *@brief 	to read key value of keyboard in timeout
 *@details	read key within time limit, the process is as follows: press one key, release it, and return key code 
 *@param	unTime	<=0: not timeout, keep on waiting for reading key
							other value: latency time (unit: second)
 *@param	pnCode	Obtain input key code; if no key is pressed in regulated time, pnCode value is 0. 
 *@li       NDK_OK 				   operation succeeded
 *@li   	other EM_NDK_ERR	   operation failed
*/

/**
*@brief Key Injection
 *@details Generic key injection for all key types, with different algorithm...
 *@param[in] Method             Key generation method,ref EM_SEC_KEYIN_METHOD "EM_SEC_KEYIN_METHOD"
 *@param[in] pstKGData          All needed data for key injection, ref ST_SEC_KEYIN_DATA "ST_SEC_KEYIN_DATA"
 *@param[in] pstKcvData         Key check value (KCV) for the injected key, Optional
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE". 
*/

extern int (*NAPI_SecGenerateKey)( EM_SEC_KEYIN_METHOD Method, ST_SEC_KEYIN_DATA *pstKGData, ST_SEC_KCV_DATA *pstKcvData);

/**
 @brief Set the Lenth of passward during PIN entry.
 *@param[in] key    the Lenth of passward: 
                    like:0,4,6
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE". 
*/

extern int (*NAPI_SecVPPSetExpPinLenIn)(char *pszExpPinLenIn);

/**
 *@brief        Initialises the Virtual (internal) PIN pad. Start the PIN entry mode.
 *@param[in] SessionType    For SessionType "SEC_VPP_MASTER_SESSION", pAD will be an encrypted session key, see ST_SEC_SESSION_KEY.
 *@param[in] CipherID       PIN Key Algorithm: TDES or AES
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

extern int (*NAPI_SecVPPInit)( EM_SEC_VPP_SESSION_TYPE SessionType,
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

extern int (*NAPI_SecVPPGetEvent)(int *nEvent, uchar *psPinBlock, int *pnOutPinLen, uchar *psKsn, int *pnOutKsnLen);

/**
 @brief Simulated key code to externally influence PIN entry procedure.
 *@param[in] key    The simulated key may be set externally during PIN entry: 
                    KEY_CANCEL - simulates pressing CANCEL key
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE". 
*/

extern int (*NAPI_SecVPPSetEvent)(uint key);

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

extern int (*NAPI_SecEncryption)(ST_SEC_ENCRYPTION_DATA *pstDataIn, uchar *psDataOut, int *pnOutLen, uchar *psKsnOut, int *pnOutKsnLen);

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

extern int (*NAPI_SecDecryption)(ST_SEC_ENCRYPTION_DATA *pstDataIn, uchar *psDataOut, int *pnOutLen, uchar *psKsnOut, int *pnOutKsnLen);

/**
 *@brief        Generate Message Authentication Code for a block of data.
 *@param[in] CipherType      Full cipher identifier (e.g. SEC_CIPHER_AES_128_CBC)
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
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE". 
*/

extern int (*NAPI_SecGenerateMAC)(EM_SEC_MAC_TYPE MacType, uchar ucKeyID, uchar *psIV, int unIVSize, uchar *psDataIn, int nDataInLen, uchar *pAD, int unADSize, uchar *psMacOut, int *pnOutLen, uchar *psKsnOut, int *nOutKsnLen);

/**
 *@brief		Returns key information such as KCV, length, etc.
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

extern int (*NAPI_SecGetKeyInfo)(EM_SEC_KEY_INFO_ID InfoID, uchar ucKeyID, EM_SEC_CRYPTO_KEY_TYPE KeyType, EM_SEC_KEY_USAGE KeyUsage,
					  uchar *pAD, uint unADSize, uchar *psOutInfo, int *pnOutInfoLen);
/**
 *@brief		Delete key.
 *@param[in] ucKeyID		 Key index, 1~250
 *@param[in] KeyType		 Key Type
 *@param[in] KeyUsage		 Key Usage

 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

extern int (*NAPI_SecDeleteKey)(uchar ucKeyID, EM_SEC_CRYPTO_KEY_TYPE KeyType, EM_SEC_KEY_USAGE KeyUsage);

/**
 *@brief		Set KeyOwner.
 *@param[in] pszOwner		Key Owner,
 							1. If the application does not set the keyOwner, the shared key table "Phoenix_Share_Table" is used by default;
							2. Only the root application can configure the keyowner arbitrarily. 
							3. User permission application allows set the keyowner to as "" (self-key table) or "*" (shared key table);
 
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

extern int (*NAPI_SecSetKeyOwner)(char *pszOwner);

/**
 *@brief		Returns Security Module Version.

 *@param[out] pszVerInfoOut	  Version Info
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

extern int (*NAPI_SecGetVer)(uchar * pszVerInfoOut);

/**
 *@brief		Get Random Number.
 *@param[in] nRandLen		Length of Random Number,

 *@param[out] pvRandom	  Random Number
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

extern int (*NAPI_SecGetRandom)(int nRandLen , void *pvRandom);

/**
 *@brief	Get tamper status
 *@param[out]	pnStatus			Tamper status (\ref EM_SEC_TAMPER_STATUS "EM_SEC_TAMPER_STATUS")
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

extern int (*NAPI_SecGetTamperStatus)(uint32_t *pnStatus);

/**
 *@brief 		Get RTC time(root only)
 *@param[in]    stTime Time
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

extern int (*NAPI_SecGetRtcTime)(struct tm *t);

/**
 *@brief 		Set RTC time(root only)
 *@param[in]    stTime Time
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

extern int (*NAPI_SecSetRtcTime)(struct tm *t);

/**
 *@brief	 Delate all keys
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/

extern int (*NAPI_SecKeyErase)(void);

/**
 *@brief	Get time left before next reboot required from PCI 4.0
 *@details 	This function gets the time to reset as specified in PCI 4.0 (and above). PCI 4.0 specifies that device must be rebooted every 24 hours maximum. If application does not implements this rebooting sequence, firmware does perform that feature on its own side, this function retrieves how much time until it.\n
It is useful for applications in order to reboot terminal by its own when it is "idle". This is, when no critical action is running: payment transaction, batch process, updating tasks, sending reports to acquire.
 *@param[in] timeToReboot Time left before next reboot
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref EM_NDK_ERR "EM_NDK_ERR".
*/

extern int (*NAPI_SecGetTimeToReboot)( unsigned int *timeToReboot );

/**
 *@brief 物理按键功能
*/
typedef enum {
	SEC_VPP_BUTTON_USAGE_QUIT,       /**< 退出*/
	SEC_VPP_BUTTON_USAGE_CLEAR,      /**< 清空*/
} EM_SEC_VPP_BUTTON_FUNC;
/**
 *@brief Set the function of physical buttons.
 *@param[in] button   the physical button to be set
 *@param[in] func     the function of the physical button (\ref EM_SEC_VPP_BUTTON_FUNC "EM_SEC_VPP_BUTTON_FUNC")
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
extern int (*NAPI_SecVPPSetButtonFunc)(int button, EM_SEC_VPP_BUTTON_FUNC func);

/** @} */ // end of Security

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

extern int (*NAPI_SecGetKcv)(uchar ucKeyType, uchar ucKeyIdx, ST_SEC_KCV_INFO *pstKcvInfoOut);


/** @} */ // end of Security_deprecated

#ifdef __cplusplus
}
#endif

#endif

/* End of this file*/
