package com.newland.sdk.me.module.printer;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.newland.sdk.me.module.printerPro.appimpl.internal.PrinterHelper;
import com.newland.sdk.module.printer.SpaceScale;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.module.printer.Alignment;
import com.newland.sdk.module.printer.BarcodeEncode;
import com.newland.sdk.module.printer.BarcodeFormat;
import com.newland.sdk.module.printer.EnFontSize;
import com.newland.sdk.module.printer.ErrorCode;
import com.newland.sdk.module.printer.FontScale;
import com.newland.sdk.module.printer.FontSize;
import com.newland.sdk.module.printer.ImageFormat;
import com.newland.sdk.module.printer.PrintListener;
import com.newland.sdk.module.printer.PrintScriptUtil;
import com.newland.sdk.module.printer.QRCodeErrorCorrectionLevel;
import com.newland.sdk.module.printer.TextFormat;
import com.newland.sdk.module.printer.TwoDimensionCodeEncode;
import com.newland.sdk.module.printer.TwoDimensionCodeFormat;
import com.newland.sdk.module.printer.ZhFontSize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The implements of print Script Util
 *
 * @author linsi
 * @since V3.10.01
 */
public class MEPrintScriptUtil implements PrintScriptUtil {
    private DeviceLogger deviceLogger = DeviceLoggerFactory.getLogger(MEPrintScriptUtil.class);
    private static MEPrintScriptUtil instance;
    private List<String> scriptList = new ArrayList<String>();
    private MEPrinter MEPrinter;
    private Context context;
    private String fontsPath;
    private Map<String, Bitmap> imgMap = new HashMap<String, Bitmap>();
    private int bitmapName = 1;

    private MEPrintScriptUtil(MEPrinter MEPrinter, Context context) {
        this.context = context;
        this.MEPrinter = MEPrinter;
    }

    public static MEPrintScriptUtil getInstance(MEPrinter MEPrinter, Context context) {
        if (instance == null) {
            synchronized (MEPrintScriptUtil.class) {
                if (instance == null) {
                    Log.d("SDK MEPrintScriptUtil","instance is null.");
                    instance = new MEPrintScriptUtil(MEPrinter, context);
                }
            }
        }
        Log.d("SDK MEPrintScriptUtil","instance:" + instance);
        return instance;
    }

