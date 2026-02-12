package com.newland.sdk.mtypex.bluetooth;


import com.newland.sdk.mtype.conn.DeviceConnParams;
import com.newland.sdk.mtype.conn.DeviceConnType;
import com.newland.sdk.mtype.event.DeviceEventListener;
import com.newland.sdk.mtype.event.DeviceMenuEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 蓝牙连接参数<p>
 * BlueTooth V100 Conn Params
 * 
 * @author lance
 * @modify
 * <pre><blockquote> 
 * at 1.1.0
 * 不再能够控制具体的连接行为，相关连接行为将会使用一个通用的策略完成。
 * </pre></blockquote>
 */
public class BlueToothConnParams implements DeviceConnParams {
	
	/**
	 * 默认的连接通道<p>
	 * DEFAULT CONNECT CHANNEL
	 */
	private static final int DEFAULT_CONNECT_CHANNEL = 6;
	
	/**
	 * @deprecated by 1.1.0
	 */
	public static final String KEY_IS_AUTO_CONNECT = "IS_AUTO_CONNECT";
	
	public static final String KEY_BT_DEFAULT_CHANNEL = "BT_DEFAULT_CHANNEL";
	/**
	 * @deprecated by 1.1.0
	 */
	public static final String KEY_IS_USING_REFLECT_TO_CONNECT = "IS_USING_REFLECT_TO_CONNECT";
	/**
	 * @deprecated by 1.1.0
	 */
	public static final String KEY_IS_BT_USING_INSECURE = "IS_BT_USING_INSECURE";
	
	private Map<String,String> params = new HashMap<String,String>();
	
	private DeviceEventListener<DeviceMenuEvent> initiativeListener;
	
	public BlueToothConnParams(String address){
		params.put(BlueToothConnector.PARAM_BLUETOOTH_REMOTEADDR, address);
	}
	public BlueToothConnParams(String address, DeviceEventListener<DeviceMenuEvent> initiativeListener){
		params.put(BlueToothConnector.PARAM_BLUETOOTH_REMOTEADDR, address);
		this.initiativeListener=initiativeListener;
	}
	
	private boolean checkIsEnabled(String value){
		return (value.equalsIgnoreCase("yes") 
				|| value.equalsIgnoreCase("enabled")
				|| value.equalsIgnoreCase("enable")
				|| value.equalsIgnoreCase("true")
				|| value.equalsIgnoreCase("t")
				|| value.equalsIgnoreCase("y"));
	}
	
	/**
	 * @deprecated by 1.1.0
	 * @return
	 */
	public boolean isAutoConnect(){
		String v = params.get(KEY_IS_USING_REFLECT_TO_CONNECT);
		return v == null || checkIsEnabled(v);
	}
	
	/**
	 * @deprecated by 1.1.0
	 * @return
	 */
	public boolean usingreflectToConnect(){
		String v = params.get(KEY_IS_USING_REFLECT_TO_CONNECT);
		return v != null && checkIsEnabled(v);
	}
	/**
	 * @deprecated by 1.1.0
	 * @return
	 */
	public boolean isUsingInSecure(){
		String v = params.get(KEY_IS_BT_USING_INSECURE);
		return v == null || (checkIsEnabled(v)); //默认为true
	}
	
	public int getBTDefaultChannel(){
		String str = params.get(KEY_BT_DEFAULT_CHANNEL);
		if(str != null)
			return Integer.valueOf(str);
		
		return DEFAULT_CONNECT_CHANNEL;
	}

	@Override
	public DeviceConnType getConnectType() {
		return DeviceConnType.BLUETOOTH_V100;
	}

	@Override
	public Set<String> getParamKeys() {
		return params.keySet();
	}

	@Override
	public String getParam(String key) {
		return params.get(key);
	}
	
	/**
	 * @deprecated by 1.1.0
	 * @param isAutoConnect
	 */
	public void setAutoConnect(boolean isAutoConnect){
		params.put(KEY_IS_AUTO_CONNECT, String.valueOf(isAutoConnect));
	}
	
	/**
	 * @deprecated by 1.1.0
	 * @param isInsecure
	 */
	public void setUsingInsecure(boolean isInsecure){
		params.put(KEY_IS_BT_USING_INSECURE, String.valueOf(isInsecure));
	}
	public void setDefaultChannel(Integer channel){
		params.put(KEY_BT_DEFAULT_CHANNEL, String.valueOf(channel));
	}
	/**
	 * @deprecated by 1.1.0
	 * @param isUsingreflectToConnect
	 */
	public void setUsingreflectToConnect(boolean isUsingreflectToConnect){
		params.put(KEY_IS_USING_REFLECT_TO_CONNECT, String.valueOf(isUsingreflectToConnect));
	}

	public DeviceEventListener<DeviceMenuEvent> getInitiativeListener() {
		return initiativeListener;
	}

//	public interface DeviceInitiativeListener{
//		
//		public  void onError(Exception ex);
//		
//		public void fetchEcode(String eCode);
//	}
}
