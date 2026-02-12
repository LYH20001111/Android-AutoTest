package com.newland.nsdk.core.api.external.display;

import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.exception.NSDKException;

/**
 * <b>[External Module]</b> Provides the ability to display text and image on external device.
 *
 * <p>How to get this module:</p>
 * <pre>
 *     ExtDisplay extDisplay = (ExtDisplay)ExtNSDKModuleManagerImpl.getInstance().getModule(ModuleType.EXT_DISPLAY);
 * </pre>
 */
public interface ExtDisplay extends Module {
    /**
     * Displays English text.
     *
     * <p>Example:</p>
     * <pre>
     *      try {
     *          String[] messages = new String[4];
     *          messages[0] = "line1";
     *          messages[2] = "line3";
     *
     *          DisplayTextParameters textParameter = new DisplayTextParameters();
     *          textParameter.setAlignType(AlignType.CENTER);
     *          textParameter.setFontColor(0);
     *          textParameter.setFontSize(FontSize.NORMAL);
     *
     *          extDisplay.displayText(messages, textParameter);
     *      } catch (NSDKException e) {
     *          // Handle the error.
     *      }
     * </pre>
     *
     * @param messages <b>[Required]</b> Messages to display. Each string of this parameter will be displayed on a single line in order. 4 lines supported now.
     * @param param    <b>[Required]</b> Parameters for English text display. See {@link DisplayTextParameters}.
     * @throws NSDKException
     */
    void displayText(String[] messages, DisplayTextParameters param) throws NSDKException;

    /**
     * Displays custom Chinese text.
     *
     * <p>Example:</p>
     * <pre>
     *     try {
     *             byte[] textData = new byte[]{0x4e, 0x2d, 0x65, 0x87};
     *
     *             DisplayCNTextParameter cnTextParameter = new DisplayCNTextParameter();
     *             cnTextParameter.setxCoordinate(1);
     *             cnTextParameter.setyCoordinate(1);
     *             cnTextParameter.setTimeout(10000);
     *             cnTextParameter.setFontColor(0);
     *
     *             extDisplay.displayCNText(textData, cnTextParameter);
     *     } catch (NSDKException e) {
     *         // Handle the error.
     *     }
     * </pre>
     *
     * @param textData  <b>[Required]</b> Chinese text to be displayed.
     * @param parameter <b>[Required]</b> Parameters for Chinese text display, see {@link DisplayCNTextParameters}.
     * @throws NSDKException
     */
//    void displayCNText(byte[] textData, DisplayCNTextParameters parameter) throws NSDKException;

    /**
     * Displays pre-defined Chinese text.
     *
     * <p>Example:</p>
     * <pre>
     *     try {
     *             DisplayCNTextParameter cnTextParameter = new DisplayCNTextParameter();
     *             cnTextParameter.setxCoordinate(1);
     *             cnTextParameter.setyCoordinate(1);
     *             cnTextParameter.setTimeout(10000);
     *             cnTextParameter.setFontColor(0);
     *
     *             extDisplay.displayCNText(CNTextNotifyType.TRANSACTION_SUCCESS, cnTextParameter);
     *     } catch (NSDKException e) {
     *         // Handle the error.
     *     }
     * </pre>
     *
     * @param textNotifyType <b>[Required]</b> Pre-defined Chinese message to display, see {@link CNTextNotifyType}.
     * @param parameter      <b>[Required]</b> Parameters for Chinese text display, see {@link DisplayCNTextParameters}.
     * @throws NSDKException
     */
//    void displayCNText(CNTextNotifyType textNotifyType, DisplayCNTextParameters parameter) throws NSDKException;

    /**
     * Displays loaded black-and-white image on external device.
     *
     * @param imageID <b>[Required]</b> Image ID. Value range: [1-250]. This image shall already be loaded by {@link #loadImage} first.
     * @param x       <b>[Required]</b> Display X coordinate. Value range: [0-0xFFFF].
     * @param y       <b>[Required]</b> Display Y coordinate. Value range: [0-0xFFFF].
     * @throws NSDKException
     */
    void displayImage(byte imageID, int x, int y) throws NSDKException;

