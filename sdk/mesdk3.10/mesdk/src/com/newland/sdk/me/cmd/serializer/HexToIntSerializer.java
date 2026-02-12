package com.newland.sdk.me.cmd.serializer;

import com.newland.sdk.utils.ISOUtils;
import com.newland.sdk.mtypex.serializer.Serializer;

public class HexToIntSerializer  implements Serializer{
	@Override
	public byte[] pack(Object obj) throws Exception {
		if (!(obj instanceof String)) {
			throw new IllegalArgumentException("StringHexSerializer not support type:" + obj.getClass());
		}
		return ISOUtils.hex2byte(obj.toString());
	}

	@Override
	public Object unpack(byte[] input, int offset, int len) throws Exception {
		return Integer.valueOf(ISOUtils.hexString(input),16);
	}
}
