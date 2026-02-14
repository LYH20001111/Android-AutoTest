package com.newland.nsdk.core.api.common.card.magcard;

import android.util.Log;

/**
 * Magnetic stripe card info.
 */
public class MagCardInfo {
    private TrackStatus[] trackStatus = new TrackStatus[6];
    private TrackDataFormat[] trackFormats = new TrackDataFormat[6];
    private byte[] track1Data;
    private byte[] track2Data;
    private byte[] track3Data;
    private byte[] track4Data;
    private byte[] track5Data;
    private byte[] track6Data;
    private byte[] panData;
    private String firstClearPAN;
    private String lastClearPAN;
    private int plainPANLen;
    private int plainTrack1DataLen;
    private int plainTrack2DataLen;
    private int plainTrack3DataLen;
    private int plainTrack4DataLen;
    private int plainTrack5DataLen;
    private int plainTrack6DataLen;
    private String validDate;
    private String serviceCode;
    private byte[] hash;

    /**
     * Gets track 1 data.
     *
     * @return Track 1 data. It could be plaintext or ciphertext.
     */
    public byte[] getTrack1Data() {
        return track1Data;
    }

    /**
     * Sets track 1 data.
     *
     * @param track1Data Track 1 data. It could be plaintext or ciphertext.
     */
    public void setTrack1Data(byte[] track1Data) {
        this.track1Data = track1Data;
    }

    /**
     * Gets track 2 data.
     *
     * @return Track 2 data.It could be plaintext or ciphertext.
     */
    public byte[] getTrack2Data() {
        return track2Data;
    }

    /**
     * Sets track 2 data.
     *
     * @param track2Data Track 2 data. It could be plaintext or ciphertext.
     */
    public void setTrack2Data(byte[] track2Data) {
        this.track2Data = track2Data;
    }

    /**
     * Gets track 3 data.
     *
     * @return Track 3 data. It could be plaintext or ciphertext.
     */
    public byte[] getTrack3Data() {
        return track3Data;
    }

    /**
     * Sets track 3 data.
     *
     * @param track3Data Track 3 data. It could be plaintext or ciphertext.
     */
    public void setTrack3Data(byte[] track3Data) {
        this.track3Data = track3Data;
    }

    /**
     * Gets track 4 data.
     *
     * @return Track 4 data. It could be plaintext or ciphertext.
     */
    public byte[] getTrack4Data() {
        return track4Data;
    }

    /**
     * Sets track 4 data.
     *
     * @param track4Data Track 4 data. It could be plaintext or ciphertext.
     */
    public void setTrack4Data(byte[] track4Data) {
        this.track4Data = track4Data;
    }

    /**
     * Gets track 5 data.
     *
     * @return Track 5 data. It could be plaintext or ciphertext.
     */
    public byte[] getTrack5Data() {
        return track5Data;
    }

    /**
     * Sets track 5 data.
     *
     * @param track5Data Track 5 data. It could be plaintext or ciphertext.
     */
    public void setTrack5Data(byte[] track5Data) {
        this.track5Data = track5Data;
    }

    /**
     * Gets track 6 data.
     *
     * @return Track 6 data. It could be plaintext or ciphertext.
     */
    public byte[] getTrack6Data() {
        return track6Data;
    }

    /**
     * Sets track 6 data.
     *
     * @param track6Data Track 6 data. It could be plaintext or ciphertext.
     */
    public void setTrack6Data(byte[] track6Data) {
        this.track6Data = track6Data;
    }

    /**
     * Gets PAN data.
     *
     * @return PAN data.
     * <ul>
     *     <li>PAN data is plaintext returned by internal card reader.</li>
     *     <li>PAN data could be encrypted when using external card reader.</li>
     * </ul>
     */
    public byte[] getPanData() {
        return panData;
    }

    /**
     * Sets PAN data.
     *
     * @param panData PAN data.
     *                <ul>
     *                    <li>PAN data is plaintext returned by internal card reader.</li>
     *                    <li>PAN data could be encrypted when using external card reader.</li>
     *                </ul>
     */
    public void setPanData(byte[] panData) {
        this.panData = panData;
    }

    /**
     * Gets first clear part of masked PAN.
     *
     * @return First clear part of masked PAN. This only returned when PAN data is encrypted.
     */
    public String getFirstClearPAN() {
        return firstClearPAN;
    }

    /**
     * Sets first clear part of masked PAN.
     *
     * @param firstClearPAN First clear part of masked PAN. This only returned when PAN data is encrypted.
     */
    public void setFirstClearPAN(String firstClearPAN) {
        this.firstClearPAN = firstClearPAN;
    }

    /**
     * Gets last clear part of masked PAN.
     *
     * @return Last clear part of masked PAN. This only returned when PAN data is encrypted.
     */
    public String getLastClearPAN() {
        return lastClearPAN;
    }

    /**
     * Sets last clear part of masked PAN.
     *
     * @param lastClearPAN Last clear part of masked PAN. This only returned when PAN data is encrypted.
     */
    public void setLastClearPAN(String lastClearPAN) {
        this.lastClearPAN = lastClearPAN;
    }

