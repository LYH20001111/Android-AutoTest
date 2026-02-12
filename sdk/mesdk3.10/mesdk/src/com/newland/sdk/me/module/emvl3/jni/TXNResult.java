package com.newland.sdk.me.module.emvl3.jni;

/**
 * @Description
 * @Author wuhh
 * @Date 2020/4/9
 */
public class TXNResult {
    public int returnCode;
    public int errorCode;
    public int resultCode;
    public int cvmStatus;
    public int flag1F8131;//in
    public int keyIndex;//in
    public int actualTlvLen;//The actual length of the data before use CBC to encrypt
    public int tlvLen;
    public byte[] tlvData = new byte[1536];

    public int cardSchemeId;
    public int l3TlvLen;//4 B
    public byte[] l3TlvData = new byte[1536];
}
