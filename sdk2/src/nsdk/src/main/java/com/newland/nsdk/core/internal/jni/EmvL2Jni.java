package com.newland.nsdk.core.internal.jni;

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

public class EmvL2Jni {

    private static EmvL2Jni emvL2Jni;

    static {
        System.loadLibrary("emvl2jni");
    }

    private EmvL2Jni() {
    }

    public static EmvL2Jni getInstance() {
        if (emvL2Jni == null) {
            synchronized (EmvL2Jni.class) {
                if (emvL2Jni == null) {
                    emvL2Jni = new EmvL2Jni();
                }
            }
        }
        return emvL2Jni;
    }

    public native int jniNAPIEMVL2SetCallbackFunction(EmvJNIListener callbackFunction);

    /*****************************************************************************
     * @fn jniNAPICLL2SetCallbackFunction
     * @brief Callback function setup.
     * @param callbackFunction [IN] The callback function pointer.
     * @return description
     * @retval EMVL2_ERR_NONE: Success.
     * @retval EMVL2_ERR_FAIL: Fail.
     * @note
     *****************************************************************************/
    public native int jniNAPICLL2SetCallbackFunction(EmvJNIListener callbackFunction);

    public native int jniNAPIEMVL2FreeGlobalVariable();

    public native int jniNAPIEMVL2BuildCandidateList(ArrayList<candidate_emv> candidateList,
                                                     ArrayList<aidlist_emv> aidList);

    public native int jniNAPIEMVL2SelectApplication(int selectionID, ArrayList<candidate_emv> candidateList);

    public native int jniNAPIEMVL2InitiateApplication(ArrayList<candidate_emv> candidateList);

    /*****************************************************************************
     * @fn jniNAPIEMVL2ReadApplicationData
     * @brief Read application data according the AFL. The Read Application Data
     *        function is performed immediately following the Initiate Application
     *        Processing function.
     * @return description
     * @retval EMVL2_ERR_NONE: Success.
     * @retval EMVL2_ERR_TERMINATE: AFL format error, or SW1 SW2 not '9000' in
     *         response to the READ RECORD command, or 70 template parase error, or
     *         mandatory data missing, or icc error, the application should
     *         terminate the session.
     * @note
     *****************************************************************************/
    public native int jniNAPIEMVL2ReadApplicationData();

    /*****************************************************************************
     * @Func jniNAPIEMVL2OfflineDataAuthentication
     * @brief The terminal application shall perform offline data authentication in
     *        any order after Read Application Data but before completion of the
     *        terminal action analysis.
     * @param ddol [IN] Default DDOL. If the termianl don't support DDA or default
     *        DDOL, passing NULL is OK.
     * @param ddolLen [IN] Default DDOL length.
     * @return description
     * @retval EMVL2_ERR_NONE: Offline data authentication has been performed
     *         successful.
     * @retval EMVL2_ERR_FAIL: Offline data authentication failed or don't
     *         performed.
     * @retval EMVL2_ERR_CAPK_CHECKSUM:CAPK check failed, if operator action is
     *         needed, the terminal application shall display an error message.
     * @retval EMVL2_ERR_TERMINATE: SW1 SW2 not '9000' in response to the INTERNAL
     *         AUTHENTICATE command, or 77 or 80 template parase error, or icc
     *         error, the application should terminate the session.
     * @note
     *****************************************************************************/
    public native int jniNAPIEMVL2OfflineDataAuthentication(byte[] ddol, int ddolLen);

    /*****************************************************************************
     * @Func jniNAPIEMVL2ProcessingRestrictions
     * @brief Check AVN, AUC, Application Effective/Expiration Dates.
     * @return description
     * @retval EMVL2_ERR_NONE: Processing Restrictions has been performed.
     * @note
     *****************************************************************************/
    public native int jniNAPIEMVL2ProcessingRestrictions();

    public native int jniNAPIEMVL2CardholderVerification(byte[] cvmCode, byte[] cvStatus);

    /*****************************************************************************
     * @Func jniNAPIEMVL2RiskManagement
     * @brief Perform terminal risk management.
     * @param blackCard [IN] 0: the card absent on the blacklist. 1: the card
     *        present on the blacklist.
     * @param forceOnline [IN] If the merchant force the transaction online. 0: No
     *        1: Yes
     * @param logAmount [IN] The transaction amount of the card has completed
     *        before, format n6.
     * @param targetPercentage [IN] Target Percentage to be Used for Random
     *        Selection? (in the range of 0 to 99)
     * @param maxTargetPercentage [IN] Maximum Target Percentage to be Used for
     *        Biased Random Selection? (also in the range of 0 to 99)
     * @param thresholdValue [IN] Threshold Value for Biased Random Selection (which
     *        must be zero or a positive number less than the floor limit), format
     *        n4.
     * @return description
     * @retval EMVL2_ERR_NONE: Risk management has performed.
     * @note
     *****************************************************************************/
    public native int jniNAPIEMVL2RiskManagement(int blackCard, int forceOnline, byte[] logAmount, int targetPercentage,
                                                 int maxTargetPercentage, byte[] thresholdValue);

    /*****************************************************************************
     * @Func jniNAPIEMVL2TerminalActionAnalysis
     * @brief Perform terminal action analysis. If unable online, the terminal shall
     *        set unableOnline = 1 and pass tacDefault to kernel to determine
     *        whether to approve or reject the transaction offline.
     * @param tacDenial [IN] Terminal Action Code - Denial, 5 bytes
     * @param tacOnline [IN] Terminal Action Code - Online, 5 bytes
     * @param tacDefault [IN] Terminal Action Code - Default, 5 bytes
     * @param unableOnline [IN]  0: ignore. 1: the terminal is for any reason unable
     *        to process the transaction online.
     * @param tdol [IN] Default TDOL.
     * @param tdolLen [IN] Default TDOL length.
     * @param acType [OUT] The request AC type by the result of terminal action
     *        analysis.
     * @return description
     * @retval EMVL2_ERR_NONE: Succ, the termianl should check or change acType
     *         before GAC.
     * @retval EMVL2_ERR_PARAM: Any parameter error.
     * @note
     *****************************************************************************/
    public native int jniNAPIEMVL2TerminalActionAnalysis(int[] acType, byte[] tacDenial, byte[] tacOnline,
                                                         byte[] tacDefault, int unableOnline, byte[] tdol, int tdolLen);

