package com.newland.sdkdemo.activity.RNIB;

import android.app.Activity;
import android.newland.os.NlBuild;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.newland.sdk.ModuleManage;
import com.newland.sdk.module.pin.AccountInputType;
import com.newland.sdk.module.pin.AlgorithmMode;
import com.newland.sdk.module.pin.DefaultLayout;
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
 * Author by bxy(wuhh), Date on 2019/11/18.
 */
public class DRNIBKeyBoardActivity extends Activity {
    private static final String TAG = "RNIBKeyBoardActivity";
    private PinpadModule pinInput;
    private TextView passwordTv;
    private int inputLen = 0;
    private DRNIBPinKeyBoard pinKeyBoard;
    private DRNIBPinKeyBoard2 pinKeyBoard2;
    private SoundPoolImpl soundPool;
    private static final int STYLE_1 = 1;
    private static final int STYLE_2 = 2;
    private int style = STYLE_1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rnib_activity_pin_layout);
        pinInput = ModuleManage.getInstance().getPinpadModule();
        try {
            soundPool = SoundPoolImpl.getInstance(0);
            init();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }


    private void init() {
        passwordTv = (TextView) findViewById(R.id.id_password);
        pinKeyBoard = (DRNIBPinKeyBoard) findViewById(R.id.id_rnib_pinkeyboard1);
        pinKeyBoard2 = (DRNIBPinKeyBoard2) findViewById(R.id.id_rnib_pinkeyboard2);
        if(style == STYLE_1){
            pinKeyBoard.setVisibility(View.VISIBLE);
            pinKeyBoard.getViewTreeObserver().addOnPreDrawListener(onPreDrawListener);
        } else if(style == STYLE_2) {
            pinKeyBoard2.setVisibility(View.VISIBLE);
            pinKeyBoard2.getViewTreeObserver().addOnPreDrawListener(onPreDrawListener);
        }
    }

    private OnPreDrawListener onPreDrawListener = new OnPreDrawListener() {
        private boolean first;//  To prevent it from entering the onPreDraw() all the time.

        @Override
        public boolean onPreDraw() {
            if (!first) {
                Log.d("KeyBoardView", "onPreDraw: ");
                first = true;
                boolean bool = getRNIBKeyBoardNumber();
                if (!bool) {
                    Log.d("KeyBoardView", "getRNIBKeyBoardNumber false");
                    finish();
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
                    String account = "1234567890123456";
                    PinInputExtParams pinInputExtParams = new PinInputExtParams();
                    if(AppConfig.DUKPT_DES.equals(AppConfig.KEY_SYS_ALG)){
                        pinInputExtParams.setDukptDerivateUsage(DukptDerivateUsage.PIN);
                        pinInputExtParams.setDerivateKeyLen(16);
                        pinInputExtParams.setPinBlockMode(PinBlockMode.ISO9564_FORMAT_4);
                    }
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
                    byte[] modulus = getIntent().getByteArrayExtra("modulus");// The modulus of actual transaction.it is a parameter of EmvTransInfo through emvTransInfo.getModulus().EmvTransInfo cames from onRequestPinEntry of emv callback.
                    byte[] exponent = getIntent().getByteArrayExtra("exponent");// The exponent of actual transaction.it is a parameter of EmvTransInfo through emvTransInfo.getExponent().EmvTransInfo cames from onRequestPinEntry of emv callback.
                    PinInputExtParams pinInputExtParams = new PinInputExtParams();
                    pinInputExtParams.setInputMaxLen(pwMaxLen);
                    pinInputExtParams.setPwdLengthRange(pwdLenRange);
                    pinInput.startOfflinePinInput(timeOut,modulus, exponent, pinInputListener,pinInputExtParams);
                }

            }
            return first;
        }
    };

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
            finish();

        }

        @Override
        public void onFinish(int pinLen, byte[] pinblock, byte[] ksn) {
            playClickSound();
            Log.d(TAG, "onFinish: pinLen=" + pinLen);
            if (pinLen == 0) {
                pinblock = new byte[]{};
            }
            finish();
        }

        @Override
        public void onTimeout() {
            Log.d(TAG, "onTimeout: ");
            finish();

        }

        @Override
        public void onError(int errorCode, String message) {
            Log.d(TAG, "onError: " + errorCode + " " + message);
            finish();
        }
    };

    private void updateUI(final int inputLen) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                StringBuffer buffer = new StringBuffer();
                for (int i = 0; i < inputLen; i++) {
                    buffer.append(" * ");
                }
                passwordTv.setText(buffer.toString());
            }
        });
    }

    private boolean getRNIBKeyBoardNumber() {
        try {
            Log.d(TAG, "getRNIBKeyBoardNumber: "+NlBuild.VERSION.MODEL);
            if(style == STYLE_1){
                return pinInput.loadRNIBKeyboard(12,pinKeyBoard.getKeyCoordinates(),pinKeyBoard.getTouchCoordinates(),pinKeyBoard.getKeyboradCoordinates());
            } else if(style == STYLE_2){
                return pinInput.loadRNIBKeyboard(13,pinKeyBoard2.getKeyCoordinates(),pinKeyBoard2.getTouchCoordinates(),pinKeyBoard2.getKeyboradCoordinates());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getRNIBKeyBoardNumber: false.");
        return false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            Log.d(TAG, "onDestroy: ");
            soundPool.stop();
            Log.d(TAG, "onDestroy:: ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playClickSound() {
        soundPool.play(0, 0, 0);
    }
}
