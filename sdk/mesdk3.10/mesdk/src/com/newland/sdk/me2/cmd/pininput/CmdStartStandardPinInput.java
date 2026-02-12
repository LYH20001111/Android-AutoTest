package com.newland.sdk.me2.cmd.pininput;


import com.newland.sdk.me.cmd.CmdCancelAndReset;
import com.newland.sdk.me.cmd.serializer.AccountInputTypeSerializer;
import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.me.cmd.serializer.IntegerSerializer;
import com.newland.sdk.me.cmd.serializer.StringSerializer;
import com.newland.sdk.me.module.pininput.PinConfirmType;
import com.newland.sdk.mtype.DeviceRTException;
import com.newland.sdk.mtype.common.ErrorCode;
import com.newland.sdk.module.pin.AccountInputType;
import com.newland.sdk.utils.ISOUtils;
import com.newland.sdk.mtypex.cmd.AbstractNotificationResponse;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.DeviceCommand;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.PaddingType;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.AbortableDeviceCommand;
import com.newland.sdk.mtypex.serializer.AbstractEnumSerializer;
import com.newland.sdk.me2.cmd.pininput.CmdStartStandardPinInput.CmdStartStandardPinInputResponse;
import com.newland.sdk.me2.cmd.pininput.CmdStartStandardPinInput.CmdStartStandardPinInputNotificationResponse;

@CommandEntity(cmdCode = { (byte) 0x1A, (byte) 0x01 }, responseClass = CmdStartStandardPinInputResponse.class, notificationResponseClass = CmdStartStandardPinInputNotificationResponse.class)
public class CmdStartStandardPinInput extends AbortableDeviceCommand {

	private static final int MONITOR_SWIPER = 0x01;
	private static final int MONITOR_ICCARD = 0x02;
	private static final int MONITOR_CHECK_TRACK = 0x80;
	private static final int MONITOR_DISABLE = 0x00;

	public static class PinConfirmTypeSerializer extends AbstractEnumSerializer {

		public PinConfirmTypeSerializer() {
			super(PinConfirmType.class, new byte[][] { { 0x00 }, { 0x01 } });
		}
	}


