package com.newland.sdk.me.cmd.serializer;

import com.newland.sdk.module.pin.WorkingKeyType;
import com.newland.sdk.mtypex.serializer.AbstractEnumSerializer;

public class WorkingKeyTypeSerializer  extends AbstractEnumSerializer{

	public WorkingKeyTypeSerializer() {
		super(WorkingKeyType.class, new byte[][]{{0x01},{0x02},{0x03},{(byte) 0xFF}});
	}
	 
}
