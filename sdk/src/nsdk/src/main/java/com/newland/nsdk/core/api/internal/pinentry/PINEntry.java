package com.newland.nsdk.core.api.internal.pinentry;

import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.keymanager.DUKPTDerivateUsage;
import com.newland.nsdk.core.api.common.keymanager.Key;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.api.common.keymanager.KeyUsage;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;

import java.util.Map;

/**
 * Provides the ability for online/offline PIN entry.
 * If there is no Accessibility requirement, it is recommended to use this module. If there is Accessibility requirement, it is recommended to use the {@link PINEntry2} module.
 * <p>How to get this module:</p>
 * <pre>
 *     PINEntry pinEntry = (PINEntry)NSDKModuleManagerImpl.getInstance().getModule(ModuleType.PIN_ENTRY);
 * </pre>
 */
public interface PINEntry extends Module {

    /**
     * Initializes keyboard layout.
     *
     * <ul>Note:
     * <li>This shall be called before starting PIN entry every time.</li>
     * <li>Once PIN entry is started, touch screen will be controlled by security module, applications can not detect where user touched.</li>
     * </ul>
     *
     * <p>Example: The number button layout is as below</p>
     * Number button 1, Number button 2, Number button 3,<br>
     * Number button 4, Number button 5, Number button 6,<br>
     * Number button 7, Number button 8, Number button 9,<br>
     * Number button 0<br>
     * <p></p>
     * <p>Case 1:</p>
     * <ul>
     *     <li>"numBtn" is specified in the order of Number Button 1, Number Button 2, Number Button 3, Number Button 4, Number Button 5, Number Button 6, Number Button 7, Number Button 8, Number Button 9, Number Button 0
     *     <li>"isRandomKeyboard" is set to "true"</li>
     *     <li>Random buffer {0x31, 0x35, 0x32, 0x30, 0x36, 0x38, 0x34, 0x33, 0x37, 0x39} is returned</li>
     * </ul>
     * Then according to the order of parameter "numBtn":
     * <ul>
     *     <li>0x31 is applied to Number Button 1</li>
     *     <li>0x35 is applied to Number Button 2</li>
     *     <li>0x32 is applied to Number Button 3</li>
     *     <li>0x30 is applied to Number Button 4</li>
     *     <li>0x36 is applied to Number Button 5</li>
     *     <li>0x38 is applied to Number Button 6</li>
     *     <li>0x34 is applied to Number Button 7</li>
     *     <li>0x33 is applied to Number Button 8</li>
     *     <li>0x37 is applied to Number Button 9</li>
     *     <li>0x39 is applied to Number Button 0</li>
     * </ul>
     * Finally, keyboard is showed as below:
     * <p>1  5  2</p>
     * <p>0  6  8</p>
     * <p>4  3  7</p>
     * <p>9</p>
     * <p></p>
     * <p>Case 2:</p>
     * <ul>
     *     <li>"numBtn" is specified in the order of Number Button 0, Number Button 1, Number Button 2, Number Button 3, Number Button 4, Number Button 5, Number Button 6, Number Button 7, Number Button 8, Number Button 9
     *     <li>"isRandomKeyboard" is set to "true"</li>
     *     <li>Random buffer {0x31, 0x35, 0x32, 0x30, 0x36, 0x38, 0x34, 0x33, 0x37, 0x39} is returned</li>
     * </ul>
     * Then according to the order of parameter "numBtn":
     * <ul>
     *     <li>0x31 is applied to Number Button 0</li>
     *     <li>0x35 is applied to Number Button 1</li>
     *     <li>0x32 is applied to Number Button 2</li>
     *     <li>0x30 is applied to Number Button 3</li>
     *     <li>0x36 is applied to Number Button 4</li>
     *     <li>0x38 is applied to Number Button 5</li>
     *     <li>0x34 is applied to Number Button 6</li>
     *     <li>0x33 is applied to Number Button 7</li>
     *     <li>0x37 is applied to Number Button 8</li>
     *     <li>0x39 is applied to Number Button 9</li>
     * </ul>
     * Finally, keyboard is showed as below:
     * <p>5  2  0</p>
     * <p>6  8  4</p>
     * <p>3  7  9</p>
     * <p>1</p>
     * <p></p>
     * <p>Case 3:</p>
     * <ul>
     *     <li>"numBtn" is specified in the order of Number Button 0, Number Button 1, Number Button 2, Number Button 3, Number Button 4, Number Button 5, Number Button 6, Number Button 7, Number Button 8, Number Button 9
     *     <li>"isRandomKeyboard" is set to "false"</li>
     *     <li>Fixed buffer {0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39} will be returned</li>
     * </ul>
     * Then according to the order of parameter "numBtn":
     * <ul>
     *     <li>0x30 is applied to Number Button 0</li>
     *     <li>0x31 is applied to Number Button 1</li>
     *     <li>0x32 is applied to Number Button 2</li>
     *     <li>0x33 is applied to Number Button 3</li>
     *     <li>0x34 is applied to Number Button 4</li>
     *     <li>0x35 is applied to Number Button 5</li>
     *     <li>0x36 is applied to Number Button 6</li>
     *     <li>0x37 is applied to Number Button 7</li>
     *     <li>0x38 is applied to Number Button 8</li>
     *     <li>0x39 is applied to Number Button 9</li>
     * </ul>
     * Finally, keyboard is showed as below:
     * <p>1  2  3</p>
     * <p>4  5  6</p>
     * <p>7  8  9</p>
     * <p>0</p>
     *
     * @param numBtn           <b>[Required]</b> 10 number buttons. Each button can be defined by two coordinate points(8 bytes): left-top and right-bottom.
     *                         <p>E.g., (0x00B4, 0x046D) is the left-top point of the first number button, and the right-bottom point is (0x0167, 0x0500).</p>
     *                         <p>So the first button can be defined by ((0x00B4, 0x046D), (0x0167, 0x0500)), which can be converted to a 8 bytes buffer:
     *                         {0xB4, 0x00, 0x6D, 0x04, 0x67, 0x01, 0x00, 0x05}.</p>
     * @param funcBtn          <b>[Required]</b> Function buttons(Backspace, Cancel, Enter). Each button can be defined by key ID(4 bytes) and two coordinate points(8 bytes).
     *                         <ul>
     *                         <li>Backspace button ID: {0x0A, 0x00, 0x00, 0x00}</li>
     *                         <li>Cancel button ID: {0x1B, 0x00, 0x00, 0x00}</li>
     *                         <li>Enter button ID: {0x0D, 0x00, 0x00, 0x00}</li>
     *                         <li>Quit button ID: {0x9B, 0x00, 0x00, 0x00}</li>
     *                         <li>Clear button ID: {0x9C, 0x00, 0x00, 0x00}</li>
     *                         </ul>
     *                         <p>E.g., the key ID of Enter button is {0x0D, 0x00, 0x00, 0x00}, left-top point is (0x021C, 0x03D8), right-bottom point is (0x02CF, 0x0500).</p>
     *                         <p>So the Enter button can be defined by {0x0D, 0x00, 0x00, 0x00, 0x1C, 0x02, 0xD8, 0x03, 0xCF, 0x02, 0x00, 0x05}.</p>
     *                         <p>For the function buttons, only support {PINPadButton.CANCEL, PINPadButton.BACKSPACE, PINPadButton.ENTER} and {PINPadButton.QUIT, PINPadButton.CLEAR, PINPadButton.ENTER} matching.</p>
     * @param isRandomKeyboard Whether return randomized number buffer or not.
     * @return 10 number values that match the 10 number button in order of "numBtn" parameter. This can be used to display keyboard.
     * @throws NSDKException  If it returned "-1709" means this device does not support positive sequence keyboard layout. And "isRandomKeyboard" parameter shall be set to true when using this interface.
     */
    byte[] initKeyLayout(byte[] numBtn, byte[] funcBtn, boolean isRandomKeyboard) throws NSDKException;//NAPI_SecVppTpInit

