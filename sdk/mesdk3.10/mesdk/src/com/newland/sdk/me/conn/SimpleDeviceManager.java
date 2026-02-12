package com.newland.sdk.me.conn;

import android.content.Context;
import android.newland.os.NlBuild;
import android.os.Build;
import android.os.Handler;

import com.newland.intelligent.jni.JniCmdInterface;
import com.newland.sdk.mtypex.module.common.emv.CommonUtils;
import com.newland.sdk.me.DeviceManager;
import com.newland.sdk.me.K21Driver;
import com.newland.sdk.me.ME3xDriver;
import com.newland.sdk.me.module.emvl3.impl.EmvL3Comm;
import com.newland.sdk.me.module.emvl3.impl.MEEmvL3;
import com.newland.sdk.me.module.externalPininput.PinpadPackage;
import com.newland.sdk.mtype.ConnectionCloseEvent;
import com.newland.sdk.mtype.Device;
import com.newland.sdk.mtype.DeviceDriver;
import com.newland.sdk.mtype.MposParams;
import com.newland.sdk.mtype.conn.DeviceConnParams;
import com.newland.sdk.mtype.event.DeviceEventListener;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.bluetooth.BlueToothConnParams;
import com.newland.sdk.mtypex.nseries.NSConnV100ConnParams;
import com.newland.sdk.mtypex.nseries3.NS3ConnParams;
import com.newland.sdk.mtypex.usb.UsbConnParams;

import java.lang.reflect.Method;

/**
 * 设备管理,只维持一个单例<p>
 *
 * @since ver3.10.01
 */
public class SimpleDeviceManager implements DeviceManager {

    private static SimpleDeviceManager instance;

    private static DeviceLogger deviceLogger = DeviceLoggerFactory.getLogger("SimpleDeviceManager");

    private Device device;
    private DeviceDriver driver;
    private DeviceConnState state = DeviceConnState.NOT_INIT;

    private Context context;
    private DeviceConnParams params;

    private DisconnectThread disconnectThread;
    public static Object externalLock = new Object();
    private static MposParams mMposParams;

    private SimpleDeviceManager() {
    }

    public static final DeviceManager getInstance() {
        deviceLogger.debug("SimpleDeviceManager: " + instance);
        if (instance == null) {
            synchronized (deviceLogger) {
                if (instance == null) {
                    instance = new SimpleDeviceManager();
                }
            }
        }
        return instance;
    }

    @Override
    public void init(Context context) {
        destroy();
        synchronized (deviceLogger) {
            if (state != DeviceConnState.NOT_INIT) {
                deviceLogger.info("[init]not expected state to init!" + state);
                return;
            }
            this.context = context;
            state = DeviceConnState.DISCONNCECTED;
            if (mMposParams != null) {
                driver = new ME3xDriver();
                switch (mMposParams.getConnectType()) {
                    case BLUETOOTH:
                        String macAddress = mMposParams.getAddress();
                        deviceLogger.debug("[init] macAddress=" + macAddress);
                        params = new BlueToothConnParams(macAddress);
                        break;
                    case USB:
                        params = new UsbConnParams();
                        break;
                    default:
                        throw new RuntimeException("Unsupport this connection type:" + mMposParams.getConnectType());
                }
            } else {
                driver = new K21Driver();
                initConnParams();
            }
        }
    }

