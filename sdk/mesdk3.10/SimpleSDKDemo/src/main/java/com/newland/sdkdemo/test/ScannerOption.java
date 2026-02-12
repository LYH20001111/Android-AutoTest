package com.newland.sdkdemo.test;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.newland.basetest.annotation.FragmentAno;
import com.newland.basetest.annotation.MethodAno;
import com.newland.basetest.pinc.MessageTag;
import com.newland.basetest.pinc.MethodBean;
import com.newland.basetest.view.msgDialog.MessageDialog;
import com.newland.sdk.module.scanner.ScannerModule;
import com.newland.sdkdemo.FragmentBase;
import com.newland.sdk.module.scanner.DefaultScannerLayout;
import com.newland.sdk.module.scanner.ScannerExtParams;
import com.newland.sdk.module.scanner.ScannerListener;
import com.newland.sdk.module.scanner.ScannerType;
import com.newland.sdkdemo.utils.DialogUtils;


@FragmentAno(name = "扫码", numId = 1)
public class ScannerOption extends FragmentBase {
    ScannerModule scannerModule;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerModule = getModuleManage().getScannerModule();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @MethodAno(name = "启动扫码", numId = 0)
    public void startScan(MethodBean bean) {
        DialogUtils dialogUtils = DialogUtils.getInstance();
        String[] items = new String[]{"前置","后置"};
        dialogUtils.createSingleChoiceDialog(getContext(), "选择扫码头", items, new DialogUtils.SingleChoiceDialogCallback() {
            @Override
            public void onResult(int id) {
                if(id<0){//取消对话框
                    return;
                }
                try {
                    ScannerType scannerType = ScannerType.BACK;//后置扫码头
                    if(id==0){//选择前置
                        scannerType = ScannerType.FRONT;//前置扫码头
                    }
                    ScannerExtParams scannerExtParams = new ScannerExtParams();//扫码扩展参数
                    DefaultScannerLayout defaultScannerLayout = new DefaultScannerLayout();//mesdk默认的扫码界面
                    defaultScannerLayout.setEnableSound(true);//扫码成功是否有提示音，true有提示音，false-没有提示音
                    scannerExtParams.setOnce(true);//是否单次扫码，true-单次扫码；false-连续扫码
                    scannerExtParams.setDefaultScannerLayout(defaultScannerLayout);//设置mesdk默认的扫码界面。不设置的话，应用可以自行绘制扫码预览界面，把surfaceview传给mesdk即可

                    scannerModule.startScan(getContext(), scannerType, null, 30, new ScannerListener() {
                        @Override
                        public void onTimeout() {
                            showMessage("扫码超时",MessageTag.NORMAL);
                        }

                        @Override
                        public void onResponse(String[] scanResults) {
                            showMessage("扫码成功，码值："+scanResults[0],MessageTag.NORMAL);
                        }

                        @Override
                        public void onFinish() {
                            showMessage("扫码结束，释放摄像头完成",MessageTag.NORMAL);
                        }

                        @Override
                        public void onError(int errorCode, String message) {
                            showMessage("扫码异常，异常码："+errorCode+"异常信心："+message,MessageTag.ERROR);
                        }

                        @Override
                        public void onCancel() {
                            showMessage("扫码取消",MessageTag.NORMAL);
                        }
                    },scannerExtParams);
                    showMessage("启动扫码", MessageTag.NORMAL);
                }catch (Exception e){
                    e.printStackTrace();
                    showMessage("开启扫码异常："+e,MessageTag.ERROR);
                }
            }
        });

    }

    @MethodAno(name = "停止扫码", numId = 1)
    public void stopScan(MethodBean bean) {
        showMessage("停止扫码", MessageTag.NORMAL);
    }


}
