package com.newland.nsdk.core.internal.pinentry;

/**
 * Author by wuhh, Date on 2020/4/13.
 */
public interface SysEventCallBack {
    void callback(int event, int msgLen, byte[] msg);
}
