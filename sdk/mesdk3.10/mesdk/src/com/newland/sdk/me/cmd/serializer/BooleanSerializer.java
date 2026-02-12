package com.newland.sdk.me.cmd.serializer;
 
import com.newland.sdk.mtypex.serializer.Serializer;

public class BooleanSerializer  implements Serializer {
	@Override
	public byte[] pack(Object obj) throws Exception {
		if(!(obj instanceof Boolean)){
			throw new IllegalArgumentException("BooleanSerializer not support type:"+obj.getClass());
		}
		Boolean b=(Boolean)obj;
		byte[] result=new byte[1];
		if(b.booleanValue()){
			result[0]=0x01;
		}
		return result;
	}
	
	@Override
	public Object unpack(byte[] input, int offset, int len)
			throws Exception {  
		return (input[offset]==0x01); 
	} 
}
