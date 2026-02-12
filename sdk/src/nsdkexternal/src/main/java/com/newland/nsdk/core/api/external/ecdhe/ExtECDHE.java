package com.newland.nsdk.core.api.external.ecdhe;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.keymanager.ECCType;
import com.newland.nsdk.core.api.common.keymanager.KDFInfo;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;

public interface ExtECDHE {

    /**
     * Initializes ECDHE.
     * <p><b>Note: This method can be used only by system applications.</b></p>
     *
     * @throws NSDKException
     */
    void init() throws NSDKException;

    /**
     * Releases ECDHE.
     * <p><b>Note: This method can be used only by system applications.</b></p>
     *
     * @throws NSDKException
     */
    void release() throws NSDKException;

    /**
     * Generates ECDHE key pair and returns public key.
     * <p><b>Note: This method can be used only by system applications.</b></p>
     *
     * @param curveType Curve type, see {@link ECCType}.
     * @return Public key data.
     * @throws NSDKException
     */
    byte[] generateKeyPair(ECCType curveType) throws NSDKException;

    /**
     * Generates key with HKDF.
     * <p><b>Note: This method can be used only by system applications.</b></p>
     *
     * @param keyInfo   Target key to generate. The followings required:
     *                  <ul>
     *                  <li>Key ID</li>
     *                  <li>Key type</li>
     *                  <li>Key usage</li>
     *                  <li>Key len</li>
     *                  </ul>
     * @param KDFInfo  HKDF info, see {@link KDFInfo}.
     * @param publicKey Public key.
     * @throws NSDKException
     */
    void generateSessionKey(SymmetricKey keyInfo, KDFInfo KDFInfo, byte[] publicKey) throws NSDKException;
}
