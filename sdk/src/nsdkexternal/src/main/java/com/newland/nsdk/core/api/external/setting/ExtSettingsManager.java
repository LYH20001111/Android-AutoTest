package com.newland.nsdk.core.api.external.setting;

import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.exception.NSDKException;

/**
 * Provides the ability to set/get external device configurations.
 *
 * <p>How to get this module:</p>
 * <pre>
 *     ExtSettingsManager settingsManager = (ExtSettingsManager)NSDKModuleManagerImpl.getInstance().getModule(ModuleType.EXT_SETTING);
 * </pre>
 */
public interface ExtSettingsManager extends Module {

    /**
     * Sets external device configurations.
     *
     * <p>Example:</p>
     * <pre>
     *
     *     try {
     *         settingsManager.set(ExtSettings.SYS_LED_COLOR, "6");
     *     } catch (NSDKException e) {
     *         // Handle the exception
     *     }
     * </pre>
     *
     * @param key   <b>[Required]</b> The key of external device config item. see {@link ExtSettings}.
     * @param value <b>[Required]</b> The value of the config item.
     * @throws NSDKException
     */
    void set(String key, String value) throws NSDKException;

    /**
     * Get external device configurations.
     *
     * <p>Example:</p>
     * <pre>
     *     try {
     *         String hw = settingsManager.get(ExtSettings.RO_POS_HW);
     *     } catch (NSDKException e) {
     *         // Handle the exception
     *     }
     * </pre>
     * @param key <b>[Required]</b> The key of external device config item. see {@link ExtSettings}.
     * @return
     * @throws NSDKException
     */
    String get(String key) throws NSDKException;
}
