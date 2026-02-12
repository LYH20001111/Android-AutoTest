package com.newland.sdkdemo.activity.RNIB;

import android.app.Presentation;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.newland.os.NlBuild;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.newland.sdk.ModuleManage;
import com.newland.sdk.me.module.pininput.KeyBoardParams;
import com.newland.sdk.module.pin.AccountInputType;
import com.newland.sdk.module.pin.AlgorithmMode;
import com.newland.sdk.module.pin.DukptDerivateUsage;
import com.newland.sdk.module.pin.KeyManagement;
import com.newland.sdk.module.pin.PinBlockMode;
import com.newland.sdk.module.pin.PinInputExtParams;
import com.newland.sdk.module.pin.PinpadModule;
import com.newland.sdk.module.pin.RNIBPinInputListener;
import com.newland.sdk.mtypex.module.common.emv.SoundPoolImpl;
import com.newland.sdkdemo.AppConfig;
import com.newland.sdkdemo.R;

/**
 * Author by bxy, Date on 2019/11/18.
 */
public class DRNIBKeyBoardPresentation extends Presentation {
    private static final String TAG = "RNIBPresentation";
    private PinpadModule pinInput;
    private TextView passwordTv;
    private int inputLen = 0;
    private DRNIBHorizontalPinKeyBoard pinKeyBoard;
    private SoundPoolImpl soundPool;
    private Context context;

