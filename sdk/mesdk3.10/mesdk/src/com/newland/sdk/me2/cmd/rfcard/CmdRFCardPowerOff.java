package com.newland.sdk.me2.cmd.rfcard;

import com.newland.sdk.me.cmd.serializer.ByteSerializer;
import com.newland.sdk.me.cmd.serializer.Integer2Serializer;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;

@CommandEntity(cmdCode = {(byte) 0xE2, (byte) 0x02}, responseClass = CmdRFCardPowerOff.CmdRFCardPowerOffResponse.class)
public class CmdRFCardPowerOff extends CommonDeviceCommand {

    @InstructionField(name="超时时间(毫秒)",index = 0,fixLen = 2, maxLen = 2, serializer =Integer2Serializer.class)
    private int timeOut;
    @InstructionField(name="关闭非接天线",index = 1,fixLen = 1, maxLen = 1, serializer =ByteSerializer.class)
    private byte closeAntenna=0x01;
    public CmdRFCardPowerOff(int timeOut){

        this.timeOut = timeOut;

    }

    @ResponseEntity
    public static class CmdRFCardPowerOffResponse extends AbstractSuccessResponse{

    }
}