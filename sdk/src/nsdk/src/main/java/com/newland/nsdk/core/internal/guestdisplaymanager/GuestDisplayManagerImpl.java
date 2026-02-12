package com.newland.nsdk.core.internal.guestdisplaymanager;

import android.text.TextUtils;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.internal.guestdisplaymanager.GuestDisplayManager;
import com.newland.nsdk.core.internal.jni.NSDKJni;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class GuestDisplayManagerImpl implements GuestDisplayManager {
    private static final String TAG = "GuestDisplayManagerImpl";
    private boolean isSupported;
    private static volatile GuestDisplayManagerImpl instance;

    public static GuestDisplayManagerImpl getInstance(boolean isSupported) {
        if (instance == null) {
            synchronized (GuestDisplayManagerImpl.class) {
                if (instance == null || instance.isSupported != isSupported) {
                    instance = new GuestDisplayManagerImpl(isSupported);
                }
            }
        } else {
            if (instance.isSupported != isSupported) {
                instance = new GuestDisplayManagerImpl(isSupported);
            }
        }
        return instance;
    }

    private GuestDisplayManagerImpl(boolean isSupported) {
        this.isSupported = isSupported;
    }
    @Override
    public void setBacklightStatus(boolean isBacklightOn) throws NSDKException {
        isSupported();
        int backlightStatus = 0;
        if (isBacklightOn) {
            backlightStatus = 1;
        }

        int ret = NSDKJni.getInstance().NDK_ScrBacklight(backlightStatus);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to set backlight status, ret = %d", ret));
        }

    }

    @Override
    public void displayString(int startX, int startY, String content) throws NSDKException {
        isSupported();
        if (startX < 0 || startX > 128) {
            throw new NSDKIllegalParameterException("StartX shall range from 0 to 128");
        }
        if (startY < 0 || startY > 4) {
            throw new NSDKIllegalParameterException("StartY shall range from 0 to 4.");
        }
        if (TextUtils.isEmpty(content)) {
            throw new NSDKIllegalParameterException("Display content shall not be null.");
        }

        int ret = NSDKJni.getInstance().NDK_ScrDispString(startX, startY, content, 0);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to display string, ret = %d", ret));
        }
    }

    @Override
    public void displayBitmap(int leftTopX, int leftTopY, int bitmapWidth, int bitmapHeight, byte[] bitmapData) throws NSDKException {
        isSupported();
        if (leftTopX < 0 || leftTopX > 128) {
            throw new NSDKIllegalParameterException("Left-top x shall range from 0 to 128.");
        }
        if (leftTopY < 0 || leftTopY > 35) {
            throw new NSDKIllegalParameterException("Left-top y shall range from 0 to 35.");
        }
        if (bitmapWidth < 0 || bitmapWidth > (128 - leftTopX)) {
            throw new NSDKIllegalParameterException("Bitmap width shall range from 0 to (128 - leftTopX).");
        }
        if (bitmapHeight < 0 || bitmapHeight > (36 - leftTopY)) {
            throw new NSDKIllegalParameterException("Bitmap height shall range from 0 to (36 - leftTopY).");
        }
        if (bitmapData == null || bitmapData.length == 0) {
            throw new NSDKIllegalParameterException("Bitmap data shall not be null.");
        }

        int ret = NSDKJni.getInstance().NDK_ScrDrawBitmapV(leftTopX, leftTopY, bitmapWidth, bitmapHeight, bitmapData);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to display bitmap, ret = %d", ret));
        }
    }

    @Override
    public void clearScreen() throws NSDKException{
        isSupported();
        int ret = NSDKJni.getInstance().NDK_ScrClrs();
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to clear screen, ret = %d", ret));
        }
    }

    private void isSupported() throws NSDKException{
        if (!isSupported) {
            throw new NSDKException(ErrorCode.UNSUPPORTED, "Unsupported GuestDisplayManager module.");
        }
    }
}
