package com.newland.sdk.me.module.cardreader;

import com.newland.sdk.mtype.common.EventConst;
import com.newland.sdk.mtype.event.AbstractProcessDeviceEvent;

/**
 * Card reader response event<p>
 *
 *
 * @since ver3.10.01
 */
public class OpenCardReaderEvent extends AbstractProcessDeviceEvent {

    private OpenCardReaderResult openCardReaderResult;

    /**
     * Create an event that a card reader response is completed and the custom cancels the transaction<p>
     */
    public OpenCardReaderEvent() {
        super(EventConst.EVENT_OPEN_CARDREADER_FINISH, ProcessState.USER_CANCELED, null);
    }

    /**
     * Create an event that a card reader response is completed and the transaction processing is构failed
     *
     * @param e Exception
     */
    public OpenCardReaderEvent(Throwable e) {
        super(EventConst.EVENT_OPEN_CARDREADER_FINISH, ProcessState.FAILED, e);
    }

    /**
     * Create an event that a card reader response is completed and the transaction processing is successful.<p>
     *
     * @param openCardReaderResult Card reader return result
     */
    public OpenCardReaderEvent(OpenCardReaderResult openCardReaderResult) {
        super(EventConst.EVENT_OPEN_CARDREADER_FINISH, ProcessState.SUCCESS, null);
        this.openCardReaderResult = openCardReaderResult;
    }

    /**
     * Create a card reader response event<p>
     *
     * @param event Event parameters {@link EventConst}
     * @param state Transaction state
     * @param e     Exception
     */
    public OpenCardReaderEvent(String event, ProcessState state, Throwable e) {
        super(event, state, null);
    }

    /**
     * Get the card reader return result<p>
     *
     * @return
     */
    public OpenCardReaderResult getOpenCardReaderResult() {
        return this.openCardReaderResult;
    }

}
