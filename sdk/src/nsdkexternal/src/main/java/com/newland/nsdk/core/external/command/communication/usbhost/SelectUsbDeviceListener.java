package com.newland.nsdk.core.external.command.communication.usbhost;

import android.hardware.usb.UsbDevice;

import java.util.HashMap;

/**
 * @author hlh
 * @date 2020/7/22
 */
public interface SelectUsbDeviceListener {
    UsbDevice onSelect(HashMap<String, UsbDevice> usbDeviceList);
}
