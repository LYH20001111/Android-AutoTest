package com.newland.nsdk.core.api.external.keyboard;

import com.newland.nsdk.core.api.external.display.PictureParameters;

public class InputButtonParameters extends PictureParameters {
    /**
     * Bit 0: When it is set to 1, tapping the button will end input procedure. Bit 1~7: Reserved.
     */
    byte buttonSettings;

    public byte getButtonSettings() {
        return buttonSettings;
    }

    public void setButtonSettings(byte buttonSettings) {
        this.buttonSettings = buttonSettings;
    }
}
