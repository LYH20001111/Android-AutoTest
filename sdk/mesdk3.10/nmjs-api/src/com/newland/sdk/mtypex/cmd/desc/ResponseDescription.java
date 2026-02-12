package com.newland.sdk.mtypex.cmd.desc;

import java.util.List;

import com.newland.sdk.mtypex.cmd.DeviceResponse;

public class ResponseDescription extends EntityDescription{
	
	private Class<? extends DeviceResponse> responseClass;

	public ResponseDescription(Class<? extends DeviceResponse> responseClass,List<FieldDescription> fields) {
		super(fields);
		this.responseClass = responseClass;
	}

	public Class<? extends DeviceResponse> getResponseClass() {
		return responseClass;
	}

	
}
