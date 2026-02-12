package com.newland.sdk.module.swiper;

import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.Dump;

/** 
 * SwipResult
 * <p>
 * 
 *
 * @since ver3.10.01
 */
public class SwipResult {
	private static DeviceLogger logger = DeviceLoggerFactory.getLogger(SwipResult.class);
	
	private SwipResultCode rsltCode;

	private Account account;

	private SwiperReadModel[] readModels;

	private byte[] firstTrackData;
	private byte[] secondTrackData;
	private byte[] thirdTrackData;


	private String validDate;

	private String serviceCode;

	private byte[] ksn;


	public SwipResult(SwipResultCode rsltCode) {
		this.rsltCode = rsltCode;
	}

	public SwipResult(Account account, SwiperReadModel[] readModels, byte[] firstTrackData, byte[] secondTrackData, byte[] thirdTrackData, String validDate, String serviceCode, byte[] ksn) {

		this.rsltCode = SwipResultCode.SUCCESS;
		this.account = account;
		this.readModels = readModels;
		this.firstTrackData = firstTrackData;
		this.secondTrackData = secondTrackData;
		this.thirdTrackData = thirdTrackData;
		this.validDate = validDate;
		this.serviceCode = serviceCode;
		this.ksn = ksn;
	}

	/** 
	 * Get the account number of card swiping
	 * 
	 * @since ver3.10.01
	 * @return
	 */
	public Account getAccount() {
		return account;
	}

	/** 
	 * Get the first track data
	 * <p>
	 * 
	 * @since ver3.10.01
	 * @return
	 */
	public byte[] getFirstTrackData() {
		return firstTrackData;
	}

	/**
	 * <p>
	 * Get the second track data
	 * <p>
	 * 
	 * @since ver3.10.01
	 * @return
	 */
	public byte[] getSecondTrackData() {
		return secondTrackData;
	}

	/**
	 * <p>
	 * Get the third track data
	 * <p>
	 * 
	 * @since ver3.10.01
	 * @return
	 */
	public byte[] getThirdTrackData() {
		return thirdTrackData;
	}

	/**
	 * Return the currently called swiping reading model
	 * 
	 * @since ver3.10.01
	 * @return
	 */
	public SwiperReadModel[] getReadModels() {
		return readModels;
	}

	/**
	 * Return the swiping result type
	 * 
	 * @since ver3.10.01
	 * @return
	 */
	public SwipResultCode getRsltCode() {
		return rsltCode;
	}

	/**
	 * <p>
	 * Get the card number validation date
	 * <p>
	 * 
	 * @since ver3.10.01
	 * @return
	 */
	public String getValidDate() {
		return validDate;
	}

	/**
	 * <p>
	 * Get the service code
	 * <p>
	 * 
	 * @since ver3.10.01
	 * @return
	 */
	public String getServiceCode() {
		return serviceCode;
	}

	/**
	 * <p>
	 * Get the device ksn number
	 * <p>
	 * 
	 * @since ver3.10.01
	 * @return
	 */
	public byte[] getKsn() {
		return ksn;
	}


	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("swipRslt(");
		sb.append("rsltCode:" + rsltCode + ",");
		sb.append("acct:" + (account == null ? "null" : account) + ",");
		sb.append("readModels:");
		if (readModels != null) {
			for (SwiperReadModel readModel : readModels) {
				sb.append(readModel + "|");
			}
		} else {
			sb.append("null");
		}
		sb.append(",");
		sb.append("firstTrackData:" + (firstTrackData == null ? "null" : Dump.getHexDump(firstTrackData)) + ",");
		sb.append("secondTrackData:" + (secondTrackData == null ? "null" : Dump.getHexDump(secondTrackData)) + ",");
		sb.append("thirdTrackData:" + (thirdTrackData == null ? "null" : Dump.getHexDump(thirdTrackData)) + ",");
		sb.append(")");
		return sb.toString();
	}

	/**
	 * Judge if the card is an IC card
	 * 
	 * @return
	 */
	public boolean isICCard() {
		if (null == serviceCode || serviceCode.trim().equals("")) {
			logger.info("serviceCode is empty!");
			return false;
		}
		String flag = serviceCode.substring(0, 1);
		if (flag.equals("2") || flag.equals("6")) {
			return true;
		}
		return false;
	}

}
