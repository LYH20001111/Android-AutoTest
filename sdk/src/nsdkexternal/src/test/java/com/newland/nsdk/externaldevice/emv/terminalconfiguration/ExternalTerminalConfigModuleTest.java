package com.newland.nsdk.externaldevice.emv.terminalconfiguration;

import com.newland.nsdk.core.api.common.cardreader.CardType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.external.communication.ExternalCommunicator;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.communication.mock.MockExternalCommunicator;
import com.newland.nsdk.core.external.command.emv.terminalconfiguration.ExternalTerminalConfigModule;

import org.junit.Before;
import org.junit.Test;

public class ExternalTerminalConfigModuleTest {

    private ExternalTerminalConfigModule terminalConfigModule = new ExternalTerminalConfigModule();

    @Before
    public void init(){
        ExternalCommunicator communicator = new MockExternalCommunicator();
        ExternalCommunicationManager.getInstance().setCommunicator(communicator);
    }

    @Test
    public void updateConfiguration() {
        try {
            terminalConfigModule.updateTerminalConfiguration(CardType.CONTACT_CARD.ordinal(), new byte[]{0x01, 0x02, 0x03});
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getConfiguration() {
        try {
            byte[] result = terminalConfigModule.getTerminalConfiguration(CardType.CONTACT_CARD.ordinal());
            System.out.println(String.format("Terminal configuration: %s", ISOUtils.hexString(result)));
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }
}