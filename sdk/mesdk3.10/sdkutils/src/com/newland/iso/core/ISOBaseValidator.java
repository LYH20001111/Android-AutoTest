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

import com.newland.iso.message.IField;
import com.newland.iso.message.MessageException;

/**
 * Base Validator class for jPOS composed ISOComponents (ISOMsg).
 * <p>Title: jPOS</p>
 * <p>Description: Java Framework for Financial Systems</p>
 * <p>Copyright: Copyright (c) 2000 jPOS.org.  All rights reserved.</p>
 * <p>Company: www.jPOS.org</p>
 * @author Jose Eduardo Leon
 * @version 1.0
 */
public class ISOBaseValidator implements ISOValidator {

    public ISOBaseValidator() {
        super();
    }

    /**
     * Creates the validator.
     * @param breakOnError flag indicating validation abort condition
     */
    public ISOBaseValidator( boolean breakOnError ) {
        this.breakOnError = breakOnError;
    }

    public boolean breakOnError(){
        return breakOnError;
    }

    public void setBreakOnError( boolean breakOnErr ){
        this.breakOnError = breakOnErr;
    }

    /**
     * Validate field-interdependency.
     * @param m Component to validate
     * @return ISOComponent or ISOVComponent resulting of validation process.
     * @throws MessageException if break-on-error is true and an error succedd.
     */
    public IField<?> validate( IField<?> m ) throws MessageException{
        if ( m.getValue() != m )
            throw new MessageException ( "Can't call validate on non Composite" );
        return m;
    }

    /** Flag used to indicate if validat process break
     * on first error or keep an error set **/
    protected boolean breakOnError = false;
}