package com.newland.nsdk.core.api.internal.pinentry;

import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.keymanager.Key;

import java.util.Map;

/**
 * This module is usually used for the accessibility PIN Entry mode. If you want to implement the normal PIN Entry mode, please use {@link PINEntry} module.
 * <p>Note: {@link PINEntry} only supports "RNIB" accessibility PIN Entry mode, if you want to use another accessibility PIN Entry mode, please refer to this module.</p>
 */
public interface PINEntry2 extends Module {

    /**
     * Initializes the layout of the pinpad to be shown.
     * <p>Note: This is the essential called interface at first.</p>
     * @param pinPadButtonMap     <b>[Required]</b>
     * @param screenArea          <b>[Required]</b>
     * @param pinpadArea          <b>[Required]</b>
     * @param keyboardParameters  <b>[Required]</b>
     * @return
     * @throws NSDKException
     */
    byte[] initKeyLayout(Map<PINPadButton, int[]> pinPadButtonMap, int[] screenArea, int[] pinpadArea, KeyboardParameters keyboardParameters) throws NSDKException;

    /**
     * Sets the customized actions according to the extended event input.
     * <p>Note: This interface requires the "eventActionsMap" includes all the mapping relationship of the expected pinpad.</p>
     * @param eventActionsMap  <b>[Required]</b> The mapping relationship of the expected pinpad.
     * @throws NSDKException
     */
    void setCustomizedActions(Map<ExtendedEvent, PINCustomizedAction> eventActionsMap) throws NSDKException;

    /**
     * Starts online PIN input.
     *
     * <p>Note: Key layout shall be install before starting PIN input, if it needs to additional customized actions, shall call {@link PINEntry2#setCustomizedActions(Map)}.</p>
     *
     * <p>Example:</p>
     * <pre>
     *     SymmetricKey desKey = new SymmetricKey();
     *     desKey.setKeyID((byte)2);
     *     desKey.setKeyType(KeyType.DES);
     *     desKey.setKeyUsage(KeyUsage.PIN);
     *
     *     // If using derived DUKPT key
     *     // DUKPTDerivateKey desKey = new DUKPTDerivateKey();
     *     // desKey.setKeyID((byte)2);
     *     // desKey.setKeyType(KeyType.AES);
     *     // desKey.setKeyUsage(KeyUsage.DUKPT);
     *     // desKey.setDerivateKeyLen(16);
     *     // desKey.setDerivateKeyType(KeyType.AES);
     *     // desKey.setDerivateUsage(DUKPTDerivateUsage.PIN);
     *
     *     int timeout = 60;
     *     String pan = "6212261402009762466";
     *
     *     PINEntry2Parameters params = new PINEntry2Parameters();
     *     params.setPINBlockMode(PINBlockMode.ISO9564_0);
     *     params.setMinPINLen(6);
     *     params.setMaxPINLen(10);
     *     params.setPINLengthRange(new byte[]{0x06,0x08,0x09});
     *     PINEntry2Listener pinEntry2Listener = new PINEntry2Listener() {
     *        {@code @Override}
     *         public void onFinish(int pinLen, byte[] pinBlock, byte[] ksn) {
     *             // PIN input completed successfully.
     *         }
     *
     *        {@code @Override}
     *         public void onTimeout() {
     *             // Handle timeout.
     *         }
     *
     *        {@code @Override}
     *         public void onKeyPress() {
     *             // Handle key press.
     *         }
     *
     *        {@code @Override}
     *         public void onCancel() {
     *             // PIN input cancelled.
     *         }
     *
     *        {@code @Override}
     *         public void onClear() {
     *             // Handle clear event.
     *         }
     *
     *        {@code @Override}
     *         public void onBackspace() {
     *             // Handle backspace event.
     *         }
     *
     *         {@code @Override}
     *         public void onError(int code, String message) {
     *             // Handle PIN input error.
     *         }
     *
     *         {@code @Override}
     *         public void onExtendedEvent(ExtendedEventInfo extendedEventInfo) {
     *             //Handle the extended events like {@link ExtendedEvent#TOO_LONG} and {@link ExtendedEvent#TOO_SHORT}.
     *             if (extendedEventInfo != null) {
     *                 ExtendedEvent event = extendedEventInfo.getExtendedEvent();
     *                 TouchState state = extendedEventInfo.getTouchState();
     *                 switch (event) {
     *                     case TOO_LONG:
     *                         EventBus.getDefault().post(new MessageEvent("Too long", MessageTag.NORMAL));
     *                         break;
     *                     case TOO_SHORT:
     *                         EventBus.getDefault().post(new MessageEvent("Too short", MessageTag.NORMAL));
     *                         break;
     *                     default:
     *                         EventBus.getDefault().post(new MessageEvent("ExtendedEvent", MessageTag.NORMAL));
     *                 }
     *
     *             }
     *         }
     *     };
     *
     *     try {
     *         pinEntry2.startOnlinePINEntry(desKey, pan, timeout, params, pinEntry2Listener);
     *     } catch (NSDKException e) {
     *          //Handle the exception
     *     }
     *
     * </pre>
     *
     *
     * @param key        <b>[Required]</b> Key used to encrypt PIN.
     *                   <ul>
     *                   <li>Key ID</li>
     *                   <li>Key type</li>
     *                   <li>Key usage: If using DUKPT to encrypt PIN, set key usage to {@link KeyUsage#DUKPT}</li>
     *                   <li>If using derived DUKPT key, the following required:
     *                   <ul>
     *                   <li>Derivate key type: see {@link KeyType}</li>
     *                   <li>Derivate key usage: {@link DUKPTDerivateUsage#PIN}</li>
     *                   <li>Derivate key len: 16/24 for derived DES key, 16/24/32 for derived AES key. It shall be less than the len of AES DUKPT key.</li>
     *                   </ul>
     *                   </li>
     *                   </ul>
     * @param pan        <b>[Required]</b> PAN. When {@link PINEntryParameters#pinBlockMode} is {@link com.newland.nsdk.core.api.common.pinentry.PINBlockMode#ISO9564_1} and {@link com.newland.nsdk.core.api.common.pinentry.PINBlockMode#ISO9564_2}, pan info is not required.
     * @param timeout    <b>[Required]</b> Timeout for PIN entry. Unit: second. Value range: [5-200].
     * @param parameters <b>[Required]</b> PIN entry parameters. See {@link PINEntry2Parameters}
     *                   <ul>
     *                   <li><b>[Required]</b> PIN block mode</li>
     *                   </ul>
     * @param listener   <b>[Required]</b> Listens to PIN events. See {@link PINEntry2Listener}
     * @throws NSDKException
     */
    void startOnlinePINEntry(Key key, String pan, int timeout, PINEntry2Parameters parameters, PINEntry2Listener listener) throws NSDKException;

