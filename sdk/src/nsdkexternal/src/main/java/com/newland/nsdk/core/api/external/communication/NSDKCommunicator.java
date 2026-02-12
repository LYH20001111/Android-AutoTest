package com.newland.nsdk.core.api.external.communication;

import com.newland.nsdk.core.api.common.exception.NSDKException;

/**
 * Default communicator provided by NSDK which used to establish communication channel.
 *
 * <p>To get an ExternalCommunicator:</p>
 * <pre>
 *     NSDKCommunicator communicatorListener = new CommunicatorListener() {
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
 *     NSDKCommunicator communicator = ExtNSDKModuleManagerImpl.getInstance()
 *                                             .getNSDKCommunicator(context, ExternalCommunicatorType.USB, communicatorListener);
 * </pre>
 */
public interface NSDKCommunicator extends ExternalCommunicator {
    /**
     * Opens communication channel. It shall be called before data exchange with external device.
     *
     * <p>Note: To check if the external device is opened, call {@link #isConnected()}.</p>
     *
     * @param timeout <b>[Required]</b> Timeout for opening. Unit: ms. Only used for bluetooth communicator now.
     * @throws NSDKException
     */
    void open(int timeout) throws NSDKException;

    /**
     * Closes communication channel. It shall be called when data exchange finished.
     *
     * <p>Note: To check if the external device is closed, call {@link #isConnected()}.</p>
     *
     * @param timeout <b>[Required]</b> Timeout for closing. Unit: ms.
     * @throws NSDKException
     */
    void close(int timeout) throws NSDKException;

    /**
     * Checks if the communication channel is connected.
     *
     * @return Communication channel state.
     * <ul>
     *     <li>"true": The communication channel is connected.</li>
     *     <li>"false": The communication channel is disconnected.</li>
     * </ul>
     * @throws NSDKException
     */
    boolean isConnected();

}