    /**
     * Displays black-and-white image on external device.
     *
     * <p>Example:</p>
     * <pre>
     *     DisplayListener displayListener = new DisplayListener() {
     *        {@code @Override}
     *         public void onError(int errorCode, String message) {
     *             // Do something when image display failed.
     *         }
     *
     *        {@code @Override}
     *         public void onSuccess() {
     *             // Do something when image display successful.
     *         }
     *     };
     *
     *     try {
     *         byte[] imageData = {0x42,0x4D, (byte) 0x96,0x00,0x00,0x00,0x00,0x00,
     *                 0x00,0x00,0x3E,0x00,0x00,0x00,0x28,0x00,0x00,0x00,0x16,0x00,
     *                 0x00,0x00,0x16,0x00,0x00,0x00,0x01,0x00,0x01,0x00,0x00,0x00,
     *                 0x00,0x00,0x58,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
     *                 0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00, (byte) 0xFF,
     *                 (byte) 0xFF, (byte) 0xFF,0x00,0x00,0x00,0x00,0x00,0x01, (byte) 0xFF,
     *                 (byte) 0x80,0x00,0x01,0x00, (byte) 0x80,0x00,0x01,0x00,0x40,0x00,
     *                 0x02,0x00,0x40,0x00,0x02,0x00,0x40,0x00,0x04,0x00,0x40,0x00,0x04,
     *                 0x00,0x00,0x00,0x08,0x00,0x00,0x00,0x08,0x00,0x00,0x00,0x00,0x00,
     *                 0x00,0x00,0x08,0x00,0x00,0x00,0x02,0x01, (byte) 0xC0,0x00,0x02,
     *                 0x09,0x00,0x00,0x02,0x6E,0x00,0x00,0x02,0x70,0x00,0x00,0x02,0x40,
     *                 0x00,0x00,0x02,0x40,0x00,0x00,0x02,0x40,0x00,0x00,0x02,0x40,0x00,
     *                 0x00,0x02,0x40,0x00,0x00,0x02,0x40,0x00,0x00,0x01, (byte) 0x80,0x00,0x00};
     *
     *         extDisplay.displayImage(imageData, 1, 1, displayListener);
     *     } catch (NSDKException e) {
     *         // Handle the error.
     *     }
     * </pre>
     *
     * @param imageData <b>[Required]</b> Image to display on external device.
     * @param x         <b>[Required]</b> Display X coordinate. Value range: [0-0xFFFF].
     * @param y         <b>[Required]</b> Display Y coordinate. Value range: [0-0xFFFF].
     * @param listener  <b>[Required]</b> Listens to the image display result. See {@link DisplayListener}.
     * @throws NSDKException
     */
    void displayImage(byte[] imageData, int x, int y, DisplayListener listener) throws NSDKException;

