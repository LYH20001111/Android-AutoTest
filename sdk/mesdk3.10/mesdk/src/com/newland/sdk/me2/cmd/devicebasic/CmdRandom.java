package com.newland.sdk.me2.cmd.devicebasic;

import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.me.cmd.serializer.Integer2Serializer;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;

@CommandEntity(cmdCode = { (byte)0xF1,(byte)0x04}, responseClass = CmdRandom.CmdRandomResponse.class)
public class CmdRandom extends CommonDeviceCommand {

	@InstructionField(name = "随机数长度", index = 1, fixLen = 2, maxLen = 2, serializer = Integer2Serializer.class)
	private int len;

	public CmdRandom(int len) {
		this.len = len;
	}

	@ResponseEntity
	public static class CmdRandomResponse extends AbstractSuccessResponse{
		@InstructionField(name="设备随机数",index = 0, maxLen = 512, serializer = ByteArrSerializer.class)
		private byte[] random;

		public byte[] getRandom() {
			return random;
		}
		
	}

}
