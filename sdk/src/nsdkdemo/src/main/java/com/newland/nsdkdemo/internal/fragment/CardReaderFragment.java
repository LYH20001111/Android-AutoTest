package com.newland.nsdkdemo.internal.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;

import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.card.contact.ContactCardSlot;
import com.newland.nsdk.core.api.common.card.contactless.CardSearchMode;
import com.newland.nsdk.core.api.common.card.contactless.ContactlessCardInfo;
import com.newland.nsdk.core.api.common.card.contactless.ContactlessCardType;
import com.newland.nsdk.core.api.common.card.magcard.MagCardInfo;
import com.newland.nsdk.core.api.common.card.magcard.TrackDataFormat;
import com.newland.nsdk.core.api.common.card.magcard.TrackStatus;
import com.newland.nsdk.core.api.common.cardreader.CardReaderListener;
import com.newland.nsdk.core.api.common.cardreader.CardReaderParameters;
import com.newland.nsdk.core.api.common.cardreader.CardType;
import com.newland.nsdk.core.api.common.cardreader.ExtendedCardReaderListener;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.internal.cardreader.CardReader;
import com.newland.nsdk.core.api.internal.devicemanager.DeviceInfo;
import com.newland.nsdk.core.api.internal.devicemanager.DeviceManager;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.AppConfig;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.annotation.MethodGridEntity;
import com.newland.nsdkdemo.common.utils.DialogUtils;
import com.newland.nsdkdemo.common.utils.MessageTag;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class CardReaderFragment extends InternalBaseFragment {

    private CardReader mCardReader;
    private DeviceInfo deviceInfo;
    private DeviceManager deviceManager;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor mEditor;
    private long startTime, endTime;

    public CardReaderFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_cardreader_f);
    }

    @Override
    public void initData() {
        mCardReader = (CardReader) moduleManager.getModule(ModuleType.CARD_READER);
        deviceManager = (DeviceManager) moduleManager.getModule(ModuleType.DEVICE_MANAGER);
        sharedPreferences = context.getSharedPreferences(AppConfig.SharedPreferenceConfig.SHARE_PREFERENCE, Context.MODE_PRIVATE);
        mEditor = sharedPreferences.edit();
        try {
            deviceInfo = deviceManager.getDeviceInfo();
        } catch (NSDKException e) {
            e.printStackTrace();
        }
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
    private static final int INDEX_OPENCARDREADER_OPTIONAL = 2;
    private static final int INDEX_CLOSECARDREADER = 3;
    private static final int INDEX_ISCARDPRESENT = 4;
    private static final int INDEX_ISCARDINSERTED = 5;
    private static final int INDEX_DISPLAY_RFID_LOGO = 6;
    private static final int INDEX_CANCEL_RFID_LOGO = 7;

    @MethodGridEntity(btnnameid = R.string.tv_open_card, functionid = INDEX_OPENCARDREADER, issync = false)
    private void openCardReader() {
        try {
            startTime = System.currentTimeMillis();
            showMessage(context.getString(R.string.msg_swipe_insert_rf_card) + "\r\n", MessageTag.TIP);
            CardReaderParameters parameter = new CardReaderParameters();
            CardType[] cardTypes = new CardType[]{CardType.CONTACT_CARD, CardType.MAG_CARD, CardType.CONTACTLESS_CARD};
            parameter.setContactlessCardTypes(new ContactlessCardType[]{ContactlessCardType.TYPE_F, ContactlessCardType.TYPE_A, ContactlessCardType.TYPE_B});
            LogUtils.d("CardReaderFragment", "********* open card reader.");
            mCardReader.openCardReader(cardTypes, 10, parameter, cardReaderListener);
            LogUtils.d("CardReaderFragment", "********* Card reader opened.");
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage(e, context.getString(R.string.msg_reader_open_exception));
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_open_card_optional, functionid = INDEX_OPENCARDREADER_OPTIONAL, issync = false)
    private void openCardReaderWithOptionalInterfaces() {
        DialogUtils.createCustomDialog(context, R.string.tv_open_card_optional, null, R.layout.dialog_open_cardreader_optional, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                CheckBox cbContactlessCard = view.findViewById(R.id.cb_contactlessCard);
                cbContactlessCard.setChecked(true);
                CheckBox cbMagCard = view.findViewById(R.id.cb_magCard);
                cbMagCard.setChecked(true);
                CheckBox cbIC1 = view.findViewById(R.id.cb_ic1);
                cbIC1.setChecked(true);
                Spinner spnCardSearchMode = view.findViewById(R.id.spn_cardSearchMode);
                spnCardSearchMode.setSelection(sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.CARD_READER_CARD_SEARCH_MODE, 0));
                spnCardSearchMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.CARD_READER_CARD_SEARCH_MODE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                Switch swIsVerifyTrack = view.findViewById(R.id.sw_cardSearch_verifyTrack);
                swIsVerifyTrack.setChecked(sharedPreferences.getBoolean(AppConfig.SharedPreferenceConfig.CARD_READER_VERIFY_TRACK, false));
            }

            @Override
            public void onResult(int id, View view) {
                CheckBox rbContactlessCard = view.findViewById(R.id.cb_contactlessCard);
                CheckBox rbMagCard = view.findViewById(R.id.cb_magCard);
                CheckBox rbIC1 = view.findViewById(R.id.cb_ic1);
                CheckBox rbIC2 = view.findViewById(R.id.cb_ic2);
                Spinner spnCardSearchMode = view.findViewById(R.id.spn_cardSearchMode);
                CardSearchMode cardSearchMode = "Default".equals(spnCardSearchMode.getSelectedItem().toString()) ? CardSearchMode.DEFAULT : (spnCardSearchMode.getSelectedItem().toString().contains("LPCD") ? CardSearchMode.RF_LPCD : CardSearchMode.CARD_EVENT);
                Switch swIsVerifyTrack = view.findViewById(R.id.sw_cardSearch_verifyTrack);
                mEditor.putBoolean(AppConfig.SharedPreferenceConfig.CARD_READER_VERIFY_TRACK, swIsVerifyTrack.isChecked());
                mEditor.commit();
                List<CardType> cardTypeList = new LinkedList<>();
                List<ContactCardSlot> cardSlotList = new LinkedList<>();
                CardReaderParameters parameters = new CardReaderParameters();
                parameters.setContactlessCardTypes(new ContactlessCardType[] {ContactlessCardType.TYPE_A, ContactlessCardType.TYPE_B, ContactlessCardType.TYPE_F});
                if (rbContactlessCard.isChecked()) {
                    cardTypeList.add(CardType.CONTACTLESS_CARD);
                }
                if (rbMagCard.isChecked()) {
                    cardTypeList.add(CardType.MAG_CARD);
                }
                if (rbIC1.isChecked() || rbIC2.isChecked()) {
                    cardTypeList.add(CardType.CONTACT_CARD);
                }
                if (cardTypeList.size() == 0) {
                    showMessage("Please select at lease one interface for detection.", MessageTag.ERROR);
                    return;
                }
                CardType[] cardTypes = new CardType[cardTypeList.size()];
                for (int i = 0; i < cardTypeList.size(); i++) {
                    cardTypes[i] = cardTypeList.get(i);
                }

                if (rbIC1.isChecked()) {
                    cardSlotList.add(ContactCardSlot.IC1);
                }
                if (rbIC2.isChecked()) {
                    cardSlotList.add(ContactCardSlot.IC2);
                }
                ContactCardSlot[] cardSlots = new ContactCardSlot[cardSlotList.size()];
                for (int i = 0; i < cardSlotList.size(); i++) {
                    cardSlots[i] = cardSlotList.get(i);
                }
                parameters.setCardSlots(cardSlots);
                parameters.setCardSearchMode(cardSearchMode);
                parameters.setVerifyTrack(swIsVerifyTrack.isChecked());
                try {
                    showMessage(context.getString(R.string.msg_swipe_insert_rf_card) + "\r\n", MessageTag.TIP);
                    mCardReader.openCardReader(cardTypes, 10, parameters, extendedCardReaderListener);
                } catch (NSDKException e) {
                    showErrorMessage(e, context.getString(R.string.tv_open_card_optional));
                }

            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_revocate_card, functionid = INDEX_CLOSECARDREADER)
    private void closeCardReader() {
        try {
            LogUtils.d("CardReaderFragment", "********* Cancel card reader.");
            mCardReader.cancelCardReader();
            LogUtils.d("CardReaderFragment", "********* Card reader Cancelled.");
            showMessage(context.getString(R.string.msg_revocate_reader) + context.getString(R.string.msg_succ) + "\r\n", MessageTag.NORMAL);
        } catch (Exception e) {
            showErrorMessage(e,context.getString(R.string.msg_revocate_reader_exception));
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
            showErrorMessage(e,context.getString(R.string.msg_error));
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_iccard_is_inserted, functionid = INDEX_ISCARDINSERTED)
    private void isCardInserted() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.dialog_check_status), null, R.layout.dialog_is_card_inserted, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View view) {
                RadioButton rbIC1 = view.findViewById(R.id.rb_ic1);
                RadioButton rbIC2 = view.findViewById(R.id.rb_ic2);
                boolean isExit = false;
                if (rbIC1.isChecked()) {
                    try {
                        isExit = mCardReader.checkCardSlotStatus(ContactCardSlot.IC1);
                    } catch (NSDKException e) {
                        showErrorMessage(e, e.getMessage());
                    }
                } else if (rbIC2.isChecked()) {
                    try {
                        isExit = mCardReader.checkCardSlotStatus(ContactCardSlot.IC2);
                    } catch (NSDKException e) {
                        showErrorMessage(e, e.getMessage());
                    }
                }
                if (isExit) {
                    showMessage(context.getString(R.string.card_in_slot) + "\r\n", MessageTag.DATA);
                } else {
                    showMessage(context.getString(R.string.no_card_in_slot) + "\r\n", MessageTag.DATA);
                }
            }
        });
    }

    private CardReaderListener cardReaderListener = new CardReaderListener() {
        @Override
        public void onTimeout() {
            endTime = System.currentTimeMillis();
            LogUtils.d("CardReaderFragment", "********* Timeout in listener. timeout:" + (endTime - startTime));
            showMessage(context.getString(R.string.msg_timeout) + "\r\n", MessageTag.NORMAL);
        }

        @Override
        public void onCancel() {
            LogUtils.d("CardReaderFragment", "********* Cancelled in listener.");
            showMessage(context.getString(R.string.msg_cancel_open_reader) + "\r\n", MessageTag.NORMAL);
        }

        @Override
        public void onError(int errorCode, String message) {
            showMessage(String.format(Locale.US, "%s:%d", message, errorCode));

        }

        @Override
        public void onFindMagCard(MagCardInfo result) {
            showMessage(context.getString(R.string.msg_cardreader_swiper), MessageTag.DATA);
            try {
                byte[] firstTrackData = result.getTrack1Data();
                byte[] secondTrack = result.getTrack2Data();
                byte[] thirdTrack = result.getTrack3Data();
                byte[] fourthTrack = result.getTrack4Data();
                byte[] fifthTrack = result.getTrack5Data();
                byte[] sixthTrack = result.getTrack6Data();
                String serviceCode = result.getServiceCode();
                String expireDate = result.getValidDate();
                TrackStatus trackStatus[] =  result.getTrackStatus();
                TrackDataFormat[] trackDataFormats = result.getTrackFormats();

                String msg = "";
                if (trackStatus[0] == TrackStatus.GOOD){
                    msg = (firstTrackData == null ? null : new String(firstTrackData, "gbk"));
                } else {
                    msg = String.format("%s", trackStatus[0]);
                }
                showMessage("Track 1: "+ msg, MessageTag.DATA);
                showMessage("Track 1 format: " + trackDataFormats[0].name(), MessageTag.DATA);

                if (trackStatus[1] == TrackStatus.GOOD){
                    msg = (secondTrack == null ? null : new String(secondTrack, "gbk"));
                } else {
                    msg = String.format("%s", trackStatus[1]);
                }
                showMessage("Track 2: "+ msg, MessageTag.DATA);
                showMessage("Track 2 format: " + trackDataFormats[1].name(), MessageTag.DATA);

                if (trackStatus[2] == TrackStatus.GOOD){
                    msg = (thirdTrack == null ? null : new String(thirdTrack, "gbk"));
                } else {
                    msg = String.format("%s", trackStatus[2]);
                }
                showMessage("Track 3: "+ msg, MessageTag.DATA);
                showMessage("Track 3 format: " + trackDataFormats[2].name(), MessageTag.DATA);

                if (trackStatus[3] == TrackStatus.GOOD){
                    msg = (fourthTrack == null ? null : new String(fourthTrack, "gbk"));
                } else {
                    msg = String.format("%s", trackStatus[3]);
                }
                showMessage("Track 4: "+ msg, MessageTag.DATA);
                showMessage("Track 4 format: " + trackDataFormats[3].name(), MessageTag.DATA);

                if (trackStatus[4] == TrackStatus.GOOD){
                    msg = (fifthTrack == null ? null : new String(fifthTrack, "gbk"));
                } else {
                    msg = String.format("%s", trackStatus[4]);
                }
                showMessage("Track 5: "+ msg, MessageTag.DATA);
                showMessage("Track 5 format: " + trackDataFormats[4].name(), MessageTag.DATA);

                if (trackStatus[5] == TrackStatus.GOOD){
                    msg = (sixthTrack == null ? null : new String(sixthTrack, "gbk"));
                } else {
                    msg = String.format("%s", trackStatus[5]);
                }
                showMessage("Track 6: "+ msg, MessageTag.DATA);
                showMessage("Track 6 format: " + trackDataFormats[5].name(), MessageTag.DATA);

                showMessage(context.getString(R.string.msg_service_code) + serviceCode, MessageTag.DATA);
                showMessage(context.getString(R.string.msg_card_vaild_date) + expireDate, MessageTag.DATA);
                showMessage(context.getString(R.string.msg_card_NO) + new String(result.getPanData()), MessageTag.DATA);
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
                ContactlessCardFragment.idmpmm = contactlessCardInfo.getIDmPMm();
                showMessage(String.format("IDmPMm: %s", contactlessCardInfo.getIDmPMm() == null ? "null" : ISOUtils.hexString(contactlessCardInfo.getIDmPMm())));
            }
        }
    };

    private ExtendedCardReaderListener extendedCardReaderListener = new ExtendedCardReaderListener() {
        @Override
        public void onTimeout() {
            endTime = System.currentTimeMillis();
            LogUtils.d("CardReaderFragment", "********* Timeout in listener. timeout:" + (endTime - startTime));
            showMessage(context.getString(R.string.msg_timeout) + "\r\n", MessageTag.NORMAL);
        }

        @Override
        public void onCancel() {
            LogUtils.d("CardReaderFragment", "********* Cancelled in listener.");
            showMessage(context.getString(R.string.msg_cancel_open_reader) + "\r\n", MessageTag.NORMAL);
        }

        @Override
        public void onError(int errorCode, String message) {
            showMessage(String.format(Locale.US, "%s:%d", message, errorCode));

        }

        @Override
        public void onFindMagCard(MagCardInfo result) {
            showMessage(context.getString(R.string.msg_cardreader_swiper), MessageTag.DATA);
            try {
                byte[] firstTrackData = result.getTrack1Data();
                byte[] secondTrack = result.getTrack2Data();
                byte[] thirdTrack = result.getTrack3Data();
                byte[] fourthTrack = result.getTrack4Data();
                byte[] fifthTrack = result.getTrack5Data();
                byte[] sixthTrack = result.getTrack6Data();
                String serviceCode = result.getServiceCode();
                String expireDate = result.getValidDate();
                TrackStatus trackStatus[] =  result.getTrackStatus();
                TrackDataFormat[] trackDataFormats = result.getTrackFormats();

                String msg = "";
                if (trackStatus[0] == TrackStatus.GOOD){
                    msg = (firstTrackData == null ? null : new String(firstTrackData, "gbk"));
                } else {
                    msg = String.format("%s", trackStatus[0]);
                }
                showMessage("Track 1: "+ msg, MessageTag.DATA);
                showMessage("Track 1 format: " + trackDataFormats[0].name(), MessageTag.DATA);

                if (trackStatus[1] == TrackStatus.GOOD){
                    msg = (secondTrack == null ? null : new String(secondTrack, "gbk"));
                } else {
                    msg = String.format("%s", trackStatus[1]);
                }
                showMessage("Track 2: "+ msg, MessageTag.DATA);
                showMessage("Track 2 format: " + trackDataFormats[1].name(), MessageTag.DATA);

                if (trackStatus[2] == TrackStatus.GOOD){
                    msg = (thirdTrack == null ? null : new String(thirdTrack, "gbk"));
                } else {
                    msg = String.format("%s", trackStatus[2]);
                }
                showMessage("Track 3: "+ msg, MessageTag.DATA);
                showMessage("Track 3 format: " + trackDataFormats[2].name(), MessageTag.DATA);

                if (trackStatus[3] == TrackStatus.GOOD){
                     msg = (fourthTrack == null ? null : new String(fourthTrack, "gbk"));
                } else {
                    msg = String.format("%s", trackStatus[3]);
                }
                showMessage("Track 4: "+ msg, MessageTag.DATA);
                showMessage("Track 4 format: " + trackDataFormats[3].name(), MessageTag.DATA);

                if (trackStatus[4] == TrackStatus.GOOD){
                    msg = (fifthTrack == null ? null : new String(fifthTrack, "gbk"));
                } else {
                    msg = String.format("%s", trackStatus[4]);
                }
                showMessage("Track 5: "+ msg, MessageTag.DATA);
                showMessage("Track 5 format: " + trackDataFormats[4].name(), MessageTag.DATA);

                if (trackStatus[5] == TrackStatus.GOOD){
                    msg = (sixthTrack == null ? null : new String(sixthTrack, "gbk"));
                } else {
                    msg = String.format("%s", trackStatus[5]);
                }
                showMessage("Track 6: "+ msg, MessageTag.DATA);
                showMessage("Track 6 format: " + trackDataFormats[5].name(), MessageTag.DATA);

                showMessage(context.getString(R.string.msg_service_code) + serviceCode, MessageTag.DATA);
                showMessage(context.getString(R.string.msg_card_vaild_date) + expireDate, MessageTag.DATA);
                showMessage(context.getString(R.string.msg_card_NO) + new String(result.getPanData()), MessageTag.DATA);


            } catch (Exception e) {
                e.printStackTrace();
                showErrorMessage(e, context.getString(R.string.msg_get_trackdata_plain_error));
                showMessage(context.getString(R.string.msg_check_mainkey_workingkey_AID_RID) + "\r\n", MessageTag.ERROR);
                showMessage(context.getString(R.string.msg_check_cardReader_and_swipe_again) + "\r\n", MessageTag.ERROR);
            }
        }
        @Override
        public void onFindContactCard() {
            showMessage(context.getString(R.string.msg_cradreader_insert) + " in IC1.");
        }

        @Override
        public void onFindContactCards(ContactCardSlot[] cardSlots) {
            for (ContactCardSlot cardSlot : cardSlots) {
                showMessage(context.getString(R.string.msg_cradreader_insert) + " in:" + cardSlot.name(), MessageTag.DATA);
            }

        }

        @Override
        public void onFindContactlessCard(ContactlessCardType contactlessCardType, ContactlessCardInfo contactlessCardInfo) {
            showMessage(String.format("%s (%s)", context.getString(R.string.msg_cardreader_rfcard), contactlessCardType), MessageTag.DATA);
            if (contactlessCardType == ContactlessCardType.TYPE_F) {
                ContactlessCardFragment.idmpmm = contactlessCardInfo.getIDmPMm();
                showMessage(String.format("IDmPMm: %s", contactlessCardInfo.getIDmPMm() == null ? "null" : ISOUtils.hexString(contactlessCardInfo.getIDmPMm())));
            }
        }
    };
}
