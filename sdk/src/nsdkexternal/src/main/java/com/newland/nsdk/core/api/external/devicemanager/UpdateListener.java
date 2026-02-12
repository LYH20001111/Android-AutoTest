package com.newland.nsdk.core.api.external.devicemanager;

/**
 * A listener used to monitor the result of file transfer.
 */
public interface UpdateListener {
    /**
     * Invoked when error occurs.
     *
     * @param errorCode Error code.
     * @param message   Error message.
     */
    void onError(int errorCode, String message);

    /**
     * Invoked during file transferring.
     *
     * @param percent Indicates the progress of file transferring from 0 to 100 percent.
     */
    void onFileTransferProgress(int percent);

    /**
     * Invoked when update is complete.
     *
     * <p>Note: For application/firmware update, this means update is started, check device to see if update is completed successfully.</p>
     */
    void onComplete();
}
