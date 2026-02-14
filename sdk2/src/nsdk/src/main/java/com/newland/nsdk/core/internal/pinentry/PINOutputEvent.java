package com.newland.nsdk.core.internal.pinentry;

import com.newland.nsdk.core.api.internal.pinentry.ExtendedEvent;
import com.newland.nsdk.core.api.internal.pinentry.TouchState;

/**
 * @author ycq
 * @date 2025/8/1
 */
public class PINOutputEvent {
    private PINKeyEvent pinKeyEvent;          // PIN输入事件类型
    private byte[] pinBlock;            // PIN数据
    private byte[] ksn;                 // KSN数据
    private ExtendedEvent extendedEvent;   // 操作事件类型
    private TouchState touchState;      // 事件状态
    private int pinLen;                 // 当前输入的PIN长度

    public PINOutputEvent() {}

    public PINOutputEvent(PINKeyEvent pinKeyEvent, byte[] pinBlock, byte[] ksn,
                          ExtendedEvent extendedEvent, TouchState touchState, int pinLen) {
        this.pinKeyEvent = pinKeyEvent;
        this.pinBlock = pinBlock;
        this.ksn = ksn;
        this.extendedEvent = extendedEvent;
        this.touchState = touchState;
        this.pinLen = pinLen;
    }

    // Getter & Setter 方法

    public PINKeyEvent getPinKeyEvent() {
        return pinKeyEvent;
    }

    public void setPinKeyEvent(PINKeyEvent pinKeyEvent) {
        this.pinKeyEvent = pinKeyEvent;
    }

    public byte[] getPinBlock() {
        return pinBlock;
    }

    public void setPinBlock(byte[] pinBlock) {
        this.pinBlock = pinBlock;
    }

    public byte[] getKsn() {
        return ksn;
    }

    public void setKsn(byte[] ksn) {
        this.ksn = ksn;
    }

    public ExtendedEvent getExtendedEvent() {
        return extendedEvent;
    }

    public void setExtendedEvent(ExtendedEvent eventType) {
        this.extendedEvent = eventType;
    }

    public TouchState getTouchState() {
        return touchState;
    }

    public void setTouchState(TouchState touchState) {
        this.touchState = touchState;
    }

    public int getPinLen() {
        return pinLen;
    }

    public void setPinLen(int pinLen) {
        this.pinLen = pinLen;
    }
}
