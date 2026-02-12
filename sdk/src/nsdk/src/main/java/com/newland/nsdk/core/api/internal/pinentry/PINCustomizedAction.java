package com.newland.nsdk.core.api.internal.pinentry;

public enum PINCustomizedAction {
    /**
     * Ignore the event, and it will not trigger any events.
     */
    IGNORE,
    /**
     * Triggered "NULL" event.
     */
    NONE,
    /**
     * Triggered "ESC" event, and exits the PIN entry process.
     */
    ESC,
    /**
     * Triggered "Enter" event, and exits the PIN entry process with all the entered PIN block calculated.
     */
    ENTER,
    /**
     * Triggered "Cancel" event, and cancels the ongoing PIN entry process.
     */
    CANCEL,
    /**
     * Triggered "Backspace" event, and deleted the last entered digit.
     */
    BACKSPACE,
    /**
     * Triggered "Clear" event, and clears all the entered digits.
     */
    CLEAR,
    /**
     * Triggered when selected digits.
     */
    INPUT,

    /**
     * Triggered when selected a number key. Select the key value corresponding to the coordinate when pressed. Select the key value corresponding to the coordinate when released.
     */
    SELECT
}
