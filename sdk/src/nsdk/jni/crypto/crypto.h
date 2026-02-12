/* Security Module */
#ifndef FORTH_CRYPTO_H
#define FORTH_CRYPTO_H

#include <stdint.h>
#include "comm.h"
#include "napi.h"
#include "ndk.h"

/**
        @brief  Key Type.
*/
typedef enum {
    KEY_TYPE_DES,
    KEY_TYPE_AES,
    KEY_TYPE_SM4,
	KEY_TYPE_HMAC = 3,
	KEY_TYPE_MAX,
	KEY_TYPE_ASYM_RSA = 0x20,
	KEY_TYPE_ASYM_ECC,
	KEY_TYPE_ASYM_SM2,
	KEY_TYPE_ASYM_MAX
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
	SEC_CIPHER_AES_DUKPT_ECB,  /**< AES encryption in ECB mode, with AES DUKPT key variant for Data Encryption */
	SEC_CIPHER_AES_DUKPT_CBC,  /**< AES  encryption in CBC mode, with AES DUKPT key variant for Data Encryption*/
	/* SM4 Algorithm*/
	SEC_CIPHER_SM4_ECB=24,        /**< ECB mode without specifying the key length, firmware will use appropriate algorithm according to the key length */
    SEC_CIPHER_SM4_CBC,        /**< CBC mode without specifying the key length, firmware will use appropriate algorithm according to the key length */
	SEC_CIPHER_DES_CTR = 28,
	SEC_CIPHER_AES_CTR,
	SEC_CIPHER_TYPE_MAX
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
	SEC_MAC_AES_DUKPT_UNIONPAY_ECB,  /**< MAC using a DUKPT key  with TDES on the first 4 bytes of last block. In accordance to the UnionPay requirement*/

	/* SM4 MAC*/
	SEC_MAC_SM4_LAST=18,            /**< MAC Digital signature with SM4 on last block only. 9606 */
    SEC_MAC_SM4_X99,             /**< MAC Digital signature with SM4 on all blocks. X99 */
    SEC_MAC_SM4_UNIONPAY_ECB,
	SEC_MAC_HMAC_SHA1 = 21,
	SEC_MAC_HMAC_SHA256,
	SEC_MAC_TDES_CMAC = 30,
	SEC_MAC_DUKPT_CMAC,
	SEC_MAC_AES_CMAC,
	SEC_MAC_AES_DUKPT_CMAC,
	SEC_MAC_MAX
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
	/* Asym Auth Key*/
	KEY_USE_ASYM_AUTH = 0x20,
	/* Asym Data Key*/
	KEY_USE_ASYM_DATA,
	/* Asym Key Use for AUTH&ENC */
	KEY_USE_ASYM_ANY,
	/* Asym Key Use for KEY DISTRIBUTION */
	KEY_USE_ASYM_KEY_DISTRIBUTION,
} EM_SEC_KEY_USAGE;

typedef enum {
	SEC_MD_NONE=0,    /**< None. (not support yet)*/
	SEC_MD_SHA1,      /**< The SHA-1 message digest. */
	SEC_MD_SHA224,    /**< The SHA-224 message digest. */
	SEC_MD_SHA256,    /**< The SHA-256 message digest. */
	SEC_MD_SHA384,    /**< The SHA-384 message digest. */
	SEC_MD_SHA512,    /**< The SHA-512 message digest. */
	SEC_MD_SM3,       /**(not support yet)*/
} EM_SEC_MD_TYPE;

typedef enum {
	ASYM_RSA_NO_ENCODING = 0, /**(not support yet)*/
	ASYM_RSA_PKCS_V15 = 1, /**< Use PKCS#1 v1.5 encoding. */
	ASYM_RSA_PKCS_V21 = 2, /**< Use PKCS#1 v2.1 encoding. */
	ASYM_ECC_ASN1 = 0x10,
} EM_SEC_ASYM_ENCODING_MODE;

typedef enum {
	ASYM_RSA_DEFAULT = 0,
	ASYM_RSA_PUBLIC,
	ASYM_RSA_PRIVATE,
}EM_SEC_ASYM_CRYPTO_MODE;

