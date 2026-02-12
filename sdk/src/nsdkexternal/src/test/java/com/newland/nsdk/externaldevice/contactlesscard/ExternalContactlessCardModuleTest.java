package com.newland.nsdk.externaldevice.contactlesscard;

import com.newland.nsdk.core.api.common.card.contactless.ContactlessCardType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.external.card.ExtAPDUOutput;
import com.newland.nsdk.core.api.external.communication.ExternalCommunicator;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.communication.mock.MockExternalCommunicator;
import com.newland.nsdk.core.external.command.contactlesscard.ContactlessCardMode;
import com.newland.nsdk.core.external.command.contactlesscard.ContactlessCardResult;
import com.newland.nsdk.core.external.command.contactlesscard.ExternalContactlessCardModule;

import org.junit.Before;
import org.junit.Test;

public class ExternalContactlessCardModuleTest {

    private ExternalContactlessCardModule contactlessCardModule = new ExternalContactlessCardModule();

    @Before
    public void init(){
        ExternalCommunicator communicator = new MockExternalCommunicator();
        ExternalCommunicationManager.getInstance().setCommunicator(communicator);
    }

    @Test
    public void checkReader() {
        try {
            byte[] data = contactlessCardModule.checkReader();
            if (data != null) {
                System.out.println("Data: " + ISOUtils.hexString(data));
            }
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkCard() {
        try {
            boolean result = contactlessCardModule.checkCard(20000);
            System.out.println("Check card result: " + result);
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void exchangeAPDU() {
        try {
            ExtAPDUOutput data = contactlessCardModule.exchangeAPDU((byte)3, new byte[]{0x11, 0x11, 0x11});
            if (data != null) {
                System.out.println("APDU result: " + ISOUtils.hexString(data.getData()));
            }
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deactivate() {
        try {
            contactlessCardModule.deactivate();
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void exchangeClearAPDU() {
        try {
            byte[] data = contactlessCardModule.exchangeClearAPDU(new byte[]{0x11, 0x11, 0x11});
            if (data != null) {
                System.out.println("APDU result: " + ISOUtils.hexString(data));
            }
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkFelicaCard() {
        try {
            byte[] data = contactlessCardModule.checkFelicaCard(2000);
            if (data != null) {
                System.out.println("ID: " + ISOUtils.hexString(data));
            }
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void searchCard() {
        ContactlessCardType[] cardTypes = new ContactlessCardType[]{ContactlessCardType.TYPE_A, ContactlessCardType.TYPE_B};

        try {
            ContactlessCardResult result = contactlessCardModule.searchCard(cardTypes, ContactlessCardMode.WUPA, 100);
            if (result != null) {
                System.out.println("Card type: " + result.getCardType());
                if (result.getATQA() != null) {
                    System.out.println("ATQA: " + ISOUtils.hexString(result.getATQA()));
                }
                if (result.getUID() != null) {
                    System.out.println("UID: " + ISOUtils.hexString(result.getUID()));
                }
                if (result.getSAK() != null) {
                    System.out.println("SAK: " + ISOUtils.hexString(result.getSAK()));
                }
            }
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void exchangeFelicaAPDU() {
        try {
            byte[] data = contactlessCardModule.exchangeFelicaAPDU(new byte[]{0x11, 0x11, 0x11});
            if (data != null) {
                System.out.println("APDU result: " + ISOUtils.hexString(data));
            }
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void authenticateWithExternalKey() {
        try {
            contactlessCardModule.authenticateWithExternalKey((byte) 0x60, new byte[]{0x11, 0x22, 0x33}, (byte) 3, new byte[] {0x44, 0x55, 0x66, 0x77});
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void readBlock() {
        try {
            byte[] data = contactlessCardModule.readBlock((byte)2);
            if (data != null) {
                System.out.println("Read data: " + ISOUtils.hexString(data));
            }
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void writeBlock() {
        try {
            contactlessCardModule.writeBlock((byte)2, new byte[]{0x11, 0x22, 0x33});
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void increment() {
        try {
            contactlessCardModule.increment((byte)2, new byte[]{0x11, 0x22, 0x33});
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void decrement() {
        try {
            contactlessCardModule.decrement((byte)2, new byte[]{0x11, 0x22, 0x33});
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void transfer() {
        try {
            contactlessCardModule.transfer((byte)2);
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void restore() {
        try {
            contactlessCardModule.restore((byte)2);
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getATS() {
        try {
            byte[] data = contactlessCardModule.getATS((byte) 2);
            if (data != null) {
                System.out.println("Response data: " + ISOUtils.hexString(data));
            }
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

//    @Test
//    public void getPresenceType(){
//        ContactlessCardType[] cardTypes = new ContactlessCardType[] {ContactlessCardType.ACARD};
//        try {
//            System.out.println("Card type: " + contactlessCardModule.getPresenceType(cardTypes));
//            cardTypes = new ContactlessCardType[] {ContactlessCardType.BCARD};
//            System.out.println("Card type: " + contactlessCardModule.getPresenceType(cardTypes));
//            cardTypes = new ContactlessCardType[] {ContactlessCardType.ACARD, ContactlessCardType.BCARD};
//            System.out.println("Card type: " + contactlessCardModule.getPresenceType(cardTypes));
//            cardTypes = new ContactlessCardType[] {ContactlessCardType.M1CARD};
//            System.out.println("Card type: " + contactlessCardModule.getPresenceType(cardTypes));
//            cardTypes = new ContactlessCardType[] {ContactlessCardType.ACARD, ContactlessCardType.M1CARD};
//            System.out.println("Card type: " + contactlessCardModule.getPresenceType(cardTypes));
//            cardTypes = new ContactlessCardType[] {ContactlessCardType.BCARD, ContactlessCardType.M1CARD};
//            System.out.println("Card type: " + contactlessCardModule.getPresenceType(cardTypes));
//            cardTypes = new ContactlessCardType[] {ContactlessCardType.ACARD, ContactlessCardType.BCARD, ContactlessCardType.M1CARD};
//            System.out.println("Card type: " + contactlessCardModule.getPresenceType(cardTypes));
//        } catch (NSDKIllegalParameterException e) {
//            e.printStackTrace();
//        }
//    }
}