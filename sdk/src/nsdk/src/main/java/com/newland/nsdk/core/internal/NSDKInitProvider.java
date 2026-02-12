package com.newland.nsdk.core.internal;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.newland.os.NlBuild;
import android.text.TextUtils;
import android.util.Log;

import com.newland.nsdk.BuildConfig;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.common.NSDKSystemAlertActivity;
import com.newland.nsdk.core.internal.system.SystemPropertyUtil;

import java.util.Locale;

public class NSDKInitProvider extends ContentProvider {
    private static final String TAG = "NSDKInitProvider";
    public NSDKInitProvider() {
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();

        String sdkVersion = BuildConfig.VERSION_NAME;
        Log.i(TAG, String.format(Locale.US,"NSDK version: %s", sdkVersion));
        String buildId = BuildConfig.SDK_BUILD_ID;
        Log.i(TAG, String.format(Locale.US,"NSDK build id: %s", buildId));
        String firmwareVersion = NlBuild.VERSION.NL_FIRMWARE;
        Log.i(TAG, String.format(Locale.US,"Firmware version: %s", firmwareVersion));
        // 进行版本检查
        if (needVersionWarning(sdkVersion, firmwareVersion)) {
            NSDKSystemAlertActivity.show(context, "NSDK", sdkVersion);
        }
        return true;
    }

    private boolean needVersionWarning(String sdkVersion, String firmwareVersion) {
        boolean isProDevice = !TextUtils.isEmpty(firmwareVersion) && firmwareVersion.toLowerCase().startsWith("v");
        boolean isTempVersion = TextUtils.isEmpty(sdkVersion) || sdkVersion.toLowerCase().contains("beta");
        String buildType = SystemPropertyUtil.getProperty("ro.build.type", "");
        Log.i(TAG, String.format(Locale.US,"Build type: %s", buildType));

        if (isTempVersion && isProDevice) {
            if (buildType.contains("userdebug") || buildType.contains("eng")) {
                // 这种固件虽然是 V 开头的，但是只是用于内部测试，不会提供给客户，可以不提醒
                return false;
            }
            Log.w(TAG, String.format(Locale.US,"NSDK[%s] is for development use only. Not for production.", sdkVersion));
            return true;
        }

        LogUtils.i(TAG, String.format(Locale.US,"NSDK[%s] is official release, can be used on both PRO and DEV devices.", sdkVersion));
        return false;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        return "";
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return 0;
    }
}