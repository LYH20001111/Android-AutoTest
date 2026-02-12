package com.newland.nsdk.core.external.command.emv.listener;

import java.util.ArrayList;
/**
 * @author Helen
 * @date 2021/6/29
 */
public interface ExtPerformListener {
    /**
     * Invoked when it needs to select an AID from candidate AID list.
     *
     * <p>Note: EMV kernel will be suspended after this callback is triggered, call {@link ExtEMVL3#responseEvent(int, byte[])} to resume EMV process.</p>
     *
     * @param candidateAIDList Candidate AID list, see {@link CandidateAID}.
     *
     *</p>
     *<font color="#0000FF"><b>After completing this listener,you need to call responseEvent to notify EMVL3 to process.</b></font><br>
     *  <br>
     *{@link ExtEMVL3#responseEvent(int eventResult, byte[] data)}<br>
     * <font><b>eventResult</b></font>:the result of this listener. <br><pre>
     *                 {@link com.newland.nsdk.core.api.external.emvl3.EMVL3ErrorCode#L3_ERR_SUCC}: Success<br>
     *                 {@link com.newland.nsdk.core.api.external.emvl3.EMVL3ErrorCode#L3_ERR_CANCEL}: Cancel<br>
     *                 {@link com.newland.nsdk.core.api.external.emvl3.EMVL3ErrorCode#L3_ERR_TIMEOUT}: Timeout<br>
     *                 {@link com.newland.nsdk.core.api.external.emvl3.EMVL3ErrorCode#L3_ERR_FAIL}: other failure<br><pre>
     * <font><b>data</b></font>: additional data of listener result or other special L2/L3 data you want to exchange<br>
     *
     * <p>The contents and format of the data field are given in the table below.<br>
     *  The non-TLV data items are fixed and must always be present. <br>
     *  The TLV data items that follow may be present in any order and not all TLV data items are mandatory.<br>
     *  See the table for more details.</p>
     * <table border="1">
     * <th>Data Item</th>
     * <th>Length</th>
     * <th>Description</th>
     * <tr>
     * <td>Select index (non-TLV)</td>
     * <td align="center">1</td>
     * <td>the Index of candidate list which you select<br>
     * </td>
     * </tr>
     * <tr>
     * <td>Application Identifier (AID)</td>
     * <td align="center">5-16</td>
     * <td>Identifies the application as described in ISO/IEC 7816-5.<br>
     * Tag: 9F06 Format: b.<br>
     * </td>
     * </tr>
     * <tr>
     * <td>Extern</td>
     * <td align="center">Var</td>
     * <td>Other L2/L3 data element<br>
     * </td>
     * </tr>
     * </table>
     */
    void onCandidateAIDList(ArrayList<ExtCandidateAID> candidateAIDList);

    /**
     * Invoked when it needs to check credentials (optional).
     *
     * <p>Note: </p>
     * <ul>
     *     <li>Only used for Unionpay PBOC.</li>
     *     <li>EMV kernel will be suspended after this callback is triggered, call {@link ExtEMVL3#responseEvent(int, byte[])} to resume EMV process.</li>
     * </ul>
     *
     * @param type   Credential type.
     *               <ul>
     *               <li>0: ID Card</li>
     *               <li>1: Military ID Card</li>
     *               <li>2: Passport</li>
     *               <li>3: Entry Permit</li>
     *               <li>4: Temporary ID Card</li>
     *               <li>5: Other</li>
     *               </ul>
     * @param number Credential number.
     *
     *</p>
     *<font color="#0000FF"><b>After completing this listener,you need to call responseEvent to notify EMVL3 to process.</b></font><br>
     *  <br>
     *{@link ExtEMVL3#responseEvent(int eventResult, byte[] data)}<br>
     * <font><b>eventResult</b></font>:the result of this listener. <br><pre>
     *              {@link com.newland.nsdk.core.api.external.emvl3.EMVL3ErrorCode#L3_ERR_SUCC}: Success<br>
     *              {@link com.newland.nsdk.core.api.external.emvl3.EMVL3ErrorCode#L3_ERR_CANCEL}: Cancel<br>
     *              {@link com.newland.nsdk.core.api.external.emvl3.EMVL3ErrorCode#L3_ERR_TIMEOUT}: Timeout<br>
     *              {@link com.newland.nsdk.core.api.external.emvl3.EMVL3ErrorCode#L3_ERR_FAIL}: other failure<br><pre>
     * <font><b>data</b></font>:  null (default) or exchange L2/L3 data(Reserved).<br>
     */
    void onCredentialsCheck(byte type, byte[] number);

