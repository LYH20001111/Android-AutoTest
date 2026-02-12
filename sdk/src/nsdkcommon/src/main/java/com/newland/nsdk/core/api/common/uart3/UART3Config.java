package com.newland.nsdk.core.api.common.uart3;

import com.newland.nsdk.core.api.common.serialport.BaudRate;
import com.newland.nsdk.core.api.common.serialport.DataBits;
import com.newland.nsdk.core.api.common.serialport.ParityBit;
import com.newland.nsdk.core.api.common.serialport.SerialPortSettings;
import com.newland.nsdk.core.api.common.serialport.StopBits;

/**
 * This class is used to record UART3 port configuration info.
 * @deprecated This class is instead by {@link SerialPortSettings}.
 */
public class UART3Config extends SerialPortSettings {

    /**
     * Initializes a new instance of serial port settings using the specified baud rate, parity bit, data bits, and stop bits.
     *
     * @param baudRate  Baud rate, see {@link BaudRate}
     * @param dataBits  Data bits, see {@link DataBits}
     * @param parityBit Parity bit, see {@link ParityBit}
     * @param stopBits  Stop bits, see {@link StopBits}
     */
    public UART3Config(BaudRate baudRate, DataBits dataBits, ParityBit parityBit, StopBits stopBits) {
        super(baudRate, dataBits, parityBit, stopBits, false);
    }

    public UART3Config(BaudRate baudRate, DataBits dataBits, ParityBit parityBit, StopBits stopBits, boolean isBlocked) {
        super(baudRate, dataBits, parityBit, stopBits, isBlocked);
    }
}
