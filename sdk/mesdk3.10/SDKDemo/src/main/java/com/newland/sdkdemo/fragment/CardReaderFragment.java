package com.newland.sdkdemo.fragment;

import android.content.Context;
import android.support.annotation.Nullable;

import com.newland.sdk.module.cardreader.CardReaderExtParams;
import com.newland.sdk.module.cardreader.CardReaderListener;
import com.newland.sdk.module.cardreader.CardReaderModule;
import com.newland.sdk.module.cardreader.CardType;
import com.newland.sdk.module.cardreader.RFCardInfo;
import com.newland.sdk.module.cardreader.SearchCardRule;
import com.newland.sdk.module.pin.KeyManagement;
import com.newland.sdk.module.rfcard.RFCardType;
import com.newland.sdk.module.swiper.MSDAlgorithmType;
import com.newland.sdk.module.swiper.MagStripeCardModule;
import com.newland.sdk.module.swiper.SwipExtParams;
import com.newland.sdk.module.swiper.SwipResult;
import com.newland.sdk.module.swiper.SwipResultCode;
import com.newland.sdk.module.swiper.SwiperReadModel;
import com.newland.sdk.utils.ISOUtils;
import com.newland.sdkdemo.AppConfig;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.adapter.LayoutMode;
import com.newland.sdkdemo.annotation.MethodGridEntity;
import com.newland.sdkdemo.utils.MessageTag;

/**
 * Author by bxy, Date on 2019/5/11 0011.
 */
public class CardReaderFragment extends BaseFragment {

    private CardReaderModule cardReader;
    private MagStripeCardModule magStripeCardModule;

    private KeyManagement keyManagement = KeyManagement.MKSK;

