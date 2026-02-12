package com.newland.nsdk.core.internal.devicemanager;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.newland.os.NlBuild;
import android.newland.security.CertificateInfo;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import com.newland.nsdk.BuildConfig;
import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.card.contact.ContactCardSlot;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.serialport.ParityBit;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.internal.devicemanager.AntiRemovalStatus;
import com.newland.nsdk.core.api.internal.devicemanager.BatteryProperty;
import com.newland.nsdk.core.api.internal.devicemanager.DeviceInfo;
import com.newland.nsdk.core.api.internal.devicemanager.DeviceLight;
import com.newland.nsdk.core.api.internal.devicemanager.DeviceManager;
import com.newland.nsdk.core.api.internal.devicemanager.EthernetMode;
import com.newland.nsdk.core.api.internal.devicemanager.KeyboardButton;
import com.newland.nsdk.core.api.internal.devicemanager.LightMode;
import com.newland.nsdk.core.api.internal.devicemanager.RadarGain;
import com.newland.nsdk.core.api.internal.devicemanager.ScannerConfig;
import com.newland.nsdk.core.api.internal.devicemanager.TamperReason;
import com.newland.nsdk.core.api.internal.devicemanager.TamperStatus;
import com.newland.nsdk.core.api.internal.exception.NSDKNDKException;
import com.newland.nsdk.core.common.uart3.SerialPortJni;
import com.newland.nsdk.core.internal.NSDKModuleManagerImpl;
import com.newland.nsdk.core.internal.jni.NSDKJni;
import com.newland.nsdk.core.internal.system.SystemPropertyUtil;

import java.io.File;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Author by liudan, Date on 2020/1/19.
 */
public class DeviceManagerImpl implements DeviceManager {

    private static final String TAG = "DeviceManagerImpl";
    private byte[] caps = new byte[9];
    private static int REMOTE_CFG_TYPE_MAG = 1;
    private static int REMOTE_CFG_TYPE_RFID = 2;
    private static int REMOTE_CFG_TYPE_IC = 4;
    private static int REMOTE_CFG_TYPE_PRN = 8;
    public boolean isSupported;

    private volatile static DeviceManagerImpl instance;

    public static DeviceManagerImpl getInstance(boolean isSupported) {
        if (instance == null) {
            synchronized (DeviceManagerImpl.class) {
                if (instance == null || instance.isSupported != isSupported) {
                    instance = new DeviceManagerImpl(isSupported);
                }
            }
        } else {
            if (instance.isSupported != isSupported) {
                instance = new DeviceManagerImpl(isSupported);
            }
        }
        return instance;
    }

    private DeviceManagerImpl(){
        this.isSupported = true;
    };

    private DeviceManagerImpl(boolean isSupported){
        this.isSupported = isSupported;
    }

