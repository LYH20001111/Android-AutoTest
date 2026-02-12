package com.newland.sdk.me.module.emvl3;

/**
 * @Description
 * @Author wuhh
 * @Date 2019/12/30
 */
public class TransactionResult {
    private int errorCode;
    private int resultCode;
    private int cvmStatus;
    private byte[] tlvData;

    public TransactionResult(int errorCode,int resultCode){
        this.errorCode =  errorCode;
        this.resultCode = resultCode;
    }

    public TransactionResult(int errorCode,int resultCode,int cvmStatus,byte[] tlvData){
        this.errorCode =  errorCode;
        this.resultCode = resultCode;
        this.cvmStatus = cvmStatus;
        this.tlvData = tlvData;
    }
    public int getResultCode() {
        return resultCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public byte[] getTlvData() {
        return tlvData;
    }

    public int getCvmStatus() {
        return cvmStatus;
    }

}
