package com.newland.nsdkdemo.internal.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
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
import com.newland.nsdk.core.api.internal.NSDKModuleManager;
import com.newland.nsdk.core.api.internal.pinentry.PINEntry;
import com.newland.nsdk.core.api.internal.pinentry.PINEntryListener;
import com.newland.nsdk.core.api.internal.pinentry.PINEntryParameters;
import com.newland.nsdk.core.internal.NSDKModuleManagerImpl;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.AppConfig;
import com.newland.nsdkdemo.common.MessageEvent;
import com.newland.nsdkdemo.common.utils.MessageTag;
import com.newland.nsdkdemo.internal.pin.PinPadInitUtils;
import com.newland.nsdkdemo.internal.pin.SecondDisplayManager;

import org.greenrobot.eventbus.EventBus;

public class PINEntry2SecondDisplayActivity extends AppCompatActivity {
    private SecondDisplayManager secondDisplayManager;
    public static final int KEY_TYPE_DES = 0;
    public static final int KEY_TYPE_AES = 1;
    public static final int KEY_TYPE_DUKPT = 2;
    public static final int KEY_TYPE_AES_DUKPT = 3;
    private TextView firstDisplay_Message,  pinInputState;
    private int backspaceFlag = -1;
    private NSDKModuleManager nsdkModuleManager = NSDKModuleManagerImpl.getInstance();
    EventBus eventBus = EventBus.getDefault();
    private PINEntry pinInput;
    private TextView cipherText;
    private boolean isRandomLayout = false;
    private int keyType = 0;
    private int currentKeyLen = 0;
    private StringBuffer buffer;
    private String textbuffer;
    private Button[] numButtons = new Button[10];
    private Button back, cancel, enter;
    private byte[] funcBtnLayout = new byte[36];
    private byte[] numBtnLayout = new byte[80];
    private String PAN;
    private PinPadInitUtils pinPadInitUtils;
    private AlertDialog firstDisplayDialog;
    private boolean isCheckIcPresent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firstdisplay_alert_dialog);
        final Display display = SecondDisplayManager.getSecondDisplay(this);
        if (display != null) {
            View view = getLayoutInflater().inflate(R.layout.firstdisplay_alert_dialog, null);
            AlertDialog.Builder firstDisplayAlertDialog = new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setView(view)
                    .setTitle(getIntent().getStringExtra("moduleItem"));
            firstDisplayDialog = firstDisplayAlertDialog.create();
            firstDisplayDialog.show();
            firstDisplay_Message = view.findViewById(R.id.firstDisplay_PinMessage);
            pinInputState = view.findViewById(R.id.pinInputState);
            secondDisplayManager = new SecondDisplayManager(this, R.layout.activity_second_display2);
            secondDisplayManager.onDisplayAdded(1);
            secondDisplayManager.getWindow().addFlags(3);
            secondDisplayManager.getWindow().addFlags(5);
            secondDisplayManager.show();
            Bundle bundle = new Bundle();
            bundle = getIntent().getExtras();
            isRandomLayout = bundle.getBoolean("isRandomLayout", true);
            keyType = bundle.getInt("keyType", 0);
            PAN = bundle.getString("PAN", "6212261402009762466");
            isCheckIcPresent = getIntent().getBooleanExtra("CheckIcPresent", false);
            pinInput = (PINEntry) nsdkModuleManager.getModule(ModuleType.PIN_ENTRY);
            onStartPin();
        }

    }

    private void onStartPin() {
        cipherText = secondDisplayManager.findViewById(R.id.cipherPassword);
        cipherText.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            boolean first = false;
            @Override
            public boolean onPreDraw() {
                if (!first) {
                    first = true;
                    boolean initialreq = initPINPadLayout();
                    if (!initialreq) {
                        finish();
                        return first;
                    }
                    SymmetricKey desKey = new SymmetricKey();
                    final PINEntryParameters pinEntryParameters = new PINEntryParameters();
                    pinEntryParameters.setPINBlockMode(PINBlockMode.ISO9564_0);

                    if (keyType == KEY_TYPE_AES) {
                        desKey.setKeyID(AppConfig.Keys.MKSK_AES_INDEX_WK_PIN);
                        desKey.setKeyUsage(KeyUsage.PIN);
                        desKey.setKeyType(KeyType.AES);
                        pinEntryParameters.setPINBlockMode(PINBlockMode.ISO9564_4);
                    } else if (keyType == KEY_TYPE_DUKPT) {
                        desKey.setKeyType(KeyType.DES);
                        desKey.setKeyID(AppConfig.Keys.DUKPT_DES_INDEX);
                        desKey.setKeyUsage(KeyUsage.DUKPT);
                    } else if(keyType == KEY_TYPE_AES_DUKPT) {
                        desKey = new DUKPTDerivateKey();
                        desKey.setKeyID(AppConfig.Keys.DUKPT_AES_INDEX);
                        desKey.setKeyType(KeyType.AES);
                        desKey.setKeyUsage(KeyUsage.DUKPT);

                        ((DUKPTDerivateKey)desKey).setDerivateKeyLen(16);
                        ((DUKPTDerivateKey)desKey).setDerivateKeyType(KeyType.AES);
                        ((DUKPTDerivateKey)desKey).setDerivateUsage(DUKPTDerivateUsage.PIN);

                        pinEntryParameters.setPINBlockMode(PINBlockMode.ISO9564_4);
                    } else {
                        desKey.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_WK_PIN);
                        desKey.setKeyUsage(KeyUsage.PIN);
                        desKey.setKeyType(KeyType.DES);
                    }
                    pinEntryParameters.setMaxPINLen(8);
                    pinEntryParameters.setMinPINLen(6);
                    pinEntryParameters.setPINLengthRange(new byte[]{0x06, 0x08, 0x09});
                    pinEntryParameters.setCheckIcPresent(isCheckIcPresent);
                    try {
                        pinInput.startOnlinePINEntry(desKey, "6212261402009762466", 30, pinEntryParameters, pinEntryListener);
                    } catch (NSDKException e) {
                        pinEntryListener.onError(e.getCode(), e.getMessage());
                        finish();
                    }
                }
                return first;
            }
        });
    }


    private void setKeyLen(int len) {
        buffer = new StringBuffer();
        for (int i = 0; i < len; i++) {
            buffer.append("*");
        }
        if(backspaceFlag != 1) {
            textbuffer = getResources().getString(R.string.msg_pin_pininput_inputkey_tip);
        }else {
            textbuffer= getResources().getString(R.string.msg_pin_pininput_deletekey_tip);
            backspaceFlag = 0;
        }
        eventBus.post(new MessageEvent(textbuffer, MessageTag.NORMAL));
        Message msg = new Message();
        msg.what = 100;
        msg.obj = buffer;
        mHandler.sendMessage(msg);
    }


    private PINEntryListener pinEntryListener = new PINEntryListener() {
        @Override
        public void onFinish(int pinLen, byte[] pinBlock, byte[] KSN) {
            String pinBlockData = "PIN Block = " + (pinBlock.length == 0 ? "null" : ISOUtils.hexString(pinBlock));
            String ksn = "KSN = " + (KSN == null ? "null" : ISOUtils.hexString(KSN));
            String result;
            if (keyType == KEY_TYPE_DUKPT || keyType == KEY_TYPE_AES_DUKPT) {
                result = String.format("%s <br> %s <br> %s", pinBlockData, ksn, getResources().getString(R.string.msg_pin_pininput_check_ksn));
            } else {
                result = pinBlockData;
            }
            currentKeyLen = 0;
            secondDisplayManager.dismiss();
            secondDisplayManager.release();
            Message msg = new Message();
            msg.what = 101;
            msg.obj = result;
            mHandler.sendMessage(msg);
        }

        @Override
        public void onTimeout() {
            finish();
            EventBus.getDefault().post(new MessageEvent(getResources().getString(R.string.msg_pin_pininput_timeout), MessageTag.ERROR));
            pinInputState.setTextColor(Color.RED);
            firstDisplayDialog.dismiss();
            secondDisplayManager.dismiss();
            secondDisplayManager.release();
            finish();
        }

        @Override
        public void onKeyPress() {
            currentKeyLen = currentKeyLen + 1;
            setKeyLen(currentKeyLen);
            pinInputState.setText(getResources().getString(R.string.msg_pin_pininput_first_display_tv_state_inputpin));
        }

        @Override
        public void onCancel() {
            eventBus.post(new MessageEvent(getResources().getString(R.string.msg_pin_pininput_oncancel_tip), MessageTag.NORMAL));
            firstDisplayDialog.dismiss();
            secondDisplayManager.dismiss();
            secondDisplayManager.release();
            finish();
        }

        @Override
        public void onClear() {
            currentKeyLen = 0;
            setKeyLen(currentKeyLen);
            pinInputState.setText(getResources().getString(R.string.msg_pin_pininput_first_display_tv_state_onclear));
            firstDisplay_Message.setText(getResources().getString(R.string.msg_pin_pininput_first_display_tv_onclear));
            eventBus.post(new MessageEvent(getResources().getString(R.string.msg_pin_pininput_onclear), MessageTag.NORMAL));
        }

        @Override
        public void onBackspace() {
            currentKeyLen = currentKeyLen - 1;
            if (currentKeyLen < 0) {
                currentKeyLen = 0;
            }
            setKeyLen(currentKeyLen);
            backspaceFlag = 1;
            pinInputState.setText(getResources().getString(R.string.msg_pin_pininput_first_display_tv_state_onbackspace));
        }

        @Override
        public void onError(int errorCode, String Error) {
            firstDisplayDialog.dismiss();
            secondDisplayManager.dismiss();
            secondDisplayManager.release();
            eventBus.post(new MessageEvent(Error, MessageTag.ERROR));
            finish();
        }
    };


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            cipherText = secondDisplayManager.findViewById(R.id.cipherPassword);
            switch (msg.what) {
                case 100:
                    StringBuffer buffer = (StringBuffer) msg.obj;
                    firstDisplay_Message.setText(buffer.toString());
                    cipherText.setText(buffer.toString());
                    break;
                case 101:
                    String result = (String) msg.obj;
                    eventBus.post(new MessageEvent(result, MessageTag.NORMAL));
                    firstDisplayDialog.dismiss();
                    finish();
                    break;
                default:
                    break;
            }
        }
    };


    private boolean initPINPadLayout() {
        cipherText = secondDisplayManager.findViewById(R.id.cipherPassword);
        pinPadInitUtils = new PinPadInitUtils(this);
        back = secondDisplayManager.findViewById(R.id.delete);
        cancel = secondDisplayManager.findViewById(R.id.Cancel);
        enter = secondDisplayManager.findViewById(R.id.enter);
        numButtons[0] = secondDisplayManager.findViewById(R.id.num0);
        numButtons[1] = secondDisplayManager.findViewById(R.id.num1);
        numButtons[2] = secondDisplayManager.findViewById(R.id.num2);
        numButtons[3] = secondDisplayManager.findViewById(R.id.num3);
        numButtons[4] = secondDisplayManager.findViewById(R.id.num4);
        numButtons[5] = secondDisplayManager.findViewById(R.id.num5);
        numButtons[6] = secondDisplayManager.findViewById(R.id.num6);
        numButtons[7] = secondDisplayManager.findViewById(R.id.num7);
        numButtons[8] = secondDisplayManager.findViewById(R.id.num8);
        numButtons[9] = secondDisplayManager.findViewById(R.id.num9);
        pinPadInitUtils.setBackSpace(back);
        pinPadInitUtils.setCancel(cancel);
        pinPadInitUtils.setEnter(enter);
        pinPadInitUtils.setNumBtn(numButtons);
        funcBtnLayout = pinPadInitUtils.getFuncLayout();
        numBtnLayout = pinPadInitUtils.getNumLayout();
        try {
            byte[] res = pinInput.initKeyLayout(numBtnLayout, funcBtnLayout, isRandomLayout);
            pinPadInitUtils.setRandomLayout(res, isRandomLayout);
            return true;
        }catch (NSDKException e) {
            e.printStackTrace();
        }
        return false;
    }
}
