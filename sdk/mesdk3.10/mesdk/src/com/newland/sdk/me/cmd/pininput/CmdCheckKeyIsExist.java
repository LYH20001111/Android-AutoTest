package com.newland.sdk.me.cmd.pininput;

import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.me.cmd.serializer.IntegerSerializer;
import com.newland.sdk.me.cmd.serializer.KeyTypeSerializer;
import com.newland.sdk.module.pin.AlgorithmMode;
import com.newland.sdk.module.pin.KeyType;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;

@CommandEntity(cmdCode = { (byte) 0x1A, (byte) 0x25 }, responseClass = CmdCheckKeyIsExist.CmdCheckKeyIsExistResponse.class)
public class CmdCheckKeyIsExist extends CommonDeviceCommand {
	@InstructionField(name = "密钥类型",index=0,fixLen = 1,maxLen=1,serializer=KeyTypeSerializer.class)
	private KeyType keyType;

	@InstructionField(name="算法模式",index=1,fixLen=1,maxLen=1,serializer=CmdLoadMainKeyAndVerify.AlgorithmModeSerializer.class)
	private AlgorithmMode algorithmMode;
	
	@InstructionField(name = "密钥索引",index=2,fixLen = 1,maxLen=1,serializer=IntegerSerializer.class)
	private  int keyIndex;
	
	@InstructionField(name = "校验值",index=3,maxLen=8, serializer=ByteArrSerializer.class)
	private byte[] checkValue ;
	
	public CmdCheckKeyIsExist(KeyType keyType, AlgorithmMode algorithmMode, int keyIndex, byte[] checkValue){
		this.keyType = keyType;
		this.algorithmMode = algorithmMode;
		this.keyIndex = keyIndex;
		this.checkValue = checkValue;
	}
	@ResponseEntity
	public static class CmdCheckKeyIsExistResponse  extends AbstractSuccessResponse {
		@InstructionField(name = "秘钥是否存在",index=0,fixLen = 1,maxLen=1,serializer=IntegerSerializer.class)
		private int isExist;
		
		@InstructionField(name = "校验值",index=1,maxLen=16,serializer=ByteArrSerializer.class)
		private byte[] checkValue;

		public int getIsExist() {
			return isExist;
		}

		public byte[] getCheckValue() {
			return checkValue;
		}
		
	}

}
