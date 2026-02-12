package com.newland.nsdkdemo.common.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MethodGroupEntity {

    String finddesc() default "";

    //for internationalization
    int groupnameid() default -1;

    String groupname() default "";

    int groupid() default 0;

    //for internationalization
    int childnameid() default -1;

    String childname() default "";

    int childid() default 0;
}
