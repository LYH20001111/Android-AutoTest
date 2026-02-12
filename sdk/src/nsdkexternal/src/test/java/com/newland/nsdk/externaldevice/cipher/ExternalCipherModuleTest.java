package com.newland.nsdk.externaldevice.cipher;


import com.newland.nsdk.core.api.common.crypto.AsymAlgorithmParameters;
import com.newland.nsdk.core.api.common.crypto.AsymCryptoMode;
import com.newland.nsdk.core.api.common.crypto.AsymEncodingMode;
import com.newland.nsdk.core.api.common.crypto.CipherOutput;
import com.newland.nsdk.core.api.common.crypto.CipherType;
import com.newland.nsdk.core.api.common.crypto.MACOutput;
import com.newland.nsdk.core.api.common.crypto.MACType;
import com.newland.nsdk.core.api.common.crypto.MessageDigestType;
import com.newland.nsdk.core.api.common.crypto.PaddingMode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.keymanager.AsymKeyType;
import com.newland.nsdk.core.api.common.keymanager.AsymKeyUsage;
import com.newland.nsdk.core.api.common.keymanager.AsymmetricKey;
import com.newland.nsdk.core.api.common.keymanager.KeyUsage;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.external.communication.ExternalCommunicator;
import com.newland.nsdk.core.api.external.crypto.ExtCrypto;
import com.newland.nsdk.core.external.command.cipher.ExtMacBlockFlag;
import com.newland.nsdk.core.external.command.cipher.ExternalCipherModule;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.communication.mock.MockExternalCommunicator;
import com.newland.nsdk.core.external.ExtCryptoImpl;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;

public class ExternalCipherModuleTest {

    private ExternalCipherModule cipherModule = new ExternalCipherModule();
    private ExtCrypto extCipher = new ExtCryptoImpl();

    @Before
    public void init(){
        ExternalCommunicator communicator = new MockExternalCommunicator();
        ExternalCommunicationManager.getInstance().setCommunicator(communicator);
    }

