package com.newland.sdk.me.cmd.pininput;
  
import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.me.cmd.serializer.IntegerSerializer;
import com.newland.sdk.me.cmd.serializer.StringSerializer;
import com.newland.sdk.me.cmd.serializer.WorkingKeyTypeSerializer;
import com.newland.sdk.module.pin.AlgorithmMode;
import com.newland.sdk.module.pin.LoadWKMode;
import com.newland.sdk.module.pin.WorkingKeyType;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;
import com.newland.sdk.mtypex.serializer.AbstractEnumSerializer;

@CommandEntity(cmdCode = { (byte)0x1A,(byte)0x05 }, responseClass = CmdLoadWorkingKeyAndVerify.CmdLoadWorkingKeyAndVerifyResponse.class)
public class CmdLoadWorkingKeyAndVerify extends CommonDeviceCommand{
	@InstructionField(name="密钥加载模式",index=0,fixLen=1,maxLen=1,serializer=LoadWKModeSerializer.class)
	private LoadWKMode loadKeyMode;

	@InstructionField(name="算法模式",index=1,fixLen=1,maxLen=1,serializer=CmdLoadMainKeyAndVerify.AlgorithmModeSerializer.class)
	private AlgorithmMode algorithmMode;

	@InstructionField( name = "密钥类型",index=2,fixLen = 1,maxLen=1,serializer=WorkingKeyTypeSerializer.class)
	private WorkingKeyType type;
	
	@InstructionField(name = "主密钥索引",index=3,fixLen = 1,maxLen=1,serializer=IntegerSerializer.class)
	private  int mainKeyIndex;
	
	@InstructionField(name = "工作密钥索引",index=4,fixLen = 1,maxLen=1,serializer=IntegerSerializer.class)
	private int workingKeyIndex;
	
	@InstructionField(name = "密钥",index=5,maxLen=32,serializer=ByteArrSerializer.class)
	private byte[] data ;

	@InstructionField(name = "校验值",index=6,maxLen=8,serializer=ByteArrSerializer.class)
	private byte[] checkValue ;

	@InstructionField(name = "CBC初始值",index=7,maxLen=16,serializer=ByteArrSerializer.class)
	private byte[] cbcInitData ;

	public CmdLoadWorkingKeyAndVerify(LoadWKMode loadKeyMode,AlgorithmMode algorithmMode,WorkingKeyType type, int mainKeyIndex, int workingKeyIndex, byte[] data,byte[] checkValue,byte[] cbcInitData) {
		this.loadKeyMode = loadKeyMode;
		this.algorithmMode = algorithmMode;
		this.type = type;
		this.mainKeyIndex = mainKeyIndex;
		this.workingKeyIndex = workingKeyIndex;
		this.data = data;
		this.checkValue=checkValue;
		this.cbcInitData = cbcInitData;
	}
	
	@ResponseEntity
	public static class CmdLoadWorkingKeyAndVerifyResponse extends AbstractSuccessResponse{

		@InstructionField(name = "应答码",index=0,fixLen = 2,maxLen=2,serializer=StringSerializer.class)
		private String answerCode;
		
		@InstructionField(name = "Checkvalue",index=1,maxLen=16,serializer=ByteArrSerializer.class)
		private byte[] checkvalue;		
		
		public String getAnswerCode(){
			return answerCode;
		}
		public byte[] getCheckvalue(){
			return checkvalue;
		}
	}

	public static class LoadWKModeSerializer extends AbstractEnumSerializer {
		public LoadWKModeSerializer() {
			super(LoadWKMode.class, new byte[][]{{0x01},{0x02},{(byte) 0xFF},{(byte) 0xFF},{(byte) 0xFF},{(byte) 0xFF}});
		}
	}
}
