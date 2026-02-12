package com.newland.nsdk.core.api.external.devicemanager;

/**
 * Bluetooth info.
 */
public class BluetoothInfo {
    private String name;
    private String macAddress;

    /**
     * Gets bluetooth name.
     *
     * @return Bluetooth name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets bluetooth name.
     *
     * @param name Bluetooth name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets bluetooth MAC address.
     *
     * @return MAC address.
     */
    public String getMacAddress() {
        return macAddress;
    }

    /**
     * Sets bluetooth MAC address.
     *
     * @param macAddress MAC address.
     */
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
}
