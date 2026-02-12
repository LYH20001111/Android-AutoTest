package com.newland.nsdk.core.external.command.communication.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.common.exception.NSDKCommunicationException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class BluetoothUtils {
    private BluetoothAdapter bluetoothAdapter;
    private static BluetoothUtils instance;
    public static final int OPEN_TIMEOUT = 120000;

    private BluetoothUtils(){
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static BluetoothUtils getInstance() {
        if (instance == null) {
            synchronized (BluetoothUtils.class) {
                if (instance == null) {
                    instance = new BluetoothUtils();
                }
            }
        }

        return instance;
    }

    public ArrayList<BluetoothDevice> getBondedDevices() throws NSDKException {
        if (bluetoothAdapter == null) {
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_BLUETHOOTH_NOT_SUPPORTED, "Bluetooth not supported.");
        }
        if (!bluetoothAdapter.isEnabled()) {
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_BLUETHOOTH_DISABLED, "Please enable bluetooth.");
        }

        ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<>();
        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : devices) {
            LogUtils.d(getClass().getName(), ">>>> isBluetoothConnected device:" + device.getName());
//            if (isMacAddressValid(device.getAddress())) {
                bluetoothDevices.add(device);
//            }
        }

        return bluetoothDevices;
    }

    public static boolean isDeviceNameValid(String deviceName) {
        if (deviceName.startsWith("C-ME30S")) {
            return true;
        }

        return false;
    }

    public static boolean isMacAddressValid(String macAddress) {
        // MAC address for Newland bluetooth devices is 38:3C:9C:XX:XX:XX
        String regStr = "38:3C:9C:([A-Fa-f0-9]{2}:){2}[A-Fa-f0-9]{2}";
        if (macAddress.matches(regStr)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isConnected(BluetoothDevice device) {
        if (device == null) {
            return false;
        }

        try {
            Method m = device.getClass().getMethod("isConnected", (Class[]) null);
            return (boolean) m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
