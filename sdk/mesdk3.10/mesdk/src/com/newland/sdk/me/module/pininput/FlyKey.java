package com.newland.sdk.me.module.pininput;

import android.content.Context;
import android.text.TextUtils;

import com.newland.forth.spi.crypto.keystore.KEY_USE;
import com.newland.forth.spi.crypto.keystore.KeyGenerateMethod;
import com.newland.forth.spi.crypto.keystore.KeyInfoID;
import com.newland.ndk.NdkApiManager;
import com.newland.ndk.napi.EM_SEC_ASYM_ENCODING_MODE;
import com.newland.ndk.napi.EM_SEC_CRYPTO_KEY_TYPE;
import com.newland.ndk.napi.EM_SEC_KEYIN_METHOD;
import com.newland.ndk.napi.EM_SEC_KEY_USAGE;
import com.newland.ndk.napi.EM_SEC_MD_TYPE;
import com.newland.ndk.napi.ST_SEC_ASYM_KEYIN_DATA;
import com.newland.ndk.napi.ST_SEC_ASYM_KEY_INFO;
import com.newland.ndk.napi.ST_SEC_KCV_DATA;
import com.newland.ndk.napi.ST_SEC_KEYIN_DATA;
import com.newland.ndk.napi.SecNapi;
import com.newland.nsdk.plugin.rkl.common.NSDKDeviceException;
import com.newland.rkl.RKLListener;
import com.newland.rkl.RKLProcessor;
import com.newland.rkl.api.NewlandAPI;
import com.newland.rkl.api.RKLAPI;
import com.newland.rkl.communicator.nl.INewlandHostCommunicator;
import com.newland.rkl.communicator.nl.v1.NewlandHostCommunicator;
import com.newland.rkl.config.LocationType;
import com.newland.rkl.exception.DeviceException;
import com.newland.rkl.exception.RKLException;
import com.newland.rkl.processor.nl.LocationUtils;
import com.newland.sdk.me.DeviceManager;
import com.newland.sdk.module.devicebasic.DeviceBasicModule;
import com.newland.sdk.module.pin.RKLParams;
import com.newland.sdk.mtype.Device;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.utils.ISOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @Description
 * @Author wuhh
 * @Date 2022/4/11
 */
