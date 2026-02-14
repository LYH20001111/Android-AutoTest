package com.newland.nsdk.core.internal.recovery;

import android.content.Context;
import android.newland.os.NlRecovery;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.internal.recovery.Recovery;

public class RecoveryImpl implements Recovery {
    private Context mContext;
    private volatile static RecoveryImpl instance;
    private android.newland.os.NlRecovery recovery;

    public static RecoveryImpl getInstance(Context mContext) {
        if (instance == null) {
            synchronized (RecoveryImpl.class) {
                if (instance == null || instance.mContext != mContext) {
                    instance = new RecoveryImpl(mContext);
                }
            }
        } else {
            if (instance.mContext != mContext) {
                instance = new RecoveryImpl(mContext);
            }
        }
        return instance;
    }

    private RecoveryImpl(Context mContext) {
        this.mContext = mContext;
        recovery = new NlRecovery(mContext);
    }

    @Override
    public void keepApps(String[] pkgNames) throws NSDKException {
        boolean isSucc = recovery.keepApps(pkgNames);
        if (!isSucc) {
            throw new NSDKException("Failed to keep apps.");
        }
    }

    @Override
    public void keepApps(String[] pkgNames, String[] dataPaths) throws NSDKException {
        if (dataPaths != null && dataPaths.length != 0) {
            for (String dataPath : dataPaths) {
                if (dataPath == null) {
                    throw new NSDKIllegalParameterException("Data path shall not be null.");
                }
                if (!dataPath.startsWith("/data/share") && !dataPath.startsWith("/mnt/sdcard/") && !dataPath.startsWith("/mnt/shell/emulated/0/")) {
                    throw new NSDKIllegalParameterException("Existing invalid data path here.");
                }
            }
        }
        boolean isSucc = recovery.keepApps(pkgNames, dataPaths);
        if (!isSucc) {
            throw new NSDKException("Failed to keep apps and data.");
        }
    }
}
