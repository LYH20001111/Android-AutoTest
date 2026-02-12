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
#define L3_ERR_SUCC				0
#define L3_ERR_BASE				(-500)
#define L3_ERR_FAIL				(L3_ERR_BASE-1)
#define L3_ERR_CANCEL			(L3_ERR_BASE-2)
#define L3_ERR_TIMEOUT			(L3_ERR_BASE-3)
#define L3_ERR_FORMAT			(L3_ERR_BASE-4)
#define L3_ERR_OVERFLOW			(L3_ERR_BASE-5)
#define L3_ERR_PARAM			(L3_ERR_BASE-6)
#define L3_ERR_TAG_ABSENT		(L3_ERR_BASE-7)
#define L3_ERR_BYPASS			(L3_ERR_BASE-8)
#define L3_ERR_ONLINE_FAIL		(L3_ERR_BASE-9)
#define L3_ERR_ONLINE_UNABLE		(L3_ERR_BASE-10)
#define L3_ERR_UNABLE_FORCE_DECLINE (L3_ERR_BASE-11)
#define L3_ERR_ACTIVATE				(L3_ERR_BASE-12)
#define L3_ERR_COLLISION			(L3_ERR_BASE-13)
#define L3_ERR_KERNEL_ERR			(L3_ERR_BASE-14)	//we need L2 kernel error code, but we get error code==0
#define L3_ERR_SWIPE_CHIP			(L3_ERR_BASE-15)	//This is a Chip Card, but swipe in magnetic swipe reader
#define L3_ERR_REMOVE_INTERRUPT			(L3_ERR_BASE-16)	//Transaction interrupt due to card been removed/loose
#define L3_ERR_FALLBACK					(L3_ERR_BASE-17)
#define L3_ERR_TRY_AGAIN				(L3_ERR_BASE-18)
#define L3_ERR_RFID_UPED                (L3_ERR_BASE-19) 

#define L3_ERR_EXT_READER_BASE    		 	(-600)
#define L3_ERR_EXT_READER_UNCONNECT			(L3_ERR_EXT_READER_BASE-1)
#define L3_ERR_EXT_READER_SEND    			(L3_ERR_EXT_READER_BASE-2)
#define L3_ERR_EXT_READER_RECV    			(L3_ERR_EXT_READER_BASE-3)
#define L3_ERR_EXT_READER_DATA    		 	(L3_ERR_EXT_READER_BASE-4)
#define L3_ERR_EXT_READER_DATA_LENGTH    	(L3_ERR_EXT_READER_BASE-5)
#define L3_ERR_EXT_READER_INVAILD_PARAM  	(L3_ERR_EXT_READER_BASE-6)
#define L3_ERR_EXT_READER_COMMAND_FAIL   	(L3_ERR_EXT_READER_BASE-7)
#define L3_ERR_EXT_READER_CARD_NOT_PRESENT  (L3_ERR_EXT_READER_BASE-8)
#define L3_ERR_EXT_READER_COLLISION  		(L3_ERR_EXT_READER_BASE-9)





///Card data input mode
#define L3_CARD_MAGSTRIPE	((unsigned int)0x01)
#define L3_CARD_CONTACT		((unsigned int)0x02)
#define L3_CARD_CONTACTLESS	((unsigned int)0x04)
#define L3_CARD_MANUAL		((unsigned int)0x08)
#define L3_CARD_OTHER_EVENT	((unsigned int)0x10)


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

//Clss CVM
#define L3_CVM_NO_CVM                           0x00
#define L3_CVM_SIGNATURE                        0x10
#define L3_CVM_ONLINE_PIN                       0x20
#define L3_CVM_CDCVM                            0x30
#define L3_CVM_CVM_NA                           0xF0


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
	L3_DATA_CVM_OUTCOME,
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
	L3_CALLBACK_GET_APDU_DATA,          ///<FUNC_GET_APDU_DATA
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
	UI_CHIP_ERR_RETRY,			//Reading Chip error, Retry 3 times before Fallback 
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
	UI_STRIPE_MANUAL,
	UI_INSERT_MANUAL,
	UI_TAP_MANUAL,

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


///Callback function prototype definition

/*****************************************************************************
*@fn		FUNC_UI_EVENT
*@brief		Display the message according uiEventID.
*@param		[IN]  uiEventID: According to the definition with prefix uiEventID.
*@param		[IN]  uiEventData: UI event data or NULL
*@return	
*@note
*
*@date  	18.12.07
*@author	bigfacecat
*@brief		create
*****************************************************************************/
typedef int (*FUNC_UI_EVENT)(L3_UIID uiEventID, unsigned char*uiEventData);

