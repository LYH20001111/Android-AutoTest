package com.newland.nsdk.core.internal.BarcodeDecoder;

import android.content.Context;
import android.newland.os.NlBuild;
import android.newland.scan.ScanUtil;
import android.newland.scan.SoftEngine;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.internal.barcodedecoder.BarcodeDecoder;
import com.newland.nsdk.core.api.internal.barcodedecoder.DecodingByteCallback;
import com.newland.nsdk.core.api.internal.barcodedecoder.DecodingCallback;
import com.newland.nsdk.core.api.internal.barcodedecoder.IDecodingCallback;
import com.newland.nsdk.core.api.internal.exception.NSDKNDKException;

import java.io.UnsupportedEncodingException;

/**
 * The interface Barcode decoder spi.
 */
public class BarcodeDecoderImpl implements BarcodeDecoder, SoftEngine.ScanningCallback {

    private static final String TAG = "BarcodeDecoderImpl";
    private boolean isSupported;
    private Context context;

    private IDecodingCallback iDecodingCallback;
    /**
     * The flag of stop status
     */
    private volatile boolean isStop = true;
    private SoftEngine mSoftEngine = null;

    private volatile static BarcodeDecoderImpl instance;

    public static BarcodeDecoderImpl getInstance(Context context, boolean isSupported) {
        if (instance == null) {
            synchronized (BarcodeDecoderImpl.class) {
                if (instance == null || instance.isSupported != isSupported || instance.context != context) {
                    instance = new BarcodeDecoderImpl(context, isSupported);
                }
            }
        } else {
            if (instance.isSupported != isSupported || instance.context != context) {
                instance = new BarcodeDecoderImpl(context, isSupported);
            }
        }
        return instance;
    }

    private BarcodeDecoderImpl(Context context, boolean isSupported) {
        String[] sf = NlBuild.VERSION.NL_FIRMWARE.split("\\.");
        boolean isOldF7 = false;
        if (android.os.Build.MODEL.equalsIgnoreCase("FPOS F7") && null != sf && sf.length >= 3) {
            StringBuilder version = new StringBuilder();
            version = version.append(sf[0]).append(sf[1]).append(sf[2]);
            String versionStr = version.substring(1);
            isOldF7 = (versionStr.compareToIgnoreCase("1007") < 0);
        }
        LogUtils.d(TAG, "isOldF7: " + isOldF7);

        if (isOldF7) {
            // F7 V1.0.07 以下固件不支持带参数的 getInstance
            mSoftEngine = SoftEngine.getInstance();
        } else {
            mSoftEngine = SoftEngine.getInstance(context);
        }
        this.isSupported = isSupported;
    }

    private void isSupported() throws NSDKException {
        if(!isSupported){
            throw new NSDKException(ErrorCode.UNSUPPORTED, "UnSupported BarcodeDecoder Module");
        }
    }

    /**
     * 因为 x5 固件解码库的配置跟 A7 不一样，固件目前没有计划更新解码库，所以只能 nsdk 进行适配，将 x5 的解码库配置设置成跟 A7 一样
     */
    public void configCPOSX5(Context context) {
        ScanUtil scanUtil = new ScanUtil(context);
        scanUtil.init();
        // code93：x5 比 A7 解码出来的值多了“RT”，“Check”打开就不会上送“RT”了
        int ret = scanUtil.setNlsScn (CodeID.CODE93, "Check", "1");
        LogUtils.d(TAG, "set CODE93 Check ret: " + ret);
        // EAN_8：x5 比 A7 解码出来的值少了最后一位校验位
        ret = scanUtil.setNlsScn (CodeID.EAN8, "TrsmtChkChar", "1");
        LogUtils.d(TAG, "set EAN8 Check TrsmtChkChar: " + ret);
        // EAN _13：x5 比 A7 解码出来的值少了最后一位校验位
        ret = scanUtil.setNlsScn (CodeID.EAN13, "TrsmtChkChar", "1");
        LogUtils.d(TAG, "set EAN13 TrsmtChkChar ret: " + ret);
        // UPC_A：x5 比 A7 解码出来的值少了最后一位校验位
        ret = scanUtil.setNlsScn (CodeID.UPCA, "TrsmtChkChar", "1");
        LogUtils.d(TAG, "set UPCA TrsmtChkChar ret: " + ret);
        // UPC_E：x5 比 A7 解码出来的值少了第一位传统字符“0”以及最后一位校验位
        ret = scanUtil.setNlsScn (CodeID.UPCE, "TrsmtSysDigit", "1");
        LogUtils.d(TAG, "set UPCE TrsmtSysDigit ret: " + ret);
        ret = scanUtil.setNlsScn (CodeID.UPCE, "TrsmtChkChar", "1");
        LogUtils.d(TAG, "set UPCE TrsmtChkChar ret: " + ret);
    }

    /**
     * Sets scanning callback.
     *
     * @param decodingCallback the scanning callback
     */
    @Override
    public void setDecodingCallback(IDecodingCallback decodingCallback) throws NSDKException {
        isSupported();

        if (decodingCallback == null) {
            throw new NSDKIllegalParameterException("Decoding callback is null.");
        }
        if (decodingCallback instanceof DecodingCallback) {
            LogUtils.d(TAG, "set DecodingCallback.");
            this.iDecodingCallback = (DecodingCallback) decodingCallback;
        } else if (decodingCallback instanceof DecodingByteCallback) {
            LogUtils.d(TAG, "set DecodingByteCallback.");
            this.iDecodingCallback = (DecodingByteCallback) decodingCallback;
        }
        mSoftEngine.setScanningCallback(this);
    }

