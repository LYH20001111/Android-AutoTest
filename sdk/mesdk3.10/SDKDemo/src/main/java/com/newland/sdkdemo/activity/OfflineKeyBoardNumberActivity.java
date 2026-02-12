package com.newland.sdkdemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.newland.os.NlBuild;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.widget.TextView;

import com.newland.sdk.ModuleManage;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.module.pin.KeyboardRandom;
import com.newland.sdk.module.pin.PinInputExtParams;
import com.newland.sdk.module.pin.PinInputListener;
import com.newland.sdk.module.pin.PinpadModule;
import com.newland.sdk.utils.ISOUtils;
import com.newland.sdkdemo.AppConfig;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.event.PinEntryListener;
import com.newland.sdkdemo.utils.SoundPoolImpl;

/**
 * Password Keyboard Activity
 */
public class OfflineKeyBoardNumberActivity extends Activity {
    private static final DeviceLogger logger = DeviceLoggerFactory.getLogger(OfflineKeyBoardNumberActivity.class);
    private static final String TAG = "OfflineKeyBoardNumber";
    private PinpadModule pinInput;
    private TextView txtPassword;
    private StringBuffer buffer;
    private int inputLen = 0;
    private PinKeyBoard pkb;
    private SoundPoolImpl spi;
    private PinEntryListener pinEntryListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.input_pin_fragment);
        pinInput = ModuleManage.getInstance().getPinpadModule();
        spi = SoundPoolImpl.getInstance();
        spi.initLoad(this);
        pinEntryListener = AppConfig.getPinEntryListener();
        init();
    }

    private void init() {
        txtPassword = (TextView) findViewById(R.id.txt_password);
        pkb = (PinKeyBoard) findViewById(R.id.n900pinkeyboard);
        pkb.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {

            private boolean first;//  To prevent it from entering the onPreDraw() all the time.

            @Override
            public boolean onPreDraw() {
                if (!first) {
                    first = true;
                    boolean bool = getRandomKeyBoardNumber();
                    if (!bool) {
                        finish();
                        return first;
                    }
                    int pwMaxLen = 12;
                    byte[] pwdLenRange = getPinLengthRange(0, 12);
                    int timeOut = 59;
                        // when the connection paramters is NS3ConnParams,it uses input offline pin method.
                        byte[] modulus = getIntent().getByteArrayExtra("modulus");// The modulus of actual transaction.it is a parameter of EmvTransInfo through emvTransInfo.getModulus().EmvTransInfo cames from onRequestPinEntry of emv callback.
                        byte[] exponent = getIntent().getByteArrayExtra("exponent");// The exponent of actual transaction.it is a parameter of EmvTransInfo through emvTransInfo.getExponent().EmvTransInfo cames from onRequestPinEntry of emv callback.
                        PinInputExtParams pinInputExtParams = new PinInputExtParams();
                        pinInputExtParams.setInputMaxLen(pwMaxLen);
                        pinInputExtParams.setPwdLengthRange(pwdLenRange);
                        pinInput.startOfflinePinInput(timeOut,modulus, exponent, pinInputListener,pinInputExtParams);
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
    private boolean getRandomKeyBoardNumber() {
        try {
            Log.d(TAG, "getRandomKeyBoardNumber: "+ NlBuild.VERSION.MODEL);
            if(NlBuild.VERSION.MODEL.equals("P300")){
                return true;
            }
            byte[] initCoordinate = pkb.getCoordinate();
            Log.i(TAG, getString(R.string.keyboard_activity_log_init_coordinates) + ISOUtils.hexString(initCoordinate));
            // get key value of random keyboard
            byte[] keySeq = pkb.getPinKeySeq(PinKeyBoard.PinKeySeq.RANDOM_NUM);
            KeyboardRandom keyboardRandom = null;
            // If the number is random and the function key is fixed, do not pass the key value sequence.
            if (keySeq != null) {
                keyboardRandom = new KeyboardRandom(initCoordinate, keySeq);
            } else {
                keyboardRandom = new KeyboardRandom(initCoordinate);
            }

            byte[] randomCoordinate = pinInput.loadRandomKeyboard(keyboardRandom);
            pkb.loadRandomKeyboardfinished(randomCoordinate);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
            if(pinEntryListener!=null){
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
                if(pinEntryListener!=null){
                    pinEntryListener.onFinish(new byte[]{});
                }

            } else {
                AppConfig.EMV.pinBlock = pinblock;
                Log.i(TAG, getString(R.string.keyboard_activity_log_input_success) + ISOUtils.hexString(pinblock)+";ksn:"+(ksn==null?null:ISOUtils.hexString(ksn)));
                Intent i = new Intent();
                i.putExtra("pin", pinblock);
                setResult(RESULT_OK, i);
                finish();
                if(pinEntryListener!=null){
                    pinEntryListener.onFinish(pinblock);
                }
            }
        }

        @Override
        public void onTimeout() {
            finish();
            Log.e(TAG,getString(R.string.msg_gd_overtime));
            if(pinEntryListener!=null){
                pinEntryListener.onFinish(null);
            }
        }

        @Override
        public void onError(int errorCode, String message) {
            Log.i(TAG, getString(R.string.keyboard_activity_log_input_exception)+message);
            Intent i = new Intent();
            setResult(-2, i);
            finish();
            if(pinEntryListener!=null){
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        spi.release();
    }
}
