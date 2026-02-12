package com.newland.nsdk.core.api.internal.ethernetmanager;

import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.exception.NSDKException;

/**
 * Provides ethernet related operations.
 * <p>Note: For the devices whose Android platform version is higher than A10(29), this module is not supported yet. You can call the native interfaces of Android to implement the function.</p>
 * <p>How to get this module:</p>
 * <pre>
 *     EthernetManager ethernetManager = (EthernetManager)NSDKModuleManagerImpl.getInstance().getModule(ModuleType.ETHERNET_MANAGER);
 * </pre>
 */
public interface EthernetManager extends Module {
    /**
     * Enables ethernet.
     *
     * <p>Note: If ethernet is enabled, OTG is enabled too.</p>
     *
     * @throws NSDKException
     */
    void enable() throws NSDKException;

    /**
     * Disables ethernet.
     *
     * <p>Note: OTG will not be disabled with ethernet. If you need to disable OTG after ethernet is disabled, you need to set '0' to '/sys/class/usb_ctrl/otg_mode'.</p>
     *
     * @throws NSDKException
     */
    void disable() throws NSDKException;

    /**
     * Gets ethernet status.
     *
     * @return Ethernet status. See {@link EthernetStatus}.
     * @throws NSDKException
     */
    EthernetStatus getStatus() throws NSDKException;

    /**
     * Gets ethernet config.
     *
     * @return Ethernet config(DHCP/STATIC).
     * @throws NSDKException
     */
    String getConfig() throws NSDKException;
}
