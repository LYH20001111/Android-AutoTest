package com.newland.nsdk.core.internal;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.utils.LogLevel;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.common.utils.NativeDebugLevel;
import com.newland.nsdk.core.api.internal.NSDKModuleManager;
import com.newland.nsdk.core.api.internal.barcodedecoder.BarcodeDecoder;
import com.newland.nsdk.core.api.internal.cardreader.CardReader;
import com.newland.nsdk.core.api.internal.devicemanager.DeviceInfo;
import com.newland.nsdk.core.api.internal.devicemanager.DeviceManager;
import com.newland.nsdk.core.api.internal.exception.NSDKNDKException;
import com.newland.nsdk.core.common.NSDKExecutors;
import com.newland.nsdk.core.common.Version;
import com.newland.nsdk.core.common.uart3.SerialPortJni;
import com.newland.nsdk.core.internal.BarcodeDecoder.BarcodeDecoderImpl;
import com.newland.nsdk.core.internal.analogserial.AnalogSerialManagerImpl;
import com.newland.nsdk.core.internal.barcodescanner.BarcodeScannerImpl;
import com.newland.nsdk.core.internal.beeper.BeeperImpl;
import com.newland.nsdk.core.internal.bootprovider.BootProviderImpl;
import com.newland.nsdk.core.internal.cardreader.CardReaderImpl;
import com.newland.nsdk.core.internal.cashbox.CashBoxImpl;
import com.newland.nsdk.core.internal.crypto.CryptoImpl;
import com.newland.nsdk.core.internal.devicemanager.DeviceManagerImpl;
import com.newland.nsdk.core.internal.devicestatisticsmanager.DeviceStatisticsManagerImpl;
import com.newland.nsdk.core.internal.emvl2.EMVL2ServiceImpl;
import com.newland.nsdk.core.internal.ethernetmanager.EthernetManagerImpl;
import com.newland.nsdk.core.internal.futurex.FutureXImpl;
import com.newland.nsdk.core.internal.guestdisplaymanager.GuestDisplayManagerImpl;
import com.newland.nsdk.core.internal.jni.NSDKJni;
import com.newland.nsdk.core.internal.keymanager.KeyManagerImpl;
import com.newland.nsdk.core.internal.led.LEDImpl;
import com.newland.nsdk.core.internal.pinentry.PINEntry2Impl;
import com.newland.nsdk.core.internal.pinentry.PINEntryImpl;
import com.newland.nsdk.core.internal.printer.PrinterImpl;
import com.newland.nsdk.core.internal.recovery.RecoveryImpl;
import com.newland.nsdk.core.internal.routemanager.RouteManagerImpl;
import com.newland.nsdk.core.internal.serialportmanager.SerialPortManagerImpl;
import com.newland.nsdk.core.internal.system.SettingsManagerImpl;
import com.newland.nsdk.core.internal.system.SystemPropertyUtil;

import java.util.HashMap;
import java.util.Locale;

/**
 * <p>Call entry for the development kit.</p>
 * <p>call step:</p>
 * <p>1.get the instance of the NSDKModuleManager. NSDKModuleManager moduleManager = NSDKModuleManager.getInstance();</p>
 * <p>2.Initializes the device module.</p>
 * <p>3.invoke the method to get the device module.</p>
 * <p>4.Destroy device module</p>
 * Author by liudan, Date on 2020/1/19.
 */
public class NSDKModuleManagerImpl implements NSDKModuleManager {
    public static final String TAG = "NSDKModuleManagerImpl";
    // 新大陆 A7 的 android API level 是 25
    public static final int A7 = 25;
    private static HashMap<String, Module> modules = new HashMap<String, Module>();
    private static NSDKModuleManagerImpl moduleManage;
    private Context context;

    private NSDKModuleManagerImpl() {

    }

    public static NSDKModuleManagerImpl getInstance() {
        if (moduleManage == null) {
            synchronized (NSDKModuleManagerImpl.class) {
                if (moduleManage == null) {
                    moduleManage = new NSDKModuleManagerImpl();
                }
            }
        }
        return moduleManage;
    }

    /**
     * Initializes the device module.
     *
     * @return
     */
    @Override
    public synchronized void init(Context context) throws NSDKException{
        if (context == null) {
            throw new NSDKIllegalParameterException("Context shall not be null");
        }
        this.context = context;
        modules.put(ModuleType.SETTINGS, SettingsManagerImpl.getInstance(context));
        modules.put(ModuleType.BOOT_PROVIDER, BootProviderImpl.getInstance(context));
        modules.put(ModuleType.RECOVERY, RecoveryImpl.getInstance(context));
        modules.put(ModuleType.DEVICE_STATISTICS_MANAGER, DeviceStatisticsManagerImpl.getInstance(context));
        modules.put(ModuleType.ROUTE_MANAGER, RouteManagerImpl.getInstance(context));
        init();
    }

