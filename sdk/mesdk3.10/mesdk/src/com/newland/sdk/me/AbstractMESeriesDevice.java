package com.newland.sdk.me;

import com.newland.sdk.me.cmd.common.CmdDeviceGetTime;
import com.newland.sdk.me.cmd.common.CmdDeviceSetTime;
import com.newland.sdk.me.cmd.common.CmdGetDeviceParams;
import com.newland.sdk.me.cmd.common.CmdSetDeviceParams;
import com.newland.sdk.me.cmd.common.CmdGetDeviceInfo;
import com.newland.sdk.me.cmd.common.CmdGetTusn;
import com.newland.sdk.me.cmd.common.CmdRandom;
import com.newland.sdk.me.cmd.common.CmdSetCSN;
import com.newland.sdk.me.utils.DeviceInfoUtils;
import com.newland.sdk.module.devicebasic.DeviceInfo;
import com.newland.sdk.mtype.Module;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.utils.TLVPackage;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.conn.DeviceExecutor;

import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public abstract class AbstractMESeriesDevice extends AbstractDevice {

    private DeviceLogger logger = DeviceLoggerFactory.getLogger(this.getClass().getName());

    protected final Map<ModuleType, Module> standardModules = new HashMap<ModuleType, Module>();

    protected final Map<String, Module> externalModules = new HashMap<String, Module>();

    public AbstractMESeriesDevice(DeviceExecutor deviceExecutor) {
        super(deviceExecutor);
        initModule();
    }

    protected abstract void initModule();

    protected abstract void setStandardModules(ModuleType moduleType);

    protected abstract void setExModule(String moduleType);

    @Override
    public DeviceInfo getDeviceInfo() {
        if (!DeviceInfoUtils.getHasSecModule()) {
            CmdGetDeviceInfo.CmdGetDeviceInfoResponse response = new CmdGetDeviceInfo.CmdGetDeviceInfoResponse();
            return response.getDeviceInfo();
        }
        CmdGetDeviceInfo.CmdGetDeviceInfoResponse response = (CmdGetDeviceInfo.CmdGetDeviceInfoResponse) invoke(new CmdGetDeviceInfo());
        return response.getDeviceInfo();
    }

    @Override
    public Date getDeviceDate() {
        CmdDeviceGetTime.GetTimeResponse deviceResponse = (CmdDeviceGetTime.GetTimeResponse) invoke(new CmdDeviceGetTime());
        return deviceResponse.getDeviceDate();
    }

    @Override
    public void setDeviceDate(Date date) {
        invoke(new CmdDeviceSetTime(date));
    }

    @Override
    public ModuleType[] getSupportStandardModule() {
        Set<ModuleType> keyset = standardModules.keySet();
        return keyset.toArray(new ModuleType[keyset.size()]);
    }

    @Override
    public Module getStandardModule(ModuleType moduleType) {
        Module module = standardModules.get(moduleType);
        if(module==null && DeviceInfoUtils.getHasSecModule()){
            setStandardModules(moduleType);
            module = standardModules.get(moduleType);
        }
        if (module == null && !DeviceInfoUtils.getHasSecModule() && moduleType.equals(ModuleType.PRINTER)) {
            setStandardModules(moduleType);
            module = standardModules.get(moduleType);
        }
        return module;
    }

    @Override
    public String[] getSupportExModule() {
        Set<String> keyset = externalModules.keySet();
        return keyset.toArray(new String[keyset.size()]);
    }

    @Override
    public Module getExModule(String moduleType) {
        Module module = externalModules.get(moduleType);
        if(module==null && DeviceInfoUtils.getHasSecModule()){
            setExModule(moduleType);
            module = externalModules.get(moduleType);
        }
        return module;
    }

    @Override
    public void setDeviceParams(TLVPackage tlvPackage) {
        invoke(new CmdSetDeviceParams(tlvPackage.pack()));
    }

    @Override
    public TLVPackage getDeviceParams(int... tags) {
        CmdGetDeviceParams.CmdGetDeviceParamsResponse response = (CmdGetDeviceParams.CmdGetDeviceParamsResponse) invoke(new CmdGetDeviceParams(tags));
        return response.getParamsContent();
    }

    @Override
    public void reset() {
        deviceExecutor.cancelCurrentExecCmd();
    }

    @Override
    public void setCSN(String csn) {
        if (!DeviceInfoUtils.getHasSecModule()) {
            return;
        }
        invoke(new CmdSetCSN((byte) 0x04, csn));
    }

    @Override
    public Locale getDefaultLocale() {
        return Locale.getDefault();
    }

    @Override
    public byte[] getRandom(int len) {
        CmdRandom.CmdRandomResponse response = (CmdRandom.CmdRandomResponse) invoke(new CmdRandom(len));
        return response.getRandom();
    }

    @Override
    public String getTusn() {
        CmdGetTusn.CmdTusnResponse response = (CmdGetTusn.CmdTusnResponse) invoke(new CmdGetTusn());
        logger.debug("tusn answerCode:" + response.getAnswerCode());
        return response.getPosTusn();
    }
    protected Module getInstanceByReflect(String classname, Object[] params) {
        Module instance = null;
        try {
            Class classType = Class.forName(classname);
            Constructor<?>[] consts = classType.getConstructors();
            Constructor<?> constructor = null;
            for (int i = 0; i < consts.length; i++) {
                int paramsLength = consts[i].getParameterAnnotations().length;
                if (paramsLength == params.length) {
                    constructor = consts[i];
                    break;
                }
            }
            if (constructor != null) {
                Class<?>[] type = constructor.getParameterTypes();
                instance = (Module) classType.getConstructor(type).newInstance(params);
            }
        } catch (Exception e) {
            logger.error("reflect module instance failed!");
            e.printStackTrace();
        }
        return instance;
    }
}
