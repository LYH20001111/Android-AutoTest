package com.newland.nsdk.core.internal.printer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;

import com.newland.me.module.printer.TTFPrint;
import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.internal.devicemanager.DeviceManager;
import com.newland.nsdk.core.api.internal.exception.NSDKNDKException;
import com.newland.nsdk.core.api.internal.printer.Printer;
import com.newland.nsdk.core.api.internal.printer.PrinterStatus;
import com.newland.nsdk.core.api.internal.printer.PrintingParameters;
import com.newland.nsdk.core.api.internal.printer.PrintingResultListener;
import com.newland.nsdk.core.api.internal.printer.PrnParams;
import com.newland.nsdk.core.common.NSDKExecutors;
import com.newland.nsdk.core.internal.NSDKModuleManagerImpl;
import com.newland.nsdk.core.internal.jni.NSDKJni;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Printer module.
 */
public class PrinterImpl implements Printer {
    private static final String TAG = "PrinterImpl";
    private static final String PATH = "/data/share/nsdkPrintBitmap";

    public boolean isSupported;

    private volatile static PrinterImpl instance;
    private int gray = 5;
    private int highQualityGray = -1;

    public static PrinterImpl getInstance(boolean isSupported) {
        if (instance == null) {
            synchronized (PrinterImpl.class) {
                if (instance == null || instance.isSupported != isSupported) {
                    instance = new PrinterImpl(isSupported);
                }
            }
        } else {
            if (instance.isSupported != isSupported) {
                instance = new PrinterImpl(isSupported);
            }
        }
        return instance;
    }

    private PrinterImpl() {
        NSDKJni.getInstance().NDK_PrnModuleInit();
        this.isSupported = true;
    }

    private PrinterImpl(boolean isSupported){
        NSDKJni.getInstance().NDK_PrnModuleInit();
        this.isSupported = isSupported;
    }

    private void isSupported() throws NSDKException {
        if(!isSupported){
            throw new NSDKException(ErrorCode.UNSUPPORTED, "UnSupported Printer Module");
        }
    }

    @Override
    public void printImage(final byte[] image, final int startX, final int imageWidth, final int imageHeight, final PrintingResultListener printListener) throws NSDKException {
        isSupported();

        if (printListener == null) {
            throw new NSDKIllegalParameterException("Printing listener is null.");
        }

        if (image == null) {
            throw new NSDKIllegalParameterException("Image data is null.");
        }

        if (startX < 0 || imageWidth <= 0 || imageHeight <= 0) {
            throw new NSDKIllegalParameterException("Invalid start X, image width, image length.");
        }

        NSDKExecutors.threadStart(new Runnable() {
            @Override
            public void run() {

                File imageFile = null;
                try {
                    File parentFile = new File(PATH);
                    if (!parentFile.exists()) {
                        parentFile.mkdirs();
                        parentFile.setWritable(true, false);
                        parentFile.setReadable(true, false);
                        parentFile.setExecutable(true, false);
                    }

                    imageFile = File.createTempFile("bitmap", ".png", parentFile);
                    imageFile.setWritable(true, false);
                    imageFile.setReadable(true, false);
                    imageFile.setExecutable(true, false);

                    FileOutputStream fOut = new FileOutputStream(imageFile);

                    // 这里将外面传进来的图片数据转成 bitmap 后再压缩成 png 的，
                    // 是为了支持外部可以以不同的图片格式传进来数据，然后这里统一转成 png 的格式传给底层
                    Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();

                    String printData = String.format(Locale.US, "*image x:%d %d*%d path:%s\n", startX, imageWidth, imageHeight, imageFile.getAbsolutePath());
                    int ret = TTFPrint.getInstance().PrintScipt(printData.getBytes(StandardCharsets.UTF_8), printData.getBytes(StandardCharsets.UTF_8).length, 1);
                    LogUtils.d(TAG, ">>>NSDK TTFPrint ret=" + ret);
                    printListener.onEventRaised(ret);
                    resetPrnParams(gray);
                } catch (Exception e) {
                    e.printStackTrace();
                    printListener.onEventRaised(ErrorCode.ERROR);
                } finally {
                    if (imageFile != null) {
                        imageFile.delete();
                    }
                }
            }
        });
    }

