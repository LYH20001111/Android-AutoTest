package com.newland.sdk.me.cmd.pininput;

import com.newland.sdk.me.cmd.serializer.IntegerSerializer;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;

@CommandEntity(cmdCode = { (byte) 0x1A, (byte) 0x27 }, responseClass = CmdKSNIncrease.CmdKSNIncreaseResponse.class)
/**
 * dukpt模式下ksn自增1
 * 
 * @author tiramisu
 *
 */
public class CmdKSNIncrease extends CommonDeviceCommand {

	@InstructionField(name = "dukpt密钥索引", index = 1, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int dukptIndex;

	public CmdKSNIncrease(int dukptIndex) {
		this.dukptIndex = dukptIndex;
	}

	@ResponseEntity
	public static class CmdKSNIncreaseResponse extends AbstractSuccessResponse {
	}
}
