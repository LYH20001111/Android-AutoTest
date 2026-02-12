package com.newland.nsdk.core.external.command.contactlesscard;

public enum ContactlessCardMode {
    /**
     * REQA.
     */
    REQA(0x26),
    /**
     * WUPA. This is recommended for apps.
     */
    WUPA(0x52);
    private int code;

    ContactlessCardMode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
