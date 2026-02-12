package com.newland.sdkdemo.test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import com.newland.basetest.annotation.FragmentAno;
import com.newland.basetest.annotation.MethodAno;
import com.newland.basetest.pinc.MessageTag;
import com.newland.basetest.pinc.MethodBean;
import com.newland.mesdk.simple.demo.R;
import com.newland.sdk.module.printer.Alignment;
import com.newland.sdk.module.printer.BarcodeFormat;
import com.newland.sdk.module.printer.EnFontSize;
import com.newland.sdk.module.printer.ErrorCode;
import com.newland.sdk.module.printer.FontScale;
import com.newland.sdk.module.printer.FontSize;
import com.newland.sdk.module.printer.ImageFormat;
import com.newland.sdk.module.printer.PaperSize;
import com.newland.sdk.module.printer.PrintListener;
import com.newland.sdk.module.printer.PrintScriptUtil;
import com.newland.sdk.module.printer.SpaceScale;
import com.newland.sdk.module.printer.TextFormat;
import com.newland.sdk.module.printer.TwoDimensionCodeEncode;
import com.newland.sdk.module.printer.TwoDimensionCodeFormat;
import com.newland.sdk.module.printer.ZhFontSize;
import com.newland.sdkdemo.FragmentBase;
import com.newland.sdk.module.printer.PrinterModule;
import com.newland.sdk.module.printer.PrinterStatus;
import com.newland.sdkdemo.utils.DialogUtils;

