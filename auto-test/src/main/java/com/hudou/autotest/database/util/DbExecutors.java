package com.hudou.autotest.database.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DbExecutors {
    private static final ExecutorService IO = Executors.newSingleThreadExecutor();

    public static void io(Runnable block) {
        IO.execute(block);
    }
}