    /**
     * Initializes keyboard layout.
     *
     * <ul>Note:
     * <li>This shall be called before starting PIN entry every time.</li>
     * <li>Once PIN entry is started, touch screen will be controlled by security module, applications can not detect where user touched.</li>
     * </ul>
     *
     * <p>Example:</p>
     * <pre>
     * {@code
     *     public Map<PINPadButton, int[]> getPINPadButtons(){
     *         int[] l = new int[2];
     *         getLocationOnScreen(l);
     *         int x0 = l[0], x1 = (int) (l[0] + width / 4), x2 = (int) (l[0] + width / 2), x3 = (int) (l[0] + width / 4 * 3), x4 = (int) (l[0] + width);
     *         int y0 = l[1], y1 = (int) (l[1] + height / 4), y2 = (int) (l[1] + height / 2), y3 = (int) (l[1] + height / 4 * 3), y4 = (int) (l[1] + height);
     *
     *         Map<PINPadButton, int[]> buttonMap = new HashMap<>();
     *         buttonMap.put(PINPadButton.NUMBER_0, new int[]{x1, y3, x2, y4});
     *         buttonMap.put(PINPadButton.NUMBER_1, new int[]{x0, y0, x1, y1});
     *         buttonMap.put(PINPadButton.NUMBER_2, new int[]{x1, y0, x2, y1});
     *         buttonMap.put(PINPadButton.NUMBER_3, new int[]{x2, y0, x3, y1});
     *         buttonMap.put(PINPadButton.NUMBER_4, new int[]{x0, y1, x1, y2});
     *         buttonMap.put(PINPadButton.NUMBER_5, new int[]{x1, y1, x2, y2});
     *         buttonMap.put(PINPadButton.NUMBER_6, new int[]{x2, y1, x3, y2});
     *         buttonMap.put(PINPadButton.NUMBER_7, new int[]{x0, y2, x1, y3});
     *         buttonMap.put(PINPadButton.NUMBER_8, new int[]{x1, y2, x2, y3});
     *         buttonMap.put(PINPadButton.NUMBER_9, new int[]{x2, y2, x3, y3});
     *
     *         buttonMap.put(PINPadButton.CANCEL, new int[]{x3, y0, x4, y1});
     *         buttonMap.put(PINPadButton.BACKSPACE, new int[]{x3, y1, x4, y2});
     *         buttonMap.put(PINPadButton.ENTER, new int[]{x3, y2, x4, y4});
     *
     *         return buttonMap;
     *     }
     *
     *    boolean isRandomLayout = true;
     *
     *    try {
     *        Map<PINPadButton, int[]> buttons = getPINPadButtons();
     *        byte[] outSeq = pinInput.initKeyLayout(buttons,isRandomLayout);
     *        // E.g., {6, 5, 0, 4, 8, 9, 1, 3, 2, 7} is returned, then:
     *        // "6" is used to display for PINPadButton.NUMBER_0 which is at {x1, y3, x2, y4}
     *        // "5" is used to display for PINPadButton.NUMBER_1 which is at {x0, y0, x1, y1}
     *        // "0" is used to display for PINPadButton.NUMBER_2 which is at {x1, y0, x2, y1}
     *        // "4" is used to display for PINPadButton.NUMBER_3 which is at {x2, y0, x3, y1}
     *        // "8" is used to display for PINPadButton.NUMBER_4 which is at {x0, y1, x1, y2}
     *        // "9" is used to display for PINPadButton.NUMBER_5 which is at {x1, y1, x2, y2}
     *        // "1" is used to display for PINPadButton.NUMBER_6 which is at {x2, y1, x3, y2}
     *        // "3" is used to display for PINPadButton.NUMBER_7 which is at {x0, y2, x1, y3}
     *        // "2" is used to display for PINPadButton.NUMBER_8 which is at {x1, y2, x2, y3}
     *        // "7" is used to display for PINPadButton.NUMBER_9 which is at {x2, y2, x3, y3}
     *    } catch (Exception e) {
     *        e.printStackTrace();
     *    }
     * }
     * </pre>
     *
     * @param pinPadButtons    PIN pad button coordinates. At least 13 buttons required, see {@link PINPadButton}. Each button needs two points to locate: left top and right bottom.
     *                         <p>E.g., (0x00B4, 0x046D) is the left-top point of the first number button, and the right-bottom point is (0x0167, 0x0500).</p>
     *                         <p>So add this button to this parameter as below:</p>
     *                         <pre>pinPadButtons.put(PINPadButton.NUMBER_0, new int[]{0x00B4, 0x046D, 0x0167, 0x0500});</pre>
     *                         <p>For the function buttons, only support {PINPadButton.CANCEL, PINPadButton.BACKSPACE, PINPadButton.ENTER} and {PINPadButton.QUIT, PINPadButton.CLEAR, PINPadButton.ENTER} matching.</p>
     * @param isRandomKeyboard Whether return randomized number buffer or not.
     * @return 10 number values that match the 10 number button in order of {@link PINPadButton}. This can be used to display keyboard.
     * @throws NSDKException
     */
    byte[] initKeyLayout(Map<PINPadButton, int[]> pinPadButtons, boolean isRandomKeyboard) throws NSDKException;

