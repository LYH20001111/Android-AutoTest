package com.newland.sdk.module.settings;

/**
 * <p>Get device info</p>
 *
 * @author linsi
 */
public enum InfoItem {
    IMEI("IMEI"),
    MEID("MEID"),
    //    CONFIG_PATH("Profile path."),
    FIRMWARE("Firmware version."),
    FIRMWARE_ID("Firmware id."),
    HARDWARE_ID("Hardware id."),
    HARDWARE_CONFIG("Hardware config."),
    MODEL("Model"),
    SERIAL_NUMBER("Serial number."),
    //    KERNEL_VERSION("Kernel version."),
    //    MANUFACTURE("Manufacture"),
    BASEBAND("Baseband version."),
    CUSTOMER_ID("Customer id."),
    BOOTLOADER_VERSION("Bootloader version."),
    TOUCHSCREEN_NAME("Touchscreen name."),
    TOUCHSCREEN_RESOLUTION("Touchscreen resolution."),
    TOUCHSCREEN_VERSION("Touchscreen version."),
    PROCESSOR_INFO("Processor info");
    private String description;

    InfoItem(String description) {
        this.description = description;
    }

    public String toString() {
        return description;
    }
}
