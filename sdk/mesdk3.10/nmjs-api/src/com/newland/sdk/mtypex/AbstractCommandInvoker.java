package com.newland.sdk.mtypex;

import android.os.Handler;

import com.newland.sdk.mtype.DeviceException;
import com.newland.sdk.mtype.DeviceRTException;
import com.newland.sdk.mtype.common.ErrorCode;
import com.newland.sdk.mtype.event.AbstractProcessDeviceEvent;
import com.newland.sdk.mtype.event.DeviceEvent;
import com.newland.sdk.mtype.event.DeviceEventListener;
import com.newland.sdk.mtypex.cmd.CommandInvokeRslt;
import com.newland.sdk.mtypex.cmd.DeviceCommand;
import com.newland.sdk.mtypex.cmd.DeviceResponse;
import com.newland.sdk.mtypex.conn.InvokeEvent;

import java.util.concurrent.TimeUnit;

public abstract class AbstractCommandInvoker {

    protected DeviceResponse invoke(AbstractDevice owner, DeviceCommand deviceCmd, long timeout, TimeUnit timeunit) {
        DeviceResponse response;
        if (timeout <= 0) {
            response = owner.getDeviceExecutor().invoke(deviceCmd);
        } else {
            response = owner.getDeviceExecutor().invoke(deviceCmd, timeout, timeunit);
        }
        return dealDevResp(owner, response);
    }

    /**
     * According to the DeviceResponse ,make correlation processing.
     * <p>
     * <li>if Device response succeed,return DeviceResponse.</li>
     * <li>if Device response failed, throw related exception.</li>
     * <li>if request in process is undone,return null.</li>
     * </ol>
     *
     * @param response
     * @return
     * @since ver3.10.01
     */
    protected DeviceResponse dealDevResp(AbstractDevice owner, DeviceResponse response) {
        if (CommandInvokeRslt.USER_CANCELED == response.getProcessRslt()) //用户撤消时,返回为空
            return null;

        if (CommandInvokeRslt.FAILED == response.getProcessRslt()) {
            if (response.getException() != null) {
                if (response.getException() instanceof DeviceRTException) {
                    DeviceRTException e = (DeviceRTException) response.getException();
                    throw new DeviceRTException(e.getCode(), e.getMessage(), e);
                } else if (response.getException() instanceof DeviceException) {
                    DeviceException e = (DeviceException) response.getException();
                    throw new DeviceRTException(e.getCode(), e.getMessage(), e);
                } else {
                    Throwable e = response.getException();
                    throw new DeviceRTException(ErrorCode.UNKNOWN, e.getMessage(), e);
                }
            }
            throw new DeviceRTException(ErrorCode.UNKNOWN, "unknown exception!");
        }

        return response;
    }


    protected DeviceResponse invoke(AbstractDevice owner, DeviceCommand deviceCmd) {
        return invoke(owner, deviceCmd, -1, null);
    }

    protected <T extends AbstractProcessDeviceEvent> void invoke(AbstractDevice owner, DeviceCommand deviceCmd, final DeviceEventListener<T> listener, final EventMaker<T> eventMaker) {
        owner.getDeviceExecutor().invoke(deviceCmd, new DeviceEventListener<InvokeEvent>() {
            @Override
            public void onEvent(InvokeEvent event, Handler handler) {
                DeviceResponse deviceResponse = event.getDeviceResponse();
                T tgtEvent = eventMaker.makeEvent(deviceResponse);
                listener.onEvent(tgtEvent, handler);
            }

            @Override
            public Handler getUIHandler() {
                return listener.getUIHandler();
            }
        });
    }

    protected <T extends AbstractProcessDeviceEvent> void invoke(AbstractDevice owner, DeviceCommand deviceCmd, long timeout, TimeUnit timeunit, final DeviceEventListener<T> listener, final EventMaker<T> eventMaker) {
        owner.getDeviceExecutor().invoke(deviceCmd, timeout, timeunit, new DeviceEventListener<InvokeEvent>() {
            @Override
            public void onEvent(InvokeEvent event, Handler handler) {
                DeviceResponse deviceResponse = event.getDeviceResponse();
                T tgtEvent = eventMaker.makeEvent(deviceResponse);
                listener.onEvent(tgtEvent, handler);
            }

            @Override
            public Handler getUIHandler() {
                return listener.getUIHandler();
            }
        });
    }

    protected interface EventMaker<T extends DeviceEvent> {
        public T makeEvent(DeviceResponse deviceResponse);
    }

}
