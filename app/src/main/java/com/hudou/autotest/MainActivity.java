package com.hudou.autotest;

import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.hudou.autotest.base.activity.BaseMainActivity;

import java.util.List;

public class MainActivity extends BaseMainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
    }

    @Override
    public void addNavFragment(List<Fragment> list) {
        list.add(new PSFragment());
        list.add(new SettingFragment());
    }
}