    /*****************************************************************************
     * @Func jniNAPIEMVL2GenerateAC1st
     * @brief Send first generate AC command to ICC to perform card action analysis.
     *        CDA signature should be requested if terminal and ICC supported both.
     *        The terminal application check return value to obtain the card
     *        desicion of offline approval, offline declined or online request. If
     *        ICC requested advice, the terminal application should be send a advice
     *        message to the issuer.
     * @param reqACType [IN] The termianl final desicion of AC type.
     * @param advice [OUT] 0: No 1: Yes.
     * @return description
     * @retval EMVL2_ERR_TERMINATE: 77 or 80 template parase error, or CID format
     *         error, or icc error, the application should terminate the session.
     * @retval EMVL2_ERR_GO_ONLINE: The termianl should process transaction online.
     * @retval EMVL2_ERR_DECLINE: The termianl should decline the transaction.
     * @retval EMVL2_ERR_ACCEPT: The termianl should accept the transaction
     * @retval EMVL2_ERR_GAC2_AAC: The termianl should request AAC by calling
     *         NAPI_EMVL2GenerateAC2nd.
     * @note
     *****************************************************************************/
    public native int jniNAPIEMVL2GenerateAC1st(int[] advice, int reqACType);

    /*****************************************************************************
     * @Func jniNAPIEMVL2GenerateAC2nd
     * @brief Send second generate AC command to ICC to perform card action
     *        analysis. CDA signature always be requested if reqACType is TC. If
     *        online processing performed after NAPI_EMVL2GenerateAC1st, the
     *        termianl application analysis the issuer authentication response code
     *        and make the final desicion of reqACType. Authentication response code
     *        (TAG 8A) should be updated by application. If issuer responses Issuer
     *        Authentication Data (TAG 91), the application should also store first
     *        for issuer authentication. If termianl unable online,
     *        NAPI_EMVL2TerminalActionAnalysis should be called first to make the
     *        final desicion before calling this function. After call this function,
     *        the terminal application check the return value to obtain the card
     *        desicion of TC approval or AAC declined. If ICC requested advice, the
     *        terminal application should be send a advice message to the issuer.
     * @param reqACType [IN] Online result or issuer voice result of the issuer.
     * @param script71 [IN] Type 71 issuer script.
     * @param script71Len [IN] The length of script71.
     * @param script72 [IN] Type 72 issuer script.
     * @param script72Len [IN] The length of script72.
     * @param scriptResultLen [IN] The size of buffer scriptResult.
     * @param advice [OUT] 0: No 1: Yes.
     * @param scriptResult [OUT] Return the script result.
     * @param scriptResultLen [OUT] The real length of scriptResult.
     * @return description
     * @retval EMVL2_ERR_PARAM: Any parameter error.
     * @retval EMVL2_ERR_FORMAT: There is not Issuer Script Command(tag 86) in
     *         script.
     * @retval EMVL2_ERR_TERMINATE: 77 or 80 template parase error, or CID format
     *         error, or icc error, the application should terminate the session.
     * @retval EMVL2_ERR_DECLINE: The termianl should decline the transaction.
     * @retval EMVL2_ERR_ACCEPT: The termianl should accept the transaction
     * @note
     *****************************************************************************/
    public native int jniNAPIEMVL2GenerateAC2nd(int[] advice, int reqACType, byte[] script71, int script71Len,
                                                byte[] script72, int script72Len, byte[] scriptResult, int[] scriptResultLen);

    /*****************************************************************************
     * @Func jniNAPIEMVL2InitTagData
     * @brief Clear all the tag data in data store space.
     * @return description
     * @retval EMVL2_ERR_NONE: Success.
     * @retval EMVL2_ERR_FAIL: Fail.
     * @note
     *****************************************************************************/
    public native int jniNAPIEMVL2InitTagData();

    /*****************************************************************************
     * @Func jniNAPIEMVL2ExistTag
     * @brief Check the special tag wheather exist in the data space.
     * @param tag [IN] The tag which want to check.
     * @return description
     * @retval EMVL2_ERR_NONE: present.
     * @retval EMVL2_ERR_TAG_ABSENT: absent
     * @note
     *****************************************************************************/
    public native int jniNAPIEMVL2ExistTag(int tag);

    /*****************************************************************************
     * @Func jniNAPIEMVL2GetPinpk
     * @brief The terminal application get PIN pk for offline encryption PIN
     *        verification.
     * @param pinPK [OUT] PIN pk.
     * @return description
     * @retval EMVL2_ERR_NONE: Succ.
     * @retval EMVL2_ERR_FAIL: Fail.
     * @note
     *****************************************************************************/
    public native int jniNAPIEMVL2GetPinpk(publickey pinPK);

    /*****************************************************************************
     * @Func jniNAPIEMVL2Initialize
     * @brief Initialize kernel status and clear kernel configuration. This API must
     *        be call to clear kernel internal status and all tag data space and
     *        callback function set will be cleared.
     * @return description
     * @retval EMVL2_ERR_NONE: Success.
     * @retval EMVL2_ERR_FAIL: Fail.
     * @note
     *****************************************************************************/
    public native int jniNAPIEMVL2Initialize();

    /*****************************************************************************
     * @Func jniNAPIEMVL2SetConfig
     * @brief Set kernel configuration.
     * @param opt [IN] Configuration bitmap like macro definitions:
     *        EMVL2_SUPPORT_XXX
     * @param val [IN]  0: Unset, 1: Set.
     * @return description
     * @retval EMVL2_ERR_NONE: Success.
     * @note
     *****************************************************************************/
    public native int jniNAPIEMVL2SetConfig(int opt, int val);

