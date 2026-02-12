/* Security Module */
#ifndef FORTH_CRYPTO_H
#define FORTH_CRYPTO_H

#include <stdint.h>
#include "comm.h"

typedef enum{
	CMDID_NAPI_SecVPPInit = 7600,
	CMDID_NAPI_SecVPPGetEvent,
	CMDID_NAPI_SecVPPSetEvent,
	CMDID_NAPI_SecEncryption,
	CMDID_NAPI_SecDecryption,
	CMDID_NAPI_SecGenerateMAC,
	CMDID_NAPI_SecGetKeyInfo,
	CMDID_NAPI_SecDeleteKey,
	CMDID_NAPI_SecGetServKeyOwner,
	CMDID_NAPI_SecGenerateKey,
//	CMDID_NAPI_SecKeyErase,
	CMDID_NAPI_SecClear,
	CMDID_NAPI_SecGetVer,
	CMDID_NAPI_SecGetRandom,
	CMDID_NAPI_SecGetTamperStatus,
	CMDID_NAPI_SecVppTpInit,
	CMDID_NAPI_SecKlaMKLDAuth,
//	CMDID_NAPI_SecReadCardInfo,
	CMDID_NAPI_SecGetDrySR,
	CMDID_NAPI_SecGetMposKeyOwner,
	CMDID_NAPI_SecSetMposKeyOwner,
	CMDID_NAPI_SecKmGetMac,
}napi_sec_cmd_t;

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
    NAPI_ERR_SEC_CFG_CLEARKEY_LIMIT       = (NAPI_ERR_SEC_CFG_BASE - 8),       /* check if the clearkey is allowed to be installed: 0 - disable, 1- enable */
    NAPI_ERR_SEC_CFG_VPP_STATIC_KEY_LAYOUT_LIMIT = (NAPI_ERR_SEC_CFG_BASE - 9),       /* check if the clearkey is allowed to be installed: 0 - disable, 1- enable */

}EM_NAPI_ERR;

/**
        @brief  Key Type.
*/
typedef enum {
    KEY_TYPE_DES,
    KEY_TYPE_AES,
    KEY_TYPE_SM4,
	KEY_TYPE_MAX,
	KEY_TYPE_ASYM_RSA = 0x20,
	KEY_TYPE_ASYM_ECC,                  /**(not support yet)*/
	KEY_TYPE_ASYM_SM2,                  /**(not support yet)*/
	KEY_TYPE_ASYM_MAX,
	EM_SEC_CRYPTO_KEY_TYPE_MAX=65536
} EM_SEC_CRYPTO_KEY_TYPE;

/**
        @brief  block cipher mode.
        @details All block cipher modes are defined here. But only ECB & CBC modes are supported at present.
*/
typedef enum {
    SEC_CIPHER_MODE_ECB,
    SEC_CIPHER_MODE_CBC,
    SEC_CIPHER_MODE_CFB,
    SEC_CIPHER_MODE_OFB,
    SEC_CIPHER_MODE_CTR,
    SEC_CIPHER_MODE_GCM,
    SEC_CIPHER_MODE_STREAM,
    SEC_CIPHER_MODE_CCM
} EM_SEC_CIPHER_MODE;

