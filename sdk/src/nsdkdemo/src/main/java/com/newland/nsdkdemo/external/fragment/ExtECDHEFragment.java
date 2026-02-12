package com.newland.nsdkdemo.external.fragment;

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

import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.crypto.MessageDigestType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.keymanager.ECCType;
import com.newland.nsdk.core.api.common.keymanager.KDFInfo;
import com.newland.nsdk.core.api.common.keymanager.KDFType;
import com.newland.nsdk.core.api.common.keymanager.KeyInfoID;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.api.common.keymanager.KeyUsage;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.external.keymanager.ExtKeyManager;
import com.newland.nsdk.core.api.internal.keymanager.KeyManager;
import com.newland.nsdk.core.external.ExtECDHEImpl;
import com.newland.nsdk.core.internal.ecdhe.ECDHEImpl;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.AppConfig;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.annotation.MethodGridEntity;
import com.newland.nsdkdemo.common.utils.DialogUtils;
import com.newland.nsdkdemo.common.utils.EnumUtils;
import com.newland.nsdkdemo.internal.fragment.InternalBaseFragment;

public class ExtECDHEFragment extends ExtBaseFragment {

    private ExtECDHEImpl ecdhe;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor mEditor;
    private ExtKeyManager keyManager;
    private byte[] PublicKey = null;
    public ExtECDHEFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.ecdhe);
    }

    @Override
    public void initData() {
        ecdhe = new ExtECDHEImpl();
        keyManager = (ExtKeyManager)moduleManager.getModule(ModuleType.EXT_KEY_MANAGER);
        sharedPreferences = context.getSharedPreferences("ExtECDHE", Context.MODE_PRIVATE);
        mEditor = sharedPreferences.edit();
    }

    @Override
    public Object getModule() {
        return ExtECDHEFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.ecdhe_init, functionid = 0)
    private void init(){
       try {
           ecdhe.init();
           showMessage(context.getString(R.string.ecdhe_init));
       } catch (NSDKException e) {
           e.printStackTrace();
           showErrorMessage(e, context.getString(R.string.ecdhe_init));
       }
    }

    @MethodGridEntity(btnnameid = R.string.ecdhe_generate_key_pair, functionid = 1)
    private void generateKeyPair(){
        try {
            byte[] publicKey = ecdhe.generateKeyPair(ECCType.P_521);
            showMessage(String.format("%s: %s", context.getString(R.string.ecdhe_generate_key_pair), ISOUtils.hexString(publicKey)));
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, context.getString(R.string.ecdhe_generate_key_pair));
        }
    }

    @MethodGridEntity(btnnameid = R.string.ecdhe_generate_sk, functionid = 2)
    private void generateSK(){
        SymmetricKey sessionKey = new SymmetricKey();
        sessionKey.setKeyType(KeyType.AES);
        sessionKey.setKeyUsage(KeyUsage.TR31_KEK);
        sessionKey.setKeyID(AppConfig.Keys.RKI_ECDHE_SK_ID);
        sessionKey.setKeyLen(24);

        KDFInfo hkdfInfo = new KDFInfo();
        hkdfInfo.setKDFType(KDFType.HKDF);
        hkdfInfo.setMessageDigestType(MessageDigestType.SHA256);
        hkdfInfo.setSalt(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08});

        byte[] publicKey = ISOUtils.hex2byte("0029E3345D190388F4E5C6C5EDE7E79A30AE1F523D7303A995914E1A56D902281F6FA5A8F53FDB740DC9E3CE773F13FBC6CAF901E76EB4A3B3A1455FF8D037E4830D015692F79F1EE4E57B81A5CE383492822F4476E3CF17E1516A9E531125F0EE89BB49C1E8BADF9DC95B169E6932FCD5A6D855829599796DD2F845CFEB0F84111C1E81");

        try {
            ecdhe.generateSessionKey(sessionKey, hkdfInfo, publicKey);
            byte[] kcv = keyManager.getKeyInfo(KeyInfoID.KCV, sessionKey);
            LogUtils.d("gen_sk_kcv:", ISOUtils.hexString(kcv));
            showMessage(context.getString(R.string.ecdhe_generate_sk));
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, context.getString(R.string.ecdhe_generate_sk));
        }
    }

    @MethodGridEntity(btnnameid = R.string.ecdhe_generate, functionid = 3)
    private void generate_ecdhe() {
        DialogUtils.createCustomDialog(context, R.string.ecdhe_generate, null, R.layout.dialog_ecdhe, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                Switch swDefaultCasesKeyID = view.findViewById(R.id.switch_default_key_id_btn);
                swDefaultCasesKeyID.setChecked(true);
                LinearLayout llSwitchButton = view.findViewById(R.id.linear_switchViewParams);
                llSwitchButton.setVisibility(View.VISIBLE);
                LinearLayout llDefaultCasesKeyIDParams = view.findViewById(R.id.linear_ecdhe_generateDefaultKeyIDCaseParams);
                llDefaultCasesKeyIDParams.setVisibility(View.VISIBLE);
                LinearLayout llEditKeyIDParams = view.findViewById(R.id.linear_ecdhe_generateEditKeyIDParams);
                llEditKeyIDParams.setVisibility(View.GONE);
                Spinner spnECCType = view.findViewById(R.id.spnECCType);
                Spinner spnKeyUsage = view.findViewById(R.id.spnECDHEKeyUsage);
                Spinner spnKeyType = view.findViewById(R.id.spnECDHEKeyType);
                Spinner spnKeyLen = view.findViewById(R.id.spnECDHEKeyLen);
                Spinner spnKDFType = view.findViewById(R.id.spnECDHEKdfType);
                Spinner spnMessageDigestType = view.findViewById(R.id.spnECDHEMessageDigest);
                spnECCType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.ECDHE_ECC_TYPE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int eccType = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.ECDHE_ECC_TYPE, 0);
                spnECCType.setSelection(eccType);

                spnKeyUsage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.ECDHE_KEY_USAGE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int keyUsage = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.ECDHE_KEY_USAGE, 0);
                spnKeyUsage.setSelection(keyUsage);

                spnKeyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.ECDHE_KEY_TYPE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int keyType = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.ECDHE_KEY_TYPE, 0);
                spnKeyType.setSelection(keyType);

                spnKeyLen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.ECDHE_KEY_LEN, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int keyLen = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.ECDHE_KEY_LEN, 0);
                spnKeyLen.setSelection(keyLen);

                spnKDFType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.ECDHE_KDF_TYPE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int kdfType = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.ECDHE_KDF_TYPE, 0);
                spnKDFType.setSelection(kdfType);

                spnMessageDigestType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.ECDHE_MESSAGE_DIGEST_TYPE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int messageDigestType = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.ECDHE_MESSAGE_DIGEST_TYPE, 0);
                spnMessageDigestType.setSelection(messageDigestType);

                RadioGroup rgSelection = view.findViewById(R.id.rgECDHEGenerationSelect);
                RadioButton rbGenerateKeyPair = view.findViewById(R.id.rbGenerateKeyPair);
                RadioButton rbGenerateSK = view.findViewById(R.id.rbGenerateSK);
                LinearLayout llGenerateKeyPairParams = view.findViewById(R.id.linear_generateKeyPair);
                llGenerateKeyPairParams.setVisibility(View.VISIBLE);
                rbGenerateSK.setChecked(true);
                LinearLayout llGenerateSK = view.findViewById(R.id.linear_generateSK);
                llGenerateSK.setVisibility(View.VISIBLE);
                rgSelection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if(checkedId == rbGenerateKeyPair.getId()) {
                            llGenerateSK.setVisibility(View.GONE);
                            llDefaultCasesKeyIDParams.setVisibility(View.GONE);
                            llSwitchButton.setVisibility(View.GONE);

                        }
                        if(checkedId == rbGenerateSK.getId()) {
                            llGenerateSK.setVisibility(View.VISIBLE);
                            llEditKeyIDParams.setVisibility(View.GONE);
                            llSwitchButton.setVisibility(View.VISIBLE);
                        }
                    }
                });
                swDefaultCasesKeyID.setOnCheckedChangeListener(((buttonView, isChecked) -> {
                    if(isChecked) {
                        llDefaultCasesKeyIDParams.setVisibility(View.VISIBLE);
                        llEditKeyIDParams.setVisibility(View.GONE);
                        spnKDFType.setSelection(0);
                        spnKeyLen.setSelection(2);
                        spnKeyType.setSelection(1);
                        spnKeyUsage.setSelection(6);
                        spnMessageDigestType.setSelection(3);
                        if(rbGenerateKeyPair.isChecked()) {
                            llDefaultCasesKeyIDParams.setVisibility(View.GONE);
                            llEditKeyIDParams.setVisibility(View.GONE);
                        }
                    }else {
                        llDefaultCasesKeyIDParams.setVisibility(View.GONE);
                        llEditKeyIDParams.setVisibility(View.VISIBLE);
                        if(rbGenerateKeyPair.isChecked()) {
                            llDefaultCasesKeyIDParams.setVisibility(View.GONE);
                            llEditKeyIDParams.setVisibility(View.GONE);
                        }
                    }
                }));
            }

            @Override
            public void onResult(int id, View view) {
                Switch swDefaultCasesKeyID = view.findViewById(R.id.switch_default_key_id_btn);
                RadioGroup rgSelection = view.findViewById(R.id.rgECDHEGenerationSelect);
                RadioButton rbGenerateKeyPair = view.findViewById(R.id.rbGenerateKeyPair);
                RadioButton rbGenerateSK = view.findViewById(R.id.rbGenerateSK);
                Spinner spnECCType = view.findViewById(R.id.spnECCType);
                Spinner spnKeyUsage = view.findViewById(R.id.spnECDHEKeyUsage);
                Spinner spnKeyType = view.findViewById(R.id.spnECDHEKeyType);
                Spinner spnKeyLen = view.findViewById(R.id.spnECDHEKeyLen);
                Spinner spnKDFType = view.findViewById(R.id.spnECDHEKdfType);
                Spinner spnMessageDigestType = view.findViewById(R.id.spnECDHEMessageDigest);
                Spinner spnDefaultKeyID = view.findViewById(R.id.spnECDHEDefaultCasesKeyID);
                ECCType eccType = EnumUtils.getECCType(spnECCType.getSelectedItem().toString());


                byte defaultKeyID = EnumUtils.getDefaultKeyID(spnDefaultKeyID.getSelectedItem().toString());
                KeyUsage keyUsage = EnumUtils.getKeyUsage(spnKeyUsage.getSelectedItem().toString());
                KeyType keyType = EnumUtils.getKeyType(spnKeyType.getSelectedItem().toString());
                int keyLen = EnumUtils.getKeyLen(spnKeyLen.getSelectedItem().toString());
                KDFType kdfType = EnumUtils.getKDFType(spnKDFType.getSelectedItem().toString());
                MessageDigestType messageDigestType = EnumUtils.getMessageDigestType(spnMessageDigestType.getSelectedItem().toString());
                EditText etECHDE_Salt = view.findViewById(R.id.etSalt);

                if(rbGenerateKeyPair.isChecked()) {
                    try {
                        PublicKey = ecdhe.generateKeyPair(eccType);
                        showMessage(String.format("%s: %s", context.getString(R.string.ecdhe_generate_key_pair), ISOUtils.hexString(PublicKey)));
                    } catch (NSDKException e) {
                        e.printStackTrace();
                        showErrorMessage(e, context.getString(R.string.ecdhe_release));
                    }
                }else if(rbGenerateSK.isChecked()) {
                    try {
                        PublicKey = ecdhe.generateKeyPair(eccType);
                        showMessage(String.format("%s: %s", context.getString(R.string.ecdhe_generate_key_pair), ISOUtils.hexString(PublicKey)));
                    } catch (NSDKException e) {
                        e.printStackTrace();
                        showErrorMessage(e, context.getString(R.string.ecdhe_release));
                    }
                    if(swDefaultCasesKeyID.isChecked()) {
                        SymmetricKey sessionKey = new SymmetricKey();
                        sessionKey.setKeyType(keyType);
                        sessionKey.setKeyUsage(keyUsage);
                        sessionKey.setKeyID(defaultKeyID);
                        sessionKey.setKeyLen(keyLen);

                        KDFInfo hkdfInfo = new KDFInfo();
                        hkdfInfo.setKDFType(kdfType);
                        hkdfInfo.setMessageDigestType(messageDigestType);
                        hkdfInfo.setSalt(ISOUtils.hex2byte(etECHDE_Salt.getText().toString()));

                        try {
                            ecdhe.generateSessionKey(sessionKey, hkdfInfo, PublicKey);
                            byte[] kcv = keyManager.getKeyInfo(KeyInfoID.KCV, sessionKey);
                            showMessage(context.getString(R.string.ecdhe_generate_sk));
                        } catch (NSDKException e) {
                            e.printStackTrace();
                            showErrorMessage(e, context.getString(R.string.ecdhe_generate_sk));
                        }
                    }else {
                        EditText etKeyID = view.findViewById(R.id.dialog_ecdhe_keyID);
                        int keyID = Integer.parseInt(etKeyID.getText().toString());
                        SymmetricKey sessionKey = new SymmetricKey();
                        sessionKey.setKeyType(keyType);
                        sessionKey.setKeyUsage(keyUsage);
                        sessionKey.setKeyID((byte)keyID);
                        sessionKey.setKeyLen(keyLen);

                        KDFInfo hkdfInfo = new KDFInfo();
                        hkdfInfo.setKDFType(kdfType);
                        hkdfInfo.setMessageDigestType(messageDigestType);
                        hkdfInfo.setSalt(ISOUtils.hex2byte(etECHDE_Salt.getText().toString()));

                        try {
                            ecdhe.generateSessionKey(sessionKey, hkdfInfo, PublicKey);
                            byte[] kcv = keyManager.getKeyInfo(KeyInfoID.KCV, sessionKey);
                            showMessage(context.getString(R.string.ecdhe_generate_sk));
                        } catch (NSDKException e) {
                            e.printStackTrace();
                            showErrorMessage(e, context.getString(R.string.ecdhe_generate_sk));
                        }
                    }

                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.ecdhe_release, functionid = 4)
    private void release(){
        try {
            ecdhe.release();
            showMessage(context.getString(R.string.ecdhe_release));
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, context.getString(R.string.ecdhe_release));
        }
    }
}