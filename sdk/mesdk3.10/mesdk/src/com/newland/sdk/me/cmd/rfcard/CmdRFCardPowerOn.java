package com.newland.sdk.me.cmd.rfcard;

import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.me.cmd.serializer.Integer2Serializer;
import com.newland.sdk.module.rfcard.FelicaParams;
import com.newland.sdk.module.rfcard.RFCardType;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;
import com.newland.sdk.mtypex.serializer.AbstractEnumSerializer;

import java.util.Arrays;

import static com.newland.sdk.module.rfcard.RFCardType.ACARD;

@CommandEntity(cmdCode = {(byte) 0xE2, (byte) 0x01}, responseClass = CmdRFCardPowerOn.CmdRFCardPowerOnResponse.class)
public class CmdRFCardPowerOn extends CommonDeviceCommand {
    private static final int A_RFCARD = 0x0001;
    private static final int B_RFCARD = 0x0002;
    private static final int M1_RFCARD = 0x0004;
    private static final int FELICA_RFCARD = 0x0008;
    private static final int M0_CARD = 0x0010;

    @InstructionField(name = "寻卡类型", index = 0, fixLen = 2, maxLen = 2, serializer = com.newland.sdk.me.cmd.serializer.Integer2Serializer.class)
    private int rfCardType = 0x00;
    @InstructionField(name = "超时时间", index = 1, fixLen = 2, maxLen = 2, serializer = com.newland.sdk.me.cmd.serializer.Integer2Serializer.class)
    private int timeOut;
    @InstructionField(name = "sak", index = 2, fixLen = 1,maxLen = 1, serializer = com.newland.sdk.me.cmd.serializer.ByteSerializer.class)
    private byte sak = (byte) 0xff;
    @InstructionField(name = "felica卡参数", index = 3, maxLen = 128, serializer = com.newland.sdk.me.cmd.serializer.ByteArrSerializer.class)
    private byte[] felicas;

    public CmdRFCardPowerOn(RFCardType[] rfCardTypes, int timeout) {
        if (null != rfCardTypes) {
            for (RFCardType cardType : rfCardTypes) {
                if (cardType == ACARD) {
                    this.rfCardType |= A_RFCARD;
                } else if (cardType == RFCardType.BCARD) {
                    this.rfCardType |= B_RFCARD;
                } else if (cardType == RFCardType.M1CARD) {
                    this.rfCardType |= M1_RFCARD;
                } else if (cardType == RFCardType.FELICA_CARD) {
                    this.rfCardType |= FELICA_RFCARD;
                } else if (cardType == RFCardType.M0CARD) {
                    this.rfCardType |= M0_CARD;
                }
            }
        }
        this.timeOut = timeout;
    }

    public CmdRFCardPowerOn(RFCardType[] rfCardTypes, int timeout, byte sak) {
        this(rfCardTypes, timeout);
        this.sak = sak;
    }

    public CmdRFCardPowerOn(RFCardType[] rfCardTypes, int timeout, FelicaParams[] felicaParams) {
        this(rfCardTypes, timeout);
        if (felicaParams != null && felicaParams.length > 0 ) {
            felicas = new byte[felicaParams.length * 4];
            for (int i = 0; i < felicaParams.length; i++) {
                System.arraycopy(felicaParams[i].getSystemCode(), 0, felicas, i * 4, 2);
                felicas[i * 4 + 2] = felicaParams[i].getRequestCode();
                felicas[i * 4 + 3] = felicaParams[i].getTimeSlot();
            }
        }

    }

    public static class RFCardTypeSerializer extends AbstractEnumSerializer {
        public RFCardTypeSerializer() {
            super(RFCardType.class, new byte[][]{{A_RFCARD},{B_RFCARD},{M1_RFCARD},{FELICA_RFCARD},{M0_CARD}});
        }
    }

    @ResponseEntity
    public static class CmdRFCardPowerOnResponse extends AbstractSuccessResponse {
        @InstructionField(name = "卡类型", index = 1, fixLen = 2, maxLen = 2, serializer = Integer2Serializer.class)
        private int rfCardType;
        @InstructionField(name = "卡片内部序列号", index = 2, maxLen = 1024, serializer = com.newland.sdk.me.cmd.serializer.ByteArrSerializer.class)
        private byte[] cardSerialNo;
        @InstructionField(name = "ATQA", index = 3, fixLen = 2, maxLen = 2, serializer = com.newland.sdk.me.cmd.serializer.ByteArrSerializer.class)
        private byte[] ATQA;
        @InstructionField(name = "SAK", index = 4, fixLen = 1, maxLen = 1, serializer = com.newland.sdk.me.cmd.serializer.ByteSerializer.class)
        private byte SAK;
        @InstructionField(name = "Felica卡的出厂识别码(IDm)和出厂参数(PMm)", index = 5, maxLen = 512, serializer = com.newland.sdk.me.cmd.serializer.ByteArrSerializer.class)
        private byte[] IDmAndPMm;

        @InstructionField(name = "ATS数据", index = 6, maxLen = 32, serializer = ByteArrSerializer.class)
        private byte[] ats;

        public RFCardType getRFCardType() {
            if(rfCardType == A_RFCARD){
                return RFCardType.ACARD;
            }else if(rfCardType == B_RFCARD){
                return RFCardType.BCARD;
            }else if(rfCardType == M1_RFCARD){
                return RFCardType.M1CARD;
            }else if(rfCardType == FELICA_RFCARD){
                return RFCardType.FELICA_CARD;
            }else if(rfCardType == M0_CARD){
                return RFCardType.M0CARD;
            }
            return null;
        }

        public byte[] getCardSerialNo() {
            return cardSerialNo;
        }

        public byte[] getATQA() {
            if (ATQA != null && ATQA.length == 2 && Arrays.equals(ATQA, new byte[]{(byte) 0xFF, (byte) 0xFF})) {
                return null;
            } else {
                return ATQA;
            }
        }

        public byte getSAK() {
            return SAK;
        }

        public byte[] getIDmAndPMm() {
            return IDmAndPMm;
        }

        public byte[] getAts() {
            return ats;
        }

    }
}
