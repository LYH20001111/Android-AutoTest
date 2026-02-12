package com.newland.nsdkdemo.internal.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.newland.nsdk.core.api.common.card.contact.ContactCardConfig;
import com.newland.nsdk.core.api.common.card.contact.ContactCardSlot;
import com.newland.nsdk.core.api.common.card.contact.ContactCardType;
import com.newland.nsdk.core.api.common.card.contact.SAMClkFrequency;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.internal.card.contact.CPUContactCard;
import com.newland.nsdk.core.internal.card.contact.CPUContactCardImpl;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.AppConfig;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.annotation.MethodGridEntity;
import com.newland.nsdkdemo.common.fragment.BaseFragment;
import com.newland.nsdkdemo.common.utils.DialogUtils;
import com.newland.nsdkdemo.common.utils.MessageTag;

public class ContactCardFragment extends InternalBaseFragment {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor mEditor;
    private CPUContactCard cpuContactCard;
    private ContactCardSlot cardSlot = ContactCardSlot.IC1;
    private ContactCardType cardType;

    public ContactCardFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_iccard_f);
    }

    @Override
    public void initData() {
        cpuContactCard = new CPUContactCardImpl(cardSlot);
        sharedPreferences = context.getSharedPreferences(AppConfig.SharedPreferenceConfig.SHARE_PREFERENCE, Context.MODE_PRIVATE);
        mEditor = sharedPreferences.edit();
    }

    @Override
    public Object getModule() {
        return ContactCardFragment.this;
    }

    private static final int INDEX_ICCARD_SET_CONFIG = 1;
    private static final int INDEX_ICCARD_POWERON = 2;
    private static final int INDEX_ICCARD_COMMUNICATION = 3;
    private static final int INDEX_ICCARD_POWEROFF = 4;

    @MethodGridEntity(btnnameid = R.string.tv_iccard_set_config, functionid = INDEX_ICCARD_SET_CONFIG)
    private void setConfig() {
        showMessage(context.getString(R.string.msg_power_off_tip), MessageTag.TIP);
        DialogUtils.createCustomDialog(context, R.string.tv_iccard_set_config, null, R.layout.dialog_contact_card_set_config, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                Spinner spnConfigName = view.findViewById(R.id.spn_contactCard_setConfig);
                spnConfigName.setSelection(sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.CONTACT_CARD_SET_CONFIG_NAME, 0));
                spnConfigName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.CONTACT_CARD_SET_CONFIG_NAME, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onResult(int id, View view) {
                Spinner spnConfigName = view.findViewById(R.id.spn_contactCard_setConfig);
                SAMClkFrequency samClkFrequency = spnConfigName.getSelectedItem().toString().contains("2.4MHz") ? SAMClkFrequency.CLK_2400KHz : SAMClkFrequency.CLK_4000KHz;
                ContactCardConfig config = new ContactCardConfig();
                config.setSamClkFrequency(samClkFrequency);

                try {
                    cpuContactCard.setConfig(config);
                    showMessage(context.getString(R.string.msg_set_config));
                } catch (NSDKException e) {
                    showErrorMessage(e, e.getMessage());
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_iccard_poweron, functionid = INDEX_ICCARD_POWERON)
    private void icCardPowerOn() {
        DialogUtils.createCustomDialog(context, R.string.tv_iccard_poweron, null, R.layout.dialog_contactcard_powerup, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                RadioButton rbSlotIC1 = view.findViewById(R.id.contact_slot_IC1);
                RadioButton rbContactTypeCPU = view.findViewById(R.id.contact_type_CPU);
                LinearLayout llICCardData = view.findViewById(R.id.linear_iccard_data);
                rbContactTypeCPU.setChecked(true);
                rbSlotIC1.setChecked(true);
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
                if(rbContactSlotIC1.isChecked()) {
                    cardSlot = ContactCardSlot.IC1;
                }
                if(rbContactSlotIC2.isChecked()) {
                    cardSlot = ContactCardSlot.IC2;
                }


               if( rbContactSlotSAM1.isChecked()) {
                   cardSlot = ContactCardSlot.SAM1;
               }

               if(rbContactSlotSAM2.isChecked()) {
                   cardSlot = ContactCardSlot.SAM2;
               }
               if(rbContactSlotSAM3.isChecked()) {
                   cardSlot = ContactCardSlot.SAM3;
               }
               if(rbContactSlotSAM4.isChecked()) {
                   cardSlot = ContactCardSlot.SAM4;
               }

               if(rbContactTypeCPU.isChecked()) {
                    cardType = ContactCardType.CPU;
               }

               cpuContactCard = new CPUContactCardImpl(cardSlot);
               showMessage(context.getString(R.string.common_slot) + cardSlot + "；" + context.getString(R.string.common_card_type) + cardType, MessageTag.DATA);
               byte[] poweronresult = null;
               try {
                    poweronresult = cpuContactCard.powerUp();
                    showMessage(context.getString(R.string.msg_poweron_result) + (poweronresult == null ? null : ISOUtils.hexString(poweronresult)) + "\r\n", MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_poweron_end) + "\r\n", MessageTag.DATA);
               } catch (NSDKException e) {
                    e.printStackTrace();
                    showErrorMessage(e, context.getString(R.string.msg_slot_poweron_ex));
               }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_iccard_transation, functionid = INDEX_ICCARD_COMMUNICATION)
    private void icCardCommunication() {
        DialogUtils.createCustomDialog(context, R.string.tv_iccard_transation, null, R.layout.dialog_contactcard_powerup, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                RadioButton rbContactSlotIC1 = view.findViewById(R.id.contact_slot_IC1);
                RadioButton rbContactTypeCPU = view.findViewById(R.id.contact_type_CPU);
                LinearLayout llICCardData = view.findViewById(R.id.linear_iccard_data);
                rbContactTypeCPU.setChecked(true);
                rbContactSlotIC1.setChecked(true);
                llICCardData.setVisibility(View.VISIBLE);
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
                EditText etICCardSend = view.findViewById(R.id.edit_ICCardSend);
                if(rbContactSlotIC1.isChecked()) {
                    cardSlot = ContactCardSlot.IC1;
                }
                if(rbContactSlotIC2.isChecked()) {
                    cardSlot = ContactCardSlot.IC2;
                }
                if( rbContactSlotSAM1.isChecked()) {
                    cardSlot = ContactCardSlot.SAM1;
                }

                if(rbContactSlotSAM2.isChecked()) {
                    cardSlot = ContactCardSlot.SAM2;
                }
                if(rbContactSlotSAM3.isChecked()) {
                    cardSlot = ContactCardSlot.SAM3;
                }
                if(rbContactSlotSAM4.isChecked()) {
                    cardSlot = ContactCardSlot.SAM4;
                }
                if(rbContactTypeCPU.isChecked()) {
                    cardType = ContactCardType.CPU;
                }
                cpuContactCard = new CPUContactCardImpl(cardSlot);
                String data = etICCardSend.getText().toString();

                byte[] req = ISOUtils.hex2byte(data);
                showMessage(context.getString(R.string.common_slot) + cardSlot + "；" + context.getString(R.string.common_card_type) + cardType, MessageTag.DATA);
                byte[] back = new byte[0];
                try {
                    back = cpuContactCard.performAPDU(req);
                    showMessage(context.getString(R.string.msg_iccard_type) + cardType, MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_send_data) + data, MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_receive_data) + (back == null ? null : ISOUtils.hexString(back)), MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_iccard_comm_succ) + "\r\n", MessageTag.DATA);
                } catch (NSDKException e) {
                    e.printStackTrace();
                    showMessage(context.getString(R.string.msg_iccard_type) + cardType, MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_send_data) + "0084000004", MessageTag.DATA);
                    showErrorMessage(e, context.getString(R.string.msg_iccard_comm_ex));
                }
            }
        });


    }



    @MethodGridEntity(btnnameid = R.string.tv_iccard_powerof, functionid = INDEX_ICCARD_POWEROFF)
    private void icCardPowerOff() {
        showMessage(context.getString(R.string.common_slot) + cardSlot + "；" + context.getString(R.string.common_card_type) + cardType, MessageTag.DATA);
        try {
            cpuContactCard.powerDown();
            showMessage(cardSlot.toString() + context.getString(R.string.msg_poweroff_end) + "\r\n", MessageTag.DATA);
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, context.getString(R.string.msg_slot_poweroff_ex));
        }
    }
}