/*****************************************************************************
*@fn		FUNC_GET_PIN
*@brief		The application should start epp and complete the PIN input processing. 
*			The EMVL3 module will continue process the CVM of PIN verification.
*@param		[IN]  type: The input PIN type.
*@param		[IN]  cnt: The rest PIN try counter of offline PIN.
*@param		[IN]  pinPK: PIN public key,used for enciphered offline PIN
*@param		[OUT] sw1sw2: the SW of VERIFY command, used for offline PIN
*@return	L3_ERR_CANCEL, L3_ERR_TIMEOUT, L3_ERR_BYPASS, L3_ERR_GETCLT10, L3_ERR_GETCBAD, L3_ERR_FAIL
*@note
*
*@date  	18.12.07
*@author	bigfacecat
*@brief		create
*****************************************************************************/
typedef int (*FUNC_GET_PIN)(L3_PIN_TYPE type, unsigned int cnt, publicKey *pinPK, unsigned char *sw1sw2);

/*****************************************************************************
*@fn		FUNC_GET_AMOUNT
*@brief		The application should complete the amount input processing.
*			The data of L3_DATA_AMOUNT, L3_DATA_AMOUNT_OTHER should be set.
*@param		[IN]  type: The input amount type.
*@return	L3_ERR_CANCEL, L3_ERR_TIMEOUT
*@note
*
*@date  	18.12.07
*@author	bigfacecat
*@brief		create
*****************************************************************************/
typedef int (*FUNC_GET_AMOUNT)(L3_AMOUNT_TYPE type, unsigned char *amt);

/*****************************************************************************
*@fn		FUNC_SELECT_CANDIDATE_LIST
*@brief		The application should show the candidate list and return the
*			selection.
*@param		[IN]  candidateList: The candidate list.
*@param		[IN]  listNum: The number of candidate list.
*@param		[OUT] pSelect: The index of selected aid (start from 0)
*@return	L3_ERR_SUCC, L3_ERR_FAIL, L3_ERR_CANCEL, L3_ERR_TIMEOUT
*@note
*
*@date  	18.12.07
*@author	bigfacecat
*@brief		create
*****************************************************************************/
typedef int (*FUNC_SELECT_CANDIDATE_LIST)(L3_CANDIDATE_LIST candidateList[], int listNum,  int *pSelect);

/*****************************************************************************
*@fn		FUNC_SELECT_ACCOUNT_TYPE
*@brief		The application should complete the account type selection.
*@param		[OUT] acct_type: Account type, (start from 0)
*@return	L3_ERR_SUCC, L3_ERR_FAIL, L3_ERR_CANCEL, L3_ERR_TIMEOUT
*@note
*
*@date  	18.12.07
*@author	bigfacecat
*@brief		create
*****************************************************************************/
typedef int (*FUNC_SELECT_ACCOUNT_TYPE)(unsigned char *acct_type);

/*****************************************************************************
*@fn		FUNC_SELECT_LANGUAGE
*@brief		The application should match the languagePreference and complete 
*			the language selection.
*			The data of L3_DATA_TERMINAL_LANGUAGE should be set.
*@param		[IN]  languagePreference: The preference language of icc. 
*								1-4 languages stored in order of preference,
*								each represented by 2 alphabetical characters
*								according to ISO 639.
*@return	L3_ERR_SUCC, L3_ERR_FAIL, L3_ERR_CANCEL, L3_ERR_TIMEOUT
*@note
*
*@date  	18.12.07
*@author	bigfacecat
*@brief		create
*****************************************************************************/
typedef int (*FUNC_SELECT_LANGUAGE)();

/*****************************************************************************
*@fn		FUNC_CHECK_CREDENTIALS
*@brief		The application should complete the credentials checking.
*@param		NONE
*@return	L3_ERR_SUCC, L3_ERR_FAIL, L3_ERR_CANCEL, L3_ERR_TIMEOUT
*@note
*
*@date  	18.12.07
*@author	bigfacecat
*@brief		create
*****************************************************************************/
typedef int (*FUNC_CHECK_CREDENTIALS)(void);

