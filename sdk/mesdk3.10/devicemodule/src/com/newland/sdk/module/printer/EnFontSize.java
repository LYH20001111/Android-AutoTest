package com.newland.sdk.module.printer;

/**
 * <p>English font size</p>
 * <p>FONT_8x16 is defined as height is 8 pixel, the width is 16 pixel.</p>
 *
 * @author linsi
 * @since V3.10.01
 */
public enum EnFontSize {

    UN_VALUED(-1),
    FONT_8x16(1),
    FONT_16x16(2),
    FONT_16x32(3),
    FONT_24x32(4),
    FONT_6x8(5),
    FONT_8x8(6),
    FONT_5x7(7),
    FONT_5x16(8),
    FONT_10x16(9),
    FONT_10x8(10),
    FONT_12x16A(11),
    FONT_12x24A(12),
    FONT_16x32A(13),
    FONT_12x16B(14),
    FONT_12x24B(15),
    FONT_16x32B(16),
    FONT_12x16C(17),
    FONT_12x24C(18),
    FONT_16x32C(19),
    FONT_24x24A(20),
    FONT_32x32A(21),
    FONT_24x24B(22),
    FONT_32x32B(23),
    FONT_24x24C(24),
    FONT_32x32C(25),
    FONT_12x12(26),
    FONT_12x12A(27),
    FONT_12x12B(28),
    FONT_12x12C(29),
    FONT_8x12(30),
    FONT_8x24(31),
    FONT_8x32(32),
    FONT_12x32A(33),
    FONT_12x32B(34),
    FONT_12x32C(35),
    FONT_8x16BL(36),
    FONT_16x16BL(37),
    FONT_12x24BL(38),
    FONT_14x14(39),
    FONT_14x16(40),
    FONT_14x18(41),
    FONT_14x20(42),
    FONT_14x24(43),
    FONT_14x28(44);

    private int size;

    EnFontSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

}
