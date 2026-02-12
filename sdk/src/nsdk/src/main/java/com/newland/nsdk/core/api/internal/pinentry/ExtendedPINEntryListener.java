package com.newland.nsdk.core.api.internal.pinentry;

public interface ExtendedPINEntryListener extends PINEntryListener{
    /**
     * Invoked when press enter with insufficient PIN.
     */
    void onPINLengthInsufficient();

    /**
     * Invoked when PIN is full, the following press is invalid.
     */
    void onPINLengthExceeded();
}
