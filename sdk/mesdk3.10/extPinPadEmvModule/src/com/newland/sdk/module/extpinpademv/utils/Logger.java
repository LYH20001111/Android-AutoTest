package com.newland.sdk.module.extpinpademv.utils;

import android.util.Log;

/**
 * Author by bxy, Date on 2019/12/30.
 */
public class Logger{
    private static final boolean isDebug = true;
    public static void d(String tag,String msg){
        if(!isDebug){
            return;
        }
        Log.d(tag,msg);
    }
}