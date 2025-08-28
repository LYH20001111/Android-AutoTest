package com.hudou.autotest.util;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {
    private static SharedPreferences sharedPreferences;
    public static final String DEBUG_MODE = "debug_mode";
    public static final String IS_PHYSICAL_KEYBOARD = "isPhysicalKeyboard";
    public static final String TEST_CASE = "testcase";
    public static final String DEFAULT_VALUE = "default_value";

    /**
     * Init
     */
    public static void init(Context context) {
        sharedPreferences = context.getSharedPreferences("AutoTest-Pre", MODE_PRIVATE);
        saveInit();
    }

    private static void saveInit() {
        save(DEBUG_MODE, true);
    }

    /**
     * Save String
     *
     * @param key   param key
     * @param value save string value
     */
    public static void save(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Save boolean
     *
     * @param key   param key
     * @param value save boolean value
     */
    public static void save(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * Save int
     *
     * @param key   param key
     * @param value save int value
     */
    public static void save(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * Get String
     *
     * @param key          param key
     * @param defaultValue default value
     * @return param string value
     */
    public static String get(String key, String defaultValue) {
        if (sharedPreferences == null) {
            return defaultValue;
        }
        if (key == null || "".equals(key)) {
            return defaultValue;
        }
        String value = sharedPreferences.getString(key, null);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    /**
     * Get boolean
     *
     * @param key          param key
     * @param defaultValue default value
     * @return param string value
     */
    public static boolean get(String key, boolean defaultValue) {
        if (sharedPreferences == null) {
            return defaultValue;
        }
        if (key == null || "".equals(key)) {
            return defaultValue;
        }
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    /**
     * Get int
     *
     * @param key          param key
     * @param defaultValue default value
     * @return param int value
     */
    public static int get(String key, int defaultValue) {
        if (sharedPreferences == null) {
            return defaultValue;
        }
        if (key == null || "".equals(key)) {
            return defaultValue;
        }
        int value = sharedPreferences.getInt(key, 0);
        if (value == 0) {
            value = defaultValue;
        }
        return value;
    }

    /**
     * Clear all params
     */
    public static boolean clear() {
        if (sharedPreferences == null) {
            return false;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        return editor.clear().commit();
    }


}
