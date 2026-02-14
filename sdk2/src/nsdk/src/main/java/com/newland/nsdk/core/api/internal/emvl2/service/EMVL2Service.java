package com.newland.nsdk.core.api.internal.emvl2.service;

import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.internal.emvl2.listener.EmvJNIListener;
import com.newland.nsdk.core.api.internal.emvl2.type.EmvConst;
import com.newland.nsdk.core.api.internal.emvl2.type.aidlist_clss;
import com.newland.nsdk.core.api.internal.emvl2.type.aidlist_emv;
import com.newland.nsdk.core.api.internal.emvl2.type.candidate_emv;
import com.newland.nsdk.core.api.internal.emvl2.type.config_clss;
import com.newland.nsdk.core.api.internal.emvl2.type.ep_opt;
import com.newland.nsdk.core.api.internal.emvl2.type.publickey;
import com.newland.nsdk.core.api.internal.emvl2.type.rf_transdata;

import java.util.ArrayList;

/**
 * Provides EMV L2 related functions.
 */
public interface EMVL2Service extends Module {

    /**
     * Sets EMV event listener.
     *
     * @param callbackFunction [IN] EMV event listener. See {@link EmvJNIListener}.
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Success.</li>
     * <li>{@link EmvConst#EMVL2_ERR_FAIL}: Failed.</li>
     * </ul>
     */
    int NAPIEMVL2SetCallbackFunction(EmvJNIListener callbackFunction);

    /**
     * Sets EMV event listener.
     *
     * @param callbackFunction [IN] EMV event listener. See {@link EmvJNIListener}.
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Success.</li>
     * <li>{@link EmvConst#EMVL2_ERR_FAIL}: Failed.</li>
     * </ul>
     */
    int NAPICLL2SetCallbackFunction(EmvJNIListener callbackFunction);


    /**
     * Frees global variables.
     *
     * @return Result code.
     */
    int NAPIEMVL2FreeGlobalVariable();


    /**
     * Builds candidate list by PSE or AID selection method.
     *
     * @param candidateList [OUT] Candidate list after building.
     * @param aidList       [IN] Terminal AID list.
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NEED_CONFIRM}: Success, candidate list needs to be confirmed by cardholder.</li>
     * <li>{@link EmvConst#EMVL2_ERR_AUTO_SELECT}: Success, no need to confirm candidate list by cardholder.</li>
     * <li>{@link EmvConst#EMVL2_ERR_PARAM}: Any params errors.</li>
     * <li>{@link EmvConst#EMVL2_ERR_CARD_BLOCK}: ICC response 6A81 which means the card is blocked when PSE selection.</li>
     * <li>{@link EmvConst#EMVL2_ERR_APP_BLOCK}: ICC response 6283 which means the application is blocked when AID selection.</li>
     * <li>{@link EmvConst#EMVL2_ERR_TERMINATE}: There are no mutually supported applications or ICC error, the application should terminate the transaction.</li>
     * </ul>
     */
    int NAPIEMVL2BuildCandidateList(ArrayList<candidate_emv> candidateList, ArrayList<aidlist_emv> aidList);


    /**
     * Selects the application from the candidate list by auto selection or cardholder confirmation.
     *
     * @param selectionID   [IN] The index of candidate list to be selected. 0: auto selection.
     * @param candidateList [IN/OUT] The candidate list after final selection. If application selection failed, related candidate list will be removed, candidate list will be updated.
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Application selection successful.</li>
     * <li>{@link EmvConst#EMVL2_ERR_PARAM}: Any params errors.</li>
     * <li>{@link EmvConst#EMVL2_ERR_APP_BLOCK}: ICC response 6283 which means the application is blocked when AID selection.</li>
     * <li>{@link EmvConst#EMVL2_ERR_NO_MORE_APPS}: Before final selection or after selection failed, there is no more application in candidate list. The terminal application should terminate the transaction.</li>
     * <li>{@link EmvConst#EMVL2_ERR_SELECT_NEXT}: After selection failed, candidate list still has applications. The terminal application can continue perform auto selection or cardholder confirmation.</li>
     * </ul>
     */
    int NAPIEMVL2SelectApplication(int selectionID, ArrayList<candidate_emv> candidateList);


    /**
     * Sends "Get Process Option (GPO)" command to IC card to initiate application processing.
     *
     * @param candidateList Candidate list.
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Success.</li>
     * <li>{@link EmvConst#EMVL2_ERR_PARAM}:Any one of mandatory terminal TAGs about 9F33, 9F35 or 9F40 is missing.</li>
     * <li>{@link EmvConst#EMVL2_ERR_SELECT_NEXT}: The card returns SW1 SW2 = '6985' in response to GET PROCESSING OPTIONS command, the terminal should display "NOT ACCEPTED" message and shall return to application selection.</li>
     * <li>{@link EmvConst#EMVL2_ERR_NO_MORE_APPS}: The card returns SW1 SW2 = '6985' in response to GET PROCESSING OPTIONS command, and there is no more application in the candidate list, the terminal application should terminate the session.</li>
     * <li>{@link EmvConst#EMVL2_ERR_TERMINATE}: The card returns SW1 SW2 not '6985' or '9000', 77 or 80 template parse error, AIP or AFL missing or ICC error, the application should terminate the session.</li>
     * </ul>
     */
    int NAPIEMVL2InitiateApplication(ArrayList<candidate_emv> candidateList);

    /**
     * Reads application data according to the AFL. This is performed after the Initiate Application Processing function.
     *
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Success.</li>
     * <li>{@link EmvConst#EMVL2_ERR_TERMINATE}: AFL format error, or SW1 SW2 not '9000' in response to the READ RECORD command, or 70 template parse error, or mandatory data missing, or any ICC error, the application should terminate the transaction.</li>
     * </ul>
     */
    int NAPIEMVL2ReadApplicationData();

    /**
     * Performs offline data authentication.
     *
     * <p>Terminal application shall perform offline data authentication in any order after Read Application Data but before completion of terminal action analysis.</p>
     *
     * @param ddol    [IN] Default DDOL. If the termianl don't support DDA or default DDOL, passing NULL is OK.
     * @param ddolLen [IN] Default DDOL length.
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Offline data authentication has been performed successfully.</li>
     * <li>{@link EmvConst#EMVL2_ERR_FAIL}: Offline data authentication failed or not performed.</li>
     * <li>{@link EmvConst#EMVL2_ERR_CAPK_CHECKSUM}: CAPK check failed, if operator action is needed, the terminal application shall display an error message.</li>
     * <li>{@link EmvConst#EMVL2_ERR_TERMINATE}: SW1 SW2 not '9000' in response to the INTERNAL AUTHENTICATE command, or 77 or 80 template parse error, or icc error, the application should terminate the session.</li>
     * </ul>
     */
    int NAPIEMVL2OfflineDataAuthentication(byte[] ddol, int ddolLen);

    /**
     * Checks Application Version Number, Application Usage Control, Application Effective/Expiration  Dates.
     *
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Restriction processing has been performed.</li>
     * </ul>
     */
    int NAPIEMVL2ProcessingRestrictions();


