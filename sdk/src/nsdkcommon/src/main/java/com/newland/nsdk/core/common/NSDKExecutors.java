package com.newland.nsdk.core.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Author by wuhh, Date on 2020/2/10.
 */
public class NSDKExecutors {

    private static ExecutorService mThreadPool = NSDKExecutors.newCachedThreadPool(5);
    private static ExecutorService mFixedThreadPool = Executors.newFixedThreadPool(5);
    private static boolean threadPoolRunning = true;

    private NSDKExecutors() {
    }

    public static boolean isThreadPoolRunning() {
        return threadPoolRunning;
    }

    public static void setThreadPoolRunning(boolean threadPoolRunning) {
        NSDKExecutors.threadPoolRunning = threadPoolRunning;
    }

    public static ExecutorService getThreadPoolInstance() {
        if (mThreadPool == null) {
            mThreadPool = NSDKExecutors.newCachedThreadPool(5);
        }
        return mThreadPool;
    }

    public static ExecutorService getFixedThreadPoolInstance() {
        return mFixedThreadPool;
    }

    private static ThreadPoolExecutor newCachedThreadPool(int corePoolSize) {
        if (corePoolSize < 0) {
            return null;
        }

        return new ThreadPoolExecutor(corePoolSize, Integer.MAX_VALUE,
                3L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
    }

    public static void threadStart(Runnable runnable) {
        NSDKExecutors.getThreadPoolInstance().submit(runnable);
    }

    public static void release() {
        if (mThreadPool != null) {
            mThreadPool.shutdown();
            mThreadPool = null;
        }
    }
}
