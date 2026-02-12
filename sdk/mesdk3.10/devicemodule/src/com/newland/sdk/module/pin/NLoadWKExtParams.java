package com.newland.sdk.module.pin;

/**
 * Created by youjf on 2019/7/26 11:15
 */
public class NLoadWKExtParams extends LoadWKExtParams {
    /**
     * the master key type.{@link MasterKeyType}
     */
    private MasterKeyType masterKeyType;
    /**
     * The initial vector of the encryption mode.
     */
    private byte[] InitialVector;
    /**
     * The block cipher modes.The default is {@link CipherMode#ECB}.
     */
    private CipherMode cipherMode;
    /**
     * The PaddingMode mode and key Length.<p>
     *
     * PaddingMode mode for the cipher mode. <p>
     * No padding by default.
     */
    private PaddingMode mPaddingMode;

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
        return cipherMode;
    }
    /**
     * set cipher mode.
     * @param cipherMode {@link CipherMode}
     */
    public void setCipherMode(CipherMode cipherMode) {
        this.cipherMode = cipherMode;
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
     * get master key type
     * @return  {@link MasterKeyType}
     */
    public MasterKeyType getMasterKeyType() {
        return masterKeyType;
    }

    /**
     * set master key type
     * @param masterKeyType {@link MasterKeyType}
     */
    public void setMasterKeyType(MasterKeyType masterKeyType) {
        this.masterKeyType = masterKeyType;
    }
}