    /**
     * Performs cardholder verification. Analysis CVM list and returns current supported CVM code to the terminal to perform. If the CVM code is about PIN verification, the terminal should pass the result status by calling this API again.
     *
     * @param cvmCode  Return current CVM code to the terminal to perform CV method.
     *                 <ul>
     *                 <li>If CVM code is {@link EmvConst#EMVL2_CVM_CUSTOM_CONDITION}, means kernel does not recognize CV condition code or CV rule, and application needs to process custom CV method.</li>
     *                 <li>If {@link EmvConst#EMVL2_CVM_DONE}, means cardholder verification completed and not need to call this API again.</li>
     *                 </ul>
     * @param cvStatus <ul>
     *                 <li>[IN] Result of current CV method.
     *                 <ul>
     *                 <li>If current CV code is {@link EmvConst#EMVL2_CVM_CUSTOM_CONDITION}, terminal should return check result in byte 1 about {@link EmvConst#EMVL2_CVS_UNRECOGNIZE}, {@link EmvConst#EMVL2_CVS_UNSUPPORT}, {@link EmvConst#EMVL2_CVS_UNSATISFY}, or {@link EmvConst#EMVL2_CVS_OK}.</li>
     *                 <li>If current CV code is about offline PIN verify, terminal should return offline PIN verification result {@link EmvConst#EMVL2_CVS_OFFLINEPIN_VERIFICATION_OK}, or {@link EmvConst#EMVL2_CVS_OFFLINEPIN_VERIFICATION_FAIL} following two bytes status word that return in VERIFY command from IC card. </li>
     *                 <li>If previous CV code is {@link EmvConst#EMVL2_CVM_ONLINE_PIN}, terminal should return online PIN process result about {@link EmvConst#EMVL2_CVS_ONLINEPIN_OK}.</li>
     *                 </ul></li>
     *                 <li>[OUT] See below:
     *                 <ul>
     *                 <li>If CVM code is {@link EmvConst#EMVL2_CVM_CUSTOM_CONDITION}, it returns 10 bytes data. byte 1: CV rule. byte 2: CV condition. byte 3~6: Amount X. byte 7~10: Amount Y.</li>
     *                 <li>If CVM code is about PIN verify, first byte of cvStatus means PIN try counter. </li>
     *                 <li>If first byte of cvStatus is {@link EmvConst#EMVL2_CVS_FAIL}, means there is no more CV method can do and cardholder verification is failed, not need to call this API again.</li>
     *                 </ul></li>
     *                 </ul>
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Cardholder verification is performed.</li>
     * <li>{@link EmvConst#EMVL2_ERR_PARAM}: cvmCode or cvStatus is NULL pointer.</li>
     * <li>{@link EmvConst#EMVL2_ERR_TERMINATE}: Encounters formatting errors in CVM List such as a list with an odd number of bytes, Status word is not 9000, 63C0, 6983, 6984 or any error undefined in response to the VERIFY command, or any ICC error, the application should terminate the session.</li>
     * </ul>
     */
    int NAPIEMVL2CardholderVerification(byte[] cvmCode, byte[] cvStatus);

    /**
     * Performs terminal risk management.
     *
     * @param blackCard           [IN] 0: the card absent on the blacklist. 1: the card presented on the blacklist.
     * @param forceOnline         [IN] If the merchant forces the transaction online. 0: No. 1: Yes.
     * @param logAmount           [IN] The transaction amount of the card has completed before, format n6.
     * @param targetPercentage    [IN] Target Percentage to be used for Random Selection. (in the range of 0 to 99)
     * @param maxTargetPercentage [IN] Maximum Target Percentage to be used for Biased Random Selection. (also in the range of 0 to 99)
     * @param thresholdValue      [IN] Threshold Value for Biased Random Selection (which must be zero or a positive number less than the floor limit), format n4.
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Risk management has been performed.</li>
     * </ul>
     */
    int NAPIEMVL2RiskManagement(int blackCard, int forceOnline, byte[] logAmount, int targetPercentage, int maxTargetPercentage, byte[] thresholdValue);

    /**
     * Performs terminal action analysis. The terminal application should require AC according to the return value of acType by performing card action analysis.
     *
     * @param acType        [OUT] The request AC type by the result of terminal action analysis.
     * @param tacDenial     [IN] Terminal Action Code - Denial, 5 bytes.
     * @param tacOnline     [IN] Terminal Action Code - Online, 5 bytes.
     * @param tacDefault    [IN] Terminal Action Code - Default, 5 bytes.
     * @param disableOnline [IN] 0: ignore. 1: the terminal is for any reason disable to process the transaction online.
     * @param tdol          [IN] Default TDOL.
     * @param tdolLen       [IN] Default TDOL length.
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Success, the terminal should check or change acType before GAC.</li>
     * <li>{@link EmvConst#EMVL2_ERR_PARAM}: Any parameter errors.</li>
     * </ul>
     */
    int NAPIEMVL2TerminalActionAnalysis(int[] acType, byte[] tacDenial, byte[] tacOnline, byte[] tacDefault, int disableOnline, byte[] tdol, int tdolLen);

    /**
     * Sends first Generate AC command to ICC to perform card action analysis.
     *
     * <p>CDA signature should be required if terminal and ICC supported both. The terminal application checks return value to obtain the card's decision of offline approval, offline declined or online request. If ICC requested advice, the terminal application should send an advice message to the issuer.</p>
     *
     * @param reqACType [IN] Terminal final decision of AC type.
     * @param advice    [OUT] 0: No 1: Yes.
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_TERMINATE}: 77 or 80 template parse error, or CID format error, or icc error, the application should terminate the session.</li>
     * <li>{@link EmvConst#EMVL2_ERR_GO_ONLINE}: The terminal should process transaction online.</li>
     * <li>{@link EmvConst#EMVL2_ERR_DECLINE}: The terminal should decline the transaction.</li>
     * <li>{@link EmvConst#EMVL2_ERR_ACCEPT}: The terminal should accept the transaction.</li>
     * <li>{@link EmvConst#EMVL2_ERR_GAC2_AAC}: The terminal should request AAC by calling {@link #NAPIEMVL2GenerateAC2nd}.</li>
     * </ul>
     */
    int NAPIEMVL2GenerateAC1st(int[] advice, int reqACType);

    /**
     * Sends second Generate AC command to ICC to perform card action analysis.
     *
     * <p>
     * If online processing performed after {@link #NAPIEMVL2GenerateAC1st}, terminal application analyzes the issuer authentication response code
     * and make the final decision of reqACType. Authentication Response Code (TAG 8A) should be updated by application.
     * If issuer returns Issuer Authentication Data (TAG 91), the application should also store first for issuer authentication.
     * If online is disabled, {@link #NAPIEMVL2TerminalActionAnalysis} should be called first to make the final decision before calling this function.
     * After this function is called, terminal application checks the return value to obtain the card decision of TC approval or AAC declined.
     * If ICC requests advice, terminal application should send a advice message to the issuer.
     * </p>
     *
     * @param advice          [OUT] 0: No 1: Yes.
     * @param reqACType       [IN] Online result or issuer voice result of the issuer. If online is disabled, assign second TAA result or direct request decision of AC type. CDA signature always be required if reqACType is TC.
     * @param script71        [IN] Type 71 issuer script.
     * @param script71Len     [IN] The length of script71.
     * @param script72        [IN] Type 72 issuer script.
     * @param script72Len     [IN] The length of script72.
     * @param scriptResult    [OUT] Return the script result.
     * @param scriptResultLen [IN/OUT] The size of buffer scriptResult/The actual length of script result.
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_PARAM}: Any parameter errors.</li>
     * <li>{@link EmvConst#EMVL2_ERR_FORMAT}: There is no Issuer Script Command(tag 86) in script.</li>
     * <li>{@link EmvConst#EMVL2_ERR_TERMINATE}: 77 or 80 template parse error, or CID format error, or icc error, the application should terminate the session.</li>
     * <li>{@link EmvConst#EMVL2_ERR_DECLINE}: The terminal should decline the transaction.</li>
     * <li>{@link EmvConst#EMVL2_ERR_ACCEPT}: The terminal should accept the transaction.</li>
     * </ul>
     */
    int NAPIEMVL2GenerateAC2nd(int[] advice, int reqACType, byte[] script71, int script71Len, byte[] script72, int script72Len, byte[] scriptResult, int[] scriptResultLen);

    /**
     * Clears all the tag data in kernel data store space.
     *
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Success.</li>
     * <li>{@link EmvConst#EMVL2_ERR_FAIL}: Failed.</li>
     * </ul>
     */
    int NAPIEMVL2InitTagData();

    /**
     * Checks if the tag exist in the kernel data space.
     *
     * @param tag [IN] The tag to check.
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Present.</li>
     * <li>{@link EmvConst#EMVL2_ERR_TAG_ABSENT}: Absent.</li>
     * </ul>
     */
    int NAPIEMVL2ExistTag(int tag);

    /**
     * Gets PIN encryption PK for offline encryption PIN verification.
     *
     * @param pinPK [OUT] PIN encryption  PK structure.
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Success.</li>
     * <li>{@link EmvConst#EMVL2_ERR_FAIL}: Failed.</li>
     * </ul>
     */
    int NAPIEMVL2GetPinpk(publickey pinPK);

