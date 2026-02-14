package com.newland.nsdk.core.internal.serialportmanager;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.serialport.BaudRate;
import com.newland.nsdk.core.api.common.serialport.DataBits;
import com.newland.nsdk.core.api.common.serialport.ParityBit;
import com.newland.nsdk.core.api.common.serialport.SerialPortSettings;
import com.newland.nsdk.core.api.common.serialport.StopBits;
import com.newland.nsdk.core.api.common.uart3.UART3Config;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.internal.devicemanager.RadarGain;
import com.newland.nsdk.core.api.internal.serialportmanager.SerialPort;
import com.newland.nsdk.core.api.internal.serialportmanager.SerialPortType;
import com.newland.nsdk.core.common.uart3.NDKPortManager;
import com.newland.nsdk.core.common.uart3.SerialPortJni;
import com.newland.nsdk.core.internal.system.SystemPropertyUtil;

import java.util.Locale;

public class SerialPortImpl implements SerialPort {
    private static final String TAG = "SerialPortImpl";
    private SerialPortType serialPortType;
    private UART3Config uart3Config;
    private RS232Manager rs232Manager;
    private NDKPortManager ndkPortManager;
    private SerialPortSettings settings;
    private String nodeName;
    private Object object = new Object();
    private boolean isNDKPort;
    private Context mContext;
    private NlAccessControlManagerUtils nlAccessControlManagerUtils;
    public SerialPortImpl(Context mContext, SerialPortType serialPortType, SerialPortSettings settings) throws NSDKException{
        this.mContext = mContext;
        this.serialPortType = serialPortType;
        this.settings = settings;
        try {
            nlAccessControlManagerUtils = new NlAccessControlManagerUtils(mContext);
        } catch (NSDKException e) {
            LogUtils.w(TAG, "current device is old framework device.");
        }
        if (!isNDKPort()) {
            isNDKPort = false;
            if (serialPortType == SerialPortType.RS232) {
                if ((Build.MODEL.contains("CPOS") && Build.VERSION.SDK_INT >= 33 && isNewFrameWork())) {
                    String nodeNamePrefix = "/dev/ttyUSB";
                    String readData = nlAccessControlManagerUtils.read(nlAccessControlManagerUtils.getNodeName(NlAccessControlManagerUtils.CPOS_X5_A14_RS232));
                    String[] devInfos = readData.split("\n");
                    for (String devInfo : devInfos) {
                        if (devInfo.contains("path:usb-xhci-hcd.2.auto-1.4")) {
                            String nodeName = nodeNamePrefix + devInfo.charAt(0);
                            rs232Manager = new RS232Manager(nodeName);
                        }
                    }
                    if (rs232Manager == null) {
                        throw new NSDKException(ErrorCode.ERROR, "Please pull out USB cable.");
                    }
                } else {
                    rs232Manager = new RS232Manager(SerialPortType.RS232);
                }
            } else if (serialPortType == SerialPortType.RS232B) {
                rs232Manager = new RS232Manager(SerialPortType.RS232B);
            } else if (serialPortType == SerialPortType.PINPAD) {
                if (Build.MODEL.contains("CPOS")) {
                    rs232Manager = new RS232Manager(SerialPortType.PINPAD);
                }
            } else if (serialPortType == SerialPortType.USB_DEVICE) { //20251117: 将 N950SU-C 传入的 USB_DEVICE 转向 RS232，修复传入 USB_DEVICE 枚举，getPortType 接口返回 RS232 不一致的问题
                rs232Manager = new RS232Manager(SerialPortType.RS232);
            }
        } else{
            isNDKPort = true;
            ndkPortManager = NDKPortManager.getInstance();
        }
    }

    public SerialPortImpl(String nodeName, SerialPortSettings settings, SerialPortType serialPortType) {
        this.nodeName = nodeName;
        this.settings = settings;
        this.serialPortType = serialPortType;
        rs232Manager = new RS232Manager(nodeName);
    }

    @Override
    public void setConfig(SerialPortSettings settings) throws NSDKException {
        if (settings == null) {
            this.settings = new UART3Config(BaudRate.BPS115200, DataBits.DATA_BIT_8, ParityBit.NO_CHECK, StopBits.STOP_BIT_ONE, true);
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
            this.settings = new SerialPortSettings(settings.getBaudRate(), settings.getDataBits(), settings.getParityBit(), settings.getStopBits(), settings.getIsBlocked(), settings.isFlowCtr());
        }
        close();
        if (isNDKPort) {
            ndkPortManager.open(this.settings);
        } else {
            rs232Manager.open(this.settings);
        }
    }

    @Override
    public void setHardwareFlowControl(boolean enableFlowControl) throws NSDKException {
        if (!Build.MODEL.contains("U2000")) {
            LogUtils.e(TAG, "This method is only supported in U2000 devices now.");
            return;
        }
        settings.setFlowCtr(enableFlowControl);
        close();
        if (isNDKPort) {
            ndkPortManager.open(settings);
        } else {
            rs232Manager.open(settings);
        }
    }

