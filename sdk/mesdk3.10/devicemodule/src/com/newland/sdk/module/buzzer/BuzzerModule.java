package com.newland.sdk.module.buzzer;

import com.newland.sdk.mtype.Module;

/**
 * Buzzer Module
 *
 * @since 3.10.01
 */
public interface BuzzerModule extends Module {
    /**
     * Plays the specified sound.
     *
     * @param count times of play.
     * @param time  time per play(millisecond)
     * @param interval interval time(millisecond)
     * @return
     */
    public boolean play(int count,int time,int interval);

    /**
     * Stop playing.
     *
     * @return
     */
    public boolean stop();
}
