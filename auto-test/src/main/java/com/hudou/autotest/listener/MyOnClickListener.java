package com.hudou.autotest.listener;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;

public abstract class MyOnClickListener implements View.OnClickListener {

    private static final long CLICK_DEBOUNCE_TIME = 500;
    private long lastClickTime = 0;
    private Handler handler = new Handler();
    private Runnable resetClickTimeRunnable = () -> lastClickTime = 0;

    /**
     * 判断是否是快速点击
     * @return
     */
    private boolean isFastClick(){
        long currentTime = System.currentTimeMillis(); // 获取当前时间
        if (currentTime - lastClickTime > CLICK_DEBOUNCE_TIME) { // 判断是否超过阈值
            lastClickTime = currentTime; // 更新上次点击时间
            handler.removeCallbacks(resetClickTimeRunnable); // 移除之前的延迟重置任务
            handler.postDelayed(resetClickTimeRunnable, CLICK_DEBOUNCE_TIME); // 设置延迟重置任务
            return false;
        }
        return true;
    }

    /**
     * 判断是否是快速点击
     * @return
     */
    public boolean canClick(){
        long currentTime = SystemClock.elapsedRealtime();
        if (currentTime - lastClickTime < CLICK_DEBOUNCE_TIME) {
            return false; // 在防抖动间隔内忽略点击事件
        }
        lastClickTime = currentTime;
        return true;
    }


    @Override
    public void onClick(View v) {
        if (isFastClick()){
            return;
        }
        dealClick(v);
    }


    public abstract void dealClick(View v);


}
