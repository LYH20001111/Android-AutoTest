package com.newland.sdk.mtypex.serializer;

/**
 * Field serializer
 *
 *
 */
public interface Serializer {

    public byte[] pack(Object obj) throws Exception;

    public Object unpack(byte[] input, int offset, int len) throws Exception;

}
