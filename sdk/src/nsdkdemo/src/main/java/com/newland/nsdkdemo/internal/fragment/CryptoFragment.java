package com.newland.nsdkdemo.internal.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
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
import com.newland.nsdk.core.api.common.crypto.CSRFileType;
import com.newland.nsdk.core.api.common.crypto.CSRParameters;
import com.newland.nsdk.core.api.common.crypto.CipherOutput;
import com.newland.nsdk.core.api.common.crypto.CipherParameters;
import com.newland.nsdk.core.api.common.crypto.CipherType;
import com.newland.nsdk.core.api.common.crypto.CryptogramInfo;
import com.newland.nsdk.core.api.common.crypto.GCMCipherOut;
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
import com.newland.nsdk.core.api.common.keymanager.DUKPTKey;
import com.newland.nsdk.core.api.common.keymanager.KeyGenerateMethod;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.api.common.keymanager.KeyUsage;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.internal.NSDKModuleManager;
import com.newland.nsdk.core.api.internal.crypto.Crypto;
import com.newland.nsdk.core.api.internal.keymanager.KeyManager;
import com.newland.nsdk.core.internal.NSDKModuleManagerImpl;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.AppConfig;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.annotation.MethodGridEntity;
import com.newland.nsdkdemo.common.utils.DialogUtils;
import com.newland.nsdkdemo.common.utils.EnumUtils;
import com.newland.nsdkdemo.common.utils.MessageTag;
import com.newland.nsdkdemo.internal.utils.CryptoHelper;
import com.newland.nsdkdemo.internal.utils.XMLUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CryptoFragment extends InternalBaseFragment {

    private static final String TAG = "CipherFragment";
    private Crypto mCrypto;
    private KeyManager mKeymanager;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor mEditor;
    private int asymKeyID;
    private byte[] cipherout;
    private byte[] aesCtrCipherOut;

    private int decryptAsymKeyID;
    private byte[] decryptIVData;

    //sign-verify parameters
    private String messageDigestFlag;

    private CryptoHelper cryptoHelper;
    private List<XMLUtils.DataXmlElements> dataXmlElementsList;

    public CryptoFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_cipher_f);
    }

    @Override
    public void initData() {
        mCrypto = (Crypto) moduleManager.getModule(ModuleType.CRYPTO);
        mKeymanager = (KeyManager) moduleManager.getModule(ModuleType.KEY_MANAGER);
        sharedPreferences = context.getSharedPreferences("Crypto", Context.MODE_PRIVATE);
        mEditor = sharedPreferences.edit();
        cryptoHelper = new CryptoHelper(mCrypto);

        try {
            InputStream inputStream = context.getAssets().open("data/bigdata.xml");
            dataXmlElementsList = XMLUtils.readDataXML(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getModule() {
        return CryptoFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_cal_mac, functionid = 1)
    public void generateMac() {
        byte[] datain = ISOUtils.hex2byte("88888881113366677883373215563223abcdeffdaabbccdd");
        MACOutput macOutput = null;
        try {
            macOutput = mCrypto.generateMAC(AppConfig.Keys.MKSK_DES_INDEX_WK_MAC, MACType.TDES_X99, null, datain);
            LogUtils.d(TAG, "generateMac: " + ISOUtils.hexString(macOutput.getData()));
            showMessage("MAC: " + ISOUtils.hexString(macOutput.getData()));
        } catch (NSDKException e) {
            e.printStackTrace();
            LogUtils.d(TAG, "generateMac: MacOutput is null");
            showErrorMessage(e, "generate mac");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_cal_mac_aes_dukpt, functionid = 2)
    public void generateMacAESDUKPT() {
        DUKPTDerivateKey key = new DUKPTDerivateKey();
        key.setKeyID(AppConfig.Keys.DUKPT_AES_INDEX);
        key.setDerivateKeyLen(16);
        key.setDerivateKeyType(KeyType.AES);
        key.setDerivateUsage(DUKPTDerivateUsage.MAC_GEN);

        byte[] datain = ISOUtils.hex2byte("88888881113366677883373215563223abcdeffdaabbccdd");
        MACOutput macOutput = null;
        try {
            macOutput = mCrypto.generateMAC(key, MACType.AES_DUKPT_LAST, null, datain);
            LogUtils.d(TAG, "generateMac: " + ISOUtils.hexString(macOutput.getData()));
            showMessage("MAC(AES DUKPT): " + ISOUtils.hexString(macOutput.getData()));
        } catch (NSDKException e) {
            e.printStackTrace();
            LogUtils.d(TAG, "generateMac: MacOutput is null");
            showErrorMessage(e, "generate mac");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_encry, functionid = 3)
    public void encrypt() {
        SymmetricKey desKey = new SymmetricKey();
        desKey.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_WK_DATA);
        desKey.setKeyUsage(KeyUsage.DATA);
        byte[] datain = ISOUtils.hex2byte("88888881113366677883373215563223abcdeffdaabbccdd");
        CipherOutput cipherOutput = null;
        try {
            cipherOutput = mCrypto.encrypt(desKey, CipherType.DES_ECB, null, null, datain);
            LogUtils.d(TAG, "des encrypt: " + ISOUtils.hexString(cipherOutput.getData()));
            showMessage("DES encryption result: " + ISOUtils.hexString(cipherOutput.getData()));
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "do DES encryption");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_decrypt, functionid = 4)
    public void decrypt() {
        SymmetricKey desKey = new SymmetricKey();
        desKey.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_WK_DATA);
        desKey.setKeyUsage(KeyUsage.DATA);
//        byte[] datain = ISOUtils.hex2byte("DD55F3341D5ECFF5FBC3336594620743D316FC7C3EABE42E");
        byte[] datain = ISOUtils.hex2byte("2E6536213349CADAA90336743E115281B388B09BD2C709A0");
        CipherOutput cipherOutput = null;
        try {
            cipherOutput = mCrypto.decrypt(desKey, CipherType.DES_ECB, PaddingMode.NONE, null, datain);
            showMessage("DES decryption result: " + ISOUtils.hexString(cipherOutput.getData()));
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "do DES decryption");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_aes_encry, functionid = 5)
    private void aesEncrypt() {
        byte[] datain = new byte[16];
        SymmetricKey key = new SymmetricKey();
        key.setKeyID(AppConfig.Keys.MKSK_AES_INDEX_WK_DATA);
        key.setKeyUsage(KeyUsage.DATA);
        try {
            CipherOutput out = mCrypto.encrypt(key, CipherType.AES_ECB, null, null, datain);
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

    @MethodGridEntity(btnnameid = R.string.tv_pin_aes_decrypt, functionid = 6)
    private void desDecrypt() {
        byte[] datain = ISOUtils.hex2byte("3FB865A2E2740494FFFF67A03E830EA3");
        SymmetricKey key = new SymmetricKey();
        key.setKeyID(AppConfig.Keys.MKSK_AES_INDEX_WK_DATA);
        key.setKeyUsage(KeyUsage.DATA);
        try {
            CipherOutput out = mCrypto.decrypt(key, CipherType.AES_ECB, null, null, datain);
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

    @MethodGridEntity(btnnameid = R.string.tv_pin_aes_dukpt_encry, functionid = 7)
    public void encryptAESDUKPT() {
        DUKPTDerivateKey desKey = new DUKPTDerivateKey();
        desKey.setKeyID(AppConfig.Keys.DUKPT_AES_INDEX);
        desKey.setKeyUsage(KeyUsage.DUKPT);
        desKey.setDerivateUsage(DUKPTDerivateUsage.DATA_ENC);
        desKey.setDerivateKeyType(KeyType.AES);
        desKey.setDerivateKeyLen(16);
        byte[] datain = ISOUtils.hex2byte("88888881113366677883373215563223abcdeffdaabbccdd8888888111336667");
        CipherOutput cipherOutput = null;
        try {
            cipherOutput = mCrypto.encrypt(desKey, CipherType.AES_DUKPT_ECB, null, null, datain);
            LogUtils.d(TAG, "des encrypt: " + ISOUtils.hexString(cipherOutput.getData()));
            showMessage("AES DUKPT encryption result: " + ISOUtils.hexString(cipherOutput.getData()));
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "do AES DUKPT encryption");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_aes_dukpt_decry, functionid = 8)
    public void decryptAESDUKPT() {
        DUKPTDerivateKey desKey = new DUKPTDerivateKey();
        desKey.setKeyID(AppConfig.Keys.DUKPT_AES_INDEX);
        desKey.setKeyUsage(KeyUsage.DUKPT);
        desKey.setDerivateUsage(DUKPTDerivateUsage.DATA_DEC);
        desKey.setDerivateKeyType(KeyType.AES);
        desKey.setDerivateKeyLen(16);
        byte[] datain = ISOUtils.hex2byte("DD55F3341D5ECFF5FBC3336594620743D316FC7C3EABE42EDD55F3341D5ECFF5");
        CipherOutput cipherOutput = null;
        try {
            cipherOutput = mCrypto.decrypt(desKey, CipherType.AES_DUKPT_ECB, PaddingMode.NONE, null, datain);
            showMessage("AES DUKPT decryption result: " + ISOUtils.hexString(cipherOutput.getData()));
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "do AES DUKPT decryption");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_dukpt_encry, functionid = 9)
    private void dukptEncrypt() {
        byte[] datain = new byte[16];
        SymmetricKey key = new SymmetricKey();
        key.setKeyID(AppConfig.Keys.DUKPT_DES_INDEX);
        key.setKeyType(KeyType.DES);
        key.setKeyUsage(KeyUsage.DUKPT);
        try {
            CipherOutput out = mCrypto.encrypt(key, CipherType.DUKPT_ECB_RESP, null, null, datain);
            if (out != null) {
                showMessage("DUKPT Encrypt: " + ISOUtils.hexString(out.getData()));
                showMessage("DUKPT ksn: " + ISOUtils.hexString(out.getKsn()));
            } else {
                showMessage("DUKPT Encrypt: CipherOutput is null");
            }
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "do DUKPT encrypt");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_dukpt_decrypt, functionid = 10)
    private void dukptDecrypt() {
        byte[] datain = ISOUtils.hex2byte("3F82D44F845E323A3F82D44F845E323A");
        SymmetricKey key = new SymmetricKey();
        key.setKeyID(AppConfig.Keys.DUKPT_DES_INDEX);
        key.setKeyType(KeyType.DES);
        key.setKeyUsage(KeyUsage.DUKPT);
        try {
            CipherOutput out = mCrypto.decrypt(key, CipherType.DUKPT_ECB_RESP, null, null, datain);
            if (out != null) {
                showMessage("DUKPT Decrypt: " + ISOUtils.hexString(out.getData()));
            } else {
                showMessage("DUKPT Decrypt: CipherOutput is null");
            }
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "de DUKPT decrypt");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_get_random_num, functionid = 11)
    public void getrandom() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_get_random_num), null, R.layout.dialog_crypto_random_number, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                EditText etRandomDataLen = dialogView.findViewById(R.id.et_crypto_set_random_data_len);
                int dataLen = Integer.parseInt(etRandomDataLen.getText().toString());
                byte[] randomOut;
                try {
                    randomOut = mCrypto.getRandom(dataLen);
                    showMessage("Random data: " + ISOUtils.hexString(randomOut));
                } catch (NSDKException e) {
                    e.printStackTrace();
                    showErrorMessage(e, "get random data");
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_asym_sign_verify, functionid = 12)
    public void signAsym(){
        DialogUtils.createCustomDialog(context, R.string.tv_pin_asym_sign_verify, null, R.layout.dialog_crypto_asym_sign_verify, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                Switch swDefaultCase = view.findViewById(R.id.switch_default_key_id_btn);
                swDefaultCase.setChecked(true);
                LinearLayout llEditKeyIDCaseParams = view.findViewById(R.id.linear_sign_asym_edit_key_id_cases_params);
                llEditKeyIDCaseParams.setVisibility(View.GONE);

                Spinner spnAsymKeyType = view.findViewById(R.id.spn_sign_verify_asym_keyType);
                spnAsymKeyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.ASYM_AUTH_KEY_TYPE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int asymKeyType = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.ASYM_AUTH_KEY_TYPE, 0);
                spnAsymKeyType.setSelection(asymKeyType);

                Spinner spnAsymKeyUsage = view.findViewById(R.id.spn_sign_verify_asym_keyUsage);
                spnAsymKeyUsage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.ASYM_AUTH_KEY_USAGE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int asymKeyUsage = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.ASYM_AUTH_KEY_USAGE, 0);
                spnAsymKeyUsage.setSelection(asymKeyUsage);

                Spinner spnMessageDigestType = view.findViewById(R.id.spn_sign_verify_asym_messageDigestType);
                spnMessageDigestType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.ASYM_AUTH_KEY_MESSAGE_DIGEST_TYPE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int messageDigestType = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.ASYM_AUTH_KEY_MESSAGE_DIGEST_TYPE, 0);
                spnMessageDigestType.setSelection(messageDigestType);

                Spinner spnAsymEncodingMode = view.findViewById(R.id.spn_sign_verify_asym_encodingMode);
                spnAsymEncodingMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.ASYM_AUTH_KEY_ENCODING_MODE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int asymEncodingMode = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.ASYM_AUTH_KEY_ENCODING_MODE, 0);
                spnAsymEncodingMode.setSelection(asymEncodingMode);
                EditText etOriginalData = view.findViewById(R.id.et_sign_verify_original_data);
                etOriginalData.setText("12345678");

                swDefaultCase.setOnCheckedChangeListener(((buttonView, isChecked) -> {
                    if (swDefaultCase.isChecked()) {
                        llEditKeyIDCaseParams.setVisibility(View.GONE);
                        spnAsymEncodingMode.setSelection(1);
                        spnAsymKeyType.setSelection(0);
                        spnAsymKeyUsage.setSelection(0);
                        spnMessageDigestType.setSelection(3);
                        etOriginalData.setText("12345678");
                    }else {
                        llEditKeyIDCaseParams.setVisibility(View.VISIBLE);
                        spnAsymEncodingMode.setSelection(0);
                        spnMessageDigestType.setSelection(0);
                        spnAsymKeyType.setSelection(0);
                        spnAsymKeyUsage.setSelection(0);
                    }
                }));
            }
            @Override
            public void onResult(int id, View view) {
                Switch swDefaultCase = view.findViewById(R.id.switch_default_key_id_btn);


                Spinner spnAsymKeyType = view.findViewById(R.id.spn_sign_verify_asym_keyType);
                Spinner spnAsymKeyUsage = view.findViewById(R.id.spn_sign_verify_asym_keyUsage);
                Spinner spnMessageDigestType = view.findViewById(R.id.spn_sign_verify_asym_messageDigestType);
                Spinner spnAsymEncodingMode = view.findViewById(R.id.spn_sign_verify_asym_encodingMode);
                EditText spnOriginalData = view.findViewById(R.id.et_sign_verify_original_data);

                AsymKeyType keyType = EnumUtils.getAsymKeyType(spnAsymKeyType.getSelectedItem().toString());
                AsymKeyUsage keyUsage = EnumUtils.getAsymKeyUsage(spnAsymKeyUsage.getSelectedItem().toString());
                MessageDigestType messageDigestType = EnumUtils.getMessageDigestType(spnMessageDigestType.getSelectedItem().toString());
                AsymEncodingMode encodingMode = EnumUtils.getAsymEncodingMode(spnAsymEncodingMode.getSelectedItem().toString());
                messageDigestFlag = EnumUtils.getMessageDigestInstance(spnMessageDigestType.getSelectedItem().toString());

                if (swDefaultCase.isChecked()) {
                    //Inject Default Asym-Auth Key for Sign-verify Progress
                    byte[] keyValue = ISOUtils.hex2byte("308204A40201000282010100C6DEE0BC45C7D2D062602F76C3FBFE5B2FB72A77217CC201B312F7FF8DAFBF2DD3B1404313A975F5F54ECE9D502FF6634468E5FD90D8A834C89AB5647A32B69BAACED6E265B2EE5A5089C58FF33A1F1D57CB2F9A4C0376E4A64EEC0D89C66562C984FCDC86A657EA4F7CC3E85AA4A4C1192597AC10F8E2A7A0EB805952A58C433C489513EEF5CF3CA8613BDF47B9FCCB6476D3DBC6E5C4BE2D392947FEFB0F92F0AAAA78F7C791251F3A4223A583A9CBD084F04CE36B166FAC121D8A1CCC02D38B4B4B88516CFFA06518FB0975F97E8E02D8ECE519A1BE41729271B98D449925DE9AF1591BD0A7D9DE4407C51081BA10FEC8017859A26DC23C02B450D9B11EF102030100010282010100BED1F50A325ABE69BD3B55CFBBD5FC063B0EA1EC95714426A5513A2D3822BE6A9689A983B346132DE227B0113A740B12CCFD6A5197BE8C07B9C4D8F084604CDFA951B6D69D86C73659B9189C3B6235A0CE30E488450FBDF13FB2D2C55AC1C75EB6C6A86A61B912FA7D32D638096199C4BF00573C7F3C911F0F45696E4BE315656B3364D5051F01467FDBD3415A498A5C821FD67232D2A8A193B94A956C26C0117D0568D28CEDA59669551DABF1B994A3F314C925133F88F31E7885162C3880C65E19D48390D5EA058384D082476B9E9229631F059C189A3BAE60EB78B3E650B3A5B77DEBABFA56D8970DBF444FF8510146195AA1BF7698CF01A31CFC74FFD7C102818100F95559F528B0892169E661EF8A1A5012905013E4F857B7EF26891E019F644A2119825DFAC7EEF099971C68B32E95BA0FEFD6E7DF8CC8FAFB7B28A743C15AD7ACD930BCBE3A497C1D3893CA4E90FE13E2D7A2EE85A3354674F02521E4B51C3A4AEB1162E01637CA9B001A6F2ECDB3BB07DCF8ABD8A0666A7CA2DE5C3A2A991D6D02818100CC301DD2DDF6B1AC0D78D5F12F0798A2B264B112802D3E81A086DA3FC896507E0C8A6B1B5E66B5ECF686121A6805FE984C60FE1C9E0503E15416364CEE3B22651DDEC0A1001F9C37ACDC640B62E5EE16D27974D5EE92EFFD5386DA9492FCCACF54BD5FA0915ED115F8991D897B2D59E09ADED149B405AC10958084514C50691502818007DD2448322F5733E1962D9293857EEF06F42F9C7224BA1D65D6BF4687D36EEF1A51DD4AF2915BAF4C6FCDF190CF921DBC8FC7A26A5B50672C1C3D224AEFE58B83122171D27ECCD653197E30FA2BB94ED74441479FBD276ABAC4410C6895EA54C0933CCE1A8549F3978E3DE179056929B753748011970956C300466263438F050281807529A2E34D53F1AD1CE9DA3113605377FFCF013FF16684B852C92E506D23BB3A28AE00396B189A894707B5398BB8ECD6ACF4F6BAAAFD8BB56ECF7406FEA7D5DB99A1287CF99A29C4549EFD94FF019A7563FE27495E24D82A4F145135F185B645F384DA6B431ED9F0B67DFD51D6E935EA48535459EB3F59F506240148B8F666E502818100904C79FC91B9C0E9BF0B949A75341EF6524A06E4FEE00A63D4FEC81C00E0849AFC2A414A07F43379DAE40038C12CFA2DF8EA3FAC89784C121736A1EE508CBC3E3235766413857CD97546446D141DC7D73684A1FC7C2F777C6ECE84CDA9D33D7588A127A87F43303473AEC72F9147C67B959949262F136CB806525D7440AE48E1");
                    AsymmetricKey dstKey = new AsymmetricKey();
                    dstKey.setKeyID((byte) AppConfig.Keys.ASYM_AUTH_ID);
                    dstKey.setKeyUsage(AsymKeyUsage.AUTH);
                    dstKey.setKeyType(AsymKeyType.RSA);
                    dstKey.setKeyData(keyValue);
                    dstKey.setKeyLen(keyValue.length);

                    try {
                        mKeymanager.generateKey(KeyGenerateMethod.CLEAR, null, null, dstKey, null);
                    } catch (NSDKException e) {
                        e.printStackTrace();
                    }

                    //Sign-verify
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
                        signedData = mCrypto.signAsym(key, parameters, hash);
                        showMessage(String.format("Asym signed data: %s", ISOUtils.hexString(signedData)));
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                        showMessage("Failed to get SHA256 message digest instance.", MessageTag.ERROR);
                    } catch (NSDKException e) {
                        e.printStackTrace();
                        showErrorMessage(e, "do asymmetric signing");
                    }

                    try {
                        mCrypto.verifyAsym(key, parameters, hash, signedData);
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
                    key.setKeyUsage(keyUsage);
                    key.setKeyType(keyType);

                    AsymAlgorithmParameters parameters = new AsymAlgorithmParameters();
                    parameters.setMessageDigestType(messageDigestType);
                    parameters.setEncodingMode(encodingMode);


                    String originalString = spnOriginalData.getText().toString();
                    byte[] hash = null;
                    byte[] signedData = null;

                    try {
                        MessageDigest digest = MessageDigest.getInstance(messageDigestFlag);
                        hash = digest.digest(originalString.getBytes());
                        signedData = mCrypto.signAsym(key, parameters, hash);
                        showMessage(String.format("Asym signed data: %s", ISOUtils.hexString(signedData)));
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                        showMessage("Failed to get SHA256 message digest instance.", MessageTag.ERROR);
                    } catch (NSDKException e) {
                        e.printStackTrace();
                        showErrorMessage(e, "do asymmetric signing");
                    }

                    try {
                        mCrypto.verifyAsym(key, parameters, hash, signedData);
                        showMessage("Signed data is verified successfully.");
                    } catch (NSDKException e) {
                        e.printStackTrace();
                        showErrorMessage(e, "verify signed data");
                    }
                }

            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_asym_encry, functionid = 13)
    public void encryptAsym(){
        AsymmetricKey key = new AsymmetricKey();
        key.setKeyID(AppConfig.Keys.ASYM_DATA_ID);
        key.setKeyUsage(AsymKeyUsage.DATA);
        key.setKeyType(AsymKeyType.RSA);

        AsymAlgorithmParameters parameters = new AsymAlgorithmParameters();
        parameters.setMessageDigestType(MessageDigestType.SHA256);
        parameters.setEncodingMode(AsymEncodingMode.PKCS_V21);
        parameters.setCryptoMode(AsymCryptoMode.PUBLIC);

        byte[] data = ISOUtils.hex2byte("0102030405060708090A0B0C0D0E0F");

        try {
            byte[] result = mCrypto.encryptAsym(key, parameters, data);
            LogUtils.d("test", ISOUtils.hexString(result));
            showMessage(String.format("Asym encryption result: %s", ISOUtils.hexString(result)));
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "do asym encryption");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_asym_decry, functionid = 14)
    public void decryptAsym(){
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
            byte[] encryptedData = mCrypto.encryptAsym(key, parameters, data);
            parameters.setCryptoMode(AsymCryptoMode.PRIVATE);
            LogUtils.d("asymEncryptedData:", ISOUtils.hexString(encryptedData));
            byte[] decryptedData = mCrypto.decryptAsym(key, parameters, encryptedData);
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

    @MethodGridEntity(btnnameid = R.string.tv_pin_encrypt_test, functionid = 15)
    private void encryptOptional() {
        DialogUtils.createCustomDialog(context, R.string.tv_pin_encrypt_test, null, R.layout.dialog_crypto_encrypt, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                //Init Dialog And  RadioGroup State
                Switch swDefaultKeyID = view.findViewById(R.id.switch_default_key_id_btn);
                swDefaultKeyID.setChecked(true);
                Spinner spnDefaultKeyID = view.findViewById(R.id.spn_crypto_encrypt_defaultKeyID);
                LinearLayout llDefaultKeyIDParams = view.findViewById(R.id.linear_crypto_encrypt_defaultKeyID_params);
                llDefaultKeyIDParams.setVisibility(View.VISIBLE);
                LinearLayout llEditKeyIDParams = view.findViewById(R.id.linear_crypto_encrypt_editKeyID_params);
                llEditKeyIDParams.setVisibility(View.GONE);
                LinearLayout llKeyTypeParams = view.findViewById(R.id.linear_crypto_encrypt_keyType_params);
                llKeyTypeParams.setVisibility(View.GONE);
                RadioGroup rgSelection = view.findViewById(R.id.crypto_symm_asym_selection_radioGroup);
                RadioButton rbEncrypt = view.findViewById(R.id.crypto_encrypt_radio);
                RadioButton rbAsymEncrypt = view.findViewById(R.id.crypto_asym_encrypt_radio);
                rbEncrypt.setChecked(true);
                LinearLayout llEncryptAsymDefaultKeyIDParams = view.findViewById(R.id.linear_crypto_encrypt_asym_defaultKeyID_params);
                llEncryptAsymDefaultKeyIDParams.setVisibility(View.VISIBLE);
                LinearLayout llEncryptAsymEditKeyIDParams = view.findViewById(R.id.linear_crypto_encrypt_asym_editKeyID_params);
                llEncryptAsymEditKeyIDParams.setVisibility(View.GONE);
                LinearLayout llEncryptionParams = view.findViewById(R.id.linear_crypto_encryption_params);
                llEncryptionParams.setVisibility(View.VISIBLE);
                LinearLayout llAsymEncryptionParams = view.findViewById(R.id.linear_crypto_asym_encryption_params);
                llAsymEncryptionParams.setVisibility(View.GONE);

                rgSelection.setOnCheckedChangeListener(((group, checkedId) -> {
                    if(checkedId == rbEncrypt.getId()) {
                        swDefaultKeyID.setChecked(true);
                        llEncryptionParams.setVisibility(View.VISIBLE);
                        llAsymEncryptionParams.setVisibility(View.GONE);
                    }
                    if(checkedId == rbAsymEncrypt.getId()) {
                        swDefaultKeyID.setChecked(true);
                        llEncryptionParams.setVisibility(View.GONE);
                        llAsymEncryptionParams.setVisibility(View.VISIBLE);
                        llDefaultKeyIDParams.setVisibility(View.GONE);
                    }
                    mEditor.putInt(AppConfig.SharedPreferenceConfig.SYMM_ASYM, checkedId);
                    mEditor.commit();
                }));

                //Keep RadioGroup's State
                int symmAsym = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.SYMM_ASYM, 0);
                if(symmAsym == rbEncrypt.getId()) {
                    rbEncrypt.setChecked(true);
                } else if(symmAsym == rbAsymEncrypt.getId()) {
                    rbAsymEncrypt.setChecked(true);
                }

                EditText etKeyID = view.findViewById(R.id.et_crypto_encrypt_keyID);
                etKeyID.setText(String.valueOf(sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.ENCRYPT_KEY_ID, 0)));

                Spinner spnKeyUsage = view.findViewById(R.id.spn_crypto_encrypt_keyUsage);
                LinearLayout llDukptEncryption = view.findViewById(R.id.linear_crypto_dukpt_encryption);
                llDukptEncryption.setVisibility(View.GONE);

                Spinner spnCipheType = view.findViewById(R.id.spn_crypto_encrypt_cipheType);
                spnCipheType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.ENCRYPT_CIPHER_TYPE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int cipherType = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.ENCRYPT_CIPHER_TYPE, 0);
                spnCipheType.setSelection(cipherType);

                Spinner spnKeyType = view.findViewById(R.id.spn_crypto_encrypt_keyType);
                spnKeyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (!swDefaultKeyID.isChecked()) {
                            if("AES".equals(spnKeyType.getSelectedItem().toString()) && "DUKPT".equals(spnKeyUsage.getSelectedItem().toString())) {
                                llDukptEncryption.setVisibility(View.VISIBLE);
                                ArrayAdapter adapter = EnumUtils.getAESDUKPTCipherTypeArrayAdapter(context);
                                spnCipheType.setAdapter(adapter);
                            }else {
                                llDukptEncryption.setVisibility(View.GONE);
                                ArrayAdapter adapter = EnumUtils.getOtherCipherTypeArrayAdapter(context);
                                spnCipheType.setAdapter(adapter);
                            }
                        }
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.ENCRYPT_KEY_TYPE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int keyType = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.ENCRYPT_KEY_TYPE, 0);
                spnKeyType.setSelection(keyType);

                spnKeyUsage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(!swDefaultKeyID.isChecked()) {
                            if("AES".equals(spnKeyType.getSelectedItem().toString()) && "DUKPT".equals(spnKeyUsage.getSelectedItem().toString())) {
                                llDukptEncryption.setVisibility(View.VISIBLE);
                                ArrayAdapter adapter = EnumUtils.getAESDUKPTCipherTypeArrayAdapter(context);
                                spnCipheType.setAdapter(adapter);
                            }else {
                                llDukptEncryption.setVisibility(View.GONE);
                                ArrayAdapter adapter = EnumUtils.getOtherCipherTypeArrayAdapter(context);
                                spnCipheType.setAdapter(adapter);
                            }
                        }
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.ENCRYPT_KEY_USAGE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                //Keep Spinners' State for Next test
                //Symmetric Encrypt
                int keyUsage = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.ENCRYPT_KEY_USAGE, 0);
                if(!swDefaultKeyID.isChecked()) {
                    spnKeyUsage.setSelection(keyUsage);
                }


                Spinner spnDUKPTDerivateUsage = view.findViewById(R.id.spn_crypto_encrypt_dukpt_derivateUsage);
                spnDUKPTDerivateUsage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.ENCRYPT_DUKPT_DERIVATE_USAGE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int dukptDerivateUsage = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.ENCRYPT_DUKPT_DERIVATE_USAGE, 0);
                spnDUKPTDerivateUsage.setSelection(dukptDerivateUsage);

                Spinner spnDUKPTDerivateKeyType = view.findViewById(R.id.spn_crypto_encrypt_dukpt_derivateKeyType);
                spnDUKPTDerivateKeyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.ENCRYPT_DUKPT_DERIVATE_TYPE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int dukptDerivateKeyType = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.ENCRYPT_DUKPT_DERIVATE_TYPE, 0);
                spnDUKPTDerivateKeyType.setSelection(dukptDerivateKeyType);

                Spinner spnDUKPTDerivateKeyLen = view.findViewById(R.id.spn_crypto_encrypt_dukpt_derivateKeyLen);
                spnDUKPTDerivateKeyLen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.ENCRYPT_DUKPT_DERIVATE_KEY_LEN, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int dukptDerivateKeyLen = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.ENCRYPT_DUKPT_DERIVATE_KEY_LEN, 0);
                spnDUKPTDerivateKeyLen.setSelection(dukptDerivateKeyLen);

                Spinner spnPaddingMode = view.findViewById(R.id.spn_crypto_encrypt_paddingMode);
                spnPaddingMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.ENCRYPT_PADDING_MODE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int paddingMode = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.ENCRYPT_PADDING_MODE, 0);
                spnPaddingMode.setSelection(paddingMode);

                EditText etPaddingData = view.findViewById(R.id.spn_crypto_encrypt_paddingData);
                etPaddingData.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.ENCRYPT_PADDING_DATA, null));

                EditText etEncryptDataIn = view.findViewById(R.id.spn_crypto_encrypt_encryptData);
                etEncryptDataIn.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.ENCRYPT_DATA_IN, null));

                //Asymmetric Encryption
                EditText etAsymKeyID = view.findViewById(R.id.spn_crypto_encrypt_asym_keyID);
                etAsymKeyID.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.ENCRYPT_ASYM_KEY_ID, "0"));

                Spinner spnAsymKeyType = view.findViewById(R.id.spn_crypto_encrypt_asym_keyType);
                spnAsymKeyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.ENCRYPT_ASYM_KEY_Type, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int asymKeyType = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.ENCRYPT_ASYM_KEY_Type, 0);
                spnAsymKeyType.setSelection(asymKeyType);

                Spinner spnAsymKeyUsage = view.findViewById(R.id.spn_crypto_encrypt_asym_keyUsage);
                spnAsymKeyUsage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.ENCRYPT_ASYM_KEY_Usage, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int asymKeyUsage = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.ENCRYPT_ASYM_KEY_Usage, 0);
                spnAsymKeyUsage.setSelection(asymKeyUsage);

                Spinner spnAsymMessageDigestType = view.findViewById(R.id.spn_crypto_encrypt_asym_messageDigestType);
                spnAsymMessageDigestType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.ENCRYPT_ASYM_MESSAGE_DIGEST_TYPE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int asymMessageDigestType = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.ENCRYPT_ASYM_MESSAGE_DIGEST_TYPE, 0);
                spnAsymMessageDigestType.setSelection(asymMessageDigestType);

                Spinner spnAsymCryptoMode = view.findViewById(R.id.spn_crypto_encrypt_asym_cryptoMode);
                spnAsymCryptoMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.ENCRYPT_ASYM_CRYPTO_MODE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int asymCryptoMode = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.ENCRYPT_ASYM_CRYPTO_MODE, 0);
                spnAsymCryptoMode.setSelection(asymCryptoMode);

                Spinner spnAsymEncodingMode = view.findViewById(R.id.spn_crypto_encrypt_asym_encodingMode);
                spnAsymEncodingMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.ENCRYPT_ASYM_ENCODING_MODE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int asymEncodingMode = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.ENCRYPT_ASYM_ENCODING_MODE, 0);
                spnAsymEncodingMode.setSelection(asymEncodingMode);

                EditText etAsymData = view.findViewById(R.id.et_crypto_encrypt_asym_datain);
                etAsymData.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.ENCRYPT_ASYM_DATA, null));

                swDefaultKeyID.setOnCheckedChangeListener(((buttonView, isChecked) -> {
                    if(isChecked) {
                        llDukptEncryption.setVisibility(View.GONE);
                        ArrayAdapter adapter = EnumUtils.getOtherCipherTypeArrayAdapter(context);
                        spnCipheType.setAdapter(adapter);
                        if(rbEncrypt.isChecked()) {
                            llDefaultKeyIDParams.setVisibility(View.VISIBLE);
                            llEditKeyIDParams.setVisibility(View.GONE);
                        }else if(rbAsymEncrypt.isChecked()){
                            llEncryptAsymDefaultKeyIDParams.setVisibility(View.VISIBLE);
                            llEncryptAsymEditKeyIDParams.setVisibility(View.GONE);
                        }
                    }else {
                        llDukptEncryption.setVisibility(View.GONE);
                        if (rbEncrypt.isChecked()) {
                            llDefaultKeyIDParams.setVisibility(View.GONE);
                            llEditKeyIDParams.setVisibility(View.VISIBLE);
                            llKeyTypeParams.setVisibility(View.VISIBLE);
                        }else if (rbAsymEncrypt.isChecked()) {
                            llEncryptAsymDefaultKeyIDParams.setVisibility(View.GONE);
                            llEncryptAsymEditKeyIDParams.setVisibility(View.VISIBLE);
                        }
                    }
                }));

                spnDefaultKeyID.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if("DUKPT_AES_INDEX".equals(spnDefaultKeyID.getSelectedItem().toString())) {
                            llDukptEncryption.setVisibility(View.VISIBLE);
                            ArrayAdapter adapter = EnumUtils.getAESDUKPTCipherTypeArrayAdapter(context);
                            spnCipheType.setAdapter(adapter);
                            spnDUKPTDerivateKeyLen.setSelection(1);
                            spnDUKPTDerivateKeyType.setSelection(1);
                            spnDUKPTDerivateUsage.setSelection(6);
                        }else {
                            llDukptEncryption.setVisibility(View.GONE);
                            ArrayAdapter adapter = EnumUtils.getOtherCipherTypeArrayAdapter(context);
                            spnCipheType.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onResult(int id, View view) {
                RadioButton rbEncrypt = view.findViewById(R.id.crypto_encrypt_radio);
                RadioButton rbAsymEncrypt = view.findViewById(R.id.crypto_asym_encrypt_radio);
                //Symmetric Encrypt
                EditText etKeyID = view.findViewById(R.id.et_crypto_encrypt_keyID);

                Spinner spnKeyUsage = view.findViewById(R.id.spn_crypto_encrypt_keyUsage);
                Spinner spnDUKPTDerivateUsage = view.findViewById(R.id.spn_crypto_encrypt_dukpt_derivateUsage);
                Spinner spnDUKPTDerivateKeyType = view.findViewById(R.id.spn_crypto_encrypt_dukpt_derivateKeyType);
                Spinner spnDUKPTDerivateKeyLen = view.findViewById(R.id.spn_crypto_encrypt_dukpt_derivateKeyLen);
                Spinner spnCipheType = view.findViewById(R.id.spn_crypto_encrypt_cipheType);
                Spinner spnPaddingMode = view.findViewById(R.id.spn_crypto_encrypt_paddingMode);
                EditText etPaddingData = view.findViewById(R.id.spn_crypto_encrypt_paddingData);
                EditText etDataIn = view.findViewById(R.id.spn_crypto_encrypt_encryptData);
                LinearLayout llDUKPTEncryption = view.findViewById(R.id.linear_crypto_dukpt_encryption);
                Spinner spnAsymKeyType = view.findViewById(R.id.spn_crypto_encrypt_asym_keyType);
                Spinner spnAsymKeyUsage = view.findViewById(R.id.spn_crypto_encrypt_asym_keyUsage);
                Spinner spnAsymMessageDigestType = view.findViewById(R.id.spn_crypto_encrypt_asym_messageDigestType);
                Spinner spnAsymCryptoMode = view.findViewById(R.id.spn_crypto_encrypt_asym_cryptoMode);
                Spinner spnAsymEncodingMode = view.findViewById(R.id.spn_crypto_encrypt_asym_encodingMode);
                Spinner spnDefaultKeyID = view.findViewById(R.id.spn_crypto_encrypt_defaultKeyID);
                Spinner spnAsymDefaultKeyID = view.findViewById(R.id.spn_crypto_encrypt_asym_defaultKeyID);
                Switch swDefaultKeyID = view.findViewById(R.id.switch_default_key_id_btn);

                byte[] encryptIVData = ISOUtils.hex2byte(etPaddingData.getText().toString());
                mEditor.putString(AppConfig.SharedPreferenceConfig.ENCRYPT_PADDING_DATA, etPaddingData.getText().toString());
                mEditor.commit();
                byte[] datain = ISOUtils.hex2byte(etDataIn.getText().toString());
                if(datain == null) {
                    datain = new byte[16];
                }
                mEditor.putString(AppConfig.SharedPreferenceConfig.ENCRYPT_DATA_IN, etDataIn.getText().toString());
                mEditor.commit();
                EditText etAsymDataIn = view.findViewById(R.id.et_crypto_encrypt_asym_datain);
                mEditor.putString(AppConfig.SharedPreferenceConfig.ENCRYPT_ASYM_DATA, etAsymDataIn.getText().toString());
                mEditor.commit();
                byte defaultKeyID = EnumUtils.getDefaultKeyID(spnDefaultKeyID.getSelectedItem().toString());
                byte asymDefaultKeyID = EnumUtils.getAsymDefaultKeyID(spnAsymDefaultKeyID.getSelectedItem().toString());
                int keyID = Integer.parseInt(etKeyID.getText().toString());
                mEditor.putInt(AppConfig.SharedPreferenceConfig.ENCRYPT_KEY_ID, keyID);

                KeyUsage keyUsage = EnumUtils.getKeyUsage(spnKeyUsage.getSelectedItem().toString());
                KeyType dukptDerivateKeyType = EnumUtils.getKeyType(spnDUKPTDerivateKeyType.getSelectedItem().toString());
                AsymEncodingMode asymEncodingMode = EnumUtils.getAsymEncodingMode(spnAsymEncodingMode.getSelectedItem().toString());
                AsymCryptoMode asymCryptoMode = EnumUtils.getAsymCryptoMode(spnAsymCryptoMode.getSelectedItem().toString());
                MessageDigestType messageDigestType = EnumUtils.getMessageDigestType(spnAsymMessageDigestType.getSelectedItem().toString());
                AsymKeyUsage asymKeyUsage = EnumUtils.getAsymKeyUsage(spnAsymKeyUsage.getSelectedItem().toString());
                AsymKeyType asymKeyType = EnumUtils.getAsymKeyType(spnAsymKeyType.getSelectedItem().toString());
                PaddingMode paddingMode = EnumUtils.getPaddingMode(spnPaddingMode.getSelectedItem().toString());
                CipherType cipherType = EnumUtils.getCipherType(spnCipheType.getSelectedItem().toString());
                int dukptDerivateLen = EnumUtils.getKeyLen(spnDUKPTDerivateKeyLen.getSelectedItem().toString());
                DUKPTDerivateUsage dukptDerivateUsage = EnumUtils.getDukptDerivateUsage(spnDUKPTDerivateUsage.getSelectedItem().toString());

                CipherOutput cipherOutput;
                if(rbEncrypt.isChecked()) {
                    if (swDefaultKeyID.isChecked()) {
                        SymmetricKey symmetricKey = new SymmetricKey();
                        symmetricKey.setKeyID(defaultKeyID);
                        symmetricKey.setKeyUsage(keyUsage);
                        if(llDUKPTEncryption.getVisibility() == View.VISIBLE) {
                            DUKPTDerivateKey dukptDerivateKey = new DUKPTDerivateKey();
                            dukptDerivateKey.setKeyID(defaultKeyID);
                            dukptDerivateKey.setKeyUsage(keyUsage);
                            dukptDerivateKey.setDerivateKeyLen(dukptDerivateLen);
                            dukptDerivateKey.setDerivateKeyType(dukptDerivateKeyType);
                            dukptDerivateKey.setDerivateUsage(dukptDerivateUsage);
                            try {
                                cipherOutput = mCrypto.encrypt(dukptDerivateKey, cipherType, paddingMode, encryptIVData, datain);
                                showMessage("Encryption Result:" + ISOUtils.hexString(cipherOutput.getData()));
                                mEditor.putString(AppConfig.SharedPreferenceConfig.ENCRYPTION_CIPHER_OUT, ISOUtils.hexString(cipherOutput.getData()));
                                mEditor.commit();
                            }catch (NSDKException e) {
                                showErrorMessage(e, context.getString(R.string.tv_pin_encry));
                            }
                        }else {
                            try {
                                cipherOutput = mCrypto.encrypt(symmetricKey, cipherType, paddingMode, encryptIVData ,datain);
                                cipherout = cipherOutput.getData();
                                mEditor.putString(AppConfig.SharedPreferenceConfig.ENCRYPTION_CIPHER_OUT, ISOUtils.hexString(cipherout));
                                mEditor.commit();
                                showMessage("Encryption Result:" + ISOUtils.hexString(cipherOutput.getData()));
                            }catch (NSDKException e) {
                                showErrorMessage(e, context.getString(R.string.tv_pin_encry));
                            }
                        }
                    }else {
                        SymmetricKey symmetricKey = new SymmetricKey();
                        symmetricKey.setKeyID((byte)keyID);
                        symmetricKey.setKeyUsage(keyUsage);
                        if(llDUKPTEncryption.getVisibility() == View.VISIBLE) {
                            DUKPTDerivateKey dukptDerivateKey = new DUKPTDerivateKey();
                            dukptDerivateKey.setKeyID((byte)keyID);
                            dukptDerivateKey.setKeyUsage(keyUsage);
                            dukptDerivateKey.setDerivateKeyLen(dukptDerivateLen);
                            dukptDerivateKey.setDerivateKeyType(dukptDerivateKeyType);
                            dukptDerivateKey.setDerivateUsage(dukptDerivateUsage);
                            try {
                                cipherOutput = mCrypto.encrypt(dukptDerivateKey, cipherType, paddingMode, encryptIVData, datain);
                                showMessage("Encryption Result:" + ISOUtils.hexString(cipherOutput.getData()));
                                mEditor.putString(AppConfig.SharedPreferenceConfig.ENCRYPTION_CIPHER_OUT, ISOUtils.hexString(cipherOutput.getData()));
                                mEditor.commit();
                            }catch (NSDKException e) {
                                showErrorMessage(e, context.getString(R.string.tv_pin_encry));
                            }
                        }else {
                            try {
                                cipherOutput = mCrypto.encrypt(symmetricKey, cipherType, paddingMode, encryptIVData ,datain);
                                cipherout = cipherOutput.getData();
                                mEditor.putString(AppConfig.SharedPreferenceConfig.ENCRYPTION_CIPHER_OUT, ISOUtils.hexString(cipherout));
                                mEditor.commit();
                                showMessage("Encryption Result:" + ISOUtils.hexString(cipherOutput.getData()));
                            }catch (NSDKException e) {
                                showErrorMessage(e, context.getString(R.string.tv_pin_encry));
                            }
                        }
                    }
                }else if(rbAsymEncrypt.isChecked()){
                    LogUtils.d("asymEncrypt", String.valueOf(asymDefaultKeyID));
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
                            cipherout = mCrypto.encryptAsym(asymmetricKey, asymAlgorithmParameters, ISOUtils.hex2byte(etAsymDataIn.getText().toString()));
                            mEditor.putString(AppConfig.SharedPreferenceConfig.ASYM_ENCRYPTION_CIPHER_OUT, ISOUtils.hexString(cipherout));
                            mEditor.commit();
                            showMessage("Asym Encryption Result:" + ISOUtils.hexString(cipherout));
                        }catch (NSDKException e) {
                            showErrorMessage(e, context.getString(R.string.tv_pin_asym_encry));
                        }
                    }else {
                        EditText etAsymKeyID = view.findViewById(R.id.spn_crypto_encrypt_asym_keyID);
                        asymKeyID = Integer.parseInt(etAsymKeyID.getText().toString());
                        mEditor.putString(AppConfig.SharedPreferenceConfig.ENCRYPT_ASYM_KEY_ID, etAsymKeyID.getText().toString());
                        mEditor.commit();
                        AsymmetricKey asymmetricKey = new AsymmetricKey();
                        asymmetricKey.setKeyUsage(asymKeyUsage);
                        asymmetricKey.setKeyID((byte) asymKeyID);
                        asymmetricKey.setKeyType(asymKeyType);

                        AsymAlgorithmParameters asymAlgorithmParameters = new AsymAlgorithmParameters();
                        asymAlgorithmParameters.setCryptoMode(asymCryptoMode);
                        asymAlgorithmParameters.setEncodingMode(asymEncodingMode);
                        asymAlgorithmParameters.setMessageDigestType(messageDigestType);

                        try {
                            cipherout = mCrypto.encryptAsym(asymmetricKey, asymAlgorithmParameters, ISOUtils.hex2byte(etAsymDataIn.getText().toString()));
                            mEditor.putString(AppConfig.SharedPreferenceConfig.ASYM_ENCRYPTION_CIPHER_OUT, ISOUtils.hexString(cipherout));
                            mEditor.commit();
                            showMessage("Asym Encryption Result:" + ISOUtils.hexString(cipherout));
                        }catch (NSDKException e) {
                            showErrorMessage(e, context.getString(R.string.tv_pin_asym_encry));
                        }
                    }

                }

                }

        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_decrypt_test, functionid = 16)
    private void decryptOptional() {
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
                etDataIn.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.ENCRYPTION_CIPHER_OUT, ""));
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
                        if("DUKPT_AES_INDEX".equals(spnDefaultKeyID.getSelectedItem().toString())) {
                            ArrayAdapter adapter = EnumUtils.getAESDUKPTCipherTypeArrayAdapter(context);
                            spnCipherType.setAdapter(adapter);
                            llDecryptAESDUKPTParams.setVisibility(View.VISIBLE);
                        } else {
                            ArrayAdapter adapter = EnumUtils.getOtherCipherTypeArrayAdapter(context);
                            spnCipherType.setAdapter(adapter);
                            llDecryptAESDUKPTParams.setVisibility(View.GONE);
                        }
                        if(rbDecrypt.isChecked()) {
                            llDefaultKeyIDParams.setVisibility(View.VISIBLE);
                            llEditKeyIDParams.setVisibility(View.GONE);
                            llDefaultKeyTypeParams.setVisibility(View.GONE);
                        }else if(rbAsymDecrypt.isChecked()) {
                            llDecryptAsymDefaultKeyIDParams.setVisibility(View.VISIBLE);
                            llDecryptAsymEditKeyIDParams.setVisibility(View.GONE);
                        }
                    }else {
                        if("AES".equals(spnKeyType.getSelectedItem().toString()) && "DUKPT".equals(spnKeyUsage.getSelectedItem().toString())) {
                            ArrayAdapter adapter = EnumUtils.getAESDUKPTCipherTypeArrayAdapter(context);
                            spnCipherType.setAdapter(adapter);
                        } else {
                            ArrayAdapter adapter = EnumUtils.getOtherCipherTypeArrayAdapter(context);
                            spnCipherType.setAdapter(adapter);
                        }
                        if(rbDecrypt.isChecked()) {
                            llDefaultKeyIDParams.setVisibility(View.GONE);
                            llEditKeyIDParams.setVisibility(View.VISIBLE);
                            llDefaultKeyTypeParams.setVisibility(View.VISIBLE);
                        }else if(rbAsymDecrypt.isChecked()){
                            llDecryptAsymEditKeyIDParams.setVisibility(View.VISIBLE);
                            llDecryptAsymDefaultKeyIDParams.setVisibility(View.GONE);

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
                            etDataIn.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.ENCRYPTION_CIPHER_OUT, ""));
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
                            etDataIn.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.ASYM_ENCRYPTION_CIPHER_OUT, ""));
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
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.DECRYPT_DERIVATE_KEY_TYPE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int derivateKeyType = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.DECRYPT_DERIVATE_KEY_TYPE, 0);
                spnDerivateKeyType.setSelection(derivateKeyType);

                spnDerivateKeyLen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.DECRYPT_DERIVATE_KEY_LEN, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int derivateKeyLen = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.DECRYPT_DERIVATE_KEY_LEN, 0);
                spnDerivateKeyLen.setSelection(derivateKeyLen);

                spnDerivateUsage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.DECRYPT_DERIVATE_KEY_USAGE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int derivateKeyUsage = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.DECRYPT_DERIVATE_KEY_USAGE, 0);
                spnDerivateUsage.setSelection(derivateKeyUsage);

                spnCipherType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.DECRYPT_CIPHER_TYPE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int cipherType = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.DECRYPT_CIPHER_TYPE, 0);
                spnCipherType.setSelection(cipherType);

                spnPaddingMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.DECRYPT_PADDING_MODE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int paddingMode = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.DECRYPT_PADDING_MODE, 0);
                spnPaddingMode.setSelection(paddingMode);


                spnAsymKeyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.DECRYPT_ASYM_KEY_TYPE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int asymKeyType = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.DECRYPT_ASYM_KEY_TYPE, 0);
                spnAsymKeyType.setSelection(asymKeyType);

                spnAsymKeyUsage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.DECRYPT_ASYM_KEY_USAGE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int asymKeyUsage = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.DECRYPT_ASYM_KEY_USAGE, 0);
                spnAsymKeyUsage.setSelection(asymKeyUsage);

                spnMessageDigestType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.DECRYPT_ASYM_MESSAGE_DIGEST_TYPE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int messageDigestType = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.DECRYPT_ASYM_MESSAGE_DIGEST_TYPE, 0);
                spnMessageDigestType.setSelection(messageDigestType);

                spnAsymCryptoMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.DECRYPT_ASYM_CRYPTO_MODE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int asymCryptoMode = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.DECRYPT_ASYM_CRYPTO_MODE, 0);
                spnAsymCryptoMode.setSelection(asymCryptoMode);

                spnAsymEncodingMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.DECRYPT_ASYM_ENCODING_MODE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int asymEncodingMode = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.DECRYPT_ASYM_ENCODING_MODE, 0);
                spnAsymEncodingMode.setSelection(asymEncodingMode);

                spnAESDUKPTCipherType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.DECRYPT_AES_DUKPT_CIPHER_TYPE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int aesDUKPTCipherType = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.DECRYPT_AES_DUKPT_CIPHER_TYPE, 0);
                spnAESDUKPTCipherType.setSelection(aesDUKPTCipherType);


                EditText etDecryptKeyID = view.findViewById(R.id.et_decrypt_edit_keyID);
                etDecryptKeyID.setText(String.valueOf(sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.DECRYPT_KEY_ID, 0)));
                EditText etAsymKeyID = view.findViewById(R.id.et_asym_editKeyID);
                etAsymKeyID.setText(String.valueOf(sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.DECRYPT_ASYM_KEY_ID, 0)));

                spnDefaultKeyID.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (swDefaultKeyId.isChecked() && "DUKPT_AES_INDEX".equals(spnDefaultKeyID.getSelectedItem().toString())) {
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
                mEditor.putString(AppConfig.SharedPreferenceConfig.ENCRYPT_PADDING_DATA, etIVData.getText().toString());
                mEditor.commit();
                EditText etDataIn = view.findViewById(R.id.et_decrypt_data);
                cipherout = ISOUtils.hex2byte(etDataIn.getText().toString());
                EditText etKeyID = view.findViewById(R.id.et_decrypt_edit_keyID);
                int decryptKeyID = Integer.parseInt(etKeyID.getText().toString());
                mEditor.putInt(AppConfig.SharedPreferenceConfig.DECRYPT_KEY_ID, decryptKeyID);
                mEditor.commit();
                CipherOutput cipherOutput;

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
                                cipherOutput = mCrypto.decrypt(decryptDukptDerivateKey, decryptCipherType, decryptPaddingMode, decryptIVData, cipherout);
                                showMessage("DUKPTDerivateKey decrypt result: " + ISOUtils.hexString(cipherOutput.getData()));
                            }catch (NSDKException e) {
                                showErrorMessage(e, context.getString(R.string.tv_pin_aes_dukpt_decry));
                            }
                        }else {
                            try {
                                cipherOutput = mCrypto.decrypt(decryptKey, decryptCipherType, decryptPaddingMode, decryptIVData, cipherout);
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
                                cipherOutput = mCrypto.decrypt(decryptDukptDerivateKey, decryptCipherType, decryptPaddingMode, decryptIVData, cipherout);
                                showMessage("DUKPTDerivateKey decrypt result: " + ISOUtils.hexString(cipherOutput.getData()));
                            }catch (NSDKException e) {
                                showErrorMessage(e, context.getString(R.string.tv_pin_aes_dukpt_decry));
                            }
                        }else {
                            try {
                                cipherOutput = mCrypto.decrypt(decryptKey, decryptCipherType, decryptPaddingMode, decryptIVData, cipherout);
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
                            decrypt_cipherout = mCrypto.decryptAsym(decryptAsymmetricKey, decryptAsymAlgorithmParameters, cipherout);
                            showMessage("Asym Decryption result: " + ISOUtils.hexString(decrypt_cipherout));
                        }catch (NSDKException e) {
                            showErrorMessage(e, context.getString(R.string.tv_pin_asym_decry));
                        }
                    }else {
                        EditText etAsymKeyID = view.findViewById(R.id.et_asym_editKeyID);
                        decryptAsymKeyID = Integer.parseInt(etAsymKeyID.getText().toString());
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.DECRYPT_ASYM_KEY_ID, decryptAsymKeyID);
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
                            decrypt_cipherout = mCrypto.decryptAsym(decryptAsymmetricKey, decryptAsymAlgorithmParameters, cipherout);
                            showMessage("Asym Decryption result: " + ISOUtils.hexString(decrypt_cipherout));
                        }catch (NSDKException e) {
                            showErrorMessage(e, context.getString(R.string.tv_pin_asym_decry));
                        }
                    }

                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_cal_mac, functionid = 17)
    private void calculateMac() {
        DialogUtils.createCustomDialog(context, R.string.tv_pin_cal_mac, null, R.layout.dialog_crypto_caculate_mac, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                Switch swDefaultKeyID = view.findViewById(R.id.switch_default_key_id_btn);
                swDefaultKeyID.setChecked(true);
                LinearLayout llUseDefaultKeyIDParams = view.findViewById(R.id.linear_generate_mac_by_default_key_id_params);
                llUseDefaultKeyIDParams.setVisibility(View.VISIBLE);
                LinearLayout llUseEditKeyIDParams = view.findViewById(R.id.linear_generate_mac_by_edit_key_params);
                llUseEditKeyIDParams.setVisibility(View.GONE);
                LinearLayout llDefaultKeyIDAESDUKPTMacParams = view.findViewById(R.id.linear_caculate_mac_default_key_id_aes_dukpt_params);
                llDefaultKeyIDAESDUKPTMacParams.setVisibility(View.GONE);
                swDefaultKeyID.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if(isChecked) {
                        llUseDefaultKeyIDParams.setVisibility(View.VISIBLE);
                        llUseEditKeyIDParams.setVisibility(View.GONE);
                    } else {
                        llUseDefaultKeyIDParams.setVisibility(View.GONE);
                        llUseEditKeyIDParams.setVisibility(View.VISIBLE);
                    }
                });


                //DefaultKeyID
                Spinner spnDefaultMacType = view.findViewById(R.id.spn_caculate_mac_default_key_id_macType);
                spnDefaultMacType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.GENERATE_MAC_DEFAULT_KEY_ID_MAC_TYPE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int defaultMacTypePosition = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.GENERATE_MAC_DEFAULT_KEY_ID_MAC_TYPE, 0);
                spnDefaultMacType.setSelection(defaultMacTypePosition);

                Spinner spnDefaultKeyID = view.findViewById(R.id.spn_caculate_mac_default_keyID);
                spnDefaultKeyID.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if("DUKPT_AES_INDEX".equals(spnDefaultKeyID.getSelectedItem().toString())) {
                            llDefaultKeyIDAESDUKPTMacParams.setVisibility(View.VISIBLE);
                            ArrayAdapter adapter = EnumUtils.getAESDUKPTMacTypeArrayAdapter(context);
                            spnDefaultMacType.setAdapter(adapter);
                        }else {
                            llDefaultKeyIDAESDUKPTMacParams.setVisibility(View.GONE);
                            ArrayAdapter adapter = EnumUtils.getOtherMacTypeArrayAdapter(context);
                            spnDefaultMacType.setAdapter(adapter);
                        }
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.GENERATE_MAC_DEFAULT_KEY_ID, position);
                        mEditor.commit();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int defaultKeyIDSelection = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.GENERATE_MAC_DEFAULT_KEY_ID, 0);
                spnDefaultKeyID.setSelection(defaultKeyIDSelection);

                Spinner spnDefaultKeyIDDerivateKeyType = view.findViewById(R.id.spn_caculate_mac_default_key_id_derivate_keyType);
                spnDefaultKeyIDDerivateKeyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if("DUKPT_AES_INDEX".equals(spnDefaultKeyID.getSelectedItem().toString())) {
                            spnDefaultKeyIDDerivateKeyType.setSelection(1);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                Spinner spnDefaultKeyIDDerivateKeyUsage = view.findViewById(R.id.spn_caculate_mac_default_key_id_derivate_keyUsage);
                spnDefaultKeyIDDerivateKeyUsage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if("DUKPT_AES_INDEX".equals(spnDefaultKeyID.getSelectedItem().toString())) {
                            spnDefaultKeyIDDerivateKeyUsage.setSelection(3);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                Spinner spnDefaultKeyIDDerivateKeyLen = view.findViewById(R.id.spn_caculate_mac_default_key_id_derivate_keyLen);
                spnDefaultKeyIDDerivateKeyLen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if("DUKPT_AES_INDEX".equals(spnDefaultKeyID.getSelectedItem().toString())) {
                            ArrayAdapter adapter = EnumUtils.getAESDUKPTMacTypeArrayAdapter(context);
                            spnDefaultMacType.setAdapter(adapter);
                            spnDefaultKeyIDDerivateKeyLen.setSelection(1);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                //EditKeyID
                LinearLayout llEditKeyIDAESDUKPTParams = view.findViewById(R.id.linear_caculate_mac_edit_key_id_aes_dukpt_params);
                llEditKeyIDAESDUKPTParams.setVisibility(View.GONE);

                Spinner spnEditMacType = view.findViewById(R.id.spn_caculate_mac_edit_key_id_macType);
                spnEditMacType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(spnEditMacType.getSelectedItem().toString().contains("AES_DUKPT")) {
                            llEditKeyIDAESDUKPTParams.setVisibility(View.VISIBLE);
                            ArrayAdapter adapter = EnumUtils.getAESDUKPTMacTypeArrayAdapter(context);
                            spnEditMacType.setAdapter(adapter);
                        }else {
                            llEditKeyIDAESDUKPTParams.setVisibility(View.GONE);
                            ArrayAdapter adapter = EnumUtils.getOtherMacTypeArrayAdapter(context);
                            spnEditMacType.setAdapter(adapter);
                        }
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.GENERATE_MAC_EDIT_MAC_TYPE, position);
                        mEditor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int editKeyIDMacTypePosition = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.GENERATE_MAC_EDIT_MAC_TYPE, 0);
                spnEditMacType.setSelection(editKeyIDMacTypePosition);

                Spinner spnEditKeyIDDerivateKeyType = view.findViewById(R.id.spn_caculate_mac_edit_key_id_derivate_keyType);
                spnEditKeyIDDerivateKeyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(spnEditMacType.getSelectedItem().toString().contains("AES_DUKPT")) {
                            spnEditKeyIDDerivateKeyType.setSelection(1);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                Spinner spnEditKeyIDDerivateKeyUsage = view.findViewById(R.id.spn_caculate_mac_edit_key_id_derivate_keyUsage);
                spnEditKeyIDDerivateKeyUsage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(spnEditMacType.getSelectedItem().toString().contains("AES_DUKPT")) {
                            spnEditKeyIDDerivateKeyUsage.setSelection(3);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                Spinner spnEditKeyIDDerivateKeyLen = view.findViewById(R.id.spn_caculate_mac_edit_key_id_derivate_keyLen);
                spnEditKeyIDDerivateKeyLen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(spnEditMacType.getSelectedItem().toString().contains("AES_DUKPT")) {
                            spnEditKeyIDDerivateKeyLen.setSelection(1);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                EditText etKeyID = view.findViewById(R.id.et_caculate_mac_keyID);
                etKeyID.setText(String.valueOf(sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.GENERATE_MAC_EDIT_KEY_ID, 1)));
                EditText etOriginalData = view.findViewById(R.id.et_generate_mac_original_data);
                etOriginalData.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.GENERATE_MAC_ORIGINAL_DATA, ""));
                EditText etIVData = view.findViewById(R.id.et_generate_mac_IV_data);
                etIVData.setText(sharedPreferences.getString(AppConfig.SharedPreferenceConfig.GENERATE_MAC_IV_DATA, ""));
            }

            @Override
            public void onResult(int id, View view) {
                MACOutput macOutput = null;
                Switch swDefaultKeyID = view.findViewById(R.id.switch_default_key_id_btn);
                EditText etOriginalData = view.findViewById(R.id.et_generate_mac_original_data);
                EditText etIVData = view.findViewById(R.id.et_generate_mac_IV_data);
                byte[] data = ISOUtils.hex2byte(etOriginalData.getText().toString());
                mEditor.putString(AppConfig.SharedPreferenceConfig.GENERATE_MAC_ORIGINAL_DATA, ISOUtils.hexString(data));
                mEditor.commit();
                byte[] iv = ISOUtils.hex2byte(etIVData.getText().toString());
                mEditor.putString(AppConfig.SharedPreferenceConfig.GENERATE_MAC_IV_DATA, ISOUtils.hexString(iv));
                //DefaultKeyID
                Spinner spnDefaultKeyID = view.findViewById(R.id.spn_caculate_mac_default_keyID);
                Spinner spnDefaultMacType = view.findViewById(R.id.spn_caculate_mac_default_key_id_macType);
                Spinner spnDefaultKeyIDDerivateKeyType = view.findViewById(R.id.spn_caculate_mac_default_key_id_derivate_keyType);
                Spinner spnDefaultKeyIDDerivateKeyUsage = view.findViewById(R.id.spn_caculate_mac_default_key_id_derivate_keyUsage);
                Spinner spnDefaultKeyIDDerivateKeyLen = view.findViewById(R.id.spn_caculate_mac_default_key_id_derivate_keyLen);
                LinearLayout llDefaultKeyIDAESDUKPTMacParams = view.findViewById(R.id.linear_caculate_mac_default_key_id_aes_dukpt_params);

                byte defaultKeyID = EnumUtils.getDefaultKeyID(spnDefaultKeyID.getSelectedItem().toString());
                MACType defaultKeyIDMacType = EnumUtils.getMacType(spnDefaultMacType.getSelectedItem().toString());
                KeyType defaultKeyIDDerivateKeyType = EnumUtils.getKeyType(spnDefaultKeyIDDerivateKeyType.getSelectedItem().toString());
                DUKPTDerivateUsage defaultKeyIDDerivateKeyUsage = EnumUtils.getDukptDerivateUsage(spnDefaultKeyIDDerivateKeyUsage.getSelectedItem().toString());
                int defaultKeyIDDerivateKeyLen = EnumUtils.getKeyLen(spnDefaultKeyIDDerivateKeyLen.getSelectedItem().toString());
                //EditKeyID
                LinearLayout llEditKeyIDAESDUKPTParams = view.findViewById(R.id.linear_caculate_mac_edit_key_id_aes_dukpt_params);
                Spinner spnEditMacType = view.findViewById(R.id.spn_caculate_mac_edit_key_id_macType);
                Spinner spnEditKeyIDDerivateKeyType = view.findViewById(R.id.spn_caculate_mac_edit_key_id_derivate_keyType);
                Spinner spnEditKeyIDDerivateKeyLen = view.findViewById(R.id.spn_caculate_mac_edit_key_id_derivate_keyLen);
                Spinner spnEditKeyIDDerivateKeyUsage = view.findViewById(R.id.spn_caculate_mac_edit_key_id_derivate_keyUsage);
                EditText etKeyID = view.findViewById(R.id.et_caculate_mac_keyID);

                MACType editKeyIDMacType = EnumUtils.getMacType(spnEditMacType.getSelectedItem().toString());
                KeyType editKeyIDDerivateKeyType = EnumUtils.getKeyType(spnEditKeyIDDerivateKeyType.getSelectedItem().toString());
                DUKPTDerivateUsage editKeyIDDerivateKeyUsage = EnumUtils.getDukptDerivateUsage(spnEditKeyIDDerivateKeyUsage.getSelectedItem().toString());
                int editKeyIDDerivateKeyLen = EnumUtils.getKeyLen(spnEditKeyIDDerivateKeyLen.getSelectedItem().toString());
                if(swDefaultKeyID.isChecked()) {
                    if(llDefaultKeyIDAESDUKPTMacParams.getVisibility() == View.VISIBLE) {
                        DUKPTDerivateKey dukptDerivateKey = new DUKPTDerivateKey();
                        dukptDerivateKey.setKeyID(defaultKeyID);
                        dukptDerivateKey.setDerivateUsage(defaultKeyIDDerivateKeyUsage);
                        dukptDerivateKey.setDerivateKeyLen(defaultKeyIDDerivateKeyLen);
                        dukptDerivateKey.setDerivateKeyType(defaultKeyIDDerivateKeyType);
                        try {
                            macOutput = mCrypto.generateMAC(dukptDerivateKey, defaultKeyIDMacType, iv, data);
                            showMessage("AES DUKPT MAC: " + ISOUtils.hexString(macOutput.getData()));
                        }catch (NSDKException e) {
                            showErrorMessage(e, context.getString(R.string.tv_pin_cal_mac_aes_dukpt));
                        }
                    }else {
                        try {
                            macOutput = mCrypto.generateMAC(defaultKeyID, defaultKeyIDMacType, iv, data);
                            showMessage("MAC: " + ISOUtils.hexString(macOutput.getData()));
                        }catch (NSDKException e) {
                            showErrorMessage(e, context.getString(R.string.tv_pin_cal_mac));
                        }
                    }
                }else {
                    int editKeyID = Integer.parseInt(etKeyID.getText().toString());
                    mEditor.putInt(AppConfig.SharedPreferenceConfig.GENERATE_MAC_EDIT_KEY_ID, editKeyID);
                    mEditor.commit();
                    if(llEditKeyIDAESDUKPTParams.getVisibility() == View.VISIBLE) {
                        DUKPTDerivateKey dukptDerivateKey = new DUKPTDerivateKey();
                        dukptDerivateKey.setKeyID((byte) editKeyID);
                        dukptDerivateKey.setDerivateKeyType(editKeyIDDerivateKeyType);
                        dukptDerivateKey.setDerivateUsage(editKeyIDDerivateKeyUsage);
                        dukptDerivateKey.setDerivateKeyLen(editKeyIDDerivateKeyLen);
                        try {
                            macOutput = mCrypto.generateMAC(dukptDerivateKey, editKeyIDMacType, iv ,data);
                            showMessage("AES DUKPT MAC: " + ISOUtils.hexString(macOutput.getData()));
                        }catch (NSDKException e) {
                            showErrorMessage(e, context.getString(R.string.tv_pin_cal_mac_aes_dukpt));
                        }
                    }else {
                        try {
                            macOutput = mCrypto.generateMAC((byte) editKeyID, editKeyIDMacType, iv, data);
                            showMessage("MAC: " + ISOUtils.hexString(macOutput.getData()));
                        }catch (NSDKException e) {
                            showErrorMessage(e, context.getString(R.string.tv_pin_cal_mac));
                        }
                    }
                }

            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_aes_ctr_encrypy, functionid = 18)
    private void aesCtrEncrypt() {
        byte[] iv = ISOUtils.hex2byte("11223344556677889900000000000000");
        byte[] data = ISOUtils.hex2byte("222222222222222222222222222222223333333333333333333333333333333344444444");
        try {
            SymmetricKey symmetricKey = new SymmetricKey();
            symmetricKey.setKeyID(AppConfig.Keys.MKSK_AES_INDEX_WK_DATA);
            symmetricKey.setKeyType(KeyType.AES);
            symmetricKey.setKeyUsage(KeyUsage.DATA);

            CipherOutput cipherOutput = mCrypto.encrypt(symmetricKey, CipherType.AES_CTR, PaddingMode.NONE, iv, data);
            aesCtrCipherOut = cipherOutput.getData();
            showMessage("Data:" + ISOUtils.hexString(cipherOutput.getData()));
            showMessage("Ksn:" + ISOUtils.hexString(cipherOutput.getKsn()));
        } catch (NSDKException e) {
            showMessage(e.getMessage(), MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_cal_hmac_sha_256_mac, functionid = 19)
    private void calculateSHA256MAC() {
        byte[] iv = ISOUtils.hex2byte("11223344556677889900000000000000");
        try {
            MACOutput macOutput = mCrypto.generateMAC(AppConfig.Keys.HMAC_KEY_ID, MACType.HMAC_SHA256, iv, aesCtrCipherOut);
            showMessage("Generate SHA256 MAC:" + ISOUtils.hexString(macOutput.getData()));
        } catch (NSDKException e) {
            showMessage(e.getMessage(), MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_encrypt_big_data_aes_ecb, functionid = 20)
    private void encryptBigDataWithAesEcb() {
        String aesEcbExpectedResult = ISOUtils.hexString(getDataByName(AppConfig.DataXMLName.EXPECTED_ENCRYPT_BIG_DATA_AES_ECB));
        byte[] bigData = new byte[4000];
        byte[] data = ISOUtils.hex2byte("2021222324252627");
        for (int i = 0; i < 500; i++) {
            System.arraycopy(data, 0, bigData, i * 8, data.length);
        }
        try {
            SymmetricKey symmetricKey = new SymmetricKey();
            symmetricKey.setKeyType(KeyType.AES);
            symmetricKey.setKeyUsage(KeyUsage.DATA);
            symmetricKey.setKeyID(AppConfig.Keys.MKSK_AES_INDEX_WK_DATA);
            CipherOutput cipherOutput = cryptoHelper.encrypt(symmetricKey, CipherType.AES_ECB, PaddingMode.NONE, null, bigData);
            byte[] result = cipherOutput.getData();
            if (aesEcbExpectedResult.equals(ISOUtils.hexString(result))) {
                showMessage("Encrypt BigData with AES ECB mode success, and it meets the expected result.");
            }
        } catch (NSDKException e) {
            showErrorMessage(e, "Encrypt BigData with AES ECB mode failed.");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_encrypt_big_data_aes_cbc, functionid = 21)
    private void encryptBigDataWithAesCbc() {
        String aesCbcExpectedResult = ISOUtils.hexString(getDataByName(AppConfig.DataXMLName.EXPECTED_ENCRYPT_BIG_DATA_AES_CBC));
        byte[] bigData = getDataByName(AppConfig.DataXMLName.ENCRYPT_BIG_DATA_AES_CBC);
        byte[] iv = ISOUtils.hex2byte("000102030405060708090A0B0C0D0E0F");
        SymmetricKey aesDataKey = new SymmetricKey();
        aesDataKey.setKeyID(AppConfig.Keys.MKSK_AES_INDEX_WK_DATA);
        aesDataKey.setKeyType(KeyType.AES);
        aesDataKey.setKeyUsage(KeyUsage.DATA);

        try {
            CipherOutput cipherOutput = cryptoHelper.encrypt(aesDataKey, CipherType.AES_CBC, PaddingMode.ZEROS, iv, bigData);
            if (aesCbcExpectedResult.equals(ISOUtils.hexString(cipherOutput.getData()))) {
                showMessage("Encrypt BigData with AES CBC mode success, and it meets the expected result.");
            } else {
                showMessage("Encrypt BigData with AES CBC mode success, but it didn't meet the expected result.", MessageTag.ERROR);
            }
        } catch (NSDKException e) {
            showErrorMessage(e, "Encrypt BigData with AES CBC mode failed. ");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_encrypt_big_data_des_ecb, functionid = 22)
    private void encryptBigDataWithDesEcb() {
        String desEcbExpectedResult = ISOUtils.hexString(getDataByName(AppConfig.DataXMLName.EXPECTED_ENCRYPT_BIG_DATA_DES_ECB));
        byte[] bigData = new byte[4000];
        byte[] data = ISOUtils.hex2byte("2021222324252627");
        for (int i = 0; i < 500; i++) {
            System.arraycopy(data, 0, bigData, i * 8, data.length);
        }
        SymmetricKey desDataKey = new SymmetricKey();
        desDataKey.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_WK_DATA);
        desDataKey.setKeyType(KeyType.DES);
        desDataKey.setKeyUsage(KeyUsage.DATA);

        try {
            CipherOutput cipherOutput = cryptoHelper.encrypt(desDataKey, CipherType.DES_ECB, PaddingMode.NONE, null, bigData);
            if (desEcbExpectedResult.equals(ISOUtils.hexString(cipherOutput.getData()))) {
                showMessage("Encrypt BigData with DES EBC mode success, and it meets the expected result.");
            } else {
                showMessage("Encrypt BigData with DES ECB mode success, but it didn't meet the expected result.", MessageTag.ERROR);
            }
        } catch (NSDKException e) {
            showErrorMessage(e, "Encrypt BigData with DES ECB mode failed.");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_encrypt_big_data_des_cbc, functionid = 23)
    private void encryptBigDataWithDesCbc() {
        String desCbcExpectedResult = ISOUtils.hexString(getDataByName(AppConfig.DataXMLName.EXPECTED_ENCRYPT_BIG_DATA_DES_CBC));
        byte[] iv = ISOUtils.hex2byte("0001020304050607");
        byte[] bigData = getDataByName(AppConfig.DataXMLName.ENCRYPT_BIG_DATA_DES_CBC);
        SymmetricKey desDataKey = new SymmetricKey();
        desDataKey.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_WK_DATA);
        desDataKey.setKeyType(KeyType.DES);
        desDataKey.setKeyUsage(KeyUsage.DATA);

        try {
            CipherOutput cipherOutput = cryptoHelper.encrypt(desDataKey, CipherType.DES_CBC, PaddingMode.ZEROS, iv, bigData);
            if (desCbcExpectedResult.equals(ISOUtils.hexString(cipherOutput.getData()))) {
                showMessage("Encrypt BigData with DES CBC mode success, and it meets the expected result.");
            } else {
                showMessage("Encrypt BigData with DES CBC mode success, but it didn't meet the expected result.", MessageTag.ERROR);
            }
        } catch (NSDKException e) {
            showErrorMessage(e, "Encrypt BigData with DES CBC mode failed.");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_decrypt_big_data_aes_ecb, functionid = 24)
    private void decryptBigDataWithAesEcb() {
        byte[] encryptedBigData = getDataByName(AppConfig.DataXMLName.DECRYPT_BIG_DATA_AES_ECB);
        SymmetricKey aesDataKey = new SymmetricKey();
        aesDataKey.setKeyID(AppConfig.Keys.MKSK_AES_INDEX_WK_DATA);
        aesDataKey.setKeyType(KeyType.AES);
        aesDataKey.setKeyUsage(KeyUsage.DATA);
        byte[] aesEcbExpectedResult = new byte[4000];
        byte[] data = ISOUtils.hex2byte("2021222324252627");
        for (int i = 0; i < 500; i++) {
            System.arraycopy(data, 0, aesEcbExpectedResult, i * 8, data.length);
        }
        try {
            CipherOutput cipherOutput = cryptoHelper.decrypt(aesDataKey, CipherType.AES_ECB, PaddingMode.NONE, null, encryptedBigData);
            Log.d(TAG, "Decrypted Data:" + ISOUtils.hexString(cipherOutput.getData()));
            if (ISOUtils.hexString(aesEcbExpectedResult).equals(ISOUtils.hexString(cipherOutput.getData()))) {
                showMessage("Decrypt BigData with AES ECB mode success, and it meets the expected result.");
            }
        } catch (NSDKException e) {
           showErrorMessage(e, "Decrypt BigData with AES ECB mode failed.");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_decrypt_big_data_aes_cbc, functionid = 25)
    private void decryptBigDataWithAesCbc() {
        String aesCbcExpectedResult = ISOUtils.hexString(getDataByName(AppConfig.DataXMLName.EXPECTED_DECRYPT_BIG_DATA_AES_CBC));
        byte[] iv = ISOUtils.hex2byte("000102030405060708090A0B0C0D0E0F");
        byte[] encryptedBigData = getDataByName(AppConfig.DataXMLName.DECRYPT_BIG_DATA_AES_CBC);
        SymmetricKey aesDataKey = new SymmetricKey();
        aesDataKey.setKeyID(AppConfig.Keys.MKSK_AES_INDEX_WK_DATA);
        aesDataKey.setKeyType(KeyType.AES);
        aesDataKey.setKeyUsage(KeyUsage.DATA);

        try {
            CipherOutput cipherOutput = cryptoHelper.decrypt(aesDataKey, CipherType.AES_CBC, PaddingMode.ZEROS, iv, encryptedBigData);
            if (aesCbcExpectedResult.equals(ISOUtils.hexString(cipherOutput.getData()))) {
                showMessage("Decrypt BigData with AES CBC mode success, and it meets the expected result.");
            } else {
                showMessage("Decrypt BigData with AES CBC mode success, but it didn't meet the expected result.", MessageTag.ERROR);
            }
        } catch (NSDKException e) {
            showErrorMessage(e, "Decrypt BigData with AES CBC mode failed.");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_decrypt_big_data_des_ecb, functionid = 26)
    private void decryptBigDataWithDesEcb() {
        String desEcbExpectedResult = ISOUtils.hexString(getDataByName(AppConfig.DataXMLName.EXPECTED_DECRYPT_BIG_DATA_DES_ECB));
        byte[] encryptedBigData = getDataByName(AppConfig.DataXMLName.DECRYPT_BIG_DATA_DES_ECB);
        SymmetricKey desDataKey = new SymmetricKey();
        desDataKey.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_WK_DATA);
        desDataKey.setKeyType(KeyType.DES);
        desDataKey.setKeyUsage(KeyUsage.DATA);

        try {
            CipherOutput cipherOutput = cryptoHelper.decrypt(desDataKey, CipherType.DES_ECB, PaddingMode.NONE, null, encryptedBigData);
            if (desEcbExpectedResult.equals(ISOUtils.hexString(cipherOutput.getData()))) {
                showMessage("Decrypt BigData with DES ECB mode success, and it meets the expected result.");
            } else {
                showMessage("Decrypt BigData with DES ECB mode success, but it didn't meet the expected result.", MessageTag.ERROR);
            }
        } catch (NSDKException e) {
            showErrorMessage(e, "Decrypt BigData with DES ECB mode failed.");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_decrypt_big_data_des_cbc, functionid = 27)
    private void decryptBigDataWithDesCbc() {
        String desCbcExpectedResult = ISOUtils.hexString(getDataByName(AppConfig.DataXMLName.EXPECTED_DECRYPT_BIG_DATA_DES_CBC));
        byte[] encryptedBigData = getDataByName(AppConfig.DataXMLName.DECRYPT_BIG_DATA_DES_CBC);
        byte[] iv = ISOUtils.hex2byte("0001020304050607");
        SymmetricKey desDataKey = new SymmetricKey();
        desDataKey.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_WK_DATA);
        desDataKey.setKeyType(KeyType.DES);
        desDataKey.setKeyUsage(KeyUsage.DATA);

        try {
             CipherOutput cipherOutput = cryptoHelper.decrypt(desDataKey, CipherType.DES_CBC, PaddingMode.ZEROS, iv, encryptedBigData);
             if (desCbcExpectedResult.equals(ISOUtils.hexString(cipherOutput.getData()))) {
                 showMessage("Decrypt BigData with DES CBC mode success, and it meets the expected result.");
             } else {
                 showMessage("Decrypt BigData with DES CBC mode success, but it didn't meet the expected result.", MessageTag.ERROR);
             }
        } catch (NSDKException e) {
            showErrorMessage(e, "Decrypt BigData with DES CBC mode failed.");
        }
    }
    @MethodGridEntity(btnnameid = R.string.tv_pin_encrypt_big_data_aes_ctr, functionid = 28)
    private void encryptBigDataWithAesCtr() {
        String aesCtrExpectedResult = ISOUtils.hexString(getDataByName(AppConfig.DataXMLName.EXPECTED_ENCRYPT_BIG_DATA_AES_CTR));
        byte[] encryptedBigData = getDataByName(AppConfig.DataXMLName.ENCRYPT_BIG_DATA_AES_CTR);
        byte[] iv = ISOUtils.hex2byte("000102030405060708090A0B0C0D0E0F");
        SymmetricKey aesDataKey = new SymmetricKey();
        aesDataKey.setKeyID(AppConfig.Keys.MKSK_AES_INDEX_WK_DATA);
        aesDataKey.setKeyType(KeyType.AES);
        aesDataKey.setKeyUsage(KeyUsage.DATA);

        try {
            CipherOutput cipherOutput = cryptoHelper.encryptWithCTR(aesDataKey, CipherType.AES_CTR, PaddingMode.NONE, iv, encryptedBigData);
            if (aesCtrExpectedResult.equals(ISOUtils.hexString(cipherOutput.getData()))) {
                showMessage("Encrypt BigData with AES CTR mode success, and it meets the expected result.");
            } else {
                showMessage("Encrypt BigData with AES CTR mode success, but it didn't meet the expected result.", MessageTag.ERROR);
            }
        } catch (NSDKException e) {
            showErrorMessage(e, "Encrypt BigData with AES CTR mode failed.");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_Pin_decrypt_big_data_aes_ctr, functionid = 29)
    private void decryptBigDataWithAesCtr() {
        String aesCtrExpectedResult = ISOUtils.hexString(getDataByName(AppConfig.DataXMLName.EXPECTED_DECRYPT_BIG_DATA_AES_CTR));
        byte[] decryptedBigData = getDataByName(AppConfig.DataXMLName.DECRYPT_BIG_DATA_AES_CTR);
        byte[] iv = ISOUtils.hex2byte("000102030405060708090A0B0C0D0E0F");
        SymmetricKey aesDataKey = new SymmetricKey();
        aesDataKey.setKeyID(AppConfig.Keys.MKSK_AES_INDEX_WK_DATA);
        aesDataKey.setKeyType(KeyType.AES);
        aesDataKey.setKeyUsage(KeyUsage.DATA);

        try {
            CipherOutput cipherOutput = cryptoHelper.decryptWithCTR(aesDataKey, CipherType.AES_CTR, PaddingMode.NONE, iv, decryptedBigData);
            Log.d("test", "AES CTR encrypted data: " + ISOUtils.hexString(cipherOutput.getData()));
            if (aesCtrExpectedResult.equals(ISOUtils.hexString(cipherOutput.getData()))) {
                showMessage("Decrypt BigData with AES CTR mode success, and it meets the expected result.");
            } else {
                showMessage("Decrypt BigData with AES CTR mode success, but it didn't meet the expected result.", MessageTag.ERROR);
            }
        } catch (NSDKException e) {
            showErrorMessage(e, "Decrypt BigData with AES CTR mode failed.");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_encrypt_big_data_des_ctr, functionid = 30)
    private void encryptBigDataWithDesCtr() {
        String desCtrExpectedResult = ISOUtils.hexString(getDataByName(AppConfig.DataXMLName.EXPECTED_ENCRYPT_BIG_DATA_DES_CTR));
        byte[] encryptedBigData = getDataByName(AppConfig.DataXMLName.ENCRYPT_BIG_DATA_DES_CTR);
        byte[] iv = ISOUtils.hex2byte("0001020304050607");
        SymmetricKey desDataKey = new SymmetricKey();
        desDataKey.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_WK_DATA);
        desDataKey.setKeyType(KeyType.DES);
        desDataKey.setKeyUsage(KeyUsage.DATA);

        try {
            CipherOutput cipherOutput = cryptoHelper.encryptWithCTR(desDataKey, CipherType.DES_CTR, PaddingMode.NONE, iv, encryptedBigData);
            if (desCtrExpectedResult.equals(ISOUtils.hexString(cipherOutput.getData()))) {
                showMessage("Encrypt BigData with DES CTR mode success, and it meets the expected result.");
            } else {
                showMessage("Encrypt BigData with DES CTR mode success, but it didn't meet the expected result.", MessageTag.ERROR);
            }
        } catch (NSDKException e) {
            showErrorMessage(e, "Encrypt BigData with DES CTR mode failed.");
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_Pin_decrypt_big_data_des_ctr, functionid = 31)
    private void decryptBigDataWithDesCtr() {
        String desCtrExpectedResult = ISOUtils.hexString(getDataByName(AppConfig.DataXMLName.EXPECTED_ENCRYPT_BIG_DATA_DES_CTR));
        byte[] decryptedBigData = getDataByName(AppConfig.DataXMLName.ENCRYPT_BIG_DATA_DES_CTR);
        byte[] iv = ISOUtils.hex2byte("0001020304050607");
        SymmetricKey desDataKey = new SymmetricKey();
        desDataKey.setKeyID(AppConfig.Keys.MKSK_DES_INDEX_WK_DATA);
        desDataKey.setKeyType(KeyType.DES);
        desDataKey.setKeyUsage(KeyUsage.DATA);

        try {
            CipherOutput cipherOutput = cryptoHelper.decryptWithCTR(desDataKey, CipherType.DES_CTR, PaddingMode.NONE, iv, decryptedBigData);
            if (desCtrExpectedResult.equals(ISOUtils.hexString(cipherOutput.getData()))) {
                showMessage("Decrypt BigData with DES CTR mode success, and it meets the expected result.");
            } else {
                showMessage("Decrypt BigData with DES CTR mode success, but it didn't meet the expected result.", MessageTag.ERROR);
            }
        } catch (NSDKException e) {
            showErrorMessage(e, "Decrypt BigData with DES CTR mode failed.");
        }
    }


    @MethodGridEntity(btnnameid = R.string.tv_pin_generate_cmac_aes_dukpt, functionid = 32)
    private void generateAesDukptCmac() {
        DUKPTDerivateKey key = new DUKPTDerivateKey();
        key.setKeyID(AppConfig.Keys.DUKPT_AES_INDEX);
        key.setDerivateKeyLen(16);
        key.setDerivateKeyType(KeyType.AES);
        key.setDerivateUsage(DUKPTDerivateUsage.MAC_GEN);
        byte[] datain = ISOUtils.hex2byte("88888881113366677883373215563223abcdeffdaabbccdd");
        byte[] iv = new byte[16];
        try {
            MACOutput macOutput = mCrypto.generateMAC(key, MACType.AES_DUKPT_CMAC, iv, datain);
            showMessage(ISOUtils.hexString(macOutput.getData()));
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_init_csr, functionid = 33)
    private void initCSR() {
        AsymmetricKey asymmetricKey = new AsymmetricKey();
        asymmetricKey.setKeyID(AppConfig.Keys.ASYM_DATA_ID);
        asymmetricKey.setKeyType(AsymKeyType.RSA);
        asymmetricKey.setKeyUsage(AsymKeyUsage.DATA);
        List<byte[]> oidList = new LinkedList<>();
        oidList.add(new byte[] {0x55, 0x1D, 0x12});
        List<byte[]> valueList = new LinkedList<>();
        valueList.add("www.newlandnpt.com".getBytes(StandardCharsets.UTF_8));
        CSRParameters parameters = new CSRParameters();
        parameters.setUserName("C=CN, O=Newland Payment Technology Co.Ltd, CN=cert");
        parameters.setMessageDigestType(MessageDigestType.SHA256);
        parameters.setAsymmetricKey(asymmetricKey);
        parameters.setCA(false);
        parameters.setCertType((byte) 0x10);
        parameters.setOidList(oidList);
        parameters.setValueList(valueList);
        parameters.setKeyUsage((byte) 0x01);
        try {
            mCrypto.initCSR(parameters);
            showMessage("Init CSR success.");
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_generate_csr_data, functionid = 34)
    private void generateCSRData() {
        try {
            byte[] csrData = mCrypto.generateCSR(CSRFileType.PEM);
            showMessage("Generate CSR data: \n" + new String(csrData));
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_release_csr, functionid = 35)
    private void releaseCSR() {
        try {
            mCrypto.releaseCSR();
            showMessage("Release CSR success.");
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_create_cryptogram, functionid = 36)
    private void createCryptogram() {
        byte[] krdPrivateKey = ISOUtils.hex2byte("308204A50201000282010100A750AD1C0FF55ACB066D246A93DCB0DCD0FA183EC737F70526EB9FC9ECBD328F05DC2910897E544B20F86C6C3BAD6C24E9D5A0C8B3E4616F231CAB8A6E188A74D84D1CB91A3FCDC3352748EEF2355FC1E101BCFEAAB7E7E2EC8C59EB48D78B26A30CE6F6BC1AC3ACE23DBCD38E982D7C647A1C95B6D40FCFAFE608865617C07F2E39CA303B105E482A0CEE681CCB4424FB405272ADA639034778510E59A5E1BFEB0BE427BEC852544406C9A1539ECDE0910F7F4DE70602F8A27CFE4E7E050146555640A2AF42D86C822BADD85F703551ABDBEEB5445C32782C4E989A1DA7C4830842896CEA135BCD5AB328EA4ED656F39A87A3D238BD282948109874105DB22902030100010282010100A3D3E81D21372256C78D18EA4EABFA75CD1E059D7ADE3EBEC9B44FBA9D57486928D8C150D30062B349AE5623C86F7003D8FEF7B76E05C2ADDAD898D32C28557241D587B96B8D6C01A74B372BD8F810323C0AC2AF2E2473270C6E0521D02A55B358562AD50FACB94AD0209983210DBB0421323C49104326D43AACE84DF980BA577825180718FEFD9C071BC4193DC00D2D46AB4710EF6157AF03FF2AA94829416E7E0A6A7A7B5A57ED11903CA1B7DA02477F5CB9A213610A0A1AA58B9C93CDC6DD9940DB6A764DD5188B9D98A7989AD7B436F777ED9BBEF5726CAEE182A7A4DC3C497040977CBB04A220D702734588D592B22CF2E8E8C6640AA472FED4C14A327102818100D731DFE419D2313DC16349CBB8DA3226B96A340113341D87143262E8105810DCD24F37D60EE7A64F3967D49037D38C318F18A3F3F4CAEC25C7D60599451709538966EBBCD9F1CF9EED2C4629D8E6DF98B339FBDE444A89DB28254EF1F63C803A4F64AFDAB4FAE7957EABF42A660299B9CBFB25F803FE5AA443517EB5C9A382C502818100C70A986A574E5AB2D7064D77BBA25B5D24EE7167BE6DB0DF616CD4EA4990ABC46CB891EC0A0EB019619556951C25683D13D7F7A70C5420EA862CE38004D4D3368C105015DAA526EBCB613B05326956CFB842046FBFDBB7CEA5AA1F6A62EB007E1BDA1A910678571F94167AE6AD8E9A7C08EE236C2C5CCE2D7E930D99C80398150281806E7D141878CD7C2CDA0B518B80F8BF0134F7FB8585C79F5588A02A3A3E9208EE6828F66138BBE59F0DB96AE13558AFC58E543771FD80E6E8AB070F4ED7B3713D6BF1E51DCCC52435D44E8423DE530400D21CF65DF3B055C1A9862657837722DB4D8243EC463A0107B7C7301148D912F6DB6DB2DBF9A8F1FF1EF54BDAA8920485028181009823C328D59F30B8AE9F44AB96A29D4F6F214B51CF3F2D093200EC264120F20A6C481051E9C61C58EA3C2A3843915C42035EFB0F96DEDF6224393F93E51D806C9B5704C00FA6593FB6EF951B7DD302E637A34E9CD47BEFDD66C7C8C79AE0A239C2B44DC638B9D76E6DD5590FE5EDB0ABCA6FAC67CCE90B37BF1E4C3115AF99990281810093FF4B00CDCEE41C1755EC1D7DE6FD2DB9984FA1F58A0FD923F3B443B040CF9B76E31A04E7D20DCB27A34B5FF0127D2315F104168F3D811B361C0D55474135A7692020C4A9ACE4DA7A0605DAC9AEF8E71455E533160A02B16272CE768182058A47204FBEDB7EBFE5229602A4FE24DBCFC57AA6319F087AF296C19AB5E510A4BB");
        byte[] krdCertificate = ISOUtils.hex2byte("2D2D2D2D2D424547494E2043455254494649434154452D2D2D2D2D0A4D494944317A434341722B6741774942416749485752795541424F514E54414E42676B71686B69473977304241517346414443426A54454C4D416B47413155450A42684D4351303478447A414E42674E564241674D426B5A31536D6C68626A45504D4130474131554542777747526E5661614739314D5373774B5159445651514B0A44434A4F5A5864735957356B494642686557316C626E51675647566A614735766247396E65534244627934735448526B4D526377465159445651514C457735450A52565967546C42554945314752794244515445574D425147413155454177774E52455657494531475279425464574A4451544165467730794D7A41314D4463770A4D4441774D4442614677307A4D7A41314D4467774D4441774D4442614D49474F4D517377435159445651514745774A44546A45504D41304741315545434177470A526E564B615746754D513877445159445651514844415A476456706F623355784B7A417042674E5642416F4D496B356C64327868626D516755474635625756750A644342555A574E6F626D39736232643549454E764C69784D64475178466A415542674E564241734D445552465669424E526B63675533566951304578474441570A42674E5642414D4D443035424F5445774D4451794E6A49344E53314C56444343415349774451594A4B6F5A496876634E415145424251414467674550414443430A41516F4367674542414B6451725277503956724C426D306B61705063734E7A512B68672B787A6633425362726E386E7376544B504264777045496C2B564573670A2B4778734F3631734A4F6E566F4D697A3547467649787972696D3459696E545954527935476A2F4E777A556E534F37794E562F42345147382F717133352B4C730A6A466E72534E654C4A714D4D3576613847734F73346A3238303436594C58786B65687956747451507A362F6D43495A574638422F4C6A6E4B4D447351586B67710A444F356F484D74454A507441556E4B74706A6B4452336852446C6D6C34622F72432B516E7673685356455147796146546E7333676B51392F54656347417669690A6650354F66675542526C5657514B4B765174687367697574324639774E564772322B3631524677796543784F6D4A6F647038534443454B4A624F6F54573831610A73796A7154745A57383571486F39493476536770534243596442426473696B434177454141614D354D446377485159445652304F4242594546433331352F41580A6A383646417A2B77564C2F3147443451323262744D416B4741315564457751434D41417743775944565230504241514441674F6F4D41304743537147534962330A44514542437755414134494241514241373066506252393775593054515065347552412F66622B6D63634F78556532687A48672F396D5445372B6231622B4A520A684A5563723165466D7A6A524F2F36336D56664B783952496A61765A6D4535306E464A424871666D6248456376335A626471355A77636B34566A414B3166734C0A554C53795631447A6D397756634A3932324142476C706C6F4F2F705A447031574C77522B534778764E755046762B5A56712B3139486849385430754F4E41464C0A6C69774959764A676E5169337152305A786D793345554D4148794B74657859696D35693355306965594F546D6D2F66494F32322F58644B46616F7242506642630A4654486D774A63344A755577735451316E6D6554596C447650434C744630556A6334454930526D69485057526A39676D634739483573507573636D556964474F0A4A656853732B7A46506539442B72657430632B496954575472436372423339656E4452580A2D2D2D2D2D454E442043455254494649434154452D2D2D2D2D00");

        AsymmetricKey cryptoKey = new AsymmetricKey();
        cryptoKey.setKeyID((byte) 5);
        cryptoKey.setKeyType(AsymKeyType.RSA);
        cryptoKey.setKeyUsage(AsymKeyUsage.KEY_DISTRIBUTION);
        cryptoKey.setKeyData(krdPrivateKey);
        cryptoKey.setKeyLen(krdPrivateKey.length);
        try {
            mKeymanager.generateKey(KeyGenerateMethod.CLEAR, null, null, cryptoKey, krdCertificate);
            showMessage("Generate crypto key success.");
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
            return;
        }

        SymmetricKey sessionKey = new SymmetricKey();
        sessionKey.setKeyID((byte) 5);
        sessionKey.setKeyType(KeyType.AES);
        sessionKey.setKeyUsage(KeyUsage.KEK);
        sessionKey.setKeyData(ISOUtils.hex2byte("3131313131313131313131313131313131313131313131313131313131313131"));
        sessionKey.setKeyLen(32);

        try {
            mKeymanager.generateKey(KeyGenerateMethod.CLEAR, null, sessionKey);
            showMessage("Generate session key success.");
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
            return;
        }

        SymmetricKey componentSecretKey = new SymmetricKey();
        componentSecretKey.setKeyID((byte) 5);
        componentSecretKey.setKeyType(KeyType.AES);
        componentSecretKey.setKeyUsage(KeyUsage.DATA);
        componentSecretKey.setKeyData(ISOUtils.hex2byte("3333333333333333333333333333333333333333333333333333333333333333"));
        componentSecretKey.setKeyLen(32);

        try {
            mKeymanager.generateKey(KeyGenerateMethod.CLEAR, null, componentSecretKey);
            showMessage("Generate component screct key success.");
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
            return;
        }

        CryptogramInfo cryptogramInfo = new CryptogramInfo();
        cryptogramInfo.setPrefixInfo(ISOUtils.hex2byte("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"));
        byte[] suffix = new byte[64];
        Arrays.fill(suffix, (byte) 0x3F);
        cryptogramInfo.setSuffixInfo(suffix);
        cryptogramInfo.setEncodingMode(AsymEncodingMode.PKCS_V21);
        cryptogramInfo.setMessageDigestType(MessageDigestType.SHA256);

        try {
            byte[] cryptogram = mCrypto.createCryptogram(cryptoKey, sessionKey, componentSecretKey, cryptogramInfo);
            showMessage("Created cryptogram: " + ISOUtils.hexString(cryptogram));
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_aes_gcm_encrypt, functionid = 36)
    private void aesGcmEncrypt() {
        SymmetricKey aesKey = new SymmetricKey();
        aesKey.setKeyID((byte) 3);
        aesKey.setKeyType(KeyType.AES);
        aesKey.setKeyUsage(KeyUsage.DATA);
        aesKey.setKeyData(ISOUtils.hex2byte("11111111111111111111111111111111"));
        aesKey.setKeyLen(16);
        try {
            mKeymanager.generateKey(KeyGenerateMethod.CLEAR, null, aesKey);
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }

        SymmetricKey cryptoKey = new SymmetricKey();
        cryptoKey.setKeyID((byte) 3);
        cryptoKey.setKeyType(KeyType.AES);
        cryptoKey.setKeyUsage(KeyUsage.DATA);
        cryptoKey.setKeyLen(16);
        CipherParameters encryptParameters = new CipherParameters();
        encryptParameters.setCipherType(CipherType.AES_GCM);
        encryptParameters.setAuthData(ISOUtils.hex2byte("11223344556677889900AABBCCDDEEFF"));
        encryptParameters.setIv(ISOUtils.hex2byte("11111111111111111111111111111111"));
        encryptParameters.setAuthTagLen(16);
        byte[] data = ISOUtils.hex2byte("1212121212121212121212121212121212121212121212121212121212121212");
        try {
            GCMCipherOut encryptResult = (GCMCipherOut) mCrypto.encrypt(cryptoKey, encryptParameters, data);
            if (encryptResult != null) {
                byte[] encryptData = encryptResult.getData();
                byte[] encryptAuthTag = encryptResult.getAuthTag();
                showMessage("AES GCM Encrypt Data: " + ISOUtils.hexString(encryptData));
                showMessage("AES GCM Auth Tag: " + ISOUtils.hexString(encryptAuthTag));
            }
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_aes_gcm_decrypt, functionid = 37)
    private void aesGcmDecrypt() {
        SymmetricKey aesKey = new SymmetricKey();
        aesKey.setKeyID((byte) 3);
        aesKey.setKeyType(KeyType.AES);
        aesKey.setKeyUsage(KeyUsage.DATA);
        aesKey.setKeyData(ISOUtils.hex2byte("11111111111111111111111111111111"));
        aesKey.setKeyLen(16);
        try {
            mKeymanager.generateKey(KeyGenerateMethod.CLEAR, null, aesKey);
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }

        SymmetricKey cryptoKey = new SymmetricKey();
        cryptoKey.setKeyID((byte) 3);
        cryptoKey.setKeyType(KeyType.AES);
        cryptoKey.setKeyUsage(KeyUsage.DATA);
        cryptoKey.setKeyLen(16);
        CipherParameters decryptParameters = new CipherParameters();
        decryptParameters.setCipherType(CipherType.AES_GCM);
        decryptParameters.setAuthTag(ISOUtils.hex2byte("8c99513c7f75d8f791d63cae29d4b2fc"));
        decryptParameters.setAuthData(ISOUtils.hex2byte("11223344556677889900AABBCCDDEEFF"));
        decryptParameters.setIv(ISOUtils.hex2byte("11111111111111111111111111111111"));

        byte[] encryptedData = ISOUtils.hex2byte("44e92a9fb576ba8ad5a982806316ab8f9448cba556674ff048ce472739c37eae");
        try {
            CipherOutput decryptResult = mCrypto.decrypt(cryptoKey, decryptParameters, encryptedData);
            showMessage("AES GCM Decrypt Data: " + ISOUtils.hexString(decryptResult.getData()));
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_aes_gcm_dukpt_encrypt, functionid = 38)
    private void aesGcmDukptEncrypt() {
        DUKPTKey dukptKey = new DUKPTKey();
        dukptKey.setKeyID((byte) 3);
        dukptKey.setKeyType(KeyType.AES);
        dukptKey.setKeyUsage(KeyUsage.DUKPT);
        dukptKey.setKeyData(ISOUtils.hex2byte("22222222222222222222222222222222"));
        dukptKey.setKeyLen(16);
        dukptKey.setKSN(ISOUtils.hex2byte("000000000000000000000000"));
        try {
            mKeymanager.generateKey(KeyGenerateMethod.CLEAR, null, dukptKey);
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }

        DUKPTDerivateKey cryptoKey = new DUKPTDerivateKey();
        cryptoKey.setKeyID((byte) 3);
        cryptoKey.setDerivateKeyType(KeyType.AES);
        cryptoKey.setKeyUsage(KeyUsage.DUKPT);
        cryptoKey.setDerivateUsage(DUKPTDerivateUsage.DATA_BOTH);
        cryptoKey.setDerivateKeyLen(16);
        CipherParameters encryptParameters = new CipherParameters();
        encryptParameters.setCipherType(CipherType.AES_GCM);
        encryptParameters.setAuthData(ISOUtils.hex2byte("11223344556677889900AABBCCDDEEFF"));
        encryptParameters.setIv(ISOUtils.hex2byte("11111111111111111111111111111111"));
        encryptParameters.setAuthTagLen(16);
        byte[] data = ISOUtils.hex2byte("1212121212121212121212121212121212121212121212121212121212121212");
        try {
            GCMCipherOut encryptResult = (GCMCipherOut) mCrypto.encrypt(cryptoKey, encryptParameters, data);
            if (encryptResult != null) {
                byte[] encryptData = encryptResult.getData();
                byte[] encryptAuthTag = encryptResult.getAuthTag();
                showMessage("AES GCM Encrypt Data: " + ISOUtils.hexString(encryptData));
                showMessage("AES GCM Auth Tag: " + ISOUtils.hexString(encryptAuthTag));
            }
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_aes_gcm_dukpt_decrypt, functionid = 39)
    private void aesGcmDukptDecrypt() {
        DUKPTKey dukptKey = new DUKPTKey();
        dukptKey.setKeyID((byte) 3);
        dukptKey.setKeyType(KeyType.AES);
        dukptKey.setKeyUsage(KeyUsage.DUKPT);
        dukptKey.setKeyData(ISOUtils.hex2byte("22222222222222222222222222222222"));
        dukptKey.setKeyLen(16);
        dukptKey.setKSN(ISOUtils.hex2byte("000000000000000000000000"));
        try {
            mKeymanager.generateKey(KeyGenerateMethod.CLEAR, null, dukptKey);
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }
        DUKPTDerivateKey cryptoKey = new DUKPTDerivateKey();
        cryptoKey.setKeyID((byte) 3);
        cryptoKey.setDerivateKeyType(KeyType.AES);
        cryptoKey.setKeyUsage(KeyUsage.DUKPT);
        cryptoKey.setDerivateUsage(DUKPTDerivateUsage.DATA_BOTH);
        cryptoKey.setDerivateKeyLen(16);
        CipherParameters decryptParameters = new CipherParameters();
        decryptParameters.setCipherType(CipherType.AES_GCM);
        decryptParameters.setAuthTag(ISOUtils.hex2byte("59b045c49c9dc5ff0855d46e3e23ecc6"));
        decryptParameters.setAuthData(ISOUtils.hex2byte("11223344556677889900AABBCCDDEEFF"));
        decryptParameters.setIv(ISOUtils.hex2byte("11111111111111111111111111111111"));

        byte[] encryptedData = ISOUtils.hex2byte("8a2dd95afa89dd2ef534bd9f2fae262782a54b6163df91d217f1b8b4afe4d545");
        try {
            CipherOutput decryptResult = mCrypto.decrypt(cryptoKey, decryptParameters, encryptedData);
            showMessage("AES GCM Decrypt Data: " + ISOUtils.hexString(decryptResult.getData()));
        } catch (NSDKException e) {
            showErrorMessage(e, e.getMessage());
        }
    }


    private byte[] getDataByName(String name) {
        for (XMLUtils.DataXmlElements dataXmlElements : dataXmlElementsList) {
            if (dataXmlElements.getName().equalsIgnoreCase(name)) {
                return dataXmlElements.getData();
            }
        }
        return null;
    }
}
