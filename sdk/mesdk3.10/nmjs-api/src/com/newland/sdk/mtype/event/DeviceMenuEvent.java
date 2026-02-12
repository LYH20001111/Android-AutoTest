package com.newland.sdk.mtype.event;

import com.newland.sdk.mtype.Device;
import com.newland.sdk.mtype.DeviceRTException;
import com.newland.sdk.mtype.common.ErrorCode;
import com.newland.sdk.mtype.util.InnerUtils;

/**
 * Active menu wakeup event
 * <p>
 * 
 * @author ld
 * @since ver2.0
 */
public class DeviceMenuEvent extends AbstractDeviceEvent {
	private static final int KEY_CANCEL = 0x1B;
	private static final int NO_MENU = 0xFF;
	private static final int MAC_ERROR = 0xFE;
	private static final int TIMEOUT_ERROR = 0xFC;
	
	private byte[] payload;
	/**
	 * Create an active menu wakeup event
	 * @param device Device
	 * @param eventName Event name 
	 * @param payload Menu content
	 */
	public DeviceMenuEvent(Device device, String eventName, byte[] payload) {
		super(device, eventName);
		this.payload = payload;
	}

	/**
	 * 获取交易代码<p> Get the transaction code 
	 * @return
	 * @throws DeviceRTException
	 */
	public String getEcode() throws DeviceRTException {
		if (payload != null) {
			byte[] respCode = new byte[2];
			final byte[] content = new byte[payload.length - respCode.length];
			System.arraycopy(payload, 0, respCode, 0, 2);
			System.arraycopy(payload, 2, content, 0, content.length);
			try {
				String respCodeStr = new String(respCode, "iso8859-1");
				int nativeCode = Integer.valueOf(respCodeStr);
				if (nativeCode != 0 && nativeCode != 8) {
					throw new DeviceRTException(ErrorCode.DEVICE_INVOKE_FAILED, "device invoke failed!nativeCode:" + respCodeStr);
				}
				byte[] keyCode = new byte[1];
				System.arraycopy(content, 0, keyCode, 0, keyCode.length);
				if (keyCode[0] == KEY_CANCEL) {
					return null;
				}
				if (keyCode[0] == NO_MENU) {
					throw new DeviceRTException(ErrorCode.DEVICE_INVOKE_FAILED, "launchMenu failed  due to no menu, resultCode = " + keyCode[0]);
				}
				if (keyCode[0] == MAC_ERROR) {
					throw new DeviceRTException(ErrorCode.DEVICE_INVOKE_FAILED, "launchMenu failed  due to mac error, resultCode = " + keyCode[0]);
				}
				if (keyCode[0] == TIMEOUT_ERROR) {
					throw new DeviceRTException(ErrorCode.DEVICE_INVOKE_FAILED, "launchMenu failed  due to time out, resultCode = " + keyCode[0]);
				}
				byte[] eCode = new byte[5];
				System.arraycopy(content, keyCode.length, eCode, 0, eCode.length);
				return new String(eCode);
			} catch (Exception e) {
				throw new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED, "serialize response failed![" + InnerUtils.hexString(payload) + "]", e);
			}
		}
		return null;
	}

}
