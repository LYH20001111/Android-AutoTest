package com.newland.sdk.me.cmd.rfcard;


import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;

@CommandEntity(cmdCode = { (byte) 0xE2, (byte) 0x15 }, responseClass = CmdRFCardATS.CmdRFCardATSResponse.class )
public class CmdRFCardATS extends CommonDeviceCommand {

	@ResponseEntity
	public static class CmdRFCardATSResponse extends AbstractSuccessResponse {
		private static final long serialVersionUID = 1L;
		@InstructionField(name = "ATS数据", index = 1, maxLen = 32, serializer = ByteArrSerializer.class)
		private byte[] ats;

		public byte[] getATS() {
			return ats;
		}
	}
}
