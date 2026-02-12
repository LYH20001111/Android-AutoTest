package com.newland.sdk.mtypex.nseries3;

import android.content.Context;

import com.newland.intelligent.jni.CmdRspListener;
import com.newland.intelligent.jni.JniCmdInterface;
import com.newland.sdk.mtype.Device;
import com.newland.sdk.mtype.DeviceRTException;
import com.newland.sdk.mtype.OpenTrasactionException;
import com.newland.sdk.mtype.TransactionStatus;
import com.newland.sdk.mtype.common.ErrorCode;
import com.newland.sdk.mtype.common.EventConst;
import com.newland.sdk.mtype.event.DeviceEventListener;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.mtypex.cmd.CommandSerializer;
import com.newland.sdk.mtypex.cmd.DeviceCommand;
import com.newland.sdk.mtypex.cmd.DeviceResponse;
import com.newland.sdk.mtypex.cmd.ErrorResponse;
import com.newland.sdk.mtypex.cmd.packager.NLMpos3ProtocalPackager;
import com.newland.sdk.mtypex.cmd.packager.ResponseUnpackListener;
import com.newland.sdk.mtypex.conn.Abortable;
import com.newland.sdk.mtypex.conn.AbortableDeviceCommand;
import com.newland.sdk.mtypex.conn.DeviceInnerEventDispatcher;
import com.newland.sdk.mtypex.conn.InnerDeviceConnState;
import com.newland.sdk.mtypex.conn.InvokeEvent;
import com.newland.sdk.mtypex.conn.TransationabledDeviceExecutor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class NS3Executor implements TransationabledDeviceExecutor {
    private static DeviceLogger logger = DeviceLoggerFactory.getLogger(NS3Executor.class);
    private volatile int eventNameSeed = 0;
    private NLMpos3ProtocalPackager packager;
    private CommandSerializer cmdSerializer;
    private Context context;
    private static final int DEFALUT_TIMEOUT_CODE = -9999;

    public NS3Executor(Context context, CommandSerializer cmdSerializer) {
        this.context = context;
        this.cmdSerializer = cmdSerializer;
    }

    @Override
    public void init(Device device) throws Exception {
        packager = new NLMpos3ProtocalPackager(cmdSerializer);
    }

    @Override
    public void invoke(DeviceCommand deviceRequest, DeviceEventListener<InvokeEvent> listener) {
        registerAbortOperator(deviceRequest);
        if (listener == null) {
            logger.warn("DeviceEventListener should not be null!");
        }
        String eventName = EventConst.EVENT_EXECUTE_FINISH_ + (eventNameSeed++);
        DeviceInnerEventDispatcher.instance().registerEvent(eventName, listener);
        new Thread(new InvokingThread(eventName, deviceRequest, 4, TimeUnit.SECONDS)).start();
    }

    @Override
    public void invoke(DeviceCommand deviceRequest, long timeout, TimeUnit timeunit, DeviceEventListener<InvokeEvent> listener) {
        registerAbortOperator(deviceRequest);
        if (listener == null) {
            logger.error("DeviceEventListener should not be null!");
        }
        String eventName = EventConst.EVENT_EXECUTE_FINISH_ + (eventNameSeed++);
        DeviceInnerEventDispatcher.instance().registerEvent(eventName, listener);

        new Thread(new InvokingThread(eventName, deviceRequest, timeout, timeunit)).start();
    }

    @Override
    public DeviceResponse invoke(final DeviceCommand deviceCmd) {
        try {
            registerAbortOperator(deviceCmd);
            SynInvokeRunnable invokeRunnable = new SynInvokeRunnable(deviceCmd, -1, null);
            new Thread(invokeRunnable).start();
            invokeRunnable.waitting();
            logger.debug(">>>(debug)jniMposLibCmd_responeCode:" + invokeRunnable.jniCmdResultCode + ",deviceCmd:" + deviceCmd);
            logger.debug(">>>(debug)jniMposLibCmd_responseData:" + (invokeRunnable.jniResponseData == null ? "null" : InnerUtils.hexString(invokeRunnable.jniResponseData)) + ",deviceCmd:" + deviceCmd);
            DefaultJNICmdInvokeNotifyListener listener = new DefaultJNICmdInvokeNotifyListener(null, deviceCmd, invokeRunnable.jniReqPayload, invokeRunnable.jniResponseData);
            if (invokeRunnable.jniCmdResultCode < 0 && invokeRunnable.jniCmdResultCode != DEFALUT_TIMEOUT_CODE) {
                showErrorLog(invokeRunnable.jniCmdResultCode, invokeRunnable.jniReqPayload, invokeRunnable.jniResponseData);
                listener.notifyErrResp(invokeRunnable.jniResponseData, new DeviceRTException(ErrorCode.DEVICE_INVOKE_FAILED, "execute jniMposLibCmd falied"));
            } else if (invokeRunnable.jniResponseData == null || invokeRunnable.jniResponseData.length <= 0) {
                showErrorLog(invokeRunnable.jniCmdResultCode, invokeRunnable.jniReqPayload, invokeRunnable.jniResponseData);
                listener.notifyErrResp(invokeRunnable.jniResponseData, new DeviceRTException(ErrorCode.PROCESS_TIMEOUT, "execute jniMposLibCmd timeout"));
            } else {
                listener.notifyResponse();
            }
            return listener.response;
        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorResponse(e);
        }
    }

    @Override
    public DeviceResponse invoke(final DeviceCommand deviceRequest, final long timeout, final TimeUnit timeunit) {
        try {
            registerAbortOperator(deviceRequest);
            SynInvokeRunnable invokeRunnable = new SynInvokeRunnable(deviceRequest, timeout, timeunit);
            new Thread(invokeRunnable).start();
            invokeRunnable.waitting();
            logger.debug(">>>(debug)jniMposLibCmd_responeCode:" + invokeRunnable.jniCmdResultCode + ",deviceCmd:" + deviceRequest);
            logger.debug(">>>(debug)jniMposLibCmd_responseData:" + (invokeRunnable.jniResponseData == null ? "null" : InnerUtils.hexString(invokeRunnable.jniResponseData)) + ",deviceCmd:" + deviceRequest);
            DefaultJNICmdInvokeNotifyListener listener = new DefaultJNICmdInvokeNotifyListener(null, deviceRequest, invokeRunnable.jniReqPayload, invokeRunnable.jniResponseData);
            if (invokeRunnable.jniCmdResultCode < 0 && invokeRunnable.jniCmdResultCode != DEFALUT_TIMEOUT_CODE) {
                showErrorLog(invokeRunnable.jniCmdResultCode, invokeRunnable.jniReqPayload, invokeRunnable.jniResponseData);
                listener.notifyErrResp(invokeRunnable.jniResponseData, new DeviceRTException(ErrorCode.DEVICE_INVOKE_FAILED, "execute jniMposLibCmd falied"));
            } else if (invokeRunnable.jniResponseData == null || invokeRunnable.jniResponseData.length <= 0) {
                showErrorLog(invokeRunnable.jniCmdResultCode, invokeRunnable.jniReqPayload, invokeRunnable.jniResponseData);
                listener.notifyErrResp(invokeRunnable.jniResponseData, new DeviceRTException(ErrorCode.PROCESS_TIMEOUT, "execute jniMposLibCmd timeout"));
            } else {
                listener.notifyResponse();
            }
            return listener.response;
        } catch (Exception e) {
            return new ErrorResponse(e);
        }

    }

    class SynInvokeRunnable implements Runnable {
        private byte[] jniReqPayload;
        private DeviceCommand deviceRequest;
        volatile int jniCmdResultCode = DEFALUT_TIMEOUT_CODE;
        private byte[] jniResponse = new byte[8192];
        volatile byte[] jniResponseData;
        private Object syncObject = new Object();
        private long timeout;
        private TimeUnit timeUnit;
        private int[] jniRespLen = new int[1];
        ;

        public SynInvokeRunnable(DeviceCommand deviceRequest, long timeout, TimeUnit timeUnit) {
            this.deviceRequest = deviceRequest;
            this.timeout = timeout;
            this.timeUnit = timeUnit;
        }

        @Override
        public void run() {
            try {
                jniReqPayload = packager.packJNIReqData(deviceRequest);
                logger.debug(">>>(debug)execute command data:" + (null == jniReqPayload ? null : InnerUtils.hexString(jniReqPayload)) + ",deviceCmd:" + deviceRequest);
                jniCmdResultCode = JniCmdInterface.getInstance().jniMposLibCmd(jniReqPayload, jniReqPayload.length, jniResponse, jniRespLen);
                if (jniResponse != null) {
                    jniResponseData = new byte[jniRespLen[0]];
                    System.arraycopy(jniResponse, 0, jniResponseData, 0, jniRespLen[0]);
                }
            } catch (Exception e) {
                logger.error("Instruction execution exception[jniMposLibCmd],send data packet ：" + (null == jniReqPayload ? null : InnerUtils.hexString(jniReqPayload)));
                e.printStackTrace();
                synchronized (syncObject) {
                    syncObject.notify();
                }

            }
            synchronized (syncObject) {
                logger.debug(">>>SynInvokeRunnable notify");
                syncObject.notify();
            }
        }

        void waitting() {
            synchronized (syncObject) {
                try {
                    long timeMills = 4000;
                    logger.debug(">>>wait timeout:" + timeout);
                    if (timeout > 0) {
                        timeMills = timeUnit.toMillis(timeout);
                    }
                    syncObject.wait(timeMills);
                } catch (InterruptedException e) {
                    // todo
                }
            }
        }
    }

    /**
     * Sending commands to the JNI Library
     *
     * @author yjf
     */
    public class InvokingThread implements Runnable {
        String eventName = null;
        DeviceCommand cmd;
        long timeout = -1;
        TimeUnit timeunit = null;
        private byte[] respondData = new byte[4000];
        private int cmdResult = -1;
        private int[] len = new int[1];
        private byte[] cmdResponseValue = null;
        byte[] reqPayload = null;

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
                reqPayload = packager.packJNIReqData(cmd);
                logger.debug(">>>(debug,async)execute cmd-:" + cmd.toString() + ",command data:" + (null == reqPayload ? null : InnerUtils.hexString(reqPayload)));

                cmdResult = JniCmdInterface.getInstance().jniMposLibCmd(reqPayload, reqPayload.length, respondData, len, new CmdRspListener() {

                    @Override
                    public void callback(int arg0, byte[] arg1) {
                        logger.debug(">>>(debug,async) callback state" + arg0);
                        logger.debug(">>>(debug,async) callback data" + (arg1 == null ? "null" : InnerUtils.hexString(arg1)));
                        DefaultJNICmdInvokeNotifyListener listener = new DefaultJNICmdInvokeNotifyListener(eventName, cmd, reqPayload, arg1);
                        listener.notifyResponse();
                    }
                });
                if (respondData != null) {
                    cmdResponseValue = new byte[len[0]];
                    System.arraycopy(respondData, 0, cmdResponseValue, 0, len[0]);
                }
                logger.debug(">>>(debug,async) jniMposLibCmd_responeCode:" + cmdResult);
                logger.debug(">>>(debug,async) jniMposLibCmd result：" + eventName + ":" + (cmdResponseValue == null ? "null" : InnerUtils.hexString(cmdResponseValue)));
                DefaultJNICmdInvokeNotifyListener listener = new DefaultJNICmdInvokeNotifyListener(eventName, cmd, reqPayload, cmdResponseValue);
                if (cmdResult < 0) {
                    showErrorLog(cmdResult, reqPayload, cmdResponseValue);
                    listener.notifyErrResp(cmdResponseValue, new DeviceRTException(ErrorCode.DEVICE_INVOKE_FAILED, "execute jniMposLibCmd falied"));
                } else if (cmdResponseValue == null || cmdResponseValue.length <= 0) {
                    showErrorLog(cmdResult, reqPayload, cmdResponseValue);
                    listener.notifyErrResp(cmdResponseValue, new DeviceRTException(ErrorCode.PROCESS_TIMEOUT, "execute jniMposLibCmd timeout"));
                } else {
                    listener.notifyResponse();
                }

            } catch (Exception e) {
                e.printStackTrace();
                try {
                    DeviceInnerEventDispatcher.instance().dispatchEvent(new InvokeEvent(eventName, new ErrorResponse(e)));
                    DeviceInnerEventDispatcher.instance().removeEvent(eventName);
                } catch (Exception e1) {
                }
            }
        }

    }

    /**
     * Parsing the response message data returned by the JNI
     */
    private class DefaultJNICmdInvokeNotifyListener {
        private byte[] requestData;
        private byte[] respPayload;
        private DeviceCommand cmd;
        private DeviceResponse response;
        private String eventName;

        public DefaultJNICmdInvokeNotifyListener(String eventName, DeviceCommand cmd, byte[] requestData, byte[] respPayload) {
            this.cmd = cmd;
            this.respPayload = respPayload;
            this.eventName = eventName;
            this.requestData = requestData;
        }

        public void notifyResponse() {
            if (respPayload == null || respPayload.length <= 0) {
                notifyErrResp(respPayload, new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED, "response body should not be null or length == 0!"));
            }

            packager.unpackJNIRespData(cmd, requestData, respPayload, new ResponseUnpackListener() {
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
            errMsg +=InnerUtils.hexString(respPayload);
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

    private void registerAbortOperator(DeviceCommand command) {
        if (command instanceof AbortableDeviceCommand) {
            ((AbortableDeviceCommand) command).setOutAbortController(new Abortable() {
                @Override
                public void abort() {
                    cancelCurrentExecCmd();
                }

                @Override
                public void abort(int keyCode) {
                    logger.debug(">>>cancel ExecCmd keyCode=" + keyCode);
                    JniCmdInterface.getInstance().jniMposLibCmdCancel(keyCode);
                }
            });
        }
    }

    @Override
    public DeviceResponse directInvoke(DeviceCommand deviceCommand) throws IOException, InterruptedException {
        throw new UnsupportedOperationException("unsupport directInvoke operation");
    }

    @Override
    public void cancelCurrentExecCmd() {
        logger.debug(">>>cancelCurrentExecCmd4");
        JniCmdInterface.getInstance().jniMposLibCmdCancel(0x04);
    }

    @Override
    public void destroy() {
        //myJNI = null;
    }

    @Override
    public boolean isAlive() {
        return true;
    }

    @Override
    public boolean isBusy() {
        return false;
    }


    @Override
    public InnerDeviceConnState getDeviceConnectionState() {
        return InnerDeviceConnState.PREPARED;
    }

    @Override
    public void beginTransaction(long timeout, TimeUnit timeUnit) throws OpenTrasactionException {
        JniCmdInterface.getInstance().Ndk_beginTransactions((int) timeUnit.toMillis(timeout));
    }

    @Override
    public void endTransaction() throws OpenTrasactionException {
        JniCmdInterface.getInstance().Ndk_endTransactions();
    }

    @Override
    public TransactionStatus getTransactionStatus() {
        if (JniCmdInterface.getInstance().Ndk_getStatus() == -4002) {
            return TransactionStatus.HOLD;
        } else {
            return TransactionStatus.NOT_IN;
        }
    }

    @Override
    public Context getContext() {
        return context;
    }

    private void showErrorLog(int resultCode, byte[] requestData, byte[] responseData) {
        logger.error(">>>[jniMposLibCmd error] responeCode:" + resultCode);
        logger.error(">>>[jniMposLibCmd error] requestData:" + (null == requestData ? null : InnerUtils.hexString(requestData)));
        logger.error(">>>[jniMposLibCmd error] responseData:" + (responseData == null ? null : InnerUtils.hexString(responseData)));
    }
}
