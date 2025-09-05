package com.hudou.autotest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hudou.autotest.R;
import com.hudou.autotest.constant.ChildModel;
import com.hudou.autotest.constant.GroupModel;

import java.util.ArrayList;
import java.util.List;

public class MyExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<GroupModel> groupList;
    private ArrayList<ArrayList<ChildModel>> childList;

    public MyExpandableListAdapter(Context context, List<GroupModel> groupList, ArrayList<ArrayList<ChildModel>> childList) {
        this.context = context;
        this.groupList = groupList;
        this.childList = childList;
    }

    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childList.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupModel groupModel = (GroupModel) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.auto_test_group_item, null);
        }
        //ImageView groupIcon = convertView.findViewById(R.id.group_icon);
        TextView groupTextView = convertView.findViewById(R.id.group_name);
        groupTextView.setText(groupModel.getGroupName());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildModel childModel = (ChildModel) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.auto_test_child_item, parent, false);
        }
        ImageView childIcon = convertView.findViewById(R.id.child_icon);
        TextView childTextView = convertView.findViewById(R.id.child_name);
        childIcon.setImageResource(childModel.getChildIcon());
        childTextView.setText(childModel.getChildName());
        childTextView.setTextColor(childModel.getColor());
        // 根据条件设置子项为不可点击
//        if (AutoTestSettingFragment.getEditCap() == AutoTestSettingFragment.EditCap.OFF
//                && childPosition == 0
//                && groupPosition == 0) {
//            convertView.setClickable(false);
//            convertView.setFocusable(false);
//            convertView.setEnabled(false);
//            // 设置触摸监听器，拦截触摸事件
//            convertView.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    // 拦截所有触摸事件
//                    return true;
//                }
//            });
//        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
