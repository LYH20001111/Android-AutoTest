package com.hudou.autotest.report.excel;

import static com.hudou.autotest.base.activity.AutoTestMainActivity.resultItemList;

import com.hudou.autotest.base.activity.AutoTestMainActivity;
import com.hudou.autotest.fragment.AutoTestSettingFragment;

import java.io.File;
import java.util.Date;
import java.util.LinkedHashMap;

public class ReportOutput {
    public static String excelPath = "还未输出测试报告";
    private String filePath = AutoTestSettingFragment.getReportPath();
    private final static String excel2003L = ".xls";    //2003- 版本的excel
    private final static String excel2007U = ".xlsx";  //2007版本
    public final static String[] formats = new String[]{excel2003L, excel2007U};
    private static final String INVALID_CHARACTERS_REGEX = "[<>:\"/\\\\|?*]";
    private final LinkedHashMap<String, String[]> sheetMap = new LinkedHashMap<String, String[]>() {{
        if (AutoTestSettingFragment.isEnglishReport()) {
            put("TestCase Results Summary", new String[]{"Test Item", "Total Num", "Pass Num", "Fail Num", "Pass Rate", "Start Time", "End Time", "Total Time"});
            put("TestCase Result Details", new String[]{"Test Item", "CaseName", "Result", "Chinese Description", "English Description", "Detail"});
        } else {
            put("测试案例结果汇总", new String[]{"案例测试项", "案例总数", "案例通过数", "案例失败数", "通过率", "开始时间", "结束时间", "总时长"});
            put("测试案例结果详情", new String[]{"案例测试项", "案例号", "测试结果", "中文案例描述", "英文案例描述", "案例详情"});
        }
    }};

    public boolean outputExcel(String prefix, int index) {
        String time = ExcelUtils.stampToDate(String.valueOf(new Date().getTime()));
        File dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (prefix == null
                || prefix.contains(File.separator)
                || prefix.contains(".")
                || prefix.matches(".*" + INVALID_CHARACTERS_REGEX + ".*")) {
            prefix = "";
        }
        filePath = filePath + prefix + "TestReport_" + time + formats[index];
        ExcelUtils.initExcel(filePath, sheetMap);
        boolean isSuccess = ExcelUtils.writeDataToExcel(resultItemList, filePath, AutoTestMainActivity.getContext());
        if (isSuccess) {
            excelPath = filePath;
        }
        return isSuccess;
    }


}
