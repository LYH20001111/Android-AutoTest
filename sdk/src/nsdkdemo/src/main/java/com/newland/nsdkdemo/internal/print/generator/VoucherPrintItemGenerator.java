package com.newland.nsdkdemo.internal.print.generator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;

import com.google.zxing.BarcodeFormat;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.internal.print.BarcodeUtils;
import com.newland.nsdkdemo.internal.print.BitmapDraw;
import com.newland.nsdkdemo.internal.print.QrcodeUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VoucherPrintItemGenerator {
    private static VoucherPrintItemGenerator voucherPrintItemGenerator;

    public static VoucherPrintItemGenerator getInstance() {
        //DCL
        if (voucherPrintItemGenerator == null) {
            synchronized (VoucherPrintItemGenerator.class) {
                if (voucherPrintItemGenerator == null) {
                    voucherPrintItemGenerator = new VoucherPrintItemGenerator();
                }
            }

        }
        return voucherPrintItemGenerator;
    }

    /**
     * To get the template voucher.
     * @param mContext  Activity context.
     * @return TemplateVoucher bitmap data.
     */
    public Bitmap getTemplateVoucher(Context mContext) {
        BitmapDraw bitmapDraw = new BitmapDraw(mContext);
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.newland_npt);
        bitmapDraw.image(bitmap);
        bitmapDraw.text("----------------------------------------------", BitmapDraw.NORMAL_FONT_SIZE, false, Paint.Align.CENTER);
        bitmapDraw.text("MERCHANT COPY", BitmapDraw.SMALL_FONT_SIZE, false, Paint.Align.LEFT);
        bitmapDraw.text("----------------------------------------------", BitmapDraw.NORMAL_FONT_SIZE, false, Paint.Align.CENTER);
        bitmapDraw.text("MNAME:payment test", BitmapDraw.NORMAL_FONT_SIZE, false, Paint.Align.LEFT);
        bitmapDraw.text("MERCHANTID:308350193990080", BitmapDraw.NORMAL_FONT_SIZE, false, Paint.Align.LEFT);
        bitmapDraw.text("TERMINAL ID:09101515", BitmapDraw.NORMAL_FONT_SIZE, false, Paint.Align.LEFT);
        bitmapDraw.text("----------------------------------------------", BitmapDraw.NORMAL_FONT_SIZE, false, Paint.Align.CENTER);
        bitmapDraw.text("PAN:", BitmapDraw.NORMAL_FONT_SIZE, false, Paint.Align.LEFT);
        bitmapDraw.text("621700******0826/I", BitmapDraw.LARGE_FONT_SIZE, false, Paint.Align.LEFT);
        bitmapDraw.text("Expdate:2208", BitmapDraw.NORMAL_FONT_SIZE, false, Paint.Align.LEFT);
        bitmapDraw.text("Serial NO.:001", BitmapDraw.NORMAL_FONT_SIZE, false, Paint.Align.LEFT);
        bitmapDraw.text("Trans Type:", BitmapDraw.NORMAL_FONT_SIZE, false, Paint.Align.LEFT);
        bitmapDraw.text("SALE", BitmapDraw.LARGE_FONT_SIZE, false, Paint.Align.LEFT);
        bitmapDraw.text("Batch NO.:000008", BitmapDraw.NORMAL_FONT_SIZE, false, Paint.Align.LEFT);
        bitmapDraw.text("Trace NO.:000213", BitmapDraw.NORMAL_FONT_SIZE, false, Paint.Align.LEFT);
        bitmapDraw.text("Amount: $ 0.12", BitmapDraw.LARGE_FONT_SIZE, false, Paint.Align.LEFT);
        bitmapDraw.text("----------------------------------------------", BitmapDraw.NORMAL_FONT_SIZE, false, Paint.Align.CENTER);
        bitmapDraw.image(BarcodeUtils.createBarCode("1234567890", BarcodeFormat.CODE_93, 800, 120));
        bitmapDraw.image(QrcodeUtils.create2dCode("101010", 200));
        return bitmapDraw.getBitmap();
    }

}
