package com.newland.nsdk.core.api.external.futurex;

import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.keymanager.InstalledKeyInfo;

import java.util.List;

/**
 * Provides the ability to execute FutureX RKL process.
 *
 * <p>How to get this module:</p>
 * <pre>
 *     ExtFutureX futureX = (ExtFutureX)ExtNSDKModuleManagerImpl.getInstance().getModule(ModuleType.EXT_FUTUREX);
 * </pre>
 */
public interface ExtFutureX extends Module {
    /**
     * Gets PEDI request data from device.
     *
     * @return PEDI request data to be sent to host.
     * @throws NSDKException
     */
    byte[] getPEDI() throws NSDKException;

    /**
     * Sets PEDI response data to device.
     *
     * @param data PEDI response data received from host.
     * @throws NSDKException
     */
    void setPEDI(byte[] data) throws NSDKException;

    /**
     * Gets PEDK request data from device.
     *
     * @return PEDK request data to be sent to host.
     * @throws NSDKException
     */
    byte[] getPEDK() throws NSDKException;

    /**
     * Sets PEDK response data to device.
     *
     * @param data PEDK response data received from host.
     * @throws NSDKException
     */
    void setPEDK(byte[] data) throws NSDKException;

    /**
     * Gets PEDV request data from device.
     *
     * @return PEDV request data to be sent to host.
     * @throws NSDKException
     */
    byte[] getPEDV() throws NSDKException;

    /**
     * Sets PEDV response data to device.
     *
     * @param data PEDV response data received from host.
     * @throws NSDKException
     */
    void setPEDV(byte[] data) throws NSDKException;

    /**
     * Gets the number of installed keys.
     *
     * @return How many keys installed for the last time RKL process.
     * @throws NSDKException
     */
    int getInstalledKeyNum() throws NSDKException;

    /**
     * Gets detail info of installed keys.
     *
     * @return Detail info of installed keys for the last RKL process. See {@link InstalledKeyInfo}.
     * @throws NSDKException
     */
    List<InstalledKeyInfo> getInstalledKeyInfo() throws NSDKException;

    /**
     * Sets index of device sign cert.
     *
     * @param index Index of device sign cert.
     * @throws NSDKException
     */
    void setDeviceSignCertIndex(byte index) throws NSDKException;

    /**
     * Sets device group.
     *
     * @param name Device group name.
     * @throws NSDKException
     */
    void setDeviceGroup(String name) throws NSDKException;
}
