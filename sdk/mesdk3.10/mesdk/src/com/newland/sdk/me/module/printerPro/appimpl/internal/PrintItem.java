package com.newland.sdk.me.module.printerPro.appimpl.internal;

import android.graphics.Bitmap;

/**
 * @Description
 * @Author wuhh
 * @Date 2021/6/29
 */
public class PrintItem {
    public static final int TYPE_START = 1;
    public static final int TYPE_TEXT = 2;
    public static final int TYPE_TABLE = 3;
    public static final int TYPE_IMAGE = 4;
    public static final int TYPE_TTTO_IMAGE = 5;
    public static final int TYPE_SCRIPT = 6;
    public static final int TYPE_END = 7;
    public static final String PLACEHOLDER_IMAGE = "PLACEHOLDER_IMAGE";
    public static final String PLACEHOLDER_IMAGE_WIDTH = "PLACEHOLDER_IMAGE_WIDTH";
    public static final String PLACEHOLDER_IMAGE_HEIGHT = "PLACEHOLDER_IMAGE_HEIGHT";

    private int type;
    private Bitmap bitmap;//保存内部创建的图片,需要回收;
    private String script;
    private Object format;
    public PrintItem(int type,Bitmap bitmap,String script,Object format){
        this.type = type;
        this.bitmap = bitmap;
        this.script = script;
        this.format = format;
    }

    public int getType() {
        return type;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getScript() {
        return script;
    }

    public Object getFormat() {
        return format;
    }
}
