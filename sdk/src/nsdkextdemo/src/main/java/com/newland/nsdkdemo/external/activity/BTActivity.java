package com.newland.nsdkdemo.external.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.utils.LogLevel;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.external.communication.CommunicatorListener;
import com.newland.nsdk.core.api.external.communication.ExternalCommunicatorState;
import com.newland.nsdk.core.api.external.communication.ExternalCommunicatorType;
import com.newland.nsdk.core.api.external.communication.NSDKCommunicator;
import com.newland.nsdk.core.external.ExtNSDKModuleManagerImpl;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.SDKExecutors;
import com.newland.nsdkdemo.external.ExtInitiator;
import com.newland.nsdkdemo.external.adapter.BTDevicesAdapter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class BTActivity extends Activity {
    private static final int REQUEST_PERMISSION_ACCESS_LOCATION = 1;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final String TAG = "BTActivity";
    private static final int CONNECT_FAILED = 0;
    private static final int CONNECT_SUCCESS = 1;
    private static final int BOND_BONDING = 2;
    private static final int BOND_NONE = 3;

    private Switch switchBt;
    private TextView tvSwitch;
    private Button btnRefresh;
    private LinearLayout llBtDevices;
    private RecyclerView connectedRecyclerView;
    private RecyclerView availableRecyclerView;
    private RadioButton rbBluetooth;
    private RadioButton rbBLE;

    private BluetoothAdapter bluetoothAdapter;
    private BTDevicesAdapter connectedDevicesAdapter;
    private BTDevicesAdapter availableDevicesAdapter;
    private BluetoothReceiver bluetoothReceiver;

    ArrayList<BluetoothDevice> availableBluetoothDevices;

    Method removeBondMethod;

    AlertDialog paringDialog;

    private ExtNSDKModuleManagerImpl moduleManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        availableBluetoothDevices = new ArrayList<>();
        bluetoothReceiver = new BluetoothReceiver();

        moduleManager = ExtNSDKModuleManagerImpl.getInstance();
        moduleManager.setDebugMode(LogLevel.VERBOSE);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.tv_extcomm_pairing).setCancelable(false);
        paringDialog = builder.create();

        try {
            removeBondMethod = BluetoothDevice.class.getMethod("removeBond", (Class[]) null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        registerBluetoothReceiver();

        initUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothAdapter != null && bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        unregisterReceiver(bluetoothReceiver);
    }

    protected void registerBluetoothReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        registerReceiver(bluetoothReceiver, intentFilter);
    }

    private void initUI() {
        tvSwitch = findViewById(R.id.tv_bt_swith);
        switchBt = findViewById(R.id.switch_bt);
        btnRefresh = findViewById(R.id.btn_bt_refresh);
        btnRefresh.setOnClickListener(v -> {refreshUI();});

        rbBluetooth = findViewById(R.id.rb_bluetooth);
        rbBLE = findViewById(R.id.rb_ble);

        llBtDevices = findViewById(R.id.ll_bt_devices);
        switchBt.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                tvSwitch.setText(R.string.tv_extcomm_pair_switch_on);
                if (!bluetoothAdapter.isEnabled()) {
                    Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(mIntent, REQUEST_ENABLE_BT);
                }
            } else {
                tvSwitch.setText(R.string.tv_extcomm_pair_switch_off);
                if (bluetoothAdapter.isEnabled()) {
                    bluetoothAdapter.disable();
                }
            }
        });

        if (bluetoothAdapter == null) {
            switchBt.setChecked(false);
            switchBt.setEnabled(false);
        } else {
            if (bluetoothAdapter.isEnabled()) {
                switchBt.setChecked(true);
                tvSwitch.setText(R.string.tv_extcomm_pair_switch_on);
                llBtDevices.setVisibility(View.VISIBLE);

                initConnectedDevices();
                initAvailableDevices();
                startSearch();
            } else {
                switchBt.setChecked(false);
                tvSwitch.setText(R.string.tv_extcomm_pair_switch_off);
                llBtDevices.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void initConnectedDevices() {
        connectedRecyclerView = findViewById(R.id.rv_connected_devices);
        connectedRecyclerView.setLayoutManager(new LinearLayoutManager(BTActivity.this, LinearLayoutManager.VERTICAL, false));
        connectedRecyclerView.addItemDecoration(new DividerItemDecoration(BTActivity.this, DividerItemDecoration.VERTICAL));
        setConnectedAdapter();
    }

    private void setConnectedAdapter() {
        ArrayList<BluetoothDevice> bluetoothDevices = getConnectedBluetoothDevices();
        connectedDevicesAdapter = new BTDevicesAdapter(bluetoothDevices);
        connectedDevicesAdapter.setOnItemClickListener(new BTDevicesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BluetoothDevice device, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BTActivity.this);
                builder.setTitle(String.format("%s(%s)", device.getName(), device.getAddress()))
                        .setMessage(R.string.tv_extcomm_disconnect_the_device)
                        .setPositiveButton(R.string.dialog_ok, (dialog, id) -> {
                            try {
                                NSDKCommunicator communicator = ExtInitiator.getInstance().getCurrentCommunicator();
                                if (communicator != null) {
                                    communicator.close(10000);
                                }
                                connectedDevicesAdapter.removeDevice(device);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        })
                        .setNegativeButton(R.string.dialog_btn_cancel, (dialog, id) -> {
                        });
                builder.create().show();
            }

            @Override
            public void onItemLongClick(BluetoothDevice device, int position) {

            }
        });

        connectedRecyclerView.setAdapter(connectedDevicesAdapter);
    }

    private ArrayList<BluetoothDevice> getConnectedBluetoothDevices() {
        ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<>();
        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice d:devices) {
            if (isConnected(d)) {
                bluetoothDevices.add(d);
            }
        }
        return bluetoothDevices;
    }

    private void initAvailableDevices() {
        availableRecyclerView = findViewById(R.id.rv_available_devices);
        availableRecyclerView.setLayoutManager(new LinearLayoutManager(BTActivity.this, LinearLayoutManager.VERTICAL, false));
        availableRecyclerView.addItemDecoration(new DividerItemDecoration(BTActivity.this, DividerItemDecoration.VERTICAL));

        setAvailableAdapter();
    }

    private void setAvailableAdapter() {
        availableDevicesAdapter = new BTDevicesAdapter(new ArrayList<>());
        ArrayList<BluetoothDevice> pairedDevices = getPairedBluetoothDevices();
        availableDevicesAdapter.addDevices(pairedDevices);
        availableDevicesAdapter.setOnItemClickListener(new BTDevicesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BluetoothDevice device, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BTActivity.this);
                builder.setTitle(String.format("%s(%s)", device.getName(), device.getAddress()))
                        .setMessage(R.string.tv_extcomm_pair_the_device)
                        .setPositiveButton(R.string.dialog_ok, (dialog, id) -> {
                            if (bluetoothAdapter != null && bluetoothAdapter.isDiscovering()) {
                                bluetoothAdapter.cancelDiscovery();
                            }
                            ArrayList<BluetoothDevice> pairedDevices = getPairedBluetoothDevices();
                            if (pairedDevices.contains(device)) {
                                Message msg = new Message();
                                msg.what = BOND_BONDING;
                                handler.sendMessage(msg);
                                startConnect(device);
                            } else {
//                                if (rbBLE.isChecked()) {
//                                    // 30SU can be connected directly when BLE
//                                    startConnect(device);
//                                } else {
//                                    device.createBond();
//                                }
                                device.createBond();
                            }
                        })
                        .setNegativeButton(R.string.dialog_btn_cancel, (dialog, id) -> {
                        });
                builder.create().show();
            }

            @Override
            public void onItemLongClick(BluetoothDevice device, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BTActivity.this);
                builder.setTitle(String.format("%s(%s)", device.getName(), device.getAddress()))
                        .setMessage(R.string.tv_extcomm_forget_the_device)
                        .setPositiveButton(R.string.dialog_ok, (dialog, id) -> {
                            if (removeBondMethod != null) {
                                try {
                                    removeBondMethod.invoke(device, (Object[]) null);
                                    availableDevicesAdapter.removeDevice(device);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton(R.string.dialog_btn_cancel, (dialog, id) -> {
                        });
                builder.create().show();
            }
        });

        availableRecyclerView.setAdapter(availableDevicesAdapter);
    }

    private ArrayList<BluetoothDevice> getPairedBluetoothDevices() {
        ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<>();
        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice d : devices) {
            if (isConnected(d)) {
                continue;
            }
            bluetoothDevices.add(d);
        }
        return bluetoothDevices;
    }

    private void startSearch() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkAccessFinePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (checkAccessFinePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSION_ACCESS_LOCATION);
                // Wait for permission
                return;
            }
        }
        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
            }

            bluetoothAdapter.startDiscovery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_ACCESS_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startSearch();
                }
                break;
            default:
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode != RESULT_OK) {
                switchBt.setChecked(false);
            }
        }
    }

    class BluetoothReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            switch (action) {
                case BluetoothDevice.ACTION_FOUND:
                    if (device != null && !bluetoothAdapter.getBondedDevices().contains(device)) {
                        availableDevicesAdapter.addDevice(device);
                    }
                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                    Message msg = new Message();
                    switch (bondState) {
                        case BluetoothDevice.BOND_NONE:
                            msg.what = BOND_NONE;
                            handler.sendMessage(msg);
                            break;
                        case BluetoothDevice.BOND_BONDING:
                            msg.what = BOND_BONDING;
                            handler.sendMessage(msg);
                            break;
                        case BluetoothDevice.BOND_BONDED:
                            LogUtils.d(TAG, ">>> BOND_BONDED start connect");
                            startConnect(device);
                            break;
                        default:
                            break;
                    }
                    break;
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int btState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (btState == BluetoothAdapter.STATE_ON) {
                        refreshUI();
                    } else if (btState == BluetoothAdapter.STATE_OFF) {
                        bluetoothAdapter.cancelDiscovery();

                        llBtDevices.setVisibility(View.INVISIBLE);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void refreshUI() {
        if (connectedDevicesAdapter == null) {
            initConnectedDevices();
            initAvailableDevices();
        }
        connectedDevicesAdapter.clearDevices();
        connectedDevicesAdapter.addDevices(getConnectedBluetoothDevices());
        availableDevicesAdapter.clearDevices();
        availableDevicesAdapter.addDevices(getPairedBluetoothDevices());

        startSearch();
    }

    private void startConnect(BluetoothDevice device) {
        // Disconnect other connection first.
        if (ExtInitiator.getInstance().getCurrentCommunicator() != null) {
            try {
                ExtInitiator.getInstance().getCurrentCommunicator().close(10000);
            } catch (NSDKException e) {
                e.printStackTrace();
            }
        }

        NSDKCommunicator communicator = null;
        try {
            LogUtils.d(TAG, String.format("*&# Start to connect %s(%s)", device.getName(), device.getAddress()));
            CommunicatorListener listener = new CommunicatorListener() {
                @Override
                public BluetoothDevice onBluetoothList(ArrayList<BluetoothDevice> arrayList) {
                    LogUtils.d(TAG, String.format("*&# onBluetoothList %s(%s)", device.getName(), device.getAddress()));
                    return device;
                }

                @Override
                public void onConnectedStateChange(ExternalCommunicatorState externalCommunicatorState) {
                    LogUtils.d(getClass().getName(), "onConnectedStateChange>>>" + externalCommunicatorState);
                    LogUtils.d(TAG, "onConnectedStateChange: " + externalCommunicatorState);
                }
            };
            if (rbBluetooth.isChecked()) {
                communicator = moduleManager.getNSDKCommunicator(BTActivity.this, ExternalCommunicatorType.BLUETOOTH_CLASSIC, listener);
            } else if (rbBLE.isChecked()) {
                communicator = moduleManager.getNSDKCommunicator(BTActivity.this, ExternalCommunicatorType.BLUETOOTH_LOW_ENERGY, listener);
            }
            if (communicator != null) {
                ExtInitiator.getInstance().setCurrentCommunicator(communicator);
                SDKExecutors.threadStart(() -> {
                    try {
                        ExtInitiator.getInstance().getCurrentCommunicator().open(20000);
                        moduleManager.initExternalModules();

                        // Sleep some time to make sure the state of previous connected bluetooth device is completely disconnected
                        // so that it can be removed from connected device list when refreshing UI.
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Message msg = new Message();
                        msg.what = CONNECT_SUCCESS;
                        msg.obj = device;
                        handler.sendMessage(msg);
                    } catch (NSDKException e) {
                        e.printStackTrace();
                        onConnectFailed(device, e);
                    }
                });
            }
        } catch (NSDKException e) {
            e.printStackTrace();
            onConnectFailed(device, e);
        }
    }

    private void onConnectFailed(BluetoothDevice device, NSDKException e) {
        Message msg = new Message();
        msg.what = CONNECT_FAILED;
        msg.obj = e;
        handler.sendMessage(msg);
    }

    private Handler handler = new Handler(Looper.getMainLooper(), message -> {
        switch (message.what) {
            case CONNECT_SUCCESS:
                refreshUI();
                if (paringDialog.isShowing()) {
                    paringDialog.dismiss();
                }
                break;
            case CONNECT_FAILED:
                if (paringDialog.isShowing()) {
                    paringDialog.dismiss();
                }
                NSDKException e = (NSDKException) message.obj;
                Toast.makeText(BTActivity.this,String.format("Failed to connect: %s(%d)", e.getMessage(), e.getCode()), Toast.LENGTH_SHORT).show();
                break;
            case BOND_BONDING:
                if (!paringDialog.isShowing()) {
                    paringDialog.show();
                }
                break;
            case BOND_NONE:
                if (paringDialog.isShowing()) {
                    paringDialog.dismiss();
                }
                break;
            default:
                break;
        }
        return true;
    });

    public static boolean isConnected(BluetoothDevice device) {
        if (device == null) {
            return false;
        }

        try {
            Method m = device.getClass().getMethod("isConnected", (Class[]) null);
            return (boolean) m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}