	@InstructionField(name = "密钥索引", index = 0, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int keyIndex;

	@InstructionField(name = "密钥类型", index = 1, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int pinManageType;

	@InstructionField(name = "主账号指示位", index = 2, fixLen = 1, maxLen = 1, serializer = AccountInputTypeSerializer.class)
	private AccountInputType acctInputType;

	@InstructionField(name = "主账号/主账号哈希值", index = 3, fixLen = 20, maxLen = 20, padding = 'F', paddingType = PaddingType.RIGHT, serializer = ByteArrSerializer.class)
	private byte[] acctSymbol;

	@InstructionField(name = "密钥", index = 4, maxLen = 24, serializer = ByteArrSerializer.class)
	private byte[] wkBody;

	@InstructionField(name = "输入密码的最大长度", index = 5, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int inputMaxLen;

	@InstructionField(name = "加密附加输入", index = 6, fixLen = 10, maxLen = 10, paddingType = PaddingType.RIGHT, serializer = ByteArrSerializer.class)
	private byte[] pinPadding;

	@InstructionField(name = "是否启用回车键", index = 7, fixLen = 1, maxLen = 1, serializer = PinConfirmTypeSerializer.class)
	private PinConfirmType pinConfirmType;

	@InstructionField(name = "按键超时", index = 8, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int timeout;

	@InstructionField(name = "显示数据", index = 9, maxLen = 64, serializer = StringSerializer.class)
	private String displayContent;

	@InstructionField(name = "数字键声音", index = 10, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int numKeySound = 0x01;

	@InstructionField(name = "*键声音", index = 11, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int starKeySound = 0x01;

	@InstructionField(name = "#键声音", index = 12, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int poundKeySound = 0x01;

	@InstructionField(name = "取消键声音", index = 13, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int cancelKeySound = 0x01;

	@InstructionField(name = "退格键声音", index = 14, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int backspaceKeySound = 0x01;

	@InstructionField(name = "确认键声音", index = 15, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int enterKeySound = 0x01;

	@InstructionField(name = "外设监听功能", index = 16, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int funcKeyCode = 0;

	@InstructionField(name = "密码长度控制", index = 17, maxLen = 20, serializer = ByteArrSerializer.class)
	private byte[] pwdInputRange;

	@InstructionField(name = "Pinblock模式", index = 18, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int pinblockModel;

	public CmdStartStandardPinInput(int keyIndex, byte[] wkData, int pinManageType, AccountInputType acctInputType, String acctSymbol, int inputMaxLen, byte[] pinPadding, PinConfirmType pinConfirmType, String displayContent, int timeout, byte[] pwdInputRange, int pinblockModel) {
		if(wkData!=null){
			this.keyIndex = keyIndex;
			this.wkBody = wkData;
		}else{
			this.keyIndex = keyIndex;
			this.wkBody = new byte[0];
		}
		this.pinManageType = pinManageType;
		this.acctInputType = acctInputType;
		if (acctInputType == AccountInputType.USE_ACCOUNT) {
			this.acctSymbol = acctSymbol.getBytes();
		} else if (acctInputType == AccountInputType.USE_ACCT_HASH) {
			this.acctSymbol = ISOUtils.hex2byte(acctSymbol);
		} else if (acctInputType == AccountInputType.UNUSE_ACCOUNT) {
			if (acctSymbol != null) {
				this.acctSymbol = acctSymbol.getBytes();
			}
		} else {
			throw new DeviceRTException(ErrorCode.UNKNOWN, "not support account input type!");
		}
		this.inputMaxLen = inputMaxLen;
		this.pinPadding = pinPadding;
		this.pinConfirmType = pinConfirmType;
		this.timeout = timeout;
		this.displayContent = displayContent;
		this.pwdInputRange = pwdInputRange;
		this.pinblockModel = pinblockModel;
	}


	public CmdStartStandardPinInput(int keyIndex, byte[] wkData, int pinManageType, AccountInputType acctInputType, String acctSymbol, int inputMaxLen, byte[] pinPadding, PinConfirmType pinConfirmType, String displayContent, int timeout, int pinblockModel) {
		if(wkData!=null){
			this.keyIndex = keyIndex;
			this.wkBody = wkData;
		}else{
			this.keyIndex = keyIndex;
			this.wkBody = new byte[0];
		}
		this.pinManageType = pinManageType;
		this.acctInputType = acctInputType;
		if (acctInputType == AccountInputType.USE_ACCOUNT) {
			this.acctSymbol = acctSymbol.getBytes();
		} else if (acctInputType == AccountInputType.USE_ACCT_HASH) {
			this.acctSymbol = ISOUtils.hex2byte(acctSymbol);
		} else {
			throw new DeviceRTException(ErrorCode.UNKNOWN, "not support account input type!");
		}
		this.inputMaxLen = inputMaxLen;
		this.pinPadding = pinPadding;
		this.pinConfirmType = pinConfirmType;
		this.timeout = timeout;
		this.displayContent = displayContent;
		this.pinblockModel = pinblockModel;
	}

	@ResponseEntity
	public static class CmdStartStandardPinInputResponse extends AbstractSuccessResponse {
		@InstructionField(name = "返回的功能键", index = 0, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
		private int returnKey;

		@InstructionField(name = "加密后的密码长度", index = 1, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
		private int cypherLength;

		@InstructionField(name = "加密后的PIN BLOCK", index = 2, fixLen = 8,maxLen=8, serializer = ByteArrSerializer.class)
		private byte[] encryptPinBlock;

		@InstructionField(name = "KSN", index = 3, fixLen = 10, maxLen = 10, serializer = ByteArrSerializer.class)
		private byte[] ksn;

		public int getCyherLength() {
			return cypherLength;
		}

		public int getReturnKey() {
			return returnKey;
		}

		public byte[] getKsn() {
			return ksn;
		}

		public byte[] getEncryptPinBlock() {
			return encryptPinBlock;
		}

	}

	@ResponseEntity
	public static class CmdStartStandardPinInputNotificationResponse extends AbstractNotificationResponse {
		@InstructionField(name = "返回的功能键", index = 0, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
		private int returnKey;

		public int getReturnKey() {
			return returnKey;
		}
	}

	@Override
	public DeviceCommand getAbortCommand() {
		return new CmdCancelAndReset();
	}

	private enum keyManagementDeprecated {

		/**
		 * MK/SK
		 */
		MKSK,
		/**
		 * DUKPT
		 */
		DUKPT,
		/**
		 * FIXED
		 */
		FIXED,
		/**
		 * Beijing Lakala adopts this type.
		 */
		FIXED_BEIJING_LAKAL,
		/**
		 * Shanghai Lakala encryption type
		 */
		MKSK_SHANGHAI_LAKAL,
		/**
		 * Hanyin encryption type
		 */
		FIXED_HANYIN,
		/**
		 * SM4
		 */
		SM4,
		/**
		 * AES
		 */
		MKSK_AES,
	}

	class keyManagementDeprecatedSerializer  extends AbstractEnumSerializer{

		public keyManagementDeprecatedSerializer() {
			super(keyManagementDeprecated.class, new byte[][]{{0x00},{0x01},{0x02},{0x03},{0x04},{0x05},{0x07},{0x08}});
		}
	}
}
