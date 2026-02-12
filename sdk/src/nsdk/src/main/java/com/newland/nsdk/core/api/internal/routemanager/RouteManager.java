package com.newland.nsdk.core.api.internal.routemanager;

import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.exception.NSDKException;

import java.util.List;
/**
 * Route Manager
 *
 *
 * <p>How to get this Module:</p>
 * <pre>
 *     RouteManager mRouteManager = (RouteManager)NSDKModuleManagerImpl.getInstance().getModule(ModuleType.ROUTE_MANAGER);
 * </pre>
 * @version This module is supported in N910 PRO V1.1.47 or higher and P300 V1.1.05 or higher.
 */
public interface RouteManager extends Module {
    /**
     * Enable customized routes, make it valid to database.
     * @throws NSDKException
     */
    void enableMultiPath() throws NSDKException;

    /**
     * Disabled customized routes.
     * @throws NSDKException
     */
    void disableMultiPath() throws NSDKException;

    /**
     * Add customized route.
     *
     * <p>Note: This method can only save the customized route record, which should call "enableMultiPath()" afterwards to make it valid to database.</p>
     * <p>When multiple networks are enabled at the same time, they are accessed based on the configured routes in the database.
     *    For example, when set mobile to "192.168.1.1", the device will access the IP address by mobile if mobile and WIFI are enabled at the same time.
     * </p>
     * <p>For Example:</p>
     * <pre>
     *     RouteManager mRouteManager = (RouteManager)NSDKModuleManagerImpl.getInstance().getModule(ModuleType.ROUTE_MANAGER);
     *     try {
     *         mRouteManager.addRoute("192.168.152.200", NetWorkType.NET_WORK_MOBILE);
     *         mRouteManager.addRoute(“163.177.151.118”, NetWorkType.NET_WORK_WIFI);
     *         mRouteManager.enableMultiPath();
     *     } catch(NSDKException e) {
     *         e.printStackTrack();
     *     }
     *
     * </pre>
     *
     * @param ip            <b>[Required]</b> IP of the customized route.
     * @param networkType   <b>[Required]</b> The networkType of the customized route, see {@link com.newland.nsdk.core.api.internal.routemanager.NetWorkType}
     * @throws NSDKException
     */
    void addRoute(String ip, NetWorkType networkType) throws NSDKException;

    /**
     * Remove designated customized route.
     * <p>Note: This method can only record the route to be removed, which should call "enabledMultiPath()" afterwards to make it valid to database.</p>
     * <p>For Example:</p>
     * <pre>
     *     RouteManager mRouteManager = (RouteManager)NSDKModuleManagerImpl.getInstance().getModule(ModuleType.ROUTE_MANAGER);
     *     try {
     *         mRouteManager.removeRoute("163.177.151.118", NetWorkType.NET_WORK_WIFI);
     *         mRouteManager.enableMultiPath();
     *     } catch(NSDKException e) {
     *         e.printStackTrack();
     *     }
     * </pre>
     * @param ip
     * @param networkType
     * @throws NSDKException
     */
    void removeRoute(String ip, NetWorkType networkType) throws NSDKException;

    /**
     * Get customized route information queried by IP
     * <p>Note:This method can only get route information in database.</p>
     * <p>For Example:</p>
     * <pre>
     *     RouteManager mRouteManager = (RouteManager)NSDKModuleManagerImpl.getInstance().getModule(ModuleType.ROUTE_MANAGER);
     *     try {
     *         RouteInfo mRouteInfo = mRouteManager.queryRouteByIp("163.177.151.118");
     *         String address = mRouteInfo.getAddress();
     *         int networkType = mRouteInfo.getNetworkType();
     *         Log.d("RouteManager", "Address:" + address);
     *         Log.d("RouteManager", "NetworkType:" + String.valueOf(networkType));
     *     } catch(NSDKException e) {
     *         e.printStackTrack();
     *     }
     * </pre>
     * @param ip
     * @return Route information, see {@link com.newland.nsdk.core.api.internal.routemanager.RouteInfo}
     * @throws NSDKException
     */
    RouteInfo queryRouteByIp(String ip) throws NSDKException;

    /**
     * Remove all routes.
     * <p>Note: This method can remove all routes, including not only in database, but temporarily saved in buffer. </p>
     * @throws NSDKException
     */
    void removeAllRoute() throws NSDKException;

    /**
     * Get all routes information.
     * <p>Note: When there's no records in database, this method will throw a exception.</p>
     * <pre>
     *     RouteManager mRouteManager = (RouteManager)NSDKModuleImpl.getInstance().getModule(ModuleType.ROUTE_MANAGER);
     *     try {
     *         List<RouteInfo> routeInfos = mRouteManager.getRouteList();
     *         for (RouteInfo routeInfo : routeInfos) {
     *              Log.d("RouteManager", "Address:" + routeInfo.getAddress());
     *              Log.d("RouteManager", "NetworkType:" + routeInfo.getNetworkType());
     *         }
     *     } catch(NSDKException e) {
     *         e.printStackTrack();
     *     }
     * </pre>
     * @return The list of all customized route data.
     * @throws NSDKException
     */
    List<RouteInfo> getRouteList() throws NSDKException;


}
