package com.newland.nsdk.core.external.command.emv.listener;

/**
 * @author Helen
 * @date 2021/6/28
 */
public class ExtTLVResult {
    private int dataStatus;
    private int actualDataLen;
    private byte[] data;

    /**
     * Gets data status.
     *
     * @return Data status.
     * <ul>
     *     <li>0: Success</li>
     *     <li>1: Failed</li>
     *     <li>2: Not exist</li>
     * </ul>
     */
    public int getDataStatus() {
        return dataStatus;
    }

    /**
     * Sets data status.
     *
     * @param dataStatus Data status.
     *                   <ul>
     *                       <li>0: Success</li>
     *                       <li>1: Failed</li>
     *                       <li>2: Not exist</li>
     *                   </ul>
     */
    public void setDataStatus(int dataStatus) {
        this.dataStatus = dataStatus;
    }

    /**
     * Gets the actual length of the data before using CBC to encrypt.
     *
     * @return The actual length of the data before using CBC to encrypt.
     */
    public int getActualDataLen() {
        return actualDataLen;
    }

    /**
     * Sets the actual length of the data before using CBC to encrypt.
     *
     * @param actualDataLen The actual length of the data before using CBC to encrypt.
     */
    public void setActualDataLen(int actualDataLen) {
        this.actualDataLen = actualDataLen;
    }

    /**
     * Gets TLV or TLV list data.
     *
     * @return TLV or TLV list data.
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Sets TLV or TLV list data.
     *
     * @param data TLV or TLV list data.
     */
    public void setData(byte[] data) {
        this.data = data;
    }

}
