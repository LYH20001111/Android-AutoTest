package com.newland.sdk.me2.cmd.pininput;


import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.me.cmd.serializer.ByteSerializer;
import com.newland.sdk.module.pin.KeyboardRandom;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;
import com.newland.sdk.me2.cmd.pininput.CmdRandomKeyboard.CmdRandomKeyboardResponse;

@CommandEntity(cmdCode = { (byte) 0x1A, (byte) 0x22 }, responseClass = CmdRandomKeyboardResponse.class)
public class CmdRandomKeyboard extends CommonDeviceCommand {
	private final byte[] DEFAULT_LAYOUT = { (byte) 0x00, (byte) 0x88, (byte) 0x01, (byte) 0x89, (byte) 0x00, (byte) 0x91, (byte) 0x01, (byte) 0x0B,
			(byte) 0x01, (byte) 0x06, (byte) 0x01, (byte) 0x89, (byte) 0x01, (byte) 0x0F, (byte) 0x01, (byte) 0x0B, (byte) 0x01, (byte) 0x84, (byte) 0x01,
			(byte) 0x88, (byte) 0x01, (byte) 0x8C, (byte) 0x01, (byte) 0x0B, (byte) 0x02, (byte) 0x01, (byte) 0x01, (byte) 0x88, (byte) 0x00, (byte) 0x13,
			(byte) 0x01, (byte) 0x80, (byte) 0x00, (byte) 0x88, (byte) 0x01, (byte) 0xFE, (byte) 0x00, (byte) 0x91, (byte) 0x01, (byte) 0x80, (byte) 0x01,
			(byte) 0x06, (byte) 0x01, (byte) 0xFE, (byte) 0x01, (byte) 0x0F, (byte) 0x01, (byte) 0x80, (byte) 0x01, (byte) 0x84, (byte) 0x01, (byte) 0xFD,
			(byte) 0x01, (byte) 0x8C, (byte) 0x01, (byte) 0x80, (byte) 0x02, (byte) 0x01, (byte) 0x01, (byte) 0xFD, (byte) 0x00, (byte) 0x13, (byte) 0x01,
			(byte) 0xF5, (byte) 0x00, (byte) 0x88, (byte) 0x02, (byte) 0x78, (byte) 0x00, (byte) 0x99, (byte) 0x01, (byte) 0xF5, (byte) 0x01, (byte) 0x0E,
			(byte) 0x02, (byte) 0x78, (byte) 0x01, (byte) 0x1F, (byte) 0x01, (byte) 0xF5, (byte) 0x01, (byte) 0x94, (byte) 0x02, (byte) 0x78, (byte) 0x00,
			(byte) 0x19, (byte) 0x02, (byte) 0x6A, (byte) 0x00, (byte) 0x88, (byte) 0x02, (byte) 0XF0, (byte) 0x00, (byte) 0x99, (byte) 0x02, (byte) 0x6A,
			(byte) 0x01, (byte) 0x0E, (byte) 0x02, (byte) 0XF0, (byte) 0x01, (byte) 0x1F, (byte) 0x02, (byte) 0x6A, (byte) 0x01, (byte) 0x94, (byte) 0x02,
			(byte) 0XF0, (byte) 0x01, (byte) 0XA5, (byte) 0x01, (byte) 0xF5, (byte) 0x02, (byte) 0x8F, (byte) 0x02, (byte) 0x5A };

	@InstructionField(name = "坐标数据", index = 0, maxLen = 256, serializer = ByteArrSerializer.class)
	private byte[] coordinate;
	@InstructionField(name = "键盘布局模式", index = 1, maxLen = 1, fixLen = 1, serializer = ByteSerializer.class)
	private byte random = 0x00;
	@InstructionField(name = "键值序列", index = 2, maxLen = 15, serializer = ByteArrSerializer.class)
	private byte[] numSeq;

	public CmdRandomKeyboard(KeyboardRandom keyboardRandom) {
		if (null == keyboardRandom.getCoordinate()) {
			this.coordinate = DEFAULT_LAYOUT;
		}
		this.coordinate = keyboardRandom.getCoordinate();
		this.random=keyboardRandom.getRandomLayout();
		this.numSeq=keyboardRandom.getKeySeq();
	}

	@ResponseEntity
	public static class CmdRandomKeyboardResponse extends AbstractSuccessResponse {
		@InstructionField(name = "键值数据", index = 0, maxLen = 24, serializer = ByteArrSerializer.class)
		private byte[] keyCodes;

		public byte[] getKeyCodes() {
			return keyCodes;
		}
	}
}
