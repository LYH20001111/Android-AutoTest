package com.newland.sdkdemo.fragment;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.newland.ndk.NdkApiManager;
import com.newland.ndk.SecN;
import com.newland.ndk.h.ST_SEC_KCV_INFO;
import com.newland.ndk.h.ST_SEC_KEY_INFO;
import com.newland.sdk.ModuleManage;
import com.newland.sdk.module.pin.AccountInputType;
import com.newland.sdk.module.pin.AlgorithmMode;
import com.newland.sdk.module.pin.CipherExtParams;
import com.newland.sdk.module.pin.CipherMode;
import com.newland.sdk.module.pin.CipherResult;
import com.newland.sdk.module.pin.DefaultLayout;
import com.newland.sdk.module.pin.InjectKeyType;
import com.newland.sdk.module.pin.KeyManagement;
import com.newland.sdk.module.pin.KeyType;
import com.newland.sdk.module.pin.LoadKeyMode;
import com.newland.sdk.module.pin.LoadWKMode;
import com.newland.sdk.module.pin.MacAlgorithm;
import com.newland.sdk.module.pin.MacResult;
import com.newland.sdk.module.pin.MacType;
import com.newland.sdk.module.pin.NPinpadModule;
import com.newland.sdk.module.pin.PinInputExtParams;
import com.newland.sdk.module.pin.PinInputListener;
import com.newland.sdk.module.pin.PinpadModule;
import com.newland.sdk.module.pin.TusnData;
import com.newland.sdk.module.pin.WorkingKeyType;
import com.newland.sdk.mtype.DeviceInvokeException;
import com.newland.sdk.mtype.util.Dump;
import com.newland.sdk.utils.ISOUtils;
import com.newland.sdkdemo.AppConfig;
import com.newland.sdkdemo.MainActivity;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.adapter.LayoutMode;
import com.newland.sdkdemo.annotation.MethodGridEntity;
import com.newland.sdkdemo.utils.DialogUtils;
import com.newland.sdkdemo.utils.MessageTag;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Author by bxy, Date on 2019/5/10 0010.
 */
public class PinMKSKFragment extends BaseFragment {

