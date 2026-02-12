package com.newland.sdk.module.pin;

public class TusnData {
    /**
     * device type
     */
    private String deviceType;
    /**
     * device serial number
     */
    private String sn;
    /**
     * device serial number ciphertext
     */
    private String encryptedData;

    public TusnData(String deviceType, String sn, String encryptedData) {
        this.deviceType = deviceType;
        this.sn = sn;
        this.encryptedData = encryptedData;
    }

    /**
     * get the device serial number
     *
     * @return
     */
    public String getSn() {
        return sn;
    }

    /**
     * get the device serial number ciphertext
     *
     * @return
     */
    public String getEncryptedData() {
        return encryptedData;
    }

    /**
     * get device type
     *
     * @return
     */
    public String getDeviceType() {
        return deviceType;
    }
}
