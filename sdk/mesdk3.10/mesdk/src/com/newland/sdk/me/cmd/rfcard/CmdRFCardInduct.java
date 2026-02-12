package com.newland.sdk.me.cmd.rfcard;

import com.newland.sdk.me.cmd.rfcard.CmdRFCardInduct.CmdRFCardInductResponse;
import com.newland.sdk.me.cmd.serializer.Integer2Serializer;
import com.newland.sdk.me.cmd.serializer.IntegerSerializer;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;

@CommandEntity(cmdCode = { (byte) 0xE2, (byte) 0x03 }, responseClass = CmdRFCardInductResponse.class)
public class CmdRFCardInduct extends CommonDeviceCommand {
	@InstructionField(name = "非接寻卡次数", index = 0, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int rfSearchTimes=3;
	@InstructionField(name = "寻卡间隔时间", index = 1, fixLen = 2, maxLen = 2, serializer = Integer2Serializer.class)
	private int intervalTime=10;
	@ResponseEntity
	public static class CmdRFCardInductResponse extends AbstractSuccessResponse {
	}
}
