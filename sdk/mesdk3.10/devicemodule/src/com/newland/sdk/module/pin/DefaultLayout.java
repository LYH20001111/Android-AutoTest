package com.newland.sdk.module.pin;

import android.graphics.Bitmap;
import android.newland.os.NlBuild;

/**
 * Author by bxy, Date on 2019/11/18.<p>
 * <p>
 * Build-In password keyboard layout configuration.
 */
public class DefaultLayout {
    /**
     * show password when isHalfScreen.
     */
    private boolean halfScreenShowPs;
    /**
     * Online or offline password.
     */
    private boolean isOnlinePin;
    /**
     * Amount display.<p>
     * This param is valid if isHalfScreen is false.
     */
    private String amount;
    /**
     * A message at the top of the password keyboard.<p>
     * This is valid if that isHalfScreen is true.
     */
    private String displayMessage;
    /**
     * Whether to display half screen.
     */
    private boolean isHalfScreen;
    /**
     * Keyboard divider size.
     */
    private int dividerSize;
    /**
     * Keyboard keys round size.
     */
    private int roundSize;
    /**
     * Keyboard background color.
     */
    private int bgColor;
    /**
     * The scale of height to width.
     */
    private float scale;
    /**
     * Click sound whether it is valid or not.
     */
    private boolean enableClickSound;
    /**
     * Keyboard random type.
     */
    private KeyRondomType keyRondomType;
    /**
     * Keyboard Style.
     */
    private DefaultLayout.Style layoutStyle;
    /**
     * Cancel key attribute.
     */
    private DefaultLayout.KeyAttribute cancelKeyAttr;
    /**
     * BackSpace key attribute.
     */
    private DefaultLayout.KeyAttribute backSpaceKeyAttr;
    /**
     * Confirm key attribute.
     */
    private DefaultLayout.KeyAttribute confirmAttr;
    /**
     * Number key attribute.
     */
    private DefaultLayout.KeyAttribute numKeyAttr;

    /**
     * Only supports 90 degrees, 180 degrees, and 0 degrees, defaults to 0 degrees
     */
    private int angle;

    /**
     * The constructor of layout configuration.
     *
     * @param isOnline
     */
    public DefaultLayout(boolean isOnline) {
        this.isOnlinePin = isOnline;
        this.dividerSize = -1;
        this.roundSize = -1;
        this.bgColor = -1;
        this.scale = (NlBuild.VERSION.MODEL.equals("X800")? 0.95f : 0.8f);
        this.enableClickSound = true;
        this.angle = 0;
    }

    public boolean isHalfScreenShowPs() {
        return halfScreenShowPs;
    }

    public void setHalfScreenShowPs(boolean halfScreenShowPs) {
        this.halfScreenShowPs = halfScreenShowPs;
    }

    /**
     * Online or offline password.
     *
     * @return
     */
    public boolean getIsOnline() {
        return isOnlinePin;
    }

    /**
     * Get display amount.
     *
     * @return
     */
    public String getAmount() {
        return amount;
    }

    /**
     * Set display amount.
     *
     * @param amount
     */
    public void setAmount(String amount) {
        this.amount = amount;
    }

    /**
     * Get display message.
     *
     * @return
     */
    public String getDisplayMessage() {
        return displayMessage;
    }

