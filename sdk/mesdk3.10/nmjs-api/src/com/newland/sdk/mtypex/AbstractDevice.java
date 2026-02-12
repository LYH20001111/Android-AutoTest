package com.newland.sdk.mtypex;

import java.util.concurrent.TimeUnit;

import com.newland.sdk.mtype.Device;
import com.newland.sdk.mtype.DeviceOutofLineException;
import com.newland.sdk.mtype.DeviceTransationManager;
import com.newland.sdk.mtype.OpenTrasactionException;
import com.newland.sdk.mtype.TransactionStatus;
import com.newland.sdk.mtype.event.AbstractProcessDeviceEvent;
import com.newland.sdk.mtype.event.DeviceEventListener;
import com.newland.sdk.mtypex.cmd.DeviceCommand;
import com.newland.sdk.mtypex.cmd.DeviceResponse;
import com.newland.sdk.mtypex.conn.DeviceExecutor;
import com.newland.sdk.mtypex.conn.TransationabledDeviceExecutor;

public abstract class AbstractDevice extends AbstractCommandInvoker implements Device {

    protected DeviceExecutor deviceExecutor;

    private Object bundle;

    public AbstractDevice(DeviceExecutor deviceExecutor) {
        super();
        this.deviceExecutor = deviceExecutor;
        try {
            this.deviceExecutor.init(this);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DeviceOutofLineException("init device executor failed!", e);
        }
    }

    @Override
    public boolean isAlive() {
        return deviceExecutor.isAlive();
    }

    @Override
    public void destroy() {
        deviceExecutor.destroy();
    }

    /**
     * >
     * <p>
     * Chang the access rights to the lineal device to access to the DeviceExecutor
     * Allowed to inherit the device object can access to the corresponding equipment actuators
     *
     * @return
     * @since 1.1.2
     */
    protected DeviceExecutor getDeviceExecutor() {
        return deviceExecutor;
    }

    public DeviceResponse invoke(DeviceCommand deviceCmd, long timeout, TimeUnit timeunit) {
        return super.invoke(this, deviceCmd, timeout, timeunit);
    }

    public DeviceResponse invoke(DeviceCommand deviceCmd) {
        return super.invoke(this, deviceCmd);
    }

    public <T extends AbstractProcessDeviceEvent> void invoke(DeviceCommand deviceCmd, final DeviceEventListener<T> listener, final EventMaker<T> eventMaker) {
        super.invoke(this, deviceCmd, listener, eventMaker);
    }

    public <T extends AbstractProcessDeviceEvent> void invoke(DeviceCommand deviceCmd, long timeout, TimeUnit timeunit, final DeviceEventListener<T> listener, final EventMaker<T> eventMaker) {
        super.invoke(this, deviceCmd, timeout, timeunit, listener, eventMaker);
    }

    protected DeviceResponse dealDevResp(DeviceResponse response) {
        return super.dealDevResp(this, response);
    }

    public Object getBundle() {
        return bundle;
    }

    public void setBundle(Object obj) {
        this.bundle = obj;
    }

    public DeviceTransationManager getDeviceTransationManager() {
        if (deviceExecutor instanceof TransationabledDeviceExecutor) {
            DeviceTransationManager manager = new DeviceTransationManager() {
                @Override
                public TransactionStatus getTransactionStatus() {
                    return ((TransationabledDeviceExecutor) deviceExecutor).getTransactionStatus();
                }

                @Override
                public void endTransaction() throws OpenTrasactionException {
                    ((TransationabledDeviceExecutor) deviceExecutor).endTransaction();
                }

                @Override
                public void beginTransaction(long timeout, TimeUnit timeUnit)
                        throws OpenTrasactionException {
                    ((TransationabledDeviceExecutor) deviceExecutor).beginTransaction(timeout, timeUnit);
                }

                @Override
                public boolean isBusy() {
                    return ((TransationabledDeviceExecutor) deviceExecutor).isBusy();
                }
            };
            return manager;
        }
        return null;
    }

}
