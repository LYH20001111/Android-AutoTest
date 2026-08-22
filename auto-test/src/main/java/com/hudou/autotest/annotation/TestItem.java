package com.hudou.autotest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TestItem {

    String name();

    String description();

    /**
     * 不支持的设备型号列表，默认支持所有设备。当前设备命中时，该测试项下所有案例对该设备不适用。
     * 支持填写 Build.MODEL（如 SM-G9880）或 Build.MANUFACTURER + " " + Build.MODEL（如 samsung SM-G9880），忽略大小写匹配。
     */
    String[] unsupportedDevice() default {};

    /**
     * 测试项不适用当前设备的原因说明，由宿主应用设置，进入测试项或点击运行选项时的弹窗会展示该文案。
     */
    String unsupportedDeviceDes() default "";

    interface Members {
        String name = "name";
        String description = "description";
        String unsupportedDevice = "unsupportedDevice";
        String unsupportedDeviceDes = "unsupportedDeviceDes";
    }
}
