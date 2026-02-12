package com.newland.sdk.me2.module.devicebasic;

import android.content.Context;
import android.newland.os.NlBuild;
import android.os.Build;
import android.os.SystemClock;

import com.newland.ndk.NdkApiManager;
import com.newland.ndk.SecN;
import com.newland.sdk.me.cmd.common.CmdDeviceGetTime;
import com.newland.sdk.me.cmd.common.CmdDeviceSetTime;
import com.newland.sdk.me.cmd.common.CmdGetDeviceParams;
import com.newland.sdk.me.cmd.common.CmdSetDeviceParams;
import com.newland.sdk.me.utils.DeviceInfoUtils;
import com.newland.sdk.me2.cmd.devicebasic.CmdCancelAndReset;
import com.newland.sdk.me2.cmd.devicebasic.CmdRandom;
import com.newland.sdk.me2.cmd.devicebasic.CmdSetCSN;
import com.newland.sdk.me2.cmd.devicebasic.CmdGetDeviceInfo;
import com.newland.sdk.module.devicebasic.DeviceBasicModule;
import com.newland.sdk.module.devicebasic.DeviceInfo;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.AbstractModule;
import com.newland.sdk.mtypex.conn.DeviceExecutor;
import com.newland.sdk.mtypex.nseries.NSeriesDeviceExecutor;
import com.newland.sdk.utils.TLVPackage;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

public class MEDeviceBasic extends AbstractModule implements DeviceBasicModule {

    private DeviceLogger deviceLogger = DeviceLoggerFactory.getLogger("MEDeviceBasic");
    private static String sdkVersion = null;
    private static Properties sdkProperties;
    private Context context;
    private NSeriesDeviceExecutor deviceExecutor;

    public MEDeviceBasic(AbstractDevice device, DeviceExecutor deviceExecutor) {
        super(device);
        this.context = deviceExecutor.getContext();
        this.deviceExecutor = (NSeriesDeviceExecutor)deviceExecutor;
    }

    @Override
    public boolean isStandardModule() {
        return true;
    }

    @Override
    public ModuleType getStandardModuleType() {
        return ModuleType.DEVICE_BASIC;
    }

    @Override
    public String getExModuleType() {
        return null;
    }

    @Override
    public DeviceInfo getDeviceInfo() {
        CmdGetDeviceInfo.CmdGetDeviceInfoResponse response = (CmdGetDeviceInfo.CmdGetDeviceInfoResponse) invoke(new CmdGetDeviceInfo());
        return response.getDeviceInfo();
    }

    @Override
    public String getTusn() {
        String deviceType = "04";// 01 ATM, 02 传统POS, 03 MPOS, 04 智能POS   09人脸设备
        if (isSupFaceRecognition()) {
            deviceType = "09";
        }
        String serialNo = "000003";// 新大陆厂商序号 000003
        String sn = getDeviceInfo().getSN();
        String tusn = serialNo + deviceType + sn;
        // 人行二次改造，判定文件存在则进行下一步验证，否则返回不支持
        File file = new File("/newland/factory/flag_sn_20");
        if (!file.exists()) {
            deviceLogger.debug("[getTusn] 文件不存在"+sn);
            return sn;
        } else {
            deviceLogger.debug("[getTusn] 文件存在"+tusn);
            return tusn;
        }
    }

    @Override
    public void setCSN(String csn) {
        invoke(new CmdSetCSN((byte)0x04,csn));
    }

    @Override
    public void setDeviceDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        SystemClock.setCurrentTimeMillis(cal.getTimeInMillis());
        invoke(new CmdDeviceSetTime(date));
    }

    @Override
    public Date getDeviceDate() {
        CmdDeviceGetTime.GetTimeResponse deviceResponse = (CmdDeviceGetTime.GetTimeResponse) invoke(new CmdDeviceGetTime());
        return deviceResponse.getDeviceDate();
    }

    @Override
    public void setDeviceParams(TLVPackage tlvPackage) {
        invoke(new CmdSetDeviceParams(tlvPackage.pack()));
    }

    @Override
    public TLVPackage getDeviceParams(int... tags) {
        CmdGetDeviceParams.CmdGetDeviceParamsResponse response = (CmdGetDeviceParams.CmdGetDeviceParamsResponse) invoke(new CmdGetDeviceParams(tags));
        return response.getParamsContent();
    }

    @Override
    public byte[] getRandom(int len) {
        byte[] random = new byte[513];
        if(len > random.length -1 ){
            return null;
        }
        SecN secN = NdkApiManager.getNdkApiManager().getSecN();
        int ret = secN.NDK_SecGetRandom(len, random);
        if(ret != 0){
            return null;
        }
        byte[] target = new byte[len];
        System.arraycopy(random,0,target,0,len);
        return target;
    }

    @Override
    public void reset() {
        try {
            deviceExecutor.cancelCurrentExecCmd();
        } finally {
            try {
                deviceExecutor.directInvoke(new CmdCancelAndReset());
            } catch (Exception e) {

            }
        }
    }

    @Override
    public String getSDKVersion() {
        if (sdkVersion == null)
            initSDKVersion();
        return sdkVersion;
    }

    @Override
    public boolean hasSecurityModule() {
        return DeviceInfoUtils.getHasSecModule();
    }

    @Override
    public String getBatteryHealthStatus() {
        return null;
    }

    private void initSDKVersion() {
        if (sdkProperties == null) {
            try {
                Properties p = new Properties();
                URL url = getClass().getClassLoader().getResource("sdk.properties");
                InputStream inputStream = null;
                if(url != null){
                    inputStream = url.openStream();
                }
                if(inputStream == null){
                    inputStream = this.getClass().getClassLoader().getResourceAsStream("sdk.properties");
                }
                if (inputStream == null)
                    return;
                else {
                    p.load(inputStream);
                    sdkProperties = p;
                }
            } catch (Exception e) {
                deviceLogger.error("load sdkProperties failed!", e);
            }
        }
        try {
            if (sdkProperties == null)
                return;
            sdkVersion = sdkProperties.getProperty("mesdk.version");
        } catch (Exception e) {
            deviceLogger.error("failed to init sdk version!", e);
        }
    }

    /**
     * 是否支持人脸识别
     * @return
     */
    private boolean isSupFaceRecognition(){
        try {
            String config = NlBuild.VERSION.NL_HARDWARE_CONFIG;// 硬件配置码
            deviceLogger.debug("[isSupFaceRecognition] config:"+config);
            if (config != null && config.length() >= 46) {
                String faceRecognitionParam = config.substring(44, 46);
                deviceLogger.debug("[isSupFaceRecognition] faceRecognitionParam:"+faceRecognitionParam);
                if ("01".equals(faceRecognitionParam) || "02".equals(faceRecognitionParam) || "03".equals(faceRecognitionParam)) {
                    return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
