package com.newland.nsdk.core.api.internal.emvl2.listener;

import com.newland.nsdk.core.api.internal.emvl2.type.aidlist_clss;
import com.newland.nsdk.core.api.internal.emvl2.type.candidate_clss;
import com.newland.nsdk.core.api.internal.emvl2.type.capk;
import com.newland.nsdk.core.api.internal.emvl2.type.crlEntry;

import java.util.ArrayList;


/**
 * A listener used to monitor EMV events.
 */
public interface EmvJNIListener {
    /**
     * Output EMV Debug log.
     *
     * @param log    [IN] EMV debug log.
     * @param logLen [IN] The length of log.
     * @return Result code.
     */
    int emvDebug(byte[] log, int logLen);

    /**
     * Require user to enter password.
     *
     * @return Result code.
     */
    int getOnlinePIN();

    /**
     * Get AID configuration.
     *
     * @param terminalAid [IN] The final select AID.
     * @param aidList     [OUT] The final select AID configuration data.
     * @param aidListLen  [OUT] The final select AID configuration data length.
     * @return Result code.
     */
    int getAidTlvList(aidlist_clss terminalAid, byte[] aidList, int[] aidListLen);

    /**
     * Check the card in the exception list.
     *
     * @param pan   [IN] PAN
     * @param panSn [IN] PAN SN. If SN == 0xFF, it will not check SN.
     * @return <ul>
     * <li>1: Yes</li>
     * <li>0: No</li>
     * </ul>
     */
    int checkExceptionList(byte[] pan, int panSn);

    /**
     * Get CAPK according to RID and CAPK index.
     *
     * @param capk  [OUT] Certification Authority Public Key.
     * @param rid   [IN] Registered Application Provider Identifier, 5 bytes.
     * @param index [IN] Certification Authority Public Key Index.
     * @return <ul>
     * <li>0:  Get CAPK successfully.</li>
     * <li><0: Any error for getting CAPK.</li>
     * </ul>
     */
    int getCapk(capk capk, byte[] rid, int index);

    /**
     * Check if the issuer public certificate in the revocation list.
     *
     * @param crl [IN] Certificate Revocation List, RID + CAPK INDEX + CSN.
     * @return <ul>
     * <li>0: Can't find the certificate in CRL.</li>
     * <li>1: Find the certificate in CRL.</li>
     * </ul>
     */
    int checkCRL(crlEntry crl);

    /**
     * ICC exchange APDU data.
     *
     * @param rAPDU    [OUT] Response data of APDU command including 2 bytes of status word.
     * @param rAPDULen [OUT] The length of received APDU data.
     * @param cAPDU    [IN] Request data of APDU command.
     * @param cAPDULen [IN] The length of request APDU data.
     * @return <ul>
     * <li>0: Perform ICC command successful.</li>
     * <li><0: Any ICC error.</li>
     * </ul>
     */
    int exchangeAPDU(byte[] rAPDU, int[] rAPDULen, byte[] cAPDU, int cAPDULen);

    /**
     * Display AID candidate list, let user choose an AID.
     *
     * @param candidateList AID candidate list.
     * @return Result code.
     */
    int candidate(ArrayList<candidate_clss> candidateList);

    /**
     * Contact card power down.
     *
     * @return Result code.
     */
    int powerDown();

    /**
     * Use in Paypass dek and det.
     *
     * @param messageType [IN] Message type.
     *                    <ul>
     *                    <li>0x01: dek</li>
     *                    <li>0x02: det</li>
     *                    </ul>
     * @param data        [IN/OUT] The dek or det value.
     * @param dataLen     [IN/OUT] The dek or det value length.
     * @return <ul>
     *     <li>0: Success</li>
     *     <li>-1: Failed</li>
     * </ul>
     */
    int dek_det(byte messageType, byte[] data, int[] dataLen);

    /**
     * After final select.
     *
     * @param aid        AID data.
     * @param aidLen     AID data length.
     * @param tlvData    TLV data.
     * @param tlvDataLen TLV data length.
     * @return Result code.
     */
    int after_final_select(byte[] aid, int aidLen, byte[] tlvData, int[] tlvDataLen);

    /**
     * Get APDU data.
     *
     * @param cardInterface Card interface.
     * @param reqAPDU       Request APDU data.
     * @param reqLength     Length of request APDU data.
     * @param rspAPDU       Response APDU data.
     * @param rspLength     Length of response APDU data.
     * @return Result code.
     */
    int get_apdu_data(int cardInterface, byte[] reqAPDU, int reqLength, byte[] rspAPDU, int rspLength);
}
