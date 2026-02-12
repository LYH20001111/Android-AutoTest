package com.newland.nsdk.externaldevice.common;


import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.led.LEDColor;
import com.newland.nsdk.core.api.external.communication.ExternalCommunicator;
import com.newland.nsdk.core.api.external.devicemanager.BaudRateMode;
import com.newland.nsdk.core.api.external.devicemanager.BeeperControl;
import com.newland.nsdk.core.api.external.devicemanager.BeeperTone;
import com.newland.nsdk.core.api.external.devicemanager.DecryptionMode;
import com.newland.nsdk.core.api.external.devicemanager.DeviceConfiguration;
import com.newland.nsdk.core.external.command.common.ExternalCommonModule;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.communication.mock.MockExternalCommunicator;

import org.junit.Before;
import org.junit.Test;

public class ExternalCommonModuleTest {

    private ExternalCommonModule commonModule = new ExternalCommonModule();

    @Before
    public void init(){
        ExternalCommunicator communicator = new MockExternalCommunicator();
        ExternalCommunicationManager.getInstance().setCommunicator(communicator);
    }

    @Test
    public void getVersionNumberTest() {
        try {
            String versionNumber = commonModule.getVersionNumber();
            System.out.println(String.format("Version number: %s", versionNumber));
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void flashLED() {
        try {
            commonModule.flashLED(LEDColor.BLUE, 1000);
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void beepTest() {
        try {
            commonModule.beep(BeeperTone.ALERT, 100);
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getSerialNumber() {
        try {
            String serialNumber = commonModule.getSerialNumber();
            System.out.println(String.format("Get serial number: %s", serialNumber));
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void setSerialNumber() {
        try {
            // Success
            commonModule.setSerialNumber("1234567890123456");
            // Failed
//            commonModule.setSerialNumber("2234567890123456");
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadConfiguration() {
        try {
            DeviceConfiguration config = new DeviceConfiguration();
            config.setBaudRateMode(BaudRateMode.MODE_9600_8_N_1);
            config.setWorkingKeyDecryptionMode(DecryptionMode.TDEA_CBC);
            config.setBeeperControl(BeeperControl.KEY_PAD_ONLY);
            commonModule.loadConfiguration(config);
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getConfiguration(){
        try{
            DeviceConfiguration config = commonModule.getConfiguration();
            System.out.println("Get configuration, beeper control: " + config.getBeeperControl());
            System.out.println("Get configuration, working decrypt mode: " + config.getWorkingKeyDecryptionMode());
            System.out.println("Get configuration, baud rate: " + config.getBaudRateMode());
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void reboot(){
        try{
            commonModule.reboot();
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }
}