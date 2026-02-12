package com.newland.sdk.module.serialport;

import android.support.annotation.Nullable;

import com.newland.sdk.mtype.Module;

/**
 * @description:  Serial port communication
 * @author: Lindan
 * @create: 2019/7/26
 */

public interface SerialPortModule extends Module {

	/**
	 * Open and set serial communication parameters
	 * @param portType The type of serial port {@link PortType#RS232}
	 * @param baudrate Baud Rate{@link Baudrate#BPS115200}
	 * @param params Extra serial parameters {@link SerialExtParams}, if can be null.
	 * @return 0 : Open serial port success<p>
	 *         <0 : Failure
	 * @since 3.10.01
	 */
	public int open(PortType portType, Baudrate baudrate,@Nullable SerialExtParams params);
	
	/** 
	 * Get the JNI version
	 * @return
	 * @since 3.10.01
	 */
	public String getVersion();

	/**
	 * Read serial port data
	 * @param outputData Read out data
	 * @param lengthMax The max length to read
	 * @param timeOut  Timeout(Time Unit:ms), read data right now if time <= 0
	 * @return   If result >= 0, means successful reading and the result is the length of the data.
	 *  <p> -1:Failure
	 *  @since 3.10.01
	 */
	public int read(byte[] outputData, int lengthMax, int timeOut);
	/**
	 * Write serial port data
	 * @param inputData Data to be written
	 * @param lengthMax  The max length to write
	 * @param timeOut Timeout(Time Unit:ms), write data right now if time <= 0
	 * @return >=0:The length of data written <p> <0:Failure
	 * @since 3.10.01
	 */
	public int write(byte[] inputData, int lengthMax, int timeOut);
	/** 
	 * Close serial port
	 * @return  the node of the device
	 * @since 3.10.01
	 */
	public int close();

	/** 
	 * Clear serial port buffer
	 * @param type  Serial buffer type  <p>
	 *              =0:clear input serial buffer<p>
	 *              =1:clear output serial buffer<p>
	 *              =2:clear input serial buffer and output serial buffer<p>
	 * @return      true:success; false:failure;
	 * @since 3.10.01
	 */
	public boolean clearBuffer(int type);
	
	/** 
	 * Check the status of serial data<p>
	 * @param type  Serial buffer type   <p>
	 *              =0: input serial buffer<p>
	 *              =1: output serial buffer<p>
	 * @return  true:empty; false:not empty
	 * @since 3.10.01
	 */
	boolean isBufferEmpty(int type);

	/**
	 * only support N850,N950S-C module PINPAD port.
	 */
	int getBufferLength(PortType type);
}
