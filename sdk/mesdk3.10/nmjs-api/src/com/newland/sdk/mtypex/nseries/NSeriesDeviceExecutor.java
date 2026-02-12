package com.newland.sdk.mtypex.nseries;

import android.content.Context;

import com.newland.k21controller.ControllerException;
import com.newland.k21controller.K21CmdInvokeNotifyListener;
import com.newland.k21controller.K21ControllerManager;
import com.newland.k21controller.K21DeviceCommand;
import com.newland.k21controller.K21DeviceResponse;
import com.newland.k21controller.K21Status;
import com.newland.k21controller.K21TransactionListener;
import com.newland.k21controller.util.Dump;
import com.newland.sdk.mtype.Device;
import com.newland.sdk.mtype.DeviceRTException;
import com.newland.sdk.mtype.OpenTrasactionException;
import com.newland.sdk.mtype.TransactionNeededException;
import com.newland.sdk.mtype.TransactionStatus;
import com.newland.sdk.mtype.common.ErrorCode;
import com.newland.sdk.mtype.common.EventConst;
import com.newland.sdk.mtype.event.DeviceEventListener;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.mtype.util.SimIdGenerator;
import com.newland.sdk.mtypex.cmd.CommandSerializer;
import com.newland.sdk.mtypex.cmd.DeviceCommand;
import com.newland.sdk.mtypex.cmd.DeviceResponse;
import com.newland.sdk.mtypex.cmd.ErrorResponse;
import com.newland.sdk.mtypex.cmd.UserCanceledResponse;
import com.newland.sdk.mtypex.cmd.packager.ByteArrayProtocalPackagerReader;
import com.newland.sdk.mtypex.cmd.packager.DeviceCommProtocalPackager;
import com.newland.sdk.mtypex.cmd.packager.NLMposProtocalPackager;
import com.newland.sdk.mtypex.cmd.packager.ReadResponseListener;
import com.newland.sdk.mtypex.cmd.packager.ResponseUnpackListener;
import com.newland.sdk.mtypex.conn.Abortable;
import com.newland.sdk.mtypex.conn.AbortableDeviceCommand;
import com.newland.sdk.mtypex.conn.DeviceInnerEventDispatcher;
import com.newland.sdk.mtypex.conn.DirectMessageListenerManager;
import com.newland.sdk.mtypex.conn.InnerDeviceConnState;
import com.newland.sdk.mtypex.conn.InvokeEvent;
import com.newland.sdk.mtypex.conn.TransationabledDeviceExecutor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class NSeriesDeviceExecutor implements TransationabledDeviceExecutor {

	private static DeviceLogger logger = DeviceLoggerFactory.getLogger(NSeriesDeviceExecutor.class);

	private SimIdGenerator simIdGenerator = new SimIdGenerator(0xFE);

	private static final Object idGenSync = new Object();

	private volatile int eventNameSeed = 0;

	private K21ControllerManager controllerManager;

	private Context context;

	private CommandSerializer cmdSerializer;

	private DeviceCommProtocalPackager packager;

	private volatile boolean needTransaction;

	public NSeriesDeviceExecutor(Context context, CommandSerializer cmdSerializer) {
		this.context = context;
		this.controllerManager = K21ControllerManager.getInstance(context);
		this.cmdSerializer = cmdSerializer;
	}

	@Override
	public void init(Device device) throws Exception {
		controllerManager.connect();
		packager = new NLMposProtocalPackager(cmdSerializer);
	}

	private class DefaultReadResponseListener implements ReadResponseListener {

		byte[] serial;
		byte[] body;

		@Override
		public void processRslt(byte[] serial, byte[] body) {
			this.serial = serial;
			this.body = body;
		}

		@Override
		public void notifyDirectMessage(byte[] cmdCode, byte[] body) {
			throw new UnsupportedOperationException("not support this method yet!");
		}

		@Override
		public boolean processRslt(byte[] recvData) {
			return false;
		}

	}

	private class DefaultK21CmdInvokeNotifyListener implements K21CmdInvokeNotifyListener {

		private DeviceCommand cmd;

		private DeviceResponse response;

		private String eventName;

		public DefaultK21CmdInvokeNotifyListener(DeviceCommand cmd, String eventName) {
			this.cmd = cmd;
			this.eventName = eventName;
		}

		@Override
		public void notify(K21DeviceResponse k21Resp) {
			/**
			 * 优先判定k21底层返回的状态
			 */
			switch (k21Resp.getInvokeResult()) {
			case FAILED:
				notifyErrResp(null, k21Resp.getThrowable());
				return;
			case PACKAGE_ERROR:
				notifyErrResp(null, new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED, "package failed"));
				return;
			case USER_CANCEL:
				notifyResponse(true, new UserCanceledResponse());
				return;
			default:
			}

			byte[] respPayload = k21Resp.getResponse();
			DefaultReadResponseListener readResplistener = new DefaultReadResponseListener();
			if (respPayload != null) {
				try {
					packager.readResponseFrom(new ByteArrayProtocalPackagerReader(respPayload), readResplistener);
				} catch (Exception e) {
					notifyErrResp(respPayload, e);
					return;
				}
			}
			if (readResplistener.body == null || readResplistener.body.length <= 0) {
				notifyErrResp(respPayload, new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED, "response body should not be null or length == 0!"));
			}

			packager.unpack(cmd, readResplistener.body, new ResponseUnpackListener() {
				@Override
				public void unpackFinished(boolean isNotifyResponse, DeviceResponse response) {
					boolean isFinished = true;
					if (isNotifyResponse) {
						isFinished = false;
					}
					notifyResponse(isFinished, response);
				}
			});
		}

		private void notifyErrResp(byte[] respPayload, Throwable e) {
			String errMsg = "failed to invoke cmd:" + cmd.getClass().getName();
			if (logger.isDebugEnabled() && respPayload != null)
				errMsg += Dump.getHexDump(respPayload);
			notifyResponse(true, new ErrorResponse(new DeviceRTException(ErrorCode.DEVICE_INVOKE_FAILED, errMsg, e)));
			return;
		}

		private void notifyResponse(boolean isFinished, DeviceResponse response) {
			this.response = response;
			if (eventName != null) {
				DeviceInnerEventDispatcher.instance().dispatchEvent(new InvokeEvent(eventName, response));
				if (isFinished)
					DeviceInnerEventDispatcher.instance().removeEvent(eventName);
			}

		}
	}

	public class InvokingThread implements Runnable {

		String eventName = null;
		DeviceCommand cmd;
		long timeout = -1;
		TimeUnit timeunit = null;

		public InvokingThread(String eventName, DeviceCommand cmd) {
			this(eventName, cmd, -1, null);
		}

		public InvokingThread(String eventName, DeviceCommand cmd, long timeout, TimeUnit timeunit) {
			this.cmd = cmd;
			this.timeout = timeout;
			this.timeunit = timeunit;
			this.eventName = eventName;
		}

		@Override
		public void run() {
			try {
				int serial = simIdGenerator.getId(idGenSync, 2).intValue();
				byte[] reqPayload = packager.pack(serial, cmd);
				K21DeviceCommand k21cmd = new K21DeviceCommand(reqPayload);
				K21DeviceResponse response = null;
				DefaultK21CmdInvokeNotifyListener notifyListener = new DefaultK21CmdInvokeNotifyListener(cmd, eventName);
				if (timeout < 0) {
					response = controllerManager.sendCmd(k21cmd, notifyListener);
				} else {
					response = controllerManager.sendCmd(k21cmd, timeout, timeunit, notifyListener);
				}
				notifyListener.notify(response);
			} catch (Exception e) {
				try {
					DeviceInnerEventDispatcher.instance().dispatchEvent(new InvokeEvent(eventName, new ErrorResponse(e)));
					DeviceInnerEventDispatcher.instance().removeEvent(eventName);
				} catch (Exception e1) {
				}
			}
		}

	}

	@Override
	public void invoke(DeviceCommand deviceRequest, DeviceEventListener<InvokeEvent> listener) {
		transactionHoldCheck();
		registerAbortOperator(deviceRequest);
		if (listener == null) {
			logger.warn("DeviceEventListener should not be null!");
		}
		String eventName = EventConst.EVENT_EXECUTE_FINISH_ + (eventNameSeed++);
		DeviceInnerEventDispatcher.instance().registerEvent(eventName, listener);
		new Thread(new InvokingThread(eventName, deviceRequest)).start();
	}

	@Override
	public void invoke(DeviceCommand deviceRequest, long timeout, TimeUnit timeunit, DeviceEventListener<InvokeEvent> listener) {
		transactionHoldCheck();
		registerAbortOperator(deviceRequest);
		if (listener == null) {
			logger.warn("DeviceEventListener should not be null!");
		}
		String eventName = EventConst.EVENT_EXECUTE_FINISH_ + (eventNameSeed++);
		DeviceInnerEventDispatcher.instance().registerEvent(eventName, listener);
		new Thread(new InvokingThread(eventName, deviceRequest, timeout, timeunit)).start();
	}

	@Override
	public DeviceResponse invoke(DeviceCommand cmd, long timeout, TimeUnit timeunit) {
		transactionHoldCheck();
		registerAbortOperator(cmd);
		try {
			int serial = simIdGenerator.getId(idGenSync, 2).intValue();
			byte[] reqPayload = packager.pack(serial, cmd);
			K21DeviceCommand k21cmd = new K21DeviceCommand(reqPayload);
			DefaultK21CmdInvokeNotifyListener listener = new DefaultK21CmdInvokeNotifyListener(cmd, null);
			K21DeviceResponse response = controllerManager.sendCmd(k21cmd, timeout, timeunit, null);
			listener.notify(response);
			return listener.response;
		} catch (Exception e) {
			return new ErrorResponse(e);
		}
	}

	@Override
	public DeviceResponse invoke(DeviceCommand cmd) {
		transactionHoldCheck();
		registerAbortOperator(cmd);
		try {
			int serial = simIdGenerator.getId(idGenSync, 2).intValue();
			byte[] reqPayload = packager.pack(serial, cmd);
			K21DeviceCommand k21cmd = new K21DeviceCommand(reqPayload);
			DefaultK21CmdInvokeNotifyListener listener = new DefaultK21CmdInvokeNotifyListener(cmd, null);
			K21DeviceResponse response = controllerManager.sendCmd(k21cmd, null);
			listener.notify(response);
			return listener.response;
		} catch (Exception e) {
			return new ErrorResponse(e);
		}
	}

	@Override
	public DeviceResponse directInvoke(DeviceCommand deviceCommand) throws IOException, InterruptedException {
		return invoke(deviceCommand);
	}

	@Override
	public void cancelCurrentExecCmd() {
		controllerManager.cancelCmd();
	}

	@Override
	public void destroy() {
		controllerManager.close();
	}

	@Override
	public boolean isAlive() {
		K21Status tempStatus = controllerManager.getK21Status();
		return tempStatus != K21Status.DIS_CONNECTED && tempStatus != K21Status.CONNECT_FAILED && tempStatus != K21Status.SECURITY_ATTACK;
	}

	@Override
	public boolean isBusy() {
		K21Status tempStatus = controllerManager.getK21Status();
		return tempStatus == K21Status.BUSY || tempStatus == K21Status.BUSY_IN_TRANSACTION || tempStatus == K21Status.CMD_INVOKE || tempStatus == K21Status.HOLD_TRANSACTION;
	}

