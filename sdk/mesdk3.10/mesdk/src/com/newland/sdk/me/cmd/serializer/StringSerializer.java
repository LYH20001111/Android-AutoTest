package com.newland.sdk.me.cmd.serializer;
 
import com.newland.sdk.mtypex.serializer.Serializer;

public class StringSerializer  implements Serializer {
	@Override
	public byte[] pack(Object obj) throws Exception {
		if(!(obj instanceof String)){
			throw new IllegalArgumentException("StringSerializer not support type:"+obj.getClass());
		}
		return obj.toString().getBytes("GBK");
	} 

	@Override
	public Object unpack(byte[] input, int offset, int len)
			throws Exception { 
		byte[] datebs = new byte[len];
		System.arraycopy(input, offset, datebs, 0, len); 

		return new String(datebs); 
	} 
}