    /**
     * Starts offline PIN entry.
     *
     * <ul>Note:
     * <li>Usually, offline PIN is used during EMV process.</li>
     * <li>Offline PIN needs IC card to verify. So the card shall be powered up before starting offline PIN entry.</li>
     * <li>Check PIN block returned by {@link PINEntry2Listener#onFinish(int, byte[], byte[])} to see if the offline PIN is correct.</li>
     * </ul>
     *
     * <p>Example:</p>
     * <pre>
     *     byte[] modulus = ISOUtils.hex2byte("C2E9A3EAA63CEC9D7945623523D066DD212EDEAF100A99F1C722AB102E20243231F69ED105F22999367788DD1BF1503BD9180FA168F33D9AE43932E751D90171D407FCC7EF799BA9BAF963BF5A726489C39A1BD0B2D76B77883EB38A6E9BF425046B81509022D1AF13B3E1DEEC7ACECB1F77498431A11300D21BC413BE0C98FB");
     *     byte[] exponent = ISOUtils.hex2byte("010001");
     *
     *     RSAKey rsaKey = new RSAKey();
     *     rsaKey.setModulus(modulus);
     *     rsaKey.setExponent(exponent);
     *
     *     int timeout = 60;
     *
     *     PINEntry2Listener pinEntry2Listener = new PINEntry2Listener() {
     *        {@code @Override}
     *         public void onFinish(int pinLen, byte[] pinBlock, byte[] ksn) {
     *             // PIN input completed successfully.
     *             if (pinBlock[0] == 0x90 && pinBlock[1] == 0x00) {
     *                 // Offline PIN is correct
     *             } else {
     *                 // Offline PIN is not correct
     *             }
     *         }
     *
     *        {@code @Override}
     *         public void onTimeout() {
     *             // Handle timeout.
     *         }
     *
     *        {@code @Override}
     *         public void onKeyPress() {
     *             // Handle key press.
     *         }
     *
     *        {@code @Override}
     *         public void onCancel() {
     *             // PIN input cancelled.
     *         }
     *
     *        {@code @Override}
     *         public void onClear() {
     *             // Handle clear event.
     *         }
     *
     *        {@code @Override}
     *         public void onBackspace() {
     *             // Handle backspace event.
     *         }
     *
     *         {@code @Override}
     *         public void onError(int code, String message) {
     *             // Handle PIN input error.
     *         }
     *
     *         {@code @Override}
     *         public void onExtendedEvent(ExtendedEventInfo extendedEventInfo) {
     *             //Handle the extended events like {@link ExtendedEvent#TOO_LONG} and {@link ExtendedEvent#TOO_SHORT}.
     *             if (extendedEventInfo != null) {
     *                 ExtendedEvent event = extendedEventInfo.getExtendedEvent();
     *                 TouchState state = extendedEventInfo.getTouchState();
     *                 switch (event) {
     *                     case TOO_LONG:
     *                         EventBus.getDefault().post(new MessageEvent("Too long", MessageTag.NORMAL));
     *                         break;
     *                     case TOO_SHORT:
     *                         EventBus.getDefault().post(new MessageEvent("Too short", MessageTag.NORMAL));
     *                         break;
     *                     default:
     *                         EventBus.getDefault().post(new MessageEvent("ExtendedEvent", MessageTag.NORMAL));
     *                 }
     *
     *             }
     *         }
     *     };
     *
     *     try {
     *         pinEntry2.startOfflinePINEntry(rsaKey, timeout, null, pinEntry2Listener);
     *     } catch (NSDKException e) {
     *          //Handle the exception
     *     }
     *
     * </pre>
     *
     * @param rsaKey     <b>[Optional]</b> RSA key. This is required for cipher PIN, otherwise it can be set to null.
     * @param timeout    <b>[Required]</b> Timeout. Unit: second. Value range: [5-200].
     * @param parameters <b>[Optional]</b> PIN entry parameters. See {@link PINEntry2Parameters}
     * @param listener   <b>[Required]</b> Listens to PIN events. See {@link PINEntry2Listener}
     * @throws NSDKException
     */
    void startOfflinePINEntry(RSAKey rsaKey, int timeout, PINEntry2Parameters parameters, PINEntry2Listener listener) throws NSDKException;

    /**
     * Cancels PIN entry.
     */
    void cancelPINEntry() throws NSDKException;
}
