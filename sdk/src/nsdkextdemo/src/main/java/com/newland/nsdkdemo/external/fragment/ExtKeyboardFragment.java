package com.newland.nsdkdemo.external.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;

import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.crypto.AlgorithmParameters;
import com.newland.nsdk.core.api.common.crypto.CipherType;
import com.newland.nsdk.core.api.common.crypto.KCVMode;
import com.newland.nsdk.core.api.common.crypto.PaddingMode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.keymanager.CipherMode;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.api.common.keymanager.KeyUsage;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.external.display.PictureType;
import com.newland.nsdk.core.api.external.keyboard.AmountListener;
import com.newland.nsdk.core.api.external.keyboard.AmountParameters;
import com.newland.nsdk.core.api.external.keyboard.AmountType;
import com.newland.nsdk.core.api.external.keyboard.ExtKeyboard;
import com.newland.nsdk.core.api.external.keyboard.InputButtonParameters;
import com.newland.nsdk.core.api.external.keyboard.InputItem;
import com.newland.nsdk.core.api.external.keyboard.InputListener;
import com.newland.nsdk.core.api.external.keyboard.InputParameters;
import com.newland.nsdk.core.api.external.keyboard.InputType;
import com.newland.nsdk.core.api.external.keyboard.KeyboardListener;
import com.newland.nsdk.core.api.external.keyboard.KeyboardMode;
import com.newland.nsdk.core.api.external.keyboard.KeyboardParameters;
import com.newland.nsdk.core.api.external.keyboard.PromptID;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.AppConfig;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.annotation.MethodGridEntity;
import com.newland.nsdkdemo.common.utils.DialogUtils;
import com.newland.nsdkdemo.common.utils.EnumUtils;
import com.newland.nsdkdemo.common.utils.MessageTag;

import java.math.BigDecimal;
import java.util.Locale;

public class ExtKeyboardFragment extends ExtBaseFragment {

    private ExtKeyboard extKeyboard;
    private static final int INDEX_EXTKEYBOARD_STARTINPUT = 1;
    private static final int INDEX_EXTKEYBOARD_STARTINPUT_CRYPTO = 2;
    private static final int INDEX_EXTKEYBORAD_INPUT_DATA = 3;
    private static final int INDEX_EXTKEYBORAD_INPUT_AMOUNT = 4;

