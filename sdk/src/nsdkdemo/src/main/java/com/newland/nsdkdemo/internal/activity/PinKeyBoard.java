package com.newland.nsdkdemo.internal.activity;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import android.view.View;
import android.view.WindowManager;

import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.internal.pinentry.PINPadButton;
import com.newland.nsdkdemo.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Random password Keyboard
 *
 */
public class PinKeyBoard extends View {
    private float width, height;
    private Paint paint;
    private int[] nums;
    private int contentsize;
    private Path path;
    // For function key randomization
    private byte[] rf = new byte[]{0x1B, 0x0A, 0x0D};
    private DisplayMetrics dm;
    // digital background color
    // dividing line
    // font color of the function key
    // number
    // cancel background
    // backspace background
    // determination background
    private int[] colors = new int[]{0xfff5f5f9, 0xffe1e1e1, 0xffffffff, 0xff000000, 0xfff24c4d, 0xfff3e250, 0xff70d145};

    public PinKeyBoard(Context context) {
        super(context);
        getScreenResolution(context);
        init();
    }

    public PinKeyBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        getScreenResolution(context);
        init();
    }

    private void getScreenResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        LogUtils.i("N900PinKeyBoard----1", "height=" + dm.heightPixels + ";width" + dm.widthPixels);
    }

    private void init() {
        path = new Path();
        paint = new Paint();
        paint.setAntiAlias(true);
        nums = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(colors[0]);


        if (rf[0] == 0x1B)
            // red
            paint.setColor(colors[4]);
        else if (rf[0] == 0x0A)
            // yellow
            paint.setColor(colors[5]);
        else if (rf[0] == 0x0D)
            // green
            paint.setColor(colors[6]);
        canvas.drawRect(width / 4 * 3 + 1, 0, width, height/4, paint);


        if (rf[1] == 0x1B)
            // red
            paint.setColor(colors[4]);
        else if (rf[1] == 0x0A)
            // yellow
            paint.setColor(colors[5]);
        else if (rf[1] == 0x0D)
            // green
            paint.setColor(colors[6]);
        canvas.drawRect(width / 4 * 3 + 1, height / 4 + 1, width, height / 2, paint);


        if (rf[2] == 0x1B)
            // red
            paint.setColor(colors[4]);
        else if (rf[2] == 0x0A)
            // yellow
            paint.setColor(colors[5]);
        else if (rf[2] == 0x0D)
            // green
            paint.setColor(colors[6]);
        canvas.drawRect(width / 4 * 3 + 1, height / 2 + 1, width, height, paint);

        paint.setColor(colors[1]);
        paint.setStrokeWidth(1f);
        canvas.drawLine(0, height / 4, width / 4 * 3, height / 4, paint);
        canvas.drawLine(0, height / 2, width / 4 * 3, height / 2, paint);
        canvas.drawLine(0, height / 4 * 3, width / 4 * 3, height / 4 * 3, paint);
        canvas.drawLine(width / 4, 0, width / 4, height, paint);
        canvas.drawLine(width / 2, 0, width / 2, height, paint);
        canvas.drawLine(width / 4 * 3, 0, width / 4 * 3, height, paint);

        paint.setColor(colors[2]);
        paint.setTextSize(contentsize);
        for (int i = 0; i < rf.length; i++) {
            float cx = 0, cy = 0;
            if (i == 0) {
                cx = width / 8 * 7;
                cy = height / 8;
            } else if (i == 1) {
                cx = width / 8 * 7;
                cy = height / 8 * 3;
            } else if (2 == i) {
                cx = width / 8 * 7;
                cy = height / 4 * 3;
            }
            if (rf[i] == 0x1B) {
                drawStringCenter(canvas, cx, cy, getResources().getString(R.string.keyboard_cancel));
            } else if (rf[i] == 0x0A) {
                drawStringCenter(canvas, cx, cy, getResources().getString(R.string.keyboard_backspace));
            } else if (rf[i] == 0x0D) {
                drawStringCenter(canvas, cx, cy, getResources().getString(R.string.keyboard_confirm));
            }
        }

        paint.setColor(colors[3]);
        paint.setTextSize(contentsize + 10);
        drawStringCenter(canvas, width / 8, height / 8, nums[1] + "");
        drawStringCenter(canvas, width / 8 * 3, height / 8, nums[2] + "");
        drawStringCenter(canvas, width / 8 * 5, height / 8, nums[3] + "");

        drawStringCenter(canvas, width / 8, height / 8 * 3, nums[4] + "");
        drawStringCenter(canvas, width / 8 * 3, height / 8 * 3, nums[5] + "");
        drawStringCenter(canvas, width / 8 * 5, height / 8 * 3, nums[6] + "");

        drawStringCenter(canvas, width / 8, height / 8 * 5, nums[7] + "");
        drawStringCenter(canvas, width / 8 * 3, height / 8 * 5, nums[8] + "");
        drawStringCenter(canvas, width / 8 * 5, height / 8 * 5, nums[9] + "");

        drawStringCenter(canvas, width / 8, height / 8 * 7, "*");
        drawStringCenter(canvas, width / 8 * 3, height / 8 * 7, nums[0] + "");
        drawStringCenter(canvas, width / 8 * 5, height / 8 * 7, "#");
    }

    private void drawStringCenter(Canvas canvas, float centerpointX,
                                  float centerpointY, String s) {
        paint.setStyle(Style.FILL);
        paint.setStrokeWidth(4);
        FontMetrics fmtemp = paint.getFontMetrics();
        int ctwidth = (int) paint.measureText(s);
        int ctheight = (int) Math.ceil(fmtemp.descent - fmtemp.ascent);
        int ctdescent = (int) fmtemp.descent;
        canvas.drawText(s, centerpointX - ctwidth / 2, centerpointY - ctdescent
                + ctheight / 2, paint);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = width / 5 * 4;
        for (int size = 15; size < 100; size++) {
            paint.setTextSize(size);
            FontMetrics fm = paint.getFontMetrics();
            float cs = fm.descent - fm.ascent;
            float tempw = paint.measureText(getResources().getString(R.string.keyboard_confirm));
            if (cs > height / 8 || tempw > width / 8) {
                contentsize = size;
                break;
            }
        }
        this.setMeasuredDimension((int) width, (int) height);
    }

    public void onPINPadInited(byte[] initResult) {
        byte[] keyCodes = getKeyCodes(initResult);

        byte[] numsbyte = new byte[]{keyCodes[0], keyCodes[1], keyCodes[2], keyCodes[4], keyCodes[5],
                keyCodes[6], keyCodes[8], keyCodes[9], keyCodes[10], keyCodes[12]};
        int[] nums = new int[numsbyte.length];
        for (int i = 0; i < numsbyte.length; i++) {
            nums[i] = numsbyte[i] - 48;
        }
        rf[0] = keyCodes[3];
        rf[1] = keyCodes[7];
        rf[2] = keyCodes[14];
        setRandomNumber(nums);
    }

    public void setAreaCoordination(byte[] initResult) {
        for (int i = 0; i < initResult.length; i++) {
            nums[i] = initResult[i] - 48;
        }
        setRandomNumber(nums);
    }

    private byte[] getKeyCodes(byte[] outSeq) {
        byte[] keySeq = new byte[15];
        byte[] fiveKey = new byte[]{0x1b, 0x0a, 0x0d, 0x2e, 0x1c};
        int n2 = 0, n1 = 0;
        for (int i = 0; i < 15; i++) {
            if (i == 11 || i == 13) {
                if (i == 11) {
                    keySeq[i] = 0x2e;
                }
                if (i == 13) {
                    keySeq[i] = 0x1c;
                }
            } else if (i == 3 || i == 7 || i == 14) {
                keySeq[i] = fiveKey[n1++];
            } else {
                keySeq[i] = outSeq[n2++];
            }
        }
        return keySeq;
    }

    // Set the corresponding key position after random
    public void setRandomNumber(int[] nums) {
        this.nums = nums;
        invalidate();
    }

    public int[] getAreaCoordination() {
        int[] l = new int[2];
        getLocationOnScreen(l);
        int[] areaCoordination = new int[4];
        areaCoordination[0] = 0;
        areaCoordination[1] = 0;
        areaCoordination[2] = (int) width;
        areaCoordination[3] = (int) (l[1] + height);
        return areaCoordination;
    }

    private int[] coordinateInt;

    // Gets the random keyboard key coordinates
    public void getCoordinates(byte[] numbtn, byte[] funbtn, boolean isRandomLayout) {
        int[] l = new int[2];
        getLocationOnScreen(l);
        int x0 = l[0], x1 = (int) (l[0] + width / 4), x2 = (int) (l[0] + width / 2), x3 = (int) (l[0] + width / 4 * 3), x4 = (int) (l[0] + width);
        int y0 = l[1], y1 = (int) (l[1] + height / 4), y2 = (int) (l[1] + height / 2), y3 = (int) (l[1] + height / 4 * 3), y4 = (int) (l[1] + height);
        if (l[1] != 0)
            coordinateInt = new int[]{x0, y0, x1, y1, x1, y0, x2, y1, x2, y0, x3, y1,
                    x3, y0, x4, y1,//Cancel
                    x0, y1, x1, y2, x1, y1, x2, y2, x2, y1, x3, y2,
                    x3, y1, x4, y2, //Delete
                    x0, y2, x1, y3, x1, y2, x2, y3, x2, y2, x3, y3,
                    x0, y0, x0, y0, // null
                    x1, y3, x2, y4, // 0
                    x4, y4, x4, y4, // null
                    x3, y2, x4, y4}; //confirm
        // Initial set of coordinates
        byte[] initCoordinate = new byte[coordinateInt.length * 2];
        for (int i = 0, j = 0; i < coordinateInt.length; i++, j++) {
            initCoordinate[j] = (byte) (coordinateInt[i] & 0xff);
            j++;
            initCoordinate[j] = (byte) ((coordinateInt[i] >> 8) & 0xff);
        }

        byte[] keySeq = ISOUtils.hex2byte("3132331b3435360a3738392e301c0d");

        setKeyCoordinate(initCoordinate, keySeq, numbtn, funbtn, isRandomLayout);
    }

    private void setKeyCoordinate(byte[] initCoordinate, byte[] keySeq, byte[] numBtn, byte[] funKey, boolean isRandomLayout) {
        int n2 = 0;
        int offset = 0;
        int funOffset = 0, numBtnOffset = 0;
        byte[] x1 = new byte[2], y1 = new byte[2], x2 = new byte[2], y2 = new byte[2];
        for (int i = 0; i < keySeq.length; i++) {
            System.arraycopy(initCoordinate, offset, x1, 0, 2);
            offset += 2;
            System.arraycopy(initCoordinate, offset, y1, 0, 2);
            offset += 2;
            System.arraycopy(initCoordinate, offset, x2, 0, 2);
            offset += 2;
            System.arraycopy(initCoordinate, offset, y2, 0, 2);
            offset += 2;

            if (keySeq[n2] >= '0' && keySeq[n2] <= '9') {
                numBtnOffset = (keySeq[n2] - '0') * 8;
                System.arraycopy(x1, 0, numBtn, numBtnOffset, 2);
                numBtnOffset += 2;
                System.arraycopy(y1, 0, numBtn, numBtnOffset, 2);
                numBtnOffset += 2;
                System.arraycopy(x2, 0, numBtn, numBtnOffset, 2);
                numBtnOffset += 2;
                System.arraycopy(y2, 0, numBtn, numBtnOffset, 2);
                numBtnOffset += 2;
            }

            if (keySeq[n2] == 0x1B || keySeq[n2] == 0x0A || keySeq[n2] == 0x0D || keySeq[n2] == 0x9b || keySeq[n2] == 0x9c) {
                byte[] temp = new byte[4];
                System.arraycopy(keySeq, n2, temp, 0, 1);
                System.arraycopy(temp, 0, funKey, funOffset, 4);
                funOffset += 4;
                System.arraycopy(x1, 0, funKey, funOffset, 2);
                funOffset += 2;
                System.arraycopy(y1, 0, funKey, funOffset, 2);
                funOffset += 2;
                System.arraycopy(x2, 0, funKey, funOffset, 2);
                funOffset += 2;
                System.arraycopy(y2, 0, funKey, funOffset, 2);
                funOffset += 2;
            }
            n2++;
        }
    }

    private byte[] endianSwab16(byte[] data) {
        try {
            byte[] temp = new byte[2];
            temp[0] = data[1];
            temp[1] = data[0];
            return temp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public Map<PINPadButton, int[]> getPINPadButtons(){
        int[] l = new int[2];
        getLocationOnScreen(l);
        int x0 = l[0], x1 = (int) (l[0] + width / 4), x2 = (int) (l[0] + width / 2), x3 = (int) (l[0] + width / 4 * 3), x4 = (int) (l[0] + width);
        int y0 = l[1], y1 = (int) (l[1] + height / 4), y2 = (int) (l[1] + height / 2), y3 = (int) (l[1] + height / 4 * 3), y4 = (int) (l[1] + height);

        Map<PINPadButton, int[]> buttonMap = new HashMap<>();
        buttonMap.put(PINPadButton.NUMBER_0, new int[]{x1, y3, x2, y4});
        buttonMap.put(PINPadButton.NUMBER_1, new int[]{x0, y0, x1, y1});
        buttonMap.put(PINPadButton.NUMBER_2, new int[]{x1, y0, x2, y1});
        buttonMap.put(PINPadButton.NUMBER_3, new int[]{x2, y0, x3, y1});
        buttonMap.put(PINPadButton.NUMBER_4, new int[]{x0, y1, x1, y2});
        buttonMap.put(PINPadButton.NUMBER_5, new int[]{x1, y1, x2, y2});
        buttonMap.put(PINPadButton.NUMBER_6, new int[]{x2, y1, x3, y2});
        buttonMap.put(PINPadButton.NUMBER_7, new int[]{x0, y2, x1, y3});
        buttonMap.put(PINPadButton.NUMBER_8, new int[]{x1, y2, x2, y3});
        buttonMap.put(PINPadButton.NUMBER_9, new int[]{x2, y2, x3, y3});

        buttonMap.put(PINPadButton.CANCEL, new int[]{x3, y0, x4, y1});
        buttonMap.put(PINPadButton.BACKSPACE, new int[]{x3, y1, x4, y2});
        buttonMap.put(PINPadButton.ENTER, new int[]{x3, y2, x4, y4});

        return buttonMap;
    }

    public Map<PINPadButton, int[]> getPINPadButtonsWithPINEntry2(){
        int[] l = new int[2];
        getLocationOnScreen(l);
        int x0 = l[0], x1 = (int) (l[0] + width / 4), x2 = (int) (l[0] + width / 2), x3 = (int) (l[0] + width / 4 * 3), x4 = (int) (l[0] + width);
        int y0 = l[1], y1 = (int) (l[1] + height / 4), y2 = (int) (l[1] + height / 2), y3 = (int) (l[1] + height / 4 * 3), y4 = (int) (l[1] + height);

        Map<PINPadButton, int[]> buttonMap = new HashMap<>();

        buttonMap.put(PINPadButton.NUMBER_1, new int[]{x0, y0, x1, y1});
        buttonMap.put(PINPadButton.NUMBER_2, new int[]{x1, y0, x2, y1});
        buttonMap.put(PINPadButton.NUMBER_3, new int[]{x2, y0, x3, y1});
        buttonMap.put(PINPadButton.CANCEL, new int[]{x3, y0, x4, y1});
        buttonMap.put(PINPadButton.NUMBER_4, new int[]{x0, y1, x1, y2});
        buttonMap.put(PINPadButton.NUMBER_5, new int[]{x1, y1, x2, y2});
        buttonMap.put(PINPadButton.NUMBER_6, new int[]{x2, y1, x3, y2});
        buttonMap.put(PINPadButton.BACKSPACE, new int[]{x3, y1, x4, y2});
        buttonMap.put(PINPadButton.NUMBER_7, new int[]{x0, y2, x1, y3});
        buttonMap.put(PINPadButton.NUMBER_8, new int[]{x1, y2, x2, y3});
        buttonMap.put(PINPadButton.NUMBER_9, new int[]{x2, y2, x3, y3});
        buttonMap.put(PINPadButton.ENTER, new int[]{x3, y2, x4, y4});
        buttonMap.put(PINPadButton.NUMBER_0, new int[]{x1, y3, x2, y4});

        return buttonMap;
    }

    public Map<PINPadButton, int[]> getPINPadButtons2() {
        int[] l = new int[2];
        getLocationOnScreen(l);
        int x0 = l[0], x1 = (int) (l[0] + width / 4), x2 = (int) (l[0] + width / 2), x3 = (int) (l[0] + width / 4 * 3), x4 = (int) (l[0] + width);
        int y0 = l[1], y1 = (int) (l[1] + height / 4), y2 = (int) (l[1] + height / 2), y3 = (int) (l[1] + height / 4 * 3), y4 = (int) (l[1] + height);

        Map<PINPadButton, int[]> buttonMap = new HashMap<>();
        buttonMap.put(PINPadButton.NUMBER_0, new int[]{x1, y3, x2, y4});
        buttonMap.put(PINPadButton.NUMBER_1, new int[]{x0, y0, x1, y1});
        buttonMap.put(PINPadButton.NUMBER_2, new int[]{x1, y0, x2, y1});
        buttonMap.put(PINPadButton.NUMBER_3, new int[]{x2, y0, x3, y1});
        buttonMap.put(PINPadButton.NUMBER_4, new int[]{x0, y1, x1, y2});
        buttonMap.put(PINPadButton.NUMBER_5, new int[]{x1, y1, x2, y2});
        buttonMap.put(PINPadButton.NUMBER_6, new int[]{x2, y1, x3, y2});
        buttonMap.put(PINPadButton.NUMBER_7, new int[]{x0, y2, x1, y3});
        buttonMap.put(PINPadButton.NUMBER_8, new int[]{x1, y2, x2, y3});
        buttonMap.put(PINPadButton.NUMBER_9, new int[]{x2, y2, x3, y3});

        buttonMap.put(PINPadButton.QUIT, new int[]{x3, y0, x4, y1});
        buttonMap.put(PINPadButton.CLEAR, new int[]{x3, y1, x4, y2});
        buttonMap.put(PINPadButton.ENTER, new int[]{x3, y2, x4, y4});

        return buttonMap;
    }

    public int[] getColors() {
        return colors;
    }

    public void setColors(int[] colors) {
        this.colors = colors;
    }

}
