package com.newland.nsdkdemo.external.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.newland.nsdk.core.api.common.crypto.AsymEncodingMode;
import com.newland.nsdk.core.api.common.crypto.KCVMode;
import com.newland.nsdk.core.api.common.crypto.MessageDigestType;
import com.newland.nsdk.core.api.common.crypto.PaddingMode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.keymanager.AsymAlgInfo;
import com.newland.nsdk.core.api.common.keymanager.AsymKeyType;
import com.newland.nsdk.core.api.common.keymanager.AsymKeyUsage;
import com.newland.nsdk.core.api.common.keymanager.AsymmetricKey;
import com.newland.nsdk.core.api.common.keymanager.CipherMode;
import com.newland.nsdk.core.api.common.keymanager.DUKPTKey;
import com.newland.nsdk.core.api.common.keymanager.KeyGenerateMethod;
import com.newland.nsdk.core.api.common.keymanager.KeyInfoID;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.api.common.keymanager.KeyUsage;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.external.keymanager.ExtKeyManager;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.AppConfig;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.annotation.MethodGridEntity;
import com.newland.nsdkdemo.common.utils.DialogUtils;
import com.newland.nsdkdemo.common.utils.EnumUtils;
import com.newland.nsdkdemo.common.utils.FileUtils;
import com.newland.nsdkdemo.common.utils.MessageTag;

import java.util.Arrays;

public class ExtKeyManagerFragment extends ExtBaseFragment {
    private static final String TAG = "ExtKeyManagerFragment";

    private ExtKeyManager extKeyManager;
    private static final int INDEX_EXTPINPAD_LOADDESWK = 1;
    private static final int INDEX_EXTPINPAD_LOADAESWK = 2;
    private static final int INDEX_EXTPINPAD_LOADDESTR31 = 3;
    private static final int INDEX_EXTPINPAD_LOADAESTR31 = 4;
    private static final int INDEX_EXTPINPAD_LOADDUKPT = 5;
    private static final int INDEX_EXTPINPAD_GETKCV = 6;
    private static final int INDEX_EXTPINPAD_GETKSN = 7;
    private static final int INDEX_EXTPINPAD_INCREASEKSN = 8;
    private static final int INDEX_EXTPINPAD_DELETEKEY = 9;
    private static final int INDEX_RESET_CERT_STATUS = 10;
    private static final int INDEX_LOAD_TRUSTED_CERT = 11;
    private static final int INDEX_ASYM_DISTRIBUTION_KEY = 12;
    private static final int INDEX_ASYM_DATA_KEY = 13;
    private static final int INDEX_ASYM_AUTH_KEY = 14;
    private static final int INDEX_GENERATE_SK = 15;
    private static final int INDEX_GET_CERT_KEY = 16;
    private static final int INDEX_INIT_ATOMIC = 17;
    private static final int INDEX_COMMIT_ATOMIC = 18;
    private static final int INDEX_CLEAR_KEYS = 19;
    private static final int INDEX_LOAD_KEYS = 20;
    private static final int INDEX_GENERATE_ASYM_KEY = 21;
    private int loadAsymmetricSessionKeyFlag = -1;
    private int loadAsymFlag = -1;
    private int load_symm_kek_Flag = -1;
    private String KEKData;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor mEditor;

    //Load Asymmetric Keys Params
    private int load_asym_Flag = -1;
    private int load_asymmetric_keyID;
    private byte[] load_asymmetric_datain;
    private int load_asymmetric_session_key_flag = -1;
    private byte[] loadAsymmetricDatain;
    private int loadAsymmetricKeyID;
    private String KEYData;

