package com.newland.sdk.module.pin;

/**
 * @author youjf
 * @description the mode of loading master key or DUKPT key
 * @date 2019/7/30
 * @since V3.10.01
 */
public enum LoadKeyMode {
    /**
     * Use a default key as the encryption key.
     */
    DEFAULT_ENCRYPT,
    /**
     * Use a specific master key as the encryption key.
     */
    CUSTOM_ENCRYPT,
    /**
     *  Key is created from KeyData wich contains crear key data.<p>
     *
     *  Attention:the clear mode only for debug device.<p>
     */
    PLAIN,
    /**
     * Key is generated under TR-31 rules.
     */
    TR31,
    /**
     * Key is generated under GISKE rules.
     * Not support
     */
    GISKE,
    /**
     * A random key is generated and stored in the specified index.
     * Not support
     */
    RANDOM,
    /**
     * Session key is generated randomly, then encrypted under the corresponding master key.
     * Not support
     */
    RANDOM_OUT,
}
