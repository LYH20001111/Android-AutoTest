package com.hudou.autotest.fragment;

import com.hudou.autotest.base.fragment.BaseFragment;
import com.hudou.autotest.databinding.AutoTestBaseSettingFragmentBinding;
import com.hudou.autotest.util.SharedPreferencesUtil;


public class BaseSettingFragment extends BaseFragment<AutoTestBaseSettingFragmentBinding> {
    @Override
    public void initData() {
        super.initData();
        viewBinding.swDebug.setChecked(SharedPreferencesUtil.get(SharedPreferencesUtil.DEBUG_MODE, true));
        viewBinding.swDebug.setOnCheckedChangeListener((compoundButton, isChecked) -> SharedPreferencesUtil.save(SharedPreferencesUtil.DEBUG_MODE, isChecked));
    }
}
