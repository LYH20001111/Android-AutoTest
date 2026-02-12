package com.newland.nsdkdemo.external.fragment;

import android.content.Context;
import android.content.res.AssetManager;
import android.view.View;
import android.widget.Switch;

import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.external.display.AlignType;
import com.newland.nsdk.core.api.external.display.ButtonCode;
import com.newland.nsdk.core.api.external.display.ButtonParameters;
import com.newland.nsdk.core.api.external.display.DiaplayQRImageParameters;
import com.newland.nsdk.core.api.external.display.DisplayColorImageParameters;
import com.newland.nsdk.core.api.external.display.DisplayConfiguration;
import com.newland.nsdk.core.api.external.display.DisplayListener;
import com.newland.nsdk.core.api.external.display.DisplayTextParameters;
import com.newland.nsdk.core.api.external.display.ExtDisplay;
import com.newland.nsdk.core.api.external.display.FontSize;
import com.newland.nsdk.core.api.external.display.PictureParameters;
import com.newland.nsdk.core.api.external.display.PictureType;
import com.newland.nsdk.core.api.external.display.SelectionCallback;
import com.newland.nsdk.core.api.external.display.TitleParameters;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.annotation.MethodGridEntity;
import com.newland.nsdkdemo.common.utils.DialogUtils;
import com.newland.nsdkdemo.common.utils.FileUtils;
import com.newland.nsdkdemo.common.utils.MessageTag;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Objects;

public class ExtDisplayFragment extends ExtBaseFragment {

    ExtDisplay mExtDisplay;

    private static final int INDEX_DISPLAY_TEXT = 1;
    private static final int INDEX_DISPLAY_CNTEXT = 2;
    private static final int INDEX_LOAD_IMAGE = 2;

    private static final int INDEX_DISPLAY_IMAGE1 = 3;
    private static final int INDEX_DISPLAY_IMAGE2 = 4;
    private static final int INDEX_LOAD_COLOR_IMAGE = 5;
    private static final int INDEX_DISPLAY_LOADED_PICTURE = 6;
    private static final int INDEX_DISPLAY_PICTURE = 7;

    private static final int INDEX_DISPLAY_QR = 8;
    private static final int INDEX_DISPLAY_SET_AUTO_CLEAN = 9;
    private static final int INDEX_DISPLAY_CANCEL_AUTO_CLEAN = 10;
    private static final int INDEX_DISPLAY_CLEAN = 11;

    private static final int INDEX_BACK_HOME = 12;
    private static final int INDEX_MENU_OPTION = 13;
    private static final int INDEX_DISPLAY_VIEW = 14;
    private static final int INDEX_SET_RETURN_HOME = 15;
    private static final int INDEX_SET_UI_MODE = 16;
    private static final int INDEX_VERSION_DISPLAY = 17;
    private static final int INDEX_GET_BUTTON_OPTION = 18;
    private final Object imageLock = new Object();

    public ExtDisplayFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_extdisplay_f);
    }

    @Override
    public void initData() {
        mExtDisplay = (ExtDisplay) moduleManager.getModule(ModuleType.EXT_DISPLAY);

    }

    @Override
    public Object getModule() {
        return ExtDisplayFragment.this;
    }


    @MethodGridEntity(btnnameid = R.string.tv_display_text, functionid = INDEX_DISPLAY_TEXT)
    private void displayText() {
        try {
            String[] messages = new String[4];
            messages[0] = "line1";
            messages[2] = "line3";

            DisplayTextParameters textParameter = new DisplayTextParameters();
            textParameter.setAlignType(AlignType.CENTER);
            textParameter.setFontColor(0000);
            textParameter.setFontSize(FontSize.NORMAL);

            mExtDisplay.displayText(messages, textParameter);
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, context.getString(R.string.tv_display_text));
        }
    }

