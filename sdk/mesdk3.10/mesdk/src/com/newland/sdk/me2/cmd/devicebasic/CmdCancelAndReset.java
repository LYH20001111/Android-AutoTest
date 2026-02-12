package com.newland.sdk.me2.cmd.devicebasic;


import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;

@CommandEntity(cmdCode={(byte)0X1D,(byte)0x08},responseClass=CmdCancelAndReset.CancelAndResetResponse.class)
public class CmdCancelAndReset extends CommonDeviceCommand {
	
	@ResponseEntity
	public static final class CancelAndResetResponse extends AbstractSuccessResponse {

		/**
		 * 
		 */
		private static final long serialVersionUID = -4351088268290398302L;
		
	}

}
