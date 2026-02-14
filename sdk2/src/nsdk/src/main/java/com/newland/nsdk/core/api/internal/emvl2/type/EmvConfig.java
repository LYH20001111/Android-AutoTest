package com.newland.nsdk.core.api.internal.emvl2.type;

/**
 * EMV config tags.
 */
public class EmvConfig {
    /**
     * Default Terminal Action Code, n5
     */
    public static final int _EMVPARAM_DF11_TACDEFAULT = 0xDF11;
    /**
     * Denial Terminal Action Code, n5
     */
    public static final int _EMVPARAM_DF13_TACDENIAL = 0xDF13;
    /**
     * Online Terminal Action Code, n5
     */
    public static final int _EMVPARAM_DF12_TACONLINE = 0xDF12;
    /**
     * target percent, n1
     */
    public static final int _EMVPARAM_DF17_TARGETPER = 0xDF17;
    /**
     * Max target percent, n1
     */
    public static final int _EMVPARAM_DF16_MAXTARPER = 0xDF16;
    /**
     * Threshold value, n4
     */
    public static final int _EMVPARAM_DF15_THRESHOLDVA = 0xDF15;
    /**
     * Transaction reference currency convert, n4, default 0
     */
    public static final int _EMVPARAM_DF22_TRANSCONV = 0xDF22;
    /**
     * Script length limit, n1, default 0
     */
    public static final int _EMVPARAM_DF23_SCRDEV = 0xDF23;
    /**
     * ICS (Implementation Conformance Statement), b, 7
     */
    public static final int _EMVPARAM_DF24_ICS = 0xDF24;
    /**
     * Test type indicator, n1
     */
    public static final int _EMVPARAM_DF25_STATUS = 0xDF25;
    /**
     * 9F7A if Terminal support EC, n1
     */
    public static final int _EMVPARAM_9F7A_ECIND = 0x9F7A;
    /**
     * 9F35(Terminal), n2, 1
     */
    public static final int _EMVPARAM_9F35_TYPE = 0x9F35;
    /**
     * 9F33(Terminal), b, 3
     */
    public static final int _EMVPARAM_9F33_CAP = 0x9F33;
    /**
     * 9F40(Terminal), b, 5
     */
    public static final int _EMVPARAM_9F40_ADDCAP = 0x9F40;
    /**
     * 4F(ICC), 9F06(Terminal), b, 5-16 bytes
     */
    public static final int _EMVPARAM_9F06_AID = 0x9F06;
    /**
     * 9F09(Terminal), b, 2 bytes
     */
    public static final int _EMVPARAM_9F09_APPVER = 0x9F09;
    /**
     * 9F39(Terminal), n2, 1 bytes
     */
    public static final int _EMVPARAM_9F39_POSENTRY = 0x9F39;
    /**
     * 9F1B(Terminal), b, 4 bytes
     */
    public static final int _EMVPARAM_9F1B_FLOORLIMIT = 0x9F1B;
    /**
     * 9F01(Terminal), n6-11, 6 bytes
     */
    public static final int _EMVPARAM_9F01_ACQID = 0x9F01;
    /**
     * 9F15(Terminal), n4, 2 bytes
     */
    public static final int _EMVPARAM_9F15_MERCACODE = 0x9F15;
    /**
     * 9F16(Terminal), ans15, 15 bytes
     */
    public static final int _EMVPARAM_9F16_MERCHID = 0x9F16;
    /**
     * 5F2A(Terminal), n3, 2 bytes
     */
    public static final int _EMVPARAM_5F2A_TRANSCCODE = 0x5F2A;
    /**
     * 5F36(Terminal), n1, 1 bytes
     */
    public static final int _EMVPARAM_5F36_TRANSCEXP = 0x5F36;
    /**
     * 9F3C(Terminal), n3, 2 bytes
     */
    public static final int _EMVPARAM_9F3C_TRANSREFCCODE = 0x9F3C;
    /**
     * 9F3D(Terminal), n1, 1 bytes
     */
    public static final int _EMVPARAM_9F3D_TRANSREFCEXP = 0x9F3D;
    /**
     * 9F1A(Terminal), n3, 2 bytes
     */
    public static final int _EMVPARAM_9F1A_TERMCCODE = 0x9F1A;
    /**
     * 9F1E(Terminal), an8, 8 bytes
     */
    public static final int _EMVPARAM_9F1E_IFDSERNUM = 0x9F1E;
    /**
     * Transaction Date
     */
    public static final int _EMVPARAM_9A_TRANSDATE = 0x9A;
    /**
     * Transaction Time
     */
    public static final int _EMVPARAM_9F21_TRANSTIME = 0x9F21;
    /**
     * Transaction Type
     */
    public static final int _EMVPARAM_9C_TRANSTYPE = 0x9C;
    /**
     * Unpredictable Number
     */
    public static final int _EMVPARAM_9F37_UNPNUM = 0x9F37;
    /**
     * Dedicated File Name
     */
    public static final int _EMVPARAM_84_DFNAME = 0x84;
    /**
     * Cryptogram Information Data
     */
    public static final int _EMVPARAM_9F27_CID = 0x9F27;
    /**
     * Account type()
     */
    public static final int _EMVPARAM_5F57_ACOUNTTYPE = 0x5F57;
    /**
     * Cardholder certificate identify
     */
    public static final int _EMVPARAM_9F61_HOLDERCERTID = 0x9F61;
    /**
     * Cardholder certification type
     */
    public static final int _EMVPARAM_9F62_HOLDERCERTTYPE = 0x9F62;
    /**
     * EC issuer response auth code
     */
    public static final int _EMVPARAM_9F74_EC_RESPAUTHCODE = 0x9F74;
    /**
     * EC threshold
     */
    public static final int _EMVPARAM_9F6D_EC_THRESHOLD = 0x9f6d;
    /**
     * EC balance
     */
    public static final int _EMVPARAM_9F79_EC_BALANCE_IN = 0x9F79;
    /**
     * Amount, Authorised<Binary>
     */
    public static final int _EMVPARAM_81_AUTHAMNTB = 0x81;
    /**
     * ARC(Authorisation Response Code)
     */
    public static final int _EMVPARAM_8A_ARC = 0x8A;
    /**
     * Amount, Authorised<Binary>
     */
    public static final int _EMVPARAM_9F02_AUTHAMNTN = 0x9F02;
    /**
     * Amount,Other<Binary>
     */
    public static final int _EMVPARAM_9F04_OTHERAMNTB = 0x9F04;
    /**
     * Amount,Other<Numeric>
     */
    public static final int _EMVPARAM_9F03_OTHERAMNTN = 0x9F03;
    /**
     * Transcation Sequence Counter
     */
    public static final int _EMVPARAM_9F41_TRSEQCNTR = 0x9F41;
    /**
     * Application Primary Account Number
     */
    public static final int _EMVPARAM_5A_PAN = 0x5A;
    /**
     * PAN_SN(Application Primary Account Number Sequence Number)
     */
    public static final int _EMVPARAM_5F34_PANSN = 0x5F34;
    /**
     * 9F1C(Terminal), an8, 8 bytes
     */
    public static final int _EMVPARAM_9F1C_TERMID = 0x9F1C;
    /**
     * Default ddol, var
     */
    public static final int _EMVPARAM_DF44_DEDDOL = 0xDF44;
    /**
     * Default tdol, var
     */
    public static final int _EMVPARAM_DF45_DETDOL = 0xDF45;
    /**
     * Application select indicator, n1
     */
    public static final int _EMVPARAM_DF01_APPSELIND = 0xDF01;
    /**
     * Fallback pos entry, n1
     */
    public static final int _EMVPARAM_DF26_FALLPOTERY = 0xDF26;
    /**
     * Limit exist?(To determine the identity of the following limit exists), b8, 1
     */
    public static final int _EMVPARAM_DF27_LIMITEXIST = 0xDF27;
    /**
     * Contactless terminal transaction limit n12, 6bytes
     */
    public static final int _EMVPARAM_DF20_CLLIMMIT = 0xDF20;
    /**
     * Contactless terminal offline minimum limit n12, 6bytes
     */
    public static final int _EMVPARAM_DF19_CLOFFLIMIT = 0xDF19;
    /**
     * Terminal implement CVM Limit, n12, 6bytes
     */
    public static final int _EMVPARAM_DF21_CVMLIMT = 0xDF21;
    /**
     * 9F66 Terminal transaction attribute ,b32, 4bytes
     */
    public static final int _EMVPARAM_9F66_TRANSPROP = 0x9F66;
    /**
     * The default of contactless status check is 0
     */
    public static final int _EMVPARAM_DF29_STATUSCHECK = 0xDF29;
    /**
     * Application ID
     */
    public static final int _EMVPARAM_DF2A_APPID = 0xDF2A;
    /**
     * 9F4E(Terminal), ans20, 20 bytes
     */
    public static final int _EMVPARAM_9F4E_MERCNAME = 0x9F4E;
    /**
     * Default ddol (Paypass+)
     */
    public static final int _EMVPARAM_DF2B_DEUDOL = 0xDF2B;
    /**
     * (Paypass+) Support mag stripe ?  1 support ;  0 not support [Paypass]
     */
    public static final int _EMVPARAM_DF2C_MAGSTRIND = 0xDF2C;
    /**
     * Paypass mag stripe application version 9F6D
     */
    public static final int _EMVPARAM_DF2D_MAGAPPVER = 0xDF2D;
    /**
     * support data exchang or not
     */
    public static final int _EMVPARAM_DF2E_DEXCHANGE = 0xDF2E;
    /**
     * b8  Only EMV mode; b7  Only mag-stripe mode; b6 On device cardholder verification
     */
    public static final int _EMVPARAM_DF2F_KERNELCONF = 0xDF2F;
    /**
     * Max Number of Torn Transaction Log Records. !=0 support Torn
     */
    public static final int _EMVPARAM_DF32_MNUMTORN = 0xDF32;
    /**
     * 00,NO; bit1:Balance Read Before GAC; bit2:Balance Read After GAC
     */
    public static final int _EMVPARAM_DF33_BALANFLAG = 0xDF33;
    /**
     * Paywave config
     */
    public static final int _EMVPARAM_DF34_PWCONFIG = 0xDF34;
    /**
     * Paywave2 terminal require reader execute CVM
     */
    public static final int _EMVPARAM_DF35_CVMREQ = 0xDF35;
    /**
     * 0x00 support all dda version; 0x01 support only version 0x01
     */
    public static final int _EMVPARAM_DF36_DDAVER = 0xDF36;
    /**
     * Kernel id
     */
    public static final int _EMVPARAM_DF37_KERNELID = 0xDF37;
    /**
     * @deprecated
     */
    public static final int _EMVPARAM_DF38_VISATTQ = 0xDF38;
    /**
     * Status check
     */
    public static final int _EMVPARAM_DF39_STATUSCHECK = 0xDF39;
    /**
     * Zero amount allowed
     */
    public static final int _EMVPARAM_DF3A_ZEROALLOW = 0xDF3A;
    /**
     * Reserved config
     */
    public static final int _EMVPARAM_DF3B_EXAIDSUPP = 0xDF3B;
    /**
     * Contactless cardholder verification AID
     */
    public static final int _EMVPARAM_DF3C_CLSSCVA = 0xDF3C;
    /**
     * 0x00 Deactivated; 0x01 Activated
     */
    public static final int _EMVPARAM_DF3D_DRLSTATUS = 0xDF3D;
    /**
     * DRL Data len = 8*36
     */
    public static final int _EMVPARAM_DF3F_DRLDATA = 0xDF3F;

