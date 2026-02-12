package com.newland.nsdk.core.common.uart3;

import android.os.Build;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.serialport.BaudRate;
import com.newland.nsdk.core.api.common.serialport.SerialPortSettings;
import com.newland.nsdk.core.api.common.uart3.UART3Config;
import com.newland.nsdk.core.api.common.utils.LogUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;

public class NDKPortManager {
    // 除 N950S ECR 外只能用 COM2（值为 1），COM1 是安卓和安全模块的通信串口，不让用
    private static final int COM1 = 0;
    private static final int COM2 = 1;
    private int port = COM2;
    private NDKPortManager ndkPortManagerHolder;

    private NDKPortManager() {
    }

    public static NDKPortManager getInstance() {
        return NDKPortManagerHolder.singletonHolder;
    }

    private static class NDKPortManagerHolder {
        private static NDKPortManager singletonHolder = new NDKPortManager();
    }

    public void open(SerialPortSettings config) throws NSDKException {
        if (Build.MODEL.contains("N950S ECR") || Build.MODEL.contains("N950S-C")) {
            port = COM1;
        }
        String baudRate = getBaudRate(config);
        String dataBits = getDataBits(config);
        String parityBit = getParityBit(config);
        String stopBits = getStopBits(config);

        String configStr = String.format("%s,%s,%s,%s", baudRate, dataBits, parityBit, stopBits);
        LogUtils.e(getClass().getName(), "open config:"+configStr);
        int ret = SerialPortJni.getInstance().portNDKOpen(port, configStr);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format("Failed to open COM2(%d)", ret));
        }
    }

    public void close() throws NSDKException {
        int ret = SerialPortJni.getInstance().portNDKClose(port);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format("Failed to close COM2(%d)", ret));
        }
    }

    public byte[] read(int maxLen, int timeout) throws NSDKException {
        byte[] outData = new byte[maxLen];
        int[] outDataLen = new int[1];

        if(timeout < 0){
            timeout = 0;
        }

        int ret = SerialPortJni.getInstance().portNDKRead(port, maxLen, timeout, outData, outDataLen);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        /**
         * NDK串口数据小于maxlen时，会返回-10，并返回实际接收到的数据
         */
        if (ret != ErrorCode.OK && ret != ErrorCode.TIMEOUT) {
            throw new NSDKException(ret, String.format("Failed to read data(%d)", ret));
        }

        if (outDataLen[0] <= 0) {
            return null;
        }

        return Arrays.copyOf(outData, outDataLen[0]);
    }

    public void write(byte[] data) throws NSDKException {
        if (data == null || data.length == 0) {
            throw new NSDKIllegalParameterException("No data to write.");
        }

        int ret = SerialPortJni.getInstance().portNDKWrite(port, data.length, data);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format("Failed to write data(%d)", ret));
        }
    }

    public int readBufLen() throws NSDKException {
        int[] len = new int[1];
        int ret = SerialPortJni.getInstance().portNDKReadLen(port, len);
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to read buf length(%d)", ret));
        }
        int readLen = 0;
        if (len[0] > 0) {
            readLen = len[0];
        }
        return readLen;
    }

    public void clearBuf() throws NSDKException {
        int ret = SerialPortJni.getInstance().portNDKClrBuf(port);
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format("Failed to clear buf(%d)", ret));
        }
    }

    private String getBaudRate(SerialPortSettings config) {
        if (config == null || config.getBaudRate() == null) {
            return String.valueOf(BaudRate.BPS115200.toValue());
        }

        return String.valueOf(config.getBaudRate().toValue());
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
}
