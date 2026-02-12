package com.newland.nsdkdemo.internal.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.card.Card;
import com.newland.nsdk.core.api.common.card.contactless.ActivationResult;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.internal.card.contactless.CPUContactlessCard;
import com.newland.nsdk.core.internal.card.contactless.CPUContactlessCardImpl;
import com.newland.nsdk.plugin.card.api.common.contactless.ContactlessKeyMode;
import com.newland.nsdk.plugin.card.api.internal.contactless.FelicaCard;
import com.newland.nsdk.plugin.card.api.internal.contactless.M0Card;
import com.newland.nsdk.plugin.card.api.internal.contactless.M1Card;
import com.newland.nsdk.plugin.card.internal.contactless.FelicaCardImpl;
import com.newland.nsdk.plugin.card.internal.contactless.M0CardImpl;
import com.newland.nsdk.plugin.card.internal.contactless.M1CardImpl;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.AppConfig;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.annotation.MethodGridEntity;
import com.newland.nsdkdemo.common.utils.DialogUtils;
import com.newland.nsdkdemo.common.utils.MessageTag;

import java.util.Arrays;

public class ContactlessCardFragment extends InternalBaseFragment {

    private CPUContactlessCard mCPUContactlessCard;
    private M1Card mM1Card;
    private M0Card mM0Card;
    private FelicaCard mFelicaCard;
    private String uid;
    private Card currentCard;

    private static final int INDEX_RFCARD_POWERON = 1;
    private static final int INDEX_RFCARD_POWEROFF = 2;

    private static final int INDEX_RFCARD_FILL0 = 3;

    private static final int INDEX_RFCARD_TRANSMIT = 4;
    private static final int INDEX_FELICA_TRANSMIT = 5;

    private static final int INDEX_RFCARD_FILL1 = 6;

    private static final int INDEX_M1_AUTHENCIATE = 7;
    private static final int INDEX_M1_READ = 8;
    private static final int INDEX_M1_WRITE = 9;

    private static final int INDEX_M1_INCREASE = 10;
    private static final int INDEX_M1_DECREASE = 11;
    private static final int INDEX_M1_TRANSFER = 12;
    private static final int INDEX_M1_RESTORE = 13;

    private static final int INDEX_RFCARD_FILL2 = 14;
    private static final int INDEX_RFCARD_FILL3 = 15;

    private static final int INDEX_M0_AUTHENCIATE = 16;
    private static final int INDEX_M0_READ = 17;
    private static final int INDEX_M0_WRITE = 18;

    public static byte[] idmpmm;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor mEditor;

