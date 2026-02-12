package com.newland.sdk.module.externalPin;

/**
 * @author linsi
 * @date 2025/02/28
 */
public class MenuOptionParams {

    private MenuOptionMode mode = MenuOptionMode.DEFAULT_MODE;

    public MenuOptionMode getMode() {
        return mode;
    }

    /**
     * {@link MenuOptionMode}
     * @param mode
     */
    public void setMode(MenuOptionMode mode) {
        this.mode = mode;
    }
}
