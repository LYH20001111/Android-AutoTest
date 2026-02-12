package com.newland.sdk;

import android.content.Context;
import com.newland.buildtask.R;
import com.newland.emv.jni.service.EmvJNIService;
import com.newland.intelligent.jni.JniCmdInterface;
import com.newland.sdk.common.RunningModel;
import com.newland.sdk.me.ConnUtils;
import com.newland.sdk.me.DeviceManager;
import com.newland.sdk.me.module.usb.MEUSB;
import com.newland.sdk.module.devicebasic.DeviceBasicModule;
import com.newland.sdk.module.displayScreen.DisplayScreenModule;
import com.newland.sdk.module.usb.USBModule;
import com.newland.sdk.mtype.ExModuleType;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.module.cardreader.CardReaderModule;
import com.newland.sdk.module.emv.EMVModule;
import com.newland.sdk.module.externalKeyboard.ExtKeyboardModule;
import com.newland.sdk.module.externalPin.ExtPinpadModule;
import com.newland.sdk.module.externalrfcard.ExtRFCardModule;
import com.newland.sdk.module.externalsignature.ExtSignatureModule;
import com.newland.sdk.module.iccard.ICCardModule;
import com.newland.sdk.module.light.IndicatorLightModule;
import com.newland.sdk.module.pin.PinpadModule;
import com.newland.sdk.module.printer.PrinterModule;
import com.newland.sdk.module.rfcard.RFCardModule;
import com.newland.sdk.module.scanner.ScannerModule;
import com.newland.sdk.module.serialport.SerialPortModule;
import com.newland.sdk.module.sm.SmModule;
import com.newland.sdk.module.swiper.MagStripeCardModule;
import com.newland.sdk.module.cashbox.ExtCashBoxModule;
import com.newland.sdk.module.buzzer.BuzzerModule;
import com.newland.sdk.module.buzzer.MeBuzzer;
import com.newland.sdk.mtypex.module.common.emv.SoundPoolImpl;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

/**
 * <p>Call entry for the development kit.</p>
 * <p>call step:</p>
 * <p>1.get the instance of the ModuleManage. ModuleManage moduleManage = ModuleManage.getInstance();</p>
 * <p>2.Initializes the device module.</p>
 * <p>3.invoke the method to get the device module.</p>
 * <p>4.Destroy device module</p>
 */
public class ModuleManage {
   private Context context;
    private DeviceManager deviceManager;
    private static ModuleManage moduleManage;

    public static ModuleManage getInstance(){
        if(moduleManage==null){
            synchronized (ModuleManage.class){
                if(moduleManage == null){
                    moduleManage = new ModuleManage();
                }
            }
        }
        return moduleManage;
    }