    @Override
    public void addText(TextFormat textFormat, String data) {
        try {
            if (data == null) {
                deviceLogger.error("addText, data is null.");
                return;
            }
            TextFormat format = new TextFormat();
            if (textFormat == null) { // default value
                format.setAlignment(Alignment.LEFT);
                format.setEnFontSize(EnFontSize.UN_VALUED);
                format.setZhFontSize(ZhFontSize.UN_VALUED);
                format.setFontSize(FontSize.NORMAL);
                format.setFontScale(FontScale.ORINARY);
                format.setLinefeed(true);
                format.setUnderline(false);
                format.setSpaceScale(SpaceScale.NORMAL);

            } else {
                format.setAlignment(textFormat.getAlignment());
                format.setEnFontSize(textFormat.getEnFontSize());
                format.setZhFontSize(textFormat.getZhFontSize());
                format.setFontSize(textFormat.getFontSize());
                format.setFontScale(textFormat.getFontScale());
                format.setLinefeed(textFormat.isLinefeed());
                format.setUnderline(textFormat.isUnderline());
                format.setSpaceScale(textFormat.getSpaceScale());
                format.setStrikethrough(textFormat.isStrikethrough());
                format.setSpaceSizeConversion(textFormat.isSpaceSizeConversion());
            }

            StringBuilder printText = new StringBuilder();
            int englishFontSize = format.getEnFontSize().getSize();
            int zhFontSize = format.getZhFontSize().getSize();
            int scale = format.getFontScale().getScale();
            int spaceScale = format.getSpaceScale().getSpaceScale();
            String fontSize;
            String scriptText;
            if (format.getEnFontSize() == EnFontSize.UN_VALUED) {
                switch (format.getFontSize()) {
                    case SMALL:
                        englishFontSize = EnFontSize.FONT_8x16.getSize();
                        break;
                    case NORMAL:
                        englishFontSize = EnFontSize.FONT_12x24A.getSize();
                        break;
                    case LARGE:
                        englishFontSize = EnFontSize.FONT_16x32A.getSize();
                        break;
                    case SUPER_LARGE:
                        englishFontSize = EnFontSize.FONT_16x32A.getSize();
                        scale = 0;
                        break;
                    default:
                        englishFontSize = EnFontSize.FONT_12x24A.getSize();
                        break;
                }
            }
            if (format.getZhFontSize() == ZhFontSize.UN_VALUED) {
                switch (format.getFontSize()) {
                    case SMALL:
                        zhFontSize = ZhFontSize.FONT_16x16.getSize();
                        break;
                    case NORMAL:
                        zhFontSize = ZhFontSize.FONT_24x24.getSize();
                        break;
                    case LARGE:
                        zhFontSize = ZhFontSize.FONT_32x32.getSize();
                        break;
                    case SUPER_LARGE:
                        zhFontSize = ZhFontSize.FONT_32x32.getSize();
                        scale = 0;
                        break;
                    default:
                        zhFontSize = ZhFontSize.FONT_24x24.getSize();
                        break;
                }
            }
            deviceLogger.debug("-----zhFontSize:"+zhFontSize+";englishFontSize:"+englishFontSize+";spaceScale");
            fontSize = "!NLFONT " + zhFontSize + " " + englishFontSize + " " + scale + " "+spaceScale+ "\n";
            printText.append(fontSize);
            if(!(format.isLinefeed()&&PrinterHelper.isEnablePrinterPro)){
                data = data.replaceAll("\n", "\r");
                if ("".equals(data.trim())) {
                    data = "\r";
                }
            }
            String textCmd = "*text";
            if (data.equals("\r")) {
                textCmd = "*feedline";
                String p = "p:16";
                switch (format.getFontSize()) {
                    case SMALL:
                        p = "p:16";
                        break;
                    case NORMAL:
                        p = "p:24";
                        break;
                    case LARGE:
                        p = "p:32";
                        break;
                    case SUPER_LARGE:
                        p = "p:40";
                        break;
                    default:
                        p = "p:24";
                        break;
                }
                scriptText = textCmd + " " + p + "\n";
            } else {
                if (format.isLinefeed()) {
                    textCmd = "*text";
                    if(format.isStrikethrough()){
                        textCmd = "*strikethrough";
                    } else if (format.isUnderline()) {
                        textCmd = "*underline";
                    }
                } else {
                    textCmd = "*TEXT";
                    if(format.isStrikethrough()){
                        textCmd = "*STRIKETHROUGH";
                    } else if (format.isUnderline()) {
                        textCmd = "*UNDERLINE";
                    }
                }
                switch (format.getAlignment()) {
                    case LEFT:
                        scriptText = textCmd + " l" + " " + data + "\n";
                        break;
                    case CENTER:
                        scriptText = textCmd + " c" + " " + data + "\n";
                        break;
                    case RIGHT:
                        scriptText = textCmd + " r" + " " + data + "\n";
                        break;
                    default:
                        scriptText = textCmd + " l" + " " + data + "\n";
                        break;
                }

            }
            printText.append(scriptText);

            if(format.isSpaceSizeConversion()){
                printText.append("!SPACECHAR 0\n");
            }else {
                printText.append("!SPACECHAR 1\n");
            }

            if(PrinterHelper.isEnablePrinterPro&&format.isLinefeed()&&data.contains("\n")){
                Log.d("[][]", "addText: 111");
                String[] scripts = data.split("\\n");
                for (int i = 0; i < scripts.length; i++) {
                    deviceLogger.debug("addScript script=" + scripts[i]);
                    addText(format,scripts[i]);
                }
            }else {
                scriptList.add(printText.toString());
                deviceLogger.debug("addText: " + printText.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void addImage(ImageFormat imageFormat, Bitmap bitmap) {
        try {
            if (bitmap == null) {
                deviceLogger.error("addImage, bitmap is null.");
                return;
            }
            ImageFormat format = new ImageFormat();
            if (imageFormat == null) { // default value
                format.setAlignment(Alignment.LEFT);
                format.setHeight(bitmap.getHeight());
                format.setWidth(bitmap.getWidth());
                format.setOffset(0);
            } else {
                format.setAlignment(imageFormat.getAlignment());
                format.setHeight(imageFormat.getHeight());
                format.setWidth(imageFormat.getWidth());
                format.setOffset(imageFormat.getOffset());
            }

            StringBuilder printText = new StringBuilder();
            bitmapName += 1;
            imgMap.put(String.valueOf(bitmapName), bitmap);
            if (format.getOffset() > 0) {
                printText.append("*image x:").append(format.getOffset()).append(" ").append(format.getWidth()).append("*").append(format.getHeight()).append(" path:").append(String.valueOf(bitmapName)).append("\n");
            } else {
                switch (format.getAlignment()) {
                    case LEFT:
                        printText.append("*image l").append(" ").append(format.getWidth()).append("*").append(format.getHeight()).append(" path:").append(String.valueOf(bitmapName)).append("\n");
                        break;
                    case CENTER:
                        printText.append("*image c").append(" ").append(format.getWidth()).append("*").append(format.getHeight()).append(" path:").append(String.valueOf(bitmapName)).append("\n");
                        break;
                    case RIGHT:
                        printText.append("*image r").append(" ").append(format.getWidth()).append("*").append(format.getHeight()).append(" path:").append(String.valueOf(bitmapName)).append("\n");
                        break;
                    default:
                        printText.append("*image l").append(" ").append(format.getWidth()).append("*").append(format.getHeight()).append(" path:").append(String.valueOf(bitmapName)).append("\n");
                        break;
                }

            }
            deviceLogger.debug("addImage:" + printText.toString());
            scriptList.add(printText.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addBarcode(BarcodeFormat barcodeFormat, String barcode) {
        try {
            if (barcode == null) {
                deviceLogger.error("addBarcode, bitmap is null.");
                return;
            }
            BarcodeFormat format = new BarcodeFormat();
            if (barcodeFormat == null) { // default value
                format.setAlignment(Alignment.LEFT);
                format.setHeight(64);
                format.setWidth(2);
                format.setBarcodeEncode(BarcodeEncode.CODE128);
                format.setBelowShown(false);
            } else {
                format.setAlignment(barcodeFormat.getAlignment());
                format.setHeight(barcodeFormat.getHeight());
                format.setWidth(barcodeFormat.getWidth());
                format.setBarcodeEncode(barcodeFormat.getBarcodeEncode());
                format.setBelowShown(barcodeFormat.isBelowShown());
            }

            StringBuilder printText = new StringBuilder();

            if (format.getWidth() <= 0 || format.getWidth() > 8) {
                format.setWidth(8);
            }
            if (format.getHeight() < 64) {
                format.setHeight(64);
            }
            printText.append("!BARCODE " + format.getWidth() + " " + format.getHeight() + " " + (format.isBelowShown() ? 1 : 0) + " " + format.getBarcodeEncode().getSize() + "\n"); // 3:CODE128.
            switch (format.getAlignment()) {
                case LEFT:
                    printText.append("*BARCODE l" + " " + barcode + "\n");
                    break;
                case RIGHT:
                    printText.append("*BARCODE r" + " " + barcode + "\n");
                    break;
                case CENTER:
                    printText.append("*BARCODE c" + " " + barcode + "\n");
                    break;
                default:
                    printText.append("*BARCODE l" + " " + barcode + "\n");
                    break;
            }
            deviceLogger.debug("addBarcode:" + printText.toString());
            scriptList.add(printText.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addTwoDimensionCode(TwoDimensionCodeFormat twoDimensionCodeFormat, String code) {
        try {
            if (code == null) {
                deviceLogger.error("addTwoDimensionCode, twoBarcode is null.");
                return;
            }
            TwoDimensionCodeFormat format = new TwoDimensionCodeFormat();
            if (twoDimensionCodeFormat == null) { // default value
                format.setAlignment(Alignment.LEFT);
                format.setHeight(100);
                format.setQrCodeErrorCorrectionLevel(QRCodeErrorCorrectionLevel.LEVEL_2ST);
                format.setTwoDimensionCodeEncode(TwoDimensionCodeEncode.QRCODE);
            } else {
                format.setAlignment(twoDimensionCodeFormat.getAlignment());
                format.setHeight(twoDimensionCodeFormat.getHeight());
                format.setOffset(twoDimensionCodeFormat.getOffset());
                format.setQrCodeErrorCorrectionLevel(twoDimensionCodeFormat.getQrCodeErrorCorrectionLevel());
                format.setTwoDimensionCodeEncode(twoDimensionCodeFormat.getTwoDimensionCodeEncode());
            }

            StringBuilder printText = new StringBuilder();
            printText.append("!QRCODE " + format.getHeight() + " " + format.getQrCodeErrorCorrectionLevel().getLevel() + " " + format.getTwoDimensionCodeEncode().getSize() + "\n");
            if (format.getOffset() > 0) {
                printText.append("*QRCODE x:").append(format.getOffset()).append(" " + code + "\n");
            } else {
                switch (format.getAlignment()) {
                    case LEFT:
                        printText.append("*QRCODE l" + " " + code + "\n");
                        break;
                    case RIGHT:
                        printText.append("*QRCODE r" + " " + code + "\n");
                        break;
                    case CENTER:
                        printText.append("*QRCODE c" + " " + code + "\n");
                        break;
                    default:
                        printText.append("*QRCODE l" + " " + code + "\n");
                        break;
                }
            }

            deviceLogger.debug("addTwoDimensionCode:" + printText.toString());
            scriptList.add(printText.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addPaperFeed(int lineNum) {
        try {
            String feedLine = "*feedline " + lineNum + "\n";
            deviceLogger.debug("addPaperFeed:" + feedLine);
            scriptList.add(feedLine);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addDottedLine() {
        try {
            String dottedLine = "*line" + "\n";
            deviceLogger.debug("addDottedLine:" + dottedLine);
            scriptList.add(dottedLine);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setGray(int gray) {
        try {
            String strGray = "!gray" + " " + gray + "\n";
            deviceLogger.debug("setGray:" + strGray);
            scriptList.add(strGray);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setLineSpacing(int space) {
        try {
            String strSpace = "!yspace" + " " + space + "\n";
            deviceLogger.debug("setSpace:" + strSpace);
            scriptList.add(strSpace);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addFont(Context context, String name) {
        try {
            fontsPath = MEPrinter.setFont(context, name);
            if (fontsPath != null) {
                StringBuilder printerText = new StringBuilder();
                printerText.append("!font " + fontsPath + "\n");
                printerText.append("!gray 8\n");  // darken color
                deviceLogger.debug("addFont:" + printerText.toString());
                scriptList.add(printerText.toString());
            } else {
                deviceLogger.error(fontsPath + " is null, but reset it.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override

    public void print(PrintListener printListener) {
        try {
            if (scriptList.size() <= 0) {
                deviceLogger.error("scriptList is empty.");
                printListener.onError(ErrorCode.PARAM_ERROR, ErrorCode.PARAM_ERROR.toString());
                return;
            }
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < scriptList.size(); i++) {
                if (scriptList.get(i) != null) {
                    buffer.append(scriptList.get(i));
                }
            }
            deviceLogger.debug("script data:" + "\n" + buffer.toString());
            this.MEPrinter.print(buffer.toString(), imgMap, printListener);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // reset
            imgMap = new HashMap<String, Bitmap>();
            bitmapName = 1;
            scriptList = new ArrayList<String>();
            if (fontsPath != null) {
                StringBuffer printerText = new StringBuffer();
                printerText.append("!font " + fontsPath + "\n");// set font
                printerText.append("!gray 8\n");  // darken color
                scriptList.add(printerText.toString());
            } else {
                deviceLogger.error("The font path is null.");
            }
        }

    }

    @Override
    public void reverseDisplay(boolean onOff) {
        String flag = (onOff == true ? "on" : "off");
        String reverse = "!reverse" + " " + flag + "\n";
        deviceLogger.debug(reverse);
        scriptList.add(reverse);
    }

    @Override
    public void addPaperCut() {
        try {
            String paperCut = "*cut"  + "\n";
            deviceLogger.debug("addPaperCut:" + paperCut);
            scriptList.add(paperCut);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reset() {
        deviceLogger.debug("[reset]");
        imgMap = new HashMap<String, Bitmap>();
        bitmapName = 1;
        scriptList = new ArrayList<String>();
    }

}
