package com.newland.sdkdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.newland.sdkdemo.R;

import java.util.List;

/**
 * Author by bxy, Date on 2018/11/14 0014.
 */
public class ListViewAdapter extends ArrayAdapter<ListViewItem> {

    public static final int TYPE1 = 1;
    public static final int TYPE2 = 2;
    private int type = TYPE2;
    public ListViewAdapter(Context context, List<ListViewItem> objects) {
        super(context, 0, objects);
    }

    public void setType(int type){
        this.type = type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListViewItem item = getItem(position);
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            if(type == TYPE2){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_item2, null);
                holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
                holder.tvDesc = (TextView) convertView.findViewById(R.id.tvDesc);
            }else{
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_item1, null);
                holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(type == TYPE2){
            holder.tvName.setText(item.title);
            holder.tvDesc.setText(item.desc);
        }else{
            holder.tvName.setText(item.title);
        }
        return convertView;
    }
    private class ViewHolder {
        TextView tvName, tvDesc;
    }
}
