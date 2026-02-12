package com.newland.nsdk.core.api.external.pinentry;

import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.keymanager.Key;
import com.newland.nsdk.core.api.common.keymanager.AsymmetricKey;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.common.keymanager.KeyUsage;

/**
 * <b>[External Module]</b> Provides the ability for online/offline PIN entry.
 *
 * <p>How to get this module:</p>
 * <pre>
 *     ExtPINEntry extPINEntry = (ExtPINEntry)ExtNSDKModuleManagerImpl.getInstance().getModule(ModuleType.EXT_PIN_ENTRY);
 * </pre>
 */
public interface ExtPINEntry extends Module {
    /**
     * Starts online PIN entry.
     *
     * <p>Example:</p>
     * <pre>
     *     try {
     *         SymmetricKey pinKey = new SymmetricKey();
     *         pinKey.setKeyID(pinKeyIndex);
     *         pinKey.setKeyType(KeyType.DES);
     *
     *         String pan = "6214050711033116";
     *         int timeout = 60;
     *
     *         ExtPINEntryParameters parameter = new ExtPINEntryParameters();
     *         parameter.setMaxPINLen((byte) 6);
     *         parameter.setMaskLine(ExtPINMaskLine.LINE_4);
     *         parameter.setDisplayMessages(new String[]{"Input online pin"});
     *         parameter.setPINBlockMode(PINBlockMode.ISO9564_0);
     *         parameter.setAutoComplete(true);
     *
     *         extPINEntry.startOnlinePINEntry(pinKey, pan, timeout, parameter, new ExtPINEntryListener() {
     *
     *            {@code @Override}
     *             public void onOnlineSuccess(int pinLen, byte[] pinBlock, byte[] dukptKsn) {
     *                 // Do something when online PIN input success.
     *             }
     *
     *            {@code @Override}
     *             public void onOfflineSuccess(int pinLen, byte[] pinBlock, byte[] randomKey) {
     *                 // Do something when offline PIN input success.
     *             }
     *
     *            {@code @Override}
     *             public void onError(int errorCode, String msg) {
     *                 // Do something when error.
     *             }
     *
     *            {@code @Override}
     *             public void onTimeout() {
     *                 // Do something when timeout.
     *             }
     *
     *            {@code @Override}
     *             public void onCancel() {
     *                 // Do something when cancelled.
     *             }
     *         });
     *     } catch (NSDKException e) {
     *         // Handle the error.
     *     }
     * </pre>
     *
     * @param key                <b>[Required]</b> PIN key used to encrypt PIN.
     *                           <ul>
     *                           <li>Key ID</li>
     *                           <li>Key type</li>
     *                           <li>Key usage: If using DUKPT to encrypt PIN, set key usage to {@link KeyUsage#DUKPT}</li>
     *                           </ul>
     * @param panInfo            <b>[Required]</b> Plain PAN or Hash value of PAN which will be used to calculate PIN block.
     * @param timeout            <b>[Required]</b> Timeout between key presses. Unit: second.
     * @param pinEntryParameters <b>[Required]</b> Parameters for online PIN entry.
     *                           <ul>
     *                           <li>{@link ExtPINEntryParameters} is for normal use.</li>
     *                           <li>{@link ExtOnlinePINParameters} provides reserved parameters.</li>
     *                           </ul>
     * @param listener           <b>[Required]</b> Listens to the result of PIN entry. See {@link ExtPINEntryListener}.
     * @throws NSDKException
     */
    void startOnlinePINEntry(Key key, String panInfo, int timeout, ExtPINEntryParameters pinEntryParameters, ExtPINEntryListener listener) throws NSDKException;

