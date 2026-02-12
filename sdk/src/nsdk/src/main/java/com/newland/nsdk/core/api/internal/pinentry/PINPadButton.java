package com.newland.nsdk.core.api.internal.pinentry;

import java.util.Map;

/**
 * PIN pad buttons.
 */
public enum PINPadButton {
    /**
     * Indicates the button to display the first number of number buffer returned by {@link PINEntry#initKeyLayout(Map, boolean)}
     */
    NUMBER_0,
    /**
     * Indicates the button to display the second number of number buffer returned by {@link PINEntry#initKeyLayout(Map, boolean)}
     */
    NUMBER_1,
    /**
     * Indicates the button to display the 3th number of number buffer returned by {@link PINEntry#initKeyLayout(Map, boolean)}
     */
    NUMBER_2,
    /**
     * Indicates the button to display the 4th number of number buffer returned by {@link PINEntry#initKeyLayout(Map, boolean)}
     */
    NUMBER_3,
    /**
     * Indicates the button to display the 5th number of number buffer returned by {@link PINEntry#initKeyLayout(Map, boolean)}
     */
    NUMBER_4,
    /**
     * Indicates the button to display the 6th number of number buffer returned by {@link PINEntry#initKeyLayout(Map, boolean)}
     */
    NUMBER_5,
    /**
     * Indicates the button to display the 7th number of number buffer returned by {@link PINEntry#initKeyLayout(Map, boolean)}
     */
    NUMBER_6,
    /**
     * Indicates the button to display the 8th number of number buffer returned by {@link PINEntry#initKeyLayout(Map, boolean)}
     */
    NUMBER_7,
    /**
     * Indicates the button to display the 9th number of number buffer returned by {@link PINEntry#initKeyLayout(Map, boolean)}
     */
    NUMBER_8,
    /**
     * Indicates the button to display the 10th number of number buffer returned by {@link PINEntry#initKeyLayout(Map, boolean)}
     */
    NUMBER_9,
    /**
     * Indicates the ENTER button.
     */
    ENTER,
    /**
     * Indicates the BACKSPACE button.
     */
    BACKSPACE,
    /**
     * Indicates the CANCEL button.
     * <ul>
     *     <li>If user has entered any password, it will clear the entered password on first click, exit PIN input on second click.</li>
     *     <li>If there is no password entered, it will cancel PIN input on first click.</li>
     * </ul>
     */
    CANCEL,
    /**
     * This button will only clear the entered password.
     */
    CLEAR,
    /**
     * This button will cancel PIN input directly without clearing the entered password.
     */
    QUIT,

    /**
     * This button is used for filling the key layout.
     */
    BLANK1,

    /**
     * This button is used for filling the key layout.
     */
    BLANK2,

    /**
     * This button is used for switching ADA blind keyboard.
     */
    SWITCH,

    /**
     * Indicates the button to display the space character.
     */
    SPACE,
}
