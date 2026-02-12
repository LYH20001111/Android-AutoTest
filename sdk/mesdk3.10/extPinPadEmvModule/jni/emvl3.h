	/**************************************************************************
* Copyright (c) 2012, Newland Payment Co.ltd EMV Proj.
* All rights reserved.
*
* @file		EMVL3.H
* @brief	EMV Level 3 function can ensure Payment System-specific requirements
*			and recommendations are being applied to terminal configuration.
*			Including contact, contactless, stripe and manual payment, the
*			payment application can convenient complete transaction process and
*			capture the transaction message interaction with the Financial Institution
*			clients of Payment Systems and their processors.
*			These function also help payment application easy to pass the
*			Payment System-managed Terminal Integration Testing, prior to field
*			deployment of a new or upgraded device, that has already successfully
*			completed EMV Type Approval testing.
*
* @version  0.1
* @date		18.12.06
* @author	bigfacecat
* @brief	create
**************************************************************************/
#ifndef _EMVL3_H_
#define _EMVL3_H_

#include "comm.h"
#ifdef __cplusplus
extern "C" {///using the C compiler
#endif

/****************************include files********************************/
///standard library header files

///other header files


/**************************global macro definitions***********************/

#define LV_CLOSE 0x00  //close debug
#define LV_DEBUG 0x01  //log with normal debug informaiton, normal/recommand use this value;
#define LV_ALL   0x03  //log with all of the debug information


//Return value
#define L3_ERR_SUCC				    0
#define L3_ERR_BASE				    (-500)
#define L3_ERR_FAIL				    (L3_ERR_BASE-1)
#define L3_ERR_CANCEL			    (L3_ERR_BASE-2)
#define L3_ERR_TIMEOUT			    (L3_ERR_BASE-3)
#define L3_ERR_FORMAT			    (L3_ERR_BASE-4)
#define L3_ERR_OVERFLOW			    (L3_ERR_BASE-5)
#define L3_ERR_PARAM			    (L3_ERR_BASE-6)
#define L3_ERR_TAG_ABSENT		    (L3_ERR_BASE-7)
#define L3_ERR_BYPASS			    (L3_ERR_BASE-8)
#define L3_ERR_ONLINE_FAIL		    (L3_ERR_BASE-9)
#define L3_ERR_ONLINE_UNABLE		(L3_ERR_BASE-10)
#define L3_ERR_UNABLE_FORCE_DECLINE (L3_ERR_BASE-11)
#define L3_ERR_ACTIVATE				(L3_ERR_BASE-12)
#define L3_ERR_COLLISION			(L3_ERR_BASE-13)
#define L3_ERR_KERNEL_ERR			(L3_ERR_BASE-14)	//we need L2 kernel error code, but we get error code==0

///Card data input mode
#define L3_CARD_MAGSTRIPE	        ((unsigned int)0x01)
#define L3_CARD_CONTACT		        ((unsigned int)0x02)
#define L3_CARD_CONTACTLESS	        ((unsigned int)0x04)
#define L3_CARD_MANUAL		        ((unsigned int)0x08)
#define L3_CARD_OTHER_ENVET	        ((unsigned int)0x10)


///Transaction type
#define L3_TRANSACTION_PURCHASE				((unsigned char)0x00)
#define L3_TRANSACTION_CASH_ADVANCE			((unsigned char)0x01)
#define L3_TRANSACTION_PURCHASE_CASHBACK	((unsigned char)0x09)
#define L3_TRANSACTION_CASH_DISBURSEMENT	((unsigned char)0x17)
#define L3_TRANSACTION_REFUND				((unsigned char)0x20)

///Account type
#define L3_ACCOUNT_DEFAULT		"00"
#define L3_ACCOUNT_SAVINGS		"10"
#define L3_ACCOUNT_DEBIT		"20"
#define L3_ACCOUNT_CREDIT		"30"



/**************************global data type definitions*******************/

//Transaction result
typedef enum{
	L3_TXN_OK,
//	L3_TXN_ERROR,
//	L3_TXN_COLLISION,
//	L3_TXN_TRY_AGAIN,
	L3_TXN_TERMINATE,
	L3_TXN_TRY_ANOTHER,
	L3_TXN_DECLINE,
	L3_TXN_APPROVED,
	L3_TXN_ONLINE,
//	L3_TXN_FORCE_ACCEPT,
//	L3_TXN_UNABLE_ONLINE,
//	L3_TXN_UNABLE_FORCE_DECLINE,
}L3_TXN_RES;


//Module ID
typedef enum{
	L3_MODULE_API,
	L3_MODULE_EMV,
	L3_MODULE_EP,
	L3_MODULE_QPBOC,
	L3_MODULE_PAYPASS,
	L3_MODULE_PAYWAVE,
	L3_MODULE_EXPRESSPAY,
	L3_MODULE_DPAS,
	L3_MODULE_JCB,
	L3_MODULE_PURE,
	L3_MODULE_RUPAY,
	L3_MODULE_INTERAC,
	L3_MODULE_MIR,
	L3_MDDULE_MULTIBANCO,
}L3_MODULE;


//EMVL3 data type
typedef enum{

	L3_DATA_PAN,
	L3_DATA_TRACK1,
	L3_DATA_TRACK2,
	L3_DATA_TRACK3,
	L3_DATA_DD_CARD_TRACK1,     /*If Track 1 Data is present, then DD Card (Track1) contains a
									copy of the discretionary data field of Track 1 Data as returned
									by the Card in the file read using the READ RECORD
									command during a mag-stripe mode transaction (i.e. without
									Unpredictable Number (Numeric), Application Transaction
									Counter, CVC3 (Track1) and nUN included).


									Length :var. up to 56

									Format: ans*/

	L3_DATA_DD_CARD_TRACK2,		/*DD Card (Track2) contains a copy of the discretionary data
								field of Track 2 Data as returned by the Card during a mag-stripe
								mode transaction(i.e. without Unpredictable Number
								(Numeric), Application Transaction Counter, CVC3 (Track2)
								and nUN included).

								Length: var. up to 11 bytes
								Format: cn*/
	L3_DATA_EXPIRE_DATE,
	L3_DATA_SERVICE_CODE,
	L3_DATA_CARDHOLDER_NAME,
	L3_DATA_POS_ENTRY_MODE,
	L3_DATA_CARD_SCHEME_ID,

	L3_DATA_SIGNATURE,		/**< signature flag*/
	L3_DATA_ADVISE,			/**< advise flag*/
	L3_DATA_ISSUER_SCRIPT_RESULT,

	L3_DATA_GENERIC_TLV,				//< Generic TLV encoded data(e.g. 0x5F2A, 0x9C, 0x9F26)
	L3_DATA_ONLINE_PIN,		/**< online PIN flag*/
}L3_DATA;

/// Kernel configuration bitmap
/// Byte 1 application selection
#define L3_CFG_SUPPORT_EC					(0x0001)	///< Support pboc election currency transaction
#define L3_CFG_SUPPORT_SM					(0x0002)	///< Support sm algorithm
#define L3_CFG_SUPPORT_EXTERNAL_READER		(0x0004)	///< Support external contactless reader



/* Used to determine the config support functions,
        1 support
        0 not supported
        opt    configuration bitmap like above macro definitions: L3_CFG_SUPPORT_XXX
        cfg    configuration pointer */
#define L3_CFG_GET(cfg, opt) \
        ((cfg)[((unsigned int)(opt)) >> 8] & (((unsigned int)(opt)) & 0x00FF))

/*Setting configuration-related functions*/
#define L3_CFG_SET(cfg, opt) \
        ((cfg)[((unsigned int)(opt)) >> 8] |= (((unsigned int)(opt)) & 0x00FF))

/*Clear configuration-related setting*/
#define L3_CFG_UNSET(cfg, opt) \
        ((cfg)[((unsigned int)(opt)) >> 8] &= ~(((unsigned int)(opt)) & 0x00FF))

/// End of Kernel configuration bitmap


///Module processing status
typedef enum {
	L3_AFTER_FINAL_SELECTION,
	L3_AFTER_INITIATE_APP,
	L3_AFTER_READ_DATA,
	L3_AFTER_ODA,
	L3_AFTER_CV,
	L3_AFTER_GAC1,
}L3_STATUS;


///Callback function ID
typedef enum{
	L3_CALLBACK_DEBUG,					///< FUNC_DEBUG
	L3_CALLBACK_UI_EVENT,				///< FUNC_UI_EVENT
	L3_CALLBACK_GET_PIN,				///< FUNC_GET_PIN
	L3_CALLBACK_GET_AMOUNT,				///< FUNC_GET_AMOUNT
	L3_CALLBACK_SELECT_CANDIDATE_LIST,	///< FUNC_SELECT_CANDIDATE_LIST
	L3_CALLBACK_SELECT_ACCOUNT_TYPE,	///< FUNC_SELECT_ACCOUNT_TYPE
	L3_CALLBACK_SELECT_LANGUAGE,		///< FUNC_SELECT_LANGUAGE
	L3_CALLBACK_CHECK_CREDENTIALS,		///< FUNC_CHECK_CREDENTIALS
	L3_CALLBACK_VOICE_REFERRALS,		///< FUNC_VOICE_REFERRALS
	L3_CALLBACK_DEK_DET,				///< FUNC_DEK_DET
	L3_CALLBACK_AFTER_FINAL_SELECT,		///<FUNC_AFTER_FINAL_SELECT
	L3_CALLBACK_CARD_DETECT_EVENT,		///<FUNC_CARD_DETECT_EVENT
	L3_CALLBACK_GET_MANUAL_DATA,		///<FUNC_GET_MANUAL_DATA
}L3_CALLBACK;

///PIN input type
typedef enum {
	L3_PIN_ONLINE,
	L3_PIN_OFFLINE,
	L3_PIN_OFFLINE_ENCIPHERED,
}L3_PIN_TYPE;

///Amount input type
typedef enum {
	L3_AMOUNT,
	L3_AMOUNT_CASHBACK,
}L3_AMOUNT_TYPE;

typedef enum {
	L3_CONTACT = 0x01,
	L3_CONTACTLESS = 0x02,
}L3_CARD_INTERFACE;

typedef enum {
	L3_MANUAL_PAN = 0x01,
	L3_MANUAL_OTHER_DATA = 0x02,
}L3_MANUAL_DATA;


typedef enum
{
	CONFIG_UPT,  // New config will add , Existing config will update
	CONFIG_GET,	 // Get one configuration
	CONFIG_RMV,  // Remove one configuration
	CONFIG_FLUSH,// Remove all of the configuration

} L3_CONFIG_OP;

typedef enum
{
	UI_PRESENT_CARD,
	UI_PROCESSING,              //Ui display during card swiping 1.contact 2.contactless 0.mag
	UI_CAPK_LOAD_FAIL,
	UI_SEE_PHONE,
	UI_CARDNUM_CONFIRM,			//card number Confirm
} L3_UIID;

typedef enum
{
	UI_KEYIN = 0,
	UI_STRIPE,
	UI_INSERT,
	UI_TAP,
	UI_INSERTC_TAP,
	UI_STRIPE_INSERT,
	UI_STRIPE_TAP,
	UI_STRIPE_INSERT_TAP,
	UI_STRIPE_INSERT_TAP_MANUAL,
	UI_PRESENTCARD_AGAIN,
	UI_USE_CHIP,				//ues chip for this transaction
	UI_FALLBACK_CT,				//chip error, use mag-stripe
	UI_FALLBACK_CLSS,			// Insert, swipe or try another card
	UI_STRIPE_INSERT_MANUAL,
	UI_STRIPE_TAP_MANUAL,
	UI_INSERT_TAP_MANUAL,

} L3_UI_CARD;



typedef enum
{
	L3_ONLINE_FAIL = 0,
	L3_ONLINE_SUCC = 1,

}L3_ONLINE_RESULT;

typedef struct{
	unsigned char	aid[16];
	unsigned char	aidLen;
	unsigned char	kernelId[8];
	unsigned char	externCheckFlag;  /** 0x00-default, will no check
									   |0x01-should be matching transactionType
									   |0x02-should be matching externString*/
	unsigned char	transactionType;
	unsigned char 	*externString;
	unsigned char   externStrLen;
}L3_AID_ENTRY;


typedef struct{
	unsigned char   pkModulus[248];
	unsigned char  	pkModulusLen;
	unsigned char   pkExponent[3];
	unsigned char   hashValue[20];
	unsigned char   expiredDate[4];
	unsigned char	rid[5];
	unsigned char	index;
	unsigned char 	pkAlgorithmIndicator;
	unsigned char   hashAlgorithmIndicator;
	unsigned char   rfu[4];                 /*RFU*/
}L3_CAPK_ENTRY;

typedef struct{
	unsigned char	rid[5];
	unsigned char	index;
	unsigned char	csn[3];
	unsigned char   rfu[3];                 /*RFU*/
}L3_CRL_ENTRY;

typedef struct{
	unsigned char	pan[10];
	unsigned char   panLen;
	unsigned char	panSN;		//0xFF: ignore
	unsigned char   rfu[4];                 /*RFU*/
}L3_EXCEPTION_FILE_ENTRY;


typedef struct candidate_list{
	unsigned char *aid;
	unsigned char *lable;
	unsigned char *perferName;
	unsigned char aidLen;
	unsigned char lableLen;
	unsigned char perferNameLen;
	unsigned char issuerCodeTableIndex;
	unsigned char terminalCodeTable[2];
	unsigned char languagePreference[8];
	unsigned char priority;

	unsigned char kernelId[8];			/* Entry Point new add*/
	unsigned char *extendAid;			/* Entry Point new add*/
	unsigned int  extendAidLen;			/* 0: does not support extended AID name,> 0:00 that the length */
	unsigned char terminalPriority;		/* 0 to 255; 255 is the greatest priority*/
	unsigned char *tag9F0A;
	unsigned int  tag9F0ALen;
	unsigned char *customTagData;
	unsigned int  customDataSize;
	unsigned char resv[4];				/* usReserve bytes */

}L3_CANDIDATE_LIST;

/// Public key struct
typedef struct
{
	unsigned char modulus[248];  /**< Public Key modulus */
	unsigned char modulusLen;                   /**< Public Key modulus Length */
	unsigned char exponent[3];                  /**< Public Key Exponent */
}publicKey;

#ifdef __cplusplus
}
#endif

#endif

