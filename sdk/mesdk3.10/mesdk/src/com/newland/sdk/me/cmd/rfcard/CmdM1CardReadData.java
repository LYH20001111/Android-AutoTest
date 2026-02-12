package com.newland.sdk.me.cmd.rfcard;

import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.me.cmd.serializer.IntegerSerializer;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;

@CommandEntity(cmdCode = { (byte) 0xE2, (byte) 0x07 }, responseClass = CmdM1CardReadData.CmdM1CardReadDataResponse.class)
public class CmdM1CardReadData extends CommonDeviceCommand {

	@InstructionField(name = "块号", index = 0, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int blockNo;

	public CmdM1CardReadData(int blockNo) {
		this.blockNo = blockNo;
	}
	@ResponseEntity
	public static class CmdM1CardReadDataResponse extends AbstractSuccessResponse {
		@InstructionField(name = "Data", index = 0, maxLen = 128, serializer = ByteArrSerializer.class)
		private byte[] data;

		public byte[] getData() {
			return data;
		}

	}

}