    public static String getProperties(String key) {
        String defaultValue = "unknown";
        String value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            value = (String) (get.invoke(c, key, defaultValue));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    private static boolean isSupportConfig() {
        String version = NlBuild.VERSION.NL_FIRMWARE;
        version = version.replaceAll("V", "").replace("T", "");
        LogUtils.d(TAG, "NL_HARDWARE_ID:" + NlBuild.VERSION.NL_HARDWARE_ID + ">>>> version:" + version);
        // 硬件识别码
        if ("SA1".equals(NlBuild.VERSION.NL_HARDWARE_ID)) {
            //3G版本1.1.12之前不支持硬件配置码
            return "1.1.12".compareToIgnoreCase(version) <= 0;
        }
        return true;
    }

    public static String getSysProperty(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method method = c.getMethod("get", String.class);
            value = (String) (method.invoke(c, key));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static boolean getHasSecModule() {
        boolean hasSecModule = true;
        if (getSysProperty("persist.sys.HasSecModule", "yes").equals("no")) {
            hasSecModule = false;
        }
        LogUtils.d(TAG, ">>>hasSecModule=" + hasSecModule);
        return hasSecModule;
    }

    /**
     * Get the device information<p>
     *
     * @return Current device information
     */
    @Override
    public DeviceInfo getDeviceInfo() throws NSDKException{
        if(!isSupported){
            throw new NSDKException(ErrorCode.UNSUPPORTED, "UnSupported DeviceManager Module");
        }

        final int ret = NSDKJni.getInstance().NDK_SysGetCapability(caps.length, caps);

//        if (ret != ErrorCode.OK) {
//            throw new NSDKNDKException(ret, String.format("Failed to get capability, ret[%d]", ret));
//        }
        LogUtils.d(TAG, String.format(">>>NL_HARDWARE_CONFIG: %s", NlBuild.VERSION.NL_HARDWARE_CONFIG));
        LogUtils.d(TAG, String.format(">>>Capability: %s", ISOUtils.hexString(caps)));
        final int remoteCfg = Integer.parseInt(SystemPropertyUtil.getProperty("persist.sys.remotecfg", "15"));
        Log.d(TAG, "remoteCfg:" + remoteCfg);

        DeviceInfo deviceInfo = new DeviceInfo() {

            @Override
            public String getSN() {
                byte[] snBytes = new byte[30];

                int len = NSDKJni.getInstance().NDK_GetDeviceSN(snBytes);
                LogUtils.d(getClass().getName(), "SN:" + ISOUtils.hexString(snBytes));

                if (len > 0) {
                    String sn = new String(snBytes).substring(0, len);
                    return sn;
                } else {
                    return null;
                }
            }

            @Override
            public String getPN() {
                byte[] snBytes = new byte[30];
                int SYS_HWINFO_GET_POS_PSN = 4;

                int len = NSDKJni.getInstance().NDK_SysGetPosInfo(SYS_HWINFO_GET_POS_PSN, snBytes);
                LogUtils.d(getClass().getName(), "PN:" + ISOUtils.hexString(snBytes));

                if (len > 0) {
                    String sn = new String(snBytes).substring(0, len);
                    return sn;
                } else {
                    return null;
                }
            }

            @Override
            public boolean isSupportMagCard() {
                if (ret != ErrorCode.OK) {
                    if (isSupportConfig()) {
                        String CONFIG = NlBuild.VERSION.NL_HARDWARE_CONFIG;
                        if (CONFIG != null && CONFIG.length() > 16) {
                            String cfg = CONFIG.substring(14, 16);
                            return !"FF".equals(cfg) && ((remoteCfg & REMOTE_CFG_TYPE_MAG) == REMOTE_CFG_TYPE_MAG) ;
                        }
                    }
                } else {
                    return caps[6] == 'Y' && ((remoteCfg & REMOTE_CFG_TYPE_MAG) == REMOTE_CFG_TYPE_MAG);
                }
                return false;
            }

            @Override
            public boolean isSupportICCard() {
                if (ret != ErrorCode.OK) {
                    if (isSupportConfig()) {
                        String CONFIG = NlBuild.VERSION.NL_HARDWARE_CONFIG;
                        if (CONFIG != null && CONFIG.length() > 18) {
                            String cfg = CONFIG.substring(16, 18);
                            return !"FF".equals(cfg) && ((remoteCfg & REMOTE_CFG_TYPE_IC) == REMOTE_CFG_TYPE_IC);
                        }
                    }
                } else {
                    return caps[5] == 'Y' && ((remoteCfg & REMOTE_CFG_TYPE_IC) == REMOTE_CFG_TYPE_IC);
                }
                return false;
            }

            @Deprecated
            public boolean isSupportQuickPass() {
                if (ret != ErrorCode.OK) {
                    if (isSupportConfig()) {
                        String CONFIG = NlBuild.VERSION.NL_HARDWARE_CONFIG;
                        if (CONFIG != null && CONFIG.length() > 14) {
                            String cfg = CONFIG.substring(12, 14);
                            return !"FF".equals(cfg) && ((remoteCfg & REMOTE_CFG_TYPE_RFID) == REMOTE_CFG_TYPE_RFID);
                        }
                    }
                } else {
                    return caps[4] == 'Y' && ((remoteCfg & REMOTE_CFG_TYPE_RFID) == REMOTE_CFG_TYPE_RFID);
                }
                return false;
            }

            @Override
            public boolean isSupportContactlessCard() {
                if (ret != ErrorCode.OK) {
                    if (isSupportConfig()) {
                        String CONFIG = NlBuild.VERSION.NL_HARDWARE_CONFIG;
                        if (CONFIG != null && CONFIG.length() > 14) {
                            String cfg = CONFIG.substring(12, 14);
                            return !"FF".equals(cfg) && ((remoteCfg & REMOTE_CFG_TYPE_RFID) == REMOTE_CFG_TYPE_RFID);
                        }
                    }
                } else {
                    return caps[4] == 'Y' && ((remoteCfg & REMOTE_CFG_TYPE_RFID) == REMOTE_CFG_TYPE_RFID);
                }
                return false;
            }

            @Override
            public boolean isSupportHCE() {
                int[] result = new int[1];
                int ret = NSDKJni.getInstance().NDK_RfidFuncisSupport(0, result);
                if (ret != ErrorCode.OK) {
                    return isSupportHCEByHardware();
                }
                int r = result[0];
                //结果为 2 时，代表当前芯片为 3916，驱动无法判断是否支持，需要使用硬件配置码进行补充判断；结果为 1 代表当前设备非接芯片支持 HCE，其他为不支持
                if (r != 2) {
                    return r == 1;
                } else {
                    return isSupportHCEByHardware();
                }
            }

            @Override
            public boolean isSupportLPCD() {
                int[] result = new int[1];
                int ret = NSDKJni.getInstance().NDK_RfidFuncisSupport(1, result);
                if (ret != ErrorCode.OK) {
                    return false;
                }
                return result[0] == 1;
            }

            @Override
            public boolean isSupportPrint() {
                if (ret != ErrorCode.OK) {
                    if (isSupportConfig()) {
                        String CONFIG = NlBuild.VERSION.NL_HARDWARE_CONFIG;
                        if (CONFIG != null && CONFIG.length() > 20) {
                            String cfg = CONFIG.substring(18, 20);
                            return !"FF".equals(cfg) && ((remoteCfg & REMOTE_CFG_TYPE_PRN) == REMOTE_CFG_TYPE_PRN);
                        }
                    }
                } else {
                    if (Build.MODEL.contains("CPOS") && (Integer.parseInt(getFirmwareVer().split("\\.")[2]) > 61)) {
                        if (isSupportConfig()) {
                            String CONFIG = NlBuild.VERSION.NL_HARDWARE_CONFIG;
                            if (CONFIG != null && CONFIG.length() > 20) {
                                String cfg = CONFIG.substring(18, 20);
                                return !"FF".equals(cfg) && ((remoteCfg & REMOTE_CFG_TYPE_PRN) == REMOTE_CFG_TYPE_PRN);
                            }
                        }
                    }
                    return caps[3] == 'Y' && ((remoteCfg & REMOTE_CFG_TYPE_PRN) == REMOTE_CFG_TYPE_PRN);
                }
                return false;
            }

            @Override
            public boolean isSupportOffline() {
                return isSupportICCard();
            }

            @Override
            public String getFirmwareVer() {
                return NlBuild.VERSION.NL_FIRMWARE;
            }

            @Override
            public String getContactlessVer() {
                if (isSupportQuickPass()) {
                    try {
                        return getContactlessVersion();
                    } catch (NSDKException e) {
                        e.printStackTrace();
                        return "Unknown";
                    }
                }

                return "Unsupported";
            }

            @Override
            public boolean isSupportUSB() {

                if (isSupportConfig()) {
                    String CONFIG = NlBuild.VERSION.NL_HARDWARE_CONFIG;
                    if (CONFIG != null && CONFIG.length() >= 34) {
                        String cfg = CONFIG.substring(32, 34);
                        return !"FF".equals(cfg);
                    }
                }

                return true;
            }

            @Override
            public boolean isSupportGPS() {
                if (isSupportConfig()) {
                    String CONFIG = NlBuild.VERSION.NL_HARDWARE_CONFIG;
                    if (CONFIG != null && CONFIG.length() >= 12) {
                        String cfg = CONFIG.substring(10, 12);
                        return !"00".equals(cfg);
                    }
                }
                return true;
            }

            @Override
            public boolean isSupportEthernet() {
                if (isSupportConfig()) {
                    String CONFIG = NlBuild.VERSION.NL_HARDWARE_CONFIG;
                    if (CONFIG != null && CONFIG.length() >= 28) {
                        String cfg = CONFIG.substring(26, 28);
                        return !"FF".equals(cfg);
                    }
                }
                return false;
            }

            @Override
            public boolean isSupportCashBox() {
                if (isSupportConfig()) {
                    String CONFIG = NlBuild.VERSION.NL_HARDWARE_CONFIG;
                    if (CONFIG != null && CONFIG.length() >= 30) {
                        String cfg = CONFIG.substring(28, 30);
                        return "01".equals(cfg);
                    }
                }
                return false;
            }

            @Override
            public boolean isSupportSam() {
                if (isSupportConfig()) {
                    String CONFIG = NlBuild.VERSION.NL_HARDWARE_CONFIG;
                    if (CONFIG != null && CONFIG.length() >= 24) {
                        String cfg = CONFIG.substring(22, 24);
                        return !"FF".equals(cfg);
                    }
                }
                return true;
            }

            @Override
            public boolean isSupportPinpadPort() {
                if (isSupportConfig()) {
                    String CONFIG = NlBuild.VERSION.NL_HARDWARE_CONFIG;
                    if (CONFIG != null && CONFIG.length() >= 32) {
                        String cfg = CONFIG.substring(30, 32);
                        //判断有2个外置串口就认为它支持外接密码键盘(x5，走NLUART3)
                        if (Build.MODEL.contains("X5") || Build.MODEL.contains("x5")) {
                            return "02".equals(cfg) || "03".equals(cfg) || "04".equals(cfg);
                        } else {
                            return "02".equals(cfg) || "03".equals(cfg);
                        }
                    }
                }
                return false;
            }

            @Override
            public boolean isSupport232Port() {
                if (isSupportConfig()) {
                    String CONFIG = NlBuild.VERSION.NL_HARDWARE_CONFIG;
                    if (CONFIG != null && CONFIG.length() >= 32) {
                        String cfg = CONFIG.substring(30, 32);
                        return "01".equals(cfg) || "03".equals(cfg) || "04".equals(cfg) || "05".equals(cfg) || "11".equals(cfg);
                    }
                }
                return false;
            }

            @Override
            public boolean isSupportCamera() {
                if (isSupportConfig()) {
                    String CONFIG = NlBuild.VERSION.NL_HARDWARE_CONFIG;
                    if (CONFIG != null && CONFIG.length() >= 26) {
                        String cfg = CONFIG.substring(24, 26);
                        return !"FF".equals(cfg);
                    }
                }
                return true;
            }

            @Override
            public ScannerConfig getScannerConfig() {
                boolean hasFrontCamera = false;
                boolean hasBackCamera = false;
                boolean hasPaymentCamera = false;
                boolean hasFrontScanner = false;
                boolean hasHardScanner = false;

                String SCANNER_LICENCE_FILE = "/newland/factory/ScanCommon/license/thk88.lic";
                boolean supportSoftDecoding = isFileExist(SCANNER_LICENCE_FILE);

                if (isSupportConfig()) {
                    String CONFIG = NlBuild.VERSION.NL_HARDWARE_CONFIG;
                    if (CONFIG != null && CONFIG.length() >= 10) {
                        String cfg = CONFIG.substring(8, 10);

                        if ("01".equals(cfg)) {
                            hasFrontScanner = true;
                        } else if ("03".equals(cfg)) {
                            hasFrontCamera = true;
                        } else if ("10".equals(cfg)) {
                            hasBackCamera = true;
                        } else if ("11".equals(cfg)) {
                            hasBackCamera = true;
                            hasFrontScanner = true;
                        } else if ("12".equals(cfg)) {
                            hasBackCamera = true;
                            hasFrontCamera = true;
                        } else if ("13".equals(cfg)) {
                            hasBackCamera = true;
                        } else if ("14".equals(cfg)) {
                            hasBackCamera = true;
                            hasHardScanner = true;
                            hasFrontCamera = true;
                            hasFrontScanner = true;
                        }else if ("15".equals(cfg)) {
                            hasFrontCamera = true;
                            hasFrontScanner = true;
                            hasHardScanner = true;
                        } else if ("04".equals(cfg)) {
                            hasFrontCamera = true;
                        } else if ("20".equals(cfg)) {
                            hasPaymentCamera = true;
                            // 目前只有 X5 上有这个支付摄像头，如果是这个配置，还得再判断设备里面是否有前后置摄像头
                            String BACK_CAMERA_FILE = "/sys/class/back_camera";
                            String FRONT_CAMERA_FILE = "/sys/class/front_camera";
                            if (isFileExist(BACK_CAMERA_FILE)) {
                                hasBackCamera = true;
                            }
                            if (isFileExist(FRONT_CAMERA_FILE)) {
                                hasFrontCamera = true;
                            }
                        } else {
                            LogUtils.d(TAG, "Unknown scanner config code:" + cfg);
                        }
                    } else {
                        LogUtils.d(TAG, "Hardware config is too short to get scanner config code.");
                    }
                } else {
                    LogUtils.d(TAG, "Hardware config is not supported.");
                }
                return new ScannerConfig(hasFrontCamera, hasBackCamera, hasPaymentCamera, hasFrontScanner, supportSoftDecoding, hasHardScanner);
            }

            @Override
            public String getCustomerID() {
                return getProperties("ro.build.customer_id");
            }

            @Override
            public boolean isSupportGuestDisplay() {
                if (isSupportConfig()) {
                    String CONFIG = NlBuild.VERSION.NL_HARDWARE_CONFIG;
                    if (CONFIG != null && CONFIG.length() >= 38) {
                        String cfg = CONFIG.substring(36, 38);
                        return "01".equals(cfg);
                    }
                }
                return false;
            }

            @Override
            public boolean isSupportBeep() {
                if (isSupportConfig()) {
                    String CONFIG = NlBuild.VERSION.NL_HARDWARE_CONFIG;
                    if (CONFIG != null && CONFIG.length() >= 50) {
                        String cfg = CONFIG.substring(48, 50);
                        return !"FF".equals(cfg);
                    }
                }
                return false;
            }

            @Override
            public int isSupportSubScreen() {
                if (isSupportConfig()) {
                    String CONFIG = NlBuild.VERSION.NL_HARDWARE_CONFIG;
                    if (CONFIG != null && CONFIG.length() >= 36) {
                        String cfg = CONFIG.substring(34, 36);
                        if ("01".equals(cfg)) {
                            return 0x01;
                        }
                        if ("02".equals(cfg)) {
                            return 0x02;
                        }
                        if ("FF".equals(cfg)) {
                            return 0xFF;
                        }
                    }
                }
                return 0xFF;
            }

            @Override
            public ArrayList<ContactCardSlot> getContactCardSlots() {
                ArrayList<ContactCardSlot> slots = new ArrayList<>();
                if (isSupportConfig()) {
                    String CONFIG = NlBuild.VERSION.NL_HARDWARE_CONFIG;
                    if (CONFIG != null) {
                        String cfg;
                        if (CONFIG.length() >= 18) {
                            cfg = CONFIG.substring(16, 18);
                            if ("01".equals(cfg)) {
                                slots.add(ContactCardSlot.IC1);
                            }
                        } else {
                            slots.add(ContactCardSlot.IC1);
                            slots.add(ContactCardSlot.IC2);
                        }
                        if (CONFIG.length() >= 24) {
                            cfg = CONFIG.substring(22, 24);
                            if ("01".equals(cfg)) {
                                slots.add(ContactCardSlot.SAM1);
                            } else if ("02".equals(cfg)) {
                                slots.add(ContactCardSlot.SAM1);
                                slots.add(ContactCardSlot.SAM2);
                            } else if ("03".equals(cfg)) {
                                slots.add(ContactCardSlot.SAM2);
                            } else if ("04".equals(cfg)) {
                                slots.add(ContactCardSlot.SAM1);
                                slots.add(ContactCardSlot.SAM2);
                                slots.add(ContactCardSlot.SAM3);
                            }
                        } else {
                            slots.add(ContactCardSlot.SAM1);
                            slots.add(ContactCardSlot.SAM2);
                            slots.add(ContactCardSlot.SAM3);
                            slots.add(ContactCardSlot.SAM4);
                        }
                    }
                } else {
                    slots.add(ContactCardSlot.IC1);
                    slots.add(ContactCardSlot.IC2);
                    slots.add(ContactCardSlot.SAM1);
                    slots.add(ContactCardSlot.SAM2);
                    slots.add(ContactCardSlot.SAM3);
                    slots.add(ContactCardSlot.SAM4);
                }
                return slots;
            }

            @Override
            public String getDeviceModel() {
                return NlBuild.VERSION.MODEL;
            }

            @Override
            public int getAndroidVersion() {
                return Build.VERSION.SDK_INT;
            }

            @Override
            public int getLEDConfig() {
                if (isSupportConfig()) {
                    String CONFIG = NlBuild.VERSION.NL_HARDWARE_CONFIG;
                    if (CONFIG != null && CONFIG.length() >= 56) {
                        String cfg = CONFIG.substring(54, 56);
                        return ISOUtils.hex2byte(cfg)[0];
                    }
                }
                return 0x00;
            }

            @Override
            public boolean isPhysicalKeyboard() {
                if (isSupportConfig()) {
                    String CONFIG = NlBuild.VERSION.NL_HARDWARE_CONFIG;
                    if (CONFIG != null && CONFIG.length() >= 22) {
                        String cfg = CONFIG.substring(20, 22);
                        return "02".equals(cfg);
                    }
                }
                return false;
            }

            @Override
            public boolean isDualMsr() {
                if (isSupportConfig()) {
                    String CONFIG = NlBuild.VERSION.NL_HARDWARE_CONFIG;
                    if (CONFIG != null && CONFIG.length() >= 16) {
                        String cfg = CONFIG.substring(14, 16);
                        return "02".equals(cfg);
                    }
                }
                return false;
            }
        };
        return deviceInfo;
    }

    /**
     * Get the current device time and date
     *
     * @return Current device time and date
     */
    @Override
    public Date getPOSDate() throws NSDKException {
        if(!isSupported){
            throw new NSDKException(ErrorCode.UNSUPPORTED, "UnSupported DeviceManager Module");
        }

        byte[] date = new byte[15];
        int ret = NSDKJni.getInstance().getDeviceDate(date);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, "Failed to get device date.");
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            return sdf.parse(new String(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Set the device inner time and date
     *
     * @param date
     */
    @Override
    public void setPOSDate(Date date) throws NSDKException {
        if(!isSupported){
            throw new NSDKException(ErrorCode.UNSUPPORTED, "UnSupported DeviceManager Module");
        }

        if (date == null) {
            throw new NSDKIllegalParameterException("Date shall not be null.");
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        SystemClock.setCurrentTimeMillis(cal.getTimeInMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        int ret = NSDKJni.getInstance().setDeviceDate(sdf.format(date).getBytes());
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, "Failed to set device date.");
        }
    }

    /**
     * get sdk version
     *
     * @return
     */
    @Override
    public String getSDKVersion() {
        return BuildConfig.VERSION_NAME;
    }

    /**
     * Whether the terminal has a security module.
     *
     * @return yes if success, false if no.
     */
    @Override
    public boolean isExistSecurityModule() {
        return getHasSecModule();
    }

    @Override
    public TamperStatus getTamperStatus() throws NSDKException {
        if(!isSupported){
            throw new NSDKException(ErrorCode.UNSUPPORTED, "UnSupported DeviceManager Module");
        }

        int[] status = new int[1];

        int ret = NSDKJni.getInstance().getTamperStatus(status);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format("Failed to get tamper status, result code = %d", ret));
        }

        for (TamperStatus s : TamperStatus.values()) {
            if (s.getCode() == status[0]) {
                return s;
            }
        }

        return null;
    }

    @Override
    public TamperStatus[] getTamperStatuses() throws NSDKException {
        if (!isSupported) {
            throw new NSDKException(ErrorCode.UNSUPPORTED, "Unsupported DeviceManager Module.");
        }

        int[] status = new int[1];

        int ret = NSDKJni.getInstance().getTamperStatus(status);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException(ret, String.format(Locale.US, "Failed to get tamper status, result code = %d", ret));
        }
        int tamperStatus = status[0];
        List<TamperStatus> tamperStatusList = new ArrayList<>();
        if (tamperStatus != 0) {
            for (TamperStatus s : TamperStatus.values()) {
                if ((s.getCode() & tamperStatus) == s.getCode()) {
                    tamperStatusList.add(s);
                }
            }
            tamperStatusList.remove(TamperStatus.NONE);
        } else {
            tamperStatusList.add(TamperStatus.NONE);
        }

        return tamperStatusList.toArray(new TamperStatus[0]);
    }

    @Override
    public List<String> getNonDeletableAppList(Context context) throws NSDKException {
        List<String> nonDelAppList = new ArrayList<>();
        if (context == null) {
            throw new NSDKException(ErrorCode.PARAM_ERROR, "Context shall not be null");
        }
        CertificateInfo certificateInfo = new CertificateInfo(context);
        String[] result = certificateInfo.getNonDeleApps();
        for (String s : result) {
            nonDelAppList.add(s);
        }
        
        String sysProperty = "persist.sys.cantuninstall.";
       for (int i = 0; ; i++) {
            String sysPropertyGot = SystemPropertyUtil.getProperty(sysProperty + String.valueOf(i), "unknown");
            if ("unknown".equalsIgnoreCase(sysPropertyGot)) {
                break;
            }
            nonDelAppList.add(sysPropertyGot);
       }

        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        for (PackageInfo pi : packageInfos) {
            if ((pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                nonDelAppList.add(pi.applicationInfo.packageName);
            }
        }
        Iterator<String> it = nonDelAppList.iterator();
        List<String> newNonDelAppList = new ArrayList<>();
        while(it.hasNext()) {
            String s = it.next();
            if (!newNonDelAppList.contains(s)) {
                newNonDelAppList.add(s);
            }
        }

        return newNonDelAppList;
    }

    @Override
    public void setKeyVolume(boolean isOpen) throws NSDKException {
        boolean isSupported = NlBuild.VERSION.NL_HARDWARE_CONFIG.substring(20,22).equals("02");
        if (!isSupported) {
            throw new NSDKException(ErrorCode.UNSUPPORTED, "This method is only supported in devices with physical keyboard.");
        }
        int volume = isOpen ? 1 : 0;
        int ret = NSDKJni.getInstance().setSysBeep_Extern(1, volume);
        if (ret == ErrorCode.UNSUPPORTED) {
            ret = NSDKJni.getInstance().setSysKeyVol(isOpen);
            if (ret != ErrorCode.OK) {
                throw new NSDKException(ret, String.format(Locale.US, "Failed to set key volume, ret = %d", ret));
            }
        } else if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to set key volume, ret = %d", ret));
        }
    }

    @Override
    public void setRadarDetectionDistance(RadarGain radarGain, int delta) throws NSDKException {
        if (delta < 15 || delta > 1022) {
            throw new NSDKException(ErrorCode.PARAM_ERROR, "Delta shall range from 15 to 1022");
        }
        byte gain = 0x3B;
        if (radarGain != null) {
            gain = radarGain.getCode();
        }
        String strGain = ISOUtils.hexString(new byte[] {gain});
        String strDelta = String.valueOf(delta);
        int ret = SerialPortJni.getInstance().setRadarDetectionDistance(strGain, strDelta);
        if (ret == -5) {
            throw new NSDKException(ErrorCode.UNSUPPORTED, "This method is only supported on U2000 devices.");
        } else if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to set radar detection distance, ret = %d", ret));
        }
    }

