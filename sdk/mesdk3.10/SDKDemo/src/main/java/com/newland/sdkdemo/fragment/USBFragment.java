package com.newland.sdkdemo.fragment;

import android.content.Context;
import android.hardware.usb.UsbDevice;

import com.newland.sdk.module.usb.SelectUsbDeviceListener;
import com.newland.sdk.module.usb.USBModule;
import com.newland.sdk.utils.ISOUtils;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.adapter.LayoutMode;
import com.newland.sdkdemo.annotation.MethodGridEntity;
import com.newland.sdkdemo.utils.DialogUtils;
import com.newland.sdkdemo.utils.MessageTag;

import java.util.HashMap;
import java.util.Map;

/**
 * Author by youjf
 */
public class USBFragment extends BaseFragment {
    private USBModule usbModule;
    private String[] names;
    private UsbDevice selectedDevice = null;
    private Object object = new Object();

    public USBFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_usb_f);
    }

    @Override
    public void initData() {
        usbModule = moduleManage.getUSBModule();
    }

    @Override
    public Object getModule() {
        return USBFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.open, functionid = 1)
    private void openUSB() {
        try {
            usbModule.open(new SelectUsbDeviceListener() {
                @Override
                public UsbDevice onSelect(final HashMap<String, UsbDevice> usbDeviceList) {

                    if(usbDeviceList!=null && usbDeviceList.size()>0){
                        showMessage("usbDeviceList.size():"+usbDeviceList.size());
                        names = new String[usbDeviceList.size()];
                        int i=0;
                        for(Map.Entry<String, UsbDevice>  entry:usbDeviceList.entrySet()){
                            entry.getValue();
                            names[i] = entry.getKey();
                            i++;
                        }

                        DialogUtils.createSingleChoiceDialog(context, "", names, new DialogUtils.SingleChoiceDialogCallback() {
                            @Override
                            public void onResult(int id) {
                                if(id<0){
                                    synchronized (object){
                                        object.notify();
                                    }
                                    return;
                                }
                                selectedDevice = usbDeviceList.get(names[id]);
                                showMessage("selected device:"+names[id]);
                                synchronized (object){
                                    object.notify();
                                }
                            }
                        });
                    }else {
                        showMessage("usbDeviceList is null",MessageTag.ERROR);
                        synchronized (object){
                            object.notify();
                        }
                    }
                    synchronized (object){
                        try {
                            object.wait();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                    return selectedDevice;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            showMessage(""+e,MessageTag.ERROR);
        }

    }



    @MethodGridEntity(btnnameid = R.string.write, functionid = 2)
    private void writeSerial() {
        try {
            int result = usbModule.write(ISOUtils.hex2byte("0000005B"), 4, 3000);
            showMessage(context.getString(R.string.msg_write_data) + result + "\r\n", MessageTag.NORMAL);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_write_data_error) + e, MessageTag.ERROR);

        }
    }

    @MethodGridEntity(btnnameid = R.string.read, functionid = 3)
    private void readSerial() {
        try {
            showMessage(context.getString(R.string.start_read_serial), MessageTag.TIP);
            byte[] readBuffer = new byte[10];
            int read = usbModule.read(readBuffer, 10, 3000);
            if (read > 0) {
                showMessage(context.getString(R.string.msg_get_usb_data_length) + read, MessageTag.NORMAL);
                byte[] readData = new byte[read];
                System.arraycopy(readBuffer, 0, readData, 0, read);
                showMessage(context.getString(R.string.msg_get_usb_data) + ISOUtils.hexString(readData), MessageTag.TIP);
            } else {
                showMessage(context.getString(R.string.read_serial_null), MessageTag.ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_get_usb_data_error) + e, MessageTag.ERROR);

        }
    }

    @MethodGridEntity(btnnameid = R.string.close, functionid = 4)
    private void closeSerial() {
        try {
            int reuslt = usbModule.close();
            showMessage(context.getString(R.string.msg_close_usb_result) + reuslt + "\r\n", MessageTag.NORMAL);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_close_usb_error) + e, MessageTag.ERROR);

        }
    }
}