/**
*brief Data Formats
*/
typedef enum {
	TR34_BLOCK_ENCODING_ASN1 = 0,
	TR34_BLOCK_ENCODING_RAW1 = 1,
	TR34_BLOCK_ENCODING_XML = 2,
	TR34_BLOCK_ENCODING_RAW2 = 3,
	TR34_BLOCK_ENCODING_RAW3 = 4,
} EM_SEC_TR34_ENCODING_MODE;

typedef enum {
	P_224,
	P_256,
	P_384,
	P_521,
	ECC_TYPE_MAX,
} ECC_TYPE;

typedef struct {
	uint16_t x;  /* the point's X coordination */
	uint16_t y;  /* the point;s Y coordination */
}vpp_point;

typedef struct {
	vpp_point l_top;  /* left top point */
	vpp_point r_bottom;  /* right bottom point */
}vpp_button;

typedef struct {
	uint32_t key;  /* Value of key-press, e.g., '1', '2', ENTER. */
	vpp_button btn;  /* Button area */
}vpp_key;

typedef struct {
    uint32_t swipeDistance;
    uint32_t clickInterval;
    uint32_t pressTime;
    uint8_t clickMode;
    uint8_t effectMode;
    uint8_t isRandomKeypad;
    uint8_t rev[6];
}vppAAConfig_st;

/*
* 触屏事件类型定义
*/
typedef enum {
    VPP_EVENT_NONE,              //无事件
    VPP_EVENT_SWIPE_LEFT,        //左滑事件
    VPP_EVENT_SWIPE_RIGHT,       //右滑事件
    VPP_EVENT_SWIPE_UP,          //上滑事件
    VPP_EVENT_SWIPE_DOWN,        //下滑事件
    VPP_EVENT_CLICK,             //点击事件
    VPP_EVENT_DOUBLE_CLICK,      //双击事件
    VPP_EVENT_TRIPLE_CLICK,      //三击事件
    VPP_EVENT_LONG_PRESS,        //长按事件
    VPP_EVENT_EVENT_MAX
} vppEvent_em;

/*
* 触屏事件状态定义
*/
typedef enum {
    VPP_TOUCH_NONE,                // 无触点
    VPP_TOUCH_ABOVE_KEYPAD,        // 触点位于键盘上方区域（键盘在触点下方）
    VPP_TOUCH_BELOW_KEYPAD,        // 触点位于键盘下方区域（键盘在触点上方）
    VPP_TOUCH_LEFT_OF_KEYPAD,      // 触点位于键盘左侧区域（键盘在触点右侧）
    VPP_TOUCH_RIGHT_OF_KEYPAD,     // 触点位于键盘右侧区域（键盘在触点左侧）
    VPP_TOUCH_ON_DIGIT_KEY,        // 触点落在键盘的数字键上（0-9，不区分具体键值）
    VPP_TOUCH_ON_ENTER_KEY,        // 触点落在键盘的确认键上
    VPP_TOUCH_ON_BACKSPACE_KEY,    // 触点落在键盘的退格键上
    VPP_TOUCH_ON_CANCEL_KEY,        // 触点落在键盘的取消键上
    VPP_TOUCH_ON_CLEAR_KEY,
    VPP_TOUCH_ON_SPACEKEY,
    VPP_TOUCH_ON_SWITCHKEY
} vppTouchState_em;

/*
* 触屏事件处理行为定义
*/
typedef enum {
    VPP_ACTION_IGNORE,     //忽略该事件，不处理也不返回事件给应用
    VPP_ACTION_NONE,      //仅返回事件，不做处理
    VPP_ACTION_ESC,        //退出输PIN流程，返回对应的事件给应用
    VPP_ACTION_ENTER,      //确认当前已输入所有键值，退出输PIN流程并返回对应事件给应用
    VPP_ACTION_CANCEL,     //取消，已输入数字时清除掉已输入的所有数字，并返回事件给应用；未输入数字时取消PIN输入流程，结束输PIN流程，并返回事件给应用
    VPP_ACTION_BACKSPACE,  //退格，删除最后输入的数字键并返回对应事件给应用
    VPP_ACTION_CLEAR,      //清空，当前已输入的键值并返回对应事件给应用
    VPP_ACTION_INPUT,      //输入当前已选中的键值并返回对应事件给应用
    VPP_ACTION_SELECT,      //选中坐标对应的键值，按下生效时选中按下坐标对应的键值，抬起生效选中抬起时坐标对应的键值
    VPP_ACTION_MAX
} vppAction_em;

