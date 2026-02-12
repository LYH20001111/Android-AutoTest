package com.newland.nsdkdemo.internal.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.internal.barcodedecoder.BarcodeDecoder;
import com.newland.nsdk.core.api.internal.barcodedecoder.DecodingByteCallback;
import com.newland.nsdk.core.api.internal.barcodedecoder.DecodingCallback;
import com.newland.nsdk.core.api.internal.barcodescanner.BarcodeScanner;
import com.newland.nsdk.core.api.internal.barcodescanner.ScanParameters;
import com.newland.nsdk.core.api.internal.barcodescanner.ScannerType;
import com.newland.nsdk.core.api.internal.devicemanager.DeviceManager;
import com.newland.nsdk.core.api.internal.devicemanager.ScannerConfig;
import com.newland.nsdk.core.api.internal.setting.SettingsManager;
import com.newland.nsdk.core.internal.NSDKModuleManagerImpl;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.AppConfig;
import com.newland.nsdkdemo.common.MainActivity;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.annotation.MethodGridEntity;
import com.newland.nsdkdemo.common.utils.DialogUtils;
import com.newland.nsdkdemo.common.utils.EnumUtils;
import com.newland.nsdkdemo.common.utils.MessageTag;
import com.newland.nsdkdemo.internal.activity.CameraActivity;
import com.newland.nsdkdemo.internal.decode.DecodeUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

public class BarcodeScannerFragment extends InternalBaseFragment {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor mEditor;
    private ScannerConfig scannerConfig;
    private BarcodeDecoder barcodeDecoder;
    private BarcodeScanner barcodeScanner;
    private Bitmap takenPhotoBitmap;
    private final int MESSAGE_DECODING = 101;
    private final int MESSAGE_DECODING_BYTE = 100;
    private final int REQUEST_TAKE_PHOTO = 1;
    private final int REQUEST_BARCODE_SCANNER = 2;
    private final int FRONT_CAMERA = 0;
    private final int BACK_CAMERA = 1;
    private final int PAYMENT_CAMERA = 2;
    private final int HARDWARE_SCANNER = 3;
    private final int PREVIEW_NORMAL = 0;
    private final int PREVIEW_SCAN = 1;
    private final Object waitPhotoTakenLock = new Object();


