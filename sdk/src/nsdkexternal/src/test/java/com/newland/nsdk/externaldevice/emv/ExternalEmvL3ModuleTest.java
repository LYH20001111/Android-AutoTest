package com.newland.nsdk.externaldevice.emv;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.external.communication.ExternalCommunicator;
import com.newland.nsdk.core.api.external.emvl3.ExtEMVL3Const;
import com.newland.nsdk.core.api.external.emvl3.listener.CandidateAID;
import com.newland.nsdk.core.api.external.emvl3.listener.ExtPerformTransactionListener;
import com.newland.nsdk.core.api.external.emvl3.listener.TransactionResult;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.communication.mock.MockExternalCommunicator;
import com.newland.nsdk.core.external.command.emv.ExternalEmvL3Module;
import com.newland.nsdk.core.api.external.emvl3.TLVResult;
import com.newland.nsdk.core.external.command.message.ExternalMessage;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ExternalEmvL3ModuleTest {

    ExternalEmvL3Module emvL3Module = new ExternalEmvL3Module();

    @Before
    public void init(){
        ExternalCommunicator communicator = new MockExternalCommunicator();
        ExternalCommunicationManager.getInstance().setCommunicator(communicator);
    }

    @Test
    public void initEmv() {
        try {
            emvL3Module.initEMV(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08});
//            emvL3Module.initEmv(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, });
//            emvL3Module.initEmv(null);
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void setData() {
        try {
//            emvL3Module.setData(0x9F8107, new byte[]{0x01, 0x02, 0x03, 0x04});
            emvL3Module.setTlvListData(new byte[]{0x01, 0x02, 0x03, 0x04});
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getData() {
        try {
            TLVResult result = emvL3Module.getTlvListData((byte) 0x22, 256, (byte) 1,new int[]{0x9F8109});
            System.out.println(String.format("Get tvl data-status: %02X", result.getDataStatus()));
            System.out.println(String.format("Get tvl data-actual data len: %d", result.getActualDataLen()));
            if (result.getDataStatus() == 0) {
                System.out.println(String.format("Get tlv data-result: %s", ISOUtils.hexString(result.getData())));
            }
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void setTlvList() {
        try {
            emvL3Module.setTlvListData(new byte[]{0x01, 0x02, 0x03, 0x04});
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getTlvList() {
        try {
            TLVResult result = emvL3Module.getTlvListData((byte) 0x22, 1000, (byte) 0x34, new int[]{0x9F01, 0x9F2321, 0x9F26});
            System.out.println(String.format("Get tvl data-status: %02X", result.getDataStatus()));
            System.out.println(String.format("Get tvl data-actual data len: %d", result.getActualDataLen()));
            if (result.getDataStatus() == 0) {
                System.out.println(String.format("Get tlv data-result: %s", ISOUtils.hexString(result.getData())));
            }
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void setDebugMode() {
        try {
            emvL3Module.setDebugMode((byte) 0x32);
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getVersion() {
        try {
            String version = emvL3Module.getVersion((byte) 0x32);
            System.out.println(String.format("Version: %s", version));
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void setConfig() {
        try {
            emvL3Module.setConfig(1,(byte) 1);
            System.out.println(String.format("setConfig: %s", true));
        } catch (NSDKException e) {
            e.printStackTrace();
            System.out.println(String.format("setConfig: %s", false));
        }
    }
    @Test
    public void getConfig() {
        try {
            boolean result = emvL3Module.getConfig(1);
            System.out.println(String.format("getConfig: %s", result));
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void cancelTransaction() {
        try {
            emvL3Module.cancelTransaction();
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void performTransaction(){
        this.setCallback();
        try {
            int cardTypes = ExtEMVL3Const.CardInterface.CONTACT | ExtEMVL3Const.CardInterface.CONTACTLESS | ExtEMVL3Const.CardInterface.MAGSTRIPE;
            TransactionResult result = emvL3Module.performTransaction(cardTypes, 60, new byte[]{0x11, 0x22, 0x33});
            System.out.println(String.format("Result : %d", result.getResult()));
            System.out.println(String.format("TLV data : %s", ISOUtils.hexString(result.getTLVListData())));
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

//    @Test
//    public void performTransaction1() {
//        new Thread() {
//            @Override
//            public void run() {
//                try {
//                    System.out.println("Start to perform transaction.");
//                    emvL3Module.performTransaction((byte) 0x00, 10, new byte[]{0x11, 0x22, 0x33});
//                    System.out.println("After transaction performed.");
//                } catch (NSDKException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        new Thread() {
//            @Override
//            public void run() {
//                getVersion();
//            }
//        }.start();
//
//        try {
//            Thread.sleep(20000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

    @Test
    public void completeTransaction() {
        try {
            byte[] commandData = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A};
            TransactionResult result = emvL3Module.completeTransaction((byte) 0x01, commandData);
            System.out.println(String.format("Transaction result: %d", result.getResult()));
            System.out.println(String.format("Error code: %s", result.getErrorCode()));
            System.out.println(String.format("Data status: %d", result.getTLVDataStatus()));
            System.out.println(String.format("Actual data len: %d", result.getActualDataLen()));
            System.out.println(String.format("Actual data len: %s", result.getTLVListData() == null ? "null" : ISOUtils.hexString(result.getTLVListData())));
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void terminateTransaction() {
        try {
            emvL3Module.terminateTransaction("Test transaction.", 10000);
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void setCallback() {
        try {
            emvL3Module.setCallback(new ExtPerformTransactionListener() {
                @Override
                public void onCardNumberConfirm(String maskPAN) {
                    System.out.println(String.format("onCardNumberConfirm %s", maskPAN));
                }

                @Override
                public void onUIEvent(int uiEventID, byte[] uiEventData) {
                    System.out.println(String.format("UI event, UI event id: %d, event data: %s", uiEventID, ISOUtils.hexString(uiEventData)));
                }

                @Override
                public void onCandidateAIDList(ArrayList<CandidateAID> externalCandidateAidList) {
                    try {
                        System.out.println(String.format("Select candidate AID, number: %d", externalCandidateAidList.size()));
                        byte[] aid;
                        if (externalCandidateAidList != null && externalCandidateAidList.size() > 0) {
                            for (CandidateAID candidateAID:externalCandidateAidList) {
                                if (candidateAID != null) {
                                    aid = candidateAID.getAID();
                                    System.out.println("Candidate AID data: " + aid == null ? "null" : ISOUtils.hexString(aid));
                                }
                            }
                        }

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        baos.write((byte) 1);
                        baos.write(externalCandidateAidList.get(1).getAID());

                        emvL3Module.responseEvent(0,baos.toByteArray());
                    } catch (NSDKException | IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCredentialsCheck(byte type, byte[] number) {
                    System.out.println(String.format("Check credentials, type: %d, number: %s", type, ISOUtils.hexString(number)));
                    try {

                        emvL3Module.responseEvent(0,new byte[]{0x01, 0x02, 0x03, 0x04});
                    } catch (NSDKException e) {
                        e.printStackTrace();
                    }
                }


                @Override
                public void onFinalSelect(byte cardInterface, byte[] aid) {
                    System.out.println(String.format("Final select, card interface: %d, aid: %s", cardInterface, ISOUtils.hexString(aid)));
                    try {
                        emvL3Module.responseEvent(0,new byte[]{0x01, 0x02, 0x03});
                    } catch (NSDKException e) {
                        e.printStackTrace();
                    }
                }

                /**
                 * PIN entry request.
                 *
                 * @param pinType Required PIN type.
                 *                <ul>
                 *                <li>L3_PIN_ONLINE</li>
                 *                <li>L3_PIN_OFFLINE</li>
                 *                <li>L3_PIN_OFFLINE_ENCIPHERED</li>
                 *                </ul>
                 * @param tlvData TLV data.
                 * @return
                 */
                @Override
                public void onPinEntry(byte pinType, byte[] tlvData) {
                    System.out.println(String.format("PIN entry deal, pin type: %d, tlv data: %s", pinType, ISOUtils.hexString(tlvData)));
                    try {
                        emvL3Module.responseEvent(0,new byte[]{ (byte) 0x59});
                    } catch (NSDKException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onTransResult(TransactionResult transactionResult) {
                    //TODO
                }
            });
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void extractEmvResponseData(){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] data1 = ISOUtils.hex2byte("200824C312F213030004A45504C69625B454D564C325F43686B496E5461674C6973745D2C206C696E655B3135335D3E3E3E656D54616754797065203D20312C20756E5461674E616D65203D203078394634310D0A038D");
        byte[] dataUseless = ISOUtils.hex2byte("254152");
        byte[] data2 = ISOUtils.hex2byte("0200064C312F33303003643265155652");
        outputStream.write(ExternalMessage.ACK);
        outputStream.write(ExternalMessage.ACK);
        try {
            outputStream.write(data1);
            outputStream.write(dataUseless);
            outputStream.write(ExternalMessage.ACK);
            outputStream.write(ExternalMessage.ACK);
            outputStream.write(data2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ArrayList<ExternalMessage> messages = new ArrayList<>();
            byte[] restData = ExternalMessage.extractResponseData(outputStream.toByteArray(), messages);
            if (restData != null) {
                System.out.println("Rest data: " + ISOUtils.hexString(restData));
            }
            for (ExternalMessage message: messages) {
                System.out.println("Message type: " + message.getMessageType());
                System.out.println("Message data: " + ISOUtils.hexString(message.getMessageData()));
            }
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void transactionPreprocess() {
        try {
            byte[] errorCode = emvL3Module.clssPreProcess(new byte[]{0x11, 0x22, 0x33});
            System.out.println("Error code: " + ISOUtils.hexString(errorCode));
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }
}