package com.newland.nsdk.core.external.command.communication.uart;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.uart3.UART3Config;
import com.newland.nsdk.core.api.common.uart3.UART3Type;
import com.newland.nsdk.core.api.external.communication.CommunicatorListener;
import com.newland.nsdk.core.api.external.communication.ExternalCommunicatorState;
import com.newland.nsdk.core.api.external.communication.NSDKCommunicator;
import com.newland.nsdk.core.api.common.exception.NSDKCommunicationException;
import com.newland.nsdk.core.common.uart3.UART3PortImpl;
import com.newland.nsdk.core.external.command.communication.CommunicatorExtension;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.exception.ExternalErrorMessage;

/**
 * Provides methods to open ,close uart serial port, and interact with external pinpad
 *
 * @author hlh
 * @date 2021/4/21
 */
public class UART3PortCommunicator implements NSDKCommunicator, CommunicatorExtension {
    private static final String TAG = "SerialPort";
    private static UART3PortCommunicator serialPort;

    private final int MAX_SIZE = 512;
    static volatile int fd = -1;
    private volatile boolean isConnected = false;

    private ExternalCommunicatorState state;
    private CommunicatorListener listener;
    private UART3PortImpl uart3Port;
    private UART3Config config;
    private UART3Type mType;

    private UART3PortCommunicator(CommunicatorListener listener) {
        this.listener = listener;
    }

    public static UART3PortCommunicator getInstance(CommunicatorListener listener) {
        if (serialPort == null) {
            synchronized (UART3PortCommunicator.class) {
                if (serialPort == null) {
                    serialPort = new UART3PortCommunicator(listener);
                }
            }
        } else {
            if (listener != serialPort.listener) {
                serialPort = new UART3PortCommunicator(listener);
            }
        }
        return serialPort;
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }

    public void setUART3Config(UART3Type type, UART3Config config) throws NSDKException {
        if (!isConnected) {
            uart3Port = new UART3PortImpl(type);
            uart3Port.setConfig(config);
            mType = type;
        }

        this.config = config;
    }

    @Override
    public void open(int timeout) throws NSDKException {
        if (uart3Port == null) {
            throw new NSDKIllegalParameterException("Call the method of setUARTConfig before this method!");
        }

        if (isConnected) {
            return;
        }
        state = ExternalCommunicatorState.CONNECTTING;
        listener.onConnectedStateChange(ExternalCommunicatorState.CONNECTTING);

        uart3Port.open();

        isConnected = true;
        state = ExternalCommunicatorState.CONNECTED;
        listener.onConnectedStateChange(ExternalCommunicatorState.CONNECTED);

    }

    @Override
    public void close(int timeout) throws NSDKException {
        if (!isConnected) {
            return;
        }

        state = ExternalCommunicatorState.DISCONNECTTING;
        listener.onConnectedStateChange(ExternalCommunicatorState.DISCONNECTTING);
        uart3Port.close();

        state = ExternalCommunicatorState.DISCONNECTED;
        listener.onConnectedStateChange(ExternalCommunicatorState.DISCONNECTED);
        isConnected = false;
    }

    @Override
    public synchronized void send(byte[] data, int timeout) throws NSDKException {
        if (state == ExternalCommunicatorState.DISCONNECTED || state == ExternalCommunicatorState.DISCONNECTED) {
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_SEND_ERROR, ExternalErrorMessage.FAILED_TO_SEND_DATA);
        }

        uart3Port.write(data, data.length, timeout);
    }

    @Override
    public synchronized byte[] receive(int timeout) throws NSDKException {
        if (state == ExternalCommunicatorState.DISCONNECTED || state == ExternalCommunicatorState.DISCONNECTED) {
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_SEND_ERROR, ExternalErrorMessage.FAILED_TO_SEND_DATA);
        }

        try {
            byte[] buf = uart3Port.read(MAX_SIZE, timeout);
            if (buf != null) {
                return buf;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void clear() throws NSDKException {
        if (state == ExternalCommunicatorState.DISCONNECTED || state == ExternalCommunicatorState.DISCONNECTED) {
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_SEND_ERROR, ExternalErrorMessage.FAILED_TO_SEND_DATA);
        }
        try {
            uart3Port.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setCommunicationTimeout(int sendTimeout, int receiveTimeout) {
        ExternalCommunicationManager.getInstance().setSendTimeout(sendTimeout);
        ExternalCommunicationManager.getInstance().setReceiveTimeout(receiveTimeout);
    }

    @Override
    public void sendInterrupt(byte[] data, int timeout) throws NSDKException {
        if (state == ExternalCommunicatorState.DISCONNECTED || state == ExternalCommunicatorState.DISCONNECTED) {
            throw new NSDKCommunicationException(ErrorCode.EXT_COMMUNICATION_SEND_ERROR, ExternalErrorMessage.FAILED_TO_SEND_DATA);
        }

        uart3Port.write(data, data.length, timeout);
    }
}
