package com.newland.nsdk.core.api.external.keyboard;

import java.math.BigDecimal;

public interface AmountListener {
    /**
     * Invoked when input amount procedure finished.
     * @param amount  The amount input in the PIN pad.
     */
    void onResult(BigDecimal amount);
}
