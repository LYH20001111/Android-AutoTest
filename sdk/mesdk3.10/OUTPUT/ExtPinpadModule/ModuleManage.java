package com.newland.sdk;

import android.content.Context;

import com.newland.sdk.me.ConnUtils;
import com.newland.sdk.me.DeviceManager;
import com.newland.sdk.module.externalrfcard.ExtRFCardModule;
import com.newland.sdk.mtype.ExModuleType;
import com.newland.sdk.module.externalPin.ExtPinpadModule;
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
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>Call entry for the development kit.</p>
 * <p>call step:</p>
 * <p>1.get the instance of the ModuleManage. ModuleManage moduleManage=new ModuleManage();</p>
 * <p>2.invoke the method to get the device module.</p>
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

            if (opendebug()) {
                // 生产机 或者 调试机且没有关闭mesdk日志
                if (isProductDevice() || DeviceLoggerFactory.getLoggerLevel() != 0) {
                    setDebugMode(true);
                }
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

    public ExtIndicatorLightModule getExtIndicatorLightModule() {
        ExtIndicatorLightModule extLightModule = (ExtIndicatorLightModule) deviceManager.getDevice().getExModule(ExModuleType.LIGHT);
        if (null == extLightModule)
            throw new RuntimeException("The ExtIndicatorLightModule is not supported by this device or development kit");
        return extLightModule;
    }
    public ExtCardReaderModule getExtCardReaderModule() {
        ExtCardReaderModule extCardReaderModule = (ExtCardReaderModule) deviceManager.getDevice().getExModule(ExModuleType.CARDREADER);
        if (null == extCardReaderModule)
            throw new RuntimeException("The ExtCardReaderModule is not supported by this device or development kit");
        return extCardReaderModule;
    }
    public ExtICCardModule getExtICCardModule(){
        ExtICCardModule extICCardModule = (ExtICCardModule)deviceManager.getDevice().getExModule(ExModuleType.ICCARD);
        if(extICCardModule == null){
            throw new RuntimeException("The ExtICCardModule is not supported by this device or development kit");
        }
        return extICCardModule;
    }

    public ExtMagicCardModule getExtMagCardModule(){
        ExtMagicCardModule extMagCardModule = (ExtMagicCardModule)deviceManager.getDevice().getExModule(ExModuleType.MAGCARD);
        if(extMagCardModule == null){
            throw new RuntimeException("The ExtMagicCardModule is not supported by this device or development kit");
        }
        return extMagCardModule;
    }

    public ExtBuzzerModule getExtBuzzerModule(){
        ExtBuzzerModule extBuzzerModule = (ExtBuzzerModule)deviceManager.getDevice().getExModule(ExModuleType.BUZZER);
        if(extBuzzerModule == null){
            throw new RuntimeException("The extBuzzerModule is not supported by this device or development kit");
        }
        return extBuzzerModule;
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
    public void setLoggerLevel(int loggerLevel) {
        if (loggerLevel == 0) {
            DeviceLoggerFactory.setLoggerLevel(-1, -1, -1, loggerLevel);
            RunningModel.isDebugEnabled = false;
            if (hasInit) {
                new EmvJNIService().jniemvSetDebugMode(0);
            }
            JniCmdInterface.getInstance().jniMposLibCmd(new byte[]{(byte) 0xA1, 0x01, 0x00, 0x00, 0x00}, 5, null, null);
        }
    }
    /**
     * release the device resources.
     *
     * @return
     */
    public void destroy() {
        deviceManager.disconnect();
        deviceManager = null;
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