/**
   @brief  Full cipher identifier for data encryption.
   @details Including the cipher type and also the block cipher operation mode. Only ECB & CBC modes are supported at present.
*/
//typedef enum {
//    /* TDES Algorithm */
//    SEC_CIPHER_DES_ECB,      /**< ECB mode without specifying the key length, firmware will use appropriate algorithm according to the key length */
//    SEC_CIPHER_DES_CBC,      /**< CBC mode without specifying the key length, firmware will use appropriate algorithm according to the key length */
//    SEC_CIPHER_DES_CFB,
//    SEC_CIPHER_DES_OFB,
//    SEC_CIPHER_AES_ECB,        /**< ECB mode without specifying the key length, firmware will use appropriate algorithm according to the key length */
//    SEC_CIPHER_AES_CBC,        /**< CBC mode without specifying the key length, firmware will use appropriate algorithm according to the key length */
//    SEC_CIPHER_AES_CFB,
//    SEC_CIPHER_AES_OFB,
//    /* DUKPT TDES Algorithm */
//    SEC_CIPHER_DUKPT_ECB_RESP,     /**< 3DES encryption in ECB mode, with DUKPT key variant for Data Encryption: response (Input Message) */
//    SEC_CIPHER_DUKPT_ECB_BOTH,    /**< 3DES encryption in ECB mode, with DUKPT key variant for Data Encryption: request or both ways (Output Message) */
//    SEC_CIPHER_DUKPT_CBC_RESP,     /**< 3DES encryption in CBC mode, with DUKPT key variant for Data Encryption: response (Input Message) */
//    SEC_CIPHER_DUKPT_CBC_BOTH,    /**< 3DES encryption in CBC mode, with DUKPT key variant for Data Encryption: request or both ways (Output Message) */
//    SEC_CIPHER_DUKPT_CFB_RESP,
//    SEC_CIPHER_DUKPT_CFB_BOTH,
//    SEC_CIPHER_DUKPT_OFB_RESP,
//    SEC_CIPHER_DUKPT_OFB_BOTH,
//    /* DUKPT AES Algorithm: Unrealized */
//    SEC_CIPHER_AES_DUKPT_ECB_RESP,     /**< 3DES encryption in ECB mode, with DUKPT key variant for Data Encryption: response (Input Message) */
//    SEC_CIPHER_AES_DUKPT_ECB_BOTH,    /**< 3DES encryption in ECB mode, with DUKPT key variant for Data Encryption: request or both ways (Output Message) */
//    SEC_CIPHER_AES_DUKPT_CBC_RESP,     /**< 3DES encryption in CBC mode, with DUKPT key variant for Data Encryption: response (Input Message) */
//    SEC_CIPHER_AES_DUKPT_CBC_BOTH,    /**< 3DES encryption in CBC mode, with DUKPT key variant for Data Encryption: request or both ways (Output Message) */
//    SEC_CIPHER_AES_DUKPT_CFB_RESP,
//    SEC_CIPHER_AES_DUKPT_CFB_BOTH,
//    SEC_CIPHER_AES_DUKPT_OFB_RESP,
//    SEC_CIPHER_AES_DUKPT_OFB_BOTH,
//    /* SM4 Algorithm*/
//	SEC_CIPHER_SM4_ECB,        /**< ECB mode without specifying the key length, firmware will use appropriate algorithm according to the key length */
//    SEC_CIPHER_SM4_CBC,        /**< CBC mode without specifying the key length, firmware will use appropriate algorithm according to the key length */
//
//	SEC_CIPHER_TYPE_MAX
//} EM_SEC_CIPHER_TYPE_OLD;

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
//#ifndef OVERSEA
	/* SM4 Algorithm*/
	SEC_CIPHER_SM4_ECB = 24,        /**< ECB mode without specifying the key length, firmware will use appropriate algorithm according to the key length */
	SEC_CIPHER_SM4_CBC,        /**< CBC mode without specifying the key length, firmware will use appropriate algorithm according to the key length */
//    SEC_CIPHER_SM4_CFB,
//    SEC_CIPHER_SM4_OFB,
//#endif
	SEC_CIPHER_TYPE_MAX,
	SEC_CIPHER_KEYLEN_8= (1<<8),			/**<Encrypted with 8-byte  key*/
	SEC_CIPHER_KEYLEN_16=(1<<9),		   /**<Encrypted with 16-byte  key*/
	SEC_CIPHER_KEYLEN_24=(1<<10),			/**<Encrypted with 24-byte	key*/
	SEC_CIPHER_DERIVE_PIN=(1<<11),           /**< DUKPT key derive a key for PIN*/
	SEC_CIPHER_DERIVE_MAC= (1<<12),            /**<DUKPT key derive a key for MAC*/
	SEC_CIPHER_DERIVE_MAC_RESP=(1<<13),           /**<DUKPT key derive a key for MAC(response)*/
	EM_SEC_CIPHER_TYPE_MAX = 65536
} EM_SEC_CIPHER_TYPE;

