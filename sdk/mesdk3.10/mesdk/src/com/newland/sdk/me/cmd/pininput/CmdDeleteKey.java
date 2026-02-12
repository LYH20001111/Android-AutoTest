package com.newland.sdk.me.cmd.pininput;

import com.newland.sdk.me.cmd.serializer.IntegerSerializer;
import com.newland.sdk.module.pin.AlgorithmMode;
import com.newland.sdk.module.pin.KeyType;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;

@CommandEntity(cmdCode = {(byte) 0x1A, (byte) 0x20}, responseClass = CmdDeleteKey.CmdDeleteKeyResponse.class)
public class CmdDeleteKey extends CommonDeviceCommand {
    private static final int DELETE_TEK = 0x00;
    private static final int DELETE_MAIN_KEY = 0x01;
    private static final int DELETE_PIN_KEY = 0x02;
    private static final int DELETE_MAC_KEY = 0x03;
    private static final int DELETE_DATAENCRYPT_KEY = 0x04;
    private static final int DELETE_ALL_KEY = 0x05;

    @InstructionField(name = "密钥类型",index=0,fixLen = 1,maxLen=1,serializer=IntegerSerializer.class)
    private int keyType;

    @InstructionField(name="算法模式",index=1,fixLen=1,maxLen=1,serializer=CmdLoadMainKeyAndVerify.AlgorithmModeSerializer.class)
    private AlgorithmMode algorithmMode;

    @InstructionField(name = "密钥索引", index = 2, fixLen=1,maxLen = 1, serializer = IntegerSerializer.class)
    private int keyIndex;

    public CmdDeleteKey(KeyType keyType, AlgorithmMode algorithmMode, int keyIndex) {
        switch (keyType){
            case TRANSPORT_KEY:
                this.keyType = DELETE_TEK;
                break;
            case MASTER_KEY:
                this.keyType = DELETE_MAIN_KEY;
                break;
            case PIN_KEY:
                this.keyType = DELETE_PIN_KEY;
                break;
            case MAC_KEY:
                this.keyType = DELETE_MAC_KEY;
                break;
            case TRACK_KEY:
                this.keyType = DELETE_DATAENCRYPT_KEY;
                break;
        }
        this.algorithmMode = algorithmMode;
        this.keyIndex = keyIndex;
    }

    public CmdDeleteKey(){
        this.keyType = DELETE_ALL_KEY;
        this.algorithmMode = AlgorithmMode.DES;
        this.keyIndex = 0;
    }

    @ResponseEntity
    public static class CmdDeleteKeyResponse extends AbstractSuccessResponse {
    }
}
