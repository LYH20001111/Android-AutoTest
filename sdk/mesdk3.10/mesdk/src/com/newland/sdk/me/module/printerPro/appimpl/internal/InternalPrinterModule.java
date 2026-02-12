package com.newland.sdk.me.module.printerPro.appimpl.internal;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.Log;

import com.newland.event.EventCallBack;
import com.newland.intelligent.jni.JniCmdInterface;
import com.newland.sdk.module.printer.ErrorCode;
import com.newland.sdk.module.printerPro.NAlignment;
import com.newland.sdk.module.printerPro.NBarcodeFormat;
import com.newland.sdk.module.printerPro.NImageFormat;
import com.newland.sdk.module.printerPro.NPrintErrorCode;
import com.newland.sdk.module.printerPro.NPrintListener;
import com.newland.sdk.module.printerPro.NPrinterModule;
import com.newland.sdk.module.printerPro.NPrinterStatus;
import com.newland.sdk.module.printerPro.NTableTextFormat;
import com.newland.sdk.module.printerPro.NTextFormat;
import com.newland.sdk.module.printerPro.NTwoDimensionalCodeFormat;
import com.newland.ndk.NdkApiManager;
import com.newland.ndk.Print;
import com.newland.sdk.me.module.emvl3.utils.METhreadExecutors;
import com.newland.sdk.me.module.printer.TTFPrint;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.AbstractModule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Description
 * @Author wuhh
 * @Date 2021/6/25
 */
public class InternalPrinterModule extends AbstractModule implements NPrinterModule {
    private DeviceLogger deviceLogger = DeviceLoggerFactory.getLogger("InternalPrinterModule");
    private Print mPrint;
    private List<PrintItem> mPrintItemList;
    private LinkedQueue mLinkedQueue;
    private PrinterHelper mPrinterHelper;
    private TextPaint mTextPaint;

    private volatile NPrintErrorCode mErrorCode = null;
    private Object mPrinterSync = new Object();
    private boolean mPrinterIsBusy = false;

    private Object mPrinterEventSync = new Object();

    private static final String IMAGE_MODE_PNG = "PNG";
    private static final String IMAGE_MODE_RGBA = "RGBA";
    private static final int SYS_EVENT_TIMEOUT = (22*60*60*1000);
    public static final String IMAGE_MODE = IMAGE_MODE_PNG;
    private static final String SCRIPT_IMAGEMODE = (IMAGE_MODE.equals(IMAGE_MODE_PNG)?"":"!image rgba\n");
    public InternalPrinterModule() {
        super(null);
        mPrint = NdkApiManager.getNdkApiManager().getPrint();
        mPrinterHelper = new PrinterHelper();
        mTextPaint = new TextPaint();
        mPrintItemList = new ArrayList<>();
        mLinkedQueue = new LinkedQueue();
    }

