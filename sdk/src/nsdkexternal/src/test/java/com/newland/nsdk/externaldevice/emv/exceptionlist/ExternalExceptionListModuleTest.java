package com.newland.nsdk.externaldevice.emv.exceptionlist;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.external.communication.ExternalCommunicator;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.communication.mock.MockExternalCommunicator;
import com.newland.nsdk.core.external.command.emv.exceptionlist.ExternalExceptionListModule;

import org.junit.Before;
import org.junit.Test;

public class ExternalExceptionListModuleTest {

    private ExternalExceptionListModule externalExceptionListModule = new ExternalExceptionListModule();

    @Before
    public void init(){
        ExternalCommunicator communicator = new MockExternalCommunicator();
        ExternalCommunicationManager.getInstance().setCommunicator(communicator);
    }

    @Test
    public void updateExceptionList() {
        try {
//            externalExceptionListModule.updateExceptionList(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A}, (byte) 0x33);
            externalExceptionListModule.updateExceptionList(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07}, (byte) 0x33);
//            externalExceptionListModule.updateExceptionList(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x11}, (byte) 0x33);
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkExceptionList() {
        try {
            boolean result = externalExceptionListModule.checkExceptionList(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A}, (byte) 0x23);
//            boolean result = externalExceptionListModule.getExceptionList(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07}, (byte) 0x23);
//            boolean result = externalExceptionListModule.getExceptionList(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x11}, (byte) 0x23);
            System.out.println("Found exception list: " + result);
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void remove() {
        try {
            externalExceptionListModule.removeExceptionList(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A}, (byte) 0x23);
//            externalExceptionListModule.remove(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07}, (byte) 0x23);
//            externalExceptionListModule.remove(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x11}, (byte) 0x23);
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void removeAll() {
        try {
            externalExceptionListModule.removeAllExceptionList();
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }
}