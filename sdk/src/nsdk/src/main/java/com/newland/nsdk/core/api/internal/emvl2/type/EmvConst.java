package com.newland.nsdk.core.api.internal.emvl2.type;

/**
 * EMV const.
 * <ul>
 *     <li>AS: Application Selection. </li>
 *     <li>DA: Data Authentication</li>
 *     <li>IPKC: Issuer Public Key Certificate</li>
 *     <li>CAPK: Certification Authority Public Key</li>
 *     <li>CV: Cardholder Verification</li>
 *     <li>CVM: Cardholder Verification Methods</li>
 *     <li>TRM: Terminal Risk Management</li>
 *     <li>TAA: Terminal Action Analysis</li>
 *     <li>TAC: Terminal Action Codes</li>
 *     <li>DAC: Default Action Codes</li>
 *     <li>CP: Completion Process</li>
 *     <li>MISC: Miscellaneous</li>
 *     <li>TC: Terminal Capabilities</li>
 *     <li>ATC: Additional Terminal Capabilities</li>
 * </ul>
 */
public class EmvConst {
    /**
     * Support PSE selection method
     */
    public static final int AS_Support_PSE = (0x0080);
    /**
     * Support Cardholder confirmation
     */
    public static final int AS_Support_CardHolder_Confirm = (0x0040);
    /**
     * Have a preferred order of displaying applications
     */
    public static final int AS_Support_Preferred_Order = (0x0020);
    /**
     * Does the terminal perform partial AID selection
     */
    public static final int AS_Support_Partial_AID = (0x0010);
    /**
     * Does the terminal have multi language support
     */
    public static final int AS_Support_Multi_Language = (0x0008);
    /**
     * Does the terminal support Common Character Set
     */
    public static final int AS_Support_Common_Charset = (0x0004);

    /**
     * During DA, does the terminal check the revocation of IPKC
     */
    public static final int DA_Support_IPKC_Revoc_Check = (0x0180);
    /**
     *  Does the terminal contain a default DDOL
     */
    public static final int DA_Support_Default_DDOL = (0x0140);
    /**
     * Is operation action required when loading CAPK failed
     */
    public static final int DA_Support_CAPKLoad_Fail_Action = (0x0120);
    /**
     * Is CAPK verified with CAPK checksum
     */
    public static final int DA_Support_CAPK_Checksum = (0x0110);

    /**
     * Terminal supports bypass PIN entry
     */
    public static final int CV_Support_Bypass_PIN = (0x0280);
    /**
     * Terminal supports Get Data for PIN Try
     */
    public static final int CV_Support_PIN_Try_Counter = (0x0240);
    /**
     * Terminal supports Fail CVM
     */
    public static final int CV_Support_Fail_CVM = (0x0220);
    /**
     * Are amounts known before CVM processing
     */
    public static final int CV_Support_Amounts_before_CVM = (0x0210);
    /**
     *
     */
    public static final int CV_Support_Bypass_ALL_PIN = (0x0208);

    /**
     * Floor Limit Checking, mandatory for terminal with offline capability
     */
    public static final int TRM_Support_FloorLimit = (0x0380);
    /**
     * Random Transaction Selections, mandatory for offline terminal with online capability, except when cardholder controlled
     */
    public static final int TRM_Support_RandomSelect = (0x0340);
    /**
     * Velocity checking, mandatory for for terminal with offline capability
     */
    public static final int TRM_Support_VelocityCheck = (0x0320);
    /**
     * Support transaction log
     */
    public static final int TRM_Support_TransLog = (0x0310);
    /**
     * Support exception file
     */
    public static final int TRM_Support_ExceptionFile = (0x0308);
    /**
     * Performance of TRM based on AIP setting
     */
    public static final int TRM_Support_AIPBased = (0x0304);
    /**
     * EMV has a different log policy with PBOC2, marked here
     */
    public static final int TRM_Use_EMV_LogPolicy = (0x0302);

    /**
     * Does the terminal support Terminal Action Codes
     */
    public static final int TAA_Support_TAC = (0x0480);
    /**
     * Does the terminal process DAC prior to first GenAC
     */
    public static final int TAA_Support_DAC_before_1GenAC = (0x0440);
    /**
     * Does the terminal process DAC after first GenAC
     */
    public static final int TAA_Support_DAC_after_1GenAC = (0x0420);
    /**
     * Does the terminal skip DAC processing and automatically request an AAC when unable to process online
     */
    public static final int TAA_Support_Skip_DAC_OnlineFail = (0x0410);
    /**
     * Does the terminal process DAC as normal when unable to process online
     */
    public static final int TAA_Support_DAC_OnlineFail = (0x0408);
    /**
     * Device capable of detecting CDA Failure before TAA
     */
    public static final int TAA_Support_CDAFail_Detected = (0x0404);
    /**
     * CDA always requested in a first Gen AC, ARQC request
     */
    public static final int TAA_Support_CDA_Always_in_ARQC = (0x0402);
    /**
     * CDA always requested in a second Gen AC when successful host response is received, with TC request
     */
    public static final int TAA_Support_CDA_Alawys_in_2TC = (0x0401);

    /**
     * Transaction forced Online capability
     */
    public static final int CP_Support_Force_Online = (0x0580);
    /**
     * Transaction forced Acceptance capability
     */
    public static final int CP_Support_Force_Accept = (0x0540);
    /**
     * Does the terminal support advices
     */
    public static final int CP_Support_Advices = (0x0520);
    /**
     * Does the terminal support Issuer Initiated Voice Referrals
     */
    public static final int CP_Support_Issuer_VoiceRef = (0x0510);
    /**
     *  Does the terminal support Batch Data Capture
     */
    public static final int CP_Support_Batch_Data_Capture = (0x0508);
    /**
     * Does the terminal support Online Data Capture
     */
    public static final int CP_Support_Online_Data_capture = (0x0504);
    /**
     * Does the terminal support a default TDOL
     */
    public static final int CP_Support_Default_TDOL = (0x0502);

