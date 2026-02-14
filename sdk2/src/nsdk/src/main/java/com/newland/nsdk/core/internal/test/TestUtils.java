package com.newland.nsdk.core.internal.test;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.internal.exception.NSDKNDKException;
import com.newland.nsdk.core.internal.jni.NSDKJni;

import java.util.Locale;

public class TestUtils {

    private static final String TAG = "TestUtils";

    private volatile static TestUtils instance;

    public static TestUtils getInstance() {
        if (instance == null) {
            synchronized (TestUtils.class) {
                if (instance == null) {
                    instance = new TestUtils();
                }
            }
        } else {
            instance = new TestUtils();
        }
        return instance;
    }

    private TestUtils(){}

    public void clearSymmetricKeys() throws NSDKException {
        int ret = NSDKJni.getInstance().NAPI_SecSymmKeyErase();

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US,"Failed to clear symmetric keys, result code = %d", ret));
        }
    }
}