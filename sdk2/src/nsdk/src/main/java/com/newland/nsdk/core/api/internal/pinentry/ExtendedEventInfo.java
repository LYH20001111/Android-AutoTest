package com.newland.nsdk.core.api.internal.pinentry;

public class ExtendedEventInfo {
    ExtendedEvent extendedEvent;
    TouchState touchState;

    public ExtendedEventInfo(ExtendedEvent extendedEvent, TouchState touchState) {
        this.extendedEvent = extendedEvent;
        this.touchState = touchState;
    }

    /**
     * Gets the triggered extended event.
     * @return The triggered extended event. See {@link ExtendedEvent}.
     */
    public ExtendedEvent getExtendedEvent() {
        return extendedEvent;
    }

    /**
     * Sets the triggered extended event.
     * @param extendedEvent  The triggered extended event. See {@link ExtendedEvent}.
     */
    public void setExtendedEvent(ExtendedEvent extendedEvent) {
        this.extendedEvent = extendedEvent;
    }

    /**
     * Gets the state of the touch screen when the extended event triggered.
     * @return The state of the touch screen when the extended event triggered. See {@link TouchState}.
     */
    public TouchState getTouchState() {
        return touchState;
    }

    /**
     * Sets the state of the touch screen when the extended event triggered.
     * @param touchState The state of the touch screen when the extended event triggered. See {@link TouchState}.
     */
    public void setTouchState(TouchState touchState) {
        this.touchState = touchState;
    }
}