    @Override
    public void printImage(final Bitmap bitmap, final PrintingParameters parameters, final PrintingResultListener printingResultListener) throws NSDKException {
        if (bitmap == null || parameters == null || printingResultListener == null) {
            throw new NSDKIllegalParameterException("Bitmap, printing parameters and printing result listener shall not be null.");
        }
        if (parameters.getExpectedImageWidth() == 0 || parameters.getExpectedImageHeight() == 0) {
            throw new NSDKIllegalParameterException("Expected image width and height shall be more than 0.");
        }
        NSDKExecutors.threadStart(new Runnable() {
            @Override
            public void run() {
                int ret = TTFPrint.getInstance().ProcessBitmap(bitmap, parameters.getExpectedImageWidth(), parameters.getExpectedImageHeight(), parameters.getStartX(), 1);
                LogUtils.d(TAG, ">>>NSDK TTFPrint ret=" + ret);
                printingResultListener.onEventRaised(ret);
                resetPrnParams(gray);
            }
        });

    }

    @Override
    public void printImage(final String path, final PrintingParameters parameters, final PrintingResultListener printingResultListener) throws NSDKException {
        if (path == null || path.isEmpty()) {
            throw new NSDKIllegalParameterException("Image path shall not be null.");
        }
        if (parameters == null || printingResultListener == null) {
            throw new NSDKIllegalParameterException("Printing parameters and listener shall not be null.");
        }
        NSDKExecutors.threadStart(new Runnable() {
            @Override
            public void run() {
                String printData = String.format(Locale.US, "*image x:%d %d*%d path:%s\n", parameters.getStartX(), parameters.getExpectedImageWidth(), parameters.getExpectedImageHeight(), path);
                int ret = TTFPrint.getInstance().PrintScipt(printData.getBytes(StandardCharsets.UTF_8), printData.getBytes(StandardCharsets.UTF_8).length, 1);
                LogUtils.d(TAG, ">>>NSDK TTFPrint ret=" + ret);
                printingResultListener.onEventRaised(ret);
                resetPrnParams(gray);
            }
        });
    }

    @Override
    public void printRGBAImage(final byte[] image, final PrintingParameters parameters, final PrintingResultListener printListener) throws NSDKException {
        isSupported();

        if (parameters == null || printListener == null || image == null) {
            throw new NSDKIllegalParameterException("Printing image, parameters and listener shall not be null.");
        }

        if (parameters.getStartX() < 0 || parameters.getExpectedImageWidth() <= 0 || parameters.getExpectedImageHeight() <= 0 || parameters.getImageWidth() <= 0 || parameters.getImageHeight() <= 0) {
            throw new NSDKIllegalParameterException("Start X, image width, image height shall be >0.");
        }

        NSDKExecutors.threadStart(new Runnable() {
            @Override
            public void run() {

                File imageFile = null;
                try {
                    File parentFile = new File(PATH);
                    if (!parentFile.exists()) {
                        parentFile.mkdirs();
                        parentFile.setWritable(true, false);
                        parentFile.setReadable(true, false);
                        parentFile.setExecutable(true, false);
                    }

                    imageFile = File.createTempFile("bitmap", ".png", parentFile);
                    imageFile.setWritable(true, false);
                    imageFile.setReadable(true, false);
                    imageFile.setExecutable(true, false);

                    FileOutputStream fOut = new FileOutputStream(imageFile);
                    fOut.write(image);
                    fOut.flush();
                    fOut.close();

                    // !image后面的宽高是真实值。*image 后面的宽高是期望值。
                    String printData = String.format(Locale.US, "!image rgba %d*%d\n*image x:%d %d*%d path:%s\n",
                            parameters.getImageWidth(),
                            parameters.getImageHeight(),
                            parameters.getStartX(),
                            parameters.getExpectedImageWidth(),
                            parameters.getExpectedImageHeight(),
                            imageFile.getAbsolutePath());
                    int ret = TTFPrint.getInstance().PrintScipt(printData.getBytes(StandardCharsets.UTF_8), printData.getBytes(StandardCharsets.UTF_8).length, 1);
                    LogUtils.d(TAG, ">>>NSDK TTFPrint ret=" + ret);
                    printListener.onEventRaised(ret);
                    resetPrnParams(gray);
                } catch (Exception e) {
                    e.printStackTrace();
                    printListener.onEventRaised(ErrorCode.ERROR);
                } finally {
                    if (imageFile != null) {
                        imageFile.delete();
                    }
                }
            }
        });
    }


