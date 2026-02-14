package com.newland.nsdk.core.api.internal.analogserial;

import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.serialport.BaudRate;
import com.newland.nsdk.core.api.common.serialport.SerialPortSettings;
import com.newland.nsdk.core.api.internal.serialportmanager.SerialPortType;

/**
 * Provides analog serial related operations
 *
 * <p>How to get this Module:</p>
 * <pre>
 *     Note:Declare permission in AndroidManifest.xml before using this interface
 *     < uses-permission android:name="android.permission.MANAGE_ANALOG_SERIAL" />
 *
 *     AnalogSerialManager mAnalogSerialManager = (AnalogSerialManager) NSDKModuleManagerImpl.getInstance().getModuleType(ModuleType.ANALOG_SERIAL);
 * </pre>
 * @deprecated This module can use {@link com.newland.nsdk.core.api.internal.serialportmanager.USBSerialPort} instead, which created by {@link com.newland.nsdk.core.api.internal.serialportmanager.SerialPortManager#createInstance(SerialPortType, SerialPortSettings)}.
 */
public interface AnalogSerialManager extends Module {

    /**
     * Open device analog serial.
     *
     * <p>Note:This shall be called before read, write and ioctl, supports X5.</p>
     *
     * @throws NSDKException
     */
    void open() throws NSDKException;

    /**
     * Open designated portName device analog serial
     * <p>Note:This shall be called before read, write and ioctl, and shall be called after getPostName() to get available port name.</p>
     * <p>For Example:</p>
     * <pre>
     *     AnalogSerialManager mAnalogSerialManager = (AnalogSerialManager) NSDKModuleManagerImpl.getInstance().getModule(ModuleType.ANALOG_SERIAL);
     *     try {
     *         String portName = mAnalogSerialManager.getPortName();
     *         mAnalogSerialManager.open(portName);
     *     } catch(NSDKException e) {
     *         e.printStackTrack();
     *     }
     *
     * </pre>
     *
     * @param portName <b>[Required]</b> The port designated.
     * @throws NSDKException
     */
    void open(String portName) throws NSDKException;

    /**
     * Close device analog serial.
     *
     * @throws NSDKException
     */
    void close() throws NSDKException;

    /**
     * Get available port name.
     *
     * @return Available port name.
     * @throws NSDKException
     */
    String getPortName() throws NSDKException;

    /**
     * Set analog serial configuration
     *
     * <p>For Example:</p>
     * <pre>
     *        AnalogSerialManager analogSerialManager = (AnalogSerialManager) NSDKModuleManagerImpl.getInstance().getModule(ModuleType.ANALOG_SERIAL);
     *        SerialPortSettings serialPortSettings = new SerialPortSetting(BaudRate.BPS115200, DataBits.DATA_BIT_5, ParityBit.ODD_CHECK, StopBits.STOP_BIT_TWO, true, false);
     *         try {
     *             analogSerialManager.setConfig(serialPortSettings);
     *         } catch (NSDKException e) {
     *             e.printStackTrace();
     *         }
     * </pre>
     *
     *
     * @param serialPortSettings <b>[Optional]</b> Serial configurations to be set, see{@link com.newland.nsdk.core.api.common.serialport.SerialPortSettings}.
     *                <ul> If it is null, default values will be applied.
     *                <li>Default baud rate: {@link BaudRate#BPS115200}</li>
     *                <li>Default data bits: {@link com.newland.nsdk.core.api.common.serialport.DataBits#DATA_BIT_8}</li>
     *                <li>Default parity bit: {@link com.newland.nsdk.core.api.common.serialport.ParityBit#NO_CHECK} </li>
     *                <li>Default stop bits: {@link com.newland.nsdk.core.api.common.serialport.StopBits#STOP_BIT_ONE}</li>
     *                <li>Default isIrEnabled: "N"</li>
     *                <li>Default isBlocked: "N"</li>
     *
     *                </ul>
     * @throws NSDKException
     */
    void setConfig(SerialPortSettings serialPortSettings) throws NSDKException;