    /**
     * Initializes RNIB key layout, which is used for the blind.
     * <ul>Note:
     * <li>This shall be called before starting the PIN entry for the blind.</li>
     * <li>Once PIN entry is started, touch screen will be controlled by security module, applications can not detect where user touched.</li>
     * </ul>
     * <p>Example:</p>
     * <pre>
     * @code
     *      int[] l = new int[2];
     *      getLocationOnScreen(l);
     *      int x0 = l[0], x1 = (int) (l[0] + width / 4), x2 = (int) (l[0] + width / 2), x3 = (int) (l[0] + width / 4 * 3), x4 = (int) (l[0] + width);
     *      int y0 = l[1], y1 = (int) (l[1] + height / 4), y2 = (int) (l[1] + height / 2), y3 = (int) (l[1] + height / 4 * 3), y4 = (int) (l[1] + height);
     *      Map<PINPadButton, int[]> pinPadButtonMap = new HashMap<>();
     *      pinPadButtonMap.put(PINPadButton.NUMBER_1, new int[] {x0, y0, x1, y1});
     *      pinPadButtonMap.put(PINPadButton.NUMBER_2, new int[] {x1, y0, x2, y1});
     *      pinPadButtonMap.put(PINPadButton.NUMBER_3, new int[] {x2, y0, x3, y1});
     *      pinPadButtonMap.put(PINPadButton.NUMBER_4, new int[] {x0, y1, x1, y2});
     *      pinPadButtonMap.put(PINPadButton.NUMBER_5, new int[] {x1, y1, x2, y2});
     *      pinPadButtonMap.put(PINPadButton.NUMBER_6, new int[] {x2, y1, x3, y2});
     *      pinPadButtonMap.put(PINPadButton.NUMBER_7, new int[] {x0, y2, x1, y3});
     *      pinPadButtonMap.put(PINPadButton.NUMBER_8, new int[] {x1, y2, x2, y3});
     *      pinPadButtonMap.put(PINPadButton.NUMBER_9, new int[] {x2, y2, x3, y3});
     *      pinPadButtonMap.put(PINPadButton.NUMBER_0, new int[] {x1, y3, x2, y4});
     *      pinPadButtonMap.put(PINPadButton.CANCEL, new int[] {x0, y3, x1, y5});
     *      //BackSpace function could be optional
     *      pinPadButtonMap.put(PINPadButton.BACKSPACE, new int[] {x1, y4, x2, y5});
     *      pinPadButtonMap.put(PINPadButton.ENTER, new int[] {x2, y3, x3, y5});
     *
     *      //ScreeArea can be two coordinate of top-left and right-bottom or one coordinate of roght-bottom.
     *      int[] screenArea = new int[] {0,0, width, height};
     *      try {
     *          pinEntry.initKeyLayout(pinPadButtonMap, screenArea)
     *      } catch (NSDKException e) {
     *
     *      }
     *
     * </pre>
     * @param pinPadButtons    <b>[Required]</b> PIN pad button coordinates. The number keys shall contain {@link PINPadButton#NUMBER_0} to {@link PINPadButton#NUMBER_9}, and the function keys
     *                         shall contain {@link PINPadButton#CANCEL}, {@link PINPadButton#BACKSPACE} and {@link PINPadButton#ENTER}. Each button needs two points to locate: left top and right bottom.
     *                         <p>E.g., (0x00B4, 0x046D) is the left-top point of the first number button, and the right-bottom point is (0x0167, 0x0500).</p>
     *                         <p>So add this button to this parameter as below:</p>
     *                         <pre>pinPadButtons.put(PINPadButton.NUMBER_0, new int[]{0x00B4, 0x046D, 0x0167, 0x0500});</pre>
     *                         <p>For the blink keyboard, this only support {PINPadButton.CANCEL, PINPadButton.BACKSPACE, PINPadButton.ENTER} and {PINPadButton.CANCEL, PINPadButton.ENTER} matching.</p>
     * @param screenArea       <b>[Required]</b> The whole screen area coordinates, which are used for detect whether the current position is out of the keypad range. It can be two points' coordinates(left-top and right-bottom) whose length is 4 or the right-bottom coordinate whose length is 2.
     * @throws NSDKException
     */
    void initKeyLayout(Map<PINPadButton, int[]> pinPadButtons, int[] screenArea) throws NSDKException;

