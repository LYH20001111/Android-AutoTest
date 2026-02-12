package com.newland.sdk.me.cmd.rfcard;

import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;

/**
 * Felica Card transmit command.
 *
 * @author linsi
 * @since V3.10.01
 */
@CommandEntity(cmdCode = {(byte) 0xE2, (byte) 0x05}, responseClass = CmdFelicaCardTransmit.CmdFelicaCardTransmitResponse.class)
public class CmdFelicaCardTransmit extends CommonDeviceCommand {

    @InstructionField(name = "数据", index = 0, maxLen = 4000, serializer = com.newland.sdk.me.cmd.serializer.ByteArrSerializer.class)
    private byte[] req;

    public CmdFelicaCardTransmit(byte[] req) {
        this.req = req;
    }

    @ResponseEntity
    public static class CmdFelicaCardTransmitResponse extends AbstractSuccessResponse {
        @InstructionField(name = "应答数据", index = 0, maxLen = 4000, serializer = com.newland.sdk.me.cmd.serializer.ByteArrSerializer.class)
        private byte[] data;

        public byte[] getData() {
            return data;
        }

    }
}
