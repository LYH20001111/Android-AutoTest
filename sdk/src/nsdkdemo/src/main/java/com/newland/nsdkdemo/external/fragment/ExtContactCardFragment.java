package com.newland.nsdkdemo.external.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.card.contact.ContactCardSlot;
import com.newland.nsdk.core.api.common.card.contact.ContactCardType;
import com.newland.nsdk.core.api.common.crypto.AlgorithmParameters;
import com.newland.nsdk.core.api.common.crypto.CipherOutput;
import com.newland.nsdk.core.api.common.crypto.CipherType;
import com.newland.nsdk.core.api.common.crypto.PaddingMode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.keymanager.CipherMode;
import com.newland.nsdk.core.api.common.keymanager.DUKPTDerivateKey;
import com.newland.nsdk.core.api.common.keymanager.DUKPTDerivateUsage;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.api.common.keymanager.KeyUsage;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.external.card.ExtAPDUOutput;
import com.newland.nsdk.core.api.external.card.contact.ExtCPUContactCard;
import com.newland.nsdk.core.api.external.crypto.ExtCrypto;
import com.newland.nsdk.core.api.internal.card.contact.CPUContactCard;
import com.newland.nsdk.core.external.card.contact.ExtCPUContactCardImpl;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.AppConfig;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.annotation.MethodGridEntity;
import com.newland.nsdkdemo.common.fragment.BaseFragment;
import com.newland.nsdkdemo.common.utils.DialogUtils;
import com.newland.nsdkdemo.common.utils.MessageTag;

public class ExtContactCardFragment extends ExtBaseFragment {

    private ExtCPUContactCard extCPUContactCard;
    private ContactCardSlot cardSlot = ContactCardSlot.IC1;
    private ContactCardType cardType;

    private static final int INDEX_EXTSMARTCARD_POWERON = 1;
    private static final int INDEX_EXTSMARTCARD_APDU = 2;

    private static final int INDEX_EXTSMARTCARD_CRYPTO_APDU = 3;
    private static final int INDEX_EXTSMARTCARD_POWERDOWN = 4;

    @SuppressLint("ValidFragment")
    public ExtContactCardFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_extsmartcard_f);
    }

    @Override
    public void initData() {
        extCPUContactCard = new ExtCPUContactCardImpl(cardSlot);
    }

    @Override
    public Object getModule() {
        return ExtContactCardFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.m0_power_on, functionid = INDEX_EXTSMARTCARD_POWERON)
    private void powerUp() {
        DialogUtils.createCustomDialog(context, R.string.tv_iccard_poweron, null, R.layout.dialog_contactcard_powerup, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                RadioButton rbContactSlotIC1 = view.findViewById(R.id.contact_slot_IC1);
                RadioButton rbContactTypeCPU = view.findViewById(R.id.contact_type_CPU);
                LinearLayout llICCardData = view.findViewById(R.id.linear_iccard_data);
                rbContactTypeCPU.setChecked(true);
                rbContactSlotIC1.setChecked(true);
                llICCardData.setVisibility(View.GONE);

            }

            @Override
            public void onResult(int id, View view) {
                RadioButton rbContactSlotIC1 = view.findViewById(R.id.contact_slot_IC1);
                RadioButton rbContactSlotIC2 = view.findViewById(R.id.contact_slot_IC2);
                RadioButton rbContactSlotSAM1 = view.findViewById(R.id.contact_slot_SAM1);
                RadioButton rbContactSlotSAM2 = view.findViewById(R.id.contact_slot_SAM2);
                RadioButton rbContactSlotSAM3 = view.findViewById(R.id.contact_slot_SAM3);
                RadioButton rbContactSlotSAM4 = view.findViewById(R.id.contact_slot_SAM4);
                RadioButton rbContactTypeCPU = view.findViewById(R.id.contact_type_CPU);
                if (rbContactSlotIC1.isChecked()) {
                    cardSlot = ContactCardSlot.IC1;
                } else if (rbContactSlotIC2.isChecked()) {
                    cardSlot = ContactCardSlot.IC2;
                } else if (rbContactSlotSAM1.isChecked()) {
                    cardSlot = ContactCardSlot.SAM1;

                } else if (rbContactSlotSAM2.isChecked()) {
                    cardSlot = ContactCardSlot.SAM2;
                } else if (rbContactSlotSAM3.isChecked()) {
                    cardSlot = ContactCardSlot.SAM3;

                } else if (rbContactSlotSAM4.isChecked()) {
                    cardSlot = ContactCardSlot.SAM4;
                } else if (rbContactTypeCPU.isChecked()) {
                    cardType = ContactCardType.CPU;
                }
                byte[] atr = null;
                try {
                    atr = extCPUContactCard.powerUp();
                    showMessage("ATR:" + ISOUtils.hexString(atr));
                } catch (NSDKException e) {
                    e.printStackTrace();
                    showErrorMessage(e, "power up");
                }
            }
        });
    }
    @MethodGridEntity(btnnameid = R.string.tv_extsmartcard_apdu, functionid = INDEX_EXTSMARTCARD_APDU)
    private void sendCommand() {
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
                    ExtAPDUOutput result = extCPUContactCard.performAPDU(key, parameters, req);
                    showMessage(context.getString(R.string.msg_send_data) + (req == null ? null : ISOUtils.hexString(req)), MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_get_data) + (result == null ? null : ISOUtils.hexString(result.getData())), MessageTag.DATA);
                } catch (NSDKException e) {
                    e.printStackTrace();
                    showErrorMessage(e, context.getString(R.string.msg_card_transmit_error));
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_extsmartcard_crypto_apdu, functionid = INDEX_EXTSMARTCARD_CRYPTO_APDU)
    private void sendEncryptedCommand() {
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
            ExtAPDUOutput result = extCPUContactCard.performAPDU(key, params, 5, encryptCmd.getData());
            showMessage("apdu result:" + ISOUtils.hexString(result.getData()));
            showMessage("apdu result length:" + result.getDataLen());
            CipherOutput result1 = crypto.decrypt(key, CipherType.DES_ECB, PaddingMode.NONE, null, result.getData());
            showMessage("apdu decrypt result:" + ISOUtils.hexString(result1.getData()));
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, e.getMessage());
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_extsmartcard_powerdown, functionid = INDEX_EXTSMARTCARD_POWERDOWN)
    private void powerDown() {
        try {
            extCPUContactCard.powerDown();
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, context.getString(R.string.tv_extsmartcard_powerdown));
        }
        showMessage(context.getString(R.string.msg_poweroff_end) + "\r\n", MessageTag.DATA);
    }
}