/*****************************************************************************
*@fn		FUNC_VOICE_REFERRALS
*@brief	An attended terminal shall either display the ¡®CALL YOUR BANK¡¯ message to the attendant, 

* or shall alert the attendant in some other way that the Issuer has requested a voice referral.
* Appropriate application data, such as the Application PAN, should be displayed
* or printed to the attendant in order to perform the referral. Appropriate
* messages should be displayed requesting the attendant to enter data indicating
* that the transaction has been approved or declined as a result of the referral process. 
*@param		[OUT] result: 0-APPROVE(TC), 1-Declined(AAC)
*@return	L3_ERR_SUCC, L3_ERR_FAIL
*@note
*
*@date  	18.12.07
*@author	bigfacecat
*@brief		create
*****************************************************************************/
typedef int (*FUNC_VOICE_REFERRALS)(int *result);


/*****************************************************************************
*@fn		FUNC_Debug
*@brief		Output EMV Debug log
*@param		[IN]  log: Emv debug log
*@param		[IN]  len: length of log
*@return	
*@note
*@date  	18.12.07
*@author	bigfacecat
*@brief		create
*****************************************************************************/
typedef void(*FUNC_DEBUG)(const char *log, int len);

/*****************************************************************************
*@fn		FUNCDekDet
*@brief		Paypass DataExchange,DEK,DET
*@param		[IN]  type: 0x01-DEK, 0x02-DET
*@param		[IN/OUT]  buf: DEK sent command or DET response command
*@param		[IN/OUT]  len: command length
*@return	L3_ERR_FAIL, L3_ERR_CANCEL, L3_ERR_TIMEOUT
*@note
*@brief		create
*****************************************************************************/
typedef int(*FUNC_DEK_DET)(unsigned char type, unsigned char *buf, int *len);



/*****************************************************************************
*@fn		FUNC_AfterFinalSelect
*@brief		This function used after Final Selection but before GPO
*			We can update the Terminal/AID Configurations according to the different 
*			AID in current transaction by using 'NAPI_L3SetData'. [Option function	]
*@param		[IN]  interface: Contact/Contactless
*@param		[IN]  aid: The aid been select
*@param		[IN]  aidLen: The length of aid
*@return	0
*@note
*@brief		create
*****************************************************************************/
typedef int(*FUNC_AFTER_FINAL_SELECT)(L3_CARD_INTERFACE interface, unsigned char *aid, int aidLen);


/*****************************************************************************
*@fn		FUNC_CARD_DETECT_EVENT
*@brief		This function is used to handle the special event while detecting card

*			such as CANCEL,Manual or other keyboard event
*@param		[IN]   input: L3_CARD_MANUAL|L3_CARD_OTHER_EVENT
			[Out]  res: L3_CARD_MANUAL or L3_CARD_OTHER_EVENT
*@return	L3_ERR_SUCC,
			L3_ERR_FAIL, 
			L3_ERR_CANCEL, Cancel detect card 
			L3_ERR_TIMEOUT
*@note
*****************************************************************************/
typedef int(*FUNC_CARD_DETECT_EVENT)(int input, int *res);


/*****************************************************************************
*@fn		FUNC_GET_MANUAL_DATA
*@brief		This function is used to get Manual card data,such as Expiry date, CVV2
*			Used for manual transaction
*@param		[Out]  res: L3_CARD_MANUAL or L3_CARD_OTHER_EVENT
*@return	L3_ERR_SUCC,
			L3_ERR_FAIL, 
			L3_ERR_CANCEL,
			L3_ERR_TIMEOUT
*@note
*****************************************************************************/
typedef int(*FUNC_GET_MANUAL_DATA)(void);


/*****************************************************************************
*@fn	FUNC_GET_APDU_DATA
*@brief Optional callback to give applications access to the APDU exchange data.
*@param	[IN] if: Card interface used, @see L3_CARD_INTERFACE
*@param	[IN] req: The APDU sent to the card.
*@param	[IN] req_length: Length of the APDU sent to the card.
*@param	[IN] rsp: The card response from the card. Full response, includes SW1SW2.
*@param	[IN] rsp_length: Length of the card, response,  including SW1SW2, if L1 errors, rsp_length would be zero.
*@return    L3_ERR_SUCC,
            L3_ERR_FAIL,
            L3_ERR_CANCEL
*@note This callback is to be called after exchanging an APDU with the card.
* The L3 should check the return code, giving the app the possibility of aborting the transaction.
*****************************************************************************/
typedef int (*FUNC_GET_APDU_DATA)(L3_CARD_INTERFACE interface, const unsigned char *req, int req_length, const unsigned char *rsp, int rsp_length);