    /**
     * Start decode boolean.
     *
     * @param imageData the image data
     * @param nWidth    the width of image
     * @param nHeight   the height of image
     */
    @Override
    public void startDecode(byte[] imageData, int nWidth, int nHeight) throws NSDKException {
        isSupported();

        if (iDecodingCallback == null) {
            throw new NSDKIllegalParameterException("Decoding callback shall not be null. Please set decoding callback first.");
        }

        if (imageData == null || nWidth <= 0 || nHeight <= 0) {
            throw new NSDKIllegalParameterException("Image data shall not be null, and width/height shall be > 0.");
        }

        if (mSoftEngine == null) {
            throw new NSDKException("Soft engine is null.");
        }
        isStop = false;
        boolean ret = mSoftEngine.startDecode(imageData, nWidth, nHeight);
        if (!ret) {
            throw new NSDKNDKException("Failed to start decoding.");
        }
    }

    /**
     * Stop decode boolean.
     *
     * @return the boolean
     */
    @Override
    public void stopDecode() throws NSDKException {
        isSupported();

        LogUtils.d(getClass().getName(), "stopDecode");
        if (isStop) {
            return;
        }

        // 一般没初始化，没执行 startDecode 的情况下会返回 false
        boolean ret = mSoftEngine.stopDecode();

        if (!ret) {
            throw new NSDKException("Failed to stop decoding.");
        }

        isStop = true;
    }

    /**
     * SoftEngine 回调函数
     *
     * @param eventCode
     * @param param1
     * @param param2
     * @param length
     */
    @Override
    public void onScanningCallback(int eventCode, int param1, byte[] param2, int length) {
        if (iDecodingCallback != null) {
            if (eventCode == 1) {
                if (iDecodingCallback instanceof DecodingCallback) {
                    String strResult = doEncodingStr(param2, param2.length);
                    ((DecodingCallback) iDecodingCallback).onDecodingCallback(1, strResult);
                } else if (iDecodingCallback instanceof DecodingByteCallback) {
                    LogUtils.d(TAG, "enter branch 2");
                    ((DecodingByteCallback) iDecodingCallback).onDecodingByteCallback(1, param2);
                }
            }
            isStop = true;
        }
    }

    //UTF-8编码
    public static final String UTF8 = "UTF-8";
    //GBK编码
    public static final String GBK = "GBK";

    /**
     * 对元数据中的汉字部分进行UTF-8或者GBK格式的转化
     *
     * @param bytes  要转换的元数据
     * @param length 元数据的有效长度
     * @return 转换后的字符串
     * @Author weicx@newlandpayment.com
     */
    private static String doEncodingStr(byte[] bytes, int length) {
        if (bytes == null || bytes.length < length) {
            return null;
        }
        String encoding = guessEncodings(bytes, length) ? UTF8 : GBK;

        // 先判断是不是属于ASCII码值
        StringBuffer result = new StringBuffer();
        if (encoding.equals(UTF8)) {
            result.append(new String(bytes, 0, length));
        } else {
            // 如果有汉字，则转化为GBK格式编码
            int index = 0;
            while (index < length) {
                int value = bytes[index] & 0xff;
                if (value < 0x7f) {
                    result.append((char) bytes[index]);
                    index++;
                    continue;
                } else {
                    try {
                        if (index + 2 < bytes.length) {
                            result.append(new String(bytes, index, 2, GBK));
                        } else {
                            // 快要越界了，无法按照2个2个来合并
                            result.append(new String(bytes, index, bytes.length - index));
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        if (index + 2 < bytes.length) {
                            result.append(new String(bytes, index, 2));
                        } else {
                            result.append(new String(bytes, index, bytes.length - index));
                        }
                    }
                    index += 2;
                    continue;
                }
            }
        }

        return result.toString();
    }


    /**
     * 猜测编码格式，只支持GBK和UTF-8的编码格式
     *
     * @param contents 要测试的元数据
     * @param length   元数据的有效长度
     * @return 是否为UTF-8编码格式
     * @Author weicx@newlandpayment.com
     */
    private static boolean guessEncodings(byte[] contents, int length) {
        boolean isUTF8 = true;

        int index = 0;
        while (index < length) {
            int value = contents[index] & 0xff;
            if (value <= 127) {
                index++;
                continue;
            } else if ((value & 0xf0) == 0xf0) {// 4
                if (index + 3 < length && (contents[index + 1] & 0xc0) == 0x80
                        && (contents[index + 2] & 0xc0) == 0x80
                        && (contents[index + 3] & 0xc0) == 0x80) {
                    index += 4;
                    continue;
                } else {
                    isUTF8 = false;
                    break;
                }
            } else if ((value & 0xf0) == 0xe0) {// 3
                if (index + 2 < length && (contents[index + 1] & 0xc0) == 0x80
                        && (contents[index + 2] & 0xc0) == 0x80) {
                    index += 3;
                    continue;
                } else {
                    isUTF8 = false;
                    break;
                }
            } else if ((value & 0xf0) == 0xc0) {// 2
                if (index + 1 < length && (contents[index + 1] & 0xc0) == 0x80) {
                    index += 2;
                    continue;
                } else {
                    isUTF8 = false;
                    break;
                }
            } else {
                isUTF8 = false;
                break;
            }
        }

        return isUTF8;
    }
}
