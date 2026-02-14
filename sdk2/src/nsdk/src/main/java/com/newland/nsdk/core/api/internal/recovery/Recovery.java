package com.newland.nsdk.core.api.internal.recovery;

import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.exception.NSDKException;

public interface Recovery extends Module {
    /**
     * Keep the specified APP on the device, uninstall all other non-system apps, and delete all application data.
     * @param pkgNames <b>[Required]</b> The applications' package name which will be kept through recovery.
     * @throws NSDKException
     */
    void keepApps(String[] pkgNames) throws NSDKException;

    /**
     * Keep the specified APP in the device, uninstall all other non-system apps, and keep the application data.
     * @param pkgNames   <b>[Required]</b> The applications' package name which will be kept through recovery.
     * @param dataPaths  <b>[Required]</b> The applications' data path which will be kept through recovery. The data path shall be used in absolute path and shall start with:
     *                   <li>/data/share</li>
     *                   <li>/mnt/sdcard/</li>
     *                   <li>/mnt/shell/emulated/0/</li>
     * @throws NSDKException
     */
    void keepApps(String[] pkgNames, String[] dataPaths) throws NSDKException;
}
