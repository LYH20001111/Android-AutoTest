package com.newland.sdk.module.externalPin;

/**
 * @Description
 * @Author Denise
 * @Date 2024/04/15 10:25
 */
public interface UpdateListener {

    void onError(int errorCode, String message);

    void onFileTransferProgress(int precent);
    void onComplete();

}
