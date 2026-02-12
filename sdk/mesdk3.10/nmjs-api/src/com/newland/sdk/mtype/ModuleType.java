package com.newland.sdk.mtype;

/**
 * Define all the device module that the devices may support<p>
 * Devices needing unified<tt>SDK</tt> must meet the interface invocation mode for given devices <p>
 */
public enum ModuleType {

    /**
     * Common card reading device <p>
     */
    COMMON_CARDREADER("Common card reader"),
    /**
     * Magnetic stripe card reader module <p>
     */
    MAGCARDREADER("Magnetic stripe card reader module"),
    /**
     * Contact card reader module <p>
     */
    ICCARDREADER("Contact card reader module"),
    /**
     * Contactless reader module <p>
     */
    RFCARDREADER("Contactless reader module"),
    /**
     * Buzzer module <p>
     */
    BUZZER("buzzer module"),
    /**
     * Pinpad module <p>
     */
    PINPAD("Pinpad module"),
    /**
     * Printer module <p>
     */
    PRINTER("Printer module"),
    /**
     * Printer pro module <p>
     */
    PRINTER_PRO("Printer pro module"),
    /**
     * EMV module <p>
     */
    EMV("EMV module"),
    /**
     * EMV L3 module <p>
     */
    EMV_L3("EMV L3 module"),
    /**
     * Scanner module <p>
     */
    SCANNER("Scanner module"),
    /**
     * Indicator light module <p>
     */
    INDICATOR_LIGHT("Indicator light module"),

    /**
     * USB SerialPort module
     */
    USB_SERIALPORT("USB SerialPort module"),
    /**
     * SM module
     */
    SM("SM module"),
    /**
     * Display screen module
     */
    DISPLAY_SCREEN("display screen module"),
    /**
     * Device basic module
     */
    DEVICE_BASIC("Device basic module"),
    /**
     * System settings module
     */
    SETTINGS("System settings module");

    private String description;

    ModuleType(String description) {
        this.description = description;
    }

    public String toString() {
        return this.description;
    }
}