    /**
     * Clears all tag data, initializes kernel status and clears kernel configuration.
     *
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Success.</li>
     * <li>{@link EmvConst#EMVL2_ERR_FAIL}: Failed.</li>
     * </ul>
     */
    int NAPIEMVL2Initialize();

    /**
     * Sets kernel configuration.
     *
     * @param opt [IN] Configuration bitmap as macro defined: EMVL2_SUPPORT_XXX
     * @param val [IN] 0: Unset, 1: Set.
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Success.</li>
     * </ul>
     */
    int NAPIEMVL2SetConfig(int opt, int val);

    /**
     * Gets kernel current configuration.
     *
     * @param opt [IN] Configuration bitmap as macro defined: EMVL2_SUPPORT_XXX
     * @return <ul>
     * <li>0: unset.</li>
     * <li>1: set.</li>
     * </ul>
     */
    int NAPIEMVL2GetConfig(int opt);

    /**
     * Gets EMV level 2 version information.
     *
     * @return Version.
     */
    String NAPIEMVL2GetVersion();

    /**
     * Gets kernel configuration checksum.
     *
     * @param checksum [OUT] Kernel configuration checksum.
     * @param size     [IN] The buffer size of checksum. Minimum value is 4.
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Success.</li>
     * <li>{@link EmvConst#EMVL2_ERR_PARAM}: checksum is NULL, or size is less than 4 bytes.</li>
     * <li>{@link EmvConst#EMVL2_ERR_FAIL}</li>
     * </ul>
     */
    int NAPIEMVL2GetConfigChecksum(byte[] checksum, int size);

    /**
     * Gets kernel checksum.
     *
     * @param checksum [OUT] Kernel checksum.
     * @param size     [IN] The buffer size of checksum. Minimum value is 4.
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Success.</li>
     * <li>{@link EmvConst#EMVL2_ERR_PARAM}: checksum is NULL, or size is less than 4 bytes.</li>
     * <li>{@link EmvConst#EMVL2_ERR_FAIL}</li>
     * </ul>
     */
    int NAPIEMVL2GetKernelChecksum(byte[] checksum, int size);

    /**
     * Contactless transaction processing.
     *
     * @param obj_epopt      [IN/OUT] Entry_Point Trading Options.
     * @param obj_rfdata     [IN/OUT] Trans Data Options.
     * @param ctrl           [IN] Some control data.
     *                       <ul>
     *                       <li>byte 1: Card seeking flag</li>
     *                       <ul>
     *                           <li>NO SEEK CARD =0</li>
     *                           <li>SEEK CARD IN APP = 1</li>
     *                           <li>SEEK CARD IN SERVER = 2</li>
     *                           <li>ACTIVE CARD IN SERVER = 3</li>
     *                       </ul>
     *                       <li>byte 2: qPBOC getting data flag</li>
     *                       <ul>
     *                           <li>NO NEED TO GET DATA = 0</li>
     *                           <li>NEED TO GET DATA = 1</li>
     *                       </ul>
     *                       <li>byte 3: Process light flag(see the below, it needs to set all four lights status)</li>
     *                       <li>byte 4: Card reading OK light flag(see the below, it needs to set all four lights status)</li>
     *                       <ul>
     *                           <li>bit 8-7 first light:</li>
     *                           <ul>
     *                               <li>LED_RFID_BLUE_ON = 0x40</li>
     *                               <li>LED_RFID_BLUE_OFF = 0x80</li>
     *                               <li>LED_RFID_BLUE_FLICK = 0xc0</li>
     *                           </ul>
     *                           <li>bit 6-5 third light:</li>
     *                           <ul>
     *                               <li>LED_RFID_GREEN_ON = 0x10</li>
     *                               <li>LED_RFID_GREEN_OFF = 0x20</li>
     *                               <li>LED_RFID_GREEN_FLICK = 0x30</li>
     *                           </ul>
     *                           <li>bit 4-3 second light:</li>
     *                           <ul>
     *                               <li>LED_RFID_YELLOW_ON = 0x04</li>
     *                               <li>LED_RFID_YELLOW_OFF = 0x08</li>
     *                               <li>LED_RFID_YELLOW_FLICK = 0x0c</li>
     *                           </ul>
     *                           <li>bit 2-1 four light:</li>
     *                           <ul>
     *                               <li>LED_RFID_RED_ON = 0x01</li>
     *                               <li>LED_RFID_RED_OFF = 0x02</li>
     *                               <li>LED_RFID_RED_FLICK = 0x03</li>
     *                           </ul>
     *                       </ul>
     *                       <li>byte 5: After Final Selection callback flag</li>
     *                       <ul>
     *                           <li>NO NEED CALLBACK = 0</li>
     *                           <li>NEED CALLBACK = 1</li>
     *                           <li>NAPI_EMV_BEFKRNEEDCALLBACK = 2</li>
     *                       </ul>
     *                       </ul>
     * @param processData    [IN] TLV transaction data.
     * @param processDataLen [IN] TLV Date length.
     * @return Transaction result
     */
    int NAPICLL2PerformTransaction(ep_opt obj_epopt, rf_transdata obj_rfdata, byte[] ctrl, byte[] processData, int processDataLen);

    /**
     * Contactless transaction pre-processing.
     *
     * @param obj_epopt  [IN] Entry_Point Transaction Options
     * @param obj_rfdata [IN] Contactless transaction mandatory data.
     * @return <ul>
     * <li>{@link EmvConst#EMV_TRANS_RF_TERMINATE}: Terminate a transaction.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_TRYOTHERINT}: Try another interface.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_ACTIVE_KERNEL}: According to the response of the corresponding kernel ID activation related transactions, continue to complete the transaction.</li>
     * </ul>
     */
    int NAPICLL2EntryPointProcess(ep_opt obj_epopt, rf_transdata obj_rfdata);

    /**
     * Processes a Paypass transaction.
     *
     * @param obj_epopt  [IN] Entry_Point Transaction Options.
     * @param obj_rfdata [IN] Contactless transaction mandatory data.
     * @return <ul>
     * <li>{@link EmvConst#EMV_TRANS_RF_TERMINATE}: Terminate a transaction.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_ACCEPT}: M/CHIP Offline Approve.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_DENIAL}: CLSS M/CHIP decline.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_GOONLINE}: CLSS M/CHIP Online Request.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_GOONLINE}: CLSS MAG Online Request.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_ACCEPT}: CLSS MAG Approve.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_TRYOTHERINT}: Try another interface.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_SELECT_NEXT_AID}: Select next aid.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_DENIAL}: CLSS MAG Decline.</li>
     * </ul>
     */
    int NAPICLL2PayPassProcess(ep_opt obj_epopt, rf_transdata obj_rfdata);

    /**
     * Processes a Paywave transaction.   
     *
     * @param obj_epopt  [IN] Entry_Point Transaction Options.
     * @param obj_rfdata [IN] Contactless transaction mandatory data.
     * @return <ul>
     * <li>{@link EmvConst#EMV_TRANS_RF_TERMINATE}: Terminate a transaction.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_ACCEPT}: M/CHIP Offline Approve.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_DENIAL}: CLSS M/CHIP decline.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_GOONLINE}: CLSS M/CHIP Online Request.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_GOONLINE}: CLSS MAG Online Request.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_ACCEPT}: CLSS MAG Approve.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_TRYOTHERINT}: Try another interface.</li>
     * <li>{@link EmvConst#EMV_TRANS_DOWNCARD}: See phone.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_SELECT_NEXT_AID}: Select next aid.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_DENIAL}: CLSS MAG Decline.</li>
     * </ul>
     */
    int NAPICLL2PayWaveProcess(ep_opt obj_epopt, rf_transdata obj_rfdata);

    /**
     * Processes an ExpressPay transaction.
     *
     * @param obj_epopt  [IN] Entry_Point Transaction Options.
     * @param obj_rfdata [IN] Contactless transaction mandatory data.
     * @return <ul>
     * <li>{@link EmvConst#EMV_TRANS_RF_TERMINATE}: Terminate a transaction.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_ACCEPT}: M/CHIP Offline Approve.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_DENIAL}: CLSS M/CHIP decline.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_GOONLINE}: CLSS M/CHIP Online Request.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_GOONLINE}: CLSS MAG Online Request.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_ACCEPT}: CLSS MAG Approve.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_TRYOTHERINT}: Try another interface.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_CONTACTLESS_NOTPER}: CLSS Decline.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_SELECT_NEXT_AID}: Select next aid.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_DENIAL}: CLSS MAG Decline.</li>
     * </ul>
     */
    int NAPICLL2ExpressPayProcess(ep_opt obj_epopt, rf_transdata obj_rfdata);

