package com.newland.nsdk.core.api.internal.printer;

/**
 * Printer status.
 */
public enum PrinterStatus {
    /**
     * Good.
     */
    OK(0),
    /**
     * Printing.
     */
    BUSY(8),
    /**
     * Out of paper.
     */
    NO_PAPER(2),
    /**
     * Overheat.
     */
    OVERHEAT(4),
    /**
     * Abnormal voltage.
     */
    VOL_ERR(112),
    /**
     * No printer or printer damaged.
     */
    BAD(113);

    private int code;

    PrinterStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
