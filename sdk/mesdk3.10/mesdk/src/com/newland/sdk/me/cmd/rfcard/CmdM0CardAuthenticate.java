package com.newland.sdk.me.cmd.rfcard;

import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;

@CommandEntity(cmdCode = {(byte) 0xE2, (byte) 0x0B}, responseClass = CmdM0CardAuthenticate.CmdM0CardAuthenticateResponse.class)
public class CmdM0CardAuthenticate extends CommonDeviceCommand {

    @InstructionField(name = "Key", index = 1, maxLen = 128, serializer = ByteArrSerializer.class)
    private byte[] key;

    public CmdM0CardAuthenticate(byte[] key) {
        this.key = key;
    }

    @ResponseEntity
    public static class CmdM0CardAuthenticateResponse extends AbstractSuccessResponse {

    }
}