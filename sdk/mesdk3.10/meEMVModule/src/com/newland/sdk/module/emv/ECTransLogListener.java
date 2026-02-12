package com.newland.sdk.module.emv;

import java.util.List;

public interface ECTransLogListener {

    /**
     * <p>It will be triggered when the transaction requires the app to response an application selection.</p>
     *
     * @param aidEntityList A collection of available applications
     * @return AID index{@link AIDEntity#getIndex()}
     */
    public int onRequestSelectApplication(List<AIDEntity> aidEntityList);

    /**
     * <p>It will be triggered when the transaction finish.</p>
     *
     * @param transLogs A collection of EC transaction logs.{@link ECTransLog}
     */
    public void onResult(List<ECTransLog> transLogs);
}
