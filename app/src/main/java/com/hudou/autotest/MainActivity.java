package com.hudou.autotest;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;

import com.hudou.autotest.base.activity.AutoTestMainActivity;
import com.hudou.autotest.navigation.PSFragment;
import com.hudou.autotest.navigation.SettingFragment;

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
    public void addNavFragment(List<Fragment> list) {
        list.add(new PSFragment());
        list.add(new SettingFragment());
        //list.add(new AutoTestSettingFragment());
    }
}