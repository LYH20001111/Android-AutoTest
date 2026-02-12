package com.newland.sdk.mtypex.nseries3;

import android.content.Context;
import com.newland.sdk.mtype.conn.DeviceConnParams;
import com.newland.sdk.mtype.conn.DeviceConnType;
import com.newland.sdk.mtypex.conn.DeviceConnection;
import com.newland.sdk.mtypex.conn.DeviceConnector;

public class NS3Connector implements DeviceConnector {

	@Override
	public DeviceConnType[] getSupportConnType() {
		return new DeviceConnType[]{DeviceConnType.NSCONNECTOR_V300};
	}


	@Override
	public DeviceConnection create(Context context, DeviceConnParams params)
			throws Exception {
		throw new UnsupportedOperationException("not support this method!");
	}

}
