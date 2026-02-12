package com.newland.sdk.module.externalPin;

public class BleBaseParams {
    boolean isBleBaseLogEnable;//是否开启底座调试日志
    boolean isStartSeetings = true;//未连接时，初始化是否跳转到系统设置页面
    BleBaseStatusListener bleBaseStatusListener;
    boolean isChangePortType = true;//初始化底座串口时，是否修改全局的portType。MIS循环初始化/读底座串口，会影响键盘的接口类型

    private boolean isHostMode = true;
    public BleBaseParams(boolean isBleBaseLogEnable,boolean isStartSeetings){
        this.isBleBaseLogEnable = isBleBaseLogEnable;
        this.isStartSeetings = isStartSeetings;
    }

    public boolean isBleBaseLogEnable() {
        return isBleBaseLogEnable;
    }

    public boolean isStartSeetings() {
        return isStartSeetings;
    }

    public BleBaseStatusListener getBleBaseStatusListener() {
        return bleBaseStatusListener;
    }

    public void setBleBaseStatusListener(BleBaseStatusListener bleBaseStatusListener) {
        this.bleBaseStatusListener = bleBaseStatusListener;
    }

    public boolean isChangePortType() {
        return isChangePortType;
    }

    public void setChangePortType(boolean changePortType) {
        isChangePortType = changePortType;
    }

    public boolean isHostMode() {
        return isHostMode;
    }

    /**
     * usb host mode or device mode; default:host mode;
     * @param hostMode
     */
    public void setHostMode(boolean hostMode) {
        isHostMode = hostMode;
    }
}