    @Override
    public void awakeExternalDevice() throws NSDKException {
        if (isNewFrameWork() && Build.MODEL.equalsIgnoreCase("U200")) {
            int type = nlAccessControlManagerUtils.getNodeName(NlAccessControlManagerUtils.U200_WAKE_UP);
            nlAccessControlManagerUtils.write(type, new byte[] {0x31});
        } else {
            int ret = SerialPortJni.getInstance().awakeExternalDevice();
            if (ret == -5) {
                throw new NSDKException(ErrorCode.UNSUPPORTED, "This method is only supported on U2000 devices.");
            } else if (ret != ErrorCode.OK) {
                throw new NSDKException(ret, String.format(Locale.US, "Failed to awake external device, ret = %d", ret));
            }
        }

    }

    @Override
    public boolean getExternalPowerSupply() throws NSDKException {
        int ret = SerialPortJni.getInstance().getExternalPowerSupply();
        if (ret == -5) {
            throw new NSDKException(ErrorCode.UNSUPPORTED, "This method is only supported on U2000 devices.");
        } else if (ret < 0) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to get external power supply, ret = %d", ret));
        }
        return ret == 1;
    }

    @Override
    public void open() throws NSDKException {
        if (settings == null) {
            this.settings = new SerialPortSettings(BaudRate.BPS115200, DataBits.DATA_BIT_8, ParityBit.NO_CHECK, StopBits.STOP_BIT_ONE, true);
        }
        if (isNDKPort) {
            ndkPortManager.open(settings);
        } else {
            rs232Manager.open(settings);
        }

    }

    @Override
    public void flush() throws NSDKException {
        if (isNDKPort) {
            ndkPortManager.clearBuf();
        } else {
            rs232Manager.flush();
        }
    }

    @Override
    public byte[] read(int length, int timeoutMs) throws NSDKException {
        if (timeoutMs < 0 || timeoutMs > 20000) {
            throw new NSDKIllegalParameterException("TimeoutMs shall be range from 0 to 20000 ms.");
        }
        if (isNDKPort) {
            if (length < 0 || length > 512) {
                throw new NSDKIllegalParameterException("PINPAD data length shall be range from 0 to 512.");
            }
            if (settings.getIsBlocked()) {
                return ndkPortManager.read(length, timeoutMs);
            } else {
                return ndkPortManager.read(length, 0);
            }
        } else {
            if (length < 0 || length > 2048) {
                throw new NSDKIllegalParameterException("RS232 length shall be range from 0 to 2048.");
            }
            if (settings.getIsBlocked()) {
                return rs232Manager.read(length, timeoutMs);
            } else {
                return rs232Manager.read(length, 0);
            }

        }
    }

    @Override
    public int readLen() throws NSDKException {
        if (isNDKPort) {
            return ndkPortManager.readBufLen();
        } else {
            return rs232Manager.readLen();
        }
    }

    @Override
    public int write(final byte[] data, final int timeoutMs) throws NSDKException {
        if (timeoutMs < 0 || timeoutMs > 20000) {
            throw new NSDKIllegalParameterException("TimeoutMs shall be range from 0 to 20000 ms.");
        }
        long startTime = System.currentTimeMillis();
        long endTime = startTime + timeoutMs;

        int ret = -1;
        if (!isNDKPort) {
            if (data == null || data.length == 0 || data.length > 2048) {
                throw new NSDKIllegalParameterException("RS232 data length shall be range from 1 to 2048.");
            }
            if (settings.getIsBlocked() && timeoutMs != 0) {
                while(System.currentTimeMillis() < endTime) {
                    ret = rs232Manager.write(data, data.length, timeoutMs);
                    if (ret == data.length) {
                        break;
                    }
                }
            } else {
                do {
                    ret = rs232Manager.write(data, data.length, 10);
                } while (ret != data.length);

            }

        } else {
            if (data == null || data.length == 0 || data.length > 512) {
                throw new NSDKIllegalParameterException("PINPAD data length shall be range from 1 to 512.");
            }
            ndkPortManager.write(data);
        }
        return ret;
    }

    @Override
    public SerialPortType getPortType() throws NSDKException {
        return this.serialPortType;
    }

    @Override
    public int ioctl(int cmd, byte[] args) throws NSDKException {
        int ret = -9999;
        if (serialPortType == SerialPortType.PINPAD) {
            throw new NSDKException(ret, "Unsupported ioctl function for PINPAD.");
        }
        ret = rs232Manager.ioctl(cmd, args);
        return ret;
    }

    @Override
    public void close() throws NSDKException {
        if (isNDKPort) {
            ndkPortManager.close();
        } else {
            rs232Manager.close();
        }
    }

    private boolean isNDKPort() {
        return serialPortType == SerialPortType.PINPAD && !Build.MODEL.contains("CPOS");
    }

    private boolean isNewFrameWork() {
        return "V2".equalsIgnoreCase(SystemPropertyUtil.getProperty("ro.build.version.nl_api", ""));
    }
}
