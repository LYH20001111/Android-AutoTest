package com.newland.sdk.module.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.newland.buildtask.R;

import java.util.ArrayList;
import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private Context mContext;
    private List<BluetoothDevice> mBTDeviceList;
    private LayoutInflater mLayoutInflater;
    private OnItemClickListener mOnItemClickListener;

    public DeviceAdapter(Context context) {
        mContext = context;
        mBTDeviceList = new ArrayList<>();
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public DeviceAdapter(Context context, List<BluetoothDevice> btDeviceList) {
        mContext = context;
        mBTDeviceList = btDeviceList;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new DeviceViewHolder(mLayoutInflater.inflate(R.layout.bluetooth_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final DeviceViewHolder holder, int position) {
        String deviceName = mBTDeviceList.get(position).getName();
        String deviceAddress = mBTDeviceList.get(position).getAddress();
        String name = TextUtils.isEmpty(deviceName) ? deviceAddress : deviceName;
        holder.deviceName.setText(name);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (mOnItemClickListener != null) {
                   mOnItemClickListener.onItemClick(holder.itemView, holder.getAdapterPosition());
               }
           }
       });
    }

    @Override
    public int getItemCount() {
        return mBTDeviceList.size();
    }

    static class DeviceViewHolder extends RecyclerView.ViewHolder {

        private TextView deviceName, tipConnecting;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.tv_device_name);
            tipConnecting = itemView.findViewById(R.id.tv_connecting);
        }
    }

    public BluetoothDevice getDevice(int position) {
        return mBTDeviceList.get(position);
    }

    public void add(BluetoothDevice btDevice) {
        for (int i = 0; i < mBTDeviceList.size(); i++) {
            if (mBTDeviceList.get(i).getAddress().equals(btDevice.getAddress())) {
                return;
            }
        }
        mBTDeviceList.add(btDevice);
        notifyDataSetChanged();
    }

    public void delete(int position) {
        mBTDeviceList.remove(position);
        notifyDataSetChanged();
    }

    public void clear() {
        mBTDeviceList.clear();
        notifyDataSetChanged();
    }

    public List<BluetoothDevice> getBTDeviceList() {
        return mBTDeviceList;
    }

    interface OnItemClickListener {

        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
