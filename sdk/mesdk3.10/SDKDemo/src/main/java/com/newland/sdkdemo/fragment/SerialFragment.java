package com.newland.sdkdemo.fragment;

import android.content.Context;
import android.view.View;
import android.widget.RadioGroup;

import com.newland.sdk.module.serialport.Baudrate;
import com.newland.sdk.module.serialport.PortType;
import com.newland.sdk.module.serialport.SerialExtParams;
import com.newland.sdk.module.serialport.SerialPortModule;
import com.newland.sdk.utils.ISOUtils;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.adapter.LayoutMode;
import com.newland.sdkdemo.annotation.MethodGridEntity;
import com.newland.sdkdemo.utils.DialogUtils;
import com.newland.sdkdemo.utils.MessageTag;

import java.util.Arrays;

/**
 * Author by bxy, Date on 2019/5/11 0011.
 */
public class SerialFragment extends BaseFragment {
    private SerialPortModule serial;
    private static final int INDEX_OPEN_SERIAL = 1;
    private static final int INDEX_GET_JIN_VERSION = 2;
    private static final int INDEX_WRITE_SERIAL = 3;
    private static final int INDEX_READ_SERIAL = 4;
    private static final int INDEX_CLOSE_SERIAL = 5;
    private String writeData = "1234567890";
    private PortType portType = PortType.RS232;
    private Baudrate baudrate = Baudrate.BPS115200;

    public SerialFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_usbserial_f);
    }

    @Override
    public void initData() {
        serial = moduleManage.getSerialPortModule();
    }

    @Override
    public Object getModule() {
        return SerialFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.tv_open_usb, functionid = INDEX_OPEN_SERIAL)
    private void openSerial() {
            String[] ports = new String[]{"RS232", "MIN_USB", "PINPAD","BLEBASE_RS232","BLEBASE_USB1"};
            DialogUtils.createCustomDialog(context, context.getString(R.string.tv_set_usb_config), null, R.layout.dialog_serial, new DialogUtils.CustomDialogCallback() {
                @Override
                public void onResult(int id, View dialogView) {
                    try {
                    if (id == -1) {
                        return;
                    }
                        SerialExtParams serialExtParams = new SerialExtParams();
                        RadioGroup portGroup = dialogView.findViewById(R.id.group_oprt);
                    RadioGroup bpsGroup = dialogView.findViewById(R.id.group_bps);
                    switch (portGroup.getCheckedRadioButtonId()) {
                        case R.id.btn_rs232:
                            portType = PortType.RS232;
                            serialExtParams = null;
                            break;
                        case R.id.btn_min_usb:
                            portType = PortType.MIN_USB;
                            serialExtParams = null;
                            break;
                        case R.id.btn_pinpad:
                            portType = PortType.PINPAD;
                            serialExtParams = null;
                            break;
                        case R.id.btn_blebase_rs232:
                            portType = PortType.BLEBASE_RS232;
                            serialExtParams = null;
                            //serialExtParams.setBleName("");
                            //serialExtParams.setBleAddress("");
                            break;
                        case R.id.btn_blebase_usb1:
                            portType = PortType.BLEBASE_USB1;
                            serialExtParams = null;
                            //serialExtParams.setBleName("");
                            //serialExtParams.setBleAddress("");
                            break;
                    }

                    switch (bpsGroup.getCheckedRadioButtonId()) {
                        case R.id.btn_bps9600:
                            baudrate = Baudrate.BPS9600;
                            break;
                        case R.id.btn_bps57600:
                            baudrate = Baudrate.BPS57600;
                            break;
                        case R.id.btn_bps115200:
                            baudrate = Baudrate.BPS115200;
                            break;
                    }

                    int result = serial.open(portType, baudrate, serialExtParams);
                    showMessage(context.getString(R.string.msg_open_usb_success) + result, MessageTag.NORMAL);
                    } catch (Exception e) {
                        e.printStackTrace();
                        showMessage(context.getString(R.string.msg_open_usb_error) + e, MessageTag.ERROR);
                    }
                }
            });
    }


    @MethodGridEntity(btnnameid = R.string.tv_get_JNI_version, functionid = INDEX_GET_JIN_VERSION)
    private void getJNIVersion() {
        try {
            //showMessage(""+"Product Model: " + android.os.Build.MODEL + "," + android.os.Build.VERSION.SDK + "," + android.os.Build.VERSION.RELEASE, MessageTag.DATA);
            String version = serial.getVersion();
            showMessage(context.getString(R.string.msg_JNI_version) + version + "\r\n", MessageTag.NORMAL);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_get_JNI_version_error) + e, MessageTag.ERROR);

        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_write_usb_data, functionid = INDEX_WRITE_SERIAL)
    private void writeSerial() {
        try {
            int result = serial.write(ISOUtils.hex2byte("02000332302F032D"), 8, 3000);
            showMessage(context.getString(R.string.msg_write_data) + result + "\r\n", MessageTag.NORMAL);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_write_data_error) + e, MessageTag.ERROR);

        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_get_usb_data, functionid = INDEX_READ_SERIAL)
    private void readSerial() {
        try {
            showMessage(context.getString(R.string.start_read_serial), MessageTag.TIP);
            byte[] readBuffer = new byte[244];
            int read = serial.read(readBuffer, 244, 3000);
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

    @MethodGridEntity(btnnameid = R.string.tv_close_usb, functionid = INDEX_CLOSE_SERIAL)
    private void closeSerial() {
        try {
            int reuslt = serial.close();
            showMessage(context.getString(R.string.msg_close_usb_result) + reuslt + "\r\n", MessageTag.NORMAL);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_close_usb_error) + e, MessageTag.ERROR);

        }
    }
}
