package com.newland.nsdk.core.api.internal.printer;

import android.graphics.Bitmap;

import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.exception.NSDKException;


/**
 * Provides the ability to print.
 *
 * <p>How to get this module:</p>
 * <pre>
 *     Printer printer = (Printer)NSDKModuleManagerImpl.getInstance().getModule(ModuleType.PRINTER);
 * </pre>
 */
public interface Printer extends Module {

    /**
     * Prints an image with its actual width and height.
     *
     * <p><b>Note: Please make sure card reader is closed before printing. Otherwise it will fail to print.</b></p>
     *
     * <p>Example:</p>
     * <pre>
     *     // Get the bitmap according to your needs.
     *     Bitmap bitmap = getBitmap();
     *
     *     int width = bitmap.getWidth();
     *     int height = bitmap.getHeight();
     *
     *     ByteArrayOutputStream baos = new ByteArrayOutputStream();
     *     bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
     *
     *     int startX = 0;
     *
     *     PrintingResultListener listener = new PrintingResultListener() {
     *         {@code @Override}
     *          public void onEventRaised(int status) {
     *              // Handle the result.
     *          }
     *     };
     *
     *     try{
     *         printer.printImage(baos.toByteArray(), startX, width, height, listener);
     *     } catch(NSDKException e){
     *         // Handle the exception.
     *     }
     * </pre>
     *
     * @param image                  <b>[Required]</b> Compressed data of bitmap.
     * @param startX                 <b>[Required]</b> Starting Offset from where to print, shall be >=0.
     * @param imageWidth             <b>[Required]</b> Width of the image, shall be >0.
     * @param imageHeight            <b>[Required]</b> Height of the image, shall be >0.
     * @param printingResultListener <b>[Required]</b> Listens to the status of printer. See {@link PrintingResultListener}.
     * @throws NSDKException
     */
    void printImage(byte[] image, int startX, int imageWidth, int imageHeight, PrintingResultListener printingResultListener) throws NSDKException;

    /**
     * Prints bitmap image which compared to the previous interface has higher effectiveness.
     * <p><b>Note: Please make sure card reader is closed before printing. Otherwise it will fail to print.</b></p>
     *
     * <p>Example:</p>
     * <pre>
     *     // Get the bitmap according to your needs.
     *     Bitmap bitmap = getBitmap();
     *
     *     //If you want to zoom the bitmap image.
     *     int expectedWidth = bitmap.getWidth() * 2;
     *     int expectedHeight = bitmap.getHeight() * 2;
     *
     *
     *     int startX = 0;
     *
     *     PrintingParameters printingParameters = new PrintingParameters();
     *     printingParameters.setStartX(startX);
     *     printingParameters.setExpectedWidth(expectedWidth);
     *     printingParameters.setExpectedHeight(expectedHeight);
     *
     *     PrintingResultListener listener = new PrintingResultListener() {
     *         {@code @Override}
     *          public void onEventRaised(int status) {
     *              // Handle the result.
     *          }
     *     };
     *
     *     try{
     *         printer.printImage(bitmap, printingParameters, listener);
     *     } catch(NSDKException e){
     *         // Handle the exception.
     *     }
     * </pre>
     * @param bitmap                  <b>[Required]</b> The bitmap image.
     * @param parameters              <b>[Required]</b> Printing parameters, in this case, {@link PrintingParameters#startX}, {@link PrintingParameters#expectedImageWidth} and {@link PrintingParameters#expectedImageHeight} are required.
     * @param printingResultListener  <b>[Required]</b> Listens to the status of printer. See {@link PrintingResultListener}.
     * @throws NSDKException
     */
    void printImage(Bitmap bitmap, PrintingParameters parameters, PrintingResultListener printingResultListener) throws NSDKException;