public class FlyKey {
    private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger("FlyKey");
    private Context context;
    private SecNapi secNapi;
    private DeviceBasicModule deviceBasicModule;
    public FlyKey(Context context,Device device) {
        this.context = context;
        deviceBasicModule = (DeviceBasicModule) device.getStandardModule(ModuleType.DEVICE_BASIC);
        secNapi = NdkApiManager.getNdkApiManager().getSecNapi();
    }
    private String copyFileFromAssets(String fileName/*String hostType, String fileName*/) {
        InputStream in = null;
        FileOutputStream out = null;
        File directory = new File(context.getFilesDir(), "rklconfig");
        String fileToCopy = fileName;//String.format("%s_%s", hostType, fileName);
        String path = String.format("%s/%s", directory.getAbsolutePath(), fileToCopy);

        if (!directory.exists()) {
            directory.mkdirs();
            directory.setWritable(true, false);
            directory.setReadable(true, false);
            directory.setExecutable(true, false);
        }

        File configFile = new File(path);
        configFile.setWritable(true, false);
        configFile.setReadable(true, false);
        configFile.setExecutable(true, false);

        if (configFile.exists()) {
            configFile.delete();
        }

        try {
            in = context.getAssets().open(fileToCopy);
            out = new FileOutputStream(configFile);
            int length = -1;
            byte[] buf = new byte[1024];
            while ((length = in.read(buf)) != -1) {
                out.write(buf, 0, length);
            }
            out.flush();
            return path;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private RKLAPI rklapi = new NewlandAPI() {
        @Override
        public byte[] getRandom(int len) throws DeviceException {
            byte[] random = deviceBasicModule.getRandom(len);
            devicelogger.debug("[getRandom] len="+len+ " random="+ ISOUtils.hexString(random));
            if(random == null){
                throw new NSDKDeviceException(-1, String.format("Random number generation failed"));
            }
            return random;
        }

        @Override
        public String getDeviceSN() throws DeviceException {
            String sn = deviceBasicModule.getDeviceInfo().getSN();
            devicelogger.debug("[getDeviceSN] sn="+sn);
            if(sn == null || sn.equals("")){
                throw new NSDKDeviceException(-1, String.format("Gets SN failed"));
            }
            return sn;
        }

        @Override
        public String getDeviceCert(int keyID) throws DeviceException {
            devicelogger.debug("[getDeviceCert] keyID="+keyID);
            int[] len = new int[1];
            byte[] data = new byte[2048];
            int KEY_TYPE_ASYM_RSA = 0x20;
            int ret = secNapi.NAPI_SecGetKeyInfo(KeyInfoID.SEC_KEY_INFO_CERT.ordinal(),
                    keyID,KEY_TYPE_ASYM_RSA,
                    KEY_USE.KEY_USE_ASYM_AUTH.getCode(),null,0,
                    data,len);
            devicelogger.debug("[getDeviceCert] ret="+ret+" keyID="+keyID+" len="+len[0]+" data="+new String(data,0,len[0]));
            if(ret != 0){
                throw new NSDKDeviceException(ret, String.format("Gets cert failed. ret[%d] keyID[%d]",ret,keyID));
            }
            return new String(data,0,len[0]);
        }

        @Override
        public String getLocationInfo(int type) throws DeviceException {
            devicelogger.debug("[getLocationInfo] type="+type);
            if (type == 1) {
                String info = LocationUtils.getInstance(context).getLocationInfo(LocationType.GPS);
                devicelogger.debug("[getLocationInfo] GPS type="+type+" info="+info);
                return info;
            } else if (type == 0) {
                String info = LocationUtils.getInstance(context).getLocationInfo(LocationType.CELL_LOCATION);
                devicelogger.debug("[getLocationInfo] CELL_LOCATION type="+type+" info="+info);
                return info;
            } else {
                throw new NSDKDeviceException(-6, String.format("Unsupported location type: %d", type));
            }
        }

        @Override
        public void resetCertStatus() throws DeviceException {
            int ret = secNapi.NAPI_SecResetCertStatus();
            devicelogger.debug("[resetCertStatus] ret="+ret);
            if(ret != ret){
                throw new NSDKDeviceException(ret, String.format("reset cert status failed"));
            }
        }

        @Override
        public void loadTrustedCert(boolean isCA, int certId, int certType, String cert) throws DeviceException {
            devicelogger.debug("[loadTrustedCert] isCA="+isCA+" certId="+certId+" certType="+certType+" cert="+cert);
            if (TextUtils.isEmpty(cert)) {
                throw new NSDKDeviceException(-6, "Cert shall not be empty.");
            } else {
                byte[] certData = cert.getBytes();
                int[] len = new int[1];
                byte[] outData = new byte[2048];
                int ret = secNapi.NAPI_SecLoadTrustedCert((char) 0,certData,certData.length,outData,len);
                devicelogger.debug("[loadTrustedCert] NAPI_SecLoadTrustedCert ret="+ret);
                if(ret != ret){
                    throw new NSDKDeviceException(ret, String.format("LoadTrustedCert failed"));
                }
                byte[] data2 = new byte[len[0]];
                System.arraycopy(outData,0,data2,0,len[0]);
                devicelogger.debug("[loadTrustedCert] NAPI_SecLoadTrustedCert len="+len[0]+" data2="+ISOUtils.hexString(data2));
                ST_SEC_KEYIN_DATA keyData = new ST_SEC_KEYIN_DATA();
                ST_SEC_KCV_DATA kcvData = new ST_SEC_KCV_DATA();
                int KEY_TYPE_ASYM_RSA = 0x20;
                keyData.KeyType = KEY_TYPE_ASYM_RSA;
                keyData.ucKeyIdx = certId;
                keyData.pKeyData = data2;
                keyData.nKeyDataLen = data2.length;
                keyData.nKeyLen = data2.length;//1310公钥证书不匹配
                if (certType == 0) {
                    keyData.KeyUsage = KEY_USE.KEY_USE_ASYM_AUTH.getCode();
                } else {
                    keyData.KeyUsage = KEY_USE.KEY_USE_ASYM_KEY_DISTRIBUTION.getCode();
                }
                keyData.pAD = certData;//
                keyData.nADSize = certData.length;
                ret = secNapi.NAPI_SecGenerateKey(KeyGenerateMethod.SEC_KIM_CLEAR.ordinal(), keyData,kcvData);
                devicelogger.debug("[loadTrustedCert] NAPI_SecGenerateKey ret="+ret);
                if(ret != 0){
                    throw new NSDKDeviceException(ret, String.format("LoadTrustedCert step generate key failed"));
                }
            }
        }

        @Override
        public byte[] generateSKEncryptedByTrustedCert(int certId, int skId, int skType, int skLen) throws DeviceException {
            devicelogger.debug("[generateSKEncryptedByTrustedCert] certId="+certId+" skId="+skId+" skType="+skType+" skLen="+skLen);
            ST_SEC_ASYM_KEYIN_DATA keyData = new ST_SEC_ASYM_KEYIN_DATA();
            ST_SEC_KCV_DATA kcvData = new ST_SEC_KCV_DATA();
            keyData.KEKType = EM_SEC_CRYPTO_KEY_TYPE.KEY_TYPE_ASYM_RSA;
            keyData.KEKUsage = EM_SEC_KEY_USAGE.KEY_USE_ASYM_KEY_DISTRIBUTION;
            keyData.ucKEKIdx = certId;

            keyData.ucKeyIdx = skId;
            keyData.KeyUsage = EM_SEC_KEY_USAGE.KEY_USE_TR31_KEK;
            keyData.nKeyLen = skLen;
            if (skType == 0) {
                keyData.KeyType = EM_SEC_CRYPTO_KEY_TYPE.KEY_TYPE_DES;
            } else {
                if (skType != 1) {
                    throw new NSDKDeviceException(-6, String.format("Unsupported session key type: %s", skType));
                }
                keyData.KeyType = EM_SEC_CRYPTO_KEY_TYPE.KEY_TYPE_AES;
            }
            keyData.MdAlg = EM_SEC_MD_TYPE.SEC_MD_SHA256;
            keyData.EncodingMode = EM_SEC_ASYM_ENCODING_MODE.ASYM_RSA_PKCS_V21;

            keyData.pAD = new byte[4];
            keyData.nADSize = keyData.pAD.length;
            keyData.pKeyData = new byte[1024];

//            int[] len = new int[1];
//            byte[] data = new byte[256];
//            int ret = secNapi.NewlandV1SecAsymGenerateKey(certId,skId,data,skType,skLen,len);
//            if(ret != 0){
//                throw new NSDKDeviceException(ret, String.format("generateSKEncryptedByTrustedCert step failed"));
//            }
//            byte[] skData = new byte[len[0]];
//            System.arraycopy(data,0,skData,0,skData.length);
//            devicelogger.debug("[generateSKEncryptedByTrustedCert] len="+len[0]+" skData="+ISOUtils.hexString(skData));
//            return skData;

            int ret = secNapi.NAPI_SecAsymGenerateKey(EM_SEC_KEYIN_METHOD.SEC_KIM_RANDOM_OUT,keyData,kcvData);
            devicelogger.debug("[generateSKEncryptedByTrustedCert] ret="+ret);
            if(ret != 0){
                throw new NSDKDeviceException(ret, String.format("generateSKEncryptedByTrustedCert step failed"));
            }
            devicelogger.debug("[generateSKEncryptedByTrustedCert] keyData.pAD="+ISOUtils.hexString(keyData.pAD));
            devicelogger.debug("[generateSKEncryptedByTrustedCert] keyData.pKeyData="+ISOUtils.hexString(keyData.pKeyData));
            int keyLen = InnerUtils.bytesToInt(keyData.pAD,0,4,false);
            devicelogger.debug("[generateSKEncryptedByTrustedCert] keyLen="+keyLen);
            byte[] key = new byte[keyLen];
            System.arraycopy(keyData.pKeyData,0,key,0,key.length);
            devicelogger.debug("[generateSKEncryptedByTrustedCert] key="+ISOUtils.hexString(key));
            return key;
        }

        @Override
        public void loadTR31KeyBlock(int kekID, int kekType, int keyId, int keyType, int keyUsage, String tr31String) throws DeviceException {
            devicelogger.debug("[loadTR31KeyBlock] kekID="+kekID+" kekType="+kekType+" keyId="+keyId+" keyType="+keyType+" keyUsage="+keyUsage+" tr31String="+tr31String);
            if (TextUtils.isEmpty(tr31String)) {
                throw new NSDKDeviceException(-6, "Key content shall not be empty.");
            } else {
                ST_SEC_KEYIN_DATA keyData = new ST_SEC_KEYIN_DATA();
                ST_SEC_KCV_DATA kcvData = new ST_SEC_KCV_DATA();
                keyData.ucKEKIdx = kekID;
                if (kekType == 0) {
                    keyData.KEKType = EM_SEC_CRYPTO_KEY_TYPE.KEY_TYPE_DES.getCode();
                } else {
                    if (kekType != 1) {
                        throw new NSDKDeviceException(-6, String.format("Unsupported KEK type: %s", kekType));
                    }
                    keyData.KEKType = EM_SEC_CRYPTO_KEY_TYPE.KEY_TYPE_AES.getCode();
                }

                keyData.KEKUsage = EM_SEC_KEY_USAGE.KEY_USE_TR31_KEK.getCode();
                if(kekType == 0){
                    keyData.KeyType = EM_SEC_CRYPTO_KEY_TYPE.KEY_TYPE_DES.getCode();
                }else if(kekType == 1){
                    keyData.KeyType = EM_SEC_CRYPTO_KEY_TYPE.KEY_TYPE_AES.getCode();
                }else {
                    throw new NSDKDeviceException(-6, String.format("Unsupported key type: %s", kekType));
                }
                keyData.KeyUsage = keyUsage;
                keyData.ucKeyIdx = keyId;
                byte[] tr31KeyData = tr31String.getBytes();
                keyData.nKeyLen =tr31KeyData.length;
                keyData.pKeyData = tr31KeyData;
                keyData.nKeyDataLen = tr31KeyData.length;
                int ret = secNapi.NAPI_SecGenerateKey(KeyGenerateMethod.SEC_KIM_TR31.ordinal(),keyData,kcvData);
                devicelogger.debug("[loadTR31KeyBlock] NAPI_SecGenerateKey ret="+ret);
                if(ret != 0){
                    throw new NSDKDeviceException(ret, String.format("load TR31 key block failed"));
                }
            }
        }

        @Override
        public byte[] sign(int certId, byte[] hash) throws DeviceException {
            devicelogger.debug("[sign] certId="+certId+" hash="+hash);
            ST_SEC_ASYM_KEY_INFO asymKeyInfo = new ST_SEC_ASYM_KEY_INFO();
            asymKeyInfo.KeyIdx = certId;
            asymKeyInfo.KeytType = EM_SEC_CRYPTO_KEY_TYPE.KEY_TYPE_ASYM_RSA;
            asymKeyInfo.KeyUsage = EM_SEC_KEY_USAGE.KEY_USE_ASYM_AUTH;
            int[] signLen = new int[1];
            byte[] sign = new byte[1024];
            int ret = secNapi.NAPI_SecAsymSign(asymKeyInfo,
                    EM_SEC_MD_TYPE.SEC_MD_SHA256,
                    EM_SEC_ASYM_ENCODING_MODE.ASYM_RSA_PKCS_V15,
                    hash.length,hash,signLen,sign);
            devicelogger.debug("[sign] NAPI_SecAsymSign ret="+ret);
            if(ret != 0){
                throw new NSDKDeviceException(ret, String.format("sign failed"));
            }
            byte[] signData = new byte[signLen[0]];
            System.arraycopy(sign,0,signData,0,signLen[0]);
            devicelogger.debug("[sign] signLen="+signLen[0]+" signData="+ISOUtils.hexString(signData));
            return signData;
        }

        @Override
        public boolean verify(int certId, byte[] hash, byte[] signedData) throws DeviceException {
            devicelogger.debug("[verify] certId="+certId+" hash="+ISOUtils.hexString(hash)+" signedData="+ISOUtils.hexString(signedData));
            ST_SEC_ASYM_KEY_INFO asymKeyInfo = new ST_SEC_ASYM_KEY_INFO();
            asymKeyInfo.KeyIdx = certId;
            asymKeyInfo.KeytType = EM_SEC_CRYPTO_KEY_TYPE.KEY_TYPE_ASYM_RSA;
            asymKeyInfo.KeyUsage = EM_SEC_KEY_USAGE.KEY_USE_ASYM_AUTH;
            int ret = secNapi.NAPI_SecAsymVerify(asymKeyInfo,EM_SEC_MD_TYPE.SEC_MD_SHA256,
                    EM_SEC_ASYM_ENCODING_MODE.ASYM_RSA_PKCS_V15,hash.length,hash,signedData.length,signedData);
            devicelogger.debug("[verify] NAPI_SecAsymVerify ret="+ret);
            if(ret != 0){
                throw new NSDKDeviceException(ret, String.format("verify failed"));
            }
            return true;
        }

        @Override
        public void initAtomic() throws DeviceException {
            devicelogger.debug("[initAtomic]");
            int ret = secNapi.NAPI_SecInitAtomic();
            if(ret != 0){
                throw new NSDKDeviceException(ret, String.format("initAtomic failed"));
            }
        }

        @Override
        public void commitAtomic(boolean isSuccess) throws DeviceException {
            devicelogger.debug("[commitAtomic] isSuccess="+isSuccess);
            char status = 0;
            if(isSuccess){
                status = 1;
            }
            int ret = secNapi.NAPI_SecCommitAtomic(status);
            if(ret != 0){
                throw new NSDKDeviceException(ret, String.format("commitAtomic failed"));
            }
        }

        @Override
        public String getSSLCert() throws DeviceException {
            devicelogger.debug("[getSSLCert]");
            BufferedReader bufReader = null;
            String sslCert;
            try {
                bufReader = new BufferedReader(new InputStreamReader(new FileInputStream("/system/etc/rki_pki/ca/RKMS_TLS_SubCA.crt")));
                String line = null;
                boolean found = false;
                StringBuilder sb = new StringBuilder();

                while((line = bufReader.readLine()) != null) {
                    if (line.equals("-----BEGIN CERTIFICATE-----")) {
                        found = true;
                    }
                    if (found) {
                        sb.append(line);
                        sb.append("\n");
                    }
                }
                sslCert = sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
                throw new NSDKDeviceException(-1, "Failed to read SSL cert.", e);
            } finally {
                try {
                    if (bufReader != null) {
                        bufReader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sslCert;
        }

        @Override
        public INewlandHostCommunicator getHostCommunicator() throws DeviceException {
            return NewlandHostCommunicator.getInstance();
        }
    };

    public void startRKL(RKLParams params, RKLListener listener){
        try {
            devicelogger.debug("[startRKL] getConfigFile="+params.getConfigFile()+" getRklApi="+params.getRklApi());
            if(params.getRklApi() == RKLParams.RKLAPIType.NewlandAPI){
                RKLProcessor.getInstance().setAPI(rklapi);
            }else {
                return;
            }
            String configPath = copyFileFromAssets(params.getConfigFile());
            RKLProcessor.getInstance().start(configPath, listener);
        } catch (RKLException e) {
            e.printStackTrace();
        }
    }
}
