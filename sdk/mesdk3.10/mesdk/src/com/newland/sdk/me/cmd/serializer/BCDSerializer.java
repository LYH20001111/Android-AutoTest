package com.newland.sdk.me.cmd.serializer;

import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.mtypex.serializer.Serializer;

public class BCDSerializer implements Serializer {
	@Override
	public byte[] pack(Object obj) throws Exception {
		if (!(obj instanceof String)) {
			throw new IllegalArgumentException("BCDSerializer not support type:" + obj.getClass());
		}
		return InnerUtils.str2bcd(obj.toString(), true);
	}

	@Override
	public Object unpack(byte[] input, int offset, int len) throws Exception {
		byte[] datebs = new byte[len];
		System.arraycopy(input, offset, datebs, 0, len);
		return InnerUtils.bcd2str(datebs, 0, len * 2, true);
	}
}
