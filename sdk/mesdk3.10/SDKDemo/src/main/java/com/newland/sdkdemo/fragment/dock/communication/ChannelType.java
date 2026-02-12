package com.newland.sdkdemo.fragment.dock.communication;

/**
 * Access Type of SP100
 *
 * @author linsi
 */
public enum ChannelType {
    DEFAULT_USB("DEFAULT_USB"),
    DOCK_USB1("DOCK_USB1"),
    DOCK_UART("DOCK_UART");

    private String description;

    ChannelType(String description) {
        this.description = description;
    }
}
