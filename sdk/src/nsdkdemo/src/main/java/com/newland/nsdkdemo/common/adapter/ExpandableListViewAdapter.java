package com.newland.nsdkdemo.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.newland.nsdkdemo.R;

import java.util.List;
import java.util.Map;

public class ExpandableListViewAdapter extends BaseExpandableListAdapter {

    private List<ExpandableListViewParent> mParentList;
    private Map<ExpandableListViewParent, List<ExpandableListViewSon>> mSonList;
    private Context context;

    public ExpandableListViewAdapter(Context context, Map<ExpandableListViewParent, List<ExpandableListViewSon>> sonListMap, List<ExpandableListViewParent> parentList) {
        this.context = context;
        mSonList = sonListMap;
        mParentList = parentList;
    }

    @Override
    public Object getChild(int parentPos, int childPos) {
        return mSonList.get(mParentList.get(parentPos)).get(childPos);
    }

    @Override
    public int getGroupCount() {
        if (mParentList == null) {
            return 0;
        }
        return mParentList.size();
    }

    @Override
    public int getChildrenCount(int parentPos) {
        if (mSonList.get(mParentList.get(parentPos)) == null) {
            return 0;
        }
        return mSonList.get(mParentList.get(parentPos)).size();
    }

    @Override
    public Object getGroup(int parentPos) {
        return mParentList.get(parentPos);
    }

    @Override
    public long getGroupId(int parentPos) {
        return parentPos;
    }

    @Override
    public long getChildId(int parentPos, int childPos) {
        return childPos;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int parentPos, boolean isExpanded, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        GroupHolder holder = null;
        if (view == null) {
            holder = new GroupHolder();
            view = LayoutInflater.from(context).inflate(R.layout.exlistview_parent_item, null);
            holder.groupName = view.findViewById(R.id.id_parent_title);
            holder.arrow = view.findViewById(R.id.id_parent_arrow);
            view.setTag(holder);
        } else {
            holder = (GroupHolder) view.getTag();
        }
        try {
            if (isExpanded) {
                holder.arrow.setBackgroundResource(R.drawable.expandlist_expand_sel);
            } else {
                holder.arrow.setBackgroundResource(R.drawable.expandlist_expand);
            }
            holder.groupName.setText(mParentList.get(parentPos).bean.groupname + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public View getChildView(int parentPos, int childPos, boolean b, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        ChildHolder holder = null;
        if (view == null) {
            holder = new ChildHolder();
            view = LayoutInflater.from(context).inflate(R.layout.exlistview_child_item, null);
            holder.childName = view.findViewById(R.id.id_name);
            holder.finddesc = view.findViewById(R.id.id_finddesc);
            view.setTag(holder);
        } else {
            holder = (ChildHolder) view.getTag();
        }
        try {
            holder.childName.setText(mSonList.get(mParentList.get(parentPos)).get(childPos).bean.childname + "");
            holder.finddesc.setText(mSonList.get(mParentList.get(parentPos)).get(childPos).bean.finddesc + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    class GroupHolder {
        public TextView groupName;
        public ImageView arrow;
    }

    class ChildHolder {
        public TextView childName;
        public TextView finddesc;
    }
}
