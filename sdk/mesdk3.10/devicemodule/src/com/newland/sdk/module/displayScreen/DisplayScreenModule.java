package com.newland.sdk.module.displayScreen;

import android.support.annotation.IntRange;

/**
 * @description: Operate on N550 customer display
 * @author:  Suyuming
 * @create:  2019/7/28
 */
public interface DisplayScreenModule {

    /**
     * Set the digital led brightness of device<p>
     * @param brightness range:0-7<p>
     * @return
     * @since 3.10.01
     */
    boolean setBrightness(@IntRange(from = 0, to = 7) int brightness);

    /**
     * Set the device digital led content<p>
     * @param message Support 6 Numbers and 1 decimal point, maximum support 2 decimal places.<p>
     * @return
     * @since 3.10.01
     */
    boolean showMessage(String message);

    /**
     * Turn off the device digital led<p>
     * @return  true,Success;false,fail;
     * @since 3.10.01
     */
    boolean turnOffLed();
}
