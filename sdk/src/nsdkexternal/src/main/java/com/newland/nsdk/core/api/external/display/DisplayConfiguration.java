package com.newland.nsdk.core.api.external.display;

public class DisplayConfiguration {
    /**
     * Whether to clear screen content before displaying following content.
     */
    boolean isClearScreen;
    /**
     * Whether to display text above the picture if they are located in the same area.
     */
    boolean isTextAbove;

    public boolean isClearScreen() {
        return isClearScreen;
    }

    public void setClearScreen(boolean clearScreen) {
        isClearScreen = clearScreen;
    }

    public boolean isTextAbove() {
        return isTextAbove;
    }

    public void setTextAbove(boolean textAbove) {
        isTextAbove = textAbove;
    }
}
