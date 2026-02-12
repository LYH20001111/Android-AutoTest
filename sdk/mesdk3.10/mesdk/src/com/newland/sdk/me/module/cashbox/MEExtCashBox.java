package com.newland.sdk.me.module.cashbox;
 
 
import android.annotation.SuppressLint;
import android.content.Context;
import android.newland.NlCashBoxManager;

import com.newland.sdk.module.cashbox.ExtCashBoxModule;
import com.newland.sdk.mtype.ExModuleType;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.AbstractModule;

public class MEExtCashBox extends AbstractModule implements ExtCashBoxModule {
	private DeviceLogger logger = DeviceLoggerFactory.getLogger("MEExtCashBox");

	private NlCashBoxManager nlCashBoxManager;
	public MEExtCashBox(AbstractDevice device) {
		super(device);
	}
	@SuppressLint("WrongConstant")
	public MEExtCashBox(AbstractDevice device, Context context) {
		super(device);
		nlCashBoxManager=(NlCashBoxManager) context.getSystemService("cashbox_service");
	}

	@Override
	public boolean isStandardModule() {
		return false;
	}

	@Override
	public ModuleType getStandardModuleType() {
		return null;
	}


	@Override
	public String getExModuleType() {
		return ExModuleType.CASHBOX;
	}


	@Override
	public int getVoltage() {
		return nlCashBoxManager.getVoltage();
	}


	@Override
	public void setVoltage(int mVoltage) {
		logger.debug("[setVoltage] mVoltage:"+mVoltage);
		nlCashBoxManager.setVoltage(mVoltage);		
	}


	@Override
	public long getTimeSec() {
		logger.debug("[getTimeSec]");
		return nlCashBoxManager.getTimeSec();
	}


	@Override
	public void setTimeSec(long mTimeSec) {
		logger.debug("[setTimeSec] mTimeSec:"+mTimeSec);
		nlCashBoxManager.setTimeSec(mTimeSec);
	}


	@Override
	public int OpenCashBox() {
		logger.debug("[OpenCashBox] ");
		return nlCashBoxManager.OpenCashBox();
	}


	@Override
	public int OpenCashBox(int voltage) {
		logger.debug("[OpenCashBox] voltage:"+voltage);
		return nlCashBoxManager.OpenCashBox(voltage);
	}


	@Override
	public int OpenCashBox(int voltage, long timeSec) {
		logger.debug("[OpenCashBox] voltage:"+voltage+"; timeSec:"+timeSec);
		return nlCashBoxManager.OpenCashBox(voltage, timeSec);
	}

}
