package com.newland.sdk.module.settings;

/**
 * <p>Setting option</p>
 *
 * @author linsi
 */
public enum SettingsItems {
    STORAGE("Storage"),
    BATTERY("Battery"),
    HOME("Home"),
    BACKUP_RESET("Backup & reset"),
//    APPS("Applications"),
    DATA_USAGE("Data usage"),
    ACCESSIBILITY("Accessibility"),
    DEVELOPER_OPTIONS("Developer options"),
    LOCATION("Location"),
    SECURITY("Security"),
    PRINT("Print"),
    VPN("Vpn"),
    /**
     * <p>Set Screen lock options include support and display.</p>
     * <p>Display with support And blank with nonsupport.</p>
     */
    SCREEN_LOCK("Screen lock");

    private String description;

    SettingsItems(String description) {
        this.description = description;
    }

    public String toString() {
        return description;
    }
}
