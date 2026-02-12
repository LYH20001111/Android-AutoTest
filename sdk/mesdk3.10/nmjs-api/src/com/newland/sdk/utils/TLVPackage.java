package com.newland.sdk.utils;

import java.util.Enumeration;

/**
 * BER-TLV Utils.
 *
 * @since ver3.10.01
 */
public interface TLVPackage {

    /**
     * Return an enumeration of the Vector of tags.
     *
     * @return
     */
    public Enumeration elements();

    /**
     * Append a data to the TLV data with TLVMsg object.
     *
     * @param tlvToAppend
     */
    public void append(TLVMsg tlvToAppend);

    /**
     * Append a data for a new tag to the TLV data.
     *
     * @param tag
     * @param value
     */
    public void append(int tag, byte[] value);

    /**
     * Append a data for a new tag to the TLV data.
     *
     * @param tag
     * @param value
     */
    public void append(int tag, String value);

    /**
     * Remove the data for the specified tag from the TLV data.
     *
     * @param tag
     */
    public void deleteByTag(int tag);

    /**
     * Get the specified TLV object.
     *
     * @param tag
     * @return
     */
    public TLVMsg find(int tag);


    /**
     * Get the value of the specified tag.
     *
     * @param tag
     * @return
     */
    public String getString(int tag);

    /**
     * Get the value of the specified tag.
     *
     * @param tag
     * @return
     */
    public byte[] getValue(int tag);

    /**
     * Check if the tag exists in the TLV data.
     *
     * @param tag
     * @return
     */
    public boolean hasTag(int tag);

    /**
     * Parse the TLV data.then invoke {@link #getString(int) {@link #getValue(int)}} to get the value of the specified tag.
     *
     * @param buf
     */
    public void unpack(byte[] buf);

    /**
     * Get the TLV data.
     *
     * @return
     */
    public byte[] pack();

}
