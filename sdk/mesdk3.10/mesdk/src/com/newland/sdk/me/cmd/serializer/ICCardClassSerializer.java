package com.newland.sdk.me.cmd.serializer;

import com.newland.sdk.module.iccard.ICCardType;
import com.newland.sdk.mtypex.serializer.AbstractEnumSerializer;

/**
 * ICCard 类型序列化器
 *
 */
public class ICCardClassSerializer  extends AbstractEnumSerializer {
	public ICCardClassSerializer() {
		super(ICCardType.class,new byte[][]{{0x00},{0x06},{0x07},{0x08},{0x09},{0x0a},{0x0b},{0x0c},{0x0d},{0x0e},{0x0f},{0x10},{0x11},{0x12},{0x13},{0x14},{0x15},{(byte)0xFF},{(byte)0xFF},{(byte)0xFF},{(byte)0xFF}});
	}
}	
