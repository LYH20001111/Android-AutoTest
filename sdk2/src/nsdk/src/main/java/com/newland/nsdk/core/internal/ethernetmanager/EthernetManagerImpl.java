package com.newland.nsdk.core.internal.ethernetmanager;

import android.content.Context;
import android.newland.net.ethernet.NlEthernetManager;
import android.os.Handler;
import android.os.Looper;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.internal.ethernetmanager.EthernetManager;
import com.newland.nsdk.core.api.internal.ethernetmanager.EthernetStatus;

import java.util.Locale;

public class EthernetManagerImpl implements EthernetManager {
    private boolean isSupported;
    private Context context;
    private NlEthernetManager ethernetManager;

    private volatile static EthernetManagerImpl instance;
    public static EthernetManagerImpl getInstance(Context context, boolean isSupported) throws NSDKException {
        if (instance == null) {
            synchronized (EthernetManagerImpl.class) {
                if (instance == null || instance.context != context || instance.isSupported != isSupported) {
                    instance = new EthernetManagerImpl(context, isSupported);
                }
            }
        } else {
            if (instance.context != context || instance.isSupported != isSupported) {
                instance = new EthernetManagerImpl(context, isSupported);
            }
        }
        if(instance.isSupported && instance.ethernetManager == null){
            throw new NSDKException(ErrorCode.UNSUPPORTED, "NlEthernetManager Init Fail");
        }
        return instance;
    }

    private EthernetManagerImpl(final Context mContext, boolean isSupported){
        this.isSupported = isSupported;
        this.context = mContext;
        if(isSupported){
            if(Looper.myLooper() == Looper.getMainLooper()){
                ethernetManager = new NlEthernetManager(mContext);
            }else{
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        ethernetManager = new NlEthernetManager(mContext);
                    }
                });
                long startTime = System.currentTimeMillis();
                boolean isTimeout = false;
                int timeout = 20000;
                while(ethernetManager == null && !isTimeout){
                    try {
                        Thread.sleep(10);
                        if(timeout - (System.currentTimeMillis() - startTime) <= 0){
                            isTimeout = true;
                        }
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        } else {
            ethernetManager = null;
        }
    }

    private void isSupported() throws NSDKException {
        if(!isSupported){
            throw new NSDKException(ErrorCode.UNSUPPORTED, "UnSupported EthernetManager Module");
        }else if(ethernetManager == null){
            throw new NSDKException(ErrorCode.UNSUPPORTED, "NlEthernetManager Is NULL");
        }
    }

    @Override
    public void enable() throws NSDKException {
        isSupported();

        try {
            ethernetManager.start();
        } catch (Exception e) {
            e.printStackTrace();
            throw new NSDKException(ErrorCode.ERROR, String.format(Locale.US, "Failed to enable ethernet: %s", e.getMessage()), e);
        }
    }

    @Override
    public void disable() throws NSDKException {
        isSupported();

        try {
            ethernetManager.stop();
        } catch (Exception e) {
            e.printStackTrace();
            throw new NSDKException(ErrorCode.ERROR, String.format(Locale.US, "Failed to disable ethernet: %s", e.getMessage()), e);
        }
    }

    @Override
    public EthernetStatus getStatus() throws NSDKException {
        isSupported();

        try {
            boolean enable = ethernetManager.isAvailable();
            if (enable) {
                return EthernetStatus.ENABLED;
            }
            return EthernetStatus.DISABLED;
        } catch (Exception e) {
            e.printStackTrace();
            return EthernetStatus.UNKNOWN;
        }
    }

    @Override
    public String getConfig() throws NSDKException {
        isSupported();

        try {
            return ethernetManager.getConfig();
        } catch (Exception e) {
            e.printStackTrace();
            throw new NSDKException(ErrorCode.ERROR, String.format(Locale.US, "Failed to get ethernet config: %s", e.getMessage()), e);
        }
    }
}
