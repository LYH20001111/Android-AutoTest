package com.newland.sdk.mtypex.conn;

import android.util.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.newland.sdk.mtype.DeviceOutofLineException;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.Dump;
import com.newland.sdk.mtype.util.SimIdGenerator;
import com.newland.sdk.mtypex.cmd.CommandSerializer;
import com.newland.sdk.mtypex.cmd.DeviceCommand;
import com.newland.sdk.mtypex.cmd.DeviceResponse;
import com.newland.sdk.mtypex.cmd.ErrorResponse;
import com.newland.sdk.mtypex.cmd.packager.ProtocalPackagerReader;
import com.newland.sdk.mtypex.cmd.packager.ReadResponseListener;
import com.newland.sdk.mtypex.cmd.packager.ResponseUnpackListener;

/**
 * Standard full duplex connection implementation<p>
 *
 * @since ver3.10.01
 */
public abstract class AbstractDuplexDeviceConnection extends AbstractDeviceConnection implements ProtocalPackagerReader {

    private Map<Integer, DataFramePackage> requestDataFrameMapping = Collections.synchronizedMap(new HashMap<Integer, DataFramePackage>());

    private SimIdGenerator simIdGenerator = new SimIdGenerator(0xFE);

    private static final Object idGenSync = new Object();

    private static DeviceLogger logger = DeviceLoggerFactory.getLogger(AbstractDuplexDeviceConnection.class);

    private static final long MAX_SEND_WAITING = 120 * 1000;

    private Boolean isClosed = false;

    private final OutputReader outputReader;

    private final DataFrameTimeoutCheck timeoutCheck;

    private boolean isCompleteProcessing = false;
	private byte[] mReceiveData = null;
	private Object mCompleteModeObj = new Object();

