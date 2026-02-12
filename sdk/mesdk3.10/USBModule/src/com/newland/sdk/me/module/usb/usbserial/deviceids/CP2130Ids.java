package com.newland.sdk.me.module.usb.usbserial.deviceids;

import static com.newland.sdk.me.module.usb.usbserial.deviceids.Helpers.createDevice;
import static com.newland.sdk.me.module.usb.usbserial.deviceids.Helpers.createTable;

public class CP2130Ids
{
    private static final long[] cp2130Devices = createTable(
            createDevice(0x10C4, 0x87a0)
    );

    public static boolean isDeviceSupported(int vendorId, int productId)
    {
        return Helpers.exists(cp2130Devices, vendorId, productId);
    }
}
