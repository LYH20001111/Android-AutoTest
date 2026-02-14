package com.newland.nsdk.core.api.internal.devicemanager;

/**
 * This is used to adjust the gain of the U2000 radar, the smaller code has the farther detection distance.
 */
public enum RadarGain {
    RADAR_GAIN_0x0B((byte) 0x0B),
    RADAR_GAIN_0x1B((byte) 0x1B),
    RADAR_GAIN_0x2B((byte) 0x2B),
    RADAR_GAIN_0x3B((byte) 0x3B),
    RADAR_GAIN_0x4B((byte) 0x4B),
    RADAR_GAIN_0x5B((byte) 0x5B),
    RADAR_GAIN_0x6B((byte) 0x6B),
    RADAR_GAIN_0x7B((byte) 0x7B),
    RADAR_GAIN_0x8B((byte) 0x8B),
    RADAR_GAIN_0x9B((byte) 0x9B),
    RADAR_GAIN_0xAB((byte) 0xAB),
    RADAR_GAIN_0xBB((byte) 0xBB),
    RADAR_GAIN_0xCB((byte) 0xCB);


    byte code;
    RadarGain(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }
}