    private class DataFrameTimeoutCheck extends Thread {
        public void run() {
            Long current = 0L;
            while (!isInterrupted() && !isClosed()) {
                if (requestDataFrameMapping != null) {
                    try {
                        current = System.currentTimeMillis();

                        synchronized (requestDataFrameMapping) {
                            Set<Entry<Integer, DataFramePackage>> entrys = requestDataFrameMapping.entrySet();
                            for (Iterator<Entry<Integer, DataFramePackage>> itor = entrys.iterator(); itor.hasNext(); ) {
                                Entry<Integer, DataFramePackage> entry = itor.next();
                                DataFramePackage pack = null;
                                if ((pack = entry.getValue()) == null) {
                                    itor.remove();
                                    continue;
                                }
                                long expected = current - pack.timeout;
                                if (pack.timestamp < expected) {
                                    if (logger.isDebugEnabled()) {
                                        Date now = new Date(current);
                                        Date pre = new Date(pack.timestamp);
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
                                        logger.debug("notify a timeout cmd!0x" + Integer.toString(pack.serialId, 16) + ",[timeout:" + pack.timeout + ",current:" + sdf.format(now) + ",pre:" + sdf.format(pre) + "];");
                                    }
                                    pack.notifyResult(false, null);
                                    itor.remove();
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.warn("data frame timeout check meeting error!", e);
                    }
                    try {
                        Thread.sleep(120L);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        }
    }

    public AbstractDuplexDeviceConnection(CommandSerializer serializer) {
        super(serializer);
        outputReader = new OutputReader();
        timeoutCheck = new DataFrameTimeoutCheck();
    }

    private byte[] waitCompleteModeResp(long timeOutMs) {
        synchronized (mCompleteModeObj) {
            try {
                mReceiveData = null;
                isCompleteProcessing = true;
                mCompleteModeObj.wait(timeOutMs);
                return mReceiveData;
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                isCompleteProcessing = false;
            }
            return null;
        }
    }

    private void notifyCompleteModeResp(byte[] receive) {
        synchronized (mCompleteModeObj) {
            mReceiveData = receive;
            mCompleteModeObj.notify();
        }
    }

    @Override
    public DeviceResponse send(DeviceCommand deviceCmd, long timeout) throws IOException, InterruptedException {
        return send(deviceCmd, null, timeout);
    }

    @Override
    public DeviceResponse send(DeviceCommand deviceCmd, InvokeStateNotifyListener eventListener, long timeout) throws IOException, InterruptedException {
        if (deviceCmd instanceof DeviceCommandComplete) {
            byte[] request = ((DeviceCommandComplete) deviceCmd).getSendData();
            synchronized (this) {
                write(request);
            }
            if (logger.isDebugEnabled())
                logger.debug("CommandComplete mode send=" + (request == null ? "null" : Dump.getHexDump(request)));
            byte[] receive = waitCompleteModeResp(timeout);
            if (logger.isDebugEnabled())
                logger.debug("CommandComplete mode receive=" + (receive == null ? "null" : Dump.getHexDump(receive)));
            return new DeviceResponseComplete(receive);
        } else if (deviceCmd instanceof DeviceCommandWrite) {
            byte[] request = ((DeviceCommandWrite) deviceCmd).getSendData();
            synchronized (this) {
                write(request);
            }
            if (logger.isDebugEnabled())
                logger.debug("DeviceCommandWrite mode send=" + (request == null ? "null" : Dump.getHexDump(request)));
            return new DeviceResponseWrite(new byte[]{});
        }else {
            DataFramePackage frame = new DataFramePackage(deviceCmd, eventListener, timeout);
            requestDataFrameMapping.put(frame.serialId, frame);
            byte[] payload = null;
            try {
                payload = frame.pack();
            } catch (Exception e) {
                return new ErrorResponse(e);
            }
            if (logger.isDebugEnabled())
                logger.debug("send request[" + deviceCmd.getClass() + "], full package:" + Dump.getHexDump(payload));
            synchronized (this) {//确保写数据串行发生
                write(payload);
            }
            return frame.waitResult();
        }
    }

    private class OutputReader extends Thread {

        public OutputReader() {
        }

        @Override
        public void run() {
            try {
                while (!isInterrupted() && !isClosed()) {
                    packager.readResponseFrom(AbstractDuplexDeviceConnection.this, new ReadResponseListener() {
                        @Override
                        public void processRslt(byte[] serial, byte[] body) {
                            AbstractDuplexDeviceConnection.this.processRslt(serial, body);
                        }

                        @Override
                        public void notifyDirectMessage(byte[] cmdCode, byte[] body) {
                            AbstractDuplexDeviceConnection.this.notifyDirectMessage(cmdCode, body);

                        }

						@Override
						public boolean processRslt(byte[] recvData) {
                            logger.debug("output reader processRslt isCompleteProcessing="+isCompleteProcessing);
                            if(isCompleteProcessing){
                                notifyCompleteModeResp(recvData);
                                return true;
                            }
                            return false;

						}
                    });
                }
            } catch (InterruptedException e) {
                logger.warn("output reader meet interrupt event!", e);
            } catch (DeviceOutofLineException e) {
                logger.warn("output reader meet device disconnected event!", e);
            } catch (Exception e) {
                logger.error("output reader meet fatal exception! connection should be closed!", e);
            } finally {
                if (logger.isDebugEnabled())
                    logger.debug("OutputReader finished:close[" + isClosed() + "],interrupt:[" + isInterrupted() + "]");
                close0(true);
            }
        }
    }


    public void processRslt(byte[] serial, byte[] body) {
        final int reqSerial = ((int) serial[0] & 0xff) - 1;
        synchronized (requestDataFrameMapping) {
            final DataFramePackage framePackage = requestDataFrameMapping.get(reqSerial);
            if (framePackage != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("notify request:" + reqSerial + ",data[" + Dump.getHexDump(body) + "]");

                }
                packager.unpack(framePackage.cmd, body, new ResponseUnpackListener() {
                    @Override
                    public void unpackFinished(boolean isNotifyResponse, DeviceResponse response) {
                        framePackage.notifyResult(isNotifyResponse, response);
                        if (!isNotifyResponse)
                            requestDataFrameMapping.remove(reqSerial);
                    }
                });
            } else {
                if (logger.isDebugEnabled())
                    logger.debug("dataFramePackage not found!by serial:" + reqSerial);
            }
        }
    }

    private class DataFramePackage {

        private int serialId;

        private DeviceCommand cmd;

        private DeviceResponse response;

        private volatile boolean requestFinished = false;

        private Long timestamp;

        private Long timeout;

        private InvokeStateNotifyListener eventListener;

        /**
         * Perform synchronizer
         */
        private final Object invokeSync = new Object();

        private DataFramePackage(DeviceCommand cmd, InvokeStateNotifyListener eventListener, Long timeout) {
            this.timestamp = System.currentTimeMillis();
            this.eventListener = eventListener;
            serialId = simIdGenerator.getId(idGenSync, 2).intValue();
            this.cmd = cmd;
            if (timeout > 0) {
                this.timeout = timeout;
            } else {
                this.timeout = MAX_SEND_WAITING;
            }
        }

        public byte[] pack() {
            return packager.pack(serialId, cmd);
        }


        public DeviceResponse waitResult() throws InterruptedException {
            synchronized (invokeSync) {
                if (logger.isDebugEnabled())
                    logger.debug("start waiting!!thread:" + invokeSync + ",serialId:" + serialId);
                //超时事件单位应该为 mills
                invokeSync.wait(MAX_SEND_WAITING);
                if (logger.isDebugEnabled())
                    logger.debug("notify finished!thread:" + invokeSync + ",serialId:" + serialId + " finished!");
                return response;
            }
        }

        public void notifyResult(boolean isNotifyResponse, DeviceResponse response) {
            synchronized (invokeSync) {
                if (!requestFinished) {
                    if (!isNotifyResponse) {//如果不是通知类的响应
                        if (response != null)
                            this.response = response;
                        if (logger.isDebugEnabled())
                            logger.debug("notify thread:" + invokeSync + ",serialId:" + serialId);
                        invokeSync.notify();
                        requestFinished = true;
                    } else {
                        eventListener.notify(response);
                    }
                }
            }
        }

    }


    private void close0(boolean isOutputReaderFinished) {
        if (logger.isDebugEnabled())
            logger.debug("receiving conn close signal!");
        boolean toDoClose = false;
        synchronized (isClosed) {
            if (!isClosed) {
                isClosed = true;
                toDoClose = true;
            }
        }
        if (toDoClose) {
            if (logger.isDebugEnabled())
                logger.debug("start to close connection!");
            if (!outputReader.isInterrupted() && !isOutputReaderFinished) {
                outputReader.interrupt();
                try {
                    outputReader.join(300);
                } catch (InterruptedException e) {
                }
            }
            try {
                Map<Integer, DataFramePackage> temp = null;
                synchronized (requestDataFrameMapping) {
                    temp = requestDataFrameMapping;
                    if (requestDataFrameMapping != null) {
                        requestDataFrameMapping = null;
                    }
                }
                synchronized (temp) {
                    if (temp != null) {
                        for (Integer serialId : temp.keySet()) {
                            DataFramePackage pack = temp.get(serialId);
                            if (pack != null) {
                                pack.notifyResult(false, null);
                            }
                        }
                    }
                }
                temp.clear();
                timeoutCheck.interrupt();
            } catch (Exception e) {
                logger.warn("close connection meet error!", e);
            } finally {
                try {
                    implClose();
                } catch (Exception e) {
                    logger.warn("close implClose meet error!", e);
                }
            }
        }
    }

    public void close() {
        if (logger.isDebugEnabled())
            logger.debug("someone try to close connection!");
        close0(false);
    }

    protected void serviceStart() {
        outputReader.start();
        timeoutCheck.start();
    }

    public boolean isClosed() {
        return isClosed;
    }


    /**
     * Requires the bottom layer to implement a channel closure<p>
     *
     * @since ver3.10.01
     */
    protected abstract void implClose();

    /**
     * Write data
     *
     * @param buffer
     * @throws IOException
     * @since ver3.10.01
     */
    public abstract void write(byte[] buffer) throws IOException;

}
