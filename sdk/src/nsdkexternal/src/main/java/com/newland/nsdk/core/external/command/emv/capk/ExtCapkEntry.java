package com.newland.nsdk.core.external.command.emv.capk;

/**
 * @author Helen
 * @date 2021/6/28
 */
public class ExtCapkEntry {
    public static int CAPK_LEN = 284;
    private byte[] modulus;
    private byte[] exponent;
    private byte[] hash;
    private byte[] expiredDate;
    private byte[] rid;
    private int index;
    private byte algorithmIndicator;
    private byte hashAlgorithm;

    public ExtCapkEntry(){

    }

    public ExtCapkEntry(byte[] modulus,byte[] exponent,byte[] hash,byte[] expiredDate,
                   byte[] rid,int index,byte algorithmIndicator,byte hashAlgorithm){
        this.modulus = modulus;
        this.exponent = exponent;
        this.hash = hash;
        this.expiredDate = expiredDate;
        this.rid = rid;
        this.index = index;
        this.algorithmIndicator = algorithmIndicator;
        this.hashAlgorithm = hashAlgorithm;
    }

    public void setAlgorithmIndicator(byte algorithmIndicator) {
        this.algorithmIndicator = algorithmIndicator;
    }

    public byte getAlgorithmIndicator() {
        return algorithmIndicator;
    }

    public void setExpiredDate(byte[] expiredDate) {
        this.expiredDate = expiredDate;
    }

    public byte[] getExpiredDate() {
        return expiredDate;
    }

    public void setExponent(byte[] exponent) {
        this.exponent = exponent;
    }

    public byte[] getExponent() {
        return exponent;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public byte[] getHash() {
        return hash;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setRid(byte[] rid) {
        this.rid = rid;
    }

    public byte[] getRid() {
        return rid;
    }

    public void setModulus(byte[] modulus) {
        this.modulus = modulus;
    }

    public byte[] getModulus() {
        return modulus;
    }

    public void setHashAlgorithm(byte hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    public byte getHashAlgorithm() {
        return hashAlgorithm;
    }
}
