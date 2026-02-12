package com.newland.sdk.me.cmd.serializer;

import com.newland.sdk.module.rfcard.RFKeyMode;
import com.newland.sdk.mtypex.serializer.AbstractEnumSerializer;

/**
 * 非接卡 类型序列化器
 *
 */
public class RFKeyModeSerializer  extends AbstractEnumSerializer {
	public RFKeyModeSerializer() {
		super(RFKeyMode.class,new byte[][]{{0x60},{0x00},{0x61},{0x01}}); 
	}
}	
