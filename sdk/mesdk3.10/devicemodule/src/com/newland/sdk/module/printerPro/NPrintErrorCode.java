package com.newland.sdk.module.printerPro;

/**
 * <p>Error detail</p>
 * <p>It describes an error that occurred while printing.</p>
 *
 * @author linsi
 */
public enum NPrintErrorCode {
    /**
     * Print failed
     */
    FAILED("Print failed."),

    /**
     * Invalid parameter
     */
    PARAM_ERROR("Invalid parameter."),

    /**
     * Invalid file path
     */
    INVALID_FILE_PATH("Invalid file path."),

    /**
     * Printer is busy
     */
    BUSY("Printer is busy."),

    /**
     * Out of paper
     */
    OUTOF_PAPER("Out of paper."),

    /**
     * Heat limit exceeded
     */
    HEAT_LIMITED("Heat limit exceeded."),

    /**
     * abnormal voltage
     */
    ABNORMAL_VOLTAGE("abnormal voltage."),

    /**
     * Printer is destroyed
     */
    DESTROYED("Printer is destroyed."),

    /**
     * The printing roller is not in the correct position
     */
    PPSERR("The printing roller is not in the correct position."),

    /**
     * The cutter is not work.
     * only for CPOS devices.
     */
    CUTTER_ERROR("The cutter is not work.");

    private String description;

    NPrintErrorCode(String description) {
        this.description = description;
    }

    public String toString() {
        return description;
    }
}
