package com.newland.event;

/**
 * Author by bxy, Date on 2019/5/24 0024.
 */
public interface EventCallBack {
    public void callback(int event, int msgLen, byte[] msg);
}