    /**
     * Mag-stripe CVM Capability – CVM Required
     */
    public static final int _EMVPARAM_DF42_MAGSCVM = 0xDF42;
    /**
     * Max Lifetime of Torn Transaction Log Record  0x012c
     */
    public static final int _EMVPARAM_DF43_MEXLTTORN = 0xDF43;
    /**
     * Mobile Support Indicator
     */
    public static final int _EMVPARAM_DF46_MOSUPPIND = 0xDF46;
    /**
     * Mag-stripe CVM Capability – No CVM Required
     */
    public static final int _EMVPARAM_DF47_MAGSNOCVM = 0xDF47;
    /**
     * CVM Capability – no CVM Required
     */
    public static final int _EMVPARAM_DF48_CAPNOCVM = 0xDF48;
    /**
     * expresspay 3.0 Terminal capabilities
     */
    public static final int _EMVPARAM_DF49_EXTERMCAP = 0xDF49;
    /**
     * expresspay 3.0 Random range
     */
    public static final int _EMVPARAM_DF4A_EXRANDOM = 0xDF4A;
    /**
     * expresspay 3.0 Timeout
     */
    public static final int _EMVPARAM_DF4B_EXTIMEEX = 0xDF4B;
    /**
     * For Paypass 3.0 test
     */
    public static final int _EMVPARAM_DF52_PPTLV = 0xDF52;
    /**
     * expresspay 3.1 drl limit
     */
    public static final int _EMVPARAM_DF53_EXDRLDATA = 0xDF53;

