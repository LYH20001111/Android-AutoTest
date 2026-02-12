package com.newland.sdk.module.printer;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import com.newland.sdk.mtype.Module;

import java.util.Map;

/**
 * Printer related functions
 *
 * @author linsi
 * @since V3.10.01
 */
public interface PrinterModule extends Module {
    /**
     * Get the current printer state
     *
     * @return Printer state {@link PrinterStatus#NORMAL}
     * @since V3.10.01
     */
    public PrinterStatus getStatus();

    /**
     * <p>Get the print script utility class</p>
     * <p>You can get a print script utility class by call this function, and  combine functions in {@link PrintScriptUtil}<p/>
     *
     * @param context
     * @return Print script utility class {@link PrintScriptUtil}
     */
    public PrintScriptUtil getPrintScriptUtil(Context context);

    /**
     * Print by Script (it should be printed by  multiple times if the length of data greater than 4k).
     *
     * @param scriptData    make the data(UTF-8 Encode) follows the rules of NEWLAND Script printing specification.
     * @param map           map
     * @param printListener The print listener
     * @since V3.10.01
     */
    public void print(String scriptData, @Nullable Map<String, Bitmap> map, PrintListener printListener);

    /**
     * <p>set the specified font.</p>
     * <p>After call this method you need set this font path by script {@link #print(String, Map, PrintListener)} if you want to custom the font style.</p>
     * <p>Search the file in system dir first, second in assets dir.</p>
     *
     * @param context  Context
     * @param fileName The name of font file(e.g DroidSans.ttf)
     * @return The font path
     * @since V3.10.01
     */
    public String setFont(Context context, String fileName);

    /**
     * Cut paper
     *
     * @return True: paper cut successfully.False: paper cut failed.
     * @since V3.10.01
     */
    public boolean paperCut();

    /**
     * Set the print paper size
     *
     * @param size paper size{@link PaperSize#SIZE_2INCH}
     * @return True: Set paper size successfully.False: Set paper size failed.
     * @since V3.10.01
     */
    public boolean setPaperSize(PaperSize size);

    /**
     * Print blanks
     *
     * @param lineNum Paper move distance
     * @since V3.10.01
     */
    public boolean paperFeed(int lineNum);

    /**
     * set printer status listener
     * @return if true,if printer status change,can get notify. otherwise failed.
     */
    public boolean setStatusListener(PrinterStatusListener listener);

    /**
     * cancel status listener.
     * @return
     */
    public void cancelStatusListener();

    /**
     * Gets the size of the string.
     * Contains width and height.
     * @param param the font size of text. If param equal to null,use last font and size.
     * @param text  content
     * @return
     */
    public TextSize getTextSize(String param,String text);

    /**
     * Print by Script
     *
     * @param scriptData    make the data(GBK Encode) follows the rules of NEWLAND Script printing specification.
     * @param map           map
     * @param printListener The print listener
     * @since V3.10.43_02
     */
    public void printScriptByNDK(String scriptData, @Nullable Map<String, Bitmap> map, PrintListener printListener);


    /**
     * @param flag  if it is true, the printing speed will slow down,but the printing quality will be higher, and the default mode is false.
     */
    public void setEnableHighQualityMode(boolean flag);
}

