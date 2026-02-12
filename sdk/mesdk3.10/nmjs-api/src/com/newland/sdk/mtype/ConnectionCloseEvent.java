package com.newland.sdk.mtype;

import com.newland.sdk.mtype.event.AbstractProcessDeviceEvent;

/**
 * 连接关闭
 * Disconnection 
 * 
 * @author lance
 *
 */
public class ConnectionCloseEvent extends AbstractProcessDeviceEvent {

	/**
	 * 构建一个客户正常断开的连接关闭事件<p>
	 * Construct a customer normal disconnection event
	 * 
	 * @param eventName 断开事件名称<p>Disconnection event name
	 */
	public ConnectionCloseEvent(String eventName) {
		super(eventName, ProcessState.SUCCESS, null);
	}
	
	/**
	 * 构建一个非正常断开的连接关闭事件<p>
	 * Construct a non-normal disconnection event
	 * 
	 * @param eventName 断开事件名称  <p>Disconnection event name
	 * @param e 断开异常<p>Disconnection abnormity
	 */
	public ConnectionCloseEvent(String eventName, Throwable e){
		super(eventName, ProcessState.FAILED,e);
	}
	
	/**
	 * 构建一个客户正常断开的连接关闭事件<p>
	 *  Construct a customer normal disconnection event
	 * 
	 * @since 1.1.6
	 * @param device 断开的设备<p> Disconnected device 
	 * @param eventName 事件名称 <p>Event name 
	 */
	public ConnectionCloseEvent(Device device, String eventName) {
		super(device,eventName, ProcessState.SUCCESS, null);
	}
	/**
	 * 构建一个非正常断开的连接关闭事件<p>
	 * Construct a customer non-normal disconnection event
	 * 
	 * @since 1.1.6
	 * @param device 断开的设备 <p>Disconnected device 
	 * @param eventName 事件名称  <p>Event name 
	 * @param e 断开异常 <p>Disconnection abnormity
	 */	
	public ConnectionCloseEvent(Device device, String eventName, Throwable e){
		super(device,eventName, ProcessState.FAILED,e);
	}

}
