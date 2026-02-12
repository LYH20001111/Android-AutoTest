package com.newland.nsdk.core.internal.analogserial;

import android.annotation.SuppressLint;
import android.content.Context;
import android.newland.content.NlContext;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.serialport.DataBits;
import com.newland.nsdk.core.api.common.serialport.ParityBit;
import com.newland.nsdk.core.api.common.serialport.SerialPortSettings;
import com.newland.nsdk.core.api.common.serialport.StopBits;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.internal.analogserial.AnalogSerialManager;

import java.util.Locale;

public class AnalogSerialManagerImpl implements AnalogSerialManager {
    private String TAG = "AnalogManagerImpl";
    private boolean isSupported;
    private Context mContext;
    private volatile static AnalogSerialManagerImpl instance;
    private android.newland.AnalogSerialManager mAnalogSerialManager;

    public static AnalogSerialManagerImpl getInstance(Context mContext, boolean isSupported) {
        if (instance == null) {
            synchronized (AnalogSerialManagerImpl.class) {
                if (instance == null || instance.mContext != mContext || instance.isSupported != isSupported) {
                    instance = new AnalogSerialManagerImpl(mContext, isSupported);
                }
            }
        } else {
            if (instance.isSupported != isSupported || instance.mContext != mContext) {
                instance = new AnalogSerialManagerImpl(mContext, isSupported);
            }
        }
        return instance;
    }

    @SuppressLint("WrongConstant")
    private AnalogSerialManagerImpl(Context mContext, boolean isSupported) {
        this.isSupported = isSupported;
        this.mContext = mContext;
        LogUtils.d(TAG, "mContext:" + (mContext == null ? "null" : "not null"));
        if (mContext != null) {
            mAnalogSerialManager = (android.newland.AnalogSerialManager) mContext.getSystemService(NlContext.ANALOG_SERIAL_SERVICE);
            this.isSupported = true;
        } else {
            this.isSupported = false;
        }

    }

    private void isSupported() throws NSDKException {
        if (!isSupported) {
            throw new NSDKException(ErrorCode.NOT_SUPPORTED, "Unsupported AnalogSerial Module.");
        }
    }




    @Override
    public void open() throws NSDKException {
        isSupported();
        int ret = mAnalogSerialManager.open();
        if (ret <= -1) {
            throw new NSDKException(ErrorCode.ERROR, "Failed to open device.", ret);
        }
    }

    @Override
    public void open(String portName) throws NSDKException {
        isSupported();
        int ret = mAnalogSerialManager.open(portName);
        if (ret == -1) {
            throw new NSDKException(ErrorCode.ERROR, "Failed to open designated device.");
        }
    }

    @Override
    public void close() throws NSDKException {
        isSupported();
        mAnalogSerialManager.close();
    }

    @Override
    public String getPortName() throws NSDKException {
        isSupported();
        String portName = mAnalogSerialManager.getPortName();
        return portName;
    }

    @Override
    public void setConfig(SerialPortSettings serialPortSettings) throws NSDKException {
        isSupported();
        StringBuffer configArgs = new StringBuffer();
        int baud = 115200;
        if (serialPortSettings != null) {
            if (serialPortSettings.getBaudRate() != null){
                baud = serialPortSettings.getBaudRate().toValue();
            }
            if (serialPortSettings.getDataBits() != null) {
                configArgs.append(getDataBitsValue(serialPortSettings.getDataBits()));
            } else {
                LogUtils.e(TAG, "input null, forced to DataBits 8.");
                configArgs.append("8");
            }
            if (serialPortSettings.getParityBit() != null) {
                configArgs.append(getParityBitValue(serialPortSettings.getParityBit()));
            } else {
                LogUtils.e(TAG, "input null, forced to ParityBit.NO_CHECK.");
                configArgs.append("N");
            }
            if (serialPortSettings.getStopBits() != null) {
                configArgs.append(getStopBitsValue(serialPortSettings.getStopBits()));
            } else {
                LogUtils.e(TAG, "input null, forced to StopBits 1.");
                configArgs.append("1");
            }
            configArgs.append("N");
            configArgs.append(getIsBlockedValue(serialPortSettings.getIsBlocked()));
        } else {
            configArgs.append("8");
            configArgs.append("N");
            configArgs.append("1");
            configArgs.append("N");
            configArgs.append("N");
        }
        byte[] buf = configArgs.toString().getBytes();
        int ret = mAnalogSerialManager.setconfig(baud, 0, buf);
        if (ret != 0) {
            switch (ret) {
                case -2:
                    throw new NSDKException(ErrorCode.ERROR, "Failed to set serial configuration.", ret);
                case -3:
                    throw new NSDKException(ErrorCode.ERROR, "Failed to enable read|write block.", ret);
                case -14:
                    throw new NSDKIllegalParameterException("Parameters error.");
                default:
                    throw new NSDKException(ret, String.format(Locale.US, "Failed to set serial configuration, ret = %d", ret));
            }
        }

    }

