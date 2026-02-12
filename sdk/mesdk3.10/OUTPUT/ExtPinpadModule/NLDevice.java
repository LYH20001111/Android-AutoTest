package com.newland.sdk.me;

import android.newland.os.NlBuild;
import android.os.Build;
import com.newland.sdk.me.conn.SimpleDeviceManager;
import com.newland.sdk.me.module.externalPininput.MEExternalPinInput;
import com.newland.sdk.me.module.externalrfcard.MeExternalRFCard;
import com.newland.sdk.mtype.ExModuleType;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtypex.conn.DeviceExecutor;

import java.util.Locale;

public class NLDevice extends AbstractMESeriesDevice {
    private DeviceLogger deviceLogger = DeviceLoggerFactory.getLogger(NLDevice.class);

    public NLDevice(DeviceExecutor deviceExecutor) {
        super(deviceExecutor);
        getDefaultLocale();
        deviceLogger.info("SDKVersion:" + SimpleDeviceManager.getInstance().getSDKVersion());
    }

    protected void initModule() {
        externalModules.put(ExModuleType.PINPAD, new MEExternalPinInput(this, deviceExecutor.getContext()));
        externalModules.put(ExModuleType.RFCARD, new MeExternalRFCard(this, deviceExecutor.getContext()));
    }

    @Override
    public Locale getDefaultLocale() {
        try {
            Locale defalutlocale = Locale.getDefault();
            deviceLogger.debug("---defalutlocale.getCountry()" + defalutlocale.getCountry());
            String[] sf = NlBuild.VERSION.NL_FIRMWARE.split("\\.");
            if (null != sf && sf.length >= 3) {
                StringBuilder version = new StringBuilder();
                version = version.append(sf[0]).append(sf[1]).append(sf[2]);
                String versionStr = version.substring(1);

                boolean isOldN910Version = versionStr.compareToIgnoreCase("2308") < 0 && Build.MODEL.equals("N910");
                boolean isOldN900Version = versionStr.compareToIgnoreCase("2300") < 0 && Build.MODEL.equals("N900");
                deviceLogger.debug("---version" + versionStr + ";isOldN910Version=" + isOldN910Version + ";isOldN900Version");

                if (isOldN910Version || isOldN900Version) {//旧固件只有西班牙语，西班牙语默认为墨西哥国家
                    if (defalutlocale.getCountry().equalsIgnoreCase("ES")) {
                        Locale mxLocale = new Locale("ES", "MX");
                        Locale.setDefault(mxLocale);
                    }
                }
            }
        } catch (Exception ex) {
            deviceLogger.error("set defalut locale failed!");
            ex.printStackTrace();
        }
        return Locale.getDefault();
    }
}
