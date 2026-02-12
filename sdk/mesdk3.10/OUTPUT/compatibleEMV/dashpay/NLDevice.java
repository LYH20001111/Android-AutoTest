package com.newland.sdk.me;

import android.newland.os.NlBuild;
import android.os.Build;
import android.util.Log;

import com.newland.ndk.NdkApiManager;
import com.newland.ndk.Print;
import com.newland.sdk.me.conn.SimpleDeviceManager;
import com.newland.sdk.me.module.cardreader.MECardReader;
import com.newland.sdk.me.module.cashbox.MEExtCashBox;
import com.newland.sdk.me.module.devicebasic.MEDeviceBasic;
import com.newland.sdk.me.module.emv.MEEMVL2;
import com.newland.sdk.me.module.emvl3.impl.MENEmvL3Decorator;
import com.newland.sdk.me.module.externalCardreader.MEExtCardReader;
import com.newland.sdk.me.module.externalKeyboard.MEExternalKeyboard;
import com.newland.sdk.me.module.externalLight.MEExtLight;
import com.newland.sdk.me.module.externalPininput.ReMEExternalPininput;
import com.newland.sdk.me.module.externalScanBox.MEExternalScanBox;
import com.newland.sdk.me.module.externalbuzzer.MEExtBuzzer;
import com.newland.sdk.me.module.externaliccard.MEExtICCard;
import com.newland.sdk.me.module.externalmagiccard.MEExtMagStripeCard;
import com.newland.sdk.me.module.externalrfcard.MeExternalRFCard;
import com.newland.sdk.me.module.externalsignature.MeExternalSignature;
import com.newland.sdk.me.module.guestDisplay.MEDisplayScreen;
import com.newland.sdk.me.module.iccard.MEICCard;
import com.newland.sdk.me.module.light.MELight;
import com.newland.sdk.me.module.pininput.MENPinpad;
import com.newland.sdk.me.module.printer.MEPrinter;
import com.newland.sdk.me.module.printerPro.appimpl.internal.InternalPrinterModule;
import com.newland.sdk.me.module.rfcard.MERFCard;
import com.newland.sdk.me.module.serialport.MESerial;
import com.newland.sdk.me.module.settings.MESettings;
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
    private DeviceLogger deviceLogger = DeviceLoggerFactory.getLogger("NLDevice");

    public NLDevice(DeviceExecutor deviceExecutor) {
        super(deviceExecutor);
        getDefaultLocale();
        deviceLogger.info("SDKVersion:" + SimpleDeviceManager.getInstance().getSDKVersion());
    }

    protected void initModule() {
        try {
            Print print = NdkApiManager.getNdkApiManager().getPrint();
            int initRet = print.NDK_PrnModuleInit();
            Log.d("NLDevice", "[getStatus] NDK_PrnModuleInit = " + initRet);
            if(initRet == 0){
                initRet = print.NDK_PrnCutterInit();
                Log.d("NLDevice", "[getStatus] NDK_PrnCutterInit = " + initRet);
            }
        }catch (Exception e){
            e.printStackTrace();
        }catch (Error e){
            e.printStackTrace();
        }
        if (!DeviceInfoUtils.getHasSecModule()) {
            standardModules.put(ModuleType.PRINTER, new MEPrinter(this, deviceExecutor.getContext()));
            standardModules.put(ModuleType.PRINTER_PRO, new InternalPrinterModule());
            standardModules.put(ModuleType.EMV, new MEEMVL2(this,deviceExecutor.getContext()));
            standardModules.put(ModuleType.EMV_L3, new MENEmvL3Decorator(deviceExecutor.getContext(),this));
            standardModules.put(ModuleType.USB_SERIALPORT, new MESerial(this, deviceExecutor.getContext()));
            standardModules.put(ModuleType.DEVICE_BASIC, new MEDeviceBasic(this, deviceExecutor.getContext()));
            externalModules.put(ExModuleType.PINPAD, new ReMEExternalPininput(this, deviceExecutor.getContext()));
            externalModules.put(ExModuleType.RFCARD, new MeExternalRFCard(this, deviceExecutor.getContext()));
            externalModules.put(ExModuleType.SIGNATURE, new MeExternalSignature(this, deviceExecutor.getContext()));
            externalModules.put(ExModuleType.SCANNER, new MEExternalScanBox(this, deviceExecutor.getContext()));
            standardModules.put(ModuleType.DISPLAY_SCREEN, new MEDisplayScreen(this, deviceExecutor.getContext()));
            externalModules.put(ExModuleType.KEYBOARD, new MEExternalKeyboard(this, deviceExecutor.getContext()));
            externalModules.put(ExModuleType.CASHBOX, new MEExtCashBox(this, deviceExecutor.getContext()));
            standardModules.put(ModuleType.SETTINGS, new MESettings(this, deviceExecutor.getContext()));
            externalModules.put(ExModuleType.MAGCARD, new MEExtMagStripeCard(this, deviceExecutor.getContext()));
            externalModules.put(ExModuleType.ICCARD, new MEExtICCard(this, deviceExecutor.getContext()));
            externalModules.put(ExModuleType.LIGHT, new MEExtLight(this, deviceExecutor.getContext()));
            externalModules.put(ExModuleType.CARDREADER, new MEExtCardReader(this, deviceExecutor.getContext()));
            externalModules.put(ExModuleType.BUZZER, new MEExtBuzzer(this, deviceExecutor.getContext()));
            standardModules.put(ModuleType.SCANNER, new MEScanner(this,deviceExecutor.getContext()));

        } else {
            //默认不做加载全部模块，减少开机时，应用内存，需要用再添加
//            standardModules.put(ModuleType.SCANNER, new MEScanner(this,deviceExecutor.getContext()));
//            standardModules.put(ModuleType.COMMON_CARDREADER, new MECardReader(this));
//            standardModules.put(ModuleType.PINPAD, new MENPinpad(this, deviceExecutor.getContext()));
//            standardModules.put(ModuleType.MAGCARDREADER, new MEMagStripeCard(this));
//            standardModules.put(ModuleType.ICCARDREADER, new MEICCard(this));
//            standardModules.put(ModuleType.RFCARDREADER, new MERFCard(this));
//            standardModules.put(ModuleType.PRINTER, new MEPrinter(this, deviceExecutor.getContext()));
//            standardModules.put(ModuleType.PRINTER_PRO, new InternalPrinterModule());
//            standardModules.put(ModuleType.INDICATOR_LIGHT, new MELight(this));
//            standardModules.put(ModuleType.EMV,new MEEMVL2(this,deviceExecutor.getContext()));
//            standardModules.put(ModuleType.EMV_L3, new MENEmvL3Decorator(deviceExecutor.getContext(),this));
//            standardModules.put(ModuleType.USB_SERIALPORT, new MESerial(this, deviceExecutor.getContext()));
//            standardModules.put(ModuleType.SM, new MESmModule(this, deviceExecutor.getContext()));
//            standardModules.put(ModuleType.DEVICE_BASIC, new MEDeviceBasic(this, deviceExecutor.getContext()));
//            externalModules.put(ExModuleType.PINPAD, new ReMEExternalPininput(this, deviceExecutor.getContext()));
//            standardModules.put(ModuleType.DISPLAY_SCREEN, new MEDisplayScreen(this, deviceExecutor.getContext()));
//            externalModules.put(ExModuleType.KEYBOARD, new MEExternalKeyboard(this, deviceExecutor.getContext()));
//            externalModules.put(ExModuleType.CASHBOX, new MEExtCashBox(this, deviceExecutor.getContext()));
//            externalModules.put(ExModuleType.RFCARD, new MeExternalRFCard(this, deviceExecutor.getContext()));
//            externalModules.put(ExModuleType.SIGNATURE, new MeExternalSignature(this, deviceExecutor.getContext()));
//            externalModules.put(ExModuleType.SCANNER, new MEExternalScanBox(this, deviceExecutor.getContext()));
//            standardModules.put(ModuleType.SETTINGS, new MESettings(this, deviceExecutor.getContext()));
//            externalModules.put(ExModuleType.MAGCARD, new MEExtMagStripeCard(this, deviceExecutor.getContext()));
//            externalModules.put(ExModuleType.ICCARD, new MEExtICCard(this, deviceExecutor.getContext()));
//            externalModules.put(ExModuleType.LIGHT, new MEExtLight(this, deviceExecutor.getContext()));
//            externalModules.put(ExModuleType.CARDREADER, new MEExtCardReader(this, deviceExecutor.getContext()));
//            externalModules.put(ExModuleType.BUZZER, new MEExtBuzzer(this, deviceExecutor.getContext()));
        }
    }

    @Override
    protected void setStandardModules(ModuleType moduleType) {
        switch (moduleType){
//            case SCANNER:
//                 standardModules.put(ModuleType.SCANNER, new MEScanner(this,deviceExecutor.getContext()));
//                break;
            case COMMON_CARDREADER:
                standardModules.put(ModuleType.COMMON_CARDREADER, new MECardReader(this));
                break;
//            case PINPAD:
//                standardModules.put(ModuleType.PINPAD, new MENPinpad(this, deviceExecutor.getContext()));
//                break;
            case MAGCARDREADER:
                standardModules.put(ModuleType.MAGCARDREADER, new MEMagStripeCard(this));
                break;
//            case ICCARDREADER:
//                standardModules.put(ModuleType.ICCARDREADER, new MEICCard(this));
//                break;
            case RFCARDREADER:
                standardModules.put(ModuleType.RFCARDREADER, new MERFCard(this));
                break;
            case PRINTER:
                standardModules.put(ModuleType.PRINTER, new MEPrinter(this, deviceExecutor.getContext()));
                break;
//            case PRINTER_PRO:
//                standardModules.put(ModuleType.PRINTER_PRO, new InternalPrinterModule());
//                break;
//            case INDICATOR_LIGHT:
//                standardModules.put(ModuleType.INDICATOR_LIGHT, new MELight(this));
//                break;
//            case EMV:
//                standardModules.put(ModuleType.EMV,new MEEMVL2(this,deviceExecutor.getContext()));
//                break;
//            case EMV_L3:
//                standardModules.put(ModuleType.EMV_L3, new MENEmvL3Decorator(deviceExecutor.getContext(),this));
//                break;
//            case USB_SERIALPORT:
//                standardModules.put(ModuleType.USB_SERIALPORT, new MESerial(this, deviceExecutor.getContext()));
//                break;
//            case SM:
//                standardModules.put(ModuleType.SM, new MESmModule(this, deviceExecutor.getContext()));
//                break;
//            case DEVICE_BASIC:
//                standardModules.put(ModuleType.DEVICE_BASIC, new MEDeviceBasic(this, deviceExecutor.getContext()));
//                break;
//            case DISPLAY_SCREEN:
//                standardModules.put(ModuleType.DISPLAY_SCREEN, new MEDisplayScreen(this, deviceExecutor.getContext()));
//                break;
//            case SETTINGS:
//                standardModules.put(ModuleType.SETTINGS, new MESettings(this, deviceExecutor.getContext()));
//                break;
        }
    }

    @Override
    protected void setExModule(String moduleType) {
		/*
        switch (moduleType){
            case ExModuleType.PINPAD:
                externalModules.put(ExModuleType.PINPAD, new ReMEExternalPininput(this, deviceExecutor.getContext()));
                break;
            case ExModuleType.KEYBOARD:
                externalModules.put(ExModuleType.KEYBOARD, new MEExternalKeyboard(this, deviceExecutor.getContext()));
                break;
            case ExModuleType.CASHBOX:
                externalModules.put(ExModuleType.CASHBOX, new MEExtCashBox(this, deviceExecutor.getContext()));
                break;
            case ExModuleType.RFCARD:
                externalModules.put(ExModuleType.RFCARD, new MeExternalRFCard(this, deviceExecutor.getContext()));
                break;
            case ExModuleType.SIGNATURE:
                externalModules.put(ExModuleType.SIGNATURE, new MeExternalSignature(this, deviceExecutor.getContext()));
                break;
            case ExModuleType.SCANNER:
                externalModules.put(ExModuleType.SCANNER, new MEExternalScanBox(this, deviceExecutor.getContext()));
                break;
            case ExModuleType.MAGCARD:
                externalModules.put(ExModuleType.MAGCARD, new MEExtMagStripeCard(this, deviceExecutor.getContext()));
                break;
            case ExModuleType.ICCARD:
                externalModules.put(ExModuleType.ICCARD, new MEExtICCard(this, deviceExecutor.getContext()));
                break;
            case ExModuleType.LIGHT:
                externalModules.put(ExModuleType.LIGHT, new MEExtLight(this, deviceExecutor.getContext()));
                break;
            case ExModuleType.CARDREADER:
                externalModules.put(ExModuleType.CARDREADER, new MEExtCardReader(this, deviceExecutor.getContext()));
                break;
            case ExModuleType.BUZZER:
                externalModules.put(ExModuleType.BUZZER, new MEExtBuzzer(this, deviceExecutor.getContext()));
                break;
        }
		*/
    }

    @Override
    public Locale getDefaultLocale() {
        try {
            Locale defalutlocale = Locale.getDefault();
            deviceLogger.debug("[getDefaultLocale] defalutlocale.getCountry()" + defalutlocale.getCountry());
            String[] sf = NlBuild.VERSION.NL_FIRMWARE.split("\\.");
            if (null != sf && sf.length >= 3) {
                StringBuilder version = new StringBuilder();
                version = version.append(sf[0]).append(sf[1]).append(sf[2]);
                String versionStr = version.substring(1);

                boolean isOldN910Version = versionStr.compareToIgnoreCase("2308") < 0 && Build.MODEL.equals("N910");
                boolean isOldN900Version = versionStr.compareToIgnoreCase("2300") < 0 && Build.MODEL.equals("N900");
                deviceLogger.debug("[getDefaultLocale] version:" + versionStr + ";isOldN910Version=" + isOldN910Version + ";isOldN900Version");

                if (isOldN910Version || isOldN900Version) {//旧固件只有西班牙语，西班牙语默认为墨西哥国家
                    if (defalutlocale.getCountry().equalsIgnoreCase("ES")) {
                        Locale mxLocale = new Locale("ES", "MX");
                        Locale.setDefault(mxLocale);
                    }
                }
            }
        } catch (Exception ex) {
            deviceLogger.error("[getDefaultLocale] set defalut locale failed!");
            ex.printStackTrace();
        }
        return Locale.getDefault();
    }
}
