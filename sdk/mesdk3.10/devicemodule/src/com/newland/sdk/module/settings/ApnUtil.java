package com.newland.sdk.module.settings;

import android.newland.telephony.ApnEntity;

import java.util.List;

/**
 * <p>APN Util</p>
 *
 * @author linsi
 */
public interface ApnUtil {
    /**
     * <p>Get the current default APN of the system.</p>
     *
     * @return
     */
    ApnEntity getCurrentApn();

    /**
     * <p>Get all ANP of system.</p>
     *
     * @return
     */
    List<ApnEntity> getSystemApns();
    /**
     * <p>Remove the ANP of the specified id.</p>
     *
     * @param id
     * @return
     */
    boolean removeApn(int id);

    /**
     * <p>Add a new APN.</p>
     *
     * @param apnEntity
     * @return Represent the id value inserted into the database. -1 means fail.
     */
    int addNewApn(ApnEntity apnEntity);

    /**
     * <p>Set the default APN with id which is _id value in the database.</p>
     *
     * @param id
     * @return Number of rows to update. -1 means fail.
     */
    int setDefaultApn(int id);

    /**
     * <p>Gets the apn list for the current SIM card.</p>
     *
     * @return
     */
    List<ApnEntity> getCardApns();

}
