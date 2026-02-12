package com.newland.sdk.me.cmd.common;

import android.newland.os.NlBuild;
import android.os.Build;

import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.me.cmd.serializer.ByteSerializer;
import com.newland.sdk.me.cmd.serializer.IntegerSerializer;
import com.newland.sdk.me.cmd.serializer.StringSerializer;
import com.newland.sdk.me.utils.DeviceInfoUtils;
import com.newland.sdk.module.devicebasic.DeviceInfo;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.utils.ISOUtils;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;
import com.newland.ndk.NdkApiManager;
import com.newland.ndk.h.EM_SYS_HWINFO;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import static com.newland.ndk.h.EM_SYS_HWINFO.SYS_HWINFO_GET_POS_PSN;

@CommandEntity(cmdCode = {(byte) 0xF1, (byte) 0x01}, responseClass = CmdGetDeviceInfo.CmdGetDeviceInfoResponse.class)
public class CmdGetDeviceInfo extends CommonDeviceCommand {

    private static DeviceLogger logger = DeviceLoggerFactory.getLogger(CmdGetDeviceInfo.class);

    @ResponseEntity
    public static class CmdGetDeviceInfoResponse extends AbstractSuccessResponse {

        @InstructionField(name = "设备硬件编号", index = 0, fixLen = 12, maxLen = 12, serializer = StringSerializer.class)
        private String sn;

        @InstructionField(name = "设备个人化状态", index = 1, fixLen = 1, maxLen = 1, serializer = ByteSerializer.class)
        private byte personalizationState;

        @InstructionField(name = "应用版本", index = 2, fixLen = 16, maxLen = 16, serializer = StringSerializer.class)
        private String appVersion;

        @InstructionField(name = "设备应用编号(UDID)", index = 3, fixLen = 10, maxLen = 10, serializer = StringSerializer.class)
        private String udId;