    @Override
    public NPrinterStatus getStatus() {
        try {
            int[] status = new int[1];
            int ret = mPrint.NDK_PrnGetStatus(status);
            if (ret == 0) {
                if (status[0] == 0) {
                    return NPrinterStatus.NORMAL;
                } else if (status[0] == 8) {
                    return NPrinterStatus.BUSY;
                } else if (status[0] == 2) {
                    return NPrinterStatus.OUTOF_PAPER;
                } else if (status[0] == 4) {
                    return NPrinterStatus.OVER_HEAT;
                } else if (status[0] == 112) {
                    return NPrinterStatus.LOW_VOLTAGE;
                } else if (status[0] == 1024) {
                    return NPrinterStatus.DESTROYED;
                } else if (status[0] == 2048) {
                    return NPrinterStatus.PPSERR;
                } else if (status[0] == 512) {
                    return NPrinterStatus.CUTTER_ERROR;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return NPrinterStatus.DESTROYED;
    }

    public void addScript(String script) {
        deviceLogger.debug("addScript script=" + script);
        if (script == null) {
            return;
        }
        addPrintItem(new PrintItem(PrintItem.TYPE_SCRIPT, null, script + "\n", null));
    }

    @Override
    public void addText(NTableTextFormat... textFormat) {
        if (textFormat == null) {
            deviceLogger.error("addText, NTableTextFormat is null.");
            return;
        }
        for (NTableTextFormat item : textFormat) {
            deviceLogger.debug("addText NTableTextFormat getContent=" + item.getContent());
        }
        addPrintItem(new PrintItem(PrintItem.TYPE_TABLE, null, null, textFormat));
    }

    @Override
    public void addText(NTextFormat... textFormat) {
        if (textFormat == null) {
            deviceLogger.error("addText, textFormat is null.");
            return;
        }
        if (textFormat.length == 1) {
            NTextFormat item = textFormat[0];
            addTextItem(item, item.getContent());
        } else {
            for (NTextFormat item : textFormat) {
                deviceLogger.debug("addText:" + item.getContent());
            }
            addPrintItem(new PrintItem(PrintItem.TYPE_TEXT, null, null, textFormat));
        }
    }

    private void addTextItem(NTextFormat textFormat, String content) {
        mTextPaint.reset();
        int fontMaxSize = mPrinterHelper.getFontMaxSize(textFormat);
        mTextPaint.setTextSize(fontMaxSize);
        if (textFormat.getTypeface() != null) {
            mTextPaint.setTypeface(textFormat.getTypeface());
        } else {
            mTextPaint.setTypeface(null);
        }
        int offset = textFormat.getOffset();
        float maxWidth = mPrinterHelper.getMaxWidth();
        if (offset > 0) {
            maxWidth = maxWidth - offset;
        }
        float width = mTextPaint.measureText(content);

        int index = mTextPaint.breakText(content, 0, content.length(), true, maxWidth, null);
        if (width > maxWidth && index == 0) {

        }
        if (index > 0) {
            String content1 = content.substring(0, index);
            NTextFormat content1Format = new NTextFormat.Builder().content(content1).fontSize(textFormat.getFontSize()).typeface(textFormat.getTypeface())
                    .marginBottom(textFormat.getMarginBottom()).alignment(textFormat.getAlignment()).
                            offset(textFormat.getOffset()).isUnderline(textFormat.isUnderline()).isReverse(textFormat.isReverse()).create();
            addPrintItem(new PrintItem(PrintItem.TYPE_TEXT, null, null, new NTextFormat[]{content1Format}));
            String content2 = content.substring(index);
            if (content2.length() > 0) {
                addTextItem(textFormat, content2);
            }
        } else {
            addPrintItem(new PrintItem(PrintItem.TYPE_TEXT, null, null, new NTextFormat[]{textFormat}));
        }
    }

    @Override
    public void addImage(NImageFormat imageFormat) {
        try {
            if (imageFormat == null) {
                deviceLogger.error("addImage, imageFormat is null.");
                return;
            }

            Bitmap bitmap = imageFormat.getBitmap();
            if (bitmap == null) {
                deviceLogger.error("addImage, bitmap is null.");
                return;
            }
            deviceLogger.debug("[addImage] Format getWidth=" + imageFormat.getWidth() + " getHeight=" + imageFormat.getHeight());
            deviceLogger.debug("[addImage] Bitmap getWidth=" + bitmap.getWidth() + " getHeight=" + bitmap.getHeight());
            StringBuilder printText = new StringBuilder();

            String thresholdFs = "";
            if (imageFormat.getThreshold() != -1) {
                thresholdFs = "yz:" + imageFormat.getThreshold() + ";";
            }
            int offset = imageFormat.getOffset();
            if (offset > 0) {
                printText.append("*image x:" + offset).append(" ").append(PrintItem.PLACEHOLDER_IMAGE_WIDTH).append("*").append(PrintItem.PLACEHOLDER_IMAGE_HEIGHT).append(" path:").append(thresholdFs).append(PrintItem.PLACEHOLDER_IMAGE).append("\n");
            } else {
                switch (imageFormat.getAlignment()) {
                    case LEFT:
                        printText.append("*image l").append(" ").append(PrintItem.PLACEHOLDER_IMAGE_WIDTH).append("*").append(PrintItem.PLACEHOLDER_IMAGE_HEIGHT).append(" path:").append(thresholdFs).append(PrintItem.PLACEHOLDER_IMAGE).append("\n");
                        break;
                    case CENTER:
                        printText.append("*image c").append(" ").append(PrintItem.PLACEHOLDER_IMAGE_WIDTH).append("*").append(PrintItem.PLACEHOLDER_IMAGE_HEIGHT).append(" path:").append(thresholdFs).append(PrintItem.PLACEHOLDER_IMAGE).append("\n");
                        break;
                    case RIGHT:
                        printText.append("*image r").append(" ").append(PrintItem.PLACEHOLDER_IMAGE_WIDTH).append("*").append(PrintItem.PLACEHOLDER_IMAGE_HEIGHT).append(" path:").append(thresholdFs).append(PrintItem.PLACEHOLDER_IMAGE).append("\n");
                        break;
                    default:
                        printText.append("*image l").append(" ").append(PrintItem.PLACEHOLDER_IMAGE_WIDTH).append("*").append(PrintItem.PLACEHOLDER_IMAGE_HEIGHT).append(" path:").append(thresholdFs).append(PrintItem.PLACEHOLDER_IMAGE).append("\n");
                        break;
                }
            }

            String script = printText.toString();
            deviceLogger.debug("addImage: " + script);
            addPrintItem(new PrintItem(PrintItem.TYPE_IMAGE, null, script, imageFormat));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addBarcode(NBarcodeFormat format) {
        try {
            if (format == null) {
                deviceLogger.error("addBarcode, bitmap is null.");
                return;
            }
            String content = format.getContent();
            int width = format.getWidth();
            int height = format.getHeight();
            NBarcodeFormat.Type type = format.getCodeType();
            boolean isShowCodeContent = format.isShowCodeContent();

            StringBuilder printText = new StringBuilder();

            if (content == null) {
                deviceLogger.error("addBarcode, content is null.");
                return;
            }

            if (width <= 0 || width > 8) {
                width = 8;
            }

            if (height < 64) {
                height = 64;
            }

            if (type == null) {
                type = NBarcodeFormat.Type.CODE128;
            }
            printText.append("!BARCODE " + width + " " + height + " " + (isShowCodeContent ? 1 : 0) + " " + type.getCode() + "\n"); // 3:CODE128.
            switch (format.getAlignment()) {
                case LEFT:
                    printText.append("*BARCODE l" + " " + content + "\n");
                    break;
                case RIGHT:
                    printText.append("*BARCODE r" + " " + content + "\n");
                    break;
                case CENTER:
                    printText.append("*BARCODE c" + " " + content + "\n");
                    break;
                default:
                    printText.append("*BARCODE l" + " " + content + "\n");
                    break;
            }
            String script = printText.toString();
            deviceLogger.debug("addBarcode:" + script);
            addPrintItem(new PrintItem(PrintItem.TYPE_SCRIPT, null, script, null));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addTwoDimensionCode(NTwoDimensionalCodeFormat format) {
        try {
            if (format == null) {
                deviceLogger.error("addTwoDimensionCode, twoBarcode is null.");
                return;
            }
            String content = format.getContent();
            int height = format.getHeight();
            NAlignment alignment = format.getAlignment();
            NTwoDimensionalCodeFormat.Type codeType = format.getCodeType();
            if (content == null) {
                deviceLogger.error("addTwoDimensionCode, content is null.");
                return;
            }
            if (alignment == null) {
                alignment = NAlignment.CENTER;
            }
            if (codeType == null) {
                codeType = NTwoDimensionalCodeFormat.Type.QRCODE;
            }

            StringBuilder printText = new StringBuilder();
            printText.append("!QRCODE " + height + " " + 2 + " " + codeType.getCode() + "\n");
            switch (alignment) {
                case LEFT:
                    printText.append("*QRCODE l" + " " + content + "\n");
                    break;
                case RIGHT:
                    printText.append("*QRCODE r" + " " + content + "\n");
                    break;
                case CENTER:
                    printText.append("*QRCODE c" + " " + content + "\n");
                    break;
                default:
                    printText.append("*QRCODE l" + " " + content + "\n");
                    break;
            }
            String script = printText.toString();
            deviceLogger.debug("addTwoDimensionCode:" + script);
            addPrintItem(new PrintItem(PrintItem.TYPE_SCRIPT, null, script, null));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addPaperFeed(int pixel) {
        try {
            String feedLine = "*feedline p:" + pixel + "\n";
            deviceLogger.debug("addPaperFeed:" + feedLine);
            addPrintItem(new PrintItem(PrintItem.TYPE_SCRIPT, null, feedLine, null));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addPaperCut() {
        try {
            String paperCut = "*cut" + "\n";
            deviceLogger.debug("addPaperCut:" + paperCut);
            addPrintItem(new PrintItem(PrintItem.TYPE_SCRIPT, null, paperCut, null));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addPrintItem(PrintItem item) {
        synchronized (mPrinterSync) {
            if (mPrinterIsBusy) {
                deviceLogger.error("[addPrintItem] printer is busy.");
                return;
            }
        }
        mPrintItemList.add(item);
    }

    @Override
    public void startPrint(final NPrintListener listener) {
        if (listener == null) {
            deviceLogger.error("[startPrint] listener is null.");
            return;
        }
        synchronized (mPrinterSync) {
            if (mPrinterIsBusy) {
                listener.onError(NPrintErrorCode.BUSY, NPrintErrorCode.BUSY.toString() + "!");
                return;
            }
            mPrinterIsBusy = true;
        }
        startPrintThread(new NPrintListener() {
            @Override
            public void onSuccess() {
                mPrinterIsBusy = false;
                listener.onSuccess();
            }

            @Override
            public void onError(NPrintErrorCode error, String msg) {
                mPrinterIsBusy = false;
                listener.onError(error, msg);
            }
        });
    }

    private boolean closeMag(){
        JniCmdInterface.getInstance().jniMposLibCmdCancel(1);
        int ret = NdkApiManager.getNdkApiManager().getMagCard().NDK_MagClose();
        Log.e("Printer","[closeMag] InternalPrinterModule 3.10 ret="+ret);
        if(ret == -5){
            return false;
        }
        return true;
    }

    public void startPrintThread(final NPrintListener listener) {
        if(!closeMag()){
            listener.onError(NPrintErrorCode.FAILED, NPrintErrorCode.FAILED.toString());
            return;
        }
        final File fileDir = creatBitmapDir();
        deviceLogger.debug("[startPrint] startPrintThread.");
        mErrorCode = null;
        mLinkedQueue.add(new PrintItem(PrintItem.TYPE_START, null, null, null));
        METhreadExecutors.startThread(new Runnable() {
            @Override
            public void run() {
                deviceLogger.error("[startPrint] start print thread!" +" isMESDKModeIng="+PrinterHelper.isMESDKModeIng);
                try {
                    TTFPrint ttfPrint = TTFPrint.getInstance();
                    boolean isEmpty = mLinkedQueue.isEmpty();
                    if (isEmpty) {
                        deviceLogger.error("[startPrint] print queue is null.");
                        mErrorCode = NPrintErrorCode.PARAM_ERROR;
                        return;
                    }
                    int itemCount = 0;
                    File pngFile = null;
                    int bitmapName = 0;
                    PrintItem printItem = (PrintItem) mLinkedQueue.poll();
                    boolean isCompelte = printItem.getType() != PrintItem.TYPE_END ? false : true;
                    while (!isCompelte) {
                        isEmpty = mLinkedQueue.isEmpty();
//                          deviceLogger.debug("[startPrint]  isEmpty="+isEmpty);
                        if (isEmpty) {
                            Thread.sleep(1);
                            continue;
                        }
                        printItem = (PrintItem) mLinkedQueue.poll();
                        int type = printItem.getType();
                        isCompelte = type != PrintItem.TYPE_END ? false : true;
                        deviceLogger.debug("[startPrint] print isCompelte=" + isCompelte);
                        if (isCompelte) {
                            mErrorCode = (NPrintErrorCode) printItem.getFormat();
                            break;
                        }
                        deviceLogger.debug("[startPrint] print getType=" + printItem.getType() + " getBitmap=" + printItem.getBitmap() + " getScript=" + printItem.getScript());
                        String sciptData = null;
                        byte[] scriptFb = null;
                        if (type == PrintItem.TYPE_IMAGE || type == PrintItem.TYPE_TTTO_IMAGE) {
                            bitmapName++;
                            boolean isRecycle = true;
                            Bitmap bitmap = printItem.getBitmap();
                            if (bitmap == null && printItem.getFormat() != null) {
                                isRecycle = false;
                                bitmap = ((NImageFormat) (printItem.getFormat())).getBitmap();
                            }

                            if(type == PrintItem.TYPE_TTTO_IMAGE && IMAGE_MODE.equals(IMAGE_MODE_RGBA)){
                                pngFile = savaRGBA(bitmapName, fileDir, bitmap);
                            } else {
                                pngFile = savaPng(bitmapName, fileDir, bitmap);
                            }

                            if (pngFile == null) {
                                deviceLogger.debug("[startPrint] print pngFile==null");
                                mErrorCode = NPrintErrorCode.FAILED;
                                return;
                            }
                            if (isRecycle && !bitmap.isRecycled()) {
                                bitmap.recycle();
                            }
                            sciptData = printItem.getScript().replace(PrintItem.PLACEHOLDER_IMAGE, pngFile.getAbsolutePath());
                        } else if (type == PrintItem.TYPE_SCRIPT) {
                            sciptData = printItem.getScript();
                        }

                        deviceLogger.debug("[startPrint] print sciptData=" + sciptData);
                        scriptFb = sciptData.getBytes("UTF-8");
                        int ret = 0;
                        if(PrinterHelper.isMESDKModeIng){
                            byte[] targetScript = new byte[scriptFb.length + 1];
                            System.arraycopy(scriptFb, 0, targetScript, 0, scriptFb.length);
                            targetScript[targetScript.length - 1] = '\0';
                            ret = ttfPrint.PrintScipt(targetScript, targetScript.length, 0);
                        }else {
                            ret = ttfPrint.PrintScipt(scriptFb,scriptFb.length, 0);
                        }

                        if (ret != 0) {
                            deviceLogger.error("[startPrint] PrintScipt result=" + ret);
                            mErrorCode = getPrintErrorCode(ret);
                            return;
                        }

                        if (pngFile != null) {
                            mPrinterHelper.deleteFile(pngFile);
                        }
                        if (itemCount++ % PrinterHelper.COUNT_MAX_GC == 0) {
                            //System.gc();
                        }
                    }
                } catch (Exception e) {
                    listener.onError(NPrintErrorCode.FAILED, NPrintErrorCode.FAILED.toString());
                    e.printStackTrace();
                } finally {
                    mLinkedQueue.clear();
                    mPrinterHelper.deleteFile(new File(PrinterHelper.PATH));
                    System.gc();
                    deviceLogger.error("[startPrint] print thread end!!! ErrorCode="+mErrorCode);
                    if (mErrorCode != null) {
                        listener.onError(mErrorCode,mErrorCode.toString());
                        return;
                    }
                    while (true) {
                        int ret = NdkApiManager.getNdkApiManager().getSysN().NDK_SYS_RegisterEvent(0x40, SYS_EVENT_TIMEOUT, new EventCallBack() {
                            @Override
                            public void callback(int event, int len, byte[] data) {
                                deviceLogger.debug("[NDK_SYS_RegisterEvent] event="+event);
                                NdkApiManager.getNdkApiManager().getSysN().NDK_SYS_UnRegisterEvent(0x40);
                                printerEventNotify();
                            }
                        });
                        deviceLogger.debug("[startPrint] print NDK_SYS_RegisterEvent ret="+ret);

                        printerEventWait();

                        if(PrinterHelper.hasRegisterPrinterEvent){
                            ret = NdkApiManager.getNdkApiManager().getSysN().NDK_SYS_UnRegisterEvent(0x40);
                            deviceLogger.debug("[startPrint] print NDK_SYS_UnRegisterEvent ret="+ret);
                        }

                        int size = mLinkedQueue.size();
                        deviceLogger.debug("[startPrint] print printerEventNotify size="+size);
                        if(size > PrinterHelper.COUNT_MAX_QUEUE){//打印事件可能存在概率性不能上报的情况
                            continue;
                        }
                        int[] status = new int[1];
                        ret = mPrint.NDK_PrnGetStatus(status);
                        if (ret != 0) {
                            listener.onError(NPrintErrorCode.FAILED, NPrintErrorCode.FAILED.toString());
                            return;
                        }
                        try {
                            Thread.sleep(5);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (status[0] == 0) {
                            deviceLogger.debug("[startPrint] print succ.");
                            listener.onSuccess();
                            return;
                        } else if (status[0] == 8) {
                            continue;
                        } else {
                            NPrintErrorCode code = getPrintErrorCode(status[0]);
                            deviceLogger.debug("[startPrint] print fail. errorcode="+code);
                            listener.onError(code,code.toString());
                            return;
                        }
                    }
                }
            }
        });
        METhreadExecutors.startThread(new Runnable() {
            @Override
            public void run() {
                try {
                    deviceLogger.error("[startPrint] queue thread start!!!");
                    if (mPrintItemList == null || (mPrintItemList != null && mPrintItemList.size() <= 0)) {
                        deviceLogger.error("[startPrint] queue PrintItemList=" + mPrintItemList);
                        mErrorCode = NPrintErrorCode.PARAM_ERROR;
                        return;
                    }
                    deviceLogger.debug("[startPrint] queue size="+mPrintItemList.size());
                    int maxWidth = (int) mPrinterHelper.getMaxWidth();

                    boolean startText = false, startTable = false;
                    ArrayList<PrintItem[]> textList = new ArrayList();
                    ArrayList<PrintItem[]> tableList = new ArrayList();
                    for (int i = 0; i < mPrintItemList.size(); i++) {
                        int queueSize = mLinkedQueue.size();
                        while (queueSize > PrinterHelper.COUNT_MAX_QUEUE) {
                            Thread.sleep(3);
                            queueSize = mLinkedQueue.size();
                            continue;
                        }
                        if(mErrorCode != null){
                            deviceLogger.debug("[startPrint] queue thread exit ErrorCode="+mErrorCode);
                            return;
                        }
                        PrintItem item = mPrintItemList.get(i);
                        deviceLogger.debug("[startPrint] queue getType=" + item.getType() + " getBitmap=" + item.getBitmap()+ " getScript=" + item.getScript());
                        int type = item.getType();
                        if (type == PrintItem.TYPE_TEXT || startText) {
                            startText = true;
                            if (type == PrintItem.TYPE_TEXT) {
                                textList.add(getPrintItems(item));
                            }
                            if (type != PrintItem.TYPE_TEXT || (i == mPrintItemList.size() - 1) || textList.size() > PrinterHelper.COUNT_MAX_ITEM) {
                                startText = false;
                                Bitmap text2Bitmap = mPrinterHelper.text2Bitmap(maxWidth, textList);
                                if (text2Bitmap == null) {
                                    deviceLogger.error("[startPrint] queue text2Bitmap.=" + text2Bitmap);
                                    mErrorCode = NPrintErrorCode.PARAM_ERROR;
                                    return;
                                }
                                dealBitmap(PrintItem.TYPE_TEXT, text2Bitmap, true, null);
                                textList.clear();
                            }
                        }
                        if (type == PrintItem.TYPE_TABLE || startTable) {
                            startTable = true;
                            if (type == PrintItem.TYPE_TABLE) {
                                tableList.add(getPrintItems(item));
                            }
                            if (type != PrintItem.TYPE_TABLE || (i == mPrintItemList.size() - 1) || tableList.size() > PrinterHelper.COUNT_MAX_ITEM) {
                                startTable = false;
                                Bitmap text2Bitmap = mPrinterHelper.text2Bitmap(maxWidth, tableList);
                                if (text2Bitmap == null) {
                                    deviceLogger.error("[startPrint] queue text2Bitmap=" + text2Bitmap);
                                    mErrorCode = NPrintErrorCode.PARAM_ERROR;
                                    return;
                                }
                                dealBitmap(PrintItem.TYPE_TEXT, text2Bitmap, true, null);
                                tableList.clear();
                            }
                        }
                        if (type == PrintItem.TYPE_IMAGE) {
                            boolean isNeedRecycle = false;
                            Bitmap bitmap = ((NImageFormat) item.getFormat()).getBitmap();
                            float[] whs = getImageWidthHeightScale(bitmap, item);
                            if (bitmap != null && whs[2] != 1) {
                                bitmap = mPrinterHelper.imageScale(bitmap, whs[2]);
                                isNeedRecycle = true;
                            }
                            dealBitmap(type, bitmap, isNeedRecycle, item);
                        }
                        if (type == PrintItem.TYPE_SCRIPT) {
                            mLinkedQueue.add(new PrintItem(PrintItem.TYPE_SCRIPT, null, item.getScript(), null));
                        }
                    }

                } catch (Exception e) {
                    mErrorCode = NPrintErrorCode.FAILED;
                    e.printStackTrace();
                } finally {
                    mLinkedQueue.add(new PrintItem(PrintItem.TYPE_END, null, null, mErrorCode));
                    mPrintItemList.clear();
                    deviceLogger.error("[startPrint] queue thread end!!!");
                }
            }

        });
    }



    private PrintItem[] getPrintItems(PrintItem item) {
        Object[] nTextFormats = (Object[]) item.getFormat();
        PrintItem[] printItems = new PrintItem[nTextFormats.length];
        for (int j = 0; j < printItems.length; j++) {
            PrintItem printItem = new PrintItem(-1, null, null, nTextFormats[j]);
            printItems[j] = printItem;
        }
        return printItems;
    }

    private File creatBitmapDir() {
        File parentFile = new File(PrinterHelper.PATH);
        if (!parentFile.exists()) {
            parentFile.mkdirs();
            parentFile.setWritable(true, false);
            parentFile.setReadable(true, false);
            parentFile.setExecutable(true, false);
        }
        return parentFile;
    }

    private File savaPng(int name, File fileDir, Bitmap bitmap) {
        long startTime = System.currentTimeMillis();
        FileOutputStream fOut = null;
        try {
            File bitmapFile = File.createTempFile("bitmap_" + name, ".png", fileDir);
            bitmapFile.setWritable(true, false);
            bitmapFile.setReadable(true, false);
            bitmapFile.setExecutable(true, false);
            fOut = new FileOutputStream(bitmapFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            return bitmapFile;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            deviceLogger.debug("[savaPng] disTime="+(System.currentTimeMillis() - startTime));
        }
        return null;
    }

    private File savaRGBA(int name, File fileDir, Bitmap bitmap) {
        long startTime = System.currentTimeMillis();
        FileOutputStream fOut = null;
        try {
            int bytes = bitmap.getByteCount();//返回可用于储存此位图像素的最小字节数
            ByteBuffer buffer = ByteBuffer.allocate(bytes);//使用allocate()静态方法创建字节缓冲区
            bitmap.copyPixelsToBuffer(buffer);//将位图的像素复制到指定的缓冲区
            byte[] rgba = buffer.array();

            File bitmapFile = File.createTempFile("bitmap_" + name, ".rgba", fileDir);
            bitmapFile.setWritable(true, false);
            bitmapFile.setReadable(true, false);
            bitmapFile.setExecutable(true, false);
            fOut = new FileOutputStream(bitmapFile);
            fOut.write(rgba);
            fOut.flush();
            return bitmapFile;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            deviceLogger.debug("[savaRGBA] disTime="+(System.currentTimeMillis() - startTime));
        }
        return null;
    }
    private void dealBitmap(final int printType, final Bitmap bitmap, boolean isRecycle, final PrintItem printItem) throws Exception {
        int heightBitmap = bitmap.getHeight();
        deviceLogger.debug("[dealBitmap] heightBitmap="+heightBitmap);
        if (heightBitmap > PrinterHelper.ITEM_MAX_PX) {
            int count = heightBitmap / PrinterHelper.ITEM_MAX_PX;
            mPrinterHelper.imageSplit(bitmap, 1, count, new PrinterHelper.SplitComplete() {
                @Override
                public void onComplete(Bitmap piece) throws Exception {
                    if (printType == PrintItem.TYPE_TEXT) {
                        StringBuilder printText = new StringBuilder();
                        printText.append(SCRIPT_IMAGEMODE).append("*image l").append(" ").append(piece.getWidth()).append("*").append(piece.getHeight()).append(" path:").append(PrintItem.PLACEHOLDER_IMAGE).append("\n");
                        mLinkedQueue.add(new PrintItem(PrintItem.TYPE_TTTO_IMAGE, piece, printText.toString(), null));
                    } else if (printType == PrintItem.TYPE_IMAGE) {
                        float[] whs = getImageWidthHeightScale(bitmap, printItem);
                        String scipt = printItem.getScript().replace(PrintItem.PLACEHOLDER_IMAGE_WIDTH, (int) whs[0] + "");
                        scipt = scipt.replace(PrintItem.PLACEHOLDER_IMAGE_HEIGHT, piece.getHeight() + "");
                        deviceLogger.debug("[splitBitmap] scipt=" + scipt);
                        mLinkedQueue.add(new PrintItem(PrintItem.TYPE_IMAGE, piece, scipt, null));
                    } else {
                        deviceLogger.error("[splitBitmap] printType error.");
                        throw new Exception();
                    }
                }
            });
            if (isRecycle && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        } else {
            if (printType == PrintItem.TYPE_TEXT) {
                StringBuilder printText = new StringBuilder();
                printText.append(SCRIPT_IMAGEMODE).append("*image l").append(" ").append(bitmap.getWidth()).append("*").append(bitmap.getHeight()).append(" path:").append(PrintItem.PLACEHOLDER_IMAGE).append("\n");
                mLinkedQueue.add(new PrintItem(PrintItem.TYPE_TTTO_IMAGE, bitmap, printText.toString(), null));
            } else if (printType == PrintItem.TYPE_IMAGE) {
                float[] whs = getImageWidthHeightScale(bitmap, printItem);
                String scipt = printItem.getScript().replace(PrintItem.PLACEHOLDER_IMAGE_WIDTH, (int) whs[0] + "");
                scipt = scipt.replace(PrintItem.PLACEHOLDER_IMAGE_HEIGHT, (int) whs[1] + "");
                if (isRecycle) {//表示图片大于maxWidth,是压缩后新的图片,需要回收;否则是调用者传入的图片不能回收;
                    mLinkedQueue.add(new PrintItem(PrintItem.TYPE_IMAGE, bitmap, scipt, null));
                } else {
                    mLinkedQueue.add(new PrintItem(PrintItem.TYPE_IMAGE, null, scipt, printItem.getFormat()));
                }
            } else {
                deviceLogger.error("[splitBitmap] printType error..");
                throw new Exception();
            }
        }
    }

    private float[] getImageWidthHeightScale(Bitmap bitmap, PrintItem printItem) {
        NImageFormat nImageFormat = (NImageFormat) printItem.getFormat();
        float scale = 1;
        int formatWidth = nImageFormat.getWidth();
        int formatHeight = nImageFormat.getHeight();
        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();
        int targetWidth, targetHeight;
        int maxWidth = (int) mPrinterHelper.getMaxWidth();
        if (formatWidth <= 0 && formatHeight <= 0) {
            if (imageWidth > maxWidth) {
                scale = maxWidth * 1.0f / imageWidth;
                targetWidth = maxWidth;
                targetHeight = (int) (imageHeight * scale);
            } else {
                targetWidth = imageWidth;
                targetHeight = imageHeight;
            }
        } else {
            targetWidth = formatWidth;
            targetHeight = formatHeight;
            scale = formatWidth * 1.0f / imageWidth;
        }
        return new float[]{targetWidth, targetHeight, scale};
    }

    private NPrintErrorCode getPrintErrorCode(int ret) {
        if (ret == -6) {
            return NPrintErrorCode.PARAM_ERROR;
        } else if (ret == -7) {
            return NPrintErrorCode.INVALID_FILE_PATH;
        } else if (ret == 8) {
            return NPrintErrorCode.BUSY;
        } else if (ret == 2) {
            return NPrintErrorCode.OUTOF_PAPER;
        } else if (ret == 4) {
            return NPrintErrorCode.HEAT_LIMITED;
        } else if (ret == 112 || ret == 1024) {
            return NPrintErrorCode.DESTROYED;
        } else if (ret == 2048) {
            return NPrintErrorCode.PPSERR;
        } else if (ret == 512) {
            return NPrintErrorCode.CUTTER_ERROR;
        } else {
            return NPrintErrorCode.FAILED;
        }
    }

    private void printerEventWait() {
        synchronized (mPrinterEventSync) {
            try {
                deviceLogger.debug("[printerEventWait]");
                mPrinterEventSync.wait(4000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void printerEventNotify() {
        synchronized (mPrinterEventSync) {
            mPrinterEventSync.notify();
            deviceLogger.debug("[printerEventNotify]");
        }
    }

    @Override
    public boolean isStandardModule() {
        return false;
    }

    @Override
    public ModuleType getStandardModuleType() {
        return null;
    }

    @Override
    public String getExModuleType() {
        return null;
    }
}
