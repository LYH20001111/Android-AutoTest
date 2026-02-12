package com.newland.sdk.me.module.emv;

import android.content.Context;

import com.newland.sdk.mtypex.AbstractDevice;

/**
 * Author by bxy, Date on 2019/11/11.
 */
public class MEEMVL2 extends MEEMVLevel2 {
    public static String emvProfilePathPath = null;
    public MEEMVL2(AbstractDevice device, Context context) {
        super(device,context);
    }

    @Override
    public String getProfilePath() {
        return emvProfilePathPath;
    }
}
