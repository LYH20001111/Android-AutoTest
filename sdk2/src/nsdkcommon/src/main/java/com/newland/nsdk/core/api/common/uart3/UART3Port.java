package com.newland.nsdk.core.api.common.uart3;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.serialport.BaudRate;
import com.newland.nsdk.core.api.common.serialport.DataBits;
import com.newland.nsdk.core.api.common.serialport.ParityBit;
import com.newland.nsdk.core.api.common.serialport.SerialPortSettings;
import com.newland.nsdk.core.api.common.serialport.StopBits;
import com.newland.nsdk.core.api.common.uart3.UART3Config;

/**
 * Provides the ability to communicate with devices on UART3.
 *
 * <p>How to create a UART3Port instance:</p>
 * <pre>
 *     // Type is required to create a UART3Port.
 *     UART3Port uart3Port = new UART3PortImpl(UART3Type.RS232_OTHER);
 * </pre>
 *
 * @deprecated This module can be only supported on the following devices.
 *             <ul>
 *                 <li>CPOS X5 A7/A10</li>
 *                 <li>N910 A7</li>
 *                 <li>N700 A7</li>
 *                 <li>N850 A7</li>
 *                 <li>U2000</li>
 *             </ul>
 *             The other devices please refer to {@link com.newland.nsdk.core.api.internal.serialportmanager.SerialPort}, which created by {@link com.newland.nsdk.core.api.internal.serialportmanager.SerialPortManager#createInstance(com.newland.nsdk.core.api.internal.serialportmanager.SerialPortType, SerialPortSettings)}.
 */
public interface UART3Port {
    /**
     * Opens UART3 port.
     * <p>Note: If config has not been set before, default config will be applied.</p>
     * <ul>
     * <li>Default data bits: {@link DataBits#DATA_BIT_8}</li>
     * <li>Default parity bit: {@link ParityBit#NO_CHECK} </li>
     * <li>Default stop bits: {@link StopBits#STOP_BIT_ONE}</li>
     * <li>Default baud rate: {@link BaudRate#BPS115200}</li>
     * </ul>
     *
     * @throws NSDKException
     */
    void open() throws NSDKException;

    /**
     * Sets configuration of this UART3 port before it is opened.
     *
     * @param config <b>[Optional]</b> Configuration of this UART3 port.See {@link UART3Config}.
     *               <ul> If it is null, default values will be applied.
     *               <li>Default data bits: {@link DataBits#DATA_BIT_8}</li>
     *               <li>Default parity bit: {@link ParityBit#NO_CHECK} </li>
     *               <li>Default stop bits: {@link StopBits#STOP_BIT_ONE}</li>
     *               <li>Default baud rate: {@link BaudRate#BPS115200}</li>
     *               </ul>
     * @throws NSDKException
     */
    void setConfig(UART3Config config);

    /**
     * Reads data after the port is opened.
     *
     * @param maxLen  <b>[Required]</b> Max length to read.
     * @param timeout <b>[Required]</b> Timeout to read. Unit: ms, not to wait when it is <=0.
     * @return Data.
     * @throws NSDKException
     */
    byte[] read(int maxLen, int timeout) throws NSDKException;

    /**
     * Writes data after the port is opened.
     *
     * @param data    <b>[Required]</b> Data to write.
     * @param maxLen  <b>[Required]</b> Max length to write.
     * @param timeout <b>[Required]</b> Timeout to write. Unit: ms, not to wait when it is <=0.
     * @throws NSDKException
     */
    void write(byte[] data, int maxLen, int timeout) throws NSDKException;

    /**
     * Closes this UART3 port.
     *
     * @throws NSDKException
     */
    void close() throws NSDKException;

    /**
     * Clears receiving buffer of port.
     *
     * @throws NSDKException
     */
    void flush() throws NSDKException;
}
