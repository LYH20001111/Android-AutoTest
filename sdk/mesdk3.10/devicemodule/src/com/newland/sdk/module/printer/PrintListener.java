package com.newland.sdk.module.printer;

/**
 * The print listener
 *
 * @author linsi
 * @since V3.10.01
 */
public interface PrintListener {
    /**
     * Print successfully.
     */
    void onSuccess();

    /**
     * @param error {@link ErrorCode#BUSY}
     * @param msg   {@link ErrorCode#toString()}
     */
    void onError(ErrorCode error, String msg);
}
