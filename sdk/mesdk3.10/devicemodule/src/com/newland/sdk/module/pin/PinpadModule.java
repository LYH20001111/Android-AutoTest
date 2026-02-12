package com.newland.sdk.module.pin;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.newland.rkl.RKLListener;
import com.newland.sdk.mtype.Module;

import java.util.Map;

/**
 * pininput module
 *
 * @author youjf
 * @since ver3.10.01
 */
public interface PinpadModule extends Module {

    /**
     * Starts a RKL process
     * @param params {@link RKLParams}
     * @param listener Listens to the status of current RKL process {@link RKLListener}
     */
    public void startRKL(RKLParams params,RKLListener listener);

    /**
     * Load the master key (supporting pos checking kcv)
     *
     * @param loadKeyMode     the mode of loading master key{@link LoadKeyMode}
     * @param algorithmMode   the algorithm mode{@link AlgorithmMode}
     * @param masterKeyIndex  master key index（1-200）
     * @param masterKeyData   <p>master key data to be loaded. </p>
     *                        <p>The data is plain master key data encrypted by 16 bytes 0x31 with 3DES algorithm when loadKeyMode is{@link LoadKeyMode#DEFAULT_ENCRYPT}</p>
     * @param checkValue      master key check value(sm4:6 bytes，other:4 bytes)
     * @param loadMKExtParams the external params used to load master key</p>{@link LoadMKExtParams}
     * @return true if success,false if error.
     * @since ver3.10.01
     */
    public boolean loadMasterKey(LoadKeyMode loadKeyMode, AlgorithmMode algorithmMode, @IntRange(from = 1, to = 200) int masterKeyIndex, @NonNull byte[] masterKeyData, @Nullable byte[] checkValue, LoadMKExtParams loadMKExtParams);

    /**
     * Load a working key
     *
     * @param keyWorkingMode  key working mode
     * @param workingKeyType  working key type
     * @param masterKeyIndex  associated master key index(1-200)
     * @param workingKeyIndex loaded working key index(1-200)
     * @param data            key data
     * @param kcv             checking value(sm4:6bytes，other:4bytes)
     * @param loadWKExtParams the external params that used in loading working key
     * @return true if success,false if error.
     * @since ver3.10.01
     */
    public boolean loadWorkingKey(LoadWKMode keyWorkingMode, AlgorithmMode algorithmMode, WorkingKeyType workingKeyType, @IntRange(from = 1, to = 200) int masterKeyIndex, @IntRange(from = 1, to = 200) int workingKeyIndex, @NonNull byte[] data, @Nullable byte[] kcv, LoadWKExtParams loadWKExtParams);

    /**
     * Load the DUKPT with KSN and IPEK
     *
     * @param loadKeyMode        the mode of loading DUKPT{@link LoadKeyMode}
     * @param ipekIndex          IPEK index (1-200)
     * @param ksn                fixed 10 bytes KSN, plaintext data
     * @param encryptedIPEK      the initial encryptedIPEK
     * @param loadDuktpExtParams The params used in load dukpt.
     * @return true if success,false if error.
     * @since ver3.10.01
     */
    public boolean loadIPEK(LoadKeyMode loadKeyMode, @IntRange(from = 1, to = 200) int ipekIndex, @NonNull byte[] ksn, @NonNull byte[] encryptedIPEK, LoadDuktpExtParams loadDuktpExtParams);

    /**
     * Data encryption
     *
     * @param keyManagement Key management system{@link KeyManagement}
     * @param algorithmMode the encryption algorithm {@link AlgorithmMode}
     * @param cipherMode    the encrypt type{@link CipherMode}
     * @param keyIndex      <p>(1-200),if the keyIndex is a working key index,it isn't need to set the workingKeyData in params</p>
     *                      <p>if the keyIndex is a master key index, it is need to set the workingKeyData in params</p>
     * @param inputData     the data to be encrypted
     * @param params        <p>the external params used to encrypt data,</p>
     *                      <p>e.g. the cbc init data and the working key data.</p>
     *                      {@link CipherExtParams}
     * @return encrypt result{@link CipherResult}
     * @since ver3.10.01
     */
    public CipherResult encrypt(KeyManagement keyManagement, AlgorithmMode algorithmMode, CipherMode cipherMode, @IntRange(from = 1, to = 200) int keyIndex, @NonNull byte[] inputData, CipherExtParams params);

