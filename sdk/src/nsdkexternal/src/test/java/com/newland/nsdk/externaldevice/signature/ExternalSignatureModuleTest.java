package com.newland.nsdk.externaldevice.signature;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.external.communication.ExternalCommunicator;
import com.newland.nsdk.core.api.external.signature.ExtSignatureInfo;
import com.newland.nsdk.core.api.external.signature.PreviousSignatureState;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.communication.mock.MockExternalCommunicator;
import com.newland.nsdk.core.external.command.signature.ExternalSignatureModule;
import com.newland.nsdk.core.external.command.signature.HandshakeResult;

import org.junit.Before;
import org.junit.Test;

public class ExternalSignatureModuleTest {
    private ExternalSignatureModule signatureModule = new ExternalSignatureModule();

    @Before
    public void init(){
        ExternalCommunicator communicator = new MockExternalCommunicator();
        ExternalCommunicationManager.getInstance().setCommunicator(communicator);
    }

    @Test
    public void handshake() {
        try {
            HandshakeResult result = signatureModule.handshake();
            System.out.println("Is ready: " + result.isReady());
            System.out.println("Support storage: " + result.isStorageSupported());
            if (result.getSoftwareVersion() != null) {
                System.out.println("Software version: " + ISOUtils.hexString(result.getSoftwareVersion()));
            }

            if (result.getSerialNumber() != null) {
                System.out.println("SN: " + result.getSerialNumber());
            }
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkPreviousSignatureState() {
        try {
            PreviousSignatureState result = signatureModule.checkPreviousSignatureState(new byte[]{0x11, 0x22, 0x33}, true);
            System.out.println("Result: " + result);
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void inputSignature() {
        try {
            ExtSignatureInfo result = signatureModule.inputSignature((byte) 3, 100, "display message".getBytes());
            if (result.getNumber() != null) {
                System.out.println("Number: " + ISOUtils.hexString(result.getNumber()));
            }

            if (result.getData() != null) {
                System.out.println("Data: " + ISOUtils.hexString(result.getData()));
            }
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void completeSign() {
        try {
            signatureModule.completeSign();
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getLastFailedSignature() {
        try {
            ExtSignatureInfo info = signatureModule.getLastFailedSignature();
            if (info.getData() != null) {
                System.out.println("Data: " + ISOUtils.hexString(info.getData()));
            }
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void batchEndResponse() {
        try {
            signatureModule.batchEndResponse();
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getFailedSignature() {
        try {
            ExtSignatureInfo info = signatureModule.getFailedSignature(new byte[]{0x11, 0x22, 0x33});
            if (info.getData() != null) {
                System.out.println("Data: " + ISOUtils.hexString(info.getData()));
            }
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getSuccessSignature() {
        try {
            ExtSignatureInfo info = signatureModule.getSuccessSignature(new byte[]{0x11, 0x22, 0x33});
            if (info.getData() != null) {
                System.out.println("Data: " + ISOUtils.hexString(info.getData()));
            }
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }
}