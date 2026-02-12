package com.newland.nsdk.core.api.external.devicemanager;

/**
 * Device info.
 */
public class ExtDeviceInfo {
    private String softwareVersion;

    private String posSN;

    private String posPN;

    private String buildOSVersion;

    private String hardware;

    private String napiAPIVersion;

    private String napiLibVersion;

    private String buildBootVersion;

    private String buildDevCFGVersion;

    private String model;

    private String buildPCIFirmwareVersion;

    private String buildPCIHardwareVersion;

    private String posCPUType;

    private String posBoardVersion;

    private String posBoardNumber;

    private String rfType;

    private String rfVersion;

    private String wifiDrvVersion;

    private DeviceAttribute deviceAttribute;

//    private String customerID;
//
//    private String language;
//
//    private String printerGreyLevel;
//
//    private String autoRun;
//
//    private String autoSleep;
//
//    private String brightness;
//
//    private boolean backLightOn;
//
//    private int keyVol;
//
//    private String bootUpPromptBmp;
//
//    private String shutdownPromptBmp;
//
//    private String shutdownPromptBmpInCharging;
//
//    private String shutdownPromptBmpInFullCharging;
//
//    private String shutdownPromptBmpInLowPower;
//
//    private int powerMode;
//
//    private String batteryStatus;
//
//    private int batteryLevel;
//
//    private boolean autoPowerOff;
//
//    private boolean autoWakeUp;
//
//    private int printLen;
//
//    private String powerRunTime;

    /**
     * Gets software version.
     *
     * @return Software version
     */
    public String getSoftwareVersion() {
        return softwareVersion;
    }

    /**
     * Sets software version.
     *
     * @param softwareVersion Software version.
     */
    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    /**
     * Gets POS SN.
     *
     * @return POS SN.
     */
    public String getPosSN() {
        return posSN;
    }

    /**
     * Sets POS SN.
     *
     * @param posSN POS SN.
     */
    public void setPosSN(String posSN) {
        this.posSN = posSN;
    }

    /**
     * Gets POS PN.
     *
     * @return POS PN.
     */
    public String getPosPN() {
        return posPN;
    }

    /**
     * Sets POS PN.
     *
     * @param posPN POS PN.
     */
    public void setPosPN(String posPN) {
        this.posPN = posPN;
    }

    /**
     * Gets OS version.
     *
     * @return OS version.
     */
    public String getBuildOSVersion() {
        return buildOSVersion;
    }

    /**
     * Sets OS version.
     *
     * @param buildOSVersion OS version.
     */
    public void setBuildOSVersion(String buildOSVersion) {
        this.buildOSVersion = buildOSVersion;
    }

    /**
     * Gets hardware configuration.
     *
     * @return Hardware configuration.
     */
    public String getHardware() {
        return hardware;
    }

    /**
     * Sets hardware configuration.
     *
     * @param hardware Hardware configuration.
     */
    public void setHardware(String hardware) {
        this.hardware = hardware;
    }

    /**
     * Gets NAPI API version.
     *
     * @return NAPI API version.
     */
    public String getNapiAPIVersion() {
        return napiAPIVersion;
    }

    /**
     * Sets NAPI API version.
     *
     * @param napiAPIVersion NAPI version.
     */
    public void setNapiAPIVersion(String napiAPIVersion) {
        this.napiAPIVersion = napiAPIVersion;
    }

    /**
     * Gets NAPI lib version.
     *
     * @return NAPI lib version.
     */
    public String getNapiLibVersion() {
        return napiLibVersion;
    }

    /**
     * Sets NAPI lib version.
     *
     * @param napiLibVersion NAPI lib version.
     */
    public void setNapiLibVersion(String napiLibVersion) {
        this.napiLibVersion = napiLibVersion;
    }

    /**
     * Gets boot version.
     *
     * @return Boot version.
     */
    public String getBuildBootVersion() {
        return buildBootVersion;
    }

    /**
     * Sets boot version.
     *
     * @param buildBootVersion Boot version.
     */
    public void setBuildBootVersion(String buildBootVersion) {
        this.buildBootVersion = buildBootVersion;
    }