    /**
     * Starts online PIN entry.
     *
     * <p>Example:</p>
     * <pre>
     *     try {
     *         SymmetricKey pinKey = new SymmetricKey();
     *         pinKey.setKeyID(pinKeyIndex);
     *         pinKey.setKeyType(KeyType.DES);
     *
     *         int timeout = 60;
     *
     *         SymmetricKey panKey = new SymmetricKey();
     *         panKey.setKeyID(trackKeyIndex);
     *         panKey.setKeyType(KeyType.DES);
     *
     *         CipherPAN cipherPAN = new CipherPAN();
     *         cipherPAN.setPANKey(panKey);
     *         cipherPAN.setCipherPAN(ISOUtils.hex2byte("86BEC8567FDD69F104063642C76CFEC4"));
     *
     *         ExtPINEntryParameters parameter = new ExtPINEntryParameters();
     *         parameter.setMaxPINLen((byte) 6);
     *         parameter.setMaskLine(ExtPINMaskLine.LINE_4);
     *         parameter.setDisplayMessages(new String[]{"Input online pin"});
     *         parameter.setPINBlockMode(PINBlockMode.ISO9564_0);
     *         parameter.setAutoComplete(true);
     *
     *         extPINEntry.startOnlinePINEntry(pinKey, cipherPAN, timeout, parameter, new ExtPINEntryListener() {
     *
     *            {@code @Override}
     *             public void onOnlineSuccess(int pinLen, byte[] pinBlock, byte[] dukptKsn) {
     *                 // Do something when online PIN input success.
     *             }
     *
     *            {@code @Override}
     *             public void onOfflineSuccess(int pinLen, byte[] pinBlock, byte[] randomKey) {
     *                 // Do something when offline PIN input success.
     *             }
     *
     *            {@code @Override}
     *             public void onError(int errorCode, String msg) {
     *                 // Do something when error.
     *             }
     *
     *            {@code @Override}
     *             public void onTimeout() {
     *                 // Do something when timeout.
     *             }
     *
     *            {@code @Override}
     *             public void onCancel() {
     *                 // Do something when cancelled.
     *             }
     *         });
     *     } catch (NSDKException e) {
     *         // Handle the error.
     *     }
     * </pre>
     *
     * @param key               <b>[Required]</b> PIN key used to encrypt PIN, only {@link SymmetricKey} or {@link AsymmetricKey} is allowed.
     * @param cipherPAN         <b>[Required]</b> Cipher PAN which is encrypted by PAN key, see {@link CipherPAN}. The pan will be used to calculate the pin block.
     * @param timeout           <b>[Required]</b> Timeout between key presses. Unit: second.
     * @param pinInputParameter <b>[Required]</b> Parameters for online PIN entry.
     *                          <ul>
     *                          <li>{@link ExtPINEntryParameters} is for normal use.</li>
     *                          <li>{@link ExtOnlinePINParameters} provides reserved parameters.</li>
     *                          </ul>
     * @param listener          <b>[Required]</b> Listens to the result of PIN entry. See {@link ExtPINEntryListener}.
     * @throws NSDKException
     */
    void startOnlinePINEntry(Key key, CipherPAN cipherPAN, int timeout, ExtPINEntryParameters pinInputParameter, ExtPINEntryListener listener) throws NSDKException;

