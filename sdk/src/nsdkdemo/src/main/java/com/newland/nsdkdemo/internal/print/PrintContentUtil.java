package com.newland.nsdkdemo.internal.print;

import android.content.Context;
import android.graphics.Bitmap;

import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdkdemo.internal.print.generator.VoucherPrintItemGenerator;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

public class PrintContentUtil {
    private static PrintContentUtil mPrintContentUtil;
    private int width = 0;
    private int height = 0;

    private PrintContentUtil() {

    }

    public static PrintContentUtil getInstance() {
        if (mPrintContentUtil == null) {
            mPrintContentUtil = new PrintContentUtil();
        }

        return mPrintContentUtil;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public byte[] getPrintImage(Context context) {
        width = 0;
        height = 0;

        Bitmap bitmap = VoucherPrintItemGenerator.getInstance().getTemplateVoucher(context);
        if (bitmap == null) {
            return null;
        }
        width = bitmap.getWidth();
        height = bitmap.getHeight();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public byte[] getPrintRGBAImage(Context context) {
        width = 0;
        height = 0;
        Bitmap bitmap = VoucherPrintItemGenerator.getInstance().getTemplateVoucher(context);
        if (bitmap == null) {
            return null;
        }
        width = bitmap.getWidth();
        height = bitmap.getHeight();

        byte[] rgba = bitmapToRGBA(bitmap);
        LogUtils.d("PrintContentUtil", String.format(Locale.US, "width: %d, height: %d, rgba.length: %d", width, height, rgba.length));
        return rgba;
    }

    public static byte[] bitmapToRGBA(Bitmap bitmap) {
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        byte[] bytes = new byte[pixels.length * 4];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int i = 0;
        for (int pixel : pixels) {
            int a = (pixel >> 24) & 0xff;
            int r = (pixel >> 16) & 0xff;
            int g = (pixel >> 8) & 0xff;
            int b = pixel & 0xff;
            bytes[i++] = (byte) r;
            bytes[i++] = (byte) g;
            bytes[i++] = (byte) b;
            bytes[i++] = (byte) a;
        }
        return bytes;
    }



}
