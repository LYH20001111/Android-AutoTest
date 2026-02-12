package com.newland.sdk.me.module.devicebasic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.SystemClock;

import com.newland.intelligent.jni.JniCmdInterface;
import com.newland.sdk.mtypex.module.common.emv.CommonUtils;
import com.newland.sdk.me.cmd.common.CmdDeviceGetTime;
import com.newland.sdk.me.cmd.common.CmdDeviceSetTime;
import com.newland.sdk.me.cmd.common.CmdGetDeviceInfo;
import com.newland.sdk.me.cmd.common.CmdGetDeviceParams;
import com.newland.sdk.me.cmd.common.CmdGetTusn;
import com.newland.sdk.me.cmd.common.CmdRandom;
import com.newland.sdk.me.cmd.common.CmdSetCSN;
import com.newland.sdk.me.cmd.common.CmdSetDeviceParams;
import com.newland.sdk.me.utils.DeviceInfoUtils;
import com.newland.sdk.module.devicebasic.DeviceBasicModule;
import com.newland.sdk.module.devicebasic.DeviceInfo;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.AbstractModule;
import com.newland.sdk.utils.TLVPackage;

import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.Date;

public class MEDeviceBasic extends AbstractModule implements DeviceBasicModule {

    private DeviceLogger deviceLogger = DeviceLoggerFactory.getLogger("MEDeviceBasic");

    private Context context;
    public MEDeviceBasic(AbstractDevice device, Context context) {
        super(device);
        this.context = context;
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
        deviceLogger.debug("[DeviceInfo]");
        if (!DeviceInfoUtils.getHasSecModule()) {
            CmdGetDeviceInfo.CmdGetDeviceInfoResponse response = new CmdGetDeviceInfo.CmdGetDeviceInfoResponse();
            return response.getDeviceInfo();
        }
        CmdGetDeviceInfo.CmdGetDeviceInfoResponse response = (CmdGetDeviceInfo.CmdGetDeviceInfoResponse) invoke(new CmdGetDeviceInfo());
        return response.getDeviceInfo();
    }

    @Override
    public String getTusn() {
        deviceLogger.debug("[getTusn]");
        CmdGetTusn.CmdTusnResponse response = (CmdGetTusn.CmdTusnResponse) invoke(new CmdGetTusn());
        return response.getPosTusn();
    }

    @Override
    public void setCSN(String csn) {
        deviceLogger.debug("[setCSN] csn:"+csn);
        if (!DeviceInfoUtils.getHasSecModule()) {
            return;
        }
        invoke(new CmdSetCSN((byte) 0x04, csn));
    }

    @Override
    public void setDeviceDate(Date date) {
        deviceLogger.debug("[setDeviceDate]");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        SystemClock.setCurrentTimeMillis(cal.getTimeInMillis());
        int version = Build.VERSION.SDK_INT;
        if(version<25) {//A7以上不需要K21再设置一次
            invoke(new CmdDeviceSetTime(date));
        }
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
        CmdRandom.CmdRandomResponse response = (CmdRandom.CmdRandomResponse) invoke(new CmdRandom(len));
        return response.getRandom();
    }

    @Override
    public void reset() {
        JniCmdInterface.getInstance().jniMposLibCmdCancel(4);
    }

    @Override
    public String getSDKVersion() {
        return CommonUtils.getInstance().getSDKVersion();
    }

    @Override
    public boolean hasSecurityModule() {
        return DeviceInfoUtils.getHasSecModule();
    }

    @Override
    public String getBatteryHealthStatus() {
        return new BatteryHealth(context).getBatteryHealthStatus();
    }
    public class BatteryHealth{
        private BatteryStatusReceiver batteryStatusReceiver;
        private Object batteryObj = null;
        private String batterStatus = null;

        private Context context;
        public BatteryHealth(Context context){
            this.context = context;
        }

        public String getBatteryHealthStatus(){
            try {
                int sdkInt = Build.VERSION.SDK_INT;
                deviceLogger.debug("getBatteryHealthStatus sdkInt="+sdkInt);
                if (sdkInt >= 31) {// Android 12 (API 31)
                    byte[] data = readFileByBytes("/sys/class/nl_bhd/state_string");
                    String[] string = new String(data).trim().split(",");
                    if (string != null && string.length == 2) {
                        deviceLogger.debug("getBatteryHealthStatus string[0]:"+string[0]);
                        switch (string[0]) {
                            case "RED":
                                return "RED";
                            case "YELLOW":
                                return "YELLOW";
                            default:
                                return "GREEN";
                        }
                    } else if ("no error".equals(new String(data).trim())) {
                        deviceLogger.debug("getBatteryHealthStatus GREEN.");
                        return "GREEN";
                    }
                }else {
                    batteryStatusReceiver = new BatteryStatusReceiver();
                    IntentFilter intentFilter = new IntentFilter();
                    intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
                    this.context.registerReceiver(batteryStatusReceiver,intentFilter);
                    batteryObj = new Object();
                    synchronized(batteryObj){
                        batteryObj.wait(3*1000);
                    }
                    if(batteryStatusReceiver != null){
                        this.context.unregisterReceiver(batteryStatusReceiver);
                        batteryStatusReceiver = null;
                    }
                    deviceLogger.debug("getBatteryHealthStatus batterStatus="+batterStatus);
                    return batterStatus;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            deviceLogger.debug("getBatteryHealthStatus null.");
            return null;
        }

        public class BatteryStatusReceiver extends BroadcastReceiver {
            public BatteryStatusReceiver() {
            }

            @Override
            public void onReceive(final Context context, Intent intent) {
                try {
                    String action = intent.getAction();
                    if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                        context.unregisterReceiver(batteryStatusReceiver);
                        batteryStatusReceiver = null;
                        int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN);
                        deviceLogger.debug("BatteryStatusReceiver onReceive health="+health);
                        switch (health) {
                            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                            case 10:
                                batterStatus = "YELLOW";
                                synchronized(batteryObj){
                                    batteryObj.notifyAll();
                                }
                                break;
                            case BatteryManager.BATTERY_HEALTH_DEAD:
                            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                                batterStatus = "RED";
                                synchronized(batteryObj) {
                                    batteryObj.notifyAll();
                                }
                                break;
                            default:
                                batterStatus = "GREEN";
                                synchronized(batteryObj){
                                    batteryObj.notifyAll();
                                }
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private byte[] readFileByBytes(String filePath) throws Exception {
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

}
