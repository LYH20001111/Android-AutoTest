package com.newland.nsdk.core.api.external.display;

/**
 * Font size.
 */
public enum FontSize {
    /**
     * Normal font size to display on the screen.
     */
    NORMAL(1),
    /**
     * Small font size to display on the screen.
     */
    SMALL(2),
    /**
     * Large font size to display on the screen.
     */
    LARGE(3);
    private int code;
    FontSize(int code){
        this.code = code;
    }
    public int getCode(){
        return this.code;
    }
}
