package com.newland.sdkdemo.fragment;

import android.content.Context;

import com.newland.nsdk.cardemulation.CardDetector;
import com.newland.nsdk.cardemulation.CardDetectorImpl;
import com.newland.nsdk.cardemulation.CardEmulator;
import com.newland.nsdk.cardemulation.CardEmulatorImpl;
import com.newland.nsdk.cardemulation.utils.CEStateType;
import com.newland.nsdk.cardemulation.utils.CardEmulatorListener;
import com.newland.nsdk.cardemulation.utils.CardEmulatorState;
import com.newland.nsdk.cardemulation.utils.EmulateCardConfig;
import com.newland.nsdk.cardemulation.utils.EmulateCardFileType;
import com.newland.nsdk.cardemulation.utils.EmulateCardType;
import com.newland.nsdk.cardemulation.utils.MagCardResult;
import com.newland.nsdk.cardemulation.utils.PollingCardsListener;
import com.newland.sdk.utils.ISOUtils;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.adapter.LayoutMode;
import com.newland.sdkdemo.annotation.MethodGridEntity;

import java.nio.charset.StandardCharsets;

public class CardEmulatorFragment extends BaseFragment {

    private CardEmulator cardEmulator;
    private CardDetector cardDetector;

    public CardEmulatorFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_card_emulator_f);
    }

    @Override
    public void initData() {
        cardEmulator = CardEmulatorImpl.getInstance(context);
        cardDetector = CardDetectorImpl.getInstance();
    }

    @Override
    public Object getModule() {
        return CardEmulatorFragment.this;
    }

    @MethodGridEntity(btnname = "init", functionid = 1)
    private void init() {
        try {
            cardEmulator.init();
            showMessage("Init");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnname = "writeData", functionid = 2)
    private void writeData() {
        try {
            String url = "https://www.newlandnpt.com";
            showMessage("writeData=" + url);
            byte[] data = spliceDataForConfiguration(EmulateCardType.T4T, url, (byte) 0x00);
            cardEmulator.writeData(EmulateCardFileType.T2T_NDEF, ISOUtils.hex2byte("0346910116550068747470733A2F2F7777772E62616964752E636F6D540F1A616E64726F69642E636F6D3A706B6768747470733A2F2F7777772E6E65776C616E646E70742E636F6D"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnname = "readData", functionid = 3)
    private void readData() {
        try {
            showMessage("readData");
            byte[] data = cardEmulator.readData(EmulateCardFileType.T2T_NDEF, 64);
            byte[] url = new byte[57];
            System.arraycopy(data, 7, url, 0, url.length);
            showMessage("readData=" + new String(url).trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnname = "start", functionid = 4)
    private void start() {
        try {
            showMessage("start card emulator");
            cardEmulator.start(EmulateCardType.T4T, new CardEmulatorListener() {
                @Override
                public void onDetect() {
                    showMessage("onDetect");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnname = "stop", functionid = 5)
    private void stop() {
        try {
            showMessage("stop card emulator");
            cardEmulator.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnname = "getState", functionid = 6)
    private void getState() {
        try {
            showMessage("getState");
            CardEmulatorState status = cardEmulator.getStatus(EmulateCardType.T4T);
            showMessage("getState=" + status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnname = "writeConfig", functionid = 7)
    private void writeConfig() {
        try {
            showMessage("writeConfig");
            EmulateCardConfig cardConfig = new EmulateCardConfig();
            cardEmulator.writeConfig(EmulateCardType.T4T, cardConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnname = "readConfig", functionid = 8)
    private void readConfig() {
        try {
            showMessage("readConfig");
            EmulateCardConfig cardConfig = cardEmulator.readConfig(EmulateCardType.T4T);
            showMessage(String.format("UID=%s, ndefSize=%s", ISOUtils.hexString(cardConfig.getUID()), ISOUtils.hexString(cardConfig.getNdefSize())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnname = "getCEState", functionid = 9)
    private void getCEState() {
        try {
            int ceState = cardEmulator.getCEState(CEStateType.WORK);
            showMessage("getCEState=" + ceState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnname = "pollingCards", functionid = 10)
    private void openCardReader() {
        try {
            cardDetector.pollingCards(10, EmulateCardType.T4T, new PollingCardsListener() {
                @Override
                public void onFindMagCard(MagCardResult magCardResult) {
                    showMessage("onFindMagCard card=" + new String(magCardResult.getPanData()));
                }

                @Override
                public void onFindContactlessCard() {
                    showMessage("onFindContactlessCard");
                }

                @Override
                public void onFindContactCard() {
                    showMessage("onFindContactCard");
                }

                @Override
                public void onFindHCE() {
                    showMessage("onFindHCE");
                }

                @Override
                public void onCompletePolling() {
                    showMessage("onCompletePolling");
                }

                @Override
                public void onTimeout() {
                    showMessage("onTimeout");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnname = "stopPolling", functionid = 11)
    private void stopPolling() {
        try {
            showMessage("stopPolling");
            cardDetector.stopPolling();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final byte NFC_START = (byte) 0x03;
    public static final byte NFC_TAIL = (byte) 0xFE;
    public static final byte NFC_FORUM_TYPE = (byte) 0xD1;
    public static final byte NFC_RECORD_TYPE_LEN = (byte) 0x01;
    public static final byte NFC_URI_TYPE = (byte) 0x55;

    public byte[] spliceDataForConfiguration(EmulateCardType cardType, String url, byte uriCode) {
        byte[] tempData = new byte[100];
        byte[] result = null;
        int inLen = url.length();
        if (cardType == EmulateCardType.T2T) {
            tempData[0] = NFC_START;
            tempData[1] = (byte) (inLen + 5);
            tempData[2] = NFC_FORUM_TYPE;
            tempData[3] = NFC_RECORD_TYPE_LEN;
            tempData[4] = (byte) (inLen + 1);
            tempData[5] = NFC_URI_TYPE;
            tempData[6] = uriCode;
            System.arraycopy(url.getBytes(StandardCharsets.UTF_8), 0, tempData, 7, inLen);
            System.arraycopy(new byte[] {NFC_TAIL}, 0, tempData, 7 + inLen, 1);
            result = new byte[inLen + 8];
            System.arraycopy(tempData, 0, result, 0, result.length);
        } else if (cardType == EmulateCardType.T4T) {
            int ndefLen = inLen + 5;
            tempData[0] = (byte) (ndefLen >> 8);
            tempData[1] = (byte) ndefLen;
            tempData[2] = NFC_FORUM_TYPE;
            tempData[3] = NFC_RECORD_TYPE_LEN;
            tempData[4] = (byte) (inLen + 1);
            tempData[5] = NFC_URI_TYPE;
            tempData[6] = uriCode;
            System.arraycopy(url.getBytes(StandardCharsets.UTF_8), 0, tempData, 7, inLen);
            result = new byte[inLen + 7];
            System.arraycopy(tempData, 0, result, 0, result.length);
        }
        return result;
    }
}
