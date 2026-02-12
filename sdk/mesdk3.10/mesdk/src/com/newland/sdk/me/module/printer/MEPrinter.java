package com.newland.sdk.me.module.printer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.newland.event.EventCallBack;
import com.newland.intelligent.jni.JniCmdInterface;
import com.newland.ndk.FileN;
import com.newland.ndk.NdkApiManager;
import com.newland.ndk.Print;
import com.newland.ndk.SysN;
import com.newland.sdk.me.cmd.printer.CmdPaperSize;
import com.newland.sdk.me.cmd.printer.CmdPapercut;
import com.newland.sdk.me.module.printerPro.appimpl.internal.PrinterHelper;
import com.newland.sdk.me.module.printerPro.meimpl.AppPrinter;
import com.newland.sdk.me.utils.DeviceInfoUtils;
import com.newland.sdk.me.utils.FileIOUtils;
import com.newland.sdk.me.utils.FileUtils;
import com.newland.sdk.module.printer.PrinterStatusListener;
import com.newland.sdk.module.printer.TextSize;
import com.newland.sdk.module.printerPro.NPrinterStatus;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.module.printer.ErrorCode;
import com.newland.sdk.module.printer.PaperSize;
import com.newland.sdk.module.printer.PrintListener;
import com.newland.sdk.module.printer.PrintScriptUtil;
import com.newland.sdk.module.printer.PrinterModule;
import com.newland.sdk.module.printer.PrinterStatus;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.AbstractModule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MEPrinter extends AbstractModule implements PrinterModule {

    private static final String PATH = "/data/share/printBitmap";

    private DeviceLogger deviceLogger = DeviceLoggerFactory.getLogger("MEPrinter");

    private final int MAX_CMD_TIME_OUT = 90;

    private Print print;
    private SysN sysN;
    private Object statusSyncObj = new Object();
    private PrinterStatusListener  mStatusListener = null;
    private Object object = new Object();

    private AppPrinter mAppPrinter;
    private Context context;

    private static volatile int isHighQuality = 0;

    public MEPrinter(AbstractDevice device,Context context) {
        super(device);
        this.context = context;
        NdkApiManager ndkApiManager = NdkApiManager.getNdkApiManager();
        print = ndkApiManager.getPrint();
        sysN = ndkApiManager.getSysN();
        if(PrinterHelper.isEnablePrinterPro){
            mAppPrinter = new AppPrinter(context);
        }
        try {
            int initRet = print.NDK_PrnModuleInit();
            deviceLogger.info("[getStatus] NDK_PrnModuleInit = " + initRet);
            if (initRet == 0) {
                initRet = print.NDK_PrnCutterInit();
                deviceLogger.info("[getStatus] NDK_PrnCutterInit = " + initRet);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isStandardModule() {
        return false;
    }

    @Override
    public ModuleType getStandardModuleType() {
        return ModuleType.PRINTER;
    }

    @Override
    public String getExModuleType() {
        return null;
    }

    @Override
    public PrinterStatus getStatus() {
        try {
            int[] status = new int[1];
            int ret = print.NDK_PrnGetStatus(status);
            deviceLogger.debug("[getStatus]PrinterStatus ret:"+ret);
            if(ret == 0){
                deviceLogger.debug("[getStatus]PrinterStatus status:"+status[0]);

                if (status[0] == 0) {
                    return PrinterStatus.NORMAL;
                } else if (status[0] == 8) {
                    return PrinterStatus.BUSY;
                } else if (status[0] == 2) {
                    return PrinterStatus.OUTOF_PAPER;
                } else if (status[0] == 4) {
                    return PrinterStatus.OVER_HEAT;
                } else if (status[0] == 112) {
                    return PrinterStatus.LOW_VOLTAGE;
                } else if (status[0] == 1024) {
                    return PrinterStatus.DESTROYED;
                } else if (status[0] == 2048) {
                    return PrinterStatus.PPSERR;
                }else if(status[0] == 512){
                    return PrinterStatus.CUTTER_ERROR;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return PrinterStatus.DESTROYED;
    }

    @Override
    public PrintScriptUtil getPrintScriptUtil(Context context) {
        return MEPrintScriptUtil.getInstance(this, context);
    }

    @Override
    public void print(final String scriptData, final Map<String, Bitmap> map, final PrintListener printListener) {
        print0(scriptData, map, new PrintListener() {
            @Override
            public void onSuccess() {
                printListener.onSuccess();
                if(isHighQuality == 2){
                    setPrnSetParam(false);
                }
            }

            @Override
            public void onError(ErrorCode error, String msg) {
                printListener.onError(error,msg);
                if(isHighQuality == 2){
                    setPrnSetParam(false);
                }
            }
        });
    }

    public void print0(final String scriptData, final Map<String, Bitmap> map, final PrintListener printListener) {
        Log.d("Printer","[print] Printer Mode="+(PrinterHelper.isEnablePrinterPro?"AppSDKMode":"MESDKMode"));
        if(!closeMag()){
            printListener.onError(ErrorCode.FAILED, ErrorCode.FAILED.toString());
            return;
        }
        if(PrinterHelper.isEnablePrinterPro){
            synchronized (object) {
                deviceLogger.debug("[print] start......");
                mAppPrinter.print(scriptData, map, printListener);
            }
        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (object){
                        if (null == scriptData || (scriptData != null && scriptData.equals("") == true)) {
                            printListener.onSuccess(); // It call back in success when the data is empty.
                        }
                        String key = null;
                        Bitmap bitmap = null;
                        String printData = null;
                        FileOutputStream fOut = null;
                        File f = null;

                        try {
                            deleteFile(new File(PATH));
                            printData = scriptData;
                            if (map != null) {
                                Iterator<Map.Entry<String, Bitmap>> it = map.entrySet().iterator();
                                while (it.hasNext()) {
                                    boolean isNeedRecycle = false;
                                    Map.Entry<String, Bitmap> entry = it.next();
                                    key = entry.getKey();
                                    bitmap = entry.getValue();
                                    int maxWidth = 384;
                                    if (Build.MODEL.equals("CPOS X1")|| Build.MODEL.equals("CPOS X3")|| Build.MODEL.equals("CPOS X5")|| Build.MODEL.equals("STAR A-6300")) {
                                        maxWidth = 576;
                                    }
                                    Bitmap bitmap1 = bitmap;
                                    if (bitmap != null && bitmap.getWidth() > maxWidth) {
                                        bitmap1 = zoomImg(bitmap, maxWidth);
                                        isNeedRecycle = true;
                                    }
                                    //bitmap = convertToBMW(bitmap, bitmap.getWidth(), bitmap.getHeight(), 100);
                                    File parentFile = new File(PATH);
                                    if (!parentFile.exists()) {
                                        parentFile.mkdirs();
                                        parentFile.setWritable(true, false);
                                        parentFile.setReadable(true, false);
                                        parentFile.setExecutable(true, false);
                                    }
                                    f = File.createTempFile("bitmap", ".png", parentFile);
                                    // 赋予文件777权限
                                    f.setWritable(true, false);
                                    f.setReadable(true, false);
                                    f.setExecutable(true, false);
                                    fOut = new FileOutputStream(f);
                                    bitmap1.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                                    fOut.flush();
                                    fOut.close();
                                    if (isNeedRecycle && !bitmap1.isRecycled()) {
                                        bitmap1.recycle();
                                    }
                                    if (printData.indexOf("path:" + key + "\n") != -1) {
                                        printData = printData.replace("path:" + key + "\n", "path:" + f.getAbsolutePath() + "\n");
                                    }
                                    if (printData.indexOf("path:yz:0;" + key + "\n") != -1) {
                                        printData = printData.replace("path:yz:0;" + key + "\n", "path:yz:0;" + f.getAbsolutePath() + "\n");
                                    }
                                    Matcher m = Pattern.compile("path:yz:.*?" + key).matcher(printData);
                                    while (m.find()) {
                                        String tager = m.group().substring(0, m.group().length() - key.length());
                                        printData = printData.replace(tager + key + "\n", tager + f.getAbsolutePath() + "\n");
                                    }
                                }
                            }
                            try {
                                ErrorCode errorCode = ErrorCode.FAILED;
                                TTFPrint ttfPrint = TTFPrint.getInstance();
                                if(ttfPrint == null){
                                    deviceLogger.error("[print] TTFPrint == null");
                                    printListener.onError(errorCode, errorCode.toString());
                                    return;
                                }
                                if(isHighQuality == 1){
                                    isHighQuality = 2;
                                    setPrnSetParam(true);
                                }
                                int ret = ttfPrint.PrintScipt(printData.getBytes("UTF-8"), printData.getBytes("UTF-8").length,1);
                                deviceLogger.error("[print] SDK TTFPrint ret=" + ret);

                                if(ret == -1){//-1是TTF字体库文件损坏，删除文件，应用需要重新设置字体库文件
                                    deviceLogger.error( "[print] SDK TTFPrint ret -1, delete font file");
                                    try {
                                        String dir = context.getFilesDir() + File.separator + "fonts" + File.separator;
                                        File proFile = new File(dir);
                                        File[] files = proFile.listFiles();
                                        for (File file : files) {
                                            if (file.getName().endsWith(".ttc") || file.getName().endsWith(".TTC")) {
                                                String fontPath = dir + file.getName();
                                                boolean isSucess = new File(fontPath).delete();
                                                deviceLogger.info("delete " + fontPath + ": " + isSucess);
                                                break;
                                            }
                                        }
                                    } catch (Exception | Error e) {
                                        e.printStackTrace();
                                    }
                                    printListener.onError(ErrorCode.FONT_FILE_ERROR, "" + ret);
                                    return;
                                }
                                if (ret == 0) {
                                    printListener.onSuccess();
                                    return;
                                }
                                if (ret == -6) {
                                    errorCode = ErrorCode.PARAM_ERROR;
                                } else if (ret == -7) {
                                    errorCode = ErrorCode.INVALID_FILE_PATH;
                                } else if (ret == 8) {
                                    errorCode = ErrorCode.BUSY;
                                } else if (ret == 2) {
                                    errorCode = ErrorCode.OUTOF_PAPER;
                                } else if (ret == 4) {
                                    errorCode = ErrorCode.HEAT_LIMITED;
                                } else if (ret == 112 || ret == 1024) {
                                    errorCode = ErrorCode.DESTROYED;
                                } else if (ret == 2048) {
                                    errorCode = ErrorCode.PPSERR;
                                }else if(ret == 512){
                                    errorCode = ErrorCode.CUTTER_ERROR;
                                } else {
                                    errorCode = ErrorCode.FAILED;
                                }
                                printListener.onError(errorCode, ""+ret);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            printListener.onError(ErrorCode.FAILED, ErrorCode.FAILED.toString());
                        } finally {
                            deleteFile(new File(PATH));
                            System.gc();
                        }
                    }

                }
            }).start();
        }
    }

    @Override
    public String setFont(Context context, String name) {
        try {
            if (name == null || context == null) {
                return null;
            }
            String fileName;
            boolean isFileOK = false;
            // search in system dir.
            fileName = "/system/fonts/" + name;
            isFileOK = FileUtils.isFileExists(fileName);
            if (isFileOK) {
                deviceLogger.debug("[setFont] setFonts file is exists in system dir! fileName=" + fileName);
                File file = new File(fileName);
                file.setWritable(true, false);
                file.setReadable(true, false);
                file.setExecutable(true, false);
                return fileName;
            } else {
                // search file is assert
                deviceLogger.debug("[setFont] search file is assert dir.");
                String dir = context.getFilesDir() + File.separator + "fonts" + File.separator;
                File propFile = new File(dir);
                if (!propFile.exists()) {
                    boolean isSuccess = propFile.mkdir();
                    if (!isSuccess) {
                        deviceLogger.error("[setFont] mkdir fonts fails!!!");
                        return null;
                    }
                }
                File filePath = new File(dir + name);// The file absolute path
                boolean isExitsts = FileUtils.isFileExists(filePath);
                if(isExitsts && (filePath.length()<=0)){
                    deviceLogger.error("[setFont] file err Path="+filePath+" size="+filePath.length());
                    filePath.delete();
                    isExitsts = false;
                }
                if (!isExitsts) {
                    deviceLogger.debug("[setFont] " + filePath + " is not exists ");
                    InputStream inputStream = null;
                    try {
                        inputStream = context.getAssets().open(name);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                    isFileOK = FileIOUtils.writeFileFromIS(filePath, inputStream);
                    if (!isFileOK) {
                        deviceLogger.error("[setFont] writeFileFromIS failed.");
                        return null;
                    }
                }
                fileName = dir + name;
            }
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public boolean paperCut() {
        if(!closeMag()){
            return false;
        }
        deviceLogger.debug("[paperCut]");
        int[] status = new int[1];
        int ret = NdkApiManager.getNdkApiManager().getPrint().NDK_PrnGetStatus(status);
        if (ret == 0 && status[0] == 0) {
            ret = NdkApiManager.getNdkApiManager().getPrint().NDK_PrnCutterPerformance();
            if(ret == 0){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setPaperSize(PaperSize size) {
        if(!closeMag()){
            return false;
        }
        try {
            deviceLogger.debug("[setPaperSize] size:"+size);
            CmdPaperSize.CmdPaperSizeResponse response = (CmdPaperSize.CmdPaperSizeResponse) invoke(new CmdPaperSize(size));
            return response.getResultCode();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean paperFeed(int lineNum) {
        if(!closeMag()){
            return false;
        }
        try {
//            synchronized (PrinterHelper.printerSync){
                deviceLogger.debug("[paperFeed] lineNum:"+lineNum);
                String feedLine = "*feedline " + lineNum + "\n";
                int ret = TTFPrint.getInstance().PrintScipt(feedLine.getBytes("UTF-8"), feedLine.getBytes("UTF-8").length,1);
                deviceLogger.debug("[paperFeed] PrintScipt ret="+ret);
                if(ret == 0){
                    return true;
                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean setStatusListener(PrinterStatusListener listener) {
        deviceLogger.debug("==========[setStatusListener]listener:"+listener);
        synchronized (statusSyncObj){
            mStatusListener = listener;
            if(mStatusListener == null){
                return false;
            }
            int ret = sysN.NDK_SYS_RegisterEvent(64,23*60*60*1000,  new EventCallBack() {
                @Override
                public void callback(int event, int len, byte[] bytes) {
                    if(event == 0){
                        deviceLogger.debug("[setStatusListener]event == 0:"+event);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                cancelStatusListener();
                                setStatusListener(mStatusListener);
                            }
                        }).start();
                    }else {
                        deviceLogger.debug("[setStatusListener]event:"+event);
                        mStatusListener.onStatus(getStatus());
                    }

                }
            });
            deviceLogger.debug("[setStatusListener] Register Pin Event ret="+ret);
            if(ret == 0 || ret == -4007){
                PrinterHelper.hasRegisterPrinterEvent = true;
            }
            if(ret == 0){
                return true;
            }
        }
        return false;
    }

    @Override
    public void cancelStatusListener() {
        synchronized (statusSyncObj){
            PrinterHelper.hasRegisterPrinterEvent = false;
            int ret = sysN.NDK_SYS_UnRegisterEvent(64);
            deviceLogger.debug("[cancelStatusListener] UnRegister Pin Event ret="+ret);
        }
    }

    @Override
    public TextSize getTextSize(String param, String text) {
        if(!closeMag()){
            return null;
        }
        TTFPrint ttfPrint = TTFPrint.getInstance();
        if(param != null){
            try {
                int ret = ttfPrint.PrintScipt(param.getBytes("UTF-8"), param.getBytes("UTF-8").length,1);
                if(ret != 0){
                    deviceLogger.error("[getTextSize] param="+param+" error.");
                    return null;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        TTFPrint.Size size = ttfPrint.new Size(0, 0);
        int ret = ttfPrint.GetStrPrnSize(text, size);
        if(ret != 0) {
            deviceLogger.error("[getTextSize] GetStrPrnSize ret=" + ret);
        }
        return new TextSize(size.getWidth(),size.getHeight());
    }

    @Override
    public void printScriptByNDK(final String scriptData, @Nullable final Map<String, Bitmap> map, final PrintListener printListener) {
        try {
            deviceLogger.debug("[printByNDK]");
            if(printListener==null){
                deviceLogger.error("[printByNDK] printListener==null");
                return;
            }
            if(scriptData==null && map==null){
                deviceLogger.error("[printByNDK] scriptData==null");
                printListener.onError(ErrorCode.PARAM_ERROR,"print data is null");
                return;
            }
            if(Build.MODEL.startsWith("FPOS")){
                deviceLogger.error("[printByNDK] Has not SecModule");
                printListener.onError(ErrorCode.FAILED,"unsupport print");
                return;
            }
            if(!closeMag()){
                printListener.onError(ErrorCode.FAILED, ErrorCode.FAILED.toString());
                return;
            }
            if(PrinterHelper.isEnablePrinterPro){
                synchronized (object) {
                    deviceLogger.debug("[print] start......");
                    mAppPrinter.print(scriptData, map, printListener);
                }
            }else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (object){
                            if (null == scriptData || (scriptData != null && scriptData.equals("") == true)) {
                                printListener.onSuccess(); // It call back in success when the data is empty.
                            }
                            String key = null;
                            Bitmap bitmap = null;
                            String printData = null;
                            FileOutputStream fOut = null;
                            File f = null;

                            try {
                                deleteFile(new File(PATH));
                                printData = scriptData;
                                if (map != null) {
                                    Iterator<Map.Entry<String, Bitmap>> it = map.entrySet().iterator();
                                    while (it.hasNext()) {
                                        boolean isNeedRecycle = false;
                                        Map.Entry<String, Bitmap> entry = it.next();
                                        key = entry.getKey();
                                        bitmap = entry.getValue();
                                        int maxWidth = 384;
                                        if (Build.MODEL.equals("CPOS X1")|| Build.MODEL.equals("CPOS X3")|| Build.MODEL.equals("CPOS X5")|| Build.MODEL.equals("STAR A-6300")) {
                                            maxWidth = 576;
                                        }
                                        Bitmap bitmap1 = bitmap;
                                        if (bitmap != null && bitmap.getWidth() > maxWidth) {
                                            bitmap1 = zoomImg(bitmap, maxWidth);
                                            isNeedRecycle = true;
                                        }
                                        File parentFile = new File(PATH);
                                        if (!parentFile.exists()) {
                                            parentFile.mkdirs();
                                            parentFile.setWritable(true, false);
                                            parentFile.setReadable(true, false);
                                            parentFile.setExecutable(true, false);
                                        }
                                        f = File.createTempFile("bitmap", ".png", parentFile);
                                        // 赋予文件777权限
                                        f.setWritable(true, false);
                                        f.setReadable(true, false);
                                        f.setExecutable(true, false);
                                        fOut = new FileOutputStream(f);
                                        bitmap1.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                                        fOut.flush();
                                        fOut.close();
                                        if (isNeedRecycle && !bitmap1.isRecycled()) {
                                            bitmap1.recycle();
                                        }
                                        if (printData.indexOf("path:" + key + "\n") != -1) {
                                            printData = printData.replace("path:" + key + "\n", "path:" + f.getAbsolutePath() + "\n");
                                        }
                                        if (printData.indexOf("path:yz:0;" + key + "\n") != -1) {
                                            printData = printData.replace("path:yz:0;" + key + "\n", "path:yz:0;" + f.getAbsolutePath() + "\n");
                                        }
                                        Matcher m = Pattern.compile("path:yz:.*?" + key).matcher(printData);
                                        while (m.find()) {
                                            String tager = m.group().substring(0, m.group().length() - key.length());
                                            printData = printData.replace(tager + key + "\n", tager + f.getAbsolutePath() + "\n");
                                        }
                                    }
                                }
                                try {
                                    ErrorCode errorCode = ErrorCode.FAILED;
                                    if(Build.MODEL.startsWith("CPOS")){
                                        deviceLogger.debug("CPOS NDK_PrnModuleInit");
                                    }

                                    int ret = NdkApiManager.getNdkApiManager().getPrint().NDK_Script_Print(printData.getBytes("GBK"),printData.getBytes("GBK").length);

                                    deviceLogger.error("[print] SDK NDKPrint ret=" + ret);
                                    if (ret == 0) {
                                        printListener.onSuccess();
                                        return;
                                    }
                                    if (ret == -6) {
                                        errorCode = ErrorCode.PARAM_ERROR;
                                    } else if (ret == -7) {
                                        errorCode = ErrorCode.INVALID_FILE_PATH;
                                    } else if (ret == 8) {
                                        errorCode = ErrorCode.BUSY;
                                    } else if (ret == 2) {
                                        errorCode = ErrorCode.OUTOF_PAPER;
                                    } else if (ret == 4) {
                                        errorCode = ErrorCode.HEAT_LIMITED;
                                    } else if (ret == 112 || ret == 1024) {
                                        errorCode = ErrorCode.DESTROYED;
                                    } else if (ret == 2048) {
                                        errorCode = ErrorCode.PPSERR;
                                    }else if(ret == 512){
                                        errorCode = ErrorCode.CUTTER_ERROR;
                                    } else {
                                        errorCode = ErrorCode.FAILED;
                                    }
                                    printListener.onError(errorCode, errorCode.toString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                printListener.onError(ErrorCode.FAILED, ErrorCode.FAILED.toString());
                            } finally {
                                deleteFile(new File(PATH));
                                System.gc();
                            }
                        }

                    }
                }).start();
            }

        }catch (Exception e){
            e.printStackTrace();

        }
    }

    @Override
    public void setEnableHighQualityMode(boolean flag) {
        isHighQuality = flag?1:0;
        if(!flag){
            setPrnSetParam(flag);
        }
    }

    private void setPrnSetParam(boolean flag){
        int ret = NdkApiManager.getNdkApiManager().getPrint().NDK_PrnSetParam(8,flag?1:0);
        deviceLogger.debug("PrnSetParam flag="+flag+" ret="+ret);
    }

    /**
     * 处理图片
     *
     * @param bm       所要转换的bitmap
     * @param newWidth 新的宽
     * @return 指定宽高的bitmap
     */
    private Bitmap zoomImg(Bitmap bm, int newWidth) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleWidth);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    private void deleteFile(File file) {
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

    private boolean closeMag(){
        JniCmdInterface.getInstance().jniMposLibCmdCancel(1);
        int ret = NdkApiManager.getNdkApiManager().getMagCard().NDK_MagClose();
        Log.e("Printer","[closeMag] 3.10 ret="+ret);
        if(ret == -5){
            return false;
        }
        return true;
    }

//    private boolean saveParam(String param) {
//        FileN file = NdkApiManager.getNdkApiManager().getFileN();
//        String fileName = "/appfs/printhqm";
//        int fd = -1;
//        try {
//            fd = file.NDK_FsOpen(fileName, "w+");
//            deviceLogger.debug("FsOpen fd="+fd);
//            if (fd < 0) {
//                return false;
//            }
//            int len = file.NDK_FsWrite(fd,param.getBytes(),param.getBytes().length);
//            deviceLogger.debug("FsWrite len="+len);
//            if (len < 0 ) {
//                return false;
//            }
//            deviceLogger.debug("printhqm param="+param);
//            return true;
//        } finally {
//            int ret = file.NDK_FsClose(fd);
//        }
//    }
//    private  String getParam() {
//        FileN file = NdkApiManager.getNdkApiManager().getFileN();
//        String fileName = "/appfs/printhqm";
//        int fd = -1;
//        try {
//            int ret = file.NDK_FsExist(fileName);
//            //deviceLogger.debug("FsExist ret="+ret);
//            if(ret != 0){
//                return null;
//            }
//
//            fd = file.NDK_FsOpen(fileName, "r");
//            //deviceLogger.debug("FsOpen fd="+fd);
//            if (fd < 0) {
//                return null;
//            }
//
//            int[] size = new int[1];
//            ret = file.NDK_FsFileSize(fileName,size);
//            //deviceLogger.debug("FsFileSize ret="+ret);
//            if(ret != 0){
//                return null;
//            }
//
//            byte[] buffer = new byte[size[0]];
//            int len = file.NDK_FsRead(fd, buffer, buffer.length);
//            //deviceLogger.debug("FsRead len="+len);
//            if (len < 0) {
//                return null;
//            }
//            String result = new String(buffer);
//            //deviceLogger.debug("getParam HighQuality result="+result);
//            return result;
//        } finally {
//            int ret = file.NDK_FsClose(fd);
//            //deviceLogger.debug("FsClose ret="+ret);
//        }
//    }
}
