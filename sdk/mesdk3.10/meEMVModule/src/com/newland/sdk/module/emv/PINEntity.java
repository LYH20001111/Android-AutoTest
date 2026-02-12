package com.newland.sdk.module.emv;

public class PINEntity {

    private PinRequiredType pinRequiredType;
    private int offlinePinTimes;
    private byte[] modulus;
    private byte[] exponent;


    public PINEntity(PinRequiredType pinRequiredType, int offlinePinTimes, byte[] modulus, byte[] exponent) {
        this.pinRequiredType = pinRequiredType;
        this.offlinePinTimes = offlinePinTimes;
        this.modulus = modulus;
        this.exponent = exponent;
    }

    /**
     * Get the PIN required type returned by emv kernel.
     * @return
     */
    public PinRequiredType getPinRequiredType() {
        return pinRequiredType;
    }

    /**
     * Get the number of remained offline PIN retries.
     * @return
     */
    public int getOfflinePinTimes() {
        return offlinePinTimes;
    }

    /**
     * <p>Modulus of public key.</p>
     * <p>This parameter needs to be used when the transaction require an offline encrypted PIN.</p>
     * <p>This parameter will be null when the transaction require an offline plaintext PIN.</p>
     * @return
     */
    public byte[] getModulus() {
        return modulus;
    }

    /**
     * <p>Exponent of public key.</p>
     * <p>This parameter needs to be used when the transaction require an offline encrypted PIN.</p>
     * <p>This parameter will be null when the transaction require an offline plaintext PIN.</p>
     * @return
     */
    public byte[] getExponent() {
        return exponent;
    }

}
