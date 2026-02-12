package com.newland.nsdk.core.external;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.keymanager.ECCType;
import com.newland.nsdk.core.api.common.keymanager.InstalledKeyInfo;
import com.newland.nsdk.core.api.common.keymanager.KDFInfo;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.external.ecdhe.ExtECDHE;
import com.newland.nsdk.core.api.external.futurex.ExtFutureX;
import com.newland.nsdk.core.external.command.common.ExternalCommonModule;
import com.newland.nsdk.core.external.command.common.FileTransferUtil;
import com.newland.nsdk.core.external.command.ecdhe.ExternalECDHEModule;
import com.newland.nsdk.core.external.command.futurex.FutureXCommandType;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

public class ExtECDHEImpl implements ExtECDHE {
    private ExternalECDHEModule ecdheModule;

    public ExtECDHEImpl(){
        ecdheModule = new ExternalECDHEModule();
    }

    @Override
    public void init() throws NSDKException {
        this.ecdheModule.init();
    }

    @Override
    public void release() throws NSDKException {
        this.ecdheModule.release();
    }

    @Override
    public byte[] generateKeyPair(ECCType curveType) throws NSDKException {
        return this.ecdheModule.generateKeyPair(curveType);
    }

    @Override
    public void generateSessionKey(SymmetricKey sessionKey, KDFInfo kdfInfo, byte[] publicKey) throws NSDKException {
        this.ecdheModule.generateSessionKey(sessionKey, kdfInfo, publicKey);
    }
}
