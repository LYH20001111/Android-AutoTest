package com.newland.sdk.module.pin;
/**
 * @Description
 * @Author wuhh
 * @Date 2022/4/11
 */
public class RKLParams {
    private String configFile;
    private RKLAPIType rklApi;

    /**
     * gets config file path.
     * @return
     */
    public String getConfigFile() {
        return configFile;
    }

    /**
     * RKL config file path.
     * @param configFile
     */
    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    /**
     * gets rkl protocol.
     * @return
     */
    public RKLAPIType getRklApi() {
        return rklApi;
    }

    /**
     * sets rkl protocol.
     * @param rklApi
     */
    public void setRklApi(RKLAPIType rklApi) {
        this.rklApi = rklApi;
    }

    public enum RKLAPIType{
        NewlandAPI,
    }
}