    /*****************************************************************************
     * @Func jniNAPIEMVL2GetConfig
     * @brief Get kernel current configuration.
     * @param opt [IN] Configuration bitmap like macro definitions:
     *        EMVL2_SUPPORT_XXX
     * @return description
     * @retval 0: unset.
     * @retval 1: set.
     * @note
     *****************************************************************************/
    public native int jniNAPIEMVL2GetConfig(int opt);

    /*****************************************************************************
     * @Func jniNAPIEMVL2GetVersion
     * @brief Get the EMV level2 version infomation.
     * @return description
     * @retval value: description
     * @note
     *****************************************************************************/
    public native String jniNAPIEMVL2GetVersion();

    /*****************************************************************************
     * @Func jniNAPIEMVL2GetConfigChecksum
     * @brief Get kernel config checksum.
     * @param checksum [IN] The buffer used for storing the checksum.
     * @param size [IN] The buffer size of checksum. It must be not less than 4.
     * @param checksum [OUT] Kernel config checksum.
     * @return description
     * @retval EMVL2_ERR_NONE
     * @retval EMVL2_ERR_PARAM
     * @retval EMVL2_ERR_FAIL
     * @note
     *****************************************************************************/
    public native int jniNAPIEMVL2GetConfigChecksum(byte[] checksum, int size);

    /*****************************************************************************
     * @Func jniNAPIEMVL2GetKernelChecksum
     * @brief Get kernel checksum.
     * @param checksum [IN] The buffer used for storing the checksum.
     * @param size [IN] The buffer size of checksum. It must be not less than 4.
     * @param checksum [OUT] Kernel checksum.
     * @return description
     * @retval EMVL2_ERR_NONE
     * @retval EMVL2_ERR_PARAM
     * @retval EMVL2_ERR_FAIL
     * @note
     *****************************************************************************/
    public native int jniNAPIEMVL2GetKernelChecksum(byte[] checksum, int size);

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
     *@param  ProcessData      [IN] the tlv trans data
     *@param  ProcessDataLen      [IN] tlv Date len
     *@return transaction result
     *@note
     *****************************************************************************/
    public native int jniNAPICLL2PerformTransaction(ep_opt obj_epopt, rf_transdata obj_rfdata, byte[] Ctrl, byte[] ProcessData, int ProcessDataLen);

    /**
     * @param obj_epopt  --- Entry_Point Trading Options
     * @param obj_rfdata --- RF data
     * @return
     * @Func: jniNAPICLL2EntryPointProcess
     * @brief Entry_Point transaction
     * @li <0 FAIL
     */
    public native int jniNAPICLL2EntryPointProcess(ep_opt obj_epopt, rf_transdata obj_rfdata);

    /**
     * @param obj_epopt  --- Entry_Point Trading Options
     * @param obj_rfdata --- RF data
     * @return
     * @Func: jniNAPICLL2PayPassProcess
     * @brief PayPass transaction
     * @li <0 FAIL
     */
    public native int jniNAPICLL2PayPassProcess(ep_opt obj_epopt, rf_transdata obj_rfdata);

    /**
     * @param obj_epopt  --- Entry_Point Trading Options
     * @param obj_rfdata --- RF data
     * @return
     * @Func: jniNAPICLL2PayWaveProcess
     * @brief PayWave transaction
     * @li <0 FAIL
     */
    public native int jniNAPICLL2PayWaveProcess(ep_opt obj_epopt, rf_transdata obj_rfdata);

    /**
     * @param obj_epopt  --- Entry_Point Trading Options
     * @param obj_rfdata --- RF data
     * @return
     * @Func: jniNAPICLL2ExpressPayProcess
     * @li <0 FAIL
     */
    public native int jniNAPICLL2ExpressPayProcess(ep_opt obj_epopt, rf_transdata obj_rfdata);

    /**
     * @param obj_epopt  --- Entry_Point Trading Options
     * @param obj_rfdata --- RF data
     * @return
     * @Func: jniNAPICLL2JCBProcess
     * @brief JCB transaction
     * @li <0 FAIL
     */
    public native int jniNAPICLL2JCBProcess(ep_opt obj_epopt, rf_transdata obj_rfdata);

    /**
     * @param obj_epopt  --- Entry_Point Trading Options
     * @param obj_rfdata --- RF data
     * @return
     * @Func: jniNAPICLL2DiscoverPayProcess
     * @brief DiscoverPay transaction
     * @li <0 FAIL
     */
    public native int jniNAPICLL2DiscoverPayProcess(ep_opt obj_epopt, rf_transdata obj_rfdata);

    /**
     * @param obj_epopt  --- Entry_Point Trading Options
     * @param obj_rfdata --- RF data
     * @return
     * @Func: jniNAPICLL2QpbocProcess
     * @brief Qpboc transaction
     * @li <0 FAIL
     */
    public native int jniNAPICLL2QpbocProcess(ep_opt obj_epopt, rf_transdata obj_rfdata);

    /**
     * @param obj_epopt  --- Entry_Point Trading Options
     * @param obj_rfdata --- RF data
     * @return
     * @Func: jniNAPICLL2PureProcess
     * @brief Pure transaction
     * @li <0 FAIL
     */
    public native int jniNAPICLL2PureProcess(ep_opt obj_epopt, rf_transdata obj_rfdata);

    /**
     * @param obj_epopt  --- Entry_Point Trading Options
     * @param obj_rfdata --- RF data
     * @return
     * @Func: jniNAPICLL2InteracProcess
     * @li <0 FAIL
     */
    public native int jniNAPICLL2InteracProcess(ep_opt obj_epopt, rf_transdata obj_rfdata);

    /**
     * @param obj_epopt  --- Entry_Point Trading Options
     * @param obj_rfdata --- RF data
     * @return
     * @Func: jniNAPICLL2RupayProcess
     * @brief Rupay transaction
     * @li <0 FAIL
     */
    public native int jniNAPICLL2RupayProcess(ep_opt obj_epopt, rf_transdata obj_rfdata);