    /**
     * Starts online PIN input.
     *
     * <p>Note: Key layout shall be initialized before starting PIN input.</p>
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
     *     PINEntryParameters params = new PINEntryParameters();
     *     params.setPINBlockMode(PINBlockMode.ISO9564_0);
     *     params.setMinPINLen(6);
     *     params.setMaxPINLen(10);
     *     params.setPINLengthRange(new byte[]{0x06,0x08,0x09});
     *
     *     PINEntryListener listener = new PINEntryListener() {
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
     *        {@code @Override}
     *         public void onError(int code, String message) {
     *             // Handle PIN input error.
     *         }
     *     };
     *
     *     try {
     *         pinEntry.startOnlinePINEntry(desKey, pan, timeout, params, listener);
     *     } catch (NSDKException e) {
     *         // Handle the exception
     *     }
     * </pre>
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
     * @param parameters <b>[Required]</b> PIN entry parameters. See {@link PINEntryParameters}
     *                   <ul>
     *                   <li><b>[Required]</b> PIN block mode</li>
     *                   </ul>
     * @param listener   <b>[Required]</b> Listens to PIN events. See {@link PINEntryListener}
     * @throws NSDKException
     */
    void startOnlinePINEntry(Key key, String pan, int timeout, PINEntryParameters parameters, PINEntryListener listener) throws NSDKException;//NAPI_SecVPPInit

