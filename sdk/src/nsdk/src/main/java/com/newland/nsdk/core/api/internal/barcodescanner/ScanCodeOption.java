package com.newland.nsdk.core.api.internal.barcodescanner;

/**
 * This is a class for setting barcode performance.
 * <p>The following is the supported codeId, key and value.</p>
 * <ul>
 *     <li>codeId:"CODE128", key:"Enable", value:"0" means disabled, "1" means enabled.</li>
 *     <li>codeId:"UCCEAN128", key:"Enable", value:"0" means disabled, "1" means enabled.</li>
 *     <li>codeId:"EAN8", key:"Enable", value:"0" means disabled, "1" means enabled.</li>
 *     <li>codeId:"EAN13", key:"Enable", value:"0" means disabled, "1" means enabled.</li>
 *     <li>codeId:"ISSN", key:"Enable", value:"0" means disabled, "1" means enabled.</li>
 *     <li>codeId:"ISBN", key:"Enable", value:"0" means disabled, "1" means enabled.</li>
 *     <li>codeId:"UPCE", key:"Enable", value:"0" means disabled, "1" means enabled.</li>
 *     <li>codeId:"UPCA", key:"Enable", value:"0" means disabled, "1" means enabled.</li>
 *     <li>codeId:"CODEBAR", key:"Enable", value:"0" means disabled, "1" means enabled.</li>
 *     <li>codeId:"CODEBAR", key:"StartStopMode", value:"0" means "ABCD", "1" means "TNXE", "2" means "abcd", "3" means "tnxe".</li>
 *     <li>codeId:"CODEBAR", key:"TrsmtStasrtStop", value:"0" means disabled transmit starts and stops bit, "1" means enabled transmit starts and stops bit.</li>
 *     <li>codeId:"CODE39", key:"Enable", value:"0" means disabled, "1" means enabled.</li>
 *     <li>codeId:"CODE93", key:"Enable", value:"0" means disabled, "1" means enabled.</li>
 *     <li>codeId:"DM", key:"Enable", value:"0" means disabled, "1" means enabled.</li>
 *     <li>codeId:"PDF417", key:"Enable", value:"0" means disabled, "1" means enabled.</li>
 *     <li>codeId:"QR", key:"Enable", value:"0" means disabled, "1" means enabled.</li>
 *     <li>codeId:"QR", key:"CodeNum", value:"1" means it can only identify one QR code with several codes, "2" means it can identify two QR codes with several codes.</li>
 *     <li>codeId:"QR", key:"NumFixed", value:"0" means not fixed the code number, "1" means fixed the code number.</li>
 *     <li>codeId:"UPC/EAN", key:"Add-On", value:"Enable" means to read the additional code information, "Disable" means not to read the additional code information.</li>
 * </ul>
 */
public class ScanCodeOption {
    private String codeId;
    private String key;
    private String value;

    /**
     * Gets the id of code to be set.
     * @return The id of code to be set.
     */
    public String getCodeId() {
        return codeId;
    }

    /**
     * Sets the id of code to be set.
     * @param codeId the id of code to be set.
     */
    public void setCodeId(String codeId) {
        this.codeId = codeId;
    }

    /**
     * Gets the attribute key of the code.
     * @return The attribute key of the code.
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the attribute key of the code.
     * @param key the attribute key of the code.
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets the attribute value of the code.
     * @return the attribute value of the code.
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the attribute value of the code.
     * @param value the attribute value of the code.
     */
    public void setValue(String value) {
        this.value = value;
    }
}
