package com.newland.nsdk.externaldevice.keymanager;

import com.newland.nsdk.core.api.common.crypto.AsymEncodingMode;
import com.newland.nsdk.core.api.common.crypto.MessageDigestType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.keymanager.AsymKeyType;
import com.newland.nsdk.core.api.common.keymanager.AsymKeyUsage;
import com.newland.nsdk.core.api.common.keymanager.AsymmetricKey;
import com.newland.nsdk.core.api.common.keymanager.KeyGenerateMethod;
import com.newland.nsdk.core.api.common.keymanager.KeyInfoID;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.api.common.keymanager.KeyUsage;
import com.newland.nsdk.core.common.keymanager.ST_SEC_ASYM_KEYIN_DATA;
import com.newland.nsdk.core.common.keymanager.ST_SEC_KCV_DATA;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.external.communication.ExternalCommunicator;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.communication.mock.MockExternalCommunicator;
import com.newland.nsdk.core.external.command.keymanager.ExternalKeyManagerModule;

import org.junit.Before;
import org.junit.Test;

public class ExternalKeyManagerModuleTest {

    private ExternalKeyManagerModule keyManagerModule = new ExternalKeyManagerModule();

    @Before
    public void init() {
        ExternalCommunicator communicator = new MockExternalCommunicator();
        ExternalCommunicationManager.getInstance().setCommunicator(communicator);
    }

