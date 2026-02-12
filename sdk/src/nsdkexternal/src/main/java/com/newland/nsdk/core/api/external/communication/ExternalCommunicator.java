package com.newland.nsdk.core.api.external.communication;

import com.newland.nsdk.core.api.common.exception.NSDKException;

/**
 * Provides the ability to exchange data with the external device after the communication channel is established.
 */
public interface ExternalCommunicator {
    /**
     * Sends data to external device.
     *
     * @param data    <b>[Required]</b> Data to send.
     * @param timeout <b>[Required]</b> Timeout for sending data. Unit: ms
     * @throws NSDKException
     */
    void send(byte[] data, int timeout) throws NSDKException;

    /**
     * Receives data from external device.
     *
     * @param timeout <b>[Required]</b> Timeout for receiving data. Unit: ms
     * @return Received data.
     * @throws NSDKException
     */
    byte[] receive(int timeout) throws NSDKException;

    /**
     * Sets timeout for sending/receiving data.
     *
     * @param sendTimeout    <b>[Required]</b> Timeout for sending data. Default value is 60000ms. Unit: ms.
     * @param receiveTimeout <b>[Required]</b> Timeout for receiving data. Default value is 60000ms. Unit: ms.
     * @deprecated This method has been moved to {@link com.newland.nsdk.core.api.external.ExtNSDKModuleManager#setCommunicationTimeout(int, int)}
     */
    @Deprecated
    void setCommunicationTimeout(int sendTimeout, int receiveTimeout);

    /**
     * Clear serial buffer
     *
     * @throws NSDKException
     */
    void clear() throws NSDKException;

}
