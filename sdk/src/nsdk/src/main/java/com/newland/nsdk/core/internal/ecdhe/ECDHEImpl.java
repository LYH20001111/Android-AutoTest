package com.newland.nsdk.core.internal.ecdhe;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.keymanager.ECCType;
import com.newland.nsdk.core.api.common.keymanager.KDFInfo;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.internal.ecdhe.ECDHE;
import com.newland.nsdk.core.api.internal.exception.NSDKNDKException;
import com.newland.nsdk.core.internal.jni.NSDKJni;
import com.newland.nsdk.core.internal.keymanager.ST_SEC_KEYIN_DATA;

import java.util.Arrays;
import java.util.Locale;

public class ECDHEImpl implements ECDHE {
    private static final String TAG = "ECDHEImpl";
    private long handle = -1;
    @Override
    public void init() throws NSDKException {
        long[] handle = new long[1];
        int ret = NSDKJni.getInstance().NAPI_SecECDHEInit(handle);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to init ECDHE, result code = %d", ret));
        }
        this.handle = handle[0];
        LogUtils.d(TAG, "ECDHE init handle: " + this.handle);
    }

    @Override
    public void release() throws NSDKException {
        if (this.handle == -1) {
            return;
        }
        int ret = NSDKJni.getInstance().NAPI_SecECDHERelease(this.handle);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to release ECDHE, result code = %d", ret));
        }
    }

    @Override
    public byte[] generateKeyPair(ECCType curveType) throws NSDKException {
        if (curveType == null) {
            throw new NSDKIllegalParameterException("Curve type shall not be null.");
        }

        if (this.handle == -1) {
            throw new NSDKException("Please init ECDHE first.");
        }

        int len = 5 * 1024;
        byte[] publicKey = new byte[len];
        int[] outDataLen = new int[1];

        int ret = NSDKJni.getInstance().NAPI_SecECDHEGenerateKeyPair(this.handle, curveType.ordinal(), publicKey, outDataLen);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to generate ECDHE public key, result code = %d.", ret));
        }

        if (outDataLen[0] > 0 && outDataLen[0] <= len) {
            byte[] result = Arrays.copyOf(publicKey, outDataLen[0]);
            LogUtils.d(TAG, String.format(Locale.US, "ECDHE public key len: %d, key data: %s", outDataLen[0], ISOUtils.hexString(result)));
            return result;
        }

        return null;
    }

    @Override
    public void generateSessionKey(SymmetricKey sessionKey, KDFInfo kdfInfo, byte[] publicKey) throws NSDKException {
        if (sessionKey == null || kdfInfo == null || publicKey == null) {
            throw new NSDKIllegalParameterException("Session key, HKDF info and public key shall not be null.");
        }

        if (sessionKey.getKeyType() == null || sessionKey.getKeyUsage() == null) {
            throw new NSDKIllegalParameterException("Session key type and usage shall not be null.");
        }

        if (kdfInfo.getKDFType() == null || kdfInfo.getMessageDigestType() == null) {
            throw new NSDKIllegalParameterException("HKDF type and message digest type shall not be null.");
        }

        if (this.handle == -1) {
            throw new NSDKException("Please init ECDHE first.");
        }

        ST_SEC_KEYIN_DATA keyInData = new ST_SEC_KEYIN_DATA();
        keyInData.setKeyType(sessionKey.getKeyType().getCode());
        keyInData.setUcKeyIdx(sessionKey.getKeyID());
        keyInData.setKeyUsage(sessionKey.getKeyUsage().getCode());
        keyInData.setnKeyLen(sessionKey.getKeyLen());

        ST_SEC_ECDHE_KDF_INFO ecdheKdfInfo = new ST_SEC_ECDHE_KDF_INFO();
        ecdheKdfInfo.setKdfType(kdfInfo.getKDFType().ordinal());
        ecdheKdfInfo.setMdAlg(kdfInfo.getMessageDigestType().ordinal());
        if (kdfInfo.getSalt() != null && kdfInfo.getSalt().length > 0) {
            ecdheKdfInfo.setSaltLen(kdfInfo.getSalt().length);
            ecdheKdfInfo.setSalt(kdfInfo.getSalt());
        }
        if (kdfInfo.getInfo() != null && kdfInfo.getInfo().length > 0) {
            ecdheKdfInfo.setInfoLen(kdfInfo.getInfo().length);
            ecdheKdfInfo.setInfo(kdfInfo.getInfo());
        }

        int ret = NSDKJni.getInstance().NAPI_SecECDHEGenSK(this.handle, keyInData, ecdheKdfInfo, publicKey.length, publicKey);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to generate ECDHE session key, result code = %d.", ret));
        }
    }
}
