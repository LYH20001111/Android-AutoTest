package com.newland.sdk.module.usb;

import com.newland.sdk.mtype.Module;

/**
 * Author by bxy, Date on 2019/11/21.
 */
public interface USBModule {

    /**
     * Open usb device
     *
     * @param listener Usb device selection listener
     * @return = 0 : Open success<p>
     * < 0 : Failure
     */
    public int open(SelectUsbDeviceListener listener);

    /**
     * Read usb port data
     *
     * @param outputData Read out data
     * @param lengthMax  The max length to read
     * @param timeOut    Timeout(Time Unit:ms), read data right now if time <= 0
     * @return If result >= 0, means successful reading and the result is the length of the data.<p>
     * -1:Failure
     */
    public int read(byte[] outputData, int lengthMax, int timeOut);

    /**
     * Write usb port data
     *
     * @param inputData Data to be written
     * @param lengthMax The max length to write
     * @param timeOut   Timeout(Time Unit:ms), write data right now if time <= 0
     * @return >=0:The length of data written <p>
     * <0:Failure
     */
    public int write(byte[] inputData, int lengthMax, int timeOut);

    /**
     * Clear usb port buffer
     *
     * @return true:success; false:failure;
     */
    public boolean clearBuffer();

    /**
     * Close usb port
     *
     * @return = 0 : Success<p>
     * < 0 : Failure
     */
    public int close();

    /**
     * Set USB Serial Port Config
     *
     * @param usbSerialPortConfig {@link UsbSerialPortConfig#DATA_BITS_5}
     * @return
     * @since 3.10.42_06
     */
    public void setConfig(UsbSerialPortConfig usbSerialPortConfig);
}
