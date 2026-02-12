package com.newland.nsdkdemo.external.fragment;

import android.content.Context;

import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.external.signature.ExtESignature;
import com.newland.nsdk.core.api.external.signature.ExtESignatureListener;
import com.newland.nsdk.core.api.external.signature.ExtESignatureParameters;
import com.newland.nsdk.core.api.external.signature.ImageFormat;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.annotation.MethodGridEntity;
import com.newland.nsdkdemo.common.utils.MessageTag;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

public class ExtESignatureFragment extends ExtBaseFragment{
    private static final String PATH = "/sdcard/nsdksignature";
    private ExtESignature extSignature;
    public ExtESignatureFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.ext_esignatureFragment);
    }

    @Override
    public void initData() {
         extSignature = (ExtESignature) moduleManager.getModule(ModuleType.EXT_ESIGNATURE);
    }

    @Override
    public Object getModule() {
        return ExtESignatureFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.start_esignature, functionid = 1)
    private void startSignature() {
        try {
            ExtESignatureParameters parameters = new ExtESignatureParameters();
            parameters.setSupportByPass(true);
            parameters.setSupportDisplayMessage(true);
            parameters.setShowButtons(true);
            parameters.setImageFormat(ImageFormat.DEFAULT);
            parameters.setRetryTime(3);
            parameters.setAreaWidth(300);
            parameters.setAreaHeight(300);
            parameters.setDisplayMessage("12345678");
            extSignature.start(parameters, 20, new ExtESignatureListener() {
                @Override
                public void onComplete(byte[] bytes) {
                    showMessage("length:" + bytes.length);
                    File file = new File(PATH);
                    if (!file.exists()) {
                        file.mkdirs();
                        file.setWritable(true, false);
                        file.setReadable(true, false);
                        file.setExecutable(true, false);
                    }

                    File imageFile = null;
                    try {
                        imageFile = File.createTempFile("bitmap", ".png", file);
                        imageFile.setWritable(true, false);
                        imageFile.setReadable(true, false);
                        imageFile.setExecutable(true, false);
                        FileOutputStream fOut = new FileOutputStream(imageFile);
                        fOut.write(bytes);
                        fOut.flush();
                        fOut.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(int errorCode, String errorMessage) {
                    showMessage(String.format(Locale.US,  "Failed to sign, ret[%d]:%s", errorCode, errorMessage), MessageTag.ERROR);
                }

                @Override
                public void onCancel() {
                    showMessage("OnCancel", MessageTag.TIP);
                }
            });
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.start_esignature));
        }
    }
}
