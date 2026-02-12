package com.newland.sdk.me.cmd.light;

import com.newland.sdk.me.cmd.serializer.ByteSerializer;
import com.newland.sdk.me.cmd.serializer.IntegerSerializer;
import com.newland.sdk.module.light.LightColor;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;
import com.newland.sdk.mtypex.serializer.AbstractEnumSerializer;
import com.newland.sdk.me.cmd.serializer.Integer2Serializer;

@CommandEntity(cmdCode = { (byte) 0x1D, (byte) 0x02 }, responseClass = CmdoperateLightBlink.CmdoperateLightBlinkResponse.class)
public class CmdoperateLightBlink extends CommonDeviceCommand {
	private static final int LIGHT_BLUE = 0x01;

	private static final int LIGHT_GREEN = 0x02;

	private static final int LIGHT_YELLOW = 0x04;

	private static final int LIGHT_RED = 0x08;

	@InstructionField(name = "闪烁次数", index = 0, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int times;
	@InstructionField(name = "指示灯颜色", index = 1, fixLen = 1, maxLen = 1, serializer = ByteSerializer.class)
	private byte lightColor;
	@InstructionField(name = "闪烁时间间隔", index = 2, fixLen = 2, maxLen = 2, serializer = Integer2Serializer.class)
	private int interval;


	public CmdoperateLightBlink(LightColor[] lighttypes, int times, int interval) {
		this.times = times;
		if (null != lighttypes) {
			for (LightColor lightColor : lighttypes) {
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
		this.interval = interval;
	}

	public static class LightTypeSerializer extends AbstractEnumSerializer {
		public LightTypeSerializer() {
			super(LightColor.class, new byte[][] { { 0x01 }, { 0x02 }, { 0x04 }, { 0x08 } });
		}
	}

	@ResponseEntity
	public static class CmdoperateLightBlinkResponse extends AbstractSuccessResponse {

	}

}
