package com.newland.nsdk.core.api.external;

import android.content.Context;

import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.crypto.AlgorithmParameters;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.keymanager.CipherMode;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.common.uart3.UART3Config;
import com.newland.nsdk.core.api.common.uart3.UART3Type;
import com.newland.nsdk.core.api.common.utils.LogLevel;
import com.newland.nsdk.core.api.external.communication.CommunicatorListener;
import com.newland.nsdk.core.api.external.communication.ExternalCommunicator;
import com.newland.nsdk.core.api.external.communication.ExternalCommunicatorType;
import com.newland.nsdk.core.api.external.communication.NSDKCommunicator;

/**
 * <b>(v2.3.0-alpha3)</b> Main entrance of NSDK(Newland Software Development Kit) external modules. Provides the ability to get all the external device modules.
 *
 * <b><p>Note: If using bluetooth to connect external PIN pad device, pair it in the system settings on the android device first.</p></b>
 *
 * <p>Follow below steps to operate external device:</p>
 * <pre>
 *     // 1. Get the instance of the module manager.
 *     ExtNSDKModuleManager moduleManager = ExtNSDKModuleManagerImpl.getInstance();
 *
 *     // 2. Get a default communicator provided by NSDK.
 *     CommunicatorListener communicatorListener = new CommunicatorListener() {
 *        {@code @Override}
 *         public BluetoothDevice onBluetoothList(ArrayList<BluetoothDevice> arrayList) {
 *             // Choose expected bluetooth device to return.
 *         }
 *
 *        {@code @Override}
 *         public void onConnectedStateChange(ExternalCommunicatorState externalCommunicatorState) {
 *             // Do something when state changes.
 *         }
 *     };
 *     try {
 *         communicator = moduleManager.getNSDKCommunicator(context, ExternalCommunicatorType.BLUETOOTH_CLASSIC, communicatorListener);
 *     } catch (NSDKException e) {
 *         e.printStackTrace();
 *     }
 *
 *     // 2. Or new a customized communicator which implements 'ExternalCommunicator' and set it to NSDK.
 *     // CustomizedCommunicator communicator = new CustomizedCommunicator();
 *     // moduleManager.setCommunicator(communicator)
 *
 *     // Set UART config if needed.
 *     // UART3Config config = new UART3Config(BaudRate.BPS115200, DataBits.DATA_BIT_8, ParityBit.NO_CHECK, StopBits.STOP_BIT_ONE);
 *     // moduleManager.setUART3Config(UART3Type.PINPAD_A7, config);
 *
 *     // 3. Open external device.
 *     try {
 *         int timeout = 2000;
 *         communicator.open(timeout);
 *     } catch (NSDKException e) {
 *         e.printStackTrace();
 *     }
 *
 *     // 4. Init external modules after the device is opened successfully.
 *     try{
 *         moduleManager.initExternalModules();
 *     } catch(NSDKException e) {
 *         // Handle the exception.
 *     }
 *
 *     // 5. Get the specified device module.
 *     ExtBeeper beeper = (ExtBeeper) moduleManager.getModule("EXT_BEEPER");
 *
 *     // 6. Invoke methods of the beeper module.
 *     try {
 *        beeper.beep(BeeperTone.SUCCESS, 1000);
 *     } catch (NSDKException e) {
 *         // Handle the exception.
 *     }
 *
 *     // 7. Close external device.
 *     try {
 *         communicator.close();
 *     } catch (NSDKException e) {
 *         e.printStackTrace();
 *     }
 *
 *     // 8. Destroy device modules.
 *     moduleManager.destroy();
 * </pre>
 */
public interface ExtNSDKModuleManager {
    /**
     * Initializes external device modules.
     *
     * <p>This shall be called after external device is opened and before getting any external device modules.</p>
     *
     * @throws NSDKException
     */
    void initExternalModules() throws NSDKException;

    /**
     * Gets the device module according to the module name.
     *
     * @param moduleName <b>[Required]</b> The name of the device module. See {@link ModuleType}.
     * @return The required device module.
     */
    Module getModule(String moduleName);

    /**
     * Releases device resources.
     */
    void destroy();

    /**
     * Gets default communicator which is implemented by NSDK to establish communication channel and handle data exchange with external device.
     *
     * @param context        <b>[Required]</b> The context should not be null.
     * @param type           <b>[Required]</b> Communication type of external device. See {@link ExternalCommunicatorType}.
     * @param listener       <b>[Required]</b> Listens to device list and returns selected device when the communication type is {@link ExternalCommunicatorType#BLUETOOTH_CLASSIC} or {@link ExternalCommunicatorType#BLUETOOTH_LOW_ENERGY}.
     * @return NSDK default communicator to establish and manage communication channel. See{@link ExternalCommunicator}.
     * @throws NSDKException
     */
    NSDKCommunicator getNSDKCommunicator(Context context, ExternalCommunicatorType type, CommunicatorListener listener) throws NSDKException;

    /**
     * Sets UART3Port config, should be called before opening external device.
     *
     * <p>Note: This works when current communicator type is {@link ExternalCommunicatorType#UART3PORT}.</p>
     *
     * @param type   <b>[Required]</b> UART port type, see {@link UART3Type}.
     * @param config <b>[Required]</b> UART config, see {@link UART3Config}.
     */
    void setUART3Config(UART3Type type, UART3Config config) throws NSDKException;

    /**
     * Sets debug level.
     *
     * @param level <b>[Required]</b> Debug level, see {@link LogLevel}. Default log level is {@link LogLevel#OFF}.
     */
    void setDebugMode(LogLevel level);

    /**
     * Sets customized communicator to NSDK for data exchange with external device.
     *
     * @param communicator <b>[Required]</b> Customized communicator.
     */
    void setCommunicator(ExternalCommunicator communicator);

    /**
     * Sets timeout for sending/receiving data.
     *
     * <p>Note: The timeout shall >= 0.</p>
     *
     * @param sendTimeout    <b>[Required]</b> Timeout for sending data. Default value is 10000ms. Unit: ms.
     * @param receiveTimeout <b>[Required]</b> Timeout for receiving data. Default value is 10000ms. Unit: ms.
     */
    void setCommunicationTimeout(int sendTimeout, int receiveTimeout) throws NSDKException;

    /**
     * Tests the communication channel.
     *
     * @return Result. true: the channel is OK, false: the channel is unable to send/write data.
     * @throws NSDKException
     */
    boolean ping();

    /**
     * Set the pinpad default encrypt algorithm.
     *
     * @param key                 <b>[Required]</b> Key used to encrypt
     *                            <ul>
     *                               <li>Key type</li>
     *                               <li>Key usage</li>
     *                            </ul>
     * @param params              <b>[Required]</b> Algorithm parameters, see {@link AlgorithmParameters}.
     *                            <ul>
     *                               <li>IV is required when cipher mode is {@link CipherMode#CBC}</li>
     *                            </ul>
     * @throws NSDKException
     */
    void setCryptoMode(SymmetricKey key, AlgorithmParameters params) throws NSDKException;

    /**
     * Get external error message bt its errorCode
     * @param errCode <b>[Require]</b> ErrorCode system thrown
     * @return External error Message according to errCode
     */
    String getErrMsg(int errCode);
}