package com.newland.sdk.me.module.emvl3.jni;

import com.newland.sdk.module.emv.CAPK;

/**
 * @Description
 * @Author wuhh
 * @Date 2019/12/30
 */
public class EntryKPAC {
    public byte[] pkModulus = new byte[248];
    public byte   pkModulusLen;
    public byte[] pkExponent = new byte[3];
    public byte[] hashValue = new byte[20];
    public byte[] expiredDate = new byte[4];
    public byte[] rid = new byte[5];
    public byte	  index;
    public byte   pkAlgorithmIndicator;
    public byte   hashAlgorithmIndicator;
    public byte[] rfu = new byte[4];
}
