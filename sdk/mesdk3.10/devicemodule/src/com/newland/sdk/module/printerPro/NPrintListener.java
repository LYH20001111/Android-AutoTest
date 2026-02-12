package com.newland.sdk.module.printerPro;

/**
 * The print listener
 *
 * @author linsi
 */
public interface NPrintListener {
    /**
     * Print successfully.
     */
    void onSuccess();

    /**
     * @param error {@link NPrintErrorCode#BUSY}
     * @param msg   {@link NPrintErrorCode#toString()}
     */
    void onError(NPrintErrorCode error, String msg);
}
