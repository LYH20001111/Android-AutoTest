package com.newland.sdk.module.swiper;

/**
 * Created by youjf on 2019/7/29 08:49
 */
public class SwipExtParams {
    /**
     * account number mask（10 bytes，F represents mask，0 represents plain text，e.g. 0x00,0x00,0x00,0x00,0xFF,0xFF,0xFF,0x00
     * 	,0x00,0x00 represents the display of the front 8-bit of the main account number and the final 6-bit display of the main account, and the middle section is shielded and replaced by *）
     */
    private byte[] acctMask;
    /**
     * the ciphertext data of working key
     */
    private byte[] workingKeyData;
    /**
     * track algorithm type
     */
    private MSDAlgorithmType msdAlgorithmType;

    /**
     * used for external sp100
     */
    private byte[] cbcInitialVector;

    /**
     * get the ciphertext of working key
     * @return
     */
    public byte[] getWorkingKeyData() {
        return workingKeyData;
    }

    /**
     * set the ciphertext of working key
     * @param workingKeyData
     */
    public void setWorkingKeyData(byte[] workingKeyData) {
        this.workingKeyData = workingKeyData;
    }


    /**
     * get track algorithm type
     * @return
     */
    public MSDAlgorithmType getMSDAlgorithmType() {
        return msdAlgorithmType;
    }

    /**
     * set track algorithm type
     * @param algorithmType
     */
    public void setMSDAlgorithmType(MSDAlgorithmType algorithmType) {
        this.msdAlgorithmType = algorithmType;
    }

    /**
     * set account number mask
     * @return
     */
    public byte[] getAcctMask() {
        return acctMask;
    }

    /**
     * get account number mask
     * @param acctMask account number mask（10 bytes，F represents mask，0 represents plain text，e.g. 0x00,0x00,0x00,0x00,0xFF,0xFF,0xFF,0x00
     * 	,0x00,0x00 represents the display of the front 8-bit of the main account number and the final 6-bit display of the main account, and the middle section is shielded and replaced by *）
     */
    public void setAcctMask(byte[] acctMask) {
        this.acctMask = acctMask;
    }

    /**
     * get external sp100 cbc Initial Vector data
     * @return
     */
    public byte[] getCbcInitialVector() {
        return cbcInitialVector;
    }

    /**
     * set external sp100 cbc Initial Vector data
     * @param cbcInitialVector
     */
    public void setCbcInitialVector(byte[] cbcInitialVector) {
        this.cbcInitialVector = cbcInitialVector;
    }
}
