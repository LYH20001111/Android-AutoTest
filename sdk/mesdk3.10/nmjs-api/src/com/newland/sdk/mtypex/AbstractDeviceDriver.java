package com.newland.sdk.mtypex;

import android.content.Context;

import com.newland.sdk.mtype.ConnectionCloseEvent;
import com.newland.sdk.mtype.Device;
import com.newland.sdk.mtype.DeviceDriver;
import com.newland.sdk.mtype.conn.DeviceConnParams;
import com.newland.sdk.mtype.conn.DeviceConnType;
import com.newland.sdk.mtype.event.DeviceEventListener;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtypex.cmd.CommandSerializer;
import com.newland.sdk.mtypex.conn.DefaultDeviceExecutor;
import com.newland.sdk.mtypex.conn.DeviceConnector;
import com.newland.sdk.mtypex.conn.DeviceExecutor;
import com.newland.sdk.mtypex.conn.DeviceKeepAliveStrategy;
import com.newland.sdk.mtypex.nseries.NSeriesDeviceExecutor;
import com.newland.sdk.mtypex.nseries3.NS3Executor;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractDeviceDriver implements DeviceDriver {

    private static DeviceLogger logger = DeviceLoggerFactory.getLogger(AbstractDeviceDriver.class);

    private DeviceConnType[] supportTypes;
    private Map<DeviceConnType, DeviceConnector> connectorMapping;

    private static Properties sdkProperties;

    private static String sdkVersion = null;

    public AbstractDeviceDriver() {
        init(initConnectors());
    }

    private void init(List<DeviceConnector> connectors) {
        Set<DeviceConnType> rslt = new HashSet<DeviceConnType>();
        connectorMapping = new HashMap<DeviceConnType, DeviceConnector>();
        for (DeviceConnector connector : connectors) {
            for (DeviceConnType connType : connector.getSupportConnType()) {
                if (connectorMapping.get(connType) == null) {
                    connectorMapping.put(connType, connector);
                    rslt.add(connType);
                }
            }
        }
        supportTypes = rslt.toArray(new DeviceConnType[rslt.size()]);
    }

    @Override
    public Device connect(Context context, DeviceConnParams connParams,DeviceEventListener<ConnectionCloseEvent> closedListener) {
        DeviceConnector connector = connectorMapping.get(connParams.getConnectType());
        if (connector != null) {
            switch (connParams.getConnectType()) {
                case NSCONNECTOR_V100:
                    NSeriesDeviceExecutor executor = new NSeriesDeviceExecutor(context, new MEPayloadSerializer());
                    return createDevice(executor);
                case NSCONNECTOR_V300:
                    NS3Executor ns3Executor = new NS3Executor(context, new MEPayloadSerializer());
                    return createDevice(ns3Executor);
                case BLUETOOTH_V100:
                case USB_V100:
                    try {
                        DefaultDeviceExecutor defaultDeviceExecutor = new DefaultDeviceExecutor(context,connector, connParams, closedListener, getDeviceKeepAliveStrategy(connParams));
                        return createDevice(defaultDeviceExecutor);
                    }catch (Exception e){
                        e.printStackTrace();
                        throw new IllegalArgumentException("connect exception:"+e);
                    }
                default:
                    break;

            }
            logger.debug("connParams.getConnectType()=" + connParams.getConnectType());

        }
        throw new IllegalArgumentException("not support conntype:" + connParams.getConnectType());
    }


    public DeviceConnType[] getSupportConnType() {
        return supportTypes;
    }

    @Override
    public boolean isSupportedConnType(DeviceConnType toBeSupported) {
        for (DeviceConnType connType : supportTypes) {
            if (toBeSupported == connType) {
                return true;
            }
        }
        return false;
    }

    private void initSDKProperties() {
        if (sdkProperties == null) {
            try {
                Properties p = new Properties();
                URL url = getClass().getClassLoader().getResource("sdk.properties");
                InputStream inputStream = null;
                if(url != null){
                    inputStream = url.openStream();
                }
                if(inputStream == null){
                    inputStream = this.getClass().getClassLoader().getResourceAsStream("sdk.properties");
                }
                if (inputStream == null)
                    return;
                else {
                    p.load(inputStream);
                    sdkProperties = p;
                }
            } catch (Exception e) {
                logger.error("load sdkproperties failed!", e);
            }
        }
    }

    @Override
    public String getSDKVersion() {
        if (sdkVersion == null)
            initSDKVersion();
        return sdkVersion;
    }

    private void initSDKVersion() {
        initSDKProperties();
        try {
            if (sdkProperties == null)
                return;
            sdkVersion = sdkProperties.getProperty("mesdk.version");
        } catch (Exception e) {
            logger.error("failed to init sdk version!", e);
        }
    }

    protected static CommandSerializer getDefaultCommandSerializer(){
        return new MEPayloadSerializer();
    }

    /**
     * Get a Strategy to Keep Device Alive when connect the device
     *
     * @param connParams
     * @return
     * @since ver3.10.01
     */
    protected abstract DeviceKeepAliveStrategy getDeviceKeepAliveStrategy(DeviceConnParams connParams);

    /**
     * Init Connectors
     *
     * @return
     * @since ver3.10.01
     */
    protected abstract List<DeviceConnector> initConnectors();

    /**
     * Use DeviceExecutor to create Device
     *
     * @param executor
     * @return
     * @since ver3.10.01
     */
    protected abstract AbstractDevice createDevice(DeviceExecutor executor);

}
