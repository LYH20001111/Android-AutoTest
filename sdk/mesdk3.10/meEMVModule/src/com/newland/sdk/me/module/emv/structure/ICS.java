package com.newland.sdk.me.module.emv.structure;


public class ICS extends AbstractBitSetting {

    /********************************** ICS  related bit ************************************************/
    /*
     * AS   : Application Selection
	 * Macro:
	   AS_Support_PSE                 : Support PSE selection method
	   AS_Support_CardHolder_Confirm  : Support Cardholder confirmation
	   AS_Support_Prefferd_Order      : Have a preferred order of displaying applications
	   AS_Support_Partial_AID         : Does the terminal perform partial AID selection
	   AS_Support_Multi_Language      : Does the terminal have multi language support
	   AS_Support_Common_Charset      : Does the terminal support Common Character Set as
	                                    defined in "Annex B table 20 Book 4"

	 * EMV 4.1 ICS Version 3.9 Level2
	 */
    public static final BitTag AS_Support_PSE = new BitTag(0x0080);
    public static final BitTag AS_Support_CardHolder_Confirm = new BitTag(0x0040);
    public static final BitTag AS_Support_Preferred_Order = new BitTag(0x0020);
    public static final BitTag AS_Support_Partial_AID = new BitTag(0x0010);
    public static final BitTag AS_Support_Multi_Language = new BitTag(0x0008);
    public static final BitTag AS_Support_Common_Charset = new BitTag(0x0004);

    /*
     * DA   : Data Authentication
     * IPKC : Issuer Public Key Certificate
     * CAPK : Certification Authority Public Key
     * Macro:
          DA_Support_IPKC_Revoc_Check      : During DA, does the terminal check the revocation of IPKC
          DA_Support_Default_DDOL          : Does the terminal contain a default DDOL
          DA_Support_CAPKLoad_Fail_Action  : Is operation action required when loading CAPK fails
          DA_Support_CAPK_Checksum         : Is CAPK verified with CAPK checksum

     * EMV 4.1 ICS Version 3.9 Level2
     */
    public static final BitTag DA_Support_IPKC_Revoc_Check = new BitTag(0x0180);
    public static final BitTag DA_Support_Default_DDOL = new BitTag(0x0140);
    public static final BitTag DA_Support_CAPKLoad_Fail_Action = new BitTag(0x0120);
    public static final BitTag DA_Support_CAPK_Checksum = new BitTag(0x0110);

    /*
     * CV   : Cardholder Verification
     * CVM   : Cardholder Verification Methods
     * Macro:
          CV_Support_Bypass_PIN          : Terminal supports bypass PIN entry
          CV_Support_PIN_Try_Counter     : Terminal supports Get Data for PIN Try Counter
          CV_Support_Fail_CVM            : Terminal supports Fail CVM
          CV_Support_Amounts_before_CVM  : Are amounts known before CVM processing

     * EMV 4.1 ICS Version 3.9 Level2
     */
    public static final BitTag CV_Support_Bypass_PIN = new BitTag(0x0280);
    public static final BitTag CV_Support_PIN_Try_Counter = new BitTag(0x0240);
    public static final BitTag CV_Support_Fail_CVM = new BitTag(0x0220);
    public static final BitTag CV_Support_Amounts_before_CVM = new BitTag(0x0210);
    public static final BitTag CV_Support_Bypass_ALL_PIN = new BitTag(0x0208);

    /*
     * TRM  : Terminal Risk Management
     * Macro:
       TRM_Support_FloorLimit     : Floor Limit Checking,
                                    Mandatory for terminal with offline capability
       TRM_Support_RandomSelect   : Random Transaction Selections,
                                    Mandatory for offline terminal with online capability,
                                    except when cardholder controlled
       TRM_Support_VelocityCheck  : Velocity checking,
                                    Mandatory for for terminal with offline capability
       TRM_Support_TransLog       : Support transaction log
       TRM_Support_ExceptionFile  : Support exception file
       TRM_Support_AIPBased       : Performance of TRM based on AIP setting
       TRM_Use_EMV_LogPolicy      : EMV has a different log policy with PBOC2, marked here

     * EMV 4.1 ICS Version 3.9 Level2
     */
    public static final BitTag TRM_Support_FloorLimit = new BitTag(0x0380);
    public static final BitTag TRM_Support_RandomSelect = new BitTag(0x0340);
    public static final BitTag TRM_Support_VelocityCheck = new BitTag(0x0320);
    public static final BitTag TRM_Support_TransLog = new BitTag(0x0310);
    public static final BitTag TRM_Support_ExceptionFile = new BitTag(0x0308);
    public static final BitTag TRM_Support_AIPBased = new BitTag(0x0304);
    public static final BitTag TRM_Use_EMV_LogPolicy = new BitTag(0x0302);

