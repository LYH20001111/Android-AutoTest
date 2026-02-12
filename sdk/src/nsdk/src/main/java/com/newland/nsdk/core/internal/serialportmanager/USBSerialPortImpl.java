package com.newland.nsdk.core.internal.serialportmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.newland.content.NlContext;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.serialport.BaudRate;
import com.newland.nsdk.core.api.common.serialport.DataBits;
import com.newland.nsdk.core.api.common.serialport.ParityBit;
import com.newland.nsdk.core.api.common.serialport.SerialPortSettings;
import com.newland.nsdk.core.api.common.serialport.StopBits;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.internal.analogserial.AnalogSerialManager;
import com.newland.nsdk.core.api.internal.devicemanager.RadarGain;
import com.newland.nsdk.core.api.internal.serialportmanager.SerialPortType;
import com.newland.nsdk.core.api.internal.serialportmanager.USBSerialPort;
import com.newland.nsdk.core.internal.analogserial.AnalogSerialManagerImpl;

public class USBSerialPortImpl implements USBSerialPort {
    private SerialPortSettings serialPortSettings;
    private Context mContext;
    private AnalogSerialManager analogSerialManager;
    private android.newland.AnalogSerialManager mAnalogSerialManager;
    private SerialPortType type;
    private boolean isOpen = false;
    @SuppressLint("WrongConstant")
    public USBSerialPortImpl(Context mContext, SerialPortType type, SerialPortSettings serialPortSettings) {
        this.mContext = mContext;
        this.serialPortSettings = serialPortSettings;
        this.type = type;
        analogSerialManager = AnalogSerialManagerImpl.getInstance(mContext, true);
        mAnalogSerialManager = (android.newland.AnalogSerialManager) mContext.getSystemService(NlContext.ANALOG_SERIAL_SERVICE);
    }
    @Override
    public void setConfig(SerialPortSettings settings) throws NSDKException {
        if (settings == null) {
            serialPortSettings = new SerialPortSettings(BaudRate.BPS115200, DataBits.DATA_BIT_8, ParityBit.NO_CHECK, StopBits.STOP_BIT_ONE, true);
        } else {
            if (settings.getBaudRate() == null) {
                settings.setBaudRate(BaudRate.BPS115200);
            }
            if (settings.getDataBits() == null) {
                settings.setDataBits(DataBits.DATA_BIT_8);
            }
            if (settings.getParityBit() == null) {
                settings.setParityBit(ParityBit.NO_CHECK);
            }
            if (settings.getStopBits() == null) {
                settings.setStopBits(StopBits.STOP_BIT_ONE);
            }
            serialPortSettings = new SerialPortSettings(settings.getBaudRate(), settings.getDataBits(), settings.getParityBit(), settings.getStopBits(), settings.getIsBlocked());
        }
        analogSerialManager.setConfig(serialPortSettings);
    }

    @Override
    public void setHardwareFlowControl(boolean enableFlowControl) throws NSDKException {
        LogUtils.e("USBSerialPortImpl", "USBSerialPort do not support this method.");
    }

    @Override
    public void awakeExternalDevice() throws NSDKException {
        throw new NSDKException(ErrorCode.UNSUPPORTED, "USBSerialPort doesn't support this method.");
    }

    @Override
    public boolean getExternalPowerSupply() throws NSDKException {
        throw new NSDKException(ErrorCode.UNSUPPORTED, "USBSerialPort doesn't support this method.");
    }


    @Override
    public void open() throws NSDKException {
        analogSerialManager.open();
        analogSerialManager.setConfig(serialPortSettings);
        isOpen = true;
    }

    @Override
    public void flush() throws NSDKException {
        if (!isOpen) {
            throw new NSDKException(ErrorCode.NO_DEVICES, "No opened devices.");
        }
        int ret = analogSerialManager.ioctl(0x540B, new byte[] {2});
        if (ret != 0) {
            throw new NSDKException(ErrorCode.ERROR, "Failed to flush.", ret);
        }
    }

