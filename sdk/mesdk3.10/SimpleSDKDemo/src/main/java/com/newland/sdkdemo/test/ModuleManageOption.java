package com.newland.sdkdemo.test;

import static android.content.Context.BIND_AUTO_CREATE;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.newland.basetest.annotation.FragmentAno;
import com.newland.basetest.annotation.MethodAno;
import com.newland.basetest.pinc.MessageTag;
import com.newland.basetest.pinc.MethodBean;
import com.newland.sdk.ModuleManage;
import com.newland.sdkdemo.FragmentBase;


@FragmentAno(name = "初始化", numId = 0)
public class ModuleManageOption extends FragmentBase {
    private ModuleManage moduleManage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @MethodAno(name = "初始化ModuleManage", numId = 0)
    public void initModuleManage(MethodBean bean) {
        initModuleManage();
    }

    @MethodAno(name = "释放ModuleManage", numId = 1)
    public void destroyModuleManage(MethodBean bean) {
        releaseModuleManage();
    }

    /**
     * 初始化新大陆MESDK的ModuleManage
     * 调用新大陆mesdk接口前，必现先做一次初始化
     */
    public void initModuleManage(){
        try {
            moduleManage = ModuleManage.getInstance();
            boolean result = moduleManage.init(getContext());
            if(result){
                showMessage("初始化成功",MessageTag.NORMAL);
                getMainActivity().setModuleManage(moduleManage);
            }else {
                showMessage("初始化失败",MessageTag.ERROR);
                getMainActivity().setModuleManage(null);
            }
        }catch (Exception e){
            e.printStackTrace();
            showMessage("初始化异常："+e,MessageTag.ERROR);
            getMainActivity().setModuleManage(null);
        }

    }

    /**
     * 释放新大陆MESDK的ModuleManage
     * 退出应用时，调用释放ModuleManage，释放资源
     */
    public void releaseModuleManage(){
        try {
            if(moduleManage==null){
                showMessage("未初始化，不必释放",MessageTag.ERROR);
                return;
            }
            moduleManage.destroy();
        }catch (Exception e){
            e.printStackTrace();
            showError(e);
        }finally {
            moduleManage=null;
            getMainActivity().setModuleManage(null);
        }
    }
}
