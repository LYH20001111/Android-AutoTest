package com.newland.sdkdemo.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.DisplayMetrics;
import android.util.Log;

import com.newland.sdk.module.printer.Alignment;
import com.newland.sdk.module.printer.BarcodeEncode;
import com.newland.sdk.module.printer.BarcodeFormat;
import com.newland.sdk.module.printer.EnFontSize;
import com.newland.sdk.module.printer.ErrorCode;
import com.newland.sdk.module.printer.FontScale;
import com.newland.sdk.module.printer.FontSize;
import com.newland.sdk.module.printer.ImageFormat;
import com.newland.sdk.module.printer.PaperSize;
import com.newland.sdk.module.printer.PrintListener;
import com.newland.sdk.module.printer.PrintScriptUtil;
import com.newland.sdk.module.printer.PrinterModule;
import com.newland.sdk.module.printer.PrinterStatus;
import com.newland.sdk.module.printer.PrinterStatusListener;
import com.newland.sdk.module.printer.SpaceScale;
import com.newland.sdk.module.printer.TextFormat;
import com.newland.sdk.module.printer.TwoDimensionCodeEncode;
import com.newland.sdk.module.printer.TwoDimensionCodeFormat;
import com.newland.sdk.module.printer.ZhFontSize;
import com.newland.sdk.module.printerPro.NTableTextFormat;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.adapter.LayoutMode;
import com.newland.sdkdemo.annotation.MethodGridEntity;
import com.newland.sdkdemo.utils.DialogUtils;
import com.newland.sdkdemo.utils.MessageTag;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Author by bxy, Date on 2019/5/11 0011.
 */
public class PrinterFragment extends BaseFragment {
    private PrinterModule printerModule;
    private PrintScriptUtil printScriptUtil;
    private static final int INDEX_PRINTER_STATE = 1;
    private static final int INDEX_PRINT_SCRIPT = 2;
    private static final int INDEX_PAPER_FEED = 3;
    private static final int INDEX_PAPER_SIZE = 4;
    private static final int INDEX_GET_FONT_PATH = 5;
    private static final int INDEX_PAPER_CUT = 6;

    private static final int INDEX_PRINT_PDF = 7;

    private static final int INDEX_FILL8 = 8;

    private static final int INDEX_FILL9 = 9;
    private static final int INDEX_FILL10 = 10;
    private static final int INDEX_FILL11= 11;
    private static final int INDEX_FILL12= 12;

    private static final int INDEX_GET_PRINT_UTILS = 13;
    private static final int INDEX_UTILS_ADD_FONT = 14;
    private static final int INDEX_UTILS_ADD_TEXT = 15;

    private static final int INDEX_UTILS_ADD_IMAGE = 16;
    private static final int INDEX_UTILS_ADD_BARCODE = 17;
    private static final int INDEX_UTILS_ADD_TWOBARCODE = 18;

    private static final int INDEX_UTILS_ADD_FEEDLINE = 19;
    private static final int INDEX_UTILS_SET_GRAY = 20;
    private static final int INDEX_UTILS_SET_SAPCE = 21;

    private static final int INDEX_UTILS_ADD_DOTTED_LINE = 22;
    private static final int INDEX_UTILS_PRINT = 23;

