package com.hudou.autotest.ui.keyboard;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class KeyBoardUtil {


    /**
     * Hide the soft input.
     *
     * @param activity The activity.
     */
    public static void hideSoftInput( Activity activity) {
        InputMethodManager imm =
                (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm == null) return;
        View view = activity.getCurrentFocus();
        if (view == null) view = new View(activity);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Hide the soft input.
     *
     * @param view The view.
     */
    public static void hideSoftInput(Context context, View view) {
        InputMethodManager imm =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        //noinspection ConstantConditions
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
