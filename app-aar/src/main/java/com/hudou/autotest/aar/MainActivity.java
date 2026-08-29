package com.hudou.autotest.aar;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.hudou.autotest.base.activity.AutoTestMainActivity;
import com.hudou.autotest.aar.navigation.PSFragment;
import com.hudou.autotest.aar.navigation.SettingFragment;

import java.util.List;

public class MainActivity extends AutoTestMainActivity {
    public static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        //setContentView(R.layout.activity_main);
    }

    @Override
    public void addNavigationFragment(List<Fragment> list) {
        //list.removeIf(fragment -> fragment instanceof HomeFragment);
        list.add(new PSFragment());
        list.add(new SettingFragment());
//        list.add(new AutoTestSettingFragment());
    }

    @Override
    public boolean isPhysicalKeyboard() {
        return super.isPhysicalKeyboard();
    }

}