    /**
     * Gets DevCFG version.
     *
     * @return DevCFG version.
     */
    public String getBuildDevCFGVersion() {
        return buildDevCFGVersion;
    }

    /**
     * Sets DevCFG version.
     *
     * @param buildDevCFGVersion DevCFG version.
     */
    public void setBuildDevCFGVersion(String buildDevCFGVersion) {
        this.buildDevCFGVersion = buildDevCFGVersion;
    }

    /**
     * Gets device model.
     *
     * @return Device model.
     */
    public String getModel() {
        return model;
    }

    /**
     * Sets device model.
     *
     * @param model Device model.
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Gets PCI firmware version.
     *
     * @return PCI firmware version.
     */
    public String getBuildPCIFirmwareVersion() {
        return buildPCIFirmwareVersion;
    }

    /**
     * Sets PCI firmware version.
     *
     * @param buildPCIFirmwareVersion PCI firmware version.
     */
    public void setBuildPCIFirmwareVersion(String buildPCIFirmwareVersion) {
        this.buildPCIFirmwareVersion = buildPCIFirmwareVersion;
    }

    /**
     * Gets PCI hardware version.
     *
     * @return PCI hardware version.
     */
    public String getBuildPCIHardwareVersion() {
        return buildPCIHardwareVersion;
    }

    /**
     * Sets PCI hardware version.
     *
     * @param buildPCIHardwareVersion PCI hardware version.
     */
    public void setBuildPCIHardwareVersion(String buildPCIHardwareVersion) {
        this.buildPCIHardwareVersion = buildPCIHardwareVersion;
    }

    /**
     * Gets POS CPU type.
     *
     * @return POS CPU type.
     */
    public String getPosCPUType() {
        return posCPUType;
    }

    /**
     * Sets POS CPU type.
     *
     * @param posCPUType POS CPU type.
     */
    public void setPosCPUType(String posCPUType) {
        this.posCPUType = posCPUType;
    }

    /**
     * Gets POS board version.
     *
     * @return POS board version.
     */
    public String getPosBoardVersion() {
        return posBoardVersion;
    }

    /**
     * Sets POS board version.
     *
     * @param posBoardVersion POS board version.
     */
    public void setPosBoardVersion(String posBoardVersion) {
        this.posBoardVersion = posBoardVersion;
    }

    /**
     * Gets POS board number.
     *
     * @return POS board number.
     */
    public String getPosBoardNumber() {
        return posBoardNumber;
    }

    /**
     * Sets POS board number.
     *
     * @param posBoardNumber POS board number.
     */
    public void setPosBoardNumber(String posBoardNumber) {
        this.posBoardNumber = posBoardNumber;
    }

    /**
     * Gets RF type.
     *
     * @return RF type.
     */
    public String getRfType() {
        return rfType;
    }

    /**
     * Sets RF type.
     *
     * @param rfType RF type.
     */
    public void setRfType(String rfType) {
        this.rfType = rfType;
    }

    /**
     * Gets RF version.
     *
     * @return RF version.
     */
    public String getRfVersion() {
        return rfVersion;
    }

    /**
     * Sets RF version.
     *
     * @param rfVersion RF version.
     */
    public void setRfVersion(String rfVersion) {
        this.rfVersion = rfVersion;
    }

    /**
     * Gets WIFI driver version.
     *
     * @return WIFI driver version.
     */
    public String getWifiDrvVersion() {
        return wifiDrvVersion;
    }

