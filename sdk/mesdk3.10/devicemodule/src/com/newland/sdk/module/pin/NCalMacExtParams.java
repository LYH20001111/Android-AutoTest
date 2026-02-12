package com.newland.sdk.module.pin;

/**
 * Created by youjf on 2019/7/25 17:21
 */
public class NCalMacExtParams extends CalMacExtParams{

    //randomIndex workingKeyData

    /**
     * MAC algorithm Initial Vector.The default is 0x00.<p>
     */
    private byte[] mInitialVector;
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
     * get initial vector of the MAC algorithm.
     * @return
     */
    public byte[] getInitialVector() {
        return mInitialVector;
    }

    /**
     * Set initial vector of the MAC algorithm.
     * @param initialVector
     */
    public void setInitialVector(byte[] initialVector) {
        mInitialVector = initialVector;
    }

    /**
     * get dukpt key derived mode.{@link DukptDerivedMode}
     * @return
     */
    public DukptDerivedMode getDukptDerivedMode() {
        return mDukptDerivedMode;
    }

    /**
     * set dukpt key derived mode
     * @param dukptDerivedMode {@link DukptDerivedMode}
     */
    public void setDukptDerivedMode(DukptDerivedMode dukptDerivedMode) {
        mDukptDerivedMode = dukptDerivedMode;
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
