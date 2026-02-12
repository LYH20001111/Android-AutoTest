package com.newland.nsdk.core.api.internal.printer;

/**
 * Listens to printer status.
 */
public interface PrintingResultListener {
    /**
     * Invoked when printer status changed.
     *
     * <ul>
     *     <li>OK = 0</li>
     *     <li>ERR = -1</li>
     *     <li>ERR_PARAM = -6</li>
     *     <li>ERR_BUSY = 8</li>
     *     <li>ERR_NOPAPER = 2</li>
     *     <li>ERR_OVERHEAT = 4</li>
     *     <li>ERR_VOLERR = 112</li>
     *     <li>ERR_CUTERR = 512</li>
     *     <li>ERR_DESTROYED = 1024</li>
     *     <li>ERR_PPSERR = 2048</li>
     * </ul>
     *
     * @param result Printing result.
     */
    void onEventRaised(int result);
}
