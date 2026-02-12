package com.newland.nsdkdemo.internal.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.crypto.AsymAlgorithmParameters;
import com.newland.nsdk.core.api.common.crypto.AsymEncodingMode;
import com.newland.nsdk.core.api.common.crypto.KCVMode;
import com.newland.nsdk.core.api.common.crypto.MessageDigestType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.keymanager.AsymKeyType;
import com.newland.nsdk.core.api.common.keymanager.AsymKeyUsage;
import com.newland.nsdk.core.api.common.keymanager.AsymmetricKey;
import com.newland.nsdk.core.api.common.keymanager.KeyGenerateMethod;
import com.newland.nsdk.core.api.common.keymanager.KeyInfoID;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.api.common.keymanager.KeyUsage;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.internal.crypto.Crypto;
import com.newland.nsdk.core.api.internal.devicemanager.DeviceManager;
import com.newland.nsdk.core.api.internal.keymanager.KeyManager;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.AppConfig;
import com.newland.nsdkdemo.common.SDKExecutors;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.annotation.MethodGridEntity;
import com.newland.nsdkdemo.common.utils.DialogUtils;
import com.newland.nsdkdemo.common.utils.MessageTag;
import com.newland.nsdkdemo.common.bean.InitiationRequest;
import com.newland.nsdkdemo.common.bean.InitiationResponse;
import com.newland.nsdkdemo.common.bean.KeyRequest;
import com.newland.nsdkdemo.common.bean.KeyResponse;
import com.newland.nsdkdemo.common.bean.ResponseCode;
import com.newland.nsdkdemo.common.bean.VerificationRequest;
import com.newland.nsdkdemo.common.bean.VerificationResponse;
import com.newland.nsdkdemo.common.http.RKIService;
import com.newland.nsdkdemo.common.utils.LocationUtils;
import com.newland.nsdkdemo.internal.utils.RetrofitUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.Context.MODE_PRIVATE;
import static com.newland.nsdkdemo.common.AppConfig.SharedPreferenceConfig.DEVICE_TYPE;
import static com.newland.nsdkdemo.common.AppConfig.SharedPreferenceConfig.IS_DEMO_MODE;
import static com.newland.nsdkdemo.common.AppConfig.SharedPreferenceConfig.KDH_URL;
import static com.newland.nsdkdemo.common.AppConfig.SharedPreferenceConfig.KDH_PORT;
import static com.newland.nsdkdemo.common.AppConfig.SharedPreferenceConfig.SHARE_PREFERENCE;

public class RKIFragment extends InternalBaseFragment {
    private static final String TAG = "RKIFragment";
    private static String RKLA_VERSION = "01";
    // Location info type: 0 – Base station. 1 – GPS
    private static final int LOCTATION_TYPE_BASE_STATION = 0;
    private static final int LOCATION_TYPE_GPS = 1;
    public static int MAX_KEY_NUM = 1;

    private KeyManager keyManager;
    private DeviceManager deviceManager;
    private Crypto crypto;
    private SharedPreferences sharedPreferences;
    public static String baseUrl;
    private Retrofit retrofit;
    private RKIService rkiService;
    private SymmetricKey sk;
    private AsymmetricKey kdhDistributionKey;
    private AsymmetricKey kdhSignKey;
    private AsymmetricKey krdSignKey;
    private String sessionToken;
    /**
     * Key distribution host random number
     */
    private String kdhRandomNumber;
    /**
     * Key Receiving Device random number
     */
    private String krdRandomNumber;

    private int skType;

    private AsymAlgorithmParameters signAndVerifyParameters;

    /**
     * Test KDH:
     * IP: 47.241.70.74
     * Port: 17443
     */
    private String kdhUrl;
    private int kdhPort;

    private boolean isDemoMode;
    private int deviceType;

