package com.newland.sdk.me2.cmd.rfcard;

import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.me.cmd.serializer.ByteSerializer;
import com.newland.sdk.me.cmd.serializer.Integer2Serializer;
import com.newland.sdk.me.cmd.serializer.StringSerializer;
import com.newland.sdk.module.rfcard.RFCardType;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;
import com.newland.sdk.mtypex.serializer.AbstractEnumSerializer;

import static com.newland.sdk.module.rfcard.RFCardType.ACARD;

@CommandEntity(cmdCode = {(byte) 0xE2, (byte) 0x01}, responseClass = CmdRFCardPowerOn.CmdRFCardPowerOnResponse.class)
public class CmdRFCardPowerOn extends CommonDeviceCommand {

    private static final int A_RFCARD = 0x0001;
    private static final int B_RFCARD = 0x0002;
    private static final int M1_RFCARD = 0x0004;

    @InstructionField(name = "寻卡类型", index = 0, fixLen = 1, maxLen = 1, serializer = ByteSerializer.class)
    private byte rfCardType;
    @InstructionField(name = "超时时间", index = 1, fixLen = 2, maxLen = 2, serializer = Integer2Serializer.class)
    private int timeOut;
    @InstructionField(name = "显示内容", index = 2, maxLen = 96, serializer = StringSerializer.class)
    private String showMsg;
    @InstructionField(name = "sak", index = 3, maxLen = 1, serializer = ByteSerializer.class)
    private byte sak;

    public CmdRFCardPowerOn(RFCardType[] rfCardTypes, int timeout) {
        if (null != rfCardTypes) {
            for (RFCardType cardType : rfCardTypes) {
                if (cardType == ACARD) {
                    this.rfCardType |= A_RFCARD;
                } else if (cardType == RFCardType.BCARD) {
                    this.rfCardType |= B_RFCARD;
                } else if (cardType == RFCardType.M1CARD) {
                    this.rfCardType |= M1_RFCARD;
                }
            }
        }
        this.timeOut = timeout;
    }

    public CmdRFCardPowerOn(RFCardType[] rfCardTypes, int timeout, Byte sak) {
        if (null != rfCardTypes) {
            for (RFCardType cardType : rfCardTypes) {
                if (cardType == RFCardType.ACARD) {
                    this.rfCardType |= 0x01;
                } else if (cardType == RFCardType.BCARD) {
                    this.rfCardType |= 0x02;
                } else if (cardType == RFCardType.M1CARD) {
                    this.rfCardType |= 0x04;
                }
            }
        }
        this.timeOut = timeout;
        if (sak != 0x00) {
            this.sak = sak;
        }
    }

    public static class RFCardTypeSerializer extends AbstractEnumSerializer {
        public RFCardTypeSerializer() {
            super(RFCardType.class, new byte[][]{{0x0A}, {0x0B}, {0x0C}, {0x0D}, {0x0E}});
        }
    }

    @ResponseEntity
    public static class CmdRFCardPowerOnResponse extends AbstractSuccessResponse {
        @InstructionField(name = "卡类型", index = 0, fixLen = 1, maxLen = 1, serializer = RFCardTypeSerializer.class)
        private RFCardType rfCardType;
        @InstructionField(name = "卡片内部序列号", index = 1, maxLen = 1024, serializer = ByteArrSerializer.class)
        private byte[] cardSerialNo;
        @InstructionField(name = "ATQA", index = 2, fixLen = 2, maxLen = 2, serializer = ByteArrSerializer.class)
        private byte[] ATQA;

        public RFCardType getRFCardType() {
            return rfCardType;
        }

        public byte[] getCardSerialNo() {
            return cardSerialNo;
        }

        public byte[] getATQA() {
            return ATQA;
        }

    }
}
