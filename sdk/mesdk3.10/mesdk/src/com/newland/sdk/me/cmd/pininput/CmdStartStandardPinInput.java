package com.newland.sdk.me.cmd.pininput;

import com.newland.sdk.me.cmd.CmdCancelAndReset;
import com.newland.sdk.me.cmd.serializer.AccountInputTypeSerializer;
import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.me.cmd.serializer.IntegerSerializer;
import com.newland.sdk.me.cmd.serializer.keyManagementSerializer;
import com.newland.sdk.me.module.pininput.PinConfirmType;
import com.newland.sdk.module.pin.KeyManagement;
import com.newland.sdk.module.pin.PinBlockMode;
import com.newland.sdk.mtype.DeviceRTException;
import com.newland.sdk.mtype.common.ErrorCode;
import com.newland.sdk.module.pin.AccountInputType;
import com.newland.sdk.module.pin.AlgorithmMode;
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

@CommandEntity(cmdCode = { (byte) 0x1A, (byte) 0x01 }, responseClass = CmdStartStandardPinInput.CmdStartStandardPinInputResponse.class, notificationResponseClass = CmdStartStandardPinInput.CmdStartStandardPinInputNotificationResponse.class)
public class CmdStartStandardPinInput extends AbortableDeviceCommand {

	private static final int MONITOR_SWIPER = 0x01;
	private static final int MONITOR_ICCARD = 0x02;
	private static final int MONITOR_CHECK_TRACK = 0x80;
	private static final int MONITOR_DISABLE = 0x00;

	public static class PinConfirmTypeSerializer extends AbstractEnumSerializer {

		public PinConfirmTypeSerializer() {
			super(PinConfirmType.class, new byte[][] { { 0x00 }, { 0x01 }});
		}
	}

	@InstructionField(name = "密钥体系", index = 0, fixLen = 1, maxLen = 1, serializer = keyManagementSerializer.class)
	private KeyManagement keyManagement;

	@InstructionField(name="算法模式",index=1,fixLen=1,maxLen=1,serializer=CmdLoadMainKeyAndVerify.AlgorithmModeSerializer.class)
	private AlgorithmMode algorithmMode;

