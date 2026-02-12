package com.newland.sdk.me.cmd.pininput;
  

import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.me.cmd.serializer.ByteSerializer;
import com.newland.sdk.me.cmd.serializer.IntegerSerializer;
import com.newland.sdk.me.cmd.serializer.keyManagementSerializer;
import com.newland.sdk.me.cmd.serializer.StringSerializer;
import com.newland.sdk.module.pin.KeyManagement;
import com.newland.sdk.mtype.DeviceInvokeException;
import com.newland.sdk.module.pin.MacResult;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;

@CommandEntity(cmdCode = { (byte)0x1A,(byte)0x04 }, responseClass = CmdCalcMac.CmdCalcMacResponse.class)
public class CmdCalcMac extends CommonDeviceCommand{
	
	@InstructionField(name = "密钥体系", index = 0, fixLen = 1, maxLen = 1, serializer = keyManagementSerializer.class)
	private KeyManagement keyManagement;

	@InstructionField(name = "MAC算法",index=1,fixLen = 1,maxLen=1,serializer=IntegerSerializer.class)
	private static int macAlgorithm;

	@InstructionField( name = "密钥索引",index=2,fixLen = 1,maxLen=1,serializer=IntegerSerializer.class)
	private int keyIndex;
	
	@InstructionField(name = "块标志",index=3,fixLen = 1,maxLen=1,serializer=ByteSerializer.class)
	private byte blockFlag = 0x03 ; 

	@InstructionField(name = "MAC数据",index =4, maxLen = 4000, serializer = ByteArrSerializer.class)
	private byte[] macData;

	@InstructionField(name = "加密密钥密文",index =5, maxLen = 24, serializer = ByteArrSerializer.class)
	private byte[] keyData;

	@InstructionField( name = "随机数索引",index = 6,maxLen=2,serializer=ByteArrSerializer.class)
	private byte[] randomIndex;
	
	public enum CmdState {
		FIRST_BLOCK, NEXT_BLOCK, LAST_BLOCK,ONLY_BLOCK;
	}
	public CmdCalcMac(KeyManagement keyManagement, int macAlgorithm, int keyIndex, byte[] input, CmdState cmdState, byte[] keyData, byte[] radomIndex) {
		this.keyManagement = keyManagement;
		this.macAlgorithm = macAlgorithm;
		this.keyIndex = keyIndex;
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
		this.macData = input;
		this.keyData = keyData;
		this.randomIndex = radomIndex;
	}
	
	@ResponseEntity
	public static class CmdCalcMacResponse extends AbstractSuccessResponse{
		@InstructionField( name = "密钥索引",index=0,fixLen = 1,maxLen=1,serializer=IntegerSerializer.class)
		private int keyIndex;
		
		@InstructionField(name = "应答码",index=1,fixLen = 2,maxLen=2,serializer=StringSerializer.class)
		private String answerCode;

		@InstructionField(name = "MAC",index=2,maxLen=16,serializer=ByteArrSerializer.class)
		private byte[] mac;

		@InstructionField(name = "KSN",index=3,fixLen = 10,maxLen=10,serializer=ByteArrSerializer.class)
		private byte[] ksn ; 

		public int getkeyIndex(){
			return keyIndex;
		}
		public String getAnswerCode(){
			return answerCode;
		}

        public byte[] getMAC(){
            return mac;
        }
		public MacResult getMacResult(){
		    return new MacResult(mac,ksn);
		}

        public byte[] getKSN(){
            return ksn;
        }
	}
}
