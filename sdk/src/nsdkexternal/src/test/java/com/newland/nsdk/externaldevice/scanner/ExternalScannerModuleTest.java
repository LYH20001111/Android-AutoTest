package com.newland.nsdk.externaldevice.scanner;


import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.external.communication.ExternalCommunicator;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.communication.mock.MockExternalCommunicator;
import com.newland.nsdk.core.external.command.scanner.ExternalScannerModule;

import org.junit.Before;
import org.junit.Test;

public class ExternalScannerModuleTest {
    private ExternalScannerModule scannerModule = new ExternalScannerModule();

    @Before
    public void init(){
        ExternalCommunicator communicator = new MockExternalCommunicator();
        ExternalCommunicationManager.getInstance().setCommunicator(communicator);
    }

    @Test
    public void scan() {
        try {
            byte[] data = scannerModule.scan(100);
            if (data != null) {
                System.out.println("Data: " + ISOUtils.hexString(data));
            }
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }
}