    /**
     * Invoked after Final Selection (optional).
     *
     * <p>Note: </p>
     * <ul>
     *     <li>This is invoked after Final Selection but before GPO. You can update the Terminal/AID Configurations according to the AID.</li>
     *     <li>EMV kernel will be suspended after this callback is triggered, call {@link ExtEMVL3#responseEvent(int, byte[])} to resume EMV process.</li>
     * </ul>
     *
     * @param cardInterface Card interface.
     *                      <ul>
     *                          <li>0x01: Contact</li>
     *                          <li>0x02: Contactless</li>
     *                      </ul>
     * @param aid           AID
     *
     *</p>
     *<font color="#0000FF"><b>After completing this listener,you need to call responseEvent to notify EMVL3 to process.</b></font><br>
     *  <br>
     *{@link ExtEMVL3#responseEvent(int eventResult, byte[] data)}<br>
     * <font><b>eventResult</b></font>:the result of this listener. <br><pre>
     *              {@link com.newland.nsdk.core.api.external.emvl3.EMVL3ErrorCode#L3_ERR_SUCC}: Success<br>
     *              {@link com.newland.nsdk.core.api.external.emvl3.EMVL3ErrorCode#L3_ERR_CANCEL}: Cancel<br>
     *              {@link com.newland.nsdk.core.api.external.emvl3.EMVL3ErrorCode#L3_ERR_TIMEOUT}: Timeout<br>
     *              {@link com.newland.nsdk.core.api.external.emvl3.EMVL3ErrorCode#L3_ERR_FAIL}: other failure<br><pre>
     * <font><b>data</b></font>:  null (default) or exchange L2/L3 data(Reserved).<br>
     */
    void onFinalSelect(byte cardInterface, byte[] aid);

    /**
     * Invoked when it needs PIN entry.
     *
     * <p>Note: EMV kernel will be suspended after this callback is triggered, call {@link ExtEMVL3#responseEvent(int, byte[])} to resume EMV process.</p>
     *
     * @param pinType PIN type.
     *                <ul>
     *                <li>0: PIN_ONLINE</li>
     *                <li>1: PIN_OFFLINE</li>
     *                <li>2: PIN_OFFLINE_ENCIPHERED</li>
     *                </ul>
     * @param tlvData TLV data.
     *
     *</p>
     *<font color="#0000FF"><b>After completing this listener,you need to call responseEvent to notify EMVL3 to process.</b></font><br>
     *  <br>
     *{@link ExtEMVL3#responseEvent(int eventResult, byte[] data)}<br>
     * <font><b>eventResult</b></font>:the result of this listener. <br><pre>
     *                 {@link com.newland.nsdk.core.api.external.emvl3.EMVL3ErrorCode#L3_ERR_SUCC}: Success<br>
     *                 {@link com.newland.nsdk.core.api.external.emvl3.EMVL3ErrorCode#L3_ERR_CANCEL}: Cancel<br>
     *                 {@link com.newland.nsdk.core.api.external.emvl3.EMVL3ErrorCode#L3_ERR_TIMEOUT}: Timeout<br>
     *                 {@link com.newland.nsdk.core.api.external.emvl3.EMVL3ErrorCode#L3_ERR_FAIL}: other failure<br>
     *                 {@link com.newland.nsdk.core.api.external.emvl3.EMVL3ErrorCode#L3_ERR_BYPASS}: Bypass PIN<br><pre>
     * <font><b>data</b></font>:other special L2/L3 data you want to exchange<br>
     *
     * <p>The contents and format of the data field are given in the table below.<br>
     *  The TLV data items that follow may be present in any order and not all TLV data items are mandatory.<br>
     *  See the table for more details.</p>
     * <table border="1">
     * <th>Data Item</th>
     * <th>Length</th>
     * <th>Description</th>
     * <tr>
     * <td>KeyType</td>
     * <td align="center">1</td>
     * <td>This data item means key type for pinpad online pin. if you dont set default Key type: 0x00.<br>
     * Tag: 1F8136 Format: b.<br>
     * </td>
     * </tr>
     * <tr>
     * <td>Keyindex</td>
     * <td align="center">1</td>
     * <td>This data item means key index for pinpad online pin.if you dont set default Key index: 0x01.<br>
     * Tag: 1F8137 Format: b.<br>
     * </td>
     * </tr>
     * <tr>
     * <td>timeout</td>
     * <td align="center">1</td>
     * <td>This data item means timeout for pinpad inputting pin.Default timeout is 50s.<br>
     * Tag: 1F8138 Format:b.<br>
     * </td>
     * </tr>
     * <tr>
     * <td>PIN RANGE</td>
     * <td align="center">1</td>
     * <td>You can set up pin range by yourself. If not,default is (0,4~12). you can set the length limit to enter pin.<br>
     * Tag: 1F8135 Format: b.<br>
     * </td>
     * </tr>
     * </table>
     */
    void onPinEntry(byte pinType, byte[] tlvData);

