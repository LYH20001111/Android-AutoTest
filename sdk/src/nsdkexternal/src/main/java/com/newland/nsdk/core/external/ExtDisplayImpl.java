package com.newland.nsdk.core.external;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.external.display.DiaplayQRImageParameters;
import com.newland.nsdk.core.api.external.display.DisplayColorImageParameters;
import com.newland.nsdk.core.api.external.display.DisplayConfiguration;
import com.newland.nsdk.core.api.external.display.DisplayListener;
import com.newland.nsdk.core.api.external.display.DisplayTextParameters;
import com.newland.nsdk.core.api.external.display.ExtDisplay;
import com.newland.nsdk.core.api.external.display.SelectionCallback;
import com.newland.nsdk.core.common.NSDKExecutors;
import com.newland.nsdk.core.api.external.display.ButtonParameters;
import com.newland.nsdk.core.external.command.display.DisplayColorImageMode;
import com.newland.nsdk.core.external.command.display.ExternalDisplayModule;
import com.newland.nsdk.core.api.external.display.PictureParameters;
import com.newland.nsdk.core.api.external.display.TitleParameters;

import java.util.ArrayList;
import java.util.List;

import static com.newland.nsdk.core.external.command.display.ExternalDisplayModule.MAX_IMAGE_DATA_LEN;

import android.text.TextUtils;
import android.util.Log;

public class ExtDisplayImpl implements ExtDisplay {
    private ExternalDisplayModule externalDisplayModule;
    private static final String TAG = "ExtDisplayImpl";

    private volatile static ExtDisplayImpl instance;
    public static ExtDisplayImpl getInstance() {
        if (instance == null) {
            synchronized (ExtDisplayImpl.class) {
                if (instance == null) {
                    instance = new ExtDisplayImpl();
                }
            }
        }
        return instance;
    }

    private ExtDisplayImpl() {
        externalDisplayModule = new ExternalDisplayModule();
    }

    @Override
    public void displayText(String[] messages, DisplayTextParameters displayTextParameter) throws NSDKException {
        if (displayTextParameter == null) {
            throw new NSDKIllegalParameterException("Parameter is null!");
        }

        if (messages == null || messages.length == 0) {
            throw new NSDKIllegalParameterException("Nothing is set to display!");
        }

        try {
            if (displayTextParameter.getFontSize() != null) {
                externalDisplayModule.setFontSize(displayTextParameter.getFontSize());
            }
        } catch (NSDKException e) {
            LogUtils.d(TAG, String.format("Failed to set font size: %d. %s", e.getCode(), e.getMessage()));
        }

        try {
            if (displayTextParameter.getAlignType() != null) {
                externalDisplayModule.setDisplayDirection(displayTextParameter.getAlignType());
            }
        } catch (NSDKException e) {
            LogUtils.d(TAG, String.format("Failed to set align type: %d. %s", e.getCode(), e.getMessage()));
        }

        try {
            if (displayTextParameter.getFontColor() >= 0) {
                externalDisplayModule.setFontColor(displayTextParameter.getFontColor(), (byte) 0);
            }
        } catch (NSDKException e) {
            LogUtils.d(TAG, String.format("Failed to set font color: %d. %s", e.getCode(), e.getMessage()));
        }

        externalDisplayModule.displayText(messages);
    }

    @Override
    public void displayImage(byte imageID, int x, int y) throws NSDKException {
        externalDisplayModule.displayImage(imageID, null, x, y);
    }

    @Override
    public void displayImage(final byte[] imageData, final int x, final int y, final DisplayListener listener) throws NSDKException {
        if (imageData == null) {
            throw new NSDKIllegalParameterException("Image data should not be null!");
        }

        if (listener == null) {
            throw new NSDKIllegalParameterException("Listener should not be null!");
        }

        NSDKExecutors.threadStart(new Runnable() {
            @Override
            public void run() {
                try {
                    externalDisplayModule.displayImage((byte) 0, imageData, x, y);
                    listener.onSuccess();
                } catch (Exception e) {
                    if (e instanceof NSDKException) {
                        listener.onError(((NSDKException) e).getCode(), e.getMessage());
                    } else {
                        listener.onError(ErrorCode.EXT_ERROR, e.getMessage());
                    }
                }
            }
        });
    }

