package com.newland.nsdkdemo.internal.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
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
import com.newland.nsdk.core.api.internal.pinentry.ClickMode;
import com.newland.nsdk.core.api.internal.pinentry.EffectMode;
import com.newland.nsdk.core.api.internal.pinentry.ExtendedEvent;
import com.newland.nsdk.core.api.internal.pinentry.ExtendedEventInfo;
import com.newland.nsdk.core.api.internal.pinentry.KeyboardParameters;
import com.newland.nsdk.core.api.internal.pinentry.PINCustomizedAction;
import com.newland.nsdk.core.api.internal.pinentry.PINEntry2;
import com.newland.nsdk.core.api.internal.pinentry.PINEntry2Listener;
import com.newland.nsdk.core.api.internal.pinentry.PINEntry2Parameters;
import com.newland.nsdk.core.api.internal.pinentry.PINPadButton;
import com.newland.nsdk.core.api.internal.pinentry.TouchState;
import com.newland.nsdk.core.internal.NSDKModuleManagerImpl;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.AppConfig;
import com.newland.nsdkdemo.common.MessageEvent;
import com.newland.nsdkdemo.common.utils.MessageTag;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

public class PINEntry2KeyboardActivity extends Activity {
    private static final int KEY_TYPE_DES = 0;
    private static final int KEY_TYPE_AES = 1;
    private static final int KEY_TYPE_DUKPT = 2;
    private static final int KEY_TYPE_AES_DUKPT = 3;
    private static final String TAG = "PINEntry2KeyboardActivity";

    private static final int NORMAL_PINPAD = 0;
    private static final int ADA_PINPAD = 1;
    private static final int RNIB_PINPAD = 2;
    private static final int FRENCHSYS_B3_PINPAD = 3;
    private static final int SIBS_PINPAD = 4;

    private String PAN;
    private byte keyID = 0;
    private int keyType = KEY_TYPE_DES;
    private int keyboardStyle = 0;
    private boolean isRandomLayout = true;
    private boolean isVirtualPINPad = true;
    private boolean isCheckIcPresent = false;
    private boolean isCheckPINRange = false;
    private boolean isAutoComplete = false;
    private boolean isEnableCustomFunctionKey = false;

    private TextView txtPassword;
    private PinKeyBoard pkb;
    private RNIBPinKeyBoard rnibPkb;
    private FrenchSysB3PinKeyBoard frenchSysB3Pkb;
    private SIBSPinKeyBoard sibsPkb;
    private View adaPkb;

    private StringBuffer buffer;
    private int mCurrKeyLen = 0;

    private PINEntry2 pinEntry2;
    private Beeper beeper;
    private int[] screenArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pinEntry2 = (PINEntry2) NSDKModuleManagerImpl.getInstance().getModule(ModuleType.PIN_ENTRY_2);
        beeper = (Beeper) NSDKModuleManagerImpl.getInstance().getModule(ModuleType.BEEPER);
        screenArea = getScreenArea();

        keyType = getIntent().getIntExtra("keyType", KEY_TYPE_DES);
        isRandomLayout = getIntent().getBooleanExtra("isRandomLayout", true);
        PAN = getIntent().getStringExtra("PAN");
        keyID = getIntent().getByteExtra("KeyID", (byte) 0);
        isVirtualPINPad = getIntent().getBooleanExtra("isVirtualPINPad", true);
        isCheckIcPresent = getIntent().getBooleanExtra("CheckIcPresent", false);
        isCheckPINRange = getIntent().getBooleanExtra("CheckPINRange", false);
        isAutoComplete = getIntent().getBooleanExtra("AutoComplete", false);
        isEnableCustomFunctionKey = getIntent().getBooleanExtra("EnableCustomFunctionKey", false);

        keyboardStyle = getIntent().getIntExtra("KeyboardStyle", 0);

        if (TextUtils.isEmpty(PAN)) {
            PAN = "6217001820027645666";
        }
        LogUtils.d(TAG, "pan: " + PAN);