    /**
     * @param obj_epopt  --- Entry_Point Trading Options
     * @param obj_rfdata --- RF data
     * @return
     * @Func: jniNAPICLL2MIRProcess
     * @brief MIR transaction
     * @li <0 FAIL
     */
    public native int jniNAPICLL2MIRProcess(ep_opt obj_epopt, rf_transdata obj_rfdata);

    /**
     * @param obj_epopt  --- Entry_Point Trading Options
     * @param obj_rfdata --- RF data
     * @return
     * @Func: jniNAPICLL2MultibancoProcess
     * @brief Multibanco transaction
     * @li <0 FAIL
     */
    public native int jniNAPICLL2MultibancoProcess(ep_opt obj_epopt, rf_transdata obj_rfdata);

	/**
     * @param obj_epopt  --- Entry_Point Trading Options
     * @param obj_rfdata --- RF data
     * @return
     * @Func: jniNAPICLL2CpaceProcess
     * @brief Cpace transaction
     * @li <0 FAIL
     */
    public native int jniNAPICLL2CpaceProcess(ep_opt obj_epopt, rf_transdata obj_rfdata);

	/**
     * @param obj_epopt  --- Entry_Point Trading Options
     * @param obj_rfdata --- RF data
     * @return
     * @Func: jniNAPICLL2BancomatProcess
     * @brief Bancomat transaction
     * @li <0 FAIL
     */
    public native int jniNAPICLL2BancomatProcess(ep_opt obj_epopt, rf_transdata obj_rfdata);
	/**
     * @param obj_epopt  --- Entry_Point Trading Options
     * @param obj_rfdata --- RF data
     * @return
     * @Func: jniNAPICLL2EftposProcess
     * @brief Eftpos transaction
     * @li <0 FAIL
     */
    public native int jniNAPICLL2EftposProcess(ep_opt obj_epopt, rf_transdata obj_rfdata);

    /**
     * @param nTransFinal  ---Final transaction result
     * @return
     * @Func: jniNAPICLL2EntryPointSuspend
     * @brief Entry_Point End of transaction execution function
     * @li 0 SUCC
     * @li -1 FAIL
     */
    public native int jniNAPICLL2EntryPointSuspend(int nTransFinal);

    /**
     * @Func: jniNAPICLL2PayPassSuspend
     * @brief Paypass RF card transaction End of transaction execution functions.
     * @Param: nTransFinal the final result of a transaction (transactions accepted,
     * transactions refuse ...)
     * @Return: 0 success -1 Failed
     */
    public native int jniNAPICLL2PayPassSuspend(int nTransFinal);

    /**
     * @Func: jniNAPICLL2PayWaveSuspend
     * @brief PayWave RF card transaction End of transaction execution functions.
     * @Param: nTransFinal the final result of a transaction (transactions accepted,
     * transactions refuse ...)
     * @Return: 0 success -1 Failed
     */
    public native int jniNAPICLL2PayWaveSuspend(int nTransFinal);

    /**
     * @Func: jniNAPICLL2ExpressPaySuspend
     * @brief Expresspay RF card transaction End of transaction execution functions.
     * @Param: nTransFinal the final result of a transaction (transactions accepted,
     * transactions refuse ...)
     * @Return: 0 success -1 Failed
     */
    public native int jniNAPICLL2ExpressPaySuspend(int nTransFinal);

    /**
     * @Func: jniNAPICLL2JCBSuspend
     * @brief JCB RF card transaction End of transaction execution functions.
     * @Param: nTransFinal the final result of a transaction (transactions accepted,
     * transactions refuse ...)
     * @Return: 0 success -1 Failed
     */
    public native int jniNAPICLL2JCBSuspend(int nTransFinal);

    /**
     * @Func: jniNAPICLL2DiscoverPaySuspend
     * @brief Discover RF card transaction End of transaction execution functions.
     * @Param: nTransFinal the final result of a transaction (transactions accepted,
     * transactions refuse ...)
     * @Return: 0 success -1 Failed
     */
    public native int jniNAPICLL2DiscoverPaySuspend(int nTransFinal);

    /**
     * @Func: jniNAPICLL2QpbocSuspend
     * @brief Qpboc RF card transaction End of transaction execution functions.
     * @Param: nTransFinal the final result of a transaction (transactions accepted,
     * transactions refuse ...)
     * @Return: 0 success -1 Failed
     */
    public native int jniNAPICLL2QpbocSuspend(int nTransFinal);

    /**
     * @Func: jniNAPICLL2PureSuspend
     * @brief Pure RF card transaction End of transaction execution functions.
     * @Param: nTransFinal the final result of a transaction (transactions accepted,
     * transactions refuse ...)
     * @Return: 0 success -1 Failed
     */
    public native int jniNAPICLL2PureSuspend(int nTransFinal);

    /**
     * @Func: jniNAPICLL2InteracSuspend
     * @brief Interac RF card transaction End of transaction execution functions.
     * @Param: nTransFinal the final result of a transaction (transactions accepted,
     * transactions refuse ...)
     * @Return: 0 success -1 Failed
     */
    public native int jniNAPICLL2InteracSuspend(int nTransFinal);

    /**
     * @Func: jniNAPICLL2RupaySuspend
     * @brief Rupay RF card transaction End of transaction execution functions.
     * @Param: nTransFinal the final result of a transaction (transactions accepted,
     * transactions refuse ...)
     * @Return: 0 success -1 Failed
     */
    public native int jniNAPICLL2RupaySuspend(int nTransFinal);

    /**
     * @Func: jniNAPICLL2MIRSuspend
     * @brief Rupay RF card transaction End of transaction execution functions.
     * @Param: nTransFinal the final result of a transaction (transactions accepted,
     * transactions refuse ...)
     * @Return: 0 success -1 Failed
     */
    public native int jniNAPICLL2MIRSuspend(int nTransFinal);

