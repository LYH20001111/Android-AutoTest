package com.newland.nsdkdemo.internal.activity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.newland.nsdk.core.api.internal.pinentry.PINPadButton;

import java.util.HashMap;
import java.util.Map;

public class RNIBPinKeyBoard2 extends View{
    private float height, width;
    private Paint paint;
    private int contentsize;
    private DisplayMetrics dm;
    private int[] colors = new int[]{0xfff5f5f9, 0xffe1e1e1, 0xffffffff, 0xff000000, 0xfff24c4d, 0xfff3e250, 0xff70d145};
    private byte[] rf = new byte[]{0x1B, 0x0A, 0x0D};
    public RNIBPinKeyBoard2(Context context) {
        super(context);
        getScreenResolution(context);
        init();
    }

    public RNIBPinKeyBoard2(Context context, AttributeSet attrs) {
        super(context, attrs);
        getScreenResolution(context);
        init();
    }

    private void getScreenResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        Log.i("N900PinKeyBoard----1", "height=" + dm.heightPixels + ";width" + dm.widthPixels);
    }

    private void init(){
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(colors[3]);

        //Cancel
        setPaintColor(0, paint);
        canvas.drawRect(0, height / 4 * 3 + 1, width / 3 + 1, height, paint);

        //Enter
        setPaintColor(2,paint);
        canvas.drawRect(width / 3 * 2 + 1, height / 4 * 3 + 1, width, height, paint);

        //horizontal separator
        paint.setColor(colors[2]);
        paint.setStrokeWidth(5);
        canvas.drawLine(0, 0, width, 5, paint);
        canvas.drawLine(0, height / 4, width, height / 4, paint);
        canvas.drawLine(0, height /4 * 2, width, height / 4 * 2, paint);
        canvas.drawLine(0, height /4 * 3, width, height / 4 * 3, paint);


        //vertical separator
        canvas.drawLine(0, 0, 5, height, paint);
        canvas.drawLine(width / 3, 0, width / 3, height, paint);
        canvas.drawLine(width / 3 * 2, 0, width / 3 * 2, height, paint);
        canvas.drawLine(width - 5, 0, width, height, paint);

        paint.setColor(colors[3]);
        paint.setTextSize(width / 6);
        drawStringCenter(canvas, width / 6, height / 8 * 7, "X");
        drawStringCenter(canvas, width / 6 * 5, height / 8 * 7, "√");

        paint.setColor(colors[2]);
        paint.setTextSize(contentsize + 40);
        drawStringCenter(canvas, width / 6, height / 8, "1");
        drawStringCenter(canvas, width / 2, height / 8, "2");
        drawStringCenter(canvas, width / 6 * 5, height / 8, "3");
        drawStringCenter(canvas, width / 6, height / 8 * 3, "4");
        drawStringCenter(canvas, width / 2, height / 8 * 3, "5");
        drawStringCenter(canvas, width / 6 * 5, height / 8 * 3, "6");
        drawStringCenter(canvas, width / 6, height / 8 * 5, "7");
        drawStringCenter(canvas, width / 2, height / 8 * 5, "8");
        drawStringCenter(canvas, width / 6 * 5, height / 8 * 5, "9");
        drawStringCenter(canvas, width / 2, height / 8 * 7, "0");

    }

    private void drawStringCenter(Canvas canvas, float centerpointX,
                                  float centerpointY, String s) {
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(4);
        Paint.FontMetrics fmtemp = paint.getFontMetrics();
        int ctwidth = (int) paint.measureText(s);
        int ctheight = (int) Math.ceil(fmtemp.descent - fmtemp.ascent);
        int ctdescent = (int) fmtemp.descent;
        canvas.drawText(s, centerpointX - ctwidth / 2, centerpointY - ctdescent
                + ctheight / 2, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = View.MeasureSpec.getSize(widthMeasureSpec);
        height = width / 5 * 4;
        for (int size = 15; size < 100; size++) {
            paint.setTextSize(size);
            Paint.FontMetrics fm = paint.getFontMetrics();
            float cs = fm.descent - fm.ascent;
            float tempw = paint.measureText("ENTER");
            if (cs > height / 8 || tempw > width / 8) {
                contentsize = size;
                break;
            }
        }
        this.setMeasuredDimension((int) width, (int) height);
    }

    public Map<PINPadButton, int[]> getButtonsCoordinates() {
        int[] l = new int[2];
        getLocationOnScreen(l);
        int x0 = l[0], x1 = (int) (l[0] + width / 3), x2 = (int) (l[0] + width / 3 * 2), x3 = (int) (l[0] + width);
        int y0 = l[1], y1 = (int) (l[1] + height / 4), y2 = (int) (l[1] + height / 2), y3 = (int) (l[1] + height / 4 * 3), y4 = (int) (l[1] + height);
        Log.d("RNIB", "1:" + setLogCoordination(x0, y0, x1, y1));
        Log.d("RNIB", "2:" + setLogCoordination(x1, y0, x2, y1));
        Log.d("RNIB", "3:" + setLogCoordination(x2, y0, x3, y1));
        Log.d("RNIB", "4:" + setLogCoordination(x0, y1, x1, y2));
        Log.d("RNIB", "5:" + setLogCoordination(x1, y1, x2, y2));
        Log.d("RNIB", "6:" + setLogCoordination(x2, y1, x3, y2));
        Log.d("RNIB", "7:" + setLogCoordination(x0, y2, x1, y3));
        Log.d("RNIB", "8:" + setLogCoordination(x1, y2, x2, y3));
        Log.d("RNIB", "9:" + setLogCoordination(x2, y2, x3, y3));
        Log.d("RNIB", "0:" + setLogCoordination(x1, y3, x2, y4));
        Log.d("RNIB", "X:" + setLogCoordination(x0, y3, x1, y4));
        Log.d("RNIB", "√:" + setLogCoordination(x2, y3, x3, y4));
        Map<PINPadButton, int[]> pinPadButtonMap = new HashMap<>();
        pinPadButtonMap.put(PINPadButton.NUMBER_1, new int[] {x0, y0, x1, y1});
        pinPadButtonMap.put(PINPadButton.NUMBER_2, new int[] {x1, y0, x2, y1});
        pinPadButtonMap.put(PINPadButton.NUMBER_3, new int[] {x2, y0, x3, y1});
        pinPadButtonMap.put(PINPadButton.NUMBER_4, new int[] {x0, y1, x1, y2});
        pinPadButtonMap.put(PINPadButton.NUMBER_5, new int[] {x1, y1, x2, y2});
        pinPadButtonMap.put(PINPadButton.NUMBER_6, new int[] {x2, y1, x3, y2});
        pinPadButtonMap.put(PINPadButton.NUMBER_7, new int[] {x0, y2, x1, y3});
        pinPadButtonMap.put(PINPadButton.NUMBER_8, new int[] {x1, y2, x2, y3});
        pinPadButtonMap.put(PINPadButton.NUMBER_9, new int[] {x2, y2, x3, y3});
        pinPadButtonMap.put(PINPadButton.NUMBER_0, new int[] {x1, y3, x2, y4});
        pinPadButtonMap.put(PINPadButton.CANCEL, new int[] {x0, y3, x1, y4});
        pinPadButtonMap.put(PINPadButton.ENTER, new int[] {x2, y3, x3, y4});
        return pinPadButtonMap;

    }

    public int[] getKeypadCoordination() {
        int[] l = new int[2];
        getLocationOnScreen(l);
        int[] keypadCoordination = new int[4];
        keypadCoordination[0] = 0;
        keypadCoordination[1] = l[1];
        keypadCoordination[2] = (int) width;
        keypadCoordination[3] = (int)(l[1] + height);
        return keypadCoordination;
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

    private String setLogCoordination(int x, int y, int x1, int y1) {
        return "(" + x + "," + y + ")" + ", (" + x1 + "," + y1 + ")";
    }

    private void setPaintColor(int i, Paint paint) {
        if (rf[i] == 0x1B) {
            paint.setColor(colors[4]);
        } else if (rf[i] == 0x0A) {
            paint.setColor(colors[5]);
        } else if (rf[i] == 0x0D) {
            paint.setColor(colors[6]);
        }
    }
}
