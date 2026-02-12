package com.newland.sdk.me.cmd.serializer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.newland.sdk.mtypex.serializer.Serializer;

public class DateSerializer implements Serializer{

	@Override
	public byte[] pack(Object obj) throws Exception {
		if(!(obj instanceof Date))
			throw new IllegalArgumentException("DateSerializer not support type:"+obj.getClass());
		Date date =(Date)obj;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
		return sdf.format(date).getBytes();
	}

	@Override
	public Object unpack(byte[] input, int offset, int len)
			throws Exception {
		if(len != 14)
			throw new IllegalArgumentException("len should be 14");
		byte[] datebs = new byte[len];
		System.arraycopy(input, offset, datebs, 0, len);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
		return sdf.parse(new String(datebs));
	}

}
