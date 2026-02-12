package com.newland.nsdk.core.api.common.utils;

import java.util.ArrayList;
import java.util.List;

public class TLVUtils {
    private static final String TAG = "TLVUtils";
    public static List<TLVElement> getTLVElements(byte[] inputData) {
        List<TLVElement> tlvElementList = new ArrayList<>();
        int tag = 0;
        int tagLen = 0;
        int current = 0;
        int lenValue = 0;

        while (current < inputData.length) {
            tagLen = getTagLen(inputData, current);
            byte[] tagBytes = new byte[tagLen];
            System.arraycopy(inputData, current, tagBytes, 0, tagLen);
            tag = bytesToInt(tagBytes);
            current += tagLen;
            if ((inputData[current] & 0x80) == 0x80) {
                int tempLen = inputData[current] & 0x7F;
                switch (tempLen) {
                    case 1:
                        lenValue = inputData[current + 1] & 0xFF;
                        break;
                    case 2:
                        lenValue = (inputData[current + 1] << 8) & 0xFF00 + (inputData[current + 2] & 0xFF);
                        break;
                    case 3:
                        lenValue = (inputData[current + 1] << 16) & 0xFF0000 + (inputData[current + 2] << 8) & 0xFF00 + (inputData[current + 3] & 0xFF);
                        break;
                }
                current += tempLen + 1;
            } else {
                lenValue = inputData[current] & 0xFF;
                current++;
            }
            byte[] value = new byte[lenValue];
            System.arraycopy(inputData, current, value, 0, lenValue);
            current += lenValue;
            TLVElement tlvElement = new TLVElement();
            tlvElement.setTag(tag);
            tlvElement.setLen(lenValue);
            tlvElement.setValue(value);
            tlvElementList.add(tlvElement);
        }
        return tlvElementList;
    }

    private static int getTagLen(byte[] input, int offset) {
        int tagLen = 0;
        if ((input[offset] & 0x1F) != 0x1F) {
            tagLen = 1;
        } else {
            for (int i = 1; i < 4; i++) {
                if ((input[offset + i] & 0x80) != 0x80) {
                    tagLen = i + 1;
                    break;
                }
            }
        }
        return tagLen;
    }

    private static int bytesToInt(byte[] bytes) {
        int result = 0;
        int end = bytes.length;

        for (int i = end - 1; i >= 0; --i) {
            byte b = bytes[i];
            int leftBit = (end - 1 - i) * 8;
            result |= (b & 255) << leftBit;
        }

        return result;
    }
}