    @Override
    public PrinterStatus getStatus() throws NSDKException {
        isSupported();

        PrinterStatus status = null;
        int[] k = new int[1];
        int ret = NSDKJni.getInstance().NDK_PrnGetStatus(k);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to get printer status, result code = %d", ret));
        }

        switch (k[0]) {
            case 0:
                status = PrinterStatus.OK;
                break;
            case 2:
                status = PrinterStatus.NO_PAPER;
                break;
            case 4:
                status = PrinterStatus.OVERHEAT;
                break;
            case 8:
                status = PrinterStatus.BUSY;
                break;
            case 112:
                status = PrinterStatus.VOL_ERR;
                break;
            default:
                status = PrinterStatus.BAD;
                break;
        }
        return status;
    }

    @Override
    public void cutPaper() throws NSDKException {
        isSupported();
        isSupportedCutPaper();

        String command = "*feedline p:200\n*cut\n";
        int ret = TTFPrint.getInstance().PrintScipt(command.getBytes(StandardCharsets.UTF_8),command.getBytes(StandardCharsets.UTF_8).length,1);
        LogUtils.d(TAG, ">>>NSDK TTFPrint cutPaper ret=" + ret);
        if(ret != 0){
            throw new NSDKException(ErrorCode.ERROR, "Print cut paper error ret=" + ret);
        }
    }

    @Override
    public void setGray(int gray) throws NSDKException {
        isSupported();
        if(gray > 10 || gray < 1){
            throw new NSDKException(ErrorCode.PARAM_ERROR, "gray should in 1 to 10.");
        }
        this.gray = gray;
        String command = String.format(Locale.US, "!gray %d\n", gray);
        int ret = TTFPrint.getInstance().PrintScipt(command.getBytes(StandardCharsets.UTF_8),command.getBytes(StandardCharsets.UTF_8).length,1);
        LogUtils.d(TAG, ">>>NSDK TTFPrint setGray ret=" + ret);
        if(ret != 0){
            throw new NSDKException(ErrorCode.ERROR, "Print set gray error ret=" + ret);
        }
    }

    @Override
    public void setParams(PrnParams params) throws NSDKException{
        if (params == null) {
            throw new NSDKIllegalParameterException("Printing parameters shall not be null.");
        }
        int ret = 0;
        int gray = params.getHighQualityGray();
        if (gray == 1 && highQualityGray != -1) {
            gray = highQualityGray;
        } else {
            highQualityGray = gray;
        }
        if (params.isEnableHighQualityMode()) {
            ret = NSDKJni.getInstance().NDK_PrnSetParam(8, 1);
            String command = String.format(Locale.US, "!gray %d\n", gray);
            TTFPrint.getInstance().PrintScipt(command.getBytes(StandardCharsets.UTF_8), command.getBytes(StandardCharsets.UTF_8).length, 1);
        } else {
            ret = NSDKJni.getInstance().NDK_PrnSetParam(8, 0);
        }
        if (ret != 0) {
            LogUtils.d(TAG, "This type parameters can not set to driver.");
        }
    }

    @Override
    public void feedPaper() throws NSDKException {
        int ret = NSDKJni.getInstance().NDK_PrnFeedPaper();
        if (ret == ErrorCode.UNSUPPORTED) {
            String model = Build.MODEL;
            int feedPixels = 80;
            if ("N950".equalsIgnoreCase(model)) {
                feedPixels = 152;
            } else if ("N950S".equalsIgnoreCase(model) || model.contains("N950S-C")) {
                feedPixels = 112;
            } else if ("N960K".equals(model)) {
                feedPixels = 144;
            } else if ("X800".equals(model) || model.contains("S90")) {
                feedPixels = 178;
            } else if (model.contains("N910 Pro")) {
                feedPixels = 108;
            }
            ret = NSDKJni.getInstance().NDK_PrnFeedByPixels(feedPixels);
            if (ret != ErrorCode.OK) {
                throw new NSDKException(ret, String.format(Locale.US, "Failed to feed paper, ret = %d", ret));
            }
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to feed paper, ret = %d", ret));
        }
    }

    @Override
    public void print(String[] printContent, String[] saveContent, String path) throws NSDKException {
        isSupported();
        isSupportECR();
        if (printContent == null || printContent.length == 0) {
            throw new NSDKIllegalParameterException("Print contents shall not be null.");
        }
        if (saveContent == null || saveContent.length == 0) {
            throw new NSDKIllegalParameterException("Save content shall not be null.");
        }
        if (TextUtils.isEmpty(path)) {
            throw new NSDKIllegalParameterException("Record save path shall not be null.");
        }
        int ret = TTFPrint.getInstance().ecrPrint(printContent, saveContent, path);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to print ecr, ret = %d", ret));
        }
    }

    @Override
    public boolean verifyLast() throws NSDKException {
        isSupported();
        isSupportECR();
        return TTFPrint.getInstance().ecrVerifyLast();
    }

    @Override
    public void setFont(String fontPath) throws NSDKException {
        isSupported();
        isSupportECR();
        if (TextUtils.isEmpty(fontPath)) {
            throw new NSDKIllegalParameterException("Custom font path shall not be null.");
        }

        int ret = TTFPrint.getInstance().setFont(fontPath);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to set custom font, ret = %d", ret));
        }
    }

    void isSupportedCutPaper() throws NSDKException {
        NSDKModuleManagerImpl moduleManager = NSDKModuleManagerImpl.getInstance();
        DeviceManager deviceManager = (DeviceManager) moduleManager.getModule(ModuleType.DEVICE_MANAGER);
        String deviceModel = deviceManager.getDeviceInfo().getDeviceModel();
        LogUtils.d(TAG, ">>>NSDK Device Mode =" + deviceModel);
        if(!"CPOS X5".equals(deviceModel)){
            throw new NSDKException(ErrorCode.UNSUPPORTED, deviceModel + " do not support cut paper function");
        }
    }

    void isSupportECR() throws NSDKException{
        if (!Build.MODEL.contains("ECR")) {
            throw new NSDKException(ErrorCode.UNSUPPORTED, "This method can only be used in N950S-ECR devices");
        }
    }

    public Size getPrintingStringSize(String str) throws NSDKException {
        isSupported();

        if (str == null || str.isEmpty()) {
            return new Size(0, 0);
        }

        int[] width = new int[1];
        int[] height = new int[1];
        int ret = 0;

        ret = TTFPrint.getInstance().GetStrPrnSize(str.getBytes(), str.getBytes().length, width, height);

        if (ret != 0) {
            throw new NSDKNDKException(ret, "Failed to get size.");
        }

        return new Size(width[0], height[0]);
    }

    private void resetPrnParams(int gray) {
        NSDKJni.getInstance().NDK_PrnSetParam(8, 0);
        String command = String.format(Locale.US, "!gray %d\n", gray);
        TTFPrint.getInstance().PrintScipt(command.getBytes(StandardCharsets.UTF_8), command.getBytes(StandardCharsets.UTF_8).length, 1);
    }
}

