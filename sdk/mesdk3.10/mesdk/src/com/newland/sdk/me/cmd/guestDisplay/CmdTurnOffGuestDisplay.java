package com.newland.sdk.me.cmd.guestDisplay;

import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.me.cmd.serializer.ByteSerializer;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;

import java.util.Arrays;

@CommandEntity(cmdCode = { (byte)0x20,(byte)0x04 }, responseClass = com.newland.sdk.me.cmd.guestDisplay.CmdTurnOffGuestDisplay.CmdTurnOffGuestDisplayResponse.class)
public class CmdTurnOffGuestDisplay extends CommonDeviceCommand{

	@InstructionField( name = "设备类型",index=0,fixLen = 1,maxLen=1,serializer=ByteSerializer.class)
	private byte devType=1;

	@ResponseEntity
	public static class CmdTurnOffGuestDisplayResponse extends AbstractSuccessResponse{
		@InstructionField( name = "应答码",index = 0,fixLen = 2,maxLen = 2, serializer = ByteArrSerializer.class)
		private byte[] resultCode;

		public boolean getResultCode(){
			try {
				if(resultCode!=null && Arrays.equals(resultCode,new byte[]{0x30,0x31})){
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	}
}
