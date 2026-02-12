package com.newland.sdkdemo.fragment;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import com.newland.NlBluetooth.control.BluetoothController;
import com.newland.ndk.NdkApiManager;
import com.newland.sdk.module.cardreader.CardReaderExtParams;
import com.newland.sdk.module.cardreader.CardReaderListener;
import com.newland.sdk.module.cardreader.CardReaderModule;
import com.newland.sdk.module.cardreader.CardType;
import com.newland.sdk.module.cardreader.RFCardInfo;
import com.newland.sdk.module.emv.EMVModule;
import com.newland.sdk.module.emv.EMVTransController;
import com.newland.sdk.module.emv.EmvExtParams;
import com.newland.sdk.module.emv.TransactionExtParams;
import com.newland.sdk.module.emv.TransactionType;
import com.newland.sdk.module.externalCardreader.ExtCardReaderModule;
import com.newland.sdk.module.externalPin.PinpadInitExtParams;
import com.newland.sdk.module.externaliccard.ExtICCardModule;
import com.newland.sdk.module.externalmagic.ExtMagicCardModule;
import com.newland.sdk.module.iccard.ICCardType;
import com.newland.sdk.module.pin.AlgorithmMode;
import com.newland.sdk.module.pin.CipherMode;
import com.newland.sdk.module.pin.KeyManagement;
import com.newland.sdk.module.rfcard.RFCardType;
import com.newland.sdk.module.serialport.Baudrate;
import com.newland.sdk.module.serialport.PortType;
import com.newland.sdk.module.swiper.SwipResult;
import com.newland.sdk.module.swiper.SwiperReadModel;
import com.newland.sdk.utils.ISOUtils;
import com.newland.sdkdemo.AppConfig;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.adapter.LayoutMode;
import com.newland.sdkdemo.annotation.MethodGridEntity;
import com.newland.sdkdemo.event.EMVListener;
import com.newland.sdkdemo.utils.DialogUtils;
import com.newland.sdkdemo.utils.MessageTag;
import com.newland.sdkdemo.utils.MyRadioGroup;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author youjf
 * @description
 * @date 2020/12/28
 * @since V3.10.33
 */
public class ExternalCardReaderFragment extends BaseFragment {
    private ExtICCardModule extICCardModule;
    private ExtMagicCardModule extMagicCardModule;
    private ExtCardReaderModule extCardReaderModule;
    private EMVModule emvModule;

    private EMVTransController controller;
    private ICCardType icCardType = ICCardType.CPUCARD;


