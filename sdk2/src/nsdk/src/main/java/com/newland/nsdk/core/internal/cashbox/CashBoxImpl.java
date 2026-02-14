package com.newland.nsdk.core.internal.cashbox;

import android.annotation.SuppressLint;
import android.content.Context;
import android.newland.NlCashBoxManager;
import android.newland.content.NlContext;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.internal.cashbox.CashBox;

public class CashBoxImpl implements CashBox {
    private static final String TAG = "CashBoxImpl";
    public boolean isSupported;
    private Context mContext;
    private NlCashBoxManager cashBoxManager;

    private volatile static CashBoxImpl instance;
    public static CashBoxImpl getInstance(Context context, boolean isSupported) {
        if (instance == null) {
            synchronized (CashBoxImpl.class) {
                if (instance == null || instance.mContext != context || instance.isSupported != isSupported) {
                    instance = new CashBoxImpl(context, isSupported);
                }
            }
        } else {
            if (instance.mContext != context || instance.isSupported != isSupported) {
                instance = new CashBoxImpl(context, isSupported);
            }
        }
        return instance;
    }

    @SuppressLint("WrongConstant")
    private CashBoxImpl(Context mContext){
        isSupported = true;
        this.mContext = mContext;
        cashBoxManager = (android.newland.NlCashBoxManager)mContext.getSystemService(NlContext.CASHBOX_SERVICE);
    }

    @SuppressLint("WrongConstant")
    private CashBoxImpl(Context mContext, boolean isSupported){
        this.isSupported = isSupported;
        this.mContext = mContext;
        if(isSupported){
            cashBoxManager = (android.newland.NlCashBoxManager)mContext.getSystemService(NlContext.CASHBOX_SERVICE);
        } else {
            cashBoxManager = null;
        }
    }

    private void isSupported() throws NSDKException {
        if(!isSupported){
            throw new NSDKException(ErrorCode.UNSUPPORTED, "UnSupported CashBox Module");
        }
    }

    @Override
    public void open() throws NSDKException {
        isSupported();

        int result = cashBoxManager.OpenCashBox();
        LogUtils.d(TAG, "Open cash box result: " + result);
        if (result == 1) {
            // success
            return;
        }

        if(result == -2){
            throw new NSDKIllegalParameterException("Open Cash Box Parameter Error");
        } else {
            throw new NSDKException(ErrorCode.ERROR, "Open Cash Box Error", result);
        }
    }

    @Override
    public void open(int voltage) throws NSDKException {
        isSupported();

        int result = cashBoxManager.OpenCashBox(voltage);
        LogUtils.d(TAG, "Open cash box result: " + result);
        if (result == 1) {
            // success
            return;
        }

        if(result == -2){
            throw new NSDKIllegalParameterException("Open Cash Box Parameter Error");
        } else {
            throw new NSDKException(ErrorCode.ERROR, "Open Cash Box Error", result);
        }
    }

    @Override
    public void open(int voltage, long time) throws NSDKException {
        isSupported();

        int result = cashBoxManager.OpenCashBox(voltage, time);
        LogUtils.d(TAG, "Open cash box result: " + result);
        if (result == 1) {
            // success
            return;
        }

        if(result == -2){
            throw new NSDKIllegalParameterException("Open Cash Box Parameter Error");
        } else {
            throw new NSDKException(ErrorCode.ERROR, "Open Cash Box Error", result);
        }
    }
}
