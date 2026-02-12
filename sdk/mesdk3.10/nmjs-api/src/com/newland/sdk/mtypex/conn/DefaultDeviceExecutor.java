package com.newland.sdk.mtypex.conn;

import android.content.Context;
import android.util.Log;

import com.newland.sdk.mtype.ConnectionCloseEvent;
import com.newland.sdk.mtype.Device;
import com.newland.sdk.mtype.DeviceInvokeException;
import com.newland.sdk.mtype.DeviceOutofLineException;
import com.newland.sdk.mtype.ProcessTimeoutException;
import com.newland.sdk.mtype.common.EventConst;
import com.newland.sdk.mtype.conn.DeviceConnParams;
import com.newland.sdk.mtype.event.DeviceEventListener;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.SimIdGenerator;
import com.newland.sdk.mtypex.cmd.CommandInvokeRslt;
import com.newland.sdk.mtypex.cmd.DeviceCommand;
import com.newland.sdk.mtypex.cmd.DeviceResponse;
import com.newland.sdk.mtypex.conn.DeviceConnection.InvokeStateNotifyListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 设备连接基于如下模式管理:
 * <p>
 * <ol>
 * <li>支持的设备在工作时，仅保留一个连接通道，该通道在上条指令响应结束前将无限期阻塞，直到设备响应，超时或者接受到撤消指令。</li>
 * <li>管理器将会未设置超时时间的指令，默认设置一个超时时间</li>
 * <li>管理器将支持阻塞和非阻塞2种模式执行指令，任何一种模式均可以设置一个可控的超时时间。</li>
 * </ol>
 * <p>
 * 关于超时处理
 * <ol>
 * <li>在设备被认为是超时处理时，设备将被尝试发送一次撤消指令（如果指令支持撤消）或一个<tt>touch（测试连接）</tt>
 * 指令（若指令不支持撤消）。
 * <li>若设备在指定时间内返回，则被认为是执行超时，这次执行将被设置一个{@link ProcessTimeoutException}异常。但设备存活。
 * </li>
 * <li>若设备未在指定时间内返回返回，则认为连接已中断，管理器将执行{@link DefaultDeviceExecutor#destroy()}方法
 * </li>
 * <li>若设备本身不支持<tt>touch</tt>指令，则在超时后将直接触发一个关闭操作。</li>
 * </ol>
 * <p>
 * 关于指令撤消
 * <ol>
 * <li>不是所有指令都认为是可撤消。可撤消指令需要继承<tt>AbortableDeviceCommand</tt>
 * ，并制订用于撤消的指令。仅可撤消指令会响应撤消请求。</li>
 * <li>可撤消指令在未进队列时，将直接被置撤消位而不被执行。</li>
 * <li>若撤消指令在执行是发生撤消请求，将中断执行指令过程中的线程等待，并发送一个撤消指令给设备。</li>
 * <li>若撤消指令不响应，会被认为设备状态不合法。将会要求关闭连接.</li>
 * </ol>
 * <p>
 * 
 * 
 * @author lance
 * 
 * @since ver1.0
 */
public class DefaultDeviceExecutor implements DeviceExecutor {

	private volatile InnerDeviceConnState state;

	private DeviceLogger deviceLogger = DeviceLoggerFactory.getLogger("DefaultDeviceExecutor");

	private DeviceConnection connection;

	private static final Object idGenSync = new Object();

	private static SimIdGenerator simIdGenerator = new SimIdGenerator(999999);

	private TransferKernel transferKernel;

	private String closeEventName;

	private String awareEventName;

	private DeviceKeepAliveStrategy keepAliveStrategy;

	private boolean isKeyboardAwareEnabled = false;

	private Context context;

	private DeviceConnector deviceConnector;

	private DeviceConnParams params;

	private DeviceEventListener<ConnectionCloseEvent> closedListener;

	private Device device;

	private Timer timer = new Timer(true);

	public DefaultDeviceExecutor(Context context, DeviceConnector deviceConnector, DeviceConnParams params, DeviceEventListener<ConnectionCloseEvent> closedListener, DeviceKeepAliveStrategy touchStrategy) throws Exception {
		state = InnerDeviceConnState.NOT_INIT;
		this.keepAliveStrategy = touchStrategy;
		this.context = context;
		this.deviceConnector = deviceConnector;
		this.params = params;
		this.closedListener = closedListener;
	}

	private void registerListener(DeviceEventListener<ConnectionCloseEvent> closedListener) {
		closeEventName = EventConst.EVENT_DEVICE_CONN_CLOSE_ + simIdGenerator.getId(idGenSync);
		DeviceInnerEventDispatcher.instance().registerOnceEvent(closeEventName, closedListener);

	}

	public void init(Device device) throws Exception {
		this.device = device;
		connection = deviceConnector.create(context, params);
		transferKernel = new TransferKernel(connection);
		transferKernel.start();
		state = InnerDeviceConnState.PREPARED;
		registerListener(closedListener);

	}

	/**
	 * 异常响应对象
	 * <p>
	 * 
	 * @author lance
	 * @since ver1.0
	 */
	private class ErrorResponse implements DeviceResponse {

		/**
		 *
		 */
		private static final long serialVersionUID = 1214830402297639249L;
		private Throwable e;

		public ErrorResponse(Throwable e) {
			this.e = e;
		}

		@Override
		public boolean isSuccess() {
			return false;
		}

		@Override
		public Throwable getException() {
			return e;
		}

		@Override
		public boolean isUserCanceled() {
			return false;
		}

		@Override
		public CommandInvokeRslt getProcessRslt() {
			return CommandInvokeRslt.FAILED;
		}

	}

	public void destroy() {
		destroy0(true);
	}

	private void destroy0(boolean isUserCancel) {
		synchronized (state) {
			if (state == InnerDeviceConnState.CLOSED)
				return;
			state = InnerDeviceConnState.CLOSED;
		}
		if (transferKernel.isAlive()) {
			try {
				transferKernel.interrupt();
				transferKernel.join(300); // 等待结束
			} catch (Exception e1) {
			}
		}

		try {
			connection.close();
		} catch (IOException e) {
			deviceLogger.debug("failed to close connection:" + connection.getId(), e);
		} finally {
			ConnectionCloseEvent event = null;
			if (!isUserCancel) {
				event = new ConnectionCloseEvent(this.device, closeEventName, transferKernel.closeReason);
			} else {
				event = new ConnectionCloseEvent(this.device, closeEventName);
			}
			deviceLogger.info("process a connection close event!" + closeEventName);
			DeviceInnerEventDispatcher.instance().dispatchEvent(event);
			DeviceInnerEventDispatcher.instance().removeEvent(awareEventName);

			transferKernel.clearQueue();
		}

	}

	private interface ResponseCallback {
		public void callback(InnerMessage message);
	}

	private enum InnerExecutingState {

		PREPARED, // 等待执行
		CANCEL, // 外部撤消
		RUNNING, // 运行中
		SUCCESS, // 交易结束
		TIMEOUT, // 超时
		CAUGHTINTERRUPT;// 接收到中断指令
	}

	private class CancelResponse implements DeviceResponse {
		/**
		 *
		 */
		private static final long serialVersionUID = -7987062045603113461L;

		@Override
		public boolean isUserCanceled() {
			return true;
		}

		@Override
		public boolean isSuccess() {
			return false;
		}

		@Override
		public Throwable getException() {
			return null;
		}

		@Override
		public CommandInvokeRslt getProcessRslt() {
			return CommandInvokeRslt.USER_CANCELED;
		}

	}

	/**
	 * 内部队列消息
	 * 
	 * @author lance
	 * 
	 * @since ver1.0
	 */
	private class InnerMessage {

		private DeviceLogger logger = DeviceLoggerFactory.getLogger("InnerMessage");
		/**
		 * 执行同步器
		 */
		private final Object invokeSync = new Object();

		private volatile InnerExecutingState executingState = InnerExecutingState.PREPARED;

		private DeviceCommand request;
		private DeviceResponse deviceResponse;
		private long timeout = keepAliveStrategy.getDefaultExecTimeout();
		private ResponseCallback callback;
		private String eventName;
		private TimerTask timetask = new TimerTask() {
			@Override
			public void run() {
				if (logger.isDebugEnabled())
					logger.debug("InnerMessage [" + request.getClass().getName() + "] Timeout!" + timeout);
				InnerMessage.this.cancel();
			}
		};

		private class AbortController implements Abortable {
			@Override
			public void abort() {
				cancel();
			}

			@Override
			public void abort(int keyCode) {
				cancel();
			}
		}

		public InnerMessage(DeviceCommand request, String eventName, ResponseCallback callback) {
			this.request = request;
			if (request instanceof AbortableDeviceCommand) {
				((AbortableDeviceCommand) this.request).setOutAbortController(new AbortController()); // 若可撤消指令,设置一个指令撤消控制器.使得可以通过外部指令直接调用到innermsg撤消控制.
			}
			this.callback = callback;
			this.eventName = eventName;
		}

		public InnerMessage(DeviceCommand request, String eventName, long timeout, TimeUnit timeunit, ResponseCallback callback) {
			this(request, eventName, callback);
			this.timeout = timeunit.toMillis(timeout);
		}

		public boolean startSendwait(Thread doCmd, long timeout) throws InterruptedException {
			synchronized (executingState) {
				if (executingState == InnerExecutingState.PREPARED) { // 若是其他状态,例如已经被撤消了.就不执行了.
					if (deviceLogger.isDebugEnabled())
						deviceLogger.debug("start cmd...");
					doCmd.start();
					executingState = InnerExecutingState.RUNNING;
				} else
					return false;
			}
			synchronized (invokeSync) {
				invokeSync.wait(timeout);
			}
			if (deviceLogger.isDebugEnabled())
				deviceLogger.debug("cmd end...");
			return true;

		}

		private void startTimeoutCount() {
			timer.schedule(timetask, timeout);
		}

		public void cancel() {
			boolean doNotify = true;
			synchronized (executingState) {
				if (executingState == InnerExecutingState.RUNNING) { // 当指令已经执行,则需要唤醒线程
					this.deviceResponse = new CancelResponse();
					executingState = InnerExecutingState.CANCEL;
				} else if (executingState == InnerExecutingState.PREPARED) { // 否则,说明还在队列里,可以直接撤消.
					this.deviceResponse = new CancelResponse();
					executingState = InnerExecutingState.CANCEL;
					doNotify = false;
				} else
					return;
			}
			if (doNotify) {
				synchronized (invokeSync) {
					invokeSync.notify();
				}
			} else
				doResponseBack(); // 可以通知执行事件响应了。

		}

		public void notifyReceive(DeviceResponse deviceResponse) {
			synchronized (executingState) {
				if (executingState == InnerExecutingState.RUNNING) {
					if (deviceLogger.isDebugEnabled())
						logger.debug("notify deviceResponse:" + (deviceResponse == null ? "null" : deviceResponse.getClass().getName()));
					this.deviceResponse = deviceResponse;
					executingState = InnerExecutingState.SUCCESS;

				} else
					return;
			}
			synchronized (invokeSync) {
				invokeSync.notify();
			}
		}

		public void checkTimeout() {
			synchronized (executingState) {// 到检查超时时，状态如果还是执行中，表示还未执行notifyReceive，表示超时。
				if (executingState == InnerExecutingState.RUNNING) {
					executingState = InnerExecutingState.TIMEOUT;
				}
			}
		}

		/**
		 * 在未经过测试前,避免死锁,只好新建一个对象用于控制callback为空时的重入. 而避免使用公用对象,避免死锁.
		 */
		private Object callbackSync = new Object();

		public void doResponseBack() {
			new Thread(new Runnable() {
				@Override
				public void run() {
					synchronized (callbackSync) {
						if (callback != null) {
							timetask.cancel();
							callback.callback(InnerMessage.this);
							callback = null;
						}
					}
				}
			}).start();
		}

	}

	private class TransferKernel extends Thread {

		/**
		 * 预关闭标志
		 */
		private volatile boolean shouldBeClosed = false;
		private Throwable closeReason = null;

		private InnerMessage currMsg = null;

		/**
		 * 开始空闲时间
		 * <p>
		 */
		private long startIdletime = -1L;
		/**
		 * 尝试连接失败的次数
		 */
		private int failedTimes = 0;
		/**
		 * 消息队列
		 * <p>
		 */
		private LinkedBlockingQueue<InnerMessage> msgQueue = new LinkedBlockingQueue<InnerMessage>();
		/**
		 * 持有的设备连接
		 */
		private final DeviceConnection connection;

		public TransferKernel(DeviceConnection connection) {
			this.connection = connection;
		}

		public void clearQueue() {
			LinkedBlockingQueue<InnerMessage> tempQueue = null;
			synchronized (this) {
				if (msgQueue != null) {
					tempQueue = msgQueue;
					transferKernel.msgQueue = null;
				}
			}

			InnerMessage innerMsg = null;
			if (tempQueue != null) {
				while ((innerMsg = tempQueue.poll()) != null) { // 唤醒所有阻塞线程
					Exception closeReason = new DeviceOutofLineException("conncection has ben destroyed!");

					innerMsg.deviceResponse = new ErrorResponse(closeReason);
					innerMsg.doResponseBack();
				}
			}

		}

		/**
		 * 
		 * @author lance
		 * 
		 * @since ver1.0
		 */
		private class DoCmd extends Thread {

			private DeviceConnection.InvokeStateNotifyListener listener;

			private final InnerMessage innerMessage;

			public DoCmd(final InnerMessage innerMessage) {
				this.innerMessage = innerMessage;
				if (innerMessage.eventName != null) { // 如果本身基于事件方式处理,则可以在调用过程中允许一个多次事件响应的过程.
					listener = new InvokeStateNotifyListener() {
						@Override
						public void notify(DeviceResponse deviceResponse) {
							DeviceInnerEventDispatcher.instance().dispatchEvent(new InvokeEvent(innerMessage.eventName, deviceResponse));
						}
					};
				}
			}

			@Override
			public void run() {
				if (innerMessage == null)
					return;

				DeviceResponse response = null;
				try {
					if (deviceLogger.isDebugEnabled())
						deviceLogger.debug("start send innerMessage!");
                    response = connection.send(innerMessage.request, listener, innerMessage.timeout);
					if (deviceLogger.isDebugEnabled())
						deviceLogger.debug("send innerMessage finished!");
				} catch (InterruptedException e) {
				} catch (Exception e) {// 该连接被认为有问题
					deviceLogger.error("send request meet error!,connection should be closed!", e);
					shouldBeClosed = true;
					closeReason = e;
					response = new ErrorResponse(e);
				}
				if (response == null) {
					if (deviceLogger.isDebugEnabled())
						deviceLogger.debug("send innerMessage meet null response!");
					response = new ErrorResponse(new NullPointerException("send but return null response!"));
				}

				innerMessage.notifyReceive(response);

			}

		}

		public void interrupt() {
			shouldBeClosed = true;
			super.interrupt();
		}

		@Override
		public void run() {
			try {
				while (!Thread.currentThread().isInterrupted() && !shouldBeClosed) {

					if (connection.isClosed()) {// 判断连接是否关闭
						shouldBeClosed = true;
						closeReason = new DeviceOutofLineException("connection should have been closed!");
						break;
					}

					/**
					 * 每隔90毫秒获取一次消息
					 */
					synchronized (state) {
						currMsg = msgQueue.poll(90L, TimeUnit.MILLISECONDS);
					}

					if (currMsg != null) {
						state = InnerDeviceConnState.BUSY;
						startIdletime = -1; // 空闲重置

						DoCmd doCmd = new DoCmd(currMsg);
						boolean isInvoking = false;
						isInvoking = currMsg.startSendwait(doCmd, currMsg.timeout);
						if (doCmd.isAlive())
							doCmd.interrupt();
						if (isInvoking) { // 若有执行，要判定是否超时或者是被撤消。
							if (deviceLogger.isDebugEnabled())
								deviceLogger.debug("innerMessage currMsg:"+currMsg.request.getClass().getName());
							handleRslt(currMsg); // 检查交易状态
						} // 若没有执行，可能被撤消，直接返回为空即可。
						currMsg.doResponseBack();
					} else {
						state = InnerDeviceConnState.PREPARED;

						if (isKeyboardAwareEnabled) {
							if (startIdletime < 0) {
								startIdletime = System.currentTimeMillis();
							} else {
								long duringTime = System.currentTimeMillis() - startIdletime;
								if (duringTime >= -1L) {
									doTouch();
									startIdletime = -1;
								}
							}
						}

					}
					Thread.sleep(3);
				}
			} catch (Exception e) {
				// deviceLogger.error("kernel meet fatal exception! connection
				// should be closed!",e);
				shouldBeClosed = true;
				closeReason = e;
			} finally {
				DefaultDeviceExecutor.this.destroy0(false);// 销毁,并回收资源
			}
		}

		private void doTouch() throws Exception {
            failedTimes = 0;
//			int rslt = -1;
//			rslt = keepAliveStrategy.touch(connection);
//			if (rslt < 0) {
//				deviceLogger.warn("device not touched!failed time:" + failedTimes);
//				failedTimes++;
//			} else {
//				failedTimes = 0;
//				if (isKeyboardAwareEnabled) {
//					if (rslt > 0) { // 当键盘唤醒支持设置时，将发起唤醒
//						if (awareEventName != null) {
//							DeviceInnerEventDispatcher.instance().dispatchEvent(new DeviceKeyboardAwareEvent(device, awareEventName, rslt));
//						}
//					}
//				}
//			}
		}

		/**
		 * 结果处理
		 * 
		 * @param currMsg
		 * @throws Exception
		 */
		private void handleRslt(InnerMessage currMsg) throws Exception {
			currMsg.checkTimeout(); // 检查交易是否超时
			if (currMsg.executingState == InnerExecutingState.TIMEOUT || currMsg.executingState == InnerExecutingState.CANCEL) {
				if (currMsg.request instanceof AbortableDeviceCommand) {
					AbortableDeviceCommand toBeCanceled = (AbortableDeviceCommand) currMsg.request;
					connection.send(toBeCanceled.getAbortCommand(), null, keepAliveStrategy.getDefaultResetTimeout());
				} else {
					keepAliveStrategy.doDefaultReset(connection);
				}

				if (currMsg.executingState == InnerExecutingState.TIMEOUT) {
					// 新建一个超时的事件通知
					currMsg.deviceResponse = new ErrorResponse(new ProcessTimeoutException("invoke timeout:" + currMsg.request));

					failedTimes++;
					deviceLogger.warn("device execute timeout!failed time:" + failedTimes);
				}
			} else if (currMsg.executingState == InnerExecutingState.SUCCESS) { // 当成功结束时，将失败次数重置
				failedTimes = 0;
			}
		}
	}

	public DeviceResponse execute0(DeviceCommand deviceRequest, long timeout, TimeUnit timeunit, DeviceEventListener<InvokeEvent> listener) throws Throwable {
		if (!isAlive()) {
			throw new DeviceOutofLineException("connection is closed or not inited!");
		}
		deviceLogger.debug("execute0: timeout="+timeout+" listener="+listener+" deviceRequest="+deviceRequest);
		String eventName = null;
		ResponseCallback callback = null;
		if (listener != null) {
			boolean regSucc = false;
			while (!regSucc) {
				eventName = EventConst.EVENT_EXECUTE_FINISH_ + simIdGenerator.getId(idGenSync);
				regSucc = DeviceInnerEventDispatcher.instance().registerEvent(eventName, listener);
			}
			final String en = eventName;
			callback = new ResponseCallback() {
				@Override
				public void callback(InnerMessage msg) {
					InvokeEvent event = null;
					if (msg == null) {
						event = new InvokeEvent(en, null);
					} else {
						event = new InvokeEvent(en, msg.deviceResponse);
					}
					DeviceInnerEventDispatcher.instance().dispatchEvent(event);
					DeviceInnerEventDispatcher.instance().removeEvent(event.getEventName());// 注销事件
				}
			};
		} else {
			callback = new ResponseCallback() {
				@Override
				public void callback(InnerMessage msg) {
					if (msg == null)
						return;

					synchronized (msg) {
						msg.notify();
					}
				}
			};
		}
		InnerMessage innerMessage = null;
		if (timeout <= 0) {
			innerMessage = new InnerMessage(deviceRequest, eventName, callback);
		} else {
			innerMessage = new InnerMessage(deviceRequest, eventName, timeout, timeunit, callback);
		}
		boolean isAbort = false;
		synchronized (deviceRequest) { // 确保在判断是否被撤消的过程中,所有撤消过程全部阻塞
			if (deviceRequest instanceof AbortableDeviceCommand) {
				deviceLogger.debug(" deviceRequest:"+deviceRequest.getClass().getName()+" do isAbort");
				isAbort = ((AbortableDeviceCommand) deviceRequest).isAbort();
			}
			if (isAbort) {// 如果被取消
				return new CancelResponse();
			}
			boolean hasBeenPut = false;
			deviceLogger.debug("execute0: state="+state);
			synchronized (state) {
				if (!isAlive()) {
					throw new DeviceOutofLineException("connection is closed or not inited!!");
				}
				if (transferKernel.msgQueue != null) {
					transferKernel.msgQueue.offer(innerMessage);
					hasBeenPut = true;
					innerMessage.startTimeoutCount();
				}
			}
			deviceLogger.debug("execute0: hasBeenPut="+hasBeenPut);
			if (hasBeenPut) {
				if (eventName == null) { // 没有事件名称,则需要同步返回
					deviceLogger.debug("execute0: eventName="+eventName);
					synchronized (innerMessage) {
						innerMessage.wait(innerMessage.timeout);// 一定要被其他事件唤醒,如果不被唤醒,说明这里存在bug.需要查找问题
					}
					deviceLogger.debug("execute0: innerMessage="+innerMessage);
					if (innerMessage.deviceResponse == null) {
						innerMessage.deviceResponse = new ErrorResponse(new ProcessTimeoutException("process time out!"));
					}
					return innerMessage.deviceResponse;
				}
				return null;
			} else { // 没有放入对列，对列已经被清空或者销毁，连接已经处于关闭状态
				Exception closeReason = new DeviceOutofLineException("conncection has been destroyed!");
				innerMessage.deviceResponse = new ErrorResponse(closeReason);
				if (eventName != null) {
					innerMessage.doResponseBack();// 利用回调送一个关闭事件
				}
				return innerMessage.deviceResponse;// 返回关闭事件
			}
		}
	}

	@Override
	public void invoke(DeviceCommand deviceRequest, DeviceEventListener<InvokeEvent> listener) {
		try {
			execute0(deviceRequest, -1, null, listener);
		} catch (Throwable e) {
			deviceLogger.error("send meeting error", e);
		}
	}

	@Override
	public void invoke(DeviceCommand deviceRequest, long timeout, TimeUnit timeunit, DeviceEventListener<InvokeEvent> listener) {
		try {
			execute0(deviceRequest, timeout, timeunit, listener);
		} catch (Throwable e) {
			deviceLogger.error("send meeting error", e);
		}
	}

	@Override
	public DeviceResponse invoke(DeviceCommand deviceRequest, long timeout, TimeUnit timeunit) {
		try {
			if(deviceRequest instanceof DeviceCommandWrite){
				return connection.send(deviceRequest,timeunit.toMillis(timeout));
			}
			return execute0(deviceRequest, timeout, timeunit, null);
		} catch (InterruptedException e) {
			return null;
		} catch (Throwable e) {
			throw new DeviceInvokeException("invoke request failed!", e);
		}
	}

	@Override
	public DeviceResponse invoke(DeviceCommand deviceRequest) {
		try {
			if(deviceRequest instanceof DeviceCommandWrite){
				return connection.send(deviceRequest,-1);
			}
			return execute0(deviceRequest, -1, null, null);
		} catch (InterruptedException e) {
			return null;
		} catch (Throwable e) {
			throw new DeviceInvokeException("invoke request failed!", e);
		}
	}

	@Override
	public boolean isAlive() {
		return !(state == InnerDeviceConnState.CLOSED || state == InnerDeviceConnState.NOT_INIT);
	}

//	@Override
//	public boolean isBusy() {
//		return state == InnerDeviceConnState.BUSY;
//	}
//
//	@Override
//	public void isDeviceAvailable() {
//		if (!isAlive()) {
//			throw new DeviceOutofLineException("device is not alive!try to reconnect!");
//		}
//	}

	@Override
	public InnerDeviceConnState getDeviceConnectionState() {
		return state;
	}

	@Override
	public DeviceResponse directInvoke(DeviceCommand deviceCommand) throws IOException, InterruptedException {
		return connection.send(deviceCommand, keepAliveStrategy.getDefaultExecTimeout());
	}


	@Override
	public void cancelCurrentExecCmd() {
		// for(InnerMessage innerMessage : transferKernel.msgQueue){
		// //释放队列里所有阻塞着的交易.
		// innerMessage.cancel();
		// }
		List<InnerMessage> toCanceled = new ArrayList<InnerMessage>();
		synchronized (state) {
			transferKernel.msgQueue.drainTo(toCanceled);
			if (transferKernel.currMsg != null) { // 释放当前交易
				try {
					if (deviceLogger.isDebugEnabled())
						deviceLogger.debug("cancel executing event:" + transferKernel.currMsg.eventName);
					transferKernel.currMsg.cancel();
				} catch (Exception e) { // 不使用锁的机制,所以可能currMsg会为空,这里处理一下所有可能出现的异常
				}
			}
		}

		for (InnerMessage innerMessage : toCanceled) {
			if (deviceLogger.isDebugEnabled())
				deviceLogger.debug("cancel queue event:" + innerMessage.eventName);
			innerMessage.cancel();
		}
	}

//	@Override
//	public DirectMessageListenerManager getDirectMessageListenerManager() {
//		return connection;
//	}

	@Override
	public Context getContext() {
		return context;
	}

}
