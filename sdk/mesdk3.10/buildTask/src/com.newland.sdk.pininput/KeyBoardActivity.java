package com.newland.sdk.pininput;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.newland.SettingsManager;
import android.newland.content.NlContext;
import android.newland.os.NlBuild;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.newland.buildtask.R;
import com.newland.forth.module.jni.ForthJni;
import com.newland.sdk.me.module.pininput.KeyBoardParams;
import com.newland.sdk.module.pin.DefaultLayout;
import com.newland.sdk.module.pin.KeyboardRandom;
import com.newland.sdk.module.pin.PinInputListener;
import com.newland.sdk.module.pin.PinpadModule;
import com.newland.sdk.module.pin.RNIBPinInputListener;
import com.newland.sdk.mtypex.module.common.emv.SoundPoolImpl;
import com.newland.sdk.utils.ISOUtils;

import java.net.URL;

/**
 * Author by bxy(wuhh), Date on 2019/11/18.
 */
public class KeyBoardActivity extends Activity {
    private static final String TAG = "KeyBoardActivity";
    private PinpadModule pinInput;
    private TextView amountTv, displaymsgTv, passwordTv,passwordTv2;
    private int inputLen = 0;
    private KeyBoardView keyBoardView;
    private SoundPoolImpl soundPool;
    private LinearLayout upperHalfLy, pslayout;
    private PSView psShowView;

    private KeyBoardLayoutConfig lyConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_layout);
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
        defaultLayout.setLayoutStyle(param.getLayoutStyle());
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

        keyBoardView = (KeyBoardView) findViewById(R.id.id_pinkeyboard);
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

        keyBoardView.setKeyBoardLayoutConfig(lyConfig);
        keyBoardView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
            private boolean first;//  To prevent it from entering the onPreDraw() all the time.

            @Override
            public boolean onPreDraw() {
                if (!first) {
                    Log.d("KeyBoardView", "onPreDraw: ");
                    first = true;
                    boolean bool = getRandomKeyBoardNumber();
                    Log.d("KeyBoardView", "onPreDraw: bool:"+bool);
                    if (!bool) {
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
        });

    }

    private PinInputListener pinInputListener = new PinInputListener() {
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
//        if(lyConfig.getDefaultLayoutParam().getIsHalfScreen()){
//            return;
//        }
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
    private boolean getRandomKeyBoardNumber() {
        try {
            Log.d(TAG, "getRandomKeyBoardNumber: "+NlBuild.VERSION.MODEL);
            byte[] initCoordinate = keyBoardView.getCoordinate();
            // get key value of random keyboard
            byte[] keySeq = keyBoardView.getPinKeySeq();
            KeyboardRandom keyboardRandom = null;
            // If the number is random and the function key is fixed, do not pass the key value sequence.
            if (keySeq != null) {
                keyboardRandom = new KeyboardRandom(initCoordinate, keySeq);
            } else {
                keyboardRandom = new KeyboardRandom(initCoordinate);
            }
            byte[] randomCoordinate = pinInput.loadRandomKeyboard(keyboardRandom);
            keyBoardView.loadVppInitKey(randomCoordinate);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            Log.d(TAG, "onDestroy: ");
            soundPool.stop();
            //KeyBoardParams.setKeyManagement(null);
            //KeyBoardParams.setAlgorithmMode(null);
            //KeyBoardParams.setPan(null);
            //KeyBoardParams.setPinInputListener(null);
            //KeyBoardParams.setPinInputExtParams(null);
            //KeyBoardParams.setPinpadModule(null);
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
