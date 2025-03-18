package com.hudou.autotest.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;

public class MyViewPager extends ViewPager {
    public MyViewPager(@NonNull Context context) {
        super(context);
        init();
    }

    public MyViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false; // 不处理触摸事件
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return false; // 不拦截触摸事件
    }

    @Override
    public void setOffscreenPageLimit(int limit) {
        super.setOffscreenPageLimit(limit);
    }

    /**
     * 重写 setOffscreenPageLimit 方法，并通过反射修改内部的 mOffscreenPageLimit 属性值。但需要注意的是，这种方法可能因 Android 版本更新而失效
     */
    private void init() {
        try {
            Field field = ViewPager.class.getDeclaredField("mOffscreenPageLimit");
            field.setAccessible(true);
            field.setInt(this, 0); // 设置为 0

            Field defaultOffscreenPages = ViewPager.class.getDeclaredField("DEFAULT_OFFSCREEN_PAGES");
            defaultOffscreenPages.setAccessible(true);
            defaultOffscreenPages.setInt(this, 0); // 设置为 0
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
