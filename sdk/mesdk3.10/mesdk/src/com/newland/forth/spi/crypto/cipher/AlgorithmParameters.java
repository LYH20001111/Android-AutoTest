package com.newland.forth.spi.crypto.cipher;

import com.newland.forth.spi.crypto.keystore.CipherMode;
import com.newland.forth.spi.crypto.keystore.DukptDerivedMode;
import com.newland.sdk.module.pin.DukptDerivateUsage;

/**
 * The type Algorithm parameters.
 */
public class AlgorithmParameters {
    /**
     * The Padding mode.
     */
    private PaddingMode paddingMode;
    /**
     * The Mac mode.
     */
    private MacMode macMode;

    /**
     * The Cipher mode.
     */
    private CipherMode cipherMode;

    /**
     * Dukpt Derived mode
     */
    private DukptDerivedMode dukptDerivedMode;

    /**
     * Dukpt derivate usage
     */
    private DukptDerivateUsage dukptDerivateUsage;

    /**
     *
     */
    private int derivateKeyLen;

    public PaddingMode getPaddingMode() {
        return paddingMode;
    }

    public void setPaddingMode(PaddingMode paddingMode) {
        this.paddingMode = paddingMode;
    }

    public MacMode getMacMode() {
        return macMode;
    }

    public void setMacMode(MacMode macMode) {
        this.macMode = macMode;
    }

    public CipherMode getCipherMode() {
        return cipherMode;
    }

    public void setCipherMode(CipherMode cipherMode) {
        this.cipherMode = cipherMode;
    }

    public DukptDerivedMode getDukptDerivedMode() {
        return dukptDerivedMode;
    }

    public void setDukptDerivedMode(DukptDerivedMode dukptDerivedMode) {
        this.dukptDerivedMode = dukptDerivedMode;
    }

    public DukptDerivateUsage getDukptDerivateUsage() {
        return dukptDerivateUsage;
    }

    public void setDukptDerivateUsage(DukptDerivateUsage dukptDerivateUsage) {
        this.dukptDerivateUsage = dukptDerivateUsage;
    }

    public int getDerivateKeyLen() {
        return derivateKeyLen;
    }

    public void setDerivateKeyLen(int derivateKeyLen) {
        this.derivateKeyLen = derivateKeyLen;
    }
}
