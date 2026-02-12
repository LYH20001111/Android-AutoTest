package com.newland.sdk.module.pin;

/**
 * Working key mode
 * @author ld
 *
 * @since 2.0.0
 */
public enum LoadWKMode {
	/**
	 * Encrypted text  
	 */
	ENCRYPT,
	/**
	 * plain text
	 */
	PLAIN,
	/**
	 * Key is generated under TR-31 rules.
	 * Not support
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
