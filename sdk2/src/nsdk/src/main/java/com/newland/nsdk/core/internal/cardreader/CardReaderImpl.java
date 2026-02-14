package com.newland.nsdk.core.internal.cardreader;

import android.os.Build;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.card.contact.ContactCardSlot;
import com.newland.nsdk.core.api.common.card.contactless.CardSearchMode;
import com.newland.nsdk.core.api.common.card.contactless.ContactlessCardInfo;
import com.newland.nsdk.core.api.common.card.contactless.ContactlessCardType;
import com.newland.nsdk.core.api.common.card.magcard.MagCardInfo;
import com.newland.nsdk.core.api.common.cardreader.CardReaderListener;
import com.newland.nsdk.core.api.common.cardreader.CardReaderParameters;
import com.newland.nsdk.core.api.common.cardreader.CardType;
import com.newland.nsdk.core.api.common.cardreader.ExtendedCardReaderListener;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.internal.cardreader.CardReader;
import com.newland.nsdk.core.api.internal.exception.NSDKNDKException;
import com.newland.nsdk.core.common.NSDKExecutors;
import com.newland.nsdk.core.internal.jni.NSDKJni;

import java.util.Arrays;
import java.util.Locale;


/**
 * Device terminal card reader module<p>
 * <p>
 * Author by liudan, Date on 2020/1/19.
 */
public class CardReaderImpl implements CardReader {

    private static final String TAG = "CardReaderImpl";
    private static final int MAG_CARD = 0x01;
    private static final int CONTACT_CARD = 0x02;
    private static final int CONTACTLESS_CARD = 0x04;
    private static final int CONTACT_CARD_SLOT_2 = 0x08;
    private static final int RF_TYPE_A = 0x01;
    private static final int RF_TYPE_B = 0x02;
    private static final int RF_TYPE_F = 0x04;
    private static final int RF_TYPE_V = 0x08;
    private volatile boolean isOpenCardReader;
    private Object openCloseSync = new Object();
    private Object cardTypeSync = new Object();
    private CardType mCardType = null;

    public boolean isSupported;
    private boolean isSupportLPCD;

    private volatile static CardReaderImpl instance;

    public static CardReaderImpl getInstance(boolean isSupported, boolean isSupportLPCD) {
        if (instance == null) {
            synchronized (CardReaderImpl.class) {
                if (instance == null || instance.isSupported != isSupported || instance.isSupportLPCD != isSupportLPCD) {
                    instance = new CardReaderImpl(isSupported, isSupportLPCD);
                }
            }
        } else {
            if (instance.isSupported != isSupported || instance.isSupportLPCD != isSupportLPCD) {
                instance = new CardReaderImpl(isSupported, isSupportLPCD);
            }
        }
        return instance;
    }

    private CardReaderImpl(){
        this.isSupported = true;
    }

    private CardReaderImpl(boolean isSupported){
        this.isSupported = isSupported;
    }

    private CardReaderImpl(boolean isSupported, boolean isSupportLPCD) {
        this.isSupported = isSupported;
        this.isSupportLPCD = isSupportLPCD;
    }

    private void isSupported() throws NSDKException {
        if(!isSupported){
            throw new NSDKException(ErrorCode.UNSUPPORTED, "UnSupported CardReader Module");
        }
    }

