package com.newland.sdk.me2.module.cardreader;

import android.newland.os.NlBuild;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.newland.sdk.me.module.cardreader.K21CardReaderEvent;
import com.newland.sdk.me2.cmd.cardreader.CmdOpenCardReader;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.ProcessTimeoutException;
import com.newland.sdk.mtype.common.ErrorCode;
import com.newland.sdk.mtype.event.DeviceEventListener;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.module.cardreader.CardReaderExtParams;
import com.newland.sdk.module.cardreader.CardReaderListener;
import com.newland.sdk.module.cardreader.CardReaderModule;
import com.newland.sdk.module.cardreader.CardType;
import com.newland.sdk.module.cardreader.RFCardInfo;
import com.newland.sdk.module.cardreader.SearchCardRule;
import com.newland.sdk.module.rfcard.RFCardType;
import com.newland.sdk.module.swiper.SwiperReadModel;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.AbstractModule;
import com.newland.sdk.mtypex.cmd.DeviceResponse;
import com.newland.sdk.mtypex.conn.AbortableDeviceCommand;

import java.util.concurrent.TimeUnit;

/**
 * @author youjf
 * @description
 * @date 2019/9/3
 * @since V3.10.01
 */
public class MECardReader extends AbstractModule implements CardReaderModule {

    private DeviceLogger logger = DeviceLoggerFactory.getLogger(com.newland.sdk.me.module.cardreader.MECardReader.class);
    private AbortableDeviceCommand lastCmd = null;
    private CardType[] lastReaderTypes = null;
    protected final int CANCEL_EVENT_CODE = 0x01;

    public MECardReader(AbstractDevice owner) {
        super(owner);
    }

    @Override
    public void openCardReader(@NonNull CardType[] cardTypes, int timeout, @NonNull final CardReaderListener cardReaderListener, CardReaderExtParams cardReaderExternalParams) {
        boolean isMSDChecking = false;
        SearchCardRule searchCardRule = SearchCardRule.NORMAL;
        SwiperReadModel[] checkReadModel = null;
        RFCardType[] expectedRfCardTypes = null;
        if (cardReaderExternalParams != null) {
            if (cardReaderExternalParams.getSearchCardRule() != null) {
                searchCardRule = cardReaderExternalParams.getSearchCardRule();
            }
            if (cardReaderExternalParams.getCheckReadModel() != null) {
                checkReadModel = cardReaderExternalParams.getCheckReadModel();
                isMSDChecking = true;
            }
            if (cardReaderExternalParams.getExpectedRfCardTypes() != null) {
                expectedRfCardTypes = cardReaderExternalParams.getExpectedRfCardTypes();
            }
        }

        if (NlBuild.VERSION.MODEL.equals("N510")) {  //N510设备只支持非接卡
            cardTypes = new CardType[]{CardType.RFCARD};
        }
        CmdOpenCardReader cmdOpenCardReader = new CmdOpenCardReader("", cardTypes, expectedRfCardTypes, true, isMSDChecking, timeout);
        int invokeTimeout = timeout + 3;// pos超时上加个3秒
        lastReaderTypes = null;
        invoke(cmdOpenCardReader, (long) invokeTimeout, TimeUnit.SECONDS, new DeviceEventListener<K21CardReaderEvent>() {
            @Override
            public void onEvent(K21CardReaderEvent openCardReaderEvent, Handler handler) {

                if (openCardReaderEvent.isUserCanceled()) {
                    cardReaderListener.onCancel();
                } else if (openCardReaderEvent.isSuccess()) {
                    switch (openCardReaderEvent.getOpenCardReaderResult().getResponseCardTypes()[0]) {
                        case MSGCARD:
                            boolean isSuccessful = openCardReaderEvent.getOpenCardReaderResult().isMSDDataCorrectly();
                            cardReaderListener.onFindMagCard(isSuccessful);
                            break;
                        case ICCARD:
                            cardReaderListener.onFindICCard();
                            break;
                        case RFCARD:
                            RFCardType rfCardType = openCardReaderEvent.getOpenCardReaderResult().getResponseRFCardType();
                            byte sak = 0x00;
                            if (rfCardType == RFCardType.M1CARD) {
                                sak = openCardReaderEvent.getOpenCardReaderResult().getSAK();
                            }
                            RFCardInfo rfCardInfo = new RFCardInfo(sak, openCardReaderEvent.getOpenCardReaderResult().getSnr(), null);
                            cardReaderListener.onFindRFCard(rfCardType, rfCardInfo);
                            break;
                        default:
                            break;
                    }
                } else if (openCardReaderEvent.isFailed()) {
                    if (openCardReaderEvent.getException().getCause() instanceof ProcessTimeoutException) {
                        cardReaderListener.onTimeout();
                        return;
                    }
                    cardReaderListener.onError(ErrorCode.OPEN_CARDER_ERROR, openCardReaderEvent.getException().getMessage());
                    return;
                } else if (openCardReaderEvent.getOpenCardReaderResult() == null || (openCardReaderEvent.getOpenCardReaderResult().getResponseCardTypes()) == null || openCardReaderEvent.getOpenCardReaderResult().getResponseCardTypes().length <= 0) {
                    cardReaderListener.onError(ErrorCode.OPEN_CARDER_NULL, "open card reader failed,respond data is null");
                } else {
                    cardReaderListener.onError(ErrorCode.OPEN_CARDER_ERROR, "open card reader failed");
                }
            }

            @Override
            public Handler getUIHandler() {
                return null;
            }
        }, new EventMaker<K21CardReaderEvent>() {
            @Override
            public K21CardReaderEvent makeEvent(DeviceResponse deviceResponse) {
                K21CardReaderEvent event = null;
                try {
                    DeviceResponse response = dealDevResp(deviceResponse);
                    if (response == null) {
                        event = new K21CardReaderEvent();
                    } else if (response instanceof CmdOpenCardReader.CmdCardreaderNotificationResponse) {
                        CmdOpenCardReader.CmdCardreaderNotificationResponse notificationResponse = (CmdOpenCardReader.CmdCardreaderNotificationResponse) response;
                        event = new K21CardReaderEvent(notificationResponse.getReturnKey());
                    } else {
                        CmdOpenCardReader.CmdOpenCardReaderResponse cmdResponse = (CmdOpenCardReader.CmdOpenCardReaderResponse) response;
                        lastReaderTypes = cmdResponse.getCardInputTypes();
                        event = new K21CardReaderEvent(cmdResponse.getOpenCardReaderResult());
                    }
                } catch (Exception e) {
                    event = new K21CardReaderEvent(e);
                }
                return event;
            }

        });
        lastCmd = cmdOpenCardReader;
    }

    @Override
    public void cancelCardReader() {
        if (lastCmd != null) {
            AbortableDeviceCommand tmp = lastCmd;
            lastCmd = null;
            tmp.abort(CANCEL_EVENT_CODE);
        }
    }

    @Override
    public CardType[] getLastReaderTypes() {
        return lastReaderTypes;
    }

    @Override
    public void setLastReaderTypes(CardType[] cardTypes) {
        lastReaderTypes = cardTypes;
    }

    @Override
    public boolean isStandardModule() {
        return true;
    }

    @Override
    public ModuleType getStandardModuleType() {
        return ModuleType.COMMON_CARDREADER;
    }

    @Override
    public String getExModuleType() {
        return null;
    }
}
