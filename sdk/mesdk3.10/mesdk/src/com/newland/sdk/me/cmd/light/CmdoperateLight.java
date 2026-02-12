package com.newland.sdk.me.cmd.light;

import com.newland.sdk.me.cmd.serializer.ByteSerializer;
import com.newland.sdk.module.light.LightColor;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;


@CommandEntity(cmdCode = { (byte)0x1D,(byte)0x12 }, responseClass = CmdoperateLight.CmdoperateLightResponse.class)
public class CmdoperateLight extends CommonDeviceCommand{
	public static final byte LIGHT_TURN_ON=0x01;
	public static final byte LIGHT_TURN_OFF=0x00;
	public static final byte LIGHT_BLINK=0x02;
	
	private static final int LIGHT_BLUE = 0x01;

	private static final int LIGHT_GREEN = 0x02;

	private static final int LIGHT_YELLOW = 0x04;

	private static final int LIGHT_RED = 0x08;

	@InstructionField(name="指示灯状态",index = 0,fixLen = 1, maxLen = 1, serializer = ByteSerializer.class)
	private byte status;
	@InstructionField(name="指示灯颜色",index = 1,fixLen = 1, maxLen = 1, serializer = ByteSerializer.class)
	private byte lightColor;
	
	public CmdoperateLight(byte status, LightColor[] lightColors) {
		this.status = status;
		if (null != lightColors) {
			for (LightColor lightColor : lightColors) {
				if (lightColor == LightColor.BLUE) {
					this.lightColor |= LIGHT_BLUE;
				} else if (lightColor == LightColor.GREEN) {
					this.lightColor |= LIGHT_GREEN;
				} else if (lightColor == LightColor.YELLOW) {
					this.lightColor |= LIGHT_YELLOW;
				} else if (lightColor == LightColor.RED) {
					this.lightColor |= LIGHT_RED;
				}
			}
		}
	} 

	@ResponseEntity
	public static class CmdoperateLightResponse extends AbstractSuccessResponse{
 
	}

}
