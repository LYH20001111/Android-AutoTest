package com.newland.nsdk.core.api.external.beeper;

import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.external.devicemanager.BeeperTone;

/**
 * <b>[External Module]</b> Beeper.
 *
 * <p>How to get this module:</p>
 * <pre>
 *     ExtBeeper extBeeper = (ExtBeeper)ExtNSDKModuleManagerImpl.getInstance().getModule(ModuleType.EXT_BEEPER);
 * </pre>
 */
public interface ExtBeeper  extends Module {
    /**
     * Beep.
     *
     * <p>Examples:</p>
     * <pre>
     *     try {
     *         // Alert beeping for 3 seconds.
     *         extBeeper.beep(BeeperTone.ALERT, 3000);
     *
     *         // Success beeping for 2 seconds.
     *         extBeeper.beep(BeeperTone.SUCCESS, 2000);
     *     } catch(NSDKException e) {
     *        // Handle the exception according to different exception types.
     *     }
     *
     * </pre>
     *
     * @param tone     <b>[Required]</b> The frequency of beeping. See {@link BeeperTone}
     * @param duration <b>[Required]</b> The period of time to beep. Unit: ms. Value range: [0-99999].
     * @throws NSDKException If error occurs.
     */
    void beep(BeeperTone tone, int duration) throws NSDKException;

    /**
     * Beep with assigned frequency and duration.
     * @param frequency   <b>[Required]</b> The frequency of beeping. Unit: Hz. Value range: [1-4000].
     * @param duration    <b>[Required]</b> The duration tome of beep. Uint: ms. Value range: >0.
     * @throws NSDKException
     */
    void beep(int frequency, int duration) throws NSDKException;
}