/*
* 事件类型与处理行为
*/
typedef struct {
    vppEvent_em event;        //事件类型
    vppAction_em action;      //事件类型绑定的处理行为
} vppEventActionMap_st;

/*
* 设置事件处理行为的方式
*/
typedef enum {
    VPP_MAP_RESET,            //重置，清空已有设置
    VPP_MAP_SETDEFAULT,       //设置为默认配置，普通输PIN模式
    VPP_MAP_ADD,              //追加
    VPP_MAP_MODE_MAX
} vppMapSetMode_em;


typedef enum{
	SEC_VPP_BUTTON_USAGE_QUIT,
	SEC_VPP_BUTTON_USAGE_CLEAR,
}EM_SEC_VPP_BUTTON_FUNC;

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
*brief Tr34 Key Block structure
*details Used by function NAPI_SecTR34ProcessKeyBlock .\n
*/
typedef struct {
	uint8_t                       *keyBlock;        ///< Pointer to the TR34 key block data in X9.73 CMS format.
	uint32_t                      keyBlockLen;   ///< Length of the TR34 key block data.
	EM_SEC_TR34_ENCODING_MODE     keyBlockMode;
} ST_SEC_TR34_BLOCK_PARAMS;

/**
*@brief Crypto information
*/
typedef struct {
	uchar asymKeyIdx;
	uchar knIdx;
	EM_SEC_CRYPTO_KEY_TYPE keyType;
	EM_SEC_KEY_USAGE       keyUsage;
} ST_SEC_TR34_KEY_INFO;

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
	NAPI_SEC_PIN_SM4_1,
	NAPI_SEC_PIN_SM4_2,
	NAPI_SEC_PIN_SM4_3,
	NAPI_SEC_PIN_SM4_4,
	NAPI_SEC_PIN_SM4_5,
	NAPI_SEC_PIN_ISO9564_4 = 12,
}NAPI_EM_SEC_PIN;

/**
 *@brief VPP Service Return Value
*/
typedef enum{
    NAPI_SEC_VPP_KEY_PIN,					/**< PIN button down. Application should print '*'*/
    NAPI_SEC_VPP_KEY_BACKSPACE,				/**< Backspace button down*/
    NAPI_SEC_VPP_KEY_CLEAR,					/**< Clear button down*/
    NAPI_SEC_VPP_KEY_ENTER,					/**< Enter button down*/
    NAPI_SEC_VPP_KEY_ESC,					/**< Cancel pin input*/
    NAPI_SEC_VPP_KEY_NULL,					/**< NULL event*/
	NAPI_SEC_VPP_PIN_LESS_THAN_MIN_LEN,  /**< pin inputlen is too short*/
	NAPI_SEC_VPP_PIN_EXCEED_MAX_LEN,  /**< pin inputlen is too long*/
	NAPI_SEC_VPP_KEY_ADA_ON,					/** <ADA模式PIN输入开启 */
	NAPI_SEC_VPP_KEY_ADA_OFF,				/** <ADA模式PIN输入关闭 */
	NAPI_SEC_VPP_SLID_LEFT,                 /** 滑动到键盘左边*/
	NAPI_SEC_VPP_SLID_RIGHT,           		/** 滑动到键盘右边*/
	NAPI_SEC_VPP_SLID_UP,              		/** 滑动到键盘上边*/
	NAPI_SEC_VPP_SLID_DOWN,            		/** 滑动到键盘下边*/
	NAPI_SEC_VPP_SLID_NUMKEY,          		/** 滑动到数字键*/
	NAPI_SEC_VPP_SLID_ENTER,           		/** 滑动到确认键*/
	NAPI_SEC_VPP_SLID_CANCLE,          		/** 滑动到取消键*/
	NAPI_SEC_VPP_SLID_BACKSPACE,       		/** 滑动到退格键*/
	NAPI_EM_SEC_VPP_KEY_MAX
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

typedef enum {
	DUKPT_DERIVATE_NONE,            /**< NONE */
	DUKPT_DERIVATE_KEK,             /**< key Encryption Key */
	DUKPT_DERIVATE_PIN,             /**< PIN Encryption */
	DUKPT_DERIVATE_MAC,             /**< Message Authentication,generation */
    DUKPT_DERIVATE_MAC_VERIFY,      /**< Message Authentication,verification*/
	DUKPT_DERIVATE_MAC_BOTH,        /**< Message Authentication, both ways(When the key type is DES, it is consistent with DUKPT_DERIVATE_MAC) */
	DUKPT_DERIVATE_DATA_ENC,        /**< Data Encryption, encrypt */
	DUKPT_DERIVATE_DATA_DEC,        /**< Data Encryption, decrypt */
	DUKPT_DERIVATE_ENC_BOTH,        /**< Data Encryption, both ways (When the key type is DES, it is consistent with DUKPT_DERIVATE_DATA_ENC)*/
	DUKPT_DERIVATE_DERIVATEKEY,     /**< Key Derivation */
	DUKPT_DERIVATE_DERIVATEKEY_INITIAL, /**< Initial Key Derivation */
} EM_SEC_DUKPT_DERIVATE_USAGE;

typedef struct {
	EM_SEC_CRYPTO_KEY_TYPE KeyType; //指定派生密钥的算法
	EM_SEC_DUKPT_DERIVATE_USAGE DerivateUsage; //指定派生密钥
	int nKeyLen;//指定派生密钥的长度
}ST_SEC_DUKPT_DERIVATE_DATA;

typedef struct{
	EM_SEC_CRYPTO_KEY_TYPE KeytType;
	EM_SEC_KEY_USAGE KeyUsage;
	uchar KeyIdx;
}ST_SEC_ASYM_KEY_INFO;

#define ST_SEC_KEYINFO ST_SEC_ASYM_KEY_INFO

typedef struct{
	uint unBit;
	uchar ucRsaPubExp[5];
}ST_SEC_ASYM_ALG_INFO;

typedef struct {
    int authTagLen;
    uchar *authTag;
    int adAuthDataLen;
    uchar *adAuthData;
}ST_SEC_GCM_APPEND_DATA;

typedef struct {
    EM_SEC_CRYPTO_KEY_TYPE KeyType;
    EM_SEC_DUKPT_DERIVATE_USAGE DerivateUsage;
    int nKeyLen;
    int authTagLen;
    uchar *authTag;
    int adAuthDataLen;
    uchar *adAuthData;
}ST_SEC_GCM_AES_DUKPT_APPEND_DATA;
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
} EM_SEC_KEY_INFO_ID;

