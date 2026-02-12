package com.newland.sdk.me.cmd.pininput;

import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.me.cmd.serializer.IntegerSerializer;
import com.newland.sdk.me.cmd.serializer.keyManagementSerializer;
import com.newland.sdk.me.cmd.serializer.StringHexSerializer;
import com.newland.sdk.me.cmd.serializer.StringSerializer;
import com.newland.sdk.module.pin.AlgorithmMode;
import com.newland.sdk.module.pin.CipherMode;
import com.newland.sdk.module.pin.KeyManagement;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;

@CommandEntity(cmdCode = { (byte) 0x1A, (byte) 0x03 }, responseClass = CmdEncryptDecrypt.CmdEncryptDecryptResponse.class)
public class CmdEncryptDecrypt extends CommonDeviceCommand {
	@InstructionField(name = "密钥体系", index = 0, fixLen = 1, maxLen = 1, serializer = keyManagementSerializer.class)
	private KeyManagement keyManagement;

	@InstructionField(name="算法模式",index=1,fixLen=1,maxLen=1,serializer=CmdLoadMainKeyAndVerify.AlgorithmModeSerializer.class)
	private AlgorithmMode algorithmMode;

	@InstructionField(name = "加解密模式", index = 2, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int encryptDecryptType;

	@InstructionField(name = "密钥索引", index = 3, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int keyIndex;

	@InstructionField(name = "待加密/解密数据", index = 4, maxLen = 4000, serializer = ByteArrSerializer.class)
	private byte[] input;

	@InstructionField(name = "(加密密钥)密文", index = 5, maxLen = 24, serializer = ByteArrSerializer.class)
	private byte[] wk;

	@InstructionField(name = "CBC的初始值", index = 6, maxLen = 16, serializer = ByteArrSerializer.class)
	private byte[] cbcInit;

	/**
	 * @param keyManagement
	 * @param algorithmMode
	 * @param encryptDecryptType
	 * @param keyIndex
	 * @param input
	 * @param wkData
	 * @param cbcInit
	 * @param mode 0:encry  1:decry
	 */
	public CmdEncryptDecrypt(KeyManagement keyManagement, AlgorithmMode algorithmMode, CipherMode encryptDecryptType, int keyIndex, byte[] input, byte[] wkData, byte[] cbcInit, int mode) {
		this.keyManagement = keyManagement;
		this.algorithmMode = algorithmMode;
		if(encryptDecryptType == CipherMode.ECB){
			if(mode == 0){//encry
				this.encryptDecryptType = 0x01;
			}else{
				this.encryptDecryptType = 0x03;
			}
		}else if(encryptDecryptType == CipherMode.CBC){
			if(mode == 0){//encry
				this.encryptDecryptType = 0x02;
			}else{
				this.encryptDecryptType = 0x04;
			}
		}
		this.keyIndex = keyIndex;
		this.input = input;
		this.wk = wkData;
		this.cbcInit = cbcInit;
	}



	@ResponseEntity
	public static class CmdEncryptDecryptResponse extends AbstractSuccessResponse {
		@InstructionField(name = "主密钥索引", index = 0, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
		private int keyIndex;

		@InstructionField(name = "应答码", index = 1, fixLen = 2, maxLen = 2, serializer = StringSerializer.class)
		private String answerCode;

		@InstructionField(name = "加/解密后的数据", index = 2, maxLen = 4000, serializer = ByteArrSerializer.class)
		private byte[] encDecData;

		@InstructionField(name = "ksn", index = 3, fixLen = 10, maxLen = 10, serializer = StringHexSerializer.class)
		private String ksn;

		public int getkeyIndex() {
			return keyIndex;
		}

		public String getAnswerCode() {
			return answerCode;
		}

		public byte[] getEncDecData() {
			return encDecData;
		}

		public String getKsn() {
			return ksn;
		}

	}

}
