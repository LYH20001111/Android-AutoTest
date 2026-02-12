package com.newland.nsdkdemo.external.fragment;

import android.annotation.SuppressLint;
import android.content.Context;

import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.external.scanner.ExtScanner;
import com.newland.nsdk.core.api.external.scanner.ExtScannerListener;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.annotation.MethodGridEntity;
import com.newland.nsdkdemo.common.utils.MessageTag;

public class ExtScannerFragment extends ExtBaseFragment {

    private ExtScanner extScanner;
    private static final int INDEX_EXTSCANNER_START = 1;
    private static final int INDEX_EXTSCANNER_STOP = 2;
    private Context mContext;

    @SuppressLint("ValidFragment")
    public ExtScannerFragment(Context context) {
        super(context, LayoutMode.GRID);
        mContext = context;
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_extscan_f);
    }

    @Override
    public void initData() {
        extScanner = (ExtScanner) moduleManager.getModule(ModuleType.EXT_SCANNER);
    }

    @Override
    public Object getModule() {
        return ExtScannerFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.tv_start_scanner, functionid = INDEX_EXTSCANNER_START)
    private void startScan() {
        try {
            extScanner.startScan(60, new ExtScannerListener() {
                @Override
                public void onSuccess(String s) {
                    showMessage("Scanning result: " + s);
                }

                @Override
                public void onError(int i, String msg) {
//                    showMessage(moduleManager.getErrMsg(i), MessageTag.ERROR);
                    showMessage(String.format("[%d] %s", i, msg), MessageTag.ERROR);
                }

                @Override
                public void onTimeout() {
                    showMessage("Scanning timeout.");
                }
            });
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "start scanning");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_stop_scanner, functionid = INDEX_EXTSCANNER_STOP)
    private void stopScan() {
        try {
            extScanner.stopScan();
            showMessage("Scanning stopped.");
        } catch (NSDKException e) {
            showErrorMessage(e, "stop scanning");
            e.printStackTrace();
        }
    }
}
