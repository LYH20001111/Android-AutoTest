package com.newland.sdk.module.pin;

/**
 * Created by youjf on 2019/7/25 17:09
 */
public class NCipherExtParams extends CipherExtParams {
    // workingKeyData(Session key) usage(DATA_ENC_ONLY)
    /**
     * Initial Vector.The default is 0x00.<p>
     * 8 bytes for TDES/SM4, 16 bytes for AES.
     */
    private byte[] mInitialVector;
//    /**
//     * The block cipher modes.The default is {@link CipherMode#ECB}.
//     */
//    private CipherMode mCipherMode;
    /**
     * PaddingMode mode for the cipher.No padding by default.
     */
    private PaddingMode.Mode mPaddingMode;
    /**
     * Dukpt key derived mode. The default is BOTH mode.{@link DukptDerivedMode}
     */
    private DukptDerivedMode mDukptDerivedMode;

    /**
     * dukpt aes derivate usage
     */
    private DukptDerivateUsage mDukptDerivateUsage;

    /**
     * dukpt aes derivate key length
     */
    private int derivateKeyLen;

    /**
     * get initial vector.
     * @return
     */
    public byte[] getInitialVector() {
        return mInitialVector;
    }

    /**
     * set initial vector.
     * @param initialVector
     */
    public void setInitialVector(byte[] initialVector) {
        mInitialVector = initialVector;
    }

//    /**
//     * get the cipher mode.{@link CipherMode}
//     * @return
//     */
//    public CipherMode getCipherMode() {
//        return mCipherMode;
//    }
//
//    /**
//     * set the cipher mode.
//     * @param cipherMode {@link CipherMode}
//     */
//    public void setCipherMode(CipherMode cipherMode) {
//        mCipherMode = cipherMode;
//    }

    /**
     * get padding mode.
     * @return
     */
    public PaddingMode.Mode getPaddingMode() {
        return mPaddingMode;
    }

    /**
     * set padding mode.
     * @param paddingMode
     */
    public void setPaddingMode(PaddingMode.Mode paddingMode) {
        mPaddingMode = paddingMode;
    }

    /**
     * get dukpt key derived mode.
     * @return
     */
    public DukptDerivedMode getDukptDerivedMode() {
        return mDukptDerivedMode;
    }

    /**
     * set dukpt key derived mode.
     * @param dukptDerivedMode
     */
    public void setDukptDerivedMode(DukptDerivedMode dukptDerivedMode) {
        this.mDukptDerivedMode = dukptDerivedMode;
    }

    public DukptDerivateUsage getDukptDerivateUsage() {
        return mDukptDerivateUsage;
    }

    /**
     *  set dukpt aes derivate usage
     * @param mDukptDerivateUsage {@link DukptDerivateUsage}
     */
    public void setDukptDerivateUsage(DukptDerivateUsage mDukptDerivateUsage) {
        this.mDukptDerivateUsage = mDukptDerivateUsage;
    }

    public int getDerivateKeyLen() {
        return derivateKeyLen;
    }

    public void setDerivateKeyLen(int derivateKeyLen) {
        this.derivateKeyLen = derivateKeyLen;
    }
}
