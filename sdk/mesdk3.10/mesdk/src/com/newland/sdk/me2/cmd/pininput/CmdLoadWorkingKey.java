package com.newland.sdk.me2.cmd.pininput;

import com.newland.sdk.me2.cmd.pininput.CmdLoadWorkingKey.CmdLoadWorkingKeyResponse;
import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.me.cmd.serializer.IntegerSerializer;
import com.newland.sdk.me.cmd.serializer.StringSerializer;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;
import com.newland.sdk.mtypex.serializer.AbstractEnumSerializer;

@CommandEntity(cmdCode = { (byte)0x1A,(byte)0x05 }, responseClass = CmdLoadWorkingKeyResponse.class)
public class CmdLoadWorkingKey extends CommonDeviceCommand {

	@InstructionField( name = "密钥类型",index=0,fixLen = 1,maxLen=1,serializer=IntegerSerializer.class)
	private int type;
	
	@InstructionField(name = "主密钥索引",index=1,fixLen = 1,maxLen=1,serializer=IntegerSerializer.class)
	private  int mainKeyIndex;
	
	@InstructionField(name = "工作密钥索引",index=2,fixLen = 1,maxLen=1,serializer=IntegerSerializer.class)
	private int workingKeyIndex;
	
	@InstructionField(name = "密钥",index=3,maxLen=24,serializer=ByteArrSerializer.class)
	private byte[] data ;
	
	@InstructionField(name = "校验值",index=4,maxLen=8,serializer=ByteArrSerializer.class)
	private byte[] checkValue ;
	@InstructionField(name = "装载方式",index=5,fixLen=1,maxLen=1,serializer=IntegerSerializer.class)
	private int loadKeyType;

	public CmdLoadWorkingKey(int loadKeyType, int type, int mainKeyIndex, int workingKeyIndex, byte[] data, byte[] kcv) {
		this.loadKeyType=loadKeyType;
		this.type = type;
		this.mainKeyIndex = mainKeyIndex;
		this.workingKeyIndex = workingKeyIndex;
		this.data = data;
		this.checkValue=kcv;
	}
	
	@ResponseEntity
	public static class CmdLoadWorkingKeyResponse extends AbstractSuccessResponse {

		@InstructionField(name = "应答码",index=0,fixLen = 2,maxLen=2,serializer=StringSerializer.class)
		private String answerCode;
		
		@InstructionField(name = "Checkvalue",index=1,fixLen = 8,maxLen=8,serializer=ByteArrSerializer.class)
		private byte[] checkvalue;		
		
		public String getAnswerCode(){
			return answerCode;
		}
		public byte[] getCheckvalue(){
			return checkvalue;
		}
	}

}