    /**
     * Does the terminal support account type selection
     */
    public static final int MISC_Support_Account_Select = (0x0680);
    /**
     * Is Issuer Script Device Limit greater than 128 bytes
     */
    public static final int MISC_Support_ISDL_Greater_than_128 = (0x0640);
    /**
     * Does the terminal support internal date management
     */
    public static final int MISC_Support_Internal_Date_Mana = (0x0620);
    /**
     * Default UDOL (paypass only)
     */
    public static final int MISC_PP_Support_Default_UDOL = (0x0602);
    /**
     * Mag stripe application version (paypass only)
     */
    public static final int MISC_MISC_PP_Support_MagAppVer = (0x0601);

    /**
     * Keyboard input card number
     */
    public static final int TC_Manual_Key_Entry = (0x0080);
    /**
     * Magnetic stripe card
     */
    public static final int TC_Magnetic_Stripe = (0x0040);
    /**
     * Contact IC card
     */
    public static final int TC_IC_With_Contacts = (0x0020);
    /**
     * Plaintext PIN verification
     */
    public static final int TC_Plaintext_PIN = (0x0180);
    /**
     * Online ciphertext PIN verification
     */
    public static final int TC_Enciphered_PIN_Online = (0x0140);
    /**
     * External signature info (paper)
     */
    public static final int TC_Signature_Paper = (0x0120);
    /**
     * Offline ciphertext PIN verification
     */
    public static final int TC_Enciphered_PIN_Offline = (0x0110);
    /**
     * NO CVM
     */
    public static final int TC_No_CVM_Required = (0x0108);
    /**
     * Cardholder certificate
     */
    public static final int TC_Cardholder_Cert = (0x0101);
    /**
     * Static data authentication(SDA)
     */
    public static final int TC_SDA = (0x0280);
    /**
     * Dynamic data authentication(DDA)
     */
    public static final int TC_DDA = (0x0240);
    /**
     * retain card
     */
    public static final int TC_Card_Capture = (0x0220);
    /**
     * Composite dynamic data authentication(CDA)
     */
    public static final int TC_CDA = (0x0208);

    /**
     * Cash
     */
    public static final int ATC_Cash = (0x0080);
    /**
     * Goods
     */
    public static final int ATC_Goods = (0x0040);
    /**
     * Services
     */
    public static final int ATC_Services = (0x0020);
    /**
     * Cashback
     */
    public static final int ATC_Cashback = (0x0010);
    /**
     * Inquiry
     */
    public static final int ATC_Inquiry = (0x0008);
    /**
     * Transfer
     */
    public static final int ATC_Transfer = (0x0004);
    /**
     * Payment
     */
    public static final int ATC_Payment = (0x0002);
    /**
     * Administrative
     */
    public static final int ATC_Administrative = (0x0001);
    /**
     * Deposit
     */
    public static final int ATC_Cash_Deposit = (0x0180);
    /**
     * Numeric_Keys
     */
    public static final int ATC_Numeric_Keys = (0x0280);
    /**
     * Alphabetic_Special_Keys
     */
    public static final int ATC_Alphabetic_Special_Keys = (0x0240);
    /**
     * Command_Keys
     */
    public static final int ATC_Command_Keys = (0x0220);
    /**
     * Function_Keys
     */
    public static final int ATC_Function_Keys = (0x0210);
    /**
     * Print_Attendant
     */
    public static final int ATC_Print_Attendant = (0x0380);
    /**
     * Print_Cardholder
     */
    public static final int ATC_Print_Cardholder = (0x0340);
    /**
     * Display_Attendant
     */
    public static final int ATC_Display_Attendant = (0x0320);
    /**
     * Display_Cardholder
     */
    public static final int ATC_Display_Cardholder = (0x0310);
    /**
     * Code_Table_10
     */
    public static final int ATC_Code_Table_10 = (0x0302);
    /**
     * Code_Table_9
     */
    public static final int ATC_Code_Table_9 = (0x0301);
    /**
     * Code_Table_8
     */
    public static final int ATC_Code_Table_8 = (0x0480);
    /**
     * Code_Table_7
     */
    public static final int ATC_Code_Table_7 = (0x0440);
    /**
     * Code_Table_6
     */
    public static final int ATC_Code_Table_6 = (0x0420);
    /**
     * Code_Table_5
     */
    public static final int ATC_Code_Table_5 = (0x0410);
    /**
     * Code_Table_4
     */
    public static final int ATC_Code_Table_4 = (0x0408);
    /**
     * Code_Table_3
     */
    public static final int ATC_Code_Table_3 = (0x0404);
    /**
     * Code_Table_2
     */
    public static final int ATC_Code_Table_2 = (0x0402);
    /**
     * Code_Table_1
     */
    public static final int ATC_Code_Table_1 = (0x0401);

