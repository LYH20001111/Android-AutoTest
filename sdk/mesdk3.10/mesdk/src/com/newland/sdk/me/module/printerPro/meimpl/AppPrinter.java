package com.newland.sdk.me.module.printerPro.meimpl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NavUtils;
import android.util.Log;

import com.newland.sdk.me.module.printerPro.appimpl.internal.InternalPrinterModule;

import static com.newland.sdk.me.module.printerPro.meimpl.EM_PRN_ZM_FONT.*;
import static com.newland.sdk.me.module.printerPro.meimpl.EM_PRN_HZ_FONT.*;

import com.newland.sdk.me.module.printerPro.appimpl.internal.PrinterHelper;
import com.newland.sdk.module.printer.ErrorCode;
import com.newland.sdk.module.printer.PrintListener;
import com.newland.sdk.module.printerPro.NAlignment;
import com.newland.sdk.module.printerPro.NImageFormat;
import com.newland.sdk.module.printerPro.NPrintErrorCode;
import com.newland.sdk.module.printerPro.NPrintListener;
import com.newland.sdk.module.printerPro.NTextFormat;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.InnerUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Description
 * @Author wuhh
 * @Date 2021/7/8
 */
public class AppPrinter {

    private DeviceLogger deviceLogger = DeviceLoggerFactory.getLogger("PrinterHelper");
    private static final String N = "\n";
    private static final String SPACE = " ";
    private static final String COLON = ":";
    private static final String STAR = "\\*";
    private static final String SEMICOLON = ";";

    private InternalPrinterModule mNPrinterModule;
    private HashMap<String,Typeface> mTypefaceHashMap;
    private Context mContext;
    private Object mPrinterSync = new Object();

    public AppPrinter(Context context) {
        mContext = context;
        mTypefaceHashMap = new HashMap<>();
        mNPrinterModule = new InternalPrinterModule();
    }

