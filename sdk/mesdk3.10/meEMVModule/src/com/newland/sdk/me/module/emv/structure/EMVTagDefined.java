package com.newland.sdk.me.module.emv.structure;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EMVTagDefined {

    /**
     * Tag value
     *
     * @return
     */
    public int tag();
}
