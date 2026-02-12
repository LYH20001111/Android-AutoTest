package com.newland.nsdkdemo.external.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;

import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.crypto.CipherType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.keymanager.DUKPTKey;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.api.common.keymanager.KeyUsage;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.common.pinentry.PINBlockMode;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.external.pinentry.Alignment;
import com.newland.nsdk.core.api.external.pinentry.CipherPAN;
import com.newland.nsdk.core.api.external.pinentry.ExtOfflinePINParameters;
import com.newland.nsdk.core.api.external.pinentry.ExtPINEntry;
import com.newland.nsdk.core.api.external.pinentry.ExtPINEntryListener;
import com.newland.nsdk.core.api.external.pinentry.ExtPINEntryParameters;
import com.newland.nsdk.core.api.external.pinentry.ExtPINMaskLine;
import com.newland.nsdk.core.api.external.pinentry.ExtendedCipherPAN;
import com.newland.nsdk.core.api.external.pinentry.ExtendedExtPINEntryListener;
import com.newland.nsdk.core.api.external.pinentry.ExtendedExtPINEntryParams;
import com.newland.nsdk.core.api.external.pinentry.PINMessageMode;
import com.newland.nsdk.core.api.external.pinentry.RSAKey;
import com.newland.nsdk.core.api.external.pinentry.SessionType;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.AppConfig;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.annotation.MethodGridEntity;
import com.newland.nsdkdemo.common.utils.DialogUtils;
import com.newland.nsdkdemo.common.utils.EnumUtils;
import com.newland.nsdkdemo.common.utils.MessageTag;

public class ExtPINEntryFragment extends ExtBaseFragment {

    private ExtPINEntry extPinInput;
    private static final int INDEX_EXTPINPAD_ONLINEPIN = 1;
    private static final int INDEX_EXTPINPAD_ONLINEPIN_AES = 2;
    private static final int INDEX_EXTPINPAD_ONLINEPIN_DUKPT = 3;
    private static final int INDEX_EXTPINPAD_OFFLINEPIN = 4;
    private static final int INDEX_EXTPINPAD_OFFLINEPIN_AES = 5;
    private static final int INDEX_EXTPINPAD_CANCEL = 6;
    private static final int INDEX_EXTPINPAD_PININPUT_TEST = 7;
    private static final int INDEX_EXTPINPAD_ENHANCED_ONLINEPIN = 8;
    private static final int INDEX_EXTPINPAD_VERIFY_OFFLINE_PIN = 9;
    private int dukptFlag = -1;




    private ExtPINEntryListener listener = new ExtPINEntryListener() {

        @Override
        public void onOnlineSuccess(int pinLen, byte[] pinBlock, byte[] dukptKsn) {
            showMessage("Online PIN Success, PIN len=" + pinLen + "\n"
                    + "PIN block=" + (pinBlock != null ? ISOUtils.hexString(pinBlock) : "null") + "\n"
                    + "KSN=" + (dukptKsn != null ? ISOUtils.hexString(dukptKsn) : "null"));
        }

        @Override
        public void onOfflineSuccess(int pinLen, byte[] pinBlock, byte[] randomKey) {
            showMessage("Offline PIN input success, PIN len=" + pinLen + "\n"
                    + "PIN block=" + (pinBlock != null ? ISOUtils.hexString(pinBlock) : "null") + "\n"
                    + "Random key=" + (randomKey != null ? ISOUtils.hexString(randomKey) : "null"));
        }

        @Override
        public void onError(int i, String msg) {
            showMessage(String.format("[%d] %s", i, msg), MessageTag.ERROR);
        }

        @Override
        public void onTimeout() {
            showMessage("Online PIN input timeout!");
        }

        @Override
        public void onCancel() {
            showMessage("Online PIN input canceled!");
        }
    };