    /**
     * Prints image store in the device which compared to the previous interface has higher effectiveness.
     * <p><b>Note: Please make sure card reader is closed before printing. Otherwise it will fail to print.</b></p>
     *
     * <p>Example:</p>
     * <pre>
     *     // Get the bitmap according to your needs.
     *     String path = "data/Share/nsdk_template.jpg";
     *     int width = 360;
     *     int height = 240;
     *
     *     //If you want to zoom the bitmap image.
     *     int expectedWidth = width * 2;
     *     int expectedHeight = height * 2;
     *
     *
     *     int startX = 0;
     *
     *     PrintingParameters printingParameters = new PrintingParameters();
     *     printingParameters.setStartX(startX);
     *     printingParameters.setExpectedWidth(expectedWidth);
     *     printingParameters.setExpectedHeight(expectedHeight);
     *
     *     PrintingResultListener listener = new PrintingResultListener() {
     *         {@code @Override}
     *          public void onEventRaised(int status) {
     *              // Handle the result.
     *          }
     *     };
     *
     *     try{
     *         printer.printImage(bitmap, printingParameters, listener);
     *     } catch(NSDKException e){
     *         // Handle the exception.
     *     }
     * </pre>
     * @param path                    <b>[Required]</b> The absolute path of the target image file in the device.
     * @param parameters              <b>[Required]</b> Printing parameters, in this case, {@link PrintingParameters#startX}, {@link PrintingParameters#expectedImageWidth} and {@link PrintingParameters#expectedImageHeight} are required.
     * @param printingResultListener  <b>[Required]</b> Listens to the status of printer. See {@link PrintingResultListener}.
     * @throws NSDKException
     */
    void printImage(String path, PrintingParameters parameters, PrintingResultListener printingResultListener) throws NSDKException;
    /**
     * Prints an image with expected width and height.
     *
     * <p><b>Note: Please make sure card reader is closed before printing. Otherwise it will fail to print.</b></p>
     *
     * <p>Example:</p>
     * <pre>
     *     // Get the bitmap according to your needs.
     *     Bitmap bitmap = getBitmap();
     *
     *     PrintingParameters parameters = new PrintingParameters();
     *     parameters.setStartX(0);
     *     parameters.setImageWidth(bitmap.getWidth());
     *     parameters.setImageHeight(bitmap.getHeight());
     *     parameters.setExpectedImageWidth(bitmap.getWidth()/2);
     *     parameters.setExpectedImageHeight(bitmap.getHeight()/2);
     *
     *     int size = bitmap.getByteCount();
     *     ByteBuffer buf = ByteBuffer.allocate(size);
     *     bitmap.copyPixelsToBuffer(buf);
     *     byte[] rgba = buf.array();
     *
     *     PrintingResultListener listener = new PrintingResultListener() {
     *         {@code @Override}
     *          public void onEventRaised(int status) {
     *              // Handle the result.
     *          }
     *     };
     *
     *     try{
     *         printer.printImage(rgba, parameters, listener);
     *     } catch(NSDKException e){
     *         // Handle the exception.
     *     }
     * </pre>
     *
     * @param image                  <b>[Required]</b> RGBA data.
     * @param parameters             <b>[Required]</b> Printing parameters, see {@link PrintingParameters}.
     * @param printingResultListener <b>[Required]</b> Listens to the status of printer. See {@link PrintingResultListener}.
     * @throws NSDKException
     */
    void printRGBAImage(byte[] image, PrintingParameters parameters, PrintingResultListener printingResultListener) throws NSDKException;

    /**
     * Gets printer status.
     *
     * @return See {@link PrinterStatus}
     */
    PrinterStatus getStatus() throws NSDKException;

    /**
     * Cuts paper.(Only supported on CPOS X5 device)
     *
     * @throws NSDKException
     */
    void cutPaper() throws NSDKException;

    /**
     * Sets printing gray.
     *
     * @param gray <b>[Required]</b> Printing gray. Value range: [1,10].
     * @throws NSDKException
     */
    void setGray(int gray) throws NSDKException;

    /**
     * Sets the printing params
     * <p>Note: This can be used to set whether to enable high-quality mode and set its gray.</p>
     * @param params <b>[Required]</b> Printing parameters to be set, details see {@link PrnParams}.
     */
    void setParams(PrnParams params) throws NSDKException;

    /**
     * Feeds paper.
     * @throws NSDKException
     */
    void feedPaper() throws NSDKException;

    /**
     * Prints content and save the printed content to the target path.
     * <p>Note: This method can be only used in N950S-ECR devices, other devices call this will return "Unsupported"(-9999)</p>
     * @param printContent  <b>[Required]</b> The script content for TTF-print.
     * @param saveContent   <b>[Required]</b> The actual print content.
     * @param path          <b>[Required]</b> The target txt file path which is used to save the actual printed content.
     * @throws NSDKException
     */
    void print(String[] printContent, String[] saveContent, String path) throws NSDKException;

    /**
     * Compares the hash value of the save txt and the last content's, if it matches, this method will return true, which means the consistency of the content after power shut down occasionally.
     * <p>Note: This method can be only used in N950S-ECR devices, other devices call this will return "Unsupported"(-9999)</p>
     * @return The consistency of the content after the power shut down occasionally.
     * @throws NSDKException
     */
    boolean verifyLast() throws NSDKException;

    /**
     * Sets the custom font for the follow-up printing procedure.
     * <p>Note: This method can be only used in N950S-ECR devices, other devices call this will return "Unsupported"(-9999)</p>
     * @param fontPath  <b>[Required]</b> The custom font path, which shall be in the "sys/fonts" path.
     * @throws NSDKException
     */
    void setFont(String fontPath) throws NSDKException;
}

