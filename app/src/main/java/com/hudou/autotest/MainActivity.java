package com.hudou.autotest;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.hudou.autotest.base.activity.AutoTestMainActivity;
import com.hudou.autotest.fragment.AutoTestSettingFragment;
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
//        list.add(new AutoTestSettingFragment());
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == 1) {
//            if (grantResults.length > 0
//                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
//                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//
//            } else {
//                // 权限被拒绝，提示用户
//                Toast.makeText(this, "权限被拒绝，无法进行文件操作", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
}