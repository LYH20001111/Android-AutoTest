package com.newland.nsdk.core.api.internal.serialportmanager;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.serialport.SerialPortSettings;

/**
 * Provides the ability to communicate with devices on USB.
 *
 * <p>To get the instance of this module should use {@link SerialPortManager#createInstance(SerialPortType, SerialPortSettings)} to create. </p>
 * <p>For Example:</p>
 * <pre>
 *     SerialPortManager serialPortManager = (SerialPortManager) NSDKModuleManagerImpl.getInstance().getModule(ModuleType.SERIAL_PORT_MANAGER);
 *     //Configs of serial port
 *     SerialPortSettings portSettings = new SerialPortSettings(BaudRate.115200, DataBits.DATA_BIT_8, ParityBit.NO_CHECK, StopBits.STOP_BIT_ONE, true);
 *     USBSerialPort serialPort = (USBSerialPort) serialPortManager.createInstance(SerialPortType.RS232, portSettings);
 * </pre>
 */
public interface USBSerialPort extends SerialPort{
    /**
     * Gets available port name.
     * @return
     * @throws NSDKException
     */
    String getPortName() throws NSDKException;
}
