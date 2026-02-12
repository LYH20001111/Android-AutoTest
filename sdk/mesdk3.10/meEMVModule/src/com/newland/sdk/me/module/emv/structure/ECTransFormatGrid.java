package com.newland.sdk.me.module.emv.structure;

import com.newland.sdk.mtype.DeviceRTException;
import com.newland.sdk.mtype.common.ErrorCode;

public class ECTransFormatGrid {
	
	private String methodName;
	
	private int expectedLen;

	private int minLen;

	private int maxLen;
	
	private int len;
	
	public ECTransFormatGrid(int expectedLen, int minLen, int maxLen, String methodName){
		this.methodName = methodName;
		this.expectedLen = expectedLen;
		this.minLen = minLen;
		this.maxLen = maxLen;
		
	}

	public String getMethodName() {
		return methodName;
	}

	public int getExpectedLen() {
		return expectedLen;
	}

	public int getMinLen() {
		return minLen;
	}

	public int getMaxLen() {
		return maxLen;
	}

	public int getLen() {
		return len;
	}

	public void setLen(int len) {
		if(expectedLen > 0 && expectedLen == len){
			this.len = len;
		}else if(minLen <= len && len <= maxLen){
			this.len = len;
		}else{
			throw new DeviceRTException(ErrorCode.UNKNOWN, methodName+" len not match:"+len);
		}
	}


}
