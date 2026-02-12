package com.newland.nsdk.core.api.common.keymanager;

import com.newland.nsdk.core.api.common.crypto.MessageDigestType;

/**
 * KDF info.
 */
public class KDFInfo {
    private KDFType kdfType;
    private MessageDigestType messageDigestType;
    private byte[] salt;
    private byte ucSaltLen;
    private byte[] info;
    private byte ucInfoLen;

    public MessageDigestType getMessageDigestType() {
        return messageDigestType;
    }

    public void setMessageDigestType(MessageDigestType messageDigestType) {
        this.messageDigestType = messageDigestType;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public byte[] getInfo() {
        return info;
    }

    public void setInfo(byte[] info) {
        this.info = info;
    }

    public KDFType getKDFType() {
        return kdfType;
    }

    public void setKDFType(KDFType kdfType) {
        this.kdfType = kdfType;
    }

    public void setUcInfoLen(byte ucInfoLen) {
        this.ucInfoLen = ucInfoLen;
    }

    public void setUcSaltLen(byte ucSaltLen) {
        this.ucSaltLen = ucSaltLen;
    }

    public byte getUcSaltLen() {
        return ucSaltLen;
    }

    public byte getUcInfoLen() {
        return ucInfoLen;
    }
}
