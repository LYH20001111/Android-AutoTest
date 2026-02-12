package com.newland.sdk.mtypex.conn;

import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;

/**
 * @Description
 * @Author wuhh
 * @Date 2021/3/1
 */
public class DeviceResponseWrite extends AbstractSuccessResponse {

    private byte[] receiveData;

    public DeviceResponseWrite(byte[] receiveData){
        this.receiveData = receiveData;
    }

    public byte[] getReceiveData(){
        return receiveData;
    }
}