    public BarcodeScannerFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_scanner_f);
    }

    @Override
    public void initData() {
        sharedPreferences = context.getSharedPreferences(AppConfig.SharedPreferenceConfig.SHARE_PREFERENCE, Context.MODE_PRIVATE);
        mEditor = sharedPreferences.edit();
        DeviceManager deviceManager = (DeviceManager) NSDKModuleManagerImpl.getInstance().getModule(ModuleType.DEVICE_MANAGER);
        try {
            scannerConfig = deviceManager.getDeviceInfo().getScannerConfig();
            barcodeDecoder = (BarcodeDecoder) moduleManager.getModule(ModuleType.BARCODE_DECODER);
            barcodeScanner = (BarcodeScanner) moduleManager.getModule(ModuleType.BARCODE_SCANNER);
        } catch (NSDKException e) {
            e.printStackTrace();
            showMessage(e.getMessage());
        }
    }

    @Override
    public Object getModule() {
        return BarcodeScannerFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.decode_barcode, functionid = 1)
    private void decodeBarcode() {
       Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
       if (takePhotoIntent.resolveActivity(context.getPackageManager()) != null) {
           File fileImage = null;
           try {
                String fileName = "JPEG_" + System.currentTimeMillis() + "_";
                fileImage = File.createTempFile(fileName, ".bmp", context.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
           } catch (Exception e) {
               e.printStackTrace();
           }
           Uri imageURI = null;
           if (fileImage != null) {
                imageURI = FileProvider.getUriForFile(context, "com.newland.nsdkdemo.fileprovider", fileImage);
               ((MainActivity)context).setListener(listener, fileImage);
           }
           takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
           ((Activity)context).startActivityForResult(takePhotoIntent, 1);
       }
       waitPhotoTaken();
        try {
            barcodeDecoder.setDecodingCallback(new DecodingCallback() {
                @Override
                public void onDecodingCallback(int eventCode, String result) {
                    if (eventCode == 1) {
                        showMessage("Decode result: " + result);
                    }
                    showMessage("eventCode: " + eventCode + ", result: " + result);
                    try {
                        barcodeDecoder.stopDecode();
                    } catch (NSDKException e) {
                        e.printStackTrace();
                    }
                }
            });

            if (takenPhotoBitmap == null) {
                return;
            }
            Bitmap resizeBitmap = resizeBimap(takenPhotoBitmap);
            byte[] yuvData = DecodeUtil.convertBitmapToYUV420(resizeBitmap);
            barcodeDecoder.startDecode(yuvData, resizeBitmap.getWidth(), resizeBitmap.getHeight());
            showMessage("Start decoding ....");
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }
    }

    @MethodGridEntity(divtipid = 0, functionid = 2)
    private void fill1(){

    }

    @MethodGridEntity(divtipid = 0, functionid = 3)
    private void fill2() {

    }

    @MethodGridEntity(btnnameid = R.string.front_scan, functionid = 4, btnimageid = 1)
    private void frontCameraScan() {
        DialogUtils.createCustomDialog(context, R.string.front_scan, null, R.layout.dialog_set_scan_params, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                Spinner spnFocusMode = view.findViewById(R.id.spn_FocusMode);
                spnFocusMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.START_FRONT_CAMERA_SCAN_FOCUS_MODE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                spnFocusMode.setSelection(sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.START_FRONT_CAMERA_SCAN_FOCUS_MODE, 0));
                EditText editTimeout = view.findViewById(R.id.edit_scan_timeout);
                editTimeout.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.START_FRONT_CAMERA_SCAN_TIMEOUT, "15000"));
                Switch swIsContinuousScanning = view.findViewById(R.id.sw_isContinuousScanning);
                swIsContinuousScanning.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    mEditor.putBoolean(AppConfig.SharedPreferenceConfig.START_FRONT_CAMERA_IS_CONTINUOUS_SCANNING, isChecked);
                    mEditor.commit();
                });
                swIsContinuousScanning.setChecked(sharedPreferences.getBoolean(AppConfig.SharedPreferenceConfig.START_FRONT_CAMERA_IS_CONTINUOUS_SCANNING, false));
            }

            @Override
            public void onResult(int id, View view) {
                Spinner spnFocusMode = view.findViewById(R.id.spn_FocusMode);
                int focusMode = EnumUtils.getFocusMode(spnFocusMode.getSelectedItem().toString());
                EditText editTimeout = view.findViewById(R.id.edit_scan_timeout);
                int timeout = Integer.parseInt(editTimeout.getText().toString());
                mEditor.putString(AppConfig.SharedPreferenceConfig.START_FRONT_CAMERA_SCAN_TIMEOUT, String.valueOf(timeout));
                Switch swIsContinuous = view.findViewById(R.id.sw_isContinuousScanning);
                boolean isContinuousScanning = swIsContinuous.isChecked();
                if (scannerConfig != null) {
                    if (!scannerConfig.hasFrontCamera()) {
                        showMessage(context.getString(R.string.no_front_camera), MessageTag.ERROR);
                        return;
                    }
                    startScan(FRONT_CAMERA, focusMode, timeout, isContinuousScanning);
                } else {
                    showMessage(context.getString(R.string.no_scanner_config), MessageTag.ERROR);
                }
            }
        });

    }

    @MethodGridEntity(btnnameid = R.string.rear_scan, functionid = 5, btnimageid = 2)
    private void rearCameraScan() {
        DialogUtils.createCustomDialog(context, R.string.rear_scan, null, R.layout.dialog_set_scan_params, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                Spinner spnFocusMode = view.findViewById(R.id.spn_FocusMode);
                spnFocusMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.START_BACK_CAMERA_SCAN_FOCUS_MODE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                spnFocusMode.setSelection(sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.START_BACK_CAMERA_SCAN_FOCUS_MODE, 0));
                EditText editTimeout = view.findViewById(R.id.edit_scan_timeout);
                editTimeout.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.START_BACK_CAMERA_SCAN_TIMEOUT, "15000"));
                Switch swIsContinuousScanning = view.findViewById(R.id.sw_isContinuousScanning);
                swIsContinuousScanning.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    mEditor.putBoolean(AppConfig.SharedPreferenceConfig.START_BACK_CAMERA_IS_CONTINUOUS_SCANNING, isChecked);
                    mEditor.commit();
                });
                swIsContinuousScanning.setChecked(sharedPreferences.getBoolean(AppConfig.SharedPreferenceConfig.START_BACK_CAMERA_IS_CONTINUOUS_SCANNING, false));
            }

            @Override
            public void onResult(int id, View view) {
                Spinner spnFocusMode = view.findViewById(R.id.spn_FocusMode);
                int focusMode = EnumUtils.getFocusMode(spnFocusMode.getSelectedItem().toString());
                EditText editTimeout = view.findViewById(R.id.edit_scan_timeout);
                int timeout = Integer.parseInt(editTimeout.getText().toString());
                mEditor.putString(AppConfig.SharedPreferenceConfig.START_BACK_CAMERA_SCAN_TIMEOUT, String.valueOf(timeout));
                Switch swIsContinuous = view.findViewById(R.id.sw_isContinuousScanning);
                boolean isContinuousScanning = swIsContinuous.isChecked();
                if (scannerConfig != null) {
                    if (!scannerConfig.hasBackCamera()) {
                        showMessage(context.getString(R.string.no_back_camera), MessageTag.ERROR);
                        return;
                    }
                    startScan(BACK_CAMERA, focusMode, timeout, isContinuousScanning);
                } else {
                    showMessage(context.getString(R.string.no_scanner_config), MessageTag.ERROR);
                }
            }
        });

    }

    @MethodGridEntity(btnnameid = R.string.payment_scan, functionid = 6, btnimageid = 3)
    private void paymentCameraScan() {
        DialogUtils.createCustomDialog(context, R.string.payment_scan, null, R.layout.dialog_set_scan_params, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                Spinner spnFocusMode = view.findViewById(R.id.spn_FocusMode);
                spnFocusMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.START_PAYMENT_CAMERA_SCAN_FOCUS_MODE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                spnFocusMode.setSelection(sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.START_PAYMENT_CAMERA_SCAN_FOCUS_MODE, 0));
                EditText editTimeout = view.findViewById(R.id.edit_scan_timeout);
                editTimeout.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.START_PAYMENT_CAMERA_SCAN_TIMEOUT, "15000"));
                Switch swIsContinuousScanning = view.findViewById(R.id.sw_isContinuousScanning);
                swIsContinuousScanning.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    mEditor.putBoolean(AppConfig.SharedPreferenceConfig.START_PAYMENT_CAMERA_IS_CONTINUOUS_SCANNING, isChecked);
                    mEditor.commit();
                });
                swIsContinuousScanning.setChecked(sharedPreferences.getBoolean(AppConfig.SharedPreferenceConfig.START_PAYMENT_CAMERA_IS_CONTINUOUS_SCANNING, false));
            }

            @Override
            public void onResult(int id, View view) {
                Spinner spnFocusMode = view.findViewById(R.id.spn_FocusMode);
                int focusMode = EnumUtils.getFocusMode(spnFocusMode.getSelectedItem().toString());
                EditText editTimeout = view.findViewById(R.id.edit_scan_timeout);
                int timeout = Integer.parseInt(editTimeout.getText().toString());
                mEditor.putString(AppConfig.SharedPreferenceConfig.START_PAYMENT_CAMERA_SCAN_TIMEOUT, String.valueOf(timeout));
                Switch swIsContinuous = view.findViewById(R.id.sw_isContinuousScanning);
                boolean isContinuousScanning = swIsContinuous.isChecked();
                if (scannerConfig != null) {
                    if (!scannerConfig.hasPaymentCamera()) {
                        showMessage(context.getString(R.string.no_payment_camera), MessageTag.ERROR);
                        return;
                    }
                    startScan(PAYMENT_CAMERA, focusMode, timeout, isContinuousScanning);
                } else {
                    showMessage(context.getString(R.string.no_scanner_config), MessageTag.ERROR);
                }
            }
        });
    }


    @MethodGridEntity(btnnameid = R.string.hardware_scanner, functionid = 7, btnimageid = 4)
    private void hardwareScanner() {
        DialogUtils.createCustomDialog(context, R.string.hardware_scanner, null, R.layout.dialog_set_scan_params, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                Spinner spnFocusMode = view.findViewById(R.id.spn_FocusMode);
                spnFocusMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.START_HARDWARE_SCANNER_SCAN_FOCUS_MODE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                spnFocusMode.setSelection(sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.START_HARDWARE_SCANNER_SCAN_FOCUS_MODE, 0));
                EditText editTimeout = view.findViewById(R.id.edit_scan_timeout);
                editTimeout.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.START_HARDWARE_SCANNER_SCAN_TIMEOUT, "15000"));
                Switch swIsContinuousScanning = view.findViewById(R.id.sw_isContinuousScanning);
                swIsContinuousScanning.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    mEditor.putBoolean(AppConfig.SharedPreferenceConfig.START_HARDWARE_SCANNER_IS_CONTINUOUS_SCANNING, isChecked);
                    mEditor.commit();
                });
                swIsContinuousScanning.setChecked(sharedPreferences.getBoolean(AppConfig.SharedPreferenceConfig.START_HARDWARE_SCANNER_IS_CONTINUOUS_SCANNING, false));
                swIsContinuousScanning.setVisibility(View.GONE);
            }

            @Override
            public void onResult(int id, View view) {
                Spinner spnFocusMode = view.findViewById(R.id.spn_FocusMode);
                int focusMode = EnumUtils.getFocusMode(spnFocusMode.getSelectedItem().toString());
                EditText editTimeout = view.findViewById(R.id.edit_scan_timeout);
                int timeout = Integer.parseInt(editTimeout.getText().toString());
                mEditor.putString(AppConfig.SharedPreferenceConfig.START_HARDWARE_SCANNER_SCAN_TIMEOUT, String.valueOf(timeout));
                Switch swIsContinuous = view.findViewById(R.id.sw_isContinuousScanning);
                boolean isContinuousScanning = swIsContinuous.isChecked();
                if (scannerConfig != null) {
                    if (!scannerConfig.supportHardScanning()) {
                        showMessage(context.getString(R.string.no_hard_scanner), MessageTag.ERROR);
                        return;
                    }
                    ScanParameters scanParameters = new ScanParameters();
                    scanParameters.setScannerType(ScannerType.HARDWARE_SCANNER);
                    scanParameters.setSoundSwitcher(false);
                    scanParameters.setTimeout(timeout);
                    scanParameters.setFocusMode(focusMode);
                    try {
                        barcodeScanner.initScan(scanParameters);
                        barcodeScanner.setDecodingCallback(decodingByteCallback);
                        barcodeScanner.startScan();
                    } catch (NSDKException e) {
                        e.printStackTrace();
                    }
                } else {
                    showMessage(context.getString(R.string.no_scanner_config), MessageTag.ERROR);
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.stop_hardware_scanning, functionid = 8, btnimageid = 5)
    private void stopHardwareScanning() {
        try {
            barcodeScanner.stopScan();
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }
    }

    @MethodGridEntity(functionid = 9)
    private void fill3() {

    }



    private void startScan(int cameraId, int focusMode, int timeout, boolean isContinuousScanning) {
        Intent intent = new Intent(context, CameraActivity.class);
        intent.putExtra("cameraId", cameraId);
        intent.putExtra("focusMode", focusMode);
        intent.putExtra("timeout", timeout);
        intent.putExtra("isContinuous", isContinuousScanning);
        if (isContinuousScanning) {
            context.startActivity(intent);
        } else {
            ((Activity) context).startActivityForResult(intent, REQUEST_BARCODE_SCANNER);
        }
    }


    private MainActivity.onActivityResultListener listener = new MainActivity.onActivityResultListener() {
        @Override
        public void onActivityResult(Bitmap bitmap) {
            takenPhotoBitmap = bitmap;
            notifyPhotoTakenLock();
        }
    };

    private void waitPhotoTaken() {
        synchronized (waitPhotoTakenLock) {
            try {
                waitPhotoTakenLock.wait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void notifyPhotoTakenLock() {
        synchronized (waitPhotoTakenLock) {
            waitPhotoTakenLock.notify();
        }
    }

    private final DecodingByteCallback decodingByteCallback = new DecodingByteCallback() {
        @Override
        public void onDecodingByteCallback(int eventCode, byte[] result) {
            if (eventCode == ErrorCode.OK) {
                showMessage("Result: " + new String(result));
            } else {
                showMessage("Error: " + new String(result));
            }
        }
    };

    private Bitmap resizeBimap(Bitmap originBitmap) {
        float ratio = 1;
        Matrix matrix = new Matrix();
        int width = originBitmap.getWidth();
        int height = originBitmap.getHeight();
        if (width > 1600 && height <= 1200) {
            ratio = (float) 1600 / width;
        }
        if (height > 1200 && width <= 1600) {
            ratio = (float) 1200 / height;
        }
        if (width > 1600 && height > 1200) {
            ratio = Math.min((float) 1600 / width, (float) 1200 / height);
        }
        matrix.postScale(ratio, ratio);
        return Bitmap.createBitmap(originBitmap, 0, 0, width, height, matrix, false);

    }



}
