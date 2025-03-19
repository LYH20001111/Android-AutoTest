package com.hudou.autotest.navigation;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hudou.autotest.MainActivity;
import com.hudou.autotest.R;
import com.hudou.autotest.annotation.Navigation;
import com.hudou.autotest.base.fragment.BaseFragment;
import com.hudou.autotest.customUI.dialog.DialogUtils;
import com.hudou.autotest.customUI.dialog.listener.NotifyDialogListener;
import com.hudou.autotest.databinding.ActivityMainBinding;
import com.hudou.autotest.fragment.BaseSettingFragment;

@Navigation(name = "Setting")
//@TestItemClass(clz = {TestItem2.class})
public class SettingFragment extends BaseSettingFragment {


    @Override
    public void onFragmentVisibility() {
        super.onFragmentVisibility();
        Toast.makeText(MainActivity.mContext, "Setting Selected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddActions() {
        super.onAddActions();
    }
}