package com.newland.nsdk.core.api.external.display;

/**
 * QR image parameters.
 */
public class DiaplayQRImageParameters {

    private QRErrorCorrectLevel level = QRErrorCorrectLevel.LEVEL_1;
    private QRMask mask = QRMask.MASK_1;
    private byte version;
    private boolean isAutoCenter;
    private byte xCoordinate;
    private byte yCoordinate;
    private QRTextPosition position = QRTextPosition.TOP;
    private byte[] textData;

    /**
     * Gets error correct level.
     *
     * @return Error correct level. See {@link QRErrorCorrectLevel}, default value is {@link QRErrorCorrectLevel#LEVEL_1}.
     */
    public QRErrorCorrectLevel getLevel() {
        return level;
    }

    /**
     * Sets error correct level.
     *
     * @param level Error correct level. See {@link QRErrorCorrectLevel}
     */
    public void setLevel(QRErrorCorrectLevel level) {
        this.level = level;
    }

    /**
     * Gets mask number.
     *
     * @return Mask number. See {@link QRMask}, default value is {@link QRMask#MASK_1}.
     */
    public QRMask getMask() {
        return mask;
    }

    /**
     * Sets mask number.
     *
     * @param mask Mask number. See {@link QRMask}.
     */
    public void setMask(QRMask mask) {
        this.mask = mask;
    }

    /**
     * Gets version of QR size.
     *
     * @return Version of QR size
     * <ul>
     * <li>Default value: 0</li>
     * <li>Value range: [0-20]</li>
     * <ul>
     *     <li>0 - 164*164</li>
     *     <li>1 - 180 * 180</li>
     *     <li>Size adds 16 pixel per version.</li>
     * </ul>
     * </ul>
     */
    public byte getVersion() {
        return version;
    }

    /**
     * Sets version of QR size.
     *
     * @param version Version of QR size
     *                <ul>
     *                <li>Default value: 0</li>
     *                <li>Value range: [0-20]</li>
     *                <ul>
     *                    <li>0 - 164*164</li>
     *                    <li>1 - 180 * 180</li>
     *                    <li>Size adds 16 pixel per version.</li>
     *                </ul>
     *                </ul>
     */
    public void setVersion(byte version) {
        this.version = version;
    }

    /**
     * Auto-center display QR code or not.
     *
     * @return Auto-center flag
     * <ul>
     *     <li>'true': Auto-center display.</li>
     *     <li>'false': Not auto-center display.</li>
     * </ul>
     */
    public boolean isAutoCenter() {
        return isAutoCenter;
    }

    /**
     * Sets whether auto-center display QR code or not.
     *
     * @param autoCenter Auto-center flag.
     *                   <ul>
     *                   <li>'true': Auto-center display.</li>
     *                   <li>'false': Not auto-center display.</li>
     *                   </ul>
     */
    public void setAutoCenter(boolean autoCenter) {
        isAutoCenter = autoCenter;
    }

    /**
     * Gets the X coordinate where the QR Code displayed.
     *
     * @return X coordinate. Value range: [0-320).
     */
    public byte getXCoordinate() {
        return xCoordinate;
    }

    /**
     * Sets the X coordinate where the QR Code displayed.
     *
     * @param xCoordinate X coordinate. Value range: [0-320). This will work when auto center is disabled.
     */
    public void setXCoordinate(byte xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    /**
     * Gets the Y coordinate where the QR Code to be displayed.
     *
     * @return Y coordinate. Value range: [0-240).
     */
    public byte getYCoordinate() {
        return yCoordinate;
    }

    /**
     * Sets the Y codrdinate where the QR Code to be displayed
     *
     * @param yCoordinate Y coordinate. Value range: [0-240). This will work when auto center is disabled.
     */
    public void setYCoordinate(byte yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    /**
     * Gets the relative position of the text and QR image.
     *
     * @return Position, see {@link QRTextPosition}. Default value is {@link QRTextPosition#TOP}.
     */
    public QRTextPosition getPosition() {
        return position;
    }

    /**
     * Sets the relative position of the text and QR image.
     *
     * @param position See {@link QRTextPosition}. Default value is {@link QRTextPosition#TOP}.
     */
    public void setPosition(QRTextPosition position) {
        this.position = position;
    }

    /**
     * Gets the text displayed when showing QR code.
     *
     * @return Text displayed when showing QR code, maximum 512 bytes.
     */
    public byte[] getTextData() {
        return textData;
    }

    /**
     * Sets the text displayed when showing QR code
     *
     * @param textData Text displayed when showing QR code, maximum 512 bytes.
     */
    public void setTextData(byte[] textData) {
        this.textData = textData;
    }
}
