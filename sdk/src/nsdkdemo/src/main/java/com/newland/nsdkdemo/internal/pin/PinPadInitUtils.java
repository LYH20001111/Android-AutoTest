package com.newland.nsdkdemo.internal.pin;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdkdemo.internal.utils.FileUtil;
import com.newland.nsdkdemo.internal.utils.SystemPropertyUtil;

import java.io.File;

public class PinPadInitUtils {
    private View backSpace;
    private View cancel;
    private View enter;
    private Context context;
    private Button[] numBtn = new Button[10];
    private int screenRotation = 0;
    private int screenWidth = 0;
    private  int screenHeight = 0;
    private double mW = 1;
    private double mH = 1;

    public PinPadInitUtils(Context context) {
        this.context = context;
    }

    public void setBackSpace(View backSpace) {
        this.backSpace = backSpace;
    }

    public void setCancel(View cancel) {
        this.cancel = cancel;
    }

    public void setEnter(View enter) {
        this.enter = enter;
    }

    public void setNumBtn(Button[] numBtn) {
        this.numBtn = numBtn;
    }



    public byte[] getFuncLayout() {
        byte[] funcBtnLayout = new byte[36];
        //Delete
        System.arraycopy(new byte[] {0x0A, 0x00, 0x00, 0x00}, 0, funcBtnLayout, 0, 4);
        System.arraycopy(getCoordinates(backSpace), 0, funcBtnLayout, 4, 8);
        //Cancel
        System.arraycopy(new byte[] {0x1B, 0x00, 0x00, 0x00}, 0, funcBtnLayout, 12, 4);
        System.arraycopy(getCoordinates(cancel), 0,funcBtnLayout, 16, 8);
        //Enter
        System.arraycopy(new byte[] {0x0D, 0x00, 0x00, 0x00}, 0, funcBtnLayout, 24, 4);
        System.arraycopy(getCoordinates(enter), 0, funcBtnLayout, 28, 8);

        return funcBtnLayout;
    }

    public byte[] getNumLayout() {
        byte[] numBtnLayout = new byte[80];

//        for(int i = 0; i < numBtn.length; i++) {
//            System.arraycopy(getCoordinates(numBtn[i]), 0 , numBtnLayout, 8 * i, 8);
//        }

        //num0
        System.arraycopy(getCoordinates(numBtn[0]),0,numBtnLayout,0,8);

        //num1
        System.arraycopy(getCoordinates(numBtn[1]),0,numBtnLayout,8,8);

        //num2
        System.arraycopy(getCoordinates(numBtn[2]),0,numBtnLayout,16,8);

        //num3
        System.arraycopy(getCoordinates(numBtn[3]),0,numBtnLayout,24,8);

        //num4
        System.arraycopy(getCoordinates(numBtn[4]),0,numBtnLayout,32,8);

        //num5
        System.arraycopy(getCoordinates(numBtn[5]),0,numBtnLayout,40,8);

        //num6
        System.arraycopy(getCoordinates(numBtn[6]),0,numBtnLayout,48,8);

        //num7
        System.arraycopy(getCoordinates(numBtn[7]),0,numBtnLayout,56,8);

        //num8
        System.arraycopy(getCoordinates(numBtn[8]),0,numBtnLayout,64,8);

        //num9
        System.arraycopy(getCoordinates(numBtn[9]),0,numBtnLayout,72,8);
        return numBtnLayout;
    }

    public void getTouchDm(Display secondDisplay, Context context) {
        final Display display = SecondDisplayManager.getSecondDisplay(context);
        File file = new File("/sys/class/" + (display != null ? "touchscreen_sec" : "touchscreen") + "/resolution");
        String dataInfo = FileUtil.readFileByLines(file);
        if (dataInfo == null) {
            return;
        }
        String[] string = dataInfo.split("x");
        int touchWidth = Integer.valueOf(string[1].trim());
        int touchHeight = Integer.valueOf(string[0].trim());
        LogUtils.d("Test", String.valueOf(touchHeight )+ String.valueOf(touchWidth));
        if ("X800".equals(Build.MODEL)) {
            String prop = SystemPropertyUtil.getProperty("persist.sys.hdmirotation", "unknown");
            if (!"unknown".equals(prop)) {
                try {
                    screenRotation = Integer.parseInt(prop.split("_")[1]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        DisplayMetrics dm = new DisplayMetrics();
        if (display != null) {
            display.getRealMetrics(dm);
        } else {
            WindowManager windowMgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowMgr.getDefaultDisplay().getRealMetrics(dm);
        }
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        mW = (double) touchWidth / screenWidth;
        mH = (double) touchHeight / screenHeight;
    }

    private byte[] getCoordinates(View view) {
        byte[] result = new byte[8];
        int[] l = new int[2];
        view.getLocationOnScreen(l);
        int leftTopX = l[0];
        int leftTopY = l[1];
        int rightBottomX = l[0] + view.getWidth();
        int rightBottomY = l[1] + view.getHeight();

        if(screenRotation == 180)
        {
            int lx = screenWidth - rightBottomX;
            int ly = screenHeight - rightBottomY;
            int rx = screenWidth - leftTopX;
            int ry = screenHeight - leftTopY;
            if (lx < 0) {
                lx = 0;
            }

            if (ly < 0) {
                ly = 0;
            }

            if (rx < 0) {
                rx = 0;
            }
            if (ry < 0) {
                ry = 0;
            }
            leftTopX = (int) (lx * mW);
            leftTopY = (int) (ly * mH);
            rightBottomX = (int) (rx * mW);
            rightBottomY = (int) (ry * mH);
        }
        result[1]=(byte) ((leftTopX >> 8) & 0xff);
        result[0]=(byte) (leftTopX & 0xff);
        result[3]=(byte) ((leftTopY >> 8) & 0xff);
        result[2]=(byte) (leftTopY & 0xff);
        result[5]=(byte) ((rightBottomX >> 8) & 0xff);
        result[4]=(byte) (rightBottomX & 0xff);
        result[7]=(byte) ((rightBottomY >> 8) & 0xff);
        result[6]=(byte) (rightBottomY & 0xff);
        return result;
    }


    public void setRandomLayout(byte[] res, boolean isRandomLayout) {
        byte[] keySeq = new byte[10];
        if(isRandomLayout) {
            System.arraycopy(res, 0, keySeq, 0, res.length);
            int[] numbuttons = new int[keySeq.length];
            for(int i = 0 ; i <keySeq.length; i++) {
                numbuttons[i] = keySeq[i] - 48;
            }
            for(int i = 0; i < 10; i++) {
                numBtn[i].setText(String.valueOf(numbuttons[i]));
                numBtn[i].invalidate();
            }
        }


    }
}
