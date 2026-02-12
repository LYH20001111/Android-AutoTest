package com.newland.sdk.module.emv;

import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.me.module.emv.structure.PbocTransFormat;
import com.newland.sdk.me.module.emv.structure.PbocTransFormatGrid;

import java.lang.reflect.Field;

/**
 *EMV transaction log<p>
 * 
 *
 */
public class EMVTransLog {
	
	private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger(EMVTransLog.class);
	
	/**
	 * Transaction date
	 */
	private byte[] tradeDate;
	/**
	 * Transaction time
	 */
	private byte[] tradeTime;
	/**
	 * Transaction amount
	 */
	private byte[] tradeAmount;
	/**
	 * Other amount
	 */
	private byte[] otherAmount;
	/**
	 * Country code
	 */
	private byte[] countryCode;
	/**
	 * Amount code
	 */
	private byte[] currencyCode;
	/**
	 *  Merchant name
	 */
	private byte[] merchantName;
	/**
	 * Transaction  type
	 */
	private byte[] tradeType;
	
	/**
	 * Transaction  counter
	 */
	private byte[] transCount;
	
	private PbocTransFormat format;
	
	public EMVTransLog(byte[] transLog , PbocTransFormat format){
		this.format = format;
		init(transLog);
	}
	
	public EMVTransLog(byte[] transLog){
		this.tradeDate=new byte[3];
		System.arraycopy(transLog, 0, tradeDate, 0, 3);
		this.tradeTime=new byte[3];
		System.arraycopy(transLog, 3, tradeTime, 0, 3);
		this.tradeAmount=new byte[6];
		System.arraycopy(transLog, 6, tradeAmount, 0, 6);
		this.otherAmount=new byte[6];
		System.arraycopy(transLog, 12, otherAmount, 0, 6);
		this.countryCode=new byte[2];
		System.arraycopy(transLog, 18, countryCode, 0, 2);
		this.currencyCode=new byte[2];
		System.arraycopy(transLog, 20, currencyCode, 0, 2);
		this.merchantName=new byte[20];
		System.arraycopy(transLog, 22, merchantName, 0, 20);
		this.tradeType=new byte[1];
		System.arraycopy(transLog, 42, tradeType, 0, 1);
		this.transCount=new byte[2];
		System.arraycopy(transLog, 43, transCount, 0, 2);
	}
	
	//commom_emv获取到的PbocTransLog没有transCount
	public EMVTransLog(byte[] transLog , int lenth){
		this.tradeDate=new byte[3];
		System.arraycopy(transLog, 0, tradeDate, 0, 3);
		this.tradeTime=new byte[3];
		System.arraycopy(transLog, 3, tradeTime, 0, 3);
		this.tradeAmount=new byte[6];
		System.arraycopy(transLog, 6, tradeAmount, 0, 6);
		this.otherAmount=new byte[6];
		System.arraycopy(transLog, 12, otherAmount, 0, 6);
		this.countryCode=new byte[2];
		System.arraycopy(transLog, 18, countryCode, 0, 2);
		this.currencyCode=new byte[2];
		System.arraycopy(transLog, 20, currencyCode, 0, 2);
		this.merchantName=new byte[20];
		System.arraycopy(transLog, 22, merchantName, 0, 20);
		this.tradeType=new byte[1];
		System.arraycopy(transLog, 42, tradeType, 0, 1);
	}
	private void init(byte[] transLog) {
		int offset = 0;
		byte[] value = new byte[0];
		for(PbocTransFormatGrid grid:format.getGridlist()){
			String methodName = grid.getMethodName();
			int len = grid.getLen();
			value = new byte[len];
			System.arraycopy(transLog, offset, value, 0, len);
			offset += len;
			try {
				Field f = getClass().getDeclaredField(methodName);
				f.setAccessible(true);
				f.set(this, value);
			} catch (Exception e) {
				devicelogger.error("failed to get value!",e);
			} 
		}
	}

	public byte[] getTradeDate() {
		return tradeDate;
	}

	public byte[] getTradeTime() {
		return tradeTime;
	}

	public byte[] getTradeAmount() {
		return tradeAmount;
	}

	public byte[] getOtherAmount() {
		return otherAmount;
	}

	public byte[] getCountryCode() {
		return countryCode;
	}

	public byte[] getCurrencyCode() {
		return currencyCode;
	}

	public byte[] getMerchantName() {
		return merchantName;
	}

	public byte[] getTradeType() {
		return tradeType;
	}

	public byte[] getTransCount() {
		return transCount;
	}


}
