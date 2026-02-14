package com.newland.nsdk.core.api.common.card.contactless;

public enum CardSearchMode {
    //Default card detection mode.
    DEFAULT,
    //Card detection with card events mechanism which can be used to save power consume.
    CARD_EVENT,
    //Contactless card detection with lower power card detection(LPCD) mode, which may decrease the detection distance to 0~4cm.
    RF_LPCD,
}
