package com.newland.nsdk.core.api.external.signature;

import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.exception.NSDKException;

/**
 * <b>[External Module]</b> Provides e-signature related functions.
 *
 * <p>How to get this module:</p>
 * <pre>
 *     ExtSignature extSignature = (ExtSignature)ExtNSDKModuleManagerImpl.getInstance().getModule(ModuleType.EXT_SIGNATURE);
 * </pre>
 */
public interface ExtESignature extends Module {
    /**
     * Start electronic signature process.
     * @param parameters  <b>[Required]</b> The electronic signature relative parameters.
     * @param timeout     <b>[Required]</b> Signature process timeout. Unit:seconds
     * @param listener    <b>[Required]</b> The listener to receive the signature process result.
     * @throws NSDKException
     */
    void start(ExtESignatureParameters parameters, int timeout, ExtESignatureListener listener) throws NSDKException;
}
