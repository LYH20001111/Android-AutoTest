package com.newland.nsdk.core.api.internal.setting;

import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.exception.NSDKException;

/**
 * Provides the ability to set/get configurations.
 *
 * <p>How to get this module:</p>
 * <pre>
 *     SettingsManager settingsManager = (SettingsManager)NSDKModuleManagerImpl.getInstance().getModule(ModuleType.SETTINGS);
 * </pre>
 */
public interface SettingsManager extends Module {

    /**
     * Sets device config.
     *
     * <p>Example:</p>
     * <pre>
     *     try {
     *         settingsManager.set(Settings.SCREEN_OFF_TIMEOUT, "50");
     *     } catch (NSDKException e) {
     *         // Handle the exception
     *     }
     * </pre>
     *
     * @param settingName <b>[Required]</b> The key of device config item. see {@link Settings}.
     * @param value       <b>[Optional]</b> The value of the config item.
     * @throws NSDKException
     */
    void set(String settingName, String value) throws NSDKException;

    /**
     * Gets device config.
     *
     * @param settingName <b>[Required]</b> The key of device config item. see {@link Settings}.
     * @return The config item value .
     * @throws NSDKException
     */
    String get(String settingName) throws NSDKException;
}