    /**
     * Processes a JCB transaction.
     *
     * @param obj_epopt  [IN] Entry_Point Transaction Options.
     * @param obj_rfdata [IN] Contactless transaction mandatory data.
     * @return <ul>
     * <li>{@link EmvConst#EMV_TRANS_RF_TERMINATE}: Terminate a transaction.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_ACCEPT}: M/CHIP Offline Approve.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_DENIAL}: CLSS M/CHIP decline.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_GOONLINE}: CLSS M/CHIP Online Request.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_GOONLINE}: CLSS MAG Online Request.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_ACCEPT}: CLSS MAG Approve.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_TRYOTHERINT}: Try another interface.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_SELECT_NEXT_AID}: Select next aid.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_DENIAL}: CLSS MAG Decline.</li>
     * </ul>
     */
    int NAPICLL2JCBProcess(ep_opt obj_epopt, rf_transdata obj_rfdata);

    /**
     * Processes a D-pas transaction.
     *
     * @param obj_epopt  [IN] Entry_Point Transaction Options.
     * @param obj_rfdata [IN] Contactless transaction mandatory data.
     * @return <ul>
     * <li>{@link EmvConst#EMV_TRANS_RF_TERMINATE}: Terminate a transaction.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_ACCEPT}: M/CHIP Offline Approve.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_DENIAL}: CLSS M/CHIP decline.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_GOONLINE}: CLSS M/CHIP Online Request.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_GOONLINE}: CLSS MAG Online Request.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_ACCEPT}: CLSS MAG Approve.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_TRYOTHERINT}: Try another interface.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_SELECT_NEXT_AID}: Select next aid.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_DENIAL}: CLSS MAG Decline.</li>
     * </ul>
     */
    int NAPICLL2DiscoverPayProcess(ep_opt obj_epopt, rf_transdata obj_rfdata);

    /**
     * Processes a qPBOC transaction.    
     *
     * @param obj_epopt  [IN] Entry_Point Transaction Options.
     * @param obj_rfdata [IN] Contactless transaction mandatory data.
     * @return <ul>
     * <li>{@link EmvConst#EMV_TRANS_RF_TERMINATE}: Terminate a transaction.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_ACCEPT}: M/CHIP Offline Approve.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_DENIAL}: CLSS M/CHIP decline.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_GOONLINE}: CLSS M/CHIP Online Request.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_GOONLINE}: CLSS MAG Online Request.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_ACCEPT}: CLSS MAG Approve.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_TRYOTHERINT}: Try another interface.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_SELECT_NEXT_AID}: Select next aid.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_DENIAL}: CLSS MAG Decline.</li>
     * </ul>
     */
    int NAPICLL2QpbocProcess(ep_opt obj_epopt, rf_transdata obj_rfdata);

    /**
     * Processes a Pure transaction.
     *
     * @param obj_epopt  [IN] Entry_Point Transaction Options.
     * @param obj_rfdata [IN] Contactless transaction mandatory data.
     * @return <ul>
     * <li>{@link EmvConst#EMV_TRANS_RF_TERMINATE}: Terminate a transaction.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_ACCEPT}: M/CHIP Offline Approve.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_DENIAL}: CLSS M/CHIP decline.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_GOONLINE}: CLSS M/CHIP Online Request.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_GOONLINE}: CLSS MAG Online Request.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_ACCEPT}: CLSS MAG Approve.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_TRYOTHERINT}: Try another interface.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_SELECT_NEXT_AID}: Select next aid.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_DENIAL}: CLSS MAG Decline.</li>
     * </ul>
     */
    int NAPICLL2PureProcess(ep_opt obj_epopt, rf_transdata obj_rfdata);

    /**
     * Processes an Interac transaction.
     *
     * @param obj_epopt  [IN] Entry_Point Transaction Options.
     * @param obj_rfdata [IN] Contactless transaction mandatory data.
     * @return <ul>
     * <li>{@link EmvConst#EMV_TRANS_RF_TERMINATE}: Terminate a transaction.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_ACCEPT}: M/CHIP Offline Approve.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_DENIAL}: CLSS M/CHIP decline.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_GOONLINE}: CLSS M/CHIP Online Request.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_GOONLINE}: CLSS MAG Online Request.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_ACCEPT}: CLSS MAG Approve.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_TRYOTHERINT}: Try another interface.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_SELECT_NEXT_AID}: Select next aid.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_DENIAL}: CLSS MAG Decline.</li>
     * </ul>
     */
    int NAPICLL2InteracProcess(ep_opt obj_epopt, rf_transdata obj_rfdata);

    /**
     * Processes a Rupay transaction.
     *
     * @param obj_epopt  [IN] Entry_Point Transaction Options.
     * @param obj_rfdata [IN] Contactless transaction mandatory data.
     * @return <ul>
     * <li>{@link EmvConst#EMV_TRANS_RF_TERMINATE}: Terminate a transaction.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_ACCEPT}: M/CHIP Offline Approve.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_DENIAL}: CLSS M/CHIP decline.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_GOONLINE}: CLSS M/CHIP Online Request.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_GOONLINE}: CLSS MAG Online Request.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_GOONLINE_LONGTAP}: CLSS M.CHIP Online Request legacy card.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_GOONLINE_ONLINETAP}: CLSS M.CHIP Online Request no legacy card.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_ACCEPT}: CLSS MAG Approve.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_TRYOTHERINT}: Try another interface.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_SELECT_NEXT_AID}: Select next aid.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_DENIAL}: CLSS MAG Decline.</li>
     * </ul>
     */
    int NAPICLL2RupayProcess(ep_opt obj_epopt, rf_transdata obj_rfdata);
	
	/**
     * Processes a Mir transaction.
     *
     * @param obj_epopt  [IN] Entry_Point Transaction Options.
     * @param obj_rfdata [IN] Contactless transaction mandatory data.
     * @return <ul>
     * <li>{@link EmvConst#EMV_TRANS_RF_TERMINATE}: Terminate a transaction.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_ACCEPT}: M/CHIP Offline Approve.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_DENIAL}: CLSS M/CHIP decline.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_GOONLINE}: CLSS M/CHIP Online Request.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_GOONLINE}: CLSS MAG Online Request.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_GOONLINE_LONGTAP}: CLSS M.CHIP Online Request legacy card.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_GOONLINE_ONLINETAP}: CLSS M.CHIP Online Request no legacy card.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_ACCEPT}: CLSS MAG Approve.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_TRYOTHERINT}: Try another interface.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_SELECT_NEXT_AID}: Select next aid.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_DENIAL}: CLSS MAG Decline.</li>
     * </ul>
     */
    int NAPICLL2MIRProcess(ep_opt obj_epopt, rf_transdata obj_rfdata);
	
	/**
     * Processes a Multibanco transaction.
     *
     * @param obj_epopt  [IN] Entry_Point Transaction Options.
     * @param obj_rfdata [IN] Contactless transaction mandatory data.
     * @return <ul>
     * <li>{@link EmvConst#EMV_TRANS_RF_TERMINATE}: Terminate a transaction.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_ACCEPT}: M/CHIP Offline Approve.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_DENIAL}: CLSS M/CHIP decline.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_GOONLINE}: CLSS M/CHIP Online Request.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_GOONLINE}: CLSS MAG Online Request.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_GOONLINE_LONGTAP}: CLSS M.CHIP Online Request legacy card.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_GOONLINE_ONLINETAP}: CLSS M.CHIP Online Request no legacy card.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_ACCEPT}: CLSS MAG Approve.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_TRYOTHERINT}: Try another interface.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_SELECT_NEXT_AID}: Select next aid.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_DENIAL}: CLSS MAG Decline.</li>
     * </ul>
     */
    int NAPICLL2MultibancoProcess(ep_opt obj_epopt, rf_transdata obj_rfdata);

