package com.newland.nsdkdemo.external.fragment;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.external.setting.ExtSettings;
import com.newland.nsdk.core.api.external.setting.ExtSettingsManager;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.annotation.MethodGridEntity;
import com.newland.nsdkdemo.common.utils.DialogUtils;
import com.newland.nsdkdemo.common.utils.EnumUtils;

import java.util.Locale;

public class ExtSettingsManagerFragment extends ExtBaseFragment {

    private ExtSettingsManager mSettingsManager;

    private static final int INDEX_GET_PROPERTY = 1;

    private static final int INDEX_SET_PROPERTY = 2;

    public ExtSettingsManagerFragment(Context context) {
        super(context, LayoutMode.GRID);
    }


    @Override
    public String title() {
        return "ExtSettingsManager";
    }

    @Override
    public void initData() {
        mSettingsManager = (ExtSettingsManager) moduleManager.getModule(ModuleType.EXT_SETTING);
    }

    @Override
    public Object getModule() {
        return ExtSettingsManagerFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.ext_settings_get_property, functionid = INDEX_GET_PROPERTY)
    private void getProperty() {
        DialogUtils.createCustomDialog(context, R.string.ext_settings_get_property, null, R.layout.dialog_ext_setting_set_get_property, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                LinearLayout llSetPropertyParams = view.findViewById(R.id.linear_extSettings_setPropertyParams);
                llSetPropertyParams.setVisibility(View.GONE);
            }

            @Override
            public void onResult(int id, View view) {
                Spinner spnPropertyKey = view.findViewById(R.id.spn_extSetting_getPropertyKey);
                String key = EnumUtils.getPropertyKey(spnPropertyKey.getSelectedItem().toString());
                try {
                    String value = mSettingsManager.get(key);
                    showMessage(String.format(Locale.US, "Get Property %s value:%s", key, value));
                } catch (NSDKException e) {
                    showErrorMessage(e, context.getString(R.string.ext_settings_get_property));
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.ext_settings_set_property, functionid = INDEX_SET_PROPERTY)
    private void setProperty() {
        DialogUtils.createCustomDialog(context, R.string.ext_settings_set_property, null, R.layout.dialog_ext_setting_set_get_property, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                LinearLayout llGetPropertyParams = view.findViewById(R.id.linear_extSettings_getPropertyParams);
                llGetPropertyParams.setVisibility(View.GONE);
            }

            @Override
            public void onResult(int id, View view) {
                Spinner spnKey = view.findViewById(R.id.spn_extSetting_setPropertyKey);
                String key = EnumUtils.getPropertyKey(spnKey.getSelectedItem().toString());
                EditText editValue = view.findViewById(R.id.edit_extSettings_setPropertyValue);
                String value = editValue.getText().toString();
                try {
                    mSettingsManager.set(key, value);
                    showMessage(String.format(Locale.US, "Set Property value %s to %s success", value, key));
                } catch (NSDKException e) {
                    showErrorMessage(e, context.getString(R.string.ext_settings_set_property));
                }
            }
        });
        try {
            String value = "1";
            mSettingsManager.set(ExtSettings.SYS_LED_COLOR, value);
            showMessage("Set SYS_LED_COLOR " + value + " Success.");
        } catch (Exception e) {
            showErrorMessage(e, "Set Property Error");
        }
    }

}
