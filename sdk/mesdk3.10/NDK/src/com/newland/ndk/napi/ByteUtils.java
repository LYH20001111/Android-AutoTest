package com.newland.ndk.napi;

import java.util.Arrays;

/**
 * @Description
 * @Author wuhh
 * @Date 2020/11/26
 */
public class ByteUtils {
    public static byte[] intToBytes(int value,int len,boolean isBigEndian){
        byte[] bs = new byte[len];
        Arrays.fill(bs, (byte)0x00);
        for(int i = 0; i < len; i++){
            int j = i;
            if(isBigEndian){
                j = len - i -1;
            }
            bs[j] = (byte)((value >> (i * 8)) & 0xff);
        }
        return bs;
    }
}
