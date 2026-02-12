package com.newland.nsdkdemo.internal.pin;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public class SecondDisplayManager implements DisplayManager.DisplayListener {
    private static final String TAG = "SecondDisplayManager";
    private final Context context;
    private SecondDisplayPresentation presentation = null;
    private int layoutId;
    private final View view;
    private boolean mustShow = false;
    private DisplayManager manager;


    public SecondDisplayManager(Context context, int layoutId) {
        this.context = context;
        this.layoutId = layoutId;
        this.view = null;
        init();
    }


    public SecondDisplayManager(Context context, View view) {
        this.context = context;
        this.view = view;
        init();
    }

    public Display getDisplay() {
        if (presentation != null) {
            return presentation.getDisplay();
        }
        return null;
    }

    private void init() {
        Log.d(TAG, "init");
        Display display = getSecondDisplay(context);
        if (display != null) {
            Log.d(TAG, "display id = " + display.getDisplayId());
            if (view != null) {
                presentation = new SecondDisplayPresentation(view, display);
            } else {
                presentation = new SecondDisplayPresentation(context, layoutId, display);
            }
        }
        manager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        presentation.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        manager.registerDisplayListener(this, new Handler());
    }

    public Window getWindow() {
        if (presentation != null) {
            return presentation.getWindow();
        }
        return null;
    }

    public void show() {
        Log.d(TAG, "show");
        if (presentation != null && !presentation.isShowing()) {
            presentation.show();
        }
        mustShow = true;
    }

    public void dismiss() {
        Log.d(TAG, "dismiss");
        if (presentation != null && presentation.isShowing()) {
            presentation.dismiss();
        }
        mustShow = false;
    }

    public void release() {
        manager.unregisterDisplayListener(this);
    }

    public View getContentView() {
        if (presentation != null) {
            return presentation.contentView;
        }
        return null;
    }


    public <T extends View> T findViewById(@IdRes int id) {
        return presentation.findViewById(id);
    }



    @Override
    public void onDisplayAdded(int displayId) {
        Log.d(TAG, "display added " + displayId);
        Display display = getSecondDisplay(context);
        if (display != null && display.getDisplayId() == displayId) {
            if (presentation != null) {
                if (presentation.getDisplay().getDisplayId() == displayId) {
                    return;
                }
                presentation.dismiss();
            }
            if (view != null) {
                if (view.getParent() != null) {
                    ((ViewGroup) (view.getParent())).removeView(view);
                }
                presentation = new SecondDisplayPresentation(view, display);
            } else {
                presentation = new SecondDisplayPresentation(context, layoutId, display);
            }
            if (mustShow) {
                presentation.show();
            }
        }
    }

    @Override
    public void onDisplayRemoved(int displayId) {
        Log.d(TAG, "display removed " + displayId);
        if (presentation != null && presentation.getDisplay().getDisplayId() == displayId) {
            dismiss();
            presentation = null;
        }
    }

    @Override
    public void onDisplayChanged(int displayId) {
        Log.d(TAG, "display changed " + displayId);
    }

    public static Display getSecondDisplay(Context context) {
        DisplayManager manager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        Display[] displays = manager.getDisplays();
        if (displays.length > 1) {
            return displays[1];
        }
        return null;
    }
}
