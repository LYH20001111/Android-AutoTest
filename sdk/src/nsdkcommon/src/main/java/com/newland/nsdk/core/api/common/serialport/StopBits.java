package com.newland.nsdk.core.api.common.serialport;

/**
 * Stop bits sent at the end of every character allow the receiver to detect the end of a character and to resynchronise with the character stream.
 */
public enum StopBits {
    /**
     * 1 stop bit
     */
    STOP_BIT_ONE,

    /**
     * 2 stop bits
     */
    STOP_BIT_TWO
}