    /**
     * Initializes the device module.
     * @param context
     * @return
     */
	public boolean init(Context context){
        try {
            this.context = context;
            deviceManager = ConnUtils.getDeviceManager();
            deviceManager.init(context);
            SoundPoolImpl.getInstance(0).initLoad(context,R.raw.click);
            SoundPoolImpl.getInstance(1).initLoad(context,R.raw.beep);
            SoundPoolImpl.getInstance(2).initLoad(context,R.raw.error);
            if (opendebug()) {
                setDebugMode(true);
            }
            deviceManager.connect();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private ModuleManage() {
    }


    /**
     * <p>Get the common CardReaderModule.</p>
     * <p>Achieve the purpose of obtaining the detected card and then you can use the specified cardModule(MagStripeCardModule\ICCardModule\RFCardModule) to cotinue the next step.</p>
     *
     * @return
     */
    public CardReaderModule getCardReaderModule() {
        CardReaderModule cardReaderModule = (CardReaderModule) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_CARDREADER);
        if (null == cardReaderModule)
            throw new RuntimeException("The CardReaderModule is not supported by this device or development kit");
        return cardReaderModule;
    }

    /**
     * <p>Get the printer module to print the bill.</p>
     *
     * @return
     */
    public PrinterModule getPrinterModule() {
        PrinterModule printerModule = (PrinterModule) deviceManager.getDevice().getStandardModule(ModuleType.PRINTER);
        if (null == printerModule)
            throw new RuntimeException("The PrinterModule is not supported by this device or development kit");
        return printerModule;
    }

    /**
     * <p>Get the EMV L3 module to start the contact/contactless transaction process.</p>
     * <p>By using object-oriented programming approach to achieve the transaction.</p>
     *
     * @return
     */
    public EMVModule getEMVModule() {
        EMVModule emvModule = (EMVModule) deviceManager.getDevice().getStandardModule(ModuleType.EMV);
        if (null == emvModule)
            throw new RuntimeException("The EMVModule is not supported by this device or development kit");
        return emvModule;
    }

    /**
     * <p>Get the USB\SerialPort module.</p>
     * <p>Achieve the purpose of communication with external equipment.</p>
     *
     * @return
     */
    public SerialPortModule getSerialPortModule() {
        SerialPortModule serialPortModule = (SerialPortModule) deviceManager.getDevice().getStandardModule(ModuleType.USB_SERIALPORT);
        if (null == serialPortModule)
            throw new RuntimeException("The SerialPortModule is not supported by this device or development kit");
        return serialPortModule;
    }

    /**
     * <p>Get the scanner module.</p>
     * <p>Achieve the purpose of scanning barcode、qrcode PDF147 and other type of codes.</p>
     *
     * @return
     */
    public ScannerModule getScannerModule() {
        ScannerModule scannerModule = (ScannerModule) deviceManager.getDevice().getStandardModule(ModuleType.SCANNER);
        if (null == scannerModule)
            throw new RuntimeException("The ScannerModule is not supported by this device or development kit");
        return scannerModule;
    }

    /**
     * <p>Get the Pinpad module</p>
     * <p>Achieve the purpose of obtaining the pinblock、calculating the data or loading key.</p>
     *
     * @return
     */
    public PinpadModule getPinpadModule() {
        PinpadModule pinpadModule = (PinpadModule) deviceManager.getDevice().getStandardModule(ModuleType.PINPAD);
        if (null == pinpadModule)
            throw new RuntimeException("The PinpadModule is not supported by this device or development kit");
        return pinpadModule;
    }

    /**
     * <p>Get the MagStripe Card module.</p>
     * <p>Achieve the purpose of obtaining the track data.</p>
     *
     * @return
     */
    public MagStripeCardModule getMagStripeCardModule() {
        MagStripeCardModule magStripeCardModule = (MagStripeCardModule) deviceManager.getDevice().getStandardModule(ModuleType.MAGCARDREADER);
        if (null == magStripeCardModule)
            throw new RuntimeException("The MagStripeCardModule is not supported by this device or development kit");
        return magStripeCardModule;
    }

    /**
     * <p>Get the IC Card module.</p>
     * <p>Achieve the purpose of communication with IC card.</p>
     *
     * @return
     */
    public ICCardModule getICCardModule() {
        ICCardModule icCardModule = (ICCardModule) deviceManager.getDevice().getStandardModule(ModuleType.ICCARDREADER);
        if (null == icCardModule)
            throw new RuntimeException("The ICCardModule is not supported by this device or development kit");
        return icCardModule;
    }

    /**
     * <p>Get the RF Card module.</p>
     * <p>Achieve the purpose of communication with RF card.</p>
     *
     * @return
     */
    public RFCardModule getRFCardModule() {
        RFCardModule rfCardModule = (RFCardModule) deviceManager.getDevice().getStandardModule(ModuleType.RFCARDREADER);
        if (null == rfCardModule)
            throw new RuntimeException("The RFCardModule is not supported by this device or development kit");
        return rfCardModule;
    }

    /**
     * <p>Get the indicator light module.</p>
     * <p>Achieve the purpose of turning on/off the indicator light(blue\green\yellow\red).</p>
     *
     * @return
     */
    public IndicatorLightModule getIndicatorLightModule() {
        IndicatorLightModule indicatorLightModule = (IndicatorLightModule) deviceManager.getDevice().getStandardModule(ModuleType.INDICATOR_LIGHT);
        if (null == indicatorLightModule)
            throw new RuntimeException("The IndicatorLightModule is not supported by this device or development kit");
        return indicatorLightModule;
    }

    /**
     * <p>Get the SM module.</p>
     * <p>Achieve the purpose of calculating the data with SHA256/SHA512/RSA/SM2/SM3/SM4.</p>
     *
     * @return
     */
    public SmModule getSmModule() {
        SmModule smModule = (SmModule) deviceManager.getDevice().getStandardModule(ModuleType.SM);
        if (null == smModule)
            throw new RuntimeException("The SmModule is not supported by this device or development kit");
        return smModule;
    }

    /**
     * <p>Get the display screen module.Valid only for devices with customer display.(N550) </p>
     *
     * @return
     */
    public DisplayScreenModule getDisplayScreenModule() {
        DisplayScreenModule displayScreenModule = (DisplayScreenModule) deviceManager.getDevice().getStandardModule(ModuleType.DISPLAY_SCREEN);
        if (null == displayScreenModule)
            throw new RuntimeException("The DisplayScreenModule is not supported by this device or development kit");
        return displayScreenModule;
    }

    /**
     * <p>Get the device basic module.</p>
     * <p>Achieve the purpose of basic function.</p>
     *
     * @return
     */
    public DeviceBasicModule getDeviceBasicModule() {
        DeviceBasicModule deviceBasicModule = (DeviceBasicModule) deviceManager.getDevice().getStandardModule(ModuleType.DEVICE_BASIC);
        if (null == deviceBasicModule)
            throw new RuntimeException("The DeviceBasicModule is not supported by this device or development kit");
        return deviceBasicModule;
    }

    /**
     * <p>Get the USB module.</p>
     * <p>Achieve the purpose of communication with external equipment.</p>
     *
     * @return
     */
    public USBModule getUSBModule() {
        USBModule usbModule = new MEUSB(context);
        return usbModule;
    }

    /**
     * <p>Get the external RF card module.</p>
     * <p>Achieve the purpose of communication with RF card.</p>
     *
     * @return
     */
    public ExtRFCardModule getExtRFCardModule() {
        ExtRFCardModule extRFCardModule = (ExtRFCardModule) deviceManager.getDevice().getExModule(ExModuleType.RFCARD);
        if (null == extRFCardModule)
            throw new RuntimeException("The ExtRFCardModule is not supported by this device or development kit");
        return extRFCardModule;
    }

    /**
     * <p>Get the external Pinpad module</p>
     * <p>Achieve the purpose of obtaining the pinblock、calculating the data or loading key.</p>
     *
     * @return
     */
    public ExtPinpadModule getExtPinpadModule() {
        ExtPinpadModule extPinInputModule = (ExtPinpadModule) deviceManager.getDevice().getExModule(ExModuleType.PINPAD);
        if (null == extPinInputModule)
            throw new RuntimeException("ThE ExtPinpadModule is not supported by this device or development kit");
        return extPinInputModule;
    }

    /**
     * <p>Get the external signature module</p>
     * <p>Achieve the purpose of electronic signature.</p>
     *
     * @return
     */
    public ExtSignatureModule getExtSignatureModule() {
        ExtSignatureModule extSignatureModule = (ExtSignatureModule) deviceManager.getDevice().getExModule(ExModuleType.SIGNATURE);
        if (null == extSignatureModule)
            throw new RuntimeException("The ExtSignatureModule is not supported by this device or development kit");
        return extSignatureModule;
    }

    /**
     * <p>Get the external Keyboard module</p>
     * <p>Valid only for the number keyboard of newland.</p>
     *
     * @return
     */
    public ExtKeyboardModule getExtKeyboardModule() {
        ExtKeyboardModule extKeyboardModule = (ExtKeyboardModule) deviceManager.getDevice().getExModule(ExModuleType.KEYBOARD);
        if (null == extKeyboardModule)
            throw new RuntimeException("The ExtKeyboardModule is not supported by this device or development kit");
        return extKeyboardModule;
    }
    /**
     * <p>Get the external cashbox module</p>
     *
     * @return
     */
    public ExtCashBoxModule getExtCashModule() {
        ExtCashBoxModule extCashBoxModule = (ExtCashBoxModule) deviceManager.getDevice().getExModule(ExModuleType.CASHBOX);
        if (null == extCashBoxModule)
            throw new RuntimeException("ThE ExtCashBoxModule is not supported by this device or development kit");
        return extCashBoxModule;
    }

    /**
     * <p>Get the Buzzer module.</p>
     *
     * @return
     */
    public BuzzerModule getBuzzerModule() {
        BuzzerModule buzzerModule = new MeBuzzer();
        if (null == buzzerModule)
            throw new RuntimeException("The BuzzerModule is not supported by this device or development kit");
        return buzzerModule;
    }

    public void setDebugMode(boolean select) {
        if (select) {
            RunningModel.isDebugEnabled = true;
            new EmvJNIService().jniemvSetDebugMode(3);
            JniCmdInterface.getInstance().jniMposLibCmd(new byte[]{(byte) 0xA1,0x01,0x01,0x00,0x00},5,null,null);
        } else {
            RunningModel.isDebugEnabled = false;
            new EmvJNIService().jniemvSetDebugMode(0);
            JniCmdInterface.getInstance().jniMposLibCmd(new byte[]{(byte) 0xA1,0x01,0x00,0x00,0x00},5,null,null);
        }
    }

    /**
     * release the device resources.
     *
     * @return
     */
    public void destroy() {
        deviceManager.disconnect();
		SoundPoolImpl.getInstance(0).unLoad();
        SoundPoolImpl.getInstance(1).unLoad();
        deviceManager = null;
		moduleManage = null;
    }
    /**
     * <p>Get the external scan module</p>
     * <p>Valid only for the scanner of ME66.</p>
     * @return
     */
    public ExtScanBoxModule getExtScanBoxModule()  {
        ExtScanBoxModule extScanBoxModule = (ExtScanBoxModule) deviceManager.getDevice().getExModule(ExModuleType.SCANNER);
        if (null == extScanBoxModule)
            throw new RuntimeException("The ExtScanBoxModule is not supported by this device or development kit");
        return extScanBoxModule;
    }

    private boolean opendebug() {
        String fwVersion = NlBuild.VERSION.NL_FIRMWARE;
        if (fwVersion.startsWith("T") || fwVersion.startsWith("D")) {
            return true;
        }
        if(!isProductDevice()){
            return true;
        }
        boolean debugmode = false;
        InputStream signInput = null;
        InputStream keyInputStream = null;
        File authFile = new File("/data/share/EpayParameter/authFile.properties");
        try {
            //读取签名文件数据
            if (!authFile.exists()) {
                logger.info("[init]debug log authFile is not exist,production mode log.");
                return false;
            }
            signInput = new FileInputStream(authFile);
            byte[] signData = new byte[signInput.available()];
            signInput.read(signData);
            //读取pem证书
            keyInputStream = getClass().getClassLoader().getResourceAsStream("res/raw/mesdk_public_key.pem");
            if (null == keyInputStream) {
                logger.info("[init]mesdk_public_key is not exist,Production mode log");
                return false;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(keyInputStream));
            String s = br.readLine();
            StringBuffer publickey = new StringBuffer();
            while (null != s) {
                if (s.charAt(0) == '-') {
                    s = br.readLine();
                    continue;
                }
                publickey.append(s + "\r");
                s = br.readLine();
            }
            byte[] keybyte = Base64.decode(publickey.toString(), Base64.DEFAULT);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keybyte);
            PublicKey publicKey = kf.generatePublic(keySpec);
            //被签的原文
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
            String toSign = Build.SERIAL + simpleDateFormat.format(new Date());
            //生成的签名
            String sign = signData.toString();
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initVerify(publicKey);
            signature.update(toSign.getBytes("utf-8"));
            debugmode = signature.verify(signData);
            logger.info("[init]Debug log authorization failed,production mode log.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != signInput) {
                try {
                    signInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != keyInputStream) {
                try {
                    keyInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (authFile.exists()) {
                authFile.delete();
            }
        }
        return debugmode;
    }


    /**
     * 判断设备是否是生产机
     * @return
     */
    private boolean isProductDevice() {
        try{
            String resultCode = getSystemProperty("ro.epay.adb");
            logger.debug("-----------isProductDevice:" + resultCode);
            if (null != resultCode && resultCode.trim().equalsIgnoreCase("0")) {
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;

    }

    /**
     * 读取系统属性 返回值 1 表示开发样机; 返回值0 表示生产机器
     */
    public static String getSystemProperty(String key) {
        String result = null;
        try {
            Class<?> spCls = Class.forName("android.os.SystemProperties");
            Class<?>[] typeArgs = new Class[2];
            typeArgs[0] = String.class;
            typeArgs[1] = String.class;
            Constructor<?> spcs = spCls.getConstructor( new  Class[ 0 ]);
            Object[] valueArgs = new Object[2];
            valueArgs[0] = key;
            valueArgs[1] = null;
            Object sp = spcs.newInstance( new  Object[]{});
            Method method = spCls.getMethod("get", typeArgs);
            result = (String) method.invoke(sp, valueArgs);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return result;
    }

}