    @Override
    public void loadImage(final byte imageID, final byte[] imageData, final DisplayListener listener) throws NSDKException {
        if (imageData == null) {
            throw new NSDKIllegalParameterException("Image data should not be null!");
        }

        if (listener == null) {
            throw new NSDKIllegalParameterException("Listener should not be null!");
        }

        NSDKExecutors.threadStart(new Runnable() {
            @Override
            public void run() {
                try {
                    externalDisplayModule.loadImage(imageID, imageData);
                    listener.onSuccess();
                } catch (Exception e) {
                    if (e instanceof NSDKException) {
                        listener.onError(((NSDKException) e).getCode(), e.getMessage());
                    } else {
                        listener.onError(ErrorCode.EXT_ERROR, e.getMessage());
                    }
                }
            }
        });
    }

    @Override
    public void loadColorImage(final byte imageID, final byte[] imageData, final DisplayListener listener) throws NSDKException {
        if (imageData == null || imageData.length == 0) {
            throw new NSDKIllegalParameterException("Invalid image data(a buffer that max length shall be 2048).");
        }

        if (listener == null) {
            throw new NSDKIllegalParameterException("Listener should not be null!");
        }

        NSDKExecutors.threadStart(new Runnable() {
            @Override
            public void run() {
                List<byte[]> dataList = splitData(imageData);
                int dataCount = dataList.size();
                int subsequentPackageNumber = dataCount - 1;

                try {
                    for(int currentPackageNumber = 0; currentPackageNumber < dataCount; currentPackageNumber ++) {
                        externalDisplayModule.displayColorImage((byte) DisplayColorImageMode.ONLY_LOAD.ordinal(), 0, 0, 0, 0,
                                imageID,
                                currentPackageNumber,
                                subsequentPackageNumber,
                                dataList.get(currentPackageNumber),
                                0);
                        subsequentPackageNumber --;
                    }
                    listener.onSuccess();
                } catch (Exception e) {
                    if (e instanceof NSDKException) {
                        listener.onError(((NSDKException) e).getCode(), e.getMessage());
                    } else {
                        listener.onError(ErrorCode.EXT_ERROR, e.getMessage());
                    }
                }
            }
        });
    }