    /**
     * Set display message.
     *
     * @param displayMessage
     */
    public void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }

    /**
     * Get half screen flag.
     *
     * @return
     */
    public boolean getIsHalfScreen() {
        return isHalfScreen;
    }

    /**
     * Set half screen flag.
     *
     * @param halfScreen
     */
    public void setIsHalfScreen(boolean halfScreen) {
        isHalfScreen = halfScreen;
    }

    /**
     * Get layout style.
     *
     * @return
     */
    public Style getLayoutStyle() {
        return layoutStyle;
    }

    /**
     * Set layout style{@link Style}.
     *
     * @param layoutStyle
     */
    public void setLayoutStyle(Style layoutStyle) {
        this.layoutStyle = layoutStyle;
    }

    /**
     * Get divider size.
     *
     * @return
     */
    public int getDividerSize() {
        return dividerSize;
    }

    /**
     * Set divider size.
     *
     * @param dividerSize
     */
    public void setDividerSize(int dividerSize) {
        this.dividerSize = dividerSize;
    }

    /**
     * Get round size.
     *
     * @return
     */
    public int getRoundSize() {
        return roundSize;
    }

    /**
     * Set round size.
     *
     * @param roundSize
     */
    public void setRoundSize(int roundSize) {
        this.roundSize = roundSize;
    }

    /**
     * Get keyboard background color
     *
     * @return
     */
    public int getBgColor() {
        return bgColor;
    }

    /**
     * Set keyboard background color
     *
     * @param bgColor
     */
    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    /**
     * Get keyboard random type.
     *
     * @return
     */
    public KeyRondomType getKeyRondomType() {
        return keyRondomType;
    }

    /**
     * Set keyboard random type.{@link KeyRondomType}
     *
     * @param keyRondomType
     */
    public void setKeyRondomType(KeyRondomType keyRondomType) {
        this.keyRondomType = keyRondomType;
    }

    /**
     * Get cancel key attribute.
     *
     * @return
     */
    public KeyAttribute getCancelKeyAttr() {
        return cancelKeyAttr;
    }

    /**
     * Set cancel key attribute.
     *
     * @param cancelKeyAttr
     */
    public void setCancelKeyAttr(KeyAttribute cancelKeyAttr) {
        this.cancelKeyAttr = cancelKeyAttr;
    }

    /**
     * Get backspace key attribute.
     *
     * @return
     */
    public KeyAttribute getBackSpaceKeyAttr() {
        return backSpaceKeyAttr;
    }

    /**
     * Set backspace key attribute.
     *
     * @param backSpaceKeyAttr
     */
    public void setBackSpaceKeyAttr(KeyAttribute backSpaceKeyAttr) {
        this.backSpaceKeyAttr = backSpaceKeyAttr;
    }

    /**
     * Get confirm key attribute.
     *
     * @return
     */
    public KeyAttribute getConfirmAttr() {
        return confirmAttr;
    }

    /**
     * Set confirm key attribute.
     *
     * @param confirmAttr
     */
    public void setConfirmAttr(KeyAttribute confirmAttr) {
        this.confirmAttr = confirmAttr;
    }

    /**
     * Get number key attribute.
     *
     * @return
     */
    public KeyAttribute getNumKeyAttr() {
        return numKeyAttr;
    }

    /**
     * Set number key attribute.
     *
     * @param numKeyAttr
     */
    public void setNumKeyAttr(KeyAttribute numKeyAttr) {
        this.numKeyAttr = numKeyAttr;
    }

    public int getAngle() {
        return angle;
    }

    /**
     * Only supports 90 degrees, 180 degrees, and 0 degrees, defaults to 0 degrees
     */
    public void setAngle(int angle) {
        this.angle = angle;
    }

    /**
     * Get the scale of height to width.
     *
     * @return
     */
    public float getScale() {
        return scale;
    }

    /**
     * Set the scale of height to width.
     *
     * @param scale
     */
    public void setScale(float scale) {
        this.scale = scale;
    }

    /**
     * Get click sound flag.
     *
     * @return
     */
    public boolean getEnableClickSound() {
        return enableClickSound;
    }

    /**
     * Set click sound flag.
     *
     * @param enableClickSound
     */
    public void setEnableClickSound(boolean enableClickSound) {
        this.enableClickSound = enableClickSound;
    }

    public enum Style {
        /**
         *
         */
        STYLE_1,
        /**
         *
         */
        STYLE_2,
        /**
         *
         */
        STYLE_3
    }

    public enum KeyRondomType {
        /**
         * Number key and function key are not random.
         */
        NORMAL,
        /**
         * Numbers are random,but function keys are not random.
         */
        RANDOM_NUM,
        /**
         * Number key and function key are random.
         */
        RANDOM_ALL;
    }

    public static class KeyAttribute {
        /**
         * The key value.
         */
        private int value;
        /**
         * The key background color
         */
        private int backgroundColor;
        /**
         * The text on the button.
         */
        private String text;
        /**
         * The key text size.
         */
        private int textSize;
        /**
         * The key text color.
         */
        private int textColor;
        /**
         * The key bitmap.
         */
        private Bitmap bitmap;

        /**
         * @param value           Key type.{@link DefaultLayout.Key}
         * @param backgroundColor The key background color.
         * @param text            The text on the key.
         * @param textSize        The key text size.
         * @param textColor       The key text color.
         * @param bitmap          The bitmap over the key.
         */
        public KeyAttribute(int value, int backgroundColor, String text, int textSize, int textColor, Bitmap bitmap) {
            this.value = value;
            this.backgroundColor = backgroundColor;
            this.text = text;
            this.textSize = textSize;
            this.textColor = textColor;
            this.bitmap = bitmap;
        }

        /**
         * Get key value.
         *
         * @return
         */
        public int getValue() {
            return value;
        }

        /**
         * Get key background color.
         *
         * @return
         */
        public int getBackgroundColor() {
            return backgroundColor;
        }

        /**
         * Get the text on the button.
         *
         * @return
         */
        public String getText() {
            return text;
        }

        /**
         * Get key text size.
         *
         * @return
         */
        public int getTextSize() {
            return textSize;
        }

        /**
         * Get key text color.
         *
         * @return
         */
        public int getTextColor() {
            return textColor;
        }

        /**
         * Get key bitmap.
         *
         * @return
         */
        public Bitmap getBitmap() {
            return bitmap;
        }
    }

    public class Key {
        public static final int NUM = 0x7E;
        public static final int CANCEL = 0x1B;
        public static final int BACKSPACE = 0x0A;
        public static final int CONFIRM = 0x0D;
        public static final int CLEAR = 0x9C;
        public static final int EXIT = 0x9B;
    }
}