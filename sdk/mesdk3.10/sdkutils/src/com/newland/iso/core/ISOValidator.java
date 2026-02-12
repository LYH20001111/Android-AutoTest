package com.newland.iso.core;

import com.newland.iso.message.IField;
import com.newland.iso.message.MessageException;


/**
 * Validates a jPOS ISOComponent. These kind of validations are
 * considered in lowest level. Validation models at higher level
 * must not be considered here. Something like field-interdependency
 * and others complex validation rules are not included in these validations.
 * <p>Title: jPOS</p>
 * <p>Description: Java Framework for Financial Systems</p>
 * <p>Copyright: Copyright (c) 2000 jPOS.org.  All rights reserved.</p>
 * <p>Company: www.jPOS.org</p>
 * @author Jose Eduardo Leon
 * @version 1.0
 */
public interface ISOValidator {

    /**
     * Validate an ISOComponent.
     * @throws ISOVException if break-on-error is assummed and there are
     * some errors.
     */
    public IField<?> validate( IField<?> m ) throws MessageException;
}
