package com.newland.sdk.pininput;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

public class PSView extends View {
    private float width, height;
    private Paint paint;
    private int contentsize;
    private DisplayMetrics dm;

    private int ps_size = 6;
    private int ps_input_length = 0;
    private int span = 10;
    private int strokeWidth=2;
    private float rectWidth;

    public PSView(Context context) {
        super(context);
        getScreenResolution(context);
        init();
    }

    public PSView(Context context, @Nullable AttributeSet attrs) {
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
    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        contentsize = 25;
        paint.setTextSize(contentsize);
    }

    private void drawStringCenter(Canvas canvas, float centerpointX, float centerpointY, String s) {
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(4);
        Paint.FontMetrics fmtemp = paint.getFontMetrics();
        int ctwidth = (int) paint.measureText(s);
        int ctheight = (int) Math.ceil(fmtemp.descent - fmtemp.ascent);
        int ctdescent = (int) fmtemp.descent;
        canvas.drawText(s, centerpointX - ctwidth / 2, centerpointY - ctdescent + ctheight/2+("*".equals(s)?ctheight/8:0), paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = width /4;
        rectWidth = (width-(ps_size-1)*span)/ps_size;
 //       height = rectWidth+strokeWidth*2;
        for (int size = 15; size < 200; size++) {
            paint.setTextSize(size);
            Paint.FontMetrics fm = paint.getFontMetrics();
            float cs = fm.descent - fm.ascent;
            float tempw = paint.measureText("*");
            if (cs > rectWidth  || tempw > rectWidth ) {
                contentsize = size;
                break;
            }
        }
        this.setMeasuredDimension((int) width, (int) height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(0xff303030);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.STROKE);
        RectF r = new RectF();
        for(int i=0;i<ps_size;i++){
            r.set(i*(rectWidth+span)+strokeWidth,(height-rectWidth)/2,i*(rectWidth+span)+rectWidth-strokeWidth,(height-rectWidth)/2+rectWidth);
            canvas.drawRect(r,paint);
        }
        paint.setColor(Color.BLACK);
        paint.setTextSize(contentsize);
        for(int i=0;i<ps_input_length;i++){
            drawStringCenter(canvas,i*(rectWidth+span)+rectWidth/2,(height-rectWidth)/2+rectWidth/2,"*");
        }
    }

    public void setPs_size(int size){
        this.ps_size=size;
        if(this.ps_size>6)
            span=10-(this.ps_size-6)*4/3;
        rectWidth = (width-(ps_size-1)*span)/ps_size;
        invalidate();
    }

    public void showInput(int length){
        if(length<=ps_size) {
            this.ps_input_length = length;
            invalidate();
        }
    }
}