    public DRNIBKeyBoardPresentation(Context context, Display display) {
        super(context, display);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rnib_presentation_pin_layout);
        soundPool = SoundPoolImpl.getInstance(0);
        pinInput = ModuleManage.getInstance().getPinpadModule();
        init();
        Window window = this.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setDimAmount(0f);
        }
    }


    private void init() {
        passwordTv = (TextView) findViewById(R.id.id_password);
        pinKeyBoard = (DRNIBHorizontalPinKeyBoard) findViewById(R.id.id_pinkeyboard3);

        pinKeyBoard.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
            private boolean first;//  To prevent it from entering the onPreDraw() all the time.

            @Override
            public boolean onPreDraw() {
                if (!first) {
                    Log.d("KeyBoardView", "onPreDraw: ");
                    first = true;
                    boolean bool = getRNIBKeyBoardNumber();
                    if (!bool) {
                        cancel();
                        return first;
                    }
                    boolean isOnline = true;
                    if (isOnline) {
                        int wkPinIndex =1;
                        int pwMaxLen = 12;
                        byte[] pwdLenRange = new byte[]{0,4,6};
                        int timeOut = 59;
                        KeyManagement keyManagement = null;
                        AlgorithmMode algorithmMode = null;
                        if (AppConfig.MKSK_DES.equals(AppConfig.KEY_SYS_ALG)) {
                            wkPinIndex = AppConfig.Pin.MKSK_DES_INDEX_WK_PIN;
                            keyManagement = KeyManagement.MKSK;
                            algorithmMode = AlgorithmMode.DES;
                        } else if (AppConfig.MKSK_SM4.equals(AppConfig.KEY_SYS_ALG)) {
                            wkPinIndex = AppConfig.Pin.MKSK_SM4_INDEX_WK_PIN;
                            keyManagement = KeyManagement.MKSK;
                            algorithmMode = AlgorithmMode.SM4;
                        } else if (AppConfig.MKSK_AES.equals(AppConfig.KEY_SYS_ALG)) {
                            wkPinIndex = AppConfig.Pin.MKSK_AES_INDEX_WK_PIN;
                            keyManagement = KeyManagement.MKSK;
                            algorithmMode = AlgorithmMode.AES;
                        } else if (AppConfig.DUKPT_DES.equals(AppConfig.KEY_SYS_ALG)) {
                            wkPinIndex = AppConfig.Pin.DUKPT_DES_INDEX;
                            keyManagement = KeyManagement.DUKPT;
                            algorithmMode = AlgorithmMode.DES;
                        }
                        PinInputExtParams pinInputExtParams = new PinInputExtParams();
                        if(AppConfig.DUKPT_DES.equals(AppConfig.KEY_SYS_ALG)){
                            pinInputExtParams.setDukptDerivateUsage(DukptDerivateUsage.PIN);
                            pinInputExtParams.setDerivateKeyLen(16);
                            pinInputExtParams.setPinBlockMode(PinBlockMode.ISO9564_FORMAT_4);
                        }
                        String account = "1234567890123456";
                        pinInputExtParams.setInputMaxLen(pwMaxLen);
                        pinInputExtParams.setPwdLengthRange(pwdLenRange);
                        pinInputExtParams.setAcctInputType(AccountInputType.USE_ACCOUNT);
                        pinInput.startPinInput(keyManagement, algorithmMode, wkPinIndex,
                                account, timeOut, pinInputListener,pinInputExtParams);
                    } else {
                        int pwMaxLen = 12;
                        byte[] pwdLenRange = new byte[]{0,4,6};
                        int timeOut = 59;
                        // when the connection paramters is NS3ConnParams,it uses input offline pin method.
                        byte[] modulus = null;// The modulus of actual transaction.it is a parameter of EmvTransInfo through emvTransInfo.getModulus().EmvTransInfo cames from onRequestPinEntry of emv callback.
                        byte[] exponent = null;// The exponent of actual transaction.it is a parameter of EmvTransInfo through emvTransInfo.getExponent().EmvTransInfo cames from onRequestPinEntry of emv callback.
                        PinInputExtParams pinInputExtParams = new PinInputExtParams();
                        pinInputExtParams.setInputMaxLen(pwMaxLen);
                        pinInputExtParams.setPwdLengthRange(pwdLenRange);
                        pinInput.startOfflinePinInput(timeOut,modulus, exponent, pinInputListener,pinInputExtParams);
                    }
                }
                return first;
            }
        });

    }
    private boolean getRNIBKeyBoardNumber() {
        try {
            Log.d(TAG, "getRNIBKeyBoardNumber: "+ NlBuild.VERSION.MODEL);
            return pinInput.loadRNIBKeyboard(15,pinKeyBoard.getKeyCoordinates(),pinKeyBoard.getTouchCoordinates(),pinKeyBoard.getKeyboradCoordinates());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getRNIBKeyBoardNumber: false.");
        return false;
    }
    private RNIBPinInputListener pinInputListener = new RNIBPinInputListener() {
        @Override
        public void onSlidNumberKey() {
            Log.d(TAG, "onSlidNumberKey: ");
        }

        @Override
        public void onSlidNoDigitKey() {

        }

        @Override
        public void onSlidBackSpace() {

        }

        @Override
        public void onSlidEnter() {

        }

        @Override
        public void onSlidCancel() {

        }

        @Override
        public void onSlidUp() {

        }

        @Override
        public void onSlidDown() {

        }

        @Override
        public void onSlidLeft() {

        }

        @Override
        public void onSlidRight() {

        }

        @Override
        public void onKeyPress() {
            playClickSound();
            inputLen = inputLen + 1;
            Log.d(TAG, "onKeyPress: inputLen=" + inputLen);
            updateUI(inputLen);

        }

        @Override
        public void onBackspace() {
            playClickSound();
            inputLen = (inputLen <= 0 ? 0 : inputLen - 1);
            Log.d(TAG, "onBackspace: inputLen=" + inputLen);
            updateUI(inputLen);

        }

        @Override
        public void onCancel() {
            playClickSound();
            Log.d(TAG, "onCancel: ");
            cancel();

        }

        @Override
        public void onFinish(int pinLen, byte[] pinblock, byte[] ksn) {
            playClickSound();
            Log.d(TAG, "onFinish: pinLen=" + pinLen);
            if (pinLen == 0) {
                pinblock = new byte[]{};
            }
            cancel();

        }

        @Override
        public void onTimeout() {
            Log.d(TAG, "onTimeout: ");
            cancel();

        }

        @Override
        public void onError(int errorCode, String message) {
            Log.d(TAG, "onError: " + errorCode + " " + message);
            cancel();
        }
    };

    private void updateUI(final int inputLen) {
        passwordTv.post(()->{
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < inputLen; i++) {
                buffer.append(" * ");
            }
            passwordTv.setText(buffer.toString());
        });
    }


    @Override
    protected void onStop() {
        soundPool.stop();
        super.onStop();
    }

    private void playClickSound() {
        soundPool.play(0, 0, 0);
    }
}
