package com.hudou.autotest;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.hudou.autotest.base.activity.AutoTestMainActivity;
import com.hudou.autotest.fragment.AutoTestSettingFragment;
import com.hudou.autotest.navigation.PSFragment;
import com.hudou.autotest.navigation.SettingFragment;
import com.newland.lib.ModuleManage;
import com.newland.serviceapi.cardreader.CardReaderListener;
import com.newland.serviceapi.cardreader.CardType;
import com.newland.serviceapi.cardreader.ICCardInfo;
import com.newland.serviceapi.cardreader.MagInfo;
import com.newland.serviceapi.cardreader.RFCardInfo;
import com.newland.serviceapi.cardreader.RFCardType;
import com.newland.serviceapi.deviceService.ChannelType;

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
//        list.add(new AutoTestSettingFragment());
    }

}