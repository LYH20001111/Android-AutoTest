package com.newland.sdk.me.cmd.common;

import java.util.Date;

import com.newland.sdk.me.cmd.serializer.DateSerializer;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;

@CommandEntity(cmdCode = { (byte)0x1D,(byte)0x05 }, responseClass = CmdDeviceGetTime.GetTimeResponse.class)
public class CmdDeviceGetTime extends CommonDeviceCommand{
	
	@ResponseEntity
	public static class GetTimeResponse extends AbstractSuccessResponse{

		@InstructionField(name="时间",index = 0,fixLen = 14, maxLen = 14, serializer = DateSerializer.class)
		private Date deviceDate;

		public Date getDeviceDate() {
			return deviceDate;
		}
	}

}
