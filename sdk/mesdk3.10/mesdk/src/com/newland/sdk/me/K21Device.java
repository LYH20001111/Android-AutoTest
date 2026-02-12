package com.newland.sdk.me;

import android.newland.os.NlBuild;

import com.newland.ndk.NdkApiManager;
import com.newland.sdk.me.module.cashbox.MEExtCashBox;
import com.newland.sdk.me.module.emv.MEEMVL2;
import com.newland.sdk.me.module.externalCardreader.MEExtCardReader;
import com.newland.sdk.me.module.externalLight.MEExtLight;
import com.newland.sdk.me.module.externalPininput.ReMEExternalPininput;
import com.newland.sdk.me.module.externalScanBox.MEExternalScanBox;
import com.newland.sdk.me.module.externalbuzzer.MEExtBuzzer;
import com.newland.sdk.me.module.externaliccard.MEExtICCard;
import com.newland.sdk.me.module.externalmagiccard.MEExtMagStripeCard;
import com.newland.sdk.me2.module.devicebasic.MEDeviceBasic;
import com.newland.sdk.me.module.serialport.MESerial;
import com.newland.sdk.me2.cmd.devicebasic.CmdSetCSN;
import com.newland.sdk.me.utils.DeviceInfoUtils;
import com.newland.sdk.me2.module.cardreader.MECardReader;
import com.newland.sdk.me.module.externalrfcard.MeExternalRFCard;
import com.newland.sdk.me.module.externalsignature.MeExternalSignature;
import com.newland.sdk.me2.module.iccard.MEICCard;
import com.newland.sdk.me.module.light.MELight;
import com.newland.sdk.me2.module.pininput.MEPinpad;
import com.newland.sdk.me.module.printer.MEPrinter;
import com.newland.sdk.me2.module.rfcard.MERFCard;
import com.newland.sdk.me.module.scanner.MEScanner;
import com.newland.sdk.me.module.sm.MESmModule;
import com.newland.sdk.me2.module.swiper.MEMagStripeCard;
import com.newland.sdk.mtype.ExModuleType;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtypex.conn.DeviceExecutor;

import java.io.File;

public class K21Device extends AbstractMESeriesDevice {
    private DeviceLogger logger = DeviceLoggerFactory.getLogger("K21Device");

    public K21Device(DeviceExecutor deviceExecutor) {
        super(deviceExecutor);
    }

    protected void initModule() {
        standardModules.put(ModuleType.SCANNER, new MEScanner(this,deviceExecutor.getContext()));
        standardModules.put(ModuleType.COMMON_CARDREADER, new MECardReader(this));
        standardModules.put(ModuleType.PINPAD, new MEPinpad(this, deviceExecutor.getContext()));
        standardModules.put(ModuleType.MAGCARDREADER, new MEMagStripeCard(this));
        standardModules.put(ModuleType.ICCARDREADER, new MEICCard(this));
        standardModules.put(ModuleType.EMV, new MEEMVL2(this, deviceExecutor.getContext()));
        standardModules.put(ModuleType.RFCARDREADER, new MERFCard(this));
        standardModules.put(ModuleType.PRINTER, new MEPrinter(this, deviceExecutor.getContext()));
        externalModules.put(ExModuleType.PINPAD, new ReMEExternalPininput(this, deviceExecutor.getContext()));
        standardModules.put(ModuleType.INDICATOR_LIGHT, new MELight(this));
        standardModules.put(ModuleType.USB_SERIALPORT, new MESerial(this, deviceExecutor.getContext()));
        externalModules.put(ExModuleType.RFCARD, new MeExternalRFCard(this, deviceExecutor.getContext()));
        externalModules.put(ExModuleType.SIGNATURE, new MeExternalSignature(this, deviceExecutor.getContext()));
        externalModules.put(ExModuleType.SCANNER, new MEExternalScanBox(this, deviceExecutor.getContext()));
        standardModules.put(ModuleType.SM, new MESmModule(this, deviceExecutor.getContext()));
        externalModules.put(ExModuleType.CASHBOX, new MEExtCashBox(this, deviceExecutor.getContext()));
        standardModules.put(ModuleType.DEVICE_BASIC, new MEDeviceBasic(this, deviceExecutor));
        externalModules.put(ExModuleType.MAGCARD, new MEExtMagStripeCard(this, deviceExecutor.getContext()));
        externalModules.put(ExModuleType.ICCARD, new MEExtICCard(this, deviceExecutor.getContext()));
        externalModules.put(ExModuleType.LIGHT, new MEExtLight(this, deviceExecutor.getContext()));
        externalModules.put(ExModuleType.CARDREADER, new MEExtCardReader(this, deviceExecutor.getContext()));
        externalModules.put(ExModuleType.BUZZER, new MEExtBuzzer(this, deviceExecutor.getContext()));
    }

    @Override
    protected void setStandardModules(ModuleType moduleType) {

    }

    @Override
    protected void setExModule(String moduleType) {

    }

    @Override
    public void setCSN(String csn) {
        if (!DeviceInfoUtils.getHasSecModule()) {
            return;
        }
        invoke(new CmdSetCSN((byte) 0x04, csn));
    }

    @Override
    public byte[] getRandom(int len) {
        if (len <= 0) {
            throw new IllegalArgumentException("The len can't be less than 0.");
        }
        byte[] random = new byte[len];
        int ret = NdkApiManager.getNdkApiManager().getSecN().NDK_SecGetRandom(len, random);
        if (ret == 0) {
            return random;
        }
        throw new ArithmeticException("The ret value is not 0, but " + ret + ".");
    }

    @Override
    public String getTusn() {
        String deviceType = "04";// 01 ATM, 02 传统POS, 03 MPOS, 04 智能POS   09人脸设备
        if (isSupFaceRecognition()) {
            deviceType = "09";
        }
        String serialNo = "000003";// Newland manufactures no 000003
        String sn = getDeviceInfo().getSN();
        String tusn = serialNo + deviceType + sn;
        File file = new File("/newland/factory/flag_sn_20");
        if (!file.exists()) {
            logger.debug("[getTusn] the file is not exit. " + sn);
            return sn;
        } else {
            logger.debug("[getTusn] the file is exit. " + tusn);
            return tusn;
        }
    }


    /**
     * 是否支持人脸识别
     * @return
     */
    private boolean isSupFaceRecognition(){
        try {
            String config = NlBuild.VERSION.NL_HARDWARE_CONFIG;// 硬件配置码
            logger.debug("[isSupFaceRecognition] config:"+config);
            if (config != null && config.length() >= 46) {
                String faceRecognitionParam = config.substring(44, 46);
                logger.debug("[isSupFaceRecognition] faceRecognitionParam:"+faceRecognitionParam);
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
