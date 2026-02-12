package com.newland.sdk.module.emv;

import com.newland.sdk.mtype.common.Const.EmvStandardReference;
import com.newland.sdk.me.module.emv.structure.AbstractEMVPackage;
import com.newland.sdk.me.module.emv.structure.EMVTagDefined;

/**
 * import the online transaction result data to continue the process.
 * <p>
 *
 *
 * @since ver3.10.01
 */
public class OnlineTransactionData extends AbstractEMVPackage {

    /**
     * @see EmvStandardReference#AUTHORISATION_RESPONSE_CODE
     */
    @EMVTagDefined(tag = EmvStandardReference.AUTHORISATION_RESPONSE_CODE)
    private String authorisationResponseCode;// 0x8a
    /**
     * @see EmvStandardReference#AUTHORISATION_CODE
     */
    @EMVTagDefined(tag = EmvStandardReference.AUTHORISATION_CODE)
    private String authorisationCode;// 0x89
    /**
     * @see EmvStandardReference#ISSUER_SCRIPT_TEMPLATE_1
     */
    @EMVTagDefined(tag = EmvStandardReference.ISSUER_SCRIPT_TEMPLATE_1)
    private byte[] issuerScriptTemplate1;// 0x71

    /**
     * @see EmvStandardReference#ISSUER_SCRIPT_TEMPLATE_2
     */
    @EMVTagDefined(tag = EmvStandardReference.ISSUER_SCRIPT_TEMPLATE_2)
    private byte[] issuerScriptTemplate2;// 0x72

    /**
     * @see EmvStandardReference#ISSUER_AUTHENTICATION_DATA
     */
    @EMVTagDefined(tag = EmvStandardReference.ISSUER_AUTHENTICATION_DATA)
    private byte[] issuerAuthenticationData;// 0x91

    private byte[] tlvData;

    public byte[] getTlvData() {
        return tlvData;
    }

    /**
     * the online transaction result data in the form of ber- TLV
     * @param tlvData
     */
    public void setTlvData(byte[] tlvData) {
        this.tlvData = tlvData;
    }

    /**
     * Transaction response code：from the 8583 specification field39
     *
     * @param authorisationResponseCode "00" is online sucessful,and the others is failed
     */
    public void setAuthorisationResponseCode(String authorisationResponseCode) {
        this.authorisationResponseCode = authorisationResponseCode;
    }

    /**
     * Script 1 of card issuing bank
     *
     * @param issuerScriptTemplate1
     */
    @Deprecated
    public void setIssuerScriptTemplate1(byte[] issuerScriptTemplate1) {
        this.issuerScriptTemplate1 = issuerScriptTemplate1;
    }

    /**
     * Script 2 of card issuing bank
     *
     * @param issuerScriptTemplate2
     */
    @Deprecated
    public void setIssuerScriptTemplate2(byte[] issuerScriptTemplate2) {
        this.issuerScriptTemplate2 = issuerScriptTemplate2;
    }

    /**
     * Card issuing bank authentication data
     *
     * @param issuerAuthenticationData
     */
    @Deprecated
    public void setIssuerAuthenticationData(byte[] issuerAuthenticationData) {
        this.issuerAuthenticationData = issuerAuthenticationData;
    }

    /**
     * Authorization code
     *
     * @param authorisationCode
     */
    public void setAuthorisationCode(String authorisationCode) {
        this.authorisationCode = authorisationCode;
    }

    /**
     * get AuthorisationResponseCode
     * @return
     */
    public String getAuthorisationResponseCode() {
        return authorisationResponseCode;
    }

    public String getAuthorisationCode() {
        return authorisationCode;
    }

    public byte[] getIssuerScriptTemplate1() {
        return issuerScriptTemplate1;
    }

    public byte[] getIssuerScriptTemplate2() {
        return issuerScriptTemplate2;
    }

    public byte[] getIssuerAuthenticationData() {
        return issuerAuthenticationData;
    }
}
