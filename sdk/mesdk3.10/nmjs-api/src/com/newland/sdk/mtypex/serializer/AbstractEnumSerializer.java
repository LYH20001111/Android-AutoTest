package com.newland.sdk.mtypex.serializer;

import java.util.Arrays;

import com.newland.sdk.mtype.util.Dump;

public class AbstractEnumSerializer implements Serializer{
	
	private Class<? extends Enum> expectedClazz;
	
	private Enum[] expectedEnums;
	
	private byte[][] expectedBytes;
	
	public AbstractEnumSerializer(Class<? extends Enum> expectedClazz, byte[][] expectedBytes){
		this.expectedClazz = expectedClazz; 
		expectedEnums = expectedClazz.getEnumConstants();
		this.expectedBytes = expectedBytes;
		if(expectedEnums.length != expectedBytes.length){
			throw new IllegalArgumentException("enum numbers not match!");
		}
	}
	
	

	@Override
	public byte[] pack(Object obj) throws Exception {
		if(!expectedClazz.equals(obj.getClass())){
			throw new IllegalArgumentException("not support type:"+obj.getClass().getName());
		}
		Enum toCompared = (Enum)obj;
		int i = 0;
		for(Enum<?> e:expectedEnums){
			if(e.equals(toCompared)){
				return expectedBytes[i]; 
			}
			i++;
		}
		throw new IllegalArgumentException("not expected enum:"+toCompared.getClass().getName());
	}

	@Override
	public Object unpack(byte[] input, int offset, int len)
			throws Exception {
		byte[] rslt = new byte[len];
		System.arraycopy(input, offset, rslt, 0, len);
		int i = 0;
		for(byte[] expected:expectedBytes){
			if(Arrays.equals(rslt, expected)){
				return expectedEnums[i];
			}
			i++;
		}
		throw new IllegalArgumentException("not expected bytes:"+Dump.getHexDump(rslt));
	}

}