typedef enum {
    SEC_PADDING_NONE = 0,      /**< Never pad (full blocks only) */
    SEC_PADDING_PKCS7,         /**< PKCS7 padding, same as PKCS5. Always padded even though the data is multiple of block size. The value of each added byte is the number of bytes that are added, e.g. "DD DD DD DD 04 04 04 04" */
    SEC_PADDING_ONE_AND_ZEROS, /**< ISO/IEC 7816-4 padding. First byte is a mandatory byte valued '80' then rest bytes are set to zero, e.g. "DD DD DD DD 80 00 00 00" */
    SEC_PADDING_ZEROS_AND_LEN, /**< ANSI X.923 padding. Zeros are padded and the last byte defines the padding boundaries or the number of padded bytes, e.g. "DD DD DD DD 00 00 00 04" */
    SEC_PADDING_ZEROS         /**< zero padding (not reversible!), e.g. "DD DD DD DD 00 00 00 00" */
} EM_SEC_PADDING;

/**
        @brief  MAC (Message Authentication Code) Algorithms.
*/
//typedef enum {
//    /* TDES MAC */
//    SEC_MAC_TDES_LAST, 	         /**< MAC Digital signature with TDES on last block only. 9606 */
//    SEC_MAC_TDES_X99, 	         /**< MAC Digital signature with TDES on all blocks. X99 */
//    SEC_MAC_TDES_X919,           /**< MAC Digital signature with sigle DES on each block, but full TDES on last block. */
//    SEC_MAC_TDES_UNIONPAY_ECB,   /**< MAC Digital signature with TDES on the first 4 bytes of last block. In accordance to the UnionPay requirement */
//    /* DUKPT MAC */
//    SEC_MAC_DUKPT_LAST,          /**< MAC using a DUKPT key with TDES on last block only. 9606 */
//    SEC_MAC_DUKPT_X99,           /**< MAC using a DUKPT key with TDES on all blocks.  X99 */
//    SEC_MAC_DUKPT_X919,          /**< MAC Digital signature with sigle DES on each block, but full TDES on last block. */
//    SEC_MAC_DUKPT_UNIONPAY_ECB,  /**< MAC Digital signature with TDES on the first 4 bytes of last block. In accordance to the UnionPay requirement */
//    /* DUKPT RESPONSE MAC */
//    SEC_MAC_DUKPT_RESP_LAST,     /**< MAC using a DUKPT key (response) with TDES on last block only. 9606 */
//    SEC_MAC_DUKPT_RESP_X99,      /**< MAC using a DUKPT key (response) with TDES on all blocks.  X99 */
//    SEC_MAC_DUKPT_RESP_X919,     /**< MAC using a DUKPT key (response) with sigle DES on each block, but full TDES on last block. */
//    SEC_MAC_DUKPT_RESP_UNIONPAY_ECB, /**< MAC using a DUKPT key (response) with TDES on the first 4 bytes of last block. In accordance to the UnionPay requirement */
//    /* AES MAC */
//    SEC_MAC_AES_LAST,            /**< MAC Digital signature with AES on last block only. 9606 */
//    SEC_MAC_AES_X99,             /**< MAC Digital signature with AES on all blocks. X99 */
//    /* AES DUKPT MAC */
//    SEC_MAC_AES_DUKPT_LAST,      /**< MAC using a DUKPT key with AES on last block only. 9606 */
//    SEC_MAC_AES_DUKPT_X99,       /**< MAC using a DUKPT key with AES on all blocks. X99 */
//    SEC_MAC_AES_DUKPT_RESP_LAST, /**< MAC using a DUKPT key (response) with AES on last block only. 9606 */
//    SEC_MAC_AES_DUKPT_RESP_X99,  /**< MAC using a DUKPT key (response) with AES on all blocks. X99 */
//
//	/* SM4 MAC*/
//	SEC_MAC_SM4_LAST,            /**< MAC Digital signature with SM4 on last block only. 9606 */
//    SEC_MAC_SM4_X99,             /**< MAC Digital signature with SM4 on all blocks. X99 */
//	SEC_MAC_MAX
//} EM_SEC_MAC_TYPE;

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
    SEC_KIM_MAX            /**< Invalid keygen method. */
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
 *@brief Key Injection Data