    @Override
    public byte[] read(int length, int timeoutMs) throws NSDKException {
        if (length <= 0 || length > 2048) {
            throw new NSDKIllegalParameterException("Read length shall be  >0 and <2048.");
        }
        if (!isOpen) {
            throw new NSDKException(ErrorCode.NO_DEVICES, "No opened devices.");
        }
        if (timeoutMs < 0 || timeoutMs > 20000) {
            throw new NSDKIllegalParameterException("TimeoutMs shall be range from 0 to 20000 ms.");
        }
        long startTime = System.currentTimeMillis();
        long endTime = startTime + timeoutMs;
        byte[] data = null;
        int len = 0;
        int remainLength = length;
        byte[] result = new byte[2048];
        if (serialPortSettings.getIsBlocked()) {
            do {
                try {
                    byte[] tempData = analogSerialManager.read(remainLength, 0);
                    if (tempData == null || tempData.length == 0) {
                        continue;
                    }
                    System.arraycopy(tempData, 0, result, len, tempData.length);
                    len += tempData.length;
                    remainLength -= tempData.length;
                    if (len >= length) {
                        break;
                    }
                } catch (Exception e) {
                    if (((NSDKException)e).getCode() != -1) {
                        e.printStackTrace();
                    }

                }
            } while (System.currentTimeMillis() < endTime);
            if (len != 0) {
                LogUtils.d("USBSerialPortImpl", "len:" + len);
                data = new byte[len];
                System.arraycopy(result, 0, data, 0, len);

            }
            return data;
        } else {
            try {
                data = analogSerialManager.read(length, -1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return data;
    }


    @Override
    public int write(byte[] data, int timeoutMs) throws NSDKException {
        if (data == null || data.length == 0) {
            throw new NSDKIllegalParameterException("Data shall not be null");
        }
        if (data.length > 4096) {
            throw new NSDKIllegalParameterException("Data length shall be range from 0 to 4096.");
        }
        if (!isOpen) {
            throw new NSDKException(ErrorCode.NO_DEVICES, "No opened devices.");
        }
        if (timeoutMs < 0 || timeoutMs > 20000) {
            throw new NSDKIllegalParameterException("TimeoutMs shall be range from 0 to 20000 ms.");
        }
        long startTime = System.currentTimeMillis();
        long endTime = startTime + timeoutMs;
        int len = 0;
        int writeLen = 0;
        byte[] tempData = null;
        int length = data.length;
        if (serialPortSettings.getIsBlocked()) {
            do {
                tempData = new byte[data.length - len];
                System.arraycopy(data, len, tempData, len, length);
                writeLen = mAnalogSerialManager.write(tempData, tempData.length, 0);
                if (writeLen < 0) {
                    continue;
                }
                len += writeLen;
                length -= writeLen;
                if (len >= data.length) {
                    break;
                }
            } while (System.currentTimeMillis() < endTime);
        } else {
            @SuppressLint("WrongConstant") android.newland.AnalogSerialManager analogSerialManager = (android.newland.AnalogSerialManager)mContext.getSystemService(NlContext.ANALOG_SERIAL_SERVICE);
            len = analogSerialManager.write(data, data.length, -1);
        }
       return len;
    }

    @Override
    public SerialPortType getPortType() throws NSDKException {
        return this.type;
    }

    @Override
    public int ioctl(int cmd, byte[] args) throws NSDKException {
        if (!isOpen) {
            throw new NSDKException(ErrorCode.NO_DEVICES, "No opened devices.");
        }
        return analogSerialManager.ioctl(cmd, args);
    }

    @Override
    public void close() throws NSDKException {
        analogSerialManager.close();
        isOpen = false;
    }

    @Override
    public String getPortName() throws NSDKException {
        return analogSerialManager.getPortName();
    }

    @Override
    public int readLen() throws NSDKException {
        if (!isOpen) {
            throw new NSDKException(ErrorCode.NO_DEVICES, "No opened devices.");
        }
        return analogSerialManager.ioctl(0x541B, new byte[] {0});
    }

}
