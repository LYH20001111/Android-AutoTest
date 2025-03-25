package com.hudou.autotest.report.excel;

import static com.hudou.autotest.base.activity.AutoTestMainActivity.resultItemList;

import android.os.Build;

import com.hudou.autotest.base.activity.AutoTestMainActivity;
import com.hudou.autotest.constant.ResultItem;

import java.io.File;
import java.util.Date;
import java.util.List;

public class ReportOutput {
    public static String excelPath;
    private String[] titleName = {"案例测试项", "案例号", "测试结果", "中文案例描述", "英文案例描述", "案例详情"};
    private String filePath = "/sdcard/auto_test/report/";
    public boolean outputExcel(){
        String time = ExcelUtils.stampToDate(String.valueOf(new Date().getTime()));
        File dir = new File(filePath);
        if (!dir.exists()){
            dir.mkdirs();
        }
        filePath = filePath + File.separator + "TestReport_" + time + ".xlsx";
        ExcelUtils.initExcel(filePath, titleName);
        boolean isSuccess = ExcelUtils.writeDataToExcel(resultItemList, filePath, AutoTestMainActivity.getContext());
        if (isSuccess){
            excelPath = filePath;
        }
        return isSuccess;
    }


}
