package com.newland.sdk.module.printer;

/**
 * @author youjf
 * @description
 * @date 2020/6/30
 * @since V3.10.22
 */
public enum SpaceScale {
    /**
     *normal,used default
     */
    NORMAL(0),
    /**
     * zoom with fontscal{@link FontScale}
     */
    ZOOM(1);
    private int spaceScale;

    SpaceScale(int spaceScale) {
        this.spaceScale = spaceScale;
    }

    public int getSpaceScale() {
        return spaceScale;
    }
}
