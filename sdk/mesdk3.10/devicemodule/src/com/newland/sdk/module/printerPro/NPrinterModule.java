package com.newland.sdk.module.printerPro;

/**
 * @Description
 * @Author wuhh
 * @Date 2021/6/25
 */
public interface NPrinterModule {

    /**
     * Get the current printer state
     *
     * @return Printer state {@link NPrinterStatus#NORMAL}
     * @since V3.10.01
     */
    public NPrinterStatus getStatus();

    /**
     * Add text with the table to print.
     *
     * @param textFormat Set the text format to print.
     */
    public void addText(NTableTextFormat... textFormat);

    /**
     * Add texts to print.
     *
     * @param textFormat Set the text format to print.
     */
    public void addText(NTextFormat... textFormat);

    /**
     * Add a image to print.
     *
     * @param imageFormat Set the image format to print.
     */
    public void addImage(NImageFormat imageFormat);

    /**
     * Add a barcode to print.
     *
     * @param codeFormat <p>Set the barcode format to print.</p>
     *                   <p>The default coded format is code 128.</p>
     */
    public void addBarcode(NBarcodeFormat codeFormat);

    /**
     * Add a two-dimension code to print.
     *
     * @param codeFormat Set the two-dimension code format to print.
     */
    public void addTwoDimensionCode(NTwoDimensionalCodeFormat codeFormat);

    /**
     * Add blanks to print
     *
     * @param pixel Pixel size
     */
    public void addPaperFeed(int pixel);

    /**
     * add a paper cutting command
     */
    public void addPaperCut();
    /**
     * 启动打印
     *
     * @param listener - 打印结果监听器
     */
    void startPrint(NPrintListener listener);
}
