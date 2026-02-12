package com.newland.sdkdemo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.newland.os.NlBuild;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.newland.sdk.ModuleManage;
import com.newland.sdk.module.pin.AccountInputType;
import com.newland.sdk.module.pin.AlgorithmMode;
import com.newland.sdk.module.pin.KeyManagement;
import com.newland.sdk.module.pin.KeyboardRandom;
import com.newland.sdk.module.pin.PinInputExtParams;
import com.newland.sdk.module.pin.PinInputListener;
import com.newland.sdk.module.pin.PinpadModule;
import com.newland.sdk.utils.ISOUtils;
import com.newland.sdkdemo.AppConfig;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.event.PinEntryListener;

/**
 * Random password keyboard activity
 */
public class KeyBoardNumberImageActivity extends Activity {
    private static final String TAG = "KeyBoardNumber";
    private PinpadModule pinInput;
    private ImageView iv0, iv1, iv2, iv3, iv4, iv5, iv6, iv7, iv8, iv9, ivCancel, ivDelete, ivConfirm;
    private TextView txtPassword;
    private StringBuffer buffer;
    private int inputLen = 0;
    private Context context;
    String accNo;
    private PinEntryListener pinEntryListener;
    public final int[] resImages = {R.drawable.keyboard_0, R.drawable.keyboard_1, R.drawable.keyboard_2, R.drawable.keyboard_3, R.drawable.keyboard_4, R.drawable.keyboard_5, R.drawable.keyboard_6, R.drawable.keyboard_7, R.drawable.keyboard_8, R.drawable.keyboard_9};
    private byte[] rf = new byte[]{0x1B, 0x0A, 0x0D};
    public final int[] resImagesFun = {R.drawable.keyboard_cancel, R.drawable.keyboard_bacespeace, R.drawable.keyboard_confirm};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.input_pin_image_fragment);
        context = this;
        pinInput = ModuleManage.getInstance().getPinpadModule();
        pinEntryListener = AppConfig.getPinEntryListener();
        init();

    }

    private void init() {
        accNo = getIntent().getStringExtra("accNo");
        txtPassword = (TextView) findViewById(R.id.txt_password);
        iv0 = (ImageView) findViewById(R.id.iv_0);
        iv1 = (ImageView) findViewById(R.id.iv_1);
        iv2 = (ImageView) findViewById(R.id.iv_2);
        iv3 = (ImageView) findViewById(R.id.iv_3);
        iv4 = (ImageView) findViewById(R.id.iv_4);
        iv5 = (ImageView) findViewById(R.id.iv_5);
        iv6 = (ImageView) findViewById(R.id.iv_6);
        iv7 = (ImageView) findViewById(R.id.iv_7);
        iv8 = (ImageView) findViewById(R.id.iv_8);
        iv9 = (ImageView) findViewById(R.id.iv_9);

        ivCancel = (ImageView) findViewById(R.id.iv_cancel);
        ivDelete = (ImageView) findViewById(R.id.iv_backspeace);
        ivConfirm = (ImageView) findViewById(R.id.iv_enter);

        iv0.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {

            private boolean first;//  To prevent it from entering the onPreDraw() all the time.

            @Override
            public boolean onPreDraw() {
                if (!first) {
                    first = true;
                    View[] btns = new View[]{iv0, iv1, iv2, iv3, iv4, iv5, iv6, iv7, iv8, iv9, ivCancel, ivDelete, ivConfirm};
                    boolean bool = getRandomKeyBoardNumber(btns);
                    if (!bool) {
                        finish();
                        return first;
                    }
                    int wkPinIndex = 1;
                    AccountInputType acctInputType = AccountInputType.USE_ACCOUNT;
                    int pwMaxLen = 6;
                    byte[] pwdLenRange = getPinLengthRange(0, 6);
                    byte[] pinPadding = new byte[]{'F', 'F', 'F', 'F', 'F', 'F', 'F', 'F', 'F', 'F'};
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
                    pinInputExtParams.setInputMaxLen(pwMaxLen);
                    pinInputExtParams.setPwdLengthRange(pwdLenRange);
                    if (accNo != null && !"".equals(accNo)) {
                        pinInputExtParams.setAcctInputType(AccountInputType.USE_ACCOUNT);
                    } else {
                        pinInputExtParams.setAcctInputType(AccountInputType.UNUSE_ACCOUNT);
                    }
                    pinInput.startPinInput(keyManagement, algorithmMode, wkPinIndex,
                            accNo, timeOut, pinInputListener, pinInputExtParams);
                }
                return first;
            }
        });

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2: // inputting
                    int len = (Integer) msg.obj;
                    buffer = new StringBuffer();
                    for (int i = 0; i < len; i++) {
                        buffer.append(" * ");
                    }
                    txtPassword.setText(buffer.toString());
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * Get the number of random keyboard
     *
     * @return
     */
    private boolean getRandomKeyBoardNumber(View[] btns) {
        try {
            Log.d(TAG, "getRandomKeyBoardNumber: "+ NlBuild.VERSION.MODEL);
            if(NlBuild.VERSION.MODEL.equals("P300")){
                return true;
            }
            // get key value of random keyboard
            KeyboardRandom keyboardRandom = null;
            keyboardRandom = new KeyboardRandom(btns, KeyboardRandom.PinKeySeq.NUM_RANDOM);
            byte[] randomCoordinate = pinInput.loadRandomKeyboard(keyboardRandom);
            Log.i(TAG, "randomCoordinate:" + ISOUtils.hexString(randomCoordinate));

            byte[] numserial = new byte[10];// Get number key
            byte[] functionserial = new byte[3];
            int d = 0;
            int t = 0;
            for (int i = 0; i < randomCoordinate.length; i++) {
                //Cancel Backspace Space Space Confirm Skip，
                if (i == 11 || i == 13) {
                    continue;
                }
                if (i == 3 || i == 7 || i == 14) {
                    functionserial[t] = (byte) (randomCoordinate[i]);
                    t++;
                    continue;
                }
                numserial[d] = (byte) (randomCoordinate[i] & 0x0f);
                d++;
            }
            Log.i(TAG, "numserial:" + ISOUtils.hexString(numserial));
            Log.i(TAG, "functionserial:" + ISOUtils.hexString(functionserial));

            View[] btnsNum = new View[]{btns[1], btns[2], btns[3], btns[4], btns[5], btns[6], btns[7], btns[8], btns[9], btns[0]};
            View[] btnsfunction = new View[]{btns[10], btns[11], btns[12]};
            // Deal with image
            for (int i = 0; i < btnsNum.length; i++) {
                int number = numserial[i] & 0xff;
                ((ImageView) btnsNum[i]).setImageResource(resImages[number]);
            }

//			for(int j=0;j<btnsfunction.length;j++){ //It`s used when function keys are random.
//				switch (functionserial[j]){
//					case 0x1B:
//						((ImageView) btnsfunction[j]).setImageResource(resImagesFun[0]);
//						break;
//					case 0x0A:
//						((ImageView) btnsfunction[j]).setImageResource(resImagesFun[1]);
//						break;
//					case 0x0D:
//						((ImageView) btnsfunction[j]).setImageResource(resImagesFun[2]);
//						break;
//
//				}
//			}
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private PinInputListener pinInputListener = new PinInputListener() {
        @Override
        public void onKeyPress() {
            inputLen = inputLen + 1;
            Log.i(TAG, getString(R.string.keyboard_activity_log_press_key_code) + inputLen);
            Message msg = mHandler.obtainMessage(2);
            msg.obj = inputLen;
            msg.sendToTarget();
        }

        @Override
        public void onBackspace() {
            inputLen = (inputLen <= 0 ? 0 : inputLen - 1);
            Log.i(TAG, getString(R.string.keyboard_activity_log_press_cancel_code) + inputLen);
            Message msg = mHandler.obtainMessage(2);
            msg.obj = inputLen;
            msg.sendToTarget();
        }

        @Override
        public void onCancel() {
            Log.i(TAG, "Is UserCanceled");
            finish();
            if (pinEntryListener != null) {
                pinEntryListener.onFinish(null);
            }
        }

        @Override
        public void onFinish(int pinblockLen, byte[] pinblock,byte[] ksn) {
            Log.i(TAG, "Is Success");
            if (pinblockLen == 0) {
                Log.i(TAG, getString(R.string.keyboard_activity_log_input_empty));
                Intent i = new Intent();
                i.putExtra("pin", new byte[]{});
                setResult(RESULT_OK, i);
                finish();
                if (pinEntryListener != null) {
                    pinEntryListener.onFinish(new byte[]{});
                }
            } else {
                AppConfig.EMV.pinBlock = pinblock;
                Log.i(TAG, getString(R.string.keyboard_activity_log_input_success) + ISOUtils.hexString(pinblock)+";ksn:"+(ksn==null?null:ISOUtils.hexString(ksn)));
                Intent i = new Intent();
                i.putExtra("pin", pinblock);
                setResult(RESULT_OK, i);
                finish();
                if (pinEntryListener != null) {
                    pinEntryListener.onFinish(pinblock);
                }
            }
        }

        @Override
        public void onTimeout() {
            finish();
            Log.e(TAG, getString(R.string.msg_gd_overtime));
            if (pinEntryListener != null) {
                pinEntryListener.onFinish(null);
            }
        }

        @Override
        public void onError(int errorCode, String message) {
            Log.i(TAG, getString(R.string.keyboard_activity_log_input_exception) + message);
            Intent i = new Intent();
            setResult(-2, i);
            finish();
            if (pinEntryListener != null) {
                pinEntryListener.onFinish(null);
            }
        }
    };

    /**
     * Gets the length range of the input password
     *
     * @param pinMinLen Minimum length allowed
     * @param pinMaxLen Maximum length allowed
     * @return
     */
    private byte[] getPinLengthRange(int pinMinLen, int pinMaxLen) {
        byte[] sumPinLen = new byte[]{0x00, 0x00, 0x00, 0x00, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C};
        byte[] pinLen = new byte[pinMaxLen - pinMinLen + 1];
        System.arraycopy(sumPinLen, pinMinLen, pinLen, 0, pinLen.length);
        return pinLen;
    }

}
