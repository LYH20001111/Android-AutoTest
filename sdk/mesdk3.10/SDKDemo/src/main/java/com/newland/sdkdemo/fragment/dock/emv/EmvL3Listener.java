package com.newland.sdkdemo.fragment.dock.emv;

import android.content.Context;

import com.newland.sdk.module.emvl3.EMVL3Listener;
import com.newland.sdk.pinpad.utils.LoggerUtil;

public class EmvL3Listener implements EMVL3Listener {
    private Context context;
    private EmvL3Controller emvL3Controller;

    public EmvL3Listener(Context context) {
        this.context = context;
    }

    public EmvL3Controller getEmvL3Controller() {
        return emvL3Controller;
    }

    public void setEmvL3Controller(EmvL3Controller emvL3Controller) {
        this.emvL3Controller = emvL3Controller;
    }

    @Override
    public void onRequestInputPIN() {
        LoggerUtil.debug("EmvL3Listener-demo","onRequestInputPIN");
        emvL3Controller.doOnlinePin();
    }
}