        @InstructionField(name = "设备状态", index = 4, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
        private Integer deviceState;

        @InstructionField(name = "固件编号", index = 5, fixLen = 16, maxLen = 16, serializer = StringSerializer.class)
        private String firmwareVersion;

        @InstructionField(name = "客户序列号（CSN）", index = 6, maxLen = 100, serializer = ByteArrSerializer.class)
        private byte[] csn;
        @InstructionField(name = "密钥序列号（KSN）", index = 7, maxLen = 40, serializer = ByteArrSerializer.class)
        private byte[] ksn;
        @InstructionField(name = "产品ID", index = 8, fixLen = 2, maxLen = 2, serializer = ByteArrSerializer.class)
        private byte[] pid;
        @InstructionField(name = "厂商ID", index = 9, fixLen = 2, maxLen = 2, serializer = StringSerializer.class)
        private String vid;
        @InstructionField(name = "生产SN号", index = 10, maxLen = 40, serializer = StringSerializer.class)
        private String customSN;
        @InstructionField(name = "Boot版本", index = 11, maxLen = 40, serializer = StringSerializer.class)
        private String bootVersion;

        public DeviceInfo getDeviceInfo() {
            DeviceInfo deviceInfo = new DeviceInfo() {

                @Override
                public String getSN() {
                    try {
                        if (!DeviceInfoUtils.getHasSecModule()) {
                            String sn = "", tusn = NlBuild.TUSN;
                            int[] len = new int[1];
                            byte[] usn = new byte[128];
                            NdkApiManager ndkApiManager = NdkApiManager.getNdkApiManager();
                            int ret = ndkApiManager.getSysN().NDK_SysGetPosInfo(EM_SYS_HWINFO.SYS_HWINFO_GET_POS_USN, len, usn);
                            if (ret == 0) {
                                sn = new String(usn).trim();
                            } else {
                                if (tusn.substring(0, 8).equals("00000304")) {
                                    sn = tusn.substring(8, tusn.length());
                                }
                            }
                            logger.debug(">>>sn=" + sn);
                            return sn;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                    return sn;
                }

                @Override
                public String getFirmwareVer() {
                    return NlBuild.VERSION.NL_FIRMWARE;
                }

                public String getAppVer() {
                    return appVersion;
                }

                @Override
                public String getVID() {
                    return vid;
                }

                public String getCustomSN() {
                    return customSN;
                }
                @Override
                public String getKSN() {
                    return null == ksn ? null : ISOUtils.hexString(ksn);
                }

                @Override
                public String getSeFwVersion() {
                    logger.debug("SE FW Version:"+firmwareVersion);
                    return firmwareVersion;
                }

                public String toString() {
                    StringBuilder sb = new StringBuilder();
                    sb.append("deviceInfo:[");
                    sb.append("sn:" + getSN() + ",");
                    sb.append("appVer:" + getAppVer() + ",");
                    sb.append("csn:" + (null == getCSN() ? null : ISOUtils.hexString(getCSN())) + ",");
                    sb.append("vid:" + getVID() + ",");
                    sb.append("customSN:" + getCustomSN() + ",");
                    sb.append("isSupportAudio:" + isSupportAudio() + ",");
                    sb.append("isSupportBlueTooth:" + isSupportBlueTooth() + ",");
                    sb.append("isSupportUSB:" + isSupportUSB() + ",");
                    sb.append("isSupportMagCard:" + isSupportMagCard() + ",");
                    sb.append("isSupportICCard:" + isSupportICCard() + ",");
                    sb.append("isSupportQuickPass:" + isSupportQuickPass() + ",");
                    sb.append("isSupportPrint:" + isSupportPrint() + ",");
                    sb.append("isSupportLCD:" + isSupportLCD() + ",");
                    sb.append("isSupport232Port:" + isSupport232Port() + ",");
                    sb.append("isSupportGuestDisplay:" + isSupportGuestDisplay() + ",");
                    sb.append("isSupportSubscreen:" + isSupportSubscreen() + ",");
                    sb.append("isSupportCashBox:" + isSupportCashBox() + ",");
                    sb.append("isSupportCamera:" + isSupportCamera() + ",");
                    sb.append("isSupportEthernet:" + isSupportEthernet() + ",");
                    sb.append("isSupportGPS:" + isSupportGPS() + ",");
                    sb.append("isSupportPinpadPort:" + isSupportPinpadPort() + ",");
                    sb.append("isSupportSam:" + isSupportSam() + ",");
                    sb.append("isSupportOffLine:" + isSupportOffLine() + ",");
                    sb.append("getCustomerID:" + getCustomerID() + ",");
                    sb.append("firmwareVer:" + getFirmwareVer() + "]");
                    return sb.toString();
                }

                @Override
                public boolean isSupportAudio() {
                    return isSupport(1);
                }

                @Override
                public boolean isSupportBlueTooth() {
                    return isSupport(2);
                }

                @Override
                public boolean isSupportMagCard() {
                    return isSupport(4);
                }

                @Override
                public boolean isSupportICCard() {
                    return isSupport(5);
                }

                @Override
                public boolean isSupportQuickPass() {
                    return isSupport(6);
                }

                @Override
                public boolean isSupportPrint() {
                    String model = Build.MODEL;
                    logger.debug("model=" + model);
                    if (matchModel(new String[]{"CPOS X3","CPOS X5","CPOS X1","STAR A-6300"})) {
                        String isSupPrint = getProperties("persist.sys.HasPrnModule");
                        logger.debug("isSupPrint:" + isSupPrint);
                        if ("yes".equals(isSupPrint)) {
                            return true;
                        }
                        return false;
                    }

                    return isSupport(7);
                }

                @Override
                public boolean isSupportLCD() {
                    return isSupport(8);
                }

                @Override
                public String getBootVersion() {
                    return bootVersion;
                }

                @Override
                public boolean isSupportOffLine() {
                    return isSupport(3);
                }

                @Override
                public byte[] getCSN() {
                    return csn;
                }

                @Override
                public String getModel() {
                    return NlBuild.VERSION.MODEL;
                }

                @Override
                public boolean isSupportUSB() {

                    if (isSupportConfig()) {
                        String CONFIG = NlBuild.VERSION.NL_HARDWARE_CONFIG;
                        if (CONFIG != null && CONFIG.length() >= 34) {
                            String cfg = CONFIG.substring(32, 34);
                            if ("10".equals(cfg)||"11".equals(cfg)||"21".equals(cfg)||"40".equals(cfg)) {
                                return true;
                            }
                        }
                    }
                    if(matchModel(new String[]{"N850", "CPOS X3","CPOS X5","CPOS X1","F7","STAR A-6300"})){
                        return true;
                    }
                    return false;
                }

                @Override
                public boolean isSupportGPS() {
                    if (isSupportConfig()) {
                        String CONFIG = NlBuild.VERSION.NL_HARDWARE_CONFIG;
                        if (CONFIG != null && CONFIG.length() >=12) {
                            String cfg = CONFIG.substring(10, 12);
                            if ("01".equals(cfg)) {
                                return true;
                            }
                        }else if(CONFIG != null && CONFIG.length()==10){//若长度=3,硬件识别码第3个字节返回值：01，则设备无GPS, 13则设备有GPS
                            String cfg = CONFIG.substring(8, 10);
                            if ("13".equals(cfg)) {
                                return true;
                            }
                        }
                    }
                    return false;
                }

                @Override
                public boolean isSupportEthernet() {
                    if (isSupportConfig()) {
                        String CONFIG = NlBuild.VERSION.NL_HARDWARE_CONFIG;
                        if (CONFIG != null && CONFIG.length() >= 28) {
                            String cfg = CONFIG.substring(26, 28);
                            if ("01".equals(cfg)) {
                                return true;
                            }
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
                            if ("01".equals(cfg)) {
                                return true;
                            }
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
                            if ("01".equals(cfg) || "02".equals(cfg) || "03".equals(cfg)) {
                                return true;
                            }
                        }
                    }
                    return false;
                }

                @Override
                public boolean isSupportPinpadPort() {
                    if (isSupportConfig()) {
                        String CONFIG = NlBuild.VERSION.NL_HARDWARE_CONFIG;
                        if (CONFIG != null && CONFIG.length() >= 32) {
                            String cfg = CONFIG.substring(30, 32);
                            if ("02".equals(cfg) || "03".equals(cfg) || "04".equals(cfg)) {//判断有2个外置串口就认为它支持外接密码键盘(x5，走NLUART3)
                                return true;
                            }
                        } else {
                            if (matchModel(new String[]{"N850", "F7"}))
                                return true;
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
                            if ("01".equals(cfg) || "03".equals(cfg) || "04".equals(cfg)) {
                                return true;
                            }
                        } else {
                            if (matchModel(new String[]{"N850", "F7", "N550", "CPOS X3", "CPOS X5","CPOS X3","STAR A-6300"}))
                                return true;
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
                            if ("01".equals(cfg)||"02".equals(cfg)||"03".equals(cfg)) {
                                return true;
                            }
                        }else if(CONFIG != null && CONFIG.length() >= 10){//摄像头类型
                            String cfg = CONFIG.substring(8, 10);
                            if ("10".equals(cfg)||"11".equals(cfg)||"12".equals(cfg)||"13".equals(cfg)||"04".equals(cfg)||"20".equals(cfg)) {
                                return true;
                            }
                        }
                    }
                    return false;
                }

                @Override
                public String getCustomerID() {
                    String version = "unknown";
                    /**
                     * ro.build.newland_sdk 后续固件版本增加的属性值
                     *
                     */
                    version = getProperties("ro.build.newland_sdk");
                    if ("unknown".equals(version)) {
                        version = getProperties("ro.build.customer_id");
                        if ("unknown".equals(version)) {
                            // 根据MTMS之前的规则判断
                            //20180719，SDK 2.0： SDK 2.0分支、银商专用、阿里千牛，其他的都是SDK 3.0。
                            return version;
                        } else if ("ChinaUms".equals(version) || "SDK_2.0".equals(version) || "AliQianNiu".equals(version)) {
                            return "SDK2.0";
                        } else {
                            return "SDK3.0";
                        }
                    } else {
                        return version;
                    }
                }

                @Override
                public boolean isSupportGuestDisplay() {
                    if (isSupportConfig()) {
                        String CONFIG = NlBuild.VERSION.NL_HARDWARE_CONFIG;
                        if (CONFIG != null && CONFIG.length() >= 38) {
                            String cfg = CONFIG.substring(36, 38);
                            if ("01".equals(cfg)) {
                                return true;
                            }
                        }
                    }
                    return false;
                }

                @Override
                public int isSupportSubscreen() {
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
                    return 0x00;
                }

                @Override
                public String getPN() {
                    try {
                        int[] len = new int[1];
                        byte[] psn = new byte[128];
                        int ret = NdkApiManager.getNdkApiManager().getSysN().NDK_SysGetPosInfo(SYS_HWINFO_GET_POS_PSN,len,psn);
                        if(ret == 0){
                            byte[] posPsn = new byte[len[0]];
                            System.arraycopy(psn,0,posPsn,0,posPsn.length);
                            String pn = new String(posPsn).trim();
                            return pn;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return "";
                }

                @Override
                public String getPCIVersion() {
                    try {
                        String version = getProperties("ro.build.version.pci_firmware");
                        logger.debug("-----PCI version:"+version);
                        return version;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return "";
                }

            };
            return deviceInfo;
        }

        private boolean matchModel(String[] models) {
            for (String model : models) {
                if (Build.MODEL.equalsIgnoreCase(model))
                    return true;
            }
            return false;
        }

        private boolean isSupport(int index) {
            if (!DeviceInfoUtils.getHasSecModule()) {
                deviceState = 0;
            }
            if (null == deviceState) {
                throw new UnsupportedOperationException("hardware not support this method yet!");
            }
            int temp = (deviceState >> (8 - index)) & 0x01;
            if (temp == 1) {
                return true;
            }
            return false;
        }

        /**
         * 获取系统属性值
         *
         * @param key
         * @return 返回值 unknown  表示属性值不存在。
         * 其他返回具体的属性值
         */

        private static String getProperties(String key) {
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

    }

    private static boolean isSupportConfig() {
        String version = NlBuild.VERSION.NL_FIRMWARE;
        version = version.replaceAll("V", "").replace("T", "");
        if ("SA1".equals(NlBuild.VERSION.NL_HARDWARE_ID)) {// 硬件识别码
            return "1.1.12".compareToIgnoreCase(version) <= 0; //3G版本1.1.12之前不支持硬件配置码
        }
        return true;
    }

    public static byte[] readFileByBytes(String filePath) throws Exception {
        File file = new File(filePath);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            byte[] fileBytes = new byte[(int) file.length()];
            fis.read(fileBytes);
            return fileBytes;
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }
}

