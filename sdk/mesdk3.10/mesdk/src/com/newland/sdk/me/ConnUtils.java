package com.newland.sdk.me;

import com.newland.sdk.me.conn.SimpleDeviceManager;

/**
 * 连接工具类
 * 
 *
 * @since ver3.10.01
 */
public class ConnUtils {
	
	/**
	 * 获得一个连接管理器<p>
	 * 
	 * @since ver3.10.01
	 * @return
	 */
	public static final DeviceManager getDeviceManager(){
		return SimpleDeviceManager.getInstance();
	}

}
