package com.newland.sdkdemo.fragment.dock;

import android.content.Context;

import com.newland.sdk.dock.DockModuleManage;
import com.newland.sdk.dock.deviceinfo.DockDeviceInfoModule;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.adapter.LayoutMode;
import com.newland.sdkdemo.annotation.MethodGridEntity;
import com.newland.sdkdemo.fragment.BaseFragment;

public class DockDeviceInfoFragment extends BaseFragment {
    private static final String TAG = "DeviceInfoFragment";

    private static final int INDEX_DEV_GET_INFO = 1;

    private DockDeviceInfoModule dockDeviceInfo;

    public DockDeviceInfoFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.device_info);
    }

    @Override
    public void initData() {
        dockDeviceInfo = DockModuleManage.getInstance().getDockDeviceInfoModule();
    }

    @Override
    public Object getModule() {
        return DockDeviceInfoFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.dev_mod_getinfo, functionid = INDEX_DEV_GET_INFO)
    private void sysGetPosInfo() {
        String type = dockDeviceInfo.getDeviceType();
        showMessage(context.getString(R.string.dev_infotype_machine_type) + " = " + type);
        String bioVersion = dockDeviceInfo.getBIOSVersion();
        showMessage(context.getString(R.string.dev_infotype_bios_info) + " = " + bioVersion);
        String sn = dockDeviceInfo.getSN();
        showMessage(context.getString(R.string.dev_infotype_machine_serial_num) + " = " + sn);
        String machineNumber = dockDeviceInfo.getMachineNumber();
        showMessage(context.getString(R.string.dev_infotype_machine_num) + " = " + machineNumber);

    }
}
