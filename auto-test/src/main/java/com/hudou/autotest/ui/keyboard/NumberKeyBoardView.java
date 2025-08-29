package com.hudou.autotest.ui.keyboard;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.view.MotionEvent;


import com.hudou.autotest.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NumberKeyBoardView extends KeyboardView implements KeyboardView.OnKeyboardActionListener {

    private static final int KEYCODE_EMPTY = -10;
    private int mDeleteWidth;
    private int mDeleteHeight;
    private Drawable mDeleteBackgroundColor;
    private Drawable mDeleteDrawable;
    private Rect mDeleteDrawRect;

    private IOnKeyboardListener mOnKeyboardListener;
    private boolean showOK = true;
    private boolean mDeletePressed = false;

    public NumberKeyBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public NumberKeyBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public NumberKeyBoardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NumberKeyboardView,
                defStyleAttr, 0);
        mDeleteDrawable = a.getDrawable(R.styleable.NumberKeyboardView_xnkv_deleteDrawable);
        mDeleteBackgroundColor = a.getDrawable(R.styleable.NumberKeyboardView_xnkv_deleteBackgroundColor);
        mDeleteWidth = a.getDimensionPixelOffset(R.styleable.NumberKeyboardView_xnkv_deleteWidth,
                -1);
        mDeleteHeight = a.getDimensionPixelOffset(R.styleable.NumberKeyboardView_xnkv_deleteHeight,
                -1);
        a.recycle();

        Keyboard keyboard = new Keyboard(context, R.xml.num_keyboard);
        setKeyboard(keyboard);

        setEnabled(true);
        setPreviewEnabled(false); // 设置按键没有点击放大镜显示的效果
        setOnKeyboardActionListener(this);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        List<Keyboard.Key> keys = getKeyboard().getKeys();
        for (Keyboard.Key key : keys) {
            if (key.codes[0] == KEYCODE_EMPTY) {
                if (showOK) {
                    key.label = "确定";
                } else {
                    drawKeyBackground(key, canvas, mDeleteBackgroundColor);
                }
            } else if (key.codes[0] == Keyboard.KEYCODE_DELETE) {
                drawKeyBackground(key, canvas, mDeleteBackgroundColor);
                drawDeleteButton(key, canvas);
            }
        }
    }

    /**
     * Draw the background of the keys
     *
     * @param key
     * @param canvas
     * @param drawable
     */
    private void drawKeyBackground(Keyboard.Key key, Canvas canvas, Drawable drawable) {
//        ColorDrawable drawable = new ColorDrawable(color);
        drawable.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
        // 只对删除键做 pressed 判断
        if (key.codes[0] == Keyboard.KEYCODE_DELETE) {
            drawable.setState(mDeletePressed
                    ? new int[]{android.R.attr.state_pressed}
                    : new int[]{});
        } else {
            drawable.setState(new int[]{});   // 其它键保持默认
        }
        drawable.draw(canvas);
    }


    public void setShowCancelButton(boolean isShow) {
        this.showOK = isShow;
        invalidate();
    }


    /**
     * Set the return button icon
     *
     * @param icon
     */
    public void setBackKeyIcon(Drawable icon) {
        mDeleteDrawable = icon;
        invalidate();
    }

    private void drawDeleteButton(Keyboard.Key key, Canvas canvas) {
        if (mDeleteDrawable == null) {
            return;
        }

        // Calculate the coordinates of the delete icon drawing
        if (mDeleteDrawRect == null || mDeleteDrawRect.isEmpty()) {
            int drawWidth, drawHeight;
            int intrinsicWidth = mDeleteDrawable.getIntrinsicWidth();
            int intrinsicHeight = mDeleteDrawable.getIntrinsicHeight();

            if (mDeleteWidth > 0 && mDeleteHeight > 0) {
                drawWidth = mDeleteWidth;
                drawHeight = mDeleteHeight;
            } else if (mDeleteWidth > 0 && mDeleteHeight <= 0) {
                drawWidth = mDeleteWidth;
                drawHeight = drawWidth * intrinsicHeight / intrinsicWidth;
            } else if (mDeleteWidth <= 0 && mDeleteHeight > 0) {
                drawHeight = mDeleteHeight;
                drawWidth = drawHeight * intrinsicWidth / intrinsicHeight;
            } else {
                drawWidth = intrinsicWidth;
                drawHeight = intrinsicHeight;
            }

            // Limit the size of the icon to prevent it from exceeding the buttons
            if (drawWidth > key.width) {
                drawWidth = key.width;
                drawHeight = drawWidth * intrinsicHeight / intrinsicWidth;
            }
            if (drawHeight > key.height) {
                drawHeight = key.height;
                drawWidth = drawHeight * intrinsicWidth / intrinsicHeight;
            }

            // get the coordinates of the delete icon drawing
            int left = key.x + (key.width - drawWidth) / 2;
            int top = key.y + (key.height - drawHeight) / 2;
            mDeleteDrawRect = new Rect(left, top, left + drawWidth, top + drawHeight);
        }

        // draw delete icon drawing
        if (mDeleteDrawRect != null && !mDeleteDrawRect.isEmpty()) {
            mDeleteDrawable.setBounds(mDeleteDrawRect.left, mDeleteDrawRect.top,
                    mDeleteDrawRect.right, mDeleteDrawRect.bottom);
            mDeleteDrawable.draw(canvas);
        }
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        // process click event of keys
        // click delete key
        if (primaryCode == Keyboard.KEYCODE_DELETE) {
            if (mOnKeyboardListener != null)
                mOnKeyboardListener.onDeleteKeyEvent();
        }
        // click number key
        else if (primaryCode != KEYCODE_EMPTY) {
            if (mOnKeyboardListener != null) {
                mOnKeyboardListener.onInsertKeyEvent(Character.toString(
                        (char) primaryCode));
            }
        } else if (showOK && primaryCode == KEYCODE_EMPTY) {
            if (mOnKeyboardListener != null)
                mOnKeyboardListener.onOK();
        }
    }

    //the Characters value of  0-9 numbers
    private final List<Character> keyCodes = Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');

    /**
     * Randomly shuffle the order of keys on the numeric keypad。
     */
    public void shuffleKeyboard() {
        Keyboard keyboard = getKeyboard();
        if (keyboard != null && keyboard.getKeys() != null && keyboard.getKeys().size() > 0) {

            Collections.shuffle(keyCodes); // 随机排序数字

            // Traverse  all keys
            List<Keyboard.Key> keys = getKeyboard().getKeys();
            int index = 0;
            for (Keyboard.Key key : keys) {
                // if key is number
                if (key.codes[0] != KEYCODE_EMPTY && key.codes[0] != Keyboard.KEYCODE_DELETE) {
                    char code = keyCodes.get(index++);
                    key.codes[0] = code;
                    key.label = Character.toString(code);
                }
            }
            setKeyboard(keyboard);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getKeyboard() == null) {
            return super.onTouchEvent(event);
        }

        int x = (int) event.getX();
        int y = (int) event.getY();
        boolean insideDelete = false;

        // 先找到“删除键”
        Keyboard.Key deleteKey = null;
        for (Keyboard.Key key : getKeyboard().getKeys()) {
            if (key.codes[0] == Keyboard.KEYCODE_DELETE) {
                deleteKey = key;
                insideDelete = x >= key.x && x <= key.x + key.width &&
                        y >= key.y && y <= key.y + key.height;
                break;
            }
        }

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (insideDelete) {
                    mDeletePressed = true;
                    invalidate();
                    return true;      // 删除键自己处理
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mDeletePressed) { // 只有删除键被按下时才管
                    boolean old = mDeletePressed;
                    mDeletePressed = insideDelete;
                    if (old != mDeletePressed) invalidate();
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mDeletePressed) {
                    if (insideDelete && mOnKeyboardListener != null) {
                        mOnKeyboardListener.onDeleteKeyEvent();
                    }
                    mDeletePressed = false;
                    invalidate();
                    return true;
                }
                break;
        }

        // 其它任何情况都交给父类，保证普通键能收到 MOVE/CANCEL
        return super.onTouchEvent(event);
    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }

    /**
     * set keyboard listener
     *
     * @param listener IOnKeyboardListener
     */
    public void setIOnKeyboardListener(IOnKeyboardListener listener) {
        this.mOnKeyboardListener = listener;
    }

    /**
     * keyboard listener。
     */
    public interface IOnKeyboardListener {

        /**
         * click number key
         *
         * @param text 输入的数字
         */
        void onInsertKeyEvent(String text);

        /**
         * click delete key
         */
        void onDeleteKeyEvent();

        /**
         * click OK key
         */
        void onOK();

    }

    public void callOnInsertKey(String text) {
        if (mOnKeyboardListener != null) {
            mOnKeyboardListener.onInsertKeyEvent(text);
        }
    }

    public void callOnDeleteKey() {
        if (mOnKeyboardListener != null) {
            mOnKeyboardListener.onDeleteKeyEvent();
        }
    }

    public void callOnOK() {
        if (mOnKeyboardListener != null) {
            mOnKeyboardListener.onOK();
        }
    }

}
