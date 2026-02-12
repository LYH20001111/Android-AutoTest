package com.newland.sdk.mtypex.conn;

import com.newland.sdk.mtypex.cmd.DeviceCommand;

/**
 * @Description
 * @Author wuhh
 * @Date 2021/3/1
 */
public class DeviceCommandComplete implements DeviceCommand {

    private byte[] sendData;
    public DeviceCommandComplete(byte[] sendData){
        this.sendData = sendData;
    }

    public byte[] getSendData(){
        return sendData;
    }
}