    /**
     * paypass 3.1 rrp Maximum Relay Resistance Grace Period
     */
    public static final int _EMVPARAM_DF54_MAXRRPGP = 0xDF54;
    /**
     * paypass 3.1 rrp Minimum Relay Resistance Grace Period
     */
    public static final int _EMVPARAM_DF55_MINRRPGP = 0xDF55;
    /**
     * paypass 3.1 rrp Relay Resistance Accuracy Threshold
     */
    public static final int _EMVPARAM_DF56_RRPAT = 0xDF56;
    /**
     * paypass 3.1 rrp Relay Resistance Transmission Time Mismatch Threshold
     */
    public static final int _EMVPARAM_DF57_RRPTTMT = 0xDF57;
    /**
     * paypass 3.1 rrp Terminal Expected Transmission Time For Relay Resistance C-APDU
     */
    public static final int _EMVPARAM_DF58_TETTFRRC = 0xDF58;
    /**
     * paypass 3.1 rrp Terminal Expected Transmission Time For Relay Resistance R-APDU
     */
    public static final int _EMVPARAM_DF59_TETTFRRR = 0xDF59;

    /**
     * Combination options
     */
    public static final int _EMVPARAM_DF60_COMBINATIONOPT = 0xDF60;
    /**
     * JCB Terminal Interchange Profile (static)
     */
    public static final int _EMVPARAM_9F53_TIP = 0x9F53;
    /**
     * Default MDOL
     */
    public static final int _EMVPARAM_DF61_MDOL = 0xDF61;
    /**
     * Pure Contactless App Cap
     */
    public static final int _EMVPARAM_DF62_CONTAPPCAP = 0xDF62;
    /**
     * Pure IO options
     */
    public static final int _EMVPARAM_DF63_IOOPTION = 0xDF63;
    /**
     * Pure terminal AID value
     */
    public static final int _EMVPARAM_DF64_TERMINALAIDVALUE = 0xDF64;
    /**
     * Pure memory slot read template
     */
    public static final int _EMVPARAM_BF71_MEMSLOTREADTEM = 0xBF71;
    /**
     * Pure memory slot update template
     */
    public static final int _EMVPARAM_BF70_MEMSLOTUPDATETEM = 0xBF70;
    /**
     * Terminal priority
     */
    public static final int _EMVPARAM_DF65_TERMINALPRIORITY = 0xDF65;
    /**
     * Terminal risk manage data
     */
    public static final int _EMVPARAM_9F1D_TRMDATA = 0x9F1D;
    /**
     * Pure terminal transaction data
     */
    public static final int _EMVPARAM_9F76_TERMTRANSDATA = 0x9F76;
    /**
     * Pure mtol
     */
    public static final int _EMVPARAM_DF66_MTOL = 0xDF66;
    /**
     * Pure atdtol
     */
    public static final int _EMVPARAM_DF79_ATDTOL = 0xDF79;
    /**
     * Pure postimeout transaction
     */
    public static final int _EMVPARAM_DF7A_POSTIMEOUTTRANS = 0xDF7A;
    /**
     * Pure auto run
     */
    public static final int _EMVPARAM_DF7B_AUTORUN = 0xDF7B;
    /**
     * Pure POSTIMEOUTLONG
     */
    public static final int _EMVPARAM_DF7C_POSTIMEOUTLONG = 0xDF7C;
    /**
     * Pure ATOL
     */
    public static final int _EMVPARAM_DF7E_ATOL = 0xDF7E;
    /**
     * Interac Terminal Transaction Information (TTI)
     */
    public static final int _EMVPARAM_9F59_TTI = 0x9F59;
    /**
     * Interac Terminal Option Status (TOS)
     */
    public static final int _EMVPARAM_9F5E_TOS = 0x9F5E;
    /**
     * Interac Merchant Type Indicator
     */
    public static final int _EMVPARAM_9F58_MTI = 0x9F58;
    /**
     * Interac Terminal Contactless Receipt Required Limit
     */
    public static final int _EMVPARAM_9F5D_TerConRecLimit = 0x9F5D;
    /**
     * Interac TTT
     */
    public static final int _EMVPARAM_9F5A_TTT = 0x9F5A;
    /**
     * Interac Retry Limit
     */
    public static final int _EMVPARAM_DF4C_INTERAC_RELimit = 0xDF4C;
    /**
     * Used for feature
     */
    public static final int _EMVPARAM_DF7F_EMVCONFIGRES = 0xDF7F;
    /**
     * Trans type check flag, used for paypass, n1
     */
    public static final int _EMVPARAM_1F8101_TRANSTYPECKFLAG = 0x1F8101;
    /**
     * ContaceLess Select by AID list, n1
     */
    public static final int _EMVPARAM_1F8102_PPSEAIDSEL = 0x1F8102;
	/* EMV,validate track 2 equivalent data against the PAN and expiry date*/
	public static final int _EMVPARAM_1F8118_VerifyTrackData =0x1F8118;
	public static final int _EMVPARAM_1F8119_ZeroFloorForCashBack =0x1F8119;    /* EMV,apply a zero value floor limit for cashback transactions*/
	public static final int _EMVPARAM_DF8151_DPASCTRL	 = 0xDF8151;	    /* DPAS ctrl flag*/
	public static final int _EMVPARAM_DF8152_DPASDATALIST =0xDF8152;	    /* DPAS Data Container Read List*/
	public static final int _EMVPARAM_DF8153_REPREPROCESS =0xDF8153;	    /* kernel do the pre process again, 1 byte*/
	public static final int _EMVPARAM_DF8165_AEC =0xDF8165;	    /* AEC (Allow Expired Card) , 1 byte*/
	public static final int _EMVPARAM_DF8166_TAED	 =0xDF8166;	    /* TAED (Threshold Application Expiration Date) ,3 byte*/
	public static final int _EMVPARAM_9F7B_ECLIMIT = 0x9F7B ;     /* Paypass,READER_CLSS_TRANS_LIMIT_ON_DEVICE_CVM(DF8125), [history reason,tag same as EMV EC-limit], n12  6bytes*/
	public static final int _EMVPARAM_DF7D_TRANSTYPE = 0xDF7D;	    /* paypass, trans type, [same as Pure TYPEAAT],n1,*/
	public static final int _EMVPARAM_1F8103_ADDTERCAP =0x1F8103;;    /* rupay Additional Terminal Capabilities Extension DF3A*/
	public static final int _EMVPARAM_1F8104_SERVICEID =0x1F8104  ;  /* rupay service id DF16.*/
	public static final int _EMVPARAM_1F8105_SERVICEQUA =0x1F8105 ;   /* rupay Service Qualifier*/
	public static final int _EMVPARAM_1F8106_SERVICEDATA =0x1F8106 ;   /* rupay Service Data DF45*/
	public static final int _EMVPARAM_1F8107_SERVICEPRMISS =0x1F8107 ;   /* rupay PRMiss DF47*/
	public static final int _EMVPARAM_1F8108_SERVICEPRMACQKEY =0x1F8108 ;   /* rupay PRMacq Key value*/
	public static final int _EMVPARAM_1F8113_DataRecord_EMV =0x1F8113;    //Data Record Tags (EMV)
	public static final int _EMVPARAM_1F8114_DataRecord_MSD =0x1F8114;    //Data Record Tags (MSD / Magstripe / Non-EMV)
	public static final int _EMVPARAM_1F8115_DiscretionaryData_EMV =0x1F8115 ;   //Discretionary Data Tags (EMV)
	public static final int _EMVPARAM_1F8116_DiscretionaryData_MSD =0x1F8116;    //Discretionary Data Tags (MSD / Magstripe / Non-EMV)
	public static final int _EMVPARAM_1F8117_Optional_DataRecord_MSD =0x1F8117 ;   //Optional Data Record Tags (MSD / Magstripe / Non-EMV)
	public static final int _EMVPARAM_1F816F_ENFORCEMAC =0x1F816F ;   //Enforce MAC
	public static final int _EMVPARAM_1F811A_NOCVMLIMIT =0x1F811A ;   //Terminal No Cvm Limit DF52
	public static final int _EMVPARAM_1F811B_CONTACTLESSLIMIT_NOCDCVM =0x1F811B;    //Terminal Contactless Limit (Non CD-CVM) DF53
	public static final int _EMVPARAM_1F811C_CONTACTLESSLIMIT_CDCVM =0x1F811C ;   //Terminal Contactless Limit (CD-CVM) DF54
	public static final int _EMVPARAM_1F811D_TERTPM_CAP =0x1F811D;    //Terminal TPM Capabilities DF55
	public static final int _EMVPARAM_1F811E_DE_TAGLIST =0x1F811E ;   //Data Exchange Tag List
	public static final int _EMVPARAM_1F811F_DE_COMMON_TAG =0x1F811F;    //Data Exchange Common Tag(it is no in the aid configuration file)
	public static final int _EMVPARAM_DF8118_CVMCAP_ABOVEEQUALLIMIT =0xDF8118;    //CVM Capabilities (above or equal to CVM Limit)
	public static final int _EMVPARAM_DF8119_CVMCAP_BELOWLIMIT =0xDF8119;    //CVM Capabilities (below to CVM Limit)
	public static final int _EMVPARAM_DF8011_TAC_SWITCH =0xDF8011;    //Tac-Switch
	public static final int _EMVPARAM_DF8012_TERFUNCAUTH	 =0xDF8012 ;   //terminal function authorization


}