    /**
     * @Func: jniNAPICLL2MultibancoSuspend
     * @brief Rupay RF card transaction End of transaction execution functions.
     * @Param: nTransFinal the final result of a transaction (transactions accepted,
     * transactions refuse ...)
     * @Return: 0 success -1 Failed
     */
    public native int jniNAPICLL2MultibancoSuspend(int nTransFinal);

	/**
     * @Func: jniNAPICLL2CpaceSuspend
     * @brief Rupay RF card transaction End of transaction execution functions.
     * @Param: nTransFinal the final result of a transaction (transactions accepted,
     * transactions refuse ...)
     * @Return: 0 success -1 Failed
     */
    public native int jniNAPICLL2CpaceSuspend(int nTransFinal);

	/**
     * @Func: jniNAPICLL2BancomatSuspend
     * @brief Rupay RF card transaction End of transaction execution functions.
     * @Param: nTransFinal the final result of a transaction (transactions accepted,
     * transactions refuse ...)
     * @Return: 0 success -1 Failed
     */
    public native int jniNAPICLL2BancomatSuspend(int nTransFinal);

	/**
     * @Func: jniNAPICLL2EftposSuspend
     * @brief Eftpos RF card transaction End of transaction execution functions.
     * @Param: nTransFinal the final result of a transaction (transactions accepted,
     * transactions refuse ...)
     * @Return: 0 success -1 Failed
     */
    public native int jniNAPICLL2EftposSuspend(int nTransFinal);
	
    /**
     * @Func: jniNAPICLL2EntryPointGetVersion
     * @brief: get Entrypoint Kernel Version
     * @Param:
     * @Return:
     * @retval: Version
     */
    public native String jniNAPICLL2EntryPointGetVersion();

    /**
     * @Func: jniNAPICLL2PaypassGetVersion
     * @brief: get Paypass Kernel Version
     * @Param:
     * @Return:
     * @retval: Version
     */
    public native String jniNAPICLL2PaypassGetVersion();

    /**
     * @Func: jniNAPICLL2PaywaveGetVersion
     * @brief: get Paywave Kernel Version
     * @Param:
     * @Return:
     * @retval: Version
     */
    public native String jniNAPICLL2PaywaveGetVersion();

    /**
     * @Func: jniNAPICLL2ExpresspayGetVersion
     * @brief: get ExpressPay Kernel Version
     * @Param:
     * @Return:
     * @retval: Version
     */
    public native String jniNAPICLL2ExpresspayGetVersion();

    /**
     * @Func: jniNAPICLL2JCBGetVersion
     * @brief: get JCB Kernel Version
     * @Param:
     * @Return:
     * @retval: Version
     */
    public native String jniNAPICLL2JCBGetVersion();

    /**
     * @Func: jniNAPICLL2DiscoverpayGetVersion
     * @brief: get Discoverpay Kernel Version
     * @Param:
     * @Return:
     * @retval: Version
     */
    public native String jniNAPICLL2DiscoverpayGetVersion();

    /**
     * @Func: jniNAPICLL2QpbocGetVersion
     * @brief: get Qpboc Kernel Version
     * @Param:
     * @Return:
     * @retval: Version
     */
    public native String jniNAPICLL2QpbocGetVersion();

    /**
     * @Func: jniNAPICLL2PureGetVersion
     * @brief: get Pure Kernel Version
     * @Param:
     * @Return:
     * @retval: Version
     */
    public native String jniNAPICLL2PureGetVersion();

    /**
     * @Func: jniNAPICLL2InteracGetVersion
     * @brief: get Interac Kernel Version
     * @Param:
     * @Return:
     * @retval: Version
     */
    public native String jniNAPICLL2InteracGetVersion();

    /**
     * @Func: jniNAPICLL2RupayGetVersion
     * @brief: get Rupay Kernel Version
     * @Param:
     * @Return:
     * @retval: Version
     */
    public native String jniNAPICLL2RupayGetVersion();

    /**
     * @Func: jniNAPICLL2MIRGetVersion
     * @brief: get MIR Kernel Version
     * @Param:
     * @Return:
     * @retval: Version
     */
    public native String jniNAPICLL2MIRGetVersion();

    /**
     * @Func: jniNAPICLL2MultibancoGetVersion
     * @brief: get Multibanco Kernel Version
     * @Param:
     * @Return:
     * @retval: Version
     */
    public native String jniNAPICLL2MultibancoGetVersion();

	/**
     * @Func: jniNAPICLL2CpaceGetVersion
     * @brief: get Cpace Kernel Version
     * @Param:
     * @Return:
     * @retval: Version
     */
    public native String jniNAPICLL2CpaceGetVersion();

	/**
     * @Func: jniNAPICLL2BancomatGetVersion
     * @brief: get Bancomat Kernel Version
     * @Param:
     * @Return:
     * @retval: Version
     */
    public native String jniNAPICLL2BancomatGetVersion();

	/**
     * @Func: jniNAPICLL2EftposGetVersion
     * @brief: get Eftpos Kernel Version
     * @Param:
     * @Return:
     * @retval: Version
     */
    public native String jniNAPICLL2EftposGetVersion();
    /**
     * @param checksum [IN] The buffer used for storing the checksum.
     * @param size [IN] The buffer size of checksum. It must be not less than 4.
     * @param checksum [OUT] Kernel checksum.
     * @return description
     * @Func: jniNAPIEntryPointGetKernelChecksum
     * @brief: get EntryPoint Kernel Checksum
     * @retval EMVL2_ERR_NONE
     * @retval EMVL2_ERR_PARAM
     * @retval EMVL2_ERR_FAIL
     */
    public native int jniNAPIEntryPointGetKernelChecksum(byte[] checksum, int size);

    /**
     * @param checksum [IN] The buffer used for storing the checksum.
     * @param size [IN] The buffer size of checksum. It must be not less than 4.
     * @param checksum [OUT] Kernel checksum.
     * @return description
     * @Func: jniNAPIPaypassGetKernelChecksum
     * @brief: get Paypass Kernel Checksum
     * @retval EMVL2_ERR_NONE
     * @retval EMVL2_ERR_PARAM
     * @retval EMVL2_ERR_FAIL
     */
    public native int jniNAPIPaypassGetKernelChecksum(byte[] checksum, int size);

