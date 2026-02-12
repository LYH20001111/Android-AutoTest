package com.newland.nsdk.core.external.command.communication.mock;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.message.functionId.EmvFunctionId;
import com.newland.nsdk.core.api.external.communication.NSDKCommunicator;
import com.newland.nsdk.core.external.command.emv.ExternalEmvCallbackID;
import com.newland.nsdk.core.external.command.message.ExternalMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessageType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MockExternalCommunicator implements NSDKCommunicator {
    private volatile ExternalMessage currentRequestMessage;

    @Override
    public void open(int timeout) throws NSDKException {

    }

    @Override
    public void close(int timeout) throws NSDKException {

    }

    @Override
    public void setCommunicationTimeout(int sendTimeout, int receiveTimeout) {
        ExternalCommunicationManager.getInstance().setSendTimeout(sendTimeout);
        ExternalCommunicationManager.getInstance().setReceiveTimeout(receiveTimeout);
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public void send(byte[] data, int timeout) throws NSDKException {
        System.out.println(String.format("############## Send data: %s", ISOUtils.hexString(data)));
        ExternalMessage requestMessage = new ExternalMessage();
        if (data[0] != ExternalMessage.ACK) {
            requestMessage = ExternalMessage.unpack(data);
            currentRequestMessage = requestMessage;

            switch (requestMessage.getMessageType()) {
                case ExternalMessageType.EMV_REQUEST:
                    MockEmvResponse.handleEmvRequestMessage(requestMessage);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public byte[] receive(int timeout) throws NSDKException {
        byte[] data = null;
        ExternalMessage responseMessage = new ExternalMessage();
        switch (currentRequestMessage.getMessageType()) {
            case ExternalMessageType.GET_VERSION_NUMBER_REQUEST:
                responseMessage.setMessageType(ExternalMessageType.GET_VERSION_NUMBER_RESPONSE);
                // Message data = Response code(2 bytes) + Software version number len(1 byte) + Software version number
                responseMessage.setMessageData(new byte[]{0x30, 0x30, 0x04, 0x31, 0x32, 0x33, 0x34});
//                responseMessage.setMessageData(new byte[]{0x30, 0x32, 0x00}); // General error
                break;
            case ExternalMessageType.BEEP_REQUEST:
                return new byte[]{ExternalMessage.ACK};
//                responseMessage.setMessageType(ExternalMessageType.BEEP_REQUEST);
//                responseMessage.setMessageData(new byte[]{0x30, 0x30});
//                break;
            case ExternalMessageType.GET_SET_SERIAL_NUMBER_REQUEST:
                responseMessage.setMessageType(ExternalMessageType.GET_SET_SERIAL_NUMBER_RESPONSE);
                responseMessage.setMessageData("1234567890123456".getBytes());
                break;
            case ExternalMessageType.CONFIGURATION_LOAD_REQUEST:
                responseMessage.setMessageType(ExternalMessageType.CONFIGURATION_LOAD_RESPONSE);
                responseMessage.setMessageData(new byte[]{0x30, 0x30});
                break;
            case ExternalMessageType.GET_CONFIGURATION_REQUEST:
                responseMessage.setMessageType(ExternalMessageType.GET_CONFIGURATION_RESPONSE);
                responseMessage.setMessageData(new byte[]{0x31, 0x32, 0x31, 0x32});
                break;
            case ExternalMessageType.REBOOT_REQUEST:
                responseMessage.setMessageType(ExternalMessageType.REBOOT_RESPONSE);
                responseMessage.setMessageData(new byte[]{0x30, 0x30});
//                responseMessage.setMessageData(new byte[]{0x30, 0x31});
//                responseMessage.setMessageData(new byte[]{0x30, 0x32});
//                responseMessage.setMessageData(new byte[]{0x30, 0x34});
                break;
            case ExternalMessageType.SET_PIN_LINE_REQUEST:
                responseMessage.setMessageType(ExternalMessageType.SET_PIN_LINE_RESPONSE);
                responseMessage.setMessageData(new byte[]{0x30, 0x30});
//                responseMessage.setMessageData(new byte[]{0x30, 0x31});
//                responseMessage.setMessageData(new byte[]{0x30, 0x32});
                break;
            case ExternalMessageType.LOAD_KEY_BLOCK_REQUEST:
                responseMessage.setMessageType(ExternalMessageType.LOAD_KEY_BLOCK_RESPONSE);
                responseMessage.setMessageData(new byte[]{0x30, 0x30});
//                responseMessage.setMessageData(new byte[]{0x30, 0x32});
//                responseMessage.setMessageData(new byte[]{0x34, 0x32});
//                responseMessage.setMessageData(new byte[]{0x34, 0x33});
//                responseMessage.setMessageData(new byte[]{0x34, 0x35});
//                responseMessage.setMessageData(new byte[]{0x34, 0x36});
                break;
            case ExternalMessageType.GENERATE_KEY_REQUEST:
                responseMessage.setMessageType(ExternalMessageType.GENERATE_KEY_RESPONSE);
                responseMessage.setMessageData(new byte[]{0x30, 0x30, 0x00, 0x00});
                break;
//            case ExternalMessageType.GET_KEY_INFO_REQUEST:
//                responseMessage.setMessageType(ExternalMessageType.GET_KEY_INFO_RESPONSE);
//                responseMessage.setMessageData(new byte[]{0x30, 0x30, 0x00, 0x08, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, 0x00});
//                break;
            case ExternalMessageType.DATA_ENCRYPTION_DECRYPTION_NAPI_REQUEST:
                responseMessage = MockPinPadResponse.dataEncryptDecryptNapi(currentRequestMessage);
                break;
            case ExternalMessageType.LOAD_DUKPT_KEY_BLOCK_REQUEST:
                responseMessage.setMessageType(ExternalMessageType.LOAD_DUKPT_KEY_BLOCK_RESPONSE);
                responseMessage.setMessageData(new byte[]{0x30, 0x30});
//                responseMessage.setMessageData(new byte[]{0x30, 0x31});
//                responseMessage.setMessageData(new byte[]{0x30, 0x32});
//                responseMessage.setMessageData(new byte[]{0x30, 0x33});
//                responseMessage.setMessageData(new byte[]{0x30, 0x34});
                break;
            case ExternalMessageType.GET_KEY_CHECK_VALUE_REQUEST:
                responseMessage.setMessageType(ExternalMessageType.GET_KEY_CHECK_VALUE_RESPONSE);
                responseMessage.setMessageData(new byte[]{0x01, 0x11, 0x22, 0x33});
//                responseMessage.setMessageData(new byte[]{0x01, 0x11, 0x22, 0x33, 0x44, 0x55});
//                responseMessage.setMessageData(new byte[]{(byte) 0xFF, 0x11, 0x22, 0x33, 0x44, 0x55});
                break;
            case ExternalMessageType.GET_DUKPT_KSN_REQUEST:
                responseMessage.setMessageType(ExternalMessageType.GET_DUKPT_KSN_RESPONSE);
                responseMessage.setMessageData(new byte[]{0x30, 0x30, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A});
//                responseMessage.setMessageData(new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A});
                break;
            case ExternalMessageType.DATA_ENCRYPTION_DECRYPTION_REQUEST:
                responseMessage = MockPinPadResponse.dataEncryptDecrypt(currentRequestMessage);
                break;
            case ExternalMessageType.MAC_GENERATION_REQUEST:
                responseMessage = MockPinPadResponse.macGeneration(currentRequestMessage);
                break;
            case ExternalMessageType.MAC_GENERATION_NAPI_REQUEST:
                responseMessage = MockPinPadResponse.macGenerationNapi(currentRequestMessage);
                break;
            case ExternalMessageType.DUKPT_DATA_ENCRYPTION_DECRYPTION_REQUEST:
                responseMessage = MockPinPadResponse.dukptEncryptDecrypt(currentRequestMessage);
                break;
            case ExternalMessageType.AES_DATA_ENCRYPTION_DECRYPTION_REQUEST:
                responseMessage = MockPinPadResponse.aesDataEncryptDecrypt(currentRequestMessage);
                break;
            case ExternalMessageType.PIN_ENTRY_REQUEST:
                responseMessage = MockPinPadResponse.pinEntry(currentRequestMessage);
                break;
            case ExternalMessageType.EXTENDED_PIN_ENTRY_REQUEST:
                responseMessage = MockPinPadResponse.extendedPinEntry(currentRequestMessage);
                break;
            case ExternalMessageType.SENSITIVE_DATA_ENTRY_REQUEST:
                responseMessage = MockPinPadResponse.sensitivePinEntry(currentRequestMessage);
                break;
            case ExternalMessageType.LOAD_GISKE_KEY_REQUEST:
                responseMessage = MockPinPadResponse.loadGiskeKey();
                break;
            case ExternalMessageType.LOAD_CONVERT_ATM_TO_GISKE_REQUEST:
                responseMessage = MockPinPadResponse.convertAtmToGiske();
                break;
            case ExternalMessageType.LOAD_GISKE_TIK_REQUEST:
                responseMessage = MockPinPadResponse.loadGiskeTikKey();
                break;
            case ExternalMessageType.DELETE_KEY_REQUEST:
                responseMessage = MockPinPadResponse.deleteKey();
                break;
            case ExternalMessageType.DUKPT_KSN_INCREASE_REQUEST:
                responseMessage = MockPinPadResponse.increaseKsn();
                break;
            case ExternalMessageType.READ_MAG_CARD_REQUEST:
                responseMessage = mockReadMagCardResponse();
                break;
            case ExternalMessageType.GET_MAG_TRACK_DATA_REQUEST:
                responseMessage = mockGetMagTrackDataResponse();
                break;
            case ExternalMessageType.CARD_READ_REQUEST:
                responseMessage = mockCardReadResponse();
                break;
            case ExternalMessageType.CONTACTLESS_CARD_REQUEST:
                responseMessage = MockContactlessCardModuleResponse.response(currentRequestMessage);
                break;
            case ExternalMessageType.IC_CARD_REQUEST:
                responseMessage = MockSmartCardModuleResponse.response(currentRequestMessage);
                break;
            case ExternalMessageType.SCANNING_REQUEST:
                responseMessage = mockScanningResponse();
                break;
            case ExternalMessageType.SIGNATURE_REQUEST:
                responseMessage = MockSignatureResponse.response(currentRequestMessage);
                break;
            case ExternalMessageType.EMV_REQUEST:
                responseMessage = MockEmvResponse.response(currentRequestMessage);
                break;
            case ExternalMessageType.FILE_CREATE_REQUEST:
                responseMessage = mockUpdaterResponse(ExternalMessageType.FILE_CREATE_RESPONSE);
                break;
            case ExternalMessageType.APP_LOAD_REQUEST:
                responseMessage = mockUpdaterResponse(ExternalMessageType.APP_LOAD_RESPONSE);
                break;
            case ExternalMessageType.APP_UPDATE_REQUEST:
                responseMessage = mockUpdaterResponse(ExternalMessageType.APP_UPDATE_RESPONSE);
                break;
            case ExternalMessageType.DISPLAY_COLOR_IMAGE_REQUEST:
                responseMessage = MockDisplayResponse.response(currentRequestMessage);
                break;
            case ExternalMessageType.RANDOM_REQUEST:
                responseMessage.setMessageType(ExternalMessageType.RANDOM_RESPONSE);
                responseMessage.setMessageData(ISOUtils.hex2byte("3030000810B4AE1CF2126FCD"));
//                responseMessage.setMessageData(new byte[]{0x01, 0x11, 0x22, 0x33, 0x44, 0x55});
                break;
            case ExternalMessageType.GET_KEY_INFO_REQUEST:
                responseMessage.setMessageType(ExternalMessageType.GET_KEY_INFO_RESPONSE);
                responseMessage.setMessageData(ISOUtils.hex2byte("02137443332F303005572D2D2D2D2D424547494E2043455254494649434154452D2D2D2D2D0A4D494944785443434171326741774942416749485752306641414142757A414E42676B71686B69473977304241517346414443426854454C4D416B47413155450A42684D4351303478447A414E42674E564241674D426B5A31535647566A614735766247396E65534244627934735448526B4D524D77455159445651514C4441704F0A5546516754555A4849454E424D524977454159445651514444416C4E526B636755335669513045774868634E4D6A45774E6A45304D4441774D4441775768634E0A4D7A45774E6A45314D4441774D444177576A43426B545373774B5159445651514B44434A4F5A5864735957356B583142686557316C626E52665647566A614735760A6247396E65563944627934735448526B4D526377465159445651514C4441354F554651755647566A61454E6C626E526C636A45614D42674741315545417777520A555463334F5441774F4455304F4459304C5546727470337252646278644462627246424C6E4C3235506635546A5452614F4F32393162363748416C7141755341480A6B2F664C62485471447731306F336C58744B4D56584269385A68674967716A76544A2B4D75317A6E695262454B35716C463077306F6C7372787073706C596D440A417053624766496E7A726E736A64487A42585A73786C3834703947364B6F4967796158354C6F476F783153532F4D536A62354E566F593130416F6B52642F3349740A6F6A6A68792B56524846364864715A594B37665A4F7A7842564D55374C4D38683647454B2F367761644B69796357666873694E6F5744714A34394A78692B354F0A396143314379475652495651537744414A42674E5648524D45416A41414D41304743537147534962334451454243775541413449424151414A57574E45354E5A520A6F4F48556C674F35535330686D75394F464B4E327742306E33786A3738756F6B77504F6D48646D3062397350694D6D39364456312F415675516E68372F4643520A374238556D576836333769695A7A663573392B51453477394846316B63765936473666737866467873544558684D6A7A6A384432594837717A6A6C5876307536370A775848596C69686C6D33323562306556386A706A6E365742646335634F417839665858764E63444D6A446D6B47503071624557324533317541634A78387644670A444C764B6B4B336E74486675630A2D2D2D2D2D454E442043455254494649434154452D2D2D2D2D0A036E02137443332F303005572D2D2D2D2D424547494E2043455254494649434154452D2D2D2D2D0A4D494944785443434171326741774942416749485752306641414142757A414E42676B71686B69473977304241517346414443426854454C4D416B47413155450A42684D4351303478447A414E42674E564241674D426B5A31535647566A614735766247396E65534244627934735448526B4D524D77455159445651514C4441704F0A5546516754555A4849454E424D524977454159445651514444416C4E526B636755335669513045774868634E4D6A45774E6A45304D4441774D4441775768634E0A4D7A45774E6A45314D4441774D444177576A43426B545373774B5159445651514B44434A4F5A5864735957356B583142686557316C626E52665647566A614735760A6247396E65563944627934735448526B4D526377465159445651514C4441354F554651755647566A61454E6C626E526C636A45614D42674741315545417777520A555463334F5441774F4455304F4459304C5546727470337252646278644462627246424C6E4C3235506635546A5452614F4F32393162363748416C7141755341480A6B2F664C62485471447731306F336C58744B4D56584269385A68674967716A76544A2B4D75317A6E695262454B35716C463077306F6C7372787073706C596D440A417053624766496E7A726E736A64487A42585A73786C3834703947364B6F4967796158354C6F476F783153532F4D536A62354E566F593130416F6B52642F3349740A6F6A6A68792B56524846364864715A594B37665A4F7A7842564D55374C4D38683647454B2F367761644B69796357666873694E6F5744714A34394A78692B354F0A396143314379475652495651537744414A42674E5648524D45416A41414D41304743537147534962334451454243775541413449424151414A57574E45354E5A520A6F4F48556C674F35535330686D75394F464B4E327742306E33786A3738756F6B77504F6D48646D3062397350694D6D39364456312F415675516E68372F4643520A374238556D576836333769695A7A663573392B51453477394846316B63765936473666737866467873544558684D6A7A6A384432594837717A6A6C5876307536370A775848596C69686C6D33323562306556386A706A6E365742646335634F417839665858764E63444D6A446D6B47503071624557324533317541634A78387644670A444C764B6B4B336E74486675630A2D2D2D2D2D454E442043455254494649434154452D2D2D2D2D0A036E"));
                break;
            default:
                break;
        }

        if (responseMessage != null) {
            data = responseMessage.pack();
            // The following is for sticky package test.
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                if (responseMessage.getMessageType().equals(ExternalMessageType.EMV_RESPONSE)) {
                    if (responseMessage.getMessageData()[0] == EmvFunctionId.CALLBACK) {
                        if (responseMessage.getMessageData()[1] == ExternalEmvCallbackID.UI_EVENT) {
                            byte[] temp = ISOUtils.hex2byte("0200294C312F35010053776970652F496E73");
                            outputStream.write(data);
                            outputStream.write(temp);
                            data = outputStream.toByteArray();
                        } else if (responseMessage.getMessageData()[1] == ExternalEmvCallbackID.SELECT_CANDIDATE_LIST) {
                            byte[] temp = ISOUtils.hex2byte("6572742F5461702F4D616E75616C0373");
                            outputStream.write(temp);
                            outputStream.write(data);
                            data = outputStream.toByteArray();
                        } else if (responseMessage.getMessageData()[1] == ExternalEmvCallbackID.CHECK_CREDENTIALS) {
                            byte[] temp = ISOUtils.hex2byte("0200294C312F35010053776970652F496E736572742F5461702F4D616E75616C0373");
                            outputStream.write(temp);
                            outputStream.write(data);
                            data = outputStream.toByteArray();
                        }
                    } else if (responseMessage.getMessageData()[0] == EmvFunctionId.GET_DATA) {
                        byte[] temp = ISOUtils.hex2byte("0200294C312F35010053776970652F496E736572742F5461702F4D616E75616C0373");
                        outputStream.write(temp);
                        outputStream.write(temp);
                        outputStream.write(data);
                        outputStream.write(temp);
                        outputStream.write(temp);
                        data = outputStream.toByteArray();
                    }else if(responseMessage.getMessageData()[0] == EmvFunctionId.GET_AID_NUM){
                        byte[] temp = ISOUtils.hex2byte("" +
                                "0202684C312F133030001500139F061000000000000000000000000000000000000A9F0607A0000003330101000A9F0607A0000000032" +
                                "010000A9F0607A0000000033010000A9F0607A0000000038010000A9F0607A0000000041010000A9F0607A0000000043060000A9F0607" +
                                "A0000000046000000A9F0607A0000000046010000A9F0607A0000000042203000A9F0607A0000000101030000A9F0607A000000324101" +
                                "0000A9F0607A0000001523010000A9F0607A0000001524010000A9F0607A0000000651010000A9F0607A0000006151010000A9F0607A0" +
                                "000005241010000A9F0607A0000002771010000A9F0607A0000000999090000A9F0607A000000003101000099F0606A000000025010388");
                        outputStream.write(temp);
                        outputStream.write(temp);
                        outputStream.write(data);
                        outputStream.write(temp);
                        outputStream.write(temp);
                        data = outputStream.toByteArray();
                    }else if(responseMessage.getMessageData()[0] == EmvFunctionId.GET_CAPK_NUM){
                        byte[] temp = ISOUtils.hex2byte("" +
                                "0203444C312F1430300038A00000000390A00000000392A00000000394A00000000395A00000000396A00000000397A00000000398A00000000399" +
                                "A00000000308A00000000309A00000000352A00000000357A00000000358A00000000405A00000000406A000000004EFA000000004F0A000000004F1" +
                                "A000000004F2A000000004F3A000000004F4A000000004F8A000000004FAA000000010F8A000000004FEA000000010FEA00000006508A0000000650F" +
                                "A00000006511A00000006513A00000006510A00000006512A00000006514A00000033308A00000033309A00000033310A000000333F8A00000033302" +
                                "A00000033303A00000033304A0000000250FA00000002510A00000002562A00000002564A00000002565A00000002566A00000002567A00000002568" +
                                "A00000002504A000000025CAA000000025C9A000000025C8A0000001525AA00000027709A00000027740A0000001525C03BF");
                        outputStream.write(temp);
                        outputStream.write(temp);
                        outputStream.write(data);
                        outputStream.write(temp);
                        outputStream.write(temp);
                        data = outputStream.toByteArray();
                    }
                } else if (responseMessage.getMessageType().equals(ExternalMessageType.GET_VERSION_NUMBER_RESPONSE)) {
                    byte[] temp = ISOUtils.hex2byte("0200294C312F35010053776970652F496E736572742F5461702F4D616E75616C0373");
//                    byte[] temp = ISOUtils.hex2byte("020029");
                    outputStream.write(temp);
                    outputStream.write(temp);
                    outputStream.write(data);
                    outputStream.write(temp);
                    outputStream.write(temp);
                    data = outputStream.toByteArray();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (data != null) {
            System.out.println(String.format("############## Received data: %s", ISOUtils.hexString(data)));
        }

        return data;
    }

    @Override
    public void clear() throws NSDKException {

    }

    private ExternalMessage mockUpdaterResponse(String responseMessageType) {
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(responseMessageType);

        String responseCode = "00";
//        String responseCode = "01";
//        String responseCode = "02";
//        String responseCode = "03";
//        String responseCode = "04";
//        String responseCode = "05";
//        String responseCode = "06";
//        String responseCode = "07";

        responseMessage.setMessageData(responseCode.getBytes());
        return responseMessage;
    }

    private ExternalMessage mockScanningResponse() {
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.SCANNING_RESPONSE);

        String responseCode = "00";
//        String responseCode = "01";
//        String responseCode = "02";
//        String responseCode = "04";
//        String responseCode = "06";

        byte[] data = new byte[]{0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A};

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte functionId = 0x04;
        outputStream.write(functionId);
//        outputStream.write(ContactlessCardFunctionId.ACTIVE_FIELD);
        try {
            outputStream.write(responseCode.getBytes());
            outputStream.write(ExternalMessage.intToHexBuf(data.length));
//            outputStream.write(ExternalMessage.intToHexBuf(0));
//            outputStream.write(ExternalMessage.intToHexBuf(12));
//            outputStream.write(ExternalMessage.intToHexBuf(8));
            outputStream.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        responseMessage.setMessageData(outputStream.toByteArray());
        return responseMessage;
    }

    private ExternalMessage mockCardReadResponse() {
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.CARD_READ_RESPONSE);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        String responseCode = "00";
//        String responseCode = "01";
//        String responseCode = "02";
//        String responseCode = "05";
        byte[] atq = new byte[]{0x11, 0x11, 0x11, 0x11, 0x11};
        byte[] atr = new byte[]{0x22, 0x22, 0x22, 0x22, 0x22};
        byte cardType = 2;
        try {
            outputStream.write(responseCode.getBytes());
            outputStream.write(cardType);
            outputStream.write(ISOUtils.hex2byte("0000024C004233363037303530303030313333355E434152442F494D414745203333202020202020202020202020205E323331323230313132303130303030303030303030303030343032303030303030250033363037303530303030313333353D3233313232303131323031303030343032303030303000000E3336303730353030303031333335063336303730350431333335043233313203323031"));
            if (cardType == 0) {
                outputStream.write(ExternalMessage.intToHexBuf(atq.length));
                outputStream.write(atq);
            }

            if (cardType == 1) {
                outputStream.write(ExternalMessage.intToHexBuf(atr.length));
                outputStream.write(atr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        responseMessage.setMessageData(outputStream.toByteArray());
        return responseMessage;
    }

    private ExternalMessage mockReadMagCardResponse() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.READ_MAG_CARD_RESPONSE);
        String responseCode = "00";
//        String responseCode = "01";
//        String responseCode = "02";
//        String responseCode = "03";
//        String responseCode = "04";
//        String responseCode = "05";
//        String responseCode = "06";
//        String responseCode = "07";
//        String responseCode = "08";
//        String responseCode = "46";

        byte[] pan = new byte[]{0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11};
        String maskedPan = "123456****7890";
        byte[] track1 = new byte[]{0x33, 0x33, 0x33, 0x33, 0x33, 0x33, 0x33, 0x33};
        byte[] track2 = new byte[]{0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44};
        byte[] track3 = new byte[]{0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55};
        try {
            outputStream.write(responseCode.getBytes());
            outputStream.write(pan.length);
            outputStream.write(pan);
            outputStream.write(maskedPan.getBytes());
            outputStream.write(1);
            outputStream.write(0);
            outputStream.write(0);
            outputStream.write(ExternalMessage.intToHexBuf(track1.length));
            outputStream.write(track1);
            outputStream.write(ExternalMessage.intToHexBuf(track2.length));
//            outputStream.write(ExternalMessage.intToHexBuf(0));
            outputStream.write(track2);
            outputStream.write(ExternalMessage.intToHexBuf(track3.length));
//            outputStream.write(ExternalMessage.intToHexBuf(0));
            outputStream.write(track3);
        } catch (Exception e) {
            e.printStackTrace();
        }

        responseMessage.setMessageData(outputStream.toByteArray());
        return responseMessage;
    }

    private ExternalMessage mockGetMagTrackDataResponse() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ExternalMessage responseMessage = new ExternalMessage();
        responseMessage.setMessageType(ExternalMessageType.GET_MAG_TRACK_DATA_RESPONSE);

        byte isReadTrack1 = currentRequestMessage.getMessageData()[3];
        byte isReadTrack2 = currentRequestMessage.getMessageData()[4];
        byte isReadTrack3 = currentRequestMessage.getMessageData()[5];

        String responseCode = "00";
//        String responseCode = "01";
//        String responseCode = "02";
//        String responseCode = "03";
//        String responseCode = "04";
//        String responseCode = "05";
//        String responseCode = "06";
//        String responseCode = "46";

        byte[] pan = new byte[]{0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11};
        String maskedPan = "123456****7890";
        byte[] track1 = new byte[]{0x33, 0x33, 0x33, 0x33, 0x33, 0x33, 0x33, 0x33};
        byte[] track2 = new byte[]{0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44};
        byte[] track3 = new byte[]{0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55};
        try {
            outputStream.write(responseCode.getBytes());
            outputStream.write(pan.length);
            outputStream.write(pan);
            outputStream.write(maskedPan.getBytes());
            outputStream.write(0);
            outputStream.write(1);
            outputStream.write(0);
            if (isReadTrack1 == 1) {
                outputStream.write(ExternalMessage.intToHexBuf(track1.length));
                outputStream.write(track1);
            } else {
                outputStream.write(ExternalMessage.intToHexBuf(0));
            }
            if (isReadTrack2 == 1) {
                outputStream.write(ExternalMessage.intToHexBuf(track2.length));
                outputStream.write(track2);
            } else {
                outputStream.write(ExternalMessage.intToHexBuf(0));
            }
            if (isReadTrack3 == 1) {
                outputStream.write(ExternalMessage.intToHexBuf(track3.length));
                outputStream.write(track3);
            } else {
                outputStream.write(ExternalMessage.intToHexBuf(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        responseMessage.setMessageData(outputStream.toByteArray());
        return responseMessage;
    }
}
