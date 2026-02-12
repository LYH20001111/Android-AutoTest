package com.newland.sdkdemo.showutil;

import android.graphics.Bitmap;

public interface IShowInfo {
    void showMessage(String mess, MessageTag messageType);
    void showMessage(String mess, MessageTag messageType, boolean linefeed);
    void showImage(String imageData);
    void showImage(Bitmap bmp);
    void showImage(int resourceId);
    void cleanMessage();
//    void showResult(MethodBean bean);
    void showError(Exception e);
}
