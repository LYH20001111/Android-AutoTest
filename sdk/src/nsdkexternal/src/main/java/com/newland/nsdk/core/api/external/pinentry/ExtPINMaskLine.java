package com.newland.nsdk.core.api.external.pinentry;

/**
 * The line to display masked PIN. External device screen will show '*' instead of real PIN when a key is pressed.
 */
public enum ExtPINMaskLine {
    /**
     * Show masked PIN on line 1.
     */
    LINE_1(1),
    /**
     * Show masked PIN on line 2.
     */
    LINE_2(2),
    /**
     * Show masked PIN on line 3.
     */
    LINE_3(3),
    /**
     * Show masked PIN on line 4.
     */
    LINE_4(4),
    /**
     * Show masked PIN on line 5.
     */
    LINE_5(5);

    int code;

    ExtPINMaskLine(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
