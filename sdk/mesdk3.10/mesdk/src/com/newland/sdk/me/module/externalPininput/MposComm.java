package com.newland.sdk.me.module.externalPininput;

import com.newland.sdk.me.conn.SimpleDeviceManager;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.conn.DeviceCommandComplete;
import com.newland.sdk.mtypex.conn.DeviceCommandWrite;
import com.newland.sdk.mtypex.conn.DeviceResponseComplete;
import com.newland.sdk.utils.ISOUtils;

import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author wuhh
 * @Date 2021/3/3
 */
public class MposComm {
    private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger("MposComm");
    private AbstractDevice mAbstractDevice;
    private static final int TIMEOUT_DEFAULT_MS = 3000;
    public MposComm(AbstractDevice device){
        this.mAbstractDevice = device;
    }

    public boolean getMposConnect(){
        if(SimpleDeviceManager.getInstance().getMposParams()!=null){
            return true;
        }
        return false;
    }

    //==============================================================================================//
    //保持和PinpadPackage名称一致,但参数不一致.
    public byte[] sendPinpadCmd(byte[] allData,int timeOutMS, boolean hasAck){
        devicelogger.debug("[sendPinpadCmd] sendData=" + hexString(allData) + " timeOutMS="+ timeOutMS+ " hasAck=" + hasAck);
        byte[] receiveData = Communication(allData,timeOutMS);
        devicelogger.debug("[sendPinpadCmd] receiveData="+hexString(receiveData));
        return receiveData;
    }

    //保持和PinpadPackage名称一致,但参数不一致.
    public byte[] sendCmd(byte[] allData, int timeOutMS){
        devicelogger.debug("[sendCmd] sendData=" + hexString(allData) + " timeOutMS="+ timeOutMS);
        byte[] receiveData = Communication(allData,timeOutMS);
        devicelogger.debug("[sendCmd] receiveData="+hexString(receiveData));
        return receiveData;
    }

    //保持和PinpadPackage名称一致,但参数不一致.
    public byte[] getPinpadRspCode(byte[] allData, int timeOutMS){
        devicelogger.debug("[getPinpadRspCode] sendData="+hexString(allData)+" timeOutMS="+timeOutMS);
        byte[] receiveData = Communication(allData,timeOutMS);
        devicelogger.debug("[getPinpadRspCode] receiveData="+hexString(receiveData));
        return receiveData;
    }

    //保持和PinpadPackage名称一致,但参数不一致.
    public void getPinpadRspCodeW(byte[] data,int timeOutMS){
        devicelogger.debug("[getPinpadRspCode] sendData="+hexString(data)+" timeOutMS="+timeOutMS);
        write(data,timeOutMS);
    }

    //保持和PinpadPackage名称一致,但参数不一致.
    public void unblockSendCmd(byte[] allData,int timeOutMS){
        devicelogger.debug("[unblockSendCmd] sendData="+hexString(allData)+" timeOutMS="+timeOutMS);
        write(allData,timeOutMS);
    }
    //保持和PinpadPackage名称一致,但参数不一致.
    public byte[] boardTxn(){
        return null;
    }
    //==============================================================================================//

    //保持和MPPinpad名称一致,但参数不一致.
    public byte[] communication(byte[] data){
        byte[] respData = this.Communication(data,TIMEOUT_DEFAULT_MS);
        write(new byte[]{0x06},TIMEOUT_DEFAULT_MS);
        return respData;
    }
    //==============================================================================================//


    private void write(byte[] data,int timeOutMS){
        mAbstractDevice.invoke(new DeviceCommandWrite(data), timeOutMS, TimeUnit.MILLISECONDS);
    }

    private byte[] Communication(byte[] data,int timeOutMS){
        DeviceResponseComplete response = (DeviceResponseComplete)mAbstractDevice.invoke(new DeviceCommandComplete(data), timeOutMS, TimeUnit.MILLISECONDS);
        return response.getReceiveData();
    }

    private String hexString(byte[] data){
        return (data==null?"null":ISOUtils.hexString(data));
    }
}