    /**
     * Terminal transaction property 9F66: Support contactless magnetic stripe (MSD);
     */
    public static final int EMV_PROP_MSD = (0x0080);
	 /**
	 *Terminal transaction property 9F66: 1:clss VSDC supported 0:clss VSDC not supported
	 */
	 public static final int EMV_PROP_VSDCCLSS = (0x0040);          
    /**
     * Terminal transaction property 9F66: Support contactless PBOC
     */
    public static final int EMV_PROP_PBOCCLSS = (0x0040);
    /**
     * Terminal transaction property 9F66: Support contactless QVSDC
     */
    public static final int EMV_PROP_QVSDC = (0x0020);
    /**
     * Terminal transaction property 9F66: Support contactless qPBOC
     */
    public static final int EMV_PROP_QPBOC = (0x0020);
    /**
     * Terminal transaction property 9F66: Support contact VSDC
     */
    public static final int EMV_PROP_VSDC = (0x0010);
    /**
     * Terminal transaction property 9F66: Support contact PBOC
     */
    public static final int EMV_PROP_PBOC = (0x0010);
    /**
     * Terminal transaction property 9F66: Supports offline only;  0:Online capability
     */
    public static final int EMV_PROP_OFFLINE_ONLY = (0x0008);
    /**
     * Terminal transaction property 9F66: Supports online PIN
     */
    public static final int EMV_PROP_ONLINEPIN = (0x0004);
    /**
     * Terminal transaction property 9F66: Supports ExtSignatureInfo
     */
    public static final int EMV_PROP_SIGNATURE = (0x0002);
 	/*1:support online ODA(VCPS not support)*/
    public static final int  EMV_PROP_ODAONLINE= ( 0x0001 );            
    /**
     * Terminal transaction property 9F66: Online ciphertext
     */
    public static final int EMV_PROP_ONLINEAC = (0x0180);
    /**
     * Terminal transaction property 9F66: CVM
     */
    public static final int EMV_PROP_CVM = (0x0140);
    /*1:Support Contact offline PIN(VCPS Contact chip)*/
    public static final int EMV_PROP_OFFLINEPIN= (0x0120);        
    /**
     * Terminal transaction property 9F66: Support Issuer Update Processing
     */
    public static final int EMV_PROP_IUP = (0x0280);
    /**
     * Terminal transaction property 9F66: Support Consumer Device CVM ( mobile )
     */
    public static final int EMV_PROP_CDCVM = (0x0240);
    public static final int EMV_PROP_01VERSUPPORT = (0x0380);

    /**
     * When to input amount: No input amount
     */
    public static final int EMV_TRANS_REQAMT_NO = (0);
    /**
     * When to input amount: Application selection input
     */
    public static final int EMV_TRANS_REQAMT_APS = (1);
    /**
     * When to input amount: Data authentication input
     */
    public static final int EMV_TRANS_REQAMT_DDA = (2);
    /**
     * When to input amount: Pre-processing input
     */
    public static final int EMV_TRANS_REQAMT_RFPRECESS = (3);

    /**
     * Kernel configuration bitmap, Byte 1 application selection: Support PSE selection method
     */
    public static final int EMVL2_SUPPORT_PSE = (0x0001);
    /**
     * Kernel configuration bitmap, Byte 1 application selection: upport Cardholder confirmation
     */
    public static final int EMVL2_SUPPORT_CARDHOLDER_CONFIRM = (0x0002);

    /**
     * Kernel configuration bitmap, Byte 2 transaction initialize: Support PBOC election currency transaction
     */
    public static final int EMVL2_SUPPORT_PBOC = (0x0101);
	///< Support NSICCS transaction
	public static final int EMVL2_SUPPORT_NSICCS = (0x0102);	
    /**
     * Kernel configuration bitmap, Byte 3 Data authentication: Support SM algorithm
     */
    public static final int EMVL2_SUPPORT_SM = (0x0201);

    /**
     * Kernel configuration bitmap, Byte 4 CVM: Support bypass PIN entry
     */
    public static final int EMVL2_SUPPORT_BYPASS = (0x0301);
    /**
     * Kernel configuration bitmap, Byte 4 CVM: Support subsequent bypass PIN entry
     */
    public static final int EMVL2_SUPPORT_SUBSEQUENT_BYPASS = (0x0302);
    /**
     * Kernel configuration bitmap, Byte 4 CVM: Supports Get Data for PIN Try Counter
     */
    public static final int EMVL2_SUPPORT_GET_PIN_TRY_COUNTER = (0x0304);

    /**
     * Kernel configuration bitmap, Byte 5 Risk Management: Support floor limit checking
     */
    public static final int EMVL2_SUPPORT_FLOOR_LIMIT = (0x0401);
    /**
     * Kernel configuration bitmap, Byte 5 Risk Management: Support random transaction selection
     */
    public static final int EMVL2_SUPPORT_RANDOM_SELECT = (0x0402);
    /**
     * Kernel configuration bitmap, Byte 5 Risk Management: Support velocity checking
     */
    public static final int EMVL2_SUPPORT_VELOCITY_CHECK = (0x0404);
	/*new data rule, 20xx*/
    public static final int EMVL2_SUPPORT_NEWDATE = (0x0408);
    /**
     * Kernel configuration bitmap, Byte 6 TAA: Support ofline only terminal process DAC after first GAC, otherwise prior first GAC.
     */
    public static final int EMVL2_SUPPORT_DAC_AFTER_GAC1 = (0x0501);
    /**
     * Kernel configuration bitmap, Byte 6 TAA: Support online only terminal normal process DAC when unable to go online, else automatically request an AAC.
     */
    public static final int EMVL2_SUPPORT_DAC_ONLINEFAIL = (0x0502);
	/// Kernel configuration bitmap Byte 8 Other Function
	///< Support general terminal unpredictable number with Specification Bulletin No.144.
	 public static final int EMVL2_SUPPORT_RAND_SB144	= (0x0701);	


    /**
     * Initiate Application
     */
    public static final int EMV_PROC_TO_APPSEL_INIT = 0;
    /**
     * Read Application Data
     */
    public static final int EMV_PROC_TO_READAPPDATA = 1;
    /**
     * Data Authentication
     */
    public static final int EMV_PROC_TO_OFFLINEAUTH = 2;
    /**
     * Processing Restrictions
     */
    public static final int EMV_PROC_TO_RESTRITCT = 3;
    /**
     * Cardholder Verification
     */
    public static final int EMV_PROC_TO_CV = 4;
    /**
     * Terminal Risk Management
     */
    public static final int EMV_PROC_TO_RISKMANA = 5;
    /**
     * Terminal Action Analysis and Card Action Analysis
     */
    public static final int EMV_PROC_TO_1GENAC = 6;
    /**
     * Script Processing and Completion
     */
    public static final int EMV_PROC_TO_2GENAC = 7;
    /**
     * Execute EMV step until end
     */
    public static final int EMV_PROC_CONTINUE = 8;

