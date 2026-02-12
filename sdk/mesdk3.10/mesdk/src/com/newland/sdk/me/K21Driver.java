package com.newland.sdk.me;

import com.newland.sdk.me.cmd.CmdCancelAndReset;
import com.newland.sdk.mtype.conn.DeviceConnParams;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.AbstractDeviceDriver;
import com.newland.sdk.mtypex.conn.DeviceConnection;
import com.newland.sdk.mtypex.conn.DeviceConnector;
import com.newland.sdk.mtypex.conn.DeviceExecutor;
import com.newland.sdk.mtypex.conn.DeviceKeepAliveStrategy;
import com.newland.sdk.mtypex.nseries.NSeriesConnector;
import com.newland.sdk.mtypex.nseries3.NS3Connector;
import com.newland.sdk.mtypex.nseries3.NS3Executor;

import java.util.ArrayList;
import java.util.List;

public class K21Driver extends AbstractDeviceDriver {
    private DeviceLogger logger = DeviceLoggerFactory.getLogger("K21Driver");

    public K21Driver() {
        super();
    }

    private static final List<DeviceConnector> deviceConnectors = new ArrayList<DeviceConnector>();

    static {
        deviceConnectors.add(new NSeriesConnector());
        deviceConnectors.add(new NS3Connector());
    }

    @Override
    public List<DeviceConnector> initConnectors() {
        return deviceConnectors;
    }

    @Override
    public AbstractDevice createDevice(DeviceExecutor executor) {
        if (executor instanceof NS3Executor) {
            logger.debug("[createDevice]  NS3Executor");
            return new NLDevice(executor);
        } else {
            return new K21Device(executor);
        }

    }

    @Override
    protected DeviceKeepAliveStrategy getDeviceKeepAliveStrategy(DeviceConnParams connParams) {
        return new DefaultKeepAliveStrategy();
    }

    private class DefaultKeepAliveStrategy implements DeviceKeepAliveStrategy {

        @Override
        public long getDefaultExecTimeout() {
            return 3000L;
        }

        @Override
        public void doDefaultReset(DeviceConnection deviceConnection) throws Exception {
            if (deviceConnection != null) {
                CmdCancelAndReset reset = new CmdCancelAndReset();
                deviceConnection.send(reset, getDefaultResetTimeout());
            }
        }

        @Override
        public long getDefaultResetTimeout() {
            return 3000L;
        }
    }
}
