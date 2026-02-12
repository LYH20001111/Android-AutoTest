package com.newland.nsdkdemo.external.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.card.Card;
import com.newland.nsdk.core.api.common.card.contactless.ActivationResult;
import com.newland.nsdk.core.api.common.crypto.AlgorithmParameters;
import com.newland.nsdk.core.api.common.crypto.CipherOutput;
import com.newland.nsdk.core.api.common.crypto.CipherType;
import com.newland.nsdk.core.api.common.crypto.PaddingMode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.keymanager.CipherMode;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.api.common.keymanager.KeyUsage;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.external.card.ExtAPDUOutput;
import com.newland.nsdk.core.api.external.card.contactless.ExtCPUContactlessCard;
import com.newland.nsdk.core.api.external.crypto.ExtCrypto;
import com.newland.nsdk.core.external.card.contactless.ExtCPUContactlessCardImpl;
import com.newland.nsdk.plugin.card.api.common.contactless.ContactlessKeyMode;
import com.newland.nsdk.plugin.card.api.external.contactless.ExtFelicaCard;
import com.newland.nsdk.plugin.card.api.external.contactless.ExtM1Card;
import com.newland.nsdk.plugin.card.external.contactless.ExtFelicaCardImpl;
import com.newland.nsdk.plugin.card.external.contactless.ExtM1CardImpl;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.AppConfig;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.annotation.MethodGridEntity;
import com.newland.nsdkdemo.common.fragment.BaseFragment;
import com.newland.nsdkdemo.common.utils.DialogUtils;
import com.newland.nsdkdemo.common.utils.MessageTag;

import java.util.Arrays;

public class ExtContactlessCardFragment extends ExtBaseFragment {

    private ExtCPUContactlessCard mExtCpuContactlessCard;
    private ExtM1Card mExtM1Card;
    private ExtFelicaCard mExtFelicaCard;

    private byte[] uid;
    private byte[] IDmAndPMm = null;
    private Card currentCard;

    private static final int INDEX_RFCARD_POWERON = 1;
    private static final int INDEX_RFCARD_POWEROFF = 2;
    private static final int INDEX_RFCARD_FILL0 = 3;

    private static final int INDEX_RFCARD_TRANSMIT = 4;
    private static final int INDEX_RFCARD_CRYPTO_TRANSMIT = 5;
    private static final int INDEX_RFCARD_FILL2 = 6;

    private static final int INDEX_FELICA_TRANSMIT = 7;
    private static final int INDEX_RFCARD_FILL3 = 8;
    private static final int INDEX_RFCARD_FILL4 = 9;

    private static final int INDEX_M1_AUTHENCIATE = 10;
    private static final int INDEX_M1_READ = 11;
    private static final int INDEX_M1_WRITE = 12;
    private static final int INDEX_M1_INCREASE = 13;
    private static final int INDEX_M1_DECREASE = 14;