*/
typedef struct {
    uchar ucKEKIdx;    /**<KEK Index, 1~250 */
	EM_SEC_CRYPTO_KEY_TYPE KEKType;   /**<key type of the master key: TDES or AES*/
    EM_SEC_KEY_USAGE KEKUsage;    /**<Specify the KEK Usage*/

	uchar ucKeyIdx;    /**<Index for the injected key, 1~250 */
	EM_SEC_CRYPTO_KEY_TYPE KeyType;   /**<key type of the key to be injected. e.g. TDES or AES*/
    EM_SEC_KEY_USAGE KeyUsage;   /**<Usage of the key to be injected (\ref EM_SEC_KEY_USAGE "EM_SEC_KEY_USAGE"), for SEC_KGM_TR31 method, it is ignored and will be parsed from TR31 header*/

	EM_SEC_CIPHER_MODE CipherMode;   /**<cipher mode for the key injection, e.g. ECB, CBC*/
    EM_SEC_PADDING PadingMode;   /**<Padding mode for the cipher, e.g. ECB, CBC, PadingMode is used only when key injection methods is "SEC_KIM_CIPHER" */
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
    uchar *psKsn;      /**<DUKPT KSN for key usage "KEY_USE_DUKPT" */
	int nADSize;       /**<Length of additional data*/
	uchar *pAD;        /**<Pointer to the additional data*/
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
    SEC_VPP_INVALID_SESSION          /**< For parameter testing purposes */
} EM_SEC_VPP_SESSION_TYPE;

/**
    @brief  Session key structure
    @details Used by functions that suport an encrypted     session key.
    @note The IV for CBC mode is all zeros which means no IV.
*/
typedef struct {
    uchar IV[16];
    int KeyLen;            /**< Length of decrypted plaintext session key. Since block size of AES is multiple of 128bits, while cihpertext of AES192 and AES256 is the same 256bits  */
    uchar KeyData[32];   /**< Encrypted session key, multiple of cipher block size. */
    unsigned mode;         /**< 0: CBC, 1: ECB encrypted */
} ST_SEC_SESSION_KEY;

#define MAX_RSA_MODULUS_BITS 2048
#define MAX_RSA_MODULUS_LENGTH  ((MAX_RSA_MODULUS_BITS + 7) / 8)
#define MAX_RSA_PRIME_BITS      ((MAX_RSA_MODULUS_BITS + 1) / 2)
#define MAX_RSA_PRIME_LENGTH    ((MAX_RSA_PRIME_BITS + 7) / 8)
#define MAX_RSA_MODULUS_LEN		512		/**<RSA Max Modulus Len*/

typedef struct {
    uint32_t bits;                          /*  length in bits of modulus */
    uint8_t modulus[MAX_RSA_MODULUS_LENGTH];   /*  modulus */
    uint8_t exponent[MAX_RSA_MODULUS_LENGTH];  /*  private exponent */
    uint8_t publicExponent[MAX_RSA_MODULUS_LENGTH];    /*  public exponent */
    uint8_t prime[2][MAX_RSA_PRIME_LENGTH];            /*  prime factors */
    uint8_t primeExponent[2][MAX_RSA_PRIME_LENGTH];    /*  exponents for CRT */
    uint8_t coefficient[MAX_RSA_PRIME_LENGTH];         /*  CRT coefficient */
}rsa_priv_key_t;

/**
 *@brief RSA Key Information
*/
typedef struct {
    uint usBits;                    			/**< RSA Key Length */
    uchar sModulus[MAX_RSA_MODULUS_LEN];  	/**< Modulus */
    uchar sExponent[MAX_RSA_MODULUS_LEN]; 	/**< EXponent */
}ST_NAPI_RSA_KEY;

