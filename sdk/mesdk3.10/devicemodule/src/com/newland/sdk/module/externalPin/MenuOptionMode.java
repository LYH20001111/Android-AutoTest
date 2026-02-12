package com.newland.sdk.module.externalPin;

public enum MenuOptionMode {
    /**
     * Preselection Mode
     * Overide
     */
    DEFAULT_MODE(0),
    /**
     * Cancel Preselection Mode;
     * after pressing the number keys, do not highlight or redraw, immediately return,
     * only effective for the options displayed on the current interface,
     * for example: currently displaying options 1-5, pressing the number keys 1-5 will directly return,
     * >5 will display according to the original Preselection mode
     */
    CANCEL_MODE(1);

    private int mode;

    private MenuOptionMode(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }
}
