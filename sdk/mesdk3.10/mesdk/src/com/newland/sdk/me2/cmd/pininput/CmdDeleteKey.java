package com.newland.sdk.me2.cmd.pininput;

import com.newland.sdk.me.cmd.serializer.IntegerSerializer;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;
import com.newland.sdk.me2.cmd.pininput.CmdDeleteKey.CmdDeleteKeyResponse;

@CommandEntity(cmdCode = { (byte) 0x1A, (byte) 0x20 }, responseClass = CmdDeleteKeyResponse.class)
public class CmdDeleteKey extends CommonDeviceCommand {

	private static final int DELETE_MAIN_KEY = 0x00;
	private static final int DELETE_DATAENCRYPT_KEY = 0x01;
	private static final int DELETE_PIN_KEY = 0x02;
	private static final int DELETE_MAC_KEY = 0x03;
	private static final int DELETE_ALL_KEY = 0x04;//(0-255所有秘钥)
	private static final int DELETE_INDEX_KEY = 0x05;//(1-200的秘钥)

	@InstructionField(name = "密钥类型", index = 0, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int keyType;

	@InstructionField(name = "密钥索引", index = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int keyIndex;


	public CmdDeleteKey(int keyType, int keyIndex) {
		this.keyType = keyType;
		this.keyIndex = keyIndex;
	}
	public CmdDeleteKey(int keyType) {
		this.keyType = keyType;
	}
	public static CmdDeleteKey deleteMainKey(int mainKeyIndex) {
		return new CmdDeleteKey(DELETE_MAIN_KEY, mainKeyIndex);
	}

	public static CmdDeleteKey deleteWorkingKey(int workingKeyType, int workingKeyIndex) {
		return new CmdDeleteKey(workingKeyType, workingKeyIndex);
	}

	public static CmdDeleteKey deleteAllKey() {
		return new CmdDeleteKey(DELETE_ALL_KEY);
	}
	public static CmdDeleteKey deleteIndexKey() {
		return new CmdDeleteKey(DELETE_INDEX_KEY);
	}
	@ResponseEntity
	public static class CmdDeleteKeyResponse extends AbstractSuccessResponse {
	}
}
