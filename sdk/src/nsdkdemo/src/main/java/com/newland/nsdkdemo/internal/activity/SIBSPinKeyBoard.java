package com.newland.nsdkdemo.internal.activity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.internal.pinentry.PINPadButton;
import com.newland.nsdkdemo.R;

import java.util.HashMap;
import java.util.Map;

public class SIBSPinKeyBoard extends View {
    private float width, height;
    private Paint paint;
    private DisplayMetrics dm;
    private int contentsize = 0;
    public SIBSPinKeyBoard(Context context) {
        super(context);
        getScreenResolution(context);
        init();
    }

    public SIBSPinKeyBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        getScreenResolution(context);
        init();
    }

    private void getScreenResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        LogUtils.i("B3PinKeyBoard----1", "height=" + dm.heightPixels + ";width" + dm.widthPixels);
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, width, height, paint);
        paint.setColor(Color.WHITE);
        canvas.drawLine(0,0, width, 0, paint);
        canvas.drawLine(0, height / 4, width, height / 4, paint);
        canvas.drawLine(0, height / 2, width, height / 2, paint);
        canvas.drawLine(0, height / 4 * 3, width, height / 4 * 3, paint);
        canvas.drawLine(0, height, width, height, paint);
        canvas.drawLine(0, 0, 0, height, paint);
        canvas.drawLine(width / 3, 0, width / 3, height, paint);
        canvas.drawLine(width / 3 * 2, 0, width / 3 * 2, height, paint);
        canvas.drawLine(width, 0, width, height, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        for (int size = 15; size < 100; size++) {
            paint.setTextSize(size);
            Paint.FontMetrics fm = paint.getFontMetrics();
            float cs = fm.descent - fm.ascent;
            float tempw = paint.measureText(getResources().getString(R.string.keyboard_confirm));
            if (cs > height / 8 || tempw > width / 8) {
                contentsize = size;
                break;
            }
        }
        this.setMeasuredDimension((int) width, (int) height);
    }


    public Map<PINPadButton, int[]> getPINPadButtons() {
        int[] l = new int[2];
        getLocationOnScreen(l);
        int x0 = l[0], x1 = (int) (l[0] + width / 3), x2 = (int) (l[0] + width /3 * 2), x3 = (int) (l[0] + width), x4 = (int) (l[0] + width);
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

        buttonMap.put(PINPadButton.CANCEL, new int[]{x0, y3, x1, y4});
        buttonMap.put(PINPadButton.ENTER, new int[]{x2, y3, x3, y4});

        return buttonMap;
    }

    public int[] getAreaCoordination() {
        int[] l = new int[2];
        getLocationOnScreen(l);
        int[] areaCoordination = new int[4];
        areaCoordination[0] = l[0];
        areaCoordination[1] = l[1];
        areaCoordination[2] = (int) (l[0] + width);
        areaCoordination[3] = (int) (l[1] + height);
        return areaCoordination;
    }
}
