package com.newland.nsdk.core.api.external.pinentry;

/**
 * Parameters for offline PIN entry.
 */
public class ExtOfflinePINParameters extends ExtPINEntryParameters{
    private boolean isRandomProtectMode;
    private byte[] modulus;
    private byte[] exponent;
    /**
     * Whether to use random key or not.
     *
     * @return Random key flag.
     * <ul>
     *     <li>'true': Use random key derived by the specified KEK. Key index shall be KEK index which is used to derive random PIN key.</li>
     *     <li>'false': Use loaded PIN key. Key index shall be PIN key index.</li>
     * </ul>
     */
    public boolean isRandomProtectMode() {
        return isRandomProtectMode;
    }

    /**
     * Sets whether to use random key or not.
     *
     * @param randomProtectMode Random key flag.
     *                          <ul>
     *                              <li>'true': Use random key derived by the specified KEK. Key index shall be KEK index which is used to derive random PIN key.</li>
     *                              <li>'false': Use loaded PIN key. Key index shall be PIN key index.</li>
     *                          </ul>
     */
    public void setRandomProtectMode(boolean randomProtectMode) {
        isRandomProtectMode = randomProtectMode;
    }

    public byte[] getModulus() {
        return modulus;
    }

    public void setModulus(byte[] modulus) {
        this.modulus = modulus;
    }

    public byte[] getExponent() {
        return exponent;
    }

    public void setExponent(byte[] exponent) {
        this.exponent = exponent;
    }
}