	/**
     * Processes a Cpace transaction.
     *
     * @param obj_epopt  [IN] Entry_Point Transaction Options.
     * @param obj_rfdata [IN] Contactless transaction mandatory data.
     * @return <ul>
     * <li>{@link EmvConst#EMV_TRANS_RF_TERMINATE}: Terminate a transaction.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_ACCEPT}: M/CHIP Offline Approve.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_DENIAL}: CLSS M/CHIP decline.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_GOONLINE}: CLSS M/CHIP Online Request.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_GOONLINE}: CLSS MAG Online Request.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_GOONLINE_LONGTAP}: CLSS M.CHIP Online Request legacy card.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_GOONLINE_ONLINETAP}: CLSS M.CHIP Online Request no legacy card.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_ACCEPT}: CLSS MAG Approve.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_TRYOTHERINT}: Try another interface.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_SELECT_NEXT_AID}: Select next aid.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_DENIAL}: CLSS MAG Decline.</li>
     * </ul>
     */
    int NAPICLL2CpaceProcess(ep_opt obj_epopt, rf_transdata obj_rfdata);

	/**
     * Processes a Bancomat transaction.
     *
     * @param obj_epopt  [IN] Entry_Point Transaction Options.
     * @param obj_rfdata [IN] Contactless transaction mandatory data.
     * @return <ul>
     * <li>{@link EmvConst#EMV_TRANS_RF_TERMINATE}: Terminate a transaction.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_ACCEPT}: M/CHIP Offline Approve.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_DENIAL}: CLSS M/CHIP decline.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_GOONLINE}: CLSS M/CHIP Online Request.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_GOONLINE}: CLSS MAG Online Request.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_GOONLINE_LONGTAP}: CLSS M.CHIP Online Request legacy card.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_GOONLINE_ONLINETAP}: CLSS M.CHIP Online Request no legacy card.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_ACCEPT}: CLSS MAG Approve.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_TRYOTHERINT}: Try another interface.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_SELECT_NEXT_AID}: Select next aid.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_DENIAL}: CLSS MAG Decline.</li>
     * </ul>
     */
    int NAPICLL2BancomatProcess(ep_opt obj_epopt, rf_transdata obj_rfdata);

	/**
     * Processes a Eftpos transaction.
     *
     * @param obj_epopt  [IN] Entry_Point Transaction Options.
     * @param obj_rfdata [IN] Contactless transaction mandatory data.
     * @return <ul>
     * <li>{@link EmvConst#EMV_TRANS_RF_TERMINATE}: Terminate a transaction.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_ACCEPT}: M/CHIP Offline Approve.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_DENIAL}: CLSS M/CHIP decline.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_GOONLINE}: CLSS M/CHIP Online Request.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_GOONLINE}: CLSS MAG Online Request.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_GOONLINE_LONGTAP}: CLSS M.CHIP Online Request legacy card.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_GOONLINE_ONLINETAP}: CLSS M.CHIP Online Request no legacy card.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_ACCEPT}: CLSS MAG Approve.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_TRYOTHERINT}: Try another interface.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_SELECT_NEXT_AID}: Select next aid.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_DENIAL}: CLSS MAG Decline.</li>
     * </ul>
     */
    int NAPICLL2EftposProcess(ep_opt obj_epopt, rf_transdata obj_rfdata);
	
    /**
     * Finishes a contactless transaction.
     *
     * @param nTransFinal [IN] Final transaction result.
     * @return <ul>
     * <li>0: Success.</li>
     * <li>-1: Failed.</li>
     * </ul>
     */
    int NAPICLL2EntryPointSuspend(int nTransFinal);

    /**
     * Finishes a PayPass transaction.
     *
     * @param nTransFinal [IN] Final result of a transaction (e.g., {@link EmvConst#EMV_TRANS_RF_MCHIP_ACCEPT}, {@link EmvConst#EMV_TRANS_RF_MCHIP_DENIAL}).
     * @return <ul>
     * <li>0: Success.</li>
     * <li>-1: Failed.</li>
     * </ul>
     */
    int NAPICLL2PayPassSuspend(int nTransFinal);

    /**
     * Finishes a PayWave transaction.
     *
     * @param nTransFinal [IN] Final result of a transaction (e.g., {@link EmvConst#EMV_TRANS_RF_MCHIP_ACCEPT}, {@link EmvConst#EMV_TRANS_RF_MCHIP_DENIAL}).
     * @return <ul>
     * <li>0: Success.</li>
     * <li>-1: Failed.</li>
     * </ul>
     */
    int NAPICLL2PayWaveSuspend(int nTransFinal);

    /**
     * Finishes an ExpressPay transaction.
     *
     * @param nTransFinal [IN] Final result of a transaction (e.g., {@link EmvConst#EMV_TRANS_RF_MCHIP_ACCEPT}, {@link EmvConst#EMV_TRANS_RF_MCHIP_DENIAL}).
     * @return <ul>
     * <li>0: Success.</li>
     * <li>-1: Failed.</li>
     * </ul>
     */
    int NAPICLL2ExpressPaySuspend(int nTransFinal);

    /**
     * Finishes a JCB transaction.
     *
     * @param nTransFinal [IN] Final result of a transaction (e.g., {@link EmvConst#EMV_TRANS_RF_MCHIP_ACCEPT}, {@link EmvConst#EMV_TRANS_RF_MCHIP_DENIAL}).
     * @return <ul>
     * <li>0: Success.</li>
     * <li>-1: Failed.</li>
     * </ul>
     */
    int NAPICLL2JCBSuspend(int nTransFinal);

    /**
     * Finishes a DiscoverPay transaction.
     *
     * @param nTransFinal [IN] Final result of a transaction (e.g., {@link EmvConst#EMV_TRANS_RF_MCHIP_ACCEPT}, {@link EmvConst#EMV_TRANS_RF_MCHIP_DENIAL}).
     * @return <ul>
     * <li>0: Success.</li>
     * <li>-1: Failed.</li>
     * </ul>
     */
    int NAPICLL2DiscoverPaySuspend(int nTransFinal);

    /**
     * Finishes a qPBOC transaction.
     *
     * @param nTransFinal [IN] Final result of a transaction (e.g., {@link EmvConst#EMV_TRANS_RF_MCHIP_ACCEPT}, {@link EmvConst#EMV_TRANS_RF_MCHIP_DENIAL}).
     * @return <ul>
     * <li>0: Success.</li>
     * <li>-1: Failed.</li>
     * </ul>
     */
    int NAPICLL2QpbocSuspend(int nTransFinal);

    /**
     * Finishes a Pure transaction.
     *
     * @param nTransFinal [IN] Final result of a transaction (e.g., {@link EmvConst#EMV_TRANS_RF_MCHIP_ACCEPT}, {@link EmvConst#EMV_TRANS_RF_MCHIP_DENIAL}).
     * @return <ul>
     * <li>0: Success.</li>
     * <li>-1: Failed.</li>
     * </ul>
     */
    int NAPICLL2PureSuspend(int nTransFinal);

    /**
     * Finishes an Interac transaction.
     *
     * @param nTransFinal [IN] Final result of a transaction (e.g., {@link EmvConst#EMV_TRANS_RF_MCHIP_ACCEPT}, {@link EmvConst#EMV_TRANS_RF_MCHIP_DENIAL}).
     * @return <ul>
     * <li>0: Success.</li>
     * <li>-1: Failed.</li>
     * </ul>
     */
    int NAPICLL2InteracSuspend(int nTransFinal);

    /**
     * Finishes a Rupay transaction.
     *
     * @param nTransFinal [IN] Final result of a transaction (e.g., {@link EmvConst#EMV_TRANS_RF_MCHIP_ACCEPT}, {@link EmvConst#EMV_TRANS_RF_MCHIP_DENIAL}).
     * @return <ul>
     * <li>0: Success.</li>
     * <li>-1: Failed.</li>
     * </ul>
     */
    int NAPICLL2RupaySuspend(int nTransFinal);
	
	/**
     * Finishes a Mir transaction.
     *
     * @param nTransFinal [IN] Final result of a transaction (e.g., {@link EmvConst#EMV_TRANS_RF_MCHIP_ACCEPT}, {@link EmvConst#EMV_TRANS_RF_MCHIP_DENIAL}).
     * @return <ul>
     * <li>0: Success.</li>
     * <li>-1: Failed.</li>
     * </ul>
     */
    int NAPICLL2MIRSuspend(int nTransFinal);
	
