package com.hudou.autotest.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hudou.autotest.R;
import com.hudou.autotest.adapter.MyExpandableListAdapter;
import com.hudou.autotest.annotation.Navigation;
import com.hudou.autotest.base.fragment.BaseFragment;
import com.hudou.autotest.customUI.dialog.DialogUtils;
import com.hudou.autotest.constant.ChildModel;
import com.hudou.autotest.constant.GroupModel;
import com.hudou.autotest.customUI.dialog.listener.EditDialogListener;
import com.hudou.autotest.databinding.AutoTestBaseSettingFragmentBinding;
import com.hudou.autotest.fragment.listener.SettingInterface;
import com.hudou.autotest.util.SharedPreferencesUtil;
import com.hudou.autotest.listener.MyOnClickListener;

import java.util.ArrayList;

@Navigation(name = "设置")
public class AutoTestSettingFragment extends BaseFragment<AutoTestBaseSettingFragmentBinding> implements SettingInterface {
    private static String REPORT_PATH = "/sdcard/auto_test/";
    private static String REPORT_NAME = "这个是测试报告名";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        String reportPath = onSetReportPath();
        if (reportPath != null && !reportPath.isEmpty()){
            REPORT_PATH = reportPath;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAddActions() {

    }

    @Override
    public String onSetReportPath() {
        return null;
    }

    public enum SettingFunction{
        BASE_FUNCTION,
        DEBUG_MODE,
        EXPORT_REPORT,
        TEST_REPORT,
    }

    @Override
    public void onInitData() {
        super.onInitData();
        dealDebugMode();
        dealExportReport();
        dealReport();
    }

    @Override
    public void onActionAfterInitData() {
        super.onActionAfterInitData();
        onAddActions();
    }

    private void dealDebugMode(){
        viewBinding.swDebug.setChecked(SharedPreferencesUtil.get(SharedPreferencesUtil.DEBUG_MODE, true));
        viewBinding.swDebug.setOnCheckedChangeListener((compoundButton, isChecked) -> SharedPreferencesUtil.save(SharedPreferencesUtil.DEBUG_MODE, isChecked));
    }

    private void dealExportReport(){
        viewBinding.llExportReport.setOnClickListener(new MyOnClickListener() {

            @Override
            public void dealClick(View v) {
                DialogUtils.createSingleChoiceDialog(getActivity(), R.string.select_report_format_dialog_title, new String[]{".xlsx", ".txt"}, id -> {
                    if (id == 0){

                    }else {

                    }
                });
            }
        });
    }

    private void dealReport(){
        @SuppressLint("ResourceType")
        MyExpandableListAdapter myExpandableListAdapter = new MyExpandableListAdapter(getContext(),
                new ArrayList<GroupModel>() {{
                    add(new GroupModel("测试报告"));
                }},
                new ArrayList<ArrayList<ChildModel>>() {{
                    add(new ArrayList<ChildModel>() {{
                        add(new ChildModel(android.R.drawable.ic_menu_edit,"报告地址: " + REPORT_PATH));
                        add(new ChildModel(android.R.drawable.ic_menu_save,"输出 .xlsx 测试报告"));
                        add(new ChildModel(android.R.drawable.ic_menu_save,"输出 .txt 测试报告"));
                        add(new ChildModel(android.R.drawable.ic_menu_view,"查看测试报告名称"));
                        add(new ChildModel(android.R.drawable.ic_menu_delete,"清空测试记录", Color.RED));}});
                }}
        );
        viewBinding.elvReport.setAdapter(myExpandableListAdapter);
        viewBinding.elvReport.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                GroupModel groupModel = (GroupModel) myExpandableListAdapter.getGroup(groupPosition);
                String groupName = groupModel.getGroupName();
                if (parent.isGroupExpanded(groupPosition)){
                    ImageView groupNavigate = v.findViewById(R.id.group_navigate);
                    groupNavigate.setImageResource(R.drawable.auto_test_navigate_next);
                }else {
                    ImageView groupNavigate = v.findViewById(R.id.group_navigate);
                    groupNavigate.setImageResource(R.drawable.auto_test_navigate_down);
                }
                return false;
            }
        });

        viewBinding.elvReport.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                ChildModel childModel = (ChildModel) myExpandableListAdapter.getChild(groupPosition, childPosition);
                String childName = childModel.getChildName();
                Toast.makeText(getActivity(), "Child clicked: " + childName, Toast.LENGTH_SHORT).show();
                switch (childPosition){
                    case 0:
//                        DialogUtils.createEditTextDialog(getContext(), "您可以自定义测试报告的输出地址", false, new EditDialogListener() {
//                            @Override
//                            public void onResult(String message) {
//                                REPORT_PATH = message;
//                                TextView tvChildName = v.findViewById(R.id.child_name);
//                                tvChildName.setText("报告地址: " + REPORT_PATH);
//                            }
//                        });
                        break;
                    case 3:
                        DialogUtils.createNotifyDialog(getContext(), REPORT_NAME);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }


    public void removeFunction(SettingFunction settingFunction){
        switch (settingFunction){
            case BASE_FUNCTION:
                viewBinding.llBaseFunction.setVisibility(View.GONE);
                break;
            case DEBUG_MODE:
                viewBinding.llDebugMode.setVisibility(View.GONE);
                break;
            case EXPORT_REPORT:
                viewBinding.llExportReport.setVisibility(View.GONE);
                break;
            case TEST_REPORT:
                viewBinding.elvReport.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }


}
