package com.newland.nsdk.core.api.external.devicemanager;

/**
 * Files to update.
 */
public class UpdateFiles {
    private byte[] applicationFile;
    private byte[] firmwareFile;

    /**
     * Gets application file data.
     *
     * @return Application file data.
     */
    public byte[] getApplicationFile() {
        return applicationFile;
    }

    /**
     * Sets application file data.
     *
     * @param applicationFile Application file data.
     */
    public void setApplicationFile(byte[] applicationFile) {
        this.applicationFile = applicationFile;
    }

    /**
     * Gets firmware file data.
     *
     * @return Firmware file data.
     */
    public byte[] getFirmwareFile() {
        return firmwareFile;
    }

    /**
     * Sets firmware file data.
     *
     * @param firmwareFile Firmware file data.
     */
    public void setFirmwareFile(byte[] firmwareFile) {
        this.firmwareFile = firmwareFile;
    }
}
