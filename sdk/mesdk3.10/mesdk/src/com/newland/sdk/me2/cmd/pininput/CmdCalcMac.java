package com.newland.sdk.me2.cmd.pininput;


import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.me.cmd.serializer.ByteSerializer;
import com.newland.sdk.me.cmd.serializer.IntegerSerializer;
import com.newland.sdk.me.cmd.serializer.StringSerializer;
import com.newland.sdk.mtype.DeviceInvokeException;
import com.newland.sdk.module.pin.MacResult;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;
import com.newland.sdk.me2.cmd.pininput.CmdCalcMac.CmdCalcMacResponse;

@CommandEntity(cmdCode = { (byte)0x1A,(byte)0x04 }, responseClass = CmdCalcMacResponse.class)
public class CmdCalcMac extends CommonDeviceCommand {
	
	@InstructionField( name = "密钥索引",index=0,fixLen = 1,maxLen=1,serializer=IntegerSerializer.class)
	private int keyIndex;
	
	@InstructionField(name = "密钥类型",index=1,fixLen = 1,maxLen=1,serializer=IntegerSerializer.class)
	private int pinManageType;
	
	@InstructionField(name = "MAC算法",index=2,fixLen = 1,maxLen=1,serializer=IntegerSerializer.class)
	private static int macAlgorithm;
	
	@InstructionField(name = "块标志",index=3,fixLen = 1,maxLen=1,serializer=ByteSerializer.class)
	private byte blockFlag = 0x03 ; 

	@InstructionField(name = "MAC数据",index =4, maxLen = 1024, serializer = ByteArrSerializer.class)
	private byte[] macData;
	@InstructionField(name = "加密密钥密文",index =5, maxLen = 24, serializer = ByteArrSerializer.class)
	private byte[] key = {0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08};
	@InstructionField( name = "随机数索引",index = 6,maxLen=2,serializer=ByteArrSerializer.class)
	private byte[] randomIndex;
	
	public CmdCalcMac(int macAlgorithm,int pinManageType,int keyIndex, byte[] wkData, byte[] input,byte[] randomIndex) {
		if(wkData != null){
			this.keyIndex = keyIndex;
			this.key = wkData;
		}else{
			this.keyIndex = keyIndex;
			this.key = new byte[0];
		}
		this.macAlgorithm = macAlgorithm;
		this.macData = input;
		this.pinManageType=pinManageType;
		this.randomIndex = randomIndex;
	}
	public enum CmdState {
		FIRST_BLOCK, NEXT_BLOCK, LAST_BLOCK,ONLY_BLOCK;
	}
	public CmdCalcMac(CmdState cmdState,int macAlgorithm,int pinManageType,int keyIndex, byte[] wkData, byte[] input,byte[] randomIndex) {
		switch (cmdState) {
		case FIRST_BLOCK:
			this.blockFlag = 0x00;
			break;
		case NEXT_BLOCK:
			this.blockFlag = 0x01;
			break;
		case LAST_BLOCK:
			this.blockFlag = 0x02;
			break;
		case ONLY_BLOCK:
			this.blockFlag = 0x03;
			break;
		default:
			throw new DeviceInvokeException("illegal argument!"+cmdState);
	}
		if(wkData != null){
			this.keyIndex = keyIndex;
			this.key = wkData;
		}else{
			this.keyIndex = keyIndex;
			this.key = new byte[0];
		}
		this.macAlgorithm = macAlgorithm;
		this.macData = input;
		this.pinManageType=pinManageType;
		this.randomIndex=randomIndex;
	}
	@ResponseEntity
	public static class CmdCalcMacResponse extends AbstractSuccessResponse {
		@InstructionField( name = "密钥索引",index=0,fixLen = 1,maxLen=1,serializer=IntegerSerializer.class)
		private int keyIndex;
		
		@InstructionField(name = "应答码",index=1,fixLen = 2,maxLen=2,serializer=StringSerializer.class)
		private String answerCode;
		
		@InstructionField(name = "MAC",index=2,fixLen = 8,maxLen=8,serializer=ByteArrSerializer.class)
		private byte[] mac;
		
		@InstructionField(name = "KSN",index=3,fixLen = 10,maxLen=10,serializer=ByteArrSerializer.class)
		private byte[] ksn ; 
		
		@InstructionField(name = "国密mac结果",index=4,fixLen = 16,maxLen=16,serializer=ByteArrSerializer.class)
		private byte[] macSm ;
		
		public byte[] getMAC(){
			return mac;
		}
		public byte[] getKSN(){
			return ksn;
		}
		public int getkeyIndex(){
			return keyIndex;
		}
		public String getAnswerCode(){
			return answerCode;
		}
		
		public byte[] getMacSm() {
			return macSm;
		}
		public MacResult getMacResult(){
			if(macAlgorithm==0x05){
				return new MacResult(macSm,ksn);
			}else{
				return new MacResult(mac,ksn);
			}
		}
		
	}
}