    /**
     * Loads black-and-white image into external device for {@link #displayImage(byte, int, int)} to display image.
     *
     * <p>Example:</p>
     * <pre>
     *     DisplayListener displayListener = new DisplayListener() {
     *        {@code @Override}
     *         public void onError(int errorCode, String message) {
     *             // Do something when image loading failed.
     *         }
     *
     *        {@code @Override}
     *         public void onSuccess() {
     *             // Do something when image loading successful.
     *         }
     *     };
     *
     *     try {
     *         byte[] imageData = {0x42,0x4D, (byte) 0x96,0x00,0x00,0x00,0x00,0x00,
     *                 0x00,0x00,0x3E,0x00,0x00,0x00,0x28,0x00,0x00,0x00,0x16,0x00,
     *                 0x00,0x00,0x16,0x00,0x00,0x00,0x01,0x00,0x01,0x00,0x00,0x00,
     *                 0x00,0x00,0x58,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
     *                 0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00, (byte) 0xFF,
     *                 (byte) 0xFF, (byte) 0xFF,0x00,0x00,0x00,0x00,0x00,0x01, (byte) 0xFF,
     *                 (byte) 0x80,0x00,0x01,0x00, (byte) 0x80,0x00,0x01,0x00,0x40,0x00,
     *                 0x02,0x00,0x40,0x00,0x02,0x00,0x40,0x00,0x04,0x00,0x40,0x00,0x04,
     *                 0x00,0x00,0x00,0x08,0x00,0x00,0x00,0x08,0x00,0x00,0x00,0x00,0x00,
     *                 0x00,0x00,0x08,0x00,0x00,0x00,0x02,0x01, (byte) 0xC0,0x00,0x02,
     *                 0x09,0x00,0x00,0x02,0x6E,0x00,0x00,0x02,0x70,0x00,0x00,0x02,0x40,
     *                 0x00,0x00,0x02,0x40,0x00,0x00,0x02,0x40,0x00,0x00,0x02,0x40,0x00,
     *                 0x00,0x02,0x40,0x00,0x00,0x02,0x40,0x00,0x00,0x01, (byte) 0x80,0x00,0x00};
     *
     *         extDisplay.loadImage((byte) 1, imageData, displayListener);
     *     } catch (NSDKException e){
     *         // Handle the error.
     *     }
     * </pre>
     *
     * @param imageID   <b>[Required]</b> Image ID. Value range is [1-255].
     * @param imageData <b>[Required]</b> Image data to be loaded on external device.
     * @param listener  <b>[Required]</b> Listens to the result of image loading. See {@link DisplayListener}.
     * @throws NSDKException
     */
    void loadImage(byte imageID, byte[] imageData, DisplayListener listener) throws NSDKException;

    /**
     * Loads color image into external device for {@link #displayColorImage} to display image.
     *
     * <ul>Color image is supported on the following devices:
     * <li>ME51P</li>
     * <li>SP100 with big screen</li>
     * </ul>
     *
     * <p>Example:</p>
     * <pre>
     *     DisplayListener displayListener = new DisplayListener() {
     *        {@code @Override}
     *         public void onError(int errorCode, String message) {
     *             // Do something when image loading failed.
     *         }
     *
     *        {@code @Override}
     *         public void onSuccess() {
     *             // Do something when image loading successful.
     *         }
     *     };
     *
     *     AssetManager assetManager = context.getAssets();
     *     InputStream is = assetManager.open("newland_51.png");
     *     Bitmap bmp = BitmapFactory.decodeStream(is);
     *     ByteArrayOutputStream baos = new ByteArrayOutputStream();
     *     bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
     *
     *     try {
     *         extDisplay.loadColorImage((byte) 1, baos.toByteArray(), displayImageListener);
     *     } catchv(NSDKException e){
     *         // Handle the error.
     *     }
     *
     * </pre>
     *
     * @param imageID   <b>[Required]</b> Image ID. Value range is [0, 1].
     * @param imageData <b>[Required]</b> Image data to be loaded on external device. The image data length of each package shall be <= 2048.
     * @param listener  <b>[Required]</b> Listens to the result of image loading. See {@link DisplayListener}.
     * @throws NSDKException
     */
    void loadColorImage(byte imageID, byte[] imageData, DisplayListener listener) throws NSDKException;

