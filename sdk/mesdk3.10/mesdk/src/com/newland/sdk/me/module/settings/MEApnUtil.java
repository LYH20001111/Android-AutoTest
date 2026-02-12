package com.newland.sdk.me.module.settings;

import android.content.Context;
import android.newland.telephony.ApnEntity;
import android.newland.telephony.ApnUtils;

import com.newland.sdk.module.settings.ApnUtil;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;

import java.util.List;

public class MEApnUtil implements ApnUtil {
    private DeviceLogger deviceLogger = DeviceLoggerFactory.getLogger("MEApnUtil");
    private Context context;
    private ApnUtils nlApnUtils;
    private static MEApnUtil instance;

    public static MEApnUtil getInstance(Context context) {
        synchronized (MEApnUtil.class) {
            if (instance == null) {
                instance = new MEApnUtil(context);
            }
        }
        return instance;
    }

    private MEApnUtil(Context context) {
        this.context = context;
        nlApnUtils = new ApnUtils(context);
    }

    @Override
    public ApnEntity getCurrentApn() {
        deviceLogger.debug("[getCurrentApn]");
        return nlApnUtils.getPreferApn();
    }

    @Override
    public List<ApnEntity> getSystemApns() {
        deviceLogger.debug("[getSystemApns]getAllApnList start");
        return nlApnUtils.getAllApnList();
    }

    @Override
    public boolean removeApn(int id) {
        deviceLogger.debug("[removeApn] id:"+id);
        return nlApnUtils.removeApn(id);
    }

    @Override
    public int addNewApn(ApnEntity apnEntity) {
        deviceLogger.debug("[addNewApn]");
        deviceLogger.debug("apnEntity.getMcc:" + (apnEntity == null ? "null" : apnEntity.getMcc()));
        return nlApnUtils.addNewApn(apnEntity);
    }

    @Override
    public int setDefaultApn(int id) {
        deviceLogger.debug("[setDefaultApn], id:" + id);
        return nlApnUtils.setDefault(id);
    }

    @Override
    public List<ApnEntity> getCardApns() {
        deviceLogger.debug("[getCardApns]");
        return nlApnUtils.getCurrentApnList();
    }
}
