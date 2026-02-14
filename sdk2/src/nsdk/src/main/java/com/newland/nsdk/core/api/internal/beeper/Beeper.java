package com.newland.nsdk.core.api.internal.beeper;

import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.exception.NSDKException;

/**
 * Beeper.
 *
 * <p>How to get this module:</p>
 * <pre>
 *     Beeper beeper = (Beeper)NSDKModuleManagerImpl.getInstance().getModule(ModuleType.BEEPER);
 * </pre>
 */
public interface Beeper extends Module {
    /**
     * Beeps.
     *
     * <p>Examples:</p>
     * <pre>
     *     try {
     *         // Beep for 3 seconds with frequency 1000hz.
     *         beeper.beep(1000, 3000);
     *
     *         // Beep for 2 seconds with frequency 100hz.
     *         beeper.beep(100, 2000);
     *     } catch(NSDKException e) {
     *         // Handle the exception
     *     }
     *
     * </pre>
     *
     * @param frequency <b>[Required]</b> Beeping frequency, unit：Hz, value range: (0-4000].
     * @param duration  <b>[Required]</b> The period of time to beep. Unit: ms. Shall be >0.
     * @throws NSDKException
     */
    void beep(int frequency, int duration) throws NSDKException;

    /**
     * Sets the volume of the beeper.
     * @param volume  <b>[Required]</b> The volume to be set, value range: [1,5]
     * @throws NSDKException
     */
    void setVolume(int volume) throws NSDKException;
}