    @Override
    public void enableRadarAndHeater(boolean isRadarEnable, boolean isHeaterEnable) throws NSDKException {
        int ret = SerialPortJni.getInstance().enableRadarAndHeater(isRadarEnable, isHeaterEnable);
        if (ret == -5) {
            throw new NSDKException(ErrorCode.UNSUPPORTED, "This method is only supported on U2000 devices.");
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to enable radar and heater config, ret = %d", ret));
        }
    }

    @Override
    public TamperReason[] getTamperReason() throws NSDKException {
        TamperReason[] tempReasonArray = new TamperReason[10];
        int[] value = new int[1];
        int ret = NSDKJni.getInstance().NDK_SecGetDrySR(value);
        if (ret != 0) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to getTamperReason[%d]:%s", ret, NSDKModuleManagerImpl.getInstance().getErrMsg(ret)));
        }
        int registerValue = value[0];
        LogUtils.d(TAG, "value:" + registerValue);
        int i = 0;
        if (registerValue == TamperReason.K21_BUTTON_BATTERY_DEAD.getCode()) {
            tempReasonArray[0] = TamperReason.K21_BUTTON_BATTERY_DEAD;
            i++;
        } else if (registerValue == TamperReason.CHIP_3652_BUTTON_BATTERY_DEAD.getCode()) {
            tempReasonArray[0] = TamperReason.CHIP_3652_BUTTON_BATTERY_DEAD;
            i++;
        } else if (registerValue == TamperReason.NONE.getCode()) {
            tempReasonArray[0] = TamperReason.NONE;
            i++;
        } else {
            for (TamperReason t : TamperReason.values()) {
                if ((registerValue & t.getCode()) == t.getCode()) {
                    if (t == TamperReason.K21_BUTTON_BATTERY_DEAD || t == TamperReason.CHIP_3652_BUTTON_BATTERY_DEAD || t == TamperReason.NONE) {
                        continue;
                    }
                    tempReasonArray[i] = t;
                    Log.d(TAG, "t:" + t.name());
                    i++;
                }
            }
        }

