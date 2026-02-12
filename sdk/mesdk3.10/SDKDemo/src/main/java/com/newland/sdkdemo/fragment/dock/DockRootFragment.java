package com.newland.sdkdemo.fragment.dock;

import android.content.Context;

import com.newland.nsdk.dock.model.DockStatus;
import com.newland.sdk.DeviceController;
import com.newland.sdk.dock.DockInitListener;
import com.newland.sdk.dock.DockModuleManage;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.adapter.LayoutMode;
import com.newland.sdkdemo.annotation.MethodGridEntity;
import com.newland.sdkdemo.fragment.BaseFragment;
import com.newland.sdkdemo.utils.MessageTag;

/**
 * Copyright © 2023 Fujian Newland Payment Technology Co., Ltd
 * Author: wuhh
 * Date: 2023/9/27 17:02
 * Description:
 * History:
 * <author> <time> <version> <desc>
 */
public class DockRootFragment extends BaseFragment {

    private DockModuleManage dockModuleManage;
    public DockRootFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_dock_f);
    }

    @Override
    public void initData() {
        dockModuleManage = DockModuleManage.getInstance();
    }

    @Override
    public Object getModule() {
        return DockRootFragment.this;
    }

    private static final int INDEX_DOCKINIT = 0;
    private static final int INDEX_UART_MODULE = 1;
    private static final int INDEX_USB_MODULE = 2;
    private static final int INDEX_EMVL3MODULE = 3;
    private static final int INDEX_PIN_MODULE = 4;
    private static final int INDEX_RFID_MODULE = 5;
    private static final int INDEX_IC_MODULE = 6;
    private static final int INDEX_DEVICE_INFO_MODULE = 7;
    private static final int INDEX_CASHBOX_MODULE = 8;

    private volatile boolean isConnect = false;
    @MethodGridEntity(btnname = "DockInit", functionid = INDEX_DOCKINIT)
    private void DockInit(){
        DeviceController.getInstance().init(context);
        dockModuleManage.init(context,new DockInitListener() {
            @Override
            public void onConnected() {
                isConnect = true;
                showMessage(context.getString(R.string.msg_device_connect), MessageTag.TIP);
            }

            @Override
            public void onDisConnected(int state) {
                isConnect = false;
                showMessage(context.getString(R.string.disconnect),MessageTag.ERROR);
                boolean isEnable = DockStatus.isStateOf(state, DockStatus.STATUS_DOCK_ENABLED);
                if(!isEnable){
                    showMessage(context.getString(R.string.open_dock_setting),MessageTag.ERROR);
                }
                dockModuleManage.startSetting();
            }
        });


    }

    @MethodGridEntity(btnname = "SerialModule", functionid = INDEX_UART_MODULE)
    private void SerialModule(){
        if(!isConnect) return;
        switchFragment(new DockSerialFragment(context));
    }

    @MethodGridEntity(btnname = "USBModule", functionid = INDEX_USB_MODULE)
    private void USBModule(){
        if(!isConnect) return;
        switchFragment(new DockUsbFragment(context));
    }

    @MethodGridEntity(btnname = "EmvL3Module", functionid = INDEX_EMVL3MODULE)
    private void EmvL3Module(){
        if(!isConnect) return;
        switchFragment(new DockEmvL3Fragment(context));
    }

    @MethodGridEntity(btnname = "PinModule", functionid = INDEX_PIN_MODULE)
    private void PinModule(){
        if(!isConnect) return;
        switchFragment(new DockPinInputFragment(context));
    }

    @MethodGridEntity(btnname = "RfidModule", functionid = INDEX_RFID_MODULE)
    private void RfidModule(){
        if(!isConnect) return;
        switchFragment(new DockRFCardFragment(context));
    }

    @MethodGridEntity(btnname = "ICModule", functionid = INDEX_IC_MODULE)
    private void ICModule(){
        if(!isConnect) return;
        switchFragment(new DockICCardFragment(context));
    }

    @MethodGridEntity(btnname = "DeviceInfoModule", functionid = INDEX_DEVICE_INFO_MODULE)
    private void DeviceInfoModule(){
        if(!isConnect) return;
        switchFragment(new DockDeviceInfoFragment(context));
    }

    @MethodGridEntity(btnname = "CashBoxModule", functionid = INDEX_CASHBOX_MODULE)
    private void CashBoxModule(){
        if(!isConnect) return;
        switchFragment(new DockCashBoxFragment(context));
    }
}
