package com.newland.sdkdemo.activity.RNIB;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.newland.sdk.module.pin.PinPadButton;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * STYLE_2
 */
public class DRNIBPinKeyBoard2 extends View{
    private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger(DRNIBPinKeyBoard2.class.getSimpleName());
    private float height, width;
    private Paint paint;
    private int contentsize;
    private int[] colors = new int[]{0xfff5f5f9, 0xffe1e1e1, 0xffffffff, 0xff000000, 0xfff24c4d, 0xfff3e250, 0xff70d145};
    private byte[] rf = new byte[]{0x1B, 0x0A, 0x0D};

    private Context context;
    public DRNIBPinKeyBoard2(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public DRNIBPinKeyBoard2(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
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
        canvas.drawRect(0, height / 5 * 3 , width / 3 , height, paint);

        //Backspace
        setPaintColor(1, paint);
        canvas.drawRect(width / 3 , height / 5 * 4, width / 3 * 2, height, paint);

        //Enter
        setPaintColor(2,paint);
        canvas.drawRect(width / 3 * 2, height / 5 * 3 , width, height, paint);

        //horizontal separator
        paint.setColor(colors[2]);
        paint.setStrokeWidth(5);
        canvas.drawLine(0, 0, width, 0, paint);
        canvas.drawLine(0, height / 5, width, height / 5, paint);
        canvas.drawLine(0, height /5 * 2, width, height / 5 * 2, paint);
        canvas.drawLine(0, height /5 * 3, width, height / 5 * 3, paint);
        canvas.drawLine(width / 3 , height / 5 * 4, width / 3 * 2 , height / 5 * 4, paint);


        //vertical separator
        canvas.drawLine(0, 0, 0, height, paint);
        canvas.drawLine(width / 3, 0, width / 3, height, paint);
        canvas.drawLine(width / 3 * 2, 0, width / 3 * 2, height, paint);
        canvas.drawLine(width, 0, width, height, paint);

        paint.setColor(colors[3]);
        paint.setTextSize(width / 6);
        drawStringCenter(canvas, width / 6, height / 10 * 8, "X");
        drawStringCenter(canvas, width / 2, height / 10 * 9, "<");
        drawStringCenter(canvas, width / 6 * 5, height / 10 * 8, "√");

        paint.setColor(colors[2]);
        paint.setTextSize(contentsize + 40);
        drawStringCenter(canvas, width / 6, height / 10, "1");
        drawStringCenter(canvas, width / 2, height / 10, "2");
        drawStringCenter(canvas, width / 6 * 5, height / 10, "3");
        drawStringCenter(canvas, width / 6, height / 10 * 3, "4");
        drawStringCenter(canvas, width / 2, height / 10 * 3, "5");
        drawStringCenter(canvas, width / 6 * 5, height / 10 * 3, "6");
        drawStringCenter(canvas, width / 6, height / 2, "7");
        drawStringCenter(canvas, width / 2, height / 2, "8");
        drawStringCenter(canvas, width / 6 * 5, height / 2, "9");
        drawStringCenter(canvas, width / 2, height / 10 * 7, "0");

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
        width = MeasureSpec.getSize(widthMeasureSpec);
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

    private void setPaintColor(int i, Paint paint) {
        if (rf[i] == 0x1B) {
            paint.setColor(colors[4]);
        } else if (rf[i] == 0x0A) {
            paint.setColor(colors[5]);
        } else if (rf[i] == 0x0D) {
            paint.setColor(colors[6]);
        }
    }

    public Map<PinPadButton, int[]>  getKeyCoordinates() {
        int[] l = new int[2];
        getLocationOnScreen(l);
        int x0 = l[0], x1 = (int) (l[0] + width / 3), x2 = (int) (l[0] + width / 3 * 2), x3 = (int) (l[0] + width);
        int y0 = l[1], y1 = (int) (l[1] + height / 5), y2 = (int) (l[1] + height / 5 * 2), y3 = (int) (l[1] + height / 5 * 3), y4 = (int) (l[1] + height / 5 * 4), y5 = (int) (l[1] + height);
        int[] keyCoordinates = new int[52];
        Map<PinPadButton, int[]> buttonMap = new HashMap<>();
         buttonMap.put(PinPadButton.NUMBER_1,new int[] {x0, y0, x1, y1});//1
         buttonMap.put(PinPadButton.NUMBER_2,new int[] {x1, y0, x2, y1});//2
         buttonMap.put(PinPadButton.NUMBER_3,new int[] {x2, y0, x3, y1});//3
         buttonMap.put(PinPadButton.NUMBER_4,new int[] {x0, y1, x1, y2});//4
         buttonMap.put(PinPadButton.NUMBER_5,new int[] {x1, y1, x2, y2});//5
         buttonMap.put(PinPadButton.NUMBER_6,new int[] {x2, y1, x3, y2});//6
         buttonMap.put(PinPadButton.NUMBER_7,new int[] {x0, y2, x1, y3});//7
         buttonMap.put(PinPadButton.NUMBER_8,new int[] {x1, y2, x2, y3});//8
         buttonMap.put(PinPadButton.NUMBER_9,new int[] {x2, y2, x3, y3});//9
         buttonMap.put(PinPadButton.NUMBER_0,new int[] {x1, y3, x2, y4});//0
         buttonMap.put(PinPadButton.CANCEL,new int[] {x0, y3, x1, y5});//cancel
         buttonMap.put(PinPadButton.BACKSPACE,new int[] {x1, y4, x2, y5});//backspace
         buttonMap.put(PinPadButton.ENTER,new int[] {x2, y3, x3, y5});//enter
        return buttonMap;

    }

    public int[] getTouchCoordinates(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Display display = null;
        if (display != null) {
            display.getRealMetrics(displayMetrics);
        }else{
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getRealMetrics(displayMetrics);
        }
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        devicelogger.debug("getTouchArea screenWidth="+screenWidth+" screenHeight="+screenHeight);
        return new int[]{0x00,0x00,screenWidth,screenHeight};
    }

    public int[] getKeyboradCoordinates(){
        int[] l = new int[2];
        getLocationOnScreen(l);
        int x1 = l[0];
        int y1 = l[1];
        int x2 = x1 + (int)width;
        int y2 = y1 + (int)height;
        return new int[]{x1,y1,x2,y2};
    }

}