    public RKIFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_rki_f);
    }

    @Override
    public void initData() {
        keyManager = (KeyManager) moduleManager.getModule(ModuleType.KEY_MANAGER);
        deviceManager = (DeviceManager) moduleManager.getModule(ModuleType.DEVICE_MANAGER);
        crypto = (Crypto) moduleManager.getModule(ModuleType.CRYPTO);
        sharedPreferences = context.getSharedPreferences(SHARE_PREFERENCE, MODE_PRIVATE);
        kdhUrl = sharedPreferences.getString(KDH_URL, AppConfig.RKL_URL);
        kdhPort = sharedPreferences.getInt(KDH_PORT, 17443);
        isDemoMode = sharedPreferences.getBoolean(IS_DEMO_MODE, false);

        try {
            String firmwareType = deviceManager.getDeviceInfo().getFirmwareVer().substring(0,1);
            if ("V".equals(firmwareType)) {
                deviceType = AppConfig.DEVICE_TYPE_PRO;
            } else {
                deviceType = AppConfig.DEVICE_TYPE_DEV;
            }
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, " get device type");
        }

        // Device cert shall be injected by mother POS first.
        krdSignKey = new AsymmetricKey();
        krdSignKey.setKeyID(AppConfig.Keys.RKI_NORMAL_DEVICE_KEY_ID);
        krdSignKey.setKeyUsage(AsymKeyUsage.AUTH);
        krdSignKey.setKeyType(AsymKeyType.RSA);

        // KDH distribution key will be generated after KDH E_CERT is loaded.
        kdhDistributionKey = new AsymmetricKey();
        kdhDistributionKey.setKeyType(AsymKeyType.RSA);
        kdhDistributionKey.setKeyUsage(AsymKeyUsage.KEY_DISTRIBUTION);
        kdhDistributionKey.setKeyID((byte) AppConfig.Keys.ASYM_KEY_DISTRIBUTION_ID);

        // KDH signing key will be generated after KDH S_CERT is loaded.
        kdhSignKey = new AsymmetricKey();
        kdhSignKey.setKeyType(AsymKeyType.RSA);
        kdhSignKey.setKeyUsage(AsymKeyUsage.AUTH);
        kdhSignKey.setKeyID((byte) AppConfig.Keys.ASYM_AUTH_ID);

        // SK will be generated after RKI initiation request.
        sk = new SymmetricKey();
        sk.setKeyID((byte) AppConfig.Keys.RKI_SK_ID);
        sk.setKeyUsage(KeyUsage.TR31_KEK);

        signAndVerifyParameters = new AsymAlgorithmParameters();
        signAndVerifyParameters.setEncodingMode(AsymEncodingMode.PKCS_V15);
        signAndVerifyParameters.setMessageDigestType(MessageDigestType.SHA256);
    }

    @Override
    public Object getModule() {
        return RKIFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.set_rki_settings, functionid = 1)
    public void setKdhAddress() {
        DialogUtils.createCustomDialog(context, R.string.set_rki_settings, null, R.layout.dialog_rki_settings, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                EditText etUrl = view.findViewById(R.id.edit_kdh_url);
                EditText etPort = view.findViewById(R.id.edit_kdh_port);
                RadioButton rbDemoMode = view.findViewById(R.id.rb_demo_mode);
                RadioButton rbNormalMode = view.findViewById(R.id.rb_normal_mode);
                RadioGroup rgRkiMode = view.findViewById(R.id.rg_rki_mode);

                etUrl.setText(kdhUrl);
                etPort.setText(String.valueOf(kdhPort));

                if (deviceType == AppConfig.DEVICE_TYPE_DEV) {
                    rgRkiMode.setVisibility(View.VISIBLE);
                    if (isDemoMode) {
                        rbDemoMode.setChecked(true);
                        rbNormalMode.setChecked(false);
                    } else {
                        rbDemoMode.setChecked(false);
                        rbNormalMode.setChecked(true);
                    }
                } else {
                    rgRkiMode.setVisibility(View.GONE);
                }
            }

            @Override
            public void onResult(int id, View view) {
                EditText etUrl = view.findViewById(R.id.edit_kdh_url);
                EditText etPort = view.findViewById(R.id.edit_kdh_port);
                RadioButton rbDemoMode = view.findViewById(R.id.rb_demo_mode);

                String tempUrl = etUrl.getText().toString().trim();
                String tempPort = etPort.getText().toString().trim();
                if (TextUtils.isEmpty(tempUrl)) {
                    showMessage(String.format("%s: URL %s\r\n", context.getString(R.string.msg_write_illegal), tempUrl), MessageTag.ERROR);
                    return;
                }
                if (!isPortValid(tempPort)) {
                    showMessage(String.format("%s: Port %s\r\n", context.getString(R.string.msg_write_illegal), tempPort), MessageTag.ERROR);
                    return;
                }


                kdhUrl = etUrl.getText().toString();
                kdhPort = Integer.parseInt(etPort.getText().toString());

                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putString(KDH_URL, kdhUrl);
                myEdit.putInt(KDH_PORT, kdhPort);

                if (deviceType == AppConfig.DEVICE_TYPE_DEV) {
                    isDemoMode = rbDemoMode.isChecked();
                    myEdit.putBoolean(IS_DEMO_MODE, isDemoMode);
                }

                myEdit.commit();
                showMessage(String.format("KDH URL %s, port %d, demo mode(%s) is set sucessfully.", kdhUrl, kdhPort, isDemoMode));
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.process_rki, functionid = 2)
    public void processRki(){
        showMessage("1. Initializing RKI...");
        try {
            keyManager.resetCertStatus();
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "reset cert status");
            return;
        }

        byte[] deviceSignCert;
        String sn;
        try {
            if (isDemoMode) {
                sn = "SN0123456789";
                installDefaultDeviceCertKey();
                deviceSignCert = AppConfig.ASYM_CERT;
            } else {
                deviceSignCert = keyManager.getKeyInfo(KeyInfoID.CERTIFICATE, krdSignKey);
                sn = deviceManager.getDeviceInfo().getSN();
            }
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "get device cert and sn");
            return;
        }

        LocationUtils locationUtils = new LocationUtils(context);
        InitiationRequest initiationRequest = new InitiationRequest();
        initiationRequest.setVER(RKLA_VERSION);
        initiationRequest.setSN(sn);
        initiationRequest.setS_CERT(new String(deviceSignCert));
        initiationRequest.setL_TYPE(LOCTATION_TYPE_BASE_STATION);
        initiationRequest.setLOCATION(locationUtils.getLocationInfo());

        // Init rik service here so that the base url is always up to date
        rkiService = RetrofitUtils.getInstance(getBaseUrl()).getRetrofit().create(RKIService.class);

        LogUtils.d(TAG, String.format("RKI init request: %s", initiationRequest.toString()));
        Call<InitiationResponse> initCall = rkiService.initRKI(initiationRequest);
        initCall.enqueue(new Callback<InitiationResponse>() {
            @Override
            public void onResponse(Call<InitiationResponse> call, Response<InitiationResponse> response) {
                final InitiationResponse initiationResponse = response.body();
                LogUtils.d(TAG, String.format("RKI init response: %s", initiationResponse.toString()));
                SDKExecutors.threadStart(new Runnable() {
                    @Override
                    public void run() {
                        handleInitiationResponse(initiationResponse);
                    }
                });
            }

            @Override
            public void onFailure(Call<InitiationResponse> call, Throwable t) {
                t.printStackTrace();
                showMessage(String.format("Failed to init RKI: %s", t.getMessage()), MessageTag.ERROR);
            }
        });
    }

    public void installDefaultDeviceCertKey() throws NSDKException {
        AsymmetricKey deviceCertKey = new AsymmetricKey();
        deviceCertKey.setKeyID((byte) AppConfig.Keys.RKI_DEMO_DEVICE_KEY_ID);
        deviceCertKey.setKeyUsage(AsymKeyUsage.AUTH);
        deviceCertKey.setKeyType(AsymKeyType.RSA);
        deviceCertKey.setKeyData(AppConfig.CERT_PRIVATE_KEY);
        deviceCertKey.setKeyLen(AppConfig.CERT_PRIVATE_KEY.length);

        LogUtils.d(TAG, "Install private key of the cert.");
        keyManager.generateKey(KeyGenerateMethod.CLEAR, null, null, deviceCertKey, AppConfig.ASYM_CERT);
    }

    private void handleInitiationResponse(InitiationResponse initiationResponse) {
        String status = initiationResponse.getSTATUS();
        if (!ResponseCode.SERVER_OK.getValue().equals(status)) {
            showMessage(String.format("Initiation status is not OK: %s", status), MessageTag.ERROR);
            return;
        }

        String eCert = initiationResponse.getE_CERT();
        if (TextUtils.isEmpty(eCert)) {
            showMessage("E_CERT is empty.", MessageTag.ERROR);
            return;
        }
        String sCert = initiationResponse.getS_CERT();
        if (TextUtils.isEmpty(sCert)) {
            showMessage("S_CERT is empty.", MessageTag.ERROR);
            return;
        }

        // Verify E_CERT and load it to PIN pad
        byte[] tempPubKeyData;
        byte[] tempCertData = eCert.getBytes();
        try {
            tempPubKeyData = keyManager.loadTrustedCert(false, tempCertData);
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "load E_CERT");
            return;
        }

        kdhDistributionKey.setKeyLen(tempPubKeyData.length);
        kdhDistributionKey.setKeyData(tempPubKeyData);

        try {
            keyManager.generateKey(KeyGenerateMethod.CLEAR, null, null, kdhDistributionKey, tempCertData);
            showMessage("E_CERT is loaded successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "load E_CERT public key");
            return;
        }

        // Verify S_CERT and load it to PIN pad
        tempCertData = sCert.getBytes();
        try {
            tempPubKeyData = keyManager.loadTrustedCert(false, tempCertData);
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "load S_CERT");
            return;
        }

        kdhSignKey.setKeyLen(tempPubKeyData.length);
        kdhSignKey.setKeyData(tempPubKeyData);

        try {
            keyManager.generateKey(KeyGenerateMethod.CLEAR, null, null, kdhSignKey, tempCertData);
            showMessage("S_CERT is loaded successfully.");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "load S_CERT public key");
            return;
        }

        sessionToken = initiationResponse.getS_TOKEN();
        kdhRandomNumber = initiationResponse.getKDH_RN();
        skType = initiationResponse.getSK_TYPE();
        requestKey();
    }

    private void requestKey() {
        showMessage("2. Requesting remote keys....");
        byte[] randomData;
        try {
            randomData = crypto.getRandom(8);
        } catch (NSDKException e) {
            showErrorMessage(e, "get random data");
            return;
        }
        KeyRequest keyRequest = new KeyRequest();
        keyRequest.setMAX_KN(MAX_KEY_NUM);

        final String randomDataStr = ISOUtils.hexString(Arrays.copyOf(randomData, 4));
        krdRandomNumber = randomDataStr;
        keyRequest.setKRD_RN(krdRandomNumber);

        keyRequest.setSK_TYPE(skType);

        int keyLen;
        KeyType skKeyType;
        if (skType == KeyType.DES.getCode()){
            keyLen = 24;
            skKeyType = KeyType.DES;
        }else if (skType == KeyType.AES.getCode()){
            keyLen = 16;
            skKeyType = KeyType.AES;
        } else {
            showMessage(String.format("Unsupported key type: %s", skType), MessageTag.ERROR);
            return;
        }

        sk.setKeyType(skKeyType);
        sk.setKeyLen(keyLen);

        AsymAlgorithmParameters parameters = new AsymAlgorithmParameters();
        parameters.setEncodingMode(AsymEncodingMode.PKCS_V21);
        parameters.setMessageDigestType(MessageDigestType.SHA256);

        byte[] skData;
        try {
            skData = keyManager.generateKeyWithAsymKey(KeyGenerateMethod.RANDOM_OUT, parameters, kdhDistributionKey, sk);
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "generate SK");
            return;
        }

        String sk = ISOUtils.hexString(skData);

        keyRequest.setSK(sk);
        keyRequest.setPAD(AsymEncodingMode.PKCS_V15.ordinal());

        String originalSignData = kdhRandomNumber + keyRequest.getMAX_KN() + krdRandomNumber + keyRequest.getSK_TYPE()
                + keyRequest.getSK() + keyRequest.getPAD();

        byte[] hash;
        byte[] signedData = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            hash = digest.digest(originalSignData.getBytes());
            if (isDemoMode) {
                krdSignKey.setKeyID(AppConfig.Keys.RKI_DEMO_DEVICE_KEY_ID);
            } else {
                krdSignKey.setKeyID(AppConfig.Keys.RKI_NORMAL_DEVICE_KEY_ID);
            }
            signedData = crypto.signAsym(krdSignKey, signAndVerifyParameters, hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            showMessage("Failed to get SHA256 message digest instance.", MessageTag.ERROR);
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "sign data");
            return;
        }

        keyRequest.setSIG(ISOUtils.hexString(signedData));
        keyRequest.setS_TOKEN(sessionToken);

        LogUtils.d(TAG, String.format("RKI key request: %s", keyRequest.toString()));
        Call<KeyResponse> requestKeyCall = rkiService.requestKey(keyRequest);
        requestKeyCall.enqueue(new Callback<KeyResponse>() {
            @Override
            public void onResponse(Call<KeyResponse> call, Response<KeyResponse> response) {
                final KeyResponse keyResponse = response.body();
                LogUtils.d(TAG, String.format("RKI key response: %s", keyResponse.toString()));
                SDKExecutors.threadStart(new Runnable() {
                    @Override
                    public void run() {
                        handleKeyResponse(keyResponse);
                    }
                });
            }

            @Override
            public void onFailure(Call<KeyResponse> call, Throwable t) {
                showMessage(String.format("Failed to get remote key: %s", t.getMessage()), MessageTag.ERROR);
            }
        });
    }

    private void handleKeyResponse(KeyResponse keyResponse) {
        String status = keyResponse.getSTATUS();

        if (!ResponseCode.SERVER_OK.getValue().equals(status)) {
            showMessage(String.format("Key request status is not OK: %s", status), MessageTag.ERROR);
            return;
        }

        String signedStr = keyResponse.getSIG();
        byte[] signedData = ISOUtils.hex2byte(signedStr);
        kdhRandomNumber = keyResponse.getKDH_RN();

        String originalSignData = krdRandomNumber + keyResponse.getSTATUS() + keyResponse.getKN()
                + kdhRandomNumber + keyResponse.getPKG_NAME() + keyResponse.getPKG_ID()
                + keyResponse.getKEY_LIST().toString();

        byte[] hash;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            hash = digest.digest(originalSignData.getBytes());
            crypto.verifyAsym(kdhSignKey, signAndVerifyParameters, hash, signedData);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            showMessage("Failed to get SHA256 message digest instance.", MessageTag.ERROR);
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "verify singed data");
            return;
        }

        // Load keys
        List<KeyResponse.KEY_LIST> keyLists = keyResponse.getKEY_LIST();
        List<VerificationRequest.RESULT> results = new ArrayList<>();

        try {
            keyManager.initAtomic();
            showMessage("Init atomic, start to inject remote keys...");
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "init atomic");
            return;
        }

        int ret;
        if (keyLists != null && keyLists.size() > 0){
            for (int i = 0; i < keyLists.size(); i++) {
                VerificationRequest.RESULT result = new VerificationRequest().new RESULT();
                KeyResponse.KEY_LIST keyList = keyLists.get(i);
                result.setErrmsg("");
                byte[] kcv = null;
                if (!TextUtils.isEmpty(keyList.getKcv())) {
                    kcv = ISOUtils.hex2byte(keyList.getKcv());
                }
                byte[] keyValue =keyList.getValue().getBytes();

                if (keyValue.length <= 0){
                    result.setName(keyList.getName());
                    result.setResult(-1);
                    result.setErrmsg("Failed to get key info.");
                    results.add(result);
                    showMessage(String.format("Failed to get key info of %s.", keyList.getName()), MessageTag.ERROR);
                    commitAtomic(false);
                    break;
                }

                SymmetricKey dstKey = new SymmetricKey();
                dstKey.setKeyID((byte) keyList.getIndex());
                KeyType keyType = getKeyType(keyList.getType());
                if (keyType == null) {
                    result.setName(keyList.getName());
                    result.setResult(-1);
                    result.setErrmsg(String.format("Unsupported key type(%d)", keyList.getType()));
                    results.add(result);
                    showMessage(String.format("Unsupported key type(%d)", keyList.getType()), MessageTag.ERROR);
                    commitAtomic(false);
                    break;
                }
                dstKey.setKeyType(keyType);
                KeyUsage keyUsage =  getKeyUsage(keyList.getUsage());
                if (keyUsage == null) {
                    result.setName(keyList.getName());
                    result.setResult(-1);
                    result.setErrmsg(String.format("Unsupported key usage(%d)", keyList.getUsage()));
                    results.add(result);
                    showMessage(String.format("Unsupported key usage(%d)", keyList.getUsage()), MessageTag.ERROR);
                    commitAtomic(false);
                    break;
                }
                dstKey.setKeyUsage(keyUsage);
                dstKey.setKCV(kcv);
                dstKey.setKeyLen(keyValue.length);
                if (dstKey.getKeyUsage() == KeyUsage.DUKPT) {
                    dstKey.setKCVMode(KCVMode.NONE);
                } else {
                    dstKey.setKCVMode(KCVMode.ZERO);
                }
                dstKey.setKeyData(keyValue);

                try {
                    keyManager.generateKey(KeyGenerateMethod.TR31, null, sk, dstKey);
                    showMessage(String.format("Key(%s) is generated.", keyList.getName()));
                    ret = 0;
                } catch (NSDKException e) {
                    e.printStackTrace();
                    ret = -1;
                    result.setErrmsg(String.format("Failed to inject key(%d).", e.getCode()));
                    showErrorMessage(e, String.format("generate remote key(%s)", keyList.getName()));
                }

                result.setName(keyList.getName());
                result.setResult(ret);
                results.add(result);
                if (ret == -1) {
                    break;
                }
            }
        }
        verifyKeyInjectionResult(results);
    }

    private void verifyKeyInjectionResult(List<VerificationRequest.RESULT> results) {
        showMessage("3. Verifying key loading results....");

        byte[] randomData;
        try {
            randomData = crypto.getRandom(8);
        } catch (NSDKException e) {
            showErrorMessage(e, "get random data");
            return;
        }

        final String randomDataStr = ISOUtils.hexString(Arrays.copyOf(randomData, 4));
        krdRandomNumber = randomDataStr;

        VerificationRequest verificationRequest = new VerificationRequest();
        verificationRequest.setKRD_RN(krdRandomNumber);
        verificationRequest.setRESULT(results);

        String originalSignData = kdhRandomNumber + results.toString() + krdRandomNumber;
        byte[] hash;
        byte[] signedData = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            hash = digest.digest(originalSignData.getBytes());
            if (isDemoMode) {
                krdSignKey.setKeyID(AppConfig.Keys.RKI_DEMO_DEVICE_KEY_ID);
            } else {
                krdSignKey.setKeyID(AppConfig.Keys.RKI_NORMAL_DEVICE_KEY_ID);
            }
            signedData = crypto.signAsym(krdSignKey, signAndVerifyParameters, hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            showMessage("Failed to get SHA256 message digest instance.", MessageTag.ERROR);
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "sign data");
            return;
        }

        verificationRequest.setSIG(ISOUtils.hexString(signedData));
        verificationRequest.setS_TOKEN(sessionToken);

        LogUtils.d(TAG, String.format("RKI verification request: %s", verificationRequest.toString()));
        Call<VerificationResponse> verificationKeyCall = rkiService.verifyResult(verificationRequest);
        verificationKeyCall.enqueue(new Callback<VerificationResponse>() {
            @Override
            public void onResponse(Call<VerificationResponse> call, Response<VerificationResponse> response) {
                final VerificationResponse verificationResponse = response.body();
                LogUtils.d(TAG, String.format("RKI verification response: %s", verificationResponse.toString()));
                SDKExecutors.threadStart(new Runnable() {
                    @Override
                    public void run() {
                        handleVerificationResponse(verificationResponse);
                    }
                });
            }

            @Override
            public void onFailure(Call<VerificationResponse> call, Throwable t) {
                showMessage(String.format("Failed to verify results: %s", t.getMessage()), MessageTag.ERROR);
            }
        });
    }

    private void handleVerificationResponse(VerificationResponse verificationResponse) {
        String status = verificationResponse.getSTATUS();
        kdhRandomNumber = verificationResponse.getKDH_RN();

        if (status.equals(ResponseCode.SERVER_OK.getValue()) || status.equals(ResponseCode.SERVER_ERR_01.getValue())){
            String signedDataStr = verificationResponse.getSIG();
            byte[] signedData = ISOUtils.hex2byte(signedDataStr);
            String originalSignData = krdRandomNumber + verificationResponse.getSTATUS() + kdhRandomNumber;

            byte[] hash;
            boolean isSuccessful = true;
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                hash = digest.digest(originalSignData.getBytes());
                crypto.verifyAsym(kdhSignKey, signAndVerifyParameters, hash, signedData);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                showMessage("Failed to get SHA256 message digest instance.", MessageTag.ERROR);
                isSuccessful = false;
            } catch (NSDKException e) {
                e.printStackTrace();
                showErrorMessage(e, "verify singed data");
                isSuccessful = false;
            }

            if (!isSuccessful) {
                commitAtomic(isSuccessful);
                return;
            }

            if (status.equals(ResponseCode.SERVER_ERR_01.getValue())){
                requestKey();
            }else {
                commitAtomic(true);
                showMessage("RKI process is finished.");
            }
        } else {
            commitAtomic(false);
        }
    }

    private void commitAtomic(boolean isSuccessful) {
        try {   
            keyManager.commitAtomic(isSuccessful);
            if (isSuccessful) {
                showMessage("Commit atomic successfully.");
            } else {
                showMessage("All of the keys that already loaded are reversed.", MessageTag.ERROR);
            }
        } catch (NSDKException e) {
            e.printStackTrace();
            showErrorMessage(e, "commit atomic");
        }
    }

    private KeyUsage getKeyUsage(int usage) {
        KeyUsage[] keyUsages = KeyUsage.values();
        for (KeyUsage keyUsage: keyUsages) {
            if (keyUsage.getCode() == usage) {
                return keyUsage;
            }
        }
        return null;
    }

    private KeyType getKeyType(int type) {
        KeyType[] keyTypes = KeyType.values();
        for (KeyType keyUsage: keyTypes) {
            if (keyUsage.getCode() == type) {
                return keyUsage;
            }
        }
        return null;
    }

    public boolean isIPAddress(String str) {
        String regex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
        if (str.matches(regex)) {
            String[] arr = str.split("\\.");
            for (int i = 0; i < 4; i++) {
                int temp = Integer.parseInt(arr[i]);
                if (temp < 0 || temp > 255) return false;
            }
            return true;
        } else return false;
    }

    public boolean isPortValid(String str) {
        try {
            int port = Integer.parseInt(str);
            return port >= 0 && port <= 65535;
        } catch (Exception e) {
            return false;
        }
    }

    private String getBaseUrl() {
        String  baseUrl = String.format("https://%s:%d/", kdhUrl, kdhPort);
        LogUtils.d(TAG, String.format("Base url: %s", baseUrl));
        return baseUrl;
    }
}
