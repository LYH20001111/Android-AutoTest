package com.newland.sdkdemo.fragment.dock;

import android.content.Context;

import com.newland.sdk.dock.DockModuleManage;
import com.newland.sdk.dock.cashbox.DockCashBoxModule;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.adapter.LayoutMode;
import com.newland.sdkdemo.annotation.MethodGridEntity;
import com.newland.sdkdemo.fragment.BaseFragment;

public class DockCashBoxFragment extends BaseFragment {
    private DockCashBoxModule cashBox;
    public DockCashBoxFragment(Context context) {
        super(context, LayoutMode.GRID);
    }
    @Override
    public String title() {
        return context.getString(R.string.cashbox);
    }

    @Override
    public void initData() {
        cashBox = DockModuleManage.getInstance().getDockCashBoxModule();
    }

    @Override
    public Object getModule() {
        return DockCashBoxFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.open, functionid = 1)
    private void open() {
        cashBox.open(0,500);
    }
}
