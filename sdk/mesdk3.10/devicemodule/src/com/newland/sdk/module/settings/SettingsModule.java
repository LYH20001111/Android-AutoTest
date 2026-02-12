package com.newland.sdk.module.settings;

import android.support.annotation.IntRange;

import java.security.cert.X509Certificate;

/**
 * System Settings Module
 *
 * @author linsi
 */
public interface SettingsModule {
    /**
     * <p>Set Screen Brightness.</p>
     *
     * @param value The degree of bright
     * @return
     */
    boolean setScreenBrightness(@IntRange(from = 0, to = 255) int value);

    /**
     * <p>whether set Settings item visible or not.</p>
     *
     * @param settingsItems Settings items{@link SettingsItems}
     * @param isVisible
     */
    void setSettingsItemsVisible(SettingsItems[] settingsItems, boolean isVisible);

    /**
     * <p>whether set Status Bar battery percent visible or not.</p>
     *
     * @param isVisible
     */
    void setBatteryPercentVisible(boolean isVisible);

    /**
     * <p>whether set Status Bar battery percent drop down or not.</p>
     *
     * @param isDrop true, can drop it down, otherwise false can't.
     * @return true means success, false means fail.
     */
    boolean setStatusBarDropDown(boolean isDrop);

    /**
     * <p>Set Navigation key valid.</p>
     *
     * @param navigationKey {@link NavigationKey} three-Key navigation.
     * @param isValid
     * @return true means success, false means fail.
     */
    boolean setNavigationKeyValid(NavigationKey navigationKey, boolean isValid);

    /**
     * <p>Get a APN util.</p>
     *
     * @return
     */
    ApnUtil getApnUtil();

    /**
     * <p>Set Mobile Data Available.</p>
     *
     * @param isValid
     */
    void setMobileDataValid(boolean isValid);

    /**
     * <p>Get Mobile Data status.</p>
     *
     * @return
     */
    boolean getMobileDataStatus();

    /**
     * <p>Get certificate information</p>
     *
     * @return X509Certificate
     */
    X509Certificate getCertificateInfo();

    /**
     * <p>Get the information of the current device.</p>
     *
     * @param infoItem
     * @return
     */
    String getInfo(InfoItem infoItem);
}
