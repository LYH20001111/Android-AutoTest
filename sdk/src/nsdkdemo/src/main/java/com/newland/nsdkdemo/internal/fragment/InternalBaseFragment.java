package com.newland.nsdkdemo.internal.fragment;

import android.content.Context;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.internal.NSDKModuleManagerImpl;
import com.newland.nsdk.core.external.ExtNSDKModuleManagerImpl;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.fragment.BaseFragment;
import com.newland.nsdkdemo.common.utils.MessageTag;

public abstract class InternalBaseFragment extends BaseFragment {
    protected NSDKModuleManagerImpl moduleManager;

    public InternalBaseFragment(Context context, LayoutMode layoutMode) {
        super(context, layoutMode);
        this.moduleManager = NSDKModuleManagerImpl.getInstance();
    }

    @Override
    protected void showErrorMessage(Exception e, String operation) {
        if(e instanceof NSDKException) {
            String errMsg = moduleManager.getErrMsg(((NSDKException)e).getCode());
            showMessage(String.format("Failed to %s: [%d]%s", operation, ((NSDKException)e).getCode(), errMsg), MessageTag.ERROR);
            LogUtils.e("NSDK", String.format("%s, ErrCode: %d. ErrMsg: %s", operation, ((NSDKException)e).getCode(), errMsg));
        }else {
            super.showErrorMessage(e, operation);
        }

    }
}
