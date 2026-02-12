package com.newland.sdk.me.module.externalsignature;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.newland.sdk.me.module.externalPininput.PinpadPackage;
import com.newland.sdk.me.module.serialport.MESerial;
import com.newland.sdk.module.externalPin.PinpadInitExtParams;
import com.newland.sdk.module.externalsignature.DoSignExtParams;
import com.newland.sdk.module.externalsignature.DoSignListener;
import com.newland.sdk.module.externalsignature.ExtSignatureModule;
import com.newland.sdk.module.externalsignature.SignatureExtParams;
import com.newland.sdk.module.serialport.Baudrate;
import com.newland.sdk.module.serialport.PinpadModel;
import com.newland.sdk.module.serialport.PortType;
import com.newland.sdk.mtype.ExModuleType;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.AbstractModule;
import com.newland.sdk.utils.ISOUtils;

import java.util.Arrays;

public class MeExternalSignature extends AbstractModule implements ExtSignatureModule {
	private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger("MeExternalSignature");

	private MESerial serialOper;
	private static final byte STX = 0x02;
	private static final byte ETX = 0x03;

	/* 握手请求 */
	private static final byte HAND_SHAKE_REQ = (byte) 0xA0;
	/* 握手响应 */
	private static final byte HAND_SHAKE_RESP = (byte) 0xB0;
	/* 签字输入请求 */
	private static final byte SIGN_REQ = (byte) 0xA2;
	/* 签字完成成功响应 */
	private static final byte SIGN_SUCC_RESP = (byte) 0xB2;
	/* 签字完成异常响应 */
	private static final byte SIGN_FAIL_RESP = (byte) 0xC2;

	/* 签字结束请求 */
	private static final byte SIGN_END_REQ = (byte) 0xA3;
	/* 签字结束响应 */
	private static final byte SIGN_END_RESP = (byte) 0xB3;

	/* 前一笔签字上传状态结果通知 */
	private static final byte LAST_STATUS_REQ = (byte) 0xA1;
	/* 前一笔签字上传状态结果响应 */
	private static final byte LAST_STATUS_RESP = (byte) 0xB1;

	/* 软硬件版本请求响应 */
	private static final byte GET_VERSION_REQ_RESP = (byte) 0xF1;

	/* 签名板屏幕背光请求响应 */
	private static final byte BL_LED_REQ_RESP = (byte) 0xF2;

	/* 签名板模式请求响应 */
	private static final byte SIGN_MODE_REQ_RESP = (byte) 0xF3;

	/* 签名板显示内容请求响应 */
	private static final byte SIGN_CONTENT_REQ_RESP = (byte) 0xF3;

	private static final int MAX_RESP_LEN = 1024;

	private int mTimeOut = 5000;
	private int mRetryTime = 1;

	private PinpadModel mPinpadModel = null;
	private PortType mPortType = PortType.PINPAD;
	private Baudrate mBaudrate = Baudrate.BPS115200;
	private PinpadPackage  pinpadPackage;
	public MeExternalSignature(AbstractDevice device, Context context) {
		super(device);
		pinpadPackage=PinpadPackage.getInstance(device,context);
		mPinpadModel = pinpadPackage.getModel();
	}

	@Override
	public boolean isStandardModule() {
		return false;
	}

	@Override
	public ModuleType getStandardModuleType() {
		return null;
	}

	@Override
	public String getExModuleType() {
		return ExModuleType.SIGNATURE;
	}

	@Override
	public boolean init(PinpadInitExtParams params) {
		devicelogger.debug("[init] params:"+params);
		boolean rs=pinpadPackage.init(params,true);
		mPinpadModel=pinpadPackage.getModel();
		return rs;
	}

