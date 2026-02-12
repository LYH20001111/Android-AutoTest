
package com.newland.nsdkdemo.external.fragment;

import android.content.Context;

import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.card.contactless.ContactlessCardInfo;
import com.newland.nsdk.core.api.common.card.contactless.ContactlessCardType;
import com.newland.nsdk.core.api.common.card.magcard.MagCardInfo;
import com.newland.nsdk.core.api.common.card.magcard.TrackStatus;
import com.newland.nsdk.core.api.common.cardreader.CardReaderListener;
import com.newland.nsdk.core.api.common.cardreader.CardType;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.external.cardreader.ExtCardReader;
import com.newland.nsdk.core.api.external.cardreader.ExtCardReaderParameters;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.AppConfig;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.annotation.MethodGridEntity;
import com.newland.nsdkdemo.common.utils.MessageTag;

public class ExtCardReaderFragment extends ExtBaseFragment {

    private ExtCardReader mCardReader;

    public ExtCardReaderFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_extcardreader_f);
    }

    @Override
    public void initData() {
        mCardReader = (ExtCardReader) moduleManager.getModule(ModuleType.EXT_CARD_READER);
    }

    @Override
    public Object getModule() {
        return ExtCardReaderFragment.this;
    }

    @Override
    public int getSpanCount() {
        return 2;
    }

    private static final int INDEX_OPENCARDREADER = 1;
    private static final int INDEX_OPENCARDREADER_ENCRYPT_TRACK = 2;
    private static final int INDEX_CLOSECARDREADER = 3;
    private static final int INDEX_ISCARDPRESENT = 4;
    private static final int INDEX_ISCARDINSERTED = 5;

    @MethodGridEntity(btnnameid = R.string.tv_open_card, functionid = INDEX_OPENCARDREADER, issync = false)
    private void openCardReader() {
        try {
            showMessage(context.getString(R.string.msg_swipe_insert_rf_card) + "\r\n", MessageTag.TIP);

            ExtCardReaderParameters parameters = new ExtCardReaderParameters();
            parameters.setContactlessCardTypes(new ContactlessCardType[]{ContactlessCardType.TYPE_A, ContactlessCardType.TYPE_B, ContactlessCardType.TYPE_F});
            parameters.setPANKeyIndex((byte) 0);
            parameters.setDisplayMessages(new String[]{"Please insert/tap/swipe card..."});

            CardType[] cardTypes = new CardType[]{CardType.MAG_CARD, CardType.CONTACT_CARD, CardType.CONTACTLESS_CARD};
            LogUtils.d("CardReaderFragment", "********* open card reader.");
            mCardReader.openCardReader(cardTypes, 30, parameters, getCardReaderListener(false));
            LogUtils.d("CardReaderFragment", "********* Card reader opened.");

        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage(e, context.getString(R.string.msg_reader_open_exception));
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_open_card_encrypt_track, functionid = INDEX_OPENCARDREADER_ENCRYPT_TRACK, issync = false)
    private void openCardReaderEncryptTrack() {
        try {
            showMessage(context.getString(R.string.msg_swipe_insert_rf_card) + "\r\n", MessageTag.TIP);

            ExtCardReaderParameters parameters = new ExtCardReaderParameters();
            parameters.setContactlessCardTypes(new ContactlessCardType[]{ContactlessCardType.TYPE_A});
            parameters.setPANKeyIndex(AppConfig.Keys.MKSK_DES_INDEX_WK_TRACK);
//            parameters.setPANKeyIndex((byte) 0);
            parameters.setFirstClearPANLen((byte) 6);
            parameters.setLastClearPANLen((byte) 2);
            parameters.setDisplayMessages(new String[]{"Please insert/tap/swipe card..."});

            CardReaderListener cardReaderListener = getCardReaderListener(true);

            CardType[] cardTypes = new CardType[]{CardType.MAG_CARD, CardType.CONTACT_CARD, CardType.CONTACTLESS_CARD};
//            CardType[] cardTypes = new CardType[]{CardType.CONTACT_CARD};
            LogUtils.d("CardReaderFragment", "********* open card reader.");
            mCardReader.openCardReader(cardTypes, 30, parameters, cardReaderListener);
            LogUtils.d("CardReaderFragment", "********* Card reader opened.");

        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage(e, context.getString(R.string.msg_reader_open_exception));
        }
    }

    private CardReaderListener getCardReaderListener(boolean isEncrtyptTrack) {
        return new CardReaderListener() {
                    @Override
                    public void onTimeout() {
                        LogUtils.d("CardReaderFragment", "********* Timeout in listener.");
                        showMessage(context.getString(R.string.msg_timeout) + "\r\n", MessageTag.NORMAL);
                    }

                    @Override
                    public void onCancel() {
                        LogUtils.d("CardReaderFragment", "********* Cancelled in listener.");
                        showMessage(context.getString(R.string.msg_cancel_open_reader) + "\r\n", MessageTag.NORMAL);
                    }

                    @Override
                    public void onError(int errorCode, String message) {
//                        showMessage(moduleManager.getErrMsg(errorCode), MessageTag.ERROR);
                        showMessage(String.format("[%d] %s", errorCode, message), MessageTag.ERROR);
                    }

                    @Override
                    public void onFindMagCard(MagCardInfo result) {
                        showMessage(context.getString(R.string.msg_cardreader_swiper), MessageTag.DATA);
                        try {
                            byte[] firstTrackData = result.getTrack1Data();
                            byte[] secondTrack = result.getTrack2Data();
                            byte[] thirdTrack = result.getTrack3Data();
                            String serviceCode = result.getServiceCode();
                            String expireDate = result.getValidDate();
                            TrackStatus[] trackStatus =  result.getTrackStatus();

                            String msg = "";
                            if (trackStatus[0] == TrackStatus.GOOD){
                                String track1;
                                if (isEncrtyptTrack) {
                                    msg = (firstTrackData == null ? null : ISOUtils.hexString(firstTrackData));
                                } else {
                                    msg = (firstTrackData == null ? null : new String(firstTrackData, "gbk"));
                                }

                            } else {
                                msg = String.format("%s", trackStatus[0]);
                            }
                            showMessage("Track 1: "+ msg, MessageTag.DATA);

                            if (trackStatus[1] == TrackStatus.GOOD){
                                if (isEncrtyptTrack) {
                                    msg = (secondTrack == null ? null : ISOUtils.hexString(secondTrack));
                                } else {
                                    msg = (secondTrack == null ? null : new String(secondTrack, "gbk"));
                                }
                            } else {
                                msg = String.format("%s", trackStatus[1]);
                            }
                            showMessage("Track 2: "+ msg, MessageTag.DATA);

                            if (trackStatus[2] == TrackStatus.GOOD){
                                if (isEncrtyptTrack) {
                                    msg = (thirdTrack == null ? null : ISOUtils.hexString(thirdTrack));
                                } else {
                                    msg = (thirdTrack == null ? null : new String(thirdTrack, "gbk"));
                                }
                            } else {
                                msg = String.format("%s", trackStatus[2]);
                            }
                            showMessage("Track 3: "+ msg, MessageTag.DATA);

                            showMessage(context.getString(R.string.msg_service_code) + serviceCode, MessageTag.DATA);
                            showMessage(context.getString(R.string.msg_card_vaild_date) + expireDate, MessageTag.DATA);
                            if (isEncrtyptTrack) {
                                int len = result.getPlainPANLen() - result.getFirstClearPAN().length() - result.getLastClearPAN().length();
                                showMessage(context.getString(R.string.msg_card_NO) + String.format("%s%-"+len+"s%s", result.getFirstClearPAN(), " ", result.getLastClearPAN()).replace(" ", "*"), MessageTag.DATA);
                            } else {
                                showMessage(context.getString(R.string.msg_card_NO) + new String(result.getPanData()), MessageTag.DATA);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            showErrorMessage(e, context.getString(R.string.msg_get_trackdata_plain_error));
                            showMessage(context.getString(R.string.msg_check_mainkey_workingkey_AID_RID) + "\r\n", MessageTag.ERROR);
                            showMessage(context.getString(R.string.msg_check_cardReader_and_swipe_again) + "\r\n", MessageTag.ERROR);
                        }
                    }

                    @Override
                    public void onFindContactCard() {
                        showMessage(context.getString(R.string.msg_cradreader_insert), MessageTag.DATA);
                    }

                    @Override
                    public void onFindContactlessCard(ContactlessCardType contactlessCardType, ContactlessCardInfo contactlessCardInfo) {
                        showMessage(String.format("%s (%s)", context.getString(R.string.msg_cardreader_rfcard), contactlessCardType), MessageTag.DATA);
                        if (contactlessCardType == ContactlessCardType.TYPE_F) {
                            ExtContactlessCardFragment.IDmAndPMm = contactlessCardInfo.getIDmPMm();
                            showMessage(String.format("IDmPMm: %s", contactlessCardInfo.getIDmPMm() == null ? "null" : ISOUtils.hexString(contactlessCardInfo.getIDmPMm())));
                        }
                    }
                };
    }

    @MethodGridEntity(btnnameid = R.string.tv_revocate_card, functionid = INDEX_CLOSECARDREADER)
    private void cancelCardReader() {
        try {
            LogUtils.d("CardReaderFragment", "********* Cancel card reader.");
            mCardReader.cancelCardReader();
            LogUtils.d("CardReaderFragment", "********* Card reader Cancelled.");
            showMessage(context.getString(R.string.msg_revocate_reader) + context.getString(R.string.msg_succ) + "\r\n", MessageTag.NORMAL);
        } catch (Exception e) {
            showErrorMessage(e, context.getString(R.string.msg_revocate_reader_exception));
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_rf_is_induct, functionid = INDEX_ISCARDPRESENT)
    private void isCardPresent() {
        try {
            boolean isExit = mCardReader.isCardPresent();
            if (isExit) {
                showMessage(context.getString(R.string.msg_rf_isInducted) + "\r\n", MessageTag.DATA);
            } else {
                showMessage(context.getString(R.string.msg_rf_is_not_inducted) + "\r\n", MessageTag.DATA);
            }
        } catch (Exception e) {
            showErrorMessage(e, context.getString(R.string.msg_error));
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_ic_is_insert, functionid = INDEX_ISCARDINSERTED)
    private void isCardInserted() {
        try {
            boolean isExit = mCardReader.isCardInserted();
            if (isExit) {
                showMessage(context.getString(R.string.card_in_slot) + "\r\n", MessageTag.DATA);
            } else {
                showMessage(context.getString(R.string.no_card_in_slot) + "\r\n", MessageTag.DATA);
            }
        } catch (Exception e) {
            showErrorMessage(e, context.getString(R.string.msg_error));
        }
    }
}
