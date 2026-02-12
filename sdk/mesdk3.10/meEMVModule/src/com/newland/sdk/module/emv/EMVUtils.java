/*
 * jPOS Project [http://jpos.org]
 * Copyright (C) 2000-2012 Alejandro P. Revilla
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.newland.sdk.module.emv;

import com.newland.sdk.mtypex.module.common.emv.SimpleEmvPackager;
import com.newland.sdk.mtypex.tlv.SimpleTLVMsg;
import com.newland.sdk.mtypex.tlv.SimpleTLVPackage;
import com.newland.sdk.utils.TLVMsg;
import com.newland.sdk.utils.TLVPackage;

/**
 * EMV utility class
 *
 * @since ver3.10.01
 */
public class EMVUtils {
    private EMVUtils() {
        throw new AssertionError();
    }


    public static TLVPackage newTlvPackage() {
        return new SimpleTLVPackage();
    }

    public static TLVMsg newTlvMsg() {
        return new SimpleTLVMsg();
    }

    public static EmvPackager newEmvPackager() {
        return new SimpleEmvPackager();
    }

}
