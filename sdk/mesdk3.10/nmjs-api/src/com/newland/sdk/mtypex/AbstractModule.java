package com.newland.sdk.mtypex;

import com.newland.sdk.mtype.Device;
import com.newland.sdk.mtype.Module;
import com.newland.sdk.mtype.event.AbstractProcessDeviceEvent;
import com.newland.sdk.mtype.event.DeviceEventListener;
import com.newland.sdk.mtypex.cmd.DeviceCommand;
import com.newland.sdk.mtypex.cmd.DeviceResponse;

import java.util.concurrent.TimeUnit;

public abstract class AbstractModule extends AbstractCommandInvoker implements Module {

    private AbstractDevice owner;

    public AbstractModule(AbstractDevice owner) {
        this.owner = owner;
    }

    @Override
    public Device getOwner() {
        return owner;
    }


    @Override
    public String getDescription() {
        if (isStandardModule()) {
            return getStandardModuleType().toString();
        } else {
            return getExModuleType();
        }
    }

    protected DeviceResponse dealDevResp(DeviceResponse response) {
        return super.dealDevResp(owner, response);
    }

    protected DeviceResponse invoke(DeviceCommand deviceCmd, long timeout, TimeUnit timeunit) {
        return super.invoke(owner, deviceCmd, timeout, timeunit);
    }

    protected DeviceResponse invoke(DeviceCommand deviceCmd) {
        return super.invoke(owner, deviceCmd);
    }

//    protected <T extends AbstractProcessDeviceEvent> void invoke(DeviceCommand deviceCmd, final DeviceEventListener<T> listener, final EventMaker<T> eventMaker) {
//        super.invoke(owner, deviceCmd, listener, eventMaker);
//    }

    protected <T extends AbstractProcessDeviceEvent> void invoke(DeviceCommand deviceCmd, long timeout, TimeUnit timeunit, final DeviceEventListener<T> listener, final EventMaker<T> eventMaker) {
        super.invoke(owner, deviceCmd, timeout, timeunit, listener, eventMaker);
    }

}
