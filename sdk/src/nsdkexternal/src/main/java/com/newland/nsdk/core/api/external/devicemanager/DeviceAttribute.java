package com.newland.nsdk.core.api.external.devicemanager;

public class DeviceAttribute {
    boolean supportSmartCard;
    boolean supportContactlessCard;
    boolean supportMagCard;
    boolean supportGraphicalDisplay;
    boolean supportColourDisplay;
    boolean supportBeeper;
    boolean supportBacklight;
    String pciFirmwareID;

    public boolean isSupportSmartCard() {
        return supportSmartCard;
    }

    public void setSupportSmartCard(boolean supportSmartCard) {
        this.supportSmartCard = supportSmartCard;
    }

    public boolean isSupportContactlessCard() {
        return supportContactlessCard;
    }

    public void setSupportContactlessCard(boolean supportContactlessCard) {
        this.supportContactlessCard = supportContactlessCard;
    }

    public boolean isSupportMagCard() {
        return supportMagCard;
    }

    public void setSupportMagCard(boolean supportMagCard) {
        this.supportMagCard = supportMagCard;
    }

    public boolean isSupportGraphicalDisplay() {
        return supportGraphicalDisplay;
    }

    public void setSupportGraphicalDisplay(boolean supportGraphicalDisplay) {
        this.supportGraphicalDisplay = supportGraphicalDisplay;
    }

    public boolean isSupportColourDisplay() {
        return supportColourDisplay;
    }

    public void setSupportColourDisplay(boolean supportColourDisplay) {
        this.supportColourDisplay = supportColourDisplay;
    }

    public boolean isSupportBeeper() {
        return supportBeeper;
    }

    public void setSupportBeeper(boolean supportBeeper) {
        this.supportBeeper = supportBeeper;
    }

    public boolean isSupportBacklight() {
        return supportBacklight;
    }

    public void setSupportBacklight(boolean supportBacklight) {
        this.supportBacklight = supportBacklight;
    }

    public String getPciFirmwareID() {
        return pciFirmwareID;
    }

    public void setPciFirmwareID(String pciFirmwareID) {
        this.pciFirmwareID = pciFirmwareID;
    }
}
