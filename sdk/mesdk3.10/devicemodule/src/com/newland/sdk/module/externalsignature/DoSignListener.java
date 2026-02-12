package com.newland.sdk.module.externalsignature;

public interface DoSignListener {

    void onSuccess(byte[] signData);

    void onCancel();

    void onTimeout();

    void onError(int errorCode, String message);
}
