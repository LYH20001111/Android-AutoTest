package com.newland.nsdkdemo.external.fragment;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.external.cardemulator.EmulateCardStatus;
import com.newland.nsdk.core.api.external.cardemulator.EmulateCardType;
import com.newland.nsdk.core.api.external.cardemulator.EmulateConfig;
import com.newland.nsdk.core.api.external.cardemulator.EmulateEventType;
import com.newland.nsdk.core.api.external.cardemulator.EmulateFileType;
import com.newland.nsdk.core.api.external.cardemulator.ExtCardEmulator;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.annotation.MethodGridEntity;
import com.newland.nsdkdemo.common.utils.DialogUtils;
import com.newland.nsdkdemo.common.utils.EnumUtils;

public class ExtCardEmulatorFragment extends ExtBaseFragment{
    private ExtCardEmulator extCardEmulator;
    private static final int INDEX_CARD_EMULATOR_INIT = 1;
    private static final int INDEX_CARD_EMULATOR_WRITE_CONFIG = 2;
    private static final int INDEX_CARD_EMULATOR_WRITE_DATA = 3;
    private static final int INDEX_CARD_EMULATOR_READ_DATA = 4;
    private static final int INDEX_CARD_EMULATOR_READ_CONFIG = 5;
    private static final int INDEX_CARD_EMULATOR_START = 6;
    private static final int INDEX_CARD_EMULATOR_GET_STATUS = 7;
    private static final int INDEX_CARD_EMULATOR_GET_EVENT = 8;
    private static final int INDEX_CARD_EMULATOR_END = 9;

