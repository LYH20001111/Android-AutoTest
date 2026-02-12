package com.newland.nsdk.externaldevice.emv.aid;

import com.newland.nsdk.core.api.common.cardreader.CardType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.external.communication.ExternalCommunicator;
import com.newland.nsdk.core.api.external.emvl3.configuration.aid.AIDEntry;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.communication.mock.MockExternalCommunicator;
import com.newland.nsdk.core.external.command.emv.aid.ExternalAidModule;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ExternalAidModuleTest {

    private ExternalAidModule aidModule = new ExternalAidModule();

    @Before
    public void init(){
        ExternalCommunicator communicator = new MockExternalCommunicator();
        ExternalCommunicationManager.getInstance().setCommunicator(communicator);
    }

    @Test
    public void updateConfiguration() {
        try {
            aidModule.updateAIDConfiguration(CardType.CONTACT_CARD.ordinal(), new byte[]{0x01, 0x02, 0x03});
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getAidNumber() {
        try {
            List<AIDEntry> contactList = aidModule.getAIDList(CardType.CONTACT_CARD.ordinal()) ;
            List<AIDEntry> contactlessList = aidModule.getAIDList(CardType.CONTACTLESS_CARD.ordinal()) ;
            System.out.println("AID contactList: " + contactList.size());
            System.out.println("AID contactList: " + ISOUtils.hexString(contactList.get(1).getAid()));
            System.out.println("AID contactlessList: " + contactlessList.size());
            System.out.println("AID contactList: " + ISOUtils.hexString(contactlessList.get(1).getAid()));
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getConfiguration() {
        try {
            AIDEntry aidEntry = new AIDEntry();
            aidEntry.setAid(new byte[]{0x01, 0x02, 0x03, 0x04});
            aidEntry.setKernelId(new byte[]{0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18});
            aidEntry.setTransactionType((byte)0x9C);

            byte[] result = aidModule.getAIDConfiguration(CardType.CONTACT_CARD.ordinal(), aidEntry);
//            byte[] result = aidModule.getConfiguration((byte) 0x01, new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F}, new byte[]{0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18}, (byte)0x01, (byte)0x9C);
//            byte[] result = aidModule.getConfiguration((byte) 0x01, new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x1F}, new byte[]{0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18}, (byte)0x01, (byte)0x9C);
            System.out.println(String.format("AID configuration: %s", ISOUtils.hexString(result)));
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void remove() {
        try {
            AIDEntry aidEntry = new AIDEntry();
            aidEntry.setAid(new byte[]{0x01, 0x02, 0x03, 0x04});
            aidEntry.setKernelId(new byte[]{0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18});
            aidEntry.setTransactionType((byte)0x9C);

            aidModule.removeAID(CardType.CONTACT_CARD.ordinal(), aidEntry);
//            aidModule.remove((byte) 0x01, null, new byte[]{0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18}, (byte)0x01, (byte)0x9C);

//            aidModule.remove((byte) 0x01, new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F}, new byte[]{0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18}, (byte)0x01, (byte)0x9C);
//            aidModule.remove((byte) 0x01, new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x1F}, new byte[]{0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18}, (byte)0x01, (byte)0x9C);
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void removeAll() {
        try {
            aidModule.removeAllAID(CardType.CONTACT_CARD.ordinal());
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }
}