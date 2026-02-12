package com.newland.sdk.me.module.cardreader;


import android.util.Log;

/**
 * Author by bxy, Date on 2019/8/13 0013.
 */
public class CardReaderHelper {
    private static final String TAG = "CardReaderHelper";
    private static Object openWait = new Object();
    public static void openCardReaderWait(){
        try {
            synchronized (openWait) {
                Log.e(TAG,"[openCardReaderWait]openCardReaderWait Start");
                openWait.wait(1000);
                Log.e(TAG,"[openCardReaderWait]openCardReaderWait End");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void openCardReaderNotify(){
        try {
            synchronized (openWait) {
                openWait.notify();
                Log.e(TAG,"[openCardReaderNotify]");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
