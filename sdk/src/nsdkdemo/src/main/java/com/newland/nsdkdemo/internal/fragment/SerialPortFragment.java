package com.newland.nsdkdemo.internal.fragment;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;

import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.serialport.BaudRate;
import com.newland.nsdk.core.api.common.serialport.DataBits;
import com.newland.nsdk.core.api.common.serialport.ParityBit;
import com.newland.nsdk.core.api.common.serialport.SerialPortSettings;
import com.newland.nsdk.core.api.common.serialport.StopBits;
import com.newland.nsdk.core.api.common.uart3.UART3Config;
import com.newland.nsdk.core.api.common.uart3.UART3Port;
import com.newland.nsdk.core.api.common.uart3.UART3Type;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.internal.devicemanager.DeviceManager;
import com.newland.nsdk.core.api.internal.serialportmanager.SerialPort;
import com.newland.nsdk.core.api.internal.serialportmanager.SerialPortManager;
import com.newland.nsdk.core.api.internal.serialportmanager.SerialPortType;
import com.newland.nsdk.core.api.internal.serialportmanager.USBSerialPort;
import com.newland.nsdk.core.common.NSDKExecutors;
import com.newland.nsdk.core.common.uart3.UART3PortImpl;
import com.newland.nsdk.core.internal.NSDKModuleManagerImpl;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.annotation.MethodGridEntity;
import com.newland.nsdkdemo.common.utils.DialogUtils;
import com.newland.nsdkdemo.common.utils.EnumUtils;
import com.newland.nsdkdemo.common.utils.MessageTag;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;

public class SerialPortFragment extends InternalBaseFragment {

    private SerialPortManager serialPortManager;
    private SerialPort serialPort;
    private int openFlag = -1;
    private int i = 0;