	/**
     * Finishes a Multibanco transaction.
     *
     * @param nTransFinal [IN] Final result of a transaction (e.g., {@link EmvConst#EMV_TRANS_RF_MCHIP_ACCEPT}, {@link EmvConst#EMV_TRANS_RF_MCHIP_DENIAL}).
     * @return <ul>
     * <li>0: Success.</li>
     * <li>-1: Failed.</li>
     * </ul>
     */
    int NAPICLL2MultibancoSuspend(int nTransFinal);

	/**
     * Finishes a Cpace transaction.
     *
     * @param nTransFinal [IN] Final result of a transaction (e.g., {@link EmvConst#EMV_TRANS_RF_MCHIP_ACCEPT}, {@link EmvConst#EMV_TRANS_RF_MCHIP_DENIAL}).
     * @return <ul>
     * <li>0: Success.</li>
     * <li>-1: Failed.</li>
     * </ul>
     */
    int NAPICLL2CpaceSuspend(int nTransFinal);

	/**
     * Finishes a Bancomat transaction.
     *
     * @param nTransFinal [IN] Final result of a transaction (e.g., {@link EmvConst#EMV_TRANS_RF_MCHIP_ACCEPT}, {@link EmvConst#EMV_TRANS_RF_MCHIP_DENIAL}).
     * @return <ul>
     * <li>0: Success.</li>
     * <li>-1: Failed.</li>
     * </ul>
     */
    int NAPICLL2BancomatSuspend(int nTransFinal);
	
	/**
     * Finishes a Eftpos transaction.
     *
     * @param nTransFinal [IN] Final result of a transaction (e.g., {@link EmvConst#EMV_TRANS_RF_MCHIP_ACCEPT}, {@link EmvConst#EMV_TRANS_RF_MCHIP_DENIAL}).
     * @return <ul>
     * <li>0: Success.</li>
     * <li>-1: Failed.</li>
     * </ul>
     */
    int NAPICLL2EftposSuspend(int nTransFinal);
	
    /**
     * Gets Entrypoint version.
     *
     * @return Version
     */
    String NAPICLL2EntryPointGetVersion();

    /**
     * Gets Paypass version.
     *
     * @return Version
     */
    String NAPICLL2PaypassGetVersion();

    /**
     * Gets Paywave Kernel version.
     *
     * @return Version
     */
    String NAPICLL2PaywaveGetVersion();

    /**
     * Gets ExpressPay Kernel version.
     *
     * @return Version
     */
    String NAPICLL2ExpresspayGetVersion();

    /**
     * Gets JCB Kernel version.
     *
     * @return Version
     */
    String NAPICLL2JCBGetVersion();

    /**
     * Gets Discoverpay Kernel version.
     *
     * @return Version
     */
    String NAPICLL2DiscoverpayGetVersion();

    /**
     * Gets qPBOC Kernel version.
     *
     * @return Version
     */
    String NAPICLL2QpbocGetVersion();

    /**
     * Gets Pure Kernel version.
     *
     * @return Version
     */
    String NAPICLL2PureGetVersion();

    /**
     * Gets Interac Kernel version.
     *
     * @return Version
     */
    String NAPICLL2InteracGetVersion();

    /**
     * Gets Rupay Kernel version.
     *
     * @return Version
     */
    String NAPICLL2RupayGetVersion();
	
	/**
     * Gets Mir Kernel version.
     *
     * @return Version
     */
    String NAPICLL2MIRGetVersion();

    /**
     * Gets Multibanco Kernel version.
     *
     * @return Version
     */
    String NAPICLL2MultibancoGetVersion();

	/**
     * Gets Cpace Kernel version.
     *
     * @return Version
     */
    String NAPICLL2CpaceGetVersion();

	/**
     * Gets Bancomat Kernel version.
     *
     * @return Version
     */
    String NAPICLL2BancomatGetVersion();

	/**
     * Gets Eftpos Kernel version.
     *
     * @return Version
     */
    String NAPICLL2EftposGetVersion();

    /**
     * Gets EntryPoint Kernel Checksum.
     *
     * @param checksum [OUT] Kernel checksum.
     * @param size     [IN] The buffer size of checksum. It shall be >=4.
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Success.</li>
     * <li>{@link EmvConst#EMVL2_ERR_PARAM}: Parameter error.</li>
     * <li>{@link EmvConst#EMVL2_ERR_FAIL}: Failed.</li>
     * </ul>
     */
    int NAPIEntryPointGetKernelChecksum(byte[] checksum, int size);

    /**
     * Gets Paypass Kernel Checksum.
     *
     * @param checksum [OUT] Kernel checksum.
     * @param size     [IN] The buffer size of checksum.It shall be >=4.
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Success.</li>
     * <li>{@link EmvConst#EMVL2_ERR_PARAM}: Parameter error.</li>
     * <li>{@link EmvConst#EMVL2_ERR_FAIL}: Failed.</li>
     * </ul>
     */
    int NAPIPaypassGetKernelChecksum(byte[] checksum, int size);

    /**
     * Gets Paywave Kernel Checksum.
     *
     * @param checksum [OUT] Kernel checksum.
     * @param size     [IN] The buffer size of checksum. It shall be >=4.
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Success.</li>
     * <li>{@link EmvConst#EMVL2_ERR_PARAM}: Parameter error.</li>
     * <li>{@link EmvConst#EMVL2_ERR_FAIL}: Failed.</li>
     * </ul>
     */
    int NAPIPaywaveGetKernelChecksum(byte[] checksum, int size);

    /**
     * Gets Expresspay Kernel Checksum.
     *
     * @param checksum [OUT] Kernel checksum.
     * @param size     [IN] The buffer size of checksum. It shall be >=4.
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Success.</li>
     * <li>{@link EmvConst#EMVL2_ERR_PARAM}: Parameter error.</li>
     * <li>{@link EmvConst#EMVL2_ERR_FAIL}: Failed.</li>
     * </ul>
     */
    int NAPIExpresspayGetKernelChecksum(byte[] checksum, int size);

    /**
     * Gets Discoverpay Kernel Checksum.
     *
     * @param checksum [OUT] Kernel checksum.
     * @param size     [IN] The buffer size of checksum. It shall be >=4.
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Success.</li>
     * <li>{@link EmvConst#EMVL2_ERR_PARAM}: Parameter error.</li>
     * <li>{@link EmvConst#EMVL2_ERR_FAIL}: Failed.</li>
     * </ul>
     */
    int NAPIDiscoverpayGetKernelChecksum(byte[] checksum, int size);

    /**
     * Gets Interac Kernel Checksum.
     *
     * @param checksum [OUT] Kernel checksum.
     * @param size     [IN] The buffer size of checksum. It shall be >=4.
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Success.</li>
     * <li>{@link EmvConst#EMVL2_ERR_PARAM}: Parameter error.</li>
     * <li>{@link EmvConst#EMVL2_ERR_FAIL}: Failed.</li>
     * </ul>
     */
    int NAPIInteracGetKernelChecksum(byte[] checksum, int size);

    /**
     * Gets JCB Kernel Checksum.
     *
     * @param checksum [OUT] Kernel checksum.
     * @param size     [IN] The buffer size of checksum.  It shall be >=4.
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Success.</li>
     * <li>{@link EmvConst#EMVL2_ERR_PARAM}: Parameter error.</li>
     * <li>{@link EmvConst#EMVL2_ERR_FAIL}: Failed.</li>
     * </ul>
     */
    int NAPIJCBGetKernelChecksum(byte[] checksum, int size);

    /**
     * Gets Pure Kernel Checksum.
     *
     * @param checksum [OUT] Kernel checksum.
     * @param size     [IN] The buffer size of checksum. It shall be >=4.
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Success.</li>
     * <li>{@link EmvConst#EMVL2_ERR_PARAM}: Parameter error.</li>
     * <li>{@link EmvConst#EMVL2_ERR_FAIL}: Failed.</li>
     * </ul>
     */
    int NAPIPureGetKernelChecksum(byte[] checksum, int size);

