package com.newland.sdk.me.module.printer;

/**
 * <p>Print by TTF in SDK2.0.</p>
 * <p>Because the way calls the print library early in JNI is through the bound the package name.</p>
 *
 * @author linsi
 * @since V3.10.01
 */
public class TTFPrint {
    static {
        try {
            System.loadLibrary("nlprintex");
        } catch (Throwable e) {
            libLoadSucc = false;
            e.printStackTrace();
        }
    }

    private static TTFPrint instance = null;
    private static boolean libLoadSucc = true;
    private TTFPrint() {
    }
    public static TTFPrint getInstance() {
        if(!libLoadSucc){
            return null;
        }
        if (instance == null) {
            synchronized (TTFPrint.class) {
                if (instance == null) {
                    instance = new TTFPrint();
                }
            }
        }
        return instance;
    }

    public class Size {
        int width = 0;
        int height = 0;

        public Size(int w, int h) {
            width = w;
            height = h;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

    public int GetStrPrnSize(String str, Size size) {
        int[] width = new int[1];
        int[] height = new int[1];
        int ret = 0;

        ret = GetStrPrnSize(str.getBytes(), str.getBytes().length, width, height);

        size.setWidth(width[0]);
        size.setHeight(height[0]);

        return ret;
    }

    private native int GetStrPrnSize(byte[] str, int strLen, int[] width, int[] height);
    public native int PrintScipt(byte[] data, int nLen,int flag);
}