    @Override
    public void displayColorImage(final byte[] imageData, final boolean isBackground, final DisplayColorImageParameters displayColorImageParameter, final DisplayListener listener) throws NSDKException {
        if (displayColorImageParameter == null || listener == null) {
            throw new NSDKIllegalParameterException("Display parameters and listener shall not be null.");
        }

        if (imageData == null || imageData.length == 0) {
            throw new NSDKIllegalParameterException("No image data to load and display.");
        }

        if (displayColorImageParameter.getXCoordinate() < 0 || displayColorImageParameter.getXCoordinate() > 0xFFFF) {
            throw new NSDKIllegalParameterException("X coordinate shall be >=0 and <= 65535");
        }

        if (displayColorImageParameter.getYCoordinate() < 0 || displayColorImageParameter.getYCoordinate() > 0xFFFF) {
            throw new NSDKIllegalParameterException("Y coordinate shall be >=0 and <= 65535");
        }

        if (displayColorImageParameter.getWidth() < 0 || displayColorImageParameter.getWidth() > 0xFFFF) {
            throw new NSDKIllegalParameterException("Width shall be >=0 and <= 65535");
        }

        if (displayColorImageParameter.getHeight() < 0 || displayColorImageParameter.getHeight() > 0xFFFF) {
            throw new NSDKIllegalParameterException("Height shall be >=0 and <= 65535");
        }

        NSDKExecutors.threadStart(new Runnable() {
            @Override
            public void run() {
                DisplayColorImageMode mode = isBackground ? DisplayColorImageMode.DISPLAY_AND_SET_BACKGROUND : DisplayColorImageMode.DISPLAY;
                List<byte[]> dataList = splitData(imageData);
                int dataCount = dataList.size();
                int subsequentPackageNumber = dataCount - 1;

                try {
                    for(int currentPackageNumber = 0; currentPackageNumber < dataCount; currentPackageNumber ++) {
                        byte[] singlePackageImageData = dataList.get(currentPackageNumber);
                        externalDisplayModule.displayColorImage((byte) mode.ordinal(),
                                displayColorImageParameter.getXCoordinate(),
                                displayColorImageParameter.getYCoordinate(),
                                displayColorImageParameter.getWidth(),
                                displayColorImageParameter.getHeight(),
                                (byte) 0,
                                currentPackageNumber,
                                subsequentPackageNumber,
                                singlePackageImageData,
                                0);
                        subsequentPackageNumber --;
                    }
                    listener.onSuccess();
                } catch (Exception e) {
                    if (e instanceof NSDKException) {
                        listener.onError(((NSDKException) e).getCode(), e.getMessage());
                    } else {
                        listener.onError(ErrorCode.EXT_ERROR, e.getMessage());
                    }
                }
            }
        });
    }

    @Override
    public void displayColorImage(byte imageID, int timeout, DisplayColorImageParameters displayColorImageParameter) throws NSDKException {
        if (displayColorImageParameter == null) {
            throw new NSDKIllegalParameterException("Display parameters shall not be null.");
        }

        externalDisplayModule.displayColorImage((byte) DisplayColorImageMode.DISPLAY_LOADED_IMAGE.ordinal(),
                displayColorImageParameter.getXCoordinate(),
                displayColorImageParameter.getYCoordinate(),
                displayColorImageParameter.getWidth(),
                displayColorImageParameter.getHeight(),
                imageID,
                0,
                0,
                null,
                timeout);
    }

    @Override
    public void displayQRImage(final byte[] qrContent, final DiaplayQRImageParameters diaplayQRImageParameter, final DisplayListener displayImageListener) throws NSDKException {
        if (diaplayQRImageParameter == null) {
            throw new NSDKIllegalParameterException("Parameter should not be null !");
        }

        if (displayImageListener == null) {
            throw new NSDKIllegalParameterException("Listener should not be null !");
        }

        if (diaplayQRImageParameter.getPosition() == null) {
            throw new NSDKIllegalParameterException("Position should not be null !");
        }

        if (qrContent == null) {
            throw new NSDKIllegalParameterException("QR data should not be null !");
        }

        if (qrContent.length > 512) {
            throw new NSDKIllegalParameterException("QR data should not be more than 512 bytes!");
        }

        if (diaplayQRImageParameter.getTextData() != null && diaplayQRImageParameter.getTextData().length > 256) {
            throw new NSDKIllegalParameterException("Text data should not be more than 256 bytes!");
        }

        NSDKExecutors.threadStart(new Runnable() {
            @Override
            public void run() {
                try {
                    externalDisplayModule.displayQRCode(qrContent, diaplayQRImageParameter);
                    displayImageListener.onSuccess();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (e instanceof NSDKException) {
                        displayImageListener.onError(((NSDKException) e).getCode(), null);
                    } else {
                        displayImageListener.onError(ErrorCode.EXT_ERROR, null);
                    }
                }
            }
        });
    }

