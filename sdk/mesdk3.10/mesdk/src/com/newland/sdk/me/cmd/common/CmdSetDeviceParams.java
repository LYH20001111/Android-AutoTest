package com.newland.sdk.me.cmd.common;

import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;

@CommandEntity(cmdCode = { (byte)0x1D,(byte)0x06 }, responseClass = CmdSetDeviceParams.CmdSetDeviceParamsResponse.class)
public class CmdSetDeviceParams extends CommonDeviceCommand{
	
	@InstructionField(name="参数数据",index = 0, maxLen = 4000, serializer = ByteArrSerializer.class)
	private byte[] Params;
	
	public CmdSetDeviceParams(byte[] Params){
		this.Params=Params;
	}
	
	@ResponseEntity
	public static class CmdSetDeviceParamsResponse extends AbstractSuccessResponse{
 
	}

}