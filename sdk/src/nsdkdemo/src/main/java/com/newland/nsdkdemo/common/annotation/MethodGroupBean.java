package com.newland.nsdkdemo.common.annotation;

import java.lang.reflect.Method;

public class MethodGroupBean {

    public String finddesc;
    public String groupname;
    public int groupid;
    public String childname;
    public int childid;

    public Method method;
    public Object module;

    public MethodGroupBean(String finddesc, String groupname, int groupid, String childname, int childid, Method method, Object module) {
        this.finddesc = finddesc;
        this.groupname = groupname;
        this.groupid = groupid;
        this.childname = childname;
        this.childid = childid;
        this.method = method;
        this.module = module;
    }
}