    public Context getContext(){
        return this.context;
    }

    @Deprecated
    public void init() throws NSDKException{
        boolean isSupported = true;

        DeviceManager deviceManager = DeviceManagerImpl.getInstance(isSupported);

        // 只有开发机（D 版本固件）才允许使用临时版本 nsdk
        DeviceInfo deviceInfo = deviceManager.getDeviceInfo();
        int androidVersion = deviceInfo.getAndroidVersion();
        if (androidVersion < A7) {
            modules.clear();
            throw new NSDKException("NSDK is supported from A7.");
        }
        Log.d(TAG, "Device Mode: " + deviceInfo.getDeviceModel());

        modules.put(ModuleType.DEVICE_MANAGER, deviceManager);
        modules.put(ModuleType.EMV_L2_SERVICE, EMVL2ServiceImpl.getInstance());
        modules.put(ModuleType.LED, LEDImpl.getInstance(isSupported));

        BarcodeDecoder barcodeDecoder = BarcodeDecoderImpl.getInstance(context, deviceInfo.getScannerConfig().supportSoftDecoding());
        modules.put(ModuleType.BARCODE_DECODER, barcodeDecoder);

        modules.put(ModuleType.BARCODE_SCANNER, BarcodeScannerImpl.getInstance(context, deviceInfo.getScannerConfig()));


        isSupported = deviceInfo.isSupportICCard() || deviceInfo.isSupportMagCard() || deviceInfo.isSupportQuickPass();
        CardReader cardReader = CardReaderImpl.getInstance(isSupported, deviceInfo.isSupportLPCD());
        modules.put(ModuleType.CARD_READER, cardReader);

        isSupported = deviceManager.isExistSecurityModule();
        modules.put(ModuleType.FUTUREX, FutureXImpl.getInstance(isSupported));
        modules.put(ModuleType.CRYPTO, CryptoImpl.getInstance(isSupported));
        modules.put(ModuleType.KEY_MANAGER, KeyManagerImpl.getInstance(isSupported));
        modules.put(ModuleType.PIN_ENTRY, PINEntryImpl.getInstance(isSupported, deviceInfo));
        modules.put(ModuleType.PIN_ENTRY_2, PINEntry2Impl.getInstance(isSupported, deviceInfo));

        isSupported = deviceInfo.isSupportBeep();
        modules.put(ModuleType.BEEPER, BeeperImpl.getInstance(isSupported));

        isSupported = deviceInfo.isSupportPrint();
        modules.put(ModuleType.PRINTER, PrinterImpl.getInstance(isSupported));

        isSupported = deviceInfo.isSupportCashBox();

        modules.put(ModuleType.CASH_BOX, CashBoxImpl.getInstance(context, isSupported));

        modules.put(ModuleType.SERIAL_PORT_MANAGER, SerialPortManagerImpl.getInstance(context, deviceInfo.isSupportUSB(), deviceInfo.isSupportPinpadPort(), deviceInfo.isSupport232Port()));



        if (!isUseAndroidEthernetManager(deviceInfo)) {
            modules.put(ModuleType.ETHERNET_MANAGER, EthernetManagerImpl.getInstance(context, false));
        } else {
            modules.put(ModuleType.ETHERNET_MANAGER, EthernetManagerImpl.getInstance(context, deviceInfo.isSupportEthernet()));
        }

        isSupported = deviceInfo.isSupportUSB();
        modules.put(ModuleType.ANALOG_SERIAL, AnalogSerialManagerImpl.getInstance(context, isSupported));

        isSupported = deviceInfo.isSupportGuestDisplay();
        modules.put(ModuleType.GUEST_DISPLAY_MANAGER, GuestDisplayManagerImpl.getInstance(isSupported));



        Log.d(TAG, String.format(Locale.US, "Android version: %d", androidVersion));
        if (androidVersion == A7 && deviceInfo.isSupportQuickPass()) {
            // 不同平台非接的版本号会不一样，所以按照平台来判断非接版本号。对于 A7 来说，1.1.8 版本才有导入非接新接口
            String contactlessVersion = deviceInfo.getContactlessVer();
            Log.d(TAG, String.format(Locale.US,"Contactless module version: %s", contactlessVersion));
            Version version = Version.getVersion(contactlessVersion);
            if (version.isLower(1, 1, 8)) {
                throw new NSDKNDKException(ErrorCode.NEED_UPDATE, String.format(Locale.US,"Current contactless version is %s, required to be >= 1.1.8 for A7 devices.", contactlessVersion));
            }
        }
    }

