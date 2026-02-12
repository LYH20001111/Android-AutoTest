package com.newland.sdk.module.externalPin;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.newland.sdk.module.pin.CipherMode;
import com.newland.sdk.module.pin.CipherResult;
import com.newland.sdk.module.pin.KeyManagement;
import com.newland.sdk.module.pin.KeyType;
import com.newland.sdk.module.pin.LoadDuktpExtParams;
import com.newland.sdk.module.pin.LoadMKExtParams;
import com.newland.sdk.module.pin.LoadWKExtParams;
import com.newland.sdk.module.pin.TusnData;
import com.newland.sdk.mtype.Module;
import com.newland.sdk.module.pin.AlgorithmMode;
import com.newland.sdk.module.pin.CipherExtParams;
import com.newland.sdk.module.pin.LoadKeyMode;
import com.newland.sdk.module.pin.MacAlgorithm;
import com.newland.sdk.module.pin.MacResult;
import com.newland.sdk.module.pin.PinInputListener;
import com.newland.sdk.module.pin.WorkingKeyType;
import com.newland.sdk.module.swiper.MSDAlgorithmType;

import java.util.List;

/**
 * @description: External pin keyboard function.
 * @author: Lindan
 * @create: 2019/7/29
 */
public interface ExtPinpadModule extends Module {

    /**
     * Initialize the pinpad port
     *
     * @param params Pinpad initialization extension parameters.
     * @since 3.10.01
     */
    public boolean init(@NonNull PinpadInitExtParams params);

    /**
     * Load a master key
     *
     * @param mkMode          master key type {@link LoadKeyMode}
     * @param algorithmMode   Algorithm mode {@link AlgorithmMode}
     * @param masterIndex     Master key index[0-167]
     * @param inputData       Master key data
     * @param checkValue      Kcv, plain master key encrypt by 8 bytes 0x00.
     * @param loadMKExtParams the external params used to load master key</p>{@link LoadMKExtParams}
     * @return
     * @since 3.10.01
     */
    public boolean loadMasterKey(LoadKeyMode mkMode, @Nullable AlgorithmMode algorithmMode, int masterIndex, byte[] inputData, @Nullable byte[] checkValue, LoadMKExtParams loadMKExtParams);

    /**
     * Load a working key
     *
     * @param wkType          Working key type {@link WorkingKeyType}
     * @param algorithmMode   Algorithm mode {@link AlgorithmMode}
     * @param masterKeyIndex  Master key index
     * @param workingKeyIndex Working key index
     * @param inputData       Working key type
     * @param checkValue      Kcv, plain working key encrypt by 8 bytes 0x00.
     * @return It`s 4 bytes 0x00 when it`s successful.
     * @since 3.10.01
     */
    public boolean loadWorkingKey(WorkingKeyType wkType, AlgorithmMode algorithmMode, int masterKeyIndex, int workingKeyIndex, byte[] inputData, byte[] checkValue);
    /**
     * Load a working key
     *
     * @param wkType          Working key type {@link WorkingKeyType}
     * @param algorithmMode   Algorithm mode {@link AlgorithmMode}
     * @param masterKeyIndex  Master key index
     * @param workingKeyIndex Working key index
     * @param inputData       Working key type
     * @param checkValue      Kcv, plain working key encrypt by 8 bytes 0x00.
     * @param loadWKExtParams the external params used to load working key</p>{@link LoadWKExtParams}
     * @return It`s 4 bytes 0x00 when it`s successful.
     * @since 3.10.59_15
     */
    public boolean loadWorkingKey(WorkingKeyType wkType, AlgorithmMode algorithmMode, int masterKeyIndex, int workingKeyIndex, byte[] inputData, byte[] checkValue, LoadWKExtParams loadWKExtParams);

    /**
     * Open a PIN entry process for external pinpad
     *
     * @param keyManagement    Key management type {@link KeyManagement}
     * @param algorithmMode    Algorithm mode {@link AlgorithmMode}
     * @param masterKeyIndex   Master key index
     * @param workingKeyIndex  Working key index
     * @param timeout          TimeOut (Time Units:s)
     * @param pinInputListener Input pin listener{@link PinInputListener}
     * @param pinpadExtParams  Extra input pin parameters {@link PinpadExtParams}, it can be null.
     * @since 3.10.01
     */
    public void startExternalPinInput(KeyManagement keyManagement, AlgorithmMode algorithmMode, int masterKeyIndex, int workingKeyIndex, String pan, int timeout, PinInputListener pinInputListener, @Nullable PinpadExtParams pinpadExtParams);

