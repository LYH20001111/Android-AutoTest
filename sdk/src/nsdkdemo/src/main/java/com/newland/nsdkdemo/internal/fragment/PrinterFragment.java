package com.newland.nsdkdemo.internal.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.internal.printer.Printer;
import com.newland.nsdk.core.api.internal.printer.PrinterStatus;
import com.newland.nsdk.core.api.internal.printer.PrintingParameters;
import com.newland.nsdk.core.api.internal.printer.PrintingResultListener;
import com.newland.nsdk.core.api.internal.printer.PrnParams;
import com.newland.nsdk.core.internal.jni.NSDKJni;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.annotation.MethodGridEntity;
import com.newland.nsdkdemo.common.utils.DialogUtils;
import com.newland.nsdkdemo.common.utils.FileUtils;
import com.newland.nsdkdemo.common.utils.MessageTag;
import com.newland.nsdkdemo.internal.print.PrintContentUtil;
import com.newland.nsdkdemo.internal.print.generator.VoucherPrintItemGenerator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PrinterFragment extends InternalBaseFragment {

    private Printer mPrinter;
    private static final int INDEX_PRINT_STATUS = 1;
    private static final int INDEX_PRINT_SCRIPT = 2;
    private static final int INDEX_PRINT_HIGHER_EFFECT_SCRIPT = 3;
    private static final int INDEX_PRINT_SCRIPT_RGBA = 4;
    private static final int INDEX_PRINT_SCRIPT_RGBA_COMPRESS = 5;
    private static final int INDEX_PRINT_SCRIPT_CUT = 6;
    private static final int INDEX_PRINT_SCRIPT_SET_GRAY = 7;
    private static final int INDEX_PRINT_SCRIPT_PDF = 8;
    private static final int INDEX_PRINT_FEED_PAPER = 9;
    private static final int PAPER_WIDTH_NORMAL = 384;
    private static final int PAPER_WIDTH_LARGE = 576;

    public PrinterFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_printer_f);
    }

    @Override
    public void initData() {
        mPrinter = (Printer) moduleManager.getModule(ModuleType.PRINTER);
    }

    @Override
    public Object getModule() {
        return PrinterFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.status, functionid = INDEX_PRINT_STATUS)
    private void getPrnStatus() {
        PrinterStatus status = null;
        try {
            status = mPrinter.getStatus();
            showMessage("Printer status is: " + status);
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "get printer status");
        }
    }

    @MethodGridEntity(btnnameid = R.string.msg_print_by_ttfscript, functionid = INDEX_PRINT_SCRIPT)
    private void printByScript() {
        long startTime = System.currentTimeMillis();
        byte[] bitmap = PrintContentUtil.getInstance().getPrintImage(context);
        int width = PrintContentUtil.getInstance().getWidth();
        int height = PrintContentUtil.getInstance().getHeight();
        try {
            mPrinter.printImage(bitmap, 0, width, height, new PrintingResultListener() {

                @Override
                public void onEventRaised(int status) {
                    showMessage(String.format(">>>Print event, status=%d, time=%d", status, System.currentTimeMillis()-startTime));
                }
            });
        } catch (NSDKException e) {
            showErrorMessage(e, "print");
        }
    }

    @MethodGridEntity(btnnameid = R.string.msg_print_by_ttfscript_advanced, functionid = INDEX_PRINT_HIGHER_EFFECT_SCRIPT)
    private void advancedPrintByScript() {
        long startTime = System.currentTimeMillis();
        Bitmap bitmap = VoucherPrintItemGenerator.getInstance().getTemplateVoucher(context);
        PrintingParameters parameters = new PrintingParameters();
        parameters.setStartX(0);
        parameters.setExpectedImageWidth(bitmap.getWidth());
        parameters.setExpectedImageHeight(bitmap.getHeight());
        try {
            mPrinter.printImage(bitmap, parameters, new PrintingResultListener() {

                @Override
                public void onEventRaised(int status) {
                    showMessage(String.format(">>>Print event, status=%d, time=%d", status, System.currentTimeMillis()-startTime));
                }
            });
        } catch (NSDKException e) {
            showErrorMessage(e, "print");
        }
    }

    @MethodGridEntity(btnnameid = R.string.msg_print_by_ttfscript_rgba, functionid = INDEX_PRINT_SCRIPT_RGBA)
    private void printRGBAByScript() {
        long startTime = System.currentTimeMillis();
        byte[] bitmap = PrintContentUtil.getInstance().getPrintRGBAImage(context);
        int width = PrintContentUtil.getInstance().getWidth();
        int height = PrintContentUtil.getInstance().getHeight();

        PrintingParameters parameters = new PrintingParameters();
        parameters.setStartX(0);
        parameters.setImageWidth(width);
        parameters.setImageHeight(height);
        parameters.setExpectedImageWidth(width);
        parameters.setExpectedImageHeight(height);

        try {
            mPrinter.printRGBAImage(bitmap, parameters, new PrintingResultListener() {

                @Override
                public void onEventRaised(int status) {
                    showMessage(String.format(">>>Print event, status=%d, time=%d", status, System.currentTimeMillis()-startTime));
                }
            });
        } catch (NSDKException e) {
            showErrorMessage(e, "print RGBA image");
        }
    }

    @MethodGridEntity(btnnameid = R.string.msg_print_by_ttfscript_compress, functionid = INDEX_PRINT_SCRIPT_RGBA_COMPRESS)
    private void printRGBAByScriptCompress() {
        long startTime = System.currentTimeMillis();
        byte[] bitmap = PrintContentUtil.getInstance().getPrintRGBAImage(context);
        int width = PrintContentUtil.getInstance().getWidth();
        int height = PrintContentUtil.getInstance().getHeight();

        PrintingParameters parameters = new PrintingParameters();
        parameters.setStartX(0);
        parameters.setImageWidth(width);
        parameters.setImageHeight(height);
        parameters.setExpectedImageWidth(width/2);
        parameters.setExpectedImageHeight(height/2);

        try {
            mPrinter.printRGBAImage(bitmap, parameters, new PrintingResultListener() {

                @Override
                public void onEventRaised(int status) {
                    showMessage(String.format(">>>Print event, status=%d, time=%d", status, System.currentTimeMillis()-startTime));
                }
            });
        } catch (NSDKException e) {
            showErrorMessage(e, "print RGBA image(compress)");
        }
    }
    @MethodGridEntity(btnnameid = R.string.msg_print_by_ttfscript_cut, functionid = INDEX_PRINT_SCRIPT_CUT)
    private void printCutByScriptCompress() {
        try {
            mPrinter.cutPaper();
            showMessage("Printer cut paper");
        } catch (NSDKException e) {
          showErrorMessage(e, "print cutPaper(compress)");
        }
    }

    @MethodGridEntity(btnnameid = R.string.msg_print_by_ttfscript_set_gray, functionid = INDEX_PRINT_SCRIPT_SET_GRAY)
    private void printSetGrayByScriptCompress() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.msg_print_by_ttfscript_set_gray), null, R.layout.dialog_printer_gray, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                EditText etGray = dialogView.findViewById(R.id.et_set_gray);
                int gray;
                try {
                    gray = Integer.parseInt(etGray.getText().toString());
                } catch (Exception e) {
                    showMessage("Invalid gray value. Its value range is [1, 10]", MessageTag.ERROR);
                    return;
                }
                try {
                    mPrinter.setGray(gray);
                    showMessage("Printer set gray: " + gray);
                } catch (NSDKException e) {
                    showErrorMessage(e, "set gray(compress)");
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.msg_print_by_ttfscript_pdf, functionid = INDEX_PRINT_SCRIPT_PDF)
    private void printPDF() {
        setDefaultPDFToSDCardDir("receipt_with_image.pdf");
        setDefaultPDFToSDCardDir("receipt_without_image.pdf");
        setDefaultPDFToSDCardDir("Nexxchange_receipt.pdf");
        DialogUtils.createCustomDialog(context, R.string.msg_print_by_ttfscript_pdf, null, R.layout.dialog_print_pdf, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                List<String> pdfFiles = new ArrayList<>();
                File sdCardDir = Environment.getExternalStorageDirectory();
                if (sdCardDir != null && sdCardDir.exists()) {
                    findPDFFiles(sdCardDir, pdfFiles);
                }
                Spinner spnPdfFile = view.findViewById(R.id.spn_printPdfFile);
                ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, pdfFiles);
                spnPdfFile.setAdapter(adapter);
            }

            @Override
            public void onResult(int id, View view) {
                try {
                    PrnParams params = new PrnParams();
                    params.setEnableHighQualityMode(true);
                    mPrinter.setParams(params);
                } catch (NSDKException e) {
                    e.printStackTrace();
                }
                Spinner spnPdfFile = view.findViewById(R.id.spn_printPdfFile);
                String path = spnPdfFile.getSelectedItem().toString();
                File file = new File(path);
                if (!file.exists()) {
                    showMessage("No existing target file", MessageTag.ERROR);
                }
                ArrayList<Bitmap> bitmaps = pdfToBitmap(file);
                for (Bitmap bitmap : bitmaps) {
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    long startTime = System.currentTimeMillis();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    File file1 = new File(path.replace("pdf", "png"));
                    if (!file1.exists()) {
                        try {
                            file1.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        file1.setWritable(true);
                        file1.setReadable(true);
                    } else {
                        file1.delete();
                        try {
                            file1.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        file1.setWritable(true);
                        file1.setReadable(true);
                    }
                    FileOutputStream fileOutputStream = null;
                    try {
                        fileOutputStream = new FileOutputStream(file1);
                        fileOutputStream.write(baos.toByteArray());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        mPrinter.printImage(baos.toByteArray(), 0, width, height, new PrintingResultListener() {
                            @Override
                            public void onEventRaised(int status) {
                                showMessage(String.format(">>>Print event, status=%d, time=%d", status, System.currentTimeMillis() - startTime));
                            }
                        });
                    } catch (NSDKException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.msg_print_feed_paper, functionid = INDEX_PRINT_FEED_PAPER)
    private void feedPaper() {
        try {
            mPrinter.feedPaper();
            showMessage(context.getString(R.string.msg_print_feed_paper));
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }
    }

    private ArrayList<Bitmap> pdfToBitmap(File pdfFile) {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY));
                Bitmap bitmap;
                final int pageCount = renderer.getPageCount();
                Log.e("PrintFragment", "pageCount： " +pageCount);
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
            e.getMessage();
        }
        return bitmaps;
    }

    //Zoom the bitmap to the most available size.
    private Bitmap scaleBitmap(Bitmap origin) {
        if (origin == null) {
            return null;
        }
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int widthPixels = dm.widthPixels;
        int width = origin.getWidth();
        int height = origin.getHeight();
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

    private void findPDFFiles(File dir, List<String> pdfFiles) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        findPDFFiles(file, pdfFiles);
                    } else if (file.getName().toLowerCase().endsWith(".pdf")) {
                        pdfFiles.add(file.getAbsolutePath());
                    }
                }
            }
        }
    }

    private void setDefaultPDFToSDCardDir(String fileName) {
        try {
            File file = new File(Environment.getExternalStorageDirectory() + "/" + fileName);
            if (file.exists()) {
               file.delete();
            }
            file.createNewFile();
            file.setWritable(true);
            file.setReadable(true);
            byte[] pdfData = FileUtils.readPicDataFromAssets(context, "pdf/" + fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(pdfData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
