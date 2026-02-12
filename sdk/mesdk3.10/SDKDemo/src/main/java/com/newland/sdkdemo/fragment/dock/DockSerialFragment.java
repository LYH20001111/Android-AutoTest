package com.newland.sdkdemo.fragment.dock;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.newland.sdk.dock.DockModuleManage;
import com.newland.sdk.dock.serial.DockBaudrate;
import com.newland.sdk.dock.serial.DockSerialModule;
import com.newland.sdk.utils.ISOUtils;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.SDKExecutors;
import com.newland.sdkdemo.adapter.LayoutMode;
import com.newland.sdkdemo.annotation.MethodGridEntity;
import com.newland.sdkdemo.fragment.BaseFragment;
import com.newland.sdkdemo.utils.DialogUtils;
import com.newland.sdkdemo.utils.MessageTag;


public class DockSerialFragment extends BaseFragment {
    private static final String TAG = "SerialportFragment";

    private static final int INDEX_SERIALPORT_OPEN = 1;
    private static final int INDEX_SERIALPORT_CLRBUF = 2;
    private static final int INDEX_SERIALPORT_READ = 3;
    private static final int INDEX_SERIALPORT_READLEN = 4;
    private static final int INDEX_SERIALPORT_WRITE = 5;
    private static final int INDEX_SERIALPORT_WRITE_AND_READ = 6;
    private static final int INDEX_SERIALPORT_CLOSE = 8;
    private static final int INDEX_CASH_BOX = 9;
    private String config;
    private int portType = 0;
    private int portIndex = 0;
    private DockSerialModule dockSerialModule;

    public DockSerialFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.port_mod_f);
    }

    @Override
    public void initData() {
        dockSerialModule = DockModuleManage.getInstance().getDockSerialModule();
    }

    @Override
    public Object getModule() {
        return DockSerialFragment.this;
    }

    private int getPortType(int selectIndex) {
        portIndex = selectIndex;
        switch (portIndex) {
            case 0:
                return 0;
            case 1:
                return 8;
            case 2:
                return 10;
        }
        return -1;
    }

    @MethodGridEntity(btnnameid = R.string.port_mod_open, functionid = INDEX_SERIALPORT_OPEN)
    private void portOpen() {
        DialogUtils.createCustomDialog(context, R.string.port_mod_open, null, R.layout.dialog_port_open, new DialogUtils.CustomDialogCallback2() {
                    Spinner portTypeSpin = null;
                    Spinner baudRateSpin = null;
                    Spinner dataBitsSpin = null;
                    Spinner verMethodSpin = null;
                    Spinner stopBitsSpin = null;

                    @Override
                    public void onInit(View view) {
                        portTypeSpin = view.findViewById(R.id.sp_port_type);
                        portTypeSpin.setSelection(portIndex);
                        baudRateSpin = view.findViewById(R.id.sp_port_baudrate);
                        dataBitsSpin = view.findViewById(R.id.sp_port_data_bits);
                        verMethodSpin = view.findViewById(R.id.sp_port_ver_method);
                        stopBitsSpin = view.findViewById(R.id.sp_port_stop_bit);
                    }

                    @Override
                    public void onResult(int id, View view) {
                        SDKExecutors.getThreadPoolInstance().submit(new Runnable() {
                            @Override
                            public void run() {
                                String message = context.getString(R.string.port_mod_open);
                                String portString = portTypeSpin.getSelectedItem().toString();
                                //portType = getPortType(portTypeSpin.getSelectedItemPosition());
                                Log.d(TAG, "Select Port: " + portType + " " + portString);
                                String baundRate = baudRateSpin.getSelectedItem().toString();
                                String dataBits = dataBitsSpin.getSelectedItem().toString();
                                String verMethod = verMethodSpin.getSelectedItem().toString();
                                String stopBits = stopBitsSpin.getSelectedItem().toString();
                                String baud = baundRate + "," + dataBits + "," + verMethod + "," + stopBits;
                                Log.d(TAG, baud);

                                message += " " + portString + " with " + baud + " ";

                                try {
                                    dockSerialModule.open(DockBaudrate.valueOf(Integer.valueOf(baundRate)),null);
                                    config = baud;
                                    showMessage(message + "Success", MessageTag.TIP);
                                } catch (Exception e) {
                                    showMessage(e.getMessage(), MessageTag.ERROR);
                                }
                            }
                        });

                    }
                });
    }

    @MethodGridEntity(btnnameid = R.string.port_mod_clrbuf, functionid = INDEX_SERIALPORT_CLRBUF)
    private void portClrBuf() {
        try {
            boolean result = dockSerialModule.clearBuffer(1);
            showMessage(context.getString(R.string.clear_buffer)+result);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.port_mod_read, functionid = INDEX_SERIALPORT_READ)
    private void portRead() {
        byte[] data = new byte[256];
        int ret = dockSerialModule.read(data,data.length,1000);
        if(ret > 0 && data!=null){
            showMessage(context.getString(R.string.receive_data)+(ISOUtils.hexString(data)));
        }else {
            showMessage(context.getString(R.string.receive_data)+(ISOUtils.hexString(data)));
        }
    }


    @MethodGridEntity(btnnameid = R.string.port_mod_write, functionid = INDEX_SERIALPORT_WRITE)
    private void portWrite() {
        DialogUtils.createCustomDialog(context, R.string.port_mod_write, null, R.layout.dialog_spinner_edit, new DialogUtils.CustomDialogCallback2() {
            TextView spinnerText = null;
            Spinner spinner = null;
            ArrayAdapter<CharSequence> adapter = null;

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
                SDKExecutors.getThreadPoolInstance().submit(()->{
                    String message = context.getString(R.string.port_mod_write);
                    showMessage("Select Port: " + portType + " ");
                    String sendData = edit.getText().toString();
                    Log.d(TAG, "Send Data:" + sendData);
                    try {
                        dockSerialModule.write(ISOUtils.hex2byte(sendData));
                        showMessage(message + "Success", MessageTag.TIP);
                        showMessage("Send Len:" + sendData.length() + " Data：" + sendData, MessageTag.TIP);
                    } catch (Exception e) {
                        showMessage(e.getMessage(), MessageTag.ERROR);
                    }
                });
            }
        });
    }




    @MethodGridEntity(btnnameid = R.string.port_mod_close, functionid = INDEX_SERIALPORT_CLOSE)
    private void portClose() {
        try {
            int ret = dockSerialModule.close();
            if(ret == 0){
                showMessage(context.getString(R.string.close_successfully));
            }else{
                showMessage(context.getString(R.string.Close_failed),MessageTag.ERROR);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
