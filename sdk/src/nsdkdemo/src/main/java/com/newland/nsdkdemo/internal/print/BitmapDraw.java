package com.newland.nsdkdemo.internal.print;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;

public class BitmapDraw {
    private Context mContext;
    private final static String TAG = "BitmapDraw";
    /**
     * Normal font size
     */
    public static final int NORMAL_FONT_SIZE = 24;

    /**
     * Small font size
     */
    public static final int SMALL_FONT_SIZE = 16;

    /**
     * Large font size
     */
    public static final int LARGE_FONT_SIZE = 36;

    private final static int WRAP_PERCENT = 0;
    private final static int MATCH_PERCENT = 100;
    /**
     * Paper width.
     */
    private final static float PAPER_WIDTH_NORMAL = 384;
    /**
     * Large paper width.
     */
    private final static float PAPER_WIDTH_LARGE = 576;
    /**
     * Text or Image line space.
     */
    private final static int LINE_SPACE = 2;

    private final static int LINE_INTER_SPACE = 0;
    /**
     * Content canvas width.
     */
    private static float PAPER_CANVAS_WIDTH = PAPER_WIDTH_NORMAL;
    /**
     * Content canvas
     */
    private Canvas mCanvas;
    /**
     * result bitmap
     */
    private Bitmap resultBmp;
    /**
     * Current top Y coordinate
     */
    private float mStartY = 0;
    /**
     * Current bottom Y coordinate
     */
    private float mBottomY = 0;
    /**
     * Current left X coordinate
     */
    private float mStartX = 0;

    private static BitmapDraw instance;

    public static BitmapDraw getInstance(Context mContext) {
        if (instance == null) {
            synchronized (BitmapDraw.class) {
                if (instance == null || instance.mContext != mContext) {
                    instance = new BitmapDraw(mContext);
                }
            }
        } else {
            if (instance.mContext != mContext) {
                instance = new BitmapDraw(mContext);
            }
        }
        return instance;
    }

    public BitmapDraw(Context mContext) {
        this.mContext = mContext;
        Log.d(TAG, "instantiate BitmapDraw");
        if (Build.MODEL.contains("CPOS")) {
            //CPOS is big paper
            PAPER_CANVAS_WIDTH = PAPER_WIDTH_LARGE;
        }
    }
    private void setFont(Paint paint){
        //please put your ttf file into assets/fonts
//        Typeface typeface=Typeface.createFromAsset(BaseApplication.getAppContext().getAssets(), "fonts/fontbold.otf");
//        paint.setTypeface(typeface);
    }
    /**
     * create a bitmap.
     */
    public Bitmap getBitmap() {
        deleteExtraCanvas();
        mCanvas = null;
        return resultBmp;
    }

    /**
     * add a text with reverse color
     */
    public void textReverse(String text, float textSize, boolean bold, Paint.Align align) {
        if (text == null) {
            return;
        }
        Log.d(TAG, text);
        if (mStartX >= PAPER_CANVAS_WIDTH) {
            newline();
        }
        RectF rectF = drawText(mStartX, mStartY, text, textSize, bold, align, MATCH_PERCENT, true);
        mStartX = rectF.right;
        mBottomY = rectF.bottom;
        newline();
    }

    /**
     * add a vertically offset text
     */
    public void textOffset(float offsetY, String text, float textSize, boolean bold, Paint.Align align) {
        if (text == null) {
            return;
        }
        Log.d(TAG, text);
        if (mStartX >= PAPER_CANVAS_WIDTH) {
            newline();
        }
        RectF rectF = drawText(mStartX, mStartY + offsetY, text, textSize, bold, align, MATCH_PERCENT, false);
        mStartX = rectF.right;
        mBottomY = rectF.bottom;
        newline();
    }

    /**
     * add a text.
     *
     * @param text     Text to be drawn.
     * @param textSize Text size in pixels.
     * @param bold     Bold font.
     * @param align    Align specifies how drawText aligns its text relative to the [x,y] coordinates.
     */
    public void text(String text, float textSize, boolean bold, Paint.Align align) {
        if (text == null) {
            return;
        }
        Log.d(TAG, text);
        if (mStartX >= PAPER_CANVAS_WIDTH) {
            newline();
        }
        RectF rectF = drawText(mStartX, mStartY, text, textSize, bold, align, MATCH_PERCENT, false);
        mStartX = rectF.right;
        mBottomY = rectF.bottom;
        newline();
    }

