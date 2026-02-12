package com.newland.ndk;

import java.io.UnsupportedEncodingException;

import android.util.Log;

public class Print {
	protected Print(){
		super();
	}

	/**
	 * Print string.This function will convert all strings to be printed to matrix buffer, sending data and printing will begin after Start is called.
	 * @param pszBuf String ended with '\0' with content of ASCII or line feed
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public int NDK_PrnStr(String pszBuf){
		byte[] prnbuf = null;
		try {
			prnbuf = pszBuf.getBytes("gbk");
	//		Log.i("zheng", "len="+prnbuf.length+"  "+Integer.toHexString(prnbuf[0] & 0xff)+" "+Integer.toHexString(prnbuf[1] & 0xff));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return NDK_PrnStr_m(prnbuf);
	};

	/**
	 *Initialize printer.Clear buffer, reset print parameters (incl. font, margin, mode etc.)
	 * @param unPrnDirSwitch Enable/Disable sending data whiling printing.
							 0: Disable (Default), start all printing only after NDK_PrnStart is called.
							 1: Enable, once there is a full line of data, it will be sent to print immediately, calling NDK_PrnFeedByPixel will start paper feed and return immediately.
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_PrnInit(int unPrnDirSwitch);
	/**
	 * Print string.This function will convert all strings to be printed to matrix buffer, sending data and printing will begin after Start is called.
	 * @param pszBuf String ended with '\0' with content of ASCII or line feed
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	private native int NDK_PrnStr_m(byte[] pszBuf);

	/**
	 * Start printing.
	 * Both NDK_PrnStr and NDK_PrnImage will complete data-to-dot matrix conversion and send it to the buffer,
	 * call these functions to start sending data and printing. NDK_PrnStart will stop and return printer state after printing is completed.
	 * Applications may use the returned value to check if the printer is in the right state.
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_PrnStart();

	/**
	 * Print image.
	 * This function shall also convert bit matrix to be printed to the buffer and call NDK_PrnStart to start printing.
	 * Maximum width of thermal printer is 384 pixels. If the sum of xsize and xpos surpasses the limit above, the function will fail.
	 * As for horizontal enlargement mode, the value shall not exceed 384/2.
	 * @param unXsize Image width in pixel
	 * @param unYsize Image height in pixel
	 * @param unXpos Column of the top left of image and must meet xpos + xsize < = ndk_PR_MAXLINEWIDE (ndk_PR_MAXLINEWIDE is 384 for normal mode, 384/2 for horizontal enlargement)
	 * @param psImgBuf Dot matrix data for the image in horizontal, the first 8 dot of line 1 is byte 1, D7 is the first dot
	 * @return
	 */
	public native int NDK_PrnImage(int unXsize,int unYsize,int unXpos,byte[] psImgBuf);

	/**
	 * Get printer driver version.
	 * @param pszVer Buffer to store the version string
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_PrnGetVersion(byte[] pszVer);

	/**
	 * Set print font.
	 * @param emHZFont Chinese font
	 * @param emZMFont ASCII font
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_PrnSetFont(int emHZFont,int emZMFont);

	/**
	 *Get printer status.
	 * This function can be used to check if printer is out of paper before printing starts.
	 * @param pemStatus Printer status returned
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_PrnGetStatus(int[] pemStatus);

	/**
	 * Set print mode.
	 * @param emMode Print mode (Normal mode by default)
	 * @param unSigOrDou 0: Unidirectional; 1: Bidirectional (For dot matrix printer only)
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_PrnSetMode(int emMode,int unSigOrDou);

	/**
	 * Set print grayscale (For thermal printer only)
	 * @param unGrey Grey value: 0-5. (0: Lightest; 5: Darkest)
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_PrnSetGreyScale(int unGrey);

	/**
	 * Set left margin, word spacing and line spacing for printing.
	 * The effectiveness of setting on printer will maintain until next time.
	 * @param unBorder Left margin: 0-288 (Default: 0)
	 * @param unColumn Word spacing: 0-255(Default: 0)
	 * @param unRow Line spacing: 0-255 (Default: 0)
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_PrnSetForm(int unBorder,int unColumn, int unRow);

	/**
	 *Feed paper by pixel.Paper will not be fed immediately after the function is called. It will be saved in buffer and wait until NDK_PrnStart is called.
	 * @param unPixel Pixels to feed
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_PrnFeedByPixel(int unPixel);

	/**
	 * Enable/Disable underline.
	 * @param emStatus 0: Enable underline; 1: Disable underline
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_PrnSetUnderLine(int emStatus);

	/**
	 * Script Print
	 * @param psdata Script data
	 * @param nLen  Script data len
	 * @return On success, it returns NDK_OK; on error, it returns EM_NDK_ERR.
	 */
	public native int NDK_Script_Print(byte[] psdata,int nLen);

	/**
	 * Printer Init for CPOS device.
	 * @return
	 */
	public native int NDK_PrnModuleInit();

	/**
	 * Printer Paper cut init for CPOS device.
	 * @return
	 */
	public native int NDK_PrnCutterInit();

	/**
	 * Paper cut for CPOS device.
	 * @return
	 */
	public native int NDK_PrnCutterPerformance();

	/**
	 *
	 * @param type
	 * @param value
	 * @return
	 */
	public native int NDK_PrnSetParam(int type, int value);
}