    /**
     * Invoked when it needs to prompt user for card number conforming.
     *
     * <p>Note: EMV kernel will be suspended after this callback is triggered, call {@link ExtEMVL3#responseEvent(int, byte[])} to resume EMV process.</p>
     * @param maskPAN PAN with mask (First clear PAN ****** Last Clear PAN)
     *
     *</p>
     *<font color="#0000FF"><b>After completing this listener,you need to call responseEvent to notify EMVL3 to process.</b></font><br>
     *  <br>
     *{@link ExtEMVL3#responseEvent(int eventResult, byte[] data)}<br>
     * <font><b>eventResult</b></font>:the result of this listener. <br><pre>
     *              {@link com.newland.nsdk.core.api.external.emvl3.EMVL3ErrorCode#L3_ERR_SUCC}: Success<br>
     *              {@link com.newland.nsdk.core.api.external.emvl3.EMVL3ErrorCode#L3_ERR_CANCEL}: Cancel<br>
     *              {@link com.newland.nsdk.core.api.external.emvl3.EMVL3ErrorCode#L3_ERR_TIMEOUT}: Timeout<br>
     *              {@link com.newland.nsdk.core.api.external.emvl3.EMVL3ErrorCode#L3_ERR_FAIL}: other failure<br><pre>
     * <font><b>data</b></font>:  null (default) or exchange L2/L3 data(Reserved).<br>
     */
    void onCardNumberConfirm(String maskPAN);

