package com.newland.sdk.me.module.usb.usbserial.utils;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Predicate;

import java.util.Collection;
import java.util.List;


public class Utils {
    private static final String TAG = "USB";

    public static <T> List<T> removeIf(Collection<T> c, Predicate<? super T> predicate) {
        return Stream.of(c.iterator())
                .filterNot(predicate)
                .collect(Collectors.<T>toList());
    }

    public static void d(String msg){
//        Log.d(TAG, ""+msg);
    }
}
