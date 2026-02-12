package com.newland.nsdk.core.api.internal.pinentry;

public enum TouchState {
    /**
     * No touch event.
     */
    TOUCH_NONE,
    /**
     * Current focus is above the key pad(Key pad below).
     */
    TOUCH_ABOVE_KEYPAD,
    /**
     * Current focus is below the key pad(Key pad above).
     */
    TOUCH_BELOW_KEYPAD,
    /**
     * Current focus is in the left of the key pad(Key pad right).
     */
    TOUCH_LEFT_OF_KEYPAD,
    /**
     * Current focus is in the right of the key pad(Key pad left).
     */
    TOUCH_RIGHT_OF_KEYPAD,
    /**
     * Current focus is in the digit key.
     */
    TOUCH_ON_DIGIT_KEY,
    /**
     * Current focus is in the enter function key.
     */
    TOUCH_ON_ENTER_KEY,
    /**
     * Current focus is in the backspace function key.
     */
    TOUCH_ON_BACKSPACE_KEY,
    /**
     * Current focus is in the cancel function key.
     */
    TOUCH_ON_CANCEL_KEY,

    /**
     * Current focus is in the clear function key.
     */
    TOUCH_ON_CLEAR_KEY,

    /**
     * Current focus is in the space function key.
     */
    TOUCH_ON_SPACE_KEY,

    /**
     * Current focus is in the switch function key.
     */
    TOUCH_ON_SWITCH_KEY,
}