    private ExtendedExtPINEntryListener extendedListener = new ExtendedExtPINEntryListener() {
        @Override
        public void onOnlineSuccessExtended(int pinLen, byte[] pinBlock, byte[] dukptKsn, byte[] tlvData) {
            showMessage("Extended Online PIN Success, PIN len=" + pinLen + "\n"
                    + "PIN block=" + (pinBlock != null ? ISOUtils.hexString(pinBlock) : "null") + "\n"
                    + "KSN=" + (dukptKsn != null ? ISOUtils.hexString(dukptKsn) : "null") + "\n"
                    + "TLVData=" + (tlvData != null ? ISOUtils.hexString(tlvData) : "null"));
        }

        @Override
        public void onOnlineSuccess(int pinLen, byte[] pinBlock, byte[] dukptKsn) {
            showMessage("Online PIN Success, PIN len=" + pinLen + "\n"
                    + "PIN block=" + (pinBlock != null ? ISOUtils.hexString(pinBlock) : "null") + "\n"
                    + "KSN=" + (dukptKsn != null ? ISOUtils.hexString(dukptKsn) : "null"));
        }

        @Override
        public void onOfflineSuccess(int pinLen, byte[] pinBlock, byte[] randomKey) {
            showMessage("Offline PIN input success, PIN len=" + pinLen + "\n"
                    + "PIN block=" + (pinBlock != null ? ISOUtils.hexString(pinBlock) : "null") + "\n"
                    + "Random key=" + (randomKey != null ? ISOUtils.hexString(randomKey) : "null"));
        }

        @Override
        public void onError(int i, String msg) {
            showMessage(String.format("[%d] %s", i, msg), MessageTag.ERROR);
        }

        @Override
        public void onTimeout() {
            showMessage("Online PIN input timeout!");
        }

        @Override
        public void onCancel() {
            showMessage("Online PIN input canceled!");
        }
    };

