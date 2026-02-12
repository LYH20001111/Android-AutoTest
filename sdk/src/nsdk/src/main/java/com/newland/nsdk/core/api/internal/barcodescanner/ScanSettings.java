package com.newland.nsdk.core.api.internal.barcodescanner;

import java.util.ArrayList;
import java.util.List;

public class ScanSettings {
    private Boolean UPCEANSwitch = false;
    private List<ScanCodeOption> scanCodeOptions = new ArrayList<>();

    /**
     * Gets whether to enable UPC/EAN additional code.
     * @return Whether to enable UPC/EAN additional code.
     */
    public Boolean isUPCEANSwitch() {
        return UPCEANSwitch;
    }

    /**
     * Sets whether to enable UPC/EAN additional code.
     * @param UPCEANSwitch whether to enable UPC/EAN additional code.
     */
    public void setUPCEANSwitch(Boolean UPCEANSwitch) {
        this.UPCEANSwitch = UPCEANSwitch;
    }

    /**
     * Gets the scan code option configurations list.
     * @return The scan code option configurations list.
     */
    public List<ScanCodeOption> getScanCodeOptions() {
        return scanCodeOptions;
    }

    /**
     * Sets the scan code option configurations list.
     * @param scanCodeOptions The scan code option configuration. See {@link ScanCodeOption}.
     */
    public void setScanCodeOptions(List<ScanCodeOption> scanCodeOptions) {
        this.scanCodeOptions = scanCodeOptions;
    }
}
