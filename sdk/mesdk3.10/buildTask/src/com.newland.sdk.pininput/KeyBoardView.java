package com.newland.sdk.pininput;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.newland.os.NlBuild;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.newland.buildtask.R;
import com.newland.sdk.me.module.pininput.KeyBoardParams;
import com.newland.sdk.module.pin.DefaultLayout;
import com.newland.sdk.module.usb.USBModule;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.utils.ISOUtils;

/**
 * Author by wuhh, Date on 2019/11/18.
 */
public class KeyBoardView extends View {
    private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger(KeyBoardView.class.getSimpleName());
    private Context context;
    private float width, height;
    private Paint paint,paintText;
    private int textSize;
    private int[][] pinButtons;//len==15
    private byte[] keyValue;//len==15
    private KeyBoardLayoutConfig lyConfig;

//    private int screenWidth;
//    private int screenHeight;
    public void setKeyBoardLayoutConfig(KeyBoardLayoutConfig lyConfig){
        devicelogger.debug(">>>KeyBoardLayoutConfig="+lyConfig);
        this.lyConfig = lyConfig;
    }

    public KeyBoardView(Context context) {
        this(context,null);
    }

    public KeyBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        devicelogger.debug(">>>PinKeyBoard Init");
        paint = new Paint();
        paintText = new Paint();
        paint.setAntiAlias(true);
        paintText.setAntiAlias(true);
        keyValue = new byte[]{PinKey.KEY_NUM_1, PinKey.KEY_NUM_2, PinKey.KEY_NUM_3, PinKey.KEY_FUN_CANCEL,
                PinKey.KEY_NUM_4, PinKey.KEY_NUM_5, PinKey.KEY_NUM_6, PinKey.KEY_FUN_BACKSPACE,
                PinKey.KEY_NUM_7, PinKey.KEY_NUM_8, PinKey.KEY_NUM_9, 0x2E,
                PinKey.KEY_NUM_0, 0x1C, PinKey.KEY_FUN_CONFIRM};
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(isX800()){
            width = MeasureSpec.getSize(widthMeasureSpec);
            height = MeasureSpec.getSize(heightMeasureSpec);
            width = height*lyConfig.getDefaultLayoutParam().getScale();
            devicelogger.debug(">>>onMeasure X800 width="+width+" height="+height);
        }else{
            width = MeasureSpec.getSize(widthMeasureSpec);
            height = width*lyConfig.getDefaultLayoutParam().getScale();
        }
        devicelogger.debug(">>>onMeasure width="+width+" height="+height);
        for (int size = 15; size < 100; size++) {
            paint.setTextSize(size);
            FontMetrics fm = paint.getFontMetrics();
            float cs = fm.descent - fm.ascent;
            float tempw = paint.measureText(getResources().getString(R.string.keyboard_confirm));
            if (cs > height / 8 || tempw > width / 8) {
                textSize = size+10;
                break;
            }
        }
        this.setMeasuredDimension((int) width, (int) height);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        devicelogger.debug(">>>onDraw");
        canvas.drawColor(lyConfig.getDefaultLayoutParam().getBgColor());
        drawKeyBoard(canvas);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void drawKeyBoard(Canvas canvas){
        String value = null;
        Bitmap bitmap = null;
        int[] l = new int[2];
        getLocationOnScreen(l);
        int x0 = l[0],y0 = l[1];

        DefaultLayout.KeyAttribute keyAttribute=null;
        for(int i=0; i<keyValue.length; i++){
            int[] xy = pinButtons[i];
            int x1 = xy[0],y1 = xy[1];
            int x2 = xy[2],y2 = xy[3];
            if(x1<0||y1<0||x2<0||y2<0){
                continue;
            }
            if(i==3||i==7||i==14){
                if(keyValue[i] == PinKey.KEY_FUN_CANCEL){
                    keyAttribute = lyConfig.getDefaultLayoutParam().getCancelKeyAttr();
                }else if(keyValue[i] == PinKey.KEY_FUN_BACKSPACE){
                    keyAttribute = lyConfig.getDefaultLayoutParam().getBackSpaceKeyAttr();
                }else if(keyValue[i] == PinKey.KEY_FUN_CONFIRM){
                    keyAttribute = lyConfig.getDefaultLayoutParam().getConfirmAttr();
                }
                paint.setColor(keyAttribute.getBackgroundColor());
                value = keyAttribute.getText();
                paintText.setColor(keyAttribute.getTextColor());
                int textSize = (keyAttribute.getTextSize()<=0?this.textSize:keyAttribute.getTextSize());
                paintText.setTextSize(textSize);
            }else{
                keyAttribute = lyConfig.getDefaultLayoutParam().getNumKeyAttr();
                paint.setColor(keyAttribute.getBackgroundColor());
                value = keyValue[i]-48+"";
                paintText.setColor(keyAttribute.getTextColor());
                int textSize = (keyAttribute.getTextSize()<=0?this.textSize:keyAttribute.getTextSize());
                paintText.setTextSize(textSize);
            }
            bitmap = keyAttribute.getBitmap();
            if(lyConfig.getDefaultLayoutParam().getRoundSize() > 0){
                canvas.drawRoundRect(x1-x0,y1-y0,x2-x0,y2-y0,lyConfig.getDefaultLayoutParam().getRoundSize(),lyConfig.getDefaultLayoutParam().getRoundSize(),paint);
            }else{
                canvas.drawRect(x1-x0,y1-y0,x2-x0,y2-y0,paint);
            }
            if(keyValue[i] == PinKey.KEY_FUN_BACKSPACE && keyAttribute.getText() == null && keyAttribute.getBitmap() == null){
                drawBackspace(canvas,(x2-x1)/2+x1-x0,(y2-y1)/2+y1-y0,height/12);
            }else if(value != null){
                drawTextCenter(canvas,paintText,(x2-x1)/2+x1-x0,(y2-y1)/2+y1-y0,value);
            }else if(bitmap != null){
                drawBitmapCenter(canvas,l,xy,bitmap);
            }
        }
    }