    public void print(String scriptData, @Nullable Map<String, Bitmap> map, final PrintListener printListener) {
        deviceLogger.debug("[print] scriptData=" + scriptData);
        if (scriptData == null || printListener == null) {
            return;
        }
        String[] scriptItems = scriptData.split(N);
        if (scriptItems == null || (scriptItems != null && scriptItems.length < 1)) {
            deviceLogger.debug("[print] scriptItems=" + (scriptItems != null ? scriptItems.length : "null"));
            onParamError(printListener, "Lack of N?");
            return;
        }
        synchronized (mPrinterSync){
            if(PrinterHelper.isMESDKModeIng){
                printWait();
            }
            PrinterHelper.isMESDKModeIng = true;
        }
        deviceLogger.debug("[print] scriptItems.length=" + scriptItems.length);
        int yspace = 0, CurrHzxSize = 24 , CurrHzySize = 24, ZMDotAscXSize = 24, ZMDotAscYSize = 24;
        boolean isBlod = false, isReverse = false;
        List<NTextFormat> TEXT_UNDERLINE_List = new ArrayList<>();
        String currScriptItem = null;
        String ttfFilePath = null;
        try {
            for (String scriptItem : scriptItems) {
                currScriptItem = scriptItem;
                //deviceLogger.debug("[print] currScriptItem=" + currScriptItem);
                String[] params = scriptItem.split(SPACE);
                switch (params[0]) {
                    case "!font": {
                        ttfFilePath = params[1];
                    }
                    break;
                    case "!hz":
                    case "!asc": {
                        switch (params[1]) {
                            case "s"://16*16,8*16
                                CurrHzxSize = 16;
                                CurrHzySize = 16;
                                ZMDotAscXSize = 8;
                                ZMDotAscYSize = 16;
                                break;
                            case "n"://24*24,12*24
                                CurrHzxSize = 24;
                                CurrHzySize = 24;
                                ZMDotAscXSize = 12;
                                ZMDotAscYSize = 24;
                                break;
                            case "l"://32*32,16*32
                                CurrHzxSize = 32;
                                CurrHzySize = 32;
                                ZMDotAscXSize = 16;
                                ZMDotAscYSize = 32;
                                break;
                            case "sn"://16*24,8*24
                                CurrHzxSize = 16;
                                CurrHzySize = 24;
                                ZMDotAscXSize = 8;
                                ZMDotAscYSize = 24;
                                break;
                            case "sl"://16*32,8*32
                                CurrHzxSize = 16;
                                CurrHzySize = 32;
                                ZMDotAscXSize = 8;
                                ZMDotAscYSize = 32;
                                break;
                            case "nl"://24*32,12*32
                                CurrHzxSize = 24;
                                CurrHzySize = 32;
                                ZMDotAscXSize = 12;
                                ZMDotAscYSize = 32;
                                break;
                            default:
                                onParamError(printListener, ":" + scriptItem);
                                return;
                        }
                    }
                    break;
                    case "!gray": {
                        mNPrinterModule.addScript(scriptItem);
                    }
                    break;
                    case "!yspace": {
                        yspace = Integer.valueOf(params[1]);
                        mNPrinterModule.addScript(scriptItem);
                    }
                    break;
                    case "!NLFONT": {
                        int hzFont = Integer.parseInt(params[1]);
                        int zmFont = Integer.parseInt(params[2]);
                        String scale = params[3];
                        int[] fontParam = getFontParam(hzFont, zmFont);
                        CurrHzxSize = fontParam[0];
                        CurrHzySize = fontParam[1];
                        int hzBold = fontParam[2];
                        ZMDotAscXSize = fontParam[3];
                        ZMDotAscYSize = fontParam[4];
                        int ascBold = fontParam[5];

                        isBlod = (hzBold == 1 || ascBold == 1);

                        if (scale.equals("0")) {//0—横向 2 倍放大、纵向 2倍放大
                            CurrHzxSize = CurrHzxSize * 2;
                            CurrHzySize = CurrHzySize * 2;
                            ZMDotAscXSize = ZMDotAscXSize * 2;
                            ZMDotAscYSize = ZMDotAscYSize * 2;
                        } else if (scale.equals("1")) {//1—横向 2 倍放大、纵向正常
                            CurrHzxSize = CurrHzxSize * 2;
                            ZMDotAscXSize = ZMDotAscXSize * 2;
                        } else if (scale.equals("2")) {//2—横向正常、纵向 2 倍放大
                            CurrHzySize = CurrHzySize * 2;
                            ZMDotAscYSize = ZMDotAscYSize * 2;
                        } else if (scale.equals("3")) {//3—横向正常、纵向正常

                        } else if (scale.equals("4")) {//4—横向 3 倍放大、纵向 3倍放大
                            CurrHzxSize = CurrHzxSize * 3;
                            CurrHzySize = CurrHzySize * 3;
                            ZMDotAscXSize = ZMDotAscXSize * 3;
                            ZMDotAscYSize = ZMDotAscYSize * 3;
                        } else if (scale.equals("5")) {//5—横向 3 倍放大、纵向正常
                            CurrHzxSize = CurrHzxSize * 3;
                            ZMDotAscXSize = ZMDotAscXSize * 3;
                        }
                    }
                    break;
                    case "!NLPRNOVER": {
                        int pixel = yspace + (CurrHzySize > ZMDotAscYSize ? CurrHzySize : ZMDotAscYSize);
                        mNPrinterModule.addScript(getFeedPixel(pixel));
                    }
                    break;
                    case "!reverse": {
                        if (params[1].equals("on")) {
                            isReverse = true;
                        } else if (params[1].equals("off")) {
                            isReverse = false;
                        } else {
                            onParamError(printListener, scriptItem);
                            return;
                        }
                    }
                    break;
                    case "!barcode":
                    case "!BARCODE":
                    case "!qrcode":
                    case "!QRCODE": {
                        mNPrinterModule.addScript(scriptItem);
                    }
                    break;
                    //****************************************************************************//
                    case "*text": {
                        Object[] alignOffset = getAlignOffset(params[1]);
                        TEXT_UNDERLINE_List.add(new NTextFormat.Builder().content(getContent(scriptItem)).fontSize(getFontSize(CurrHzxSize,CurrHzySize,ZMDotAscXSize,ZMDotAscYSize)).
                                alignment((NAlignment) alignOffset[0]).typeface(getTypeface(ttfFilePath, isBlod)).marginBottom(getYspace(yspace)).
                                offset((int) alignOffset[1]).isReverse(isReverse).create());
                        mNPrinterModule.addText(TEXT_UNDERLINE_List.toArray(new NTextFormat[TEXT_UNDERLINE_List.size()]));
                        TEXT_UNDERLINE_List.clear();
                    }
                    break;
                    case "*TEXT": {
                        Object[] alignOffset = getAlignOffset(params[1]);
                        TEXT_UNDERLINE_List.add(new NTextFormat.Builder().content(getContent(scriptItem)).fontSize(getFontSize(CurrHzxSize,CurrHzySize,ZMDotAscXSize,ZMDotAscYSize)).
                                alignment((NAlignment) alignOffset[0]).typeface(getTypeface(ttfFilePath, isBlod)).marginBottom(getYspace(yspace)).
                                offset((int) alignOffset[1]).isReverse(isReverse).create());
                    }
                    break;
                    case "*underline": {
                        Object[] alignOffset = getAlignOffset(params[1]);
                        TEXT_UNDERLINE_List.add(new NTextFormat.Builder().content(getContent(scriptItem)).fontSize(getFontSize(CurrHzxSize,CurrHzySize,ZMDotAscXSize,ZMDotAscYSize)).
                                alignment((NAlignment) alignOffset[0]).typeface(getTypeface(ttfFilePath, isBlod)).marginBottom(getYspace(yspace)).
                                offset((int) alignOffset[1]).isUnderline(true).isReverse(isReverse).create());
                        mNPrinterModule.addText(TEXT_UNDERLINE_List.toArray(new NTextFormat[TEXT_UNDERLINE_List.size()]));
                        TEXT_UNDERLINE_List.clear();
                    }
                    break;
                    case "*UNDERLINE": {
                        Object[] alignOffset = getAlignOffset(params[1]);
                        TEXT_UNDERLINE_List.add(new NTextFormat.Builder().content(getContent(scriptItem)).fontSize(getFontSize(CurrHzxSize,CurrHzySize,ZMDotAscXSize,ZMDotAscYSize)).
                                alignment((NAlignment) alignOffset[0]).typeface(getTypeface(ttfFilePath, isBlod)).marginBottom(getYspace(yspace)).
                                offset((int) alignOffset[1]).isUnderline(true).isReverse(isReverse).create());
                    }
                    break;
                    case "*feedline": {
                        int pixel,lineNum;
                        if (params[1].charAt(0) == 'l') {
                            lineNum = Integer.valueOf(params[1].split(COLON)[1]);
                            pixel = (yspace + (CurrHzySize > ZMDotAscYSize ? CurrHzySize : ZMDotAscYSize))*lineNum;
                        } else if (params[1].charAt(0) == 'p') {
                            pixel = Integer.valueOf(params[1].split(COLON)[1]);
                        } else {
                            lineNum  = Integer.valueOf(params[1]);
                            pixel = (yspace + (CurrHzySize > ZMDotAscYSize ? CurrHzySize : ZMDotAscYSize))*lineNum;
                        }
                        mNPrinterModule.addScript(getFeedPixel(pixel));
                    }
                    break;
                    case "*line": {
                        if (isReverse) {
                            mNPrinterModule.addScript("!reverse on");
                        } else {
                            mNPrinterModule.addScript("!reverse off");
                        }
                        mNPrinterModule.addScript(scriptItem);
                    }
                    break;
                    case "*barcode":
                    case "*BARCODE":
                    case "*qrcode":
                    case "*QRCODE":
                    case "*pause":
                    case "*cut": {
                        mNPrinterModule.addScript(scriptItem);
                    }
                    break;
                    case "*image": {
                        Object[] alignOffset = getAlignOffset(params[1]);
                        String[] wh = params[2].split(STAR);

                        String imgName;
                        int threshold = -1;
                        if (params[3].contains("yz")) {
                            imgName = params[3].split(SEMICOLON)[1];
                            threshold = Integer.valueOf(params[3].split(SEMICOLON)[0].split(COLON)[2]);
                        } else {
                            imgName = params[3].split(COLON)[1];
                        }
                        if (Integer.valueOf(wh[0]) > getMaxWidth() || (threshold < 0 && threshold != -1) || ((int) alignOffset[1] > getMaxWidth() || (int) alignOffset[1] < 0)) {
                            onParamError(printListener, scriptItem);
                            return;
                        }
                        Bitmap bitmap = map.get(imgName);
                        mNPrinterModule.addImage(new NImageFormat.Builder().bitmap(bitmap).threshold(threshold).width(Integer.valueOf(wh[0])).height(Integer.valueOf(wh[1])).
                                alignment((NAlignment) alignOffset[0]).offset((int) alignOffset[1]).create());
                    }
                    break;
                    default:
                        deviceLogger.debug("[print] scriptItem=" + scriptItem);
                        onParamError(printListener, "not support");
                        return;
                }
            }

            Log.d("[][]", ">>>>printer3");
            mNPrinterModule.startPrintThread(new NPrintListener() {
                @Override
                public void onSuccess() {
                    mTypefaceHashMap.clear();
                    PrinterHelper.isMESDKModeIng = false;
                    deviceLogger.debug("[print] onSuccess");
                    printListener.onSuccess();
                    printNotify();
                }

                @Override
                public void onError(NPrintErrorCode error, String msg) {
                    mTypefaceHashMap.clear();
                    PrinterHelper.isMESDKModeIng = false;
                    deviceLogger.debug("[print] error=" + error + " msg=" + msg);
                    printListener.onError(getErrorCode(error), msg);
                    printNotify();
                }
            });
        } catch (Exception e) {
            onParamError(printListener, currScriptItem);
            e.printStackTrace();
        }
    }

