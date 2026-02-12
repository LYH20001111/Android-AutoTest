package com.newland.me.module.printer;

import android.graphics.Bitmap;

public class TTFPrint {
    private static TTFPrint instance = null;

    static {
        System.loadLibrary("nlprintex");
    }

    private TTFPrint() {

    }

    public static TTFPrint getInstance() {
        if (instance == null) {
            synchronized (TTFPrint.class) {
                if (instance == null) {
                    instance = new TTFPrint();
                }
            }
        }
        return instance;
    }

    public native int PrintScipt(byte[] data, int nLen, int flushFlag);

    public native int GetStrPrnSize(byte[] str, int strLen, int[] width, int[] height);

    public native int ecrPrint(String[] scriptArray, String[] recordArray, String recordPath);

    public native boolean ecrVerifyLast();

    public native int setFont(String fontPath);
    
    public native int ProcessBitmap(Bitmap bitmap, int expectedWidth, int expectHeight, int xpos, int flushFlag);

}
