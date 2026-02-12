package com.newland.sdkdemo.fragment;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.newland.sdk.module.pin.AlgorithmMode;
import com.newland.sdk.module.pin.CipherExtParams;
import com.newland.sdk.module.pin.CipherMode;
import com.newland.sdk.module.pin.CipherResult;
import com.newland.sdk.module.pin.DukptDerivateUsage;
import com.newland.sdk.module.pin.DukptDerivedMode;
import com.newland.sdk.module.pin.KeyManagement;
import com.newland.sdk.module.pin.LoadKeyMode;
import com.newland.sdk.module.pin.MacAlgorithm;
import com.newland.sdk.module.pin.MacResult;
import com.newland.sdk.module.pin.MacType;
import com.newland.sdk.module.pin.NCalMacExtParams;
import com.newland.sdk.module.pin.NCipherExtParams;
import com.newland.sdk.module.pin.NLoadDuktpExtParams;
import com.newland.sdk.module.pin.NPinpadModule;
import com.newland.sdk.module.pin.PaddingMode;
import com.newland.sdk.module.pin.PinpadModule;
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

/**
 * Author by bxy, Date on 2019/5/11 0011.
 */
public class PinDUKPTFragment extends BaseFragment {

    private PinpadModule pinInput;
    private int dukptIndex;