    public ExtContactlessCardFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_extcontactlesscard_f);
    }

    @Override
    public void initData() {
        mExtCpuContactlessCard = new ExtCPUContactlessCardImpl();
        mExtM1Card = new ExtM1CardImpl();
        mExtFelicaCard = new ExtFelicaCardImpl();

    }

    @Override
    public Object getModule() {
        return ExtContactlessCardFragment.this;
    }

    private String getMessage(byte[] msg){
        if (msg == null){
            return null;
        }
        return ISOUtils.hexString(msg);
    }

    @MethodGridEntity(btnnameid = R.string.tv_rf_power_on, functionid = INDEX_RFCARD_POWERON)
    private void activate() {
        DialogUtils.createCustomDialog(context, R.string.tv_rf_power_on, null, R.layout.dialog_ext_contactless_card_type, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                RadioButton radioButtonCPU = view.findViewById(R.id.rb_rf_cpu);
                radioButtonCPU.setChecked(true);
            }

            @Override
            public void onResult(int id, View view) {
                showMessage(R.string.msg_rf);
                RadioButton radioButtonCPU = view.findViewById(R.id.rb_rf_cpu);
                RadioButton radioButtonM1 = view.findViewById(R.id.rb_rf_m1);

                ActivationResult result = null;
                try {
                    if (radioButtonCPU.isChecked()) {
                        currentCard = mExtCpuContactlessCard;
                        result = mExtCpuContactlessCard.activate();
                    } else if (radioButtonM1.isChecked()) {
                        currentCard = mExtM1Card;
                        result = mExtM1Card.activate();
                    }
                } catch (NSDKException e) {
                    e.printStackTrace();
                    showErrorMessage(e, "activate card");
                    return;
                }

                if (result != null) {
                    uid = result.getUID();
                    showMessage("UID:" + getMessage(result.getUID()) + "\r\n", MessageTag.DATA);
                    showMessage("ATQA:" + getMessage(result.getATQA()) + "\r\n", MessageTag.DATA);
                    showMessage("ATS:" + getMessage(result.getATS()) + "\r\n", MessageTag.DATA);
                    showMessage("ATQB:" + getMessage(result.getATQB()) + "\r\n", MessageTag.DATA);
                    showMessage("SAK:" + getMessage(result.getSAK()) + "\r\n", MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_poweron_end) + "\r\n", MessageTag.DATA);
                } else {
                    showMessage("Result is null", MessageTag.ERROR);
                }
            }
        });
        /*
        try {
            mExtCpuContactlessCard.activate(new String[4]);
        } catch (NSDKException e) {
            e.printStackTrace();
        }*/
    }

    @MethodGridEntity(btnnameid = R.string.tv_rf_power_off, functionid = INDEX_RFCARD_POWEROFF, btnimageid = 2)
    private void deactivate() {
        try {
            if (currentCard instanceof ExtCPUContactlessCard) {
                mExtCpuContactlessCard.deactivate();
            } else if (currentCard instanceof ExtM1Card) {
                mExtM1Card.deactivate();
            } else if (currentCard instanceof ExtFelicaCard) {
                mExtFelicaCard.deactivate();
            } else {
                showMessage("Activate card first.", MessageTag.ERROR);
                return;
            }
            uid = null;
            showMessage(context.getString(R.string.msg_rf_poweroff_finished) + "true", MessageTag.NORMAL);
        } catch (Exception e) {
            showErrorMessage(e, context.getString(R.string.msg_rf_poweroff_error));
        }
    }


    @MethodGridEntity(divtipid = 0, functionid = INDEX_RFCARD_FILL0)
    private void fill0() {

    }

    @MethodGridEntity(btnnameid = R.string.tv_a_card_transmit, functionid = INDEX_RFCARD_TRANSMIT, btnimageid = 1)
    private void rfcardCommunication() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_a_card_transmit), null, R.layout.dialog_apdu, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                try {
                    if (id == -1) {//cancel
                        return;
                    }
                    EditText edtText = dialogView.findViewById(R.id.edit_ext_ICCardSend);
                    EditText edtIndex = dialogView.findViewById(R.id.edit_ext_apdu);
                    String str = edtText.getText().toString();//Get communication data
                    String index = edtIndex.getText().toString();
                    byte keyId = (byte) Integer.parseInt(index);
                    byte[] req = ISOUtils.hex2byte(str);
                    SymmetricKey key = new SymmetricKey();
                    key.setKeyID(keyId);
                    AlgorithmParameters parameters = new AlgorithmParameters();
                    ExtAPDUOutput result = mExtCpuContactlessCard.performAPDU(key, parameters, req);
                    showMessage(context.getString(R.string.msg_send_data) + (req == null ? null : ISOUtils.hexString(req)), MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_get_data) + (result == null ? null : ISOUtils.hexString(result.getData())), MessageTag.DATA);
                } catch (NSDKException e) {
                    e.printStackTrace();
                    showErrorMessage(e, context.getString(R.string.msg_card_transmit_error) );
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_a_card_crypto_transmit, functionid = INDEX_RFCARD_CRYPTO_TRANSMIT)
    private void rfcardEncryptedCommunication() {
        try {
            ExtCrypto crypto = (ExtCrypto) moduleManager.getModule(ModuleType.EXT_CRYPTO);
            SymmetricKey key = new SymmetricKey();
            key.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_WK_DATA);
            key.setKeyType(KeyType.DES);
            key.setKeyUsage(KeyUsage.DATA);
            AlgorithmParameters params = new AlgorithmParameters();
            params.setCipherMode(CipherMode.ECB);
            params.setPaddingMode(PaddingMode.NONE);

            byte[] command = {0x00, (byte)0x84, 0x00, 0x00, 0x04, 0x00, 0x00, 0x00};
            CipherOutput encryptCmd = crypto.encrypt(key, CipherType.DES_ECB, PaddingMode.NONE, null, command);
            showMessage("command encrypt result:" + ISOUtils.hexString(encryptCmd.getData()));
            ExtAPDUOutput result = mExtCpuContactlessCard.performAPDU(key, params, 5, encryptCmd.getData());
            showMessage("apdu result:" + ISOUtils.hexString(result.getData()));
            showMessage("apdu result length:" + result.getDataLen());
            CipherOutput result1 = crypto.decrypt(key, CipherType.DES_ECB, PaddingMode.NONE, null, result.getData());
            showMessage("apdu decrypt result:" + ISOUtils.hexString(result1.getData()));
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, e.getMessage());
        }
    }

    @MethodGridEntity(divtipid = 0, functionid = INDEX_RFCARD_FILL2)
    private void fill2() {

    }


    @MethodGridEntity(btnnameid = R.string.tv_felica_card_transmit, btnimageid = 1,functionid = INDEX_FELICA_TRANSMIT)
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
            byte[] result = mExtFelicaCard.transmit(req);
            showMessage(context.getString(R.string.msg_send_data) + ISOUtils.hexString(req) + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.msg_get_data) + (result == null?null:ISOUtils.hexString(result)) + "\r\n", MessageTag.DATA);
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, context.getString(R.string.msg_card_transmit_error));
        }
    }

    @MethodGridEntity(functionid = INDEX_RFCARD_FILL3)
    private void fill3() {
    }

    @MethodGridEntity(functionid = INDEX_RFCARD_FILL4)
    private void fill4() {
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

                    byte block = (byte) Integer.parseInt(edtBlockNum.getText().toString());
                    byte[] sn = null;

                    if (uid != null) {
                        if (uid.length > 4) {
                            sn = Arrays.copyOf(uid, 4);
                        } else {
                            sn = uid;
                        }
                    } else {
                        showMessage(context.getString(R.string.msg_check_rf_isPoweredon) + "\r\n", MessageTag.ERROR);
                        return;
                    }
                    byte[] key = ISOUtils.hex2byte(edtKey.getText().toString());
                    if (block >= 0 && block <= 255 && key.length == 6 && sn != null && sn.length == 4) {
                        try {
                            mExtM1Card.authenticate(qpKeyMode, sn, block, key);
                            showMessage(context.getString(R.string.msg_rf_external_key_auth_finished) + "true", MessageTag.NORMAL);
                            showMessage(context.getString(R.string.msg_key_mode) + qpKeyMode + "\r\n", MessageTag.DATA);
                            showMessage(context.getString(R.string.msg_UID_SN_NO) + (uid == null ? "null" : ISOUtils.hexString(sn)) + "\r\n", MessageTag.DATA);
                            showMessage(context.getString(R.string.msg_security_block_NO) + block + "\r\n", MessageTag.DATA);
                            showMessage(context.getString(R.string.msg_external_key) + (key == null ? "null" : ISOUtils.hexString(key)) + "\r\n", MessageTag.DATA);
                        } catch (NSDKException e) {
                            e.printStackTrace();
                            showErrorMessage(e, context.getString(R.string.msg_error));
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
                    byte block = (byte) Integer.parseInt(editTextData.getText().toString());
                    if (block >= 0 && block <= 255) {
                        byte[] output = mExtM1Card.readBlockData(block);

                        showMessage(context.getString(R.string.msg_storage_block) + block + "\r\n", MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_data) + (output == null ? "null" : ISOUtils.hexString(output)) + "\r\n", MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_read_block_finished) + "\r\n", MessageTag.NORMAL);
                    } else {
                        showMessage(context.getString(R.string.msg_input_illegal) + "\r\n", MessageTag.ERROR);
                    }
                } catch (NSDKException e) {
                    e.printStackTrace();
                    showErrorMessage(e, context.getString(R.string.msg_read_block_error));
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
                    byte block = (byte) Integer.parseInt(etBlock.getText().toString());
                    byte[] input = ISOUtils.hex2byte(etData.getText().toString());
                    if (block >= 0 && block <= 255 && input.length == 16) {
                        mExtM1Card.writeBlockData(block, input);
                        showMessage(context.getString(R.string.msg_write_block_data_result) + "true", MessageTag.NORMAL);
                        showMessage(context.getString(R.string.msg_storage_block) + block + "\r\n", MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_write_block_data) + (input == null ? "null" : ISOUtils.hexString(input)) + "\r\n", MessageTag.DATA);

                    } else {
                        showMessage(context.getString(R.string.msg_write_illegal) + "\r\n", MessageTag.ERROR);
                    }
                } catch (NSDKException e) {
                    e.printStackTrace();
                    showErrorMessage(e, context.getString(R.string.msg_write_block_data_error));
                    showMessage(context.getString(R.string.msg_check_rf_isPoweredon) + "\r\n", MessageTag.ERROR);
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
                    byte block = (byte) Integer.parseInt(etBlock.getText().toString());
                    byte[] input = ISOUtils.hex2byte(etData.getText().toString());
                    if (block >= 0 && block <= 255 && input.length == 4) {
                        mExtM1Card.increment(block, input);
                        showMessage(context.getString(R.string.msg_increase_operation_finished) + "true", MessageTag.NORMAL);
                        showMessage(context.getString(R.string.msg_storage_block) + block + "\r\n", MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_data) + input == null ? "null" : ISOUtils.hexString(input) + "\r\n", MessageTag.DATA);
                    } else {
                        showMessage(context.getString(R.string.msg_input_illegal) + "\r\n", MessageTag.ERROR);
                    }
                } catch (NSDKException e) {
                    e.printStackTrace();
                    showErrorMessage(e, context.getString(R.string.msg_increase_operation_error));
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
                    byte block = (byte) Integer.parseInt(etBlock.getText().toString());
                    byte[] input = ISOUtils.hex2byte(etData.getText().toString());
                    if (block >= 0 && block <= 255 && input.length == 4) {
                        mExtM1Card.decrement(block, input);
                        showMessage(context.getString(R.string.msg_decrease_operation_finished) + "true", MessageTag.NORMAL);
                        showMessage(context.getString(R.string.msg_storage_block) + block + "\r\n", MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_data) + input == null ? "null" : ISOUtils.hexString(input) + "\r\n", MessageTag.DATA);
                    } else {
                        showMessage(context.getString(R.string.msg_input_illegal) + "\r\n", MessageTag.ERROR);
                    }
                } catch (NSDKException e) {
                    e.printStackTrace();
                    showErrorMessage(e, context.getString(R.string.msg_decrease_operation_error));
                    showMessage(context.getString(R.string.msg_check_rf_isPoweredon) + "\r\n", MessageTag.ERROR);
                }
            }
        });
    }
}
