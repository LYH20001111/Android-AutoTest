package com.newland.nsdk.core.api.external.communication;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;

/**
 * A listener invoked when it needs user to choose a device from the device list.
 */
public interface CommunicatorListener {
    /**
     * Invoked when it needs user to select a bluetooth device.
     *
     * @param devices Bonded bluetooth devices which MAC address is 38:3C:9C:XX:XX:XX (Newland bluetooth device).
     * @return Selected bluetooth device.
     */
    BluetoothDevice onBluetoothList(ArrayList<BluetoothDevice> devices);

    /**
     * Invoked when the state of communicator changed.
     *
     * @param state State of communicator, see{@link ExternalCommunicatorState}
     */
    void onConnectedStateChange(ExternalCommunicatorState state);
}