    /**
     * add two text [LEFT,RIGHT].
     *
     * @param leftText  Left text to be drawn.
     * @param rightText Right text to be drawn.
     * @param textSize  Text size in pixels.
     * @param bold      Bold font.
     */
    public void text(String leftText, String rightText, float textSize, boolean bold) {
        int[] percents = {0, 100};
        String[] texts = {leftText, rightText};
        Paint.Align[] aligns = {Paint.Align.LEFT, Paint.Align.RIGHT};
        float[] textSizes = {textSize, textSize};
        boolean[] bolds = {bold, bold};
        textMulti(percents, texts, aligns, textSizes, bolds);
    }

    /**
     * add three text [LEFT,CENTER,RIGHT].
     *
     * @param leftText   Left text to be drawn.
     * @param centerText Center text to be drawn.
     * @param rightText  Right text to be drawn.
     * @param textSize   Text size in pixels.
     * @param bold       Bold font.
     */
    public void text(String leftText, String centerText, String rightText, float textSize, boolean bold) {
        int[] percents = {33, 34, 34};
        String[] texts = {leftText, centerText, rightText};
        Paint.Align[] aligns = {Paint.Align.LEFT, Paint.Align.CENTER, Paint.Align.RIGHT};
        float[] textSizes = {textSize, textSize, textSize};
        boolean[] bolds = {bold, bold, bold};
        textMulti(percents, texts, aligns, textSizes, bolds);
    }

    /**
     * add four text [LEFT,CENTER,CENTER,RIGHT]
     *
     * @param first    First text to be drawn.
     * @param second   Second text to be drawn.
     * @param third    Third text to be drawn.
     * @param fourth   Fourth text to be drawn.
     * @param textSize Text size in pixels.
     * @param bold     Bold font.
     */
    public void text(String first, String second, String third, String fourth, float textSize, boolean bold) {
        int[] percents = {25, 25, 25, 25};
        String[] texts = {first, second, third, fourth};
        Paint.Align[] aligns = {Paint.Align.LEFT, Paint.Align.CENTER, Paint.Align.CENTER, Paint.Align.RIGHT};
        float[] textSizes = {textSize, textSize, textSize, textSize};
        boolean[] bolds = {bold, bold, bold, bold};
        textMulti(percents, texts, aligns, textSizes, bolds);
    }

    /**
     * add multiple text and allocate the width according to 'percents'.
     *
     * @param percents  Percentage of width occupied .
     * @param aligns    Set the ratio of text width to paper
     * @param texts     Text array to be drawn.
     * @param textSizes Text size in pixels.
     * @param bolds     Bold font.
     */
    public void textMulti(int[] percents, String[] texts, Paint.Align[] aligns, float[] textSizes, boolean[] bolds) {
        boolean isEmpty = true;
        for (String text : texts) {
            if (text != null) {
                isEmpty = false;
                break;
            }
        }
        if (isEmpty) {
            return;
        }
        StringBuilder logBuilder = new StringBuilder();
        if (mStartX >= PAPER_CANVAS_WIDTH) {
            newline();
        }
        RectF lastRectF = new RectF(mStartX, mStartY, mStartX, mStartY);
        for (int i = 0; i < texts.length; i++) {
            String text = texts[i];
            float textSize = textSizes[i];
            boolean bold = bolds[i];
            Paint.Align align = aligns[i];
            int percent = percents[i];
            if (text != null){
                logBuilder.append(text);
            }
            logBuilder.append(" ");
            lastRectF = drawText(lastRectF.right, mStartY, text, textSize, bold, align, percent, false);
            mBottomY = Math.max(lastRectF.bottom, mBottomY);
            mStartX = lastRectF.right;
        }
        newline();
        Log.d(TAG, logBuilder.toString());
    }

    /**
     * add a centered image
     *
     * @param image image to be drawn.
     */
    public void image(Bitmap image) {
        if (image == null) {
            return;
        }
        Log.d(TAG, "[image]");
        if (mStartX >= PAPER_CANVAS_WIDTH) {
            newline();
        }
        RectF rectF = drawImage(mStartX, mStartY, image, Paint.Align.CENTER, MATCH_PERCENT);
        mStartX = rectF.right;
        mBottomY = rectF.bottom;
        newline();
    }

    /**
     * Feed the bitmap
     *
     * @param height Feeding height in pixels.
     */
    public void feedPaper(int height) {
        Log.d(TAG, "[feedPaper] " + height);
        if (mCanvas == null) {
            init();
        }
        mBottomY += height;
        resizeCanvas(mBottomY);
        newline();
    }

    /**
     * Move start position to next line
     */
    private void newline() {
        if (mCanvas == null) {
            init();
        }
        mStartX = 0;
        mStartY = mBottomY + LINE_SPACE;
        mBottomY = mStartY;
    }

