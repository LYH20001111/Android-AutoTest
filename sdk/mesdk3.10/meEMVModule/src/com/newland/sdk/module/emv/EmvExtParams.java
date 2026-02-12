package com.newland.sdk.module.emv;

import android.support.annotation.NonNull;

import com.newland.sdk.module.serialport.Baudrate;
import com.newland.sdk.module.serialport.PortType;

public class EmvExtParams {

    private boolean isExternalReader;
    /**
     * port type
     */
    private PortType portType;
    /**
     * Baud rate
     */
    private Baudrate baudrate;

    /**
     * 外接键盘emv插卡还是非接
     * 0x00 插卡
     * 0x01 非接
     */
    private int mediaType = 0x01;

    /**
     * 是否开启硬件蜂鸣
     */
    private Boolean isEnableBeep;

    private boolean isEnableEMVDebug = false;

    private boolean isEnableUsedEMVPath = false;

    private boolean isRequiredPrePowerOn = true;//emv流程是否需要上电，底座+键盘，寻卡上电，emv可设置不再次上电


    /**
     * Whether to use an external cardreader. If true, the SDK automatically matches the cardreader's parameters.
     *
     * @param isExternalReader
     */
    public EmvExtParams(boolean isExternalReader) {
        this.isExternalReader = isExternalReader;
    }

    /**
     * External card reader is used by default and set the card reader parameters manually.
     *
     * @param baudrate The Baud rate used by the external card reader.{@link Baudrate}
     * @param portType The port used by the external card reader.{@link PortType}
     */
    public EmvExtParams(@NonNull Baudrate baudrate, @NonNull PortType portType) {
        this.isExternalReader = true;
        this.baudrate = baudrate;
        this.portType = portType;
    }

    public boolean isExternalReader() {
        return isExternalReader;
    }

    public PortType getPortType() {
        return portType;
    }

    public Baudrate getBaudrate() {
        return baudrate;
    }

    /**
     * get the transaction card type.
     *
     * @return 0x00-Contact; 0x01-Contactless
     */
    public int getMediaType() {
        return mediaType;
    }

    /**
     * set the transaction card type. (Contact/Contactless)
     *
     * @param mediaType 0x00-Contact; 0x01-Contactless
     */
    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public Boolean getEnableBeep() {
        return isEnableBeep;
    }

    public void setEnableBeep(Boolean enableBeep) {
        isEnableBeep = enableBeep;
    }

    public void setPortType(PortType portType) {
        this.portType = portType;
    }

    public boolean isEnableEMVDebug() {
        return isEnableEMVDebug;
    }

    public void setEnableEMVDebug(boolean enableEMVDebug) {
        isEnableEMVDebug = enableEMVDebug;
    }

    public boolean isEnableUsedEMVPath() {
        return isEnableUsedEMVPath;
    }

    public void setEnableUsedEMVPath(boolean enableUsedEMVPath) {
        isEnableUsedEMVPath = enableUsedEMVPath;
    }

    public boolean isRequiredPrePowerOn() {
        return isRequiredPrePowerOn;
    }

    public void setRequiredPrePowerOn(boolean requiredPrePowerOn) {
        isRequiredPrePowerOn = requiredPrePowerOn;
    }
}
