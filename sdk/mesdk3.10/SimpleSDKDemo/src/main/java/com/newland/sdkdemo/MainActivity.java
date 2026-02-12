package com.newland.sdkdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.newland.basetest.base2.BaseMainActivity2;
import com.newland.basetest.view.msgDialog.CommonDialog2;
import com.newland.basetest.view.msgDialog.MessageClick;
import com.newland.basetest.view.msgDialog.MessageInterface;
import com.newland.sdk.ModuleManage;

import java.util.List;


public class MainActivity extends BaseMainActivity2 {
    private ModuleManage moduleManage;
    private MessageInterface dialog;

    @Override
    public String getProjectTitle() {
        return "新大陆简易SDKDemo";
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void addFragments(List<Fragment> list) {
        autoAdd(list,"com.newland.sdkdemo.test");
    }


    @Override
    public void switchFragment(int position) {
        if(null==moduleManage && 0!=position){
            if(null==dialog){
                dialog = CommonDialog2.createConfirmDialog(this, "确定", new MessageClick() {
                    @Override
                    public void onclick(MessageInterface messageInterface, Bundle bundle) {
                        messageInterface.dismiss();
                        switchFragment(0);
                    }
                }).setTitle("ModuleManage").setMessage("ModuleManage未初始化，请先初始化");
            }
            dialog.show();
            return;
        }
        super.switchFragment(position);
    }

    public ModuleManage getModuleManage() {
        return moduleManage;
    }

    public void setModuleManage(ModuleManage moduleManage) {
        this.moduleManage = moduleManage;
    }
}