    private void onParamError(PrintListener listener, String desc) {
        deviceLogger.error("[onError] PARAM_ERROR=" + desc);
        listener.onError(ErrorCode.PARAM_ERROR, ErrorCode.PARAM_ERROR.toString() + ":" + desc);
    }

    private Typeface getTypeface(String path, boolean isBlod) {
        //deviceLogger.debug("[getTypeface] path=" + path + " isBlod=" + isBlod+" path+isBlod="+path+isBlod);
        Typeface typeface = mTypefaceHashMap.get(path+isBlod);
        if(typeface != null){
            return typeface;
        }
        if (path != null) {
            //String path = ttfPath.substring(path.lastIndexOf("/")+1);
            //Typeface typeface =  Typeface.createFromAsset(mContext.getAssets(),path);
            if (isBlod) {
                mTypefaceHashMap.put(path+isBlod,Typeface.create(path, Typeface.BOLD));
            } else {
                mTypefaceHashMap.put(path+isBlod,Typeface.createFromFile(path));
            }
        } else {
            if (isBlod) {
                mTypefaceHashMap.put(path+isBlod,Typeface.defaultFromStyle(Typeface.BOLD));
            } else {
                mTypefaceHashMap.put(path+isBlod,null);
            }
        }
        return mTypefaceHashMap.get(path+isBlod);
    }

