package com.newland.sdkdemo.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.newland.NlBluetooth.util.Const;
import com.newland.sdk.common.RunningModel;
import com.newland.sdk.module.externalPin.BleBaseParams;
import com.newland.sdk.module.externalPin.BleBaseStatusListener;
import com.newland.sdk.module.externalPin.DisplayColorImageParams;
import com.newland.sdk.module.externalPin.DisplayDirection;
import com.newland.sdk.module.externalPin.ExtPinpadModule;
import com.newland.sdk.module.externalPin.FontSize;
import com.newland.sdk.module.externalPin.MenuOptionListener;
import com.newland.sdk.module.externalPin.MenuOptionParams;
import com.newland.sdk.module.externalPin.PinpadExtParams;
import com.newland.sdk.module.externalPin.PinpadInitExtParams;
import com.newland.sdk.module.externalPin.PropertyKey;
import com.newland.sdk.module.externalPin.ScanParams;
import com.newland.sdk.module.externalPin.ScannerListener;
import com.newland.sdk.module.externalPin.UpdateFiles;
import com.newland.sdk.module.externalPin.UpdateListener;
import com.newland.sdk.module.externalsignature.DoSignExtParams;
import com.newland.sdk.module.externalsignature.DoSignListener;
import com.newland.sdk.module.externalsignature.ExtSignatureModule;
import com.newland.sdk.module.externalsignature.SignatureExtParams;
import com.newland.sdk.module.pin.AlgorithmMode;
import com.newland.sdk.module.pin.CipherExtParams;
import com.newland.sdk.module.pin.CipherMode;
import com.newland.sdk.module.pin.CipherResult;
import com.newland.sdk.module.pin.KeyManagement;
import com.newland.sdk.module.pin.KeyType;
import com.newland.sdk.module.pin.LoadDuktpExtParams;
import com.newland.sdk.module.pin.LoadKeyMode;
import com.newland.sdk.module.pin.LoadMKExtParams;
import com.newland.sdk.module.pin.LoadWKMode;
import com.newland.sdk.module.pin.MacAlgorithm;
import com.newland.sdk.module.pin.MacResult;
import com.newland.sdk.module.pin.PinInputListener;
import com.newland.sdk.module.pin.TusnData;
import com.newland.sdk.module.pin.WorkingKeyType;
import com.newland.sdk.module.serialport.Baudrate;
import com.newland.sdk.module.serialport.PinpadModel;
import com.newland.sdk.module.serialport.PortType;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Author by bxy, Date on 2019/5/11 0011.
 */
public class ExternalPinInputFragment extends BaseFragment {
    private ExtPinpadModule externalPinInput;
    private ExtSignatureModule signatureModule;
    private boolean isOverseas = true;
    private PinpadModel externaltype = PinpadModel.SP;
    private PortType portType = PortType.PINPAD;
    private Baudrate baudRate = Baudrate.BPS115200;
    private static final int INDEX_EXTERNALPIN_INIT = 1;
    private static final int INDEX_EXTERNALPIN_LOAD_MK = 2;
    private static final int INDEX_EXTERNALPIN_LOAD_WK = 3;
    private static final int INDEX_EXTERNALPIN_CAL_MAC = 4;
    private static final int INDEX_EXTERNALPIN_ENCRY = 5;
    private static final int INDEX_EXTERNALPIN_DECRY = 6;
    private static final int INDEX_EXTERNALPIN_PININPUT = 7;
    private static final int INDEX_EXTERNALPIN_CANCEL = 8;
    private static final int INDEX_EXTERNALPIN_PLAIN_PIN = 9;
    private static final int INDEX_EXTERNALPIN_SIGN = 10;
    private static final int INDEX_EXTERNALPIN_LCD_DISPLAY = 11;
    private static final int INDEX_EXTERNALPIN_CLEAR_LCD = 12;
    private static final int INDEX_EXTERNALPIN_GET_INFO = 13;
    private static final int INDEX_EXTERNALPIN_LOAD_RSA = 14;
    private static final int INDEX_EXTERNALPIN_ENCRY_DECRY_RSA = 15;
    private int dukptIndex;


    protected static final String MAINKEY = "11111111111111111111111111111111";// preset value
    // 11111111111111111111111111111111

    protected static final String WORKINGKEY_DATA_MAC = "4DE5E8B8A9DCDDF94DE5E8B8A9DCDDF8";//
    // preset value  C3CA30E29C332D8BAF1C09C84CE0367D
    protected static final String WORKINGKEY_DATA_TRACK = "DBFE96D0A5F09D24DBFE96D0A5F09D24";//
    // preset value 4DE5E8B8A9DCDDF94DE5E8B8A9DCDDF9
//	protected static final String WORKINGKEY_DATA_TRACK2 = "A0C45C59F1E549BBDD600F71D757FBAC";//
//	preset value

    protected static final String WORKINGKEY_DATA_PIN = "D2CEEE5C1D3AFBAF00374E0CC1526C86";//
    // preset value 2A288F61348FEE93FE9C0FC714BCDD73

    protected static final String SM_MAINKEY = "11111111111111111111111111111111";// preset value
    //  11111111111111111111111111111111
    protected static final String SM_WORKINGKEY_DATA_MAC = "E97748E56A3D1F883832852C305242E8";//
    // preset value 17171717171717171717171717171717
    protected static final String SM_WORKINGKEY_DATA_TRACK = "585B9D3F745C8EA95400BD2A3EBDFABF";
    // preset value 22222222222222222222222222222222
    protected static final String SM_WORKINGKEY_DATA_PIN = "3526A987BBC659EC3219956DC1FF38B0";//
    // preset value 33333333333333333333333333333333
    private int indexMK, indexWKPin, indexWKTrack, indexWKMac;

