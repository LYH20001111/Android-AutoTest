package com.hudou.autotest.fragment;

import static com.hudou.autotest.base.activity.AutoTestMainActivity.resultData;
import static com.hudou.autotest.base.activity.AutoTestMainActivity.resultItemList;

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
import com.hudou.autotest.constant.EditCap;
import com.hudou.autotest.constant.SettingFunction;
import com.hudou.autotest.customUI.dialog.DialogUtils;
import com.hudou.autotest.constant.ChildModel;
import com.hudou.autotest.constant.GroupModel;
import com.hudou.autotest.customUI.dialog.listener.MultiChoiceDialogListener;
import com.hudou.autotest.customUI.dialog.listener.NotifyOptionDialogListener;
import com.hudou.autotest.databinding.AutoTestBaseSettingFragmentBinding;
import com.hudou.autotest.fragment.listener.SettingInterface;
import com.hudou.autotest.report.excel.ReportOutput;
import com.hudou.autotest.util.PermissionUtil;
import com.hudou.autotest.util.ReflectionUtils;
import com.hudou.autotest.util.SharedPreferencesUtil;
import com.hudou.autotest.listener.MyOnClickListener;

import java.util.ArrayList;
import java.util.List;

@Navigation(name = "设置")
public class AutoTestSettingFragment extends BaseFragment<AutoTestBaseSettingFragmentBinding> implements SettingInterface {
    private String REPORT_PATH;
    private String TESTFILES_PATH;
    private static EditCap editCap = EditCap.OFF;
    private List<String> fileDirList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fileDirList = addAssetsDirs();
        String reportPath = onSetReportPath();
        if (reportPath != null && !reportPath.isEmpty()){
            REPORT_PATH = reportPath;
        }else {
            REPORT_PATH = ReflectionUtils.getConfig("reportPath");
        }

        String testFilesPath = onSetTestFilesPath();
        if (testFilesPath != null && !testFilesPath.isEmpty()){
            TESTFILES_PATH = testFilesPath;
        }else {
            TESTFILES_PATH = ReflectionUtils.getConfig("testFilesPath");
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAddActions() {

    }

    @Override
    public List<String> addAssetsDirs() {
        return new ArrayList<String>(){{add("test");}};
    }

    @Override
    public String onSetReportPath() {
        return null;
    }

    @Override
    public String onSetTestFilesPath() {
        return null;
    }

    @Override
    public void onInitData() {
        super.onInitData();
        dealDebugMode();
        dealExportReport();
        dealReport();
        deadPermission();
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
                    add(new GroupModel("加载应用文件"));
                }},
                new ArrayList<ArrayList<ChildModel>>() {{
                    add(new ArrayList<ChildModel>() {{
                        add(new ChildModel(android.R.drawable.ic_menu_edit,"报告地址: " + REPORT_PATH, Color.GRAY));
                        add(new ChildModel(android.R.drawable.ic_menu_save,"输出 .xlsx 测试报告"));
                        add(new ChildModel(android.R.drawable.ic_menu_save,"实时记录测试 (report.txt) ", Color.GRAY));
                        add(new ChildModel(android.R.drawable.ic_menu_view,"查看测试报告名称"));
                        add(new ChildModel(android.R.drawable.ic_menu_delete,"清空测试记录", Color.RED));}});
                    add(new ArrayList<ChildModel>() {{
                        add(new ChildModel(android.R.drawable.ic_menu_edit,"文件地址: " + TESTFILES_PATH, Color.GRAY));
                        add(new ChildModel(android.R.drawable.ic_menu_save,"加载测试应用文件"));}});
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
                switch (groupPosition){
                    case 0:
                        switch (childPosition){
                            case 0:
                                if (getEditCap() == EditCap.OFF){
                                    Toast.makeText(getContext(), "不可编辑", Toast.LENGTH_SHORT).show();
                                    return true;
                                }
                                DialogUtils.createEditTextDialog(getContext(), "您可以自定义测试报告的输出地址", false, message -> {
                                    REPORT_PATH = message;
                                    TextView tvChildName = v.findViewById(R.id.child_name);
                                    getActivity().runOnUiThread(() -> {
                                        tvChildName.setText("报告地址: " + REPORT_PATH);
                                        //动态保存
                                        childModel.setChildName("报告地址: " + REPORT_PATH);
                                        myExpandableListAdapter.notifyDataSetChanged();
                                    });
                                });
                                break;
                            case 1:
                                if (PermissionUtil.checkReadWritePermission(getActivity())){
                                    if (fileDirList != null && !fileDirList.isEmpty()) {
                                        DialogUtils.outputDialog(getActivity());
                                    }
                                }else {
                                    Toast.makeText(getContext(), "没有对外读写存储权限", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 2:
                                break;
                            case 3:
                                DialogUtils.createNotifyDialog(getContext(), ReportOutput.excelPath);
                                break;
                            case 4:
                                DialogUtils.createNotifyOptionsDialog(getContext(), "您确定要清空测试记录吗？", new NotifyOptionDialogListener() {
                                    @Override
                                    public void onPositive() {
                                        resultData = null;
                                        resultItemList = new ArrayList<>();
                                        Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onNegative() {
                                        Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            default:
                                break;
                        }
                        break;
                    case 1:
                        switch (childPosition){
                            case 0:
                                if (getEditCap() == EditCap.OFF){
                                    Toast.makeText(getContext(), "不可编辑", Toast.LENGTH_SHORT).show();
                                    return true;
                                }
                                DialogUtils.createEditTextDialog(getContext(), "您可以自定义应用测试文件的加载地址", false, message -> {
                                    TESTFILES_PATH = message;
                                    TextView tvChildName = v.findViewById(R.id.child_name);
                                    getActivity().runOnUiThread(() -> {
                                        tvChildName.setText("文件地址: " + TESTFILES_PATH);
                                        //动态保存
                                        childModel.setChildName("文件地址: " + TESTFILES_PATH);
                                        myExpandableListAdapter.notifyDataSetChanged();
                                    });
                                });
                                break;
                            case 1:
                                if (PermissionUtil.checkReadWritePermission(getActivity())){
                                    if (fileDirList != null && !fileDirList.isEmpty()) {
                                        DialogUtils.loadingFilesDialog(getActivity(), fileDirList, TESTFILES_PATH);
                                    }
                                }else {
                                    Toast.makeText(getContext(), "没有对外读写存储权限", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    private void deadPermission(){
        viewBinding.llPermissionSetting.setOnClickListener(new MyOnClickListener() {
            @Override
            public void dealClick(View v) {
                DialogUtils.createMultiChoiceDialog(getContext(), R.string.permission_dialog_title, new String[]{"读写外部存储权限"}, new MultiChoiceDialogListener() {
                    @Override
                    public void onResult(ArrayList<Integer> choiceList) {
                        for(Integer index : choiceList){
                            switch (index){
                                case 0:
                                    PermissionUtil.requestReadWritePermission(getActivity());
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                });
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

    public static EditCap getEditCap(){
        return editCap;
    }

    public void setEditCap(EditCap editCap){
        this.editCap = editCap;
    }



}
