package com.newland.sdk.me.cmd.serializer;

import com.newland.sdk.module.rfcard.RFCardType;
import com.newland.sdk.mtypex.serializer.AbstractEnumSerializer;

/**
 * 非接卡 类型序列化器
 *
 */
public class RFCardClassSerializer  extends AbstractEnumSerializer {
	public RFCardClassSerializer() {
		super(RFCardType.class,new byte[][]{{0x0A},{0x0B},{0x0C},{ 0x0D },{ 0x0E }});
	}
}	
