package com.newland.sdk.me.cmd.printer;

import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;

import java.util.Arrays;

@CommandEntity(cmdCode = {(byte) 0x1B, (byte) 0x04}, responseClass = com.newland.sdk.me.cmd.printer.CmdPapercut.CmdPapercutResponse.class)
public class CmdPapercut extends CommonDeviceCommand {

    @ResponseEntity
    public static class CmdPapercutResponse extends AbstractSuccessResponse {

        @InstructionField(name = "应答码", index = 0, fixLen = 2, maxLen = 2, serializer = ByteArrSerializer.class)
        private byte[] resultCode;

        public boolean getResultCode() {
            try {
                if (resultCode != null && Arrays.equals(resultCode, new byte[]{0x30, 0x31})) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }

}
