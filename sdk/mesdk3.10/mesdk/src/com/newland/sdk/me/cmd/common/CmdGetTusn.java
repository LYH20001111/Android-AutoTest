package com.newland.sdk.me.cmd.common;


import com.newland.sdk.me.cmd.serializer.StringSerializer;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;

@CommandEntity(cmdCode = {(byte) 0xF1, (byte) 0x03}, responseClass = CmdGetTusn.CmdTusnResponse.class)
public class CmdGetTusn extends CommonDeviceCommand {

    @ResponseEntity
    public static class CmdTusnResponse extends AbstractSuccessResponse {
        @InstructionField(name = "应答码", index = 0, fixLen = 2, maxLen = 2, serializer = StringSerializer.class)
        private String answerCode;
        @InstructionField(name = "设备TUSN", index = 1, fixLen = 20, maxLen = 20, serializer = StringSerializer.class)
        private String PosTusn;

        public String getPosTusn() {
            return PosTusn;
        }

        public String getAnswerCode() {
            return answerCode;
        }

    }

}