/*****************************************************************************
*@fn		NAPI_L3Init
*@brief		Initialize newland Level3 module		
*			Initialization and configuration need only be performed once at module startup 
*			and whilst configuration remains the same during processing.

*@param		[IN] filePath:	kernel configuration file path
*@param		[IN] config:	According to configuration bitmap definitions.
*@return	description
*@retval 	0:	Success.
*@retval 	<0:	Fail.
*@note
*****************************************************************************/

int NAPI_L3Init(char *filePath, char *config);

/**************************global function declarations*******************/

/*****************************************************************************
*@fn		NAPI_L3GetVersion
*@brief		Get the version of EMVL3 module, EMVL2, EP and other contactless kernel.
*@param		[IN]  module: Module type you want to get version.
*@return	The point of module version string.
*@note
*
*@date  	18.12.07
*@author	bigfacecat
*@brief		create
*****************************************************************************/
const char * NAPI_L3GetVersion(L3_MODULE module);

/*****************************************************************************
*@fn		NAPI_L3GetKernelChecksum
*@brief		Get the Kernel Checksum of EMVL3 module, EMVL2, EP and other contactless kernel.
*@param		[IN]  module: Module type you want to get version.
*@param		[IN]  checksum:	The buffer used for storing the checksum.
*@param		[IN]  size:		The buffer size of checksum. It must be not less than 4.
*@return	
*@retval 	EMVL2_ERR_NONE
*@retval 	EMVL2_ERR_PARAM
*@retval 	EMVL2_ERR_FAIL
*@note
*****************************************************************************/
int NAPI_L3GetKernelChecksum(L3_MODULE module, unsigned char *checksum, unsigned int size);


///////////////////////////////////////////////////////////////////////////
//						EMVL3 configuration management					 //
//   The parameter type include ICS, AID, CAPK, CRL, exception file.  Kernel configuration bitmap(byte 1) //
// Every parameter type has a entry to index the special record.		 //
///////////////////////////////////////////////////////////////////////////

/*****************************************************************************
*@brief		 Used to determine the config support functions,    
*@param		[IN] offset:   opt    configuration bitmap like above macro definitions: L3_CFG_SUPPORT_XXX
*@param		[IN] nMode:   1 support,    0 not supported                     
*@return	
*@retval 	0:  successful.
*@retval 	<0: failed
*@note
*****************************************************************************/

int NAPI_L3SetConfig(int offset, int nMode);

/*****************************************************************************
*@brief		Get the result of whether the specified configuration is supported.   
*@param		[IN] offset:   opt    configuration bitmap like above macro definitions: L3_CFG_SUPPORT_XXX                  
*@return	
*@retval 	1:  set.
*@retval 	0: unset
*@note
*****************************************************************************/

int NAPI_L3GetConfig(int offset);


/*****************************************************************************
*@brief		Terminal configuration management(Update/Get)
*@param		[IN] interface: Contact / Contactless
*@param		[IN/OUT] tlv_list: Tlv terminal configuration string
*@param 	[IN/OUT] tlv_len:  the length of tlv string
*@param		[IN] mode: Update/Get/Remove/Flush, if Flush, will delete all of the configuration(Terminal and AID)
*@return	
*@retval 	0:  successful.
*@retval 	<0: failed
*@note
*****************************************************************************/
int NAPI_L3LoadTerminalConfig(L3_CARD_INTERFACE interface, unsigned char tlv_list[], int *tlv_len, L3_CONFIG_OP mode);

/*****************************************************************************
*@brief		AID configuration management(Update/Get/Remove/Flush)
*@param		[IN] interface: Contact / Contactless
*@param		[IN] aidEntry:  Aid indicator(Get/Remove) or NULL(Update/Flush)
*@param		[IN/OUT] tlv_list: Tlv AID configuration string
*@param 	[IN/OUT] tlv_len:  the length of tlv string
*@param		[IN] mode: Update/Get/Remove/Flush, if Flush, will delete all of the configuration(Terminal and AID)
*@return	
*@retval 	0:  successful.
*@retval 	<0: failed
*@note
*****************************************************************************/
int NAPI_L3LoadAIDConfig(L3_CARD_INTERFACE interface, L3_AID_ENTRY *aidEntry, unsigned char tlv_list[], int *tlv_len, L3_CONFIG_OP mode);



