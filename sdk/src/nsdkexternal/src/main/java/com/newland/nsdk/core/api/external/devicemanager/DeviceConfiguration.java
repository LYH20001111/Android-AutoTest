package com.newland.nsdk.core.api.external.devicemanager;

/**
 * Device configuration.
 */
public class DeviceConfiguration {
    private BaudRateMode baudRateMode;
    private DecryptionMode workingKeyDecryptionMode;
    private BeeperControl beeperControl;

    /**
     * Creates an instance without parameters.
     */
    public DeviceConfiguration() {
    }

    /**
     * Creates an instance with specified parameters.
     *
     * @param baudRateMode             Baud rate mode. See {@link BaudRateMode}
     * @param workingKeyDecryptionMode <b>[Not yet supported]</b> Working key decryption mode. See {@link DecryptionMode}
     * @param beeperControl            <b>[Not yet supported]</b> Control when to beep. See {@link BeeperControl}
     */
    public DeviceConfiguration(BaudRateMode baudRateMode, DecryptionMode workingKeyDecryptionMode, BeeperControl beeperControl) {
        this.baudRateMode = baudRateMode;
        this.workingKeyDecryptionMode = workingKeyDecryptionMode;
        this.beeperControl = beeperControl;
    }

    /**
     * Gets baud rate mode.
     *
     * @return Baud rate mode. See {@link BaudRateMode}
     */
    public BaudRateMode getBaudRateMode() {
        return baudRateMode;
    }

    /**
     * Sets baud rate mode.
     *
     * @param baudRateMode Baud rate mode. See {@link BaudRateMode}
     */
    public void setBaudRateMode(BaudRateMode baudRateMode) {
        this.baudRateMode = baudRateMode;
    }

    /**
     * Gets working key decryption mode.
     *
     * @return Working key decryption mode. See {@link DecryptionMode}
     */
    public DecryptionMode getWorkingKeyDecryptionMode() {
        return workingKeyDecryptionMode;
    }

    /**
     * Sets working key decryption mode.
     *
     * @param workingKeyDecryptionMode <b>[Not yet supported]</b> Working key decryption mode. See {@link DecryptionMode}
     */
    public void setWorkingKeyDecryptionMode(DecryptionMode workingKeyDecryptionMode) {
        this.workingKeyDecryptionMode = workingKeyDecryptionMode;
    }

    /**
     * Gets beeper control mode.
     *
     * @return Beeper control mode. See {@link BeeperControl}
     */
    public BeeperControl getBeeperControl() {
        return beeperControl;
    }

    /**
     * Sets beeper control mode.
     *
     * @param beeperControl <b>[Not yet supported]</b> Beeper control mode. See {@link BeeperControl}
     */
    public void setBeeperControl(BeeperControl beeperControl) {
        this.beeperControl = beeperControl;
    }
}
