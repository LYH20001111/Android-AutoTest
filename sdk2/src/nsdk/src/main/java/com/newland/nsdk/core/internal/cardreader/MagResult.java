package com.newland.nsdk.core.internal.cardreader;

/**
 * SwipeResult <p>
 * Author by liudan, Date on 2020/1/19.
 */
public class MagResult {

    private byte[] account = new byte[20];

    private byte accountLen;

    private byte[] accountHash = new byte[20];

    private byte trackIndicatingbit;

    private byte firstTrackLen;
    private byte[] firstTrackData = new byte[256];

    private byte secondTrackLen;
    private byte[] secondTrackData = new byte[256];

    private byte thirdTrackLen;
    private byte[] thirdTrackData = new byte[256];

    private byte fourthTrackLen;
    private byte[] fourthTrackData = new byte[256];

    private byte fifthTrackLen;
    private byte[] fifthTrackData = new byte[256];

    private byte sixthTrackLen;
    private byte[] sixthTrackData = new byte[256];

    private byte[] validDate = new byte[4];

    private byte[] serviceCode = new byte[3];

    private byte[] trackStatus = new byte[6];

    private byte[] trackFormats = new byte[6];

    public byte getAccountLen() {
        return accountLen;
    }

    public void setAccountLen(byte accountLen) {
        this.accountLen = accountLen;
    }


    public byte[] getAccount() {
        return account;
    }

    public byte[] getAccountHash() {
        return accountHash;
    }

    public byte getTrackIndicatingbit() {
        return trackIndicatingbit;
    }

    public byte getFirstTrackLen() {
        return firstTrackLen;
    }

    public byte[] getFirstTrackData() {
        return firstTrackData;
    }

    public byte getSecondTrackLen() {
        return secondTrackLen;
    }

    public byte[] getSecondTrackData() {
        return secondTrackData;
    }

    public byte getThirdTrackLen() {
        return thirdTrackLen;
    }

    public byte[] getThirdTrackData() {
        return thirdTrackData;
    }

    public byte getFourthTrackLen() {
        return fourthTrackLen;
    }

    public byte[] getFourthTrackData() {
        return fourthTrackData;
    }

    public byte getFifthTrackLen() {
        return fifthTrackLen;
    }

    public byte[] getFifthTrackData() {
        return fifthTrackData;
    }

    public byte getSixthTrackLen() {
        return sixthTrackLen;
    }

    public byte[] getSixthTrackData() {
        return sixthTrackData;
    }

    public byte[] getValidDate() {
        return validDate;
    }

    public byte[] getServiceCode() {
        return serviceCode;
    }

    public byte[] getTrackStatus() {
        return trackStatus;
    }

    public byte[] getTrackFormats() {
        return trackFormats;
    }

    public void setTrackStatus(byte[] trackStatus) {
        this.trackStatus = trackStatus;
    }
}
