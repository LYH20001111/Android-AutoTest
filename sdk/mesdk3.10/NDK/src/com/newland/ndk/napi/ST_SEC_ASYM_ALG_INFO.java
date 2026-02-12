package com.newland.ndk.napi;

import java.util.Arrays;

/**
 * @Description
 * @Author wuhh
 * @Date 2020/11/26
 */
public class ST_SEC_ASYM_ALG_INFO {
    public int unBit;
    public byte[] ucRsaPubExp;
    public int getSize(){
        return 9;
    }
    public byte[] getData(){
        byte[] data = new byte[getSize()];
        Arrays.fill(data, (byte) 0);
        byte[] unBitFb = ByteUtils.intToBytes(unBit,4,false);
        System.arraycopy(unBitFb,0,data,0,unBitFb.length);
        System.arraycopy(ucRsaPubExp,0,data,unBitFb.length,ucRsaPubExp.length);
        return data;
    }
}
