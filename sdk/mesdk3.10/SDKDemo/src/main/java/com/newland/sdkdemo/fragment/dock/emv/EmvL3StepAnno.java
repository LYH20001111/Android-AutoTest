package com.newland.sdkdemo.fragment.dock.emv;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author by wuhh, Date on 2020/3/19.
 */
@Documented  //有关java doc的注解
@Retention(RetentionPolicy.RUNTIME)  //保留时间，这种类型的Annotations将被JVM保留,所以他们能在运行时被JVM或其他使用反射机制的代码所读取和使用.
@Target(ElementType.METHOD) //针对方法

public @interface EmvL3StepAnno {
    int index();
}
