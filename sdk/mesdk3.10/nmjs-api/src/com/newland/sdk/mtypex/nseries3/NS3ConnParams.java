package com.newland.sdk.mtypex.nseries3;

import java.util.Set;

import com.newland.sdk.mtype.conn.DeviceConnParams;
import com.newland.sdk.mtype.conn.DeviceConnType;

public class NS3ConnParams  implements DeviceConnParams {

	@Override
	public DeviceConnType getConnectType() {
		return DeviceConnType.NSCONNECTOR_V300;
	}

	@Override
	public Set<String> getParamKeys() {
		return null;
	}

	@Override
	public String getParam(String key) {
		return null;
	}

}
