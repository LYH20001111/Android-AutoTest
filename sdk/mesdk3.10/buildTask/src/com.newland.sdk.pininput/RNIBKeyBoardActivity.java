package com.newland.sdk.pininput;

import android.app.Activity;
import android.newland.os.NlBuild;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.newland.buildtask.R;
import com.newland.sdk.me.module.pininput.KeyBoardParams;
import com.newland.sdk.module.pin.DefaultLayout;
import com.newland.sdk.module.pin.PinpadModule;
import com.newland.sdk.module.pin.RNIBPinInputListener;
import com.newland.sdk.mtypex.module.common.emv.SoundPoolImpl;

/**
 * Author by bxy(wuhh), Date on 2019/11/18.
 */
public class RNIBKeyBoardActivity extends Activity {
    private static final String TAG = "RNIBKeyBoardActivity";
    private PinpadModule pinInput;
    private TextView amountTv, displaymsgTv, passwordTv,passwordTv2;
    private int inputLen = 0;
    private RNIBPinKeyBoard pinKeyBoard;
    private RNIBPinKeyBoard2 pinKeyBoard2;
    private SoundPoolImpl soundPool;
    private LinearLayout upperHalfLy, pslayout;
    private PSView psShowView;

    private KeyBoardLayoutConfig lyConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rnib_pin_layout);
        try {
            soundPool = SoundPoolImpl.getInstance(0);
            dealkeyBoardConfig();
            init();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    private void dealkeyBoardConfig() {
        Log.d(TAG, "dealkeyBoardConfig: ");
        pinInput = KeyBoardParams.getPinpadModule();
        lyConfig = new KeyBoardLayoutConfig(this);
        DefaultLayout param = KeyBoardParams.getPinInputExtParams().getDefaultLayout();
        Log.d(TAG, "dealkeyBoardConfig: param="+param);
        DefaultLayout defaultLayout = new DefaultLayout(param.getIsOnline());
        defaultLayout.setAmount(param.getAmount());
        defaultLayout.setDisplayMessage(param.getDisplayMessage());
        defaultLayout.setIsHalfScreen(param.getIsHalfScreen());
        defaultLayout.setDividerSize(param.getDividerSize());
        defaultLayout.setRoundSize(param.getRoundSize());
        defaultLayout.setBgColor(param.getBgColor());
        defaultLayout.setScale(param.getScale());
        defaultLayout.setEnableClickSound(param.getEnableClickSound());
        defaultLayout.setKeyRondomType(param.getKeyRondomType());
        defaultLayout.setLayoutStyle( (param.getLayoutStyle() == null || param.getLayoutStyle() == DefaultLayout.Style.STYLE_3)
                ? DefaultLayout.Style.STYLE_1 : param.getLayoutStyle());
        defaultLayout.setCancelKeyAttr(param.getCancelKeyAttr());
        defaultLayout.setBackSpaceKeyAttr(param.getBackSpaceKeyAttr());
        defaultLayout.setConfirmAttr(param.getConfirmAttr());
        defaultLayout.setNumKeyAttr(param.getNumKeyAttr());
        defaultLayout.setHalfScreenShowPs(param.isHalfScreenShowPs());
        defaultLayout.setAngle(param.getAngle());
        lyConfig.updateConfig(defaultLayout);
    }

    private void init() {
        upperHalfLy = (LinearLayout) findViewById(R.id.id_upperhalf);
        passwordTv = (TextView) findViewById(R.id.id_password);

        pslayout = findViewById(R.id.ps_layout);
        psShowView = findViewById(R.id.pstext);
        passwordTv2 = (TextView) findViewById(R.id.id_password2);

        pinKeyBoard = (RNIBPinKeyBoard) findViewById(R.id.id_rnib_pinkeyboard1);
        pinKeyBoard2 = (RNIBPinKeyBoard2) findViewById(R.id.id_rnib_pinkeyboard2);
        if(lyConfig.getDefaultLayoutParam().getLayoutStyle() == DefaultLayout.Style.STYLE_1){
            pinKeyBoard.setVisibility(View.VISIBLE);
        } else if(lyConfig.getDefaultLayoutParam().getLayoutStyle() == DefaultLayout.Style.STYLE_2) {
            pinKeyBoard2.setVisibility(View.VISIBLE);
        }
        if (lyConfig.getDefaultLayoutParam().getIsHalfScreen()) {
            upperHalfLy.setVisibility(View.INVISIBLE);
            if (lyConfig.getDefaultLayoutParam().isHalfScreenShowPs()) {
                pslayout.setVisibility(View.VISIBLE);
                String displayMessage = lyConfig.getDefaultLayoutParam().getDisplayMessage();
                if (displayMessage == null) {
                    displayMessage = this.getResources().getString(R.string.keyboard_enterpwd);
                }
                passwordTv2.setText(displayMessage + "");
            }
        } else {
            upperHalfLy.setVisibility(View.VISIBLE);
            pslayout.setVisibility(View.GONE);

            amountTv = (TextView) findViewById(R.id.id_amount);
            displaymsgTv = (TextView) findViewById(R.id.id_displaymsg);
            if (lyConfig.getDefaultLayoutParam().getAmount() != null) {
                amountTv.setText(lyConfig.getDefaultLayoutParam().getAmount() + "");
            }
            String displayMessage = lyConfig.getDefaultLayoutParam().getDisplayMessage();
            if (displayMessage == null) {
                displayMessage = this.getResources().getString(R.string.keyboard_enterpwd);
            }
            displaymsgTv.setText(displayMessage + "");
        }

        if(lyConfig.getDefaultLayoutParam().getAngle() == 90 ||
                lyConfig.getDefaultLayoutParam().getAngle() == 180 ||
                lyConfig.getDefaultLayoutParam().getAngle() == 270 ){
            pslayout.setVisibility(View.GONE);
        }

        if(lyConfig.getDefaultLayoutParam().getLayoutStyle() == DefaultLayout.Style.STYLE_1){
            pinKeyBoard.setKeyBoardLayoutConfig(lyConfig);
            pinKeyBoard.getViewTreeObserver().addOnPreDrawListener(onPreDrawListener);
        } else if (lyConfig.getDefaultLayoutParam().getLayoutStyle() == DefaultLayout.Style.STYLE_2) {
            pinKeyBoard2.setKeyBoardLayoutConfig(lyConfig);
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
                KeyBoardParams.getPinInputExtParams().setDefaultLayout(null);
                if (lyConfig.getDefaultLayoutParam().getIsOnline()) {
                    pinInput.startPinInput(KeyBoardParams.getKeyManagement(),
                            KeyBoardParams.getAlgorithmMode(), KeyBoardParams.getKeyIndex(),
                            KeyBoardParams.getPan(), KeyBoardParams.getTimeout(),
                            pinInputListener, KeyBoardParams.getPinInputExtParams());
                } else {
                    pinInput.startOfflinePinInput(KeyBoardParams.getTimeout(),
                            KeyBoardParams.getModulus(), KeyBoardParams.getExponent(),
                            pinInputListener, KeyBoardParams.getPinInputExtParams());
                }

            }
            return first;
        }
    };

    private RNIBPinInputListener pinInputListener = new RNIBPinInputListener() {
        @Override
        public void onSlidNumberKey() {
            ((RNIBPinInputListener)KeyBoardParams.getPinInputListener()).onSlidNumberKey();
        }

        @Override
        public void onSlidNoDigitKey() {
            ((RNIBPinInputListener)KeyBoardParams.getPinInputListener()).onSlidNoDigitKey();
        }

        @Override
        public void onSlidBackSpace() {
            ((RNIBPinInputListener)KeyBoardParams.getPinInputListener()).onSlidBackSpace();
        }

        @Override
        public void onSlidEnter() {
            ((RNIBPinInputListener)KeyBoardParams.getPinInputListener()).onSlidEnter();
        }

        @Override
        public void onSlidCancel() {
            ((RNIBPinInputListener)KeyBoardParams.getPinInputListener()).onSlidCancel();
        }

        @Override
        public void onSlidUp() {
            ((RNIBPinInputListener)KeyBoardParams.getPinInputListener()).onSlidUp();
        }

        @Override
        public void onSlidDown() {
            ((RNIBPinInputListener)KeyBoardParams.getPinInputListener()).onSlidDown();
        }

        @Override
        public void onSlidLeft() {
            ((RNIBPinInputListener)KeyBoardParams.getPinInputListener()).onSlidLeft();
        }

        @Override
        public void onSlidRight() {
            ((RNIBPinInputListener)KeyBoardParams.getPinInputListener()).onSlidRight();
        }

        @Override
        public void onKeyPress() {
            playClickSound();
            inputLen = inputLen + 1;
            Log.d(TAG, "onKeyPress: inputLen=" + inputLen);
            updateUI(inputLen);
            KeyBoardParams.getPinInputListener().onKeyPress();
        }

        @Override
        public void onBackspace() {
            playClickSound();
            inputLen = (inputLen <= 0 ? 0 : inputLen - 1);
            Log.d(TAG, "onBackspace: inputLen=" + inputLen);
            updateUI(inputLen);
            KeyBoardParams.getPinInputListener().onBackspace();
        }

        @Override
        public void onCancel() {
            playClickSound();
            Log.d(TAG, "onCancel: ");
            finish();
            KeyBoardParams.getPinInputListener().onCancel();
        }

        @Override
        public void onFinish(int pinLen, byte[] pinblock, byte[] ksn) {
            playClickSound();
            Log.d(TAG, "onFinish: pinLen=" + pinLen);
            if (pinLen == 0) {
                pinblock = new byte[]{};
            }
            finish();
            KeyBoardParams.getPinInputListener().onFinish(pinLen, pinblock, ksn);
        }

        @Override
        public void onTimeout() {
            Log.d(TAG, "onTimeout: ");
            finish();
            KeyBoardParams.getPinInputListener().onTimeout();
        }

        @Override
        public void onError(int errorCode, String message) {
            Log.d(TAG, "onError: " + errorCode + " " + message);
            finish();
            KeyBoardParams.getPinInputListener().onError(errorCode, message);
        }
    };

    private void updateUI(final int inputLen) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (inputLen > 6) {
                    psShowView.setPs_size(inputLen);
                    psShowView.showInput(inputLen);
                } else {
                    psShowView.setPs_size(6);
                    psShowView.showInput(inputLen);
                }

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
            if(lyConfig.getDefaultLayoutParam().getLayoutStyle() == DefaultLayout.Style.STYLE_1){
                return pinInput.loadRNIBKeyboard(12,pinKeyBoard.getKeyCoordinates(),pinKeyBoard.getTouchCoordinates(),pinKeyBoard.getKeyboradCoordinates());
            } else if(lyConfig.getDefaultLayoutParam().getLayoutStyle() == DefaultLayout.Style.STYLE_2){
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
        if (lyConfig.getDefaultLayoutParam().getEnableClickSound()) {
            soundPool.play(0, 0, 0);
        }
    }
}
