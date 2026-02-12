package com.newland.nsdkdemo.external.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.newland.nsdkdemo.R;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class BTDevicesAdapter extends RecyclerView.Adapter<BTDevicesAdapter.BTViewHolder>{

    private ArrayList<BluetoothDevice> bluetoothDevices;
    private OnItemClickListener listener;
    public static final int ADD_DEVCIE = 0;
    public static final int ADD_DEVCIES = 1;
    public static final int REMOVE_DEVCIE = 2;
    public static final int CLEAR_DEVCIES = 3;

    private Handler handler = new Handler(Looper.getMainLooper(), message -> {
        switch (message.what) {
            case ADD_DEVCIE:
                BluetoothDevice device = (BluetoothDevice) message.obj;
                if (!bluetoothDevices.contains(device)) {
                    bluetoothDevices.add(device);
                }
                break;
            case ADD_DEVCIES:
                ArrayList<BluetoothDevice> devices = (ArrayList<BluetoothDevice>) message.obj;
                if (devices != null && devices.size() > 0) {
                    for (BluetoothDevice d : devices) {
                        if (bluetoothDevices.contains(d)) {
                            continue;
                        }
                        bluetoothDevices.add(d);
                    }
                }
                break;
            case REMOVE_DEVCIE:
                bluetoothDevices.remove(message.obj);
                break;
            case CLEAR_DEVCIES:
                bluetoothDevices.clear();
                break;
            default:
                break;
        }
        notifyDataSetChanged();
        return true;
    });

    public BTDevicesAdapter(ArrayList<BluetoothDevice> bondedDevices) {
        bluetoothDevices = bondedDevices;
    }

    @NonNull
    @Override
    public BTViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycleview_bt_row_item, viewGroup, false);

        return new BTViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BTViewHolder viewHolder, int position) {
        BluetoothDevice device = bluetoothDevices.get(position);
        String address = device.getAddress();
        viewHolder.getTvBtMacAddress().setText(address);
        String deviceName = device.getName();
        viewHolder.getTvBtName().setText(TextUtils.isEmpty(deviceName) ? address : deviceName);
        viewHolder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(device, position);
            }
        });
        viewHolder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onItemLongClick(device, position);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return bluetoothDevices.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void removeDevice(BluetoothDevice device) {
        Message msg = new Message();
        msg.what = REMOVE_DEVCIE;
        msg.obj = device;
        handler.sendMessage(msg);
    }

    public void clearDevices() {
        Message msg = new Message();
        msg.what = CLEAR_DEVCIES;
        handler.sendMessage(msg);
    }

    public void addDevice(BluetoothDevice device) {
        Message msg = new Message();
        msg.what = ADD_DEVCIE;
        msg.obj = device;
        handler.sendMessage(msg);
    }

    public void addDevices(ArrayList<BluetoothDevice> devices) {
        Message msg = new Message();
        msg.what = ADD_DEVCIES;
        msg.obj = devices;
        handler.sendMessage(msg);
    }

    public interface OnItemClickListener {
        void onItemClick(BluetoothDevice device, int position);
        void onItemLongClick(BluetoothDevice device, int position);
    }

    public static class BTViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvBtMacAddress;
        private final TextView tvBtName;

        public BTViewHolder(@NonNull View itemView) {
            super(itemView);

            tvBtMacAddress = itemView.findViewById(R.id.tv_bt_mac_address);
            tvBtName = itemView.findViewById(R.id.tv_bt_name);
        }

        public TextView getTvBtMacAddress() {
            return tvBtMacAddress;
        }

        public TextView getTvBtName() {
            return tvBtName;
        }
    }
}