    public SerialPortFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_uart3port_f);
    }

    @Override
    public void initData() {
        serialPortManager = (SerialPortManager) NSDKModuleManagerImpl.getInstance().getModule(ModuleType.SERIAL_PORT_MANAGER);
    }

    @Override
    public Object getModule() {
        return SerialPortFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.uart3port_open, functionid = 0)
    private void open(){
        String[] ports = {"RS232", "RS232B", "PINPAD", "USB(typeC)", "USB_HOST", "USB_DEVICE"};
        DialogUtils.createSingleChoiceDialog(context, context.getString(R.string.tv_uart3port_f), ports, id -> {
            SerialPortType type = null;
            switch (id) {
                case 0:
                    type = SerialPortType.RS232;
                    break;
                case 1:
                    type = SerialPortType.RS232B;
                    break;
                case 2:
                    type = SerialPortType.PINPAD;
                    break;
                case 3:
                    type = SerialPortType.USB;
                    break;
                case 4:
                    type = SerialPortType.USB_HOST;
                    break;
                case 5:
                    type = SerialPortType.USB_DEVICE;
                    break;
                default:
                    break;
            }

            Log.d("type", "type:" + type.name());
            try {
                if (serialPort != null) {
                    serialPort.close();
                }
                serialPort = (SerialPort) serialPortManager.createInstance(type, new SerialPortSettings(BaudRate.BPS115200, DataBits.DATA_BIT_8, ParityBit.NO_CHECK, StopBits.STOP_BIT_ONE, false));
                serialPort.open();
                openFlag = 1;
                showMessage(String.format("%s is opened.", type));
            } catch (NSDKException e) {
                e.printStackTrace();
                showErrorMessage(e, "open uart3 port");
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.uart3port_set_node_name, functionid = 1)
    private void openWithNodeName() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.uart3port_set_node_name), null, R.layout.dialog_set_node_name, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View view) {
                EditText editNodeName = view.findViewById(R.id.edit_setNodeName);
                String nodeName = editNodeName.getText().toString();
                try {
                    serialPort = (SerialPort) serialPortManager.createInstance(nodeName, new SerialPortSettings(BaudRate.BPS115200, DataBits.DATA_BIT_8, ParityBit.NO_CHECK, StopBits.STOP_BIT_ONE, true, false));
                    serialPort.open();
                    openFlag = 1;
                    showMessage(String.format("%s is opened.", nodeName));
                } catch (NSDKException e) {
                    showErrorMessage(e, context.getString(R.string.uart3port_set_node_name));
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.uart3port_send, functionid = 1)
    private void send(){
        DialogUtils.createCustomDialog(context, R.string.uart3port_send, null, R.layout.dialog_uart3_send_receive_data, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                RadioButton rbSendPortData = view.findViewById(R.id.uart3port_send_data_radio);
                RadioButton rbReceivePortData = view.findViewById(R.id.uart3port_receive_data_radio);
                rbSendPortData.setChecked(true);
                rbReceivePortData.setChecked(false);
                LinearLayout llPortDataPrams = view.findViewById(R.id.linear_uart3port_data);
                llPortDataPrams.setVisibility(View.VISIBLE);
                LinearLayout llPortDataLenAndTimeoutParams = view.findViewById(R.id.linear_uart3port_data_len_timeout);
                llPortDataLenAndTimeoutParams.setVisibility(View.VISIBLE);
                LinearLayout llPortRadioButtonsView = view.findViewById(R.id.linear_uart3port_radiobuttons);
                llPortRadioButtonsView.setVisibility(View.GONE);
                LinearLayout llMaxLengthParams = view.findViewById(R.id.linear_maxlength);
                llMaxLengthParams.setVisibility(View.GONE);
            }

            @Override
            public void onResult(int id, View view) {
                EditText etPortData = view.findViewById(R.id.et_uart3port_edit_data);
                EditText etPortTimeout = view.findViewById(R.id.et_uart3port_timeOut);
                CheckBox cbHexMode = view.findViewById(R.id.cb_hex_mode);

                if (serialPort == null) {
                    showMessage("No UART3 port is opened.");
                    return;
                }
                byte[] data = null;
                if (cbHexMode.isChecked()) {
                    data = ISOUtils.hex2byte(etPortData.getText().toString());
                } else {
                    data = etPortData.getText().toString().getBytes(StandardCharsets.UTF_8);
                }

                try {
                    int timeOut = Integer.parseInt(etPortTimeout.getText().toString());
                    int length = serialPort.write(data, timeOut);
                    showMessage(String.format(Locale.US, "Data is sent: %s, Length: %d", ISOUtils.hexString(data), length));
                } catch (NSDKException e) {
                    e.printStackTrace();
                    showErrorMessage(e, "send");
                }
            }
        });

    }
    @MethodGridEntity(btnnameid = R.string.tv_analog_serial_read_len, functionid = 2)
    private void readLen() {
        try {
            int readLen = serialPort.readLen();
            showMessage(context.getString(R.string.tv_serial_port_read_len) + readLen);
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }
    }

    @MethodGridEntity(btnnameid = R.string.uart3port_receive, functionid = 3)
    private void receive(){
        DialogUtils.createCustomDialog(context, R.string.uart3port_receive, null, R.layout.dialog_uart3_send_receive_data, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                RadioButton rbSendPortData = view.findViewById(R.id.uart3port_send_data_radio);
                RadioButton rbReceivePortData = view.findViewById(R.id.uart3port_receive_data_radio);
                rbSendPortData.setChecked(true);
                rbReceivePortData.setChecked(false);
                LinearLayout llPortDataParams = view.findViewById(R.id.linear_uart3port_data);
                llPortDataParams.setVisibility(View.GONE);
                LinearLayout llPortDataLenAndTimeoutParams = view.findViewById(R.id.linear_uart3port_data_len_timeout);
                llPortDataLenAndTimeoutParams.setVisibility(View.VISIBLE);
                LinearLayout llRadioButtonsView = view.findViewById(R.id.linear_uart3port_radiobuttons);
                llRadioButtonsView.setVisibility(View.GONE);
            }

            @Override
            public void onResult(int id, View view) {
                EditText etPortTimeout = view.findViewById(R.id.et_uart3port_timeOut);
                EditText etDataMaxLen = view.findViewById(R.id.et_uart3port_data_maxLen);
                if (serialPort == null) {
                    showMessage("No UART3 port is opened.");
                    return;
                }
                try {
                    int maxLen = Integer.parseInt(etDataMaxLen.getText().toString());
                    int timeOut = Integer.parseInt(etPortTimeout.getText().toString());
                    byte[] result = serialPort.read(maxLen, timeOut);
                    showMessage(String.format("Received data: %s", ISOUtils.hexString(result)));
                } catch (NSDKException e) {
                    e.printStackTrace();
                    showErrorMessage(e, "receive");
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.uart3port_flush, functionid = 4)
    private void flush(){
        if (serialPort == null) {
            showMessage("No UART3 port is opened.");
            return;
        }

        try {
            serialPort.flush();
            showMessage("UART3 port is flushed.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "flush");
        }
    }


    @MethodGridEntity(btnnameid = R.string.uart3port_close, functionid = 5)
    private void close(){
        if (serialPort == null) {
            showMessage("No UART3 port is opened.");
            return;
        }

        try {
            serialPort.close();
//            serialPort = null;
            openFlag = 0;
            showMessage("UART3 port is closed.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "close uart3 port");
        }
    }
    @MethodGridEntity(btnnameid = R.string.uart3port_set_config, functionid = 6)
    private void setConfig() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.uart3port_set_config), null, R.layout.dialog_uart3, new DialogUtils.CustomDialogCallback() {

            @Override
            public void onResult(int id, View view) {
                Spinner spnBuadRate = view.findViewById(R.id.spn_uart3port_buadrate);
                Spinner spnDataBit = view.findViewById(R.id.spn_uart3port_databit);
                Spinner spnParityBit = view.findViewById(R.id.spn_uart3port_paritybit);
                Spinner spnStopBit = view.findViewById(R.id.spn_uart3port_stopbit);
                Switch swIsBlocked = view.findViewById(R.id.sw_uart3port_isBlocked);
                Switch swFlowControl = view.findViewById(R.id.sw_uart3port_flowControlEnable);
                boolean isBlocked = swIsBlocked.isChecked();
                BaudRate baudRate = EnumUtils.getBaudRate(spnBuadRate.getSelectedItem().toString());
                DataBits dataBits = EnumUtils.getDataBits(spnDataBit.getSelectedItem().toString());
                ParityBit parityBit = EnumUtils.getParityBit(spnParityBit.getSelectedItem().toString());
                StopBits stopBits =EnumUtils.getStopBits(spnStopBit.getSelectedItem().toString());
                boolean flowControl = swFlowControl.isChecked();


                try {
                        SerialPortSettings settings = new SerialPortSettings(baudRate, dataBits, parityBit, stopBits, isBlocked, flowControl);
                        serialPort.setConfig(settings);
                        showMessage(String.format("Set BaudRate = %s, DataBit = %s, ParityBit = %s, StopBit = %s success!",
                                baudRate, dataBits, parityBit, stopBits));
                    }catch (Exception e) {
                        if(e instanceof NSDKException) {
                            showErrorMessage(e, context.getString(R.string.uart3port_set_config));
                        }else {
                            e.printStackTrace();
                        }
                    }
            }
        });
    }


    @MethodGridEntity(btnnameid = R.string.uart3port_ioctl, functionid = 7)
    private void ioctl() {
        try {
            int ret = serialPort.ioctl(0x541B, new byte[] {0});
            showMessage("Result of ioctl:" + ret);
            ret = serialPort.ioctl(0x541B, new byte[] {1});
            showMessage("Result of ioctl:" + ret);
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.uart3port_ioctl));
        }
    }

    @MethodGridEntity(btnnameid = R.string.uart3port_get_port_type, functionid = 8)
    private void getPortType() {
        try {
            SerialPortType serialPortType = serialPort.getPortType();
            showMessage(context.getString(R.string.msg_uart3port_current_serial_port_type) + (serialPortType == null ? context.getString(R.string.msg_uart3port_none_type): serialPortType.name()));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.uart3port_get_port_type));
        }
    }

    @MethodGridEntity(btnnameid = R.string.uart3port_set_flow_control, functionid = 9)
    private void setFlowControl() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.uart3port_set_flow_control), null, R.layout.dialog_flow_control, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                Switch swFlowControl = dialogView.findViewById(R.id.sw_flow_control);
                boolean flowControl = swFlowControl.isChecked();
                try {
                    serialPort.setHardwareFlowControl(flowControl);
                    showMessage(context.getString(R.string.uart3port_set_flow_control));
                } catch (NSDKException e) {
                    showErrorMessage(e, context.getString(R.string.uart3port_set_flow_control));
                }


            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.uart3port_awake_external_device, functionid = 10)
    private void awakeExternalDevice() {
        try {
            serialPort.awakeExternalDevice();
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }
    }

    @MethodGridEntity(btnnameid = R.string.uart3port_get_external_power_supply, functionid = 11)
    private void getExternalPowerSupply() {
        try {
            boolean isExternalPowerSupply = serialPort.getExternalPowerSupply();
            showMessage("Is External Power Supply:" + isExternalPowerSupply);
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }
    }



}