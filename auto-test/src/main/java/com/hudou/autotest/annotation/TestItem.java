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

    interface Members {
        String name = "name";
        String description = "description";
    }
}
