package com.newland.nsdk.core.api.external.display;

public interface SelectionCallback {
    /**
     * Invoked when item selected.
     * @param code  The selected item code.
     */
    void onSelected(int code);

    /**
     * Invoked when error occurred.
     * @param code   The error code.
     * @param errorMessage  The error message.
     */
    void onError(int code, String errorMessage);

    /**
     * Invoked when pinpad cancelled.
     */
    void onCancel();

    /**
     * Invoked when selection procedure timeout.
     */
    void onTimeout();
}
