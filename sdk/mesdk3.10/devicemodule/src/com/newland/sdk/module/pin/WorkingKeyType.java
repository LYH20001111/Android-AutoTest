package com.newland.sdk.module.pin;

/**
 * @description: working key type
 * @author: Lindan 
 * @create: 2019/7/29
 */
public enum WorkingKeyType {
    /**
     * Magnetic stripe card Data encryption type
     */
    TRACK,
    /**
     * PIN encryption type
     */
    PIN,
    /**
     * Key type for abstract calcualtion
     */
    MAC,
    /**
     * the key for only encryption data.
     * Not support
     */
    DATA_ENC_ONLY,
}