	@Override
	public boolean setSignatureParams(SignatureExtParams params) {
		devicelogger.debug("[setSignatureParams] params:"+(params==null?null:params.toString()));
		if(mPinpadModel==null){
			mPinpadModel=pinpadPackage.getModel();
		}

		if (mPinpadModel == PinpadModel.SP) {
			devicelogger.debug("[handShake]");
			byte[] resp = pinpadPackage.boardTxn(null, HAND_SHAKE_REQ, new byte[0], mTimeOut + 3000);
			if (!pinpadPackage.checkResp(HAND_SHAKE_RESP, resp, 1)) {
				return false;
			}
			if (resp[1] != 1) {
				return false;
			}
		}

		// Timeout
		int bordTimeout = params.getBordTimeout();
		if (bordTimeout <= 0) {
			bordTimeout = 1;
		}
		this.mTimeOut = bordTimeout*1000;
		if (bordTimeout > (0xFFFF / 10)) {
			bordTimeout = 0xFFFF / 10;
		}
		bordTimeout *= 10;
		byte[] data = pinpadPackage.intToB2(bordTimeout);
		if (mPinpadModel == PinpadModel.SP) {
			byte[] resp = pinpadPackage.boardTxn(null, (byte) 0xF7, data,(mTimeOut+PinpadPackage.EXTCMD_OFFSETTIME_MS));
			if (!pinpadPackage.checkResp((byte) 0xF7, resp, 1)) {
				devicelogger.error("[setSignatureParams] setBordTimeout failed.");
				return false;
			}
			if (resp[1] != 0) {
				return false;
			}
		} else if (mPinpadModel == PinpadModel.SP_OVERSEAS) {
		}

		// ReSignature times
		int reSignTimes = params.getReSignTimes();
		if (reSignTimes < 0) {
			reSignTimes = 0;
		}
		if (reSignTimes > 0xFF) {
			reSignTimes = 0xFF;
		}
		this.mRetryTime = reSignTimes;
		data = new byte[1];
		data[0] = (byte) reSignTimes;
		if (mPinpadModel == PinpadModel.SP) {
			byte[] resp = pinpadPackage.boardTxn(null, (byte) 0xF8, data,(mTimeOut+PinpadPackage.EXTCMD_OFFSETTIME_MS));
			if (!pinpadPackage.checkResp((byte) 0xF8, resp, 1)) {
				devicelogger.error("[setSignatureParams] setReSignTimes failed.");
				return false;
			}
			if (resp[1] != 0) {
				return false;
			}
		} else if (mPinpadModel == PinpadModel.SP_OVERSEAS) {
		}

		// isWhiteBackground
		boolean isWhiteBackground = params.isWhiteBackground();
		byte[] state = new byte[]{0x00};
		if (!isWhiteBackground) {
			state[0] = 0x10;
		}
		if (mPinpadModel == PinpadModel.SP) {
			byte[] resp = pinpadPackage.boardTxn(null, (byte) 0xE4, state,(mTimeOut+PinpadPackage.EXTCMD_OFFSETTIME_MS));
			if (!pinpadPackage.checkResp((byte) 0xE4, resp, 1)) {
				devicelogger.error("[setSignatureParams] setWhiteBackground failed.");
				return false;
			}
			if (resp[1] != 0) {
				return false;
			}
		} else if (mPinpadModel == PinpadModel.SP_OVERSEAS) {
			devicelogger.error("[setSignatureParams] setWhiteBackground Oversea not supported yet ");
		}

		// isBackLight
		boolean isBackLight = params.isBackLight();
		data = new byte[1];
		data[0] = (byte) (isBackLight ? 1 : 0);
		if (mPinpadModel == PinpadModel.SP) {
			byte[] resp = pinpadPackage.boardTxn(null, BL_LED_REQ_RESP, data,(mTimeOut+PinpadPackage.EXTCMD_OFFSETTIME_MS));
			if (!pinpadPackage.checkResp(BL_LED_REQ_RESP, resp, 1)) {
				devicelogger.error("[setSignatureParams] setBoardLedBl failed.");
				return false;
			}
			if (resp[1] != 0) {
				return false;
			}
		} else if (mPinpadModel == PinpadModel.SP_OVERSEAS) {
			devicelogger.error("[setSignatureParams] isBackLight Oversea not supported yet ");
		}

		// isSaveSign
		boolean isSaveSign = params.isSaveSign();
		data = new byte[1];
		data[0] = (byte) (isSaveSign ? 1 : 0);
		if (mPinpadModel == PinpadModel.SP) {
			byte[] resp = pinpadPackage.boardTxn(null, SIGN_MODE_REQ_RESP, data,(mTimeOut+PinpadPackage.EXTCMD_OFFSETTIME_MS));
			if (!pinpadPackage.checkResp(SIGN_MODE_REQ_RESP, resp, 1) || resp[1] != 0) {
				devicelogger.error("[setSignatureParams] setBoardLedBl failed.");
				return false;
			}
		} else if (mPinpadModel == PinpadModel.SP_OVERSEAS) {
			devicelogger.error("[setSignatureParams] isSaveSign Oversea not supported yet ");
		}
		return true;
	}

