package com.newland.nsdkdemo.internal.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.crypto.AlgorithmParameters;
import com.newland.nsdk.core.api.common.crypto.AsymAlgorithmParameters;
import com.newland.nsdk.core.api.common.crypto.AsymCryptoMode;
import com.newland.nsdk.core.api.common.crypto.AsymEncodingMode;
import com.newland.nsdk.core.api.common.crypto.KCVMode;
import com.newland.nsdk.core.api.common.crypto.MACType;
import com.newland.nsdk.core.api.common.crypto.MessageDigestType;
import com.newland.nsdk.core.api.common.crypto.PaddingMode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.keymanager.AsymAlgInfo;
import com.newland.nsdk.core.api.common.keymanager.AsymKeyType;
import com.newland.nsdk.core.api.common.keymanager.AsymKeyUsage;
import com.newland.nsdk.core.api.common.keymanager.AsymmetricKey;
import com.newland.nsdk.core.api.common.keymanager.CipherMode;
import com.newland.nsdk.core.api.common.keymanager.DUKPTKey;
import com.newland.nsdk.core.api.common.keymanager.ExportMode;
import com.newland.nsdk.core.api.common.keymanager.KDFInfo;
import com.newland.nsdk.core.api.common.keymanager.KDFType;
import com.newland.nsdk.core.api.common.keymanager.KeyGenerateMethod;
import com.newland.nsdk.core.api.common.keymanager.KeyInfoID;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.api.common.keymanager.KeyUsage;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.internal.crypto.Crypto;
import com.newland.nsdk.core.api.internal.keymanager.KeyManager;
import com.newland.nsdk.core.api.internal.keymanager.MACVerifyParameters;
import com.newland.nsdk.core.common.NSDKExecutors;
import com.newland.nsdk.core.common.keymanager.ST_SEC_ASYM_ALG_INFO;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.AppConfig;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.annotation.MethodGridEntity;
import com.newland.nsdkdemo.common.utils.DialogUtils;
import com.newland.nsdkdemo.common.utils.EnumUtils;
import com.newland.nsdkdemo.common.utils.FileUtils;
import com.newland.nsdkdemo.common.utils.MessageTag;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class KeyManagerFragment extends InternalBaseFragment {

    private static final String TAG = "KeyManagerFragment";
    private KeyManager mKeyManager;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor mEditor;
    //Load Symmetric KEK Keys Params
    private int load_symm_kek_Flag = -1;
    private String KEKData;

    //Load Symmetric Keys By KEK Params
    private String KEYData;

    //Load Asymmetric Keys Params
    private int loadAsymFlag = -1;
    private int loadAsymmetricKeyID;
    private byte[] loadAsymmetricDatain;
    private int loadAsymmetricSessionKeyFlag = -1;
//    SymmetricKey sourceKey = new SymmetricKey();



    public KeyManagerFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_keyManager_f);
    }

    @Override
    public void initData() {
        mKeyManager = (KeyManager) moduleManager.getModule(ModuleType.KEY_MANAGER);
        sharedPreferences = context.getSharedPreferences("KEK Params", context.MODE_PRIVATE);
        mEditor = sharedPreferences.edit();

    }

    @Override
    public Object getModule() {
        return KeyManagerFragment.this;
    }


    @MethodGridEntity(btnnameid = R.string.tv_pin_load_des_wk, functionid = 1)
    public void loadDESKeys() {
        SymmetricKey desKey = new SymmetricKey();
        byte[] keyBuffer;

        desKey.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_MK);
        desKey.setKeyType(KeyType.DES);
        desKey.setKeyUsage(KeyUsage.KEK);

        desKey.setKeyLen(24);
        keyBuffer = ISOUtils.hex2byte("000000000000000000000000000000000000000000000001");
        desKey.setKeyData(keyBuffer);
        desKey.setKCVMode(KCVMode.NONE);

        AlgorithmParameters algorithmParameters = new AlgorithmParameters();
        algorithmParameters.setCipherMode(CipherMode.ECB);

        try {
            // KeyGenerateMethod.CLEAR is used for DEV device, for PRO device, please use master POS or RKL to load master key.
            mKeyManager.generateKey(KeyGenerateMethod.CLEAR, algorithmParameters, null, desKey);
            showMessage("DES KEK is generated successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            if (e.getCode() != ErrorCode.SEC_CFG_CLEARKEY_LIMIT) {
                showErrorMessage(e, "load DES KEK key");
                return;
            }
        }

        SymmetricKey sourceKey = new SymmetricKey();
        sourceKey.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_MK);
        sourceKey.setKeyType(KeyType.DES);
        sourceKey.setKeyUsage(KeyUsage.KEK);

        desKey = new SymmetricKey();
        desKey.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_WK_DATA);
        desKey.setKeyType(KeyType.DES);
        desKey.setKeyUsage(KeyUsage.DATA);
        desKey.setKeyLen(16);
        desKey.setKeyData(ISOUtils.hex2byte("253C9D9D7C2FBBFA253C9D9D7C2FBBFA"));
        desKey.setKCVMode(KCVMode.NONE);

        try {
            mKeyManager.generateKey(KeyGenerateMethod.CIPHER, algorithmParameters, sourceKey, desKey);
            showMessage("Data key loaded with DES KEK successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "load Data key with DES KEK.");
        }

        desKey.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_WK_PIN);
        desKey.setKeyData(ISOUtils.hex2byte("F679786E2411E3DEF679786E2411E3DE"));
        desKey.setKeyUsage(KeyUsage.PIN);
        try {
            mKeyManager.generateKey(KeyGenerateMethod.CIPHER, algorithmParameters, sourceKey, desKey);
            showMessage("PIN key loaded with DES KEK successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "load PIN key with DES KEK.");
        }

        desKey.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_WK_MAC);
        desKey.setKeyData(ISOUtils.hex2byte("575CD3E1CA1449D3575CD3E1CA1449D3"));
        desKey.setKeyUsage(KeyUsage.MAC);
        desKey.setKCVMode(KCVMode.ZERO);
        desKey.setKCV(ISOUtils.hex2byte("A8B7B5"));
        try {
            mKeyManager.generateKey(KeyGenerateMethod.CIPHER, algorithmParameters, sourceKey, desKey);
            showMessage("MAC key loaded with DES KEK successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "load MAC key with DES KEK.");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_load_aes_wk, functionid = 2)
    public void loadAESKeys() {
        SymmetricKey desKey = new SymmetricKey();
        AlgorithmParameters algorithmParameters = new AlgorithmParameters();

        desKey.setKeyID(AppConfig.Keys.MKSK_AES_INDEX_MK);
        desKey.setKeyType(KeyType.AES);
        desKey.setKeyUsage(KeyUsage.KEK);
        desKey.setKeyLen(32);
        byte[] keyBuffer = ISOUtils.hex2byte("0000000000000000000000000000000000000000000000000000000000000005");
        desKey.setKeyData(keyBuffer);
        desKey.setKCVMode(KCVMode.NONE);

        try {
            mKeyManager.generateKey(KeyGenerateMethod.CLEAR, algorithmParameters, null, desKey);
            showMessage("AES KEK is generated successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            if (e.getCode() != ErrorCode.SEC_CFG_CLEARKEY_LIMIT) {
                showErrorMessage(e, "load AES KEK key");
                return;
            }
        }

        SymmetricKey sourceKey = new SymmetricKey();
        sourceKey.setKeyID(AppConfig.Keys.MKSK_AES_INDEX_MK);
        sourceKey.setKeyType(KeyType.AES);
        sourceKey.setKeyUsage(KeyUsage.KEK);

        desKey = new SymmetricKey();
        desKey.setKeyID(AppConfig.Keys.MKSK_AES_INDEX_WK_DATA);
        desKey.setKeyType(KeyType.AES);
        desKey.setKeyUsage(KeyUsage.DATA);
        desKey.setKeyLen(32);
        desKey.setKeyData(ISOUtils.hex2byte("7B3F982CB88BE0279D1262272321B90E7B3F982CB88BE0279D1262272321B90E"));
        desKey.setKCVMode(KCVMode.ZERO);
        desKey.setKCV(ISOUtils.hex2byte("8B7575B780"));

        try {
            mKeyManager.generateKey(KeyGenerateMethod.CIPHER, sourceKey, desKey);
            showMessage("Data key loaded with AES KEK successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "load Data key with AES KEK.");
        }

        desKey.setKeyID(AppConfig.Keys.MKSK_AES_INDEX_WK_MAC);
        desKey.setKeyLen(24);
        desKey.setKeyData(ISOUtils.hex2byte("11514B0CF9B50E295BBEBB95D2A9E68EB61EBA671777EF2C0D7A73236674AF29"));
        desKey.setKeyUsage(KeyUsage.MAC);
        desKey.setKCVMode(KCVMode.ZERO);
        desKey.setKCV(ISOUtils.hex2byte("5E15B0642E"));
        try {
            mKeyManager.generateKey(KeyGenerateMethod.CIPHER, sourceKey, desKey);
            showMessage("MAC key loaded with AES KEK successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "load MAC key with AES KEK.");
        }

        desKey.setKeyID(AppConfig.Keys.MKSK_AES_INDEX_WK_PIN);
        desKey.setKeyLen(16);
        desKey.setKeyData(ISOUtils.hex2byte("544DB7FEF3FE3F0B998EF40A54A77376"));
        desKey.setKeyUsage(KeyUsage.PIN);
        desKey.setKCVMode(KCVMode.NONE);
        try {
            mKeyManager.generateKey(KeyGenerateMethod.CIPHER, sourceKey, desKey);
            showMessage("PIN key loaded with AES KEK successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "load PIN key with AES KEK.");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_load_des_tr31, functionid = 3)
    private void loadDESTR31Key(){
        String masterKey = "F0000000000000000000000000000000000000000000000F";
        String keyData = "B0088D0TB00E0100KS080002A36E15E00A9B737DB7B58B35289CE708D8D3E5695F2590E992748BF8A982C6E9";

        SymmetricKey srcKey = new SymmetricKey();
        srcKey.setKeyID(AppConfig.Keys.TR31_KEK);
        srcKey.setKeyUsage(KeyUsage.TR31_KEK);
        srcKey.setKeyType(KeyType.DES);
        srcKey.setKCVMode(KCVMode.NONE);
        srcKey.setKeyLen(24);
        srcKey.setKeyData(ISOUtils.hex2byte(masterKey));

        SymmetricKey desKey = new SymmetricKey();
        desKey.setKeyID(AppConfig.Keys.TR31_KEY);
        desKey.setKeyType(KeyType.DES);
        desKey.setKeyUsage(KeyUsage.DATA);
        desKey.setKeyData(keyData.getBytes());
        desKey.setKeyLen(88);
        desKey.setKCVMode(KCVMode.ZERO);
        desKey.setKCV(ISOUtils.hex2byte("E2F243"));

        try {
            mKeyManager.generateKey(KeyGenerateMethod.CLEAR, null, null, srcKey, null);
            showMessage("DES TR31 Master Key generated successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            if (e.getCode() != ErrorCode.SEC_CFG_CLEARKEY_LIMIT) {
                showErrorMessage(e, "generate DES TR31 master key");
                return;
            }
        }

        try {
            mKeyManager.generateKey(KeyGenerateMethod.TR31,null, srcKey, desKey, null);
            showMessage("DES TR31 Key generated successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "generate DES TR31 key");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_load_aes_tr31, functionid = 4)
    private void loadAESTR31Key(){
        String masterKey = "FFF1000000000000000000000000000000000000000000000000000000001FFF";
        String keyData = "D0112P0AE00N00003D2DA5771F95A8BD2010664C22C9F82800BA73D34C5C5B5FED6FDBD36FA27EBAE428BD068DC7B315D8B3A59F61D05905";

        SymmetricKey srcKey = new SymmetricKey();
        srcKey.setKeyID(AppConfig.Keys.TR31_KEK);
        srcKey.setKeyUsage(KeyUsage.TR31_KEK);
        srcKey.setKeyType(KeyType.AES);
        srcKey.setKCVMode(KCVMode.NONE);
        srcKey.setKeyLen(32);
        srcKey.setKeyData(ISOUtils.hex2byte(masterKey));

        SymmetricKey desKey = new SymmetricKey();
        desKey.setKeyID(AppConfig.Keys.TR31_KEY);
        desKey.setKeyType(KeyType.AES);
        desKey.setKeyUsage(KeyUsage.PIN);
        desKey.setKeyData(keyData.getBytes());
        desKey.setKeyLen(112);
        desKey.setKCVMode(KCVMode.NONE);

        try {
            mKeyManager.generateKey(KeyGenerateMethod.CLEAR, null, null, srcKey, null);
            showMessage("AES TR31 Master Key generated successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            if (e.getCode() != ErrorCode.SEC_CFG_CLEARKEY_LIMIT) {
                showErrorMessage(e, "generate AES TR31 master Key");
                return;
            }
        }

        try {
            mKeyManager.generateKey(KeyGenerateMethod.TR31,null, srcKey, desKey, null);
            showMessage("AES TR31 Key generated successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "generate AES TR31 Key");
        }
    }

    @MethodGridEntity(btnnameid = R.string.dialog_tv_keymanager_get_keyinfo, functionid = 5)
    public void getkeyInfo() {
        DialogUtils.createCustomDialog(context, R.string.tv_pin_getkcv, null, R.layout.dialog_keymanager_get_keyinfo, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                Switch swDefaultKeyID = view.findViewById(R.id.switch_default_key_id_btn);
                LinearLayout llGetDefaultKeyIDParams = view.findViewById(R.id.linear_keymanager_getKeyInfo_defaultKeyID_params);
                llGetDefaultKeyIDParams.setVisibility(View.VISIBLE);
                LinearLayout llGetEditKeyIDParams = view.findViewById(R.id.linear_keymanager_getKeyInfo_editKeyID_params);
                llGetEditKeyIDParams.setVisibility(View.GONE);
                RadioButton rbGetKcv = view.findViewById(R.id.keymanager_get_KCV_radio);
                rbGetKcv.setChecked(true);
                LinearLayout llGetKcvParams = view.findViewById(R.id.linear_keymanager_get_kcv_params);
                llGetKcvParams.setVisibility(View.VISIBLE);
                swDefaultKeyID.setChecked(true);
                swDefaultKeyID.setOnCheckedChangeListener(((buttonView, isChecked) -> {
                    if (isChecked) {
                        llGetDefaultKeyIDParams.setVisibility(View.VISIBLE);
                        llGetEditKeyIDParams.setVisibility(View.GONE);

                    } else {
                        llGetDefaultKeyIDParams.setVisibility(View.GONE);
                        llGetEditKeyIDParams.setVisibility(View.VISIBLE);
                    }
                }));

                EditText etKeyID = view.findViewById(R.id.et_keymanager_getKeyInfo_keyID);
                etKeyID.setText(String.valueOf(sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_GET_KEYINFO_KEY_ID, 0)));

                Spinner spnKeyType = view.findViewById(R.id.spn_keymanager_getKeyInfo_keyType);
                Spinner spnKeyUsage = view.findViewById(R.id.spn_keymanager_getKeyInfo_keyUsage);
                spnKeyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_GET_KEYINFO_KEY_TYPE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int keyType = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_GET_KEYINFO_KEY_TYPE, 0);
                spnKeyType.setSelection(keyType);

                spnKeyUsage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_GET_KEYINFO_KEY_USAGE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int keyUsage = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_GET_KEYINFO_KEY_USAGE, 0);
                spnKeyUsage.setSelection(keyUsage);
            }

            @Override
            public void onResult(int id, View view) {
                Switch swDefaultKeyID = view.findViewById(R.id.switch_default_key_id_btn);
                Spinner spnDefaultKeyID = view.findViewById(R.id.spn_keymanager_getKeyInfo_defaultKeyID);
                EditText etKeyID = view.findViewById(R.id.et_keymanager_getKeyInfo_keyID);
                int getKeyInfoKeyID = Integer.parseInt(etKeyID.getText().toString());
                mEditor.putInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_GET_KEYINFO_KEY_ID, getKeyInfoKeyID);
                mEditor.commit();
                Spinner spnKeyType = view.findViewById(R.id.spn_keymanager_getKeyInfo_keyType);
                Spinner spnKeyUsage = view.findViewById(R.id.spn_keymanager_getKeyInfo_keyUsage);

                byte defaultKeyID = EnumUtils.getDefaultKeyID(spnDefaultKeyID.getSelectedItem().toString());
                KeyType keyType = EnumUtils.getKeyType(spnKeyType.getSelectedItem().toString());
                AsymKeyType asymKeyType = EnumUtils.getAsymKeyType(spnKeyType.getSelectedItem().toString());
                KeyUsage keyUsage = EnumUtils.getKeyUsage(spnKeyUsage.getSelectedItem().toString());
                AsymKeyUsage asymKeyUsage = EnumUtils.getAsymKeyUsage(spnKeyUsage.getSelectedItem().toString());

                RadioButton rbGetKcv = view.findViewById(R.id.keymanager_get_KCV_radio);
                RadioButton rbGetKsn = view.findViewById(R.id.keymanager_get_KSN_radio);
                if(rbGetKcv.isChecked()) {
                    if(swDefaultKeyID.isChecked()) {
                        if (spnKeyType.getSelectedItem().toString().contains("ASYM")) {
                            AsymmetricKey asDesKey = new AsymmetricKey();
                            asDesKey.setKeyID(defaultKeyID);
                            asDesKey.setKeyUsage(asymKeyUsage);
                            asDesKey.setKeyType(asymKeyType);
                            byte[] result = null;
                            try {
                                result = mKeyManager.getKeyInfo(KeyInfoID.KCV, asDesKey);
                            } catch (NSDKException e) {
                                showErrorMessage(e, context.getString(R.string.tv_pin_getkcv));
                            }
                            String kcvStr = result == null ? "null" : ISOUtils.hexString(result);
                            LogUtils.d(TAG, "getKeyInfo kcv=" + kcvStr);
                            showMessage("Get KCV = " + kcvStr);
                        } else {
                            SymmetricKey desKey = new SymmetricKey();
                            desKey.setKeyID(defaultKeyID);

                            desKey.setKeyType(keyType);
                            desKey.setKeyUsage(keyUsage);

                            byte[] result;
                            try {
                                result = mKeyManager.getKeyInfo(KeyInfoID.KCV, desKey);
                                String kcvStr = result == null ? "null" : ISOUtils.hexString(result);
                                showMessage("Get KCV = " + kcvStr);
                            } catch (NSDKException e) {
                                e.printStackTrace();
                                showErrorMessage(e, context.getString(R.string.tv_pin_getkcv));
                            }
                        }
                    }else {
                        if (spnKeyType.getSelectedItem().toString().contains("ASYM")) {
                            AsymmetricKey asDesKey = new AsymmetricKey();
                            asDesKey.setKeyID((byte) getKeyInfoKeyID);
                            asDesKey.setKeyUsage(asymKeyUsage);
                            asDesKey.setKeyType(asymKeyType);
                            byte[] result = null;
                            try {
                                result = mKeyManager.getKeyInfo(KeyInfoID.KCV, asDesKey);
                            } catch (NSDKException e) {
                                showErrorMessage(e, context.getString(R.string.tv_pin_getkcv));
                            }
                            String kcvStr = result == null ? "null" : ISOUtils.hexString(result);
                            LogUtils.d(TAG, "getKeyInfo kcv=" + kcvStr);
                            showMessage("Get KCV = " + kcvStr);
                        } else {
                            SymmetricKey desKey = new SymmetricKey();
                            desKey.setKeyID((byte) getKeyInfoKeyID);

                            desKey.setKeyType(keyType);
                            desKey.setKeyUsage(keyUsage);

                            byte[] result = null;
                            try {
                                result = mKeyManager.getKeyInfo(KeyInfoID.KCV, desKey);
                                String kcvStr = result == null ? "null" : ISOUtils.hexString(result);
                                showMessage("Get KCV = " + kcvStr);
                            } catch (NSDKException e) {
                                e.printStackTrace();
                                showErrorMessage(e, context.getString(R.string.tv_pin_getkcv));
                            }
                        }
                    }


                }else if(rbGetKsn.isChecked()) {
                    if (swDefaultKeyID.isChecked()) {
                        SymmetricKey desKey = new SymmetricKey();
                        desKey.setKeyUsage(keyUsage);
                        desKey.setKeyType(keyType);
                        desKey.setKeyID(defaultKeyID);

                        try {
                            byte[] ksn = mKeyManager.getKeyInfo(KeyInfoID.KSN, desKey);
                            if(spnKeyType.getSelectedItem().toString().contains("AES")) {
                                showMessage("AES DUKPT KSN:" + ISOUtils.hexString(ksn));
                            }else {
                                showMessage("DUKPT KSN:" + ISOUtils.hexString(ksn));
                            }
                        }catch (NSDKException e) {
                            showErrorMessage(e, context.getString(R.string.dialog_tv_keymanager_get_keyinfo));
                        }
                    } else {
                        SymmetricKey desKey = new SymmetricKey();
                        desKey.setKeyUsage(keyUsage);
                        desKey.setKeyType(keyType);
                        desKey.setKeyID((byte)getKeyInfoKeyID);

                        try {
                            byte[] ksn = mKeyManager.getKeyInfo(KeyInfoID.KSN, desKey);
                            if(spnKeyType.getSelectedItem().toString().contains("AES")) {
                                showMessage("AES DUKPT KSN:" + ISOUtils.hexString(ksn));
                            }else {
                                showMessage("DUKPT KSN:" + ISOUtils.hexString(ksn));
                            }
                        }catch (NSDKException e) {
                            showErrorMessage(e, context.getString(R.string.dialog_tv_keymanager_get_keyinfo));
                        }
                    }

                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_delete_key, functionid = 7)
    public void deleteKey() {
        DialogUtils.createCustomDialog(context, R.string.tv_pin_delete_key, null, R.layout.dialog_keymanager_delete_key, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                Switch swDefaultKeyID = view.findViewById(R.id.switch_default_key_id_btn);
                swDefaultKeyID.setChecked(true);
                LinearLayout llDeleteDefaultKeyIDParams = view.findViewById(R.id.linear_keymanager_delete_defaultKeyID_params);
                llDeleteDefaultKeyIDParams.setVisibility(View.VISIBLE);
                LinearLayout llDeleteEditKeyIDParmas = view.findViewById(R.id.linear_keymanager_delete_editKeyID_params);
                llDeleteEditKeyIDParmas.setVisibility(View.GONE);
                swDefaultKeyID.setOnCheckedChangeListener(((buttonView, isChecked) -> {
                    if(isChecked) {
                        llDeleteDefaultKeyIDParams.setVisibility(View.VISIBLE);
                        llDeleteEditKeyIDParmas.setVisibility(View.GONE);
                    }else {
                        llDeleteDefaultKeyIDParams.setVisibility(View.GONE);
                        llDeleteEditKeyIDParmas.setVisibility(View.VISIBLE);
                    }
                }));

                Spinner spnKeyType = view.findViewById(R.id.spn_keymanager_delete_key_keyType);
                spnKeyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_DELETE_KEYS_KEY_TYPE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int keyType = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_DELETE_KEYS_KEY_TYPE, 0);
                spnKeyType.setSelection(keyType);

                Spinner spnKeyUsage = view.findViewById(R.id.spn_keymanager_delete_key_keyUsage);
                spnKeyUsage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_DELETE_KEYS_KEY_USAGE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int keyUsage = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_DELETE_KEYS_KEY_USAGE, 0);
                spnKeyUsage.setSelection(keyUsage);

                EditText etKeyID = view.findViewById(R.id.et_keymanager_key_index);
                etKeyID.setText(String.valueOf(sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_DELETE_KEYS_KEY_ID, 0)));

            }

            @Override
            public void onResult(int id, View view) {
                Switch swDefaultKeyID = view.findViewById(R.id.switch_default_key_id_btn);
                Spinner spnDeleteDefaultKeyID = view.findViewById(R.id.spn_keymanager_delete_defaultKeyID);
                Spinner spnKeyType = view.findViewById(R.id.spn_keymanager_delete_key_keyType);
                Spinner spnKeyUsage = view.findViewById(R.id.spn_keymanager_delete_key_keyUsage);
                EditText etKeyID = view.findViewById(R.id.et_keymanager_key_index);
                byte defaultKeyID;
                if(spnDeleteDefaultKeyID.getSelectedItem().toString().contains("ASYM")) {
                    defaultKeyID  =EnumUtils.getAsymDefaultKeyID(spnDeleteDefaultKeyID.getSelectedItem().toString());
                }else {
                    defaultKeyID = EnumUtils.getDefaultKeyID(spnDeleteDefaultKeyID.getSelectedItem().toString());
                }
                AsymKeyUsage asymKeyUsage = null;
                AsymKeyType asymKeyType = null;

                KeyType keyType= EnumUtils.getKeyType(spnKeyType.getSelectedItem().toString());
                KeyUsage keyUsage = EnumUtils.getKeyUsage(spnKeyUsage.getSelectedItem().toString());
                if(keyType == null) {
                    asymKeyType = EnumUtils.getAsymKeyType(spnKeyType.getSelectedItem().toString());
                }
                if(keyUsage == null) {
                    asymKeyUsage = EnumUtils.getAsymKeyUsage(spnKeyUsage.getSelectedItem().toString());
                }

                if(swDefaultKeyID.isChecked()) {
                    if(keyUsage == null || keyType == null) {

                        AsymmetricKey asymmetricKey = new AsymmetricKey();
                        asymmetricKey.setKeyID(defaultKeyID);
                        asymmetricKey.setKeyType(asymKeyType);
                        asymmetricKey.setKeyUsage(asymKeyUsage);

                        try {
                            mKeyManager.deleteKey(asymmetricKey);
                            showMessage(String.format("Delete %s %s Key Success.", spnKeyType.getSelectedItem().toString(), spnKeyUsage.getSelectedItem().toString()));
                        }catch (NSDKException e) {
                            showErrorMessage(e, context.getString(R.string.tv_pin_delete_key));
                        }
                    }else {
                        SymmetricKey key = new SymmetricKey();
                        key.setKeyID(defaultKeyID);
                        key.setKeyType(keyType);
                        key.setKeyUsage(keyUsage);

                        try {
                            mKeyManager.deleteKey(key);
                            showMessage(String.format("Delete %s %s Key Success.", spnKeyType.getSelectedItem().toString(), spnKeyUsage.getSelectedItem().toString()));
                        }catch (NSDKException e) {
                            showErrorMessage(e, context.getString(R.string.tv_pin_delete_key));
                        }
                    }

                }else {
                    int keyID = Integer.parseInt(etKeyID.getText().toString());
                    mEditor.putInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_DELETE_KEYS_KEY_ID, keyID);
                    mEditor.commit();
                    if(keyUsage == null || keyType == null) {
                        AsymmetricKey asymmetricKey = new AsymmetricKey();
                        asymmetricKey.setKeyID((byte)keyID);
                        asymmetricKey.setKeyUsage(asymKeyUsage);
                        asymmetricKey.setKeyType(asymKeyType);

                        try {
                            mKeyManager.deleteKey(asymmetricKey);
                            showMessage(String.format("Delete %s %s Key Success.", spnKeyType.getSelectedItem().toString(), spnKeyUsage.getSelectedItem().toString()));
                        }catch (NSDKException e) {
                            showErrorMessage(e, context.getString(R.string.tv_pin_delete_key));
                        }
                    }else {
                        SymmetricKey key = new SymmetricKey();
                        key.setKeyID((byte) keyID);
                        key.setKeyType(keyType);
                        key.setKeyUsage(keyUsage);

                        try {
                            mKeyManager.deleteKey(key);
                            showMessage(String.format("Delete %s %s Key Success.", spnKeyType.getSelectedItem().toString(), spnKeyUsage.getSelectedItem().toString()));
                        }catch (NSDKException e) {
                            showErrorMessage(e, context.getString(R.string.tv_pin_delete_key));
                        }
                    }
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_load_dukpt, functionid = 8)
    private void secKIMCipherDUKPTTest() {
        int result = -1;
        SymmetricKey sourceKey = new SymmetricKey();
        DUKPTKey desKey = new DUKPTKey();
        byte[] kcvValue = new byte[3];
        byte[] ksnBuffer = new byte[10];
        AlgorithmParameters algorithmParameters = new AlgorithmParameters();

        sourceKey.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_MK);
        sourceKey.setKeyType(KeyType.DES);
        sourceKey.setKeyUsage(KeyUsage.KEK);

        algorithmParameters.setCipherMode(CipherMode.ECB);

        desKey.setKeyID(AppConfig.Keys.DUKPT_DES_INDEX);
        desKey.setKeyType(KeyType.DES);
        desKey.setKeyUsage(KeyUsage.DUKPT);
        desKey.setKeyLen(16);

        desKey.setKeyData(ISOUtils.hex2byte("4DE2C2838D2990C94DE2C2838D2990C9"));
        desKey.setKCVMode(KCVMode.NONE);
        Arrays.fill(ksnBuffer, (byte) 0x0);
        desKey.setKSN(ksnBuffer);

        try {
            mKeyManager.generateKey(KeyGenerateMethod.CIPHER,
                    algorithmParameters, sourceKey, desKey);
            result = 0;
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, context.getString(R.string.tv_pin_load_dukpt));
            result = e.getCode();
        }
        if (result < 0) {
            showMessage("Failed to load DUKPT key, result: " + result);
            return;
        }
        showMessage("DUKPT key loaded successfully.");
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_load_aes_dukpt, functionid = 9)
    private void loadAESDUKPTKey() {
        SymmetricKey sourceKey = new SymmetricKey();
        DUKPTKey desKey = new DUKPTKey();
        byte[] ksnBuffer = new byte[12];
        AlgorithmParameters algorithmParameters = new AlgorithmParameters();

        sourceKey.setKeyID(AppConfig.Keys.MKSK_AES_INDEX_MK);
        sourceKey.setKeyType(KeyType.AES);
        sourceKey.setKeyUsage(KeyUsage.KEK);

        algorithmParameters.setCipherMode(CipherMode.ECB);

        desKey.setKeyID(AppConfig.Keys.DUKPT_AES_INDEX);
        desKey.setKeyType(KeyType.AES);
        desKey.setKeyUsage(KeyUsage.DUKPT);
        desKey.setKeyLen(16);

        desKey.setKeyData(ISOUtils.hex2byte("466ABA521EFDA3C6FFC40494E4E9138F"));
        desKey.setKCVMode(KCVMode.NONE);
        Arrays.fill(ksnBuffer, (byte) 0x0);
        desKey.setKSN(ksnBuffer);

        try {
            mKeyManager.generateKey(KeyGenerateMethod.CIPHER,
                    algorithmParameters, sourceKey, desKey);
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "load AES DUKPT key");
            return;
        }
        showMessage("AES DUKPT key loaded successfully.");
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_aes_dukpt_derive, functionid = 10)
    private void deriveAESDUKPTKey() {
        SymmetricKey desKey = new SymmetricKey();
        AlgorithmParameters algorithmParameters = new AlgorithmParameters();

        algorithmParameters.setCipherMode(CipherMode.ECB);

        desKey.setKeyID(AppConfig.Keys.DUKPT_AES_INDEX);
        desKey.setKeyType(KeyType.AES);
        desKey.setKeyUsage(KeyUsage.DUKPT);
        desKey.setKeyLen(16);

        desKey.setKCVMode(KCVMode.NONE);

        try {
            byte[] ksn = mKeyManager.getKeyInfo(KeyInfoID.KSN, desKey);
            if (null == ksn) {
                showMessage("AES DUKPT key shall be loaded first.", MessageTag.ERROR);
                return;
            }
            showMessage(String.format("Before derivation, ksn: %s", ISOUtils.hexString(ksn)));
            mKeyManager.generateKey(KeyGenerateMethod.DUKPT_DERIVE,
                    algorithmParameters, null, desKey);
            ksn = mKeyManager.getKeyInfo(KeyInfoID.KSN, desKey);
            if (ksn == null) {
                showMessage("Failed to get ksn after derivation.", MessageTag.ERROR);
                return;
            }
            showMessage(String.format("After derivation, ksn: %s", ISOUtils.hexString(ksn)));
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "derive AES DUKPT key");
            return;
        }

        showMessage("AES DUKPT key derived successfully.");
    }
    @MethodGridEntity(btnnameid = R.string.tv_pin_load_giske_key, functionid = 11)
    private void loadGiskeKey() {
        String pinKeyData = "A0120P0T484426ABF5161D756E98E73E66253E509DBE0A3F6F9586DD1441FB12C2DD1A9948F8D38F2A5D357EEF6CC6EBBC5FDC06F4C18AD52E539342";
        //GISKE TMK installed in KeyID:15
        SymmetricKey srcKey = new SymmetricKey();
        srcKey.setKeyID((byte) 15);
        srcKey.setKeyType(KeyType.DES);
        srcKey.setKeyUsage(KeyUsage.KEK);
        //GISKE Pin Key to be installed in KeyID: 16
        SymmetricKey dstKey = new SymmetricKey();
        dstKey.setKeyID((byte) 16);
        dstKey.setKeyType(KeyType.DES);
        dstKey.setKeyUsage(KeyUsage.PIN);
        dstKey.setKeyData(pinKeyData.getBytes());
        dstKey.setKeyLen(120);
        try {
            mKeyManager.generateKey(KeyGenerateMethod.GISKE, srcKey, dstKey);
            showMessage("Install GISKE pin key successfully.");
        } catch (NSDKException e){
            e.printStackTrace();
        }

    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_update_aes_dukpt_ik, functionid = 12)
    public void updateAESDUKPTIK(){
        SymmetricKey sourceKey = new SymmetricKey();
        DUKPTKey desKey = new DUKPTKey();

        byte[] ksnBuffer = {0x12, 0x34, 0x56, 0x78, (byte) 0x90, 0x12, 0x34, 0x56, 0x00, 0x00, 0x00, 0x00};
        AlgorithmParameters algorithmParameters = new AlgorithmParameters();

        sourceKey.setKeyID(AppConfig.Keys.DUKPT_AES_INDEX);
        sourceKey.setKeyType(KeyType.AES);
        sourceKey.setKeyUsage(KeyUsage.DUKPT);

        algorithmParameters.setCipherMode(CipherMode.ECB);

        desKey.setKeyID(AppConfig.Keys.DUKPT_AES_INDEX);
        desKey.setKeyType(KeyType.AES);
        desKey.setKeyUsage(KeyUsage.DUKPT);
        desKey.setKeyLen(16);

        desKey.setKeyData(ISOUtils.hex2byte("4121C3828A05CD616DADB1AD8EC980A0"));
        desKey.setKCVMode(KCVMode.NONE);
        desKey.setKSN(ksnBuffer);

        try {
            byte[] ksn = mKeyManager.getKeyInfo(KeyInfoID.KSN, desKey);
            if (null == ksn) {
                showMessage("AES DUKPT key shall be loaded first.", MessageTag.ERROR);
                return;
            }
            showMessage(String.format("Before update, ksn: %s", ISOUtils.hexString(ksn)));
            mKeyManager.generateKey(KeyGenerateMethod.AES_DUKPT_UPDATE_IK,
                    algorithmParameters, sourceKey, desKey);
            ksn = mKeyManager.getKeyInfo(KeyInfoID.KSN, desKey);
            if (ksn == null) {
                showMessage("Failed to get ksn after update.", MessageTag.ERROR);
                return;
            }
            showMessage(String.format("After update, ksn: %s", ISOUtils.hexString(ksn)));
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "update AES DUKPT IK");
            return;
        }
        showMessage("AES DUKPT IK updated successfully.");
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_increase_ksn, functionid = 13)
    public void increaseKsn(){
        try {
            SymmetricKey desKey = new SymmetricKey();
            desKey.setKeyID(AppConfig.Keys.DUKPT_DES_INDEX);
            desKey.setKeyType(KeyType.DES);
            desKey.setKeyUsage(KeyUsage.DUKPT);

            mKeyManager.increaseKSN(AppConfig.Keys.DUKPT_DES_INDEX);
            byte[] ksn = mKeyManager.getKeyInfo(KeyInfoID.KSN, desKey);
            showMessage(String.format("KSN: %s", ISOUtils.hexString(ksn)));
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "increase ksn");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_increase_aes_ksn, functionid = 14)
    public void increaseAESKSN(){
        try {
            SymmetricKey desKey = new SymmetricKey();
            desKey.setKeyID(AppConfig.Keys.DUKPT_AES_INDEX);
            desKey.setKeyType(KeyType.AES);
            desKey.setKeyUsage(KeyUsage.DUKPT);

            mKeyManager.increaseKSN(desKey);
            byte[] ksn = mKeyManager.getKeyInfo(KeyInfoID.KSN, desKey);
            showMessage(String.format("AES KSN: %s", ISOUtils.hexString(ksn)));
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "increase AES KSN");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_reset_cert_status, functionid = 15)
    public void resetCertStatus(){
        try {
            mKeyManager.resetCertStatus();
            showMessage("Reset cert status successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "reset cert status");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_load_trusted_cert, functionid = 16)
    public void loadTrustedCert(){
//        String certCa = FileUtils.readFromAssets(context, "Newland_MFG_CA.crt");
//        byte[] certCaBuf = certCa.getBytes();
        String certEnc = FileUtils.readFromAssets(context, "cert/DEV_NPT_RKMS_ENC_Cert.pem");
        byte[] certEncBuf = certEnc.getBytes();
        String certSign = FileUtils.readFromAssets(context, "cert/DEV_NPT_RKMS_AUTH_Cert.pem");
        byte[] certSignBuf = certSign.getBytes();
//        LogUtils.d(TAG, String.format("CA cert string: %s", certCa));
//        LogUtils.d(TAG, String.format("CA cert buffer: %s", ISOUtils.hexString(certCaBuf)));
        LogUtils.d(TAG, String.format("Enc cert string: %s", certEnc));
        LogUtils.d(TAG, String.format("Enc cert buffer: %s", ISOUtils.hexString(certEncBuf)));
        LogUtils.d(TAG, String.format("Sign cert string: %s", certSign));
        LogUtils.d(TAG, String.format("Sign buffer: %s", ISOUtils.hexString(certSignBuf)));
        byte[] keyData;

//        try {
//            keyData = mKeyManager.loadTrustedCert(true, certCaBuf);
//            showMessage("CA cert is loaded successfully.");
//        } catch (NSDKException e) {
//            e.printStackTrace();
//            showErrorMessage(e, "load trusted CA cert");
//            return;
//        }

        try {
            keyData = mKeyManager.loadTrustedCert(false, certEncBuf);
            showMessage("Enc cert is loaded successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "load trusted enc cert");
            return;
        }

        AsymmetricKey distributionKey = new AsymmetricKey();
        distributionKey.setKeyType(AsymKeyType.RSA);
        distributionKey.setKeyUsage(AsymKeyUsage.KEY_DISTRIBUTION);
        distributionKey.setKeyID((byte) AppConfig.Keys.ASYM_KEY_DISTRIBUTION_ID);
        distributionKey.setKeyData(keyData);
        distributionKey.setKeyLen(keyData.length);

        try {
            mKeyManager.generateKey(KeyGenerateMethod.CLEAR, null, null, distributionKey, certEncBuf);
            showMessage("Asym distribution key is generated successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "load asym distribution key");
            return;
        }

        try {
            keyData = mKeyManager.loadTrustedCert(false, certSignBuf);
            showMessage("Sign cert is loaded successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "load trusted sign cert");
            return;
        }

        AsymmetricKey authKey = new AsymmetricKey();
        authKey.setKeyType(AsymKeyType.RSA);
        authKey.setKeyUsage(AsymKeyUsage.AUTH);
        authKey.setKeyID((byte) AppConfig.Keys.ASYM_AUTH_ID);
        authKey.setKeyData(keyData);
        authKey.setKeyLen(keyData.length);

        try {
            mKeyManager.generateKey(KeyGenerateMethod.CLEAR, null, null, authKey, certSignBuf);
            showMessage("Asym auth key is generated successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "load asym auth key");
            return;
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_asym_distribution_key, functionid = 17)
    public void generateAsymDistributionKey(){
        byte[] keyValue = ISOUtils.hex2byte("308204A40201000282010100C7B433BED854C40AB66006F9F2E50FD8F70E162A1989C4468A41E1F8A6D5443B1F121F57CF711DF5239D161594DE223EBF640D3A7A9EDDE23E1CEE3332EA05B0E8E66983F510E798E259621EEB6F7DC72573C0BF879078DD34BCAF652B1142E2370ABBB4AA33A8E61F173197C0009F416FBEE0F12205906D91761D104ABEC57CBB70A8660F5990447D5115523E1618D5DD3454132CC35440F56C1DFB21D86A3D4E7BCADD957F8CBAF02DBEF75390EC3FC09C81CC3A2C2EE6B2F0AB2EB56327F254963FB5E37DD9D07F8876050E1B3C2DA891039B44DC9FF2AD321092AD07695E7198B1590778AE6A0B11078C59BE7D055C40C14BA0ACCB390C34D1F4EAA08DB7020301000102820101008F4E74178E7BFD96465B50864AD42F741D8DA14022C566F0CBC40D5976B6F1D88F2A5D0D9151F61274B50425068805010C2CCB055CFAE5F4B567E35320452942534F0D06E174790D8FD85E7E1BAB0D123C80FD1F3433EB57A9C18107D348F6BB088E8E364E30F611F4DB9AFDEF8D42BB1D6A57571277F5B57ABDCC8B6567D2A4B3D7B02EC249055210CE27E177F68E4B2D8762AED2A292D152B7147E556B746D2C5E4EB10A747CDA092773AF1BE63DE462CACC78BF404AB7645C569116E92BD1B08A7DAE9DCCEBB9F06975B53E0F7EEF87D3632654052960320EA5DA89CC860E7845D4223149C74174DEF64CFEF56533E74AEB447C9105CEF8BA4AC1A761271902818100E9D59F19698F4F88B1054911DEA48F185B24A37D1FD59DF9C86F6402933F943256660C68D89E6CE47D6C2AF73D30D43B26B4AE727B3B909A45B94544708B26A2F2CB3CE80208EAB8563E08A4C8F01CE42D9EE582237E112AEB150978F741DBB4D8F5CF8CF3EC84B2F8BEDF8515CD09F10A77D440144A25C1164AFEA857B1046302818100DAA258C411A46F1586F4CA9D721EAE91103130085152C730FF080AD09613D4E9A26F6184A1C33FC156AA51B39661B2011A4F62C2BEFE7FD3ECAFE7D4E3D01780F8497E65AC921BA4F32E405547F7C20207E4BA10C080217AB5955B0DC214390F77F68A49C4CD7F6A5F674115B5FE0051A5EB778FF775EE32D4D0D65A6A79BF9D02818073870A6AE8BF5841258C81F4653692482B47A7CA7AFEF464E3453D79143CF6400475B8ADD8503566921CEE0166E708040D747937B070D65992171E04D941B69E0D2CA273D2058BC9F53A02D53F3D7A58B5BD6A90E86E31972DBC2008F6C83FFF52ADE79431336AC06DAB080DDD9E86C9E32D1EDD65E8D84C54724AECEB1E571702818100CC7151360F154736C71AF2B865B561218F159FC7B75C039DC26D5FC78AFF0272728D5A902EBFA8477F6606BE3B1A21DCBC208DC673D901E947037A72ED9071DA0CD2949A38D3EB7BC456749C0A8F1860D3C6C362D4CF3BFF45FB62C8ABBA435D3E9D50D5086DECFEA835A27BCDD1B5B4A3C55AE949CA8232102534BC19A2BD550281806EB82AE428F6A7BD1831B21CC1DCF2BD158F7A391FFB6238323F7F0BEE67677571E9990532D1B8ADF31CBD534F073DAD5F61CC8C7B86A85B43D95A1170FD0EB38143A5B0635B7CEAA22942C802FA2A2C0DA557B8B856F3FE5DAA150CF74675A28A1D0F0CD0BAFCE5645746BF819206DDC88FD27F571F5EAD7A295E38979B9B0F");
        AsymmetricKey dstKey = new AsymmetricKey();
        dstKey.setKeyID((byte) AppConfig.Keys.ASYM_KEY_DISTRIBUTION_ID);
        dstKey.setKeyUsage(AsymKeyUsage.KEY_DISTRIBUTION);
        dstKey.setKeyType(AsymKeyType.RSA);
        dstKey.setKeyData(keyValue);
        dstKey.setKeyLen(keyValue.length);

        try {
            mKeyManager.generateKey(KeyGenerateMethod.CLEAR, null, null, dstKey, null);
            showMessage("Asym distribution key is generated successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "generate asym distribution key");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_asym_data_key, functionid = 18)
    public void generateAsymDataKey(){
        byte[] keyValue = ISOUtils.hex2byte("308204A40201000282010100C7B433BED854C40AB66006F9F2E50FD8F70E162A1989C4468A41E1F8A6D5443B1F121F57CF711DF5239D161594DE223EBF640D3A7A9EDDE23E1CEE3332EA05B0E8E66983F510E798E259621EEB6F7DC72573C0BF879078DD34BCAF652B1142E2370ABBB4AA33A8E61F173197C0009F416FBEE0F12205906D91761D104ABEC57CBB70A8660F5990447D5115523E1618D5DD3454132CC35440F56C1DFB21D86A3D4E7BCADD957F8CBAF02DBEF75390EC3FC09C81CC3A2C2EE6B2F0AB2EB56327F254963FB5E37DD9D07F8876050E1B3C2DA891039B44DC9FF2AD321092AD07695E7198B1590778AE6A0B11078C59BE7D055C40C14BA0ACCB390C34D1F4EAA08DB7020301000102820101008F4E74178E7BFD96465B50864AD42F741D8DA14022C566F0CBC40D5976B6F1D88F2A5D0D9151F61274B50425068805010C2CCB055CFAE5F4B567E35320452942534F0D06E174790D8FD85E7E1BAB0D123C80FD1F3433EB57A9C18107D348F6BB088E8E364E30F611F4DB9AFDEF8D42BB1D6A57571277F5B57ABDCC8B6567D2A4B3D7B02EC249055210CE27E177F68E4B2D8762AED2A292D152B7147E556B746D2C5E4EB10A747CDA092773AF1BE63DE462CACC78BF404AB7645C569116E92BD1B08A7DAE9DCCEBB9F06975B53E0F7EEF87D3632654052960320EA5DA89CC860E7845D4223149C74174DEF64CFEF56533E74AEB447C9105CEF8BA4AC1A761271902818100E9D59F19698F4F88B1054911DEA48F185B24A37D1FD59DF9C86F6402933F943256660C68D89E6CE47D6C2AF73D30D43B26B4AE727B3B909A45B94544708B26A2F2CB3CE80208EAB8563E08A4C8F01CE42D9EE582237E112AEB150978F741DBB4D8F5CF8CF3EC84B2F8BEDF8515CD09F10A77D440144A25C1164AFEA857B1046302818100DAA258C411A46F1586F4CA9D721EAE91103130085152C730FF080AD09613D4E9A26F6184A1C33FC156AA51B39661B2011A4F62C2BEFE7FD3ECAFE7D4E3D01780F8497E65AC921BA4F32E405547F7C20207E4BA10C080217AB5955B0DC214390F77F68A49C4CD7F6A5F674115B5FE0051A5EB778FF775EE32D4D0D65A6A79BF9D02818073870A6AE8BF5841258C81F4653692482B47A7CA7AFEF464E3453D79143CF6400475B8ADD8503566921CEE0166E708040D747937B070D65992171E04D941B69E0D2CA273D2058BC9F53A02D53F3D7A58B5BD6A90E86E31972DBC2008F6C83FFF52ADE79431336AC06DAB080DDD9E86C9E32D1EDD65E8D84C54724AECEB1E571702818100CC7151360F154736C71AF2B865B561218F159FC7B75C039DC26D5FC78AFF0272728D5A902EBFA8477F6606BE3B1A21DCBC208DC673D901E947037A72ED9071DA0CD2949A38D3EB7BC456749C0A8F1860D3C6C362D4CF3BFF45FB62C8ABBA435D3E9D50D5086DECFEA835A27BCDD1B5B4A3C55AE949CA8232102534BC19A2BD550281806EB82AE428F6A7BD1831B21CC1DCF2BD158F7A391FFB6238323F7F0BEE67677571E9990532D1B8ADF31CBD534F073DAD5F61CC8C7B86A85B43D95A1170FD0EB38143A5B0635B7CEAA22942C802FA2A2C0DA557B8B856F3FE5DAA150CF74675A28A1D0F0CD0BAFCE5645746BF819206DDC88FD27F571F5EAD7A295E38979B9B0F");
        AsymmetricKey dstKey = new AsymmetricKey();
        dstKey.setKeyID((byte) AppConfig.Keys.ASYM_DATA_ID);
        dstKey.setKeyUsage(AsymKeyUsage.DATA);
        dstKey.setKeyType(AsymKeyType.RSA);
        dstKey.setKeyData(keyValue);
        dstKey.setKeyLen(keyValue.length);

        try {
            mKeyManager.generateKey(KeyGenerateMethod.CLEAR, null, null, dstKey, null);
            showMessage("Asym data key is generated successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "generate asym data key");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_asym_auth_key, functionid = 19)
    public void generateAsymAuthKey(){
        byte[] keyValue = ISOUtils.hex2byte("308204A40201000282010100C6DEE0BC45C7D2D062602F76C3FBFE5B2FB72A77217CC201B312F7FF8DAFBF2DD3B1404313A975F5F54ECE9D502FF6634468E5FD90D8A834C89AB5647A32B69BAACED6E265B2EE5A5089C58FF33A1F1D57CB2F9A4C0376E4A64EEC0D89C66562C984FCDC86A657EA4F7CC3E85AA4A4C1192597AC10F8E2A7A0EB805952A58C433C489513EEF5CF3CA8613BDF47B9FCCB6476D3DBC6E5C4BE2D392947FEFB0F92F0AAAA78F7C791251F3A4223A583A9CBD084F04CE36B166FAC121D8A1CCC02D38B4B4B88516CFFA06518FB0975F97E8E02D8ECE519A1BE41729271B98D449925DE9AF1591BD0A7D9DE4407C51081BA10FEC8017859A26DC23C02B450D9B11EF102030100010282010100BED1F50A325ABE69BD3B55CFBBD5FC063B0EA1EC95714426A5513A2D3822BE6A9689A983B346132DE227B0113A740B12CCFD6A5197BE8C07B9C4D8F084604CDFA951B6D69D86C73659B9189C3B6235A0CE30E488450FBDF13FB2D2C55AC1C75EB6C6A86A61B912FA7D32D638096199C4BF00573C7F3C911F0F45696E4BE315656B3364D5051F01467FDBD3415A498A5C821FD67232D2A8A193B94A956C26C0117D0568D28CEDA59669551DABF1B994A3F314C925133F88F31E7885162C3880C65E19D48390D5EA058384D082476B9E9229631F059C189A3BAE60EB78B3E650B3A5B77DEBABFA56D8970DBF444FF8510146195AA1BF7698CF01A31CFC74FFD7C102818100F95559F528B0892169E661EF8A1A5012905013E4F857B7EF26891E019F644A2119825DFAC7EEF099971C68B32E95BA0FEFD6E7DF8CC8FAFB7B28A743C15AD7ACD930BCBE3A497C1D3893CA4E90FE13E2D7A2EE85A3354674F02521E4B51C3A4AEB1162E01637CA9B001A6F2ECDB3BB07DCF8ABD8A0666A7CA2DE5C3A2A991D6D02818100CC301DD2DDF6B1AC0D78D5F12F0798A2B264B112802D3E81A086DA3FC896507E0C8A6B1B5E66B5ECF686121A6805FE984C60FE1C9E0503E15416364CEE3B22651DDEC0A1001F9C37ACDC640B62E5EE16D27974D5EE92EFFD5386DA9492FCCACF54BD5FA0915ED115F8991D897B2D59E09ADED149B405AC10958084514C50691502818007DD2448322F5733E1962D9293857EEF06F42F9C7224BA1D65D6BF4687D36EEF1A51DD4AF2915BAF4C6FCDF190CF921DBC8FC7A26A5B50672C1C3D224AEFE58B83122171D27ECCD653197E30FA2BB94ED74441479FBD276ABAC4410C6895EA54C0933CCE1A8549F3978E3DE179056929B753748011970956C300466263438F050281807529A2E34D53F1AD1CE9DA3113605377FFCF013FF16684B852C92E506D23BB3A28AE00396B189A894707B5398BB8ECD6ACF4F6BAAAFD8BB56ECF7406FEA7D5DB99A1287CF99A29C4549EFD94FF019A7563FE27495E24D82A4F145135F185B645F384DA6B431ED9F0B67DFD51D6E935EA48535459EB3F59F506240148B8F666E502818100904C79FC91B9C0E9BF0B949A75341EF6524A06E4FEE00A63D4FEC81C00E0849AFC2A414A07F43379DAE40038C12CFA2DF8EA3FAC89784C121736A1EE508CBC3E3235766413857CD97546446D141DC7D73684A1FC7C2F777C6ECE84CDA9D33D7588A127A87F43303473AEC72F9147C67B959949262F136CB806525D7440AE48E1");
        AsymmetricKey dstKey = new AsymmetricKey();
        dstKey.setKeyID((byte) AppConfig.Keys.ASYM_AUTH_ID);
        dstKey.setKeyUsage(AsymKeyUsage.AUTH);
        dstKey.setKeyType(AsymKeyType.RSA);
        dstKey.setKeyData(keyValue);
        dstKey.setKeyLen(keyValue.length);

        try {
            mKeyManager.generateKey(KeyGenerateMethod.CLEAR, null, null, dstKey, null);
            showMessage("Asym auth key is generated successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "generate asym auth key");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_load_session_key, functionid = 20)
    public void generateKeyWithAsymKey(){
        // srcKey shall be generated first.
        AsymmetricKey srcKey = new AsymmetricKey();
        srcKey.setKeyID((byte) AppConfig.Keys.ASYM_KEY_DISTRIBUTION_ID);
        srcKey.setKeyUsage(AsymKeyUsage.KEY_DISTRIBUTION);
        srcKey.setKeyType(AsymKeyType.RSA);

        SymmetricKey dstKey = new SymmetricKey();
        dstKey.setKeyID((byte) AppConfig.Keys.RKI_SK_ID);
        dstKey.setKeyType(KeyType.DES);
        dstKey.setKeyUsage(KeyUsage.TR31_KEK);
        dstKey.setKeyLen(24);

        AsymAlgorithmParameters parameters = new AsymAlgorithmParameters();
        parameters.setEncodingMode(AsymEncodingMode.PKCS_V21);
        parameters.setMessageDigestType(MessageDigestType.SHA256);

        try {
            byte[] result = mKeyManager.generateKeyWithAsymKey(KeyGenerateMethod.RANDOM_OUT, parameters, srcKey, dstKey);
            showMessage(String.format("Random SK: %s", ISOUtils.hexString(result)));
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "generate key with asym key");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_get_device_cert, functionid = 21)
    public void getDeviceCertKey(){
        AsymmetricKey key = new AsymmetricKey();
        // Device cert shall be injected by master POS first.
        key.setKeyID((byte) 255);
        key.setKeyUsage(AsymKeyUsage.AUTH);
        key.setKeyType(AsymKeyType.RSA);
        try {
            byte[] result = mKeyManager.getKeyInfo(KeyInfoID.CERTIFICATE, key);
            showMessage(String.format("Device cert: %s", ISOUtils.hexString(result)));
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "get device cert");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_init_atomic, functionid = 22)
    public void initAtomic(){
        try {
            mKeyManager.initAtomic();
            showMessage("Init atomic successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "init atomic");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_commit_atomic, functionid = 23)
    public void commitAtomic(){
        try {
            mKeyManager.commitAtomic(true);
            showMessage("Commit atomic successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "commit atomic");
        }
    }

    @MethodGridEntity(btnname = "DIVERSIFY_X Key", functionid = 24)
    public void diversifyXKey(){
        SymmetricKey kekKey = new SymmetricKey();
        kekKey.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_MK);
        kekKey.setKeyType(KeyType.DES);
        kekKey.setKeyUsage(KeyUsage.KEK);

        SymmetricKey diversifyXKey = new SymmetricKey();
        diversifyXKey.setKeyID((byte) 21);
        diversifyXKey.setKeyType(KeyType.DES);
        diversifyXKey.setKeyUsage(KeyUsage.KEK);
        diversifyXKey.setKeyData(ISOUtils.hex2byte("D2D2D2D2D2D2D2D2"));
//        diversifyXKey.setKeyData(ISOUtils.hex2byte("D2D2D2D2D2D2D2D2D3D3D3D3D3D3D3D3"));
        diversifyXKey.setKeyLen(8);
        try {
            mKeyManager.generateKey(KeyGenerateMethod.DIVERSIFY_X, kekKey, diversifyXKey);
            showMessage("DIVERSIFY_X key is generated successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "generate DIVERSIFY_X key.");
        }
    }

    @MethodGridEntity(btnnameid = R.string.dialog_tv_keymanager_load_keys, functionid = 25)
    private void loadKeys() {
        DialogUtils.createCustomDialog(context, R.string.dialog_tv_keymanager_load_keys, null, R.layout.dialog_keymanager_load_keys, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                Switch swDefaultKeyID = view.findViewById(R.id.switch_default_key_id_btn);
                swDefaultKeyID.setChecked(true);
                LinearLayout llLoadDefaultKEKIDParams = view.findViewById(R.id.linear_keymanager_load_defaultKEKID_params);
                llLoadDefaultKEKIDParams.setVisibility(View.VISIBLE);
                LinearLayout llLoadEditKEKIDParams = view.findViewById(R.id.linear_keymanager_load_editKEKID_params);
                llLoadEditKEKIDParams.setVisibility(View.GONE);
                LinearLayout llLoadDefaultKeyByKEKParams = view.findViewById(R.id.linear_keymanager_load_symm_defaultKeyByKEK_Params);
                llLoadDefaultKeyByKEKParams.setVisibility(View.VISIBLE);
                LinearLayout llLoadEditKeyByKEKParams = view.findViewById(R.id.linear_keymanager_load_symm_editKeyByKEK_params);
                llLoadEditKeyByKEKParams.setVisibility(View.GONE);
                LinearLayout llLoadAsymDefaultKeyIDParams = view.findViewById(R.id.linear_keymanager_loadAsymDefaultKeyID_Params);
                llLoadAsymDefaultKeyIDParams.setVisibility(View.VISIBLE);
                LinearLayout llLoadAsymEditKeyIDParams = view.findViewById(R.id.linear_keymanager_load_asym_editKeyID_params);
                llLoadAsymEditKeyIDParams.setVisibility(View.GONE);
                LinearLayout llLoadSymmOfAsymDeafultKeyIDParams = view.findViewById(R.id.linear_keymanager_loadSymmOfAsym_defaultKeyID_params);
                llLoadSymmOfAsymDeafultKeyIDParams.setVisibility(View.VISIBLE);
                LinearLayout llLoadSymmOfAsymEditKeyIDParams = view.findViewById(R.id.linear_keymanager_loadSymmOfAsym_editKeyID_params);
                llLoadSymmOfAsymEditKeyIDParams.setVisibility(View.GONE);
                LinearLayout llLoadSymmKeysKeyDataParams = view.findViewById(R.id.linear_keymanager_load_symm_keys_keyData_params);
                llLoadSymmKeysKeyDataParams.setVisibility(View.VISIBLE);

                RadioGroup rgLoadSymmOrAsymKeys = view.findViewById(R.id.keymanager_load_keys_radioGroup);
                RadioButton rbLoadSymmKeys = view.findViewById(R.id.keymanager_load_symm_keys_radio);
                RadioButton rbLoadAsymKeys = view.findViewById(R.id.keymanager_load_asym_keys_radio);
                rbLoadSymmKeys.setChecked(true);
                load_symm_kek_Flag = 2;
                LinearLayout llLoadSymmKekKeysParams = view.findViewById(R.id.linear_keymanager_load_symmKeys_params);
                llLoadSymmKekKeysParams.setVisibility(View.VISIBLE);
                LinearLayout llLoadSymmKeysParams = view.findViewById(R.id.linear_keymanager_load_symm_keys_by_kek_params);
                llLoadSymmKeysParams.setVisibility(View.GONE);
                LinearLayout llLoadAsymKeysType = view.findViewById(R.id.linear_keymanager_load_asym_keys_views_params);
                llLoadAsymKeysType.setVisibility(View.GONE);
                LinearLayout llLoadAsymKeysParams = view.findViewById(R.id.linear_keymanager_loadAsymKeys_params);
                llLoadAsymKeysParams.setVisibility(View.GONE);
                LinearLayout llLoadSymmKeysOfAsymKeysParams = view.findViewById(R.id.linear_keymanager_load_symmetricKey_of_asymmetircKey_params);
                llLoadSymmKeysOfAsymKeysParams.setVisibility(View.GONE);
                LinearLayout llSymmKekKeysParams = view.findViewById(R.id.linear_keymanager_load_symm_kek_views_params);
                llSymmKekKeysParams.setVisibility(View.VISIBLE);
                LinearLayout llSymmKeysKsnParams = view.findViewById(R.id.linear_keymanager_loadSymmKeyByKEK_ksn_params);
                llSymmKeysKsnParams.setVisibility(View.GONE);
                LinearLayout llLoadAsymKeyDataParams = view.findViewById(R.id.linear_keymanager_load_asym_keyData_Params);
                llLoadAsymKeyDataParams.setVisibility(View.VISIBLE);
                RadioGroup rgLoadSymmKekKeys = view.findViewById(R.id.keymanager_load_symm_kek_radioGroup);
                RadioButton rbLoadSymmKekKeys = view.findViewById(R.id.rbLoadSymmKEK);
                RadioButton rbLoadSymmKeysByKek = view.findViewById(R.id.keymanager_load_symm_otherKeys_radio);
                rbLoadSymmKekKeys.setChecked(true);
                RadioGroup rgLoadAsymKeys = view.findViewById(R.id.keymanager_load_asym_keys_radioGroup);
                RadioButton rbLoadAsymSessionKey = view.findViewById(R.id.keymanager_load_sessionKey_radio);
                RadioButton rbLoadAsymOtherKeys = view.findViewById(R.id.keymanager_load_other_asymKeys_radio);
                rbLoadAsymOtherKeys.setChecked(true);

                swDefaultKeyID.setOnCheckedChangeListener((buttonView, isChecked)-> {
                    if(isChecked) {
                        if (rbLoadSymmKeys.isChecked()) {
                            llLoadDefaultKEKIDParams.setVisibility(View.VISIBLE);
                            llLoadEditKEKIDParams.setVisibility(View.GONE);
                            llLoadDefaultKeyByKEKParams.setVisibility(View.VISIBLE);
                            llLoadEditKeyByKEKParams.setVisibility(View.GONE);
                        } else if(rbLoadAsymKeys.isChecked()) {
                            llLoadAsymDefaultKeyIDParams.setVisibility(View.VISIBLE);
                            llLoadAsymEditKeyIDParams.setVisibility(View.GONE);
                            llLoadSymmOfAsymDeafultKeyIDParams.setVisibility(View.VISIBLE);
                            llLoadSymmOfAsymEditKeyIDParams.setVisibility(View.GONE);
                        }
                    } else {
                        if (rbLoadSymmKeys.isChecked()) {
                            llLoadDefaultKEKIDParams.setVisibility(View.GONE);
                            llLoadEditKEKIDParams.setVisibility(View.VISIBLE);
                            llLoadDefaultKeyByKEKParams.setVisibility(View.GONE);
                            llLoadEditKeyByKEKParams.setVisibility(View.VISIBLE);
                        } else if (rbLoadAsymKeys.isChecked()) {
                            llLoadAsymDefaultKeyIDParams.setVisibility(View.GONE);
                            llLoadAsymEditKeyIDParams.setVisibility(View.VISIBLE);
                            llLoadSymmOfAsymDeafultKeyIDParams.setVisibility(View.GONE);
                            llLoadSymmOfAsymEditKeyIDParams.setVisibility(View.VISIBLE);
                        }
                    }
                });

                rgLoadSymmOrAsymKeys.setOnCheckedChangeListener((group, checkedId) -> {
                    if(checkedId == rbLoadSymmKeys.getId()) {
                        rbLoadSymmKekKeys.setChecked(true);
                        llLoadSymmKekKeysParams.setVisibility(View.VISIBLE);
                        llLoadSymmKeysParams.setVisibility(View.GONE);
                        llLoadAsymKeysType.setVisibility(View.GONE);
                        llLoadAsymKeysParams.setVisibility(View.GONE);
                        llLoadSymmKeysOfAsymKeysParams.setVisibility(View.GONE);
                        llSymmKekKeysParams.setVisibility(View.VISIBLE);
                        load_symm_kek_Flag = 2;
                        loadAsymFlag = -1;
                    }
                    if(checkedId == rbLoadAsymKeys.getId()) {
                        rbLoadAsymOtherKeys.setChecked(true);
                        llLoadSymmKekKeysParams.setVisibility(View.GONE);
                        llLoadSymmKeysParams.setVisibility(View.GONE);
                        llLoadAsymKeysType.setVisibility(View.VISIBLE);
                        llLoadAsymKeysParams.setVisibility(View.VISIBLE);
                        llLoadSymmKeysOfAsymKeysParams.setVisibility(View.GONE);
                        llSymmKekKeysParams.setVisibility(View.GONE);
                        load_symm_kek_Flag = -1;
                        loadAsymFlag = 1;
                    }
                });

                rgLoadAsymKeys.setOnCheckedChangeListener((group, checkedId) -> {
                    if(checkedId == rbLoadAsymSessionKey.getId()) {
                        loadAsymmetricSessionKeyFlag = 1;
                        llLoadAsymKeysParams.setVisibility(View.VISIBLE);
                        llLoadSymmKeysOfAsymKeysParams.setVisibility(View.VISIBLE);
                        llLoadAsymKeyDataParams.setVisibility(View.GONE);
                    }
                    if(checkedId == rbLoadAsymOtherKeys.getId()) {
                        loadAsymmetricSessionKeyFlag = 0;
                        llLoadAsymKeysParams.setVisibility(View.VISIBLE);
                        llLoadSymmKeysOfAsymKeysParams.setVisibility(View.GONE);
                        llLoadAsymKeyDataParams.setVisibility(View.VISIBLE);
                    }
                });
                rgLoadSymmKekKeys.setOnCheckedChangeListener((group, checkedId) -> {
                    if(checkedId == rbLoadSymmKekKeys.getId()) {
                        llLoadSymmKeysParams.setVisibility(View.GONE);
                        llLoadSymmKekKeysParams.setVisibility(View.VISIBLE);
                        llLoadSymmKeysKeyDataParams.setVisibility(View.GONE);
                    }
                    if (checkedId == rbLoadSymmKeysByKek.getId()) {
                        llLoadSymmKekKeysParams.setVisibility(View.VISIBLE);
                        llLoadSymmKeysParams.setVisibility(View.VISIBLE);
                        llLoadSymmKeysKeyDataParams.setVisibility(View.GONE);
                    }
                });



                EditText etLoadSymmOrAsymKekKeyID = view.findViewById(R.id.et_keymanager_load_symm_editKEKKeyID);
                etLoadSymmOrAsymKekKeyID.setText(String.valueOf(sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEK_KEY_ID, 0)));
                Spinner spnLoadSymmOrAsymKekKeyUsage = view.findViewById(R.id.spn_keymanager_load_symm_kek_keyUsage);
                Spinner spnLoadSymmOrAsymKekKeyLen = view.findViewById(R.id.spn_keymanager_load_symm_kek_keyLen);
                Spinner spnLoadSymmOrAsymKekKcvMode = view.findViewById(R.id.spn_keymanager_load_symm_kek_kcvMode);
                Spinner spnLoadSymmOrAsymKekKeyType = view.findViewById(R.id.spn_keymanager_load_symm_kek_keyType);
                spnLoadSymmOrAsymKekKeyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEK_KEY_TYPE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int kekKeyType = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEK_KEY_TYPE, 0);
                spnLoadSymmOrAsymKekKeyType.setSelection(kekKeyType);

                spnLoadSymmOrAsymKekKeyLen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEK_KEY_LEN, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int kekKeyLen = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEK_KEY_LEN, 0);
                spnLoadSymmOrAsymKekKeyLen.setSelection(kekKeyLen);

                spnLoadSymmOrAsymKekKcvMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEK_KCV_MODE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int kekKcvMode = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEK_KCV_MODE, 0);
                spnLoadSymmOrAsymKekKcvMode.setSelection(kekKcvMode);

                spnLoadSymmOrAsymKekKeyUsage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEK_KEY_USAGE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int kekKeyUsage = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEK_KEY_USAGE, 0);
                spnLoadSymmOrAsymKekKeyUsage.setSelection(kekKeyUsage);

                EditText etLoadSymmOrAsymKeysKeyID = view.findViewById(R.id.et_keymanager_load_symm_editKeyByKEK_keyID);
                etLoadSymmOrAsymKeysKeyID.setText(String.valueOf(sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEYS_KEY_ID, 0)));
                Spinner spnLoadSymmOrAsymKeysKeyUsage = view.findViewById(R.id.spn_keymanager_loadSymmKeyByKEK_keyUsage);
                Spinner spnLoadSymmOrAsymKeysKeyLen = view.findViewById(R.id.spn_keymanager_loadSymmKeyByKEK_keyLen);
                Spinner spnLoadSymmOrAsymKeysKcvMode = view.findViewById(R.id.spn_keymanager_loadSymmKeyByKEK_kcvMode);
                Spinner spnLoadSymmOrAsymKeysKeyType = view.findViewById(R.id.spn_keymanager_loadSymmKeyByKEK_keyType);
                spnLoadSymmOrAsymKeysKeyUsage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if("DUKPT".equals(spnLoadSymmOrAsymKeysKeyUsage.getSelectedItem().toString())) {
                            llSymmKeysKsnParams.setVisibility(View.VISIBLE);
                        }else {
                            llSymmKeysKsnParams.setVisibility(View.GONE);
                        }
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEYS_KEY_USAGE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int keyUsage = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEYS_KEY_USAGE, 0);
                spnLoadSymmOrAsymKeysKeyUsage.setSelection(keyUsage);

                spnLoadSymmOrAsymKeysKeyLen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEYS_KEY_LEN, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int keyLen = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEYS_KEY_LEN, 0);
                spnLoadSymmOrAsymKeysKeyLen.setSelection(keyLen);

                spnLoadSymmOrAsymKeysKcvMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEYS_KEY_KCV_MODE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int kcvMode = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEYS_KEY_KCV_MODE, 0);
                spnLoadSymmOrAsymKeysKcvMode.setSelection(kcvMode);

                spnLoadSymmOrAsymKeysKeyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEYS_KEY_TYPE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int keyType = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEYS_KEY_TYPE, 0);
                spnLoadSymmOrAsymKeysKeyType.setSelection(keyType);

                EditText etLoadKekKeys_KeyData = view.findViewById(R.id.et_keymanager_load_symm_kek_keyData);
                etLoadKekKeys_KeyData.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEK_KEY_DATA, ""));

                EditText etLoadKeys_KeyData = view.findViewById(R.id.spn_keymanager_loadSymmKeyByKEK_keyData);
                etLoadKeys_KeyData.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEYS_KEY_DATA, ""));

                Spinner spnLoadAsymKeysKeyUsage = view.findViewById(R.id.spn_keymanager_load_asym_keyUsage);
                Spinner spnLoadAsymKeysKeyType = view.findViewById(R.id.spn_keymanager_load_asym_keyType);
                Spinner spnLoadAsymKeysEncodingMode = view.findViewById(R.id.spn_keymanager_load_asym_encodingMode);
                Spinner spnLoadAsymKeysMessageDigestType = view.findViewById(R.id.spn_keymanager_load_asym_messageDigestType);

                spnLoadAsymKeysKeyUsage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOAD_ASYM_KEY_USAGE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int asymKeyUsage = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOAD_ASYM_KEY_USAGE, 0);
                spnLoadAsymKeysKeyUsage.setSelection(asymKeyUsage);

                spnLoadAsymKeysKeyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOAD_ASYM_KEY_TYPE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int asymKeyType = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOAD_ASYM_KEY_TYPE, 0);
                spnLoadAsymKeysKeyType.setSelection(asymKeyType);

                spnLoadAsymKeysEncodingMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOAD_ASYM_ENCODING_MODE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int asymEncondingMode = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOAD_ASYM_ENCODING_MODE, 0);
                spnLoadAsymKeysEncodingMode.setSelection(asymEncondingMode);

                spnLoadAsymKeysMessageDigestType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOAD_ASYM_MESSAGE_DIGEST_TYPE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int asymMessageDigestType = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOAD_ASYM_MESSAGE_DIGEST_TYPE, 0);
                spnLoadAsymKeysMessageDigestType.setSelection(asymMessageDigestType);

                EditText etLoadAsymKeyID = view.findViewById(R.id.et_keymanager_load_asym_editKeyID);
                etLoadAsymKeyID.setText(String.valueOf(sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOAD_ASYM_KEY_ID, 0)));

                EditText etLoadAsymKeyKeyData = view.findViewById(R.id.et_keymanager_load_asym_keyData);
                etLoadAsymKeyKeyData.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOAD_ASYM_KEY_DATA, ""));

                EditText etKekKcvData = view.findViewById(R.id.et_keymanager_load_symm_kek_kcvData);
                etKekKcvData.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEK_KCV_DATA, ""));

                EditText etKcvData = view.findViewById(R.id.et_keymanager_loadSymmKeyByKEK_kcvData);
                etKcvData.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEYS_KCV_DATA, ""));

                EditText etKsnData = view.findViewById(R.id.et_keymanager_loadSymmKeyByKEK_ksnData);
                etKsnData.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEYS_KSN_DATA, ""));

                Spinner spnLoadDefaultKEKID = view.findViewById(R.id.spn_keymanager_load_defaultKEKID);
                spnLoadDefaultKEKID.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEK_DEFAULT_KEY_ID, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int defaultKeyIDPosition = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEK_DEFAULT_KEY_ID, 0);
                spnLoadDefaultKEKID.setSelection(defaultKeyIDPosition);

                Spinner spnLoadSymmDefaultKeyByKEK = view.findViewById(R.id.spn_keymanager_load_symm_defaultKeyByKEK_keyID);
                spnLoadSymmDefaultKeyByKEK.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEYS_DEFAULT_KEY_ID, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int symmByKEKDefaultKeyIDPosition = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEYS_DEFAULT_KEY_ID, 0);
                spnLoadSymmDefaultKeyByKEK.setSelection(symmByKEKDefaultKeyIDPosition);

                Spinner spnLoadAsymDefaultKeyID = view.findViewById(R.id.spn_keymanager_load_asym_defaultKeyID);
                spnLoadAsymDefaultKeyID.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOAD_ASYM_DEFAULT_KEY_ID, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int asymDefaultKeyIDPosition = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOAD_ASYM_DEFAULT_KEY_ID, 0);
                spnLoadAsymDefaultKeyID.setSelection(asymDefaultKeyIDPosition);

                Spinner spnLoadSymmOfAsymDefaultKeyID = view.findViewById(R.id.spn_keymanager_loadSymmOfAsym_defaultKeyID);
                spnLoadSymmOfAsymDefaultKeyID.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOAD_SYMM_OF_ASYM_DEFAULT_KEY_ID, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int symmOfAsymDefaultKeyIDPosition = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOAD_SYMM_OF_ASYM_DEFAULT_KEY_ID, 0);
                spnLoadSymmOfAsymDefaultKeyID.setSelection(symmOfAsymDefaultKeyIDPosition);

                Spinner spnLoadSymmOfAsymKeyType = view.findViewById(R.id.spn_keymanager_loadSymmOfAsym_keyType);
                spnLoadSymmOfAsymKeyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOAD_SYMM_OF_ASYM_KEY_TYPE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int symmOfAsymKeyTypePositon = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOAD_SYMM_OF_ASYM_KEY_TYPE, 0);
                spnLoadSymmOfAsymKeyType.setSelection(symmOfAsymKeyTypePositon);

                Spinner spnLoadSymmOfAsymKeyUsage = view.findViewById(R.id.spn_keymanager_loadSymmOfAsym_keyUsage);
                spnLoadSymmOfAsymKeyUsage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOAD_SYMM_OF_ASYM_KEY_USAGE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int symmOfAsymKeyUsagePosition = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOAD_SYMM_OF_ASYM_KEY_USAGE, 0);
                spnLoadSymmOfAsymKeyUsage.setSelection(symmOfAsymKeyUsagePosition);

                Spinner spnLoadSymmOfAsymKeyLen = view.findViewById(R.id.spn_keymanager_loadSymmOfAsym_keyLen);
                spnLoadSymmOfAsymKeyLen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOAD_SYMM_OF_ASYM_KEY_LEN, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int symmOfAsymKeyLenPosition = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOAD_SYMM_OF_ASYM_KEY_LEN, 0);
                spnLoadSymmOfAsymKeyLen.setSelection(symmOfAsymKeyLenPosition);

                LinearLayout llLoadSymmKeyByKEKKcvDataParams = view.findViewById(R.id.linear_keymanager_loadSymmKeyByKEK_kcvData_Params);
                llLoadSymmKeyByKEKKcvDataParams.setVisibility(View.GONE);
                spnLoadSymmOrAsymKeysKcvMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(position != 0) {
                            llLoadSymmKeyByKEKKcvDataParams.setVisibility(View.VISIBLE);
                        } else {
                            llLoadSymmKeyByKEKKcvDataParams.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                LinearLayout llLoadSymmKEKKcvDataParams = view.findViewById(R.id.linear_keymanager_load_symm_kek_kcvData_params);
                llLoadSymmKEKKcvDataParams.setVisibility(View.GONE);
                spnLoadSymmOrAsymKekKcvMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(position == 0) {
                            llLoadSymmKEKKcvDataParams.setVisibility(View.GONE);
                        } else {
                            llLoadSymmKEKKcvDataParams.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                LinearLayout llLoadSymmOfAsymKcvDataParams = view.findViewById(R.id.linear_keymanager_loadSymmOfAsym_kcvData_params);
                llLoadSymmOfAsymKcvDataParams.setVisibility(View.GONE);
                Spinner spnLoadSymmOfAsymKcvMode = view.findViewById(R.id.spn_keymanager_loadSymmOfAsym_kcvMode);
                spnLoadSymmOfAsymKcvMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            llLoadSymmOfAsymKcvDataParams.setVisibility(View.GONE);
                        } else {
                            llLoadSymmOfAsymKcvDataParams.setVisibility(View.VISIBLE);
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
                Spinner spnLoadDefaultKEKID = view.findViewById(R.id.spn_keymanager_load_defaultKEKID);
                Spinner spnLoadDefaultKeyByKEKID = view.findViewById(R.id.spn_keymanager_load_symm_defaultKeyByKEK_keyID);
                Spinner spnLoadAsymDefaultKeyID = view.findViewById(R.id.spn_keymanager_load_asym_defaultKeyID);
                Spinner spnLoadSymmOfAsymDefaultKeyId = view.findViewById(R.id.spn_keymanager_loadSymmOfAsym_defaultKeyID);
                Spinner spnLoadSymmKEKCipherMode = view.findViewById(R.id.spn_keymanager_load_symm_kek_cipherMode);
                Spinner spnLoadSymmKEKPaddingMode = view.findViewById(R.id.spn_keymanager_load_symm_kek_paddingMode);
                RadioButton rbLoadSymmKeys = view.findViewById(R.id.keymanager_load_symm_otherKeys_radio);
                RadioButton rbLoadSymmKEKKeys = view.findViewById(R.id.rbLoadSymmKEK);
                EditText etLoadSymmOrAsymKeys_KeyID = view.findViewById(R.id.et_keymanager_load_symm_editKeyByKEK_keyID);
                int loadSymmetricKeysKeyID = Integer.parseInt(etLoadSymmOrAsymKeys_KeyID.getText().toString());
                mEditor.putInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEYS_KEY_ID, loadSymmetricKeysKeyID);
                mEditor.commit();
                Spinner spnLoadSymmKeyByKEKKeyUsage = view.findViewById(R.id.spn_keymanager_loadSymmKeyByKEK_keyUsage);
                Spinner spnLoadSymmKeyByKEKKeyLen = view.findViewById(R.id.spn_keymanager_loadSymmKeyByKEK_keyLen);
                Spinner spnLoadSymmKeyByKEKKeyKcvMode = view.findViewById(R.id.spn_keymanager_loadSymmKeyByKEK_kcvMode);
                Spinner spnLoadSymmKeyByKEKKeyType = view.findViewById(R.id.spn_keymanager_loadSymmKeyByKEK_keyType);
                Spinner spnLoadSymmKeyByKEKCipherMode = view.findViewById(R.id.spn_keymanager_loadSymmKeyByKEK_cipherMode);
                Spinner spnLoadSymmKeyByKEKPaddingMode = view.findViewById(R.id.spn_keymanager_loadSymmKeyByKEK_paddingMode);
                EditText etLoadSymmKeyByKEKKeyKekKeyID = view.findViewById(R.id.et_keymanager_load_symm_editKEKKeyID);
                int loadSymmetricKekKeysKeyID = Integer.parseInt(etLoadSymmKeyByKEKKeyKekKeyID.getText().toString());
                mEditor.putInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEK_KEY_ID, loadSymmetricKekKeysKeyID);
                mEditor.commit();
                Spinner spnLoadSymmKekKeyUsage = view.findViewById(R.id.spn_keymanager_load_symm_kek_keyUsage);
                Spinner spnLoadSymmKekKeyLen = view.findViewById(R.id.spn_keymanager_load_symm_kek_keyLen);
                Spinner spnLoadSymmKekKcvMode = view.findViewById(R.id.spn_keymanager_load_symm_kek_kcvMode);
                Spinner spnLoadSymmKekKeyType = view.findViewById(R.id.spn_keymanager_load_symm_kek_keyType);
                Spinner spnLoadAsymKeyUsage = view.findViewById(R.id.spn_keymanager_load_asym_keyUsage);
                Spinner spnLoadAsymKeyType = view.findViewById(R.id.spn_keymanager_load_asym_keyType);
                Spinner spnLoadAsymEncodingMode = view.findViewById(R.id.spn_keymanager_load_asym_encodingMode);
                Spinner spnLoadAsymMessageDigestType = view.findViewById(R.id.spn_keymanager_load_asym_messageDigestType);
                Spinner spnLoadSymmOfAsymKcvMode = view.findViewById(R.id.spn_keymanager_loadSymmOfAsym_kcvMode);
                Spinner spnLoadSymmOfAsymKeyType = view.findViewById(R.id.spn_keymanager_loadSymmOfAsym_keyType);
                Spinner spnLoadSymmOfAsymKeyUsage = view.findViewById(R.id.spn_keymanager_loadSymmOfAsym_keyUsage);
                Spinner spnLoadSymmOfAsymKeyLen = view.findViewById(R.id.spn_keymanager_loadSymmOfAsym_keyLen);


                byte defaultKEKID = EnumUtils.getDefaultKeyID(spnLoadDefaultKEKID.getSelectedItem().toString());
                byte defaultKeyByKEKID = EnumUtils.getDefaultKeyID(spnLoadDefaultKeyByKEKID.getSelectedItem().toString());
                byte asymDefaultKeyID = EnumUtils.getAsymDefaultKeyID(spnLoadAsymDefaultKeyID.getSelectedItem().toString());
                byte symmOfAsymKeyID = EnumUtils.getDefaultKeyID(spnLoadSymmOfAsymDefaultKeyId.getSelectedItem().toString());
                KeyType loadSymmetricKekKeysKeyType = EnumUtils.getKeyType(spnLoadSymmKekKeyType.getSelectedItem().toString());
                KeyUsage loadSymmetricKekKeysKeyUsage = EnumUtils.getKeyUsage(spnLoadSymmKekKeyUsage.getSelectedItem().toString());
                int loadSymmetricKekKeysKeyLen = EnumUtils.getKeyLen(spnLoadSymmKekKeyLen.getSelectedItem().toString());
                PaddingMode loadSymmKEKPaddingMode = EnumUtils.getPaddingMode(spnLoadSymmKEKPaddingMode.getSelectedItem().toString());
                CipherMode loadSymmKEKCipherMode = EnumUtils.getCipherMode(spnLoadSymmKEKCipherMode.getSelectedItem().toString());
                PaddingMode loadSymmKeyByKEKPaddingMode = EnumUtils.getPaddingMode(spnLoadSymmKeyByKEKPaddingMode.getSelectedItem().toString());
                CipherMode loadSymmKeyByKEKCipherMode = EnumUtils.getCipherMode(spnLoadSymmKeyByKEKCipherMode.getSelectedItem().toString());
                KCVMode loadSymmetricKEKKeysKcvMode = EnumUtils.getKcvMode(spnLoadSymmKekKcvMode.getSelectedItem().toString());
                KeyUsage loadSymmetricKeysKeyUsage = EnumUtils.getKeyUsage(spnLoadSymmKeyByKEKKeyUsage.getSelectedItem().toString());
                int loadSymmetricKeysKeyLen = EnumUtils.getKeyLen(spnLoadSymmKeyByKEKKeyLen.getSelectedItem().toString());
                KCVMode loadSymmetricKeysKcvMode = EnumUtils.getKcvMode(spnLoadSymmKeyByKEKKeyKcvMode.getSelectedItem().toString());
                KeyType loadSymmetricKeysKeyType = EnumUtils.getKeyType(spnLoadSymmKeyByKEKKeyType.getSelectedItem().toString());
                KeyType loadAsymmetricSymmetricKeyKeyType = EnumUtils.getKeyType(spnLoadSymmOfAsymKeyType.getSelectedItem().toString());
                KeyUsage loadAsymmetricSymmetricKeyKeyUsage = EnumUtils.getKeyUsage(spnLoadSymmOfAsymKeyUsage.getSelectedItem().toString());
                int loadAsymmetricSymmetricKeyKeyLen = EnumUtils.getKeyLen(spnLoadSymmOfAsymKeyLen.getSelectedItem().toString());
                MessageDigestType loadAsymmetricSessionKeyMessageDigestType = EnumUtils.getMessageDigestType(spnLoadAsymMessageDigestType.getSelectedItem().toString());
                AsymEncodingMode loadAsymmetricSessionKeyAsymEncodingMode = EnumUtils.getAsymEncodingMode(spnLoadAsymEncodingMode.getSelectedItem().toString());
                AsymKeyUsage loadAsymmetricAsymKeyUsage = EnumUtils.getAsymKeyUsage(spnLoadAsymKeyUsage.getSelectedItem().toString());
                AsymKeyType loadAsymmetricAsymKeyType = EnumUtils.getAsymKeyType(spnLoadAsymKeyType.getSelectedItem().toString());
                KCVMode loadSymmOfAsymKcvMode = EnumUtils.getKcvMode(spnLoadSymmOfAsymKcvMode.getSelectedItem().toString());
                EditText etLoadKekKeyData = view.findViewById(R.id.et_keymanager_load_symm_kek_keyData);
                byte[] loadSymmetricKekKeysKeyData = ISOUtils.hex2byte(etLoadKekKeyData.getText().toString());
                KEKData = ISOUtils.hexString(loadSymmetricKekKeysKeyData);
                mEditor.putString(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEK_KEY_DATA, KEKData);
                mEditor.commit();
                EditText etLoadSymmOfAsymKcvData = view.findViewById(R.id.et_keymanager_loadSymmOfAsym_kcvData);
                byte[] loadSymmOfAsymKcvData = null;
                if (!"NONE".equals(spnLoadSymmOfAsymKcvMode.getSelectedItem().toString()) && !etLoadSymmOfAsymKcvData.getText().toString().isEmpty()) {
                    loadSymmOfAsymKcvData = ISOUtils.hex2byte(etLoadSymmOfAsymKcvData.getText().toString());
                }

                if(load_symm_kek_Flag == 2) {
                    EditText etKEKIVData = view.findViewById(R.id.et_keymanager_load_symm_kek_IV_data);
                    String kekIVData = etKEKIVData.getText().toString();
                    if (swDefaultKeyID.isChecked()) {
                        load_symm_kek_Flag = 0;
                        EditText etKekKcvData = view.findViewById(R.id.et_keymanager_load_symm_kek_kcvData);
                        String kekKcvData = etKekKcvData.getText().toString();
                        mEditor.putString(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEK_KCV_DATA, kekKcvData);
                        mEditor.commit();
                        SymmetricKey sourceKey = new SymmetricKey();
                        SymmetricKey desKey = new SymmetricKey();
                        byte[] kekKcv;
                        if(!"NONE".equals(spnLoadSymmKekKcvMode.getSelectedItem().toString()) || !kekKcvData.isEmpty()) {
                            kekKcv = ISOUtils.hex2byte(kekKcvData);
                            sourceKey.setKCV(kekKcv);
                        }
                        sourceKey.setKeyUsage(loadSymmetricKekKeysKeyUsage);
                        sourceKey.setKeyID(defaultKEKID);
                        sourceKey.setKeyLen(loadSymmetricKekKeysKeyLen);
                        sourceKey.setKCVMode(KCVMode.NONE);
                        sourceKey.setKeyData(loadSymmetricKekKeysKeyData);
                        sourceKey.setKeyType(loadSymmetricKekKeysKeyType);
                        AlgorithmParameters sourceKeyAlgorithmParameters = new AlgorithmParameters();
                        sourceKeyAlgorithmParameters.setCipherMode(loadSymmKEKCipherMode);
                        sourceKeyAlgorithmParameters.setPaddingMode(loadSymmKEKPaddingMode);
                        sourceKeyAlgorithmParameters.setIV(ISOUtils.hex2byte(kekIVData));

                        if(rbLoadSymmKEKKeys.isChecked()) {
                            try {
                                mKeyManager.generateKey(KeyGenerateMethod.CLEAR, sourceKeyAlgorithmParameters, null, sourceKey);
                                showMessage(String.format("Load %s %s  Key success.", loadSymmetricKekKeysKeyType, loadSymmetricKekKeysKeyUsage), MessageTag.NORMAL);
                            }catch (NSDKException e) {
                                showErrorMessage(e, String.format("Load %s %s  Key.", loadSymmetricKekKeysKeyType, loadSymmetricKekKeysKeyUsage));
                            }
                        }
                        if(rbLoadSymmKeys.isChecked()) {
                            try {
                                mKeyManager.getKeyInfo(KeyInfoID.KCV, sourceKey);
                            }catch (NSDKException e) {
                                showErrorMessage(e, "Get KEK Info");
                                showMessage(context.getString(R.string.tv_pin_inject_kek_check), MessageTag.ERROR);
                            }


                            EditText etLoadKeysKeyData = view.findViewById(R.id.spn_keymanager_loadSymmKeyByKEK_keyData);
                            EditText etKcvData = view.findViewById(R.id.et_keymanager_loadSymmKeyByKEK_kcvData);
                            String kcvData = etKcvData.getText().toString();
                            byte[] loadSymmetricKeysKeyData = ISOUtils.hex2byte(etLoadKeysKeyData.getText().toString());
                            EditText etKsnData = view.findViewById(R.id.et_keymanager_loadSymmKeyByKEK_ksnData);
                            String ksnData = etKsnData.getText().toString();
                            mEditor.putString(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEYS_KSN_DATA, ksnData);
                            KEYData = etLoadKeysKeyData.getText().toString();
                            mEditor.putString(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEYS_KEY_DATA, KEYData);
                            mEditor.commit();
                            byte[] kcv;
                            byte[] ksn;

                            desKey.setKeyType(loadSymmetricKeysKeyType);
                            desKey.setKeyID(defaultKeyByKEKID);
                            desKey.setKeyUsage(loadSymmetricKeysKeyUsage);

                            desKey.setKeyData(loadSymmetricKeysKeyData);
                            desKey.setKeyLen(loadSymmetricKeysKeyLen);
                            desKey.setKCVMode(loadSymmetricKeysKcvMode);
                            if(loadSymmetricKeysKcvMode != KCVMode.NONE || !kcvData.isEmpty()) {
                                kcv = ISOUtils.hex2byte(kekKcvData);
                                desKey.setKCV(kcv);
                            }
                            if(loadSymmetricKeysKeyUsage == KeyUsage.DUKPT) {
                                ksn = ISOUtils.hex2byte(ksnData);
                                AlgorithmParameters algorithmParameters = new AlgorithmParameters();
                                algorithmParameters.setCipherMode(CipherMode.ECB);
                                DUKPTKey dukptKey = new DUKPTKey();
                                dukptKey.setKeyType(loadSymmetricKeysKeyType);
                                dukptKey.setKeyID(defaultKeyByKEKID);
                                dukptKey.setKeyUsage(loadSymmetricKeysKeyUsage);
                                dukptKey.setKSN(ksn);
                                dukptKey.setKeyData(loadSymmetricKeysKeyData);
                                dukptKey.setKeyLen(loadSymmetricKeysKeyLen);
                                dukptKey.setKCVMode(loadSymmetricKeysKcvMode);
                                try {
                                    mKeyManager.generateKey(KeyGenerateMethod.CIPHER, algorithmParameters, sourceKey, dukptKey);
                                    showMessage(String.format("Load %s %s Key by KEK success.", loadSymmetricKeysKeyType, loadSymmetricKeysKeyUsage), MessageTag.NORMAL);
                                }catch (NSDKException e) {
                                    showErrorMessage(e, String.format("Load %s %s Key by KEK.", loadSymmetricKeysKeyType, loadSymmetricKeysKeyUsage));
                                }
                            }else {
                                try {
                                    mKeyManager.generateKey(KeyGenerateMethod.CIPHER, sourceKey, desKey);
                                    showMessage(String.format("Load %s %s Key by KEK success.", loadSymmetricKeysKeyType, loadSymmetricKeysKeyUsage), MessageTag.NORMAL);
                                }catch (NSDKException e) {
                                    showErrorMessage(e, String.format("Load %s %s Key by KEK.", loadSymmetricKeysKeyType, loadSymmetricKeysKeyUsage));
                                }
                            }

                        }
                    } else {
                        load_symm_kek_Flag = 0;
                        EditText etKekKcvData = view.findViewById(R.id.et_keymanager_load_symm_kek_kcvData);
                        String kekKcvData = etKekKcvData.getText().toString();
                        mEditor.putString(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEK_KCV_DATA, kekKcvData);
                        mEditor.commit();

                        SymmetricKey sourceKey = new SymmetricKey();
                        SymmetricKey desKey = new SymmetricKey();
                        byte[] kekKcv;
                        if(!"NONE".equals(spnLoadSymmKekKcvMode.getSelectedItem().toString()) || !kekKcvData.isEmpty()) {
                            kekKcv = ISOUtils.hex2byte(kekKcvData);
                            sourceKey.setKCV(kekKcv);
                        }
                        sourceKey.setKeyUsage(loadSymmetricKekKeysKeyUsage);
                        sourceKey.setKeyID((byte) loadSymmetricKekKeysKeyID);
                        sourceKey.setKeyLen(loadSymmetricKekKeysKeyLen);
                        sourceKey.setKCVMode(KCVMode.NONE);
                        sourceKey.setKeyData(loadSymmetricKekKeysKeyData);
                        sourceKey.setKeyType(loadSymmetricKekKeysKeyType);

                        AlgorithmParameters srcAlgorithmParameters = new AlgorithmParameters();
                        srcAlgorithmParameters.setCipherMode(loadSymmKEKCipherMode);
                        srcAlgorithmParameters.setPaddingMode(loadSymmKEKPaddingMode);
                        srcAlgorithmParameters.setIV(ISOUtils.hex2byte(kekIVData));

                        if(rbLoadSymmKEKKeys.isChecked()) {
                            try {
                                mKeyManager.generateKey(KeyGenerateMethod.CLEAR, srcAlgorithmParameters, null, sourceKey);
                                showMessage(String.format("Load %s %s  Key success.", loadSymmetricKekKeysKeyType, loadSymmetricKekKeysKeyUsage), MessageTag.NORMAL);
                            }catch (NSDKException e) {
                                showErrorMessage(e, String.format("Load %s %s  Key.", loadSymmetricKekKeysKeyType, loadSymmetricKekKeysKeyUsage));
                            }
                        }

                        if(rbLoadSymmKeys.isChecked()) {
                            try {
                                mKeyManager.getKeyInfo(KeyInfoID.KCV, sourceKey);
                            }catch (NSDKException e) {
                                showErrorMessage(e, "Get KEK Info");
                                showMessage(context.getString(R.string.tv_pin_inject_kek_check), MessageTag.ERROR);
                            }
                            EditText etLoadKeysKeyData = view.findViewById(R.id.spn_keymanager_loadSymmKeyByKEK_keyData);
                            EditText etKcvData = view.findViewById(R.id.et_keymanager_loadSymmKeyByKEK_kcvData);
                            String kcvData = etKcvData.getText().toString();
                            byte[] loadSymmetricKeysKeyData = ISOUtils.hex2byte(etLoadKeysKeyData.getText().toString());
                            EditText etKsnData = view.findViewById(R.id.et_keymanager_loadSymmKeyByKEK_ksnData);
                            String ksnData = etKsnData.getText().toString();
                            mEditor.putString(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEYS_KSN_DATA, ksnData);
                            KEYData = etLoadKeysKeyData.getText().toString();
                            mEditor.putString(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOADKEYS_KEY_DATA, KEYData);
                            mEditor.commit();
                            EditText etLoadSymmKeyByKEKIVData = view.findViewById(R.id.et_keymanager_loadSymmKeyByKEK_IV_data);
                            String loadSymmKeyByKEKIVData = etLoadSymmKeyByKEKIVData.getText().toString();
                            byte[] kcv;
                            byte[] ksn;
                            AlgorithmParameters dstKeyAlgorithmParameters = new AlgorithmParameters();
                            dstKeyAlgorithmParameters.setPaddingMode(loadSymmKeyByKEKPaddingMode);
                            dstKeyAlgorithmParameters.setCipherMode(loadSymmKeyByKEKCipherMode);
                            dstKeyAlgorithmParameters.setIV(ISOUtils.hex2byte(loadSymmKeyByKEKIVData));

                            desKey.setKeyType(loadSymmetricKeysKeyType);
                            desKey.setKeyID((byte) loadSymmetricKeysKeyID);
                            desKey.setKeyUsage(loadSymmetricKeysKeyUsage);

                            desKey.setKeyData(loadSymmetricKeysKeyData);
                            desKey.setKeyLen(loadSymmetricKeysKeyLen);
                            desKey.setKCVMode(loadSymmetricKeysKcvMode);
                            if(loadSymmetricKeysKcvMode != KCVMode.NONE || !kcvData.isEmpty()) {
                                kcv = ISOUtils.hex2byte(kekKcvData);
                                desKey.setKCV(kcv);
                            }
                            if(loadSymmetricKeysKeyUsage == KeyUsage.DUKPT) {
                                ksn = ISOUtils.hex2byte(ksnData);
                                DUKPTKey dukptKey = new DUKPTKey();
                                dukptKey.setKeyType(loadSymmetricKeysKeyType);
                                dukptKey.setKeyID((byte) loadSymmetricKeysKeyID);
                                dukptKey.setKeyUsage(loadSymmetricKeysKeyUsage);
                                dukptKey.setKSN(ksn);
                                dukptKey.setKeyData(loadSymmetricKeysKeyData);
                                dukptKey.setKeyLen(loadSymmetricKeysKeyLen);
                                dukptKey.setKCVMode(loadSymmetricKeysKcvMode);
                                try {
                                    mKeyManager.generateKey(KeyGenerateMethod.CIPHER, dstKeyAlgorithmParameters, sourceKey, dukptKey);
                                    showMessage(String.format("Load %s %s Key by KEK success.", loadSymmetricKeysKeyType, loadSymmetricKeysKeyUsage), MessageTag.NORMAL);
                                }catch (NSDKException e) {
                                    showErrorMessage(e, String.format("Load %s %s Key by KEK.", loadSymmetricKeysKeyType, loadSymmetricKeysKeyUsage));
                                }
                            }else {
                                try {
                                    mKeyManager.generateKey(KeyGenerateMethod.CIPHER, dstKeyAlgorithmParameters, sourceKey, desKey);
                                    showMessage(String.format("Load %s %s Key by KEK success.", loadSymmetricKeysKeyType, loadSymmetricKeysKeyUsage), MessageTag.NORMAL);
                                }catch (NSDKException e) {
                                    showErrorMessage(e, String.format("Load %s %s Key by KEK.", loadSymmetricKeysKeyType, loadSymmetricKeysKeyUsage));
                                }
                            }

                        }
                    }

                }else if(loadAsymFlag == 1) {
                    if(swDefaultKeyID.isChecked()) {
                        if(loadAsymmetricSessionKeyFlag != 1) {
                            EditText etLoadAsymKeyKeyData = view.findViewById(R.id.et_keymanager_load_asym_keyData);
                            if(etLoadAsymKeyKeyData.getText().toString().isEmpty()) {
                                showMessage(context.getString(R.string.input_params_detect), MessageTag.ERROR);
                                return;
                            }else {
                                loadAsymmetricDatain = ISOUtils.hex2byte(etLoadAsymKeyKeyData.getText().toString());
                                mEditor.putString(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOAD_ASYM_KEY_DATA, etLoadAsymKeyKeyData.getText().toString());
                            }

                            AsymmetricKey desKey = new AsymmetricKey();
                            desKey.setKeyID(asymDefaultKeyID);
                            desKey.setKeyUsage(loadAsymmetricAsymKeyUsage);
                            desKey.setKeyType(loadAsymmetricAsymKeyType);
                            desKey.setKeyData(loadAsymmetricDatain);
                            desKey.setKeyLen(loadAsymmetricDatain.length);
                            try {
                                mKeyManager.generateKey(KeyGenerateMethod.CLEAR, null, null, desKey, null);
                                showMessage(String.format("%s %s Key is generated successfully.", loadAsymmetricAsymKeyType, loadAsymmetricAsymKeyUsage));
                            }catch (NSDKException e) {
                                showErrorMessage(e, String.format("%s %s Key is generated successfully.", loadAsymmetricAsymKeyType, loadAsymmetricAsymKeyUsage));
                                showMessage(String.format("%s %s Key generated failed.",  loadAsymmetricAsymKeyType, loadAsymmetricAsymKeyUsage), MessageTag.ERROR);
                            }
                        }else {
                            loadAsymmetricSessionKeyFlag = 0;
                            SymmetricKey desKey = new SymmetricKey();
                            desKey.setKeyID(symmOfAsymKeyID);
                            desKey.setKeyType(loadAsymmetricSymmetricKeyKeyType);
                            desKey.setKeyUsage(loadAsymmetricSymmetricKeyKeyUsage);
                            desKey.setKeyLen(loadAsymmetricSymmetricKeyKeyLen);
                            desKey.setKCVMode(loadSymmOfAsymKcvMode);
                            desKey.setKCV(loadSymmOfAsymKcvData);

                            AsymmetricKey srcKey = new AsymmetricKey();
                            srcKey.setKeyID(asymDefaultKeyID);
                            srcKey.setKeyUsage(loadAsymmetricAsymKeyUsage);
                            srcKey.setKeyType(loadAsymmetricAsymKeyType);

                            AsymAlgorithmParameters asymAlgorithmParameters = new AsymAlgorithmParameters();
                            asymAlgorithmParameters.setEncodingMode(loadAsymmetricSessionKeyAsymEncodingMode);
                            asymAlgorithmParameters.setMessageDigestType(loadAsymmetricSessionKeyMessageDigestType);

                            try {
                                byte[] result = mKeyManager.generateKeyWithAsymKey(KeyGenerateMethod.RANDOM_OUT, asymAlgorithmParameters, srcKey, desKey);
                                showMessage(String.format("Random SK: %s", ISOUtils.hexString(result)));
                            } catch (NSDKException e) {
                                e.printStackTrace();
                                showErrorMessage(e, "generate key with asym key");
                            }
                        }
                    } else {
                        EditText etLoadAsymKeyID = view.findViewById(R.id.et_keymanager_load_asym_editKeyID);
                        if(etLoadAsymKeyID.getText().toString().isEmpty()) {
                            showMessage(context.getString(R.string.input_params_detect), MessageTag.ERROR);
                            return;
                        }else {
                            loadAsymmetricKeyID = Integer.parseInt(etLoadAsymKeyID.getText().toString());
                            mEditor.putInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOAD_ASYM_KEY_ID, loadAsymmetricKeyID);
                            mEditor.commit();
                        }
                        EditText etLoadAsymKeyKeyData = view.findViewById(R.id.et_keymanager_load_asym_keyData);
                        if(etLoadAsymKeyKeyData.getText().toString().isEmpty()) {
                            showMessage(context.getString(R.string.input_params_detect), MessageTag.ERROR);
                            return;
                        }else {
                            loadAsymmetricDatain = ISOUtils.hex2byte(etLoadAsymKeyKeyData.getText().toString());
                            mEditor.putString(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOAD_ASYM_KEY_DATA, etLoadAsymKeyKeyData.getText().toString());

                        }
                        if(loadAsymmetricSessionKeyFlag != 1) {
                            AsymmetricKey desKey = new AsymmetricKey();
                            desKey.setKeyID((byte) loadAsymmetricKeyID);
                            desKey.setKeyUsage(loadAsymmetricAsymKeyUsage);
                            desKey.setKeyType(loadAsymmetricAsymKeyType);
                            desKey.setKeyData(loadAsymmetricDatain);
                            desKey.setKeyLen(loadAsymmetricDatain.length);
                            try {
                                mKeyManager.generateKey(KeyGenerateMethod.CLEAR, null, null, desKey, null);
                                showMessage(String.format("%s %s Key is generated successfully.", loadAsymmetricAsymKeyType, loadAsymmetricAsymKeyUsage));
                            }catch (NSDKException e) {
                                showErrorMessage(e, String.format("%s %s Key is generated successfully.", loadAsymmetricAsymKeyType, loadAsymmetricAsymKeyUsage));
                                showMessage(String.format("%s %s Key generated failed.",  loadAsymmetricAsymKeyType, loadAsymmetricAsymKeyUsage), MessageTag.ERROR);
                            }
                        }else {
                            loadAsymmetricSessionKeyFlag = 0;
                            EditText etLoadSymmOfAsym_KeyID = view.findViewById(R.id.et_keymanager_loadSymmOfAsym_editKeyID);
                            int load_asymmetric_symmetricKey_keyID = Integer.parseInt(etLoadSymmOfAsym_KeyID.getText().toString());
                            SymmetricKey desKey = new SymmetricKey();
                            desKey.setKeyID((byte) load_asymmetric_symmetricKey_keyID);
                            desKey.setKeyType(loadAsymmetricSymmetricKeyKeyType);
                            desKey.setKeyUsage(loadAsymmetricSymmetricKeyKeyUsage);
                            desKey.setKeyLen(loadAsymmetricSymmetricKeyKeyLen);
                            desKey.setKCVMode(loadSymmOfAsymKcvMode);
                            desKey.setKCV(loadSymmOfAsymKcvData);

                            AsymmetricKey srcKey = new AsymmetricKey();
                            srcKey.setKeyID((byte) loadAsymmetricKeyID);
                            srcKey.setKeyUsage(loadAsymmetricAsymKeyUsage);
                            srcKey.setKeyType(loadAsymmetricAsymKeyType);

                            AsymAlgorithmParameters asymAlgorithmParameters = new AsymAlgorithmParameters();
                            asymAlgorithmParameters.setEncodingMode(loadAsymmetricSessionKeyAsymEncodingMode);
                            asymAlgorithmParameters.setMessageDigestType(loadAsymmetricSessionKeyMessageDigestType);

                            try {
                                byte[] result = mKeyManager.generateKeyWithAsymKey(KeyGenerateMethod.RANDOM_OUT, asymAlgorithmParameters, srcKey, desKey);
                                showMessage(String.format("Random SK: %s", ISOUtils.hexString(result)));
                            } catch (NSDKException e) {
                                e.printStackTrace();
                                showErrorMessage(e, "generate key with asym key");
                            }
                        }
                    }


                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_load_des_hkdf, functionid = 26)
    private void loadHKDF() {
        DialogUtils.createCustomDialog(context, R.string.tv_pin_load_des_hkdf, null, R.layout.dialog_keymanager_load_hkdf, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                EditText edSN = view.findViewById(R.id.edSN);
                EditText edSalt = view.findViewById(R.id.ed_keymanager_load_hkdf_salt);
                EditText edBDKID = view.findViewById(R.id.ed_keymanager_load_hkdf_BDKID);
                EditText edHKDFKeyID = view.findViewById(R.id.ed_keymanager_load_hkdf_keyID);
                Spinner spnKDFMessageDigestType = view.findViewById(R.id.spn_keymanager_load_hkdf_messageDigestType);

                edBDKID.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOAD_HKDF_BDK_ID, "252"));
                edHKDFKeyID.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOAD_HKDF_KEY_ID, "0"));
                edSN.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOAD_HKDF_SN, ""));
                edSalt.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOAD_HKDF_SALT, ""));
                spnKDFMessageDigestType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOAD_HKDF_MESSAGE_DIGEST_TYPE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int KDFMessageDigestTypePosition = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOAD_HKDF_MESSAGE_DIGEST_TYPE, 0);
                spnKDFMessageDigestType.setSelection(KDFMessageDigestTypePosition);
            }

            @Override
            public void onResult(int id, View view) {
                EditText edSN = view.findViewById(R.id.edSN);
                EditText edSalt = view.findViewById(R.id.ed_keymanager_load_hkdf_salt);
                EditText edBDKID = view.findViewById(R.id.ed_keymanager_load_hkdf_BDKID);
                EditText edHKDFKeyID = view.findViewById(R.id.ed_keymanager_load_hkdf_keyID);
                Spinner spnKDFMessageDigestType = view.findViewById(R.id.spn_keymanager_load_hkdf_messageDigestType);

                mEditor.putString(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOAD_HKDF_BDK_ID, edBDKID.getText().toString());
                mEditor.commit();
                mEditor.putString(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOAD_HKDF_KEY_ID, edHKDFKeyID.getText().toString());
                mEditor.commit();
                mEditor.putString(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOAD_HKDF_SN, edSN.getText().toString());
                mEditor.commit();
                mEditor.putString(AppConfig.SharedPreferenceConfig.KEYMANAGER_LOAD_HKDF_SALT, edSalt.getText().toString());
                mEditor.commit();

                byte[] sn = hex2Byte(edSN.getText().toString());
                byte[] salt = null;
                if (!edSalt.getText().toString().isEmpty()) {
                    salt = hex2Byte(edSalt.getText().toString());
                }
                SymmetricKey srcKey = new SymmetricKey();
                srcKey.setKeyID((byte) Integer.parseInt(edBDKID.getText().toString()));
                srcKey.setKeyType(KeyType.DES);
                srcKey.setKeyUsage(KeyUsage.KEK);

                SymmetricKey dstKey = new SymmetricKey();
                dstKey.setKeyID((byte) Integer.parseInt(edHKDFKeyID.getText().toString()));
                dstKey.setKeyUsage(KeyUsage.KEK);
                dstKey.setKeyType(KeyType.DES);
                dstKey.setKeyLen(24);

                KDFInfo hkdfInfo = new KDFInfo();
                hkdfInfo.setKDFType(KDFType.HKDF);
                hkdfInfo.setMessageDigestType(EnumUtils.getMessageDigestType(spnKDFMessageDigestType.getSelectedItem().toString()));
                hkdfInfo.setInfo(sn);
                hkdfInfo.setSalt(salt);

                try {
                    mKeyManager.generateKeyWithHKDF(KeyGenerateMethod.HKDF, null, hkdfInfo, srcKey, dstKey);
                    showMessage("Generate Key With HKDF success.");
                }catch (NSDKException e) {
                    e.printStackTrace();
                    showErrorMessage(e, context.getString(R.string.tv_pin_load_des_hkdf));
                }
            }
        });

    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_get_symm_key_num, functionid = 27)
    private void getSymmKeyNum() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_pin_get_symm_key_num), null, R.layout.dialog_keymanager_get_symm_key_num, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View view) {
                EditText edKeyID = view.findViewById(R.id.edit_getSymmKeyNum_keyID);
                int keyID = Integer.parseInt(edKeyID.getText().toString());
                try {
                    Map<Integer, Integer> map = mKeyManager.getSymmKeyNums();
                    showMessage(String.format(Locale.US, "The installed key number of symmetric keys ID %d:%d" , keyID, (map.get(keyID) == null ? 0 : map.get(keyID))));
                } catch (NSDKException e) {
                    showErrorMessage(e, context.getString(R.string.tv_pin_get_symm_key_num));
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_get_symm_key_by_id, functionid = 28)
    private void getSymmKeyInfoById() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_pin_get_symm_key_by_id), null, R.layout.dialog_keymanager_get_symm_key_num, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View view) {
                EditText edKeyID = view.findViewById(R.id.edit_getSymmKeyNum_keyID);
                byte keyID = (byte) Integer.parseInt(edKeyID.getText().toString());
                try {
                    SymmetricKey[] symmetricKeys = mKeyManager.getSymmKeyInfoByID(keyID);
                    for (SymmetricKey symmetricKey : symmetricKeys) {
                        showMessage(String.format(Locale.US, "KeyType:%s, KeyUsage:%s, KCV:%s", symmetricKey.getKeyType().name(), symmetricKey.getKeyUsage().name(), ISOUtils.hexString(symmetricKey.getKCV())));
                    }
                } catch (NSDKException e) {
                    showErrorMessage(e, context.getString(R.string.tv_pin_get_symm_key_by_id));
                }
            }
        });

    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_genearte_asym_key_pair, functionid = 29)
    private void getAsymKeyPair() {
        NSDKExecutors.threadStart(new Runnable() {
            @Override
            public void run() {
                try {
                    //step1: install KT key
                    AsymAlgInfo algInfo = new AsymAlgInfo();
                    algInfo.setUnBit(2048);
                    algInfo.setUcRSAPubExp(ISOUtils.hex2byte("0100000001"));
                    AsymmetricKey ktKey = new AsymmetricKey();
                    ktKey.setKeyID((byte) 241);
                    ktKey.setKeyUsage(AsymKeyUsage.KEY_DISTRIBUTION);
                    ktKey.setKeyType(AsymKeyType.RSA);
                    mKeyManager.generateAsymKey(ktKey, algInfo);
                    //step2:get KT key public key for dataKey
                    byte[] kt_publicKey = mKeyManager.getKeyInfo(KeyInfoID.PUBLIC_KEY, ktKey);

                    //step3: set KT public key into dataKey and install dataKey.
                    AsymmetricKey dataKey = new AsymmetricKey();
                    dataKey.setKeyID((byte) 240);
                    dataKey.setKeyUsage(AsymKeyUsage.DATA);
                    dataKey.setKeyType(AsymKeyType.RSA);
                    dataKey.setKeyData(kt_publicKey);
                    dataKey.setKeyLen(kt_publicKey.length);
                    mKeyManager.generateKey(KeyGenerateMethod.CLEAR, null, null, dataKey, null);
                    showMessage("DATA key generated");

                    //step4:Encrypt KEK data
                    AsymAlgorithmParameters cryptoParams = new AsymAlgorithmParameters();
                    cryptoParams.setCryptoMode(AsymCryptoMode.PUBLIC);
                    cryptoParams.setEncodingMode(AsymEncodingMode.PKCS_V21);
                    cryptoParams.setMessageDigestType(MessageDigestType.SHA256);
                    Crypto crypto = (Crypto)moduleManager.getModule(ModuleType.CRYPTO);
                    byte[] encryptedData = crypto.encryptAsym(dataKey, cryptoParams, ISOUtils.hex2byte("11111111111111111111111111111111"));

                    //step5:Use encrypted data and KT key to derive a symmetric key.
                    AsymAlgorithmParameters asymAlgorithmParameters = new AsymAlgorithmParameters();
                    asymAlgorithmParameters.setEncodingMode(AsymEncodingMode.PKCS_V21);
                    asymAlgorithmParameters.setMessageDigestType(MessageDigestType.SHA256);
                    SymmetricKey symmetricKey = new SymmetricKey();
                    symmetricKey.setKeyID((byte) 222);
                    symmetricKey.setKeyUsage(KeyUsage.KEK);
                    symmetricKey.setKeyType(KeyType.DES);
                    symmetricKey.setKeyData(encryptedData);
                    symmetricKey.setKeyLen(encryptedData.length);
                    mKeyManager.generateKeyWithAsymKey(KeyGenerateMethod.CIPHER, asymAlgorithmParameters, ktKey, symmetricKey);
                    showMessage("Generate symmetric KEK key success.");


                } catch (NSDKException e) {
                    e.printStackTrace();
                    showErrorMessage(e, context.getString(R.string.tv_pin_genearte_asym_key_pair));
                }
            }
        });

    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_clear_symmetric_keys, functionid = 30)
    private void clearSymmetricKeys() {
        try {
            mKeyManager.clearSymmetricKeys();
            showMessage(context.getString(R.string.tv_pin_clear_symmetric_keys));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.tv_pin_clear_symmetric_keys));
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_load_ecc_key, functionid = 31)
    private void loadECCKey() {
        byte[] TR31KEK = new byte[] {0x11,0x11,0x11,0x11,0x11,0x11,0x11,0x11,0x11,0x11,0x11,0x11,0x11,0x11,0x11,0x11,0x11,0x11,0x11,0x11,0x11,0x11,0x11,0x11,0x11,0x11,0x11,0x11,0x11,0x11,0x11,0x11};
        byte[] ECCTR31 = "D0304S2EN01N0000DE52A6F4B2E15DA3A9E8A81F877505ECB8912048CA3F268601491046EA7F728E555D496AF13C452169E05EC00533F3A98041AA84B408D939A18D292D41098F0793EFCD52948C1330C6AE4B5F298A891932CC9C8A3AABE07D881B1F6CF82B36792CDB2EB9705BCE0F9AEC436168F305AB1D1A87D2A23CAFB94ACA4B5A8CF1EC487E900CBF53DF5C3E71C82AEAB22035BE".getBytes(StandardCharsets.UTF_8);
        byte[] ECCDER = new byte[] {0x30,0x77,0x02,0x01,0x01,0x04,0x20, (byte) 0x82,0x6D,0x17, (byte) 0xE5,0x07,0x67, (byte) 0xB1,0x65, (byte) 0xB0, (byte) 0xE4, (byte) 0xD9, (byte) 0xE3,0x32, (byte) 0xF8, (byte) 0xD1, (byte) 0xD1, (byte) 0xE2,0x02,0x24,0x28,0x4F, (byte) 0xB4, (byte) 0xDA, (byte) 0xF1, (byte) 0xE5,0x0A,0x03,0x24,0x6E,0x70,0x79,0x7D, (byte) 0xA0,0x0A,0x06,0x08,0x2A, (byte) 0x86,0x48, (byte) 0xCE,0x3D,0x03,0x01,0x07, (byte) 0xA1,0x44,0x03,0x42,0x00,0x04,0x72,0x1C, (byte) 0x97, (byte) 0x8F, (byte) 0xCE, (byte) 0xBD, (byte) 0xCD, (byte) 0xF9, (byte) 0x8A, (byte) 0x85,0x18, (byte) 0xBD, (byte) 0xC4, (byte) 0xFE, (byte) 0xDF, (byte) 0xD8,0x02, (byte) 0xB4, (byte) 0xEE,0x41,0x28, (byte) 0xE2,0x51,0x3B,0x66,0x55, (byte) 0x93,0x37,0x5E,0x23, (byte) 0x87, (byte) 0x86,0x01,0x4E,0x7C, (byte) 0xBE, (byte) 0x85,0x11, (byte) 0x91,0x5D, (byte) 0xC5,0x33,0x7A, (byte) 0xF5,0x7D, (byte) 0xCD,0x24, (byte) 0x8F,0x26,0x53, (byte) 0xC7, (byte) 0xA6, (byte) 0xAA, (byte) 0xAE, (byte) 0xE6, (byte) 0x91,0x30, (byte) 0x96, (byte) 0xFD,0x71, (byte) 0xC8,0x5B, (byte) 0xC4, (byte) 0xD7};

        try {
            SymmetricKey tr31KEKKey = new SymmetricKey();
            tr31KEKKey.setKeyID((byte) 10);
            tr31KEKKey.setKeyType(KeyType.AES);
            tr31KEKKey.setKeyUsage(KeyUsage.TR31_KEK);
            tr31KEKKey.setKeyData(TR31KEK);
            tr31KEKKey.setKeyLen(TR31KEK.length);

            mKeyManager.generateKey(KeyGenerateMethod.CLEAR, null, tr31KEKKey);
            showMessage("Generate TR31 KEK key success.");

            AsymmetricKey eccKey = new AsymmetricKey();
            eccKey.setKeyID((byte) 11);
            eccKey.setKeyType(AsymKeyType.ECC);
            eccKey.setKeyUsage(AsymKeyUsage.AUTH_DATA);
            eccKey.setKeyData(ECCTR31);
            eccKey.setKeyLen(ECCTR31.length);

            mKeyManager.generateKey(KeyGenerateMethod.TR31, tr31KEKKey, eccKey);
            showMessage("Generate ECC key by TR31 KEK key success.");

            AsymmetricKey clearEccKey = new AsymmetricKey();
            clearEccKey.setKeyID((byte) 15);
            clearEccKey.setKeyType(AsymKeyType.ECC);
            clearEccKey.setKeyUsage(AsymKeyUsage.AUTH_DATA);
            clearEccKey.setKeyData(ECCDER);
            clearEccKey.setKeyLen(ECCDER.length);

            mKeyManager.generateKey(KeyGenerateMethod.CLEAR, null, clearEccKey);
            showMessage("Generate ECC key in clear mode success.");
        } catch (NSDKException e) {
            showMessage(e.getMessage(), MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_load_hmac_key, functionid = 32)
    private void loadHMACKey() {
        byte[] HAMCKey = ISOUtils.hex2byte("0000000000000000111111111111111122222222222222223333333333333333444444444444444455555555555555556666666666666666777777777777777788888888888888889999999999999999AAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBCCCCCCCCCCCCCCCCDDDDDDDDDDDDDDDDEEEEEEEEEEEEEEEEFFFFFFFFFFFFFFFF");
        byte[] hmacTR31 = "D0112M7HC01E00005C9964E405CE7AB43A5C07567CFF686FA2D38AA440FB88DB14B77E0D667BC24A154F9B7AF7BBE3AD28D968B63F35AE3E".getBytes(StandardCharsets.UTF_8);

        try {
            SymmetricKey hmacKey = new SymmetricKey();
            hmacKey.setKeyID(AppConfig.Keys.HMAC_KEY_ID);
            hmacKey.setKeyType(KeyType.HMAC);
            hmacKey.setKeyUsage(KeyUsage.MAC);
            hmacKey.setKeyData(HAMCKey);
            hmacKey.setKeyLen(HAMCKey.length);
            mKeyManager.generateKey(KeyGenerateMethod.CLEAR, null, hmacKey);
            showMessage("Load HMACKey success.");

            SymmetricKey tr31KEKKey = new SymmetricKey();
            tr31KEKKey.setKeyID((byte) 10);
            tr31KEKKey.setKeyType(KeyType.AES);
            tr31KEKKey.setKeyUsage(KeyUsage.TR31_KEK);
            SymmetricKey hmacTR31Key = new SymmetricKey();
            hmacTR31Key.setKeyID(AppConfig.Keys.TR31_HMAC_KEY);
            hmacTR31Key.setKeyType(KeyType.HMAC);
            hmacTR31Key.setKeyUsage(KeyUsage.MAC);
            hmacTR31Key.setKeyData(hmacTR31);
            hmacTR31Key.setKeyLen(hmacTR31.length);
            mKeyManager.generateKey(KeyGenerateMethod.TR31, tr31KEKKey, hmacTR31Key);
            showMessage("Load hamcKey with TR31 KEK key success.");
        } catch (NSDKException e) {
            showMessage(e.getMessage(), MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_load_key_with_symm_key, functionid = 33)
    private void loadKeyWithSymmKey() {
        SymmetricKey srcKey = new SymmetricKey();
        srcKey.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_MK);
        srcKey.setKeyType(KeyType.DES);
        srcKey.setKeyUsage(KeyUsage.KEK);
        srcKey.setKCVMode(KCVMode.NONE);
        srcKey.setKeyData(ISOUtils.hex2byte("000000000000000000000000000000000000000000000001"));
        srcKey.setKeyLen(24);

        AlgorithmParameters algorithmParameters = new AlgorithmParameters();
        algorithmParameters.setCipherMode(CipherMode.ECB);
        algorithmParameters.setPaddingMode(PaddingMode.NONE);

        try {
            mKeyManager.generateKey(KeyGenerateMethod.CLEAR, algorithmParameters, null, srcKey);
            showMessage("DES KEK generated successful.");
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }

        SymmetricKey dstKey = new SymmetricKey();
        dstKey.setKeyID((byte) 18);
        dstKey.setKeyType(KeyType.DES);
        dstKey.setKeyUsage(KeyUsage.PIN);
        dstKey.setKCVMode(KCVMode.NONE);
        dstKey.setKeyLen(16);
        try {
            byte[] randomKeyData = mKeyManager.generateKeyWithSymmKey(KeyGenerateMethod.RANDOM_OUT, algorithmParameters, srcKey, dstKey);
            showMessage("Random DES PIN key generated successful.");
            showMessage("Random Key Data: " + (randomKeyData == null ? null : ISOUtils.hexString(randomKeyData)));
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }


    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_load_pup_2_key, functionid = 34)
    private void loadPUP2Key() {
        byte[] TKIData = ISOUtils.hex2byte("B8CB7CE32059B949C8768EE384166A62");
        byte[] TKIKcvData = ISOUtils.hex2byte("E3B4C0");
        byte[] KTKData = "B0080K0TD00N00005186474F0BCC0ADCC841ACEF14A9D2321F0F2FE035CBC03CD5C4F5F078B994C5".getBytes(StandardCharsets.UTF_8);
        byte[] KTKKcvData = ISOUtils.hex2byte("C681DC");
        byte[] KTPINData = "B0080P0TE00N00000CBFC2CAE0A5727CBB400C93C63EAD90BF475CE530451A5E3C3BA060783DF436".getBytes(StandardCharsets.UTF_8);
        byte[] KTPINKcvData = ISOUtils.hex2byte("3AD9F1");
        byte[] KTMACData = "B0080M3TC00N0000E557EE2523064025509FA59CD9310A44CEF69E4A1FDF1EAA23AB4451CF4D32DB".getBytes(StandardCharsets.UTF_8);
        byte[] KTMACKcvData = ISOUtils.hex2byte("0664C1");

        //method CLEAR : 45 KEK
        SymmetricKey kekKey = new SymmetricKey();
        kekKey.setKeyType(KeyType.DES);
        kekKey.setKeyUsage(KeyUsage.KEK);
        kekKey.setKeyID((byte) 45);
        kekKey.setKeyData(TKIData);
        kekKey.setKeyLen(TKIData.length);
        kekKey.setKCVMode(KCVMode.ZERO);
        kekKey.setKCV(TKIKcvData);

        try {
            mKeyManager.generateKey(KeyGenerateMethod.CLEAR, null, kekKey);
            showMessage("Generate TKI success.");
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }

        //method PUP_2 : 45 KEK -> 47 KEK
        SymmetricKey generateKekKey = new SymmetricKey();
        generateKekKey.setKeyID((byte) 47);
        generateKekKey.setKeyType(KeyType.DES);
        generateKekKey.setKeyUsage(KeyUsage.KEK);
        generateKekKey.setKeyData(KTKData);
        generateKekKey.setKeyLen(KTKData.length);
        generateKekKey.setKCVMode(KCVMode.ZERO);
        generateKekKey.setKCV(KTKKcvData);

        try {
            mKeyManager.generateKey(KeyGenerateMethod.PUP_2, kekKey, generateKekKey);
            showMessage("Generate KTK with TKI by PUP_2 method success.");
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }

        //method PUP_2: 47 KEK -> 48 PIN KEK
        SymmetricKey pinKekKey = new SymmetricKey();
        pinKekKey.setKeyType(KeyType.DES);
        pinKekKey.setKeyUsage(KeyUsage.PIN_KEK);
        pinKekKey.setKeyID((byte) 48);
        pinKekKey.setKeyData(KTPINData);
        pinKekKey.setKeyLen(KTPINData.length);
        pinKekKey.setKCVMode(KCVMode.ZERO);
        pinKekKey.setKCV(KTPINKcvData);

        try {
            mKeyManager.generateKey(KeyGenerateMethod.PUP_2, generateKekKey, pinKekKey);
            showMessage("Generate PIN_KEK key with generated KEK key by PUP_2 method success.");
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }

        //method PUP_2: 47 KEK -> 49 MAC
        SymmetricKey macKey = new SymmetricKey();
        macKey.setKeyID((byte) 49);
        macKey.setKeyType(KeyType.DES);
        macKey.setKeyUsage(KeyUsage.MAC);
        macKey.setKeyData(KTMACData);
        macKey.setKeyLen(KTMACData.length);
        macKey.setKCVMode(KCVMode.ZERO);
        macKey.setKCV(KTMACKcvData);

        try {
            mKeyManager.generateKey(KeyGenerateMethod.PUP_2, generateKekKey, macKey);
            showMessage("Generate MAC key with generated KEK key by PUP_2 method success.");
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }

        //method DIVERSITY_X: 48 PIN_KEK -> 35 PIN
        SymmetricKey pinKey = new SymmetricKey();
        pinKey.setKeyID((byte) 35);
        pinKey.setKeyType(KeyType.DES);
        pinKey.setKeyUsage(KeyUsage.PIN);
        pinKey.setKeyData(ISOUtils.hex2byte("BBCD3993939823894432C66C6C67DC76"));
        pinKey.setKeyLen(16);
        pinKey.setKCVMode(KCVMode.NONE);

        try {
            mKeyManager.generateKey(KeyGenerateMethod.DIVERSIFY_X, pinKekKey, pinKey);
            showMessage("Generate PIN key with PIN_KEK key success.");
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_key_export, functionid = 35)
    private void exportKey() {
        SymmetricKey kekKey = new SymmetricKey();
        kekKey.setKeyID((byte) 6);
        kekKey.setKeyType(KeyType.AES);
        kekKey.setKeyUsage(KeyUsage.TR31_KEK);
        kekKey.setKeyData(ISOUtils.hex2byte("3232323232323232323232323232323232323232323232323232323232323232"));
        kekKey.setKeyLen(32);
        try {
            mKeyManager.generateKey(KeyGenerateMethod.CLEAR, null, kekKey);
            showMessage("Generate KEK key success.");
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
            return;
        }

        SymmetricKey exportedKey = new SymmetricKey();
        exportedKey.setKeyID(AppConfig.Keys.MKSK_AES_INDEX_WK_DATA);
        exportedKey.setKeyType(KeyType.AES);
        exportedKey.setKeyUsage(KeyUsage.DATA);

        try {
            byte[] exportedKeyData = mKeyManager.exportKey(ExportMode.ANSI_X9143, kekKey, exportedKey, null);
            showMessage("Exported key data: " + ISOUtils.hexString(exportedKeyData));
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }

    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_load_pubkey, functionid = 36)
    private void injectPubKey() {
        byte[] xmlData = ISOUtils.hex2byte("3C6570323A6D657373616765207370656376657273696F6E3D22303832302220786D6C6E733A6570323D22687474703A2F2F7777772E656674706F73323030302E6368223E0A20203C6570323A636667646174616E7466206D73676E756D3D223235353233333034223E0A202020203C6570323A6163643E0A2020202020203C6570323A416371417574685075624B65793E4141414141414143456A414341514541413558646D3959496B4762336F494A6774394C793356394E37372F477A33743969492F77364B674C6E5A5A7038376839767857744E594137424C58594A454C4E38596C66564E37684B75696B61394173336A376F41355532486B7356413249713634506577366C536A6E555038626A55634142517A5A3347562B4862327541733741437842447A4D4F48613639356B5642386B5A594764674C47622B50464C596F427432626832336F78315967765879684B487336664978416B68646454396B61746758796F2F7A694B76613349396174446E62726D67494F6C764442567762462B3533494A5A4E41417773656774415237674B3635314D497844664B74524461586133717553736246484B5952554E5264464662722B72496E2B305367734B754B55434B5938466D31776F742B2B6E3668516C72753547396345465870794D75467A6B504D314A633168786F4C652B4971344371543842414148726E5A6C566E304B6E6C79782F6F57375969373246524573774661626257727668354D2B415A765A4767673D3D3C2F6570323A416371417574685075624B65793E0A2020202020203C6570323A416371445375625075624B65793E4141414141414146456A414341514541412B416A4C71616A6B6B656332464F66426C4C4A72467244776B61527143784A7430716F584E45587463496842514F4547586B674C4A6D6F444F30324A5A4C34534E74353055412F793338416249667357566B6B46664A4D793649757A7A6D724D346545576F51546E596D71326979744557754555733437654C4E666235783857486538564777364E455247315A76663130635452497437423644334539334B6D2B5445686C2F6454627348643952764C6B6C72312F2F4F58466358512B353344596935465A6262327573534E427038486651756A444431522F654B6979496A6C435546474137567562374675363457366E755A6633747874774D583365764A4D756F45327545573034384F6E6B374B6A7452774757516D307338376B2B6250756378535045444978596E3275573472414343576852522F45746B61716252444E6B354841455651335A566B6276362F623659537074734241414878364A376378634B476435664E456F6937585250336941314B41592F5245334257486E3234534C5A3938773D3D3C2F6570323A416371445375625075624B65793E0A2020202020203C6570323A41637149443E303C2F6570323A41637149443E0A2020202020203C6570323A416371496E69496E743E3430303C2F6570323A416371496E69496E743E0A2020202020203C6570323A416371496E6954696D653E3033303030303C2F6570323A416371496E6954696D653E0A2020202020203C6570323A4163714E616D653E536574203120546573742053797374656D3C2F6570323A4163714E616D653E0A2020202020203C6570323A4163715443444F4C466F6C6C6F773E6E7749477841616648416A42426C387141706F447777616649514D3D3C2F6570323A4163715443444F4C466F6C6C6F773E0A2020202020203C6570323A4163715443444F4C5265733E6E7749476E78774977515A664B674B6141384D476E7945443C2F6570323A4163715443444F4C5265733E0A2020202020203C6570323A436F6D6D41646472416371417574685372763E0A20202020202020203C6570323A496E7465726E6574416464723E7465737473797374656D2E6570322E63683C2F6570323A496E7465726E6574416464723E0A20202020202020203C6570323A496E7465726E6574506F72744E6F3E353930363C2F6570323A496E7465726E6574506F72744E6F3E0A2020202020203C2F6570323A436F6D6D41646472416371417574685372763E0A2020202020203C6570323A436F6D6D41646472416371445375625372763E0A20202020202020203C6570323A496E7465726E6574416464723E7465737473797374656D2E6570322E63683C2F6570323A496E7465726E6574416464723E0A20202020202020203C6570323A496E7465726E6574506F72744E6F3E353935363C2F6570323A496E7465726E6574506F72744E6F3E0A2020202020203C2F6570323A436F6D6D41646472416371445375625372763E0A2020202020203C6570323A526576526574727944656C3E31303C2F6570323A526576526574727944656C3E0A2020202020203C6570323A4B657950414E526374444F4C3E6E7945446D674F6648416966515154424270384742773D3D3C2F6570323A4B657950414E526374444F4C3E0A2020202020203C6570323A4B657950414E526374444F4C496E643E41413D3D3C2F6570323A4B657950414E526374444F4C496E643E0A2020202020203C6570323A4B657950414E526374496E643E41513D3D3C2F6570323A4B657950414E526374496E643E0A2020202020203C6570323A4B657950414E52637454726D426C6F636B3E44303131323939485830304E303030304136463637343731453733424230383945333344393345453046463041364334343935443639444230363846314138383036454630384641413136444545313737363142444630333133393536353939463233433430443133374145374333353C2F6570323A4B657950414E52637454726D426C6F636B3E0A202020203C2F6570323A6163643E0A202020203C6570323A41637149443E303C2F6570323A41637149443E0A202020203C6570323A54726D49443E32363139373032343C2F6570323A54726D49443E0A20203C2F6570323A636667646174616E74663E0A3C2F6570323A6D6573736167653E");
        byte[] macData = ISOUtils.hex2byte("67242DF093822268064BA704651FF249CA15D70463E352D044B6D22AE3612BCF");
        byte[] keyData = new byte[16];
        Arrays.fill(keyData, (byte) 0x31);
        SymmetricKey kekKey = new SymmetricKey();
        kekKey.setKeyID((byte) 5);
        kekKey.setKeyType(KeyType.AES);
        kekKey.setKeyUsage(KeyUsage.KEK);
        kekKey.setKeyData(keyData);
        kekKey.setKeyLen(keyData.length);

        try {
            mKeyManager.generateKey(KeyGenerateMethod.CLEAR, null, kekKey);
            showMessage("Generate AES kek key success.");
        } catch (NSDKException e) {
            if (e.getCode() == ErrorCode.SEC_CFG_UNIQUE) {
                showMessage("already exists target key, not need to install again.", MessageTag.TIP);
            } else {
                showErrorMessage(e, e.getMessage());
                return;
            }
        }

        KDFInfo kdfInfo = new KDFInfo();
        kdfInfo.setKDFType(KDFType.ONLY_EXPAND);
        kdfInfo.setMessageDigestType(MessageDigestType.SHA256);
        kdfInfo.setInfo(ISOUtils.hex2byte("32323232323232323232323232323232"));
        kdfInfo.setUcInfoLen((byte) 16);
        kdfInfo.setSalt(ISOUtils.hex2byte("35353535353535353535353535353535"));
        kdfInfo.setUcSaltLen((byte) 16);

        SymmetricKey macKey = new SymmetricKey();
        macKey.setKeyID((byte) 5);
        macKey.setKeyType(KeyType.HMAC);
        macKey.setKeyUsage(KeyUsage.MAC);
        macKey.setKeyLen(16);

        try {
            mKeyManager.generateKeyWithHKDF(KeyGenerateMethod.HKDF, null, kdfInfo, kekKey, macKey);
            showMessage("Generate AES KDF key with AES KEK key success.");
        } catch (NSDKException e) {
            if (e.getCode() == ErrorCode.SEC_CFG_UNIQUE) {
                showMessage("already exists target key, not need to install again.", MessageTag.TIP);
            } else {
                showErrorMessage(e, e.getMessage());
                return;
            }
        }

        MACVerifyParameters macVerifyParameters = new MACVerifyParameters();
        macVerifyParameters.setMacData(macData);
        macVerifyParameters.setMacType(MACType.HMAC_SHA256);
        macVerifyParameters.setMacKeyInfo(macKey);

        Map<AsymmetricKey, String> injectPubKeyMap = new HashMap<>();

        AsymmetricKey pubKey1 = new AsymmetricKey();
        pubKey1.setKeyID((byte) 1);
        pubKey1.setKeyType(AsymKeyType.RSA);
        pubKey1.setKeyUsage(AsymKeyUsage.KEY_DISTRIBUTION);
        injectPubKeyMap.put(pubKey1, "AcqAuthPubKey");

        AsymmetricKey pubKey2 = new AsymmetricKey();
        pubKey2.setKeyID((byte) 2);
        pubKey2.setKeyType(AsymKeyType.RSA);
        pubKey2.setKeyUsage(AsymKeyUsage.KEY_DISTRIBUTION);
        injectPubKeyMap.put(pubKey2, "AcqDSubPubKey");

        try {
            Map<Byte, Boolean> injectionResult = mKeyManager.injectPubKey(injectPubKeyMap, macVerifyParameters, xmlData, null);
            for (Map.Entry<Byte, Boolean> entry : injectionResult.entrySet()) {
                byte keyId = entry.getKey();
                boolean isSuccess = entry.getValue();
                showMessage(String.format(Locale.US, "Inject public key ID:%d, inject result:%b", keyId, isSuccess));
            }

        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }


    }

    //Transfer encoding mode from UTF_8 to ASCII, used before HKDF derivate Keys.
    private byte[] hex2Byte(String fmt) {
        if (fmt != null) {
            byte[] before = fmt.getBytes(StandardCharsets.US_ASCII);
            byte[] after = new byte[before.length];

            for (int i = 0; i < before.length; i++) {
                System.arraycopy(before, i, after, i, 1);
            }
            return after;
        }
        return null;
    }


}
