package com.newland.sdk.mtype.common;

/**
 * @description
 * @author youjf
 * @date 2019/7/30
 * @since V3.10.01
 */
public class ErrorCode {
    /**
     * param error,
     */
    public static final int PARAM_ERROR = -1;
    /**
     * open card reader failed
     */
    public static final int OPEN_CARDER_ERROR = -2;
    /**
     * open card reader failed,respond data is null
     */
    public static final int OPEN_CARDER_NULL = -3;
    /**
     * load key failed,kcv error
     */
    public static final int LOAD_KEY_KCV_ERROR = 41;
    /**
     * load key failed,invalid index
     */
    public static final int LOAD_KEY_INVALID_INDEX = 43;
    /**
     * load key failed,the key data length error
     */
    public static final int LOAD_KEY_DATA_LEN_ERROR = 45;
    /**
     * pin input exception
     */
    public static final int INPUT_PIN_ERROR = -1;
    /**
     * pin input failed
     */
    public static final int INPUT_PIN_FAILED = -2;

    /**
     * Unknown exception<p>
     */
    public static final int UNKNOWN = -100;

    /**
     *  Thread processing timeout<p>
     */
    public static final int PROCESS_TIMEOUT = -101;

    /**
     * Device disconnected<p>
     */
    public static final int DEVICE_DISCONNECTED = -102;

    /**
     * Device invoking failed <p>
     */
    public static final int DEVICE_INVOKE_FAILED = -103;

    /**
     * Not supported device connection <p>
     */
    public static final int NOT_SUPPORTED_CONNECTOR_TYPE = -104;

    /**
     * Instruction serialization/un-serialization failed<p>
     */
    public static final int SERIALIZE_OR_UNSERIALIZE_FAILED = -105;

    /**
     * EMV transaction process failed<p>
     */
    public static final int EMV_TRANSFER_FAILED = -107;
    /**
     *  Transaction initialization failed<p>
     */
    public static final int OPEN_TRANSATION_FAILED = -108;
    /**
     *  Transaction execution needed<p>
     */
    public static final int TRANSACTION_NEEDED = -109;
    /**
     * Device is busy<p>
     */
    public static final int DEVICE_BUSY = -110;
    /**
     *  Scanner initialization failed <p>
     */
    public static final int SCANNER_INIT_FAILED = -111;

    /**
     * Emv catalog creation failed<p>
     */
    public static final int CREATE_EMV_FOLDER_ERROR = -112;
    /**
     * The account number is null while start pininput<p>
     */
    public static final int START_PININPUT_ACCTSYMBOL_NULL = -113;
    /**
     * Not supported SCANNER type<p>
     */
    public static final int SCANNER_UNSUPPORT = -114;

    /**
     * Not supported SCANNER type<p>
     */
    public static final int DECODE = -115;
}
