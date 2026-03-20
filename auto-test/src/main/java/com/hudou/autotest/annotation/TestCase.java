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

    interface Members {
        String name = "name";
        String enDes = "enDes";
        String tip = "tip";
        String abandon = "abandon";
        String abandonDes = "abandonDes";
    }
}
