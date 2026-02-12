package com.newland.sdk.me.cmd.guestDisplay;
import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.me.cmd.serializer.ByteSerializer;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;

import java.util.Arrays;

@CommandEntity(cmdCode = { (byte)0x20,(byte)0x02 }, responseClass = com.newland.sdk.me.cmd.guestDisplay.CmdSetGuestDisplayBrightness.CmdGuestDisplayBrightnessResponse.class)
public class CmdSetGuestDisplayBrightness extends CommonDeviceCommand{

	@InstructionField( name = "设备类型",index=0,fixLen = 1,maxLen=1,serializer=ByteSerializer.class)
	private byte devType=1;

	@InstructionField( name = "亮度值",index=1,fixLen = 1,maxLen=1,serializer=ByteSerializer.class)
	private byte brightValue;

	public CmdSetGuestDisplayBrightness(byte brightValue){
		this.brightValue = brightValue;
	}
	@ResponseEntity
	public static class CmdGuestDisplayBrightnessResponse extends AbstractSuccessResponse{
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
