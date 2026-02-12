package com.newland.sdk.module.emv;

import com.newland.sdk.mtype.common.Const.EmvSelfDefinedReference;
import com.newland.sdk.mtype.common.Const.EmvStandardReference;
import com.newland.sdk.me.module.emv.structure.AbstractEMVPackage;
import com.newland.sdk.me.module.emv.structure.EMVTagDefined;
import com.newland.sdk.mtype.util.Dump;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * CA public key object
 */
public class CAPK extends AbstractEMVPackage {

    /**
     * @see EmvStandardReference#AID_TERMINAL
     */
    @EMVTagDefined(tag = EmvStandardReference.AID_TERMINAL)
    protected byte[] rid;

    /**
     * @see EmvStandardReference#CA_PUBLIC_KEY_INDEX_TERMINAL
     */
    @EMVTagDefined(tag = EmvStandardReference.CA_PUBLIC_KEY_INDEX_TERMINAL)
    protected int index;

    /**
     * @see EmvSelfDefinedReference#CA_PK_EXPIRATION_DATE
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.CA_PK_EXPIRATION_DATE)
    private String expirationDate;

    /**
     * @see EmvSelfDefinedReference#CA_PK_HASH_ALGORITHM_INDICATOR
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.CA_PK_HASH_ALGORITHM_INDICATOR)
    private int hashAlgorithmIndicator;

    /**
     * @see EmvSelfDefinedReference#CA_PK_ALGORITHM_INDICATOR
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.CA_PK_ALGORITHM_INDICATOR)
    private int publicKeyAlgorithmIndicator;

    /**
     * Public key modulus
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.CAPK_MODULUS)
    private byte[] modulus;

    /**
     * Public key exponent
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.CAPK_EXPONENT)
    private byte[] exponent;

    /**
     * Public key fingerprint
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.CAPK_SHA1CHECKSUM)
    private byte[] sha1CheckSum;


    public CAPK() {
    }

    /**
     * Construct a CA public key <p>
     *
     * @param index                       CA public key index (0x9f22)
     * @param hashAlgorithmIndicator      CA public key hash algorithm indicator (0xDF06)
     * @param publicKeyAlgorithmIndicator CA public key algorithm indicator (0xDF07)
     * @param modulus                     CA public key modulus (0xDF02)
     * @param exponent                    CA public key exponent (0xDF04)
     * @param sha1CheckSum                CA public key check sum (0xDF03)
     * @param expirationDate              CA public key expiration date (Format yyyyMMdd)(0xDF05)
     */
    public CAPK(int index, int hashAlgorithmIndicator, int publicKeyAlgorithmIndicator, byte[] modulus, byte[] exponent, byte[] sha1CheckSum, String expirationDate) {
        this.index = index;
        this.expirationDate = expirationDate;
        this.hashAlgorithmIndicator = hashAlgorithmIndicator;
        this.publicKeyAlgorithmIndicator = publicKeyAlgorithmIndicator;
        this.modulus = modulus;
        this.exponent = exponent;
        this.sha1CheckSum = sha1CheckSum;
    }

    /**
     * Construct a CA public key <p>
     *
     * @param index                       CA public key index (0x9f22)
     * @param hashAlgorithmIndicator      CA public key hash algorithm indicator (0xDF06)
     * @param publicKeyAlgorithmIndicator CA public key algorithm indicator (0xDF07)
     * @param modulus                     CA public key modulus (0xDF02)
     * @param exponent                    CA public key exponent (0xDF04)
     * @param sha1CheckSum                CA public key check sum (0xDF03)
     * @param expirationDate              CA public key expiration date (Format yyyyMMdd)(0xDF05)
     */
    public CAPK(int index, int hashAlgorithmIndicator, int publicKeyAlgorithmIndicator, byte[] modulus, byte[] exponent, byte[] sha1CheckSum, Date expirationDate) {
        this.index = index;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
        this.expirationDate = sdf.format(expirationDate);
        this.hashAlgorithmIndicator = hashAlgorithmIndicator;
        this.publicKeyAlgorithmIndicator = publicKeyAlgorithmIndicator;
        this.modulus = modulus;
        this.exponent = exponent;
        this.sha1CheckSum = sha1CheckSum;
    }

    /**
     * Get the CA public key expiration date (0xDF05)
     *
     * @return
     */
    public String getExpirationDate() {
        return expirationDate;
    }

    /**
     * Get the CA public key hash algorithm indicator (0xDF06)
     *
     * @return int
     */
    public int getHashAlgorithmIndicator() {
        return hashAlgorithmIndicator;
    }

    /**
     * Get the CA public key algorithm indicator (0xDF07)
     *
     * @return int
     */
    public int getPublicKeyAlgorithmIndicator() {
        return publicKeyAlgorithmIndicator;
    }

    /**
     * Get the CA public key modulus (DF02)
     *
     * @return byte[]
     */
    public byte[] getModulus() {
        return modulus;
    }

    /**
     * Get the CA public key exponent (DF04)
     *
     * @return byte[]
     */
    public byte[] getExponent() {
        return exponent;
    }

    /**
     * Get the CA public key check sum (DF03)
     *
     * @return byte[]
     */
    public byte[] getSha1CheckSum() {
        return sha1CheckSum;
    }

    /**
     * Get the RID (9f06)
     *
     * @return byte[]
     */
    public byte[] getRid() {
        return rid;
    }

    /**
     * Set the RID (9f06)
     *
     * @param rid
     */
    public void setRid(byte[] rid) {
        this.rid = rid;
    }

    /**
     * Get the CA public key index（0x9f22）
     *
     * @return int Index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Set the CA public key index（0x9f22）
     *
     * @param index
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     *
     * @param expirationDate
     */
    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setHashAlgorithmIndicator(int hashAlgorithmIndicator) {
        this.hashAlgorithmIndicator = hashAlgorithmIndicator;
    }

    public void setPublicKeyAlgorithmIndicator(int publicKeyAlgorithmIndicator) {
        this.publicKeyAlgorithmIndicator = publicKeyAlgorithmIndicator;
    }

    public void setModulus(byte[] modulus) {
        this.modulus = modulus;
    }

    public void setExponent(byte[] exponent) {
        this.exponent = exponent;
    }

    public void setSha1CheckSum(byte[] sha1CheckSum) {
        this.sha1CheckSum = sha1CheckSum;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CA Public Key:[");

        sb.append("KeySize: " + modulus.length * 8 + "-bit,");
        sb.append("Exponent:" + Dump.getHexDump(exponent) + ",");
        sb.append("Modulus:" + Dump.getHexDump(modulus) + ",");
        sb.append("Checksum:" + Dump.getHexDump(sha1CheckSum) + "]");

        return sb.toString();
    }


}
