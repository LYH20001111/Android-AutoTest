package com.newland.nsdk.core.external;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.external.signature.ExtESignatureListener;
import com.newland.nsdk.core.api.external.signature.ExtESignatureParameters;
import com.newland.nsdk.core.api.external.signature.ExtESignature;
import com.newland.nsdk.core.external.command.signature.ExternalSignatureModule;

public class ExtESignatureImpl implements ExtESignature {
    private ExternalSignatureModule externalSignatureModule;
    private static volatile ExtESignatureImpl instance;

    public static ExtESignatureImpl getInstance() {
        if (instance == null) {
            synchronized (ExtESignatureImpl.class) {
                if (instance == null) {
                    instance = new ExtESignatureImpl();
                }
            }
        }
        return instance;
    }

    private ExtESignatureImpl() {
        externalSignatureModule = new ExternalSignatureModule();
    }
    @Override
    public void start(ExtESignatureParameters parameters, int timeout, ExtESignatureListener listener) throws NSDKException {
        if (parameters == null) {
            throw new NSDKIllegalParameterException("ESignature parameters shall not be null");
        }
        if (timeout <= 0) {
            throw new NSDKIllegalParameterException("ESignature timeout shall be >0.");
        }
        if (listener == null) {
            throw new NSDKIllegalParameterException("ESignature listener shall not be null.");
        }

        externalSignatureModule.startSignature(parameters, timeout, listener);
    }
}