    /**
     * <p>Get the device module.</p>
     *
     * @return
     */
    @Override
    public Module getModule(String moduleName) {
        if (modules.isEmpty()) {
            LogUtils.e(TAG, "Please call init first.");
            return null;
        }
        return modules.get(moduleName);
    }

    /**
     * release the device resources.
     *
     * @return
     */
    @Override
    public void destroy() {
        moduleManage = null;
        modules.clear();
        NSDKExecutors.release();
    }

    @Override
    public void setDebugMode(LogLevel level) {
        if (level == null) {
            level = LogLevel.OFF;
        }
        LogUtils.setLogLevel(level);
    }

    @Deprecated
    public void enableNativeLog(boolean isEnable) {
        NSDKJni.getInstance().enableNativeLog(isEnable);
    }

    @Override
    public void setNativeDebugMode(NativeDebugLevel nativeDebugLevel) throws NSDKException{
        if (nativeDebugLevel == null) {
            throw new NSDKException(ErrorCode.PARAM_ERROR, "NativeDebugLevel shall not be null.");
        }
        switch (nativeDebugLevel) {
            case ALL_ON:
                NSDKJni.getInstance().enableNativeLog(true);
                NSDKJni.getInstance().enableNDKLog(1,1);
                SerialPortJni.getInstance().setDebugMode(2);
                break;
            case ALL_OFF:
                NSDKJni.getInstance().enableNativeLog(false);
                NSDKJni.getInstance().enableNDKLog(0,0);
                SerialPortJni.getInstance().setDebugMode(0);
                break;
            case NSDK_ON:
                NSDKJni.getInstance().enableNativeLog(true);
                SerialPortJni.getInstance().setDebugMode(2);
                break;
            case NSDK_OFF:
                NSDKJni.getInstance().enableNativeLog(false);
                SerialPortJni.getInstance().setDebugMode(0);
                break;
            case DRIVER_ON:
                NSDKJni.getInstance().enableNDKLog(1,1);
                break;
            case DRIVER_OFF:
                NSDKJni.getInstance().enableNDKLog(0,0);
                break;
            case DRIVER_DETECT_CARD_OFF:
                NSDKJni.getInstance().enableNDKLog(2,1);
                break;
        }
    }

    @Override
    public String getErrMsg(int errCode) {
        String errMsg = "";
        int length = 0;
        try {
            byte[] msg = new byte[128];
            NSDKJni.getInstance().getErrorMsg(errCode, msg);
            for(int i = 0; i< msg.length; ++i) {
                if(msg[i] == 0) {
                    length = i;
                    break;
                }
            }
            errMsg = new String(msg, 0, length, "UTF-8");
        }catch (Exception e) {
            e.printStackTrace();
        }
        return errMsg;
    }

    private boolean isUseAndroidEthernetManager(DeviceInfo deviceInfo) {
        String platform = SystemPropertyUtil.getProperty("ro.board.platform","unknown");
        LogUtils.d(TAG, "Current platform is " + platform);
        LogUtils.d(TAG, "MODEL is " + Build.MODEL);
        return !"rk3326".equals(platform) && (!Build.MODEL.contains("CPOS") || deviceInfo.getAndroidVersion() < 29) && !Build.MODEL.contains("X800") && !Build.MODEL.equals("U2000") && !Build.MODEL.contains("P300") && !Build.MODEL.contains("N750")
                && !Build.MODEL.contains("N950") && !(deviceInfo.getAndroidVersion() >= 29);
    }

//    private boolean isExhibitionDevice() {
//        try {
//            LogUtils.d(TAG, "Enter isExhibition");
//            android.newland.security.CertificateInfo certificateInfo = new CertificateInfo(context);
//            LogUtils.d(TAG, "get certificateInfo");
//            X509Certificate certificate = certificateInfo.getCertificateInfo();
//            LogUtils.d(TAG,"Is certificate null? :" + String.valueOf(certificate == null));
//            if (certificate == null) {
//                return false;
//            }
//            MessageDigest md = MessageDigest.getInstance("SHA-1");
//            String sha1 = ISOUtils.hexString(md.digest(certificate.getEncoded()));
//            LogUtils.d(TAG, "SHA1: " + sha1);
//            if (!"8D7DD1022D6776E3F132A9E84DE2BB48E2BCA230".equalsIgnoreCase(sha1)) {
//                return false;
//            }
//        } catch (NoSuchAlgorithmException | CertificateEncodingException e) {
//            e.printStackTrace();
//        }
//        return true;
//    }

    //Check device SN whether NSDK should skip the version check process
    private boolean checkSpecificSN(DeviceInfo deviceInfo, String[] sn) {
        String deviceSN = deviceInfo.getSN();
        for (String SN : sn) {
            if (SN.equalsIgnoreCase(deviceSN)) {
                return true;
            }
        }
        return false;

    }

}
