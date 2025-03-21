package com.hudou.autotest.annotation;

import com.hudou.autotest.base.item.AutoTestTestItem;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TestItemClass {
    Class<? extends AutoTestTestItem>[] clz();
}
