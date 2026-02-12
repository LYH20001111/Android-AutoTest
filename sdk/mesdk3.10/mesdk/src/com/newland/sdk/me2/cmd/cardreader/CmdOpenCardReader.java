package com.newland.sdk.me2.cmd.cardreader;

import com.newland.sdk.me.cmd.CmdCancelAndReset;
import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.me.cmd.serializer.ByteSerializer;
import com.newland.sdk.me.cmd.serializer.IntegerSerializer;
import com.newland.sdk.me.module.cardreader.OpenCardReaderResult;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.module.cardreader.CardType;
import com.newland.sdk.module.rfcard.RFCardType;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.mtypex.cmd.AbstractNotificationResponse;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.DeviceCommand;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.AbortableDeviceCommand;
import com.newland.sdk.me2.cmd.cardreader.CmdOpenCardReader.CmdOpenCardReaderResponse;
import java.util.ArrayList;
import java.util.List;

@CommandEntity(cmdCode = { (byte) 0xD1, (byte) 0x01 }, responseClass = CmdOpenCardReaderResponse.class)
public class CmdOpenCardReader extends AbortableDeviceCommand {

	private DeviceLogger logger = DeviceLoggerFactory.getLogger(CmdOpenCardReader.class);

	private static final int MASK_SWIPER = 0x01;

	private static final int MASK_ICCARD = 0x02;

	private static final int MASK_RFCARD = 0x04;

	private static final int A_RFCARD = 0x14;

	private static final int B_RFCARD = 0x24;

	private static final int M1_RFCARD = 0x44;

	private static final int CHECK_TRACK = 0x20;

	@InstructionField(name = "读卡模式", index = 0, fixLen = 1, maxLen = 1, serializer = ByteSerializer.class)
	private byte openCardType = 0x00;

	@InstructionField(name = "超时时间", index = 1, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int timeout;

	@InstructionField(name = "屏显信息", index = 2, maxLen = 44, serializer = ByteArrSerializer.class)
	private byte[] screenShow = new byte[0];

	@InstructionField(name = "刷卡规则", index = 3, fixLen = 1, maxLen = 1, serializer = ByteSerializer.class)
	private byte opencardrule = 0x20;

	@InstructionField(name = "非接寻卡有效次数", index = 4, fixLen = 1, maxLen = 1, serializer = ByteArrSerializer.class)
	private byte[] effectivetimes = InnerUtils.intToBytes(2, 1, true);

	@InstructionField(name = "寻卡时间间隔", index = 5, fixLen = 2, maxLen = 2, serializer = ByteArrSerializer.class)
	private byte[] intervaltimes = InnerUtils.intToBytes(500, 2, true);

	public CmdOpenCardReader(String screenText, CardType[] openReaders, RFCardType[] expectedRfCardTypes, boolean isAllowfallback, boolean isMSDChecking,
							 int timeout) {
		for (CardType moduleType : openReaders) {
			if (moduleType == CardType.MSGCARD) {
				openCardType |= MASK_SWIPER;
			} else if (moduleType == CardType.ICCARD) {
				openCardType |= MASK_ICCARD;
			} else if (moduleType == CardType.RFCARD) {
				openCardType |= MASK_RFCARD;
			} else {
				throw new IllegalArgumentException("not supported operation!");
			}
		}
		if (null != expectedRfCardTypes) {
			for (RFCardType oct : expectedRfCardTypes) {
				if (oct == RFCardType.ACARD) {
					this.openCardType |= A_RFCARD;
				} else if (oct == RFCardType.BCARD) {
					this.openCardType |= B_RFCARD;
				} else if (oct == RFCardType.M1CARD) {
					this.openCardType |= M1_RFCARD;
				}
			}
		}
		if (!isMSDChecking) {
			opencardrule = 0x00;
		}
		if (isAllowfallback) {
			opencardrule = 0x00;
		}
		this.timeout = timeout;
		try {
			this.screenShow = screenText.getBytes("GBK");
		} catch (Exception e) {
			logger.error("failed to getBytes!", e);
		}
	}

	@Override
	public DeviceCommand getAbortCommand() {
		return new CmdCancelAndReset();
	}

	@ResponseEntity
	public static final class CmdOpenCardReaderResponse extends AbstractSuccessResponse {
		private static final long serialVersionUID = 7980345612313197152L;
		private DeviceLogger logger = DeviceLoggerFactory.getLogger(CmdOpenCardReaderResponse.class);

		@InstructionField(name = "开启的读卡模式", index = 0, fixLen = 1, maxLen = 1, serializer = ByteSerializer.class)
		private byte modelMask;

		@InstructionField(name = "刷卡结果", index = 1, maxLen = 1, serializer = ByteSerializer.class)
		private Byte cardResultType;

		@InstructionField(name = "SNR", index = 2, maxLen = 256, serializer = ByteArrSerializer.class)
		private byte[] snr;

		public CardType[] getCardInputTypes() {
			List<CardType> moduleTypes = new ArrayList<CardType>();
			if ((modelMask & MASK_SWIPER) != 0) {
				moduleTypes.add(CardType.MSGCARD);
			} else if ((modelMask & MASK_ICCARD) != 0) {
				moduleTypes.add(CardType.ICCARD);
			} else if ((modelMask & MASK_RFCARD) != 0) {
				moduleTypes.add(CardType.RFCARD);
			}

			return moduleTypes.toArray(new CardType[moduleTypes.size()]);
		}


		public OpenCardReaderResult getOpenCardReaderResult() {
			try {
				List<CardType> moduleTypes = new ArrayList<CardType>();
				RFCardType rfCardType = null;
				boolean isMSDDataCorrectly = true;

				if ((modelMask & MASK_SWIPER) != 0) {
					moduleTypes.add(CardType.MSGCARD);
				} else if ((modelMask & MASK_ICCARD) != 0) {
					moduleTypes.add(CardType.ICCARD);
				} else if ((modelMask & MASK_RFCARD) != 0) {
					moduleTypes.add(CardType.RFCARD);
				}

				if (0x11 == (int) (modelMask & 0xFF) || (null != cardResultType && (0x11 == (int) (cardResultType & 0xFF)))) {// 刷卡结束,但刷卡错误,建议重刷
					isMSDDataCorrectly = false;
				}
				if (null == cardResultType) {
					rfCardType = null;
				} else if ((byte) 0x14 == cardResultType) {
					rfCardType = RFCardType.ACARD;
				} else if ((byte) 0x24 == cardResultType) {
					rfCardType = RFCardType.BCARD;
				} else if ((byte) 0x44 == cardResultType) {
					rfCardType = RFCardType.M1CARD;
				}
				OpenCardReaderResult result = new OpenCardReaderResult(moduleTypes.toArray(new CardType[moduleTypes.size()]), rfCardType, isMSDDataCorrectly, snr, (byte)0x00,null);
				return result;
			} catch (Exception e) {
				return null;
			}
		}

	}

	@ResponseEntity
	public static class CmdCardreaderNotificationResponse extends AbstractNotificationResponse {

		private static final long serialVersionUID = 7980345612313196552L;
		@InstructionField(name = "读卡方式及按键", index = 0, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
		private int returnKey;

		public int getReturnKey() {
			return returnKey;
		}
	}

}
