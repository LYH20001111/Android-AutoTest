package com.newland.nsdk.core.internal.routemanager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.IBinder;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.internal.devicemanager.DeviceInfo;
import com.newland.nsdk.core.api.internal.devicemanager.DeviceManager;
import com.newland.nsdk.core.api.internal.routemanager.NetWorkType;
import com.newland.nsdk.core.api.internal.routemanager.RouteInfo;
import com.newland.nsdk.core.api.internal.routemanager.RouteManager;
import com.newland.nsdk.core.internal.devicemanager.DeviceManagerImpl;
import com.newland.nsdk.core.internal.system.SystemPropertyUtil;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class RouteManagerImpl implements RouteManager {
    private static final String TAG = "RouteManagerImpl";
    private static volatile RouteManagerImpl instance;
    private Context mContext;
    private android.newland.net.RouteManager mRouteManager;
    private boolean isSupport = true;
    public static RouteManagerImpl getInstance(Context mContext) {
        if (instance == null) {
            synchronized (RouteManagerImpl.class) {
                if (instance == null || instance.mContext != mContext) {
                    instance = new RouteManagerImpl(mContext);
                }
            }
        } else {
            if (instance.mContext != mContext) {
                instance = new RouteManagerImpl(mContext);
            }
        }
        return instance;
    }

    public RouteManagerImpl(Context mContext) {
        this.mContext = mContext;
        mRouteManager = android.newland.net.RouteManager.getInstance(mContext);
    }

    private boolean isSupport() {
        String newlandFrameVersion = SystemPropertyUtil.getProperty("ro.build.version.nl_api", "");
        LogUtils.d(TAG, "frame version: " + newlandFrameVersion);
        if ("V2".equalsIgnoreCase(newlandFrameVersion)) {
            return true;
        } else {
            try {
                Class<?> serviceManagerClass = Class.forName("android.os.ServiceManager");
                Method getServiceMethod = serviceManagerClass.getMethod("getService", new Class[]{String.class});
                IBinder serviceManager = (IBinder)getServiceMethod.invoke(null, new Object[]{Context.CONNECTIVITY_SERVICE});
                Class<?> stubClass = Class.forName("android.net.IConnectivityManager$Stub");
                Method asInterfaceMethod = stubClass.getMethod("asInterface", new Class[]{IBinder.class});
                Object IConnectivityManager = asInterfaceMethod.invoke(null, serviceManager);
                Class<?> IConnectivityManagerClass = Class.forName(IConnectivityManager.getClass().getName());
                DeviceManager deviceManager = DeviceManagerImpl.getInstance(true);
                DeviceInfo deviceInfo = deviceManager.getDeviceInfo();
                int androidVersion = deviceInfo.getAndroidVersion();
                Method Method_removeRoute = null;
                if (androidVersion <= 29) {
                    LogUtils.d(TAG, "is A10 device.");
                    Method_removeRoute = IConnectivityManagerClass.getDeclaredMethod("removeRouteToHostAddress", int.class, byte[].class);
                } else {
                    LogUtils.d(TAG, "is A12 device.");
                    Method_removeRoute = IConnectivityManagerClass.getDeclaredMethod("removeRouteToHostAddress", int.class, byte[].class, String.class, String.class);
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

    }

    @Override
    public void enableMultiPath() throws NSDKException {
        if (!isSupport()) {
            throw new NSDKException(ErrorCode.UNSUPPORTED, "Unsupported RoutManager Module.");
        }
        boolean isEnable = mRouteManager.enableMultiPath();
        if (!isEnable) {
            throw new NSDKException(ErrorCode.ERROR, "Failed to enable MultiPath.");
        }
    }

    @Override
    public void disableMultiPath() throws NSDKException {
        if (!isSupport()) {
            throw new NSDKException(ErrorCode.UNSUPPORTED, "Unsupported RoutManager Module.");
        }
        boolean isDisabled = mRouteManager.disableMultiPath();
        if (!isDisabled) {
            throw new NSDKException(ErrorCode.ERROR, "Failed to disable MultiPath.");
        }
    }

    @Override
    public void addRoute(String ip, NetWorkType networkType) throws NSDKException {
        if (!isSupport()) {
            throw new NSDKException(ErrorCode.UNSUPPORTED, "Unsupported RoutManager Module.");
        }
        boolean isAdded = mRouteManager.addRoute(ip, networkType.getCode());
        if (!isAdded) {
            throw new NSDKException(ErrorCode.ERROR, "Failed to add route.");
        }
    }

    @Override
    public void removeRoute(String ip, NetWorkType networkType) throws NSDKException {
        if (!isSupport()) {
            throw new NSDKException(ErrorCode.UNSUPPORTED, "Unsupported RoutManager Module.");
        }
        boolean isRemoved = mRouteManager.removeRoute(ip, networkType.getCode());
        if (!isRemoved) {
            throw new NSDKException(ErrorCode.ERROR, "Failed to remove route.");
        }
    }

    @Override
    public RouteInfo queryRouteByIp(String ip) throws NSDKException {
        if (!isSupport()) {
            throw new NSDKException(ErrorCode.UNSUPPORTED, "Unsupported RoutManager Module.");
        }
        android.newland.net.RouteManager.RouteInfo routeInfo= mRouteManager.queryRouteByIp(ip);
        if (routeInfo == null) {
            throw new NSDKException("UnExisting route queried by IP.");
        }
        RouteInfo mRouteInfo = new RouteInfo(routeInfo.getAddress(), routeInfo.getNetworkType());
        return mRouteInfo;
    }

    @Override
    public void removeAllRoute() throws NSDKException {
        if (!isSupport()) {
            throw new NSDKException(ErrorCode.UNSUPPORTED, "Unsupported RoutManager Module.");
        }
        mRouteManager.removeAllRoute();
    }

    @Override
    public List<RouteInfo> getRouteList() throws NSDKException {
        if (!isSupport()) {
            throw new NSDKException(ErrorCode.UNSUPPORTED, "Unsupported RoutManager Module.");
        }
        List<android.newland.net.RouteManager.RouteInfo> routeInfoList = mRouteManager.getRouteList();
        List<RouteInfo> mRouteInfoList = new ArrayList<>();

        if (routeInfoList == null) {
            throw new NSDKException("No route records.");
        }
        for (int i = 0; i < routeInfoList.size(); i++) {
            RouteInfo mRouteInfo = new RouteInfo(routeInfoList.get(i).getAddress(), routeInfoList.get(i).getNetworkType());
            mRouteInfoList.add(mRouteInfo);
        }

        return mRouteInfoList;
    }

}