    /**
     * Gets Rupay Kernel Checksum.
     *
     * @param checksum [OUT] Kernel checksum.
     * @param size     [IN] The buffer size of checksum. It shall be >=4.
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Success.</li>
     * <li>{@link EmvConst#EMVL2_ERR_PARAM}: Parameter error.</li>
     * <li>{@link EmvConst#EMVL2_ERR_FAIL}: Failed.</li>
     * </ul>
     */
    int NAPIRupayGetKernelChecksum(byte[] checksum, int size);

    /**
     * Gets Qpboc Kernel Checksum.
     *
     * @param checksum [OUT] Kernel checksum.
     * @param size     [IN] The buffer size of checksum. It shall be >=4.
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Success.</li>
     * <li>{@link EmvConst#EMVL2_ERR_PARAM}: Parameter error.</li>
     * <li>{@link EmvConst#EMVL2_ERR_FAIL}: Failed.</li>
     * </ul>
     */
    int NAPIQpbocGetKernelChecksum(byte[] checksum, int size);

    /**
     * Gets MIR Kernel Checksum.
     *
     * @param checksum [OUT] Kernel checksum.
     * @param size     [IN]	The buffer size of checksum. It shall be >=4.
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Success.</li>
     * <li>{@link EmvConst#EMVL2_ERR_PARAM}: Parameter error.</li>
     * <li>{@link EmvConst#EMVL2_ERR_FAIL}: Failed.</li>
     * </ul>
     */
    int NAPIMIRGetKernelChecksum(byte[] checksum, int size);

    /**
     * Gets Multibanco Kernel Checksum.
     *
     * @param checksum [OUT] Kernel checksum.
     * @param size     [IN] The buffer size of checksum. It must be not less than 4.
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Success.</li>
     * <li>{@link EmvConst#EMVL2_ERR_PARAM}: Parameter error.</li>
     * <li>{@link EmvConst#EMVL2_ERR_FAIL}: Failed.</li>
     * </ul>
     */
    int NAPIMultibancoGetKernelChecksum(byte[] checksum, int size);

	/**
     * Gets Cpace Kernel Checksum.
     *
     * @param checksum [OUT] Kernel checksum.
     * @param size     [IN] The buffer size of checksum. It must be not less than 4.
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Success.</li>
     * <li>{@link EmvConst#EMVL2_ERR_PARAM}: Parameter error.</li>
     * <li>{@link EmvConst#EMVL2_ERR_FAIL}: Failed.</li>
     * </ul>
     */
    int NAPICpaceGetKernelChecksum(byte[] checksum, int size);

	/**
     * Gets Bancomat Kernel Checksum.
     *
     * @param checksum [OUT] Kernel checksum.
     * @param size     [IN] The buffer size of checksum. It must be not less than 4.
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Success.</li>
     * <li>{@link EmvConst#EMVL2_ERR_PARAM}: Parameter error.</li>
     * <li>{@link EmvConst#EMVL2_ERR_FAIL}: Failed.</li>
     * </ul>
     */
    int NAPIBancomatGetKernelChecksum(byte[] checksum, int size);
	
	/**
     * Gets Eftpos Kernel Checksum.
     *
     * @param checksum [OUT] Kernel checksum.
     * @param size     [IN] The buffer size of checksum. It must be not less than 4.
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Success.</li>
     * <li>{@link EmvConst#EMVL2_ERR_PARAM}: Parameter error.</li>
     * <li>{@link EmvConst#EMVL2_ERR_FAIL}: Failed.</li>
     * </ul>
     */
    int NAPIEftposGetKernelChecksum(byte[] checksum, int size);
	
    /**
     * Sets the file path of the libs.
     *
     * @param filePath [IN] File path.
     * @return <ul>
     * <li>0: Success</li>
     * <li><0: Failed</li>
     * </ul>
     */
    int NAPICLL2EntryPointInitialize(String filePath);


    /**
     * Sets terminal AIDs.
     *
     * @param termAids [IN] Terminal AIDs.
     */
    void NAPICLL2EntryPointSetTerminalAid(ArrayList<aidlist_clss> termAids);

    /**
     * Sets terminal config data.
     *
     * @param config [IN] Terminal config data.
     */
    void NAPICLL2EntryPointSetEmvConfig(config_clss config);

    /**
     * Whether return to the application after the final selection and before the GPO.
     *
     * @param flag [in] 1: Yes, 0: No
     */
    void NAPICLL2EntryPointSetRunToFinalSel(int flag);

    /**
     * Gets status data.
     *
     * @return 40 bytes of status data.
     */
    byte[] NAPIEMVL2GetStatusData();

    /**
     * Gets detailed transaction error code.
     *
     * @return Error code.
     */
    int NAPIEMVL2GetErrorCode();

    /**
     * Packs data by EMV TLV struct conventions.
     *
     * @param outBuf   [OUT] The buffer pointer of TLV packing data to store.
     * @param bufLen   [IN/OUT] The size of TLV buffer./The actual size of the TLV packing data.
     * @param tag      [IN] Tag definition.
     * @param tagLen   [IN] The length of tag value.
     * @param tagValue [IN] Tag value.
     * @param type     [IN] Reserved.
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Success.</li>
     * <li>{@link EmvConst#EMVL2_ERR_PARAM}: Any parameter error.</li>
     * <li>{@link EmvConst#EMVL2_ERR_OVERFLOW}: The size of buffer is too small.</li>
     * </ul>
     */
    public int NAPIEMVL2PackTLV(byte[] outBuf, int[] bufLen, int tag, int tagLen, byte[] tagValue, int type);

    /**
     * Gets the TLV data of specified tags.
     *
     * @param outBuf  [OUT] The output TLV data.
     * @param bufLen  [IN/OUT] The max length of TLV buffer/The actual length of TLV buffer.
     * @param tagList [IN] The tag list which will be packed to TLV buffer.
     * @param tagNum  [IN] The number of tags.
     * @param control [IN]	0: Not pack empty tag to TLV buffer. 1: Pack empty tag with format "TL" to TLV buffer.
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Success.</li>
     * <li>{@link EmvConst#EMVL2_ERR_PARAM}: Any parameter error.</li>
     * <li>{@link EmvConst#EMVL2_ERR_OVERFLOW}: The size of TLV buffer is too small.</li>
     * </ul>
     */
    int NAPIEMVL2PackTagDataToTLV(byte[] outBuf, int[] bufLen, int[] tagList, int tagNum, int control);

    /**
     * Finds the tag and gets its value.
     *
     * @param tagName   [IN] The tag to get value.
     * @param outData   [OUT] The buffer used to store the tag's value.
     * @param maxOutLen [IN] The max size of the buffer.
     * @return <ul>
     * <li>>= 0: The actual length of the tag value.</li>
     * <li>{@link EmvConst#EMVL2_ERR_PARAM}: Buffer is NULL or max length is 0.</li>
     * <li>{@link EmvConst#EMVL2_ERR_TAG_ABSENT}: Tag is absent.</li>
     * <li>{@link EmvConst#EMVL2_ERR_OVERFLOW}: The length of the tag value is greater than max length.</li>
     * </ul>
     */
    int NAPIEMVL2GetData(int tagName, byte[] outData, int maxOutLen);

    /**
     * Sets value to the specified tag.
     *
     * @param tagName [IN] The tag to set.
     * @param data    [IN] The value set to the tag.
     * @param maxLen  [IN] The size of the buffer.
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Success.</li>
     * <li>{@link EmvConst#EMVL2_ERR_PARAM}: Buffer is NULL.</li>
     * <li>{@link EmvConst#EMVL2_ERR_TAG_UNKNOWN}: Unknown tag.</li>
     * <li>{@link EmvConst#EMVL2_ERR_TAG_REPEAT}: The tag is already exist.</li>
     * <li>{@link EmvConst#EMVL2_ERR_OVERFLOW}: Kernel data space is overflow.</li>
     * </ul>
     */
    int NAPIEMVL2SetData(int tagName, byte[] data, int maxLen);


    /**
     * Gets Data.
     *
     * @param emgetdata
     * @param dataout
     * @param valuelen
     * @return Result code.
     */
    public int NAPIEMVL2ICCGetData(int emgetdata, byte[] dataout, int[] valuelen);