    public PinDUKPTFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_pin_f);
    }

    @Override
    public void initData() {
        pinInput = moduleManage.getPinpadModule();
        if (AppConfig.DUKPT_DES.equals(AppConfig.KEY_SYS_ALG)) {
            dukptIndex = AppConfig.Pin.DUKPT_DES_INDEX;
        } else if (AppConfig.DUKPT_AES.equals(AppConfig.KEY_SYS_ALG)) {
            dukptIndex = AppConfig.Pin.DUKPT_AES_INDEX;
        }
    }

    @Override
    public Object getModule() {
        return PinDUKPTFragment.this;
    }

    private static final int INDEX_LOADDUKPT = 1;
    private static final int INDEX_GETDUKPTKSN = 2;
    private static final int INDEX_KSNINCREASE = 3;
    private static final int INDEX_INPUTONLINEPIN = 4;
    private static final int INDEX_INPUTONLINEPIN_RNIB = 5;
    private static final int INDEX_CANLEINPUTPIN = 6;
    private static final int INDEX_CALMAC = 7;
    private static final int INDEX_ENCRYPTION = 8;
    private static final int INDEX_DECRYPTION = 9;

    @MethodGridEntity(btnnameid = R.string.tv_pin_load_ipek, functionid = INDEX_LOADDUKPT)
    public void loadDukpt() {
        if(AppConfig.DUKPT_AES.equals(AppConfig.KEY_SYS_ALG)){
            String KSN = "62303030303030200001";//When loading KSN, the last 21bit will be zeroed out,32303030303030200001 is the first KSN
            String ipek = "2F8E26EF7E61558D27367721654C26C5";
            NLoadDuktpExtParams extParams = new NLoadDuktpExtParams();
            extParams.setAlgorithmMode(AlgorithmMode.AES);
            boolean result = pinInput.loadIPEK(LoadKeyMode.PLAIN, dukptIndex, ISOUtils.hex2byte(KSN), ISOUtils.hex2byte(ipek), extParams);
            String msg = (result ? context.getString(R.string.msg_load_ipek_result) : context.getString(R.string.msg_load_ipek_failed));
            showMessage(msg + "\r\n", MessageTag.DATA);
        }else if(AppConfig.DUKPT_DES.equals(AppConfig.KEY_SYS_ALG)){
            String KSN = "32303030303030200001";//When loading KSN, the last 21bit will be zeroed out,32303030303030200001 is the first KSN
            String ipek = "64A16ADE8FC4E6CB20582F9DD297EA22";//plain key:2F8E26EF7E61558D27367721654C26C5
            boolean result = pinInput.loadIPEK(LoadKeyMode.DEFAULT_ENCRYPT, dukptIndex, ISOUtils.hex2byte(KSN), ISOUtils.hex2byte(ipek), null);
            String msg = (result ? context.getString(R.string.msg_load_ipek_result) : context.getString(R.string.msg_load_ipek_failed));
            showMessage(msg + "\r\n", MessageTag.DATA);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_get_ksn, functionid = INDEX_GETDUKPTKSN)
    private void getDukptKsn() {
        try {
            byte[] ksn = null;
            if(AppConfig.DUKPT_AES.equals(AppConfig.KEY_SYS_ALG)){
                showMessage(context.getString(R.string.msg_get_dukpt_ksn), MessageTag.TIP);
                ksn = pinInput.getDukptAESKsn(dukptIndex);
            }else if (AppConfig.DUKPT_DES.equals(AppConfig.KEY_SYS_ALG)){
                showMessage(context.getString(R.string.msg_get_dukpt_ksn), MessageTag.TIP);
                ksn = pinInput.getDukptKsn(dukptIndex);
            }
            showMessage("ksn:" + (ksn == null ? null : ISOUtils.hexString(ksn)), MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_get_dukpt_ksn_faild) + e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_increase_ksn, functionid = INDEX_KSNINCREASE)
    private void ksnIncrease() {
        try {
            boolean result = false;
            if(AppConfig.DUKPT_AES.equals(AppConfig.KEY_SYS_ALG)){
                showMessage(context.getString(R.string.msg_increase_dukpt_ksn), MessageTag.TIP);
                result = pinInput.ksnAESIncrease(dukptIndex);
            }else if (AppConfig.DUKPT_DES.equals(AppConfig.KEY_SYS_ALG)){
                showMessage(context.getString(R.string.msg_increase_dukpt_ksn), MessageTag.TIP);
                result = pinInput.ksnIncrease(dukptIndex);
            }
            showMessage(context.getString(R.string.msg_increase_dukpt_ksn_resut) + result, MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_increase_dukpt_ksn_faild) + e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_pininput, functionid = INDEX_INPUTONLINEPIN)
    private void inputOnlinePin() {
        try {
            boolean isExternalPinpad = false;
            if("CPOS X5".equals(Build.MODEL)){
                isExternalPinpad = true;
            }
            ((MainActivity) context).startOnlinePinInput("6225760008219599",isExternalPinpad,false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_pininput_online_rnib, functionid = INDEX_INPUTONLINEPIN_RNIB)
    private void inputOnlinePinRNIB() {
        try {
            boolean isExternalPinpad = false;
            if("CPOS X5".equals(Build.MODEL)){
                isExternalPinpad = true;
            }
            ((MainActivity) context).startOnlinePinInput("6225760008219599",isExternalPinpad,false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_canle_pininput, functionid = INDEX_CANLEINPUTPIN)
    private void canleInputPin() {
        try {
            BaseFragment.setFunRunning(false);
            pinInput.cancelPinInput();
            showMessage(context.getString(R.string.msg_revocate_last_pwd_succ) + "\r\n", MessageTag.TIP);
        } catch (Exception e) {
            showMessage(context.getString(R.string.msg_revocate_last_pwd_ex) + e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_cal_mac, functionid = INDEX_CALMAC)
    private void calMac() {
        if(AppConfig.DUKPT_AES.equals(AppConfig.KEY_SYS_ALG)){
            NCalMacExtParams nCalMacExtParams = new NCalMacExtParams();
            nCalMacExtParams.setDukptDerivateUsage(DukptDerivateUsage.MAC_GEN);
            nCalMacExtParams.setDerivateKeyLen(16);
            byte[] data = ISOUtils.hex2byte("88888881113366677883373215563223abcdeffdaabbccdd");
            MacResult result = ((NPinpadModule)pinInput).calculateMac(MacType.DUKPT_AES_9606,dukptIndex,data,nCalMacExtParams);
            showMessage("DUKPT_AES_X99" + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.msg_enter_value) + ISOUtils.hexString(data) + "\r\n", MessageTag.DATA);
            showMessage(context.getString(R.string.msg_mac_cal_result) + (ISOUtils.hexString(result.getMac())), MessageTag.DATA);
        }else if(AppConfig.DUKPT_DES.equals(AppConfig.KEY_SYS_ALG)){
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
                        } else {
                            showMessage(context.getString(R.string.common_nonsupport) + "\r\n", MessageTag.ERROR);
                            return;
                        }
                        byte[] output = pinInput.calcMac(KeyManagement.DUKPT, macAlgorithm, dukptIndex, input, null).getMac();
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
                try {
                    byte[] input = ISOUtils.hex2byte(value.getText().toString());
                    RadioGroup group1 = dialogView.findViewById(R.id.radioGroup_encrypt_type1);
                    byte[] cbciv = null;
                    if (R.id.radio_CBC == group1.getCheckedRadioButtonId()) {
                        cipherMode = CipherMode.CBC;

                        if (AppConfig.DUKPT_DES.equals(AppConfig.KEY_SYS_ALG)) {
                            cbciv = new byte[]{0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01};
                        } else if (AppConfig.DUKPT_AES.equals(AppConfig.KEY_SYS_ALG) || AppConfig.MKSK_AES.equals(AppConfig.KEY_SYS_ALG)) {
                            cbciv = new byte[]{0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01};
                        }
                    } else if (R.id.radio_ECB == group1.getCheckedRadioButtonId()) {
                        cipherMode = CipherMode.ECB;
                    }
                    if (AppConfig.DUKPT_DES.equals(AppConfig.KEY_SYS_ALG)) {
                        algorithmMode = AlgorithmMode.DES;
                    } else if (AppConfig.DUKPT_AES.equals(AppConfig.KEY_SYS_ALG)) {
                        algorithmMode = AlgorithmMode.AES;
                    }

                    CipherResult cipherResult = null;
                    if (AppConfig.DUKPT_AES.equals(AppConfig.KEY_SYS_ALG)){
                        NCipherExtParams nCipherExtParams = new NCipherExtParams();
                        nCipherExtParams.setInitialVector(cbciv);
                        nCipherExtParams.setPaddingMode(PaddingMode.Mode.NONE);
                        nCipherExtParams.setDukptDerivateUsage(DukptDerivateUsage.DATA_BOTH);
                        nCipherExtParams.setDerivateKeyLen(16);
                        cipherResult = pinInput.encrypt(KeyManagement.DUKPT, algorithmMode, cipherMode, dukptIndex, input, nCipherExtParams);
                    }else if (AppConfig.DUKPT_DES.equals(AppConfig.KEY_SYS_ALG)) {
                        CipherExtParams cipherExtParams = new CipherExtParams();
                        cipherExtParams.setCbcInit(cbciv);
                        cipherResult = pinInput.encrypt(KeyManagement.DUKPT, algorithmMode, cipherMode, dukptIndex, input, cipherExtParams);
                    }
                    showMessage(context.getString(R.string.msg_encrypt_data) + value.getText().toString() + "\r\n", MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_encrypt_key) + AppConfig.KEY_SYS_ALG + "\r\n", MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_encrypt_mode) + cipherMode, MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_encrypt_result) + (cipherResult.getData() == null ? null : ISOUtils.hexString(cipherResult.getData())), MessageTag.DATA);
                    AppConfig.Pin.encryptResult = cipherResult.getData();
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(context.getString(R.string.msg_encrypt_ex) + e, MessageTag.ERROR);
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
                try {
                    byte[] input = ISOUtils.hex2byte(value.getText().toString());
                    RadioGroup group1 = dialogView.findViewById(R.id.radioGroup_encrypt_type1);
                    byte[] cbciv = null;
                    if (R.id.radio_CBC == group1.getCheckedRadioButtonId()) {
                        cipherMode = CipherMode.CBC;
                        if (AppConfig.DUKPT_DES.equals(AppConfig.KEY_SYS_ALG)) {
                            cbciv = new byte[]{0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01};
                        } else if (AppConfig.DUKPT_AES.equals(AppConfig.KEY_SYS_ALG) || AppConfig.MKSK_AES.equals(AppConfig.KEY_SYS_ALG)) {
                            cbciv = new byte[]{0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01};
                        }
                    } else if (R.id.radio_ECB == group1.getCheckedRadioButtonId()) {
                        cipherMode = CipherMode.ECB;
                    }
                    if (AppConfig.DUKPT_DES.equals(AppConfig.KEY_SYS_ALG)) {
                        algorithmMode = AlgorithmMode.DES;
                    } else if (AppConfig.DUKPT_AES.equals(AppConfig.KEY_SYS_ALG)) {
                        algorithmMode = AlgorithmMode.AES;
                    }
                    CipherResult cipherResult = null;
                    if (AppConfig.DUKPT_DES.equals(AppConfig.KEY_SYS_ALG)) {
                        CipherExtParams cipherExtParams = new CipherExtParams();
                        cipherExtParams.setCbcInit(cbciv);
                        cipherResult = pinInput.decrypt(KeyManagement.DUKPT, algorithmMode, cipherMode, dukptIndex, input, cipherExtParams);
                    } else if (AppConfig.DUKPT_AES.equals(AppConfig.KEY_SYS_ALG)) {
                        NCipherExtParams nCipherExtParams = new NCipherExtParams();
                        nCipherExtParams.setInitialVector(cbciv);
                        nCipherExtParams.setPaddingMode(PaddingMode.Mode.NONE);
                        nCipherExtParams.setDukptDerivateUsage(DukptDerivateUsage.DATA_BOTH);
                        nCipherExtParams.setDerivateKeyLen(16);
                        cipherResult = pinInput.decrypt(KeyManagement.DUKPT, algorithmMode, cipherMode, dukptIndex, input, nCipherExtParams);
                    }

                    showMessage(context.getString(R.string.msg_decrypt_data) + value.getText().toString() + "\r\n", MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_decrypt_key) + AppConfig.KEY_SYS_ALG + "\r\n", MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_decrypt_mode) + algorithmMode, MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_decrypt_result) + (cipherResult.getData() == null ? null : ISOUtils.hexString(cipherResult.getData())), MessageTag.DATA);
                    showMessage(context.getString(R.string.msg_check_hint) + "\r\n", MessageTag.TIP);
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(context.getString(R.string.msg_decrypt_ex) + e, MessageTag.ERROR);
                    showMessage(context.getString(R.string.msg_decrypt_data) + value.getText().toString(), MessageTag.ERROR);
                    showMessage(context.getString(R.string.msg_decrypt_key) + AppConfig.KEY_SYS_ALG, MessageTag.ERROR);
                    showMessage(context.getString(R.string.msg_decrypt_mode) + algorithmMode, MessageTag.ERROR);
                }
            }
        });

    }
}