    /**
     * @param checksum [IN] The buffer used for storing the checksum.
     * @param size [IN] The buffer size of checksum. It must be not less than 4.
     * @param checksum [OUT]  Kernel checksum.
     * @return description
     * @Func: jniNAPIPaywaveGetKernelChecksum
     * @brief: get Paywave Kernel Checksum
     * @retval EMVL2_ERR_NONE
     * @retval EMVL2_ERR_PARAM
     * @retval EMVL2_ERR_FAIL
     */
    public native int jniNAPIPaywaveGetKernelChecksum(byte[] checksum, int size);

    /**
     * @param checksum [IN] The buffer used for storing the checksum.
     * @param size [IN] The buffer size of checksum. It must be not less than 4.
     * @param checksum [OUT] Kernel checksum.
     * @return description
     * @Func: jniNAPIExpresspayGetKernelChecksum
     * @brief: get Expresspay Kernel Checksum
     * @retval EMVL2_ERR_NONE
     * @retval EMVL2_ERR_PARAM
     * @retval EMVL2_ERR_FAIL
     */
    public native int jniNAPIExpresspayGetKernelChecksum(byte[] checksum, int size);

    /**
     * @param checksum [IN] The buffer used for storing the checksum.
     * @param size [IN] The buffer size of checksum. It must be not less than 4.
     * @param checksum [OUT] Kernel checksum.
     * @return description
     * @Func: jniNAPIDiscoverpayGetKernelChecksum
     * @brief: get Discoverpay Kernel Checksum
     * @retval EMVL2_ERR_NONE
     * @retval EMVL2_ERR_PARAM
     * @retval EMVL2_ERR_FAIL
     */
    public native int jniNAPIDiscoverpayGetKernelChecksum(byte[] checksum, int size);

    /**
     * @param checksum [IN] The buffer used for storing the checksum.
     * @param size [IN] The buffer size of checksum. It must be not less than 4.
     * @param checksum [OUT] Kernel checksum.
     * @return description
     * @Func: jniNAPIInteracGetKernelChecksum
     * @brief: get Interac Kernel Checksum
     * @retval EMVL2_ERR_NONE
     * @retval EMVL2_ERR_PARAM
     * @retval EMVL2_ERR_FAIL
     */
    public native int jniNAPIInteracGetKernelChecksum(byte[] checksum, int size);

    /**
     * @param checksum [IN] The buffer used for storing the checksum.
     * @param size [IN] The buffer size of checksum. It must be not less than 4.
     * @param checksum [OUT] Kernel checksum.
     * @return description
     * @Func: jniNAPIJCBGetKernelChecksum
     * @brief: get JCB Kernel Checksum
     * @retval EMVL2_ERR_NONE
     * @retval EMVL2_ERR_PARAM
     * @retval EMVL2_ERR_FAIL
     */
    public native int jniNAPIJCBGetKernelChecksum(byte[] checksum, int size);

    /**
     * @param checksum [IN] The buffer used for storing the checksum.
     * @param size [IN] The buffer size of checksum. It must be not less than 4.
     * @param checksum [OUT] Kernel checksum.
     * @return description
     * @Func: jniNAPIPureGetKernelChecksum
     * @brief: get Pure Kernel Checksum
     * @retval EMVL2_ERR_NONE
     * @retval EMVL2_ERR_PARAM
     * @retval EMVL2_ERR_FAIL
     */
    public native int jniNAPIPureGetKernelChecksum(byte[] checksum, int size);

    /**
     * @param checksum [IN] The buffer used for storing the checksum.
     * @param size [IN] The buffer size of checksum. It must be not less than 4.
     * @param checksum [OUT] Kernel checksum.
     * @return description
     * @Func: jniNAPIRupayGetKernelChecksum
     * @brief: get Rupay Kernel Checksum
     * @retval EMVL2_ERR_NONE
     * @retval EMVL2_ERR_PARAM
     * @retval EMVL2_ERR_FAIL
     */
    public native int jniNAPIRupayGetKernelChecksum(byte[] checksum, int size);

    /**
     * @param checksum [IN] The buffer used for storing the checksum.
     * @param size [IN] The buffer size of checksum. It must be not less than 4.
     * @param checksum [OUT] Kernel checksum.
     * @return description
     * @Func: jniNAPIQpbocGetKernelChecksum
     * @brief: get Qpboc Kernel Checksum
     * @retval EMVL2_ERR_NONE
     * @retval EMVL2_ERR_PARAM
     * @retval EMVL2_ERR_FAIL
     */
    public native int jniNAPIQpbocGetKernelChecksum(byte[] checksum, int size);

    /**
     * @param checksum [IN] The buffer used for storing the checksum.
     * @param size [IN] The buffer size of checksum. It must be not less than 4.
     * @param checksum [OUT] Kernel checksum.
     * @return description
     * @Func: jniNAPIMIRGetKernelChecksum
     * @brief: get MIR Kernel Checksum
     * @retval EMVL2_ERR_NONE
     * @retval EMVL2_ERR_PARAM
     * @retval EMVL2_ERR_FAIL
     */
    public native int jniNAPIMIRGetKernelChecksum(byte[] checksum, int size);

    /**
     * @param checksum [IN] The buffer used for storing the checksum.
     * @param size [IN] The buffer size of checksum. It must be not less than 4.
     * @param checksum [OUT] Kernel checksum.
     * @return description
     * @Func: jniNAPIMultibancoGetKernelChecksum
     * @brief: get Multibanco Kernel Checksum
     * @retval EMVL2_ERR_NONE
     * @retval EMVL2_ERR_PARAM
     * @retval EMVL2_ERR_FAIL
     */
    public native int jniNAPIMultibancoGetKernelChecksum(byte[] checksum, int size);

