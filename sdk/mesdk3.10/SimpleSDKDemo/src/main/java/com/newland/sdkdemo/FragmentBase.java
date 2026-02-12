package com.newland.sdkdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import com.newland.basetest.base2.BaseFragment2;
import com.newland.sdk.ModuleManage;

public class FragmentBase extends BaseFragment2 {

    public MainActivity getMainActivity(){
        return (MainActivity)getActivity();
    }

    public ModuleManage getModuleManage(){
        return getMainActivity().getModuleManage();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("onCreate",this.getClass().getName());
        super.onCreate(savedInstanceState);
    }

}
