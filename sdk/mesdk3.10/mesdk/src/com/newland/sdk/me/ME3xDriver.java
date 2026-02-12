package com.newland.sdk.me;


import com.newland.sdk.mtype.conn.DeviceConnParams;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.AbstractMESeriesDriver;
import com.newland.sdk.mtypex.bluetooth.BlueToothConnector;
import com.newland.sdk.mtypex.conn.DeviceConnector;
import com.newland.sdk.mtypex.conn.DeviceExecutor;
import com.newland.sdk.mtypex.conn.DeviceKeepAliveStrategy;
import com.newland.sdk.mtypex.usb.UsbConnector;

import java.util.ArrayList;
import java.util.List;


public class ME3xDriver extends AbstractMESeriesDriver {

	private static final List<DeviceConnector> deviceConnectors = new ArrayList<DeviceConnector>();
	static{
		deviceConnectors.add(new BlueToothConnector(getDefaultCommandSerializer()));
		deviceConnectors.add(new UsbConnector(getDefaultCommandSerializer()));
	}
	
	@Override
	protected AbstractDevice createDevice(DeviceExecutor executor) {
		return new ME3xDevice(executor);
	}
	@Override
	protected DeviceKeepAliveStrategy getDeviceKeepAliveStrategy(DeviceConnParams connParams) {
			return new DefaultME3xKeepAliveStrategy(3);
	}
	

	@Override
	protected List<DeviceConnector> initConnectors() {
		return deviceConnectors;
	}

}