    @Test
    public void loadKeyBlock() {
        try {
            keyManagerModule.loadKeyBlock((byte) 1, (byte) 2, (byte) 3, (byte) 4, new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08}, new byte[]{0x11, 0x22, 0x33}, -1);
//            keyManagerModule.loadKeyBlock(1, 1, 3, 4, new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08}, new byte[]{0x11, 0x22, 0x33}, 16);
//            keyManagerModule.loadKeyBlock(256, 2, 3, 4, new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08}, new byte[]{0x11, 0x22, 0x33}, -1);
//            keyManagerModule.loadKeyBlock(1, 3, 3, 4, new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08}, new byte[]{0x11, 0x22, 0x33}, -1);
//            keyManagerModule.loadKeyBlock(1, 2, 4, 4, new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08}, new byte[]{0x11, 0x22, 0x33}, -1);
//            keyManagerModule.loadKeyBlock(1, 2, 3, 256, new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08}, new byte[]{0x11, 0x22, 0x33}, -1);
//            keyManagerModule.loadKeyBlock(1, 2, 3, 4, null, new byte[]{0x11, 0x22, 0x33}, -1);
//            keyManagerModule.loadKeyBlock(1, 2, 3, 4, new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08}, new byte[]{0x11, 0x22}, -1);
//            keyManagerModule.loadKeyBlock(1, 1, 3, 4, new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08}, new byte[]{0x11, 0x22, 0x33}, 22);
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadDukptBlock() {
        try {
//            keyManagerModule.loadDukptBlock(0, 1, (byte) 1, new byte[]{0x11, 0x22, 0x33, 0x44, 0x55}, new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A});
//            keyManagerModule.loadDukptBlock(0, 1, (byte) 0, new byte[]{0x11, 0x22, 0x33, 0x44, 0x55}, null);
            keyManagerModule.loadDukptBlock((byte) 256, (byte) 1, (byte) 2, new byte[]{0x11, 0x22, 0x33, 0x44, 0x55}, null);
//            keyManagerModule.loadDukptBlock(0, 256, (byte) 0, new byte[]{0x11, 0x22, 0x33, 0x44, 0x55}, null);
//            keyManagerModule.loadDukptBlock(0, 1, (byte) 3, new byte[]{0x11, 0x22, 0x33, 0x44, 0x55}, null);
//            keyManagerModule.loadDukptBlock(0, 1, (byte) 0, null, null);
//            keyManagerModule.loadDukptBlock(0, 1, (byte) 1, new byte[]{0x11, 0x22, 0x33, 0x44, 0x55}, null);
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getKcv() {
        try {
            byte[] kcv = keyManagerModule.getKcv((byte) 1, (byte) 3);
            System.out.println(String.format("Get KCV: %s", ISOUtils.hexString(kcv)));
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getKsn() {
        try {
            byte[] ksn = keyManagerModule.getKsn((byte) 1);
            System.out.println(String.format("Get KSN: %s", ISOUtils.hexString(ksn)));
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadGiskeKey() {
        try {
            keyManagerModule.loadGiskeKey((byte) 2, (byte) 3, (byte) 4, new byte[]{0x11, 0x22, 0x33}, new byte[]{0x77, (byte) 0x88, (byte) 0x99});
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadGiskeTik() {
        try {
            keyManagerModule.loadGiskeTik((byte) 2, (byte) 3, (byte) 4, new byte[]{0x11, 0x22, 0x33}, new byte[]{0x44, 0x55, 0x66}, new byte[]{0x77, (byte) 0x88, (byte) 0x99});
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void convertAtmToGiske() {
        try {
            keyManagerModule.convertAtmToGiske((byte) 2, (byte) 3, new byte[]{0x11, 0x22, 0x33});
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteKey() {
        try {
            keyManagerModule.deleteKey((byte) 1, (byte) 0, (byte) 0);
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void increaseKsn() {
        try {
            keyManagerModule.increaseKsn((byte) 1);
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void generateKeyNapi() {
        SymmetricKey srcKey = new SymmetricKey();
        srcKey.setKeyID((byte) 0);
        srcKey.setKeyType(KeyType.DES);
        srcKey.setKeyUsage(KeyUsage.KEK);

        SymmetricKey dstKey = new SymmetricKey();
        dstKey.setKeyID((byte) 1);
        dstKey.setKeyType(KeyType.DES);
        dstKey.setKeyUsage(KeyUsage.PIN);
        dstKey.setKeyLen(16);
        dstKey.setKeyData(new byte[]{0x31, 0x31, 0x31, 0x31, 0x31, 0x31, 0x31, 0x31, 0x31, 0x31, 0x31, 0x31, 0x31, 0x31, 0x31, 0x31});

        byte[] iv = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};

        dstKey.setKCV(new byte[]{0x23,0x21,0x14});
        try {
            keyManagerModule.generateKeyNapi(KeyGenerateMethod.CIPHER, null, srcKey, dstKey, iv);
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void generateKeyWithAsymKey() {
        ST_SEC_ASYM_KEYIN_DATA keyinData = new ST_SEC_ASYM_KEYIN_DATA();
        ST_SEC_KCV_DATA kcvData = new ST_SEC_KCV_DATA();
        try {
            keyinData.setEncodingMode(AsymEncodingMode.PKCS_V21.ordinal());
            keyinData.setMdAlg(MessageDigestType.SHA256.ordinal());
            keyinData.setKeyType(KeyType.DES.ordinal());

            byte[] ret = keyManagerModule.generateKeyWithAsymKey(KeyGenerateMethod.RANDOM_OUT.ordinal(), keyinData, kcvData);
            String random = ret == null ? "null" : ISOUtils.hexString(ret);
            System.out.println("generateKeyWithAsymKey : " + random);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getKeyInfo() {
        AsymmetricKey krdSignKey = new AsymmetricKey();
        krdSignKey.setKeyID((byte) 255);
        krdSignKey.setKeyUsage(AsymKeyUsage.AUTH);
        krdSignKey.setKeyType(AsymKeyType.RSA);

        try {
            byte[] result = keyManagerModule.getKeyInfoNapi(KeyInfoID.CERTIFICATE, krdSignKey);
            System.out.println("Key info: " + ISOUtils.hexString(result));
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadTrustedCert() {
        //TODO 证书数据从nsdkdemo中读取

        try {
            byte[] result = keyManagerModule.loadTrustedCert(true, null);
            System.out.println("Key info: " + ISOUtils.hexString(result));
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void resetCertStatus() {
        try {
            keyManagerModule.resetCertStatus();
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void initAtomic() {
        try {
            keyManagerModule.initAtomic();
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void commitAtomic() {
        try {
            keyManagerModule.commitAtomic(true);
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }
}