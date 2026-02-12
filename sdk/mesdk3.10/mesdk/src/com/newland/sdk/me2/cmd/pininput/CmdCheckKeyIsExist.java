package com.newland.sdk.me2.cmd.pininput;


import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.me.cmd.serializer.IntegerSerializer;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.me2.cmd.pininput.CmdCheckKeyIsExist.CmdCheckKeyIsExistResponse;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;

@CommandEntity(cmdCode = { (byte) 0x1A, (byte) 0x25 }, responseClass = CmdCheckKeyIsExistResponse.class)
public class CmdCheckKeyIsExist  extends CommonDeviceCommand {
	@InstructionField(name = "密钥类型",index=0,fixLen = 1,maxLen=1,serializer=IntegerSerializer.class)
	private int keyType;
	
	@InstructionField(name = "密钥索引",index=1,fixLen = 1,maxLen=1,serializer=IntegerSerializer.class)
	private  int keyIndex;
	
	@InstructionField(name = "校验值",index=2,maxLen=8, serializer=ByteArrSerializer.class)
	private byte[] checkValue ;
	
	public CmdCheckKeyIsExist(int keyType,int keyIndex,byte[] checkValue){
		this.keyType = keyType;
		this.keyIndex = keyIndex;
		this.checkValue = checkValue;
	}
	@ResponseEntity
	public static class CmdCheckKeyIsExistResponse  extends AbstractSuccessResponse {
		@InstructionField(name = "秘钥是否存在",index=0,fixLen = 1,maxLen=1,serializer=IntegerSerializer.class)
		private int isExist;
		
		@InstructionField(name = "校验值",index=1,fixLen = 4,maxLen=4,serializer=ByteArrSerializer.class)
		private byte[] checkValue;

		public int getIsExist() {
			return isExist;
		}

		public byte[] getCheckValue() {
			return checkValue;
		}
		
	}

}
