package com.newland.sdk.me.module.cardreader;

import android.newland.os.NlBuild;
import android.os.Handler;
import android.util.Log;

import com.newland.intelligent.jni.JniCmdInterface;
import com.newland.ndk.NdkApiManager;
import com.newland.ndk.Print;
import com.newland.sdk.mtypex.module.common.emv.CommonUtils;
import com.newland.sdk.me.cmd.cardreader.CmdOpenCardReader;
import com.newland.sdk.me.module.emv.EMVInnerUtils;
import com.newland.sdk.module.light.IndicatorLightModule;
import com.newland.sdk.module.light.LightColor;
import com.newland.sdk.module.light.LightState;
import com.newland.sdk.mtype.Device;
import com.newland.sdk.mtype.DeviceInvokeException;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.ProcessTimeoutException;
import com.newland.sdk.mtype.common.ErrorCode;
import com.newland.sdk.mtype.common.ErrorMsg;
import com.newland.sdk.mtype.common.ErrorMsgHelper;
import com.newland.sdk.mtype.event.DeviceEventListener;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.module.cardreader.CardReaderExtParams;
import com.newland.sdk.module.cardreader.CardReaderListener;
import com.newland.sdk.module.cardreader.CardReaderModule;
import com.newland.sdk.module.cardreader.CardType;
import com.newland.sdk.module.cardreader.RFCardInfo;
import com.newland.sdk.module.cardreader.SearchCardRule;
import com.newland.sdk.module.rfcard.FelicaParams;
import com.newland.sdk.module.rfcard.RFCardType;
import com.newland.sdk.module.swiper.SwiperReadModel;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.AbstractModule;
import com.newland.sdk.mtypex.cmd.DeviceResponse;
import com.newland.sdk.mtypex.conn.AbortableDeviceCommand;
import com.newland.sdk.utils.ISOUtils;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class MECardReader extends AbstractModule implements CardReaderModule {

    private DeviceLogger logger = DeviceLoggerFactory.getLogger("MECardReader");
    private AbortableDeviceCommand lastCmd = null;
    private CardType[] lastReaderTypes = null;
    protected final int CANCEL_EVENT_CODE = 0x01;
    private Object openCloseSync = new Object();
    private boolean isNeedCancel = false;
    private IndicatorLightModule indicatorLightModule;
    private Device device;

    public MECardReader(AbstractDevice owner) {
        super(owner);
        this.device = owner;
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

    @Override
    public void openCardReader(CardType[] cardTypes, int timeout, final CardReaderListener cardReaderListener, CardReaderExtParams cardReaderExternalParams) {
        Log.d("SDKVersion", "openCardReader,SDKVersion:"+ CommonUtils.getInstance().getSDKVersion());
        logger.debug("[openCardReader]timeout:" + timeout + "; cardTypes:" + cardTypes + "; cardReaderListener:" + cardReaderListener);
        if(getStatus() == 8 && cardReaderListener != null)  {
            cardReaderListener.onError(ErrorCode.OPEN_CARDER_ERROR,"Printer is busy.");
            return;
        }
        synchronized (openCloseSync) {
            try {
                boolean isSupport = EMVInnerUtils.getIndicatorsAndBeep();
                logger.debug("[openCardReader]isSupport beep:" + isSupport);
                if (isSupport) {
                    //欧洲visa要求寻卡第一个灯常亮
                    indicatorLightModule = (IndicatorLightModule) device.getStandardModule(ModuleType.INDICATOR_LIGHT);
                    indicatorLightModule.operateLight(new LightColor[]{LightColor.GREEN, LightColor.YELLOW, LightColor.RED}, LightState.TURNOFF);
                    indicatorLightModule.operateLight(new LightColor[]{LightColor.BLUE}, LightState.TURNON);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            boolean isMSDChecking = false;
            SearchCardRule searchCardRule = SearchCardRule.RFCARD_QUICKLY;
            SwiperReadModel[] checkReadModel = null;
            RFCardType[] expectedRfCardTypes = null;
            FelicaParams[] felicaParams = null;
            byte[] vasParams = null;
            boolean vasEnable = false;
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
                if (cardReaderExternalParams.getFelicaParams() != null) {
                    felicaParams = cardReaderExternalParams.getFelicaParams();
                }

                vasEnable = cardReaderExternalParams.isVasEnable();
                if (cardReaderExternalParams.getVasParams() != null) {
                    vasParams = cardReaderExternalParams.getVasParams();
                }
            }

            if (NlBuild.VERSION.MODEL.equals("N510")) {  //N510设备只支持非接卡
                cardTypes = new CardType[]{CardType.RFCARD};
            }
            lastReaderTypes = null;
            if (searchCardRule == SearchCardRule.CARD_DETECT) {
                final byte[] cmd = new byte[2 + 11];
                cmd[0] = (byte) 0xD1;
                cmd[1] = 0x01;
                byte[] param = new CmdOpenCardReader().getCmdOpenCardReader(cardTypes, isMSDChecking, checkReadModel, expectedRfCardTypes, timeout, searchCardRule, felicaParams,vasEnable,vasParams,
                        (null == cardReaderExternalParams ? true : cardReaderExternalParams.isCheckUnionCard()),(null == cardReaderExternalParams ? false : cardReaderExternalParams.isEnablePreParam()));
                System.arraycopy(param, 0, cmd, 2, param.length);
                Log.d("libsdk0", "openCardReader: startThread");
                SDKExecutors.startThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.d("libsdk0", "openCardReader: currentThread name=" + Thread.currentThread().getName());
                            final byte[] out = new byte[32];
                            final int[] outLen = new int[1];
                            int ret = JniCmdInterface.getInstance().jniMposLibCmd(cmd, cmd.length, out, outLen);
                            if (Arrays.equals(new byte[]{0x30, 0x30}, new byte[]{out[0], out[1]})) {
                                byte modelMask = out[2];
                                byte cardResultType = out[3];
                                if ((modelMask & CmdOpenCardReader.MASK_SWIPER) != 0) {
                                    boolean isMSDDataCorrectly = true;
                                    if (0x11 == (int) (modelMask & 0xFF) || ((0x11 == (int) (cardResultType & 0xFF)))) {// 刷卡结束,但刷卡错误,建议重刷
                                        isMSDDataCorrectly = false;
                                    }
                                    lastReaderTypes = new CardType[]{CardType.MSGCARD};
                                    cardReaderListener.onFindMagCard(isMSDDataCorrectly);
                                } else if ((modelMask & CmdOpenCardReader.MASK_ICCARD) != 0) {
                                    lastReaderTypes = new CardType[]{CardType.ICCARD};
                                    cardReaderListener.onFindICCard();
                                } else if ((modelMask & CmdOpenCardReader.MASK_RFCARD) != 0) {
                                    lastReaderTypes = new CardType[]{CardType.RFCARD};
                                    cardReaderListener.onFindRFCard(null, null);
                                }
                            } else if (Arrays.equals(new byte[]{0x31, 0x30}, new byte[]{out[0], out[1]})) {
                                cardReaderListener.onCancel();
                            } else if (Arrays.equals(new byte[]{0x30, 0x37}, new byte[]{out[0], out[1]})) {
                                cardReaderListener.onTimeout();
                            } else {
                                int command = (cmd[0] << 8 | cmd[1]);
                                ErrorMsg msg = ErrorMsgHelper.getInstance().getErrorMsg(command);
                                DeviceInvokeException invokeException = new DeviceInvokeException("-1", "ErrCode:" + msg.getErrCode() + " ErrMsg:" + msg.getErrMsg());
                                logger.error("[openCardReader] requestData=" + (null == cmd ? "null" : InnerUtils.hexString(cmd)));
                                logger.error("[openCardReader] responseData:" + (null == out ? "null" : InnerUtils.hexString(out)));
                                logger.error("[openCardReader] ErrCode:" + msg.getErrCode() + " ErrMsg:" + msg.getErrMsg() + " OtherMsg:" + msg.getOtherMsg());
                                cardReaderListener.onError(ErrorCode.OPEN_CARDER_ERROR, msg.getErrCode() + " ErrMsg:" + msg.getErrMsg() + " OtherMsg:" + msg.getOtherMsg());
                            }
                        } catch (Exception e) {
                            cardReaderListener.onError(ErrorCode.OPEN_CARDER_ERROR, "open card reader failed");
                            e.printStackTrace();
                        }
                    }
                });
                CardReaderHelper.openCardReaderWait();
                isNeedCancel = true;
                return;
            }
            CmdOpenCardReader cmdOpenCardReader = new CmdOpenCardReader(cardTypes, isMSDChecking, checkReadModel, expectedRfCardTypes, timeout, searchCardRule, felicaParams,vasEnable,vasParams,
                    (null == cardReaderExternalParams ? true : cardReaderExternalParams.isCheckUnionCard()),(null == cardReaderExternalParams ? false : cardReaderExternalParams.isEnablePreParam()));
            int invokeTimeout = timeout + 3;// pos超时上加个3秒
            SearchCardRule finalSearchCardRule = searchCardRule;
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
                                if (rfCardType == RFCardType.M1CARD || finalSearchCardRule == SearchCardRule.RFCARD_QUICKLY) {
                                    sak = openCardReaderEvent.getOpenCardReaderResult().getSAK();
                                }
                                byte[] atqa =  openCardReaderEvent.getOpenCardReaderResult().getAtqa();
                               logger.debug("atqa:"+(atqa==null?null: ISOUtils.hexString(atqa)));
                                RFCardInfo rfCardInfo = new RFCardInfo(sak, openCardReaderEvent.getOpenCardReaderResult().getSnr(), openCardReaderEvent.getOpenCardReaderResult().getIDmAndPmm());
                                rfCardInfo.setAtqa(atqa);
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
            CardReaderHelper.openCardReaderWait();
        }
    }

    @Override
    public void cancelCardReader() {
        logger.debug("[cancelCardReader]");
        synchronized (openCloseSync) {
            if (isNeedCancel) {
                JniCmdInterface.getInstance().jniMposLibCmdCancel(1);
                isNeedCancel = false;
            }
            if (lastCmd != null) {
                AbortableDeviceCommand tmp = lastCmd;
                lastCmd = null;
                tmp.abort(CANCEL_EVENT_CODE);
            }
        }
    }

    @Override
    public CardType[] getLastReaderTypes() {
        return lastReaderTypes;
    }

    public void setLastReaderTypes(CardType[] lastReaderTypes) {
        this.lastReaderTypes = lastReaderTypes;
    }

    public int getStatus() {
        try {
            int[] status = new int[1];
            Print print = NdkApiManager.getNdkApiManager().getPrint();
            int ret = print.NDK_PrnGetStatus(status);
            logger.debug("[getStatus] 3.10 ret="+ret+" status[0]="+status[0]);
            if(ret == 0){
                return status[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