    @SuppressLint("ValidFragment")
    public ExtKeyboardFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_extkeyboard);
    }

    @Override
    public void initData() {
        extKeyboard = (ExtKeyboard) moduleManager.getModule(ModuleType.EXT_KEYBOARD);
    }

    @Override
    public Object getModule() {
        return ExtKeyboardFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.msg_extkeyboard_input, functionid = INDEX_EXTKEYBOARD_STARTINPUT)
    private void startInput() {
        DialogUtils.createCustomDialog(context, R.string.msg_extkeyboard_input, null, R.layout.dialog_ext_keyboard, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                Switch swDefaultKeyID = view.findViewById(R.id.switch_default_key_id_btn);
                swDefaultKeyID.setChecked(true);
                LinearLayout llKeyBoardDefaultKeyIDParams = view.findViewById(R.id.linear_keyboardDefaultKeyIDParams);
                llKeyBoardDefaultKeyIDParams.setVisibility(View.VISIBLE);
                LinearLayout llKeyBoardEditKeyIDParams = view.findViewById(R.id.linear_keyboardEditKeyIDParams);
                llKeyBoardEditKeyIDParams.setVisibility(View.GONE);
                swDefaultKeyID.setOnCheckedChangeListener((buttonVIew, isChecked) -> {
                    if (isChecked) {
                        llKeyBoardDefaultKeyIDParams.setVisibility(View.VISIBLE);
                        llKeyBoardEditKeyIDParams.setVisibility(View.GONE);
                    } else {
                        llKeyBoardDefaultKeyIDParams.setVisibility(View.GONE);
                        llKeyBoardEditKeyIDParams.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onResult(int id, View dialogView) {
                Switch swDefaultKeyID = dialogView.findViewById(R.id.switch_default_key_id_btn);
                EditText etKeyID = dialogView.findViewById(R.id.dialog_ext_keyboard_keyID);
                int keyID = Integer.parseInt(etKeyID.getText().toString());
                Spinner spnKeyLen = dialogView.findViewById(R.id.dialog_ext_keyboard_KeyLen);
                EditText etKeyData = dialogView.findViewById(R.id.dialog_ext_keyboard_keyData);
                byte[] keyData = ISOUtils.hex2byte(etKeyData.getText().toString());
                Spinner spnKeyBoardMode = dialogView.findViewById(R.id.dialog_ext_keyboard_KeyboardMode);
                EditText etKeyBoardMinLen = dialogView.findViewById(R.id.dialog_ext_keyboard_minLen);
                int minLen = Integer.parseInt(etKeyBoardMinLen.getText().toString());
                EditText etKeyBoardMaxLen = dialogView.findViewById(R.id.dialog_ext_keyboard_maxLen);
                int maxLen = Integer.parseInt(etKeyBoardMaxLen.getText().toString());
                Spinner spnKeyType = dialogView.findViewById(R.id.dialog_ext_keyboard_KeyType);
                Spinner spnKeyUsage = dialogView.findViewById(R.id.dialog_ext_keyboard_KeyUsage);
                Spinner spnKcvMode = dialogView.findViewById(R.id.dialog_ext_keyboard_kcvMode);
                EditText etKcv = dialogView.findViewById(R.id.dialog_ext_keyboard_kcv);
                byte[] kcv = ISOUtils.hex2byte(etKcv.getText().toString());
                Spinner spnKeyBoardDefaultKeyID = dialogView.findViewById(R.id.spnKeyBoardDefaultKeyID);


                byte keyboardDefaultKeyID = EnumUtils.getDefaultKeyID(spnKeyBoardDefaultKeyID.getSelectedItem().toString());
                KCVMode kcvMode = EnumUtils.getKcvMode(spnKcvMode.getSelectedItem().toString());
                KeyUsage keyUsage = EnumUtils.getKeyUsage(spnKeyUsage.getSelectedItem().toString());
                KeyType keyType = EnumUtils.getKeyType(spnKeyType.getSelectedItem().toString());
                int keyLen = EnumUtils.getKeyLen(spnKeyLen.getSelectedItem().toString());
                KeyboardMode keyboardMode = EnumUtils.getKeyboardMode(spnKeyBoardMode.getSelectedItem().toString());

                if(swDefaultKeyID.isChecked()) {
                    KeyboardParameters parameter = new KeyboardParameters();
                    SymmetricKey key = new SymmetricKey();
                    key.setKeyID(keyboardDefaultKeyID);
                    key.setKeyLen(keyLen);
                    key.setKeyData(keyData);
                    key.setKeyType(keyType);
                    key.setKeyUsage(keyUsage);
                    key.setKCVMode(kcvMode);
                    key.setKCV(kcv);
                    int keyPressTimeout = 60;
                    parameter.setMaxLen((byte) maxLen);
                    parameter.setMinLen((byte) minLen);
                    parameter.setPromptID(PromptID.PHONE_NUMBER);
                    parameter.setKeyboardMode(keyboardMode);
                    try {
                        extKeyboard.startKeyEntry(key, keyPressTimeout, parameter, new KeyboardListener() {
                            @Override
                            public void onError(int i, String msg) {
                                showMessage(String.format("[%d] %s", i, msg), MessageTag.ERROR);
                            }

                            @Override
                            public void onSuccess(int len, byte[] bytes) {
                                showMessage("Keyboard input success, data : " + ISOUtils.hexString(bytes) + "\n key code len:" + len);
                            }

                            @Override
                            public void onTimeout() {
                                showMessage("Keyboard input timeout!");
                            }

                            @Override
                            public void onCancel() {
                                showMessage("Keyboard input cancelled!");
                            }
                        });
                    } catch (NSDKException e) {
                        e.printStackTrace();
                        showErrorMessage(e, "input PIN");
                    }
                } else {
                    KeyboardParameters parameter = new KeyboardParameters();
                    SymmetricKey key = new SymmetricKey();
                    key.setKeyID((byte)keyID);
                    key.setKeyLen(keyLen);
                    key.setKeyData(keyData);
                    key.setKeyType(keyType);
                    key.setKeyUsage(keyUsage);
                    key.setKCVMode(kcvMode);
                    key.setKCV(kcv);
                    int keyPressTimeout = 60;
                    parameter.setMaxLen((byte) maxLen);
                    parameter.setMinLen((byte) minLen);
                    parameter.setPromptID(PromptID.PHONE_NUMBER);
                    parameter.setKeyboardMode(keyboardMode);
                    try {
                        extKeyboard.startKeyEntry(key, keyPressTimeout, parameter, new KeyboardListener() {
                            @Override
                            public void onError(int i, String msg) {
                                showMessage(String.format("[%d] %s", i, msg), MessageTag.ERROR);
                            }

                            @Override
                            public void onSuccess(int len, byte[] bytes) {
                                showMessage("Keyboard input success, data : " + ISOUtils.hexString(bytes) + "\n key code len:" + len);
                            }

                            @Override
                            public void onTimeout() {
                                showMessage("Keyboard input timeout!");
                            }

                            @Override
                            public void onCancel() {
                                showMessage("Keyboard input cancelled!");
                            }
                        });
                    } catch (NSDKException e) {
                        e.printStackTrace();
                        showErrorMessage(e, "input PIN");
                    }
                }

            }
        });

    }

    @MethodGridEntity(btnnameid = R.string.msg_extkeyboard_input_crypto, functionid = INDEX_EXTKEYBOARD_STARTINPUT_CRYPTO)
    private void startInputEncrypted() {
        KeyboardParameters parameter = new KeyboardParameters();
        SymmetricKey key = new SymmetricKey();
        key.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_WK_DATA);
        key.setKeyType(KeyType.DES);
        key.setKeyUsage(KeyUsage.DATA);

        AlgorithmParameters params = new AlgorithmParameters();
        params.setCipherMode(CipherMode.ECB);
        params.setPaddingMode(PaddingMode.NONE);
        int keyPressTimeout = 60;
        parameter.setMaxLen((byte) 10);
        parameter.setMinLen((byte) 6);
        parameter.setPromptID(PromptID.PHONE_NUMBER);
        parameter.setKeyboardMode(KeyboardMode.ONLY_DIGITS);
        try {
            extKeyboard.startKeyEntry(key, params, keyPressTimeout, parameter, new KeyboardListener() {
                @Override
                public void onError(int i, String msg) {
                    showMessage(String.format("[%d] %s", i, msg), MessageTag.ERROR);
                }

                @Override
                public void onSuccess(int len, byte[] bytes) {
                    showMessage("Keyboard input success, data : " + ISOUtils.hexString(bytes) + "\n key code len:" + len);
                }

                @Override
                public void onTimeout() {
                    showMessage("Keyboard input timeout!");
                }

                @Override
                public void onCancel() {
                    showMessage("Keyboard input cancelled!");
                }
            });
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "input PIN");
        }
    }

    @MethodGridEntity(btnnameid = R.string.ext_keyboard_input_data, functionid = INDEX_EXTKEYBORAD_INPUT_DATA)
    private void inputData() {
        InputItem[] inputItems = new InputItem[2];
        for (int i = 0; i < inputItems.length; i++) {
            inputItems[i] = new InputItem();
            inputItems[i].setType(InputType.PHONE_NUMBER);
            inputItems[i].setInputSettings((byte) 0x00);
            inputItems[i].setFormatCode((byte) 0x00);
            inputItems[i].setMinDigits(1);
            inputItems[i].setMaxDigits(8);
            inputItems[i].setTimeout(10);
        }
        InputParameters inputParameters = new InputParameters();
        InputButtonParameters[] inputButtonParameters = new InputButtonParameters[3];
        inputButtonParameters[0] = new InputButtonParameters();
        inputButtonParameters[0].setHeight(97);
        inputButtonParameters[0].setWidth(73);
        inputButtonParameters[0].setPictureType(PictureType.COLOR_IMAGE);
        inputButtonParameters[0].setId(1);
        inputButtonParameters[0].setX(1);
        inputButtonParameters[0].setY(140);

        inputButtonParameters[1] = new InputButtonParameters();
        inputButtonParameters[1].setHeight(50);
        inputButtonParameters[1].setWidth(60);
        inputButtonParameters[1].setPictureType(PictureType.BITMAP);
        inputButtonParameters[1].setId(1);
        inputButtonParameters[1].setX(107);
        inputButtonParameters[1].setY(140);

        inputButtonParameters[2] = new InputButtonParameters();
        inputButtonParameters[2].setHeight(97);
        inputButtonParameters[2].setWidth(73);
        inputButtonParameters[2].setPictureType(PictureType.COLOR_IMAGE);
        inputButtonParameters[2].setId(1);
        inputButtonParameters[2].setX(213);
        inputButtonParameters[2].setY(140);
        inputParameters.setButtons(inputButtonParameters);
        inputParameters.setBytePassKey(0);
        inputParameters.setCipherType(CipherType.DES_ECB);
        SymmetricKey encryptKey = new SymmetricKey();
        encryptKey.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_WK_DATA);
        encryptKey.setKeyType(KeyType.DES);
        encryptKey.setKeyUsage(KeyUsage.DATA);
        inputParameters.setEncryptKey(encryptKey);
        inputParameters.setIv(null);
        inputParameters.setDisplayLine(2);
        inputParameters.setPromptLine(1);

        try {
            extKeyboard.inputData(inputItems, inputParameters, new InputListener() {
                @Override
                public void onComplete() {
                    for (InputItem inputItem : inputItems) {
                        showMessage("Entry data: " + (inputItem.getValue() == null ? "null" : new String(inputItem.getValue())));
                        showMessage("Button code: " + inputItem.getButtonCode());
                        showMessage("Actual Length: " + inputItem.getActualLen());
                    }
                }

                @Override
                public void onError(int errorCode, String errorMessage) {
                    showMessage(String.format(Locale.US, "Failed to input data[%d]:%s", errorCode, errorMessage), MessageTag.ERROR);
                }
            });
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.ext_keyboard_input_data));
        }
    }

    @MethodGridEntity(btnnameid = R.string.ext_keyboard_input_amount, functionid = INDEX_EXTKEYBORAD_INPUT_AMOUNT)
    private void inputAmount() {
        AmountParameters amountParameters = new AmountParameters();
        amountParameters.setTitle("Test Sale");
        amountParameters.setText("Test Text");
        amountParameters.setMaxDigits(12);
        try {
            extKeyboard.inputAmount(AmountType.DEFAULT, amountParameters, 20, new AmountListener() {
                @Override
                public void onResult(BigDecimal bigDecimal) {
                    showMessage("Amount:" + bigDecimal.toString());
                }
            });
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.ext_keyboard_input_amount));
        }
    }

}