    /**
     * Gets length of plain PAN.
     *
     * @return Length of plain PAN.
     */
    public int getPlainPANLen() {
        return plainPANLen;
    }

    /**
     * Sets length of plain PAN.
     *
     * @param plainPANLen Length of plain PAN.
     */
    public void setPlainPANLen(int plainPANLen) {
        this.plainPANLen = plainPANLen;
    }

    /**
     * Gets the length of plain track 1 data.
     *
     * @return The length of plain track 1 data.
     */
    public int getPlainTrack1DataLen() {
        return plainTrack1DataLen;
    }

    /**
     * Sets the length of plain track 1 data.
     *
     * @param plainTrack1DataLen The length of plain track 1 data.
     */
    public void setPlainTrack1DataLen(int plainTrack1DataLen) {
        this.plainTrack1DataLen = plainTrack1DataLen;
    }

    /**
     * Gets the length of plain track 2 data.
     *
     * @return The length of plain track 2 data.
     */
    public int getPlainTrack2DataLen() {
        return plainTrack2DataLen;
    }

    /**
     * Sets the length of plain track 2 data.
     *
     * @param plainTrack2DataLen The length of plain track 2 data.
     */
    public void setPlainTrack2DataLen(int plainTrack2DataLen) {
        this.plainTrack2DataLen = plainTrack2DataLen;
    }

    /**
     * Gets the length of plain track 3 data.
     *
     * @return The length of plain track 3 data.
     */
    public int getPlainTrack3DataLen() {
        return plainTrack3DataLen;
    }

    /**
     * Sets the length of plain track 3 data.
     *
     * @param plainTrack3DataLen The length of plain track 3 data.
     */
    public void setPlainTrack3DataLen(int plainTrack3DataLen) {
        this.plainTrack3DataLen = plainTrack3DataLen;
    }

    /**
     * Gets the length of plain track 4 data.
     *
     * @return The length of plain track 4 data.
     */
    public int getPlainTrack4DataLen() {
        return plainTrack4DataLen;
    }

    /**
     * Sets the length of plain track 4 data.
     *
     * @param plainTrack4DataLen The length of plain track 4 data.
     */
    public void setPlainTrack4DataLen(int plainTrack4DataLen) {
        this.plainTrack4DataLen = plainTrack4DataLen;
    }

    /**
     * Gets the length of plain track 5 data.
     *
     * @return The length of plain track 5 data.
     */
    public int getPlainTrack5DataLen() {
        return plainTrack5DataLen;
    }

    /**
     * Sets the length of plain track 5 data.
     *
     * @param plainTrack5DataLen The length of plain track 5 data.
     */
    public void setPlainTrack5DataLen(int plainTrack5DataLen) {
        this.plainTrack5DataLen = plainTrack5DataLen;
    }

    /**
     * Gets the length of plain track 6 data.
     *
     * @return The length of plain track 6 data.
     */
    public int getPlainTrack6DataLen() {
        return plainTrack6DataLen;
    }

    /**
     * Sets the length of plain track 6 data.
     *
     * @param plainTrack6DataLen The length of plain track 6 data.
     */
    public void setPlainTrack6DataLen(int plainTrack6DataLen) {
        this.plainTrack6DataLen = plainTrack6DataLen;
    }

    /**
     * Gets valid date of the card.
     *
     * @return Valid date of the card.
     */
    public String getValidDate() {
        return validDate;
    }

    /**
     * Sets valid date of the card.
     *
     * @param validDate Valid date of the card.
     */
    public void setValidDate(String validDate) {
        this.validDate = validDate;
    }

    /**
     * Gets service code of the card.
     *
     * @return Service code of the card.
     */
    public String getServiceCode() {
        return serviceCode;
    }

    /**
     * Sets service code of the card.
     *
     * @param serviceCode Service code of the card.
     */
    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

//    public byte[] getHash() {
//        return hash;
//    }
//
//    public void setHash(byte[] hash) {
//        this.hash = hash;
//    }


    /**
     * Gets status of three tracks.
     *
     * @return Status of three tracks.
     */
    public TrackStatus[] getTrackStatus() {
        return trackStatus;
    }

    /**
     * Sets status of three tracks.
     *
     * @param trackStatus Status of three tracks.
     */
    public void setTrackStatus(byte[] trackStatus) {

        TrackStatus[] tmp = new TrackStatus[]{TrackStatus.GOOD, TrackStatus.BAD, TrackStatus.EMPTY};

        for (int i = 0; i < 6; i++) {
            this.trackStatus[i] = tmp[trackStatus[i]];
        }
    }

    public TrackDataFormat[] getTrackFormats() {
        return trackFormats;
    }

    public void setTrackFormats(byte[] trackFormats) {
        TrackDataFormat[] tmp = new TrackDataFormat[] {TrackDataFormat.ISO, TrackDataFormat.JIS, TrackDataFormat.UNKNOWN};
        for (int i = 0; i < 6; i++) {
            if (trackFormats[i] == -1) {
                this.trackFormats[i] = TrackDataFormat.UNKNOWN;
            } else {
                this.trackFormats[i] = tmp[trackFormats[i]];
            }
        }
    }
}
