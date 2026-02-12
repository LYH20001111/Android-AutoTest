package com.newland.sdk.me;

//import com.newland.sdk.mpos.cmd.common.CmdCancelAndReset;
import com.newland.sdk.mtypex.conn.DeviceConnection;
import com.newland.sdk.mtypex.conn.DeviceKeepAliveStrategy;

import java.util.concurrent.TimeUnit;

/**
 * 默认的me31和me30连接策略
 * 
 * @author lance
 * @since ver1.0
 */
public class DefaultME3xKeepAliveStrategy implements DeviceKeepAliveStrategy {
	
	private int timeoutSec;

	public DefaultME3xKeepAliveStrategy(int timeoutSec){
		this.timeoutSec = timeoutSec;
	}
	
//	@Override
//	public int touch(DeviceConnection deviceConnection) throws Exception {
//		return 0;
//	}
//
//	@Override
//	public boolean isTouchAvailable() {
//		return false;
//	}
//
//	@Override
//	public boolean isKeyboardAwareSupported() {
//		return false;
//	}

	@Override
	public long getDefaultExecTimeout() {
		return TimeUnit.SECONDS.toMillis(timeoutSec);
	}

	@Override
	public void doDefaultReset(DeviceConnection deviceConnection) throws Exception{
		if(deviceConnection != null && !deviceConnection.isClosed()){
//			CmdCancelAndReset reset = new CmdCancelAndReset();
//			deviceConnection.send(reset,getDefaultResetTimeout());
		}
	}

//	@Override
//	public long getTouchDuringTime() {
//		return -1L;
//	}
//
//	@Override
//	public int getTouchMaxFailedTimes() {
//		return -1;
//	}
//
//	@Override
//	public long getTouchTimeout() {
//		return -1L;
//	}

	@Override
	public long getDefaultResetTimeout() {
		return 3000L;
	}
}