    private int[] getFontParam(int emHZFont, int emZMFont) {
        if ((emHZFont <= 0) || (emHZFont > PRN_HZ_FONT_48x48C)
                || (emZMFont <= 0) || (emZMFont > PRN_ZM_FONT_12x24BL)) {
            return null;
        }
        int CurrHzxSize = 24, CurrHzySize = 24, hzBold = 0;
        int ZMDotAscXSize = 12, ZMDotAscYSize = 24, ascBold = 0;

        switch (emHZFont) {
            case PRN_HZ_FONT_24x24USER:
            case PRN_HZ_FONT_24x24A:
            case PRN_HZ_FONT_24x24B:
            case PRN_HZ_FONT_24x24C:
            case PRN_HZ_FONT_24x24:
                CurrHzxSize = 24;
                CurrHzySize = 24;
                break;
            case PRN_HZ_FONT_24x24BL:
                CurrHzxSize = 24;
                CurrHzySize = 24;
                //TODO
                hzBold = 1;
                break;
            case PRN_HZ_FONT_16x32:
                CurrHzxSize = 16;
                CurrHzySize = 32;
                break;
            case PRN_HZ_FONT_32x32:
                CurrHzxSize = 32;
                CurrHzySize = 32;
                break;
            case PRN_HZ_FONT_32x16:
                CurrHzxSize = 32;
                CurrHzySize = 16;
                break;
            case PRN_HZ_FONT_24x32:
                CurrHzxSize = 24;
                CurrHzySize = 32;
                break;
            case PRN_HZ_FONT_16x16:
                CurrHzxSize = 16;
                CurrHzySize = 16;
                break;
            case PRN_HZ_FONT_16x16BL:
                CurrHzxSize = 16;
                CurrHzySize = 16;
                //TODO
                hzBold = 1;
                break;
            case PRN_HZ_FONT_12x16:
                CurrHzxSize = 12;
                CurrHzySize = 16;
                break;
            case PRN_HZ_FONT_16x8:
                CurrHzxSize = 16;
                CurrHzySize = 8;
                break;
            case PRN_HZ_FONT_12x12A:
                CurrHzxSize = 12;
                CurrHzySize = 12;
                break;
            case PRN_HZ_FONT_16x24:
                CurrHzxSize = 16;
                CurrHzySize = 24;
                break;
            case PRN_HZ_FONT_48x24A:
            case PRN_HZ_FONT_48x24B:
            case PRN_HZ_FONT_48x24C:
                CurrHzxSize = 48;
                CurrHzySize = 24;
                break;
            case PRN_HZ_FONT_24x48A:
            case PRN_HZ_FONT_24x48B:
            case PRN_HZ_FONT_24x48C:
                CurrHzxSize = 24;
                CurrHzySize = 48;
                break;
            case PRN_HZ_FONT_48x48A:
            case PRN_HZ_FONT_48x48B:
            case PRN_HZ_FONT_48x48C:
                CurrHzxSize = 48;
                CurrHzySize = 48;
                break;
        }

        switch (emZMFont) {
            case PRN_ZM_FONT_8x12:
                ZMDotAscXSize = 8;
                ZMDotAscYSize = 12;
                break;
            case PRN_ZM_FONT_8x16:
                ZMDotAscXSize = 8;
                ZMDotAscYSize = 16;
                break;
            case PRN_ZM_FONT_8x16BL:
                ZMDotAscXSize = 8;
                ZMDotAscYSize = 16;
                //TODO
                ascBold = 1;
                break;
            case PRN_ZM_FONT_16x16:
                ZMDotAscXSize = 16;
                ZMDotAscYSize = 16;
                break;
            case PRN_ZM_FONT_16x16BL:
                ZMDotAscXSize = 16;
                ZMDotAscYSize = 16;
                //TODO
                ascBold = 1;
                break;
            case PRN_ZM_FONT_8x24:
                ZMDotAscXSize = 8;
                ZMDotAscYSize = 24;
                break;
            case PRN_ZM_FONT_8x32:
                ZMDotAscXSize = 8;
                ZMDotAscYSize = 32;
                break;
            case PRN_ZM_FONT_12x32A:
            case PRN_ZM_FONT_12x32B:
            case PRN_ZM_FONT_12x32C:
                ZMDotAscXSize = 12;
                ZMDotAscYSize = 32;
                break;
            case PRN_ZM_FONT_16x32:
                ZMDotAscXSize = 16;
                ZMDotAscYSize = 32;
                break;
            case PRN_ZM_FONT_24x32:
                ZMDotAscXSize = 24;
                ZMDotAscYSize = 32;
                break;
            case PRN_ZM_FONT_6x8:
                ZMDotAscXSize = 6;
                ZMDotAscYSize = 8;
                break;
            case PRN_ZM_FONT_8x8:
                ZMDotAscXSize = 8;
                ZMDotAscYSize = 8;
                break;
            case PRN_ZM_FONT_5x7:
                ZMDotAscXSize = 5;
                ZMDotAscYSize = 7;
                break;
            case PRN_ZM_FONT_5x16:
                ZMDotAscXSize = 5;
                ZMDotAscYSize = 16;
                break;
            case PRN_ZM_FONT_10x16:
                ZMDotAscXSize = 10;
                ZMDotAscYSize = 16;
                break;
            case PRN_ZM_FONT_10x8:
                ZMDotAscXSize = 10;
                ZMDotAscYSize = 8;
                break;
            case PRN_ZM_FONT_12x12A:
            case PRN_ZM_FONT_12x12B:
            case PRN_ZM_FONT_12x12C:
            case PRN_ZM_FONT_12x12:
                ZMDotAscXSize = 12;
                ZMDotAscYSize = 12;
                break;
            case PRN_ZM_FONT_12x16A:
            case PRN_ZM_FONT_12x16B:
            case PRN_ZM_FONT_12x16C:
                ZMDotAscXSize = 12;
                ZMDotAscYSize = 16;
                break;
            case PRN_ZM_FONT_12x24A:
            case PRN_ZM_FONT_12x24B:
            case PRN_ZM_FONT_12x24C:
                ZMDotAscXSize = 12;
                ZMDotAscYSize = 24;
                break;
            case PRN_ZM_FONT_12x24BL:
                ZMDotAscXSize = 12;
                ZMDotAscYSize = 24;
                //TODO
                ascBold = 1;
                break;
            case PRN_ZM_FONT_16x32A:
            case PRN_ZM_FONT_16x32B:
            case PRN_ZM_FONT_16x32C:
                ZMDotAscXSize = 16;
                ZMDotAscYSize = 32;
                break;
            case PRN_ZM_FONT_24x24A:
            case PRN_ZM_FONT_24x24B:
            case PRN_ZM_FONT_24x24C:
                ZMDotAscXSize = 24;
                ZMDotAscYSize = 24;
                break;
            case PRN_ZM_FONT_32x32A:
            case PRN_ZM_FONT_32x32B:
            case PRN_ZM_FONT_32x32C:
                ZMDotAscXSize = 32;
                ZMDotAscYSize = 32;
                break;
        }

        int[] fontParam = new int[6];
        fontParam[0] = CurrHzxSize;
        fontParam[1] = CurrHzySize;
        fontParam[2] = hzBold;
        fontParam[3] = ZMDotAscXSize;
        fontParam[4] = ZMDotAscYSize;
        fontParam[5] = ascBold;
        return fontParam;
    }

