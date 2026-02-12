package com.newland.sdk.me.cmd.rfcard;

import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;

@CommandEntity(cmdCode = { (byte) 0xE2, (byte) 0x04 }, responseClass = CmdRFCardTransmit.CmdRFCardTransmitResponse.class)
public class CmdRFCardTransmit extends CommonDeviceCommand {

	@InstructionField(name = "数据", index = 0, maxLen = 4000, serializer = com.newland.sdk.me.cmd.serializer.ByteArrSerializer.class)
	private byte[] req;

	public CmdRFCardTransmit(byte[] req) {

		this.req = req;
	}

	@ResponseEntity
	public static class CmdRFCardTransmitResponse extends AbstractSuccessResponse {
		@InstructionField(name = "应答数据", index = 0, maxLen = 4000, serializer = com.newland.sdk.me.cmd.serializer.ByteArrSerializer.class)
		private byte[] data;

		public byte[] getData() {
			return data;
		}

	}
}