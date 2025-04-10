package com.hudou.autotest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestCase {
    long numId() default 0;
    String name();
    String enDes() default "";

    interface Members {
        String numId = "numId";
        String name = "name";
        String enDes = "enDes";
    }
}
