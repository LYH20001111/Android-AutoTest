package com.newland.nsdk.core.api.common.crypto;

import com.newland.nsdk.core.api.common.keymanager.AsymmetricKey;

import java.util.List;

public class CSRParameters {
    String userName;
    AsymmetricKey asymmetricKey;
    MessageDigestType messageDigestType;
    byte keyUsage;
    byte certType;
    boolean isCA;
    List<byte[]> oidList;
    List<byte[]> valueList;

    /**
     * Gets the user name for CSR file.
     * @return The user name for CSR file.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the user name for CSR file.
     * @param userName The user name for CSR file.
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Gets the asymmetric key for CSR generation process.
     * @return The asymmetric key for CSR generation process. See {@link AsymmetricKey}.
     */
    public AsymmetricKey getAsymmetricKey() {
        return asymmetricKey;
    }

    /**
     * Sets the asymmetric key for CSR generation process.
     * @param asymmetricKey The asymmetric key for CSR generation process. See {@link AsymmetricKey}.
     */
    public void setAsymmetricKey(AsymmetricKey asymmetricKey) {
        this.asymmetricKey = asymmetricKey;
    }

    /**
     * Gets the message digest type for CSR generation process.
     * @return The message digest type for CSR generation process. See {@link MessageDigestType}.
     */
    public MessageDigestType getMessageDigestType() {
        return messageDigestType;
    }

    /**
     * Sets the message digest type for CSR generation process.
     * @param messageDigestType The message digest type for CSR generation process. See {@link MessageDigestType}.
     */
    public void setMessageDigestType(MessageDigestType messageDigestType) {
        this.messageDigestType = messageDigestType;
    }

    /**
     * Gets the cert type for CSR generation process.
     * @return The cert type for CSR generation process.
     */
    public byte getCertType() {
        return certType;
    }

    /**
     * Sets the cert type for CSR generation process.
     * <p>Note:Support cert type as following, and they can be combined to set together.</p>
     * <ul>
     *     <li>bit0:X509_NS_CERT_TYPE_OBJECT_SIGNING_CA(0x01)</li>
     *     <li>bit1:X509_NS_CERT_TYPE_EMAIL_CA(0x02)</li>
     *     <li>bit2:X509_NS_CERT_TYPE_SSL_CA(0x04)</li>
     *     <li>bit3:X509_NS_CERT_TYPE_RESERVED(0x08)</li>
     *     <li>bit4:X509_NS_CERT_TYPE_OBJECT_SIGNING(0x10)</li>
     *     <li>bit5:X509_NS_CERT_TYPE_EMAIL(0x20)</li>
     *     <li>bit6:X509_NS_CERT_TYPE_SSL_SERVER(0x40)</li>
     *     <li>bit7:X509_NS_CERT_TYPE_SSL_CLIENT(0x80)</li>
     * </ul>
     * @param certType The cert type for CSR generation process.
     */
    public void setCertType(byte certType) {
        this.certType = certType;
    }

    /**
     * Gets whether it is CA certification.
     * @return Whether it is CA certification.
     */
    public boolean isCA() {
        return isCA;
    }

    /**
     * Sets whether it is CA certification.
     * @param CA Whether it is CA certification.
     */
    public void setCA(boolean CA) {
        isCA = CA;
    }

    /**
     * Gets the extension oid value list.
     * @return The extension oid value list.
     */
    public List<byte[]> getOidList() {
        return oidList;
    }

    /**
     * Sets the extension oid value list.
     * <p>Note:This shall matches with the {@link CSRParameters#valueList}.</p>
     * @param oidList The extension oid value list.
     */
    public void setOidList(List<byte[]> oidList) {
        this.oidList = oidList;
    }

    /**
     * Gets the extension value list.
     * @return The extension value list.
     */
    public List<byte[]> getValueList() {
        return valueList;
    }

    /**
     * Sets the extension value list.
     * <p>Note:This shall matches with the {@link CSRParameters#oidList}.</p>
     * @param valueList The extension value list.
     */
    public void setValueList(List<byte[]> valueList) {
        this.valueList = valueList;
    }

    /**
     * Gets the key usage for CSR generation process.
     * @return The key usage for CSR generation process.
     */
    public byte getKeyUsage() {
        return keyUsage;
    }

     /**
     * Sets the key usage for CSR generation process.
     * <p>Note:Support key usage as following, and they can be combined to set together.</p>
     * <ul>
     *     <li>bit0:X509_KU_ENCIPHER_ONLY(0x01)</li>
     *     <li>bit1:X509_KU_CRL_SIGN(0x02)</li>
     *     <li>bit2:X509_KU_KEY_CERT_SIGN(0x04)</li>
     *     <li>bit3:X509_KU_KEY_AGREEMENT(0x08)</li>
     *     <li>bit4:X509_KU_DATA_ENCIPHERMENT(0x10)</li>
     *     <li>bit5:X509_KU_KEY_ENCIPHERMENT(0x20)</li>
     *     <li>bit6:X509_KU_NON_REPUDIATION(0x40)</li>
     *     <li>bit7:X509_KU_DIGITAL_SIGNATURE(0x80)</li>
     *     <li>X509_KU_DECIPHER_ONLY(0x8000)</li>
     * </ul>
     * @param keyUsage The key usage for CSR generation process.
     */
    public void setKeyUsage(byte keyUsage) {
        this.keyUsage = keyUsage;
    }
}