    public void initRNIBCoordinate(){
        pinButtons = initKeyCoordinates(0);
    }
    public byte[] getCoordinate() {
        int[][] pinButtons0 = initKeyCoordinates(lyConfig.getDefaultLayoutParam().getAngle());
        int[] coordinateInt = new int[pinButtons0.length*4];
        for(int i=0; i<pinButtons0.length; i++){
            System.arraycopy(pinButtons0[i],0,coordinateInt,i*4,4);
        }
        byte[] initCoordinate = new byte[coordinateInt.length * 2];
        for (int i = 0, j = 0; i < coordinateInt.length; i++, j++) {
            initCoordinate[j] = (byte) ((coordinateInt[i] >> 8) & 0xff);
            j++;
            initCoordinate[j] = (byte) (coordinateInt[i] & 0xff);
        }

        pinButtons = initKeyCoordinates(0);
        return initCoordinate;
    }
    public byte[] getPinKeySeq() {
        if (lyConfig.getDefaultLayoutParam().getKeyRondomType() == DefaultLayout.KeyRondomType.NORMAL) {// not random
            return new byte[]{0x31, 0x32, 0x33, PinKey.KEY_FUN_CANCEL, 0x34, 0x35, 0x36,
                    PinKey.KEY_FUN_BACKSPACE, 0x37, 0x38, 0x39, 0x2E, 0x30, 0x1C, PinKey.KEY_FUN_CONFIRM};
        } else if (lyConfig.getDefaultLayoutParam().getKeyRondomType() == DefaultLayout.KeyRondomType.RANDOM_NUM) {//Numbers are random,but function keys are not random.
            return null;
        } else {//Number key and function key are random.
            return new byte[]{0x7E, 0x7E, 0x7E, 0x7F, 0x7E, 0x7E, 0x7E, 0x7F,
                    0x7E, 0x7E, 0x7E, 0x2E, 0x7E, 0x1C, 0x7F};
        }
    }

    public int getStatusBarHeight() {
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");

        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        devicelogger.debug( "getStatusBarHeight: "+statusBarHeight);
        return statusBarHeight;
    }
    public int getNavigationBarHeight() {
        int navigationBarHeight = 0;
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");

        if (resourceId > 0) {
            navigationBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        devicelogger.debug( "navigationBarHeight: "+navigationBarHeight);
        return navigationBarHeight;
    }


    private int[][] initKeyCoordinates(int angle){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Display display = null;//getPresentationDisplay(context);
        if (display != null) {
            display.getRealMetrics(displayMetrics);
        }else{
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getRealMetrics(displayMetrics);
        }
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        devicelogger.debug(">>>KeyBoardView screenWidth="+screenWidth+" screenHeight="+screenHeight);

        //getStatusBarHeight();
        //getNavigationBarHeight();
        int[] l = new int[2];
        getLocationOnScreen(l);
        int x0 = l[0],itemWidth = (int)(width/4);
        int y0 = l[1],itemHeight = (int)(height/4);
        int dividerSize = lyConfig.getDefaultLayoutParam().getDividerSize();
        devicelogger.debug(">>>initKeyCoordinates width="+width+" height="+height+" x0="+x0+" y0="+y0+" itemWidth="+itemWidth+" itemHeight="+itemHeight+" dividerSize="+dividerSize+" screenWidth="+screenWidth+" screenHeight="+screenHeight);
        int[][] buttons = new int [16][];
        if(angle == 0 || angle == 180){
            for(int i=0; i<4; i++){
                for(int j=0; j<4; j++){
                    int t = i*4+j;
                    if(angle == 180){
                        buttons[t] = new int[]{j*itemWidth+x0,i*itemHeight+y0,(j+1)*itemWidth+x0 ,(i+1)*itemHeight+y0 };
                        int x = screenWidth -  buttons[t][0];
                        int y = screenHeight - buttons[t][1] ;
                        buttons[t] = new int[]{x - itemWidth + dividerSize, y - itemHeight + dividerSize, x - dividerSize, y - dividerSize};
                    }else {
                        buttons[t] = new int[]{j*itemWidth+x0+dividerSize,
                                i*itemHeight+y0+dividerSize,
                                (j+1)*itemWidth+x0-dividerSize,
                                (i+1)*itemHeight+y0-dividerSize};
                    }
                }
            }
        }

        if(angle == 90){
            int x1 = screenHeight - y0;
            int y1 = x0;
            for (int i = 4; i > 0; i--) {
                for (int j = 0; j < 4; j++) {
                    int t = (4 - i) * 4 + j;
                    buttons[t] = new int[]{
                            x1 - itemHeight*(5 - i) + dividerSize,
                            y1 + itemWidth*j + dividerSize,
                            x1 - itemHeight*(5 - i) + itemHeight - dividerSize,
                            y1 + itemWidth*j + itemWidth- dividerSize,};
                }
            }
        }
        if(angle == 270){
            int x1 = screenHeight - itemHeight * 4;
            int y1 = itemWidth * 4;
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    int t = i * 4 + j;
                    buttons[t] = new int[]{
                            x1 + itemHeight * i + dividerSize,
                            y1 - (itemWidth * (j + 1)) + dividerSize,
                            (x1 + itemHeight * i) + itemHeight - dividerSize,
                            y1 - (itemWidth * (j + 1)) + itemWidth - dividerSize,
                    };
                }
            }
        }

