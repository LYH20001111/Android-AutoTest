package com.newland.nsdk.externaldevice.communication;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.message.ExternalMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessageType;

import org.junit.Test;

public class ExternalCommunicationManagerTest {

    @Test
    public void sendCancel() {
        new Thread() {
            @Override
            public void run() {
                try {
                    ExternalMessage requestMessage = new ExternalMessage();
                    requestMessage.setMessageType(ExternalMessageType.SET_PIN_LINE_REQUEST);
                    ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.SET_PIN_LINE_RESPONSE, null);
                } catch (NSDKException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread() {
            @Override
            public void run() {
                try {
                    ExternalCommunicationManager.getInstance().send(null);
                } catch (NSDKException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                try {
                    ExternalCommunicationManager.getInstance().sendInterrupt(null);
                } catch (NSDKException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}