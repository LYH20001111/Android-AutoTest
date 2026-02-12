package com.newland.sdk.module.pin;

/**
 * @author youjf
 * @description
 * @date 2019/8/6
 * @since 3.10.01
 */
public class NLoadDuktpExtParams extends LoadDuktpExtParams {

    /**
     * Setting the decryption key type.
     */
    private MasterKeyType mSrcmasterKeyType;
    /**
     *
     */
    private AlgorithmMode algorithmMode;
    /**
     * Dukpt key derived mode. The default is BOTH mode.{@link DukptDerivedMode}
     */
    private DukptDerivedMode mDukptDerivedMode;
    /**
     * Initial Vector.The default is 0x00.
     * 8 bytes for TDES/SM4, 16 bytes for AES.
     */
    private byte[] InitialVector;
    /**
     * The block cipher modes.The default is {@link CipherMode#ECB}.
     */
    private CipherMode mCipherMode;
    /**
     * The PaddingMode mode and key Length.<p>
     *
     * PaddingMode mode for the cipher mode. <p>
     * No padding by default.
     */
    private PaddingMode mPaddingMode;

    /**
     * get dukpt key derived mode.
     * @return {@link DukptDerivedMode}
     */
    public DukptDerivedMode getDukptDerivedMode() {
        return mDukptDerivedMode;
    }

    /**
     * set dukpt key derived mode.
     * @param dukptDerivedMode {@link DukptDerivedMode}
     */
    public void setDukptDerivedMode(DukptDerivedMode dukptDerivedMode) {
        mDukptDerivedMode = dukptDerivedMode;
    }

    /**
     * get initial vector.
     * @return
     */
    public byte[] getInitialVector() {
        return InitialVector;
    }

    /**
     * set initial vector.
     * @param initialVector
     */
    public void setInitialVector(byte[] initialVector) {
        InitialVector = initialVector;
    }

    /**
     * get cipher mode.
     * @return {@link CipherMode}
     */
    public CipherMode getCipherMode() {
        return mCipherMode;
    }

    /**
     * set cipher mode.
     * @param cipherMode {@link CipherMode}
     */
    public void setCipherMode(CipherMode cipherMode) {
        mCipherMode = cipherMode;
    }

    /**
     * get PaddingMode mode and exactly the length of key.
     * @return {@link PaddingMode}
     */
    public PaddingMode getPaddingMode() {
        return mPaddingMode;
    }

    /**
     * set PaddingMode mode and exactly the length of key.
     * @param paddingMode  {@link PaddingMode}
     */
    public void setPaddingMode(PaddingMode paddingMode) {
        mPaddingMode = paddingMode;
    }
    /**
     * get the decryption key type
     * @return  {@link MasterKeyType}
     */
    public MasterKeyType getSrcMasterKeyType() {
        return mSrcmasterKeyType;
    }

    /**
     * Set the decryption key type
     * @param mSrcmasterKeyType {@link MasterKeyType}
     */
    public void setSrcMasterKeyType(MasterKeyType mSrcmasterKeyType) {
        this.mSrcmasterKeyType = mSrcmasterKeyType;
    }

    public AlgorithmMode getAlgorithmMode() {
        return algorithmMode;
    }

    public void setAlgorithmMode(AlgorithmMode algorithmMode) {
        this.algorithmMode = algorithmMode;
    }
}
