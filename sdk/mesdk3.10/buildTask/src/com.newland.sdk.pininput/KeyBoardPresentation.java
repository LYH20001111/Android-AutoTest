package com.newland.sdk.pininput;

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
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.newland.buildtask.R;
import com.newland.sdk.me.module.pininput.KeyBoardParams;
import com.newland.sdk.module.pin.DefaultLayout;
import com.newland.sdk.module.pin.KeyboardRandom;
import com.newland.sdk.module.pin.PinInputListener;
import com.newland.sdk.module.pin.PinpadModule;
import com.newland.sdk.module.pin.RNIBPinInputListener;
import com.newland.sdk.mtypex.module.common.emv.SoundPoolImpl;
import com.newland.sdk.utils.ISOUtils;

/**
 * Author by bxy, Date on 2019/11/18.
 */
public class KeyBoardPresentation extends Presentation {
    private static final String TAG = "KeyBoardActivity";
    private PinpadModule pinInput;
    private TextView amountTv, displaymsgTv, passwordTv,passwordTv2;
    private int inputLen = 0;
    private KeyBoardView keyBoardView;
    private SoundPoolImpl soundPool;
    private LinearLayout upperHalfLy, pslayout;
    private PSView psShowView;
    private KeyBoardLayoutConfig lyConfig;
    private Context context;

    public KeyBoardPresentation(Context outerContext, Display display) {
        super(outerContext, display);
        this.context = outerContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presentation_pin_layout);
        soundPool = SoundPoolImpl.getInstance(0);
        dealkeyBoardConfig();
        init();
        Window window = this.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setDimAmount(0f);
        }
    }

    private void dealkeyBoardConfig() {
        Log.d(TAG, "dealkeyBoardConfig: ");
        pinInput = KeyBoardParams.getPinpadModule();
        lyConfig = new KeyBoardLayoutConfig(context);
        DefaultLayout param = KeyBoardParams.getPinInputExtParams().getDefaultLayout();
        //why not use param directly?
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
                pslayout.setVisibility(View.GONE);//INVISIBLE改为GONE
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
        keyBoardView.setKeyBoardLayoutConfig(lyConfig);
        keyBoardView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
            private boolean first;//  To prevent it from entering the onPreDraw() all the time.

            @Override
            public boolean onPreDraw() {
                if (!first) {
                    Log.d("KeyBoardView", "onPreDraw: ");
                    first = true;
                    boolean bool = getRandomKeyBoardNumber();

                    if (!bool) {
                        cancel();
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
            cancel();
            KeyBoardParams.getPinInputListener().onCancel();
        }

        @Override
        public void onFinish(int pinLen, byte[] pinblock, byte[] ksn) {
            playClickSound();
            Log.d(TAG, "onFinish: pinLen=" + pinLen);
            if (pinLen == 0) {
                pinblock = new byte[]{};
            }
            cancel();
            KeyBoardParams.getPinInputListener().onFinish(pinLen, pinblock, ksn);
        }

        @Override
        public void onTimeout() {
            Log.d(TAG, "onTimeout: ");
            cancel();
            KeyBoardParams.getPinInputListener().onTimeout();
        }

        @Override
        public void onError(int errorCode, String message) {
            Log.d(TAG, "onError: " + errorCode + " " + message);
            cancel();
            KeyBoardParams.getPinInputListener().onError(errorCode, message);
        }
    };

    private void updateUI(final int inputLen) {
//        if(lyConfig.getDefaultLayoutParam().getIsHalfScreen()){
//            return;
//        }
        psShowView.post(()->{
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
        });
    }

    private boolean getRandomKeyBoardNumber() {
        try {
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
            Log.d(TAG, "getRandomKeyBoardNumber: "+ ISOUtils.hexString(randomCoordinate));
            keyBoardView.loadVppInitKey(randomCoordinate);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onStop() {
        soundPool.stop();
        super.onStop();
    }

    private void playClickSound() {
        if (lyConfig.getDefaultLayoutParam().getEnableClickSound()) {
            soundPool.play(0, 0, 0);
        }
    }
}
