package com.newland.nsdk.externaldevice.smartcard;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.external.communication.ExternalCommunicator;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.communication.mock.MockExternalCommunicator;
import com.newland.nsdk.core.external.command.smartcard.ExternalSmartCardModule;

import org.junit.Before;
import org.junit.Test;

public class ExternalSmartCardModuleTest {

    private ExternalSmartCardModule smartModule = new ExternalSmartCardModule();

    @Before
    public void init(){
        ExternalCommunicator communicator = new MockExternalCommunicator();
        ExternalCommunicationManager.getInstance().setCommunicator(communicator);
    }

    @Test
    public void searchCard(){
        byte[] apdu = {0x30,0x33,0x35};
        try{
            smartModule.searchCard(5,apdu,"hdsajkl",null,null,null);
        }catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void powerUp(){
        try{
            smartModule.powerUp(5,new String[]{"fgsgfd"});
        }catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void powerDown(){
        try{
            smartModule.powerDown();
        }catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void exchangeAPDU(){
        byte[] apdu = {0x30,0x33,0x35};
        byte keyId = 1;
        byte keyType = 1;
        byte keyMode = 1;
        byte[] iv = new byte[]{0x11, 0x22, 0x33};
        try{
            smartModule.exchangeAPDU(keyId, keyType, keyMode, null, apdu);
            smartModule.exchangeAPDU(keyId, keyType, keyMode, iv, apdu);
        }catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void detect(){
        try{
            smartModule.checkCard(5);
        }catch (NSDKException e) {
            e.printStackTrace();
        }
    }
}
