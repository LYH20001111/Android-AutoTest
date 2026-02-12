package com.newland.sdk.me.cmd.serializer;
 
import com.newland.sdk.mtypex.serializer.Serializer;

public class ByteArrSerializer  implements Serializer {
	@Override
	public byte[] pack(Object obj) throws Exception {
		if(!(obj instanceof byte[])){
			throw new IllegalArgumentException("ByteArrSerializer not support type:"+obj.getClass());
		}
		byte[] b=(byte[])obj; 
		return b;
	} 

	@Override
	public Object unpack(byte[] input, int offset, int len)
			throws Exception { 
		byte[] data=new byte[len];
		System.arraycopy(input, offset, data, 0, len);
		return data; 
	} 
}
