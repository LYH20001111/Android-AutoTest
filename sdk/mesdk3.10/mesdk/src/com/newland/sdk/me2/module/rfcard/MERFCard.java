package com.newland.sdk.me2.module.rfcard;

import com.newland.sdk.me2.cmd.rfcard.CmdM1CardAuthenticate;
import com.newland.sdk.me2.cmd.rfcard.CmdM1CardDecrement;
import com.newland.sdk.me2.cmd.rfcard.CmdM1CardIncrement;
import com.newland.sdk.me2.cmd.rfcard.CmdM1CardReadData;
import com.newland.sdk.me2.cmd.rfcard.CmdM1CardReadData.CmdM1CardReadDataResponse;
import com.newland.sdk.me2.cmd.rfcard.CmdM1CardWriteData;
import com.newland.sdk.me2.cmd.rfcard.CmdRFCardInduct;
import com.newland.sdk.me2.cmd.rfcard.CmdRFCardPowerOff;
import com.newland.sdk.me2.cmd.rfcard.CmdRFCardPowerOn;
import com.newland.sdk.me2.cmd.rfcard.CmdRFCardPowerOn.CmdRFCardPowerOnResponse;
import com.newland.sdk.me2.cmd.rfcard.CmdRFCardTransmit;
import com.newland.sdk.module.rfcard.RFCardMode;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.common.ErrorMsg;
import com.newland.sdk.mtype.common.ErrorMsgHelper;
import com.newland.sdk.module.rfcard.RFCardModule;
import com.newland.sdk.module.rfcard.RFCardPowerOnExtParams;
import com.newland.sdk.module.rfcard.RFCardType;
import com.newland.sdk.module.rfcard.RFKeyMode;
import com.newland.sdk.module.rfcard.RFResult;
import com.newland.sdk.utils.ISOUtils;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.AbstractModule;

import java.util.concurrent.TimeUnit;

import static com.newland.sdk.me.cmd.CmdCode.RFID_POWERON;

public class MERFCard extends AbstractModule implements RFCardModule {

    private Object rfSync = new Object();

    public MERFCard(AbstractDevice owner) {
        super(owner);
    }

    @Override
    public boolean isStandardModule() {
        return true;
    }

    @Override
    public ModuleType getStandardModuleType() {
        return ModuleType.RFCARDREADER;
    }

    @Override
    public String getExModuleType() {
        return null;
    }

    private byte[] fetchApduData(byte[] dataLength) {
        byte[] header = ISOUtils.hex2byte("00C00000");
        byte[] sendData = new byte[header.length + dataLength.length];
        System.arraycopy(header, 0, sendData, 0, header.length);
        System.arraycopy(dataLength, 0, sendData, header.length, dataLength.length);
        CmdRFCardTransmit.CmdRFCardTransmitResponse response = (CmdRFCardTransmit.CmdRFCardTransmitResponse) super.invoke(new CmdRFCardTransmit(sendData));
        if (null != response) {
            return response.getData();
        }
        throw new NullPointerException("response is null!");
    }

    @Override
    public RFResult powerOn(RFCardType[] rfCardType, int timeout, RFCardPowerOnExtParams powerOnExtParams) {
        int invokeTimeout = timeout + 3;
        CmdRFCardPowerOnResponse response = null;
        if (powerOnExtParams == null) {
            response = (CmdRFCardPowerOnResponse) super.invoke(new CmdRFCardPowerOn(rfCardType, timeout), invokeTimeout, TimeUnit.SECONDS);
        } else {
            if (powerOnExtParams.getFelicaParams() != null) {
                throw new UnsupportedOperationException("This method is not supported by setFelicaParams in SDK2.0.");
            } else if ((byte) 0xFF != powerOnExtParams.getSak()) {
                response = (CmdRFCardPowerOnResponse) super.invoke(new CmdRFCardPowerOn(rfCardType, timeout, powerOnExtParams.getSak()), invokeTimeout, TimeUnit.SECONDS);
            } else {
                response = (CmdRFCardPowerOnResponse) super.invoke(new CmdRFCardPowerOn(rfCardType, timeout), invokeTimeout, TimeUnit.SECONDS);
            }
        }
        if (null != response) {
            return new RFResult(response.getRFCardType(), response.getCardSerialNo(), response.getATQA(), (byte) 0xff, null, null);
        }
        ErrorMsg msg = ErrorMsgHelper.getInstance().getErrorMsg(RFID_POWERON);
        throw new NullPointerException("response is null!" + " ErrCode:" + msg.getErrCode() + " ErrMsg:" + msg.getErrMsg() + " OtherMsg:" + msg.getOtherMsg());
    }

    @Override
    public boolean powerOff() {
        try {
            super.invoke(new CmdRFCardPowerOff(60));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isCardExist() {
        try {
            invoke(new CmdRFCardInduct());
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    @Override
    public byte[] transmit(byte[] command, long timeout) {
        CmdRFCardTransmit.CmdRFCardTransmitResponse response = (CmdRFCardTransmit.CmdRFCardTransmitResponse) super.invoke(new CmdRFCardTransmit(command), timeout, TimeUnit.SECONDS);
        byte[] apduData = null;
        if (null != response) {
            apduData = response.getData();
            if (null != apduData) {
                String apduStr = ISOUtils.hexString(apduData);
                if (apduData.length == 2 && apduStr.startsWith("61")) {
                    byte[] dataleg = new byte[]{apduData[1]};
                    apduData = fetchApduData(dataleg);
                }
                return apduData;

            }
        }
        throw new NullPointerException("response is null!");
    }

    @Override
    public byte[] felicaTransmit(byte[] command, long timeout) {
        throw new UnsupportedOperationException("This method is not supported in SDK2.0.");
    }

    @Override
    public boolean m1Authenticate(RFKeyMode rfKeyMode, byte[] snr, int blockNo, byte[] key) {
        try {
            super.invoke(new CmdM1CardAuthenticate(rfKeyMode, snr, blockNo, key));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public byte[] m1ReadBlockData(int blockNo) {
        CmdM1CardReadDataResponse response = (CmdM1CardReadDataResponse) super.invoke(new CmdM1CardReadData(blockNo));
        if (null != response) {
            return response.getData();
        }
        throw new NullPointerException("response is null!");
    }

    @Override
    public boolean m1WriteBlockData(int blockNo, byte[] data) {
        try {
            super.invoke(new CmdM1CardWriteData(blockNo, data));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean m1Increment(int blockNo, byte[] data) {
        try {
            super.invoke(new CmdM1CardIncrement(blockNo, data));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean m1Decrement(int blockNo, byte[] data) {
        try {
            super.invoke(new CmdM1CardDecrement(blockNo, data));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public byte[] getCardATS() {
        return null;
    }

    @Override
    public byte[] communication(byte[] sendData, int timeOut) {
        throw new UnsupportedOperationException("This method is not supported in SDK2.0.");
    }

    @Override
    public boolean setRFMode(RFCardMode rfCardMode) {
        throw new UnsupportedOperationException("This method is not supported in SDK2.0.");
    }

    @Override
    public boolean m0Authenticate(byte[] command) {
        throw new UnsupportedOperationException("This method is not supported in SDK2.0.");
    }

    @Override
    public byte[] m0ReadBlockData(int blockNo) {
        throw new UnsupportedOperationException("This method is not supported in SDK2.0.");
    }

    @Override
    public boolean m0WriteBlockData(int blockNo, byte[] data) {
        throw new UnsupportedOperationException("This method is not supported in SDK2.0.");
    }


}