    private String getFeedPixel(int pixel) {
        return "*feedline p:" + pixel + "\n";
    }

    private int getYspace(int space) {
        return space <= 0 ? 4 : space;
    }

    private Object[] getAlignOffset(String param) {
        NAlignment nAlignment = null;
        int offset = 0;
        switch (param.charAt(0)) {
            case 'l':
                nAlignment = NAlignment.LEFT;
                break;
            case 'c':
                nAlignment = NAlignment.CENTER;
                break;
            case 'r':
                nAlignment = NAlignment.RIGHT;
                break;
            case 'x':
                nAlignment = NAlignment.LEFT;
                offset = Integer.valueOf(param.split(":")[1]);
                break;
            default:
                deviceLogger.debug("[getAlignOffset] error.");
                return null;
        }
        Object[] alinOffset = new Object[2];
        alinOffset[0] = nAlignment;
        alinOffset[1] = offset;
        return alinOffset;
    }


    private ErrorCode getErrorCode(NPrintErrorCode errorCode) {
        switch (errorCode) {
            case FAILED:
                return ErrorCode.FAILED;
            case PARAM_ERROR:
                return ErrorCode.PARAM_ERROR;
            case INVALID_FILE_PATH:
                return ErrorCode.INVALID_FILE_PATH;
            case BUSY:
                return ErrorCode.BUSY;
            case OUTOF_PAPER:
                return ErrorCode.OUTOF_PAPER;
            case HEAT_LIMITED:
                return ErrorCode.HEAT_LIMITED;
            case ABNORMAL_VOLTAGE:
                return ErrorCode.ABNORMAL_VOLTAGE;
            case DESTROYED:
                return ErrorCode.DESTROYED;
            case PPSERR:
                return ErrorCode.PPSERR;
            case CUTTER_ERROR:
        }
        deviceLogger.debug("[getErrorCode] FAILED");
        return ErrorCode.FAILED;
    }

