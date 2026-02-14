package com.newland.nsdk.core.api.internal.pinentry;

public enum ExtendedEvent {
    /**
     * No extended event.
     */
    NONE,
    /**
     * Swipe left in the touch screen.
     */
    SWIPE_LEFT,
    /**
     * Swipe right in the touch screen.
     */
    SWIPE_RIGHT,
    /**
     * Swipe up in the touch screen.
     */
    SWIPE_UP,
    /**
     * Swipe down in the touch screen..
     */
    SWIPE_DOWN,
    /**
     * Click in the touch screen.
     */
    CLICK,
    /**
     * Double-click in the touch screen.
     */
    DOUBLE_CLICK,
    /**
     * Triple-click in the touch screen.
     */
    TRIPLE_CLICK,
    /**
     * Long-press in the touch screen.
     */
    LONG_PRESS,
    /**
     * Entered PIN length too long.
     */
    TOO_LONG,
    /**
     * Entered PIN length too short.
     */
    TOO_SHORT,
}
