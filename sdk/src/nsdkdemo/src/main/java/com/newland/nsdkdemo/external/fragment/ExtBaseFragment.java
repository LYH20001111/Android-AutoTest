package com.newland.nsdkdemo.external.fragment;

import android.content.Context;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.external.ExtNSDKModuleManagerImpl;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.fragment.BaseFragment;
import com.newland.nsdkdemo.common.utils.MessageTag;

public abstract class ExtBaseFragment extends BaseFragment {
    protected ExtNSDKModuleManagerImpl moduleManager;

    public ExtBaseFragment(Context context, LayoutMode layoutMode) {
        super(context, layoutMode);
        this.moduleManager = ExtNSDKModuleManagerImpl.getInstance();
    }

    @Override
    protected void showErrorMessage(Exception e, String operation) {
        if(e instanceof NSDKException) {
//            String errMsg = moduleManager.getErrMsg(((NSDKException)e).getCode());
            showMessage(String.format("Failed to %s: [%d]", operation, ((NSDKException)e).getCode()), MessageTag.ERROR);
            LogUtils.e("NSDK", String.format("%s, ErrCode: %d", operation, ((NSDKException)e).getCode()));
        }else {
            super.showErrorMessage(e, operation);
        }
    }
}
