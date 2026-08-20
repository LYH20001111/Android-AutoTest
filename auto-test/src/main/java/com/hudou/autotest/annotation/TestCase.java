package com.hudou.autotest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestCase {
    String name();

    String enDes() default "";

    String tip() default "";

    boolean abandon() default false;

    String abandonDes() default "";

    /**
     * 不支持的设备型号列表，默认支持所有设备。
     * 支持填写 Build.MODEL（如 SM-G9880）或 Build.MANUFACTURER + " " + Build.MODEL（如 samsung SM-G9880），忽略大小写匹配。
     */
    String[] unsupportedDevice() default {};

    interface Members {
        String name = "name";
        String enDes = "enDes";
        String tip = "tip";
        String abandon = "abandon";
        String abandonDes = "abandonDes";
        String unsupportedDevice = "unsupportedDevice";
    }
}