    /**
     * Starts offline PIN entry.
     *
     * <ul>Note:
     * <li>Usually, offline PIN is used during EMV process.</li>
     * <li>Offline PIN needs IC card to verify. So the card shall be powered up before starting offline PIN entry.</li>
     * <li>Check PIN block returned by {@link PINEntryListener#onFinish(int, byte[], byte[])} to see if the offline PIN is correct.</li>
     * </ul>
     *
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
     *     PINEntryListener listener = new PINEntryListener() {
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
     *        {@code @Override}
     *         public void onError(int code, String message) {
     *             // Handle PIN input error.
     *         }
     *     };
     *
     *     try {
     *          pinEntry.startOfflinePINEntry(rsaKey, timeout, null, listener);
     *     } catch (NSDKException e) {
     *         e.printStackTrace();
     *     }
     * </pre>
     *
     * @param rsaKey     <b>[Optional]</b> RSA key. This is required for cipher PIN, otherwise it can be set to null.
     * @param timeout    <b>[Required]</b> Timeout. Unit: second. Value range: [5-200].
     * @param parameters <b>[Optional]</b> PIN entry parameters. See {@link PINEntryParameters}
     * @param listener   <b>[Required]</b> Listens to PIN events. See {@link PINEntryListener}
     * @throws NSDKException
     */
    void startOfflinePINEntry(RSAKey rsaKey, int timeout, PINEntryParameters parameters, PINEntryListener listener) throws NSDKException;//NAPI_SecVPPInit

