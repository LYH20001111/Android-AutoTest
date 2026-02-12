package com.newland.sdk.me.cmd.cardreader;

import com.newland.sdk.me.cmd.CmdCancelAndReset;
import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.me.cmd.serializer.ByteSerializer;
import com.newland.sdk.me.cmd.serializer.IntegerSerializer;
import com.newland.sdk.me.module.cardreader.OpenCardReaderResult;
import com.newland.sdk.module.cardreader.CardType;
import com.newland.sdk.module.cardreader.SearchCardRule;
import com.newland.sdk.module.rfcard.FelicaParams;
import com.newland.sdk.module.rfcard.RFCardType;
import com.newland.sdk.module.swiper.SwiperReadModel;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.mtypex.cmd.AbstractNotificationResponse;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.DeviceCommand;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.AbortableDeviceCommand;
import com.newland.sdk.mtypex.serializer.AbstractEnumSerializer;
import com.newland.sdk.utils.ISOUtils;

import java.util.ArrayList;
import java.util.List;

@CommandEntity(cmdCode = {(byte) 0xD1, (byte) 0x01}, responseClass = CmdOpenCardReader.CmdOpenCardReaderResponse.class, notificationResponseClass = CmdOpenCardReader.CmdCardreaderNotificationResponse.class)
public class CmdOpenCardReader extends AbortableDeviceCommand {

    private DeviceLogger logger = DeviceLoggerFactory.getLogger(CmdOpenCardReader.class);

    public static final int MASK_SWIPER = 0x01;

    public static final int MASK_ICCARD = 0x02;

    public static final int MASK_RFCARD = 0x04;

    private static final int A_RFCARD = 0x01;
    private static final int B_RFCARD = 0x02;
    private static final int M1_RFCARD = 0x04;
    private static final int FELICA_RFCARD = 0x08;
    private static final int M0_RFCARD = 0x10;

    private static final int CHECK_FIRST_TRACK = 0x10;

    private static final int CHECK_SECOND_TRACK = 0x20;

    private static final int CHECK_THIRD_TRACK = 0x40;

    public static class SearchCardRuleSerializer extends AbstractEnumSerializer {

        public SearchCardRuleSerializer() {
            super(SearchCardRule.class, new byte[][]{{0x03}, {0x01}, {0x04}, {0x02}, {0x05},{0x06}});
        }
    }

    @InstructionField(name = "读卡模式", index = 0, fixLen = 1, maxLen = 1, serializer = ByteSerializer.class)
    private byte modelMask = 0x00;

    @InstructionField(name = "期望非接卡类型", index = 1, fixLen = 1, maxLen = 1, serializer = ByteSerializer.class)
    private byte expectedRfCardTypes = 0x00;

