package com.newland.sdk.module.printer;

/**
 * <p>Text format</p>
 * <p>It is used in {@link PrintScriptUtil#addText(TextFormat, String)}</p>
 *
 * @author linsi
 * @since V3.10.01
 */
public class TextFormat {

    private FontSize fontSize = FontSize.NORMAL;
    private Alignment alignment = Alignment.LEFT;
    private boolean linefeed = true;
    private ZhFontSize zhFontSize = ZhFontSize.UN_VALUED;
    private EnFontSize enFontSize = EnFontSize.UN_VALUED;
    private FontScale fontScale = FontScale.ORINARY;
    private boolean underline = false;
    private SpaceScale spaceScale = SpaceScale.NORMAL;
    private boolean strikethrough = false;
    private boolean spaceSizeConversion = true;

    public boolean isSpaceSizeConversion() {
        return spaceSizeConversion;
    }

    /**
     * Whether the space char size is be rewritted.
     * if spaceSizeConversion is false,the size of the space char depends on the font file, the default is true
     * @param
     *
     */
    public void setSpaceSizeConversion(boolean spaceSizeConversion) {
        this.spaceSizeConversion = spaceSizeConversion;
    }

    public boolean isStrikethrough() {
        return strikethrough;
    }

    /**
     * <p>Set whether text is strikethrough.</p>
     *
     * @param strikethrough The default value is false.
     */
    public void setStrikethrough(boolean strikethrough) {
        this.strikethrough = strikethrough;
    }

    /**
     * <p>Set font size</p>
     * <p>The font size of a line print style, the default is {@link FontSize#NORMAL}.</p>
     *
     * @param fontSize Font size
     */
    public void setFontSize(FontSize fontSize) {
        this.fontSize = fontSize;
    }

    /**
     * <p>Set a line alignment</p>
     * <p>The alignment of a line print style, the default is {@link Alignment#LEFT}.</p>
     *
     * @param alignment Alignment
     */
    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

    /**
     * <p>Set line feed</p>
     * <p>Whether print a line of text for newline, the default is false that means no a new line break.</p>
     * <p>The value of the last text should be set to true to wrap the line when there are multiple texts in a line.</p>
     *
     * @param linefeed If feed at line is true, else is false.The default value is true.
     */
    public void setLinefeed(boolean linefeed) {
        this.linefeed = linefeed;
    }

    /**
     * Set chinese font size
     *
     * @param zhFontSize Chinese font size
     */
    public void setZhFontSize(ZhFontSize zhFontSize) {
        this.zhFontSize = zhFontSize;
    }

    /**
     * Set english font size
     *
     * @return English font size
     */
    public void setEnFontSize(EnFontSize enFontSize) {
        this.enFontSize = enFontSize;
    }

    /**
     * Set font fontScale
     *
     * @param fontScale Font fontScale
     */
    public void setFontScale(FontScale fontScale) {
        this.fontScale = fontScale;
    }

    /**
     * <p>Set whether text is underlined.</p>
     *
     * @param underline The default value is false.
     */
    public void setUnderline(boolean underline) {
        this.underline = underline;
    }

    public FontSize getFontSize() {
        return fontSize;
    }

    public Alignment getAlignment() {
        return alignment;
    }

    public boolean isLinefeed() {
        return linefeed;
    }

    public ZhFontSize getZhFontSize() {
        return zhFontSize;
    }

    public EnFontSize getEnFontSize() {
        return enFontSize;
    }

    public FontScale getFontScale() {
        return fontScale;
    }

    public boolean isUnderline() {
        return underline;
    }

    public SpaceScale getSpaceScale() {
        return spaceScale;
    }

    public void setSpaceScale(SpaceScale spaceScale) {
        this.spaceScale = spaceScale;
    }
}
