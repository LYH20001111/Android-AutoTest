package com.newland.nsdk.core.common.uart3;


import android.os.Build;
import android.util.Log;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.serialport.BaudRate;
import com.newland.nsdk.core.api.common.uart3.UART3Config;
import com.newland.nsdk.core.api.common.uart3.UART3Type;
import com.newland.nsdk.core.api.common.utils.LogUtils;

import java.util.Arrays;

public class UART3Manager {
    private final int JNI_PORT_RS232 = 1;
    private final int JNI_PORT_PINPAD = 2;
    private final int JNI_PORT_RS232_A = 5;
    private final int JNI_PORT_RS232_B = 6;
    private UART3Type type;
    private SerialPortJni serialPortJni = SerialPortJni.getInstance();
    private int portType = 0;
    private int fd = -1;

    public UART3Manager(UART3Type type) {

        if (type == UART3Type.RS232_CPOS) {
            portType = JNI_PORT_PINPAD;
        } else if (type == UART3Type.RS232_A7){
            if (Build.MODEL.equalsIgnoreCase("U2000")) {
                portType = JNI_PORT_RS232_A;
            } else {
                portType = JNI_PORT_RS232;
            }
        } else if (type == UART3Type.RS232B) {
            portType = JNI_PORT_RS232_B;
        } else {
            portType = JNI_PORT_RS232;
        }

        LogUtils.e("UART3Manager", "type:" + type);
    }

    public void open(UART3Config config) throws NSDKException {
        StringBuilder sb = new StringBuilder();
        sb.append(getDataBits(config));
        sb.append(getParityBit(config));
        sb.append(getStopBits(config));
        // 红外通讯防止反射串扰（自发自收防护功能）
        sb.append('N');
        /**
         * 设置串口是否阻塞，Y 阻塞，N 非阻塞；在实际的测试过程中发现
         * CPOS终端上，Y无法接收数据并且会导致设备节点卡住
         */
        sb.append(getIsBlocked(config));
        int baudRate = config.getBaudRate() == null ? BaudRate.BPS115200.toValue() : config.getBaudRate().toValue();
        LogUtils.e(getClass().getName(), "baudRate:" + baudRate);

        fd = serialPortJni.portOpen(portType, baudRate, sb.toString().getBytes());
        if (fd < 0) {
            throw new NSDKException("Failed to open");
        }
    }

    public synchronized byte[] read(int maxLen, int timeout) throws NSDKException {
        if (this.fd < 0) {
            throw new NSDKException(ErrorCode.ERROR, "Please open first.");
        }

        byte[] data = new byte[maxLen];
        LogUtils.d(getClass().getName(), ">> timeout:"+timeout);
        int ret = serialPortJni.portRead(this.fd, data, maxLen, timeout);
        LogUtils.d(getClass().getName(), ">> portRead:" + ret);
        if (ret > 0) {
            return Arrays.copyOf(data, ret);
        }

        if (ret == -2) {
            throw new NSDKException(ErrorCode.ERROR, String.format("fd error(%d).", ret));
        }

        return null;
    }

    public synchronized void write(byte[] data, int maxLen, int timeout) throws NSDKException {
        if (this.fd < 0) {
            throw new NSDKException(ErrorCode.ERROR, "Please open first.");
        }

        int ret = serialPortJni.portWrite(this.fd, data, maxLen, timeout);
        if (ret < 0) {
            throw new NSDKException(ErrorCode.ERROR, String.format("Fail to write(%d).", ret));
        }
    }

    public synchronized void close( UART3Type type) throws NSDKException {
        LogUtils.d("Test", "***** fd: " + this.fd);
        if (this.fd < 0) {
            return;
        }
        int ret = serialPortJni.portClose(this.fd);
        if (ret < 0) {
            throw new NSDKException(ErrorCode.ERROR, String.format("Fail to close(%d).", ret));
        }

        this.fd = -1;
    }

    public void flush() throws NSDKException {
        serialPortJni.portClearBuf(this.fd, JNI_PORT_PINPAD);
    }

    private String getDataBits(UART3Config config) {
        if (config == null || config.getDataBits() == null) {
            return "8";
        }
        switch (config.getDataBits()) {
            case DATA_BIT_5:
                return "5";
            case DATA_BIT_6:
                return "6";
            case DATA_BIT_7:
                return "7";
            default:
                return "8";
        }
    }

    private String getStopBits(UART3Config config) {
        if (config == null || config.getStopBits() == null) {
            return "1";
        }
        switch (config.getStopBits()) {
            case STOP_BIT_TWO:
                return "2";
            default:
                return "1";
        }
    }

    private String getParityBit(UART3Config config) {
        if (config == null || config.getParityBit() == null) {
            return "N";
        }
        switch (config.getParityBit()) {
            case ODD_CHECK:
                return "O";
            case EVEN_CHECK:
                return "E";
            default:
                return "N";
        }
    }

    private String getIsBlocked(UART3Config config) {
        if (config == null) {
            return "N";
        }
        if (config.getIsBlocked()) {
            return "Y";
        }
        return "N";
    }
}