/**
* @brief  Enumerate all AID, include terminal config 
*@param		[IN] interface: Contact/Contactless
* @param 	[OUT] aidEntry: AID Lists
* @param 	[IN]maxCount:   Max size of aidEntry
* @return
* @li        >0           Number of AID(<= maxCount)
* @li        <= 0         FAIL
*/
int NAPI_L3EnumEmvConfig(L3_CARD_INTERFACE interface, L3_AID_ENTRY * aidEntry, int maxCount);


/*****************************************************************************
*@brief		CAPK management(Update/Get/Remove/Flush)
*@param		[IN] capk: ca publick key
*@param		[IN] mode:  Update/Get/Remove/Flush
*@return	
*@retval 	0:  successful.
*@retval 	<0: failed
*@note
*****************************************************************************/
int NAPI_L3LoadCAPK(L3_CAPK_ENTRY *capk, L3_CONFIG_OP mode);

/**
* @brief     Enumerate CAPK
* @param in  start        ---Start index
* @param in  end          ---End index
* @param out capk        ---CAPK Lists
* @return
* @li        >0            Number of public key
* @li        <= 0          FAIL
*/
int NAPI_L3EnumCapk(int start, int end, char capk[][6]);

/*****************************************************************************
*@brief		Certification revocation list management(Update/Get/Remove/Flush)
*@param		[IN] crl: certification revocation list
*@param		[IN] mode:  Update/Get/Remove/Flush
*@return	
*@retval 	0:  successful.
*@retval 	<0: failed
*@note
*****************************************************************************/
int NAPI_L3LoadRevocationList(L3_CRL_ENTRY *crl, L3_CONFIG_OP mode);


/*****************************************************************************
*@brief		Exception list management(Update/Get/Remove/Flush)
*@param		[IN] exceptionList: exception list
*@param		[IN] mode:  Update/Get/Remove/Flush
*@return	
*@retval 	0:  successful.
*@retval 	<0: failed
*@note
*****************************************************************************/
int NAPI_L3LoadExceptionList(L3_EXCEPTION_FILE_ENTRY *exceptionList, L3_CONFIG_OP mode);






///////////////////////////////////////////////////////////////////////////
//						EMVL3 transaction management					 //
//   Simplify the transaction processing of any card data input type to  //
// facilitate application development, and support executing EMV L2,     //
// paypass, paywave and any contractless kernels automatically.		     //
///////////////////////////////////////////////////////////////////////////

/*****************************************************************************
*@fn		NAPI_L3PerformTransaction
*@brief		Perform transactions on the MSR, contact and contactless card interfaces. 
*			A transaction may have a simple flow that can be completed via this API, 
*			in which case the result returned will be Offline Approved, Offline Declined, Failed or some other error status. 
*			A transaction may also have a more complex flow that may require going online for authorization. 
*			For such flows the transaction will have to be completed by using multiple API. 
*			In this case this API will return a status of Online Authorization Required.
*			When this happens, the terminal must send the transaction online for authorization.
*@param		[IN] data: transaction data
*@param		[IN] dataLen: the length of data
*@param		[OUT] res: the transaction result
*@return	description
*@retval 	error code: level 2 or level 3 error code 
*@note
*
*@date  	18.12.07
*@author	linld
*****************************************************************************/
int NAPI_L3PerformTransaction(char *data, int dataLen, L3_TXN_RES *res);



/*****************************************************************************
*@fn		NAPI_L3CompleteTransaction
*@brief		Complete the transaction. 
*			When NAPI_L3PerformTransaction return a status of Online Authorization Required.
*			the terminal must send the transaction online for authorization.
*			The back-end host may respond back with a result or there may be a timeout or a network error.
*			In any case, the terminal should use this API conveying the result of the Online Authorization Request to complete the transaction.
*@param		[IN] data: transaction data
*@param		[IN] dataLen: the length of data
*@param		[OUT] res: the transaction result
*@return	description
*@retval 	errorcode: 
*@note
*
*@date  	18.12.07
*@author	linld
*****************************************************************************/
int NAPI_L3CompleteTransaction(char *data, int dataLen, L3_TXN_RES *res);


/*****************************************************************************
*@fn		NAPI_L3TerminateTransaction
*@brief		Terminate transaction and release resource.
*@return	description
*@retval 	value: description
*@note
*
*@date  	18.12.07
*@author	bigfacecat
*@brief		create
*****************************************************************************/
int NAPI_L3TerminateTransaction();

