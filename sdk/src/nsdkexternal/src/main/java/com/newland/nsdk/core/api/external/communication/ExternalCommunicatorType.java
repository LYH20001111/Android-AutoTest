package com.newland.nsdk.core.api.external.communication;

/**
 * External device type.
 */
public enum ExternalCommunicatorType {
    /**
     * The communicator of external device with host device is PINPAD serial or RS232。
     */
    UART3PORT,

    /**
     * The communicator of external device with host device is USBHOST.
     */
    USB,

    /**
     * The communicator of external device with host device is bluetooth classic.
     */
    BLUETOOTH_CLASSIC,

    /**
     * Bluetooth Low Energy (BLE).
     */
    BLUETOOTH_LOW_ENERGY
}
