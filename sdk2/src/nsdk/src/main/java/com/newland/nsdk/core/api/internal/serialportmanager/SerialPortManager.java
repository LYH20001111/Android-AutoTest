package com.newland.nsdk.core.api.internal.serialportmanager;

import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.serialport.SerialPortSettings;

/**
 * Helper class for create an instance to communicate with devices on USB or UART3 port.
 *
 * <p>How to create an instance of USB or UART3:</p>
 * <pre>
 *     SerialPortManager serialPortManager = (SerialPortManager) NSDKModuleManagerImpl.getInstance().getModule(ModuleType.SERIAL_PORT_MANAGER);
 *     //Configs of serial port
 *     SerialPortSettings portSettings = new SerialPortSettings(BaudRate.115200, DataBits.DATA_BIT_8, ParityBit.NO_CHECK, StopBits.STOP_BIT_ONE, true);
 *     //UART3(SerialPortType.RS232 or SerialPortType.PINPAD)
 *     SerialPort serialPort = serialPortManager.createInstance(SerialPortType.RS232, portSettings);
 *     //USB(SerialPortType.USB)
 *     USBSerialPort usbSerialPort = (USBSerialPort) serialPortManager.createInstance(SerialPortType.USB, portSettings);
 * </pre>
 */
public interface SerialPortManager extends Module {
    /**
     * To get an instance of the type you input.
     * @param type                   <b>[required]</b> The serial port type of the instance you want to create.
     * @param serialPortSettings     <b>[Optional]</b> The configurations of the serial port instance, which has default values as following:
     *                               <ul>
     *                                    <li>Default data bits: {@link com.newland.nsdk.core.api.common.serialport.DataBits#DATA_BIT_8}</li>
     *                                    <li>Default parity bit: {@link com.newland.nsdk.core.api.common.serialport.ParityBit#NO_CHECK}</li>
     *                                    <li>Default stop bits: {@link com.newland.nsdk.core.api.common.serialport.StopBits#STOP_BIT_ONE}</li>
     *                                    <li>Default baud rate： {@link com.newland.nsdk.core.api.common.serialport.BaudRate#BPS115200}</li>
     *                                    <li>Default read/write isBlocked: true</li>
     *                               </ul>
     * @return The instance of serial port.
     * @throws NSDKException
     */
    SerialPort createInstance(SerialPortType type, SerialPortSettings serialPortSettings) throws NSDKException;

    /**
     * To create an SerialPort instance of node name you input.
     * @param nodeName               <b>[Required]</b> The node name which is used to open the serial node.
     * @param serialPortSettings     <b>[Required]</b> The configurations of the serial port instance, which has default values as following:
     *                               <ul>
     *                                    <li>Default data bits: {@link com.newland.nsdk.core.api.common.serialport.DataBits#DATA_BIT_8}</li>
     *                                    <li>Default parity bit: {@link com.newland.nsdk.core.api.common.serialport.ParityBit#NO_CHECK}</li>
     *                                    <li>Default stop bits: {@link com.newland.nsdk.core.api.common.serialport.StopBits#STOP_BIT_ONE}</li>
     *                                    <li>Default baud rate： {@link com.newland.nsdk.core.api.common.serialport.BaudRate#BPS115200}</li>
     *                                    <li>Default read/write isBlocked: true</li>
     *                               </ul>
     * @return The instance of serial port created by node name.
     * @throws NSDKException
     */
    SerialPort createInstance(String nodeName, SerialPortSettings serialPortSettings) throws NSDKException;
}
