package com.newland.nsdkdemo.external.fragment;

import android.content.Context;

import com.newland.nsdk.core.external.ExtNSDKModuleManagerImpl;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.fragment.BaseFragment;

public abstract class ExtBaseFragment extends BaseFragment {
    protected ExtNSDKModuleManagerImpl moduleManager;

    public ExtBaseFragment(Context context, LayoutMode layoutMode) {
        super(context, layoutMode);
        this.moduleManager = ExtNSDKModuleManagerImpl.getInstance();
    }
}
