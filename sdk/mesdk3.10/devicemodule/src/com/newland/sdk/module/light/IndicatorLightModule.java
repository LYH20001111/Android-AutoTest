package com.newland.sdk.module.light;


import com.newland.sdk.mtype.Module;

/**
 * @description: Operate the indicator light
 * @author: Lindan
 * @create: 2019/7/26
 */
public interface IndicatorLightModule extends Module {

    /**
     * Control indicator light.(non-blocking mode)
     *
     * @param lightColor The type of indicator light {@link LightColor#BLUE}
     * @param lightState The state of indicator light  {@link LightState#TURNON}
     * @return true  if success, false if error.
     * @since V3.10.01
     */
    public boolean operateLight(LightColor[] lightColor, LightState lightState);

    /**
     * Control indicator light flashing.(blocking mode)
     *
     * @param lightColor   The type of indicator light {@link LightColor#BLUE}
     * @param count        The count of blink
     * @param timeInterval Time interval(units:ms), The total time is not more than 3 seconds.
     * @return true  if success, false if error.
     * @since V3.10.01
     */
    public boolean blinkLight(LightColor[] lightColor, int count, int timeInterval);
}
