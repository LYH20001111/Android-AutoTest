package com.newland.sdk.me;

import android.newland.os.NlBuild;
import android.os.Build;

import com.newland.sdk.me.conn.SimpleDeviceManager;
import com.newland.sdk.me.module.cardreader.MECardReader;
import com.newland.sdk.me.module.cashbox.MEExtCashBox;
import com.newland.sdk.me.module.devicebasic.MEDeviceBasic;
import com.newland.sdk.me.module.externalKeyboard.MEExternalKeyboard;
import com.newland.sdk.me.module.externalPininput.MEExternalPinInput;
import com.newland.sdk.me.module.externalScanBox.MEExternalScanBox;
import com.newland.sdk.me.module.externalrfcard.MeExternalRFCard;
import com.newland.sdk.me.module.externalsignature.MeExternalSignature;
import com.newland.sdk.me.module.guestDisplay.MEDisplayScreen;
import com.newland.sdk.me.module.iccard.MEICCard;
import com.newland.sdk.me.module.light.MELight;
import com.newland.sdk.me.module.pininput.MEPinpad;
import com.newland.sdk.me.module.printer.MEPrinter;
import com.newland.sdk.me.module.rfcard.MERFCard;
import com.newland.sdk.me.module.serialport.MESerial;
import com.newland.sdk.me.module.sm.MESmModule;
import com.newland.sdk.me.module.swiper.MEMagStripeCard;
import com.newland.sdk.me.utils.DeviceInfoUtils;
import com.newland.sdk.mtype.ExModuleType;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtypex.conn.DeviceExecutor;
import com.newland.sdk.me.module.scanner.MEScanner;

import java.util.Locale;

public class NLDevice extends AbstractMESeriesDevice {
    private DeviceLogger deviceLogger = DeviceLoggerFactory.getLogger(NLDevice.class);

    public NLDevice(DeviceExecutor deviceExecutor) {
        super(deviceExecutor);
        getDefaultLocale();
        deviceLogger.info("SDKVersion:" + SimpleDeviceManager.getInstance().getSDKVersion());
    }

    protected void initModule() {
        if (!DeviceInfoUtils.getHasSecModule()) {
            standardModules.put(ModuleType.PRINTER, new MEPrinter(this));
            standardModules.put(ModuleType.USB_SERIALPORT, new MESerial(this, deviceExecutor.getContext()));
            standardModules.put(ModuleType.DEVICE_BASIC, new MEDeviceBasic(this, deviceExecutor.getContext()));
            externalModules.put(ExModuleType.PINPAD, new MEExternalPinInput(this, deviceExecutor.getContext()));
            externalModules.put(ExModuleType.RFCARD, new MeExternalRFCard(this, deviceExecutor.getContext()));
            externalModules.put(ExModuleType.SIGNATURE, new MeExternalSignature(this, deviceExecutor.getContext()));
            externalModules.put(ExModuleType.SCANNER, new MEExternalScanBox(this, deviceExecutor.getContext()));
            standardModules.put(ModuleType.DISPLAY_SCREEN, new MEDisplayScreen(this, deviceExecutor.getContext()));
            externalModules.put(ExModuleType.KEYBOARD, new MEExternalKeyboard(this, deviceExecutor.getContext()));
            externalModules.put(ExModuleType.CASHBOX, new MEExtCashBox(this, deviceExecutor.getContext()));
        } else {
            standardModules.put(ModuleType.SCANNER, new MEScanner(this));
            standardModules.put(ModuleType.COMMON_CARDREADER, new MECardReader(this));
            standardModules.put(ModuleType.PINPAD, new MEPinpad(this, deviceExecutor.getContext()));
            standardModules.put(ModuleType.MAGCARDREADER, new MEMagStripeCard(this));
            standardModules.put(ModuleType.ICCARDREADER, new MEICCard(this));
            standardModules.put(ModuleType.RFCARDREADER, new MERFCard(this));
            standardModules.put(ModuleType.PRINTER, new MEPrinter(this));
            standardModules.put(ModuleType.INDICATOR_LIGHT, new MELight(this));
            standardModules.put(ModuleType.USB_SERIALPORT, new MESerial(this, deviceExecutor.getContext()));
            standardModules.put(ModuleType.SM, new MESmModule(this));
            standardModules.put(ModuleType.DEVICE_BASIC, new MEDeviceBasic(this, deviceExecutor.getContext()));
            externalModules.put(ExModuleType.PINPAD, new MEExternalPinInput(this, deviceExecutor.getContext()));
            standardModules.put(ModuleType.DISPLAY_SCREEN, new MEDisplayScreen(this, deviceExecutor.getContext()));
            externalModules.put(ExModuleType.KEYBOARD, new MEExternalKeyboard(this, deviceExecutor.getContext()));
            externalModules.put(ExModuleType.CASHBOX, new MEExtCashBox(this, deviceExecutor.getContext()));
            externalModules.put(ExModuleType.RFCARD, new MeExternalRFCard(this, deviceExecutor.getContext()));
            externalModules.put(ExModuleType.SIGNATURE, new MeExternalSignature(this, deviceExecutor.getContext()));
            externalModules.put(ExModuleType.SCANNER, new MEExternalScanBox(this, deviceExecutor.getContext()));
        }
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
