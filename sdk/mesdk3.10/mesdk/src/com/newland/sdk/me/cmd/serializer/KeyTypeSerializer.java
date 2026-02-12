package com.newland.sdk.me.cmd.serializer;

import com.newland.sdk.module.pin.KeyType;
import com.newland.sdk.mtypex.serializer.AbstractEnumSerializer;

public class KeyTypeSerializer extends AbstractEnumSerializer {

	public KeyTypeSerializer() {
		super(KeyType.class, new byte[][]{{0x00},{0x01},{0x02},{0x03},{0x04}});
	}
	
}