    @Override
    public int ioctl(int cmd, byte[] args) throws NSDKException {
        isSupported();
        if (cmd != 0x540B && cmd != 0x541B) {
            throw new NSDKIllegalParameterException("Command shall be 0x540B or 0x541B");
        }
        if (args == null) {
            throw new NSDKIllegalParameterException("Args shall not be null.");
        }
        int ret = mAnalogSerialManager.ioctl(cmd, args);
        if (ret < 0) {
            if (ret == -14) {
                throw new NSDKException(ErrorCode.ERROR, "Device not supported.", ret);
            } else {
                throw new NSDKException(ErrorCode.ERROR, "Failed to ioctl device.", ret);
            }
        }
        return ret;
    }

    @Override
    public byte[] read(int maxLength, int timeout) throws NSDKException {
        isSupported();
        if (maxLength > 2048 || maxLength <= 0) {
            throw new NSDKIllegalParameterException("Read max length shall be >0 and <=2048.");
        }
        byte[] result = new byte[maxLength];
        int ret = mAnalogSerialManager.read(result, maxLength, timeout);
        if (ret == -1) {
            throw new NSDKException(ErrorCode.ERROR, "Failed to read data from serial buffer.");
        }
        byte[] data = null;
        if (ret > 0) {
            data = new byte[ret];
            System.arraycopy(result, 0, data, 0, ret);
        }
        return data;
    }

    @Override
    public void write(byte[] buf, int maxLength, int timeout) throws NSDKException {
        isSupported();
        if ( buf == null || buf.length == 0) {
            throw new NSDKIllegalParameterException("Data to be written shall not be null");
        }
        if (maxLength <= 0) {
            throw new NSDKIllegalParameterException("Max length shall be > 0.");
        }
        int ret = mAnalogSerialManager.write(buf, maxLength, timeout);
        if (ret <= 0) {
            throw new NSDKException(ErrorCode.ERROR, "Failed to write data to serial buffer.", ret);
        }
    }

    @Override
    public void setMacMode(boolean isMac) throws NSDKException {
        isSupported();
        mAnalogSerialManager.setMacMode(isMac);
    }

    @Override
    public boolean isMacMode() throws NSDKException {
        isSupported();
        boolean isMacMode = mAnalogSerialManager.isMacMode();
        return isMacMode;
    }

    private String getStopBitsValue(StopBits stopBits) {
        switch (stopBits) {
            case STOP_BIT_TWO:
                return "2";
            default:
                return "1";
        }
    }
    private String getParityBitValue(ParityBit parityBit) {
        switch (parityBit) {
            case ODD_CHECK:
                return "O";
            case EVEN_CHECK:
                return "E";
            default:
                return "N";
        }
    }

    private String getDataBitsValue(DataBits dataBits) {
        switch (dataBits) {
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

    private String getIsBlockedValue(boolean isBlocked) {
        if (isBlocked) {
            return "Y";
        } else {
            return "N";
        }
    }

    private String getIsIrEnabledValue(boolean isIrEnabled) {
        if (isIrEnabled) {
            return "Y";
        } else {
            return "N";
        }
    }
}
