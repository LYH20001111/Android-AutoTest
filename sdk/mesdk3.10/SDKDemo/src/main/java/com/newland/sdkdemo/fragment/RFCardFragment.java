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

import com.newland.sdk.module.rfcard.FelicaParams;
import com.newland.sdk.module.rfcard.RFCardMode;
import com.newland.sdk.module.rfcard.RFCardModule;
import com.newland.sdk.module.rfcard.RFCardPowerOnExtParams;
import com.newland.sdk.module.rfcard.RFCardType;
import com.newland.sdk.module.rfcard.RFKeyMode;
import com.newland.sdk.module.rfcard.RFResult;
import com.newland.sdk.mtype.util.Dump;
import com.newland.sdk.utils.ISOUtils;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.adapter.LayoutMode;
import com.newland.sdkdemo.annotation.MethodGridEntity;
import com.newland.sdkdemo.utils.DialogUtils;
import com.newland.sdkdemo.utils.MessageTag;

import java.util.ArrayList;
import java.util.List;

/**
 * Author by bxy, Date on 2019/5/11 0011.
 */
public class RFCardFragment extends BaseFragment {
    private RFCardModule rfCardModule;
    private String snr;
    private byte[] IDmAndPMm = null;

    private static final int INDEX_RFCARD_POWERON = 1;
    private static final int INDEX_RFCARD_INDUCT = 2;
    private static final int INDEX_RFCARD_POWEROFF = 3;


    private static final int INDEX_RFCARD_FILL0 = 4;
    private static final int INDEX_RFCARD_FILL1 = 5;
    private static final int INDEX_RFCARD_FILL2 = 6;

    private static final int INDEX_RFCARD_TRANSMIT = 7;
    private static final int INDEX_RFCARD_FILL3 = 8;
    private static final int INDEX_RFCARD_FILL4 = 9;

    private static final int INDEX_RFCARD_FILL5 = 10;
    private static final int INDEX_RFCARD_FILL6 = 11;
    private static final int INDEX_RFCARD_FILL7 = 12;

    private static final int INDEX_FELICA_TRANSMIT = 13;
    private static final int INDEX_RFCARD_FILL8 = 14;
    private static final int INDEX_RFCARD_FILL9 = 15;

    private static final int INDEX_RFCARD_FILL10 = 16;
    private static final int INDEX_RFCARD_FILL11 = 17;
    private static final int INDEX_RFCARD_FILL12 = 18;

    private static final int INDEX_M1_AUTHENCIATE = 19;
    private static final int INDEX_M1_READ = 20;
    private static final int INDEX_M1_WRITE = 21;

    private static final int INDEX_M1_INCREASE = 22;
    private static final int INDEX_M1_DECREASE = 23;
    private static final int INDEX_RFCARD_FILL13 = 24;

    private static final int INDEX_RFCARD_FILL14 = 25;
    private static final int INDEX_RFCARD_FILL15 = 26;
    private static final int INDEX_RFCARD_FILL16 = 27;

    private static final int INDEX_M0_AUTHENCIATE = 28;
    private static final int INDEX_M0_READ = 29;
    private static final int INDEX_M0_WRITE = 30;
    private static final int INDEX_SET_RF_MODE = 31;

