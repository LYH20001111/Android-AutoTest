package com.hudou.autotest.util;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class SynchronizedMutableLiveData<T> extends MutableLiveData<T> {
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final AtomicBoolean processed = new AtomicBoolean(true);
    private CountDownLatch latch = new CountDownLatch(1);

    @Override
    @MainThread
    public void setValue(T value) {
        super.setValue(value);
        processed.set(false);
        latch.countDown();
    }

    @Override
    public void postValue(T value) {
        mainHandler.post(() -> {
            setValue(value);
        });
    }

    public void synchronizedPostValue(T value) throws InterruptedException {
        postValue(value);
        latch.await(); // 等待上一个值被处理
        latch = new CountDownLatch(1); // 重置计数器
    }

    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        super.observe(owner, value -> {
            observer.onChanged(value);
            processed.set(true); // 标记当前值已处理
        });
    }
}