    /**
     * Invoked when it needs to display message according to the UI event.
     *
     * @param uiEventID   UI event ID.
     *                    <ul>
     *                        <li>0: UI_PRESENT_CARD</li>
     *                        <li>1: UI_PROCESSING</li>
     *                        <li>2: UI_CAPK_LOAD_FAIL</li>
     *                        <li>3: UI_SEE_PHONE</li>
     *                        <li>6: UI_PIN_STATUS</li>
     *                    </ul>
     * @param uiEventData Event related data.
     *                    <ul>
     *                        <li>For event UI_PRESENT_CARD, one byte:
     *                            <ul>
     *                                <li>0x00: UI_KEYIN, reserved.</li>
     *                                <li>0x01: UI_STRIPE, card reader is ready to read mag card, prompt user for swiping card.</li>
     *                                <li>0x02: UI_INSERT, card reader is ready to read contact card, prompt user for inserting card.</li>
     *                                <li>0x03: UI_TAP, card reader is ready to read contactless card, prompt user for tapping card.</li>
     *                                <li>0x04: UI_INSERTC_TAP, card reader is ready to read contact/contactless card, prompt user for inserting/tapping card.</li>
     *                                <li>0x05: UI_STRIPE_INSERT, card reader is ready to read mag/contact card, prompt user for swiping/inserting card.</li>
     *                                <li>0x06: UI_STRIPE_TAP, card reader is ready to read mag/contactless card, prompt user for swiping/tapping card.</li>
     *                                <li>0x07: UI_STRIPE_INSERT_TAP, card reader is ready to read mag/contact/contactless card, prompt user for swiping/inserting/tapping card.</li>
     *                                <li>0x08: UI_PRESENTCARD_AGAIN, user shall present the card again to continue the transaction.</li>
     *                                <li>0x09: UI_USE_CHIP, the card swiped is a chip card, chip interface shall be used.</li>
     *                                <li>0x0A: UI_FALLBACK_CT, a fallback from Contact (Chip) to MSR occurred. Card reader is ready to read mag card, prompt user for swiping card.</li>
     *                                <li>0x0B:UI_FALLBACK_CLSS, a fallback from Contactless to Contact/MSR occurred. Card reader is ready to read mag/contact card, prompt user for swiping/inserting card.</li>
     *                                <li>0x0C:UI_STRIPE_INSERT_TAP_MANUAL, prompt user for swiping/inserting/tapping card or manually input card number.</li>
     *                                <li>0x0D:UI_STRIPE_INSERT_MANUAL, prompt user for swiping/inserting card or manually input card number.</li>
     *                                <li>0x0E:UI_STRIPE_TAP_MANUAL, prompt user for swiping/tapping card or manually input card number.</li>
     *                                <li>0x0F:UI_INSERT_TAP_MANUAL, prompt user for inserting/tapping card or manually input card number.</li>
     *                                <li>0x10:UI_STRIPE_MANUAL, prompt user for swiping card or manually input card number.</li>
     *                                <li>0x11:UI_INSERT_MANUAL, prompt user for inserting card or manually input card number.</li>
     *                                <li>0x12:UI_TAP_MANUAL, prompt user for tapping card or manually input card number.</li>
     *                            </ul>
     *                        </li>
     *                        <li>For event UI_PIN_STATUS, the event data is consist of:
     *                            <ul>
     *                                <li>PIN_ENTRY_STATUS: 4 bytes
     *                                    <ul>
     *                                        <li>0: ERR_SUCC</li>
     *                                        <li>-501: ERR_FAIL</li>
     *                                        <li>-502: ERR_CANCLE</li>
     *                                        <li>-503: ERR_TIMEOUT</li>
     *                                        <li>-508: ERR_BYPASS</li>
     *                                    </ul>
     *                                </li>
     *                                <li>PINBLOCK:
     *                                    <ul>
     *                                        <li>TAG 1F8155: ANSI X9.8 encrypted PIN block. If not enter pinblock is all 0, TLV Length is 0.</li>
     *                                        <li>TAG 1F8153: KSN for DUKPT key type</li>
     *                                    </ul>
     *                                </li>
     *                            </ul>
     *                        </li>
     *                    </ul>
     *
     *</p>
     *<font color="#0000FF"><b>After completing this listener,you need to call responseEvent to notify EMVL3 to process.</b></font><br>
     *  <br>
     *{@link ExtEMVL3#responseEvent(int eventResult, byte[] data)}<br>
     * <font><b>eventResult</b></font>:the result of this listener. <br><pre>
     *              {@link com.newland.nsdk.core.api.external.emvl3.EMVL3ErrorCode#L3_ERR_SUCC}: Success<br>
     *              {@link com.newland.nsdk.core.api.external.emvl3.EMVL3ErrorCode#L3_ERR_CANCEL}: Cancel<br>
     *              {@link com.newland.nsdk.core.api.external.emvl3.EMVL3ErrorCode#L3_ERR_TIMEOUT}: Timeout<br>
     *              {@link com.newland.nsdk.core.api.external.emvl3.EMVL3ErrorCode#L3_ERR_FAIL}: other failure<br><pre>
     * <font><b>data</b></font>:  null (default) or exchange L2/L3 data(Reserved).<br>
     */
    void onUIEvent(int uiEventID, byte[] uiEventData);

}
