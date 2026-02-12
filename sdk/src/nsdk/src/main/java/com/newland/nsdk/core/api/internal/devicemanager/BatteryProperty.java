package com.newland.nsdk.core.api.internal.devicemanager;

/**
 * The battery properties returned by {@link DeviceManager#getBatteryProperty()}.
 */
public class BatteryProperty {
    public BatteryProperty(double temperature, double adapterVoltage, double chargeCurrent,
        boolean isSupportGetBatteryTemperature, boolean isSupportGetChargeCurrent) {
        this.temperature = temperature;
        this.adapterVoltage = adapterVoltage;
        this.chargeCurrent = chargeCurrent;
        this.isSupportGetBatteryTemperature= isSupportGetBatteryTemperature;
        this.isSupportGetChargeCurrent= isSupportGetChargeCurrent;
    }

    /**
     * The temperature of the battery, unit: ℃
     */
    double temperature;
    /**
     * The voltage of the battery adapter, unit: V
     */
    double adapterVoltage;
    /**
     * The charge current of the device, unit: A
     */
    double chargeCurrent;
    /**
     * Whether is supported to get the battery temperature.
     */
    boolean isSupportGetBatteryTemperature;
    /**
     * Whether is supported to get the charge current.
     */
    boolean isSupportGetChargeCurrent;

    /**
     * Gets the battery temperature. Unit: ℃
     * @return The battery temperature. Unit: ℃
     */
    public double getTemperature() {
        return this.temperature;
    }

    /**
     * Gets the adapter voltage. Unit: V
     * @return The adapter voltage. Unit: V
     */
    public double getAdapterVoltage() {
        return this.adapterVoltage;
    }

    /**
     * Gets the charge current. Unit: A
     * @return The charge current. Unit: A
     */
    public double getChargeCurrent() {
        return this.chargeCurrent;
    }

    /**
     * Gets whether the current device supports to get battery temperature.
     * @return Whether the current device supports to get battery temperature.
     */
    public boolean isSupportGetBatteryTemperature() {
        return this.isSupportGetBatteryTemperature;
    }

    /**
     * Gets whether the current device supports to get charge current.
     * @return Whether the current device supports to get charge current.
     */
    public boolean isSupportGetChargeCurrent() {
        return this.isSupportGetChargeCurrent;
    }
}