    /**
     * Displays image using the specified image data.
     *
     * <ul>Color image is supported on the following devices:
     * <li>ME51P</li>
     * <li>SP100 with big screen</li>
     * </ul>
     *
     * <p>Example:</p>
     * <pre>
     *     DisplayListener displayListener = new DisplayListener() {
     *        {@code @Override}
     *         public void onError(int errorCode, String message) {
     *             // Do something when image display failed.
     *         }
     *
     *        {@code @Override}
     *         public void onSuccess() {
     *             // Do something when image display successful.
     *         }
     *     };
     *
     *     try {
     *         AssetManager assetManager = context.getAssets();
     *         InputStream is = assetManager.open("scene_51.jpg");
     *         Bitmap bmp = BitmapFactory.decodeStream(is);
     *         ByteArrayOutputStream baos = new ByteArrayOutputStream();
     *         bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
     *
     *         DisplayColorImageParameters pictureParameter = new DisplayColorImageParameters();
     *         pictureParameter.setXCoordinate(0);
     *         pictureParameter.setYCoordinate(0);
     *         pictureParameter.setWidth(bmp.getWidth());
     *         pictureParameter.setHeight(bmp.getHeight());
     *
     *         extDisplay.displayColorImage(baos.toByteArray(), false, pictureParameter, displayListener);
     *     } catch (NSDKException e) {
     *         // Handle the error.
     *     }
     * </pre>
     *
     * @param imageData    <b>[Required]</b> Image data.
     * @param isBackground <b>[Required]</b> Whether or not to set this image as background when width=320px and height=240px.
     * @param parameter    <b>[Required]</b> Parameters for color image display. See {@link DisplayColorImageParameters}.
     * @param listener     <b>[Required]</b> Listens to the image display result. See {@link DisplayListener}.
     * @throws NSDKException
     */
    void displayColorImage(byte[] imageData, boolean isBackground, DisplayColorImageParameters parameter, DisplayListener listener) throws NSDKException;

    /**
     * Displays the image that has been loaded to the external device via {@link #loadColorImage} before.
     *
     * <ul>Color image is supported on the following devices:
     * <li>ME51P</li>
     * <li>SP100 with big screen</li>
     * </ul>
     *
     * <p>Example:</p>
     * <pre>
     *     try {
     *         AssetManager assetManager = context.getAssets();
     *
     *         InputStream is = assetManager.open("newland_51.png");
     *         Bitmap bmp = BitmapFactory.decodeStream(is);
     *
     *         DisplayColorImageParameters pictureParameter = new DisplayColorImageParameters();
     *         pictureParameter.setXCoordinate(0);
     *         pictureParameter.setYCoordinate(0);     *
     *         pictureParameter.setWidth(bmp.getWidth());     *
     *         pictureParameter.setHeight(bmp.getHeight());
     *
     *         extDisplay.displayColorImage((byte)1, 5000, pictureParameter);
     *     } catch (NSDKException e) {
     *         // Handle the error.
     *     }
     * </pre>
     *
     * @param imageID   <b>[Required]</b> Image id. Value range: [0, 1].
     * @param timeout   <b>[Required]</b> Duration time for color image display. Unit: ms.
     * @param parameter <b>[Required]</b> Parameters for color image display. See {@link DisplayColorImageParameters}.
     * @throws NSDKException
     */
    void displayColorImage(byte imageID, int timeout, DisplayColorImageParameters parameter) throws NSDKException;

    /**
     * Displays QR Code of specified data.
     *
     * <p>Example:</p>
     * <pre>
     *     DisplayListener displayListener = new DisplayListener() {
     *        {@code @Override}
     *         public void onError(int errorCode, String message) {
     *             // Do something when image loading failed.
     *         }
     *
     *        {@code @Override}
     *         public void onSuccess() {
     *             // Do something when image loading successful.
     *         }
     *     };
     *
     *     try {
     *        byte[] textData = "test".getBytes();
     *        byte[] qrData = "This is QR code".getBytes();
     *
     *        DisplayQRImageParameters qrImageParameter = new DiaplayQRImageParameters();
     *        qrImageParameter.setAutoCenter(false);
     *        qrImageParameter.setPosition(QRTextPosition.TOP);
     *        qrImageParameter.setXCoordinate((byte) 0);
     *        qrImageParameter.setYCoordinate((byte) 0);
     *        qrImageParameter.setTextData(textData);
     *
     *        extDisplay.displayQRImage(qrData, qrImageParameter, displayListener);
     *    } catch (NSDKException e) {
     *        e.printStackTrace();
     *    }
     * </pre>
     *
     * @param qrContent <b>[Required]</b> QR content data.
     * @param parameter <b>[Required]</b> Parameters for QR code display. See{@link DiaplayQRImageParameters}
     * @param listener  <b>[Required]</b> Listens to the result of QR code display. See {@link DisplayListener}.
     * @throws NSDKException
     */
    void displayQRImage(byte[] qrContent, DiaplayQRImageParameters parameter, DisplayListener listener) throws NSDKException;

