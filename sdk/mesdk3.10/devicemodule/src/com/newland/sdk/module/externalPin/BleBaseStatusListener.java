package com.newland.sdk.module.externalPin;

public interface BleBaseStatusListener {
    /**
     * BluetoothBase connection status
     * @param status BLUETOOTH_UNKNOWN = 0;
     *         BLUETOOTH_CLOSED = 1;
     *         BLUETOOTH_CLOSING = 2;
     *         BLUETOOTH_OPENING = 3;
     *         BLUETOOTH_IDLE = 4;
     *         BLUETOOTH_SEARCHING = 5;
     *         BLUETOOTH_CONNECTING = 6;
     *         BLUETOOTH_CONNECTED = 7;
     *         BLUETOOTH_WAITING = 8;
     *         BLUETOOTH_BOND_BONDING = 10;
     *         BLUETOOTH_BOND_NONE = 11;
     *         BLUETOOTH_BOND_BONDED = 12;
     *         BLUETOOTH_CONNECT_FAIL = 13;
     *         BLUETOOTH_DISCONNECTED = 14;
     *         BLUETOOTH_COMMAND_CONNECTING = 9;
     */
   public void onStatusChange(int status);
}
