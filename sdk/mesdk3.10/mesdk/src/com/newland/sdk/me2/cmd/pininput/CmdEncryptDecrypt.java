package com.newland.sdk.me2.cmd.pininput;


import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.me.cmd.serializer.ByteSerializer;
import com.newland.sdk.me.cmd.serializer.IntegerSerializer;
import com.newland.sdk.me.cmd.serializer.StringSerializer;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;
import com.newland.sdk.mtypex.serializer.AbstractEnumSerializer;
import com.newland.sdk.me2.cmd.pininput.CmdEncryptDecrypt.CmdEncryptDecryptResponse;

@CommandEntity(cmdCode = { (byte) 0x1A, (byte) 0x03 }, responseClass = CmdEncryptDecryptResponse.class)
public class CmdEncryptDecrypt extends CommonDeviceCommand {

	@InstructionField(name = "密钥索引", index = 0, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int keyIndex;

	@InstructionField(name = "加密模式", index = 1, fixLen = 1, maxLen = 1, serializer = ByteSerializer.class)
	private byte encryptType;

	@InstructionField(name = "待加密/验密数据", index = 2, maxLen = 4000, serializer = ByteArrSerializer.class)
	private byte[] input;

	@InstructionField(name = "(加密密钥)密文", index = 3, maxLen = 24, serializer = ByteArrSerializer.class)
	private byte[] wk;

	@InstructionField(name = "CBC的初始值", index = 4, maxLen = 16, serializer = ByteArrSerializer.class)
	private byte[] cbcInit;


	public CmdEncryptDecrypt(int keyIndex, byte[] wkData, byte encryptType, byte[] input, byte[] cbcInit) {
		this.encryptType = encryptType;
		if(wkData!=null){
			this.keyIndex = keyIndex;
			this.wk = wkData;
		}else{
			this.keyIndex = keyIndex;
			this.wk = new byte[0];
		}
		this.input = input;
		this.cbcInit = cbcInit;
		if(cbcInit==null){
			this.cbcInit = new byte[8];
		}
	}

	@ResponseEntity
	public static class CmdEncryptDecryptResponse extends AbstractSuccessResponse {
		@InstructionField(name = "主密钥索引", index = 0, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
		private int keyIndex;

		@InstructionField(name = "应答码", index = 1, fixLen = 2, maxLen = 2, serializer = StringSerializer.class)
		private String answerCode;

		@InstructionField(name = "加密后的密码", index = 2, maxLen = 1024, serializer = ByteArrSerializer.class)
		private byte[] encryptedPassword;

		public int getkeyIndex() {
			return keyIndex;
		}

		public String getAnswerCode() {
			return answerCode;
		}

		public byte[] getEncryptedPassword() {
			return encryptedPassword;
		}
	}
}
