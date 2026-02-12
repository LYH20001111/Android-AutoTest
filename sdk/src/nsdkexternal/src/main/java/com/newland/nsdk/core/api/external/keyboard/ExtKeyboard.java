package com.newland.nsdk.core.api.external.keyboard;

import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.crypto.AlgorithmParameters;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.keymanager.Key;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;

/**
 * <b>[External Module]</b> Provides the ability to use the external device as a normal keyboard for digits/character input.
 *
 * <p>How to get this module:</p>
 * <pre>
 *     ExtKeyboard extKeyboard = (ExtKeyboard)ExtNSDKModuleManagerImpl.getInstance().getModule(ModuleType.EXT_KEYBOARD);
 * </pre>
 */
public interface ExtKeyboard extends Module {
    /**
     * Starts key entry, the input will be encrypted by the specified protection key.
     *
     * <p>Example:</p>
     * <pre>
     *     SymmetricKey key = new SymmetricKey();
     *     key.setKeyID((byte) 129);
     *     key.setKeyType(KeyType.DES);
     *
     *     int timeout = 1;
     *
     *     KeyboardParameter parameter = new KeyboardParameter();
     *     parameter.setMaxLen((byte) 8);
     *     parameter.setMinLen((byte) 4);
     *     parameter.setPromptID(PromptID.PHONE_NUMBER);
     *     parameter.setKeyboardMode(KeyboardMode.ALL_CHARACTERS);
     *     try {
     *         extKeyboard.startKeyEntry(key, timeout, parameter, new KeyboardListener() {
     *            {@code @Override}
     *             public void onError(int code,String message) {
     *                 // Do something when error.
     *             }
     *
     *            {@code @Override}
     *             public void onSuccess(int inputLen, byte[] encryptedData) {
     *                 // Do something when success.
     *             }
     *
     *            {@code @Override}
     *             public void onTimeout() {
     *                 // Do something when timeout.
     *             }
     *
     *            {@code @Override}
     *             public void onCancel() {
     *                 // Do something when cancel.
     *             }
     *         });
     *     } catch (NSDKException e) {
     *         // Handle the error
     *     }
     * </pre>
     *
     * @param dataKey   <b>[Required]</b> Key used to encrypt input data.
     * @param timeout   <b>[Required]</b> Timeout between key presses. Unit: second, value range: (0, 90].
     * @param parameter <b>[Required]</b> Parameters for keyboard entry, see {@link KeyboardParameters}.
     * @param listener  <b>[Required]</b> Listens to the result of keyboard entry.
     * @throws NSDKException
     */
    void startKeyEntry(Key dataKey, int timeout, KeyboardParameters parameter, KeyboardListener listener) throws NSDKException;

    /**
     * Starts key entry, the input will be encrypted by the specified protection key.
     * @param dataKey   <b>[Required]</b> Key used to encrypt input data.
     * @param algParams <b>[Optional]</b> The algorithm parameter used to encrypt input data.
     * @param timeout   <b>[Required]</b> Timeout between key presses. Unit: second, value range: (0, 90].
     * @param parameter <b>[Required]</b> Parameters for keyboard entry, see {@link KeyboardParameters}.
     * @param listener  <b>[Required]</b> Listens to the result of keyboard entry.
     * @throws NSDKException
     */
    void startKeyEntry(SymmetricKey dataKey, AlgorithmParameters algParams, final int timeout, final KeyboardParameters parameter, final KeyboardListener listener) throws NSDKException;

    /**
     * Cancels current keyboard entry.
     *
     * @throws NSDKException
     */
    void cancelKeyEntry() throws NSDKException;

    /**
     * Initiates once or serial times of input data request in PIN pad, and the data entry result will return back by listener.
     * @param inputItems  <b>[Required]</b> The configuration for the input data procedure, see {@link InputItem}.
     * @param parameters  <b>[Required]</b> The parameters for the input data procedure, see {@link InputParameters}.
     * @param listener    <b>[Required]</b> Listens to the result of input data procedure, see {@link InputListener}.
     * @throws NSDKException
     */
    void inputData(InputItem[] inputItems, InputParameters parameters, InputListener listener) throws NSDKException;

    /**
     * Initiates input amount procedure, the amount entry result will return back by listener.
     * @param amountType      <b>[Required]</b> The input amount mode, see {@link AmountType}.
     * @param parameters      <b>[Required]</b> The parameters of the amount to be set, see {@link AmountParameters}.
     * @param timeout         <b>[Required]</b> The input amount process timeout. Unit:s, shall be >0.
     * @param amountListener  <b>[Required]</b> Listens to the result of amount entry.
     * @throws NSDKException
     */
    void inputAmount(AmountType amountType, AmountParameters parameters, int timeout, AmountListener amountListener) throws NSDKException;
}
