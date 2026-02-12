package com.newland.nsdk.core.external.command.cipher;

/**
 * MAC block flag.
 */
public enum ExtMacBlockFlag {
    /**
     * First block of data.
     */
    FIRST(0),
    /**
     * Indicates this is not the last block of data.
     */
    NEXT(1),
    /**
     * Last block of data.
     */
    LAST(2),
    /**
     * Indicates there is only one block of data.
     */
    ONLY(3);

    int code;
    ExtMacBlockFlag(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
