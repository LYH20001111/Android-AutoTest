package com.newland.nsdkdemo.common.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MethodGridEntity {
    String btnname() default "";

    int btnnameid() default -1;

    int functionid();

    int btnimageid() default -1;

    boolean issync() default true;

    int divtipid() default -1;
}
