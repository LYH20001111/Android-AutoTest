package com.newland.sdk.me2.cmd.iccard;

import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.me.cmd.serializer.ICCardClassSerializer;
import com.newland.sdk.me.cmd.serializer.ICCardSlotSerializer;
import com.newland.sdk.module.iccard.ICCardSlot;
import com.newland.sdk.module.iccard.ICCardType;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;

@CommandEntity(cmdCode = {(byte) 0xE1, (byte) 0x05}, responseClass = CmdICCardTransmit.CmdICCardTransmitResponse.class)
public class CmdICCardTransmit extends CommonDeviceCommand {

    @InstructionField(name = "卡Slot", index = 0, fixLen = 1, maxLen = 1, serializer = ICCardSlotSerializer.class)
    private ICCardSlot slot;

    @InstructionField(name = "卡类型", index = 1, fixLen = 1, maxLen = 1, serializer = ICCardClassSerializer.class)
    private ICCardType iCCardType;

    @InstructionField(name = "数据", index = 2, maxLen = 4000, serializer = ByteArrSerializer.class)
    private byte[] req;

    public CmdICCardTransmit(ICCardSlot slot, ICCardType iCCardType, byte[] req) {
        this.slot = slot;
        this.iCCardType = iCCardType;
        this.req = req;
    }

    @ResponseEntity
    public static class CmdICCardTransmitResponse extends AbstractSuccessResponse {
        @InstructionField(name = "应答数据", index = 0, maxLen = 4000, serializer = ByteArrSerializer.class)
        private byte[] response;

        public byte[] getResponse() {
            return response;
        }


    }
}