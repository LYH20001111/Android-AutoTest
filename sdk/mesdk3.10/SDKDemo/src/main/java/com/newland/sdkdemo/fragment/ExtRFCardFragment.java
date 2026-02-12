package com.newland.sdkdemo.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import com.newland.sdk.module.emv.EMVModule;
import com.newland.sdk.module.emv.EMVTransController;
import com.newland.sdk.module.emv.EmvExtParams;
import com.newland.sdk.module.externalPin.PinpadInitExtParams;
import com.newland.sdk.module.externalrfcard.ExtRFCardModule;
import com.newland.sdk.module.light.LightColor;
import com.newland.sdk.module.light.LightState;
import com.newland.sdk.module.rfcard.RFCardType;
import com.newland.sdk.module.rfcard.RFKeyMode;
import com.newland.sdk.module.rfcard.RFResult;
import com.newland.sdk.module.serialport.PortType;
import com.newland.sdk.mtype.util.Dump;
import com.newland.sdk.utils.ISOUtils;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.adapter.LayoutMode;
import com.newland.sdkdemo.annotation.MethodGridEntity;
import com.newland.sdkdemo.utils.DialogUtils;
import com.newland.sdkdemo.utils.MessageTag;

import java.util.ArrayList;
import java.util.List;

public class ExtRFCardFragment extends BaseFragment {

    private ExtRFCardModule extRFCard;
    private boolean isOverseas = false;
    private String snr;
    private byte[] IDmAndPMm;

