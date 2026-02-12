package com.newland.nsdk.core.api.external.pinentry;

import com.newland.nsdk.core.api.common.keymanager.CipherMode;

/**
 * Parameters for online PIN entry.
 */
public class ExtOnlinePINParameters extends ExtPINEntryParameters{
    private CipherMode extendedKeyMode;
    private byte[] extendedPINKeyData;
    private int extendedPINKeyLen;

    /**
     * <b>[Not yet supported]</b> Gets extended key mode of the key which is used to encrypt PIN block.
     *
     * @return Extended key mode of the key which is used to encrypt PIN block. The following modes are allowed:
     * <ul>
     *     <li>{@link CipherMode#CBC}</li>
     *     <li>{@link CipherMode#ECB}</li>
     * </ul>
     */
    public CipherMode getExtendedKeyMode() {
        return extendedKeyMode;
    }

    /**
     * <b>[Not yet supported]</b> Sets extended key mode of the key which is used to encrypt PIN block.
     *
     * @param extendedKeyMode Extended key mode of the key which is used to encrypt PIN block. The following modes are allowed:
     *                        <ul>
     *                            <li>{@link CipherMode#CBC}</li>
     *                            <li>{@link CipherMode#ECB}</li>
     *                        </ul>
     */
    public void setExtendedKeyMode(CipherMode extendedKeyMode) {
        this.extendedKeyMode = extendedKeyMode;
    }

    /**
     * <b>[Not yet supported]</b> Gets extended key which is used to encrypt PIN block.
     *
     * @return Extended key which is used to encrypt PIN block.
     */
    public byte[] getExtendedPINKeyData() {
        return extendedPINKeyData;
    }

    /**
     * <b>[Not yet supported]</b> Sets extended key which is used to encrypt PIN block.
     *
     * @param extendedPINKeyData Extended key which is used to encrypt PIN block.
     */
    public void setExtendedPINKeyData(byte[] extendedPINKeyData) {
        this.extendedPINKeyData = extendedPINKeyData;
    }

    /**
     * <b>[Not yet supported]</b> Gets the length of extended key.
     *
     * @return Extended key length.
     */
    public int getExtendedPINKeyLen() {
        return extendedPINKeyLen;
    }

    /**
     * <b>[Not yet supported]</b> Sets extended key length.
     *
     * @param extendedPINKeyLen Extended key length.
     */
    public void setExtendedPINKeyLen(int extendedPINKeyLen) {
        this.extendedPINKeyLen = extendedPINKeyLen;
    }
}
