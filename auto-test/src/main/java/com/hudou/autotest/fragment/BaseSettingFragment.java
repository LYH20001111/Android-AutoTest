package com.hudou.autotest.fragment;

import android.content.DialogInterface;
import android.view.View;

import com.hudou.autotest.R;
import com.hudou.autotest.base.fragment.BaseFragment;
import com.hudou.autotest.customUI.dialog.DialogUtils;
import com.hudou.autotest.databinding.AutoTestBaseSettingFragmentBinding;
import com.hudou.autotest.fragment.listener.SettingInterface;
import com.hudou.autotest.util.SharedPreferencesUtil;
import com.hudou.autotest.listener.MyOnClickListener;


public class BaseSettingFragment extends BaseFragment<AutoTestBaseSettingFragmentBinding> implements SettingInterface {

    @Override
    public void onAddActions() {

    }

    public enum SettingFunction{
        BASE_FUNCTION,
        DEBUG_MODE,
        EXPORT_REPORT,
    }

    @Override
    public void onInitData() {
        super.onInitData();
        dealDebugMode();
        dealExportReport();
    }

    @Override
    public void onActionAfterInitData() {
        super.onActionAfterInitData();
        onAddActions();
    }

    private void dealDebugMode(){
        viewBinding.swDebug.setChecked(SharedPreferencesUtil.get(SharedPreferencesUtil.DEBUG_MODE, true));
        viewBinding.swDebug.setOnCheckedChangeListener((compoundButton, isChecked) -> SharedPreferencesUtil.save(SharedPreferencesUtil.DEBUG_MODE, isChecked));
    }

    private void dealExportReport(){
        viewBinding.llExportReport.setOnClickListener(new MyOnClickListener() {

            @Override
            public void dealClick(View v) {
                DialogUtils.createSingleChoiceDialog(getActivity(), R.string.select_report_format_dialog_title, new String[]{".xlsx", ".txt"}, id -> {
                    if (id == 0){

                    }else {

                    }
                });
            }
        });
    }

    public void removeFunction(SettingFunction settingFunction){
        switch (settingFunction){
            case BASE_FUNCTION:
                viewBinding.llBaseFunction.setVisibility(View.GONE);
                break;
            case DEBUG_MODE:
                viewBinding.llDebugMode.setVisibility(View.GONE);
                break;
            case EXPORT_REPORT:
                viewBinding.llExportReport.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }


}
