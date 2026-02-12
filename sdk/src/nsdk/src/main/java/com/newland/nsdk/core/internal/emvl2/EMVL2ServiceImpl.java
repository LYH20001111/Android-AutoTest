package com.newland.nsdk.core.internal.emvl2;

import com.newland.nsdk.core.api.common.emv.EMVErrorMessage;
import com.newland.nsdk.core.api.internal.emvl2.listener.EmvJNIListener;
import com.newland.nsdk.core.api.internal.emvl2.service.EMVL2Service;
import com.newland.nsdk.core.api.internal.emvl2.type.EmvConst;
import com.newland.nsdk.core.api.internal.emvl2.type.aidlist_clss;
import com.newland.nsdk.core.api.internal.emvl2.type.aidlist_emv;
import com.newland.nsdk.core.api.internal.emvl2.type.candidate_emv;
import com.newland.nsdk.core.api.internal.emvl2.type.config_clss;
import com.newland.nsdk.core.api.internal.emvl2.type.ep_opt;
import com.newland.nsdk.core.api.internal.emvl2.type.publickey;
import com.newland.nsdk.core.api.internal.emvl2.type.rf_transdata;
import com.newland.nsdk.core.internal.jni.EmvL2Jni;

import java.util.ArrayList;

public class EMVL2ServiceImpl implements EMVL2Service {

    private volatile static EMVL2ServiceImpl instance;

    public static EMVL2ServiceImpl getInstance() {
        if (instance == null) {
            synchronized (EMVL2ServiceImpl.class) {
                if (instance == null) {
                    instance = new EMVL2ServiceImpl();
                }
            }
        }
        return instance;
    }
    private EMVL2ServiceImpl(){
    }

    /*****************************************************************************
     *@fn NAPIEMVL2SetCallbackFunction
     *@brief Callback function setup.
     *@param callbackFunction       [IN] The callback function pointer.
     *@return description
     *@retval EMVL2_ERR_NONE:	Success.
     *@retval EMVL2_ERR_FAIL:	Fail.
     *@note
     *****************************************************************************/
    @Override
    public int NAPIEMVL2SetCallbackFunction(EmvJNIListener callbackFunction) {
        int ret = EmvL2Jni.getInstance().jniNAPIEMVL2SetCallbackFunction(callbackFunction);
        return ret;
    }

