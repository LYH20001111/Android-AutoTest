package com.hudou.autotest.report.excel;

import static com.hudou.autotest.base.activity.AutoTestMainActivity.resultItemList;

import com.hudou.autotest.R;
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
            put(AutoTestMainActivity.getContext().getString(R.string.report_summary_sheet_title_en), new String[]{
                    AutoTestMainActivity.getContext().getString(R.string.report_summary_test_item_en),
                    AutoTestMainActivity.getContext().getString(R.string.report_summary_total_num_en),
                    AutoTestMainActivity.getContext().getString(R.string.report_summary_pass_num_en),
                    AutoTestMainActivity.getContext().getString(R.string.report_summary_abandon_num_en),
                    AutoTestMainActivity.getContext().getString(R.string.report_summary_fail_num_en),
                    AutoTestMainActivity.getContext().getString(R.string.report_summary_pass_rate_en),
                    AutoTestMainActivity.getContext().getString(R.string.report_summary_start_time_en),
                    AutoTestMainActivity.getContext().getString(R.string.report_summary_end_time_en),
                    AutoTestMainActivity.getContext().getString(R.string.report_summary_total_time_en)});
            put(AutoTestMainActivity.getContext().getString(R.string.report_detail_sheet_title_en), new String[]{
                    AutoTestMainActivity.getContext().getString(R.string.report_detail_test_item_en),
                    AutoTestMainActivity.getContext().getString(R.string.report_detail_case_name_en),
                    AutoTestMainActivity.getContext().getString(R.string.report_detail_result_en),
                    AutoTestMainActivity.getContext().getString(R.string.report_detail_chinese_description_en),
                    AutoTestMainActivity.getContext().getString(R.string.report_detail_english_description_en),
                    AutoTestMainActivity.getContext().getString(R.string.report_detail_detail_en)});
        } else {
            put(AutoTestMainActivity.getContext().getString(R.string.report_summary_sheet_title), new String[]{
                    AutoTestMainActivity.getContext().getString(R.string.report_summary_test_item),
                    AutoTestMainActivity.getContext().getString(R.string.report_summary_total_num),
                    AutoTestMainActivity.getContext().getString(R.string.report_summary_pass_num),
                    AutoTestMainActivity.getContext().getString(R.string.report_summary_abandon_num),
                    AutoTestMainActivity.getContext().getString(R.string.report_summary_fail_num),
                    AutoTestMainActivity.getContext().getString(R.string.report_summary_pass_rate),
                    AutoTestMainActivity.getContext().getString(R.string.report_summary_start_time),
                    AutoTestMainActivity.getContext().getString(R.string.report_summary_end_time),
                    AutoTestMainActivity.getContext().getString(R.string.report_summary_total_time)});
            put(AutoTestMainActivity.getContext().getString(R.string.report_detail_sheet_title), new String[]{
                    AutoTestMainActivity.getContext().getString(R.string.report_detail_test_item),
                    AutoTestMainActivity.getContext().getString(R.string.report_detail_case_name),
                    AutoTestMainActivity.getContext().getString(R.string.report_detail_result),
                    AutoTestMainActivity.getContext().getString(R.string.report_detail_chinese_description),
                    AutoTestMainActivity.getContext().getString(R.string.report_detail_english_description),
                    AutoTestMainActivity.getContext().getString(R.string.report_detail_detail)});
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