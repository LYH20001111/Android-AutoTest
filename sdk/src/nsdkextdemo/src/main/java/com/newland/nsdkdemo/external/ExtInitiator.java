package com.newland.nsdkdemo.external;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Message;
import android.view.View;
import android.widget.RadioButton;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.serialport.BaudRate;
import com.newland.nsdk.core.api.common.serialport.DataBits;
import com.newland.nsdk.core.api.common.serialport.ParityBit;
import com.newland.nsdk.core.api.common.serialport.StopBits;
import com.newland.nsdk.core.api.common.uart3.UART3Config;
import com.newland.nsdk.core.api.common.uart3.UART3Type;
import com.newland.nsdk.core.api.common.utils.LogLevel;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.external.communication.CommunicatorListener;
import com.newland.nsdk.core.api.external.communication.ExternalCommunicatorState;
import com.newland.nsdk.core.api.external.communication.ExternalCommunicatorType;
import com.newland.nsdk.core.api.external.communication.NSDKCommunicator;
import com.newland.nsdk.core.external.ExtNSDKModuleManagerImpl;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.MainActivity;
import com.newland.nsdkdemo.common.SDKExecutors;
import com.newland.nsdkdemo.common.fragment.ModuleInfo;
import com.newland.nsdkdemo.common.utils.DialogUtils;
import com.newland.nsdkdemo.common.utils.MessageTag;
import com.newland.nsdkdemo.external.activity.BTActivity;
import com.newland.nsdkdemo.external.fragment.ExtBeeperFragment;
import com.newland.nsdkdemo.external.fragment.ExtCardEmulatorFragment;
import com.newland.nsdkdemo.external.fragment.ExtCardReaderFragment;
import com.newland.nsdkdemo.external.fragment.ExtContactCardFragment;
import com.newland.nsdkdemo.external.fragment.ExtContactlessCardFragment;
import com.newland.nsdkdemo.external.fragment.ExtCryptoFragment;
import com.newland.nsdkdemo.external.fragment.ExtDeviceManagerFragment;
import com.newland.nsdkdemo.external.fragment.ExtDisplayFragment;
import com.newland.nsdkdemo.external.fragment.ExtECDHEFragment;
import com.newland.nsdkdemo.external.fragment.ExtESignatureFragment;
import com.newland.nsdkdemo.external.fragment.ExtKeyManagerFragment;
import com.newland.nsdkdemo.external.fragment.ExtKeyboardFragment;
import com.newland.nsdkdemo.external.fragment.ExtLEDFragment;
import com.newland.nsdkdemo.external.fragment.ExtPINEntryFragment;
import com.newland.nsdkdemo.external.fragment.ExtRKIFragment;
import com.newland.nsdkdemo.external.fragment.ExtScannerFragment;
import com.newland.nsdkdemo.external.fragment.ExtSettingsManagerFragment;

import java.util.ArrayList;

public class ExtInitiator {
    private Context context;

    private NSDKCommunicator currentCommunicator;
    private ExtNSDKModuleManagerImpl moduleManager;
    public static ExtInitiator getInstance() {
        return ExtInitiatorHolder.singletonHolder;
    }

    public void init(Context context){
        this.context = context;
        moduleManager = ExtNSDKModuleManagerImpl.getInstance();
        moduleManager.setDebugMode(LogLevel.VERBOSE);
    }

    public void addFragments(ArrayList<ModuleInfo> moduleFragments) {
        moduleFragments.add(new ModuleInfo(new ExtKeyManagerFragment(this.context), R.string.tv_extkeymanager_f, R.drawable.pin));
        moduleFragments.add(new ModuleInfo(new ExtKeyboardFragment(this.context), R.string.tv_extkeyboard, R.drawable.keyboard));
        moduleFragments.add(new ModuleInfo(new ExtScannerFragment(this.context), R.string.tv_extscan_f, R.drawable.scanner));
        moduleFragments.add(new ModuleInfo(new ExtCardReaderFragment(this.context), R.string.tv_extcardreader_f, R.drawable.cardreader));
        moduleFragments.add(new ModuleInfo(new ExtContactCardFragment(this.context), R.string.tv_extsmartcard_f, R.drawable.iccard));
        moduleFragments.add(new ModuleInfo(new ExtContactlessCardFragment(this.context), R.string.tv_extcontactlesscard_f, R.drawable.rfcard));
        moduleFragments.add(new ModuleInfo(new ExtCryptoFragment(this.context), R.string.tv_extcipher_f, R.drawable.pin));
        moduleFragments.add(new ModuleInfo(new ExtPINEntryFragment(this.context), R.string.tv_extpininput_f, R.drawable.pin));
        moduleFragments.add(new ModuleInfo(new ExtDisplayFragment(this.context), R.string.tv_extdisplay_f, R.drawable.cardreader));
        moduleFragments.add(new ModuleInfo(new ExtDeviceManagerFragment(this.context), R.string.tv_extdevicebasic_f, R.drawable.ternaml));
        moduleFragments.add(new ModuleInfo(new ExtBeeperFragment(this.context), R.string.tv_extbeeper_f, R.drawable.buzzer));
        moduleFragments.add(new ModuleInfo(new ExtLEDFragment(this.context), R.string.tv_extlight_f, R.drawable.light));
        moduleFragments.add(new ModuleInfo(new ExtRKIFragment(this.context), R.string.tv_extrki_f, R.drawable.keyboard));
        moduleFragments.add(new ModuleInfo(new ExtSettingsManagerFragment(this.context), R.string.ext_settingsManger, R.drawable.pin));
        moduleFragments.add(new ModuleInfo(new ExtECDHEFragment(this.context), R.string.ext_ecdhe, R.drawable.pin));
        moduleFragments.add(new ModuleInfo(new ExtESignatureFragment(this.context), R.string.ext_esignatureFragment, R.drawable.pin));
        moduleFragments.add(new ModuleInfo(new ExtCardEmulatorFragment(this.context), R.string.ext_card_emulator_fragment, R.drawable.cardreader));
    }

