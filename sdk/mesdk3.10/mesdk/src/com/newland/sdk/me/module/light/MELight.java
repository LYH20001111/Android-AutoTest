package com.newland.sdk.me.module.light;

import com.newland.sdk.me.cmd.light.CmdoperateLight;
import com.newland.sdk.me.cmd.light.CmdoperateLightBlink;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.module.light.IndicatorLightModule;
import com.newland.sdk.module.light.LightState;
import com.newland.sdk.module.light.LightColor;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.AbstractModule;

public class MELight extends AbstractModule implements IndicatorLightModule {

	public MELight(AbstractDevice device) {
		super(device);
	}

	@Override
	public boolean isStandardModule() {
		return true;
	}

	@Override
	public ModuleType getStandardModuleType() {
		return ModuleType.INDICATOR_LIGHT;
	}

	@Override
	public String getExModuleType() {
		return null;
	}

	@Override
	public boolean operateLight(LightColor[] lightColor, LightState lightState) {
		try {
			switch (lightState){
				case TURNON:
					invoke(new CmdoperateLight(CmdoperateLight.LIGHT_TURN_ON, lightColor));
					break;

				case TURNOFF:
					invoke(new CmdoperateLight(CmdoperateLight.LIGHT_TURN_OFF, lightColor));

					break;
				case BLINK:
					invoke(new CmdoperateLight(CmdoperateLight.LIGHT_BLINK, lightColor));

					break;

			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean blinkLight(LightColor[] lightColor, int count, int timeInterval) {
		try {
			invoke(new CmdoperateLightBlink(lightColor, count, timeInterval));
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}
}
