package com.newland.nsdk.externaldevice.updater;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.external.communication.ExternalCommunicator;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.communication.mock.MockExternalCommunicator;
import com.newland.nsdk.core.external.command.updater.ExternalUpdaterModule;

import org.junit.Before;
import org.junit.Test;

public class ExternalUpdaterModuleTest {
    private ExternalUpdaterModule updaterModule = new ExternalUpdaterModule();

    @Before
    public void init(){
        ExternalCommunicator communicator = new MockExternalCommunicator();
        ExternalCommunicationManager.getInstance().setCommunicator(communicator);
    }

    @Test
    public void createFile() {
        try {
            updaterModule.createFile("app name".getBytes());
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadApp() {
        try {
            updaterModule.loadApp("app name".getBytes());
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateApp() {
        try {
            updaterModule.updateApp("app name".getBytes(), true);
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }
}