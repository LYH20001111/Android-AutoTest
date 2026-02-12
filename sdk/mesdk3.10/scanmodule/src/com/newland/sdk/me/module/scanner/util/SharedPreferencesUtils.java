package com.newland.sdk.me.module.scanner.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferencesUtils {
    public static final String MESDK_SCAN_RESULT = "MESDK_SCAN_RESULT";

    private SharedPreferencesUtils() {
        throw new AssertionError();
    }

    public static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putBoolean(key, value);
        edit.apply();
    }

    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(key, false);
    }

    public static void setString(Context context, String key, String value) {
        try {
            SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
            edit.putString(key, value);
            edit.apply();
        }catch (Exception | Error e){
            e.printStackTrace();
        }
    }

    public static String getString(Context context, String key) {
        try {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            return sp.getString(key, "");
        }catch (Exception | Error e){
            e.printStackTrace();
        }
       return  "";
    }
}