    /**
     * Transaction type: GOODS
     */
    public static final int EMV_TRANS_GOODS = (0x01);
    /**
     * Transaction type: SERVICES
     */
    public static final int EMV_TRANS_SERVICES = (0x02);
    /**
     * Transaction type: CASH
     */
    public static final int EMV_TRANS_CASH = (0x03);
    /**
     * Transaction type: CASHBACK
     */
    public static final int EMV_TRANS_CASHBACK = (0x04);
    /**
     * Transaction type: INQUIRY
     */
    public static final int EMV_TRANS_INQUIRY = (0x05);
    /**
     * Transaction type: TRANFER
     */
    public static final int EMV_TRANS_TRANFER = (0x06);
    /**
     * Transaction type: ADMIN
     */
    public static final int EMV_TRANS_ADMIN = (0x07);
    /**
     * Transaction type: CASHDEPOSIT
     */
    public static final int EMV_TRANS_CASHDEPOSIT = (0x08);
    /**
     * Transaction type: PAYMENT
     */
    public static final int EMV_TRANS_PAYMENT = (0x09);


    /**
     * Entry Point: good
     */
    public static final int EMV_TRANS_EP_PURCHASE = 0x00;
    /**
     * Entry Point: cash
     */
    public static final int EMV_TRANS_EP_CASH_ADVANCE = 0x01;
    /**
     * Entry Point: cash back
     */
    public static final int EMV_TRANS_EP_PURCHASE_CASHBACK = 0x09;
    /**
     * Entry Point: refund
     */
    public static final int EMV_TRANS_EP_REFUND = 0x20;
    /**
     * Manual cash
     */
    public static final int EMV_TRANS_EP_MANUAL_CASH = 0x12;

    /**
     * PBOC LOG
     */
    public static final int EMV_TRANS_PBOCLOG = (0x0A);
    /**
     * SALE
     */
    public static final int EMV_TRANS_SALE = (0x0B);
    /**
     * PREAUTH
     */
    public static final int EMV_TRANS_PREAUTH = (0x0C);
    /**
     * BALANCE
     */
    public static final int EMV_TRANS_BALANCE = (0x0D);
    /**
     * ECLOADLOG
     */
    public static final int EMV_TRANS_ECLOADLOG = (0x0E);

    /**
     * EC goods
     */
    public static final int EMV_TRANS_EC_GOODS = (EMV_TRANS_GOODS);
    /**
     * EC services
     */
    public static final int EMV_TRANS_EC_SERVICES = (EMV_TRANS_SERVICES);
    /**
     * EC sale
     */
    public static final int EMV_TRANS_EC_SALE = (EMV_TRANS_SALE);
    /**
     * EC bindload
     */
    public static final int EMV_TRANS_EC_BINDLOAD = (0x21);
    /**
     * EC nobindload
     */
    public static final int EMV_TRANS_EC_NOBINDLOAD = (0x22);
    /**
     * EC cashload
     */
    public static final int EMV_TRANS_EC_CASHLOAD = (0x23);
    /**
     * EC upload (nonsupport)
     */
    public static final int EMV_TRANS_EC_UPLOAD = (0x24);
    /**
     * PBOC LOG
     */
    public static final int EMV_TRANS_EC_INQUIRE_LOG = (EMV_TRANS_PBOCLOG);
    /**
     * EC BALANCE
     */
    public static final int EMV_TRANS_EC_INQUIRE_AMOUNT = (0x25);
    /**
     * EC cashload void
     */
    public static final int EMV_TRANS_EC_CASHLOAD_VOID = (0x26);

    /**
     * QPBOC/MSD Trans type
     */
    public static final int EMV_TRANS_RF_START = (0x30);

    /**
     * QPBOC/MSD goods
     */
    public static final int EMV_TRANS_RF_GOODS = (EMV_TRANS_GOODS);

    /**
     * QPBOC/MSD services
     */
    public static final int EMV_TRANS_RF_SERVICES = (EMV_TRANS_SERVICES);

    /**
     * QPBOC/MSD sale
     */
    public static final int EMV_TRANS_RF_SALE = (EMV_TRANS_SALE);

    /**
     * Contactless EC bindload
     */
    public static final int EMV_TRANS_RF_BINDLOAD = (0x31);

    /**
     * Contactless EC nobindload
     */
    public static final int EMV_TRANS_RF_NOBINDLOAD = (0x32);

    /**
     * Contactless EC cashload
     */
    public static final int EMV_TRANS_RF_CASHLOAD = (0x33);

    /**
     * QPBOC EC BALANCE
     */
    public static final int EMV_TRANS_RF_INQUIRE_AMOUNT = (0x34);

    /**
     * Contactless EC upload (nonsupport)
     */
    public static final int EMV_TRANS_RF_UPLOAD = (0x35);

    /**
     * Contactless EC cashload void
     */
    public static final int EMV_TRANS_RF_CASHLOAD_VOID = (0x36);

    /**
     * Contactless PBOC LOG
     */
    public static final int EMV_TRANS_RF_PBOCLOG = (0x37);

    /**
     * Card information writing
     */
    public static final int EMV_TRANS_RF_UPTCARDINFO = (0x38);

    public static final int EMV_TRANS_RF_PBOC_SALE = (0x39);
    /**
     * Contactless ECLOAD LOG
     */
    public static final int EMV_TRANS_RF_ECLOADLOG = (0x40);