    /**
     * Open an offline pin input process.
     *
     * @param keyIndex         master key index(sp100 device and pos device should have loaded the same master key)
     * @param algorithmMode    algorithm mode{@link AlgorithmMode}
     * @param timeout          TimeOut (Time Units:s)
     * @param modulus          modulus  (offline CipherPin)
     * @param exponent         exponent (offline CipherPin)
     * @param pinInputListener Input pin listener{@link PinInputListener}
     * @param pinpadExtParams  Extra input pin parameters {@link PinpadExtParams}, it can be null.
     * @since 3.10.01
     */
    public void startOfflinePinInput(int keyIndex, @NonNull AlgorithmMode algorithmMode, int timeout, byte[] modulus, byte[] exponent, PinInputListener pinInputListener, @Nullable PinpadExtParams pinpadExtParams);

    /**
     * fetch the plain input data
     * (The overseas of sp100 support since V3.10.52)
     *
     * @param timeout          TimeOut (Time Units:s)
     * @param pinInputListener Input pin listener{@link PinInputListener}
     * @param pinpadExtParams  Extra input pin parameters {@link PinpadExtParams}, it can be null.
     */
    public void startPlainPinInput(int timeout, PinInputListener pinInputListener, @Nullable PinpadExtParams pinpadExtParams);

    /**
     * Cancel the previous pin input
     *
     * @since 3.10.01
     */
    @Deprecated
    public void cancelPinInput();

    /**
     * Calculate mac
     *
     * @param keyManagement   Key management type{@link KeyManagement}
     * @param macAlgorithm    Mac algorithm {@link MacAlgorithm}
     * @param masterKeyIndex  Master key index
     * @param workingKeyIndex Working key index
     * @param inputData       The data for mac calculation
     * @param calMacExtParams Extra calculation mac parameters,{@link MacExtParams} it can be null.
     * @return {@link MacResult} Include mac and ksn
     * @since 3.10.01
     */
    public MacResult calcMac(KeyManagement keyManagement, int macAlgorithm, int masterKeyIndex, int workingKeyIndex, byte[] inputData, @Nullable MacExtParams calMacExtParams);


    /**
     * Encrypt data
     *
     * @param keyManagement   Key management type{@link KeyManagement}
     * @param algorithmMode   Algorithm mode {@link AlgorithmMode}
     * @param masterKeyIndex  Master key index
     * @param workingKeyIndex Working key index
     * @param inputData       The date for encrypted
     * @param params          Encryption or encryption extra parameters.
     * @return {@link CipherResult} Include encryption result, it can be null.
     * @since 3.10.01
     */
    public CipherResult encrypt(KeyManagement keyManagement, AlgorithmMode algorithmMode, CipherMode cipherMode, int masterKeyIndex, int workingKeyIndex, byte[] inputData, @Nullable CipherExtParams params);

    /**
     * Decrypt data
     *
     * @param keyManagement   Key management type{@link KeyManagement}
     * @param algorithmMode   Algorithm mode {@link AlgorithmMode}
     * @param masterKeyIndex  Master key index
     * @param workingKeyIndex Working key index
     * @param inputData       The date for decrypted
     * @param params          Encryption or encryption extra parameters{@link CipherExtParams}, it can be null.
     * @return {@link CipherResult} Include encryption result
     * @since 3.10.01
     */
    public CipherResult decrypt(KeyManagement keyManagement, AlgorithmMode algorithmMode, CipherMode cipherMode, int masterKeyIndex, int workingKeyIndex, byte[] inputData, @Nullable CipherExtParams params);


    /**
     * Calculate track data
     *
     * @param keyManagement    Key management type{@link KeyManagement}
     * @param msdAlgorithmType MSD algorithm mode {@link MSDAlgorithmType}
     * @param masterKeyIndex   Master key index
     * @param workingKeyIndex  Working key index
     * @param inputData        Encryption or encryption extra parameters.
     * @return
     * @since 3.10.01
     */
    public byte[] calculateTrackData(KeyManagement keyManagement, MSDAlgorithmType msdAlgorithmType, int masterKeyIndex, int workingKeyIndex, byte[] inputData);