typedef struct {
    EM_SEC_KEY_USAGE KeyUsage;
    uint             unPinBlockLen;
    uchar            psPinBlock[32];
    uint             unTSKLen;
    uchar            psTSK[32];
}ST_SEC_VERIFY_PIN_AD;

typedef unsigned long ECDHE_HANDLE;
typedef void* CSR_HANDLE;

/**
 *@brief KDF Information
*/
typedef enum
{
	SEC_ECDHE_HKDF = 0,
	SEC_ECDHE_HKDF_GOOGLE_SMART_TAP,
	SEC_ECDHE_MAX  = 65536,
} EM_SEC_ECDHE_KDF_TYPE;

/**
 *@brief ECDHE Key Information
*/
typedef struct {
	uchar ucKeyID;                   /**<Key index, 1~255 */
	EM_SEC_CRYPTO_KEY_TYPE KeyType;  /**<key type of the key. e.g. TDES or AES*/
	EM_SEC_KEY_USAGE KeyUsage;       /**<Usage of the key. (\ref EM_SEC_KEY_USAGE "EM_SEC_KEY_USAGE")*/
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


static const int COMMAND_PEDI = 0;
static const int COMMAND_PEDK = 1;
static const int COMMAND_PEDV = 2;

/**
 *@brief		Delete key.
 *@param[in] ucKeyID		 Key index, 1~250
 *@param[in] KeyType		 Key Type
 *@param[in] KeyUsage		 Key Usage

 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref EM_NAPI_ERR "EM_NAPI_ERR".
*/
extern int(*NAPI_SecDeleteKey)(uchar ucKeyID, EM_SEC_CRYPTO_KEY_TYPE KeyType, EM_SEC_KEY_USAGE KeyUsage);

extern int(*NAPI_SecSymmKeyErase)();

extern int (*NAPI_SecKeyErase)();

extern int (*NAPI_SecTR34GenerateRandom)(uint32_t unTokenSize, uchar *psTokenData);

extern int (*NAPI_SecTR34ProcessKeyBlock)(ST_SEC_TR34_BLOCK_PARAMS *TR34Params, ST_SEC_TR34_KEY_INFO * cryptoKeyInfo, uint8_t *adData, uint32_t adDataLen);

extern int (*NAPI_SecGeneratePubkeyCert)(ST_SEC_ASYM_KEY_INFO *pstKeyinfo, ST_SEC_ASYM_KEY_INFO *pstCAinfo);

extern int (*NAPI_SecGetSymmKeyNum)(int *pnTotalKeyNum, ST_SEC_KEYNUM_INFO *pstKeyNumInfoArray, int* pnArrayCount);

extern int (*NAPI_SecGetSymmKeyInfoByID)(uchar ucKeyID, ST_SEC_SYMM_KEYID_INFO *pstKeyInfoArray, int* pnArrayCount);

extern int (*NAPI_SecVPPAAInit)(vpp_key *keyInfo, uint32_t keyNum, vppAAConfig_st *config, vpp_button *tsArea, vpp_button *keypadArea, void *pad, uint32_t adSize);

extern int (*NAPI_SecVPPAASetMap)(vppEventActionMap_st *mapList, uint32_t count, uint8_t mode);

extern int (*NAPI_SecVPPAAGetPin)(uint32_t *vppkey, uint32_t *vppEvent, uint32_t *state, uint8_t *pinblock, uint32_t *outPinLen, uint8_t *ksn, uint32_t *ksnLen);

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
*@brief Symmetric Key Injection  with the Asymmetric key
 *@details Inject symmetric key with the asymmetric key for all key types, with different algorithm...
 *@param[in] Method             Key generation method,ref EM_SEC_KEYIN_METHOD "EM_SEC_KEYIN_METHOD"
 *@param[in] pstKGData          All needed data for key injection, ref ST_SEC_ASYM_KEYIN_DATA "ST_SEC_ASYM_KEYIN_DATA"
 *@param[in] pstKcvData         Key check value (KCV) for the injected key, Optional
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref EM_NAPI_ERR "EM_NAPI_ERR".
*/
extern int (*NAPI_SecAsymGenerateKey)( EM_SEC_KEYIN_METHOD Method, ST_SEC_ASYM_KEYIN_DATA *pstKGData, ST_SEC_KCV_DATA *pstKcvData);

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
 * @brief     Initialize the touch screen keyboard.
 * @param[in] keyInfo       Key information(Value of key-press, Button area).
 * @param[in] keyNum        the number of key.
 * @param[in] tsArea        Touch screen area.
 * @param[in] keypadArea    Keypad area.
 * @return
   On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
 */
extern int (*NAPI_SecVppRNIBTpInit)(vpp_key *keyInfo, uint32_t keyNum, vpp_button *tsArea, vpp_button *keypadArea);

extern int (*NAPI_SecVPPSetButtonFunc)(int button, EM_SEC_VPP_BUTTON_FUNC func);


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
 *@brief	设置密钥属主应用名称
 *@details 	仅供系统应用(Keyloader)使用，通过该接口指定后续安装密钥的属主名称。
 *			当安装密钥的时候，系统安全服务将会判断调用者身份，再决定是否采用该函数设置的密钥属主名称：
 *			-针对普通用户程序：
 *				该设置无效，系统安全服务会直接指定安装密钥的属主为当前用户程序
 *			-针对系统应用程序：
 *				判断若是Keyloader系统程序，则安全服务采用\ref NDK_SecSetKeyOwner "NDK_SecSetKeyOwner()"设置的应用名为当前安装密钥的属主，
 *					如果Keyloader未设置过密钥属主，则默认密钥属主指定为Keyloader本身
 *				若非Keyloader系统程序，则直接以当前系统应用为密钥属主
 *@param	pszName			密钥属主应用名称(长度小于256)，若传递的是空字符串，则会清空之前设置的密钥属主
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		参数非法(pszName为NULL或者应用名称长度大于等于256)
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败
*/
extern int (*NAPI_SecSetKeyOwner)(char *pszName);

/**
 *@brief	初始化PIN输入的触摸屏键盘，输入10个数字键盘的按钮坐标，以及3个功能键(退格／取消／确认)的按键信息。输出返回随机排列后的数字按键值
 *@param	num_btn			10个数字按钮(vpp_button)，每个按钮占8字节，总大小为10*8=80字节。每个按钮由“左上”“右下”两个点组成，一个点由"x""y"2个uint16_t型坐标组成(uint16_t x, uint16_t y)
 *@param					例如第一个按钮为0点开始的16像素(0x10)正方形，那么num_btn[0] = ((0x0000,0x0000),(0x0010, 0x0010))
 *@param	func_key		3个功能按键(退格、取消、确认键，vpp_key[3])，总大小为3*(4+8)=36字节，每个按键组成结构为键值+按钮(int key, vpp_button)，key为4字节int型，取值为K_BASP/K_ENTER/K_ESC三者之一
 *@retval	out_seq			输出存储10字节随机排列的按键值('0'-'9')，与输入参数num_btn[10]一一对应，可用于键盘显示。
 *@return
 *@li	NDK_OK				操作成功
 *@li	\ref NDK_ERR "NDK_ERR" 			操作失败
*/
extern int (*NAPI_SecVppTpInit)(uchar *num_btn, uchar *func_key, uchar *out_seq);

/**
 @brief Set the Lenth of passward during PIN entry.
 *@param[in] key    the Lenth of passward:
                    like:0,4,6
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
extern int (*NAPI_SecVPPSetExpPinLenIn)(char *pszExpPinLenIn);

extern int (*NAPI_SecGetRandom)(int nRandLen, void *pvRandom);

extern int (*NAPI_SecLoadTrustedCert)(char isCA, char * cert, int certlen, char * pubkey, int * pubkeylen);

extern int (*NAPI_SecResetCertStatus)(void);

extern int (*NAPI_SecInitAtomic)(void);

extern int (*NAPI_SecCommitAtomic)(char status);

/**
 *@brief	Encrypt data using the specified algorithm and the specified asymmetric key
 *@param[in] pstDataIn 	    Pointer to the key information used for encryption (\ref ST_SEC_ASYM_KEY_INFO "ST_SEC_ASYM_KEY_INFO")
 *@param[in] MdAlg			Hash algorithm mode
 *@param[in] PaddingMode    Data pading mode
 *@param[in] CryptoMode     Data encryption mode
 *@param[in] nDataInLen     Length of data to be encrypted
 *@param[in] psDataIn	    Data to be encrypted
 *@param[out] pnDataOutLen	Data length after encryption
 *@param[out] psDataOut		Point to the data after encryption
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
extern int (*NAPI_SecAsymEncryption)(ST_SEC_ASYM_KEY_INFO *pstKeyinfo, EM_SEC_MD_TYPE MdAlg, EM_SEC_ASYM_ENCODING_MODE PaddingMode, EM_SEC_ASYM_CRYPTO_MODE CryptoMode, int nDataInLen, const unsigned char *psDataIn,  int* pnDataOutLen, unsigned char *psDataOut);

/**
 *@brief	Decrypt data using the specified algorithm and the specified asymmetric key
 *@param[in] pstDataIn 	    Pointer to the key information used for decryption (\ref ST_SEC_ASYM_KEY_INFO "ST_SEC_ASYM_KEY_INFO")
 *@param[in] MdAlg			Hash algorithm mode
 *@param[in] PaddingMode    Data pading mode
 *@param[in] CryptoMode     Data encryption mode
 *@param[in] nDataInLen     Length of data to be decrypted
 *@param[in] psDataIn	    Data to be decrypted
 *@param[out] pnDataOutLen	Data length after decryption
 *@param[out]psDataOut		Point to the data after decryption
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
extern int (*NAPI_SecAsymDecryption)(ST_SEC_ASYM_KEY_INFO *pstKeyinfo, EM_SEC_MD_TYPE MdAlg, EM_SEC_ASYM_ENCODING_MODE PaddingMode, EM_SEC_ASYM_CRYPTO_MODE CryptoMode,int nDataInLen, const unsigned char *psDataIn,  int* pnDataOutLen, unsigned char *psDataOut);

/**
 *@brief	Get data signature
 *@param[in] pstDataIn 	    Pointer to the key information used for signature (\ref ST_SEC_ASYM_KEY_INFO "ST_SEC_ASYM_KEY_INFO")
 *@param[in] MdAlg  		Hash algorithm mode
 *@param[in] PaddingMode    Data pading mode
 *@param[in] nHashLen       The length of the hash data to be signed
 *@param[in] psHash	  		Hash data to be signed
 *@param[out] nSigLen		Signature data length
 *@param[out] psSig			Signature data
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
extern int (*NAPI_SecAsymSign)(ST_SEC_ASYM_KEY_INFO *pstKeyinfo, EM_SEC_MD_TYPE MdAlg, EM_SEC_ASYM_ENCODING_MODE PaddingMode, int nHashLen, const unsigned char *psHash,  int* nSigLen, const unsigned char *psSig);

/**
 *@brief	Verify that the signature is correct
 *@param[in] pstDataIn 	    Pointer to the key information used for signature (\ref ST_SEC_ASYM_KEY_INFO "ST_SEC_ASYM_KEY_INFO")
 *@param[in] MdAlg  		Hash algorithm mode
 *@param[in] PaddingMode    Data pading mode
 *@param[in] nHashLen       The length of the hash data to be signed
 *@param[in] psHash	  		Hash data to be signed
 *@param[in] nSigLen		Signature data length
 *@param[in] psSig			Signature data
 *@return
  On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
extern int (*NAPI_SecAsymVerify)(ST_SEC_ASYM_KEY_INFO *pstKeyinfo,  EM_SEC_MD_TYPE MdAlg, EM_SEC_ASYM_ENCODING_MODE PaddingMode, int nHashLen, const unsigned char *psHash,  int nSigLen, const unsigned char *psSig);

/**
  *@brief	  ask for a random generated asymmetric key
  *@param[out] handle	   	the handle of generating process
  *@param[in] pstKeyinfo	pointer to the key information (\ref ST_SEC_ASYM_KEY_INFO "ST_SEC_ASYM_KEY_INFO")
  *@param[in] nADSize		size of pAD buffer
  *@param[in] pAD			pointer to the algorithm information(\ref ST_SEC_ASYM_ALG_INFO "ST_SEC_ASYM_ALG_INFO")
  *@return
   On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
 */

//CSR
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
 *@brief	 set the KeyUsage of csr process(Not Supported on Android)
 *@param[in] handle 	        the handle of csr process
 *@param[in] ucKeyUsage		    KeyUsage
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
*@brief	 set the Extension information of csr process(Not Supported on Android)
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



extern int (*NAPI_SecGenerateAsymKey)(NAPI_HANDLE* handle, ST_SEC_ASYM_KEY_INFO *pstKeyinfo, int nADSize, uchar *pAD);
extern int (*NAPI_SecGenerateAsymKeyState)(NAPI_HANDLE handle);
extern int (*NAPI_SecCancelGenerateAsymKey)(NAPI_HANDLE handle);

extern int (*NAPI_SecECDHEInit)(ECDHE_HANDLE* handle);
extern int (*NAPI_SecECDHERelease)(ECDHE_HANDLE handle);
extern int (*NAPI_SecECDHEGenKeyPair)(ECDHE_HANDLE handle, ECC_TYPE CurveType, int *pnPubKeyLen, uchar *psPubKey);
extern int (*NAPI_SecECDHEGenSK)(ECDHE_HANDLE handle, ST_SEC_ECDHE_KEY_INFO *pstSessionKeyInfo, ST_SEC_ECDHE_KDF_INFO *pstKDFInfo, int nServPubKeyLen, uchar *pServPubKey);

typedef struct {
	uchar keyIndex;
	EM_SEC_CRYPTO_KEY_TYPE keyType;
	EM_SEC_KEY_USAGE       keyUsage;
	uint pinBlockFmt;
	uint  panLen;
	uchar pan[32];
	uint adSize;
	void *ad;
} ST_SEC_PINBLOCK_INFO;

typedef struct {
	uint8_t     type;    //pinblock转化类型
	uint32_t    datalen;  //附加数据长度
	union {
		ST_NAPI_RSA_KEY RsaKey;     //脱机密文pin传递RSA公钥，
		uint8_t       data[512];    //附加数据，css时传递sn信息
	};
} ST_SEC_PINCONVERT_INFO;


/**
*@brief
*@param[in] pstOriPINInfo Pointer to the origin PIN info (\ref ST_SEC_PINBLOCK_INFO "ST_SEC_PINBLOCK_INFO")
*@param[in] pstDstPINInfo Pointer to the destination PIN info (\ref ST_SEC_PINBLOCK_INFO "ST_SEC_PINBLOCK_INFO")
*@param[in] nOriPINBlockLen the length of origin PINBlock.
*@param[in] psOriPINBlock Pointer to the origin PINBlock.
*@param[out] pnDstPINBlockLen Pointer to the length of destination PINBlock.
*@param[out] psDstPINBlock Pointer to the destination PINBlock.
*@return
On success, it returns \ref NAPI_OK "NAPI_OK"; on error, it returns \ref NAPI_ERR_CODE "NAPI_ERR_CODE".
*/
extern int (*NAPI_SecPINBlockConvert)(ST_SEC_PINBLOCK_INFO *pstOriPINInfo, int oriPinBlockLen, uchar* oriPinBlock, ST_SEC_PINBLOCK_INFO *pstDstPINInfo, int* outPinBlockLen, uchar* outPinBlock);


/**
 *@brief key export mode
*/
typedef enum {
	EXPORT_ANSI_X9143,            /**<通过ANSI X9.143方式导出*/
	EXPORT_MODE_MAX
} EM_SEC_KEYEXPORT_MODE;

extern int (*NAPI_SecKeyExport)(EM_SEC_KEYEXPORT_MODE mode, ST_SEC_KEYINFO *kek, ST_SEC_KEYINFO *key, uint8_t *outdata, uint32_t *outlen, void *pad, uint32_t adSize);

typedef struct {
    uint8_t *prefix;                            //前缀
    uint32_t prefixLen;                         //前缀长度
    uint8_t *suffix;                            //后缀
    uint32_t suffixLen;                         //后缀长度
    EM_SEC_MD_TYPE MdAlg;                       //HASH算法
    EM_SEC_ASYM_ENCODING_MODE EncodingMode;     //加密数据填充方式
    uint8_t rev[4];                             //预留数据
} ST_SEC_CRYPTOINFO;

extern int (*NAPI_SecCreateCryptogram)(ST_SEC_ASYM_KEY_INFO *cryptoKey, ST_SEC_CRYPTOINFO * cryptoInfo, ST_SEC_KEYINFO *sessionKey, ST_SEC_KEYINFO *componentSecret, uint8_t *outdata, uint32_t *outlen, uint8_t *pad, uint32_t adSize);


/**
 * @brief Key injection mode
 */
typedef enum {
	VERIFY_SIGNATURE,              /**< Install public key by verifying signature */
	VERIFY_MAC,                    /**< Install public key by verifying MAC */
} EM_SEC_INJECTKEY_MODE;

typedef struct {
	uint8_t keyIndex;                  /**< Key index for MAC calculation */
	EM_SEC_CRYPTO_KEY_TYPE keyType;    /**< Key type for MAC calculation */
	EM_SEC_KEY_USAGE keyUsage;         /**< Key usage for MAC calculation */
	EM_SEC_MAC_TYPE macMode;           /**< MAC calculation mode */
	uint32_t        ivLen;             /**< 初始向量长度 */
	uint8_t         iv[16];            /**< 初始向量 */
	uint32_t        msgLen;            /**< 待验证数据信息长度 */
	uint8_t         *keyMsg;           /**< 待验证数据包，包含公钥信息 */
	uint32_t        macLen;            /**< MAC数据长度*/
	uint8_t         *macData;          /**< MAC值*/
} ST_SEC_VERIFYMAC_INFO;

typedef struct {
	EM_SEC_INJECTKEY_MODE mode;             /**< 验证密钥合法性方式 */
	union {
		ST_SEC_VERIFYMAC_INFO macInfo;      /**< mac方式验证密钥合法性所需信息 */
	};
} ST_SEC_VERIFYKEY_INFO;

typedef struct {
	ST_SEC_ASYM_KEY_INFO pubKey;            /**< 安装公钥位置 */
	uint8_t *tag;                           /**< 公钥数据tag*/
	int result;                             /**< 安装公钥结果 */
} ST_SEC_INJECTKEY_INFO;


extern int (*NAPI_SecInjectPubKey)(ST_SEC_VERIFYKEY_INFO *keyVerifyInfo, ST_SEC_INJECTKEY_INFO *keyInfolist, uint32_t keyListCount, uint8_t *pad, uint32_t adSize);

typedef enum {
	CA_CERT_PATH,      //CA path
	CA_CERT_DATA,      //CA data
} ALG_VERIFY_CA_TYPE;
extern int (*NAPI_AlgRSAVerifyCert)(ALG_VERIFY_CA_TYPE CaType, uint8_t * ca, uint32_t calen, uint8_t * cert, uint32_t certlen, uint8_t *pubkey, uint32_t *publen);
#endif