	@InstructionField(name = "密钥索引", index = 2, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int keyIndex;

	@InstructionField(name = "主账号指示位", index = 3, fixLen = 1, maxLen = 1, serializer = AccountInputTypeSerializer.class)
	private AccountInputType acctInputType = AccountInputType.USE_ACCOUNT;;

	@InstructionField(name = "主账号/主账号哈希值", index = 4, fixLen = 40, maxLen = 40, padding = 'F', paddingType = PaddingType.RIGHT, serializer = ByteArrSerializer.class)
	private byte[] acctSymbol;

	@InstructionField(name = "密钥", index = 5, maxLen = 24, serializer = ByteArrSerializer.class)
	private byte[] wkBody;

	@InstructionField(name = "输入密码的最大长度", index = 6, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int inputMaxLen;

	@InstructionField(name = "是否启用回车键", index = 7, fixLen = 1, maxLen = 1, serializer = PinConfirmTypeSerializer.class)
	private PinConfirmType pinConfirmType=PinConfirmType.ENABLE_ENTER;

	@InstructionField(name = "按键超时", index = 8, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int timeout;

	@InstructionField(name = "密码长度控制", index = 9, maxLen = 20, serializer = ByteArrSerializer.class)
	private byte[] pwdInputRange;

	@InstructionField(name = "Pinblock模式", index = 10, fixLen = 1,maxLen = 1, serializer = IntegerSerializer.class)
	private int pinblockModel;

	@InstructionField(name = "模数", index = 11, maxLen = 1024, serializer = ByteArrSerializer.class)
	private byte[] modulus;
	@InstructionField(name = "指数", index = 12, maxLen = 1024, serializer = ByteArrSerializer.class)
	private byte[] exponent;

	@InstructionField(name = "pinFormatMode", index = 13, fixLen = 1,maxLen = 1, serializer = IntegerSerializer.class)
	private int pinFormatMode;
	@InstructionField(name = "pinEventMode", index = 14, fixLen = 1,maxLen = 1, serializer = IntegerSerializer.class)
	private int pinEventMode = 1;//1-用事件机制，0-用非事件机制，轮询方式
	@InstructionField(name = "DukptDerivateUsage", index = 15, fixLen = 1,maxLen = 1, serializer = IntegerSerializer.class)
	private int dukptDerivateUsage;
	@InstructionField(name = "DerivateKeyLen", index = 16, fixLen = 1,maxLen = 1, serializer = IntegerSerializer.class)
	private int derivateKeyLen;
	@InstructionField(name = "isRNIB", index = 17, fixLen = 1,maxLen = 1, serializer = IntegerSerializer.class)
	private int isRNIB = 0;//0:normal,1:盲人键盘
	/**
	 * input online pin
	 * @param keyManagement
	 * @param algorithmMode
	 * @param keyIndex
	 * @param acctInputType
	 * @param acctSymbol
	 * @param wkData
	 * @param inputMaxLen
	 * @param pinConfirmType
	 * @param timeout
	 * @param pwdInputRange
	 * @param pinblockModel
	 */
	public CmdStartStandardPinInput(KeyManagement keyManagement, AlgorithmMode algorithmMode, int keyIndex,
									AccountInputType acctInputType, String acctSymbol, byte[] wkData, int inputMaxLen,
									PinConfirmType pinConfirmType, int timeout, byte[] pwdInputRange,
									int pinblockModel, PinBlockMode pinFormatMode,int dukptDerivateUsage,int derivateKeyLen,int isRNIB) {
		this.keyManagement = keyManagement;
		this.algorithmMode = algorithmMode;
		this.keyIndex = keyIndex;
		this.wkBody = wkData;
		this.acctInputType = acctInputType;
		if (acctInputType == AccountInputType.USE_ACCOUNT) {
			this.acctSymbol = acctSymbol.getBytes();
		} else if (acctInputType == AccountInputType.USE_ACCT_HASH) {
			this.acctSymbol = ISOUtils.hex2byte(acctSymbol);
		} else if (acctInputType == AccountInputType.UNUSE_ACCOUNT) {
			if(acctSymbol!=null){
				this.acctSymbol = acctSymbol.getBytes();
			}
		}else {
			throw new DeviceRTException(ErrorCode.UNKNOWN,
					"not support account input type!");
		}
		this.inputMaxLen = inputMaxLen;
		this.pinConfirmType = pinConfirmType;
		this.timeout = timeout;
		this.pwdInputRange = pwdInputRange;
		this.pinblockModel=pinblockModel;
		this.pinFormatMode = 0xFF;
		if(pinFormatMode != null){
			this.pinFormatMode = pinFormatMode.getCode();
		}
		this.dukptDerivateUsage = dukptDerivateUsage;
		this.derivateKeyLen = derivateKeyLen;
		this.isRNIB = isRNIB;
		if(isRNIB==1){
			pinEventMode = 0;
		}
	}
	/**
	 * input offline pin
	 * @param inputMaxLen
	 * @param pinConfirmType
	 * @param timeout
	 * @param pwdInputRange
	 * @param modulus
	 * @param exponent
	 */
	public CmdStartStandardPinInput(int inputMaxLen, PinConfirmType pinConfirmType, int timeout, byte[] pwdInputRange, byte[] modulus, byte[] exponent,int isRNIB) {
		this.keyManagement = KeyManagement.MKSK;
		this.algorithmMode = AlgorithmMode.DES;

		this.inputMaxLen = inputMaxLen;
		this.pinConfirmType = pinConfirmType;
		this.timeout = timeout;
		this.pwdInputRange = pwdInputRange;
		this.pinblockModel = 2;
		this.modulus = modulus;
		this.exponent = exponent;
		this.isRNIB = isRNIB;
		if(isRNIB==1){
			pinEventMode = 0;
		}
	}


	@ResponseEntity
	public static class CmdStartStandardPinInputResponse extends
			AbstractSuccessResponse {
		@InstructionField(name = "返回的功能键", index = 0, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
		private int returnKey;

		@InstructionField(name = "加密后的密码长度", index = 1, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
		private int cypherLength;

		@InstructionField(name = "PIN BLOCK", index = 2, maxLen= 32, serializer = ByteArrSerializer.class)
		private byte[] pinBlock;

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
			return pinBlock;
		}

	}

	@ResponseEntity
	public static class CmdStartStandardPinInputNotificationResponse extends
			AbstractNotificationResponse {
		@InstructionField(name = "返回的功能键", index = 0, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
		private int returnKey;
		@InstructionField(name = "keyEvent", index = 1, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
		private int keyEvent;

		public int getReturnKey() {
			return returnKey;
		}
		public int getKeyEvent() {
			return keyEvent;
		}
	}

	@Override
	public DeviceCommand getAbortCommand() {
		return new CmdCancelAndReset();
	}
}
