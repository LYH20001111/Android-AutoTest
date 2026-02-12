package com.newland.sdk.me.module.usb.usbserial;

import java.util.List;


public interface SerialPortCallback {
    void onSerialPortsDetected(List<UsbSerialDevice> serialPorts);
}