    /**
     * JNI interface, operate corresponding device, this should be called after calling AnalogSerialManager.open().
     *
     * <p>For Example:</p>
     * <pre>
     *     AnalogSerialManager mAnalogSerialManager = (AnalogSerialManager) NSDKModuleManagerImpl.getInstance().getModule(ModuleType.ANALOG_SERIAL);
     *     byte[] args = new byte[1];
     *     args[0] = 0;
     *     try {
     *         mAnalogSerialManager.open();
     *         mAnalogSerialManager.ioctl(0x540B, args);
     *     } catch(NSDKException e) {
     *          e.printStackTrace();
     *     }
     * </pre>
     *
     * @param cmd   <b>[Required]</b> Command. cmd = 0x540B means clear buffer, cmd = 0x541B means detect buffer data volume.
     * @param args  Optional Parameters, different args means different operation.
     *              <ul>when cmd = 0x540B:
     *              <li>TCIFLSH:args[0] = 0, clear serial input buffer.</li>
     *              <li>TCOFLSH:args[0] = 1, clear serial output buffer.</li>
     *              <li>TCIOFLSH:args[0] = 2, clear serial input and output buffer.</li>
     *              </ul>
     *              <ul>when cmd = 0x541B:
     *              <li>FINEAR:args[0] = 0, check the serial port input buffer.</li>
     *              <li>FONEAR:args[0] = 1, check the serial port output buffer.</li>
     *              </ul>
     * @return Result of ioctl.
     *              <ul>result = 0: success.
     *              <li>when cmd = 0x540B, clear serial buffer success</li>
     *              <li>when cmd = 0x541B, serial buffer is null</li>
     *              </ul>
     *              <ul>result > 0: success.
     *              <li>only when cmd = 0x541B, the length of valid buffer data.</li>
     *              </ul>
     *              <ul>result < 0: fail.
     *              <li>especially when result = -14:Unsupported Device</li>
     *              </ul>
     * @throws NSDKException
     */
    int ioctl(int cmd, byte[] args) throws NSDKException;

    /**
     * Read data from serial port, this shall be called after calling AnalogSerialManager.open().
     *
     * <p>For Example:</p>
     * <pre>
     *     AnalogSerialManager mAnalogSerialManager = (AnalogSerialManager) NSDKModuleManagerImpl.getInstance().getModule(ModuleType.ANALOG_SERIAL);
     *     try {
     *         mAnalogSerialManager.open();
     *         byte[] data = mAnalogSerialManager.read(100, 5);
     *     } catch(NSDKException e) {
     *         e.printStackTrack();
     *     }
     *
     * </pre>
     *
     * @param maxLength   <b>[required]</b> Max read data length, each time can only read no more than 2048 data.
     * @param timeout     <b>[required]</b> Read timeout(s). When timeout <=0, read immediately.
     *
     * @return Serial port data. When >0 means Read data volume, =-1 means failed.
     * @throws NSDKException
     */
    byte[] read(int maxLength, int timeout) throws NSDKException;

    /**
     * Write data to serial port, this shall be called after calling AnalogSerialManager.open().
     *
     * <p>For Example:</p>
     * <pre>
     *     AnalogSerialManager mAnalogSerialManager = (AnalogSerialManager) NSDKModuleManagerImpl.getInstance().getModule(ModuleType.ANALOG_SERIAL);
     *     byte[] data = ISOUtils.hex2byte("ABGGSJWKDH123");
     *     try {
     *         mAnalogSerialManager.open();
     *         mAnalogSerialManager.write(data, 20, 5);
     *     } catch(NSDKException e) {
     *         e.printStackTrack();
     *     }
     *
     * </pre>
     *
     * @param buf         <b>[required]</b> Data to be written to serial port.
     * @param maxLength   <b>[required]</b> Write max data length.
     * @param timeout     <b>[required]</b> Write timeout(s). When timeout <=0, write immediately.
     * @throws NSDKException
     */
    void write(byte[] buf, int maxLength, int timeout) throws NSDKException;

    /**
     * Set serial port communication mode to mac mode.
     *
     * @param isMac <b>True: mac mode, False: windows mode</b>
     *
     * @throws NSDKException
     * @deprecated
     */
    void setMacMode(boolean isMac) throws NSDKException;

    /**
     * Check the current mode whether mac mode.
     *
     * @return <b>True: mac mode, False: windows mode</b>
     * @throws NSDKException
     * @deprecated
     */
    boolean isMacMode() throws NSDKException;
}