    public void release() {
        moduleManager.destroy();
        try {
            if (currentCommunicator != null) {
                currentCommunicator.close(10000);
            }
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    public NSDKCommunicator getCurrentCommunicator() {
        return currentCommunicator;
    }

    public void setCurrentCommunicator(NSDKCommunicator currentCommunicator) {
        this.currentCommunicator = currentCommunicator;
    }

    private static class ExtInitiatorHolder {
        private static ExtInitiator singletonHolder = new ExtInitiator();
    }

    private CommunicatorListener listener = new CommunicatorListener() {
        @Override
        public BluetoothDevice onBluetoothList(ArrayList<BluetoothDevice> arrayList) {
            return null;
        }

        @Override
        public void onConnectedStateChange(ExternalCommunicatorState externalCommunicatorState) {
            LogUtils.d(getClass().getName(), "onConnectedStateChange>>>" + externalCommunicatorState);
            ((MainActivity)context).showMessage("onConnectedStateChange: " + externalCommunicatorState, MessageTag.NORMAL);
        }
    };

    public void switchConnectType() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.connect_type), null, R.layout.dialog_connect_type, new DialogUtils.CustomDialogCallback() {

            RadioButton rbBluetooth;
            RadioButton rbUSB;
            RadioButton rbRS232;
            RadioButton rbPINPad;

            @Override
            public void onResult(int id, View view) {
                rbBluetooth = view.findViewById(R.id.rb_bluetooth);
                rbUSB = view.findViewById(R.id.rb_usb);
                rbRS232 = view.findViewById(R.id.rb_rs232);
                rbPINPad = view.findViewById(R.id.rb_pinpad);
                if (id == -1) {
                    return;
                }
                try {
                    NSDKCommunicator communicator = null;
                    if (rbPINPad.isChecked()) {
                        disconnect();

                        UART3Config config = new UART3Config(BaudRate.BPS115200, DataBits.DATA_BIT_8, ParityBit.NO_CHECK, StopBits.STOP_BIT_ONE);

                        communicator = moduleManager.getNSDKCommunicator(context, ExternalCommunicatorType.UART3PORT, listener);
                        if(Build.MODEL.contains("CPOS")) {
                            moduleManager.setUART3Config(UART3Type.PINPAD_CPOS, config);
                        }else {
                            moduleManager.setUART3Config(UART3Type.PINPAD_A7, config);
                        }
                        ((MainActivity)context).showMessage("PINPAD communicator is set.", MessageTag.NORMAL);
                    }else if (rbRS232.isChecked()) {
                        disconnect();

                        UART3Config config = new UART3Config(BaudRate.BPS115200, DataBits.DATA_BIT_8, ParityBit.NO_CHECK, StopBits.STOP_BIT_ONE);

                        communicator = moduleManager.getNSDKCommunicator(context, ExternalCommunicatorType.UART3PORT, listener);
                        if(Build.MODEL.contains("CPOS")) {
                            moduleManager.setUART3Config(UART3Type.RS232_CPOS, config);
                        }else {
                            moduleManager.setUART3Config(UART3Type.RS232_A7, config);
                        }
                        ((MainActivity)context).showMessage("RS232 communicator is set.", MessageTag.NORMAL);
                    }else if (rbUSB.isChecked()) {
                        disconnect();

                        communicator = moduleManager.getNSDKCommunicator(context, ExternalCommunicatorType.USB, listener);
                        ((MainActivity)context).showMessage("USB communicator is set.", MessageTag.NORMAL);
                    } else if (rbBluetooth.isChecked()) {
                        Intent intent = new Intent(context, BTActivity.class);
                        context.startActivity(intent);
                        return;
                    }

                    setCurrentCommunicator(communicator);
                    ((MainActivity)context).showMessage("Connecting...", MessageTag.NORMAL);
                    connect(communicator);
                } catch (NSDKException e) {
                    e.printStackTrace();
                    ((MainActivity)context).showMessage(String.format("Failed to set connect type. %s(%d)", e.getMessage(), e.getCode()), MessageTag.ERROR);
                }
            }
        });
    }

    public void disconnect() {
        if (currentCommunicator != null) {
            try {
                currentCommunicator.close(10000);
            } catch (NSDKException e) {
                e.printStackTrace();
                ((MainActivity)context).showMessage(String.format("Failed to disconnect. %s(%d)", e.getMessage(), e.getCode()), MessageTag.ERROR);
                return;
            }
        }

//        ((MainActivity)context).showMessage("Disconnected.", MessageTag.NORMAL);
    }

    public void connect(NSDKCommunicator communicator){
        SDKExecutors.threadStart(() -> {
            try {
                communicator.open(20000);
                moduleManager.initExternalModules();
                ((MainActivity)context).showMessage("Device is ready.", MessageTag.NORMAL);
            } catch (NSDKException e) {
                e.printStackTrace();
                ((MainActivity)context).showMessage(String.format("Failed to connect. %s(%d)", e.getMessage(), e.getCode()), MessageTag.ERROR);
            }
        });
    }

    public void ping() {
        long startTime = System.currentTimeMillis();
        boolean ret = moduleManager.ping();
        ((MainActivity)context).showMessage(String.format("Ping result: %s, %dms", ret, (System.currentTimeMillis() - startTime)), MessageTag.NORMAL);
    }
}
