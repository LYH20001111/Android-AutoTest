package com.newland.nsdk.core.common.uart3;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.serialport.BaudRate;
import com.newland.nsdk.core.api.common.serialport.DataBits;
import com.newland.nsdk.core.api.common.serialport.ParityBit;
import com.newland.nsdk.core.api.common.serialport.StopBits;
import com.newland.nsdk.core.api.common.uart3.UART3Port;
import com.newland.nsdk.core.api.common.uart3.UART3Config;
import com.newland.nsdk.core.api.common.uart3.UART3Type;
import com.newland.nsdk.core.api.common.utils.LogUtils;

public class UART3PortImpl implements UART3Port {
    private UART3Type type;
    private UART3Config config;
    private NDKPortManager ndkPortManager;
    private UART3Manager uart3Manager;
    private boolean isNDKPort;
    private final int TIMEOUT = 50;

    public UART3PortImpl(UART3Type type) {
        this.type = type;

        if (isNDKPort()) {
            isNDKPort = true;
            ndkPortManager = NDKPortManager.getInstance();
        } else {
            isNDKPort = false;
            uart3Manager = new UART3Manager(type);
        }
    }

    @Override
    public void open() throws NSDKException {
        if (config == null) {
            this.config = new UART3Config(BaudRate.BPS115200, DataBits.DATA_BIT_8, ParityBit.NO_CHECK, StopBits.STOP_BIT_ONE);
        }

        if (isNDKPort) {
            ndkPortManager.open(config);
        } else {
            uart3Manager.open(config);
        }
    }

    @Override
    public void setConfig(UART3Config uart3Config) {
        if (uart3Config == null) {
            this.config = new UART3Config(BaudRate.BPS115200, DataBits.DATA_BIT_8, ParityBit.NO_CHECK, StopBits.STOP_BIT_ONE);
        } else {
            this.config = uart3Config;
        }
    }

    @Override
    public byte[] read(int maxLen, int timeout) throws NSDKException {
        if (maxLen <= 0) {
            throw new NSDKIllegalParameterException("Max len shall be >0");
        }

        if (timeout < 0) {
            timeout = 0;
        }
        if (isNDKPort) {
            return ndkPortManager.read(maxLen, timeout);
        } else {
            return uart3Manager.read(maxLen, timeout);
        }


    }

    @Override
    public void write(byte[] data, int maxLen, int timeout) throws NSDKException {
        if (maxLen <= 0) {
            throw new NSDKIllegalParameterException("Max len shall be >0");
        }

        if (data == null || data.length == 0) {
            throw new NSDKIllegalParameterException("No data to write.");
        }

        if (timeout < 0) {
            timeout = 0;
        }

        if (isNDKPort) {
            ndkPortManager.write(data);
        } else {
            uart3Manager.write(data, maxLen, timeout);
        }
    }

    @Override
    public void close() throws NSDKException {
        if (isNDKPort) {
            ndkPortManager.close();
        } else {
            uart3Manager.close(type);
        }
    }

    @Override
    public void flush() throws NSDKException {
        if (isNDKPort) {
            ndkPortManager.clearBuf();
        } else {
            uart3Manager.flush();
        }
    }

    private boolean isNDKPort() {
        if (this.type.getCode() == -1) {
            return true;
        }

        return false;
    }
}
