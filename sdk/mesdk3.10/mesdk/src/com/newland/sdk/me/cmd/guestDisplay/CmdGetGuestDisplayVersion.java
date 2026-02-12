package com.newland.sdk.me.cmd.guestDisplay;

import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.me.cmd.serializer.ByteSerializer;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;

@CommandEntity(cmdCode = { (byte)0x20,(byte)0x01 }, responseClass = com.newland.sdk.me.cmd.guestDisplay.CmdGetGuestDisplayVersion.CmdGetVersionResponse.class)
public class CmdGetGuestDisplayVersion extends CommonDeviceCommand {

	@InstructionField( name = "设备类型",index=0,fixLen = 1,maxLen=1,serializer=ByteSerializer.class)
	private byte devType=1;

	@ResponseEntity
	public static class CmdGetVersionResponse extends AbstractSuccessResponse {

		@InstructionField( name = "客显版本",index = 0,maxLen = 128, serializer = ByteArrSerializer.class)
		private byte[] version=null;

		public String getVersion(){
			try {
				if(version!=null){
					return new String(version,"GBK");
                }
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
