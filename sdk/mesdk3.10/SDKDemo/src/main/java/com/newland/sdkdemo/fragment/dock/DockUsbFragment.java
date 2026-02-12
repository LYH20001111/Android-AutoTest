package com.newland.sdkdemo.fragment.dock;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.newland.sdk.dock.DockModuleManage;
import com.newland.sdk.dock.impl.usb.DockUSBInfo;
import com.newland.sdk.dock.usb.DockPortType;
import com.newland.sdk.dock.usb.DockUSBModule;
import com.newland.sdk.utils.ISOUtils;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.SDKExecutors;
import com.newland.sdkdemo.adapter.LayoutMode;
import com.newland.sdkdemo.annotation.MethodGridEntity;
import com.newland.sdkdemo.fragment.BaseFragment;
import com.newland.sdkdemo.utils.DialogUtils;

public class DockUsbFragment extends BaseFragment {
    private static final String TAG = "UsbFragment";

    private static final int INDEX_USB_GET_AVAILABLE_INFO = 1;
    private static final int INDEX_USB_GET_INFO = 2;
    private static final int INDEX_USB_OPEN = 3;
    private static final int INDEX_USB_CLOSE = 4;
    private static final int INDEX_USB_IS_ONLINE = 5;
    private static final int INDEX_USB_CLR_BUF = 6;
    private static final int INDEX_USB_READ = 7;
    private static final int INDEX_USB_WRITE = 9;
    private int usbNum = 0;

    private DockUSBModule dockUSBModule;