/**
 *@brief Key Verification Mode
*/
typedef enum{
	NAPI_SEC_KCV_NONE=0,		/**<Without Verification*/
	NAPI_SEC_KCV_ZERO,		/**<Encrypt 8 bytes 0x00 with DES/TDES or encrypt 16 bytes 0x00 with SM4. Take the first 3 bytes as KCV value.*/
	NAPI_SEC_KCV_VAL,		/**<Verify plaintext with parity check method, then encrypt "\x12\x34x56\x78\x90\x12\x34\x56" with DES/TDES. Take the first 3 bytes as KCV value.(NOT SUPPORTED)*/
	NAPI_SEC_KCV_DATA,		/**<Caculate MAC of "aucDstKeyValue(ciphertext) + KcvData" in defined mode Take the 8 bytes MAC as KCV vlaue.(NOT SUPPORTED) */
}EM_NAPI_SEC_KCV;

/**
 * PinBlock Mode
 */
typedef enum {
	NAPI_SEC_PIN_ISO9564_0=3,    /**<Encrypt with main account. Complement password with 'F'*/
	NAPI_SEC_PIN_ISO9564_1=4,    /**<Encrypt without main account. Complement password with radom number*/
	NAPI_SEC_PIN_ISO9564_2=5,    /**<Encrypt without main account. Complement password with 'F'*/
	NAPI_SEC_PIN_ISO9564_3=6,    /**<Encrypt with main account. Complement password with radom number*/
    NAPI_SEC_PIN_ISO9564_4 = 12,
}NAPI_EM_SEC_PIN;

typedef enum{
	NAPI_SEC_VPP_KEY_PIN,
	NAPI_SEC_VPP_KEY_BACKSPACE,
	NAPI_SEC_VPP_KEY_CLEAR,
	NAPI_SEC_VPP_KEY_ENTER,
	NAPI_SEC_VPP_KEY_ESC,
	NAPI_SEC_VPP_KEY_NULL,
	NAPI_SEC_VPP_PIN_LESS_THAN_MIN_LEN,  /**< pin inputlen is too short*/
	NAPI_SEC_VPP_PIN_EXCEED_MAX_LEN,  /**< pin inputlen is too long*/
	NAPI_SEC_VPP_KEY_ADA_ON,					/** <ADA模式PIN输入开启 */
	NAPI_SEC_VPP_KEY_ADA_OFF,				/** <ADA模式PIN输入关闭 */
	NAPI_SEC_VPP_SLID_LEFT,            /** 滑动到键盘左边 10*/
	NAPI_SEC_VPP_SLID_RIGHT,           /** 滑动到键盘右边11*/
	NAPI_SEC_VPP_SLID_UP,              /** 滑动到键盘上边12*/
	NAPI_SEC_VPP_SLID_DOWN,            /** 滑动到键盘下边13*/
	NAPI_SEC_VPP_SLID_NUMKEY,          /** 滑动到数字键14*/
	NAPI_SEC_VPP_SLID_ENTER,           /** 滑动到确认键15*/
	NAPI_SEC_VPP_SLID_CANCLE,          /** 滑动到取消键16*/
	NAPI_SEC_VPP_SLID_BACKSPACE,       /** 滑动到退格键17*/
	NAPI_SEC_VPP_SLID_NODIGIT,         /** 滑动到无效键18*/
	NAPI_SEC_VPP_SLID_CLEAR,           /** 滑动到清空键19*/
	NAPI_EM_SEC_VPP_KEY_MAX = 65536
}NAPI_EM_SEC_VPP_KEY;
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

typedef enum{
	DUKPT_DERIVATE_NONE,                /**NONE */
	DUKPT_DERIVATE_KEK,                 /**< key Encryption Key */
	DUKPT_DERIVATE_PIN,                 /**< PIN Encryption */
	DUKPT_DERIVATE_MAC_GEN,             /**< Message Authentication,generation */
	DUKPT_DERIVATE_MAC_VERIFY,          /**< Message Authentication,verification */
	DUKPT_DERIVATE_MAC_BOTH,            /**< Message Authentication, both ways(When the key type is DES, it is consistent with DUKPT_DERIVATE_MAC_GEN) */
	DUKPT_DERIVATE_DATA_ENC,            /**< Data Encryption, encrypt */
	DUKPT_DERIVATE_DATA_DEC,            /**< Data Encryption, decrypt */
	DUKPT_DERIVATE_DATA_BOTH,           /**< Data Encryption, both ways (When the key type is DES, it is consistent with DUKPT_DERIVATE_DATA_ENC)*/
	DUKPT_DERIVATE_DERIVATEKEY,         /**< Key Derivation */
	DUKPT_DERIVATE_DERIVATEKEY_INITIAL, /**< Initial Key Derivation */
} EM_SEC_DUKPT_DERIVATE_USAGE;

