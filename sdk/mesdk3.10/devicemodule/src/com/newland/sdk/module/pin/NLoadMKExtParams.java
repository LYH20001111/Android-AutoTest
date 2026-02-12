package com.newland.sdk.module.pin;

/**
 * Created by youjf on 2019/7/26 10:44
 */
public class NLoadMKExtParams extends LoadMKExtParams{
    /**
     * the master key type.{@link MasterKeyType}
     */
    private MasterKeyType masterKeyType;
    /**
     * Initial Vector.The default is 0x00.
     * 8 bytes for TDES/SM4, 16 bytes for AES.
     */
    private byte[] InitialVector;
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
