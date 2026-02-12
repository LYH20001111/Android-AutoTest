package com.newland.nsdk.core.external.command.display;

public class QRCodeParameters {
    private Byte level;
    private Byte mask;
    private Byte version;
    private Byte textPosition;
    private String text;

    /**
     * Get error correct level.
     *
     * @return Error correct level. Value range [0-3], default value is 1.
     */
    public Byte getLevel() {
        return level;
    }

    /**
     * Set error correct level.
     *
     * @param level Error correct level. Value range [0-3], default value is 1.
     */
    public void setLevel(Byte level) {
        this.level = level;
    }

    /**
     * Get mask number.
     *
     * @return Mask number. Value range [0-7], default value is 1.
     */
    public Byte getMask() {
        return mask;
    }

    /**
     * Set mask.
     *
     * @param mask Mask number. Value range [0-7], default value is 1.
     */
    public void setMask(Byte mask) {
        this.mask = mask;
    }

    /**
     * Get version.
     *
     * @return Version. Value range [0-20], default value is 0.
     */
    public Byte getVersion() {
        return version;
    }

    /**
     * Set version.
     *
     * @param version Version. Value range [0-20], default value is 0.
     */
    public void setVersion(Byte version) {
        this.version = version;
    }

    /**
     * Get text position.
     *
     * @return Text position.
     * <ul>
     *     <li>0: Bottom of QR code.</li>
     *     <li>1: Top of QR code.</li>
     * </ul>
     */
    public Byte getTextPosition() {
        return textPosition;
    }

    /**
     * Set text position.
     *
     * @param textPosition Text position.
     *                     <ul>
     *                         <li>0: Bottom of QR code.</li>
     *                         <li>1: Top of QR code.</li>
     *                     </ul>
     */
    public void setTextPosition(Byte textPosition) {
        this.textPosition = textPosition;
    }

    /**
     * Get display text.
     *
     * @return Text displayed with QR code.
     */
    public String getText() {
        return text;
    }

    /**
     * Set display text.
     *
     * @param text Text displayed with QR code.
     */
    public void setText(String text) {
        this.text = text;
    }
}
