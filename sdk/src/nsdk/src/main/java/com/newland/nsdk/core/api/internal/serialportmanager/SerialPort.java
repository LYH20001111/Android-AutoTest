package com.newland.nsdk.core.api.internal.serialportmanager;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.serialport.SerialPortSettings;

/**
 * Provides the ability to communicate with devices on serial ports.
 *
 * <p>To get the instance of this module should use {@link SerialPortManager#createInstance(SerialPortType, SerialPortSettings)} to create. </p>
 * <p>For Example:</p>
 * <pre>
 *     SerialPortManager serialPortManager = (SerialPortManager) NSDKModuleManagerImpl.getInstance().getModule(ModuleType.SERIAL_PORT_MANAGER);
 *     //Configs of serial port
 *     SerialPortSettings portSettings = new SerialPortSettings(BaudRate.115200, DataBits.DATA_BIT_8, ParityBit.NO_CHECK, StopBits.STOP_BIT_ONE, true);
 *     SerialPort serialPort = serialPortManager.createInstance(SerialPortType.RS232, portSettings);
 * </pre>
 */
public interface SerialPort {
    /**
     * Sets the config of port.
     * <p>Note: For {@link SerialPortType#PINPAD} or {@link SerialPortType#RS232}, this shall be call before "open()". For {@link SerialPortType#USB}, this shall be used after "open()".</p>
     * @param settings   The configurations set to serial port.
     * @throws NSDKException
     */
    void setConfig(SerialPortSettings settings) throws NSDKException;

    /**
     * Set the hardware flow control property for the serial port. This is only used in U2000 devices now.
     *
     * @param enableFlowControl Whether to enable hardware flow control. Use 'true' to enable it and 'false' to disable it.
     *                         Enabling hardware flow control makes CTS (Clear To Send) and DTS (Data Terminal Ready) signals effective.
     *
     * @throws NSDKException if an exception occurs while setting the hardware flow control property.
     */
    void setHardwareFlowControl(boolean enableFlowControl) throws NSDKException;

    /**
     * For U2000 devices to awake external device.
     * @throws NSDKException
     */
    void awakeExternalDevice() throws NSDKException;

    /**
     * Gets external power supply configuration
     * @return Whether support external power supply or not.
     * @throws NSDKException
     */
    boolean getExternalPowerSupply() throws NSDKException;

    /**
     * Open device serial port.
     * @throws NSDKException
     */
    void open() throws NSDKException;

    /**
     * Clear the data in cache buffer.
     * @throws NSDKException
     */
    void flush() throws NSDKException;

    /**
     * Read port data within timeout.
     * @param length   <b>[Required]</b> The length of data you want to read in serial port.
     *                     <ul>USB:supports no more than 2048 bytes data.</ul>
     *                     <ul>RS232:supports no more than 2048 bytes data.</ul>
     *                     <ul>PINPAD:supports no moer than 512 bytes data.</ul>
     * @param timeoutMs  <b>[Required]</b> Timeout(ms), 0~20000 ms(For PINPAD, timeout shall be > 0).Only if {@link SerialPortSettings#isBlocked} is true, timeout makes sense.
     * @return The data which is read from serial port within timeout.
     * @throws NSDKException
     */
    byte[] read(int length, int timeoutMs) throws NSDKException;

    /**
     * Obtain the amount of data in the port buffer.
     * <p>Note: This can only used when the data operation is not more than 2048.</p>
     * @return The amount of data in the port buffer.
     * @throws NSDKException
     */
    int readLen() throws NSDKException;

    /**
     * Write data into serial port within timeout.
     * <p>Note: For {@link SerialPortType#PINPAD}, there is no differences between blocking and no-blocking.</p>
     * @param data     <b>[Required]</b> The data to be written to serial port.
     *                     <ul>USB:supports no more than 2048 bytes data.</ul>
     *                     <ul>RS232:supports no more than 2048 bytes data.</ul>
     *                     <ul>PINPAD:supports no moer than 512 bytes data.</ul>
     * @param timeoutMs  <b>[Required]</b> Timeout(ms), 0~20000 ms. Only if {@link SerialPortSettings#isBlocked} is true, timeout makes sense.
     * @return The length of data written to the serial port successfully. When serialPortType is PINPAD, it will always be -1.
     * @throws NSDKException
     */
    int write(byte[] data, int timeoutMs) throws NSDKException;

    /**
     * Returns the port type of current serial port.
     * @return Type of the current serial port.
     * @throws NSDKException
     */
    SerialPortType getPortType() throws NSDKException;

    /**
     * Operates device's driver.
     * <p>Note: This interface is reserved for expandable functions and PINPAD is not support this interface.</p>
     * @param cmd    <b>[Required]</b> The command to operate device's driver.
     * @param args   <b>[Optional]</b> The arguments of each command, which maybe not needs to input.
     * @return The result of operation to device's driver.
     * @throws NSDKException
     */
    @Deprecated
    int ioctl(int cmd, byte[] args) throws NSDKException;

    /**
     * Close the device's serial port.
     * @throws NSDKException
     */
    void close() throws NSDKException;
}
