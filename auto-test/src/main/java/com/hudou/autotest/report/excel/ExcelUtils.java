package com.hudou.autotest.report.excel;

import android.content.Context;
import android.os.Environment;


import com.hudou.autotest.constant.ResultData;
import com.hudou.autotest.constant.ResultItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Pattern;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ExcelUtils {
    private static WritableCellFormat fileNameFormat = null;
    private static WritableCellFormat titleFormat = null;
    private static WritableCellFormat contentFormat = null;
    private static WritableCellFormat failResultFormat = null;
    private static WritableCellFormat detailFormat = null;

    public static void initExcel(String fileName, TreeMap<String, String[]> sheetMap) {
        initFormat();
        WritableWorkbook workbook = null;
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            workbook = Workbook.createWorkbook(file);

            int index = 0;
            for (String key : sheetMap.keySet()){
                WritableSheet sheet = workbook.createSheet(key, index);
                for (int j = 0; j < sheetMap.get(key).length; j++){
                    sheet.addCell(new Label(j, 0, sheetMap.get(key)[j], titleFormat));
                }
                sheet.setRowView(0, 500);
                index++;
            }
            workbook.write();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Set parameters such as format font
     */
    private static void initFormat() {
        try {
            WritableFont fileNameFont = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD);
            fileNameFormat = new WritableCellFormat(fileNameFont);
            fileNameFormat.setAlignment(Alignment.CENTRE);
            fileNameFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
            fileNameFormat.setBackground(Colour.VERY_LIGHT_YELLOW);

            WritableFont titleFont = new WritableFont(WritableFont.ARIAL, 16, WritableFont.BOLD);
            titleFormat = new WritableCellFormat(titleFont);
            titleFormat.setAlignment(Alignment.CENTRE);
            titleFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
            titleFormat.setBackground(Colour.GRAY_25);

            WritableFont contentFont = new WritableFont(WritableFont.ARIAL, 12);
            contentFormat = new WritableCellFormat(contentFont);
            contentFormat.setAlignment(Alignment.CENTRE);//居中
            contentFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
            contentFormat.setWrap(true);//自动换行
            contentFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN); //边框

            failResultFormat = new WritableCellFormat(contentFont);
            failResultFormat.setAlignment(Alignment.CENTRE);//居中
            failResultFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
            failResultFormat.setWrap(true);//自动换行
            failResultFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN); //边框
            failResultFormat.setBackground(Colour.RED);//适用于案例失败

            detailFormat = new WritableCellFormat(contentFont);
            detailFormat.setAlignment(Alignment.LEFT);//居左
            detailFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
            detailFormat.setWrap(true);//自动换行
            detailFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN); //边框
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> boolean writeDataToExcel(List<ResultItem> resultItemList, String fileName, Context context) {
        if (resultItemList != null) {
            WritableWorkbook writebook = null;
            InputStream in = null;
            try {
                WorkbookSettings setEncode = new WorkbookSettings();
                setEncode.setEncoding("UTF-8");
                //setEncode.setEncoding("ISO-8859-1");
                writebook = Workbook.createWorkbook(new File(fileName), Workbook.getWorkbook(new FileInputStream(new File(fileName)), setEncode));


                WritableSheet summarySheet = writebook.getSheet(0);
                summarySheet.setColumnView(0, 50); //设置列宽
                summarySheet.setColumnView(1, 25);
                summarySheet.setColumnView(2, 25);
                summarySheet.setColumnView(3, 25);
                summarySheet.setColumnView(4, 50);
                summarySheet.setColumnView(5, 25);
                summarySheet.setColumnView(6, 25);
                summarySheet.setColumnView(7, 25);


                WritableSheet detailSheet = writebook.getSheet(1);
                detailSheet.setColumnView(0, 25); //设置列宽
                detailSheet.setColumnView(1, 25);
                detailSheet.setColumnView(2, 50);
                detailSheet.setColumnView(3, 50);
                detailSheet.setColumnView(4, 50);
                detailSheet.setColumnView(5, 50);

                int row = 1;
                for (ResultItem item : resultItemList){
                    summarySheet.addCell(new Label(0, row, String.valueOf(item.getClz()), contentFormat));
                    summarySheet.addCell(new Label(1, row, String.valueOf(item.getResultDataList().size()), contentFormat));
                    summarySheet.addCell(new Label(2, row, String.valueOf(countPass(item.getResultDataList())), contentFormat));
                    summarySheet.addCell(new Label(3, row, String.valueOf(countFail(item.getResultDataList())), contentFormat));
                    summarySheet.addCell(new Label(4, row, percentageCalculator(countPass(item.getResultDataList()), item.getResultDataList().size()), contentFormat));
                    summarySheet.addCell(new Label(5, row, String.valueOf(0), contentFormat));
                    summarySheet.addCell(new Label(6, row, String.valueOf(0), contentFormat));
                    summarySheet.addCell(new Label(7, row, String.valueOf(0), contentFormat));
                    row++;
                }

                int r = 1;
                for (ResultItem item : resultItemList){
                    for (int i = 0; i < item.getResultDataList().size(); i++){
                        detailSheet.addCell(new Label(0, r, String.valueOf(item.getClz()), contentFormat));
                        detailSheet.addCell(new Label(1, r, item.getResultDataList().get(i).getId(), contentFormat));
                        if (item.getResultDataList().get(i).getResult().equals("测试通过")) {
                            detailSheet.addCell(new Label(2, r, item.getResultDataList().get(i).getResult(), contentFormat));
                        }else {
                            detailSheet.addCell(new Label(2, r, item.getResultDataList().get(i).getResult(), failResultFormat));
                        }
                        detailSheet.addCell(new Label(3, r, item.getResultDataList().get(i).getTestCaseName(), contentFormat));
                        detailSheet.addCell(new Label(4, r, item.getResultDataList().get(i).getChineseDescription(), contentFormat));
                        detailSheet.addCell(new Label(5, r, item.getResultDataList().get(i).getDetail(), contentFormat));
                        r++;
                    }
                }

                writebook.write();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (writebook != null) {
                    try {
                        writebook.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determine whether sdcard exist or not
     * @return true: exist; false: not exist.
     */
    private static boolean existSDCard() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    // 获取存储根目录
    private static String getRootStorage() {
        if (existSDCard()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            return null;
        }
    }

    /**
     * Convert timestamp to time
     * @param s timestamp
     * @return the format data of you want
     */
    public static String stampToDate(String s) {
        String res;
        Pattern pattern = Pattern.compile("[0-9]*");
        if (!pattern.matcher(s).matches()) return "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static String testCaseDate(String s) {
        String res;
        Pattern pattern = Pattern.compile("[0-9]*");
        if (!pattern.matcher(s).matches()) return "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static long timeDifference(String startTime, String endTime) {
        long diffInSeconds = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        try {
            Date start = sdf.parse(startTime);
            Date end = sdf.parse(endTime);

            long diffInMillies = end.getTime() - start.getTime();
            diffInSeconds = diffInMillies / 1000;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return diffInSeconds;
    }

    /**
     *  计算百分比
     * @param numerator 分子
     * @param denominator 分母
     * @return 百分数
     */
    public static String percentageCalculator(int numerator, int denominator){
        // 计算百分比
        double percentage = ((double) numerator / denominator) * 100;
        // 格式化输出百分比为两位小数
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.##");
        String formattedPercentage = df.format(percentage);
        return (formattedPercentage + "%");
    }

    private static int countPass(List<ResultData> resultDataList){
        int count = 0;
        for(ResultData resultData : resultDataList){
            if (resultData.getResult().equals("测试通过")){
                count++;
            }
        }
        return count;
    }

    private static int countFail(List<ResultData> resultDataList){
        int count = 0;
        for(ResultData resultData : resultDataList){
            if (resultData.getResult().equals("测试失败")){
                count++;
            }
        }
        return count;
    }


}