    private void initConnParams() {
        String current_driver_version = NlBuild.VERSION.NL_FIRMWARE;
        deviceLogger.debug("[initConnParams]version: " + SDKVersion() + "current_driver_version:" + current_driver_version);
        if (SDKVersion().trim().equalsIgnoreCase("SDK2.0")) {
            this.params = new NSConnV100ConnParams();
        } else if (SDKVersion().trim().equalsIgnoreCase("SDK3.0") || SDKVersion().trim().equalsIgnoreCase("CHS")) {//医保的F10设备命名CHS
            this.params = new NS3ConnParams();
        } else if (SDKVersion().trim().equalsIgnoreCase("Overseas")) {
            this.params = new NS3ConnParams();
        } else if (SDKVersion().trim().equalsIgnoreCase("Brasil")) {
            this.params = new NS3ConnParams();
        } else {
            if ("SA1".equals(NlBuild.VERSION.NL_HARDWARE_ID) && Build.MODEL.equals("N900")) { //900 3G设备只支持2.0
                this.params = new NSConnV100ConnParams();
                return;
            }
            if (Build.MODEL.equals("N900")) {
                if (current_driver_version.equals("V2.0.28") || current_driver_version.equals("V2.1.03") || current_driver_version.equals("V2.1.05") || current_driver_version.equals("V2.1.09") || current_driver_version.equals("V2.1.18") || current_driver_version.equals("V2.1.27") || current_driver_version.equals("V2.1.37") || current_driver_version.equals("V2.1.49") || current_driver_version.equals("V2.1.53") || current_driver_version.equals("V2.1.58")
                        || current_driver_version.equals("V2.1.62") || current_driver_version.equals("V2.0.16") || current_driver_version.equals("V2.1.17") || current_driver_version.equals("V2.1.21") || current_driver_version.equals("V2.1.23") || current_driver_version.equals("V2.1.24") || current_driver_version.equals("V2.1.29") || current_driver_version.equals("V2.1.31") || current_driver_version.equals("V2.1.32") || current_driver_version.equals("V2.1.40") || current_driver_version.equals("V2.1.41")
                        || current_driver_version.equals("V2.1.44") || current_driver_version.equals("V2.0.45") || current_driver_version.equals("V2.1.46") || current_driver_version.equals("V2.1.48") || current_driver_version.equals("V2.1.51") || current_driver_version.equals("V2.1.55") || current_driver_version.equals("V2.1.56") || current_driver_version.equals("V2.1.60")) {
                    this.params = new NS3ConnParams();
                } else {
                    this.params = new NSConnV100ConnParams(); //默认2.0连接
                }

            } else if (Build.MODEL.equals("N910")) {
                if (current_driver_version.equals("V2.0.28") || current_driver_version.equals("V2.1.03") || current_driver_version.equals("V2.1.05") || current_driver_version.equals("V2.1.09") || current_driver_version.equals("V2.1.18") || current_driver_version.equals("V2.1.27") || current_driver_version.equals("V2.1.35") || current_driver_version.equals("V2.1.40") || current_driver_version.equals("V2.1.52") || current_driver_version.equals("V2.1.54")
                        || current_driver_version.equals("V2.1.64") || current_driver_version.equals("V2.0.16") || current_driver_version.equals("V2.1.13") || current_driver_version.equals("V2.1.21") || current_driver_version.equals("V2.1.23") || current_driver_version.equals("V2.1.24") || current_driver_version.equals("V2.1.29") || current_driver_version.equals("V2.1.30") || current_driver_version.equals("V2.1.32") || current_driver_version.equals("V2.1.43") || current_driver_version.equals("V2.0.33")
                        || current_driver_version.equals("V2.1.44") || current_driver_version.equals("V2.0.45") || current_driver_version.equals("V2.1.37") || current_driver_version.equals("V2.0.36") || current_driver_version.equals("V2.1.66") || current_driver_version.equals("V2.1.67") || current_driver_version.equals("V2.1.68") || current_driver_version.equals("V2.1.71") || current_driver_version.equals("V2.1.72") || current_driver_version.equals("V2.3.00") || current_driver_version.equals("V2.1.57") || current_driver_version.equals("V2.1.26")) {
                    this.params = new NS3ConnParams();
                } else {
                    this.params = new NSConnV100ConnParams(); //默认2.0连接
                }
            } else {
                this.params = new NS3ConnParams();
            }
        }
    }

    /**
     * 获取新大陆SDK版本信息。
     *
     * @return SDK2.0      -- 非事件机制版本
     * SDK3.0      -- 事件机制版本
     */
    public static String SDKVersion() {
        String version = "unknown";
        /**
         * ro.build.newland_sdk 后续固件版本增加的属性值
         *
         */
        version = getProperties("ro.build.newland_sdk");
        deviceLogger.debug("[SDKVersion] newland_sdk version=" + version);
        if ("unknown".equals(version)) {
            version = getProperties("ro.build.customer_id");
            deviceLogger.debug("[SDKVersion] customer_id version=" + version);
            if ("unknown".equals(version)) {
                // 根据MTMS之前的规则判断
                //20180719，SDK 2.0： SDK 2.0分支、银商专用、阿里千牛，其他的都是SDK 3.0。
                return version;
            } else if ("ChinaUms".equals(version) || "SDK_2.0".equals(version) || "AliQianNiu".equals(version)) {
                if ("ChinaUms".equals(version) && !Build.MODEL.equals("N900") && !Build.MODEL.equals("N910")) {
                    return "SDK3.0";
                }
                return "SDK2.0";
            } else {
                return "SDK3.0";
            }
        } else {
            return version;
        }
    }