    @SuppressLint("ValidFragment")
    public ExtPINEntryFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_extpininput_f);
    }

    @Override
    public void initData() {
        extPinInput = (ExtPINEntry) moduleManager.getModule(ModuleType.EXT_PIN_ENTRY);
    }

    @Override
    public Object getModule() {
        return ExtPINEntryFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_pininput_online, functionid = INDEX_EXTPINPAD_ONLINEPIN)
    private void startOnlinePINEntry() {

        try {
            SymmetricKey pinKey = new SymmetricKey();
            pinKey.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_WK_PIN);
            pinKey.setKeyType(KeyType.DES);

            SymmetricKey panKey = new SymmetricKey();
            panKey.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_WK_TRACK);
            panKey.setKeyType(KeyType.DES);

            CipherPAN cipherPan = new CipherPAN();
            cipherPan.setPANKey(panKey);
            cipherPan.setClearPANLen(16);
            cipherPan.setCipherPAN(ISOUtils.hex2byte("86BEC8567FDD69F104063642C76CFEC4"));

            ExtPINEntryParameters parameter = new ExtPINEntryParameters();
            parameter.setMaxPINLen((byte) 6);
            parameter.setMaskLine(ExtPINMaskLine.LINE_4);
            parameter.setDisplayMessages(new String[]{"Input online pin"});
            parameter.setPINBlockMode(PINBlockMode.ISO9564_0);
            parameter.setAutoComplete(true);
            parameter.setPinLengthRange(new byte[] {4, 5, 6});
            parameter.setPinMaskAlignment(Alignment.Right);
            String pan = "6225780779834244";
            extPinInput.startOnlinePINEntry(pinKey, pan,60, parameter, listener);
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "input online PIN(DES)");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_pininput_online_aes, functionid = INDEX_EXTPINPAD_ONLINEPIN_AES)
    private void startOnlinePINEntryAES() {

        try {
            SymmetricKey pinKey = new SymmetricKey();
            pinKey.setKeyID(AppConfig.Keys.MKSK_AES_INDEX_WK_PIN);
            pinKey.setKeyType(KeyType.AES);

            SymmetricKey panKey = new SymmetricKey();
            panKey.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_WK_TRACK);
            panKey.setKeyType(KeyType.DES);

            CipherPAN cipherPan = new CipherPAN();
            cipherPan.setPANKey(panKey);
            cipherPan.setClearPANLen(16);
            cipherPan.setCipherPAN(ISOUtils.hex2byte("86BEC8567FDD69F104063642C76CFEC4"));

            ExtPINEntryParameters parameter = new ExtPINEntryParameters();
            parameter.setMaxPINLen((byte) 6);
            parameter.setMaskLine(ExtPINMaskLine.LINE_4);
            parameter.setDisplayMessages(new String[]{"Input online pin", null, null, null});
            parameter.setPINBlockMode(PINBlockMode.ISO9564_4);
//            parameter.setPlaintTextPan("6214050711033116");
            parameter.setAutoComplete(true);
            String pan = "6225780779834244";
            extPinInput.startOnlinePINEntry(pinKey, pan,60, parameter, listener);
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "input online PIN(AES)");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_pininput_online_dukpt, functionid = INDEX_EXTPINPAD_ONLINEPIN_DUKPT)
    private void startOnlinePINEntryDUKPT() {

        try {
            DUKPTKey pinKey = new DUKPTKey();
            pinKey.setKeyID(AppConfig.Keys.DUKPT_DES_INDEX);
            pinKey.setKeyType(KeyType.DES);
            pinKey.setKeyUsage(KeyUsage.DUKPT);

            SymmetricKey panKey = new SymmetricKey();
            panKey.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_WK_TRACK);
            panKey.setKeyType(KeyType.DES);

            CipherPAN cipherPan = new CipherPAN();
            cipherPan.setPANKey(panKey);
            cipherPan.setClearPANLen(16);
            cipherPan.setCipherPAN(ISOUtils.hex2byte("86BEC8567FDD69F104063642C76CFEC4"));

            ExtPINEntryParameters parameter = new ExtPINEntryParameters();
            parameter.setMaxPINLen((byte) 6);
            parameter.setMaskLine(ExtPINMaskLine.LINE_4);
            parameter.setDisplayMessages(new String[]{"Input online pin", null, null, null});
            parameter.setPINBlockMode(PINBlockMode.ISO9564_0);
//            parameter.setPlaintTextPan("6214050711033116");
            parameter.setAutoComplete(true);
            String pan = "6225780779834244";
            extPinInput.startOnlinePINEntry(pinKey, pan, 60, parameter, listener);
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "input online PIN(DUKPT)");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_pininput_offline, functionid = INDEX_EXTPINPAD_OFFLINEPIN)
    private void startOfflinePINEntry() {
        try {
            ExtOfflinePINParameters parameter = new ExtOfflinePINParameters();
            parameter.setMaxPINLen((byte) 6);
            parameter.setMaskLine(ExtPINMaskLine.LINE_4);
            parameter.setDisplayMessages(new String[]{"Input offline pin", null, null, null});

            SymmetricKey key = new SymmetricKey();
            key.setKeyType(KeyType.DES);
            key.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_MK);
            parameter.setRandomProtectMode(true);
//            key.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_WK_PIN);
//            parameter.setRandomProtectMode(false);
            parameter.setPINBlockMode(PINBlockMode.ISO9564_0);
            parameter.setAutoComplete(true);

            String pan = "6225780779834244";

            extPinInput.startOfflinePINEntry(key, pan, 60, parameter, listener);
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "input offline PIN(DES)");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_pininput_offline_aes, functionid = INDEX_EXTPINPAD_OFFLINEPIN_AES)
    private void startOfflinePINEntryAES() {
        try {
            ExtOfflinePINParameters parameter = new ExtOfflinePINParameters();
            parameter.setMaxPINLen((byte) 6);
            parameter.setMaskLine(ExtPINMaskLine.LINE_4);
            parameter.setDisplayMessages(new String[]{"Input offline pin", null, null, null});

            SymmetricKey key = new SymmetricKey();
            key.setKeyType(KeyType.AES);
//            key.setKeyID(pinKeyIndex);
//            parameter.setRandomProtectMode(true);

            key.setKeyID(AppConfig.Keys.MKSK_AES_INDEX_WK_PIN);
            parameter.setRandomProtectMode(false);
            parameter.setPINBlockMode(PINBlockMode.ISO9564_4);
            parameter.setAutoComplete(true);

            String pan = "6225780779834244";

            extPinInput.startOfflinePINEntry(key, pan, 60, parameter, listener);
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "input offline PIN(AES)");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_canle_pininput, functionid = INDEX_EXTPINPAD_CANCEL)
    private void cancelPINEntry() {
        try {
            extPinInput.cancelPINEntry();
            showMessage("PIN input cancelled successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "cancel PIN input");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_extpininput_f, functionid = INDEX_EXTPINPAD_PININPUT_TEST)
    private void startPinInput() {
        DialogUtils.createCustomDialog(context, R.string.tv_extpininput_f, null, R.layout.dialog_ext_pinentry, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                Switch swDefaultKeyID = view.findViewById(R.id.switch_default_key_id_btn);
                swDefaultKeyID.setChecked(true);
                RadioGroup rgPinInputSelection = view.findViewById(R.id.dialog_ext_pininput_online_offline_radioGroup);
                RadioButton rbOnlinePinInput = view.findViewById(R.id.dialog_ext_pininput_online_radioButton);
                RadioButton rbOfflinePinInput = view.findViewById(R.id.dialog_ext_pininput_offline_radioButton);
                rbOnlinePinInput.setChecked(true);
                LinearLayout llOnlineParams = view.findViewById(R.id.linear_extPininputOnlineParams);
                llOnlineParams.setVisibility(View.VISIBLE);
                LinearLayout llOfflineParams = view.findViewById(R.id.linear_extPininputOfflineParams);
                llOfflineParams.setVisibility(View.GONE);
                LinearLayout llOnlineDefaultPinKeyIDParams = view.findViewById(R.id.linear_extPininputOnlineDefaultPinKeyIDParams);
                llOnlineDefaultPinKeyIDParams.setVisibility(View.VISIBLE);
                LinearLayout llOnlineEditPinKeyIDParams = view.findViewById(R.id.linear_extPininputOnlineEditPinKeyIDParams);
                llOnlineEditPinKeyIDParams.setVisibility(View.GONE);
                LinearLayout llOnlineDefaultPanKeyIDParams = view.findViewById(R.id.linear_extPininputOnlineDefaultPanKeyIDParams);
                llOnlineDefaultPanKeyIDParams.setVisibility(View.VISIBLE);
                LinearLayout llOnlineEditPanKeyIDParams = view.findViewById(R.id.linear_extPininputOnlineEditPanKeyIDParams);
                llOnlineEditPanKeyIDParams.setVisibility(View.GONE);
                LinearLayout llOfflineDefaultKeyIDParams = view.findViewById(R.id.linear_extPininputOfflineDefaultKeyIDParams);
                llOfflineDefaultKeyIDParams.setVisibility(View.VISIBLE);
                LinearLayout llOfflineEditKeyIDParams = view.findViewById(R.id.linear_extPininputOfflineEditKeyIDParams);
                llOfflineEditKeyIDParams.setVisibility(View.GONE);
                swDefaultKeyID.setOnCheckedChangeListener(((buttonView, isChecked) -> {
                    if(isChecked) {
                        if (rbOnlinePinInput.isChecked()) {
                            llOnlineDefaultPinKeyIDParams.setVisibility(View.VISIBLE);
                            llOnlineEditPinKeyIDParams.setVisibility(View.GONE);
                            llOnlineDefaultPanKeyIDParams.setVisibility(View.VISIBLE);
                            llOnlineEditPanKeyIDParams.setVisibility(View.GONE);
                        }

                        if(rbOfflinePinInput.isChecked()) {
                            llOfflineDefaultKeyIDParams.setVisibility(View.VISIBLE);
                            llOfflineEditKeyIDParams.setVisibility(View.GONE);
                        }
                    } else {
                        if(rbOnlinePinInput.isChecked()) {
                            llOnlineDefaultPinKeyIDParams.setVisibility(View.GONE);
                            llOnlineEditPinKeyIDParams.setVisibility(View.VISIBLE);
                            llOnlineDefaultPanKeyIDParams.setVisibility(View.GONE);
                            llOnlineEditPanKeyIDParams.setVisibility(View.VISIBLE);
                        }

                        if(rbOfflinePinInput.isChecked()) {
                            llOfflineDefaultKeyIDParams.setVisibility(View.GONE);
                            llOfflineEditKeyIDParams.setVisibility(View.VISIBLE);
                        }
                    }
                }));
                rgPinInputSelection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (checkedId == rbOnlinePinInput.getId()) {
                            llOnlineParams.setVisibility(View.VISIBLE);
                            llOfflineParams.setVisibility(View.GONE);
                        }
                        if(checkedId == rbOfflinePinInput.getId()) {
                            llOnlineParams.setVisibility(View.GONE);
                            llOfflineParams.setVisibility(View.VISIBLE);
                        }
                    }
                });
                Switch swOnlineIsDUKPT = view.findViewById(R.id.swPininputOnlineIsDukptCheck);
                swOnlineIsDUKPT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            dukptFlag = 1;
                        }else {
                            dukptFlag = 0;
                        }
                    }
                });

            }

            @Override
            public void onResult(int id, View view) {
                Switch swDefaultKeyID = view.findViewById(R.id.switch_default_key_id_btn);
                RadioButton rbOnlinePinInput = view.findViewById(R.id.dialog_ext_pininput_online_radioButton);
                RadioButton rbOfflinePinInput = view.findViewById(R.id.dialog_ext_pininput_offline_radioButton);
                Spinner spnOnlinePinKeyKeyType = view.findViewById(R.id.dialog_extPininputOnlinePinKeyKeyType);
                Spinner spnOnlinePanKeyKeyType = view.findViewById(R.id.dialog_ext_pininput_online_panKey_keyType);
                Spinner spnOfflineKeyType = view.findViewById(R.id.spnExtPininputOfflineKeyType);
                Spinner spnOnlineDefaultPinKeyID = view.findViewById(R.id.spnExtPininputOnlinePinKeyDefaultID);
                Spinner spnOnlineDefaultPanKeyID = view.findViewById(R.id.spnExtPininputOnlinePanKeyDefaultID);
                Spinner spnOfflineDefaultKeyID = view.findViewById(R.id.spnExtPininputOfflineDefaultKeyID);

                byte offlineKeyID = EnumUtils.getDefaultKeyID(spnOfflineDefaultKeyID.getSelectedItem().toString());
                byte onlinePinKeyID = EnumUtils.getDefaultKeyID(spnOnlineDefaultPinKeyID.getSelectedItem().toString());
                byte onlinePanKeyID = EnumUtils.getDefaultKeyID(spnOnlineDefaultPanKeyID.getSelectedItem().toString());
                KeyType offlineKeyType = EnumUtils.getKeyType(spnOfflineKeyType.getSelectedItem().toString());
                KeyType onlinePinKeyKeyType = EnumUtils.getKeyType(spnOnlinePinKeyKeyType.getSelectedItem().toString());
                KeyType onlinePanKeyKeyType = EnumUtils.getKeyType(spnOnlinePanKeyKeyType.getSelectedItem().toString());
                Switch swIsRandomProtectMode = view.findViewById(R.id.swExtPininpuIsRandomProtectModeCheck);
                if (swDefaultKeyID.isChecked()) {
                    if(rbOnlinePinInput.isChecked()) {
                        EditText etCipherPan = view.findViewById(R.id.dialog_ext_pininput_cipherPan);
                        String panData = etCipherPan.getText().toString();
                        SymmetricKey pinKey = new SymmetricKey();
                        pinKey.setKeyType(onlinePinKeyKeyType);
                        pinKey.setKeyID(onlinePinKeyID);
                        if(dukptFlag == 1) {
                            dukptFlag = 0;
                            pinKey.setKeyUsage(KeyUsage.DUKPT);
                        }

                        SymmetricKey panKey = new SymmetricKey();
                        panKey.setKeyID(onlinePanKeyID);
                        panKey.setKeyType(onlinePanKeyKeyType);

                        CipherPAN cipherPAN = new CipherPAN();
                        cipherPAN.setCipherPAN(ISOUtils.hex2byte(panData));
                        cipherPAN.setClearPANLen(16);
                        cipherPAN.setPANKey(panKey);

                        ExtPINEntryParameters extPINEntryParameters = new ExtPINEntryParameters();
                        extPINEntryParameters.setMaskLine(ExtPINMaskLine.LINE_4);
                        extPINEntryParameters.setMaxPINLen((byte) 6);
                        extPINEntryParameters.setDisplayMessages(new String[]{"Input online pin", null, null, null});
                        extPINEntryParameters.setAutoComplete(true);
                        if("AES".equals(spnOfflineKeyType.getSelectedItem())) {
                            extPINEntryParameters.setPINBlockMode(PINBlockMode.ISO9564_4);
                        }else {
                            extPINEntryParameters.setPINBlockMode(PINBlockMode.ISO9564_0);
                        }
                        try {
                            extPinInput.startOnlinePINEntry(pinKey, panData, 60, extPINEntryParameters, listener);
                        }catch (NSDKException e) {
                            showErrorMessage(e, context.getString(R.string.dialog_ext_tv_pin_pininput_online_pin));
                        }
                    }else if(rbOfflinePinInput.isChecked()) {
                        EditText etOfflinePan = view.findViewById(R.id.etPininputOfflinePanData);
                        String PAN = etOfflinePan.getText().toString();
                        ExtOfflinePINParameters parameters = new ExtOfflinePINParameters();
                        swIsRandomProtectMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if(isChecked) {
                                    parameters.setRandomProtectMode(true);
                                }else {
                                    parameters.setRandomProtectMode(false);
                                }
                            }
                        });
                        parameters.setMaskLine(ExtPINMaskLine.LINE_4);
                        parameters.setMaxPINLen((byte) 6);
                        parameters.setAutoComplete(true);
                        if("AES".equals(spnOfflineKeyType.getSelectedItem().toString())) {
                            parameters.setPINBlockMode(PINBlockMode.ISO9564_4);
                        }else {
                            parameters.setPINBlockMode(PINBlockMode.ISO9564_0);
                        }
                        parameters.setDisplayMessages(new String[] {"Input offline PIN", null, null, null});

                        SymmetricKey symmetricKey = new SymmetricKey();
                        symmetricKey.setKeyType(offlineKeyType);
                        symmetricKey.setKeyID(offlineKeyID);

                        try {
                            extPinInput.startOfflinePINEntry(symmetricKey, PAN, 60 , parameters, listener);
                        }catch (NSDKException e) {
                            showErrorMessage(e, context.getString(R.string.dialog_ext_tv_pin_pininput_offline_pin));
                        }
                    }
                } else {
                    if(rbOnlinePinInput.isChecked()) {
                        EditText etCipherPan = view.findViewById(R.id.dialog_ext_pininput_cipherPan);
                        String panData = etCipherPan.getText().toString();
                        EditText etOnlinePinKeyKeyID = view.findViewById(R.id.dialog_extPininputOnlinePinKeyEditKeyID);
                        int onlinePinKeyKeyID = Integer.parseInt(etOnlinePinKeyKeyID.getText().toString());
                        EditText etOnlinePanKeyKeyID = view.findViewById(R.id.dialog_ext_pininput_online_panKey_keyID);
                        int onlinePanKeyKeyID = Integer.parseInt(etOnlinePanKeyKeyID.getText().toString());
                        SymmetricKey pinKey = new SymmetricKey();
                        pinKey.setKeyType(onlinePinKeyKeyType);
                        pinKey.setKeyID((byte) onlinePinKeyKeyID);
                        if(dukptFlag == 1) {
                            dukptFlag = 0;
                            pinKey.setKeyUsage(KeyUsage.DUKPT);
                        }

                        SymmetricKey panKey = new SymmetricKey();
                        panKey.setKeyID((byte) onlinePanKeyKeyID);
                        panKey.setKeyType(onlinePanKeyKeyType);

                        CipherPAN cipherPAN = new CipherPAN();
                        cipherPAN.setCipherPAN(ISOUtils.hex2byte(panData));
                        cipherPAN.setClearPANLen(16);
                        cipherPAN.setPANKey(panKey);

                        ExtPINEntryParameters extPINEntryParameters = new ExtPINEntryParameters();
                        extPINEntryParameters.setMaskLine(ExtPINMaskLine.LINE_4);
                        extPINEntryParameters.setMaxPINLen((byte) 6);
                        extPINEntryParameters.setDisplayMessages(new String[]{"Input online pin", null, null, null});
                        extPINEntryParameters.setAutoComplete(true);
                        if("AES".equals(spnOfflineKeyType.getSelectedItem())) {
                            extPINEntryParameters.setPINBlockMode(PINBlockMode.ISO9564_4);
                        }else {
                            extPINEntryParameters.setPINBlockMode(PINBlockMode.ISO9564_0);
                        }
                        try {
                            extPinInput.startOnlinePINEntry(pinKey, panData, 60, extPINEntryParameters, listener);
                        }catch (NSDKException e) {
                            showErrorMessage(e, context.getString(R.string.dialog_ext_tv_pin_pininput_online_pin));
                        }
                    }else if(rbOfflinePinInput.isChecked()) {
                        EditText etOfflinePan = view.findViewById(R.id.etPininputOfflinePanData);
                        String PAN = etOfflinePan.getText().toString();
                        EditText etOfflineKeyID = view.findViewById(R.id.etExtPininputOfflineEditKeyID);
                        int offlineEditKeyID = Integer.parseInt(etOfflineKeyID.getText().toString());
                        ExtOfflinePINParameters parameters = new ExtOfflinePINParameters();
                        swIsRandomProtectMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if(isChecked) {
                                    parameters.setRandomProtectMode(true);
                                }else {
                                    parameters.setRandomProtectMode(false);
                                }
                            }
                        });
                        parameters.setMaskLine(ExtPINMaskLine.LINE_4);
                        parameters.setMaxPINLen((byte) 6);
                        parameters.setAutoComplete(true);
                        if("AES".equals(spnOfflineKeyType.getSelectedItem().toString())) {
                            parameters.setPINBlockMode(PINBlockMode.ISO9564_4);
                        }else {
                            parameters.setPINBlockMode(PINBlockMode.ISO9564_0);
                        }
                        parameters.setDisplayMessages(new String[] {"Input offline PIN", null, null, null});

                        SymmetricKey symmetricKey = new SymmetricKey();
                        symmetricKey.setKeyType(offlineKeyType);
                        symmetricKey.setKeyID((byte) offlineEditKeyID);

                        try {
                            extPinInput.startOfflinePINEntry(symmetricKey, PAN, 60 , parameters, listener);
                        }catch (NSDKException e) {
                            showErrorMessage(e, context.getString(R.string.dialog_ext_tv_pin_pininput_offline_pin));
                        }
                    }
                }

            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.ext_pin_enhanced_pininput, functionid = INDEX_EXTPINPAD_ENHANCED_ONLINEPIN)
    private void startEnhancedOnlinePIN() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.ext_pin_enhanced_pininput), null, R.layout.dialog_ext_new_pin_entry, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View view) {
                Switch swIcCheckPresent = view.findViewById(R.id.sw_ext_checkIcPresent);
                boolean icCheckPresent = swIcCheckPresent.isChecked();
                try {
                    SymmetricKey pinKey = new SymmetricKey();
                    pinKey.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_WK_PIN);
                    pinKey.setKeyType(KeyType.DES);

                    SymmetricKey panKey = new SymmetricKey();
                    panKey.setKeyID(AppConfig.Keys.MKSK_AES_INDEX_WK_DATA);
                    panKey.setKeyType(KeyType.DES);
                    panKey.setKeyUsage(KeyUsage.DATA);

                    ExtendedCipherPAN cipherPan = new ExtendedCipherPAN();
                    cipherPan.setPANKey(panKey);
                    cipherPan.setClearPANLen(16);
                    cipherPan.setCipherPAN(ISOUtils.hex2byte("045155935EAEE27294056065FE1E08C8"));
                    cipherPan.setCipherType(CipherType.DES_ECB);
                    cipherPan.setIv(null);
                    cipherPan.setAdditionalData(null);
                    cipherPan.setPANKey(panKey);

                    ExtendedExtPINEntryParams parameter = new ExtendedExtPINEntryParams();
                    parameter.setMaxPINLen((byte) 6);
                    parameter.setMaskLine(ExtPINMaskLine.LINE_4);
                    parameter.setDisplayMessages(new String[]{"CNY", "$1307" , "Input online pin"});
                    parameter.setPINBlockMode(PINBlockMode.ISO9564_0);
                    parameter.setAutoComplete(true);
                    parameter.setPinLengthRange(new byte[] {4, 5, 6});
                    parameter.setPinMaskAlignment(Alignment.Right);
                    parameter.setPinMessageMode(PINMessageMode.TRANSACTION_TYPE_AMOUNT);
                    parameter.setMinLen(0);
                    parameter.setSessionType(SessionType.MASTER_SESSION);
                    parameter.setCheckIcPresent(icCheckPresent);
                    extPinInput.startOnlinePINEntry(pinKey, cipherPan,60, parameter, extendedListener);
                } catch (NSDKException e) {
                    showErrorMessage(e, context.getString(R.string.ext_pin_enhanced_pininput));
                }
            }
        });

    }

    @MethodGridEntity(btnnameid = R.string.ext_pin_verify_offline_pin, functionid = INDEX_EXTPINPAD_VERIFY_OFFLINE_PIN)
    private void verifyOfflinePIN() {
        ExtPINEntryParameters parameters = new ExtPINEntryParameters();
        parameters.setMaxPINLen((byte) 0x06);
        parameters.setDisplayMessages(new String[] {"Enter Offline PIN."});
        RSAKey rsaKey = new RSAKey();
        rsaKey.setExponent(ISOUtils.hex2byte("010203"));
        rsaKey.setModulus(ISOUtils.hex2byte("000102030405060708090A0B0C0D0E0F"));
        try {
            extPinInput.startOfflinePINEntry(rsaKey, 120, parameters, listener);
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }
    }

}
