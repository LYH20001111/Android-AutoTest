/*
 * jPOS Project [http://jpos.org]
 * Copyright (C) 2000-2008 Alejandro P. Revilla
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

package com.newland.iso.core;

import java.util.BitSet;

import com.newland.iso.message.Field;
import com.newland.iso.message.IField;
import com.newland.iso.message.packager.FieldPackager;

/**
 * IF*_BITMAP classes extends this class instead of ISOFieldPackager
 * so packagers can check if field-1 ISOFieldPackager is an instance
 * of an ISOBitMapPackager and handle differences between ANSI X9.2
 * and ISO-8583 packaging schemes.
 *
 * @author apr@cs.com.uy
 * @version $Id: ISOBitMapPackager.java 2594 2008-01-22 16:41:31Z apr $
 *
 * @see FieldPackager
 */
public abstract class ISOBitMapPackager extends FieldPackager {
    public ISOBitMapPackager() {
        super();
    }
    public ISOBitMapPackager(int len, String description) {
        super(len, description);
    }
    public IField<?> createComponent(int fieldNumber) {
        return new Field<BitSet> (fieldNumber);
    }
}
