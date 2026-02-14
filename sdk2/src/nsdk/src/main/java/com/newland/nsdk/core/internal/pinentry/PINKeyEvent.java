package com.newland.nsdk.core.internal.pinentry;

/**
 * PIN key event.
 */
public enum PINKeyEvent {
    /**
     * PIN Key is pressed.
     */
    PIN,
    /**
     * Backspace key is pressed.
     */
    BACKSPACE,
    /**
     * Clear key is pressed.
     */
    CLEAR,
    /**
     * Enter key is pressed.
     */
    ENTER,
    /**
     * ESC key is pressed.
     */
    ESC,
    /**
     * No key events occurred.
     */
    NULL,
    TOO_SHORT,
    TOO_LONG,
    ADA_ON,
    ADA_OFF,
    SLID_LEFT,
    /**
     * Slid to the right of the keypad area.
     */
    SLID_RIGHT,
    /**
     * Slid to the number key.
     */
    /**
     * Slid above the keypad area.
     */
    SLID_UP,
    /**
     * Slid below the keypad area.
     */
    SLID_DOWN,
    /**
     * Slid to the left of the keypad area.
     */
    SLID_NUMKEY,
    /**
     * Slid to the "ENTER" function key.
     */
    SLID_ENTER,
    /**
     * Slid to the "CANCEL" function key.
     */
    SLID_CANCEL,
    /**
     * Slid to the "BACKSPACE" function key.
     */
    SLID_BACKSPACE,
    /**
     * Slid to no digit area within the keyboard, this is used for X800 device.
     */
    SLID_NO_DIGIT,
}
