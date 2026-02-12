package com.newland.nsdk.core.api.external.display;

/**
 * Parameters for English text display.
 */
public class DisplayTextParameters {
    private int fontColor = -1;
    private FontSize fontSize = null;
    private AlignType alignType = null;

    /**
     * Gets text font color.
     *
     * @return Text font color. Value range: [0x0000-0xFFFF].
     */
    public int getFontColor() {
        return fontColor;
    }
    /**
     * Sets text font color.
     *
     * @param fontColor Text font color. Value range: [0x0000-0xFFFF]. It will not set font color when the value is <0.
     */
    public void setFontColor(int fontColor) {
        this.fontColor = fontColor;
    }

    /**
     * Gets text font size.
     *
     * @return Text font size. See {@link FontSize}.
     */
    public FontSize getFontSize() {
        return fontSize;
    }
    /**
     * Sets text font size.
     *
     * @param fontSize Text font size. See {@link FontSize}. It will not set font size when the value is null.
     */
    public void setFontSize(FontSize fontSize) {
        this.fontSize = fontSize;
    }

    /**
     * Gets the text position in a line.
     *
     * @return Text position in a line. See {@link AlignType}.
     */
    public AlignType getAlignType() {
        return alignType;
    }
    /**
     * Sets the text position in a line.
     *
     * @param alignType Text position in a line. See {@link AlignType}.  It will not set align type when the value is null.
     */
    public void setAlignType(AlignType alignType) {
        this.alignType = alignType;
    }
}
