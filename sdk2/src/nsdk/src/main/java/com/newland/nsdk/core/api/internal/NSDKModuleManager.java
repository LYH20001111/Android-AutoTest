package com.newland.nsdk.core.api.internal;

import android.content.Context;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.utils.LogLevel;
import com.newland.nsdk.core.api.common.utils.NativeDebugLevel;

/**
 * Main entrance of NSDK(Newland Software Development Kit). Provides the ability to get all the internal device modules.
 *
 * <p>For internal modules, follow below steps to develop:</p>
 * <pre>
 *     // 1. Get the instance of the module manager.
 *     NSDKModuleManager moduleManager = NSDKModuleManagerImpl.getInstance();
 *     //If you want to obtain the overall debug information, please call the following two interfaces.
 *     moduleManager.setDebugMode(LogLevel.VERBOSE);
 *     moduleManager.setNativeDebugMode(NativeDebugLevel.ALL_ON);
 *
 *     // 2. Initialize device modules.
 *     try {
 *         moduleManager.init(context);
 *     } catch(NSDKException e) {
 *         // Handle the exception.
 *     }
 *
 *    // 3. Get the specified device module.
 *    Beeper beeper = (Beeper)moduleManager.getModule(ModuleType.BEEPER);
 *
 *    // 4. Invoke methods of the device module.
 *    try {
 *        // Beep for 3 seconds with frequency 1000hz.
 *        beeper.beep(1000, 3000);
 *    } catch (NSDKException e) {
 *        // Handle the exception.
 *    }
 *
 *     // 5. Release NSDK resources.
 *     moduleManager.destroy();
 *</pre>
 */
public interface NSDKModuleManager {
    /**
     * Initializes internal device modules.
     *
     * <ul>Note:
     * <li>This shall be called before getting any internal device modules.</li>
     * <li>{@link ErrorCode#NEED_UPDATE} will be thrown if it needs firmware update.</li>
     * </ul>
     *
     * @param context <b>[Required]</b> The context for device modules to use.
     * @throws NSDKException
     */
    void init(Context context) throws NSDKException;

    /**
     * Gets the device module according to the module name.
     *
     * @param moduleName <b>[Required]</b> The name of the device module. See {@link ModuleType}.
     * @return The required device module.
     */
    Module getModule(String moduleName);

    /**
     * Releases device resources.
     */
    void destroy();

    /**
     * Sets debug level.
     * <p>Note: This is called to set the log level of the Java. If you want to obtain more driver information, please call {@link NSDKModuleManager#setNativeDebugMode(NativeDebugLevel)}.</p>
     *
     * @param level <b>[Required]</b> Debug level, see {@link LogLevel}. Default log level is {@link LogLevel#OFF}.
     */
    void setDebugMode(LogLevel level);

    /**
     * Sets whether to open native log.
     * @param isEnable Whether to open native log.
     * @deprecated Replaced by {@link #setNativeDebugMode(NativeDebugLevel)}.
     */
    void enableNativeLog(boolean isEnable);

    /**
     * Sets whether to open native NSDK or Driver log.
     * @param nativeDebugLevel <b>[Required]</b> Native Debug Level, see{@link NativeDebugLevel}.
     */
    void setNativeDebugMode(NativeDebugLevel nativeDebugLevel) throws NSDKException;

    /**
     * Get Error Message by its ErrorCode
     * @param errCode <b>[Required]</b>
     * @return Error Message according to errCode
     */
    String getErrMsg(int errCode);


}