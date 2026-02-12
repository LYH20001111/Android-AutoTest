package com.newland.sdkdemo.fragment.dock;

import android.content.Context;

import com.newland.sdk.DeviceController;
import com.newland.sdk.inter.externaliccard.ExtICCardModule;
import com.newland.sdk.inter.externalpin.AccessType;
import com.newland.sdk.inter.externalpin.ExtParams;
import com.newland.sdk.module.emvl3.EmvInitParams;
import com.newland.sdk.pinpad.utils.ISOUtils;
import com.newland.sdkdemo.AppConfig;
import com.newland.sdkdemo.MainActivity;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.SDKExecutors;
import com.newland.sdkdemo.adapter.LayoutMode;
import com.newland.sdkdemo.annotation.MethodGridEntity;
import com.newland.sdkdemo.fragment.BaseFragment;
import com.newland.sdkdemo.fragment.dock.communication.ChannelType;
import com.newland.sdkdemo.fragment.dock.communication.CommunicationTool;
import com.newland.sdkdemo.fragment.dock.emv.EmvL3Listener;
import com.newland.sdkdemo.utils.DialogUtils;
import com.newland.sdkdemo.utils.MessageTag;

public class DockICCardFragment extends BaseFragment {
    private ExtICCardModule extICCardModule;
    private ExtParams extParams;

    public DockICCardFragment(Context context){
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return "null";
    }

    @Override
    public void initData() {
        extICCardModule = DeviceController.getInstance().getExtICCardModule();
    }

    @Override
    public Object getModule() {
        return DockICCardFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.tv_external_pin_init, functionid = 1)
    private void initExternalPinpad() {
        extParams = new ExtParams();
        CommunicationTool.getInstance().init(context);
        String[] accessTypes = new String[]{"DEFAULT-USB","DOCK-UART","DOCK_USB1"};
        DialogUtils.createSingleChoiceDialog(context, context.getString(R.string.msg_select_access_type), accessTypes, new DialogUtils.SingleChoiceDialogCallback() {
            @Override
            public void onResult(int id) {
                if (id == 0) {
                    extParams.setAccessType(AccessType.DEFAULT_USB);
                    CommunicationTool.getInstance().setChannelType(ChannelType.DEFAULT_USB);
                    showMessage(R.string.msg_choose_default_usb, MessageTag.NORMAL);
                } else if (id == 1) {
                    extParams.setAccessType(AccessType.OTHERS);
                    CommunicationTool.getInstance().setChannelType(ChannelType.DOCK_UART);
                    showMessage(R.string.msg_choose_serial, MessageTag.NORMAL);
                } else if (id == 2) {
                    extParams.setAccessType(AccessType.OTHERS);
                    CommunicationTool.getInstance().setChannelType(ChannelType.DOCK_USB1);
                    showMessage(R.string.msg_choose_usb, MessageTag.NORMAL);
                }
                SDKExecutors.getFixedThreadPoolInstance().submit(()->{
                    boolean init = extICCardModule.init(CommunicationTool.getInstance().getCommunicationListener(),extParams);
                    if (init) {
                        showMessage(context.getString(R.string.msg_ext_pininput_init_pinpad_success) + "\r\n", MessageTag.NORMAL);
                    } else {
                        showMessage(context.getString(R.string.msg_ext_pininput_init_pinpad_exception) + "\r\n", MessageTag.ERROR);
                    }
                });
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_iccard_poweron, functionid = 2)
    private void powerOn() {
        byte[] data = extICCardModule.powerOn();
        showMessage("poweron result:" + (data==null?null: ISOUtils.hexString(data)), MessageTag.NORMAL);
    }

    @MethodGridEntity(btnnameid = R.string.tv_rf_transmit, functionid = 3)
    private void rfcardCommunication() {
        String apdu = "0084000004";
        byte req[] = ISOUtils.hex2byte(apdu);
        byte result[] = extICCardModule.transmit(req,null);
        showMessage(context.getString(R.string.msg_send_data) + apdu + "\r\n", MessageTag.DATA);
        showMessage(context.getString(R.string.msg_get_data) + (result == null ? "null" : ISOUtils.hexString(result)) + "\r\n", MessageTag.DATA);
    }

    @MethodGridEntity(btnnameid = R.string.tv_rf_power_off, functionid = 4)
    private void rfcardPowerOff() {
        extICCardModule.powerOff();
        showMessage(context.getString(R.string.msg_rf_poweroff_finished) + "\r\n", MessageTag.NORMAL);
    }


}
