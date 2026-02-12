package com.newland.nsdk.core.external.command.communication;

import com.newland.nsdk.core.api.common.exception.NSDKException;

public interface CommunicatorExtension {
    void sendInterrupt(byte[] data, int timeout) throws NSDKException;
}
