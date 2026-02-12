package com.newland.nsdk.core.external.command.emv.listener;

/**
 * @author Helen
 * @date 2021/6/29
 */
public interface ExtCompleteListener {
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