typedef struct {
	EM_SEC_CRYPTO_KEY_TYPE KeyType; //指定派生密钥的算法
	EM_SEC_DUKPT_DERIVATE_USAGE DerivateUsage; //指定派生密钥
	int nKeyLen;//指定派生密钥的长度
}ST_SEC_DUKPT_DERIVATE_DATA;

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
    SEC_KEY_INFO_MAX
} EM_SEC_KEY_INFO_ID;

/**
 *@brief		Delete key.
 *@param[in] ucKeyID		 Key index, 1~250
 *@param[in] KeyType		 Key Type
 *@param[in] KeyUsage		 Key Usage

 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref EM_NAPI_ERR "EM_NAPI_ERR".
*/
extern int(*NAPI_SecDeleteKey)(uchar ucKeyID, EM_SEC_CRYPTO_KEY_TYPE KeyType, EM_SEC_KEY_USAGE KeyUsage);


extern int (*NAPI_SecKeyErase)();

/**
 *@brief         Encrypt Data using the algorithm and Key specified.
 *@param[in] pstDataIn       Pointer to the data for encryption (\ref ST_SEC_ENCRYPTION_DATA "ST_SEC_ENCRYPTION_DATA")
 *@param[out] psDataOut      Pointer to output data
 *@param[out] pnOutLen       Pointer to size of output data
 *@param[out] psKsnOut       Pointer to output KSN if it is a DUKPT encryption
 *@param[out] pnOutKsnLen    Pointer to the size of output KSN if it is a DUKPT encryption.
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref EM_NAPI_ERR "EM_NAPI_ERR". 
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
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref EM_NAPI_ERR "EM_NAPI_ERR". 
*/
extern int (*NAPI_SecDecryption)(ST_SEC_ENCRYPTION_DATA *pstDataIn, uchar *psDataOut, int *pnOutLen, uchar *psKsnOut, int *pnOutKsnLen);

/**
*@brief Key Injection
 *@details Generic key injection for all key types, with different algorithm...
 *@param[in] Method             Key generation method,ref EM_SEC_KEYIN_METHOD "EM_SEC_KEYIN_METHOD"
 *@param[in] pstKGData          All needed data for key injection, ref ST_SEC_KEYIN_DATA "ST_SEC_KEYIN_DATA"
 *@param[in] pstKcvData         Key check value (KCV) for the injected key, Optional
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref EM_NAPI_ERR "EM_NAPI_ERR". 
*/
extern int (*NAPI_SecGenerateKey)( EM_SEC_KEYIN_METHOD Method, ST_SEC_KEYIN_DATA *pstKGData, ST_SEC_KCV_DATA *pstKcvData);

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
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref EM_NAPI_ERR "EM_NAPI_ERR". 
*/
extern int (*NAPI_SecGenerateMAC)(EM_SEC_MAC_TYPE MacType, uchar ucKeyID, uchar *psIV, int unIVSize, uchar *psDataIn, int nDataInLen, uchar *pAD, int unADSize,
                       uchar *psMacOut, int *pnOutLen, uchar *psKsnOut, int *nOutKsnLen);

