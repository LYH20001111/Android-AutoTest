package com.newland.sdkdemo.utils;

public enum PinKeyType {
	CONFIRM("ConfirmKey"),
	CANCEL("CancelKey"),
	DELETE("DeleteKey"),
	NUMBER("NumKey");
	
	public String code;
	PinKeyType(String code){
		this.code = code;
	}
	public static PinKeyType getFromCode(String code){
		for(PinKeyType rcs:values()){
			if(rcs.code==code)
				return rcs;
		}
		return null;
	}
}