    /**
     * Transaction Return value: None
     */
    public static final int EMVL2_ERR_NONE = (0);
    /**
     * Transaction Return value: Failed
     */
    public static final int EMVL2_ERR_FAIL = (-1);
    /**
     * Transaction Return value: Parameter error
     */
    public static final int EMVL2_ERR_PARAM = (-2);
    /**
     * Transaction Return value: Format error
     */
    public static final int EMVL2_ERR_FORMAT = (-3);
    /**
     * Transaction Return value: Overflow
     */
    public static final int EMVL2_ERR_OVERFLOW = (-4);

    /**
     * Transaction Return value: Terminate
     */
    public static final int EMVL2_ERR_TERMINATE = (-10);
    /**
     *
     */
    public static final int EMVL2_ERR_SELECT_NEXT = (-11);
    /**
     * The application must accept the session
     */
    public static final int EMVL2_ERR_ACCEPT = (-12);
    /**
     * The application must decline the session
     */
    public static final int EMVL2_ERR_DECLINE = (-13);
    /**
     * Indicate entry point to process online
     */
    public static final int EMVL2_ERR_GO_ONLINE = (-14);
    /**
     * Should send second GAC command and request AAC
     */
    public static final int EMVL2_ERR_GAC2_AAC = (-15);
    /**
     * App blocked
     */
    public static final int EMVL2_ERR_APP_BLOCK = (-16);
    /**
     * No more apps
     */
    public static final int EMVL2_ERR_NO_MORE_APPS = (-17);
    /**
     * Card blocked
     */
    public static final int EMVL2_ERR_CARD_BLOCK = (-18);

    public static final int EMVL2_ERR_TAG_ABSENT = (-100);
    public static final int EMVL2_ERR_TAG_REPEAT = (-101);
    public static final int EMVL2_ERR_TAG_UNKNOWN = (-102);

    public static final int EMVL2_ERR_AUTO_SELECT = (-200);
    public static final int EMVL2_ERR_NEED_CONFIRM = (-201);

    public static final int EMVL2_ERR_CAPK_CHECKSUM = (-300);

    /**
     * CVM processing is completed
     */
    public static final int EMVL2_CVM_DONE = 0x00;
    /**
     * Plaintext PIN verification performed by ICC
     */
    public static final int EMVL2_CVM_OFFLINE_PIN = 0x01;
    /**
     * Enciphered PIN verified online
     */
    public static final int EMVL2_CVM_ONLINE_PIN = 0x02;
    /**
     * Plaintext PIN verification performed by ICC and signature(paper)
     */
    public static final int EMVL2_CVM_OFFLINE_PIN_SIGNATURE = 0x03;
    /**
     * Enciphered PIN verification performed by ICC
     */
    public static final int EMVL2_CVM_ENCIPHERED_PIN = 0x04;
    /**
     * Enciphered PIN verification performed by ICC and signature(paper)
     */
    public static final int EMVL2_CVM_ENCIPHERED_PIN_SIGNATURE = 0x05;
    /**
     * ExtSignatureInfo(paper)
     */
    public static final int EMVL2_CVM_SIGNATURE = 0x1E;
    /**
     * Need application to process custom cv condition
     */
    public static final int EMVL2_CVM_CUSTOM_CONDITION = 0x1F;
    /**
     * Cardholder ID Verification(PBOC transaction, non-EMV CVM Method)
     */
    public static final int EMVL2_CVM_CARDHOLDER_ID = 0x20;

    /**
     * Custom CVM perform OK.
     */
    public static final int EMVL2_CVS_OK = 0x00;
    /**
     * PIN pad absent or malfunction in any reason. (TVR bit will be set)
     */
    public static final int EMVL2_CVS_PINPAD_ABSENT_OR_MALFUNCTION = 0x01;
    /**
     * PIN entry bypassed on external PED or internal PED. (TVR bit will be set)
     */
    public static final int EMVL2_CVS_PIN_BYPASSED = 0x02;
    /**
     * PIN entry cancelled on external PED or internal PED. The transaction processing should be cancelled.
     */
    public static final int EMVL2_CVS_PIN_CANCELLED = 0x03;
    /**
     * Offline PIN verification completed successfully on external PED or internal PED if they have EMV function.
     */
    public static final int EMVL2_CVS_OFFLINEPIN_VERIFICATION_OK = 0x04;
    /**
     * Offline PIN verification failed, sw1sw2 is not 9000, 63C0, 6983, 6984 or any error undefined. The transaction must be terminated.
     */
    public static final int EMVL2_CVS_OFFLINEPIN_VERIFICATION_FAIL = 0x05;
    /**
     * Online PIN entered and successful get the cipher text.
     */
    public static final int EMVL2_CVS_ONLINEPIN_OK = 0x0A;
    /**
     * Custom CVM condition is unsatisfied.
     */
    public static final int EMVL2_CVS_UNSATISFY = 0x10;
    /**
     * Unsupported custom CVM.
     */
    public static final int EMVL2_CVS_UNSUPPORT = 0x11;
    /**
     * Unrecognized custom CVM.
     */
    public static final int EMVL2_CVS_UNRECOGNIZE = 0x12;
    /**
     * Custom CVM perform failed.
     */
    public static final int EMVL2_CVS_FAIL = 0x13;

    /**
     * AC type: AAC
     */
    public static final int EMVL2_AAC = 0x00;
    /**
     * AC type: TC
     */
    public static final int EMVL2_TC = 0x01;
    /**
     * AC type: ARQC
     */
    public static final int EMVL2_ARQC = 0x02;

    /**
     * Online failed
     */
    public static final int EMV_TRANS_ONLINEFAIL = (5);

    /**
     * Online success and transaction acceptance
     */
    public static final int EMV_TRANS_ONLINESUCC_ACCEPT = (6);

    /**
     * Online success and transaction denial
     */
    public static final int EMV_TRANS_ONLINESUCC_DENIAL = (7);

