package com.newland.nsdkdemo.internal.fragment;

import android.content.Context;

import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.internal.beeper.Beeper;
import com.newland.nsdk.core.api.internal.ethernetmanager.EthernetManager;
import com.newland.nsdk.core.api.internal.ethernetmanager.EthernetStatus;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.annotation.MethodGridEntity;

public class EthernetFragment extends InternalBaseFragment {

    private EthernetManager ethernetManager;

    public EthernetFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_ethernet);
    }

    @Override
    public void initData() {
        ethernetManager = (EthernetManager) moduleManager.getModule(ModuleType.ETHERNET_MANAGER);
    }

    @Override
    public Object getModule() {
        return EthernetFragment.this;
    }

    private static final int INDEX_ENABLE = 1;
    private static final int INDEX_DISABLE = 2;
    private static final int INDEX_GET_STATUS = 3;
    private static final int INDEX_GET_CONFIG = 4;

    @MethodGridEntity(btnnameid = R.string.ethernet_enable, functionid = INDEX_ENABLE)
    private void open() {
        try {
            ethernetManager.enable();
            showMessage(context.getString(R.string.ethernet_enable));
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "enable ethernet");
        }
    }

    @MethodGridEntity(btnnameid = R.string.ethernet_disable, functionid = INDEX_DISABLE)
    private void close() {
        try {
            ethernetManager.disable();
            showMessage(context.getString(R.string.ethernet_disable));
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "disable ethernet");
        }
    }

    @MethodGridEntity(btnnameid = R.string.ethernet_get_status, functionid = INDEX_GET_STATUS)
    private void getStatus() {
        try {
            EthernetStatus status = ethernetManager.getStatus();
            showMessage(String.format("%s: %s", context.getString(R.string.ethernet_get_status), status));
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "get ethernet status");
        }
    }

    @MethodGridEntity(btnnameid = R.string.ethernet_get_config, functionid = INDEX_GET_CONFIG)
    private void getConfig() {
        try {
            String config = ethernetManager.getConfig();
            showMessage(String.format("%s: %s", context.getString(R.string.ethernet_get_config), config));
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "get ethernet config");
        }
    }
}
