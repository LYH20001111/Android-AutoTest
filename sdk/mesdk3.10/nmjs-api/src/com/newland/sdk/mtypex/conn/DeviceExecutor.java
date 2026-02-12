package com.newland.sdk.mtypex.conn;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import android.content.Context;

import com.newland.sdk.mtype.Device;
import com.newland.sdk.mtype.event.DeviceEventListener;
import com.newland.sdk.mtypex.cmd.DeviceCommand;
import com.newland.sdk.mtypex.cmd.DeviceResponse;

/**
 * Device command executor<p>
 *
 * @since ver3.10.01
 */
public interface DeviceExecutor {


    /**
     * Device execution initialization
     *
     * @param device
     * @throws Exception
     */
    public void init(Device device) throws Exception;

    /**
     * Non blocking way, and use the default instruction timeout time({@link ExecutorConst#EXEC_TIMEOUT}) to execute a device instruction.<p>
     *
     * @param deviceRequest Device instructions
     * @param listener      Execution event listener
     */
    public void invoke(DeviceCommand deviceRequest, DeviceEventListener<InvokeEvent> listener);

    /**
     * Non blocking way, and use the specified timeout time to execute a device instruction.<p>
     *
     * @param deviceRequest Device instructions
     * @param timeout       Timeout
     * @param timeunit      Timeout unit
     * @param listener      Execution event listener
     */
    public void invoke(DeviceCommand deviceRequest, long timeout, TimeUnit timeunit, DeviceEventListener<InvokeEvent> listener);

    /**
     * Blocking way, and use the specified timeout time to execute a device instruction.<p>
     *
     * @param deviceRequest Device instructions
     * @param timeout       Timeout
     * @param timeunit      Timeout unit
     * @return Response result If the client revokes actively at the SDK side, it will return null.
     */
    public DeviceResponse invoke(DeviceCommand deviceRequest, long timeout, TimeUnit timeunit);

    /**
     * Blocking way, and use the default instruction timeout time({@link ExecutorConst#EXEC_TIMEOUT}) to execute a device instruction.<p>
     *
     * @param deviceRequest Device instructions
     * @return Response result If the client revokes actively at the SDK side, it will return null.
     */
    public DeviceResponse invoke(DeviceCommand deviceRequest);

    /**
     * Skip the queue and execute directly instruction
     *
     * @param deviceCommand
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    public DeviceResponse directInvoke(DeviceCommand deviceCommand) throws IOException, InterruptedException;

    /**
     * Revoke all instructions that are in execution.<p>
     */
    public void cancelCurrentExecCmd();

    /**
     * Close the connector
     */
    public void destroy();

    /**
     * Check whether the connector is alive or not
     *
     * @return
     */
    public boolean isAlive();


    /**
     * Get the connector status<p>
     *
     * @return
     */
    public InnerDeviceConnState getDeviceConnectionState();

    /**
     * Get context
     *
     * @return
     */
    public Context getContext();


}
