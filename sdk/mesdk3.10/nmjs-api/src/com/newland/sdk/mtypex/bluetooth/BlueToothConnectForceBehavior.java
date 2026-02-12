package com.newland.sdk.mtypex.bluetooth;

import android.os.Build;

import com.newland.sdk.common.RunningModel;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;

import java.util.regex.Pattern;

/**
 * 蓝牙连接策略定义<p>
 * 主要根据以下版本判定蓝牙连接策略<p><ol>
 * <li>Build.MANUFACTURER;设备厂商</li>
 * <li>Build.MODEL;设备型号 </li></ol>
 * 
 * BlueToothConnect<p>
 */
public class BlueToothConnectForceBehavior {
	
	private static final DeviceLogger logger = DeviceLoggerFactory.getLogger("BlueToothConnectForceBehavior");
	
	public enum ConnectType{
		EITHER,
		SECURE,
		INSECURE,
	}
	
	private Pattern p_model;
	private Pattern m_model;
	private boolean isUsingCustomBond;
	private ConnectType forceConnectType;
	private boolean forceUsingReflect;
	private boolean forceUsingDefault;
	
	/**
	 * 蓝牙连接策略
	 * 
	 * @param manufacturer 厂商判定，支持正则表达式 
	 * @param buildModel 设备型号判定，支持正则表达式
	 * @param isUsingCustomBond 是否使用自定义的配对方式，默认启用。
	 * @param forceConnectType 是否强制连接方式为SECURE/INSECURE。该项如果不设置，默认使用INSECURE方式连接。但若使用INSECURE连接，前提条件是能够通过反射获得对应方法。
	 * @param forceUsingReflect 是否强制使用反射，默认不强制
	 * @param forceUsingDefault 是否强制使用默认连接方式(即使用通用的android连接方式，不使用反射模式），该方式开启会覆盖{@link #forceUsingReflect}
	 */
	public BlueToothConnectForceBehavior(String manufacturer , String buildModel, boolean isUsingCustomBond, ConnectType forceConnectType, boolean forceUsingReflect, boolean forceUsingDefault){
		m_model = Pattern.compile(manufacturer);
		p_model = Pattern.compile(buildModel);
		this.isUsingCustomBond = isUsingCustomBond;
		this.forceConnectType = forceConnectType;
		this.forceUsingReflect = forceUsingReflect;
		this.forceUsingDefault = forceUsingDefault;
	}
	
	/**
	 * 是否使用自定义的蓝牙配对过程<p>
	 * 如果不强制设置，则默认会使用自定义的蓝牙配对连接过程<p>
	 * 
	 * @return
	 */
	public boolean isUsingCustomBond(){
		return isUsingCustomBond;
	}
	
	/**
	 * 强制连接类型<p>
	 * connect Type<p>
	 * 
	 * @return
	 */
	public ConnectType forceConnectType(){
		return forceConnectType;
	}
	
	
	
	/**
	 * 由当前上下文环境，判断是否匹配
	 * @return
	 */
	public boolean matches(){
		boolean rslt = m_model.matcher(Build.MANUFACTURER).matches() && p_model.matcher(Build.MODEL).matches();
		if(RunningModel.isDebugEnabled){
			logger.debug("[matches]Build.MANUFACTURER:"+Build.MANUFACTURER+",Build.MODEL:"+Build.MODEL+",("+m_model.pattern()+","+p_model.pattern()+")?matches:"+rslt);
		}
		return rslt;
	}

	public boolean forceUsingReflect() {
		return forceUsingReflect;
	}
	public boolean forceUsingDefault() {
		return forceUsingDefault;
	}	


}