    /**
     * Starts online PIN entry with the keyboard for the blind.
     * <p>Note: RNIB Key layout shall be initialized before starting PIN input.</p>
     * <p>Example:</p>
     * <pre>
     * @code
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
     *     PINEntryParameters params = new PINEntryParameters();
     *     params.setPINBlockMode(PINBlockMode.ISO9564_0);
     *     params.setMinPINLen(6);
     *     params.setMaxPINLen(10);
     *     params.setPINLengthRange(new byte[]{0x06,0x08,0x09});
     *
     *     RNIBPINEntryListener listener = new RNIBPINEntryListener() {
     *        {@code @Override}
     *         public void onSlidNumberKey() {
     *             // Slid into number key, application shall call beeper to notice.
     *         }
     *        {@code @Override}
     *         public void onSlidEnter() {
     *             // Slid into enter function key, application shall play the according voice.
     *         }
     *        {@code @Override}
     *         public void onSlidCancel() {
     *             // Slid into cancel function key, application shall play the according voice.
     *         }
     *        {@code @Override}
     *         public void onSlidUp() {
     *             // Slid above the keyboard area, application shall play the according voice.
     *         }
     *        {@code @Override}
     *         public void onSlidDown() {
     *             // Slid below the keyboard area, application shall play the according voice.
     *         }
     *        {@code @Override}
     *         public void onSlidLeft() {
     *             // Slid to the left of the keyboard area, application shall play the according voice.
     *         }
     *        {@code @Override}
     *         public void onSlidRight() {
     *             // Slid to the right of the keyboard area, application shall play the according voice.
     *         }
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
     *        {@code @Override}
     *         public void onError(int code, String message) {
     *             // Handle PIN input error.
     *         }
     *     };
     *
     *     try {
     *         pinEntry.startOnlinePINEntry(desKey, pan, timeout, params, listener);
     *     } catch (NSDKException e) {
     *         // Handle the exception
     *     }
     * </pre>
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
     * @param parameters <b>[Required]</b> PIN entry parameters. See {@link PINEntryParameters}
     *                   <ul>
     *                   <li><b>[Required]</b> PIN block mode</li>
     *                   </ul>
     * @param listener   <b>[Required]</b> Listens to RNIB PIN events. See {@link RNIBPINEntryListener}
     * @throws NSDKException
     */
    void startOnlinePINEntry(Key key, String pan, int timeout, PINEntryParameters parameters, RNIBPINEntryListener listener) throws NSDKException;

