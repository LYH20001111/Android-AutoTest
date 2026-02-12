package com.newland.nsdk.core.api.external.led;

import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.led.LEDColor;
import com.newland.nsdk.core.api.common.led.LEDState;

/**
 * Provides the ability to operate external LED lights.
 *
 * <p>How to get this module:</p>
 * <pre>
 *     ExtLED led = (ExtLED)ExtNSDKModuleManagerImpl.getInstance().getModule(ModuleType.EXT_LED);
 * </pre>
 */
public interface ExtLED extends Module {
    /**
     * Sets the state of specified LED lights.
     *
     * <p>The light will keep the state until next setting.</p>
     *
     * <p>Example:</p>
     * <pre>
     *     try {
     *         // Turn on the green and blue lights.
     *         LEDColor[] lights = new LEDColor[]{LEDColor.GREEN, LEDColor.BLUE};
     *         led.setState(lights, LEDState.ON);
     *
     *         // Turn off the yellow light.
     *         lights = new LEDColor[]{LEDColor.YELLOW};
     *         led.setState(lights, LEDState.OFF);
     *
     *         // Make all the lights blink.
     *         lights = new LEDColor[]{LEDColor.GREEN, LEDColor.BLUE, LEDColor.YELLOW, LEDColor.RED};
     *         led.setState(lights, LEDState.BLINK);
     *     } catch (NSDKException e) {
     *         // Handle the exception.
     *     }
     *
     * </pre>
     *
     * @param ledColor <b>[Required]</b> LED lights to control. See {@link LEDColor}
     * @param ledState <b>[Required]</b> The state of LED lights. See {@link LEDState}
     * @throws NSDKException
     */
    void setState(LEDColor[] ledColor, LEDState ledState) throws NSDKException;
}
