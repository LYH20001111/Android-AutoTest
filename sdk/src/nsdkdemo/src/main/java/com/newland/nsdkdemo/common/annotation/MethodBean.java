package com.newland.nsdkdemo.common.annotation;

import java.lang.reflect.Method;

public class MethodBean {
    public String desc;
    public String name;
    public int index;
    public Method method;
    public Object module;

    public MethodBean(String desc, String name, int index, Method method, Object module) {
        this.desc = desc;
        this.name = name;
        this.index = index;
        this.method = method;
        this.module = module;
    }
}
