package com.newland.sdkdemo.fragment.dock.communication;

import android.content.Context;
import android.util.Log;

import com.newland.sdk.dock.DockModuleManage;
import com.newland.sdk.dock.serial.DockBaudrate;
import com.newland.sdk.dock.usb.DockPortType;
import com.newland.sdk.inter.externalpin.CommunicationListener;
import com.newland.sdk.inter.externalpin.ErrorCode;
import com.newland.sdk.pinpad.utils.ISOUtils;
import com.newland.sdkdemo.AppConfig;

/**
 * A communication example for Ext-PinKeyboard in Newland(NPT) Terminal.
 *
 * @author linsi
 */
public class CommunicationTool {
    private final String TAG = this.getClass().getSimpleName();
    private ChannelType channelType;
    private Context context;
    private static CommunicationTool instance;

    private DockModuleManage dockModuleManage;
    private CommunicationTool() {

    }
    public static CommunicationTool getInstance() {
        if (instance == null) {
            instance = new CommunicationTool();
        }
        return instance;
    }
    public void init(Context context){
        this.context = context;
        this.dockModuleManage = DockModuleManage.getInstance();
    }
    public void setChannelType(ChannelType channelType) {
        this.channelType = channelType;
    }


    public CommunicationListener getCommunicationListener() {
        return communicationListener;
    }

    public DockModuleManage getDockModuleManage() {
        return dockModuleManage;
    }

    /**
     * Open
     *
     * @return 0: Successfully, -1: Failed, Other: Other exception.
     */
    private int open() {
        Log.i(TAG, "[open]:" + channelType);
        if (this.channelType == ChannelType.DOCK_USB1) {

        } else if (this.channelType == ChannelType.DOCK_UART) {
            int ret = dockModuleManage.getDockSerialModule().open(DockBaudrate.BPS115200,null);
            Log.i(TAG, "DOCK_UART open: ret:"+ret);
            return ret;
        }
        return -1;
    }

    /**
     * Read data
     *
     * @param outputData
     * @param timeOut
     * @return 0: Successfully, -1: Failed, Other: Other exception.
     */
    private int read(byte[] outputData, int timeOut) {
        Log.i(TAG, "[read]:" + channelType);
        Log.i(TAG, "[read]:outputData:" + (outputData == null ? "null" : ISOUtils.hexString(outputData)) + ", timeOut:" + timeOut);
        if (this.channelType == ChannelType.DOCK_USB1) {

        } else if (this.channelType == ChannelType.DOCK_UART) {
            int ret = dockModuleManage.getDockSerialModule().read(outputData, outputData.length,timeOut);
            Log.i(TAG, "[read]: DOCK_UART ret="+ret+" outputData:" + (outputData == null ? "null" : ISOUtils.hexString(outputData)) + ", timeOut:" + timeOut);
            if (ret > 0){
                return 0;
            }
        }
        return -1;
    }

    /**
     * @param inputData
     * @param timeOut
     * @return 0: Successfully, -1: Failed, Other: Other exception.
     */
    private int write(byte[] inputData, int timeOut) {
        Log.i(TAG, "[write]:" + channelType);
        Log.i(TAG, "[write]:outputData:" + (inputData == null ? "null" : ISOUtils.hexString(inputData)) + ", timeOut:" + timeOut);
        if (this.channelType == ChannelType.DOCK_USB1) {

        } else if (this.channelType == ChannelType.DOCK_UART) {
            int ret = dockModuleManage.getDockSerialModule().write(inputData);
            Log.i(TAG, "[write]: DOCK_UART ret="+ret);
            if(ret > 0){
                return 0;
            }
        }
        return -1;
    }

    /**
     * Close
     *
     * @return 0: Successfully, -1: Failed, Other: Other exception.
     */
    private int close() {
        Log.i(TAG, "[close]:" + channelType);
        if (this.channelType == ChannelType.DOCK_USB1) {

        } else if (this.channelType == ChannelType.DOCK_UART) {
            int ret = dockModuleManage.getDockSerialModule().close();
            Log.i(TAG, "[close]: DOCK_UART ret="+ret);
            return ret;
        }
        return -1;
    }

    private void error(ErrorCode errorCode, String errMsg) {
        Log.i(TAG, "[error]:" + channelType);
        Log.i(TAG, "[error]:errCode:" + errorCode + ", errMsg:" + errMsg);
        if (this.channelType == ChannelType.DOCK_USB1) {

        } else if (this.channelType == ChannelType.DOCK_UART) {

        }
    }

    /**
     * Clear
     *
     * @return 0: Successfully, -1: Failed, Other: Other exception.
     */
    private int clear() {
        Log.i(TAG, "[clear]:" + channelType);
        if (this.channelType == ChannelType.DOCK_USB1) {

        } else if (this.channelType == ChannelType.DOCK_UART) {
            boolean result = dockModuleManage.getDockSerialModule().clearBuffer(1);
            Log.i(TAG, "[clear]: DOCK_UART result="+result);
            return ((result == true) ? 0 : -1);
        }
        return -1;
    }

    private CommunicationListener communicationListener = new CommunicationListener() {
        @Override
        public int open() {
            return instance.open();
        }

        @Override
        public int read(byte[] outputData, int timeOut) {
            return instance.read(outputData, timeOut);

        }

        @Override
        public int write(byte[] inputData, int timeOut) {
            return instance.write(inputData, timeOut);
        }

        @Override
        public int close() {
            return instance.close();
        }

        @Override
        public void error(ErrorCode errorCode, String errMsg) {
            instance.error(errorCode, errMsg);
        }

        @Override
        public int clear() {
            return instance.clear();
        }
    };


}