    private PinpadModule pinInput;
    private KeyManagement keySysAlg;
    private int indexMK, indexWKPin, indexWKTrack, indexWKMac;
    public PinMKSKFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_pin_f);
    }

    @Override
    public void initData() {
        pinInput = moduleManage.getPinpadModule();
        if (AppConfig.MKSK_DES.equals(AppConfig.KEY_SYS_ALG)) {
            indexWKPin = AppConfig.Pin.MKSK_DES_INDEX_WK_PIN;
            indexWKTrack = AppConfig.Pin.MKSK_DES_INDEX_WK_TRACK;
            indexWKMac = AppConfig.Pin.MKSK_DES_INDEX_WK_MAC;
        } else if (AppConfig.MKSK_SM4.equals(AppConfig.KEY_SYS_ALG)) {
            indexWKPin = AppConfig.Pin.MKSK_SM4_INDEX_WK_PIN;
            indexWKTrack = AppConfig.Pin.MKSK_SM4_INDEX_WK_TRACK;
            indexWKMac = AppConfig.Pin.MKSK_SM4_INDEX_WK_MAC;
        } else if (AppConfig.MKSK_AES.equals(AppConfig.KEY_SYS_ALG)) {
            indexWKPin = AppConfig.Pin.MKSK_AES_INDEX_WK_PIN;
            indexWKTrack = AppConfig.Pin.MKSK_AES_INDEX_WK_TRACK;
            indexWKMac = AppConfig.Pin.MKSK_AES_INDEX_WK_MAC;
        }
    }

    @Override
    public Object getModule() {
        return PinMKSKFragment.this;
    }

    private static final int INDEX_LOADMAINKEY = 1;
    private static final int INDEX_LOADWORKINGKEY = 2;
    private static final int INDEX_INPUTONLINEPIN = 3;
    private static final int INDEX_INPUTONLINEPIN_RBIN = 4;
    private static final int INDEX_INPUTOFFLINE = 5;
    private static final int INDEX_CANLEINPUTPIN = 6;
    private static final int INDEX_CALMAC = 7;
    private static final int INDEX_ENCRYPTION = 8;
    private static final int INDEX_DECRYPTION = 9;
    private static final int INDEX_DELETEMK = 10;
    private static final int INDEX_DELETEWK = 11;
    private static final int INDEX_DELETEALLKEY = 12;
    private static final int INDEX_CHECKKEY = 13;
    private static final int INDEX_PBC21INFO = 14;

    private static final int INDEX_HMAC = 15;
    @MethodGridEntity(btnnameid = R.string.tv_pin_load_mk, functionid = INDEX_LOADMAINKEY)
    public void loadMainKey() {
        try {
            boolean result = false;
            if (AppConfig.MKSK_DES.equals(AppConfig.KEY_SYS_ALG)) {
                final String mkDES = "253C9D9D7C2FBBFA253C9D9D7C2FBBFA";//PLAIN KEY：11111111111111111111111111111111
                result = pinInput.loadMasterKey(LoadKeyMode.DEFAULT_ENCRYPT, AlgorithmMode.DES, AppConfig.Pin.MKSK_DES_INDEX_MK, ISOUtils.hex2byte(mkDES), ISOUtils.hex2byte("82E13665"), null);
            } else if (AppConfig.MKSK_SM4.equals(AppConfig.KEY_SYS_ALG)) {
                String mkSM4 = "0812C8D3ED259C7EB0167D647748FF35";//PLAIN KEY:11111111111111111111111111111111
                result = pinInput.loadMasterKey(LoadKeyMode.DEFAULT_ENCRYPT, AlgorithmMode.SM4, AppConfig.Pin.MKSK_SM4_INDEX_MK, ISOUtils.hex2byte(mkSM4), ISOUtils.hex2byte("F8D06870B9EA"), null);
            } else if (AppConfig.MKSK_AES.equals(AppConfig.KEY_SYS_ALG)) {
                String mkAES = "053A5184A74CC33188E5FDC351D0CF06";//PLAIN KEY:11111111111111111111111111111111
                result = pinInput.loadMasterKey(LoadKeyMode.DEFAULT_ENCRYPT, AlgorithmMode.AES, AppConfig.Pin.MKSK_AES_INDEX_MK, ISOUtils.hex2byte(mkAES), ISOUtils.hex2byte("EE23D81C"), null);
            }
            String msg = (result ? context.getString(R.string.msg_load_mk_succ) : context.getString(R.string.msg_load_mk_failed));
            showMessage(msg + "\r\n", MessageTag.DATA);
        } catch (Exception e) {
            showMessage(context.getString(R.string.msg_load_mk_failed) + e, MessageTag.ERROR);
        }
    }

    private void loadWorkingKeyPin() {
        boolean result = false;
        if (AppConfig.MKSK_DES.equals(AppConfig.KEY_SYS_ALG)) {
            byte[] pinKeyDES = ISOUtils.hex2byte("D2CEEE5C1D3AFBAF00374E0CC1526C86");//PLAIN KEY：2A288F61348FEE93FE9C0FC714BCDD73
            byte[] checkKcv = ISOUtils.hex2byte("58A2BBF9");
            result = pinInput.loadWorkingKey(LoadWKMode.ENCRYPT, AlgorithmMode.DES, WorkingKeyType.PIN, AppConfig.Pin.MKSK_DES_INDEX_MK, AppConfig.Pin.MKSK_DES_INDEX_WK_PIN, pinKeyDES, checkKcv, null);
        } else if (AppConfig.MKSK_SM4.equals(AppConfig.KEY_SYS_ALG)) {
            byte[] pinKeySM4 = ISOUtils.hex2byte("3526A987BBC659EC3219956DC1FF38B0");//PLAIN KEY:33333333333333333333333333333333
            byte[] checkKcv = ISOUtils.hex2byte("A43FDDA62EA0");
            result = pinInput.loadWorkingKey(LoadWKMode.ENCRYPT, AlgorithmMode.SM4, WorkingKeyType.PIN, AppConfig.Pin.MKSK_SM4_INDEX_MK, AppConfig.Pin.MKSK_SM4_INDEX_WK_PIN, pinKeySM4, checkKcv, null);
        } else if (AppConfig.MKSK_AES.equals(AppConfig.KEY_SYS_ALG)) {
            byte[] pinKeyAES = ISOUtils.hex2byte("34A9575EEFE69DE078B4E29A24D04CD7");//PLAIN KEY:66666666666666666666666666666666
            byte[] checkKcv = ISOUtils.hex2byte("2DB6A815");
            result = pinInput.loadWorkingKey(LoadWKMode.ENCRYPT, AlgorithmMode.AES, WorkingKeyType.PIN, AppConfig.Pin.MKSK_AES_INDEX_MK, AppConfig.Pin.MKSK_AES_INDEX_WK_PIN, pinKeyAES, checkKcv, null);
        }
        String msg = (result ? context.getString(R.string.msg_load_pin_wk_succ) : context.getString(R.string.msg_load_pin_wk_failed));
        showMessage(msg + "\r\n", MessageTag.DATA);
    }

    private void loadWorkingKeyTrack() {
        boolean result = false;
        if (AppConfig.MKSK_DES.equals(AppConfig.KEY_SYS_ALG)) {
            byte[] trackKeyDES = ISOUtils.hex2byte("DBFE96D0A5F09D24DBFE96D0A5F09D24");//PLAIN KEY:4DE5E8B8A9DCDDF94DE5E8B8A9DCDDF9
            byte[] checkKcv = ISOUtils.hex2byte("5B4C8BED");
            result = pinInput.loadWorkingKey(LoadWKMode.ENCRYPT, AlgorithmMode.DES, WorkingKeyType.TRACK, AppConfig.Pin.MKSK_DES_INDEX_MK, AppConfig.Pin.MKSK_DES_INDEX_WK_TRACK, trackKeyDES, checkKcv, null);
        } else if (AppConfig.MKSK_SM4.equals(AppConfig.KEY_SYS_ALG)) {
            byte[] trackKeySM4 = ISOUtils.hex2byte("585B9D3F745C8EA95400BD2A3EBDFABF");//PLAIN KEY:22222222222222222222222222222222
            byte[] checkKcv = ISOUtils.hex2byte("AD9358BA9110");
            result = pinInput.loadWorkingKey(LoadWKMode.ENCRYPT, AlgorithmMode.SM4, WorkingKeyType.TRACK, AppConfig.Pin.MKSK_SM4_INDEX_MK, AppConfig.Pin.MKSK_SM4_INDEX_WK_TRACK, trackKeySM4, checkKcv, null);
        } else if (AppConfig.MKSK_AES.equals(AppConfig.KEY_SYS_ALG)) {
            byte[] trackKeyAES = ISOUtils.hex2byte("06C8D45B628EAEA8A30C75579F321211");//PLAIN KEY:88888888888888888888888888888888
            byte[] checkKcv = ISOUtils.hex2byte("8B4217FE");
            result = pinInput.loadWorkingKey(LoadWKMode.ENCRYPT, AlgorithmMode.AES, WorkingKeyType.TRACK, AppConfig.Pin.MKSK_AES_INDEX_MK, AppConfig.Pin.MKSK_AES_INDEX_WK_TRACK, trackKeyAES, checkKcv, null);
        }
        String msg = (result ? context.getString(R.string.msg_load_track_wk_succ) : context.getString(R.string.msg_load_track_wk_failed));
        showMessage(msg + "\r\n", MessageTag.DATA);
    }

    private void loadWorkingKeyMac() {
        boolean result = false;
        if (AppConfig.MKSK_DES.equals(AppConfig.KEY_SYS_ALG)) {
            byte[] macKeyDES = ISOUtils.hex2byte("A30FE2C1D07BCC11A30FE2C1D07BCC11");//PLAIN KEY:66666666666666666666666666666666
            byte[] checkKcv = ISOUtils.hex2byte("B0B563C2");
            result = pinInput.loadWorkingKey(LoadWKMode.ENCRYPT, AlgorithmMode.DES, WorkingKeyType.MAC, AppConfig.Pin.MKSK_DES_INDEX_MK, AppConfig.Pin.MKSK_AES_INDEX_WK_MAC, macKeyDES, checkKcv, null);
        }

        if (AppConfig.MKSK_SM4.equals(AppConfig.KEY_SYS_ALG)) {
            byte[] macKeySM4 = ISOUtils.hex2byte("E97748E56A3D1F883832852C305242E8");//PLAIN KEY:17171717171717171717171717171717
            byte[] checkKcv = ISOUtils.hex2byte("6642C0C20B6C");
            result = pinInput.loadWorkingKey(LoadWKMode.ENCRYPT, AlgorithmMode.SM4, WorkingKeyType.MAC, AppConfig.Pin.MKSK_SM4_INDEX_MK, AppConfig.Pin.MKSK_SM4_INDEX_WK_MAC, macKeySM4, checkKcv, null);
        }

        if (AppConfig.MKSK_AES.equals(AppConfig.KEY_SYS_ALG)) {
            byte[] macKeyAES = ISOUtils.hex2byte("DC44B424BCB85288CE3BE42430864E8B");//PLAIN KEY:77777777777777777777777777777777
            byte[] checkKcv = ISOUtils.hex2byte("2C3F18B2");
            result = pinInput.loadWorkingKey(LoadWKMode.ENCRYPT, AlgorithmMode.AES, WorkingKeyType.MAC, AppConfig.Pin.MKSK_AES_INDEX_MK, AppConfig.Pin.MKSK_AES_INDEX_WK_MAC, macKeyAES, checkKcv, null);
        }
        String msg = (result ? context.getString(R.string.msg_load_mac_wk_succ) : context.getString(R.string.msg_load_mac_wk_failed));
        showMessage(msg + "\r\n", MessageTag.DATA);
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_load_wk, functionid = INDEX_LOADWORKINGKEY)
    public void loadWorkingKey() {
        final String[] arrayWorkingKey = new String[]{context.getString(R.string.msg_wk_array_pinwk), context.getString(R.string.msg_wk_array_trackwk), context.getString(R.string.msg_wk_array_macwk)};
        DialogUtils.createMultiChoiceDialog(context, context.getString(R.string.msg_pl_choose_load_wk_type), arrayWorkingKey, new DialogUtils.MultiChoiceDialogCallback() {
            @Override
            public void onResult(ArrayList<Integer> yourChoices) {
                try {
                    if (yourChoices == null) {
                        return;
                    }
                    for (int i = 0; i < yourChoices.size(); i++) {
                        byte[] kcv = null;
                        if (yourChoices.get(i) == 0) {
                            loadWorkingKeyPin();
                        } else if (yourChoices.get(i) == 1) {
                            loadWorkingKeyTrack();
                        } else if (yourChoices.get(i) == 2) {
                            loadWorkingKeyMac();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(e.getMessage(), MessageTag.ERROR);
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_pininput_online, functionid = INDEX_INPUTONLINEPIN)
    private void inputOnlinePin() {
        try {
            boolean isExternalPinpad = false;
            if("CPOS X5".equals(Build.MODEL)){
                isExternalPinpad = true;
            }
            ((MainActivity) context).startOnlinePinInput("6225760008219599",isExternalPinpad,false);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error)+e,MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_pininput_online_rnib, functionid = INDEX_INPUTONLINEPIN_RBIN)
    private void inputOnlinePinRNIB() {
        try {
            boolean isExternalPinpad = false;
            if("CPOS X5".equals(Build.MODEL)){
                isExternalPinpad = true;
            }
            ((MainActivity) context).startOnlinePinInput("6225760008219599",isExternalPinpad,true);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error)+e,MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_pininput_offline, functionid = INDEX_INPUTOFFLINE)
    private void inputOffLine() {
        try {
            boolean isExternalPinpad = false;
            if("CPOS X5".equals(Build.MODEL)){
                isExternalPinpad = true;
            }
            ((MainActivity) context).startOfflinePinInput(null, null,isExternalPinpad);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error)+e,MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_canle_pininput, functionid = INDEX_CANLEINPUTPIN)
    private void canleInputPin() {
        try {
            BaseFragment.setFunRunning(false);
            pinInput.cancelPinInput();
            showMessage(context.getString(R.string.msg_revocate_last_pwd_succ) + "\r\n", MessageTag.NORMAL);
        } catch (Exception e) {
            showMessage(context.getString(R.string.msg_revocate_last_pwd_ex) + e , MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_cal_mac, functionid = INDEX_CALMAC)
    private void calMac() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.msg_cal_mac), null, R.layout.dialog_caclmac, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                try {
                    if (id == -1) {//cancel
                        return;
                    }
                    EditText value = (EditText) dialogView.findViewById(R.id.edit_caclmac_value);
                    String string = value.getText().toString();
                    byte[] input = string.getBytes("GBK");
                    int macAlgorithm = MacAlgorithm.DES.ECB;
                    RadioGroup group1 = dialogView.findViewById(R.id.radioGroup_mac_type);
                    if (R.id.radio_MAC_ECB == group1.getCheckedRadioButtonId()) {
                        macAlgorithm = MacAlgorithm.DES.ECB;
                    } else if (R.id.radio_MAC_X99 == group1.getCheckedRadioButtonId()) {
                        macAlgorithm = MacAlgorithm.DES.X99;
                    } else if (R.id.radio_MAC_X919 == group1.getCheckedRadioButtonId()) {
                        macAlgorithm = MacAlgorithm.DES.X919;
                    } else if (R.id.radio_MAC_9606 == group1.getCheckedRadioButtonId()) {
                        macAlgorithm = MacAlgorithm.DES.M9606;
                    } else if (R.id.radio_MAC_CBC == group1.getCheckedRadioButtonId()) {
                        macAlgorithm = MacAlgorithm.DES.CBC;
                    } else if (R.id.radio_MAC_SM4 == group1.getCheckedRadioButtonId()) {
                        macAlgorithm = MacAlgorithm.SM4.X99;
                    } else if (R.id.radio_MAC_AES == group1.getCheckedRadioButtonId()) {
                        macAlgorithm = MacAlgorithm.AES.X99;
                    } else if (R.id.radio_MAC_SM4_union == group1.getCheckedRadioButtonId()) {
                        macAlgorithm = MacAlgorithm.SM4.SM4_UNIONPAY;
                    }
                    byte[] output = pinInput.calcMac(KeyManagement.MKSK, macAlgorithm, indexWKMac, input, null).getMac();
                    showMessage(context.getString(R.string.msg_enter_value) + string + "\r\n", MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_mac_algorithm) + macAlgorithm + "\r\n", MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_mac_cal_result) + (output==null?null:ISOUtils.hexString(output)), MessageTag.DATA);
                } catch (DeviceInvokeException e) {
                    showMessage(context.getString(R.string.msg_error) + e, MessageTag.ERROR);
                } catch (UnsupportedEncodingException e) {
                    showMessage(context.getString(R.string.msg_enter_value_error) + "\r\n", MessageTag.ERROR);
                } catch (Exception e) {
                    showMessage(e.getMessage(), MessageTag.ERROR);
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_encry, functionid = INDEX_ENCRYPTION)
    private void encryption() {
        DialogUtils.createCustomDialog(context, R.string.common_encrypt, null, R.layout.dialog_encryption, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                EditText value = view.findViewById(R.id.edit_encryption_value);
                value.setText("1234567812345678adcbadcbadcbadcb");
            }

            @Override
            public void onResult(int id, View dialogView) {
                EditText value = (EditText) dialogView.findViewById(R.id.edit_encryption_value);
                AlgorithmMode algorithmMode = AlgorithmMode.DES;
                CipherMode cipherMode = CipherMode.ECB;
                CipherExtParams cipherExtParams = new CipherExtParams();
                try {
                    byte[] input = ISOUtils.hex2byte(value.getText().toString());
                    RadioGroup group1 = dialogView.findViewById(R.id.radioGroup_encrypt_type1);
                    byte[] cbciv = null;
                    if (R.id.radio_CBC == group1.getCheckedRadioButtonId()) {
                        cipherMode = CipherMode.CBC;
                        if (AppConfig.MKSK_DES.equals(AppConfig.KEY_SYS_ALG)) {
                            cbciv = new byte[]{0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01};
                        } else if (AppConfig.MKSK_SM4.equals(AppConfig.KEY_SYS_ALG) || AppConfig.MKSK_AES.equals(AppConfig.KEY_SYS_ALG)) {
                            cbciv = new byte[]{0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01};
                        }
                    } else if (R.id.radio_ECB == group1.getCheckedRadioButtonId()) {
                        cipherMode = CipherMode.ECB;
                    }
                    if (AppConfig.MKSK_DES.equals(AppConfig.KEY_SYS_ALG)) {
                        algorithmMode = AlgorithmMode.DES;
                    } else if (AppConfig.MKSK_SM4.equals(AppConfig.KEY_SYS_ALG)) {
                        algorithmMode = AlgorithmMode.SM4;
                    } else if (AppConfig.MKSK_AES.equals(AppConfig.KEY_SYS_ALG)) {
                        algorithmMode = AlgorithmMode.AES;
                    }
                    cipherExtParams.setCbcInit(cbciv);
                    CipherResult cipherResult = pinInput.encrypt(KeyManagement.MKSK, algorithmMode, cipherMode, indexWKTrack, input, cipherExtParams);

                    showMessage(context.getString(R.string.msg_encrypt_data) + value.getText().toString() + "\r\n", MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_encrypt_key) + AppConfig.KEY_SYS_ALG + "\r\n", MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_encrypt_mode) + algorithmMode, MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_encrypt_result) + (cipherResult.getData() == null ? null : ISOUtils.hexString(cipherResult.getData())) + "\r\n", MessageTag.DATA);
                    AppConfig.Pin.encryptResult = cipherResult.getData();
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(context.getString(R.string.msg_encrypt_ex) + e, MessageTag.ERROR);
                    showMessage(context.getString(R.string.msg_encrypt_data) + value.getText().toString(), MessageTag.ERROR);
                    showMessage(context.getString(R.string.msg_encrypt_key) + AppConfig.KEY_SYS_ALG, MessageTag.ERROR);
                    showMessage(context.getString(R.string.msg_encrypt_mode) + algorithmMode, MessageTag.ERROR);

                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_decry, functionid = INDEX_DECRYPTION)
    private void decryption() {
        DialogUtils.createCustomDialog(context, R.string.common_decrypt, null, R.layout.dialog_encryption, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                EditText value = view.findViewById(R.id.edit_encryption_value);
                if (AppConfig.Pin.encryptResult == null) {
                    value.setHint(context.getString(R.string.msg_enter_or_encrypt_first));
                } else {
                    value.setText(ISOUtils.hexString(AppConfig.Pin.encryptResult));
                }
            }

            @Override
            public void onResult(int id, View dialogView) {
                EditText value = (EditText) dialogView.findViewById(R.id.edit_encryption_value);
                AlgorithmMode algorithmMode = AlgorithmMode.DES;
                CipherMode cipherMode = CipherMode.ECB;
                CipherExtParams cipherExtParams = new CipherExtParams();
                try {
                    byte[] input = ISOUtils.hex2byte(value.getText().toString());
                    RadioGroup group1 = dialogView.findViewById(R.id.radioGroup_encrypt_type1);
                    byte[] cbciv = null;
                    if (R.id.radio_CBC == group1.getCheckedRadioButtonId()) {
                        cipherMode = CipherMode.CBC;
                        if (AppConfig.MKSK_DES.equals(AppConfig.KEY_SYS_ALG)) {
                            cbciv = new byte[]{0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01};
                        } else if (AppConfig.MKSK_SM4.equals(AppConfig.KEY_SYS_ALG) || AppConfig.MKSK_AES.equals(AppConfig.KEY_SYS_ALG)) {
                            cbciv = new byte[]{0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01};
                        }
                    } else if (R.id.radio_ECB == group1.getCheckedRadioButtonId()) {
                        cipherMode = CipherMode.ECB;
                    }
                    if (AppConfig.MKSK_DES.equals(AppConfig.KEY_SYS_ALG)) {
                        algorithmMode = AlgorithmMode.DES;
                    } else if (AppConfig.MKSK_SM4.equals(AppConfig.KEY_SYS_ALG)) {
                        algorithmMode = AlgorithmMode.SM4;
                    } else if (AppConfig.MKSK_AES.equals(AppConfig.KEY_SYS_ALG)) {
                        algorithmMode = AlgorithmMode.AES;
                    }
                    cipherExtParams.setCbcInit(cbciv);
                    CipherResult cipherResult = pinInput.decrypt(KeyManagement.MKSK, algorithmMode, cipherMode, indexWKTrack, input, cipherExtParams);

                    showMessage(context.getString(R.string.msg_decrypt_data) + value.getText().toString(), MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_decrypt_key) + AppConfig.KEY_SYS_ALG, MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_decrypt_mode) + algorithmMode, MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_decrypt_result) + (cipherResult.getData() == null ? null : ISOUtils.hexString(cipherResult.getData())), MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_check_hint), MessageTag.TIP);
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(context.getString(R.string.msg_decrypt_ex) + e.getMessage(), MessageTag.ERROR);
                    showMessage(context.getString(R.string.msg_decrypt_data) + value.getText().toString(), MessageTag.ERROR);
                    showMessage(context.getString(R.string.msg_decrypt_key) + AppConfig.KEY_SYS_ALG, MessageTag.ERROR);
                    showMessage(context.getString(R.string.msg_decrypt_mode) + algorithmMode, MessageTag.ERROR);
                }
            }
        });

    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_delete_mk, functionid = INDEX_DELETEMK)
    private void deleteMK() {
        try {
            boolean isSucc = false;
            if (AppConfig.MKSK_DES.equals(AppConfig.KEY_SYS_ALG)) {
                isSucc = pinInput.deleteKey(KeyType.MASTER_KEY, AlgorithmMode.DES, AppConfig.Pin.MKSK_DES_INDEX_MK);
            } else if (AppConfig.MKSK_SM4.equals(AppConfig.KEY_SYS_ALG)) {
                isSucc = pinInput.deleteKey(KeyType.MASTER_KEY, AlgorithmMode.SM4, AppConfig.Pin.MKSK_SM4_INDEX_MK);
            } else if (AppConfig.MKSK_AES.equals(AppConfig.KEY_SYS_ALG)) {
                isSucc = pinInput.deleteKey(KeyType.MASTER_KEY, AlgorithmMode.AES, AppConfig.Pin.MKSK_AES_INDEX_MK);
            }
            if (isSucc == true) {
                showMessage(context.getString(R.string.msg_mk_del_succ) + "\r\n", MessageTag.NORMAL);
            } else {
                showMessage(context.getString(R.string.msg_mk_del_failed) + "\r\n", MessageTag.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_mk_del_failed) + e + "\r\n", MessageTag.ERROR);
        }
    }

    private void deleteWKPin() {
        boolean isSucc = false;
        if (AppConfig.MKSK_DES.equals(AppConfig.KEY_SYS_ALG)) {
            isSucc = pinInput.deleteKey(KeyType.PIN_KEY, AlgorithmMode.DES, AppConfig.Pin.MKSK_DES_INDEX_WK_PIN);
        } else if (AppConfig.MKSK_SM4.equals(AppConfig.KEY_SYS_ALG)) {
            isSucc = pinInput.deleteKey(KeyType.PIN_KEY, AlgorithmMode.SM4, AppConfig.Pin.MKSK_SM4_INDEX_WK_PIN);
        } else if (AppConfig.MKSK_AES.equals(AppConfig.KEY_SYS_ALG)) {
            isSucc = pinInput.deleteKey(KeyType.PIN_KEY, AlgorithmMode.AES, AppConfig.Pin.MKSK_AES_INDEX_WK_PIN);
        }
        if (isSucc == true) {
            showMessage(context.getString(R.string.msg_del_pin_wk_succ) + "\r\n", MessageTag.DATA);
        } else {
            showMessage(context.getString(R.string.msg_del_pin_wk_failed) + "\r\n", MessageTag.ERROR);
        }

    }

    private void deleteWKTrack() {
        boolean isSucc = false;
        if (AppConfig.MKSK_DES.equals(AppConfig.KEY_SYS_ALG)) {
            isSucc = pinInput.deleteKey(KeyType.TRACK_KEY, AlgorithmMode.DES, AppConfig.Pin.MKSK_DES_INDEX_WK_TRACK);
        } else if (AppConfig.MKSK_SM4.equals(AppConfig.KEY_SYS_ALG)) {
            isSucc = pinInput.deleteKey(KeyType.TRACK_KEY, AlgorithmMode.SM4, AppConfig.Pin.MKSK_SM4_INDEX_WK_TRACK);
        } else if (AppConfig.MKSK_AES.equals(AppConfig.KEY_SYS_ALG)) {
            isSucc = pinInput.deleteKey(KeyType.TRACK_KEY, AlgorithmMode.AES, AppConfig.Pin.MKSK_AES_INDEX_WK_TRACK);
        }

        if (isSucc == true) {
            showMessage(context.getString(R.string.msg_del_track_wk_succ) + "\r\n", MessageTag.DATA);
        } else {
            showMessage(context.getString(R.string.msg_del_track_wk_failed) + "\r\n", MessageTag.ERROR);
        }

    }

    private void deleteWKMac() {
        boolean isSucc = false;
        if (AppConfig.MKSK_DES.equals(AppConfig.KEY_SYS_ALG)) {
            isSucc = pinInput.deleteKey(KeyType.MAC_KEY, AlgorithmMode.DES, AppConfig.Pin.MKSK_DES_INDEX_WK_MAC);
        } else if (AppConfig.MKSK_SM4.equals(AppConfig.KEY_SYS_ALG)) {
            isSucc = pinInput.deleteKey(KeyType.MAC_KEY, AlgorithmMode.SM4, AppConfig.Pin.MKSK_SM4_INDEX_WK_MAC);
        } else if (AppConfig.MKSK_AES.equals(AppConfig.KEY_SYS_ALG)) {
            isSucc = pinInput.deleteKey(KeyType.MAC_KEY, AlgorithmMode.AES, AppConfig.Pin.MKSK_AES_INDEX_WK_MAC);
        }
        if (isSucc == true) {
            showMessage(context.getString(R.string.msg_del_mac_wk_succ) + "\r\n", MessageTag.DATA);
        } else {
            showMessage(context.getString(R.string.msg_del_mac_wk_failed) + "\r\n", MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_delete_wk, functionid = INDEX_DELETEWK)
    private void deleteWK() {
        final String[] arrayWorkingKey = new String[]{context.getString(R.string.msg_wk_array_pinwk), context.getString(R.string.msg_wk_array_trackwk), context.getString(R.string.msg_wk_array_macwk)};
        DialogUtils.createSingleChoiceDialog(context, context.getString(R.string.msg_pl_choose_del_wk_type), arrayWorkingKey, new DialogUtils.SingleChoiceDialogCallback() {
            @Override
            public void onResult(int id) {
                try {
                    if (id == 0) {
                        deleteWKPin();
                    } else if (id == 1) {
                        deleteWKTrack();
                    } else if (id == 2) {
                        deleteWKMac();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(e.getMessage(),MessageTag.ERROR);
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_delete_all_key, functionid = INDEX_DELETEALLKEY)
    private void deleteAllKey() {
        pinInput.deleteAllKeys();
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_check_key, functionid = INDEX_CHECKKEY)
    private void checkKey() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.msg_check_key_exist), null, R.layout.dialog_checkkey, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                try {
                    if (id == -1) {//cancel
                        return;
                    }
                    RadioGroup group1 = dialogView.findViewById(R.id.radioGroup_keytype);
                    EditText value = dialogView.findViewById(R.id.edit_keyindex);
                    int keyIndex = Integer.parseInt(value.getText().toString());
                    KeyType keyType = null;
                    AlgorithmMode algorithmMode = AlgorithmMode.DES;
                    if (R.id.radio_transkey == group1.getCheckedRadioButtonId()) {
                        keyType = KeyType.TRANSPORT_KEY;
                        if (AppConfig.MKSK_DES.equals(AppConfig.KEY_SYS_ALG)) {
                            algorithmMode = AlgorithmMode.DES;
                        } else if (AppConfig.MKSK_SM4.equals(AppConfig.KEY_SYS_ALG)) {
                            algorithmMode = AlgorithmMode.SM4;
                        } else if (AppConfig.MKSK_AES.equals(AppConfig.KEY_SYS_ALG)) {
                            algorithmMode = AlgorithmMode.AES;
                        }
                    } else if (R.id.radio_mainkey == group1.getCheckedRadioButtonId()) {
                        keyType = KeyType.MASTER_KEY;
                        if (AppConfig.MKSK_DES.equals(AppConfig.KEY_SYS_ALG)) {
                            keyType = KeyType.MASTER_KEY;
                            algorithmMode = AlgorithmMode.DES;
                        } else if (AppConfig.MKSK_SM4.equals(AppConfig.KEY_SYS_ALG)) {
                            algorithmMode = AlgorithmMode.SM4;
                        } else if (AppConfig.MKSK_AES.equals(AppConfig.KEY_SYS_ALG)) {
                            algorithmMode = AlgorithmMode.AES;
                        }
                    } else if (R.id.radio_pinkey == group1.getCheckedRadioButtonId()) {
                        keyType = KeyType.PIN_KEY;
                        if (AppConfig.MKSK_DES.equals(AppConfig.KEY_SYS_ALG)) {
                            algorithmMode = AlgorithmMode.DES;
                        } else if (AppConfig.MKSK_SM4.equals(AppConfig.KEY_SYS_ALG)) {
                            algorithmMode = AlgorithmMode.SM4;
                        } else if (AppConfig.MKSK_AES.equals(AppConfig.KEY_SYS_ALG)) {
                            algorithmMode = AlgorithmMode.AES;
                        }
                    } else if (R.id.radio_trackkey == group1.getCheckedRadioButtonId()) {
                        keyType = KeyType.TRACK_KEY;
                        if (AppConfig.MKSK_DES.equals(AppConfig.KEY_SYS_ALG)) {
                            algorithmMode = AlgorithmMode.DES;
                        } else if (AppConfig.MKSK_SM4.equals(AppConfig.KEY_SYS_ALG)) {
                            algorithmMode = AlgorithmMode.SM4;
                        } else if (AppConfig.MKSK_AES.equals(AppConfig.KEY_SYS_ALG)) {
                            algorithmMode = AlgorithmMode.AES;
                        }
                    } else if (R.id.radio_mackey == group1.getCheckedRadioButtonId()) {
                        keyType = KeyType.MAC_KEY;
                        if (AppConfig.MKSK_DES.equals(AppConfig.KEY_SYS_ALG)) {
                            algorithmMode = AlgorithmMode.DES;
                        } else if (AppConfig.MKSK_SM4.equals(AppConfig.KEY_SYS_ALG)) {
                            algorithmMode = AlgorithmMode.SM4;
                        } else if (AppConfig.MKSK_AES.equals(AppConfig.KEY_SYS_ALG)) {
                            algorithmMode = AlgorithmMode.AES;
                        }
                    }

                    boolean result = pinInput.checkKeyIsExist(keyType, algorithmMode, keyIndex, null);
                    showMessage(context.getString(R.string.msg_key_type) + keyType, MessageTag.NORMAL);
                    showMessage(context.getString(R.string.msg_key_index) + keyIndex, MessageTag.NORMAL);
                    showMessage(context.getString(R.string.msg_is_exist) + result, MessageTag.NORMAL);
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(context.getString(R.string.msg_check_key_ex) + e, MessageTag.ERROR);
                }
            }
        });
    }

    @MethodGridEntity(btnname = "HMAC", functionid = INDEX_HMAC)
    private void HMAC() {
        NPinpadModule nPinpadModule = ((NPinpadModule)pinInput);

        int masterTR31Index = 10;
        byte[] masterKey_TR31 = ISOUtils.hex2byte("1111111111111111111111111111111111111111111111111111111111111111");
        boolean result = nPinpadModule.injectKey(LoadKeyMode.PLAIN,null,0,null,
                AlgorithmMode.AES,masterTR31Index, InjectKeyType.MASTER_TR31,masterKey_TR31,null);
        showMessage("injectKey MASTER_TR31 result:"+result);

        int workingKeyHMAC = 10;
        byte[] HMACKey_TR31 = "D0112M7HC01E00005C9964E405CE7AB43A5C07567CFF686FA2D38AA440FB88DB14B77E0D667BC24A154F9B7AF7BBE3AD28D968B63F35AE3E".getBytes();//pain text:11223344556677881122334455667788
        result = nPinpadModule.injectKey(LoadKeyMode.TR31,AlgorithmMode.AES,masterTR31Index, InjectKeyType.MASTER_TR31,
                AlgorithmMode.HMAC,workingKeyHMAC, InjectKeyType.WORKINGKEY_MAC,HMACKey_TR31,null);
        showMessage("injectKey WORKINGKEY_MAC HMAC result:"+result);

        byte[] inputData = ISOUtils.hex2byte("0E0100KS080001E348D505B81BAB1EE951DC13AA1C560");
        MacResult macResult = nPinpadModule.calculateMac(MacType.HMAC_SHA1,workingKeyHMAC,inputData,null);
        showMessage("calculateMac HMAC_SHA1 result:"+ISOUtils.hexString(macResult.getMac()));
    }
}