//    @MethodGridEntity(btnnameid = R.string.tv_display_cntext, functionid = INDEX_DISPLAY_CNTEXT)
//    private void displayCNText() {
//        try {
//            DisplayCNTextParameters cnTextParameter = new DisplayCNTextParameters();
//
//            cnTextParameter.setXCoordinate(1);
//            cnTextParameter.setYCoordinate(1);
//            cnTextParameter.setTimeout(10000);
//            cnTextParameter.setFontColor(0);
//
//            mExtDisplay.displayCNText(CNTextNotifyType.TRANSACTION_SUCCESS, cnTextParameter);
//        } catch (NSDKException e) {
//            e.printStackTrace();
//        }
//    }

    @MethodGridEntity(btnnameid = R.string.tv_display_loadimage, functionid = INDEX_LOAD_IMAGE)
    private void loadedImage() {
        try {
            byte[] imageData = {0x42, 0x4D, (byte) 0x96, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x3E, 0x00, 0x00, 0x00, 0x28, 0x00, 0x00, 0x00, 0x16, 0x00,
                    0x00, 0x00, 0x16, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x58, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xFF,
                    (byte) 0xFF, (byte) 0xFF, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, (byte) 0xFF,
                    (byte) 0x80, 0x00, 0x01, 0x00, (byte) 0x80, 0x00, 0x01, 0x00, 0x40, 0x00,
                    0x02, 0x00, 0x40, 0x00, 0x02, 0x00, 0x40, 0x00, 0x04, 0x00, 0x40, 0x00, 0x04,
                    0x00, 0x00, 0x00, 0x08, 0x00, 0x00, 0x00, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x08, 0x00, 0x00, 0x00, 0x02, 0x01, (byte) 0xC0, 0x00, 0x02,
                    0x09, 0x00, 0x00, 0x02, 0x6E, 0x00, 0x00, 0x02, 0x70, 0x00, 0x00, 0x02, 0x40,
                    0x00, 0x00, 0x02, 0x40, 0x00, 0x00, 0x02, 0x40, 0x00, 0x00, 0x02, 0x40, 0x00,
                    0x00, 0x02, 0x40, 0x00, 0x00, 0x02, 0x40, 0x00, 0x00, 0x01, (byte) 0x80, 0x00, 0x00};
            mExtDisplay.loadImage((byte) 1, imageData, displayImageListener);
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.tv_display_loadimage));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_display_image1, functionid = INDEX_DISPLAY_IMAGE1)
    private void displayLoadedImage() {
        try {
            mExtDisplay.displayImage((byte) 1, 20, 20);
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.tv_display_image1));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_display_image2, functionid = INDEX_DISPLAY_IMAGE2)
    private void displayImage() {
        try {
            mExtDisplay.clearScreen();
            byte[] imageData = {0x42, 0x4D, (byte) 0x96, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x3E, 0x00, 0x00, 0x00, 0x28, 0x00, 0x00, 0x00, 0x16, 0x00,
                    0x00, 0x00, 0x16, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x58, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xFF,
                    (byte) 0xFF, (byte) 0xFF, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, (byte) 0xFF,
                    (byte) 0x80, 0x00, 0x01, 0x00, (byte) 0x80, 0x00, 0x01, 0x00, 0x40, 0x00,
                    0x02, 0x00, 0x40, 0x00, 0x02, 0x00, 0x40, 0x00, 0x04, 0x00, 0x40, 0x00, 0x04,
                    0x00, 0x00, 0x00, 0x08, 0x00, 0x00, 0x00, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x08, 0x00, 0x00, 0x00, 0x02, 0x01, (byte) 0xC0, 0x00, 0x02,
                    0x09, 0x00, 0x00, 0x02, 0x6E, 0x00, 0x00, 0x02, 0x70, 0x00, 0x00, 0x02, 0x40,
                    0x00, 0x00, 0x02, 0x40, 0x00, 0x00, 0x02, 0x40, 0x00, 0x00, 0x02, 0x40, 0x00,
                    0x00, 0x02, 0x40, 0x00, 0x00, 0x02, 0x40, 0x00, 0x00, 0x01, (byte) 0x80, 0x00, 0x00};
            mExtDisplay.displayImage(imageData, 1, 1, displayImageListener);
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.tv_display_image2));
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_display_loadcolorimage, functionid = INDEX_LOAD_COLOR_IMAGE)
    private void loadColorImage() {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream in = assetManager.open("pic/newland_320x240.bmp");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] tempbytes = new byte[in.available()];
            for (int i = 0; (i = in.read(tempbytes)) != -1;) {
                baos.write(tempbytes, 0, i);
            }

            mExtDisplay.loadColorImage((byte) 1, baos.toByteArray(), displayImageListener);
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "load color image");
        } catch (IOException e) {
            e.printStackTrace();
            showMessage("Failed to get image data.", MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_display_loaded_picture, functionid = INDEX_DISPLAY_LOADED_PICTURE)
    private void displayLoadedPicture() {
        try {
            DisplayColorImageParameters pictureParameter = new DisplayColorImageParameters();
            pictureParameter.setXCoordinate(0);
            pictureParameter.setYCoordinate(0);
            pictureParameter.setWidth(320);
            pictureParameter.setHeight(240);
            mExtDisplay.displayColorImage((byte)1, 5000, pictureParameter);
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "display loaded picture");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_display_picture, functionid = INDEX_DISPLAY_PICTURE)
    private void displayPicture() {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream in = assetManager.open("pic/newland_320x240.bmp");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] tempbytes = new byte[in.available()];
            for (int i = 0; (i = in.read(tempbytes)) != -1;) {
                baos.write(tempbytes, 0, i);
            }

            DisplayColorImageParameters pictureParameter = new DisplayColorImageParameters();
            pictureParameter.setXCoordinate(0);
            pictureParameter.setYCoordinate(0);
            pictureParameter.setWidth(320);
            pictureParameter.setHeight(240);
            mExtDisplay.displayColorImage(baos.toByteArray(), false, pictureParameter, displayImageListener);
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "display picture");
        } catch (IOException e) {
            e.printStackTrace();
            showMessage("Failed to get picture data");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_display_qr, functionid = INDEX_DISPLAY_QR)
    private void displayQR() {
        try {
            byte[] qrData = "This is QR code".getBytes();
            DiaplayQRImageParameters qrImageParameter = new DiaplayQRImageParameters();
            qrImageParameter.setAutoCenter(false);
            qrImageParameter.setVersion((byte) 0);
            qrImageParameter.setXCoordinate((byte) 50);
            qrImageParameter.setYCoordinate((byte) 1);
            mExtDisplay.displayQRImage(qrData, qrImageParameter, displayImageListener);
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "display QR code");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_display_autoclean, functionid = INDEX_DISPLAY_SET_AUTO_CLEAN)
    private void setAutoClean() {
        try {
            mExtDisplay.setAutoClearScreen(true);
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, context.getString(R.string.tv_display_autoclean));
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_display_cancelautoclean, functionid = INDEX_DISPLAY_CANCEL_AUTO_CLEAN)
    private void cancelAutoClean() {
        try {
            mExtDisplay.setAutoClearScreen(false);
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, context.getString(R.string.tv_display_cancelautoclean));
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_display_clean, functionid = INDEX_DISPLAY_CLEAN)
    private void cleanScreen() {
        try {
            mExtDisplay.clearScreen();
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, context.getString(R.string.tv_display_clean));
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_display_backhome, functionid = INDEX_BACK_HOME)
    private void backHome() {
        try {
            mExtDisplay.backToHome();
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, context.getString(R.string.tv_display_backhome));
        }
    }

    @MethodGridEntity(btnnameid = R.string.ext_display_menu_option, functionid = INDEX_MENU_OPTION)
    private void displayMenuOption() {
        String[] menus = new String[] {"Menu 1", null, "Menu 3", "Menu Four", "Menu Five", "Menu 6", "Menu 7", "Menu 8", "Menu 9", "Menu 10", "Menu 11", "Menu 12"};
        try {
            mExtDisplay.displayMenu(20, "Menu Title", menus, new SelectionCallback() {
                @Override
                public void onSelected(int menuId) {
                    showMessage("Selected menu:" + menuId);
                }

                @Override
                public void onError(int errorCode, String errorString) {
                    showMessage(String.format(Locale.US, "Failed to display menu option, ret[%d]:%s", errorCode, errorString), MessageTag.ERROR);

                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onTimeout() {

                }
            });
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.ext_display_menu_option));
        }
    }

    @MethodGridEntity(btnnameid = R.string.ext_display_view, functionid = INDEX_DISPLAY_VIEW)
    private void displayView() {
        String[] messages = new String[2];
        messages[0] = "View Display";
        messages[1] = "Line Two";
        DisplayConfiguration configuration = new DisplayConfiguration();
        configuration.setClearScreen(true);
        configuration.setTextAbove(false);
        PictureParameters[] pictureParameters = new PictureParameters[3];
        pictureParameters[0] = new PictureParameters();
        pictureParameters[0].setHeight(97);
        pictureParameters[0].setWidth(73);
        pictureParameters[0].setPictureType(PictureType.BITMAP);
        pictureParameters[0].setId(1);
        pictureParameters[0].setX(1);
        pictureParameters[0].setY(140);

        pictureParameters[1] = new PictureParameters();
        pictureParameters[1].setHeight(50);
        pictureParameters[1].setWidth(60);
        pictureParameters[1].setPictureType(PictureType.BITMAP);
        pictureParameters[1].setId(1);
        pictureParameters[1].setX(107);
        pictureParameters[1].setY(140);

        pictureParameters[2] = new PictureParameters();
        pictureParameters[2].setHeight(97);
        pictureParameters[2].setWidth(73);
        pictureParameters[2].setPictureType(PictureType.BITMAP);
        pictureParameters[2].setId(1);
        pictureParameters[2].setX(213);
        pictureParameters[2].setY(140);
        try {
            mExtDisplay.displayView(configuration, messages, pictureParameters);
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.ext_display_view));
        }
    }

    @MethodGridEntity(btnnameid = R.string.ext_display_set_return_home, functionid = INDEX_SET_RETURN_HOME)
    private void setReturnHome() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.ext_display_set_return_home), null, R.layout.dialog_ext_display_return_home, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View view) {
                Switch swClearScreen = view.findViewById(R.id.sw_isClearScreen);
                boolean isClearScreen = swClearScreen.isChecked();
                Switch swEnableCancelKey = view.findViewById(R.id.sw_enableCancelKey);
                boolean enableCancelKey = swEnableCancelKey.isChecked();
                try {
                    mExtDisplay.setReturnToHome(isClearScreen, enableCancelKey);
                    showMessage("Set return home configuration success.");
                } catch (NSDKException e) {
                    showErrorMessage(e, context.getString(R.string.ext_display_set_return_home));
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.ext_display_set_ui_mode, functionid = INDEX_SET_UI_MODE)
    private void setUIMode() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.ext_display_set_ui_mode), null, R.layout.dialog_ext_display_set_ui_mode, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View view) {
                Switch swIsDefaultUIMode = view.findViewById(R.id.sw_isDefaultUIMode);
                byte mode = 0x00;
                if (!swIsDefaultUIMode.isChecked()) {
                    mode |= 0x80;
                }
                try {
                    mExtDisplay.setUIMode(mode);
                    showMessage("Set UI Mode success");
                } catch (NSDKException e) {
                    showErrorMessage(e, context.getString(R.string.ext_display_set_ui_mode));
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.ext_display_version, functionid = INDEX_VERSION_DISPLAY)
    private void displayVersion() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.ext_display_view), null, R.layout.dialog_ext_display_version, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View view) {
                Switch swVersionDisplay = view.findViewById(R.id.swDisplayVersion);
                try {
                    mExtDisplay.displayVersion(swVersionDisplay.isChecked());
                    showMessage("Set version display mode success.");
                } catch (NSDKException e) {
                    showErrorMessage(e, context.getString(R.string.ext_display_view));
                }
            }
        });

    }
    @MethodGridEntity(btnnameid = R.string.ext_display_get_button_option, functionid = INDEX_GET_BUTTON_OPTION)
    private void displayButtons() {
        byte[] confirmData = Objects.requireNonNull(FileUtils.readPicDataFromAssets(context, "pic/confirm.png"));
        byte[] backspaceData = Objects.requireNonNull(FileUtils.readPicDataFromAssets(context, "pic/backspace.png"));
        byte[] cancelData = Objects.requireNonNull(FileUtils.readPicDataFromAssets(context, "pic/cancel.png"));

        try {
            mExtDisplay.loadColorImage((byte) 2, confirmData, displayImageListener);
            lockImageProcess();
            mExtDisplay.loadColorImage((byte) 3, backspaceData, displayImageListener);
            lockImageProcess();
            mExtDisplay.loadColorImage((byte) 4, cancelData, displayImageListener);
            lockImageProcess();
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }

        TitleParameters titleParameters = new TitleParameters();
        titleParameters.setX(100);
        titleParameters.setY(50);
        titleParameters.setText("Text");
        titleParameters.setTitleText("Display Buttons Test");
        ButtonParameters[] buttonParameters = new ButtonParameters[3];

        buttonParameters[0] = new ButtonParameters();
        buttonParameters[0].setHeight(97);
        buttonParameters[0].setWidth(73);
        buttonParameters[0].setId(4);
        buttonParameters[0].setX(1);
        buttonParameters[0].setY(140);
        buttonParameters[0].setButtonCode(ButtonCode.CANCEL);

        buttonParameters[1] = new ButtonParameters();
        buttonParameters[1].setHeight(50);
        buttonParameters[1].setWidth(60);
        buttonParameters[1].setId(3);
        buttonParameters[1].setX(107);
        buttonParameters[1].setY(140);
        buttonParameters[1].setButtonCode(ButtonCode.BACKSPACE);

        buttonParameters[2] = new ButtonParameters();
        buttonParameters[2].setHeight(97);
        buttonParameters[2].setWidth(73);
        buttonParameters[2].setId(2);
        buttonParameters[2].setX(213);
        buttonParameters[2].setY(140);
        buttonParameters[2].setButtonCode(ButtonCode.ENTER);
        try {
            mExtDisplay.displayButtons(titleParameters, buttonParameters, 20, true, new SelectionCallback() {
                @Override
                public void onSelected(int buttonCode) {
                    showMessage("Selected button code:" + buttonCode);
                }

                @Override
                public void onError(int errorCode, String errorMessage) {
                    showMessage(String.format(Locale.US, "Failed to display buttons[%d]:%s", errorCode, errorMessage), MessageTag.ERROR);
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onTimeout() {

                }
            });
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.ext_display_get_button_option));
        }
    }

    private DisplayListener displayImageListener = new DisplayListener() {
        @Override
        public void onError(int i, String s) {
            showMessage(String.format("[%d] %s", i, s), MessageTag.ERROR);
            notifyImageLock();
        }

        @Override
        public void onSuccess() {
            showMessage(context.getString(R.string.tv_display_success) + "\r\n", MessageTag.NORMAL);
            notifyImageLock();
        }
    };

    private void lockImageProcess() {
        synchronized (imageLock) {
                try {
                    imageLock.wait();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }

    }
    private void notifyImageLock() {
        synchronized (imageLock) {
                try {
                    imageLock.notify();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

}
