package com.newland.nsdkdemo.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MethodEntity {

    String finddesc() default "";

    //for internationalization
    int nameid() default -1;

    String name() default "";

    int id() default 0;
}
