package com.newland.nsdkdemo.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

public class LocationUtils {

    private Context mContext;
    public LocationUtils(Context mContext) {
        this.mContext = mContext;
    }

    @SuppressLint("MissingPermission")
    public String getLocationInfo(){
        int mcc;
        int mnc ;
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

        if (!hasSimCard(mContext)){
            mcc = 460;
            mnc = 0;
        }else {
            String operator = telephonyManager.getNetworkOperator();
            if (operator == null || operator.equals("")){
                mcc = 460;
                mnc = 0;
            }else {
                mcc = Integer.parseInt(operator.substring(0, 3));
                mnc = Integer.parseInt(operator.substring(3));
            }

        }
        int cid = 0;
        int lac = 0;

        //03 05 11 is Telecom CDMA
        if (mnc == 11 || mnc == 03 || mnc == 05){
            CdmaCellLocation location = (CdmaCellLocation) telephonyManager.getCellLocation();
            if (location != null){
                cid = location.getBaseStationId();
                lac = location.getNetworkId();
                mnc = location.getSystemId();
            }

        } else {
            assert telephonyManager != null;
            GsmCellLocation location = (GsmCellLocation) telephonyManager.getCellLocation();
            if (location != null){
                cid = location.getCid();
                lac = location.getLac();
            }
        }

        return mcc + "," + mnc + "," + lac + "," + cid;
    }

    public boolean hasSimCard(Context context) {
        TelephonyManager telMgr = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();
        boolean result = true;
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT:
                // no SIM card
                result = false; // 没有SIM卡
                break;
            case TelephonyManager.SIM_STATE_UNKNOWN:
                result = false;
                break;
        }
        Log.d("try", result ? "SIM card" : "no SIM card");
        return result;
    }
}
