package com.newland.sdk.utils;

/**
 * TLV interface.
 *
 * @since ver3.10.01
 */
public interface TLVMsg {

    /**
     * get the tag of the tlv data.
     *
     * @return
     */
    public int getTag();

    /**
     * get the value of the tlv data.
     *
     * @return
     */
    public byte[] getValue();

    /**
     * return the tlv data with byte array format.
     *
     * @return
     */
    public byte[] pack();

    /**
     * return the tlv data with hex format.
     *
     * @return
     */
    public String toHexString();

}
