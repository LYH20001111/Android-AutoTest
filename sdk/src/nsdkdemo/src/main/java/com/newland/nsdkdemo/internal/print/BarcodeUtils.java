package com.newland.nsdkdemo.internal.print;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.text.TextPaint;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class BarcodeUtils {

    private static final int BLACK = 0xff000000;
    private static final int WHITE = 0xffffffff;

    /**
     * Create bar code bitmap
     *
     * @param code          bar code
     * @param format        bar type,such as：BarcodeFormat.CODE_128
     * @param desiredWidth  bar width,such as： 400
     * @param desiredHeight bar height,such as： 100
     * @return bar code bitmap
     */
    public static Bitmap createBarCode(String code, BarcodeFormat format, int desiredWidth, int desiredHeight) {
        BitMatrix result = null;
        try {
            result = new MultiFormatWriter().encode(code, format, desiredWidth, desiredHeight, null);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        // All are 0, or black, by default
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK :WHITE ;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * Create barcode bitmap with the code text below it
     *
     * @param code          bar code
     * @param format        bar type,such as：BarcodeFormat.CODE_128
     * @param desiredWidth  bar width,such as： 400
     * @param desiredHeight bar height,such as： 100
     * @return bar code bitmap
     */
    public static Bitmap createBarCodeWithText(String code, BarcodeFormat format, int desiredWidth, int desiredHeight) {
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(code, format, desiredWidth, desiredHeight, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        Bitmap nBitmp = Bitmap.createBitmap(width, height + 30, Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(nBitmp);
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        Paint paint = new Paint();
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawColor(WHITE);

        c.drawBitmap(bitmap, 0, 0, paint);
        // draw text
        TextPaint textPaint = new TextPaint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(20);
        textPaint.setColor(BLACK);
        textPaint.setStrokeWidth(0.5f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColorFilter(f);
        c.drawText(code, width / 2, height + 20, textPaint);
        textPaint.setColor(WHITE);
        textPaint.setStrokeWidth(10f);
        c.drawLine(0, 0, width, 0, textPaint);
        c.save();
        c.restore();
        return nBitmp;
    }
}
