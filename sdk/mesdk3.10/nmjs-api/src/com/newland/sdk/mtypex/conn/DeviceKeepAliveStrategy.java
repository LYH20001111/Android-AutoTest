package com.newland.sdk.mtypex.conn;


/**
 * Device survival decision strategy<p>
 *
 *
 * @since ver3.10.01
 */
public interface DeviceKeepAliveStrategy {

    /**
     * Default execution timeout
     *
     * @return
     * @since ver3.10.01
     */
    public long getDefaultExecTimeout();

    /**
     * Default undo timeout
     *
     * @return
     * @since ver3.10.01
     */
    public long getDefaultResetTimeout();


    /**
     * Reset instruction, when the state of the device is unknown, the instruction is executed first to ensure that the equipment is in normal condition.
     * <p>
     * Any exception in this method will cause the connection to be interrupted.
     *
     * @param deviceConnection
     * @since ver3.10.01
     */
    public void doDefaultReset(DeviceConnection deviceConnection) throws Exception;

}
