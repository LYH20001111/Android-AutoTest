package com.newland.sdk.module.externalbuzzer;

import com.newland.sdk.module.externalPin.PinpadInitExtParams;

/**
 * @author youjf
 * @description
 * @date 2020/6/10
 * @since V3.10.20
 */
public interface ExtBuzzerModule {
    /**
     * init external pinpad
     * @param params {@link PinpadInitExtParams}
     * @return
     */
    public boolean init(PinpadInitExtParams params);

    /**
     * Plays the specified sound.
     *
     * @param buzzerTone 0-default;1-Alert (750Hz);2-sucess(1500Hz)
     * @param time  time per play(millisecond)
     * @return
     */
    public boolean play(int buzzerTone, int time);
}
