package com.newland.nsdk.core.internal.serialportmanager;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.serialport.BaudRate;
import com.newland.nsdk.core.api.common.serialport.SerialPortSettings;
import com.newland.nsdk.core.api.common.uart3.UART3Config;
import com.newland.nsdk.core.api.common.uart3.UART3Type;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.internal.serialportmanager.SerialPortType;
import com.newland.nsdk.core.common.uart3.SerialPortJni;

import java.util.Arrays;
import java.util.Locale;

public class RS232Manager {
    private final int JNI_PORT_RS232 = 1;
    private final int JNI_PORT_PINPAD = 2;
    private final int JNI_PORT_U2000_A = 5;
    private final int JNI_PORT_U2000_B = 6;
    private final int JNI_PORT_CPOS_A14_RS232 = 7;
    private UART3Type type;
    private String nodeName;
    private SerialPortJni serialPortJni = SerialPortJni.getInstance();
    private int portType = 0;
    private int fd = -1;

    public RS232Manager(SerialPortType type) {
        if (type == SerialPortType.PINPAD) {
            if (Build.VERSION.SDK_INT >= 33) {
                portType = JNI_PORT_CPOS_A14_RS232;
            } else {
                portType = JNI_PORT_RS232;
            }
        } else if (type == SerialPortType.RS232){
            if (Build.MODEL.equalsIgnoreCase("U2000")) {
                portType = JNI_PORT_U2000_A;
            } else if (Build.MODEL.equalsIgnoreCase("U200")) {
                portType = JNI_PORT_U2000_B;
            } else if (Build.MODEL.contains("CPOS")){
                portType = JNI_PORT_PINPAD;
            } else if (Build.MODEL.contains("N950S-C")) {
                portType = JNI_PORT_U2000_B;
            } else {
                portType = JNI_PORT_RS232;
            }

        } else if (type == SerialPortType.RS232B){
            portType = JNI_PORT_U2000_B;
        }
    }

    public RS232Manager(String nodeName) {
        this.nodeName = nodeName;
    }

    public void open(SerialPortSettings config) throws NSDKException {
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
        sb.append(getIsBlock(config));
        sb.append(getIsFlowCtrl(config));
        int baudRate = config.getBaudRate() == null ? BaudRate.BPS115200.toValue() : config.getBaudRate().toValue();
//        LogUtils.e(getClass().getName(), "baudRate:" + baudRate);
//        LogUtils.e(getClass().getName(), "baudRate:" + baudRate + "\nDataBits:" + config.getDataBits().name() + "\nParityBit:" + config.getParityBit().name() + "\nStopBits:" + config.getStopBits().name() + "\nisBlocked:" + config.getIsBlocked());
        if (!TextUtils.isEmpty(nodeName)) {
            fd = serialPortJni.portOpenWithNodeName(nodeName, baudRate, sb.toString().getBytes());
        } else {
            Log.d("RS232Manager", "portType:" + portType);
            fd = serialPortJni.portOpen(portType, baudRate, sb.toString().getBytes());
        }

        if (fd < 0) {
            throw new NSDKException("Failed to open");
        }
    }

    public synchronized byte[] read(int maxLen, int timeout) throws NSDKException {
        if (this.fd < 0) {
            throw new NSDKException(ErrorCode.ERROR, "Please open first.");
        }
        if (maxLen < 0 || maxLen > 2048) {
            throw new NSDKIllegalParameterException("Max length shall be range from 0 to 2048.");
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

    public synchronized int write(byte[] data, int maxLen, int timeout) throws NSDKException {
        if (this.fd < 0) {
            throw new NSDKException(ErrorCode.ERROR, "Please open first.");
        }
        if (data == null || data.length == 0) {
            throw new NSDKIllegalParameterException("Data shall not be null.");
        }
        if (data.length > 2048) {
            throw new NSDKIllegalParameterException("Data length shall be range from 1 to 2048.");
        }
        if (timeout <= 0) {
            timeout = 0;
        }

        int ret = serialPortJni.portWrite(this.fd, data, maxLen, timeout);
//        if (ret < 0) {
//            throw new NSDKException(ErrorCode.ERROR, String.format("Fail to write(%d).", ret));
//        }
        return ret;
    }

    public synchronized void close() throws NSDKException {
        LogUtils.d("RS232Manager", "***** fd: " + this.fd);
        if (this.fd < 0) {
            return;
        }
        int ret = serialPortJni.portClose(this.fd);
        if (ret < 0) {
            throw new NSDKException(ErrorCode.ERROR, String.format("Fail to close(%d).", ret));
        }

        this.fd = -1;
    }

    public synchronized int ioctl(int cmd, byte[] args) throws NSDKException {
        if (this.fd < 0) {
             throw new NSDKException("Please open first.");
        }
        return serialPortJni.portIOCTL(this.fd, cmd, args);
    }

    public int readLen() throws NSDKException {
        if (this.fd < 0) {
            throw new NSDKException("Please open first.");
        }
        int[] len = new int[1];
        int ret = SerialPortJni.getInstance().portReadLen(fd, len);
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to read buf length(%d)", ret));
        }
        return len[0];
    }

    public void flush() throws NSDKException {
        if (this.fd < 0) {
            throw new NSDKException("Please open first");
        }
        serialPortJni.portClearBuf(this.fd, 2);
    }

    private String getDataBits(SerialPortSettings config) {
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

    private String getStopBits(SerialPortSettings config) {
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

    private String getParityBit(SerialPortSettings config) {
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

    private String getIsBlock(SerialPortSettings config) {
        if (config == null) {
            return "N";
        }
        if (config.getIsBlocked()) {
            return "Y";
        }
        return "N";
    }

    private String getIsFlowCtrl(SerialPortSettings config) {
        if (config == null) {
            return "N";
        }
        if (config.isFlowCtr()) {
            return "Y";
        }
        return "N";
    }
}
