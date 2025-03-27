package com.hudou.autotest.report.excel;

import static com.hudou.autotest.base.activity.AutoTestMainActivity.resultItemList;

import android.os.Build;

import com.hudou.autotest.base.activity.AutoTestMainActivity;
import com.hudou.autotest.constant.Config;
import com.hudou.autotest.constant.ResultItem;
import com.hudou.autotest.util.ReflectionUtils;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class ReportOutput {
    public static String excelPath = "还未输出测试报告";
    private String filePath = ReflectionUtils.getConfig(Config.REPORT_PATH);
    private final static String excel2003L =".xls";    //2003- 版本的excel
    private final static String excel2007U =".xlsx";  //2007版本
    private final TreeMap<String, String[]> sheetMap = new TreeMap<String, String[]>(){{
        put("测试案例结果汇总", new String[]{"案例测试项", "案例总数", "案例通过数", "案例失败数", "通过率", "开始时间", "结束时间", "总时长"});
        put("测试案例结果详情", new String[]{"案例测试项", "案例号", "测试结果", "中文案例描述", "英文案例描述", "案例详情"});
    }};

    public boolean outputExcel(){
        String time = ExcelUtils.stampToDate(String.valueOf(new Date().getTime()));
        File dir = new File(filePath);
        if (!dir.exists()){
            dir.mkdirs();
        }
        filePath = filePath + "TestReport_" + time + excel2007U;
        ExcelUtils.initExcel(filePath, sheetMap);
        boolean isSuccess = ExcelUtils.writeDataToExcel(resultItemList, filePath, AutoTestMainActivity.getContext());
        if (isSuccess){
            excelPath = filePath;
        }
        return isSuccess;
    }


}
