package com.newland.nsdkdemo.common.annotation;

import java.lang.reflect.Method;

public class MethodGridBean {
    public String name;
    public int nameid;
    public int functionid;
    public int imageid;
    public boolean issync;
    public int divtipid;

    public Method method;
    public Object module;

    public MethodGridBean(String name, int nameid, int functionid, int imageid, boolean issync, int divtipid, Method method, Object module) {
        this.name = name;
        this.nameid = nameid;
        this.functionid = functionid;
        this.imageid = imageid;
        this.issync = issync;
        this.divtipid = divtipid;
        this.method = method;
        this.module = module;
    }

    public MethodGridBean(int nameid, int imageid) {
        this.nameid = nameid;
        this.imageid = imageid;
    }
}
