package com.newland.nsdkdemo.internal.utils;

import android.util.Log;

import com.newland.nsdk.core.api.common.crypto.CipherOutput;
import com.newland.nsdk.core.api.common.crypto.CipherType;
import com.newland.nsdk.core.api.common.crypto.PaddingMode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.internal.crypto.Crypto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is a helper class for encrypt or decrypt big data.
 */
public class CryptoHelper {
    private static final String TAG = "CryptoHelper";

    private static final int SINGLE_PACKAGE_LEN = 2000;

    private static final int MIN_AES_LEN = 16;

    private static final int MIN_DES_LEN = 8;
    private static final int AES_CTR = 1;
    private static final int DES_CTR = 2;

    private Crypto crypto;
    public CryptoHelper(Crypto crypto) {
        this.crypto = crypto;
    }

    /**
     * This method is used to encrypt big data in ECB/CBC encryption mode.
     * @param key
     * @param cipherType
     * @param paddingMode
     * @param iv
     * @param data
     * @return
     * @throws NSDKException
     */
    public CipherOutput encrypt(SymmetricKey key, CipherType cipherType, PaddingMode paddingMode, byte[] iv, byte[] data) throws NSDKException {
        if (key == null || cipherType == null || data == null) {
            throw new NSDKIllegalParameterException("SymmetricKey, cipher type and data shall not be null.");
        }
        if ((cipherType == CipherType.DES_CBC || cipherType == CipherType.AES_CBC) && paddingMode == PaddingMode.NONE) {
            throw new NSDKIllegalParameterException("Padding mode shall not be null when cipher type is CBC mode.");
        }
        int packages = (int) Math.ceil(data.length / (SINGLE_PACKAGE_LEN * 1.0));
        //when data length less than 2000, packages is 1.
        if (packages == 0) {
            packages = 1;
        }
        byte[] ksn = null;
        byte[] iv2 = null;
        boolean isAesMode = false;
        if (key.getKeyType() == KeyType.AES) {
            if (iv != null && iv.length != MIN_AES_LEN) {
                throw new NSDKIllegalParameterException("The IV of AES mode shall be 16 bytes.");
            }
            iv2 = new byte[MIN_AES_LEN];
            isAesMode = true;
        } else {
            if (iv != null && iv.length != MIN_DES_LEN) {
                throw new NSDKIllegalParameterException("The IV of DES mode shall be 8 bytes.");
            }
            iv2 = new byte[MIN_DES_LEN];
        }
        if (iv != null) {
            System.arraycopy(iv, 0, iv2, 0, iv2.length);
        }


        List<byte[]> dataPackages = spiltData(data, packages);
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            for (byte[] dataSpilt : dataPackages) {
                if (dataSpilt.length % MIN_AES_LEN == 0) {
                    paddingMode = PaddingMode.NONE;
                } else {
                    paddingMode = PaddingMode.ZEROS;
                }
                CipherOutput cipherOutput = crypto.encrypt(key, cipherType, paddingMode, iv2, dataSpilt);
                if (isAesMode) {
                    System.arraycopy(cipherOutput.getData(), cipherOutput.getData().length - MIN_AES_LEN, iv2, 0, MIN_AES_LEN);
                } else {
                    System.arraycopy(cipherOutput.getData(), cipherOutput.getData().length - MIN_DES_LEN, iv2, 0, MIN_DES_LEN);
                }
                ksn = cipherOutput.getKsn();
                stream.write(cipherOutput.getData());
            }
            return new CipherOutput(stream.toByteArray(), ksn);
        } catch (IOException e) {
            throw new NSDKException(e);
        }
    }

    /**
     * This method is used to encrypt big data in CTR encryption mode.
     * @param key
     * @param cipherType
     * @param paddingMode
     * @param iv
     * @param data
     * @return
     * @throws NSDKException
     */
    public CipherOutput encryptWithCTR(SymmetricKey key, CipherType cipherType, PaddingMode paddingMode, byte[] iv, byte[] data) throws NSDKException {
        if (key == null || cipherType == null || data == null) {
            throw new NSDKIllegalParameterException("SymmetricKey, cipher type and data shall not be null.");
        }
        if (cipherType != CipherType.DES_CTR && cipherType != CipherType.AES_CTR) {
            throw new NSDKIllegalParameterException("Cipher type shall be CTR.");
        }
        int spiltLen = cipherType == CipherType.AES_CTR ? MIN_AES_LEN : MIN_DES_LEN;
        int packages = (int) Math.ceil(data.length / (spiltLen * 1.0));
        //when data length less than 2000, packages is 1.
        if (packages == 0) {
            packages = 1;
        }
        byte[] ksn = null;
        byte[] iv2 = null;
        boolean isAesMode = false;
        if (key.getKeyType() == KeyType.AES) {
            if (iv != null && iv.length != MIN_AES_LEN) {
                throw new NSDKIllegalParameterException("The IV of AES mode shall be 16 bytes.");
            }
            iv2 = new byte[MIN_AES_LEN];
            isAesMode = true;
        } else {
            if (iv != null && iv.length != MIN_DES_LEN) {
                throw new NSDKIllegalParameterException("The IV of DES mode shall be 8 bytes.");
            }
            iv2 = new byte[MIN_DES_LEN];
        }
        if (iv != null) {
            System.arraycopy(iv, 0, iv2, 0, iv2.length);
        }


        List<byte[]> dataPackages = spiltDataInCTRMode(data, packages, (cipherType == CipherType.AES_CTR ? AES_CTR : DES_CTR));
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            for (byte[] dataSpilt : dataPackages) {
                if (dataSpilt.length % MIN_AES_LEN == 0) {
                    paddingMode = PaddingMode.NONE;
                } else {
                    paddingMode = PaddingMode.ZEROS;
                }
                CipherOutput cipherOutput = crypto.encrypt(key, cipherType, paddingMode, iv2, dataSpilt);
                iv2 = incrementCounter(iv2, 0, iv2.length, (cipherType == CipherType.AES_CTR ? AES_CTR : DES_CTR));
                ksn = cipherOutput.getKsn();
                stream.write(cipherOutput.getData());
            }
            return new CipherOutput(stream.toByteArray(), ksn);
        } catch (IOException e) {
            throw new NSDKException(e);
        }
    }

    /**
     * This method is used to decrypt big data in ECB/CBC decryption mode.
     * @param key
     * @param cipherType
     * @param paddingMode
     * @param iv
     * @param data
     * @return
     * @throws NSDKException
     */
    public CipherOutput decrypt(SymmetricKey key, CipherType cipherType, PaddingMode paddingMode, byte[] iv, byte[] data) throws NSDKException {
        if (key == null || cipherType == null || data == null) {
            throw new NSDKIllegalParameterException("SymmetricKey, cipher type and data shall not be null.");
        }
        if ((cipherType == CipherType.AES_CBC || cipherType == CipherType.DES_CBC) && paddingMode == PaddingMode.NONE) {
            throw new NSDKIllegalParameterException("Padding mode shall not be null when cipher type is CBC mode.");
        }
        byte[] iv2 = null;
        boolean isAesMode = false;
        if (key.getKeyType() == KeyType.AES) {
            if (iv != null && iv.length != MIN_AES_LEN) {
                throw new NSDKIllegalParameterException("The IV of AES mode shall be 16 bytes.");
            }
            iv2 = new byte[MIN_AES_LEN];
            isAesMode = true;
        } else {
            if (iv != null && iv.length != MIN_DES_LEN) {
                throw new NSDKIllegalParameterException("The IV of DES mode shall be 8 bytes.");
            }
            iv2 = new byte[MIN_DES_LEN];
        }
        if (iv != null) {
            System.arraycopy(iv, 0, iv2, 0, iv2.length);
        }
        int packages = (int) Math.ceil(data.length / (SINGLE_PACKAGE_LEN * 1.0));
        List<byte[]> packagesData = spiltData(data, packages);
        byte[] ksn = null;
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            for (byte[] dataSpilt : packagesData) {
                CipherOutput cipherOutput = crypto.decrypt(key, cipherType, paddingMode, iv2, dataSpilt);
                ksn = cipherOutput.getKsn();
                if (isAesMode) {
                    System.arraycopy(dataSpilt, dataSpilt.length - MIN_AES_LEN, iv2, 0, MIN_AES_LEN);
                } else {
                    System.arraycopy(dataSpilt, dataSpilt.length - MIN_DES_LEN, iv2, 0, MIN_DES_LEN);
                }
                stream.write(cipherOutput.getData());
            }
            return new CipherOutput(stream.toByteArray(), ksn);
        } catch (IOException e) {
            throw new NSDKException(e);
        }
    }

    /**
     * This method is used to decrypt big data in CTR decryption mode.
     * @param key
     * @param cipherType
     * @param paddingMode
     * @param iv
     * @param data
     * @return
     * @throws NSDKException
     */
    public CipherOutput decryptWithCTR(SymmetricKey key, CipherType cipherType, PaddingMode paddingMode, byte[] iv, byte[] data) throws NSDKException {
        if (key == null || cipherType == null || data == null) {
            throw new NSDKIllegalParameterException("SymmetricKey, cipher type and data shall not be null.");
        }
        if (cipherType != CipherType.AES_CTR && cipherType != CipherType.DES_CTR) {
            throw new NSDKIllegalParameterException("Cipher type shall be CTR mode.");
        }

        byte[] iv2 = null;
        if (key.getKeyType() == KeyType.AES) {
            if (iv != null && iv.length != MIN_AES_LEN) {
                throw new NSDKIllegalParameterException("The IV of AES mode shall be 16 bytes.");
            }
            iv2 = new byte[MIN_AES_LEN];
        } else {
            if (iv != null && iv.length != MIN_DES_LEN) {
                throw new NSDKIllegalParameterException("The IV of DES mode shall be 8 bytes.");
            }
            iv2 = new byte[MIN_DES_LEN];
        }
        if (iv != null) {
            System.arraycopy(iv, 0, iv2, 0, iv2.length);
        }
        int spiltLen = cipherType == CipherType.AES_CTR ? MIN_AES_LEN : MIN_DES_LEN;
        int packages = (int) Math.ceil(data.length / (spiltLen * 1.0));
        List<byte[]> packagesData = spiltDataInCTRMode(data, packages, (cipherType == CipherType.AES_CTR ? AES_CTR : DES_CTR));
        byte[] ksn = null;
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            for (byte[] dataSpilt : packagesData) {
                CipherOutput cipherOutput = crypto.decrypt(key, cipherType, paddingMode, iv2, dataSpilt);
                ksn = cipherOutput.getKsn();
                iv2 = incrementCounter(iv2, 0, iv2.length, (cipherType == CipherType.AES_CTR ? AES_CTR : DES_CTR));
                stream.write(cipherOutput.getData());
            }
            return new CipherOutput(stream.toByteArray(), ksn);
        } catch (IOException e) {
            throw new NSDKException(e);
        }
    }

    private List<byte[]> spiltDataInCTRMode(byte[] data, int packages, int mode) {
        List<byte[]> dataPackages = new ArrayList<>();
        int spiltUnit = MIN_DES_LEN;
        if (mode == AES_CTR) {
            spiltUnit = MIN_AES_LEN;
        }
        for (int i = 0; i < packages - 1; i++) {
            byte[] temp = new byte[spiltUnit];
            System.arraycopy(data, i * spiltUnit, temp, 0, spiltUnit);
            dataPackages.add(temp);
        }
        byte[] lastPartData = new byte[data.length - (packages - 1) * spiltUnit];
        System.arraycopy(data, (packages - 1) * spiltUnit, lastPartData, 0, lastPartData.length);
        dataPackages.add(lastPartData);
        return dataPackages;
    }

    private List<byte[]> spiltData(byte[] data, int packages) {
        List<byte[]> dataPackages = new ArrayList<>();
        for (int i = 0; i < packages - 1; i++) {
            byte[] temp = new byte[SINGLE_PACKAGE_LEN];
            System.arraycopy(data, i * SINGLE_PACKAGE_LEN, temp, 0, SINGLE_PACKAGE_LEN);
            dataPackages.add(temp);
        }
        byte[] lastPartData = new byte[data.length - (packages - 1) * SINGLE_PACKAGE_LEN];
        System.arraycopy(data, (packages - 1) * SINGLE_PACKAGE_LEN, lastPartData, 0, lastPartData.length);
        dataPackages.add(lastPartData);
        return dataPackages;
    }

    private static byte[] incrementCounter(byte[] iv, int offset, int length, int mode) {
        byte[] incrementIv = new byte[iv.length];
        int nonceLength = 8;
        if (mode == DES_CTR) {
            nonceLength = 4;
        }
        System.arraycopy(iv, 0, incrementIv, 0, nonceLength);
        long counter = 0;
        for (int i = 0; i < length; i++) {
            counter <<= 8;
            counter |= iv[offset + i] & 0xFF;
        }

        counter += 1;

        for (int i = 0; i < length; i++) {
            iv[offset + length - 1 - i] = (byte) (counter & 0xFF);
            counter >>= 8;
        }

        System.arraycopy(iv, (mode == AES_CTR ? 8 : 4), incrementIv, nonceLength, (mode == AES_CTR ? 8 : 4));



        return incrementIv;
    }
}