    /**
     * Open the card reader by non-blocking way<p>
     *
     * @param timeout            timeout(second)(0-82800],
     * @param cardReaderListener Non-blocked listener{@link CardReaderListener}
     */
    @Override
    public void openCardReader(final CardType[] cardTypes, final int timeout, final CardReaderParameters cardReaderParameter, final CardReaderListener cardReaderListener) throws NSDKException {
        isSupported();

        synchronized (openCloseSync) {
            if (isOpenCardReader) {
                throw new NSDKException(ErrorCode.ERROR, "Card reader is busy.");
            }

            if (cardTypes == null || cardTypes.length == 0) {
                throw new NSDKIllegalParameterException("Please set what cards to search.");
            }

            if (cardReaderParameter == null || cardReaderListener == null) {
                throw new NSDKIllegalParameterException("Card reader parameters and listener shall not be null.");
            }

            if (timeout <= 0) {
                throw new NSDKIllegalParameterException("Timeout shall be >0.");
            }
            if (cardReaderParameter.getCardSearchMode() == CardSearchMode.RF_LPCD && !isSupportLPCD) {
                throw new NSDKIllegalParameterException("Current device is not support LPCD function, please choose another card detect function.");
            }

            boolean isSearchContactCard = false;
            for (CardType ct : cardTypes) {
                if (ct == CardType.CONTACT_CARD) {
                    isSearchContactCard = true;
                    break;
                }
            }
            ContactCardSlot[] cardSlots = cardReaderParameter.getCardSlots();
            if (isSearchContactCard && (cardSlots == null || cardSlots.length == 0)) {
                cardReaderParameter.setCardSlots(new ContactCardSlot[] {ContactCardSlot.IC1});
            }
            final boolean isContainCardSlot2 = isContainCardSlot2(cardReaderParameter.getCardSlots());
            if (isContainCardSlot2 && !(cardReaderListener instanceof ExtendedCardReaderListener)) {
                throw new NSDKIllegalParameterException("It shall be ExtendedCardReaderListener when contains IC2.");
            }

            NSDKJni.getInstance().resetCancelFlag();
            NSDKExecutors.threadStart(new Runnable() {
                @Override
                public void run() {
                    try {
                        CardReaderResult result = new CardReaderResult();

                        boolean isSearchContactlessCard = false;
                        int readMode = 0;
                        int clType = 0;
                        int nLenParamTypeF = 0;
                        int nLenParamTypeV = 0;
                        byte[] paramTypeF = null;
                        byte[] paramTypeV = null;
                        for (CardType ct: cardTypes) {
                            if (ct == CardType.CONTACTLESS_CARD) {
                                isSearchContactlessCard = true;
                                readMode = readMode | CONTACTLESS_CARD;
                            } else if (ct == CardType.CONTACT_CARD) {
                                if (isContainCardSlot2) {
                                    readMode = readMode | CONTACT_CARD_SLOT_2;
                                }
                                if (isContainCardSlot1(cardReaderParameter.getCardSlots())) {
                                    readMode = readMode | CONTACT_CARD;
                                }
                            } else if (ct == CardType.MAG_CARD) {
                                readMode = readMode | MAG_CARD;
                            }
                        }

                        if (isSearchContactlessCard) {
                            if (cardReaderParameter.getContactlessCardTypes() == null || cardReaderParameter.getContactlessCardTypes().length == 0) {
                                clType = RF_TYPE_A | RF_TYPE_B;
                            } else {
                                for (ContactlessCardType clCardType: cardReaderParameter.getContactlessCardTypes()){
                                    if (clCardType == ContactlessCardType.TYPE_A){
                                        clType |= RF_TYPE_A;
                                    } else if (clCardType == ContactlessCardType.TYPE_B){
                                        clType |= RF_TYPE_B;
                                    } else if (clCardType == ContactlessCardType.TYPE_F){
                                        clType |= RF_TYPE_F;
                                        paramTypeF = cardReaderParameter.getTypeFParameters();
                                        if (paramTypeF != null){
                                            nLenParamTypeF = paramTypeF.length;
                                        }
                                    } else if (clCardType == ContactlessCardType.TYPE_V){
                                        clType |= RF_TYPE_V;
                                        paramTypeV = cardReaderParameter.getTypeVParameters();
                                        if (paramTypeV != null){
                                            nLenParamTypeV = paramTypeV.length;
                                        }
                                    }
                                }
                            }
                        }

                        int ret = -1;
                        if (cardReaderParameter.getCardSearchMode() == CardSearchMode.CARD_EVENT) {
                            ret = NSDKJni.getInstance().openCardReaderWithCardEvent(readMode, clType, cardReaderParameter.isVerifyTrack(), timeout, paramTypeF, nLenParamTypeF, paramTypeV, nLenParamTypeV, result);
                        } else {
                            if (isContainCardSlot2) {
                                ret = NSDKJni.getInstance().openCardReader2(readMode, clType, cardReaderParameter.isVerifyTrack(),timeout, paramTypeF, nLenParamTypeF,paramTypeV, nLenParamTypeV, result);
                            } else {
                                ret = NSDKJni.getInstance().openCardReader(readMode, clType, cardReaderParameter.isVerifyTrack(),timeout, paramTypeF, nLenParamTypeF,paramTypeV, nLenParamTypeV, result,  (cardReaderParameter.getCardSearchMode() == CardSearchMode.RF_LPCD));
                            }
                        }



                        if (ret == 0) {
                            byte cardResultInterface = result.getCardInterface();
                            switch (cardResultInterface) {
                                case MAG_CARD:
                                    setLastReaderType(CardType.MAG_CARD);
                                    onFindMagCard(result, cardReaderListener);
                                    break;
                                case CONTACT_CARD:
                                    // 目前寻卡还不能判断卡类型
                                    setLastReaderType(CardType.CONTACT_CARD);
                                    isOpenCardReader = false;
                                    cardReaderListener.onFindContactCard();
                                    break;
                                case CONTACT_CARD_SLOT_2:
                                    isOpenCardReader = false;
                                    ((ExtendedCardReaderListener) cardReaderListener).onFindContactCards(new ContactCardSlot[] {ContactCardSlot.IC2});
                                    break;
                                case CONTACT_CARD | CONTACT_CARD_SLOT_2:
                                    isOpenCardReader = false;
                                    ((ExtendedCardReaderListener)cardReaderListener).onFindContactCards(new ContactCardSlot[] {ContactCardSlot.IC1, ContactCardSlot.IC2});
                                    break;
                                case CONTACTLESS_CARD:
                                    setLastReaderType(CardType.CONTACTLESS_CARD);
                                    LogUtils.e(TAG, "result.getContactlessCardType()=" + result.getContactlessCardType());
                                    ContactlessCardType resultContactlessCardType = null;
                                    switch(result.getContactlessCardType()){
                                        case RF_TYPE_A:
                                            resultContactlessCardType = ContactlessCardType.TYPE_A;
                                            break;
                                        case RF_TYPE_B:
                                            resultContactlessCardType = ContactlessCardType.TYPE_B;
                                            break;
                                        case RF_TYPE_F:
                                            resultContactlessCardType = ContactlessCardType.TYPE_F;
                                            break;
                                        case RF_TYPE_V:
                                            resultContactlessCardType = ContactlessCardType.TYPE_V;
                                            break;
                                        default:
                                            break;
                                    }
                                    ContactlessCardInfo cardInfo = new ContactlessCardInfo();
                                    if (result.getContactlessResult().getIdmpmmLen() > 0) {
                                        cardInfo.setIDmPMm(Arrays.copyOf(result.getContactlessResult().getIdmpmm(), result.getContactlessResult().getIdmpmmLen()));
                                    }

                                    isOpenCardReader = false;
                                    cardReaderListener.onFindContactlessCard(resultContactlessCardType, cardInfo);
                                    break;
                            }
                        } else if (ret == ErrorCode.TIMEOUT) {//timeout
                            isOpenCardReader = false;
                            cardReaderListener.onTimeout();
                        } else if (ret == ErrorCode.CANCELLED) {//cancel
                            isOpenCardReader = false;
                            cardReaderListener.onCancel();
//                        } else if (ret == ErrorCode.MAG_CARD_BUSY) {
//                            cardReaderListener.onError(ret, "Magnetic stripe reader is busy");
//                        } else if (ret == ErrorCode.ICCARD_BUSY) {
//                            cardReaderListener.onError(ret, "IC card reader is busy");
//                        } else if (ret == ErrorCode.RFID_BUSY) {
//                            cardReaderListener.onError(ret, "RF card reader is busy");
                        } else if (ret == ErrorCode.TRACK_STATUS_ERROR) {
                            isOpenCardReader = false;
                            cardReaderListener.onError(ErrorCode.TRACK_STATUS_ERROR, "Track status error.");
                        } else if (ret == ErrorCode.TRACK_FORMAT_ERROR) {
                            isOpenCardReader = false;
                            cardReaderListener.onError(ErrorCode.TRACK_FORMAT_ERROR, "Track data format error.");
                        } else if (ret == ErrorCode.RFID_MULTI_CARDS) {
                            isOpenCardReader = false;
                            cardReaderListener.onError(ret, "Multi RF cards are found");
                        } else if (ret == ErrorCode.FELICA_COLLISION) {
                            isOpenCardReader = false;
                            cardReaderListener.onError(ErrorCode.FELICA_COLLISION, "Multi Felica cards are found, please use polling in FelicaCard class to detect afterwards.");
                        } else {
                            isOpenCardReader = false;
                            cardReaderListener.onError(ErrorCode.ERROR, String.format(Locale.US, "Failed to open card reader(%d)", ret));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        isOpenCardReader = false;
                        cardReaderListener.onError(ErrorCode.ERROR, "Failed to open card reader.");
                    }
                }
            });
            isOpenCardReader = true;
        }


    }


    private void onFindMagCard(CardReaderResult result, CardReaderListener cardReaderListener) {
        byte[] panHashStr = new byte[result.getMagResult().getAccountLen()];
        byte[] panStr = new byte[result.getMagResult().getAccountLen()];
        byte[] track1 = new byte[result.getMagResult().getFirstTrackLen()];
        byte[] track2 = new byte[result.getMagResult().getSecondTrackLen()];
        byte[] track3 = new byte[result.getMagResult().getThirdTrackLen()];
        byte[] track4 = new byte[result.getMagResult().getFourthTrackLen()];
        byte[] track5 = new byte[result.getMagResult().getFifthTrackLen()];
        byte[] track6 = new byte[result.getMagResult().getSixthTrackLen()];

//        LogUtils.d(TAG, "account len=" + result.getMagResult().getAccountLen());
//        LogUtils.d(TAG, "account=" + result.getMagResult().getAccount());

        System.arraycopy(result.getMagResult().getAccountHash(), 0, panHashStr, 0, panHashStr.length);
        System.arraycopy(result.getMagResult().getAccount(), 0, panStr, 0, panStr.length);
        System.arraycopy(result.getMagResult().getFirstTrackData(), 0, track1, 0, track1.length);
        System.arraycopy(result.getMagResult().getSecondTrackData(), 0, track2, 0, track2.length);
        System.arraycopy(result.getMagResult().getThirdTrackData(), 0, track3, 0, track3.length);
        System.arraycopy(result.getMagResult().getFourthTrackData(), 0, track4, 0, track4.length);
        System.arraycopy(result.getMagResult().getFifthTrackData(), 0, track5, 0, track5.length);
        System.arraycopy(result.getMagResult().getSixthTrackData(), 0, track6, 0, track6.length);
        MagCardInfo magCardInfo = new MagCardInfo();
        magCardInfo.setTrackStatus(result.getMagResult().getTrackStatus());
        magCardInfo.setTrackFormats(result.getMagResult().getTrackFormats());
        magCardInfo.setTrack1Data(track1);
        magCardInfo.setPlainTrack1DataLen(track1.length);
        magCardInfo.setTrack2Data(track2);
        magCardInfo.setPlainTrack2DataLen(track2.length);
        magCardInfo.setTrack3Data(track3);
        magCardInfo.setPlainTrack3DataLen(track3.length);
        magCardInfo.setTrack4Data(track4);
        magCardInfo.setPlainTrack4DataLen(track4.length);
        magCardInfo.setTrack5Data(track5);
        magCardInfo.setPlainTrack5DataLen(track5.length);
        magCardInfo.setTrack6Data(track6);
        magCardInfo.setPlainTrack6DataLen(track6.length);
        magCardInfo.setPanData(panStr);
        magCardInfo.setPlainPANLen(panStr.length);
        magCardInfo.setValidDate(new String(result.getMagResult().getValidDate()));
        magCardInfo.setServiceCode(new String(result.getMagResult().getServiceCode()));

        isOpenCardReader = false;
        cardReaderListener.onFindMagCard(magCardInfo);
    }

    /**
     * Cancel the current card reading <p>
     */
    @Override
    public void cancelCardReader() throws NSDKException {
        isSupported();
        synchronized (openCloseSync) {
            int ret;
            if (isOpenCardReader) {
                ret = NSDKJni.getInstance().cancelCardReader();
            } else {
                ret = NSDKJni.getInstance().closeCardReader();
            }
            if (ret == ErrorCode.PARAM_ERROR) {
                throw new NSDKIllegalParameterException();
            }
            if (ret != ErrorCode.OK) {
                throw new NSDKNDKException(ret, "Failed to cancel card reader.");
            }
        }
    }

    /**
     * Get the card reader module type triggered last time<p>
     *
     * @return card reader module type ,include following type: <ol>
     * <li>{@link CardType#MAG_CARD}</li>
     * <li>{@link CardType#CONTACT_CARD}</li>
     * <Li>{@link CardType#CONTACTLESS_CARD}</li></ol>
     */
    @Override
    public CardType getLastReaderType() {
        synchronized (cardTypeSync) {
            return mCardType;
        }
    }

    @Override
    public boolean isCardInserted() throws NSDKException {
        isSupported();
        int ret = NSDKJni.getInstance().ICCheckSlotsState(ContactCardSlot.IC1.ordinal());
        return ret == 0;
    }

    @Override
    public boolean checkCardSlotStatus(ContactCardSlot cardSlot) throws NSDKException {
        isSupported();
        if (cardSlot.ordinal() > 1) {
            throw new NSDKException(ErrorCode.UNSUPPORTED, "This interface is only support to check IC1 or IC2 slot status.");
        }
        int ret = NSDKJni.getInstance().ICCheckSlotsState(cardSlot.ordinal());
        return ret == 0;
    }

    @Override
    public void openRf() throws NSDKException {
        int ret = NSDKJni.getInstance().RFOn();
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to open RF[%d]", ret));
        }
    }