///////////////////////////////////////////////////////////////////////////
//						EMVL3 data management							 //
//   Manage the EMVL3 module data of transaction data and control mode,  //
// which can set by application, and also support output the kernel data.//
///////////////////////////////////////////////////////////////////////////

/*****************************************************************************
*@fn		NAPI_L3SetData
*@brief		Setup kernel data.
*@param		[IN]  tag: Tag.
*@param		[IN]  data: The setting data buffer.
*@param		[IN]  len: The length of data.
*@return	description
*@retval 	value: description
*@note
*
*@date  	18.12.07
*@author	bigfacecat
*@brief		create
*****************************************************************************/
int NAPI_L3SetData(unsigned int tag, void *data, unsigned int len);

/*****************************************************************************
*@fn		NAPI_L3GetData
*@brief		Get the EMVL3 data 
*@param		[IN]  type: ,L3_DATA or L2 TAG.
*@param		[OUT] data: The data buffer.
*@param		[IN]  maxLen: The buffer length of data.
*@return	description
*@retval  	0    tag value does not exist
*@retval  	>0   The length of the Value
*@retval  	-1   Data length exceeds length limit
*@note
*
*@date  	18.12.07
*@author	bigfacecat
*@brief		create
*****************************************************************************/
int NAPI_L3GetData(unsigned int type, void *data, int maxLen);

/*****************************************************************************
*@fn		NAPI_L3GetTlvData
*@brief		Get the TLV format data from current kernel.
*@param		[IN] tagList: The tag list which will be packed to tlvData.
*@param		[IN] tagNum: The number of tag list.
*@param 	[OUT] tlvData: Out tlv data
*@param		[IN] maxLen: The size of tlvData.
*@param		[IN] ctl: Control code.
					bit0: 1: ignore the zero length tag.
*@return	description
*@retval 	< 0 Fail
			> 0 the length of out data(tlvData)
*@note
*
*@date  	18.09.18
*@author	bigfacecat
*@brief		create
*****************************************************************************/
int NAPI_L3GetTlvData(unsigned int *tagList, unsigned int tagNum, unsigned char *tlvData, unsigned int maxLen,int ctl);

///////////////////////////////////////////////////////////////////////////
//						EMVL3 callback function							 //
//   Manage the callback function of EMVL3 module.						 //
///////////////////////////////////////////////////////////////////////////

/*****************************************************************************
*@fn		NAPI_L3SetCallback
*@brief		Set the EMVL3 callback function according L3_CALLBACK id.
*@param		[IN] id: The callback function ID. 
*@param		[IN] func: The callback function pointer.
*@return	description
*@retval 	value: description
*@note
*
*@date  	18.12.07
*@author	bigfacecat
*@brief		create
*****************************************************************************/
int NAPI_L3SetCallback(L3_CALLBACK id, void *func);


/**
* @Func		NAPI_L3SetDebugMode
* @brief Setup Debug Mode
* @param [IN]debugLV   LV_CLOSE / LV_DEBUG / LV_ALL
* @return
* @date 2019-04-26
*/
void NAPI_L3SetDebugMode(int debugLV);

/**
* @Func	NAPI_L3DetectCard
* @brief Detect Card
* @param [IN] interface: indicate card interfaces on which to look for a card

*	     Bit 1: Use MSR Interface if set to 1.
*		 Bit 2: Use Contact Interface if set to 1.
*		 Bit 3: Use Contactless Interface if set to 1.
*		 Bits 4-8: RFU. The RFU bits must be set to 0.
*		 One or more of the defined bits can be set at the same time
* @param [IN] timeout: Time in seconds for which the Reader will look for a card 
*		 on the specified interfaces before returning a timeout error
* @param [OUT] res: current card interface if the card reading successfully
*   	 0x01: MSR interface
*		 0x02: Contact Interface
*		 0x04: Contactless interface	
* @return Error code
*		 L3_ERR_SUCC
*		 L3_ERR_FAIL
*		 L3_ERR_CANCEL
*		 L3_ERR_TIMEOUT
*		 L3_ERR_SWIPE_CHIP: This is a Chip Card, but swipe in magnetic swipe reader
* @date 2019-04-26
*/
int NAPI_L3DetectCard(int interface, int timeout, int *res);


#ifdef __cplusplus
}
#endif

#endif