    @Override
    public void displayMenu(int timeout, String title, String[] menus, SelectionCallback callback) throws NSDKException {
        if (timeout < 0) {
            throw new NSDKIllegalParameterException("Timeout shall be >=0");
        }
        if (TextUtils.isEmpty(title)) {
            throw new NSDKIllegalParameterException("Title shall not be null");
        }
        if (menus == null || menus.length == 0 || menus.length > 12) {
            throw new NSDKIllegalParameterException("Menu numbers shall range from 1 to 12");
        }
        if (callback == null) {
            throw new NSDKIllegalParameterException("Callback shall not be null");
        }
        externalDisplayModule.displayMenu(timeout, title, menus, callback);
    }

    @Override
    public void displayButtons(TitleParameters titleParameters, ButtonParameters[] buttons, int timeout, boolean isReturnHome, SelectionCallback callback) throws NSDKException {

        if (titleParameters == null) {
            throw new NSDKIllegalParameterException("Title parameters shall not be null");
        }
        if (buttons == null || buttons.length == 0 || buttons.length > 8) {
            throw new NSDKIllegalParameterException("Button numbers shall range from 1 to 8");
        }
        if (timeout < 0) {
            throw new NSDKIllegalParameterException("Timeout shall be >=0.");
        }
        if (callback == null) {
            throw new NSDKIllegalParameterException("SelectionCallback shall not be null.");
        }
        externalDisplayModule.displayButtons(titleParameters, buttons, timeout, isReturnHome, callback);

    }

    @Override
    public void displayView(DisplayConfiguration displayConfiguration, String[] messages, PictureParameters[] pictures) throws NSDKException {
        if (displayConfiguration == null) {
            throw new NSDKIllegalParameterException("Display configuration shall not be null");
        }
        byte config = 0x00;
        if (displayConfiguration.isClearScreen()) {
            config |= 0x02;
        }
        if (displayConfiguration.isTextAbove()) {
            config |= 0x04;
        }
        LogUtils.d(TAG, "config:" + config);
        externalDisplayModule.displayView(config, messages, pictures);
    }

    @Override
    public void setAutoClearScreen(boolean isAuto) throws NSDKException {
        if (isAuto) {
            externalDisplayModule.setDisplayMode((byte) 1);
        } else {
            externalDisplayModule.setDisplayMode((byte) 0);
        }
    }

    @Override
    public void clearScreen() throws NSDKException {
        externalDisplayModule.clearScreen();
    }

    @Override
    public void backToHome() throws NSDKException {
        externalDisplayModule.returnMainMenu();
    }

    @Override
    public void setReturnToHome(boolean isReturnHome, boolean enableCancelKey) throws NSDKException {
        byte config = 0x00;
        if (isReturnHome) {
            config |= 0x01;
        }
        if (enableCancelKey) {
            config |= 0x02;
        }

        externalDisplayModule.setReturnToHome(config);


    }

    @Override
    public void setUIMode(byte mode) throws NSDKException {
        if ((mode & 0x40) == 0x40) {
            throw new NSDKException(ErrorCode.EXT_UNSUPPORTED, "Current only support set UI mode.");
        }
        externalDisplayModule.setUIMode(mode);
    }

    @Override
    public void displayVersion(boolean isDisplay) throws NSDKException {
        if (isDisplay) {
            externalDisplayModule.displayVersion((byte) 0x01);
        } else {
            externalDisplayModule.displayVersion((byte) 0x00);
        }

    }

    private List<byte[]> splitData(byte[] data) {
        List<byte[]> dataList = new ArrayList<>();
        int totalLen = data.length;
        int offset = 0;
        byte[] tempBuf;
        int tempBufLen;
        while (offset < totalLen) {
            if (totalLen - offset >= MAX_IMAGE_DATA_LEN) {
                tempBufLen = MAX_IMAGE_DATA_LEN;
            } else {
                tempBufLen = totalLen - offset;
            }
            tempBuf = new byte[tempBufLen];
            System.arraycopy(data, offset, tempBuf, 0, tempBufLen);
            dataList.add(tempBuf);
            offset += tempBufLen;
        }
        return dataList;
    }
}
