package com.newland.nsdk.core.external.command.emv.aid;

/**
 * @author Helen
 * @date 2021/6/28
 */
public class ExtAIDEntry {
    /**
     * Value of TAG 9F06.
     */
    private byte[] aid;
    /**
     * Value of TAG DF37.
     */
    private byte[] kernelId;
    /**
     *  Extend usage
     *  <p>
     * 0x00 - default, will no check<br>
     * |0x01 - should be matching transactionType<br>
     * |0x02 - should be matching externString</p>
     */
    private int externCheckFlag;
    /**
     * Value of TAG 9C.
     */
    private byte transactionType;

    public ExtAIDEntry(){

    }

    public ExtAIDEntry(byte[] aid,byte[] kernelId,int externCheckFlag,byte transactionType){
        this.aid = aid;
        this.kernelId = kernelId;
        this.externCheckFlag = externCheckFlag;
        this.transactionType = transactionType;
    }
    /**
     * Gets value of TAG 9C.
     *
     * @return Value of TAG 9C.
     */
    public byte getTransactionType() {
        return transactionType;
    }

    /**
     * Sets value of TAG 9C.
     *
     * @param transactionType Value of TAG 9C.
     */
    public void setTransactionType(byte transactionType) {
        this.transactionType = transactionType;
    }

    /**
     * Gets TAG 9F06 data.
     *
     * @return AID data.
     */
    public byte[] getAid() {
        return aid;
    }

    /**
     * Sets TAG 9F06 data.
     *
     * @param aid AID data.
     */
    public void setAid(byte[] aid) {
        this.aid = aid;
    }

    /**
     * Gets value of TAG DF37.
     *
     * @return Value of TAG DF37.
     */
    public byte[] getKernelId() {
        return kernelId;
    }

    /**
     * Sets value of TAG DF37.
     *
     * @param kernelId Value of TAG DF37.
     */
    public void setKernelId(byte[] kernelId) {
        this.kernelId = kernelId;
    }

    /**
     * Gets check flag.
     *
     * @return The check flag.
     */
    public int getExternCheckFlag() {
        return externCheckFlag;
    }

    /**
     * Sets check flag.
     *
     * @param checkFlag The check flag}.
     */
    public void setExternCheckFlag(int checkFlag) {
        this.externCheckFlag = checkFlag;
    }

}
