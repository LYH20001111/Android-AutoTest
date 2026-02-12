package com.newland.sdk.me.cmd.pininput;

import com.newland.sdk.module.pin.AlgorithmMode;
import com.newland.sdk.module.pin.LoadKeyMode;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.PaddingType;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;
import com.newland.sdk.mtypex.serializer.AbstractEnumSerializer;

@CommandEntity(cmdCode={(byte)0x1A,(byte)0x02},responseClass=CmdLoadMainKeyAndVerify.CmdLoadMainKeyAndVerifyResponse.class)
public class CmdLoadMainKeyAndVerify extends CommonDeviceCommand{
    @InstructionField(name="密钥加载模式",index=0,fixLen=1,maxLen=1,serializer=LoadKeyModeSerializer.class)
	private LoadKeyMode loadKeyMode;

	@InstructionField(name="算法模式",index=1,fixLen=1,maxLen=1,serializer=AlgorithmModeSerializer.class)
	private AlgorithmMode algorithmMode;

    @InstructionField(name="主密钥索引",index =2,fixLen=1,maxLen=1,serializer= com.newland.sdk.me.cmd.serializer.IntegerSerializer.class)
    private int mainKeyIndex;

    @InstructionField(name="密钥数据",index=3,maxLen=1024,serializer= com.newland.sdk.me.cmd.serializer.ByteArrSerializer.class)
    private byte[] data;

    @InstructionField(name="主密钥索引[作为传输密钥]",index=4,maxLen=1,fixLen=1,serializer= com.newland.sdk.me.cmd.serializer.IntegerSerializer.class)
    private int transportKeyIndex;

    @InstructionField(name="检验值",index=5,maxLen=8,padding='0',paddingType=PaddingType.RIGHT,serializer= com.newland.sdk.me.cmd.serializer.ByteArrSerializer.class)
    private byte[] checkValue;

	@InstructionField(name="初始向量",index=6,maxLen=16,serializer= com.newland.sdk.me.cmd.serializer.ByteArrSerializer.class)
	private byte[] cbcInit;

	public CmdLoadMainKeyAndVerify(LoadKeyMode loadKeyMode, AlgorithmMode algorithmMode , int mainKeyIndex, byte[] data, byte[] checkValue, int transportKeyIndex, byte[] cbcInit) {
		this.loadKeyMode = loadKeyMode;
		this.algorithmMode = algorithmMode;
		this.mainKeyIndex = mainKeyIndex;
		this.data = data;
		this.transportKeyIndex=transportKeyIndex;
		this.checkValue=checkValue;
		this.cbcInit = cbcInit;
	}
	@ResponseEntity
	public  static class CmdLoadMainKeyAndVerifyResponse extends AbstractSuccessResponse{
		
		@InstructionField(name = "应答码",index=0,fixLen = 2,maxLen=2,serializer= com.newland.sdk.me.cmd.serializer.StringSerializer.class)
		private String answerCode;
		
		@InstructionField(name = "Checkvalue",index=1,maxLen=16,serializer= com.newland.sdk.me.cmd.serializer.ByteArrSerializer.class)
		private byte[] checkValue;		
		
		public String getAnswerCode(){
			return answerCode;
		}
		public byte[] getCheckValue(){
			return checkValue;
		}
	}


	public static class LoadKeyModeSerializer extends AbstractEnumSerializer{
		public LoadKeyModeSerializer() {
			super(LoadKeyMode.class, new byte[][]{{0x01},{0x02},{0x03},{0x04}
					,{(byte) 0xFF},{(byte) 0xFF},{(byte) 0xFF}});
		}
	}

	public static class AlgorithmModeSerializer extends AbstractEnumSerializer{
		public AlgorithmModeSerializer() {
			super(AlgorithmMode.class, new byte[][]{{0x01},{0x02},{0x03},{0x04}});
		}
	}
}