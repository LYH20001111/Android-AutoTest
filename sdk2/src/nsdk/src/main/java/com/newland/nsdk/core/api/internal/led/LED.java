package com.newland.nsdk.core.api.internal.led;


import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.led.LEDColor;
import com.newland.nsdk.core.api.common.led.LEDState;


/**
 * Provides the ability to operate the LED lights.
 *
 * <p>How to get this module:</p>
 * <pre>
 *     LED led = (LED)NSDKModuleManagerImpl.getInstance().getModule(ModuleType.LED);
 * </pre>
 */
public interface LED extends Module {

    /**
     * Sets the led background display parameters.
     * <p>
     *      <ul>This method support to set the following function:
     *      <li>Sets the background display location with {@link DisplayParameters#x} and {@link DisplayParameters#y}, if it is not be set, the module will use the latest effective value.</li>
     *      <li>Sets the background display orientation with {@link DisplayParameters#isHorizontal} whose default value is true.</li>
     *      <li>Sets whether the background display with the led blinks by setting {@link DisplayParameters#isBackgroundAlwaysDisplayed} whose default value is false.</li>
     *      </ul>
     * </p>
     * <p>The following is the calling examples: </p>
     * <pre>
     *     LED mLED = (LED)nsdkModuleManager.getModule(ModuleType.LED);
     *     DisplayParameters parameters = new DisplayParameters();
     *     //To set the background coordination
     *     parameters.setX(100);
     *     parameters.setY(200);
     *     try {
     *         mLED.setDisplayParameters(parameters);
     *     } catch (NSDKException e) {
     *         e.printStackTrace();
     *     }
     *     //To set whether to display background horizontally.
     *     parameters.setHorizontal(true);
     *     try {
     *          mLED.setDisplayParameters(parameters);
     *     } catch (NSDKException e) {
     *         e.printStackTrace();
     *     }
     *     //To set whether to always display background.
     *     parameters.setBackgroundAlwaysDisplayed(true);
     *      try {
     *           mLED.setDisplayParameters(parameters);
     *      } catch (NSDKException e) {
     *          e.printStackTrace();
     *      }
     * </pre>
     *
     * @param parameters  <b>[Required]</b> The led background related display parameters.
     *                    <ul>
     *                      <li>x: The background displayed horizontal location, default value is -1, means not to change the x coordination.</li>
     *                      <li>y: The background displayed vertical location, default value is -1, means not to change the y coordination.</li>
     *                      <li>isHorizontal: Whether to display horizontally, default value is true.</li>
     *                      <li>isBackgroundAlwaysDisplayed: Whether the background display with the led blinks.</li>
     *                    </ul>
     * @throws NSDKException
     */
    void setDisplayParameters(DisplayParameters parameters) throws NSDKException;

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

    /**
     * Sets the state of specified LED lights.
     * <p>Supported devices:</p>
     * <ul>
     *     <li>P300</li>
     * </ul>
     * @param lights <b>[Required]</b> LED lights to control. See {@link LEDLight}
     * @throws NSDKException
     */
    void setState(LEDLight[] lights) throws NSDKException;

    /**
     * Makes LED lights blink.
     *
     * <p>The light will be turned off after blinking finished.</p>
     *
     * <p>Example:</p>
     * <pre>
     *     // Make the green and blue lights blink for 3 times every 1 second.
     *     LEDColor[] lights = new LEDColor[]{LEDColor.GREEN, LEDColor.BLUE};
     *     int count = 3;
     *     int timeInterval = 1000;
     *     try {
     *         led.blink(lights, count, timeInterval);
     *     } catch (NSDKException e) {
     *         // Handle the exception.
     *     }
     *
     * </pre>
     *
     * @param ledColor     <b>[Required]</b> LED lights to control. See {@link LEDColor}
     * @param count        <b>[Required]</b> How many times to blink the lights, shall be >0.
     * @param timeInterval <b>[Required]</b> Time interval(units: ms), shall be >0. If the lights of the device is analogue, this shall be <= 12700.
     * @throws NSDKException
     */
    void blink(LEDColor[] ledColor, int count, int timeInterval) throws NSDKException;

    /**
     * Makes LED lights blink.
     * <p>Note: This method can support different blink on and off duration. And it is block when calling this method.</p>
     * <p>The following is the calling example:</p>
     * <pre>
     *      LED mLED = (LED)nsdkModuleManager.getModule(ModuleType.LED);
     *      LEDLight ledLight1 = new LEDLight(LEDColor.BLUE);
     *      LEDLight ledLight2 = new LEDLight(LEDColor.RED);
     *      int onDuration = 500;
     *      int offDuration = 2000;
     *      //Situation 1:count <= 0, continuously blink, and shall be stopped by application.
     *      try {
     *          mLED.blink(new LEDLight[] {ledLight1, ledLight2}, 0, onDuration, offDuration);
     *      } catch (NSDKException e) {
     *          e.printStackTrace();
     *      }
     *      //To stop led blinks.
     *      try {
     *          mLED.set(LEDColor[] {LEDColor.RED, LEDColor.BLUE}, LEDState.OFF);
     *      } catch (NSDKException e) {
     *          e.printStackTrace();
     *      }
     *
     *      //Situation 2:count > 0, the led will be stopped when the blink count ends.
     *      try {
     *          mLED.blink(new LEDLight[] {ledLight1, ledLight2}, 5, onDuration, offDuration);
     *      } catch (NSDKException e) {
     *          e.printStackTrace();
     *      }
     *
     * </pre>
     * @param ledLights    <b>[Required]</b> The led lights configuration. The lights position and color shall be linear relationship as below.
     *                     <ul>
     *                        <li>1->Blue</li>
     *                        <li>2->Yellow</li>
     *                        <li>3->Green</li>
     *                        <li>4->Red</li>
     *                     </ul>
     * @param count        <b>[Required]</b> The blink counts, if <=0 means blink continuously, which shall be stop be {@link LED#setState(LEDColor[], LEDState)}.
     * @param onDuration   <b>[Required]</b> The lights on duration time, unit: 100ms, value range:(0, 127]
     * @param offDuration  <b>[Required]</b> The lights off duration time, unit: 100ms, value range:(0, 127]
     * @throws NSDKException
     */
    void blink(LEDLight[] ledLights, int count, int onDuration, int offDuration) throws NSDKException;
}