    public ExternalCardReaderFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_external_cardrader);
    }

    @Override
    public void initData() {
        extCardReaderModule = moduleManage.getExtCardReaderModule();
        extICCardModule = moduleManage.getExtICCardModule();
        extMagicCardModule = moduleManage.getExtMagCardModule();
        extICCardModule = moduleManage.getExtICCardModule();
        emvModule = moduleManage.getEMVModule();
        AppConfig.isUsePinpadByDockUSB=false;
        AppConfig.isUsePinpadByDockRS232=false;

    }

    @Override
    public Object getModule() {
        return ExternalCardReaderFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.tv_external_pin_init, functionid = 0)
    private void initExternalPinpad() {
        DialogUtils.createSingleChoiceDialog(context,"select module",new String[]{"just pinpad","BluetoothBase pinpad port","BluetoothBase USB1 port","USB"},new DialogUtils.SingleChoiceDialogCallback(){
            @Override
        public void onResult(int id) {
            if(id<0){
                return;
            }
            if(id==0){
                boolean result =extCardReaderModule.init(null);
                if (result) {
                    showMessage(context.getString(R.string.msg_ext_pininput_init_pinpad_success) + "\r\n", MessageTag.NORMAL);
                } else {
                    showMessage(context.getString(R.string.msg_ext_pininput_init_pinpad_exception) + "\r\n", MessageTag.ERROR);
                }
            }else if(id==1){
                PinpadInitExtParams pinpadInitExtParams = new PinpadInitExtParams(PortType.BLEBASE_RS232,null,null,null);
                boolean result = extCardReaderModule.init(pinpadInitExtParams);
//                BluetoothController.getInstance().setLog(true);

                AppConfig.isUsePinpadByDockRS232=true;
                if (result) {
                    showMessage(context.getString(R.string.msg_ext_pininput_init_pinpad_success) + "\r\n", MessageTag.NORMAL);
                } else {
                    showMessage(context.getString(R.string.msg_ext_pininput_init_pinpad_exception) + "\r\n", MessageTag.ERROR);
                }
            }else if(id==2){
                PinpadInitExtParams pinpadInitExtParams = new PinpadInitExtParams(PortType.BLEBASE_USB1,null,null,null);
                boolean result = extCardReaderModule.init(pinpadInitExtParams);
//                BluetoothController.getInstance().setLog(true);

                AppConfig.isUsePinpadByDockUSB=true;
                if (result) {
                    showMessage(context.getString(R.string.msg_ext_pininput_init_pinpad_success) + "\r\n", MessageTag.NORMAL);
                } else {
                    showMessage(context.getString(R.string.msg_ext_pininput_init_pinpad_exception) + "\r\n", MessageTag.ERROR);
                }
            }else if(id==3){
                PinpadInitExtParams pinpadInitExtParams = new PinpadInitExtParams(PortType.USB,null,null,null);
                boolean result = extCardReaderModule.init(pinpadInitExtParams);

                if (result) {
                    showMessage(context.getString(R.string.msg_ext_pininput_init_pinpad_success) + "\r\n", MessageTag.NORMAL);
                } else {
                    showMessage(context.getString(R.string.msg_ext_pininput_init_pinpad_exception) + "\r\n", MessageTag.ERROR);
                }
            }
        }
    });
    }

    @MethodGridEntity(btnnameid = R.string.opencardreader, functionid = 1)
    private void openCardReader() {
        try {
            List<CardType> cardTypeList = new ArrayList<CardType>();
            String[] items = new String[]{context.getString(R.string.mag), context.getString(R.string.contact), context.getString(R.string.contactless)};
            DialogUtils.createMultiChoiceDialog(context, context.getString(R.string.card_readding_mode), items, new DialogUtils.MultiChoiceDialogCallback() {
                @Override
                public void onResult(ArrayList<Integer> yourChoices) {
                    if (yourChoices == null || yourChoices.size() < 1) {
                        return;
                    }
                    for (Integer choice : yourChoices) {
                        if (choice == 0) {
                            cardTypeList.add(CardType.MSGCARD);
                        } else if (choice == 1) {
                            cardTypeList.add(CardType.ICCARD);
                        } else if (choice == 2) {
                            cardTypeList.add(CardType.RFCARD);
                        }
                    }
                    showMessage(context.getString(R.string.msg_swipe_insert_rf_card) + "\r\n", MessageTag.TIP);
                    CardReaderExtParams extParams = new CardReaderExtParams();
                    extParams.setFirstLineMessage("Strat reading");
                    extParams.setSecondLineMessage("Testing1..Testing1.");
                    extParams.setThirdLineMessage("Testing2Testing1");
                    extParams.setFourthLineMessage("Testing3Testing1");

                    /**
                     *   If need get the track data from non-BankCard. You need set the setCheckUnionCard like below.
                     *
                     */
                    // extParams.setCheckUnionCard(false);

                    extCardReaderModule.openCardReader(cardTypeList.toArray(new CardType[cardTypeList.size()]), 30, new CardReaderListener() {
                        @Override
                        public void onTimeout() {
                            showMessage("onTimeout", MessageTag.ERROR);
                        }

                        @Override
                        public void onCancel() {
                            showMessage("onCancel", MessageTag.ERROR);
                        }

                        @Override
                        public void onError(int errorCode, String message) {
                            showMessage("onError:" + message, MessageTag.ERROR);
                        }

                        @Override
                        public void onFindMagCard(boolean isSuccessful) {
                            showMessage("onFindMagCard:" + isSuccessful, MessageTag.TIP);
                            KeyManagement keyManagement = KeyManagement.MKSK;
                            int keyIndex = AppConfig.Pin.MKSK_DES_INDEX_WK_TRACK;
                            AlgorithmMode algorithmMode = AlgorithmMode.DES;
                            if (AppConfig.DUKPT_DES.equals(AppConfig.KEY_SYS_ALG)) {
                                keyManagement = KeyManagement.DUKPT;
                                keyIndex = AppConfig.Pin.DUKPT_DES_INDEX;
                            }
                            if (AppConfig.MKSK_AES.equals(AppConfig.KEY_SYS_ALG)) {
                                keyIndex = AppConfig.Pin.MKSK_AES_INDEX_WK_TRACK;
                                algorithmMode = AlgorithmMode.AES;
                            }
//                            SwipResult swipResult = extMagicCardModule.readEncryptResult(keyManagement,129,new SwiperReadModel[]{SwiperReadModel.FIRST_TRACK,SwiperReadModel.SECOND_TRACK,SwiperReadModel.THIRD_TRACK},algorithmMode,CipherMode.ECB,null);
                            SwipResult swipResult = extMagicCardModule.readPlainResult(new SwiperReadModel[]{SwiperReadModel.FIRST_TRACK, SwiperReadModel.SECOND_TRACK, SwiperReadModel.THIRD_TRACK});
                            if (swipResult == null) {
                                showMessage("swipResult==null", MessageTag.ERROR);
                                return;
                            }
                            byte[] firstData = swipResult.getFirstTrackData();
                            byte[] secondData = swipResult.getSecondTrackData();
                            byte[] thirdData = swipResult.getThirdTrackData();
                            String acctNo = swipResult.getAccount().getAcctNo();
                            String validDate = swipResult.getValidDate();
                            String serviceCode = swipResult.getServiceCode();
                            showMessage("FirstTrackData: " + (firstData == null ? null : ISOUtils.hexString(firstData)), MessageTag.DATA);
                            showMessage("SecondTrackData: " + (secondData == null ? null : ISOUtils.hexString(secondData)), MessageTag.DATA);
                            showMessage("ThirdTrackData: " + (thirdData == null ? null : ISOUtils.hexString(thirdData)), MessageTag.DATA);
                            showMessage("AcctNo: " + acctNo);
                            showMessage("ValidDate: " + validDate);
                            showMessage("ServiceCode: " + serviceCode);
                        }

                        @Override
                        public void onFindICCard() {
                            showMessage("onFindICCard", MessageTag.TIP);
                            AppConfig.isExternalEmv = true;
                            EmvExtParams extParams;
                            if (AppConfig.isUsePinpadByDockUSB) {
                                extParams = new EmvExtParams(Baudrate.BPS115200,PortType.BLEBASE_USB1);
                            }else if(AppConfig.isUsePinpadByDockRS232){
                                extParams = new EmvExtParams(Baudrate.BPS115200,PortType.BLEBASE_RS232);
                            }else{
                                extParams = new EmvExtParams(true);
                            }
                            extParams.setMediaType(0x00);
                            emvModule.init(context, extParams);
                            controller = emvModule.getEmvTransController(new EMVListener(context, emvModule, TransactionType.STANDARD));
                            controller.startEMV(TransactionType.STANDARD, new BigDecimal(0.01), false, null);
                        }

                        @Override
                        public void onFindRFCard(@Nullable RFCardType rfCardType, @Nullable RFCardInfo rfCardInfo) {
                            showMessage("onFindRFCard", MessageTag.TIP);
                            AppConfig.isExternalEmv = true;
                            EmvExtParams extParams;
                            if (AppConfig.isUsePinpadByDockUSB) {
                                extParams = new EmvExtParams(Baudrate.BPS115200,PortType.BLEBASE_USB1);
                            }else if(AppConfig.isUsePinpadByDockRS232){
                                extParams = new EmvExtParams(Baudrate.BPS115200,PortType.BLEBASE_RS232);
                            }else{
                                extParams = new EmvExtParams(true);
                            }
                            extParams.setMediaType(0x01);
                            emvModule.init(context, extParams);
                            controller = emvModule.getEmvTransController(new EMVListener(context, emvModule, TransactionType.STANDARD));
                            controller.startEMV(TransactionType.STANDARD, new BigDecimal(0.01), false, null);
                        }
                    }, extParams);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Exception:" + e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_revocate_card, functionid = 2)
    private void cancelCardReader() {
        try {
            setFunRunning(false);
            extCardReaderModule.cancelCardReader();
            showMessage(context.getString(R.string.msg_revocate_reader) + context.getString(R.string.msg_succ) + "\r\n", MessageTag.NORMAL);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Exception:" + e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_iccard_poweron, functionid = 3)
    private void icCardPowerOn() {

        try {
            byte[] result = extICCardModule.powerOn();
            showMessage(context.getString(R.string.msg_poweron_result) + (result == null ? null : ISOUtils.hexString(result)) + "\r\n", MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Exception:" + e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_iccard_transation, functionid = 4)
    private void icCardCommunication() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_iccard_transation), null, R.layout.dialog_iccard_communication, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                try {
                    if (id == -1) {//cancel
                        return;
                    }
                    EditText edtText = dialogView.findViewById(R.id.edit_ICCardSend);
                    String str = edtText.getText().toString();//Get communication data
                    byte req[] = ISOUtils.hex2byte(str);
                    byte back[] = extICCardModule.transmit(req, null);
                    showMessage(context.getString(R.string.msg_receive_data) + (back == null ? null : ISOUtils.hexString(back)), MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_iccard_comm_succ) + "\r\n", MessageTag.DATA);
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(context.getString(R.string.msg_iccard_comm_ex) + e, MessageTag.ERROR);
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_iccard_powerof, functionid = 5)
    private void icCardPowerOff() {
        try {
            extICCardModule.powerOff();
            showMessage(context.getString(R.string.msg_poweroff_end), MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("icCard PowerOff Exception:" + e, MessageTag.ERROR);
        }

    }

    @MethodGridEntity(btnnameid = R.string.tv_iccard_state, functionid = 6)
    private void icCardSlotState() {
        try {
            boolean isCardIn = extICCardModule.isCardIn();
            showMessage("is card in slot:" + isCardIn);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("check ic Card SlotState Exception:" + e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_iccard_type, functionid = 7)
    private void selectICCardType() {
        try {
            DialogUtils.createCustomDialog(context, context.getString(R.string.tv_iccard_type), null, R.layout.dialog_exticcard_poweron, new DialogUtils.CustomDialogCallback() {
                @Override
                public void onResult(int id, View dialogView) {
                    try {
                        if (id == -1) {//cancel
                            return;
                        }
                        icCardType = getICCardType(dialogView);
                        boolean result = extICCardModule.setICCardType(icCardType, null);
                        showMessage("select IC card type result:" + result, MessageTag.DATA);
                    } catch (Exception e) {
                        e.printStackTrace();
                        showMessage(context.getString(R.string.msg_pl_check_inserted) + "\r\n", MessageTag.ERROR);
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("select IC card type Exception:" + e, MessageTag.ERROR);
        }
    }


        private ICCardType getICCardType(View dialogView) {
            try {
                MyRadioGroup iccardType = (MyRadioGroup) dialogView.findViewById(R.id.radioGroup_iccard_type);
                int tyepCheckedId = iccardType.getCheckedRadioButtonId();


                switch (tyepCheckedId) {
                    case R.id.radio_CPUCARD:
                        icCardType = ICCardType.CPUCARD;
                        break;
                    case R.id.radio_SAM1:
                        icCardType = ICCardType.SAM1;
                        break;
                    case R.id.radio_SAM2:
                        icCardType = ICCardType.SAM2;
                        break;
                    case R.id.radio_SLE44X2:
                        icCardType = ICCardType.SLE44X2;
                        break;
                    case R.id.radio_SLE44X8:
                        icCardType = ICCardType.SLE44X8;
                        break;
                    case R.id.radio_AT88SC102:
                        icCardType = ICCardType.AT88SC102;
                        break;
                    case R.id.radio_AT88SC1604:
                        icCardType = ICCardType.AT88SC1604;
                        break;
                    case R.id.radio_AT88SC1608:
                        icCardType = ICCardType.AT88SC1608;
                        break;
                    case R.id.radio_ISO7816:
                        icCardType = ICCardType.ISO7816;
                        break;
                    case R.id.radio_AT88SC153:
                        icCardType = ICCardType.AT88SC153;
                        break;
                    case R.id.radio_AT24C01:
                        icCardType = ICCardType.AT24C01;
                        break;
                    case R.id.radio_AT24C02:
                        icCardType = ICCardType.AT24C02;
                        break;
                    case R.id.radio_AT24C04:
                        icCardType = ICCardType.AT24C04;
                        break;
                    case R.id.radio_AT24C08:
                        icCardType = ICCardType.AT24C08;
                        break;
                    case R.id.radio_AT24C16:
                        icCardType = ICCardType.AT24C16;
                        break;
                    case R.id.radio_AT24C33:
                        icCardType = ICCardType.AT24C32;
                        break;
                    case R.id.radio_AT24C64:
                        icCardType = ICCardType.AT24C64;
                        break;
                    case R.id.radio_AT24C128:
                        icCardType = ICCardType.AT24C128;
                        break;
                    case R.id.radio_AT24C256:
                        icCardType = ICCardType.AT24C256;
                        break;
                }
                return icCardType;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ICCardType.CPUCARD;
        }

}