    public ContactlessCardFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_rfcard_f);
    }

    @Override
    public void initData() {
        // Shall search for card via CardReader first.
        mCPUContactlessCard = new CPUContactlessCardImpl();
        mM1Card = new M1CardImpl();
        mFelicaCard = new FelicaCardImpl();
        mM0Card = new M0CardImpl();
        sharedPreferences = context.getSharedPreferences("ContactlessCardFragment", Context.MODE_PRIVATE);
        mEditor = sharedPreferences.edit();
    }

    @Override
    public Object getModule() {
        return ContactlessCardFragment.this;
    }

    private String getMessage(byte[] msg){
        if (msg == null){
            return null;
        }
        return ISOUtils.hexString(msg);
    }

    @MethodGridEntity(btnnameid = R.string.tv_rf_power_on, functionid = INDEX_RFCARD_POWERON)
    private void powerOn() {
        DialogUtils.createCustomDialog(context, R.string.tv_rf_power_on, null, R.layout.dialog_contactless_card_type, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                RadioButton radioButtonCPU = view.findViewById(R.id.rb_rf_cpu);
                RadioButton radioButtonM1 = view.findViewById(R.id.rb_rf_m1);
                RadioButton radioButtonM0 = view.findViewById(R.id.rb_rf_m0);
                LinearLayout linear_contactless_AtoBtransfer = view.findViewById(R.id.linear_contactless_AtoBtransfer);
                radioButtonCPU.setChecked(true);
                linear_contactless_AtoBtransfer.setVisibility(View.GONE);
            }

            @Override
            public void onResult(int id, View view) {
                showMessage(R.string.msg_rf);
                RadioButton radioButtonCPU = view.findViewById(R.id.rb_rf_cpu);
                RadioButton radioButtonM1 = view.findViewById(R.id.rb_rf_m1);
                RadioButton radioButtonM0 = view.findViewById(R.id.rb_rf_m0);


                ActivationResult result = null;
                try {
                    if (radioButtonCPU.isChecked()) {
                        currentCard = mCPUContactlessCard;
                        result = mCPUContactlessCard.activate();
                    } else if (radioButtonM1.isChecked()) {
                        currentCard = mM1Card;
                        result = mM1Card.activate();
                    } else if (radioButtonM0.isChecked()) {
                        currentCard = mM0Card;
                        result = mM0Card.activate();
                    }
                } catch (NSDKException e) {
                    e.printStackTrace();
                    if (e.getCode() == ErrorCode.RFID_NOT_DETECTED || e.getCode() == ErrorCode.MI_NOTAGERR) {
                        showMessage(context.getString(R.string.msg_card_reader_open_first), MessageTag.ERROR);
                    } else {
                        showErrorMessage(e, "activate card");
                    }
                    return;
                }

                if (result != null) {
                    showMessage("UID:" + getMessage(result.getUID()) + "\r\n", MessageTag.DATA);
                    showMessage("ATQA:" + getMessage(result.getATQA()) + "\r\n", MessageTag.DATA);
                    showMessage("ATS:" + getMessage(result.getATS()) + "\r\n", MessageTag.DATA);
                    showMessage("ATQB:" + getMessage(result.getATQB()) + "\r\n", MessageTag.DATA);
                    showMessage("SAK:" + getMessage(result.getSAK()) + "\r\n", MessageTag.DATA);
                    uid = getMessage(result.getUID());
                    showMessage(context.getString(R.string.msg_poweron_end) + "\r\n", MessageTag.DATA);
                } else {
                    showMessage("Result is null", MessageTag.ERROR);
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_rf_power_off, functionid = INDEX_RFCARD_POWEROFF, btnimageid = 2)
    private void rfcardPowerOff() {
        try {
            if (currentCard instanceof CPUContactlessCard) {
                mCPUContactlessCard.deactivate();
            } else if (currentCard instanceof M1Card) {
                mM1Card.deactivate();
            } else if (currentCard instanceof M0Card) {
                mM0Card.deactivate();
            } else if (currentCard instanceof FelicaCard) {
                mFelicaCard.deactivate();
            } else {
                showMessage(context.getString(R.string.msg_card_activate_first), MessageTag.ERROR);
                return;
            }
            showMessage(context.getString(R.string.msg_rf_poweroff_finished) + "true", MessageTag.NORMAL);
        } catch (Exception e) {
            showErrorMessage(e,context.getString(R.string.msg_rf_poweroff_error));
        }
    }

    @MethodGridEntity(functionid = INDEX_RFCARD_FILL0)
    private void fill0() {
    }

    @MethodGridEntity(btnnameid = R.string.tv_a_card_transmit, functionid = INDEX_RFCARD_TRANSMIT, btnimageid = 1)
    private void rfcardCommunication() {
        DialogUtils.createCustomDialog(context, R.string.tv_a_card_transmit, null, R.layout.dialog_contactless_card_type, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                LinearLayout llCardTypeParams = view.findViewById(R.id.linear_contactless_cardType);
                llCardTypeParams.setVisibility(View.GONE);
            }

            @Override
            public void onResult(int id, View view) {
                try {
                    if (id == -1) {//cancel
                        return;
                    }
                    currentCard = mCPUContactlessCard;
                    EditText edtText = view.findViewById(R.id.edit_ICCardSend);
                    String str = edtText.getText().toString();//Get communication data
                    byte[] req = ISOUtils.hex2byte(str);
                    byte[] result = mCPUContactlessCard.performAPDU(req);
                    showMessage(context.getString(R.string.msg_send_data) + (req == null ? null : ISOUtils.hexString(req)), MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_get_data) + (result == null ? null : ISOUtils.hexString(result)), MessageTag.DATA);
                } catch (NSDKException e) {
                    if (e.getCode() == ErrorCode.PATH_ERROR) {
                        showMessage( context.getString(R.string.msg_card_activate_first), MessageTag.ERROR);
                        return;
                    }
                    e.printStackTrace();
                    showErrorMessage(e, context.getString(R.string.tv_a_card_transmit));
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_felica_card_transmit, btnimageid = 2, functionid = INDEX_FELICA_TRANSMIT)
    private void felicaCardCommunication() {
//        if (idmpmm == null) {
//            showMessage("IDmPMm is null, please open card reader to detect felica card first.");
//            return;
//        }
        DialogUtils.createCustomDialog(context, R.string.tv_felica_card_transmit, null, R.layout.dialog_felica_transmit, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                EditText editCommand = view.findViewById(R.id.edit_felica_transmit_command);
                EditText editTimeout = view.findViewById(R.id.edit_felica_transmit_timeout);
                EditText editRetryTimes = view.findViewById(R.id.edit_felica_transmit_retryTimes);
                editCommand.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.FELICA_CARD_TRANSMIT_COMMAND, ""));
                editTimeout.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.FELICA_CARD_TRANSMIT_TIMEOUT, ""));
                editRetryTimes.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.FELICA_CARD_TRANSMIT_RETRY_TIMES, ""));
            }

            @Override
            public void onResult(int id, View view) {
                EditText editCommand = view.findViewById(R.id.edit_felica_transmit_command);
                EditText editTimeout = view.findViewById(R.id.edit_felica_transmit_timeout);
                EditText editRetryTimes = view.findViewById(R.id.edit_felica_transmit_retryTimes);
                mEditor.putString(AppConfig.SharedPreferenceConfig.FELICA_CARD_TRANSMIT_COMMAND, editCommand.getText().toString());
                mEditor.putString(AppConfig.SharedPreferenceConfig.FELICA_CARD_TRANSMIT_TIMEOUT, editTimeout.getText().toString());
                mEditor.putString(AppConfig.SharedPreferenceConfig.FELICA_CARD_TRANSMIT_RETRY_TIMES, editRetryTimes.getText().toString());
                mEditor.commit();

                byte[] command = ISOUtils.hex2byte(editCommand.getText().toString());
                int timeout = Integer.parseInt(editTimeout.getText().toString());
                int retryTimes = Integer.parseInt(editRetryTimes.getText().toString());

                try {
                    byte[] result = mFelicaCard.transmit(command, timeout, retryTimes);
                    showMessage(context.getString(R.string.msg_send_data) + ISOUtils.hexString(command) + "\r\n", MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_get_data) + (result == null ? null : ISOUtils.hexString(result)) + "\r\n", MessageTag.DATA);
                } catch (NSDKException e) {
                    e.printStackTrace();
                    showErrorMessage(e,context.getString(R.string.tv_felica_card_transmit));
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_felica_card_polling, functionid = INDEX_RFCARD_FILL1, btnimageid = 3)
    private void felicaCardPolling() {
        DialogUtils.createCustomDialog(context, R.string.tv_felica_card_polling, null, R.layout.dialog_felica_polling, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                EditText editSystemCode = view.findViewById(R.id.edit_felica_polling_systemCode);
                EditText editRequestCode = view.findViewById(R.id.edit_felica_polling_requestCode);
                EditText editTimeslot = view.findViewById(R.id.edit_felica_polling_timeslot);
                editSystemCode.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.FELICA_CARD_POLLING_SYSTEM_CODE, ""));
                editRequestCode.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.FELICA_CARD_POLLING_REQUEST_CODE, ""));
                editTimeslot.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.FELICA_CARD_POLLING_TIME_SLOT, ""));
            }

            @Override
            public void onResult(int id, View view) {
                EditText editSystemCode = view.findViewById(R.id.edit_felica_polling_systemCode);
                EditText editRequestCode = view.findViewById(R.id.edit_felica_polling_requestCode);
                EditText editTimeslot = view.findViewById(R.id.edit_felica_polling_timeslot);
                mEditor.putString(AppConfig.SharedPreferenceConfig.FELICA_CARD_POLLING_SYSTEM_CODE, editSystemCode.getText().toString());
                mEditor.putString(AppConfig.SharedPreferenceConfig.FELICA_CARD_POLLING_REQUEST_CODE, editRequestCode.getText().toString());
                mEditor.putString(AppConfig.SharedPreferenceConfig.FELICA_CARD_POLLING_TIME_SLOT, editTimeslot.getText().toString());
                mEditor.commit();
                byte[] systemCode = ISOUtils.hex2byte(editSystemCode.getText().toString());
                byte requestCode = ISOUtils.hex2byte(editRequestCode.getText().toString())[0];
                byte timeslot = ISOUtils.hex2byte(editTimeslot.getText().toString())[0];

                try {
                    byte[] receiveData = mFelicaCard.polling(systemCode, requestCode, timeslot);
                    showMessage(context.getString(R.string.msg_receive_data) + ISOUtils.hexString(receiveData));
                } catch (NSDKException e) {
                    showErrorMessage(e, context.getString(R.string.tv_felica_card_polling));
                }

            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_m1_authorization, functionid = INDEX_M1_AUTHENCIATE, btnimageid = 1)
    private void m1Athenticate() {
        String[] items = new String[]{"KEYA_0x60", "KEYA_0x00", "KEYB_0x61", "KEYB_0x01"};
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_m1_authorization), items, R.layout.dialog_m1_external_auth, new DialogUtils.CustomDialogCallback() {

            @Override
            public void onResult(int id, View dialogView) {
                ContactlessKeyMode qpKeyMode = ContactlessKeyMode.KEYA_0X60;
                if (id >= 0) {
                    if (id == 0) {
                        qpKeyMode = ContactlessKeyMode.KEYA_0X60;
                        showMessage("KEYA_0X60", MessageTag.DATA);
                    } else if (id == 1) {
                        showMessage("KEYA_0X00", MessageTag.DATA);
                        qpKeyMode = ContactlessKeyMode.KEYA_0X00;
                    } else if (id == 2) {
                        showMessage("KEYB_0X61", MessageTag.DATA);
                        qpKeyMode = ContactlessKeyMode.KEYB_0X61;
                    } else {
                        showMessage("KEYB_0X01", MessageTag.DATA);
                        qpKeyMode = ContactlessKeyMode.KEYB_0X01;
                    }

                    EditText edtBlockNum = dialogView.findViewById(R.id.edit_qccard_block);
                    EditText edtKey = dialogView.findViewById(R.id.edit_qccard_key);

                    int block = Integer.valueOf(edtBlockNum.getText().toString());
                    byte[] sn = null;

                    if (uid != null) {
                        sn = ISOUtils.hex2byte(uid);
                        if (sn.length > 4) {
                            sn = Arrays.copyOf(sn, 4);
                        }
                    } else {
                        showMessage(context.getString(R.string.msg_check_rf_isPoweredon) + "\r\n", MessageTag.ERROR);
                        return;
                    }
                    byte[] key = ISOUtils.hex2byte(edtKey.getText().toString());
                    if (block >= 0 && block <= 255 && key.length == 6 && sn != null && sn.length == 4) {
                        try {
                            mM1Card.authenticate(qpKeyMode, sn, (byte) block, key);
                            showMessage(context.getString(R.string.msg_rf_external_key_auth_finished) + "true", MessageTag.NORMAL);
                            showMessage(context.getString(R.string.msg_key_mode) + qpKeyMode + "\r\n", MessageTag.DATA);
                            showMessage(context.getString(R.string.msg_UID_SN_NO) + (uid == null ? "null" : ISOUtils.hexString(sn)) + "\r\n", MessageTag.DATA);
                            showMessage(context.getString(R.string.msg_security_block_NO) + block + "\r\n", MessageTag.DATA);
                            showMessage(context.getString(R.string.msg_external_key) + (key == null ? "null" : ISOUtils.hexString(key)) + "\r\n", MessageTag.DATA);
                        } catch (Exception e) {
                            e.printStackTrace();
                            showErrorMessage(e, context.getString(R.string.tv_m1_authorization));
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

    @MethodGridEntity(btnnameid = R.string.tv_m1_write_block, functionid = INDEX_M1_WRITE, btnimageid = 3)
    public void m1CardWrite() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_m1_write_block), null, R.layout.dialog_m1_write, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                try {
                    if (id == -1) {//cancel
                        return;
                    }
                    EditText etBlock = dialogView.findViewById(R.id.edit_qccard_block);
                    EditText etData = dialogView.findViewById(R.id.edit_qccard_data);
                    int block = Integer.valueOf(etBlock.getText().toString());
                    byte[] input = ISOUtils.hex2byte(etData.getText().toString());
                    if (block >= 0 && block <= 255 && input.length == 16) {
                        mM1Card.writeBlockData((byte) block, input);
                        showMessage(context.getString(R.string.msg_write_block_data_result) + "true", MessageTag.NORMAL);
                        showMessage(context.getString(R.string.msg_storage_block) + block + "\r\n", MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_write_block_data) + (input == null ? "null" : ISOUtils.hexString(input)) + "\r\n", MessageTag.DATA);

                    } else {
                        showMessage(context.getString(R.string.msg_write_illegal) + "\r\n", MessageTag.ERROR);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showErrorMessage(e,context.getString(R.string.tv_m1_write_block));
                    showMessage(context.getString(R.string.msg_check_rf_isPoweredon) + "\r\n", MessageTag.ERROR);
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_m1_read_block, functionid = INDEX_M1_READ, btnimageid = 2)
    private void m1CardRead() {
        TextView tip = LayoutInflater.from(context).inflate(R.layout.dialog_edittext, null).findViewById(R.id.textview_tip);
        tip.setText(context.getString(R.string.dialog_tv_qccard_block));
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_m1_read_block), null, R.layout.dialog_edittext, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                try {
                    if (id == -1) {//cancel
                        return;
                    }
                    EditText editTextData = dialogView.findViewById(R.id.edit_data);
                    int block = Integer.valueOf(editTextData.getText().toString());
                    if (block >= 0 && block <= 255) {
                        byte[] output = mM1Card.readBlockData((byte) block);

                        showMessage(context.getString(R.string.msg_storage_block) + block + "\r\n", MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_data) + (output == null ? "null" : ISOUtils.hexString(output)) + "\r\n", MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_read_block_finished) + "\r\n", MessageTag.NORMAL);
                    } else {
                        showMessage(context.getString(R.string.msg_input_illegal) + "\r\n", MessageTag.ERROR);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showErrorMessage(e,context.getString(R.string.tv_m1_read_block));
                }

            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_m1_increment, functionid = INDEX_M1_INCREASE, btnimageid = 4)
    private void m1CardIncrease() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_m1_increment), null, R.layout.dialog_m1_operate, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                try {
                    if (id == -1) {//cancel
                        return;
                    }
                    EditText etBlock = dialogView.findViewById(R.id.edit_m1_block);
                    EditText etData = dialogView.findViewById(R.id.edit_m1_data);
                    int block = Integer.valueOf(etBlock.getText().toString());
                    byte[] input = ISOUtils.hex2byte(etData.getText().toString());
                    if (block >= 0 && block <= 255 && input.length == 4) {
                        mM1Card.increment((byte) block, input);
                        showMessage(context.getString(R.string.msg_increase_operation_finished) + "true", MessageTag.NORMAL);
                        showMessage(context.getString(R.string.msg_storage_block) + block + "\r\n", MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_data) + input == null ? "null" : ISOUtils.hexString(input) + "\r\n", MessageTag.DATA);
                    } else {
                        showMessage(context.getString(R.string.msg_input_illegal) + "\r\n", MessageTag.ERROR);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showErrorMessage(e,context.getString(R.string.tv_m1_increment));
                    showMessage(context.getString(R.string.msg_check_rf_isPoweredon) + "\r\n", MessageTag.ERROR);
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_m1_decrement, functionid = INDEX_M1_DECREASE, btnimageid = 5)
    private void m1CardDecrease() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_m1_decrement), null, R.layout.dialog_m1_operate, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                try {
                    if (id == -1) {//cancel
                        return;
                    }
                    EditText etBlock = dialogView.findViewById(R.id.edit_m1_block);
                    EditText etData = dialogView.findViewById(R.id.edit_m1_data);
                    int block = Integer.valueOf(etBlock.getText().toString());
                    byte[] input = ISOUtils.hex2byte(etData.getText().toString());
                    if (block >= 0 && block <= 255 && input.length == 4) {
                        mM1Card.decrement((byte) block, input);
                        showMessage(context.getString(R.string.msg_decrease_operation_finished) + "true", MessageTag.NORMAL);
                        showMessage(context.getString(R.string.msg_storage_block) + block + "\r\n", MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_data) + input == null ? "null" : ISOUtils.hexString(input) + "\r\n", MessageTag.DATA);
                    } else {
                        showMessage(context.getString(R.string.msg_input_illegal) + "\r\n", MessageTag.ERROR);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showErrorMessage(e,context.getString(R.string.tv_m1_decrement));
                    showMessage(context.getString(R.string.msg_check_rf_isPoweredon) + "\r\n", MessageTag.ERROR);
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_m1_transfer, functionid = INDEX_M1_TRANSFER, btnimageid = 6)
    private void rfM1Transfer() {
        TextView tip = LayoutInflater.from(context).inflate(R.layout.dialog_edittext, null).findViewById(R.id.textview_tip);
        tip.setText(context.getString(R.string.dialog_tv_qccard_block));
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_m1_transfer), null, R.layout.dialog_edittext, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                if (id == -1) {//cancel
                    return;
                }
                EditText editTextData = dialogView.findViewById(R.id.edit_data);
                int block = Integer.valueOf(editTextData.getText().toString());
                if (block >= 0 && block <= 255) {
                    int ret = 0;
                    try {
                        mM1Card.transfer((byte) block);
                    } catch (NSDKException e) {
                        e.printStackTrace();
                        showErrorMessage(e, "transfer");
//                        ret = e.getCode();
                    }
//                    showMessage("TRANSFER result is: " + ret);
                } else {
                    showMessage(context.getString(R.string.msg_input_illegal) + "\r\n", MessageTag.ERROR);
                }

            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_m1_restore, functionid = INDEX_M1_RESTORE, btnimageid = 7)
    private void rfM1Restore() {
        TextView tip = LayoutInflater.from(context).inflate(R.layout.dialog_edittext, null).findViewById(R.id.textview_tip);
        tip.setText(context.getString(R.string.dialog_tv_qccard_block));
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_m1_restore), null, R.layout.dialog_edittext, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                if (id == -1) {//cancel
                    return;
                }
                EditText editTextData = dialogView.findViewById(R.id.edit_data);
                int block = Integer.valueOf(editTextData.getText().toString());
                if (block >= 0 && block <= 255) {
                    int ret = 0;
                    try {
                        mM1Card.restore((byte) block);
                    } catch (NSDKException e) {
                        e.printStackTrace();
                        showErrorMessage(e, "restore");
//                        ret = e.getCode();
                    }
//                    showMessage("RESTORE result is: " + ret);
                } else {
                    showMessage(context.getString(R.string.msg_input_illegal) + "\r\n", MessageTag.ERROR);
                }

            }
        });
    }

    @MethodGridEntity(divtipid = 0, functionid = INDEX_RFCARD_FILL2)
    private void fill2() {

    }

    @MethodGridEntity(divtipid = 0, functionid = INDEX_RFCARD_FILL3)
    private void fill3() {
    }

    @MethodGridEntity(btnnameid = R.string.tv_m0_card_auth, functionid = INDEX_M0_AUTHENCIATE, btnimageid = 1)
    private void m0Authenciate() {
        try {
            byte[] key = ISOUtils.hex2byte("49454D4B41455242214E4143554F5946");
            mM0Card.authenticate(key);
            showMessage(context.getString(R.string.tv_m0_card_auth) + ":" + "true", MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage(e, context.getString(R.string.tv_m0_card_auth));
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_mo_card_read, functionid = INDEX_M0_READ, btnimageid = 2)
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
                    if (block >= 0 && block < 255) {
                        byte[] data = mM0Card.readBlockData((byte) block);
                        showMessage(context.getString(R.string.msg_storage_block) + block + "\r\n", MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_data) + (data == null ? "null" : ISOUtils.hexString(data)), MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_read_block_finished) + "\r\n", MessageTag.NORMAL);

                    } else {
                        showMessage(context.getString(R.string.msg_input_illegal) + "\r\n", MessageTag.ERROR);
                    }
                } catch (Exception e) {
                    showErrorMessage(e, context.getString(R.string.tv_mo_card_read));
                    showMessage(context.getString(R.string.msg_check_rf_poweron_or_block_data_input) + "\r\n", MessageTag.ERROR);
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_m0_card_write, functionid = INDEX_M0_WRITE, btnimageid = 3)
    private void m0WriteBlockData() {
        DialogUtils.createCustomDialog(context, R.string.tv_m1_write_block, null, R.layout.dialog_m0_write, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                EditText block = view.findViewById(R.id.edit_qccard_block);
                EditText data = view.findViewById(R.id.edit_qccard_data);
                block.setText("4");
                data.setText(ISOUtils.hexString(ISOUtils.hex2byte("01020304")));
            }

            @Override
            public void onResult(int id, View view) {
                try {
                    EditText etBlock = view.findViewById(R.id.edit_qccard_block);
                    EditText etData = view.findViewById(R.id.edit_qccard_data);
                    int block = Integer.valueOf(etBlock.getText().toString());
                    byte[] input = ISOUtils.hex2byte(etData.getText().toString());
                    if (block >= 0 && block < 255) {
                        mM0Card.writeBlockData((byte) block, input);
                        showMessage(context.getString(R.string.msg_write_block_data_result) + "true", MessageTag.NORMAL);
                        showMessage(context.getString(R.string.msg_storage_block) + block + "\r\n", MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_write_block_data) + (input == null ? "null" : ISOUtils.hexString(input) + "\r\n"), MessageTag.DATA);
                    } else {
                        showMessage(context.getString(R.string.msg_write_illegal) + "\r\n", MessageTag.ERROR);
                    }
                } catch (Exception e) {
                    showErrorMessage(e,context.getString(R.string.tv_m0_card_write));
                    showMessage(context.getString(R.string.msg_check_rf_isPoweredon) + "\r\n", MessageTag.ERROR);
                }
            }
        });
    }
}