    /**
     * Online success and return reference
     */
    public static final int EMV_TRANS_ONLINESUCC_ISSREF = (8);
    public static final int UI_MSGID_APPROVED = 0x03;
    public static final int UI_MSGID_DECLINED = 0x07;
    public static final int UI_MSGID_PLEASE_ENTER_YOURE_PIN = 0x09;
    public static final int UI_MSGID_PROCESSING_ERROR = 0x0F;
    public static final int UI_MSGID_REMOVE_CARD = 0x10;
    public static final int UI_MSGID_TRY_AGAIN = 0x11;
    public static final int UI_MSGID_WELCOME = 0x14;
    public static final int UI_MSGID_PRESENT_CARD = 0x15;
    public static final int UI_MSGID_PROCESSING = 0x16;
    public static final int UI_MSGID_CARD_READ_OK = 0x17;
    public static final int UI_MSGID_INSERT_OR_SWIPE_CARD = 0x18;
    public static final int UI_MSGID_PRESENT_ONE_CARD_ONLY = 0x19;
    public static final int UI_MSGID_APPROVED_SIGN = 0x1A;
    public static final int UI_MSGID_AUTHORISING_PLEASE_WAIT = 0x1B;
    public static final int UI_MSGID_ERROR_OTHER_CARD = 0x1C;
    public static final int UI_MSGID_INSERT_CARD = 0x1D;
    public static final int UI_MSGID_CLEAR_DISPLAY = 0x1E;
    public static final int UI_MSGID_SEE_PHONE = 0x20;
    public static final int UI_MSGID_PRESENT_CARD_AGAIN = 0x21;
    public static final int UI_MSGID_USE_ANOTHER_CARD = 0x22;
    public static final int  UI_MSGID_CANNOT_CONTACTLESS=0x31;//intarac Cannot process contactless transaction 
    public static final int  UI_MSGID_SEE_ATTENDANT	=0x32;//interac  please see attendant
    public static final int  UI_MSGID_TOO_MANY_TAPS=0x33;//interac Cannot process transaction -too many taps
    public static final int  UI_MSGID_PAYMENT_NOTACCEPT=0x34;//interac Payment Type Not Accepted
    public static final int  UI_MSGID_GOTOCOMPLETE=0x35;//rupay goto complete
    public static final int  UI_MSGID_LIMITREACH_INSERT=0x36;//interac insert card becasue of limit reach
    public static final int  UI_MSGID_AUTH_PHONE=0x37;//dpas Please authenticate yourself to your device and try again
    public static final int UI_MSGID_NA = 0xFF;
    /***Paypass define Status  *****/
    public static final int UI_STATUS_NOT_READY = 0x00;
    public static final int UI_STATUS_IDLE = 0x01;
    public static final int UI_STATUS_READY_TO_READ = 0x02;
    public static final int UI_STATUS_PROCESSING = 0x03;
    public static final int UI_STATUS_CARD_READ_SUCCESSFULLY = 0x04;
    public static final int UI_STATUS_PROCESSING_ERROR = 0x05;
    public static final int UI_STATUS_STATUS_NA = 0xFF;
/*mir PROCESSING ERRORs*/
    public static final int UI_STATUS_GAC_NOANSWER =0x06;
    public static final int UI_STATUS_GAC_BADANSWER =0x07;
    public static final int UI_STATUS_GAC_BADCID =0x08;
    public static final int UI_STATUS_PERFORM_RECOVERY_NOSUPPORT =0x10;
    public static final int UI_STATUS_PERFORM_RECOVERY_LIMITEXCEED =0x11;
    public static final int UI_STATUS_PERFORM_BADSW =0x12;
    public static final int UI_STATUS_COMPLETE_RECOVERY_NOSUPPORT =0x20;
    public static final int UI_STATUS_COMPLETE_RECOVERY_LIMITEXCEED =0x21;
    public static final int UI_STATUS_COMPLETE_BADSW =0x22;
    public static final int UI_STATUS_READ_RECORD_RECOVERY_NOSUPPORT =0x30;
    public static final int UI_STATUS_READ_RECORD_RECOVERY_LIMITEXCEED =0x31;
    /***Paypass define Value Qualifier   *****/
    public static final int UI_VALUE_QUALIFIER_NONE = 0x00;
    public static final int UI_VALUE_QUALIFIER_AMOUNT = 0x10;
    public static final int UI_VALUE_QUALIFIER_BALANCE = 0x20;
    /***A.1.68 Error Indication   'DF8115'  6 ***/
    public static final int ER_L1_OK = 0x00;
    public static final int ER_L1_TIMEOUT = 0x01;
    public static final int ER_L1_TRANSMISSION = 0x02;
    public static final int ER_L1_PROTOCOL = 0x03;
    public static final int ER_L2_OK = 0x00;
    public static final int ER_L2_CARD_DATA_MISSING = 0x01;
    public static final int ER_L2_CAM_FAILD = 0x02;
    public static final int ER_L2_STATUS_BYTES = 0x03;
    public static final int ER_L2_PARSING_ERROR = 0x04;
    public static final int ER_L2_MAX_LIMIT_EXCEEDED = 0x05;
    public static final int ER_L2_CARD_DATA_ERROR = 0x06;
    public static final int ER_L2_MAGSTRIPE_NOT_SUPPORTED = 0x07;
    public static final int ER_L2_NO_PPSE = 0x08;
    public static final int ER_L2_PPSE_FAULT = 0x09;
    public static final int ER_L2_EMPTY_CANDIDATE_LIST = 0x0A;
    public static final int ER_L2_IDS_READ_ERROR = 0x0B;
    public static final int ER_L2_IDS_WRITE_ERROR = 0x0C;
    public static final int ER_L2_IDS_DATA_ERROR = 0x0D;
    public static final int ER_L2_IDS_NO_MATCHING_AC = 0x0E;
    public static final int ER_L2_TERMINAL_DATA_ERROR = 0x0F;
    public static final int ER_L3_OK = 0x00;
    public static final int ER_L3_TIME_OUT = 0x01;
    public static final int ER_L3_STOP = 0x02;
    public static final int ER_L3_AMOUNT_NOT_PRESENT = 0x03;
    /**
     * A.1.110  Outcome Parameter Set  Tag:  'DF8129' Length:  8
     **/
    /*Byte 1 bit8-5 Status*/
    public static final int OP_STATUS_APPROVED = 0x10;
    public static final int OP_STATUS_DECLINED = 0x20;
    public static final int OP_STATUS_ONLINE_REQUEST = 0x30;
    public static final int OP_STATUS_END_APPLICATION = 0x40;
    public static final int OP_STATUS_SELECT_NEXT = 0x50;
    public static final int OP_STATUS_TRY_ANOTHER_INTERFACE = 0x60;
    public static final int OP_STATUS_TRY_AGAIN = 0x70;
    public static final int OP_STATUS_NA = 0xF0;
    /*Byte 2 bit8-5 Start*/
    public static final int OP_START_A = 0x00;
    public static final int OP_START_B = 0x10;
    public static final int OP_START_C = 0x20;
    public static final int OP_START_D = 0x30;
    public static final int OP_START_NA = 0xF0;
    /*Byte 3 bit8-5 Online Response Data*/
    public static final int OP_ONLINE_RESPONSE_DATA_NA = 0xF0;
    /*Byte 4 bit8-5 CVM*/
    public static final int OP_NO_CVM = 0x00;
    public static final int OP_OBTAIN_SIGNATURE = 0x10;
    public static final int OP_ONLINE_PIN = 0x20;
    public static final int OP_CONFIRMATION_CODE_VERIFIED = 0x30;
    public static final int OP_CVM_NA = 0xF0;
    /**
     * Byte 5 bit8-4, User Interface Request Data
     */
    public static final int OP_UI_REQUEST_ON_OUTCOME_PRESENT = 0x80;
    public static final int OP_UI_REQUEST_ON_RESTART_PRESENT = 0x40;
    public static final int OP_DATA_RECORD_PRSENT = 0x20;
    public static final int OP_DISCRETIONARY_DATA_PRESENT = 0x10;
    /**
     * <ul>
     *     <li>0: N/A</li>
     *     <li>1: YES</li>
     * </ul>
     */
    public static final int OP_RECEIPT = 0x40;
    /**
     * Byte 6 bit8-5 Alternate Interface Preference
     */
    public static final int OP_ALTERNATE_INTERFACE_PREFERENCE = 0xF0;
    public static final int OP_ALTERNATE_INTERFACE_CONTACTCHIP	=0x01;
    public static final int OP_ALTERNATE_INTERFACE_MAGSTRIP=0x02;
    /**
     * Byte 7 bit8-1 Field Off Request
     * <ul>
     *     <li>FF: N/A</li>
     *     <li>Other value: Hold time in units of 100ms</li>
     * </ul>
     */
    public static final int OP_FIELD_OFF_REQUEST = 0xFF;
    /**
     * Byte 8 bit8-1 Removal Timeout
     */
    public static final int OP_REMOVAL_TIMEOUT = 0xFF;
    /**
     * Transaction terminate
     */
    public static final int EMV_TRANS_RF_TERMINATE = -1;
    /**
     * RF M/CHIP transaction success
     */
    public static final int EMV_TRANS_RF_MCHIP_ACCEPT = 11;
    /**
     * RF M/CHIP transaction denial
     */
    public static final int EMV_TRANS_RF_MCHIP_DENIAL = 12;
    /**
     * RF M/CHIP transaction go online
     */
    public static final int EMV_TRANS_RF_MCHIP_GOONLINE = 13;
    /**
     * RF Mag Stripe transaction go online
     */
    public static final int EMV_TRANS_RF_MAG_GOONLINE = 14;
    /**
     * RF Mag Stripe transaction aproved (refund transaction)
     */
    public static final int EMV_TRANS_RF_MAG_ACCEPT = 15;
    /**
     * RF active card
     */
    public static final int EMV_TRANS_RF_ACTIVE_CARD = 16;
    /**
     * RF try another interface
     */
    public static final int EMV_TRANS_RF_TRYOTHERINT = 17;
    public static final int EMV_TRANS_RF_ACTIVE_KERNEL = 18;
    /**
     * Exit kernel and select next AID
     */
    public static final int EMV_TRANS_RF_SELECT_NEXT_AID = 19;
    public static final int EMV_TRANS_RF_MAG_DENIAL = 20;
    // expresspay
    public static final int EMV_TRANS_RF_CONTACTLESS_NOTPER = 21;
    /**
     * GPO, SW=6986, used for paywave, unnecessary
     */
    public static final int EMV_TRANS_DOWNCARD = -15;
    /**
     * RF M/CHIP transaction go online
     */
    public static final int EMV_TRANS_RF_MCHIP_GOONLINE_LONGTAP = 22;
    /**
     * RF M/CHIP transaction go online
     */
    public static final int EMV_TRANS_RF_MCHIP_GOONLINE_ONLINETAP = 23;
    /*JCB*/
    public static final int EMV_TRANS_RF_GOONLINE_Hold  =24;        	/*RF transaction go online, Present and Hold */
    public static final int EMV_TRANS_RF_GOONLINE_2Present =25;      	/*RF transaction go online, Two Present*/
    public static final int EMV_TRANS_RF_ENDAPP_ONDEVCVM	=26;			/*End Application whith restart, on-device CVM*/
    public static final int EMV_TRANS_RF_ENDAPP_COMMERR	=27;			/*End Application whith restart, communication errors*/
    /*rupay*/
    public static final int EMV_TRANS_RF_1GAC_AAC  =28;       	/**<First Generate AC return AAC */
    public static final int EMV_TRANS_RF_ONLINETAP_ONLINEFAIL =29;        	/**<OnlineTap OnlineFail*/

