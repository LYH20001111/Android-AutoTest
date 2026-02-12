package com.newland.sdk.module.bluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.newland.buildtask.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothActivity extends Activity {

    private BluetoothAdapter mBluetoothAdapter;

    private DeviceAdapter mBondedAdapter;
    private DeviceAdapter mAvailableAdapter;

    private LinearLayout mContainer;  // 所有的布局
    private LinearLayout mLlConnectDevice;  // 已连接设备

    private BroadcastReceiver mDiscoverReceiver;

    private TextView mTvTips;   // "正在打开蓝牙" 提示
    private TextView mTvConnectDevice;  // 已连接设备
    private TextView mTvDeviceTip;  // 已连接设备后面提示
    private Switch mSwitch;
    private int mPosition;

    private boolean isConnecting = false;  // 正在连接判断

    private static BluetoothInterface sBluetoothInterface;

    public static void start(Context context, BluetoothInterface bluetoothInterface) {
        sBluetoothInterface = bluetoothInterface;
        Intent intent = new Intent(context, BluetoothActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mContainer = findViewById(R.id.ll_container);
        mLlConnectDevice = findViewById(R.id.ll_connect_device);
        mTvTips = findViewById(R.id.tv_tips);
        mTvConnectDevice = findViewById(R.id.tv_connect_device);
        mTvDeviceTip = findViewById(R.id.tv_device_tip);

        initSwitch();

        initConnectDevice();
        initBondedDevices();
        initAvailableDevices();
    }

    // 显示已连接的设备
    private void initConnectDevice() {
        if (mBluetoothAdapter.isEnabled()) {
            boolean ret = sBluetoothInterface.isConnected();
            String connectedDeviceName = sBluetoothInterface.getDeviceName();
            if (ret && !TextUtils.isEmpty(connectedDeviceName)) {
                mLlConnectDevice.setVisibility(View.VISIBLE);
                mTvConnectDevice.setText(connectedDeviceName);
            } else {
                mLlConnectDevice.setVisibility(View.GONE);
            }
            mLlConnectDevice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CommonUtils.isFastClick()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(BluetoothActivity.this)
                                .setTitle(R.string.connect_device)
                                .setMessage(sBluetoothInterface.getDeviceName())
                                .setNegativeButton(R.string.disconnect, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        mLlConnectDevice.setVisibility(View.GONE);
                                                    }
                                                });
                                                sBluetoothInterface.disconnect();
//                                                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                                    @Override
//                                                    public void run() {
//                                                        BluetoothManager.getInstance(BluetoothActivity.this).getOnConnectListener().onDisconnected();
//                                                    }
//                                                });
                                                initBondedDevices();
                                            }
                                        }).start();
                                    }
                                });
                        builder.create().show();
                    }
                }
            });
        }
    }

    // 显示已经配对的设备
    private void initBondedDevices() {
        isConnecting = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView rvBondedDevices = findViewById(R.id.rv_bonded_devices);
                List<BluetoothDevice> list = getBondedDevices();
                mBondedAdapter = new DeviceAdapter(BluetoothActivity.this, list);
                rvBondedDevices.setLayoutManager(new LinearLayoutManager(BluetoothActivity.this));
                rvBondedDevices.setAdapter(mBondedAdapter);

                mBondedAdapter.setOnItemClickListener(new DeviceAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(final View view, final int position) {
                        if (CommonUtils.isFastClick()) {
                            final BluetoothDevice device = mBondedAdapter.getBTDeviceList().get(position);
                            final String deviceName = TextUtils.isEmpty(device.getName()) ? device.getAddress() : device.getName();
                            AlertDialog.Builder builder = new AlertDialog.Builder(BluetoothActivity.this)
                                    .setTitle(R.string.paired_device)
                                    .setMessage(getString(R.string.device_name) + deviceName)
                                    .setPositiveButton(R.string.connect, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (isConnecting) {
                                                CommonUtils.showToast(BluetoothActivity.this, getString(R.string.connecting));
                                                return;
                                            }

                                            if (device.getAddress().equals(sBluetoothInterface.getDeviceName())) {
                                                CommonUtils.showToast(BluetoothActivity.this, deviceName + "\n" + getString(R.string.connected));
                                                return;
                                            }

                                            cancelDiscovery();

                                            final TextView textView = view.findViewById(R.id.tv_connecting);
                                            textView.setVisibility(View.VISIBLE);

                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    isConnecting = true;
                                                    final boolean ret = sBluetoothInterface.startConnect(deviceName, device.getAddress());
                                                    isConnecting = false;

                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            textView.setVisibility(View.GONE);
                                                            if (ret) {
                                                                CommonUtils.showToast(BluetoothActivity.this, deviceName + "\n" + getString(R.string.connect_success));
                                                                mLlConnectDevice.setVisibility(View.VISIBLE);
                                                                mTvConnectDevice.setText(deviceName);
//                                                                finish();
//                                                                BluetoothManager.getInstance(BluetoothActivity.this).getOnConnectListener().onConnected(deviceName, device.getAddress());
                                                            } else {
                                                                CommonUtils.showToast(BluetoothActivity.this, deviceName + "\n" + getString(R.string.connect_failed));
//                                                                BluetoothManager.getInstance(BluetoothActivity.this).getOnConnectListener().onFailed(CommonUtils.ErrorCode.CONNECT_FAILED, getString(R.string.connect_failed));
                                                            }
                                                        }
                                                    });
                                                }
                                            }).start();
                                        }
                                    })
                                    .setNegativeButton(R.string.cancel_save, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            try {
                                                BluetoothDevice bluetoothDevice = getBondedDevices().get(position);
                                                Method method = bluetoothDevice.getClass().getMethod("removeBond");
                                                method.invoke(bluetoothDevice);
                                                mBondedAdapter.delete(position);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                            builder.create().show();
                        }
                    }
                });
            }
        });
    }

    // 显示可用的设备
    private void initAvailableDevices() {
        isConnecting = false;

        registerDiscoverReceiver();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView rvAvailableDevices = findViewById(R.id.rv_available_devices);
                mAvailableAdapter = new DeviceAdapter(BluetoothActivity.this);
                rvAvailableDevices.setLayoutManager(new LinearLayoutManager(BluetoothActivity.this));
                rvAvailableDevices.setAdapter(mAvailableAdapter);
                mAvailableAdapter.setOnItemClickListener(new DeviceAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {
                        if (CommonUtils.isFastClick()) {
                            if (isConnecting) {
                                CommonUtils.showToast(BluetoothActivity.this, getString(R.string.connecting));
                                return;
                            }

                            cancelDiscovery();
                            final TextView textView = view.findViewById(R.id.tv_connecting);
                            textView.setVisibility(View.VISIBLE);

                            final BluetoothDevice device = mAvailableAdapter.getDevice(position);
                            String name = device.getName();
                            final String address = device.getAddress();

                            final String deviceName = TextUtils.isEmpty(name) ? address : name;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    isConnecting = true;
                                    final boolean ret = sBluetoothInterface.startConnect(deviceName, address);
                                    isConnecting = false;

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            textView.setVisibility(View.GONE);
                                            if (ret) {

                                                try {
                                                    Method method = device.getClass().getMethod("createBond");
                                                    method.invoke(device);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                                                    CommonUtils.showToast(BluetoothActivity.this, deviceName + "\n" + getString(R.string.connect_success));
                                                    mAvailableAdapter.delete(position);
                                                    mBondedAdapter.add(device);
                                                    mLlConnectDevice.setVisibility(View.VISIBLE);
                                                    mTvConnectDevice.setText(deviceName);
//                                                    BluetoothManager.getInstance(BluetoothActivity.this).getOnConnectListener().onConnected(deviceName, device.getAddress());
                                                    cancelDiscovery();
//                                                    finish();
                                                } else {
                                                    mPosition = position;
                                                }
                                            } else {
                                                CommonUtils.showToast(BluetoothActivity.this, deviceName + "\n" + getString(R.string.connect_failed));
//                                                BluetoothManager.getInstance(BluetoothActivity.this).getOnConnectListener().onFailed(CommonUtils.ErrorCode.CONNECT_FAILED, getString(R.string.connect_failed));
                                            }
                                        }
                                    });
                                }
                            }).start();
                        }
                    }
                });
            }
        });
    }

    // 获取已配对的设备
    private List<BluetoothDevice> getBondedDevices() {
        Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();
        return new ArrayList<>(bondedDevices);
    }

    private void initSwitch() {
        mSwitch = findViewById(R.id.bt_switch);
        if (mBluetoothAdapter.isEnabled()) {
            mSwitch.setChecked(true);
            mContainer.setVisibility(View.VISIBLE);
        } else {
            mContainer.setVisibility(View.GONE);
        }
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mBluetoothAdapter.enable();
                    mTvTips.setVisibility(View.VISIBLE);
                    mSwitch.setEnabled(false);

                    initAvailableDevices();
                } else {
                    cancelDiscovery();
                    unregisterDiscoverReceiver();

                    if (mAvailableAdapter != null) {
                        mAvailableAdapter.clear();
                    }
                    mBluetoothAdapter.disable();
                    mContainer.setVisibility(View.GONE);

                    // 关闭蓝牙时把连接状态设为false
//                    ModuleManager.getInstance().setConnected(false);
                }
            }
        });
    }

    // 注册蓝牙广播
    private void registerDiscoverReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED); // 蓝牙状态值发生改变
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND); // 发现远程设备
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED); // 设备连接状态改变
//        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED); // 与远程设备断开连接
        if (mDiscoverReceiver == null) {
            mDiscoverReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);

                    if (bluetoothState == BluetoothAdapter.STATE_ON) {
                        mBluetoothAdapter.startDiscovery();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTvTips.setVisibility(View.GONE);
                                mContainer.setVisibility(View.VISIBLE);
                                mSwitch.setEnabled(true);
                            }
                        });

                        initConnectDevice();
                        initBondedDevices();
                        initAvailableDevices();
                    }

                    if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                        final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!getBondedDevices().contains(device)) {
                                    mAvailableAdapter.add(device);
                                }
                            }
                        });
                    } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(intent.getAction())) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!TextUtils.isEmpty(sBluetoothInterface.getDeviceName())) {
                                    mLlConnectDevice.setVisibility(View.VISIBLE);
                                    mTvConnectDevice.setText(sBluetoothInterface.getDeviceName());
                                }
                            }
                        });
//                        finish();
                    }else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction())) {

                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                            CommonUtils.showToast(BluetoothActivity.this, device.getName() + "\n" + getString(R.string.connect_success));
                            mAvailableAdapter.delete(mPosition);
                            mBondedAdapter.add(device);
                            mLlConnectDevice.setVisibility(View.VISIBLE);
                            mTvConnectDevice.setText(device.getAddress());
//                            BluetoothManager.getInstance(BluetoothActivity.this).getOnConnectListener().onConnected(device.getName(), device.getAddress());
                            cancelDiscovery();
                        }
                    }
                }
            };

            registerReceiver(mDiscoverReceiver, filter);
            mBluetoothAdapter.startDiscovery();
        }
    }

    @Override
    protected void onDestroy() {
        cancelDiscovery();
        unregisterDiscoverReceiver();
        super.onDestroy();
    }

    private void cancelDiscovery() {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    private void unregisterDiscoverReceiver() {
        if (mDiscoverReceiver != null) {
            unregisterReceiver(mDiscoverReceiver);
            mDiscoverReceiver = null;
        }
    }
}