    /**
     * LCD display of external pinpad
     *
     * @param inputData <p>The msg to display. They are shown on line 1 、 line 2、line3、and line 4.</p>
     *                  <p>line 1 msg is empty when the first data is null</p>
     *                  <p>Refer to ASCII code table for characters, numbers and English(0x20 <= ASCII code< 0x80)</p>
     *                  <p>Refer to following code table for Chinese text:</p>
     *                  80  请	81  输	82  入	83  密	84  码	85  余	86  额	87  元	88  您	89  的   <p>
     *                  8A  再	8B  金	8C  银	8D  联	8E  广	8F  东	90  深	91  圳	92  欢	93  迎   <p>
     *                  94  光	95  临	96  天	97  虹	98  商	99  场	9A  阳	9B  江	9C  农	9D  信   <p>
     *                  9E  主	9F  钥	A0  号	A1  错	A2  手	A3  机	A4  确	A5  认	A6  帐	A7  单   <p>
     *                  A8  小	A9  灵	AA  通	AB  固	AC  话	AD  电	AE  可	AF  用	B0  户	B1  总   <p>
     *                  B2  积	B3  分	B4  续	B5  费	B6  有	B7  效	B8  币	B9  专	BA  消	BB  预   <p>
     *                  BC  约  BD 挥    BE 卡
     * @return
     * @since 3.10.01
     */
    public boolean showMessage(List<byte[]> inputData);

    /**
     * LCD display of external pinpad
     *
     * @param inputData            <p>The msg to display. They are shown on line 1 and line 2.</p>
     *                             <p>Line 1 msg is empty when the first data is null</p>
     * @param messageExtParams <p>Reserved</p>
     * @return
     * @since sp100 version: >=V4.00.03/ ME51 version: >=V05.00.03
     */
    public boolean showMessage(List<String> inputData, MessageExtParams messageExtParams);

    /**
     * Clear screen of external pinpad
     *
     * @return since 3.10.01
     */
    public boolean clearScreen();

    /**
     * Get the specified pinpad information by key.
     *
     * @param key "VERSION","SN","BAUDRATE","PORTTYPE","PN"
     * @return
     */
    public String getInfo(String key);

    /**
     * @param index    RSA key index
     * @param keyLen   RSA key data length
     * @param module   module data
     * @param exponent exponent data
     * @return
     */
    public boolean loadRSA(int index, int keyLen, byte[] module, byte[] exponent);

    /**
     * @param index RSA key index
     * @param data  data to encry/decry
     * @return
     */
    public byte[] rsaEncryDecry(int index, byte[] data);

    /**
     * Check whether the secret key is loaded or not.
     *
     * @param keyType       key type
     * @param algorithmMode the algorithm mode
     * @param keyIndex      key index
     * @param checkValue    Key Check Value (Left 4 digits of the result of encrypting zeros with the key).
     * @return true if success,false if error.
     * @since sp100 version: 3.2.19
     */
    public boolean checkKeyIsExist(KeyType keyType, @NonNull AlgorithmMode algorithmMode, @IntRange(from = 1, to = 250) int keyIndex, @Nullable byte[] checkValue);

    /**
     * Load the DUKPT with KSN and IPEK
     *
     * @param loadKeyMode        the mode of loading DUKPT.(the default kek of Chinese sp100 is 24 bytes 0x38 if loadKeyMode is {@link LoadKeyMode#DEFAULT_ENCRYPT}){@link LoadKeyMode}
     * @param ipekIndex          IPEK index (1-250)
     * @param ksn                fixed 10 bytes KSN, plaintext data
     * @param ipek               the initial IPEK Data
     * @param loadDuktpExtParams The params used in load dukpt.
     * @return true if success,false if error.
     * @since sp100 version 3.2.19
     */
    public boolean loadIPEK(LoadKeyMode loadKeyMode, @IntRange(from = 1, to = 200) int ipekIndex, @NonNull byte[] ksn, @NonNull byte[] ipek, LoadDuktpExtParams loadDuktpExtParams);

    /**
     * KSN increase by 1 on dukpt mode
     *
     * @param dukptKeyIndex (1-250)dukpt key index
     * @return true if success,false if error.
     * @since sp100 version 3.2.19
     */
    public boolean ksnIncrease(@IntRange(from = 1, to = 250) int dukptKeyIndex);

    /**
     * Get dukptKsn
     *
     * @param dukptKeyIndex (1-250)dukpt Key Index
     * @return ksn data
     * @since ver3.2.19
     */
    public byte[] getDukptKsn(@IntRange(from = 1, to = 250) int dukptKeyIndex);

    /**
     * Delete a master key or a working key
     *
     * @param keyType       the key type to be deleted，unsupport{@link KeyType#TRANSPORT_KEY} for now.
     * @param algorithmMode the algorithm mode of the key, only support {@link AlgorithmMode#DES} for now.
     * @param keyIndex      (1-250)the index of the key to be deleted.
     * @return true if success,false if error.
     * @since sp100 version 3.2.19
     */
    public boolean deleteKey(KeyType keyType, AlgorithmMode algorithmMode, @IntRange(from = 1, to = 250) int keyIndex);

