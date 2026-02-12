package com.newland.nsdk.core.api.common.card.contact;

public enum SAMClkFrequency {
    /**
     * SAM CLK 2.4MHz.
     */
    CLK_2400KHz(1),
    /**
     * SAM CLK 4MHz. Default.
     */
    CLK_4000KHz(0);

    int code;
    SAMClkFrequency(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
