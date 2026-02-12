package com.newland.sdk.me.module.printerPro.appimpl.internal;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;

import com.newland.sdk.module.printer.TextFormat;
import com.newland.sdk.module.printerPro.NAlignment;
import com.newland.sdk.module.printerPro.NTableTextFormat;
import com.newland.sdk.module.printerPro.NTextFormat;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.utils.ISOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description
 * @Author wuhh
 * @Date 2021/6/28
 */
public class PrinterHelper {
    public static boolean isEnablePrinterPro = false;
    private static final boolean isSavaBitmap = false;
    private static final boolean isDeleteFile = true;
//    public static final String PATH = "/sdcard/PrintBitmap";
    public static final String PATH = "/data/share/PrintBitmap";

    private DeviceLogger deviceLogger = DeviceLoggerFactory.getLogger("PrinterHelper");
    public static boolean isMESDKModeIng = false;
    public static boolean hasRegisterPrinterEvent = false;
//    public static final int ITEM_MAX_PX = 100;
//    public static final int COUNT_MAX_ITEM = 200;

    public static int ITEM_MAX_PX = 1000;
    public static int COUNT_MAX_ITEM = 5;

    public static final int COUNT_MAX_GC = 100;
    public static final int COUNT_MAX_QUEUE = 20;

    public Bitmap imageScale(Bitmap bm, float scaleWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleWidth);
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    public void imageSplit(Bitmap bitmap, int xPiece, int yPiece,SplitComplete splitComplete) throws Exception {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int pieceWidth = width / xPiece;
        int pieceHeight = height / yPiece;
        for (int i = 0; i < yPiece; i++) {
            for (int j = 0; j < xPiece; j++) {
                int xValue = j * pieceWidth;
                int yValue = i * pieceHeight;
                Bitmap piece = Bitmap.createBitmap(bitmap, xValue, yValue, pieceWidth, pieceHeight);
                splitComplete.onComplete(piece);
            }
        }
    }

    public interface SplitComplete{
        public void onComplete(Bitmap bitmap) throws Exception;
    }

    private int getText2BitmapHeight(ArrayList<PrintItem[]> textList){
        Object formatCls = null;
        if(textList != null && textList.size() > 0){
            formatCls = textList.get(0)[0].getFormat();
        }
        int height = 0;
        if(formatCls instanceof NTextFormat){
            for (PrintItem[] printItems: textList) {
                int textMaxSizeOfRow = 0;
                int paddingBottomOfRow = 0;
                for (PrintItem printItem:printItems) {
                    Object format = printItem.getFormat();
                    NTextFormat textFormat = (NTextFormat)format;
                    int fontMaxSize = getFontMaxSize(textFormat);
                    if(fontMaxSize > textMaxSizeOfRow){
                        textMaxSizeOfRow = fontMaxSize;
                    }
                    if(textFormat.getMarginBottom() > paddingBottomOfRow){
                        paddingBottomOfRow = textFormat.getMarginBottom();
                    }
                }
                height += textMaxSizeOfRow + paddingBottomOfRow;
            }
            return height;
        }else if(formatCls instanceof NTableTextFormat){
            for (PrintItem[] printItems: textList) {
                int maxBorderHeight = 0;
                for (PrintItem printItem:printItems) {
                    Object format = printItem.getFormat();
                    NTableTextFormat textFormat = (NTableTextFormat)format;
                    if(textFormat.getBorderHeight() > maxBorderHeight){
                        maxBorderHeight = textFormat.getBorderHeight();
                    }
                }
                height += maxBorderHeight;
            }
            return height;
        }
        deviceLogger.error("[getText2BitmapHeight] error.");
        return -1;


    }