    @Override
    public void closeRf() throws NSDKException {
        int ret = NSDKJni.getInstance().RFClose();
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to close RF[%d]", ret));
        }
    }

//    @Override
//    public void displayRfidLogo(boolean isDisplayed) throws NSDKException {
//        isSupportedDisplayRfidLogo();
//        int ret = NSDKJni.getInstance().displayRfidLogo(isDisplayed);
//        if (ret != 0) {
//            throw new NSDKException(ret, String.format(Locale.US, "Failed to set Rfid logo status, ret = %d", ret));
//        }
//    }

    @Override
    public boolean isCardPresent() throws NSDKException {
        isSupported();
        int ret = NSDKJni.getInstance().RFIsCardPresent(-1);
        return ret == 0;
    }

    @Override
    public boolean isCardPresent(ContactlessCardType[] cardTypes) throws NSDKException {
        isSupported();
        if (cardTypes == null || cardTypes.length == 0) {
            throw new NSDKIllegalParameterException("Contactless card types shall not be null.");
        }

        int clType = 0;
        for (ContactlessCardType cardType : cardTypes) {
            switch (cardType) {
                case TYPE_A:
                    clType |= RF_TYPE_A;
                    break;
                case TYPE_B:
                    clType |= RF_TYPE_B;
                    break;
                case TYPE_F:
                    clType |= RF_TYPE_F;
                    break;
                case TYPE_V:
                    clType |= RF_TYPE_V;
                    break;
            }
        }
        int ret = NSDKJni.getInstance().RFIsCardPresent(clType);
        return ret == ErrorCode.OK;
    }

    private void setLastReaderType(CardType cardType) {
        synchronized (cardTypeSync) {
            mCardType = cardType;
        }
    }


    private void isSupportedDisplayRfidLogo() throws NSDKException {
        if (!Build.MODEL.equalsIgnoreCase("N750")) {
            throw new NSDKException(ErrorCode.UNSUPPORTED, "UnSupported DisplayRfidLogo function.");
        }
    }

    private String getAccount(byte[] account) {
        if (account == null) {
            return null;
        }
        String acct = ISOUtils.bcd2str(account, 0, account.length * 2, false);
        acct = acct.replace('E', '*');

        int index = acct.indexOf('F');
        if (index > 0) {
            return acct.substring(0, index);
        } else {
            return acct;
        }
    }

    private boolean isContainCardSlot2(ContactCardSlot[] slots) {
        if (slots != null && slots.length != 0) {
            for (ContactCardSlot cardSlot : slots) {
                if (cardSlot == ContactCardSlot.IC2) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isContainCardSlot1(ContactCardSlot[] slots) {
        for (ContactCardSlot cardSlot : slots) {
            if (cardSlot == ContactCardSlot.IC1) {
                return true;
            }
        }
        return false;
    }

    private static final int MASK_FIRSTTRACK = 0x01;
    private static final int MASK_SECONDTRACK = 0x02;
    private static final int MASK_THIRDTRACK = 0x04;

}