//	@Override
	public void isDeviceAvailable() {
		for (int i = 1; i <= 3; i++) {
			K21Status k21Status = controllerManager.getK21Status();
			if (K21Status.PREPARED == k21Status || k21Status == K21Status.HOLD_TRANSACTION) {
				break;
			} else if (i == 3) {
				throw new DeviceRTException(ErrorCode.DEVICE_BUSY, "device is not prepare!try to resend!");
			}
			InnerUtils.sleep(30);
		}
	}

	@Override
	public InnerDeviceConnState getDeviceConnectionState() {
		if (controllerManager == null)
			return InnerDeviceConnState.NOT_INIT;

		K21Status state = controllerManager.getK21Status();
		switch (state) {
		case PREPARED:
		case HOLD_TRANSACTION: // 据马鑫汶反馈，该状态为当前应用持有事务并空闲。若持有事务繁忙则返回CMD_INVOKE.
			return InnerDeviceConnState.PREPARED;
		case BUSY:
		case BUSY_IN_TRANSACTION:
		case CMD_INVOKE:
			return InnerDeviceConnState.BUSY;
		case CONNECT_FAILED:
		case DIS_CONNECTED:
		case SECURITY_ATTACK:
			return InnerDeviceConnState.CLOSED;
		default:
			throw new DeviceRTException(ErrorCode.UNKNOWN, "unknown k21status:" + state);
		}
	}

