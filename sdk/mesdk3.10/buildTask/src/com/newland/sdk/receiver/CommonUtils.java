package com.newland.sdk.receiver;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CommonUtils {

    private static final String TAG = "MESDKLOG";

    // newland_debug.keystore证书的SHA256
    public static final String SHA256 = "88038237548067993298C7DCB8FEFE79CDD4005B735E2A4373ED10FB2DABAB00";
    public static final String PACKAGE_NAME = "com.newland.openlog";

    public static String getAppSignatures(Context context, String packageName) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            if (packageInfo.signatures != null && packageInfo.signatures.length > 0) {
                return getSignatureHashes(packageInfo.signatures);
            }
        } catch (Exception e) {}
        return null;
    }

    // 处理签名数组并生成哈希值
    private static String getSignatureHashes(Signature[] signatures) {
        StringBuilder sb = new StringBuilder();

        for (Signature signature : signatures) {
            String hash = getSignatureHash(signature);
            if (hash != null) {
                sb.append(hash).append("\n");
            }
        }
        return sb.toString().trim();
    }

    // 计算单个签名的哈希值 (SHA-256)
    private static String getSignatureHash(Signature signature) {
        try {
            byte[] cert = signature.toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] publicKey = md.digest(cert);

            // 转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : publicKey) {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isOpenLog(Context context) {
        try {
            String appSignatures = getAppSignatures(context, PACKAGE_NAME);
            if (CommonUtils.SHA256.equalsIgnoreCase(appSignatures)) {
                Log.d(TAG, "signature match");
                String value = getVersionName(context, CommonUtils.PACKAGE_NAME);
                if (value != null && value.contains("-")) {
                    String[] values = value.split("-");
                    String currentDate = new SimpleDateFormat("yyMMdd", Locale.CHINA).format(new Date());
                    Log.d(TAG, "DATE=" + value + ", currentDate=" + currentDate);
                    if (currentDate.compareTo(values[0]) >= 0 && currentDate.compareTo(values[1]) <= 0) {
                        return true;
                    }
                }
            } else {
                Log.e(TAG, "signature not match");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String getVersionName(Context context, String packageName) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isInstalled(Context context, String pkgName) {
        try {
            if (pkgName == null || TextUtils.isEmpty(pkgName)) {
                return false;
            }
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
            Log.d(TAG, pkgName + " is installed");
            return packageInfo != null;
        } catch (Exception e) {}
        Log.d(TAG, pkgName + " is not installed");
        return false;
    }

    public static String getOtherAppMetaData(Context context, String targetPackageName, String metaDataKey) {
        try {
            // 获取目标应用的 ApplicationInfo（需要 QUERY_ALL_PACKAGES 权限）
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(targetPackageName, PackageManager.GET_META_DATA);
            // 读取 meta-data
            Bundle metaData = appInfo.metaData;
            if (metaData != null) {
                return metaData.getString(metaDataKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
