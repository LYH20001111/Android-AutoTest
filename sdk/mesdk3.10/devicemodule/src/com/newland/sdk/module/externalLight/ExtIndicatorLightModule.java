package com.newland.sdk.module.externalLight;


import com.newland.sdk.module.light.LightColor;
import com.newland.sdk.module.light.LightState;

/**
 * @description: Operate the indicator light
 * @author: Lindan
 */
public interface ExtIndicatorLightModule {

    /**
     * Control indicator light.(non-blocking mode)
     *
     * @param lightColor The type of indicator light {@link LightColor#BLUE}
     * @param lightState The state of indicator light  {@link LightState#TURNON}
     * @param timeout  unit:ms
     * @return true  if success, false if error.
     * @since V3.10.01
     */
    public boolean operateLight(LightColor[] lightColor, LightState lightState, int timeout);

}