	@Override
	public byte[] doSign(String code) {
		devicelogger.debug("[doSign], code = " + code);
		if(mPinpadModel==null){
			mPinpadModel=pinpadPackage.getModel();
		}
		byte[] tmp;
		if (code != null) {
			tmp = code.getBytes();
		} else {
			tmp = new byte[0];
		}
		byte[] data;
		if (mPinpadModel == PinpadModel.SP) {
			data = new byte[8];
			System.arraycopy(tmp, 0, data, 0, Math.min(tmp.length, data.length));
			byte[] resp = pinpadPackage.boardTxn(null, SIGN_REQ, data,(mTimeOut+PinpadPackage.EXTCMD_OFFSETTIME_MS));
			if (!pinpadPackage.checkResp(SIGN_SUCC_RESP, resp, 1)) {
				devicelogger.error("[doSign], null");
				tmp = null;
//				return null;
			}else {
				tmp = new byte[resp.length - 1];
				System.arraycopy(resp, 1, tmp, 0, resp.length - 1);
			}

			// signEnd
			resp = pinpadPackage.boardTxn(null, SIGN_END_REQ, new byte[0],(mTimeOut+PinpadPackage.EXTCMD_OFFSETTIME_MS));
			if (!pinpadPackage.checkResp(SIGN_END_RESP, resp, 0)) {
				devicelogger.error("[doSign] SignEnd failed");
			}
			devicelogger.debug("[doSign] sign data: " + InnerUtils.hexString(tmp));
			return tmp;
		} else {
			//握手请求
			devicelogger.info("[handShake]");// TODO: 2022/2/11
			byte[] messageType = "S0".getBytes();
			byte[] resp = pinpadPackage.boardTxn(messageType, HAND_SHAKE_REQ, new byte[0], PinpadPackage.EXTCMD_TIMEOUT_MS + 2000);
			devicelogger.info("Oversea handShake response: " + (resp == null ? "null" : InnerUtils.hexString(resp)));
			pinpadPackage.getPinpadRspCode();
			if (resp == null || !Arrays.equals(new byte[]{0x53, 0x31}, new byte[]{resp[0], resp[1]})||!Arrays.equals(new byte[]{0x30, 0x30}, new byte[]{resp[4], resp[5]})) {
				devicelogger.error("[handShake] resopnd error");
				return null;
			}

			//发起签名
//			byte[] messageType = "S0".getBytes();
			data = new byte[1 + 2 + 8];
			data[0] = (byte) mRetryTime;
			byte[] timeout = InnerUtils.intToBytes(mTimeOut/1000, 2, true);
			System.arraycopy(timeout, 0, data, 1, timeout.length);
			System.arraycopy(tmp, 0, data, 3, tmp.length);
			resp = pinpadPackage.boardTxn(messageType, SIGN_REQ, data,(mTimeOut+PinpadPackage.EXTCMD_OFFSETTIME_MS));
			devicelogger.debug("[doSign] Oversea doSign response: " + (resp == null ? "null" : InnerUtils.hexString(resp)));
			pinpadPackage.getPinpadRspCode();
			byte[] temp = null;
			if (resp != null && resp.length >= 5) {
				if (Arrays.equals("S1".getBytes(), new byte[]{resp[0], resp[1]})) {
					if (Arrays.equals(new byte[]{0x30, 0x30}, new byte[]{resp[4], resp[5]})) {
						temp = new byte[resp.length - 6];
						System.arraycopy(resp, 6, temp, 0, temp.length);
						devicelogger.debug("[doSign] Sign data" + InnerUtils.hexString(temp));
					} else {
						String respCode = new String(new byte[]{resp[4], resp[5]});
						devicelogger.error("[doSign] Oversea doSign failed, " + respCode);
//						return null;
					}
				} else {
					devicelogger.error("[doSign] message type error");
//					return null;
				}
			} else {
				devicelogger.error("[doSign] resp error");
//				return null;
			}

			// Complete Signature
			resp = pinpadPackage.boardTxn(messageType, (byte) 0xA3, new byte[0],(mTimeOut)+PinpadPackage.EXTCMD_OFFSETTIME_MS);
			devicelogger.debug("[doSign] Oversea complete signature response: " + ISOUtils.hexString(resp));
			pinpadPackage.getPinpadRspCode();
			if (resp != null && resp.length >= 5) {
				if (Arrays.equals("S1".getBytes(), new byte[]{resp[0], resp[1]})) {
					if (!Arrays.equals(new byte[]{0x30, 0x30}, new byte[]{resp[4], resp[5]})) {
//						return temp;
//					} else {
						String respCode = new String(new byte[]{resp[4], resp[5]});
						devicelogger.error("[doSign] Complete Signature error, " + respCode);
					}
				} else {
					devicelogger.error("[doSign] Complete Signature error");
				}
			}
			return temp;
		}
	}

