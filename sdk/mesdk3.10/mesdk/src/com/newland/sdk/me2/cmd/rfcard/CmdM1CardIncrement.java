package com.newland.sdk.me2.cmd.rfcard;

import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.me.cmd.serializer.IntegerSerializer;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;

@CommandEntity(cmdCode = { (byte) 0xE2, (byte) 0x0B}, responseClass = CmdM1CardIncrement.CmdM1CardIncrementResponse.class)
public class CmdM1CardIncrement extends CommonDeviceCommand {

	@InstructionField(name = "块号", index = 0, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int blockNo;
	@InstructionField(name = "Data", index = 1, fixLen = 4, maxLen = 4, serializer = ByteArrSerializer.class)
	private byte[] data;

	public CmdM1CardIncrement(int blockNo, byte[] data) {
		this.blockNo = blockNo;
		this.data = data;
	}

	@ResponseEntity
	public static class CmdM1CardIncrementResponse extends AbstractSuccessResponse {
	}

}