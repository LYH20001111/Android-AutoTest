package com.newland.sdk.mtypex.conn;

import com.newland.sdk.mtypex.cmd.DeviceCommand;

/**
 * @Description
 * @Author wuhh
 * @Date 2021/3/4
 */
public class DeviceCommandWrite implements DeviceCommand {
    private byte[] sendData;

    public DeviceCommandWrite(byte[] sendData){
        this.sendData = sendData;
    }

    public byte[] getSendData(){
        return sendData;
    }

}
