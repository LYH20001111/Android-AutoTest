package com.newland.autotest.annotation;

import com.newland.autotest.base.item.BaseTestItem;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TestItemClass {
    Class<? extends BaseTestItem>[] clz();
}
