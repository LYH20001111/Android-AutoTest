package com.newland.nsdk.core.api.internal.led;

import com.newland.nsdk.core.api.common.led.LEDColor;
import com.newland.nsdk.core.api.common.led.LEDState;

/**
 * The class for setting led configurations of the contactless detection field on P300 devices.
 */
public class LEDLight {
    private Integer number;
    private LEDColor color;
    private LEDState state;

    public LEDLight(){}
    public LEDLight(int number, LEDColor color, LEDState state){
        this.number = number;
        this.color = color;
        this.state = state;
    }

    public LEDLight(int number) {
        this.number = number;
    }

    public LEDLight(LEDColor color) {
        this.color = color;
    }

    /**
     * Gets the index of the LED to be set.
     * @return The index of the LED to be set.
     */
    public Integer getNumber() {
        return number;
    }

    /**
     * Sets the index of the LED to be set.
     * @param number The index of the LED to be set.
     */
    public void setNumber(Integer number) {
        this.number = number;
    }

    /**
     * Gets the color of the LED to be set.
     * @return The color of the LED to be set. See {@link LEDColor}.
     */
    public LEDColor getColor() {
        return color;
    }

    /**
     * Sets the color of the LED to be set.
     * @param color The color of the LED to be set. See {@link LEDColor}.
     */
    public void setColor(LEDColor color) {
        this.color = color;
    }

    /**
     * Gets the state of the LED to be set.
     * @return The state of the LED to be set. See {@link LEDState}.
     */
    public LEDState getState() {
        return state;
    }

    /**
     * Sets the state of the LED to be set.
     * @param state The state of the LED to be set. See {@link LEDState}.
     */
    public void setState(LEDState state) {
        this.state = state;
    }
}
