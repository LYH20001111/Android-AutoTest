package com.hudou.autotest.annotation;

import com.hudou.autotest.constant.FunctionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Function {
    String title();
    FunctionType type() default FunctionType.BUTTON;
}
