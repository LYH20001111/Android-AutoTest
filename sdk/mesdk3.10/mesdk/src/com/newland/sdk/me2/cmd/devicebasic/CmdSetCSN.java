package com.newland.sdk.me2.cmd.devicebasic;

import com.newland.sdk.me.cmd.serializer.ByteSerializer;
import com.newland.sdk.me.cmd.serializer.StringSerializer;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;


@CommandEntity(cmdCode = {(byte) 0xFF, (byte) 0x02}, responseClass = CmdSetCSN.CmdSetCSNResponse.class)
public class CmdSetCSN extends CommonDeviceCommand {

    @InstructionField(name = "mode", index = 0, fixLen = 1, maxLen = 1, serializer = ByteSerializer.class)
    private byte mode;
    @InstructionField(name = "客户序列号CSN", index = 1, maxLen = 20, serializer = StringSerializer.class)
    private String data;

    public CmdSetCSN(byte mode, String data) {
        this.mode = mode;
        this.data = data;
    }

    @ResponseEntity
    public static class CmdSetCSNResponse extends AbstractSuccessResponse {

    }

}
