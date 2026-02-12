package com.newland.sdk.me2.cmd.rfcard;

import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.me.cmd.serializer.IntegerSerializer;
import com.newland.sdk.me.cmd.serializer.RFKeyModeSerializer;
import com.newland.sdk.module.rfcard.RFKeyMode;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;

@CommandEntity(cmdCode = {(byte) 0xE2, (byte) 0x08}, responseClass = CmdM1CardAuthenticate.CmdM1CardAuthenticateResponse.class)
public class CmdM1CardAuthenticate extends CommonDeviceCommand {

    @InstructionField(name = "KEY模式", index = 0, fixLen = 1, maxLen = 1, serializer = RFKeyModeSerializer.class)
    private RFKeyMode keyMode;
    @InstructionField(name = "SNR", index = 1, fixLen = 4, maxLen = 4, serializer = ByteArrSerializer.class)
    private byte[] SNR;
    @InstructionField(name = "要认证的块号", index = 2, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
    private int blockNo;
    @InstructionField(name = "密钥", index = 3, fixLen = 6, maxLen = 6, serializer = ByteArrSerializer.class)
    private byte[] key;

    public CmdM1CardAuthenticate(RFKeyMode keyMode, byte[] SNR, int blockNo, byte[] key) {
        this.keyMode = keyMode;
        this.SNR = SNR;
        this.blockNo = blockNo;
        this.key = key;
    }

    @ResponseEntity
    public static class CmdM1CardAuthenticateResponse extends AbstractSuccessResponse {

    }
}