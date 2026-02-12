package com.newland.sdk.mtypex.cmd.packager;

import com.newland.sdk.mtype.DeviceInvokeException;
import com.newland.sdk.mtype.DeviceRTException;
import com.newland.sdk.mtype.ProcessTimeoutException;
import com.newland.sdk.mtype.common.ErrorCode;
import com.newland.sdk.mtype.common.ErrorMsg;
import com.newland.sdk.mtype.common.ErrorMsgHelper;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.Dump;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.mtypex.cmd.CommandSerializer;
import com.newland.sdk.mtypex.cmd.DeviceCommand;
import com.newland.sdk.mtypex.cmd.DeviceResponse;
import com.newland.sdk.mtypex.cmd.ErrorResponse;
import com.newland.sdk.mtypex.cmd.UserCanceledResponse;
import com.newland.sdk.mtypex.cmd.desc.CommandDescription;

public class NLMpos3ProtocalPackager {

	private static DeviceLogger logger = DeviceLoggerFactory.getLogger(NLMpos3ProtocalPackager.class);

	private static final int LEN_CMD = 2;

	private CommandSerializer serializer;

	public NLMpos3ProtocalPackager(CommandSerializer serializer) {
		this.serializer = serializer;
	}

	/**
	 * 组装JINI方式的请求包
	 * 
	 * @return 指令号和可变数据域
	 */
	public byte[] packJNIReqData(DeviceCommand cmd) {
		CommandDescription cmdDesc = getCmdDescription(cmd);
		byte[] cmdcode = cmdDesc.getCmdCode();

		byte[] body = requestToPayload(cmd);

		if (cmdcode == null)
			throw new IllegalArgumentException("cmdcode should not be null!");

		if (body == null)
			throw new IllegalArgumentException("body should not be null!");
		byte[] payload = new byte[LEN_CMD + body.length];

		System.arraycopy(cmdcode, 0, payload, 0, 2);

		System.arraycopy(body, 0, payload, 2, body.length);
		logger.debug(">>>packJNIReqData:" + (payload == null ? "null" : InnerUtils.hexString(payload)));
		return payload;
	}

	/**
	 * 解析JNI返回的数据包
	 * 
	 * @param cmd
	 * @param responseData
	 *            响应码+响应数据
	 * @param listener
	 *            解析响应数据的监听器
	 */
	public void unpackJNIRespData(DeviceCommand cmd, byte[] requestData, byte[] responseData, ResponseUnpackListener listener) {
		DeviceResponse response = null;
		boolean isNotifyResponse = false;
		if (responseData != null) {
			try {
				byte[] respCode = new byte[2];
				System.arraycopy(responseData, 0, respCode, 0, 2);
				byte[] content = new byte[responseData.length - 2];
				System.arraycopy(responseData, 2, content, 0, responseData.length - 2);
				logger.debug(">>>respCode：" + (respCode == null ? "null" : InnerUtils.hexString(respCode)));
				String respCodeStr = new String(respCode, "iso8859-1");
				int nativeCode = Integer.valueOf(respCodeStr);
				if (logger.isDebugEnabled())
					logger.debug("receive resp nativeCode:" + nativeCode);
				switch (nativeCode) {
				case 0:
					if (logger.isDebugEnabled())
						logger.debug("start unpack response,content[" + Dump.getHexDump(content) + "]");
					response = loadDeviceResponse(cmd, content);
					break;
				case 7:// 设备执行超时
					ProcessTimeoutException timeoutException = new ProcessTimeoutException("device invoke timeout!" + nativeCode);
					response = new ErrorResponse(timeoutException);
					recordLog(nativeCode, requestData, responseData,"");
					break;
				case 8: { // 当状态为8时,则可能是一个临时的事件响应,这时候会判断是否该指令是否传入了对应执行的事件监听器
					isNotifyResponse = true;
					response = loadNotifiedDeviceResponse(cmd, content);
					break;
				}
				case 10: {
					logger.debug(">>>Cancel Event");
					response = new UserCanceledResponse();
					break;
				}
				case 11: // 指令注册监听失败
				default:
					int command = 0;
					if(requestData!=null && requestData.length >= 2)
						command = (requestData[0]<<8|requestData[1]);
					ErrorMsg msg = ErrorMsgHelper.getInstance().getErrorMsg(command);
					DeviceInvokeException invokeException = new DeviceInvokeException(respCodeStr,"ErrCode:"+msg.getErrCode()+" ErrMsg:"+msg.getErrMsg());
					response = new ErrorResponse(invokeException);
					recordLog(nativeCode, requestData, responseData,"ErrCode:"+msg.getErrCode()+" ErrMsg:"+msg.getErrMsg()+" OtherMsg:"+msg.getOtherMsg());
					break;
				}

			} catch (Exception e) {
				recordLog(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED, requestData, responseData,"");
				DeviceRTException e1 = new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED, "serialize response failed![" + Dump.getHexDump(responseData) + "]", e);
				response = new ErrorResponse(e1);
			}
		}
		listener.unpackFinished(isNotifyResponse, response);
	}

	private void recordLog(int nativeCode, byte[] requestData, byte[] responseData,String errorMsg) {
		logger.error("[Command execute error]errorCode:" + nativeCode);
		logger.error("[Command execute error]requestData:" + (null == requestData ? "null" : InnerUtils.hexString(requestData)));
		logger.error("[Command execute error]responseData:" + (null == responseData ? "null" : InnerUtils.hexString(responseData)));
		logger.error("[Command execute error]errorMsg:"+errorMsg);
	}

	/**
	 * 将指令序列化成一个字节流
	 * 
	 * @since ver3.10.01
	 * @param deviceCmd
	 *            对应指令
	 * @return 请求字节流
	 */
	protected <T extends DeviceCommand> byte[] requestToPayload(T deviceCmd) {
		return serializer.toRequestPayload(deviceCmd);
	}

	/**
	 * 将字节流反序列化成一个响应
	 * 
	 * @since ver3.10.01
	 * @param deviceCmd
	 *            对应的指令类型
	 * @param payload
	 *            响应实体
	 * @return 响应实体
	 */
	protected <T extends DeviceCommand> DeviceResponse loadDeviceResponse(T deviceCmd, byte[] payload) {
		return serializer.loadDeviceResponse(deviceCmd, payload);
	}

	protected <T extends DeviceCommand> DeviceResponse loadNotifiedDeviceResponse(T deviceCmd, byte[] payload) {
		return serializer.loadNotifiedDeviceResponse(deviceCmd, payload);
	}

	/**
	 * 获得一个指令的描述
	 * 
	 * @since ver3.10.01
	 * @param deviceCmd
	 *            指令类型
	 * @return 指令描述
	 */
	protected <T extends DeviceCommand> CommandDescription getCmdDescription(T deviceCmd) {
		return serializer.getCmdDescription(deviceCmd);
	}
}