    /********** flow control flag********************/
    public static final int EMV_TRANS_GOTOTERMINATE = 0;
    public static final int EMV_TRANS_SENDMESSAGE = 30;
    public static final int KERNEL_ID_PAYPASS = 0x02;
    public static final int KERNEL_ID_PAYWAVE = 0x03;
    public static final int KERNEL_ID_EXPRESSPAY = 0x04;
    public static final int KERNEL_ID_JCB = 0x05;
    public static final int KERNEL_ID_DISCOVER = 0x06;
    public static final int KERNEL_ID_UNIONPAY = 0x07;
    public static final int KERNEL_ID_RUPAY = 0x0D;
    public static final int KERNEL_ID_MCCS = 0x20;
    public static final int KERNEL_ID_INTERAC = 0x21;
    public static final int KERNEL_ID_MADA = 0x2D;
    public static final int KERNEL_ID_CPACE = 0x2E;
    public static final int KERNEL_ID_MIR = 0x810643;
    public static final int KERNEL_ID_MULTIBANCO = 0xC14D42;

    public static final int KERNEL_MASTERCARD = 0x02;
    public static final int KERNEL_VISA = 0x03;
    public static final int KERNEL_AMEX = 0x04;
    public static final int KERNEL_JCB = 0x05;
    public static final int KERNEL_DISCOVER = 0x06;
    public static final int KERNEL_UNIONPAY = 0x07;
    public static final int KERNEL_RUPAY = 0x0D;
    public static final int KERNEL_MCCS = 0x20;
    public static final int KERNEL_INTERAC = 0x21;
    public static final int KERNEL_MADA = 0x2D;
    public static final int KERNEL_BANCOMAT_LEGACY = 0x3D;
    public static final int KERNEL_CPACE = 0x2E;
    public static final int KERNEL_MIR = 0x810643;
    public static final int KERNEL_MULTIBANCO = 0xC14D42;
    public static final int KERNEL_EFTPOS = 0x810744;
	
