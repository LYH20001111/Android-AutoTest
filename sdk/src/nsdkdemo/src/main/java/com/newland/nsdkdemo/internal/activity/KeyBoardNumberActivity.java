package com.newland.nsdkdemo.internal.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.TextView;

import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.keymanager.DUKPTDerivateKey;
import com.newland.nsdk.core.api.common.keymanager.DUKPTDerivateUsage;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.api.common.keymanager.KeyUsage;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.common.pinentry.PINBlockMode;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.internal.beeper.Beeper;
import com.newland.nsdk.core.api.internal.pinentry.ExtendedRNIBPINEntryListener;
import com.newland.nsdk.core.api.internal.pinentry.PINEntry;
import com.newland.nsdk.core.api.internal.pinentry.PINEntryListener;
import com.newland.nsdk.core.api.internal.pinentry.PINEntryParameters;
import com.newland.nsdk.core.api.internal.pinentry.PINPadButton;
import com.newland.nsdk.core.api.internal.pinentry.RNIBPINEntryListener;
import com.newland.nsdk.core.internal.NSDKModuleManagerImpl;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.AppConfig;
import com.newland.nsdkdemo.common.MessageEvent;
import com.newland.nsdkdemo.common.utils.MessageTag;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;


/**
 * Password Keyboard Activity
 */
