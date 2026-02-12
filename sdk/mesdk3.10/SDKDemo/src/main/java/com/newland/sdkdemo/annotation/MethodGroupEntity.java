package com.newland.sdkdemo.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Author by bxy, Date on 2019/4/8 0008.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodGroupEntity {

    String finddesc() default "";

    int  groupnameid() default -1;//for internationalization
    String groupname() default "";

    int groupid() default 0;

    int childnameid() default -1;//for internationalization
    String childname() default "";

    int childid() default 0;
}