    public RFCardFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_rfcard_f);
    }

    @Override
    public void initData() {
        rfCardModule = moduleManage.getRFCardModule();
    }

    @Override
    public Object getModule() {
        return RFCardFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.tv_rf_power_on, functionid = INDEX_RFCARD_POWERON)
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

            }

            @Override
            public void onResult(int id, View view) {
                try {
                    showMessage(R.string.msg_rf);
                    List<RFCardType> cardTypeList = new ArrayList<RFCardType>();
                    int timeout = 60;
                    RFCardPowerOnExtParams rfCardPowerOnExtParams = null;

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
                    rfCardPowerOnExtParams = null;
                    Switch aSwitch = view.findViewById(R.id.switch_extended_param);
                    if (aSwitch.isChecked()) {
                        boolean sakIsChecked = ((RadioButton) view.findViewById(R.id.rf_power_on_extended_param_sak)).isChecked();
                        boolean felicaIsChecked = ((RadioButton) view.findViewById(R.id.rf_power_on_extended_param_felica)).isChecked();
                        if (sakIsChecked && felicaIsChecked) {
                            showMessage(context.getString(R.string.msg_error) + "\r\n", MessageTag.ERROR);
                            return;
                        }
                        if (sakIsChecked) {
                            rfCardPowerOnExtParams = new RFCardPowerOnExtParams();
                            EditText editSak = view.findViewById(R.id.edit_rf_power_on_sak);
                            if (editSak.getText().toString() != null && editSak.getText().toString().length() > 0) {
                                int sak = Integer.valueOf(editSak.getText().toString());
                                rfCardPowerOnExtParams.setSak((byte)sak);
                            }
                        } else if (felicaIsChecked) {
                            rfCardPowerOnExtParams = new RFCardPowerOnExtParams();
                            FelicaParams[] felicas = new FelicaParams[2];
                            felicas[0] = new FelicaParams();
                            felicas[0].setSystemCode(new byte[]{(byte) 0xFF, (byte) 0xFF});
                            felicas[0].setRequestCode((byte) 0x08);
                            felicas[0].setTimeSlot((byte) 0x08);
                            felicas[1] = new FelicaParams();
                            felicas[1].setSystemCode(new byte[]{(byte) 0x80, 0x08});
                            felicas[1].setRequestCode((byte) 0x01);
                            felicas[1].setTimeSlot((byte) 0x00);
                            rfCardPowerOnExtParams.setFelicaParams(felicas);
                        }
                    }
                    RFResult rfResult = rfCardModule.powerOn(cardTypeList.toArray(new RFCardType[cardTypeList.size()]), timeout, rfCardPowerOnExtParams);

                    if (rfResult != null && rfResult.getRfcardType() != null) {
                        showMessage(context.getString(R.string.msg_rf_type) + rfResult.getRfcardType() + "\r\n", MessageTag.DATA);
                    } else {
                        showMessage(context.getString(R.string.msg_error) + "\r\n", MessageTag.ERROR);
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

    @MethodGridEntity(btnnameid = R.string.tv_rf_power_off, functionid = INDEX_RFCARD_POWEROFF, btnimageid = 4)
    private void rfcardPowerOff() {
        try {
            boolean result = rfCardModule.powerOff();
            showMessage(context.getString(R.string.msg_rf_poweroff_finished) + result, MessageTag.NORMAL);
        } catch (Exception e) {
            showMessage(context.getString(R.string.msg_rf_poweroff_error) + e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_rf_is_induct, functionid = INDEX_RFCARD_INDUCT, btnimageid = 3)
    private void rfcardIsInducted() {
        try {
            boolean isExit = rfCardModule.isCardExist();
            if (isExit) {
                showMessage(context.getString(R.string.msg_rf_isInducted) + "\r\n", MessageTag.DATA);
            } else {
                showMessage(context.getString(R.string.msg_rf_is_not_inducted) + "\r\n", MessageTag.DATA);
            }

        } catch (Exception e) {
            showMessage(context.getString(R.string.msg_error) +e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(divtipid = 0, functionid = INDEX_RFCARD_FILL0)
    private void fill0() {

    }

    @MethodGridEntity(divtipid = R.string.tv_a_card_transmit, functionid = INDEX_RFCARD_FILL1)
    private void fill1() {

    }

    @MethodGridEntity(divtipid = 0, functionid = INDEX_RFCARD_FILL2)
    private void fill2() {

    }

    @MethodGridEntity(btnnameid = R.string.tv_a_card_transmit, functionid = INDEX_RFCARD_TRANSMIT, btnimageid = 2)//
    private void rfcardCommunication() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_a_card_transmit), null, R.layout.dialog_iccard_communication, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                try {
                    if(id == -1){//cancel
                        return;
                    }
                    EditText edtText = dialogView.findViewById(R.id.edit_ICCardSend);
                    String str = edtText.getText().toString();//Get communication data
                    byte req[] = ISOUtils.hex2byte(str);
                    byte result[] = rfCardModule.transmit(req, 60);
                    showMessage(context.getString(R.string.msg_send_data) + (req==null?null:ISOUtils.hexString(req)), MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_get_data) + (result==null?null:ISOUtils.hexString(result)), MessageTag.DATA);
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(context.getString(R.string.msg_card_transmit_error) + e , MessageTag.ERROR);
                }
            }
        });
    }

    @MethodGridEntity(functionid = INDEX_RFCARD_FILL3)
    private void fill3() {
    }

    @MethodGridEntity(functionid = INDEX_RFCARD_FILL4)
    private void fill4() {
    }


    @MethodGridEntity(divtipid = 0, functionid = INDEX_RFCARD_FILL5)
    private void fill5() {

    }

    @MethodGridEntity(divtipid = R.string.tv_felica_card_transmit, functionid = INDEX_RFCARD_FILL6)
    private void fill6() {

    }

    @MethodGridEntity(divtipid = 0, functionid = INDEX_RFCARD_FILL7)
    private void fill7() {

    }

    @MethodGridEntity(btnnameid = R.string.tv_rf_transmit, btnimageid = 2,functionid = INDEX_FELICA_TRANSMIT)
    private void felicaCardCommunication() {
        try {

            if (IDmAndPMm == null || IDmAndPMm.length <= 0) {
                showMessage(context.getString(R.string.msg_felica_card_transmit_power_on_first) + "\r\n", MessageTag.ERROR);
                return;
            }
            byte[] req = new byte[16];
            req[0] = 16;
            req[1] = 0x06;
            req[10] = 0x01;
            req[11] = 0x09;
            req[12] = 0x00;
            req[13] = 0x01;
            req[14] = (byte) 0x80;
            req[15] = 0x00;
            System.arraycopy(IDmAndPMm, 2, req, 2, 8);
            byte result[] = rfCardModule.felicaTransmit(req, 60);
            showMessage(context.getString(R.string.msg_send_data) + ISOUtils.hexString(req) + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.msg_get_data) + (result == null?null:ISOUtils.hexString(result)) + "\r\n", MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_card_transmit_error) + e , MessageTag.ERROR);
        }
    }


    @MethodGridEntity(functionid = INDEX_RFCARD_FILL8)
    private void fill8() {
    }

    @MethodGridEntity(functionid = INDEX_RFCARD_FILL9)
    private void fill9() {
    }

    @MethodGridEntity(divtipid = 0, functionid = INDEX_RFCARD_FILL10)
    private void fill10() {

    }

    @MethodGridEntity(divtipid = R.string.tv_m1, functionid = INDEX_RFCARD_FILL11)
    private void fill11() {

    }

    @MethodGridEntity(divtipid = 0, functionid = INDEX_RFCARD_FILL12)
    private void fill12() {

    }

    @MethodGridEntity(btnnameid = R.string.tv_m1_authorization, functionid = INDEX_M1_AUTHENCIATE, btnimageid = 2)
    private void m1Athenticate() {
        String[] items = new String[]{"KEYA_0x60", "KEYA_0x00", "KEYB_0x61", "KEYB_0x01"};
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_m1_authorization), items, R.layout.dialog_m1_external_auth, new DialogUtils.CustomDialogCallback() {

            @Override
            public void onResult(int id, View dialogView) {
                RFKeyMode qpKeyMode = RFKeyMode.KEYA_0X60;
                if (id >= 0) {
                    if (id == 0) {
                        qpKeyMode = RFKeyMode.KEYA_0X60;
                        showMessage("KEYA_0X60", MessageTag.DATA);
                    } else if (id == 1) {
                        showMessage("KEYA_0X00", MessageTag.DATA);
                        qpKeyMode = RFKeyMode.KEYA_0X00;
                    } else if (id == 2) {
                        showMessage("KEYB_0X61", MessageTag.DATA);
                        qpKeyMode = RFKeyMode.KEYB_0X61;
                    } else {
                        showMessage("KEYB_0X01", MessageTag.DATA);
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
                            boolean isSucess = rfCardModule.m1Authenticate(qpKeyMode, sn, block, key);
                            showMessage(context.getString(R.string.msg_rf_external_key_auth_finished) + isSucess, MessageTag.NORMAL);
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

    @MethodGridEntity(btnnameid = R.string.tv_m1_write_block, functionid = INDEX_M1_WRITE, btnimageid = 4)
    public void m1CardWrite() {
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
                        boolean result = rfCardModule.m1WriteBlockData(block, input);
                        showMessage(context.getString(R.string.msg_write_block_data_result) +result, MessageTag.NORMAL);
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

    @MethodGridEntity(btnnameid = R.string.tv_m1_read_block, functionid = INDEX_M1_READ, btnimageid = 3)
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
                        byte output[] = rfCardModule.m1ReadBlockData(block);

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

    @MethodGridEntity(btnnameid = R.string.tv_m1_increment, functionid = INDEX_M1_INCREASE, btnimageid = 5)
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
                        boolean result = rfCardModule.m1Increment(block, input);
                        showMessage(context.getString(R.string.msg_increase_operation_finished) + result, MessageTag.NORMAL);
                        showMessage(context.getString(R.string.msg_storage_block) + block + "\r\n", MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_data) + input == null ? "null" : ISOUtils.hexString(input) + "\r\n", MessageTag.DATA);
                    } else {
                        showMessage(context.getString(R.string.msg_input_illegal) + "\r\n", MessageTag.ERROR);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(context.getString(R.string.msg_increase_operation_error) + e.getMessage() + "\r\n", MessageTag.ERROR);
                    showMessage(context.getString(R.string.msg_check_rf_isPoweredon) + "\r\n", MessageTag.ERROR);
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_m1_decrement, functionid = INDEX_M1_DECREASE, btnimageid = 6)
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
                        boolean result = rfCardModule.m1Decrement(block, input);
                        showMessage(context.getString(R.string.msg_decrease_operation_finished) +result, MessageTag.NORMAL);
                        showMessage(context.getString(R.string.msg_storage_block) + block + "\r\n", MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_data) + input == null ? "null" : ISOUtils.hexString(input) + "\r\n", MessageTag.DATA);
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


    @MethodGridEntity(functionid = INDEX_RFCARD_FILL13)
    private void fill13() {
    }

    @MethodGridEntity(divtipid = 0, functionid = INDEX_RFCARD_FILL14)
    private void fill14() {
    }

    @MethodGridEntity(divtipid = R.string.tv_m0, functionid = INDEX_RFCARD_FILL15)
    private void fill15() {
    }

    @MethodGridEntity(divtipid = 0, functionid = INDEX_RFCARD_FILL16)
    private void fill16() {
    }

    @MethodGridEntity(btnnameid = R.string.tv_m0_card_auth, functionid = INDEX_M0_AUTHENCIATE, btnimageid = 2)
    private void m0Authenciate() {
        try {
            byte[] key = ISOUtils.hex2byte("49454D4B41455242214E4143554F5946"); //默认密钥")
            boolean result = rfCardModule.m0Authenticate(key);
            showMessage(context.getString(R.string.tv_m0_card_auth)+":"+result, MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_common_failed), MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_mo_card_read, functionid = INDEX_M0_READ, btnimageid = 3)
    private void m0ReadBlockData() {
        DialogUtils.createCustomDialog(context, R.string.tv_m1_read_block, null, R.layout.dialog_edittext, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                TextView tip = view.findViewById(R.id.textview_tip);
                tip.setText(context.getString(R.string.dialog_tv_mifarecard_block));
                EditText value = view.findViewById(R.id.edit_data);
                value.setText("4");
            }

            @Override
            public void onResult(int id, View dialogView) {
                try {
                    EditText value = dialogView.findViewById(R.id.edit_data);
                    int block = Integer.valueOf(value.getText().toString());
                    if (block >= 0) {
                        byte[] data = rfCardModule.m0ReadBlockData(block);
                        showMessage(context.getString(R.string.msg_storage_block) + block + "\r\n", MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_data) + (data == null ? "null" : ISOUtils.hexString(data)) , MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_read_block_finished) + "\r\n", MessageTag.NORMAL);

                    } else {
                        showMessage(context.getString(R.string.msg_input_illegal) + "\r\n", MessageTag.ERROR);
                    }
                } catch (Exception e) {
                    showMessage(context.getString(R.string.msg_read_block_error) + e, MessageTag.ERROR);
                    showMessage(context.getString(R.string.msg_check_rf_poweron_or_block_data_input) + "\r\n", MessageTag.ERROR);
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_m0_card_write, functionid = INDEX_M0_WRITE, btnimageid = 4)
    private void m0WriteBlockData() {
        DialogUtils.createCustomDialog(context, R.string.tv_m1_write_block, null, R.layout.dialog_m0_write, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                EditText block = (EditText) view.findViewById(R.id.edit_qccard_block);
                EditText data = (EditText) view.findViewById(R.id.edit_qccard_data);
                block.setText("4");
                data.setText(ISOUtils.hexString(ISOUtils.hex2byte("01020304")));
            }

            @Override
            public void onResult(int id, View view) {
                try {
                    EditText exblock = (EditText) view.findViewById(R.id.edit_qccard_block);
                    EditText exdata = (EditText) view.findViewById(R.id.edit_qccard_data);
                    int block = Integer.valueOf(exblock.getText().toString());
                    byte input[] = ISOUtils.hex2byte(exdata.getText().toString());
                    if (block >= 0) {
                        boolean result = rfCardModule.m0WriteBlockData(block, input);
                        showMessage(context.getString(R.string.msg_write_block_data_result) + result, MessageTag.NORMAL);
                        showMessage(context.getString(R.string.msg_storage_block) + block + "\r\n", MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_write_block_data) + (input == null ? "null" : ISOUtils.hexString(input) + "\r\n"), MessageTag.DATA);
                    } else {
                        showMessage(context.getString(R.string.msg_write_illegal) + "\r\n", MessageTag.ERROR);
                    }
                } catch (Exception e) {
                    showMessage(context.getString(R.string.msg_write_block_data_error) + e.getMessage() + "\r\n", MessageTag.ERROR);
                    showMessage(context.getString(R.string.msg_check_rf_isPoweredon) + "\r\n", MessageTag.ERROR);
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_set_rf_mode, functionid = INDEX_SET_RF_MODE, btnimageid = 4)
    private void setRFMode() {
        DialogUtils.createSingleChoiceDialog(context, "Set RF Mode", new String[]{"RING M1 MODE", "DEFAULT"}, new DialogUtils.SingleChoiceDialogCallback() {
            @Override
            public void onResult(int id) {
                if (id<0){
                    return;
                }
                if (id==0){
                   boolean rslt = rfCardModule.setRFMode(RFCardMode.RING_M1);
                   showMessage("setRFMode rslt:" + rslt);
                } else {
                    boolean rslt = rfCardModule.setRFMode(RFCardMode.DEFAULT);
                    showMessage("setRFMode rslt:" + rslt);
                }
            }
        });
    }
}
