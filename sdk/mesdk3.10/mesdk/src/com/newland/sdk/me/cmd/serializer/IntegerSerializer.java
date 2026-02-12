package com.newland.sdk.me.cmd.serializer;

import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.mtypex.serializer.Serializer;

public class IntegerSerializer  implements Serializer {
	private int len=1; 
	
	public int getLen() {
		return len;
	}
	public void setLen(int len) {
		this.len = len;
	}

	@Override
	public byte[] pack(Object obj) throws Exception {
		if (!(obj instanceof Integer)) {
			throw new IllegalArgumentException("IntegerSerializer not support type:" + obj.getClass());
		}
		Integer integer = (Integer) obj;
		
		return InnerUtils.intToBytes(integer,len,true);
	}
	 

    

	@Override
	public Object unpack(byte[] input, int offset, int len)
			throws Exception {  
		if(len != this.len)
			throw new IllegalArgumentException("len should be "+this.len); 
		byte[] databs = new byte[this.len];
		System.arraycopy(input, offset, databs, 0, this.len);
		
		return InnerUtils.bytesToInt(databs, 0, len, true);
	} 
}
