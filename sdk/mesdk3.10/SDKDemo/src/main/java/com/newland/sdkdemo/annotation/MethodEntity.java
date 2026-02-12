package com.newland.sdkdemo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author by bxy, Date on 2019/4/8 0008.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MethodEntity {

    String finddesc() default "";

    int nameid() default -1;//for internationalization
    String name() default "";

    int id() default 0;
}
