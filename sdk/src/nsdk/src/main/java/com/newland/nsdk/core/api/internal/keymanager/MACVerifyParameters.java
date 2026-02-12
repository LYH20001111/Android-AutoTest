package com.newland.nsdk.core.api.internal.keymanager;

import com.newland.nsdk.core.api.common.crypto.MACType;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;

import java.util.Map;

/**
 * The parameters for MAC verification mode in {@link KeyManager#injectPubKey(Map, VerifyParameters, byte[], byte[])}.
 */
public class MACVerifyParameters extends VerifyParameters{
    private MACType macType;
    private SymmetricKey macKeyInfo;
    private byte[] macData;
    private byte[] iv;


    /**
     * Gets the MAC type of MAC calculation.
     * @return The MAC type of MAC calculation.
     */
    public MACType getMacType() {
        return macType;
    }

    /**
     * Sets the MAC type of MAC calculation.
     * @param macType The MAC type of MAC calculation.
     */
    public void setMacType(MACType macType) {
        this.macType = macType;
    }

    /**
     * Gets the symmetrical MAC key information.
     * @return The symmetrical MAC key information.
     */
    public SymmetricKey getMacKeyInfo() {
        return macKeyInfo;
    }

    /**
     * Sets the symmetrical MAC key information.
     * @param macKeyInfo The symmetrical MAC key information.
     * <ul>
     *     <li><b>[Required]</b> keyID: The symmetrical MAC key id.</li>
     *     <li><b>[Required]</b> keyType: The symmetrical key type.</li>
     *     <li><b>[Required]</b> keyUsage: The symmetrical key usage.</li>
     * </ul>
     */
    public void setMacKeyInfo(SymmetricKey macKeyInfo) {
        this.macKeyInfo = macKeyInfo;
    }

    /**
     * Gets the calculation MAC data.
     * @return The calculation MAC data.
     */
    public byte[] getMacData() {
        return macData;
    }

    /**
     * Sets the calculation MAC data.
     * @param macData The calculation MAC data.
     */
    public void setMacData(byte[] macData) {
        this.macData = macData;
    }

    /**
     * Gets the initial vector.
     * @return The initial vector.
     */
    public byte[] getIv() {
        return iv;
    }

    /**
     * Sets the initial vector.
     * @param iv The initial vector.
     */
    public void setIv(byte[] iv) {
        this.iv = iv;
    }
}