    @SuppressLint("ValidFragment")
    public ExtKeyManagerFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_extkeymanager_f);
    }

    @Override
    public void initData() {
        extKeyManager = (ExtKeyManager) moduleManager.getModule(ModuleType.EXT_KEY_MANAGER);
        sharedPreferences = context.getSharedPreferences("KEK Params", context.MODE_PRIVATE);
        mEditor = sharedPreferences.edit();
    }

    @Override
    public Object getModule() {
        return ExtKeyManagerFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_load_des_wk, functionid = INDEX_EXTPINPAD_LOADDESWK)
    private void loadDESWorkKey() {
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
            // KeyGenerateMethod.CLEAR is used for DEV device, for PRO device, please use mother POS or RKL to load master key.
            extKeyManager.generateKey(KeyGenerateMethod.CLEAR,algorithmParameters,  null, desKey, keyBuffer);
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
            extKeyManager.generateKey(KeyGenerateMethod.CIPHER, algorithmParameters, sourceKey, desKey,null);
            showMessage("Data key loaded with DES KEK successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "load Data key with DES KEK.");
        }

        desKey.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_WK_PIN);
        desKey.setKeyData(ISOUtils.hex2byte("F679786E2411E3DEF679786E2411E3DE"));
        desKey.setKeyUsage(KeyUsage.PIN);
        try {
            extKeyManager.generateKey(KeyGenerateMethod.CIPHER, algorithmParameters, sourceKey, desKey,null);
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
            extKeyManager.generateKey(KeyGenerateMethod.CIPHER, algorithmParameters, sourceKey, desKey,null);
            showMessage("MAC key loaded with DES KEK successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "load MAC key with DES KEK.");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_load_aes_wk, functionid = INDEX_EXTPINPAD_LOADAESWK)
    private void loadAESWorkKey() {
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
            extKeyManager.generateKey(KeyGenerateMethod.CLEAR,algorithmParameters,null, desKey,null);
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
            extKeyManager.generateKey(KeyGenerateMethod.CIPHER, null,sourceKey, desKey, null);
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
        //This is needed when KCVMode is not KCVMode.NONE.
        algorithmParameters.setPaddingMode(PaddingMode.ZEROS);
        try {
            extKeyManager.generateKey(KeyGenerateMethod.CIPHER, algorithmParameters, sourceKey, desKey, null);
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
            extKeyManager.generateKey(KeyGenerateMethod.CIPHER, null,sourceKey, desKey, null);
            showMessage("PIN key loaded with AES KEK successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "load PIN key with AES KEK.");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_load_des_tr31, functionid = INDEX_EXTPINPAD_LOADDESTR31)
    private void loadDESTR31Key(){
        String masterKey = "F0000000000000000000000000000000000000000000000F";
        String keyData = "423030383844305442303045303130304b5330383030303241333645313545303041394237333744423742353842333532383943453730384438443345353639354632353930453939323734384246384139383243364539";

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
        desKey.setKeyData(ISOUtils.hex2byte(keyData));
        desKey.setKeyLen(88);
        desKey.setKCVMode(KCVMode.ZERO);
        desKey.setKCV(ISOUtils.hex2byte("E2F243"));

        try {
            extKeyManager.generateKey(KeyGenerateMethod.CLEAR, null, null, srcKey, null);
            showMessage("DES TR31 Master Key generated successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            if (e.getCode() != ErrorCode.SEC_CFG_CLEARKEY_LIMIT) {
                showErrorMessage(e, "generate DES TR31 master key");
                return;
            }
        }

        try {
            extKeyManager.generateKey(KeyGenerateMethod.TR31,null, srcKey, desKey, null);
            showMessage("DES TR31 Key generated successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "generate DES TR31 key");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_load_aes_tr31, functionid = INDEX_EXTPINPAD_LOADAESTR31)
    private void loadAESTR31Key(){

        String masterKey = "FFF1000000000000000000000000000000000000000000000000000000001FFF";
        String keyData = "44303131325030414530304E30303030334432444135373731463935413842443230313036363443323243394638323830304241373344333443354335423546454436464442443336464132374542414534323842443036384443374233313544384233413539463631443035393035";

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
        desKey.setKeyData(ISOUtils.hex2byte(keyData));
        desKey.setKeyLen(112);
        desKey.setKCVMode(KCVMode.NONE);

        try {
            extKeyManager.generateKey(KeyGenerateMethod.CLEAR, null, null, srcKey, null);
            showMessage("AES TR31 Master Key generated successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            if (e.getCode() != ErrorCode.SEC_CFG_CLEARKEY_LIMIT) {
                showErrorMessage(e, "generate AES TR31 master Key");
                return;
            }
        }

        try {
            extKeyManager.generateKey(KeyGenerateMethod.TR31,null, srcKey, desKey, null);
            showMessage("AES TR31 Key generated successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "generate AES TR31 Key");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_load_dukpt, functionid = INDEX_EXTPINPAD_LOADDUKPT)
    private void loadDukptKey() {
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
            extKeyManager.generateKey(KeyGenerateMethod.CIPHER, algorithmParameters, sourceKey, desKey, null);
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

    @MethodGridEntity(btnnameid = R.string.dialog_tv_keymanager_get_keyinfo, functionid = INDEX_EXTPINPAD_GETKCV)
    private void getKeyInfo() {
        DialogUtils.createCustomDialog(context, R.string.tv_pin_getkcv, null, R.layout.dialog_keymanager_get_keyinfo, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                LinearLayout llGetKeyInfoDefaultKeyIDParams = view.findViewById(R.id.linear_keymanager_getKeyInfo_defaultKeyID_params);
                LinearLayout llGetKeyInfoEditKeyIDParams = view.findViewById(R.id.linear_keymanager_getKeyInfo_editKeyID_params);
                llGetKeyInfoDefaultKeyIDParams.setVisibility(View.VISIBLE);
                llGetKeyInfoEditKeyIDParams.setVisibility(View.GONE);
                Switch swDefaultKeyID = view.findViewById(R.id.switch_default_key_id_btn);
                swDefaultKeyID.setChecked(true);
                swDefaultKeyID.setOnCheckedChangeListener(((buttonView, isChecked) -> {
                    if (swDefaultKeyID.isChecked()) {
                        llGetKeyInfoDefaultKeyIDParams.setVisibility(View.VISIBLE);
                        llGetKeyInfoEditKeyIDParams.setVisibility(View.GONE);
                    } else {
                        llGetKeyInfoDefaultKeyIDParams.setVisibility(View.GONE);
                        llGetKeyInfoEditKeyIDParams.setVisibility(View.VISIBLE);
                    }
                }));
                RadioGroup rgGetKeyinfo = view.findViewById(R.id.dialog_keymanager_get_keyInfo_radioGroup);
                RadioButton rbGetKcv = view.findViewById(R.id.keymanager_get_KCV_radio);
                RadioButton rbGetKsn = view.findViewById(R.id.keymanager_get_KSN_radio);
                rbGetKcv.setChecked(true);
                LinearLayout llGetKcvParams = view.findViewById(R.id.linear_keymanager_get_kcv_params);
                llGetKcvParams.setVisibility(View.VISIBLE);
                rgGetKeyinfo.setOnCheckedChangeListener((group, checkedId) -> {
                    if(checkedId == rbGetKcv.getId()) {
                        rbGetKcv.setChecked(true);
                        rbGetKsn.setChecked(false);
                    }

                    if(checkedId == rbGetKsn.getId()) {
                        rbGetKcv.setChecked(false);
                        rbGetKsn.setChecked(true);
                    }
                });

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
                EditText etKeyID = view.findViewById(R.id.et_keymanager_getKeyInfo_keyID);
                int getKeyInfoKeyID = Integer.parseInt(etKeyID.getText().toString());
                mEditor.putInt(AppConfig.SharedPreferenceConfig.KEYMANAGER_GET_KEYINFO_KEY_ID, getKeyInfoKeyID);
                mEditor.commit();
                Spinner spnKeyType = view.findViewById(R.id.spn_keymanager_getKeyInfo_keyType);
                Spinner spnKeyUsage = view.findViewById(R.id.spn_keymanager_getKeyInfo_keyUsage);
                Switch swDefaultKeyID = view.findViewById(R.id.switch_default_key_id_btn);

                KeyType keyType = EnumUtils.getKeyType(spnKeyType.getSelectedItem().toString());
                AsymKeyType asymKeyType = EnumUtils.getAsymKeyType(spnKeyType.getSelectedItem().toString());
                KeyUsage keyUsage = EnumUtils.getKeyUsage(spnKeyUsage.getSelectedItem().toString());
                AsymKeyUsage asymKeyUsage = EnumUtils.getAsymKeyUsage(spnKeyUsage.getSelectedItem().toString());
                RadioButton rbGetKcv = view.findViewById(R.id.keymanager_get_KCV_radio);
                RadioButton rbGetKsn = view.findViewById(R.id.keymanager_get_KSN_radio);
                Spinner spnDefaultKeyInfoID = view.findViewById(R.id.spn_keymanager_getKeyInfo_defaultKeyID);
                byte defaultKeyID = EnumUtils.getDefaultKeyID(spnDefaultKeyInfoID.getSelectedItem().toString());
                if (swDefaultKeyID.isChecked()) {
                    if(rbGetKcv.isChecked()) {
                        if (spnKeyType.getSelectedItem().toString().contains("ASYM")) {
                            AsymmetricKey asDesKey = new AsymmetricKey();
                            asDesKey.setKeyID(defaultKeyID);
                            asDesKey.setKeyUsage(asymKeyUsage);
                            asDesKey.setKeyType(asymKeyType);
                            byte[] result = null;
                            try {
                                result = extKeyManager.getKeyInfo(KeyInfoID.KCV, asDesKey);
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

                            byte[] result = null;
                            try {
                                result = extKeyManager.getKeyInfo(KeyInfoID.KCV, desKey);
                                String kcvStr = result == null ? "null" : ISOUtils.hexString(result);
                                showMessage("Get KCV = " + kcvStr);
                            } catch (NSDKException e) {
                                e.printStackTrace();
                                showErrorMessage(e, context.getString(R.string.tv_pin_getkcv));
                            }
                        }

                    }else if(rbGetKsn.isChecked()) {
                        SymmetricKey desKey = new SymmetricKey();
                        desKey.setKeyUsage(keyUsage);
                        desKey.setKeyType(keyType);
                        desKey.setKeyID((byte)getKeyInfoKeyID);

                        try {
                            byte[] ksn = extKeyManager.getKeyInfo(KeyInfoID.KSN, desKey);
                            if(spnKeyType.getSelectedItem().toString().contains("AES")) {
                                showMessage("AES DUKPT KSN:" + ISOUtils.hexString(ksn));
                            }else {
                                showMessage("DUKPT KSN:" + ISOUtils.hexString(ksn));
                            }
                        }catch (NSDKException e) {
                            showErrorMessage(e, context.getString(R.string.dialog_tv_keymanager_get_keyinfo));
                        }
                    }
                } else {
                    if(rbGetKcv.isChecked()) {
                        if (spnKeyType.getSelectedItem().toString().contains("ASYM")) {
                            AsymmetricKey asDesKey = new AsymmetricKey();
                            asDesKey.setKeyID((byte) getKeyInfoKeyID);
                            asDesKey.setKeyUsage(asymKeyUsage);
                            asDesKey.setKeyType(asymKeyType);
                            byte[] result = null;
                            try {
                                result = extKeyManager.getKeyInfo(KeyInfoID.KCV, asDesKey);
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
                                result = extKeyManager.getKeyInfo(KeyInfoID.KCV, desKey);
                                String kcvStr = result == null ? "null" : ISOUtils.hexString(result);
                                showMessage("Get KCV = " + kcvStr);
                            } catch (NSDKException e) {
                                e.printStackTrace();
                                showErrorMessage(e, context.getString(R.string.tv_pin_getkcv));
                            }
                        }

                    }else if(rbGetKsn.isChecked()) {
                        SymmetricKey desKey = new SymmetricKey();
                        desKey.setKeyUsage(keyUsage);
                        desKey.setKeyType(keyType);
                        desKey.setKeyID((byte)getKeyInfoKeyID);

                        try {
                            byte[] ksn = extKeyManager.getKeyInfo(KeyInfoID.KSN, desKey);
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

    @MethodGridEntity(btnnameid = R.string.tv_pin_getdukptksn, functionid = INDEX_EXTPINPAD_GETKSN)
    private void getDUKPTKSN() {
        try {
            SymmetricKey desKey = new SymmetricKey();
            desKey.setKeyID(AppConfig.Keys.DUKPT_DES_INDEX);
            desKey.setKeyType(KeyType.DES);
            desKey.setKeyUsage(KeyUsage.DUKPT);

            extKeyManager.increaseKSN(AppConfig.Keys.DUKPT_DES_INDEX);
            byte[] ksn = extKeyManager.getKeyInfo(KeyInfoID.KSN, desKey);
            showMessage(String.format("KSN: %s", ISOUtils.hexString(ksn)));
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "increase ksn");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_increase_ksn, functionid = INDEX_EXTPINPAD_INCREASEKSN)
    private void increaseKSN() {
        try {
            SymmetricKey desKey = new SymmetricKey();
            desKey.setKeyID(AppConfig.Keys.DUKPT_DES_INDEX);
            desKey.setKeyType(KeyType.DES);
            desKey.setKeyUsage(KeyUsage.DUKPT);

            extKeyManager.increaseKSN(AppConfig.Keys.DUKPT_DES_INDEX);
            byte[] ksn = extKeyManager.getKeyInfo(KeyInfoID.KSN, desKey);
            showMessage(String.format("KSN: %s", ISOUtils.hexString(ksn)));
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "increase ksn");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_delete_key, functionid = INDEX_EXTPINPAD_DELETEKEY)
    private void deleteKey() {
        DialogUtils.createCustomDialog(context, R.string.tv_pin_delete_key, null, R.layout.dialog_keymanager_delete_key, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                LinearLayout llGetDefaultKeyInfoIDParams = view.findViewById(R.id.linear_keymanager_delete_defaultKeyID_params);
                llGetDefaultKeyInfoIDParams.setVisibility(View.VISIBLE);
                LinearLayout llGeyEditKeyInfoIDParams = view.findViewById(R.id.linear_keymanager_delete_editKeyID_params);
                llGeyEditKeyInfoIDParams.setVisibility(View.GONE);
                Switch swDefaultKeyID = view.findViewById(R.id.switch_default_key_id_btn);
                swDefaultKeyID.setChecked(true);
                swDefaultKeyID.setOnCheckedChangeListener(((buttonView, isChecked) -> {
                    if (swDefaultKeyID.isChecked()) {
                        llGetDefaultKeyInfoIDParams.setVisibility(View.VISIBLE);
                        llGeyEditKeyInfoIDParams.setVisibility(View.GONE);
                    } else {
                        llGetDefaultKeyInfoIDParams.setVisibility(View.GONE);
                        llGeyEditKeyInfoIDParams.setVisibility(View.VISIBLE);
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
                Spinner spnKeyType = view.findViewById(R.id.spn_keymanager_delete_key_keyType);
                Spinner spnKeyUsage = view.findViewById(R.id.spn_keymanager_delete_key_keyUsage);
                EditText etKeyID = view.findViewById(R.id.et_keymanager_key_index);

                KeyType keyType= EnumUtils.getKeyType(spnKeyType.getSelectedItem().toString());
                KeyUsage keyUsage = EnumUtils.getKeyUsage(spnKeyUsage.getSelectedItem().toString());
                int keyID = Integer.parseInt(etKeyID.getText().toString());
                Spinner spnDefaultKeyID = view.findViewById(R.id.spn_keymanager_delete_defaultKeyID);
                byte defaultKeyID = EnumUtils.getDefaultKeyID(spnDefaultKeyID.getSelectedItem().toString());

                SymmetricKey key = new SymmetricKey();
                if (swDefaultKeyID.isChecked()) {
                    key.setKeyID(defaultKeyID);
                    key.setKeyType(keyType);
                    key.setKeyUsage(keyUsage);

                    try {
                        extKeyManager.deleteKey(key);
                        showMessage(String.format("Delete %s %s Key Success.", spnKeyType.getSelectedItem().toString(), spnKeyUsage.getSelectedItem().toString()));
                    }catch (NSDKException e) {
                        showErrorMessage(e, context.getString(R.string.tv_pin_delete_key));
                    }
                } else {
                    key.setKeyID((byte) keyID);
                    key.setKeyType(keyType);
                    key.setKeyUsage(keyUsage);

                    try {
                        extKeyManager.deleteKey(key);
                        showMessage(String.format("Delete %s %s Key Success.", spnKeyType.getSelectedItem().toString(), spnKeyUsage.getSelectedItem().toString()));
                    }catch (NSDKException e) {
                        showErrorMessage(e, context.getString(R.string.tv_pin_delete_key));
                    }
                }
            }
        });

    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_reset_cert_status, functionid = INDEX_RESET_CERT_STATUS)
    public void resetCertStatus(){
        try {
            extKeyManager.resetCertStatus();
            showMessage("Reset cert status successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "reset cert status");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_load_trusted_cert, functionid = INDEX_LOAD_TRUSTED_CERT)
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
//            keyData = extKeyManager.loadTrustedCert(true, certCaBuf);
//            showMessage("CA cert is loaded successfully.");
//        } catch (NSDKException e) {
//            e.printStackTrace();
//            showErrorMessage(e, "load trusted CA cert");
//            return;
//        }

        try {
            keyData = extKeyManager.loadTrustedCert(false, certEncBuf);
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
            extKeyManager.generateKey(KeyGenerateMethod.CLEAR, null, null, distributionKey, certEncBuf);
            showMessage("Asym distribution key is generated successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "load asym distribution key");
            return;
        }

        try {
            keyData = extKeyManager.loadTrustedCert(false, certSignBuf);
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
            extKeyManager.generateKey(KeyGenerateMethod.CLEAR, null, null, authKey, certSignBuf);
            showMessage("Asym auth key is generated successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "load asym auth key");
            return;
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_asym_distribution_key, functionid = INDEX_ASYM_DISTRIBUTION_KEY)
    public void generateAsymDistributionKey(){
        byte[] keyValue = ISOUtils.hex2byte("308204A40201000282010100C7B433BED854C40AB66006F9F2E50FD8F70E162A1989C4468A41E1F8A6D5443B1F121F57CF711DF5239D161594DE223EBF640D3A7A9EDDE23E1CEE3332EA05B0E8E66983F510E798E259621EEB6F7DC72573C0BF879078DD34BCAF652B1142E2370ABBB4AA33A8E61F173197C0009F416FBEE0F12205906D91761D104ABEC57CBB70A8660F5990447D5115523E1618D5DD3454132CC35440F56C1DFB21D86A3D4E7BCADD957F8CBAF02DBEF75390EC3FC09C81CC3A2C2EE6B2F0AB2EB56327F254963FB5E37DD9D07F8876050E1B3C2DA891039B44DC9FF2AD321092AD07695E7198B1590778AE6A0B11078C59BE7D055C40C14BA0ACCB390C34D1F4EAA08DB7020301000102820101008F4E74178E7BFD96465B50864AD42F741D8DA14022C566F0CBC40D5976B6F1D88F2A5D0D9151F61274B50425068805010C2CCB055CFAE5F4B567E35320452942534F0D06E174790D8FD85E7E1BAB0D123C80FD1F3433EB57A9C18107D348F6BB088E8E364E30F611F4DB9AFDEF8D42BB1D6A57571277F5B57ABDCC8B6567D2A4B3D7B02EC249055210CE27E177F68E4B2D8762AED2A292D152B7147E556B746D2C5E4EB10A747CDA092773AF1BE63DE462CACC78BF404AB7645C569116E92BD1B08A7DAE9DCCEBB9F06975B53E0F7EEF87D3632654052960320EA5DA89CC860E7845D4223149C74174DEF64CFEF56533E74AEB447C9105CEF8BA4AC1A761271902818100E9D59F19698F4F88B1054911DEA48F185B24A37D1FD59DF9C86F6402933F943256660C68D89E6CE47D6C2AF73D30D43B26B4AE727B3B909A45B94544708B26A2F2CB3CE80208EAB8563E08A4C8F01CE42D9EE582237E112AEB150978F741DBB4D8F5CF8CF3EC84B2F8BEDF8515CD09F10A77D440144A25C1164AFEA857B1046302818100DAA258C411A46F1586F4CA9D721EAE91103130085152C730FF080AD09613D4E9A26F6184A1C33FC156AA51B39661B2011A4F62C2BEFE7FD3ECAFE7D4E3D01780F8497E65AC921BA4F32E405547F7C20207E4BA10C080217AB5955B0DC214390F77F68A49C4CD7F6A5F674115B5FE0051A5EB778FF775EE32D4D0D65A6A79BF9D02818073870A6AE8BF5841258C81F4653692482B47A7CA7AFEF464E3453D79143CF6400475B8ADD8503566921CEE0166E708040D747937B070D65992171E04D941B69E0D2CA273D2058BC9F53A02D53F3D7A58B5BD6A90E86E31972DBC2008F6C83FFF52ADE79431336AC06DAB080DDD9E86C9E32D1EDD65E8D84C54724AECEB1E571702818100CC7151360F154736C71AF2B865B561218F159FC7B75C039DC26D5FC78AFF0272728D5A902EBFA8477F6606BE3B1A21DCBC208DC673D901E947037A72ED9071DA0CD2949A38D3EB7BC456749C0A8F1860D3C6C362D4CF3BFF45FB62C8ABBA435D3E9D50D5086DECFEA835A27BCDD1B5B4A3C55AE949CA8232102534BC19A2BD550281806EB82AE428F6A7BD1831B21CC1DCF2BD158F7A391FFB6238323F7F0BEE67677571E9990532D1B8ADF31CBD534F073DAD5F61CC8C7B86A85B43D95A1170FD0EB38143A5B0635B7CEAA22942C802FA2A2C0DA557B8B856F3FE5DAA150CF74675A28A1D0F0CD0BAFCE5645746BF819206DDC88FD27F571F5EAD7A295E38979B9B0F");
        AsymmetricKey dstKey = new AsymmetricKey();
        dstKey.setKeyID((byte) AppConfig.Keys.ASYM_KEY_DISTRIBUTION_ID);
        dstKey.setKeyUsage(AsymKeyUsage.KEY_DISTRIBUTION);
        dstKey.setKeyType(AsymKeyType.RSA);
        dstKey.setKeyData(keyValue);
        dstKey.setKeyLen(keyValue.length);

        try {
            extKeyManager.generateKey(KeyGenerateMethod.CLEAR, null, null, dstKey, null);
            showMessage("Asym distribution key is generated successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "generate asym distribution key");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_asym_data_key, functionid = INDEX_ASYM_DATA_KEY)
    public void generateAsymDataKey(){
        byte[] keyValue = ISOUtils.hex2byte("308204A40201000282010100C7B433BED854C40AB66006F9F2E50FD8F70E162A1989C4468A41E1F8A6D5443B1F121F57CF711DF5239D161594DE223EBF640D3A7A9EDDE23E1CEE3332EA05B0E8E66983F510E798E259621EEB6F7DC72573C0BF879078DD34BCAF652B1142E2370ABBB4AA33A8E61F173197C0009F416FBEE0F12205906D91761D104ABEC57CBB70A8660F5990447D5115523E1618D5DD3454132CC35440F56C1DFB21D86A3D4E7BCADD957F8CBAF02DBEF75390EC3FC09C81CC3A2C2EE6B2F0AB2EB56327F254963FB5E37DD9D07F8876050E1B3C2DA891039B44DC9FF2AD321092AD07695E7198B1590778AE6A0B11078C59BE7D055C40C14BA0ACCB390C34D1F4EAA08DB7020301000102820101008F4E74178E7BFD96465B50864AD42F741D8DA14022C566F0CBC40D5976B6F1D88F2A5D0D9151F61274B50425068805010C2CCB055CFAE5F4B567E35320452942534F0D06E174790D8FD85E7E1BAB0D123C80FD1F3433EB57A9C18107D348F6BB088E8E364E30F611F4DB9AFDEF8D42BB1D6A57571277F5B57ABDCC8B6567D2A4B3D7B02EC249055210CE27E177F68E4B2D8762AED2A292D152B7147E556B746D2C5E4EB10A747CDA092773AF1BE63DE462CACC78BF404AB7645C569116E92BD1B08A7DAE9DCCEBB9F06975B53E0F7EEF87D3632654052960320EA5DA89CC860E7845D4223149C74174DEF64CFEF56533E74AEB447C9105CEF8BA4AC1A761271902818100E9D59F19698F4F88B1054911DEA48F185B24A37D1FD59DF9C86F6402933F943256660C68D89E6CE47D6C2AF73D30D43B26B4AE727B3B909A45B94544708B26A2F2CB3CE80208EAB8563E08A4C8F01CE42D9EE582237E112AEB150978F741DBB4D8F5CF8CF3EC84B2F8BEDF8515CD09F10A77D440144A25C1164AFEA857B1046302818100DAA258C411A46F1586F4CA9D721EAE91103130085152C730FF080AD09613D4E9A26F6184A1C33FC156AA51B39661B2011A4F62C2BEFE7FD3ECAFE7D4E3D01780F8497E65AC921BA4F32E405547F7C20207E4BA10C080217AB5955B0DC214390F77F68A49C4CD7F6A5F674115B5FE0051A5EB778FF775EE32D4D0D65A6A79BF9D02818073870A6AE8BF5841258C81F4653692482B47A7CA7AFEF464E3453D79143CF6400475B8ADD8503566921CEE0166E708040D747937B070D65992171E04D941B69E0D2CA273D2058BC9F53A02D53F3D7A58B5BD6A90E86E31972DBC2008F6C83FFF52ADE79431336AC06DAB080DDD9E86C9E32D1EDD65E8D84C54724AECEB1E571702818100CC7151360F154736C71AF2B865B561218F159FC7B75C039DC26D5FC78AFF0272728D5A902EBFA8477F6606BE3B1A21DCBC208DC673D901E947037A72ED9071DA0CD2949A38D3EB7BC456749C0A8F1860D3C6C362D4CF3BFF45FB62C8ABBA435D3E9D50D5086DECFEA835A27BCDD1B5B4A3C55AE949CA8232102534BC19A2BD550281806EB82AE428F6A7BD1831B21CC1DCF2BD158F7A391FFB6238323F7F0BEE67677571E9990532D1B8ADF31CBD534F073DAD5F61CC8C7B86A85B43D95A1170FD0EB38143A5B0635B7CEAA22942C802FA2A2C0DA557B8B856F3FE5DAA150CF74675A28A1D0F0CD0BAFCE5645746BF819206DDC88FD27F571F5EAD7A295E38979B9B0F");
        AsymmetricKey dstKey = new AsymmetricKey();
        dstKey.setKeyID((byte) AppConfig.Keys.ASYM_DATA_ID);
        dstKey.setKeyUsage(AsymKeyUsage.DATA);
        dstKey.setKeyType(AsymKeyType.RSA);
        dstKey.setKeyData(keyValue);
        dstKey.setKeyLen(keyValue.length);

        try {
            extKeyManager.generateKey(KeyGenerateMethod.CLEAR, null, null, dstKey, null);
            showMessage("Asym data key is generated successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "generate asym data key");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_asym_auth_key, functionid = INDEX_ASYM_AUTH_KEY)
    public void generateAsymAuthKey(){
        byte[] keyValue = ISOUtils.hex2byte("308204A40201000282010100C6DEE0BC45C7D2D062602F76C3FBFE5B2FB72A77217CC201B312F7FF8DAFBF2DD3B1404313A975F5F54ECE9D502FF6634468E5FD90D8A834C89AB5647A32B69BAACED6E265B2EE5A5089C58FF33A1F1D57CB2F9A4C0376E4A64EEC0D89C66562C984FCDC86A657EA4F7CC3E85AA4A4C1192597AC10F8E2A7A0EB805952A58C433C489513EEF5CF3CA8613BDF47B9FCCB6476D3DBC6E5C4BE2D392947FEFB0F92F0AAAA78F7C791251F3A4223A583A9CBD084F04CE36B166FAC121D8A1CCC02D38B4B4B88516CFFA06518FB0975F97E8E02D8ECE519A1BE41729271B98D449925DE9AF1591BD0A7D9DE4407C51081BA10FEC8017859A26DC23C02B450D9B11EF102030100010282010100BED1F50A325ABE69BD3B55CFBBD5FC063B0EA1EC95714426A5513A2D3822BE6A9689A983B346132DE227B0113A740B12CCFD6A5197BE8C07B9C4D8F084604CDFA951B6D69D86C73659B9189C3B6235A0CE30E488450FBDF13FB2D2C55AC1C75EB6C6A86A61B912FA7D32D638096199C4BF00573C7F3C911F0F45696E4BE315656B3364D5051F01467FDBD3415A498A5C821FD67232D2A8A193B94A956C26C0117D0568D28CEDA59669551DABF1B994A3F314C925133F88F31E7885162C3880C65E19D48390D5EA058384D082476B9E9229631F059C189A3BAE60EB78B3E650B3A5B77DEBABFA56D8970DBF444FF8510146195AA1BF7698CF01A31CFC74FFD7C102818100F95559F528B0892169E661EF8A1A5012905013E4F857B7EF26891E019F644A2119825DFAC7EEF099971C68B32E95BA0FEFD6E7DF8CC8FAFB7B28A743C15AD7ACD930BCBE3A497C1D3893CA4E90FE13E2D7A2EE85A3354674F02521E4B51C3A4AEB1162E01637CA9B001A6F2ECDB3BB07DCF8ABD8A0666A7CA2DE5C3A2A991D6D02818100CC301DD2DDF6B1AC0D78D5F12F0798A2B264B112802D3E81A086DA3FC896507E0C8A6B1B5E66B5ECF686121A6805FE984C60FE1C9E0503E15416364CEE3B22651DDEC0A1001F9C37ACDC640B62E5EE16D27974D5EE92EFFD5386DA9492FCCACF54BD5FA0915ED115F8991D897B2D59E09ADED149B405AC10958084514C50691502818007DD2448322F5733E1962D9293857EEF06F42F9C7224BA1D65D6BF4687D36EEF1A51DD4AF2915BAF4C6FCDF190CF921DBC8FC7A26A5B50672C1C3D224AEFE58B83122171D27ECCD653197E30FA2BB94ED74441479FBD276ABAC4410C6895EA54C0933CCE1A8549F3978E3DE179056929B753748011970956C300466263438F050281807529A2E34D53F1AD1CE9DA3113605377FFCF013FF16684B852C92E506D23BB3A28AE00396B189A894707B5398BB8ECD6ACF4F6BAAAFD8BB56ECF7406FEA7D5DB99A1287CF99A29C4549EFD94FF019A7563FE27495E24D82A4F145135F185B645F384DA6B431ED9F0B67DFD51D6E935EA48535459EB3F59F506240148B8F666E502818100904C79FC91B9C0E9BF0B949A75341EF6524A06E4FEE00A63D4FEC81C00E0849AFC2A414A07F43379DAE40038C12CFA2DF8EA3FAC89784C121736A1EE508CBC3E3235766413857CD97546446D141DC7D73684A1FC7C2F777C6ECE84CDA9D33D7588A127A87F43303473AEC72F9147C67B959949262F136CB806525D7440AE48E1");
        AsymmetricKey dstKey = new AsymmetricKey();
        dstKey.setKeyID((byte) AppConfig.Keys.ASYM_AUTH_ID);
        dstKey.setKeyUsage(AsymKeyUsage.AUTH);
        dstKey.setKeyType(AsymKeyType.RSA);
        dstKey.setKeyData(keyValue);
        dstKey.setKeyLen(keyValue.length);

        try {
            extKeyManager.generateKey(KeyGenerateMethod.CLEAR, null, null, dstKey, null);
            showMessage("Asym auth key is generated successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "generate asym auth key");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_load_session_key, functionid = INDEX_GENERATE_SK)
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
            byte[] result = extKeyManager.generateKeyWithAsymKey(KeyGenerateMethod.RANDOM_OUT, parameters, srcKey, dstKey);
            showMessage(String.format("Random SK: %s", ISOUtils.hexString(result)));
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "generate key with asym key");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_get_device_cert, functionid = INDEX_GET_CERT_KEY)
    public void getDeviceCertKey(){
        AsymmetricKey key = new AsymmetricKey();
        // Device cert shall be injected by mother POS first.
        key.setKeyID((byte) 10);
        key.setKeyUsage(AsymKeyUsage.AUTH);
        key.setKeyType(AsymKeyType.RSA);
        try {
            byte[] result = extKeyManager.getKeyInfo(KeyInfoID.CERTIFICATE, key);
            showMessage(String.format("Device cert: %s", ISOUtils.hexString(result)));
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "get device cert");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_init_atomic, functionid = INDEX_INIT_ATOMIC)
    public void initAtomic(){
        try {
            extKeyManager.initAtomic();
            showMessage("Init atomic successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "init atomic");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_commit_atomic, functionid = INDEX_COMMIT_ATOMIC)
    public void commitAtomic(){
        try {
            extKeyManager.commitAtomic(true);
            showMessage("Commit atomic successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "commit atomic");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_clear_symm_keys, functionid = INDEX_CLEAR_KEYS)
    public void clearSymmKeys(){
        try {
            showMessage("Clearing symmetric keys, please wait...");
            extKeyManager.clearSymmetricKeys();
            showMessage("Symmetric keys are cleared.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "clear symmetric keys");
        }
    }


    @MethodGridEntity(btnnameid = R.string.dialog_tv_keymanager_load_keys, functionid = INDEX_LOAD_KEYS)
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
                Spinner spnLoadSymmKeyByKEKKcvMode = view.findViewById(R.id.spn_keymanager_loadSymmKeyByKEK_kcvMode);
                spnLoadSymmKeyByKEKKcvMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                Spinner spnLoadSymmKEKKcvMode = view.findViewById(R.id.spn_keymanager_load_symm_kek_kcvMode);
                spnLoadSymmKEKKcvMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                if (!spnLoadSymmOfAsymKcvMode.getSelectedItem().toString().equals("NONE") && !etLoadSymmOfAsymKcvData.getText().toString().equals("")) {
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
                                extKeyManager.generateKey(KeyGenerateMethod.CLEAR, sourceKeyAlgorithmParameters, null, sourceKey, null);
                                showMessage(String.format("Load %s %s  Key success.", loadSymmetricKekKeysKeyType, loadSymmetricKekKeysKeyUsage), MessageTag.NORMAL);
                            }catch (NSDKException e) {
                                showErrorMessage(e, String.format("Load %s %s  Key.", loadSymmetricKekKeysKeyType, loadSymmetricKekKeysKeyUsage));
                            }
                        }
                        if(rbLoadSymmKeys.isChecked()) {
                            try {
                                extKeyManager.getKeyInfo(KeyInfoID.KCV, sourceKey);
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
                                    extKeyManager.generateKey(KeyGenerateMethod.CIPHER, algorithmParameters, sourceKey, dukptKey, null);
                                    showMessage(String.format("Load %s %s Key by KEK success.", loadSymmetricKeysKeyType, loadSymmetricKeysKeyUsage), MessageTag.NORMAL);
                                }catch (NSDKException e) {
                                    showErrorMessage(e, String.format("Load %s %s Key by KEK.", loadSymmetricKeysKeyType, loadSymmetricKeysKeyUsage));
                                }
                            }else {
                                try {
                                    extKeyManager.generateKey(KeyGenerateMethod.CIPHER, null, sourceKey, desKey, null);
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
                                extKeyManager.generateKey(KeyGenerateMethod.CLEAR, srcAlgorithmParameters, null, sourceKey, null);
                                showMessage(String.format("Load %s %s  Key success.", loadSymmetricKekKeysKeyType, loadSymmetricKekKeysKeyUsage), MessageTag.NORMAL);
                            }catch (NSDKException e) {
                                showErrorMessage(e, String.format("Load %s %s  Key.", loadSymmetricKekKeysKeyType, loadSymmetricKekKeysKeyUsage));
                            }
                        }

                        if(rbLoadSymmKeys.isChecked()) {
                            try {
                                extKeyManager.getKeyInfo(KeyInfoID.KCV, sourceKey);
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
                                    extKeyManager.generateKey(KeyGenerateMethod.CIPHER, dstKeyAlgorithmParameters, sourceKey, dukptKey, null);
                                    showMessage(String.format("Load %s %s Key by KEK success.", loadSymmetricKeysKeyType, loadSymmetricKeysKeyUsage), MessageTag.NORMAL);
                                }catch (NSDKException e) {
                                    showErrorMessage(e, String.format("Load %s %s Key by KEK.", loadSymmetricKeysKeyType, loadSymmetricKeysKeyUsage));
                                }
                            }else {
                                try {
                                    extKeyManager.generateKey(KeyGenerateMethod.CIPHER, dstKeyAlgorithmParameters, sourceKey, desKey, null);
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
                                extKeyManager.generateKey(KeyGenerateMethod.CLEAR, null, null, desKey, null);
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
                                byte[] result = extKeyManager.generateKeyWithAsymKey(KeyGenerateMethod.RANDOM_OUT, asymAlgorithmParameters, srcKey, desKey);
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
                                extKeyManager.generateKey(KeyGenerateMethod.CLEAR, null, null, desKey, null);
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
                                byte[] result = extKeyManager.generateKeyWithAsymKey(KeyGenerateMethod.RANDOM_OUT, asymAlgorithmParameters, srcKey, desKey);
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

    @MethodGridEntity(btnnameid = R.string.dialog_tv_keymanager_generate_asym_key, functionid = INDEX_GENERATE_ASYM_KEY)
    private void generateAsymKey() {
        try {
            AsymmetricKey asymmetricKey = new AsymmetricKey();
            asymmetricKey.setKeyID((byte) 74);
            asymmetricKey.setKeyType(AsymKeyType.RSA);
            asymmetricKey.setKeyUsage(AsymKeyUsage.KEY_DISTRIBUTION);
            AsymAlgInfo asymAlgInfo = new AsymAlgInfo();
            asymAlgInfo.setUnBit(2048);
            asymAlgInfo.setUcRSAPubExp(ISOUtils.hex2byte("010001"));
            extKeyManager.generateAsymKey(asymmetricKey, asymAlgInfo);
            showMessage("Generate Asym Key success");
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.dialog_tv_keymanager_generate_asym_key));
        }
    }
}