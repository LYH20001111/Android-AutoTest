package com.newland.nsdkdemo.internal.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import com.newland.nsdk.core.api.internal.routemanager.RouteInfo;
import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.internal.routemanager.NetWorkType;
import com.newland.nsdk.core.api.internal.routemanager.RouteManager;
import com.newland.nsdk.core.internal.NSDKModuleManagerImpl;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.annotation.MethodGridEntity;
import com.newland.nsdkdemo.common.utils.DialogUtils;
import com.newland.nsdkdemo.common.utils.MessageTag;

import java.util.List;

public class RouteManagerFragment extends InternalBaseFragment{
    private RouteManager mRouteManager;
    public RouteManagerFragment(Context context) {
        super(context, LayoutMode.GRID);

    }

    @Override
    public String title() {
        return context.getString(R.string.tv_route_manager);
    }

    @Override
    public void initData() {
        mRouteManager = (RouteManager) NSDKModuleManagerImpl.getInstance().getModule(ModuleType.ROUTE_MANAGER);
    }

    @Override
    public Object getModule() {
        return RouteManagerFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.tv_route_manager_enable_multi_path, functionid = 1)
    private void enableMultiPath() {
        try {
            mRouteManager.enableMultiPath();
            showMessage(context.getString(R.string.tv_route_manager_enable_multi_path));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.tv_route_manager_enable_multi_path));
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_route_manager_disable_multi_path, functionid = 2)
    private void disableMultiPath() {
        try {
            mRouteManager.disableMultiPath();
            showMessage(context.getString(R.string.tv_route_manager_disable_multi_path));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.tv_route_manager_disable_multi_path));
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_route_manager_add_route, functionid = 3)
    private void addRoute() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_route_manager_add_route), null, R.layout.route_manager_add_remove_route, (id, view)-> {
            Spinner spnNetworkType = view.findViewById(R.id.spn_routeManager_networkType);
            NetWorkType netWorkType = getNetworkType(spnNetworkType.getSelectedItem().toString());
            Log.d("getNetwork", netWorkType.name());
            EditText editIp = view.findViewById(R.id.edit_routeManager_ip);
            String ip = editIp.getText().toString();
            try {
                mRouteManager.addRoute(ip, netWorkType);
                showMessage(context.getString(R.string.tv_route_manager_add_route));
            } catch (NSDKException e) {
                showErrorMessage(e, context.getString(R.string.tv_route_manager_add_route));
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @MethodGridEntity(btnnameid = R.string.tv_route_manager_remove_route, functionid = 4)
    private void removeRoute() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_route_manager_remove_route), null, R.layout.route_manager_add_remove_route, (id, view)-> {
            EditText editIp = view.findViewById(R.id.edit_routeManager_ip);
            String ip = editIp.getText().toString();
            Spinner spnNetworkType = view.findViewById(R.id.spn_routeManager_networkType);
            NetWorkType netWorkType = getNetworkType(spnNetworkType.getSelectedItem().toString());
            try {
                mRouteManager.removeRoute(ip, netWorkType);
                showMessage(context.getString(R.string.tv_route_manager_remove_route));
            } catch (NSDKException e) {
                showErrorMessage(e, context.getString(R.string.tv_route_manager_remove_route));
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_route_manager_remove_all_routes, functionid = 5)
    private void removeAllRoutes() {
        try {
            mRouteManager.removeAllRoute();
            showMessage(context.getString(R.string.msg_remove_all_routes));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.tv_route_manager_remove_all_routes));
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_route_manager_query_route_by_ip, functionid = 6)
    private void queryRouteByIp() {
        DialogUtils.createCustomDialog(context, R.string.tv_route_manager_query_route_by_ip, null, R.layout.route_manager_add_remove_route, new DialogUtils.CustomDialogCallback2() {

            @Override
            public void onInit(View view) {
                LinearLayout llNetworkTypeParams = view.findViewById(R.id.linear_routeManager_networkType);
                llNetworkTypeParams.setVisibility(View.GONE);
            }

            @Override
            public void onResult(int id, View view) {
                EditText editIp = view.findViewById(R.id.edit_routeManager_ip);
                try {
                    RouteInfo routeInfo = mRouteManager.queryRouteByIp(editIp.getText().toString());
                    Log.d("Networktype", routeInfo.getNetworkType() + "");
                    showMessage(context.getString(R.string.msg_get_network) + getNetworkTypeName(routeInfo.getNetworkType()));
                    showMessage(context.getString(R.string.msg_get_address) + routeInfo.getAddress());
                } catch (NSDKException e) {
                    showMessage(e.getMessage(), MessageTag.ERROR);
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_route_manager_get_route_list, functionid = 7)
    private void getRouteList() {
        try {
            List<RouteInfo> routeInfos = mRouteManager.getRouteList();
            for (RouteInfo ri : routeInfos) {
                showMessage(context.getString(R.string.msg_get_network) + getNetworkTypeName(ri.getNetworkType()));
                showMessage(context.getString(R.string.msg_get_address) + ri.getAddress());
            }
//                for (int i = 0; i < routeInfos.size(); i++) {
//                    showMessage(routeInfos.get(i).getNetworkType() + "");
//                    showMessage(context.getString(R.string.msg_get_network) + getNetworkTypeName(routeInfos.get(i).getNetworkType()));
//                    showMessage(context.getString(R.string.msg_get_address) + routeInfos.get(i).getAddress());
//                }


        } catch (NSDKException e) {
            showMessage(e.getMessage(), MessageTag.ERROR);
        }
    }

    private NetWorkType getNetworkType(String tempStr) {
        if (tempStr.equals("Mobile")) {
            return NetWorkType.NET_WORK_MOBILE;
        } else if (tempStr.equals("Wifi")) {
            return NetWorkType.NET_WORK_WIFI;
        } else if (tempStr.equals("Ethernet")) {
            return NetWorkType.NET_WORK_ETHERNET;
        }
        return null;
    }

    private String getNetworkTypeName(int code) {
        if (code == NetWorkType.NET_WORK_MOBILE.getCode()) {
            return "4G";
        } else if (code == NetWorkType.NET_WORK_WIFI.getCode()) {
            return "WIFI";
        } else if (code == NetWorkType.NET_WORK_ETHERNET.getCode()) {
            return "Ethernet";
        }
        return null;
    }
}
