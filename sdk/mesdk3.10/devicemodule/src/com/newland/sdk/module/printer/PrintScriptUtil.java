package com.newland.sdk.module.printer;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;

/**
 * <p>The print script utility</p>
 * <p>1. Combine functions that start with the add like the {@link #addText(TextFormat, String)},</p>
 * <p> and it can format the data (text/image/barcode/qrcode/font) which you pass in.<p/>
 * <p>2. You can use {@link #addFont(Context, String)} to customize font libraries.<p/>
 * <p>3. you can use {@link #setLineSpacing(int)} to make the line space differ.<p/>
 * <p>4. Call the {@link #print(PrintListener)} to print.<p/>
 *
 * @author linsi
 * @since V3.10.01
 */
public interface PrintScriptUtil {
    /**
     * <p>Add font.</p>
     *
     * @param context Context
     * @param name    The name of font file.(e.g DroidSans.ttf)
     * @since V3.10.01
     */
    public void addFont(Context context, String name);

    /**
     * Add texts to print.
     *
     * @param textFormat Set the text format to print.
     * @param data       Texts
     */
    public void addText(@Nullable TextFormat textFormat, String data);

    /**
     * Add a image to print.
     *
     * @param imageFormat Set the image format to print.
     * @param bitmap      The image data.
     */
    public void addImage(@Nullable ImageFormat imageFormat, Bitmap bitmap);

    /**
     * Add a barcode to print.
     *
     * @param barcodeFormat <p>Set the barcode format to print.</p>
     *                      <p>The default coded format is code 128.</p>
     * @param barcode       The barcode data.
     */
    public void addBarcode(@Nullable BarcodeFormat barcodeFormat, String barcode);

    /**
     * Add a two-dimension code to print.
     *
     * @param twoDimensionCodeFormat Set the two-dimension code format to print.
     * @param code                   The two-dimension code data.
     */
    public void addTwoDimensionCode(@Nullable TwoDimensionCodeFormat twoDimensionCodeFormat, String code);

    /**
     * Add blanks to print
     *
     * @param lineNum Paper move distance
     * @since V3.10.01
     */
    public void addPaperFeed(int lineNum);

    /**
     * Add a dotted line to print
     *
     * @since V3.10.01
     */
    public void addDottedLine();

    /**
     * Set a custom concentration to print.
     *
     * @param gray The value of gray. The range from 1 to 10, the default is 5.
     */
    public void setGray(@IntRange(from = 1, to = 10) int gray);

    /**
     * Set the line spacing.
     *
     * @param space The value of space. The range from 0 to 60, the default is 6.
     */
    public void setLineSpacing(@IntRange(from = 0, to = 60) int space);

    /**
     * <p>Print</p>
     *
     * @param printListener The print listener
     */
    public void print(PrintListener printListener);

    /**
     * The switch of reverse display.
     *
     * @param onOff reverse display or not.
     */
    public void reverseDisplay(boolean onOff);

    /**
     * add a paper cutting command
     */
    public void addPaperCut();

    /**
     * reset the content to be printer.
     */
    public void reset();
}
