package com.newland.nsdkdemo.internal.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.keymanager.KeyInfoID;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.api.common.keymanager.KeyUsage;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.common.pinentry.PINBlockMode;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.internal.devicemanager.DeviceInfo;
import com.newland.nsdk.core.api.internal.devicemanager.DeviceManager;
import com.newland.nsdk.core.api.internal.keymanager.KeyManager;
import com.newland.nsdk.core.api.internal.pinentry.PINConvertMode;
import com.newland.nsdk.core.api.internal.pinentry.PINConvertParameters;
import com.newland.nsdk.core.api.internal.pinentry.PINEntry;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.AppConfig;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.annotation.MethodGridEntity;
import com.newland.nsdkdemo.common.utils.DialogUtils;
import com.newland.nsdkdemo.common.utils.MessageTag;
import com.newland.nsdkdemo.internal.activity.KeyBoardNumberActivity;
import com.newland.nsdkdemo.internal.activity.SecondDisplayActivity;
import com.newland.nsdkdemo.internal.activity.SecondDisplayRNIBActivity;

public class PINEntryFragment extends InternalBaseFragment {

    private PINEntry mPINEntry;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor mEditor;
    private KeyManager keyManager;
    private DeviceManager deviceManager;
    private KeyUsage keyUsage;
    private KeyType keyType;
    private byte keyID;
    private int Key_Type;
    private boolean isRandomLayout;
    private String title;

