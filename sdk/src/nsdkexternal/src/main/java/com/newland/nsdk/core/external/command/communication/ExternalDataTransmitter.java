package com.newland.nsdk.core.external.command.communication;


import com.newland.nsdk.core.api.common.exception.NSDKException;

public interface ExternalDataTransmitter {
    /**
     * @param data
     * @param timeout Unit: ms
     * @throws NSDKException
     */
    void send(byte[] data, int timeout) throws NSDKException;

    /**
     * @param timeout Unit: ms
     * @return
     * @throws NSDKException
     */
    byte[] receive(int timeout) throws NSDKException;
}
