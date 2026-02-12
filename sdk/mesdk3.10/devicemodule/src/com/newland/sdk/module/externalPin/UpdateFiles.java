package com.newland.sdk.module.externalPin;

/**
 * @Description
 * @Author Denise
 * @Date 2024/04/15 10:24
 */
public class UpdateFiles {
    private byte[] applicationFile;
    private byte[] firmwareFile;

    public byte[] getApplicationFile() {
        return applicationFile;
    }

    public void setApplicationFile(byte[] applicationFile) {
        this.applicationFile = applicationFile;
    }

    public byte[] getFirmwareFile() {
        return firmwareFile;
    }

    public void setFirmwareFile(byte[] firmwareFile) {
        this.firmwareFile = firmwareFile;
    }
}