    /*****************************************************************************
     *@fn NAPICLL2SetCallbackFunction
     *@brief Callback function setup.
     *@param  callbackFunction      [IN] The callback function pointer.
     *@return description
     *@retval EMVL2_ERR_NONE:	Success.
     *@retval EMVL2_ERR_FAIL:	Fail.
     *@note
     *****************************************************************************/
    @Override
    public int NAPICLL2SetCallbackFunction(EmvJNIListener callbackFunction) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2SetCallbackFunction(callbackFunction);
        return ret;
    }

    @Override
    public int NAPIEMVL2FreeGlobalVariable() {
        int ret = EmvL2Jni.getInstance().jniNAPIEMVL2FreeGlobalVariable();
        return ret;
    }

    @Override
    public int NAPIEMVL2BuildCandidateList(ArrayList<candidate_emv> candidateList, ArrayList<aidlist_emv> aidList) {
        int ret = EmvL2Jni.getInstance().jniNAPIEMVL2BuildCandidateList(candidateList, aidList);
        return ret;
    }


    @Override
    public int NAPIEMVL2SelectApplication(int selectionID, ArrayList<candidate_emv> candidateList) {
        int ret = EmvL2Jni.getInstance().jniNAPIEMVL2SelectApplication(selectionID, candidateList);
        return ret;
    }

    @Override
    public int NAPIEMVL2InitiateApplication(ArrayList<candidate_emv> candidateList) {
        int ret = EmvL2Jni.getInstance().jniNAPIEMVL2InitiateApplication(candidateList);
        return ret;
    }

    /*****************************************************************************
     *@fn NAPIEMVL2ReadApplicationData
     *@brief Read application data according the AFL. The Read Application Data
     *			function is performed immediately following the Initiate Application
     *			Processing function.
     *@return description
     *@retval EMVL2_ERR_NONE:			Success.
     *@retval EMVL2_ERR_TERMINATE:	AFL format error, or SW1 SW2 not '9000' in response to the
     *				READ RECORD command, or 70 template parase error, or mandatory data missing,
     *				or icc error, the application should terminate the session.
     *@note
     *****************************************************************************/
    @Override
    public int NAPIEMVL2ReadApplicationData() {
        int ret = EmvL2Jni.getInstance().jniNAPIEMVL2ReadApplicationData();
        return ret;
    }

    /*****************************************************************************
     * @Func NAPIEMVL2OfflineDataAuthentication
     * @brief The terminal application shall perform offline data authentication in any order after Read
     *			Application Data but before completion of the terminal action analysis.
     * @param  ddol [IN] 		Default DDOL. If the termianl don't support DDA or default DDOL, passing NULL is OK.
     * @param  ddolLen [IN] 	Default DDOL length.
     * @return description
     * @retval EMVL2_ERR_NONE:			Offline data authentication has been performed successful.
     * @retval EMVL2_ERR_FAIL:			Offline data authentication failed or don't performed.
     * @retval EMVL2_ERR_CAPK_CHECKSUM:CAPK check failed, if operator action is needed, the terminal application shall display an error message.
     * @retval EMVL2_ERR_TERMINATE:	SW1 SW2 not '9000' in response to the
     *			INTERNAL AUTHENTICATE command, or 77 or 80 template parase error,
     *			or icc error, the application should terminate the session.
     *@note
     *****************************************************************************/
    @Override
    public int NAPIEMVL2OfflineDataAuthentication(byte[] ddol, int ddolLen) {
        int ret = EmvL2Jni.getInstance().jniNAPIEMVL2OfflineDataAuthentication(ddol, ddolLen);
        return ret;
    }

    /*****************************************************************************
     * @Func NAPIEMVL2ProcessingRestrictions
     * @brief Check AVN, AUC, Application Effective/Expiration Dates.
     * @return description
     * @retval EMVL2_ERR_NONE:			Processing Restrictions has been performed.
     * @note
     *****************************************************************************/
    @Override
    public int NAPIEMVL2ProcessingRestrictions() {
        int ret = EmvL2Jni.getInstance().jniNAPIEMVL2ProcessingRestrictions();
        return ret;
    }

    @Override
    public int NAPIEMVL2CardholderVerification(byte[] cvmCode, byte[] cvStatus) {
        int ret = EmvL2Jni.getInstance().jniNAPIEMVL2CardholderVerification(cvmCode, cvStatus);
        return ret;
    }

    /*****************************************************************************
     * @Func NAPIEMVL2RiskManagement
     * @brief Perform terminal risk management.
     * @param blackCard   [IN] 0: the card absent on the blacklist. 1: the card present on the blacklist.
     * @param forceOnline [IN] If the merchant force the transaction online. 0: No 1: Yes
     * @param logAmount   [IN] The transaction amount of the card has completed before, format n6.
     * @param targetPercentage   [IN] Target Percentage to be Used for Random Selection? (in the range of 0 to 99)
     * @param maxTargetPercentage   [IN] Maximum Target Percentage to be Used for Biased Random Selection? (also in the range of 0 to 99)
     * @param thresholdValue   [IN] Threshold Value for Biased Random Selection (which must be zero or a positive number less than the floor limit), format n4.
     * @return description
     * @retval EMVL2_ERR_NONE:	Risk management has performed.
     * @note
     *****************************************************************************/
    @Override
    public int NAPIEMVL2RiskManagement(int blackCard, int forceOnline, byte[] logAmount, int targetPercentage, int maxTargetPercentage, byte[] thresholdValue) {
        int ret = EmvL2Jni.getInstance().jniNAPIEMVL2RiskManagement(blackCard, forceOnline, logAmount, targetPercentage, maxTargetPercentage, thresholdValue);
        return ret;
    }

    /*****************************************************************************
     * @Func NAPIEMVL2TerminalActionAnalysis
     * @brief Perform terminal action analysis. If unable online,
     *			the terminal shall set unableOnline = 1 and pass tacDefault to kernel to determine whether to
     *			approve or reject the transaction offline.
     * @param tacDenial   [IN] Terminal Action Code - Denial, 5 bytes
     * @param tacOnline   [IN] Terminal Action Code - Online, 5 bytes
     * @param tacDefault   [IN] Terminal Action Code - Default, 5 bytes
     * @param unableOnline   [IN] 0: ignore. 1: the terminal is for any reason unable to process the transaction online.
     * @param tdol   [IN] Default TDOL.
     * @param tdolLen   [IN] Default TDOL length.
     * @param acType   [OUT] The request AC type by the result of terminal action analysis.
     * @return description
     * @retval EMVL2_ERR_NONE:		Succ, the termianl should check or change acType before GAC.
     * @retval EMVL2_ERR_PARAM:	Any parameter error.
     * @note
     *****************************************************************************/
    @Override
    public int NAPIEMVL2TerminalActionAnalysis(int[] acType, byte[] tacDenial, byte[] tacOnline, byte[] tacDefault, int unableOnline, byte[] tdol, int tdolLen) {
        int ret = EmvL2Jni.getInstance().jniNAPIEMVL2TerminalActionAnalysis(acType, tacDenial, tacOnline, tacDefault, unableOnline, tdol, tdolLen);
        return ret;
    }

    /*****************************************************************************
     * @Func NAPIEMVL2GenerateAC1st
     * @brief Send first generate AC command to ICC to perform card action analysis.
     *			CDA signature should be requested if terminal and ICC supported both.
     *			The terminal application check return value to obtain the card desicion
     *			of offline approval, offline declined or online request. If ICC requested
     *			advice, the terminal application should be send a advice message to the issuer.
     * @param reqACType   [IN] The termianl final desicion of AC type.
     * @param advice   [OUT] 0: No 1: Yes.
     * @return description
     * @retval EMVL2_ERR_TERMINATE: 	77 or 80 template parase error, or CID format error,
     *			or icc error, the application should terminate the session.
     * @retval EMVL2_ERR_GO_ONLINE: 	The termianl should process transaction online.
     * @retval EMVL2_ERR_DECLINE:		The termianl should decline the transaction.
     * @retval EMVL2_ERR_ACCEPT:		The termianl should accept the transaction
     * @retval EMVL2_ERR_GAC2_AAC:		The termianl should request AAC by calling NAPIEMVL2GenerateAC2nd.
     * @note
     *****************************************************************************/
    @Override
    public int NAPIEMVL2GenerateAC1st(int[] advice, int reqACType) {
        int ret = EmvL2Jni.getInstance().jniNAPIEMVL2GenerateAC1st(advice, reqACType);
        return ret;
    }

    /*****************************************************************************
     *@Func NAPIEMVL2GenerateAC2nd
     *@brief Send second generate AC command to ICC to perform card action analysis.
     *			CDA signature always be requested if reqACType is TC.
     *			If online processing performed after NAPIEMVL2GenerateAC1st, the termianl
     *			application analysis the issuer authentication response code and
     *			make the final desicion of reqACType. Authentication response code (TAG 8A)
     *			should be updated by application. If issuer responses Issuer Authentication Data (TAG 91),
     *			the application should also store first for issuer authentication.
     *			If termianl unable online, NAPIEMVL2TerminalActionAnalysis should be
     *			called first to make the final desicion before calling this function.
     *			After call this function, the terminal application check the return value
     *			to obtain the card desicion of TC approval or AAC declined. If ICC requested
     *			advice, the terminal application should be send a advice message to the issuer.
     *@param reqACType       [IN] Online result or issuer voice result of the issuer.
     *@param script71       [IN] Type 71 issuer script.
     *@param script71Len       [IN] The length of script71.
     *@param script72       [IN] Type 72 issuer script.
     *@param script72Len       [IN] The length of script72.
     *@param scriptResultLen       [IN] The size of buffer scriptResult.
     *@param advice       [OUT] 0: No 1: Yes.
     *@param scriptResult       [OUT] Return the script result.
     *@param scriptResultLen       [OUT] The real length of scriptResult.
     *@return description
     *@retval EMVL2_ERR_PARAM:		Any parameter error.
     *@retval EMVL2_ERR_FORMAT: 		There is not Issuer Script Command(tag 86) in script.
     *@retval EMVL2_ERR_TERMINATE: 	77 or 80 template parase error, or CID format error,
     *				or icc error, the application should terminate the session.
     *@retval EMVL2_ERR_DECLINE:		The termianl should decline the transaction.
     *@retval EMVL2_ERR_ACCEPT:		The termianl should accept the transaction
     *@note
     *****************************************************************************/
    @Override
    public int NAPIEMVL2GenerateAC2nd(int[] advice, int reqACType, byte[] script71, int script71Len, byte[] script72, int script72Len, byte[] scriptResult, int[] scriptResultLen) {
        int ret = EmvL2Jni.getInstance().jniNAPIEMVL2GenerateAC2nd(advice, reqACType, script71, script71Len, script72, script72Len, scriptResult, scriptResultLen);
        return ret;
    }

    /*****************************************************************************
     *@Func NAPIEMVL2InitTagData
     *@brief Clear all the tag data in data store space.
     *@return description
     *@retval EMVL2_ERR_NONE:	Success.
     *@retval EMVL2_ERR_FAIL:	Fail.
     *@note
     *****************************************************************************/
    @Override
    public int NAPIEMVL2InitTagData() {
        int ret = EmvL2Jni.getInstance().jniNAPIEMVL2InitTagData();
        return ret;
    }

    /*****************************************************************************
     *@Func NAPIEMVL2ExistTag
     *@brief Check the special tag wheather exist in the data space.
     *@param tag       [IN] The tag which want to check.
     *@return description
     *@retval EMVL2_ERR_NONE: 		present.
     *@retval EMVL2_ERR_TAG_ABSENT: 	absent
     *@note
     *****************************************************************************/
    @Override
    public int NAPIEMVL2ExistTag(int tag) {
        int ret = EmvL2Jni.getInstance().jniNAPIEMVL2ExistTag(tag);
        return ret;
    }

    /*****************************************************************************
     *@Func NAPIEMVL2GetPinpk
     *@brief The terminal application get PIN pk for offline encryption PIN verification.
     *@param pinPK       [OUT] PIN pk.
     *@return description
     *@retval EMVL2_ERR_NONE:	Succ.
     *@retval EMVL2_ERR_FAIL:	Fail.
     *@note
     *****************************************************************************/
    @Override
    public int NAPIEMVL2GetPinpk(publickey pinPK) {
        int ret = EmvL2Jni.getInstance().jniNAPIEMVL2GetPinpk(pinPK);
        return ret;
    }

    /*****************************************************************************
     *@Func NAPIEMVL2Initialize
     *@brief Initialize kernel status and clear kernel configuration.
     *			This API must be call to clear kernel internal status and all tag data space and callback function
     *			set will be cleared.
     *@return description
     *@retval EMVL2_ERR_NONE:	Success.
     *@retval EMVL2_ERR_FAIL:	Fail.
     *@note
     *****************************************************************************/
    @Override
    public int NAPIEMVL2Initialize() {
        int ret = EmvL2Jni.getInstance().jniNAPIEMVL2Initialize();
        return ret;
    }

    /*****************************************************************************
     *@Func NAPIEMVL2SetConfig
     *@brief Set kernel configuration.
     *@param opt       [IN] Configuration bitmap like macro definitions: EMVL2_SUPPORT_XXX
     *@param val       [IN] 0: Unset, 1: Set.
     *@return description
     *@retval EMVL2_ERR_NONE:	Success.
     *@note
     *****************************************************************************/
    @Override
    public int NAPIEMVL2SetConfig(int opt, int val) {
        int ret = EmvL2Jni.getInstance().jniNAPIEMVL2SetConfig(opt, val);
        return ret;
    }

    /*****************************************************************************
     *@Func NAPIEMVL2GetConfig
     *@brief Get kernel current configuration.
     *@param opt       [IN] Configuration bitmap like macro definitions: EMVL2_SUPPORT_XXX
     *@return description
     *@retval 0:	unset.
     *@retval 1:	set.
     *@note
     *****************************************************************************/

    @Override
    public int NAPIEMVL2GetConfig(int opt) {
        int ret = EmvL2Jni.getInstance().jniNAPIEMVL2GetConfig(opt);
        return ret;
    }

    /*****************************************************************************
     *@Func NAPIEMVL2GetVersion
     *@brief Get the EMV level2 version infomation.
     *@return description
     *@retval value: description
     *@note
     *****************************************************************************/
    @Override
    public String NAPIEMVL2GetVersion() {
        String ret = EmvL2Jni.getInstance().jniNAPIEMVL2GetVersion();
        return ret;
    }

    /*****************************************************************************
     *@Func NAPIEMVL2GetConfigChecksum
     *@brief Get kernel config checksum.
     *@param checksum       [IN] The buffer used for storing the checksum.
     *@param size       [IN] The buffer size of checksum. It must be not less than 4.
     *@param checksum       [OUT] Kernel config checksum.
     *@return description
     *@retval EMVL2_ERR_NONE
     *@retval EMVL2_ERR_PARAM
     *@retval EMVL2_ERR_FAIL
     *@note
     *****************************************************************************/
    @Override
    public int NAPIEMVL2GetConfigChecksum(byte[] checksum, int size) {
        int ret = EmvL2Jni.getInstance().jniNAPIEMVL2GetConfigChecksum(checksum, size);
        return ret;
    }

    /*****************************************************************************
     *@Func NAPIEMVL2GetKernelChecksum
     *@brief Get kernel checksum.
     *@param checksum       [IN] The buffer used for storing the checksum.
     *@param size       [IN] The buffer size of checksum. It must be not less than 4.
     *@param checksum       [OUT] Kernel checksum.
     *@return description
     *@retval EMVL2_ERR_NONE
     *@retval EMVL2_ERR_PARAM
     *@retval EMVL2_ERR_FAIL
     *@note
     *****************************************************************************/
    @Override
    public int NAPIEMVL2GetKernelChecksum(byte[] checksum, int size) {
        int ret = EmvL2Jni.getInstance().jniNAPIEMVL2GetKernelChecksum(checksum, size);
        return ret;
    }

    /*****************************************************************************
     *@fn jniNAPICLL2PerformTransaction
     *@brief contactless transation processing
     *@param obj_epopt       [IN][OUT] Entry_Point Trading Options
     *@param obj_rfdata       [IN][OUT] Trans Data Options
     *@param Ctrl       [IN] some ctrl data,
    byte 1 seekcard flag
    NO SEEK CARD =0,
    SEEK CARD IN APP = 1,
    SEEK CARD IN SERVER = 2,
    ACTIVE CARD IN SERVER = 3,
    btye 2 qpboc getdata flag
    QPBOC NONEED GET DATA = 0,
    QPBOC NEED GET DATA = 1,
    byte 3 process light Flag(see the below, it need set the all four lights status)
    byte 4 card read ok light Flag(see the below, it need set the all four lights status)
    bit 8-7 first light
    LED_RFID_BLUE_ON = 0x40,
    LED_RFID_BLUE_OFF = 0x80,
    LED_RFID_BLUE_FLICK = 0xc0,
    bit 6-5 second light
    LED_RFID_GREEN_ON = 0x10,
    LED_RFID_GREEN_OFF = 0x20,
    LED_RFID_GREEN_FLICK = 0x30,
    bit 4-3 third light
    LED_RFID_YELLOW_ON = 0x04,
    LED_RFID_YELLOW_OFF = 0x08,
    LED_RFID_YELLOW_FLICK = 0x0c,
    bit 2-1 four light
    LED_RFID_RED_ON = 0x01,
    LED_RFID_RED_OFF = 0x02,
    LED_RFID_RED_FLICK = 0x03,
    byte 5 AfterFinalSelect callback Flag
    NO NEED CALLBACK = 0,
    NEED CALLBACK = 1,
     *@param ProcessData       [IN] the tlv trans data
     *@param ProcessDataLen       [IN] tlv Date len
     *@return transaction result
     *@note
     *****************************************************************************/

    @Override
    public int NAPICLL2PerformTransaction(ep_opt obj_epopt, rf_transdata obj_rfdata, byte[] Ctrl, byte[] ProcessData, int ProcessDataLen) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2PerformTransaction(obj_epopt, obj_rfdata, Ctrl, ProcessData, ProcessDataLen);
        return ret;
    }

    /**
     * @param obj_epopt  --- Entry_Point Trading Options
     * @param obj_rfdata --- RF data
     * @return
     * @Func: NAPICLL2EntryPointProcess
     * @brief Entry_Point transaction
     * @li <0 FAIL
     */

    @Override
    public int NAPICLL2EntryPointProcess(ep_opt obj_epopt, rf_transdata obj_rfdata) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2EntryPointProcess(obj_epopt, obj_rfdata);
        return ret;
    }

    /**
     * @param obj_epopt  --- Entry_Point Trading Options
     * @param obj_rfdata --- RF data
     * @return
     * @Func: NAPICLL2PayPassProcess
     * @brief PayPass transaction
     * @li <0 FAIL
     */

    @Override
    public int NAPICLL2PayPassProcess(ep_opt obj_epopt, rf_transdata obj_rfdata) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2PayPassProcess(obj_epopt, obj_rfdata);
        return ret;
    }

    /**
     * @param obj_epopt  --- Entry_Point Trading Options
     * @param obj_rfdata --- RF data
     * @return
     * @Func: NAPICLL2PayWaveProcess
     * @brief PayWave transaction
     * @li <0                         FAIL
     */

    @Override
    public int NAPICLL2PayWaveProcess(ep_opt obj_epopt, rf_transdata obj_rfdata) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2PayWaveProcess(obj_epopt, obj_rfdata);
        return ret;
    }

    /**
     * @param obj_epopt  --- Entry_Point Trading Options
     * @param obj_rfdata --- RF data
     * @return
     * @Func: NAPICLL2ExpressPayProcess
     * @li <0                         FAIL
     */

    @Override
    public int NAPICLL2ExpressPayProcess(ep_opt obj_epopt, rf_transdata obj_rfdata) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2ExpressPayProcess(obj_epopt, obj_rfdata);
        return ret;
    }

    /**
     * @param obj_epopt  --- Entry_Point Trading Options
     * @param obj_rfdata --- RF data
     * @return
     * @Func: NAPICLL2JCBProcess
     * @brief JCB transaction
     * @li <0                         FAIL
     */

    @Override
    public int NAPICLL2JCBProcess(ep_opt obj_epopt, rf_transdata obj_rfdata) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2JCBProcess(obj_epopt, obj_rfdata);
        return ret;
    }

    /**
     * @param obj_epopt  --- Entry_Point Trading Options
     * @param obj_rfdata --- RF data
     * @return
     * @Func: NAPICLL2DiscoverPayProcess
     * @brief DiscoverPay transaction
     * @li <0                         FAIL
     */

    @Override
    public int NAPICLL2DiscoverPayProcess(ep_opt obj_epopt, rf_transdata obj_rfdata) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2DiscoverPayProcess(obj_epopt, obj_rfdata);
        return ret;
    }

    /**
     * @param obj_epopt  --- Entry_Point Trading Options
     * @param obj_rfdata --- RF data
     * @return
     * @Func: NAPICLL2QpbocProcess
     * @brief Qpboc transaction
     * @li <0                         FAIL
     */
    @Override
    public int NAPICLL2QpbocProcess(ep_opt obj_epopt, rf_transdata obj_rfdata) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2QpbocProcess(obj_epopt, obj_rfdata);
        return ret;
    }

    /**
     * @param obj_epopt  --- Entry_Point Trading Options
     * @param obj_rfdata --- RF data
     * @return
     * @Func: NAPICLL2PureProcess
     * @brief Pure transaction
     * @li <0                         FAIL
     */
    @Override
    public int NAPICLL2PureProcess(ep_opt obj_epopt, rf_transdata obj_rfdata) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2PureProcess(obj_epopt, obj_rfdata);
        return ret;
    }

    /**
     * @param obj_epopt  --- Entry_Point Trading Options
     * @param obj_rfdata --- RF data
     * @return
     * @Func: NAPICLL2InteracProcess
     * @li <0                         FAIL
     */
    @Override
    public int NAPICLL2InteracProcess(ep_opt obj_epopt, rf_transdata obj_rfdata) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2InteracProcess(obj_epopt, obj_rfdata);
        return ret;
    }

    /**
     * @param obj_epopt  --- Entry_Point Trading Options
     * @param obj_rfdata --- RF data
     * @return
     * @Func: NAPICLL2RupayProcess
     * @brief Rupay transaction
     * @li <0                         FAIL
     */
    @Override
    public int NAPICLL2RupayProcess(ep_opt obj_epopt, rf_transdata obj_rfdata) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2RupayProcess(obj_epopt, obj_rfdata);
        return ret;
    }

    /**
     * @param obj_epopt  --- Entry_Point Trading Options
     * @param obj_rfdata --- RF data
     * @return
     * @Func: NAPICLL2MIRProcess
     * @brief MIR transaction
     * @li <0                         FAIL
     */
    @Override
    public int NAPICLL2MIRProcess(ep_opt obj_epopt, rf_transdata obj_rfdata) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2MIRProcess(obj_epopt, obj_rfdata);
        return ret;
    }

    /**
     * @param obj_epopt  --- Entry_Point Trading Options
     * @param obj_rfdata --- RF data
     * @return
     * @Func: NAPICLL2MultibancoProcess
     * @brief Multibanco transaction
     * @li <0                         FAIL
     */
    @Override
    public int NAPICLL2MultibancoProcess(ep_opt obj_epopt, rf_transdata obj_rfdata) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2MultibancoProcess(obj_epopt, obj_rfdata);
        return ret;
    }

	/**
     * @param obj_epopt  --- Entry_Point Trading Options
     * @param obj_rfdata --- RF data
     * @return
     * @Func: NAPICLL2CpaceProcess
     * @brief Cpace transaction
     * @li <0                         FAIL
     */
    @Override
    public int NAPICLL2CpaceProcess(ep_opt obj_epopt, rf_transdata obj_rfdata) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2CpaceProcess(obj_epopt, obj_rfdata);
        return ret;
    }

	/**
     * @param obj_epopt  --- Entry_Point Trading Options
     * @param obj_rfdata --- RF data
     * @return
     * @Func: NAPICLL2BancomatProcess
     * @brief Bancomat transaction
     * @li <0                         FAIL
     */
    @Override
    public int NAPICLL2BancomatProcess(ep_opt obj_epopt, rf_transdata obj_rfdata) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2BancomatProcess(obj_epopt, obj_rfdata);
        return ret;
    }
	/**
     * @param obj_epopt  --- Entry_Point Trading Options
     * @param obj_rfdata --- RF data
     * @return
     * @Func: NAPICLL2EftposProcess
     * @brief Eftpos transaction
     * @li <0                         FAIL
     */
    @Override
    public int NAPICLL2EftposProcess(ep_opt obj_epopt, rf_transdata obj_rfdata) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2EftposProcess(obj_epopt, obj_rfdata);
        return ret;
    }
    /**
     * @param nTransFinal    ---Final transaction result
     * @return
     * @Func: NAPICLL2EntryPointSuspend
     * @brief Entry_Point End of transaction execution function
     * @li 0 SUCC
     * @li -1 FAIL
     */
    @Override
    public int NAPICLL2EntryPointSuspend(int nTransFinal) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2EntryPointSuspend(nTransFinal);
        return ret;
    }

    /**
     * @Func: NAPICLL2PayPassSuspend
     * @brief Paypass RF card transaction End of transaction execution functions.
     * @Param: nTransFinal the final result of a transaction (transactions accepted, transactions refuse ...)
     * @Return: 0 success
     * -1 Failed
     */
    @Override
    public int NAPICLL2PayPassSuspend(int nTransFinal) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2PayPassSuspend(nTransFinal);
        return ret;
    }

    /**
     * @Func: NAPICLL2PayWaveSuspend
     * @brief PayWave RF card transaction End of transaction execution functions.
     * @Param: nTransFinal the final result of a transaction (transactions accepted, transactions refuse ...)
     * @Return: 0 success
     * -1 Failed
     */
    @Override
    public int NAPICLL2PayWaveSuspend(int nTransFinal) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2PayWaveSuspend(nTransFinal);
        return ret;
    }

    /**
     * @Func: NAPICLL2ExpressPaySuspend
     * @brief Expresspay RF card transaction End of transaction execution functions.
     * @Param: nTransFinal the final result of a transaction (transactions accepted, transactions refuse ...)
     * @Return: 0 success
     * -1 Failed
     */
    @Override
    public int NAPICLL2ExpressPaySuspend(int nTransFinal) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2ExpressPaySuspend(nTransFinal);
        return ret;
    }

    /**
     * @Func: NAPICLL2JCBSuspend
     * @brief JCB RF card transaction End of transaction execution functions.
     * @Param: nTransFinal the final result of a transaction (transactions accepted, transactions refuse ...)
     * @Return: 0 success
     * -1 Failed
     */
    @Override
    public int NAPICLL2JCBSuspend(int nTransFinal) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2JCBSuspend(nTransFinal);
        return ret;
    }

    /**
     * @Func: NAPICLL2DiscoverPaySuspend
     * @brief Discover RF card transaction End of transaction execution functions.
     * @Param: nTransFinal the final result of a transaction (transactions accepted, transactions refuse ...)
     * @Return: 0 success
     * -1 Failed
     */
    @Override
    public int NAPICLL2DiscoverPaySuspend(int nTransFinal) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2DiscoverPaySuspend(nTransFinal);
        return ret;
    }

    /**
     * @Func: NAPICLL2QpbocSuspend
     * @brief Qpboc RF card transaction End of transaction execution functions.
     * @Param: nTransFinal the final result of a transaction (transactions accepted, transactions refuse ...)
     * @Return: 0 success
     * -1 Failed
     */
    @Override
    public int NAPICLL2QpbocSuspend(int nTransFinal) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2QpbocSuspend(nTransFinal);
        return ret;
    }

    /**
     * @Func: NAPICLL2PureSuspend
     * @brief Pure RF card transaction End of transaction execution functions.
     * @Param: nTransFinal the final result of a transaction (transactions accepted, transactions refuse ...)
     * @Return: 0 success
     * -1 Failed
     */
    @Override
    public int NAPICLL2PureSuspend(int nTransFinal) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2PureSuspend(nTransFinal);
        return ret;
    }

    /**
     * @Func: NAPICLL2InteracSuspend
     * @brief Interac RF card transaction End of transaction execution functions.
     * @Param: nTransFinal the final result of a transaction (transactions accepted, transactions refuse ...)
     * @Return: 0 success
     * -1 Failed
     */
    @Override
    public int NAPICLL2InteracSuspend(int nTransFinal) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2InteracSuspend(nTransFinal);
        return ret;
    }

    /**
     * @Func: NAPICLL2RupaySuspend
     * @brief Rupay RF card transaction End of transaction execution functions.
     * @Param: nTransFinal the final result of a transaction (transactions accepted, transactions refuse ...)
     * @Return: 0 success
     * -1 Failed
     */
    @Override
    public int NAPICLL2RupaySuspend(int nTransFinal) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2RupaySuspend(nTransFinal);
        return ret;
    }

    /**
     * @Func: NAPICLL2MIRSuspend
     * @brief MIR RF card transaction End of transaction execution functions.
     * @Param: nTransFinal the final result of a transaction (transactions accepted, transactions refuse ...)
     * @Return: 0 success
     * -1 Failed
     */
    @Override
    public int NAPICLL2MIRSuspend(int nTransFinal) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2MIRSuspend(nTransFinal);
        return ret;
    }

    /**
     * @Func: NAPICLL2MultibancoSuspend
     * @brief Multibanco RF card transaction End of transaction execution functions.
     * @Param: nTransFinal the final result of a transaction (transactions accepted, transactions refuse ...)
     * @Return: 0 success
     * -1 Failed
     */
    @Override
    public int NAPICLL2MultibancoSuspend(int nTransFinal) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2MultibancoSuspend(nTransFinal);
        return ret;
    }

	/**
     * @Func: NAPICLL2CpaceSuspend
     * @brief Cpace RF card transaction End of transaction execution functions.
     * @Param: nTransFinal the final result of a transaction (transactions accepted, transactions refuse ...)
     * @Return: 0 success
     * -1 Failed
     */
    @Override
    public int NAPICLL2CpaceSuspend(int nTransFinal) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2CpaceSuspend(nTransFinal);
        return ret;
    }

	/**
     * @Func: NAPICLL2BancomatSuspend
     * @brief Bancomat RF card transaction End of transaction execution functions.
     * @Param: nTransFinal the final result of a transaction (transactions accepted, transactions refuse ...)
     * @Return: 0 success
     * -1 Failed
     */
    @Override
    public int NAPICLL2BancomatSuspend(int nTransFinal) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2BancomatSuspend(nTransFinal);
        return ret;
    }
	/**
     * @Func: NAPICLL2EftposSuspend
     * @brief Eftpos RF card transaction End of transaction execution functions.
     * @Param: nTransFinal the final result of a transaction (transactions accepted, transactions refuse ...)
     * @Return: 0 success
     * -1 Failed
     */
    @Override
    public int NAPICLL2EftposSuspend(int nTransFinal) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2EftposSuspend(nTransFinal);
        return ret;
    }
    /**
     * @Func: NAPICLL2EntryPointGetVersion
     * @brief: get Entrypoint Kernel Version
     * @Param:
     * @Return:
     * @retval: Version
     */
    @Override
    public String NAPICLL2EntryPointGetVersion() {
        String ret = EmvL2Jni.getInstance().jniNAPICLL2EntryPointGetVersion();
        return ret;
    }

    /**
     * @Func: NAPICLL2PaypassGetVersion
     * @brief: get Paypass Kernel Version
     * @Param:
     * @Return:
     * @retval: Version
     */
    @Override
    public String NAPICLL2PaypassGetVersion() {
        String ret = EmvL2Jni.getInstance().jniNAPICLL2PaypassGetVersion();
        return ret;
    }

    /**
     * @Func: NAPICLL2PaywaveGetVersion
     * @brief: get Paywave Kernel Version
     * @Param:
     * @Return:
     * @retval: Version
     */
    @Override
    public String NAPICLL2PaywaveGetVersion() {
        String ret = EmvL2Jni.getInstance().jniNAPICLL2PaywaveGetVersion();
        return ret;
    }

    /**
     * @Func: NAPICLL2ExpresspayGetVersion
     * @brief: get ExpressPay Kernel Version
     * @Param:
     * @Return:
     * @retval: Version
     */
    @Override
    public String NAPICLL2ExpresspayGetVersion() {
        String ret = EmvL2Jni.getInstance().jniNAPICLL2ExpresspayGetVersion();
        return ret;
    }

    /**
     * @Func: NAPICLL2JCBGetVersion
     * @brief: get JCB Kernel Version
     * @Param:
     * @Return:
     * @retval: Version
     */
    @Override
    public String NAPICLL2JCBGetVersion() {
        String ret = EmvL2Jni.getInstance().jniNAPICLL2JCBGetVersion();
        return ret;
    }

    /**
     * @Func: NAPICLL2DiscoverpayGetVersion
     * @brief: get Discoverpay Kernel Version
     * @Param:
     * @Return:
     * @retval: Version
     */
    @Override
    public String NAPICLL2DiscoverpayGetVersion() {
        String ret = EmvL2Jni.getInstance().jniNAPICLL2DiscoverpayGetVersion();
        return ret;
    }

    /**
     * @Func: NAPICLL2QpbocGetVersion
     * @brief: get Qpboc Kernel Version
     * @Param:
     * @Return:
     * @retval: Version
     */
    @Override
    public String NAPICLL2QpbocGetVersion() {
        String ret = EmvL2Jni.getInstance().jniNAPICLL2QpbocGetVersion();
        return ret;
    }

    /**
     * @Func: NAPICLL2PureGetVersion
     * @brief: get Pure Kernel Version
     * @Param:
     * @Return:
     * @retval: Version
     */
    @Override
    public String NAPICLL2PureGetVersion() {
        String ret = EmvL2Jni.getInstance().jniNAPICLL2PureGetVersion();
        return ret;
    }

    /**
     * @Func: NAPICLL2InteracGetVersion
     * @brief: get Interac Kernel Version
     * @Param:
     * @Return:
     * @retval: Version
     */
    @Override
    public String NAPICLL2InteracGetVersion() {
        String ret = EmvL2Jni.getInstance().jniNAPICLL2InteracGetVersion();
        return ret;
    }

    /**
     * @Func: NAPICLL2RupayGetVersion
     * @brief: get Rupay Kernel Version
     * @Param:
     * @Return:
     * @retval: Version
     */
    @Override
    public String NAPICLL2RupayGetVersion() {
        String ret = EmvL2Jni.getInstance().jniNAPICLL2RupayGetVersion();
        return ret;
    }

    /**
     * @Func: NAPICLL2MIRGetVersion
     * @brief: get MIR Kernel Version
     * @Param:
     * @Return:
     * @retval: Version
     */
    @Override
    public String NAPICLL2MIRGetVersion() {
        String ret = EmvL2Jni.getInstance().jniNAPICLL2MIRGetVersion();
        return ret;
    }

    /**
     * @Func: NAPICLL2MultibancoGetVersion
     * @brief: get Multibanco Kernel Version
     * @Param:
     * @Return:
     * @retval: Version
     */
    @Override
    public String NAPICLL2MultibancoGetVersion() {
        String ret = EmvL2Jni.getInstance().jniNAPICLL2MultibancoGetVersion();
        return ret;
    }

	/**
     * @Func: NAPICLL2CpaceGetVersion
     * @brief: get Cpace Kernel Version
     * @Param:
     * @Return:
     * @retval: Version
     */
    @Override
    public String NAPICLL2CpaceGetVersion() {
        String ret = EmvL2Jni.getInstance().jniNAPICLL2CpaceGetVersion();
        return ret;
    }
	
	/**
     * @Func: NAPICLL2BancomatGetVersion
     * @brief: get Bancomat Kernel Version
     * @Param:
     * @Return:
     * @retval: Version
     */
    @Override
    public String NAPICLL2BancomatGetVersion() {
        String ret = EmvL2Jni.getInstance().jniNAPICLL2BancomatGetVersion();
        return ret;
    }
	/**
     * @Func: NAPICLL2EftposGetVersion
     * @brief: get Eftpos Kernel Version
     * @Param:
     * @Return:
     * @retval: Version
     */
    @Override
    public String NAPICLL2EftposGetVersion() {
        String ret = EmvL2Jni.getInstance().jniNAPICLL2EftposGetVersion();
        return ret;
    }
    /**
     * @param checksum [IN] The buffer used for storing the checksum.
     * @param size [IN] The buffer size of checksum. It must be not less than 4.
     * @param checksum [OUT] Kernel checksum.
     * @return description
     * @Func: NAPIEntryPointGetKernelChecksum
     * @brief: get EntryPoint Kernel Checksum
     * @retval EMVL2_ERR_NONE
     * @retval EMVL2_ERR_PARAM
     * @retval EMVL2_ERR_FAIL
     */
    @Override
    public int NAPIEntryPointGetKernelChecksum(byte[] checksum, int size) {
        int ret = EmvL2Jni.getInstance().jniNAPIEntryPointGetKernelChecksum(checksum, size);
        return ret;
    }

    /**
     * @param checksum [IN] The buffer used for storing the checksum.
     * @param size [IN] The buffer size of checksum. It must be not less than 4.
     * @param checksum [OUT] Kernel checksum.
     * @return description
     * @Func: NAPIPaypassGetKernelChecksum
     * @brief: get Paypass Kernel Checksum
     * @retval EMVL2_ERR_NONE
     * @retval EMVL2_ERR_PARAM
     * @retval EMVL2_ERR_FAIL
     */
    @Override
    public int NAPIPaypassGetKernelChecksum(byte[] checksum, int size) {
        int ret = EmvL2Jni.getInstance().jniNAPIPaypassGetKernelChecksum(checksum, size);
        return ret;
    }

    /**
     * @param checksum [IN] The buffer used for storing the checksum.
     * @param size [IN] The buffer size of checksum. It must be not less than 4.
     * @param checksum [OUT] Kernel checksum.
     * @return description
     * @Func: NAPIPaywaveGetKernelChecksum
     * @brief: get Paywave Kernel Checksum
     * @retval EMVL2_ERR_NONE
     * @retval EMVL2_ERR_PARAM
     * @retval EMVL2_ERR_FAIL
     */
    @Override
    public int NAPIPaywaveGetKernelChecksum(byte[] checksum, int size) {
        int ret = EmvL2Jni.getInstance().jniNAPIPaywaveGetKernelChecksum(checksum, size);
        return ret;
    }

    /**
     * @param checksum [IN]	The buffer used for storing the checksum.
     * @param size [IN] The buffer size of checksum. It must be not less than 4.
     * @param checksum [OUT] Kernel checksum.
     * @return description
     * @Func: NAPIExpresspayGetKernelChecksum
     * @brief: get Expresspay Kernel Checksum
     * @retval EMVL2_ERR_NONE
     * @retval EMVL2_ERR_PARAM
     * @retval EMVL2_ERR_FAIL
     */
    @Override
    public int NAPIExpresspayGetKernelChecksum(byte[] checksum, int size) {
        int ret = EmvL2Jni.getInstance().jniNAPIExpresspayGetKernelChecksum(checksum, size);
        return ret;
    }

    /**
     * @param checksum [IN] The buffer used for storing the checksum.
     * @param size [IN] The buffer size of checksum. It must be not less than 4.
     * @param checksum [OUT] Kernel checksum.
     * @return description
     * @Func: NAPIDiscoverpayGetKernelChecksum
     * @brief: get Discoverpay Kernel Checksum
     * @retval EMVL2_ERR_NONE
     * @retval EMVL2_ERR_PARAM
     * @retval EMVL2_ERR_FAIL
     */
    @Override
    public int NAPIDiscoverpayGetKernelChecksum(byte[] checksum, int size) {
        int ret = EmvL2Jni.getInstance().jniNAPIDiscoverpayGetKernelChecksum(checksum, size);
        return ret;
    }

    /**
     * @param checksum [IN] The buffer used for storing the checksum.
     * @param size [IN] The buffer size of checksum. It must be not less than 4.
     * @param checksum [OUT] Kernel checksum.
     * @return description
     * @Func: NAPIInteracGetKernelChecksum
     * @brief: get Interac Kernel Checksum
     * @retval EMVL2_ERR_NONE
     * @retval EMVL2_ERR_PARAM
     * @retval EMVL2_ERR_FAIL
     */
    @Override
    public int NAPIInteracGetKernelChecksum(byte[] checksum, int size) {
        int ret = EmvL2Jni.getInstance().jniNAPIInteracGetKernelChecksum(checksum, size);
        return ret;
    }

    /**
     * @param checksum [IN] The buffer used for storing the checksum.
     * @param size [IN] The buffer size of checksum. It must be not less than 4.
     * @param checksum [OUT] Kernel checksum.
     * @return description
     * @Func: NAPIJCBGetKernelChecksum
     * @brief: get JCB Kernel Checksum
     * @retval EMVL2_ERR_NONE
     * @retval EMVL2_ERR_PARAM
     * @retval EMVL2_ERR_FAIL
     */
    @Override
    public int NAPIJCBGetKernelChecksum(byte[] checksum, int size) {
        int ret = EmvL2Jni.getInstance().jniNAPIJCBGetKernelChecksum(checksum, size);
        return ret;
    }

    /**
     * @param checksum [IN] The buffer used for storing the checksum.
     * @param size [IN] The buffer size of checksum. It must be not less than 4.
     * @param checksum [OUT] Kernel checksum.
     * @return description
     * @Func: NAPIPureGetKernelChecksum
     * @brief: get Pure Kernel Checksum
     * @retval EMVL2_ERR_NONE
     * @retval EMVL2_ERR_PARAM
     * @retval EMVL2_ERR_FAIL
     */
    @Override
    public int NAPIPureGetKernelChecksum(byte[] checksum, int size) {
        int ret = EmvL2Jni.getInstance().jniNAPIPureGetKernelChecksum(checksum, size);
        return ret;
    }

    /**
     * @param checksum [IN] The buffer used for storing the checksum.
     * @param size [IN] The buffer size of checksum. It must be not less than 4.
     * @param checksum [OUT] Kernel checksum.
     * @return description
     * @Func: NAPIRupayGetKernelChecksum
     * @brief: get Rupay Kernel Checksum
     * @retval EMVL2_ERR_NONE
     * @retval EMVL2_ERR_PARAM
     * @retval EMVL2_ERR_FAIL
     */
    @Override
    public int NAPIRupayGetKernelChecksum(byte[] checksum, int size) {
        int ret = EmvL2Jni.getInstance().jniNAPIRupayGetKernelChecksum(checksum, size);
        return ret;
    }

    /**
     * @param checksum [IN] The buffer used for storing the checksum.
     * @param size [IN] The buffer size of checksum. It must be not less than 4.
     * @param checksum [OUT] Kernel checksum.
     * @return description
     * @Func: NAPIQpbocGetKernelChecksum
     * @brief: get Qpboc Kernel Checksum
     * @retval EMVL2_ERR_NONE
     * @retval EMVL2_ERR_PARAM
     * @retval EMVL2_ERR_FAIL
     */
    @Override
    public int NAPIQpbocGetKernelChecksum(byte[] checksum, int size) {
        int ret = EmvL2Jni.getInstance().jniNAPIQpbocGetKernelChecksum(checksum, size);
        return ret;
    }

    /**
     * @param checksum [IN] The buffer used for storing the checksum.
     * @param size [IN] The buffer size of checksum. It must be not less than 4.
     * @param checksum [OUT] Kernel checksum.
     * @return description
     * @Func: NAPIMIRGetKernelChecksum
     * @brief: get MIR Kernel Checksum
     * @retval EMVL2_ERR_NONE
     * @retval EMVL2_ERR_PARAM
     * @retval EMVL2_ERR_FAIL
     */
    @Override
    public int NAPIMIRGetKernelChecksum(byte[] checksum, int size) {
        int ret = EmvL2Jni.getInstance().jniNAPIMIRGetKernelChecksum(checksum, size);
        return ret;
    }

    /**
     * @param checksum [IN] The buffer used for storing the checksum.
     * @param size [IN] The buffer size of checksum. It must be not less than 4.
     * @param checksum [OUT] Kernel checksum.
     * @return description
     * @Func: NAPIMultibancoGetKernelChecksum
     * @brief: get Multibanco Kernel Checksum
     * @retval EMVL2_ERR_NONE
     * @retval EMVL2_ERR_PARAM
     * @retval EMVL2_ERR_FAIL
     */
    @Override
    public int NAPIMultibancoGetKernelChecksum(byte[] checksum, int size) {
        int ret = EmvL2Jni.getInstance().jniNAPIMultibancoGetKernelChecksum(checksum, size);
        return ret;
    }

	/**
     * @param checksum [IN] The buffer used for storing the checksum.
     * @param size [IN] The buffer size of checksum. It must be not less than 4.
     * @param checksum [OUT] Kernel checksum.
     * @return description
     * @Func: NAPICpaceGetKernelChecksum
     * @brief: get Cpace Kernel Checksum
     * @retval EMVL2_ERR_NONE
     * @retval EMVL2_ERR_PARAM
     * @retval EMVL2_ERR_FAIL
     */
    @Override
    public int NAPICpaceGetKernelChecksum(byte[] checksum, int size) {
        int ret = EmvL2Jni.getInstance().jniNAPICpaceGetKernelChecksum(checksum, size);
        return ret;
    }

	/**
     * @param checksum [IN] The buffer used for storing the checksum.
     * @param size [IN] The buffer size of checksum. It must be not less than 4.
     * @param checksum [OUT] Kernel checksum.
     * @return description
     * @Func: NAPIBancomatGetKernelChecksum
     * @brief: get Bancomat Kernel Checksum
     * @retval EMVL2_ERR_NONE
     * @retval EMVL2_ERR_PARAM
     * @retval EMVL2_ERR_FAIL
     */
    @Override
    public int NAPIBancomatGetKernelChecksum(byte[] checksum, int size) {
        int ret = EmvL2Jni.getInstance().jniNAPIBancomatGetKernelChecksum(checksum, size);
        return ret;
    }
	/**
     * @param checksum [IN] The buffer used for storing the checksum.
     * @param size [IN] The buffer size of checksum. It must be not less than 4.
     * @param checksum [OUT] Kernel checksum.
     * @return description
     * @Func: NAPIEftposGetKernelChecksum
     * @brief: get Eftpos Kernel Checksum
     * @retval EMVL2_ERR_NONE
     * @retval EMVL2_ERR_PARAM
     * @retval EMVL2_ERR_FAIL
     */
    @Override
    public int NAPIEftposGetKernelChecksum(byte[] checksum, int size) {
        int ret = EmvL2Jni.getInstance().jniNAPIEftposGetKernelChecksum(checksum, size);
        return ret;
    }

    @Override
    public int NAPICLL2EntryPointInitialize(String filepathname) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2EntryPointInitialize(filepathname);
        return ret;
    }

    @Override
    public void NAPICLL2EntryPointSetTerminalAid(ArrayList<aidlist_clss> termAids) {
        EmvL2Jni.getInstance().jniNAPICLL2EntryPointSetTerminalAid(termAids);
    }

    /**
     * @param config config_clss
     * @return
     * @Func NAPICLL2EntryPointSetEmvConfig
     * @brief set entrypoint config data
     * @li
     */
    @Override
    public void NAPICLL2EntryPointSetEmvConfig(config_clss config) {
        EmvL2Jni.getInstance().jniNAPICLL2EntryPointSetEmvConfig(config);
    }

    /**
     * @param flag 1 or 0
     * @return
     * @Func NAPICLL2EntryPointSetRunToFinalSel
     * @brief Set the flag to run to final select
     * @li SUCC
     */
    @Override
    public void NAPICLL2EntryPointSetRunToFinalSel(int flag) {
        EmvL2Jni.getInstance().jniNAPICLL2EntryPointSetRunToFinalSel(flag);
    }

    /*****************************************************************************
     * @Func NAPIEMVL2GetStatusData
     * @brief This function returns a pointer to 40 bytes of status data.
     * @return A pointer to 40 bytes of status data.
     * @retval value: description
     * @note
     *****************************************************************************/
    @Override
    public byte[] NAPIEMVL2GetStatusData() {
        byte[] ret = EmvL2Jni.getInstance().jniNAPIEMVL2GetStatusData();
        return ret;
    }

    /*****************************************************************************
     * @Func NAPIEMVL2GetErrorCode
     * @brief Get the detailed errorcode after the last function calling.
     * @return errorcode
     * @retval value: description
     * @note
     *****************************************************************************/
    @Override
    public int NAPIEMVL2GetErrorCode() {
        int ret = EmvL2Jni.getInstance().jniNAPIEMVL2GetErrorCode();
        return ret;
    }

    /*****************************************************************************
     *@fn jniNAPIEMVL2PackTLV
     *@brief Pack data by EMV TLV struct conventions.
     *@param tag       [IN] Tag definition.
     *@param tagLen       [IN] The length of tag value.
     *@param tagValue       [IN] Tag value.
     *@param bufLen       [IN] The size of tlvBuf.
     *@param type       [IN] Reserve.
     *@param outBuf       [OUT] The buffer pointer of TLV packing data to store.
     *@param bufLen       [OUT] The real size of the TLV packing data.
     *@return description
     *@retval EMVL2_ERR_NONE: 		Success.
     *@retval EMVL2_ERR_PARAM: 		Any parameter error.
     *@retval EMVL2_ERR_OVERFLOW:		The size of buffer tlvBuf is too small.
     *@note
     *****************************************************************************/
    @Override
    public int NAPIEMVL2PackTLV(byte[] outBuf, int[] bufLen, int tag, int tagLen, byte[] tagValue, int type) {
        int ret = EmvL2Jni.getInstance().jniNAPIEMVL2PackTLV(outBuf, bufLen, tag, tagLen, tagValue, type);
        return ret;
    }

    /*****************************************************************************
     * @Func NAPIEMVL2PackTagDataToTLV
     * @brief Pack tag data to tlv format list. If the tag is not exist, pack none to the tlv buffer and continue the next tag.
     * @param bufLen   [IN] The length of tlvBuf.
     * @param tagList   [IN] The tag list which will be packed to tlvBuf.
     * @param tagNum   [IN] The number of tag list.
     * @param control   [IN] bit 1 = 1: Zero Len Tag Allow.
     * @param outBuf   [OUT] The output packed data.
     * @param bufLen   [OUT] The output length of tlvBuf.
     * @return description
     * @retval EMVL2_ERR_NONE: 		Success.
     * @retval EMVL2_ERR_PARAM: 		Any parameter error.
     * @retval EMVL2_ERR_OVERFLOW:		The size of buffer tlvBuf is too small.
     * @note
     *****************************************************************************/
    @Override
    public int NAPIEMVL2PackTagDataToTLV(byte[] outBuf, int[] bufLen, int[] tagList, int tagNum, int control) {
        int ret = EmvL2Jni.getInstance().jniNAPIEMVL2PackTagDataToTLV(outBuf, bufLen, tagList, tagNum, control);
        return ret;
    }

    /*****************************************************************************
     * @Func NAPIEMVL2GetData
     * @brief Find the tag and load its value with store format.
     * @param tagname   [IN] The tag which want to load.
     * @param outdata   [OUT] The buffer which saved the loaded tag value.
     * @param maxoutlen   [IN] The size of buffer pusData.
     * @return    >= 0: 	The real length of the loaded tag value.
     * @retval EMVL2_ERR_NONE: 		Success.
     * @retval EMVL2_ERR_PARAM: 		Any parameter error.
     * @retval EMVL2_ERR_TAG_ABSENT: 	Tag is absent.
     * @retval EMVL2_ERR_OVERFLOW:		The size of buffer val is too small.
     * @note
     *****************************************************************************/
    @Override
    public int NAPIEMVL2GetData(int tagname, byte[] outdata, int maxoutlen) {
        int ret = EmvL2Jni.getInstance().jniNAPIEMVL2GetData(tagname, outdata, maxoutlen);
        return ret;
    }

    /*****************************************************************************
     * @Func NAPIEMVL2SetData
     * @brief Store the tag data indirectly, not change the format of value.
     * @param tagname   [IN] The tag which want to store.
     * @param datain   [IN] The buffer pointer of the tag value.
     * @param maxlen   [IN] The size of buffer val.
     * @return description
     * @retval EMVL2_ERR_NONE: 		Success.
     * @retval EMVL2_ERR_FAIL: 		Fail.
     * @retval EMVL2_ERR_TAG_REPEAT: 	The tag has existed when store by unique.
     * @note
     *****************************************************************************/
    @Override
    public int NAPIEMVL2SetData(int tagname, byte[] datain, int maxlen) {
        int ret = EmvL2Jni.getInstance().jniNAPIEMVL2SetData(tagname, datain, maxlen);
        return ret;
    }

    @Override
    public int NAPIEMVL2ICCGetData(int emgetdata, byte[] dataout, int[] valuelen) {
        int ret = EmvL2Jni.getInstance().jniNAPIEMVL2ICCGetData(emgetdata, dataout, valuelen);
        return ret;
    }

    /*****************************************************************************
     * @Func NAPIEMVL2GetPBOCLog
     * @brief get EMV Transaction Detail
     * @param nRec [IN]          >0, Number of records to read
     *                          =PBOCLOG_SFI       transaction Detail SFI
     *                          =PBOCLOG_RECNUM    Number of records
     *                          =PBOCLOG_FMT       Transaction Detail Format
     *        out   pusOut   Transaction Detail Data
     *        in    nOutMaxLen  Transaction Detail Data Maxlen
     *
     * @return
     * @li   < 0            FAIL
     *       > 0            Transaction Detail Data Len
     *       = 0            No Detail
     */
    @Override
    public int NAPIEMVL2GetPBOCLog(int nRec, byte[] pusOut, int nOutMaxLen) {
        int ret = EmvL2Jni.getInstance().jniemvGetPBOCLog(nRec, pusOut, nOutMaxLen);
        return ret;
    }

    /*****************************************************************************
     * @Func NAPIEMVL2GetecloadLog
     * @brief get EMV Transaction Detail
     * @param nRec [IN]          >0, Number of records to read
     *                          =PBOCLOG_SFI       transaction Detail SFI
     *                          =PBOCLOG_RECNUM    Number of records
     *                          =PBOCLOG_FMT       Transaction Detail Format
     *        out   pusOut   Transaction Detail Data
     *        in    nOutMaxLen  Transaction Detail Data Maxlen
     *
     * @return
     * @li   < 0            FAIL
     *       > 0            Transaction Detail Data Len
     *       = 0            No Detail
     */
    @Override
    public int NAPIEMVL2GetecloadLog(int nRec, byte[] pusOut, int nOutMaxLen) {
        int ret = EmvL2Jni.getInstance().jniemvGetecloadLog(nRec, pusOut, nOutMaxLen);
        return ret;
    }

    /**
     * @return
     * @Func: NAPICLL2EntryPointGetCLL2Status
     * @brief Get CLL2 Status
     * @retval: Status
     */

    @Override
    public byte[] NAPICLL2EntryPointGetCLL2Status() {
        byte[] ret = EmvL2Jni.getInstance().jniNAPICLL2EntryPointGetCLL2Status();
        return ret;
    }

    /**
     * @param tagname [IN]    ---TAG name, i.e: 0x9F36
     * @param dataout [OUT]      ---out Value
     * @param dataoutlen [OUT]    ---Out length
     * @return
     * @Func: NAPICLL2EntryPointGetDataByTagName
     * @brief Use GET DATA Command to get value of the Tag (Contactless)
     * @li 0 SUCC
     * @li -1 FAIL
     */
    @Override
    public int NAPICLL2EntryPointGetDataByTagName(int tagname, byte[] dataout, int[] dataoutlen) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2EntryPointGetDataByTagName(tagname, dataout, dataoutlen);
        return ret;
    }

    /**
     * @param debugLv LV_CLOSE / LV_DEBUG / LV_ALL
     * @return
     * @Func NAPIEMVL2SetDebugMode
     * @brief Set Kernel Debug Mode
     */
    @Override
    public void NAPIEMVL2SetDebugMode(int debugLv) {
        EmvL2Jni.getInstance().jniNAPIEMVL2SetDebugMode(debugLv);
    }

	@Override
    public void NAPICLL2SetIsNDKEMV(int isNDKEMV) {
        EmvL2Jni.getInstance().jniNAPICLL2SetIsNDKEMV(isNDKEMV);
    }

    @Override
    public int NAPIEMVSetKeytoPIN(int KeyValue) {
        int ret = EmvL2Jni.getInstance().jniemvSetKeytoPIN(KeyValue);
        return ret;
    }

    /**
     * @param tagname [IN]                  ---    TagName
     * @param outdata [OUT]                   ---    Value
     * @param maxoutlen [IN]                 ---    Value Maximum length limit
     * @return
     * @Func: NAPICLL2EntryPointGetData
     * @brief Get the data value of TagName
     * @li 0        tag value does not exist
     * @li >0       The length of the Value
     * @li -1       Data length exceeds length limit
     */
    @Override
    public int NAPICLL2EntryPointGetData(int tagname, byte[] outdata, int maxoutlen) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2EntryPointGetData(tagname, outdata, maxoutlen);
        return ret;
    }


    /**
     * @param tagname [IN]   TagName
     *           in       datain    Value
     *           in       maxlen    Value Length
     * @return
     * @Func: NAPICLL2EntryPointSetData
     * @brief Set the data value of TagName
     * @li 0        SUCC
     * @li <0        FAIL
     * @li -2        No set of permissions for this Tag
     */
    @Override
    public int NAPICLL2EntryPointSetData(int tagname, byte[] datain, int maxlen) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2EntryPointSetData(tagname, datain, maxlen);
        return ret;
    }


    /**
     * @param outBuf  [out]    The output packed data
     * @param bufLen  [in]    The output length of tlvBuf
     * @param tagList [in]     The tag list which will be packed to tlvBuf
     * @param tagNum  [in]     The number of tag list
     * @param control [in]     0: don't pack 0 length tag to tlvBuf. 1: pack 0 length tag as format TL to tlvBuf
     * @return Success:        <br>
     * {@link EmvConst#EMVL2_ERR_NONE}<br>
     * Failure         <br>
     * {@link EmvConst#EMVL2_ERR_PARAM}: tlvBuf or bufLen is NULL pointer<br>
     * {@link EmvConst#EMVL2_ERR_OVERFLOW}: bufLen which indicated the size of tlvBuf is too small to store the packed TLV data
     * @Func: NAPICLL2EntryPointFetchData
     */
    @Override
    public int NAPICLL2EntryPointFetchData(byte[] outBuf, int[] bufLen, int[] tagList, int tagNum, int control) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2EntryPointFetchData(outBuf, bufLen, tagList, tagNum, control);
        return ret;
    }

    /**
     * @return errorcode
     * @Func: NAPICLL2GetErrorCode
     * @brief Get the detailed errorcode after the last function calling.
     * @retval value: description
     * @note
     */
    @Override
    public int NAPICLL2GetErrorCode() {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2GetErrorCode();
        return ret;
    }

    /**
     * Get IC Card Exception status
     *
     * @return <br>0:  Good status
     * <br>1:  IC Card was removed/loose while transaction is performing
     */
    @Override
    public int GetExceptionRemove() {
        int ret = EmvL2Jni.getInstance().jniGetExceptionRemove();
        return ret;
    }

    /**
     * Reset IC Card Exception status
     */
    @Override
    public void ResetExceptionRemove() {
        EmvL2Jni.getInstance().jniResetExceptionRemove();
    }

    @Override
    public int NAPICLL2SetCustomerTagList(int[] tagList, int tagNum) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2SetCustomerTagList(tagList,tagNum);
        return ret;
    }

    @Override
    public int NAPICLL2GetCandidateCustomData(byte[] aid, int aidlen, byte[] customdata) {
        int ret = EmvL2Jni.getInstance().jniNAPICLL2GetCandidateCustomData(aid,aidlen,customdata);
        return ret;
    }

    @Override
    public int NAPICLL2GirocardProcess(ep_opt obj_epopt, rf_transdata obj_rfdata) {
        return EmvL2Jni.getInstance().jniNAPICLL2GirocardProcess(obj_epopt, obj_rfdata);
    }

    @Override
    public int NAPICLL2GirocardSuspend(int nTransFinal) {
        return EmvL2Jni.getInstance().jniNAPICLL2GirocardSuspend(nTransFinal);
    }

    @Override
    public String NAPICLL2GirocardGetVersion() {
        return EmvL2Jni.getInstance().jniNAPICLL2GirocardGetVersion();
    }

    @Override
    public int NAPICLL2GirocardGetKernelChecksum(byte[] checksum, int size) {
        return EmvL2Jni.getInstance().jniNAPIGirocardGetKernelChecksum(checksum, size);
    }

    @Override
    public String getErrorMessage(int emvErrorCode) {
        return EMVErrorMessage.getMessageByKeyId(emvErrorCode);
    }

    @Override
    public void NAPIEMVL2UseExternalReader(boolean isExternalReader) {
        EmvL2Jni.getInstance().jniNAPIEMVL2UseExternalReader(isExternalReader ? 1 : 0);
    }
}