        init();
    }

    private void init() {
        int layoutID = R.layout.input_pin_fragment;
        if (keyboardStyle == ADA_PINPAD) {
            layoutID = R.layout.input_ada_pin_fragment;
        } else if (keyboardStyle == RNIB_PINPAD) {
            layoutID = R.layout.input_rnib_pin_fragment;
        } else if (keyboardStyle == FRENCHSYS_B3_PINPAD) {
            layoutID = R.layout.input_frenchsys_b3_fragment;
        } else if (keyboardStyle == SIBS_PINPAD) {
            layoutID = R.layout.input_sibs_pin_fragment;
        }
        setContentView(layoutID);
        if (keyboardStyle != SIBS_PINPAD) {
            txtPassword = findViewById(R.id.txt_password);
        }
        switch (keyboardStyle) {
            case NORMAL_PINPAD:
                initNormalPinPad();
                break;
            case ADA_PINPAD:
                initADAPinPad();
                break;
            case RNIB_PINPAD:
                initRNIBPinPad();
                break;
            case FRENCHSYS_B3_PINPAD:
                initFrenchSysB3Pinpad();
                break;
            case SIBS_PINPAD:
                initSIBSPinPad();
                break;
        }
    }

    private void initNormalPinPad() {
        pkb = findViewById(R.id.n900pinkeyboard);
        if (isVirtualPINPad) {
            pkb.setVisibility(View.VISIBLE);
            pkb.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                private boolean first;

                @Override
                public boolean onPreDraw() {
                    if (!first) {
                        first = true;
                        if (!initNormalPINPadLayout() || !setNormalMap()) {
                            finish();
                        } else {
                            startNormalPINEntry();
                        }
                    }
                    return first;
                }
            });
        } else {
            pkb.setVisibility(View.INVISIBLE);
            startNormalPINEntry();
        }
    }

    private void initADAPinPad() {
        adaPkb = findViewById(R.id.collect_secure_pin_ada);
        adaPkb.setVisibility(View.VISIBLE);
        adaPkb.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            private boolean first;

            @Override
            public boolean onPreDraw() {
                if (!first) {
                    first = true;
                    if (!initADAPINPadLayout() || !setADAMap()) {
                        finish();
                    } else {
                        startADAPINEntry();
                    }
                }
                return first;
            }
        });
    }

    private void initRNIBPinPad() {
        rnibPkb = findViewById(R.id.rnibKeyboard);
        rnibPkb.setVisibility(View.VISIBLE);
        rnibPkb.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            private boolean first;

            @Override
            public boolean onPreDraw() {
                if (!first) {
                    first = true;
                    if (!initRNIBKeyLayout() || !setRNIBMap()) {
                        finish();
                    } else {
                        startRNIBPINEntry();
                    }
                }
                return first;
            }
        });
    }

    private void initFrenchSysB3Pinpad() {
        frenchSysB3Pkb = findViewById(R.id.b3_pinpad);
        frenchSysB3Pkb.setVisibility(View.VISIBLE);
        frenchSysB3Pkb.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            private boolean first;

            @Override
            public boolean onPreDraw() {
                if (!first) {
                    first = true;
                    if (!initFrenchSysB3KeyLayout() || !setFrenchSysB3Map()) {
                        finish();
                    } else {
                        startFrenchSysPINEntry();
                    }
                }
                return first;
            }
        });
    }

    private void initSIBSPinPad() {
        sibsPkb = findViewById(R.id.sibs_pinpad);
        sibsPkb.setVisibility(View.VISIBLE);
        sibsPkb.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            private boolean first;

            @Override
            public boolean onPreDraw() {
                if (!first) {
                    first = true;
                    if (!initSibsKeyLayout() || !setSIBSMap()) {
                        finish();
                    } else {
                        startSIBSPINEntry();
                    }
                }
                return first;
            }
        });
    }

    private boolean initNormalPINPadLayout() {
        try {
            KeyboardParameters parameters = new KeyboardParameters();
            parameters.setRandomPinpad(isRandomLayout);
            byte[] outSeq = pinEntry2.initKeyLayout(pkb.getPINPadButtonsWithPINEntry2(), screenArea, pkb.getAreaCoordination(), parameters);
            if (outSeq != null && outSeq.length > 0 && isRandomLayout) {
                pkb.onPINPadInited(outSeq);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "Failed to init normal PIN pad layout" + e);
            int errCode = e instanceof NSDKException ? ((NSDKException) e).getCode() : -1;
            EventBus.getDefault().post(new MessageEvent(e.getMessage(), MessageTag.ERROR));
            normalListener.onError(errCode, e.getMessage());
            return false;
        }
    }

    private boolean initADAPINPadLayout() {
        try {
            Map<PINPadButton, int[]> buttons = new HashMap<>();

            buttons.put(PINPadButton.NUMBER_1, getButtonCoordinates(R.id.one));
            buttons.put(PINPadButton.NUMBER_2, getButtonCoordinates(R.id.two));
            buttons.put(PINPadButton.NUMBER_3, getButtonCoordinates(R.id.three));
            buttons.put(PINPadButton.NUMBER_4, getButtonCoordinates(R.id.four));
            buttons.put(PINPadButton.NUMBER_5, getButtonCoordinates(R.id.five));
            buttons.put(PINPadButton.CANCEL, getButtonCoordinates(R.id.cancel));

            buttons.put(PINPadButton.NUMBER_6, getButtonCoordinates(R.id.six));
            buttons.put(PINPadButton.NUMBER_7, getButtonCoordinates(R.id.seven));
            buttons.put(PINPadButton.NUMBER_8, getButtonCoordinates(R.id.eight));
            buttons.put(PINPadButton.NUMBER_9, getButtonCoordinates(R.id.nine));
            buttons.put(PINPadButton.NUMBER_0, getButtonCoordinates(R.id.zero));
            buttons.put(PINPadButton.ENTER, getButtonCoordinates(R.id.enter));

            buttons.put(PINPadButton.BACKSPACE, getButtonCoordinates(R.id.backKey));
            buttons.put(PINPadButton.SWITCH, getButtonCoordinates(R.id.toggle_to_normal));

            KeyboardParameters parameters = new KeyboardParameters();
            parameters.setSwipeDistance(30);
            parameters.setClickMode(ClickMode.RELEASE);
            parameters.setEffectMode(EffectMode.PRESS);
            parameters.setRandomPinpad(false);

            pinEntry2.initKeyLayout(buttons, screenArea, screenArea, parameters);

            return true;
        } catch (Exception e) {
            LogUtils.e(TAG, "Failed to init ADA PIN pad layout" + e);
            int errCode = e instanceof NSDKException ? ((NSDKException) e).getCode() : -1;
            EventBus.getDefault().post(new MessageEvent(e.getMessage(), MessageTag.ERROR));
            adaListener.onError(errCode, e.getMessage());
            return false;
        }
    }

    private boolean initRNIBKeyLayout() {
        try {
            KeyboardParameters parameters = new KeyboardParameters();
            parameters.setSwipeDistance(30);
            parameters.setClickInterval(300);
            parameters.setPressTime(200);
            parameters.setClickMode(ClickMode.RELEASE);
            parameters.setEffectMode(EffectMode.RELEASE);
            parameters.setRandomPinpad(false);
            pinEntry2.initKeyLayout(rnibPkb.getButtonsCoordinates(), screenArea, rnibPkb.getAreaCoordination(), parameters);
            return true;
        } catch (Exception e) {
            LogUtils.e(TAG, "Failed to init RNIB key layout" + e);
            int errCode = e instanceof NSDKException ? ((NSDKException) e).getCode() : -1;
            EventBus.getDefault().post(new MessageEvent(e.getMessage(), MessageTag.ERROR));
            rnibListener.onError(errCode, e.getMessage());
            return false;
        }
    }

    private boolean initFrenchSysB3KeyLayout() {
        try {
            KeyboardParameters parameters = new KeyboardParameters();
            parameters.setSwipeDistance(30);
            parameters.setClickInterval(300);
            parameters.setPressTime(200);
            parameters.setClickMode(ClickMode.PRESS);
            parameters.setEffectMode(EffectMode.RELEASE);
            parameters.setRandomPinpad(false);
            pinEntry2.initKeyLayout(frenchSysB3Pkb.getPINPadButtons(), screenArea, frenchSysB3Pkb.getAreaCoordination(), parameters);
            return true;
        } catch (Exception e) {
            LogUtils.e(TAG, "Failed to init FrenchSys B3 key layout" + e);
            int errCode = e instanceof NSDKException ? ((NSDKException) e).getCode() : -1;
            EventBus.getDefault().post(new MessageEvent(e.getMessage(), MessageTag.ERROR));
            frenchSysB3Listener.onError(errCode, e.getMessage());
            return false;
        }
    }

    private boolean initSibsKeyLayout() {
        try {
            KeyboardParameters parameters = new KeyboardParameters();
            parameters.setSwipeDistance(30);
            parameters.setClickInterval(300);
            parameters.setPressTime(200);
            parameters.setClickMode(ClickMode.RELEASE);
            parameters.setEffectMode(EffectMode.RELEASE);
            parameters.setRandomPinpad(false);
            pinEntry2.initKeyLayout(sibsPkb.getPINPadButtons(), screenArea, sibsPkb.getAreaCoordination(), parameters);
            return true;
        } catch (Exception e) {
            LogUtils.e(TAG, "Failed to init SIBS key layout" + e);
            int errCode = e instanceof NSDKException ? ((NSDKException) e).getCode() : -1;
            EventBus.getDefault().post(new MessageEvent(e.getMessage(), MessageTag.ERROR));
            sibsListener.onError(errCode, e.getMessage());
            return false;
        }
    }
    private void startNormalPINEntry() {
        SymmetricKey desKey = new SymmetricKey();

        final PINEntry2Parameters params = new PINEntry2Parameters();
        params.setPINBlockMode(PINBlockMode.ISO9564_0);
        if (keyID != (byte) 0) {
            if (keyType == 1) {
                desKey.setKeyType(KeyType.AES);
                desKey.setKeyUsage(KeyUsage.PIN);
                params.setPINBlockMode(PINBlockMode.ISO9564_4);
            } else if (keyType == 2) {
                desKey.setKeyType(KeyType.DES);
                desKey.setKeyUsage(KeyUsage.DUKPT);
            } else if (keyType == 3) {
                desKey = new DUKPTDerivateKey();
                desKey.setKeyType(KeyType.AES);
                desKey.setKeyUsage(KeyUsage.DUKPT);
                ((DUKPTDerivateKey) desKey).setDerivateKeyLen(16);
                ((DUKPTDerivateKey) desKey).setDerivateKeyType(KeyType.AES);
                ((DUKPTDerivateKey) desKey).setDerivateUsage(DUKPTDerivateUsage.PIN);
                params.setPINBlockMode(PINBlockMode.ISO9564_4);
            } else {
                desKey.setKeyType(KeyType.DES);
                desKey.setKeyUsage(KeyUsage.PIN);
            }
            desKey.setKeyID(keyID);
        } else {
            if (keyType == 1) {
                desKey.setKeyID(AppConfig.Keys.MKSK_AES_INDEX_WK_PIN);
                desKey.setKeyType(KeyType.AES);
                desKey.setKeyUsage(KeyUsage.PIN);
                params.setPINBlockMode(PINBlockMode.ISO9564_4);
            } else if (keyType == 2) {
                desKey.setKeyID(AppConfig.Keys.DUKPT_DES_INDEX);
                desKey.setKeyType(KeyType.DES);
                desKey.setKeyUsage(KeyUsage.DUKPT);
            } else if (keyType == 3) {
                desKey = new DUKPTDerivateKey();
                desKey.setKeyID(AppConfig.Keys.DUKPT_AES_INDEX);
                desKey.setKeyType(KeyType.AES);
                desKey.setKeyUsage(KeyUsage.DUKPT);

                ((DUKPTDerivateKey) desKey).setDerivateKeyLen(16);
                ((DUKPTDerivateKey) desKey).setDerivateKeyType(KeyType.AES);
                ((DUKPTDerivateKey) desKey).setDerivateUsage(DUKPTDerivateUsage.PIN);

                params.setPINBlockMode(PINBlockMode.ISO9564_4);
            } else {
                desKey.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_WK_PIN);
                desKey.setKeyType(KeyType.DES);
                desKey.setKeyUsage(KeyUsage.PIN);
            }
        }


        params.setMinPINLen(6);
        params.setMaxPINLen(8);
        params.setAutoComplete(isAutoComplete);
        params.setPINLengthRange(new byte[]{0x06, 0x08, 0x09});
        params.setCheckIcPresent(isCheckIcPresent);
        //Current device only support set "CANCEL" key with quit function and "BACKSPACE" key with clear function.
        if (isEnableCustomFunctionKey) {
            Map<PINPadButton, PINPadButton> customButtons = new HashMap<>();
            customButtons.put(PINPadButton.CANCEL, PINPadButton.QUIT);
            customButtons.put(PINPadButton.BACKSPACE, PINPadButton.CLEAR);
            params.setCustomButtons(customButtons);
        }
        try {
            pinEntry2.startOnlinePINEntry(desKey, PAN, 30, params, normalListener);
        } catch (NSDKException e) {
            LogUtils.e(TAG, "Failed to start normal PIN entry" + e);
            normalListener.onError(e.getCode(), e.getMessage());
            finish();
        }
    }


    private void startADAPINEntry() {
        SymmetricKey desKey = new SymmetricKey();

        final PINEntry2Parameters params = new PINEntry2Parameters();
        params.setPINBlockMode(PINBlockMode.ISO9564_0);
        if (keyID != (byte) 0) {
            if (keyType == 1) {
                desKey.setKeyType(KeyType.AES);
                desKey.setKeyUsage(KeyUsage.PIN);
                params.setPINBlockMode(PINBlockMode.ISO9564_4);
            } else if (keyType == 2) {
                desKey.setKeyType(KeyType.DES);
                desKey.setKeyUsage(KeyUsage.DUKPT);
            } else if (keyType == 3) {
                desKey = new DUKPTDerivateKey();
                desKey.setKeyType(KeyType.AES);
                desKey.setKeyUsage(KeyUsage.DUKPT);
                ((DUKPTDerivateKey) desKey).setDerivateKeyLen(16);
                ((DUKPTDerivateKey) desKey).setDerivateKeyType(KeyType.AES);
                ((DUKPTDerivateKey) desKey).setDerivateUsage(DUKPTDerivateUsage.PIN);
                params.setPINBlockMode(PINBlockMode.ISO9564_4);
            } else {
                desKey.setKeyType(KeyType.DES);
                desKey.setKeyUsage(KeyUsage.PIN);
            }
            desKey.setKeyID(keyID);
        } else {
            if (keyType == 1) {
                desKey.setKeyID(AppConfig.Keys.MKSK_AES_INDEX_WK_PIN);
                desKey.setKeyType(KeyType.AES);
                desKey.setKeyUsage(KeyUsage.PIN);
                params.setPINBlockMode(PINBlockMode.ISO9564_4);
            } else if (keyType == 2) {
                desKey.setKeyID(AppConfig.Keys.DUKPT_DES_INDEX);
                desKey.setKeyType(KeyType.DES);
                desKey.setKeyUsage(KeyUsage.DUKPT);
            } else if (keyType == 3) {
                desKey = new DUKPTDerivateKey();
                desKey.setKeyID(AppConfig.Keys.DUKPT_AES_INDEX);
                desKey.setKeyType(KeyType.AES);
                desKey.setKeyUsage(KeyUsage.DUKPT);

                ((DUKPTDerivateKey) desKey).setDerivateKeyLen(16);
                ((DUKPTDerivateKey) desKey).setDerivateKeyType(KeyType.AES);
                ((DUKPTDerivateKey) desKey).setDerivateUsage(DUKPTDerivateUsage.PIN);

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
        params.setAutoComplete(isAutoComplete);
        try {
            pinEntry2.startOnlinePINEntry(desKey, PAN, 80, params, adaListener);
        } catch (NSDKException e) {
            LogUtils.e(TAG, "Failed to start normal PIN entry" + e);
            adaListener.onError(e.getCode(), e.getMessage());
            finish();
        }
    }

    private void startRNIBPINEntry() {
        SymmetricKey desKey = new SymmetricKey();

        final PINEntry2Parameters params = new PINEntry2Parameters();
        params.setPINBlockMode(PINBlockMode.ISO9564_0);
        if (keyID != (byte) 0) {
            if (keyType == 1) {
                desKey.setKeyType(KeyType.AES);
                desKey.setKeyUsage(KeyUsage.PIN);
                params.setPINBlockMode(PINBlockMode.ISO9564_4);
            } else if (keyType == 2) {
                desKey.setKeyType(KeyType.DES);
                desKey.setKeyUsage(KeyUsage.DUKPT);
            } else if (keyType == 3) {
                desKey = new DUKPTDerivateKey();
                desKey.setKeyType(KeyType.AES);
                desKey.setKeyUsage(KeyUsage.DUKPT);
                ((DUKPTDerivateKey) desKey).setDerivateKeyLen(16);
                ((DUKPTDerivateKey) desKey).setDerivateKeyType(KeyType.AES);
                ((DUKPTDerivateKey) desKey).setDerivateUsage(DUKPTDerivateUsage.PIN);
                params.setPINBlockMode(PINBlockMode.ISO9564_4);
            } else {
                desKey.setKeyType(KeyType.DES);
                desKey.setKeyUsage(KeyUsage.PIN);
            }
            desKey.setKeyID(keyID);
        } else {
            if (keyType == 1) {
                desKey.setKeyID(AppConfig.Keys.MKSK_AES_INDEX_WK_PIN);
                desKey.setKeyType(KeyType.AES);
                desKey.setKeyUsage(KeyUsage.PIN);
                params.setPINBlockMode(PINBlockMode.ISO9564_4);
            } else if (keyType == 2) {
                desKey.setKeyID(AppConfig.Keys.DUKPT_DES_INDEX);
                desKey.setKeyType(KeyType.DES);
                desKey.setKeyUsage(KeyUsage.DUKPT);
            } else if (keyType == 3) {
                desKey = new DUKPTDerivateKey();
                desKey.setKeyID(AppConfig.Keys.DUKPT_AES_INDEX);
                desKey.setKeyType(KeyType.AES);
                desKey.setKeyUsage(KeyUsage.DUKPT);

                ((DUKPTDerivateKey) desKey).setDerivateKeyLen(16);
                ((DUKPTDerivateKey) desKey).setDerivateKeyType(KeyType.AES);
                ((DUKPTDerivateKey) desKey).setDerivateUsage(DUKPTDerivateUsage.PIN);

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
        params.setAutoComplete(isAutoComplete);
        try {
            pinEntry2.startOnlinePINEntry(desKey, PAN, 80, params, rnibListener);
        } catch (NSDKException e) {
            LogUtils.e(TAG, "Failed to start normal PIN entry" + e);
            rnibListener.onError(e.getCode(), e.getMessage());
            finish();
        }
    }

    private void startFrenchSysPINEntry() {
        SymmetricKey desKey = new SymmetricKey();

        final PINEntry2Parameters params = new PINEntry2Parameters();
        params.setPINBlockMode(PINBlockMode.ISO9564_0);
        if (keyID != (byte) 0) {
            if (keyType == 1) {
                desKey.setKeyType(KeyType.AES);
                desKey.setKeyUsage(KeyUsage.PIN);
                params.setPINBlockMode(PINBlockMode.ISO9564_4);
            } else if (keyType == 2) {
                desKey.setKeyType(KeyType.DES);
                desKey.setKeyUsage(KeyUsage.DUKPT);
            } else if (keyType == 3) {
                desKey = new DUKPTDerivateKey();
                desKey.setKeyType(KeyType.AES);
                desKey.setKeyUsage(KeyUsage.DUKPT);
                ((DUKPTDerivateKey) desKey).setDerivateKeyLen(16);
                ((DUKPTDerivateKey) desKey).setDerivateKeyType(KeyType.AES);
                ((DUKPTDerivateKey) desKey).setDerivateUsage(DUKPTDerivateUsage.PIN);
                params.setPINBlockMode(PINBlockMode.ISO9564_4);
            } else {
                desKey.setKeyType(KeyType.DES);
                desKey.setKeyUsage(KeyUsage.PIN);
            }
            desKey.setKeyID(keyID);
        } else {
            if (keyType == 1) {
                desKey.setKeyID(AppConfig.Keys.MKSK_AES_INDEX_WK_PIN);
                desKey.setKeyType(KeyType.AES);
                desKey.setKeyUsage(KeyUsage.PIN);
                params.setPINBlockMode(PINBlockMode.ISO9564_4);
            } else if (keyType == 2) {
                desKey.setKeyID(AppConfig.Keys.DUKPT_DES_INDEX);
                desKey.setKeyType(KeyType.DES);
                desKey.setKeyUsage(KeyUsage.DUKPT);
            } else if (keyType == 3) {
                desKey = new DUKPTDerivateKey();
                desKey.setKeyID(AppConfig.Keys.DUKPT_AES_INDEX);
                desKey.setKeyType(KeyType.AES);
                desKey.setKeyUsage(KeyUsage.DUKPT);

                ((DUKPTDerivateKey) desKey).setDerivateKeyLen(16);
                ((DUKPTDerivateKey) desKey).setDerivateKeyType(KeyType.AES);
                ((DUKPTDerivateKey) desKey).setDerivateUsage(DUKPTDerivateUsage.PIN);

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
        params.setAutoComplete(isAutoComplete);
        try {
            pinEntry2.startOnlinePINEntry(desKey, PAN, 60, params, frenchSysB3Listener);
        } catch (NSDKException e) {
            LogUtils.e(TAG, "Failed to start normal PIN entry" + e);
            frenchSysB3Listener.onError(e.getCode(), e.getMessage());
            finish();
        }
    }


    private void startSIBSPINEntry() {
        SymmetricKey desKey = new SymmetricKey();

        final PINEntry2Parameters params = new PINEntry2Parameters();
        params.setPINBlockMode(PINBlockMode.ISO9564_0);
        if (keyID != (byte) 0) {
            if (keyType == 1) {
                desKey.setKeyType(KeyType.AES);
                desKey.setKeyUsage(KeyUsage.PIN);
                params.setPINBlockMode(PINBlockMode.ISO9564_4);
            } else if (keyType == 2) {
                desKey.setKeyType(KeyType.DES);
                desKey.setKeyUsage(KeyUsage.DUKPT);
            } else if (keyType == 3) {
                desKey = new DUKPTDerivateKey();
                desKey.setKeyType(KeyType.AES);
                desKey.setKeyUsage(KeyUsage.DUKPT);
                ((DUKPTDerivateKey) desKey).setDerivateKeyLen(16);
                ((DUKPTDerivateKey) desKey).setDerivateKeyType(KeyType.AES);
                ((DUKPTDerivateKey) desKey).setDerivateUsage(DUKPTDerivateUsage.PIN);
                params.setPINBlockMode(PINBlockMode.ISO9564_4);
            } else {
                desKey.setKeyType(KeyType.DES);
                desKey.setKeyUsage(KeyUsage.PIN);
            }
            desKey.setKeyID(keyID);
        } else {
            if (keyType == 1) {
                desKey.setKeyID(AppConfig.Keys.MKSK_AES_INDEX_WK_PIN);
                desKey.setKeyType(KeyType.AES);
                desKey.setKeyUsage(KeyUsage.PIN);
                params.setPINBlockMode(PINBlockMode.ISO9564_4);
            } else if (keyType == 2) {
                desKey.setKeyID(AppConfig.Keys.DUKPT_DES_INDEX);
                desKey.setKeyType(KeyType.DES);
                desKey.setKeyUsage(KeyUsage.DUKPT);
            } else if (keyType == 3) {
                desKey = new DUKPTDerivateKey();
                desKey.setKeyID(AppConfig.Keys.DUKPT_AES_INDEX);
                desKey.setKeyType(KeyType.AES);
                desKey.setKeyUsage(KeyUsage.DUKPT);

                ((DUKPTDerivateKey) desKey).setDerivateKeyLen(16);
                ((DUKPTDerivateKey) desKey).setDerivateKeyType(KeyType.AES);
                ((DUKPTDerivateKey) desKey).setDerivateUsage(DUKPTDerivateUsage.PIN);

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
        params.setAutoComplete(isAutoComplete);
        try {
            pinEntry2.startOnlinePINEntry(desKey, PAN, 60, params, sibsListener);
        } catch (NSDKException e) {
            LogUtils.e(TAG, "Failed to start normal PIN entry" + e);
            sibsListener.onError(e.getCode(), e.getMessage());
            finish();
        }
    }

    private boolean setNormalMap() {
        Map<ExtendedEvent, PINCustomizedAction> eventActionMap = new HashMap<>();
        eventActionMap.put(ExtendedEvent.CLICK, PINCustomizedAction.INPUT);
        if (isCheckPINRange) {
            eventActionMap.put(ExtendedEvent.TOO_LONG, PINCustomizedAction.ESC);
            eventActionMap.put(ExtendedEvent.TOO_SHORT, PINCustomizedAction.ESC);
        }
        try {
            pinEntry2.setCustomizedActions(eventActionMap);
            return true;
        } catch (NSDKException e) {
            normalListener.onError(e.getCode(), e.getMessage());
            finish();
        }
        return false;
    }

    private boolean setADAMap() {
        Map<ExtendedEvent, PINCustomizedAction> eventActionMap = new HashMap<>();
        eventActionMap.put(ExtendedEvent.CLICK, PINCustomizedAction.INPUT);
        if (isCheckPINRange) {
            eventActionMap.put(ExtendedEvent.TOO_LONG, PINCustomizedAction.ESC);
            eventActionMap.put(ExtendedEvent.TOO_SHORT, PINCustomizedAction.ESC);
        }
        eventActionMap.put(ExtendedEvent.SWIPE_LEFT, PINCustomizedAction.SELECT);
        eventActionMap.put(ExtendedEvent.SWIPE_RIGHT, PINCustomizedAction.SELECT);
        eventActionMap.put(ExtendedEvent.SWIPE_UP, PINCustomizedAction.SELECT);
        eventActionMap.put(ExtendedEvent.SWIPE_DOWN, PINCustomizedAction.SELECT);
        try {
            pinEntry2.setCustomizedActions(eventActionMap);
            return true;
        } catch (NSDKException e) {
            adaListener.onError(e.getCode(), e.getMessage());
            finish();
        }
        return false;
    }

    private boolean setRNIBMap() {
        Map<ExtendedEvent, PINCustomizedAction> eventActionMap = new HashMap<>();
        eventActionMap.put(ExtendedEvent.DOUBLE_CLICK, PINCustomizedAction.INPUT);
        if (isCheckPINRange) {
            eventActionMap.put(ExtendedEvent.TOO_LONG, PINCustomizedAction.ESC);
            eventActionMap.put(ExtendedEvent.TOO_SHORT, PINCustomizedAction.ESC);
        }
        eventActionMap.put(ExtendedEvent.SWIPE_LEFT, PINCustomizedAction.SELECT);
        eventActionMap.put(ExtendedEvent.SWIPE_RIGHT, PINCustomizedAction.SELECT);
        eventActionMap.put(ExtendedEvent.SWIPE_UP, PINCustomizedAction.SELECT);
        eventActionMap.put(ExtendedEvent.SWIPE_DOWN, PINCustomizedAction.SELECT);
        try {
            pinEntry2.setCustomizedActions(eventActionMap);
            return true;
        } catch (NSDKException e) {
            rnibListener.onError(e.getCode(), e.getMessage());
            finish();
        }
        return false;
    }

    private boolean setFrenchSysB3Map() {
        Map<ExtendedEvent, PINCustomizedAction> eventActionMap = new HashMap<>();
        eventActionMap.put(ExtendedEvent.DOUBLE_CLICK, PINCustomizedAction.INPUT);
        if (isCheckPINRange) {
            eventActionMap.put(ExtendedEvent.TOO_LONG, PINCustomizedAction.ESC);
            eventActionMap.put(ExtendedEvent.TOO_SHORT, PINCustomizedAction.ESC);
        }
        eventActionMap.put(ExtendedEvent.CLICK, PINCustomizedAction.SELECT);
        eventActionMap.put(ExtendedEvent.SWIPE_LEFT, PINCustomizedAction.SELECT);
        eventActionMap.put(ExtendedEvent.SWIPE_RIGHT, PINCustomizedAction.SELECT);
        eventActionMap.put(ExtendedEvent.SWIPE_UP, PINCustomizedAction.SELECT);
        eventActionMap.put(ExtendedEvent.SWIPE_DOWN, PINCustomizedAction.SELECT);
        try {
            pinEntry2.setCustomizedActions(eventActionMap);
            return true;
        } catch (NSDKException e) {
            frenchSysB3Listener.onError(e.getCode(), e.getMessage());
            finish();
        }
        return false;
    }

    private boolean setSIBSMap() {
        Map<ExtendedEvent, PINCustomizedAction> eventActionMap = new HashMap<>();
        eventActionMap.put(ExtendedEvent.DOUBLE_CLICK, PINCustomizedAction.INPUT);
        eventActionMap.put(ExtendedEvent.TRIPLE_CLICK, PINCustomizedAction.ESC);
        if (isCheckPINRange) {
            eventActionMap.put(ExtendedEvent.TOO_LONG, PINCustomizedAction.ESC);
            eventActionMap.put(ExtendedEvent.TOO_SHORT, PINCustomizedAction.ESC);
        }
        eventActionMap.put(ExtendedEvent.SWIPE_LEFT, PINCustomizedAction.SELECT);
        eventActionMap.put(ExtendedEvent.SWIPE_RIGHT, PINCustomizedAction.SELECT);
        eventActionMap.put(ExtendedEvent.SWIPE_UP, PINCustomizedAction.SELECT);
        eventActionMap.put(ExtendedEvent.SWIPE_DOWN, PINCustomizedAction.SELECT);
        try {
            pinEntry2.setCustomizedActions(eventActionMap);
            return true;
        } catch (NSDKException e) {
            sibsListener.onError(e.getCode(), e.getMessage());
            finish();
        }
        return false;
    }

    private final PINEntry2Listener normalListener = new PINEntry2Listener() {
        @Override
        public void onFinish(int pinLen, byte[] pinBlock, byte[] ksn) {
            LogUtils.d(TAG, "onFinish: pinLen=" + pinLen);
            LogUtils.d(TAG, "onFinish: pinblock=" + (pinBlock != null ? ISOUtils.hexString(pinBlock) : "null"));
            LogUtils.d(TAG, "onFinish: ksn=" + (ksn != null ? ISOUtils.hexString(ksn) : "null"));

            String pinBlockStr = "PIN Block=";
            String ksnStr = null;
            String displayMessage = null;

            if (pinBlock != null) {
                pinBlockStr += (pinBlock.length == 0 ? "null" : ISOUtils.hexString(pinBlock));
                ksnStr = "KSN=" + (ksn == null ? "null" : ISOUtils.hexString(ksn));

                if (keyType == KEY_TYPE_DUKPT || keyType == KEY_TYPE_AES_DUKPT) {
                    displayMessage = String.format("%s,%s(Please increase KSN before next DUKPT PIN entry)", pinBlockStr, ksnStr);
                }
            }

            displayMessage = pinBlockStr;
            finish();
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

        @SuppressLint("DefaultLocale")
        @Override
        public void onError(int errorCode, String message) {
            LogUtils.d(TAG, "onError: " + errorCode + ", " + message);
            finish();
            EventBus.getDefault().post(new MessageEvent(String.format("onError: %d, %s", errorCode, message), MessageTag.ERROR));
        }

        @Override
        public void onExtendedEvent(ExtendedEventInfo extendedEventInfo) {
            if (extendedEventInfo != null) {
                ExtendedEvent event = extendedEventInfo.getExtendedEvent();
                TouchState state = extendedEventInfo.getTouchState();
                switch (event) {
                    case TOO_LONG:
                        EventBus.getDefault().post(new MessageEvent("Too long", MessageTag.NORMAL));
                        break;
                    case TOO_SHORT:
                        EventBus.getDefault().post(new MessageEvent("Too short", MessageTag.NORMAL));
                        break;
                    default:
                        EventBus.getDefault().post(new MessageEvent("ExtendedEvent", MessageTag.NORMAL));
                }

            }
        }
    };

    private final PINEntry2Listener adaListener = new PINEntry2Listener() {
        boolean switchToNormal = false;

        @Override
        public void onFinish(int pinLen, byte[] pinBlock, byte[] ksn) {
            LogUtils.d(TAG, "onFinish: pinLen=" + pinLen);
            LogUtils.d(TAG, "onFinish: pinblock=" + (pinBlock != null ? ISOUtils.hexString(pinBlock) : "null"));
            LogUtils.d(TAG, "onFinish: ksn=" + (ksn != null ? ISOUtils.hexString(ksn) : "null"));

            String pinBlockStr = "PIN Block=";
            String ksnStr = null;
            String displayMessage = null;

            if (pinBlock != null) {
                pinBlockStr += (pinBlock.length == 0 ? "null" : ISOUtils.hexString(pinBlock));
                ksnStr = "KSN=" + (ksn == null ? "null" : ISOUtils.hexString(ksn));

                if (keyType == KEY_TYPE_DUKPT || keyType == KEY_TYPE_AES_DUKPT) {
                    displayMessage = String.format("%s,%s(Please increase KSN before next DUKPT PIN entry)", pinBlockStr, ksnStr);
                }
            }

            displayMessage = pinBlockStr;
            finish();
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
            if (!switchToNormal) {
                finish();
            } else {
                switchToNormal = false;
            }
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

        @SuppressLint("DefaultLocale")
        @Override
        public void onError(int errorCode, String message) {
            LogUtils.d(TAG, "onError: " + errorCode + ", " + message);
            finish();
            EventBus.getDefault().post(new MessageEvent(String.format("onError: %d, %s", errorCode, message), MessageTag.ERROR));
        }

        @Override
        public void onExtendedEvent(ExtendedEventInfo extendedEventInfo) {
            if (extendedEventInfo != null) {
                ExtendedEvent event = extendedEventInfo.getExtendedEvent();
                TouchState state = extendedEventInfo.getTouchState();
                switch (event) {
                    case TOO_LONG:
                        EventBus.getDefault().post(new MessageEvent("Too long", MessageTag.NORMAL));
                        break;
                    case TOO_SHORT:
                        EventBus.getDefault().post(new MessageEvent("Too short", MessageTag.NORMAL));
                        break;
                    case CLICK:
                        if (state != null) {
                            if (state == TouchState.TOUCH_ON_SWITCH_KEY && keyboardStyle == 1) {
                                try {
                                    pinEntry2.cancelPINEntry();
                                    switchToNormal = true;
                                } catch (NSDKException e) {
                                    e.printStackTrace();
                                }
                                switchToNormalKeyboard();
                            }
                        }
                    default:
                        EventBus.getDefault().post(new MessageEvent("ExtendedEvent", MessageTag.NORMAL));
                }

            }
        }
    };

    private final PINEntry2Listener rnibListener = new PINEntry2Listener() {
        @Override
        public void onFinish(int pinLen, byte[] pinBlock, byte[] ksn) {
            LogUtils.d(TAG, "onFinish: pinLen=" + pinLen);
            LogUtils.d(TAG, "onFinish: pinblock=" + (pinBlock != null ? ISOUtils.hexString(pinBlock) : "null"));
            LogUtils.d(TAG, "onFinish: ksn=" + (ksn != null ? ISOUtils.hexString(ksn) : "null"));

            String pinBlockStr = "PIN Block=";
            String ksnStr = null;
            String displayMessage = null;

            if (pinBlock != null) {
                pinBlockStr += (pinBlock.length == 0 ? "null" : ISOUtils.hexString(pinBlock));
                ksnStr = "KSN=" + (ksn == null ? "null" : ISOUtils.hexString(ksn));

                if (keyType == KEY_TYPE_DUKPT || keyType == KEY_TYPE_AES_DUKPT) {
                    displayMessage = String.format("%s,%s(Please increase KSN before next DUKPT PIN entry)", pinBlockStr, ksnStr);
                }
            }

            displayMessage = pinBlockStr;
            finish();
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

        @SuppressLint("DefaultLocale")
        @Override
        public void onError(int errorCode, String message) {
            LogUtils.d(TAG, "onError: " + errorCode + ", " + message);
            finish();
            EventBus.getDefault().post(new MessageEvent(String.format("onError: %d, %s", errorCode, message), MessageTag.ERROR));
        }

        @Override
        public void onExtendedEvent(ExtendedEventInfo extendedEventInfo) {
            if (extendedEventInfo != null) {
                ExtendedEvent event = extendedEventInfo.getExtendedEvent();
                TouchState state = extendedEventInfo.getTouchState();
                switch (event) {
                    case TOO_LONG:
                        EventBus.getDefault().post(new MessageEvent("Too long", MessageTag.NORMAL));
                        break;
                    case TOO_SHORT:
                        EventBus.getDefault().post(new MessageEvent("Too short", MessageTag.NORMAL));
                        break;
                    case SWIPE_LEFT:
                    case SWIPE_RIGHT:
                    case SWIPE_DOWN:
                    case SWIPE_UP:
                        if (state != null) {
                            if (state == TouchState.TOUCH_ON_DIGIT_KEY) {
                                beep();
                            }
                            if (state == TouchState.TOUCH_ON_CANCEL_KEY) {
                                playAudio(R.raw.transaction_cancelled);
                            }
                            if (state == TouchState.TOUCH_ON_ENTER_KEY) {
                                playAudio(R.raw.all_digits_entered);
                            }
                            if (state == TouchState.TOUCH_ON_BACKSPACE_KEY) {
                                playAudio(R.raw.last_digit_cleared);
                            }
                            EventBus.getDefault().post(new MessageEvent("Swipe, TouchState: " + state, MessageTag.NORMAL));
                        }
                        break;
                    default:
                        EventBus.getDefault().post(new MessageEvent("ExtendedEvent: " + extendedEventInfo.getExtendedEvent() + ", TouchState: " + state, MessageTag.NORMAL));
                        break;
                }

            }
        }
    };

    private final PINEntry2Listener frenchSysB3Listener = new PINEntry2Listener() {
        @Override
        public void onFinish(int pinLen, byte[] pinBlock, byte[] ksn) {
            LogUtils.d(TAG, "onFinish: pinLen=" + pinLen);
            LogUtils.d(TAG, "onFinish: pinblock=" + (pinBlock != null ? ISOUtils.hexString(pinBlock) : "null"));
            LogUtils.d(TAG, "onFinish: ksn=" + (ksn != null ? ISOUtils.hexString(ksn) : "null"));

            String pinBlockStr = "PIN Block=";
            String ksnStr = null;
            String displayMessage = null;

            if (pinBlock != null) {
                pinBlockStr += (pinBlock.length == 0 ? "null" : ISOUtils.hexString(pinBlock));
                ksnStr = "KSN=" + (ksn == null ? "null" : ISOUtils.hexString(ksn));

                if (keyType == KEY_TYPE_DUKPT || keyType == KEY_TYPE_AES_DUKPT) {
                    displayMessage = String.format("%s,%s(Please increase KSN before next DUKPT PIN entry)", pinBlockStr, ksnStr);
                }
            }

            displayMessage = pinBlockStr;
            finish();
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

        @SuppressLint("DefaultLocale")
        @Override
        public void onError(int errorCode, String message) {
            LogUtils.d(TAG, "onError: " + errorCode + ", " + message);
            finish();
            EventBus.getDefault().post(new MessageEvent(String.format("onError: %d, %s", errorCode, message), MessageTag.ERROR));
        }

        @Override
        public void onExtendedEvent(ExtendedEventInfo extendedEventInfo) {
            if (extendedEventInfo != null) {
                ExtendedEvent event = extendedEventInfo.getExtendedEvent();
                TouchState state = extendedEventInfo.getTouchState();
                switch (event) {
                    case TOO_LONG:
                        EventBus.getDefault().post(new MessageEvent("Too long", MessageTag.NORMAL));
                        break;
                    case TOO_SHORT:
                        EventBus.getDefault().post(new MessageEvent("Too short", MessageTag.NORMAL));
                        break;
                    case SWIPE_LEFT:
                    case SWIPE_RIGHT:
                    case SWIPE_DOWN:
                    case SWIPE_UP:
                        if (state != null) {
                            if (state == TouchState.TOUCH_ON_DIGIT_KEY) {
                                beep();
                            }
                            if (state == TouchState.TOUCH_ON_CANCEL_KEY) {
                                playAudio(R.raw.transaction_cancelled);
                            }
                            if (state == TouchState.TOUCH_ON_ENTER_KEY) {
                                playAudio(R.raw.all_digits_entered);
                            }
                            if (state == TouchState.TOUCH_ON_BACKSPACE_KEY) {
                                playAudio(R.raw.last_digit_cleared);
                            }
                            EventBus.getDefault().post(new MessageEvent("Swipe, TouchState: " + state, MessageTag.NORMAL));
                        }
                        break;
                    default:
                        EventBus.getDefault().post(new MessageEvent("ExtendedEvent: " + extendedEventInfo.getExtendedEvent() + ", TouchState: " + extendedEventInfo.getTouchState(), MessageTag.NORMAL));
                }

            }
        }
    };

    private final PINEntry2Listener sibsListener = new PINEntry2Listener() {
        @Override
        public void onFinish(int pinLen, byte[] pinBlock, byte[] ksn) {
            LogUtils.d(TAG, "onFinish: pinLen=" + pinLen);
            LogUtils.d(TAG, "onFinish: pinblock=" + (pinBlock != null ? ISOUtils.hexString(pinBlock) : "null"));
            LogUtils.d(TAG, "onFinish: ksn=" + (ksn != null ? ISOUtils.hexString(ksn) : "null"));

            String pinBlockStr = "PIN Block=";
            String ksnStr = null;
            String displayMessage = null;

            if (pinBlock != null) {
                pinBlockStr += (pinBlock.length == 0 ? "null" : ISOUtils.hexString(pinBlock));
                ksnStr = "KSN=" + (ksn == null ? "null" : ISOUtils.hexString(ksn));

                if (keyType == KEY_TYPE_DUKPT || keyType == KEY_TYPE_AES_DUKPT) {
                    displayMessage = String.format("%s,%s(Please increase KSN before next DUKPT PIN entry)", pinBlockStr, ksnStr);
                }
            }

            displayMessage = pinBlockStr;
            finish();
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
            EventBus.getDefault().post(new MessageEvent("onClear", MessageTag.NORMAL));
        }

        @Override
        public void onBackspace() {
            LogUtils.d(TAG, "onBackspace: ");
            mCurrKeyLen = mCurrKeyLen - 1;
            if (mCurrKeyLen < 0) {
                mCurrKeyLen = 0;
            }
            EventBus.getDefault().post(new MessageEvent("onBackspace", MessageTag.NORMAL));
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onError(int errorCode, String message) {
            LogUtils.d(TAG, "onError: " + errorCode + ", " + message);
            finish();
            EventBus.getDefault().post(new MessageEvent(String.format("onError: %d, %s", errorCode, message), MessageTag.ERROR));
        }

        @Override
        public void onExtendedEvent(ExtendedEventInfo extendedEventInfo) {
            if (extendedEventInfo != null) {
                ExtendedEvent event = extendedEventInfo.getExtendedEvent();
                TouchState state = extendedEventInfo.getTouchState();
                switch (event) {
                    case TOO_LONG:
                        EventBus.getDefault().post(new MessageEvent("Too long", MessageTag.NORMAL));
                        break;
                    case TOO_SHORT:
                        EventBus.getDefault().post(new MessageEvent("Too short", MessageTag.NORMAL));
                        break;
                    case SWIPE_LEFT:
                        //Cancel PINEntry process when there is no pin block while swiping left on the screen.
                        if (mCurrKeyLen == 0) {
                            //todo: Play the operation guide audio again.
                        } else {
                            if (state != null) {
                                if (state == TouchState.TOUCH_ON_DIGIT_KEY) {
                                    beep();
                                }
                                if (state == TouchState.TOUCH_ON_CANCEL_KEY) {
                                    playAudio(R.raw.transaction_cancelled);
                                }
                                if (state == TouchState.TOUCH_ON_ENTER_KEY) {
                                    playAudio(R.raw.all_digits_entered);
                                }
                                if (state == TouchState.TOUCH_ON_BACKSPACE_KEY) {
                                    playAudio(R.raw.last_digit_cleared);
                                }
                                EventBus.getDefault().post(new MessageEvent("Swipe, TouchState: " + state, MessageTag.NORMAL));
                            }
                        }
                        break;
                    case SWIPE_RIGHT:
                    case SWIPE_DOWN:
                    case SWIPE_UP:
                        if (state != null) {
                            if (state == TouchState.TOUCH_ON_DIGIT_KEY) {
                                beep();
                            }
                            if (state == TouchState.TOUCH_ON_CANCEL_KEY) {
                                playAudio(R.raw.transaction_cancelled);
                            }
                            if (state == TouchState.TOUCH_ON_ENTER_KEY) {
                                playAudio(R.raw.all_digits_entered);
                            }
                            if (state == TouchState.TOUCH_ON_BACKSPACE_KEY) {
                                playAudio(R.raw.last_digit_cleared);
                            }
                            EventBus.getDefault().post(new MessageEvent("Swipe, TouchState: " + state, MessageTag.NORMAL));
                        }
                        break;
                    case TRIPLE_CLICK:
                        try {
                            pinEntry2.cancelPINEntry();
                        } catch (NSDKException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        EventBus.getDefault().post(new MessageEvent("ExtendedEvent: " + extendedEventInfo.getExtendedEvent() + ", TouchState: " + state, MessageTag.NORMAL));
                        break;
                }

            }
        }
    };

    // Switch to normal pin entry
    private void switchToNormalKeyboard() {
        LogUtils.d(TAG, "Switching to normal keyboard");
        keyboardStyle = 0;
        mCurrKeyLen = 0;
        // delay 500ms
        new Handler(Looper.getMainLooper()).postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                init();
                            }
                        });
                    }
                },
                500
        );
    }


    private int[] getButtonCoordinates(int buttonId) {
        View button = findViewById(buttonId);
        if (button == null) {
            return new int[]{0, 0, 0, 0};
        }

        int[] location = new int[2];
        button.getLocationOnScreen(location);

        int left = location[0];
        int top = location[1];
        int right = left + button.getWidth();
        int bottom = top + button.getHeight();

        LogUtils.d(TAG, "Button coordinates: " + left + ", " + top + ", " + right + ", " + bottom);

        return new int[]{left, top, right, bottom};
    }

    private int[] getScreenArea() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return new int[]{0, 0, metrics.widthPixels, metrics.heightPixels};
    }


    private void setKeyLen(int len) {
        Message msg = mHandler.obtainMessage(1);
        msg.obj = len;
        msg.sendToTarget();
    }


    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) { // inputting
                int len = (Integer) msg.obj;
                buffer = new StringBuffer();
                for (int i = 0; i < len; i++) {
                    buffer.append(" * ");
                }
                txtPassword.setText(buffer.toString());
            }
        }
    };

    private void beep() {
        try {
            beeper.beep(1000, 200);
        } catch (NSDKException e) {
            EventBus.getDefault().post(new MessageEvent(e.getMessage(), MessageTag.ERROR));
        }
    }

    private void playAudio(int rawID) {
        MediaPlayer mediaPlayer = MediaPlayer.create(PINEntry2KeyboardActivity.this, rawID);
        mediaPlayer.start();
    }
}
