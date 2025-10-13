package com.hudou.autotest.fragment;

import static com.hudou.autotest.base.activity.AutoTestMainActivity.resultData;
import static com.hudou.autotest.base.activity.AutoTestMainActivity.resultItemList;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.hudou.autotest.BuildConfig;
import com.hudou.autotest.R;
import com.hudou.autotest.adapter.MyExpandableListAdapter;
import com.hudou.autotest.annotation.Function;
import com.hudou.autotest.annotation.Navigation;
import com.hudou.autotest.annotation.TestItem;
import com.hudou.autotest.base.activity.AutoTestMainActivity;
import com.hudou.autotest.base.fragment.BaseFragment;
import com.hudou.autotest.constant.ChildModel;
import com.hudou.autotest.constant.Config;
import com.hudou.autotest.constant.EditCap;
import com.hudou.autotest.constant.FunctionType;
import com.hudou.autotest.constant.GroupModel;
import com.hudou.autotest.constant.ResultData;
import com.hudou.autotest.constant.ResultItem;
import com.hudou.autotest.constant.SettingFunction;
import com.hudou.autotest.constant.TableItem;
import com.hudou.autotest.databinding.AutoTestBaseSettingFragmentBinding;
import com.hudou.autotest.fragment.listener.SettingInterface;
import com.hudou.autotest.listener.MyOnClickListener;
import com.hudou.autotest.report.excel.ReportOutput;
import com.hudou.autotest.ui.dialog.listener.NotifyOptionDialogListener;
import com.hudou.autotest.util.PermissionUtil;
import com.hudou.autotest.util.ReflectionUtils;
import com.hudou.autotest.util.SharedPreferencesUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Navigation(name = "设置")
public class AutoTestSettingFragment extends BaseFragment<AutoTestBaseSettingFragmentBinding> implements SettingInterface {
    private static String REPORT_PATH;
    private String TESTFILES_PATH;
    private static EditCap editCap = EditCap.OFF;
    private static boolean isEnglishReport = false;
    private List<String> fileDirList;
    private String[] permission = new String[]{"读写外部存储权限"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fileDirList = addAssetsDirs();
        String reportPath = onSetReportPath();
        if (reportPath != null && !reportPath.isEmpty()) {
            REPORT_PATH = reportPath;
        } else {
            REPORT_PATH = ReflectionUtils.getConfig(Config.REPORT_PATH);
        }

        String testFilesPath = onSetTestFilesPath();
        if (testFilesPath != null && !testFilesPath.isEmpty()) {
            TESTFILES_PATH = testFilesPath;
        } else {
            TESTFILES_PATH = ReflectionUtils.getConfig(Config.LOAD_FILES_PATH);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAddActions() {

    }

    @Override
    public List<String> addAssetsDirs() {
        return new ArrayList<String>() {{
            add(ReflectionUtils.getConfig(Config.DEFAULT_FILE_DIR));
        }};
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
    public String onAddReportNamePrefix() {
        return "";
    }

    @Override
    public void onInitData() {
        super.onInitData();
        dealDebugMode();
        dealExportReport();
        dealReport();
        dealPermission();
        addFunctionLayouts(viewBinding.getRoot().findViewById(R.id.ll_additional_setting));
    }

    @Override
    public void onActionAfterInitData() {
        super.onActionAfterInitData();
        onAddActions();
    }

    private void dealDebugMode() {
        viewBinding.swDebug.setChecked(SharedPreferencesUtil.get(SharedPreferencesUtil.DEBUG_MODE, true));
        viewBinding.swDebug.setOnCheckedChangeListener((compoundButton, isChecked) -> SharedPreferencesUtil.save(SharedPreferencesUtil.DEBUG_MODE, isChecked));
    }

    private void dealExportReport() {
        viewBinding.llExportReport.setOnClickListener(new MyOnClickListener() {

            @Override
            public void dealClick(View v) {
                Dialog.singleChoiceDialog(getActivity(), R.string.select_report_format_dialog_title, new String[]{".xlsx", ".txt"}, id -> {
                    if (id == 0) {

                    } else {

                    }
                });
            }
        });
    }

    private interface Group {
        int TEST_REPORT = 0;
        int LOAD_FILES = 1;
        int OTHERS = 2;
    }

    private interface ReportChild {
        int REPORT_PATH = 0;
        int RECORDING_TESTING = 1;
        int RECORD_SUMMARY = 2;
        int OUTPUT_REPORT = 3;
        int VIEW_XLSX_FILE_NAME = 4;
        int CLEAN_RECORDS = 5;
    }

    private interface FilesChild {
        int FILES_PATH = 0;
        int LOAD_FILES = 1;
    }

    private interface OthersChild {
        int STRUCTURE_VERSION = 0;
    }

    private void dealReport() {
        @SuppressLint("ResourceType")
        MyExpandableListAdapter myExpandableListAdapter = new MyExpandableListAdapter(getContext(),
                new ArrayList<GroupModel>() {{
                    add(new GroupModel(getResourceString(R.string.group_test_report)));
                    add(new GroupModel(getResourceString(R.string.group_load_files)));
                    add(new GroupModel(getResourceString(R.string.group_others)));
                }},
                new ArrayList<ArrayList<ChildModel>>() {{
                    add(new ArrayList<ChildModel>() {{
                        add(new ChildModel(android.R.drawable.ic_menu_edit, getResourceString(R.string.child_report_path) + REPORT_PATH, Color.GRAY));
                        add(new ChildModel(android.R.drawable.ic_menu_save, getResourceString(R.string.child_recording), Color.GRAY));
                        add(new ChildModel(android.R.drawable.ic_menu_search, getResourceString(R.string.child_record_summary)));
                        add(new ChildModel(android.R.drawable.ic_menu_save, getResourceString(R.string.child_output_report)));
                        add(new ChildModel(android.R.drawable.ic_menu_view, getResourceString(R.string.child_view_report_name)));
                        add(new ChildModel(android.R.drawable.ic_menu_delete, getResourceString(R.string.child_clean_records), Color.RED));
                    }});
                    add(new ArrayList<>(Arrays.asList(
                                    new ChildModel(android.R.drawable.ic_menu_edit, getResourceString(R.string.child_files_path) + TESTFILES_PATH, Color.GRAY),
                                    new ChildModel(android.R.drawable.ic_menu_save, getResourceString(R.string.child_load_files))
                            ))
                    );
                    add(new ArrayList<ChildModel>(){{
                        add(new ChildModel(android.R.drawable.ic_menu_view, getResourceString(R.string.child_structure_version) + BuildConfig.StructureVersion, Color.GRAY));
                    }});
                }}
        );
        viewBinding.elvReport.setAdapter(myExpandableListAdapter);
        viewBinding.elvReport.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                GroupModel groupModel = (GroupModel) myExpandableListAdapter.getGroup(groupPosition);
                String groupName = groupModel.getGroupName();
                if (parent.isGroupExpanded(groupPosition)) {
                    ImageView groupNavigate = v.findViewById(R.id.group_navigate);
                    groupNavigate.setImageResource(R.drawable.auto_test_navigate_next);
                } else {
                    ImageView groupNavigate = v.findViewById(R.id.group_navigate);
                    groupNavigate.setImageResource(R.drawable.auto_test_navigate_down);
                }
                return false;
            }
        });

        viewBinding.elvReport.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                ChildModel childModel = (ChildModel) myExpandableListAdapter.getChild(groupPosition, childPosition);
                switch (groupPosition) {
                    case Group.TEST_REPORT:
                        switch (childPosition) {
                            case ReportChild.REPORT_PATH:
                                if (getEditCap() == EditCap.OFF) {
                                    Toast.makeText(getContext(), R.string.no_editting, Toast.LENGTH_SHORT).show();
                                    return true;
                                }
                                Dialog.editDialog(getContext(), getString(R.string.edit_report_path), false, message -> {
                                    REPORT_PATH = message;
                                    TextView tvChildName = v.findViewById(R.id.child_name);
                                    getActivity().runOnUiThread(() -> {
                                        tvChildName.setText(getResourceString(R.string.child_report_path) + REPORT_PATH);
                                        //动态保存
                                        childModel.setChildName(getResourceString(R.string.child_report_path) + REPORT_PATH);
                                        myExpandableListAdapter.notifyDataSetChanged();
                                    });
                                });
                                break;
                            case ReportChild.RECORDING_TESTING:
                                break;
                            case ReportChild.RECORD_SUMMARY:
                                List<TableItem> items = new ArrayList<>();
                                for (ResultItem resultItem : resultItemList) {
                                    items.add(new TableItem(ReflectionUtils.getAnnotationValue(resultItem.getClz(), TestItem.class, TestItem.Members.name),
                                            countFail(resultItem.getResultDataList()), resultItem.getResultDataList().size()));
                                }
                                Dialog.createTableDialog(getContext(), getString(R.string.record_summary), items);
                                break;
                            case ReportChild.OUTPUT_REPORT:
                                Dialog.singleChoiceDialog(getActivity(), R.string.test_report_format_selection, ReportOutput.formats, index -> {
                                    if (index < 0) {
                                        return;
                                    }
                                    if (PermissionUtil.checkReadWritePermission(getActivity())) {
                                        Dialog.outputDialog(getActivity(), onAddReportNamePrefix(), index);
                                    } else {
                                        Toast.makeText(getContext(), R.string.no_read_write_permission, Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            case ReportChild.VIEW_XLSX_FILE_NAME:
                                Dialog.notifyDialog(getContext(), ReportOutput.excelPath);
                                break;
                            case ReportChild.CLEAN_RECORDS:
                                Dialog.notifyOptionsDialog(getContext(), getString(R.string.clean_records_title), new NotifyOptionDialogListener() {
                                    @Override
                                    public void onPositive() {
                                        resultData = null;
                                        resultItemList = new CopyOnWriteArrayList<>();
                                        new Thread(() -> {
                                            AutoTestMainActivity.getDb().dao().clearAll();
                                        }).start();
                                        Toast.makeText(getContext(), R.string.success, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onNegative() {
                                        Toast.makeText(getContext(), R.string.cancelled, Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            default:
                                break;
                        }
                        break;
                    case Group.LOAD_FILES:
                        switch (childPosition) {
                            case FilesChild.FILES_PATH:
                                if (getEditCap() == EditCap.OFF) {
                                    Toast.makeText(getContext(), R.string.no_editting, Toast.LENGTH_SHORT).show();
                                    return true;
                                }
                                Dialog.editDialog(getContext(), getString(R.string.edit_files_path), false, message -> {
                                    TESTFILES_PATH = message;
                                    TextView tvChildName = v.findViewById(R.id.child_name);
                                    getActivity().runOnUiThread(() -> {
                                        tvChildName.setText(getResourceString(R.string.child_files_path) + TESTFILES_PATH);
                                        //动态保存
                                        childModel.setChildName(getResourceString(R.string.child_files_path) + TESTFILES_PATH);
                                        myExpandableListAdapter.notifyDataSetChanged();
                                    });
                                });
                                break;
                            case FilesChild.LOAD_FILES:
                                if (PermissionUtil.checkReadWritePermission(getActivity())) {
                                    if (fileDirList != null && !fileDirList.isEmpty()) {
                                        Dialog.loadingFilesDialog(getActivity(), fileDirList, TESTFILES_PATH);
                                    }
                                } else {
                                    Toast.makeText(getContext(), R.string.no_read_write_permission, Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                break;
                        }
                        break;
                    case Group.OTHERS:
                        switch (childPosition) {
                            case OthersChild.STRUCTURE_VERSION:
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

    private void dealPermission() {
        viewBinding.llPermissionSetting.setOnClickListener(new MyOnClickListener() {
            @Override
            public void dealClick(View v) {
                Dialog.multiChoiceDialog(getContext(), R.string.permission_dialog_title, permission, choiceList -> {
                    for (Integer index : choiceList) {
                        switch (index) {
                            case 0:
                                PermissionUtil.requestReadWritePermission(getActivity());
                                break;
                            default:
                                break;
                        }
                    }
                });
            }
        });
    }

    private static int countFail(List<ResultData> resultDataList) {
        int count = 0;
        for (ResultData resultData : resultDataList) {
            if ("测试失败".equals(resultData.getResult())) {
                count++;
            }
        }
        return count;
    }

    private void addFunctionLayouts(LinearLayout parent) {
        Method[] methods = Arrays.stream(getClass().getDeclaredMethods())
                .sorted(Comparator.comparing(Method::getName))
                .toArray(Method[]::new);

        for (Method method : methods) {
            if (method.isAnnotationPresent(Function.class)) {
                viewBinding.tvAdditionFunctionTitle.setVisibility(View.VISIBLE);
                viewBinding.llAdditionalSetting.setVisibility(View.VISIBLE);

                Function function = method.getAnnotation(Function.class);
                String title = function.title();
                FunctionType type = function.type();

                LinearLayout functionLayout = new LinearLayout(getContext());
                functionLayout.setOrientation(LinearLayout.VERTICAL);
                functionLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                functionLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.auto_test_ripple_effect));

                // 创建新的 LinearLayout
                LinearLayout textViewLayout = new LinearLayout(getContext());
                textViewLayout.setOrientation(LinearLayout.HORIZONTAL);
                textViewLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                LinearLayout.LayoutParams functionLayoutLayoutParams = (LinearLayout.LayoutParams) textViewLayout.getLayoutParams();
                functionLayoutLayoutParams.leftMargin = 20;
                functionLayoutLayoutParams.rightMargin = 20;
                functionLayoutLayoutParams.topMargin = 20;
                textViewLayout.setLayoutParams(functionLayoutLayoutParams);
                textViewLayout.setMinimumHeight(30);

                switch (type) {
                    case SWITCH: {
                        LinearLayout container = new LinearLayout(getContext());
                        container.setOrientation(LinearLayout.HORIZONTAL);
                        container.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        ));

                        TextView textView = new TextView(getContext());
                        textView.setText(title);
                        textView.setTextColor(Color.BLACK);
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        textView.setTypeface(null, Typeface.BOLD);
                        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                                0,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                1
                        );
                        textView.setLayoutParams(textParams);

                        Switch switchBtn = new Switch(getContext());
                        switchBtn.setText("");
                        //switchBtn.setChecked(true);

                        switchBtn.setThumbTintList(null);
                        switchBtn.setTrackTintList(null);
                        switchBtn.setThumbResource(R.drawable.auto_test_switch_custom_thumb_selector);
                        switchBtn.setTrackResource(R.drawable.auto_test_switch_custom_track_selector);

                        container.addView(textView);
                        container.addView(switchBtn);

                        container.setOnClickListener(v -> {
                            // 切换 Switch 的状态
                            boolean newState = !switchBtn.isChecked();
                            switchBtn.setChecked(newState);
                            // 这里也可以调用 Switch 的监听器里定义的业务逻辑
                        });
                        switchBtn.setOnCheckedChangeListener((b, isChecked) -> {
                            try {
                                method.setAccessible(true);
                                if (method.getParameterTypes().length > 0) {
                                    method.invoke(this, isChecked);   // 有参
                                } else {
                                    method.invoke(this);              // 无参
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        textViewLayout.addView(container);
                        break;
                    }
                    case BUTTON: {
                        // 创建 TextView
                        TextView textView = new TextView(getContext());
                        textView.setLayoutParams(new LinearLayout.LayoutParams(
                                0,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                1));
                        textView.setText(title);
                        textView.setTypeface(null, Typeface.BOLD);
                        textView.setTextSize(20);
                        textView.setTextColor(Color.BLACK);

                        // 将 TextView 添加到 LinearLayout
                        textViewLayout.addView(textView);

                        functionLayout.setOnClickListener(v -> {
                            try {
                                method.setAccessible(true);
                                method.invoke(this);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        });
                        break;
                    }
                    default:
                        break;
                }

                // 创建分隔线
                View divider = new View(getContext());
                divider.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        4));
                divider.setBackgroundColor(Color.parseColor("#dfdfdf"));

                LinearLayout.LayoutParams dividerLayoutParams = (LinearLayout.LayoutParams) divider.getLayoutParams();
                dividerLayoutParams.topMargin = 20;
                divider.setLayoutParams(dividerLayoutParams);

                functionLayout.addView(textViewLayout);
                functionLayout.addView(divider);
                parent.addView(functionLayout);

            }
        }
    }


    private String getResourceString(@StringRes int res) {
        return getContext().getString(res, "");
    }


    public void removeFunction(SettingFunction settingFunction) {
        switch (settingFunction) {
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

    public static EditCap getEditCap() {
        return editCap;
    }

    public void setEditCap(EditCap editCap) {
        AutoTestSettingFragment.editCap = editCap;
    }

    public static boolean isEnglishReport() {
        return isEnglishReport;
    }

    public static String getReportPath() {
        return REPORT_PATH;
    }

    public static void setIsEnglishReport(boolean isEnglishReport) {
        AutoTestSettingFragment.isEnglishReport = isEnglishReport;
    }
}
