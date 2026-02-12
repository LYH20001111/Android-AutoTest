package com.newland.iso.core;

import java.util.ListIterator;


/**
 * List of errors. It is formed during validation process.
 * <p>Title: jPOS</p>
 * <p>Description: Java Framework for Financial Systems</p>
 * <p>Copyright: Copyright (c) 2000 jPOS.org.  All rights reserved.</p>
 * <p>Company: www.jPOS.org</p>
 * @author Jose Eduardo Leon
 * @version 1.0
 */
public interface ISOVErrorList {

    /**
     * Add an ISOVError to the list.
     * @see com.newland.iso.core.jpos.iso.ISOVError
     * @param Error error detailed instance.
     * @return true if error list change after operation. See Collection.
     */
    public boolean addISOVError( ISOVError Error );

    /**
     * Get an error list iterator.
     * @return iterator instance.
     */
    public ListIterator errorListIterator();
}
