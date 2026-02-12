package com.newland.sdk.me;

import android.newland.os.NlBuild;
import com.newland.ndk.NdkApiManager;
import com.newland.sdk.me2.cmd.devicebasic.CmdSetCSN;
import com.newland.sdk.me.utils.DeviceInfoUtils;
import com.newland.sdk.me.module.printer.MEPrinter;
import com.newland.sdk.me.module.scanner.MEScanner;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtypex.conn.DeviceExecutor;
import com.newland.sdk.me2.module.cardreader.MECardReader;
import com.newland.sdk.me2.module.swiper.MEMagStripeCard;
import com.newland.sdk.me2.module.rfcard.MERFCard;
import java.io.File;

public class K21Device extends AbstractMESeriesDevice {
    private DeviceLogger logger = DeviceLoggerFactory.getLogger("K21Device");

    public K21Device(DeviceExecutor deviceExecutor) {
        super(deviceExecutor);
    }

    protected void initModule() {
        standardModules.put(ModuleType.SCANNER, new MEScanner(this,deviceExecutor.getContext()));
        standardModules.put(ModuleType.PRINTER, new MEPrinter(this, deviceExecutor.getContext()));
        standardModules.put(ModuleType.COMMON_CARDREADER, new MECardReader(this));
        standardModules.put(ModuleType.MAGCARDREADER, new MEMagStripeCard(this));
        standardModules.put(ModuleType.RFCARDREADER, new MERFCard(this));
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
