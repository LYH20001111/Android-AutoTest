package com.newland.nsdk.core.external.command.common;

import java.util.ArrayList;
import java.util.List;

public class FileTransferUtil {
    public static List<byte[]> splitData(byte[] data, int maxLenSinglePackage) {
        List<byte[]> dataList = new ArrayList<>();
        int totalLen = data.length;
        int offset = 0;
        byte[] tempBuf;
        int tempBufLen;
        while (offset < totalLen) {
            if (totalLen - offset >= maxLenSinglePackage) {
                tempBufLen = maxLenSinglePackage;
            } else {
                tempBufLen = totalLen - offset;
            }
            tempBuf = new byte[tempBufLen];
            System.arraycopy(data, offset, tempBuf, 0, tempBufLen);
            dataList.add(tempBuf);
            offset += tempBufLen;
        }
        return dataList;
    }
}
