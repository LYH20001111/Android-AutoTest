package com.newland.nsdk.core.external.command.emv.listener;

import com.newland.nsdk.core.api.common.emv.EMVErrorCode;

/**
 * Transaction result.
 */
public class TransactionResult {
    private byte result;
    private byte cvmStatus;
    private int errorCode;
    private byte tlvDataStatus;
    private int actualDataLen;
    private byte[] tlvListData;

    /**
     * Gets transaction status.
     *
     * @return Transaction status.
     * <ul>
     *     <li>0: OK</li>
     *     <li>1: TERMINATE</li>
     *     <li>2: TRY_ANOTHER</li>
     *     <li>3: DECLINE</li>
     *     <li>4: APPROVED</li>
     *     <li>5: ONLINE</li>
     * </ul>
     */
    public byte getResult() {
        return result;
    }

    /**
     * Sets transaction status.
     *
     * @param result Transaction status.
     *               <ul>
     *                   <li>0: OK</li>
     *                   <li>1: TERMINATE</li>
     *                   <li>2: TRY_ANOTHER</li>
     *                   <li>3: DECLINE</li>
     *                   <li>4: APPROVED</li>
     *                   <li>5: ONLINE</li>
     *               </ul>
     */
    public void setResult(byte result) {
        this.result = result;
    }

    /**
     * Gets CVM status.
     *
     * @return CVM status.
     * <ul>
     *     <li>0x00: NO_CVM</li>
     *     <li>0x10: OBTAIN_SIGNATURE</li>
     *     <li>0x20: ONLINE_PIN</li>
     *     <li>0x30: CONFIRMATION_CODE_VERIFIED</li>
     *     <li>0xF0: CVM_NA</li>
     * </ul>
     */
    public byte getCVMStatus() {
        return cvmStatus;
    }

    /**
     * Sets CVM status.
     *
     * @param cvmStatus CVM status.
     *                  <ul>
     *                      <li>0x00: NO_CVM</li>
     *                      <li>0x10: OBTAIN_SIGNATURE</li>
     *                      <li>0x20: ONLINE_PIN</li>
     *                      <li>0x30: CONFIRMATION_CODE_VERIFIED</li>
     *                      <li>0xF0: CVM_NA</li>
     *                  </ul>
     */
    public void setCVMStatus(byte cvmStatus) {
        this.cvmStatus = cvmStatus;
    }

    /**
     * Gets error code of current emv transaction.
     *
     * @return Error code , see{@link EMVErrorCode}.
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Sets error code.
     *
     * @param errorCode Error code.
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Gets TLV data status.
     *
     * @return TLV data status.
     * <ul>
     *     <li>0: Success</li>
     *     <li>1: Failed</li>
     *     <li>2: Not exist</li>
     * </ul>
     */
    public byte getTLVDataStatus() {
        return tlvDataStatus;
    }

    /**
     * Sets TLV data status.
     *
     * @param tlvDataStatus TLV data status.
     *                      <ul>
     *                          <li>0: Success</li>
     *                          <li>1: Failed</li>
     *                          <li>2: Not exist</li>
     *                      </ul>
     */
    public void setTLVDataStatus(byte tlvDataStatus) {
        this.tlvDataStatus = tlvDataStatus;
    }

    /**
     * Gets length of plain data.
     *
     * @return Length of plain data.
     */
    public int getActualDataLen() {
        return actualDataLen;
    }

    /**
     * Sets length of plain data.
     *
     * @param actualDataLen Length of plain data.
     */
    public void setActualDataLen(int actualDataLen) {
        this.actualDataLen = actualDataLen;
    }

    /**
     * Gets TLV list data.
     *
     * @return TLV list data.
     */
    public byte[] getTLVListData() {
        return tlvListData;
    }

    /**
     * Sets TLV list data.
     *
     * @param tlvListData TLV list data.
     */
    public void setTLVListData(byte[] tlvListData) {
        this.tlvListData = tlvListData;
    }
}