    /**
     * Starts offline PIN entry.
     *
     * <p>Note: This only requests to enter PIN, it will not verify offline PIN.</p>
     *
     * <p>Example:</p>
     * <pre>
     *     ExtPINEntryListener listener = new ExtPINEntryListener() {
     *        {@code @Override}
     *         public void onOnlineSuccess(int pinLen, byte[] pinBlock, byte[] dukptKSN) {
     *             // Do something when online PIN input success.
     *         }
     *
     *        {@code @Override}
     *         public void onOfflineSuccess(int pinLen, byte[] pinBlock, byte[] randomKey) {
     *             // Do something when offline PIN input success.
     *             // When random protect mode is set to true, random key will be returned. It is encrypted by the specified master key.
     *         }
     *
     *        {@code @Override}
     *         public void onError(int errorCode, String msg) {
     *             // Do something when error.
     *         }
     *
     *        {@code @Override}
     *         public void onTimeout() {
     *             // Do something when timeout.
     *         }
     *
     *        {@code @Override}
     *         public void onCancel() {
     *             // Do something when cancelled.
     *         }
     *     }
     *
     *     // Case 1: Normal offline PIN entry
     *     try {
     *         ExtPINEntryParameters parameter = new ExtPINEntryParameters();
     *         parameter.setMaxPINLen((byte) 6);
     *         parameter.setMaskLine(ExtPINMaskLine.LINE_4);
     *         parameter.setDisplayMessages(new String[]{"Input offline pin"});
     *         parameter.setPINBlockMode(PINBlockMode.ISO9564_0);
     *         parameter.setAutoComplete(true);
     *
     *         SymmetricKey pinKey = new SymmetricKey();
     *         pinKey.setKeyID(pinKeyIndex);
     *         pinKey.setKeyType(KeyType.DES);
     *
     *         String pan = "6214050711033116";
     *
     *         int timeout = 60;
     *
     *         extPINEntry.startOfflinePINEntry(pinKey, timeout, pan, parameter, listener);
     *     } catch (NSDKException e) {
     *         // Handle the error.
     *     }
     *
     *     // Case 2: Offline PIN entry using random key
     *     try {
     *         ExtOfflinePINParameters parameter = new ExtOfflinePINParameters();
     *         parameter.setMaxPINLen((byte) 6);
     *         parameter.setMaskLine(ExtPINMaskLine.LINE_4);
     *         parameter.setDisplayMessages(new String[]{"Input offline pin"});
     *         parameter.setPINBlockMode(PINBlockMode.ISO9564_0);
     *         parameter.setAutoComplete(true);
     *         parameter.setRandomProtectMode(true);
     *
     *         // The key used to protect random key.
     *         SymmetricKey key = new SymmetricKey();
     *         key.setKeyID(masterKeyIndex);
     *         key.setKeyType(KeyType.DES);
     *
     *         String pan = "6214050711033116";
     *
     *         extPINEntry.startOfflinePINEntry(key, pan, timeout, parameter, listener);
     *     } catch (NSDKException e) {
     *         // Handle the error.
     *     }
     * </pre>
     *
     * @param key                      <b>[Required]</b> This could be PIN key or protection key which is used to generate random key when set {@link ExtOfflinePINParameters#setRandomProtectMode(boolean)} to true.
     * @param panInfo                  <b>[Required]</b> Plain PAN or hash value of PAN which will be used to calculate PIN block.
     * @param timeout                  <b>[Required]</b> Timeout between key presses. Unit: second.
     * @param offlinePinInputParameter <b>[Required]</b> Parameters for online PIN entry.
     *                                 <ul>
     *                                 <li>{@link ExtPINEntryParameters} is for normal use.</li>
     *                                 <li>{@link ExtOfflinePINParameters} provides more parameters for offline PIN entry.</li>
     *                                 </ul>
     * @param listener                 <b>[Required]</b> Listens to the result of PIN entry. See {@link ExtPINEntryListener}.
     * @throws NSDKException
     */
    void startOfflinePINEntry(Key key, String panInfo, int timeout, ExtPINEntryParameters offlinePinInputParameter, ExtPINEntryListener listener) throws NSDKException;

    /**
     * Starts offline PIN entry process.
     * <p>Note: This is used to initiates an offline PIN entry process on the pin-pad.</p>
     * @param rsaKey               <b>[Optional]</b> RSA key. This is required for cipher PIN, otherwise it can be set to null. See {@link RSAKey}.
     * @param timeout              <b>[Required]</b> Timeout. Unit: second. Value range: [5, 200]
     * @param pinEntryParameters   <b>[Required]</b> PIN entry parameters. In this instruct, only two object is valid in {@link ExtPINEntryParameters}.
     *                             <ul>
     *                                 <li>maxPINLen: The maximum pin entry length, value range: [4, 12]</li>
     *                                 <li>displayMessages: The messages to be shown on the pin-pad.</li>
     *                             </ul>
     * @param listener             <b>[Required]</b> Listen to PIN events. See {@link ExtPINEntryListener}.
     * @throws NSDKException
     */
    void startOfflinePINEntry(RSAKey rsaKey, int timeout, ExtPINEntryParameters pinEntryParameters, ExtPINEntryListener listener) throws NSDKException;

    /**
     * Cancels PIN entry.
     */
    void cancelPINEntry() throws NSDKException;
}