        if (i == 0) {
            tempReasonArray[0] = TamperReason.NONE;
            i++;
        }

        TamperReason[] tamperReasons = new TamperReason[i];
        System.arraycopy(tempReasonArray, 0, tamperReasons, 0, i);
        return tamperReasons;
    }

    @Override
    public AntiRemovalStatus getAntiRemovalStatus() throws NSDKException {
        int[] status = new int[1];
        int ret = NSDKJni.getInstance().NAPI_SecGetDeviceStatus(status);
        if (ret == ErrorCode.NOT_SUPPORTED) {
            throw new NSDKException(ErrorCode.NOT_SUPPORTED, "This method is not supported in this device.");
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to get device anti-removal status, ret = %d", ret));
        }
        AntiRemovalStatus antiRemovalStatus = AntiRemovalStatus.ARMED;
        int state = status[0];
        if (state == (1 << 9)) {
            antiRemovalStatus = AntiRemovalStatus.DISARMED;
        } else if (state == (1 << 10)) {
            antiRemovalStatus = AntiRemovalStatus.LOCKED;
        }
        return antiRemovalStatus;
    }

    @Override
    public void setAntiRemovalStatus(AntiRemovalStatus status) throws NSDKException {
        if (status == null) {
            throw new NSDKIllegalParameterException("AntiRemovalStatus shall not be null.");
        }
        int antiRemovalStatus = 0;
        switch (status) {
            case ARMED:
                antiRemovalStatus = 1 << 11;
                break;
            case LOCKED:
                antiRemovalStatus = 1 << 10;
                break;
            case DISARMED:
                antiRemovalStatus = 1 << 9;
                break;
        }
        int ret = NSDKJni.getInstance().NAPI_SecSetDeviceStatus(antiRemovalStatus);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret == ErrorCode.NOT_SUPPORTED) {
            throw new NSDKException(ErrorCode.NOT_SUPPORTED, "This method is not supported in this device.");
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to set device anti-removal status, ret = %d", ret));
        }
    }

    @Override
    public void setEthernetMode(EthernetMode mode) throws NSDKException {
        if (mode == null) {
            throw new NSDKIllegalParameterException("Ethernet mode shall not be null.");
        }
        if (mode == EthernetMode.NON_CONFIGURABLE) {
            throw new NSDKIllegalParameterException("NON_CONFIGURABLE is read only");
        }
        int ret = SerialPortJni.getInstance().setEthernetMode(mode.ordinal());

        if (ret == ErrorCode.IOCTL_ERROR) {
            throw new NSDKException(ErrorCode.NOT_SUPPORTED, "This device is not support setting ethernet mode.");
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to set ethernet mode, ret = %d", ret));
        }
    }

    @Override
    public EthernetMode getEthernetMode() throws NSDKException {
        int[] mode = new int[1];
        int ret = SerialPortJni.getInstance().getEthernetMode(mode);
        if (ret == ErrorCode.IOCTL_ERROR) {
            return EthernetMode.NON_CONFIGURABLE;
        }
        if (ret < 0) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to get ethernet mode, ret = %d", ret));
        }
        int m = mode[0];
        for (EthernetMode ethernetMode : EthernetMode.values()) {
            if (m == ethernetMode.ordinal()) {
                return ethernetMode;
            }
        }
        return EthernetMode.ALL_ON;
    }

    @Override
    public BatteryProperty getBatteryProperty() throws NSDKException {
        byte[] isSupportGetBatteryTemp = new byte[1024];
        int[] isSupportGetBatteryTempLen = new int[1];
        byte[] isSupportGetChargeCurrent = new byte[1024];
        int[] isSupportGetChargeCurrentLen = new int[1];
        byte[] batteryTemp = new byte[1024];
        int[] batteryTempLen = new int[1];
        byte[] adapterVoltage = new byte[1024];
        int[] adapterVoltageLen = new int[1];
        byte[] chargeCurrent = new byte[1024];
        int[] chargeCurrentLen = new int[1];
        int ret = NSDKJni.getInstance().NDK_SysGetBatteryProperty(isSupportGetBatteryTemp, isSupportGetBatteryTempLen, isSupportGetChargeCurrent, isSupportGetChargeCurrentLen, batteryTemp, batteryTempLen, adapterVoltage, adapterVoltageLen, chargeCurrent, chargeCurrentLen);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret == ErrorCode.UNSUPPORTED) {
            throw new NSDKException(ErrorCode.NOT_SUPPORTED, "Not Supported this method in current device.");
        }
        boolean supportGetBatteryTemp = false;
        if (isSupportGetBatteryTempLen[0] > 0) {
            supportGetBatteryTemp = "1".equals(new String(Arrays.copyOf(isSupportGetBatteryTemp, isSupportGetBatteryTempLen[0])));
        }
        boolean supportGetChargeCurrent = false;
        if (isSupportGetChargeCurrentLen[0] > 0) {
            supportGetChargeCurrent = "1".equals(new String(Arrays.copyOf(isSupportGetChargeCurrent, isSupportGetChargeCurrentLen[0])));
        }
        double temperature = 0;
        if (batteryTempLen[0] > 0) {
            String temperatureStr = new String(Arrays.copyOf(batteryTemp, batteryTempLen[0]));
            if (!"-1".equals(temperatureStr)) {
                temperature = (double) (Long.parseLong(temperatureStr) / 10.0);
            } else {
                temperature = -1;
            }

        }
        double voltage = 0;
        if (adapterVoltageLen[0] > 0) {
            String voltageStr = new String(Arrays.copyOf(adapterVoltage, adapterVoltageLen[0]));
            if (!"-1".equals(voltageStr)) {
                voltage = (double) (Long.parseLong(voltageStr) / 1000000.0);
            } else {
                voltage = -1;
            }

        }
        double current = 0;
        if (chargeCurrentLen[0] > 0) {
            String currentStr = new String(Arrays.copyOf(chargeCurrent, chargeCurrentLen[0]));
            if (!"-1".equals(currentStr)) {
                current = (double) (Long.parseLong(currentStr) / 1000000.0);
            } else {
                current = -1;
            }

        }
        return new BatteryProperty(temperature, voltage, current, supportGetBatteryTemp, supportGetChargeCurrent);
    }

    @Override
    public void setDeviceLightMode(DeviceLight deviceLight, LightMode lightMode, Integer flashingInterval) throws NSDKException {
        if (deviceLight == null || lightMode == null) {
            throw new NSDKIllegalParameterException("Device light and its light mode shall not be null.");
        }
        if (lightMode == LightMode.BLINK && flashingInterval == null) {
            throw new NSDKIllegalParameterException("Flashing interval shall not be null when light mode is blink.");
        }

        int interval = 0;
        if (lightMode == LightMode.BLINK) {
            interval = flashingInterval;
        }
        int ret = NSDKJni.getInstance().NDK_LedFuncModeSet(deviceLight.ordinal(), interval);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to set device light mode, ret = %d", ret));
        }
    }

    @Override
    public void setLongPressButtons(int keyMask, boolean enableLongPress) throws NSDKException {
        if (getDeviceInfo().isPhysicalKeyboard()) {
            int status = enableLongPress ? 1 : 0;
            int ret = NSDKJni.getInstance().NDK_SysSetKeyLongPress(keyMask, status);
            if (ret == ErrorCode.PARAM_ERROR) {
                throw new NSDKIllegalParameterException();
            }
            if (ret != ErrorCode.OK) {
                throw new NSDKException(ret, "Set key long press failed");
            }
        } else {
            throw new NSDKException(ErrorCode.NOT_SUPPORTED, "Not supported this method in current device.");
        }
    }

    public String getContactlessVersion() throws NSDKException {
        byte[] version = new byte[50];
        int ret = NSDKJni.getInstance().RFGetVersion(version.length, version);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format("Failed to get contactless card module version, result code = %d", ret));
        }
        String versionStr = new String(version).trim();
        LogUtils.d(TAG, String.format(">>>Contactless card module version = %s", versionStr));
        return versionStr;
    }

    private boolean isFileExist(String path){
        File file = new File(path);
        return file.exists();
    }

    private boolean isSupportHCEByHardware() {
        if (isSupportConfig()) {
            String CONFIG = NlBuild.VERSION.NL_HARDWARE_CONFIG;
            if (CONFIG != null && CONFIG.length() > 14) {
                String cfg = CONFIG.substring(12, 14);
                return "07".equals(cfg) || "08".equals(cfg);
            }
        }
        return false;
    }
}