	/**
     * @param checksum [IN] The buffer used for storing the checksum.
     * @param size [IN] The buffer size of checksum. It must be not less than 4.
     * @param checksum [OUT] Kernel checksum.
     * @return description
     * @Func: jniNAPICpaceGetKernelChecksum
     * @brief: get Cpace Kernel Checksum
     * @retval EMVL2_ERR_NONE
     * @retval EMVL2_ERR_PARAM
     * @retval EMVL2_ERR_FAIL
     */
    public native int jniNAPICpaceGetKernelChecksum(byte[] checksum, int size);

	/**
     * @param checksum [IN] The buffer used for storing the checksum.
     * @param size [IN] The buffer size of checksum. It must be not less than 4.
     * @param checksum [OUT] Kernel checksum.
     * @return description
     * @Func: jniNAPIBancomatGetKernelChecksum
     * @brief: get Cpace Kernel Checksum
     * @retval EMVL2_ERR_NONE
     * @retval EMVL2_ERR_PARAM
     * @retval EMVL2_ERR_FAIL
     */
    public native int jniNAPIBancomatGetKernelChecksum(byte[] checksum, int size);
	/**
     * @param checksum [IN] The buffer used for storing the checksum.
     * @param size [IN] The buffer size of checksum. It must be not less than 4.
     * @param checksum [OUT] Kernel checksum.
     * @return description
     * @Func: jniNAPIEftposGetKernelChecksum
     * @brief: get Eftpos Kernel Checksum
     * @retval EMVL2_ERR_NONE
     * @retval EMVL2_ERR_PARAM
     * @retval EMVL2_ERR_FAIL
     */
    public native int jniNAPIEftposGetKernelChecksum(byte[] checksum, int size);

    public native int jniNAPICLL2EntryPointInitialize(String filepathname);

    public native void jniNAPICLL2EntryPointSetTerminalAid(ArrayList<aidlist_clss> termAids);

    /**
     * @param config config_clss
     * @return
     * @Func jniNAPICLL2EntryPointSetEmvConfig
     * @brief set entrypoint config data
     * @li
     */
    public native void jniNAPICLL2EntryPointSetEmvConfig(config_clss config);

    /**
     * @param flag 1 or 0
     * @return
     * @Func jniNAPICLL2EntryPointSetRunToFinalSel
     * @brief Set the flag to run to final select
     * @li SUCC
     */
    public native void jniNAPICLL2EntryPointSetRunToFinalSel(int flag);

    /*****************************************************************************
     * @Func jniNAPIEMVL2GetStatusData
     * @brief This function returns a pointer to 40 bytes of status data.
     * @return A pointer to 40 bytes of status data.
     * @retval value: description
     * @note
     *****************************************************************************/
    public native byte[] jniNAPIEMVL2GetStatusData();

    /*****************************************************************************
     * @Func jniNAPIEMVL2GetErrorCode
     * @brief Get the detailed errorcode after the last function calling.
     * @return errorcode
     * @retval value: description
     * @note
     *****************************************************************************/
    public native int jniNAPIEMVL2GetErrorCode();

    /*****************************************************************************
     *@fn jniNAPIEMVL2PackTLV
     *@brief Pack data by EMV TLV struct conventions.
     *@param tag       [IN] Tag definition.
     *@param tagLen       [IN] The length of tag value.
     *@param tagValue       [IN] Tag value.
     *@param bufLen       [IN] The size of tlvBuf.
     *@param type       [IN] Reserve.
     *@param outBuf       [OUT]  The buffer pointer of TLV packing data to store.
     *@param bufLen       [OUT] The real size of the TLV packing data.
     *@return description
     *@retval EMVL2_ERR_NONE: 		Success.
     *@retval EMVL2_ERR_PARAM: 		Any parameter error.
     *@retval EMVL2_ERR_OVERFLOW:		The size of buffer tlvBuf is too small.
     *@note
     *****************************************************************************/
    public native int jniNAPIEMVL2PackTLV(byte[] outBuf, int[] bufLen, int tag, int tagLen, byte[] tagValue, int type);

    /*****************************************************************************
     * @Func jniNAPIEMVL2PackTagDataToTLV
     * @brief Pack tag data to tlv format list. If the tag is not exist, pack none
     *        to the tlv buffer and continue the next tag.
     * @param bufLen [IN] The length of tlvBuf.
     * @param tagList [IN] The tag list which will be packed to tlvBuf.
     * @param tagNum [IN] The number of tag list.
     * @param control [IN] bit 1 = 1: Zero Len Tag Allow.
     * @param outBuf [OUT] The output packed data.
     * @param bufLen [OUT] The output length of tlvBuf.
     * @return description
     * @retval EMVL2_ERR_NONE: Success.
     * @retval EMVL2_ERR_PARAM: Any parameter error.
     * @retval EMVL2_ERR_OVERFLOW: The size of buffer tlvBuf is too small.
     * @note
     *****************************************************************************/
    public native int jniNAPIEMVL2PackTagDataToTLV(byte[] outBuf, int[] bufLen, int[] tagList, int tagNum, int control);

    /*****************************************************************************
     * @Func jniNAPIEMVL2GetData
     * @brief Find the tag and load its value with store format.
     * @param tagname [IN] The tag which want to load.
     * @param outdata [OUT] The buffer which saved the loaded tag value.
     * @param maxoutlen [IN]  The size of buffer pusData.
     * @return >= 0: The real length of the loaded tag value.
     * @retval EMVL2_ERR_NONE: Success.
     * @retval EMVL2_ERR_PARAM: Any parameter error.
     * @retval EMVL2_ERR_TAG_ABSENT: Tag is absent.
     * @retval EMVL2_ERR_OVERFLOW: The size of buffer val is too small.
     * @note
     *****************************************************************************/
    public native int jniNAPIEMVL2GetData(int tagname, byte[] outdata, int maxoutlen);

