package com.newland.nsdkdemo.internal.fragment;

import android.content.Context;
import android.util.Log;

import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.internal.jni.NSDKJni;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdk.core.api.internal.cashbox.CashBox;
import com.newland.nsdkdemo.common.annotation.MethodGridEntity;

import java.util.Locale;

public class CashBoxFragment extends InternalBaseFragment {

    private CashBox cashBox;

    public CashBoxFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_cashbox_f);
    }

    @Override
    public void initData() {
        cashBox = (CashBox) moduleManager.getModule(ModuleType.CASH_BOX);
    }

    @Override
    public Object getModule() {
        return CashBoxFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.cashbox_open, functionid = 0)
    private void openCashBox(){
        try {
            cashBox.open();
            showMessage("Open cash box with default voltage(12v) and allow to open again after 500ms.");
        }catch (NSDKException e){
            e.printStackTrace();
            showErrorMessage(e, "open cash box");
        }
    }

    @MethodGridEntity(btnnameid = R.string.cashbox_open_delay, functionid = 1)
    private void openCashBoxDlay(){
        try {
            cashBox.open(1, 10000);
            showMessage("Open cash box with 24v and allow to open again after 10s.");
        }catch (NSDKException e){
            e.printStackTrace();
            showErrorMessage(e, "open cash box");
        }
    }
}