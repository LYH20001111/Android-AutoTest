package com.newland.sdk.me.cmd;

import com.newland.sdk.me.cmd.CmdCancelAndReset.CancelAndResetResponse;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;

@CommandEntity(cmdCode={(byte)0X1D,(byte)0x08},responseClass=CancelAndResetResponse.class)
public class CmdCancelAndReset extends CommonDeviceCommand{
	
	@ResponseEntity
	public static final class CancelAndResetResponse extends AbstractSuccessResponse{

		/**
		 * 
		 */
		private static final long serialVersionUID = -4351088268290398302L;
		
	}

}
