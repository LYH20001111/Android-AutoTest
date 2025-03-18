package com.hudou.autotest.navigation;


import android.widget.Toast;

import com.hudou.autotest.MainActivity;
import com.hudou.autotest.R;
import com.hudou.autotest.annotation.Navigation;
import com.hudou.autotest.base.fragment.BaseFragment;
import com.hudou.autotest.customUI.dialog.DialogUtils;
import com.hudou.autotest.customUI.dialog.listener.NotifyDialogListener;
import com.hudou.autotest.databinding.ActivityMainBinding;

@Navigation(name = "Setting")
//@TestItemClass(clz = {TestItem2.class})
public class SettingFragment extends BaseFragment<ActivityMainBinding> {

    @Override
    public void onFragmentVisibility() {
        super.onFragmentVisibility();
        Toast.makeText(MainActivity.mContext, "Setting Selected", Toast.LENGTH_SHORT).show();
    }
}