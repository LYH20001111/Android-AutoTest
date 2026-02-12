package com.newland.sdk.module.emv;

import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.me.module.emv.structure.ECTransFormat;
import com.newland.sdk.me.module.emv.structure.ECTransFormatGrid;

import java.lang.reflect.Field;

/**
 * Ec load transaction Log
 * 
 *
 */
public class ECTransLog {

	private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger(ECTransLog.class);

	/**
	 * Transation date<p>
	 */
	private byte[] tradeDate;
	/**
	 *
	 * Transaction time<p>
	 */
	private byte[] tradeTime;
	/**
	 *
	 * CountryCode<p>
	 */
	private byte[] countryCode;
	/**
	 *
	 * MerchantName<p>
	 */
	private byte[] merchantName;
	/**
	 * DF4F
	 */
	private byte[] DF4F;

	/**
	 *
	 * The blance before load
	 */
	private byte[] blanceOld;

	/**
	 *
	 * The blance after load
	 */
	private byte[] blanceNew;

	/**
	 *
	 * Transaction count
	 */
	private byte[] transCount;

	private ECTransFormat format;
	private byte p1; // 0x9f或0xdf
	private byte p2; // 0x79

	public ECTransLog(byte[] transLog, ECTransFormat format) {
		this.format = format;
		init(transLog);
	}

	public ECTransLog(byte[] transLog) {
		this.tradeDate = new byte[3];
		System.arraycopy(transLog, 0, tradeDate, 0, 3);
		this.tradeTime = new byte[3];
		System.arraycopy(transLog, 3, tradeTime, 0, 3);
		this.countryCode = new byte[2];
		System.arraycopy(transLog, 6, countryCode, 0, 2);
		this.merchantName = new byte[20];
		System.arraycopy(transLog, 8, merchantName, 0, 20);
		this.transCount = new byte[2];
		System.arraycopy(transLog, 28, transCount, 0, 2);
	}

	// commom_emv获取到的PbocTransLog没有transCount
	public ECTransLog(byte[] transLog, int lenth) {
		this.tradeDate = new byte[3];
		System.arraycopy(transLog, 0, tradeDate, 0, 3);
		this.tradeTime = new byte[3];
		System.arraycopy(transLog, 3, tradeTime, 0, 3);
		this.countryCode = new byte[2];
		System.arraycopy(transLog, 6, countryCode, 0, 2);
		this.merchantName = new byte[20];
		System.arraycopy(transLog, 8, merchantName, 0, 20);
	}

	private void init(byte[] transLog) {
		int offset = 0;
		byte[] value = new byte[0];
		for (ECTransFormatGrid grid : format.getGridlist()) {
			String methodName = grid.getMethodName();
			int len = grid.getLen();
			value = new byte[len];
			System.arraycopy(transLog, offset, value, 0, len);
			offset += len;
			try {
				Field f = getClass().getDeclaredField(methodName);
				f.setAccessible(true);
				f.set(this, value);
				if (methodName.equals("DF4F")) {
					if (value != null) {
						p1 = value[0];
						p2 = value[1];
						byte[] mblanceOld = new byte[6];
						byte[] mblanceNew = new byte[6];
						System.arraycopy(value, 2, mblanceOld, 0, 6);
						System.arraycopy(value, 8, mblanceNew, 0, 6);
						setBlanceOld(mblanceOld);
						setBlanceNew(mblanceNew);
					}
				}
			} catch (Exception e) {
				devicelogger.error("failed to get value!", e);
			}
		}
	}

	public byte[] getTradeDate() {
		return tradeDate;
	}

	public byte[] getTradeTime() {
		return tradeTime;
	}

	public byte[] getCountryCode() {
		return countryCode;
	}

	public byte[] getMerchantName() {
		return merchantName;
	}

	public byte[] getTransCount() {
		return transCount;
	}

	public byte[] getBlanceOld() {
		return blanceOld;
	}

	public void setBlanceOld(byte[] blanceOld) {
		this.blanceOld = blanceOld;
	}

	public byte[] getBlanceNew() {
		return blanceNew;
	}

	public void setBlanceNew(byte[] blanceNew) {
		this.blanceNew = blanceNew;
	}

	public byte getP1() {
		return p1;
	}

	public byte getP2() {
		return p2;
	}

	public byte[] getDF4F() {
		return DF4F;
	}

}
