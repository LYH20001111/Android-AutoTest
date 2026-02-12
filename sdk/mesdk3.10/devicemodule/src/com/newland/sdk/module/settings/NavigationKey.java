package com.newland.sdk.module.settings;

/**
 * <p>Navigation Value</p>
 *
 * @author linsi
 */
public enum NavigationKey {
    /**
     * <p>Reboot can be valid globally.</p>
     */
    HOME("Home key to return to the home screen.");
    /**
     * <p>It has been valid since firmware version V1.1.09</p>
     */
//    RECENTS_KEY("Recents key to display the recent tasks screen.");

    private String description;

    NavigationKey(String description) {
        this.description = description;
    }

    public String toString() {
        return description;
    }
}