    /**
     * init canvas
     */
    private void init() {
        mStartY = 0;
        mBottomY = 0;
        mStartX = 0;
        resultBmp = Bitmap.createBitmap((int) PAPER_CANVAS_WIDTH, 40, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(resultBmp);
        mCanvas.drawColor(Color.WHITE);
    }

    /**
     * Resize canvas
     *
     * @param height the minimum canvas height
     */
    private void resizeCanvas(float height) {
        if (resultBmp.getHeight() > height) {
            return;
        }
        Bitmap temp = Bitmap.createBitmap((int) PAPER_CANVAS_WIDTH, (int) ((int) height * 1.2), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(temp);
        mCanvas.drawColor(Color.WHITE);
        mCanvas.drawBitmap(resultBmp, 0, 0, new Paint());
        resultBmp = temp;
    }

    private List<String> spiltText(String text) {
        if (text.isEmpty()) {
            Log.e(TAG, "Text to be spilt shall not be null");
            return null;
        }
        List<String> values = new ArrayList<>();
        if (!text.contains("\n")) {
            values.add(text);
            return values;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '\n') {
                if (builder.length() != 0) {
                    values.add(builder.toString());
                } else {
                    values.add("");
                }
                builder = new StringBuilder();
            } else {
                builder.append(ch);
            }
        }
        if (builder.length() != 0) {
            values.add(builder.toString());
        }
        return values;
    }

    /**
     * Delete redundant canvas
     */
    private void deleteExtraCanvas() {
        if (resultBmp.getHeight() == mBottomY) {
            return;
        }
        Bitmap temp = Bitmap.createBitmap((int) PAPER_CANVAS_WIDTH, (int) mBottomY, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(temp);
        mCanvas.drawColor(Color.WHITE);
        mCanvas.drawBitmap(resultBmp, 0, 0, new Paint());
        resultBmp = temp;
    }

    /**
     * Draw text
     *
     * @param left         the X coordinate of the left side of the drawing
     * @param top          the Y coordinate of the top of the drawing
     * @param text         the text to be drawn
     * @param textSize     set the paint's text size in pixel units
     * @param bold         true to set the text to bold, false to normal.
     * @param align        set the ratio of text width to paper
     * @param widthPercent set the ratio of text width to paper,from 0-100,
     *                     0 to automatically adapt the occupied width according to the text width.
     *                     If this value exceeds the remaining width, the remaining width is automatically used
     * @param reverse      true to reverse display
     * @return the drawn area
     */
    private RectF drawText(float left, float top, String text, float textSize, boolean bold, Paint.Align align,int widthPercent, boolean reverse) {
        if (widthPercent < 0 || widthPercent > 100) {
            Log.e(TAG, "width percent shall be range from 0 to 100.");
            return null;
        }
        RectF rectF = new RectF(left, top, left, top);
        if (text == null) {
            return rectF;
        }
        if (mCanvas == null) {
            init();
        }
        Paint paint = new Paint();
        paint.setFakeBoldText(bold);
        paint.setTextSize(textSize);
        paint.setTextAlign(align);
        paint.setAntiAlias(true);
        setFont(paint);
        if (reverse) {
            paint.setColor(Color.WHITE);
        }
        List<String> values = spiltText(text);
        for (String value : values) {
            //Calculate the width of the occupied by text
            float width;
            float measureTextWidth = paint.measureText(value);
            float remainWidth = PAPER_CANVAS_WIDTH - left;
            if (widthPercent == 0) {
                width = Math.min(remainWidth, measureTextWidth);
            } else {
                width = Math.min(remainWidth, PAPER_CANVAS_WIDTH * widthPercent / 100);
            }
            // Draws text on the specified coordinates
            float drawnBottom = drawOneText(left, top, value, paint, width, reverse);
            // Reset bottom and right.
            rectF.bottom = Math.max(rectF.bottom, drawnBottom);
            rectF.right = Math.max(rectF.right, left + width);
            top = rectF.bottom + LINE_SPACE;
        }
        return rectF;
    }


    /**
     * Execute to draw text
     *
     * @param left      the X coordinate of the left side of the drawing
     * @param top       the Y coordinate of the top of the drawing
     * @param value     text content
     * @param paint     text paint tool
     * @param rectWidth rect width
     * @param reverse   true to reverse display
     * @return The Y coordinate of the bottom of the drawing
     */
    private float drawOneText(float left, float top, String value, Paint paint, float rectWidth, boolean reverse) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float textHeight = fontMetrics.descent - fontMetrics.ascent;
        //case 1: value is "".
        if (value.length() == 0) {
            resizeCanvas(top + textHeight);
            return top + textHeight;
        }
        //case 2: the width is too short or 0.
        int subIndex = paint.breakText(value, 0, value.length(), true, rectWidth, null);
        if (subIndex == 0) {
            System.err.print(value + "'s width is too short.");
            return top;
        }
        //case 3: value is normal and width is non-0.
        String line = value.substring(0, subIndex);
        String nextLine = value.substring(subIndex);
        float offsetX;
        switch (paint.getTextAlign()) {
            case CENTER:
                offsetX = rectWidth / 2;
                break;
            case RIGHT:
                offsetX = rectWidth;
                break;
            default:
                offsetX = 0;
                break;
        }
        resizeCanvas(top + textHeight);
        if (reverse) {
            //draw a black background for text
            Paint paintRect = new Paint();
            paintRect.setColor(Color.BLACK);
            paintRect.setStrokeWidth(2);
            paintRect.setStyle(Paint.Style.FILL);
            setFont(paintRect);
            if (!TextUtils.isEmpty(nextLine)) {
                mCanvas.drawRect(left, top, PAPER_CANVAS_WIDTH, top + textHeight + LINE_INTER_SPACE, paintRect);
            } else {
                mCanvas.drawRect(left, top, PAPER_CANVAS_WIDTH, top + textHeight, paintRect);
            }
            mCanvas.save();
        }
        mCanvas.drawText(line, left + offsetX, top - fontMetrics.ascent, paint);
        mCanvas.save();
        if (!TextUtils.isEmpty(nextLine)) {
            return drawOneText(left, top + textHeight + LINE_INTER_SPACE, nextLine, paint, rectWidth, reverse);
        }
        return top + textHeight;
    }

    /**
     * Draw image bitmap
     *
     * @param startX       the X coordinate of the left side of the drawing
     * @param startY       the Y coordinate of the top of the drawing
     * @param image        the image to be drawn
     * @param align        set the ratio of text width to paper
     * @param widthPercent set the ratio of image width to paper,from 0-100,
     *                     0 to automatically adapt the occupied width according to the image width.
     * @return the drawn area
     */
    private RectF drawImage(float startX, float startY, Bitmap image, Paint.Align align, int widthPercent) {
        if (widthPercent < 0 || widthPercent > 100) {
            Log.e(TAG, "width percent shall be range from 0 to 100.");
            return null;
        }
        RectF rectF = new RectF(startX, startY, startX, startY);
        if (image == null) {
            return rectF;
        }
        if (mCanvas == null) {
            init();
        }
        Paint paint = new Paint();
        paint.setTextAlign(align);
        float width;
        float remainWidth = PAPER_CANVAS_WIDTH - startX;
        if (widthPercent == WRAP_PERCENT) {
            width = Math.min(PAPER_CANVAS_WIDTH, image.getWidth());
        } else {
            width = Math.min(remainWidth, PAPER_CANVAS_WIDTH * widthPercent / 100);
        }
        float drawnBottom = drawOneImage(startX, startY, image, paint, width);
        // Reset bottom and right.
        rectF.bottom = Math.max(rectF.bottom, drawnBottom);
        rectF.right = Math.max(rectF.right, startX + width);
        return rectF;
    }

    /**
     * Execute to draw image
     *
     * @param startX    the X coordinate of the left side of the drawing
     * @param startY    the Y coordinate of the top of the drawing
     * @param bitmap    the image bitmap
     * @param paint     paint tool
     * @param rectWidth rect width
     * @return The Y coordinate of the bottom of the drawing
     */
    private float drawOneImage(float startX, float startY, Bitmap bitmap, Paint paint, float rectWidth) {
        float offsetX = 0;
        Bitmap image = scaleBitmap(bitmap);
        switch (paint.getTextAlign()) {
            case CENTER:
                offsetX = (rectWidth - image.getWidth()) / 2;
                break;
            case RIGHT:
                offsetX = rectWidth - image.getWidth();
                break;
            case LEFT:
            default:
                break;
        }
        resizeCanvas(startY + image.getHeight());
        mCanvas.drawBitmap(image, startX + offsetX, startY, paint);
        mCanvas.save();
        return startY + image.getHeight();
    }

    /**
     * To scale the bitmap input, and return in the suitable size.
     * @param origin   The original bitmap
     * @return Bitmap in the suitable size
     */
    private Bitmap scaleBitmap(Bitmap origin) {
        if (origin == null) {
            return null;
        }
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int widthPixels = dm.widthPixels;
        int width = origin.getWidth();
        int height = origin.getHeight();
        float ratio = (float) PAPER_WIDTH_NORMAL / (float) widthPixels;
        if (Build.MODEL.contains("CPOS")) {
            ratio = (float) (PAPER_WIDTH_LARGE * 3) / (float) widthPixels;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(ratio, ratio);
        Bitmap newBitmap = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        origin.recycle();
        return newBitmap;
    }


}