    /*****************************************************************************
     * @Func jniNAPIEMVL2SetData
     * @brief Store the tag data indirectly, not change the format of value.
     * @param tagname [IN] The tag which want to store.
     * @param datain [IN] The buffer pointer of the tag value.
     * @param maxlen [IN] The size of buffer val.
     * @return description
     * @retval EMVL2_ERR_NONE: Success.
     * @retval EMVL2_ERR_FAIL: Fail.
     * @retval EMVL2_ERR_TAG_REPEAT: The tag has existed when store by unique.
     * @note
     *****************************************************************************/
    public native int jniNAPIEMVL2SetData(int tagname, byte[] datain, int maxlen);

    public native int jniNAPIEMVL2ICCGetData(int emgetdata, byte[] dataout, int[] valuelen);

    /*****************************************************************************
     * @Func jniemvGetPBOCLog
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
    public native int jniemvGetPBOCLog(int nRec, byte[] pusOut, int nOutMaxLen);

    /*****************************************************************************
     * @Func jniemvGetecloadLog
     * @brief get EMV ECLoad Log
     * @param nRec [IN]          >0, Number of records to read
     *                          =PBOCLOG_SFI       ECLoad Log SFI
     *                          =PBOCLOG_RECNUM    Number of records
     *                          =PBOCLOG_FMT       ECLoad Log Format
     *        out   pusOut   ECLoad Log Data
     *        in    nOutMaxLen  ECLoad Log Data Maxlen
     *
     * @return
     * @li   < 0            FAIL
     *       > 0            CLoad Log Data len
     *       = 0            No Log
     */
    public native int jniemvGetecloadLog(int nRec, byte[] pusOut, int nOutMaxLen);

    /**
     * @return
     * @Func: jniNAPICLL2EntryPointGetCLL2Status
     * @brief Get CLL2 Status
     * @retval: Status
     */
    public native byte[] jniNAPICLL2EntryPointGetCLL2Status();

    /**
     * @param tagname [IN]   ---TAG name, i.e: 0x9F36
     * @param dataout [OUT]  ---out Value
     * @param dataoutlen [OUT]  ---Out length
     * @return
     * @Func: jniNAPICLL2EntryPointGetDataByTagName
     * @brief Use GET DATA Command to get value of the Tag (Contactless)
     * @li 0 SUCC
     * @li -1 FAIL
     */
    public native int jniNAPICLL2EntryPointGetDataByTagName(int tagname, byte[] dataout, int[] dataoutlen);

    /**
     * @param debugLv LV_CLOSE / LV_DEBUG / LV_ALL
     * @return
     * @Func jniNAPIEMVL2SetDebugMode
     * @brief Set Kernel Debug Mode
     */
    public native void jniNAPIEMVL2SetDebugMode(int debugLv);

	public native void jniNAPICLL2SetIsNDKEMV(int isNDKEMV);

    public native int jniemvSetKeytoPIN(int KeyValue);

    /**
     * @param tagname [IN]   --- TagName
     * @param outdata [OUT]  --- Value
     * @param maxoutlen [IN]   --- Value Maximum length limit
     * @return
     * @Func: jniNAPICLL2EntryPointGetData
     * @brief Get the data value of TagName
     * @li 0 tag value does not exist
     * @li >0 The length of the Value
     * @li -1 Data length exceeds length limit
     */
    public native int jniNAPICLL2EntryPointGetData(int tagname, byte[] outdata, int maxoutlen);

    /**
     * @param tagname [IN]  TagName in datain Value in maxlen Value Length
     * @return
     * @Func: jniNAPICLL2EntryPointSetData
     * @brief Set the data value of TagName
     * @li 0 SUCC
     * @li <0 FAIL
     * @li -2 No set of permissions for this Tag
     */
    public native int jniNAPICLL2EntryPointSetData(int tagname, byte[] datain, int maxlen);

    /**
     * @param outBuf  [out] The output packed data
     * @param bufLen  [in] The output length of tlvBuf
     * @param tagList [in] The tag list which will be packed to tlvBuf
     * @param tagNum  [in] The number of tag list
     * @param control [in] 0: donâ€™t pack 0 length tag to tlvBuf. 1: pack 0 length
     *                tag as format TL to tlvBuf
     * @return Success: <br>
     * {@link EmvConst#EMVL2_ERR_NONE}<br>
     * Failure <br>
     * {@link EmvConst#EMVL2_ERR_PARAM}: tlvBuf
     * or bufLen is NULL pointer<br>
     * {@link EmvConst#EMVL2_ERR_OVERFLOW}:
     * bufLen which indicated the size of tlvBuf is too small to store the
     * packed TLV data
     * @Func: jniNAPICLL2EntryPointFetchData
     */
    public native int jniNAPICLL2EntryPointFetchData(byte[] outBuf, int[] bufLen, int[] tagList, int tagNum,
                                                     int control);

    /**
     * @return errorcode
     * @Func: jniNAPICLL2GetErrorCode
     * @brief Get the detailed errorcode after the last function calling.
     * @retval value: description
     * @note
     */
    public native int jniNAPICLL2GetErrorCode();

    /**
     * Get IC Card Exception status
     *
     * @return <br>0:  Good status
     * <br>1:  IC Card was removed/loose while transaction is performing
     */
    public native int jniGetExceptionRemove();

    /**
     * Reset IC Card Exception status
     */
    public native void jniResetExceptionRemove();
    /**
     * Set the custom tag list
     * @param tagList
     * @param tagNum
     * @return 0-SUCC, <0-FAIL
     */
    public native int jniNAPICLL2SetCustomerTagList(int[] tagList, int tagNum);
    /**
     * Get the Candidate list custom tag value
     * @param aid
     * @param aidlen
     * @param customdata
     * @return 0-SUCC, <0-FAIL
     */
    public native int jniNAPICLL2GetCandidateCustomData(byte[] aid, int aidlen, byte[] customdata);

    public native int jniNAPICLL2GirocardProcess(ep_opt var1, rf_transdata var2);

    public native int jniNAPICLL2GirocardSuspend(int var1);

    public native String jniNAPICLL2GirocardGetVersion();

    public native int jniNAPIGirocardGetKernelChecksum(byte[] checkSum, int size);

    public native int jniNAPIEMVL2UseExternalReader(int externalReader);

}