    public DockUsbFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.usb_mod_f);
    }

    @Override
    public void initData() {
        dockUSBModule = DockModuleManage.getInstance().getDockUSBModule();
    }

    @Override
    public Object getModule() {
        return DockUsbFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.usb_mod_get_available_info, functionid = INDEX_USB_GET_AVAILABLE_INFO)
    private void usbGetAvailableInfo() {
        try {
            usbNum = dockUSBModule.getUSBCount();
            showMessage("USB count:" + usbNum);
        } catch (Exception e) {
            showMessage(e.getMessage());
        }
    }

    @MethodGridEntity(btnnameid = R.string.usb_mod_get_info, functionid = INDEX_USB_GET_INFO)
    private void usbGetInfo() {
        try {
            showMessage("Obtain USB device information");
            String[] items = new String[]{"USB1","USB2"};
            DialogUtils.createSingleChoiceDialog(context,"Obtain USB device information",items,new DialogUtils.SingleChoiceDialogCallback(){

                @Override
                public void onResult(int id) {
                    if(id<0){
                        return;
                    }
                    try {
                        DockUSBInfo usbInfo = null;
                        if(id==0){
                            usbInfo = dockUSBModule.getUSBInfo(DockPortType.USB1);
                            showMessage("The USB1 device information is as follows：");
                        }else{
                            usbInfo = dockUSBModule.getUSBInfo(DockPortType.USB2);
                            showMessage("The USB2 device information is as follows：");
                        }
                        showMessage("DeviceType:"+usbInfo.getDeviceType());
                        showMessage("count:"+usbInfo.getCount());
                        showMessage("factoryID:"+usbInfo.getFactoryID());
                        showMessage("ManufacturerName:"+usbInfo.getManufacturerName());
                        showMessage("ManufacturerID:"+usbInfo.getManufacturerID());
                        showMessage("SupplierID:"+usbInfo.getSupplierID());

                    }catch (Exception e){
                        e.printStackTrace();
                        showMessage("Exception："+e);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.usb_mod_open, functionid = INDEX_USB_OPEN)
    private void usbOpenPort() {
        try {
            showMessage("open usb device");
            String[] items = new String[]{"USB1","USB2"};
            DialogUtils.createSingleChoiceDialog(context,"open usb device",items,new DialogUtils.SingleChoiceDialogCallback(){

                @Override
                public void onResult(int id) {
                    if(id<0){
                        return;
                    }
                    try {
                        int result = -1;
                        if(id==0){
                            result = dockUSBModule.open(DockPortType.USB1);
                            showMessage("open USB1");
                        }else{
                            result = dockUSBModule.open(DockPortType.USB2);
                            showMessage("open USB2");
                        }
                        if(result==0){
                            showMessage("successful");
                        }else{
                            showMessage("failed");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        showMessage("Exception："+e);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @MethodGridEntity(btnnameid = R.string.usb_mod_close, functionid = INDEX_USB_CLOSE)
    private void usbClosePort() {
        try {
            showMessage("close usb device");
            String[] items = new String[]{"USB1","USB2"};
            DialogUtils.createSingleChoiceDialog(context,"close usb device",items,new DialogUtils.SingleChoiceDialogCallback(){

                @Override
                public void onResult(int id) {
                    if(id<0){
                        return;
                    }
                    try {
                        int result = -1;
                        if(id==0){
                            result = dockUSBModule.close(DockPortType.USB1);
                            showMessage("close USB1 device");
                        }else{
                            result = dockUSBModule.close(DockPortType.USB2);
                            showMessage("clode USB2 device");
                        }
                        if(result==0){
                            showMessage("successful");
                        }else{
                            showMessage("failed");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        showMessage("Exception："+e);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.usb_mod_is_online, functionid = INDEX_USB_IS_ONLINE)
    private void usbIsOnline() {
        try {
            showMessage("Is the USB device online");
            String[] items = new String[]{"USB1","USB2"};
            DialogUtils.createSingleChoiceDialog(context,"Is the USB device online",items,new DialogUtils.SingleChoiceDialogCallback(){

                @Override
                public void onResult(int id) {
                    if(id<0){
                        return;
                    }
                    try {
                        boolean isOnline = false;
                        if(id==0){
                            isOnline = dockUSBModule.isOnline(DockPortType.USB1);
                            showMessage("USB1:"+isOnline);
                        }else{
                            isOnline = dockUSBModule.isOnline(DockPortType.USB2);
                            showMessage("USB2:："+isOnline);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        showMessage("Exception："+e);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.usb_mod_clrbuf, functionid = INDEX_USB_CLR_BUF)
    private void usbPortClrBuf() {
        try {
            showMessage("clear buffer");
            String[] items = new String[]{"USB1","USB2"};
            DialogUtils.createSingleChoiceDialog(context,"clear buffer",items,new DialogUtils.SingleChoiceDialogCallback(){

                @Override
                public void onResult(int id) {
                    if(id<0){
                        return;
                    }
                    try {
                        boolean isOnline = false;
                        if(id==0){
                            isOnline = dockUSBModule.clearBuffer(DockPortType.USB1);
                            showMessage("clear USB1 buffer"+isOnline);
                        }else{
                            isOnline = dockUSBModule.clearBuffer(DockPortType.USB2);
                            showMessage("clear USB2 buffer："+isOnline);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        showMessage("Exception："+e);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.usb_mod_read, functionid = INDEX_USB_READ)
    private void usbPortRead() {
        try {
            showMessage("read");
            String[] items = new String[]{"USB1","USB2"};
            DialogUtils.createSingleChoiceDialog(context,"read usb data",items,new DialogUtils.SingleChoiceDialogCallback(){

                @Override
                public void onResult(int id) {
                    if(id<0){
                        return;
                    }
                    try {
                        DockPortType dockPortType;
                        if(id==0){
                            dockPortType = DockPortType.USB1;
                        }else{
                            dockPortType = DockPortType.USB2;
                        }

                        byte[] data = new byte[128];
                        int length = dockUSBModule.read(dockPortType,data,data.length,1000);
                        if(length>0){
                            byte[] outData = new byte[length];
                            System.arraycopy(data,0,outData,0,length);
                            showMessage(dockPortType+"Recv len："+length);
                            showMessage(dockPortType+"Recv data："+(ISOUtils.hexString(outData)));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        showMessage("Exception："+e);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @MethodGridEntity(btnnameid = R.string.usb_mod_write, functionid = INDEX_USB_WRITE)
    private void usbPortWrite() {
        String[] items = new String[]{"USB1","USB2"};
        DialogUtils.createCustomDialog(context, R.string.port_mod_write, items, R.layout.dialog_spinner_edit, new DialogUtils.CustomDialogCallback2() {
            TextView editText = null;
            EditText edit = null;

            @Override
            public void onInit(View view) {
                editText = view.findViewById(R.id.edit_title);
                edit = view.findViewById(R.id.edit_text);
                editText.setText("Send Data:");
                edit.setText("123123123123");
            }

            @Override
            public void onResult(int id, View view) {
                SDKExecutors.getFixedThreadPoolInstance().submit(()->{
                    String message = context.getString(R.string.port_mod_write);
                    String sendData = edit.getText().toString();
                    Log.d(TAG, "Send Data:" + sendData);
                    try {
                        DockPortType dockPortType;
                        if(id==0){
                            dockPortType = DockPortType.USB1;
                        }else{
                            dockPortType = DockPortType.USB2;
                        }
                        dockUSBModule.write(dockPortType, ISOUtils.hex2byte(sendData));
                        showMessage(message + "Success");
                        showMessage("Send Len:" + sendData.length() + " Data：" + sendData);
                    } catch (Exception e) {
                        showMessage(e.getMessage());
                    }
                });
            }
        });
    }

}
