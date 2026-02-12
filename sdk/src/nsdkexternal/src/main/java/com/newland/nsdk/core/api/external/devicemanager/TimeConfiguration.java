package com.newland.nsdk.core.api.external.devicemanager;

public class TimeConfiguration {
    private int autoBacklightOffTime;
    private int autoSleepTime;
    private int autoTurnOffTime;

    /**
     * Gets auto backlight-off time.
     *
     * @return Auto backlight-off time. Unit: s, >=10.
     */
    public int getAutoBacklightOffTime() {
        return autoBacklightOffTime;
    }

    /**
     * Sets auto backlight-off time.
     *
     * @param autoBacklightOffTime Auto backlight-off time. Unit: s, >=10. If set it to 0, means using former configuration.
     */
    public void setAutoBacklightOffTime(int autoBacklightOffTime) {
        this.autoBacklightOffTime = autoBacklightOffTime;
    }

    /**
     * Gets auto sleep time.
     *
     * @return Auto sleep time. Unit: s, >=60.
     */
    public int getAutoSleepTime() {
        return autoSleepTime;
    }

    /**
     * Sets auto sleep time.
     *
     * @param autoSleepTime Auto sleep time. Unit: s, >=60. If set it to 0, means using former configuration.
     */
    public void setAutoSleepTime(int autoSleepTime) {
        this.autoSleepTime = autoSleepTime;
    }

    /**
     * Gets auto turn-off time.
     *
     * @return Auto turn-off time. Unit: s, >=1.
     */
    public int getAutoTurnOffTime() {
        return autoTurnOffTime;
    }

    /**
     * Sets auto turn-off time.
     *
     * @param autoTurnOffTime Auto turn-off time. Unit: s, >=1. If set it to 0, means using former configuration.
     */
    public void setAutoTurnOffTime(int autoTurnOffTime) {
        this.autoTurnOffTime = autoTurnOffTime;
    }
}
