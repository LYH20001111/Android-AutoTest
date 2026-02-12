package com.newland.sdk.module.printerPro;


/**
 * Printer status
 * @author linsi
 */
public enum NPrinterStatus {
    /**
     * Normal
     */
    NORMAL("Printer is available."),
    /**
     * Out of paper
     */
    OUTOF_PAPER("Out of paper."),
    /**
     * Over heat
     */
    OVER_HEAT("Over heat."),
    /**
     * Low voltage
     */
    LOW_VOLTAGE("Low voltage."),
    /**
     * Printer is busy
     */
    BUSY("Printer is busy."),
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

    NPrinterStatus(String description) {
        this.description = description;
    }

    public String toString() {
        return description;
    }

}
