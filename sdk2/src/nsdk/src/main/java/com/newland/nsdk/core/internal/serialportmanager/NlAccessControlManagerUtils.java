package com.newland.nsdk.core.internal.serialportmanager;

import android.content.Context;
import android.util.Log;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.internal.serialportmanager.SerialPortType;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;

public class NlAccessControlManagerUtils {
    private Context context;
    private static final String ACCESS_CONTROL_MANAGER_SERVICE = "accessControlManagerService";
    private static final String CPOS_X5_A14_RS232_PORT = "/proc/tty/driver/usbserial";
    private static final String U200_WAKE_UP_PORT = "/sys/class/rs232-1_wakeup/wakeup_rs232";
    public static final int CPOS_X5_A14_RS232 = 1;
    public static final int U200_WAKE_UP = 2;
    private Object nlAccessControlManagerInstance = null;
    Class<?> nlAccessControlManagerClass = null;
    public NlAccessControlManagerUtils(Context context) throws NSDKException{
        this.context = context;
        try {
            nlAccessControlManagerClass = Class.forName("android.newland.NlAccessControlManager");
            nlAccessControlManagerInstance = context.getSystemService(ACCESS_CONTROL_MANAGER_SERVICE);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new NSDKException(e.getMessage());
        }
    }

    public int getNodeName(int type) {
        try {
            Method getDevInfoMethod = nlAccessControlManagerClass.getMethod("getDevInfo");
            String devInfo = (String) getDevInfoMethod.invoke(nlAccessControlManagerInstance);
            String[] portNames = devInfo.split("\n");
            for (String portName : portNames) {
                if (type == CPOS_X5_A14_RS232 && portName.contains(CPOS_X5_A14_RS232_PORT)) {
                    return Integer.parseInt(portName.split("." + CPOS_X5_A14_RS232_PORT)[0]);
                } else if (type == U200_WAKE_UP && portName.contains(U200_WAKE_UP_PORT)) {
                    return Integer.parseInt(portName.split("." + U200_WAKE_UP_PORT)[0]);
                }
            }
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }

    public String read(int devIndex) {
        try {
            Method readMethod = nlAccessControlManagerClass.getMethod("read", int.class);
            return (String) readMethod.invoke(nlAccessControlManagerInstance, devIndex);
        } catch (Exception e) {
            return null;
        }
    }

    public void write(int devIndex, byte[] buffer) throws NSDKException {
        try {
            Method writeMethod = nlAccessControlManagerClass.getMethod("write", int.class, byte[].class, int.class);
            writeMethod.invoke(nlAccessControlManagerInstance, devIndex, buffer, buffer.length);
        } catch (Exception e) {
            throw new NSDKException(e.getMessage());
        }
    }


    public String getPortName(SerialPortType serialPortType) {
        try {
            Class<?> enumClass = Class.forName("android.newland.PortType");
            Method getPortNameMethod = nlAccessControlManagerClass.getMethod("getPortName", enumClass);
            Method valueOfMethod = enumClass.getMethod("valueOf", String.class);
            Object type = valueOfMethod.invoke(null, serialPortType.name());
            return (String) getPortNameMethod.invoke(nlAccessControlManagerInstance, type);
        } catch (Exception e) {
            return null;
        }
    }

}