	@Override
	public void doSign(@Nullable String definedInput, @NonNull DoSignListener listener, DoSignExtParams extParams) {
		devicelogger.debug("[doSign], code = " + definedInput);
		if (mPinpadModel == null) {
			mPinpadModel = pinpadPackage.getModel();
		}
		if (mPinpadModel == PinpadModel.SP_OVERSEAS) {
			//握手请求
			byte[] messageType = "S0".getBytes();
			byte[] resp = pinpadPackage.boardTxn(messageType, HAND_SHAKE_REQ, new byte[0], PinpadPackage.EXTCMD_TIMEOUT_MS + 2000);
			devicelogger.info("Oversea handShake response: " + InnerUtils.hexString(resp));
			pinpadPackage.getPinpadRspCode();
			if (resp == null || !Arrays.equals(new byte[]{0x53, 0x31}, new byte[]{resp[0], resp[1]}) ||
					!Arrays.equals(new byte[]{0x30, 0x30}, new byte[]{resp[4], resp[5]})) {
				devicelogger.error("[handShake] response error");
				listener.onError(-1, "handShake failed");
				return;
			}

			byte[] code;
			if (definedInput != null) {
				code = definedInput.getBytes();
			} else {
				code = new byte[0];
			}
			messageType = "S2".getBytes();
			byte[] data = new byte[13 + code.length];
			byte[] defaultParam = new byte[]{0x01, 0x00, 0x00, 0x00};
			byte[] defaultWidth = InnerUtils.intToBytes(300, 2, true);
			byte[] defaultHeight = InnerUtils.intToBytes(100, 2, true);
			int timeout = 60;
			System.arraycopy(defaultParam, 0, data, 0, defaultParam.length);
			System.arraycopy(defaultWidth, 0, data, 4, defaultWidth.length);
			System.arraycopy(defaultHeight, 0, data, 6, defaultHeight.length);
			data[8] = 0x03; // 默认重试3次
			byte[] defaultTimeout = InnerUtils.intToBytes(timeout, 2, true);
			System.arraycopy(defaultTimeout, 0, data, 9, defaultTimeout.length);

			if (extParams != null) {
				if (extParams.isBypass()) {
					data[0] = 0x00;
				} else {
					data[0] = 0x01;
				}
				if (extParams.isContainCode()) {
					data[1] = 0x00;
				} else {
					data[1] = 0x01;
				}
				if (extParams.isButtonDisplay()) {
					data[2] = 0x00;
				} else {
					data[2] = 0x01;
				}
				if (extParams.isJBigData()) {
					data[3] = 0x01;
				} else {
					data[3] = 0x00;
				}
				if (extParams.getWidth() > 0) {
					byte[] width = InnerUtils.intToBytes(extParams.getWidth(), 2, true);
					System.arraycopy(width, 0, data, 4, width.length);
				}
				if (extParams.getHeight() > 0) {
					byte[] height = InnerUtils.intToBytes(extParams.getHeight(), 2, true);
					System.arraycopy(height, 0, data, 6, height.length);
				}
				if (extParams.getRetryTime() > 0) {
					data[8] = extParams.getRetryTime();
				}
				if (extParams.getTimeout() > 0) {
					timeout = extParams.getTimeout();
					byte[] timeoutTemp = InnerUtils.intToBytes(extParams.getTimeout(), 2, true);
					System.arraycopy(timeoutTemp, 0, data, 9, timeoutTemp.length);
				}
			}

			if (definedInput != null && !definedInput.isEmpty()) {
				byte[] dataLength = InnerUtils.intToBytes(definedInput.length(), 2, true);
				System.arraycopy(dataLength, 0, data, 11, dataLength.length);
				System.arraycopy(code, 0, data, 13, code.length);
			}

			resp = pinpadPackage.boardTxn(messageType, (byte) 0x00, data, (timeout * 1000 + PinpadPackage.EXTCMD_OFFSETTIME_MS));
			devicelogger.debug("[doSign] Oversea doSign response: " + InnerUtils.hexString(resp));

			pinpadPackage.getPinpadRspCode();
			if (resp != null && resp.length >= 5) {
				if (Arrays.equals("S3".getBytes(), new byte[]{resp[0], resp[1]})) {
					if (Arrays.equals(new byte[]{0x30, 0x30}, new byte[]{resp[3], resp[4]})) {
						byte[] temp = new byte[resp.length - 5 - 6];
						System.arraycopy(resp, 5 + 6, temp, 0, temp.length);
						devicelogger.debug("[doSign] Sign data=" + InnerUtils.hexString(temp));
						listener.onSuccess(temp);
					} else {
						String respCode = new String(new byte[]{resp[3], resp[4]});
						devicelogger.error("[doSign] Oversea doSign failed errorCode=" + respCode);
						int errorCode;
						String errorMessage;
						switch (respCode) {
							case "01":
								errorCode = -11;
								errorMessage = "Parameter error";
								break;
							case "02":
								errorCode = -12;
								errorMessage = "Area out of the screen";
								break;
							case "03":
								errorCode = -13;
								errorMessage = "The device does not supported signature";
								break;
							case "07":
								errorCode = -14;
								errorMessage = "Bypass";
								break;
							case "09":
								listener.onTimeout();
								return;
							case "10":
								listener.onCancel();
								return;
							case "55":
								errorCode = -17;
								errorMessage = "Not supported";
								break;
							default:
								errorCode = -10;
								errorMessage = "Other error " + respCode;
								break;
						}
						listener.onError(errorCode, errorMessage);
					}
				} else {
					devicelogger.error("[doSign] message type error");
					listener.onError(-2, "Message Type error");
				}
			} else {
				devicelogger.error("[doSign] resp error");
				listener.onError(-3, "Response data is null");
			}
		} else {
			devicelogger.error("PinpadMode.SP not supported");
			listener.onError(-100, "Not supported");
		}
	}
}
