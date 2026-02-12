package com.newland.nsdkdemo.internal.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.keymanager.KeyInfoID;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.api.common.keymanager.KeyUsage;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.internal.devicemanager.DeviceInfo;
import com.newland.nsdk.core.api.internal.devicemanager.DeviceManager;
import com.newland.nsdk.core.api.internal.keymanager.KeyManager;
import com.newland.nsdk.core.api.internal.pinentry.PINEntry2;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.AppConfig;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.annotation.MethodGridEntity;
import com.newland.nsdkdemo.common.utils.DialogUtils;
import com.newland.nsdkdemo.common.utils.MessageTag;
import com.newland.nsdkdemo.internal.activity.PINEntry2KeyboardActivity;
import com.newland.nsdkdemo.internal.activity.PINEntry2SecondDisplayActivity;

public class PINEntry2Fragment extends InternalBaseFragment {

    private static final String TAG = "PINEntry2Fragment";
    private PINEntry2 mPInEntry2;
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

    private static final int NORMAL_PINPAD = 0;
    private static final int ADA_PINPAD = 1;
    private static final int RNIB_PINPAD = 2;
    private static final int FRENCHSYS_B3_PINPAD = 3;
    private static final int SIBS_PINPAD = 4;

    public PINEntry2Fragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_pinInput2_f);
    }

    @Override
    public void initData() {
        mPInEntry2 = (PINEntry2) moduleManager.getModule(ModuleType.PIN_ENTRY_2);
        keyManager = (KeyManager) moduleManager.getModule(ModuleType.KEY_MANAGER);
        deviceManager = (DeviceManager) moduleManager.getModule(ModuleType.DEVICE_MANAGER);
        sharedPreferences = context.getSharedPreferences("PINENTRY2", Context.MODE_PRIVATE);
        mEditor = sharedPreferences.edit();
    }

    @Override
    public Object getModule() {
        return PINEntry2Fragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_pininput_online, functionid = 1)
    private void startOnlinePin() {
        DialogUtils.createCustomDialog(context, R.string.tv_pin_pininput_online, null, R.layout.dialog_pinentry2, new DialogUtils.CustomDialogCallback2() {

            @Override
            public void onInit(View view) {
                Spinner spnKeyboardStyle = view.findViewById(R.id.spn_keyboard_style);
                spnKeyboardStyle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.PINENTRY_KEYBOARD_STYLE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int keyboardStyle = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.PINENTRY_KEYBOARD_STYLE, 0);
                spnKeyboardStyle.setSelection(keyboardStyle);

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
                Switch autoComplete = view.findViewById(R.id.switch_auto_complete);
                autoComplete.setChecked(sharedPreferences.getBoolean(AppConfig.SharedPreferenceConfig.PINENTRY_AUTO_COMPLETE, false));
                autoComplete.setOnCheckedChangeListener(((buttonView, isChecked) -> {
                    mEditor.putBoolean(AppConfig.SharedPreferenceConfig.PINENTRY_AUTO_COMPLETE, isChecked);
                    mEditor.commit();
                }));

                EditText etPanData = view.findViewById(R.id.et_pin_pininput_panData);
                etPanData.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.PINENTRY_PAN_DATA, "6217001820027645666"));
            }

            @Override
            public void onResult(int id, View view) {
                Spinner spnKeyboardStyle = view.findViewById(R.id.spn_keyboard_style);
                Switch swCheckIcPresent = view.findViewById(R.id.switch_check_ic_present);
                Switch swCheckPINRange = view.findViewById(R.id.switch_check_pin_range);
                Switch swEnableCustomFunctionKey = view.findViewById(R.id.switch_enable_custom_key);
                Switch autoComplete = view.findViewById(R.id.switch_auto_complete);
                EditText etPininputPanData = view.findViewById(R.id.et_pin_pininput_panData);
                int keyboardStyle = spnKeyboardStyle.getSelectedItemPosition();
                boolean isCheckIcPresent = swCheckIcPresent.isChecked();
                boolean isCheckPINRange = swCheckPINRange.isChecked();
                boolean isEnableCustomFunctionKey = swEnableCustomFunctionKey.isChecked();
                boolean isAutoComplete = autoComplete.isChecked();
                String PAN = etPininputPanData.getText().toString();
                mEditor.putString("PAN", PAN);
                mEditor.commit();
                LogUtils.i(TAG, "keyboardStyle:" + keyboardStyle + " isCheckIcPresent:" + isCheckIcPresent + " isCheckPINRange:" + isCheckPINRange + " isEnableCustomFunctionKey:" + isEnableCustomFunctionKey);
                if (keyboardStyle == NORMAL_PINPAD) {
                    keyUsage = KeyUsage.PIN;
                    keyType = KeyType.DES;
                    Key_Type = KEY_TYPE_DES;
                    isRandomLayout = true;
                    title = context.getString(R.string.input_pin_online_keyboard_normal);
                } else if (keyboardStyle == ADA_PINPAD) {
                    keyUsage = KeyUsage.PIN;
                    keyType = KeyType.DES;
                    Key_Type = KEY_TYPE_DES;
                    isRandomLayout = false;
                    title = context.getString(R.string.input_pin_online_keyboard_ada);
                } else if (keyboardStyle == RNIB_PINPAD) {
                    keyUsage = KeyUsage.PIN;
                    keyType = KeyType.DES;
                    Key_Type = KEY_TYPE_DES;
                    isRandomLayout = false;
                    title = context.getString(R.string.input_pin_online_keyboard_rnib);
                } else if (keyboardStyle == FRENCHSYS_B3_PINPAD) {
                    keyUsage = KeyUsage.PIN;
                    keyType = KeyType.DES;
                    Key_Type = KEY_TYPE_DES;
                    isRandomLayout = false;
                    title = context.getString(R.string.input_pin_online_keyboard_b3);
                } else if (keyboardStyle == SIBS_PINPAD) {
                    keyUsage = KeyUsage.PIN;
                    keyType = KeyType.DES;
                    Key_Type = KEY_TYPE_DES;
                    isRandomLayout = false;
                    title = context.getString(R.string.input_pin_online_keyboard_sibs);
                }
                keyID = AppConfig.Keys.MKSK_DES_INDEX_WK_PIN;
                SymmetricKey pinKey = new SymmetricKey();
                pinKey.setKeyUsage(keyUsage);
                pinKey.setKeyType(keyType);
                pinKey.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_WK_PIN);
                try {
                    keyManager.getKeyInfo(KeyInfoID.KCV, pinKey);
                } catch (NSDKException e) {
                    showErrorMessage(e, context.getString(R.string.dialog_tv_pin_pininput_test_item));
                    showMessage("Please load keys first", MessageTag.ERROR);
                    return;
                }
                selectScreen(Key_Type, isRandomLayout, title, PAN, keyID, keyboardStyle, isCheckIcPresent, isCheckPINRange, isEnableCustomFunctionKey, isAutoComplete);
            }
        });
    }

    public void selectScreen(int keyType, Boolean isRandomLayout, String moduleItem, String PAN, byte keyID, int keyboardStyle, boolean isCheckIcPresent, boolean isCheckPINRange, boolean isEnableCustomFunctionKey, boolean isAutoComplete) {
        try {
            DeviceInfo deviceInfo = deviceManager.getDeviceInfo();
            String model = deviceInfo.getDeviceModel();
            if ("X800".equalsIgnoreCase(model)) {
                Intent intent = new Intent(context, PINEntry2SecondDisplayActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isRandomLayout", isRandomLayout);
                bundle.putInt("keyType", keyType);
                bundle.putBoolean("isX800", true);
                bundle.putString("moduleItem", moduleItem);
                bundle.putByte("KeyID", keyID);

                bundle.putInt("KeyboardStyle", keyboardStyle);
                bundle.putBoolean("CheckIcPresent", isCheckIcPresent);
                bundle.putBoolean("CheckPINRange", isCheckPINRange);
                bundle.putBoolean("EnableCustomFunctionKey", isEnableCustomFunctionKey);
                bundle.putBoolean("AutoComplete", isAutoComplete);
                bundle.putString("PAN", PAN);
                intent.putExtras(bundle);
                context.startActivity(intent);

            } else {
                Intent intent = new Intent(context, PINEntry2KeyboardActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isRandomLayout", isRandomLayout);
                bundle.putInt("keyType", keyType);
                bundle.putBoolean("isX800", false);
                bundle.putByte("KeyID", keyID);
                bundle.putBoolean("isVirtualPINPad", isVirtualPINPad());

                bundle.putInt("KeyboardStyle", keyboardStyle);
                bundle.putBoolean("CheckIcPresent", isCheckIcPresent);
                bundle.putBoolean("CheckPINRange", isCheckPINRange);
                bundle.putBoolean("EnableCustomFunctionKey", isEnableCustomFunctionKey);
                bundle.putBoolean("AutoComplete", isAutoComplete);
                bundle.putString("PAN", PAN);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            showMessage(e.toString(), 1);
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
