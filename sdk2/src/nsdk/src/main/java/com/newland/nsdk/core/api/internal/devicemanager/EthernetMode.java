package com.newland.nsdk.core.api.internal.devicemanager;

public enum EthernetMode {
    /**
     * Ethernet is off all the time.
     */
    ALL_OFF,
    /**
     * Ethernet is on all the time.
     */
    ALL_ON,
    /**
     * Ethernet mode is configurable.
     */
    CONFIGURABLE,
    /**
     * Ethernet mode is non-configurable(Device not support ethernet mode setting), read-only.
     */
    NON_CONFIGURABLE,
}
