package com.newland.nsdk.core.api.external.pinentry;

/**
 * RSA key used to verify offline PIN.
 */
public class RSAKey {
    private byte[] modulus;
    private byte[] exponent;

    /**
     * Gets modulus.
     *
     * @return Modulus.
     */
    public byte[] getModulus() {
        return modulus;
    }

    /**
     * Sets modulus.
     *
     * @param modulus Modulus.
     */
    public void setModulus(byte[] modulus) {
        this.modulus = modulus;
    }

    /**
     * Gets exponent.
     *
     * @return Exponent.
     */
    public byte[] getExponent() {
        return exponent;
    }

    /**
     * Sets exponent.
     *
     * @param exponent Exponent.
     */
    public void setExponent(byte[] exponent) {
        this.exponent = exponent;
    }
}
