package com.newland.nsdkdemo.external.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;

import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.crypto.AsymAlgorithmParameters;
import com.newland.nsdk.core.api.common.crypto.AsymCryptoMode;
import com.newland.nsdk.core.api.common.crypto.AsymEncodingMode;
import com.newland.nsdk.core.api.common.crypto.CipherOutput;
import com.newland.nsdk.core.api.common.crypto.CipherType;
import com.newland.nsdk.core.api.common.crypto.MACOutput;
import com.newland.nsdk.core.api.common.crypto.MACType;
import com.newland.nsdk.core.api.common.crypto.MessageDigestType;
import com.newland.nsdk.core.api.common.crypto.PaddingMode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.keymanager.AsymKeyType;
import com.newland.nsdk.core.api.common.keymanager.AsymKeyUsage;
import com.newland.nsdk.core.api.common.keymanager.AsymmetricKey;
import com.newland.nsdk.core.api.common.keymanager.DUKPTDerivateKey;
import com.newland.nsdk.core.api.common.keymanager.DUKPTDerivateUsage;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.api.common.keymanager.KeyUsage;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.external.crypto.ExtCrypto;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.AppConfig;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.annotation.MethodGridEntity;
import com.newland.nsdkdemo.common.utils.DialogUtils;
import com.newland.nsdkdemo.common.utils.EnumUtils;
import com.newland.nsdkdemo.common.utils.MessageTag;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class ExtCryptoFragment extends ExtBaseFragment {

    private String TAG = "ExtCryptoFragment";
    private ExtCrypto extCrypto;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor mEditor;
    private static final int INDEX_EXTPINPAD_MAC = 1;
    private static final int INDEX_EXTPINPAD_ENCRYPT = 2;
    private static final int INDEX_EXTPINPAD_DECRYPT = 3;
    private static final int INDEX_EXTPINPAD_AES_ENCRYPT = 4;
    private static final int INDEX_EXTPINPAD_AES_DECRYPT = 5;
    private static final int INDEX_EXTPINPAD_DUKPT_ENCRYPT = 6;
    private static final int INDEX_EXTPINPAD_DUKPT_DECRYPT = 7;
    private static final int INDEX_EXTPINPAD_RANDOM = 8;
    private static final int INDEX_EXTPINPAD_ASYM_SIGN_VERIFY = 9;
    private static final int INDEX_EXTPINPAD_ASYM_ENCRYPT = 10;
    private static final int INDEX_EXTPINPAD_ASYM_DECRYPT = 11;
    private static final int INDEX_EXTPINPAD_BIGDATA_ENCRYPT = 12;
    private static final int INDEX_EXTPINPAD_BIGDATA_DECRYPT = 13;
    private static final int INDEX_EXTPINPAD_ENCRYPT_OPTIONAL = 14;
    private static final int INDEX_EXTPINPAD_DECRYPT_OPTIONAL = 15;
    private static final int INDEX_EXTPINPAD_CALCULATE_MAC = 16;

    private byte[] cipherout;
    private byte[] decryptIVData;
    private int decryptAsymKeyID;
    private String messageDigestFlag;
    @SuppressLint("ValidFragment")
    public ExtCryptoFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_extcipher_f);
    }

    @Override
    public void initData() {
        extCrypto = (ExtCrypto) moduleManager.getModule(ModuleType.EXT_CRYPTO);
        sharedPreferences = context.getSharedPreferences("EXT_CRYPTO", Context.MODE_PRIVATE);
        mEditor = sharedPreferences.edit();
    }

    @Override
    public Object getModule() {
        return ExtCryptoFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_cal_mac, functionid = INDEX_EXTPINPAD_MAC)
    private void generateMac() {
        byte[] datain = ISOUtils.hex2byte("88888881113366677883373215563223abcdeffdaabbccdd");
        MACType macType = null;
        try {
            macType = MACType.TDES_X99;
            MACOutput out = extCrypto.generateMAC(AppConfig.Keys.MKSK_DES_INDEX_WK_MAC, macType, null, datain);
            if (out != null) {
                showMessage("Generate MAC: " + ISOUtils.hexString(out.getData()));
            } else {
                showMessage("Generate MAC: MacOutput is null");
            }

        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "generate MAC");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_encry, functionid = INDEX_EXTPINPAD_ENCRYPT)
    private void encrypt() {
        byte[] datain = ISOUtils.hex2byte("88888881113366677883373215563223abcdeffdaabbccdd");
        SymmetricKey key = new SymmetricKey();
        key.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_WK_DATA);
        try {
            CipherOutput out = extCrypto.encrypt(key, CipherType.DES_ECB, null, null, datain);
            if (out != null) {
                showMessage("Encrypt: " + ISOUtils.hexString(out.getData()));
            } else {
                showMessage("Encrypt: CipherOutput is null");
            }
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "do encrypt");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_decrypt, functionid = INDEX_EXTPINPAD_DECRYPT)
    private void decrypt() {
        byte[] datain = ISOUtils.hex2byte("DD55F3341D5ECFF5FBC3336594620743D316FC7C3EABE42E");
        SymmetricKey key = new SymmetricKey();
        key.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_WK_DATA);
        try {
            CipherOutput out = extCrypto.decrypt(key, CipherType.DES_ECB, null, null, datain);
            if (out != null) {
                showMessage("Decrypt: " + ISOUtils.hexString(out.getData()));
            } else {
                showMessage("Decrypt: CipherOutput is null");
            }
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "do decrypt");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_aes_encry, functionid = INDEX_EXTPINPAD_AES_ENCRYPT)
    private void aesEncrypt() {
        byte[] datain = new byte[16];
        SymmetricKey key = new SymmetricKey();
        key.setKeyID(AppConfig.Keys.MKSK_AES_INDEX_WK_DATA);
        try {
            CipherOutput out = extCrypto.encrypt(key, CipherType.AES_ECB, null, null, datain);
            if (out != null) {
                showMessage("AES Encrypt: " + ISOUtils.hexString(out.getData()));
            } else {
                showMessage("AES Encrypt: CipherOutput is null");
            }
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "do AES encrypt");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_aes_decrypt, functionid = INDEX_EXTPINPAD_AES_DECRYPT)
    private void aesDecrypt() {
        byte[] datain = new byte[16];
        SymmetricKey key = new SymmetricKey();
        key.setKeyID(AppConfig.Keys.MKSK_AES_INDEX_WK_DATA);
        try {
            CipherOutput out = extCrypto.decrypt(key, CipherType.AES_ECB, null, null, datain);
            if (out != null) {
                showMessage("AES Decrypt: " + ISOUtils.hexString(out.getData()));
            } else {
                showMessage("AES Decrypt: CipherOutput is null");
            }
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "do AES decrypt");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_dukpt_encry, functionid = INDEX_EXTPINPAD_DUKPT_ENCRYPT)
    private void dukptEncrypt() {
        byte[] datain = new byte[16];
        SymmetricKey key = new SymmetricKey();
        key.setKeyID(AppConfig.Keys.DUKPT_DES_INDEX);
        key.setKeyType(KeyType.DES);
        key.setKeyUsage(KeyUsage.DUKPT);
        try {
            CipherOutput out = extCrypto.encrypt(key, CipherType.DUKPT_ECB_RESP, null, null, datain);
            if (out != null) {
                showMessage("DUKPT Encrypt: " + ISOUtils.hexString(out.getData()));
            } else {
                showMessage("DUKPT Encrypt: CipherOutput is null");
            }
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "do DUKPT encrypt");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_dukpt_decrypt, functionid = INDEX_EXTPINPAD_DUKPT_DECRYPT)
    private void dukptDecrypt() {
        byte[] datain = new byte[16];
        SymmetricKey key = new SymmetricKey();
        key.setKeyID(AppConfig.Keys.DUKPT_DES_INDEX);
        key.setKeyType(KeyType.DES);
        key.setKeyUsage(KeyUsage.DUKPT);
        try {
            CipherOutput out = extCrypto.decrypt(key, CipherType.DUKPT_ECB_RESP, null, null, datain);
            if (out != null) {
                showMessage("DUKPT Decrypt: " + ISOUtils.hexString(out.getData()));
            } else {
                showMessage("DUKPT Decrypt: CipherOutput is null");
            }
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "do DUKPT decrypt");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_get_random_num, functionid = INDEX_EXTPINPAD_RANDOM)
    private void getRandom() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_get_random_num), null, R.layout.dialog_crypto_random_number, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                EditText etRandomDataLen = dialogView.findViewById(R.id.et_crypto_set_random_data_len);
                int dataLen = Integer.parseInt(etRandomDataLen.getText().toString());
                if (dataLen > 0) {
                    try {
                        byte[] random = extCrypto.getRandom(dataLen);
                        if (random != null) {
                            showMessage(context.getText(R.string.msg_random_result) + ISOUtils.hexString(random));
                        } else {
                            showMessage(context.getText(R.string.msg_random_result) + " is null");
                        }
                    } catch (NSDKException e) {
                        e.printStackTrace();
                        showErrorMessage(e, context.getString(R.string.msg_random_result));
                    }
                } else {
                    showMessage(context.getString(R.string.msg_input_illegal), MessageTag.ERROR);
                }

            }
        });

    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_asym_sign_verify, functionid = INDEX_EXTPINPAD_ASYM_SIGN_VERIFY)
    public void signAsym() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_pin_asym_sign_verify), null, R.layout.dialog_crypto_asym_sign_verify, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View view) {
                Switch swDefaultKeyID = view.findViewById(R.id.switch_default_key_id_btn);
                swDefaultKeyID.setChecked(true);
                LinearLayout llEditKeyIDParams = view.findViewById(R.id.switch_default_key_id_btn);
                llEditKeyIDParams.setVisibility(View.GONE);
                EditText setAsymKeyID = view.findViewById(R.id.et_sign_verify_asym_keyID);
                Spinner spnAsymKeyType = view.findViewById(R.id.spn_sign_verify_asym_keyType);
                Spinner spnAsymKeyUsage = view.findViewById(R.id.spn_sign_verify_asym_keyUsage);
                Spinner spnMessageDigestType = view.findViewById(R.id.spn_sign_verify_asym_messageDigestType);
                Spinner spnAsymEncodingMode = view.findViewById(R.id.spn_sign_verify_asym_encodingMode);
                EditText etOriginData = view.findViewById(R.id.et_sign_verify_original_data);

                AsymKeyType asymKeyType = EnumUtils.getAsymKeyType(spnAsymKeyType.getSelectedItem().toString());
                AsymKeyUsage asymKeyUsage = EnumUtils.getAsymKeyUsage(spnAsymKeyUsage.getSelectedItem().toString());
                MessageDigestType messageDigestType = EnumUtils.getMessageDigestType(spnMessageDigestType.getSelectedItem().toString());
                AsymEncodingMode asymEncodingMode = EnumUtils.getAsymEncodingMode(spnAsymEncodingMode.getSelectedItem().toString());

                swDefaultKeyID.setOnCheckedChangeListener(((buttonView, isChecked) -> {
                    if (isChecked) {
                        llEditKeyIDParams.setVisibility(View.GONE);
                    } else {
                        llEditKeyIDParams.setVisibility(View.VISIBLE);
                    }
                }));
                if (swDefaultKeyID.isChecked()) {
                    AsymmetricKey key = new AsymmetricKey();
                    key.setKeyID((byte) AppConfig.Keys.ASYM_AUTH_ID);
                    key.setKeyUsage(AsymKeyUsage.AUTH);
                    key.setKeyType(AsymKeyType.RSA);

                    AsymAlgorithmParameters parameters = new AsymAlgorithmParameters();
                    parameters.setMessageDigestType(MessageDigestType.SHA256);
                    parameters.setEncodingMode(AsymEncodingMode.PKCS_V15);

                    String originalString = "12345678";
                    byte[] hash = null;
                    byte[] signedData = null;

                    try {
                        MessageDigest digest = MessageDigest.getInstance("SHA-256");
                        hash = digest.digest(originalString.getBytes());
                        signedData = extCrypto.signAsym(key, parameters, hash);
                        showMessage(String.format("Asym signed data: %s", ISOUtils.hexString(signedData)));
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                        showMessage("Failed to get SHA256 message digest instance.", MessageTag.ERROR);
                    } catch (NSDKException e) {
                        e.printStackTrace();
                        showErrorMessage(e, "do asymmetric signing");
                    }

                    try {
                        extCrypto.verifyAsym(key, parameters, hash, signedData);
                        showMessage("Signed data is verified successfully.");
                    } catch (NSDKException e) {
                        e.printStackTrace();
                        showErrorMessage(e, "verify signed data");
                    }
                } else {
                    EditText etAsymKeyID = view.findViewById(R.id.et_sign_verify_asym_keyID);
                    int keyID = Integer.parseInt(etAsymKeyID.getText().toString());
                    AsymmetricKey key = new AsymmetricKey();
                    key.setKeyID((byte) keyID);
                    key.setKeyUsage(asymKeyUsage);
                    key.setKeyType(asymKeyType);

                    AsymAlgorithmParameters parameters = new AsymAlgorithmParameters();
                    parameters.setMessageDigestType(messageDigestType);
                    parameters.setEncodingMode(asymEncodingMode);

                    String originalString = etOriginData.getText().toString();
                    byte[] hash = null;
                    byte[] signedData = null;

                    try {
                        MessageDigest digest = MessageDigest.getInstance(messageDigestFlag);
                        hash = digest.digest(originalString.getBytes());
                        signedData = extCrypto.signAsym(key, parameters, hash);
                        showMessage(String.format("Asym signed data: %s", ISOUtils.hexString(signedData)));
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                        showMessage("Failed to get SHA256 message digest instance.", MessageTag.ERROR);
                    } catch (NSDKException e) {
                        e.printStackTrace();
                        showErrorMessage(e, "do asymmetric signing");
                    }

                    try {
                        extCrypto.verifyAsym(key, parameters, hash, signedData);
                        showMessage("Signed data is verified successfully.");
                    } catch (NSDKException e) {
                        e.printStackTrace();
                        showErrorMessage(e, "verify signed data");
                    }
                }


            }
        });