    /**
     * Displays Menu Options.
     * @param timeout   <b>[Required]</b> The display timeout. Unit:s. Value range: >0.
     * @param title     <b>[Required]</b> The menu title. Its title shall not be more than 32.
     * @param menus     <b>[Required]</b> The menu options to be shown. The max option number is 12.
     * @param callback  <b>[Required]</b> The callback to be invoked when error occurred or menu option selected.
     * @throws NSDKException
     */
    void displayMenu(int timeout, String title, String[] menus, SelectionCallback callback) throws NSDKException;

    /**
     * Displays buttons.
     * <p>Note:The images of the buttons shall be loaded by {@link ExtDisplay#displayColorImage(byte, int, DisplayColorImageParameters)} first.</p>
     * @param titleParameters  <b>[Required]</b> The parameters related to the title to be shown on the pinpad screen, details see {@link TitleParameters}.
     * @param buttons          <b>[Required]</b> The parameters related to the buttons to be shown on the pinpad screen, details see {@link ButtonParameters}.
     * @param timeout          <b>[Required]</b> The timeout of the display and selection procedure.
     * @param isReturnHome     <b>[Required]</b> Whether to return home after the selection.
     * @param callback         <b>[Required]</b> The callback of the selection, invoked when button selected, details see {@link SelectionCallback}.
     * @throws NSDKException
     */
    void displayButtons(TitleParameters titleParameters, ButtonParameters[] buttons, int timeout, boolean isReturnHome, SelectionCallback callback) throws NSDKException;

    /**
     * Display custom view on Pin pad.
     * @param displayConfiguration  <b>[Required]</b> The display configuration, see {@link DisplayConfiguration}.
     * @param messages              <b>[Required]</b> The messages to be displayed.
     * @param pictures              <b>[Required]</b> The parameters of the pictures to be displayed. See {@link PictureParameters}.
     * @throws NSDKException
     */
    void displayView(DisplayConfiguration displayConfiguration, String[] messages, PictureParameters[] pictures) throws NSDKException;

    /**
     * Sets whether or not to clean screen before displaying PIN/text automatically.
     *
     * @param isAuto <b>[Required]</b> Auto-clean flag.
     *               <ul>
     *               <li>'true': It will clean screen first, then display PIN/text to the specified lines. In this case, the screen will only have the PIN/text displayed.</li>
     *               <li>'false': It will not clean screen before displaying PIN/text. In this case, the screen may still have some old information displayed with the PIN/text.</li>
     *               </ul>
     * @throws NSDKException
     */
    void setAutoClearScreen(boolean isAuto) throws NSDKException;

    /**
     * Cleans the screen.
     *
     * @throws NSDKException
     */
    void clearScreen() throws NSDKException;

    /**
     * Returns to main menu.
     *
     * @throws NSDKException
     */
    void backToHome() throws NSDKException;

    /**
     * Sets the configuration of return to home.
     * @param isReturnHome      <b>[Required]</b> Whether returns to home after some operations.
     * @param enableCancelKey   <b>[Required]</b> Whether enables "Cancel" key to return to home.
     * @throws NSDKException
     */
    void setReturnToHome(boolean isReturnHome, boolean enableCancelKey) throws NSDKException;

    /**
     * Sets the UI mode used in the pinpad.
     * @param mode  <b>[Required]</b> The UI mode to be used. If bit7 is set to 1, it means using the custom UI afterwards.
     * @throws NSDKException
     */
    void setUIMode(byte mode) throws NSDKException;

    /**
     * Whether to display the version in the bottom or not.
     * @param isDisplay  <b>[Required]</b> Whether to display the version in the bottom or not.
     * @throws NSDKException
     */
    void displayVersion(boolean isDisplay) throws NSDKException;
}
