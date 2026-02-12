package com.newland.sdk.me.cmd.serializer;
 
import com.newland.sdk.mtypex.serializer.Serializer;

public class ByteSerializer  implements Serializer {
	@Override
	public byte[] pack(Object obj) throws Exception {
		if(!(obj instanceof Byte)){
			throw new IllegalArgumentException("ByteSerializer not support type:"+obj.getClass());
		}
		byte b=(Byte)obj;
		byte[] result=new byte[]{b}; 
		return result;
	} 

	@Override
	public Object unpack(byte[] input, int offset, int len)
			throws Exception {
		if(len != 1)
			throw new IllegalArgumentException("len should be 1");
		return input[offset]; 
	} 
}