    /**
     * Sets WIFI driver version.
     *
     * @param wifiDrvVersion WIFI driver version.
     */
    public void setWifiDrvVersion(String wifiDrvVersion) {
        this.wifiDrvVersion = wifiDrvVersion;
    }

//    public String getCustomerID() {
//        return customerID;
//    }
//
//    public void setCustomerID(String customerID) {
//        this.customerID = customerID;
//    }
//
//    public String getLanguage() {
//        return language;
//    }
//
//    public void setLanguage(String language) {
//        this.language = language;
//    }
//
//    public String getPrinterGreyLevel() {
//        return printerGreyLevel;
//    }
//
//    public void setPrinterGreyLevel(String printerGreyLevel) {
//        this.printerGreyLevel = printerGreyLevel;
//    }
//
//    public String getAutoRun() {
//        return autoRun;
//    }
//
//    public void setAutoRun(String autoRun) {
//        this.autoRun = autoRun;
//    }
//
//    public String getAutoSleep() {
//        return autoSleep;
//    }
//
//    public void setAutoSleep(String autoSleep) {
//        this.autoSleep = autoSleep;
//    }
//
//    public String getBrightness() {
//        return brightness;
//    }
//
//    public void setBrightness(String brightness) {
//        this.brightness = brightness;
//    }
//
//    public boolean isBackLightOn() {
//        return backLightOn;
//    }
//
//    public void setBackLightOn(boolean backLightOn) {
//        this.backLightOn = backLightOn;
//    }
//
//    public int getKeyVol() {
//        return keyVol;
//    }
//
//    public void setKeyVol(int keyVol) {
//        this.keyVol = keyVol;
//    }
//
//    public String getBootUpPromptBmp() {
//        return bootUpPromptBmp;
//    }
//
//    public void setBootUpPromptBmp(String bootUpPromptBmp) {
//        this.bootUpPromptBmp = bootUpPromptBmp;
//    }
//
//    public String getShutdownPromptBmp() {
//        return shutdownPromptBmp;
//    }
//
//    public void setShutdownPromptBmp(String shutdownPromptBmp) {
//        this.shutdownPromptBmp = shutdownPromptBmp;
//    }
//
//    public String getShutdownPromptBmpInCharging() {
//        return shutdownPromptBmpInCharging;
//    }
//
//    public void setShutdownPromptBmpInCharging(String shutdownPromptBmpInCharging) {
//        this.shutdownPromptBmpInCharging = shutdownPromptBmpInCharging;
//    }
//
//    public String getShutdownPromptBmpInFullCharging() {
//        return shutdownPromptBmpInFullCharging;
//    }
//
//    public void setShutdownPromptBmpInFullCharging(String shutdownPromptBmpInFullCharging) {
//        this.shutdownPromptBmpInFullCharging = shutdownPromptBmpInFullCharging;
//    }
//
//    public String getShutdownPromptBmpInLowPower() {
//        return shutdownPromptBmpInLowPower;
//    }
//
//    public void setShutdownPromptBmpInLowPower(String shutdownPromptBmpInLowPower) {
//        this.shutdownPromptBmpInLowPower = shutdownPromptBmpInLowPower;
//    }
//
//    public int getPowerMode() {
//        return powerMode;
//    }
//
//    public void setPowerMode(int powerMode) {
//        this.powerMode = powerMode;
//    }
//
//    public String getBatteryStatus() {
//        return batteryStatus;
//    }
//
//    public void setBatteryStatus(String batteryStatus) {
//        this.batteryStatus = batteryStatus;
//    }
//
//    public int getBatteryLevel() {
//        return batteryLevel;
//    }
//
//    public void setBatteryLevel(int batteryLevel) {
//        this.batteryLevel = batteryLevel;
//    }
//
//    public boolean isAutoPowerOff() {
//        return autoPowerOff;
//    }
//
//    public void setAutoPowerOff(boolean autoPowerOff) {
//        this.autoPowerOff = autoPowerOff;
//    }
//
//    public boolean isAutoWakeUp() {
//        return autoWakeUp;
//    }
//
//    public void setAutoWakeUp(boolean autoWakeUp) {
//        this.autoWakeUp = autoWakeUp;
//    }
//
//    public int getPrintLen() {
//        return printLen;
//    }
//
//    public void setPrintLen(int printLen) {
//        this.printLen = printLen;
//    }
//
//    public String getPowerRunTime() {
//        return powerRunTime;
//    }
//
//    public void setPowerRunTime(String powerRunTime) {
//        this.powerRunTime = powerRunTime;
//    }

    public DeviceAttribute getDeviceAttribute() {
        return deviceAttribute;
    }

    public void setDeviceAttribute(DeviceAttribute deviceAttribute) {
        this.deviceAttribute = deviceAttribute;
    }
}
