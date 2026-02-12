package com.newland.sdk.module.cashbox;

import com.newland.sdk.mtype.Module;


/**
 * cash box
 * @author lindan
 */
public interface ExtCashBoxModule extends Module {
	/**
	 * Getting working voltage
	 * @return 0 indicates 12V，1 indicates 24V
	 */
	public int getVoltage();
	/**
	 *  Setting working voltage
	 * @param mVoltage
	 * 0 indicates 12V，1 indicates 24V
	 */
	public void setVoltage(int mVoltage);
	/**
	 * Getting the current delay,unit：ms
	 * @return
	 */
	public long getTimeSec();
	/**
	 * Setting delay time
	 * @param mTimeSec unit:ms，not less than 0
	 */
	public void setTimeSec(long mTimeSec);
	/**
	 *   Opening CashBox
	 *
	 * @return 1：Open up success，-1：Open failed，-2:indicates that the param setting is unlawful
	 */
	public int OpenCashBox();
	/**
	 * @param voltage
     * Working voltage of opening CashBox
	 * @return
	 */
	public int OpenCashBox(int voltage);
	/**
	 * @param voltage Working voltage of opening CashBox
	 * @param timeSec Time-delay
	 * @return
	 */
	public int OpenCashBox(int voltage, long timeSec);
}
