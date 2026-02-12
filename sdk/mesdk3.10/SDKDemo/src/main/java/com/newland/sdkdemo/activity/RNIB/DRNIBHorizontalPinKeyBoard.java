package com.newland.sdkdemo.activity.RNIB;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.newland.sdk.module.pin.PinPadButton;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class DRNIBHorizontalPinKeyBoard extends View{
    private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger(DRNIBHorizontalPinKeyBoard.class.getSimpleName());
    private float height, width;
    private Paint paint;
    private int contentsize;
    private DisplayMetrics dm;
    private int[] colors = new int[]{0xfff5f5f9, 0xffe1e1e1, 0xffffffff, 0xff000000, 0xfff24c4d, 0xfff3e250, 0xff70d145};
    private byte[] rf = new byte[]{0x1B, 0x0A, 0x0D};
    private Context context;
    public DRNIBHorizontalPinKeyBoard(Context context) {
        super(context);
        this.context = context;
        getScreenResolution(context);
        init();
    }

    public DRNIBHorizontalPinKeyBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        getScreenResolution(context);
        init();
    }

    private void getScreenResolution(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Display display = null;//getPresentationDisplay(context);
        if (display != null) {
            display.getRealMetrics(displayMetrics);
        }else{
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getRealMetrics(displayMetrics);
        }
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
        Log.i("X800PinKeyBoard", "height=" + height + ";width" + width);
    }

    private void init(){
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(colors[3]);
        float keyBoardHeight = (height - 60);

        //Cancel
        setPaintColor(0, paint);
        canvas.drawRect(width / 4 * 3 , 0, width  + 1, keyBoardHeight / 4, paint);

        //Backspace
        setPaintColor(1, paint);
        canvas.drawRect(width / 4 * 3, keyBoardHeight / 4, width + 1, keyBoardHeight / 2, paint);

        //Enter
        setPaintColor(2,paint);
        canvas.drawRect(width / 4 * 3, keyBoardHeight / 2, width, keyBoardHeight, paint);

        //horizontal separator
        paint.setColor(colors[2]);
        paint.setStrokeWidth(5);

        canvas.drawLine(0, 0, width, 0, paint);
        canvas.drawLine(0, keyBoardHeight / 4, width, keyBoardHeight / 4, paint);
        canvas.drawLine(0, keyBoardHeight / 2, width, keyBoardHeight / 2, paint);
        canvas.drawLine(0, keyBoardHeight /4 * 3, width / 4 * 3, keyBoardHeight / 4 * 3, paint);


        //vertical separator
        canvas.drawLine(0, 0, 0, keyBoardHeight, paint);
        canvas.drawLine(width / 4, 0, width / 4, keyBoardHeight, paint);
        canvas.drawLine(width / 2, 0, width / 2, keyBoardHeight, paint);
        canvas.drawLine(width / 4 * 3, 0, width / 4 * 3, keyBoardHeight, paint);
        canvas.drawLine(width, 0, width, keyBoardHeight - 5, paint);

        paint.setColor(colors[3]);
        paint.setTextSize(contentsize + 20);
        drawStringCenter(canvas, width / 8 * 7, keyBoardHeight / 8, "X");
        drawStringCenter(canvas, width / 8 * 7, keyBoardHeight / 8 * 3, "<");
        drawStringCenter(canvas, width / 8 * 7, keyBoardHeight / 4 * 3, "√");

        paint.setColor(colors[2]);
        paint.setTextSize(contentsize + 20);
        drawStringCenter(canvas, width / 8, keyBoardHeight / 8, "1");
        drawStringCenter(canvas, width / 8 * 3, keyBoardHeight / 8, "2");
        drawStringCenter(canvas, width / 8 * 5, keyBoardHeight / 8, "3");
        drawStringCenter(canvas, width / 8, keyBoardHeight / 8 * 3, "4");
        drawStringCenter(canvas, width / 8 * 3, keyBoardHeight / 8 * 3, "5");
        drawStringCenter(canvas, width / 8 * 5, keyBoardHeight / 8 * 3, "6");
        drawStringCenter(canvas, width / 8, keyBoardHeight / 8 * 5, "7");
        drawStringCenter(canvas, width / 8 * 3, keyBoardHeight / 8 * 5, "8");
        drawStringCenter(canvas, width / 8 * 5, keyBoardHeight / 8 * 5, "9");
        drawStringCenter(canvas, width / 8 * 3, keyBoardHeight / 8 * 7, "0");

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

    public Map<PinPadButton, int[]> getKeyCoordinates() {
        int[] l = new int[2];
        getLocationOnScreen(l);
        int totalHeight = (int) (height - 60);
        int x0 = l[0], x1 = (int) (l[0] + width / 4), x2 = (int) (l[0] + width / 2), x3 = (int) (l[0] + width / 4 * 3), x4 = (int) (l[0] + width);
        int y0 = 60, y1 = (int) 60 + (totalHeight / 4), y2 = (int) 60 + (totalHeight / 2), y3 = (int) 60 +  (totalHeight / 4 * 3), y4 = (int) 60 + totalHeight;

        Map<PinPadButton, int[]> buttonMap = new HashMap<>();
        buttonMap.put(PinPadButton.NUMBER_1, new int[] {x0, y0, x1, y1});
        buttonMap.put(PinPadButton.NUMBER_2, new int[] {x1, y0, x2, y1});
        buttonMap.put(PinPadButton.NUMBER_3, new int[] {x2, y0, x3, y1});
        buttonMap.put(PinPadButton.CANCEL, new int[] {x3, y0, x4, y1});
        buttonMap.put(PinPadButton.NUMBER_4, new int[] {x0, y1, x1, y2});
        buttonMap.put(PinPadButton.NUMBER_5, new int[] {x1, y1, x2, y2});
        buttonMap.put(PinPadButton.NUMBER_6, new int[] {x2, y1, x3, y2});
        buttonMap.put(PinPadButton.BACKSPACE, new int[] {x3, y1, x4, y2});
        buttonMap.put(PinPadButton.NUMBER_7, new int[] {x0, y2, x1, y3});
        buttonMap.put(PinPadButton.NUMBER_8, new int[] {x1, y2, x2, y3});
        buttonMap.put(PinPadButton.NUMBER_9, new int[] {x2, y2, x3, y3});
        buttonMap.put(PinPadButton.ENTER, new int[] {x3, y2, x4, y4});
        buttonMap.put(PinPadButton.BLANK1, new int[] {x0, y3, x1, y4});
        buttonMap.put(PinPadButton.NUMBER_0, new int[] {x1, y3, x2, y4});
        buttonMap.put(PinPadButton.BLANK2, new int[] {x2, y3, x3, y4});
        return buttonMap;
    }

    public int[] getTouchCoordinates(){
        devicelogger.debug("getTouchArea1 screenWidth="+width+" screenHeight="+height);
        return new int[]{0x00,0x00, (int) width, (int) height};

    }

    public int[] getKeyboradCoordinates(){
        int[] l = new int[2];
        getLocationOnScreen(l);
        int x1 = l[0];
        int y1 = l[1];
        int x2 = (int)width;
        int y2 = (int)height;
        return new int[]{x1,y1,x2,y2};
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
