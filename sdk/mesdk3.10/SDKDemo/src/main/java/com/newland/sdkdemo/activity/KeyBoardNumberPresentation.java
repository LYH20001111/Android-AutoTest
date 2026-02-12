package com.newland.sdkdemo.activity;

import android.app.Activity;
import android.app.Presentation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.newland.os.NlBuild;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.widget.TextView;

import com.newland.sdk.ModuleManage;
import com.newland.sdk.module.pin.AccountInputType;
import com.newland.sdk.module.pin.AlgorithmMode;
import com.newland.sdk.module.pin.KeyManagement;
import com.newland.sdk.module.pin.KeyboardRandom;
import com.newland.sdk.module.pin.PinInputExtListener;
import com.newland.sdk.module.pin.PinInputExtParams;
import com.newland.sdk.module.pin.PinpadModule;
import com.newland.sdk.utils.ISOUtils;
import com.newland.sdkdemo.AppConfig;
import com.newland.sdkdemo.MainActivity;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.event.PinEntryListener;
import com.newland.sdkdemo.utils.MessageTag;
import com.newland.sdkdemo.utils.SoundPoolImpl;

import static android.app.Activity.RESULT_OK;


/**
 * Password Keyboard Activity
 */
public class KeyBoardNumberPresentation extends Presentation {
    private static final String TAG = "KeyBoardNumberActivity";
    private PinpadModule pinInput;
    private TextView txtPassword;
    private StringBuffer buffer;
    private int inputLen = 0;
    private PinKeyBoard pkb;
    private SoundPoolImpl spi;
    private PinEntryListener pinEntryListener;
    private Context context;

