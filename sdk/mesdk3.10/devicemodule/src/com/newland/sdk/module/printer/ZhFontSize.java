package com.newland.sdk.module.printer;

/**
 * <p>Chinese font size</p>
 * <p>FONT_16x32 is defined as height is 16 pixel, the width is 32 pixel.</p>
 *
 * @author linsi
 * @since V3.10.01
 */
public enum ZhFontSize {

    UN_VALUED(-1),
    FONT_24x24(1),
    FONT_16x32(2),
    FONT_32x32(3),
    FONT_32x16(4),
    FONT_24x32(5),
    FONT_16x16(6),
    FONT_12x16(7),
    FONT_16x8(8),
    FONT_24x24A(9),
    FONT_24x24B(10),
    FONT_24x24C(11),
    FONT_24x24USER(12),
    FONT_12x12A(13),
    FONT_16x24(14),
    FONT_16x16BL(15),
    FONT_24x24BL(16),
    FONT_48x24A(17),
    FONT_48x24B(18),
    FONT_48x24C(19),
    FONT_24x48A(20),
    FONT_24x48B(21),
    FONT_24x48C(22),
    FONT_48x48A(23),
    FONT_48x48B(24),
    FONT_48x48C(25);

    private int size;

    ZhFontSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

}
