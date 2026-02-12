package com.newland.sdk.module.printer;

/**
 * <p>Font scale</p>
 * <p>It is used in {@link TextFormat#setFontScale(FontScale)}</p>
 *
 * @author linsi
 * @since V3.10.01
 */
public enum FontScale {
    /**
     * Double magnify horizontally and double magnify vertically.
     */
    DOUBLE_MAGNIFY(0),
    /**
     * Double magnify horizontally and ordinary magnify vertically.
     */
    DOUBLE_HORIZONTAL_MAGNIFY(1),
    /**
     * Normal magnify horizontally and double magnify vertically.
     */
    DOUBLE_VERTICAL_MAGNIFY(2),
    /**
     * Ordinary scale.
     */
    ORINARY(3),
    /**
     * Triple magnify horizontally and triple magnify vertically.
     */
    TRIPLE_MAGNIFY(4),
    /**
     * Triple magnify horizontally and ordinary magnify vertically.
     */
    TRIPLE_HORIZONTAL_MAGNIFY(5),
    /**
     * Ordinary magnify horizontally and triple magnify vertically.
     */
    TRIPLE_VERTICAL__MAGNIFY(6);

    private int scale;

    FontScale(int scale) {
        this.scale = scale;
    }

    public int getScale() {
        return scale;
    }
}