    /**
     * Gets EMV Transaction Detail.
     *
     * @param nRec       [IN] See below:
     *                   <ul>
     *                    <li>>0: Number of records to read.</li>
     *                    <li>=PBOCLOG_SFI: transaction detail SFI.</li>
     *                    <li>=PBOCLOG_RECNUM: Number of records.</li>
     *                    <li>=PBOCLOG_FMT: Transaction detail format.</li>
     *                   </ul>
     * @param pusOut     [OUT] Transaction detail data.
     * @param nOutMaxLen [IN] Max len of transaction detail data.
     * @return <ul>
     * <li><0: Failed.</li>
     * <li>>0: Transaction detail data length.</li>
     * <li>=0: No detail data.</li>
     * </ul>
     */
    public int NAPIEMVL2GetPBOCLog(int nRec, byte[] pusOut, int nOutMaxLen);

    /**
     * Gets EMV ECLoad Log.
     *
     * @param nRec       [IN]  See below:
     *                   <ul>
     *                    <li>>0: Number of records to read.</li>
     *                    <li>=PBOCLOG_SFI: ECLoad log SFI.</li>
     *                    <li>=PBOCLOG_RECNUM: Number of records.</li>
     *                    <li>=PBOCLOG_FMT: ECLoad log format.</li>
     *                   </ul>
     * @param pusOut     [OUT] ECLoad log data.
     * @param nOutMaxLen [IN] Max length of ECLoad log data.
     * @return <ul>
     *     <li><0: Failed.</li>
     *     <li>>0: CLoad log data len.</li>
     *     <li>=0: No log.</li>
     * </ul>
     */
    public int NAPIEMVL2GetecloadLog(int nRec, byte[] pusOut, int nOutMaxLen);

    /**
     * Gets contactless level 2 status.
     *
     * @return Contactless level2 status.
     */
    byte[] NAPICLL2EntryPointGetCLL2Status();

    /**
     * Uses GET DATA Command to get value of the Tag.
     *
     * @param tagName    [IN] TAG name, i.e: 0x9F36
     * @param dataOut    [OUT] Out Value.
     * @param dataOutLen [OUT] Out length.
     * @return <ul>
     * <li>0: Success.</li>
     * <li>-1: Failed.</li>
     * </ul>
     */
    int NAPICLL2EntryPointGetDataByTagName(int tagName, byte[] dataOut, int[] dataOutLen);

    /**
     * Sets Kernel debug mode.
     *
     * @param debugLv Debug level.
     *                <ul>
     *                <li>LV_CLOSE(0): Close debug.</li>
     *                <li>LV_DEBUG(1): Log with normal debug information. This is recommended.</li>
     *                <li>LV_ALL(3): Log with all of the debug information.</li>
     *                </ul>
     */
    void NAPIEMVL2SetDebugMode(int debugLv);

	void NAPICLL2SetIsNDKEMV(int isNDKEMV);


    /**
     * Sets key to PIN.
     *
     * @param KeyValue
     * @return Result code.
     */
    int NAPIEMVSetKeytoPIN(int KeyValue);

    /**
     * Gets TLV value of the tag.
     *
     * @param tagName   [IN] The tag to get.
     * @param outData   [OUT] The value the tag.
     * @param maxOutLen [IN] Max length of the data.
     * @return <ul>
     * <li>0: Tag is not exist.</li>
     * <li>-1: Data length exceeds length limit.</li>
     * <li>>0: the length of the value.</li>
     * </ul>
     */
    int NAPICLL2EntryPointGetData(int tagName, byte[] outData, int maxOutLen);

    /**
     * Sets the value to the tag.
     *
     * @param tagName [IN] The tag to set.
     * @param dataIn  [IN] Value
     * @param maxLen  [IN] Value Length
     * @return <ul>
     * <li>0: Success.</li>
     * <li><0: Failed.</li>
     * <li>-2: No permission for setting this tag.</li>
     * </ul>
     */
    int NAPICLL2EntryPointSetData(int tagName, byte[] dataIn, int maxLen);

    /**
     * Packs tag data with tlv format.
     *
     * @param outBuf  [OUT]    The output packed data.
     * @param bufLen  [IN]    Maximum length of the TLV buffer.
     * @param tagList [IN]     Tags to get value and packed to the TLV buffer.
     * @param tagNum  [IN]     The number of tags.
     * @param control [IN]     0: Not pack empty tag to the TLV buffer. 1: Pack empty tag with format "TL" to the TLV buffer.
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Success.</li>
     * <li>{@link EmvConst#EMVL2_ERR_PARAM}: TLV buffer is null or max buffer length is 0.</li>
     * <li>{@link EmvConst#EMVL2_ERR_OVERFLOW}: Buffer length is too small to store the packed TLV data.</li>
     * </ul>
     */
    int NAPICLL2EntryPointFetchData(byte[] outBuf, int[] bufLen, int[] tagList, int tagNum, int control);

    /**
     * Gets detailed error code.
     *
     * @return Error code.
     */
    int NAPICLL2GetErrorCode();

    /**
     * Gets IC Card Exception status
     *
     * @return <ul>
     * <li>0: Good status</li>
     * <li>1: IC Card is removed/loose while transaction is performing</li>
     * </ul>
     */
    int GetExceptionRemove();

    /**
     * Resets IC Card Exception status.
     */
    void ResetExceptionRemove();

    /**
     * Sets the custom tag list
     * @param tagList
     * @param tagNum
     * @return 0-SUCC, <0-FAIL
     */
    int NAPICLL2SetCustomerTagList(int[] tagList, int tagNum);
    /**
     * Get the Candidate list custom tag value
     * @param aid
     * @param aidlen
     * @param customdata
     * @return 0-SUCC, <0-FAIL
     */
    int NAPICLL2GetCandidateCustomData(byte[] aid, int aidlen, byte[] customdata);

    /**
     * Processes a Girocard transaction.
     *
     * @param obj_epopt  [IN] Entry_Point Transaction Options.
     * @param obj_rfdata [IN] Contactless transaction mandatory data.
     * @return <ul>
     * <li>{@link EmvConst#EMV_TRANS_RF_TERMINATE}: Terminate a transaction.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_ACCEPT}: M/CHIP Offline Approve.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_DENIAL}: CLSS M/CHIP decline.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_GOONLINE}: CLSS M/CHIP Online Request.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_GOONLINE}: CLSS MAG Online Request.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_GOONLINE_LONGTAP}: CLSS M.CHIP Online Request legacy card.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MCHIP_GOONLINE_ONLINETAP}: CLSS M.CHIP Online Request no legacy card.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_ACCEPT}: CLSS MAG Approve.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_TRYOTHERINT}: Try another interface.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_SELECT_NEXT_AID}: Select next aid.</li>
     * <li>{@link EmvConst#EMV_TRANS_RF_MAG_DENIAL}: CLSS MAG Decline.</li>
     * </ul>
     */
    int NAPICLL2GirocardProcess(ep_opt obj_epopt, rf_transdata obj_rfdata);

    /**
     * Finishes a Girocard transaction.
     *
     * @param nTransFinal [IN] Final result of a transaction (e.g., {@link EmvConst#EMV_TRANS_RF_MCHIP_ACCEPT}, {@link EmvConst#EMV_TRANS_RF_MCHIP_DENIAL}).
     * @return <ul>
     * <li>0: Success.</li>
     * <li>-1: Failed.</li>
     * </ul>
     */
    int NAPICLL2GirocardSuspend(int nTransFinal);

    /**
     * Gets Girocard Kernel Checksum.
     *
     * @param checksum [OUT] Kernel checksum.
     * @param size     [IN] The buffer size of checksum. It must be not less than 4.
     * @return <ul>
     * <li>{@link EmvConst#EMVL2_ERR_NONE}: Success.</li>
     * <li>{@link EmvConst#EMVL2_ERR_PARAM}: Parameter error.</li>
     * <li>{@link EmvConst#EMVL2_ERR_FAIL}: Failed.</li>
     * </ul>
     */
    int NAPICLL2GirocardGetKernelChecksum(byte[] checksum, int size);

    /**
     * Gets Girocard Kernel version.
     *
     * @return Version Girocard version.
     */
    String NAPICLL2GirocardGetVersion();

    /**
     * Gets error message according to the error code.
     *
     * @param emvErrorCode EMV error code.
     * @return Error message
     */
    String getErrorMessage(int emvErrorCode);

    void NAPIEMVL2UseExternalReader(boolean isExternalReader);
}
