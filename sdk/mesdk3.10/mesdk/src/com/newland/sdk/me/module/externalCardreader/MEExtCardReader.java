package com.newland.sdk.me.module.externalCardreader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;

import com.newland.sdk.me.module.externalPininput.PinpadPackage;
import com.newland.sdk.module.cardreader.CardReaderExtParams;
import com.newland.sdk.module.cardreader.CardReaderListener;
import com.newland.sdk.module.cardreader.CardType;
import com.newland.sdk.module.externalCardreader.ExtCardReaderModule;
import com.newland.sdk.module.externalPin.PinpadInitExtParams;
import com.newland.sdk.module.rfcard.RFCardType;
import com.newland.sdk.module.serialport.PinpadModel;
import com.newland.sdk.mtype.ExModuleType;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.common.ErrorCode;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.AbstractModule;
import com.newland.sdk.utils.ISOUtils;

import java.util.Arrays;
import java.util.List;

public class MEExtCardReader extends AbstractModule implements ExtCardReaderModule {

    private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger("MEExtCardReader");
    private PinpadPackage mPinpadPackage;
    private static final int TIMEOUT = 5000;
    private static final byte NAK = 0x15;
    private PinpadModel pinpadModel = PinpadModel.SP_OVERSEAS;
    private CardType mCardType;//最近一次识别到的卡类型;
    private static boolean globalCheckUnionCard = true; // 设置是否校验银行卡二磁道规则

    public MEExtCardReader(AbstractDevice device, Context context) {
        super(device);
        mPinpadPackage = PinpadPackage.getInstance(device, context);
        pinpadModel = mPinpadPackage.getModel();
    }

    @Override
    public boolean init(@NonNull PinpadInitExtParams params) {
        boolean rs = mPinpadPackage.init(params, false);
        pinpadModel = mPinpadPackage.getModel();
        return rs;
    }

    @Override
    public void openCardReader(@NonNull final CardType[] cardTypes, final int timeout, @NonNull final CardReaderListener cardReaderListener, final CardReaderExtParams cardReaderExtParams) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    devicelogger.debug("[openCardReader] cardTypes:" + cardTypes + "; timeout:" + timeout);
                    mCardType = null;
                    MEExtCardReader.globalCheckUnionCard = true;
                    byte[] messageType = new byte[]{0x42, 0x42}; //"BB"