    private static String getProperties(String key) {
        String defaultValue = "unknown";
        String value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            value = (String) (get.invoke(c, key, defaultValue));
        } catch (Exception e) {
            e.printStackTrace();
            deviceLogger.error("[getProperties]get property error, " + e.getMessage());
        }
        return value;
    }

    private class DisconnectThread extends Thread {
        @Override
        public void run() {
            try {
                synchronized (deviceLogger) {
                    if (state == DeviceConnState.DISCONNECTING && device != null) {
                        device.destroy();
                        device = null;
                    }
                }
            } catch (Exception e) {
                deviceLogger.error("[DisconnectThread]failed to disconnect!", e);
            } finally {
                state = DeviceConnState.DISCONNCECTED;
            }
        }
    }

    private void disconnect0(Throwable e) {
        if (null != device && null != context){
            PinpadPackage.getInstance((AbstractDevice) device, context).closePinpad();
        }
        EmvL3Comm.setEmvL3Comm(null);
        MEEmvL3.setMeEmvL3(null);
        synchronized (deviceLogger) {
            if (state != DeviceConnState.CONNECTED && state != DeviceConnState.CONNECTING) {
                deviceLogger.info("[disconnect0]not expected state to disconnect!" + state);
                return;
            }
            state = DeviceConnState.DISCONNECTING;
        }

        disconnectThread = new DisconnectThread();
        disconnectThread.start();
        try {
            disconnectThread.join(300);
        } catch (InterruptedException e1) {
            return;
        }
    }


    @Override
    public Device getDevice() {
        return device;
    }

    @Override
    public void disconnect() {
        try {
            JniCmdInterface.getInstance().jniMposLibCmd(new byte[]{(byte) 0xA1, 0x02}, 2, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnect0(null);
    }


    @Override
    public void destroy() {
        disconnect0(null);
        try {
            if (disconnectThread != null) {
                disconnectThread.join();
                disconnectThread = null;
            }
        } catch (InterruptedException e) {
        } finally {
            device = null;
            context = null;
            driver = null;
            params = null;
            instance = null;
            state = DeviceConnState.NOT_INIT;
        }
    }

    @Override
    public boolean connect() throws Exception {
        synchronized (deviceLogger) {
            if (DeviceConnState.DISCONNCECTED != state) {
                deviceLogger.info("[connect]not expected state to connect!" + state);
                return false;
            }
            state = DeviceConnState.CONNECTING;
        }
        try {
            device = driver.connect(context, params, new DeviceEventListener<ConnectionCloseEvent>() {
                @Override
                public void onEvent(final ConnectionCloseEvent event, final Handler handler) {
                    {
                        if (event.isSuccess()) {
                            deviceLogger.info("[connect:ConnectionCloseEvent]Device connected successfully.");
                        } else {
                            deviceLogger.error("[connect:ConnectionCloseEvent]An error occurred while trying to disconnect the device.", event.getException());
                        }
                        synchronized (deviceLogger) {
                            if (state == DeviceConnState.CONNECTED) {
                                state = DeviceConnState.DISCONNCECTED;
                                device = null;
                            }
                        }
                    }
                }

                @Override
                public Handler getUIHandler() {
                    return null;
                }
            });
            state = DeviceConnState.CONNECTED;
        } catch (Exception e) {
            disconnect0(e);
//            throw e;
            return false;
        }
        return true;
    }

    @Override
    public DeviceConnState getDeviceConnState() {
        return state;
    }

    @Override
    public String getSDKVersion() {
        return CommonUtils.getInstance().getSDKVersion();
    }

    @Override
    public void setMposParams(MposParams params) {
        this.mMposParams = params;
    }

    @Override
    public MposParams getMposParams() {
        return mMposParams;
    }


}
