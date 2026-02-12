package com.newland.nsdk.externaldevice.emv.capk;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.external.communication.ExternalCommunicator;
import com.newland.nsdk.core.api.external.emvl3.configuration.capk.CAPKEntry;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.communication.mock.MockEmvResponse;
import com.newland.nsdk.core.external.command.communication.mock.MockExternalCommunicator;
import com.newland.nsdk.core.external.command.emv.capk.ExternalCapkModule;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class CAPKModuleTest {

    private ExternalCapkModule capkModule = new ExternalCapkModule();

    @Before
    public void init(){
        ExternalCommunicator communicator = new MockExternalCommunicator();
        ExternalCommunicationManager.getInstance().setCommunicator(communicator);
    }

    @Test
    public void getCapkNumber() {
        try {
            List<CAPKEntry> capkList = capkModule.getCAPKList();

            System.out.println("CAPK list: " + capkList.size());
            System.out.println("CAPK list: " + capkList.get(0).getIndex()+"--"+ISOUtils.hexString(capkList.get(0).getRID()));
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateCapk() {
        try {
            capkModule.loadCAPK(MockEmvResponse.getMockCapk(false));
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getCapk() {
        try {
            CAPKEntry capk = capkModule.getCAPK(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05}, (byte) 0x22);
            System.out.println(String.format("Modulus: %s", ISOUtils.hexString(capk.getModulus())));
            System.out.println(String.format("Exponent: %s", ISOUtils.hexString(capk.getExponent())));
            System.out.println(String.format("Hash: %s", ISOUtils.hexString(capk.getHash())));
            System.out.println(String.format("ExpiredDate: %s", ISOUtils.hexString(capk.getExpiredDate())));
            System.out.println(String.format("RID: %s", ISOUtils.hexString(capk.getRID())));
            System.out.println(String.format("Index: %02X", capk.getIndex()));
            System.out.println(String.format("AlgorithmIndicator: %02X", capk.getAlgorithmIndicator()));
            System.out.println(String.format("HashAlgorithm: %02X", capk.getHashAlgorithm()));
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void remove() {
        try {
            capkModule.removeCAPK(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05}, (byte) 0x22);
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void removeAll() {
        try {
            capkModule.removeAllCAPK();
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }
}