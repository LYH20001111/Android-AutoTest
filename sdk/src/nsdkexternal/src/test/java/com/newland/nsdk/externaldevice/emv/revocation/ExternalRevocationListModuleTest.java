package com.newland.nsdk.externaldevice.emv.revocation;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.external.communication.ExternalCommunicator;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.communication.mock.MockExternalCommunicator;
import com.newland.nsdk.core.external.command.emv.revocationlist.ExternalRevocationListModule;

import org.junit.Before;
import org.junit.Test;

public class ExternalRevocationListModuleTest {
    private ExternalRevocationListModule revocationListModule = new ExternalRevocationListModule();

    @Before
    public void init(){
        ExternalCommunicator communicator = new MockExternalCommunicator();
        ExternalCommunicationManager.getInstance().setCommunicator(communicator);
    }

    @Test
    public void updateRevocationList() {
        try {
            revocationListModule.updateRevocationList(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05}, (byte) 0x23, new byte[]{0x11, 0x22, 0x33});
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkRevocationList() {
        try {
            boolean result = revocationListModule.checkRevocationList(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05}, (byte) 0x23, new byte[]{0x11, 0x22, 0x33});
            System.out.println("Found revocation list: " + result);
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void remove() {
        try {
            revocationListModule.removeRevocationList(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05}, (byte) 0x23, new byte[]{0x11, 0x22, 0x33});
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void removeAll() {
        try {
            revocationListModule.removeAllRevocationList();
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }
}