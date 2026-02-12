package com.newland.sdk.me2.cmd.rfcard;

import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.me.cmd.serializer.IntegerSerializer;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;

@CommandEntity(cmdCode = { (byte) 0xE2, (byte) 0x0A }, responseClass = CmdM1CardWriteData.CmdM1CardWriteDataResponse.class)
public class CmdM1CardWriteData extends CommonDeviceCommand {

	@InstructionField(name = "块号", index = 0, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int blockNo;
	@InstructionField(name = "Data", index = 1, fixLen = 16, maxLen = 16, serializer = ByteArrSerializer.class)
	private byte[] data;

	public CmdM1CardWriteData(int blockNo, byte[] data) {
		this.blockNo = blockNo;
		this.data = data;
	}
	@ResponseEntity
	public static class CmdM1CardWriteDataResponse extends AbstractSuccessResponse {
	}

}