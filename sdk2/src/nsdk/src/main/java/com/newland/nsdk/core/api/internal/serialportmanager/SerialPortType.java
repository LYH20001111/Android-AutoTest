package com.newland.nsdk.core.api.internal.serialportmanager;

public enum SerialPortType {
    /**
     * RS232 port for devices.Specially for U2000, this means "RS232A" in device.
     */
    RS232,
    /**
     * "RS232B" port for U2000.
     */
    RS232B,
    /**
     * PINPAD port for devices.
     */
    PINPAD,

    /**
     * Micro-USB for devices, which is used to do OTA.
     */
    USB,

    /**
     * Extended USB Type-A interface in devices like P300 and N750P.
     */
    USB_HOST,

    /**
     * The USB interface in the devices whose silk-screen printing is "device".
     */
    USB_DEVICE,
}
