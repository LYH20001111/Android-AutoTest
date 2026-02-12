package com.newland.nsdkdemo.internal.print;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.nio.charset.StandardCharsets;
import java.util.Hashtable;

public class QrcodeUtils {
    private static final int BLACK = 0xff000000;
    private static final int WHITE = 0xffffffff;
    /**
     * Create QR code bitmap
     *
     * @param code QR code
     * @return QR code
     */
    public static Bitmap create2dCode(String code)  {
        if (TextUtils.isEmpty(code)) {
            return null;
        }
        BitMatrix matrix;
        try {
            matrix = new MultiFormatWriter().encode(code,
                    BarcodeFormat.QR_CODE, 300, 300);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = BLACK;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * Create QR code bitmap
     *
     * @param code  QR code
     * @param qrLen bitmap side length
     * @return QR code
     */
    public static Bitmap create2dCode(String code, int qrLen) {
        return create2dCode(code,qrLen, ErrorCorrectionLevel.L);
    }

    /**
     * Create QR code bitmap
     *
     * @param code  QR code
     * @param qrLen bitmap side length
     * @param errorCorrection error correction level
     * @return QR code
     */
    public static Bitmap create2dCode(String code, int qrLen, ErrorCorrectionLevel errorCorrection){
        if (TextUtils.isEmpty(code)) {
            return null;
        }
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.MARGIN, 1);
        hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
        hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrection);
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(code, BarcodeFormat.QR_CODE, qrLen, qrLen, hints);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }

        int[] pixels = new int[qrLen * qrLen];
        for (int y = 0; y < qrLen; y++) {
            for (int x = 0; x < qrLen; x++) {
                if (bitMatrix.get(x, y)) {
                    pixels[y * qrLen + x] = BLACK;
                } else {
                    pixels[y * qrLen + x] = WHITE;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(qrLen, qrLen, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, qrLen, 0, 0, qrLen, qrLen);
        return bitmap;
    }
}
