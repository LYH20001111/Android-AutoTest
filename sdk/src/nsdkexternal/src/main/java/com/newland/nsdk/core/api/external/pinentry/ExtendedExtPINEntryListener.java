package com.newland.nsdk.core.api.external.pinentry;

public interface ExtendedExtPINEntryListener extends ExtPINEntryListener{
    void onOnlineSuccessExtended(int pinLen, byte[] pinBlock, byte[] dukptSN, byte[] tlvData);
}
