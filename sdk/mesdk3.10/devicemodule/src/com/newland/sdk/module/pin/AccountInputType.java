package com.newland.sdk.module.pin;

/**
 * In password entry, the account number corresponding to the password is required as entry condition.
 * <p>
 * If the type is{@link #USE_ACCOUNT}, it represents that, in password entry, the corresponding account number is the original account number.
 * <p>
 * If the type is {@link #USE_ACCT_HASH}, it represents that, in password entry, the corresponding account number is the returned acctHashId before the card swiping.
 * <p>
 *
 *
 * @since v1.0
 */
public enum AccountInputType {

    /**
     * It is account number that is entered
     */
    USE_ACCOUNT,
    /**
     * It is the hash value of account number that is entered
     */
    USE_ACCT_HASH,
    /**
     * No-master account number mode
     */
    UNUSE_ACCOUNT

}
