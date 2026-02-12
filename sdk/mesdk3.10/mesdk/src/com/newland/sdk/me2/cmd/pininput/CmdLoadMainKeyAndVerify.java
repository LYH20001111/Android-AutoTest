package com.newland.sdk.me2.cmd.pininput;


import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.me.cmd.serializer.IntegerSerializer;
import com.newland.sdk.me.cmd.serializer.StringSerializer;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.PaddingType;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;
import com.newland.sdk.me2.cmd.pininput.CmdLoadMainKeyAndVerify.CmdLoadMainKeyAndVerifyResponse;
import com.newland.sdk.mtypex.serializer.AbstractEnumSerializer;

@CommandEntity(cmdCode={(byte)0x1A,(byte)0x02},responseClass=CmdLoadMainKeyAndVerifyResponse.class)
public class CmdLoadMainKeyAndVerify extends CommonDeviceCommand {
	
    @InstructionField(name="kekType",index=0,fixLen=1,maxLen=1,serializer=IntegerSerializer.class)
	private int kekType;
    @InstructionField(name="主密钥索引",index =1,fixLen=1,maxLen=1,serializer=IntegerSerializer.class)
    private int mainKeyIndex;
    @InstructionField(name="密钥数据",index=2,maxLen=1024,serializer=ByteArrSerializer.class)
    private byte[] data;
    @InstructionField(name="主密钥索引[作为传输密钥]",index=3,maxLen=1,fixLen=1,serializer=IntegerSerializer.class)
    private int transportKeyIndex;
    @InstructionField(name="检验值",index=4,maxLen=8,padding='0',paddingType=PaddingType.RIGHT,serializer=ByteArrSerializer.class)
    private byte[] checkValue;
	public CmdLoadMainKeyAndVerify(int kekType, int mainKeyIndex, byte[] data,byte[] checkValue,int transportKeyIndex) {

		this.kekType = kekType;
		this.mainKeyIndex = mainKeyIndex;
		this.data = data;
		this.transportKeyIndex=transportKeyIndex;
		this.checkValue=checkValue;
	}
	@ResponseEntity
	public  static class CmdLoadMainKeyAndVerifyResponse extends AbstractSuccessResponse {
		
		@InstructionField(name = "应答码",index=0,fixLen = 2,maxLen=2,serializer=StringSerializer.class)
		private String answerCode;
		
		@InstructionField(name = "Checkvalue",index=1,fixLen = 6,maxLen=6,serializer=ByteArrSerializer.class)
		private byte[] checkValue;		
		
		public String getAnswerCode(){
			return answerCode;
		}
		public byte[] getCheckValue(){
			return checkValue;
		}
	}

}