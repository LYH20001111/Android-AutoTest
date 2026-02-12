package com.newland.nsdk.externaldevice.keymanager;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.external.command.keymanager.Giske;

import org.junit.Test;

public class GiskeTest {

    @Test
    public void pack() {
        Giske giske = new Giske();
        giske.setAlgorithm(Giske.GiskeAlgorithm.T);
        giske.setKeyData(new byte[]{0x11, 0x22, 0x33, 0x44, 0x55});
        giske.setKeyUsage(Giske.GiskeKeyUsage.USAGE_00);
        giske.setMac(new byte[]{0x1A, 0x1B, 0x1C, 0x1D});
        giske.setMode(Giske.GiskeMode.E);
        giske.setVersionNumber(Giske.GiskeVersion.VERSION_A);
        try {
            System.out.println(ISOUtils.hexString(giske.pack()));
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }
}