                    byte[] line1 = null, line2 = null, line3 = null, line4 = null;
                    try {
                        if (cardReaderExtParams != null) {
                            String messageEncode = cardReaderExtParams.getMessageEncode();
                            if (messageEncode == null || messageEncode.equals("")) {
                                messageEncode = "GB2312";
                            }
                            if (cardReaderExtParams.getFirstLineMessage() != null) {
                                line1 = cardReaderExtParams.getFirstLineMessage().getBytes(messageEncode);
                            }
                            if (cardReaderExtParams.getSecondLineMessage() != null) {
                                line2 = cardReaderExtParams.getSecondLineMessage().getBytes(messageEncode);
                            }
                            if (cardReaderExtParams.getThirdLineMessage() != null) {
                                line3 = cardReaderExtParams.getThirdLineMessage().getBytes(messageEncode);
                            }
                            if (cardReaderExtParams.getFourthLineMessage() != null) {
                                line4 = cardReaderExtParams.getFourthLineMessage().getBytes(messageEncode);
                            }
                            MEExtCardReader.globalCheckUnionCard = cardReaderExtParams.isCheckUnionCard();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    int textLength = 0;
                    if (line1 != null) {
                        textLength += line1.length;
                    }
                    if (line2 != null) {
                        textLength += line2.length;
                    }
                    if (line3 != null) {
                        textLength += line3.length;
                    }
                    if (line4 != null) {
                        textLength += line4.length;
                    }

                    byte[] reqData = new byte[10 + textLength];
                    byte swiper = 0x00;
                    byte iccard = 0x00;
                    byte rfcard = 0x00;
                    for (CardType moduleType : cardTypes) {
                        if (moduleType == CardType.MSGCARD) {
                            swiper = 0x01;
                        } else if (moduleType == CardType.ICCARD) {
                            iccard = 0x01;
                        } else if (moduleType == CardType.RFCARD) {
                            rfcard = 0x01;
                        }
                    }

                    if (rfcard == 0x00) {
                        reqData = new byte[9 + textLength];
                    }
                    reqData[0] = rfcard;
                    reqData[1] = iccard;
                    reqData[2] = swiper;
                    int i = 0;
                    if (rfcard == 0x01) {
                        i++;
                        reqData[3] = 0x02;//0x00 - TYPE-A; 0x01 - TYPE-B; 0x02 - TYPE-A||TYPE-B
                    }

                    byte[] timeoutByte = InnerUtils.intToBytes(timeout, 2, true);
                    System.arraycopy(timeoutByte, 0, reqData, 3 + i, timeoutByte.length);

                    int line1Length = 0, line2Length = 0, line3Length = 0, line4Length = 0;
                    if (line1 != null) {
                        System.arraycopy(line1, 0, reqData, 5 + i, line1.length);
                        line1Length = line1.length;
                    }
                    reqData[5 + i + line1Length] = (byte) 0x1C;

                    if (line2 != null) {
                        System.arraycopy(line2, 0, reqData, 6 + i + line1Length, line2.length);
                        line2Length = line2.length;
                    }
                    reqData[6 + i + line1Length + line2Length] = (byte) 0x1C;

                    if (line3 != null) {
                        System.arraycopy(line3, 0, reqData, 7 + i + line1Length + line2Length, line3.length);
                        line3Length = line3.length;
                    }
                    reqData[7 + i + line1Length + line2Length + line3Length] = (byte) 0x1C;

                    if (line4 != null) {
                        System.arraycopy(line4, 0, reqData, 8 + i + line1Length + line2Length + line3Length, line4.length);
                        line4Length = line4.length;
                    }
                    reqData[8 + i + line1Length + line2Length + line3Length + line4Length] = (byte) 0x1C;

                    byte[] resp = mPinpadPackage.sendPinpadCmd(messageType, reqData, (timeout) * 1000 + PinpadPackage.EXTCMD_OFFSETTIME_MS * 2, true);//应用读的超时时间多1秒，才能收到键盘超时的响应码
                    devicelogger.debug("[openCardReader] result:" + (resp == null ? "null" : ISOUtils.hexString(resp)));
                    if (resp == null || resp[0] == NAK) {
                        devicelogger.error("[openCardReader]openCardReader failure");
                        cardReaderListener.onError(ErrorCode.OPEN_CARDER_NULL, "open card reader failed,respond data is null");
                    } else {
                        mPinpadPackage.getPinpadRspCode();
                        if (Arrays.equals(new byte[]{0x42, 0x43}, new byte[]{resp[0], resp[1]})) {
                            if (Arrays.equals(new byte[]{0x30, 0x30}, new byte[]{resp[3], resp[4]})) {
                                if (0x00 == resp[5]) {
                                    // String atq=new String(new byte[]{resp[6],resp[7]});
                                    devicelogger.debug("[openCardReader]识别RF card atq");
                                    mCardType = CardType.RFCARD;
                                    cardReaderListener.onFindRFCard(null, null);
                                } else if (0x01 == resp[5]) {
                                    devicelogger.debug("[openCardReader]识别IC card atq");
                                    mCardType = CardType.ICCARD;
                                    cardReaderListener.onFindICCard();
                                } else if (0x02 == resp[5]) {
                                    devicelogger.debug("[openCardReader]识别MAG card atq");
                                    mCardType = CardType.MSGCARD;
                                    cardReaderListener.onFindMagCard(true);
                                }

                            } else if (Arrays.equals(new byte[]{0x30, 0x31}, new byte[]{resp[3], resp[4]})) {
                                devicelogger.error("[openCardReader]onTimeout");
                                cardReaderListener.onTimeout();
                            } else if (Arrays.equals(new byte[]{0x30, 0x32}, new byte[]{resp[3], resp[4]})) {
                                devicelogger.error("[openCardReader]30 32");
                                cardReaderListener.onError(ErrorCode.OPEN_CARDER_ERROR, "IC card read error");
                            } else if (Arrays.equals(new byte[]{0x30, 0x35}, new byte[]{resp[3], resp[4]})) {
                                devicelogger.error("[openCardReader]30 35 ");
                                cardReaderListener.onCancel();
                            }  else if (Arrays.equals(new byte[]{0x34, 0x36}, new byte[]{resp[3], resp[4]})) {
                                devicelogger.error("[openCardReader]34 36，键盘按键取消 ");
                                cardReaderListener.onCancel();
                            } else {
                                cardReaderListener.onError(ErrorCode.OPEN_CARDER_ERROR, "unknow error");
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    cardReaderListener.onError(ErrorCode.OPEN_CARDER_ERROR, "Exception:"+e);
                }
            }
        }).start();


    }


    @Override
    public void cancelCardReader() {
        try {
            mCardType = null;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    devicelogger.debug("[cancelCardReader]");
                    byte[] messageType = new byte[]{0x42, 0x46}; //
//                    byte[] resp = mPinpadPackage.sendPinpadCmd(messageType,null,PinpadPackage.EXTCMD_TIMEOUT_MS,false);//
//                    devicelogger.debug("[cancelCardReader]cancel CardReader result:" + (resp == null ? "null" : ISOUtils.hexString(resp)));
                    mPinpadPackage.unblockSendCmd(messageType, null);
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CardType[] getLastReaderTypes() {
        return new CardType[]{mCardType};
    }

    public static boolean isGlobalCheckUnionCard() {
        return globalCheckUnionCard;
    }

    public static void setGlobalCheckUnionCard(boolean globalCheckUnionCard) {
        MEExtCardReader.globalCheckUnionCard = globalCheckUnionCard;
    }

    @Override
    public boolean isStandardModule() {
        return false;
    }

    @Override
    public ModuleType getStandardModuleType() {
        return null;
    }

    @Override
    public String getExModuleType() {
        return ExModuleType.CARDREADER;
    }
}