    public ExtRFCardFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.module_ext_rfcard);
    }

    @Override
    public void initData() {
        extRFCard = moduleManage.getExtRFCardModule();
    }

    @Override
    public Object getModule() {
        return ExtRFCardFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.tv_external_pin_init, functionid = 1)
    private void initExternalPinpad() {
        try {
            DialogUtils.createSingleChoiceDialog(context,"select module",new String[]{"just pinpad","BluetoothBase pinpad port","BluetoothBase and USB1 port","USB"},new DialogUtils.SingleChoiceDialogCallback(){
                @Override
                public void onResult(int id) {
                    if(id<0){
                        return;
                    }
                    if(id==0){
                        PinpadInitExtParams pinpadInitExtParams = new PinpadInitExtParams(true);
                        pinpadInitExtParams.setInit(true);
                        boolean init = extRFCard.init(pinpadInitExtParams);
                        if (init) {
                            showMessage(context.getString(R.string.msg_ext_pininput_init_pinpad_success) + "\r\n", MessageTag.NORMAL);
                        } else {
                            showMessage(context.getString(R.string.msg_ext_pininput_init_pinpad_exception) + "\r\n", MessageTag.ERROR);
                        }
                    }else if(id==1){
                        PinpadInitExtParams pinpadInitExtParams = new PinpadInitExtParams(PortType.BLEBASE_RS232,null,null,null);
                        boolean init = extRFCard.init(pinpadInitExtParams);
                        if (init) {
                            showMessage(context.getString(R.string.msg_ext_pininput_init_pinpad_success) + "\r\n", MessageTag.NORMAL);
                        } else {
                            showMessage(context.getString(R.string.msg_ext_pininput_init_pinpad_exception) + "\r\n", MessageTag.ERROR);
                        }
                    }else if(id==2){
                        PinpadInitExtParams pinpadInitExtParams = new PinpadInitExtParams(PortType.BLEBASE_USB1,null,null,null);
                        boolean init = extRFCard.init(pinpadInitExtParams);
                        if (init) {
                            showMessage(context.getString(R.string.msg_ext_pininput_init_pinpad_success) + "\r\n", MessageTag.NORMAL);
                        } else {
                            showMessage(context.getString(R.string.msg_ext_pininput_init_pinpad_exception) + "\r\n", MessageTag.ERROR);
                        }
                    }else if(id==3){
                        PinpadInitExtParams pinpadInitExtParams = new PinpadInitExtParams(PortType.USB,null,null,null);
                        boolean result = extRFCard.init(pinpadInitExtParams);

                        if (result) {
                            showMessage(context.getString(R.string.msg_ext_pininput_init_pinpad_success) + "\r\n", MessageTag.NORMAL);
                        } else {
                            showMessage(context.getString(R.string.msg_ext_pininput_init_pinpad_exception) + "\r\n", MessageTag.ERROR);
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_ext_pininput_init_pinpad_exception) + e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_rf_power_on, functionid = 2)
    private void powerOn() {
        DialogUtils.createCustomDialog(context, R.string.tv_rf_power_on, null, R.layout.dialog_rfcard_power_on, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                CheckBox checkBoxAcard = view.findViewById(R.id.checkbox_rf_A_card);
                CheckBox checkBoxBcard = view.findViewById(R.id.checkbox_rf_B_card);
                CheckBox checkBoxM1card = view.findViewById(R.id.checkbox_rf_M1_card);
                CheckBox checkBoxM0card = view.findViewById(R.id.checkbox_rf_M0_card);
                CheckBox checkBoxFelicaCard = view.findViewById(R.id.checkbox_rf_felic_card);
                checkBoxAcard.setChecked(true);
                checkBoxBcard.setChecked(true);
                checkBoxM1card.setChecked(true);
                checkBoxM0card.setChecked(true);
                checkBoxFelicaCard.setChecked(false);
                Switch aSwitch = view.findViewById(R.id.switch_extended_param);
                aSwitch.setChecked(false);
                final LinearLayout extenedParamLL = view.findViewById(R.id.extended_param_ll);
                extenedParamLL.setVisibility(View.GONE);
                aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            extenedParamLL.setVisibility(View.VISIBLE);
                        } else {
                            extenedParamLL.setVisibility(View.GONE);
                        }
                    }
                });

                view.findViewById(R.id.rf_power_on_time_out).setVisibility(View.GONE);
                checkBoxM0card.setVisibility(View.GONE);
                checkBoxFelicaCard.setVisibility(View.GONE);
            }

            @Override
            public void onResult(int id, View view) {
                try {
                    showMessage(R.string.msg_rf);
                    List<RFCardType> cardTypeList = new ArrayList<RFCardType>();
                    int timeout = 30;

                    CheckBox checkBoxAcard = view.findViewById(R.id.checkbox_rf_A_card);
                    CheckBox checkBoxBcard = view.findViewById(R.id.checkbox_rf_B_card);
                    CheckBox checkBoxM1card = view.findViewById(R.id.checkbox_rf_M1_card);
                    CheckBox checkBoxM0card = view.findViewById(R.id.checkbox_rf_M0_card);
                    CheckBox checkBoxFelicaCard = view.findViewById(R.id.checkbox_rf_felic_card);

                    if (checkBoxAcard.isChecked()) {
                        cardTypeList.add(RFCardType.ACARD);
                    }
                    if (checkBoxBcard.isChecked()) {
                        cardTypeList.add(RFCardType.BCARD);
                    }
                    if (checkBoxM1card.isChecked()) {
                        cardTypeList.add(RFCardType.M1CARD);
                    }
                    if (checkBoxM0card.isChecked()) {
                        cardTypeList.add(RFCardType.M0CARD);
                    }
                    if (checkBoxFelicaCard.isChecked()) {
                        cardTypeList.add(RFCardType.FELICA_CARD);
                    }

                    EditText editTimeout = view.findViewById(R.id.edit_rf_power_on_timeout);
                    if (editTimeout.getText().toString() != null && editTimeout.getText().toString().length() > 0) {
                        timeout = Integer.valueOf(editTimeout.getText().toString());
                    }
                    Switch aSwitch = view.findViewById(R.id.switch_extended_param);
                    if (aSwitch.isChecked()) {
                        boolean sakIsChecked = ((RadioButton) view.findViewById(R.id.rf_power_on_extended_param_sak)).isChecked();
                        boolean felicaIsChecked = ((RadioButton) view.findViewById(R.id.rf_power_on_extended_param_felica)).isChecked();
                        if (sakIsChecked && felicaIsChecked) {
                            showMessage(context.getString(R.string.msg_error) + "\r\n", MessageTag.ERROR);
                            return;
                        }
                    }
                    RFResult rfResult = extRFCard.powerOn(cardTypeList.toArray(new RFCardType[cardTypeList.size()]), timeout);
                    if (rfResult != null && rfResult.getRfcardType() != null) {
                        showMessage(context.getString(R.string.msg_rf_type) + rfResult.getRfcardType() + "\r\n", MessageTag.DATA);
                    } else {
                        showMessage(context.getString(R.string.common_exception), MessageTag.ERROR);
                        return;
                    }


                    if (rfResult.getSNR() == null) {
                        showMessage(context.getString(R.string.msg_rf_SN_NO) + "\r\n", MessageTag.DATA);
                    } else {
                        snr = ISOUtils.hexString(rfResult.getSNR());
                        showMessage(context.getString(R.string.msg_rf_SN) + ISOUtils.hexString(rfResult.getSNR()) + "\r\n", MessageTag.DATA);
                    }

                    if (rfResult.getATQA() == null) {
                        showMessage(context.getString(R.string.msg_rf_ATQA_null) + "\r\n", MessageTag.DATA);
                    } else {
                        showMessage(context.getString(R.string.msg_rf_ATQA) + Dump.getHexDump(rfResult.getATQA()) + "\r\n", MessageTag.DATA);
                    }

                    showMessage(context.getString(R.string.msg_rf_SAK) + rfResult.getSAK() + "\r\n", MessageTag.DATA);
                    if (rfResult.getRfcardType() == RFCardType.FELICA_CARD && rfResult.getIDmPMm() != null && rfResult.getIDmPMm().length > 0) {
                        IDmAndPMm = new byte[rfResult.getIDmPMm().length];
                        System.arraycopy(rfResult.getIDmPMm(), 0, IDmAndPMm, 0, rfResult.getIDmPMm().length);
                    }
                    showMessage(context.getString(R.string.msg_felica_IDm_PMm) + (rfResult.getIDmPMm()==null?"null":ISOUtils.hexString(rfResult.getIDmPMm())) + "\r\n", MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_rf_ATS) + (rfResult.getATS()==null?"null":ISOUtils.hexString(rfResult.getATS())) + "\r\n", MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_rf_poweron_finished) + "\r\n", MessageTag.NORMAL);
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(context.getString(R.string.msg_rf_poweron_error) + e, MessageTag.ERROR);
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_rf_transmit, functionid = 3)
    private void rfcardCommunication() {
        try {
            String apdu = "0084000004";
            byte req[] = ISOUtils.hex2byte(apdu);
            byte result[] = extRFCard.transmit(req);
            showMessage(context.getString(R.string.msg_send_data) + apdu + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.msg_get_data) + (result == null ? "null" : ISOUtils.hexString(result)) + "\r\n", MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_card_transmit_error) + e + "\r\n", MessageTag.ERROR);
        }
    }


    @MethodGridEntity(btnnameid = R.string.tv_rf_power_off, functionid = 4)
    private void rfcardPowerOff() {
        try {
            boolean result = extRFCard.powerOff();
            showMessage(context.getString(R.string.msg_rf_poweroff_finished) + result + "\r\n", MessageTag.NORMAL);
        } catch (Exception e) {
            showMessage(context.getString(R.string.msg_rf_poweroff_error) + e + "\r\n", MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.ext_rfcard_reset, functionid = 5)
    private void reset() {
        try {
            BaseFragment.setFunRunning(false);
            extRFCard.reset();
            showMessage(context.getString(R.string.ext_rfcard_reset_success), MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.common_exception), MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_rf_is_induct, functionid = 6)
    private void rfcardIsInducted() {
        try {
            boolean isExit = extRFCard.isCardExist();
            if (isExit) {
                showMessage(context.getString(R.string.msg_rf_isInducted) + "\r\n", MessageTag.DATA);
            } else {
                showMessage(context.getString(R.string.msg_rf_is_not_inducted) + "\r\n", MessageTag.DATA);
            }

        } catch (Exception e) {
            showMessage(context.getString(R.string.msg_error) + e, MessageTag.ERROR);
        }
    }


    @MethodGridEntity(btnnameid = R.string.tv_m1_authorization, functionid = 7)
    private void m1Athenticate() {
        String[] items = new String[]{"KEYA_0x60", "KEYA_0x00", "KEYB_0x61", "KEYB_0x01"};
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_m1_authorization), items, R.layout.dialog_m1_external_auth, new DialogUtils.CustomDialogCallback() {

            @Override
            public void onResult(int id, View dialogView) {
                RFKeyMode qpKeyMode = RFKeyMode.KEYA_0X60;
                if (id >= 0) {
                    if (id == 0) {
                        qpKeyMode = RFKeyMode.KEYA_0X60;
                    } else if (id == 1) {
                        qpKeyMode = RFKeyMode.KEYA_0X00;
                    } else if (id == 2) {
                        qpKeyMode = RFKeyMode.KEYB_0X61;
                    } else {
                        qpKeyMode = RFKeyMode.KEYB_0X01;
                    }

                    EditText edtBlockNum = dialogView.findViewById(R.id.edit_qccard_block);
                    EditText edtKey = dialogView.findViewById(R.id.edit_qccard_key);

                    int block = Integer.valueOf(edtBlockNum.getText().toString());
                    byte sn[] = null;

                    if (snr != null) {
                        sn = ISOUtils.hex2byte(snr);
                    } else {
                        showMessage(context.getString(R.string.msg_check_rf_isPoweredon) + "\r\n", MessageTag.ERROR);
                        return;
                    }
                    byte key[] = ISOUtils.hex2byte(edtKey.getText().toString());
                    if (block >= 0 && block <= 255 && key.length == 6 && sn != null && sn.length == 4) {
                        try {
                            boolean result = extRFCard.m1Authenticate(qpKeyMode, sn, block, key);
                            showMessage(context.getString(R.string.msg_rf_external_key_auth_finished) + result + "\r\n", MessageTag.DATA);
                            showMessage(context.getString(R.string.msg_key_mode) + qpKeyMode + "\r\n", MessageTag.DATA);
                            showMessage(context.getString(R.string.msg_SNR_SN_NO) + (snr == null ? "null" : ISOUtils.hexString(sn)) + "\r\n", MessageTag.DATA);
                            showMessage(context.getString(R.string.msg_security_block_NO) + block + "\r\n", MessageTag.DATA);
                            showMessage(context.getString(R.string.msg_external_key) + (key == null ? "null" : ISOUtils.hexString(key)) + "\r\n", MessageTag.DATA);
                        } catch (Exception e) {
                            e.printStackTrace();
                            showMessage(context.getString(R.string.msg_error) + e, MessageTag.ERROR);
                        }

                    } else {
                        showMessage(context.getString(R.string.msg_write_illegal), MessageTag.ERROR);
                    }
                } else {
                    showMessage(context.getString(R.string.dialog_btn_cancel), MessageTag.DATA);
                }

            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_m1_write_block, functionid = 8)
    public void writeBlockData() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_m1_write_block), null, R.layout.dialog_m1_write, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                try {
                    if(id == -1){//cancel
                        return;
                    }
                    EditText edit_qccard_block = (EditText) dialogView.findViewById(R.id.edit_qccard_block);
                    EditText edit_qccard_data = (EditText) dialogView.findViewById(R.id.edit_qccard_data);
                    int block = Integer.valueOf(edit_qccard_block.getText().toString());
                    byte input[] = ISOUtils.hex2byte(edit_qccard_data.getText().toString());
                    if (block >= 0 && block <= 255 && input.length == 16) {
                        boolean result = extRFCard.writeBlockData(block, input);
                        showMessage(context.getString(R.string.msg_write_block_data_result) + result + "\r\n", MessageTag.NORMAL);
                        showMessage(context.getString(R.string.msg_storage_block) + block + "\r\n", MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_write_block_data) + (input == null ? "null" : ISOUtils.hexString(input)) + "\r\n", MessageTag.DATA);
                    } else {
                        showMessage(context.getString(R.string.msg_write_illegal) + "\r\n", MessageTag.ERROR);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(context.getString(R.string.msg_write_block_data_error) + e.getMessage() + "\r\n", MessageTag.ERROR);
                    showMessage(context.getString(R.string.msg_check_rf_isPoweredon) + "\r\n", MessageTag.ERROR);
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_m1_read_block, functionid = 9)
    private void m1CardRead() {
        TextView tip = LayoutInflater.from(context).inflate(R.layout.dialog_edittext, null).findViewById(R.id.textview_tip);
        tip.setText(context.getString(R.string.dialog_tv_qccard_block));
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_m1_read_block), null, R.layout.dialog_edittext, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                try {
                    if(id == -1){//cancel
                        return;
                    }
                    EditText editTextData = dialogView.findViewById(R.id.edit_data);
                    int block = Integer.valueOf(editTextData.getText().toString());
                    if (block >= 0 && block <= 255) {
                        byte output[] = extRFCard.readBlockData(block);
                        showMessage(context.getString(R.string.msg_storage_block) + block + "\r\n", MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_data) + (output == null ? "null" : ISOUtils.hexString(output)) + "\r\n", MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_read_block_finished) + "\r\n", MessageTag.NORMAL);
                    } else {
                        showMessage(context.getString(R.string.msg_input_illegal) + "\r\n", MessageTag.ERROR);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(context.getString(R.string.msg_read_block_error) + e + "\r\n", MessageTag.ERROR);
                }

            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_m1_increment, functionid = 10)
    private void m1CardIncrease() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_m1_increment), null, R.layout.dialog_m1_operate, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                try {
                    if(id == -1){//cancel
                        return;
                    }
                    EditText edit_qccard_block = (EditText) dialogView.findViewById(R.id.edit_m1_block);
                    EditText edit_qccard_data = (EditText) dialogView.findViewById(R.id.edit_m1_data);
                    int block = Integer.valueOf(edit_qccard_block.getText().toString());
                    byte input[] = ISOUtils.hex2byte(edit_qccard_data.getText().toString());
                    if (block >= 0 && block <= 255 && input.length == 4) {
                        boolean result = extRFCard.m1Increment(block, input);
                        showMessage(context.getString(R.string.msg_increase_operation_finished) + result , MessageTag.NORMAL);
                        showMessage(context.getString(R.string.msg_storage_block) + block + "\r\n", MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_data) + (input == null ? "null" : ISOUtils.hexString(input)) + "\r\n", MessageTag.DATA);
                    } else {
                        showMessage(context.getString(R.string.msg_input_illegal) + "\r\n", MessageTag.ERROR);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(context.getString(R.string.msg_increase_operation_error) + e, MessageTag.ERROR);
                    showMessage(context.getString(R.string.msg_check_rf_isPoweredon) + "\r\n", MessageTag.ERROR);
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_m1_decrement, functionid = 11)
    private void m1CardDecrease() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_m1_decrement), null, R.layout.dialog_m1_operate, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                try {
                    if(id == -1){//cancel
                        return;
                    }
                    EditText edit_qccard_block = (EditText) dialogView.findViewById(R.id.edit_m1_block);
                    EditText edit_qccard_data = (EditText) dialogView.findViewById(R.id.edit_m1_data);
                    int block = Integer.valueOf(edit_qccard_block.getText().toString());
                    byte input[] = ISOUtils.hex2byte(edit_qccard_data.getText().toString());
                    if (block >= 0 && block <= 255 && input.length == 4) {
                        boolean result = extRFCard.m1Decrement(block, input);
                        showMessage(context.getString(R.string.msg_decrease_operation_finished) + result + "\r\n", MessageTag.NORMAL);
                        showMessage(context.getString(R.string.msg_storage_block) + block + "\r\n", MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_data) + (input == null ? "null" : ISOUtils.hexString(input)) + "\r\n", MessageTag.DATA);
                    } else {
                        showMessage(context.getString(R.string.msg_input_illegal) + "\r\n", MessageTag.ERROR);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(context.getString(R.string.msg_decrease_operation_error) + e.getMessage() + "\r\n", MessageTag.ERROR);
                    showMessage(context.getString(R.string.msg_check_rf_isPoweredon) + "\r\n", MessageTag.ERROR);
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.ext_rfcard_getats, functionid = 12)
    private void getATS() {
        try {
            byte[] cardATS = extRFCard.getCardATS();
            showMessage(context.getString(R.string.ext_rfcard_getats) + "：" + (cardATS == null ? null : ISOUtils.hexString(cardATS)));
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error),MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_light_poweron, functionid = 13)
    private void turnOnLED() {
        try {
            boolean result = extRFCard.operateLight(new LightColor[]{LightColor.BLUE,
                    LightColor.GREEN, LightColor.RED, LightColor.YELLOW}, LightState.TURNON);
            if (result) {
                showMessage(context.getString(R.string.msg_byr_light_open) + context.getString(R.string.msg_common_succ) + "\r\n", MessageTag.NORMAL);
            } else {
                showMessage(context.getString(R.string.msg_byr_light_open) + context.getString(R.string.msg_common_failed) + "\r\n", MessageTag.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error),MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_light_powerof, functionid = 14)
    private void turnOffLED() {
        try {
            boolean result = extRFCard.operateLight(new LightColor[]{LightColor.BLUE,
                    LightColor.GREEN, LightColor.RED, LightColor.YELLOW}, LightState.TURNOFF);
            if (result) {
                showMessage(context.getString(R.string.msg_light_off) + context.getString(R.string.msg_common_succ) + "\r\n", MessageTag.NORMAL);
            } else {
                showMessage(context.getString(R.string.msg_light_off) + context.getString(R.string.msg_common_failed) + "\r\n", MessageTag.ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error),MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.blink_on, functionid = 15)
    private void blinkLightTurnOn() {
        try {
            boolean result = extRFCard.operateLight(new LightColor[]{LightColor.BLUE,
                    LightColor.GREEN, LightColor.RED, LightColor.YELLOW}, LightState.BLINK);
            if (result) {
                showMessage(context.getString(R.string.blink_on) + context.getString(R.string.msg_common_succ) + "\r\n", MessageTag.NORMAL);
            } else {
                showMessage(context.getString(R.string.blink_on) + context.getString(R.string.msg_common_failed) + "\r\n", MessageTag.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error),MessageTag.ERROR);
        }
    }
}