    public static final int KEY_TYPE_DES = 0;
    public static final int KEY_TYPE_AES = 1;
    public static final int KEY_TYPE_DUKPT = 2;
    public static final int KEY_TYPE_AES_DUKPT = 3;
    public PINEntryFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_pinInput_f);
    }

    @Override
    public void initData() {
        mPINEntry = (PINEntry) moduleManager.getModule(ModuleType.PIN_ENTRY);
        keyManager = (KeyManager) moduleManager.getModule(ModuleType.KEY_MANAGER);
        deviceManager = (DeviceManager) moduleManager.getModule(ModuleType.DEVICE_MANAGER);
        sharedPreferences = context.getSharedPreferences("PINENTRY", Context.MODE_PRIVATE);
        mEditor = sharedPreferences.edit();
    }

    @Override
    public Object getModule() {
        return PINEntryFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_pininput_online, functionid = 1)
    private void startOnline() {
        SymmetricKey pinKey = new SymmetricKey();
        pinKey.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_WK_PIN);
        pinKey.setKeyType(KeyType.DES);
        pinKey.setKeyUsage(KeyUsage.PIN);

        try {
            keyManager.getKeyInfo(KeyInfoID.KCV, pinKey);
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, context.getString(R.string.tv_pin_pininput_online));
            showMessage("Please load PIN key first.", MessageTag.ERROR);
            return;
        }
       selectScreen(KEY_TYPE_DES, true, context.getResources().getString(R.string.tv_pin_pininput_online), "6212261402009762466", (byte) AppConfig.Keys.MKSK_DES_INDEX_WK_PIN, R.layout.input_pin_fragment, false, false);
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_pininput_online_normal, functionid = 2)
    private void startOnlineNormalPINPad() {

        SymmetricKey pinKey = new SymmetricKey();
        pinKey.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_WK_PIN);
        pinKey.setKeyType(KeyType.DES);
        pinKey.setKeyUsage(KeyUsage.PIN);

        try {
            keyManager.getKeyInfo(KeyInfoID.KCV, pinKey);
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, context.getString(R.string.tv_pin_pininput_online_normal));
            showMessage("Please load PIN key first.", MessageTag.ERROR);
            return;
        }
        selectScreen(KEY_TYPE_DES, false,context.getResources().getString(R.string.tv_pin_pininput_online_normal), "6212261402009762466", R.layout.input_pin_fragment, false);
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_pininput_online_aes, functionid = 3)
    private void startAESOnline() {
        SymmetricKey pinKey = new SymmetricKey();
        pinKey.setKeyID(AppConfig.Keys.MKSK_AES_INDEX_WK_PIN);
        pinKey.setKeyType(KeyType.AES);
        pinKey.setKeyUsage(KeyUsage.PIN);

        try {
            keyManager.getKeyInfo(KeyInfoID.KCV, pinKey);
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, context.getString(R.string.tv_pin_pininput_online_aes));
            showMessage("Please load PIN key first.", MessageTag.ERROR);
            return;
        }

        selectScreen(KEY_TYPE_AES, true, context.getResources().getString(R.string.tv_pin_pininput_online_aes), "6212261402009762466", R.layout.input_pin_fragment, false);
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_pininput_online_dukpt, functionid = 4)
    private void startDUKPTOnline() {
        SymmetricKey pinKey = new SymmetricKey();
        pinKey.setKeyID(AppConfig.Keys.DUKPT_DES_INDEX);
        pinKey.setKeyType(KeyType.DES);
        pinKey.setKeyUsage(KeyUsage.DUKPT);

        try {
            keyManager.getKeyInfo(KeyInfoID.KCV, pinKey);
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, context.getString(R.string.tv_pin_pininput_online_dukpt));
            showMessage("Please load PIN key first.", MessageTag.ERROR);
            return;
        }
        selectScreen(KEY_TYPE_DUKPT, true, context.getResources().getString(R.string.tv_pin_pininput_online_dukpt), "6212261402009762466", R.layout.input_pin_fragment, false);
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_pininput_online_aes_dukpt, functionid = 5)
    private void startAESDUKPTOnline() {
        SymmetricKey pinKey = new SymmetricKey();
        pinKey.setKeyID(AppConfig.Keys.DUKPT_AES_INDEX);
        pinKey.setKeyType(KeyType.AES);
        pinKey.setKeyUsage(KeyUsage.DUKPT);

        try {
            keyManager.getKeyInfo(KeyInfoID.KCV, pinKey);
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, context.getString(R.string.tv_pin_pininput_online_aes_dukpt));
            showMessage("Please load AES DUKPT key first.", MessageTag.ERROR);
            return;
        }

        selectScreen(KEY_TYPE_AES_DUKPT, true, context.getResources().getString(R.string.tv_pin_pininput_online_aes_dukpt), "6212261402009762466", R.layout.input_pin_fragment, false);
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_pininput_online_start, functionid = 6)
    private void startOnlinePin() {
        DialogUtils.createCustomDialog(context, R.string.tv_pin_pininput_online_start, null, R.layout.dialog_pinentry, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                Switch swDefaultCasesKeyID = view.findViewById(R.id.switch_default_key_id_btn);
                swDefaultCasesKeyID.setChecked(true);
                LinearLayout llPininputEditKeyIDParams = view.findViewById(R.id.linear_pininput_editKeyID_params);
                llPininputEditKeyIDParams.setVisibility(View.GONE);
                swDefaultCasesKeyID.setOnCheckedChangeListener((buttonView, isChecked)-> {
                    if(isChecked) {
                        llPininputEditKeyIDParams.setVisibility(View.GONE);
                    } else {
                        llPininputEditKeyIDParams.setVisibility(View.VISIBLE);
                    }
                });
                EditText etPanData = view.findViewById(R.id.et_pin_pininput_panData);
                etPanData.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.PINENTRY_PAN_DATA,""));
                RadioButton rbPininputOnline = view.findViewById(R.id.pin_pininput_online_radio);
                rbPininputOnline.setChecked(true);
                EditText etPininputKeyID = view.findViewById(R.id.et_pin_pininput_keyID);
                etPininputKeyID.setText(String.valueOf(sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.PINENTRY_KEY_ID, 0)));
                Switch swCheckIcPresent = view.findViewById(R.id.switch_check_ic_present);
                swCheckIcPresent.setChecked(sharedPreferences.getBoolean(AppConfig.SharedPreferenceConfig.PINENTRY_CHECK_IC_PRESENT, false));
                swCheckIcPresent.setOnCheckedChangeListener((buttonView, isChecked) -> {
                   mEditor.putBoolean(AppConfig.SharedPreferenceConfig.PINENTRY_CHECK_IC_PRESENT, isChecked);
                   mEditor.commit();
                });
                Switch swCheckPinRange = view.findViewById(R.id.switch_check_pin_range);
                swCheckPinRange.setChecked(sharedPreferences.getBoolean(AppConfig.SharedPreferenceConfig.PINENTRY_CHECK_PIN_RANGE, false));
                swCheckPinRange.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    mEditor.putBoolean(AppConfig.SharedPreferenceConfig.PINENTRY_CHECK_PIN_RANGE, isChecked);
                    mEditor.commit();
                });
                Switch swEnableCustomFunctionKey = view.findViewById(R.id.switch_enable_custom_key);
                swEnableCustomFunctionKey.setChecked(sharedPreferences.getBoolean(AppConfig.SharedPreferenceConfig.PINENTRY_ENABLE_CUSTOM_FUNCTION_KEY, false));
                swEnableCustomFunctionKey.setOnCheckedChangeListener(((buttonView, isChecked) -> {
                    mEditor.putBoolean(AppConfig.SharedPreferenceConfig.PINENTRY_ENABLE_CUSTOM_FUNCTION_KEY, isChecked);
                    mEditor.commit();
                }));

            }

            @Override
            public void onResult(int id, View view) {
                Switch swDefaultCasesKeyID = view.findViewById(R.id.switch_default_key_id_btn);
                Switch swCheckIcPresent = view.findViewById(R.id.switch_check_ic_present);
                Switch swCheckPINRange = view.findViewById(R.id.switch_check_pin_range);
                Switch swEnableCustomFunctionKey = view.findViewById(R.id.switch_enable_custom_key);
                EditText etPininputKeyID = view.findViewById(R.id.et_pin_pininput_keyID);
                RadioButton rbPininputOnline = view.findViewById(R.id.pin_pininput_online_radio);
                RadioButton rbPininoutOnline_Normal = view.findViewById(R.id.pin_pininput_online_normal_radio);
                RadioButton rbPininputOnline_AES = view.findViewById(R.id.pin_pininput_online_aes_radio);
                RadioButton rbPininputOnline_DUKPT = view.findViewById(R.id.pin_pininput_online_dukpt_radio);
                RadioButton rbPininputOnline_AES_DUKPT = view.findViewById(R.id.pin_pininput_online_aes_dukpt_radio);
                EditText etPininputPanData = view.findViewById(R.id.et_pin_pininput_panData);
                String PAN = etPininputPanData.getText().toString();
                boolean isCheckIcPresent = swCheckIcPresent.isChecked();
                boolean isCheckPINRange = swCheckPINRange.isChecked();
                boolean isEnableCustomFunctionKey = swEnableCustomFunctionKey.isChecked();
                mEditor.putString("PAN", PAN);
                mEditor.commit();
                if(rbPininputOnline.isChecked()) {
                    keyUsage = KeyUsage.PIN;
                    keyType = KeyType.DES;
                    Key_Type = KEY_TYPE_DES;
                    isRandomLayout = true;
                    title = context.getString(R.string.tv_pin_pininput_online);

                }else if(rbPininoutOnline_Normal.isChecked()) {
                    keyUsage = KeyUsage.PIN;
                    keyType = KeyType.DES;
                    Key_Type = KEY_TYPE_DES;
                    isRandomLayout = false;
                    title = context.getString(R.string.tv_pin_pininput_online_normal);

                }else if(rbPininputOnline_AES.isChecked()) {
                    keyUsage = KeyUsage.PIN;
                    keyType = KeyType.AES;
                    Key_Type = KEY_TYPE_AES;
                    isRandomLayout = true;
                    title = context.getString(R.string.tv_pin_pininput_online_aes);
                }else if(rbPininputOnline_DUKPT.isChecked()) {
                    keyUsage = KeyUsage.DUKPT;
                    keyType = KeyType.DES;
                    keyID = AppConfig.Keys.DUKPT_DES_INDEX;
                    Key_Type = KEY_TYPE_DUKPT;
                    isRandomLayout = true;
                    title = context.getString(R.string.tv_pin_pininput_online_dukpt);
                }else if(rbPininputOnline_AES_DUKPT.isChecked()) {
                    keyUsage = KeyUsage.DUKPT;
                    keyType = KeyType.AES;
                    Key_Type = KEY_TYPE_AES_DUKPT;
                    isRandomLayout = true;
                    title = context.getString(R.string.tv_pin_pininput_online_aes_dukpt);
                }
                if(swDefaultCasesKeyID.isChecked()) {
                    byte defaultKeyID = 0;
                    if(rbPininputOnline.isChecked() || rbPininoutOnline_Normal.isChecked()) {
                        defaultKeyID = AppConfig.Keys.MKSK_DES_INDEX_WK_PIN;
                    } else if(rbPininputOnline_AES.isChecked()) {
                        defaultKeyID = AppConfig.Keys.MKSK_AES_INDEX_WK_PIN;
                    } else if(rbPininputOnline_DUKPT.isChecked()) {
                        defaultKeyID = AppConfig.Keys.DUKPT_DES_INDEX;
                    } else if(rbPininputOnline_AES_DUKPT.isChecked()) {
                        defaultKeyID = AppConfig.Keys.DUKPT_AES_INDEX;
                    }
                    SymmetricKey pinKey = new SymmetricKey();
                    pinKey.setKeyUsage(keyUsage);
                    pinKey.setKeyType(keyType);
                    pinKey.setKeyID(defaultKeyID);
                    try {
                        keyManager.getKeyInfo(KeyInfoID.KCV, pinKey);
                    }catch (NSDKException e) {
                        showErrorMessage(e, context.getString(R.string.dialog_tv_pin_pininput_test_item));
                        showMessage("Please load keys first", MessageTag.ERROR);
                        return;
                    }
                    selectScreen(Key_Type, isRandomLayout, title, PAN, defaultKeyID, R.layout.input_pin_fragment, isCheckIcPresent, isCheckPINRange, isEnableCustomFunctionKey);
                }else {
                    keyID = (byte)Integer.parseInt(etPininputKeyID.getText().toString());
                    mEditor.putInt(AppConfig.SharedPreferenceConfig.PINENTRY_KEY_ID, keyID);
                    mEditor.commit();
                    if(String.valueOf(keyID).equals("") || String.valueOf(keyID) == null)
                    {
                        showMessage(R.string.input_params_detect, MessageTag.ERROR);
                        return;
                    }
                    SymmetricKey pinKey = new SymmetricKey();
                    pinKey.setKeyUsage(keyUsage);
                    pinKey.setKeyType(keyType);
                    pinKey.setKeyID(keyID);
                    try {
                        keyManager.getKeyInfo(KeyInfoID.KCV, pinKey);
                    }catch (NSDKException e) {
                        showErrorMessage(e, context.getString(R.string.dialog_tv_pin_pininput_test_item));
                        showMessage("Please load keys first", MessageTag.ERROR);
                        return;
                    }
                    selectScreen(Key_Type, isRandomLayout, title, PAN, keyID, R.layout.input_pin_fragment, isCheckIcPresent, isCheckPINRange, isEnableCustomFunctionKey);
                }
            }
        });


    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_pininput_online_start_rnib, functionid = 7)
    private void startRNIBOnlinePIN() {
        DialogUtils.createCustomDialog(context, R.string.tv_pin_pininput_online_start_rnib, null, R.layout.dialog_pinentry, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                Switch swUseDefaultKeyID = view.findViewById(R.id.switch_default_key_id_btn);
                swUseDefaultKeyID.setVisibility(View.GONE);
                Switch swEnableCustomFunctionKey = view.findViewById(R.id.switch_enable_custom_key);
                swEnableCustomFunctionKey.setVisibility(View.GONE);
                RadioGroup rgPINEntryType = view.findViewById(R.id.rg_pinentry_type);
                rgPINEntryType.setVisibility(View.GONE);
                LinearLayout llEditKeyIdParams = view.findViewById(R.id.linear_pininput_editKeyID_params);
                llEditKeyIdParams.setVisibility(View.GONE);
            }

            @Override
            public void onResult(int id, View view) {
                Switch swCheckIcPresent = view.findViewById(R.id.switch_check_ic_present);
                boolean isCheckIcPresent = swCheckIcPresent.isChecked();
                Switch swCheckPINRange = view.findViewById(R.id.switch_check_pin_range);
                boolean isCheckPINRange = swCheckPINRange.isChecked();
                EditText editPan = view.findViewById(R.id.et_pin_pininput_panData);
                String PAN = TextUtils.isEmpty(editPan.getText().toString()) ? "6212261402009762466" : editPan.getText().toString();
                SymmetricKey pinKey = new SymmetricKey();
                pinKey.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_WK_PIN);
                pinKey.setKeyType(KeyType.DES);
                pinKey.setKeyUsage(KeyUsage.PIN);

                try {
                    keyManager.getKeyInfo(KeyInfoID.KCV, pinKey);
                } catch (NSDKException e) {
                    e.printStackTrace();
                    showErrorMessage(e, context.getString(R.string.tv_pin_pininput_online));
                    showMessage("Please load PIN key first.", MessageTag.ERROR);
                    return;
                }
                if ("X800".equalsIgnoreCase(Build.MODEL)) {
                    selectRNIBHorizontal(KEY_TYPE_DES, true, context.getResources().getString(R.string.tv_pin_pininput_online), PAN, isCheckIcPresent, isCheckIcPresent);
                } else {
                    selectScreen(KEY_TYPE_DES, true, context.getResources().getString(R.string.tv_pin_pininput_online), PAN, AppConfig.Keys.MKSK_DES_INDEX_WK_PIN, R.layout.input_rnib_pin_fragment, isCheckIcPresent, isCheckPINRange);
                }
            }
        });


    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_pininput_convert_pin_block, functionid = 8)
    private void convertPINBlock() {
        SymmetricKey originKey = new SymmetricKey();
        originKey.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_WK_PIN);
        originKey.setKeyType(KeyType.DES);
        originKey.setKeyUsage(KeyUsage.PIN);
        SymmetricKey convertKey = new SymmetricKey();
        convertKey.setKeyID((byte) 50);
        convertKey.setKeyType(KeyType.DES);
        convertKey.setKeyUsage(KeyUsage.PIN);
        PINConvertParameters parameters = new PINConvertParameters();
        parameters.setPAN("6212261402009762466");
        parameters.setSessionPinBlockMode(PINBlockMode.ISO9564_0);
        parameters.setConvertPinBlockMode(PINBlockMode.ISO9564_0);
        parameters.setPinConvertMode(PINConvertMode.ONLY_CONVERT);
        byte[] pinblock = ISOUtils.hex2byte("5EDBE8FF00702135");

        try {
            byte[] convertData = mPINEntry.convertPINBlock(parameters, originKey, convertKey, pinblock);
            showMessage("Convert pin block: " + ISOUtils.hexString(convertData));
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }
    }

    public void selectScreen(int keyType, Boolean isRandomLayout, String moduleItem, String PAN, int id, boolean isCheckIcPresent) {
        try {
            DeviceInfo deviceInfo = deviceManager.getDeviceInfo();
            String model = deviceInfo.getDeviceModel();
            if("X800".equalsIgnoreCase(model)) {
                Intent intent = new Intent(context, SecondDisplayActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isRandomLayout", isRandomLayout);
                bundle.putInt("keyType", keyType);
                bundle.putBoolean("isX800", true);
                bundle.putString("moduleItem", moduleItem);
                bundle.putString("PAN", PAN);
                bundle.putInt("LayoutID", id);
                bundle.putBoolean("CheckIcPresent", isCheckIcPresent);
                intent.putExtras(bundle);
                context.startActivity(intent);

            }else {
                Intent intent = new Intent(context, KeyBoardNumberActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isRandomLayout", isRandomLayout);
                bundle.putInt("keyType", keyType);
                bundle.putBoolean("isX800", false);
                bundle.putBoolean("isVirtualPINPad", isVirtualPINPad());
                bundle.putString("PAN", PAN);
                bundle.putInt("LayoutID", id);
                bundle.putBoolean("CheckIcPresent", isCheckIcPresent);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        }catch (Exception e) {
            showMessage(e.toString(), 1);
        }
    }

    //Use for optional pinInput
    public void selectScreen(int keyType, Boolean isRandomLayout, String moduleItem, String PAN, byte keyID, int id, boolean isCheckIcPresent, boolean isCheckPINRange) {
        try {
            DeviceInfo deviceInfo = deviceManager.getDeviceInfo();
            String model = deviceInfo.getDeviceModel();
            if("X800".equalsIgnoreCase(model)) {
                Intent intent = new Intent(context, SecondDisplayActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isRandomLayout", isRandomLayout);
                bundle.putInt("keyType", keyType);
                bundle.putBoolean("isX800", true);
                bundle.putString("moduleItem", moduleItem);
                bundle.putString("PAN", PAN);
                bundle.putByte("KeyID", keyID);
                bundle.putInt("LayoutID", id);
                bundle.putBoolean("CheckIcPresent", isCheckIcPresent);
                bundle.putBoolean("CheckPINRange", isCheckPINRange);
                intent.putExtras(bundle);
                context.startActivity(intent);

            }else {
                Intent intent = new Intent(context, KeyBoardNumberActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isRandomLayout", isRandomLayout);
                bundle.putInt("keyType", keyType);
                bundle.putBoolean("isX800", false);
                bundle.putString("PAN", PAN);
                bundle.putByte("KeyID", keyID);
                bundle.putBoolean("isVirtualPINPad", isVirtualPINPad());
                bundle.putInt("LayoutID", id);
                bundle.putBoolean("CheckIcPresent", isCheckIcPresent);
                bundle.putBoolean("CheckPINRange", isCheckPINRange);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        }catch (Exception e) {
            showMessage(e.toString(), 1);
        }
    }

    public void selectScreen(int keyType, Boolean isRandomLayout, String moduleItem, String PAN, byte keyID, int id, boolean isCheckIcPresent, boolean isCheckPINRange, boolean isEnableCustomFunctionKey) {
        try {
            DeviceInfo deviceInfo = deviceManager.getDeviceInfo();
            String model = deviceInfo.getDeviceModel();
            if("X800".equalsIgnoreCase(model)) {
                Intent intent = new Intent(context, SecondDisplayActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isRandomLayout", isRandomLayout);
                bundle.putInt("keyType", keyType);
                bundle.putBoolean("isX800", true);
                bundle.putString("moduleItem", moduleItem);
                bundle.putString("PAN", PAN);
                bundle.putByte("KeyID", keyID);
                bundle.putInt("LayoutID", id);
                bundle.putBoolean("CheckIcPresent", isCheckIcPresent);
                bundle.putBoolean("CheckPINRange", isCheckPINRange);
                bundle.putBoolean("EnableCustomFunctionKey", isEnableCustomFunctionKey);
                intent.putExtras(bundle);
                context.startActivity(intent);

            }else {
                Intent intent = new Intent(context, KeyBoardNumberActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isRandomLayout", isRandomLayout);
                bundle.putInt("keyType", keyType);
                bundle.putBoolean("isX800", false);
                bundle.putString("PAN", PAN);
                bundle.putByte("KeyID", keyID);
                bundle.putBoolean("isVirtualPINPad", isVirtualPINPad());
                bundle.putInt("LayoutID", id);
                bundle.putBoolean("CheckIcPresent", isCheckIcPresent);
                bundle.putBoolean("CheckPINRange", isCheckPINRange);
                bundle.putBoolean("EnableCustomFunctionKey", isEnableCustomFunctionKey);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        }catch (Exception e) {
            showMessage(e.toString(), 1);
        }
    }

    public void selectRNIBHorizontal(int keyType, Boolean isRandomLayout, String moduleItem, String PAN, boolean isCheckIcPresent, boolean isCheckPINRange) {
        try {
            Intent intent = new Intent(context, SecondDisplayRNIBActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("isRandomLayout", isRandomLayout);
            bundle.putInt("keyType", keyType);
            bundle.putString("moduleItem", moduleItem);
            bundle.putString("PAN", PAN);
            bundle.putBoolean("CheckIcPresent", isCheckIcPresent);
            bundle.putBoolean("CheckPINRange", isCheckPINRange);
            intent.putExtras(bundle);
            context.startActivity(intent);
        }catch (Exception e) {
            showMessage(e.toString(), MessageTag.ERROR);
        }
    }

    private boolean isVirtualPINPad() {
        try {

            return !deviceManager.getDeviceInfo().isPhysicalKeyboard();
        } catch (NSDKException e) {
            e.printStackTrace();
            return false;
        }
    }




}