    private float getMaxWidth() {
        float maxWidth = 384;
        if (Build.MODEL.toUpperCase().contains("CPOS") || Build.MODEL.equals("STAR A-6300")) {
            maxWidth = 576;
        }
        return maxWidth;
    }

    private String getContent(String scriptItem) {
        int first = scriptItem.indexOf(SPACE);
        int second = scriptItem.indexOf(SPACE, first + 1);
        return scriptItem.substring(second + 1);
    }

    private int getFontSize(int CurrHzxSize,int CurrHzySize,int ZMDotAscXSize,int ZMDotAscYSize){
        byte hzScaleW, hzSizeH, ascScaleW, ascSizeH;
        hzScaleW = (byte) (CurrHzxSize*1.0f / CurrHzySize * 100);
        hzSizeH = (byte) CurrHzySize;
        ascScaleW= (byte) (ZMDotAscXSize*1.0f / ZMDotAscYSize * 100);
        ascSizeH = (byte) ZMDotAscYSize;
        int fontSize = InnerUtils.bytesToInt(new byte[]{(byte) CurrHzxSize, (byte) CurrHzySize, (byte) ZMDotAscXSize, (byte) ZMDotAscYSize},0,4,true);
        //deviceLogger.debug("[getFontSize] hzScaleW="+String.format("%x",hzScaleW)+" hzSizeH="+String.format("%x",hzSizeH)+" ascScaleW="+String.format("%x",ascScaleW)+" ascSizeH="+String.format("%x",ascSizeH)+" fontSize="+String.format("%x",fontSize));
//        deviceLogger.debug("[getFontSize] CurrHzxSize="+CurrHzxSize+" CurrHzySize="+CurrHzySize+" ZMDotAscXSize="+ZMDotAscXSize+" ZMDotAscYSize="+ZMDotAscYSize+" fontSize="+fontSize);
        return fontSize;
    }

    private void printWait(){
        synchronized (mPrinterSync){
            try {
                deviceLogger.debug("[printWait]");
                mPrinterSync.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void printNotify(){
        synchronized (mPrinterSync){
            mPrinterSync.notify();
            deviceLogger.debug("[printNotify]");
        }
    }
}