    /*
     * TAA  : Terminal Action Analysis
     * (x)  : the var of struct STEMVCONFIG
     * TAC  : Terminal Action Codes
     * DAC  : Default Action Codes
     * Macro:
       TAA_Support_TAC                  : Does the terminal support Terminal Action Codes
       TAA_Support_DAC_before_1GenAC    : Does the terminal process DAC prior to first GenAC
       TAA_Support_DAC_after_1GenAC     : Does the terminal process DAC after first GenAC
       TAA_Support_Skip_DAC_OnlineFail  : Does the terminal skip DAC processing and automatically
                                          request an AAC when unable to go online
       TAA_Support_DAC_OnlineFail       : Does the terminal process DAC as normal
                                          when unable to go online
       TAA_Support_CDAFail_Detected     : Device capable of detecting CDA Failure before TAA
       TAA_Support_CDA_Always_in_ARQC   : CDA always requested in a first Gen AC, ARQC request
       TAA_Support_CDA_Never_in_ARQC    : CDA never requested in a first Gen AC, ARQC request
       TAA_Support_CDA_Alawys_in_2TC    : CDA always requested in a second Gen AC when successful
                                          host response is received, with TC request
       TAA_Support_CDA_Never_in_2TC     : CDA never requested in a second Gen AC when successful
                                          host response is received, with TC request
     * EMV 4.1 ICS Version 3.9 Level2
     */
    public static final BitTag TAA_Support_TAC = new BitTag(0x0480);
    public static final BitTag TAA_Support_DAC_before_1GenAC = new BitTag(0x0440);
    public static final BitTag TAA_Support_DAC_after_1GenAC = new BitTag(0x0420);
    public static final BitTag TAA_Support_Skip_DAC_OnlineFail = new BitTag(0x0410);
    public static final BitTag TAA_Support_DAC_OnlineFail = new BitTag(0x0408);
    public static final BitTag TAA_Support_CDAFail_Detected = new BitTag(0x0404);
    public static final BitTag TAA_Support_CDA_Always_in_ARQC = new BitTag(0x0402);
    public static final BitTag TAA_Support_CDA_Alawys_in_2TC = new BitTag(0x0401);

    /*
     * CP  : Completion Process
     * (x)  : the var of struct STEMVCONFIG
     * Macro:
       CP_Support_Force_Online         : Transaction forced Online capability
       CP_Support_Force_Accept         : Transaction forced Acceptance capability
       CP_Support_Advices              : Does the terminal support advices
       CP_Support_Issuer_VoiceRef      : Does the terminal support Issuer Initiated Voice Referrals
       CP_Support_Batch_Data_Capture   : Does the terminal support Batch Data Capture
       CP_Support_Online_Data_capture  : Does the terminal support Online Data Capture
       CP_Support_Default_TDOL         : Does the terminal support a default TDOL

     * EMV 4.1 ICS Version 3.9 Level2
     */
    public static final BitTag CP_Support_Force_Online = new BitTag(0x0580);
    public static final BitTag CP_Support_Force_Accept = new BitTag(0x0540);
    public static final BitTag CP_Support_Advices = new BitTag(0x0520);
    public static final BitTag CP_Support_Issuer_VoiceRef = new BitTag(0x0510);
    public static final BitTag CP_Support_Batch_Data_Capture = new BitTag(0x0508);
    public static final BitTag CP_Support_Online_Data_capture = new BitTag(0x0504);
    public static final BitTag CP_Support_Default_TDOL = new BitTag(0x0502);

    /*
     * MISC : Miscellaneous
     * (x)  : the var of struct emvconfig
     * Macro:
       MISC_Support_Account_Select         : Does the terminal support account type selection
       MISC_Support_ISDL_Greater_than_128  : Is Issuer Script Device Limit greater than 128 bytes
       MISC_Support_Internal_Date_Mana     : Does the terminal support internal date management

     * EMV 4.1 ICS Version 3.9 Level2
     */
    public static final BitTag MISC_Support_Account_Select = new BitTag(0x0680);
    public static final BitTag MISC_Support_ISDL_Greater_than_128 = new BitTag(0x0640);
    public static final BitTag MISC_Support_Internal_Date_Mana = new BitTag(0x0620);
    //paypass update 0719 liudan
    public static final BitTag MISC_PP_Support_Default_UDOL = new BitTag(0x0602);
    public static final BitTag MISC_MISC_PP_Support_MagAppVer = new BitTag(0x0601);

    public ICS(byte[] value) {
        super(7, value);
    }

    public ICS() {
        super(7);
    }


}
