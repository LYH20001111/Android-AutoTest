package com.newland.sdk.mtypex.nseries;

import com.newland.sdk.mtype.conn.DeviceConnParams;
import com.newland.sdk.mtype.conn.DeviceConnType;

import java.util.Set;

public class NSConnV100ConnParams implements DeviceConnParams {

	@Override
	public DeviceConnType getConnectType() {
		return DeviceConnType.NSCONNECTOR_V100;
	}

	@Override
	public Set<String> getParamKeys() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParam(String key) {
		// TODO Auto-generated method stub
		return null;
	}

}