        return lyConfig.getLayoutStyle(buttons,angle);
    }

    public void loadVppInitKey(byte[] keys) {
        keyValue = keys;
        invalidate();
    }

    private void drawTextCenter(Canvas canvas, Paint paint, float centerpointX, float centerpointY, String s) {
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(4);
        FontMetrics fmtemp = paint.getFontMetrics();
        int ctwidth = (int) paint.measureText(s);
        int ctheight = (int) Math.ceil(fmtemp.descent - fmtemp.ascent);
        int ctdescent = (int) fmtemp.descent;
        canvas.drawText(s, centerpointX - ctwidth / 2, centerpointY - ctdescent + ctheight / 2, paint);
    }
    private void drawBitmapCenter(Canvas canvas,int[] l, int[] xy,Bitmap bitmap) {
        try {
            int x0 = l[0],y0 = l[1];
            int x1 = xy[0],y1 = xy[1];
            int x2 = xy[2],y2 = xy[3];
            int centerX = (x2-x1)/2+x1-x0,centerY = (y2-y1)/2+y1-y0;
    //        float itemW = (x2-x1)/2,itemH = (y2-y1)/2;
            float itemW = width/4/2,itemH = height/4/2;
            Matrix matrix = new Matrix();
            matrix.postScale(itemW/bitmap.getWidth(),itemH/bitmap.getHeight());
            Bitmap targetBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
            matrix.setTranslate(centerX-itemW/2, centerY-itemH/2);
            canvas.drawBitmap(targetBmp,matrix,new Paint());
            if (!targetBmp.isRecycled()) {
                targetBmp.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void drawBackspace(Canvas canvas, float centerX, float centerY, float sizeheight) {
        float left = centerX - sizeheight;
        float top = centerY - sizeheight / 2;
        Path path = new Path();
        path.reset();
        path.moveTo(left, top + sizeheight / 2);
        path.lineTo(left + sizeheight / 2, top);
        path.lineTo(left + 2 * sizeheight, top);
        path.lineTo(left + 2 * sizeheight, top + sizeheight);
        path.lineTo(left + sizeheight / 2, top + sizeheight);
        path.close();

        int gap = 8;
        path.moveTo(left + 2 * sizeheight / 8 * 3 + 5, top + gap);
        path.lineTo(left + 2 * sizeheight / 8 * 7 - 5, top + sizeheight - gap);
        path.moveTo(left + 2 * sizeheight / 8 * 7 - 5, top + gap);
        path.lineTo(left + 2 * sizeheight / 8 * 3 + 5, top + sizeheight - gap);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, paint);
    }

    public class PinKey{
        public static final int KEY_NUM_RANDOM = 0x7E;
        public static final int KEY_NUM_0 = 0X30;
        public static final int KEY_NUM_1 = 0X31;
        public static final int KEY_NUM_2 = 0X32;
        public static final int KEY_NUM_3 = 0X33;
        public static final int KEY_NUM_4 = 0X34;
        public static final int KEY_NUM_5 = 0X35;
        public static final int KEY_NUM_6 = 0X36;
        public static final int KEY_NUM_7 = 0X37;
        public static final int KEY_NUM_8 = 0X38;
        public static final int KEY_NUM_9 = 0X39;
        public static final int KEY_FUN_RANDOM = 0x7F;
        public static final int KEY_FUN_CANCEL = 0x1B;
        public static final int KEY_FUN_BACKSPACE = 0x0A;
        public static final int KEY_FUN_CONFIRM= 0x0D;
        public static final int KEY_FUN_CLEAR = 0x9C;
        public static final int KEY_FUN_EXIT = 0x9B;
    }
    private static boolean isX800(){
        return "X800".equals(NlBuild.VERSION.MODEL);
    }
}