//        AsymmetricKey key = new AsymmetricKey();
////        key.setKeyID((byte) 10);
//        key.setKeyID((byte) AppConfig.Keys.ASYM_AUTH_ID);
//        key.setKeyUsage(AsymKeyUsage.AUTH);
//        key.setKeyType(AsymKeyType.RSA);
//
//        AsymAlgorithmParameters parameters = new AsymAlgorithmParameters();
//        parameters.setMessageDigestType(MessageDigestType.SHA256);
//        parameters.setEncodingMode(AsymEncodingMode.PKCS_V15);
//
//        String originalString = "12345678";
//        byte[] hash = null;
//        byte[] signedData = null;
//
//        try {
//            MessageDigest digest = MessageDigest.getInstance("SHA-256");
//            hash = digest.digest(originalString.getBytes());
//            signedData = extCrypto.signAsym(key, parameters, hash);
//            showMessage(String.format("Asym signed data: %s", ISOUtils.hexString(signedData)));
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//            showMessage("Failed to get SHA256 message digest instance.", MessageTag.ERROR);
//        } catch (NSDKException e) {
//            e.printStackTrace();
//            showErrorMessage(e, "do asymmetric signing");
//        }
//
//        try {
//            extCrypto.verifyAsym(key, parameters, hash, signedData);
//            showMessage("Signed data is verified successfully.");
//        } catch (NSDKException e) {
//            e.printStackTrace();
//            showErrorMessage(e, "verify signed data");
//        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_asym_encry, functionid = INDEX_EXTPINPAD_ASYM_ENCRYPT)
    public void encryptAsym() {
        AsymmetricKey key = new AsymmetricKey();
        key.setKeyID((byte) AppConfig.Keys.ASYM_DATA_ID);
        key.setKeyUsage(AsymKeyUsage.DATA);
        key.setKeyType(AsymKeyType.RSA);

        AsymAlgorithmParameters parameters = new AsymAlgorithmParameters();
        parameters.setMessageDigestType(MessageDigestType.SHA256);
        parameters.setEncodingMode(AsymEncodingMode.PKCS_V21);
        parameters.setCryptoMode(AsymCryptoMode.PUBLIC);

        byte[] data = ISOUtils.hex2byte("0102030405060708090A0B0C0D0E0F");

        try {
            byte[] result = extCrypto.encryptAsym(key, parameters, data);
            showMessage(String.format("Asym encryption result: %s", ISOUtils.hexString(result)));
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "do asym encryption");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_asym_decry, functionid = INDEX_EXTPINPAD_ASYM_DECRYPT)
    public void decryptAsym() {
        AsymmetricKey key = new AsymmetricKey();
        key.setKeyID((byte) AppConfig.Keys.ASYM_DATA_ID);
        key.setKeyUsage(AsymKeyUsage.DATA);
        key.setKeyType(AsymKeyType.RSA);

        AsymAlgorithmParameters parameters = new AsymAlgorithmParameters();
        parameters.setMessageDigestType(MessageDigestType.SHA256);
        parameters.setEncodingMode(AsymEncodingMode.PKCS_V21);
        parameters.setCryptoMode(AsymCryptoMode.PUBLIC);

        byte[] data = ISOUtils.hex2byte("0102030405060708090A0B0C0D0E0F");

        try {
            byte[] encryptedData = extCrypto.encryptAsym(key, parameters, data);
            parameters.setCryptoMode(AsymCryptoMode.PRIVATE);
            byte[] decryptedData = extCrypto.decryptAsym(key, parameters, encryptedData);
            if (Arrays.equals(data, decryptedData)) {
                showMessage("Asym decryption is successful.");
            } else {
                showMessage("Asym decryption result is not equal with original data.");
                showMessage(String.format("Original data: %s", ISOUtils.hexString(data)));
                showMessage(String.format("Encrypted data: %s", ISOUtils.hexString(encryptedData)));
                showMessage(String.format("Decrypted data: %s", ISOUtils.hexString(decryptedData)));
            }
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "do asym encryption");
        }
    }

    String bigData128 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1111";

    @MethodGridEntity(btnnameid = R.string.tv_pin_encry, functionid = INDEX_EXTPINPAD_BIGDATA_ENCRYPT)
    private void bigDataEncrypt() {
        String bigData4K = bigData128;
        for (int i = 0; i < 5; i++) {
            bigData4K += bigData4K;
        }
        LogUtils.d("ExtCryptoFragment", "bigDataEncrypt Data Length: " + bigData4K.length());
        LogUtils.d("ExtCryptoFragment", "bigDataEncrypt Data: " + bigData4K);
        byte[] datain = ISOUtils.hex2byte(bigData4K);
        SymmetricKey key = new SymmetricKey();
        key.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_WK_DATA);
        try {
            CipherOutput out = extCrypto.encrypt(key, CipherType.DES_ECB, null, null, datain);
            if (out != null) {
                showMessage("bigDataEncrypt Data length: " + ISOUtils.hexString(out.getData()).length());
                LogUtils.d("ExtCryptoFragment", "bigDataEncrypt Data Length: " + ISOUtils.hexString(out.getData()).length());
                LogUtils.d("ExtCryptoFragment", "bigDataEncrypt Data: " + ISOUtils.hexString(out.getData()));
            } else {
                showMessage("bigDataEncrypt: CipherOutput is null");
            }
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "do encrypt");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_decrypt, functionid = INDEX_EXTPINPAD_BIGDATA_DECRYPT)
    private void bigDataDecryptStressTest() {
        String bigData4K = bigData128;
        for (int i = 0; i < 5; i++) {
            bigData4K += bigData4K;
        }
        LogUtils.d("ExtCryptoFragment", "bigDataDecrypt Data Length: " + bigData4K.length());
        LogUtils.d("ExtCryptoFragment", "bigDataDecrypt Data: " + bigData4K);
        byte[] datain = ISOUtils.hex2byte(bigData4K);
        SymmetricKey key = new SymmetricKey();
        key.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_WK_DATA);
        try {
            for (int n = 0; n < 128; n++) {
                CipherOutput out = extCrypto.decrypt(key, CipherType.DES_ECB, null, null, datain);
                if (out != null) {
                    showMessage(n + ": bigDataDecrypt Data Length: " + ISOUtils.hexString(out.getData()).length());
                    LogUtils.d("ExtCryptoFragment", "bigDataDecrypt Data Length: " + ISOUtils.hexString(out.getData()).length());
                    LogUtils.d("ExtCryptoFragment", "bigDataDecrypt Data: " + ISOUtils.hexString(out.getData()));
                } else {
                    showMessage("bigDataDecrypt: CipherOutput is null");
                }
            }
            showMessage("End bigData Decrypt Stress Test");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "do decrypt");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_encrypt_test, functionid = INDEX_EXTPINPAD_ENCRYPT_OPTIONAL)
    public void extEncryptOptional() {
        DialogUtils.createCustomDialog(context, R.string.tv_pin_encrypt_test, null, R.layout.dialog_crypto_encrypt, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                //Init Dialog And  RadioGroup State
                Switch swDefaultCasesKeyID = view.findViewById(R.id.switch_default_key_id_btn);
                swDefaultCasesKeyID.setChecked(true);
                Spinner spnDefaultKeyID = view.findViewById(R.id.spn_crypto_encrypt_defaultKeyID);
                LinearLayout llDefaultKeyIDParams = view.findViewById(R.id.linear_crypto_encrypt_defaultKeyID_params);
                llDefaultKeyIDParams.setVisibility(View.VISIBLE);
                LinearLayout llEditKeyIDParams = view.findViewById(R.id.linear_crypto_encrypt_editKeyID_params);
                llEditKeyIDParams.setVisibility(View.GONE);
                LinearLayout llEditKeyIDKeyType = view.findViewById(R.id.linear_crypto_encrypt_keyType_params);
                llEditKeyIDKeyType.setVisibility(View.GONE);
                RadioGroup rgSelection = view.findViewById(R.id.crypto_symm_asym_selection_radioGroup);
                RadioButton rbEncrypt = view.findViewById(R.id.crypto_encrypt_radio);
                RadioButton rbAsymEncrypt = view.findViewById(R.id.crypto_asym_encrypt_radio);
                rbEncrypt.setChecked(true);
                LinearLayout llSymmParams = view.findViewById(R.id.linear_crypto_encryption_params);
                llSymmParams.setVisibility(View.VISIBLE);
                LinearLayout llAsymParams = view.findViewById(R.id.linear_crypto_asym_encryption_params);
                llAsymParams.setVisibility(View.GONE);
                Spinner spnAsymDefaultKeyID = view.findViewById(R.id.spn_crypto_encrypt_asym_defaultKeyID);
                LinearLayout llEncryptAsymDefaultKeyIDParams = view.findViewById(R.id.linear_crypto_encrypt_asym_defaultKeyID_params);
                llEncryptAsymDefaultKeyIDParams.setVisibility(View.GONE);
                LinearLayout llEncryptAsymEditKeyIDParams = view.findViewById(R.id.linear_crypto_encrypt_asym_editKeyID_params);


                swDefaultCasesKeyID.setOnCheckedChangeListener(((buttonView, isChecked) -> {
                    if (isChecked) {
                        if (rbAsymEncrypt.isChecked()) {
                            llEditKeyIDKeyType.setVisibility(View.GONE);
                            llEditKeyIDParams.setVisibility(View.GONE);
                            llDefaultKeyIDParams.setVisibility(View.GONE);
                            llEncryptAsymDefaultKeyIDParams.setVisibility(View.VISIBLE);
                            llEncryptAsymEditKeyIDParams.setVisibility(View.GONE);
                        } else {
                            llDefaultKeyIDParams.setVisibility(View.VISIBLE);
                            llEditKeyIDKeyType.setVisibility(View.GONE);
                            llEditKeyIDParams.setVisibility(View.GONE);
                        }

                    } else {
                        if(rbAsymEncrypt.isChecked()) {
                            llDefaultKeyIDParams.setVisibility(View.GONE);
                            llEditKeyIDKeyType.setVisibility(View.GONE);
                            llEditKeyIDParams.setVisibility(View.GONE);
                            llEncryptAsymEditKeyIDParams.setVisibility(View.VISIBLE);
                            llEncryptAsymDefaultKeyIDParams.setVisibility(View.GONE);
                        } else {
                            llDefaultKeyIDParams.setVisibility(View.GONE);
                            llEditKeyIDKeyType.setVisibility(View.VISIBLE);
                            llEditKeyIDParams.setVisibility(View.VISIBLE);
                        }

                    }
                }));

                rgSelection.setOnCheckedChangeListener(((group, checkedId) -> {
                    if (checkedId == rbEncrypt.getId()) {
                        llSymmParams.setVisibility(View.VISIBLE);
                        llAsymParams.setVisibility(View.GONE);

                    }
                    if (checkedId == rbAsymEncrypt.getId()) {
                        llSymmParams.setVisibility(View.GONE);
                        llAsymParams.setVisibility(View.VISIBLE);
                        llEditKeyIDKeyType.setVisibility(View.GONE);
                        llEditKeyIDParams.setVisibility(View.GONE);
                        llDefaultKeyIDParams.setVisibility(View.GONE);
                        if (swDefaultCasesKeyID.isChecked()) {
                            llEncryptAsymDefaultKeyIDParams.setVisibility(View.VISIBLE);
                            llEncryptAsymEditKeyIDParams.setVisibility(View.GONE);
                        } else {
                            llEncryptAsymDefaultKeyIDParams.setVisibility(View.GONE);
                            llEncryptAsymEditKeyIDParams.setVisibility(View.VISIBLE);
                        }


                    }

                    mEditor.putInt(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_SYMM_ASYM_SELECTION, checkedId);
                    mEditor.commit();
                }));
                //Keep RadioGroup's State
                int crypto_symm_asym_selection = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_SYMM_ASYM_SELECTION, 0);


//                Spinner dialog_crypto_encrypt_keyID = view.findViewById(R.id.dialog_crypto_encrypt_keyID);
                EditText etKeyID = view.findViewById(R.id.et_crypto_encrypt_keyID);
                etKeyID.setText(String.valueOf(sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_KEY_ID, 0)));
                Spinner spnKeyUsage = view.findViewById(R.id.spn_crypto_encrypt_keyUsage);
                LinearLayout llDUKPTEncryptionParams = view.findViewById(R.id.linear_crypto_dukpt_encryption);
                llDUKPTEncryptionParams.setVisibility(View.GONE);
                //AES-DUKPT:Additional Spinner for DukptDerivateKeyInformation
                Spinner spnCipherType = view.findViewById(R.id.spn_crypto_encrypt_cipheType);
                spnCipherType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_CIPHER_TYPE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int crypto_ciphetType = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_CIPHER_TYPE, 0);
                spnCipherType.setSelection(crypto_ciphetType);

                Spinner spnKeyType = view.findViewById(R.id.spn_crypto_encrypt_keyType);
                spnKeyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (spnKeyType.getSelectedItem().toString().equals("AES") && spnKeyUsage.getSelectedItem().toString().equals("DUKPT")) {
                            llDUKPTEncryptionParams.setVisibility(View.VISIBLE);
                            String[] AES_DUKPT_cipherMode = new String[]{"AES_DUKPT_ECB", "AES_DUKPT_CBC"};
                            ArrayAdapter adapter = new ArrayAdapter(context, R.layout.spinner_items, AES_DUKPT_cipherMode);
                            spnCipherType.setAdapter(adapter);
                        } else {
                            llDUKPTEncryptionParams.setVisibility(View.GONE);
                            String[] Others_cipherMode = new String[]{"DES_ECB", "DES_CBC", "DES_CFB", "DES_OFB", "AES_ECB", "AES_CBC", "AES_CFB", "AES_OFB", "DUKPT_ECB_RESP", "DUKPT_ECB_BOTH", "DUKPT_CBC_RESP", "DUKPT_CBC_BOTH",
                                    "DUKPT_CFB_RESP", "DUKPT_CFB_BOTH", "DUKPT_OFB_RESP", "DUKPT_OFB_BOTH", "SM4_ECB", "SM4_CBC"};
                            ArrayAdapter adapter = new ArrayAdapter(context, R.layout.spinner_items, Others_cipherMode);
                            spnCipherType.setAdapter(adapter);
                        }
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_KEY_TYPE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int crypto_encrypt_keyType = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_KEY_TYPE, 0);
                spnKeyType.setSelection(crypto_encrypt_keyType);
                spnKeyUsage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if ((spnKeyType.getSelectedItem().toString().equals("AES") && spnKeyUsage.getSelectedItem().toString().equals("DUKPT"))) {
                            llDUKPTEncryptionParams.setVisibility(View.VISIBLE);
                            String[] AES_DUKPT_cipherMode = new String[]{"AES_DUKPT_ECB", "AES_DUKPT_CBC"};
                            ArrayAdapter adapter = new ArrayAdapter(context, R.layout.spinner_items, AES_DUKPT_cipherMode);
                            spnCipherType.setAdapter(adapter);
                        } else {
                            llDUKPTEncryptionParams.setVisibility(View.GONE);
                            String[] Others_cipherMode = new String[]{"DES_ECB", "DES_CBC", "DES_CFB", "DES_OFB", "AES_ECB", "AES_CBC", "AES_CFB", "AES_OFB", "DUKPT_ECB_RESP", "DUKPT_ECB_BOTH", "DUKPT_CBC_RESP", "DUKPT_CBC_BOTH",
                                    "DUKPT_CFB_RESP", "DUKPT_CFB_BOTH", "DUKPT_OFB_RESP", "DUKPT_OFB_BOTH", "SM4_ECB", "SM4_CBC"};
                            ArrayAdapter adapter = new ArrayAdapter(context, R.layout.spinner_items, Others_cipherMode);
                            spnCipherType.setAdapter(adapter);
                        }
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_KEY_USAGE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                //Keep Spinners' State for Next test
                //Symmetric Encrypt
                int crypto_encrypt_keyUsage = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_KEY_USAGE, 0);
                spnKeyUsage.setSelection(crypto_encrypt_keyUsage);
                Spinner spnDUKPTDerivateUsage = view.findViewById(R.id.spn_crypto_encrypt_dukpt_derivateUsage);
                spnDUKPTDerivateUsage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_DUKPT_DERIVATE_USAGE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int encryptDukptDerivateUsage = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_DUKPT_DERIVATE_USAGE, 0);
                spnDUKPTDerivateUsage.setSelection(encryptDukptDerivateUsage);

                Spinner spnDUKPTDerivateKeyType = view.findViewById(R.id.spn_crypto_encrypt_dukpt_derivateKeyType);
                spnDUKPTDerivateKeyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_DUKPT_DERIVATE_TYPE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int encryptDukptDerivateKeyType = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_DUKPT_DERIVATE_TYPE, 0);
                spnDUKPTDerivateKeyType.setSelection(encryptDukptDerivateKeyType);

                Spinner spnDUKPTDerivateKeyLen = view.findViewById(R.id.spn_crypto_encrypt_dukpt_derivateKeyLen);
                spnDUKPTDerivateKeyLen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_DUKPT_DERIVATE_KEY_LEN, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int encryptDukptDerivateKeyLen = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_DUKPT_DERIVATE_KEY_LEN, 0);
                spnDUKPTDerivateKeyLen.setSelection(encryptDukptDerivateKeyLen);
                Spinner spnPaddingMode = view.findViewById(R.id.spn_crypto_encrypt_paddingMode);
                spnPaddingMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_PADDING_MODE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                spnDUKPTDerivateKeyLen.setSelection(1);
                spnDUKPTDerivateKeyType.setSelection(1);
                spnDUKPTDerivateUsage.setSelection(3);


                int paddingMode = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_PADDING_MODE, 0);
                spnPaddingMode.setSelection(paddingMode);

                EditText etPaddingData = view.findViewById(R.id.spn_crypto_encrypt_paddingData);
                etPaddingData.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_PADDING_DATA, null));

                EditText etEncryptData = view.findViewById(R.id.spn_crypto_encrypt_encryptData);
                etEncryptData.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_DATA_IN, null));

                //Asymmetric Encryption
                EditText etAsymKeyID = view.findViewById(R.id.spn_crypto_encrypt_asym_keyID);
                etAsymKeyID.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_ASYM_KEY_ID, ""));




                Spinner spnAsymKeyType = view.findViewById(R.id.spn_crypto_encrypt_asym_keyType);
                spnAsymKeyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_ASYM_KEY_TYPE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int asymKeyType = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_ASYM_KEY_TYPE, 0);
                spnAsymKeyType.setSelection(asymKeyType);

                Spinner spnAsymKeyUsage = view.findViewById(R.id.spn_crypto_encrypt_asym_keyUsage);
                spnAsymKeyUsage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_ASYM_KEY_USAGE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int asymKeyUsage = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_ASYM_KEY_USAGE, 0);
                spnAsymKeyUsage.setSelection(asymKeyUsage);

                Spinner spnMessageDigestType = view.findViewById(R.id.spn_crypto_encrypt_asym_messageDigestType);
                spnMessageDigestType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_ASYM_MESSAGE_DIGEST_TYPE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int asymMessageDigestType = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_ASYM_MESSAGE_DIGEST_TYPE, 0);
                spnMessageDigestType.setSelection(asymMessageDigestType);

                Spinner spnAsymCryptoMode = view.findViewById(R.id.spn_crypto_encrypt_asym_cryptoMode);
                spnAsymCryptoMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_ASYM_CRYPTO_MODE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int asymCryptoMode = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_ASYM_CRYPTO_MODE, 0);
                spnAsymCryptoMode.setSelection(asymCryptoMode);

                Spinner spnAsymEncodingMode = view.findViewById(R.id.spn_crypto_encrypt_asym_encodingMode);
                spnAsymEncodingMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_ASYM_CRYPTO_ENCODING_MODE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int asymEncodingMode = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_ASYM_CRYPTO_ENCODING_MODE, 0);
                spnAsymEncodingMode.setSelection(asymEncodingMode);

                EditText etAsymDataIn = view.findViewById(R.id.et_crypto_encrypt_asym_datain);
                etAsymDataIn.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_ASYM_CRYPTO_DATA_IN, null));

                spnDefaultKeyID.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if ((spnKeyType.getSelectedItem().toString().equals("AES") && spnKeyUsage.getSelectedItem().toString().equals("DUKPT"))|| spnDefaultKeyID.getSelectedItem().toString().equals("DUKPT_AES_INDEX")) {
                            llDUKPTEncryptionParams.setVisibility(View.VISIBLE);
                            String[] AES_DUKPT_cipherMode = new String[]{"AES_DUKPT_ECB", "AES_DUKPT_CBC"};
                            ArrayAdapter adapter = new ArrayAdapter(context, R.layout.spinner_items, AES_DUKPT_cipherMode);
                            spnCipherType.setAdapter(adapter);

                        } else {
                            llDUKPTEncryptionParams.setVisibility(View.GONE);
                            String[] Others_cipherMode = new String[]{"DES_ECB", "DES_CBC", "DES_CFB", "DES_OFB", "AES_ECB", "AES_CBC", "AES_CFB", "AES_OFB", "DUKPT_ECB_RESP", "DUKPT_ECB_BOTH", "DUKPT_CBC_RESP", "DUKPT_CBC_BOTH",
                                    "DUKPT_CFB_RESP", "DUKPT_CFB_BOTH", "DUKPT_OFB_RESP", "DUKPT_OFB_BOTH", "SM4_ECB", "SM4_CBC"};
                            ArrayAdapter adapter = new ArrayAdapter(context, R.layout.spinner_items, Others_cipherMode);
                            spnCipherType.setAdapter(adapter);
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onResult(int id, View view) {
                Switch swDefaultKeyID = view.findViewById(R.id.switch_default_key_id_btn);
                Spinner spnDefaultkeyID = view.findViewById(R.id.spn_crypto_encrypt_defaultKeyID);
                byte defaultKeyID = EnumUtils.getDefaultKeyID(spnDefaultkeyID.getSelectedItem().toString());
                RadioButton rbEncrypt = view.findViewById(R.id.crypto_encrypt_radio);
                RadioButton rbAsymEncrypt = view.findViewById(R.id.crypto_asym_encrypt_radio);

                Spinner spnKeyUsage = view.findViewById(R.id.spn_crypto_encrypt_keyUsage);
                KeyUsage keyUsage = EnumUtils.getKeyUsage(spnKeyUsage.getSelectedItem().toString());

                Spinner spnDUKPTDerivateUsage = view.findViewById(R.id.spn_crypto_encrypt_dukpt_derivateUsage);
                DUKPTDerivateUsage dukptDerivateUsage = EnumUtils.getDukptDerivateUsage(spnDUKPTDerivateUsage.getSelectedItem().toString());

                Spinner spnDUKPTDerivateKeyType = view.findViewById(R.id.spn_crypto_encrypt_dukpt_derivateKeyType);
                KeyType dukptDerivateKeyType = EnumUtils.getKeyType(spnDUKPTDerivateKeyType.getSelectedItem().toString());

                Spinner spnDUKPTDerivateKeyLen = view.findViewById(R.id.spn_crypto_encrypt_dukpt_derivateKeyLen);
                int dukptDerivateLen = EnumUtils.getKeyLen(spnDUKPTDerivateKeyLen.getSelectedItem().toString());

                Spinner spnCipherType = view.findViewById(R.id.spn_crypto_encrypt_cipheType);
                CipherType cipherType = EnumUtils.getCipherType(spnCipherType.getSelectedItem().toString());

                Spinner spnPaddingMode = view.findViewById(R.id.spn_crypto_encrypt_paddingMode);
                PaddingMode paddingMode = EnumUtils.getPaddingMode(spnPaddingMode.getSelectedItem().toString());

                EditText etPaddingData = view.findViewById(R.id.spn_crypto_encrypt_paddingData);
                byte[] iv = null;

                iv = ISOUtils.hex2byte(etPaddingData.getText().toString());
                mEditor.putString(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_PADDING_DATA, etPaddingData.getText().toString());
                mEditor.commit();

                LinearLayout llDUKPTEncryptionParams = view.findViewById(R.id.linear_crypto_dukpt_encryption);

                EditText etEncryptData = view.findViewById(R.id.spn_crypto_encrypt_encryptData);
                byte[] datain = ISOUtils.hex2byte(etEncryptData.getText().toString());
                mEditor.putString(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_DATA_IN, etEncryptData.getText().toString());
                mEditor.commit();
                //Asymmetric Encrypt
                Spinner spnAsymDefaultKeyID = view.findViewById(R.id.spn_crypto_encrypt_asym_defaultKeyID);
                byte asymDefaultKeyID = EnumUtils.getAsymDefaultKeyID(spnAsymDefaultKeyID.getSelectedItem().toString());

                Spinner spnAsymKeyType = view.findViewById(R.id.spn_crypto_encrypt_asym_keyType);
                AsymKeyType asymKeyType = EnumUtils.getAsymKeyType(spnAsymKeyType.getSelectedItem().toString());

                Spinner spnAsymKeyUsage = view.findViewById(R.id.spn_crypto_encrypt_asym_keyUsage);
                AsymKeyUsage asymKeyUsage = EnumUtils.getAsymKeyUsage(spnAsymKeyUsage.getSelectedItem().toString());

                Spinner spnAsymMessageDigestType = view.findViewById(R.id.spn_crypto_encrypt_asym_messageDigestType);
                MessageDigestType messageDigestType = EnumUtils.getMessageDigestType(spnAsymMessageDigestType.getSelectedItem().toString());

                Spinner spnAsymCryptoMode = view.findViewById(R.id.spn_crypto_encrypt_asym_cryptoMode);
                AsymCryptoMode asymCryptoMode = EnumUtils.getAsymCryptoMode(spnAsymCryptoMode.getSelectedItem().toString());

                Spinner spnAsymEncodingMode = view.findViewById(R.id.spn_crypto_encrypt_asym_encodingMode);
                AsymEncodingMode asymEncodingMode = EnumUtils.getAsymEncodingMode(spnAsymEncodingMode.getSelectedItem().toString());

                EditText etAsymDataIn = view.findViewById(R.id.et_crypto_encrypt_asym_datain);
                mEditor.putString(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_ASYM_CRYPTO_DATA_IN, etAsymDataIn.getText().toString());
                mEditor.commit();

                CipherOutput cipherOutput = null;
                if (rbEncrypt.isChecked()) {
                    if(swDefaultKeyID.isChecked()) {
                        SymmetricKey symmetricKey = new SymmetricKey();
                        symmetricKey.setKeyID(defaultKeyID);
                        symmetricKey.setKeyUsage(keyUsage);
                        if (llDUKPTEncryptionParams.getVisibility() == View.VISIBLE) {
                            DUKPTDerivateKey dukptDerivateKey = new DUKPTDerivateKey();
                            dukptDerivateKey.setKeyID(defaultKeyID);
                            dukptDerivateKey.setKeyUsage(keyUsage);
                            dukptDerivateKey.setDerivateKeyLen(dukptDerivateLen);
                            dukptDerivateKey.setDerivateKeyType(dukptDerivateKeyType);
                            dukptDerivateKey.setDerivateUsage(dukptDerivateUsage);
                            try {
                                cipherOutput = extCrypto.encrypt(dukptDerivateKey, cipherType, paddingMode, iv, datain);
                                showMessage("Encryption Result:" + ISOUtils.hexString(cipherOutput.getData()));
                                mEditor.putString(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_ENCRYPTION_CIPHER_OUT, ISOUtils.hexString(cipherOutput.getData()));
                                mEditor.commit();
                            } catch (NSDKException e) {
                                showErrorMessage(e, context.getString(R.string.tv_pin_encry));
                            }
                        } else {
                            try {
                                cipherOutput = extCrypto.encrypt(symmetricKey, cipherType, paddingMode, iv, datain);
                                cipherout = cipherOutput.getData();
                                mEditor.putString(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_ENCRYPTION_CIPHER_OUT, ISOUtils.hexString(cipherout));
                                mEditor.commit();
                                showMessage("Encryption Result:" + ISOUtils.hexString(cipherOutput.getData()));
                            } catch (NSDKException e) {
                                showErrorMessage(e, context.getString(R.string.tv_pin_encry));
                            }
                        }
                    } else {
                        EditText etKeyID = view.findViewById(R.id.et_crypto_encrypt_keyID);
                        int keyID = Integer.parseInt(etKeyID.getText().toString());
                        SymmetricKey symmetricKey = new SymmetricKey();
                        symmetricKey.setKeyID((byte) keyID);
                        symmetricKey.setKeyUsage(keyUsage);
                        if (llDUKPTEncryptionParams.getVisibility() == View.VISIBLE) {
                            DUKPTDerivateKey dukptDerivateKey = new DUKPTDerivateKey();
                            dukptDerivateKey.setKeyID((byte) keyID);
                            dukptDerivateKey.setKeyUsage(keyUsage);
                            dukptDerivateKey.setDerivateKeyLen(dukptDerivateLen);
                            dukptDerivateKey.setDerivateKeyType(dukptDerivateKeyType);
                            dukptDerivateKey.setDerivateUsage(dukptDerivateUsage);
                            try {
                                cipherOutput = extCrypto.encrypt(dukptDerivateKey, cipherType, paddingMode, iv, datain);
                                showMessage("Encryption Result:" + ISOUtils.hexString(cipherOutput.getData()));
                                mEditor.putString(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_ENCRYPTION_CIPHER_OUT, ISOUtils.hexString(cipherOutput.getData()));
                                mEditor.commit();
                            } catch (NSDKException e) {
                                showErrorMessage(e, context.getString(R.string.tv_pin_encry));
                            }
                        } else {
                            try {
                                cipherOutput = extCrypto.encrypt(symmetricKey, cipherType, paddingMode, iv, datain);
                                cipherout = cipherOutput.getData();
                                mEditor.putString(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_ENCRYPTION_CIPHER_OUT, ISOUtils.hexString(cipherout));
                                mEditor.commit();
                                showMessage("Encryption Result:" + ISOUtils.hexString(cipherOutput.getData()));
                            } catch (NSDKException e) {
                                showErrorMessage(e, context.getString(R.string.tv_pin_encry));
                            }
                        }
                    }

                } else if (rbAsymEncrypt.isChecked()) {
                    if (swDefaultKeyID.isChecked()) {

                        AsymmetricKey asymmetricKey = new AsymmetricKey();
                        asymmetricKey.setKeyUsage(asymKeyUsage);
                        asymmetricKey.setKeyID(asymDefaultKeyID);
                        asymmetricKey.setKeyType(asymKeyType);

                        AsymAlgorithmParameters asymAlgorithmParameters = new AsymAlgorithmParameters();
                        asymAlgorithmParameters.setCryptoMode(asymCryptoMode);
                        asymAlgorithmParameters.setEncodingMode(asymEncodingMode);
                        asymAlgorithmParameters.setMessageDigestType(messageDigestType);

                        try {
                            cipherout = extCrypto.encryptAsym(asymmetricKey, asymAlgorithmParameters, ISOUtils.hex2byte(etAsymDataIn.getText().toString()));
                            mEditor.putString(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_ASYM_ENCRYPTION_CIPHER_OUT, ISOUtils.hexString(cipherout));
                            mEditor.commit();
                            showMessage("Asym Encryption Result:" + ISOUtils.hexString(cipherout));
                        } catch (NSDKException e) {
                            showErrorMessage(e, context.getString(R.string.tv_pin_asym_encry));
                        }
                    } else {
                        EditText etAsymKeyID = view.findViewById(R.id.spn_crypto_encrypt_asym_keyID);
                        int asymKeyID = Integer.parseInt(etAsymKeyID.getText().toString());
                        AsymmetricKey asymmetricKey = new AsymmetricKey();
                        asymmetricKey.setKeyUsage(asymKeyUsage);
                        asymmetricKey.setKeyID((byte) asymKeyID);
                        asymmetricKey.setKeyType(asymKeyType);

                        AsymAlgorithmParameters asymAlgorithmParameters = new AsymAlgorithmParameters();
                        asymAlgorithmParameters.setCryptoMode(asymCryptoMode);
                        asymAlgorithmParameters.setEncodingMode(asymEncodingMode);
                        asymAlgorithmParameters.setMessageDigestType(messageDigestType);

                        try {
                            cipherout = extCrypto.encryptAsym(asymmetricKey, asymAlgorithmParameters, ISOUtils.hex2byte(etAsymDataIn.getText().toString()));
                            mEditor.putString(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_ASYM_ENCRYPTION_CIPHER_OUT, ISOUtils.hexString(cipherout));
                            mEditor.commit();
                            showMessage("Asym Encryption Result:" + ISOUtils.hexString(cipherout));
                        } catch (NSDKException e) {
                            showErrorMessage(e, context.getString(R.string.tv_pin_asym_encry));
                        }
                    }

                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_decrypt_test, functionid = INDEX_EXTPINPAD_DECRYPT_OPTIONAL)
    public void ExtDecryptOptional() {
        DialogUtils.createCustomDialog(context, R.string.tv_pin_decrypt_test, null, R.layout.dialog_crypto_decrypt, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                Switch swDefaultKeyId = view.findViewById(R.id.switch_default_key_id_btn);
                swDefaultKeyId.setChecked(true);
                Spinner spnDefaultKeyID = view.findViewById(R.id.spn_decrypt_default_keyID);
                LinearLayout llDefaultKeyIDParams = view.findViewById(R.id.linear_decrypt_defaultKeyID_params);
                llDefaultKeyIDParams.setVisibility(View.VISIBLE);
                LinearLayout llEditKeyIDParams = view.findViewById(R.id.linear_decrypt_editKeyID_params);
                llEditKeyIDParams.setVisibility(View.GONE);
                LinearLayout llDefaultKeyTypeParams = view.findViewById(R.id.linear_decrypt_keyType_params);
                llDefaultKeyTypeParams.setVisibility(View.GONE);
                Spinner spnKeyType = view.findViewById(R.id.spn_decrypt_keyType);
                Spinner spnKeyUsage = view.findViewById(R.id.spn_decrypt_keyUsage);
                Spinner spnDerivateKeyType = view.findViewById(R.id.spn_decrypt_dukpt_derivateKeyType);
                Spinner spnDerivateKeyLen = view.findViewById(R.id.spn_decrypt_dukpt_derivateKeyLen);
                Spinner spnDerivateUsage = view.findViewById(R.id.spn_decrypt_dukpt_derivateKeyUsage);
                Spinner spnCipherType = view.findViewById(R.id.spn_decrypt_cipherType);
                Spinner spnPaddingMode = view.findViewById(R.id.spn_decrypt_paddingMode);
                Spinner spnAsymKeyType = view.findViewById(R.id.spn_decrypt_asym_keyType);
                Spinner spnAsymKeyUsage = view.findViewById(R.id.spn_decrypt_asym_keyUsage);
                Spinner spnMessageDigestType = view.findViewById(R.id.spn_decrypt_asym_messageDigestType);
                Spinner spnAsymCryptoMode = view.findViewById(R.id.spn_decrypt_asym_cryptoMode);
                Spinner spnAsymEncodingMode = view.findViewById(R.id.spn_decrypt_asym_encodingMode);
                Spinner spnAESDUKPTCipherType = view.findViewById(R.id.spn_decrypt_aes_dukpt_cipherType);
                Spinner spnDecryptAsymDefaultKeyID = view.findViewById(R.id.spn_decrypt_asym_defaultKeyID);
                LinearLayout llDecryptAsymDefaultKeyIDParams = view.findViewById(R.id.linear_decrypt_asym_defaultKeyID_Params);
                llDecryptAsymDefaultKeyIDParams.setVisibility(View.GONE);
                LinearLayout llDecryptAsymEditKeyIDParams = view.findViewById(R.id.linear_decrypt_asym_editKeyID_params);
                llDecryptAsymEditKeyIDParams.setVisibility(View.GONE);
                LinearLayout llDecryptData = view.findViewById(R.id.linear_decrypt_data);
                llDecryptData.setVisibility(View.VISIBLE);
                LinearLayout llDecrypt = view.findViewById(R.id.linear_decrypt_radio_button_views);
                llDecrypt.setVisibility(View.VISIBLE);
                RadioGroup rgDecrypt = view.findViewById(R.id.crypto_radioGroup_decrypt_selection);
                RadioButton rbDecrypt = view.findViewById(R.id.crypto_decrypt_symm_radio);
                RadioButton rbAsymDecrypt = view.findViewById(R.id.crypto_decrypt_asym_radio);
                rbDecrypt.setChecked(true);
                EditText etDataIn = view.findViewById(R.id.et_decrypt_data);
                etDataIn.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_ENCRYPTION_CIPHER_OUT, ""));
                LinearLayout llDecryptParameters = view.findViewById(R.id.linear_decrypt_params);
                LinearLayout llDecryptAESDUKPTParams = view.findViewById(R.id.linear_decrypt_aes_dukpt_params);
                llDecryptAESDUKPTParams.setVisibility(View.GONE);
                LinearLayout llAsymDecryptParameters = view.findViewById(R.id.linear_decrypt_asymKeys_params);
                llAsymDecryptParameters.setVisibility(View.GONE);
                LinearLayout llAESDUKPTCipherType = view.findViewById(R.id.linear_decrypt_aes_dukpt_cipherType_Params);
                llAESDUKPTCipherType.setVisibility(View.GONE);
                LinearLayout llCipherType = view.findViewById(R.id.linear_decrypt_cipherType_params);
                llCipherType.setVisibility(View.VISIBLE);

                swDefaultKeyId.setOnCheckedChangeListener((buttonView, isChecked)-> {
                    if(isChecked) {
                        if(spnDefaultKeyID.getSelectedItem().toString().equals("DUKPT_AES_INDEX")) {
                            ArrayAdapter adapter = EnumUtils.getAESDUKPTCipherTypeArrayAdapter(context);
                            spnCipherType.setAdapter(adapter);
                            llDecryptAESDUKPTParams.setVisibility(View.VISIBLE);
                        } else {
                            ArrayAdapter adapter = EnumUtils.getOtherCipherTypeArrayAdapter(context);
                            spnCipherType.setAdapter(adapter);
                            llDecryptAESDUKPTParams.setVisibility(View.GONE);
                        }
                        if(rbDecrypt.isChecked()) {
                            llDecryptParameters.setVisibility(View.VISIBLE);
                            llAsymDecryptParameters.setVisibility(View.GONE);
                            llDefaultKeyIDParams.setVisibility(View.VISIBLE);
                            llEditKeyIDParams.setVisibility(View.GONE);
                            llDefaultKeyTypeParams.setVisibility(View.GONE);
                        }else if(rbAsymDecrypt.isChecked()) {
                            llDecryptAESDUKPTParams.setVisibility(View.GONE);
                            llAsymDecryptParameters.setVisibility(View.VISIBLE);
                            llDefaultKeyTypeParams.setVisibility(View.GONE);
                            llDefaultKeyIDParams.setVisibility(View.GONE);
                            llEditKeyIDParams.setVisibility(View.GONE);
                            llDecryptAsymDefaultKeyIDParams.setVisibility(View.VISIBLE);
                            llDecryptAsymEditKeyIDParams.setVisibility(View.GONE);
                            llDecryptParameters.setVisibility(View.GONE);
                        }
                    }else {
                        if(rbDecrypt.isChecked()) {
                            llDecryptAESDUKPTParams.setVisibility(View.GONE);
                            spnDefaultKeyID.setSelection(0);
                            llDecryptParameters.setVisibility(View.VISIBLE);
                            llAsymDecryptParameters.setVisibility(View.GONE);
                            llDefaultKeyIDParams.setVisibility(View.GONE);
                            llEditKeyIDParams.setVisibility(View.VISIBLE);
                            llDefaultKeyTypeParams.setVisibility(View.VISIBLE);
                        }else if(rbAsymDecrypt.isChecked()){
                            llDecryptAESDUKPTParams.setVisibility(View.GONE);
                            llAsymDecryptParameters.setVisibility(View.VISIBLE);
                            llDefaultKeyTypeParams.setVisibility(View.GONE);
                            llDefaultKeyIDParams.setVisibility(View.GONE);
                            llEditKeyIDParams.setVisibility(View.GONE);
                            llDecryptAsymEditKeyIDParams.setVisibility(View.VISIBLE);
                            llDecryptAsymDefaultKeyIDParams.setVisibility(View.GONE);
                            llDecryptParameters.setVisibility(View.GONE);
                        }
                    }

                });

                rgDecrypt.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if(checkedId == rbDecrypt.getId()) {
                            llDecryptParameters.setVisibility(View.VISIBLE);
                            llAsymDecryptParameters.setVisibility(View.GONE);
                            llDefaultKeyIDParams.setVisibility(View.VISIBLE);
                            llEditKeyIDParams.setVisibility(View.GONE);
                            llDefaultKeyTypeParams.setVisibility(View.GONE);
                            llDecryptAsymDefaultKeyIDParams.setVisibility(View.GONE);
                            etDataIn.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_ENCRYPTION_CIPHER_OUT, ""));
                        }

                        if(checkedId == rbAsymDecrypt.getId()) {
                            swDefaultKeyId.setChecked(true);
                            llDecryptAESDUKPTParams.setVisibility(View.GONE);
                            llAsymDecryptParameters.setVisibility(View.VISIBLE);
                            llDefaultKeyTypeParams.setVisibility(View.GONE);
                            llDefaultKeyIDParams.setVisibility(View.GONE);
                            llEditKeyIDParams.setVisibility(View.GONE);
                            llDecryptAsymDefaultKeyIDParams.setVisibility(View.VISIBLE);
                            llDecryptAsymEditKeyIDParams.setVisibility(View.GONE);
                            llDecryptParameters.setVisibility(View.GONE);
                            etDataIn.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_ASYM_ENCRYPTION_CIPHER_OUT, ""));
                        }
                    }
                });

                spnKeyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(!swDefaultKeyId.isChecked()) {
                            if(("AES".equals(spnKeyType.getSelectedItem().toString()) && "DUKPT".equals(spnKeyUsage.getSelectedItem().toString()))) {
                                llDecryptAESDUKPTParams.setVisibility(View.VISIBLE);
                                ArrayAdapter adapter = EnumUtils.getAESDUKPTCipherTypeArrayAdapter(context);
                                spnCipherType.setAdapter(adapter);
                                spnDerivateUsage.setSelection(7);
                                spnDerivateKeyLen.setSelection(1);
                                spnDerivateKeyType.setSelection(1);
                            }else {
                                llDecryptAESDUKPTParams.setVisibility(View.GONE);
                                ArrayAdapter adapter = EnumUtils.getOtherCipherTypeArrayAdapter(context);
                                spnCipherType.setAdapter(adapter);
                            }
                        }

                    }


                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                spnKeyUsage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(!swDefaultKeyId.isChecked()) {
                            if(("AES".equals(spnKeyType.getSelectedItem().toString()) && "DUKPT".equals(spnKeyUsage.getSelectedItem().toString()))  ) {
                                llDecryptAESDUKPTParams.setVisibility(View.VISIBLE);
                                ArrayAdapter adapter = EnumUtils.getAESDUKPTCipherTypeArrayAdapter(context);
                                spnCipherType.setAdapter(adapter);
                                spnDerivateUsage.setSelection(7);
                                spnDerivateKeyLen.setSelection(1);
                                spnDerivateKeyType.setSelection(1);
                            }else {
                                llDecryptAESDUKPTParams.setVisibility(View.GONE);
                                ArrayAdapter adapter = EnumUtils.getOtherCipherTypeArrayAdapter(context);;
                                spnCipherType.setAdapter(adapter);
                            }
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                spnDerivateKeyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.EXT_DECRYPT_DERIVATE_KEY_TYPE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int derivateKeyType = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.EXT_DECRYPT_DERIVATE_KEY_TYPE, 0);
                spnDerivateKeyType.setSelection(derivateKeyType);

                spnDerivateKeyLen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.EXT_DECRYPT_DERIVATE_KEY_LEN, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int derivateKeyLen = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.EXT_DECRYPT_DERIVATE_KEY_LEN, 0);
                spnDerivateKeyLen.setSelection(derivateKeyLen);

                spnDerivateUsage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.EXT_DECRYPT_DERIVATE_KEY_USAGE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int derivateKeyUsage = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.EXT_DECRYPT_DERIVATE_KEY_USAGE, 0);
                spnDerivateUsage.setSelection(derivateKeyUsage);

                spnCipherType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.EXT_DECRYPT_CIPHER_TYPE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int cipherType = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.EXT_DECRYPT_CIPHER_TYPE, 0);
                spnCipherType.setSelection(cipherType);

                spnPaddingMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.EXT_DECRYPT_PADDING_MODE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int paddingMode = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.EXT_DECRYPT_PADDING_MODE, 0);
                spnPaddingMode.setSelection(paddingMode);


                spnAsymKeyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.EXT_DECRYPT_ASYM_KEY_TYPE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int asymKeyType = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.EXT_DECRYPT_ASYM_KEY_TYPE, 0);
                spnAsymKeyType.setSelection(asymKeyType);

                spnAsymKeyUsage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.EXT_DECRYPT_ASYM_KEY_USAGE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int asymKeyUsage = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.EXT_DECRYPT_ASYM_KEY_USAGE, 0);
                spnAsymKeyUsage.setSelection(asymKeyUsage);

                spnMessageDigestType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.EXT_DECRYPT_ASYM_MESSAGE_DIGEST_TYPE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int messageDigestType = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.EXT_DECRYPT_ASYM_MESSAGE_DIGEST_TYPE, 0);
                spnMessageDigestType.setSelection(messageDigestType);

                spnAsymCryptoMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.EXT_DECRYPT_ASYM_CRYPTO_MODE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int asymCryptoMode = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.EXT_DECRYPT_ASYM_CRYPTO_MODE, 0);
                spnAsymCryptoMode.setSelection(asymCryptoMode);

                spnAsymEncodingMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.EXT_DECRYPT_ASYM_ENCODING_MODE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int asymEncodingMode = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.EXT_DECRYPT_ASYM_ENCODING_MODE, 0);
                spnAsymEncodingMode.setSelection(asymEncodingMode);

                spnAESDUKPTCipherType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.EXT_DECRYPT_AES_DUKPT_CIPHER_TYPE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int aesDUKPTCipherType = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.EXT_DECRYPT_AES_DUKPT_CIPHER_TYPE, 0);
                spnAESDUKPTCipherType.setSelection(aesDUKPTCipherType);


                EditText etDecryptKeyID = view.findViewById(R.id.et_decrypt_edit_keyID);
                etDecryptKeyID.setText(String.valueOf(sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.EXT_DECRYPT_KEY_ID, 0)));
                EditText etAsymKeyID = view.findViewById(R.id.et_asym_editKeyID);
                etAsymKeyID.setText(String.valueOf(sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.EXT_DECRYPT_ASYM_KEY_ID, 0)));

                spnDefaultKeyID.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (swDefaultKeyId.isChecked() && spnDefaultKeyID.getSelectedItem().toString().equals("DUKPT_AES_INDEX")) {
                            LogUtils.d("item", String.valueOf(swDefaultKeyId.isChecked()) + String.valueOf(spnDefaultKeyID.getSelectedItem().toString().equals("DUKPT_AES_INDEX")));
                            llDecryptAESDUKPTParams.setVisibility(View.VISIBLE);
                            ArrayAdapter adapter = EnumUtils.getAESDUKPTCipherTypeArrayAdapter(context);
                            spnCipherType.setAdapter(adapter);
                            spnDerivateUsage.setSelection(7);
                            spnDerivateKeyLen.setSelection(1);
                            spnDerivateKeyType.setSelection(1);
                            spnKeyUsage.setSelection(11);
                        } else {
                            llDecryptAESDUKPTParams.setVisibility(View.GONE);
                            ArrayAdapter adapter = EnumUtils.getOtherCipherTypeArrayAdapter(context);
                            spnCipherType.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onResult(int id, View view) {
                Switch swDefaultKeyID = view.findViewById(R.id.switch_default_key_id_btn);
                RadioButton rbDecrypt = view.findViewById(R.id.crypto_decrypt_symm_radio);
                RadioButton rbAsymDecrypt = view.findViewById(R.id.crypto_decrypt_asym_radio);
                Spinner spnDefaultKeyID = view.findViewById(R.id.spn_decrypt_default_keyID);
                Spinner spnKeyUsage = view.findViewById(R.id.spn_decrypt_keyUsage);
                Spinner spnDerivateKeyType = view.findViewById(R.id.spn_decrypt_dukpt_derivateKeyType);
                Spinner spnDerivateKeyLen = view.findViewById(R.id.spn_decrypt_dukpt_derivateKeyLen);
                Spinner spnDerivateKeyUsage = view.findViewById(R.id.spn_decrypt_dukpt_derivateKeyUsage);
                Spinner spnDerivateCipherType = view.findViewById(R.id.spn_decrypt_cipherType);
                Spinner spnDerivatePaddingMode = view.findViewById(R.id.spn_decrypt_paddingMode);
                Spinner spnAsymKeyType = view.findViewById(R.id.spn_decrypt_asym_keyType);
                Spinner spnAsymKeyUsage = view.findViewById(R.id.spn_decrypt_asym_keyUsage);
                Spinner spnMessageDigestType = view.findViewById(R.id.spn_decrypt_asym_messageDigestType);
                Spinner spnAsymCryptoMode = view.findViewById(R.id.spn_decrypt_asym_cryptoMode);
                Spinner spnAsymEncodingMode = view.findViewById(R.id.spn_decrypt_asym_encodingMode);
                Spinner spnAESDUKPTCipherType = view.findViewById(R.id.spn_decrypt_aes_dukpt_cipherType);
                Spinner spnDecryptAsymDefaultKeyID = view.findViewById(R.id.spn_decrypt_asym_defaultKeyID);
                LinearLayout llDecryptAESDUKPTParams = view.findViewById(R.id.linear_decrypt_aes_dukpt_params);

                byte asymDefaultKeyID = EnumUtils.getAsymDefaultKeyID(spnDecryptAsymDefaultKeyID.getSelectedItem().toString());
                KeyUsage decryptKeyUsage = EnumUtils.getKeyUsage(spnKeyUsage.getSelectedItem().toString());
                DUKPTDerivateUsage DUKPTDerivateUsage = EnumUtils.getDukptDerivateUsage(spnDerivateKeyUsage.getSelectedItem().toString());
                KeyType DUKPTDerivateKeyType = EnumUtils.getKeyType(spnDerivateKeyType.getSelectedItem().toString());
                int DUKPTDerivateLen = EnumUtils.getKeyLen(spnDerivateKeyLen.getSelectedItem().toString());
                CipherType decryptCipherType = EnumUtils.getCipherType(spnDerivateCipherType.getSelectedItem().toString());
                PaddingMode decryptPaddingMode = EnumUtils.getPaddingMode(spnDerivatePaddingMode.getSelectedItem().toString());
                AsymKeyUsage decryptAsymKeyUsage = EnumUtils.getAsymKeyUsage(spnAsymKeyUsage.getSelectedItem().toString());
                AsymKeyType decryptAsymKeyType = EnumUtils.getAsymKeyType(spnAsymKeyType.getSelectedItem().toString());
                MessageDigestType decryptMessageDigestType = EnumUtils.getMessageDigestType(spnMessageDigestType.getSelectedItem().toString());
                AsymCryptoMode decryptAsymCryptoMode = EnumUtils.getAsymCryptoMode(spnAsymCryptoMode.getSelectedItem().toString());
                AsymEncodingMode decryptAsymEncodingMode = EnumUtils.getAsymEncodingMode(spnAsymEncodingMode.getSelectedItem().toString());
                EditText etIVData = view.findViewById(R.id.et_decrypt_IV_data);
                decryptIVData = ISOUtils.hex2byte(etIVData.getText().toString());
                mEditor.putString(AppConfig.SharedPreferenceConfig.EXT_ENCRYPT_PADDING_DATA, etIVData.getText().toString());
                mEditor.commit();
                EditText etDataIn = view.findViewById(R.id.et_decrypt_data);
                cipherout = ISOUtils.hex2byte(etDataIn.getText().toString());
                EditText etKeyID = view.findViewById(R.id.et_decrypt_edit_keyID);
                int decryptKeyID = Integer.parseInt(etKeyID.getText().toString());
                mEditor.putInt(AppConfig.SharedPreferenceConfig.EXT_DECRYPT_KEY_ID, decryptKeyID);
                mEditor.commit();
                CipherOutput cipherOutput = null;

                if(rbDecrypt.isChecked()) {
                    if(swDefaultKeyID.isChecked()) {
                        byte decryptDefaultKeyID = EnumUtils.getDefaultKeyID(spnDefaultKeyID.getSelectedItem().toString());
                        SymmetricKey decryptKey = new SymmetricKey();
                        decryptKey.setKeyUsage(decryptKeyUsage);
                        decryptKey.setKeyID(decryptDefaultKeyID);
                        if(llDecryptAESDUKPTParams.getVisibility() == View.VISIBLE) {
                            DUKPTDerivateKey decryptDukptDerivateKey = new DUKPTDerivateKey();
                            decryptDukptDerivateKey.setKeyID(decryptDefaultKeyID);
                            decryptDukptDerivateKey.setKeyUsage(KeyUsage.DUKPT);
                            decryptDukptDerivateKey.setDerivateUsage(DUKPTDerivateUsage);
                            decryptDukptDerivateKey.setDerivateKeyType(DUKPTDerivateKeyType);
                            decryptDukptDerivateKey.setDerivateKeyLen(DUKPTDerivateLen);
                            try {
                                cipherOutput = extCrypto.decrypt(decryptDukptDerivateKey, decryptCipherType, decryptPaddingMode, decryptIVData, cipherout);
                                showMessage("DUKPTDerivateKey decrypt result: " + ISOUtils.hexString(cipherOutput.getData()));
                            }catch (NSDKException e) {
                                showErrorMessage(e, context.getString(R.string.tv_pin_aes_dukpt_decry));
                            }
                        }else {
                            try {
                                cipherOutput = extCrypto.decrypt(decryptKey, decryptCipherType, decryptPaddingMode, decryptIVData, cipherout);
                                showMessage("Decrypt result: " + ISOUtils.hexString(cipherOutput.getData()));
                            }catch (NSDKException e) {
                                showErrorMessage(e, context.getString(R.string.tv_pin_decrypt_test));
                            }
                        }
                    }else {
                        SymmetricKey decryptKey = new SymmetricKey();
                        decryptKey.setKeyUsage(decryptKeyUsage);
                        decryptKey.setKeyID((byte) decryptKeyID);
                        if(llDecryptAESDUKPTParams.getVisibility() == View.VISIBLE) {
                            DUKPTDerivateKey decryptDukptDerivateKey = new DUKPTDerivateKey();
                            decryptDukptDerivateKey.setKeyID((byte) decryptKeyID);
                            decryptDukptDerivateKey.setKeyUsage(KeyUsage.DUKPT);
                            decryptDukptDerivateKey.setDerivateUsage(DUKPTDerivateUsage);
                            decryptDukptDerivateKey.setDerivateKeyType(DUKPTDerivateKeyType);
                            decryptDukptDerivateKey.setDerivateKeyLen(DUKPTDerivateLen);
                            try {
                                cipherOutput = extCrypto.decrypt(decryptDukptDerivateKey, decryptCipherType, decryptPaddingMode, decryptIVData, cipherout);
                                showMessage("DUKPTDerivateKey decrypt result: " + ISOUtils.hexString(cipherOutput.getData()));
                            }catch (NSDKException e) {
                                showErrorMessage(e, context.getString(R.string.tv_pin_aes_dukpt_decry));
                            }
                        }else {
                            try {
                                cipherOutput = extCrypto.decrypt(decryptKey, decryptCipherType, decryptPaddingMode, decryptIVData, cipherout);
                                showMessage("Decrypt result: " + ISOUtils.hexString(cipherOutput.getData()));
                            }catch (NSDKException e) {
                                showErrorMessage(e, context.getString(R.string.tv_pin_decrypt_test));
                            }
                        }
                    }

                }else if(rbAsymDecrypt.isChecked()){
                    if(swDefaultKeyID.isChecked()) {
                        AsymmetricKey decryptAsymmetricKey = new AsymmetricKey();
                        decryptAsymmetricKey.setKeyType(decryptAsymKeyType);
                        decryptAsymmetricKey.setKeyID(asymDefaultKeyID);
                        decryptAsymmetricKey.setKeyUsage(decryptAsymKeyUsage);

                        AsymAlgorithmParameters decryptAsymAlgorithmParameters = new AsymAlgorithmParameters();
                        decryptAsymAlgorithmParameters.setCryptoMode(decryptAsymCryptoMode);
                        decryptAsymAlgorithmParameters.setEncodingMode(decryptAsymEncodingMode);
                        decryptAsymAlgorithmParameters.setMessageDigestType(decryptMessageDigestType);

                        byte[] decrypt_cipherout;
                        try {
                            decrypt_cipherout = extCrypto.decryptAsym(decryptAsymmetricKey, decryptAsymAlgorithmParameters, cipherout);
                            showMessage("Asym Decryption result: " + ISOUtils.hexString(decrypt_cipherout));
                        }catch (NSDKException e) {
                            showErrorMessage(e, context.getString(R.string.tv_pin_asym_decry));
                        }
                    }else {
                        EditText etAsymKeyID = view.findViewById(R.id.et_asym_editKeyID);
                        decryptAsymKeyID = Integer.parseInt(etAsymKeyID.getText().toString());
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.EXT_DECRYPT_ASYM_KEY_ID, decryptAsymKeyID);
                        mEditor.commit();
                        AsymmetricKey decryptAsymmetricKey = new AsymmetricKey();
                        decryptAsymmetricKey.setKeyType(decryptAsymKeyType);
                        decryptAsymmetricKey.setKeyID((byte) decryptAsymKeyID);
                        decryptAsymmetricKey.setKeyUsage(decryptAsymKeyUsage);

                        AsymAlgorithmParameters decryptAsymAlgorithmParameters = new AsymAlgorithmParameters();
                        decryptAsymAlgorithmParameters.setCryptoMode(decryptAsymCryptoMode);
                        decryptAsymAlgorithmParameters.setEncodingMode(decryptAsymEncodingMode);
                        decryptAsymAlgorithmParameters.setMessageDigestType(decryptMessageDigestType);

                        byte[] decrypt_cipherout;
                        try {
                            decrypt_cipherout = extCrypto.decryptAsym(decryptAsymmetricKey, decryptAsymAlgorithmParameters, cipherout);
                            showMessage("Asym Decryption result: " + ISOUtils.hexString(decrypt_cipherout));
                        }catch (NSDKException e) {
                            showErrorMessage(e, context.getString(R.string.tv_pin_asym_decry));
                        }
                    }

                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_cal_mac, functionid = INDEX_EXTPINPAD_CALCULATE_MAC)
    public void ExtCaculateMac() {
        DialogUtils.createCustomDialog(context, R.string.tv_pin_cal_mac, null, R.layout.dialog_crypto_caculate_mac, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                Switch swDefaultKeyID = view.findViewById(R.id.switch_default_key_id_btn);
                swDefaultKeyID.setChecked(true);
                LinearLayout llUseDefaultKeyIDParams = view.findViewById(R.id.linear_generate_mac_by_default_key_id_params);
                llUseDefaultKeyIDParams.setVisibility(View.VISIBLE);
                LinearLayout llUseDefaultKeyIDAESDUKPTParams = view.findViewById(R.id.linear_caculate_mac_default_key_id_aes_dukpt_params);
                llUseDefaultKeyIDAESDUKPTParams.setVisibility(View.GONE);
                LinearLayout llUseEditKeyIDAESDUKPTParams = view.findViewById(R.id.linear_caculate_mac_edit_key_id_aes_dukpt_params);
                llUseEditKeyIDAESDUKPTParams.setVisibility(View.GONE);


                LinearLayout llUseEditKeyIDParams = view.findViewById(R.id.linear_generate_mac_by_edit_key_params);
                llUseEditKeyIDParams.setVisibility(View.GONE);



                swDefaultKeyID.setOnCheckedChangeListener((buttonView, isChecked)-> {
                    if (isChecked) {
                        llUseDefaultKeyIDParams.setVisibility(View.VISIBLE);
                        llUseEditKeyIDParams.setVisibility(View.GONE);
                    } else {
                        llUseDefaultKeyIDParams.setVisibility(View.GONE);
                        llUseEditKeyIDParams.setVisibility(View.VISIBLE);
                    }
                });

            }

            @Override
            public void onResult(int id, View view) {
                Switch swDefaultKeyID = view.findViewById(R.id.switch_default_key_id_btn);
                Spinner spnDefaultKeyIDMacType = view.findViewById(R.id.spn_caculate_mac_default_key_id_macType);
                Spinner spnDefaultKeyIDDerivateKeyID = view.findViewById(R.id.spn_caculate_mac_default_keyID);
                Spinner spnEditKeyMacType = view.findViewById(R.id.spn_caculate_mac_edit_key_id_macType);
                EditText etIVData = view.findViewById(R.id.et_generate_mac_IV_data);
                byte[] iv = ISOUtils.hex2byte(etIVData.getText().toString());
                EditText etOriginData = view.findViewById(R.id.et_generate_mac_original_data);
                byte[] data = ISOUtils.hex2byte(etOriginData.getText().toString());
                MACOutput macOutput = null;
                EditText etKeyID = view.findViewById(R.id.et_caculate_mac_keyID);

                int editKeyID = Integer.parseInt(etKeyID.getText().toString());
                byte defaultKeyID = EnumUtils.getDefaultKeyID(spnDefaultKeyIDDerivateKeyID.getSelectedItem().toString());
                MACType defaultKeyIDMacType = EnumUtils.getMacType(spnDefaultKeyIDMacType.getSelectedItem().toString());
                MACType editKeyMacType = EnumUtils.getMacType(spnEditKeyMacType.getSelectedItem().toString());


                if (swDefaultKeyID.isChecked()) {

                    try {
                        macOutput = extCrypto.generateMAC(defaultKeyID, defaultKeyIDMacType, iv, data);
                        showMessage("MAC: " + ISOUtils.hexString(macOutput.getData()));
                    }catch (NSDKException e) {
                        showErrorMessage(e, context.getString(R.string.tv_pin_cal_mac));
                    }
                } else {
                    try {
                        macOutput = extCrypto.generateMAC((byte) editKeyID, editKeyMacType, iv, data);
                        showMessage("MAC: " + ISOUtils.hexString(macOutput.getData()));
                    }catch (NSDKException e) {
                        showErrorMessage(e, context.getString(R.string.tv_pin_cal_mac));
                    }
                }
            }
        });



    }
}