    public Bitmap text2Bitmap(int width, ArrayList<PrintItem[]> textList){

        long startTime = System.currentTimeMillis();
        deviceLogger.debug("[text2Bitmap] start.");
        Object formatCls = null;
        if(textList != null && textList.size() > 0){
            formatCls = textList.get(0)[0].getFormat();
        }
        int height = getText2BitmapHeight(textList);
        deviceLogger.debug("[text2Bitmap] height="+height);
        Bitmap bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        if(formatCls instanceof NTextFormat){
            int currHeigh = 0;
            TextPaint paint = new TextPaint();
            Paint paintLine = new Paint();
            paint.setAntiAlias(true);
            paintLine.setAntiAlias(true);
            paintLine.setTextSize(0.1f);

            TextPaint paintRect = new TextPaint();
            for (PrintItem[] printItems: textList) {
                int textMaxSizeOfRow = 0;
                int paddingBottomOfRow = 0;
                int maxSizeOfRow = 0;
                boolean hadReverse = false;
                for(PrintItem printItem:printItems){
                    NTextFormat textFormat = (NTextFormat)printItem.getFormat();
                    int fontMaxSize = getFontMaxSize(textFormat);
                    if(fontMaxSize > textMaxSizeOfRow){
                        textMaxSizeOfRow = fontMaxSize;
                    }
                    if(textFormat.getMarginBottom() > paddingBottomOfRow){
                        paddingBottomOfRow = textFormat.getMarginBottom();
                    }
                }
                maxSizeOfRow = textMaxSizeOfRow + paddingBottomOfRow;

                for (PrintItem printItem:printItems) {
                    NTextFormat textFormat = (NTextFormat)printItem.getFormat();
                    NAlignment alignment = textFormat.getAlignment();
                    Paint.Align align;int x;
                    int fontMaxSize = getFontMaxSize(textFormat);
                    if(alignment == NAlignment.CENTER){
                        x = width / 2;
                        align = Paint.Align.CENTER;
                    }else if(alignment ==NAlignment.RIGHT){
                        x = width;
                        align = Paint.Align.RIGHT;
                    }else {
                        x = 0;
                        align = Paint.Align.LEFT;
                    }
                    int offset = textFormat.getOffset();
                    if(offset > 0){
                        x = offset;
                        paint.setTextAlign(Paint.Align.LEFT);
                        paintLine.setTextAlign(Paint.Align.LEFT);
                    }else {
                        paint.setTextAlign(align);
                        paintLine.setTextAlign(align);
                    }

                    if(textFormat.getTypeface()!=null){
                        paint.setTypeface(textFormat.getTypeface());
                    }else {
                        paint.setTypeface(null);
                    }

                    if(textFormat.isUnderline()){
                        paint.setUnderlineText(true);
                    }else {
                        paint.setUnderlineText(false);
                    }
                    paint.setColor(Color.BLACK);

                    int yspaceOfmax = maxSizeOfRow - (fontMaxSize + textFormat.getMarginBottom());
                    int itemCount = printItems.length;
                    if(itemCount == 1 && textFormat.isReverse()){
                        paint.setColor(Color.WHITE);
                        canvas.drawRect(0, currHeigh, getMaxWidth(),currHeigh + fontMaxSize + textFormat.getMarginBottom() ,paintRect);
                    }else {
                        if(textFormat.isReverse()){
                            paint.setColor(Color.WHITE);
                            if(isMESDKModeIng){
                                if(!hadReverse){
                                    for(PrintItem item:printItems){
                                        NTextFormat format = (NTextFormat)item.getFormat();
                                        int heightSize = getFontMaxSize(format);
                                        int yspacemax = maxSizeOfRow - (heightSize + format.getMarginBottom());
                                        if(yspacemax == 0){
                                            canvas.drawRect(0, currHeigh, getMaxWidth(), currHeigh + heightSize + textFormat.getMarginBottom() ,paintRect);
                                            hadReverse = true;
                                            break;
                                        }
                                    }
                                }
                           }else {
                                paint.setTextSize(fontMaxSize);
                                float widthOfText = paint.measureText(textFormat.getContent());
                                float startX = x;
                                if(alignment == NAlignment.CENTER){
                                    startX =  (width / 2 - widthOfText/2);
                                }else if(alignment ==NAlignment.RIGHT){
                                    startX =  (width - widthOfText);
                                }
                                canvas.drawRect(startX, yspaceOfmax + currHeigh , startX + widthOfText,yspaceOfmax + currHeigh + fontMaxSize + textFormat.getMarginBottom() ,paintRect);
                            }
                       }
                    }

                    /*
                    ContentSplitState hzState,ascState;
                    String pieceHZ = "",pieceAsc = "";
                    int currX = x;
                    String content = textFormat.getContent();
                    for (int i = 0; i < content.length(); i++) {
                        char c = content.charAt(i);
                        if (c >= 19968 && c <= 171941){
                            pieceHZ += c;
                            hzState = ContentSplitState.START;
                            ascState = ContentSplitState.END;
                        }else {
                            pieceAsc += c;
                            hzState = ContentSplitState.END;
                            ascState = ContentSplitState.START;
                        }
                        if((pieceHZ != null && !pieceHZ.equals("")) && (hzState == ContentSplitState.END || (i == content.length() -1))){
                            //paint.setTextSize(hzSizeH);
                            //paint.setTextScaleX((hzSizeW*1.0f/hzSizeH));
                            paint.setTextSize(hzSizeW);
                            canvas.drawText(pieceHZ, currX, yspaceOfmax + currHeigh + fontMaxSize + textFormat.getMarginBottom(), paint);
                            if(offset > 0){
                                currX += paint.measureText(pieceHZ);
                            }
                            pieceHZ = "";
                        }
                        if((pieceAsc != null && !pieceAsc.equals("")) && (ascState == ContentSplitState.END || (i == content.length() -1))){
                            //paint.setTextSize(ascSizeH);
                            //paint.setTextScaleX((ascSizeW*1.0f/ascSizeH));
                            float scaleW = ascSizeW*2.0f;
                            if(scaleW > ascSizeH){
                                scaleW = ascSizeW*1.5f;
                            }
                            paint.setTextSize(scaleW);
                            canvas.drawText(pieceAsc, currX, yspaceOfmax + currHeigh + fontMaxSize + textFormat.getMarginBottom(), paint);

                            currX += paint.measureText(pieceAsc);
                            pieceAsc = "";
                        }
                    }
                    */
                    paint.setTextSize(fontMaxSize);
                    canvas.drawText(textFormat.getContent(), x, yspaceOfmax + currHeigh + fontMaxSize, paint);

                    if(textFormat.getFontSize() > textMaxSizeOfRow){
                        textMaxSizeOfRow = fontMaxSize;
                    }
                    if(textFormat.getMarginBottom() > paddingBottomOfRow){
                        paddingBottomOfRow = textFormat.getMarginBottom();
                    }
                }
                currHeigh += textMaxSizeOfRow + paddingBottomOfRow;

            }
            canvas.save();
            canvas.restore();

        }else if(formatCls instanceof NTableTextFormat){
            float currHeigh = 0;
            Paint paintText = new Paint();
            Paint paintBorder = new Paint();
            paintText.setAntiAlias(true);
            paintBorder.setAntiAlias(true);
            for (PrintItem[] printItems: textList) {
                float sumWeight = 0.0f;
                int zero0Num = 0;//非0个数
                int currWidth = 0;
                int maxBorderHeight = 0;

                for (PrintItem printItem:printItems) {
                    NTableTextFormat textFormat = (NTableTextFormat)printItem.getFormat();
                    float weight = textFormat.getBorderWeight();
                    if(weight < 0){
                        deviceLogger.error("[text2Bitmap] weight="+weight);
                        return null;
                    }
                    if(weight == 0.0f){
                        zero0Num++;
                    }
                    sumWeight += weight;
                }
                if(sumWeight < 0 || sumWeight > 1){
                    deviceLogger.error("[text2Bitmap] The percentage sum of each row must be 1 or 0. but you set "+sumWeight);
                    return null;
                }

                for (PrintItem printItem:printItems) {
                    NTableTextFormat textFormat = (NTableTextFormat)printItem.getFormat();
                    double weight = textFormat.getBorderWeight();
                    if(weight == 0){
                        weight = ((1.0 - sumWeight)/zero0Num);
                    }
                    float itenWidthOfRow = (float) (weight*getMaxWidth());
                    paintBorder.setTextSize(textFormat.getBorderSize());
                    if(textFormat.isReverse()){
                        paintBorder.setStyle(Paint.Style.FILL);
                    }else {
                        paintBorder.setStyle(Paint.Style.STROKE);
                    }
                    deviceLogger.debug("[text2Bitmap] sumWeight="+sumWeight+" zero0Num="+zero0Num+" weight="+weight+" itenWidthOfRow="+itenWidthOfRow +" Content="+textFormat.getContent());
                    canvas.drawRect(currWidth, currHeigh,currWidth + itenWidthOfRow,currHeigh + textFormat.getBorderHeight() ,paintBorder);

                    NAlignment alignment = textFormat.getAlignmentOfBorder();
                    Paint.Align align;int textSize = textFormat.getFontSize();
                    float xText = textFormat.getMarginLeft();
                    if(alignment == NAlignment.CENTER){
                        align = Paint.Align.CENTER;
                        xText += (currWidth + currWidth + itenWidthOfRow)/2.0f;
                    }else if(alignment == NAlignment.RIGHT){
                        align = Paint.Align.RIGHT;
                        xText += (currWidth + itenWidthOfRow) - 2;
                    }else {
                        align = Paint.Align.LEFT;
                        xText += currWidth + 2;
                    }
                    paintText.setTextAlign(align);
                    paintText.setTextSize(textSize);
                    if(textFormat.getTypeface()!=null){
                        paintText.setTypeface(textFormat.getTypeface());
                    }else {
                        paintText.setTypeface(null);
                    }
                    if(textFormat.isReverse()){
                        paintText.setColor(Color.WHITE);
                    }else {
                        paintText.setColor(Color.BLACK);
                    }
                    canvas.drawText(textFormat.getContent(), xText, (currHeigh + currHeigh + textFormat.getBorderHeight() + textSize)/2.0f , paintText);

                    if(textFormat.getBorderHeight() > maxBorderHeight){
                        maxBorderHeight = textFormat.getBorderHeight();
                    }
                    currWidth += itenWidthOfRow;
                }
                currHeigh += maxBorderHeight;
            }
            canvas.save();
            canvas.restore();
        }
        if(isSavaBitmap){
            try {
                File imageFile = new File("/sdcard/AppSDKPrintBitmapTest_"+System.currentTimeMillis()+".png");
                FileOutputStream fOut = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        deviceLogger.debug("[text2Bitmap] end. disTime="+(System.currentTimeMillis()-startTime));
        return bitmap;
    }

    public float getMaxWidth(){
        float maxWidth = 384;
        if (Build.MODEL.toUpperCase().contains("CPOS")|| Build.MODEL.equals("STAR A-6300")) {
            maxWidth = 576;
        }
        return maxWidth;
    }

    public void deleteFile(File file) {
        if(!isDeleteFile){
            deviceLogger.error("[deleteFile] isDeleteFile="+isDeleteFile);
            return;
        }
        try {
            if (file.exists()) {
                if (file.isFile()) {
                    file.delete();
                } else {
                    // 首先得到当前的路径
                    String[] childFilePaths = file.list();
                    for (String childFilePath : childFilePaths) {
                        File childFile = new File(file.getAbsolutePath() + "/" + childFilePath);
                        deleteFile(childFile);
                    }
                    // file.delete();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private enum ContentSplitState{
        NONE,START,END
    }

    public int getFontMaxSize(NTextFormat textFormat){
        if(isMESDKModeIng){
            byte[] fontSize = InnerUtils.intToBytes(textFormat.getFontSize(),4,true);
            byte hzSizeW = fontSize[0], hzSizeH = fontSize[1] , ascSizeW = fontSize[2], ascSizeH = fontSize[3];
            int contentType = getContentType(textFormat.getContent());
            int fontMaxSize;
            if(contentType == 1){//全中文
                fontMaxSize = hzSizeW;
            }else if(contentType == 2){//全英文
                fontMaxSize = ascSizeW*2;
                if(fontMaxSize > ascSizeH){
                    deviceLogger.debug("[getFontMaxSize] fontMaxSize="+fontMaxSize+" ascSizeH="+ascSizeH);
//                    fontMaxSize = ascSizeH;
//                    fontMaxSize = (int) (ascSizeW*1.5);
                }
            }else {
                fontMaxSize = hzSizeW;//(hzSizeH > ascSizeH ? hzSizeH : ascSizeH);
            }
            return fontMaxSize;
        }else {
            return textFormat.getFontSize();
        }
    }

    private int getContentType (String content) {
        int c = 0,hzFlag = 0,ascFlag = 0;
        try {
            for(int i = 0; i < content.length(); i++) {
                c = content.charAt(i);
                if (c >= 19968 && c <= 171941) {
                    hzFlag = 1;
                }else{
                    ascFlag = 2;
                }
                if(hzFlag == 1 && ascFlag == 2){
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hzFlag + ascFlag;
    }

    /*
    boolean isUnderline = textFormat.isUnderline();
    if (isUnderline){
        float widthOfText = paint.measureText(textFormat.getContent());
        int startY = currHeigh + textFormat.getFontSize() + 4;
        if(align == Paint.Align.CENTER){
            x = (int) (x -  widthOfText/2);
        }else if(align == Paint.Align.RIGHT){
            x = (int) (x -  widthOfText);
        }
        canvas.drawLine(x,startY,x+widthOfText,startY,paintLine);
    }
    */
}
