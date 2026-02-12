package com.newland.nsdk.core.api.external.keyboard;

/**
 * Prompt ID.
 */
public enum PromptID {
    /**
     * Prompt to enter phone number.
     */
    PHONE_NUMBER((byte)1);

    private byte code;

    PromptID(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return this.code;
    }
}
