package com.newland.sdk.module.emv;

import com.newland.sdk.mtype.common.Const.EmvSelfDefinedReference;
import com.newland.sdk.mtype.common.Const.EmvStandardReference;
import com.newland.sdk.me.module.emv.structure.AbstractEMVPackage;
import com.newland.sdk.me.module.emv.structure.EMVTagDefined;
import com.newland.sdk.mtype.util.InnerUtils;

/**
 * Get the information of a card in the emv processing<p>
 * 
 *
 * @since v1.0
 */
public class EMVCardInfo extends AbstractEMVPackage {
	
	/**
	 * Card number
	 */
	@EMVTagDefined(tag = EmvStandardReference.PAN)
	private String cardNo;
	
	/**
	 * IFD serial number
	 */
	@EMVTagDefined(tag = EmvStandardReference.INTERFACE_DEVICE_SERIAL_NUMBER)
	private String interface_device_serial_number;
	
	
	@EMVTagDefined(tag = EmvStandardReference.CARD_SEQUENCE_NUMBER)
	private String card_sequence_number;
	
	
	/**
	 * Card expiration date
	 */
	@EMVTagDefined(tag = EmvStandardReference.APP_EXPIRATION_DATE)
	private String cardExpirationDate;
	
	/**
	 * Qpboc Card balance
	 */
	@EMVTagDefined(tag = EmvStandardReference.QPBOC_CARD_FUNDS)
	private String qpbocCardBalance;
	/**
	 * Qpboc Card balance
	 */
	@EMVTagDefined(tag = EmvStandardReference.PBOC_CARD_FUNDS)
	private String pbocCardBalance;

	/**
	 * (0xDF76)
	 */
	@EMVTagDefined(tag = EmvSelfDefinedReference.ERROR_CODE)
	private Integer errorcode;

	/**
	 * pboc执行结果（0xDF75）
	 * <p>
	 * pboc implementation result（0xDF75）
	 */
	@EMVTagDefined(tag = EmvSelfDefinedReference.PBOC_PROCESS_RSLT)
	private Integer executeRslt;
	private String serviceCode;
	private String track2;

	public EMVCardInfo(String cardNo, String interface_device_serial_number, String card_sequence_number, String cardExpirationDate, String qCardBalance, String pCardBalance, String serviceCode, String track2, Integer executeRslt, Integer errorcode) {
		this.cardNo = cardNo;
		this.interface_device_serial_number = interface_device_serial_number;
		this.card_sequence_number = card_sequence_number;
		this.cardExpirationDate = cardExpirationDate;
		this.qpbocCardBalance = qCardBalance;
		this.pbocCardBalance = pCardBalance;
		this.serviceCode = serviceCode;
		this.track2 = track2;
		this.executeRslt=executeRslt;
		this.errorcode=errorcode;
	}

	/**
	 * Get the card number
	 * @return
	 */
	public String getCardNo() {
		return cardNo;
	}

	/**
	 * Get the IFD serial number
	 * @return
	 */
	public String getInterface_device_serial_number() {
		return interface_device_serial_number;
	}

	/**
	 * Get the card sequence number
	 * @return
	 */
	public String getCard_sequence_number() {
		return card_sequence_number;
	}

	/**
	 * Card expiration date
	 * @return
	 */
	public String getCardExpirationDate() {
		return cardExpirationDate;
	}

	/**
	 * Card balance
	 * @return
	 */
	public String getCardBalance() {
		String balance = (null != qpbocCardBalance ? qpbocCardBalance : pbocCardBalance);
		if (null != balance) {
			balance = InnerUtils.unPadLeft(balance, '0');
		}
		return balance;
	}

	/**
	 * Get service code
	 * @return
	 */
	public String getServiceCode() {
		return serviceCode;
	}

	/**
	 * Get second track
	 * @return
	 */
	public String getTrack2() {
		return track2;
	}

	/**
	 * Get error code
	 * @return
	 */
	public Integer getErrorcode() {
		return errorcode;
	}

	/**
	 * Get the execute result
	 * @return
	 */
	public Integer getExecuteRslt() {
		return executeRslt;
	}

}
