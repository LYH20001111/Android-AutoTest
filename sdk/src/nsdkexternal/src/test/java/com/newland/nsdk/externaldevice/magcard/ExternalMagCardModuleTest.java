package com.newland.nsdk.externaldevice.magcard;


import com.newland.nsdk.core.api.common.card.magcard.MagCardInfo;
import com.newland.nsdk.core.api.common.crypto.CipherType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.external.cardreader.ExtCardReaderParameters;
import com.newland.nsdk.core.api.external.communication.ExternalCommunicator;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.communication.mock.MockExternalCommunicator;
import com.newland.nsdk.core.external.command.magcard.ExternalMagCardModule;

import org.junit.Before;
import org.junit.Test;

public class ExternalMagCardModuleTest {
    private ExternalMagCardModule magCardModule = new ExternalMagCardModule();

    @Before
    public void init(){
        ExternalCommunicator communicator = new MockExternalCommunicator();
        ExternalCommunicationManager.getInstance().setCommunicator(communicator);
    }

    @Test
    public void searchCard() {
        ExtCardReaderParameters parameter = new ExtCardReaderParameters();
        parameter.setPANKeyIndex((byte)1);
        parameter.setCipherType(CipherType.AES_CBC);
        parameter.setIV(new byte[]{0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18});
        parameter.setDisplayMessages(new String[]{"line1", null, "line3"});

        try {
            MagCardInfo result = magCardModule.searchCard(0, 10, parameter);
            if (result.getPanData() != null) {
                System.out.println("PAN: " + result.getPanData());
            }

            if (result.getFirstClearPAN() != null) {
                System.out.println("First clear part of Masked PAN: " + result.getFirstClearPAN());
            }

            if (result.getLastClearPAN() != null) {
                System.out.println("Last clear part of Masked PAN: " + result.getLastClearPAN());
            }

            if (result.getTrack1Data() != null) {
                System.out.println("Track1: " + ISOUtils.hexString(result.getTrack1Data()));
            }

            if (result.getTrack2Data() != null) {
                System.out.println("Track2: " + ISOUtils.hexString(result.getTrack2Data()));
            }

            if (result.getTrack3Data() != null) {
                System.out.println("Track3: " + ISOUtils.hexString(result.getTrack3Data()));
            }
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }
}