    public ExtCardEmulatorFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.ext_card_emulator_fragment);
    }

    @Override
    public void initData() {
        extCardEmulator = (ExtCardEmulator) moduleManager.getModule(ModuleType.EXT_CARD_EMULATOR);
    }

    @Override
    public Object getModule() {
        return ExtCardEmulatorFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.ext_card_emulator_init, functionid = INDEX_CARD_EMULATOR_INIT)
    private void init() {
        try {
            extCardEmulator.init();
            showMessage("Init card emulator success.");
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.ext_card_emulator_init));
        }
    }

    @MethodGridEntity(btnnameid = R.string.ext_card_emulator_writeConfig, functionid = INDEX_CARD_EMULATOR_WRITE_CONFIG)
    private void writeConfig() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.ext_card_emulator_writeConfig), null, R.layout.dialog_ext_card_emulator_write_read_config, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View view) {
                Spinner spnEmulateCardType = view.findViewById(R.id.spn_cardEmulator_cardType);
                EmulateCardType cardType = EnumUtils.getEmulateCardType(spnEmulateCardType.getSelectedItem().toString());
                EditText editUID = view.findViewById(R.id.edit_cardEmulator_uid);
                byte[] uid = ISOUtils.hex2byte(editUID.getText().toString());
                EditText editMemorySize = view.findViewById(R.id.edit_cardEmulator_memorySize);
                int memorySize = Integer.parseInt(editMemorySize.getText().toString());
                EmulateConfig config = new EmulateConfig();
                config.setUid(uid);
                config.setMemorySize(memorySize);

                try {
                    extCardEmulator.setConfig(cardType, config);
                    showMessage("Write configuration success.");
                } catch (NSDKException e) {
                    showErrorMessage(e, context.getString(R.string.ext_card_emulator_writeConfig));
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.ext_card_emulator_writeData, functionid = INDEX_CARD_EMULATOR_WRITE_DATA)
    private void writeData() {
        DialogUtils.createCustomDialog(context, R.string.ext_card_emulator_writeData, null, R.layout.dialog_ext_card_emulator_write_read_data, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                LinearLayout llReadDataParams = view.findViewById(R.id.linear_cardEmulator_readDataParams);
                llReadDataParams.setVisibility(View.GONE);
            }

            @Override
            public void onResult(int id, View view) {
                Spinner spnEmulateFileType = view.findViewById(R.id.spnCardEmulateFileType);
                EmulateFileType fileType = EnumUtils.getEmulateFileType(spnEmulateFileType.getSelectedItem().toString());
                EditText editData = view.findViewById(R.id.edit_cardEmulator_writeData);
                byte[] data = null;
                if (!TextUtils.isEmpty(editData.getText().toString())) {
                    if (fileType.name().contains("NDEF")) {
                        EmulateCardType cardType = EmulateCardType.T2T;
                        if (fileType.name().contains("T4T")) {
                            cardType = EmulateCardType.T4T;
                        }
                        data = EnumUtils.spliceDataForConfiguration(cardType, editData.getText().toString(), (byte) 0x00);
                    } else {
                        data = ISOUtils.hex2byte(editData.getText().toString());
                    }
                }
                try {
                    extCardEmulator.writeData(fileType, data);
                    showMessage("Write data success.");
                } catch (NSDKException e) {
                    showErrorMessage(e, context.getString(R.string.ext_card_emulator_writeData));
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.ext_card_emulator_readData, functionid = INDEX_CARD_EMULATOR_READ_DATA)
    private void readData() {
        DialogUtils.createCustomDialog(context, R.string.ext_card_emulator_readData, null, R.layout.dialog_ext_card_emulator_write_read_data, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                LinearLayout llWriteDataParams = view.findViewById(R.id.linear_cardEmulator_writeDataParams);
                llWriteDataParams.setVisibility(View.GONE);
            }

            @Override
            public void onResult(int id, View view) {
                Spinner spnEmulateFileType = view.findViewById(R.id.spnCardEmulateFileType);
                EmulateFileType fileType = EnumUtils.getEmulateFileType(spnEmulateFileType.getSelectedItem().toString());
                EditText editReadDataLength = view.findViewById(R.id.edit_cardEmulator_readDataLength);
                int readDataLength = 0;
                if (!TextUtils.isEmpty(editReadDataLength.getText().toString())) {
                    readDataLength = Integer.parseInt(editReadDataLength.getText().toString());
                }
                try {
                    byte[] data = extCardEmulator.readData(fileType, readDataLength);
                    showMessage("Read data: " + ISOUtils.hexString(data));
                } catch (NSDKException e) {
                    showErrorMessage(e, context.getString(R.string.ext_card_emulator_readData));
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.ext_card_emulator_readConfig, functionid = INDEX_CARD_EMULATOR_READ_CONFIG)
    private void readConfig() {
        DialogUtils.createCustomDialog(context, R.string.ext_card_emulator_readConfig, null, R.layout.dialog_ext_card_emulator_write_read_config, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                LinearLayout llConfigParams = view.findViewById(R.id.linear_cardEmulator_configurationParams);
                llConfigParams.setVisibility(View.GONE);
            }

            @Override
            public void onResult(int id, View view) {
                Spinner spnCardType = view.findViewById(R.id.spn_cardEmulator_cardType);
                EmulateCardType cardType = EnumUtils.getEmulateCardType(spnCardType.getSelectedItem().toString());
                try {
                    EmulateConfig emulateConfig = extCardEmulator.getConfig(cardType);
                    showMessage("SAK: " + ISOUtils.hexString(new byte[] {emulateConfig.getSak()}));
                    showMessage("UID: " + ISOUtils.hexString(emulateConfig.getUid()));
                    showMessage("ATS: " + (emulateConfig.getAts() == null ? "null" : ISOUtils.hexString(emulateConfig.getAts())));
                    showMessage("Memory Size: " + emulateConfig.getMemorySize());
                } catch (NSDKException e) {
                    showErrorMessage(e, context.getString(R.string.ext_card_emulator_readConfig));
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.ext_card_emulator_start, functionid = INDEX_CARD_EMULATOR_START)
    private void start() {
        DialogUtils.createCustomDialog(context, R.string.ext_card_emulator_start, null, R.layout.dialog_ext_card_emulator_write_read_config, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                LinearLayout llConfigParams = view.findViewById(R.id.linear_cardEmulator_configurationParams);
                llConfigParams.setVisibility(View.GONE);
            }

            @Override
            public void onResult(int id, View view) {
                Spinner spnCardType = view.findViewById(R.id.spn_cardEmulator_cardType);
                EmulateCardType cardType = EnumUtils.getEmulateCardType(spnCardType.getSelectedItem().toString());
                try {
                    extCardEmulator.start(cardType);
                    showMessage(context.getString(R.string.ext_card_emulator_start));
                } catch (NSDKException e) {
                    showErrorMessage(e, context.getString(R.string.ext_card_emulator_start));
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.ext_card_emulator_getStatus, functionid = INDEX_CARD_EMULATOR_GET_STATUS)
    private void getStatus() {
        DialogUtils.createCustomDialog(context, R.string.ext_card_emulator_getStatus, null, R.layout.dialog_ext_card_emulator_write_read_config, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                LinearLayout llConfigParams = view.findViewById(R.id.linear_cardEmulator_configurationParams);
                llConfigParams.setVisibility(View.GONE);
            }

            @Override
            public void onResult(int id, View view) {
                Spinner spnCardType = view.findViewById(R.id.spn_cardEmulator_cardType);
                EmulateCardType cardType = EnumUtils.getEmulateCardType(spnCardType.getSelectedItem().toString());
                try {
                    EmulateCardStatus status = extCardEmulator.getStatus(cardType);
                    showMessage("Current status: " + status.name());
                } catch (NSDKException e) {
                    showErrorMessage(e, context.getString(R.string.ext_card_emulator_getStatus));
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.ext_card_emulator_getEvent, functionid = INDEX_CARD_EMULATOR_GET_EVENT)
    private void getEvent() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.ext_card_emulator_getEvent), null, R.layout.dialog_ext_card_emulator_get_event, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View view) {
                Spinner spnEmulateEventType = view.findViewById(R.id.spn_cardEmulator_eventType);
                EmulateEventType emulateEventType = EnumUtils.getEmulateEventType(spnEmulateEventType.getSelectedItem().toString());
                try {
                    byte[] event = extCardEmulator.getEvent(emulateEventType);
                    showMessage("Event: " + ISOUtils.hexString(event));
                } catch (NSDKException e) {
                    showErrorMessage(e, context.getString(R.string.ext_card_emulator_getEvent));
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.ext_card_emulator_end, functionid = INDEX_CARD_EMULATOR_END)
    private void end() {
        try {
            extCardEmulator.finish();
            showMessage(context.getString(R.string.ext_card_emulator_end));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.ext_card_emulator_end));
        }
    }

}