    public ExternalPinInputFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_external_pin);
    }

    @Override
    public void initData() {
        externalPinInput = moduleManage.getExtPinpadModule();
        signatureModule = moduleManage.getExtSignatureModule();
        dukptIndex = AppConfig.Pin.DUKPT_DES_INDEX;
        if (AppConfig.MKSK_DES.equals(AppConfig.KEY_SYS_ALG)) {
            indexMK = AppConfig.Pin.MKSK_DES_INDEX_MK;
            indexWKPin = AppConfig.Pin.MKSK_DES_INDEX_WK_PIN;
            indexWKTrack = AppConfig.Pin.MKSK_DES_INDEX_WK_TRACK;
            indexWKMac = AppConfig.Pin.MKSK_DES_INDEX_WK_MAC;
        } else if (AppConfig.MKSK_SM4.equals(AppConfig.KEY_SYS_ALG)) {
            indexMK = AppConfig.Pin.MKSK_SM4_INDEX_MK;
            indexWKPin = AppConfig.Pin.MKSK_SM4_INDEX_WK_PIN;
            indexWKTrack = AppConfig.Pin.MKSK_SM4_INDEX_WK_TRACK;
            indexWKMac = AppConfig.Pin.MKSK_SM4_INDEX_WK_MAC;
        } else if (AppConfig.MKSK_AES.equals(AppConfig.KEY_SYS_ALG)) {
            indexMK = AppConfig.Pin.MKSK_AES_INDEX_MK;
            indexWKPin = AppConfig.Pin.MKSK_AES_INDEX_WK_PIN;
            indexWKTrack = AppConfig.Pin.MKSK_AES_INDEX_WK_TRACK;
            indexWKMac = AppConfig.Pin.MKSK_AES_INDEX_WK_MAC;
        }
    }

    @Override
    public Object getModule() {
        return ExternalPinInputFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.tv_external_pin_init, functionid =
            INDEX_EXTERNALPIN_INIT)
    private void initExternalPinpad() {
        DialogUtils.createSingleChoiceDialog(context, "select module", new String[]{"just pinpad"
                , "BluetoothBase pinpad port", "BluetoothBase USB1 port", "USB"},
                new DialogUtils.SingleChoiceDialogCallback() {
            @Override
            public void onResult(int id) {
                if (id < 0) {
                    return;
                }
                if (id == 0) {
                    boolean result = externalPinInput.init(null);
                    if (result) {
                        showMessage(context.getString(R.string.msg_ext_pininput_init_pinpad_success) + "\r\n", MessageTag.NORMAL);
                    } else {
                        showMessage(context.getString(R.string.msg_ext_pininput_init_pinpad_exception) + "\r\n", MessageTag.ERROR);
                    }
                } else if (id == 1) {
                    PinpadInitExtParams pinpadInitExtParams =
                            new PinpadInitExtParams(PortType.BLEBASE_RS232, null, null, null);
                    boolean result = externalPinInput.init(pinpadInitExtParams);

                    if (result) {
                        showMessage(context.getString(R.string.msg_ext_pininput_init_pinpad_success) + "\r\n", MessageTag.NORMAL);
                    } else {
                        showMessage(context.getString(R.string.msg_ext_pininput_init_pinpad_exception) + "\r\n", MessageTag.ERROR);
                    }
                } else if (id == 2) {
                    PinpadInitExtParams pinpadInitExtParams =
                            new PinpadInitExtParams(PortType.BLEBASE_USB1, null, null, null);
                    BleBaseParams bleBaseParams = new BleBaseParams(false, true);
                    /**
                     * BluetoothBase connection status
                     * @param status BLUETOOTH_UNKNOWN = 0;
                     *         BLUETOOTH_CLOSED = 1;
                     *         BLUETOOTH_CLOSING = 2;
                     *         BLUETOOTH_OPENING = 3;
                     *         BLUETOOTH_IDLE = 4;
                     *         BLUETOOTH_SEARCHING = 5;
                     *         BLUETOOTH_CONNECTING = 6;
                     *         BLUETOOTH_CONNECTED = 7;
                     *         BLUETOOTH_WAITING = 8;
                     *         BLUETOOTH_BOND_BONDING = 10;
                     *         BLUETOOTH_BOND_NONE = 11;
                     *         BLUETOOTH_BOND_BONDED = 12;
                     *         BLUETOOTH_CONNECT_FAIL = 13;
                     *         BLUETOOTH_DISCONNECTED = 14;
                     *         BLUETOOTH_COMMAND_CONNECTING = 9;
                     */
                    bleBaseParams.setBleBaseStatusListener(new BleBaseStatusListener() {
                        @Override
                        public void onStatusChange(int status) {
                            showMessage("onStatusChange:" + status + "\r\n", MessageTag.DATA);

                            switch (status) {
                                case Const.StatusConst.BLUETOOTH_CONNECTED:
                                    showMessage("Bluetooth Connected." + "\r\n", MessageTag.NORMAL);
                                    break;
                                case Const.StatusConst.BLUETOOTH_DISCONNECTED:
                                    showMessage("Bluetooth Disconnected." + "\r\n",
                                            MessageTag.NORMAL);
                                    break;
                            }


                        }
                    });
                    pinpadInitExtParams.setBleBaseParams(bleBaseParams);
                    boolean result = externalPinInput.init(pinpadInitExtParams);

                    if (result) {
                        showMessage(context.getString(R.string.msg_ext_pininput_init_pinpad_success) + "\r\n", MessageTag.NORMAL);
                    } else {
                        showMessage(context.getString(R.string.msg_ext_pininput_init_pinpad_exception) + "\r\n", MessageTag.ERROR);
                    }
                } else if (id == 3) {
                    PinpadInitExtParams pinpadInitExtParams =
                            new PinpadInitExtParams(PortType.USB, null, null, null);
                    boolean result = externalPinInput.init(pinpadInitExtParams);

                    if (result) {
                        showMessage(context.getString(R.string.msg_ext_pininput_init_pinpad_success) + "\r\n", MessageTag.NORMAL);
                    } else {
                        showMessage(context.getString(R.string.msg_ext_pininput_init_pinpad_exception) + "\r\n", MessageTag.ERROR);
                    }
                }
            }
        });

    }

    @MethodGridEntity(btnnameid = R.string.tv_load_mk, functionid = INDEX_EXTERNALPIN_LOAD_MK)
    private void externalPinpadLoadMk() {
        try {
            boolean result = false;
            String msg;
            showMessage(context.getString(R.string.msg_ext_pininput_start_load_main_key) + "\r\n"
                    , MessageTag.TIP);
            if (AppConfig.MKSK_SM4.equals(AppConfig.KEY_SYS_ALG)) {
                result = externalPinInput.loadMasterKey(LoadKeyMode.PLAIN, AlgorithmMode.SM4,
                        AppConfig.Pin.MKSK_SM4_INDEX_MK, ISOUtils.hex2byte(SM_MAINKEY),
                        ISOUtils.hex2byte("F8D068"), null);
                msg = (result ?
                        context.getString(R.string.msg_ext_pininput_load_SM4_main_key_success) :
                        context.getString(R.string.msg_ext_pininput_load_SM4_main_key_fail));
                showMessage(msg + "\r\n", MessageTag.DATA);
            } else if (AppConfig.MKSK_AES.equals(AppConfig.KEY_SYS_ALG)) {//The AES default KEK
                // is 16 bytes 0x37
                //String mkAES = "B396917369B9B91711A0C9594B8D736E";//PLAIN
                // KEY:11111111111111111111111111111111
                String mkAES = "11111111111111111111111111111111";
                result = externalPinInput.loadMasterKey(LoadKeyMode.PLAIN, AlgorithmMode.AES,
                        AppConfig.Pin.MKSK_AES_INDEX_MK, ISOUtils.hex2byte(mkAES),
                        ISOUtils.hex2byte("EE23D81C34"), null);
                msg = (result ?
                        context.getString(R.string.msg_ext_pininput_load_main_key_success) :
                        context.getString(R.string.msg_ext_pininput_load_main_key_fail));
                showMessage(msg + "\r\n", MessageTag.DATA);
            } else {
                result = externalPinInput.loadMasterKey(LoadKeyMode.PLAIN, AlgorithmMode.DES,
                        AppConfig.Pin.MKSK_DES_INDEX_MK, ISOUtils.hex2byte(MAINKEY),
                        ISOUtils.hex2byte("82E136"), null);
                msg = (result ?
                        context.getString(R.string.msg_ext_pininput_load_main_key_success) :
                        context.getString(R.string.msg_ext_pininput_load_main_key_fail));
                showMessage(msg + "\r\n", MessageTag.DATA);

                LoadMKExtParams loadMKExtParams = new LoadMKExtParams();
                loadMKExtParams.setKekIndex(AppConfig.Pin.MKSK_DES_INDEX_MK);
                result = externalPinInput.loadMasterKey(LoadKeyMode.CUSTOM_ENCRYPT,
                        AlgorithmMode.DES, 2, ISOUtils.hex2byte(MAINKEY), ISOUtils.hex2byte(
                                "4CA89C"), loadMKExtParams);
                msg = (result ?
                        context.getString(R.string.msg_ext_pininput_load_main_key_success) :
                        context.getString(R.string.msg_ext_pininput_load_main_key_fail));
                showMessage(msg + "\r\n", MessageTag.DATA);
            }
        } catch (Exception e) {
            showMessage(context.getString(R.string.msg_ext_pininput_load_main_key_excetption) + e + "\r\n", MessageTag.NORMAL);
        }

    }

    @MethodGridEntity(btnnameid = R.string.tv_load_wk, functionid = INDEX_EXTERNALPIN_LOAD_WK)
    private void externalPinpadLoadWK() {
        try {
            boolean result = false;
            String msg;
            if (AppConfig.MKSK_DES.equals(AppConfig.KEY_SYS_ALG)) {
                result = externalPinInput.loadWorkingKey(WorkingKeyType.PIN, AlgorithmMode.DES,
                        AppConfig.Pin.MKSK_DES_INDEX_MK,
                        AppConfig.Pin.MKSK_DES_INDEX_WK_PIN,
                        ISOUtils.hex2byte(WORKINGKEY_DATA_PIN), ISOUtils.hex2byte("58A2BB"));

                msg = (result ? context.getString(R.string.msg_load_pin_wk_succ) :
                        context.getString(R.string.msg_load_pin_wk_failed));
                showMessage(msg + "\r\n", MessageTag.DATA);

                result = externalPinInput.loadWorkingKey(WorkingKeyType.MAC, AlgorithmMode.DES,
                        AppConfig.Pin.MKSK_DES_INDEX_MK,
                        AppConfig.Pin.MKSK_DES_INDEX_WK_MAC,
                        ISOUtils.hex2byte(WORKINGKEY_DATA_MAC), ISOUtils.hex2byte("94897B"));

                msg = (result ? context.getString(R.string.msg_load_mac_wk_succ) :
                        context.getString(R.string.msg_load_mac_wk_failed));
                showMessage(msg + "\r\n", MessageTag.DATA);

                result = externalPinInput.loadWorkingKey(WorkingKeyType.TRACK, AlgorithmMode.DES,
                        AppConfig.Pin.MKSK_DES_INDEX_MK,
                        AppConfig.Pin.MKSK_DES_INDEX_WK_TRACK,
                        ISOUtils.hex2byte(WORKINGKEY_DATA_TRACK), ISOUtils.hex2byte("5B4C8B"));

                msg = (result ? context.getString(R.string.msg_load_track_wk_succ) :
                        context.getString(R.string.msg_load_track_wk_failed));
                showMessage(msg + "\r\n", MessageTag.DATA);
            } else if (AppConfig.MKSK_SM4.equals(AppConfig.KEY_SYS_ALG)) {
                result = externalPinInput.loadWorkingKey(WorkingKeyType.PIN, AlgorithmMode.SM4,
                        AppConfig.Pin.MKSK_SM4_INDEX_MK,
                        AppConfig.Pin.MKSK_SM4_INDEX_WK_PIN,
                        ISOUtils.hex2byte(SM_WORKINGKEY_DATA_PIN), null);

                msg = (result ? context.getString(R.string.msg_load_pin_wk_succ) :
                        context.getString(R.string.msg_load_pin_wk_failed));
                showMessage(msg + "\r\n", MessageTag.DATA);

                result = externalPinInput.loadWorkingKey(WorkingKeyType.MAC, AlgorithmMode.SM4,
                        AppConfig.Pin.MKSK_SM4_INDEX_MK,
                        AppConfig.Pin.MKSK_SM4_INDEX_WK_MAC,
                        ISOUtils.hex2byte(SM_WORKINGKEY_DATA_MAC), null);

                msg = (result ? context.getString(R.string.msg_load_mac_wk_succ) :
                        context.getString(R.string.msg_load_mac_wk_failed));
                showMessage(msg + "\r\n", MessageTag.DATA);

                result = externalPinInput.loadWorkingKey(WorkingKeyType.TRACK, AlgorithmMode.SM4,
                        AppConfig.Pin.MKSK_SM4_INDEX_MK,
                        AppConfig.Pin.MKSK_SM4_INDEX_WK_TRACK,
                        ISOUtils.hex2byte(SM_WORKINGKEY_DATA_TRACK), null);

                msg = (result ? context.getString(R.string.msg_load_track_wk_succ) :
                        context.getString(R.string.msg_load_track_wk_failed));
                showMessage(msg + "\r\n", MessageTag.DATA);

            } else if (AppConfig.MKSK_AES.equals(AppConfig.KEY_SYS_ALG)) {
                byte[] pinKeyAES = ISOUtils.hex2byte("34A9575EEFE69DE078B4E29A24D04CD7");//PLAIN
                // KEY:66666666666666666666666666666666
                byte[] checkKcv = ISOUtils.hex2byte("2DB6A815C6");
                result = externalPinInput.loadWorkingKey(WorkingKeyType.PIN, AlgorithmMode.AES,
                        AppConfig.Pin.MKSK_AES_INDEX_MK, AppConfig.Pin.MKSK_AES_INDEX_WK_PIN,
                        pinKeyAES, checkKcv);
                msg = (result ? context.getString(R.string.msg_load_pin_wk_succ) :
                        context.getString(R.string.msg_load_pin_wk_failed));
                showMessage(msg + "\r\n", MessageTag.DATA);

                byte[] macKeyAES = ISOUtils.hex2byte("DC44B424BCB85288CE3BE42430864E8B");//PLAIN
                // KEY:77777777777777777777777777777777
                byte[] macKcv = ISOUtils.hex2byte("2C3F18B2B6");
                result = externalPinInput.loadWorkingKey(WorkingKeyType.MAC, AlgorithmMode.AES,
                        AppConfig.Pin.MKSK_AES_INDEX_MK, AppConfig.Pin.MKSK_AES_INDEX_WK_MAC,
                        macKeyAES, macKcv);
                msg = (result ? context.getString(R.string.msg_load_mac_wk_succ) :
                        context.getString(R.string.msg_load_mac_wk_failed));
                showMessage(msg + "\r\n", MessageTag.DATA);

                byte[] trackKeyAES = ISOUtils.hex2byte("06C8D45B628EAEA8A30C75579F321211");
                //PLAIN KEY:88888888888888888888888888888888
                byte[] trackKcv = ISOUtils.hex2byte("8B4217FEA6");
                result = externalPinInput.loadWorkingKey(WorkingKeyType.TRACK, AlgorithmMode.AES,
                        AppConfig.Pin.MKSK_AES_INDEX_MK, AppConfig.Pin.MKSK_AES_INDEX_WK_TRACK,
                        trackKeyAES, trackKcv);
                msg = (result ? context.getString(R.string.msg_load_track_wk_succ) :
                        context.getString(R.string.msg_load_track_wk_failed));
                showMessage(msg + "\r\n", MessageTag.DATA);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_ext_pininput_load_working_key_excetption) + e + "\r\n", MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_cal_mac, functionid = INDEX_EXTERNALPIN_CAL_MAC)
    private void externalPinpadCalMac() {
        try {
            // String data =
            // "12345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012";

            showMessage(context.getString(R.string.msg_ext_pininput_start_calculate_mac) + "\r\n"
                    , MessageTag.TIP);
            MacResult calacMac = null;
            DialogUtils.createCustomDialog(context, context.getString(R.string.msg_cal_mac), null
                    , R.layout.dialog_caclmac, new DialogUtils.CustomDialogCallback() {
                @Override
                public void onResult(int id, View dialogView) {
                    try {
                        if (id == -1) {//cancel
                            return;
                        }
                        KeyManagement keyManagement = KeyManagement.MKSK;
                        if (AppConfig.DUKPT_DES.equals(AppConfig.KEY_SYS_ALG)) {
                            keyManagement = KeyManagement.DUKPT;
                            indexWKMac = dukptIndex;
                        }
                        EditText value =
                                (EditText) dialogView.findViewById(R.id.edit_caclmac_value);
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
                        byte[] output = externalPinInput.calcMac(keyManagement, macAlgorithm,
                                indexMK, indexWKMac, input, null).getMac();
                        showMessage(context.getString(R.string.msg_enter_value) + string + "\r\n"
                                , MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_mac_algorithm) + macAlgorithm + "\r\n", MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_mac_cal_result) + (output == null ? null : ISOUtils.hexString(output)), MessageTag.DATA);
                    } catch (DeviceInvokeException e) {
                        showMessage(context.getString(R.string.msg_error) + e, MessageTag.ERROR);
                    } catch (UnsupportedEncodingException e) {
                        showMessage(context.getString(R.string.msg_enter_value_error) + "\r\n",
                                MessageTag.ERROR);
                    } catch (Exception e) {
                        showMessage(e.getMessage(), MessageTag.ERROR);
                    }
                }
            });
        } catch (Exception e) {
            showMessage(context.getString(R.string.msg_ext_pininput_calculate_mac_exception) + e + "\r\n", MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_encry, functionid = INDEX_EXTERNALPIN_ENCRY)
    private void externalPinpadEncry() {
        try {
            showMessage(context.getString(R.string.msg_ext_pininput_start_encrypt_data) + "\r\n",
                    MessageTag.TIP);
            CipherResult encryptData = null;
            final String encry = "1234567812345678adcbadcbadcbadcb";
            //final String encry =
            // "12345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012";
            DialogUtils.createCustomDialog(context, R.string.common_encrypt, null,
                    R.layout.dialog_encryption, new DialogUtils.CustomDialogCallback2() {
                @Override
                public void onInit(View view) {
                    EditText value = view.findViewById(R.id.edit_encryption_value);
                    // value.setText("1234567812345678adcbadcbadcbadcb");
                    value.setText(encry);
                }

                @Override
                public void onResult(int id, View dialogView) {
                    EditText value = (EditText) dialogView.findViewById(R.id.edit_encryption_value);
                    AlgorithmMode algorithmMode = AlgorithmMode.DES;
                    CipherMode cipherMode = CipherMode.ECB;
                    CipherExtParams cipherExtParams = new CipherExtParams();
                    try {
                        KeyManagement keyManagement = KeyManagement.MKSK;
                        byte[] input = ISOUtils.hex2byte(value.getText().toString());
                        RadioGroup group1 = dialogView.findViewById(R.id.radioGroup_encrypt_type1);
                        byte[] cbciv = null;
                        if (R.id.radio_CBC == group1.getCheckedRadioButtonId()) {
                            cipherMode = CipherMode.CBC;
                            if (AppConfig.MKSK_DES.equals(AppConfig.KEY_SYS_ALG) || AppConfig.DUKPT_DES.equals(AppConfig.KEY_SYS_ALG)) {
                                cbciv = new byte[]{0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01};
                            } else if (AppConfig.MKSK_SM4.equals(AppConfig.KEY_SYS_ALG) || AppConfig.MKSK_AES.equals(AppConfig.KEY_SYS_ALG)) {
                                cbciv = new byte[]{0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01
                                        , 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01};
                            }
                        } else if (R.id.radio_ECB == group1.getCheckedRadioButtonId()) {
                            cipherMode = CipherMode.ECB;
                        }
                        if (AppConfig.MKSK_DES.equals(AppConfig.KEY_SYS_ALG)) {
                            algorithmMode = AlgorithmMode.DES;
                            indexWKTrack = AppConfig.Pin.MKSK_DES_INDEX_WK_TRACK;
                        } else if (AppConfig.MKSK_SM4.equals(AppConfig.KEY_SYS_ALG)) {
                            algorithmMode = AlgorithmMode.SM4;
                            indexWKTrack = AppConfig.Pin.MKSK_SM4_INDEX_WK_TRACK;
                        } else if (AppConfig.MKSK_AES.equals(AppConfig.KEY_SYS_ALG)) {
                            algorithmMode = AlgorithmMode.AES;
                            indexWKTrack = AppConfig.Pin.MKSK_AES_INDEX_WK_TRACK;
                        } else if (AppConfig.DUKPT_DES.equals(AppConfig.KEY_SYS_ALG)) {
                            algorithmMode = AlgorithmMode.DES;
                            keyManagement = KeyManagement.DUKPT;
                            indexWKTrack = dukptIndex;
                        }
                        cipherExtParams.setCbcInit(cbciv);
                        CipherResult cipherResult = externalPinInput.encrypt(keyManagement,
                                algorithmMode, cipherMode, indexMK, indexWKTrack, input,
                                cipherExtParams);

                        showMessage(context.getString(R.string.msg_encrypt_data) + value.getText().toString() + "\r\n", MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_encrypt_key) + AppConfig.KEY_SYS_ALG + "\r\n", MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_encrypt_mode) + algorithmMode,
                                MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_encrypt_result) + (cipherResult.getData() == null ? null : ISOUtils.hexString(cipherResult.getData())) + "\r\n", MessageTag.DATA);
                        AppConfig.Pin.encryptResult = cipherResult.getData();
                    } catch (Exception e) {
                        e.printStackTrace();
                        showMessage(context.getString(R.string.msg_encrypt_ex) + e,
                                MessageTag.ERROR);
                        showMessage(context.getString(R.string.msg_encrypt_data) + value.getText().toString(), MessageTag.ERROR);
                        showMessage(context.getString(R.string.msg_encrypt_key) + AppConfig.KEY_SYS_ALG, MessageTag.ERROR);
                        showMessage(context.getString(R.string.msg_encrypt_mode) + algorithmMode,
                                MessageTag.ERROR);

                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_ext_pininput_encrypt_exception) + e + "\r" +
                    "\n", MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_decry, functionid = INDEX_EXTERNALPIN_DECRY)
    private void externalPinpadDecry() {
        try {
            CipherResult decryptData = null;
            DialogUtils.createCustomDialog(context, R.string.common_decrypt, null,
                    R.layout.dialog_encryption, new DialogUtils.CustomDialogCallback2() {
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
                    KeyManagement keyManagement = KeyManagement.MKSK;
                    try {
                        byte[] input = ISOUtils.hex2byte(value.getText().toString());
                        RadioGroup group1 = dialogView.findViewById(R.id.radioGroup_encrypt_type1);
                        byte[] cbciv = null;
                        if (R.id.radio_CBC == group1.getCheckedRadioButtonId()) {
                            cipherMode = CipherMode.CBC;
                            if (AppConfig.MKSK_DES.equals(AppConfig.KEY_SYS_ALG) || AppConfig.DUKPT_DES.equals(AppConfig.KEY_SYS_ALG)) {
                                cbciv = new byte[]{0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01};
                            } else if (AppConfig.MKSK_SM4.equals(AppConfig.KEY_SYS_ALG) || AppConfig.MKSK_AES.equals(AppConfig.KEY_SYS_ALG)) {
                                cbciv = new byte[]{0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01
                                        , 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01};
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
                        } else if (AppConfig.DUKPT_DES.equals(AppConfig.KEY_SYS_ALG)) {
                            algorithmMode = AlgorithmMode.DES;
                            keyManagement = KeyManagement.DUKPT;
                            indexWKTrack = dukptIndex;
                        }
                        cipherExtParams.setCbcInit(cbciv);
                        CipherResult cipherResult = externalPinInput.decrypt(keyManagement,
                                algorithmMode, cipherMode, indexMK, indexWKTrack, input,
                                cipherExtParams);

                        showMessage(context.getString(R.string.msg_decrypt_data) + value.getText().toString(), MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_decrypt_key) + AppConfig.KEY_SYS_ALG, MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_decrypt_mode) + algorithmMode,
                                MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_decrypt_result) + (cipherResult.getData() == null ? null : ISOUtils.hexString(cipherResult.getData())), MessageTag.DATA);
                        showMessage(context.getString(R.string.msg_check_hint), MessageTag.TIP);
                    } catch (Exception e) {
                        e.printStackTrace();
                        showMessage(context.getString(R.string.msg_decrypt_ex) + e.getMessage(),
                                MessageTag.ERROR);
                        showMessage(context.getString(R.string.msg_decrypt_data) + value.getText().toString(), MessageTag.ERROR);
                        showMessage(context.getString(R.string.msg_decrypt_key) + AppConfig.KEY_SYS_ALG, MessageTag.ERROR);
                        showMessage(context.getString(R.string.msg_decrypt_mode) + algorithmMode,
                                MessageTag.ERROR);
                    }
                }
            });
        } catch (Exception e) {
            showMessage(context.getString(R.string.msg_ext_pininput_decrypt_exception) + e + "\r" +
                    "\n", MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_pininput, functionid = INDEX_EXTERNALPIN_PININPUT)
    private void startInputPin() {
        try {
            showMessage(context.getString(R.string.msg_ext_pininput_start_input_password),
                    MessageTag.NORMAL);
            KeyManagement keyManagement = KeyManagement.MKSK;
            AlgorithmMode algorithmMode = AlgorithmMode.DES;
            int mkIndex = AppConfig.Pin.MKSK_SM4_INDEX_MK;
            int pinkeyIndex = AppConfig.Pin.MKSK_SM4_INDEX_WK_PIN;

            if (AppConfig.MKSK_SM4.equals(AppConfig.KEY_SYS_ALG)) {
                algorithmMode = AlgorithmMode.SM4;
                keyManagement = KeyManagement.MKSK;
                mkIndex = AppConfig.Pin.MKSK_SM4_INDEX_MK;
                pinkeyIndex = AppConfig.Pin.MKSK_SM4_INDEX_WK_PIN;
            } else if (AppConfig.MKSK_DES.equals(AppConfig.KEY_SYS_ALG)) {
                algorithmMode = AlgorithmMode.DES;
                keyManagement = KeyManagement.MKSK;
                mkIndex = AppConfig.Pin.MKSK_DES_INDEX_MK;
                pinkeyIndex = AppConfig.Pin.MKSK_DES_INDEX_WK_PIN;
            } else if (AppConfig.DUKPT_DES.equals(AppConfig.KEY_SYS_ALG)) {
                algorithmMode = AlgorithmMode.DES;
                keyManagement = KeyManagement.DUKPT;
                mkIndex = AppConfig.Pin.DUKPT_DES_INDEX;
                pinkeyIndex = AppConfig.Pin.DUKPT_DES_INDEX;
            }
            externalPinInput.ksnIncrease(pinkeyIndex);

            String accNo = "6225760008219599";
            PinpadExtParams params = new PinpadExtParams();
            params.setFirstLineMessage("test 1");
            params.setSecondLineMessage("test 2");
            params.setThirdLineMessage("test 3");
            params.setFourthLineMessage("test 4");
            params.setKeyPress(true);
            externalPinInput.startExternalPinInput(keyManagement, algorithmMode, mkIndex,
                    pinkeyIndex, accNo, 60, new PinInputListener() {

                @Override
                public void onKeyPress() {
                    showMessage("Click  key", MessageTag.NORMAL);
                }

                @Override
                public void onBackspace() {
                    showMessage("Click backspace key", MessageTag.NORMAL);
                }

                @Override
                public void onCancel() {
                    showMessage(context.getString(R.string.msg_ext_pininput_cancel), MessageTag.NORMAL);
                }

                @Override
                public void onFinish(int pinblockLen, byte[] pinblock, byte[] ksn) {
                    showMessage("pinblockLen:" + pinblockLen);
                    if (pinblockLen == 0) {
                        showMessage(context.getString(R.string.msg_ext_pininput_confirm), MessageTag.NORMAL);
                    } else {
                        showMessage(context.getString(R.string.msg_ext_pininput_confirm_result) + (pinblock == null ? "null" : ISOUtils.hexString(pinblock)), MessageTag.NORMAL);
                        showMessage("ksn:" + (ksn == null ? null : ISOUtils.hexString(ksn)));
                    }
                }

                @Override
                public void onTimeout() {
                    showMessage(context.getString(R.string.msg_ext_pininput_input_password_exception_code) + "time out", MessageTag.ERROR);
                }

                @Override
                public void onError(int errorCode, String message) {
                    showMessage(context.getString(R.string.msg_ext_pininput_input_password_exception_code) + message, MessageTag.ERROR);
                }

            }, params);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_ext_pininput_input_password_exception) + e.getMessage(), MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_canle_pininput, functionid =
            INDEX_EXTERNALPIN_CANCEL)
    private void cancelPinInput() {
        try {
            BaseFragment.setFunRunning(false);
            externalPinInput.cancelPinInput();
            showMessage(context.getString(R.string.msg_ext_pininput_cancel_pininput_success) +
                    "\r\n", MessageTag.NORMAL);
        } catch (Exception e) {
            showMessage(context.getString(R.string.msg_ext_pininput_cancel_pininput_exception) + e + "\r\n", MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_external_plain_pin, functionid =
            INDEX_EXTERNALPIN_PLAIN_PIN)
    private void startInputOfflinePin() {
        try {
            showMessage(context.getString(R.string.msg_ext_pininput_start_input_plain_password),
                    MessageTag.NORMAL);
            PinpadExtParams pinpadExtParams = new PinpadExtParams();

            pinpadExtParams.setFirstLineMessage("Amount:10");
            pinpadExtParams.setSecondLineMessage("Input offline pin");
            pinpadExtParams.setThirdLineMessage(null);
            pinpadExtParams.setFourthLineMessage("Input offline pin");

            pinpadExtParams.setCardInPinpad(true);
            externalPinInput.startOfflinePinInput(AppConfig.Pin.DUKPT_DES_INDEX,
                    AlgorithmMode.DES, 30, null, "eeeeee".getBytes(),

                    new PinInputListener() {
                        @Override
                        public void onKeyPress() {
                            showMessage("Click  key", MessageTag.NORMAL);

                        }

                        @Override
                        public void onBackspace() {
                            showMessage("Click backspace key", MessageTag.NORMAL);

                        }

                        @Override
                        public void onCancel() {
                            showMessage(context.getString(R.string.msg_ext_pininput_cancel),
                                    MessageTag.NORMAL);

                        }

                        @Override
                        public void onFinish(int pinblockLen, byte[] pinblock, byte[] ksn) {
                            showMessage("pinblockLen:" + pinblockLen);
                            if (pinblockLen == 0) {
                                showMessage(context.getString(R.string.msg_ext_pininput_confirm),
                                        MessageTag.NORMAL);
                            } else {
                                showMessage(context.getString(R.string.msg_ext_pininput_input_plain_password_success) + (pinblock == null ? "null" : new String(pinblock)), MessageTag.NORMAL);
                                showMessage("ksn:" + (ksn == null ? null :
                                        ISOUtils.hexString(ksn)));
                            }
                        }

                        @Override
                        public void onTimeout() {
                            showMessage(context.getString(R.string.msg_ext_pininput_input_password_exception_code) + "time out", MessageTag.ERROR);

                        }

                        @Override
                        public void onError(int errorCode, String message) {
                            showMessage(context.getString(R.string.msg_ext_pininput_input_plain_password_error_code) + message, MessageTag.ERROR);

                        }

                    }, pinpadExtParams);
        } catch (Exception e) {
            showMessage(context.getString(R.string.msg_ext_pininput_input_plain_password_exception) + e, MessageTag.ERROR);
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_external_sign, functionid = INDEX_EXTERNALPIN_SIGN)
    private void externalPinpadSign() {
        try {
            showMessage(context.getString(R.string.msg_ext_pininput_sign) + "\r\n", MessageTag.TIP);
            boolean result = signatureModule.init(null);
            if (!result) {
                showMessage(context.getString(R.string.msg_ext_pininput_init_failed),
                        MessageTag.ERROR);
                return;
            }
            SignatureExtParams params = new SignatureExtParams();
            params.setBordTimeout(30);
            params.setReSignTimes(5);
            params.setWhiteBackground(true);
            params.setBackLight(true);
            params.setSaveSign(false);
            result = signatureModule.setSignatureParams(params);
            if (!result) {
                showMessage(context.getString(R.string.msg_ext_pininput_setparams_failed),
                        MessageTag.ERROR);
                return;
            }
            byte[] data = signatureModule.doSign("1A2B3C4D");
            showMessage(context.getString(R.string.msg_ext_pininput_sign_result) + (data == null
                    ? "null" : ISOUtils.hexString(data)) + "\r\n", MessageTag.TIP);
            if (data != null) {
//                File file = new File("/sdcard/ddd.jpeg");
//                file.delete();
//                file.createNewFile();
//                FileOutputStream fos = new FileOutputStream(file);
                Bitmap bmp = BitmapFactory.decodeByteArray(data, 3, data.length - 3);
//                if (null != bmp)
//                    showImage(bmp);
//                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                showSignDialog(bmp);
            }
            showMessage(context.getString(R.string.msg_ext_pininput_sign_end) + "\r\n",
                    MessageTag.DATA);


        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_ext_pininput_sign_exception) + "\r\n",
                    MessageTag.ERROR);
            showMessage(e.getMessage() + "\r\n", MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_external_show_message, functionid =
            INDEX_EXTERNALPIN_LCD_DISPLAY)
    private void lcdDisplay() {
        try {
            showMessage(context.getString(R.string.externalpin_show_line1) + context.getString(R.string.externalpin_show_line2), MessageTag.TIP);
            List<byte[]> data = new ArrayList<byte[]>();
            if (!isOverseas) {
                data.add(new byte[]{(byte) 0x8B, (byte) 0x86, (byte) 0x3A, (byte) 0x31,
                        (byte) 0x30});
                data.add(new byte[]{(byte) 0x80, (byte) 0x81, (byte) 0x82, (byte) 0x83,
                        (byte) 0x84});
            } else {
                data.add(new byte[]{(byte) 0x52, (byte) 0x45, (byte) 0x41, (byte) 0x44,
                        (byte) 0x59});
                data.add(null);
                data.add(new byte[]{(byte) 0x52, (byte) 0x45, (byte) 0x41, (byte) 0x44,
                        (byte) 0x59, (byte) 0x52, (byte) 0x45, (byte) 0x41, (byte) 0x44,
                        (byte) 0x59});

            }

            boolean result = externalPinInput.setDisplayDirection(DisplayDirection.CENTER);
            showMessage("set display center:" + result);
            boolean rslt1 = externalPinInput.showMessage(data);
            showMessage(context.getString(R.string.externalpin_show_rslt) + rslt1, MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.externalpin_show_error) + e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_external_pin_clear, functionid =
            INDEX_EXTERNALPIN_CLEAR_LCD)
    private void clearLCD() {
        try {
            showMessage(context.getString(R.string.externalpin_clear), MessageTag.TIP);
            boolean rslt = externalPinInput.clearScreen();
            showMessage(context.getString(R.string.externalpin_clear_rslt) + rslt, MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.externalpin_clear_error), MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_external_pin_get_info, functionid =
            INDEX_EXTERNALPIN_GET_INFO)
    private void getInfo() {
        try {
            String version = externalPinInput.getInfo("VERSION");
            String sn = externalPinInput.getInfo("SN");
            String baurate = externalPinInput.getInfo("BAUDRATE");
            String portype = externalPinInput.getInfo("PORTTYPE");
            TusnData tusnData = externalPinInput.getTusnData("123456");
            showMessage("version:" + version, MessageTag.DATA);
            showMessage("sn:" + sn, MessageTag.DATA);
            showMessage("baurate:" + baurate, MessageTag.DATA);
            showMessage("portype:" + portype, MessageTag.DATA);
            showMessage("TUSN=" + tusnData.getSn(), MessageTag.DATA);
            showMessage("EncryptData=" + tusnData.getEncryptedData(), MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.tv_external_pin_get_info_error),
                    MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.externalpin_loadrsa, functionid =
            INDEX_EXTERNALPIN_LOAD_RSA)
    private void loadRSA() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.externalpin_loadrsa),
                null, R.layout.dialog_load_rsa, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                try {
                    //Public key
                    // module
                    // ：C1ED9B344A294EC5C75DB7B2D18C04DCC8FC0CD06350463C698C4232F289D24E0EC0CD05BC6E59B0001F2EA77C7793CDBC755BD475B159CF00FF6A05ED21444AFAB612972C9DE0EFF7182973839D2C7828587FA3B85359FEB5F2F8717052467D412F1F4E74A986FFFF094E97700D192623840AAEBF6C3F34273B7B900F1585818CB1696243E81045730FB3933C3575F11401C6B3A8799F13D79740E109A91957604D47658F7203D33C6A2E23302AB9679E1EFC8CFF4332C707DBFEE0BFD30BB6A625E52F9BE8BEA1195F572967ABC426ECA8C1B62190E75D1322722A0F3B4DED5A2FD4BD512F2F452835C24D8015CF4BCF94291D88BFD5C5CC81DE124EA1BC5D
                    //Public key index：010001
                    //Public keys encrypt and decrypt data：
                    //Private key
                    // module
                    // ：C1ED9B344A294EC5C75DB7B2D18C04DCC8FC0CD06350463C698C4232F289D24E0EC0CD05BC6E59B0001F2EA77C7793CDBC755BD475B159CF00FF6A05ED21444AFAB612972C9DE0EFF7182973839D2C7828587FA3B85359FEB5F2F8717052467D412F1F4E74A986FFFF094E97700D192623840AAEBF6C3F34273B7B900F1585818CB1696243E81045730FB3933C3575F11401C6B3A8799F13D79740E109A91957604D47658F7203D33C6A2E23302AB9679E1EFC8CFF4332C707DBFEE0BFD30BB6A625E52F9BE8BEA1195F572967ABC426ECA8C1B62190E75D1322722A0F3B4DED5A2FD4BD512F2F452835C24D8015CF4BCF94291D88BFD5C5CC81DE124EA1BC5D
                    //Private key
                    // index
                    // ：8BA6696FBC4006E3D1EFA10B7A6ED44991CA4008089C9417890261E1825AD14138CA8A59A919E62821CE52B075A73E9E972A0418F92FDFB67BC77238164D307AB6144B4AE5EC43414AB4F194A7A0959769A661342AD68B262B2C6ED071CC2DCAA11827F93D759F2BD622839FF626D8876867FBB15F53BA27FA6091586A245B606F44831599FA60C7B4ABD78E8F06562354F151E174997AAD2A9338058FD5E009ECB5A818F86A9B96CD0859DA61E165921A6EC774220BAC9CB9AB6B0868D2EEF500B659D191DB97DA4663B4E5386BA98C423D46FA7ABA53CF91D414EA21D784D3CC9A6078C82D1D3700038B77EBE7B219A02E71E361E2ECE802CC4D377A929419
                    //Private keys encrypt and decrypt
                    // data：00000304N9NL1012120730899938999163757576338
                    EditText edtIndex = dialogView.findViewById(R.id.edt_index);
                    EditText keyLen = dialogView.findViewById(R.id.edt_keylen);
                    EditText module = dialogView.findViewById(R.id.edt_module);
                    EditText exponent = dialogView.findViewById(R.id.edt_exponent);
                    int index = Integer.parseInt(edtIndex.getText().toString());
                    int keyLength = Integer.parseInt(keyLen.getText().toString());
                    byte[] modeluData = ISOUtils.hex2byte(module.getText().toString());
                    byte[] exponentData = ISOUtils.hex2byte(exponent.getText().toString());
                    boolean isSucess = externalPinInput.loadRSA(index, keyLength, modeluData,
                            exponentData);
                    showMessage("load rsa result:" + isSucess);
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage("exception:" + e);
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.externalpin_rsa_encry, functionid =
            INDEX_EXTERNALPIN_ENCRY_DECRY_RSA)
    private void rsaEncryDecry() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.externalpin_rsa_encry)
                , null, R.layout.dialog_rsa_encry_decry, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                try {
                    if (id < 0) {
                        return;
                    }
//                    Source data D needs to be converted into Encryption Block (EB) during RSA
//                    calculation.  Where pkCS1PADDING V1.5 is installed in fill mode in the
//                    following way
//                   //EB = 00+ BT+PS +  00 + fillsha256 + D
                    //BT:Indicates the type of the data block;1 byte. X '01 indicates the private
                    // key operation, and X' 02 indicates the public key operation.
                    //PS：the fill bit is composed of k-3-d. K represents the length of the key.
                    // If we use the 1024-bit RSA key, the length is 1024/8=128.  D indicates the
                    // length of plaintext data D in bytes
                    //PS:Represents the filled data. When BT is X '01, the filled data is
                    // required to be X 'ff; when BT is X' 02, the filled data is random data
                    // other than X '00, and PS must be filled with at least 8 bytes.  D:
                    // Indicates a data block, which can be a signed data block or TDEA key data
                    EditText data = dialogView.findViewById(R.id.data);
                    String rsaData = data.getText().toString();

                    showMessage("data encry/decry result:" + getSHA256(rsaData));
                    byte[] D = ISOUtils.hex2byte(getSHA256(rsaData));
//                    1024/8-3-16
                    byte[] head = new byte[]{0x00, 0x01};  //00 BT
                    byte[] ps = new byte[256 - 1 - 1 - 1 - 19 - D.length];   //PS
                    Arrays.fill(ps, (byte) 0xFF);

                    byte[] fillSha256 = new byte[]{0x30, 0x31, 0x30, 0x0d, 0x06, 0x09, 0x60,
                            (byte) 0x86, 0x48, 0x01, 0x65, 0x03, 0x04, 0x02, 0x01, 0x05, 0x00,
                            0x04, 0x20};
                    byte[] eb = new byte[256];
                    System.arraycopy(head, 0, eb, 0, head.length);
                    System.arraycopy(ps, 0, eb, head.length, ps.length);
                    //0x00
                    //for sha256
                    System.arraycopy(fillSha256, 0, eb, head.length + ps.length + 1,
                            fillSha256.length);
                    System.arraycopy(D, 0, eb, head.length + ps.length + fillSha256.length + 1,
                            D.length);
                    showMessage("EB data:" + ISOUtils.hexString(eb));
                    Log.d("EB data", "EB data:" + ISOUtils.hexString(eb));
                    EditText index = dialogView.findViewById(R.id.edt_rsa_key_index);
                    int keyIndex = Integer.parseInt(index.getText().toString());
                    byte[] dataToEncryDecry = eb;
                    byte[] resultData = externalPinInput.rsaEncryDecry(keyIndex, dataToEncryDecry);
                    showMessage("data encry/decry result:" + (resultData == null ? null :
                            ISOUtils.hexString(resultData)));
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage("exception:" + e);
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.externalpin_fetch_plain_data, functionid = 16)
    private void startPlainPinInput() {
        try {
            externalPinInput.startPlainPinInput(30, new PinInputListener() {
                @Override
                public void onKeyPress() {
                    showMessage("----onKeyPress----");
                }

                @Override
                public void onBackspace() {
                    showMessage("----onBackspace----");

                }

                @Override
                public void onCancel() {
                    showMessage("----onCancel----", MessageTag.ERROR);
                }

                @Override
                public void onFinish(int pinblockLen, byte[] pinblock, byte[] ksn) {
                    showMessage("----onFinish----pinblockLen:" + pinblockLen + ";pinblock:" + (pinblock == null ? null : ISOUtils.hexString(pinblock)));

                }

                @Override
                public void onTimeout() {
                    showMessage("----onTimeout----", MessageTag.ERROR);
                }

                @Override
                public void onError(int errorCode, String message) {
                    showMessage("----onError----errorCode:" + errorCode + ";message:" + message,
                            MessageTag.ERROR);

                }
            }, null);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Exception:" + e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_check_key, functionid = 17)
    private void checkKey() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.msg_check_key_exist),
                null, R.layout.dialog_checkkey, new DialogUtils.CustomDialogCallback() {
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

                    boolean result = externalPinInput.checkKeyIsExist(keyType, algorithmMode,
                            keyIndex, null);
                    showMessage(context.getString(R.string.msg_key_type) + keyType,
                            MessageTag.NORMAL);
                    showMessage(context.getString(R.string.msg_key_index) + keyIndex,
                            MessageTag.NORMAL);
                    showMessage(context.getString(R.string.msg_is_exist) + result,
                            MessageTag.NORMAL);
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(context.getString(R.string.msg_check_key_ex) + e, MessageTag.ERROR);
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_load_ipek, functionid = 18)
    private void loadIPEK() {
        try {
            String KSN = "32303030303030200000";//When loading KSN, the last 21bit will be zeroed
            // out,32303030303030200001 is the first ksn
            String ipek = "9B4540010D6034EB79A05ACB6BD519BA";//明文:2F8E26EF7E61558D27367721654C26C5
            boolean result = externalPinInput.loadIPEK(LoadKeyMode.PLAIN,
                    AppConfig.Pin.DUKPT_DES_INDEX, ISOUtils.hex2byte(KSN), ISOUtils.hex2byte(
                            "2F8E26EF7E61558D27367721654C26C5"), null);
            String msg = (result ? context.getString(R.string.msg_load_ipek_result) :
                    context.getString(R.string.msg_load_ipek_failed));
            showMessage(msg + "\r\n", MessageTag.DATA);

//            boolean result1 = externalPinInput.loadIPEK(LoadKeyMode.PLAIN, AppConfig.Pin
//            .DUKPT_DES_INDEX, ISOUtils.hex2byte(KSN), ISOUtils.hex2byte
//            ("2F8E26EF7E61558D27367721654C26C5"), null);
//            String msg1 = (result1 ? context.getString(R.string.msg_load_ipek_result) : context
//            .getString(R.string.msg_load_ipek_failed));
//            showMessage(msg1 + "\r\n", MessageTag.DATA);
//
//            LoadDuktpExtParams loadDuktpExtParams = new LoadDuktpExtParams();
//            loadDuktpExtParams.setKekIndex(AppConfig.Pin.DUKPT_DES_INDEX);
// IPEK明文：11111111111111111111111111111111
//            boolean result2 = externalPinInput.loadIPEK(LoadKeyMode.CUSTOM_ENCRYPT, 2, ISOUtils
//            .hex2byte(KSN), ISOUtils.hex2byte("45459F8BD20F030B45459F8BD20F030B"),
//            loadDuktpExtParams);
//            String msg2 = (result2 ? context.getString(R.string.msg_load_ipek_result) : context
//            .getString(R.string.msg_load_ipek_failed));
//            showMessage(msg2 + "\r\n", MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Exception:" + e, MessageTag.ERROR);
        }

    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_get_ksn, functionid = 19)
    private void getDukptKsn() {
        try {
            showMessage(context.getString(R.string.msg_get_dukpt_ksn), MessageTag.TIP);
            byte[] ksn = externalPinInput.getDukptKsn(dukptIndex);
            showMessage("ksn:" + (ksn == null ? null : ISOUtils.hexString(ksn)), MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_get_dukpt_ksn_faild) + e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_increase_ksn, functionid = 20)
    private void ksnIncrease() {
        try {
            showMessage(context.getString(R.string.msg_increase_dukpt_ksn), MessageTag.TIP);
            boolean result = externalPinInput.ksnIncrease(dukptIndex);
            showMessage(context.getString(R.string.msg_increase_dukpt_ksn_resut) + result,
                    MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_increase_dukpt_ksn_faild) + e,
                    MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_pin_delete_key, functionid = 21)
    private void delKey() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_pin_delete_key),
                null, R.layout.dialog_del_key, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                try {
                    if (id == -1) {//cancel
                        return;
                    }
                    RadioGroup group1 = dialogView.findViewById(R.id.radioGroup_keytype_del);
                    EditText value = dialogView.findViewById(R.id.edit_keyindex_del);
                    int keyIndex = Integer.parseInt(value.getText().toString());
                    KeyType keyType = null;
                    AlgorithmMode algorithmMode = AlgorithmMode.DES;
                    if (AppConfig.MKSK_DES.equals(AppConfig.KEY_SYS_ALG)) {
                        algorithmMode = AlgorithmMode.DES;
                    } else if (AppConfig.MKSK_SM4.equals(AppConfig.KEY_SYS_ALG)) {
                        algorithmMode = AlgorithmMode.SM4;
                    } else if (AppConfig.MKSK_AES.equals(AppConfig.KEY_SYS_ALG)) {
                        algorithmMode = AlgorithmMode.AES;
                    }
                    if (R.id.radio_all == group1.getCheckedRadioButtonId()) {
                        keyType = null;
                    } else if (R.id.radio_mainkey_del == group1.getCheckedRadioButtonId()) {
                        keyType = KeyType.MASTER_KEY;
                    } else if (R.id.radio_pinkey_del == group1.getCheckedRadioButtonId()) {
                        keyType = KeyType.PIN_KEY;
                    } else if (R.id.radio_trackkey_del == group1.getCheckedRadioButtonId()) {
                        keyType = KeyType.TRACK_KEY;
                    } else if (R.id.radio_mackey == group1.getCheckedRadioButtonId()) {
                        keyType = KeyType.MAC_KEY;
                    }

                    boolean result;
                    if (keyType != null) {
                        result = externalPinInput.deleteKey(keyType, algorithmMode, keyIndex);
                    } else {
                        result = externalPinInput.deleteAllKeys();
                    }
                    showMessage(context.getString(R.string.msg_key_type) + keyType,
                            MessageTag.NORMAL);
                    showMessage(context.getString(R.string.msg_key_index) + keyIndex,
                            MessageTag.NORMAL);
                    showMessage("result = " + result, MessageTag.NORMAL);
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(context.getString(R.string.msg_check_key_ex) + e, MessageTag.ERROR);
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_external_show_message, functionid = 22)
    private void lcdDisplay2() {
        try {
            showMessage(context.getString(R.string.externalpin_show_line1) + context.getString(R.string.externalpin_show_line2), MessageTag.TIP);
            // just for version is greater than and equal to V04.00.03.
            List<String> data = new ArrayList<String>();
//            data.add(context.getString(R.string.msg_trans_ext_start_input_password));
            data.add("点阵测试-第1行");
//            data.add("中英文混显123ABC");
//            data.add(context.getString(R.string.externalpin_amount) + "10");
            boolean rslt1 = externalPinInput.showMessage(data, null);

            showMessage(context.getString(R.string.externalpin_show_rslt) + rslt1, MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.externalpin_show_error) + e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_external_backtomain, functionid = 23)
    private void backToMainScreen() {
        try {
            externalPinInput.backToMainScreen();
            showMessage(context.getString(R.string.externalpin_backtomain));
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.externalpin_backtomain_error));
        }
    }

    @MethodGridEntity(btnname = "getTusnData", functionid = 24)
    private void getTusnData() {
        try {
            TusnData tusnData = externalPinInput.getTusnData("123456");
            showMessage("DeviceType: " + tusnData.getDeviceType());
            showMessage("SN: " + tusnData.getSn());
            showMessage("EncryptedData: " + tusnData.getEncryptedData());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnname = "keepScreenOn", functionid = 25)
    private void keepScreenOn() {
        try {
            boolean rs = externalPinInput.setProperty(PropertyKey.BACKLIGHT, "2");
            showMessage("keepScreenOn: " + rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnname = "closeKeepScreenOn", functionid = 26)
    private void closeKeepScreenOn() {
        try {
            boolean rs = externalPinInput.setProperty(PropertyKey.BACKLIGHT, "1");// dim screen
            // after 30 seconds.
            showMessage("closeKeepScreenOn: " + rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getSHA256(String str) {
        MessageDigest messageDigest;
        String encodestr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodestr = ISOUtils.hexString(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodestr;
    }

    @MethodGridEntity(btnname = "updateApp", functionid = 27)
    private void updateApp() {
        try {
            //For P180
            AssetManager assetManager = context.getAssets();
            // ME30S
            String appName = "P180_V08.00.14_20240411145322.NLD";

            showMessage(String.format("Loading app: %s ...", appName));
            InputStream is = assetManager.open(appName);
            int length = is.available();
            byte[] buffer = new byte[length];
            is.read(buffer);
            UpdateFiles updateFiles = new UpdateFiles();
            updateFiles.setApplicationFile(buffer);


            externalPinInput.update(updateFiles, new UpdateListener() {
                @Override
                public void onError(int errorCode, String message) {
                    showMessage("onError: " + message);

                }

                @Override
                public void onFileTransferProgress(int precent) {
                    showMessage("onFileTransferProgress: " + precent+"%");

                }

                @Override
                public void onComplete() {
                    showMessage("onComplete");

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("error"+e.getMessage());

        }
    }

    @MethodGridEntity(btnname = "displayLogo", functionid = 28)
    private void displaylogo() {
        try{
            /**
             * P180
             */
            AssetManager assetManager = context.getAssets();
            InputStream is = assetManager.open("newland.jpg");

            Bitmap bmp = BitmapFactory.decodeStream(is);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            System.out.println("buffer:"+ISOUtils.hexString(baos.toByteArray()));

            DisplayColorImageParams pictureParameter = new DisplayColorImageParams();
            pictureParameter.setxCoordinate(0);
            pictureParameter.setyCoordinate(0);
            pictureParameter.setWidth(bmp.getWidth());
            pictureParameter.setHeight(bmp.getHeight());
            boolean result= externalPinInput.displayColorImage(baos.toByteArray(),pictureParameter);
            showMessage("displaylogo: "+result);

            /**
             * SP130
             * The image is required to handle for SP130.
             * The image should be removed the head and tail data.
             * reference to the ico_nc_newland.bmp.
             */
//            AssetManager assetManager = context.getAssets();
//            InputStream is = assetManager.open("ico_nc_newland.bmp");
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            byte[] buffer = new byte[1024];
//            int bytesRead;
//            while ((bytesRead = is.read(buffer)) != -1) {
//                baos.write(buffer, 0, bytesRead);
//            }
//            System.out.println("buffer:"+ISOUtils.hexString(baos.toByteArray()));
//
//            DisplayColorImageParams pictureParameter = new DisplayColorImageParams();
//            pictureParameter.setxCoordinate(0);
//            pictureParameter.setyCoordinate(0);
//            pictureParameter.setWidth(320); // actual image width
//            pictureParameter.setHeight(240); // actual image height
//            boolean result= externalPinInput.displayColorImage(baos.toByteArray(),pictureParameter);
//            showMessage("displaylogo: "+result);

        }catch (Exception e){
            e.printStackTrace();
            showMessage("error"+e.getMessage());
        }

    }

    @MethodGridEntity(btnname = "NoJump2MainScreen", functionid = 29)
    private void noJump2MainScreen() {

        boolean result= externalPinInput.controlPageJump(false);
        showMessage("Don`t Jump to MainScreen: "+result);


    }


    @MethodGridEntity(btnname = "showMenuOption", functionid = 30)
    private void showMenuOption() {
        /**
         * After this command is finished (chose an option), it won’t return to main page, you need to
         * manually use Return Main homepage {@link ExtPinpadModule#backToMainScreen()} method to return. This behaviour is designed for
         * continuous menu option.
         */

        String title = "Trans Menu";
        /**
         * The max size for men option is 12.
         */
        String[] menuOption = new String[]{"1. Sale", "2. Cancel"};
        MenuOptionParams menuOptionParams = null;
        externalPinInput.showMenuOption(title, menuOption, 60, new MenuOptionListener() {
            @Override
            public void onKeyPress(int option) {
                showMessage("[onKeyPress] option:" + option);
            }

            @Override
            public void onTimeout() {
                showMessage("[onTimeout]");
            }

            @Override
            public void onCancel() {
                showMessage("[onCancel]");
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                showMessage("[onError] errorCode:" + errorCode + ", errorMsg:" + errorMsg);

            }

        }, menuOptionParams);

    }

    @MethodGridEntity(btnname = "scan", functionid = 31)
    private void scan() {
        showMessage("scanning");
        ScanParams scanParams = null;
        externalPinInput.scan(60, new ScannerListener() {


            @Override
            public void onTimeout() {
                showMessage("[onTimeout]");
            }

            @Override
            public void onResponse(String[] scanResults) {
                showMessage("[onResponse] result:" + scanResults[0]);
            }

            @Override
            public void onFinish() {
                showMessage("[onFinish]");
            }

            @Override
            public void onCancel() {
                showMessage("[onCancel]");
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                showMessage("[onError] errorCode:" + errorCode + ", errorMsg:" + errorMsg);

            }

        }, scanParams);

    }

    @MethodGridEntity(btnname = "cancel", functionid = 32)
    private void cancel() {
        showMessage("cancel");
        externalPinInput.cancel();

    }

    @MethodGridEntity(btnname = "setFontSize", functionid = 33)
    private void setFontSize() {
        showMessage("setFontSize");
        String[] item = new String[]{"Normal","Small","Large"};
        DialogUtils.createSingleChoiceDialog(context, "setFontSize", item, new DialogUtils.SingleChoiceDialogCallback() {
            @Override
            public void onResult(int id) {
                if (id < 0){
                    return;
                }
                FontSize size = FontSize.NORMAL;
                if (id == 0){
                    size = FontSize.NORMAL;
                } else if(id == 1){
                    size = FontSize.SMALL;
                } else if (id == 2){
                    size = FontSize.LARGE;
                }
                boolean rslt = externalPinInput.setFontSize(size);
                showMessage("setFontSize result:" + rslt);
            }
        });
        externalPinInput.cancel();

    }

    @MethodGridEntity(btnnameid = R.string.tv_external_sign, functionid = 34)
    private void doSign() {
        try {
            showMessage(context.getString(R.string.msg_ext_pininput_sign) + "\r\n", MessageTag.TIP);
            boolean result = signatureModule.init(null);
            if (!result) {
                showMessage(context.getString(R.string.msg_ext_pininput_init_failed),
                        MessageTag.ERROR);
                return;
            }
            DoSignExtParams extParams = new DoSignExtParams();
            signatureModule.doSign("1A2B3C4D", new DoSignListener() {
                @Override
                public void onSuccess(byte[] signData) {
                    if (signData != null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(signData, 0, signData.length);
                        showSignDialog(bitmap);
                    }
                }

                @Override
                public void onCancel() {
                    showMessage("onCancel");
                }

                @Override
                public void onTimeout() {
                    showMessage("onTimeout");
                }

                @Override
                public void onError(int errorCode, String message) {
                    showMessage("onError, errorCode=" + errorCode + ", errorMessage=" + message, MessageTag.ERROR);
                }
            }, extParams);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_ext_pininput_sign_exception) + "\r\n", MessageTag.ERROR);
            showMessage(e.getMessage() + "\r\n", MessageTag.ERROR);
        }
    }

    private void showSignDialog(Bitmap bitmap) {
        ((MainActivity) context).runOnUiThread(() -> {
            Dialog dialog = new Dialog(context);
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_sign, null);
            dialog.setContentView(view);
            ((ImageView) view.findViewById(R.id.iv_sign)).setImageBitmap(bitmap);
            dialog.show();
        });
    }
}