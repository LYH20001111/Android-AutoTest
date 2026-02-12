package com.newland.sdk.module.usb;

import android.hardware.usb.UsbDevice;

import java.util.HashMap;

/**
 * Author by bxy, Date on 2019/11/21.
 */
public interface SelectUsbDeviceListener {
    /**
     * Select target device
     * @param usbDeviceList usb device list.
     * @return
     */
    public UsbDevice onSelect(HashMap<String, UsbDevice> usbDeviceList);
}