    /**
     * Data encryption
     *
     * @param keyManagement Key management system
     * @param algorithmMode the decryption algorithm {@link AlgorithmMode}
     * @param cipherMode    the decrypt type
     * @param keyIndex      (1-200), if the keyIndex is a working key index,it isn't need to set the workingKeyData in params<p>
     *                      if the keyIndex is a master key index, it is need to set the workingKeyData in params<p>
     * @param inputData     the data to be decrypted
     * @param params        the external params to decrypt data, e.g. the cbc init data and the working key data.
     * @return decrypt result{@link CipherResult}
     * @since ver3.10.01
     */
    public CipherResult decrypt(KeyManagement keyManagement, AlgorithmMode algorithmMode, CipherMode cipherMode, @IntRange(from = 1, to = 200) int keyIndex, @NonNull byte[] inputData, CipherExtParams params);

    /**
     * Random participating Mac computing
     *
     * @param keyManagement   Key management system
     * @param macAlgorithm    MAC algorithm{@link MacAlgorithm}
     * @param keyIndex        if the keyIndex is a working key index,it isn't need to set the workingKeyData in calMacExtParams<p>
     *                        if the keyIndex is a master key index, it is need to set the workingKeyData in calMacExtParams<p>
     * @param inputData       the input data
     * @param calMacExtParams the external params to calculate mac, e.g. the working key data.
     * @return mac result{@link MacResult}
     * @since ver3.10.01
     */
    public MacResult calcMac(KeyManagement keyManagement, int macAlgorithm, @IntRange(from = 1, to = 200) int keyIndex, @NonNull byte[] inputData, CalMacExtParams calMacExtParams);

    /**
     * KSN increase by 1 on dukpt mode
     *
     * @param dukptKeyIndex (1-200)dukpt key index
     * @return true if success,false if error.
     * @since ver3.10.01
     */
    public boolean ksnIncrease(@IntRange(from = 1, to = 200) int dukptKeyIndex);

    /**
     * Increase dukpt aes ksn
     * @param dukptKeyIndex (1-200)dukpt key index
     * @return true if success,false if error.
     */
    public boolean ksnAESIncrease(@IntRange(from = 1, to = 200) int dukptKeyIndex);


    /**
     * Get dukptKsn
     *
     * @param dukptKeyIndex (1-200)dukpt Key Index
     * @return ksn data
     * @since ver3.10.01
     */
    public byte[] getDukptKsn(@IntRange(from = 1, to = 200) int dukptKeyIndex);

    /**
     * Get dukpt aes ksn
     * @param dukptKeyIndex (1-200)dukpt key index
     * @return ksn data
     */
    public byte[] getDukptAESKsn(@IntRange(from = 1, to = 200) int dukptKeyIndex);

    /**
     * Check whether the secret key is loaded.
     *
     * @param keyType       key type
     * @param algorithmMode the algorithm mode
     * @param keyIndex      key index
     * @param checkValue   Key Check Value (Left 4 digits of the result of encrypting zeros with the key).
     * @return true if success,false if error.
     * @since ver3.10.01
     */
    public boolean checkKeyIsExist(KeyType keyType, AlgorithmMode algorithmMode, @IntRange(from = 1, to = 200) int keyIndex, @Nullable byte[] checkValue);

    /**
     * Delete a master key or a working key
     *
     * @param keyType       the key type to be deleted
     * @param algorithmMode the algorithm mode of the key
     * @param keyIndex      (1-200)the index of the key to be deleted
     * @return true if success,false if error.
     * @since ver3.10.01
     */
    public boolean deleteKey(KeyType keyType, AlgorithmMode algorithmMode, @IntRange(from = 1, to = 200) int keyIndex);

