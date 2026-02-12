package com.newland.nsdk.core.api.internal.cashbox;

import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.exception.NSDKException;

/**
 * Cash box.
 *
 * <p>How to get this module:</p>
 * <pre>
 *     CashBox cashBox = (CashBox)NSDKModuleManagerImpl.getInstance().getModule(ModuleType.CASH_BOX);
 * </pre>
 */
public interface CashBox  extends Module {
    /**
     * Opens cash box with default voltage(12V) and delay time(500ms).
     *
     * @throws NSDKException
     */
    void open() throws NSDKException;

    /**
     * Opens cash box with specified voltage and default delay time(500ms).
     *
     * @param voltage <b>[Required]</b> Voltage.
     *                <ul>
     *                <li>0: 12V</li>
     *                <li>1: 24V</li>
     *                </ul>
     * @throws NSDKException
     */
    void open(int voltage) throws NSDKException;

    /**
     * Opens cash box with specified voltage and delay time.
     *
     * <p>Note: Every time this open method is called, it will set a delay time to enable cash box again.</p>
     * <ol>Example:
     * <li>`open(0, 100000)` is called, cash box is opened and it is allowed to be opened again after 10 seconds.</li>
     * <li>5 seconds later, `open(0, 1000)` is called, cash box will not be opened, but this calling of "open" method will make cash box enabled after 1 second.</li>
     * <li>1 second later, cash box is enabled by the second calling of "open" method.</li>
     * <li>1 second later, `open(0, 1000)` is called again, cash box will be opened this time.</li>
     * <li>1 second later, cash box is enabled by the third calling of "open" method.</li>
     * <li>2 seconds later, cash box is enabled by the first calling of "open" method.</li>
     * </ol>
     *
     * @param voltage <b>[Required]</b> Voltage.
     *                <ul>
     *                <li>0: 12V</li>
     *                <li>1: 24V</li>
     *                </ul>
     * @param time <b>[Required]</b> Delay time that allow cash box to be opened again. Unit:ms.
     * @throws NSDKException
     */
    void open(int voltage, long time) throws NSDKException;
}