public class KeyBoardNumberActivity extends Activity {
    private static final String TAG = "KeyBoardNumberActivity";
    public static final int KEY_TYPE_DES = 0;
    public static final int KEY_TYPE_AES = 1;
    public static final int KEY_TYPE_DUKPT = 2;
    public static final int KEY_TYPE_AES_DUKPT = 3;
    public boolean isRandomLayout = true;
    private String PAN;
    private int screenWidth = 0;
    private int screenHeight = 0;
    private double mW = 1;
    private  double mH = 1;
    private int screenRotation = 0;
    private byte keyID = 0;
    private boolean isVirtualPINPad = true;
    private boolean isCheckIcPresent = false;
    private boolean isCheckPINRange = false;
    private boolean isEnableCustomFunctionKey = false;
    private TextView txtPassword;
    private StringBuffer buffer;
    private PinKeyBoard pkb;
    private RNIBPinKeyBoard rnibPkb;
    private PINEntry pinInput;
    private Beeper beeper;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor mEditor;
    // keytype, 0-DES, 1-AES, 2-DUKPT
    private int keyType = KEY_TYPE_DES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("PINENTRY", MODE_PRIVATE);
        mEditor = sharedPreferences.edit();
        pinInput = (PINEntry) NSDKModuleManagerImpl.getInstance().getModule(ModuleType.PIN_ENTRY);
        beeper = (Beeper) NSDKModuleManagerImpl.getInstance().getModule(ModuleType.BEEPER);
        keyType = getIntent().getIntExtra("keyType", KEY_TYPE_DES);
        isRandomLayout = getIntent().getBooleanExtra("isRandomLayout", true);
        PAN = sharedPreferences.getString("PAN","" );
        keyID = getIntent().getByteExtra("KeyID", (byte) 0);
        isVirtualPINPad = getIntent().getBooleanExtra("isVirtualPINPad", true);
        isCheckIcPresent = getIntent().getBooleanExtra("CheckIcPresent", false);
        isCheckPINRange = getIntent().getBooleanExtra("CheckPINRange", false);
        isEnableCustomFunctionKey = getIntent().getBooleanExtra("EnableCustomFunctionKey", false);
        int layoutID = getIntent().getIntExtra("LayoutID", R.layout.input_pin_fragment);
        setContentView(layoutID);
        LogUtils.d(TAG, "**********isVirtualPINPad: " + isVirtualPINPad);
        if(TextUtils.isEmpty(PAN)) {
            PAN = "6212261402009762466";
        }
        LogUtils.d(TAG, PAN);
        init(layoutID);
    }

    private void init(int layoutID) {

        txtPassword = findViewById(R.id.txt_password);
        if (layoutID == R.layout.input_pin_fragment) {
            pkb = findViewById(R.id.n900pinkeyboard);
            if (isVirtualPINPad) {
                pkb.setVisibility(View.VISIBLE);
                pkb.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                    private boolean first;//  To prevent it from entering the onPreDraw() all the time.

                    @Override
                    public boolean onPreDraw() {
                        if (!first) {
                            first = true;
                            boolean bool = initPINPadLayout();
                            if (!bool) {
                                finish();
                                return first;
                            }
                            startPINEntry();
                        }

                        return first;
                    }
                });
            } else {
                pkb.setVisibility(View.INVISIBLE);
                startPINEntry();
            }
        } else {
            rnibPkb = findViewById(R.id.rnibKeyboard);
            rnibPkb.setVisibility(View.VISIBLE);
            rnibPkb.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                private boolean first;
                @Override
                public boolean onPreDraw() {
                    if (!first) {
                        first = true;
                        boolean bool = initRNIBKeyLayout();
                        if (!bool) {
                            finish();
                            return first;
                        }
                        startRNIBPINEntry();
                    }
                    return first;
                }
            });
        }

    }

    private void startPINEntry() {
        SymmetricKey desKey = new SymmetricKey();

        final PINEntryParameters params = new PINEntryParameters();
        params.setPINBlockMode(PINBlockMode.ISO9564_0);
        if(keyID != (byte) 0) {
            if (keyType == 1) {
                desKey.setKeyType(KeyType.AES);
                desKey.setKeyUsage(KeyUsage.PIN);
                params.setPINBlockMode(PINBlockMode.ISO9564_4);
            } else if (keyType == 2){
                desKey.setKeyType(KeyType.DES);
                desKey.setKeyUsage(KeyUsage.DUKPT);
            } else if (keyType == 3) {
                desKey = new DUKPTDerivateKey();
                desKey.setKeyType(KeyType.AES);
                desKey.setKeyUsage(KeyUsage.DUKPT);
                ((DUKPTDerivateKey)desKey).setDerivateKeyLen(16);
                ((DUKPTDerivateKey)desKey).setDerivateKeyType(KeyType.AES);
                ((DUKPTDerivateKey)desKey).setDerivateUsage(DUKPTDerivateUsage.PIN);
                params.setPINBlockMode(PINBlockMode.ISO9564_4);
            } else {
                desKey.setKeyType(KeyType.DES);
                desKey.setKeyUsage(KeyUsage.PIN);
            }
            desKey.setKeyID(keyID);
        }else {
            if (keyType == 1) {
                desKey.setKeyID(AppConfig.Keys.MKSK_AES_INDEX_WK_PIN);
                desKey.setKeyType(KeyType.AES);
                desKey.setKeyUsage(KeyUsage.PIN);
                params.setPINBlockMode(PINBlockMode.ISO9564_4);
            } else if (keyType == 2){
                desKey.setKeyID(AppConfig.Keys.DUKPT_DES_INDEX);
                desKey.setKeyType(KeyType.DES);
                desKey.setKeyUsage(KeyUsage.DUKPT);
            } else if (keyType == 3) {
                desKey = new DUKPTDerivateKey();
                desKey.setKeyID(AppConfig.Keys.DUKPT_AES_INDEX);
                desKey.setKeyType(KeyType.AES);
                desKey.setKeyUsage(KeyUsage.DUKPT);

                ((DUKPTDerivateKey)desKey).setDerivateKeyLen(16);
                ((DUKPTDerivateKey)desKey).setDerivateKeyType(KeyType.AES);
                ((DUKPTDerivateKey)desKey).setDerivateUsage(DUKPTDerivateUsage.PIN);

                params.setPINBlockMode(PINBlockMode.ISO9564_4);
            } else {
                desKey.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_WK_PIN);
                desKey.setKeyType(KeyType.DES);
                desKey.setKeyUsage(KeyUsage.PIN);
            }
        }


        params.setMinPINLen(6);
        params.setMaxPINLen(8);
        params.setPINLengthRange(new byte[]{0x06, 0x08, 0x09});
        params.setCheckIcPresent(isCheckIcPresent);
        params.setCheckPINRange(isCheckPINRange);
        //Current device only support set "CANCEL" key with quit function and "BACKSPACE" key with clear function.
        if (isEnableCustomFunctionKey) {
            Map<PINPadButton, PINPadButton> customButtons = new HashMap<>();
            customButtons.put(PINPadButton.CANCEL, PINPadButton.QUIT);
            customButtons.put(PINPadButton.BACKSPACE, PINPadButton.CLEAR);
            params.setCustomButtons(customButtons);
        }

        int ret = 0;
        try {
            pinInput.startOnlinePINEntry(desKey, PAN,30, params, mPInInputListener);
        } catch (NSDKException e) {
            // todo Handle the exception
            e.printStackTrace();
            mPInInputListener.onError(e.getCode(), e.getMessage());
            finish();
        }
    }

    private void startRNIBPINEntry() {
        SymmetricKey desKey = new SymmetricKey();

        final PINEntryParameters params = new PINEntryParameters();
        params.setPINBlockMode(PINBlockMode.ISO9564_0);
        if(keyID != (byte) 0) {
            if (keyType == 1) {
                desKey.setKeyType(KeyType.AES);
                desKey.setKeyUsage(KeyUsage.PIN);
                params.setPINBlockMode(PINBlockMode.ISO9564_4);
            } else if (keyType == 2){
                desKey.setKeyType(KeyType.DES);
                desKey.setKeyUsage(KeyUsage.DUKPT);
            } else if (keyType == 3) {
                desKey = new DUKPTDerivateKey();
                desKey.setKeyType(KeyType.AES);
                desKey.setKeyUsage(KeyUsage.DUKPT);
                ((DUKPTDerivateKey)desKey).setDerivateKeyLen(16);
                ((DUKPTDerivateKey)desKey).setDerivateKeyType(KeyType.AES);
                ((DUKPTDerivateKey)desKey).setDerivateUsage(DUKPTDerivateUsage.PIN);
                params.setPINBlockMode(PINBlockMode.ISO9564_4);
            } else {
                desKey.setKeyType(KeyType.DES);
                desKey.setKeyUsage(KeyUsage.PIN);
            }
            desKey.setKeyID(keyID);
        }else {
            if (keyType == 1) {
                desKey.setKeyID(AppConfig.Keys.MKSK_AES_INDEX_WK_PIN);
                desKey.setKeyType(KeyType.AES);
                desKey.setKeyUsage(KeyUsage.PIN);
                params.setPINBlockMode(PINBlockMode.ISO9564_4);
            } else if (keyType == 2){
                desKey.setKeyID(AppConfig.Keys.DUKPT_DES_INDEX);
                desKey.setKeyType(KeyType.DES);
                desKey.setKeyUsage(KeyUsage.DUKPT);
            } else if (keyType == 3) {
                desKey = new DUKPTDerivateKey();
                desKey.setKeyID(AppConfig.Keys.DUKPT_AES_INDEX);
                desKey.setKeyType(KeyType.AES);
                desKey.setKeyUsage(KeyUsage.DUKPT);

                ((DUKPTDerivateKey)desKey).setDerivateKeyLen(16);
                ((DUKPTDerivateKey)desKey).setDerivateKeyType(KeyType.AES);
                ((DUKPTDerivateKey)desKey).setDerivateUsage(DUKPTDerivateUsage.PIN);

                params.setPINBlockMode(PINBlockMode.ISO9564_4);
            } else {
                desKey.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_WK_PIN);
                desKey.setKeyType(KeyType.DES);
                desKey.setKeyUsage(KeyUsage.PIN);
            }
        }


        params.setMinPINLen(6);
        params.setMaxPINLen(8);
        params.setPINLengthRange(new byte[]{0x06, 0x08, 0x09});
        params.setCheckIcPresent(isCheckIcPresent);
        params.setCheckPINRange(isCheckPINRange);
        int ret = 0;
        try {
            pinInput.startOnlinePINEntry(desKey, PAN,120, params, extendedRNIBPINEntryListener);
        } catch (NSDKException e) {
            // todo Handle the exception
            e.printStackTrace();
            mPInInputListener.onError(e.getCode(), e.getMessage());
            finish();
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: // inputting
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

    private boolean initPINPadLayout() {
        try {
            Map<PINPadButton, int[]> buttons = pkb.getPINPadButtons();
            byte[] outSeq = pinInput.initKeyLayout(buttons,isRandomLayout);
            pkb.onPINPadInited(outSeq);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            int errCode = -1;
            if (e instanceof NSDKException) {
                errCode = ((NSDKException)e).getCode();
                EventBus.getDefault().post(new MessageEvent(e.getMessage(), MessageTag.ERROR));
            }
            mPInInputListener.onError(errCode, e.getMessage());
        }
        return false;
    }

    private boolean initPINPadLayout2() {
        try {
            byte[] numbtn = new byte[80];
            byte[] funbtn = new byte[36];

            pkb.getCoordinates(numbtn, funbtn, isRandomLayout);

            byte[] outSeq = pinInput.initKeyLayout(numbtn, funbtn,isRandomLayout);
            pkb.onPINPadInited(outSeq);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            int errCode = -1;
            if (e instanceof NSDKException) {
                errCode = ((NSDKException)e).getCode();
            }
            mPInInputListener.onError(errCode, e.getMessage());
        }
        return false;
    }

    private boolean initRNIBKeyLayout() {
        try {
            pinInput.initKeyLayout(rnibPkb.getButtonsCoordinates(), rnibPkb.getAreaCoordination());
            return true;
        } catch (NSDKException e) {
            e.printStackTrace();
            postErrorMessage(e.getMessage());
            return false;
        }
    }

    private int mCurrKeyLen = 0;

    private void setKeyLen(int len) {
        Message msg = mHandler.obtainMessage(1);
        msg.obj = len;
        msg.sendToTarget();
    }

    private PINEntryListener mPInInputListener = new PINEntryListener() {
        @Override
        public void onFinish(int pinLen, byte[] pinBlock, byte[] ksn) {
            LogUtils.d(TAG, "onFinish: pinLen=" + pinLen);
            LogUtils.d(TAG, "onFinish: pinblock=" + (pinBlock != null ? ISOUtils.hexString(pinBlock) : "null"));
            LogUtils.d(TAG, "onFinish: ksn=" + (ksn != null ? ISOUtils.hexString(ksn) : "null"));
            String pinBlockStr = "PIN Block=";
            String ksnStr = null;
            String displayMessage = null ;
            if (pinBlock != null) {
                pinBlockStr += (pinBlock.length == 0 ? "null" : ISOUtils.hexString(pinBlock));
                ksnStr = "KSN=" + (ksn == null ? "null" : ISOUtils.hexString(ksn));
                if (keyType == KEY_TYPE_DUKPT || keyType == KEY_TYPE_AES_DUKPT) {
                    displayMessage = String.format("%s,%s(Please increase KSN before next DUKPT PIN entry)", pinBlockStr, ksnStr);
                }
            }
            displayMessage = pinBlockStr;
            finish();
            LogUtils.d(TAG, String.valueOf(isFinishing()));
            EventBus.getDefault().post(new MessageEvent(displayMessage, MessageTag.NORMAL));
        }

        @Override
        public void onTimeout() {
            LogUtils.d(TAG, "onTimeOut: ");
            finish();
            EventBus.getDefault().post(new MessageEvent("onTimeOut", MessageTag.NORMAL));
        }

        @Override
        public void onKeyPress() {
            LogUtils.d(TAG, "onKeyPress: ");
            mCurrKeyLen = mCurrKeyLen + 1;
            setKeyLen(mCurrKeyLen);
            EventBus.getDefault().post(new MessageEvent("onKeyPress", MessageTag.NORMAL));
        }

        @Override
        public void onCancel() {
            LogUtils.d(TAG, "onCancel: ");
            finish();
            EventBus.getDefault().post(new MessageEvent("onCancel", MessageTag.NORMAL));
        }

        @Override
        public void onClear() {
            LogUtils.d(TAG, "onClear: ");
            mCurrKeyLen = 0;
            setKeyLen(mCurrKeyLen);
            EventBus.getDefault().post(new MessageEvent("onClear", MessageTag.NORMAL));
        }

        @Override
        public void onBackspace() {
            LogUtils.d(TAG, "onBackspace: ");
            mCurrKeyLen = mCurrKeyLen - 1;
            if (mCurrKeyLen < 0) {
                mCurrKeyLen = 0;
            }
            setKeyLen(mCurrKeyLen);
            EventBus.getDefault().post(new MessageEvent("onBackspace", MessageTag.NORMAL));
        }

        @Override
        public void onError(int errorCode, String message) {
            LogUtils.d(TAG, String.format("onError: %d, %s", errorCode, message));
            finish();
            EventBus.getDefault().post(new MessageEvent(String.format("onError: %d, %s", errorCode, message), MessageTag.ERROR));
        }
    };

    private ExtendedRNIBPINEntryListener extendedRNIBPINEntryListener = new ExtendedRNIBPINEntryListener() {
        @Override
        public void onPINLengthInsufficient() {
            postTipMessage("PIN length too short");
        }

        @Override
        public void onPINLengthExceeded() {
            postTipMessage("PIN length exceeded.");
        }

        @Override
        public void onSlidNumberKey() {
            try {
                beeper.beep(3000, 500);
                postNormalMessage("Slid to number key.");
            } catch (NSDKException e) {
                postErrorMessage(e.getMessage());
            }
        }

        @Override
        public void onSlidNoDigitKey() {
            //Play alert voice.
        }

        @Override
        public void onSlidBackSpace() {
            postNormalMessage("Slid to BackSpace function key.");
            playAudio(R.raw.last_digit_cleared);
        }

        @Override
        public void onSlidEnter() {
            postNormalMessage("Slid to Enter function key.");
            playAudio(R.raw.all_digits_entered);
        }

        @Override
        public void onSlidCancel() {
            postNormalMessage("Slid to Cancel function key.");
            playAudio(R.raw.transaction_cancelled);
        }

        @Override
        public void onSlidUp() {
            try {
                beeper.beep(2000, 500);
                postNormalMessage("Slid above the keyboard.");
            } catch (NSDKException e) {
                postErrorMessage(e.getMessage());
            }
        }

        @Override
        public void onSlidDown() {
            try {
                beeper.beep(2000, 500);
                postNormalMessage("Slid below the keyboard.");
            } catch (NSDKException e) {
                postErrorMessage(e.getMessage());
            }
        }

        @Override
        public void onSlidLeft() {
            try {
                beeper.beep(2000, 500);
                postNormalMessage("Slid to the left of the keyboard.");
            } catch (NSDKException e) {
                postErrorMessage(e.getMessage());
            }
        }

        @Override
        public void onSlidRight() {
            try {
                beeper.beep(2000, 500);
                postNormalMessage("Slid to the right of the keyboard.");
            } catch (NSDKException e) {
                postErrorMessage(e.getMessage());
            }
        }

        @Override
        public void onFinish(int pinLen, byte[] pinBlock, byte[] ksn) {
            LogUtils.d(TAG, "onFinish: pinLen=" + pinLen);
            LogUtils.d(TAG, "onFinish: pinblock=" + (pinBlock != null ? ISOUtils.hexString(pinBlock) : "null"));
            LogUtils.d(TAG, "onFinish: ksn=" + (ksn != null ? ISOUtils.hexString(ksn) : "null"));
            String pinBlockStr = "PIN Block=";
            String ksnStr = null;
            String displayMessage = null ;
            if (pinBlock != null) {
                pinBlockStr += (pinBlock.length == 0 ? "null" : ISOUtils.hexString(pinBlock));
                ksnStr = "KSN=" + (ksn == null ? "null" : ISOUtils.hexString(ksn));
                if (keyType == KEY_TYPE_DUKPT || keyType == KEY_TYPE_AES_DUKPT) {
                    displayMessage = String.format("%s,%s(Please increase KSN before next DUKPT PIN entry)", pinBlockStr, ksnStr);
                }
            }
            displayMessage = pinBlockStr;
            finish();
            LogUtils.d(TAG, String.valueOf(isFinishing()));
            EventBus.getDefault().post(new MessageEvent(displayMessage, MessageTag.NORMAL));
        }

        @Override
        public void onTimeout() {
            LogUtils.d(TAG, "onTimeOut: ");
            finish();
            EventBus.getDefault().post(new MessageEvent("onTimeOut", MessageTag.NORMAL));
        }

        @Override
        public void onKeyPress() {
            LogUtils.d(TAG, "onKeyPress: ");
            mCurrKeyLen = mCurrKeyLen + 1;
            setKeyLen(mCurrKeyLen);
            EventBus.getDefault().post(new MessageEvent("onKeyPress", MessageTag.NORMAL));
        }

        @Override
        public void onCancel() {
            LogUtils.d(TAG, "onCancel: ");
            finish();
            EventBus.getDefault().post(new MessageEvent("onCancel", MessageTag.NORMAL));
        }

        @Override
        public void onClear() {
            LogUtils.d(TAG, "onClear: ");
            mCurrKeyLen = 0;
            setKeyLen(mCurrKeyLen);
            EventBus.getDefault().post(new MessageEvent("onClear", MessageTag.NORMAL));
        }

        @Override
        public void onBackspace() {
            LogUtils.d(TAG, "onBackspace: ");
            mCurrKeyLen = mCurrKeyLen - 1;
            if (mCurrKeyLen < 0) {
                mCurrKeyLen = 0;
            }
            setKeyLen(mCurrKeyLen);
            EventBus.getDefault().post(new MessageEvent("onBackspace", MessageTag.NORMAL));
        }

        @Override
        public void onError(int errorCode, String message) {
            LogUtils.d(TAG, String.format("onError: %d, %s", errorCode, message));
            finish();
            EventBus.getDefault().post(new MessageEvent(String.format("onError: %d, %s", errorCode, message), MessageTag.ERROR));
        }
    };

    private void postNormalMessage(String message) {
        EventBus.getDefault().post(new MessageEvent(message, MessageTag.NORMAL));
    }

    private void postTipMessage(String message) {
        EventBus.getDefault().post(new MessageEvent(message, MessageTag.TIP));
    }

    private void postErrorMessage(String message) {
        EventBus.getDefault().post(new MessageEvent(message, MessageTag.ERROR));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void playAudio(int rawID) {
        MediaPlayer mediaPlayer = MediaPlayer.create(KeyBoardNumberActivity.this, rawID);
        mediaPlayer.start();
    }


}
