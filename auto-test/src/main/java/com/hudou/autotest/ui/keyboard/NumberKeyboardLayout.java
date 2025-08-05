package com.hudou.autotest.ui.keyboard;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.hudou.autotest.R;


/**
 * 自定义数字键盘 ，对外使用
 */
public class NumberKeyboardLayout extends LinearLayout{
    private NumberKeyBoardView keyboardView;
    private View view;


    public NumberKeyboardLayout(Context context) {
        super(context);
        init(context);
    }

    public NumberKeyboardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NumberKeyboardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.auto_test_number_keyboard, this, true);
        keyboardView = inflate.findViewById(R.id.view_keyboard);
        view = inflate.findViewById(R.id.view);
        hide();
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });
    }


    public void hide() {
        this.setVisibility(GONE);
    }

    public void show(Context context) {
        //
        this.setVisibility(VISIBLE);


        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);


        // 强制隐藏软键盘
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    //设置删除按钮的图标
    public void setBackKeyIcon(Drawable icon) {
        keyboardView.setBackKeyIcon(icon);
    }

    //打乱键盘的按键顺序，并重新绘制键盘
    public void shuffleKeyboard() {
        keyboardView.shuffleKeyboard();
    }

    //设置键盘的按键监听
    public void setIOnKeyboardListener(NumberKeyBoardView.IOnKeyboardListener listener) {
        keyboardView.setIOnKeyboardListener(listener);
    }
}
