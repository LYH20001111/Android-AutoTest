package com.newland.nsdk.externaldevice;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.external.command.message.ExternalMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessageType;

import org.junit.Test;

public class ExternalMessageTest {

    @Test
    public void calculateLrcTest() {
        byte[] testBuf = new byte[]{0x11, 0x03, (byte) 0xF1, (byte) 0x98, (byte) 0xE7, (byte) 0x99, 0x32};
        try {
            byte result = ExternalMessage.calculateLrc(testBuf, 2, 5);
            System.out.println(bytes2HexString(testBuf));
            System.out.println("Result(2-5):" + String.format("%02X ", result));
            result = ExternalMessage.calculateLrc(testBuf, 0, testBuf.length - 1);
            System.out.println("Result(0-6):" + String.format("%02X ", result));
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void toBcdLenTest() {
        int len = 205;
        byte[] result = new byte[0];
        try {
            result = ExternalMessage.intToBcdBuffer(len);
        } catch (NSDKIllegalParameterException e) {
            e.printStackTrace();
        }
        System.out.println(bytes2HexString(result));
        String serialNumber = "123456";
        System.out.println(ISOUtils.hexString(String.format("%-16s", serialNumber).getBytes()));
    }

    @Test
    public void bcdLen2IntTest() {
        byte[] buf = new byte[]{0x00, (byte) 0x38};
        int result = ExternalMessage.bcdBuffer2Int(buf);
        System.out.println("Result: " + result);

        buf = new byte[]{0x02, 0x05};
        result = ExternalMessage.bcdBuffer2Int(buf);
        System.out.println("Result: " + result);
    }

    @Test
    public void packTest() {
        ExternalMessage scanRequestMessage = new ExternalMessage();
        scanRequestMessage.setMessageType(ExternalMessageType.SCANNING_REQUEST);
        scanRequestMessage.setMessageData(new byte[]{0x04, 0x00, 0x0A});

        ExternalMessage updateAppRequestMessage = new ExternalMessage();
        updateAppRequestMessage.setMessageType(ExternalMessageType.CLEAR_SCREEN_REQUEST);

        try {
            System.out.println(bytes2HexString(scanRequestMessage.pack()));
            System.out.println(bytes2HexString(updateAppRequestMessage.pack()));
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void unpackTest() {
        byte[] responseData = new byte[]{0x02, 0x00, 0x49, 0x64, 0x38, 0x2F, 0x00, (byte) 0x81, 0x01,
                0x13, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x01, 0x02, 0x03, 0x04,
                0x05, 0x06, 0x07, 0x08, 0x09, 0x01, 0x08, 0x00, 0x60, 0x50, 0x6C, 0x65, 0x61, 0x73,
                0x65, 0x20, 0x49, 0x6E, 0x70, 0x75, 0x74, 0x20, 0x50, 0x49, 0x4E, 0x1C, 0x1C, 0x1C, 0x1C,
                0x03, (byte) 0xEC};
        try {
            ExternalMessage responseMessage = ExternalMessage.unpack(responseData);
            System.out.println(responseMessage.getMessageType());

            // 英文显示"3C"
            responseData = new byte[]{0x02, 0x00, 0x38, 0x33, 0x43, 0x2F, 0x77, 0x65, 0x6C, 0x63, 0x6F,
                    0x6D, 0x65, 0x77, 0x65, 0x6C, 0x63, 0x6F, 0x6D, 0x65, 0x77, 0x65, 0x6C, 0x63, 0x6F,
                    0x6D, 0x65, 0x77, 0x65, 0x6C, 0x63, 0x6F, 0x6D, 0x65, 0x77, 0x65, 0x6C, 0x63, 0x6F,
                    0x6D, 0x65, 0x03, 0x1E};
            responseMessage = ExternalMessage.unpack(responseData);
            System.out.println(responseMessage.getMessageType());

            // 清屏"4e"
            responseData = new byte[]{0x02, 0x00, 0x03, 0x34, 0x65, 0x2F, 0x03, 0x7E};
            responseMessage = ExternalMessage.unpack(responseData);
            System.out.println(responseMessage.getMessageType());
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    public static String bytes2HexString(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            sb.append(String.format("%02X", data[i]));
        }

        return sb.toString();
    }
}