    @Test
    public void encryptOrDecrypt() {
        try {
            byte[] result = cipherModule.encryptOrDecryptNdk((byte)3, (byte)2, null, new byte[]{0x11, 0x22, 0x33, 0x44}, (byte)0, null);
//            byte[] result = cipherModule.encryptOrDecrypt(3, 1, new byte[]{0x11, 0x22, 0x33, 0x44}, null, 0, null);
//            byte[] result = cipherModule.encryptOrDecrypt(3, 2, null, null, 0, null);
//            byte[] result = cipherModule.encryptOrDecrypt(3, 2, new byte[]{0x11, 0x22, 0x33, 0x44}, null, 0, new byte[]{(byte) 0xB1, (byte) 0xB2, (byte) 0xB3});
            if (result != null) {
                System.out.println(String.format("Encrypted data: %s", ISOUtils.hexString(result)));
            }
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void encryptOrDecryptNapi() {
        SymmetricKey key = new SymmetricKey();
        key.setKeyID((byte) 4);
        key.setKeyUsage(KeyUsage.DATA);
        byte[] iv = new byte[]{0x11, 0x22, 0x33, 0x44};

        byte[] data = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07};

        try {
            CipherOutput result = cipherModule.encryptOrDecryptNapi((byte)0, key, CipherType.DES_CBC, PaddingMode.NONE, iv, data);
            if (result != null) {
                System.out.println(String.format("Encrypted data: %s", ISOUtils.hexString(result.getData())));
                System.out.println(String.format("KSN: %s", ISOUtils.hexString(result.getKsn())));
            }
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void generateMac() {
        byte[] data = new byte[]{0x11, 0x12, 0x13};
        byte[] key = new byte[]{0x21, 0x22, 0x23};
        try {
            for(int keyType = 0; keyType < 3; keyType ++) {
                for (int flag = 0; flag < 4; flag ++) {
                    System.out.println(String.format("Key type: %d, flag: %d", keyType, flag));
                    MACOutput macOutput = cipherModule.generateMacNdk((byte)3, (byte)keyType, (byte)3, (byte)flag, data, (byte)0, key);
                    if (macOutput == null) {
                        System.out.println("Mac output is null");
                    } else {
                        if (macOutput.getData() != null) {
                            System.out.println("Data: "+ ISOUtils.hexString(macOutput.getData()));
                        }
                        if (macOutput.getKsn() != null) {
                            System.out.println("KSN: "+ ISOUtils.hexString(macOutput.getKsn()));
                        }
                    }
                }
            }
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void generateMacNapi() {
        byte[] data = new byte[]{0x11, 0x12, 0x13};
        byte[] iv = new byte[]{0x21, 0x22, 0x23};
        try {
            System.out.println("First package.");
            MACOutput macOutput = cipherModule.generateMacNapi((byte)3, MACType.TDES_X99, iv, data, ExtMacBlockFlag.FIRST);
            if (macOutput == null) {
                System.out.println("Mac output is null");
            } else {
                if (macOutput.getData() != null) {
                    System.out.println("Data: "+ ISOUtils.hexString(macOutput.getData()));
                }
                if (macOutput.getKsn() != null) {
                    System.out.println("KSN: "+ ISOUtils.hexString(macOutput.getKsn()));
                }
            }

            System.out.println("Next package.");
            macOutput = cipherModule.generateMacNapi((byte)3, MACType.TDES_X99, iv, data, ExtMacBlockFlag.NEXT);
            if (macOutput == null) {
                System.out.println("Mac output is null");
            } else {
                if (macOutput.getData() != null) {
                    System.out.println("Data: "+ ISOUtils.hexString(macOutput.getData()));
                }
                if (macOutput.getKsn() != null) {
                    System.out.println("KSN: "+ ISOUtils.hexString(macOutput.getKsn()));
                }
            }

            System.out.println("Last package.");
            macOutput = cipherModule.generateMacNapi((byte)3, MACType.TDES_X99, iv, data, ExtMacBlockFlag.LAST);
            if (macOutput == null) {
                System.out.println("Mac output is null");
            } else {
                if (macOutput.getData() != null) {
                    System.out.println("Data: "+ ISOUtils.hexString(macOutput.getData()));
                }
                if (macOutput.getKsn() != null) {
                    System.out.println("KSN: "+ ISOUtils.hexString(macOutput.getKsn()));
                }
            }

            System.out.println("Only package.");
            macOutput = cipherModule.generateMacNapi((byte)3, MACType.TDES_X99, iv, data, ExtMacBlockFlag.ONLY);
            if (macOutput == null) {
                System.out.println("Mac output is null");
            } else {
                if (macOutput.getData() != null) {
                    System.out.println("Data: "+ ISOUtils.hexString(macOutput.getData()));
                }
                if (macOutput.getKsn() != null) {
                    System.out.println("KSN: "+ ISOUtils.hexString(macOutput.getKsn()));
                }
            }
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void aesEncryptOrDecrypt() {
        try {
            byte[] result = cipherModule.aesEncryptOrDecryptNdk((byte) 1, (byte) 2, new byte[]{0x11, 0x22, 0x33, 0x44});
            if (result != null) {
                System.out.println(String.format("Encrypted data: %s", ISOUtils.hexString(result)));
            }
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void dukptEncryptOrDecrypt() {
        byte[] data = new byte[]{0x11, 0x22, 0x33};
        byte[] iv = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        try {
            CipherOutput output = cipherModule.dukptEncryptOrDecryptNdk((byte) 1, (byte) 2, (byte) 3, iv, data);
            if (output.getData() != null) {
                System.out.println("Data: "+ ISOUtils.hexString(output.getData()));
            }
            if (output.getKsn() != null) {
                System.out.println("KSN: "+ ISOUtils.hexString(output.getKsn()));
            }
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void generateMacTest(){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        byte[] iv = new byte[8];
        Arrays.fill(iv, (byte) 0);

        int dataLen = 1024;
        byte[] data1 = new byte[dataLen];
        Arrays.fill(data1, (byte) 1);

        byte[] data2 = new byte[dataLen];
        Arrays.fill(data2, (byte) 2);

        byte[] data3 = new byte[dataLen];
        Arrays.fill(data3, (byte) 3);

        byte[] data4 = new byte[dataLen];
        Arrays.fill(data4, (byte) 4);

        byte[] data5 = new byte[5];
        Arrays.fill(data5, (byte) 5);

        try {
            stream.write(data1);
            stream.write(data2);
            stream.write(data3);
            stream.write(data4);
            stream.write(data5);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            MACOutput result = extCipher.generateMAC((byte) 0, MACType.TDES_X99, iv, stream.toByteArray());
            System.out.println(result == null ? "null":ISOUtils.hexString(result.getData()));
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getRandomTest(){
        try {
            byte[] data = cipherModule.getRandeom(8);
            if (data != null) {
                System.out.println("Random data: "+ ISOUtils.hexString(data));
            }
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void asymEncryptOrDecryptNapiTest(){
        AsymmetricKey key = new AsymmetricKey();
        key.setKeyID((byte) 3);
        key.setKeyUsage(AsymKeyUsage.DATA);
        key.setKeyType(AsymKeyType.RSA);

        byte[] data = ISOUtils.hex2byte("0102030405060708090A0B0C0D0E0F");

        try {
            byte[] ret = cipherModule.asymEncryptOrDecryptNapi((byte)0,key,MessageDigestType.SHA256,
                    AsymEncodingMode.PKCS_V21,AsymCryptoMode.PUBLIC,data);
            if (ret != null) {
                System.out.println("asymEncryptOrDecryptNapi data: "+ ISOUtils.hexString(ret));
            }
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void signVerifyAsymTest(){
        AsymmetricKey key = new AsymmetricKey();
//        key.setKeyID((byte) 10);
        key.setKeyID((byte) 2);
        key.setKeyUsage(AsymKeyUsage.AUTH);
        key.setKeyType(AsymKeyType.RSA);

        AsymAlgorithmParameters parameters = new AsymAlgorithmParameters();
        parameters.setMessageDigestType(MessageDigestType.SHA256);
        parameters.setEncodingMode(AsymEncodingMode.PKCS_V15);

        String originalString = "12345678";
        byte[] hash = null;
        byte[] signedData = null;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            hash = digest.digest(originalString.getBytes());

            byte[] ret = cipherModule.signVerifyAsym(true,key,parameters,hash,null);
            if (ret != null) {
                System.out.println("asymEncryptOrDecryptNapi data: "+ ISOUtils.hexString(ret));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}