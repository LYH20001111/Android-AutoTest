package com.newland.sdk.me.cmd.pininput;

import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.me.cmd.serializer.IntegerSerializer;
import com.newland.sdk.me.cmd.serializer.StringSerializer;
import com.newland.sdk.module.pin.LoadKeyMode;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;

@CommandEntity(cmdCode = {(byte) 0x1A, (byte) 0x17}, responseClass = CmdKSNLoad.CmdKSNLoadResponse.class)
public class CmdKSNLoad extends CommonDeviceCommand {

    @InstructionField(name = "Key加载模式", index = 0, fixLen = 1, maxLen = 1, serializer = CmdLoadMainKeyAndVerify.LoadKeyModeSerializer.class)
    private LoadKeyMode loadKeyMode;

    @InstructionField(name = "KSN索引", index = 1, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
    private int KSNIndex;

    @InstructionField(name = "KSN明文", index = 2, fixLen = 10, maxLen = 10, serializer = ByteArrSerializer.class)
    private byte[] ksn;

    @InstructionField(name = "装载的初始密钥数据", index = 3, maxLen = 512, serializer = ByteArrSerializer.class)
    private byte[] defaultKeyData;

    @InstructionField(name = "主密钥索引(作为传输密钥)", index = 4, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
    private int mainKeyIndex;

    @InstructionField(name = "校验值", index = 5, maxLen = 8, serializer = ByteArrSerializer.class)
    private byte[] checkValue;

    public CmdKSNLoad(LoadKeyMode loadKeyMode, int KSNIndex, byte[] ksn, byte[] defaultKeyData, int mainKeyIndex, byte[] checkValue) {
        this.loadKeyMode = loadKeyMode;
        this.KSNIndex = KSNIndex;
        this.ksn = ksn;
        this.defaultKeyData = defaultKeyData;
        this.mainKeyIndex = mainKeyIndex;
        this.checkValue = checkValue;
    }

    @ResponseEntity
    public static class CmdKSNLoadResponse extends AbstractSuccessResponse {

        @InstructionField(name = "应答码", index = 0, fixLen = 2, maxLen = 2, serializer = StringSerializer.class)
        private String resultCode;

        public String getResultCode() {
            return resultCode;
        }

    }
}