    private static final int INDEX_PAPER_LISTENER = 24;
    public PrinterFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_printer_f);
    }

    @Override
    public void initData() {
        printerModule = moduleManage.getPrinterModule();
    }

    @Override
    public Object getModule() {
        return PrinterFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.tv_printer_status, functionid = INDEX_PRINTER_STATE)
    private void getState() {
        try {
            showMessage(context.getString(R.string.msg_printer_status) + printerModule.getStatus() + "\n", MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_get_printer_status_error) + e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_printer_set_paper_size, functionid = INDEX_PAPER_SIZE)
    private void setPaperSize() {
        try {
            showMessage(context.getString(R.string.msg_get_printer_paper_size) + "\n", MessageTag.NORMAL);
            String[] inchs = new String[]{"2 inch", "3 inch"};
            DialogUtils.createSingleChoiceDialog(context, context.getString(R.string.msg_get_printer_paper_size), inchs, new DialogUtils.SingleChoiceDialogCallback() {
                @Override
                public void onResult(int id) {
                    PaperSize paperSize = PaperSize.SIZE_2INCH;
                    switch (id) {
                        case 0:
                            paperSize = PaperSize.SIZE_2INCH;
                            break;
                        case 1:
                            paperSize = PaperSize.SIZE_3INCH;
                            break;
                    }
                    try {
                        boolean is = printerModule.setPaperSize(paperSize);
                        if (is) {
                            showMessage(context.getString(R.string.msg_get_printer_paper_size_success) + "\n", MessageTag.DATA);
                        } else {
                            showMessage(context.getString(R.string.msg_get_printer_paper_size_fail) + "\n", MessageTag.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        showMessage(context.getString(R.string.msg_error) + e, MessageTag.ERROR);
                    }


                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error) + e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_printer_get_font_path, functionid = INDEX_GET_FONT_PATH)
    private void getFontPath() {
        try {
            String name = "simsun.ttc";
            String path = printerModule.setFont(context, name);
            showMessage(context.getString(R.string.msg_get_printer_get_font_path) + path + "\n", MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error) + e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_printer_paper_feed, functionid = INDEX_PAPER_FEED)
    private void paperFeed() {
        try {
            int lineNum = 3;
            printerModule.paperFeed(lineNum);
            showMessage(context.getString(R.string.msg_get_printer_paper_feed_complete) + lineNum + "\n", MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error) + e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_printer_paper_cut, functionid = INDEX_PAPER_CUT)
    private void paperCut() {
        try {
            boolean is = printerModule.paperCut();
            if (is) {
                showMessage(context.getString(R.string.msg_get_printer_paper_cut_complete) + "\n", MessageTag.DATA);
            } else {
                showMessage(context.getString(R.string.msg_get_printer_paper_cut_fail) + "\n", MessageTag.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error) + e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnname="printPDF", functionid = INDEX_PRINT_PDF)
    private void printPDF(){
        printerModule.setEnableHighQualityMode(true);
        ArrayList<Bitmap> bitmaps = pdfToBitmap("receipt_test.pdf");
        StringBuffer scriptBuffer = new StringBuffer();
        Map<String, Bitmap> map = new HashMap<String, Bitmap>();
        for (int i = 0; i < bitmaps.size(); i++) {
            Bitmap bitmap = bitmaps.get(i);
            map.put("bmp"+i, bitmap);
            scriptBuffer.append("*image l "+bitmap.getWidth()+"*"+bitmap.getHeight()+" path:" + "bmp"+i + "\n");
        }
        printerModule.print(scriptBuffer.toString(), map, new PrintListener() {
            @Override
            public void onSuccess() {
                showMessage(context.getString(R.string.msg_print_script_success) + "\n", MessageTag.DATA);
            }

            @Override
            public void onError(ErrorCode error, String msg) {
                showMessage(context.getString(R.string.msg_print_script_fail) + context.getString(R.string.msg_print_script_fail_error_code) + error + ", " + context.getString(R.string.msg_print_script_fail_error_msg) + msg + "\n", MessageTag.ERROR);
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_printer_print_script, functionid = INDEX_PRINT_SCRIPT)
    private void printByScript() {
        if (printerModule.getStatus() != PrinterStatus.NORMAL) {
            showMessage(context.getString(R.string.msg_print_error_and_printer_status_abnormal) + "\r\n", MessageTag.ERROR);
        } else {
            try {
                // ------------------------------------------------------------
                // Note: For details about script rules, see <TTF_Script_print_command_standard.pdf> in the doc directory of the compressed documentation package.  ！！！
                // ------------------------------------------------------------
                StringBuffer scriptBuffer = new StringBuffer();
                String fontsPath = printerModule.setFont(context, "simsun.ttc");
                if (fontsPath != null) {
                    scriptBuffer.append("!font " + fontsPath + "\n");//set font
                }
                scriptBuffer.append("*line\n!gray 3\n!yspace 10\n");
                scriptBuffer.append("!hz s\n!asc s\n*text l "+context.getString(R.string.msg_print_smallfont)+"\n"); //Both Chinese and English are set as S font and printed to the left
                scriptBuffer.append("*line\n!gray 6\n!yspace 15\n");
                scriptBuffer.append("!hz n\n!asc n\n*text c "+context.getString(R.string.msg_print_standardfont)+"\n"); //Both Chinese and English are set as N font and printed to the center
                scriptBuffer.append("*line\n!gray 10\n!yspace 20\n");
                scriptBuffer.append("!hz l\n!asc l\n*text r "+context.getString(R.string.msg_print_bigfont)+"\n");//Both Chinese and English are set as L font and printed to the right
                scriptBuffer.append("*line\n!gray 6\n!yspace 10\n");
                scriptBuffer.append("!hz sn\n!asc sn\n*underline l "+context.getString(R.string.msg_print_sn)+"\n");//Both Chinese and English are set as SN font and printed to the left and underlined
                scriptBuffer.append("!hz sl\n!asc sl\n*underline c "+context.getString(R.string.msg_print_sl)+"\n");//Both Chinese and English are set as SL font and printed to the center and underlined
                scriptBuffer.append("!hz nl\n!asc nl\n*underline r "+context.getString(R.string.msg_print_nl)+"\n");//Both Chinese and English are set as NL font and printed to the right and underlined
                scriptBuffer.append("*line\n!gray 6\n!yspace 15\n");
                scriptBuffer.append("!BARCODE 6 96 0 3\n*BARCODE l 123456712345678888\n");//Bar code width 6, height 96, left print
                scriptBuffer.append("!NLPRNOVER\n");
                scriptBuffer.append("!BARCODE 8 72 0 3\n*BARCODE c 123456712345678888\n");//Bar code width 8, height 72, center print
                scriptBuffer.append("!NLPRNOVER\n");
                scriptBuffer.append("!BARCODE 8 160 0 3\n*BARCODE r 123456712345678888\n");//Bar code width 8, height 160, right print
                scriptBuffer.append("*line\n!gray 6\n!yspace 15\n");
                scriptBuffer.append("!QRCODE 100 2 3\n*QRCODE l ABCDEFG\n");//二The height of two-dimensional code is 100, error correction level is 2, and it is printed on the left
                scriptBuffer.append("!NLPRNOVER\n");
                scriptBuffer.append("!QRCODE 200 3 3\n*QRCODE c ABCDEFGH\n");//The height of two-dimensional code is 200, error correction level is 3, and it is printed on the center
                scriptBuffer.append("!NLPRNOVER\n");
                scriptBuffer.append("!QRCODE 300 1 3\n*QRCODE r ABCDEFGHJ\n");//The height of two-dimensional code is 300, error correction level is 1, and it is printed on the right

                Map<String, Bitmap> map = new HashMap<String, Bitmap>();
                Bitmap bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.fffffffff);
                String bmp0 = "bmp0", bmp1 = "bmp1", bmp2 = "bmp2", bmp3 = "bmp3", bmp4 = "bmp4", bmp5 = "bmp5";
                map.put(bmp0, bitmap1);
                map.put(bmp1, bitmap1);
                map.put(bmp2, bitmap1);
                map.put(bmp3, bitmap1);
                map.put(bmp4, bitmap1);
                map.put(bmp5, bitmap1);
                scriptBuffer.append("*line\n!yspace 5\n");
                scriptBuffer.append("*image l 200*200 path:" + bmp0 + "\n");//Otsu threshold method is used to print, left print
                scriptBuffer.append("*image l 200*200 path:yz:240;" + bmp1 + "\n");//Set fixed threshold to 240, print in left [1-254]
                scriptBuffer.append("*image c 200*200 path:yz:128;" + bmp2 + "\n");//Set fixed threshold to 128, print in center [1-254]
                scriptBuffer.append("*image r 200*200 path:yz:68;" + bmp3 + "\n");//Set fixed threshold to 68, print in right [1-254]
                scriptBuffer.append("*image l 200*200 path:yz:0;" + bmp4 + "\n");//WellnerAdaptiveThreshold method is used to print，left print

                scriptBuffer.append("*line\n!yspace 50\n");
                scriptBuffer.append("!NLFONT 2 2 0\n*text l "+context.getString(R.string.msg_print_size)+"\n!NLFONT 2 2 3\n*text r "+context.getString(R.string.msg_print_double)+"\n");//Horizontal and vertical double magnification
                scriptBuffer.append("!NLFONT 2 2 1\n*text l "+context.getString(R.string.msg_print_size)+"\n!NLFONT 2 2 3\n*text r "+context.getString(R.string.msg_print_hdouble)+"\n");//Horizontal double magnification, vertical normal
                scriptBuffer.append("!NLFONT 2 2 2\n*text l "+context.getString(R.string.msg_print_size)+"\n!NLFONT 2 2 3\n*text r "+context.getString(R.string.msg_print_vdouble)+"\n");//Horizontal normal, vertical double magnification
                scriptBuffer.append("!NLFONT 2 2 3\n*text l "+context.getString(R.string.msg_print_size)+"\n!NLFONT 2 2 3\n*text r "+context.getString(R.string.msg_print_normal)+"\n");//Horizontal and vertical normal
                scriptBuffer.append("!NLFONT 2 2 4\n*text l "+context.getString(R.string.msg_print_size)+"\n!NLFONT 2 2 3\n*text r "+context.getString(R.string.msg_print_3times)+"\n");//Horizontal and vertical 3 times magnification
                scriptBuffer.append("!NLFONT 2 2 5\n*text l "+context.getString(R.string.msg_print_size)+"\n!NLFONT 2 2 3\n*text r "+context.getString(R.string.msg_print_h3times)+"\n");//Horizontal 3 times magnification, vertical normal
                scriptBuffer.append("!NLFONT 2 2 6\n*text l "+context.getString(R.string.msg_print_size)+"\n!NLFONT 2 2 3\n*text r "+context.getString(R.string.msg_print_v3times)+"\n");//Horizontal normal, vertical 3 times magnification

                scriptBuffer.append("*line\n!gray 6\n!yspace 20\n");

                scriptBuffer.append("!NLFONT 2 2 3\n*TEXT l "+context.getString(R.string.msg_print_song)+"1A\n");//Set the Chinese font to 24x24 and the English font to 16x16. The horizontal and vertical fonts are normal, and the font is printed on the left. After the next two printing commands are processed, they are printed on the same line
                fontsPath = printerModule.setFont(context, "DroidSansFallback.ttf");
                if (fontsPath != null) {
                    scriptBuffer.append("!font " + fontsPath + "\n");//Set font
                }
                scriptBuffer.append("!NLFONT 1 1 6\n*UNDERLINE c default\n");//Set Chinese font 16x16, English font 12x12, horizontal normal, vertical 3 times magnification, center print, underline, will not print immediately
                scriptBuffer.append("!NLFONT 2 2 0\n*text r "+context.getString(R.string.msg_print_font)+"\n");//Set Chinese font 24x24, English font 16x16, horizontal and vertical double magnification, printing on the right. Print on the same line with the data from the above two commands.

                fontsPath = printerModule.setFont(context, "simsun.ttc");
                if (fontsPath != null) {
                    scriptBuffer.append("!font " + fontsPath + "\n");//Set font
                }

                scriptBuffer.append("*line\n!gray 6\n!yspace 20\n");
                scriptBuffer.append("!BARCODE 8 120 0 0\n*BARCODE c A123456789B\n");//CODABAR:The first and last digits are any of 'A', 'B', 'C', or 'D', with the middle digit.
                scriptBuffer.append("!BARCODE 8 120 0 1\n*BARCODE c 1-.$/+% \n");//CODE39:Only support 10 digits, 26 uppercase letters, and 7 special characters('-'、'.'、'$'、'/'、'+'、'%'、space). A total of 43 characters, can be arbitrary length of data encoding.
                scriptBuffer.append("!BARCODE 8 120 0 2\n*BARCODE c 1BC-.$/56\n");//CODE93:Only support 10 digits, 26 uppercase letters, and 7 special characters('-'、'.'、'$'、'/'、'+'、'%'、space)。
                scriptBuffer.append("!BARCODE 8 120 0 3\n*BARCODE c ABC123456123\n");//CODE128:Can represent a total of 128 characters from ASCII 0 to ASCII 127, including numbers, upper and lower case letters, and symbolic characters.
                scriptBuffer.append("!BARCODE 8 120 1 4\n*BARCODE c 1234567\n");//EAN-8/JAN-8:Only digits are supported. The length is fixed. There are 8 digits: 7 data bits, and 1 parity bit.Data less than 7 bits will be preceded by 0.
                scriptBuffer.append("!BARCODE 8 120 1 4\n*BARCODE c 123456789\n");//EAN-13/JAN-13:Only digits are supported. The length is fixed at 13. There are 12 data bits and 1 parity bit. Data less than 12 bits will be preceded by 0.
                scriptBuffer.append("!BARCODE 8 120 0 5\n*BARCODE c 123456789123\n");//ITF:Only digits are supported. There are 14 bits in length, 13 data bits, and 1 parity bit. If the data is less than 13 bits, 0 is added before the parity bit.
                scriptBuffer.append("!BARCODE 8 120 1 6\n*BARCODE c 12345678912\n");//UPC-A:Only digits are supported. There are 12 bits in length, including 11 data bits and 1 parity bit. If the data is less than 11 bits, 0 is added before the parity bit
                scriptBuffer.append("!BARCODE 8 120 1 7\n*BARCODE c 1123456\n");//UPC-E:Only digits are supported. The first digit is 0 or 1, the last digit is the parity bit, and the middle six digits are data

                scriptBuffer.append("!QRCODE 300 0 0\n*QRCODE c Test000:"+context.getString(R.string.msg_print_qrcode)+"\n");//DC(Data Matrix):Encodable character set includes all ASCII characters and extended ASCII characters, a total of 256 characters.
                scriptBuffer.append("!QRCODE 200 0 0\n*QRCODE c Test111:"+context.getString(R.string.msg_print_qrcode)+"\n");//DC(Data Matrix):Encodable character set includes all ASCII characters and extended ASCII characters, a total of 256 characters.
                scriptBuffer.append("!QRCODE 100 0 0\n*QRCODE c Test222:"+context.getString(R.string.msg_print_qrcode)+"\n");//DC(Data Matrix):Encodable character set includes all ASCII characters and extended ASCII characters, a total of 256 characters.
                scriptBuffer.append("!QRCODE 300 0 1\n*QRCODE c Test555:"+context.getString(R.string.msg_print_qrcode)+"\n");//The same as DC(Data Matrix)
                scriptBuffer.append("!QRCODE 300 0 2\n*QRCODE c Test666:"+context.getString(R.string.msg_print_qrcode)+"\n");//PDF-417:Support letters and digits, poor Support for Chinese (about 500)
                scriptBuffer.append("!QRCODE 300 0 3\n*QRCODE c Test777:"+context.getString(R.string.msg_print_qrcode)+"\n");//QR Code:Chinese support is good.


                scriptBuffer.append("*line\n!gray 6\n!yspace 15\n");
                scriptBuffer.append("!hz n\n!asc n\n*text c "+context.getString(R.string.msg_print_offset)+"\n");
                scriptBuffer.append("!hz n\n!asc n\n*text x:50 x "+context.getString(R.string.msg_print_vaule)+"50\n");
                scriptBuffer.append("!hz n\n!asc n\n*underline x:60 x "+context.getString(R.string.msg_print_vaule)+"60\n");
                scriptBuffer.append("*feedline 1\n");
                scriptBuffer.append("!BARCODE 1 72 0 3\n*BARCODE x:70 100017986685631304\n");
                scriptBuffer.append("*feedline 1\n");
                scriptBuffer.append("!QRCODE 50 2 3\n*QRCODE x:80 ABC123456789DEFGH\n");
                scriptBuffer.append("*feedline 1\n");
                scriptBuffer.append("*image x:90 200*200 path:" + bmp5 + "\n");

                scriptBuffer.append("!NLFONT 13 7 0\n*TEXT x:30 X:30\n");
                scriptBuffer.append("!NLFONT 1 2 3\n*UNDERLINE x:180 X:180\n");
                scriptBuffer.append("!NLFONT 6 2 3\n*text x:270 X:270\n");
                scriptBuffer.append("!BARCODE 6 120 0 0\n*BARCODE x:50 A1234567890A\n");
                scriptBuffer.append("!QRCODE 200 0 0\n*QRCODE x:20 Test123:"+context.getString(R.string.msg_print_qrcode)+"\n");
                scriptBuffer.append("!hz n\n!asc n\n*text c "+context.getString(R.string.msg_print_offset)+"\n");
                scriptBuffer.append("*feedline 3\n");

                scriptBuffer.append("*line\n!gray 6\n!yspace 20\n");
                scriptBuffer.append("!NLFONT 1 1 3\n*TEXT l ABC"+context.getString(R.string.msg_print_size)+"\n!NLFONT 2 2 3\n*text r ABC"+context.getString(R.string.msg_print_size)+"\n");
                scriptBuffer.append("!NLFONT 3 3 3\n*TEXT l ABC"+context.getString(R.string.msg_print_size)+"\n!NLFONT 4 4 3\n*text r ABC"+context.getString(R.string.msg_print_size)+"\n");
                scriptBuffer.append("!NLFONT 5 5 3\n*TEXT l ABC"+context.getString(R.string.msg_print_size)+"\n!NLFONT 6 6 3\n*text r ABC"+context.getString(R.string.msg_print_size)+"\n");
                scriptBuffer.append("!NLFONT 7 7 3\n*TEXT l ABC"+context.getString(R.string.msg_print_size)+"\n!NLFONT 8 8 3\n*text r ABC"+context.getString(R.string.msg_print_size)+"\n");
                scriptBuffer.append("!NLFONT 9 9 3\n*TEXT l ABC"+context.getString(R.string.msg_print_size)+"\n!NLFONT 10 10 3\n*text r ABC"+context.getString(R.string.msg_print_size)+"\n");
                scriptBuffer.append("!NLFONT 11 11 3\n*TEXT l ABC"+context.getString(R.string.msg_print_size)+"\n!NLFONT 12 12 3\n*text r ABC"+context.getString(R.string.msg_print_size)+"\n");
                scriptBuffer.append("!NLFONT 13 13 3\n*TEXT l ABC"+context.getString(R.string.msg_print_size)+"\n!NLFONT 14 14 3\n*text r ABC"+context.getString(R.string.msg_print_size)+"\n");
                scriptBuffer.append("!NLFONT 15 15 3\n*TEXT l ABC"+context.getString(R.string.msg_print_size)+"\n!NLFONT 16 16 3\n*text r ABC"+context.getString(R.string.msg_print_size)+"\n");
                scriptBuffer.append("!NLFONT 17 17 3\n*TEXT l ABC"+context.getString(R.string.msg_print_size)+"\n!NLFONT 18 18 3\n*text r ABC"+context.getString(R.string.msg_print_size)+"\n");
                scriptBuffer.append("!NLFONT 19 19 3\n*TEXT l ABC"+context.getString(R.string.msg_print_size)+"\n!NLFONT 20 20 3\n*text r ABC"+context.getString(R.string.msg_print_size)+"\n");
                scriptBuffer.append("!NLFONT 21 21 3\n*TEXT l ABC"+context.getString(R.string.msg_print_size)+"\n!NLFONT 22 22 3\n*text r ABC"+context.getString(R.string.msg_print_size)+"\n");
                scriptBuffer.append("!NLFONT 23 23 3\n*TEXT l ABC"+context.getString(R.string.msg_print_size)+"\n!NLFONT 23 24 3\n*text r ABC"+context.getString(R.string.msg_print_size)+"\n");
                scriptBuffer.append("!NLFONT 25 25 3\n*TEXT l ABC"+context.getString(R.string.msg_print_size)+"\n!NLFONT 25 26 3\n*text r ABC"+context.getString(R.string.msg_print_size)+"\n");
                scriptBuffer.append("!NLFONT 25 27 3\n*TEXT l ABC"+context.getString(R.string.msg_print_size)+"\n!NLFONT 25 28 3\n*text r ABC"+context.getString(R.string.msg_print_size)+"\n");
                scriptBuffer.append("!NLFONT 25 29 3\n*TEXT l ABC"+context.getString(R.string.msg_print_size)+"\n!NLFONT 25 30 3\n*text r ABC"+context.getString(R.string.msg_print_size)+"\n");
                scriptBuffer.append("!NLFONT 25 31 3\n*TEXT l ABC"+context.getString(R.string.msg_print_size)+"\n!NLFONT 25 32 3\n*text r ABC"+context.getString(R.string.msg_print_size)+"\n");
                scriptBuffer.append("!NLFONT 25 33 3\n*TEXT l ABC"+context.getString(R.string.msg_print_size)+"\n!NLFONT 25 34 3\n*text r ABC"+context.getString(R.string.msg_print_size)+"\n");
                scriptBuffer.append("!NLFONT 25 35 3\n*TEXT l ABC"+context.getString(R.string.msg_print_size)+"\n!NLFONT 25 36 3\n*text r ABC"+context.getString(R.string.msg_print_size)+"\n");
                scriptBuffer.append("!NLFONT 25 37 3\n*TEXT l ABC"+context.getString(R.string.msg_print_size)+"\n!NLFONT 25 38 3\n*text r ABC"+context.getString(R.string.msg_print_size)+"\n");
                scriptBuffer.append("*feedline 3\n");
                scriptBuffer.append("*line\n*feedline l:1\n");
                scriptBuffer.append("*line\n*feedline l:2\n");
                scriptBuffer.append("*line\n*feedline p:24\n");
                scriptBuffer.append("*line\n*feedline p:42\n");
                scriptBuffer.append("*line\n");
                scriptBuffer.append("!reverse on\n");//start reverse display
                scriptBuffer.append("!hz n\n!asc n\n!gray 6\n!yspace 0\n");
                scriptBuffer.append("*text l MERCHANT NAME:Test merchant\n");
                scriptBuffer.append("*line\n");
                scriptBuffer.append("*underline l MERCHANT NO:123456789012345\n");
                scriptBuffer.append("!NLFONT 13 7 0\n*text l Left\n");
                scriptBuffer.append("!NLFONT 1 2 4\n*underline c Medium\n");
                scriptBuffer.append("!NLFONT 6 20 3\n*text r Right\n");
                scriptBuffer.append("!reverse off\n");//End reverse display
                scriptBuffer.append("*feedline 3\n");
                printerModule.print(scriptBuffer.toString(), map, new PrintListener() {
                    @Override
                    public void onSuccess() {
                        showMessage(context.getString(R.string.msg_print_script_success) + "\n", MessageTag.DATA);
                    }

                    @Override
                    public void onError(ErrorCode error, String msg) {
                        showMessage(context.getString(R.string.msg_print_script_fail) + context.getString(R.string.msg_print_script_fail_error_code) + error + ", " + context.getString(R.string.msg_print_script_fail_error_msg) + msg + "\n", MessageTag.ERROR);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                showMessage(context.getString(R.string.msg_print_script_error) + e, MessageTag.ERROR);
            }
        }
    }

    private ArrayList<Bitmap> pdfToBitmap(String pdfFileName) {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                InputStream assetInputStream = context.getAssets().open(pdfFileName);
                File file = new File(Environment.getExternalStorageDirectory() + "/" + pdfFileName);
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();
                file.setWritable(true);
                file.setReadable(true);
                file.setExecutable(true);
                copyToCache(assetInputStream, file);
                ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
                PdfRenderer renderer = new PdfRenderer(fileDescriptor);
                Bitmap bitmap;
                final int pageCount = renderer.getPageCount();
                for (int i = 0; i < pageCount; i++) {
                    PdfRenderer.Page page = renderer.openPage(i);
                    int width = page.getWidth();
                    int height = page.getHeight();
                    //Create bitmap with its original size.
                    bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    //Scale the bitmap to the available size.
                    Bitmap scaledBitmap = scaleBitmap(bitmap);
                    Canvas canvas = new Canvas(scaledBitmap);
                    canvas.drawColor(Color.WHITE);
                    canvas.drawBitmap(scaledBitmap, 0, 0, null);
                    Rect r = new Rect(0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight());
                    page.render(scaledBitmap, r, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                    bitmaps.add(scaledBitmap);
                    // close the page
                    page.close();
                }
                // close the renderer
                renderer.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmaps;
    }

    private void copyToCache(InputStream inputStream, File outFile) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(outFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
        } finally {
            inputStream.close();
        }
    }

    private Bitmap scaleBitmap(Bitmap origin) {
        if (origin == null) {
            return null;
        }
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int widthPixels = dm.widthPixels;
        int width = origin.getWidth();
        int height = origin.getHeight();
        int PAPER_WIDTH_NORMAL = 384;
        int PAPER_WIDTH_LARGE = 576;
        float ratio = (float) PAPER_WIDTH_NORMAL / (float) width;
        if (Build.MODEL.contains("CPOS")) {
            ratio = (float) PAPER_WIDTH_LARGE / (float) width;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(ratio, ratio);
        Bitmap newBitmap = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        origin.recycle();
        return newBitmap;
    }

    @MethodGridEntity(divtipid = 0, functionid = INDEX_FILL8)
    private void fill8() {
    }
    @MethodGridEntity(divtipid = 0, functionid = INDEX_FILL9)
    private void fill9() {
    }

    @MethodGridEntity(divtipid = 0, functionid = INDEX_FILL10)
    private void fill10() {
    }

    @MethodGridEntity(divtipid = R.string.tv_printer_util, functionid = INDEX_FILL11)
    private void fill11() {
    }

    @MethodGridEntity(divtipid = 0, functionid = INDEX_FILL12)
    private void fill12() {
    }

    @MethodGridEntity(btnnameid = R.string.tv_printer_get_print_util, functionid = INDEX_GET_PRINT_UTILS, btnimageid = 1)
    private void getPrintScriptUtil() {
        try {
            printScriptUtil = printerModule.getPrintScriptUtil(context);
            showMessage(context.getString(R.string.msg_get_print_util_complete) + "\n", MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error) + e, MessageTag.ERROR);
        }

    }

    @MethodGridEntity(btnnameid = R.string.tv_printer_util_add_font, functionid = INDEX_UTILS_ADD_FONT, btnimageid = 2)
    private void addFont() {
        try {
            if (printScriptUtil == null) {
                showMessage(context.getString(R.string.msg_get_print_util_first) + "\n", MessageTag.ERROR);
                return;
            }
            //pls add the font file in assets folder.
            String name = "simsun.ttc";
            printScriptUtil.addFont(context, name);
            showMessage(context.getString(R.string.msg_print_util_add_font_complete) + "\n", MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error) + e, MessageTag.ERROR);

        }

    }

    @MethodGridEntity(btnnameid = R.string.tv_printer_util_add_text, functionid = INDEX_UTILS_ADD_TEXT, btnimageid = 3)
    private void addText() {
        try {
            if (printScriptUtil == null) {
                showMessage(context.getString(R.string.msg_get_print_util_first) + "\n", MessageTag.ERROR);
                return;
            }
            TextFormat format = new TextFormat();
            format.setAlignment(Alignment.LEFT);
            format.setFontScale(FontScale.ORINARY);
            format.setZhFontSize(ZhFontSize.FONT_24x24);
            format.setEnFontSize(EnFontSize.FONT_8x16);
            format.setLinefeed(true);
            String text = context.getString(R.string.msg_print_util_text_specific);
            printScriptUtil.addText(format, text);

            format = new TextFormat();
            format.setAlignment(Alignment.LEFT);
            format.setFontScale(FontScale.ORINARY);
            format.setLinefeed(false);
            String text1 = context.getString(R.string.msg_print_util_text_left_line_false);
            printScriptUtil.addText(format, text1);

            format = new TextFormat();
            format.setAlignment(Alignment.CENTER);
            format.setFontScale(FontScale.ORINARY);
            format.setLinefeed(false);
            String text2 = context.getString(R.string.msg_print_util_text_center_line_false);
            printScriptUtil.addText(format, text2);

            format = new TextFormat();
            format.setAlignment(Alignment.RIGHT);
            format.setFontScale(FontScale.ORINARY);
            format.setLinefeed(true);
            String text3 = context.getString(R.string.msg_print_util_text_right_line_true);
            printScriptUtil.addText(format, text3);

            format = new TextFormat();
            format.setAlignment(Alignment.CENTER);
            format.setFontScale(FontScale.DOUBLE_HORIZONTAL_MAGNIFY);
            format.setLinefeed(true);
            String text4 = context.getString(R.string.msg_print_util_text_center_line_true);
            printScriptUtil.addText(format, text4);
            format.setSpaceScale(SpaceScale.ZOOM);
            printScriptUtil.addText(format, text4);


            format = new TextFormat();
            format.setAlignment(Alignment.LEFT);
            format.setFontSize(FontSize.SMALL);
            String text5 = context.getString(R.string.msg_print_util_text_left_samll);
            printScriptUtil.addText(format, text5);

            format = new TextFormat();
            format.setAlignment(Alignment.LEFT);
            format.setFontSize(FontSize.NORMAL);
            String text6 = context.getString(R.string.msg_print_util_text_left_normal);
            printScriptUtil.addText(format, text6);

            format = new TextFormat();
            format.setAlignment(Alignment.LEFT);
            format.setFontSize(FontSize.LARGE);
            String text7 = context.getString(R.string.msg_print_util_text_left_large);
            printScriptUtil.addText(format, text7);

            format = new TextFormat();
            format.setAlignment(Alignment.LEFT);
            format.setFontSize(FontSize.SUPER_LARGE);
            String text8 = context.getString(R.string.msg_print_util_text_left_super_large);
            printScriptUtil.addText(format, text8);

            format = new TextFormat();
            format.setAlignment(Alignment.CENTER);
            format.setFontSize(FontSize.NORMAL);
            format.setUnderline(true);
            String text9 = context.getString(R.string.msg_print_util_text_center_underline);
            printScriptUtil.addText(format, text9);

            format = new TextFormat();
            format.setAlignment(Alignment.LEFT);
            format.setFontScale(FontScale.ORINARY);
            format.setLinefeed(false);
            format.setUnderline(true);
            String text10 = context.getString(R.string.msg_print_util_text_left_underline);
            printScriptUtil.addText(format, text10);

            format = new TextFormat();
            format.setAlignment(Alignment.CENTER);
            format.setFontScale(FontScale.ORINARY);
            format.setLinefeed(false);
            String text11 = context.getString(R.string.msg_print_util_text_center);
            printScriptUtil.addText(format, text11);

            format = new TextFormat();
            format.setAlignment(Alignment.RIGHT);
            format.setFontScale(FontScale.ORINARY);
            format.setLinefeed(true);
            format.setUnderline(true);
            String text12 = context.getString(R.string.msg_print_util_text_right_underline);
            printScriptUtil.addText(format, text12);


            showMessage(context.getString(R.string.msg_print_util_add_text_complete) + "\n", MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error) + e, MessageTag.ERROR);

        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_printer_util_add_image, functionid = INDEX_UTILS_ADD_IMAGE, btnimageid = 4)
    private void addImage() {
        try {
            if (printScriptUtil == null) {
                showMessage(context.getString(R.string.msg_get_print_util_first) + "\n", MessageTag.ERROR);
                return;
            }
            ImageFormat format = new ImageFormat();
            format.setOffset(0);
            format.setWidth(370);
            format.setHeight(80);
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.print_logo);
            printScriptUtil.addImage(format, bitmap);

            format = new ImageFormat();
            format.setOffset(0);
            format.setWidth(370);
            format.setHeight(1200);
            Bitmap bitmap2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.sample_receipt);
            printScriptUtil.addImage(format, bitmap2);

            format = new ImageFormat();
            format.setOffset(0);
            format.setWidth(144);
            format.setHeight(256);
            Bitmap bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.sign);
            printScriptUtil.addImage(format, bitmap1);
            showMessage(context.getString(R.string.msg_print_util_add_imge_complete) + "\n", MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error) + e, MessageTag.ERROR);

        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_printer_util_add_barcode, functionid = INDEX_UTILS_ADD_BARCODE, btnimageid = 5)
    private void addBarcode() {
        try {
            if (printScriptUtil == null) {
                showMessage(context.getString(R.string.msg_get_print_util_first) + "\n", MessageTag.ERROR);
                return;
            }
            BarcodeFormat format = new BarcodeFormat();
            format.setAlignment(Alignment.LEFT);
            format.setWidth(2);
            format.setHeight(64);
            String BARCODE = "12345678";
            printScriptUtil.addBarcode(format, BARCODE);

            format = new BarcodeFormat();
            format.setAlignment(Alignment.CENTER);
            format.setWidth(8);
            format.setHeight(120);
            String longBarcode = "1234567812345678123456781234567812345678";
            printScriptUtil.addBarcode(format, longBarcode);

            format = new BarcodeFormat();
            format.setAlignment(Alignment.CENTER);
            format.setWidth(4);
            format.setHeight(100);
            format.setBarcodeEncode(BarcodeEncode.EAN);
            format.setBelowShown(true);
            String barcode1 = "12345678";
            printScriptUtil.addBarcode(format, barcode1);
            showMessage(context.getString(R.string.msg_print_util_add_barcode_complete) + "\n", MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error) + e, MessageTag.ERROR);

        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_printer_util_add_twobarcode, functionid = INDEX_UTILS_ADD_TWOBARCODE, btnimageid = 6)
    private void addTwoBarcode() {
        try {
            if (printScriptUtil == null) {
                showMessage(context.getString(R.string.msg_get_print_util_first) + "\n", MessageTag.ERROR);
                return;
            }
            TwoDimensionCodeFormat format = new TwoDimensionCodeFormat();
            format.setAlignment(Alignment.LEFT);
            format.setTwoDimensionCodeEncode(TwoDimensionCodeEncode.DATAMATRIX);
            String code = "12345678";
            printScriptUtil.addTwoDimensionCode(format, code);

            format = new TwoDimensionCodeFormat();
            format.setAlignment(Alignment.CENTER);
            String code1 = "1234567812345678123456781234567812345678";
            printScriptUtil.addTwoDimensionCode(format, code1);
            showMessage(context.getString(R.string.msg_print_util_add_twobarcode_complete) + "\n", MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error) + e, MessageTag.ERROR);

        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_printer_util_add_feedline, functionid = INDEX_UTILS_ADD_FEEDLINE, btnimageid = 7)
    private void addFeedLine() {
        try {
            if (printScriptUtil == null) {
                showMessage(context.getString(R.string.msg_get_print_util_first) + "\n", MessageTag.ERROR);
                return;
            }
            int lineNum = 2;
            printScriptUtil.addPaperFeed(lineNum);
            showMessage(context.getString(R.string.msg_print_util_add_feedlind_complete) + "," + lineNum + "\n", MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error) + e, MessageTag.ERROR);

        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_printer_util_set_gray, functionid = INDEX_UTILS_SET_GRAY, btnimageid = 8)
    private void setGray() {
        try {
            if (printScriptUtil == null) {
                showMessage(context.getString(R.string.msg_get_print_util_first) + "\n", MessageTag.ERROR);
                return;
            }
            String[] grays = new String[10];
            for (int i = 0; i < grays.length; i++) {
                grays[i] = String.valueOf(i + 1);
            }
            DialogUtils.createSingleChoiceDialog(context, context.getString(R.string.tv_printer_util_set_gray), grays, new DialogUtils.SingleChoiceDialogCallback() {
                @Override
                public void onResult(int id) {
                    int gray = id + 1;
                    printScriptUtil.setGray(gray);
                    showMessage(context.getString(R.string.msg_print_util_set_gray_complete) + "," + gray + "\n", MessageTag.DATA);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error) + e, MessageTag.ERROR);

        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_printer_util_set_sapce, functionid = INDEX_UTILS_SET_SAPCE, btnimageid = 9)
    private void setSpace() {
        try {
            if (printScriptUtil == null) {
                showMessage(context.getString(R.string.msg_get_print_util_first) + "\n", MessageTag.ERROR);
                return;
            }
            String[] spaces = new String[61];
            for (int i = 0; i < spaces.length; i++) {
                spaces[i] = String.valueOf(i);
            }
            DialogUtils.createSingleChoiceDialog(context, context.getString(R.string.tv_printer_util_set_sapce), spaces, new DialogUtils.SingleChoiceDialogCallback() {
                @Override
                public void onResult(int id) {
                    int space = id;
                    printScriptUtil.setLineSpacing(space);
                    showMessage(context.getString(R.string.msg_print_util_set_sapce_complete) + "," + space + "\n", MessageTag.DATA);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error) + e, MessageTag.ERROR);

        }
    }
    @MethodGridEntity(btnnameid = R.string.tv_printer_util_add_dotted_line, functionid = INDEX_UTILS_ADD_DOTTED_LINE, btnimageid = 10)
    private void addDottedLine() {
        try {
            if (printScriptUtil == null) {
                showMessage(context.getString(R.string.msg_get_print_util_first) + "\n", MessageTag.ERROR);
                return;
            }
            printScriptUtil.addDottedLine();
            showMessage(context.getString(R.string.msg_print_util_add_dotted_line_complete), MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error) + e, MessageTag.ERROR);

        }
    }
    @MethodGridEntity(btnnameid = R.string.tv_printer_util_print, functionid = INDEX_UTILS_PRINT, btnimageid = 11)
    private void printByUtils() {
        try {
            if (printScriptUtil == null) {
                showMessage(context.getString(R.string.msg_get_print_util_first) + "\n", MessageTag.ERROR);
                return;
            }
            printScriptUtil.print(new PrintListener() {
                @Override
                public void onSuccess() {
                    showMessage(context.getString(R.string.msg_print_script_success) + "\n", MessageTag.DATA);
                }

                @Override
                public void onError(ErrorCode error, String msg) {
                    showMessage(context.getString(R.string.msg_print_script_fail) + context.getString(R.string.msg_print_script_fail_error_code) + error + ", " + context.getString(R.string.msg_print_script_fail_error_msg) + msg + "\n", MessageTag.ERROR);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error) + e, MessageTag.ERROR);

        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_printer_set_listener, functionid = INDEX_PAPER_LISTENER, btnimageid = 11)
    private void setStatusListener() {
        try {
            printerModule.setStatusListener(new PrinterStatusListener() {
                @Override
                public void onStatus(PrinterStatus status) {
                    showMessage("PrinterStatus:"+status, MessageTag.ERROR);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error) + e, MessageTag.ERROR);

        }
    }

    @MethodGridEntity(btnname="printByNDK", functionid = 23, btnimageid = 11)
    private void printByNDK() {
        try {
            StringBuffer scriptBuffer = new StringBuffer();
                scriptBuffer.append("!gray 7\n!yspace 5\n");
                scriptBuffer.append("!hz s\n!asc s\n");
                scriptBuffer.append("*text l 商户存根(MERCHANT COPY)\n");
                scriptBuffer.append("!hz n\n!asc n\n*text l POS签购单(POS SALES SLIP):\n");
                scriptBuffer.append("!hz s\n!asc s\n*text l 商户名(MERCHANT NAME):\n");
                scriptBuffer.append("!hz n\n!asc n\n");
                scriptBuffer.append("*text l Newland Payment Technology（Test）\n");
                scriptBuffer.append("!hz s\n!asc s\n*text l 商户号(MERCHANT NO):\n");
                scriptBuffer.append("!hz n\n!asc n\n*text l   105000853999787 #0103\n");
                scriptBuffer.append("!hz s\n!asc s\n*text l 终端号(TERMINAL NO):\n");
                scriptBuffer.append("!hz n\n!asc n\n*text l   10131908\n");
                scriptBuffer.append("!hz s\n!asc s\n*text l 操作员号(OPERATOR NO):01\n");
                scriptBuffer.append("!hz s\n!asc s\n*text l 发卡行(ISSUER):工商银行\n");
                scriptBuffer.append("!hz s\n!asc s\n*text l 收单行(ACQUIRER):建设银行\n");
                scriptBuffer.append("!hz s\n!asc s\n*text l 卡号(CARD NO):\n");
                scriptBuffer.append("!hz s\n!asc s\n*text l   6214 8360 0111 3147 (C)\n");
                scriptBuffer.append("!hz s\n!asc s\n*text l     SN: 000\n");
                scriptBuffer.append("!hz s\n!asc s\n*text l 交易类型(TRANS TYPE):\n");
                scriptBuffer.append("!hz n\n!asc n\n*text l   预授权/AUTH\n");
                scriptBuffer.append("!hz s\n!asc s\n*text l 批次号(BATCH NO):000001\n");
                scriptBuffer.append("!hz n\n!asc n\n*text l 凭证号(VOUCHER NO):003006\n");
                scriptBuffer.append("!hz n\n!asc n\n*text l 参考号(REF NO):091411322219\n");
                scriptBuffer.append("!hz n\n!asc n\n*text l 授权码(AUTH NO):563719\n");
                scriptBuffer.append("!hz n\n!asc n\n*text l 交易日期(DATE):2021/09/14\n");
                scriptBuffer.append("!hz s\n!asc s\n*text l 交易时间(TIME):11:32:22\n");
                scriptBuffer.append("!hz s\n!asc s\n*text l 有效期(EXP.DATE):2028/06\n");
                scriptBuffer.append("!hz n\n!asc n\n*text l 金额(AMOUNT):RMB 0.44\n");
                scriptBuffer.append("!hz s\n!asc s\n*text l 备注(REFERENCE):\n");
                scriptBuffer.append("!hz s\n!asc s\n*text l TC:8A0EB2B79FFD3888 ATC:ED6A\n");
                scriptBuffer.append("!hz s\n!asc s\n*text l TVR:0000000000      TSI:0000\n");
                scriptBuffer.append("!hz s\n!asc s\n*text l AID:A000000333010101\n");
                scriptBuffer.append("!hz s\n!asc s\n*text l APP LABEL:PBOC DEBIT\n");
                scriptBuffer.append("!hz s\n!asc s\n*text l 本人确认以上交易,同意将其记入本卡帐户\n");
                scriptBuffer.append("!hz s\n!asc s\n*text l I ACKNOWLEDGE SATISFACTORY RECEIPT OF\n");
                scriptBuffer.append("!hz s\n!asc s\n*text l RELATIVE GOODS/SERVICE\n *feedline 1\n");
                scriptBuffer.append("!hz s\n!asc s\n*text l 此金额未超过1000.00元,无需签名\n");
                scriptBuffer.append("!hz s\n!asc s\n*text l NO SIGNATURE REQUIRED\n*feedline 1\n");
                scriptBuffer.append("!hz s\n!asc s\n*text l 持卡人签名(CARDHOLDER SIGNATURE)\n!NLFONT 1 12 3\n*text l 重打印凭证/DUPLICATED\n");
                scriptBuffer.append("!hz s\n!asc s\n*text l 终端版本号(VER):00001132210501(v4.1.0)\n");
                scriptBuffer.append("!hz s\n!asc s\n*text l 终端序列号(TUSN):00000304N7NL01894249\n*feedline 1\n");
                scriptBuffer.append("*feedline 1\n");
                scriptBuffer.append(" *feedline 6\n");

            Map<String, Bitmap> map = new HashMap<String, Bitmap>();
            Bitmap bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.fffffffff);
            String bmp0 = "bmp0", bmp1 = "bmp1", bmp2 = "bmp2", bmp3 = "bmp3", bmp4 = "bmp4", bmp5 = "bmp5";
            map.put(bmp0, bitmap1);
            map.put(bmp1, bitmap1);
            map.put(bmp2, bitmap1);
            map.put(bmp3, bitmap1);
            map.put(bmp4, bitmap1);
            map.put(bmp5, bitmap1);
            scriptBuffer.append("*line\n!yspace 5\n");
            scriptBuffer.append("*image l 200*200 path:" + bmp0 + "\n");//Otsu threshold method is used to print, left print
            scriptBuffer.append("*image l 200*200 path:yz:240;" + bmp1 + "\n");//Set fixed threshold to 240, print in left [1-254]
            scriptBuffer.append("*image l 200*200 path:yz:128;" + bmp2 + "\n");//Set fixed threshold to 128, print in left [1-254]
            scriptBuffer.append("*image l 200*200 path:yz:68;" + bmp3 + "\n");//Set fixed threshold to 68, print in right [1-254]
            scriptBuffer.append("*image l 200*200 path:yz:0;" + bmp4 + "\n");//WellnerAdaptiveThreshold method is used to print, left print

            printerModule.printScriptByNDK(scriptBuffer.toString(), map, new PrintListener() {
                @Override
                public void onSuccess() {
                    showMessage("print sucess");
                }

                @Override
                public void onError(ErrorCode error, String msg) {
                    showMessage("print onError,errorcode:"+error+"msg:"+msg);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error) + e, MessageTag.ERROR);
        }
    }

}