    @InstructionField(name = "超时时间", index = 2, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
    private int timeout;

    @InstructionField(name = "是否判断二磁道有效性", index = 3, fixLen = 1, maxLen = 1, serializer = ByteSerializer.class)
    private byte secondTrackEffective = 0x20; // 0x10/0x20/0x40的组合
    @InstructionField(name = "非接寻卡有效次数", index = 4, fixLen = 1, maxLen = 1, serializer = ByteArrSerializer.class)
    private byte[] effectivetimes = InnerUtils.intToBytes(2, 1, true);

    @InstructionField(name = "寻卡时间间隔", index = 5, fixLen = 2, maxLen = 2, serializer = ByteArrSerializer.class)
    private byte[] intervaltimes = InnerUtils.intToBytes(300, 2, true);

    @InstructionField(name = "是否识别具体非接卡类型", index = 6, fixLen = 1, maxLen = 1, serializer = SearchCardRuleSerializer.class)
    private SearchCardRule searchCardRule;

    @InstructionField(name = "felica卡参数", index = 7, maxLen = 128, serializer = ByteArrSerializer.class)
    private byte[] felicas;

    @InstructionField(name = "enablePreParam",  index = 8, fixLen = 1, maxLen = 1, serializer = ByteSerializer.class)
    private byte enablePreParam;

    @InstructionField(name = "vasEnable", index = 9, fixLen = 1, maxLen = 1, serializer = ByteSerializer.class)
    private byte vasEnable = 0x00;
    @InstructionField(name = "vasParam", index = 10, maxLen = 128, serializer = ByteArrSerializer.class)
    private byte[] vasParam;

    public CmdOpenCardReader() {

    }

    public CmdOpenCardReader(CardType[] openReaders, boolean isMSDChecking, SwiperReadModel[] checkReadModel, RFCardType[] expectedRfCardTypes, int timeout, SearchCardRule searchCardRule, FelicaParams[] felicaParams,boolean vasEnable,byte[] vasParams, boolean checkUnionCard,boolean enablePreParam) {
        getCmdOpenCardReader(openReaders, isMSDChecking, checkReadModel, expectedRfCardTypes, timeout, searchCardRule, felicaParams, vasEnable,vasParams,checkUnionCard,enablePreParam);
    }

    public byte[] getCmdOpenCardReader(CardType[] openReaders, boolean isMSDChecking, SwiperReadModel[] checkReadModel, RFCardType[] expectedRfCardTypes, int timeout, SearchCardRule searchCardRule, FelicaParams[] felicaParams, boolean vasEnable,byte[] vasParams,boolean checkUnionCard,boolean enablePreParam) {
        for (CardType moduleType : openReaders) {
            if (moduleType == CardType.MSGCARD) {
                modelMask |= MASK_SWIPER;
            } else if (moduleType == CardType.ICCARD) {
                modelMask |= MASK_ICCARD;
            } else if (moduleType == CardType.RFCARD) {
                modelMask |= MASK_RFCARD;
            } else {
                throw new IllegalArgumentException("not supported operation!");
            }
        }
        if (null != expectedRfCardTypes) {
            for (RFCardType oct : expectedRfCardTypes) {
                if (oct == RFCardType.ACARD) {
                    this.expectedRfCardTypes |= A_RFCARD;
                } else if (oct == RFCardType.BCARD) {
                    this.expectedRfCardTypes |= B_RFCARD;
                } else if (oct == RFCardType.M1CARD) {
                    this.expectedRfCardTypes |= M1_RFCARD;
                } else if (oct == RFCardType.FELICA_CARD) {
                    this.expectedRfCardTypes |= FELICA_RFCARD;
                } else if (oct == RFCardType.M0CARD) {
                    this.expectedRfCardTypes |= M0_RFCARD;
                }
            }
        }
        if (checkReadModel != null && checkReadModel.length > 0) {
            for (SwiperReadModel readModel : checkReadModel) {
                if (readModel == SwiperReadModel.FIRST_TRACK) {
                    secondTrackEffective |= CHECK_FIRST_TRACK;
                } else if (readModel == SwiperReadModel.SECOND_TRACK) {
                    secondTrackEffective |= CHECK_SECOND_TRACK;
                } else if (readModel == SwiperReadModel.THIRD_TRACK) {
                    secondTrackEffective |= CHECK_THIRD_TRACK;
                } else {
                    throw new IllegalArgumentException("not supported operation!");
                }
            }
        }

        if (timeout > 255) {
            throw new IllegalArgumentException("not supported timeout!" + timeout);
        }
        this.timeout = timeout;

        if (checkUnionCard) {
            secondTrackEffective |= 0x0F;
        }
        if (!isMSDChecking) {
            secondTrackEffective = 0x00;
        }
        if (null == searchCardRule || searchCardRule == SearchCardRule.RFCARD_QUICKLY) {
            this.searchCardRule = SearchCardRule.RFCARD_QUICKLY;
            this.effectivetimes = InnerUtils.intToBytes(1, 1, true);
        } else
            this.searchCardRule = searchCardRule;

        if (felicaParams != null && felicaParams.length > 0) {
            felicas = new byte[felicaParams.length * 4];
            for (int i = 0; i < felicaParams.length; i++) {
                System.arraycopy(felicaParams[i].getSystemCode(), 0, felicas, i * 4, 2);
                felicas[i * 4 + 2] = felicaParams[i].getRequestCode();
                felicas[i * 4 + 3] = felicaParams[i].getTimeSlot();
            }
        }
        if(enablePreParam){
            this.enablePreParam = 1;
        }else {
            this.enablePreParam = 0;
        }

        if(vasEnable){
            this.vasEnable = 1;
            this.vasParam = vasParams;
        }else {
            this.vasEnable = 0;
        }

        int offset = 0;
        byte[] cmd = new byte[14];
        cmd[offset] = modelMask;
        offset++;//读卡模式
        cmd[offset] = this.expectedRfCardTypes;
        offset++;//期望非接卡类型
        cmd[offset] = InnerUtils.intToBytes(timeout, 1, true)[0];
        offset++;//超时时间
        cmd[offset] = secondTrackEffective;
        offset++;//是否判断二磁道有效性
        cmd[offset] = InnerUtils.intToBytes(2, 1, true)[0];
        offset++;//非接寻卡有效次数
        System.arraycopy(InnerUtils.intToBytes(300, 2, true), 0, cmd, offset, 2);
        offset += 2;//寻卡时间间隔
        cmd[offset] = 0x03;
        offset++;//寻卡策略
        cmd[offset] = 0x00;
        offset++;//felica卡参数长度
        cmd[offset] = 0x00;
        offset++;//felica卡参数长度
        if(enablePreParam){
            cmd[offset] = 1;
        }else {
            cmd[offset] = 0;
        }

        offset++;
        cmd[offset] = 0x00;//vas enable

        offset++;
        cmd[offset] = 0x00;
        offset++;//vas卡参数长度
        cmd[offset] = 0x00;
        offset++;//vas卡参数长度
        return cmd;
    }


    @Override
    public DeviceCommand getAbortCommand() {
        return new CmdCancelAndReset();
    }

    @ResponseEntity
    public static final class CmdOpenCardReaderResponse extends AbstractSuccessResponse {

        /**
         *
         */
        private static final long serialVersionUID = 7980345612313197152L;

        @InstructionField(name = "开启的读卡模式", index = 0, fixLen = 1, maxLen = 1, serializer = ByteSerializer.class)
        private byte modelMask;
        @InstructionField(name = "刷卡结果", index = 1, maxLen = 1, serializer = ByteSerializer.class)
        private Byte cardResultType;
        @InstructionField(name = "SNR", index = 2, maxLen = 256, serializer = ByteArrSerializer.class)
        private byte[] snr;
        @InstructionField(name = "sak", index = 3, fixLen = 1, maxLen = 1, serializer = ByteSerializer.class)
        private byte sak;
        @InstructionField(name = "Felica卡的出厂识别码(IDm)和出厂参数(PMm)", index = 4, maxLen = 512, serializer = ByteArrSerializer.class)
        private byte[] IDmAndPMm;
        @InstructionField(name = "atqa", index = 5, maxLen = 256, serializer = ByteArrSerializer.class)
        private byte[] atqa;

        public CardType[] getCardInputTypes() {
            List<CardType> moduleTypes = new ArrayList<CardType>();
            if ((modelMask & MASK_SWIPER) != 0) {
                moduleTypes.add(CardType.MSGCARD);
            } else if ((modelMask & MASK_ICCARD) != 0) {
                moduleTypes.add(CardType.ICCARD);
            } else if ((modelMask & MASK_RFCARD) != 0) {
                moduleTypes.add(CardType.RFCARD);
            }

            return moduleTypes.toArray(new CardType[moduleTypes.size()]);
        }

        public OpenCardReaderResult getOpenCardReaderResult() {
            try {
                List<CardType> moduleTypes = new ArrayList<CardType>();
                RFCardType rfCardType = null;
                boolean isMSDDataCorrectly = true;

                if ((modelMask & MASK_SWIPER) != 0) {
                    moduleTypes.add(CardType.MSGCARD);
                } else if ((modelMask & MASK_ICCARD) != 0) {
                    moduleTypes.add(CardType.ICCARD);
                } else if ((modelMask & MASK_RFCARD) != 0) {
                    moduleTypes.add(CardType.RFCARD);
                }

                if (0x11 == (int) (modelMask & 0xFF) || (null != cardResultType && (0x11 == (int) (cardResultType & 0xFF)))) {// 刷卡结束,但刷卡错误,建议重刷
                    isMSDDataCorrectly = false;
                }
                if (null == cardResultType) {
                    rfCardType = null;
                } else if ((byte) A_RFCARD == cardResultType) {
                    rfCardType = RFCardType.ACARD;
                } else if ((byte) B_RFCARD == cardResultType) {
                    rfCardType = RFCardType.BCARD;
                } else if ((byte) M1_RFCARD == cardResultType) {
                    rfCardType = RFCardType.M1CARD;
                } else if ((byte) FELICA_RFCARD == cardResultType) {
                    rfCardType = RFCardType.FELICA_CARD;
                } else if ((byte) M0_RFCARD == cardResultType) {
                    rfCardType = RFCardType.M0CARD;
                }

                OpenCardReaderResult result = new OpenCardReaderResult(moduleTypes.toArray(new CardType[moduleTypes.size()]), rfCardType, isMSDDataCorrectly, snr, sak, IDmAndPMm);
                result.setAtqa(atqa);
                return result;
            } catch (Exception e) {
                return null;
            }
        }
    }

    @ResponseEntity
    public static class CmdCardreaderNotificationResponse extends AbstractNotificationResponse {

        private static final long serialVersionUID = 7980345612313196552L;
        @InstructionField(name = "读卡方式及按键", index = 0, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
        private int returnKey;

        public int getReturnKey() {
            return returnKey;
        }
    }
}
