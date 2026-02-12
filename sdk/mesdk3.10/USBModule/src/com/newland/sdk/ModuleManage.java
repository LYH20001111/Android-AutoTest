package com.newland.sdk;

import android.content.Context;

import com.newland.sdk.me.module.usb.MEUSB;
import com.newland.sdk.module.usb.USBModule;

/**
 * <p>Call entry for the development kit.</p>
 * <p>call step:</p>
 * <p>1.get the instance of the ModuleManage. ModuleManage moduleManage = ModuleManage.getInstance();</p>
 * <p>2.Initializes the device module.</p>
 * <p>3.invoke the method to get the device module.</p>
 * <p>4.Destroy device module</p>
 */
public class ModuleManage {
    private Context context;
    private static ModuleManage moduleManage;
    private USBModule usbModule;
    public static boolean isDebug = false;

    public static ModuleManage getInstance() {
        if (moduleManage == null) {
            synchronized (ModuleManage.class) {
                if (moduleManage == null) {
                    moduleManage = new ModuleManage();
                }
            }
        }
        return moduleManage;
    }

    /**
     * Initializes the device module.
     *
     * @param context
     * @return
     */
    public boolean init(Context context) {
        try {
            usbModule = new MEUSB(context);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private ModuleManage() {
    }

    /**
     * <p>Get the USB module.</p>
     * <p>Achieve the purpose of communication with external equipment.</p>
     *
     * @return
     */
    public USBModule getUSBModule() {
        return usbModule;
    }


    public void setDebugMode(boolean isDebug) {
        this.isDebug = isDebug;
    }

    /**
     * release the device resources.
     *
     * @return
     */
    public void destroy() {
        usbModule = null;
        moduleManage = null;
    }
}