//	@Override
	public DirectMessageListenerManager getDirectMessageListenerManager() {
		// TODO 暂时不支持
		return null;
	}

	@Override
	public synchronized void beginTransaction(long timeout, TimeUnit timeUnit) throws OpenTrasactionException {
		try {
			controllerManager.beginTransactions(timeout, timeUnit, new K21TransactionListener() {
				@Override
				public void timeout() {
					needTransaction = false;
					logger.warn("transaction is timeout!");
				}
			});
			needTransaction = true;
		} catch (ControllerException e) {
			throw new OpenTrasactionException("failed to open transaction!", e);
		}
	}

	@Override
	public synchronized void endTransaction() throws OpenTrasactionException {
		try {
			controllerManager.endTransactions();
			needTransaction = false;
		} catch (ControllerException e) {
			throw new OpenTrasactionException("failed to open transaction!", e);
		}
	}

	@Override
	public TransactionStatus getTransactionStatus() {
		if (controllerManager.getK21Status() == K21Status.HOLD_TRANSACTION) {
			return TransactionStatus.HOLD;
		} else {
			return TransactionStatus.NOT_IN;
		}
	}

	public synchronized void transactionHoldCheck() {
		if (needTransaction && controllerManager.getK21Status() != K21Status.HOLD_TRANSACTION) {
			logger.warn("should be working at a transaction!");
			needTransaction = false;
			throw new TransactionNeededException("should be working at a transaction!");
		}
	}

	private void registerAbortOperator(DeviceCommand command) {
		if (command instanceof AbortableDeviceCommand) {
			((AbortableDeviceCommand) command).setOutAbortController(new Abortable() {
				@Override
				public void abort() {
					cancelCurrentExecCmd();
				}

				@Override
				public void abort(int keyCode) {
					cancelCurrentExecCmd();
				}
			});
		}
	}

	@Override
	public Context getContext() {
		return context;
	}
}
