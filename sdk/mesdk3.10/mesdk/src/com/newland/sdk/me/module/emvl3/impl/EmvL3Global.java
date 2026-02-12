package com.newland.sdk.me.module.emvl3.impl;

import com.newland.sdk.module.externalPin.PinpadExtParams;
import com.newland.sdk.module.pin.PinInputListener;

/**
 * @Description 将一些无家可归的参数统一管理.
 * @Author wuhh
 * @Date 2020/11/6
 */
public class EmvL3Global {

    private volatile static int mEmvStepTimeOutMs = 60000;

    private volatile static boolean isEmvL3GetPinProcess = false;
    public static byte  pinKeyType = -1;
    public static byte  pinKeyIndex = -1;
    public static byte  pinTimeOut = -1;
    public static byte[]  pinKPwdRange;
    public static PinInputListener pinInputListener;
    public static PinpadExtParams pinpadExtParams;
    private static EmvL3Step sEmvL3Step;

    public static byte[] pinblock;
    public static void setEmvL3Step(EmvL3Step emvL3Step) {
        sEmvL3Step = emvL3Step;
    }

    /**
     * 由于EMVModule是单例的,为方便使用声明为static.
     * 该变量是线程安全的;当为true的时候,表示正处于同步指令发送的空闲时间片.
     */
    private static volatile boolean isInterruptTime = false;

    private static volatile int uiEventID = EmvL3Constant.UIEvent.UI_NONE;

    private static volatile ChannelState sChannelState = ChannelState.FREE;

    public static void setPinParam(byte keyType,byte keyIndex,byte timeOut,byte[] pwdRange,PinInputListener listener,PinpadExtParams params){
        pinKeyType = keyType;
        pinKeyIndex = keyIndex;
        pinTimeOut = timeOut;
        pinKPwdRange = pwdRange;
        pinInputListener = listener;
        pinpadExtParams = params;
        sEmvL3Step.resumeStep(EmvL3Step.EmvL3ListenerStep.onRequestInputPIN, EmvL3Step.EmvL3PauseStep.getPIN);
    }
    public static void setEmvL3GetPinProcess(boolean isPinProcess) {
        isEmvL3GetPinProcess = isPinProcess;
    }

    public static boolean getIsEmvL3GetPinProcess(){
        return isEmvL3GetPinProcess;
    }

    public static boolean isInterruptTime() {
        return isInterruptTime;
    }

    public static void setIsInterruptTime(boolean isInterruptTime) {
        EmvL3Global.isInterruptTime = isInterruptTime;
    }

    public static ChannelState getChannelState() {
        return sChannelState;
    }

    public static void setChannelState(ChannelState channelState) {
        sChannelState = channelState;
    }

    public static int getEmvStepTimeOutMs() {
        return mEmvStepTimeOutMs;
    }

    public static void setEmvStepTimeOutMs(int timeOutMs) {
        if(mEmvStepTimeOutMs <= 0){
            return;
        }
        EmvL3Global.mEmvStepTimeOutMs = timeOutMs;
    }

    public static int getUiEventID() {
        return uiEventID;
    }

    public static void setUiEventID(int uiEventID) {
        EmvL3Global.uiEventID = uiEventID;
    }
}