    /**
     * Starts offline PIN entry with the keyboard for the blind.
     *
     * <ul>Note:
     * <li>Usually, offline PIN is used during EMV process.</li>
     * <li>Offline PIN needs IC card to verify. So the card shall be powered up before starting offline PIN entry.</li>
     * <li>Check PIN block returned by {@link PINEntryListener#onFinish(int, byte[], byte[])} to see if the offline PIN is correct.</li>
     * </ul>
     *
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
     *     RNIBPINEntryListener listener = new RNIBPINEntryListener() {
     *        {@code @Override}
     *         public void onSlidNumberKey() {
     *             // Slid into number key, application shall call beeper to notice.
     *         }
     *        {@code @Override}
     *         public void onSlidEnter() {
     *             // Slid into enter function key, application shall play the according voice.
     *         }
     *        {@code @Override}
     *         public void onSlidCancel() {
     *             // Slid into cancel function key, application shall play the according voice.
     *         }
     *        {@code @Override}
     *         public void onSlidUp() {
     *             // Slid above the keyboard area, application shall play the according voice.
     *         }
     *        {@code @Override}
     *         public void onSlidDown() {
     *             // Slid below the keyboard area, application shall play the according voice.
     *         }
     *        {@code @Override}
     *         public void onSlidLeft() {
     *             // Slid to the left of the keyboard area, application shall play the according voice.
     *         }
     *        {@code @Override}
     *         public void onSlidRight() {
     *             // Slid to the right of the keyboard area, application shall play the according voice.
     *         }
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
     *        {@code @Override}
     *         public void onError(int code, String message) {
     *             // Handle PIN input error.
     *         }
     *     };
     *
     *     try {
     *          pinEntry.startOfflinePINEntry(rsaKey, timeout, null, listener);
     *     } catch (NSDKException e) {
     *         e.printStackTrace();
     *     }
     * </pre>
     *
     * @param rsaKey     <b>[Optional]</b> RSA key. This is required for cipher PIN, otherwise it can be set to null.
     * @param timeout    <b>[Required]</b> Timeout. Unit: second. Value range: [5-200].
     * @param parameters <b>[Optional]</b> PIN entry parameters. See {@link PINEntryParameters}
     * @param listener   <b>[Required]</b> Listens to RNIB PIN events. See {@link RNIBPINEntryListener}
     * @throws NSDKException
     */
    void startOfflinePINEntry(RSAKey rsaKey, int timeout, PINEntryParameters parameters, RNIBPINEntryListener listener) throws NSDKException;

    /**
     * Cancels PIN entry.
     */
    void cancelPINEntry() throws NSDKException;

    /**
     * Verifies offline PIN.
     *
     * <ul>Note:
     * <li>This is usually used when the external devices only can provide the ability to input PIN without verifying it.</li>
     * <li>Same PIN key shall be used with the external device.</li>
     * <li>Card shall be powered up before verifying offline PIN.</li>
     * </ul>
     *
     * @param key      <b>[Required]</b> Key used to decrypt PIN. When using external key to decrypt PIN, this key is used to protect the external key.
     *                 <ul>
     *                 <li>Key ID.</li>
     *                 <li>Key type: Default type is {@link KeyType#DES}.</li>
     *                 </ul>
     * @param pan      <b>[Required]</b> PAN.
     * @param pinBlock <b>[Required]</b> PIN block.
     * @param rsaKey   <b>[Optional]</b> RSA key. This is required for cipher PIN, otherwise it can be set to null.
     * @param extKey   <b>[Optional]</b> External key used to decrypt PIN.
     * @return Result of offline PIN verification. If [0x90, 0x00], success.
     * @throws NSDKException
     */
    byte[] verifyOfflinePIN(Key key, String pan, byte[] pinBlock, RSAKey rsaKey, byte[] extKey) throws NSDKException;

    /**
     * Converts the pinBlock which was encrypted by the sessionKey negotiated by the upper and pinpad device with the targeted PIN key.
     * <p>Note: This is used for the circumstance with pin block calculation with session key in the pinpad device, and convert it with the PIN key in the upper device.</p>
     * @param pinConvertParameters  <b>[Required]</b> The parameters used for convert process, see {@link PINConvertParameters}.
     * @param sessionKey            <b>[Required]</b> The symmetrical session key, which was negotiated with the pinpad device.
     * @param pinKey                <b>[Required]</b> The symmetrical pin key for convert pin block process.
     * @param pinBlock              <b>[Required]</b> The pin block data encrypted by the session key in the pinpad device.
     * @return The pin block converted by the PIN key in the upper device.
     * @throws NSDKException
     */
    byte[] convertPINBlock(PINConvertParameters pinConvertParameters, SymmetricKey sessionKey, SymmetricKey pinKey, byte[] pinBlock) throws NSDKException;
}