    public KeyBoardNumberPresentation(Context outerContext, Display display) {
        super(outerContext, display);
        context = outerContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_pin_presentation);
        pinInput = ModuleManage.getInstance().getPinpadModule();
        spi = SoundPoolImpl.getInstance();
        spi.initLoad(context);
        pinEntryListener = AppConfig.getPinEntryListener();
        init();
    }

    private void init() {
        txtPassword = (TextView) findViewById(R.id.txt_password);
        pkb = (PinKeyBoard) findViewById(R.id.n900pinkeyboard);
        final String accNo = AppConfig.accNo;//context.getIntent().getStringExtra("accNo");
        pkb.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
            private boolean first;//  To prevent it from entering the onPreDraw() all the time.

            @Override
            public boolean onPreDraw() {
                if (!first) {
                    first = true;
                    boolean bool = getRandomKeyBoardNumber();
                    if (!bool) {
                        cancel();
                        return first;
                    }
                    int wkPinIndex =1;
                    AccountInputType acctInputType = AccountInputType.USE_ACCOUNT;
                    int pwMaxLen = 12;
                    byte[] pwdLenRange = getPinLengthRange(0, 12);
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
                    if(accNo!=null && !"".equals(accNo)){
                        pinInputExtParams.setAcctInputType(AccountInputType.USE_ACCOUNT);
                    }else{
                        pinInputExtParams.setAcctInputType(AccountInputType.UNUSE_ACCOUNT);
                    }

                    pinInput.startPinInput(keyManagement, algorithmMode, wkPinIndex,
                            accNo, timeOut, pinInputListener,pinInputExtParams);
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
            byte[] initCoordinate = pkb.getCoordinate();
            Log.i(TAG, context.getString(R.string.keyboard_activity_log_init_coordinates) + ISOUtils.hexString(initCoordinate));
            // get key value of random keyboard
            byte[] keySeq = pkb.getPinKeySeq(PinKeyBoard.PinKeySeq.NORMAL);
            KeyboardRandom keyboardRandom = null;
            // If the number is random and the function key is fixed, do not pass the key value sequence.
            if (keySeq != null) {
                keyboardRandom = new KeyboardRandom(initCoordinate, keySeq);
            } else {
                keyboardRandom = new KeyboardRandom(initCoordinate);
            }

            byte[] randomCoordinate = pinInput.loadRandomKeyboard(keyboardRandom);
            Log.d(TAG, "getRandomKeyBoardNumber: "+ ISOUtils.hexString(randomCoordinate));
            pkb.loadRandomKeyboardfinished(randomCoordinate);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private PinInputExtListener pinInputListener = new PinInputExtListener() {
        @Override
        public void onNotifyStep(byte b) {
            inputLen = 0;
            Log.i(TAG, context.getString(R.string.keyboard_activity_log_press_key_code) + b);
            Message msg = mHandler.obtainMessage(2);
            msg.obj = inputLen;
            msg.sendToTarget();
        }

        @Override
        public void onKeyPress() {
            inputLen = inputLen + 1;
            Log.i(TAG, context.getString(R.string.keyboard_activity_log_press_key_code) + inputLen);
            Message msg = mHandler.obtainMessage(2);
            msg.obj = inputLen;
            msg.sendToTarget();
        }

        @Override
        public void onBackspace() {
            inputLen = (inputLen <= 0 ? 0 : inputLen - 1);
            Log.i(TAG, context.getString(R.string.keyboard_activity_log_press_cancel_code) + inputLen);
            Message msg = mHandler.obtainMessage(2);
            msg.obj = inputLen;
            msg.sendToTarget();
        }

        @Override
        public void onCancel() {
            Log.i(TAG, "Is UserCanceled");
            cancel();
            if(pinEntryListener!=null){
                pinEntryListener.onFinish(null);
            }
        }

        @Override
        public void onFinish(int pinblockLen, byte[] pinblock,byte[] ksn) {
            Log.i(TAG, "Is Success");
            if (pinblockLen == 0) {
                Log.i(TAG, context.getString(R.string.keyboard_activity_log_input_empty));
                Intent i = new Intent();
                i.putExtra("pin", new byte[]{});
                setResult(RESULT_OK, i);
                cancel();
                if(pinEntryListener!=null){
                    pinEntryListener.onFinish(new byte[]{});
                }
            } else {
                AppConfig.EMV.pinBlock = pinblock;
                Log.i(TAG, context.getString(R.string.keyboard_activity_log_input_success) + (pinblock==null?null:ISOUtils.hexString(pinblock))+"ksn:"+(ksn==null?null:ISOUtils.hexString(ksn)));
                Intent i = new Intent();
                i.putExtra("pin", pinblock);
                setResult(RESULT_OK, i);
                cancel();
                if(pinEntryListener!=null){
                    pinEntryListener.onFinish(pinblock);
                }

            }
        }

        @Override
        public void onTimeout() {
            cancel();
            Log.e(TAG,context.getString(R.string.msg_gd_overtime));
            if(pinEntryListener!=null){
                pinEntryListener.onFinish(null);
            }
        }

        @Override
        public void onError(int errorCode, String message) {
            Log.i(TAG, context.getString(R.string.keyboard_activity_log_input_exception)+message);
            Intent i = new Intent();
            setResult(-2, i);
            cancel();
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
    protected void onStop() {
        super.onStop();
        spi.release();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Rect rect = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rect);
        // status bar height
        int statusBarHeight = rect.top;

        // title bar height + status bar height
        int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();

        // title bar height
        int contentViewHeight = contentViewTop - statusBarHeight;
        // Give it different values depending on the platform.
        Log.i(TAG, "contentViewHeight=" + contentViewHeight + ";contentViewTop" + contentViewTop + "statusBarHeight" + statusBarHeight);

        // application area
        Rect outRect1 = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect1);
        Log.i(TAG, context.getString(R.string.keyboard_activity_log_application_top) + outRect1.top);// the status bar height is 50dp,The navigation bar height is 96dp.
        Log.i(TAG, context.getString(R.string.keyboard_activity_log_application_height) + outRect1.height());

        //View draw area
        Rect outRect2 = new Rect();
        getWindow().findViewById(Window.ID_ANDROID_CONTENT).getDrawingRect(outRect2);
        Log.i(TAG, context.getString(R.string.keyboard_activity_log_view_draw_area_top_error_method) + outRect2.top);   // get outRect2.top don't like the above,it maybe get 0 with outRect2.top, maybe a bug.
        int viewTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();   // right method
        Log.i(TAG, context.getString(R.string.keyboard_activity_log_view_draw_area_top_right_method) + viewTop);  // status bar height + title bar height=146
        Log.i(TAG, context.getString(R.string.keyboard_activity_log_view_draw_area_top) + outRect2.height());


        String TOUCHSCREEN_RESOLUTION = NlBuild.VERSION.TOUCHSCREEN_RESOLUTION;
        int height = Integer.valueOf(TOUCHSCREEN_RESOLUTION.split("x")[0]); // Get Touch resolution of K21
        // Geometric scaling
        int width = Integer.valueOf(TOUCHSCREEN_RESOLUTION.split("x")[1]);
        Log.i(TAG, "TOUCHSCREEN_RESOLUTION：height" + height + "width：" + width);
    }
    private void setResult(int resultCode,Intent intent){
        ((MainActivity)context).onActivityResult(002,resultCode,intent);
    }
}
