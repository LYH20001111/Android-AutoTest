package com.newland.nsdk.core.api.internal.guestdisplaymanager;

import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.exception.NSDKException;

/**
 * Provides the ability to operate Guest Display.
 *
 * <p>How to get this module:</p>
 * <pre>
 *     GuestDisplayManager guestDisplayManager = (GuestDisplayManager)NSDKModuleManagerImpl.getInstance().getModule(ModuleType.GUEST_DISPLAY_MANAGER);
 * </pre>
 */
public interface GuestDisplayManager extends Module {
    /**
     * Sets the status of guest display screen backlight.
     * @param isBacklightOn    Whether to set the backlight on.
     * @throws NSDKException
     */
    void setBacklightStatus(boolean isBacklightOn) throws NSDKException;

    /**
     * Displays String content in guest display screen.
     * @param startX          <b>[Required]</b> The start X coordinate of the displayed content. Range: [0, 128]
     * @param startY          <b>[Required]</b> The start Y coordinate of the displayed content. Range: [0, 4]
     * @param content         <b>[Required]</b> The content to be displayed in the guest display screen.
     * @throws NSDKException
     */
    void displayString(int startX, int startY, String content) throws NSDKException;

    /**
     * Displays bitmap in guest display screen.
     * <p>Note: If the bitmap scope is beyond the screen, this will throw an exception.</p>
     * @param leftTopX      <b>[Required]</b> The left-top X coordinate of the displayed bitmap. Range: [0,128]
     * @param leftTopY      <b>[Required]</b> The left-top Y coordinate of the displayed bitmap. Range: [0,35]
     * @param bitmapWidth   <b>[Required]</b> The width of the bitmap to be displayed.
     * @param bitmapHeight  <b>[Required]</b> The height of the bitmap to be displayed.
     * @param bitmapData    <b>[Required]</b> The displayed bitmap data.
     * @throws NSDKException
     */
    void displayBitmap(int leftTopX, int leftTopY, int bitmapWidth, int bitmapHeight, byte[] bitmapData) throws NSDKException;

    /**
     * Clears guest display screen.
     * @throws NSDKException
     */
    void clearScreen() throws NSDKException;
}