    public static final int KERNEL_BANCOMAT_DIGITAL = 0xCD5055;
    public static final int KERNEL_IRAN_PURE = (0x8A0682);
    public static final int TRANS_MODE = 0xDF1F;
    /*EMV*/
//		public static final int TRANS_MODE_EMV = 1;
//		public static final int TRANS_MODE_ECASH = 2;
    /*Unionpay*/
    public static final int TRANS_MODE_UNIONPAY_QPBOC = 0x06;

    public static final int TRANS_MODE_UNIONPAY_PBOC = 0x07;
    public static final int TRANS_MODE_UNIONPAY_MSD = 0x08;
    /*Paywave*/
    public static final int TRANS_MODE_PAYWAVE_QVSDC = 0x0B;
    public static final int TRANS_MODE_PAYWAVE_VSDC = 0x0C;
    public static final int TRANS_MODE_PAYWAVE_WAVE2 = 0x0D;
    public static final int TRANS_MODE_PAYWAVE_MSD = 0x0E;
    public static final int TRANS_MODE_PAYWAVE_MSD_LEGACY = 0x0F;
    /*paypass*/
    public static final int TRANS_MODE_PAYPASS_MCHIP = 0x10;
    public static final int TRANS_MODE_PAYPASS_MSTRIPE = 0x11;
    /*D-PAS*/
    public static final int TRANS_MODE_DPAS_EMV = 0x15;
    public static final int TRANS_MODE_DPAS_MSTRIPE = 0x16;
    public static final int TRANS_MODE_DPAS_ZIP = 0x17;
    /*AMEX*/
    public static final int TRANS_MODE_EXPRESSPAY_EMV = 0x1A;
    public static final int TRANS_MODE_EXPRESSPAY_MSTRIPE = 0x1B;
    public static final int TRANS_MODE_EXPRESSPAY_MOBLIE_EMV = 0x1C;
    public static final int TRANS_MODE_EXPRESSPAY_MOBLIE_MSTRIPE = 0x1D;
    /*JCB*/
    public static final int TRANS_MODE_JCB_EMV = 0x1F;
    public static final int TRANS_MODE_JCB_MSTRIPE = 0x20;
    public static final int TRANS_MODE_JCB_LEGACY = 0x21;
    /*MCCS pure*/
    public static final int TRANS_MODE_PURE_EMV = 0x24;
    /*Interac*/
    public static final int TRANS_MODE_INTERAC_EMV = 0x29;
    /*Rupay*/
    public static final int TRANS_MODE_RUPAY_EMV = 0x2E;
	/*MIR*/
    public static final int TRANS_MODE_MIR_EMV = 0x33;
    public static final int TRANS_MODE_MIR_EMV_PROTOCOL01 = 0x33;
    public static final int TRANS_MODE_MIR_EMV_PROTOCOL02 = 0x34;
	/*Multibanco*/
    public static final int TRANS_MODE_MULTIBANCO_EMV = 0x38;
/*Bancomat Legacy*/
    public static final int TRANS_MODE_BANCOMAT_LEGACY_EMV = 0x3D;
	/*Cpace*/
    public static final int TRANS_MODE_CPACE_EMV=0x42;
	/*eftpos*/
    public static final int TRANS_MODE_EFTPOS_EMV=0x4C;
	
    /**
     * STENTRYPOINTOPT.ucCtrl
     * implementation of PSE with selection of kernel based on DF61 if 9F2A is missing
     */
    public static final int EMV_EP_CTRL_CK_DF61 = 0x01;

    public static final int EMV_EP_CTRL_CK_TERMINAL_PRIORITY = 0x02;
    public static final int EMV_EP_CTRL_CK_CANDIDATE_LIST = 0x04;
    public static final int EMV_EP_CTRL_CK_CANDIDATE_LIST_ALL = 0x08;
    public static final int EMV_EP_CTRL_CK_NEWDATE = 0x20;
}