@FragmentAno(name = "打印",numId = 2)
public class PrinterOption extends FragmentBase {
    private PrinterModule printerModule;
    private PrintScriptUtil printScriptUtil;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            /**
             * 获取打印机模块对象
             */
            printerModule = getModuleManage().getPrinterModule();
            printScriptUtil = printerModule.getPrintScriptUtil(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @MethodAno(name = "获取打印机状态")
    private void getStatus(MethodBean bean){
        try {
            PrinterStatus status = printerModule.getStatus();
            switch (status){
                case NORMAL:
                    showMessage("打印机正常",MessageTag.NORMAL);
                    break;
                case OUTOF_PAPER:
                    showMessage("打印机缺纸",MessageTag.ERROR);
                    break;
                case LOW_VOLTAGE:
                    showMessage("打印机低电压",MessageTag.ERROR);
                    break;
                case OVER_HEAT:
                    showMessage("打印机温度过高",MessageTag.ERROR);
                    break;
                case BUSY:
                    showMessage("打印机忙",MessageTag.ERROR);
                    break;
                case CUTTER_ERROR:
                    showMessage("打印机切刀异常",MessageTag.ERROR);
                    break;
                default:
                    showMessage("打印机其它异常："+status,MessageTag.ERROR);
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
            showError(e);
        }
        showMessage("", MessageTag.NORMAL);
    }


    @MethodAno(name = "设置打印纸张宽度",numId = 1)
    private void setPaperSize(MethodBean bean){
        try {
            try {
                DialogUtils dialogUtils = DialogUtils.getInstance();
                String[] inchs = new String[]{"2 英寸", "3 英寸"};
                dialogUtils.createSingleChoiceDialog(getContext(),"设置打印纸张大小",inchs, new DialogUtils.SingleChoiceDialogCallback() {
                    @Override
                    public void onResult(int id) {
                        PaperSize paperSize = PaperSize.SIZE_2INCH;
                        switch (id) {
                            case 0:
                                paperSize = PaperSize.SIZE_2INCH;
                                showMessage("选择2英寸纸张",MessageTag.NORMAL);
                                break;
                            case 1:
                                paperSize = PaperSize.SIZE_3INCH;
                                showMessage("选择3英寸纸张",MessageTag.NORMAL);
                                break;
                        }
                        try {
                            /**
                             * 一般情况不需要设置纸张大小。
                             * 默认2英寸。收银机CPOS系列的POS机，支持3英寸的才需要设置纸张大小
                             */
                            boolean is = printerModule.setPaperSize(paperSize);
                            if (is) {
                                showMessage("设置纸张大小成功", MessageTag.DATA);
                            } else {
                                showMessage("设置纸张大小失败", MessageTag.ERROR);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            showError(e);
                        }
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError(e);
        }
    }

    @MethodAno(name = "设置打印灰度",numId = 2)
    private void setGray(MethodBean bean){
        DialogUtils dialogUtils = DialogUtils.getInstance();
        String[] items = new String[]{"1","2","3","4","5","6","7","8","9","10"};
        dialogUtils.createSingleChoiceDialog(getContext(), "打印灰度", items, new DialogUtils.SingleChoiceDialogCallback() {
            @Override
            public void onResult(int id) {
                if(id<0){//取消
                    return;
                }
                try {
                    printScriptUtil.setGray(id+1);
                    showMessage("设置打印灰度"+(id+1)+"完成",MessageTag.NORMAL);
                }catch (Exception e){
                    e.printStackTrace();
                    showError(e);
                }
            }
        });
    }
    @MethodAno(name = "设置打印行间距",numId = 3)
    private void setLineSpacing(MethodBean bean){
        DialogUtils dialogUtils = DialogUtils.getInstance();
        String[] items = new String[]{"0","5","10","15","20"};
        dialogUtils.createSingleChoiceDialog(getContext(), "打印灰度", items, new DialogUtils.SingleChoiceDialogCallback() {
            @Override
            public void onResult(int id) {
                if(id<0){//取消
                    return;
                }
                try {
                    printScriptUtil.setLineSpacing(5*id);
                    showMessage("设置打印行间距"+(5*id)+"完成",MessageTag.NORMAL);
                }catch (Exception e){
                    e.printStackTrace();
                    showError(e);
                }
            }
        });
    }

    @MethodAno(name = "添加文本打印",numId = 4)
    private void addText(MethodBean bean){
        TextFormat format = new TextFormat();
        format.setAlignment(Alignment.LEFT);
        format.setFontSize(FontSize.SMALL);
        String text = "左对齐,小字体abc";
        printScriptUtil.addText(format, text);
        printScriptUtil.addText(format, text);

        format = new TextFormat();
        format.setAlignment(Alignment.LEFT);
        format.setFontSize(FontSize.NORMAL);
        text = "左对齐,中字体abc";
        printScriptUtil.addText(format, text);
        printScriptUtil.addText(format, text);

        format = new TextFormat();
        format.setAlignment(Alignment.LEFT);
        format.setFontSize(FontSize.LARGE);
        text = "左对齐,大字体abc";
        printScriptUtil.addText(format, text);
        printScriptUtil.addText(format, text);

        format = new TextFormat();
        format.setAlignment(Alignment.CENTER);
        format.setFontSize(FontSize.SMALL);
        format.setZhFontSize(ZhFontSize.FONT_24x24);
        format.setEnFontSize(EnFontSize.FONT_8x16);
        text = "居中,小字体abc";
        printScriptUtil.addText(format, text);
        printScriptUtil.addText(format, text);

        format = new TextFormat();
        format.setAlignment(Alignment.CENTER);
        format.setFontSize(FontSize.NORMAL);
        text = "居中,中字体abc";
        printScriptUtil.addText(format, text);
        printScriptUtil.addText(format, text);

        format = new TextFormat();
        format.setAlignment(Alignment.CENTER);
        format.setFontSize(FontSize.LARGE);
        text = "居中,大字体abc";
        printScriptUtil.addText(format, text);
        printScriptUtil.addText(format, text);


        format = new TextFormat();
        format.setAlignment(Alignment.CENTER);
        format.setFontSize(FontSize.SMALL);
        format.setZhFontSize(ZhFontSize.FONT_24x24);
        format.setEnFontSize(EnFontSize.FONT_8x16);
        text = "右对齐,小字体abc";
        printScriptUtil.addText(format, text);
        printScriptUtil.addText(format, text);

        format = new TextFormat();
        format.setAlignment(Alignment.CENTER);
        format.setFontSize(FontSize.NORMAL);
        text = "右对齐,中字体abc";
        printScriptUtil.addText(format, text);
        printScriptUtil.addText(format, text);

        format = new TextFormat();
        format.setAlignment(Alignment.CENTER);
        format.setFontSize(FontSize.LARGE);
        text = "右对齐,大字体abc";
        printScriptUtil.addText(format, text);
        printScriptUtil.addText(format, text);

        format = new TextFormat();
        format.setAlignment(Alignment.LEFT);
        format.setFontSize(FontSize.NORMAL);
        format.setUnderline(true);
        text = "左对齐文本，带下划线,abc,123";
        printScriptUtil.addText(format, text);

        format = new TextFormat();
        format.setAlignment(Alignment.CENTER);
        format.setFontSize(FontSize.NORMAL);
        format.setUnderline(true);
        text = "居中文本，带下划线,abc,123";
        printScriptUtil.addText(format, text);

        printScriptUtil.addPaperFeed(3);//走3行空白纸
    }


    @MethodAno(name = "添加条码打印",numId = 5)
    private void addBarCode(MethodBean bean){
        BarcodeFormat barcodeFormat = new BarcodeFormat();
        barcodeFormat.setAlignment(Alignment.LEFT);
        showMessage("添加左对齐条码，码值1234567890",MessageTag.NORMAL);
        printScriptUtil.addBarcode(barcodeFormat,"1234567890");

        printScriptUtil.addPaperFeed(1);//走1行空白纸

        barcodeFormat = new BarcodeFormat();
        barcodeFormat.setAlignment(Alignment.CENTER);
        showMessage("添加居中条码，码值1234567890",MessageTag.NORMAL);
        printScriptUtil.addBarcode(barcodeFormat,"1234567890");

        printScriptUtil.addPaperFeed(1);//走1行空白纸

        barcodeFormat = new BarcodeFormat();
        barcodeFormat.setAlignment(Alignment.RIGHT);
        showMessage("添加右对齐条码，码值1234567890",MessageTag.NORMAL);
        printScriptUtil.addBarcode(barcodeFormat,"1234567890");

        printScriptUtil.addPaperFeed(1);//走1行空白纸

        barcodeFormat = new BarcodeFormat();
        barcodeFormat.setAlignment(Alignment.CENTER);
        barcodeFormat.setWidth(4);
        barcodeFormat.setHeight(128);
        showMessage("添加居中条码，码值1234567890，宽4，高128",MessageTag.NORMAL);
        printScriptUtil.addBarcode(barcodeFormat,"1234567890");
    }

    @MethodAno(name = "添加二维码打印",numId = 6)
    private void addQrCode(MethodBean bean){
        TwoDimensionCodeFormat  twoDimensionCodeFormat = new TwoDimensionCodeFormat();
        twoDimensionCodeFormat.setAlignment(Alignment.LEFT);
        showMessage("添加左对齐二维码，码值1234567890",MessageTag.NORMAL);
        printScriptUtil.addTwoDimensionCode(twoDimensionCodeFormat,"1234567890");

        printScriptUtil.addPaperFeed(1);//走1行空白纸

        twoDimensionCodeFormat = new TwoDimensionCodeFormat();
        twoDimensionCodeFormat.setAlignment(Alignment.CENTER);
        showMessage("添加居中二维码，码值1234567890",MessageTag.NORMAL);
        printScriptUtil.addTwoDimensionCode(twoDimensionCodeFormat,"1234567890");

        printScriptUtil.addPaperFeed(1);//走1行空白纸

        twoDimensionCodeFormat = new TwoDimensionCodeFormat();
        twoDimensionCodeFormat.setAlignment(Alignment.RIGHT);
        showMessage("添加右对齐二维码，码值1234567890",MessageTag.NORMAL);
        printScriptUtil.addTwoDimensionCode(twoDimensionCodeFormat,"1234567890");

        printScriptUtil.addPaperFeed(1);//走1行空白纸

        twoDimensionCodeFormat = new TwoDimensionCodeFormat();
        twoDimensionCodeFormat.setAlignment(Alignment.CENTER);
        twoDimensionCodeFormat.setHeight(128);
        showMessage("添加居中二维码，码值1234567890，高128",MessageTag.NORMAL);
        printScriptUtil.addTwoDimensionCode(twoDimensionCodeFormat,"1234567890");
    }

    @MethodAno(name = "添加图片打印",numId = 7)
    private void addImage(MethodBean bean){
        ImageFormat imageFormat = new ImageFormat();
        imageFormat.setAlignment(Alignment.CENTER);
        imageFormat.setWidth(384);
        imageFormat.setHeight(200);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.print_logo);
        printScriptUtil.addImage(imageFormat,bitmap);
        showMessage("添加图片完成",MessageTag.NORMAL);
        printScriptUtil.addPaperFeed(1);//走1行空白纸

    }

    @MethodAno(name = "添加分割行",numId = 8)
    private void addDottedLine(MethodBean bean){
        printScriptUtil.addDottedLine();
        showMessage("添加分割行完成",MessageTag.NORMAL);
        printScriptUtil.addPaperFeed(1);//走1行空白纸
    }

    @MethodAno(name = "启动打印",numId = 9)
    private void print(MethodBean bean){
        printScriptUtil.print(new PrintListener() {
            @Override
            public void onSuccess() {
                showMessage("打印完成",MessageTag.NORMAL);
            }

            @Override
            public void onError(ErrorCode errorCode, String msg) {
                showMessage("打印异常，异常码："+errorCode+";异常信息："+msg,MessageTag.ERROR);
            }
        });
    }
}
