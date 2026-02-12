package com.newland.nsdk.externaldevice.cardreader;


import com.newland.nsdk.core.api.common.card.magcard.MagCardInfo;
import com.newland.nsdk.core.api.common.card.magcard.TrackStatus;
import com.newland.nsdk.core.api.common.cardreader.CardType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.keymanager.CipherMode;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.external.cardreader.ExtCardReaderParameters;
import com.newland.nsdk.core.api.external.communication.ExternalCommunicator;
import com.newland.nsdk.core.external.command.cardreader.DetectedCardInfo;
import com.newland.nsdk.core.external.command.cardreader.ExternalCardReaderModule;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.communication.mock.MockExternalCommunicator;

import org.junit.Before;
import org.junit.Test;

import static com.newland.nsdk.core.api.common.cardreader.CardType.CONTACTLESS_CARD;
import static com.newland.nsdk.core.api.common.cardreader.CardType.CONTACT_CARD;
import static com.newland.nsdk.core.api.common.cardreader.CardType.MAG_CARD;

public class ExternalCardReaderModuleTest {

    private ExternalCardReaderModule cardReaderModule = new ExternalCardReaderModule();

    @Before
    public void init(){
        ExternalCommunicator communicator = new MockExternalCommunicator();
        ExternalCommunicationManager.getInstance().setCommunicator(communicator);
    }

    @Test
    public void searchCard() {
        ExtCardReaderParameters parameter = new ExtCardReaderParameters();
        //TODO
        CardType[] cardTypes = new CardType[]{MAG_CARD, CONTACT_CARD, CONTACTLESS_CARD};
//        ContactlessCardType[] cardTypes = new ContactlessCardType[]{ContactlessCardType.TYPE_A, ContactlessCardType.TYPE_B};

        int timeout = 30;
        parameter.setDisplayMessages(new String[] {"line1", null, "line3"});
        try {
            DetectedCardInfo cardInfo = cardReaderModule.searchCard(cardTypes, timeout, parameter);
            if (cardInfo != null) {
                System.out.println("Card type: " + cardInfo.getCardType());
                MagCardInfo magCardInfo = cardInfo.getMagCardInfo();
                System.out.println("PlainTrack1DataLen: " + magCardInfo.getPlainTrack1DataLen());
                System.out.println("PlainTrack2DataLen: " + magCardInfo.getPlainTrack2DataLen());
                System.out.println("PlainTrack3DataLen: " + magCardInfo.getPlainTrack3DataLen());
                TrackStatus[] trackStatus = magCardInfo.getTrackStatus();
                if(trackStatus[0] == TrackStatus.GOOD) {
                    System.out.println("Track1Data: " + ISOUtils.hexString(magCardInfo.getTrack1Data()));
                }

                if(trackStatus[1] == TrackStatus.GOOD) {
                    System.out.println("Track2Data: " + ISOUtils.hexString(magCardInfo.getTrack2Data()));
                }

                if(trackStatus[2] == TrackStatus.GOOD) {
                    System.out.println("Track3Data: " + ISOUtils.hexString(magCardInfo.getTrack3Data()));
                }

                System.out.println("FirstClearPAN: " + magCardInfo.getFirstClearPAN());
                System.out.println("LastClearPAN: " + magCardInfo.getLastClearPAN());
                System.out.println("ServiceCode: " + magCardInfo.getServiceCode());
                System.out.println("ValidDate: " + magCardInfo.getValidDate());
                System.out.println("PanData: " + new String(magCardInfo.getPanData()));
                // todo
            }
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void cancelSearch() {
    }

    @Test
    public void getMagCardInfo() {
        try {
            MagCardInfo result = cardReaderModule.getMagCardInfo((byte)2, KeyType.DES, CipherMode.ECB, null, new boolean[]{false, true, true});
            if (result.getPanData() != null) {
                System.out.println("PAN: " + result.getPanData());
            }

            if (result.getFirstClearPAN() != null) {
                System.out.println("Masked PAN: " + result.getFirstClearPAN());
            }

            if (result.getLastClearPAN() != null) {
                System.out.println("Masked PAN: " + result.getLastClearPAN());
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