    public CardReaderFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_cardreader_f);
    }

    @Override
    public void initData() {
        cardReader = moduleManage.getCardReaderModule();
        magStripeCardModule = moduleManage.getMagStripeCardModule();
    }

    @Override
    public Object getModule() {
        return CardReaderFragment.this;
    }

    @Override
    public int getSpanCount() {
        return 2;
    }

    private static final int INDEX_OPENCARDREADER = 1;
    private static final int INDEX_CLOSECARDREADER = 2;

    private int getWKIndex() {
        if (AppConfig.MKSK_DES.equals(AppConfig.KEY_SYS_ALG)) {
            keyManagement = KeyManagement.MKSK;
            return AppConfig.Pin.MKSK_DES_INDEX_WK_TRACK;
        } else if (AppConfig.MKSK_SM4.equals(AppConfig.KEY_SYS_ALG)) {
            keyManagement = KeyManagement.MKSK;
            return AppConfig.Pin.MKSK_SM4_INDEX_WK_TRACK;
        } else if (AppConfig.MKSK_AES.equals(AppConfig.KEY_SYS_ALG)) {
            keyManagement = KeyManagement.MKSK;
            return AppConfig.Pin.MKSK_AES_INDEX_WK_TRACK;
        } else if (AppConfig.DUKPT_DES.equals(AppConfig.KEY_SYS_ALG)) {
            keyManagement = KeyManagement.DUKPT;
            return AppConfig.Pin.DUKPT_DES_INDEX;
        } else if (AppConfig.DUKPT_AES.equals(AppConfig.KEY_SYS_ALG)) {
            keyManagement = KeyManagement.DUKPT;
            return AppConfig.Pin.DUKPT_AES_INDEX;
        }
        return -1;
    }

    @MethodGridEntity(btnnameid = R.string.tv_open_card, functionid = INDEX_OPENCARDREADER, issync = false)
    private void openCardReader() {
        try {
            showMessage(context.getString(R.string.msg_swipe_insert_rf_card) + "\r\n", MessageTag.TIP);

            CardType[] cardTypes = new CardType[]{CardType.MSGCARD, CardType.ICCARD, CardType.RFCARD};
            CardReaderExtParams cardReaderExtParams = new CardReaderExtParams();
            cardReaderExtParams.setSearchCardRule(SearchCardRule.NORMAL);

            cardReader.openCardReader(cardTypes, 30, cardReaderListener, cardReaderExtParams);

        } catch (Exception e) {
            showMessage(context.getString(R.string.msg_reader_open_exception) + "\r\n", MessageTag.ERROR);
            showMessage(e.getMessage() + "\r\n", MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_revocate_card, functionid = INDEX_CLOSECARDREADER)
    private void closeCardReader() {
        try {
            cardReader.cancelCardReader();

            showMessage(context.getString(R.string.msg_revocate_reader) + context.getString(R.string.msg_succ) + "\r\n", MessageTag.NORMAL);
        } catch (Exception e) {
            showMessage(context.getString(R.string.msg_revocate_reader_exception) + e + "\r\n", MessageTag.ERROR);
        }
    }

    private CardReaderListener cardReaderListener = new CardReaderListener() {
        @Override
        public void onTimeout() {
            showMessage(context.getString(R.string.msg_timeout) + "\r\n", MessageTag.NORMAL);
        }

        @Override
        public void onCancel() {
            showMessage(context.getString(R.string.msg_cancel_open_reader) + "\r\n", MessageTag.NORMAL);
        }

        @Override
        public void onError(int errorCode, String message) {
            showMessage(context.getString(R.string.msg_reader_open_exception) + message + "\r\n", MessageTag.ERROR);
        }

        @Override
        public void onFindMagCard(boolean isSuccessful) {
            showMessage(context.getString(R.string.msg_cardreader_swiper), MessageTag.DATA);
            showMessage(context.getString(R.string.msg_swiper_result) + isSuccessful, MessageTag.DATA);
            readPlainResult();
//            readEncryResultByMask(); // Only one type of track information can be obtained by swiping the card at a time.
        }

        @Override
        public void onFindICCard() {
            showMessage(context.getString(R.string.msg_cradreader_insert), MessageTag.DATA);
        }

        @Override
        public void onFindRFCard(@Nullable RFCardType rfCardType, @Nullable RFCardInfo rfCardInfo) {
            showMessage(context.getString(R.string.msg_cardreader_rfcard), MessageTag.DATA);
            String showMsg = "";
            try {
                switch (rfCardType) {
                    case ACARD:
                    case BCARD:
                        showMsg = context.getString(R.string.msg_cardreader_rfcard_cpu);
                        break;
                    case M1CARD:
                        byte sak = rfCardInfo.getSAK();
                        if (sak == 0x08) {
                            showMsg = context.getString(R.string.msg_cardreader_rfcard_s50);
                        } else if (sak == 0x18) {
                            showMsg = context.getString(R.string.msg_cardreader_rfcard_s70);
                        } else if (sak == 0x28) {
                            showMsg = context.getString(R.string.msg_cardreader_rfcard_s50_pro);
                        } else if (sak == 0x38) {
                            showMsg = context.getString(R.string.msg_cardreader_rfcard_s70_pro);
                        } else {
                            showMsg = "sak=" + sak;
                            showMsg = showMsg + context.getString(R.string.msg_cardreader_undefind);
                        }
                        break;
                    case M0CARD:
                        showMsg = context.getString(R.string.msg_cardreader_rfcard_m0);
                        break;
                    case FELICA_CARD:
                        showMsg = context.getString(R.string.msg_cardreader_rfcard_felica);
                        break;
                    default:
                        showMsg = context.getString(R.string.msg_cardreader_undefind_rf);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            showMessage(showMsg + "\r\n", MessageTag.DATA);
            showMessage("snr:" + (rfCardInfo.getSNR() == null ? null : ISOUtils.hexString(rfCardInfo.getSNR())), MessageTag.DATA);
        }
    };

    private void readPlainResult() {
        try {
            showMessage(context.getString(R.string.msg_return_swipeResult_plain) + "\r\n", MessageTag.NORMAL);

            SwiperReadModel[] readModels = new SwiperReadModel[]{SwiperReadModel.FIRST_TRACK, SwiperReadModel.SECOND_TRACK, SwiperReadModel.THIRD_TRACK};

            SwipResult swipRslt = magStripeCardModule.readPlainResult(readModels);

            if (null != swipRslt && swipRslt.getRsltCode() == SwipResultCode.SUCCESS) {
                AppConfig.EMV.swipResult = swipRslt;
                byte[] firstTrackData = swipRslt.getFirstTrackData();
                byte[] secondTrack = swipRslt.getSecondTrackData();
                byte[] thirdTrack = swipRslt.getThirdTrackData();
                showMessage(context.getString(R.string.msg_card_NO) + swipRslt.getAccount(), MessageTag.DATA);
                showMessage("is IC card: " + swipRslt.isICCard(), MessageTag.DATA);
                showMessage("first track data: " + (firstTrackData == null ? null : new String(firstTrackData, "gbk")), MessageTag.DATA);
                showMessage(context.getString(R.string.msg_second_trackdata) + (secondTrack == null ? null : new String(secondTrack, "gbk")), MessageTag.DATA);
                showMessage(context.getString(R.string.msg_third_trackdata) + (thirdTrack == null ? "null" : new String(thirdTrack, "gbk")), MessageTag.DATA);
            } else {
                showMessage(context.getString(R.string.msg_swipeResult_empty) + "\r\n", MessageTag.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_get_trackdata_plain_error) + e + "\r\n", MessageTag.ERROR);
            showMessage(context.getString(R.string.msg_check_mainkey_workingkey) + "\r\n", MessageTag.ERROR);
        }
    }

    /**
     * Read Mask Info by swiping a card.
     */
    private void readEncryResultByMask() {
        try {
            showMessage(context.getString(R.string.msg_read_encrypted_trackdata) + "\r\n", MessageTag.NORMAL);
            SwiperReadModel[] readModels = new SwiperReadModel[]{SwiperReadModel.FIRST_TRACK, SwiperReadModel.SECOND_TRACK, SwiperReadModel.THIRD_TRACK};
            byte[] acctMask = new byte[]{0x00, 0x00, 0x00, 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x00, 0x00, 0x00};
            SwipExtParams swipExtParams = new SwipExtParams();
            swipExtParams.setAcctMask(acctMask);
            swipExtParams.setMSDAlgorithmType(MSDAlgorithmType.UNIONPAY_MODEL);
            SwipResult swipRslt = magStripeCardModule.readEncryptResult(keyManagement, getWKIndex(), swipExtParams);

            if (null != swipRslt && swipRslt.getRsltCode() == SwipResultCode.SUCCESS) {
                AppConfig.EMV.swipResult = swipRslt;
                byte[] firstTrackData = swipRslt.getFirstTrackData();
                byte[] secondTrack = swipRslt.getSecondTrackData();
                byte[] thirdTrack = swipRslt.getThirdTrackData();
                showMessage(context.getString(R.string.msg_mask_card_No) + swipRslt.getAccount().getAcctNo(), MessageTag.DATA);
                showMessage("is IC card: " + swipRslt.isICCard(), MessageTag.DATA);
                showMessage("first track data: " + (firstTrackData == null ? null : new String(firstTrackData, "gbk")), MessageTag.DATA);
                showMessage(context.getString(R.string.msg_second_trackdata) + (secondTrack == null ? null : new String(secondTrack, "gbk")), MessageTag.DATA);
                showMessage(context.getString(R.string.msg_third_trackdata) + (thirdTrack == null ? "null" : (thirdTrack == null ? null : new String(thirdTrack, "gbk"))), MessageTag.DATA);
            } else {
                showMessage(context.getString(R.string.msg_empty_swipeResult_and_swipe_again) + "\r\n", MessageTag.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_read_masked_trackdata_error) + e.getMessage() + "\r\n", MessageTag.ERROR);
            showMessage(context.getString(R.string.msg_check_mainkey_workingkey) + "\r\n", MessageTag.ERROR);
        }
    }


}