    /**
     * Delete all keys loaded in the terminal.
     *
     * @return true if success,false if error.
     * @since ver3.10.01
     * todo
     */
    public boolean deleteAllKeys();

    /**
     * Load Random keyboard（dedicated for smart pos）
     *
     * @param keyboardRandom Keyboard object (three layout types)
     * @return Random layout, digit appointed layout and returned keyboard key values <p>
     * (For a whole keyboard appointed layout, return the whole keyboard key values, figure key（0x30~0x39）,cancel key（0x1B）,backspace key（0x0A），confirm key（0x0D），# key（0x1C） and star key（0x2E））
     * @since ver3.10.01
     */
    public byte[] loadRandomKeyboard(KeyboardRandom keyboardRandom);


    /**
     * Initialize the touch screen keyboard.
     * @param keyNum the number of key
     * @param keyInfo key information(Value of key-press，Button area）
     * @param touchCoordinates touch screen area
     * @param KeyboradCoordinates keypad area
     * @return
     */
    public boolean loadRNIBKeyboard(int keyNum, Map<PinPadButton, int[]> pinPadButtons, int[] touchCoordinates, int[] KeyboradCoordinates);

    /**
     * Get the Terminal serial number and the Terminal serial number ciphertext
     *
     * @param random Random factors:  use the last 6 digits of card number--Bank card transaction<p>
     *               use the last 6 digits of C2B code -- Scan the code to pay
     * @return TusnData the tusn data{@link TusnData}
     * @since ver3.10.01
     */
    public TusnData getTusnData(String random);

    /**
     * Open a pin input process<p>
     * This method adopts asynchronous execution and returns the invocation result via a listener. <p>
     *
     * @param keyManagement     the key management system
     * @param algorithmMode     the the algorithm mode
     * @param keyIndex          if the keyIndex is a working key index,it isn't need to set the workingKeyData in {@link PinInputExtParams}<p>
     *                          if the keyIndex is a master key index, it is need to set the workingKeyData in {@link PinInputExtParams}<p>
     * @param pan               card number
     * @param timeout           Timeout(second)
     * @param pinInputListener  pin input event listener
     * @param pinInputExtParams the external params in pin input.<p>{@link PinInputExtParams}
     * @since ver3.10.01
     */
    public void startPinInput(KeyManagement keyManagement, AlgorithmMode algorithmMode, @IntRange(from = 1, to = 200) int keyIndex, String pan, @IntRange(from = 1, to = 255) int timeout, @NonNull PinInputListener pinInputListener, PinInputExtParams pinInputExtParams);

    /**
     * Open a offline plain/cipher pin input process<p>
     * This method adopts asynchronous execution and returns the invocation result via a listener. <p>
     * If the device has not an LCD, the interface supports the multiple returns of the current pin entry state. Refer to{@link PinInputEvent}
     *
     * @param timeout           Timeout(second) (1-255)
     * @param modulus           modulus  (offline CipherPin)
     * @param exponent          exponent (offline CipherPin)
     * @param pinInputListener  pin input event listener
     * @param pinInputExtParams external params in pin input.<p>{@link PinInputExtParams}
     * @since ver3.10.01
     */
    public void startOfflinePinInput(@IntRange(from = 1, to = 255) int timeout, byte[] modulus, byte[] exponent, @NonNull PinInputListener pinInputListener, PinInputExtParams pinInputExtParams);

    /**
     * Cancel the last pin input
     *
     * @since ver3.10.01
     */
    public void cancelPinInput();

    /**
     * get key kcv. Only for MKSK key system.
     * @param keyType the key type
     * @param algorithmMode the algorithm mode of the key
     * @param keyIndex (1-200)the index of the key
     * @return the kcv of kcv(3 bytes).
     */
    byte[] getKeyKcv(KeyType keyType, AlgorithmMode algorithmMode, int keyIndex);
}