/**
*@brief 	   Returns key information such as KCV, length, etc.
*@param[in] ucKeyID 		Key index, 1~250
*@param[in] KeyType 		Key Type
*@param[in] KeyUsage		Key Usage
*@param[in] pAD 			Additional data for key information.
*@param[in] unADSize		Size of Additional data.

*@param[out] psOutInfo		 Pointer to the output buffer
*@param[out] pnOutInfoLen	 Pointer to the output length
*@return
 On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref EM_NAPI_ERR "EM_NAPI_ERR".
*/
extern int (*NAPI_SecGetKeyInfo)(EM_SEC_KEY_INFO_ID InfoID, uchar ucKeyID, EM_SEC_CRYPTO_KEY_TYPE KeyType, EM_SEC_KEY_USAGE KeyUsage,
					  uchar *pAD, uint unADSize, uchar *psOutInfo, int *pnOutInfoLen);

  /**
   *@brief		  Initialises the Virtual (internal) PIN pad. Start the PIN entry mode.
   *@param[in] SessionType	  For SessionType "SEC_VPP_MASTER_SESSION", pAD will be an encrypted session key, see ST_SEC_SESSION_KEY.
   *@param[in] CipherID 	  PIN Key Algorithm: TDES or AES
   *@param[in] ucKeyIdx 	  PIN Key index, 1~250.
   *@param[in] pPAN 		  Primary Account Number, NULL terminated character string.
   *@param[in] PINBlockFmt	  PIN BLOCK per ISO9564, format 0~4.
   *@param[in] unTimeOut	  Timeout value (seconds), 5-200.
   *@param[in] pRSAKey		  RSA public key for the offline ciphertext PIN encryption.
   *@param[in] pAD			  Additional data, for Master Session this is packed encrypted session key, given by the structure ST_SEC_SESSION_KEY.
   *@param[in] unADSize 	  Size of Additional Data.
   *@return
	On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref EM_NAPI_ERR "EM_NAPI_ERR". 
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
 *@brief		Process and get PIN entry event
 *@param[out]	nEvent		  PIN entry event, see EM_SEC_VPP_KEY 
 *@param[out]	psPinBlock	  Ciphertext pinblock if the user finish PIN entry and press Enter key.
							  During the PIN entry, the first byte of psPinBlock[0] indictaes length of current PIN digits.
 *@param[out]	pnOutPinLen   Pointer to size of output pinblock.
 *@param[out]	psKsn		  Pointer to the output KSN for current PIN encryption if the "SessionType" is DUKPT.
 *@param[out]	pnOutKsnLen   Pointer to size of output KSN if the "SessionType" is DUKPT.
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref EM_NAPI_ERR "EM_NAPI_ERR". 
*/
extern int (*NAPI_SecVPPGetEvent)(int *nEvent, uchar *psPinBlock, int *pnOutPinLen, uchar *psKsn, int *pnOutKsnLen);

/**
 @brief Simulated key code to externally influence PIN entry procedure.
 *@param[in] key    The simulated key may be set externally during PIN entry: 
                    KEY_CANCEL - simulates pressing CANCEL key
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref EM_NAPI_ERR "EM_NAPI_ERR". 
*/
extern int (*NAPI_SecVPPSetEvent)(uint key);

extern int (*NAPI_SecGetKeyOwner)(int nLenOfOwnerBuffer,char *pszOwner);

/**
 @brief Set the Lenth of passward during PIN entry.
 *@param[in] key    the Lenth of passward:
                    like:0,4,6
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
extern int (*NAPI_SecVPPSetExpPinLenIn)(char *pszExpPinLenIn);

extern int (*NAPI_SecGetRandom)(int nRandLen, void *pvRandom);

extern int (*NAPI_SecResetCertStatus)(void);
extern int (*NAPI_SecLoadTrustedCert)(char isCA, char * cert, int certlen, char * pubkey, int * pubkeylen);

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
/**
 *@brief        Initialize the touch screen keyboard.
 *@param[in]    keyInfo        key information(Value of key-press，Button area）
 *@param[in]    keyNum         the number of key
 *@param[in]    tsArea         touch screen area
 *@param[in]    keypadArea     keypad area
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
extern int (*NAPI_SecVppRNIBTpInit)(vpp_key *keyInfo, uint32_t keyNum, vpp_button *tsArea, vpp_button *keypadArea);

extern int getSupportNapi();
extern int getUseNapi();
#endif

