package com.newland.nsdk.core.internal.bootprovider;

import android.content.Context;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.internal.bootprovider.BootProvider;

public class BootProviderImpl implements BootProvider {
    private static final String TAG = "BootProviderImpl";
    private Context mContext;
    private android.newland.BootProvider bootProvider;
    private volatile static BootProviderImpl instance;

    public static BootProviderImpl getInstance(Context mContext) {
        if (instance == null) {
            synchronized (BootProviderImpl.class) {
                if (instance == null || instance.mContext != mContext) {
                    instance = new BootProviderImpl(mContext);
                }
            }
        } else {
            instance = new BootProviderImpl(mContext);
        }
        return instance;
    }
    private BootProviderImpl(Context mContext) {
        this.mContext = mContext;
        bootProvider = new android.newland.BootProvider(mContext);
    }
    @Override
    public void setCustomBootAnimation(String bootAnimation) throws NSDKException {
        boolean isSucc = bootProvider.SetCustomBootAnimation(bootAnimation);
        if (!isSucc) {
            throw new NSDKException("Failed to set custom bootAnimation.");
        }
    }

    @Override
    public void removeCustomBootAnimation() throws NSDKException {
        boolean isSucc = bootProvider.RemoveCustomBootAnimation();
        if (!isSucc) {
            throw new NSDKException("Failed to remove custom bootAnimation.");
        }
    }

    @Override
    public void setCustomBootLogo(String bootLogo) throws NSDKException {
        boolean isSucc = bootProvider.SetCustomBootLogo(bootLogo);
        if (!isSucc) {
            throw new NSDKException("Failed to set custom bootLogo.");
        }
    }

    @Override
    public void removeCustomBootLogo() throws NSDKException {
        boolean isSucc = bootProvider.RemoveCustomBootLogo();
        if (!isSucc) {
            throw new NSDKException("Failed to remove custom bootLogo");
        }
    }
}