    /**
     * Delete all keys loaded in the sp100.
     *
     * @return true if success,false if error.
     * @since sp100 version 3.2.19
     * todo
     */
    public boolean deleteAllKeys();

    /**
     * return to the main screen in the sp100.
     *
     * @since sp100 version 3.10.15
     */
    public void backToMainScreen();

    /**
     * Delete DUKPT
     *
     * @param algorithmMode the algorithm mode of the key, only support {@link AlgorithmMode#DES} for now.
     * @param keyIndex      (1-250)the index of the key to be deleted.
     * @return true if success,false if error.
     * @since sp100 unsupport
     */
    public boolean deleteDukpt(AlgorithmMode algorithmMode, @IntRange(from = 1, to = 250) int keyIndex);

    /**
     * Get the ME51 Terminal serial number and the Terminal serial number ciphertext
     *
     * @param random Random factors:  use the last 6 digits of card number--Bank card transaction<p>
     *               use the last 6 digits of C2B code -- Scan the code to pay
     * @return TusnData the tusn data{@link TusnData}
     * @since ver3.10.46
     */
    public TusnData getTusnData(String random);

    /**
     * set property
     * @param propertyKey refer to {@link PropertyKey}
     * @param value 0 or 1, "1" means enable "0" means disable
     * @return
     */
    public boolean setProperty(PropertyKey propertyKey,String value);


    /**
     * Set display direction
     * @param displayDirection {@link DisplayColorImageParams}
     * @return result
     */
    public boolean setDisplayDirection(DisplayDirection displayDirection);


    /**
     *
     * @param files update files data, like application or firmware
     * @param listener update result
     */
    public void update(UpdateFiles files, UpdateListener listener);

    /**
     * display color image, like logo
     * @param imageData image data
     * @param parameter like image width, height.
     *                  actual image width and actual image height.
     *                  For P180: maximum size is 320 * 450
     *                  For SP130: maximum size is 320 * 240
     *                  If the parameters size is equal to the maximum size, the logo will be replaced,
     *                  otherwise it will display the image.
     * @return
     */
    public boolean displayColorImage(byte[] imageData, DisplayColorImageParams parameter);


    /**
     * After setting the Page-jump Mode, all return-main-interface movement will be disabled except “Get Button Option” Command and “Return Main Menu” Command. Besides, you still can press “Cancel” button to return to main interface.
     * After setting the Page-jump Mode, all return-main-interface movement will be disabled except “Get Button Option” Command and “Return Main Menu” Command. Besides, you still can press “Cancel” button to return to main interface.
     * @param isJump whether to jump to main screen
     * @return
     */
    public boolean controlPageJump(boolean isJump);

    /**
     * get key kcv
     * @param keyType the key type
     * @param algorithmMode the algorithm mode of the key
     * @param keyIndex (1-250)the index of the key
     * @return the kcv of kcv(3 bytes).
     */
    public byte[] getKeyKcv(KeyType keyType, AlgorithmMode algorithmMode, int keyIndex);

    /**
     * After this command is finished (chose an option), it won’t return to main page, you need to
     * manually use Return Main homepage {@link ExtPinpadModule#backToMainScreen()} method to return. This behaviour is designed for
     * continuous menu option.
     *
     * @param title    menu title
     * @param menu     menu, the max menu option size is 12.
     * @param timeout  (second)
     * @param listener {@link MenuOptionListener}
     * @param params   {@link MenuOptionParams}
     */
    public void showMenuOption(@Nullable String title, String[] menu, int timeout, MenuOptionListener listener, @Nullable MenuOptionParams params);

    /**
     * Scanning, only support single scan.
     * @param timeout 1s interval, big endian.(>1)
     * @param listener {@link  ScannerListener}
     * @param params {@link ScanParams}
     */
    public void scan(int timeout, ScannerListener listener, @Nullable ScanParams params);

    /**
     * Cancel the previous method.
     * like {@link ExtPinpadModule#startExternalPinInput,ExtPinpadModule#startOfflinePinInput,ExtPinpadModule#startPlainPinInput,ExtPinpadModule#scan}
     */
    public void cancel();

    /**
     * Set font size
     * @param size {@link  FontSize}
     * @return
     */
    public boolean setFontSize(FontSize size);
}
