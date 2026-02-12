package com.newland.sdk.ble;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.newland.NlBluetooth.control.BluetoothController;
import com.newland.buildtask.R;
import com.newland.sdk.me.module.externalPininput.BleBasePackage;
import com.newland.sdk.me.utils.PreferenceUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BluetoothBaseActivity extends Activity {

    private final String TAG = this.getClass().getName();
    private final String ACTION_CONNECT_SUCCESS = "com.newland.landlinecom.action.CONNECT_SUCCESS";
    private final String ACTION_CONNECT_FAILED = "com.newland.landlinecom.action.CONNECT_FAILED";
    private final String ACTION_LIST_VISIBILITY = "com.newland.landlinecom.action.LIST_VISIBILITY";

    private Context mContext;
    private boolean mGard; //列表是否可操作
    private boolean mConnecting;

    private MyAdapter mValidDevicesAdapter;
    private MyAdapter mBondedDevicesAdapter;
    private MyAdapter mConnectedDevicesAdapter;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothReceiver1 mReceiver;

    private Switch mBluetoothSwitch;
    private TextView mValidDevicesTitleTextView;
    private TextView mBondedDevicesTitleTextView;
    private TextView mConnectedDevicesTitleTextView;
    private ListView mValidDevicesListView;
    private ListView mBondedDevicesListView;
    private ListView mConnectedDevicesListView;
    private int CancelMsg = -1;
    private int OKMsg = 1;
    private String BLE_NAME = "BLE_NAME";
    private String BLE_ADDRESS = "BLE_ADDRESS";
    //广播接受者标识
    private boolean mReceiverTag = false;
    private BroadcastReceiver mDiscoverReceiver;
    private ProgressDialog discoveryDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetoothbase);

        mContext =this;
        mGard = false;
        mConnecting = false;

        mValidDevicesListView = (ListView) findViewById(R.id.list_valid_devices);
        mBondedDevicesListView = (ListView) findViewById(R.id.list_bonded_devices);
        mConnectedDevicesListView = (ListView) findViewById(R.id.list_connected_devices);
        mValidDevicesAdapter = new MyAdapter(mContext, mValidDevicesListView);
        mBondedDevicesAdapter = new MyAdapter(mContext, mBondedDevicesListView);
        mConnectedDevicesAdapter = new MyAdapter(mContext, mConnectedDevicesListView);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter!=null && !mBluetoothAdapter.isEnabled()){
            Toast.makeText(mContext, mContext.getString(R.string.bluetooth_turn_on), Toast.LENGTH_SHORT).show();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//蓝牙状态值发生改变
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);//发现远程设备
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//设备连接状态改变
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);//与远程设备断开连接
        filter.addAction(ACTION_CONNECT_SUCCESS);
        filter.addAction(ACTION_CONNECT_FAILED);
        filter.addAction(ACTION_LIST_VISIBILITY);
        mReceiver = new BluetoothReceiver1();
        if (!mReceiverTag) {
            registerReceiver(mReceiver, filter);
            mReceiverTag = true;
        }


        mValidDevicesTitleTextView = (TextView) findViewById(R.id.text_valid_devices_title);
        mBondedDevicesTitleTextView = (TextView) findViewById(R.id.text_bonded_devices_title);
        mConnectedDevicesTitleTextView = (TextView) findViewById(R.id.text_connected_devices_title);

        mBluetoothSwitch = (Switch) findViewById(R.id.swt_bluetooth);
        mBluetoothSwitch.setChecked(mBluetoothAdapter.isEnabled());
        mBluetoothSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBluetoothSwitch.setEnabled(false);
                if (isChecked) {
                    mBluetoothAdapter.enable();//开启蓝牙
                } else {
                    BluetoothController.getInstance().disconnect();
                    mBluetoothAdapter.disable();
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (mBluetoothAdapter.isEnabled()) {
                    initView();
                }
            }
        });

        mValidDevicesListView.setAdapter(mValidDevicesAdapter);
        mValidDevicesListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mGard) {
                    return;
                }
                mGard = true;
                final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(
                        mValidDevicesAdapter.getItem(position).get("mac"));
                mBluetoothAdapter.cancelDiscovery();


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String name = device.getName();
                        String adress = device.getAddress();
                        if(name==null){
                            name = adress;
                        }
                        try {
                            Log.e(TAG,"Connecting bluetooth name:"+name+"; address:"+adress);
                            PreferenceUtils.setString(getApplicationContext(),BLE_NAME,name);
                            PreferenceUtils.setString(getApplicationContext(),BLE_ADDRESS,adress);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        try {
                            Log.e(TAG,"sendEmptyMessage name:"+name+"; address:"+adress);
                            BleBasePackage.getCancelHandler().sendEmptyMessage(OKMsg);
                        }catch (Exception e){
                            e.printStackTrace();
                        }catch (Error r){
                            r.printStackTrace();
                        }
                        finish();
                    }
                }).start();
            }
        });

        mBondedDevicesListView.setAdapter(mBondedDevicesAdapter);
        mBondedDevicesListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG,"bonded_devices onItemClick mGard:"+mGard);
                if (mGard) {
                    return;
                }
                final int pos = position;
                final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mBondedDevicesAdapter.getItem(position).get("mac"));
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(mContext.getString(R.string.bonded_devices));
                builder.setMessage(mContext.getString(R.string.device_name) + mBondedDevicesAdapter.getItem(position).get("name"));
                Log.i(TAG,"bonded devices:"+mBondedDevicesAdapter.getItem(position).get("name"));

                builder.setPositiveButton(mContext.getString(R.string.connect), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!mConnectedDevicesAdapter.isEmpty()) {
                            Log.e(TAG,"connected");
                            Toast.makeText(mContext, mContext.getString(R.string.connected), Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                        mGard = true;
                        mBluetoothAdapter.cancelDiscovery();
                        mBondedDevicesAdapter.updateMessage(pos, mContext.getString(R.string.connecting));

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String name = device.getName();
                                String adress = device.getAddress();
                                if(name==null){
                                    name = adress;
                                }
                                Log.e(TAG,"Connecting a paired device name:"+name+"; address:"+adress);
                                PreferenceUtils.setString(getApplicationContext(),BLE_NAME,name);
                                PreferenceUtils.setString(getApplicationContext(),BLE_ADDRESS,adress);

                                try {
                                    BleBasePackage.getCancelHandler().sendEmptyMessage(OKMsg);
                                }catch (Exception e){

                                }catch (Error r){}
                                finish();
                            }
                        }).start();

                    }
                });
                builder.setNegativeButton(mContext.getString(R.string.cancel_save), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mGard = true;
                        try {
                            Method removeBondMethod = device.getClass().getMethod("removeBond");
                            removeBondMethod.invoke(device);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                try {
                    builder.show();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        mConnectedDevicesListView.setAdapter(mConnectedDevicesAdapter);
        mConnectedDevicesListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mGard) {
                    return;
                }
                final int pos = position;
                final EditText deviceNameEditText = new EditText(mContext);
                deviceNameEditText.setText(mConnectedDevicesAdapter.getItem(position).get("name"));
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(mContext.getString(R.string.connect_device));
                builder.setView(deviceNameEditText);
                builder.setNegativeButton(mContext.getString(R.string.disconnect), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mGard = true;
                        mConnectedDevicesAdapter.updateMessage(pos, mContext.getString(R.string.disconnecting));
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                BluetoothController.getInstance().disconnect();
                            }
                        });
                        thread.start();
                    }
                });
                builder.setPositiveButton(mContext.getString(R.string.bluetooth_rename), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            int nameLen = deviceNameEditText.getText().toString().getBytes("GBK").length;
                            if (nameLen > 29) {
                                Toast.makeText(mContext, mContext.getString(R.string.bluetooth_name_length_tip), Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        mGard = true;
                        mConnectedDevicesAdapter.updateMessage(pos, mContext.getString(R.string.bluetooth_renaming));
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                BluetoothController.getInstance().btSetLocalName(deviceNameEditText.getText().toString());
                            }
                        });
                        thread.start();
                    }
                });
                builder.show();
            }
        });
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.e(TAG,"Bluetooth cancel");
                    BleBasePackage.getCancelHandler().sendEmptyMessage(CancelMsg);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    finish();
                }
            }
        });
        if (mBluetoothAdapter.isEnabled()) {
            initView();
        }
        registerDiscoverReceiver();

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
                    }

                    if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                        final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Map<String, String> item = new HashMap<String, String>();
                                String name = device.getName();
                                String address = device.getAddress();
                                if(name==null || name.equals("")){
                                    name = address;
                                }
                                Log.i(TAG,"Find the bluetooth name:"+name+";address:"+address);
                                item.put("name", name);
                                item.put("mac", address);
                                if (mValidDevicesAdapter!=null && mValidDevicesAdapter.indexOfMac(item.get("mac")) == -1) {
                                    mValidDevicesAdapter.add(item);
                                }
                            }
                        }
                     );
                    } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(intent.getAction())) {
                        Log.i(TAG,"Bluetooth connection successful");
                        finish();
                    }else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction())) {

                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                        }
                    }
                }
            };

            registerReceiver(mDiscoverReceiver, filter);
            mBluetoothAdapter.startDiscovery();
        }
    }


    private class BluetoothReceiver1 extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String actionString = intent.getAction();
            if (ACTION_LIST_VISIBILITY.equals(actionString)) {
                // 控制已连接设备、已配对设备、可用设备ListView的可见性
                int id = intent.getIntExtra("view", 0);
                int visibility = intent.getIntExtra("visibility", 0);
                if (id == R.id.list_valid_devices) {
                    mValidDevicesTitleTextView.setVisibility(visibility);

                } else if (id == R.id.list_bonded_devices) {
                    mBondedDevicesTitleTextView.setVisibility(visibility);

                } else if (id == R.id.list_connected_devices) {
                    mConnectedDevicesTitleTextView.setVisibility(visibility);

                } else {
                }
            }
        }
    }

    private void initView() {
        updateBondedDevicesList();
        Log.e(TAG, "Is it connected?:" + BluetoothController.getInstance().isConnectedA());
        if (!BluetoothController.getInstance().isConnectedA()) {
            boolean ret = mBluetoothAdapter.startDiscovery();
            Log.e(TAG, "initView startDiscovery: " + ret);
        }
    }

    private void updateBondedDevicesList() {
        Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();
        mBondedDevicesAdapter.clear();
        for (BluetoothDevice bluetoothDevice : bondedDevices) {
            Map<String, String> item = new HashMap<String, String>();
            item.put("name", bluetoothDevice.getName() == null ? bluetoothDevice.getAddress() : bluetoothDevice.getName());
            item.put("mac", bluetoothDevice.getAddress());
            String address = BluetoothController.getInstance().getConnectedDeviceAddressA();

            if (address != null) {
                if (BluetoothController.getInstance().getConnectedDeviceAddressA().equals(bluetoothDevice.getAddress())) {
                    mConnectedDevicesAdapter.add(item);
                    return;
                }
            }
            mBondedDevicesAdapter.add(item);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public class MyAdapter extends BaseAdapter {

        private List<Map<String, String>> list;
        private LayoutInflater inflater;
        private ListView listView;

        public MyAdapter(Context context, ListView lv) {
            list = new ArrayList<Map<String, String>>();
            inflater = LayoutInflater.from(context);
            listView = lv;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Map<String, String> getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.bluetooth_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.name);
                viewHolder.messageTextView = (TextView) convertView.findViewById(R.id.message);
                viewHolder.mactTextView = (TextView) convertView.findViewById(R.id.mac);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Map<String, String> item = list.get(position);
            viewHolder.nameTextView.setText(item.get("name") == null ? "" : item.get("name"));
            viewHolder.messageTextView.setText(item.get("message") == null ? "" : item.get("message"));
            viewHolder.mactTextView.setText(item.get("mac") == null ? "" : item.get("mac"));
            return convertView;
        }

        public void add(Map<String, String> item) {
            if (list.size() == 0) {
                Intent intent = new Intent(ACTION_LIST_VISIBILITY);
                intent.putExtra("view", listView.getId());
                intent.putExtra("visibility", View.VISIBLE);
                sendBroadcast(intent);
            }
            list.add(item);
            notifyDataSetChanged();
        }

        public void updateName(int index, String name) {
            int visiblePosition = listView.getFirstVisiblePosition();
            View view = listView.getChildAt(index - visiblePosition);
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.nameTextView = (TextView) view.findViewById(R.id.name);
            holder.nameTextView.setText(name);
        }

        public void updateMessage(int index, String message) {
            int visiblePosition = listView.getFirstVisiblePosition();
            final View view = listView.getChildAt(index - visiblePosition);
            final ViewHolder holder = (ViewHolder) view.getTag();
            holder.messageTextView = (TextView) view.findViewById(R.id.message);
            holder.messageTextView.setText(message);
            holder.messageTextView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (holder.messageTextView.getLineCount() > 1) {
                        holder.nameTextView = (TextView) view.findViewById(R.id.name);
                        holder.nameTextView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0f));
                    }
                    holder.messageTextView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }

        public void remove(int position) {
            list.remove(position);
            if (list.size() == 0) {
                Intent intent = new Intent(ACTION_LIST_VISIBILITY);
                intent.putExtra("view", listView.getId());
                intent.putExtra("visibility", View.GONE);
                sendBroadcast(intent);
            }
            notifyDataSetChanged();
        }

        public void clear() {
            list.clear();
            Intent intent = new Intent(ACTION_LIST_VISIBILITY);
            intent.putExtra("view", listView.getId());
            intent.putExtra("visibility", View.GONE);
            sendBroadcast(intent);
            notifyDataSetChanged();
        }

        public int indexOfMac(String mac) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).get("mac").equals(mac)) {
                    return i;
                }
            }
            return -1;
        }
    }

    class ViewHolder {
        TextView nameTextView;
        TextView messageTextView;
        TextView mactTextView;
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterDiscoverReceiver();
        }catch (Exception e){
            e.printStackTrace();
        }catch (Error e){

        }
        if (mReceiverTag) {
            mReceiverTag = false;
            unregisterReceiver(mReceiver);
            mBluetoothAdapter.cancelDiscovery();
        }
        mValidDevicesAdapter = null;
        mContext = null;
        mBluetoothAdapter = null;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            try {
                BleBasePackage.getCancelHandler().sendEmptyMessage(CancelMsg);
            }catch (Exception e){
                e.printStackTrace();
            }catch (Error r){}
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void unregisterDiscoverReceiver() {
        try {
            if (mDiscoverReceiver != null) {
                unregisterReceiver(mDiscoverReceiver);
                mBluetoothAdapter.cancelDiscovery();
                mDiscoverReceiver = null;
            }
        }catch (Exception e){

        }catch (Error r){

        }
    }

//    private void discoveryDialogShow() {
//        discoveryDialogDismiss();
//        discoveryDialog = new ProgressDialog(this);
//        discoveryDialog.setTitle(mContext.getString(R.string.valid_devices));
//        discoveryDialog.setMessage(mContext.getString(R.string.bluetooth_discovering));
//        discoveryDialog.setIndeterminate(true);//是否形成一个加载动画,true表示不明确加载进度形成转圈动画,false表示明确加载进度
//        discoveryDialog.setCancelable(false);//点击返回键或者dialog四周是否关闭dialog,true表示可以关闭,false表示不可关闭
//        discoveryDialog.show();
//    }
//    private void discoveryDialogDismiss(){
//        if(discoveryDialog != null){
//            discoveryDialog.dismiss();
//            discoveryDialog = null;
//        }
//    }
}
